/*     */ package com.dukascopy.charts.view.drawingstrategies.main;
/*     */ 
/*     */ import com.dukascopy.api.Period;
/*     */ import com.dukascopy.api.Unit;
/*     */ import com.dukascopy.charts.chartbuilder.ChartState;
/*     */ import com.dukascopy.charts.data.AbstractDataSequenceProvider;
/*     */ import com.dukascopy.charts.data.datacache.Data;
/*     */ import com.dukascopy.charts.mappers.time.ITimeToXMapper;
/*     */ import com.dukascopy.charts.math.dataprovider.AbstractDataSequence;
/*     */ import com.dukascopy.charts.persistence.ITheme;
/*     */ import com.dukascopy.charts.persistence.ITheme.ChartElement;
/*     */ import com.dukascopy.charts.settings.ChartSettings;
/*     */ import com.dukascopy.charts.settings.ChartSettings.Option;
/*     */ import com.dukascopy.charts.view.displayabledatapart.IDrawingStrategy;
/*     */ import java.awt.Color;
/*     */ import java.awt.Graphics;
/*     */ import java.awt.Graphics2D;
/*     */ import java.awt.geom.GeneralPath;
/*     */ import java.text.SimpleDateFormat;
/*     */ import java.util.TimeZone;
/*     */ import javax.swing.JComponent;
/*     */ 
/*     */ public class MainChartPeriodSeparatorsDrawingStrategy<DataSequenceClass extends AbstractDataSequence<DataClass>, DataClass extends Data>
/*     */   implements IDrawingStrategy
/*     */ {
/*     */   private final ChartState chartState;
/*     */   protected final AbstractDataSequenceProvider<DataSequenceClass, DataClass> dataSequenceProvider;
/*     */   protected final ITimeToXMapper timeToXMapper;
/*  33 */   protected final GeneralPath path = new GeneralPath();
/*     */   private static final int LINE_GAP_PIXELS = 10;
/*  38 */   public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat() { } ;
/*     */ 
/*     */   public MainChartPeriodSeparatorsDrawingStrategy(ChartState chartState, AbstractDataSequenceProvider<DataSequenceClass, DataClass> dataSequenceProvider, ITimeToXMapper timeToXMapper)
/*     */   {
/*  48 */     this.chartState = chartState;
/*  49 */     this.dataSequenceProvider = dataSequenceProvider;
/*  50 */     this.timeToXMapper = timeToXMapper;
/*     */   }
/*     */ 
/*     */   public void draw(Graphics g, JComponent jComponent)
/*     */   {
/*  55 */     Graphics2D g2d = (Graphics2D)g;
/*     */ 
/*  57 */     Color color = g2d.getColor();
/*     */ 
/*  59 */     Boolean drawPeriodSeparators = canDrawPeriodSeparators();
/*     */ 
/*  61 */     if (Boolean.TRUE.equals(drawPeriodSeparators)) {
/*  62 */       drawSeparators(g2d, jComponent);
/*     */     }
/*     */ 
/*  65 */     g2d.setColor(color);
/*     */   }
/*     */ 
/*     */   protected Boolean canDrawPeriodSeparators() {
/*  69 */     return (Boolean)ChartSettings.get(ChartSettings.Option.PERIOD_SEPARATORS);
/*     */   }
/*     */ 
/*     */   protected void drawSeparators(Graphics2D g, JComponent jComponent)
/*     */   {
/*  86 */     g.setColor(this.chartState.getTheme().getColor(ITheme.ChartElement.PERIOD_SEPARATORS));
/*  87 */     Period period = null;
/*     */ 
/*  90 */     if (this.chartState.getPeriod() != Period.ONE_YEAR)
/*     */     {
/*  93 */       if (this.chartState.getPeriod() == Period.MONTHLY) {
/*  94 */         period = Period.ONE_YEAR;
/*     */       }
/*  96 */       else if (this.chartState.getPeriod() == Period.WEEKLY) {
/*  97 */         period = Period.ONE_YEAR;
/*     */       }
/*  99 */       else if (this.chartState.getPeriod() == Period.DAILY) {
/* 100 */         period = Period.WEEKLY;
/*     */       }
/*     */       else {
/* 103 */         period = Period.DAILY;
/*     */       }
/*     */     }
/* 106 */     drawSeparatorsForPeriod(g, jComponent, period);
/*     */   }
/*     */ 
/*     */   protected void drawSeparatorsForPeriod(Graphics2D g2d, JComponent jComponent, Period period) {
/* 110 */     if (period == null) {
/* 111 */       return;
/*     */     }
/*     */ 
/* 114 */     AbstractDataSequence dataSequence = (AbstractDataSequence)this.dataSequenceProvider.getDataSequence();
/*     */ 
/* 116 */     if ((dataSequence != null) && (dataSequence.size() > 0)) {
/* 117 */       this.path.reset();
/*     */ 
/* 119 */       int height = jComponent.getHeight();
/* 120 */       long interval = period.getUnit().getInterval();
/*     */ 
/* 122 */       for (int i = 0; i < dataSequence.size(); i++) {
/* 123 */         Data data = dataSequence.getData(i);
/* 124 */         if (data.time % interval == 0L) {
/* 125 */           int x = this.timeToXMapper.xt(data.time);
/* 126 */           drawVerticalLine(x, height);
/*     */         }
/*     */       }
/*     */ 
/* 130 */       g2d.draw(this.path);
/*     */     }
/*     */   }
/*     */ 
/*     */   protected void drawVerticalLine(int x, int height) {
/* 135 */     for (int i = 0; i < height; i += 20) {
/* 136 */       this.path.moveTo(x, i);
/* 137 */       this.path.lineTo(x, i + 10);
/*     */     }
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Charts-5.48.jar
 * Qualified Name:     com.dukascopy.charts.view.drawingstrategies.main.MainChartPeriodSeparatorsDrawingStrategy
 * JD-Core Version:    0.6.0
 */