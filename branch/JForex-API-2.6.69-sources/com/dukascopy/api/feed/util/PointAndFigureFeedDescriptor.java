/*
 * Copyright 2011 Dukascopy Bank SA. All rights reserved.
 * DUKASCOPY PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.dukascopy.api.feed.util;

import com.dukascopy.api.DataType;
import com.dukascopy.api.Instrument;
import com.dukascopy.api.OfferSide;
import com.dukascopy.api.PriceRange;
import com.dukascopy.api.ReversalAmount;
import com.dukascopy.api.feed.FeedDescriptor;

/**
 * @author Mark Vilkel
 * 
 * The class extends {@link FeedDescriptor}
 */
public class PointAndFigureFeedDescriptor extends FeedDescriptor {

	/**
	 * "Fast" constructor, which has minimal set of parameters for {@link FeedDescriptor} creation
	 * 
	 * @param instrument
	 * @param boxSize
	 * @param reversalAmount
	 * @param offerSide
	 */
	public PointAndFigureFeedDescriptor(
			Instrument instrument,
			PriceRange boxSize,
			ReversalAmount reversalAmount,
			OfferSide offerSide
	) {
		setDataType(DataType.POINT_AND_FIGURE);
		setInstrument(instrument);
		setPriceRange(boxSize);
		setReversalAmount(reversalAmount);
		setOfferSide(offerSide);
	}

}
