/*     */ package org.apache.lucene.search;
/*     */ 
/*     */ import java.text.Collator;
/*     */ 
/*     */ public class TermRangeFilter extends MultiTermQueryWrapperFilter<TermRangeQuery>
/*     */ {
/*     */   public TermRangeFilter(String fieldName, String lowerTerm, String upperTerm, boolean includeLower, boolean includeUpper)
/*     */   {
/*  49 */     super(new TermRangeQuery(fieldName, lowerTerm, upperTerm, includeLower, includeUpper));
/*     */   }
/*     */ 
/*     */   public TermRangeFilter(String fieldName, String lowerTerm, String upperTerm, boolean includeLower, boolean includeUpper, Collator collator)
/*     */   {
/*  72 */     super(new TermRangeQuery(fieldName, lowerTerm, upperTerm, includeLower, includeUpper, collator));
/*     */   }
/*     */ 
/*     */   public static TermRangeFilter Less(String fieldName, String upperTerm)
/*     */   {
/*  80 */     return new TermRangeFilter(fieldName, null, upperTerm, false, true);
/*     */   }
/*     */ 
/*     */   public static TermRangeFilter More(String fieldName, String lowerTerm)
/*     */   {
/*  88 */     return new TermRangeFilter(fieldName, lowerTerm, null, true, false);
/*     */   }
/*     */ 
/*     */   public String getField() {
/*  92 */     return ((TermRangeQuery)this.query).getField();
/*     */   }
/*     */   public String getLowerTerm() {
/*  95 */     return ((TermRangeQuery)this.query).getLowerTerm();
/*     */   }
/*     */   public String getUpperTerm() {
/*  98 */     return ((TermRangeQuery)this.query).getUpperTerm();
/*     */   }
/*     */   public boolean includesLower() {
/* 101 */     return ((TermRangeQuery)this.query).includesLower();
/*     */   }
/*     */   public boolean includesUpper() {
/* 104 */     return ((TermRangeQuery)this.query).includesUpper();
/*     */   }
/*     */   public Collator getCollator() {
/* 107 */     return ((TermRangeQuery)this.query).getCollator();
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.search.TermRangeFilter
 * JD-Core Version:    0.6.0
 */