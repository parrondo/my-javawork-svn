package com.dukascopy.charts.data.datacache.feed;

import com.dukascopy.api.Instrument;
import com.dukascopy.api.OfferSide;
import com.dukascopy.api.Period;
import com.dukascopy.charts.data.datacache.CandleData;
import com.dukascopy.charts.data.datacache.Data;
import com.dukascopy.charts.data.datacache.TickData;
import com.dukascopy.transport.common.msg.request.FeedCommission;
import java.util.List;
import java.util.Map;

public abstract interface IFeedCommissionManager
{
  public abstract void setupFeedCommissionsFromAuthServer(List<String[]> paramList);

  public abstract void setupFeedCommissions(List<IInstrumentFeedCommissionInfo> paramList);

  public abstract void addFeedCommissions(Map<String, FeedCommission> paramMap);

  public abstract void addFeedCommissions(Map<String, FeedCommission> paramMap, Long paramLong);

  public abstract void addFeedCommissions(Map<String, FeedCommission> paramMap, long paramLong);

  public abstract void addFeedCommissions(List<IInstrumentFeedCommissionInfo> paramList);

  public abstract void clear();

  public abstract boolean hasCommission(Instrument paramInstrument);

  public abstract double getFeedCommission(Instrument paramInstrument, long paramLong);

  public abstract double getPriceWithCommission(Instrument paramInstrument, OfferSide paramOfferSide, double paramDouble, long paramLong);

  public abstract CandleData applyFeedCommissionToCandle(Instrument paramInstrument, OfferSide paramOfferSide, CandleData paramCandleData);

  public abstract TickData applyFeedCommissionToTick(Instrument paramInstrument, TickData paramTickData);

  public abstract Data[] applyFeedCommissionToData(Instrument paramInstrument, Period paramPeriod, OfferSide paramOfferSide, Data[] paramArrayOfData);
}

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.charts.data.datacache.feed.IFeedCommissionManager
 * JD-Core Version:    0.6.0
 */