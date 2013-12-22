package br.usp.each.saeg.bytecode.analysis.graph.defuse;

import java.util.Collections;
import java.util.List;

public abstract class VariableRef implements ValueRef {
	
	@Override
	public List<VariableRef> getVariableRefs() {
		return Collections.singletonList(this);
	}

}
