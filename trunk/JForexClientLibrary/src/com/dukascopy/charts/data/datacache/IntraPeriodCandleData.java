/*    */ package com.dukascopy.charts.data.datacache;
/*    */ 
/*    */ import java.text.SimpleDateFormat;
/*    */ import java.util.TimeZone;
/*    */ 
/*    */ public class IntraPeriodCandleData extends CandleData
/*    */ {
/*    */   private static final int BYTES_COUNT4 = 49;
/*    */   private static final int BYTES_COUNT5 = 25;
/*    */   public boolean empty;
/* 14 */   public boolean flat = true;
/*    */ 
/*    */   public IntraPeriodCandleData() {
/*    */   }
/*    */ 
/*    */   public IntraPeriodCandleData(boolean empty, long time, double open, double close, double low, double high, double vol) {
/* 20 */     super(time, open, close, low, high, vol);
/* 21 */     this.empty = empty;
/*    */   }
/*    */ 
/*    */   public int getBytesCount(int version) {
/* 25 */     if (version <= 4) {
/* 26 */       return 49;
/*    */     }
/* 28 */     return 25;
/*    */   }
/*    */ 
/*    */   public static int getLength(int version)
/*    */   {
/* 33 */     if (version <= 4) {
/* 34 */       return 49;
/*    */     }
/* 36 */     return 25;
/*    */   }
/*    */ 
/*    */   public void fromBytes(int version, long firstChunkCandle, double pipValue, byte[] bytes, int off)
/*    */   {
/* 41 */     this.empty = (bytes[off] == 1);
/* 42 */     if (!this.empty)
/* 43 */       super.fromBytes(version, firstChunkCandle, pipValue, bytes, off + 1);
/*    */   }
/*    */ 
/*    */   public void toBytes(int version, long firstChunkCandle, double pipValue, byte[] buff, int off)
/*    */   {
/* 48 */     if (buff.length < off + getLength(version)) {
/* 49 */       throw new ArrayIndexOutOfBoundsException("Buffer too short");
/*    */     }
/* 51 */     buff[off] = (this.empty ? 1 : 0);
/* 52 */     if (!this.empty)
/* 53 */       super.toBytes(version, firstChunkCandle, pipValue, buff, off + 1);
/*    */   }
/*    */ 
/*    */   public int hashCode()
/*    */   {
/* 59 */     int prime = 31;
/* 60 */     int result = super.hashCode();
/* 61 */     result = 31 * result + (this.empty ? 1231 : 1237);
/* 62 */     return result;
/*    */   }
/*    */ 
/*    */   public boolean equals(Object obj)
/*    */   {
/* 67 */     if (this == obj)
/* 68 */       return true;
/* 69 */     if (!super.equals(obj))
/* 70 */       return false;
/* 71 */     if (getClass() != obj.getClass())
/* 72 */       return false;
/* 73 */     IntraPeriodCandleData other = (IntraPeriodCandleData)obj;
/*    */ 
/* 75 */     return this.empty == other.empty;
/*    */   }
/*    */ 
/*    */   public String toString()
/*    */   {
/* 81 */     SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSZ");
/* 82 */     format.setTimeZone(TimeZone.getTimeZone("GMT"));
/* 83 */     return "{T:" + this.time + "(" + format.format(Long.valueOf(this.time)) + ")E:" + this.empty + "O:" + this.open + "C:" + this.close + "L:" + this.low + "H:" + this.high + "V" + this.vol + "}";
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.charts.data.datacache.IntraPeriodCandleData
 * JD-Core Version:    0.6.0
 */