package org.bjss.store.model;

public class Offer {
	private String name;
	private int type;
	private int quantity;
	private String item;
	private String requiredItem;
	private int requiredQuantityMin;
	private int applicableCount;
	private String expiryOn;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public int getQuantity() {
		return quantity;
	}
	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}
	public String getItem() {
		return item;
	}
	public void setItem(String item) {
		this.item = item;
	}
	public String getRequiredItem() {
		return requiredItem;
	}
	public void setRequiredItem(String requiredItem) {
		this.requiredItem = requiredItem;
	}
	public int getRequiredQuantityMin() {
		return requiredQuantityMin;
	}
	public void setRequiredQuantityMin(int requiredQuantityMin) {
		this.requiredQuantityMin = requiredQuantityMin;
	}
	public int getApplicableCount() {
		return applicableCount;
	}
	public void setApplicableCount(int applicableCount) {
		this.applicableCount = applicableCount;
	}
	public String getExpiryOn() {
		return expiryOn;
	}
	public void setExpiryOn(String expiryOn) {
		this.expiryOn = expiryOn;
	}
}
