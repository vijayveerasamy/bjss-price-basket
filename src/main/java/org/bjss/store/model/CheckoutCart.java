package org.bjss.store.model;

import java.util.Map;

public class CheckoutCart {
	private Map<String, Item> cartItems;
	private Map<String, Offer> checkoutOffers;
	private int cartTotal;
	private int offerTotal;
	private int checkoutTotal;
	
	public Map<String, Item> getCartItems() {
		return cartItems;
	}
	
	public void setCartItems(Map<String, Item> cartItems) {
		this.cartItems = cartItems;
	}

	public int getCartTotal() {
		return cartTotal;
	}

	public void setCartTotal(int cartTotal) {
		this.cartTotal = cartTotal;
	}

	public int getOfferTotal() {
		return offerTotal;
	}

	public void setOfferTotal(int offerTotal) {
		this.offerTotal = offerTotal;
	}

	public int getCheckoutTotal() {
		return checkoutTotal;
	}

	public void setCheckoutTotal(int checkoutTotal) {
		this.checkoutTotal = checkoutTotal;
	}

	public Map<String, Offer> getCheckoutOffers() {
		return checkoutOffers;
	}

	public void setCheckoutOffers(Map<String, Offer> checkoutOffers) {
		this.checkoutOffers = checkoutOffers;
	}
}
