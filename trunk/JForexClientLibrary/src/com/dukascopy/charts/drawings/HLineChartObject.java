/*     */ package com.dukascopy.charts.drawings;
/*     */ 
/*     */ import com.dukascopy.api.IChart.Type;
/*     */ import com.dukascopy.api.drawings.IDecoratedChartObject.Decoration;
/*     */ import com.dukascopy.api.drawings.IDecoratedChartObject.Placement;
/*     */ import com.dukascopy.api.drawings.IHorizontalLineChartObject;
/*     */ import com.dukascopy.charts.mappers.IMapper;
/*     */ import com.dukascopy.charts.utils.formatter.FormattersManager;
/*     */ import java.awt.FontMetrics;
/*     */ import java.awt.Graphics;
/*     */ import java.awt.Graphics2D;
/*     */ import java.awt.Point;
/*     */ import java.awt.Rectangle;
/*     */ import java.awt.RenderingHints;
/*     */ import java.awt.Shape;
/*     */ import java.awt.event.ActionListener;
/*     */ import java.awt.geom.AffineTransform;
/*     */ import java.awt.geom.Rectangle2D;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ 
/*     */ public class HLineChartObject extends DecoratedChartObject
/*     */   implements IHorizontalLineChartObject
/*     */ {
/*     */   private static final long serialVersionUID = 1L;
/*     */   private static final String CHART_OBJECT_NAME = "HLine";
/*     */   static final int LABEL_OFFSET = 2;
/*     */   public static final int OFFSET_TO_LABEL = 5;
/*     */ 
/*     */   public HLineChartObject(String key)
/*     */   {
/*  30 */     this(key, IChart.Type.HLINE);
/*     */   }
/*     */ 
/*     */   public HLineChartObject() {
/*  34 */     this(null, IChart.Type.HLINE);
/*  35 */     setUnderEdit(true);
/*     */   }
/*     */ 
/*     */   public HLineChartObject(String key, IChart.Type type) {
/*  39 */     super(key, type);
/*     */   }
/*     */ 
/*     */   public HLineChartObject(String key, double price) {
/*  43 */     this(key, price, IChart.Type.HLINE);
/*     */   }
/*     */ 
/*     */   public HLineChartObject(String key, double price, IChart.Type type) {
/*  47 */     super(key, type);
/*  48 */     this.prices[0] = price;
/*     */   }
/*     */ 
/*     */   public HLineChartObject(HLineChartObject chartObject) {
/*  52 */     super(chartObject);
/*     */   }
/*     */ 
/*     */   public void move(long time, double price)
/*     */   {
/*  57 */     this.prices[0] = price;
/*  58 */     getActionListener().actionPerformed(null);
/*     */   }
/*     */ 
/*     */   protected final boolean isDrawable(IMapper mapper)
/*     */   {
/*  63 */     return (isValidPrice(0)) && (!mapper.isYOutOfRange(mapper.yv(this.prices[0])));
/*     */   }
/*     */ 
/*     */   protected void drawChartObject(Graphics g, IMapper mapper, Rectangle labelDimension, Rectangle textDimension, FormattersManager formattersManager, ChartObjectDrawingMode drawingMode)
/*     */   {
/*  68 */     int y = mapper.yv(this.prices[0]);
/*     */ 
/*  70 */     int beginningOffset = 0;
/*  71 */     int endOffset = 0;
/*     */ 
/*  73 */     Map decorations = getDecorations();
/*  74 */     if (decorations != null) {
/*  75 */       for (IDecoratedChartObject.Placement placement : decorations.keySet()) {
/*  76 */         IDecoratedChartObject.Decoration decoration = (IDecoratedChartObject.Decoration)decorations.get(placement);
/*  77 */         drawDecoration(placement, (IDecoratedChartObject.Decoration)decorations.get(placement), g, mapper.getWidth(), y);
/*  78 */         if (IDecoratedChartObject.Decoration.None != decoration) {
/*  79 */           if (IDecoratedChartObject.Placement.Beginning == placement) {
/*  80 */             beginningOffset = getLineWidth();
/*     */           }
/*  82 */           else if (IDecoratedChartObject.Placement.End == placement) {
/*  83 */             endOffset = getLineWidth();
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/*  89 */     g.drawLine(beginningOffset, y, textDimension.x - 5, y);
/*  90 */     g.drawLine(textDimension.x + textDimension.width + 3, y, mapper.getWidth() - endOffset, y);
/*     */   }
/*     */ 
/*     */   protected void drawDecoration(IDecoratedChartObject.Placement placement, IDecoratedChartObject.Decoration decoration, Graphics g, int width, int y) {
/*  94 */     Graphics2D g2d = (Graphics2D)g;
/*     */ 
/*  96 */     Shape decorationShape = getShape(decoration);
/*  97 */     decorationShape = scaleToCurrentStroke(decorationShape);
/*     */ 
/*  99 */     AffineTransform transform = new AffineTransform();
/*     */ 
/* 101 */     if (IDecoratedChartObject.Placement.Beginning == placement) {
/* 102 */       transform.translate(0.0D, y - decorationShape.getBounds().getCenterY());
/*     */     }
/* 104 */     else if (IDecoratedChartObject.Placement.End == placement) {
/* 105 */       transform.translate(decorationShape.getBounds().getWidth(), decorationShape.getBounds().getHeight());
/* 106 */       transform.quadrantRotate(2);
/* 107 */       decorationShape = transform.createTransformedShape(decorationShape);
/* 108 */       transform.setToIdentity();
/* 109 */       transform.translate(width - decorationShape.getBounds().getWidth(), y - decorationShape.getBounds().getCenterY());
/*     */     }
/*     */ 
/* 112 */     g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
/* 113 */     g2d.fill(transform.createTransformedShape(decorationShape));
/*     */   }
/*     */ 
/*     */   protected Rectangle drawTextAsLabel(Graphics g, IMapper mapper, Rectangle labelDimension, FormattersManager generalFormatter, DrawingsLabelHelper drawingsLabelHelper, ChartObjectDrawingMode drawingMode)
/*     */   {
/* 118 */     if ((this.text == null) || (this.text.length() == 0)) {
/* 119 */       return ZERO_RECTANGLE;
/*     */     }
/* 121 */     FontMetrics fontMetrics = g.getFontMetrics();
/* 122 */     String label = getAdjustedLabel(this.text);
/* 123 */     Rectangle2D stringBounds = fontMetrics.getStringBounds(label, g);
/* 124 */     int x = (int)(mapper.getWidth() - stringBounds.getWidth() - 2.0D);
/* 125 */     int y = (int)(mapper.yv(this.prices[0]) - stringBounds.getHeight() / 2.0D + 2.0D);
/* 126 */     Rectangle rect = new Rectangle(x, y, (int)stringBounds.getWidth(), (int)stringBounds.getHeight());
/* 127 */     g.drawString(label, rect.x, rect.y + rect.height - 3);
/* 128 */     return rect;
/*     */   }
/*     */ 
/*     */   public final void modifyEditedDrawing(Point newPoint, Point prevPoint, IMapper mapper, int defaultRange)
/*     */   {
/* 133 */     this.prices[0] = mapper.vy(newPoint.y);
/*     */   }
/*     */ 
/*     */   public final void modifyNewDrawing(Point newPoint, IMapper mapper, int defaultRange)
/*     */   {
/* 138 */     this.prices[0] = mapper.vy(newPoint.y);
/*     */   }
/*     */ 
/*     */   public final boolean addNewPoint(Point point, IMapper mapper)
/*     */   {
/* 143 */     this.prices[0] = mapper.vy(point.y);
/* 144 */     return true;
/*     */   }
/*     */ 
/*     */   public List<Point> getHandlerMiddlePoints(IMapper mapper)
/*     */   {
/* 149 */     List handlerMiddlePoints = getHandlerMiddlePoints();
/*     */ 
/* 151 */     int y = mapper.yv(this.prices[0]);
/* 152 */     int oneThird = mapper.getWidth() / 3;
/* 153 */     handlerMiddlePoints.add(new Point(oneThird, y));
/* 154 */     handlerMiddlePoints.add(new Point(oneThird * 2, y));
/* 155 */     return handlerMiddlePoints;
/*     */   }
/*     */ 
/*     */   public boolean intersects(Point point, IMapper mapper, int range)
/*     */   {
/* 160 */     int y = mapper.yv(this.prices[0]);
/* 161 */     return (point.y <= y + range) && (point.y >= y - range);
/*     */   }
/*     */ 
/*     */   public void updateSelectedHandler(Point point, IMapper mapper, int defaultRange)
/*     */   {
/*     */   }
/*     */ 
/*     */   public final void moveLeft(IMapper mapper)
/*     */   {
/*     */   }
/*     */ 
/*     */   public final void moveRight(IMapper mapper)
/*     */   {
/*     */   }
/*     */ 
/*     */   public final int getPointsCount()
/*     */   {
/* 181 */     return 1;
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/* 186 */     return "HLine";
/*     */   }
/*     */ 
/*     */   public HLineChartObject clone()
/*     */   {
/* 191 */     return new HLineChartObject(this);
/*     */   }
/*     */ 
/*     */   public String getLocalizationKey()
/*     */   {
/* 196 */     return "item.horizontal.line";
/*     */   }
/*     */ 
/*     */   public void setTime(int pointIndex, long timeValue)
/*     */   {
/*     */   }
/*     */ 
/*     */   public boolean isSticky()
/*     */   {
/* 205 */     return false;
/*     */   }
/*     */ 
/*     */   public boolean hasTimeValue()
/*     */   {
/* 210 */     return false;
/*     */   }
/*     */ 
/*     */   public boolean isLabelEnabled()
/*     */   {
/* 219 */     return true;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Charts-5.48.jar
 * Qualified Name:     com.dukascopy.charts.drawings.HLineChartObject
 * JD-Core Version:    0.6.0
 */