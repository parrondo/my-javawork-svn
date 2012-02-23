/*    */ package com.dukascopy.transport.common.backoffice.type;
/*    */ 
/*    */ public enum PositionState
/*    */ {
/*  9 */   PENDING("PENDING"), 
/* 10 */   OPENED("OPENED");
/*    */ 
/*    */   private String state;
/*    */ 
/* 15 */   private PositionState(String state) { this.state = state; }
/*    */ 
/*    */   public String toString()
/*    */   {
/* 19 */     return this.state;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\dds2-common-2.3.77.jar
 * Qualified Name:     com.dukascopy.transport.common.backoffice.type.PositionState
 * JD-Core Version:    0.6.0
 */