/*     */ package org.apache.lucene.search.function;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import org.apache.lucene.index.IndexReader;
/*     */ import org.apache.lucene.search.FieldCache;
/*     */ import org.apache.lucene.search.FieldCache.StringIndex;
/*     */ 
/*     */ public class ReverseOrdFieldSource extends ValueSource
/*     */ {
/*     */   public String field;
/* 118 */   private static final int hcode = ReverseOrdFieldSource.class.hashCode();
/*     */ 
/*     */   public ReverseOrdFieldSource(String field)
/*     */   {
/*  61 */     this.field = field;
/*     */   }
/*     */ 
/*     */   public String description()
/*     */   {
/*  67 */     return "rord(" + this.field + ')';
/*     */   }
/*     */ 
/*     */   public DocValues getValues(IndexReader reader)
/*     */     throws IOException
/*     */   {
/*  73 */     FieldCache.StringIndex sindex = FieldCache.DEFAULT.getStringIndex(reader, this.field);
/*     */ 
/*  75 */     int[] arr = sindex.order;
/*  76 */     int end = sindex.lookup.length;
/*     */ 
/*  78 */     return new DocValues(end, arr)
/*     */     {
/*     */       public float floatVal(int doc)
/*     */       {
/*  82 */         return this.val$end - this.val$arr[doc];
/*     */       }
/*     */ 
/*     */       public int intVal(int doc)
/*     */       {
/*  87 */         return this.val$end - this.val$arr[doc];
/*     */       }
/*     */ 
/*     */       public String strVal(int doc)
/*     */       {
/*  93 */         return Integer.toString(intVal(doc));
/*     */       }
/*     */ 
/*     */       public String toString(int doc)
/*     */       {
/*  98 */         return ReverseOrdFieldSource.this.description() + '=' + strVal(doc);
/*     */       }
/*     */ 
/*     */       Object getInnerArray()
/*     */       {
/* 103 */         return this.val$arr;
/*     */       }
/*     */     };
/*     */   }
/*     */ 
/*     */   public boolean equals(Object o)
/*     */   {
/* 111 */     if (o == this) return true;
/* 112 */     if (o == null) return false;
/* 113 */     if (o.getClass() != ReverseOrdFieldSource.class) return false;
/* 114 */     ReverseOrdFieldSource other = (ReverseOrdFieldSource)o;
/* 115 */     return this.field.equals(other.field);
/*     */   }
/*     */ 
/*     */   public int hashCode()
/*     */   {
/* 123 */     return hcode + this.field.hashCode();
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.search.function.ReverseOrdFieldSource
 * JD-Core Version:    0.6.0
 */