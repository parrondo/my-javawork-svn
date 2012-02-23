/*     */ package org.apache.lucene.search.function;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import org.apache.lucene.index.IndexReader;
/*     */ import org.apache.lucene.search.Explanation;
/*     */ 
/*     */ public class CustomScoreProvider
/*     */ {
/*     */   protected final IndexReader reader;
/*     */ 
/*     */   public CustomScoreProvider(IndexReader reader)
/*     */   {
/*  46 */     this.reader = reader;
/*     */   }
/*     */ 
/*     */   public float customScore(int doc, float subQueryScore, float[] valSrcScores)
/*     */     throws IOException
/*     */   {
/*  73 */     if (valSrcScores.length == 1) {
/*  74 */       return customScore(doc, subQueryScore, valSrcScores[0]);
/*     */     }
/*  76 */     if (valSrcScores.length == 0) {
/*  77 */       return customScore(doc, subQueryScore, 1.0F);
/*     */     }
/*  79 */     float score = subQueryScore;
/*  80 */     for (int i = 0; i < valSrcScores.length; i++) {
/*  81 */       score *= valSrcScores[i];
/*     */     }
/*  83 */     return score;
/*     */   }
/*     */ 
/*     */   public float customScore(int doc, float subQueryScore, float valSrcScore)
/*     */     throws IOException
/*     */   {
/* 107 */     return subQueryScore * valSrcScore;
/*     */   }
/*     */ 
/*     */   public Explanation customExplain(int doc, Explanation subQueryExpl, Explanation[] valSrcExpls)
/*     */     throws IOException
/*     */   {
/* 122 */     if (valSrcExpls.length == 1) {
/* 123 */       return customExplain(doc, subQueryExpl, valSrcExpls[0]);
/*     */     }
/* 125 */     if (valSrcExpls.length == 0) {
/* 126 */       return subQueryExpl;
/*     */     }
/* 128 */     float valSrcScore = 1.0F;
/* 129 */     for (int i = 0; i < valSrcExpls.length; i++) {
/* 130 */       valSrcScore *= valSrcExpls[i].getValue();
/*     */     }
/* 132 */     Explanation exp = new Explanation(valSrcScore * subQueryExpl.getValue(), "custom score: product of:");
/* 133 */     exp.addDetail(subQueryExpl);
/* 134 */     for (int i = 0; i < valSrcExpls.length; i++) {
/* 135 */       exp.addDetail(valSrcExpls[i]);
/*     */     }
/* 137 */     return exp;
/*     */   }
/*     */ 
/*     */   public Explanation customExplain(int doc, Explanation subQueryExpl, Explanation valSrcExpl)
/*     */     throws IOException
/*     */   {
/* 152 */     float valSrcScore = 1.0F;
/* 153 */     if (valSrcExpl != null) {
/* 154 */       valSrcScore *= valSrcExpl.getValue();
/*     */     }
/* 156 */     Explanation exp = new Explanation(valSrcScore * subQueryExpl.getValue(), "custom score: product of:");
/* 157 */     exp.addDetail(subQueryExpl);
/* 158 */     exp.addDetail(valSrcExpl);
/* 159 */     return exp;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.search.function.CustomScoreProvider
 * JD-Core Version:    0.6.0
 */