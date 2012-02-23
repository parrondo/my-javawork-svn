/*     */ package org.apache.lucene.search;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import org.apache.lucene.index.IndexReader;
/*     */ import org.apache.lucene.util.FixedBitSet;
/*     */ 
/*     */ public class FieldCacheTermsFilter extends Filter
/*     */ {
/*     */   private String field;
/*     */   private String[] terms;
/*     */ 
/*     */   public FieldCacheTermsFilter(String field, String[] terms)
/*     */   {
/* 101 */     this.field = field;
/* 102 */     this.terms = terms;
/*     */   }
/*     */ 
/*     */   public FieldCache getFieldCache() {
/* 106 */     return FieldCache.DEFAULT;
/*     */   }
/*     */ 
/*     */   public DocIdSet getDocIdSet(IndexReader reader) throws IOException
/*     */   {
/* 111 */     return new FieldCacheTermsFilterDocIdSet(getFieldCache().getStringIndex(reader, this.field));
/*     */   }
/*     */   protected class FieldCacheTermsFilterDocIdSet extends DocIdSet {
/*     */     private FieldCache.StringIndex fcsi;
/*     */     private FixedBitSet bits;
/*     */ 
/*     */     public FieldCacheTermsFilterDocIdSet(FieldCache.StringIndex fcsi) {
/* 120 */       this.fcsi = fcsi;
/* 121 */       this.bits = new FixedBitSet(this.fcsi.lookup.length);
/* 122 */       for (int i = 0; i < FieldCacheTermsFilter.this.terms.length; i++) {
/* 123 */         int termNumber = this.fcsi.binarySearchLookup(FieldCacheTermsFilter.this.terms[i]);
/* 124 */         if (termNumber > 0)
/* 125 */           this.bits.set(termNumber);
/*     */       }
/*     */     }
/*     */ 
/*     */     public DocIdSetIterator iterator()
/*     */     {
/* 132 */       return new FieldCacheTermsFilterDocIdSetIterator();
/*     */     }
/*     */ 
/*     */     public boolean isCacheable()
/*     */     {
/* 138 */       return true;
/*     */     }
/*     */ 
/*     */     protected class FieldCacheTermsFilterDocIdSetIterator extends DocIdSetIterator {
/* 142 */       private int doc = -1;
/*     */ 
/*     */       protected FieldCacheTermsFilterDocIdSetIterator() {
/*     */       }
/* 146 */       public int docID() { return this.doc; } 
/*     */       public int nextDoc()
/*     */       {
/*     */         try {
/* 152 */           while (!FieldCacheTermsFilter.FieldCacheTermsFilterDocIdSet.this.bits.get(FieldCacheTermsFilter.FieldCacheTermsFilterDocIdSet.this.fcsi.order[(++this.doc)]));
/*     */         }
/*     */         catch (ArrayIndexOutOfBoundsException e) {
/* 154 */           this.doc = 2147483647;
/*     */         }
/* 156 */         return this.doc;
/*     */       }
/*     */ 
/*     */       public int advance(int target)
/*     */       {
/*     */         try {
/* 162 */           this.doc = target;
/* 163 */           while (!FieldCacheTermsFilter.FieldCacheTermsFilterDocIdSet.this.bits.get(FieldCacheTermsFilter.FieldCacheTermsFilterDocIdSet.this.fcsi.order[this.doc]))
/* 164 */             this.doc += 1;
/*     */         }
/*     */         catch (ArrayIndexOutOfBoundsException e) {
/* 167 */           this.doc = 2147483647;
/*     */         }
/* 169 */         return this.doc;
/*     */       }
/*     */     }
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.search.FieldCacheTermsFilter
 * JD-Core Version:    0.6.0
 */