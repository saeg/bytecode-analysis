package br.usp.each.saeg.bytecode.analysis.graph.defuse;

import java.util.List;

public class ArrayLength implements ValueRef {

	public final ValueRef arref;

	public ArrayLength(final ValueRef arref) {
		this.arref = arref;
	}

	@Override
	public List<VariableRef> getVariableRefs() {
		return arref.getVariableRefs();
	}
	
	@Override
	public int size() {
		return 1;
	}

	@Override
	public String toString() {
		return String.format("%s(%s)", 
				getClass().getSimpleName(), arref);
	}
	
}
