/*
 * Copyright 2011 Dukascopy Bank SA. All rights reserved.
 * DUKASCOPY PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.dukascopy.api.feed;

import com.dukascopy.api.Instrument;
import com.dukascopy.api.OfferSide;
import com.dukascopy.api.PriceRange;

/**
 * @author Mark Vilkel
 */
public interface IRangeBarFeedListener {
	
	/**
	 * 
	 * The method is being called when next Range Bar arrives
	 * 
	 * @param instrument
	 * @param offerSide
	 * @param priceRange
	 * @param bar
	 */
	public void onBar(
			Instrument instrument,
			OfferSide offerSide,
			PriceRange priceRange,
			IRangeBar bar
	);

}
