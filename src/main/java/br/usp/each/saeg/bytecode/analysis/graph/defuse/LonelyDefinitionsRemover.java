package br.usp.each.saeg.bytecode.analysis.graph.defuse;

import java.util.Set;

import br.usp.each.saeg.bytecode.analysis.BytecodeInstruction;
import br.usp.each.saeg.bytecode.analysis.graph.GraphNode;
import br.usp.each.saeg.bytecode.analysis.graph.GraphNodeVisitor;
import br.usp.each.saeg.bytecode.analysis.graph.PreOrderTraversalStrategy;

public class LonelyDefinitionsRemover extends GraphNodeVisitor {
	
	private Set<VariableRef> uses;

	@Override
	public void start(final GraphNode root) {
		
		final VariablesCollector variables = new VariablesCollector();
		new PreOrderTraversalStrategy(variables).traverse(root);
		
		uses = variables.getUsesVariableRefs();
		
	}
	
	@Override
	public void visit(final GraphNode node) {
		for (final BytecodeInstruction insn : node.instructions) {
			
			if (insn.frame != null && insn.frame.def != null) {
				
				VariableRef def = null;
				
				if (uses.contains(insn.frame.def)) {
					def = insn.frame.def;
				}
				
				if (def == null && insn.frame.uses.isEmpty()) {
					insn.frame = null;
				} else {
					insn.frame = new DefUseFrame(def, insn.frame.uses);
				}
			}
		}
		
	}
	
}
