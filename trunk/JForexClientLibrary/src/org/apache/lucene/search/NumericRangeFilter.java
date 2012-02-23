/*     */ package org.apache.lucene.search;
/*     */ 
/*     */ public final class NumericRangeFilter<T extends Number> extends MultiTermQueryWrapperFilter<NumericRangeQuery<T>>
/*     */ {
/*     */   private NumericRangeFilter(NumericRangeQuery<T> query)
/*     */   {
/*  47 */     super(query);
/*     */   }
/*     */ 
/*     */   public static NumericRangeFilter<Long> newLongRange(String field, int precisionStep, Long min, Long max, boolean minInclusive, boolean maxInclusive)
/*     */   {
/*  60 */     return new NumericRangeFilter(NumericRangeQuery.newLongRange(field, precisionStep, min, max, minInclusive, maxInclusive));
/*     */   }
/*     */ 
/*     */   public static NumericRangeFilter<Long> newLongRange(String field, Long min, Long max, boolean minInclusive, boolean maxInclusive)
/*     */   {
/*  75 */     return new NumericRangeFilter(NumericRangeQuery.newLongRange(field, min, max, minInclusive, maxInclusive));
/*     */   }
/*     */ 
/*     */   public static NumericRangeFilter<Integer> newIntRange(String field, int precisionStep, Integer min, Integer max, boolean minInclusive, boolean maxInclusive)
/*     */   {
/*  90 */     return new NumericRangeFilter(NumericRangeQuery.newIntRange(field, precisionStep, min, max, minInclusive, maxInclusive));
/*     */   }
/*     */ 
/*     */   public static NumericRangeFilter<Integer> newIntRange(String field, Integer min, Integer max, boolean minInclusive, boolean maxInclusive)
/*     */   {
/* 105 */     return new NumericRangeFilter(NumericRangeQuery.newIntRange(field, min, max, minInclusive, maxInclusive));
/*     */   }
/*     */ 
/*     */   public static NumericRangeFilter<Double> newDoubleRange(String field, int precisionStep, Double min, Double max, boolean minInclusive, boolean maxInclusive)
/*     */   {
/* 120 */     return new NumericRangeFilter(NumericRangeQuery.newDoubleRange(field, precisionStep, min, max, minInclusive, maxInclusive));
/*     */   }
/*     */ 
/*     */   public static NumericRangeFilter<Double> newDoubleRange(String field, Double min, Double max, boolean minInclusive, boolean maxInclusive)
/*     */   {
/* 135 */     return new NumericRangeFilter(NumericRangeQuery.newDoubleRange(field, min, max, minInclusive, maxInclusive));
/*     */   }
/*     */ 
/*     */   public static NumericRangeFilter<Float> newFloatRange(String field, int precisionStep, Float min, Float max, boolean minInclusive, boolean maxInclusive)
/*     */   {
/* 150 */     return new NumericRangeFilter(NumericRangeQuery.newFloatRange(field, precisionStep, min, max, minInclusive, maxInclusive));
/*     */   }
/*     */ 
/*     */   public static NumericRangeFilter<Float> newFloatRange(String field, Float min, Float max, boolean minInclusive, boolean maxInclusive)
/*     */   {
/* 165 */     return new NumericRangeFilter(NumericRangeQuery.newFloatRange(field, min, max, minInclusive, maxInclusive));
/*     */   }
/*     */ 
/*     */   public String getField()
/*     */   {
/* 171 */     return ((NumericRangeQuery)this.query).getField();
/*     */   }
/*     */   public boolean includesMin() {
/* 174 */     return ((NumericRangeQuery)this.query).includesMin();
/*     */   }
/*     */   public boolean includesMax() {
/* 177 */     return ((NumericRangeQuery)this.query).includesMax();
/*     */   }
/*     */   public T getMin() {
/* 180 */     return ((NumericRangeQuery)this.query).getMin();
/*     */   }
/*     */   public T getMax() {
/* 183 */     return ((NumericRangeQuery)this.query).getMax();
/*     */   }
/*     */   public int getPrecisionStep() {
/* 186 */     return ((NumericRangeQuery)this.query).getPrecisionStep();
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.search.NumericRangeFilter
 * JD-Core Version:    0.6.0
 */