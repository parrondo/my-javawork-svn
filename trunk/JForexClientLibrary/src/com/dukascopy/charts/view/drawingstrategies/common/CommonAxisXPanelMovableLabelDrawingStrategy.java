/*     */ package com.dukascopy.charts.view.drawingstrategies.common;
/*     */ 
/*     */ import com.dukascopy.charts.chartbuilder.ChartState;
/*     */ import com.dukascopy.charts.data.AbstractDataSequenceProvider;
/*     */ import com.dukascopy.charts.data.datacache.Data;
/*     */ import com.dukascopy.charts.mappers.time.GeometryCalculator;
/*     */ import com.dukascopy.charts.mappers.time.ITimeToXMapper;
/*     */ import com.dukascopy.charts.math.dataprovider.AbstractDataSequence;
/*     */ import com.dukascopy.charts.persistence.ITheme;
/*     */ import com.dukascopy.charts.persistence.ITheme.ChartElement;
/*     */ import com.dukascopy.charts.persistence.ITheme.TextElement;
/*     */ import com.dukascopy.charts.utils.formatter.DateFormatter;
/*     */ import com.dukascopy.charts.view.displayabledatapart.IDrawingStrategy;
/*     */ import java.awt.Color;
/*     */ import java.awt.Container;
/*     */ import java.awt.Dimension;
/*     */ import java.awt.Font;
/*     */ import java.awt.FontMetrics;
/*     */ import java.awt.Graphics;
/*     */ import java.awt.Graphics2D;
/*     */ import java.awt.LayoutManager;
/*     */ import java.awt.Point;
/*     */ import java.awt.geom.GeneralPath;
/*     */ import java.awt.geom.Rectangle2D.Float;
/*     */ import javax.swing.JComponent;
/*     */ 
/*     */ public class CommonAxisXPanelMovableLabelDrawingStrategy<DataSequenceClass extends AbstractDataSequence<DataClass>, DataClass extends Data>
/*     */   implements IDrawingStrategy
/*     */ {
/*     */   protected final ChartState chartState;
/*     */   protected final DateFormatter dateFormatter;
/*     */   protected final ITimeToXMapper timeToXMapper;
/*     */   protected final AbstractDataSequenceProvider<DataSequenceClass, DataClass> dataSequenceProvider;
/*     */   protected final GeometryCalculator geometryCalculator;
/*  34 */   protected final GeneralPath path = new GeneralPath();
/*     */ 
/*     */   public CommonAxisXPanelMovableLabelDrawingStrategy(DateFormatter dateFormatter, ChartState chartState, AbstractDataSequenceProvider<DataSequenceClass, DataClass> dataSequenceProvider, GeometryCalculator geometryCalculator, ITimeToXMapper timeToXMapper)
/*     */   {
/*  43 */     this.dateFormatter = dateFormatter;
/*  44 */     this.chartState = chartState;
/*  45 */     this.dataSequenceProvider = dataSequenceProvider;
/*  46 */     this.geometryCalculator = geometryCalculator;
/*  47 */     this.timeToXMapper = timeToXMapper;
/*     */   }
/*     */ 
/*     */   protected void setupHeight(JComponent jComponent, int height) {
/*  51 */     if (jComponent.getHeight() != height) {
/*  52 */       jComponent.setSize(new Dimension(jComponent.getWidth(), height));
/*  53 */       jComponent.getParent().getLayout().layoutContainer(jComponent.getParent().getParent());
/*     */     }
/*     */   }
/*     */ 
/*     */   protected int getPreferredHeight() {
/*  58 */     return 12;
/*     */   }
/*     */ 
/*     */   public void draw(Graphics g, JComponent jComponent) {
/*  62 */     if (this.chartState.isMouseCursorOnWindow(-2)) {
/*  63 */       return;
/*     */     }
/*     */ 
/*  66 */     setupHeight(jComponent, getPreferredHeight());
/*     */ 
/*  68 */     int curMouseX = this.chartState.getMouseCursorPoint().x;
/*     */ 
/*  70 */     String formattedMessage = getFormattedMessage(curMouseX);
/*  71 */     if (formattedMessage.isEmpty()) {
/*  72 */       return;
/*     */     }
/*  74 */     int formattedMessageWidth = getStringWidth(g, formattedMessage);
/*     */ 
/*  76 */     Color color = g.getColor();
/*  77 */     Font font = g.getFont();
/*     */ 
/*  79 */     g.setFont(this.chartState.getTheme().getFont(ITheme.TextElement.AXIS));
/*     */ 
/*  81 */     drawMovableLabelBackground((Graphics2D)g, jComponent, curMouseX, formattedMessageWidth, getY(jComponent) - 1, getHeight(jComponent));
/*  82 */     drawMovableLabelText(g, curMouseX, formattedMessage, formattedMessageWidth, jComponent.getHeight() - 2);
/*     */ 
/*  84 */     g.setColor(color);
/*  85 */     g.setFont(font);
/*     */   }
/*     */ 
/*     */   protected String getFormattedMessage(int curMouseX) {
/*  89 */     StringBuilder formattedMessage = new StringBuilder();
/*  90 */     long time = this.timeToXMapper.tx(curMouseX);
/*  91 */     if (time != -1L) {
/*  92 */       String formattedTime = this.dateFormatter.formatTime(time);
/*  93 */       formattedMessage.append(formattedTime);
/*     */     }
/*     */ 
/*  96 */     return formattedMessage.toString();
/*     */   }
/*     */ 
/*     */   protected int getStringWidth(Graphics g, String formattedTime) {
/* 100 */     FontMetrics fontMetrics = g.getFontMetrics(this.chartState.getTheme().getFont(ITheme.TextElement.AXIS));
/* 101 */     return fontMetrics.stringWidth(formattedTime);
/*     */   }
/*     */ 
/*     */   protected void drawMovableLabelBackground(Graphics2D g2d, JComponent jComponent, int curMouseX, int formattedTimeWidth, int y, int height)
/*     */   {
/* 112 */     this.path.reset();
/* 113 */     this.path.append(new Rectangle2D.Float(curMouseX - formattedTimeWidth / 2 - 4, y, formattedTimeWidth + 8, height), false);
/*     */ 
/* 122 */     g2d.setColor(this.chartState.getTheme().getColor(ITheme.ChartElement.AXIS_LABEL_BACKGROUND));
/* 123 */     g2d.fill(this.path);
/*     */ 
/* 125 */     g2d.setColor(this.chartState.getTheme().getColor(ITheme.ChartElement.AXIS_LABEL_FOREGROUND));
/* 126 */     g2d.draw(this.path);
/*     */   }
/*     */ 
/*     */   protected void drawMovableLabelText(Graphics g, int curMouseX, String formattedTime, int formattedTimeWidth, int y)
/*     */   {
/* 136 */     g.setColor(this.chartState.getTheme().getColor(ITheme.ChartElement.AXIS_LABEL_FOREGROUND));
/* 137 */     g.drawString(formattedTime, curMouseX - formattedTimeWidth / 2, y);
/*     */   }
/*     */ 
/*     */   protected int getHeight(JComponent jComponent)
/*     */   {
/* 145 */     return jComponent.getHeight();
/*     */   }
/*     */ 
/*     */   protected int getY(JComponent jComponent) {
/* 149 */     return 0;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Charts-5.48.jar
 * Qualified Name:     com.dukascopy.charts.view.drawingstrategies.common.CommonAxisXPanelMovableLabelDrawingStrategy
 * JD-Core Version:    0.6.0
 */