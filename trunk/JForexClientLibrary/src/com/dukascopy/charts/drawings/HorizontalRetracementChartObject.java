/*     */ package com.dukascopy.charts.drawings;
/*     */ 
/*     */ import com.dukascopy.api.IChart.Type;
/*     */ import com.dukascopy.api.IChartObject.ATTR_COLOR;
/*     */ import com.dukascopy.api.IChartObject.ATTR_DOUBLE;
/*     */ import com.dukascopy.api.IChartObject.ATTR_INT;
/*     */ import com.dukascopy.api.IChartObject.ATTR_TEXT;
/*     */ import com.dukascopy.charts.ChartProperties;
/*     */ import com.dukascopy.charts.mappers.IMapper;
/*     */ import com.dukascopy.charts.utils.GraphicHelper;
/*     */ import com.dukascopy.charts.utils.formatter.FormattersManager;
/*     */ import com.dukascopy.charts.utils.formatter.ValueFormatter;
/*     */ import java.awt.Color;
/*     */ import java.awt.FontMetrics;
/*     */ import java.awt.Graphics;
/*     */ import java.awt.Graphics2D;
/*     */ import java.awt.Point;
/*     */ import java.awt.Rectangle;
/*     */ import java.awt.geom.GeneralPath;
/*     */ import java.awt.geom.Line2D.Double;
/*     */ import java.awt.geom.Line2D.Float;
/*     */ import java.awt.geom.Rectangle2D;
/*     */ import java.awt.geom.Rectangle2D.Float;
/*     */ import java.io.IOException;
/*     */ import java.io.ObjectInputStream;
/*     */ import java.text.DecimalFormat;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import java.util.List;
/*     */ 
/*     */ abstract class HorizontalRetracementChartObject extends AbstractLeveledChartObject
/*     */ {
/*     */   private static final long serialVersionUID = 2L;
/*     */   protected static final int LABEL_OFFSET = 2;
/*  29 */   private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("0.00");
/*     */   private List<Object[]> levels;
/*  32 */   List<Line2D.Double> lines = new ArrayList();
/*     */   private transient GeneralPath path;
/*  35 */   private transient boolean isPoint1Selected = false;
/*  36 */   private transient boolean isPoint2Selected = false;
/*  37 */   private transient int selectedLevelLineIndx = -1;
/*     */ 
/*     */   protected HorizontalRetracementChartObject(String key, IChart.Type type) {
/*  40 */     super(key, type);
/*     */   }
/*     */ 
/*     */   protected HorizontalRetracementChartObject(IChart.Type type) {
/*  44 */     super(null, type);
/*     */   }
/*     */ 
/*     */   public HorizontalRetracementChartObject(String key, IChart.Type type, long time1, double price1, long time2, double price2) {
/*  48 */     super(key, type);
/*     */ 
/*  50 */     this.times[0] = time1;
/*  51 */     this.prices[0] = price1;
/*  52 */     this.times[1] = time2;
/*  53 */     this.prices[1] = price2;
/*     */   }
/*     */ 
/*     */   protected HorizontalRetracementChartObject(HorizontalRetracementChartObject chartObject) {
/*  57 */     super(chartObject);
/*     */ 
/*  59 */     this.lines = new ArrayList(chartObject.lines);
/*     */   }
/*     */ 
/*     */   protected void drawChartObject(Graphics g, IMapper mapper, Rectangle labelDimension, Rectangle textDimension, FormattersManager formattersManager, ChartObjectDrawingMode drawingMode)
/*     */   {
/*  64 */     GeneralPath drawingPath = getPath();
/*  65 */     drawingPath.reset();
/*     */ 
/*  67 */     this.lines.clear();
/*     */ 
/*  69 */     int x1 = mapper.xt(this.times[0]);
/*  70 */     int x2 = mapper.xt(this.times[1]);
/*     */ 
/*  72 */     if (!isValidTime(1)) {
/*  73 */       x2 = x1;
/*     */     }
/*     */ 
/*  76 */     drawLevels((Graphics2D)g, mapper, formattersManager.getValueFormatter(), drawingPath, Math.min(x1, x2), mapper.getWidth(), this.prices[0], this.prices[1]);
/*     */   }
/*     */ 
/*     */   protected GeneralPath getPath()
/*     */   {
/*  89 */     if (this.path == null) {
/*  90 */       this.path = new GeneralPath();
/*     */     }
/*  92 */     return this.path;
/*     */   }
/*     */ 
/*     */   protected void drawSpecificHandlers(Graphics g, IMapper mapper)
/*     */   {
/*  97 */     if (!isDrawable(mapper)) {
/*  98 */       return;
/*     */     }
/*     */ 
/* 101 */     int x1 = mapper.xt(this.times[0]);
/* 102 */     int x2 = mapper.xt(this.times[1]);
/* 103 */     int y1 = mapper.yv(this.prices[0]);
/* 104 */     int y2 = mapper.yv(this.prices[1]);
/*     */ 
/* 106 */     if (!isValidTime(1)) {
/* 107 */       x2 = x1;
/*     */     }
/*     */ 
/* 110 */     if (!isValidPrice(1)) {
/* 111 */       y2 = y1;
/*     */     }
/*     */ 
/* 114 */     GraphicHelper.drawSegmentDashedLine(g, x1, y1, x2, y2, 0.001D, 5.0D, g.getColor(), mapper.getWidth(), mapper.getHeight());
/*     */   }
/*     */ 
/*     */   public void modifyNewDrawing(Point point, IMapper mapper, int range)
/*     */   {
/* 119 */     if (!isValidPoint(0)) {
/* 120 */       return;
/*     */     }
/*     */ 
/* 123 */     this.times[1] = mapper.tx(point.x);
/* 124 */     this.prices[1] = mapper.vy(point.y);
/*     */   }
/*     */ 
/*     */   public void modifyEditedDrawing(Point point, Point prevPoint, IMapper mapper, int range)
/*     */   {
/* 129 */     long newTime = mapper.tx(point.x);
/* 130 */     double newPrice = mapper.vy(point.y);
/*     */ 
/* 132 */     if (this.isPoint2Selected) {
/* 133 */       this.times[1] = newTime;
/* 134 */       this.prices[1] = newPrice;
/*     */     }
/* 136 */     else if (this.isPoint1Selected) {
/* 137 */       this.times[0] = newTime;
/* 138 */       this.prices[0] = newPrice;
/*     */     }
/* 140 */     else if (this.selectedLevelLineIndx != -1) {
/* 141 */       double oldLevelValue = ((Double)((Object[])getLevels().get(this.selectedLevelLineIndx))[1]).doubleValue();
/* 142 */       double newLevelValue = calculateNewValue(mapper, point);
/* 143 */       newLevelValue = performLevelValueCorrections(oldLevelValue, newLevelValue);
/* 144 */       ((Object[])getLevels().get(this.selectedLevelLineIndx))[1] = Double.valueOf(newLevelValue);
/*     */     } else {
/* 146 */       if (prevPoint == null) {
/* 147 */         return;
/*     */       }
/*     */ 
/* 150 */       long timeDiff = newTime - mapper.tx(prevPoint.x);
/* 151 */       double priceDiff = newPrice - mapper.vy(prevPoint.y);
/*     */ 
/* 153 */       this.times[0] += timeDiff;
/* 154 */       this.prices[0] += priceDiff;
/* 155 */       this.times[1] += timeDiff;
/* 156 */       this.prices[1] += priceDiff;
/*     */     }
/*     */   }
/*     */ 
/*     */   private double performLevelValueCorrections(double oldLevelValue, double newLevelValue) {
/* 161 */     double MIN_OFFSET = 0.0001D;
/* 162 */     double MIN_VALUE_PERCENT = ChartProperties.MIN_LEVEL_VALUE.doubleValue() / 100.0D;
/* 163 */     double MAX_VALUE_PERCENT = ChartProperties.MAX_LEVEL_VALUE.doubleValue() / 100.0D;
/*     */ 
/* 165 */     if (newLevelValue < MIN_VALUE_PERCENT) {
/* 166 */       newLevelValue = MIN_VALUE_PERCENT;
/*     */     }
/* 168 */     else if (newLevelValue > MAX_VALUE_PERCENT) {
/* 169 */       newLevelValue = MAX_VALUE_PERCENT;
/*     */     }
/* 171 */     else if (isLevelValuePerformingRestricted(newLevelValue)) {
/* 172 */       if (oldLevelValue > newLevelValue)
/* 173 */         newLevelValue -= 0.0001D;
/*     */       else {
/* 175 */         newLevelValue += 0.0001D;
/*     */       }
/*     */     }
/*     */ 
/* 179 */     return newLevelValue;
/*     */   }
/*     */ 
/*     */   public List<Point> getHandlerMiddlePoints(IMapper mapper)
/*     */   {
/* 184 */     List handlerMiddlePoints = getHandlerMiddlePoints();
/*     */ 
/* 186 */     handlerMiddlePoints.add(new Point(mapper.xt(this.times[0]), mapper.yv(this.prices[0])));
/* 187 */     for (int i = 0; i < this.lines.size(); i++) {
/* 188 */       Line2D.Double line = (Line2D.Double)this.lines.get(i);
/*     */ 
/* 190 */       if (isLinePerformingRestricted(mapper, line))
/*     */       {
/*     */         continue;
/*     */       }
/* 194 */       handlerMiddlePoints.add(new Point((int)(line.x1 + line.x2) / 2, (int)line.y1));
/*     */     }
/* 196 */     handlerMiddlePoints.add(new Point(mapper.xt(this.times[1]), mapper.yv(this.prices[1])));
/* 197 */     return handlerMiddlePoints;
/*     */   }
/*     */ 
/*     */   public boolean intersects(Point point, IMapper mapper, int range)
/*     */   {
/* 202 */     Line2D.Float handlerDiagonalLine = createHandlerDiagonalLine(mapper);
/* 203 */     Rectangle2D.Float pointerRectangle = new Rectangle2D.Float(point.x - range, point.y - range, range * 2, range * 2);
/* 204 */     return (handlerDiagonalLine.intersects(pointerRectangle)) || (GraphicHelper.intersects(getPath(), pointerRectangle));
/*     */   }
/*     */ 
/*     */   private Line2D.Float createHandlerDiagonalLine(IMapper mapper) {
/* 208 */     int x1 = mapper.xt(this.times[0]);
/* 209 */     int x2 = mapper.xt(this.times[1]);
/* 210 */     int y1 = mapper.yv(this.prices[0]);
/* 211 */     int y2 = mapper.yv(this.prices[1]);
/* 212 */     return new Line2D.Float(x1, y1, x2, y2);
/*     */   }
/*     */ 
/*     */   public void updateSelectedHandler(Point point, IMapper mapper, int range)
/*     */   {
/* 217 */     Rectangle2D.Float pointRect = new Rectangle2D.Float(point.x - range, point.y - range, range * 2, range * 2);
/* 218 */     if (isBottomRightPointSelected(mapper, range, pointRect)) {
/* 219 */       this.isPoint2Selected = true;
/* 220 */       this.isPoint1Selected = false;
/* 221 */       this.selectedLevelLineIndx = -1;
/*     */     }
/* 223 */     else if (isUpperLeftPointSelected(mapper, range, pointRect)) {
/* 224 */       this.isPoint1Selected = true;
/* 225 */       this.isPoint2Selected = false;
/* 226 */       this.selectedLevelLineIndx = -1;
/*     */     }
/* 228 */     else if ((this.selectedLevelLineIndx = isLevelLineHandlerSelected(mapper, pointRect)) != -1) {
/* 229 */       this.isPoint1Selected = false;
/* 230 */       this.isPoint2Selected = false;
/*     */     } else {
/* 232 */       this.isPoint1Selected = false;
/* 233 */       this.isPoint2Selected = false;
/* 234 */       this.selectedLevelLineIndx = -1;
/*     */     }
/*     */   }
/*     */ 
/*     */   public int getPointsCount()
/*     */   {
/* 240 */     return 2;
/*     */   }
/*     */ 
/*     */   public int getAttrInt(IChartObject.ATTR_INT field)
/*     */   {
/* 245 */     switch (1.$SwitchMap$com$dukascopy$api$IChartObject$ATTR_INT[field.ordinal()]) {
/*     */     case 1:
/* 247 */       return getLevels().size();
/*     */     }
/*     */ 
/* 250 */     return super.getAttrInt(field);
/*     */   }
/*     */ 
/*     */   public void setAttrInt(IChartObject.ATTR_INT field, int value)
/*     */   {
/* 255 */     switch (1.$SwitchMap$com$dukascopy$api$IChartObject$ATTR_INT[field.ordinal()]) {
/*     */     case 1:
/* 257 */       if ((value < 1) || (value > 32)) {
/* 258 */         throw new IllegalArgumentException("Illegal levels count : " + value);
/*     */       }
/* 260 */       if (value < getLevels().size()) {
/* 261 */         setLevels(getLevels().subList(0, value - 1));
/*     */       } else {
/* 263 */         Object[][] levelsArray = new Object[value][3];
/* 264 */         for (int i = 0; i < value; i++) {
/* 265 */           if (i < getLevels().size())
/* 266 */             levelsArray[i] = ((Object[])getLevels().get(i));
/*     */           else {
/* 268 */             levelsArray[i] = { null, Double.valueOf(-1.0D), null };
/*     */           }
/*     */         }
/* 271 */         setLevels(Arrays.asList(levelsArray));
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 276 */     super.setAttrInt(field, value);
/*     */   }
/*     */ 
/*     */   public Color getAttrColor(IChartObject.ATTR_COLOR field)
/*     */   {
/* 281 */     int levelIndex = getLevelIndex(field);
/* 282 */     if (levelIndex >= 1) {
/* 283 */       if (levelIndex > getLevels().size()) {
/* 284 */         throw new IllegalArgumentException("Level index out of bounds : " + getLevels().size() + ". Set level's size first.");
/*     */       }
/*     */ 
/* 287 */       return (Color)((Object[])getLevels().get(levelIndex - 1))[2];
/*     */     }
/*     */ 
/* 290 */     return super.getAttrColor(field);
/*     */   }
/*     */ 
/*     */   public void setAttrColor(IChartObject.ATTR_COLOR field, Color value)
/*     */   {
/* 295 */     int levelIndex = getLevelIndex(field);
/* 296 */     if (levelIndex >= 1) {
/* 297 */       if (levelIndex > getLevels().size()) {
/* 298 */         throw new IllegalArgumentException("Level index out of bounds : " + levelIndex + "/" + getLevels().size() + ". Set level's size first.");
/*     */       }
/*     */ 
/* 301 */       ((Object[])getLevels().get(levelIndex - 1))[2] = value;
/* 302 */       return;
/*     */     }
/*     */ 
/* 305 */     super.setAttrColor(field, value);
/*     */   }
/*     */ 
/*     */   public double getAttrDouble(IChartObject.ATTR_DOUBLE field)
/*     */   {
/* 310 */     int levelIndex = getLevelIndex(field);
/* 311 */     if (levelIndex >= 1) {
/* 312 */       if (levelIndex > getLevels().size()) {
/* 313 */         throw new IllegalArgumentException("Level index out of bounds : " + getLevels().size() + ". Set level's size first.");
/*     */       }
/*     */ 
/* 316 */       return ((Double)((Object[])getLevels().get(levelIndex - 1))[1]).doubleValue();
/*     */     }
/*     */ 
/* 319 */     return super.getAttrDouble(field);
/*     */   }
/*     */ 
/*     */   public void setAttrDouble(IChartObject.ATTR_DOUBLE field, double value)
/*     */   {
/* 324 */     int levelIndex = getLevelIndex(field);
/* 325 */     if (levelIndex >= 1) {
/* 326 */       if (levelIndex > getLevels().size()) {
/* 327 */         throw new IllegalArgumentException("Level index out of bounds : " + levelIndex + "/" + getLevels().size() + ". Set level's size first.");
/*     */       }
/*     */ 
/* 330 */       ((Object[])getLevels().get(levelIndex - 1))[1] = Double.valueOf(value);
/* 331 */       return;
/*     */     }
/*     */ 
/* 334 */     super.setAttrDouble(field, value);
/*     */   }
/*     */ 
/*     */   public String getAttrText(IChartObject.ATTR_TEXT field)
/*     */   {
/* 339 */     int levelIndex = getLevelIndex(field);
/* 340 */     if (levelIndex >= 1) {
/* 341 */       if (levelIndex > getLevels().size()) {
/* 342 */         throw new IllegalArgumentException("Level index out of bounds : " + getLevels().size() + ". Set level's size first.");
/*     */       }
/*     */ 
/* 345 */       return (String)((Object[])getLevels().get(levelIndex - 1))[0];
/*     */     }
/*     */ 
/* 348 */     return super.getAttrText(field);
/*     */   }
/*     */ 
/*     */   public void setAttrText(IChartObject.ATTR_TEXT field, String value)
/*     */   {
/* 353 */     int levelIndex = getLevelIndex(field);
/* 354 */     if (levelIndex >= 1) {
/* 355 */       if (levelIndex > getLevels().size()) {
/* 356 */         throw new IllegalArgumentException("Level index out of bounds : " + levelIndex + "/" + getLevels().size() + ". Set level's size first.");
/*     */       }
/*     */ 
/* 359 */       ((Object[])getLevels().get(levelIndex - 1))[0] = value;
/* 360 */       return;
/*     */     }
/*     */ 
/* 363 */     super.setAttrText(field, value);
/*     */   }
/*     */ 
/*     */   protected int getLevelIndex(IChartObject.ATTR_DOUBLE field)
/*     */   {
/* 369 */     String fieldName = field.name();
/* 370 */     if (fieldName.startsWith("FIBO_LEVEL")) {
/* 371 */       return Integer.parseInt(fieldName.substring("FIBO_LEVEL".length()));
/*     */     }
/*     */ 
/* 374 */     return -1;
/*     */   }
/*     */ 
/*     */   protected int getLevelIndex(IChartObject.ATTR_TEXT field) {
/* 378 */     String fieldName = field.name();
/* 379 */     if (fieldName.startsWith("LEVEL")) {
/* 380 */       return Integer.parseInt(fieldName.substring("LEVEL".length()));
/*     */     }
/*     */ 
/* 383 */     return -1;
/*     */   }
/*     */ 
/*     */   protected int getLevelIndex(IChartObject.ATTR_COLOR field) {
/* 387 */     String fieldName = field.name();
/* 388 */     if (fieldName.startsWith("LEVEL")) {
/* 389 */       return Integer.parseInt(fieldName.substring("LEVEL".length()));
/*     */     }
/*     */ 
/* 392 */     return -1;
/*     */   }
/*     */ 
/*     */   protected void drawLevels(Graphics2D g2, IMapper dataMapper, ValueFormatter valueFormatter, GeneralPath path, int leftX, int rightX, double basicPrice, double secondaryPrice)
/*     */   {
/* 407 */     double priceInterval = secondaryPrice - basicPrice;
/*     */ 
/* 409 */     GeneralPath levelPath = new GeneralPath();
/* 410 */     for (Object[] level : getLevels()) {
/* 411 */       levelPath.reset();
/* 412 */       String label = (String)level[0];
/* 413 */       double value = ((Double)level[1]).doubleValue();
/* 414 */       Color color = (Color)level[2];
/*     */ 
/* 416 */       double price = basicPrice + (1.0D - value) * priceInterval;
/* 417 */       double y = dataMapper.yv(price);
/*     */ 
/* 419 */       this.lines.add(new Line2D.Double(leftX, y, rightX, y));
/* 420 */       GraphicHelper.drawSegmentLine(path, leftX, y, rightX, y, dataMapper.getWidth(), dataMapper.getHeight());
/*     */ 
/* 422 */       g2.setColor(color == null ? getColor() : color);
/* 423 */       GraphicHelper.drawSegmentLine(levelPath, leftX, y, rightX, y, dataMapper.getWidth(), dataMapper.getHeight());
/* 424 */       g2.draw(levelPath);
/*     */ 
/* 426 */       drawLabel(g2, dataMapper, rightX, value, label, valueFormatter, price);
/*     */     }
/*     */   }
/*     */ 
/*     */   protected void drawLabel(Graphics2D g2, IMapper mapper, int rightX, double value, String label, ValueFormatter valueFormatter, double labelPrice)
/*     */   {
/* 439 */     FontMetrics metrics = g2.getFontMetrics();
/* 440 */     int labelPriceY = mapper.yv(labelPrice);
/* 441 */     String levelLabel = createLevelLabel(valueFormatter, label, value, labelPrice);
/* 442 */     int stringLength = (int)metrics.getStringBounds(levelLabel, g2).getWidth();
/* 443 */     g2.drawString(levelLabel, rightX - stringLength - 5, labelPriceY - 2);
/*     */   }
/*     */ 
/*     */   protected String createLevelLabel(ValueFormatter valueFormatter, String label, double value, double labelPrice)
/*     */   {
/* 452 */     return label + " " + DECIMAL_FORMAT.format(value * 100.0D) + "%" + " (" + valueFormatter.formatPrice(labelPrice) + ")";
/*     */   }
/*     */ 
/*     */   private double calculateNewValue(IMapper mapper, Point point)
/*     */   {
/* 461 */     return calculateNewValue(mapper, point.y);
/*     */   }
/*     */ 
/*     */   private double calculateNewValue(IMapper mapper, double curY) {
/* 465 */     int y1 = mapper.yv(this.prices[0]);
/* 466 */     int y2 = mapper.yv(this.prices[1]);
/*     */ 
/* 468 */     int level100Index = getIndexForLevelValue(1.0D);
/* 469 */     level100Index = level100Index > -1 ? level100Index : this.lines.size() - 1;
/*     */ 
/* 471 */     int zeroLevelIndex = getIndexForLevelValue(0.0D);
/* 472 */     zeroLevelIndex = zeroLevelIndex > -1 ? zeroLevelIndex : 0;
/*     */ 
/* 474 */     int upperLineIndx = y1 > y2 ? zeroLevelIndex : level100Index;
/* 475 */     double upperLineY = ((Line2D.Double)this.lines.get(upperLineIndx)).getY1();
/*     */ 
/* 477 */     float newValue = (float)((curY - upperLineY) / Math.abs(y1 - y2));
/* 478 */     double result = newValue;
/*     */ 
/* 480 */     return result;
/*     */   }
/*     */ 
/*     */   private int getIndexForLevelValue(double levelValue) {
/* 484 */     for (int i = 0; i < getLevels().size(); i++) {
/* 485 */       Object[] level = (Object[])getLevels().get(i);
/* 486 */       if (Math.abs(((Double)level[1]).doubleValue()) == levelValue) {
/* 487 */         return i;
/*     */       }
/*     */     }
/* 490 */     return -1;
/*     */   }
/*     */ 
/*     */   private int isLevelLineHandlerSelected(IMapper mapper, Rectangle2D.Float pointRect) {
/* 494 */     for (int i = 0; i < this.lines.size(); i++) {
/* 495 */       Line2D.Double line = (Line2D.Double)this.lines.get(i);
/* 496 */       if ((line.intersects(pointRect)) && (!isLinePerformingRestricted(mapper, line))) {
/* 497 */         return i;
/*     */       }
/*     */     }
/* 500 */     return -1;
/*     */   }
/*     */ 
/*     */   private boolean isLinePerformingRestricted(IMapper mapper, Line2D.Double line)
/*     */   {
/* 511 */     double value = calculateNewValue(mapper, line.getY1());
/* 512 */     return isLevelValuePerformingRestricted(value);
/*     */   }
/*     */ 
/*     */   private boolean isLevelValuePerformingRestricted(double levelValue)
/*     */   {
/* 517 */     return (Math.abs(levelValue) == 0.0D) || (levelValue == 1.0D);
/*     */   }
/*     */ 
/*     */   private boolean isUpperLeftPointSelected(IMapper mapper, int range, Rectangle2D.Float pointerRect)
/*     */   {
/* 523 */     int x2 = mapper.xt(this.times[0]);
/* 524 */     int y2 = mapper.yv(this.prices[0]);
/* 525 */     Rectangle2D.Float handlerRect = new Rectangle2D.Float(x2 - range, y2 - range, range * 2, range * 2);
/* 526 */     return handlerRect.intersects(pointerRect);
/*     */   }
/*     */ 
/*     */   private boolean isBottomRightPointSelected(IMapper mapper, int range, Rectangle2D.Float pointerRect) {
/* 530 */     int x2 = mapper.xt(this.times[1]);
/* 531 */     int y2 = mapper.yv(this.prices[1]);
/* 532 */     Rectangle2D.Float handlerRect = new Rectangle2D.Float(x2 - range, y2 - range, range * 2, range * 2);
/* 533 */     return handlerRect.intersects(pointerRect);
/*     */   }
/*     */ 
/*     */   public void setColor(Color color)
/*     */   {
/* 541 */     super.setColor(color);
/* 542 */     for (Object[] level : getLevels())
/* 543 */       level[2] = null;
/*     */   }
/*     */ 
/*     */   public boolean isLevelValuesInPercents()
/*     */   {
/* 549 */     return true;
/*     */   }
/*     */ 
/*     */   protected boolean isDrawable(IMapper mapper)
/*     */   {
/* 554 */     return arePointsValid();
/*     */   }
/*     */ 
/*     */   private void readObject(ObjectInputStream in)
/*     */     throws IOException, ClassNotFoundException
/*     */   {
/* 564 */     in.defaultReadObject();
/* 565 */     if (this.levels == null)
/* 566 */       this.levels = this.levels;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Charts-5.48.jar
 * Qualified Name:     com.dukascopy.charts.drawings.HorizontalRetracementChartObject
 * JD-Core Version:    0.6.0
 */