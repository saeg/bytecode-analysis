package br.usp.each.saeg.bytecode.analysis.graph;

import java.util.Iterator;

import br.usp.each.saeg.bytecode.analysis.BytecodeInstruction;
import br.usp.each.saeg.bytecode.analysis.BytecodeInstructionType;

public class PseudoInstructionsRemover extends GraphNodeVisitor {

	@Override
	public void visit(final GraphNode node) {
		final Iterator<BytecodeInstruction> iterator = node.instructions.iterator();
		
		while (iterator.hasNext()) {
			
			final BytecodeInstruction instruction = iterator.next();
			
			final BytecodeInstructionType type = instruction.getType();
			
			if (type == BytecodeInstructionType.FRAME ||
				type == BytecodeInstructionType.LINE_NUMBER ||
				type == BytecodeInstructionType.LABEL ||
				type == BytecodeInstructionType.NOP) {
				
				if (instruction.frame == null)
					iterator.remove();
			}
			
		}
	}

}
