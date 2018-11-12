package org.bjss.store.entity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.bjss.store.model.CheckoutOffer;
import org.bjss.store.model.Offer;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Repository;

@Repository
@PropertySource("classpath:discounts.properties")
@ConfigurationProperties("discounts")
public class DiscountsData {
	
    private List<Offer> offers;
    
	private Map<String, Offer> shopOffers;//instance bound

	public List<Offer> getOffers() {
		return offers;
	}

	public void setOffers(List<Offer> offers) {
		this.offers = offers;
	}

	public Map<String, Offer> getShopOffers() {
		return shopOffers;
	}

	@PostConstruct
	public void setShopOffers() {
		shopOffers = new HashMap<>();
		
		for (Offer offer : this.offers) {
			shopOffers.put(offer.getName(), offer);
		}
	}	
	
	public Map<String, Offer> getCheckoutOffers() {
		return new HashMap<>();
	}
	
    public Offer getCheckoutOffer(String offerName) { 
    	Offer shopOffer = (Offer) shopOffers.get(offerName);
    	Offer newOffer = new CheckoutOffer();
    	
    	newOffer.setName(shopOffer.getName());
    	newOffer.setType(shopOffer.getType());
    	newOffer.setQuantity(shopOffer.getQuantity());
    	newOffer.setItem(shopOffer.getItem());
    	newOffer.setRequiredItem(shopOffer.getRequiredItem());
    	newOffer.setRequiredQuantityMin(shopOffer.getRequiredQuantityMin());
    	newOffer.setApplicableCount(shopOffer.getApplicableCount());
    	newOffer.setExpiryOn(shopOffer.getExpiryOn());
    	
    	return newOffer;
    } 
}
