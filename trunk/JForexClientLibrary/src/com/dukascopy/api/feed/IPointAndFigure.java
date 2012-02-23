/*
 * Copyright 2011 Dukascopy Bank SA. All rights reserved.
 * DUKASCOPY PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.dukascopy.api.feed;

/**
 * @author Mark Vilkel
 */
public interface IPointAndFigure extends IPriceAggregationBar {
	/**
	 * 
	 * Returns TRUE - if bar is rising or X
	 * Returns FALSE - if bar is failing or O
	 * 
	 * @return
	 */
	Boolean isRising();

}
