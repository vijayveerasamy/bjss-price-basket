package org.bjss.store.service.impl;

import org.bjss.store.entity.DiscountsData;
import org.bjss.store.entity.ProductsData;
import org.bjss.store.model.CheckoutCart;
import org.bjss.store.model.CheckoutOffer;
import org.bjss.store.model.Discount;
import org.bjss.store.model.Item;
import org.bjss.store.model.Offer;
import org.bjss.store.service.EmptyCartException;
import org.bjss.store.service.ShoppingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class ShoppingServiceImpl implements ShoppingService {
	
	private ProductsData productsData;
	private DiscountsData discountsData;
	
	public ProductsData getProductsData() {
		return productsData;
	}

	@Autowired
	public void setProductsData(ProductsData productsData) {
		this.productsData = productsData;
	}
	
	public DiscountsData getDiscountsData() {
		return discountsData;
	}

	@Autowired
	public void setDiscountsData(DiscountsData discountsData) {
		this.discountsData = discountsData;
	}
	
	@Override
	public Map<String, Item> getShopItems() {
		return productsData.getShopItems();
	}

	@Override
	public Map<String, Item> createCartItems() {
		return new ConcurrentHashMap<String, Item>();
	}

	@Override
	public boolean addItemToCart(Map<String, Item> cartItems, String itemName, int quantity) {
		boolean operationStatus = false;
		if (cartItems.containsKey(itemName)==false && productsData.getShopItems().containsKey(itemName)==true) {
			Item cartItem = productsData.getShopItemForCart(itemName, quantity);
			cartItems.put(itemName, cartItem);
			operationStatus = true;
		}
		else if (cartItems.containsKey(itemName)==true && productsData.getShopItems().containsKey(itemName)==true) { 
			Item cartItem = cartItems.get(itemName);
			cartItem.setQuantity(cartItem.getQuantity()+1);
			operationStatus = true;
		}
		
		return operationStatus;
	}
	
	@Override
	public boolean removeItemFromCart(Map<String, Item> cartItems, String itemName, int quantity) {
		boolean operationStatus = false;
		if (cartItems.containsKey(itemName)==true) {
			Item cartItem = cartItems.get(itemName);
			cartItem.setQuantity(cartItem.getQuantity()-quantity);
			if (cartItem.getQuantity()<=0)
				cartItems.remove(itemName);
			operationStatus = true;
		}
		return operationStatus;
	}

	@Override
	public CheckoutCart checkout(Map<String, Item> cartItems) throws EmptyCartException {
		CheckoutCart checkoutCart = new CheckoutCart();
		
		if (cartItems!=null &&cartItems.size()==0)
			throw new EmptyCartException("No products found in the cart.");
				
		Item cartItem = null;
		int cartTotal = 0;
		
		for (Map.Entry<String, Item> entry : cartItems.entrySet()) {
			cartItem = entry.getValue();
			cartTotal += cartItem.getPrice() * cartItem.getQuantity();
		}

		checkoutCart.setCartTotal(cartTotal);
		
		checkoutCart.setCartItems(cartItems);
		
		Item requiredCartItem = null;
		
		Map<String, Offer> shopOffers = discountsData.getShopOffers();
		
		Offer shopOffer = null;
				
		//actual count in the cart
		int applicableQuantity = 0;
		//offer amount
		int offerAmount = 0;
		
		//offer total
		int offerTotal = 0;
		
		Map<String, Offer> checkoutOffers = discountsData.getCheckoutOffers();
		
		for (Map.Entry<String, Offer> entry : shopOffers.entrySet()) {
			shopOffer = entry.getValue();
			
			//if offer expired, continue to next offer
			if ( isExpired(shopOffer.getExpiryOn()) ) continue;
			
			//if offer is percentage type
			if (shopOffer.getType()==Discount.PERCENTAGE.value()) {
				
				//both offer item, required item must be available in the cart
				if (  cartItems.containsKey(shopOffer.getItem()) && cartItems.containsKey(shopOffer.getRequiredItem()) ) {
					
					cartItem = cartItems.get(shopOffer.getItem());	
					requiredCartItem = cartItems.get(shopOffer.getRequiredItem());
				
					//if offer required quantity is less or equal to required item cart quantity
					if ( shopOffer.getRequiredQuantityMin() <= requiredCartItem.getQuantity() ) {
										
						//applicable quantity determined by cart quantity divisible by offer required quantity minus reminder
						applicableQuantity = requiredCartItem.getQuantity() - (requiredCartItem.getQuantity() % shopOffer.getRequiredQuantityMin());
						
						//Further applicable quantity determined based on offer applicable count and determined applicableQuantity which ever lesser
						applicableQuantity = applicableQuantity > shopOffer.getApplicableCount() ? shopOffer.getApplicableCount() : applicableQuantity;
						
						//Actual applicable quantity is determined based on actual cart item quantity and available applicable quantity whichever lesser
						applicableQuantity = applicableQuantity > cartItem.getQuantity() ? cartItem.getQuantity() : applicableQuantity;
						
						offerAmount = applicableQuantity * cartItem.getPrice() * shopOffer.getQuantity() /100;
						
						offerTotal += offerAmount;
						
						CheckoutOffer checkoutOffer = (CheckoutOffer) discountsData.getCheckoutOffer(shopOffer.getName());
						
						checkoutOffer.setOfferAppliedQuantity(applicableQuantity);
						checkoutOffer.setOfferAmount(offerAmount);
						
						checkoutOffers.put(checkoutOffer.getName(), checkoutOffer);
						
					}

					//offer required quantity greater then required quantity in the cart then continue to next offer
					else if ( shopOffer.getRequiredQuantityMin() > cartItems.get(shopOffer.getRequiredItem()).getQuantity() ) 
					continue;				
					
				}
					
			}
		}	
		
		checkoutCart.setOfferTotal(offerTotal);
			 
		checkoutCart.setCheckoutOffers(checkoutOffers);
		
		checkoutCart.setCheckoutTotal(checkoutCart.getCartTotal()-checkoutCart.getOfferTotal());
		 
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
			e.printStackTrace();
		}
				
		return expiryStatus;		
	}

	@Override
	public void printCheckout(CheckoutCart checkoutCart) throws EmptyCartException {
		if (checkoutCart!=null && checkoutCart.getCartItems()!=null &&checkoutCart.getCartItems().size()==0)
			throw new EmptyCartException("No products found in the cart.");
		
        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
        symbols.setGroupingSeparator(',');
        String pattern = "£#,##0.00";
        DecimalFormat currencyFormat = new DecimalFormat(pattern, symbols);
        
        pattern = "0.00p";
        
        DecimalFormat penceFormat = new DecimalFormat(pattern, symbols);
		
        double subTotal = checkoutCart.getCartTotal();
        subTotal = subTotal/100;
        
        double offerSubTotal = 0;

        System.out.println("Subtotal: "+ currencyFormat.format(subTotal) );
	
		if (checkoutCart.getCheckoutOffers()!=null && checkoutCart.getCheckoutOffers().size()>0) {
			for (Map.Entry<String, Offer> entry : checkoutCart.getCheckoutOffers().entrySet()) {
				CheckoutOffer checkoutOffer = (CheckoutOffer) entry.getValue();
				
				offerSubTotal = checkoutOffer.getOfferAmount();
				offerSubTotal = offerSubTotal/100;
				
				if (offerSubTotal >= 1)				
					System.out.println(checkoutOffer.getItem()+" "+checkoutOffer.getQuantity()+"% off: -"+ currencyFormat.format(offerSubTotal) );
				else
					System.out.println(checkoutOffer.getItem()+" "+checkoutOffer.getQuantity()+"% off: -"+ penceFormat.format(offerSubTotal) );
			}			
		}
		else 
			System.out.println("(no offers available)");
		
		double totalAmount = checkoutCart.getCartTotal() - checkoutCart.getOfferTotal();
		
		totalAmount = totalAmount/100;
			
		System.out.println("Total: "+ currencyFormat.format(totalAmount) );
	}

}
