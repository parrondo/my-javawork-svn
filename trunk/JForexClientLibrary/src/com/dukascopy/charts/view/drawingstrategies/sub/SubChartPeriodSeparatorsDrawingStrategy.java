/*    */ package com.dukascopy.charts.view.drawingstrategies.sub;
/*    */ 
/*    */ import com.dukascopy.charts.chartbuilder.ChartState;
/*    */ import com.dukascopy.charts.data.AbstractDataSequenceProvider;
/*    */ import com.dukascopy.charts.data.datacache.Data;
/*    */ import com.dukascopy.charts.mappers.time.ITimeToXMapper;
/*    */ import com.dukascopy.charts.math.dataprovider.AbstractDataSequence;
/*    */ import com.dukascopy.charts.view.drawingstrategies.main.MainChartPeriodSeparatorsDrawingStrategy;
/*    */ 
/*    */ public class SubChartPeriodSeparatorsDrawingStrategy<SequenceClassData extends AbstractDataSequence<DataClass>, DataClass extends Data> extends MainChartPeriodSeparatorsDrawingStrategy<SequenceClassData, DataClass>
/*    */ {
/*    */   public SubChartPeriodSeparatorsDrawingStrategy(ChartState chartState, AbstractDataSequenceProvider<SequenceClassData, DataClass> dataSequenceProvider, ITimeToXMapper timeToXMapper)
/*    */   {
/* 22 */     super(chartState, dataSequenceProvider, timeToXMapper);
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Charts-5.48.jar
 * Qualified Name:     com.dukascopy.charts.view.drawingstrategies.sub.SubChartPeriodSeparatorsDrawingStrategy
 * JD-Core Version:    0.6.0
 */