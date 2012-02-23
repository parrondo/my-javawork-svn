package com.dukascopy.api;

import java.util.Currency;


public interface JFUtils {

	/**
	 * Converts the amount from one instrument to another with precision of 0.1 instrumentTo pips. 
	 * For conversion median price (between {@link OfferSide#ASK} and {@link OfferSide#BID}) gets used.
	 * 
	 * @param instrumentFrom the instrument from which the amount to be converted
	 * @param instrumentTo the instrument to which the amount to be converted
	 * @param amount the amount in instrumentFrom 
	 * 
	 * @throws JFException if instrumentFrom or instrumentTo are null or not subscribed, if amount is 0 or less, if there is no active subscribed
	 * inter-instrument that could be used in conversion (e.g. for conversion between NZD/CHF and GBP/USD none of the instruments
	 * GBP/NZD, NZD/USD, GBP/CHF, USD/CHF are subscribed and active)
	 */
	public double convert(Instrument instrumentFrom, Instrument instrumentTo, double amount) throws JFException;

	/**
	 * Converts the amount from one instrument to another. 
	 * For conversion median price (between {@link OfferSide#ASK} and {@link OfferSide#BID}) gets used.
	 * 
	 * @param instrumentFrom the instrument from which the amount to be converted
	 * @param instrumentTo the instrument to which the amount to be converted
	 * @param amount the amount in instrumentFrom 
	 * @param decimalPlaces decimal places of the returned result
	 * 
	 * @throws JFException if instrumentFrom or instrumentTo are null or not subscribed, if amount is 0 or less, if there is no active subscribed
	 * inter-instrument that could be used in conversion (e.g. for conversion between NZD/CHF and GBP/USD none of the instruments
	 * GBP/NZD, NZD/USD, GBP/CHF, USD/CHF are subscribed and active)
	 */
	public double convert(Instrument instrumentFrom, Instrument instrumentTo, double amount, int decimalPlaces) throws JFException;
	
	/**
	 * Converts the amount from one instrument to another.
	 * 
	 * @param instrumentFrom the instrument from which the amount to be converted
	 * @param instrumentTo the instrument to which the amount to be converted
	 * @param amount the amount in instrumentFrom 
	 * @param decimalPlaces decimal places of the returned result
	 * @param offerSide the price used for conversion - BID or ASK. If the value is null, the median price between BID and ASK is used
	 * 
	 * @throws JFException if instrumentFrom or instrumentTo are null or not subscribed, if amount is 0 or less, if there is no active subscribed
	 * inter-instrument that could be used in conversion (e.g. for conversion between NZD/CHF and GBP/USD none of the instruments
	 * GBP/NZD, NZD/USD, GBP/CHF, USD/CHF are subscribed and active)
	 */
	public double convert(Instrument instrumentFrom, Instrument instrumentTo, double amount, int decimalPlaces, OfferSide offerSide) throws JFException;
	

	/**
	 * Converts the cost of one pip for particular instrument to specified currency.
	 * For conversion median price (between {@link OfferSide#ASK} and {@link OfferSide#BID}) gets used.
	 *  
	 * @param instrument {@link Instrument} which pip value will be converted
	 * @param currency the target currency of conversion
	 * @return the price of {@link Instrument}'s pip value in specified currency.<br/>
	 * <b>NOTE:</b> the result price is unrounded, i.e. result has unlimited precision (as many digits as are required)
	 * @throws JFException
	 */
	public double convertPipToCurrency(Instrument instrument, Currency currency) throws JFException;

	/**
	 * Converts the cost of one pip for particular instrument to specified currency 
	 * @param instrument {@link Instrument} which pip value will be converted
	 * @param currency the target currency of conversion
	 * @param offerSide the price used for conversion - BID or ASK. If the value is null, the median price between BID and ASK is used
	 * @return the price of {@link Instrument}'s pip value in specified currency.<br/>
	 * <b>NOTE:</b> the result price is unrounded, i.e. result has unlimited precision (as many digits as are required)
	 * @throws JFException
	 */
	public double convertPipToCurrency(Instrument instrument, Currency currency, OfferSide offerSide) throws JFException;
}
