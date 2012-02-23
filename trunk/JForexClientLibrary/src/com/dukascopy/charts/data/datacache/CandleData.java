/*     */ package com.dukascopy.charts.data.datacache;
/*     */ 
/*     */ import com.dukascopy.api.IBar;
/*     */ import java.text.SimpleDateFormat;
/*     */ import java.util.TimeZone;
/*     */ 
/*     */ public class CandleData extends Data
/*     */   implements IBar
/*     */ {
/*     */   protected static final int BYTES_COUNT4 = 48;
/*     */   protected static final int BYTES_COUNT5 = 24;
/*     */   public double open;
/*     */   public double close;
/*     */   public double low;
/*     */   public double high;
/*     */   public double vol;
/*     */ 
/*     */   public CandleData()
/*     */   {
/*     */   }
/*     */ 
/*     */   public CandleData(long time, double open, double close, double low, double high, double vol)
/*     */   {
/*  26 */     super(time);
/*  27 */     this.open = open;
/*  28 */     this.close = close;
/*  29 */     this.low = low;
/*  30 */     this.high = high;
/*  31 */     this.vol = vol;
/*     */   }
/*     */ 
/*     */   public boolean isFlat() {
/*  35 */     return (this.open == this.close) && (this.open == this.high) && (this.open == this.low) && (this.vol <= 0.0D);
/*     */   }
/*     */ 
/*     */   public int getBytesCount(int version)
/*     */   {
/*  40 */     if (version <= 4) {
/*  41 */       return 48;
/*     */     }
/*  43 */     return 24;
/*     */   }
/*     */ 
/*     */   public static int getLength(int version)
/*     */   {
/*  48 */     if (version <= 4) {
/*  49 */       return 48;
/*     */     }
/*  51 */     return 24;
/*     */   }
/*     */ 
/*     */   public void fromBytes(int version, long firstChunkCandle, double pipValue, byte[] bytes, int off)
/*     */   {
/*  56 */     if (version <= 4) {
/*  57 */       this.time = getLong(bytes, off);
/*  58 */       this.open = getDouble(bytes, off + 8);
/*  59 */       this.close = getDouble(bytes, off + 16);
/*  60 */       this.low = getDouble(bytes, off + 24);
/*  61 */       this.high = getDouble(bytes, off + 32);
/*  62 */       this.vol = getDouble(bytes, off + 40);
/*     */     } else {
/*  64 */       long timeInt = getInt(bytes, off);
/*  65 */       this.time = (timeInt == -2147483648L ? -9223372036854775808L : firstChunkCandle + timeInt * 1000L);
/*  66 */       this.open = (()(getInt(bytes, off + 4) / 10.0D * pipValue * 100000.0D + 0.5D) / 100000.0D);
/*  67 */       this.close = (()(getInt(bytes, off + 8) / 10.0D * pipValue * 100000.0D + 0.5D) / 100000.0D);
/*  68 */       this.low = (()(getInt(bytes, off + 12) / 10.0D * pipValue * 100000.0D + 0.5D) / 100000.0D);
/*  69 */       this.high = (()(getInt(bytes, off + 16) / 10.0D * pipValue * 100000.0D + 0.5D) / 100000.0D);
/*  70 */       this.vol = (()(Double.valueOf(Float.toString(getFloat(bytes, off + 20))).doubleValue() * 1000000.0D + 0.5D) / 1000000.0D);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void toBytes(int version, long firstChunkCandle, double pipValue, byte[] buff, int off)
/*     */   {
/*  76 */     if (version <= 4) {
/*  77 */       if (buff.length < off + 48) {
/*  78 */         throw new ArrayIndexOutOfBoundsException("Buffer too short");
/*     */       }
/*  80 */       off = putLong(buff, off, this.time);
/*  81 */       off = putDouble(buff, off, this.open);
/*  82 */       off = putDouble(buff, off, this.close);
/*  83 */       off = putDouble(buff, off, this.low);
/*  84 */       off = putDouble(buff, off, this.high);
/*  85 */       putDouble(buff, off, this.vol);
/*     */     } else {
/*  87 */       if (buff.length < off + 24) {
/*  88 */         throw new ArrayIndexOutOfBoundsException("Buffer too short");
/*     */       }
/*  90 */       off = putInt(buff, off, this.time == -9223372036854775808L ? -2147483648 : (int)((this.time - firstChunkCandle) / 1000L));
/*  91 */       off = putInt(buff, off, (int)Math.round(this.open / pipValue * 10.0D));
/*  92 */       off = putInt(buff, off, (int)Math.round(this.close / pipValue * 10.0D));
/*  93 */       off = putInt(buff, off, (int)Math.round(this.low / pipValue * 10.0D));
/*  94 */       off = putInt(buff, off, (int)Math.round(this.high / pipValue * 10.0D));
/*  95 */       putFloat(buff, off, (float)this.vol);
/*     */     }
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/* 101 */     SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSZ");
/* 102 */     format.setTimeZone(TimeZone.getTimeZone("GMT"));
/* 103 */     StringBuilder stamp = new StringBuilder();
/* 104 */     stamp.append(this.time).append("[").append(format.format(Long.valueOf(this.time))).append("] O: ").append(this.open).append(" C: ").append(this.close).append(" H: ").append(this.high).append(" L: ").append(this.low).append(" V: ").append(this.vol);
/*     */ 
/* 106 */     return stamp.toString();
/*     */   }
/*     */ 
/*     */   public CandleData clone()
/*     */   {
/* 111 */     return (CandleData)super.clone();
/*     */   }
/*     */ 
/*     */   public int hashCode()
/*     */   {
/* 116 */     int prime = 31;
/* 117 */     int result = super.hashCode();
/*     */ 
/* 119 */     long temp = Double.doubleToLongBits(this.close);
/* 120 */     result = 31 * result + (int)(temp ^ temp >>> 32);
/* 121 */     temp = Double.doubleToLongBits(this.high);
/* 122 */     result = 31 * result + (int)(temp ^ temp >>> 32);
/* 123 */     temp = Double.doubleToLongBits(this.low);
/* 124 */     result = 31 * result + (int)(temp ^ temp >>> 32);
/* 125 */     temp = Double.doubleToLongBits(this.open);
/* 126 */     result = 31 * result + (int)(temp ^ temp >>> 32);
/* 127 */     temp = Double.doubleToLongBits(this.vol);
/* 128 */     result = 31 * result + (int)(temp ^ temp >>> 32);
/* 129 */     return result;
/*     */   }
/*     */ 
/*     */   public boolean equals(Object obj)
/*     */   {
/* 134 */     if (this == obj)
/* 135 */       return true;
/* 136 */     if (!super.equals(obj))
/* 137 */       return false;
/* 138 */     if (getClass() != obj.getClass())
/* 139 */       return false;
/* 140 */     CandleData other = (CandleData)obj;
/* 141 */     if (Double.doubleToLongBits(this.close) != Double.doubleToLongBits(other.close))
/* 142 */       return false;
/* 143 */     if (Double.doubleToLongBits(this.high) != Double.doubleToLongBits(other.high))
/* 144 */       return false;
/* 145 */     if (Double.doubleToLongBits(this.low) != Double.doubleToLongBits(other.low))
/* 146 */       return false;
/* 147 */     if (Double.doubleToLongBits(this.open) != Double.doubleToLongBits(other.open)) {
/* 148 */       return false;
/*     */     }
/* 150 */     return Double.doubleToLongBits(this.vol) == Double.doubleToLongBits(other.vol);
/*     */   }
/*     */ 
/*     */   public void setOpen(double open)
/*     */   {
/* 157 */     this.open = open;
/*     */   }
/*     */ 
/*     */   public void setClose(double close) {
/* 161 */     this.close = close;
/*     */   }
/*     */ 
/*     */   public void setLow(double low) {
/* 165 */     this.low = low;
/*     */   }
/*     */ 
/*     */   public void setHigh(double high) {
/* 169 */     this.high = high;
/*     */   }
/*     */ 
/*     */   public void setVolume(double vol) {
/* 173 */     this.vol = vol;
/*     */   }
/*     */ 
/*     */   public double getOpen()
/*     */   {
/* 181 */     return this.open;
/*     */   }
/*     */ 
/*     */   public double getClose()
/*     */   {
/* 186 */     return this.close;
/*     */   }
/*     */ 
/*     */   public double getLow()
/*     */   {
/* 191 */     return this.low;
/*     */   }
/*     */ 
/*     */   public double getHigh()
/*     */   {
/* 196 */     return this.high;
/*     */   }
/*     */ 
/*     */   public double getVolume()
/*     */   {
/* 201 */     return this.vol;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.charts.data.datacache.CandleData
 * JD-Core Version:    0.6.0
 */