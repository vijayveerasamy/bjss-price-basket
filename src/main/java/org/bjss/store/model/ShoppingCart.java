package org.bjss.store.model;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.HashMap;
import java.util.Map;

public class ShoppingCart {
	private Map<String, Item> cartItems = new HashMap<>();
	private BigDecimal cartTotal = new BigDecimal(0, new MathContext(2));
	
	public Map<String, Item> getCartItems() {
		return cartItems;
	}
	
	public void setCartItems(Map<String, Item> cartItems) {
		this.cartItems = cartItems;
	}

	public BigDecimal getCartTotal() {
		return cartTotal;
	}

	public void setCartTotal(BigDecimal cartTotal) {
		this.cartTotal = cartTotal;
	}
}
