/*     */ package org.apache.lucene.index;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.util.Arrays;
/*     */ import org.apache.lucene.store.IndexInput;
/*     */ 
/*     */ class DefaultSkipListReader extends MultiLevelSkipListReader
/*     */ {
/*     */   private boolean currentFieldStoresPayloads;
/*     */   private long[] freqPointer;
/*     */   private long[] proxPointer;
/*     */   private int[] payloadLength;
/*     */   private long lastFreqPointer;
/*     */   private long lastProxPointer;
/*     */   private int lastPayloadLength;
/*     */ 
/*     */   DefaultSkipListReader(IndexInput skipStream, int maxSkipLevels, int skipInterval)
/*     */   {
/*  42 */     super(skipStream, maxSkipLevels, skipInterval);
/*  43 */     this.freqPointer = new long[maxSkipLevels];
/*  44 */     this.proxPointer = new long[maxSkipLevels];
/*  45 */     this.payloadLength = new int[maxSkipLevels];
/*     */   }
/*     */ 
/*     */   void init(long skipPointer, long freqBasePointer, long proxBasePointer, int df, boolean storesPayloads) {
/*  49 */     super.init(skipPointer, df);
/*  50 */     this.currentFieldStoresPayloads = storesPayloads;
/*  51 */     this.lastFreqPointer = freqBasePointer;
/*  52 */     this.lastProxPointer = proxBasePointer;
/*     */ 
/*  54 */     Arrays.fill(this.freqPointer, freqBasePointer);
/*  55 */     Arrays.fill(this.proxPointer, proxBasePointer);
/*  56 */     Arrays.fill(this.payloadLength, 0);
/*     */   }
/*     */ 
/*     */   long getFreqPointer()
/*     */   {
/*  62 */     return this.lastFreqPointer;
/*     */   }
/*     */ 
/*     */   long getProxPointer()
/*     */   {
/*  68 */     return this.lastProxPointer;
/*     */   }
/*     */ 
/*     */   int getPayloadLength()
/*     */   {
/*  75 */     return this.lastPayloadLength;
/*     */   }
/*     */ 
/*     */   protected void seekChild(int level) throws IOException
/*     */   {
/*  80 */     super.seekChild(level);
/*  81 */     this.freqPointer[level] = this.lastFreqPointer;
/*  82 */     this.proxPointer[level] = this.lastProxPointer;
/*  83 */     this.payloadLength[level] = this.lastPayloadLength;
/*     */   }
/*     */ 
/*     */   protected void setLastSkipData(int level)
/*     */   {
/*  88 */     super.setLastSkipData(level);
/*  89 */     this.lastFreqPointer = this.freqPointer[level];
/*  90 */     this.lastProxPointer = this.proxPointer[level];
/*  91 */     this.lastPayloadLength = this.payloadLength[level];
/*     */   }
/*     */ 
/*     */   protected int readSkipData(int level, IndexInput skipStream)
/*     */     throws IOException
/*     */   {
/*     */     int delta;
/*  98 */     if (this.currentFieldStoresPayloads)
/*     */     {
/* 104 */       int delta = skipStream.readVInt();
/* 105 */       if ((delta & 0x1) != 0) {
/* 106 */         this.payloadLength[level] = skipStream.readVInt();
/*     */       }
/* 108 */       delta >>>= 1;
/*     */     } else {
/* 110 */       delta = skipStream.readVInt();
/*     */     }
/* 112 */     this.freqPointer[level] += skipStream.readVInt();
/* 113 */     this.proxPointer[level] += skipStream.readVInt();
/*     */ 
/* 115 */     return delta;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.index.DefaultSkipListReader
 * JD-Core Version:    0.6.0
 */