package org.bjss.store.component;

import org.bjss.store.entity.DiscountsData;
import org.bjss.store.entity.ProductsData;
import org.bjss.store.model.CheckoutOffer;
import org.bjss.store.model.Item;
import org.bjss.store.model.Offer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.Map;

@Component
public class StoreFront {
    private ProductsData productsData;
    private DiscountsData discountsData;

    @Autowired
    public StoreFront(ProductsData productsData, DiscountsData discountsData) {
        this.productsData = productsData;
        this.discountsData = discountsData;
    }

    public Map<String, Item> getShopItems() {
        return productsData.getShopItems();
    }

    public Map<String, Offer>  getShopOffers() {
        return discountsData.getShopOffers();
    }

    public Item getShopItemForCart(String itemName, int quantity) {
        Item shopItem = productsData.getShopItems().get(itemName);
        Item newItem = new Item();

        newItem.setName(shopItem.getName());
        newItem.setPrice(shopItem.getPrice());
        newItem.setQuantity(quantity);

        return newItem;
    }

    public Offer getCheckoutOffer(String offerName) {
        Offer shopOffer = discountsData.getShopOffers().get(offerName);
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
