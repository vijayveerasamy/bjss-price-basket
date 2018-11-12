package org.bjss.store;

import org.junit.Assert;

import java.util.Map;

import org.bjss.store.model.CheckoutCart;
import org.bjss.store.model.Item;
import org.bjss.store.service.EmptyCartException;
import org.bjss.store.service.ShoppingService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Profile;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class PriceBasketApplicationTest {
	
	@Autowired
	public ShoppingService shoppingService;
	
	@Test
	public void noOfferCart() {
		String[] args = {"Apples", "Bread", "Soup", "Milk", "Soup"};
		
		Map<String, Item> shopItems = shoppingService.getShopItems();
		
		//Assert shop items count=4
		Assert.assertEquals(4, shopItems.size());
		
		Map<String, Item> cartItems = shoppingService.createCartItems();
		
    	for (String itemName: args)
    		shoppingService.addItemToCart(cartItems, itemName, 1);
    	
    	//Assert cart items count=4
    	Assert.assertEquals(4, cartItems.size());
    	
    	CheckoutCart checkoutCart = null;
    	
    	try {
    		checkoutCart = shoppingService.checkout(cartItems);
			shoppingService.printCheckout(checkoutCart);
		} catch (EmptyCartException e) {
			e.printStackTrace();
		}
		
    	Assert.assertEquals(440, checkoutCart.getCartTotal());
    	Assert.assertEquals(50, checkoutCart.getOfferTotal());
    	Assert.assertEquals(390, checkoutCart.getCheckoutTotal());

	}

}
