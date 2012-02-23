/*
 * Copyright 1998-2009 Dukascopy® (Suisse) SA. All rights reserved.
 * DUKASCOPY PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.dukascopy.api.system;

import com.dukascopy.api.Instrument;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Allows to set fixed overnight values for every Instrument
 *
 * @author Dmitry Shohov
 */
public class Overnights implements Serializable {
    private static final long serialVersionUID = 1L;
    
    public Map<Instrument, Double> longValues = new HashMap<Instrument, Double>();
    public Map<Instrument, Double> shortValues = new HashMap<Instrument, Double>();

    private boolean institutional;

    /**
     * Creates Overnights object with all values set to 0.0
     */
    public Overnights() {

    }

    /**
     * Creates Overnights object with default values (prices taken at 26.04.2010)
     *
     * @param institutional if true, then institutional prices will be filled in as defaults
     */
    public Overnights(boolean institutional) {
        this.institutional = institutional;
        if (institutional) {
            longValues.put(Instrument.AUDCAD, -1d);
            shortValues.put(Instrument.AUDCAD, -1.11d);

            longValues.put(Instrument.AUDCHF, -1.11d);
            shortValues.put(Instrument.AUDCHF, -1.23d);

            longValues.put(Instrument.AUDJPY, -0.98d);
            shortValues.put(Instrument.AUDJPY, -1.06d);

            longValues.put(Instrument.AUDNZD, -0.44d);
            shortValues.put(Instrument.AUDNZD, -0.69d);

            longValues.put(Instrument.AUDUSD, -0.98d);
            shortValues.put(Instrument.AUDUSD, -1.07d);

            longValues.put(Instrument.CADCHF, 0.00d);
            shortValues.put(Instrument.CADCHF, -0.11d);

            longValues.put(Instrument.CADJPY, 0.01d);
            shortValues.put(Instrument.CADJPY, -0.06d);

            longValues.put(Instrument.CHFJPY, 0.10d);
            shortValues.put(Instrument.CHFJPY, -0.05d);

            longValues.put(Instrument.EURAUD, 1.68d);
            shortValues.put(Instrument.EURAUD, 1.53d);

            longValues.put(Instrument.EURCAD, -0.19d);
            shortValues.put(Instrument.EURCAD, -0.35d);

            longValues.put(Instrument.EURCHF, -0.06d);
            shortValues.put(Instrument.EURCHF, -0.17d);

            longValues.put(Instrument.EURGBP, -0.06d);
            shortValues.put(Instrument.EURGBP, -0.18d);

            longValues.put(Instrument.EURJPY, -0.23d);
            shortValues.put(Instrument.EURJPY, -0.39d);

            longValues.put(Instrument.EURNOK, 3.07d);
            shortValues.put(Instrument.EURNOK, 2.48d);

            longValues.put(Instrument.EURNZD, 1.32d);
            shortValues.put(Instrument.EURNZD, 1.15d);

            longValues.put(Instrument.EURSEK, -1.31d);
            shortValues.put(Instrument.EURSEK, -2.63d);

            longValues.put(Instrument.EURUSD, 0.00d);
            shortValues.put(Instrument.EURUSD, -0.06d);

            longValues.put(Instrument.GBPAUD, 1.84d);
            shortValues.put(Instrument.GBPAUD, 1.66d);

            longValues.put(Instrument.GBPCAD, -0.08d);
            shortValues.put(Instrument.GBPCAD, -0.20d);

            longValues.put(Instrument.GBPCHF, 0.01d);
            shortValues.put(Instrument.GBPCHF, -0.44d);

            longValues.put(Instrument.GBPJPY, -0.03d);
            shortValues.put(Instrument.GBPJPY, -0.30d);

            longValues.put(Instrument.GBPNZD, 1.39d);
            shortValues.put(Instrument.GBPNZD, 1.12d);

            longValues.put(Instrument.GBPUSD, -0.06d);
            shortValues.put(Instrument.GBPUSD, -0.17d);

            longValues.put(Instrument.NZDCAD, -0.45d);
            shortValues.put(Instrument.NZDCAD, -0.52d);

            longValues.put(Instrument.NZDCHF, -0.50d);
            shortValues.put(Instrument.NZDCHF, -0.62d);

            longValues.put(Instrument.NZDJPY, -0.42d);
            shortValues.put(Instrument.NZDJPY, -0.49d);

            longValues.put(Instrument.NZDUSD, -0.39d);
            shortValues.put(Instrument.NZDUSD, -0.49d);

            longValues.put(Instrument.USDCAD, 0.06d);
            shortValues.put(Instrument.USDCAD, -0.06d);

            longValues.put(Instrument.USDCHF, 0.06d);
            shortValues.put(Instrument.USDCHF, -0.06d);

            longValues.put(Instrument.USDJPY, 0.01d);
            shortValues.put(Instrument.USDJPY, -0.09d);

            longValues.put(Instrument.USDNOK, 2.74d);
            shortValues.put(Instrument.USDNOK, 1.49d);

            longValues.put(Instrument.USDSEK, 0.46d);
            shortValues.put(Instrument.USDSEK, -0.46d);
        } else {
            longValues.put(Instrument.AUDCAD, -0.95d);
            shortValues.put(Instrument.AUDCAD, -1.16d);

            longValues.put(Instrument.AUDCHF, -1.05d);
            shortValues.put(Instrument.AUDCHF, -1.29d);

            longValues.put(Instrument.AUDJPY, -0.93d);
            shortValues.put(Instrument.AUDJPY, -1.11d);

            longValues.put(Instrument.AUDNZD, -0.41d);
            shortValues.put(Instrument.AUDNZD, -0.74d);

            longValues.put(Instrument.AUDUSD, -0.92d);
            shortValues.put(Instrument.AUDUSD, -1.12d);

            longValues.put(Instrument.CADCHF, 0.07d);
            shortValues.put(Instrument.CADCHF, -0.18d);

            longValues.put(Instrument.CADJPY, 0.07d);
            shortValues.put(Instrument.CADJPY, -0.12d);

            longValues.put(Instrument.CHFJPY, 0.14d);
            shortValues.put(Instrument.CHFJPY, -0.07d);

            longValues.put(Instrument.EURAUD, 1.76d);
            shortValues.put(Instrument.EURAUD, 1.44d);

            longValues.put(Instrument.EURCAD, 0.11d);
            shortValues.put(Instrument.EURCAD, -0.16d);

            longValues.put(Instrument.EURCHF, 0.02d);
            shortValues.put(Instrument.EURCHF, -0.26d);

            longValues.put(Instrument.EURGBP, 0.14d);
            shortValues.put(Instrument.EURGBP, -0.04d);

            longValues.put(Instrument.EURJPY, 0.06d);
            shortValues.put(Instrument.EURJPY, -0.17d);

            longValues.put(Instrument.EURNOK, 4.10d);
            shortValues.put(Instrument.EURNOK, 2.67d);

            longValues.put(Instrument.EURNZD, 1.39d);
            shortValues.put(Instrument.EURNZD, 0.99d);

            longValues.put(Instrument.EURSEK, 0.66d);
            shortValues.put(Instrument.EURSEK, -1.17d);

            longValues.put(Instrument.EURUSD, 0.08d);
            shortValues.put(Instrument.EURUSD, -0.14d);

            longValues.put(Instrument.GBPAUD, 1.95d);
            shortValues.put(Instrument.GBPAUD, 1.56d);

            longValues.put(Instrument.GBPCAD, 0.01d);
            shortValues.put(Instrument.GBPCAD, -0.29d);

            longValues.put(Instrument.GBPCHF, -0.02d);
            shortValues.put(Instrument.GBPCHF, -0.41d);

            longValues.put(Instrument.GBPJPY, -0.03d);
            shortValues.put(Instrument.GBPJPY, -0.29d);

            longValues.put(Instrument.GBPNZD, 1.47d);
            shortValues.put(Instrument.GBPNZD, 0.94d);

            longValues.put(Instrument.GBPUSD, 0.03d);
            shortValues.put(Instrument.GBPUSD, -0.27d);

            longValues.put(Instrument.NZDCAD, -0.39d);
            shortValues.put(Instrument.NZDCAD, -0.55d);

            longValues.put(Instrument.NZDCHF, -0.44d);
            shortValues.put(Instrument.NZDCHF, -0.65d);

            longValues.put(Instrument.NZDJPY, -0.37d);
            shortValues.put(Instrument.NZDJPY, -0.53d);

            longValues.put(Instrument.NZDUSD, -0.25d);
            shortValues.put(Instrument.NZDUSD, -0.69d);

            longValues.put(Instrument.USDCAD, 0.18d);
            shortValues.put(Instrument.USDCAD, -0.18d);

            longValues.put(Instrument.USDCHF, 0.05d);
            shortValues.put(Instrument.USDCHF, -0.17d);

            longValues.put(Instrument.USDJPY, 0.06d);
            shortValues.put(Instrument.USDJPY, -0.10d);

            longValues.put(Instrument.USDNOK, 3.23d);
            shortValues.put(Instrument.USDNOK, 2.09d);

            longValues.put(Instrument.USDSEK, 0.63d);
            shortValues.put(Instrument.USDSEK, -0.68d);
        }
    }

    /**
     * Sets overnights for instrument
     *
     * @param instrument instrument
     * @param longValue long value
     * @param shortValue short value
     */
    public void setOvernights(Instrument instrument, double longValue, double shortValue) {
        longValues.put(instrument, longValue);
        shortValues.put(instrument, shortValue);
    }

    /**
     * Returns Map with overnight long values for all instruments. Changing values in array will change values in Overnights class
     *
     * @return overnight values
     */
    public Map<Instrument, Double> getLongOvernights() {
        return longValues;
    }

    /**
     * Returns Map with overnight shoft values for all instruments. Changing values in array will change values in Overnights class
     *
     * @return overnight values
     */
    public Map<Instrument, Double> getShortOvernights() {
        return shortValues;
    }

    /**
     * Returns true if object contains institutional overnights
     *
     * @return type ov overnight values
     */
    public boolean isInstitutional() {
        return institutional;
    }
}
