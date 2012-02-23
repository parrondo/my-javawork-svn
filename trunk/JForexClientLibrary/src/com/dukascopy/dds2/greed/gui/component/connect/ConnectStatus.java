/*    */ package com.dukascopy.dds2.greed.gui.component.connect;
/*    */ 
/*    */ public enum ConnectStatus
/*    */ {
/*  7 */   ONLINE("ONLINE"), 
/*  8 */   OFFLINE("OFFLINE");
/*    */ 
/*    */   private String value;
/*    */ 
/* 13 */   private ConnectStatus(String value) { this.value = value; }
/*    */ 
/*    */   public String asString()
/*    */   {
/* 17 */     return this.value;
/*    */   }
/*    */ 
/*    */   public String toString() {
/* 21 */     return this.value;
/*    */   }
/*    */ 
/*    */   public static ConnectStatus fromString(String string) {
/* 25 */     if (ONLINE.asString().equals(string))
/* 26 */       return ONLINE;
/* 27 */     if (OFFLINE.asString().equals(string)) {
/* 28 */       return OFFLINE;
/*    */     }
/* 30 */     throw new IllegalArgumentException("Side cannot be constructed from string: " + string);
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.connect.ConnectStatus
 * JD-Core Version:    0.6.0
 */