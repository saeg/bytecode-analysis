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

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.objectweb.asm.Opcodes;

/**
 * Types of bytecode instructions.
 * 
 * @author Felipe Albuquerque
 * 
 * Completely modified by Roberto Araujo
 */
public enum BytecodeInstructionType {
	
	/** Pseudo-instruction that refers to a label of an instruction */
	LABEL,
	
	/** Pseudo-instruction that refers to the line of source code referent to an instruction */
	LINE_NUMBER,
	
	/** Pseudo-instruction that refers to a frame map of an instruction */
	FRAME,
	
	NOP(Opcodes.NOP),
	
	CONSTANT (Opcodes.ACONST_NULL,
			  Opcodes.ICONST_M1,
			  Opcodes.ICONST_0,
			  Opcodes.ICONST_1,
			  Opcodes.ICONST_2,
			  Opcodes.ICONST_3,
			  Opcodes.ICONST_4,
			  Opcodes.ICONST_5,
			  Opcodes.LCONST_0,
			  Opcodes.LCONST_1,
			  Opcodes.FCONST_0,
			  Opcodes.FCONST_1,
			  Opcodes.FCONST_2,
			  Opcodes.DCONST_0,
			  Opcodes.DCONST_1,
			  Opcodes.BIPUSH,
			  Opcodes.SIPUSH),
			  
	LDC (Opcodes.LDC),
	
	LOAD (Opcodes.ILOAD,
		  Opcodes.LLOAD,
		  Opcodes.FLOAD,
		  Opcodes.DLOAD,
		  Opcodes.ALOAD),
	
	LOAD_ARRAY (Opcodes.IALOAD,
				Opcodes.LALOAD,
				Opcodes.FALOAD,
				Opcodes.DALOAD,
				Opcodes.AALOAD,
				Opcodes.BALOAD,
				Opcodes.CALOAD,
				Opcodes.SALOAD),
				
	// occurs a definition and might occurs an use
	STORE (Opcodes.ISTORE,
		   Opcodes.LSTORE,
		   Opcodes.FSTORE,
		   Opcodes.DSTORE,
		   Opcodes.ASTORE),
	
	// occurs a definition and might occurs an use
	STORE_ARRAY (Opcodes.IASTORE,
				 Opcodes.LASTORE,
				 Opcodes.FASTORE,
				 Opcodes.DASTORE,
				 Opcodes.AASTORE,
				 Opcodes.BASTORE,
				 Opcodes.CASTORE,
				 Opcodes.SASTORE),
				 
	POP (Opcodes.POP),
	POP2 (Opcodes.POP2),
	DUP (Opcodes.DUP),
	DUP_X1 (Opcodes.DUP_X1),
	DUP_X2 (Opcodes.DUP_X2),
	DUP2 (Opcodes.DUP2),
	DUP2_X1 (Opcodes.DUP2_X1),
	DUP2_X2 (Opcodes.DUP2_X2),
	SWAP (Opcodes.SWAP),
				 
	BINARY_MATH (Opcodes.IADD,
				 Opcodes.LADD,
				 Opcodes.FADD,
				 Opcodes.DADD,
				 Opcodes.ISUB,
				 Opcodes.LSUB,
				 Opcodes.FSUB,
				 Opcodes.DSUB,
				 Opcodes.IMUL,
				 Opcodes.LMUL,
				 Opcodes.FMUL,
				 Opcodes.DMUL,
				 Opcodes.IDIV,
				 Opcodes.LDIV,
				 Opcodes.FDIV,
				 Opcodes.DDIV,
				 Opcodes.IREM,
				 Opcodes.LREM,
				 Opcodes.FREM,
				 Opcodes.DREM,
				 Opcodes.ISHL,
				 Opcodes.LSHL,
				 Opcodes.ISHR,
				 Opcodes.LSHR,
				 Opcodes.IUSHR,
				 Opcodes.LUSHR,
				 Opcodes.IAND,
				 Opcodes.LAND,
				 Opcodes.IOR,
				 Opcodes.LOR,
				 Opcodes.IXOR,
				 Opcodes.LXOR,
				 Opcodes.LCMP,
				 Opcodes.FCMPL,
				 Opcodes.FCMPG,
				 Opcodes.DCMPL,
				 Opcodes.DCMPG),
				 
	UNARY_MATH (Opcodes.INEG,
				Opcodes.LNEG,
				Opcodes.FNEG,
				Opcodes.DNEG,
				Opcodes.INSTANCEOF),
	
	// occurs a definition and an use
	INC (Opcodes.IINC),
	
	CONVERSION (Opcodes.I2L,
				Opcodes.I2F,
				Opcodes.I2D,
				Opcodes.L2I,
				Opcodes.L2F,
				Opcodes.L2D,
				Opcodes.F2I,
				Opcodes.F2L,
				Opcodes.F2D,
				Opcodes.D2I,
				Opcodes.D2L,
				Opcodes.D2F,
				Opcodes.I2B,
				Opcodes.I2C,
				Opcodes.I2S),
				
	// might occurs a p-use	
	BINARY_CMP (Opcodes.IF_ICMPEQ,
				Opcodes.IF_ICMPNE,
				Opcodes.IF_ICMPLT,
				Opcodes.IF_ICMPGE,
				Opcodes.IF_ICMPGT,
				Opcodes.IF_ICMPLE,
				Opcodes.IF_ACMPEQ,
				Opcodes.IF_ACMPNE),
	
	// might occurs a p-use 	
	UNARY_CMP (Opcodes.IFEQ,
			   Opcodes.IFNE,
			   Opcodes.IFLT,
			   Opcodes.IFGE,
			   Opcodes.IFGT,
			   Opcodes.IFLE,
			   Opcodes.IFNULL,
			   Opcodes.IFNONNULL),
			   
	GOTO (Opcodes.GOTO),
	JSR (Opcodes.JSR),
	RET (Opcodes.RET),
	
	// might occurs a p-use 	
	SWITCH (Opcodes.TABLESWITCH,
			Opcodes.LOOKUPSWITCH),
	
	// might occurs an use
	RETURN (Opcodes.IRETURN,
			Opcodes.LRETURN,
			Opcodes.FRETURN,
			Opcodes.DRETURN,
			Opcodes.ARETURN),
			
	RETURN_VOID (Opcodes.RETURN),
	
	GETSTATIC (Opcodes.GETSTATIC),
	
	// occurs a definition and might occurs an use
	PUTSTATIC (Opcodes.PUTSTATIC),
	
	GETFIELD (Opcodes.GETFIELD),
	
	// occurs a definition and might occurs an use
	PUTFIELD (Opcodes.PUTFIELD),
	
	// might occurs an use
	INVOKE (Opcodes.INVOKEVIRTUAL,
			Opcodes.INVOKESPECIAL,
			Opcodes.INVOKESTATIC,
			Opcodes.INVOKEINTERFACE),
			
	INVOKE_DYNAMIC (Opcodes.INVOKEDYNAMIC),
			
	NEW (Opcodes.NEW),
	
	NEWARRAY (Opcodes.NEWARRAY,
			  Opcodes.ANEWARRAY),
			  
	ARRAYLENGTH (Opcodes.ARRAYLENGTH),
	
	ATHROW (Opcodes.ATHROW),
	
	CHECKCAST (Opcodes.CHECKCAST),
	
	MONITOR (Opcodes.MONITORENTER,
			Opcodes.MONITOREXIT),
	
	MULTIANEWARRAY (Opcodes.MULTIANEWARRAY);
	
	/**
	 * The possible opcodes values for the JVM instruction.
	 */
	private final Set<Integer> opcodes;
	
	/**
	 * Builds an instruction type.
	 * 
	 * @param opcodes the possible opcodes values for the instruction
	 */
	private BytecodeInstructionType(final Integer... opcodes) {
		this.opcodes = new HashSet<Integer>(Arrays.asList(opcodes));
	}
	
	/**
	 * Builds an instruction type.
	 */
	private BytecodeInstructionType() {
		this.opcodes = Collections.emptySet();
	}
	
	/**
	 * Gets an instruction given its opcode value.
	 * 
	 * @param opcode the opcode value
	 * @return the instruction found
	 */
	public static BytecodeInstructionType getFromOpcode(final int opcode) {
		
		for (final BytecodeInstructionType type : BytecodeInstructionType.values()) {
			if (type.opcodes.contains(opcode)) {
				return type;
			}
		}
		
		throw new IllegalStateException("Invalid opcode:" + opcode);
	}
	
}
