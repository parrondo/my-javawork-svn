/*     */ package com.dukascopy.charts.view.drawingstrategies.common;
/*     */ 
/*     */ import com.dukascopy.api.Period;
/*     */ import com.dukascopy.charts.chartbuilder.ChartState;
/*     */ import com.dukascopy.charts.data.AbstractDataSequenceProvider;
/*     */ import com.dukascopy.charts.data.datacache.Data;
/*     */ import com.dukascopy.charts.mappers.time.ITimeToXMapper;
/*     */ import com.dukascopy.charts.math.dataprovider.AbstractDataSequence;
/*     */ import com.dukascopy.charts.persistence.ITheme;
/*     */ import com.dukascopy.charts.persistence.ITheme.ChartElement;
/*     */ import com.dukascopy.charts.persistence.ITheme.TextElement;
/*     */ import com.dukascopy.charts.utils.formatter.DateFormatter;
/*     */ import com.dukascopy.charts.view.displayabledatapart.IDrawingStrategy;
/*     */ import java.awt.Color;
/*     */ import java.awt.Font;
/*     */ import java.awt.FontMetrics;
/*     */ import java.awt.Graphics;
/*     */ import java.awt.Graphics2D;
/*     */ import java.awt.geom.GeneralPath;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Calendar;
/*     */ import java.util.List;
/*     */ import java.util.TimeZone;
/*     */ import javax.swing.JComponent;
/*     */ 
/*     */ public class CommonAxisXPanelGridDrawingStrategy<DataSequenceClass extends AbstractDataSequence<DataClass>, DataClass extends Data>
/*     */   implements IDrawingStrategy
/*     */ {
/*  30 */   final Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT 0"));
/*     */   protected final ChartState chartState;
/*     */   final ITimeToXMapper timeToXMapper;
/*     */   protected final AbstractDataSequenceProvider<DataSequenceClass, DataClass> dataSequenceProvider;
/*     */   final DateFormatter dateFormatter;
/*  38 */   protected final GeneralPath path = new GeneralPath();
/*     */ 
/*     */   public CommonAxisXPanelGridDrawingStrategy(ChartState chartState, ITimeToXMapper timeToXMapper, AbstractDataSequenceProvider<DataSequenceClass, DataClass> dataSequenceProvider, DateFormatter dateFormatter)
/*     */   {
/*  46 */     this.chartState = chartState;
/*  47 */     this.timeToXMapper = timeToXMapper;
/*  48 */     this.dataSequenceProvider = dataSequenceProvider;
/*  49 */     this.dateFormatter = dateFormatter;
/*     */   }
/*     */ 
/*     */   public void draw(Graphics g, JComponent jComponent) {
/*  53 */     Font prevFont = g.getFont();
/*  54 */     Color prevColor = g.getColor();
/*     */ 
/*  56 */     g.setColor(this.chartState.getTheme().getColor(ITheme.ChartElement.AXIS_PANEL_BACKGROUND));
/*  57 */     g.fillRect(0, 0, jComponent.getWidth(), jComponent.getHeight());
/*     */ 
/*  59 */     g.setFont(this.chartState.getTheme().getFont(ITheme.TextElement.AXIS));
/*  60 */     g.setColor(this.chartState.getTheme().getColor(ITheme.ChartElement.AXIS_PANEL_FOREGROUND));
/*     */ 
/*  62 */     this.calendar.setTimeInMillis(this.timeToXMapper.tx(0) + 1000L);
/*  63 */     int leftYear = this.calendar.get(1);
/*  64 */     FontMetrics fontMetrics = g.getFontMetrics();
/*  65 */     int leftYearWidth = fontMetrics.stringWidth(Integer.toString(leftYear));
/*     */ 
/*  67 */     List coordinates = drawLines((Graphics2D)g, jComponent, leftYearWidth);
/*  68 */     drawLabels(g, jComponent, coordinates, leftYear, fontMetrics);
/*     */ 
/*  70 */     g.setColor(prevColor);
/*  71 */     g.setFont(prevFont);
/*     */   }
/*     */ 
/*     */   void drawLabels(Graphics g, JComponent jComponent, List<Integer> coordinates, int leftYear, FontMetrics fontMetrics) {
/*  75 */     int width = jComponent.getWidth();
/*  76 */     int height = getTimePanelHeight(jComponent);
/*     */ 
/*  78 */     Data data = ((AbstractDataSequence)this.dataSequenceProvider.getDataSequence()).getLastData();
/*  79 */     if (data != null) {
/*  80 */       String lastTickTime = this.dateFormatter.formatRightTime(data.time);
/*  81 */       g.drawString(lastTickTime, width - fontMetrics.stringWidth(lastTickTime), height - 2);
/*     */     }
/*     */ 
/*  84 */     if ((leftYear > 1800) && (leftYear < 3000)) {
/*  85 */       g.drawString(Integer.toString(leftYear), 2, height - 2);
/*     */     }
/*     */ 
/*  88 */     int prevYear = leftYear;
/*  89 */     for (int i = coordinates.size() - 1; i > 0; i--)
/*     */     {
/*  91 */       int coordinate = ((Integer)coordinates.get(i)).intValue();
/*  92 */       long timeToFormat = this.timeToXMapper.tx(coordinate);
/*  93 */       if (timeToFormat == -1L)
/*     */       {
/*     */         continue;
/*     */       }
/*  97 */       String formattedTime = this.dateFormatter.formatTime(timeToFormat);
/*     */ 
/*  99 */       if ((this.chartState.getPeriod() != Period.MONTHLY) && (this.chartState.getPeriod() != Period.TICK)) {
/* 100 */         this.calendar.setTimeInMillis(timeToFormat + 1000L);
/* 101 */         int newYear = this.calendar.get(1);
/* 102 */         if (newYear > prevYear) {
/* 103 */           formattedTime = formattedTime + " " + newYear;
/* 104 */           prevYear = newYear;
/*     */         }
/*     */       }
/*     */ 
/* 108 */       g.drawString(formattedTime, coordinate + 2, height - 2);
/*     */     }
/*     */   }
/*     */ 
/*     */   List<Integer> drawLines(Graphics2D g2d, JComponent jComponent, int leftYearWidth)
/*     */   {
/* 116 */     this.path.reset();
/*     */ 
/* 118 */     int width = jComponent.getWidth();
/* 119 */     int height = getTimePanelHeight(jComponent);
/* 120 */     int y = getTimePanelY(jComponent);
/*     */ 
/* 126 */     int axisYWidth = getAxisYWidth(g2d);
/*     */ 
/* 128 */     int totalPx = width - axisYWidth;
/* 129 */     int step = 90;
/*     */ 
/* 131 */     List coordinates = new ArrayList(totalPx / step);
/*     */ 
/* 133 */     for (int px = totalPx - 1; px > 0; px -= step) {
/* 134 */       if (px > leftYearWidth) {
/* 135 */         this.path.moveTo(px, y);
/* 136 */         this.path.lineTo(px, height - 1);
/* 137 */         coordinates.add(Integer.valueOf(px));
/*     */       }
/*     */     }
/*     */ 
/* 141 */     g2d.draw(this.path);
/*     */ 
/* 143 */     return coordinates;
/*     */   }
/*     */ 
/*     */   protected int getAxisYWidth(Graphics g) {
/* 147 */     int axisYWidth = g.getFontMetrics().stringWidth("12345.67890");
/* 148 */     return axisYWidth;
/*     */   }
/*     */ 
/*     */   protected int getTimePanelHeight(JComponent jComponent) {
/* 152 */     return jComponent.getHeight();
/*     */   }
/*     */ 
/*     */   protected int getTimePanelY(JComponent jComponent) {
/* 156 */     return 0;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Charts-5.48.jar
 * Qualified Name:     com.dukascopy.charts.view.drawingstrategies.common.CommonAxisXPanelGridDrawingStrategy
 * JD-Core Version:    0.6.0
 */