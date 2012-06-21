/*
 * Copyright 2009 Dukascopy® (Suisse) SA. All rights reserved.
 * DUKASCOPY PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.dukascopy.api;

import java.util.Collection;
import java.util.Currency;
import java.util.HashSet;
import java.util.Set;

/**
 * Defines all currency pairs traded by Dukascopy
 */
public enum Instrument {
	AUDJPY(Currency.getInstance("AUD"), Currency.getInstance("JPY"), 0.01, 2),
    AUDCAD(Currency.getInstance("AUD"), Currency.getInstance("CAD"), 0.0001, 4),
    AUDCHF(Currency.getInstance("AUD"), Currency.getInstance("CHF"), 0.0001, 4),
    AUDNZD(Currency.getInstance("AUD"), Currency.getInstance("NZD"), 0.0001, 4),
    AUDSGD(Currency.getInstance("AUD"), Currency.getInstance("SGD"), 0.0001, 4),
    AUDUSD(Currency.getInstance("AUD"), Currency.getInstance("USD"), 0.0001, 4),
    CADCHF(Currency.getInstance("CAD"), Currency.getInstance("CHF"), 0.0001, 4),
    CADHKD(Currency.getInstance("CAD"), Currency.getInstance("HKD"), 0.0001, 4),
    CADJPY(Currency.getInstance("CAD"), Currency.getInstance("JPY"), 0.01, 2),
    CHFJPY(Currency.getInstance("CHF"), Currency.getInstance("JPY"), 0.01, 2),
    CHFPLN(Currency.getInstance("CHF"), Currency.getInstance("PLN"), 0.0001, 4),
    CHFSGD(Currency.getInstance("CHF"), Currency.getInstance("SGD"), 0.0001, 4),
    EURAUD(Currency.getInstance("EUR"), Currency.getInstance("AUD"), 0.0001, 4),
    EURBRL(Currency.getInstance("EUR"), Currency.getInstance("BRL"), 0.0001, 4),
    EURCAD(Currency.getInstance("EUR"), Currency.getInstance("CAD"), 0.0001, 4),
    EURCHF(Currency.getInstance("EUR"), Currency.getInstance("CHF"), 0.0001, 4),
    EURDKK(Currency.getInstance("EUR"), Currency.getInstance("DKK"), 0.0001, 4),
    EURGBP(Currency.getInstance("EUR"), Currency.getInstance("GBP"), 0.0001, 4),
    EURHKD(Currency.getInstance("EUR"), Currency.getInstance("HKD"), 0.0001, 4),
    EURHUF(Currency.getInstance("EUR"), Currency.getInstance("HUF"), 0.01, 2),
    EURJPY(Currency.getInstance("EUR"), Currency.getInstance("JPY"), 0.01, 2),
    EURMXN(Currency.getInstance("EUR"), Currency.getInstance("MXN"), 0.0001, 4),
    EURNOK(Currency.getInstance("EUR"), Currency.getInstance("NOK"), 0.0001, 4),
    EURNZD(Currency.getInstance("EUR"), Currency.getInstance("NZD"), 0.0001, 4),
    EURPLN(Currency.getInstance("EUR"), Currency.getInstance("PLN"), 0.0001, 4),
    EURRUB(Currency.getInstance("EUR"), Currency.getInstance("RUB"), 0.0001, 4),
    EURSEK(Currency.getInstance("EUR"), Currency.getInstance("SEK"), 0.0001, 4),
    EURSGD(Currency.getInstance("EUR"), Currency.getInstance("SGD"), 0.0001, 4),
    EURTRY(Currency.getInstance("EUR"), Currency.getInstance("TRY"), 0.0001, 4),
    EURUSD(Currency.getInstance("EUR"), Currency.getInstance("USD"), 0.0001, 4),
    EURZAR(Currency.getInstance("EUR"), Currency.getInstance("ZAR"), 0.0001, 4),
    GBPAUD(Currency.getInstance("GBP"), Currency.getInstance("AUD"), 0.0001, 4),
    GBPCAD(Currency.getInstance("GBP"), Currency.getInstance("CAD"), 0.0001, 4),
    GBPCHF(Currency.getInstance("GBP"), Currency.getInstance("CHF"), 0.0001, 4),
    GBPJPY(Currency.getInstance("GBP"), Currency.getInstance("JPY"), 0.01, 2),
    GBPNZD(Currency.getInstance("GBP"), Currency.getInstance("NZD"), 0.0001, 4),
    GBPUSD(Currency.getInstance("GBP"), Currency.getInstance("USD"), 0.0001, 4),
    HKDJPY(Currency.getInstance("HKD"), Currency.getInstance("JPY"), 0.0001, 4),
    HUFJPY(Currency.getInstance("HUF"), Currency.getInstance("JPY"), 0.0001, 4),
    MXNJPY(Currency.getInstance("MXN"), Currency.getInstance("JPY"), 0.0001, 4),
    NZDCAD(Currency.getInstance("NZD"), Currency.getInstance("CAD"), 0.0001, 4),
    NZDCHF(Currency.getInstance("NZD"), Currency.getInstance("CHF"), 0.0001, 4),
    NZDJPY(Currency.getInstance("NZD"), Currency.getInstance("JPY"), 0.01, 2),
    NZDSGD(Currency.getInstance("NZD"), Currency.getInstance("SGD"), 0.0001, 4),
    NZDUSD(Currency.getInstance("NZD"), Currency.getInstance("USD"), 0.0001, 4),
    SGDJPY(Currency.getInstance("SGD"), Currency.getInstance("JPY"), 0.01, 2),    
    USDBRL(Currency.getInstance("USD"), Currency.getInstance("BRL"), 0.0001, 4),
    USDCAD(Currency.getInstance("USD"), Currency.getInstance("CAD"), 0.0001, 4),
    USDCHF(Currency.getInstance("USD"), Currency.getInstance("CHF"), 0.0001, 4),
    USDCZK(Currency.getInstance("USD"), Currency.getInstance("CZK"), 0.01, 2),
    USDDKK(Currency.getInstance("USD"), Currency.getInstance("DKK"), 0.0001, 4),
    USDHKD(Currency.getInstance("USD"), Currency.getInstance("HKD"), 0.0001, 4),
    USDHUF(Currency.getInstance("USD"), Currency.getInstance("HUF"), 0.01, 2),
    USDJPY(Currency.getInstance("USD"), Currency.getInstance("JPY"), 0.01, 2),
    USDMXN(Currency.getInstance("USD"), Currency.getInstance("MXN"), 0.0001, 4),
    USDNOK(Currency.getInstance("USD"), Currency.getInstance("NOK"), 0.0001, 4),
    USDPLN(Currency.getInstance("USD"), Currency.getInstance("PLN"), 0.0001, 4),
    USDRON(Currency.getInstance("USD"), Currency.getInstance("RON"), 0.0001, 4),
    USDRUB(Currency.getInstance("USD"), Currency.getInstance("RUB"), 0.0001, 4),
    USDSEK(Currency.getInstance("USD"), Currency.getInstance("SEK"), 0.0001, 4),
    USDSGD(Currency.getInstance("USD"), Currency.getInstance("SGD"), 0.0001, 4),
    USDTRY(Currency.getInstance("USD"), Currency.getInstance("TRY"), 0.0001, 4),
    USDZAR(Currency.getInstance("USD"), Currency.getInstance("ZAR"), 0.0001, 4),
    XAGUSD(Currency.getInstance("XAG"), Currency.getInstance("USD"), 0.01, 2),
    XAUUSD(Currency.getInstance("XAU"), Currency.getInstance("USD"), 0.01, 2),
    ZARJPY(Currency.getInstance("ZAR"), Currency.getInstance("JPY"), 0.0001, 4);
    
    private Currency primaryCurrency;
    private Currency secondaryCurrency;
    private double pipValue;
    private int pipScale;
    
    private Instrument(Currency primaryCurrency, Currency secondaryCurrency, double pipValue, int pipScale) {
        this.primaryCurrency = primaryCurrency;
        this.secondaryCurrency = secondaryCurrency;
        this.pipValue = pipValue;
        this.pipScale = pipScale;
    }

    @Override
    public String toString() {
        return name().substring(0, 3) + getPairsSeparator() + name().substring(3, 6);
    }

    /**
     * Returns currency separator
     * 
     * @return currency separator
     */
    public static String getPairsSeparator() {
        return "/";
    }

    /**
     * Returns corresponding instrument for string in "CUR1/CUR2" format
     * 
     * @param instrumentAsString string in "CUR1/CUR2" format
     * @return corresponding instrument or null if no instrument was found for specified string
     */
    public static Instrument fromString(String instrumentAsString) {
        for (Instrument instrument : values()) {
            if (instrumentAsString.equals(instrument.getPrimaryCurrency().getCurrencyCode() + "/" + instrument.getSecondaryCurrency().getCurrencyCode())) {
                return instrument;
            }
        }
        return null;
    }
    
    /**
     * Returns corresponding inverted instrument for string in "CUR2/CUR1" format, e.g., string USD/EUR returns instrument EUR/USD, but string EUR/USD returns null
     * 
     * @param instrumentAsString string in "CUR2/CUR1" format
     * @return corresponding instrument or null if no instrument was found for specified string
     */
    public static Instrument fromInvertedString(String instrumentAsString) {
        for (Instrument instrument : values()) {
            if (instrumentAsString.equals(instrument.getSecondaryCurrency().getCurrencyCode() + "/" + instrument.getPrimaryCurrency().getCurrencyCode())) {
                return instrument;
            }
        }
        return null;
    }
    
    /**
     * Returns true if instrument is inverted (such as USD/EUR or JPY/USD)
     * 
     * @param instrumentStr instrument string representation
     * @return true if inverted, false if not inverted or not instrument
     */
    public static boolean isInverted(String instrumentStr) {
        return fromString(instrumentStr) == null && instrumentStr.length() == 7 && fromString(instrumentStr.substring(4, 7) + "/" + instrumentStr.substring(0, 3)) != null;
    }

    /**
     * Returns set of strings, which are instruments in "CUR1/CUR2" format
     * 
     * @param instruments collection of instruments
     * @return set of strings in "CUR1/CUR2" format
     */
    public static Set<String> toStringSet(Collection<Instrument> instruments) {
        Set<String> set = new HashSet<String>();
        if (instruments != null && !instruments.isEmpty()){
            for (Instrument instrument : instruments) {
                set.add(instrument.toString());
            }
        }
        return set;
    }
    
    public static Set<Instrument> fromStringSet(Set<String> instrumentsAsString){
        Set<Instrument> instruments = new HashSet<Instrument>();
        if (instrumentsAsString != null && !instrumentsAsString.isEmpty()){
            for (String instrumentAsString : instrumentsAsString){
                try {
                    Instrument instrument = fromString(instrumentAsString);
                    if (instrument == null){
                        instrument = fromInvertedString(instrumentAsString);
                    }
                    if (instrument == null) {
                        instrument = valueOf(instrumentAsString);
                    }
                    if (instrument != null){
                        instruments.add(instrument); 
                    }
                } catch (Throwable t) {
                    // unsupported instrument arrived
                }
            }
        }
        return instruments;
    }

    /**
     * Returns true if specified instrument is one of the traded instruments
     * 
     * @param instrumentString instrument to check
     * @return true if corresponding instrument was found, false otherwise
     */
    public static boolean contains(String instrumentString) {
        for (Instrument instr : Instrument.values()) {
            if (instr.toString().equals(instrumentString)) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Returns primary currency of this currency pair
     * 
     * @return primary currency
     */
    public Currency getPrimaryCurrency() {
        return primaryCurrency;
    }
    
    /**
     * Returns secondary currency of this currency pair
     * 
     * @return secondary currency
     */
    public Currency getSecondaryCurrency() {
        return secondaryCurrency;
    }
    
    /**
     * Returns value of one pip for this currency pair
     * 
     * @return pip
     */
    public double getPipValue() {
        return pipValue;
    }
    
    public int getPipScale() {
        return pipScale;
    }
    
   public boolean equals(String symbol) {
	if (symbol == null) {
		return false;
	} else {
		return toString().equals(symbol);
	}
}
}
