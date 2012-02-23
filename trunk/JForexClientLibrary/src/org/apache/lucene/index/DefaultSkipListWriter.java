/*     */ package org.apache.lucene.index;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.util.Arrays;
/*     */ import org.apache.lucene.store.IndexOutput;
/*     */ 
/*     */ class DefaultSkipListWriter extends MultiLevelSkipListWriter
/*     */ {
/*     */   private int[] lastSkipDoc;
/*     */   private int[] lastSkipPayloadLength;
/*     */   private long[] lastSkipFreqPointer;
/*     */   private long[] lastSkipProxPointer;
/*     */   private IndexOutput freqOutput;
/*     */   private IndexOutput proxOutput;
/*     */   private int curDoc;
/*     */   private boolean curStorePayloads;
/*     */   private int curPayloadLength;
/*     */   private long curFreqPointer;
/*     */   private long curProxPointer;
/*     */ 
/*     */   DefaultSkipListWriter(int skipInterval, int numberOfSkipLevels, int docCount, IndexOutput freqOutput, IndexOutput proxOutput)
/*     */   {
/*  47 */     super(skipInterval, numberOfSkipLevels, docCount);
/*  48 */     this.freqOutput = freqOutput;
/*  49 */     this.proxOutput = proxOutput;
/*     */ 
/*  51 */     this.lastSkipDoc = new int[numberOfSkipLevels];
/*  52 */     this.lastSkipPayloadLength = new int[numberOfSkipLevels];
/*  53 */     this.lastSkipFreqPointer = new long[numberOfSkipLevels];
/*  54 */     this.lastSkipProxPointer = new long[numberOfSkipLevels];
/*     */   }
/*     */ 
/*     */   void setFreqOutput(IndexOutput freqOutput) {
/*  58 */     this.freqOutput = freqOutput;
/*     */   }
/*     */ 
/*     */   void setProxOutput(IndexOutput proxOutput) {
/*  62 */     this.proxOutput = proxOutput;
/*     */   }
/*     */ 
/*     */   void setSkipData(int doc, boolean storePayloads, int payloadLength)
/*     */   {
/*  69 */     this.curDoc = doc;
/*  70 */     this.curStorePayloads = storePayloads;
/*  71 */     this.curPayloadLength = payloadLength;
/*  72 */     this.curFreqPointer = this.freqOutput.getFilePointer();
/*  73 */     if (this.proxOutput != null)
/*  74 */       this.curProxPointer = this.proxOutput.getFilePointer();
/*     */   }
/*     */ 
/*     */   protected void resetSkip()
/*     */   {
/*  79 */     super.resetSkip();
/*  80 */     Arrays.fill(this.lastSkipDoc, 0);
/*  81 */     Arrays.fill(this.lastSkipPayloadLength, -1);
/*  82 */     Arrays.fill(this.lastSkipFreqPointer, this.freqOutput.getFilePointer());
/*  83 */     if (this.proxOutput != null)
/*  84 */       Arrays.fill(this.lastSkipProxPointer, this.proxOutput.getFilePointer());
/*     */   }
/*     */ 
/*     */   protected void writeSkipData(int level, IndexOutput skipBuffer)
/*     */     throws IOException
/*     */   {
/* 109 */     if (this.curStorePayloads) {
/* 110 */       int delta = this.curDoc - this.lastSkipDoc[level];
/* 111 */       if (this.curPayloadLength == this.lastSkipPayloadLength[level])
/*     */       {
/* 114 */         skipBuffer.writeVInt(delta * 2);
/*     */       }
/*     */       else
/*     */       {
/* 118 */         skipBuffer.writeVInt(delta * 2 + 1);
/* 119 */         skipBuffer.writeVInt(this.curPayloadLength);
/* 120 */         this.lastSkipPayloadLength[level] = this.curPayloadLength;
/*     */       }
/*     */     }
/*     */     else {
/* 124 */       skipBuffer.writeVInt(this.curDoc - this.lastSkipDoc[level]);
/*     */     }
/* 126 */     skipBuffer.writeVInt((int)(this.curFreqPointer - this.lastSkipFreqPointer[level]));
/* 127 */     skipBuffer.writeVInt((int)(this.curProxPointer - this.lastSkipProxPointer[level]));
/*     */ 
/* 129 */     this.lastSkipDoc[level] = this.curDoc;
/*     */ 
/* 132 */     this.lastSkipFreqPointer[level] = this.curFreqPointer;
/* 133 */     this.lastSkipProxPointer[level] = this.curProxPointer;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.index.DefaultSkipListWriter
 * JD-Core Version:    0.6.0
 */