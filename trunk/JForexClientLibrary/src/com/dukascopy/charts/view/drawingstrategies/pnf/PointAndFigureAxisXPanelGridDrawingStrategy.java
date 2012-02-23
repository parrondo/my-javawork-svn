/*     */ package com.dukascopy.charts.view.drawingstrategies.pnf;
/*     */ 
/*     */ import com.dukascopy.charts.chartbuilder.ChartState;
/*     */ import com.dukascopy.charts.data.AbstractDataSequenceProvider;
/*     */ import com.dukascopy.charts.data.datacache.pnf.PointAndFigureData;
/*     */ import com.dukascopy.charts.mappers.time.GeometryCalculator;
/*     */ import com.dukascopy.charts.mappers.time.ITimeToXMapper;
/*     */ import com.dukascopy.charts.math.dataprovider.priceaggregation.pf.PointAndFigureDataSequence;
/*     */ import com.dukascopy.charts.persistence.ITheme;
/*     */ import com.dukascopy.charts.persistence.ITheme.ChartElement;
/*     */ import com.dukascopy.charts.persistence.ITheme.TextElement;
/*     */ import com.dukascopy.charts.utils.formatter.DateFormatter;
/*     */ import com.dukascopy.charts.view.drawingstrategies.common.CommonAxisXPanelGridDrawingStrategy;
/*     */ import java.awt.Color;
/*     */ import java.awt.Font;
/*     */ import java.awt.Graphics;
/*     */ import java.awt.Graphics2D;
/*     */ import java.awt.geom.GeneralPath;
/*     */ import javax.swing.JComponent;
/*     */ 
/*     */ public class PointAndFigureAxisXPanelGridDrawingStrategy extends CommonAxisXPanelGridDrawingStrategy<PointAndFigureDataSequence, PointAndFigureData>
/*     */ {
/*     */   protected final GeometryCalculator geometryCalculator;
/*     */ 
/*     */   protected PointAndFigureAxisXPanelGridDrawingStrategy(ChartState chartState, ITimeToXMapper timeToXMapper, AbstractDataSequenceProvider<PointAndFigureDataSequence, PointAndFigureData> dataSequenceProvider, DateFormatter dateFormatter, GeometryCalculator geometryCalculator)
/*     */   {
/*  39 */     super(chartState, timeToXMapper, dataSequenceProvider, dateFormatter);
/*     */ 
/*  46 */     this.geometryCalculator = geometryCalculator;
/*     */   }
/*     */ 
/*     */   public void draw(Graphics g, JComponent jComponent)
/*     */   {
/*  52 */     PointAndFigureData lastData = (PointAndFigureData)((PointAndFigureDataSequence)this.dataSequenceProvider.getDataSequence()).getLastData();
/*  53 */     if (lastData == null)
/*     */     {
/*  57 */       return;
/*     */     }
/*     */ 
/*  60 */     super.draw(g, jComponent);
/*     */ 
/*  62 */     int interval = this.geometryCalculator.getDataUnitWidth();
/*  63 */     int height = jComponent.getHeight() / 2;
/*  64 */     int datasCount = 1 + ((PointAndFigureDataSequence)this.dataSequenceProvider.getDataSequence()).indexOf(lastData.getTime());
/*     */ 
/*  67 */     Graphics2D g2d = (Graphics2D)g;
/*     */ 
/*  70 */     Font prevFont = g.getFont();
/*  71 */     Color prevColor = g.getColor();
/*     */ 
/*  73 */     g.setFont(this.chartState.getTheme().getFont(ITheme.TextElement.AXIS));
/*  74 */     g.setColor(this.chartState.getTheme().getColor(ITheme.ChartElement.DEFAULT));
/*     */ 
/*  77 */     this.path.reset();
/*     */ 
/*  79 */     drawBoxesFromTimeSeparatorLine(this.path, jComponent, height);
/*  80 */     drawBoxSeparators(interval, datasCount, this.path, height);
/*     */ 
/*  82 */     g2d.draw(this.path);
/*     */ 
/*  84 */     drawBoxLabels(interval, datasCount, g2d);
/*     */ 
/*  86 */     g.setColor(prevColor);
/*  87 */     g.setFont(prevFont);
/*     */   }
/*     */ 
/*     */   private void drawBoxesFromTimeSeparatorLine(GeneralPath path, JComponent jComponent, int height) {
/*  91 */     path.moveTo(0.0F, height);
/*  92 */     path.lineTo(jComponent.getWidth(), height);
/*     */   }
/*     */ 
/*     */   private void drawBoxSeparators(int interval, int datasCount, GeneralPath path, int height) {
/*  96 */     for (int i = 0; i < datasCount; i++) {
/*  97 */       if (!canDrawInterval(interval, i)) {
/*     */         continue;
/*     */       }
/* 100 */       int x = i * interval;
/*     */ 
/* 102 */       path.moveTo(x, 0.0F);
/* 103 */       path.lineTo(x, height);
/*     */     }
/*     */   }
/*     */ 
/*     */   private void drawBoxLabels(int interval, int datasCount, Graphics2D g2d)
/*     */   {
/* 109 */     for (int i = 0; i < datasCount; i++) {
/* 110 */       if (!canDrawInterval(interval, i)) {
/*     */         continue;
/*     */       }
/* 113 */       int x = i * interval;
/* 114 */       g2d.drawString(String.valueOf(i + 1), x + 2, 10);
/*     */     }
/*     */   }
/*     */ 
/*     */   private boolean canDrawInterval(int interval, int index)
/*     */   {
/* 122 */     if ((interval < 2) && (index % 9 != 0)) {
/* 123 */       return false;
/*     */     }
/* 125 */     if ((interval < 5) && (index % 4 != 0)) {
/* 126 */       return false;
/*     */     }
/* 128 */     if ((interval < 10) && (index % 3 != 0)) {
/* 129 */       return false;
/*     */     }
/*     */ 
/* 132 */     return (interval >= 18) || (index % 2 == 0);
/*     */   }
/*     */ 
/*     */   protected int getTimePanelY(JComponent jComponent)
/*     */   {
/* 140 */     return jComponent.getHeight() / 2;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Charts-5.48.jar
 * Qualified Name:     com.dukascopy.charts.view.drawingstrategies.pnf.PointAndFigureAxisXPanelGridDrawingStrategy
 * JD-Core Version:    0.6.0
 */