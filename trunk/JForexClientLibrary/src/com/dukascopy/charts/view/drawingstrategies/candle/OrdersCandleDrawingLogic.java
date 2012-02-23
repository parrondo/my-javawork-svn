/*     */ package com.dukascopy.charts.view.drawingstrategies.candle;
/*     */ 
/*     */ import com.dukascopy.charts.chartbuilder.ChartState;
/*     */ import com.dukascopy.charts.data.AbstractDataSequenceProvider;
/*     */ import com.dukascopy.charts.data.datacache.CandleData;
/*     */ import com.dukascopy.charts.mappers.time.GeometryCalculator;
/*     */ import com.dukascopy.charts.mappers.time.ITimeToXMapper;
/*     */ import com.dukascopy.charts.mappers.value.IValueToYMapper;
/*     */ import com.dukascopy.charts.math.dataprovider.AbstractDataSequence;
/*     */ import com.dukascopy.charts.orders.OrdersDrawingManager;
/*     */ import com.dukascopy.charts.orders.orderparts.ClosingPoint;
/*     */ import com.dukascopy.charts.orders.orderparts.OpeningPoint;
/*     */ import com.dukascopy.charts.orders.orderparts.OrderPoint;
/*     */ import com.dukascopy.charts.orders.orderparts.TextSegment;
/*     */ import com.dukascopy.charts.persistence.ITheme;
/*     */ import com.dukascopy.charts.persistence.ITheme.ChartElement;
/*     */ import com.dukascopy.charts.persistence.ITheme.TextElement;
/*     */ import com.dukascopy.charts.view.drawingstrategies.OrdersDrawingLogic;
/*     */ import java.awt.Color;
/*     */ import java.awt.Font;
/*     */ import java.awt.FontMetrics;
/*     */ import java.awt.Graphics;
/*     */ import java.awt.Graphics2D;
/*     */ import java.awt.Rectangle;
/*     */ import java.awt.geom.GeneralPath;
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Map.Entry;
/*     */ import javax.swing.JComponent;
/*     */ 
/*     */ public class OrdersCandleDrawingLogic<T extends AbstractDataSequence<D>, D extends CandleData> extends OrdersDrawingLogic
/*     */ {
/*  38 */   private static final Map<OrderPointTriangle, OrderPointTriangle> orderPointTriangles = new HashMap();
/*  39 */   private static final Map<OrderPointArrow, OrderPointArrow> orderPointArrows = new HashMap();
/*     */   final AbstractDataSequenceProvider<T, D> dataSequenceProvider;
/*     */   final GeometryCalculator geometryCalculator;
/*     */ 
/*     */   public OrdersCandleDrawingLogic(OrdersDrawingManager ordersDrawingManager, AbstractDataSequenceProvider<T, D> dataSequenceProvider, ITimeToXMapper timeToXMapper, IValueToYMapper valueToYMapper, GeometryCalculator geometryCalculator, ChartState chartState)
/*     */   {
/*  52 */     super(ordersDrawingManager, timeToXMapper, valueToYMapper, chartState);
/*  53 */     this.dataSequenceProvider = dataSequenceProvider;
/*  54 */     this.geometryCalculator = geometryCalculator;
/*     */   }
/*     */ 
/*     */   protected void plotClosingPoint(Graphics g, ClosingPoint closingPoint)
/*     */   {
/*     */   }
/*     */ 
/*     */   protected void plotOpeningPoint(Graphics g, OpeningPoint openingPoint)
/*     */   {
/*     */   }
/*     */ 
/*     */   protected void renderOpeningClosingPoints(Graphics2D g, JComponent jComponent)
/*     */   {
/*  69 */     orderPointTriangles.clear();
/*  70 */     for (OpeningPoint openingPoint : this.ordersDrawingManager.getOpeningPoints().values()) {
/*  71 */       makeUniqueTriangles(jComponent, openingPoint);
/*     */     }
/*  73 */     for (ClosingPoint closingPoint : this.ordersDrawingManager.getClosingPoints().values()) {
/*  74 */       makeUniqueTriangles(jComponent, closingPoint);
/*     */     }
/*     */ 
/*  77 */     for (OrderPointTriangle orderPointTriangle : orderPointTriangles.keySet()) {
/*  78 */       renderUniqueTriangle(g, jComponent, orderPointTriangle);
/*     */     }
/*     */ 
/*  82 */     orderPointArrows.clear();
/*  83 */     for (Map.Entry entry : this.ordersDrawingManager.getOpeningPoints().entrySet()) {
/*  84 */       makeUniqueArrows(jComponent, (OrderPoint)entry.getValue(), true);
/*     */     }
/*  86 */     for (Map.Entry entry : this.ordersDrawingManager.getClosingPoints().entrySet()) {
/*  87 */       makeUniqueArrows(jComponent, (OrderPoint)entry.getValue(), false);
/*     */     }
/*  89 */     renderUniqueArrows(g, jComponent);
/*     */   }
/*     */ 
/*     */   private void makeUniqueTriangles(JComponent jComponent, OrderPoint orderPoint) {
/*  93 */     float x = getX(orderPoint);
/*  94 */     float y = getY(orderPoint);
/*  95 */     if ((Float.isNaN(x)) || (Float.isNaN(y)) || (x + 5.0F < jComponent.getX()) || (x - 5.0F > jComponent.getX() + jComponent.getWidth())) {
/*  96 */       return;
/*     */     }
/*  98 */     OrderPointTriangle triangle = new OrderPointTriangle(null);
/*  99 */     triangle.buy = orderPoint.isBuy();
/* 100 */     triangle.x = (x + 0.5F);
/* 101 */     triangle.y = (y + 0.5F);
/* 102 */     OrderPointTriangle alreadySaved = (OrderPointTriangle)orderPointTriangles.get(triangle);
/* 103 */     if (alreadySaved == null) {
/* 104 */       alreadySaved = triangle;
/* 105 */       orderPointTriangles.put(alreadySaved, alreadySaved);
/*     */     }
/* 107 */     alreadySaved.orderPoints.add(orderPoint);
/*     */   }
/*     */ 
/*     */   private void renderUniqueTriangle(Graphics2D g, JComponent jComponent, OrderPointTriangle orderPointTriangle) {
/* 111 */     float x = orderPointTriangle.x;
/* 112 */     float y = orderPointTriangle.y;
/* 113 */     if ((x + 5.0F < jComponent.getX()) || (x - 5.0F > jComponent.getX() + jComponent.getWidth()) || (y + 5.0F < jComponent.getY()) || (y - 5.0F > jComponent.getY() + jComponent.getHeight())) {
/* 114 */       return;
/*     */     }
/*     */ 
/* 117 */     Color triangleColor = ((OrderPoint)orderPointTriangle.orderPoints.get(0)).getTriangleColor();
/* 118 */     GeneralPath path = new GeneralPath();
/* 119 */     path.moveTo(x, y);
/* 120 */     path.lineTo(orderPointTriangle.open ? x - 3.0F : x + 3.0F, y - 3.0F);
/* 121 */     path.lineTo(orderPointTriangle.open ? x - 3.0F : x + 3.0F, y + 3.0F);
/* 122 */     path.closePath();
/* 123 */     g.setColor(triangleColor);
/* 124 */     g.fill(path);
/*     */ 
/* 126 */     for (OrderPoint orderPoint : orderPointTriangle.orderPoints)
/* 127 */       orderPoint.setTrianglePath(path);
/*     */   }
/*     */ 
/*     */   private void makeUniqueArrows(JComponent jComponent, OrderPoint orderPoint, boolean open)
/*     */   {
/* 132 */     float x = getX(orderPoint);
/* 133 */     float y = getY(orderPoint);
/* 134 */     if ((Float.isNaN(x)) || (Float.isNaN(y)) || (x + 5.0F < jComponent.getX()) || (x - 5.0F > jComponent.getX() + jComponent.getWidth())) {
/* 135 */       return;
/*     */     }
/* 137 */     OrderPointArrow arrow = new OrderPointArrow(null);
/* 138 */     arrow.up = (((orderPoint.isBuy()) && (open)) || ((!orderPoint.isBuy()) && (!open)));
/* 139 */     arrow.x = (x + 0.5F);
/* 140 */     OrderPointArrow alreadySaved = (OrderPointArrow)orderPointArrows.get(arrow);
/* 141 */     if (alreadySaved == null) {
/* 142 */       alreadySaved = arrow;
/* 143 */       orderPointArrows.put(alreadySaved, alreadySaved);
/*     */     }
/* 145 */     if (open)
/* 146 */       alreadySaved.openOrderPoints.add(orderPoint);
/*     */     else
/* 148 */       alreadySaved.closeOrderPoints.add(orderPoint);
/*     */   }
/*     */ 
/*     */   private void renderUniqueArrows(Graphics2D g, JComponent jComponent)
/*     */   {
/* 153 */     AbstractDataSequence dataSequence = (AbstractDataSequence)this.dataSequenceProvider.getDataSequence();
/*     */ 
/* 155 */     Rectangle rect = jComponent.getBounds();
/* 156 */     for (OrderPointArrow arrow : orderPointArrows.keySet()) {
/* 157 */       float x = arrow.x;
/*     */ 
/* 159 */       OrderPoint orderPoint = arrow.openOrderPoints.isEmpty() ? (OrderPoint)arrow.closeOrderPoints.get(0) : (OrderPoint)arrow.openOrderPoints.get(0);
/*     */ 
/* 162 */       float y = getY(orderPoint);
/*     */ 
/* 164 */       int candleIndex = dataSequence.indexOf(orderPoint.getTime());
/* 165 */       CandleData candleData = (CandleData)dataSequence.getData(candleIndex - 1);
/*     */ 
/* 167 */       if ((candleIndex != -1) && (candleData != null)) {
/* 168 */         double price = arrow.up ? candleData.low : candleData.high;
/* 169 */         float priceY = this.valueToYMapper.yv(price);
/* 170 */         if (!Float.isNaN(priceY)) {
/* 171 */           y = priceY;
/*     */         }
/*     */       }
/*     */ 
/* 175 */       if (arrow.up)
/* 176 */         y += 3.0F;
/*     */       else {
/* 178 */         y -= 3.0F;
/*     */       }
/*     */ 
/* 181 */       if ((y + 50.0F < rect.y) || (y - 50.0F > rect.y + rect.height) || (x + 5.0F < rect.x) || (x - 5.0F > rect.x + rect.width))
/*     */       {
/*     */         continue;
/*     */       }
/*     */ 
/* 187 */       y = drawOrderPointsForArrow(g, jComponent, arrow, arrow.openOrderPoints, x, y);
/*     */ 
/* 190 */       y = drawOrderPointsForArrow(g, jComponent, arrow, arrow.closeOrderPoints, x, y);
/*     */     }
/*     */   }
/*     */ 
/*     */   private float drawOrderPointsForArrow(Graphics2D g, JComponent jComponent, OrderPointArrow arrow, List<OrderPoint> orderPoints, float x, float y) {
/* 195 */     Color color = this.chartState.getTheme().getColor(ITheme.ChartElement.ORDER_TRACKING_LINE);
/* 196 */     Font font = this.chartState.getTheme().getFont(ITheme.TextElement.ORDER);
/* 197 */     FontMetrics fm = g.getFontMetrics(font);
/*     */     TextSegment textSegment;
/* 198 */     if (!orderPoints.isEmpty()) {
/* 199 */       GeneralPath arrowPath = new GeneralPath();
/* 200 */       if (arrow.up) {
/* 201 */         arrowPath.moveTo(x, y);
/* 202 */         arrowPath.lineTo(x - 5.0F, y + 5.0F);
/* 203 */         arrowPath.lineTo(x + 5.0F, y + 5.0F);
/* 204 */         arrowPath.closePath();
/* 205 */         y += 5.0F;
/*     */       } else {
/* 207 */         arrowPath.moveTo(x, y);
/* 208 */         arrowPath.lineTo(x - 5.0F, y - 5.0F);
/* 209 */         arrowPath.lineTo(x + 5.0F, y - 5.0F);
/* 210 */         arrowPath.closePath();
/* 211 */         y -= 5.0F;
/*     */       }
/*     */ 
/* 214 */       g.setColor(((OrderPoint)orderPoints.get(0)).getArrowColor());
/* 215 */       g.fill(arrowPath);
/* 216 */       for (OrderPoint orderPoint : orderPoints) {
/* 217 */         orderPoint.setArrowPath(arrowPath);
/*     */       }
/*     */ 
/* 221 */       if (arrow.up)
/* 222 */         y += fm.getLeading();
/*     */       else {
/* 224 */         y -= fm.getDescent();
/*     */       }
/*     */ 
/* 227 */       if (orderPoints.size() < 4) {
/* 228 */         for (int i = 0; i < orderPoints.size(); i++) {
/* 229 */           OrderPoint orderPoint = (OrderPoint)orderPoints.get(i);
/*     */           TextSegment textSegment;
/* 231 */           if (arrow.up) {
/* 232 */             TextSegment textSegment = new TextSegment(orderPoint.getText(), x - fm.stringWidth(orderPoint.getText()) / 2, y + fm.getAscent(), color, null, font, new Rectangle(0, 0, jComponent.getWidth(), jComponent.getHeight()));
/*     */ 
/* 241 */             y += fm.getHeight();
/*     */           } else {
/* 243 */             textSegment = new TextSegment(orderPoint.getText(), x - fm.stringWidth(orderPoint.getText()) / 2, y, color, null, font, new Rectangle(0, 0, jComponent.getWidth(), jComponent.getHeight()));
/*     */ 
/* 252 */             y -= fm.getHeight();
/*     */           }
/* 254 */           orderPoint.setTextSegment(textSegment);
/* 255 */           textSegment.render(g);
/*     */         }
/*     */       }
/*     */       else {
/* 259 */         if (arrow.up) {
/* 260 */           TextSegment textSegment = new TextSegment("...", x - fm.stringWidth("...") / 2, y + fm.getAscent(), color, null, font, new Rectangle(0, 0, jComponent.getWidth(), jComponent.getHeight()));
/*     */ 
/* 269 */           y += fm.getHeight();
/*     */         } else {
/* 271 */           textSegment = new TextSegment("...", x - fm.stringWidth("...") / 2, y, color, null, font, new Rectangle(0, 0, jComponent.getWidth(), jComponent.getHeight()));
/*     */ 
/* 280 */           y -= fm.getHeight();
/*     */         }
/* 282 */         textSegment.render(g);
/* 283 */         for (OrderPoint orderPoint : orderPoints) {
/* 284 */           orderPoint.setTextSegment(textSegment);
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/* 289 */     return y;
/*     */   }
/*     */ 
/*     */   private static class OrderPointArrow
/*     */   {
/*     */     public boolean up;
/*     */     public float x;
/* 325 */     public final List<OrderPoint> openOrderPoints = new ArrayList();
/* 326 */     public final List<OrderPoint> closeOrderPoints = new ArrayList();
/*     */ 
/*     */     public boolean equals(Object obj)
/*     */     {
/* 330 */       if ((obj instanceof OrderPointArrow)) {
/* 331 */         OrderPointArrow o = (OrderPointArrow)obj;
/* 332 */         return (this.up == o.up) && (this.x == o.x);
/*     */       }
/* 334 */       return false;
/*     */     }
/*     */ 
/*     */     public int hashCode()
/*     */     {
/* 339 */       int val = 1;
/* 340 */       val = (int)(val + (val * 37 + this.x));
/* 341 */       val += val * 37 + (this.up ? 37 : 74);
/* 342 */       return val * 37;
/*     */     }
/*     */   }
/*     */ 
/*     */   private static class OrderPointTriangle
/*     */   {
/*     */     public boolean buy;
/*     */     public boolean open;
/*     */     public float x;
/*     */     public float y;
/* 299 */     public final List<OrderPoint> orderPoints = new ArrayList();
/*     */ 
/*     */     public boolean equals(Object obj)
/*     */     {
/* 303 */       if ((obj instanceof OrderPointTriangle)) {
/* 304 */         OrderPointTriangle o = (OrderPointTriangle)obj;
/* 305 */         return (this.buy == o.buy) && (this.open == o.open) && (this.x == o.x) && (this.y == o.y);
/*     */       }
/* 307 */       return false;
/*     */     }
/*     */ 
/*     */     public int hashCode()
/*     */     {
/* 312 */       int val = 1;
/* 313 */       val = (int)(val + (val * 37 + this.x));
/* 314 */       val = (int)(val + (val * 37 + this.y));
/* 315 */       val += val * 37 + (this.buy ? 37 : 74);
/* 316 */       val += val * 37 + (this.open ? 37 : 74);
/* 317 */       return val * 37;
/*     */     }
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Charts-5.48.jar
 * Qualified Name:     com.dukascopy.charts.view.drawingstrategies.candle.OrdersCandleDrawingLogic
 * JD-Core Version:    0.6.0
 */