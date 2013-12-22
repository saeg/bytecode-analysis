package br.usp.each.saeg.bytecode.analysis;

/**
 * Exception thrown when an error occurs while generating a graph
 * 
 * @author Felipe Albuquerque
 * 
 * modified by Roberto Araujo
 */
public class CouldNotBuildGraphException extends Exception {

	private static final long serialVersionUID = -2014589602485161613L;

	/**
	 * Creates a new exception
	 * 
	 * @param className the name of the class for what the graph couldn't be built
	 * @param methodName the name of the method for what the graph couldn't be built
	 * @param cause the throwable that caused this exception
	 */
	public CouldNotBuildGraphException(final String className, 
									   final String methodName, 
									   final Throwable cause) {
		
		super("Could not build graph for the method " 
				+ className + "." + methodName, cause);
	}
	
}
