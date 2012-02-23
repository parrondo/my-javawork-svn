/*     */ package org.apache.lucene.search.function;
/*     */ 
/*     */ import org.apache.lucene.search.Explanation;
/*     */ 
/*     */ public abstract class DocValues
/*     */ {
/* 114 */   private float minVal = (0.0F / 0.0F);
/* 115 */   private float maxVal = (0.0F / 0.0F);
/* 116 */   private float avgVal = (0.0F / 0.0F);
/* 117 */   private boolean computed = false;
/*     */ 
/*     */   public abstract float floatVal(int paramInt);
/*     */ 
/*     */   public int intVal(int doc)
/*     */   {
/*  55 */     return (int)floatVal(doc);
/*     */   }
/*     */ 
/*     */   public long longVal(int doc)
/*     */   {
/*  64 */     return ()floatVal(doc);
/*     */   }
/*     */ 
/*     */   public double doubleVal(int doc)
/*     */   {
/*  73 */     return floatVal(doc);
/*     */   }
/*     */ 
/*     */   public String strVal(int doc)
/*     */   {
/*  82 */     return Float.toString(floatVal(doc));
/*     */   }
/*     */ 
/*     */   public abstract String toString(int paramInt);
/*     */ 
/*     */   public Explanation explain(int doc)
/*     */   {
/*  94 */     return new Explanation(floatVal(doc), toString(doc));
/*     */   }
/*     */ 
/*     */   Object getInnerArray()
/*     */   {
/* 110 */     throw new UnsupportedOperationException("this optional method is for test purposes only");
/*     */   }
/*     */ 
/*     */   private void compute()
/*     */   {
/* 120 */     if (this.computed) {
/* 121 */       return;
/*     */     }
/* 123 */     float sum = 0.0F;
/* 124 */     int n = 0;
/*     */     while (true) {
/*     */       float val;
/*     */       try { val = floatVal(n);
/*     */       } catch (ArrayIndexOutOfBoundsException e) {
/* 130 */         break;
/*     */       }
/* 132 */       sum += val;
/* 133 */       this.minVal = (Float.isNaN(this.minVal) ? val : Math.min(this.minVal, val));
/* 134 */       this.maxVal = (Float.isNaN(this.maxVal) ? val : Math.max(this.maxVal, val));
/* 135 */       n++;
/*     */     }
/*     */ 
/* 138 */     this.avgVal = (n == 0 ? (0.0F / 0.0F) : sum / n);
/* 139 */     this.computed = true;
/*     */   }
/*     */ 
/*     */   public float getMinValue()
/*     */   {
/* 153 */     compute();
/* 154 */     return this.minVal;
/*     */   }
/*     */ 
/*     */   public float getMaxValue()
/*     */   {
/* 168 */     compute();
/* 169 */     return this.maxVal;
/*     */   }
/*     */ 
/*     */   public float getAverageValue()
/*     */   {
/* 183 */     compute();
/* 184 */     return this.avgVal;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.search.function.DocValues
 * JD-Core Version:    0.6.0
 */