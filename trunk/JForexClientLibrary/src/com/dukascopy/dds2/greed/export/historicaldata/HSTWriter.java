/*     */ package com.dukascopy.dds2.greed.export.historicaldata;
/*     */ 
/*     */ import com.dukascopy.api.IBar;
/*     */ import com.dukascopy.api.ITick;
/*     */ import com.dukascopy.api.Instrument;
/*     */ import com.dukascopy.api.Period;
/*     */ import com.dukascopy.api.feed.IPointAndFigure;
/*     */ import com.dukascopy.api.feed.IRangeBar;
/*     */ import com.dukascopy.api.feed.ITickBar;
/*     */ import java.io.FileOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.nio.ByteBuffer;
/*     */ 
/*     */ public class HSTWriter
/*     */   implements IFileWriter
/*     */ {
/*  16 */   private final int version = 400;
/*     */   private FileOutputStream fos;
/*     */   private String fullFileName;
/*     */   private Instrument instrument;
/*  21 */   private int periodInMinutes = 0;
/*     */   private Period period;
/*  23 */   private int digits = 0;
/*     */ 
/*     */   public HSTWriter(String fullFileName, Instrument instrument, Period period) throws IllegalArgumentException {
/*  26 */     if ((period != Period.ONE_MIN) && (period != Period.FIVE_MINS) && (period != Period.FIFTEEN_MINS) && (period != Period.THIRTY_MINS) && (period != Period.ONE_HOUR) && (period != Period.FOUR_HOURS) && (period != Period.DAILY) && (period != Period.WEEKLY) && (period != Period.MONTHLY))
/*     */     {
/*  36 */       throw new IllegalArgumentException("Incorrect period:" + period.toString());
/*     */     }
/*     */ 
/*  39 */     this.fullFileName = fullFileName;
/*  40 */     this.instrument = instrument;
/*  41 */     this.period = period;
/*  42 */     this.digits = instrument.getPipScale();
/*  43 */     this.periodInMinutes = (int)(this.period.getInterval() / Period.ONE_MIN.getInterval());
/*     */   }
/*     */ 
/*     */   public void writeHeader() throws IOException
/*     */   {
/*  48 */     this.fos = new FileOutputStream(this.fullFileName);
/*     */ 
/*  51 */     getClass(); byte[] bytesVersion = reverse(ByteBuffer.allocate(4).putInt(400).array());
/*  52 */     this.fos.write(bytesVersion);
/*     */ 
/*  55 */     String copyright = "(C)opyright 2003, MetaQuotes Software Corp.";
/*  56 */     byte[] bytesCopyright = new byte[64];
/*  57 */     System.arraycopy(copyright.getBytes(), 0, bytesCopyright, 0, copyright.length());
/*  58 */     this.fos.write(bytesCopyright);
/*     */ 
/*  61 */     String instrument = this.instrument.name();
/*  62 */     byte[] bytesInstrument = new byte[12];
/*  63 */     System.arraycopy(instrument.getBytes(), 0, bytesInstrument, 0, instrument.length());
/*  64 */     this.fos.write(bytesInstrument);
/*     */ 
/*  67 */     byte[] bytesPeriod = reverse(ByteBuffer.allocate(4).putInt(this.periodInMinutes).array());
/*  68 */     this.fos.write(bytesPeriod);
/*     */ 
/*  71 */     byte[] bytesDigits = reverse(ByteBuffer.allocate(4).putInt(this.digits).array());
/*  72 */     this.fos.write(bytesDigits);
/*     */ 
/*  75 */     byte[] bytesTimeSign = reverse(ByteBuffer.allocate(4).putInt(0).array());
/*  76 */     this.fos.write(bytesTimeSign);
/*     */ 
/*  79 */     byte[] bytesLastSync = reverse(ByteBuffer.allocate(4).putInt(0).array());
/*  80 */     this.fos.write(bytesLastSync);
/*     */ 
/*  83 */     byte[] bNotUsed = new byte[52];
/*  84 */     this.fos.write(bNotUsed);
/*     */ 
/*  86 */     this.fos.flush();
/*     */   }
/*     */ 
/*     */   public void writeCandleRateInfo(IBar candleData)
/*     */     throws IOException
/*     */   {
/* 102 */     int t = (int)(candleData.getTime() / 1000L);
/* 103 */     byte[] bytesTime = reverse(ByteBuffer.allocate(4).putInt(t).array());
/* 104 */     this.fos.write(bytesTime);
/*     */ 
/* 107 */     byte[] bytesOpen = reverse(ByteBuffer.allocate(8).putDouble(candleData.getOpen()).array());
/* 108 */     this.fos.write(bytesOpen);
/*     */ 
/* 111 */     byte[] bytesLow = reverse(ByteBuffer.allocate(8).putDouble(candleData.getLow()).array());
/* 112 */     this.fos.write(bytesLow);
/*     */ 
/* 115 */     byte[] bytesHigh = reverse(ByteBuffer.allocate(8).putDouble(candleData.getHigh()).array());
/* 116 */     this.fos.write(bytesHigh);
/*     */ 
/* 119 */     byte[] bytesClose = reverse(ByteBuffer.allocate(8).putDouble(candleData.getClose()).array());
/* 120 */     this.fos.write(bytesClose);
/*     */ 
/* 123 */     byte[] bytesVol = reverse(ByteBuffer.allocate(8).putDouble(candleData.getVolume()).array());
/* 124 */     this.fos.write(bytesVol);
/*     */   }
/*     */ 
/*     */   public void writeTickRateInfo(ITick tickData) throws IOException
/*     */   {
/*     */   }
/*     */ 
/*     */   public void close() throws IOException
/*     */   {
/* 133 */     this.fos.flush();
/* 134 */     this.fos.close();
/*     */   }
/*     */ 
/*     */   private byte[] reverse(byte[] array) {
/* 138 */     int i = 0; int j = array.length - 1;
/* 139 */     while (i < j) {
/* 140 */       swap(array, i++, j--);
/*     */     }
/* 142 */     return array;
/*     */   }
/*     */ 
/*     */   private void swap(byte[] array, int first, int second) {
/* 146 */     byte temp = array[first];
/* 147 */     array[first] = array[second];
/* 148 */     array[second] = temp;
/*     */   }
/*     */ 
/*     */   public void writeTickBarInfo(ITickBar tickBar)
/*     */     throws IOException
/*     */   {
/*     */   }
/*     */ 
/*     */   public void writePriceRangeInfo(IRangeBar priceRange)
/*     */     throws IOException
/*     */   {
/*     */   }
/*     */ 
/*     */   public void writePointAndFigureInfo(IPointAndFigure pointAndFigure)
/*     */     throws IOException
/*     */   {
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.export.historicaldata.HSTWriter
 * JD-Core Version:    0.6.0
 */