/*    */ package com.dukascopy.transport.common.datafeed;
/*    */ 
/*    */ public enum FileType
/*    */ {
/*  4 */   WORKSPACE("W"), CHART("C"), STRATEGY("S"), INDICATOR("I"), WIDGET("G");
/*    */ 
/*    */   private String typeId;
/*    */ 
/*  9 */   private FileType(String typeId) { this.typeId = typeId; }
/*    */ 
/*    */   public String getTypeId()
/*    */   {
/* 13 */     return this.typeId;
/*    */   }
/*    */ 
/*    */   public static FileType getById(String id) {
/* 17 */     if (id.equals("W"))
/* 18 */       return WORKSPACE;
/* 19 */     if (id.equals("S"))
/* 20 */       return STRATEGY;
/* 21 */     if (id.equals("I"))
/* 22 */       return INDICATOR;
/* 23 */     if (id.equals("C")) {
/* 24 */       return CHART;
/*    */     }
/* 26 */     return null;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\dds2-common-2.3.77.jar
 * Qualified Name:     com.dukascopy.transport.common.datafeed.FileType
 * JD-Core Version:    0.6.0
 */