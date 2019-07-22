package org.bjss.store;

import org.bjss.store.model.ShoppingCart;
import org.junit.Assert;
import org.bjss.store.model.CheckoutCart;
import org.bjss.store.model.Item;
import org.bjss.store.service.EmptyCartException;
import org.bjss.store.service.ShoppingService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.assertj.core.api.Assertions.*;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class PriceBasketApplicationTest {
	
	@Autowired
	public ShoppingService shoppingService;
	
	@Test
	public void noOfferCart() {
		String[] args = {"Apples", "Bread", "Soup", "Milk", "Soup"};

		ShoppingCart shoppingCart = new ShoppingCart();
		
    	for (String itemName: args)
			shoppingService.addItemToCart(shoppingCart, itemName,1);
    	
    	//Assert cart items count=4
    	Assert.assertEquals(4, shoppingCart.getCartItems().size());
    	
    	CheckoutCart checkoutCart = null;
    	
    	try {
    		checkoutCart = shoppingService.checkout(shoppingCart);
			shoppingService.printCheckout(checkoutCart);
		} catch (EmptyCartException e) {
			e.printStackTrace();
		}

		assertThat(checkoutCart.getCartTotal()).isEqualByComparingTo("4.40");
		assertThat(checkoutCart.getOfferTotal()).isEqualByComparingTo("0.50");
		assertThat(checkoutCart.getCheckoutTotal()).isEqualByComparingTo("3.90");
	}

}
