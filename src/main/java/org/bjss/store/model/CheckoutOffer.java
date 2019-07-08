package org.bjss.store.model;

public class CheckoutOffer extends Offer {
	private int offerAmount;
	private int offerAppliedQuantity;

	public int getOfferAmount() {
			return offerAmount;
		}

	public void setOfferAmount(int offerAmount) {
			this.offerAmount = offerAmount;
		}

	public int getOfferAppliedQuantity() {
			return offerAppliedQuantity;
		}

	public void setOfferAppliedQuantity(int offerAppliedQuantity) {
			this.offerAppliedQuantity = offerAppliedQuantity;
		}
}
