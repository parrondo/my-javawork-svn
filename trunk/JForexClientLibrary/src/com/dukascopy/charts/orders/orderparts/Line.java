/*    */ package com.dukascopy.charts.orders.orderparts;
/*    */ 
/*    */ import com.dukascopy.charts.utils.GraphicHelper;
/*    */ import java.awt.Color;
/*    */ import java.awt.Rectangle;
/*    */ import java.awt.geom.GeneralPath;
/*    */ 
/*    */ public abstract class Line extends AbstractOrderPart
/*    */ {
/*    */   protected Color color;
/* 14 */   protected GeneralPath path = new GeneralPath(1, 2);
/*    */ 
/*    */   public Line(String orderGroupId) {
/* 17 */     super(orderGroupId);
/*    */   }
/*    */ 
/*    */   public Color getColor() {
/* 21 */     return this.color;
/*    */   }
/*    */ 
/*    */   public void setColor(Color color) {
/* 25 */     this.color = color;
/*    */   }
/*    */ 
/*    */   public GeneralPath getPath() {
/* 29 */     return this.path;
/*    */   }
/*    */ 
/*    */   public boolean hitPoint(int x, int y) {
/* 33 */     this.hitSquareRect.setLocation(x - 5, y - 5);
/*    */ 
/* 36 */     return (this.path != null) && (GraphicHelper.intersects(this.path, this.hitSquareRect));
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Charts-5.48.jar
 * Qualified Name:     com.dukascopy.charts.orders.orderparts.Line
 * JD-Core Version:    0.6.0
 */