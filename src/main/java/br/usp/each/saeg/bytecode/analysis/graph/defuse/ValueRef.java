package br.usp.each.saeg.bytecode.analysis.graph.defuse;

import java.util.List;

public interface ValueRef {
	
	List<VariableRef> getVariableRefs();
	
	int size();

}
