/*     */ package com.dukascopy.charts.drawings;
/*     */ 
/*     */ import com.dukascopy.api.IChart.Type;
/*     */ import com.dukascopy.api.drawings.IFiboTimeZonesChartObject;
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
/*     */ import java.util.List;
/*     */ 
/*     */ public class FiboTimeZonesChartObject extends AbstractTwoPointExtLevelsChartObject
/*     */   implements IFiboTimeZonesChartObject
/*     */ {
/*     */   private static final long serialVersionUID = 1L;
/*     */   private static final String CHART_OBJECT_NAME = "Fibo Time Zones";
/*     */   private static final int LABEL_OFFSET = 5;
/*     */ 
/*     */   public FiboTimeZonesChartObject(String key)
/*     */   {
/*  29 */     super(key, IChart.Type.FIBOTIMES);
/*     */   }
/*     */ 
/*     */   public FiboTimeZonesChartObject() {
/*  33 */     super(IChart.Type.FIBOTIMES);
/*     */   }
/*     */ 
/*     */   public FiboTimeZonesChartObject(String key, long time1, double price1, long time2, double price2)
/*     */   {
/*  38 */     super(key, IChart.Type.FIBOTIMES, time1, price1, time2, price2);
/*     */   }
/*     */ 
/*     */   public FiboTimeZonesChartObject(FiboTimeZonesChartObject chartObject) {
/*  42 */     super(chartObject);
/*     */   }
/*     */ 
/*     */   protected void drawChartObject(Graphics g, IMapper mapper, Rectangle labelDimension, Rectangle textDimension, FormattersManager formattersManager, ChartObjectDrawingMode drawingMode)
/*     */   {
/*  50 */     this.lines.clear();
/*     */ 
/*  52 */     GeneralPath drawingPath = getPath();
/*  53 */     drawingPath.reset();
/*     */ 
/*  55 */     int x1 = mapper.xt(this.times[0]);
/*  56 */     int x2 = mapper.xt(this.times[1]);
/*     */ 
/*  58 */     long timeInterval = this.times[1] - this.times[0];
/*     */ 
/*  60 */     int y = mapper.yv(this.prices[0]);
/*     */ 
/*  62 */     GraphicHelper.drawSegmentDashedLine(drawingPath, x1, y, x2, y, 5.0D, 3.0D, mapper.getWidth(), mapper.getHeight());
/*  63 */     ((Graphics2D)g).draw(drawingPath);
/*     */ 
/*  65 */     GeneralPath levelPath = new GeneralPath();
/*     */ 
/*  67 */     for (Object[] level : getLevels()) {
/*  68 */       levelPath.reset();
/*     */ 
/*  70 */       String label = (String)level[0];
/*  71 */       double value = ((Double)level[1]).doubleValue() * 100.0D;
/*  72 */       Color color = (Color)level[2];
/*  73 */       g.setColor(color == null ? getColor() : color);
/*     */       long x;
/*     */       long x;
/*  75 */       if (mapper.getInterval() > 1L) {
/*  76 */         x = x1 + ()(timeInterval * value * mapper.getBarWidth() / mapper.getInterval());
/*     */       }
/*     */       else
/*     */       {
/*  80 */         x = mapper.xt(this.times[0] + ()(timeInterval * value));
/*     */       }
/*     */ 
/*  83 */       Line2D.Double line = new Line2D.Double(x, 0.0D, x, mapper.getHeight());
/*  84 */       this.lines.add(line);
/*  85 */       levelPath.append(line, false);
/*     */ 
/*  87 */       ((Graphics2D)g).draw(levelPath);
/*  88 */       drawingPath.append(levelPath, false);
/*     */       String formatedValue;
/*     */       String formatedValue;
/*  91 */       if ((int)value * 100 == (int)(value * 100.0D))
/*  92 */         formatedValue = String.valueOf((int)value);
/*     */       else {
/*  94 */         formatedValue = formattersManager.getValueFormatter().formatFibo(value);
/*     */       }
/*     */ 
/*  98 */       g.drawString((label != null) && (!label.isEmpty()) ? label : formatedValue, (int)x + 5, mapper.getHeight() / 2 + 5);
/*     */     }
/*     */   }
/*     */ 
/*     */   protected Rectangle drawTextAsLabel(Graphics g, IMapper mapper, Rectangle labelDimension, FormattersManager formattersManager, DrawingsLabelHelper drawingsLabelHelper, ChartObjectDrawingMode drawingMode)
/*     */   {
/* 109 */     if ((drawingMode == ChartObjectDrawingMode.GLOBAL_ON_MAIN_WITH_SUBCHARTS) || (drawingMode == ChartObjectDrawingMode.DEFAULT)) {
/* 110 */       return drawingsLabelHelper.drawLabelGeneric(g, mapper, this, getTime(0), getPrice(0), Double.compare(getPrice(0), getPrice(1)) >= 0);
/*     */     }
/* 112 */     return ZERO_RECTANGLE;
/*     */   }
/*     */ 
/*     */   public void modifyNewDrawing(Point point, IMapper mapper, int defaultRange)
/*     */   {
/* 119 */     if (this.currentPoint == 1) {
/* 120 */       this.times[1] = mapper.tx(point.x);
/* 121 */       this.prices[1] = mapper.vy(point.y);
/* 122 */       this.prices[0] = this.prices[1];
/*     */     }
/*     */   }
/*     */ 
/*     */   public void modifyEditedDrawing(Point point, Point prevPoint, IMapper mapper, int range)
/*     */   {
/* 129 */     super.modifyEditedDrawing(point, prevPoint, mapper, range);
/*     */ 
/* 131 */     if (this.selectedHandlerIndex > -1)
/*     */     {
/* 133 */       this.prices[0] = mapper.vy(point.y);
/* 134 */       this.prices[1] = mapper.vy(point.y);
/*     */     }
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/* 140 */     return "Fibo Time Zones";
/*     */   }
/*     */ 
/*     */   public FiboTimeZonesChartObject clone()
/*     */   {
/* 145 */     return new FiboTimeZonesChartObject(this);
/*     */   }
/*     */ 
/*     */   protected boolean isDrawable(IMapper mapper)
/*     */   {
/* 150 */     return (super.isDrawable(mapper)) && ((this.times[1] == this.times[0]) || (mapper.getInterval() / 100L <= Math.abs(this.times[1] - this.times[0])));
/*     */   }
/*     */ 
/*     */   public List<Object[]> getDefaults()
/*     */   {
/* 157 */     return ChartProperties.createDefaultLevelsFiboTimes();
/*     */   }
/*     */ 
/*     */   protected double calculateNewValue(IMapper mapper, Point point)
/*     */   {
/* 163 */     double newValue = (point.x - mapper.xt(this.times[0])) * mapper.getInterval() / (mapper.getBarWidth() * Math.abs(this.times[1] - this.times[0]));
/*     */ 
/* 167 */     if (newValue < ChartProperties.MIN_LEVEL_VALUE.doubleValue())
/* 168 */       newValue = ChartProperties.MIN_LEVEL_VALUE.doubleValue();
/* 169 */     else if (newValue > ChartProperties.MAX_LEVEL_VALUE.doubleValue()) {
/* 170 */       newValue = ChartProperties.MAX_LEVEL_VALUE.doubleValue();
/*     */     }
/* 172 */     return (int)(newValue * 100.0D) / 10000.0D;
/*     */   }
/*     */ 
/*     */   protected Point getHandlerPointForLevelIndex(Line2D.Double line)
/*     */   {
/* 177 */     return new Point((int)line.x1, (int)((line.y1 + line.y2) / 2.0D));
/*     */   }
/*     */ 
/*     */   public boolean isLevelValuesInPercents()
/*     */   {
/* 182 */     return false;
/*     */   }
/*     */ 
/*     */   public String getLocalizationKey()
/*     */   {
/* 187 */     return "item.fibonacci.time.zones";
/*     */   }
/*     */ 
/*     */   public boolean isSticky()
/*     */   {
/* 192 */     return false;
/*     */   }
/*     */ 
/*     */   public boolean hasPriceValue()
/*     */   {
/* 197 */     return false;
/*     */   }
/*     */ 
/*     */   public boolean isLabelEnabled()
/*     */   {
/* 205 */     return true;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Charts-5.48.jar
 * Qualified Name:     com.dukascopy.charts.drawings.FiboTimeZonesChartObject
 * JD-Core Version:    0.6.0
 */