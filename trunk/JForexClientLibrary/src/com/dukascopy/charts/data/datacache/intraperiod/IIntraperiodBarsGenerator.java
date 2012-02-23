package com.dukascopy.charts.data.datacache.intraperiod;

import com.dukascopy.api.Instrument;
import com.dukascopy.api.OfferSide;
import com.dukascopy.api.PriceRange;
import com.dukascopy.api.ReversalAmount;
import com.dukascopy.api.TickBarSize;
import com.dukascopy.charts.data.datacache.TickData;
import com.dukascopy.charts.data.datacache.pnf.IPointAndFigureLiveFeedListener;
import com.dukascopy.charts.data.datacache.pnf.PointAndFigureData;
import com.dukascopy.charts.data.datacache.rangebar.IPriceRangeLiveFeedListener;
import com.dukascopy.charts.data.datacache.rangebar.PriceRangeData;
import com.dukascopy.charts.data.datacache.renko.IRenkoLiveFeedListener;
import com.dukascopy.charts.data.datacache.renko.RenkoData;
import com.dukascopy.charts.data.datacache.tickbar.ITickBarLiveFeedListener;
import com.dukascopy.charts.data.datacache.tickbar.TickBarData;

public abstract interface IIntraperiodBarsGenerator
{
  public abstract void processTick(Instrument paramInstrument, TickData paramTickData);

  public abstract void startToFillInProgressPriceRange(Instrument paramInstrument, OfferSide paramOfferSide, PriceRange paramPriceRange);

  public abstract boolean isInProgressPriceRangeLoadingNow(Instrument paramInstrument, OfferSide paramOfferSide, PriceRange paramPriceRange);

  public abstract void startToFillInProgressPointAndFigure(Instrument paramInstrument, OfferSide paramOfferSide, PriceRange paramPriceRange, ReversalAmount paramReversalAmount);

  public abstract boolean isInProgressPointAndFigureLoadingNow(Instrument paramInstrument, OfferSide paramOfferSide, PriceRange paramPriceRange, ReversalAmount paramReversalAmount);

  public abstract void startToFillInProgressTickBar(Instrument paramInstrument, OfferSide paramOfferSide, TickBarSize paramTickBarSize);

  public abstract boolean isInProgressTickBarLoadingNow(Instrument paramInstrument, OfferSide paramOfferSide, TickBarSize paramTickBarSize);

  public abstract void startToFillInProgressRenko(Instrument paramInstrument, OfferSide paramOfferSide, PriceRange paramPriceRange);

  public abstract boolean isInProgressRenkoLoadingNow(Instrument paramInstrument, OfferSide paramOfferSide, PriceRange paramPriceRange);

  public abstract PriceRangeData getInProgressPriceRange(Instrument paramInstrument, OfferSide paramOfferSide, PriceRange paramPriceRange);

  public abstract PointAndFigureData getInProgressPointAndFigure(Instrument paramInstrument, OfferSide paramOfferSide, PriceRange paramPriceRange, ReversalAmount paramReversalAmount);

  public abstract TickBarData getInProgressTickBar(Instrument paramInstrument, OfferSide paramOfferSide, TickBarSize paramTickBarSize);

  public abstract RenkoData getInProgressRenko(Instrument paramInstrument, OfferSide paramOfferSide, PriceRange paramPriceRange);

  public abstract void addInProgressPriceRangeListener(Instrument paramInstrument, OfferSide paramOfferSide, PriceRange paramPriceRange, IPriceRangeLiveFeedListener paramIPriceRangeLiveFeedListener);

  public abstract void removeInProgressPriceRangeListener(IPriceRangeLiveFeedListener paramIPriceRangeLiveFeedListener);

  public abstract void addPriceRangeNotificationListener(Instrument paramInstrument, OfferSide paramOfferSide, PriceRange paramPriceRange, IPriceRangeLiveFeedListener paramIPriceRangeLiveFeedListener);

  public abstract void removePriceRangeNotificationListener(IPriceRangeLiveFeedListener paramIPriceRangeLiveFeedListener);

  public abstract void addInProgressPointAndFigureListener(Instrument paramInstrument, OfferSide paramOfferSide, PriceRange paramPriceRange, ReversalAmount paramReversalAmount, IPointAndFigureLiveFeedListener paramIPointAndFigureLiveFeedListener);

  public abstract void removeInProgressPointAndFigureListener(IPointAndFigureLiveFeedListener paramIPointAndFigureLiveFeedListener);

  public abstract void addPointAndFigureNotificationListener(Instrument paramInstrument, OfferSide paramOfferSide, PriceRange paramPriceRange, ReversalAmount paramReversalAmount, IPointAndFigureLiveFeedListener paramIPointAndFigureLiveFeedListener);

  public abstract void removePointAndFigureNotificationListener(IPointAndFigureLiveFeedListener paramIPointAndFigureLiveFeedListener);

  public abstract void addInProgressTickBarListener(Instrument paramInstrument, OfferSide paramOfferSide, TickBarSize paramTickBarSize, ITickBarLiveFeedListener paramITickBarLiveFeedListener);

  public abstract void removeInProgressTickBarListener(ITickBarLiveFeedListener paramITickBarLiveFeedListener);

  public abstract void addTickBarNotificationListener(Instrument paramInstrument, OfferSide paramOfferSide, TickBarSize paramTickBarSize, ITickBarLiveFeedListener paramITickBarLiveFeedListener);

  public abstract void removeTickBarNotificationListener(ITickBarLiveFeedListener paramITickBarLiveFeedListener);

  public abstract void addInProgressRenkoListener(Instrument paramInstrument, OfferSide paramOfferSide, PriceRange paramPriceRange, IRenkoLiveFeedListener paramIRenkoLiveFeedListener);

  public abstract void removeInProgressRenkoListener(IRenkoLiveFeedListener paramIRenkoLiveFeedListener);

  public abstract void addRenkoNotificationListener(Instrument paramInstrument, OfferSide paramOfferSide, PriceRange paramPriceRange, IRenkoLiveFeedListener paramIRenkoLiveFeedListener);

  public abstract void removeRenkoNotificationListener(IRenkoLiveFeedListener paramIRenkoLiveFeedListener);

  public abstract PointAndFigureData getOrLoadInProgressPointAndFigure(Instrument paramInstrument, OfferSide paramOfferSide, PriceRange paramPriceRange, ReversalAmount paramReversalAmount);

  public abstract PriceRangeData getOrLoadInProgressPriceRange(Instrument paramInstrument, OfferSide paramOfferSide, PriceRange paramPriceRange);

  public abstract TickBarData getOrLoadInProgressTickBar(Instrument paramInstrument, OfferSide paramOfferSide, TickBarSize paramTickBarSize);

  public abstract RenkoData getOrLoadInProgressRenko(Instrument paramInstrument, OfferSide paramOfferSide, PriceRange paramPriceRange);
}

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.charts.data.datacache.intraperiod.IIntraperiodBarsGenerator
 * JD-Core Version:    0.6.0
 */