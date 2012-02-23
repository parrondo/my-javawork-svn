/*    */ package com.dukascopy.charts.orders.orderparts;
/*    */ 
/*    */ import java.awt.Rectangle;
/*    */ 
/*    */ public abstract class AbstractOrderPart
/*    */   implements OrderPart
/*    */ {
/*    */   protected static final int HIT_SQUARE_RADIUS = 5;
/* 10 */   protected Rectangle hitSquareRect = new Rectangle(0, 0, 11, 11);
/*    */   private int paintTag;
/*    */   private String orderGroupId;
/*    */ 
/*    */   public AbstractOrderPart(String orderGroupId)
/*    */   {
/* 16 */     this.orderGroupId = orderGroupId;
/*    */   }
/*    */ 
/*    */   public void updatePaintTag(int paintTag) {
/* 20 */     this.paintTag = paintTag;
/*    */   }
/*    */ 
/*    */   public int getPaintTag() {
/* 24 */     return this.paintTag;
/*    */   }
/*    */ 
/*    */   public String getOrderGroupId() {
/* 28 */     return this.orderGroupId;
/*    */   }
/*    */ 
/*    */   public void setOrderGroupId(String orderGroupId) {
/* 32 */     this.orderGroupId = orderGroupId;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Charts-5.48.jar
 * Qualified Name:     com.dukascopy.charts.orders.orderparts.AbstractOrderPart
 * JD-Core Version:    0.6.0
 */