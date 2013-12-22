package br.usp.each.saeg.bytecode.analysis.graph;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import br.usp.each.saeg.bytecode.analysis.BytecodeInstruction;
import br.usp.each.saeg.bytecode.analysis.BytecodeInstructionType;

/**
 * A node of a graph
 * 
 * @author Felipe Albuquerque
 * 
 * modified by Roberto Araujo
 */
public class GraphNode {

	public final int id;
	
	public final List<BytecodeInstruction> instructions = new ArrayList<BytecodeInstruction>();
	
	private final List<GraphNode> children = new ArrayList<GraphNode>(2);
	private final List<GraphNode> parents = new ArrayList<GraphNode>(2);
	
	private final List<GraphNode> exceptionChildren = new ArrayList<GraphNode>(1);
	
	/**
	 * Creates a node of a graph
	 * 
	 * @param id the identifier of the node
	 */
	public GraphNode(final int id) {
		this.id = id;
	}
	
	public boolean isGotoNode() {
		for (int i = 0; i < instructions.size(); i++) {
			BytecodeInstruction insn = instructions.get(i);
			if (insn.getType() == BytecodeInstructionType.LABEL ||
					insn.getType() == BytecodeInstructionType.LINE_NUMBER ||
					insn.getType() == BytecodeInstructionType.FRAME ||
					insn.getType() == BytecodeInstructionType.GOTO) {
				continue; //skip pseudo-instructions and GOTO
			}
			
			return false;
		}
		return true;
	}

	/**
	 * Gets the children of the node
	 * 
	 * @return the children of the node
	 */
	public GraphNode[] getChildren() {
		return children.toArray(new GraphNode[children.size()]);
	}
	
	/**
	 * Gets the parents of the node
	 * 
	 * @return the parents of the node
	 */
	public GraphNode[] getParents() {
		return parents.toArray(new GraphNode[parents.size()]);
	}
	
	public GraphNode[] getExceptionChildren() {
		return exceptionChildren.toArray(new GraphNode[exceptionChildren.size()]);
	}
	
	/**
	 * Adds a child to node
	 * 
	 * @param child the child to be added
	 */
	public void addChild(final GraphNode child) {
		if ((child != null) && !children.contains(child)) {
			children.add(child);
			child.parents.add(this);
		}
	}

	/**
	 * Adds children to node
	 * 
	 * @param children the children to be added
	 */
	public void addChildren(final Collection<GraphNode> children) {
		for (final GraphNode child : children) {
			addChild(child);
		}
	}
	
	/**
	 * Adds children to node
	 * 
	 * @param children the children to be added
	 */
	public void addChildren(final GraphNode[] children) {
		for (final GraphNode child : children) {
			addChild(child);
		}
	}
	
	/**
	 * Removes a child of the node
	 * 
	 * @param child the child to be removed
	 */
	public void removeChild(final GraphNode child) {
		if (child != null) {
			children.remove(child);
			child.parents.remove(this);	
		}
	}
	
	/**
	 * Removes children of the node
	 * 
	 * @param children the children to be removed
	 */
	public void removeChildren(final Collection<GraphNode> children) {
		for (final GraphNode child : children) {
			removeChild(child);	
		}
	}
	
	/**
	 * Removes children of the node
	 * 
	 * @param children the children to be removed
	 */
	public void removeChildren(final GraphNode[] children) {
		for (final GraphNode child : children) {
			removeChild(child);	
		}
	}
	
	public void addExceptionChild(final GraphNode child) {
		if ((child != null) && !exceptionChildren.contains(child)) {
			exceptionChildren.add(child);
		}
	}
	
	public void removeExceptionChildren(final Collection<GraphNode> children) {
		for (final GraphNode child : children) {
			exceptionChildren.remove(child);
		}
	}
	
	/**
	 * Object overwrite methods
	 */

	@Override
	public String toString() {
		return String.valueOf(id);
	}

	@Override
	public int hashCode() {
		return 31 * id;
	}

	@Override
	public boolean equals(final Object obj) {
		
		if (this == obj) {
			return true;
		}
		
		if ((obj != null) && (getClass() == obj.getClass())) {
			final GraphNode other = (GraphNode) obj;
			return id == other.id;
		}
		
		return false;
	}

}
