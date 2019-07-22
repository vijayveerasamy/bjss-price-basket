package org.bjss.store.model;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.Map;

public class CheckoutCart {
	private Map<String, Item> cartItems;
	private Map<String, Offer> checkoutOffers;
	private BigDecimal cartTotal = new BigDecimal(0, new MathContext(2));
	private BigDecimal offerTotal = new BigDecimal(0, new MathContext(2));
	private BigDecimal checkoutTotal = new BigDecimal(0, new MathContext(2));
	
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

	public BigDecimal getOfferTotal() {
		return offerTotal;
	}

	public void setOfferTotal(BigDecimal offerTotal) {
		this.offerTotal = offerTotal;
	}

	public BigDecimal getCheckoutTotal() {
		return checkoutTotal;
	}

	public void setCheckoutTotal(BigDecimal checkoutTotal) {
		this.checkoutTotal = checkoutTotal;
	}

	public Map<String, Offer> getCheckoutOffers() {
		return checkoutOffers;
	}

	public void setCheckoutOffers(Map<String, Offer> checkoutOffers) {
		this.checkoutOffers = checkoutOffers;
	}
}
