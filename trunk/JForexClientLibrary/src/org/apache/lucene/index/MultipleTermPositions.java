/*     */ package org.apache.lucene.index;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.util.Arrays;
/*     */ import java.util.LinkedList;
/*     */ import java.util.List;
/*     */ import org.apache.lucene.util.ArrayUtil;
/*     */ import org.apache.lucene.util.PriorityQueue;
/*     */ 
/*     */ public class MultipleTermPositions
/*     */   implements TermPositions
/*     */ {
/*     */   private int _doc;
/*     */   private int _freq;
/*     */   private TermPositionsQueue _termPositionsQueue;
/*     */   private IntQueue _posList;
/*     */ 
/*     */   public MultipleTermPositions(IndexReader indexReader, Term[] terms)
/*     */     throws IOException
/*     */   {
/* 102 */     List termPositions = new LinkedList();
/*     */ 
/* 104 */     for (int i = 0; i < terms.length; i++) {
/* 105 */       termPositions.add(indexReader.termPositions(terms[i]));
/*     */     }
/* 107 */     this._termPositionsQueue = new TermPositionsQueue(termPositions);
/* 108 */     this._posList = new IntQueue(null);
/*     */   }
/*     */ 
/*     */   public final boolean next() throws IOException {
/* 112 */     if (this._termPositionsQueue.size() == 0) {
/* 113 */       return false;
/*     */     }
/* 115 */     this._posList.clear();
/* 116 */     this._doc = this._termPositionsQueue.peek().doc();
/*     */     do
/*     */     {
/* 120 */       TermPositions tp = this._termPositionsQueue.peek();
/*     */ 
/* 122 */       for (int i = 0; i < tp.freq(); i++)
/*     */       {
/* 124 */         this._posList.add(tp.nextPosition());
/*     */       }
/*     */ 
/* 127 */       if (tp.next()) {
/* 128 */         this._termPositionsQueue.updateTop();
/*     */       } else {
/* 130 */         this._termPositionsQueue.pop();
/* 131 */         tp.close();
/*     */       }
/*     */     }
/* 133 */     while ((this._termPositionsQueue.size() > 0) && (this._termPositionsQueue.peek().doc() == this._doc));
/*     */ 
/* 135 */     this._posList.sort();
/* 136 */     this._freq = this._posList.size();
/*     */ 
/* 138 */     return true;
/*     */   }
/*     */ 
/*     */   public final int nextPosition()
/*     */   {
/* 144 */     return this._posList.next();
/*     */   }
/*     */ 
/*     */   public final boolean skipTo(int target) throws IOException {
/* 148 */     while ((this._termPositionsQueue.peek() != null) && (target > this._termPositionsQueue.peek().doc())) {
/* 149 */       TermPositions tp = (TermPositions)this._termPositionsQueue.pop();
/* 150 */       if (tp.skipTo(target))
/* 151 */         this._termPositionsQueue.add(tp);
/*     */       else
/* 153 */         tp.close();
/*     */     }
/* 155 */     return next();
/*     */   }
/*     */ 
/*     */   public final int doc() {
/* 159 */     return this._doc;
/*     */   }
/*     */ 
/*     */   public final int freq() {
/* 163 */     return this._freq;
/*     */   }
/*     */ 
/*     */   public final void close() throws IOException {
/* 167 */     while (this._termPositionsQueue.size() > 0)
/* 168 */       ((TermPositions)this._termPositionsQueue.pop()).close();
/*     */   }
/*     */ 
/*     */   public void seek(Term arg0)
/*     */     throws IOException
/*     */   {
/* 176 */     throw new UnsupportedOperationException();
/*     */   }
/*     */ 
/*     */   public void seek(TermEnum termEnum)
/*     */     throws IOException
/*     */   {
/* 184 */     throw new UnsupportedOperationException();
/*     */   }
/*     */ 
/*     */   public int read(int[] arg0, int[] arg1)
/*     */     throws IOException
/*     */   {
/* 192 */     throw new UnsupportedOperationException();
/*     */   }
/*     */ 
/*     */   public int getPayloadLength()
/*     */   {
/* 201 */     throw new UnsupportedOperationException();
/*     */   }
/*     */ 
/*     */   public byte[] getPayload(byte[] data, int offset)
/*     */     throws IOException
/*     */   {
/* 209 */     throw new UnsupportedOperationException();
/*     */   }
/*     */ 
/*     */   public boolean isPayloadAvailable()
/*     */   {
/* 218 */     return false;
/*     */   }
/*     */ 
/*     */   private static final class IntQueue
/*     */   {
/*  56 */     private int _arraySize = 16;
/*  57 */     private int _index = 0;
/*  58 */     private int _lastIndex = 0;
/*  59 */     private int[] _array = new int[this._arraySize];
/*     */ 
/*     */     final void add(int i) {
/*  62 */       if (this._lastIndex == this._arraySize) {
/*  63 */         growArray();
/*     */       }
/*  65 */       this._array[(this._lastIndex++)] = i;
/*     */     }
/*     */ 
/*     */     final int next() {
/*  69 */       return this._array[(this._index++)];
/*     */     }
/*     */ 
/*     */     final void sort() {
/*  73 */       Arrays.sort(this._array, this._index, this._lastIndex);
/*     */     }
/*     */ 
/*     */     final void clear() {
/*  77 */       this._index = 0;
/*  78 */       this._lastIndex = 0;
/*     */     }
/*     */ 
/*     */     final int size() {
/*  82 */       return this._lastIndex - this._index;
/*     */     }
/*     */ 
/*     */     private void growArray() {
/*  86 */       this._array = ArrayUtil.grow(this._array, this._arraySize + 1);
/*  87 */       this._arraySize = this._array.length;
/*     */     }
/*     */   }
/*     */ 
/*     */   private static final class TermPositionsQueue extends PriorityQueue<TermPositions>
/*     */   {
/*     */     TermPositionsQueue(List<TermPositions> termPositions)
/*     */       throws IOException
/*     */     {
/*  37 */       initialize(termPositions.size());
/*     */ 
/*  39 */       for (TermPositions tp : termPositions)
/*  40 */         if (tp.next())
/*  41 */           add(tp);
/*     */     }
/*     */ 
/*     */     final TermPositions peek()
/*     */     {
/*  46 */       return (TermPositions)top();
/*     */     }
/*     */ 
/*     */     public final boolean lessThan(TermPositions a, TermPositions b)
/*     */     {
/*  51 */       return a.doc() < b.doc();
/*     */     }
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.index.MultipleTermPositions
 * JD-Core Version:    0.6.0
 */