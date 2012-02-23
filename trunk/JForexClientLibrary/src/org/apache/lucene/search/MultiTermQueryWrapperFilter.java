/*     */ package org.apache.lucene.search;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import org.apache.lucene.index.IndexReader;
/*     */ import org.apache.lucene.index.Term;
/*     */ import org.apache.lucene.index.TermDocs;
/*     */ import org.apache.lucene.index.TermEnum;
/*     */ import org.apache.lucene.util.FixedBitSet;
/*     */ 
/*     */ public class MultiTermQueryWrapperFilter<Q extends MultiTermQuery> extends Filter
/*     */ {
/*     */   protected final Q query;
/*     */ 
/*     */   protected MultiTermQueryWrapperFilter(Q query)
/*     */   {
/*  50 */     this.query = query;
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/*  56 */     return this.query.toString();
/*     */   }
/*     */ 
/*     */   public final boolean equals(Object o)
/*     */   {
/*  61 */     if (o == this) return true;
/*  62 */     if (o == null) return false;
/*  63 */     if (getClass().equals(o.getClass())) {
/*  64 */       return this.query.equals(((MultiTermQueryWrapperFilter)o).query);
/*     */     }
/*  66 */     return false;
/*     */   }
/*     */ 
/*     */   public final int hashCode()
/*     */   {
/*  71 */     return this.query.hashCode();
/*     */   }
/*     */ 
/*     */   public int getTotalNumberOfTerms()
/*     */   {
/*  85 */     return this.query.getTotalNumberOfTerms();
/*     */   }
/*     */ 
/*     */   public void clearTotalNumberOfTerms()
/*     */   {
/*  94 */     this.query.clearTotalNumberOfTerms();
/*     */   }
/*     */ 
/*     */   public DocIdSet getDocIdSet(IndexReader reader)
/*     */     throws IOException
/*     */   {
/* 103 */     TermEnum enumerator = this.query.getEnum(reader);
/*     */     try
/*     */     {
/*     */       DocIdSet localDocIdSet;
/* 106 */       if (enumerator.term() == null) {
/* 107 */         localDocIdSet = DocIdSet.EMPTY_DOCIDSET; jsr 168;
/*     */       }
/* 109 */       FixedBitSet bitSet = new FixedBitSet(reader.maxDoc());
/* 110 */       int[] docs = new int[32];
/* 111 */       int[] freqs = new int[32];
/* 112 */       TermDocs termDocs = reader.termDocs();
/*     */       try {
/* 114 */         termCount = 0;
/*     */         do {
/* 116 */           Term term = enumerator.term();
/* 117 */           if (term == null)
/*     */             break;
/* 119 */           termCount++;
/* 120 */           termDocs.seek(term);
/*     */           while (true) {
/* 122 */             int count = termDocs.read(docs, freqs);
/* 123 */             if (count == 0) break;
/* 124 */             for (int i = 0; i < count; i++) {
/* 125 */               bitSet.set(docs[i]);
/*     */             }
/*     */           }
/*     */ 
/*     */         }
/*     */ 
/* 131 */         while (enumerator.next());
/*     */ 
/* 133 */         this.query.incTotalNumberOfTerms(termCount);
/*     */       }
/*     */       finally {
/* 136 */         termDocs.close();
/*     */       }
/* 138 */       termCount = bitSet;
/*     */     }
/*     */     finally
/*     */     {
/*     */       int termCount;
/* 140 */       enumerator.close();
/*     */     }
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.search.MultiTermQueryWrapperFilter
 * JD-Core Version:    0.6.0
 */