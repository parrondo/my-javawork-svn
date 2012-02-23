/*     */ package com.dukascopy.charts.view.drawingstrategies.tick;
/*     */ 
/*     */ import com.dukascopy.charts.chartbuilder.ChartState;
/*     */ import com.dukascopy.charts.mappers.time.ITimeToXMapper;
/*     */ import com.dukascopy.charts.mappers.value.IValueToYMapper;
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
/*     */ import java.awt.geom.Ellipse2D.Float;
/*     */ import java.awt.geom.GeneralPath;
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Map.Entry;
/*     */ import javax.swing.JComponent;
/*     */ 
/*     */ public class OrdersTickDrawingLogic extends OrdersDrawingLogic
/*     */ {
/*  35 */   private static final Map<OpenCloseTriangle, OpenCloseTriangle> CLOSING_POINT_TRIANGLES = new HashMap();
/*     */ 
/*     */   public OrdersTickDrawingLogic(OrdersDrawingManager ordersDrawingManager, ITimeToXMapper timeToXMapper, IValueToYMapper valueToYMapper, ChartState chartState)
/*     */   {
/*  43 */     super(ordersDrawingManager, timeToXMapper, valueToYMapper, chartState);
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
/*  56 */     CLOSING_POINT_TRIANGLES.clear();
/*  57 */     for (Map.Entry entry : this.ordersDrawingManager.getOpeningPoints().entrySet()) {
/*  58 */       makeUniqueClosingPoints(jComponent, (OrderPoint)entry.getValue(), true);
/*     */     }
/*  60 */     for (Map.Entry entry : this.ordersDrawingManager.getClosingPoints().entrySet()) {
/*  61 */       makeUniqueClosingPoints(jComponent, (OrderPoint)entry.getValue(), false);
/*     */     }
/*  63 */     for (OpenCloseTriangle closingTriangle : CLOSING_POINT_TRIANGLES.keySet())
/*  64 */       renderOrderPoint(g, jComponent, closingTriangle);
/*     */   }
/*     */ 
/*     */   private void makeUniqueClosingPoints(JComponent jComponent, OrderPoint orderPoint, boolean opening)
/*     */   {
/*  69 */     float x = getX(orderPoint);
/*  70 */     float y = getY(orderPoint);
/*  71 */     if ((Float.isNaN(x)) || (Float.isNaN(y)) || (x + 5.0F < jComponent.getX()) || (x - 5.0F > jComponent.getX() + jComponent.getWidth()))
/*     */     {
/*  73 */       return;
/*     */     }
/*  75 */     OpenCloseTriangle triangle = new OpenCloseTriangle(null);
/*  76 */     triangle.buy = orderPoint.isBuy();
/*  77 */     triangle.x = (x + 0.5F);
/*  78 */     triangle.y = (y + 0.5F);
/*  79 */     triangle.opening = opening;
/*  80 */     OpenCloseTriangle alreadySaved = (OpenCloseTriangle)CLOSING_POINT_TRIANGLES.get(triangle);
/*  81 */     if (alreadySaved == null) {
/*  82 */       alreadySaved = triangle;
/*  83 */       CLOSING_POINT_TRIANGLES.put(alreadySaved, alreadySaved);
/*     */     }
/*  85 */     alreadySaved.orderPoints.add(orderPoint);
/*     */   }
/*     */ 
/*     */   private void renderOrderPoint(Graphics2D g, JComponent jComponent, OpenCloseTriangle closingTriangle) {
/*  89 */     float x = closingTriangle.x;
/*  90 */     float y = closingTriangle.y;
/*  91 */     if ((Float.isNaN(x)) || (Float.isNaN(y)) || (x + 5.0F < jComponent.getX()) || (x - 5.0F > jComponent.getX() + jComponent.getWidth()) || (y + 5.0F < jComponent.getY()) || (y - 5.0F > jComponent.getY() + jComponent.getHeight()))
/*     */     {
/*  93 */       return;
/*     */     }
/*     */     Ellipse2D.Float ellipse;
/*     */     GeneralPath arrowPath;
/*  96 */     if (!closingTriangle.orderPoints.isEmpty()) {
/*  97 */       Color arrowColor = ((OrderPoint)closingTriangle.orderPoints.get(0)).getArrowColor();
/*  98 */       Color pointColor = ((OrderPoint)closingTriangle.orderPoints.get(0)).getPointColor();
/*  99 */       ellipse = (Ellipse2D.Float)((OrderPoint)closingTriangle.orderPoints.get(0)).getPointShape();
/* 100 */       if (ellipse == null) {
/* 101 */         ellipse = new Ellipse2D.Float();
/*     */       }
/* 103 */       ellipse.x = (x - 2.0F);
/* 104 */       ellipse.y = (y - 2.0F);
/* 105 */       ellipse.height = 4.0F;
/* 106 */       ellipse.width = 4.0F;
/* 107 */       g.setColor(pointColor);
/* 108 */       g.fill(ellipse);
/*     */ 
/* 110 */       if (((closingTriangle.buy) && (closingTriangle.opening)) || ((!closingTriangle.buy) && (!closingTriangle.opening)))
/* 111 */         y += 3.0F;
/*     */       else {
/* 113 */         y -= 3.0F;
/*     */       }
/*     */ 
/* 116 */       arrowPath = ((OrderPoint)closingTriangle.orderPoints.get(0)).getArrowPath();
/* 117 */       if (((closingTriangle.buy) && (closingTriangle.opening)) || ((!closingTriangle.buy) && (!closingTriangle.opening))) {
/* 118 */         GeneralPath path = arrowPath;
/* 119 */         path.reset();
/* 120 */         path.moveTo(x, y);
/* 121 */         path.lineTo(x - 5.0F, y + 5.0F);
/* 122 */         path.lineTo(x + 5.0F, y + 5.0F);
/* 123 */         path.closePath();
/*     */       } else {
/* 125 */         GeneralPath path = arrowPath;
/* 126 */         path.reset();
/* 127 */         path.moveTo(x, y);
/* 128 */         path.lineTo(x - 5.0F, y - 5.0F);
/* 129 */         path.lineTo(x + 5.0F, y - 5.0F);
/* 130 */         path.closePath();
/*     */       }
/* 132 */       g.setColor(arrowColor);
/* 133 */       g.fill(arrowPath);
/*     */ 
/* 135 */       Color color = this.chartState.getTheme().getColor(ITheme.ChartElement.ORDER_TRACKING_LINE);
/* 136 */       Font font = this.chartState.getTheme().getFont(ITheme.TextElement.ORDER);
/* 137 */       FontMetrics fm = g.getFontMetrics();
/* 138 */       if (((closingTriangle.buy) && (closingTriangle.opening)) || ((!closingTriangle.buy) && (!closingTriangle.opening))) {
/* 139 */         y += 5.0F;
/* 140 */         y += fm.getLeading();
/*     */       } else {
/* 142 */         y -= 5.0F;
/* 143 */         y -= fm.getDescent();
/*     */       }
/*     */       TextSegment textSegment;
/* 145 */       if (closingTriangle.orderPoints.size() < 4) {
/* 146 */         for (OrderPoint orderPoint : closingTriangle.orderPoints) {
/* 147 */           String text = orderPoint.getText();
/* 148 */           if ((text != null) && (!text.equals(""))) {
/* 149 */             float textWidth = fm.stringWidth(text);
/*     */             TextSegment textSegment;
/* 152 */             if (((closingTriangle.buy) && (closingTriangle.opening)) || ((!closingTriangle.buy) && (!closingTriangle.opening))) {
/* 153 */               TextSegment textSegment = new TextSegment(text, x - textWidth / 2.0F, y + fm.getAscent(), color, null, font, new Rectangle(0, 0, jComponent.getWidth(), jComponent.getHeight()));
/*     */ 
/* 155 */               y += fm.getHeight();
/*     */             } else {
/* 157 */               textSegment = new TextSegment(text, x - textWidth / 2.0F, y, color, null, font, new Rectangle(0, 0, jComponent.getWidth(), jComponent.getHeight()));
/*     */ 
/* 159 */               y -= fm.getHeight();
/*     */             }
/* 161 */             orderPoint.setTextSegment(textSegment);
/* 162 */             textSegment.render(g);
/*     */           }
/*     */         }
/*     */       }
/*     */       else
/*     */       {
/* 168 */         if (((closingTriangle.buy) && (closingTriangle.opening)) || ((!closingTriangle.buy) && (!closingTriangle.opening))) {
/* 169 */           TextSegment textSegment = new TextSegment("...", x - fm.stringWidth("...") / 2, y + fm.getAscent(), color, null, font, new Rectangle(0, 0, jComponent.getWidth(), jComponent.getHeight()));
/*     */ 
/* 171 */           y += fm.getHeight();
/*     */         } else {
/* 173 */           textSegment = new TextSegment("...", x - fm.stringWidth("...") / 2, y, color, null, font, new Rectangle(0, 0, jComponent.getWidth(), jComponent.getHeight()));
/*     */ 
/* 175 */           y -= fm.getHeight();
/*     */         }
/* 177 */         textSegment.render(g);
/* 178 */         for (OrderPoint orderPoint : closingTriangle.orderPoints) {
/* 179 */           orderPoint.setTextSegment(textSegment);
/*     */         }
/*     */       }
/* 182 */       for (OrderPoint orderPoint : closingTriangle.orderPoints) {
/* 183 */         orderPoint.setPointShape(ellipse);
/* 184 */         orderPoint.setArrowPath(arrowPath);
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   private static class OpenCloseTriangle
/*     */   {
/*     */     public boolean buy;
/*     */     public float x;
/*     */     public float y;
/*     */     public boolean opening;
/* 260 */     public final List<OrderPoint> orderPoints = new ArrayList();
/*     */ 
/*     */     public int hashCode()
/*     */     {
/* 264 */       int prime = 31;
/* 265 */       int result = 1;
/* 266 */       result = 31 * result + (this.buy ? 1231 : 1237);
/* 267 */       result = 31 * result + (this.opening ? 1231 : 1237);
/* 268 */       return result;
/*     */     }
/*     */ 
/*     */     public boolean equals(Object obj)
/*     */     {
/* 273 */       if ((obj instanceof OpenCloseTriangle)) {
/* 274 */         OpenCloseTriangle o = (OpenCloseTriangle)obj;
/* 275 */         return (this.buy == o.buy) && (Math.abs(this.x - o.x) <= 3.0F) && (Math.abs(this.y - o.y) <= 3.0F) && (this.opening == o.opening);
/*     */       }
/* 277 */       return false;
/*     */     }
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Charts-5.48.jar
 * Qualified Name:     com.dukascopy.charts.view.drawingstrategies.tick.OrdersTickDrawingLogic
 * JD-Core Version:    0.6.0
 */