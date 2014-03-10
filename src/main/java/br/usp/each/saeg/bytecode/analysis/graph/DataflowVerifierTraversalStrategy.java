/***
 * bytecode-analysis: a prototype-library based on ASM 4 that
 *                    computes definitions/uses of each instruction
 * Copyright (c) 2014 University of Sao Paulo
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 * 3. Neither the name of the copyright holders nor the names of its
 *    contributors may be used to endorse or promote products derived from
 *    this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF
 * THE POSSIBILITY OF SUCH DAMAGE.
 */
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
