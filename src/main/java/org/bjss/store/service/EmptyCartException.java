package org.bjss.store.service;

public class EmptyCartException extends Exception {
	private static final long serialVersionUID = 7491950434081250232L;
	
	public EmptyCartException() {
        super();
    }

	public EmptyCartException(String errorMessage) {
        super(errorMessage);
    }
}
