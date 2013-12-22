package br.usp.each.saeg.bytecode.analysis;

import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.FrameNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.LineNumberNode;

import br.usp.each.saeg.bytecode.analysis.graph.defuse.DefUseFrame;

/**
 * Value object that represents a bytecode instruction.
 * 
 * @author Felipe Albuquerque
 * 
 * modified by Roberto Araujo
 */
public class BytecodeInstruction {

	private final AbstractInsnNode instruction;
	
	private final BytecodeInstructionType type;
	
	public DefUseFrame frame;
	
	/**
	 * Builds a bytecode instruction.
	 * 
	 * @param instruction An ASM AbstractInsnNode object
	 */
	public BytecodeInstruction(final AbstractInsnNode instruction) {
		this.instruction = instruction;
		this.type = getInstructionType();
	}
	
	/**
	 * Gets the instruction.
	 * 
	 * @return the instruction
	 */
	public AbstractInsnNode getInstruction() {
		return instruction;
	}

	/**
	 * Gets the instruction type.
	 * 
	 * @return the instruction type
	 */
	public BytecodeInstructionType getType() {
		return type;
	}
	
	public boolean isPredicate() {
		final boolean predicate = 
				this.getType().equals(BytecodeInstructionType.UNARY_CMP) ||
				this.getType().equals(BytecodeInstructionType.BINARY_CMP) ||
				this.getType().equals(BytecodeInstructionType.TABLESWITCH) ||
				this.getType().equals(BytecodeInstructionType.LOOKUPSWITCH);
		return predicate;
	}
	
	/**
	 * Gets the bytecode instruction type
	 * 
	 * @return the type of the instruction
	 */
	private BytecodeInstructionType getInstructionType() {
		
		BytecodeInstructionType type = null;
		
		if (instruction instanceof FrameNode) {
			type = BytecodeInstructionType.FRAME;
		} else if (instruction instanceof LineNumberNode) {
			type = BytecodeInstructionType.LINE_NUMBER;
		} else if (instruction instanceof LabelNode) {
			type = BytecodeInstructionType.LABEL;
		} else {
			type = BytecodeInstructionType
					.getFromOpcode(instruction.getOpcode());
		}
		
		return type;
	}
	
	@Override
	public String toString() {
		return type.toString();
	}

}
