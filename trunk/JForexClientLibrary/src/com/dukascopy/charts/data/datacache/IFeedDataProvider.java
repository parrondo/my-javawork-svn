package com.dukascopy.charts.data.datacache;

import com.dukascopy.api.Filter;
import com.dukascopy.api.Instrument;
import com.dukascopy.api.OfferSide;
import com.dukascopy.api.Period;
import com.dukascopy.api.PriceRange;
import com.dukascopy.api.ReversalAmount;
import com.dukascopy.api.TickBarSize;
import com.dukascopy.charts.data.datacache.dhl.IDailyHighLowManager;
import com.dukascopy.charts.data.datacache.feed.IFeedCommissionManager;
import com.dukascopy.charts.data.datacache.filtering.IFilterManager;
import com.dukascopy.charts.data.datacache.intraperiod.IIntraperiodBarsGenerator;
import com.dukascopy.charts.data.datacache.listener.DataFeedServerConnectionListener;
import com.dukascopy.charts.data.datacache.priceaggregation.dataprovider.IPriceAggregationDataProvider;
import com.dukascopy.dds2.greed.util.IOrderUtils;
import java.util.List;

public abstract interface IFeedDataProvider
{
  public static final int VERSION = 5;

  public abstract long getTimeOfFirstCandle(Instrument paramInstrument, Period paramPeriod);

  public abstract long getTimeOfFirstTick(Instrument paramInstrument);

  public abstract long getTimeOfFirstBar(Instrument paramInstrument, PriceRange paramPriceRange);

  public abstract long getTimeOfFirstBar(Instrument paramInstrument, PriceRange paramPriceRange, ReversalAmount paramReversalAmount);

  public abstract long getTimeOfFirstBar(Instrument paramInstrument, TickBarSize paramTickBarSize);

  public abstract void subscribeToLiveFeed(Instrument paramInstrument, LiveFeedListener paramLiveFeedListener);

  public abstract void unsubscribeFromLiveFeed(Instrument paramInstrument, LiveFeedListener paramLiveFeedListener);

  public abstract long getCurrentTime(Instrument paramInstrument);

  public abstract long getLastTickTime(Instrument paramInstrument);

  public abstract TickData getLastTick(Instrument paramInstrument);

  public abstract long getCurrentTime();

  public abstract void subscribeToPeriodNotifications(Instrument paramInstrument, Period paramPeriod, OfferSide paramOfferSide, LiveFeedListener paramLiveFeedListener)
    throws NoDataForPeriodException, DataCacheException;

  public abstract void unsubscribeFromPeriodNotifications(Instrument paramInstrument, Period paramPeriod, OfferSide paramOfferSide, LiveFeedListener paramLiveFeedListener);

  public abstract void subscribeToAllPeriodNotifications(Instrument paramInstrument, OfferSide paramOfferSide, LiveFeedListener paramLiveFeedListener);

  public abstract void unsubscribeFromAllPeriodNotifications(Instrument paramInstrument, OfferSide paramOfferSide, LiveFeedListener paramLiveFeedListener);

  public abstract void subscribeToAllCandlePeriods(LiveCandleListener paramLiveCandleListener);

  public abstract void unsubscribeFromAllCandlePeriods(LiveCandleListener paramLiveCandleListener);

  public abstract void subscribeToOrdersNotifications(Instrument paramInstrument, OrdersListener paramOrdersListener);

  public abstract void unsubscribeFromOrdersNotifications(Instrument paramInstrument, OrdersListener paramOrdersListener);

  public abstract void addInstrumentSubscriptionListener(InstrumentSubscriptionListener paramInstrumentSubscriptionListener);

  public abstract void removeInstrumentSubscriptionListener(InstrumentSubscriptionListener paramInstrumentSubscriptionListener);

  public abstract void addInProgressCandleListener(Instrument paramInstrument, Period paramPeriod, OfferSide paramOfferSide, LiveFeedListener paramLiveFeedListener);

  public abstract void removeInProgressCandleListener(Instrument paramInstrument, Period paramPeriod, OfferSide paramOfferSide, LiveFeedListener paramLiveFeedListener);

  public abstract void addCacheDataUpdatedListener(Instrument paramInstrument, CacheDataUpdatedListener paramCacheDataUpdatedListener);

  public abstract void removeCacheDataUpdatedListener(Instrument paramInstrument, CacheDataUpdatedListener paramCacheDataUpdatedListener);

  public abstract CandleData getInProgressCandle(Instrument paramInstrument, Period paramPeriod, OfferSide paramOfferSide);

  public abstract CandleData getInProgressCandleBlocking(Instrument paramInstrument, Period paramPeriod, OfferSide paramOfferSide)
    throws DataCacheException;

  public abstract List<Instrument> getInstrumentsCurrentlySubscribed();

  public abstract boolean isSubscribedToInstrument(Instrument paramInstrument);

  public abstract void loadTicksData(Instrument paramInstrument, long paramLong1, long paramLong2, LiveFeedListener paramLiveFeedListener, LoadingProgressListener paramLoadingProgressListener)
    throws DataCacheException;

  public abstract void loadTicksDataSynched(Instrument paramInstrument, long paramLong1, long paramLong2, LiveFeedListener paramLiveFeedListener, LoadingProgressListener paramLoadingProgressListener)
    throws DataCacheException;

  public abstract void loadTicksDataBlockingSynched(Instrument paramInstrument, long paramLong1, long paramLong2, LiveFeedListener paramLiveFeedListener, LoadingProgressListener paramLoadingProgressListener)
    throws DataCacheException;

  public abstract void loadTicksDataBefore(Instrument paramInstrument, int paramInt, long paramLong, Filter paramFilter, LiveFeedListener paramLiveFeedListener, LoadingProgressListener paramLoadingProgressListener)
    throws DataCacheException;

  public abstract void loadTicksDataAfter(Instrument paramInstrument, int paramInt, long paramLong, Filter paramFilter, LiveFeedListener paramLiveFeedListener, LoadingProgressListener paramLoadingProgressListener)
    throws DataCacheException;

  public abstract void loadTicksDataBeforeAfter(Instrument paramInstrument, int paramInt1, int paramInt2, long paramLong, Filter paramFilter, LiveFeedListener paramLiveFeedListener, LoadingProgressListener paramLoadingProgressListener)
    throws DataCacheException;

  public abstract void loadTicksDataBeforeSynched(Instrument paramInstrument, int paramInt, long paramLong, Filter paramFilter, LiveFeedListener paramLiveFeedListener, LoadingProgressListener paramLoadingProgressListener)
    throws DataCacheException;

  public abstract void loadTicksDataAfterSynched(Instrument paramInstrument, int paramInt, long paramLong, Filter paramFilter, LiveFeedListener paramLiveFeedListener, LoadingProgressListener paramLoadingProgressListener)
    throws DataCacheException;

  public abstract void loadTicksDataBeforeAfterSynched(Instrument paramInstrument, int paramInt1, int paramInt2, long paramLong, Filter paramFilter, LiveFeedListener paramLiveFeedListener, LoadingProgressListener paramLoadingProgressListener)
    throws DataCacheException;

  public abstract void loadLastAvailableTicksDataSynched(Instrument paramInstrument, long paramLong1, long paramLong2, LiveFeedListener paramLiveFeedListener, LoadingProgressListener paramLoadingProgressListener)
    throws DataCacheException;

  public abstract void loadLastAvailableNumberOfTicksDataSynched(Instrument paramInstrument, int paramInt, long paramLong, Filter paramFilter, LiveFeedListener paramLiveFeedListener, LoadingProgressListener paramLoadingProgressListener)
    throws DataCacheException;

  public abstract void loadCandlesData(Instrument paramInstrument, Period paramPeriod, OfferSide paramOfferSide, long paramLong1, long paramLong2, LiveFeedListener paramLiveFeedListener, LoadingProgressListener paramLoadingProgressListener)
    throws NoDataForPeriodException, DataCacheException;

  public abstract void loadCandlesDataSynched(Instrument paramInstrument, Period paramPeriod, OfferSide paramOfferSide, long paramLong1, long paramLong2, LiveFeedListener paramLiveFeedListener, LoadingProgressListener paramLoadingProgressListener)
    throws NoDataForPeriodException, DataCacheException;

  public abstract void loadCandlesDataBlockingSynched(Instrument paramInstrument, Period paramPeriod, OfferSide paramOfferSide, long paramLong1, long paramLong2, LiveFeedListener paramLiveFeedListener, LoadingProgressListener paramLoadingProgressListener)
    throws NoDataForPeriodException, DataCacheException;

  public abstract void loadInProgressCandleData(Instrument paramInstrument, long paramLong, LiveFeedListener paramLiveFeedListener, LoadingProgressListener paramLoadingProgressListener)
    throws NoDataForPeriodException, DataCacheException;

  public abstract void loadInProgressCandleDataSynched(Instrument paramInstrument, long paramLong, LiveFeedListener paramLiveFeedListener, LoadingProgressListener paramLoadingProgressListener)
    throws NoDataForPeriodException, DataCacheException;

  public abstract void loadCandlesDataBefore(Instrument paramInstrument, Period paramPeriod, OfferSide paramOfferSide, int paramInt, long paramLong, Filter paramFilter, LiveFeedListener paramLiveFeedListener, LoadingProgressListener paramLoadingProgressListener)
    throws NoDataForPeriodException, DataCacheException;

  public abstract void loadCandlesDataAfter(Instrument paramInstrument, Period paramPeriod, OfferSide paramOfferSide, int paramInt, long paramLong, Filter paramFilter, LiveFeedListener paramLiveFeedListener, LoadingProgressListener paramLoadingProgressListener)
    throws NoDataForPeriodException, DataCacheException;

  public abstract void loadCandlesDataBeforeAfter(Instrument paramInstrument, Period paramPeriod, OfferSide paramOfferSide, int paramInt1, int paramInt2, long paramLong, Filter paramFilter, LiveFeedListener paramLiveFeedListener, LoadingProgressListener paramLoadingProgressListener)
    throws NoDataForPeriodException, DataCacheException;

  public abstract void loadCandlesDataBeforeSynched(Instrument paramInstrument, Period paramPeriod, OfferSide paramOfferSide, int paramInt, long paramLong, Filter paramFilter, LiveFeedListener paramLiveFeedListener, LoadingProgressListener paramLoadingProgressListener)
    throws NoDataForPeriodException, DataCacheException;

  public abstract void loadCandlesDataAfterSynched(Instrument paramInstrument, Period paramPeriod, OfferSide paramOfferSide, int paramInt, long paramLong, Filter paramFilter, LiveFeedListener paramLiveFeedListener, LoadingProgressListener paramLoadingProgressListener)
    throws NoDataForPeriodException, DataCacheException;

  public abstract void loadCandlesDataBeforeAfterSynched(Instrument paramInstrument, Period paramPeriod, OfferSide paramOfferSide, int paramInt1, int paramInt2, long paramLong, Filter paramFilter, LiveFeedListener paramLiveFeedListener, LoadingProgressListener paramLoadingProgressListener)
    throws NoDataForPeriodException, DataCacheException;

  public abstract void loadLastAvailableCandlesDataSynched(Instrument paramInstrument, Period paramPeriod, OfferSide paramOfferSide, long paramLong1, long paramLong2, LiveFeedListener paramLiveFeedListener, LoadingProgressListener paramLoadingProgressListener)
    throws NoDataForPeriodException, DataCacheException;

  public abstract void loadLastAvailableNumberOfCandlesDataSynched(Instrument paramInstrument, Period paramPeriod, OfferSide paramOfferSide, int paramInt, long paramLong, Filter paramFilter, LiveFeedListener paramLiveFeedListener, LoadingProgressListener paramLoadingProgressListener)
    throws NoDataForPeriodException, DataCacheException;

  public abstract boolean isDataCached(Instrument paramInstrument, Period paramPeriod, OfferSide paramOfferSide, long paramLong1, long paramLong2)
    throws NoDataForPeriodException, DataCacheException;

  public abstract void loadTicksDataInCache(Instrument paramInstrument, long paramLong1, long paramLong2, LoadingProgressListener paramLoadingProgressListener)
    throws DataCacheException;

  public abstract void loadTicksDataInCacheSynched(Instrument paramInstrument, long paramLong1, long paramLong2, LoadingProgressListener paramLoadingProgressListener)
    throws DataCacheException;

  public abstract void loadCandlesDataInCache(Instrument paramInstrument, Period paramPeriod, OfferSide paramOfferSide, long paramLong1, long paramLong2, LoadingProgressListener paramLoadingProgressListener)
    throws NoDataForPeriodException, DataCacheException;

  public abstract void loadCandlesDataInCacheSynched(Instrument paramInstrument, Period paramPeriod, OfferSide paramOfferSide, long paramLong1, long paramLong2, LoadingProgressListener paramLoadingProgressListener)
    throws NoDataForPeriodException, DataCacheException;

  public abstract void loadOrdersHistoricalData(Instrument paramInstrument, long paramLong1, long paramLong2, OrdersListener paramOrdersListener, LoadingProgressListener paramLoadingProgressListener)
    throws DataCacheException;

  public abstract void loadOrdersHistoricalDataSynched(Instrument paramInstrument, long paramLong1, long paramLong2, OrdersListener paramOrdersListener, LoadingProgressListener paramLoadingProgressListener)
    throws DataCacheException;

  public abstract void loadOrdersHistoricalDataInCache(Instrument paramInstrument, long paramLong1, long paramLong2, LoadingProgressListener paramLoadingProgressListener)
    throws DataCacheException;

  public abstract void loadOrdersHistoricalDataInCacheSynched(Instrument paramInstrument, long paramLong1, long paramLong2, LoadingProgressListener paramLoadingProgressListener)
    throws DataCacheException;

  public abstract IOrderUtils getOrderUtils();

  public abstract IFilterManager getFilterManager();

  public abstract IPriceAggregationDataProvider getPriceAggregationDataProvider();

  public abstract IIntraperiodBarsGenerator getIntraperiodBarsGenerator();

  public abstract CurvesDataLoader.IntraperiodExistsPolicy getIntraperiodExistsPolicy();

  public abstract CurvesDataLoader getCurvesDataLoader();

  public abstract IFeedCommissionManager getFeedCommissionManager();

  public abstract LocalCacheManager getLocalCacheManager();

  public abstract long getLatestKnownTimeOrCurrentGMTTime(Instrument paramInstrument);

  public abstract void startInBackgroundFeedPreloadingToLocalCache();

  public abstract List<CandleData> loadCandlesFromToSynched(Instrument paramInstrument, Period paramPeriod, OfferSide paramOfferSide, Filter paramFilter, long paramLong1, long paramLong2)
    throws DataCacheException;

  public abstract void loadCandlesFromTo(Instrument paramInstrument, Period paramPeriod, OfferSide paramOfferSide, Filter paramFilter, long paramLong1, long paramLong2, LiveFeedListener paramLiveFeedListener, LoadingProgressListener paramLoadingProgressListener)
    throws DataCacheException;

  public abstract void loadCandlesFromToSynched(Instrument paramInstrument, Period paramPeriod, OfferSide paramOfferSide, Filter paramFilter, long paramLong1, long paramLong2, LiveFeedListener paramLiveFeedListener, LoadingProgressListener paramLoadingProgressListener)
    throws DataCacheException;

  public abstract Thread loadInCacheAsynch(Instrument paramInstrument, Period paramPeriod, OfferSide paramOfferSide, long paramLong1, long paramLong2, LoadingProgressListener paramLoadingProgressListener, CurvesDataLoader.IntraperiodExistsPolicy paramIntraperiodExistsPolicy, boolean paramBoolean, ChunkLoadingListener paramChunkLoadingListener)
    throws DataCacheException;

  public abstract void loadHistoryDataInCacheFromCFGSynched(Instrument paramInstrument, long paramLong1, long paramLong2, LoadingProgressListener paramLoadingProgressListener);

  public abstract void loadHistoryDataInCacheFromCFGSynched(Instrument paramInstrument, Period paramPeriod, OfferSide paramOfferSide, long paramLong1, long paramLong2, LoadingProgressListener paramLoadingProgressListener);

  public abstract List<Instrument> getInstrumentsSupportedByFileCacheGenerator();

  public abstract IDailyHighLowManager getDailyHighLowManager();

  public abstract void addDataFeedServerConnectionListener(DataFeedServerConnectionListener paramDataFeedServerConnectionListener);

  public abstract void removeDataFeedServerConnectionListener(DataFeedServerConnectionListener paramDataFeedServerConnectionListener);

  public abstract List<DataFeedServerConnectionListener> getDataFeedServerConnectionListeners();
}

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.charts.data.datacache.IFeedDataProvider
 * JD-Core Version:    0.6.0
 */