/*     */ package com.dukascopy.charts.drawings;
/*     */ 
/*     */ import com.dukascopy.api.IChart.Type;
/*     */ import com.dukascopy.api.IChartObject;
/*     */ import com.dukascopy.api.drawings.IAndrewsPitchforkChartObject;
/*     */ import com.dukascopy.charts.ChartProperties;
/*     */ import com.dukascopy.charts.mappers.IMapper;
/*     */ import com.dukascopy.charts.utils.GraphicHelper;
/*     */ import com.dukascopy.charts.utils.formatter.FormattersManager;
/*     */ import com.dukascopy.charts.utils.formatter.ValueFormatter;
/*     */ import java.awt.Color;
/*     */ import java.awt.Graphics;
/*     */ import java.awt.Graphics2D;
/*     */ import java.awt.Point;
/*     */ import java.awt.Rectangle;
/*     */ import java.awt.geom.GeneralPath;
/*     */ import java.awt.geom.Line2D.Double;
/*     */ import java.awt.geom.Rectangle2D.Float;
/*     */ import java.util.List;
/*     */ 
/*     */ public class AndrewsPitchforkChartObject extends AbstractThreePointFiboExtensionsChartObject
/*     */   implements IAndrewsPitchforkChartObject
/*     */ {
/*     */   static final long serialVersionUID = 1L;
/*     */   private static final String CHART_OBJECT_NAME = "Andrews Pitchfork";
/*     */   private static final int LABEL_OFFSET = 5;
/*  29 */   private transient boolean mirroredPointSelected = false;
/*     */ 
/*     */   public AndrewsPitchforkChartObject(String key)
/*     */   {
/*  33 */     super(key, IChart.Type.PITCHFORK);
/*     */   }
/*     */ 
/*     */   public AndrewsPitchforkChartObject() {
/*  37 */     super(IChart.Type.PITCHFORK);
/*     */   }
/*     */ 
/*     */   public AndrewsPitchforkChartObject(AndrewsPitchforkChartObject chartObject) {
/*  41 */     super(chartObject);
/*     */   }
/*     */ 
/*     */   public AndrewsPitchforkChartObject(String key, long time1, double price1, long time2, double price2, long time3, double price3) {
/*  45 */     super(key, IChart.Type.PITCHFORK, time1, price1, time2, price2, time3, price3);
/*     */   }
/*     */ 
/*     */   protected void drawChartObject(Graphics g, IMapper mapper, Rectangle labelDimension, Rectangle textDimension, FormattersManager formattersManager, ChartObjectDrawingMode drawingMode)
/*     */   {
/*  56 */     int x1 = mapper.xt(this.times[0]);
/*  57 */     int x2 = mapper.xt(this.times[1]);
/*  58 */     int y1 = mapper.yv(this.prices[0]);
/*  59 */     int y2 = mapper.yv(this.prices[1]);
/*     */ 
/*  61 */     GeneralPath drawingPath = getPath();
/*  62 */     drawingPath.reset();
/*     */ 
/*  64 */     int width = mapper.getWidth();
/*  65 */     int height = mapper.getHeight();
/*     */ 
/*  68 */     GraphicHelper.drawSegmentDashedLine(g, x1, y1, x2, y2, 0.001D, 5.0D, g.getColor(), width, height);
/*     */     int x3;
/*     */     int y3;
/*     */     double dx;
/*     */     double dy;
/*     */     double intervalX;
/*     */     double intervalY;
/*     */     GeneralPath levelPath;
/*  70 */     if (isValidPoint(2)) {
/*  71 */       x3 = mapper.xt(this.times[2]);
/*  72 */       y3 = mapper.yv(this.prices[2]);
/*     */ 
/*  74 */       GraphicHelper.drawSegmentDashedLine(g, x1, y1, x3, y3, 0.001D, 5.0D, g.getColor(), width, height);
/*     */ 
/*  76 */       double xOrigin = (x2 + x3) / 2;
/*  77 */       double yOrigin = (y2 + y3) / 2;
/*     */ 
/*  79 */       this.lines.clear();
/*     */ 
/*  81 */       dx = xOrigin - x1;
/*  82 */       dy = yOrigin - y1;
/*     */ 
/*  84 */       GraphicHelper.drawSegmentLine(drawingPath, x2, y2, x3, y3, width, height);
/*     */ 
/*  86 */       GraphicHelper.drawSegmentLine(drawingPath, x1, y1, x1 + dx * 7.0D, y1 + dy * 7.0D, width, height);
/*     */ 
/*  88 */       GraphicHelper.drawSegmentLine(drawingPath, x2, y2, x2 + dx * 6.0D, y2 + dy * 6.0D, width, height);
/*     */ 
/*  90 */       GraphicHelper.drawSegmentLine(drawingPath, x3, y3, x3 + dx * 6.0D, y3 + dy * 6.0D, width, height);
/*     */ 
/*  92 */       ((Graphics2D)g).draw(drawingPath);
/*     */ 
/*  94 */       intervalX = x2 - xOrigin;
/*  95 */       intervalY = y2 - yOrigin;
/*     */ 
/*  97 */       levelPath = new GeneralPath();
/*  98 */       for (Object[] level : getLevels()) {
/*  99 */         levelPath.reset();
/*     */ 
/* 101 */         String label = (String)level[0];
/* 102 */         double value = ((Double)level[1]).doubleValue();
/* 103 */         Color color = (Color)level[2];
/*     */ 
/* 105 */         double xStart = x2 + intervalX * value;
/* 106 */         double yStart = y2 + intervalY * value;
/*     */ 
/* 108 */         double xEnd = xStart + dx * 6.0D;
/* 109 */         double yEnd = yStart + dy * 6.0D;
/*     */ 
/* 111 */         g.setColor(color == null ? getColor() : color);
/*     */ 
/* 113 */         GraphicHelper.drawSegmentDashedLine(levelPath, xStart, yStart, xEnd, yEnd, 5.0D, 5.0D, width, height);
/*     */ 
/* 115 */         GraphicHelper.drawSegmentLine(drawingPath, xStart, yStart, xEnd, yEnd, width, height);
/*     */ 
/* 117 */         StringBuilder valueLabel = new StringBuilder().append(label).append("  ").append(formattersManager.getValueFormatter().formatFibo(value * 100.0D)).append("%");
/*     */ 
/* 122 */         g.drawString(valueLabel.toString(), (int)xStart + 5, (int)yStart);
/*     */ 
/* 124 */         this.lines.add(new Line2D.Double(xStart, yStart, xEnd, yEnd));
/*     */ 
/* 127 */         xStart = x3 - intervalX * value;
/* 128 */         yStart = y3 - intervalY * value;
/*     */ 
/* 130 */         xEnd = xStart + dx * 6.0D;
/* 131 */         yEnd = yStart + dy * 6.0D;
/*     */ 
/* 133 */         GraphicHelper.drawSegmentDashedLine(levelPath, xStart, yStart, xEnd, yEnd, 5.0D, 5.0D, width, height);
/*     */ 
/* 135 */         GraphicHelper.drawSegmentLine(drawingPath, xStart, yStart, xEnd, yEnd, width, height);
/*     */ 
/* 139 */         g.drawString(valueLabel.toString(), (int)xStart + 5, (int)yStart);
/*     */ 
/* 141 */         ((Graphics2D)g).draw(levelPath);
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   protected Rectangle drawTextAsLabel(Graphics g, IMapper mapper, Rectangle labelDimension, FormattersManager formattersManager, DrawingsLabelHelper drawingsLabelHelper, ChartObjectDrawingMode drawingMode)
/*     */   {
/* 151 */     if ((drawingMode == ChartObjectDrawingMode.GLOBAL_ON_MAIN_WITH_SUBCHARTS) || (drawingMode == ChartObjectDrawingMode.DEFAULT)) {
/* 152 */       return drawingsLabelHelper.drawLabelGeneric(g, mapper, this, getTime(0), getPrice(0), Double.compare(getPrice(0), getPrice(1)) >= 0);
/*     */     }
/* 154 */     return ZERO_RECTANGLE;
/*     */   }
/*     */ 
/*     */   public IChartObject clone()
/*     */   {
/* 161 */     return new AndrewsPitchforkChartObject(this);
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/* 166 */     return "Andrews Pitchfork";
/*     */   }
/*     */ 
/*     */   public List<Point> getHandlerMiddlePoints(IMapper mapper)
/*     */   {
/* 171 */     List handlerMiddlePoints = getHandlerMiddlePoints();
/*     */ 
/* 173 */     handlerMiddlePoints.add(new Point(mapper.xt(this.times[0]), mapper.yv(this.prices[0])));
/* 174 */     handlerMiddlePoints.add(new Point(mapper.xt(this.times[1]), mapper.yv(this.prices[1])));
/* 175 */     handlerMiddlePoints.add(new Point(mapper.xt(this.times[2]), mapper.yv(this.prices[2])));
/* 176 */     for (int i = 0; i < this.lines.size(); i++) {
/* 177 */       Line2D.Double line = (Line2D.Double)this.lines.get(i);
/*     */ 
/* 179 */       handlerMiddlePoints.add(getHandlerPointForLevelIndex(line));
/* 180 */       handlerMiddlePoints.add(getHandlerPointForLevelIndex(getMirroredLine(line, mapper)));
/*     */     }
/* 182 */     return handlerMiddlePoints;
/*     */   }
/*     */ 
/*     */   protected double calculateNewValue(IMapper mapper, Point point)
/*     */   {
/* 187 */     int x1 = mapper.xt(this.times[0]);
/* 188 */     int x2 = mapper.xt(this.times[1]);
/* 189 */     int x3 = mapper.xt(this.times[2]);
/* 190 */     int y1 = mapper.yv(this.prices[0]);
/* 191 */     int y2 = mapper.yv(this.prices[1]);
/* 192 */     int y3 = mapper.yv(this.prices[2]);
/*     */ 
/* 194 */     double xOrigin = (x3 + x2) / 2;
/* 195 */     double yOrigin = (y3 + y2) / 2;
/*     */ 
/* 197 */     double a2 = GraphicHelper.a(x1, y1, xOrigin, yOrigin);
/* 198 */     double b2 = GraphicHelper.b(point.x, point.y, a2);
/*     */     double y;
/*     */     double y;
/* 201 */     if (x2 == x3) {
/* 202 */       y = a2 * x2 + b2;
/*     */     } else {
/* 204 */       double a1 = GraphicHelper.a(x2, y2, x3, y3);
/* 205 */       double b1 = GraphicHelper.b(x2, y2, a1);
/*     */ 
/* 207 */       double x = GraphicHelper.xCross(a1, b1, a2, b2);
/* 208 */       y = a1 * x + b1;
/*     */     }
/*     */     double newValue;
/*     */     double newValue;
/* 214 */     if (this.mirroredPointSelected)
/* 215 */       newValue = (float)((y3 - y) / (yOrigin - y3));
/*     */     else {
/* 217 */       newValue = (float)((y2 - y) / (yOrigin - y2));
/*     */     }
/*     */ 
/* 221 */     if (newValue < ChartProperties.MIN_LEVEL_VALUE.doubleValue() / 100.0D) {
/* 222 */       newValue = ChartProperties.MIN_LEVEL_VALUE.doubleValue() / 100.0D;
/*     */     }
/* 224 */     else if (newValue > ChartProperties.MAX_LEVEL_VALUE.doubleValue() / 100.0D) {
/* 225 */       newValue = ChartProperties.MAX_LEVEL_VALUE.doubleValue() / 100.0D;
/*     */     }
/* 227 */     return newValue;
/*     */   }
/*     */ 
/*     */   protected int isLevelLineHandlerSelected(IMapper mapper, Rectangle2D.Float pointRect)
/*     */   {
/* 232 */     for (int i = 0; i < this.lines.size(); i++) {
/* 233 */       Line2D.Double line = (Line2D.Double)this.lines.get(i);
/*     */ 
/* 235 */       Line2D.Double mirroredLine = getMirroredLine(line, mapper);
/*     */ 
/* 237 */       if (line.intersects(pointRect)) {
/* 238 */         this.mirroredPointSelected = false;
/* 239 */         return i;
/* 240 */       }if (mirroredLine.intersects(pointRect)) {
/* 241 */         this.mirroredPointSelected = true;
/* 242 */         return i;
/*     */       }
/*     */     }
/* 245 */     return -1;
/*     */   }
/*     */ 
/*     */   private Line2D.Double getMirroredLine(Line2D.Double line, IMapper mapper) {
/* 249 */     int y2 = mapper.yv(this.prices[1]);
/* 250 */     int y3 = mapper.yv(this.prices[2]);
/*     */ 
/* 252 */     int x2 = mapper.xt(this.times[1]);
/* 253 */     int x3 = mapper.xt(this.times[2]);
/*     */ 
/* 255 */     double xOrigin = (x3 + x2) / 2;
/* 256 */     double yOrigin = (y3 + y2) / 2;
/*     */ 
/* 258 */     double dx = (line.getX1() - xOrigin) * 2.0D;
/* 259 */     double dy = (line.getY1() - yOrigin) * 2.0D;
/*     */ 
/* 261 */     return new Line2D.Double(line.getX1() - dx, line.getY1() - dy, line.getX2() - dx, line.getY2() - dy);
/*     */   }
/*     */ 
/*     */   protected Point getHandlerPointForLevelIndex(Line2D.Double line)
/*     */   {
/* 266 */     return new Point((int)line.x1, (int)line.y1);
/*     */   }
/*     */ 
/*     */   public List<Object[]> getDefaults()
/*     */   {
/* 271 */     return ChartProperties.createDefaultLevelsFiboExtensions();
/*     */   }
/*     */ 
/*     */   public String getLocalizationKey()
/*     */   {
/* 276 */     return "item.andrews.pitchfork";
/*     */   }
/*     */ 
/*     */   public boolean isLabelEnabled()
/*     */   {
/* 284 */     return true;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Charts-5.48.jar
 * Qualified Name:     com.dukascopy.charts.drawings.AndrewsPitchforkChartObject
 * JD-Core Version:    0.6.0
 */