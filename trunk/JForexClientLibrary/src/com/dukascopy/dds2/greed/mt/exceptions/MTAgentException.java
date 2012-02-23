/*     */ package com.dukascopy.dds2.greed.mt.exceptions;
/*     */ 
/*     */ public class MTAgentException extends RuntimeException
/*     */ {
/*     */   private static final long serialVersionUID = 1L;
/*  14 */   private byte code = -12;
/*  15 */   private String message = null;
/*     */ 
/*     */   public MTAgentException(byte code) {
/*  18 */     this.code = code;
/*     */   }
/*     */ 
/*     */   public MTAgentException(byte code, String message) {
/*  22 */     this.code = code;
/*  23 */     this.message = message;
/*     */   }
/*     */ 
/*     */   public byte getCode()
/*     */   {
/*  30 */     return this.code;
/*     */   }
/*     */ 
/*     */   public String generateMessage(String prefix) {
/*  34 */     String msg = new StringBuilder().append(prefix).append(this.message == null ? " " : this.message).toString();
/*  35 */     return msg;
/*     */   }
/*     */ 
/*     */   public String getMessage()
/*     */   {
/*  40 */     switch (this.code) {
/*     */     case -10:
/*  42 */       this.message = new StringBuilder().append("Command unknown").append(this.message == null ? " " : this.message).toString();
/*  43 */       break;
/*     */     case -11:
/*  46 */       this.message = new StringBuilder().append("Timeout error").append(this.message == null ? " " : this.message).toString();
/*  47 */       break;
/*     */     case -12:
/*  50 */       this.message = new StringBuilder().append("DDS Exception").append(this.message == null ? " " : this.message).toString();
/*  51 */       break;
/*     */     case -13:
/*  54 */       this.message = new StringBuilder().append("Reference ID not found").append(this.message == null ? " " : this.message).toString();
/*     */ 
/*  56 */       break;
/*     */     case -14:
/*  59 */       this.message = new StringBuilder().append("Reference ID not unique").append(this.message == null ? " " : this.message).toString();
/*     */ 
/*  61 */       break;
/*     */     case -15:
/*  64 */       this.message = new StringBuilder().append("Negative price").append(this.message == null ? " " : this.message).toString();
/*  65 */       break;
/*     */     case -16:
/*  68 */       this.message = new StringBuilder().append("Invalid amount").append(this.message == null ? " " : this.message).toString();
/*  69 */       break;
/*     */     case -17:
/*  72 */       this.message = new StringBuilder().append("No liquidity").append(this.message == null ? " " : this.message).toString();
/*  73 */       break;
/*     */     case -18:
/*  75 */       this.message = new StringBuilder().append("Negative time").append(this.message == null ? " " : this.message).toString();
/*  76 */       break;
/*     */     case -19:
/*  78 */       this.message = new StringBuilder().append("Thread incorrect").append(this.message == null ? " " : this.message).toString();
/*  79 */       break;
/*     */     default:
/*  82 */       this.message = new StringBuilder().append("Error message undefined").append(this.message == null ? " " : this.message).toString();
/*     */     }
/*     */ 
/*  86 */     return this.message;
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/* 105 */     String s = getClass().getName();
/* 106 */     String msg = getMessage();
/* 107 */     byte code = getCode();
/* 108 */     return this.message != null ? new StringBuilder().append(s).append(": ").append("code [").append(code).append("] message [").append(msg).append("]").toString() : s;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.mt.exceptions.MTAgentException
 * JD-Core Version:    0.6.0
 */