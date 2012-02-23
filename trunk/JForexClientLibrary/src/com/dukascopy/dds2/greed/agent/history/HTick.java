/*    */ package com.dukascopy.dds2.greed.agent.history;
/*    */ 
/*    */ import java.nio.ByteBuffer;
/*    */ import java.text.SimpleDateFormat;
/*    */ import java.util.Date;
/*    */ 
/*    */ public class HTick
/*    */ {
/* 34 */   private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSZ");
/*    */   public long time;
/*    */   public double ask;
/*    */   public double bid;
/*    */   public double askVol;
/*    */   public double bidVol;
/*    */ 
/*    */   public static HTick make(long time, double bid, double ask)
/*    */   {
/* 17 */     return make(time, bid, ask, 0.0D, 0.0D);
/*    */   }
/*    */ 
/*    */   public static HTick make(long time, double bid, double ask, double bidVolume, double askVolume) {
/* 21 */     HTick tick = new HTick();
/* 22 */     tick.time = time;
/* 23 */     tick.ask = ask;
/* 24 */     tick.bid = bid;
/* 25 */     tick.askVol = askVolume;
/* 26 */     tick.bidVol = bidVolume;
/* 27 */     return tick;
/*    */   }
/*    */ 
/*    */   public String toString()
/*    */   {
/* 38 */     return this.time + " " + this.bid + "/" + this.ask + " " + dateFormat.format(new Date(this.time));
/*    */   }
/*    */ 
/*    */   public HTick instance()
/*    */   {
/* 53 */     return new HTick();
/*    */   }
/*    */ 
/*    */   public long key(HTick t) {
/* 57 */     return t.time;
/*    */   }
/*    */ 
/*    */   public HTick read(ByteBuffer buffer) {
/* 61 */     HTick t = instance();
/* 62 */     t.time = buffer.getLong();
/* 63 */     t.bid = buffer.getDouble();
/* 64 */     t.ask = buffer.getDouble();
/* 65 */     t.bidVol = buffer.getDouble();
/* 66 */     t.askVol = buffer.getDouble();
/* 67 */     return t;
/*    */   }
/*    */ 
/*    */   public void write(ByteBuffer buffer, HTick t)
/*    */   {
/* 72 */     buffer.putLong(t.time);
/* 73 */     buffer.putDouble(t.bid);
/*    */ 
/* 75 */     buffer.putDouble(t.ask);
/* 76 */     buffer.putDouble(t.bidVol);
/* 77 */     buffer.putDouble(t.askVol);
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.dds2.greed.agent.history.HTick
 * JD-Core Version:    0.6.0
 */