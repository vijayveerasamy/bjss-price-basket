package org.bjss.store;

import java.util.Map;

import org.bjss.store.model.CheckoutCart;
import org.bjss.store.model.Item;
import org.bjss.store.service.EmptyCartException;
import org.bjss.store.service.ShoppingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Profile;

@SpringBootApplication
public class PriceBasketApplication implements CommandLineRunner {
	
	private ShoppingService shoppingService;
	
	private Map<String,Item> cartItems;
	

    @Override
    public void run(String... args) {
    	
    	cartItems = shoppingService.createCartItems();
    	
    	for (String itemName: args)
    		shoppingService.addItemToCart(cartItems, itemName, 1);
    	
    	CheckoutCart checkoutCart = null;
    	
    	try {
			checkoutCart = shoppingService.checkout(cartItems);
			shoppingService.printCheckout(checkoutCart);
		} catch (EmptyCartException ex) {
			ex.printStackTrace();
		}
    	
    }
    
    public static void main(String[] args) throws Exception {

        SpringApplication app = new SpringApplication(PriceBasketApplication.class);

        app.run(args);
    }
    	

	public ShoppingService getShoppingService() {
		return shoppingService;
	}

	@Autowired
	public void setShoppingService(ShoppingService shoppingService) {
		this.shoppingService = shoppingService;
	}
    
    
}
