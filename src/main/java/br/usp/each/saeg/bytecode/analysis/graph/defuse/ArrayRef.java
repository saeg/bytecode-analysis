package br.usp.each.saeg.bytecode.analysis.graph.defuse;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ArrayRef implements ValueRef {
	
	public final String desc;
	public final ValueRef[] counts;
	
	public ArrayRef(final String desc, final ValueRef... counts) {
		this.desc = desc;
		this.counts = counts;
	}
	
	@Override
	public List<VariableRef> getVariableRefs() {
		final List<VariableRef> values = new ArrayList<VariableRef>();
		for (final ValueRef value : counts) {
			values.addAll(value.getVariableRefs());
		}
		return Collections.unmodifiableList(values);
	}
	
	@Override
	public int size() {
		return 1;
	}
	
	@Override
	public String toString() {
		return String.format("%s(%s[%s])", 
				getClass().getSimpleName(), desc, Arrays.asList(counts));
	}
	
}
