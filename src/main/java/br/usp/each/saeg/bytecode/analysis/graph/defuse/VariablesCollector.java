package br.usp.each.saeg.bytecode.analysis.graph.defuse;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import br.usp.each.saeg.bytecode.analysis.BytecodeInstruction;
import br.usp.each.saeg.bytecode.analysis.graph.GraphNode;
import br.usp.each.saeg.bytecode.analysis.graph.GraphNodeVisitor;

public class VariablesCollector extends GraphNodeVisitor {

	private Set<VariableRef> defs;
	
	private Set<VariableRef> uses;
	
	@Override
	public void start(final GraphNode root) {
		defs = new LinkedHashSet<VariableRef>();
		uses = new LinkedHashSet<VariableRef>();
	}
	
	@Override
	public void visit(final GraphNode node) {
		for (final BytecodeInstruction insn : node.instructions) {
			
			if (insn.frame != null) {
				uses.addAll(insn.frame.uses);
				if (insn.frame.def != null)
					defs.add(insn.frame.def);
			}
			
			
		}
	}
	
	public List<VariableRef> getVariableRefs() {
		Set<VariableRef> temp = new HashSet<VariableRef>();
		temp.addAll(defs);
		temp.addAll(uses);
		
		List<VariableRef> vars = new ArrayList<VariableRef>(temp);
		Collections.sort(vars, new Comparator<VariableRef>() {
			@Override
			public int compare(VariableRef o1, VariableRef o2) {
				return o1.toString().compareTo(o2.toString());
			}
		});
		return vars;
	}

	public Set<VariableRef> getDefsVariableRefs() {
		return Collections.unmodifiableSet(defs);
	}

	public Set<VariableRef> getUsesVariableRefs() {
		return Collections.unmodifiableSet(uses);
	}
	
	public static List<VariableRef> getParameters(
			final String desc, final int acess) {
		
		final List<VariableRef> params = new ArrayList<VariableRef>();
		
		int index = 0;
		
		if ((acess & Opcodes.ACC_STATIC) == 0) {
			params.add(new Local(-1, index++)); // this
		}
		
		for (final Type type : Type.getArgumentTypes(desc)) {
			
			final Local local = new Local(-1, index++);
			params.add(local);
			
			if (type.getSort() == Type.ARRAY) {
				
				int dims = type.getDimensions();
				
				ArrayComponent component = new ArrayComponent(local);
				params.add(component);
				while (--dims > 0) {
					component = new ArrayComponent(component);
					params.add(component);
				}

			}
			
			if (type == Type.DOUBLE_TYPE || 
					type == Type.LONG_TYPE) {
				index++;
			}
		}
		
		return params;
	}
	
}
