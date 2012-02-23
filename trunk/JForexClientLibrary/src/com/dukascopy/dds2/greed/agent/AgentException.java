/*    */ package com.dukascopy.dds2.greed.agent;
/*    */ 
/*    */ public class AgentException extends RuntimeException
/*    */ {
/*    */   private static final long serialVersionUID = 8279045828547424733L;
/* 17 */   private byte code = -12;
/*    */ 
/* 19 */   private String message = null;
/*    */ 
/*    */   public AgentException(byte code) {
/* 22 */     this.code = code;
/*    */   }
/*    */ 
/*    */   public AgentException(byte code, String message) {
/* 26 */     this.code = code;
/* 27 */     this.message = message;
/*    */   }
/*    */ 
/*    */   public byte getCode()
/*    */   {
/* 34 */     return this.code;
/*    */   }
/*    */ 
/*    */   public String getMessage()
/*    */   {
/* 42 */     switch (this.code)
/*    */     {
/*    */     case -10:
/* 45 */       this.message = new StringBuilder().append("Command unknown").append(this.message == null ? "" : this.message).toString();
/* 46 */       break;
/*    */     case -11:
/* 49 */       this.message = new StringBuilder().append("Timeout error").append(this.message == null ? "" : this.message).toString();
/* 50 */       break;
/*    */     case -12:
/* 53 */       this.message = new StringBuilder().append("DDS Exception").append(this.message == null ? "" : this.message).toString();
/* 54 */       break;
/*    */     case -13:
/* 57 */       this.message = new StringBuilder().append("Reference ID not found").append(this.message == null ? "" : this.message).toString();
/* 58 */       break;
/*    */     case -14:
/* 61 */       this.message = new StringBuilder().append("Reference ID not unique").append(this.message == null ? "" : this.message).toString();
/* 62 */       break;
/*    */     case -15:
/* 65 */       this.message = new StringBuilder().append("Negative price").append(this.message == null ? "" : this.message).toString();
/* 66 */       break;
/*    */     case -16:
/* 69 */       this.message = new StringBuilder().append("Invalid amount").append(this.message == null ? "" : this.message).toString();
/* 70 */       break;
/*    */     case -17:
/* 73 */       this.message = new StringBuilder().append("No liquidity").append(this.message == null ? "" : this.message).toString();
/* 74 */       break;
/*    */     case -18:
/* 76 */       this.message = new StringBuilder().append("Negative time").append(this.message == null ? "" : this.message).toString();
/* 77 */       break;
/*    */     case -19:
/* 79 */       this.message = new StringBuilder().append("Thread incorrect").append(this.message == null ? "" : this.message).toString();
/* 80 */       break;
/*    */     default:
/* 83 */       this.message = new StringBuilder().append("Error message undefined").append(this.message == null ? "" : this.message).toString();
/*    */     }
/*    */ 
/* 86 */     return this.message;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.dds2.greed.agent.AgentException
 * JD-Core Version:    0.6.0
 */