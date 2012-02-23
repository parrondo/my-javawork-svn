/*     */ package org.apache.lucene.index;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import org.apache.lucene.store.IndexOutput;
/*     */ import org.apache.lucene.store.RAMOutputStream;
/*     */ 
/*     */ abstract class MultiLevelSkipListWriter
/*     */ {
/*     */   private int numberOfSkipLevels;
/*     */   private int skipInterval;
/*     */   private RAMOutputStream[] skipBuffer;
/*     */ 
/*     */   protected MultiLevelSkipListWriter(int skipInterval, int maxSkipLevels, int df)
/*     */   {
/*  60 */     this.skipInterval = skipInterval;
/*     */ 
/*  63 */     this.numberOfSkipLevels = (df == 0 ? 0 : (int)Math.floor(Math.log(df) / Math.log(skipInterval)));
/*     */ 
/*  66 */     if (this.numberOfSkipLevels > maxSkipLevels)
/*  67 */       this.numberOfSkipLevels = maxSkipLevels;
/*     */   }
/*     */ 
/*     */   protected void init()
/*     */   {
/*  72 */     this.skipBuffer = new RAMOutputStream[this.numberOfSkipLevels];
/*  73 */     for (int i = 0; i < this.numberOfSkipLevels; i++)
/*  74 */       this.skipBuffer[i] = new RAMOutputStream();
/*     */   }
/*     */ 
/*     */   protected void resetSkip()
/*     */   {
/*  80 */     if (this.skipBuffer == null)
/*  81 */       init();
/*     */     else
/*  83 */       for (int i = 0; i < this.skipBuffer.length; i++)
/*  84 */         this.skipBuffer[i].reset();
/*     */   }
/*     */ 
/*     */   protected abstract void writeSkipData(int paramInt, IndexOutput paramIndexOutput)
/*     */     throws IOException;
/*     */ 
/*     */   void bufferSkip(int df)
/*     */     throws IOException
/*     */   {
/* 108 */     for (int numLevels = 0; (df % this.skipInterval == 0) && (numLevels < this.numberOfSkipLevels); df /= this.skipInterval) {
/* 109 */       numLevels++;
/*     */     }
/*     */ 
/* 112 */     long childPointer = 0L;
/*     */ 
/* 114 */     for (int level = 0; level < numLevels; level++) {
/* 115 */       writeSkipData(level, this.skipBuffer[level]);
/*     */ 
/* 117 */       long newChildPointer = this.skipBuffer[level].getFilePointer();
/*     */ 
/* 119 */       if (level != 0)
/*     */       {
/* 121 */         this.skipBuffer[level].writeVLong(childPointer);
/*     */       }
/*     */ 
/* 125 */       childPointer = newChildPointer;
/*     */     }
/*     */   }
/*     */ 
/*     */   long writeSkip(IndexOutput output)
/*     */     throws IOException
/*     */   {
/* 136 */     long skipPointer = output.getFilePointer();
/* 137 */     if ((this.skipBuffer == null) || (this.skipBuffer.length == 0)) return skipPointer;
/*     */ 
/* 139 */     for (int level = this.numberOfSkipLevels - 1; level > 0; level--) {
/* 140 */       long length = this.skipBuffer[level].getFilePointer();
/* 141 */       if (length > 0L) {
/* 142 */         output.writeVLong(length);
/* 143 */         this.skipBuffer[level].writeTo(output);
/*     */       }
/*     */     }
/* 146 */     this.skipBuffer[0].writeTo(output);
/*     */ 
/* 148 */     return skipPointer;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.index.MultiLevelSkipListWriter
 * JD-Core Version:    0.6.0
 */