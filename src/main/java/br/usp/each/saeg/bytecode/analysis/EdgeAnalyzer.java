package br.usp.each.saeg.bytecode.analysis;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.analysis.Analyzer;
import org.objectweb.asm.tree.analysis.AnalyzerException;
import org.objectweb.asm.tree.analysis.BasicInterpreter;
import org.objectweb.asm.tree.analysis.BasicValue;
import org.objectweb.asm.tree.analysis.Frame;

import br.usp.each.saeg.bytecode.analysis.domain.Edge;

public class EdgeAnalyzer extends Analyzer<BasicValue> {

	private Set<Edge> edges = Collections.emptySet();
	
	private Set<Edge> exceptionEdges = Collections.emptySet();
	
	private final boolean exceptionFlow;
	
	public EdgeAnalyzer(final boolean exceptionFlow) {
		super(new BasicInterpreter());
		this.exceptionFlow = exceptionFlow;
	}
	
	@Override
	public Frame<BasicValue>[] analyze(final String owner, final MethodNode m) 
			throws AnalyzerException {
		
		edges = new LinkedHashSet<Edge>();
		exceptionEdges = new LinkedHashSet<Edge>();
		return super.analyze(owner, m);
	}
	
	@Override
	protected void newControlFlowEdge(final int src, final int dest) {
		edges.add(new Edge(src, dest));
	}
	
	@Override
	protected boolean newControlFlowExceptionEdge(final int src, final int dest) {
		if (exceptionFlow) {
			exceptionEdges.add(new Edge(src, dest));
			return true;
		}
		return false;
	}
	
	public Edge[] getEdges() {
		return edges.toArray(new Edge[edges.size()]);
	}

	public Edge[] getExceptionEdges() {
		return exceptionEdges.toArray(new Edge[exceptionEdges.size()]);
	}

}
