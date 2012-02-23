/*     */ package com.dukascopy.dds2.greed.export.historicaldata;
/*     */ 
/*     */ import com.dukascopy.api.DataType;
/*     */ import com.dukascopy.api.IBar;
/*     */ import com.dukascopy.api.ITick;
/*     */ import com.dukascopy.api.feed.IPointAndFigure;
/*     */ import com.dukascopy.api.feed.IRangeBar;
/*     */ import com.dukascopy.api.feed.ITickBar;
/*     */ import java.io.BufferedWriter;
/*     */ import java.io.File;
/*     */ import java.io.FileWriter;
/*     */ import java.io.IOException;
/*     */ import java.io.Writer;
/*     */ import java.text.DecimalFormat;
/*     */ import java.text.SimpleDateFormat;
/*     */ import java.util.TimeZone;
/*     */ 
/*     */ public class CSVWriter
/*     */   implements IFileWriter
/*     */ {
/*     */   private static final String COL_TITLES_FOR_TICK_DATA = "Time,Ask,Bid,AskVolume,BidVolume \r\n";
/*     */   private static final String COL_TITLES_FOR_CANDLE_DATA = "Time,Open,High,Low,Close,Volume \r\n";
/*     */   private static final String COL_TITLES_FOR_BAR_DATA = "Time,EndTime,Open,High,Low,Close,Volume \r\n";
/*     */   private static final String COL_TITLES_FOR_POINT_AND_FIGURE_DATA = "Time,EndTime,Open,High,Low,Close,Volume,IsRising \r\n";
/*     */   private ExportDataParameters exportDataParameters;
/*     */   private Writer writer;
/*     */   private String fullFileName;
/*     */   private DataType dataType;
/*     */   private SimpleDateFormat dateFormat;
/*     */   private SimpleDateFormat dateFormatMillisec;
/*     */   private DecimalFormat priceFormat;
/*     */   private final String delimiter;
/*     */ 
/*     */   public CSVWriter(ExportDataParameters exportDataParameters, String fullFileName, DataType dataType)
/*     */   {
/*  37 */     this.exportDataParameters = exportDataParameters;
/*  38 */     this.fullFileName = fullFileName;
/*  39 */     this.dataType = dataType;
/*     */ 
/*  41 */     this.dateFormat = new SimpleDateFormat(this.exportDataParameters.getDateFormat());
/*  42 */     this.dateFormatMillisec = new SimpleDateFormat(this.exportDataParameters.getDateFormatMillisec());
/*  43 */     this.dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
/*  44 */     this.dateFormatMillisec.setTimeZone(TimeZone.getTimeZone("GMT"));
/*  45 */     this.priceFormat = new DecimalFormat("0.#####");
/*  46 */     this.delimiter = this.exportDataParameters.getDelimiter();
/*     */   }
/*     */ 
/*     */   public void writeHeader() throws IOException
/*     */   {
/*  51 */     String header = getHeader();
/*  52 */     FileWriter fileWriter = new FileWriter(new File(this.fullFileName));
/*  53 */     this.writer = new BufferedWriter(fileWriter);
/*  54 */     this.writer.append(header, 0, header.length());
/*     */   }
/*     */ 
/*     */   public void writeTickRateInfo(ITick tickData) throws IOException
/*     */   {
/*  59 */     this.writer.write(this.dateFormatMillisec.format(Long.valueOf(tickData.getTime())) + this.delimiter + this.priceFormat.format(tickData.getAsk()) + this.delimiter + this.priceFormat.format(tickData.getBid()) + this.delimiter + this.priceFormat.format(tickData.getAskVolume()) + this.delimiter + this.priceFormat.format(tickData.getBidVolume()) + "\r\n");
/*     */   }
/*     */ 
/*     */   public void writeCandleRateInfo(IBar candleData)
/*     */     throws IOException
/*     */   {
/*  71 */     SimpleDateFormat df = this.dateFormat;
/*     */ 
/*  73 */     this.writer.write(df.format(Long.valueOf(candleData.getTime())) + this.delimiter + this.priceFormat.format(candleData.getOpen()) + this.delimiter + this.priceFormat.format(candleData.getHigh()) + this.delimiter + this.priceFormat.format(candleData.getLow()) + this.delimiter + this.priceFormat.format(candleData.getClose()) + this.delimiter + this.priceFormat.format(candleData.getVolume()) + "\r\n");
/*     */   }
/*     */ 
/*     */   public void writeTickBarInfo(ITickBar tickBar)
/*     */     throws IOException
/*     */   {
/*  86 */     SimpleDateFormat df = this.dateFormatMillisec;
/*     */ 
/*  88 */     this.writer.write(df.format(Long.valueOf(tickBar.getTime())) + this.delimiter + df.format(Long.valueOf(tickBar.getEndTime())) + this.delimiter + this.priceFormat.format(tickBar.getOpen()) + this.delimiter + this.priceFormat.format(tickBar.getHigh()) + this.delimiter + this.priceFormat.format(tickBar.getLow()) + this.delimiter + this.priceFormat.format(tickBar.getClose()) + this.delimiter + this.priceFormat.format(tickBar.getVolume()) + "\r\n");
/*     */   }
/*     */ 
/*     */   public void writePriceRangeInfo(IRangeBar priceRange)
/*     */     throws IOException
/*     */   {
/* 103 */     SimpleDateFormat df = this.dateFormat;
/*     */ 
/* 105 */     this.writer.write(df.format(Long.valueOf(priceRange.getTime())) + this.delimiter + df.format(Long.valueOf(priceRange.getEndTime())) + this.delimiter + this.priceFormat.format(priceRange.getOpen()) + this.delimiter + this.priceFormat.format(priceRange.getHigh()) + this.delimiter + this.priceFormat.format(priceRange.getLow()) + this.delimiter + this.priceFormat.format(priceRange.getClose()) + this.delimiter + this.priceFormat.format(priceRange.getVolume()) + "\r\n");
/*     */   }
/*     */ 
/*     */   public void writePointAndFigureInfo(IPointAndFigure pointAndFigure)
/*     */     throws IOException
/*     */   {
/* 119 */     SimpleDateFormat df = this.dateFormatMillisec;
/*     */ 
/* 121 */     this.writer.write(df.format(Long.valueOf(pointAndFigure.getTime())) + this.delimiter + df.format(Long.valueOf(pointAndFigure.getEndTime())) + this.delimiter + this.priceFormat.format(pointAndFigure.getOpen()) + this.delimiter + this.priceFormat.format(pointAndFigure.getHigh()) + this.delimiter + this.priceFormat.format(pointAndFigure.getLow()) + this.delimiter + this.priceFormat.format(pointAndFigure.getClose()) + this.delimiter + this.priceFormat.format(pointAndFigure.getVolume()) + this.delimiter + Boolean.toString(pointAndFigure.isRising().booleanValue()) + "\r\n");
/*     */   }
/*     */ 
/*     */   public void close()
/*     */     throws IOException
/*     */   {
/* 137 */     this.writer.close();
/*     */   }
/*     */ 
/*     */   private String getHeader()
/*     */   {
/*     */     String header;
/*     */     String header;
/* 143 */     if (this.dataType == DataType.TICKS) {
/* 144 */       header = "Time,Ask,Bid,AskVolume,BidVolume \r\n";
/*     */     }
/*     */     else
/*     */     {
/*     */       String header;
/* 145 */       if (this.dataType == DataType.TIME_PERIOD_AGGREGATION) {
/* 146 */         header = "Time,Open,High,Low,Close,Volume \r\n";
/*     */       }
/*     */       else
/*     */       {
/*     */         String header;
/* 147 */         if (this.dataType == DataType.POINT_AND_FIGURE)
/* 148 */           header = "Time,EndTime,Open,High,Low,Close,Volume,IsRising \r\n";
/*     */         else
/* 150 */           header = "Time,EndTime,Open,High,Low,Close,Volume \r\n";
/*     */       }
/*     */     }
/* 153 */     return header;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.export.historicaldata.CSVWriter
 * JD-Core Version:    0.6.0
 */