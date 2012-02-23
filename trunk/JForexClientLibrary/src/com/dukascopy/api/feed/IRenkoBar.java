/*
 * Copyright 2011 Dukascopy Bank SA. All rights reserved.
 * DUKASCOPY PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.dukascopy.api.feed;

/**
 * Class for Renko bar representation 
 * 
 * @author Mark Vilkel
 */
public interface IRenkoBar extends IPriceAggregationBar {

	/**
	 * Theory says, that prices may exceed the top (or bottom) of the current brick.
	 * Again, new bricks are only added when prices completely "fill" the brick.
	 * For example, for a 5-point chart, if prices rise from 98 to 102,
	 * the hollow brick that goes from 95 to 100 is added to the chart BUT the hollow brick that goes from 100 to 105 is NOT DRAWN.
	 * The Renko chart will give the impression that prices stopped at 100.
	 * 
	 * So this method returns in progress bar which is not drawn until fully constructed, but could be extracted.
	 * Note, that in progress bar is available only for the last fully constructed bar,
	 * for all others historical bars null will be returned.
	 * 
	 * @return
	 */
	IRenkoBar getInProgressBar();

}
