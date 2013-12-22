package br.usp.each.saeg.bytecode.analysis.graph.defuse;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ArrayValue implements ValueRef {

	public final int opcode;
	public final ValueRef arref;
	public final ValueRef index;

	public ArrayValue(final int opcode, 
					  final ValueRef arref, 
					  final ValueRef index) {
		
		this.opcode = opcode;
		this.arref = arref;
		this.index = index;
	}
	
	@Override
	public List<VariableRef> getVariableRefs() {
		final List<VariableRef> values = new ArrayList<VariableRef>();
		
		int dims = 1;
		ValueRef carref = arref;
		while (carref instanceof ArrayValue) {
			dims++;
			carref = ArrayValue.class.cast(carref).arref;
		}
		
		ValueRef root = carref;
		while (root instanceof ObjectField) {
			root = ObjectField.class.cast(root).objectref;
		}
		
		if (carref instanceof VariableRef &&
				(root instanceof Local || root instanceof StaticField)) {
			
			ArrayComponent component = new ArrayComponent(carref);
			while (--dims > 0) {
				component = new ArrayComponent(component);
			}
			values.add(component);
		}
		
		values.addAll(arref.getVariableRefs());
		values.addAll(index.getVariableRefs());
		return Collections.unmodifiableList(values);
	}
	
	@Override
	public int size() {
		return DoubleWords.instance.contains(opcode) ? 2 : 1;
	}

	@Override
	public String toString() {
		return String.format("%s(%s[%s])", 
				getClass().getSimpleName(), arref, index);
	}
	
}
