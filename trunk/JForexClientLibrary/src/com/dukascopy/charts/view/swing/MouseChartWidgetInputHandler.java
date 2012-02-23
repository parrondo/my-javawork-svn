/*     */ package com.dukascopy.charts.view.swing;
/*     */ 
/*     */ import java.awt.Container;
/*     */ import java.awt.Cursor;
/*     */ import java.awt.Dimension;
/*     */ import java.awt.Insets;
/*     */ import java.awt.Point;
/*     */ import java.awt.Rectangle;
/*     */ import java.awt.Toolkit;
/*     */ import java.awt.event.MouseEvent;
/*     */ import javax.swing.JPanel;
/*     */ import javax.swing.JRootPane;
/*     */ import javax.swing.event.MouseInputListener;
/*     */ 
/*     */ public class MouseChartWidgetInputHandler
/*     */   implements MouseInputListener
/*     */ {
/*     */   public static final int CORNER_DRAG_WIDTH = 12;
/*     */   public static final int BORDER_DRAG_THICKNESS = 5;
/*  26 */   private final int[] cursorMapping = { 6, 6, 8, 7, 7, 6, 0, 0, 0, 7, 10, 0, 0, 0, 11, 4, 0, 0, 0, 5, 4, 4, 9, 5, 5 };
/*     */ 
/*  34 */   private Cursor lastCursor = Cursor.getPredefinedCursor(0);
/*     */   private boolean isMovingWindow;
/*     */   private boolean isDraging;
/*     */   private int dragCursor;
/*     */   private int dragOffsetX;
/*     */   private int dragOffsetY;
/*     */   private int dragWidth;
/*     */   private int dragHeight;
/*     */   private AbstractChartWidgetPanel widget;
/*     */ 
/*     */   public MouseChartWidgetInputHandler(AbstractChartWidgetPanel widget)
/*     */   {
/*  51 */     this.widget = widget;
/*     */   }
/*     */ 
/*     */   public void mousePressed(MouseEvent ev)
/*     */   {
/*  56 */     Point dragWindowOffset = ev.getPoint();
/*  57 */     JPanel p = (JPanel)ev.getSource();
/*  58 */     int cursor = getCursor(calculateCorner(p, dragWindowOffset.x, dragWindowOffset.y));
/*     */ 
/*  60 */     this.isDraging = true;
/*     */ 
/*  62 */     if (cursor == 0) {
/*  63 */       this.isMovingWindow = true;
/*  64 */       this.dragOffsetX = dragWindowOffset.x;
/*  65 */       this.dragOffsetY = dragWindowOffset.y;
/*     */     } else {
/*  67 */       this.dragOffsetX = dragWindowOffset.x;
/*  68 */       this.dragOffsetY = dragWindowOffset.y;
/*  69 */       this.dragWidth = p.getWidth();
/*  70 */       this.dragHeight = p.getHeight();
/*  71 */       this.dragCursor = cursor;
/*     */     }
/*     */   }
/*     */ 
/*     */   public void mouseReleased(MouseEvent ev) {
/*  76 */     this.isMovingWindow = false;
/*  77 */     this.dragCursor = 0;
/*     */ 
/*  79 */     this.isDraging = false;
/*  80 */     JPanel p = (JPanel)ev.getSource();
/*  81 */     if ((ev.getX() < 0) || (ev.getY() < 0) || (ev.getX() > p.getWidth()) || (ev.getY() > p.getHeight())) {
/*  82 */       p.setCursor(this.lastCursor);
/*  83 */       this.widget.getMenuButtonsPanel().setVisible(false);
/*  84 */       this.widget.setMouseOver(false);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void mouseMoved(MouseEvent ev) {
/*  89 */     JPanel p = (JPanel)ev.getSource();
/*     */ 
/*  92 */     int cursor = getCursor(calculateCorner(p, ev.getX(), ev.getY()));
/*     */ 
/*  94 */     if (cursor != 0) {
/*  95 */       p.setCursor(Cursor.getPredefinedCursor(cursor));
/*     */     }
/*     */     else
/*  98 */       p.setCursor(this.lastCursor);
/*     */   }
/*     */ 
/*     */   private void adjust(Rectangle bounds, Dimension min, int deltaX, int deltaY, int deltaWidth, int deltaHeight)
/*     */   {
/* 104 */     bounds.x += deltaX;
/* 105 */     bounds.y += deltaY;
/* 106 */     bounds.width += deltaWidth;
/* 107 */     bounds.height += deltaHeight;
/* 108 */     if (min != null) {
/* 109 */       if (bounds.width < min.width) {
/* 110 */         int correction = min.width - bounds.width;
/* 111 */         if (deltaX != 0) {
/* 112 */           bounds.x -= correction;
/*     */         }
/* 114 */         bounds.width = min.width;
/*     */       }
/* 116 */       if (bounds.height < min.height) {
/* 117 */         int correction = min.height - bounds.height;
/* 118 */         if (deltaY != 0) {
/* 119 */           bounds.y -= correction;
/*     */         }
/* 121 */         bounds.height = min.height;
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public void mouseDragged(MouseEvent ev) {
/* 127 */     AbstractChartWidgetPanel p = (AbstractChartWidgetPanel)ev.getSource();
/* 128 */     Point pt = ev.getPoint();
/*     */ 
/* 130 */     if (this.isMovingWindow) {
/* 131 */       int x = p.getLocation().x + (ev.getX() - this.dragOffsetX);
/* 132 */       int y = p.getLocation().y + (ev.getY() - this.dragOffsetY);
/* 133 */       int xRightLimit = p.getParent().getWidth() - p.getWidth();
/* 134 */       int yBottomLimit = p.getParent().getHeight() - p.getHeight();
/*     */ 
/* 136 */       x = x > xRightLimit ? xRightLimit : x < 0 ? 0 : x;
/*     */ 
/* 139 */       y = y > yBottomLimit ? yBottomLimit : y < 0 ? 0 : y;
/*     */ 
/* 143 */       p.setWidgetPosition(x, y);
/*     */     }
/* 145 */     else if (this.dragCursor != 0) {
/* 146 */       Rectangle r = p.getBounds();
/* 147 */       Rectangle startBounds = new Rectangle(r);
/* 148 */       Dimension min = p.getMinimumSize();
/*     */ 
/* 150 */       switch (this.dragCursor) {
/*     */       case 11:
/* 152 */         adjust(r, min, 0, 0, pt.x + (this.dragWidth - this.dragOffsetX) - r.width, 0);
/*     */ 
/* 154 */         break;
/*     */       case 9:
/* 156 */         adjust(r, min, 0, 0, 0, pt.y + (this.dragHeight - this.dragOffsetY) - r.height);
/*     */ 
/* 158 */         break;
/*     */       case 8:
/* 160 */         adjust(r, min, 0, pt.y - this.dragOffsetY, 0, -(pt.y - this.dragOffsetY));
/*     */ 
/* 162 */         break;
/*     */       case 10:
/* 164 */         adjust(r, min, pt.x - this.dragOffsetX, 0, -(pt.x - this.dragOffsetX), 0);
/*     */ 
/* 166 */         break;
/*     */       case 7:
/* 168 */         adjust(r, min, 0, pt.y - this.dragOffsetY, pt.x + (this.dragWidth - this.dragOffsetX) - r.width, -(pt.y - this.dragOffsetY));
/*     */ 
/* 171 */         break;
/*     */       case 5:
/* 173 */         adjust(r, min, 0, 0, pt.x + (this.dragWidth - this.dragOffsetX) - r.width, pt.y + (this.dragHeight - this.dragOffsetY) - r.height);
/*     */ 
/* 177 */         break;
/*     */       case 6:
/* 179 */         adjust(r, min, pt.x - this.dragOffsetX, pt.y - this.dragOffsetY, -(pt.x - this.dragOffsetX), -(pt.y - this.dragOffsetY));
/*     */ 
/* 183 */         break;
/*     */       case 4:
/* 185 */         adjust(r, min, pt.x - this.dragOffsetX, 0, -(pt.x - this.dragOffsetX), pt.y + (this.dragHeight - this.dragOffsetY) - r.height);
/*     */ 
/* 188 */         break;
/*     */       }
/*     */ 
/* 192 */       if (!r.equals(startBounds)) {
/* 193 */         p.setWidgetBounds(r);
/*     */ 
/* 196 */         if (Toolkit.getDefaultToolkit().isDynamicLayoutActive()) {
/* 197 */           p.validate();
/* 198 */           this.widget.getRootPane().repaint();
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public void mouseEntered(MouseEvent ev) {
/* 205 */     if (this.isDraging) {
/* 206 */       return;
/*     */     }
/*     */ 
/* 209 */     JPanel p = (JPanel)ev.getSource();
/* 210 */     this.lastCursor = p.getCursor();
/* 211 */     mouseMoved(ev);
/*     */ 
/* 213 */     this.widget.getMenuButtonsPanel().setVisible(true);
/* 214 */     this.widget.setMouseOver(true);
/*     */   }
/*     */ 
/*     */   public void mouseExited(MouseEvent ev) {
/* 218 */     if (this.isDraging) {
/* 219 */       return;
/*     */     }
/*     */ 
/* 222 */     JPanel p = (JPanel)ev.getSource();
/* 223 */     p.setCursor(this.lastCursor);
/*     */ 
/* 225 */     if ((ev.getX() < 5) || (ev.getY() < 5) || (ev.getX() > p.getWidth() - 5) || (ev.getY() > p.getHeight() - 5)) {
/* 226 */       this.widget.getMenuButtonsPanel().setVisible(false);
/* 227 */       this.widget.setMouseOver(false);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void mouseClicked(MouseEvent ev)
/*     */   {
/*     */   }
/*     */ 
/*     */   private int calculateCorner(JPanel p, int x, int y)
/*     */   {
/* 240 */     Insets insets = p.getInsets();
/* 241 */     int xPosition = calculatePosition(x - insets.left, p.getWidth() - insets.left - insets.right);
/*     */ 
/* 243 */     int yPosition = calculatePosition(y - insets.top, p.getHeight() - insets.top - insets.bottom);
/*     */ 
/* 246 */     if ((xPosition == -1) || (yPosition == -1)) {
/* 247 */       return -1;
/*     */     }
/* 249 */     return yPosition * 5 + xPosition;
/*     */   }
/*     */ 
/*     */   private int getCursor(int corner)
/*     */   {
/* 257 */     if (corner == -1) {
/* 258 */       return 0;
/*     */     }
/* 260 */     return this.cursorMapping[corner];
/*     */   }
/*     */ 
/*     */   private int calculatePosition(int spot, int width)
/*     */   {
/* 274 */     if (spot < 5) {
/* 275 */       return 0;
/*     */     }
/* 277 */     if (spot < 12) {
/* 278 */       return 1;
/*     */     }
/* 280 */     if (spot >= width - 5) {
/* 281 */       return 4;
/*     */     }
/* 283 */     if (spot >= width - 12) {
/* 284 */       return 3;
/*     */     }
/* 286 */     return 2;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Charts-5.48.jar
 * Qualified Name:     com.dukascopy.charts.view.swing.MouseChartWidgetInputHandler
 * JD-Core Version:    0.6.0
 */