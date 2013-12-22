package br.usp.each.saeg.bytecode.analysis.graph.defuse;

import java.util.HashSet;
import java.util.Set;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

public final class DoubleWords {

	public static final DoubleWords instance = new DoubleWords();

	private final Set<Object> bag = new HashSet<Object>();

	private DoubleWords() {
		bag.add(Opcodes.LALOAD);
		bag.add(Opcodes.DALOAD);
		bag.add(Opcodes.LASTORE);
		bag.add(Opcodes.DASTORE);
		
		bag.add(Opcodes.LADD);
		bag.add(Opcodes.DADD);
		bag.add(Opcodes.LSUB);
		bag.add(Opcodes.DSUB);
		bag.add(Opcodes.LMUL);
		bag.add(Opcodes.DMUL);
		bag.add(Opcodes.LDIV);
		bag.add(Opcodes.DDIV);
		bag.add(Opcodes.LREM);
		bag.add(Opcodes.DREM);
		bag.add(Opcodes.LSHL);
		bag.add(Opcodes.LSHR);
		bag.add(Opcodes.LUSHR);
		bag.add(Opcodes.LAND);
		bag.add(Opcodes.LOR);
		bag.add(Opcodes.LXOR);

		bag.add(Opcodes.DCONST_0);
		bag.add(Opcodes.DCONST_1);
		bag.add(Opcodes.LCONST_0);
		bag.add(Opcodes.LCONST_1);
		bag.add(Double.class);
		bag.add(Long.class);

		bag.add(Opcodes.I2L);
		bag.add(Opcodes.F2L);
		bag.add(Opcodes.D2L);
		bag.add(Opcodes.I2D);
		bag.add(Opcodes.L2D);
		bag.add(Opcodes.F2D);

		bag.add(Type.DOUBLE_TYPE);
		bag.add(Type.LONG_TYPE);

		bag.add(Opcodes.LLOAD);
		bag.add(Opcodes.DLOAD);
		bag.add(Opcodes.LSTORE);
		bag.add(Opcodes.DSTORE);
	}

	public boolean contains(final Object obj) {
		return bag.contains(obj);
	}

}
