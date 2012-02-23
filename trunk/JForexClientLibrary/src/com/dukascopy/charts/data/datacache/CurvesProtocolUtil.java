/*     */ package com.dukascopy.charts.data.datacache;
/*     */ 
/*     */ import com.dukascopy.api.Period;
/*     */ import com.dukascopy.dds2.greed.agent.strategy.StratUtils;
/*     */ import com.dukascopy.transport.util.Base64;
/*     */ import java.io.ByteArrayInputStream;
/*     */ import java.io.ByteArrayOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.util.List;
/*     */ import java.util.StringTokenizer;
/*     */ import java.util.concurrent.TimeUnit;
/*     */ import java.util.zip.ZipEntry;
/*     */ import java.util.zip.ZipInputStream;
/*     */ 
/*     */ public class CurvesProtocolUtil
/*     */ {
/*     */   private static final String DATA_DELIMETER = ",";
/*     */   private static final String COMPRESSED_DATA_DELIMETER = ";";
/*     */   private static final long ONE_MILLION = 1000000L;
/*  26 */   private static final long ONE_SEC = TimeUnit.SECONDS.toMillis(1L);
/*     */ 
/*     */   public static void parseCandles(String compressedCandles, boolean forIntraPeriod, List<Data> data)
/*     */     throws IOException
/*     */   {
/*  31 */     if (compressedCandles == null) {
/*  32 */       return;
/*     */     }
/*  34 */     String allData = new String(decompress(compressedCandles));
/*  35 */     StringTokenizer tokenizer = new StringTokenizer(allData, ";");
/*  36 */     while (tokenizer.hasMoreTokens()) {
/*  37 */       String token = tokenizer.nextToken();
/*  38 */       if (forIntraPeriod)
/*  39 */         data.add(parseIntraPeriodCandle(token));
/*     */       else
/*  41 */         data.add(parseCandle(token));
/*     */     }
/*     */   }
/*     */ 
/*     */   public static CandleData parseCandle(String candleStr)
/*     */   {
/*  47 */     StringTokenizer tokenizer = new StringTokenizer(candleStr, ",");
/*  48 */     CandleData candleData = new CandleData();
/*  49 */     candleData.time = (Long.valueOf(tokenizer.nextToken()).longValue() * ONE_SEC);
/*  50 */     candleData.vol = Double.valueOf(tokenizer.nextToken()).doubleValue();
/*  51 */     candleData.low = Double.valueOf(tokenizer.nextToken()).doubleValue();
/*  52 */     candleData.high = Double.valueOf(tokenizer.nextToken()).doubleValue();
/*  53 */     candleData.open = Double.valueOf(tokenizer.nextToken()).doubleValue();
/*  54 */     candleData.close = Double.valueOf(tokenizer.nextToken()).doubleValue();
/*  55 */     return candleData;
/*     */   }
/*     */ 
/*     */   public static CandleData parseIntraPeriodCandle(String candleStr) {
/*  59 */     StringTokenizer tokenizer = new StringTokenizer(candleStr, ",");
/*  60 */     IntraPeriodCandleData candleData = new IntraPeriodCandleData();
/*  61 */     candleData.time = (Long.valueOf(tokenizer.nextToken()).longValue() * ONE_SEC);
/*  62 */     candleData.vol = Double.valueOf(tokenizer.nextToken()).doubleValue();
/*  63 */     candleData.low = Double.valueOf(tokenizer.nextToken()).doubleValue();
/*  64 */     candleData.high = Double.valueOf(tokenizer.nextToken()).doubleValue();
/*  65 */     candleData.open = Double.valueOf(tokenizer.nextToken()).doubleValue();
/*  66 */     candleData.close = Double.valueOf(tokenizer.nextToken()).doubleValue();
/*  67 */     return candleData;
/*     */   }
/*     */ 
/*     */   public static TickData parseTick(String tickStr) {
/*  71 */     StringTokenizer tokenizer = new StringTokenizer(tickStr, ",");
/*     */ 
/*  73 */     TickData tick = new TickData();
/*  74 */     tick.time = Long.valueOf(tokenizer.nextToken()).longValue();
/*     */ 
/*  76 */     tokenizer.nextToken();
/*  77 */     tick.ask = Double.valueOf(tokenizer.nextToken()).doubleValue();
/*  78 */     tick.bid = Double.valueOf(tokenizer.nextToken()).doubleValue();
/*  79 */     tick.askVol = Double.valueOf(tokenizer.nextToken()).doubleValue();
/*  80 */     tick.bidVol = Double.valueOf(tokenizer.nextToken()).doubleValue();
/*     */ 
/*  82 */     tick.askVol = StratUtils.round(tick.askVol / 1000000.0D, 6);
/*  83 */     tick.bidVol = StratUtils.round(tick.bidVol / 1000000.0D, 6);
/*  84 */     return tick;
/*     */   }
/*     */ 
/*     */   public static void parseTicks(String compressedTicks, List<Data> data) throws IOException {
/*  88 */     if (compressedTicks == null) {
/*  89 */       return;
/*     */     }
/*  91 */     String allData = new String(decompress(compressedTicks));
/*  92 */     StringTokenizer strTokenizer = new StringTokenizer(allData, ";");
/*  93 */     while (strTokenizer.hasMoreTokens()) {
/*  94 */       String token = strTokenizer.nextToken();
/*  95 */       data.add(parseTick(token));
/*     */     }
/*     */   }
/*     */ 
/*     */   public static Data[] bytesToChunkData(byte[] data, Period period, int version, long firstChunkCandle, double pipValue) throws DataCacheException {
/* 100 */     if (period == Period.TICK) {
/* 101 */       int bytesCount = TickData.getLength(version);
/* 102 */       if (data.length % bytesCount != 0) {
/* 103 */         throw new DataCacheException("Downloaded data file corrupted, unexpected file size ticks [" + data.length + "]");
/*     */       }
/* 105 */       Data[] dataArray = new Data[data.length / bytesCount];
/* 106 */       int offset = 0; for (int i = 0; offset < data.length; i++) {
/* 107 */         TickData tickData = new TickData();
/* 108 */         tickData.fromBytes(version, firstChunkCandle, pipValue, data, offset);
/* 109 */         dataArray[i] = tickData;
/*     */ 
/* 106 */         offset += bytesCount;
/*     */       }
/*     */ 
/* 111 */       return dataArray;
/*     */     }
/* 113 */     int bytesCount = CandleData.getLength(version);
/* 114 */     if (data.length % bytesCount != 0) {
/* 115 */       throw new DataCacheException("Downloaded data file corrupted, unexpected file size candles [" + data.length + "]");
/*     */     }
/* 117 */     Data[] dataArray = new Data[data.length / bytesCount];
/* 118 */     int offset = 0; for (int i = 0; offset < data.length; i++) {
/* 119 */       CandleData candleData = new CandleData();
/* 120 */       candleData.fromBytes(version, firstChunkCandle, pipValue, data, offset);
/* 121 */       dataArray[i] = candleData;
/*     */ 
/* 118 */       offset += bytesCount;
/*     */     }
/*     */ 
/* 123 */     return dataArray;
/*     */   }
/*     */ 
/*     */   private static byte[] decompress(String compressedData) throws IOException
/*     */   {
/* 128 */     byte[] bytes = Base64.decode(compressedData);
/* 129 */     ZipInputStream zip = new ZipInputStream(new ByteArrayInputStream(bytes));
/*     */     try {
/* 131 */       ZipEntry zipEntry = zip.getNextEntry();
/* 132 */       int size = (int)zipEntry.getSize();
/*     */       ByteArrayOutputStream output;
/*     */       ByteArrayOutputStream output;
/* 134 */       if (size == -1)
/* 135 */         output = new ByteArrayOutputStream();
/*     */       else {
/* 137 */         output = new ByteArrayOutputStream(size);
/*     */       }
/* 139 */       byte[] buffer = new byte[512];
/*     */       int i;
/* 141 */       while ((i = zip.read(buffer)) > -1) {
/* 142 */         output.write(buffer, 0, i);
/*     */       }
/* 144 */       byte[] arrayOfByte1 = output.toByteArray();
/*     */       return arrayOfByte1; } finally { zip.close(); } throw localObject;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.charts.data.datacache.CurvesProtocolUtil
 * JD-Core Version:    0.6.0
 */