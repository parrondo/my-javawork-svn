/*    */ package com.dukascopy.transport.common.protocol.json;
/*    */ 
/*    */ public class IoSessionState
/*    */ {
/*  5 */   private Long lastStateChange = Long.valueOf(System.currentTimeMillis());
/*    */ 
/*  7 */   private Integer state = Integer.valueOf(0);
/*    */   public static final int STATE_INIT = 0;
/*    */   public static final int STATE_IDLE = 1;
/*    */   public static final int STATE_READ_HEADER = 2;
/*    */   public static final int STATE_READ_DATA = 3;
/* 17 */   private int lastMessageLenght = 0;
/*    */ 
/*    */   public int getLastMessageLenght()
/*    */   {
/* 22 */     return this.lastMessageLenght;
/*    */   }
/*    */ 
/*    */   public void setLastMessageLenght(int lastMessageLenght) {
/* 26 */     this.lastMessageLenght = lastMessageLenght;
/*    */   }
/*    */ 
/*    */   public Long getLastStateChange()
/*    */   {
/* 33 */     return this.lastStateChange;
/*    */   }
/*    */ 
/*    */   public void setLastStateChange(Long lastStateChange)
/*    */   {
/* 41 */     this.lastStateChange = lastStateChange;
/*    */   }
/*    */ 
/*    */   public int getState()
/*    */   {
/* 48 */     synchronized (this.state) {
/* 49 */       return this.state.intValue();
/*    */     }
/*    */   }
/*    */ 
/*    */   public void setState(int state)
/*    */   {
/* 58 */     this.lastStateChange = Long.valueOf(System.currentTimeMillis());
/* 59 */     synchronized (this.state) {
/* 60 */       this.state = Integer.valueOf(state);
/*    */     }
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\dds2-common-2.3.77.jar
 * Qualified Name:     com.dukascopy.transport.common.protocol.json.IoSessionState
 * JD-Core Version:    0.6.0
 */