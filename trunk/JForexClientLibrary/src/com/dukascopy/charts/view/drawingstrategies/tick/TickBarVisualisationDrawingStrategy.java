/*     */ package com.dukascopy.charts.view.drawingstrategies.tick;
/*     */ 
/*     */ import com.dukascopy.charts.chartbuilder.ChartState;
/*     */ import com.dukascopy.charts.data.AbstractDataSequenceProvider;
/*     */ import com.dukascopy.charts.data.datacache.TickData;
/*     */ import com.dukascopy.charts.drawings.ValuePoint;
/*     */ import com.dukascopy.charts.mappers.time.GeometryCalculator;
/*     */ import com.dukascopy.charts.mappers.time.ITimeToXMapper;
/*     */ import com.dukascopy.charts.mappers.value.IValueToYMapper;
/*     */ import com.dukascopy.charts.math.dataprovider.TickDataSequence;
/*     */ import com.dukascopy.charts.utils.PathHelper;
/*     */ import com.dukascopy.charts.utils.formatter.DateFormatter;
/*     */ import java.awt.Color;
/*     */ import java.awt.Graphics2D;
/*     */ import java.awt.geom.GeneralPath;
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
/*     */ import javax.swing.JComponent;
/*     */ 
/*     */ class TickBarVisualisationDrawingStrategy extends AbstractTickVisualisationDrawingStrategy
/*     */ {
/*  25 */   final GeneralPath redPath = new GeneralPath();
/*  26 */   final GeneralPath greenPath = new GeneralPath();
/*  27 */   final Map<Color, GeneralPath> paths = new HashMap() { private static final long serialVersionUID = 1L; } ;
/*     */ 
/*  35 */   GeneralPath currentPath = this.greenPath;
/*     */   double previousBid;
/*     */   double previousAsk;
/*     */ 
/*     */   public TickBarVisualisationDrawingStrategy(DateFormatter dateFormatter, ChartState chartState, AbstractDataSequenceProvider<TickDataSequence, TickData> dataSequenceProvider, GeometryCalculator geometryCalculator, ITimeToXMapper timeToXMapper, IValueToYMapper valueToYMapper, PathHelper pathHelper)
/*     */   {
/*  49 */     super(dateFormatter, chartState, dataSequenceProvider, geometryCalculator, timeToXMapper, valueToYMapper, pathHelper);
/*     */   }
/*     */ 
/*     */   protected void drawTicks(Graphics2D g2, JComponent jComponent, TickDataSequence tickDataSequence, Color posColor, Color negColor)
/*     */   {
/*  54 */     if (tickDataSequence.isEmpty()) {
/*  55 */       return;
/*     */     }
/*     */ 
/*  58 */     TickData[] ticks = (TickData[])tickDataSequence.getData();
/*     */ 
/*  60 */     this.redPath.reset();
/*  61 */     this.greenPath.reset();
/*  62 */     this.previousAsk = -1.0D;
/*  63 */     this.previousBid = -1.0D;
/*     */ 
/*  65 */     this.currentPath = this.greenPath;
/*     */ 
/*  67 */     int i = tickDataSequence.getExtraBefore(); for (int j = ticks.length - tickDataSequence.getExtraAfter(); i < j; i++) {
/*  68 */       TickData tick = ticks[i];
/*  69 */       double ask = tick.ask;
/*  70 */       double bid = tick.bid;
/*     */ 
/*  72 */       float timeX = this.timeToXMapper.xt(tick.time);
/*  73 */       float askY = this.valueToYMapper.yv(ask);
/*  74 */       float bidY = this.valueToYMapper.yv(bid);
/*     */ 
/*  76 */       Color color = getColor(ask, bid);
/*  77 */       getCurrentPath(color).moveTo(timeX, bidY);
/*  78 */       getCurrentPath(color).lineTo(timeX, askY);
/*     */ 
/*  80 */       this.pathHelper.savePoints(i, timeX, askY, bidY, askY, bidY);
/*     */     }
/*     */ 
/*  83 */     Color prevColor = g2.getColor();
/*  84 */     g2.setColor(posColor);
/*  85 */     g2.draw(this.greenPath);
/*  86 */     g2.setColor(negColor);
/*  87 */     g2.draw(this.redPath);
/*  88 */     g2.setColor(prevColor);
/*     */   }
/*     */ 
/*     */   GeneralPath getCurrentPath(Color color) {
/*  92 */     GeneralPath path = (GeneralPath)this.paths.get(color);
/*  93 */     if (path != null) {
/*  94 */       this.currentPath = path;
/*     */     }
/*  96 */     return this.currentPath;
/*     */   }
/*     */ 
/*     */   Color getColor(double currentAsk, double currentBid) {
/* 100 */     Color color = null;
/*     */ 
/* 102 */     if ((!ValuePoint.isValidValue(this.previousAsk)) || (!ValuePoint.isValidValue(this.previousBid)))
/* 103 */       color = Color.GREEN;
/* 104 */     else if ((this.previousAsk < currentAsk) && (this.previousBid == currentBid))
/* 105 */       color = Color.GREEN;
/* 106 */     else if ((this.previousAsk < currentAsk) && (this.previousBid < currentBid))
/* 107 */       color = Color.GREEN;
/* 108 */     else if ((this.previousAsk == currentAsk) && (this.previousBid < currentBid))
/* 109 */       color = Color.GREEN;
/* 110 */     else if ((this.previousAsk == currentAsk) && (this.previousBid > currentBid))
/* 111 */       color = Color.RED;
/* 112 */     else if ((this.previousAsk > currentAsk) && (this.previousBid > currentBid))
/* 113 */       color = Color.RED;
/* 114 */     else if ((this.previousAsk > currentAsk) && (this.previousBid == currentBid)) {
/* 115 */       color = Color.RED;
/*     */     }
/*     */ 
/* 118 */     this.previousAsk = currentAsk;
/* 119 */     this.previousBid = currentBid;
/*     */ 
/* 121 */     return color;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Charts-5.48.jar
 * Qualified Name:     com.dukascopy.charts.view.drawingstrategies.tick.TickBarVisualisationDrawingStrategy
 * JD-Core Version:    0.6.0
 */