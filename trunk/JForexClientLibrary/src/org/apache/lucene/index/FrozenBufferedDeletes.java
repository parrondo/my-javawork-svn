/*     */ package org.apache.lucene.index;
/*     */ 
/*     */ import java.util.Iterator;
/*     */ import java.util.Map;
/*     */ import java.util.Map.Entry;
/*     */ import java.util.Set;
/*     */ import java.util.concurrent.atomic.AtomicInteger;
/*     */ import org.apache.lucene.search.Query;
/*     */ import org.apache.lucene.util.ArrayUtil;
/*     */ import org.apache.lucene.util.RamUsageEstimator;
/*     */ 
/*     */ class FrozenBufferedDeletes
/*     */ {
/*  36 */   static final int BYTES_PER_DEL_QUERY = RamUsageEstimator.NUM_BYTES_OBJECT_REF + 4 + 24;
/*     */   final PrefixCodedTerms terms;
/*     */   int termCount;
/*     */   final Query[] queries;
/*     */   final int[] queryLimits;
/*     */   final int bytesUsed;
/*     */   final int numTermDeletes;
/*     */   final long gen;
/*     */ 
/*     */   public FrozenBufferedDeletes(BufferedDeletes deletes, long gen)
/*     */   {
/*  51 */     Term[] termsArray = (Term[])deletes.terms.keySet().toArray(new Term[deletes.terms.size()]);
/*  52 */     this.termCount = termsArray.length;
/*  53 */     ArrayUtil.mergeSort(termsArray);
/*  54 */     PrefixCodedTerms.Builder builder = new PrefixCodedTerms.Builder();
/*  55 */     for (Term term : termsArray) {
/*  56 */       builder.add(term);
/*     */     }
/*  58 */     this.terms = builder.finish();
/*     */ 
/*  60 */     this.queries = new Query[deletes.queries.size()];
/*  61 */     this.queryLimits = new int[deletes.queries.size()];
/*  62 */     int upto = 0;
/*  63 */     for (Map.Entry ent : deletes.queries.entrySet()) {
/*  64 */       this.queries[upto] = ((Query)ent.getKey());
/*  65 */       this.queryLimits[upto] = ((Integer)ent.getValue()).intValue();
/*  66 */       upto++;
/*     */     }
/*     */ 
/*  69 */     this.bytesUsed = ((int)this.terms.getSizeInBytes() + this.queries.length * BYTES_PER_DEL_QUERY);
/*  70 */     this.numTermDeletes = deletes.numTermDeletes.get();
/*  71 */     this.gen = gen;
/*     */   }
/*     */ 
/*     */   public Iterable<Term> termsIterable() {
/*  75 */     return new Iterable()
/*     */     {
/*     */       public Iterator<Term> iterator() {
/*  78 */         return FrozenBufferedDeletes.this.terms.iterator();
/*     */       } } ;
/*     */   }
/*     */ 
/*     */   public Iterable<BufferedDeletesStream.QueryAndLimit> queriesIterable() {
/*  84 */     return new Iterable()
/*     */     {
/*     */       public Iterator<BufferedDeletesStream.QueryAndLimit> iterator() {
/*  87 */         return new Iterator() {
/*     */           private int upto;
/*     */ 
/*     */           public boolean hasNext() {
/*  92 */             return this.upto < FrozenBufferedDeletes.this.queries.length;
/*     */           }
/*     */ 
/*     */           public BufferedDeletesStream.QueryAndLimit next()
/*     */           {
/*  97 */             BufferedDeletesStream.QueryAndLimit ret = new BufferedDeletesStream.QueryAndLimit(FrozenBufferedDeletes.this.queries[this.upto], FrozenBufferedDeletes.this.queryLimits[this.upto]);
/*  98 */             this.upto += 1;
/*  99 */             return ret;
/*     */           }
/*     */ 
/*     */           public void remove()
/*     */           {
/* 104 */             throw new UnsupportedOperationException();
/*     */           } } ;
/*     */       }
/*     */     };
/*     */   }
/*     */ 
/*     */   public String toString() {
/* 113 */     String s = "";
/* 114 */     if (this.numTermDeletes != 0) {
/* 115 */       s = s + " " + this.numTermDeletes + " deleted terms (unique count=" + this.termCount + ")";
/*     */     }
/* 117 */     if (this.queries.length != 0) {
/* 118 */       s = s + " " + this.queries.length + " deleted queries";
/*     */     }
/* 120 */     if (this.bytesUsed != 0) {
/* 121 */       s = s + " bytesUsed=" + this.bytesUsed;
/*     */     }
/*     */ 
/* 124 */     return s;
/*     */   }
/*     */ 
/*     */   boolean any() {
/* 128 */     return (this.termCount > 0) || (this.queries.length > 0);
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.index.FrozenBufferedDeletes
 * JD-Core Version:    0.6.0
 */