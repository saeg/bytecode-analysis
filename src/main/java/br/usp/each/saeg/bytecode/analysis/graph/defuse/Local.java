package br.usp.each.saeg.bytecode.analysis.graph.defuse;

public class Local extends VariableRef {
	
	public final int opcode;
	public final int var;
	
	public Local(final int opcode, final int var) {
		this.opcode = opcode;
		this.var = var;
	}
	
	@Override
	public int size() {
		return DoubleWords.instance.contains(opcode) ? 2 : 1;
	}
	
	@Override
	public String toString() {
		return String.format("L@%d", var);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + var;
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
		
		final Local other = (Local) obj;
		
		if (var != other.var)
			return false;
		
		return true;
	}
	
}
