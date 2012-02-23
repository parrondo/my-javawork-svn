/*     */ package com.dukascopy.charts.drawings;
/*     */ 
/*     */ import com.dukascopy.api.IChart.Type;
/*     */ import com.dukascopy.api.IChartObject;
/*     */ import com.dukascopy.api.Instrument;
/*     */ import com.dukascopy.api.Period;
/*     */ import com.dukascopy.api.drawings.IGannGridChartObject;
/*     */ import com.dukascopy.charts.mappers.IMapper;
/*     */ import com.dukascopy.charts.utils.GraphicHelper;
/*     */ import com.dukascopy.charts.utils.formatter.FormattersManager;
/*     */ import com.dukascopy.charts.utils.formatter.ValueFormatter;
/*     */ import java.awt.Graphics;
/*     */ import java.awt.Graphics2D;
/*     */ import java.awt.Point;
/*     */ import java.awt.Rectangle;
/*     */ import java.awt.geom.GeneralPath;
/*     */ import java.awt.geom.Rectangle2D.Float;
/*     */ import java.util.List;
/*     */ 
/*     */ public class GannGridChartObject extends AbstractStickablePointsChartObject
/*     */   implements IGannGridChartObject
/*     */ {
/*     */   private static final long serialVersionUID = 1L;
/*     */   private static final int MIN_CELL_WIDTH = 2;
/*     */   private static final int MIN_CELL_HEIGHT_IN_PX = 3;
/*     */   private static final int LABEL_OFFSET = 3;
/*  27 */   private double pipsPerBar = 1.0D;
/*  28 */   private int cellWidthInBars = 7;
/*     */   private transient GeneralPath path;
/*  32 */   private transient boolean middlePointSelected = false;
/*     */   private transient Point middlePoint;
/*     */ 
/*     */   public GannGridChartObject()
/*     */   {
/*  37 */     this((String)null);
/*     */   }
/*     */ 
/*     */   public GannGridChartObject(String key) {
/*  41 */     super(key, IChart.Type.GANNGRID);
/*     */ 
/*  43 */     setUnderEdit(true);
/*     */   }
/*     */ 
/*     */   public GannGridChartObject(GannGridChartObject obj) {
/*  47 */     super(obj);
/*     */ 
/*  49 */     this.pipsPerBar = obj.pipsPerBar;
/*     */   }
/*     */ 
/*     */   private GeneralPath getPath()
/*     */   {
/*  54 */     if (this.path == null) {
/*  55 */       this.path = new GeneralPath();
/*     */     }
/*  57 */     return this.path;
/*     */   }
/*     */ 
/*     */   protected boolean isDrawable(IMapper mapper)
/*     */   {
/*  62 */     return (!Period.TICK.equals(mapper.getPeriod())) && (isValidPoint(0));
/*     */   }
/*     */ 
/*     */   public int getPointsCount()
/*     */   {
/*  67 */     return 1;
/*     */   }
/*     */ 
/*     */   public List<Point> getHandlerMiddlePoints(IMapper mapper)
/*     */   {
/*  72 */     List handlerMiddlePoints = getHandlerMiddlePoints();
/*     */ 
/*  74 */     handlerMiddlePoints.add(new Point(mapper.xt(this.times[0]), mapper.yv(this.prices[0])));
/*  75 */     if (null != this.middlePoint) {
/*  76 */       handlerMiddlePoints.add(this.middlePoint);
/*     */     }
/*  78 */     return handlerMiddlePoints;
/*     */   }
/*     */ 
/*     */   protected void drawChartObject(Graphics g, IMapper mapper, Rectangle labelDimension, Rectangle textDimension, FormattersManager formattersManager, ChartObjectDrawingMode drawingMode)
/*     */   {
/*  88 */     if (Period.TICK.equals(mapper.getPeriod())) {
/*  89 */       return;
/*     */     }
/*  91 */     getPath().reset();
/*     */ 
/*  93 */     double pipsPerBarValue = mapper.getInstrument().getPipValue();
/*  94 */     pipsPerBarValue *= this.pipsPerBar;
/*     */ 
/*  96 */     int x = mapper.xt(this.times[0]);
/*  97 */     int y = mapper.yv(this.prices[0]);
/*     */ 
/*  99 */     int x2 = x + this.cellWidthInBars * mapper.getBarWidth();
/* 100 */     int y2 = mapper.yv(this.prices[0] + pipsPerBarValue * this.cellWidthInBars);
/*     */ 
/* 102 */     int screenWidth = mapper.getWidth();
/* 103 */     int screenHeight = mapper.getHeight();
/*     */ 
/* 105 */     GraphicHelper.drawBeamLine(getPath(), x2, y2, x, y, screenWidth, screenHeight);
/*     */ 
/* 107 */     int dx = x2 - x;
/* 108 */     int dy = y2 - y;
/*     */ 
/* 110 */     if ((dy != 0) && (this.cellWidthInBars > 2) && (Math.abs(dy) > 3)) {
/* 111 */       int index = 0;
/* 112 */       boolean outOfRange = false;
/*     */       do {
/* 114 */         outOfRange = !GraphicHelper.drawInfiniteLine(getPath(), x2 + dx * index, y2, x + dx * (index + 2), y, screenWidth, screenHeight);
/* 115 */         index += 2;
/*     */       }
/* 117 */       while (!outOfRange);
/*     */ 
/* 120 */       index = 0;
/* 121 */       outOfRange = false;
/*     */       do {
/* 123 */         outOfRange = !GraphicHelper.drawBeamLine(getPath(), x2 - dx * index, y2 + dy * (index + 2), x2 - dx * (index + 1), y2 + dy * (index + 1), screenWidth, screenHeight);
/* 124 */         index++;
/* 125 */       }while (!outOfRange);
/*     */ 
/* 128 */       index = 0;
/* 129 */       outOfRange = false;
/*     */       do {
/* 131 */         outOfRange = !GraphicHelper.drawBeamLine(getPath(), x2 + dx * (index + 2), y2 - dy * index, x2 + dx * (index + 1), y2 - dy * (index + 1), screenWidth, screenHeight);
/* 132 */         index++;
/* 133 */       }while (!outOfRange);
/*     */     }
/*     */ 
/* 136 */     this.middlePoint = new Point(x2, y2);
/*     */ 
/* 138 */     String formattedValue = formattersManager.getValueFormatter().formatFibo(this.pipsPerBar);
/* 139 */     g.drawString(formattedValue, this.middlePoint.x + 3, this.middlePoint.y - 3);
/*     */ 
/* 141 */     ((Graphics2D)g).draw(getPath());
/*     */   }
/*     */ 
/*     */   protected Rectangle drawTextAsLabel(Graphics g, IMapper mapper, Rectangle labelDimension, FormattersManager formattersManager, DrawingsLabelHelper drawingsLabelHelper, ChartObjectDrawingMode drawingMode)
/*     */   {
/* 149 */     if ((drawingMode == ChartObjectDrawingMode.GLOBAL_ON_MAIN_WITH_SUBCHARTS) || (drawingMode == ChartObjectDrawingMode.DEFAULT)) {
/* 150 */       boolean downgrade = false;
/* 151 */       if (this.middlePoint != null) {
/* 152 */         double price = mapper.vy(this.middlePoint.y);
/* 153 */         downgrade = Double.compare(getPrice(0), price) >= 0;
/*     */       }
/* 155 */       return drawingsLabelHelper.drawLabelGeneric(g, mapper, this, getTime(0), getPrice(0), downgrade);
/*     */     }
/* 157 */     return ZERO_RECTANGLE;
/*     */   }
/*     */ 
/*     */   public void modifyEditedDrawing(Point point, Point prevPoint, IMapper mapper, int range)
/*     */   {
/* 164 */     if (this.middlePointSelected) {
/* 165 */       int x1 = mapper.xt(this.times[0]);
/* 166 */       int x = point.x;
/*     */ 
/* 168 */       double pipValue = mapper.getInstrument().getPipValue();
/* 169 */       double pipsDiff = (mapper.vy(point.y) - this.prices[0]) / pipValue;
/* 170 */       this.cellWidthInBars = Math.max(1, (int)Math.round((x - x1) / mapper.getBarWidth()));
/*     */ 
/* 172 */       this.pipsPerBar = (pipsDiff / this.cellWidthInBars);
/* 173 */       if (Math.abs(this.pipsPerBar) < 0.01D)
/* 174 */         if (this.pipsPerBar > 0.0D)
/* 175 */           this.pipsPerBar = 0.01D;
/*     */         else
/* 177 */           this.pipsPerBar = -0.01D;
/*     */     }
/*     */     else
/*     */     {
/* 181 */       super.modifyEditedDrawing(point, prevPoint, mapper, range);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void updateSelectedHandler(Point point, IMapper mapper, int range)
/*     */   {
/* 187 */     super.updateSelectedHandler(point, mapper, range);
/*     */ 
/* 189 */     if (-1 == this.selectedHandlerIndex)
/* 190 */       this.middlePointSelected = ((null != this.middlePoint) && (this.middlePoint.distance(point) < range));
/*     */     else
/* 192 */       this.middlePointSelected = false;
/*     */   }
/*     */ 
/*     */   public boolean intersects(Point point, IMapper mapper, int range)
/*     */   {
/* 199 */     Rectangle2D.Float pointerRectangle = new Rectangle2D.Float(point.x - range, point.y - range, range * 2, range * 2);
/* 200 */     return getPath().intersects(pointerRectangle);
/*     */   }
/*     */ 
/*     */   public IChartObject clone()
/*     */   {
/* 205 */     return new GannGridChartObject(this);
/*     */   }
/*     */ 
/*     */   public String getLocalizationKey()
/*     */   {
/* 210 */     return "item.gann.periods";
/*     */   }
/*     */ 
/*     */   public double getPipsPerBar()
/*     */   {
/* 215 */     return this.pipsPerBar;
/*     */   }
/*     */ 
/*     */   public void setPipsPerBar(double pipsPerBar)
/*     */   {
/* 220 */     this.pipsPerBar = pipsPerBar;
/*     */   }
/*     */ 
/*     */   public int getCellWidthInBars()
/*     */   {
/* 225 */     return this.cellWidthInBars;
/*     */   }
/*     */ 
/*     */   public void setCellWidthInBars(int cellWidthInBars)
/*     */   {
/* 230 */     this.cellWidthInBars = cellWidthInBars;
/*     */   }
/*     */ 
/*     */   public boolean isLabelEnabled()
/*     */   {
/* 238 */     return true;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Charts-5.48.jar
 * Qualified Name:     com.dukascopy.charts.drawings.GannGridChartObject
 * JD-Core Version:    0.6.0
 */