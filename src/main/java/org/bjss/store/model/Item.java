package org.bjss.store.model;

import java.math.BigDecimal;
import java.math.MathContext;

public class Item {
	private String name;
	private BigDecimal price = new BigDecimal(0, new MathContext(2));
	private int quantity;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public BigDecimal getPrice() {
		return price;
	}

	public void setPrice(BigDecimal price) {
		this.price = price;
	}
	
    public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	@Override
    public String toString() {
        return "Item{" +
                "name='" + name + "'" +
                ", price='" + price + "'" +
                ", quantity='" + quantity + "'" +
                '}';
    }	
}