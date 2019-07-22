package org.bjss.store.service.impl;

import org.bjss.store.component.StoreFront;
import org.bjss.store.model.*;
import org.bjss.store.service.EmptyCartException;
import org.bjss.store.service.ShoppingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class ShoppingServiceImpl implements ShoppingService {

	private StoreFront storeFront;

	private static final Logger LOGGER = LoggerFactory.getLogger(ShoppingServiceImpl.class);

	@Autowired
	public void setStoreFront(StoreFront storeFront) { this.storeFront = storeFront; }

	@Override
	public boolean addItemToCart(ShoppingCart shoppingCart, String itemName, int quantity) {
		boolean operationStatus = false;
		if (!shoppingCart.getCartItems().containsKey(itemName) && storeFront.getShopItems().containsKey(itemName)) {
			Item cartItem = storeFront.getShopItemForCart(itemName, quantity);
			shoppingCart.getCartItems().put(itemName, cartItem);
			operationStatus = true;
			LOGGER.info("{} added successfully", itemName);
		}
		else if (shoppingCart.getCartItems().containsKey(itemName) && storeFront.getShopItems().containsKey(itemName)) {
			Item cartItem = shoppingCart.getCartItems().get(itemName);
			cartItem.setQuantity(cartItem.getQuantity()+1);
			operationStatus = true;
			LOGGER.info("{} quantity {} updated successfully", itemName, cartItem.getQuantity());
		}

		shoppingCart.setCartTotal(new BigDecimal(0));
		shoppingCart.getCartItems().entrySet().stream().forEach(e -> shoppingCart.setCartTotal(
				shoppingCart.getCartTotal().add(
						e.getValue().getPrice().multiply(new BigDecimal(e.getValue().getQuantity()))) ));

		return operationStatus;
	}
	
	@Override
	public boolean removeItemFromCart(ShoppingCart shoppingCart, String itemName, int quantity) {
		boolean operationStatus = false;
		if (shoppingCart.getCartItems().containsKey(itemName)) {
			Item cartItem = shoppingCart.getCartItems().get(itemName);
			cartItem.setQuantity(cartItem.getQuantity()-quantity);
			if (cartItem.getQuantity()<=0) {
				shoppingCart.getCartItems().remove(itemName);
				LOGGER.info("{} cart quantity removed successfully", itemName);
			}
			else {
				LOGGER.info("{} count decremented from cart successfully", itemName);
			}

			operationStatus = true;

			shoppingCart.setCartTotal(new BigDecimal(0));
			shoppingCart.getCartItems().entrySet().stream().forEach(e -> shoppingCart.setCartTotal(
					shoppingCart.getCartTotal().add(
							e.getValue().getPrice().multiply(new BigDecimal(e.getValue().getQuantity()))) ));

		}
		return operationStatus;
	}

	@Override
	public CheckoutCart checkout(ShoppingCart shoppingCart) throws EmptyCartException {
		CheckoutCart checkoutCart = new CheckoutCart();
		
		if (shoppingCart.getCartItems()!=null &&shoppingCart.getCartItems().size()==0)
			throw new EmptyCartException("No products found in the cart.");

		checkoutCart.setCartTotal(shoppingCart.getCartTotal());
		
		checkoutCart.setCartItems(shoppingCart.getCartItems());
		
		Map<String, Offer> shopOffers = storeFront.getShopOffers();
		
		Map<String, Offer> checkoutOffers = new HashMap<>();

		shopOffers.entrySet().stream().forEach(e-> {
			if ( !isExpired(e.getValue().getExpiryOn())
					&& e.getValue().getType()==Discount.PERCENTAGE.value()
					&& shoppingCart.getCartItems().containsKey(e.getValue().getItem()) && shoppingCart.getCartItems().containsKey(e.getValue().getRequiredItem())
					&& e.getValue().getRequiredQuantityMin() <= shoppingCart.getCartItems().get(e.getValue().getRequiredItem()).getQuantity()) {

				//applicable quantity determined by cart quantity divisible by offer required quantity minus reminder
				int applicableQuantity = shoppingCart.getCartItems().get(e.getValue().getRequiredItem()).getQuantity() -
						(shoppingCart.getCartItems().get(e.getValue().getRequiredItem()).getQuantity() % e.getValue().getRequiredQuantityMin());

				//Further applicable quantity determined based on offer applicable count and determined applicableQuantity which ever lesser
				applicableQuantity = applicableQuantity > e.getValue().getApplicableCount() ? e.getValue().getApplicableCount() : applicableQuantity;

				//Actual applicable quantity is determined based on actual cart item quantity and available applicable quantity whichever lesser
				applicableQuantity = applicableQuantity > shoppingCart.getCartItems().get(e.getValue().getItem()).getQuantity() ? shoppingCart.getCartItems().get(e.getValue().getItem()).getQuantity() : applicableQuantity;

				BigDecimal offerAmount = shoppingCart.getCartItems().get(e.getValue().getItem()).getPrice().multiply(new BigDecimal(e.getValue().getQuantity())
						.divide(new BigDecimal(100))
						.multiply(new BigDecimal((applicableQuantity))));

				CheckoutOffer checkoutOffer = (CheckoutOffer) storeFront.getCheckoutOffer(e.getValue().getName());
				checkoutOffer.setOfferAppliedQuantity(applicableQuantity);
				checkoutOffer.setOfferAmount(checkoutOffer.getOfferAmount().add(offerAmount));

				checkoutOffers.put(checkoutOffer.getName(), checkoutOffer);
				checkoutCart.setOfferTotal(checkoutCart.getOfferTotal().add(offerAmount));
			}
		});
			 
		checkoutCart.setCheckoutOffers(checkoutOffers);
		checkoutCart.setCheckoutTotal(checkoutCart.getCartTotal().subtract(checkoutCart.getOfferTotal()));
		 
		return checkoutCart;
	}
	
	private boolean isExpired(String expiryOn) {
		boolean expiryStatus = true;
			
		expiryOn += " 23:59:59";
		
		SimpleDateFormat sdf = new SimpleDateFormat("ddMMyyyy HH:mm:ss");
		
		Date expiredDate;
		try {
			expiredDate = sdf.parse(expiryOn);
			expiryStatus = expiredDate.before(new Date());
		} catch (ParseException e) {
			LOGGER.error("Invalid offer expired date");
		}
				
		return expiryStatus;		
	}

	@Override
	public void printCheckout(CheckoutCart checkoutCart) throws EmptyCartException {
		if ( !Optional.of(checkoutCart).isPresent() ||
				!Optional.of(checkoutCart.getCartItems()).isPresent() ||
				checkoutCart.getCartItems().isEmpty() ) {
			throw new EmptyCartException("No products found in the cart.");
		}

		DecimalFormatSymbols symbols = new DecimalFormatSymbols();
		symbols.setGroupingSeparator(',');

		String pattern = "£#,##0.00";
		DecimalFormat currencyFormat = new DecimalFormat(pattern, symbols);

		pattern = "0.00p";
		DecimalFormat penceFormat = new DecimalFormat(pattern, symbols);

		if ( Optional.of(checkoutCart).isPresent() && Optional.of(checkoutCart.getCartItems()).isPresent() && !checkoutCart.getCartItems().isEmpty() ) {
			checkoutCart.getCartItems().entrySet().stream().forEach(e -> {
				BigDecimal itemTotal = e.getValue().getPrice().multiply(new BigDecimal(e.getValue().getQuantity()));

				if (itemTotal.intValue() >= 1) {
					LOGGER.info("{}x{} {}", e.getValue().getName(), e.getValue().getQuantity(), currencyFormat.format(itemTotal));
				} else {
					LOGGER.info("{}x{} {}", e.getValue().getName(), e.getValue().getQuantity(), penceFormat.format(itemTotal));
				}
			});
		}

		LOGGER.info("------------------------");
		LOGGER.info("Subtotal: {}", currencyFormat.format(checkoutCart.getCartTotal()) );

		if ( Optional.of(checkoutCart).isPresent() && Optional.of(checkoutCart.getCheckoutOffers()).isPresent() && !checkoutCart.getCheckoutOffers().isEmpty() ) {
			checkoutCart.getCheckoutOffers().entrySet().stream().forEach(e ->{
				CheckoutOffer checkoutOffer = (CheckoutOffer) e.getValue();

				BigDecimal offerSubTotal = checkoutOffer.getOfferAmount();

				if (offerSubTotal.intValue() >= 1) {
					LOGGER.info("{} {} % off: -{}", checkoutOffer.getItem(), checkoutOffer.getQuantity(), currencyFormat.format(offerSubTotal));
				}
				else {
					LOGGER.info("{} {} % off: -{}", checkoutOffer.getItem(), checkoutOffer.getQuantity(), penceFormat.format(offerSubTotal));
				}
			});
		}
		else {
			LOGGER.info("(no offers available)");
		}

		LOGGER.info("Total: {}", currencyFormat.format(checkoutCart.getCheckoutTotal()) );
	}

}
