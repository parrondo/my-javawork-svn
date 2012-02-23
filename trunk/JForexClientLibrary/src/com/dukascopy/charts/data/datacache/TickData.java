/*     */ package com.dukascopy.charts.data.datacache;
/*     */ 
/*     */ import com.dukascopy.api.ITick;
/*     */ import com.dukascopy.dds2.greed.agent.strategy.StratUtils;
/*     */ import java.text.SimpleDateFormat;
/*     */ import java.util.Arrays;
/*     */ import java.util.TimeZone;
/*     */ 
/*     */ public class TickData extends Data
/*     */   implements ITick
/*     */ {
/*     */   private static final int BYTES_COUNT4 = 40;
/*     */   private static final int BYTES_COUNT5 = 20;
/*     */   public double ask;
/*     */   public double bid;
/*     */   public double askVol;
/*     */   public double bidVol;
/*     */   public double[] asks;
/*     */   public double[] bids;
/*     */   public double[] askVolumes;
/*     */   public double[] bidVolumes;
/*     */ 
/*     */   public TickData()
/*     */   {
/*     */   }
/*     */ 
/*     */   public TickData(long time, double ask, double bid, double askVol, double bidVol)
/*     */   {
/*  32 */     super(time);
/*  33 */     this.ask = ask;
/*  34 */     this.bid = bid;
/*  35 */     this.askVol = askVol;
/*  36 */     this.bidVol = bidVol;
/*     */   }
/*     */ 
/*     */   public TickData(long time, double ask, double bid, double askVolume, double bidVolume, double[] asks, double[] bids, double[] askVolumes, double[] bidVolumes) {
/*  40 */     this.time = time;
/*  41 */     this.ask = ask;
/*  42 */     this.bid = bid;
/*  43 */     this.askVol = askVolume;
/*  44 */     this.bidVol = bidVolume;
/*  45 */     this.asks = asks;
/*  46 */     this.bids = bids;
/*  47 */     this.askVolumes = askVolumes;
/*  48 */     this.bidVolumes = bidVolumes;
/*     */   }
/*     */ 
/*     */   public void fromBytes(int version, long firstChunkCandle, double pipValue, byte[] bytes, int off) {
/*  52 */     if (version <= 4) {
/*  53 */       this.time = getLong(bytes, off);
/*  54 */       this.ask = getDouble(bytes, off + 8);
/*  55 */       this.bid = getDouble(bytes, off + 16);
/*  56 */       this.askVol = StratUtils.round(getDouble(bytes, off + 24) / 1000000.0D, 6);
/*  57 */       this.bidVol = StratUtils.round(getDouble(bytes, off + 32) / 1000000.0D, 6);
/*     */     } else {
/*  59 */       int timeInt = getInt(bytes, off);
/*  60 */       this.time = (timeInt == -2147483648 ? -9223372036854775808L : firstChunkCandle + timeInt);
/*  61 */       this.ask = (()(getInt(bytes, off + 4) / 10.0D * pipValue * 100000.0D + 0.5D) / 100000.0D);
/*  62 */       this.bid = (()(getInt(bytes, off + 8) / 10.0D * pipValue * 100000.0D + 0.5D) / 100000.0D);
/*  63 */       this.askVol = (()(Double.valueOf(Float.toString(getFloat(bytes, off + 12))).doubleValue() * 1000000.0D + 0.5D) / 1000000.0D);
/*  64 */       this.bidVol = (()(Double.valueOf(Float.toString(getFloat(bytes, off + 16))).doubleValue() * 1000000.0D + 0.5D) / 1000000.0D);
/*     */     }
/*     */   }
/*     */ 
/*     */   public final void toBytes(int version, long firstChunkCandle, double pipValue, byte[] buff, int off) {
/*  69 */     if (version <= 4) {
/*  70 */       if (buff.length < off + 40) {
/*  71 */         throw new ArrayIndexOutOfBoundsException("Buffer too short");
/*     */       }
/*  73 */       off = putLong(buff, off, this.time);
/*  74 */       off = putDouble(buff, off, this.ask);
/*  75 */       off = putDouble(buff, off, this.bid);
/*  76 */       off = putDouble(buff, off, Math.round(this.askVol * 1000000.0D));
/*  77 */       putDouble(buff, off, Math.round(this.bidVol * 1000000.0D));
/*     */     } else {
/*  79 */       if (buff.length < off + 20) {
/*  80 */         throw new ArrayIndexOutOfBoundsException("Buffer too short");
/*     */       }
/*  82 */       off = putInt(buff, off, this.time == -9223372036854775808L ? -2147483648 : (int)(this.time - firstChunkCandle));
/*  83 */       off = putInt(buff, off, (int)Math.round(this.ask / pipValue * 10.0D));
/*  84 */       off = putInt(buff, off, (int)Math.round(this.bid / pipValue * 10.0D));
/*  85 */       off = putFloat(buff, off, (float)this.askVol);
/*  86 */       putFloat(buff, off, (float)this.bidVol);
/*     */     }
/*     */   }
/*     */ 
/*     */   public int getBytesCount(int version) {
/*  91 */     if (version <= 4) {
/*  92 */       return 40;
/*     */     }
/*  94 */     return 20;
/*     */   }
/*     */ 
/*     */   public static int getLength(int version)
/*     */   {
/*  99 */     if (version <= 4) {
/* 100 */       return 40;
/*     */     }
/* 102 */     return 20;
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/* 107 */     SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSZ");
/* 108 */     format.setTimeZone(TimeZone.getTimeZone("GMT"));
/* 109 */     StringBuilder stamp = new StringBuilder();
/* 110 */     stamp.append(this.time).append("[").append(format.format(Long.valueOf(this.time))).append("] / ");
/* 111 */     stamp.append(this.ask).append(" / ").append(this.bid);
/* 112 */     return stamp.toString();
/*     */   }
/*     */ 
/*     */   public TickData clone()
/*     */   {
/* 117 */     return (TickData)super.clone();
/*     */   }
/*     */ 
/*     */   public boolean equals(Object o)
/*     */   {
/* 122 */     if (this == o) return true;
/* 123 */     if ((o == null) || (getClass() != o.getClass())) return false;
/* 124 */     if (!super.equals(o)) return false;
/*     */ 
/* 126 */     TickData tickData = (TickData)o;
/*     */ 
/* 128 */     if (Double.compare(tickData.ask, this.ask) != 0) return false;
/* 129 */     if (Double.compare(tickData.askVol, this.askVol) != 0) return false;
/* 130 */     if (Double.compare(tickData.bid, this.bid) != 0) return false;
/* 131 */     if (Double.compare(tickData.bidVol, this.bidVol) != 0) return false;
/* 132 */     if (!Arrays.equals(this.askVolumes, tickData.askVolumes)) return false;
/* 133 */     if (!Arrays.equals(this.asks, tickData.asks)) return false;
/* 134 */     if (!Arrays.equals(this.bidVolumes, tickData.bidVolumes)) return false;
/* 135 */     return Arrays.equals(this.bids, tickData.bids);
/*     */   }
/*     */ 
/*     */   public int hashCode()
/*     */   {
/* 142 */     int result = super.hashCode();
/*     */ 
/* 144 */     long temp = this.ask != 0.0D ? Double.doubleToLongBits(this.ask) : 0L;
/* 145 */     result = 31 * result + (int)(temp ^ temp >>> 32);
/* 146 */     temp = this.bid != 0.0D ? Double.doubleToLongBits(this.bid) : 0L;
/* 147 */     result = 31 * result + (int)(temp ^ temp >>> 32);
/* 148 */     temp = this.askVol != 0.0D ? Double.doubleToLongBits(this.askVol) : 0L;
/* 149 */     result = 31 * result + (int)(temp ^ temp >>> 32);
/* 150 */     temp = this.bidVol != 0.0D ? Double.doubleToLongBits(this.bidVol) : 0L;
/* 151 */     result = 31 * result + (int)(temp ^ temp >>> 32);
/* 152 */     result = 31 * result + (this.asks != null ? Arrays.hashCode(this.asks) : 0);
/* 153 */     result = 31 * result + (this.bids != null ? Arrays.hashCode(this.bids) : 0);
/* 154 */     result = 31 * result + (this.askVolumes != null ? Arrays.hashCode(this.askVolumes) : 0);
/* 155 */     result = 31 * result + (this.bidVolumes != null ? Arrays.hashCode(this.bidVolumes) : 0);
/* 156 */     return result;
/*     */   }
/*     */ 
/*     */   public double getAsk()
/*     */   {
/* 163 */     return this.ask;
/*     */   }
/*     */ 
/*     */   public double getBid()
/*     */   {
/* 168 */     return this.bid;
/*     */   }
/*     */ 
/*     */   public double getAskVolume()
/*     */   {
/* 173 */     return this.askVol;
/*     */   }
/*     */ 
/*     */   public double getBidVolume()
/*     */   {
/* 178 */     return this.bidVol;
/*     */   }
/*     */ 
/*     */   public double[] getAsks()
/*     */   {
/* 183 */     if (this.asks == null) {
/* 184 */       this.asks = new double[] { this.ask };
/*     */     }
/* 186 */     return this.asks;
/*     */   }
/*     */ 
/*     */   public double[] getBids()
/*     */   {
/* 191 */     if (this.bids == null) {
/* 192 */       this.bids = new double[] { this.bid };
/*     */     }
/* 194 */     return this.bids;
/*     */   }
/*     */ 
/*     */   public double[] getAskVolumes()
/*     */   {
/* 199 */     if (this.askVolumes == null) {
/* 200 */       this.askVolumes = new double[] { this.askVol };
/*     */     }
/* 202 */     return this.askVolumes;
/*     */   }
/*     */ 
/*     */   public double[] getBidVolumes()
/*     */   {
/* 207 */     if (this.bidVolumes == null) {
/* 208 */       this.bidVolumes = new double[] { this.bidVol };
/*     */     }
/* 210 */     return this.bidVolumes;
/*     */   }
/*     */ 
/*     */   public double getTotalAskVolume()
/*     */   {
/* 215 */     double totalAskVolume = 0.0D;
/* 216 */     for (double askVolume : this.askVolumes) {
/* 217 */       totalAskVolume += askVolume;
/*     */     }
/* 219 */     return ()(totalAskVolume * 100.0D + 0.5D) / 100.0D;
/*     */   }
/*     */ 
/*     */   public double getTotalBidVolume()
/*     */   {
/* 224 */     double totalBidVolume = 0.0D;
/* 225 */     for (double bidVolume : this.bidVolumes) {
/* 226 */       totalBidVolume += bidVolume;
/*     */     }
/* 228 */     return ()(totalBidVolume * 100.0D + 0.5D) / 100.0D;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.charts.data.datacache.TickData
 * JD-Core Version:    0.6.0
 */