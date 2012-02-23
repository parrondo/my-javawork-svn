/*     */ package com.dukascopy.charts.chartbuilder;
/*     */ 
/*     */ import com.dukascopy.api.OfferSide;
/*     */ import com.dukascopy.api.Period;
/*     */ import com.dukascopy.api.impl.IndicatorWrapper;
/*     */ import com.dukascopy.charts.data.AbstractDataSequenceProvider;
/*     */ import com.dukascopy.charts.data.datacache.Data;
/*     */ import com.dukascopy.charts.mappers.time.GeometryCalculator;
/*     */ import com.dukascopy.charts.mappers.time.ITimeToXMapper;
/*     */ import com.dukascopy.charts.mappers.value.IValueToYMapper;
/*     */ import com.dukascopy.charts.mappers.value.SubValueToYMapper;
/*     */ import com.dukascopy.charts.mappers.value.ValueFrame;
/*     */ import com.dukascopy.charts.math.dataprovider.AbstractDataSequence;
/*     */ import org.slf4j.Logger;
/*     */ import org.slf4j.LoggerFactory;
/*     */ 
/*     */ class OperationManagerStrategy<DataSequenceClass extends AbstractDataSequence<DataClass>, DataClass extends Data>
/*     */   implements IOperationManagerStrategy
/*     */ {
/*  18 */   private static final Logger LOGGER = LoggerFactory.getLogger(OperationManagerStrategy.class);
/*     */   final ChartState chartState;
/*     */   final AbstractDataSequenceProvider<DataSequenceClass, DataClass> dataSequenceProvider;
/*     */   final GeometryCalculator geometryCalculator;
/*     */   final ValueFrame valueFrame;
/*     */   final ITimeToXMapper timeToXMapper;
/*     */   final IValueToYMapper valueToYMapper;
/*     */   final SubValueToYMapper subValueToYMapper;
/*  28 */   float draggingDiffBuffer = 0.0F;
/*     */ 
/*     */   public OperationManagerStrategy(ChartState chartState, AbstractDataSequenceProvider<DataSequenceClass, DataClass> dataSequenceProvider, GeometryCalculator geometryCalculator, ITimeToXMapper timeToXMapper, ValueFrame valueFrame, IValueToYMapper valueToYMapper, SubValueToYMapper subValueToYMapper)
/*     */   {
/*  39 */     this.chartState = chartState;
/*  40 */     this.dataSequenceProvider = dataSequenceProvider;
/*  41 */     this.geometryCalculator = geometryCalculator;
/*  42 */     this.timeToXMapper = timeToXMapper;
/*  43 */     this.valueFrame = valueFrame;
/*  44 */     this.valueToYMapper = valueToYMapper;
/*  45 */     this.subValueToYMapper = subValueToYMapper;
/*     */   }
/*     */ 
/*     */   public boolean zoomIn()
/*     */   {
/*  50 */     boolean increased = this.geometryCalculator.increaseDataUnitWidth();
/*  51 */     if (!increased) {
/*  52 */       return false;
/*     */     }
/*  54 */     setNewSequenceSize();
/*  55 */     calculateGeometry();
/*  56 */     return true;
/*     */   }
/*     */ 
/*     */   public boolean zoomOut()
/*     */   {
/*  61 */     boolean decreased = this.geometryCalculator.decreaseDataUnitWidth();
/*  62 */     if (!decreased) {
/*  63 */       return false;
/*     */     }
/*  65 */     setNewSequenceSize();
/*  66 */     calculateGeometry();
/*  67 */     return true;
/*     */   }
/*     */ 
/*     */   public void zoomToArea(int leftX, int leftY, int rightX, int rightY)
/*     */   {
/*  73 */     if (!setNewSequenceSizeAndTime(leftX, rightX)) {
/*  74 */       return;
/*     */     }
/*     */ 
/*  77 */     setNewMinMax(leftY, rightY);
/*     */ 
/*  79 */     calculateGeometry();
/*     */   }
/*     */ 
/*     */   public void offerSideChanged(OfferSide newOfferSide)
/*     */   {
/*  85 */     this.dataSequenceProvider.setOfferSide(newOfferSide);
/*  86 */     calculateGeometry();
/*     */   }
/*     */ 
/*     */   public void setVerticalChartMovementEnabled(boolean verticalChartMovementEnabled)
/*     */   {
/*  91 */     if (verticalChartMovementEnabled) {
/*  92 */       return;
/*     */     }
/*  94 */     ((AbstractDataSequence)this.dataSequenceProvider.getDataSequence()).calculateMasterDataMinMax();
/*  95 */     calculateGeometry();
/*     */   }
/*     */ 
/*     */   public void scaleMainValueFrame(boolean scaleInOut, int height)
/*     */   {
/* 100 */     scaleValueFrame(this.valueToYMapper, scaleInOut);
/*     */   }
/*     */ 
/*     */   public void scaleSubValueFrame(SubIndicatorGroup subIndicatorGroup, boolean scaleInOut)
/*     */   {
/* 109 */     for (IndicatorWrapper indicatorWrapper : subIndicatorGroup.getSubIndicators())
/* 110 */       scaleValueFrame(this.subValueToYMapper.get(Integer.valueOf(indicatorWrapper.getId())), scaleInOut);
/*     */   }
/*     */ 
/*     */   public boolean scaleTimeFrame(int draggingDiffPx)
/*     */   {
/* 117 */     if (draggingDiffPx < 0) {
/* 118 */       boolean decreased = this.geometryCalculator.decreaseDataUnitWidth();
/* 119 */       if (!decreased)
/* 120 */         return false;
/*     */     }
/* 122 */     else if (draggingDiffPx > 0) {
/* 123 */       boolean increased = this.geometryCalculator.increaseDataUnitWidth();
/* 124 */       if (!increased)
/* 125 */         return false;
/*     */     }
/*     */     else {
/* 128 */       return false;
/*     */     }
/*     */ 
/*     */     try
/*     */     {
/* 133 */       setNewSequenceSize();
/* 134 */       int candlesShiftingDiff = Math.round(this.chartState.getChartShiftHandlerCoordinate() / this.geometryCalculator.getDataUnitWidth());
/* 135 */       if (this.chartState.isChartShiftActive()) {
/* 136 */         this.dataSequenceProvider.shiftToLastTick(candlesShiftingDiff);
/*     */       }
/* 138 */       calculateGeometry();
/*     */     } catch (Exception e) {
/* 140 */       LOGGER.error(e.getMessage(), e);
/*     */     }
/*     */ 
/* 143 */     return true;
/*     */   }
/*     */ 
/*     */   public boolean moveTimeFrame(int draggedFrom, int draggedTo, boolean isNewDragging)
/*     */   {
/* 148 */     int draggingDiff = draggedFrom - draggedTo;
/* 149 */     float direction = Math.signum(draggingDiff);
/* 150 */     int candlesToDrag = getCandlesToDrag(Math.abs(draggingDiff), isNewDragging);
/* 151 */     return shift((int)(direction * candlesToDrag));
/*     */   }
/*     */ 
/*     */   public boolean moveTimeFrame(int unitsMoved)
/*     */   {
/* 156 */     return shift(unitsMoved);
/*     */   }
/*     */ 
/*     */   public void setCustomRange(int dataUnitsBefore, long time, int dataUnitsAfter)
/*     */   {
/* 161 */     this.dataSequenceProvider.setCustomRange(dataUnitsBefore, time, dataUnitsAfter);
/* 162 */     this.geometryCalculator.recalculate(dataUnitsBefore + dataUnitsAfter);
/* 163 */     this.geometryCalculator.calculateDataUnitsCount();
/* 164 */     setNewSequenceSize();
/* 165 */     calculateGeometry();
/*     */   }
/*     */ 
/*     */   public void moveTimeFrame(long startTime, long endTime)
/*     */   {
/* 170 */     if (endTime - startTime == 0L) {
/* 171 */       return;
/*     */     }
/*     */ 
/* 174 */     int newCandlesCount = (int)(Math.abs(startTime - endTime) / this.chartState.getPeriod().getInterval());
/* 175 */     if (newCandlesCount > 2000)
/* 176 */       newCandlesCount = 2000;
/* 177 */     else if (newCandlesCount < 10) {
/* 178 */       newCandlesCount = 10;
/*     */     }
/*     */ 
/* 181 */     this.dataSequenceProvider.setTime(endTime);
/* 182 */     this.geometryCalculator.recalculate(newCandlesCount);
/* 183 */     setNewSequenceSize();
/*     */ 
/* 185 */     calculateGeometry();
/*     */   }
/*     */ 
/*     */   public boolean moveValueFrame(int draggedYFrom, int draggedYTo)
/*     */   {
/* 190 */     if (!this.chartState.isVerticalChartMovementEnabled()) {
/* 191 */       return false;
/*     */     }
/* 193 */     if (Math.abs(draggedYFrom - draggedYTo) == 0) {
/* 194 */       return false;
/*     */     }
/* 196 */     int diff = draggedYTo - draggedYFrom;
/* 197 */     double newMin = this.valueFrame.getMinValue() + diff * this.valueToYMapper.getValuesInOnePixel();
/* 198 */     double newMax = this.valueFrame.getMaxValue() + diff * this.valueToYMapper.getValuesInOnePixel();
/* 199 */     this.valueFrame.changeMinMax(newMin, newMax);
/* 200 */     return true;
/*     */   }
/*     */ 
/*     */   public void shiftChartToFront()
/*     */   {
/*     */     try {
/* 206 */       int candlesShiftingDiff = Math.round(this.chartState.getChartShiftHandlerCoordinate() / this.geometryCalculator.getDataUnitWidth());
/* 207 */       this.dataSequenceProvider.shiftToLastTick(candlesShiftingDiff);
/*     */     } catch (Exception e) {
/* 209 */       LOGGER.error(e.getMessage(), e);
/*     */     }
/* 211 */     calculateGeometry();
/*     */   }
/*     */ 
/*     */   void setNewMinMax(int leftY, int rightY)
/*     */   {
/* 218 */     if (!this.chartState.isVerticalChartMovementEnabled()) {
/* 219 */       return;
/*     */     }
/* 221 */     double min = Math.min(this.valueToYMapper.vy(leftY), this.valueToYMapper.vy(rightY));
/* 222 */     double max = Math.max(this.valueToYMapper.vy(leftY), this.valueToYMapper.vy(rightY));
/* 223 */     this.valueFrame.changeMinMax(min, max);
/* 224 */     this.valueToYMapper.setPadding(0.0D);
/*     */   }
/*     */ 
/*     */   boolean setNewSequenceSizeAndTime(int leftX, int rightX)
/*     */   {
/* 229 */     int candlesCountDiff = Math.abs(leftX - rightX) / this.geometryCalculator.getDataUnitWidth();
/* 230 */     if (candlesCountDiff == 0) {
/* 231 */       return false;
/*     */     }
/*     */ 
/* 234 */     long rightTime = this.timeToXMapper.tx(rightX);
/*     */ 
/* 236 */     int originalDataUnitsCount = this.geometryCalculator.getDataUnitsCount();
/* 237 */     int newDataUnitsCount = this.geometryCalculator.recalculate(candlesCountDiff);
/* 238 */     if (originalDataUnitsCount == newDataUnitsCount) {
/* 239 */       return false;
/*     */     }
/*     */ 
/* 242 */     this.dataSequenceProvider.shiftToLastTick(0);
/* 243 */     this.dataSequenceProvider.setTime(rightTime);
/* 244 */     this.geometryCalculator.calculateDataUnitsCount();
/* 245 */     setNewSequenceSize();
/*     */ 
/* 247 */     return true;
/*     */   }
/*     */   void setNewSequenceSize() {
/* 252 */     int geomCandlesCount = this.geometryCalculator.getDataUnitsCount();
/*     */     int result;
/*     */     try {
/* 255 */       result = this.dataSequenceProvider.setSequenceSize(geomCandlesCount);
/*     */     } catch (Exception ex) {
/* 257 */       result = -1;
/*     */     }
/* 259 */     if (result == -1) {
/* 260 */       this.geometryCalculator.recalculate(this.dataSequenceProvider.getSequenceSize());
/* 261 */       this.geometryCalculator.calculateDataUnitsCount();
/* 262 */     } else if (result != geomCandlesCount) {
/* 263 */       int recalculatedCandlesCount = this.geometryCalculator.recalculate(result);
/* 264 */       this.geometryCalculator.calculateDataUnitsCount();
/* 265 */       this.dataSequenceProvider.setSequenceSize(recalculatedCandlesCount);
/*     */     }
/*     */   }
/*     */ 
/*     */   boolean shift(int candlesToDrag)
/*     */   {
/* 271 */     if (candlesToDrag == 0) {
/* 272 */       return false;
/*     */     }
/*     */ 
/* 275 */     boolean hasShifted = this.dataSequenceProvider.shift(candlesToDrag);
/* 276 */     calculateGeometry();
/* 277 */     return hasShifted;
/*     */   }
/*     */ 
/*     */   int getCandlesToDrag(float draggingDiff, boolean isNewDragging) {
/* 281 */     if (isNewDragging)
/* 282 */       this.draggingDiffBuffer = draggingDiff;
/*     */     else {
/* 284 */       this.draggingDiffBuffer += draggingDiff;
/*     */     }
/*     */ 
/* 287 */     float candlesDragged = Math.abs(this.draggingDiffBuffer / this.geometryCalculator.getDataUnitWidth());
/* 288 */     if (candlesDragged >= 1.0D) {
/* 289 */       int candlesToDrag = (int)candlesDragged;
/* 290 */       this.draggingDiffBuffer %= this.geometryCalculator.getDataUnitWidth();
/* 291 */       return candlesToDrag;
/*     */     }
/* 293 */     return 0;
/*     */   }
/*     */ 
/*     */   void calculateGeometry()
/*     */   {
/* 298 */     AbstractDataSequence dataSequence = (AbstractDataSequence)this.dataSequenceProvider.getDataSequence();
/* 299 */     calculateMainValuesGeometry(dataSequence);
/* 300 */     calculateSubValuesGeometry(dataSequence);
/*     */   }
/*     */ 
/*     */   public double getChartMinPrice()
/*     */   {
/* 305 */     return this.valueFrame.getMinValue();
/*     */   }
/*     */ 
/*     */   public double getChartMaxPrice()
/*     */   {
/* 310 */     return this.valueFrame.getMaxValue();
/*     */   }
/*     */ 
/*     */   void calculateMainValuesGeometry(DataSequenceClass dataSequence) {
/* 314 */     if ((!this.chartState.isVerticalChartMovementEnabled()) && 
/* 315 */       (dataSequence.getMin() > 4.9E-324D) && (dataSequence.getMax() < 1.7976931348623157E+308D))
/*     */     {
/* 317 */       this.valueFrame.changeMinMax(dataSequence.getMin(), dataSequence.getMax());
/*     */     }
/*     */ 
/* 320 */     this.valueToYMapper.computeGeometry();
/*     */   }
/*     */ 
/*     */   void calculateSubValuesGeometry(DataSequenceClass dataSequence) {
/* 324 */     for (Integer id : this.subValueToYMapper.keySet()) {
/* 325 */       if (dataSequence.isFormulasMinMaxEmpty(id)) {
/*     */         continue;
/*     */       }
/* 328 */       double min = dataSequence.getFormulasMinFor(id);
/* 329 */       double max = dataSequence.getFormulasMaxFor(id);
/* 330 */       this.subValueToYMapper.get(id).computeGeometry(min, max);
/*     */     }
/*     */   }
/*     */ 
/*     */   private void scaleValueFrame(IValueToYMapper svalueToYMapper, boolean scaleInOut) {
/* 335 */     if (svalueToYMapper != null) {
/* 336 */       svalueToYMapper.setPadding(svalueToYMapper.getPadding() + (scaleInOut ? 0.01D : -0.01D));
/* 337 */       svalueToYMapper.computeGeometry();
/*     */     }
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Charts-5.48.jar
 * Qualified Name:     com.dukascopy.charts.chartbuilder.OperationManagerStrategy
 * JD-Core Version:    0.6.0
 */