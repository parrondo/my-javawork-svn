/*     */ package org.apache.lucene.search.function;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import org.apache.lucene.index.IndexReader;
/*     */ import org.apache.lucene.search.FieldCache;
/*     */ import org.apache.lucene.search.FieldCache.StringIndex;
/*     */ 
/*     */ public class OrdFieldSource extends ValueSource
/*     */ {
/*     */   protected String field;
/* 108 */   private static final int hcode = OrdFieldSource.class.hashCode();
/*     */ 
/*     */   public OrdFieldSource(String field)
/*     */   {
/*  60 */     this.field = field;
/*     */   }
/*     */ 
/*     */   public String description()
/*     */   {
/*  66 */     return "ord(" + this.field + ')';
/*     */   }
/*     */ 
/*     */   public DocValues getValues(IndexReader reader)
/*     */     throws IOException
/*     */   {
/*  72 */     int[] arr = FieldCache.DEFAULT.getStringIndex(reader, this.field).order;
/*  73 */     return new DocValues(arr)
/*     */     {
/*     */       public float floatVal(int doc)
/*     */       {
/*  77 */         return this.val$arr[doc];
/*     */       }
/*     */ 
/*     */       public String strVal(int doc)
/*     */       {
/*  83 */         return Integer.toString(this.val$arr[doc]);
/*     */       }
/*     */ 
/*     */       public String toString(int doc)
/*     */       {
/*  88 */         return OrdFieldSource.this.description() + '=' + intVal(doc);
/*     */       }
/*     */ 
/*     */       Object getInnerArray()
/*     */       {
/*  93 */         return this.val$arr;
/*     */       }
/*     */     };
/*     */   }
/*     */ 
/*     */   public boolean equals(Object o)
/*     */   {
/* 101 */     if (o == this) return true;
/* 102 */     if (o == null) return false;
/* 103 */     if (o.getClass() != OrdFieldSource.class) return false;
/* 104 */     OrdFieldSource other = (OrdFieldSource)o;
/* 105 */     return this.field.equals(other.field);
/*     */   }
/*     */ 
/*     */   public int hashCode()
/*     */   {
/* 113 */     return hcode + this.field.hashCode();
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.search.function.OrdFieldSource
 * JD-Core Version:    0.6.0
 */