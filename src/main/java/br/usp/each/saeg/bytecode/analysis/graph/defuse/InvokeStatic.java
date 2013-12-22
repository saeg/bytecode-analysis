package br.usp.each.saeg.bytecode.analysis.graph.defuse;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.objectweb.asm.Type;

import br.usp.each.saeg.bytecode.analysis.domain.Method;

public class InvokeStatic implements ValueRef {
	
	public final Method method;
	public final ValueRef[] args;

	public InvokeStatic(final Method method, 
						final ValueRef[] args) {
		
		this.method = method;
		this.args = args;
	}
	
	@Override
	public List<VariableRef> getVariableRefs() {
		final List<VariableRef> values = new ArrayList<VariableRef>();
		for (final ValueRef value : args) {
			values.addAll(value.getVariableRefs());
		}
		return Collections.unmodifiableList(values);
	}
	
	@Override
	public int size() {
		final Type type = Type.getReturnType(method.desc);
		
		if (type == Type.VOID_TYPE)
			return 0;
		
		return DoubleWords.instance.contains(type) ? 2 : 1;
	}
	
	@Override
	public String toString() {
		return String.format("%s(%s,%s)", 
				getClass().getSimpleName(), method, Arrays.asList(args));
	}
	
}
