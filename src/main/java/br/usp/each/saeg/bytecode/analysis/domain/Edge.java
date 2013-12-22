package br.usp.each.saeg.bytecode.analysis.domain;

/**
 * Edge of a graph.
 */
public class Edge {
	
	public final int src;
	
	public final int dest;

	/**
	 * Builds an edge
	 * 
	 * @param src
	 *            the source node
	 * @param dest
	 *            the destination node
	 */
	public Edge(final int src, final int dest) {
		this.src = src;
		this.dest = dest;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = prime + dest;
		result = (prime * result) + src;
		return result;
	}

	@Override
	public boolean equals(final Object obj) {

		if (this == obj) {
			return true;
		}

		if ((obj != null) && (getClass() == obj.getClass())) {
			final Edge other = (Edge) obj;
			return (dest == other.dest) && (src == other.src);
		}

		return false;
	}
	
	@Override
	public String toString() {
		return String.format("(%d, %d)", src, dest);
	}

}
