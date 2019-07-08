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

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

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

		AtomicInteger cartTotal = new AtomicInteger(0);

		cartItems.entrySet().stream().forEach(e -> cartTotal.addAndGet(e.getValue().getPrice()*e.getValue().getQuantity()));

		checkoutCart.setCartTotal(cartTotal.get());
		
		checkoutCart.setCartItems(cartItems);
		
		Map<String, Offer> shopOffers = discountsData.getShopOffers();

		//offer total
		AtomicInteger offerTotal = new AtomicInteger(0);
		
		Map<String, Offer> checkoutOffers = discountsData.getCheckoutOffers();

		shopOffers.entrySet().stream().forEach(e-> {
			if ( !isExpired(e.getValue().getExpiryOn())
					&& e.getValue().getType()==Discount.PERCENTAGE.value()
					&& cartItems.containsKey(e.getValue().getItem()) && cartItems.containsKey(e.getValue().getRequiredItem())
					&& e.getValue().getRequiredQuantityMin() <= cartItems.get(e.getValue().getRequiredItem()).getQuantity()) {

				//applicable quantity determined by cart quantity divisible by offer required quantity minus reminder
				int applicableQuantity = cartItems.get(e.getValue().getRequiredItem()).getQuantity() -
						(cartItems.get(e.getValue().getRequiredItem()).getQuantity() % e.getValue().getRequiredQuantityMin());

				//Further applicable quantity determined based on offer applicable count and determined applicableQuantity which ever lesser
				applicableQuantity = applicableQuantity > e.getValue().getApplicableCount() ? e.getValue().getApplicableCount() : applicableQuantity;

				//Actual applicable quantity is determined based on actual cart item quantity and available applicable quantity whichever lesser
				applicableQuantity = applicableQuantity > cartItems.get(e.getValue().getItem()).getQuantity() ? cartItems.get(e.getValue().getItem()).getQuantity() : applicableQuantity;

				int offerAmount = applicableQuantity * cartItems.get(e.getValue().getItem()).getPrice() * e.getValue().getQuantity() /100;

				offerTotal.addAndGet(offerAmount);

				CheckoutOffer checkoutOffer = (CheckoutOffer) discountsData.getCheckoutOffer(e.getValue().getName());
				checkoutOffer.setOfferAppliedQuantity(applicableQuantity);
				checkoutOffer.setOfferAmount(checkoutOffer.getOfferAmount()+offerAmount);
				checkoutOffers.put(checkoutOffer.getName(), checkoutOffer);
			}
		});
		
		checkoutCart.setOfferTotal(offerTotal.get());
			 
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

		checkoutCart.getCartItems().entrySet().stream().forEach(e->{
			double itemTotal = e.getValue().getPrice()*e.getValue().getQuantity();
			itemTotal = itemTotal/100;
			if(itemTotal>1)
				System.out.println(e.getValue().getName()+"x"+e.getValue().getQuantity()+"  "+ currencyFormat.format(itemTotal));
			else
				System.out.println(e.getValue().getName()+"x"+e.getValue().getQuantity()+"  "+ penceFormat.format(itemTotal));
		});
		
        double subTotal = checkoutCart.getCartTotal();
        subTotal = subTotal/100;

        System.out.println("Subtotal: "+ currencyFormat.format(subTotal) );
	
		if (checkoutCart.getCheckoutOffers()!=null && checkoutCart.getCheckoutOffers().size()>0) {
			checkoutCart.getCheckoutOffers().entrySet().stream().forEach(e ->{
				CheckoutOffer checkoutOffer = (CheckoutOffer) e.getValue();

				double offerSubTotal = checkoutOffer.getOfferAmount();
				offerSubTotal = offerSubTotal/100;

				if (offerSubTotal >= 1)
					System.out.println(checkoutOffer.getItem()+" "+checkoutOffer.getQuantity()+"% off: -"+ currencyFormat.format(offerSubTotal) );
				else
					System.out.println(checkoutOffer.getItem()+" "+checkoutOffer.getQuantity()+"% off: -"+ penceFormat.format(offerSubTotal) );
			});
		}
		else 
			System.out.println("(no offers available)");
		
		double totalAmount = checkoutCart.getCartTotal() - checkoutCart.getOfferTotal();
		
		totalAmount = totalAmount/100;
			
		System.out.println("Total: "+ currencyFormat.format(totalAmount) );
	}

}
