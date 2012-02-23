package com.dukascopy.charts.data.datacache.priceaggregation.dataprovider;

import com.dukascopy.api.Instrument;
import com.dukascopy.api.OfferSide;
import com.dukascopy.api.PriceRange;
import com.dukascopy.api.ReversalAmount;
import com.dukascopy.api.TickBarSize;
import com.dukascopy.charts.data.datacache.IFeedDataProvider;
import com.dukascopy.charts.data.datacache.LoadingProgressListener;
import com.dukascopy.charts.data.datacache.pnf.IPointAndFigureLiveFeedListener;
import com.dukascopy.charts.data.datacache.pnf.PointAndFigureData;
import com.dukascopy.charts.data.datacache.rangebar.IPriceRangeLiveFeedListener;
import com.dukascopy.charts.data.datacache.rangebar.PriceRangeData;
import com.dukascopy.charts.data.datacache.renko.IRenkoLiveFeedListener;
import com.dukascopy.charts.data.datacache.renko.RenkoData;
import com.dukascopy.charts.data.datacache.tickbar.ITickBarLiveFeedListener;
import com.dukascopy.charts.data.datacache.tickbar.TickBarData;
import java.util.List;

public abstract interface IPriceAggregationDataProvider
{
  public abstract void close();

  public abstract IFeedDataProvider getFeedDataProvider();

  public abstract void loadPriceRangeData(Instrument paramInstrument, OfferSide paramOfferSide, PriceRange paramPriceRange, int paramInt1, long paramLong, int paramInt2, IPriceRangeLiveFeedListener paramIPriceRangeLiveFeedListener, LoadingProgressListener paramLoadingProgressListener);

  public abstract void loadPriceRangeData(Instrument paramInstrument, OfferSide paramOfferSide, PriceRange paramPriceRange, int paramInt1, long paramLong, int paramInt2, IPriceRangeLiveFeedListener paramIPriceRangeLiveFeedListener, LoadingProgressListener paramLoadingProgressListener, boolean paramBoolean);

  public abstract void loadPriceRangeDataSynched(Instrument paramInstrument, OfferSide paramOfferSide, PriceRange paramPriceRange, int paramInt1, long paramLong, int paramInt2, IPriceRangeLiveFeedListener paramIPriceRangeLiveFeedListener, LoadingProgressListener paramLoadingProgressListener);

  public abstract List<PriceRangeData> loadPriceRangeData(Instrument paramInstrument, OfferSide paramOfferSide, PriceRange paramPriceRange, int paramInt1, long paramLong, int paramInt2);

  public abstract List<PriceRangeData> loadPriceRangeData(Instrument paramInstrument, OfferSide paramOfferSide, PriceRange paramPriceRange, int paramInt1, long paramLong, int paramInt2, boolean paramBoolean);

  public abstract PriceRangeData loadLastPriceRangeData(Instrument paramInstrument, OfferSide paramOfferSide, PriceRange paramPriceRange);

  public abstract void loadPriceRangeTimeIntervalSynched(Instrument paramInstrument, OfferSide paramOfferSide, PriceRange paramPriceRange, long paramLong1, long paramLong2, IPriceRangeLiveFeedListener paramIPriceRangeLiveFeedListener, LoadingProgressListener paramLoadingProgressListener);

  public abstract void loadPriceRangeTimeIntervalSynched(Instrument paramInstrument, OfferSide paramOfferSide, PriceRange paramPriceRange, long paramLong1, long paramLong2, IPriceRangeLiveFeedListener paramIPriceRangeLiveFeedListener, LoadingProgressListener paramLoadingProgressListener, boolean paramBoolean);

  public abstract void loadPriceRangeTimeInterval(Instrument paramInstrument, OfferSide paramOfferSide, PriceRange paramPriceRange, long paramLong1, long paramLong2, IPriceRangeLiveFeedListener paramIPriceRangeLiveFeedListener, LoadingProgressListener paramLoadingProgressListener);

  public abstract void loadPriceRangeTimeInterval(Instrument paramInstrument, OfferSide paramOfferSide, PriceRange paramPriceRange, long paramLong1, long paramLong2, IPriceRangeLiveFeedListener paramIPriceRangeLiveFeedListener, LoadingProgressListener paramLoadingProgressListener, boolean paramBoolean);

  public abstract List<PriceRangeData> loadPriceRangeTimeInterval(Instrument paramInstrument, OfferSide paramOfferSide, PriceRange paramPriceRange, long paramLong1, long paramLong2);

  public abstract List<PriceRangeData> loadPriceRangeTimeInterval(Instrument paramInstrument, OfferSide paramOfferSide, PriceRange paramPriceRange, long paramLong1, long paramLong2, boolean paramBoolean);

  public abstract void loadPointAndFigureData(Instrument paramInstrument, OfferSide paramOfferSide, PriceRange paramPriceRange, ReversalAmount paramReversalAmount, int paramInt1, long paramLong, int paramInt2, IPointAndFigureLiveFeedListener paramIPointAndFigureLiveFeedListener, LoadingProgressListener paramLoadingProgressListener);

  public abstract void loadPointAndFigureData(Instrument paramInstrument, OfferSide paramOfferSide, PriceRange paramPriceRange, ReversalAmount paramReversalAmount, int paramInt1, long paramLong, int paramInt2, IPointAndFigureLiveFeedListener paramIPointAndFigureLiveFeedListener, LoadingProgressListener paramLoadingProgressListener, boolean paramBoolean);

  public abstract void loadPointAndFigureDataSynched(Instrument paramInstrument, OfferSide paramOfferSide, PriceRange paramPriceRange, ReversalAmount paramReversalAmount, int paramInt1, long paramLong, int paramInt2, IPointAndFigureLiveFeedListener paramIPointAndFigureLiveFeedListener, LoadingProgressListener paramLoadingProgressListener);

  public abstract List<PointAndFigureData> loadPointAndFigureData(Instrument paramInstrument, OfferSide paramOfferSide, PriceRange paramPriceRange, ReversalAmount paramReversalAmount, int paramInt1, long paramLong, int paramInt2);

  public abstract List<PointAndFigureData> loadPointAndFigureData(Instrument paramInstrument, OfferSide paramOfferSide, PriceRange paramPriceRange, ReversalAmount paramReversalAmount, int paramInt1, long paramLong, int paramInt2, boolean paramBoolean);

  public abstract PointAndFigureData loadLastPointAndFigureData(Instrument paramInstrument, OfferSide paramOfferSide, PriceRange paramPriceRange, ReversalAmount paramReversalAmount);

  public abstract void loadPointAndFigureTimeIntervalSynched(Instrument paramInstrument, OfferSide paramOfferSide, PriceRange paramPriceRange, ReversalAmount paramReversalAmount, long paramLong1, long paramLong2, IPointAndFigureLiveFeedListener paramIPointAndFigureLiveFeedListener, LoadingProgressListener paramLoadingProgressListener);

  public abstract void loadPointAndFigureTimeIntervalSynched(Instrument paramInstrument, OfferSide paramOfferSide, PriceRange paramPriceRange, ReversalAmount paramReversalAmount, long paramLong1, long paramLong2, IPointAndFigureLiveFeedListener paramIPointAndFigureLiveFeedListener, LoadingProgressListener paramLoadingProgressListener, boolean paramBoolean);

  public abstract void loadPointAndFigureTimeInterval(Instrument paramInstrument, OfferSide paramOfferSide, PriceRange paramPriceRange, ReversalAmount paramReversalAmount, long paramLong1, long paramLong2, IPointAndFigureLiveFeedListener paramIPointAndFigureLiveFeedListener, LoadingProgressListener paramLoadingProgressListener);

  public abstract void loadPointAndFigureTimeInterval(Instrument paramInstrument, OfferSide paramOfferSide, PriceRange paramPriceRange, ReversalAmount paramReversalAmount, long paramLong1, long paramLong2, IPointAndFigureLiveFeedListener paramIPointAndFigureLiveFeedListener, LoadingProgressListener paramLoadingProgressListener, boolean paramBoolean);

  public abstract List<PointAndFigureData> loadPointAndFigureTimeInterval(Instrument paramInstrument, OfferSide paramOfferSide, PriceRange paramPriceRange, ReversalAmount paramReversalAmount, long paramLong1, long paramLong2);

  public abstract List<PointAndFigureData> loadPointAndFigureTimeInterval(Instrument paramInstrument, OfferSide paramOfferSide, PriceRange paramPriceRange, ReversalAmount paramReversalAmount, long paramLong1, long paramLong2, boolean paramBoolean);

  public abstract void loadTickBarDataSynched(Instrument paramInstrument, OfferSide paramOfferSide, TickBarSize paramTickBarSize, int paramInt1, long paramLong, int paramInt2, ITickBarLiveFeedListener paramITickBarLiveFeedListener, LoadingProgressListener paramLoadingProgressListener);

  public abstract List<TickBarData> loadTickBarData(Instrument paramInstrument, OfferSide paramOfferSide, TickBarSize paramTickBarSize, int paramInt1, long paramLong, int paramInt2);

  public abstract List<TickBarData> loadTickBarData(Instrument paramInstrument, OfferSide paramOfferSide, TickBarSize paramTickBarSize, int paramInt1, long paramLong, int paramInt2, boolean paramBoolean);

  public abstract void loadTickBarData(Instrument paramInstrument, OfferSide paramOfferSide, TickBarSize paramTickBarSize, int paramInt1, long paramLong, int paramInt2, ITickBarLiveFeedListener paramITickBarLiveFeedListener, LoadingProgressListener paramLoadingProgressListener);

  public abstract void loadTickBarData(Instrument paramInstrument, OfferSide paramOfferSide, TickBarSize paramTickBarSize, int paramInt1, long paramLong, int paramInt2, ITickBarLiveFeedListener paramITickBarLiveFeedListener, LoadingProgressListener paramLoadingProgressListener, boolean paramBoolean);

  public abstract TickBarData loadLastTickBarData(Instrument paramInstrument, OfferSide paramOfferSide, TickBarSize paramTickBarSize);

  public abstract void loadTickBarTimeIntervalSynched(Instrument paramInstrument, OfferSide paramOfferSide, TickBarSize paramTickBarSize, long paramLong1, long paramLong2, ITickBarLiveFeedListener paramITickBarLiveFeedListener, LoadingProgressListener paramLoadingProgressListener);

  public abstract void loadTickBarTimeIntervalSynched(Instrument paramInstrument, OfferSide paramOfferSide, TickBarSize paramTickBarSize, long paramLong1, long paramLong2, ITickBarLiveFeedListener paramITickBarLiveFeedListener, LoadingProgressListener paramLoadingProgressListener, boolean paramBoolean);

  public abstract void loadTickBarTimeInterval(Instrument paramInstrument, OfferSide paramOfferSide, TickBarSize paramTickBarSize, long paramLong1, long paramLong2, ITickBarLiveFeedListener paramITickBarLiveFeedListener, LoadingProgressListener paramLoadingProgressListener);

  public abstract void loadTickBarTimeInterval(Instrument paramInstrument, OfferSide paramOfferSide, TickBarSize paramTickBarSize, long paramLong1, long paramLong2, ITickBarLiveFeedListener paramITickBarLiveFeedListener, LoadingProgressListener paramLoadingProgressListener, boolean paramBoolean);

  public abstract List<TickBarData> loadTickBarTimeInterval(Instrument paramInstrument, OfferSide paramOfferSide, TickBarSize paramTickBarSize, long paramLong1, long paramLong2);

  public abstract List<TickBarData> loadTickBarTimeInterval(Instrument paramInstrument, OfferSide paramOfferSide, TickBarSize paramTickBarSize, long paramLong1, long paramLong2, boolean paramBoolean);

  public abstract void loadRenkoData(Instrument paramInstrument, OfferSide paramOfferSide, PriceRange paramPriceRange, int paramInt1, long paramLong, int paramInt2, IRenkoLiveFeedListener paramIRenkoLiveFeedListener, LoadingProgressListener paramLoadingProgressListener);

  public abstract void loadRenkoData(Instrument paramInstrument, OfferSide paramOfferSide, PriceRange paramPriceRange, int paramInt1, long paramLong, int paramInt2, IRenkoLiveFeedListener paramIRenkoLiveFeedListener, LoadingProgressListener paramLoadingProgressListener, boolean paramBoolean);

  public abstract RenkoData loadLastRenko(Instrument paramInstrument, OfferSide paramOfferSide, PriceRange paramPriceRange);

  public abstract void loadRenkoTimeIntervalSynched(Instrument paramInstrument, OfferSide paramOfferSide, PriceRange paramPriceRange, long paramLong1, long paramLong2, IRenkoLiveFeedListener paramIRenkoLiveFeedListener, LoadingProgressListener paramLoadingProgressListener);

  public abstract void loadRenkoTimeIntervalSynched(Instrument paramInstrument, OfferSide paramOfferSide, PriceRange paramPriceRange, long paramLong1, long paramLong2, IRenkoLiveFeedListener paramIRenkoLiveFeedListener, LoadingProgressListener paramLoadingProgressListener, boolean paramBoolean);

  public abstract List<RenkoData> loadRenkoTimeInterval(Instrument paramInstrument, OfferSide paramOfferSide, PriceRange paramPriceRange, long paramLong1, long paramLong2, boolean paramBoolean);

  public abstract List<RenkoData> loadRenkoTimeInterval(Instrument paramInstrument, OfferSide paramOfferSide, PriceRange paramPriceRange, long paramLong1, long paramLong2);

  public abstract void loadRenkoTimeInterval(Instrument paramInstrument, OfferSide paramOfferSide, PriceRange paramPriceRange, long paramLong1, long paramLong2, IRenkoLiveFeedListener paramIRenkoLiveFeedListener, LoadingProgressListener paramLoadingProgressListener);

  public abstract void loadRenkoTimeInterval(Instrument paramInstrument, OfferSide paramOfferSide, PriceRange paramPriceRange, long paramLong1, long paramLong2, IRenkoLiveFeedListener paramIRenkoLiveFeedListener, LoadingProgressListener paramLoadingProgressListener, boolean paramBoolean);

  public abstract void loadRenkoDataSynched(Instrument paramInstrument, OfferSide paramOfferSide, PriceRange paramPriceRange, int paramInt1, long paramLong, int paramInt2, IRenkoLiveFeedListener paramIRenkoLiveFeedListener, LoadingProgressListener paramLoadingProgressListener);

  public abstract List<RenkoData> loadRenkoData(Instrument paramInstrument, OfferSide paramOfferSide, PriceRange paramPriceRange, int paramInt1, long paramLong, int paramInt2);

  public abstract List<RenkoData> loadRenkoData(Instrument paramInstrument, OfferSide paramOfferSide, PriceRange paramPriceRange, int paramInt1, long paramLong, int paramInt2, boolean paramBoolean);
}

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.charts.data.datacache.priceaggregation.dataprovider.IPriceAggregationDataProvider
 * JD-Core Version:    0.6.0
 */