/*
 * Copyright 2011 Dukascopy Bank SA. All rights reserved.
 * DUKASCOPY PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.dukascopy.api.feed;

import com.dukascopy.api.DataType;
import com.dukascopy.api.Filter;
import com.dukascopy.api.Instrument;
import com.dukascopy.api.OfferSide;
import com.dukascopy.api.Period;
import com.dukascopy.api.PriceRange;
import com.dukascopy.api.ReversalAmount;
import com.dukascopy.api.TickBarSize;

/**
 * Implementation of {@link IFeedDescriptor}
 * 
 * @author Mark Vilkel
 */
public class FeedDescriptor implements IFeedDescriptor {
	
	private DataType dataType;
	private Instrument instrument;
	private OfferSide offerSide;
	private Period period;
	private PriceRange priceRange;
	private ReversalAmount reversalAmount;
	private TickBarSize tickBarSize;
	private Filter filter;
	
	/**
	 * Default constructor
	 */
	public FeedDescriptor() {
		
	}
	
	/**
	 * Copy constructor
	 * 
	 * @param feedDescriptor IFeedDescriptor
	 */
	public FeedDescriptor(IFeedDescriptor feedDescriptor) {
		this.dataType = feedDescriptor.getDataType();
		this.instrument = feedDescriptor.getInstrument();
		this.offerSide = feedDescriptor.getOfferSide();
		this.period = feedDescriptor.getPeriod();
		this.priceRange = feedDescriptor.getPriceRange();
		this.reversalAmount = feedDescriptor.getReversalAmount();
		this.tickBarSize = feedDescriptor.getTickBarSize();
		this.filter = feedDescriptor.getFilter();
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Instrument getInstrument() {
		return instrument;
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setInstrument(Instrument instrument) {
		this.instrument = instrument;
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public OfferSide getOfferSide() {
		return offerSide;
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setOfferSide(OfferSide offerSide) {
		this.offerSide = offerSide;
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public DataType getDataType() {
		return dataType;
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setDataType(DataType dataType) {
		this.dataType = dataType;
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Period getPeriod() {
		return period;
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setPeriod(Period period) {
		this.period = period;
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public PriceRange getPriceRange() {
		return priceRange;
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setPriceRange(PriceRange priceRange) {
		this.priceRange = priceRange;
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public ReversalAmount getReversalAmount() {
		return reversalAmount;
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setReversalAmount(ReversalAmount reversalAmount) {
		this.reversalAmount = reversalAmount;
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public TickBarSize getTickBarSize() {
		return tickBarSize;
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setTickBarSize(TickBarSize tickBarSize) {
		this.tickBarSize = tickBarSize;
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Filter getFilter() {
		return filter;
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setFilter(Filter filter) {
		this.filter = filter;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((dataType == null) ? 0 : dataType.hashCode());
		result = prime * result + ((filter == null) ? 0 : filter.hashCode());
		result = prime * result + ((instrument == null) ? 0 : instrument.hashCode());
		result = prime * result + ((offerSide == null) ? 0 : offerSide.hashCode());
		result = prime * result + ((period == null) ? 0 : period.hashCode());
		result = prime * result + ((priceRange == null) ? 0 : priceRange.hashCode());
		result = prime * result + ((reversalAmount == null) ? 0 : reversalAmount.hashCode());
		result = prime * result + ((tickBarSize == null) ? 0 : tickBarSize.hashCode());
		return result;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		FeedDescriptor other = (FeedDescriptor) obj;
		if (dataType == null) {
			if (other.dataType != null)
				return false;
		} else if (!dataType.equals(other.dataType))
			return false;
		if (filter == null) {
			if (other.filter != null)
				return false;
		} else if (!filter.equals(other.filter))
			return false;
		if (instrument == null) {
			if (other.instrument != null)
				return false;
		} else if (!instrument.equals(other.instrument))
			return false;
		if (offerSide == null) {
			if (other.offerSide != null)
				return false;
		} else if (!offerSide.equals(other.offerSide))
			return false;
		if (period == null) {
			if (other.period != null)
				return false;
		} else if (!period.equals(other.period))
			return false;
		if (priceRange == null) {
			if (other.priceRange != null)
				return false;
		} else if (!priceRange.equals(other.priceRange))
			return false;
		if (reversalAmount == null) {
			if (other.reversalAmount != null)
				return false;
		} else if (!reversalAmount.equals(other.reversalAmount))
			return false;
		if (tickBarSize == null) {
			if (other.tickBarSize != null)
				return false;
		} else if (!tickBarSize.equals(other.tickBarSize))
			return false;
		return true;
	}
	

}
