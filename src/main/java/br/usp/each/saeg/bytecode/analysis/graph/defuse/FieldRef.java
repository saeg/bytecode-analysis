package br.usp.each.saeg.bytecode.analysis.graph.defuse;

import org.objectweb.asm.Type;

public class FieldRef extends VariableRef {

	public final String owner;
	public final String name;
	public final String desc;

	public FieldRef(final String owner, 
					final String name, 
					final String desc) {
		
		this.owner = owner;
		this.name = name;
		this.desc = desc;
	}
	
	@Override
	public int size() {
		return DoubleWords.instance.contains(Type.getType(desc)) ? 2 : 1;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + desc.hashCode();
		result = prime * result + name.hashCode();
		result = prime * result + owner.hashCode();
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
		
		final FieldRef other = (FieldRef) obj;
		
		if (!desc.equals(other.desc))
			return false;
		
		if (!name.equals(other.name))
			return false;
		
		if (!owner.equals(other.owner))
			return false;
		
		return true;
	}

}
