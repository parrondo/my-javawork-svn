/*     */ package org.apache.lucene.search;
/*     */ 
/*     */ import java.io.IOException;
/*     */ 
/*     */ class ReqExclScorer extends Scorer
/*     */ {
/*     */   private Scorer reqScorer;
/*     */   private DocIdSetIterator exclDisi;
/*  32 */   private int doc = -1;
/*     */ 
/*     */   public ReqExclScorer(Scorer reqScorer, DocIdSetIterator exclDisi)
/*     */   {
/*  39 */     super(reqScorer.weight);
/*  40 */     this.reqScorer = reqScorer;
/*  41 */     this.exclDisi = exclDisi;
/*     */   }
/*     */ 
/*     */   public int nextDoc() throws IOException
/*     */   {
/*  46 */     if (this.reqScorer == null) {
/*  47 */       return this.doc;
/*     */     }
/*  49 */     this.doc = this.reqScorer.nextDoc();
/*  50 */     if (this.doc == 2147483647) {
/*  51 */       this.reqScorer = null;
/*  52 */       return this.doc;
/*     */     }
/*  54 */     if (this.exclDisi == null) {
/*  55 */       return this.doc;
/*     */     }
/*  57 */     return this.doc = toNonExcluded();
/*     */   }
/*     */ 
/*     */   private int toNonExcluded()
/*     */     throws IOException
/*     */   {
/*  72 */     int exclDoc = this.exclDisi.docID();
/*  73 */     int reqDoc = this.reqScorer.docID();
/*     */     do {
/*  75 */       if (reqDoc < exclDoc)
/*  76 */         return reqDoc;
/*  77 */       if (reqDoc > exclDoc) {
/*  78 */         exclDoc = this.exclDisi.advance(reqDoc);
/*  79 */         if (exclDoc == 2147483647) {
/*  80 */           this.exclDisi = null;
/*  81 */           return reqDoc;
/*     */         }
/*  83 */         if (exclDoc > reqDoc)
/*  84 */           return reqDoc;
/*     */       }
/*     */     }
/*  87 */     while ((reqDoc = this.reqScorer.nextDoc()) != 2147483647);
/*  88 */     this.reqScorer = null;
/*  89 */     return 2147483647;
/*     */   }
/*     */ 
/*     */   public int docID()
/*     */   {
/*  94 */     return this.doc;
/*     */   }
/*     */ 
/*     */   public float score()
/*     */     throws IOException
/*     */   {
/* 103 */     return this.reqScorer.score();
/*     */   }
/*     */ 
/*     */   public int advance(int target) throws IOException
/*     */   {
/* 108 */     if (this.reqScorer == null) {
/* 109 */       return this.doc = 2147483647;
/*     */     }
/* 111 */     if (this.exclDisi == null) {
/* 112 */       return this.doc = this.reqScorer.advance(target);
/*     */     }
/* 114 */     if (this.reqScorer.advance(target) == 2147483647) {
/* 115 */       this.reqScorer = null;
/* 116 */       return this.doc = 2147483647;
/*     */     }
/* 118 */     return this.doc = toNonExcluded();
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.search.ReqExclScorer
 * JD-Core Version:    0.6.0
 */