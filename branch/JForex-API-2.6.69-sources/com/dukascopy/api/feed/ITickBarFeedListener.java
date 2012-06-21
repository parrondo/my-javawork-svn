/*
 * Copyright 2011 Dukascopy Bank SA. All rights reserved.
 * DUKASCOPY PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.dukascopy.api.feed;

import com.dukascopy.api.Instrument;
import com.dukascopy.api.OfferSide;
import com.dukascopy.api.TickBarSize;

/**
 * @author Mark Vilkel
 */
public interface ITickBarFeedListener {
	
	/**
	 *
	 * The method is being called when next Tick Bar arrives
	 * 
	 * @param instrument
	 * @param offerSide
	 * @param tickBarSize
	 * @param bar
	 */
	public void onBar(
			Instrument instrument,
			OfferSide offerSide,
			TickBarSize tickBarSize,
			ITickBar bar
	);

}
