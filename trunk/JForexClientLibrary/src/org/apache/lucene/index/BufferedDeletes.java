/*     */ package org.apache.lucene.index;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.concurrent.atomic.AtomicInteger;
/*     */ import java.util.concurrent.atomic.AtomicLong;
/*     */ import org.apache.lucene.search.Query;
/*     */ import org.apache.lucene.util.RamUsageEstimator;
/*     */ 
/*     */ class BufferedDeletes
/*     */ {
/*  50 */   static final int BYTES_PER_DEL_TERM = 8 * RamUsageEstimator.NUM_BYTES_OBJECT_REF + 40 + 24;
/*     */ 
/*  55 */   static final int BYTES_PER_DEL_DOCID = 2 * RamUsageEstimator.NUM_BYTES_OBJECT_REF + 8 + 4;
/*     */ 
/*  62 */   static final int BYTES_PER_DEL_QUERY = 5 * RamUsageEstimator.NUM_BYTES_OBJECT_REF + 16 + 8 + 24;
/*     */ 
/*  64 */   final AtomicInteger numTermDeletes = new AtomicInteger();
/*  65 */   final Map<Term, Integer> terms = new HashMap();
/*  66 */   final Map<Query, Integer> queries = new HashMap();
/*  67 */   final List<Integer> docIDs = new ArrayList();
/*     */ 
/*  69 */   public static final Integer MAX_INT = Integer.valueOf(2147483647);
/*     */ 
/*  71 */   final AtomicLong bytesUsed = new AtomicLong();
/*     */   private static final boolean VERBOSE_DELETES = false;
/*     */   long gen;
/*     */ 
/*     */   public String toString()
/*     */   {
/*  84 */     String s = "gen=" + this.gen;
/*  85 */     if (this.numTermDeletes.get() != 0) {
/*  86 */       s = s + " " + this.numTermDeletes.get() + " deleted terms (unique count=" + this.terms.size() + ")";
/*     */     }
/*  88 */     if (this.queries.size() != 0) {
/*  89 */       s = s + " " + this.queries.size() + " deleted queries";
/*     */     }
/*  91 */     if (this.docIDs.size() != 0) {
/*  92 */       s = s + " " + this.docIDs.size() + " deleted docIDs";
/*     */     }
/*  94 */     if (this.bytesUsed.get() != 0L) {
/*  95 */       s = s + " bytesUsed=" + this.bytesUsed.get();
/*     */     }
/*     */ 
/*  98 */     return s;
/*     */   }
/*     */ 
/*     */   public void addQuery(Query query, int docIDUpto)
/*     */   {
/* 103 */     Integer current = (Integer)this.queries.put(query, Integer.valueOf(docIDUpto));
/*     */ 
/* 105 */     if (current == null)
/* 106 */       this.bytesUsed.addAndGet(BYTES_PER_DEL_QUERY);
/*     */   }
/*     */ 
/*     */   public void addDocID(int docID)
/*     */   {
/* 111 */     this.docIDs.add(Integer.valueOf(docID));
/* 112 */     this.bytesUsed.addAndGet(BYTES_PER_DEL_DOCID);
/*     */   }
/*     */ 
/*     */   public void addTerm(Term term, int docIDUpto) {
/* 116 */     Integer current = (Integer)this.terms.get(term);
/* 117 */     if ((current != null) && (docIDUpto < current.intValue()))
/*     */     {
/* 125 */       return;
/*     */     }
/*     */ 
/* 128 */     this.terms.put(term, Integer.valueOf(docIDUpto));
/* 129 */     this.numTermDeletes.incrementAndGet();
/* 130 */     if (current == null)
/* 131 */       this.bytesUsed.addAndGet(BYTES_PER_DEL_TERM + term.text.length() * 2);
/*     */   }
/*     */ 
/*     */   void clear()
/*     */   {
/* 136 */     this.terms.clear();
/* 137 */     this.queries.clear();
/* 138 */     this.docIDs.clear();
/* 139 */     this.numTermDeletes.set(0);
/* 140 */     this.bytesUsed.set(0L);
/*     */   }
/*     */ 
/*     */   void clearDocIDs() {
/* 144 */     this.bytesUsed.addAndGet(-this.docIDs.size() * BYTES_PER_DEL_DOCID);
/* 145 */     this.docIDs.clear();
/*     */   }
/*     */ 
/*     */   boolean any() {
/* 149 */     return (this.terms.size() > 0) || (this.docIDs.size() > 0) || (this.queries.size() > 0);
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.index.BufferedDeletes
 * JD-Core Version:    0.6.0
 */