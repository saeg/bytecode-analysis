package br.usp.each.saeg.bytecode.analysis.graph;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a generic graph
 */
public class Graph {

	private final GraphNode rootNode;
	
	/**
	 * Builds a generic graph
	 * 
	 * @param rootNode the root node of the graph
	 */
	public Graph(final GraphNode rootNode) {
		this.rootNode = rootNode;
	}
	
	/**
	 * Gets the root node of the graph
	 * 
	 * @return the root node of the graph
	 */
	public GraphNode getRootNode() {
		return rootNode;
	}
	
	public List<GraphNode> getNodes() {
		
		final List<GraphNode> nodes = new ArrayList<GraphNode>();
		
		final GraphNodeVisitor visitor = new GraphNodeVisitor() {
			@Override
			public void visit(final GraphNode node) {
				nodes.add(node);
			}
		};
		
		new PreOrderTraversalStrategy(visitor).traverse(rootNode);
		
		return nodes;
	}
	
}
