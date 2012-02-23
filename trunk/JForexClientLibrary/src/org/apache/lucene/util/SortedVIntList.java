/*     */ package org.apache.lucene.util;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.util.BitSet;
/*     */ import org.apache.lucene.search.DocIdSet;
/*     */ import org.apache.lucene.search.DocIdSetIterator;
/*     */ 
/*     */ public class SortedVIntList extends DocIdSet
/*     */ {
/*     */   static final int BITS2VINTLIST_SIZE = 8;
/*     */   private int size;
/*     */   private byte[] bytes;
/*     */   private int lastBytePos;
/*     */   private static final int VB1 = 127;
/*     */   private static final int BIT_SHIFT = 7;
/* 156 */   private final int MAX_BYTES_PER_INT = 5;
/*     */ 
/*     */   public SortedVIntList(int[] sortedInts)
/*     */   {
/*  57 */     this(sortedInts, sortedInts.length);
/*     */   }
/*     */ 
/*     */   public SortedVIntList(int[] sortedInts, int inputSize)
/*     */   {
/*  66 */     SortedVIntListBuilder builder = new SortedVIntListBuilder();
/*  67 */     for (int i = 0; i < inputSize; i++) {
/*  68 */       builder.addInt(sortedInts[i]);
/*     */     }
/*  70 */     builder.done();
/*     */   }
/*     */ 
/*     */   public SortedVIntList(BitSet bits)
/*     */   {
/*  78 */     SortedVIntListBuilder builder = new SortedVIntListBuilder();
/*  79 */     int nextInt = bits.nextSetBit(0);
/*  80 */     while (nextInt != -1) {
/*  81 */       builder.addInt(nextInt);
/*  82 */       nextInt = bits.nextSetBit(nextInt + 1);
/*     */     }
/*  84 */     builder.done();
/*     */   }
/*     */ 
/*     */   public SortedVIntList(DocIdSetIterator docIdSetIterator)
/*     */     throws IOException
/*     */   {
/*  95 */     SortedVIntListBuilder builder = new SortedVIntListBuilder();
/*     */     int doc;
/*  97 */     while ((doc = docIdSetIterator.nextDoc()) != 2147483647) {
/*  98 */       builder.addInt(doc);
/*     */     }
/* 100 */     builder.done();
/*     */   }
/*     */ 
/*     */   private void initBytes()
/*     */   {
/* 141 */     this.size = 0;
/* 142 */     this.bytes = new byte[''];
/* 143 */     this.lastBytePos = 0;
/*     */   }
/*     */ 
/*     */   private void resizeBytes(int newSize) {
/* 147 */     if (newSize != this.bytes.length) {
/* 148 */       byte[] newBytes = new byte[newSize];
/* 149 */       System.arraycopy(this.bytes, 0, newBytes, 0, this.lastBytePos);
/* 150 */       this.bytes = newBytes;
/*     */     }
/*     */   }
/*     */ 
/*     */   public int size()
/*     */   {
/* 162 */     return this.size;
/*     */   }
/*     */ 
/*     */   public int getByteSize()
/*     */   {
/* 169 */     return this.bytes.length;
/*     */   }
/*     */ 
/*     */   public boolean isCacheable()
/*     */   {
/* 175 */     return true;
/*     */   }
/*     */ 
/*     */   public DocIdSetIterator iterator()
/*     */   {
/* 183 */     return new DocIdSetIterator() {
/* 184 */       int bytePos = 0;
/* 185 */       int lastInt = 0;
/* 186 */       int doc = -1;
/*     */ 
/*     */       private void advance()
/*     */       {
/* 190 */         byte b = SortedVIntList.this.bytes[(this.bytePos++)];
/* 191 */         this.lastInt += (b & 0x7F);
/* 192 */         for (int s = 7; (b & 0xFFFFFF80) != 0; s += 7) {
/* 193 */           b = SortedVIntList.this.bytes[(this.bytePos++)];
/* 194 */           this.lastInt += ((b & 0x7F) << s);
/*     */         }
/*     */       }
/*     */ 
/*     */       public int docID()
/*     */       {
/* 200 */         return this.doc;
/*     */       }
/*     */ 
/*     */       public int nextDoc()
/*     */       {
/* 205 */         if (this.bytePos >= SortedVIntList.this.lastBytePos) {
/* 206 */           this.doc = 2147483647;
/*     */         } else {
/* 208 */           advance();
/* 209 */           this.doc = this.lastInt;
/*     */         }
/* 211 */         return this.doc;
/*     */       }
/*     */ 
/*     */       public int advance(int target)
/*     */       {
/* 216 */         while (this.bytePos < SortedVIntList.this.lastBytePos) {
/* 217 */           advance();
/* 218 */           if (this.lastInt >= target) {
/* 219 */             return this.doc = this.lastInt;
/*     */           }
/*     */         }
/* 222 */         return this.doc = 2147483647;
/*     */       }
/*     */     };
/*     */   }
/*     */ 
/*     */   private class SortedVIntListBuilder
/*     */   {
/* 105 */     private int lastInt = 0;
/*     */ 
/*     */     SortedVIntListBuilder() {
/* 108 */       SortedVIntList.this.initBytes();
/* 109 */       this.lastInt = 0;
/*     */     }
/*     */ 
/*     */     void addInt(int nextInt) {
/* 113 */       int diff = nextInt - this.lastInt;
/* 114 */       if (diff < 0) {
/* 115 */         throw new IllegalArgumentException("Input not sorted or first element negative.");
/*     */       }
/*     */ 
/* 119 */       if (SortedVIntList.this.lastBytePos + 5 > SortedVIntList.this.bytes.length)
/*     */       {
/* 121 */         SortedVIntList.this.resizeBytes(ArrayUtil.oversize(SortedVIntList.this.lastBytePos + 5, 1));
/*     */       }
/*     */ 
/* 125 */       while ((diff & 0xFFFFFF80) != 0) {
/* 126 */         SortedVIntList.this.bytes[SortedVIntList.access$108(SortedVIntList.this)] = (byte)(diff & 0x7F | 0xFFFFFF80);
/* 127 */         diff >>>= 7;
/*     */       }
/* 129 */       SortedVIntList.this.bytes[SortedVIntList.access$108(SortedVIntList.this)] = (byte)diff;
/* 130 */       SortedVIntList.access$408(SortedVIntList.this);
/* 131 */       this.lastInt = nextInt;
/*     */     }
/*     */ 
/*     */     void done() {
/* 135 */       SortedVIntList.this.resizeBytes(SortedVIntList.this.lastBytePos);
/*     */     }
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.util.SortedVIntList
 * JD-Core Version:    0.6.0
 */