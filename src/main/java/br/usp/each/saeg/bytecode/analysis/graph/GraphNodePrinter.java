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
