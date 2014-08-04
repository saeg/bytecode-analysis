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

public class ObjectField extends Field {

	public final Value objectref;
	
	public ObjectField(final String owner, 
					   final String name, 
					   final String desc, 
					   final Value objectref) {
		
		super(owner, name, desc);
		this.objectref = objectref;
	}
	
	@Override
	public List<Variable> getVariableRefs() {
		final List<Variable> values = new ArrayList<Variable>();
		
		values.addAll(objectref.getVariableRefs());
		
		Value root = objectref;
		while (root instanceof ObjectField) {
			root = ObjectField.class.cast(root).objectref;
		}
		
		if (objectref instanceof Variable &&
				(root instanceof Local || root instanceof StaticField)) {
			values.add(this);
		}
		
		return Collections.unmodifiableList(values);
	}
	
	@Override
	public String toString() {
		return String.format("%s.%s.%s", objectref, owner.replace("/", "."), name);
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + objectref.hashCode();
		return result;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj)
			return true;
		
		if (!super.equals(obj))
			return false;
		
		if (getClass() != obj.getClass())
			return false;
		
		final ObjectField other = (ObjectField) obj;
		
		if (!objectref.equals(other.objectref))
			return false;
		
		return true;
	}
	
}
