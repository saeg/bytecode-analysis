package br.usp.each.saeg.bytecode.analysis.graph.defuse;

public class ArrayComponent extends VariableRef {
	
	public final ValueRef arref;

	public ArrayComponent(final ValueRef arref) {
		this.arref = arref;
	}

	@Override
	public int size() {
		return arref.size();
	}
	
	@Override
	public String toString() {
		return String.format("%s[]", arref);
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + arref.hashCode();
		return result;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj)
			return true;
		
		if (obj == null)
			return false;
		
		if (getClass() != obj.getClass())
			return false;
		
		final ArrayComponent other = (ArrayComponent) obj;
		
		if (!arref.equals(other.arref))
			return false;
		
		return true;
	}

}
