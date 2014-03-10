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
import br.usp.each.saeg.bytecode.analysis.graph.BasicBlockGraphNodeJoiner;
import br.usp.each.saeg.bytecode.analysis.graph.DataflowVerifierTraversalStrategy;
import br.usp.each.saeg.bytecode.analysis.graph.GraphNode;
import br.usp.each.saeg.bytecode.analysis.graph.PreOrderTraversalStrategy;
import br.usp.each.saeg.bytecode.analysis.graph.ProgramGraph;
import br.usp.each.saeg.bytecode.analysis.graph.defuse.DefUseFrame;
import br.usp.each.saeg.bytecode.analysis.graph.defuse.DefUseFrameComputer;
import br.usp.each.saeg.bytecode.analysis.graph.defuse.LocalUseRemover;
import br.usp.each.saeg.bytecode.analysis.graph.defuse.LonelyDefinitionsRemover;
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
	
	private ProgramGraph graph;

	private int type;
	
	public static final int NORMAL_FLOW = 1 << 0;
	
	public static final int EXCEPTION_FLOW = 1 << 1;
	
	public static final int BASIC_BLOCK = 1 << 2;
	
	public static final int COMPUTE_DEF_USE	= 1 << 3;
	
	public static final int REMOVE_LOCAL_USES = 1 << 4;
	
	public static final int REMOVE_UNUSED_DEFS = 1 << 5;
	
	public static final int DEFAULT = NORMAL_FLOW |
									  BASIC_BLOCK | 
									  COMPUTE_DEF_USE | 
									  REMOVE_LOCAL_USES | 
									  REMOVE_UNUSED_DEFS;

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
						final int type) {
		
		this.className = className;
		this.methodNode = methodNode;
		this.type = type;
	}
	
	public GraphBuilder(final String className, final MethodNode methodNode) {
		this(className, methodNode, DEFAULT);
	}

	/**
	 * Builds the graph referent to the method given for the class
	 * 
	 * @return the graph referent to the method given for the class
	 * @throws CouldNotBuildGraphException when the graph cannot be built
	 */
	public ProgramGraph buildDefUseGraph() throws CouldNotBuildGraphException {

		if (graph == null) {
			
			final EdgeAnalyzer analyzer = getEdgeAnalyzer((type & EXCEPTION_FLOW) != 0);
			
			final Edge[] edges;
			if ((type & NORMAL_FLOW) != 0) {
				edges = analyzer.getEdges();
			} else {
				edges = new Edge[0];
			}
			
			final Edge[] exceptionEdges;
			if ((type & EXCEPTION_FLOW) != 0) {
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
				
				graph = new ProgramGraph(
						className, methodNode.name, methodNode.desc, nodes.get(0));
				
				if ((type & BASIC_BLOCK) != 0) {
					new PreOrderTraversalStrategy(new BasicBlockGraphNodeJoiner()).traverse(graph.getRootNode());
				}
				if ((type & COMPUTE_DEF_USE) != 0) {
					new DataflowVerifierTraversalStrategy(new DefUseFrameComputer()).traverse(graph.getRootNode());
				}
				if ((type & REMOVE_LOCAL_USES) != 0) {
					new PreOrderTraversalStrategy(new LocalUseRemover()).traverse(graph.getRootNode());
				}
				if ((type & REMOVE_UNUSED_DEFS) != 0) {
					new PreOrderTraversalStrategy(new LonelyDefinitionsRemover()).traverse(graph.getRootNode());
				}
				
			}

			// Releasing references in order to save memory
			className = null;
			methodNode = null;
		}

		return graph;
	}
	
	/**
	 * Gets the ASM EdgeAnalyser
	 * 
	 * @return the ASM EdgeAnalyser
	 * @throws CouldNotBuildGraphException
	 *             when is not possible to analyze a method
	 */
	private EdgeAnalyzer getEdgeAnalyzer(final boolean exceptionFlow) 
			throws CouldNotBuildGraphException {
		
		final EdgeAnalyzer analyzer = new EdgeAnalyzer(exceptionFlow);
		try {
			analyzer.analyze(className, methodNode);
		} catch (final AnalyzerException analyzerException) {
			throw new CouldNotBuildGraphException(
					className, methodNode.signature, analyzerException);
		}

		return analyzer;
	}
	
}
