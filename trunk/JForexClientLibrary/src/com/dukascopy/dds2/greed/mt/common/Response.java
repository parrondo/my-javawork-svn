/*     */ package com.dukascopy.dds2.greed.mt.common;
/*     */ 
/*     */ import com.dukascopy.dds2.greed.mt.exceptions.MTAgentException;
/*     */ import org.slf4j.Logger;
/*     */ import org.slf4j.LoggerFactory;
/*     */ 
/*     */ public class Response
/*     */ {
/*  22 */   private static Logger log = LoggerFactory.getLogger(Response.class.getName());
/*     */   private static final int DEFAULT_ARRAY_SIZE = 17;
/*  25 */   private byte[] data = new byte[17];
/*     */ 
/*     */   private void init(byte code) {
/*  28 */     this.data[0] = 97;
/*  29 */     this.data[1] = 103;
/*  30 */     this.data[2] = 101;
/*  31 */     this.data[3] = 110;
/*  32 */     this.data[4] = 116;
/*  33 */     this.data[5] = 0;
/*  34 */     this.data[6] = 1;
/*  35 */     this.data[7] = code;
/*  36 */     this.data[8] = 0;
/*     */   }
/*     */ 
/*     */   public Response(byte code) {
/*  40 */     this.data = new byte[17];
/*  41 */     init(code);
/*     */   }
/*     */ 
/*     */   public Response(byte code, byte type) {
/*  45 */     init(code);
/*  46 */     this.data[8] = type;
/*     */   }
/*     */ 
/*     */   public void setStringValue(String val) {
/*  50 */     byte code = this.data[7];
/*  51 */     byte[] strBytes = val.getBytes();
/*  52 */     this.data = new byte[strBytes.length + 9];
/*  53 */     this.data[0] = 97;
/*  54 */     this.data[1] = 103;
/*  55 */     this.data[2] = 101;
/*  56 */     this.data[3] = 110;
/*  57 */     this.data[4] = 116;
/*  58 */     this.data[5] = 0;
/*  59 */     this.data[6] = 1;
/*  60 */     this.data[7] = code;
/*  61 */     this.data[8] = 1;
/*     */ 
/*  63 */     System.arraycopy(strBytes, 0, this.data, 9, strBytes.length);
/*     */   }
/*     */ 
/*     */   public void setLongValue(long val) {
/*  67 */     this.data[8] = 3;
/*     */ 
/*  69 */     this.data[16] = (byte)(int)(val >>> 56);
/*  70 */     this.data[15] = (byte)(int)(val >>> 48);
/*     */ 
/*  72 */     this.data[14] = (byte)(int)(val >>> 40);
/*  73 */     this.data[13] = (byte)(int)(val >>> 32);
/*     */ 
/*  75 */     this.data[12] = (byte)(int)(val >>> 24);
/*  76 */     this.data[11] = (byte)(int)(val >>> 16);
/*     */ 
/*  78 */     this.data[10] = (byte)(int)(val >>> 8);
/*  79 */     this.data[9] = (byte)(int)(val >>> 0);
/*     */   }
/*     */ 
/*     */   public void setDoubleValue(double dval) {
/*  83 */     long val = Double.doubleToLongBits(dval);
/*     */ 
/*  85 */     this.data[8] = 2;
/*     */ 
/*  87 */     this.data[16] = (byte)(int)(val >>> 56);
/*  88 */     this.data[15] = (byte)(int)(val >>> 48);
/*     */ 
/*  90 */     this.data[14] = (byte)(int)(val >>> 40);
/*  91 */     this.data[13] = (byte)(int)(val >>> 32);
/*     */ 
/*  93 */     this.data[12] = (byte)(int)(val >>> 24);
/*  94 */     this.data[11] = (byte)(int)(val >>> 16);
/*     */ 
/*  96 */     this.data[10] = (byte)(int)(val >>> 8);
/*  97 */     this.data[9] = (byte)(int)(val >>> 0);
/*     */   }
/*     */ 
/*     */   public void setIntValue(int val) {
/* 101 */     this.data[8] = 4;
/*     */ 
/* 103 */     this.data[12] = (byte)(val >>> 24 & 0xFF);
/* 104 */     this.data[11] = (byte)(val >>> 16 & 0xFF);
/*     */ 
/* 106 */     this.data[10] = (byte)(val >>> 8 & 0xFF);
/* 107 */     this.data[9] = (byte)(val >>> 0 & 0xFF);
/*     */ 
/* 109 */     this.data[13] = 0;
/* 110 */     this.data[14] = 0;
/*     */ 
/* 112 */     this.data[15] = 0;
/* 113 */     this.data[16] = 0;
/*     */   }
/*     */ 
/*     */   public void setVoidValue() {
/* 117 */     this.data[8] = 4;
/*     */ 
/* 119 */     this.data[12] = 0;
/* 120 */     this.data[11] = 0;
/*     */ 
/* 122 */     this.data[10] = 0;
/* 123 */     this.data[9] = 0;
/*     */ 
/* 125 */     this.data[13] = 0;
/* 126 */     this.data[14] = 0;
/*     */ 
/* 128 */     this.data[15] = 0;
/* 129 */     this.data[16] = 0;
/*     */   }
/*     */ 
/*     */   public Response(Throwable e)
/*     */   {
/* 136 */     if ((e instanceof MTAgentException)) {
/* 137 */       MTAgentException agentException = (MTAgentException)e;
/* 138 */       this.data[7] = agentException.getCode();
/*     */     } else {
/* 140 */       this.data[7] = -12;
/*     */     }
/*     */   }
/*     */ 
/*     */   public byte[] getBytes() {
/* 145 */     return this.data;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.mt.common.Response
 * JD-Core Version:    0.6.0
 */