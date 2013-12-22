package br.usp.each.saeg.bytecode.analysis.graph.defuse;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import br.usp.each.saeg.bytecode.analysis.BytecodeInstruction;
import br.usp.each.saeg.bytecode.analysis.graph.GraphNode;
import br.usp.each.saeg.bytecode.analysis.graph.GraphNodeVisitor;

public class LocalUseRemover extends GraphNodeVisitor {

	@Override
	public void visit(final GraphNode node) {
		
		final Set<VariableRef> defs = new HashSet<VariableRef>();
		
		for (final BytecodeInstruction instruction : node.instructions) {
			
			final boolean predicate = instruction.isPredicate();
			
			if (instruction.frame != null) {
				
				final List<VariableRef> uses = new ArrayList<VariableRef>();
				
				for (final VariableRef use : instruction.frame.uses) {
					if (!defs.contains(use) || predicate) {
						uses.add(use);
					}
				}
				
				if (instruction.frame.def != null) {
					defs.add(instruction.frame.def);
				}
				
				instruction.frame = new DefUseFrame(instruction.frame.def, uses);
				
			}
			
		}
		
	}

}
