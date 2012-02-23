/*    */ package com.dukascopy.charts.view.drawingstrategies.tick;
/*    */ 
/*    */ import com.dukascopy.charts.chartbuilder.ChartState;
/*    */ import com.dukascopy.charts.chartbuilder.MouseControllerMetaDrawingsState;
/*    */ import com.dukascopy.charts.data.AbstractDataSequenceProvider;
/*    */ import com.dukascopy.charts.data.datacache.Data;
/*    */ import com.dukascopy.charts.data.datacache.TickData;
/*    */ import com.dukascopy.charts.mappers.time.ITimeToXMapper;
/*    */ import com.dukascopy.charts.mappers.value.IValueToYMapper;
/*    */ import com.dukascopy.charts.math.dataprovider.TickDataSequence;
/*    */ import com.dukascopy.charts.utils.formatter.ValueFormatter;
/*    */ import com.dukascopy.charts.view.drawingstrategies.main.MainChartPanelMetaDrawingsDrawingsStrategyAbstract;
/*    */ import java.awt.Graphics;
/*    */ import java.awt.Point;
/*    */ import javax.swing.JComponent;
/*    */ 
/*    */ public class MainChartPanelMetaDrawingsDrawingsStrategyTick extends MainChartPanelMetaDrawingsDrawingsStrategyAbstract
/*    */ {
/*    */   private final ITimeToXMapper tickTimeToXMapper;
/*    */   private final IValueToYMapper tickValueToYMapper;
/*    */   private final AbstractDataSequenceProvider<TickDataSequence, TickData> ticksDataSequenceProvider;
/*    */ 
/*    */   public MainChartPanelMetaDrawingsDrawingsStrategyTick(ITimeToXMapper tickTimeToXMapper, IValueToYMapper tickValueToYMapper, AbstractDataSequenceProvider<TickDataSequence, TickData> ticksDataSequenceProvider, ValueFormatter valueFormatter, ChartState chartState, MouseControllerMetaDrawingsState mouseControllerMetaDrawingsState)
/*    */   {
/* 34 */     super(valueFormatter, chartState, mouseControllerMetaDrawingsState);
/* 35 */     this.tickTimeToXMapper = tickTimeToXMapper;
/* 36 */     this.tickValueToYMapper = tickValueToYMapper;
/* 37 */     this.ticksDataSequenceProvider = ticksDataSequenceProvider;
/*    */   }
/*    */ 
/*    */   protected String getInfoMessage(Graphics g, JComponent jComponent, Point p1, Point p2)
/*    */   {
/* 42 */     if ((p1 == null) || (p2 == null)) {
/* 43 */       return "";
/*    */     }
/*    */ 
/* 46 */     TickDataSequence dataSequence = (TickDataSequence)getTicksDataSequenceProvider().getDataSequence();
/* 47 */     Data[] datas = dataSequence.getData();
/* 48 */     long p1Time = getTickTimeToXMapper().tx(p1.x);
/* 49 */     long p2Time = getTickTimeToXMapper().tx(p2.x);
/* 50 */     long start = p1Time < p2Time ? p1Time : p2Time;
/* 51 */     long end = p1Time > p2Time ? p1Time : p2Time;
/* 52 */     int ticksCount = 0;
/* 53 */     for (Data data : datas) {
/* 54 */       if ((data.getTime() > start) && (data.getTime() < end)) {
/* 55 */         ticksCount++;
/*    */       }
/*    */     }
/*    */ 
/* 59 */     double secondPrice = getTickValueToYMapper().vy(p2.y);
/* 60 */     double valueDiff = Math.abs(secondPrice - getTickValueToYMapper().vy(p1.y));
/*    */ 
/* 62 */     StringBuilder buffer = new StringBuilder();
/* 63 */     buffer.append(ticksCount).append(" / ");
/* 64 */     buffer.append(getValueFormatter().formatValueDiff(valueDiff)).append(" / ");
/* 65 */     buffer.append(getValueFormatter().formatPrice(secondPrice));
/*    */ 
/* 67 */     return buffer.toString();
/*    */   }
/*    */ 
/*    */   public ITimeToXMapper getTickTimeToXMapper()
/*    */   {
/* 72 */     return this.tickTimeToXMapper;
/*    */   }
/*    */ 
/*    */   public IValueToYMapper getTickValueToYMapper() {
/* 76 */     return this.tickValueToYMapper;
/*    */   }
/*    */ 
/*    */   public AbstractDataSequenceProvider<TickDataSequence, TickData> getTicksDataSequenceProvider() {
/* 80 */     return this.ticksDataSequenceProvider;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Charts-5.48.jar
 * Qualified Name:     com.dukascopy.charts.view.drawingstrategies.tick.MainChartPanelMetaDrawingsDrawingsStrategyTick
 * JD-Core Version:    0.6.0
 */