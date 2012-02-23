/*     */ package org.apache.lucene.index;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.util.Arrays;
/*     */ import org.apache.lucene.store.BufferedIndexInput;
/*     */ import org.apache.lucene.store.IndexInput;
/*     */ 
/*     */ abstract class MultiLevelSkipListReader
/*     */ {
/*     */   private int maxNumberOfSkipLevels;
/*     */   private int numberOfSkipLevels;
/*  49 */   private int numberOfLevelsToBuffer = 1;
/*     */   private int docCount;
/*     */   private boolean haveSkipped;
/*     */   private IndexInput[] skipStream;
/*     */   private long[] skipPointer;
/*     */   private int[] skipInterval;
/*     */   private int[] numSkipped;
/*     */   private int[] skipDoc;
/*     */   private int lastDoc;
/*     */   private long[] childPointer;
/*     */   private long lastChildPointer;
/*     */   private boolean inputIsBuffered;
/*     */ 
/*     */   public MultiLevelSkipListReader(IndexInput skipStream, int maxSkipLevels, int skipInterval)
/*     */   {
/*  67 */     this.skipStream = new IndexInput[maxSkipLevels];
/*  68 */     this.skipPointer = new long[maxSkipLevels];
/*  69 */     this.childPointer = new long[maxSkipLevels];
/*  70 */     this.numSkipped = new int[maxSkipLevels];
/*  71 */     this.maxNumberOfSkipLevels = maxSkipLevels;
/*  72 */     this.skipInterval = new int[maxSkipLevels];
/*  73 */     this.skipStream[0] = skipStream;
/*  74 */     this.inputIsBuffered = (skipStream instanceof BufferedIndexInput);
/*  75 */     this.skipInterval[0] = skipInterval;
/*  76 */     for (int i = 1; i < maxSkipLevels; i++)
/*     */     {
/*  78 */       this.skipInterval[i] = (this.skipInterval[(i - 1)] * skipInterval);
/*     */     }
/*  80 */     this.skipDoc = new int[maxSkipLevels];
/*     */   }
/*     */ 
/*     */   int getDoc()
/*     */   {
/*  87 */     return this.lastDoc;
/*     */   }
/*     */ 
/*     */   int skipTo(int target)
/*     */     throws IOException
/*     */   {
/*  95 */     if (!this.haveSkipped)
/*     */     {
/*  97 */       loadSkipLevels();
/*  98 */       this.haveSkipped = true;
/*     */     }
/*     */ 
/* 103 */     int level = 0;
/* 104 */     while ((level < this.numberOfSkipLevels - 1) && (target > this.skipDoc[(level + 1)])) {
/* 105 */       level++;
/*     */     }
/*     */ 
/* 108 */     while (level >= 0) {
/* 109 */       if (target > this.skipDoc[level]) {
/* 110 */         if (!loadNextSkip(level)) {
/* 111 */           continue;
/*     */         }
/*     */       }
/*     */ 
/* 115 */       if ((level > 0) && (this.lastChildPointer > this.skipStream[(level - 1)].getFilePointer())) {
/* 116 */         seekChild(level - 1);
/*     */       }
/* 118 */       level--;
/*     */     }
/*     */ 
/* 122 */     return this.numSkipped[0] - this.skipInterval[0] - 1;
/*     */   }
/*     */ 
/*     */   private boolean loadNextSkip(int level)
/*     */     throws IOException
/*     */   {
/* 128 */     setLastSkipData(level);
/*     */ 
/* 130 */     this.numSkipped[level] += this.skipInterval[level];
/*     */ 
/* 132 */     if (this.numSkipped[level] > this.docCount)
/*     */     {
/* 134 */       this.skipDoc[level] = 2147483647;
/* 135 */       if (this.numberOfSkipLevels > level) this.numberOfSkipLevels = level;
/* 136 */       return false;
/*     */     }
/*     */ 
/* 140 */     this.skipDoc[level] += readSkipData(level, this.skipStream[level]);
/*     */ 
/* 142 */     if (level != 0)
/*     */     {
/* 144 */       this.childPointer[level] = (this.skipStream[level].readVLong() + this.skipPointer[(level - 1)]);
/*     */     }
/*     */ 
/* 147 */     return true;
/*     */   }
/*     */ 
/*     */   protected void seekChild(int level)
/*     */     throws IOException
/*     */   {
/* 153 */     this.skipStream[level].seek(this.lastChildPointer);
/* 154 */     this.numSkipped[level] = (this.numSkipped[(level + 1)] - this.skipInterval[(level + 1)]);
/* 155 */     this.skipDoc[level] = this.lastDoc;
/* 156 */     if (level > 0)
/* 157 */       this.childPointer[level] = (this.skipStream[level].readVLong() + this.skipPointer[(level - 1)]);
/*     */   }
/*     */ 
/*     */   void close() throws IOException
/*     */   {
/* 162 */     for (int i = 1; i < this.skipStream.length; i++)
/* 163 */       if (this.skipStream[i] != null)
/* 164 */         this.skipStream[i].close();
/*     */   }
/*     */ 
/*     */   void init(long skipPointer, int df)
/*     */   {
/* 171 */     this.skipPointer[0] = skipPointer;
/* 172 */     this.docCount = df;
/* 173 */     Arrays.fill(this.skipDoc, 0);
/* 174 */     Arrays.fill(this.numSkipped, 0);
/* 175 */     Arrays.fill(this.childPointer, 0L);
/*     */ 
/* 177 */     this.haveSkipped = false;
/* 178 */     for (int i = 1; i < this.numberOfSkipLevels; i++)
/* 179 */       this.skipStream[i] = null;
/*     */   }
/*     */ 
/*     */   private void loadSkipLevels()
/*     */     throws IOException
/*     */   {
/* 185 */     this.numberOfSkipLevels = (this.docCount == 0 ? 0 : (int)Math.floor(Math.log(this.docCount) / Math.log(this.skipInterval[0])));
/* 186 */     if (this.numberOfSkipLevels > this.maxNumberOfSkipLevels) {
/* 187 */       this.numberOfSkipLevels = this.maxNumberOfSkipLevels;
/*     */     }
/*     */ 
/* 190 */     this.skipStream[0].seek(this.skipPointer[0]);
/*     */ 
/* 192 */     int toBuffer = this.numberOfLevelsToBuffer;
/*     */ 
/* 194 */     for (int i = this.numberOfSkipLevels - 1; i > 0; i--)
/*     */     {
/* 196 */       long length = this.skipStream[0].readVLong();
/*     */ 
/* 199 */       this.skipPointer[i] = this.skipStream[0].getFilePointer();
/* 200 */       if (toBuffer > 0)
/*     */       {
/* 202 */         this.skipStream[i] = new SkipBuffer(this.skipStream[0], (int)length);
/* 203 */         toBuffer--;
/*     */       }
/*     */       else {
/* 206 */         this.skipStream[i] = ((IndexInput)this.skipStream[0].clone());
/* 207 */         if ((this.inputIsBuffered) && (length < 1024L)) {
/* 208 */           ((BufferedIndexInput)this.skipStream[i]).setBufferSize((int)length);
/*     */         }
/*     */ 
/* 212 */         this.skipStream[0].seek(this.skipStream[0].getFilePointer() + length);
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 217 */     this.skipPointer[0] = this.skipStream[0].getFilePointer();
/*     */   }
/*     */ 
/*     */   protected abstract int readSkipData(int paramInt, IndexInput paramIndexInput)
/*     */     throws IOException;
/*     */ 
/*     */   protected void setLastSkipData(int level)
/*     */   {
/* 230 */     this.lastDoc = this.skipDoc[level];
/* 231 */     this.lastChildPointer = this.childPointer[level];
/*     */   }
/*     */   private static final class SkipBuffer extends IndexInput {
/*     */     private byte[] data;
/*     */     private long pointer;
/*     */     private int pos;
/*     */ 
/*     */     SkipBuffer(IndexInput input, int length) throws IOException {
/* 242 */       this.data = new byte[length];
/* 243 */       this.pointer = input.getFilePointer();
/* 244 */       input.readBytes(this.data, 0, length);
/*     */     }
/*     */ 
/*     */     public void close() throws IOException
/*     */     {
/* 249 */       this.data = null;
/*     */     }
/*     */ 
/*     */     public long getFilePointer()
/*     */     {
/* 254 */       return this.pointer + this.pos;
/*     */     }
/*     */ 
/*     */     public long length()
/*     */     {
/* 259 */       return this.data.length;
/*     */     }
/*     */ 
/*     */     public byte readByte() throws IOException
/*     */     {
/* 264 */       return this.data[(this.pos++)];
/*     */     }
/*     */ 
/*     */     public void readBytes(byte[] b, int offset, int len) throws IOException
/*     */     {
/* 269 */       System.arraycopy(this.data, this.pos, b, offset, len);
/* 270 */       this.pos += len;
/*     */     }
/*     */ 
/*     */     public void seek(long pos) throws IOException
/*     */     {
/* 275 */       this.pos = (int)(pos - this.pointer);
/*     */     }
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.index.MultiLevelSkipListReader
 * JD-Core Version:    0.6.0
 */