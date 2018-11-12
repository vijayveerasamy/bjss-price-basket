package org.bjss.store.service;

import java.util.Map;

import org.bjss.store.model.CheckoutCart;
import org.bjss.store.model.Item;

/**
 * This interface defines shopping cart functions for the Price Basket application
 * @author Vijay Veerasamy
 *
 */

public interface ShoppingService {
	/**
	 * This function is used to get the products for the controller class to display the products.
	 * @return Map contains product name as key and product object as value for all products
	 */
	public Map<String, Item> getShopItems();
	
	/**
	 * This method creates empty shopping cart.
	 * @return empty Map<String, Item>
	 */
	public Map<String, Item> createCartItems();
	
	/**
	 * This method adds the product one at time in the cart
	 * It ignores if product not found.
	 * @param cartItems Map<String, Item>
	 * @param itemName String
	 * @return boolean true for successful or false for failed
	 */
	public boolean addItemToCart(Map<String, Item> cartItems, String itemName, int quantity);
	
	/**
	 * This method removes the product completely
	 * It ignores if product not found.
	 * @param cartItems Map<String, Item>
	 * @param itemName String
	 * @return boolean true for successful or false for failed
	 */
	public boolean removeItemFromCart(Map<String, Item> cartItems, String itemName, int quantity);
	
	/**
	 * This method prepares the checkout by applying discounts and calculating the total amount
	 * @param cartItems
	 * @throws EmptyCartException
	 */
	
	public CheckoutCart checkout(Map<String, Item> cartItems) throws EmptyCartException;

	public void printCheckout(CheckoutCart checkoutCart) throws EmptyCartException;;
}
