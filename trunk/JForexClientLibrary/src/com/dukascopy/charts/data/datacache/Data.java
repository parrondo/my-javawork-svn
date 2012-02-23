/*     */ package com.dukascopy.charts.data.datacache;
/*     */ 
/*     */ import com.dukascopy.api.impl.TimedData;
/*     */ 
/*     */ public abstract class Data
/*     */   implements TimedData, Cloneable
/*     */ {
/*     */   public long time;
/*     */ 
/*     */   public Data()
/*     */   {
/*     */   }
/*     */ 
/*     */   public Data(long time)
/*     */   {
/*  15 */     this.time = time;
/*     */   }
/*     */   public abstract void toBytes(int paramInt1, long paramLong, double paramDouble, byte[] paramArrayOfByte, int paramInt2);
/*     */ 
/*     */   public abstract int getBytesCount(int paramInt);
/*     */ 
/*  23 */   protected static final int putLong(byte[] b, int off, long val) { b[(off + 7)] = (byte)(int)(val >>> 0);
/*  24 */     b[(off + 6)] = (byte)(int)(val >>> 8);
/*  25 */     b[(off + 5)] = (byte)(int)(val >>> 16);
/*  26 */     b[(off + 4)] = (byte)(int)(val >>> 24);
/*  27 */     b[(off + 3)] = (byte)(int)(val >>> 32);
/*  28 */     b[(off + 2)] = (byte)(int)(val >>> 40);
/*  29 */     b[(off + 1)] = (byte)(int)(val >>> 48);
/*  30 */     b[(off + 0)] = (byte)(int)(val >>> 56);
/*  31 */     return off + 8; }
/*     */ 
/*     */   protected static final int putInt(byte[] b, int off, int val)
/*     */   {
/*  35 */     b[(off + 3)] = (byte)(val >>> 0);
/*  36 */     b[(off + 2)] = (byte)(val >>> 8);
/*  37 */     b[(off + 1)] = (byte)(val >>> 16);
/*  38 */     b[(off + 0)] = (byte)(val >>> 24);
/*  39 */     return off + 4;
/*     */   }
/*     */ 
/*     */   protected static final int putDouble(byte[] b, int off, double val) {
/*  43 */     long j = Double.doubleToLongBits(val);
/*  44 */     b[(off + 7)] = (byte)(int)(j >>> 0);
/*  45 */     b[(off + 6)] = (byte)(int)(j >>> 8);
/*  46 */     b[(off + 5)] = (byte)(int)(j >>> 16);
/*  47 */     b[(off + 4)] = (byte)(int)(j >>> 24);
/*  48 */     b[(off + 3)] = (byte)(int)(j >>> 32);
/*  49 */     b[(off + 2)] = (byte)(int)(j >>> 40);
/*  50 */     b[(off + 1)] = (byte)(int)(j >>> 48);
/*  51 */     b[(off + 0)] = (byte)(int)(j >>> 56);
/*  52 */     return off + 8;
/*     */   }
/*     */ 
/*     */   protected static final int putFloat(byte[] b, int off, float val) {
/*  56 */     int i = Float.floatToIntBits(val);
/*  57 */     b[(off + 3)] = (byte)(i >>> 0);
/*  58 */     b[(off + 2)] = (byte)(i >>> 8);
/*  59 */     b[(off + 1)] = (byte)(i >>> 16);
/*  60 */     b[(off + 0)] = (byte)(i >>> 24);
/*  61 */     return off + 4;
/*     */   }
/*     */ 
/*     */   protected static final long getLong(byte[] b, int off) {
/*  65 */     return ((b[(off + 7)] & 0xFF) << 0) + ((b[(off + 6)] & 0xFF) << 8) + ((b[(off + 5)] & 0xFF) << 16) + ((b[(off + 4)] & 0xFF) << 24) + ((b[(off + 3)] & 0xFF) << 32) + ((b[(off + 2)] & 0xFF) << 40) + ((b[(off + 1)] & 0xFF) << 48) + (b[(off + 0)] << 56);
/*     */   }
/*     */ 
/*     */   protected static final int getInt(byte[] b, int off)
/*     */   {
/*  76 */     return ((b[(off + 3)] & 0xFF) << 0) + ((b[(off + 2)] & 0xFF) << 8) + ((b[(off + 1)] & 0xFF) << 16) + (b[(off + 0)] << 24);
/*     */   }
/*     */ 
/*     */   protected static final double getDouble(byte[] b, int off)
/*     */   {
/*  83 */     long j = ((b[(off + 7)] & 0xFF) << 0) + ((b[(off + 6)] & 0xFF) << 8) + ((b[(off + 5)] & 0xFF) << 16) + ((b[(off + 4)] & 0xFF) << 24) + ((b[(off + 3)] & 0xFF) << 32) + ((b[(off + 2)] & 0xFF) << 40) + ((b[(off + 1)] & 0xFF) << 48) + (b[(off + 0)] << 56);
/*     */ 
/*  91 */     return Double.longBitsToDouble(j);
/*     */   }
/*     */ 
/*     */   protected static final float getFloat(byte[] b, int off) {
/*  95 */     int i = ((b[(off + 3)] & 0xFF) << 0) + ((b[(off + 2)] & 0xFF) << 8) + ((b[(off + 1)] & 0xFF) << 16) + (b[(off + 0)] << 24);
/*     */ 
/*  99 */     return Float.intBitsToFloat(i);
/*     */   }
/*     */ 
/*     */   public Data clone()
/*     */   {
/*     */     try {
/* 105 */       return (Data)super.clone();
/*     */     } catch (CloneNotSupportedException e) {
/*     */     }
/* 108 */     return null;
/*     */   }
/*     */ 
/*     */   public int hashCode()
/*     */   {
/* 114 */     int prime = 31;
/* 115 */     int result = 1;
/* 116 */     result = 31 * result + (int)(this.time ^ this.time >>> 32);
/* 117 */     return result;
/*     */   }
/*     */ 
/*     */   public boolean equals(Object obj)
/*     */   {
/* 122 */     if (this == obj)
/* 123 */       return true;
/* 124 */     if (obj == null)
/* 125 */       return false;
/* 126 */     if (getClass() != obj.getClass())
/* 127 */       return false;
/* 128 */     Data other = (Data)obj;
/*     */ 
/* 130 */     return this.time == other.time;
/*     */   }
/*     */ 
/*     */   public long getTime()
/*     */   {
/* 138 */     return this.time;
/*     */   }
/*     */ 
/*     */   public void setTime(long time)
/*     */   {
/* 143 */     this.time = time;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.charts.data.datacache.Data
 * JD-Core Version:    0.6.0
 */