package br.usp.each.saeg.bytecode.analysis.graph.defuse;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Or implements ValueRef {
	
	public final ValueRef value1;
	public final ValueRef value2;

	public Or(final ValueRef value1, final ValueRef value2) {
		if (value1.size() != value2.size()) {
			throw new IllegalArgumentException("Size should be the same");
		}
		
		this.value1 = value1;
		this.value2 = value2;
	}
	
	@Override
	public List<VariableRef> getVariableRefs() {
		final List<VariableRef> values = new ArrayList<VariableRef>();
		values.addAll(value1.getVariableRefs());
		values.addAll(value2.getVariableRefs());
		return Collections.unmodifiableList(values);
	}
	
	@Override
	public int size() {
		return value1.size(); // Should be the same that value2.size()
	}
	
	@Override
	public String toString() {
		return String.format("%s(%s,%s)", 
				getClass().getSimpleName(), value1, value2);
	}
	
}
