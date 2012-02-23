/*     */ package com.dukascopy.charts.main.nulls;
/*     */ 
/*     */ import com.dukascopy.api.DataType;
/*     */ import com.dukascopy.api.DataType.DataPresentationType;
/*     */ import com.dukascopy.api.IChart;
/*     */ import com.dukascopy.api.IChart.Type;
/*     */ import com.dukascopy.api.IChartObject;
/*     */ import com.dukascopy.api.Instrument;
/*     */ import com.dukascopy.api.OfferSide;
/*     */ import com.dukascopy.api.Period;
/*     */ import com.dukascopy.api.PriceRange;
/*     */ import com.dukascopy.api.ReversalAmount;
/*     */ import com.dukascopy.api.TickBarSize;
/*     */ import com.dukascopy.api.drawings.IChartObjectFactory;
/*     */ import com.dukascopy.api.indicators.IIndicator;
/*     */ import com.dukascopy.api.indicators.OutputParameterInfo.DrawingStyle;
/*     */ import java.awt.Color;
/*     */ import java.awt.Font;
/*     */ import java.awt.image.BufferedImage;
/*     */ import java.util.Collections;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ 
/*     */ public class NullIChart
/*     */   implements IChart
/*     */ {
/*     */   String description;
/*     */ 
/*     */   public IChartObject draw(String key, IChart.Type type, long time1, double price1, long time2, double price2, long time3, double price3)
/*     */   {
/*  32 */     return new NullIChartObject();
/*     */   }
/*     */ 
/*     */   public IChartObject draw(String key, IChart.Type type, long time1, double price1, long time2, double price2)
/*     */   {
/*  38 */     return new NullIChartObject();
/*     */   }
/*     */ 
/*     */   public IChartObject draw(String key, IChart.Type type, long time1, double price1)
/*     */   {
/*  43 */     return new NullIChartObject();
/*     */   }
/*     */ 
/*     */   public IChartObject drawUnlocked(String key, IChart.Type type, long time1, double price1, long time2, double price2, long time3, double price3)
/*     */   {
/*  49 */     return new NullIChartObject();
/*     */   }
/*     */ 
/*     */   public IChartObject drawUnlocked(String key, IChart.Type type, long time1, double price1, long time2, double price2)
/*     */   {
/*  55 */     return new NullIChartObject();
/*     */   }
/*     */ 
/*     */   public IChartObject drawUnlocked(String key, IChart.Type type, long time1, double price1)
/*     */   {
/*  61 */     return new NullIChartObject();
/*     */   }
/*     */ 
/*     */   public void move(IChartObject objectToMove, long newTime, double newPrice)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void move(String chartObjectKey, long newTime, double newPrice)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void comment(String comment)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void setCommentColor(Color color)
/*     */   {
/*     */   }
/*     */ 
/*     */   public Color getCommentColor()
/*     */   {
/*  82 */     return null;
/*     */   }
/*     */ 
/*     */   public Font getCommentFont()
/*     */   {
/*  87 */     return null;
/*     */   }
/*     */ 
/*     */   public void setCommentFont(Font font)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void setCommentHorizontalPosition(int position)
/*     */   {
/*     */   }
/*     */ 
/*     */   public int getCommentHorizontalPosition()
/*     */   {
/* 100 */     return 2;
/*     */   }
/*     */ 
/*     */   public void setCommentVerticalPosition(int position)
/*     */   {
/*     */   }
/*     */ 
/*     */   public int getCommentVerticalPosition()
/*     */   {
/* 109 */     return 1;
/*     */   }
/*     */ 
/*     */   public IChartObject get(String key)
/*     */   {
/* 114 */     return new NullIChartObject();
/*     */   }
/*     */ 
/*     */   public IChartObject remove(String key)
/*     */   {
/* 119 */     return new NullIChartObject();
/*     */   }
/*     */ 
/*     */   public List<IChartObject> getAll()
/*     */   {
/* 124 */     return Collections.emptyList();
/*     */   }
/*     */ 
/*     */   public void removeAll()
/*     */   {
/*     */   }
/*     */ 
/*     */   public int size()
/*     */   {
/* 133 */     return 0;
/*     */   }
/*     */ 
/*     */   public double priceMin(int index)
/*     */   {
/* 138 */     return 1.0D;
/*     */   }
/*     */ 
/*     */   public double priceMax(int index)
/*     */   {
/* 143 */     return 2.0D;
/*     */   }
/*     */ 
/*     */   public int getBarsCount()
/*     */   {
/* 148 */     return 0;
/*     */   }
/*     */ 
/*     */   public int windowsTotal()
/*     */   {
/* 153 */     return 1;
/*     */   }
/*     */ 
/*     */   public Instrument getInstrument()
/*     */   {
/* 158 */     return null;
/*     */   }
/*     */ 
/*     */   public Period getSelectedPeriod()
/*     */   {
/* 163 */     return null;
/*     */   }
/*     */ 
/*     */   public OfferSide getSelectedOfferSide()
/*     */   {
/* 168 */     return null;
/*     */   }
/*     */ 
/*     */   public Iterator<IChartObject> iterator()
/*     */   {
/* 173 */     return new NullIChartIterator();
/*     */   }
/*     */ 
/*     */   public void setInstrument(Instrument instrument)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void addIndicator(IIndicator indicator)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void addSubIndicator(Integer subChartId, IIndicator indicator)
/*     */   {
/*     */   }
/*     */ 
/*     */   public List<IIndicator> getIndicators()
/*     */   {
/* 195 */     return null;
/*     */   }
/*     */ 
/*     */   public void removeIndicator(IIndicator indicator)
/*     */   {
/*     */   }
/*     */ 
/*     */   public DataType getDataType()
/*     */   {
/* 206 */     return null;
/*     */   }
/*     */ 
/*     */   public PriceRange getPriceRange()
/*     */   {
/* 211 */     return null;
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/* 216 */     if ((this.description != null) && (!this.description.isEmpty())) {
/* 217 */       return getDescription();
/*     */     }
/* 219 */     return super.toString();
/*     */   }
/*     */ 
/*     */   public String getDescription() {
/* 223 */     return this.description;
/*     */   }
/*     */ 
/*     */   public void setDescription(String description) {
/* 227 */     this.description = description;
/*     */   }
/*     */ 
/*     */   public void addIndicator(IIndicator indicator, Object[] optParams)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void addIndicator(IIndicator indicator, Object[] optParams, Color[] outputColors, OutputParameterInfo.DrawingStyle[] outputDrawingStyles, int[] outputWidths)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void addIndicator(IIndicator indicator, Object[] optParams, Color[] outputColors, OutputParameterInfo.DrawingStyle[] outputDrawingStyles, int[] outputWidths, int[] outputShifts)
/*     */   {
/*     */   }
/*     */ 
/*     */   public ReversalAmount getReversalAmount()
/*     */   {
/* 254 */     return null;
/*     */   }
/*     */ 
/*     */   public void repaint()
/*     */   {
/*     */   }
/*     */ 
/*     */   public IChartObjectFactory getChartObjectFactory()
/*     */   {
/* 265 */     return null;
/*     */   }
/*     */ 
/*     */   public <T extends IChartObject> void addToMainChart(T object)
/*     */   {
/*     */   }
/*     */ 
/*     */   public <T extends IChartObject> void addToSubChart(Integer subChartId, int indicatorId, T object)
/*     */   {
/*     */   }
/*     */ 
/*     */   public <T extends IChartObject> void addToMainChartUnlocked(T object)
/*     */   {
/*     */   }
/*     */ 
/*     */   public TickBarSize getTickBarSize()
/*     */   {
/* 289 */     return null;
/*     */   }
/*     */ 
/*     */   public void remove(IChartObject chartObject)
/*     */   {
/*     */   }
/*     */ 
/*     */   public Boolean isChartObjectUnlocked(IChartObject chartObject)
/*     */   {
/* 301 */     return null;
/*     */   }
/*     */ 
/*     */   public List<IChartObject> getStrategyChartObjects()
/*     */   {
/* 307 */     return null;
/*     */   }
/*     */ 
/*     */   public List<IChartObject> remove(List<IChartObject> chartObjects)
/*     */   {
/* 313 */     return null;
/*     */   }
/*     */ 
/*     */   public BufferedImage getImage()
/*     */   {
/* 322 */     return null;
/*     */   }
/*     */ 
/*     */   public void setVerticalAxisScale(double minPriceValue, double maxPriceValue)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void setVerticalAutoscale(boolean autoscale)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void setDataPresentationType(DataType.DataPresentationType presentationType)
/*     */   {
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Charts-5.48.jar
 * Qualified Name:     com.dukascopy.charts.main.nulls.NullIChart
 * JD-Core Version:    0.6.0
 */