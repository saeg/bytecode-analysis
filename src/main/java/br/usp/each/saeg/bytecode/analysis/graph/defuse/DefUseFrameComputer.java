package br.usp.each.saeg.bytecode.analysis.graph.defuse;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.IincInsnNode;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MultiANewArrayInsnNode;
import org.objectweb.asm.tree.TypeInsnNode;
import org.objectweb.asm.tree.VarInsnNode;

import br.usp.each.saeg.bytecode.analysis.BytecodeInstruction;
import br.usp.each.saeg.bytecode.analysis.domain.Method;
import br.usp.each.saeg.bytecode.analysis.graph.GraphNode;
import br.usp.each.saeg.bytecode.analysis.graph.GraphNodeVisitor;

public class DefUseFrameComputer extends GraphNodeVisitor {
	
	private Map<Integer, Stack<ValueRef>> stackMapIn;
	
	private Map<Integer, Stack<ValueRef>> stackMapOut;
	
	private HashSet<FieldRef> fields;
	
	@Override
	public void start(final GraphNode root) {
		/** start with stack always clear. */
		stackMapIn = new HashMap<Integer, Stack<ValueRef>>();
		stackMapOut = new HashMap<Integer, Stack<ValueRef>>();
		fields = new HashSet<FieldRef>();
		stackMapIn.put(root.id, new Stack<ValueRef>());
	}
	
	@Override
	public void visit(final GraphNode node) {
		
		final Stack<ValueRef> stack = new Stack<ValueRef>();
		stack.addAll(stackMapIn.get(node.id));
		
		for (int idx = 0; idx < node.instructions.size(); idx++) { 
			
			final BytecodeInstruction instruction = node.instructions.get(idx);
			
			DefUseFrame frame = null;
			
			final int opcode = instruction.getInstruction().getOpcode();
			
			switch (instruction.getType()) {
			
			case LABEL:
			case LINE_NUMBER:
			case FRAME:
			case NOP:
				/** ignore */
				break;
				
			/** pushes some constant value to the stack */
			case CONSTANT:
				stack.push(new Constant(opcode));
				break;
				
			/** pushes some constant value to the stack */
			case LDC: {
				final LdcInsnNode ldc = (LdcInsnNode) instruction.getInstruction();
				stack.push(new Constant(ldc.cst));
				break;
			}
				
			/** pushes some local variable to the stack */
			case LOAD: {
				final VarInsnNode v = (VarInsnNode) instruction.getInstruction();
				stack.push(new Local(opcode, v.var));
				break;
			}
			
			/** pops two operands (..., array reference, index) and 
			  * pushes the value in the component of the array at index position */
			case LOAD_ARRAY: {
				final ValueRef index = stack.pop();
				final ValueRef arref = stack.pop();
				stack.push(new ArrayValue(opcode, arref, index));	
				break;
			}
			
			/** pops one operand (..., value) and store to a local variable.
			  * this is a definition of that variable */
			case STORE: {
				final VarInsnNode v = (VarInsnNode) instruction.getInstruction();
				final ValueRef value = stack.pop();
				
				/* new frame to indicate definition of local variable 
				 * and use of the value in top of the stack */
				final Local definition = new Local(opcode, v.var);
				frame = new DefUseFrame(definition, value);
				
				if (value instanceof ArrayRef) {
					
					final BytecodeInstruction arraydef = 
							new BytecodeInstruction(new InsnNode(Opcodes.NOP));
					
					final List<VariableRef> uses = Collections.emptyList();
					arraydef.frame = new DefUseFrame(new ArrayComponent(definition), uses);
					
					node.instructions.add(idx + 1, arraydef);
				}
				
				break;
			}
			
			/** pops three operands (..., array reference, index, value) and
			  * store value as the component of the array at index position.
			  * this is a definition of component of the array */
			case STORE_ARRAY: {
				final ValueRef value = stack.pop();
				final ValueRef index = stack.pop();
				final ValueRef arref = stack.pop();
				
				final List<VariableRef> uses = new ArrayList<VariableRef>();
				uses.addAll(value.getVariableRefs());
				uses.addAll(index.getVariableRefs());
				uses.addAll(arref.getVariableRefs());
				
				int dims = 1;
				ValueRef carref = arref;
				while (carref instanceof ArrayValue) {
					dims++;
					carref = ArrayValue.class.cast(carref).arref;
				}
				
				ValueRef root = carref;
				while (root instanceof ObjectField) {
					root = ObjectField.class.cast(root).objectref;
				}
				
				ArrayComponent component = null;
				if (carref instanceof VariableRef &&
						(root instanceof Local || root instanceof StaticField)) {
					component = new ArrayComponent(carref);
					while (--dims > 0) {
						component = new ArrayComponent(component);
					}
				}
				
				/* new frame to indicate definition of component of the array 
				 * and use of the value in top of the stack */
				frame = new DefUseFrame(component, uses);
				break;
			}
			
			// ------- Instructions that manipulate the stack
			
			case POP: {
				final ValueRef value = stack.pop();
				
				if (value instanceof Invoke) {
					// dropping a method result
					frame = new DefUseFrame(null, value);
				}
				
				break;
			}
				
			case POP2: {
				final List<VariableRef> uses = new ArrayList<VariableRef>();
				
				ValueRef value;
				
				if (stack.peek().size() == 1) {
					value = stack.pop();
					if (value instanceof Invoke) {
						// dropping a method result
						uses.addAll(value.getVariableRefs());
					}
				}
				value = stack.pop();
				if (value instanceof Invoke) {
					// dropping a method result
					uses.addAll(value.getVariableRefs());
				}
				
				if (!uses.isEmpty()) {
					frame = new DefUseFrame(null, uses);
				}
				
				break;
			}
			
			case DUP:
				stack.push(stack.peek());
				break;
				
			case DUP_X1: {
				final ValueRef value1 = stack.pop();
				final ValueRef value2 = stack.pop();
				stack.push(value1);
				stack.push(value2);
				stack.push(value1);
				break;
			}
			
			case DUP_X2: {
				final ValueRef value1 = stack.pop(); // Always category 1
				final ValueRef value2 = stack.pop();
				if (value2.size() == 1) {
					final ValueRef value3 = stack.pop();
					stack.push(value1);
					stack.push(value3);
				} else {
					stack.push(value1);					
				}
				stack.push(value2);
				stack.push(value1);
				break;
			}
			
			case DUP2: {
				final ValueRef value1 = stack.pop();
				if (value1.size() == 1) {
					final ValueRef value2 = stack.peek();
					stack.push(value1);
					stack.push(value2);
					stack.push(value1);
				} else {
					stack.push(value1);
					stack.push(value1);
				}
				break;
			}
			
			case DUP2_X1: {
				final ValueRef value1 = stack.pop();
				if (value1.size() == 1) {
					final ValueRef value2 = stack.pop();
					final ValueRef value3 = stack.pop();
					stack.push(value2);
					stack.push(value1);
					stack.push(value3);
					stack.push(value2);
					stack.push(value1);
				} else {
					final ValueRef value2 = stack.pop();
					stack.push(value1);
					stack.push(value2);
					stack.push(value1);
				}
				break;
			}
			
			case DUP2_X2: {
				final ValueRef value1 = stack.pop();
				final ValueRef value2 = stack.pop();
				if (value1.size() == 1) {
					// In this case: value1 is category 1 
					// and value2 is mandatorily category 1
					final ValueRef value3 = stack.pop();
					if (value3.size() == 1) {
						// In this case: value3 is category 1 
						// and value4 is mandatorily category 1
						final ValueRef value4 = stack.pop();
						
						// Form 1: Value 1,2,3 and 4 are category 1
						stack.push(value2);
						stack.push(value1);
						stack.push(value4);
						stack.push(value3);
						stack.push(value2);
						stack.push(value1);
					} else {
						// In this case: value3 is category 2 
						
						// Form 3: value1 and value2 are both values of a category 1
						// and value3 is a value of a category 2 
						stack.push(value2);
						stack.push(value1);
						stack.push(value3);
						stack.push(value2);
						stack.push(value1);
					}
				} else {
					// In this case: value1 is category 2 
					if (value2.size() == 1) {
						// In this case: value2 is category 1 
						// and value3 is mandatorily category 1
						final ValueRef value3 = stack.pop();
						
						// Form 2: value1 is a value of a category 2
						// and value2 and value3 are both values of a category 1
						stack.push(value1);
						stack.push(value3);
						stack.push(value2);
						stack.push(value1);
					} else {
						// From 4: value1 and value2 are both values of a category 2
						stack.push(value1);
						stack.push(value2);
						stack.push(value1);
					}
				}
				break;
			}
			
			case SWAP: {
				final ValueRef value1 = stack.pop();
				final ValueRef value2 = stack.pop();
				stack.push(value1);
				stack.push(value2);
				break;
			}
			
			// -------
			
			/** pops two operands (..., value1, value2) and 
			  * pushes the result value1 operator value2 */
			case BINARY_MATH: {
				final ValueRef value2 = stack.pop();
				final ValueRef value1 = stack.pop();
				stack.push(new Binary(opcode, value1, value2));
				break;
			}
			
			/** basically do nothing here */
			case UNARY_MATH:
				break;
				
			/** Increment local variable by constant. 
			  * Occurs a definition and a use of that variable */
			case INC: {
				final IincInsnNode iinc = (IincInsnNode) instruction.getInstruction();
				final Local local = new Local(opcode, iinc.var);
				
				/* new frame to indicate definition and 
				 * use of the incremented variable */
				frame = new DefUseFrame(local, local);
				break;
			}
			
			/** Just wrap the top of the stack in a conversion mode */
			case CONVERSION:
				stack.push(new Conversion(opcode, stack.pop()));
				break;
				
			/** pops two operands (..., value1, value2) and 
			  * branch if comparison succeeds. 
			  * this is a p-use of popped operands */
			case BINARY_CMP: {
				final ValueRef value2 = stack.pop();
				final ValueRef value1 = stack.pop();
				
				/* new frame to indicate p-use of popped values */
				frame = new DefUseFrame(null, new Binary(opcode, value1, value2));
				break;
			}
			
			/** pops one operand (..., value) and 
			  * branch if comparison succeeds.
			  * this is a p-use of popped operand */
			case UNARY_CMP: {
				final ValueRef value = stack.pop();
				
				/* new frame to indicate p-use of popped value */
				frame = new DefUseFrame(null, value);
				break;
			}
			
			/** basically do nothing here */
			case GOTO:
				break;
				
			case JSR:
			case RET:
				throw new UnsupportedOperationException(
						"Do not support instruction types " +
						"JSR or RET. (Deprecated in Java 6)");
				
			case SWITCH: {
				final ValueRef value = stack.pop();
				
				/* new frame to indicate p-use of popped value */
				frame = new DefUseFrame(null, value);
				break;
			}
				
			/** pops one operand (..., value) and returns to caller 
			  * Occurs a use of the popped operand */
			case RETURN: {
				final ValueRef value = stack.pop();
				
				/* new frame to indicate use of popped value */
				frame = new DefUseFrame(null, value);
				break;
			}
			
			/** basically do nothing here */
			case RETURN_VOID:
				break;
				
			/** pushes some static field variable to the stack */
			case GETSTATIC: {
				final FieldInsnNode f = (FieldInsnNode) instruction.getInstruction();
				final StaticField field = new StaticField(f.owner, f.name, f.desc);
				stack.push(field);
				fields.add(field);
				break;
			}
			
			/** pops one operand (..., value) and store to a static field variable.
			  * this is a definition of that variable */
			case PUTSTATIC: {
				final FieldInsnNode f = (FieldInsnNode) instruction.getInstruction();
				final ValueRef value = stack.pop();
				
				/* new frame to indicate definition of static variable 
				 * and use of the value in top of the stack */
				final StaticField definition = new StaticField(f.owner, f.name, f.desc);
				frame = new DefUseFrame(definition, value);
				
				if (value instanceof ArrayRef) {
					
					final BytecodeInstruction arraydef = 
							new BytecodeInstruction(new InsnNode(Opcodes.NOP));
					
					final List<VariableRef> uses = Collections.emptyList();
					arraydef.frame = new DefUseFrame(new ArrayComponent(definition), uses);
					
					node.instructions.add(idx + 1, arraydef);
				}
				
				break;
			}
			
			/** pops one operand (..., object reference) and 
			  * pushes a object field variable to the stack */
			case GETFIELD: {
				final FieldInsnNode f = (FieldInsnNode) instruction.getInstruction();
				final ValueRef objectref = stack.pop();
				final ObjectField field = new ObjectField(f.owner, f.name, f.desc, objectref);
				stack.push(field);
				
				ValueRef root = objectref;
				while (root instanceof ObjectField) {
					root = ObjectField.class.cast(root).objectref;
				}
				
				if (root instanceof Local || root instanceof StaticField) { 
					fields.add(field);
				}
				break;
			}
			
			/** pops two operands (..., object reference, value) and 
			  * store value to a object field variable.
			  * this is a definition of that variable */
			case PUTFIELD: {
				final FieldInsnNode f = (FieldInsnNode) instruction.getInstruction();
				final ValueRef value = stack.pop();
				final ValueRef objectref = stack.pop();
				
				ValueRef root = objectref;
				while (root instanceof ObjectField) {
					root = ObjectField.class.cast(root).objectref;
				}
				
				/* new frame to indicate definition of object field variable 
				 * and use of the value in top of the stack */
				final ObjectField definition = new ObjectField(f.owner, f.name, f.desc, objectref);
				
				final List<VariableRef> uses = new ArrayList<VariableRef>();
				uses.addAll(value.getVariableRefs());
				uses.addAll(objectref.getVariableRefs());
				
				frame = new DefUseFrame(root instanceof VariableRef ? definition : null, uses);
				
				if (value instanceof ArrayRef) {
					
					final BytecodeInstruction arraydef = 
							new BytecodeInstruction(new InsnNode(Opcodes.NOP));
					
					final List<VariableRef> array_uses = Collections.emptyList();
					arraydef.frame = new DefUseFrame(new ArrayComponent(definition), array_uses);
					
					node.instructions.add(idx + 1, arraydef);
				}
				
				break;
			}
			
			case INVOKE: {
				final MethodInsnNode m = (MethodInsnNode) instruction.getInstruction();
				
				final int nargs = Type.getArgumentTypes(m.desc).length;
				
				final ValueRef[] args = pop(stack, opcode == Opcodes.INVOKESTATIC ? nargs : nargs + 1);
				
				final Method method = new Method(m.owner, m.name, m.desc);
				final Invoke invoke = new Invoke(method, args);
				
				if (Type.getReturnType(m.desc) == Type.VOID_TYPE) {
					frame = new DefUseFrame(null, invoke);
				} else {
					stack.push(invoke);
				}
				
				break;
			}
			
			case INVOKE_DYNAMIC:
				throw new UnsupportedOperationException("Not yet implemented.");
		
			/** Create new object */
			case NEW: {
				final TypeInsnNode type = (TypeInsnNode) instruction.getInstruction();
				stack.push(new ObjectRef(type.desc));
				break;
			}
			
			/** Create new array. pops one operand (..., count) and 
			  * pushes a array reference to the stack  */
			case NEWARRAY: {
				final ValueRef count = stack.pop();
				stack.push(new ArrayRef(count));
				break;
			}
			
			/** Get length of array. pops one operand (..., array reference) and 
			  * pushes a array length to the stack  */
			case ARRAYLENGTH: {
				final ValueRef array = stack.pop();
				stack.push(new ArrayLength(array));
				break;
			}
			
			case ATHROW: {
				final ValueRef value = stack.pop();
				
				/* new frame to indicate use of popped value */
				frame = new DefUseFrame(null, value);
				break;
			}	
				
			/** basically do nothing here */
			case CHECKCAST:
				break;
				
			case MONITOR:
				final ValueRef value = stack.pop();
				
				/* new frame to indicate use of popped value */
				frame = new DefUseFrame(null, value);
				break;
				
			case MULTIANEWARRAY: {
				final MultiANewArrayInsnNode arr = (MultiANewArrayInsnNode) instruction.getInstruction();
				final ValueRef[] counts = new ValueRef[arr.dims];
				for (int i = 0; i < arr.dims; i++) {
					counts[i] = stack.pop();
				}
				stack.push(new ArrayRef(counts));
				break;
			}
			
			}
			
			if (frame != null)
				instruction.frame = frame;
		}
		
		stackMapOut.put(node.id, stack);
	}

	@Override
	public void visitEdge(final GraphNode src, final GraphNode dest) {
		final Stack<ValueRef> newStack = new Stack<ValueRef>();
		newStack.addAll(stackMapOut.get(src.id));
		stackMapIn.put(dest.id, newStack);
	}
	
	@Override
	public void visitExceptionEdge(final GraphNode src, final GraphNode dest) {
		final Stack<ValueRef> newStack = new Stack<ValueRef>();
		newStack.push(new ObjectRef("Exception"));
		stackMapIn.put(dest.id, newStack);
	}
	
	public boolean visitEdgeMerge(final GraphNode src, final GraphNode dest) {
		
		final Stack<ValueRef> newStack = new Stack<ValueRef>();
		
		// Should have the same size and the same kind of elements
		Stack<ValueRef> oldIn = stackMapIn.get(dest.id);
		Stack<ValueRef> newIn = stackMapOut.get(src.id);
		
		boolean changed = false;
			
		for (int i = 0; i < oldIn.size(); i++) {
			
			ValueRef oldRef = oldIn.get(i);
			ValueRef newRef = newIn.get(i);
			
			if (!oldRef.getVariableRefs().containsAll(newRef.getVariableRefs()) ||
					!newRef.getVariableRefs().containsAll(oldRef.getVariableRefs())) {
				changed = true;
				newStack.add(new Or(oldRef, newRef));
			} else {
				newStack.add(oldRef);
			}
			
		}
		
		if (changed)
			stackMapIn.put(dest.id, newStack);
		
		return changed;
	}
	
	@Override
	public void end(final GraphNode root) {
		
		final List<BytecodeInstruction> fieldsInsns = 
				new ArrayList<BytecodeInstruction>(fields.size());
		
		for (final FieldRef var : fields) {
			
			final BytecodeInstruction insn =
					new BytecodeInstruction(new InsnNode(Opcodes.NOP));
			
			final List<VariableRef> uses = Collections.emptyList();
			
			insn.frame = new DefUseFrame(var, uses);
			
			fieldsInsns.add(insn);
			
			if (Type.getType(var.desc).getSort() == Type.ARRAY) {
				
				int dims = Type.getType(var.desc).getDimensions();
				
				BytecodeInstruction arraydef = 
						new BytecodeInstruction(new InsnNode(Opcodes.NOP));
				
				ArrayComponent component = new ArrayComponent(var);
				final List<VariableRef> array_uses = Collections.emptyList();
				arraydef.frame = new DefUseFrame(component, array_uses);
				
				fieldsInsns.add(arraydef);
				
				while (--dims > 0) {
					component = new ArrayComponent(component);
					arraydef = new BytecodeInstruction(new InsnNode(Opcodes.NOP));
					arraydef.frame = new DefUseFrame(component, array_uses);
					fieldsInsns.add(arraydef);
				}
				
			}
		}
		
		root.instructions.addAll(0, fieldsInsns);
		
		stackMapIn = null;
		stackMapOut = null;
		fields = null;
	}
	
	private ValueRef[] pop(Stack<ValueRef> stack, final int nargs) {
		final ValueRef[] args = new ValueRef[nargs];
		for (int i = 0; i < nargs; i++) {
			args[i] = stack.pop();
		}
		return args;
	}

}
