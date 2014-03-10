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
package br.usp.each.saeg.bytecode.analysis;

import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.FrameNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.LineNumberNode;

import br.usp.each.saeg.bytecode.analysis.graph.defuse.DefUseFrame;

/**
 * Value object that represents a bytecode instruction.
 * 
 * @author Felipe Albuquerque
 * 
 * modified by Roberto Araujo
 */
public class BytecodeInstruction {

	private final AbstractInsnNode instruction;
	
	private final BytecodeInstructionType type;
	
	public DefUseFrame frame;
	
	/**
	 * Builds a bytecode instruction.
	 * 
	 * @param instruction An ASM AbstractInsnNode object
	 */
	public BytecodeInstruction(final AbstractInsnNode instruction) {
		this.instruction = instruction;
		this.type = getInstructionType();
	}
	
	/**
	 * Gets the instruction.
	 * 
	 * @return the instruction
	 */
	public AbstractInsnNode getInstruction() {
		return instruction;
	}

	/**
	 * Gets the instruction type.
	 * 
	 * @return the instruction type
	 */
	public BytecodeInstructionType getType() {
		return type;
	}
	
	public boolean isPredicate() {
		final boolean predicate = 
				this.getType().equals(BytecodeInstructionType.UNARY_CMP) ||
				this.getType().equals(BytecodeInstructionType.BINARY_CMP) ||
				this.getType().equals(BytecodeInstructionType.SWITCH);
		return predicate;
	}
	
	/**
	 * Gets the bytecode instruction type
	 * 
	 * @return the type of the instruction
	 */
	private BytecodeInstructionType getInstructionType() {
		
		BytecodeInstructionType type = null;
		
		if (instruction instanceof FrameNode) {
			type = BytecodeInstructionType.FRAME;
		} else if (instruction instanceof LineNumberNode) {
			type = BytecodeInstructionType.LINE_NUMBER;
		} else if (instruction instanceof LabelNode) {
			type = BytecodeInstructionType.LABEL;
		} else {
			type = BytecodeInstructionType
					.getFromOpcode(instruction.getOpcode());
		}
		
		return type;
	}
	
	@Override
	public String toString() {
		return type.toString();
	}

}
