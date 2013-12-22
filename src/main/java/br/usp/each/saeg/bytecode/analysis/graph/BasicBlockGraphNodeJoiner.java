package br.usp.each.saeg.bytecode.analysis.graph;

import java.util.Arrays;
import java.util.List;

import br.usp.each.saeg.bytecode.analysis.BytecodeInstruction;
import br.usp.each.saeg.bytecode.analysis.BytecodeInstructionType;

public class BasicBlockGraphNodeJoiner extends GraphNodeVisitor {
	
	/**
	 * Groups sequential instructions of the given graph in single nodes
	 * 
	 * @param graph the graph to be simplified
	 */
	@Override
	public void visit(final GraphNode node) {
			
		GraphNode[] children = node.getChildren();
		
		while (children.length == 1) {
			
			final GraphNode child = children[0];
			
			if (child.getParents().length == 1) {
				
				List<GraphNode> nodeException = Arrays.asList(node.getExceptionChildren());
				List<GraphNode> childException = Arrays.asList(child.getExceptionChildren());
				
				if (!nodeException.containsAll(childException)
						|| !childException.containsAll(nodeException)) {
					return;
				}
				
				BytecodeInstruction instruction = node.instructions.get(node.instructions.size() - 1);
				if (instruction.getType() == BytecodeInstructionType.GOTO) {
					return;
				}
				
				child.removeExceptionChildren(childException);
				
				children = child.getChildren();
				
				node.removeChild(child);
				node.addChildren(children);
				child.removeChildren(children);					
				
				node.instructions.addAll(child.instructions);
			} 
			
			else {
				return; // return otherwise.
			}
			
		}
		
	}

}
