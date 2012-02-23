/*     */ package com.dukascopy.charts.drawings;
/*     */ 
/*     */ import com.dukascopy.api.IChart.Type;
/*     */ import com.dukascopy.api.drawings.IDecoratedChartObject.Decoration;
/*     */ import com.dukascopy.api.drawings.IDecoratedChartObject.Placement;
/*     */ import com.dukascopy.api.drawings.IVerticalLineChartObject;
/*     */ import com.dukascopy.charts.mappers.IMapper;
/*     */ import com.dukascopy.charts.utils.formatter.FormattersManager;
/*     */ import java.awt.Graphics;
/*     */ import java.awt.Graphics2D;
/*     */ import java.awt.Point;
/*     */ import java.awt.Rectangle;
/*     */ import java.awt.RenderingHints;
/*     */ import java.awt.Shape;
/*     */ import java.awt.event.ActionListener;
/*     */ import java.awt.geom.AffineTransform;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ 
/*     */ public class VLineChartObject extends DecoratedChartObject
/*     */   implements IVerticalLineChartObject
/*     */ {
/*     */   private static final long serialVersionUID = 1L;
/*     */   private static final String CHART_OBJECT_NAME = "VLine";
/*     */ 
/*     */   public VLineChartObject(String key)
/*     */   {
/*  26 */     this(key, IChart.Type.VLINE);
/*     */   }
/*     */ 
/*     */   public VLineChartObject() {
/*  30 */     this(null, IChart.Type.VLINE);
/*  31 */     setUnderEdit(true);
/*     */   }
/*     */ 
/*     */   public VLineChartObject(String key, long time) {
/*  35 */     this(key, time, IChart.Type.VLINE);
/*     */   }
/*     */ 
/*     */   public VLineChartObject(VLineChartObject chartObject) {
/*  39 */     super(chartObject);
/*     */   }
/*     */ 
/*     */   public VLineChartObject(String key, IChart.Type type) {
/*  43 */     super(key, type);
/*     */   }
/*     */ 
/*     */   public VLineChartObject(String key, long time, IChart.Type type) {
/*  47 */     this(key, type);
/*  48 */     this.times[0] = time;
/*     */   }
/*     */ 
/*     */   protected boolean isDrawable(IMapper mapper)
/*     */   {
/*  53 */     return (isValidTime(0)) && (!mapper.isXOutOfRange(mapper.xt(this.times[0])));
/*     */   }
/*     */ 
/*     */   public void drawChartObject(Graphics g, IMapper mapper, Rectangle labelDimension, Rectangle textDimension, FormattersManager formattersManager, ChartObjectDrawingMode drawingMode)
/*     */   {
/*  58 */     int x = mapper.xt(this.times[0]);
/*     */ 
/*  60 */     int beginningOffset = 0;
/*  61 */     int endOffset = 0;
/*     */ 
/*  63 */     Map decorations = getDecorations();
/*  64 */     if (decorations != null)
/*     */     {
/*  66 */       for (IDecoratedChartObject.Placement placement : decorations.keySet()) {
/*  67 */         IDecoratedChartObject.Decoration decoration = (IDecoratedChartObject.Decoration)decorations.get(placement);
/*     */ 
/*  71 */         switch (1.$SwitchMap$com$dukascopy$charts$drawings$ChartObjectDrawingMode[drawingMode.ordinal()]) { case 1:
/*     */         case 2:
/*     */         case 3:
/*  73 */           if ((placement != IDecoratedChartObject.Placement.End) && (
/*  74 */             (goto 45) || (
/*  79 */             (placement != IDecoratedChartObject.Placement.Beginning) && (
/*  80 */             (goto 45) || 
/*  85 */             (placement == IDecoratedChartObject.Placement.End) || (placement == IDecoratedChartObject.Placement.Beginning)))))
/*     */           {
/*     */             continue;
/*     */           }
/*     */ 
/*     */         case 4:
/*     */         default:
/*  95 */           drawDecoration(placement, (IDecoratedChartObject.Decoration)decorations.get(placement), g, x, mapper.getHeight());
/*     */ 
/*  97 */           if (IDecoratedChartObject.Decoration.None != decoration) {
/*  98 */             if (IDecoratedChartObject.Placement.Beginning == placement) {
/*  99 */               beginningOffset = getLineWidth();
/*     */             }
/* 101 */             else if (IDecoratedChartObject.Placement.End == placement) {
/* 102 */               endOffset = getLineWidth();
/*     */             }
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/* 108 */     g.drawLine(x, beginningOffset, x, mapper.getHeight() - endOffset);
/*     */   }
/*     */ 
/*     */   private void drawDecoration(IDecoratedChartObject.Placement placement, IDecoratedChartObject.Decoration decoration, Graphics g, int x, int height)
/*     */   {
/* 115 */     Graphics2D g2d = (Graphics2D)g;
/*     */ 
/* 117 */     Shape decorationShape = getShape(decoration);
/* 118 */     decorationShape = scaleToCurrentStroke(decorationShape);
/*     */ 
/* 120 */     AffineTransform transform = new AffineTransform();
/*     */ 
/* 122 */     if (IDecoratedChartObject.Placement.Beginning == placement) {
/* 123 */       transform.translate(decorationShape.getBounds().getWidth(), 0.0D);
/* 124 */       transform.quadrantRotate(1);
/* 125 */       decorationShape = transform.createTransformedShape(decorationShape);
/* 126 */       transform.setToIdentity();
/* 127 */       transform.translate(x - decorationShape.getBounds().getCenterX(), 0.0D);
/* 128 */     } else if (IDecoratedChartObject.Placement.End == placement) {
/* 129 */       transform.translate(0.0D, decorationShape.getBounds().getHeight());
/* 130 */       transform.quadrantRotate(-1);
/* 131 */       decorationShape = transform.createTransformedShape(decorationShape);
/* 132 */       transform.setToIdentity();
/* 133 */       transform.translate(x - decorationShape.getBounds().getCenterX(), height - decorationShape.getBounds().getHeight());
/*     */     }
/*     */ 
/* 136 */     g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
/* 137 */     g2d.fill(transform.createTransformedShape(decorationShape));
/*     */   }
/*     */ 
/*     */   protected Rectangle drawTextAsLabel(Graphics g, IMapper mapper, Rectangle labelDimension, FormattersManager generalFormatter, DrawingsLabelHelper drawingsLabelHelper, ChartObjectDrawingMode drawingMode)
/*     */   {
/* 145 */     if ((drawingMode == ChartObjectDrawingMode.GLOBAL_ON_MAIN_WITH_SUBCHARTS) || (drawingMode == ChartObjectDrawingMode.DEFAULT)) {
/* 146 */       return drawingsLabelHelper.drawTimeMarkerText(g, mapper, this, this.times[0], getLineWidth(), getText(), null);
/*     */     }
/* 148 */     return ZERO_RECTANGLE;
/*     */   }
/*     */ 
/*     */   public void modifyEditedDrawing(Point point, Point prevPoint, IMapper mapper, int defaultRange)
/*     */   {
/* 154 */     this.times[0] = mapper.tx(point.x);
/*     */   }
/*     */ 
/*     */   public void modifyNewDrawing(Point point, IMapper mapper, int defaultRange)
/*     */   {
/* 159 */     this.times[0] = mapper.tx(point.x);
/*     */   }
/*     */ 
/*     */   public boolean addNewPoint(Point point, IMapper mapper)
/*     */   {
/* 164 */     this.times[0] = mapper.tx(point.x);
/* 165 */     return true;
/*     */   }
/*     */ 
/*     */   public List<Point> getHandlerMiddlePoints(IMapper mapper)
/*     */   {
/* 170 */     List handlerMiddlePoints = getHandlerMiddlePoints();
/*     */ 
/* 172 */     int x = mapper.xt(this.times[0]);
/* 173 */     int oneThird = mapper.getHeight() / 3;
/* 174 */     handlerMiddlePoints.add(new Point(x, oneThird));
/* 175 */     handlerMiddlePoints.add(new Point(x, oneThird * 2));
/* 176 */     return handlerMiddlePoints;
/*     */   }
/*     */ 
/*     */   public boolean intersects(Point point, IMapper mapper, int range)
/*     */   {
/* 181 */     float x = mapper.xt(this.times[0]);
/* 182 */     return (point.x >= x - range) && (point.x <= x + range);
/*     */   }
/*     */ 
/*     */   public void updateSelectedHandler(Point point, IMapper mapper, int range)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void move(long time, double price)
/*     */   {
/* 192 */     this.times[0] = time;
/* 193 */     getActionListener().actionPerformed(null);
/*     */   }
/*     */ 
/*     */   public int getPointsCount()
/*     */   {
/* 198 */     return 1;
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/* 203 */     return "VLine";
/*     */   }
/*     */ 
/*     */   public boolean isGlobal()
/*     */   {
/* 208 */     return true;
/*     */   }
/*     */ 
/*     */   public VLineChartObject clone()
/*     */   {
/* 213 */     return new VLineChartObject(this);
/*     */   }
/*     */ 
/*     */   public String getLocalizationKey()
/*     */   {
/* 218 */     return "item.vertical.line";
/*     */   }
/*     */ 
/*     */   public void setTime(int index, long time) {
/* 222 */     validatePointIndex(Integer.valueOf(index));
/* 223 */     this.times[index] = time;
/*     */   }
/*     */ 
/*     */   public void setPrice(int pointIndex, double priceValue) {
/* 227 */     validatePointIndex(Integer.valueOf(pointIndex));
/* 228 */     this.prices[pointIndex] = priceValue;
/*     */   }
/*     */ 
/*     */   public boolean isSticky()
/*     */   {
/* 233 */     return false;
/*     */   }
/*     */ 
/*     */   public boolean hasPriceValue()
/*     */   {
/* 238 */     return false;
/*     */   }
/*     */ 
/*     */   public boolean isLabelEnabled()
/*     */   {
/* 246 */     return true;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Charts-5.48.jar
 * Qualified Name:     com.dukascopy.charts.drawings.VLineChartObject
 * JD-Core Version:    0.6.0
 */