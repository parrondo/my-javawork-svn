/*     */ package com.dukascopy.dds2.greed.agent;
/*     */ 
/*     */ public class AgentResponse
/*     */ {
/*  18 */   private byte[] data = new byte[17];
/*     */ 
/*     */   public AgentResponse(byte code)
/*     */   {
/*  22 */     this.data[0] = 97;
/*  23 */     this.data[1] = 103;
/*  24 */     this.data[2] = 101;
/*  25 */     this.data[3] = 110;
/*  26 */     this.data[4] = 116;
/*  27 */     this.data[5] = 0;
/*  28 */     this.data[6] = 1;
/*  29 */     this.data[7] = code;
/*  30 */     this.data[8] = 0;
/*     */   }
/*     */ 
/*     */   public void setStringValue(String val)
/*     */   {
/*  36 */     this.data[8] = 1;
/*     */ 
/*  38 */     byte[] strBytes = val.getBytes();
/*  39 */     System.arraycopy(strBytes, 0, this.data, 8, strBytes.length > 8 ? 8 : strBytes.length);
/*     */   }
/*     */ 
/*     */   public void setLongValue(long val)
/*     */   {
/*  45 */     this.data[8] = 3;
/*     */ 
/*  47 */     this.data[16] = (byte)(int)(val >>> 56);
/*  48 */     this.data[15] = (byte)(int)(val >>> 48);
/*     */ 
/*  50 */     this.data[14] = (byte)(int)(val >>> 40);
/*  51 */     this.data[13] = (byte)(int)(val >>> 32);
/*     */ 
/*  53 */     this.data[12] = (byte)(int)(val >>> 24);
/*  54 */     this.data[11] = (byte)(int)(val >>> 16);
/*     */ 
/*  56 */     this.data[10] = (byte)(int)(val >>> 8);
/*  57 */     this.data[9] = (byte)(int)(val >>> 0);
/*     */   }
/*     */ 
/*     */   public void setDoubleValue(double dval)
/*     */   {
/*  69 */     long val = Double.doubleToLongBits(dval);
/*     */ 
/*  71 */     this.data[8] = 2;
/*     */ 
/*  73 */     this.data[16] = (byte)(int)(val >>> 56);
/*  74 */     this.data[15] = (byte)(int)(val >>> 48);
/*     */ 
/*  76 */     this.data[14] = (byte)(int)(val >>> 40);
/*  77 */     this.data[13] = (byte)(int)(val >>> 32);
/*     */ 
/*  79 */     this.data[12] = (byte)(int)(val >>> 24);
/*  80 */     this.data[11] = (byte)(int)(val >>> 16);
/*     */ 
/*  82 */     this.data[10] = (byte)(int)(val >>> 8);
/*  83 */     this.data[9] = (byte)(int)(val >>> 0);
/*     */   }
/*     */ 
/*     */   public void setIntValue(int val)
/*     */   {
/*  89 */     this.data[8] = 4;
/*     */ 
/*  91 */     this.data[12] = (byte)(val >>> 24 & 0xFF);
/*  92 */     this.data[11] = (byte)(val >>> 16 & 0xFF);
/*     */ 
/*  94 */     this.data[10] = (byte)(val >>> 8 & 0xFF);
/*  95 */     this.data[9] = (byte)(val >>> 0 & 0xFF);
/*     */ 
/*  97 */     this.data[13] = 0;
/*  98 */     this.data[14] = 0;
/*     */ 
/* 100 */     this.data[15] = 0;
/* 101 */     this.data[16] = 0;
/*     */   }
/*     */ 
/*     */   public AgentResponse(Throwable e)
/*     */   {
/* 114 */     if ((e instanceof AgentException)) {
/* 115 */       AgentException agentException = (AgentException)e;
/* 116 */       this.data[7] = agentException.getCode();
/*     */     } else {
/* 118 */       this.data[7] = -12;
/*     */     }
/*     */   }
/*     */ 
/*     */   public byte[] getBytes() {
/* 123 */     return this.data;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.agent.AgentResponse
 * JD-Core Version:    0.6.0
 */