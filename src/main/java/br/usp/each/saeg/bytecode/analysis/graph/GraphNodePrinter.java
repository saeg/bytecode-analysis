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

import java.io.PrintStream;
import java.util.Arrays;
import java.util.List;

import br.usp.each.saeg.bytecode.analysis.BytecodeInstruction;

public class GraphNodePrinter extends GraphNodeVisitor {
	
	private PrintStream ps;
	
	public GraphNodePrinter() {
		this(System.out);
	}

	public GraphNodePrinter(PrintStream ps) {
		this.ps = ps;
	}

	@Override
	public void visit(final GraphNode node) {
		
		final String value = String.format("Node: [%d] = (\n" +
				" parents: %s\n" +
				" children: %s\n" +
				" instructions: %s\n)", 
				node.id, 
				new GraphNodeArrayPrinter(node.getParents()),
				new GraphNodeArrayPrinter(node.getChildren()),
				new BytecodeInstructionListPrinter(node.instructions));
		
		ps.println(value);

	}
	
	private static class GraphNodeArrayPrinter {
		
		private final GraphNode[] nodes;

		public GraphNodeArrayPrinter(final GraphNode[] nodes) {
			this.nodes = nodes;
		}
		
		@Override
		public String toString() {
			return Arrays.asList(nodes).toString();
		}
	}
	
	private static class BytecodeInstructionListPrinter {
		
		private final List<BytecodeInstruction> instructions;

		public BytecodeInstructionListPrinter(
				final List<BytecodeInstruction> instructions) {
			
			this.instructions = instructions;
		}
		
		@Override
		public String toString() {
			final StringBuilder builder = new StringBuilder();
			builder.append("{\n");
			for (final BytecodeInstruction instruction : instructions) {
				builder.append("    ").append(instruction);
				
				if (instruction.frame != null) {
					
					builder.append(" \t");
					
					if (instruction.frame.def != null) {
						builder.append("Def:").append(instruction.frame.def);
					}
					
					if (instruction.frame.def != null && 
							!instruction.frame.uses.isEmpty()) {
						builder.append("    ");
					}
					
					if (!instruction.frame.uses.isEmpty()) {
						builder.append("Use:").append(instruction.frame.uses);
					}
				}
				
				builder.append("\n");
				
			}
			builder.append("}");
			return builder.toString();
		}
		
	}

}
