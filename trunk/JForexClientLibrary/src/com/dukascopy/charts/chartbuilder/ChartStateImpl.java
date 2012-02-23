/*     */ package com.dukascopy.charts.chartbuilder;
/*     */ 
/*     */ import com.dukascopy.api.DataType;
/*     */ import com.dukascopy.api.DataType.DataPresentationType;
/*     */ import com.dukascopy.api.Instrument;
/*     */ import com.dukascopy.api.OfferSide;
/*     */ import com.dukascopy.api.Period;
/*     */ import com.dukascopy.api.PriceRange;
/*     */ import com.dukascopy.api.ReversalAmount;
/*     */ import com.dukascopy.api.TickBarSize;
/*     */ import com.dukascopy.charts.ChartProperties;
/*     */ import com.dukascopy.charts.persistence.ITheme;
/*     */ import java.awt.Point;
/*     */ 
/*     */ final class ChartStateImpl
/*     */   implements ChartState
/*     */ {
/*     */   private Instrument instrument;
/*     */   private Period period;
/*     */   private OfferSide offerSide;
/*     */   private ITheme theme;
/*     */   private DataType.DataPresentationType tickType;
/*     */   private DataType.DataPresentationType candleType;
/*     */   private DataType.DataPresentationType priceRangeType;
/*     */   private DataType.DataPresentationType pointAndFigureType;
/*     */   private DataType.DataPresentationType tickBarPresentationType;
/*     */   private DataType.DataPresentationType renkoPresentationType;
/*     */   private DataType dataType;
/*     */   private PriceRange priceRange;
/*     */   private ReversalAmount reversalAmount;
/*     */   private TickBarSize tickBarSize;
/*     */   private int indicatorIdMouseOver;
/*  39 */   private Point mouseCrossCursorPoint = ChartProperties.DEFAULT_MOUSE_CROSS_CURSOR_POINT;
/*  40 */   private boolean isMouseCrossCursorVisible = false;
/*     */ 
/*  42 */   private int chartShiftInPx = 30;
/*  43 */   private boolean isChartShiftActive = true;
/*  44 */   private boolean isVerticalChartMovementEnabled = false;
/*     */   private final boolean isReadOnly;
/*     */ 
/*     */   public ChartStateImpl(Instrument instrument, Period period, OfferSide offerSide, DataType.DataPresentationType tickType, DataType.DataPresentationType candleType, DataType.DataPresentationType priceRangeType, DataType.DataPresentationType pointAndFigureType, DataType.DataPresentationType tickBarPresentationType, DataType.DataPresentationType renkoPresentationType, boolean isReadOnly, DataType dataType, PriceRange priceRange, ReversalAmount reversalAmount, boolean verticalChartMovementEnabled, ITheme theme)
/*     */   {
/*  65 */     this.instrument = instrument;
/*  66 */     this.period = period;
/*  67 */     this.offerSide = offerSide;
/*  68 */     this.isReadOnly = isReadOnly;
/*  69 */     this.tickType = tickType;
/*  70 */     this.candleType = candleType;
/*  71 */     this.priceRangeType = priceRangeType;
/*  72 */     this.pointAndFigureType = pointAndFigureType;
/*  73 */     this.tickBarPresentationType = tickBarPresentationType;
/*  74 */     this.renkoPresentationType = renkoPresentationType;
/*  75 */     this.dataType = dataType;
/*  76 */     this.priceRange = priceRange;
/*  77 */     this.reversalAmount = reversalAmount;
/*  78 */     this.isVerticalChartMovementEnabled = verticalChartMovementEnabled;
/*  79 */     this.theme = theme;
/*     */   }
/*     */ 
/*     */   public Instrument getInstrument()
/*     */   {
/*  84 */     return this.instrument;
/*     */   }
/*     */ 
/*     */   public boolean isTickPeriod() {
/*  88 */     return Period.TICK == this.period;
/*     */   }
/*     */ 
/*     */   public boolean isReadOnly()
/*     */   {
/*  93 */     return this.isReadOnly;
/*     */   }
/*     */ 
/*     */   public boolean isMouseCrossCursorVisible()
/*     */   {
/*  98 */     return this.isMouseCrossCursorVisible;
/*     */   }
/*     */ 
/*     */   public boolean isMouseCursorOnWindow(int subWindowId)
/*     */   {
/* 103 */     return this.indicatorIdMouseOver == subWindowId;
/*     */   }
/*     */ 
/*     */   public Period getPeriod()
/*     */   {
/* 108 */     return this.period;
/*     */   }
/*     */ 
/*     */   public OfferSide getOfferSide()
/*     */   {
/* 113 */     DataType dataType = getDataType();
/* 114 */     switch (1.$SwitchMap$com$dukascopy$api$DataType[dataType.ordinal()]) { case 1:
/* 115 */       return null;
/*     */     case 2:
/* 116 */       return this.offerSide;
/*     */     case 3:
/* 117 */       return this.offerSide;
/*     */     case 4:
/* 118 */       return this.offerSide;
/*     */     case 5:
/* 119 */       return this.offerSide;
/*     */     case 6:
/* 120 */       return this.offerSide; }
/* 121 */     throw new IllegalArgumentException("Unsupported Data Type - " + dataType);
/*     */   }
/*     */ 
/*     */   public DataType.DataPresentationType getTickType()
/*     */   {
/* 127 */     return this.tickType;
/*     */   }
/*     */ 
/*     */   public DataType.DataPresentationType getCandleType()
/*     */   {
/* 132 */     return this.candleType;
/*     */   }
/*     */ 
/*     */   public Point getMouseCursorPoint()
/*     */   {
/* 138 */     return this.mouseCrossCursorPoint;
/*     */   }
/*     */ 
/*     */   public void setPeriod(Period period)
/*     */   {
/* 145 */     this.period = period;
/*     */   }
/*     */ 
/*     */   public void setOfferSide(OfferSide offerSide)
/*     */   {
/* 150 */     this.offerSide = offerSide;
/*     */   }
/*     */ 
/*     */   public void setCandleType(DataType.DataPresentationType selectedLineType)
/*     */   {
/* 155 */     this.candleType = selectedLineType;
/*     */   }
/*     */ 
/*     */   public void setTickType(DataType.DataPresentationType selectedTickType)
/*     */   {
/* 160 */     this.tickType = selectedTickType;
/*     */   }
/*     */ 
/*     */   public void setMouseCursorVisible(boolean isMouseCrossCursorVisible)
/*     */   {
/* 166 */     this.isMouseCrossCursorVisible = isMouseCrossCursorVisible;
/*     */   }
/*     */ 
/*     */   public void changeMouseCursorWindowLocation(int indicatorId)
/*     */   {
/* 171 */     this.indicatorIdMouseOver = indicatorId;
/*     */   }
/*     */ 
/*     */   public void mouseCrossCursorChanged(Point mousePoint)
/*     */   {
/* 176 */     this.mouseCrossCursorPoint = mousePoint;
/*     */   }
/*     */ 
/*     */   public int getChartShiftHandlerCoordinate()
/*     */   {
/* 183 */     return this.chartShiftInPx;
/*     */   }
/*     */ 
/*     */   public void setChartShiftHandlerCoordinate(int chartShiftHandlerCoordinates)
/*     */   {
/* 188 */     if (chartShiftHandlerCoordinates < 0)
/* 189 */       this.chartShiftInPx = 0;
/*     */     else
/* 191 */       this.chartShiftInPx = chartShiftHandlerCoordinates;
/*     */   }
/*     */ 
/*     */   public boolean isChartShiftActive()
/*     */   {
/* 197 */     return this.isChartShiftActive;
/*     */   }
/*     */ 
/*     */   public void setChartShiftActive(boolean isActive)
/*     */   {
/* 202 */     if (this.isChartShiftActive == isActive) {
/* 203 */       return;
/*     */     }
/* 205 */     this.isChartShiftActive = isActive;
/*     */   }
/*     */ 
/*     */   public boolean isVerticalChartMovementEnabled()
/*     */   {
/* 210 */     return this.isVerticalChartMovementEnabled;
/*     */   }
/*     */ 
/*     */   public void setVerticalChartMovementEnabled(boolean verticalChartMovementEnabled)
/*     */   {
/* 216 */     this.isVerticalChartMovementEnabled = verticalChartMovementEnabled;
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/* 221 */     return "ChartStateImpl{" + "period=" + this.period + ", offerSide=" + this.offerSide + ", tickType=" + this.tickType + ", candleType=" + this.candleType + ", indicatorIdMouseOver=" + this.indicatorIdMouseOver + ", mouseCrossCursorPoint=" + this.mouseCrossCursorPoint + ", isMouseCrossCursorVisible=" + this.isMouseCrossCursorVisible + ", chartShiftInPx=" + this.chartShiftInPx + ", isChartShiftActive=" + this.isChartShiftActive + ", isVerticalChartMovementEnabled=" + this.isVerticalChartMovementEnabled + "}";
/*     */   }
/*     */ 
/*     */   public void setInstrument(Instrument instrument)
/*     */   {
/* 238 */     this.instrument = instrument;
/*     */   }
/*     */ 
/*     */   public DataType.DataPresentationType getPriceRangesPresentationType()
/*     */   {
/* 243 */     return this.priceRangeType;
/*     */   }
/*     */ 
/*     */   public void setPriceRangesPresentationType(DataType.DataPresentationType priceRangeType)
/*     */   {
/* 248 */     this.priceRangeType = priceRangeType;
/*     */   }
/*     */ 
/*     */   public DataType getDataType()
/*     */   {
/* 253 */     return this.dataType;
/*     */   }
/*     */ 
/*     */   public void setDataType(DataType dataType)
/*     */   {
/* 258 */     this.dataType = dataType;
/*     */   }
/*     */ 
/*     */   public PriceRange getPriceRange()
/*     */   {
/* 263 */     return this.priceRange;
/*     */   }
/*     */ 
/*     */   public void setPriceRange(PriceRange priceRange)
/*     */   {
/* 268 */     this.priceRange = priceRange;
/*     */   }
/*     */ 
/*     */   public DataType.DataPresentationType getPointAndFigurePresentationType()
/*     */   {
/* 273 */     return this.pointAndFigureType;
/*     */   }
/*     */ 
/*     */   public void setPointAndFigurePresentationType(DataType.DataPresentationType pointAndFigurePresentationType)
/*     */   {
/* 278 */     this.pointAndFigureType = pointAndFigurePresentationType;
/*     */   }
/*     */ 
/*     */   public ReversalAmount getReversalAmount()
/*     */   {
/* 284 */     return this.reversalAmount;
/*     */   }
/*     */ 
/*     */   public void setReversalAmount(ReversalAmount reversalAmount)
/*     */   {
/* 289 */     this.reversalAmount = reversalAmount;
/*     */   }
/*     */ 
/*     */   public DataType.DataPresentationType getTickBarPresentationType()
/*     */   {
/* 295 */     return this.tickBarPresentationType;
/*     */   }
/*     */ 
/*     */   public void setTickBarPresentationType(DataType.DataPresentationType tickBarPresentationType)
/*     */   {
/* 300 */     this.tickBarPresentationType = tickBarPresentationType;
/*     */   }
/*     */ 
/*     */   public TickBarSize getTickBarSize()
/*     */   {
/* 305 */     return this.tickBarSize;
/*     */   }
/*     */ 
/*     */   public void setTickBarSize(TickBarSize tickBarSize)
/*     */   {
/* 310 */     this.tickBarSize = tickBarSize;
/*     */   }
/*     */ 
/*     */   public ITheme getTheme()
/*     */   {
/* 315 */     return this.theme;
/*     */   }
/*     */ 
/*     */   public void setTheme(ITheme theme) {
/* 319 */     this.theme = theme;
/*     */   }
/*     */ 
/*     */   public DataType.DataPresentationType getRenkoPresentationType()
/*     */   {
/* 324 */     return this.renkoPresentationType;
/*     */   }
/*     */ 
/*     */   public void setRenkoPresentationType(DataType.DataPresentationType renkoPresentationType)
/*     */   {
/* 329 */     this.renkoPresentationType = renkoPresentationType;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Charts-5.48.jar
 * Qualified Name:     com.dukascopy.charts.chartbuilder.ChartStateImpl
 * JD-Core Version:    0.6.0
 */