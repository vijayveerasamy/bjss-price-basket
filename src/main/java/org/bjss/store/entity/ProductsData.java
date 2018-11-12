package org.bjss.store.entity;

import org.bjss.store.model.Item;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import java.util.HashMap;

@Repository
@PropertySource("classpath:products.properties")
@ConfigurationProperties("products")
public class ProductsData {
 
    private List<Item> items;
    
	private Map<String, Item> shopItems;//instance bound
    
    @Override
    public String toString() {
        return "Products{" +
                ", items=" + items +

                "}";
    }

	public List<Item> getItems() {
		return items;
	}

	public void setItems(List<Item> items) {
		this.items = items;	
	}
	
	@PostConstruct
	public void setShopItems() {
		shopItems = new HashMap<>();
		
		for (Item item : this.items) {
			shopItems.put(item.getName(), item);
		}
	}

	public Map<String, Item> getShopItems() {
		return shopItems;
	}
	
    public Item getShopItemForCart(String itemName, int quantity) { 
    	Item shopItem = (Item) shopItems.get(itemName);
    	Item newItem = new Item();
    	
    	newItem.setName(shopItem.getName());
    	newItem.setPrice(shopItem.getPrice());
    	newItem.setQuantity(quantity);
    	
    	return newItem;
    } 
}