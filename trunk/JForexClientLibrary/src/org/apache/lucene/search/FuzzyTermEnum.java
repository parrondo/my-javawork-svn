/*     */ package org.apache.lucene.search;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import org.apache.lucene.index.IndexReader;
/*     */ import org.apache.lucene.index.Term;
/*     */ 
/*     */ public final class FuzzyTermEnum extends FilteredTermEnum
/*     */ {
/*     */   private int[] p;
/*     */   private int[] d;
/*     */   private float similarity;
/*  40 */   private boolean endEnum = false;
/*     */ 
/*  42 */   private Term searchTerm = null;
/*     */   private final String field;
/*     */   private final char[] text;
/*     */   private final String prefix;
/*     */   private final float minimumSimilarity;
/*     */   private final float scale_factor;
/*     */ 
/*     */   public FuzzyTermEnum(IndexReader reader, Term term)
/*     */     throws IOException
/*     */   {
/*  62 */     this(reader, term, 0.5F, 0);
/*     */   }
/*     */ 
/*     */   public FuzzyTermEnum(IndexReader reader, Term term, float minSimilarity)
/*     */     throws IOException
/*     */   {
/*  78 */     this(reader, term, minSimilarity, 0);
/*     */   }
/*     */ 
/*     */   public FuzzyTermEnum(IndexReader reader, Term term, float minSimilarity, int prefixLength)
/*     */     throws IOException
/*     */   {
/*  98 */     if (minSimilarity >= 1.0F)
/*  99 */       throw new IllegalArgumentException("minimumSimilarity cannot be greater than or equal to 1");
/* 100 */     if (minSimilarity < 0.0F)
/* 101 */       throw new IllegalArgumentException("minimumSimilarity cannot be less than 0");
/* 102 */     if (prefixLength < 0) {
/* 103 */       throw new IllegalArgumentException("prefixLength cannot be less than 0");
/*     */     }
/* 105 */     this.minimumSimilarity = minSimilarity;
/* 106 */     this.scale_factor = (1.0F / (1.0F - this.minimumSimilarity));
/* 107 */     this.searchTerm = term;
/* 108 */     this.field = this.searchTerm.field();
/*     */ 
/* 112 */     int fullSearchTermLength = this.searchTerm.text().length();
/* 113 */     int realPrefixLength = prefixLength > fullSearchTermLength ? fullSearchTermLength : prefixLength;
/*     */ 
/* 115 */     this.text = this.searchTerm.text().substring(realPrefixLength).toCharArray();
/* 116 */     this.prefix = this.searchTerm.text().substring(0, realPrefixLength);
/*     */ 
/* 118 */     this.p = new int[this.text.length + 1];
/* 119 */     this.d = new int[this.text.length + 1];
/*     */ 
/* 121 */     setEnum(reader.terms(new Term(this.searchTerm.field(), this.prefix)));
/*     */   }
/*     */ 
/*     */   protected final boolean termCompare(Term term)
/*     */   {
/* 130 */     if ((this.field == term.field()) && (term.text().startsWith(this.prefix))) {
/* 131 */       String target = term.text().substring(this.prefix.length());
/* 132 */       this.similarity = similarity(target);
/* 133 */       return this.similarity > this.minimumSimilarity;
/*     */     }
/* 135 */     this.endEnum = true;
/* 136 */     return false;
/*     */   }
/*     */ 
/*     */   public final float difference()
/*     */   {
/* 142 */     return (this.similarity - this.minimumSimilarity) * this.scale_factor;
/*     */   }
/*     */ 
/*     */   public final boolean endEnum()
/*     */   {
/* 148 */     return this.endEnum;
/*     */   }
/*     */ 
/*     */   private float similarity(String target)
/*     */   {
/* 193 */     int m = target.length();
/* 194 */     int n = this.text.length;
/* 195 */     if (n == 0)
/*     */     {
/* 198 */       return this.prefix.length() == 0 ? 0.0F : 1.0F - m / this.prefix.length();
/*     */     }
/* 200 */     if (m == 0) {
/* 201 */       return this.prefix.length() == 0 ? 0.0F : 1.0F - n / this.prefix.length();
/*     */     }
/*     */ 
/* 204 */     int maxDistance = calculateMaxDistance(m);
/*     */ 
/* 206 */     if (maxDistance < Math.abs(m - n))
/*     */     {
/* 214 */       return 0.0F;
/*     */     }
/*     */ 
/* 218 */     for (int i = 0; i <= n; i++) {
/* 219 */       this.p[i] = i;
/*     */     }
/*     */ 
/* 223 */     for (int j = 1; j <= m; j++) {
/* 224 */       int bestPossibleEditDistance = m;
/* 225 */       char t_j = target.charAt(j - 1);
/* 226 */       this.d[0] = j;
/*     */ 
/* 228 */       for (int i = 1; i <= n; i++)
/*     */       {
/* 230 */         if (t_j != this.text[(i - 1)])
/* 231 */           this.d[i] = (Math.min(Math.min(this.d[(i - 1)], this.p[i]), this.p[(i - 1)]) + 1);
/*     */         else {
/* 233 */           this.d[i] = Math.min(Math.min(this.d[(i - 1)] + 1, this.p[i] + 1), this.p[(i - 1)]);
/*     */         }
/* 235 */         bestPossibleEditDistance = Math.min(bestPossibleEditDistance, this.d[i]);
/*     */       }
/*     */ 
/* 242 */       if ((j > maxDistance) && (bestPossibleEditDistance > maxDistance))
/*     */       {
/* 245 */         return 0.0F;
/*     */       }
/*     */ 
/* 249 */       int[] _d = this.p;
/* 250 */       this.p = this.d;
/* 251 */       this.d = _d;
/*     */     }
/*     */ 
/* 262 */     return 1.0F - this.p[n] / (this.prefix.length() + Math.min(n, m));
/*     */   }
/*     */ 
/*     */   private int calculateMaxDistance(int m)
/*     */   {
/* 273 */     return (int)((1.0F - this.minimumSimilarity) * (Math.min(this.text.length, m) + this.prefix.length()));
/*     */   }
/*     */ 
/*     */   public void close()
/*     */     throws IOException
/*     */   {
/* 279 */     this.p = (this.d = null);
/* 280 */     this.searchTerm = null;
/* 281 */     super.close();
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.search.FuzzyTermEnum
 * JD-Core Version:    0.6.0
 */