package br.usp.each.saeg.bytecode.analysis.graph.defuse;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ObjectField extends FieldRef {

	public final ValueRef objectref;
	
	public ObjectField(final String owner, 
					   final String name, 
					   final String desc, 
					   final ValueRef objectref) {
		
		super(owner, name, desc);
		this.objectref = objectref;
	}
	
	@Override
	public List<VariableRef> getVariableRefs() {
		final List<VariableRef> values = new ArrayList<VariableRef>();
		
		values.addAll(objectref.getVariableRefs());
		
		ValueRef root = objectref;
		while (root instanceof ObjectField) {
			root = ObjectField.class.cast(root).objectref;
		}
		
		if (objectref instanceof VariableRef &&
				(root instanceof Local || root instanceof StaticField)) {
			values.add(this);
		}
		
		return Collections.unmodifiableList(values);
	}
	
	@Override
	public String toString() {
		return String.format("%s.%s.%s", objectref, owner.replace("/", "."), name);
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + objectref.hashCode();
		return result;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj)
			return true;
		
		if (!super.equals(obj))
			return false;
		
		if (getClass() != obj.getClass())
			return false;
		
		final ObjectField other = (ObjectField) obj;
		
		if (!objectref.equals(other.objectref))
			return false;
		
		return true;
	}
	
}
