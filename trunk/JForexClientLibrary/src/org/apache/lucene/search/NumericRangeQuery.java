/*     */ package org.apache.lucene.search;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.ObjectInputStream;
/*     */ import java.util.LinkedList;
/*     */ import org.apache.lucene.index.IndexReader;
/*     */ import org.apache.lucene.index.Term;
/*     */ import org.apache.lucene.index.TermEnum;
/*     */ import org.apache.lucene.util.NumericUtils;
/*     */ import org.apache.lucene.util.NumericUtils.IntRangeBuilder;
/*     */ import org.apache.lucene.util.NumericUtils.LongRangeBuilder;
/*     */ import org.apache.lucene.util.StringHelper;
/*     */ import org.apache.lucene.util.ToStringUtils;
/*     */ 
/*     */ public final class NumericRangeQuery<T extends Number> extends MultiTermQuery
/*     */ {
/*     */   String field;
/*     */   final int precisionStep;
/*     */   final int valSize;
/*     */   final T min;
/*     */   final T max;
/*     */   final boolean minInclusive;
/*     */   final boolean maxInclusive;
/*     */ 
/*     */   private NumericRangeQuery(String field, int precisionStep, int valSize, T min, T max, boolean minInclusive, boolean maxInclusive)
/*     */   {
/* 158 */     assert ((valSize == 32) || (valSize == 64));
/* 159 */     if (precisionStep < 1)
/* 160 */       throw new IllegalArgumentException("precisionStep must be >=1");
/* 161 */     this.field = StringHelper.intern(field);
/* 162 */     this.precisionStep = precisionStep;
/* 163 */     this.valSize = valSize;
/* 164 */     this.min = min;
/* 165 */     this.max = max;
/* 166 */     this.minInclusive = minInclusive;
/* 167 */     this.maxInclusive = maxInclusive;
/*     */ 
/* 173 */     switch (valSize) {
/*     */     case 64:
/* 175 */       setRewriteMethod(precisionStep > 6 ? CONSTANT_SCORE_FILTER_REWRITE : CONSTANT_SCORE_AUTO_REWRITE_DEFAULT);
/*     */ 
/* 179 */       break;
/*     */     case 32:
/* 181 */       setRewriteMethod(precisionStep > 8 ? CONSTANT_SCORE_FILTER_REWRITE : CONSTANT_SCORE_AUTO_REWRITE_DEFAULT);
/*     */ 
/* 185 */       break;
/*     */     default:
/* 188 */       throw new IllegalArgumentException("valSize must be 32 or 64");
/*     */     }
/*     */ 
/* 192 */     if ((min != null) && (min.equals(max)))
/* 193 */       setRewriteMethod(CONSTANT_SCORE_BOOLEAN_QUERY_REWRITE);
/*     */   }
/*     */ 
/*     */   public static NumericRangeQuery<Long> newLongRange(String field, int precisionStep, Long min, Long max, boolean minInclusive, boolean maxInclusive)
/*     */   {
/* 207 */     return new NumericRangeQuery(field, precisionStep, 64, min, max, minInclusive, maxInclusive);
/*     */   }
/*     */ 
/*     */   public static NumericRangeQuery<Long> newLongRange(String field, Long min, Long max, boolean minInclusive, boolean maxInclusive)
/*     */   {
/* 220 */     return new NumericRangeQuery(field, 4, 64, min, max, minInclusive, maxInclusive);
/*     */   }
/*     */ 
/*     */   public static NumericRangeQuery<Integer> newIntRange(String field, int precisionStep, Integer min, Integer max, boolean minInclusive, boolean maxInclusive)
/*     */   {
/* 233 */     return new NumericRangeQuery(field, precisionStep, 32, min, max, minInclusive, maxInclusive);
/*     */   }
/*     */ 
/*     */   public static NumericRangeQuery<Integer> newIntRange(String field, Integer min, Integer max, boolean minInclusive, boolean maxInclusive)
/*     */   {
/* 246 */     return new NumericRangeQuery(field, 4, 32, min, max, minInclusive, maxInclusive);
/*     */   }
/*     */ 
/*     */   public static NumericRangeQuery<Double> newDoubleRange(String field, int precisionStep, Double min, Double max, boolean minInclusive, boolean maxInclusive)
/*     */   {
/* 259 */     return new NumericRangeQuery(field, precisionStep, 64, min, max, minInclusive, maxInclusive);
/*     */   }
/*     */ 
/*     */   public static NumericRangeQuery<Double> newDoubleRange(String field, Double min, Double max, boolean minInclusive, boolean maxInclusive)
/*     */   {
/* 272 */     return new NumericRangeQuery(field, 4, 64, min, max, minInclusive, maxInclusive);
/*     */   }
/*     */ 
/*     */   public static NumericRangeQuery<Float> newFloatRange(String field, int precisionStep, Float min, Float max, boolean minInclusive, boolean maxInclusive)
/*     */   {
/* 285 */     return new NumericRangeQuery(field, precisionStep, 32, min, max, minInclusive, maxInclusive);
/*     */   }
/*     */ 
/*     */   public static NumericRangeQuery<Float> newFloatRange(String field, Float min, Float max, boolean minInclusive, boolean maxInclusive)
/*     */   {
/* 298 */     return new NumericRangeQuery(field, 4, 32, min, max, minInclusive, maxInclusive);
/*     */   }
/*     */ 
/*     */   protected FilteredTermEnum getEnum(IndexReader reader) throws IOException
/*     */   {
/* 303 */     return new NumericRangeTermEnum(reader);
/*     */   }
/*     */ 
/*     */   public String getField() {
/* 307 */     return this.field;
/*     */   }
/*     */   public boolean includesMin() {
/* 310 */     return this.minInclusive;
/*     */   }
/*     */   public boolean includesMax() {
/* 313 */     return this.maxInclusive;
/*     */   }
/*     */   public T getMin() {
/* 316 */     return this.min;
/*     */   }
/*     */   public T getMax() {
/* 319 */     return this.max;
/*     */   }
/*     */   public int getPrecisionStep() {
/* 322 */     return this.precisionStep;
/*     */   }
/*     */ 
/*     */   public String toString(String field) {
/* 326 */     StringBuilder sb = new StringBuilder();
/* 327 */     if (!this.field.equals(field)) sb.append(this.field).append(':');
/* 328 */     return (this.minInclusive ? '[' : '{') + (this.min == null ? "*" : this.min.toString()) + " TO " + (this.max == null ? "*" : this.max.toString()) + (this.maxInclusive ? ']' : '}') + ToStringUtils.boost(getBoost());
/*     */   }
/*     */ 
/*     */   public final boolean equals(Object o)
/*     */   {
/* 339 */     if (o == this) return true;
/* 340 */     if (!super.equals(o))
/* 341 */       return false;
/* 342 */     if ((o instanceof NumericRangeQuery)) {
/* 343 */       NumericRangeQuery q = (NumericRangeQuery)o;
/* 344 */       return (this.field == q.field) && (q.min == null ? this.min == null : q.min.equals(this.min)) && (q.max == null ? this.max == null : q.max.equals(this.max)) && (this.minInclusive == q.minInclusive) && (this.maxInclusive == q.maxInclusive) && (this.precisionStep == q.precisionStep);
/*     */     }
/*     */ 
/* 353 */     return false;
/*     */   }
/*     */ 
/*     */   public final int hashCode()
/*     */   {
/* 358 */     int hash = super.hashCode();
/* 359 */     hash += (this.field.hashCode() ^ 1164311910 + this.precisionStep ^ 0x64365465);
/* 360 */     if (this.min != null) hash += (this.min.hashCode() ^ 0x14FA55FB);
/* 361 */     if (this.max != null) hash += (this.max.hashCode() ^ 0x733FA5FE);
/* 362 */     return hash + (Boolean.valueOf(this.minInclusive).hashCode() ^ 0x14FA55FB) + (Boolean.valueOf(this.maxInclusive).hashCode() ^ 0x733FA5FE);
/*     */   }
/*     */ 
/*     */   private void readObject(ObjectInputStream in)
/*     */     throws IOException, ClassNotFoundException
/*     */   {
/* 369 */     in.defaultReadObject();
/* 370 */     this.field = StringHelper.intern(this.field);
/*     */   }
/*     */ 
/*     */   private final class NumericRangeTermEnum extends FilteredTermEnum
/*     */   {
/*     */     private final IndexReader reader;
/* 392 */     private final LinkedList<String> rangeBounds = new LinkedList();
/* 393 */     private final Term termTemplate = new Term(NumericRangeQuery.this.field);
/* 394 */     private String currentUpperBound = null;
/*     */ 
/*     */     NumericRangeTermEnum(IndexReader reader) throws IOException {
/* 397 */       this.reader = reader;
/*     */       long minBound;
/* 399 */       switch (NumericRangeQuery.this.valSize)
/*     */       {
/*     */       case 64:
/* 402 */         minBound = -9223372036854775808L;
/* 403 */         if ((NumericRangeQuery.this.min instanceof Long))
/* 404 */           minBound = NumericRangeQuery.this.min.longValue();
/* 405 */         else if ((NumericRangeQuery.this.min instanceof Double)) {
/* 406 */           minBound = NumericUtils.doubleToSortableLong(NumericRangeQuery.this.min.doubleValue());
/*     */         }
/* 408 */         if ((!NumericRangeQuery.this.minInclusive) && (NumericRangeQuery.this.min != null)) {
/* 409 */           if (minBound == 9223372036854775807L) break;
/* 410 */           minBound += 1L;
/*     */         }
/*     */         else
/*     */         {
/* 414 */           long maxBound = 9223372036854775807L;
/* 415 */           if ((NumericRangeQuery.this.max instanceof Long))
/* 416 */             maxBound = NumericRangeQuery.this.max.longValue();
/* 417 */           else if ((NumericRangeQuery.this.max instanceof Double)) {
/* 418 */             maxBound = NumericUtils.doubleToSortableLong(NumericRangeQuery.this.max.doubleValue());
/*     */           }
/* 420 */           if ((!NumericRangeQuery.this.maxInclusive) && (NumericRangeQuery.this.max != null)) {
/* 421 */             if (maxBound == -9223372036854775808L) break;
/* 422 */             maxBound -= 1L;
/*     */           }
/*     */           else {
/* 425 */             NumericUtils.splitLongRange(new NumericUtils.LongRangeBuilder(NumericRangeQuery.this)
/*     */             {
/*     */               public final void addRange(String minPrefixCoded, String maxPrefixCoded) {
/* 428 */                 NumericRangeQuery.NumericRangeTermEnum.this.rangeBounds.add(minPrefixCoded);
/* 429 */                 NumericRangeQuery.NumericRangeTermEnum.this.rangeBounds.add(maxPrefixCoded);
/*     */               }
/*     */             }
/*     */             , NumericRangeQuery.this.precisionStep, minBound, maxBound);
/*     */           }
/*     */ 
/*     */         }
/*     */ 
/* 432 */         break;
/*     */       case 32:
/* 437 */         int minBound = -2147483648;
/* 438 */         if ((NumericRangeQuery.this.min instanceof Integer))
/* 439 */           minBound = NumericRangeQuery.this.min.intValue();
/* 440 */         else if ((NumericRangeQuery.this.min instanceof Float)) {
/* 441 */           minBound = NumericUtils.floatToSortableInt(NumericRangeQuery.this.min.floatValue());
/*     */         }
/* 443 */         if ((!NumericRangeQuery.this.minInclusive) && (NumericRangeQuery.this.min != null)) {
/* 444 */           if (minBound == 2147483647) break;
/* 445 */           minBound++;
/*     */         }
/*     */         else
/*     */         {
/* 449 */           int maxBound = 2147483647;
/* 450 */           if ((NumericRangeQuery.this.max instanceof Integer))
/* 451 */             maxBound = NumericRangeQuery.this.max.intValue();
/* 452 */           else if ((NumericRangeQuery.this.max instanceof Float)) {
/* 453 */             maxBound = NumericUtils.floatToSortableInt(NumericRangeQuery.this.max.floatValue());
/*     */           }
/* 455 */           if ((!NumericRangeQuery.this.maxInclusive) && (NumericRangeQuery.this.max != null)) {
/* 456 */             if (maxBound == -2147483648) break;
/* 457 */             maxBound--;
/*     */           }
/*     */           else {
/* 460 */             NumericUtils.splitIntRange(new NumericUtils.IntRangeBuilder(NumericRangeQuery.this)
/*     */             {
/*     */               public final void addRange(String minPrefixCoded, String maxPrefixCoded) {
/* 463 */                 NumericRangeQuery.NumericRangeTermEnum.this.rangeBounds.add(minPrefixCoded);
/* 464 */                 NumericRangeQuery.NumericRangeTermEnum.this.rangeBounds.add(maxPrefixCoded);
/*     */               }
/*     */             }
/*     */             , NumericRangeQuery.this.precisionStep, minBound, maxBound);
/*     */           }
/*     */ 
/*     */         }
/*     */ 
/* 467 */         break;
/*     */       default:
/* 472 */         throw new IllegalArgumentException("valSize must be 32 or 64");
/*     */       }
/*     */ 
/* 476 */       next();
/*     */     }
/*     */ 
/*     */     public float difference()
/*     */     {
/* 481 */       return 1.0F;
/*     */     }
/*     */ 
/*     */     protected boolean endEnum()
/*     */     {
/* 487 */       throw new UnsupportedOperationException("not implemented");
/*     */     }
/*     */ 
/*     */     protected void setEnum(TermEnum tenum)
/*     */     {
/* 493 */       throw new UnsupportedOperationException("not implemented");
/*     */     }
/*     */ 
/*     */     protected boolean termCompare(Term term)
/*     */     {
/* 504 */       return (term.field() == NumericRangeQuery.this.field) && (term.text().compareTo(this.currentUpperBound) <= 0);
/*     */     }
/*     */ 
/*     */     public boolean next()
/*     */       throws IOException
/*     */     {
/* 512 */       if (this.currentTerm != null) {
/* 513 */         assert (this.actualEnum != null);
/* 514 */         if (this.actualEnum.next()) {
/* 515 */           this.currentTerm = this.actualEnum.term();
/* 516 */           if (termCompare(this.currentTerm)) {
/* 517 */             return true;
/*     */           }
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/* 523 */       this.currentTerm = null;
/* 524 */       while (this.rangeBounds.size() >= 2) {
/* 525 */         assert (this.rangeBounds.size() % 2 == 0);
/*     */ 
/* 527 */         if (this.actualEnum != null) {
/* 528 */           this.actualEnum.close();
/* 529 */           this.actualEnum = null;
/*     */         }
/* 531 */         String lowerBound = (String)this.rangeBounds.removeFirst();
/* 532 */         this.currentUpperBound = ((String)this.rangeBounds.removeFirst());
/*     */ 
/* 534 */         this.actualEnum = this.reader.terms(this.termTemplate.createTerm(lowerBound));
/* 535 */         this.currentTerm = this.actualEnum.term();
/* 536 */         if ((this.currentTerm != null) && (termCompare(this.currentTerm))) {
/* 537 */           return true;
/*     */         }
/* 539 */         this.currentTerm = null;
/*     */       }
/*     */ 
/* 543 */       assert ((this.rangeBounds.size() == 0) && (this.currentTerm == null));
/* 544 */       return false;
/*     */     }
/*     */ 
/*     */     public void close()
/*     */       throws IOException
/*     */     {
/* 550 */       this.rangeBounds.clear();
/* 551 */       this.currentUpperBound = null;
/* 552 */       super.close();
/*     */     }
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.search.NumericRangeQuery
 * JD-Core Version:    0.6.0
 */