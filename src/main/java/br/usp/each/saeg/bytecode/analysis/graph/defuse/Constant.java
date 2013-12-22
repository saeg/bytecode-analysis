package br.usp.each.saeg.bytecode.analysis.graph.defuse;

import java.util.Collections;
import java.util.List;

public class Constant implements ValueRef {
	
	private final int opcode;
	
	private final Object obj;
	
	public Constant(final int opcode) {
		this.opcode = opcode;
		this.obj = null;
	}
	
	public Constant(final Object obj) {
		this.opcode = -1;
		this.obj = obj;
	}
	
	@Override
	public List<VariableRef> getVariableRefs() {
		return Collections.emptyList();
	}
	
	@Override
	public int size() {
		if (obj == null)
			return DoubleWords.instance.contains(opcode) ? 2 : 1;
		return DoubleWords.instance.contains(obj.getClass()) ? 2 : 1;
	}
	
	@Override
	public String toString() {
		return getClass().getSimpleName();
	}
	
}
