package org.bjss.store;

import java.util.Arrays;
import org.bjss.store.model.CheckoutCart;
import org.bjss.store.model.ShoppingCart;
import org.bjss.store.service.EmptyCartException;
import org.bjss.store.service.ShoppingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class PriceBasketApplication implements CommandLineRunner {
	
	private ShoppingService shoppingService;

	private static final Logger LOGGER = LoggerFactory.getLogger(PriceBasketApplication.class);

    @Override
    public void run(String... args) {
		ShoppingCart shoppingCart = new ShoppingCart();

		Arrays.asList(args).stream().forEach(i -> shoppingService.addItemToCart(shoppingCart, i,1));
    	CheckoutCart checkoutCart = null;
    	
    	try {
			checkoutCart = shoppingService.checkout(shoppingCart);
			shoppingService.printCheckout(checkoutCart);
		} catch (EmptyCartException ex) {
			LOGGER.error(ex.getMessage());
		}
    }
    
    public static void main(String[] args) {
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
