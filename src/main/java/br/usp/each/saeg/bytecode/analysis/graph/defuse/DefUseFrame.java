package br.usp.each.saeg.bytecode.analysis.graph.defuse;

import java.util.List;

public class DefUseFrame {
	
	public final VariableRef def;
	
	public final List<VariableRef> uses;
	
	public DefUseFrame(final VariableRef definition, final ValueRef use) {
		this.def = definition;
		this.uses = use.getVariableRefs();
	}
	
	public DefUseFrame(final VariableRef definition, final List<VariableRef> uses) {
		this.def = definition;
		this.uses = uses;
	}

	@Override
	public String toString() {
		return String.format("Def: %s Use: %s", def, uses);
	}
}
