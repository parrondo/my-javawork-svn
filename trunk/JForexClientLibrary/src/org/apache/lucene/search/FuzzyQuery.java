/*     */ package org.apache.lucene.search;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import org.apache.lucene.index.IndexReader;
/*     */ import org.apache.lucene.index.Term;
/*     */ import org.apache.lucene.util.ToStringUtils;
/*     */ 
/*     */ public class FuzzyQuery extends MultiTermQuery
/*     */ {
/*     */   public static final float defaultMinSimilarity = 0.5F;
/*     */   public static final int defaultPrefixLength = 0;
/*     */   public static final int defaultMaxExpansions = 2147483647;
/*     */   private float minimumSimilarity;
/*     */   private int prefixLength;
/*  46 */   private boolean termLongEnough = false;
/*     */   protected Term term;
/*     */ 
/*     */   public FuzzyQuery(Term term, float minimumSimilarity, int prefixLength, int maxExpansions)
/*     */   {
/*  71 */     this.term = term;
/*     */ 
/*  73 */     if (minimumSimilarity >= 1.0F)
/*  74 */       throw new IllegalArgumentException("minimumSimilarity >= 1");
/*  75 */     if (minimumSimilarity < 0.0F)
/*  76 */       throw new IllegalArgumentException("minimumSimilarity < 0");
/*  77 */     if (prefixLength < 0)
/*  78 */       throw new IllegalArgumentException("prefixLength < 0");
/*  79 */     if (maxExpansions < 0) {
/*  80 */       throw new IllegalArgumentException("maxExpansions < 0");
/*     */     }
/*  82 */     setRewriteMethod(new MultiTermQuery.TopTermsScoringBooleanQueryRewrite(maxExpansions));
/*     */ 
/*  84 */     if (term.text().length() > 1.0F / (1.0F - minimumSimilarity)) {
/*  85 */       this.termLongEnough = true;
/*     */     }
/*     */ 
/*  88 */     this.minimumSimilarity = minimumSimilarity;
/*  89 */     this.prefixLength = prefixLength;
/*     */   }
/*     */ 
/*     */   public FuzzyQuery(Term term, float minimumSimilarity, int prefixLength)
/*     */   {
/*  96 */     this(term, minimumSimilarity, prefixLength, 2147483647);
/*     */   }
/*     */ 
/*     */   public FuzzyQuery(Term term, float minimumSimilarity)
/*     */   {
/* 103 */     this(term, minimumSimilarity, 0, 2147483647);
/*     */   }
/*     */ 
/*     */   public FuzzyQuery(Term term)
/*     */   {
/* 110 */     this(term, 0.5F, 0, 2147483647);
/*     */   }
/*     */ 
/*     */   public float getMinSimilarity()
/*     */   {
/* 118 */     return this.minimumSimilarity;
/*     */   }
/*     */ 
/*     */   public int getPrefixLength()
/*     */   {
/* 127 */     return this.prefixLength;
/*     */   }
/*     */ 
/*     */   protected FilteredTermEnum getEnum(IndexReader reader) throws IOException
/*     */   {
/* 132 */     if (!this.termLongEnough) {
/* 133 */       return new SingleTermEnum(reader, this.term);
/*     */     }
/* 135 */     return new FuzzyTermEnum(reader, getTerm(), this.minimumSimilarity, this.prefixLength);
/*     */   }
/*     */ 
/*     */   public Term getTerm()
/*     */   {
/* 142 */     return this.term;
/*     */   }
/*     */ 
/*     */   public String toString(String field)
/*     */   {
/* 147 */     StringBuilder buffer = new StringBuilder();
/* 148 */     if (!this.term.field().equals(field)) {
/* 149 */       buffer.append(this.term.field());
/* 150 */       buffer.append(":");
/*     */     }
/* 152 */     buffer.append(this.term.text());
/* 153 */     buffer.append('~');
/* 154 */     buffer.append(Float.toString(this.minimumSimilarity));
/* 155 */     buffer.append(ToStringUtils.boost(getBoost()));
/* 156 */     return buffer.toString();
/*     */   }
/*     */ 
/*     */   public int hashCode()
/*     */   {
/* 161 */     int prime = 31;
/* 162 */     int result = super.hashCode();
/* 163 */     result = 31 * result + Float.floatToIntBits(this.minimumSimilarity);
/* 164 */     result = 31 * result + this.prefixLength;
/* 165 */     result = 31 * result + (this.term == null ? 0 : this.term.hashCode());
/* 166 */     return result;
/*     */   }
/*     */ 
/*     */   public boolean equals(Object obj)
/*     */   {
/* 171 */     if (this == obj)
/* 172 */       return true;
/* 173 */     if (!super.equals(obj))
/* 174 */       return false;
/* 175 */     if (getClass() != obj.getClass())
/* 176 */       return false;
/* 177 */     FuzzyQuery other = (FuzzyQuery)obj;
/* 178 */     if (Float.floatToIntBits(this.minimumSimilarity) != Float.floatToIntBits(other.minimumSimilarity))
/*     */     {
/* 180 */       return false;
/* 181 */     }if (this.prefixLength != other.prefixLength)
/* 182 */       return false;
/* 183 */     if (this.term == null) {
/* 184 */       if (other.term != null)
/* 185 */         return false;
/* 186 */     } else if (!this.term.equals(other.term))
/* 187 */       return false;
/* 188 */     return true;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.search.FuzzyQuery
 * JD-Core Version:    0.6.0
 */