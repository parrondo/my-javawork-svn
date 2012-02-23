/*     */ package com.dukascopy.charts.view.drawingstrategies;
/*     */ 
/*     */ import com.dukascopy.charts.chartbuilder.ChartState;
/*     */ import com.dukascopy.charts.mappers.time.ITimeToXMapper;
/*     */ import com.dukascopy.charts.mappers.value.IValueToYMapper;
/*     */ import com.dukascopy.charts.orders.OrdersDrawingManager;
/*     */ import com.dukascopy.charts.orders.orderparts.ClosingLine;
/*     */ import com.dukascopy.charts.orders.orderparts.ClosingPoint;
/*     */ import com.dukascopy.charts.orders.orderparts.EntryLine;
/*     */ import com.dukascopy.charts.orders.orderparts.HorizontalLine;
/*     */ import com.dukascopy.charts.orders.orderparts.MergingLine;
/*     */ import com.dukascopy.charts.orders.orderparts.OpeningPoint;
/*     */ import com.dukascopy.charts.orders.orderparts.OrderPoint;
/*     */ import com.dukascopy.charts.orders.orderparts.ShortLine;
/*     */ import com.dukascopy.charts.orders.orderparts.StopLossLine;
/*     */ import com.dukascopy.charts.orders.orderparts.TakeProfitLine;
/*     */ import com.dukascopy.charts.orders.orderparts.TextSegment;
/*     */ import com.dukascopy.charts.persistence.ITheme;
/*     */ import com.dukascopy.charts.persistence.ITheme.TextElement;
/*     */ import com.dukascopy.charts.view.displayabledatapart.IOrdersDrawingStrategy;
/*     */ import java.awt.BasicStroke;
/*     */ import java.awt.Font;
/*     */ import java.awt.Graphics;
/*     */ import java.awt.Graphics2D;
/*     */ import java.awt.RenderingHints;
/*     */ import java.awt.Stroke;
/*     */ import java.util.HashSet;
/*     */ import java.util.Map;
/*     */ import java.util.Map.Entry;
/*     */ import java.util.Set;
/*     */ import javax.swing.JComponent;
/*     */ 
/*     */ public abstract class OrdersDrawingLogic
/*     */   implements IOrdersDrawingStrategy
/*     */ {
/*  38 */   private static final float[] DASH_PATTERN = { 2.0F, 4.0F };
/*     */   protected final OrdersDrawingManager ordersDrawingManager;
/*     */   protected final ITimeToXMapper timeToXMapper;
/*     */   protected final IValueToYMapper valueToYMapper;
/*     */   protected final ChartState chartState;
/*  45 */   protected final Set<ShortLine> filteringSet = new HashSet();
/*     */ 
/*     */   public OrdersDrawingLogic(OrdersDrawingManager ordersDrawingManager, ITimeToXMapper timeToXMapper, IValueToYMapper valueToYMapper, ChartState chartState)
/*     */   {
/*  53 */     this.ordersDrawingManager = ordersDrawingManager;
/*  54 */     this.timeToXMapper = timeToXMapper;
/*  55 */     this.valueToYMapper = valueToYMapper;
/*  56 */     this.chartState = chartState;
/*     */   }
/*     */ 
/*     */   public void drawSelectedOrders(Graphics g, JComponent jComponent) {
/*  60 */     for (HorizontalLine selectedHorizontalLine : this.ordersDrawingManager.getSelectedHorizontalLines())
/*  61 */       if (selectedHorizontalLine != null) {
/*  62 */         Font font = this.chartState.getTheme().getFont(ITheme.TextElement.ORDER);
/*  63 */         jComponent.setFont(font);
/*  64 */         g.setFont(font);
/*  65 */         g.setColor(selectedHorizontalLine.getColor());
/*  66 */         Graphics2D g2 = (Graphics2D)g;
/*  67 */         selectedHorizontalLine.plotOnSelectedPrice(this.valueToYMapper, g, jComponent.getX(), jComponent.getY(), jComponent.getWidth(), jComponent.getHeight());
/*  68 */         Stroke solidStroke = g2.getStroke();
/*  69 */         BasicStroke stroke = new BasicStroke(1.0F, 0, 0, 10.0F, DASH_PATTERN, 0.0F);
/*  70 */         g2.setStroke(stroke);
/*  71 */         g2.draw(selectedHorizontalLine.getPath());
/*  72 */         TextSegment textSegment = selectedHorizontalLine.getTextSegment();
/*  73 */         if (textSegment != null) {
/*  74 */           textSegment.render(g);
/*     */         }
/*  76 */         g2.setStroke(solidStroke);
/*  77 */         Object hintValue = ((Graphics2D)g).getRenderingHint(RenderingHints.KEY_ANTIALIASING);
/*  78 */         if (hintValue != RenderingHints.VALUE_ANTIALIAS_ON) {
/*  79 */           ((Graphics2D)g).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
/*     */         }
/*  81 */         g2.fill(selectedHorizontalLine.getHandlesPath());
/*  82 */         ((Graphics2D)g).setRenderingHint(RenderingHints.KEY_ANTIALIASING, hintValue);
/*     */       }
/*     */   }
/*     */ 
/*     */   public void drawData(Graphics g, JComponent jComponent)
/*     */   {
/*  88 */     this.ordersDrawingManager.prepareOrderParts();
/*  89 */     plotOrderParts(g, jComponent);
/*  90 */     renderOrderParts(g, jComponent);
/*     */   }
/*     */ 
/*     */   protected float getX(OrderPoint orderPoint) {
/*  94 */     return this.timeToXMapper.xt(orderPoint.getTime());
/*     */   }
/*     */ 
/*     */   protected float getY(OrderPoint orderPoint) {
/*  98 */     return this.valueToYMapper.yv(orderPoint.getPrice());
/*     */   }
/*     */ 
/*     */   protected void plotOrderParts(Graphics g, JComponent jComponent) {
/* 102 */     Font font = this.chartState.getTheme().getFont(ITheme.TextElement.ORDER);
/* 103 */     jComponent.setFont(font);
/* 104 */     g.setFont(font);
/*     */ 
/* 106 */     int x = jComponent.getX();
/* 107 */     int y = jComponent.getY();
/* 108 */     int width = jComponent.getWidth();
/* 109 */     int height = jComponent.getHeight();
/* 110 */     for (Map.Entry entry : this.ordersDrawingManager.getEntryLines().entrySet()) {
/* 111 */       ((EntryLine)entry.getValue()).plot(g, x, y, width, height);
/*     */     }
/* 113 */     for (Map.Entry entry : this.ordersDrawingManager.getStopLossLines().entrySet()) {
/* 114 */       ((StopLossLine)entry.getValue()).plot(g, x, y, width, height);
/*     */     }
/* 116 */     for (Map.Entry entry : this.ordersDrawingManager.getTakeProfitLines().entrySet()) {
/* 117 */       ((TakeProfitLine)entry.getValue()).plot(g, x, y, width, height);
/*     */     }
/* 119 */     for (Map.Entry entry : this.ordersDrawingManager.getOpeningPoints().entrySet()) {
/* 120 */       plotOpeningPoint(g, (OpeningPoint)entry.getValue());
/*     */     }
/* 122 */     for (Map.Entry entry : this.ordersDrawingManager.getMergingLines().entrySet()) {
/* 123 */       ((MergingLine)entry.getValue()).plot(g, x, y, width, height);
/*     */     }
/* 125 */     for (Map.Entry entry : this.ordersDrawingManager.getClosingLines().entrySet()) {
/* 126 */       ((ClosingLine)entry.getValue()).plot(g, x, y, width, height);
/*     */     }
/* 128 */     for (Map.Entry entry : this.ordersDrawingManager.getClosingPoints().entrySet())
/* 129 */       plotClosingPoint(g, (ClosingPoint)entry.getValue());
/*     */   }
/*     */ 
/*     */   protected void renderOrderParts(Graphics g, JComponent jComponent)
/*     */   {
/* 134 */     Font font = this.chartState.getTheme().getFont(ITheme.TextElement.ORDER);
/* 135 */     jComponent.setFont(font);
/* 136 */     g.setFont(font);
/* 137 */     Graphics2D g2 = (Graphics2D)g;
/*     */ 
/* 140 */     g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
/*     */ 
/* 142 */     renderOpeningClosingPoints(g2, jComponent);
/*     */ 
/* 145 */     BasicStroke stroke = new BasicStroke(1.0F, 0, 0, 10.0F, DASH_PATTERN, 0.0F);
/* 146 */     Stroke oldStroke = g2.getStroke();
/* 147 */     g2.setStroke(stroke);
/*     */ 
/* 149 */     this.filteringSet.clear();
/* 150 */     for (Map.Entry entry : this.ordersDrawingManager.getMergingLines().entrySet()) {
/* 151 */       this.filteringSet.add(entry.getValue());
/*     */     }
/* 153 */     for (ShortLine line : this.filteringSet) {
/* 154 */       g.setColor(line.getColor());
/* 155 */       g2.draw(line.getPath());
/*     */     }
/* 157 */     this.filteringSet.clear();
/* 158 */     for (Map.Entry entry : this.ordersDrawingManager.getClosingLines().entrySet()) {
/* 159 */       this.filteringSet.add(entry.getValue());
/*     */     }
/* 161 */     for (ShortLine line : this.filteringSet) {
/* 162 */       g.setColor(line.getColor());
/* 163 */       g2.draw(line.getPath());
/*     */     }
/* 165 */     this.filteringSet.clear();
/*     */ 
/* 168 */     g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
/* 169 */     for (Map.Entry entry : this.ordersDrawingManager.getEntryLines().entrySet()) {
/* 170 */       HorizontalLine line = (HorizontalLine)entry.getValue();
/* 171 */       if (!line.isSelected()) {
/* 172 */         g.setColor(line.getColor());
/* 173 */         g2.draw(line.getPath());
/* 174 */         TextSegment textSegment = line.getTextSegment();
/* 175 */         if (textSegment != null) {
/* 176 */           textSegment.render(g);
/*     */         }
/*     */       }
/*     */     }
/* 180 */     for (Map.Entry entry : this.ordersDrawingManager.getStopLossLines().entrySet()) {
/* 181 */       HorizontalLine line = (HorizontalLine)entry.getValue();
/* 182 */       if (!line.isSelected()) {
/* 183 */         g.setColor(line.getColor());
/* 184 */         g2.draw(line.getPath());
/* 185 */         TextSegment textSegment = line.getTextSegment();
/* 186 */         if (textSegment != null) {
/* 187 */           textSegment.render(g);
/*     */         }
/*     */       }
/*     */     }
/* 191 */     for (Map.Entry entry : this.ordersDrawingManager.getTakeProfitLines().entrySet()) {
/* 192 */       HorizontalLine line = (HorizontalLine)entry.getValue();
/* 193 */       if (!line.isSelected()) {
/* 194 */         g.setColor(line.getColor());
/* 195 */         g2.draw(line.getPath());
/* 196 */         TextSegment textSegment = line.getTextSegment();
/* 197 */         if (textSegment != null) {
/* 198 */           textSegment.render(g);
/*     */         }
/*     */       }
/*     */     }
/* 202 */     g2.setStroke(oldStroke);
/*     */   }
/*     */ 
/*     */   protected abstract void plotOpeningPoint(Graphics paramGraphics, OpeningPoint paramOpeningPoint);
/*     */ 
/*     */   protected abstract void plotClosingPoint(Graphics paramGraphics, ClosingPoint paramClosingPoint);
/*     */ 
/*     */   protected abstract void renderOpeningClosingPoints(Graphics2D paramGraphics2D, JComponent paramJComponent);
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Charts-5.48.jar
 * Qualified Name:     com.dukascopy.charts.view.drawingstrategies.OrdersDrawingLogic
 * JD-Core Version:    0.6.0
 */