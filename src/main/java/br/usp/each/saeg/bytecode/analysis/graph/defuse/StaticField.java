package br.usp.each.saeg.bytecode.analysis.graph.defuse;

public class StaticField extends FieldRef {
	
	public StaticField(final String owner, 
					   final String name, 
					   final String desc) {
		
		super(owner, name, desc);
	}
	
	@Override
	public String toString() {
		return String.format("S@%s.%s", owner.replace("/", "."), name);
	}
	
}
