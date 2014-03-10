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
