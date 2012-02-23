/*
 * Copyright 2011 Dukascopy Bank SA. All rights reserved.
 * DUKASCOPY PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.dukascopy.api.feed;

import com.dukascopy.api.Instrument;
import com.dukascopy.api.OfferSide;
import com.dukascopy.api.PriceRange;
import com.dukascopy.api.ReversalAmount;

/**
 * @author Mark Vilkel
 */
public interface IPointAndFigureFeedListener {
	
	/**
	 * 
	 *  The method is being called when next Point and Figure arrives
	 *  
	 * @param instrument
	 * @param offerSide
	 * @param priceRange
	 * @param reversalAmount
	 * @param bar
	 */
	public void onBar(
			Instrument instrument,
			OfferSide offerSide,
			PriceRange priceRange,
			ReversalAmount reversalAmount,
			IPointAndFigure bar
	);

}
