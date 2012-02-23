/*      */ package com.dukascopy.charts.math.dataprovider.priceaggregation;
/*      */ 
/*      */ import com.dukascopy.api.Filter;
/*      */ import com.dukascopy.api.Instrument;
/*      */ import com.dukascopy.api.OfferSide;
/*      */ import com.dukascopy.api.Period;
/*      */ import com.dukascopy.api.Unit;
/*      */ import com.dukascopy.api.impl.IndicatorWrapper;
/*      */ import com.dukascopy.api.indicators.IIndicator;
/*      */ import com.dukascopy.api.indicators.IndicatorInfo;
/*      */ import com.dukascopy.api.indicators.OutputParameterInfo;
/*      */ import com.dukascopy.charts.data.datacache.CandleData;
/*      */ import com.dukascopy.charts.data.datacache.Data;
/*      */ import com.dukascopy.charts.data.datacache.DataCacheException;
/*      */ import com.dukascopy.charts.data.datacache.DataCacheUtils;
/*      */ import com.dukascopy.charts.data.datacache.IFeedDataProvider;
/*      */ import com.dukascopy.charts.data.datacache.priceaggregation.AbstractPriceAggregationData;
/*      */ import com.dukascopy.charts.data.datacache.priceaggregation.IPriceAggregationLiveFeedListener;
/*      */ import com.dukascopy.charts.data.datacache.priceaggregation.TimeDataUtils;
/*      */ import com.dukascopy.charts.math.dataprovider.AbstractDataProvider;
/*      */ import com.dukascopy.charts.math.dataprovider.AbstractDataProvider.AbstractDataCacheRequestData;
/*      */ import com.dukascopy.charts.math.dataprovider.AbstractDataProvider.IndicatorData;
/*      */ import com.dukascopy.charts.math.dataprovider.AbstractDataProvider.LoadDataProgressListener;
/*      */ import com.dukascopy.charts.math.dataprovider.ISynchronizeIndicators;
/*      */ import com.dukascopy.charts.math.dataprovider.priceaggregation.buffer.IPriceAggregationShiftableBuffer;
/*      */ import com.dukascopy.charts.math.dataprovider.priceaggregation.buffer.PriceAggregationShiftableBuffer;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Arrays;
/*      */ import java.util.Collection;
/*      */ import java.util.Collections;
/*      */ import java.util.HashMap;
/*      */ import java.util.List;
/*      */ import java.util.Map;
/*      */ import java.util.Map.Entry;
/*      */ import org.slf4j.Logger;
/*      */ import org.slf4j.LoggerFactory;
/*      */ 
/*      */ public abstract class AbstractPriceAggregationDataProvider<S extends AbstractPriceAggregationDataSequence<D>, D extends AbstractPriceAggregationData, L extends IPriceAggregationLiveFeedListener<D>> extends AbstractDataProvider<D, S>
/*      */ {
/*   47 */   protected static final Logger LOGGER = LoggerFactory.getLogger(AbstractPriceAggregationDataProvider.class);
/*      */   private static final float INCREASING_PERCENT = 0.5F;
/*      */   public static final long MAX_TIME_INTERVAL_BETWEEN_TWO_BARS = 60000L;
/*      */   protected AbstractDataProvider.AbstractDataCacheRequestData dataCacheRequestData;
/*      */   protected AbstractDataProvider<D, S>.LoadDataProgressListener loadingProgressListener;
/*      */   protected final IPriceAggregationShiftableBuffer<D> mainCyclicBuffer;
/*      */   protected D[] currentHistoryRequestDataSubBuffer;
/*   59 */   protected long lastTime = -9223372036854775808L;
/*   60 */   protected int lastBarsBeforeCount = -1;
/*   61 */   protected int lastBarsAfterCount = -1;
/*      */   protected D latestData;
/*      */   protected L latestBarNotificationListener;
/*      */   protected L inProgressBarListener;
/*      */   protected boolean lastDataIsShowing;
/*   68 */   private final int LATEST_BARS_COUNT = 50;
/*   69 */   protected IPriceAggregationShiftableBuffer<D> latestBars = new PriceAggregationShiftableBuffer(50);
/*      */ 
/*      */   public AbstractPriceAggregationDataProvider(Instrument instrument, Period period, OfferSide side, int maxNumberOfCandles, int bufferSizeMultiplier, long lastTime, Filter filter, IFeedDataProvider feedDataProvider)
/*      */   {
/*   82 */     super(instrument, period, side, maxNumberOfCandles, bufferSizeMultiplier, filter, feedDataProvider);
/*      */ 
/*   92 */     this.lastTime = lastTime;
/*   93 */     this.mainCyclicBuffer = new PriceAggregationShiftableBuffer(getMaxBufferSize());
/*   94 */     reset();
/*      */   }
/*      */ 
/*      */   protected abstract D[] createArray(D paramD);
/*      */ 
/*      */   protected abstract D getInProgressBar();
/*      */ 
/*      */   protected abstract S createDataSequence(long paramLong1, long paramLong2, int paramInt1, int paramInt2, D[] paramArrayOfD, Map<Integer, Object[]> paramMap, Map<Integer, IndicatorWrapper> paramMap1, boolean paramBoolean1, boolean paramBoolean2);
/*      */ 
/*      */   protected abstract void performDataLoad(int paramInt1, long paramLong, int paramInt2);
/*      */ 
/*      */   protected abstract void performDataLoad(long paramLong1, long paramLong2);
/*      */ 
/*      */   protected abstract void removeInProgressBarListeners();
/*      */ 
/*      */   protected abstract void addInProgressBarListeners();
/*      */ 
/*      */   protected abstract long getMaxTimeIntervalBetweenTwoBars();
/*      */ 
/*      */   protected long getMaxTimeIntervalBetweenTwoBars(Period period)
/*      */   {
/*  133 */     long result = 0L;
/*  134 */     if ((Unit.Millisecond.equals(period.getUnit())) || (Unit.Second.equals(period.getUnit())) || (Unit.Minute.equals(period.getUnit())) || (Period.TICK.equals(period)))
/*      */     {
/*  140 */       result = 60000L;
/*      */     }
/*      */     else {
/*  143 */       result = period.getInterval();
/*      */     }
/*  145 */     result += result / 4L;
/*  146 */     return result;
/*      */   }
/*      */ 
/*      */   protected void dataLoaded(boolean allDataLoaded, AbstractDataProvider.AbstractDataCacheRequestData requestData, Exception e, ISynchronizeIndicators synchronizeIndicators)
/*      */   {
/*      */     try
/*      */     {
/*  158 */       if ((requestData.cancel) || (this.currentHistoryRequestDataSubBuffer == null))
/*      */       {
/*      */         return;
/*      */       }
/*      */ 
/*  165 */       checkAndAddLatestInProgressBarsNoFire(this.currentHistoryRequestDataSubBuffer);
/*      */ 
/*  167 */       if (this.mainCyclicBuffer.isEmpty()) {
/*  168 */         this.mainCyclicBuffer.setUp(this.currentHistoryRequestDataSubBuffer);
/*  169 */         fireMainBufferChanged();
/*      */       }
/*      */       else {
/*  172 */         long historyDataStartTime = this.currentHistoryRequestDataSubBuffer[0].getTime();
/*  173 */         long historyDataEndTime = this.currentHistoryRequestDataSubBuffer[(this.currentHistoryRequestDataSubBuffer.length - 1)].getTime();
/*      */ 
/*  175 */         long bufferStartTime = getFirstTime();
/*  176 */         long bufferEndTime = getLastDataTime();
/*      */ 
/*  179 */         if (historyDataStartTime < bufferStartTime) {
/*  180 */           AbstractPriceAggregationData[] array = (AbstractPriceAggregationData[])this.mainCyclicBuffer.getAll(createArray(this.mainCyclicBuffer.getSize()));
/*  181 */           if (array.length < this.currentHistoryRequestDataSubBuffer.length) {
/*  182 */             array = this.currentHistoryRequestDataSubBuffer;
/*      */           }
/*      */           else {
/*  185 */             performBufferRightShiftNoFire(array, this.currentHistoryRequestDataSubBuffer);
/*      */           }
/*      */ 
/*  190 */           this.mainCyclicBuffer.setUp(array);
/*      */         }
/*  192 */         else if (historyDataEndTime > bufferEndTime) {
/*  193 */           AbstractPriceAggregationData[] array = (AbstractPriceAggregationData[])this.mainCyclicBuffer.getAll(createArray(this.mainCyclicBuffer.getSize()));
/*      */ 
/*  195 */           if (array.length < this.currentHistoryRequestDataSubBuffer.length) {
/*  196 */             array = this.currentHistoryRequestDataSubBuffer;
/*      */           }
/*      */           else {
/*  199 */             performBufferLeftShiftNoFire(array, this.currentHistoryRequestDataSubBuffer);
/*      */           }
/*      */ 
/*  205 */           this.mainCyclicBuffer.setUp(array);
/*      */         }
/*      */ 
/*  208 */         fireMainBufferChanged();
/*      */       }
/*      */ 
/*  211 */       int inconsistencyIndex = TimeDataUtils.isConsistent((AbstractPriceAggregationData[])this.mainCyclicBuffer.getAll(createArray(this.mainCyclicBuffer.getSize())), false);
/*  212 */       if (inconsistencyIndex > -1)
/*  213 */         LOGGER.error("{}'s buffer is inconsistent for index - {}", new Object[] { getClass().getSimpleName(), new Integer(inconsistencyIndex) });
/*      */     }
/*      */     catch (RuntimeException t)
/*      */     {
/*  217 */       LOGGER.error(t.getMessage(), t);
/*  218 */       throw t;
/*      */     }
/*      */     finally {
/*  221 */       this.loadingStarted = true;
/*  222 */       fireLoadingFinished();
/*      */     }
/*      */   }
/*      */ 
/*      */   private void checkAndAddLatestInProgressBarsNoFire(D[] buffer)
/*      */   {
/*  228 */     if (buffer == null) {
/*  229 */       return;
/*      */     }
/*  231 */     AbstractPriceAggregationData lastData = buffer[(buffer.length - 1)];
/*      */ 
/*  233 */     if (lastData != null) {
/*  234 */       long lastTime = lastData.getEndTime();
/*      */ 
/*  238 */       List latestGatheredWhileHistoryLoadingData = null;
/*  239 */       if (this.latestBars.containsTime(lastTime)) {
/*  240 */         latestGatheredWhileHistoryLoadingData = this.latestBars.getAfterTimeInclude(lastTime);
/*  241 */         latestGatheredWhileHistoryLoadingData.remove(0);
/*      */       }
/*      */       else {
/*  244 */         AbstractPriceAggregationData firstKnownInProgressHistoricalBar = (AbstractPriceAggregationData)this.latestBars.getFirst();
/*      */ 
/*  246 */         long longMaxTimeIntervalBetweeTwoBars = getMaxTimeIntervalBetweenTwoBars();
/*  247 */         if ((firstKnownInProgressHistoricalBar != null) && (Math.abs(firstKnownInProgressHistoricalBar.getTime() - lastTime) <= longMaxTimeIntervalBetweeTwoBars))
/*      */         {
/*  251 */           latestGatheredWhileHistoryLoadingData = this.latestBars.getAfterTimeInclude(firstKnownInProgressHistoricalBar.getTime());
/*      */         }
/*  253 */         else if (firstKnownInProgressHistoricalBar == null)
/*      */         {
/*  258 */           firstKnownInProgressHistoricalBar = getInProgressBar();
/*      */ 
/*  260 */           if ((firstKnownInProgressHistoricalBar != null) && (Math.abs(firstKnownInProgressHistoricalBar.getTime() - lastTime) <= longMaxTimeIntervalBetweeTwoBars))
/*      */           {
/*  264 */             latestGatheredWhileHistoryLoadingData = Collections.singletonList(firstKnownInProgressHistoricalBar);
/*      */           }
/*      */         }
/*      */       }
/*      */ 
/*  269 */       if ((latestGatheredWhileHistoryLoadingData != null) && (!latestGatheredWhileHistoryLoadingData.isEmpty())) {
/*  270 */         AbstractPriceAggregationData[] array = (AbstractPriceAggregationData[])latestGatheredWhileHistoryLoadingData.toArray(createArray(latestGatheredWhileHistoryLoadingData.size()));
/*  271 */         performBufferLeftShiftNoFire(buffer, array);
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   private void fireMainBufferChanged() {
/*  277 */     fireMainBufferChanged(false, false);
/*      */   }
/*      */ 
/*      */   private void fireMainBufferChanged(boolean firstDataChange, boolean sameCandle) {
/*  281 */     if (!this.mainCyclicBuffer.isEmpty()) {
/*  282 */       this.loadingStarted = false;
/*      */ 
/*  286 */       this.firstData = ((Data)this.mainCyclicBuffer.getLast());
/*      */ 
/*  291 */       recalculateIndicators(sameCandle);
/*      */ 
/*  293 */       long dataChangedFrom = ((AbstractPriceAggregationData)this.mainCyclicBuffer.getFirst()).getTime();
/*  294 */       long dataChangedTo = ((AbstractPriceAggregationData)this.mainCyclicBuffer.getLast()).getTime();
/*      */ 
/*  298 */       fireDataChanged(dataChangedFrom, dataChangedTo, firstDataChange, sameCandle);
/*      */     }
/*      */   }
/*      */ 
/*      */   protected void performBufferLeftShiftNoFire(D[] targetBuffer, D[] sourceBuffer)
/*      */   {
/*  307 */     int indexStartFrom = 0;
/*      */ 
/*  309 */     if (targetBuffer[(targetBuffer.length - 1)].getTime() >= sourceBuffer[0].getTime()) {
/*  310 */       for (int i = 0; i < sourceBuffer.length; i++) {
/*  311 */         if (targetBuffer[(targetBuffer.length - 1)].getTime() < sourceBuffer[i].getTime()) {
/*  312 */           indexStartFrom = i;
/*  313 */           break;
/*      */         }
/*      */       }
/*      */     }
/*      */ 
/*  318 */     TimeDataUtils.shiftBufferLeft(targetBuffer, sourceBuffer, indexStartFrom);
/*      */   }
/*      */ 
/*      */   private void performBufferRightShiftNoFire(D[] targetBuffer, D[] sourceBuffer)
/*      */   {
/*  326 */     int historyRequestDataSubBufferLength = sourceBuffer.length;
/*      */ 
/*  328 */     if (sourceBuffer[(sourceBuffer.length - 1)].getTime() >= targetBuffer[0].getTime()) {
/*  329 */       for (int i = sourceBuffer.length - 1; i >= 0; i--) {
/*  330 */         if (sourceBuffer[i].getTime() < targetBuffer[0].getTime()) {
/*  331 */           historyRequestDataSubBufferLength = i + 1;
/*  332 */           break;
/*      */         }
/*      */       }
/*      */     }
/*      */ 
/*  337 */     TimeDataUtils.shiftBufferRight(targetBuffer, sourceBuffer, historyRequestDataSubBufferLength);
/*      */   }
/*      */ 
/*      */   public S getDataSequence(int numBefore, long to, int numAfter)
/*      */   {
/*  349 */     to = checkTime(to, true);
/*      */ 
/*  353 */     boolean dataExists = false;
/*  354 */     int visibleBufferDataStartIndex = -1;
/*  355 */     int visibleBufferDataEndIndex = -1;
/*      */ 
/*  360 */     if (this.mainCyclicBuffer.isEmpty()) {
/*  361 */       doHistoryRequests(numBefore, to, numAfter);
/*  362 */       AbstractPriceAggregationData[] priceRangeDataToShow = (AbstractPriceAggregationData[])createArray(0);
/*  363 */       return createDataSequence(to, to, 0, 0, priceRangeDataToShow, null, null, false, false);
/*      */     }
/*      */ 
/*  369 */     this.lastDataIsShowing = false;
/*      */ 
/*  374 */     int timeIndex = findBufferIndexForTime(to);
/*      */ 
/*  376 */     if (timeIndex < 0)
/*      */     {
/*  382 */       long latestTime = getLatestDataTime();
/*  383 */       AbstractPriceAggregationData inProgresBar = getInProgressBar();
/*  384 */       if (inProgresBar != null) {
/*  385 */         latestTime = inProgresBar.getEndTime();
/*      */       }
/*  387 */       boolean canLoadDataForRequestedTime = (dataExistsBefore(to)) && (to < latestTime);
/*      */ 
/*  389 */       if (canLoadDataForRequestedTime)
/*      */       {
/*  393 */         doHistoryRequests(numBefore, to, numAfter);
/*      */ 
/*  395 */         if (this.mainCyclicBuffer.getSize() > 0) {
/*  396 */           visibleBufferDataStartIndex = 0;
/*  397 */           visibleBufferDataEndIndex = this.mainCyclicBuffer.getLastIndex();
/*  398 */           dataExists = true;
/*      */         }
/*      */ 
/*      */       }
/*      */       else
/*      */       {
/*  405 */         visibleBufferDataStartIndex = this.mainCyclicBuffer.getSize() - numBefore;
/*  406 */         visibleBufferDataEndIndex = this.mainCyclicBuffer.getLastIndex();
/*  407 */         dataExists = true;
/*      */ 
/*  412 */         this.lastDataIsShowing = true;
/*      */       }
/*      */     }
/*  415 */     if (timeIndex >= 0)
/*      */     {
/*  419 */       Integer timeIndexBefore = dataExistBeforeTimeIndex(timeIndex, numBefore);
/*  420 */       Integer timeIndexAfter = dataExistAfterTimeIndex(timeIndex, numAfter);
/*      */ 
/*  422 */       if ((timeIndexBefore != null) && (timeIndexAfter != null))
/*      */       {
/*  426 */         visibleBufferDataStartIndex = timeIndex - numBefore + 1;
/*  427 */         visibleBufferDataEndIndex = visibleBufferDataStartIndex + numBefore + numAfter - 1;
/*      */ 
/*  429 */         dataExists = true;
/*      */       }
/*  431 */       else if ((timeIndexBefore == null) && (timeIndexAfter == null)) {
/*  432 */         if ((getFirstTime() > 0L) && (dataExistsBefore(getFirstTime())) && (getLastDataEndTime() > 0L) && (dataExistsAfter(getLastDataEndTime())))
/*      */         {
/*  441 */           reset();
/*  442 */           doHistoryRequests(numBefore, to, numAfter);
/*      */         }
/*      */         else {
/*  445 */           visibleBufferDataStartIndex = 0;
/*  446 */           visibleBufferDataEndIndex = Math.min(this.mainCyclicBuffer.getLastIndex(), numBefore + numAfter - 1);
/*  447 */           dataExists = true;
/*  448 */           this.lastDataIsShowing = true;
/*      */         }
/*      */       }
/*  451 */       else if (timeIndexBefore == null)
/*      */       {
/*  455 */         long initialTo = to;
/*  456 */         to = getFirstTime();
/*  457 */         if (!dataExistsBefore(to))
/*      */         {
/*  461 */           visibleBufferDataStartIndex = 0;
/*  462 */           visibleBufferDataEndIndex = Math.min(this.mainCyclicBuffer.getLastIndex(), numBefore + numAfter - 1);
/*  463 */           dataExists = true;
/*      */         }
/*      */         else
/*      */         {
/*  469 */           doHistoryRequests(numBefore, initialTo, numAfter);
/*      */         }
/*      */       }
/*  472 */       else if (timeIndexAfter == null)
/*      */       {
/*  476 */         long initialTo = to;
/*  477 */         to = getLastDataEndTime();
/*  478 */         boolean canLoadDataAfterTime = dataExistsAfter(to);
/*      */ 
/*  480 */         if (!canLoadDataAfterTime)
/*      */         {
/*  484 */           visibleBufferDataStartIndex = this.mainCyclicBuffer.getSize() - numBefore;
/*  485 */           visibleBufferDataEndIndex = this.mainCyclicBuffer.getLastIndex();
/*  486 */           dataExists = true;
/*      */ 
/*  491 */           this.lastDataIsShowing = true;
/*      */         }
/*      */         else
/*      */         {
/*  497 */           int priceRangesLack = getLackOfPriceRangesAfterTime(timeIndex, numAfter);
/*  498 */           numAfter = Math.round(this.maxNumberOfCandles * 0.5F) + priceRangesLack;
/*  499 */           doHistoryRequests(numBefore, initialTo, numAfter);
/*      */ 
/*  501 */           visibleBufferDataStartIndex = this.mainCyclicBuffer.getSize() - numBefore;
/*  502 */           visibleBufferDataEndIndex = this.mainCyclicBuffer.getLastIndex();
/*  503 */           dataExists = true;
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  512 */     this.lastTime = to;
/*  513 */     this.lastBarsBeforeCount = numBefore;
/*  514 */     this.lastBarsAfterCount = numAfter;
/*      */ 
/*  519 */     IndicatorsPerformingStruct indicatorsPerformingStruct = performIndicatorsOnDataSequenceRequest(numBefore, to, numAfter, -1L, -1L, dataExists, visibleBufferDataStartIndex, visibleBufferDataEndIndex);
/*      */ 
/*  533 */     AbstractPriceAggregationDataSequence dataSequence = prepareDataSequence(to, dataExists, visibleBufferDataStartIndex, visibleBufferDataEndIndex, indicatorsPerformingStruct);
/*      */ 
/*  541 */     return dataSequence;
/*      */   }
/*      */ 
/*      */   private S prepareDataSequence(long to, boolean dataExists, int visibleBufferDataStartIndex, int visibleBufferDataEndIndex, AbstractPriceAggregationDataProvider<S, D, L>.IndicatorsPerformingStruct indicatorsPerformingStruct)
/*      */   {
/*  552 */     long fromTime = to;
/*  553 */     long toTime = to;
/*  554 */     AbstractPriceAggregationData[] bufferToShow = null;
/*      */ 
/*  556 */     if (dataExists)
/*      */     {
/*  560 */       bufferToShow = (AbstractPriceAggregationData[])createArray(indicatorsPerformingStruct.outputBufferSize);
/*  561 */       this.mainCyclicBuffer.copyOfRange(bufferToShow, indicatorsPerformingStruct.outputBufferStartIndex);
/*      */ 
/*  565 */       fromTime = ((AbstractPriceAggregationData)this.mainCyclicBuffer.get(visibleBufferDataStartIndex)).getTime();
/*  566 */       toTime = ((AbstractPriceAggregationData)this.mainCyclicBuffer.get(visibleBufferDataEndIndex)).getTime();
/*      */     }
/*      */     else {
/*  569 */       bufferToShow = (AbstractPriceAggregationData[])createArray(0);
/*      */     }
/*      */ 
/*  572 */     AbstractPriceAggregationDataSequence dataSequence = createDataSequence(fromTime, toTime, indicatorsPerformingStruct.extraBefore, indicatorsPerformingStruct.extraAfter, bufferToShow, indicatorsPerformingStruct.formulaOutputs, indicatorsPerformingStruct.indicators, this.lastDataIsShowing, this.lastDataIsShowing);
/*      */ 
/*  584 */     return dataSequence;
/*      */   }
/*      */ 
/*      */   public S doGetDataSequence(long from, long to)
/*      */   {
/*  595 */     from = checkTime(from, true);
/*  596 */     to = checkTime(to, true);
/*      */ 
/*  600 */     boolean dataExists = false;
/*  601 */     int visibleBufferDataStartIndex = -1;
/*  602 */     int visibleBufferDataEndIndex = -1;
/*      */ 
/*  607 */     this.lastDataIsShowing = false;
/*      */ 
/*  612 */     if (this.mainCyclicBuffer.isEmpty()) {
/*  613 */       doHistoryRequests(from, to);
/*  614 */       AbstractPriceAggregationData[] priceRangeDataToShow = (AbstractPriceAggregationData[])createArray(0);
/*  615 */       return createDataSequence(to, to, 0, 0, priceRangeDataToShow, null, null, false, false);
/*      */     }
/*      */ 
/*  621 */     int toTimeIndex = findBufferIndexForTime(to);
/*  622 */     int fromTimeIndex = findBufferIndexForTime(from);
/*      */ 
/*  625 */     if ((toTimeIndex >= 0) && (fromTimeIndex >= 0)) {
/*  626 */       visibleBufferDataStartIndex = fromTimeIndex;
/*  627 */       visibleBufferDataEndIndex = toTimeIndex;
/*  628 */       dataExists = true;
/*      */     }
/*  630 */     else if (toTimeIndex >= 0) {
/*  631 */       visibleBufferDataStartIndex = 0;
/*  632 */       visibleBufferDataEndIndex = toTimeIndex;
/*  633 */       doHistoryRequests(from, ((AbstractPriceAggregationData)this.mainCyclicBuffer.get(toTimeIndex)).time);
/*  634 */       dataExists = true;
/*      */     }
/*  636 */     else if (fromTimeIndex >= 0) {
/*  637 */       visibleBufferDataStartIndex = fromTimeIndex;
/*  638 */       visibleBufferDataEndIndex = this.mainCyclicBuffer.getLastIndex();
/*  639 */       doHistoryRequests(((AbstractPriceAggregationData)this.mainCyclicBuffer.getLast()).time, to);
/*  640 */       dataExists = true;
/*      */     }
/*      */     else {
/*  643 */       doHistoryRequests(from, to);
/*  644 */       AbstractPriceAggregationData[] priceRangeDataToShow = (AbstractPriceAggregationData[])createArray(0);
/*  645 */       return createDataSequence(to, to, 0, 0, priceRangeDataToShow, null, null, false, false);
/*      */     }
/*      */ 
/*  651 */     this.lastTime = to;
/*      */ 
/*  655 */     IndicatorsPerformingStruct indicatorsPerformingStruct = performIndicatorsOnDataSequenceRequest(-1, -1L, -1, from, to, dataExists, visibleBufferDataStartIndex, visibleBufferDataEndIndex);
/*      */ 
/*  669 */     AbstractPriceAggregationDataSequence dataSequence = prepareDataSequence(to, dataExists, visibleBufferDataStartIndex, visibleBufferDataEndIndex, indicatorsPerformingStruct);
/*      */ 
/*  677 */     return dataSequence;
/*      */   }
/*      */ 
/*      */   private AbstractPriceAggregationDataProvider<S, D, L>.IndicatorsPerformingStruct performIndicatorsOnDataSequenceRequest(int numBefore, long time, int numAfter, long from, long to, boolean dataExists, int visibleBufferDataStartIndex, int visibleBufferDataEndIndex)
/*      */   {
/*  699 */     IndicatorsPerformingStruct result = new IndicatorsPerformingStruct(null);
/*      */ 
/*  701 */     if (dataExists) {
/*  702 */       if (this.sparceIndicator)
/*      */       {
/*  706 */         result.outputBufferSize = this.mainCyclicBuffer.getSize();
/*  707 */         result.extraBefore = visibleBufferDataStartIndex;
/*  708 */         result.extraAfter = (result.outputBufferSize - visibleBufferDataEndIndex - 1);
/*      */       }
/*      */       else
/*      */       {
/*  715 */         int shift = this.formulasMaxShift + 1;
/*  716 */         int bufferStartWithLookBack = visibleBufferDataStartIndex - shift;
/*      */ 
/*  718 */         if (bufferStartWithLookBack < 0) {
/*  719 */           result.extraBefore = visibleBufferDataStartIndex;
/*      */         }
/*      */         else {
/*  722 */           result.extraBefore = shift;
/*      */         }
/*      */ 
/*  725 */         int bufferEndWithLookForward = visibleBufferDataEndIndex + shift;
/*  726 */         if (bufferEndWithLookForward >= this.mainCyclicBuffer.getSize()) {
/*  727 */           if (visibleBufferDataEndIndex >= this.mainCyclicBuffer.getSize()) {
/*  728 */             result.extraAfter = 0;
/*      */           }
/*      */           else {
/*  731 */             result.extraAfter = (this.mainCyclicBuffer.getSize() - visibleBufferDataEndIndex - 1);
/*      */           }
/*      */         }
/*      */         else {
/*  735 */           result.extraAfter = shift;
/*      */         }
/*  737 */         result.outputBufferStartIndex = (visibleBufferDataStartIndex - result.extraBefore);
/*  738 */         result.outputBufferSize = (visibleBufferDataEndIndex + result.extraAfter - visibleBufferDataStartIndex + 1 + result.extraBefore);
/*      */       }
/*      */     }
/*      */ 
/*  742 */     for (Map.Entry entry : this.formulas.entrySet()) {
/*  743 */       AbstractDataProvider.IndicatorData formulaData = (AbstractDataProvider.IndicatorData)entry.getValue();
/*      */ 
/*  745 */       synchronized (formulaData) {
/*  746 */         boolean outputUnsynchronized = false;
/*  747 */         int idxDiff = 0;
/*  748 */         long formulaDataLastTime = formulaData.getLastTime();
/*  749 */         if ((formulaDataLastTime != ((AbstractPriceAggregationData)this.firstData).getTime()) && (this.lastDataIsShowing)) {
/*  750 */           outputUnsynchronized = true;
/*  751 */           int idx = this.mainCyclicBuffer.getTimeIndex(formulaDataLastTime);
/*  752 */           idxDiff = idx == -1 ? -1 : this.mainCyclicBuffer.getLastIndex() - idx;
/*      */         }
/*  754 */         if (formulaData.disabledIndicator) {
/*      */           continue;
/*      */         }
/*  757 */         IIndicator indicator = formulaData.indicatorWrapper.getIndicator();
/*  758 */         if (result.formulaOutputs == null) {
/*  759 */           result.formulaOutputs = new HashMap();
/*  760 */           result.indicators = new HashMap();
/*      */         }
/*  762 */         result.indicators.put(entry.getKey(), formulaData.indicatorWrapper);
/*  763 */         Object[] outputs = new Object[formulaData.getOutputDataInt().length];
/*  764 */         result.formulaOutputs.put(entry.getKey(), outputs);
/*  765 */         for (int i = 0; i < outputs.length; i++) {
/*  766 */           switch (1.$SwitchMap$com$dukascopy$api$indicators$OutputParameterInfo$Type[indicator.getOutputParameterInfo(i).getType().ordinal()]) {
/*      */           case 1:
/*  768 */             if ((dataExists) && (!outputUnsynchronized)) {
/*  769 */               outputs[i] = new int[result.outputBufferSize];
/*  770 */               System.arraycopy(formulaData.getOutputDataInt()[i], result.outputBufferStartIndex, outputs[i], 0, result.outputBufferSize);
/*      */             }
/*  772 */             else if ((dataExists) && (outputUnsynchronized) && (idxDiff > 0)) {
/*  773 */               outputs[i] = new int[result.outputBufferSize];
/*  774 */               System.arraycopy(formulaData.getOutputDataInt()[i], result.outputBufferStartIndex + idxDiff, outputs[i], 0, result.outputBufferSize - idxDiff);
/*      */             }
/*      */             else {
/*  777 */               outputs[i] = new int[0];
/*      */             }
/*      */ 
/*  780 */             break;
/*      */           case 2:
/*  782 */             if ((dataExists) && (!outputUnsynchronized)) {
/*  783 */               outputs[i] = new double[result.outputBufferSize];
/*  784 */               System.arraycopy(formulaData.getOutputDataDouble()[i], result.outputBufferStartIndex, outputs[i], 0, result.outputBufferSize);
/*      */             }
/*  786 */             else if ((dataExists) && (outputUnsynchronized) && (idxDiff > 0)) {
/*  787 */               outputs[i] = new double[result.outputBufferSize];
/*  788 */               Arrays.fill((double[])(double[])outputs[i], (0.0D / 0.0D));
/*  789 */               System.arraycopy(formulaData.getOutputDataDouble()[i], result.outputBufferStartIndex + idxDiff, outputs[i], 0, result.outputBufferSize - idxDiff);
/*      */             } else {
/*  791 */               outputs[i] = new double[0];
/*      */             }
/*      */ 
/*  794 */             break;
/*      */           case 3:
/*  796 */             if ((dataExists) && (!outputUnsynchronized)) {
/*  797 */               outputs[i] = new Object[result.outputBufferSize];
/*  798 */               System.arraycopy(formulaData.getOutputDataObject()[i], result.outputBufferStartIndex, outputs[i], 0, result.outputBufferSize);
/*      */             }
/*  800 */             else if ((dataExists) && (outputUnsynchronized) && (idxDiff > 0)) {
/*  801 */               outputs[i] = new Object[result.outputBufferSize];
/*  802 */               System.arraycopy(formulaData.getOutputDataObject()[i], result.outputBufferStartIndex + idxDiff, outputs[i], 0, result.outputBufferSize - idxDiff);
/*      */             }
/*      */             else {
/*  805 */               outputs[i] = new Object[0];
/*      */             }
/*      */           }
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/*  812 */       if (formulaData.inputDataProviders != null) {
/*  813 */         for (AbstractDataProvider indicatorDataProvider : formulaData.inputDataProviders) {
/*  814 */           if (indicatorDataProvider == null)
/*      */             continue;
/*      */           long indicatorTo;
/*      */           long indicatorTo;
/*  817 */           if (time > 0L) {
/*  818 */             indicatorTo = time;
/*      */           }
/*      */           else {
/*  821 */             indicatorTo = to;
/*      */           }
/*      */ 
/*  824 */           int indicatorBefore = 1;
/*  825 */           int indicatorAfter = numAfter > 0 ? 1 : 0;
/*  826 */           if (dataExists) {
/*  827 */             indicatorBefore = numBefore;
/*  828 */             if (indicatorBefore < 0) {
/*  829 */               indicatorBefore = 1;
/*      */             }
/*  831 */             indicatorAfter = numAfter;
/*  832 */             if (indicatorAfter < 0) {
/*  833 */               indicatorAfter = 0;
/*      */             }
/*      */           }
/*  836 */           synchronized (indicatorDataProvider)
/*      */           {
/*      */             try
/*      */             {
/*  841 */               Period forPeriod = Period.TICK.equals(indicatorDataProvider.getPeriod()) ? Period.ONE_SEC : indicatorDataProvider.getPeriod();
/*  842 */               indicatorTo = DataCacheUtils.getCandleStart(forPeriod, indicatorTo);
/*      */             } catch (DataCacheException e) {
/*  844 */               LOGGER.error(e.getLocalizedMessage(), e);
/*      */             }
/*  846 */             if (time > 0L) {
/*  847 */               indicatorDataProvider.doHistoryRequests(indicatorBefore, indicatorTo, indicatorAfter);
/*      */             }
/*      */             else {
/*  850 */               indicatorDataProvider.doHistoryRequests(from, to);
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  858 */     return (AbstractPriceAggregationDataProvider<S, D, L>.IndicatorsPerformingStruct)result;
/*      */   }
/*      */ 
/*      */   public AbstractDataProvider<D, S>.LoadDataProgressListener doHistoryRequests(long from, long to)
/*      */   {
/*  866 */     long lastTickTime = this.feedDataProvider.getLastTickTime(this.instrument);
/*  867 */     if (lastTickTime <= 0L)
/*      */     {
/*  871 */       return this.loadingProgressListener;
/*      */     }
/*      */ 
/*  874 */     to = checkTime(to);
/*  875 */     from = checkTime(from);
/*      */     try
/*      */     {
/*  878 */       stopPreviousDataLoading();
/*      */ 
/*  882 */       this.dataCacheRequestData = new AbstractDataProvider.AbstractDataCacheRequestData();
/*  883 */       this.loadingProgressListener = new AbstractDataProvider.LoadDataProgressListener(this, this.dataCacheRequestData);
/*      */ 
/*  885 */       if (this.loadingStarted) {
/*  886 */         return this.loadingProgressListener;
/*      */       }
/*      */ 
/*  889 */       fireLoadingStarted();
/*      */ 
/*  891 */       this.currentHistoryRequestDataSubBuffer = null;
/*      */ 
/*  893 */       performDataLoad(from, to);
/*      */     }
/*      */     catch (Throwable e)
/*      */     {
/*  899 */       LOGGER.error(e.getMessage(), e);
/*      */     }
/*      */ 
/*  902 */     return this.loadingProgressListener;
/*      */   }
/*      */ 
/*      */   public AbstractDataProvider<D, S>.LoadDataProgressListener doHistoryRequests(int numOfCandlesBefore, long reqTime, int numOfCandlesAfter)
/*      */   {
/*  912 */     long lastTickTime = this.feedDataProvider.getLastTickTime(this.instrument);
/*  913 */     if (lastTickTime <= 0L)
/*      */     {
/*  917 */       return this.loadingProgressListener;
/*      */     }
/*      */ 
/*  920 */     reqTime = checkTime(reqTime);
/*      */     try
/*      */     {
/*  923 */       stopPreviousDataLoading();
/*      */ 
/*  927 */       this.dataCacheRequestData = new AbstractDataProvider.AbstractDataCacheRequestData();
/*  928 */       this.loadingProgressListener = new AbstractDataProvider.LoadDataProgressListener(this, this.dataCacheRequestData);
/*      */ 
/*  930 */       boolean isBufferEmpty = this.mainCyclicBuffer.isEmpty();
/*      */ 
/*  932 */       if (isBufferEmpty) {
/*  933 */         numOfCandlesBefore = getMaxBufferSize() / 2;
/*  934 */         numOfCandlesAfter = getMaxBufferSize() - numOfCandlesBefore;
/*      */       }
/*      */       else
/*      */       {
/*  938 */         int timeIndex = findBufferIndexForTime(reqTime);
/*      */ 
/*  940 */         if (timeIndex < 0) {
/*  941 */           numOfCandlesBefore = getMaxBufferSize() / 2;
/*  942 */           numOfCandlesAfter = getMaxBufferSize() - numOfCandlesBefore;
/*      */         }
/*      */         else {
/*  945 */           Integer timeIndexForAfter = dataExistAfterTimeIndex(timeIndex, numOfCandlesAfter);
/*  946 */           Integer timeIndexForBefore = dataExistBeforeTimeIndex(timeIndex, numOfCandlesBefore);
/*      */ 
/*  948 */           if ((timeIndexForAfter == null) && (timeIndexForBefore == null)) {
/*  949 */             throw new IllegalArgumentException("No data in buffer!");
/*      */           }
/*      */ 
/*  952 */           if (timeIndexForAfter != null) {
/*  953 */             numOfCandlesAfter = 0;
/*      */           }
/*      */           else {
/*  956 */             reqTime = getLastDataTime();
/*  957 */             if (!dataExistsAfter(reqTime)) {
/*  958 */               return this.loadingProgressListener;
/*      */             }
/*      */ 
/*  961 */             int priceRangesLack = getLackOfPriceRangesAfterTime(timeIndex, numOfCandlesAfter);
/*  962 */             numOfCandlesAfter = Math.round(this.maxNumberOfCandles * 0.5F) + priceRangesLack;
/*      */           }
/*      */ 
/*  965 */           if (timeIndexForBefore != null) {
/*  966 */             numOfCandlesBefore = 0;
/*      */           }
/*      */           else {
/*  969 */             reqTime = getFirstTime();
/*  970 */             int priceRangesLack = getLackOfPriceRangesBeforeTime(timeIndex, numOfCandlesBefore);
/*  971 */             numOfCandlesBefore = Math.round(this.maxNumberOfCandles * 0.5F) + priceRangesLack;
/*      */           }
/*      */         }
/*      */       }
/*      */ 
/*  976 */       if ((numOfCandlesBefore == 0) && (numOfCandlesAfter == 0)) {
/*  977 */         return this.loadingProgressListener;
/*      */       }
/*      */ 
/*  980 */       if (this.loadingStarted) {
/*  981 */         return this.loadingProgressListener;
/*      */       }
/*      */ 
/*  984 */       fireLoadingStarted();
/*      */ 
/*  986 */       this.currentHistoryRequestDataSubBuffer = ((AbstractPriceAggregationData[])createArray(numOfCandlesBefore + numOfCandlesAfter));
/*      */ 
/*  988 */       performDataLoad(numOfCandlesBefore, reqTime, numOfCandlesAfter);
/*      */     }
/*      */     catch (Throwable e)
/*      */     {
/*  995 */       LOGGER.error(e.getMessage(), e);
/*      */     }
/*      */ 
/*  998 */     return this.loadingProgressListener;
/*      */   }
/*      */ 
/*      */   public long getLastLoadedDataTime()
/*      */   {
/* 1004 */     long time = getLastDataTime();
/* 1005 */     return time;
/*      */   }
/*      */ 
/*      */   protected void initIndicatorDataOutputBuffers(AbstractDataProvider.IndicatorData formulaData)
/*      */   {
/* 1010 */     IndicatorInfo indicatorInfo = formulaData.indicatorWrapper.getIndicator().getIndicatorInfo();
/* 1011 */     for (int i = 0; i < indicatorInfo.getNumberOfOutputs(); i++) {
/* 1012 */       OutputParameterInfo outputParameterInfo = formulaData.indicatorWrapper.getIndicator().getOutputParameterInfo(i);
/* 1013 */       switch (1.$SwitchMap$com$dukascopy$api$indicators$OutputParameterInfo$Type[outputParameterInfo.getType().ordinal()]) {
/*      */       case 1:
/* 1015 */         formulaData.getOutputDataInt()[i] = new int[this.mainCyclicBuffer.getMaxSize()];
/* 1016 */         break;
/*      */       case 2:
/* 1018 */         formulaData.getOutputDataDouble()[i] = new double[this.mainCyclicBuffer.getMaxSize()];
/* 1019 */         break;
/*      */       case 3:
/* 1021 */         formulaData.getOutputDataObject()[i] = new Object[this.mainCyclicBuffer.getMaxSize()];
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   protected void recalculateIndicator(AbstractDataProvider.IndicatorData indicatorData, boolean latestData, boolean sameCandle)
/*      */   {
/* 1029 */     if ((indicatorData.indicatorWrapper.isRecalculateOnNewCandleOnly()) && (sameCandle)) {
/* 1030 */       return;
/*      */     }
/* 1032 */     int indexFrom = 0;
/* 1033 */     int indexTo = this.mainCyclicBuffer.getLastIndex();
/*      */ 
/* 1035 */     Collection indicators = new ArrayList(1);
/* 1036 */     indicators.add(indicatorData);
/* 1037 */     if (latestData) {
/* 1038 */       recalculateIndicators(indexTo, indexTo, indicators, indexTo, (CandleData[])this.mainCyclicBuffer.getAll(createArray(this.mainCyclicBuffer.getSize())), null);
/*      */     }
/*      */     else
/* 1041 */       recalculateIndicators(indexFrom, indexTo, indicators, indexTo, (CandleData[])this.mainCyclicBuffer.getAll(createArray(this.mainCyclicBuffer.getSize())), null);
/*      */   }
/*      */ 
/*      */   protected void recalculateIndicators()
/*      */   {
/* 1047 */     recalculateIndicators(false);
/*      */   }
/*      */ 
/*      */   protected void recalculateIndicators(boolean sameBar) {
/* 1051 */     int indexTo = this.mainCyclicBuffer.getLastIndex();
/* 1052 */     int indexFrom = sameBar ? indexTo : 0;
/*      */ 
/* 1054 */     recalculateIndicators(indexFrom, indexTo, sameBar);
/*      */   }
/*      */ 
/*      */   protected void recalculateIndicators(int from, int to, boolean sameBar) {
/* 1058 */     if (!this.active) {
/* 1059 */       return;
/*      */     }
/*      */ 
/* 1062 */     int lastIndex = to;
/*      */ 
/* 1064 */     boolean split = false;
/* 1065 */     for (AbstractDataProvider.IndicatorData formulaData : this.formulas.values()) {
/* 1066 */       if ((formulaData.indicatorWrapper.getIndicator().getIndicatorInfo().isRecalculateAll()) || (formulaData.indicatorWrapper.isRecalculateOnNewCandleOnly())) {
/* 1067 */         split = true;
/* 1068 */         break;
/*      */       }
/*      */     }
/* 1071 */     if (split) {
/* 1072 */       Collection recalculateAllFormulas = new ArrayList(this.formulas.size());
/* 1073 */       Collection restOfTheFormulas = new ArrayList(this.formulas.size());
/* 1074 */       for (AbstractDataProvider.IndicatorData formulaData : this.formulas.values()) {
/* 1075 */         if ((formulaData.indicatorWrapper.isRecalculateOnNewCandleOnly()) && (sameBar)) {
/*      */           continue;
/*      */         }
/* 1078 */         if (formulaData.indicatorWrapper.getIndicator().getIndicatorInfo().isRecalculateAll())
/* 1079 */           recalculateAllFormulas.add(formulaData);
/*      */         else {
/* 1081 */           restOfTheFormulas.add(formulaData);
/*      */         }
/*      */       }
/* 1084 */       if (!recalculateAllFormulas.isEmpty()) {
/* 1085 */         recalculateIndicators(0, lastIndex, recalculateAllFormulas, lastIndex, (CandleData[])this.mainCyclicBuffer.getAll(createArray(this.mainCyclicBuffer.getSize())), null);
/*      */       }
/* 1087 */       if (!restOfTheFormulas.isEmpty()) {
/* 1088 */         recalculateIndicators(from, to, restOfTheFormulas, lastIndex, (CandleData[])this.mainCyclicBuffer.getAll(createArray(this.mainCyclicBuffer.getSize())), null);
/*      */       }
/*      */     }
/* 1091 */     else if (!this.formulas.isEmpty()) {
/* 1092 */       recalculateIndicators(from, to, this.formulas.values(), lastIndex, (CandleData[])this.mainCyclicBuffer.getAll(createArray(this.mainCyclicBuffer.getSize())), null);
/*      */     }
/*      */   }
/*      */ 
/*      */   public Filter getFilter()
/*      */   {
/* 1100 */     return this.filter;
/*      */   }
/*      */ 
/*      */   public void start()
/*      */   {
/* 1105 */     this.lastTime = checkTime(this.lastTime);
/* 1106 */     addInProgressBarListeners();
/* 1107 */     doHistoryRequests(0, this.lastTime, 0);
/*      */   }
/*      */ 
/*      */   public void dispose()
/*      */   {
/* 1112 */     reset();
/*      */ 
/* 1114 */     super.dispose();
/*      */ 
/* 1116 */     removeInProgressBarListeners();
/*      */ 
/* 1118 */     stopPreviousDataLoading();
/*      */   }
/*      */ 
/*      */   private boolean dataExistsAfter(long time)
/*      */   {
/* 1126 */     AbstractPriceAggregationData lastBar = (AbstractPriceAggregationData)this.latestBars.getLast();
/* 1127 */     if (lastBar == null) {
/* 1128 */       lastBar = getInProgressBar();
/*      */     }
/* 1130 */     if (lastBar != null)
/*      */     {
/* 1132 */       return (!this.latestBars.containsTime(time)) && (time < lastBar.getTime() - getMaxTimeIntervalBetweenTwoBars());
/*      */     }
/*      */ 
/* 1139 */     return false;
/*      */   }
/*      */ 
/*      */   private boolean dataExistsBefore(long time)
/*      */   {
/* 1144 */     long firstKnownTime = getFirstKnownTime();
/* 1145 */     firstKnownTime += getMaxTimeIntervalBetweenTwoBars();
/* 1146 */     return time > firstKnownTime;
/*      */   }
/*      */ 
/*      */   protected void reset() {
/* 1150 */     this.mainCyclicBuffer.clear();
/* 1151 */     this.latestBars.clear();
/*      */   }
/*      */ 
/*      */   private int findBufferIndexForTime(long time)
/*      */   {
/* 1158 */     int index = this.mainCyclicBuffer.getTimeIndex(time);
/* 1159 */     return index;
/*      */   }
/*      */ 
/*      */   private int getLackOfPriceRangesBeforeTime(int timeIndex, int priceRangesBeforeCount)
/*      */   {
/* 1166 */     if (dataExistBeforeTimeIndex(timeIndex, priceRangesBeforeCount) == null) {
/* 1167 */       return priceRangesBeforeCount - timeIndex;
/*      */     }
/* 1169 */     return 0;
/*      */   }
/*      */ 
/*      */   private int getLackOfPriceRangesAfterTime(int timeIndex, int priceRangesAfter)
/*      */   {
/* 1176 */     if (dataExistAfterTimeIndex(timeIndex, priceRangesAfter) == null) {
/* 1177 */       return priceRangesAfter - (this.mainCyclicBuffer.getSize() - timeIndex);
/*      */     }
/* 1179 */     return 0;
/*      */   }
/*      */ 
/*      */   private Integer dataExistBeforeTimeIndex(int timeIndex, int priceRangesBefore) {
/* 1183 */     if (timeIndex - priceRangesBefore >= 0) {
/* 1184 */       return new Integer(timeIndex);
/*      */     }
/*      */ 
/* 1187 */     return null;
/*      */   }
/*      */ 
/*      */   private Integer dataExistAfterTimeIndex(int timeIndex, int priceRangesAfter) {
/* 1191 */     if (timeIndex + priceRangesAfter < this.mainCyclicBuffer.getSize()) {
/* 1192 */       return new Integer(timeIndex);
/*      */     }
/*      */ 
/* 1195 */     return null;
/*      */   }
/*      */ 
/*      */   private long getFirstTime()
/*      */   {
/* 1202 */     AbstractPriceAggregationData firstData = (AbstractPriceAggregationData)this.mainCyclicBuffer.getFirst();
/* 1203 */     if (firstData == null) {
/* 1204 */       return -9223372036854775808L;
/*      */     }
/* 1206 */     return firstData.getTime();
/*      */   }
/*      */ 
/*      */   private long getLastDataTime()
/*      */   {
/* 1213 */     AbstractPriceAggregationData lastData = (AbstractPriceAggregationData)this.mainCyclicBuffer.getLast();
/* 1214 */     if (lastData == null) {
/* 1215 */       return -9223372036854775808L;
/*      */     }
/* 1217 */     return lastData.getTime();
/*      */   }
/*      */ 
/*      */   private long getLastDataEndTime()
/*      */   {
/* 1222 */     AbstractPriceAggregationData lastData = (AbstractPriceAggregationData)this.mainCyclicBuffer.getLast();
/* 1223 */     if (lastData == null) {
/* 1224 */       return -9223372036854775808L;
/*      */     }
/* 1226 */     return lastData.getEndTime();
/*      */   }
/*      */ 
/*      */   private long checkTime(long time, boolean canIncludeInProgressBar) {
/* 1230 */     long lastTickTime = this.feedDataProvider.getCurrentTime();
/*      */ 
/* 1232 */     if (time <= 0L) {
/* 1233 */       time = lastTickTime;
/*      */     }
/*      */ 
/* 1236 */     if (time > lastTickTime) {
/* 1237 */       time = lastTickTime;
/*      */     }
/*      */ 
/* 1240 */     long firstKnownTime = getFirstKnownTime();
/* 1241 */     if (time < firstKnownTime) {
/* 1242 */       time = firstKnownTime;
/*      */     }
/*      */ 
/* 1245 */     long latestKnownTime = getInProgressBarStartTime();
/*      */     long latestPossibleTime;
/*      */     long latestPossibleTime;
/* 1248 */     if (canIncludeInProgressBar) {
/* 1249 */       latestPossibleTime = latestKnownTime;
/*      */     }
/*      */     else {
/* 1252 */       latestPossibleTime = DataCacheUtils.getPreviousPriceAggregationBarStart(latestKnownTime);
/*      */     }
/*      */ 
/* 1255 */     if (time > latestPossibleTime) {
/* 1256 */       time = latestPossibleTime;
/*      */     }
/*      */ 
/* 1259 */     return time;
/*      */   }
/*      */ 
/*      */   private long checkTime(long time) {
/* 1263 */     return checkTime(time, false);
/*      */   }
/*      */ 
/*      */   protected void synchronizeParams(Instrument instrument, Period period, Filter filter, OfferSide side, boolean isAnyParamChanged)
/*      */   {
/* 1277 */     if (isAnyParamChanged) {
/* 1278 */       removeInProgressBarListeners();
/*      */     }
/*      */ 
/* 1281 */     this.period = period;
/* 1282 */     this.side = side;
/* 1283 */     this.filter = filter;
/* 1284 */     this.instrument = instrument;
/*      */ 
/* 1286 */     if ((this.lastBarsBeforeCount > -1) && (this.lastTime > -1L) && (this.lastBarsAfterCount > -1)) {
/* 1287 */       this.dataCacheRequestData.cancel = true;
/* 1288 */       fireLoadingFinished();
/* 1289 */       if (isAnyParamChanged)
/*      */       {
/* 1293 */         reset();
/* 1294 */         addInProgressBarListeners();
/*      */       }
/* 1296 */       doHistoryRequests(this.lastBarsBeforeCount, this.lastTime, this.lastBarsAfterCount);
/*      */     }
/* 1298 */     else if (isAnyParamChanged) {
/* 1299 */       addInProgressBarListeners();
/*      */     }
/*      */   }
/*      */ 
/*      */   protected void latestBarArrived(D bar)
/*      */   {
/* 1305 */     performLatestBar(bar);
/*      */   }
/*      */ 
/*      */   protected void inProgressBarUpdated(D bar) {
/* 1309 */     performLatestBar(bar);
/*      */ 
/* 1311 */     fireLastKnownDataChanged(bar);
/*      */   }
/*      */ 
/*      */   protected void performLatestBar(D bar) {
/* 1315 */     this.firstData = bar;
/*      */ 
/* 1317 */     if (!this.latestBars.addOrReplace(bar)) {
/* 1318 */       return;
/*      */     }
/*      */ 
/* 1321 */     if (!canPerformLatestBar())
/*      */     {
/* 1325 */       return;
/*      */     }
/*      */ 
/* 1328 */     this.latestData = bar;
/*      */ 
/* 1330 */     if (this.mainCyclicBuffer.getLast() != null) {
/* 1331 */       if (this.latestData.getTime() != ((AbstractPriceAggregationData)this.mainCyclicBuffer.getLast()).getTime())
/*      */       {
/* 1335 */         if (this.lastDataIsShowing) {
/* 1336 */           this.mainCyclicBuffer.addToEnd(this.latestData);
/* 1337 */           fireMainBufferChanged();
/*      */         }
/* 1339 */         else if (Math.abs(((AbstractPriceAggregationData)this.mainCyclicBuffer.getLast()).getEndTime() - this.latestData.getTime()) < getMaxTimeIntervalBetweenTwoBars()) {
/* 1340 */           this.mainCyclicBuffer.addToEnd(this.latestData);
/* 1341 */           recalculateIndicators(false);
/*      */         }
/*      */ 
/*      */       }
/*      */       else
/*      */       {
/* 1348 */         setLastData(this.latestData);
/* 1349 */         recalculateIndicators(true);
/*      */       }
/*      */     }
/*      */ 
/* 1353 */     if (this.lastDataIsShowing)
/* 1354 */       fireDataChanged(this.latestData.getTime(), this.latestData.getEndTime(), true, true);
/*      */   }
/*      */ 
/*      */   private void setLastData(D bar)
/*      */   {
/* 1359 */     this.mainCyclicBuffer.set(bar, this.mainCyclicBuffer.getLastIndex());
/*      */   }
/*      */ 
/*      */   private boolean canPerformLatestBar() {
/* 1363 */     long latestMainBufferTime = getLastDataEndTime();
/* 1364 */     if (latestMainBufferTime > 0L) {
/* 1365 */       boolean latestBufferContainsTime = this.latestBars.containsTime(latestMainBufferTime);
/* 1366 */       if (latestBufferContainsTime) {
/* 1367 */         return true;
/*      */       }
/* 1369 */       AbstractPriceAggregationData lastBar = (AbstractPriceAggregationData)this.latestBars.getLast();
/* 1370 */       if ((lastBar != null) && (Math.abs(lastBar.getTime() - latestMainBufferTime) < getMaxTimeIntervalBetweenTwoBars())) {
/* 1371 */         return true;
/*      */       }
/*      */     }
/*      */ 
/* 1375 */     return false;
/*      */   }
/*      */ 
/*      */   protected void historicalBarsArived(List<D> bars) {
/* 1379 */     historicalBarsArived((AbstractPriceAggregationData[])bars.toArray(createArray(bars.size())));
/*      */   }
/*      */ 
/*      */   protected void historicalBarsArived(D[] bars) {
/* 1383 */     this.currentHistoryRequestDataSubBuffer = bars;
/*      */   }
/*      */ 
/*      */   private int getMaxBufferSize() {
/* 1387 */     return this.maxNumberOfCandles * this.bufferSizeMultiplier;
/*      */   }
/*      */ 
/*      */   private void stopPreviousDataLoading() {
/* 1391 */     if (this.dataCacheRequestData != null) {
/* 1392 */       this.dataCacheRequestData.cancel = true;
/*      */     }
/* 1394 */     this.loadingStarted = false;
/*      */   }
/*      */ 
/*      */   protected long getInProgressBarStartTime() {
/* 1398 */     AbstractPriceAggregationData bar = getInProgressBar();
/* 1399 */     return bar != null ? bar.getTime() : this.feedDataProvider.getLatestKnownTimeOrCurrentGMTTime(getInstrument());
/*      */   }
/*      */ 
/*      */   protected D[] getAllBufferedData()
/*      */   {
/* 1404 */     return (AbstractPriceAggregationData[])this.mainCyclicBuffer.getAll(createArray(this.mainCyclicBuffer.getSize()));
/*      */   }
/*      */ 
/*      */   public synchronized long getLatestDataTime()
/*      */   {
/* 1409 */     AbstractPriceAggregationData inprogressBar = getInProgressBar();
/* 1410 */     if (inprogressBar == null) {
/* 1411 */       return super.getLatestDataTime();
/*      */     }
/*      */ 
/* 1414 */     return inprogressBar.getTime();
/*      */   }
/*      */ 
/*      */   private class IndicatorsPerformingStruct
/*      */   {
/*  681 */     public int extraBefore = 0;
/*  682 */     public int extraAfter = 0;
/*  683 */     public int outputBufferSize = 0;
/*  684 */     public int outputBufferStartIndex = 0;
/*  685 */     public Map<Integer, Object[]> formulaOutputs = null;
/*  686 */     public Map<Integer, IndicatorWrapper> indicators = null;
/*      */ 
/*      */     private IndicatorsPerformingStruct()
/*      */     {
/*      */     }
/*      */   }
/*      */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.charts.math.dataprovider.priceaggregation.AbstractPriceAggregationDataProvider
 * JD-Core Version:    0.6.0
 */