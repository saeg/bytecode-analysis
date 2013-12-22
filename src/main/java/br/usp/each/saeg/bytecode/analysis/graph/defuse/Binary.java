package br.usp.each.saeg.bytecode.analysis.graph.defuse;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Binary implements ValueRef {
	
	public final int opcode;
	public final ValueRef value1;
	public final ValueRef value2;

	public Binary(final int opcode, 
				  final ValueRef value1, 
				  final ValueRef value2) {
		
		this.opcode = opcode;
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
		return DoubleWords.instance.contains(opcode) ? 2 : 1;
	}
	
	@Override
	public String toString() {
		return String.format("%s(%s,%s)", 
				getClass().getSimpleName(), value1, value2);
	}
	
}
