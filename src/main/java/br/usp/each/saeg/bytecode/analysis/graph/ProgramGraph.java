package br.usp.each.saeg.bytecode.analysis.graph;

import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

import br.usp.each.saeg.bytecode.analysis.CouldNotBuildGraphException;
import br.usp.each.saeg.bytecode.analysis.GraphBuilder;
import br.usp.each.saeg.bytecode.analysis.domain.Method;
import br.usp.each.saeg.bytecode.analysis.graph.defuse.DefUseFrameComputer;
import br.usp.each.saeg.bytecode.analysis.graph.defuse.LocalUseRemover;
import br.usp.each.saeg.bytecode.analysis.graph.defuse.LonelyDefinitionsRemover;

/**
 * A Control Flow Graph (CFG) of a method
 * 
 * @author Felipe Albuquerque
 * 
 * modified by Roberto Araujo
 */
public class ProgramGraph extends Graph {

	private final Method method;
	
	/**
	 * Builds a CFG
	 * 
	 * @param method the method that generated the CFG
	 * @param rootNode the root node of the CFG
	 */
	public ProgramGraph(final Method method, final GraphNode rootNode) {
		super(rootNode);
		this.method = method;
	}
	
	/**
	 * Builds a CFG
	 * 
	 * @param owner the name of the class that has the CFG
	 * @param name the name of the method that has the CFG
	 * @param desc the descriptor of the method that has the CFG
	 * @param rootNode the root node of the CFG
	 */
	public ProgramGraph(
			final String owner, 
			final String name, 
			final String desc, 
			final GraphNode rootNode) {
		
		this(new Method(owner, name, desc), rootNode);
	}

	/**
	 * Gets the method that contains the CFG
	 * 
	 * @return the method that contains the CFG
	 */
	public Method getMethod() {
		return method;
	}

	@Override
	public String toString() {
		return String.format("Program Graph for [%s]", method);
	}
	
	public static ProgramGraph build(ClassNode cn, MethodNode mn, int type) throws CouldNotBuildGraphException {
		ProgramGraph g = new GraphBuilder(cn.name, mn, type).buildDefUseGraph();
		if (g == null)
			return null;
		new PreOrderTraversalStrategy(new BasicBlockGraphNodeJoiner()).traverse(g.getRootNode());
		new DataflowVerifierTraversalStrategy(new DefUseFrameComputer()).traverse(g.getRootNode());
		new PreOrderTraversalStrategy(new LocalUseRemover()).traverse(g.getRootNode());
		new PreOrderTraversalStrategy(new LonelyDefinitionsRemover()).traverse(g.getRootNode());
		return g;
	}

}
