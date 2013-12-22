package br.usp.each.saeg.bytecode.analysis.graph;

import java.util.HashSet;
import java.util.Set;
import java.util.Stack;

public class PreOrderTraversalStrategy implements GraphTraveralStrategy {

	private final GraphNodeVisitor visitor;
	
	public PreOrderTraversalStrategy(final GraphNodeVisitor visitor) {
		this.visitor = visitor;
	}
	
	@Override
	public void traverse(final GraphNode origin) {
		
		// to control already visited nodes
		final Set<Integer> visitedNodes = new HashSet<Integer>();
		
		// to avoid recursive calls
		final Stack<GraphNode> stack = new Stack<GraphNode>();
		
		visitor.start(origin);
		
		stack.push(origin);
		
		while (!stack.isEmpty()) {
			
			final GraphNode top = stack.peek();
			
			if (visitedNodes.add(top.id)) {
				
				visitor.visit(top);
				
				for (final GraphNode child : top.getChildren()) {
					visitor.visitEdge(top, child);
					stack.push(child);
				}
				for (final GraphNode child : top.getExceptionChildren()) {
					visitor.visitExceptionEdge(top, child);
					stack.push(child);
				}
				
			} else {
				stack.pop();
			}
			
		}
		
		visitor.end(origin);
		
	}

}
