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
public interface IRenkoBarFeedListener {

	/**
	 * The method is being called when next Renko Bar arrives
	 * 
	 * @param instrument
	 * @param offerSide
	 * @param brickSize
	 * @param bar
	 */
	public void onBar(
			Instrument instrument,
			OfferSide offerSide,
			PriceRange brickSize,
			IRenkoBar bar
	);

}
