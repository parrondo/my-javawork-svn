/*     */ package org.apache.lucene.util;
/*     */ 
/*     */ public enum Version
/*     */ {
/*  38 */   LUCENE_20, 
/*     */ 
/*  44 */   LUCENE_21, 
/*     */ 
/*  50 */   LUCENE_22, 
/*     */ 
/*  56 */   LUCENE_23, 
/*     */ 
/*  62 */   LUCENE_24, 
/*     */ 
/*  68 */   LUCENE_29, 
/*     */ 
/*  72 */   LUCENE_30, 
/*     */ 
/*  75 */   LUCENE_31, 
/*     */ 
/*  78 */   LUCENE_32, 
/*     */ 
/*  81 */   LUCENE_33, 
/*     */ 
/*  89 */   LUCENE_34, 
/*     */ 
/* 109 */   LUCENE_CURRENT;
/*     */ 
/*     */   public boolean onOrAfter(Version other)
/*     */   {
/* 113 */     return compareTo(other) >= 0;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.util.Version
 * JD-Core Version:    0.6.0
 */