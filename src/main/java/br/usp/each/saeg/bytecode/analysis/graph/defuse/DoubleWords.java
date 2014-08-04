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

import java.util.HashSet;
import java.util.Set;

import org.objectweb.asm.Opcodes;

public final class DoubleWords {

	public static final DoubleWords instance = new DoubleWords();

	private final Set<Object> bag = new HashSet<Object>();

	private DoubleWords() {
		bag.add(Opcodes.LALOAD);
		bag.add(Opcodes.DALOAD);
		bag.add(Opcodes.LASTORE);
		bag.add(Opcodes.DASTORE);
		
		bag.add(Opcodes.LADD);
		bag.add(Opcodes.DADD);
		bag.add(Opcodes.LSUB);
		bag.add(Opcodes.DSUB);
		bag.add(Opcodes.LMUL);
		bag.add(Opcodes.DMUL);
		bag.add(Opcodes.LDIV);
		bag.add(Opcodes.DDIV);
		bag.add(Opcodes.LREM);
		bag.add(Opcodes.DREM);
		bag.add(Opcodes.LSHL);
		bag.add(Opcodes.LSHR);
		bag.add(Opcodes.LUSHR);
		bag.add(Opcodes.LAND);
		bag.add(Opcodes.LOR);
		bag.add(Opcodes.LXOR);

		bag.add(Opcodes.I2L);
		bag.add(Opcodes.F2L);
		bag.add(Opcodes.D2L);
		bag.add(Opcodes.I2D);
		bag.add(Opcodes.L2D);
		bag.add(Opcodes.F2D);

		bag.add(Opcodes.LLOAD);
		bag.add(Opcodes.DLOAD);
		bag.add(Opcodes.LSTORE);
		bag.add(Opcodes.DSTORE);
	}

	public boolean contains(final Object obj) {
		return bag.contains(obj);
	}

}
