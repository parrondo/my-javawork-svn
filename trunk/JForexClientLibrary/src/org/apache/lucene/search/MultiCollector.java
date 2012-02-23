/*     */ package org.apache.lucene.search;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import org.apache.lucene.index.IndexReader;
/*     */ 
/*     */ public class MultiCollector extends Collector
/*     */ {
/*     */   private final Collector[] collectors;
/*     */ 
/*     */   public static Collector wrap(Collector[] collectors)
/*     */   {
/*  54 */     int n = 0;
/*  55 */     for (Collector c : collectors) {
/*  56 */       if (c != null) {
/*  57 */         n++;
/*     */       }
/*     */     }
/*     */ 
/*  61 */     if (n == 0)
/*  62 */       throw new IllegalArgumentException("At least 1 collector must not be null");
/*  63 */     if (n == 1)
/*     */     {
/*  65 */       Collector col = null;
/*  66 */       for (Collector c : collectors) {
/*  67 */         if (c != null) {
/*  68 */           col = c;
/*  69 */           break;
/*     */         }
/*     */       }
/*  72 */       return col;
/*  73 */     }if (n == collectors.length) {
/*  74 */       return new MultiCollector(collectors);
/*     */     }
/*  76 */     Collector[] colls = new Collector[n];
/*  77 */     n = 0;
/*  78 */     for (Collector c : collectors) {
/*  79 */       if (c != null) {
/*  80 */         colls[(n++)] = c;
/*     */       }
/*     */     }
/*  83 */     return new MultiCollector(colls);
/*     */   }
/*     */ 
/*     */   private MultiCollector(Collector[] collectors)
/*     */   {
/*  90 */     this.collectors = collectors;
/*     */   }
/*     */ 
/*     */   public boolean acceptsDocsOutOfOrder()
/*     */   {
/*  95 */     for (Collector c : this.collectors) {
/*  96 */       if (!c.acceptsDocsOutOfOrder()) {
/*  97 */         return false;
/*     */       }
/*     */     }
/* 100 */     return true;
/*     */   }
/*     */ 
/*     */   public void collect(int doc) throws IOException
/*     */   {
/* 105 */     for (Collector c : this.collectors)
/* 106 */       c.collect(doc);
/*     */   }
/*     */ 
/*     */   public void setNextReader(IndexReader reader, int o)
/*     */     throws IOException
/*     */   {
/* 112 */     for (Collector c : this.collectors)
/* 113 */       c.setNextReader(reader, o);
/*     */   }
/*     */ 
/*     */   public void setScorer(Scorer s)
/*     */     throws IOException
/*     */   {
/* 119 */     for (Collector c : this.collectors)
/* 120 */       c.setScorer(s);
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.search.MultiCollector
 * JD-Core Version:    0.6.0
 */