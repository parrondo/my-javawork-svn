/*     */ package com.dukascopy.charts.orders.orderparts;
/*     */ 
/*     */ import com.dukascopy.api.IEngine.OrderCommand;
/*     */ import com.dukascopy.charts.chartbuilder.GuiRefresher;
/*     */ import com.dukascopy.charts.mappers.value.IValueToYMapper;
/*     */ import com.dukascopy.charts.persistence.ITheme;
/*     */ import com.dukascopy.charts.persistence.ITheme.ChartElement;
/*     */ import com.dukascopy.charts.persistence.ITheme.TextElement;
/*     */ import java.awt.Color;
/*     */ import java.awt.Font;
/*     */ import java.awt.FontMetrics;
/*     */ import java.awt.Graphics;
/*     */ import java.awt.Rectangle;
/*     */ import java.awt.event.ActionEvent;
/*     */ import java.awt.event.ActionListener;
/*     */ import java.awt.geom.Ellipse2D.Float;
/*     */ import java.awt.geom.GeneralPath;
/*     */ import java.math.BigDecimal;
/*     */ import javax.swing.Timer;
/*     */ 
/*     */ public abstract class HorizontalLine extends Line
/*     */ {
/*     */   private static final int ORDER_UNHIDE_DELAY = 5000;
/*     */   private final ITheme theme;
/*     */   private float y;
/*     */   private IEngine.OrderCommand openOrderCommand;
/*     */   private BigDecimal price;
/*     */   private BigDecimal slippage;
/*     */   private boolean selected;
/*     */   private boolean hidden;
/*     */   private Timer unhideTimer;
/*     */   private boolean dragged;
/*     */   private BigDecimal selectedPrice;
/*     */   protected TextSegment textSegment;
/*     */   private String orderId;
/*  42 */   protected GeneralPath handlesPath = new GeneralPath(1, 2);
/*  43 */   private boolean editable = true;
/*     */ 
/*     */   public HorizontalLine(String orderGroupId, ITheme theme) {
/*  46 */     super(orderGroupId);
/*     */ 
/*  48 */     this.theme = theme;
/*     */   }
/*     */ 
/*     */   public float getY() {
/*  52 */     return this.y;
/*     */   }
/*     */ 
/*     */   public void setY(float y) {
/*  56 */     this.y = y;
/*     */   }
/*     */ 
/*     */   public IEngine.OrderCommand getOpenOrderCommand() {
/*  60 */     return this.openOrderCommand;
/*     */   }
/*     */ 
/*     */   public void setOpenOrderCommand(IEngine.OrderCommand openOrderCommand) {
/*  64 */     this.openOrderCommand = openOrderCommand;
/*     */   }
/*     */ 
/*     */   public BigDecimal getPrice() {
/*  68 */     return this.price;
/*     */   }
/*     */ 
/*     */   public void setPrice(BigDecimal price) {
/*  72 */     this.price = price;
/*     */   }
/*     */ 
/*     */   public BigDecimal getSlippage() {
/*  76 */     return this.slippage;
/*     */   }
/*     */ 
/*     */   public void setSlippage(BigDecimal slippage) {
/*  80 */     this.slippage = slippage;
/*     */   }
/*     */ 
/*     */   public BigDecimal getSelectedPrice() {
/*  84 */     return this.selectedPrice;
/*     */   }
/*     */ 
/*     */   public void setSelectedPrice(BigDecimal selectedPrice) {
/*  88 */     this.selectedPrice = selectedPrice;
/*     */   }
/*     */ 
/*     */   public boolean isSelected() {
/*  92 */     return this.selected;
/*     */   }
/*     */ 
/*     */   public void setSelected(boolean selected) {
/*  96 */     this.selected = selected;
/*     */   }
/*     */ 
/*     */   public boolean isHidden() {
/* 100 */     return this.hidden;
/*     */   }
/*     */ 
/*     */   public void hide(GuiRefresher guiRefresher) {
/* 104 */     this.unhideTimer = new Timer(5000, new UnhideTimedEventListener(guiRefresher, null));
/* 105 */     this.unhideTimer.setRepeats(false);
/* 106 */     this.unhideTimer.start();
/* 107 */     setVisible(false);
/*     */   }
/*     */ 
/*     */   public void setVisible(boolean visible) {
/* 111 */     this.hidden = (!visible);
/*     */   }
/*     */ 
/*     */   public void unhide() {
/* 115 */     setVisible(true);
/* 116 */     if (this.unhideTimer != null) {
/* 117 */       this.unhideTimer.stop();
/* 118 */       this.unhideTimer = null;
/*     */     }
/*     */   }
/*     */ 
/*     */   public boolean isDragged() {
/* 123 */     return this.dragged;
/*     */   }
/*     */ 
/*     */   public void setDragged(boolean dragged) {
/* 127 */     this.dragged = dragged;
/*     */   }
/*     */   public abstract String getText(BigDecimal paramBigDecimal);
/*     */ 
/*     */   public String getOrderId() {
/* 133 */     return this.orderId;
/*     */   }
/*     */ 
/*     */   public void setOrderId(String orderId) {
/* 137 */     this.orderId = orderId;
/*     */   }
/*     */ 
/*     */   public GeneralPath getHandlesPath() {
/* 141 */     return this.handlesPath;
/*     */   }
/*     */ 
/*     */   public boolean isEditable() {
/* 145 */     return this.editable;
/*     */   }
/*     */ 
/*     */   public void setEditable(boolean editable) {
/* 149 */     this.editable = editable;
/*     */   }
/*     */ 
/*     */   public void plotOnSelectedPrice(IValueToYMapper valueToYMapper, Graphics g, int chartX, int chartY, int width, int height) {
/* 153 */     if (this.color == null) {
/* 154 */       this.color = this.theme.getColor(ITheme.ChartElement.DRAWING);
/*     */     }
/* 156 */     if (!this.hidden) {
/* 157 */       if (this.selected) {
/* 158 */         float selectedY = valueToYMapper.yv(this.selectedPrice.doubleValue());
/* 159 */         plotWithY(g, chartX, chartY, width, height, selectedY, this.selectedPrice, true, this.color);
/*     */       }
/*     */     } else {
/* 162 */       this.path.reset();
/* 163 */       this.handlesPath.reset();
/* 164 */       this.textSegment = null;
/*     */     }
/*     */   }
/*     */ 
/*     */   public void plot(Graphics g, int chartX, int chartY, int width, int height) {
/* 169 */     if (this.color == null) {
/* 170 */       this.color = this.theme.getColor(ITheme.ChartElement.DRAWING);
/*     */     }
/* 172 */     if (!this.hidden) {
/* 173 */       plotWithY(g, chartX, chartY, width, height, this.y, getPrice(), false, this.color);
/*     */     } else {
/* 175 */       this.path.reset();
/* 176 */       this.handlesPath.reset();
/* 177 */       this.textSegment = null;
/*     */     }
/*     */   }
/*     */ 
/*     */   private void addHandle(int x, int y) {
/* 182 */     if (this.selected)
/* 183 */       this.handlesPath.append(new Ellipse2D.Float(x - 2, y - 2, 5.0F, 5.0F), false);
/*     */   }
/*     */ 
/*     */   private void plotWithY(Graphics g, int chartX, int chartY, int width, int height, float y, BigDecimal price, boolean handles, Color color)
/*     */   {
/* 189 */     if ((y != y) || (y < chartY) || (y > chartY + height)) {
/* 190 */       this.path.reset();
/* 191 */       this.textSegment = null;
/*     */     } else {
/* 193 */       float x1 = width * 0.33F;
/* 194 */       float x2 = width * 0.66F;
/* 195 */       String text = getText(price);
/* 196 */       if ((text != null) && (!text.equals("")))
/*     */       {
/* 198 */         Font axisFont = this.theme.getFont(ITheme.TextElement.DEFAULT);
/* 199 */         FontMetrics fm = g.getFontMetrics(axisFont);
/*     */ 
/* 201 */         int textWidth = fm.stringWidth(text) + 1;
/* 202 */         int textHeight = fm.getAscent() - 1;
/*     */ 
/* 204 */         width -= textWidth + 3;
/* 205 */         this.textSegment = new TextSegment(text, width + 3, y + textHeight / 2, color, null, axisFont);
/*     */       }
/* 207 */       if (handles) {
/* 208 */         this.path.reset();
/* 209 */         this.path.moveTo(chartX, y);
/* 210 */         this.path.lineTo(x1 - 3.0F, y);
/* 211 */         this.path.moveTo(x1 + 3.0F, y);
/* 212 */         this.path.lineTo(x2 - 3.0F, y);
/* 213 */         this.path.moveTo(x2 + 3.0F, y);
/* 214 */         this.path.lineTo(chartX + width, y);
/* 215 */         this.handlesPath.reset();
/* 216 */         addHandle((int)x1, (int)y);
/* 217 */         addHandle((int)x2, (int)y);
/*     */       } else {
/* 219 */         this.path.reset();
/* 220 */         this.path.moveTo(chartX, y);
/* 221 */         this.path.lineTo(chartX + width, y);
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public TextSegment getTextSegment() {
/* 227 */     return this.textSegment;
/*     */   }
/*     */ 
/*     */   public boolean hitPoint(int x, int y) {
/* 231 */     this.hitSquareRect.setLocation(x - 5, y - 5);
/*     */ 
/* 233 */     return ((this.textSegment != null) && (this.textSegment.containsHitPoint(x, y))) || (super.hitPoint(x, y));
/*     */   }
/*     */   private class UnhideTimedEventListener implements ActionListener {
/*     */     private GuiRefresher guiRefresher;
/*     */ 
/*     */     private UnhideTimedEventListener(GuiRefresher guiRefresher) {
/* 240 */       this.guiRefresher = guiRefresher;
/*     */     }
/*     */ 
/*     */     public void actionPerformed(ActionEvent e)
/*     */     {
/* 245 */       HorizontalLine.access$102(HorizontalLine.this, false);
/* 246 */       HorizontalLine.this.unhideTimer.stop();
/* 247 */       HorizontalLine.access$202(HorizontalLine.this, null);
/* 248 */       this.guiRefresher.refreshMainContent();
/*     */     }
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Charts-5.48.jar
 * Qualified Name:     com.dukascopy.charts.orders.orderparts.HorizontalLine
 * JD-Core Version:    0.6.0
 */