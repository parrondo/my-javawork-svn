/*    */ package com.dukascopy.charts.orders.orderparts;
/*    */ 
/*    */ public class MergingLine extends ShortLine
/*    */ {
/*    */   private String mergedToOrderGroupId;
/*    */ 
/*    */   public MergingLine(String orderGroupId, String mergedToOrderGroupId)
/*    */   {
/* 10 */     super(orderGroupId);
/* 11 */     this.mergedToOrderGroupId = mergedToOrderGroupId;
/*    */   }
/*    */ 
/*    */   public void setMergedFromPoint(double x1, double y1) {
/* 15 */     setCoordinates(x1, y1, getX2(), getY2());
/*    */   }
/*    */ 
/*    */   public boolean isMergedFromPointSet() {
/* 19 */     return (!Double.isNaN(getX1())) && (!Double.isNaN(getY1()));
/*    */   }
/*    */ 
/*    */   public void setMergedToPoint(double x2, double y2) {
/* 23 */     setCoordinates(getX1(), getY1(), x2, y2);
/*    */   }
/*    */ 
/*    */   public boolean isMergedToPointSet() {
/* 27 */     return (!Double.isNaN(getX2())) && (!Double.isNaN(getY2()));
/*    */   }
/*    */ 
/*    */   public String getMergedToOrderGroupId() {
/* 31 */     return this.mergedToOrderGroupId;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Charts-5.48.jar
 * Qualified Name:     com.dukascopy.charts.orders.orderparts.MergingLine
 * JD-Core Version:    0.6.0
 */