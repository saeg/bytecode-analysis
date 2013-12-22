package br.usp.each.saeg.bytecode.analysis.graph.defuse;

import java.util.List;

public class Conversion implements ValueRef {

	public final int opcode;
	public final ValueRef value;

	public Conversion(final int opcode, final ValueRef value) {
		this.opcode = opcode;
		this.value = value;
	}
	
	@Override
	public List<VariableRef> getVariableRefs() {
		return value.getVariableRefs();
	}
	
	@Override
	public int size() {
		return DoubleWords.instance.contains(opcode) ? 2 : 1;
	}
	
	@Override
	public String toString() {
		return value.toString();
	}

}
