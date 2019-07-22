package org.bjss.store.model;

import java.math.BigDecimal;
import java.math.MathContext;

public class CheckoutOffer extends Offer {
	private BigDecimal offerAmount = new BigDecimal(0, new MathContext(2));
	private int offerAppliedQuantity;

	public BigDecimal getOfferAmount() {
			return offerAmount;
		}

	public void setOfferAmount(BigDecimal offerAmount) {
			this.offerAmount = offerAmount;
		}

	public int getOfferAppliedQuantity() {
			return offerAppliedQuantity;
		}

	public void setOfferAppliedQuantity(int offerAppliedQuantity) {
			this.offerAppliedQuantity = offerAppliedQuantity;
		}
}
