package br.usp.each.saeg.bytecode.analysis.graph;

import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

import br.usp.each.saeg.bytecode.analysis.graph.defuse.DefUseFrameComputer;

public class DataflowVerifierTraversalStrategy implements GraphTraveralStrategy {

	private final DefUseFrameComputer computer;
	
	public DataflowVerifierTraversalStrategy(final DefUseFrameComputer computer) {
		this.computer = computer;
	}
	
	@Override
	public void traverse(final GraphNode origin) {
		
		// to control already visited nodes
		final Set<Integer> visitedNodes = new HashSet<Integer>();
		
		// to control 'changed' nodes
		final TreeSet<GraphNode> changed = new TreeSet<GraphNode>(new Comparator<GraphNode>() {
			@Override
			public int compare(GraphNode o1, GraphNode o2) {
				return o1.id - o2.id;
			}
		});
		
		computer.start(origin);
		
		visitedNodes.add(origin.id);
		changed.add(origin);
		
		// Step 1
		// Select a node whose "changed" bit is set. If no node remains 
		// whose "changed" bit is set, the method has successfully been verified. 
		while (!changed.isEmpty()) {
			// Step 1 (cont.)
			// Otherwise, turn off the "changed" bit of the selected node.			
			GraphNode node = changed.first();
			changed.remove(node);
			
			// Step 2
			// Model the effect of the node execution on the operand stack
			computer.visit(node);
			
			// Step 3
			// Determine the nodes that can follow the current node.
			for (final GraphNode child : node.getChildren()) {
				
				// Step 4
				// Merge the state of the operand stack at the end of the execution 
				// of the current node into each of the successor nodes
				
				if (visitedNodes.add(child.id)) {
					// If this is the first time the successor node has been
					// visited, record that the operand stack values calculated in
					// steps 2 and 3 are the state of the operand stack prior to
					// executing the successor node. Set the "changed" bit for the
					// successor node.
					
					// Visit edge propagates the stack state from source node to destination node
					computer.visitEdge(node, child);
					changed.add(child);
				}
				
				else {
					// If the successor node has been seen before, merge the operand stack values
					// calculated in steps 2 and 3 into the values already there. 
					// Set the "changed" bit if there is any modification to the values.
					
					if (computer.visitEdgeMerge(node, child)) {
						changed.add(child);
					}
				}
				
			}
			
			// Do the same thing to exception edges
			for (final GraphNode child : node.getExceptionChildren()) {
				if (visitedNodes.add(child.id)) {
					// In the special case of control transfer through an exception edge, 
					// the operand stack is set to contain a single object of the exception type.
					computer.visitExceptionEdge(node, child);
					changed.add(child);
				} else {
					// Do nothing here
				}
			}
			
			// Continue at step 1...
		}
		
		computer.end(origin);
		
	}

}
