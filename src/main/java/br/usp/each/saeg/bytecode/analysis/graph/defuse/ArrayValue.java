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
import java.util.List;

public class ArrayValue implements ValueRef {

	public final int opcode;
	public final ValueRef arref;
	public final ValueRef index;

	public ArrayValue(final int opcode, 
					  final ValueRef arref, 
					  final ValueRef index) {
		
		this.opcode = opcode;
		this.arref = arref;
		this.index = index;
	}
	
	@Override
	public List<VariableRef> getVariableRefs() {
		final List<VariableRef> values = new ArrayList<VariableRef>();
		
		int dims = 1;
		ValueRef carref = arref;
		while (carref instanceof ArrayValue) {
			dims++;
			carref = ArrayValue.class.cast(carref).arref;
		}
		
		ValueRef root = carref;
		while (root instanceof ObjectField) {
			root = ObjectField.class.cast(root).objectref;
		}
		
		if (carref instanceof VariableRef &&
				(root instanceof Local || root instanceof StaticField)) {
			
			ArrayComponent component = new ArrayComponent(carref);
			while (--dims > 0) {
				component = new ArrayComponent(component);
			}
			values.add(component);
		}
		
		values.addAll(arref.getVariableRefs());
		values.addAll(index.getVariableRefs());
		return Collections.unmodifiableList(values);
	}
	
	@Override
	public int size() {
		return DoubleWords.instance.contains(opcode) ? 2 : 1;
	}

	@Override
	public String toString() {
		return String.format("%s(%s[%s])", 
				getClass().getSimpleName(), arref, index);
	}
	
}
