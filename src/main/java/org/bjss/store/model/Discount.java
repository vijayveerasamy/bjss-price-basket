package org.bjss.store.model;

public enum Discount {
	AMOUNT(1),
	PERCENTAGE(2),
	MULTIBUY(3);
	
	Discount(int value) { this.value = value; }
    private final int value;
    public int value() { return value; }
}
