package br.usp.each.saeg.bytecode.analysis.domain;

/**
 * A method description  
 * 
 * @author Felipe Albuquerque
 */
public class Method {

	public final String owner;
	public final String name;
	public final String desc;

	/**
	 *  Creates a method
	 *  
	 * @param owner the name of the class that contains the method
	 * @param name the name of the method
	 * @param desc the descriptor of the method
	 */
	public Method(final String owner, 
				  final String name, 
				  final String desc) {
		
		this.owner = owner;
		this.name = name;
		this.desc = desc;
	}

	@Override
	public String toString() {
		return owner + "." + name + desc;
	}
	
}
