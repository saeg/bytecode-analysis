package br.usp.each.saeg.bytecode.analysis.graph.defuse;

import java.util.Collections;
import java.util.List;

public class ObjectRef implements ValueRef {
	
	public final String desc;
	
	public ObjectRef(final String desc) {
		this.desc = desc;
	}
	
	@Override
	public List<VariableRef> getVariableRefs() {
		return Collections.emptyList();
	}
	
	@Override
	public int size() {
		return 1;
	}
	
	@Override
	public String toString() {
		return String.format("%s(%s)", 
				getClass().getSimpleName(), desc);
	}
	
}
