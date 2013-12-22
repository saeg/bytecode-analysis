package br.usp.each.saeg.bytecode.analysis;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.analysis.AnalyzerException;
import org.objectweb.asm.tree.analysis.BasicValue;
import org.objectweb.asm.tree.analysis.Frame;

import br.usp.each.saeg.bytecode.analysis.domain.Edge;
import br.usp.each.saeg.bytecode.analysis.graph.GraphNode;
import br.usp.each.saeg.bytecode.analysis.graph.ProgramGraph;
import br.usp.each.saeg.bytecode.analysis.graph.defuse.DefUseFrame;
import br.usp.each.saeg.bytecode.analysis.graph.defuse.VariableRef;
import br.usp.each.saeg.bytecode.analysis.graph.defuse.VariablesCollector;

/**
 * Builds graphs using the ASM tool
 * 
 * @author Felipe Albuquerque
 * 
 * modified by Roberto Araujo
 */
public class GraphBuilder {
	
	private String className;
	
	private MethodNode methodNode;
	
	private ProgramGraph programGraph;

	private int flowType;
	
	public static final int NORMAL_FLOW = 1;
	
	public static final int EXCEPTION_FLOW = 2;

	/**
	 * Creates a graph builder based on a class
	 * 
	 * @param className
	 *            the name of the class
	 * @param methodNode
	 *            the method for which the graph must be created
	 */
	public GraphBuilder(final String className, 
						final MethodNode methodNode,
						final int flowType) {
		
		this.className = className;
		this.methodNode = methodNode;
		this.flowType = flowType;
	}

	/**
	 * Builds the graph referent to the method given for the class
	 * 
	 * @return the graph referent to the method given for the class
	 * @throws CouldNotBuildGraphException when the graph cannot be built
	 */
	public ProgramGraph buildDefUseGraph() throws CouldNotBuildGraphException {

		if (programGraph == null) {
			
			final EdgeAnalyzer analyzer = getEdgeAnalyzer();
			
			final Edge[] edges;
			if ((flowType & NORMAL_FLOW) != 0) {
				edges = analyzer.getEdges();
			} else {
				edges = new Edge[0];
			}
			
			final Edge[] exceptionEdges;
			if ((flowType & EXCEPTION_FLOW) != 0) {
				exceptionEdges = analyzer.getExceptionEdges();
			} else {
				exceptionEdges = new Edge[0];
			}
			
			final Frame<BasicValue>[] frames = analyzer.getFrames();
			final AbstractInsnNode[] instructions = methodNode.instructions.toArray();
			
			final Map<Integer, GraphNode> nodes = new HashMap<Integer, GraphNode>();
			
			for (int i = 0; i < frames.length; i++) {
				
				if (frames[i] != null) {
					
					final AbstractInsnNode instruction = instructions[i];
					
					final GraphNode node = new GraphNode(i);
					
					node.instructions.add(new BytecodeInstruction(instruction));
					
					nodes.put(node.id, node);
				}
				
			}

			for (final Edge edge : edges) {
				nodes.get(edge.src).addChild(nodes.get(edge.dest));
			}
			for (final Edge edge : exceptionEdges) {
				nodes.get(edge.src).addExceptionChild(nodes.get(edge.dest));
			}

			if (!nodes.isEmpty()) {
				final List<VariableRef> params = VariablesCollector
						.getParameters(methodNode.desc, methodNode.access);
				
				final List<BytecodeInstruction> paramsInsns = 
						new ArrayList<BytecodeInstruction>(params.size());
				
				for (final VariableRef var : params) {
					
					final BytecodeInstruction insn = 
							new BytecodeInstruction(new InsnNode(Opcodes.NOP));
					
					final List<VariableRef> uses = Collections.emptyList();
					
					insn.frame = new DefUseFrame(var, uses);
					
					paramsInsns.add(insn);
				}
				
				nodes.get(0).instructions.addAll(0, paramsInsns);
				
				programGraph = new ProgramGraph(
						className, methodNode.name, methodNode.desc, nodes.get(0));
			}

			// Releasing references in order to save memory
			className = null;
			methodNode = null;
		}

		return programGraph;
	}
	
	/**
	 * Gets the ASM EdgeAnalyser
	 * 
	 * @return the ASM EdgeAnalyser
	 * @throws CouldNotBuildGraphException
	 *             when is not possible to analyze a method
	 */
	private EdgeAnalyzer getEdgeAnalyzer() 
			throws CouldNotBuildGraphException {
		
		final EdgeAnalyzer analyzer = new EdgeAnalyzer();
		try {
			analyzer.analyze(className, methodNode);
		} catch (final AnalyzerException analyzerException) {
			throw new CouldNotBuildGraphException(
					className, methodNode.signature, analyzerException);
		}

		return analyzer;
	}
	
}
