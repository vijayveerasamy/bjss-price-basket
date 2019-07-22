package org.bjss.store.service;

import java.util.Map;

import org.bjss.store.model.CheckoutCart;
import org.bjss.store.model.Item;
import org.bjss.store.model.ShoppingCart;

/**
 * This interface defines shopping cart functions for the Price Basket application
 * @author Vijay Veerasamy
 *
 */

public interface ShoppingService {
	
	/**
	 * This method adds the product one at time in the cart
	 * It ignores if product not found.
	 * @param shoppingCart Map<String, Item>
	 * @param itemName String
	 * @return boolean true for successful or false for failed
	 */
	public boolean addItemToCart(ShoppingCart shoppingCart, String itemName, int quantity);
	
	/**
	 * This method removes the product completely
	 * It ignores if product not found.
	 * @param shoppingCart Map<String, Item>
	 * @param itemName String
	 * @return boolean true for successful or false for failed
	 */
	public boolean removeItemFromCart(ShoppingCart shoppingCart, String itemName, int quantity);
	
	/**
	 * This method prepares the checkout by applying discounts and calculating the total amount
	 * @param shoppingCart
	 * @throws EmptyCartException
	 */
	public CheckoutCart checkout(ShoppingCart shoppingCart) throws EmptyCartException;

	/**
	 * This method prints the checkout items, discounts, sub-total and total amount
	 * @param checkoutCart
	 * @throws EmptyCartException
	 */
	public void printCheckout(CheckoutCart checkoutCart) throws EmptyCartException;

}
