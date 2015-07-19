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

import java.util.Arrays;
import java.util.List;

import br.usp.each.saeg.bytecode.analysis.BytecodeInstruction;
import br.usp.each.saeg.bytecode.analysis.BytecodeInstructionType;

public class BasicBlockGraphNodeJoiner extends GraphNodeVisitor {
	
	private GraphNode root;
	
	public void start(final GraphNode root) {
		this.root = root;
	}
	
	/**
	 * Groups sequential instructions of the given graph in single nodes
	 * 
	 * @param node the graph node to be simplified
	 */
	@Override
	public void visit(final GraphNode node) {
			
		GraphNode[] children = node.getChildren();
		
		while (children.length == 1) {
			
			final GraphNode child = children[0];
			
			if (child == root)
				return;
			
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
