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
