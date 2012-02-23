/*     */ package org.apache.lucene.search.function;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import org.apache.lucene.index.IndexReader;
/*     */ import org.apache.lucene.search.FieldCache;
/*     */ import org.apache.lucene.search.FieldCache.IntParser;
/*     */ 
/*     */ public class IntFieldSource extends FieldCacheSource
/*     */ {
/*     */   private FieldCache.IntParser parser;
/*     */ 
/*     */   public IntFieldSource(String field)
/*     */   {
/*  51 */     this(field, null);
/*     */   }
/*     */ 
/*     */   public IntFieldSource(String field, FieldCache.IntParser parser)
/*     */   {
/*  58 */     super(field);
/*  59 */     this.parser = parser;
/*     */   }
/*     */ 
/*     */   public String description()
/*     */   {
/*  65 */     return "int(" + super.description() + ')';
/*     */   }
/*     */ 
/*     */   public DocValues getCachedFieldValues(FieldCache cache, String field, IndexReader reader)
/*     */     throws IOException
/*     */   {
/*  71 */     int[] arr = cache.getInts(reader, field, this.parser);
/*  72 */     return new DocValues(arr)
/*     */     {
/*     */       public float floatVal(int doc)
/*     */       {
/*  76 */         return this.val$arr[doc];
/*     */       }
/*     */ 
/*     */       public int intVal(int doc)
/*     */       {
/*  81 */         return this.val$arr[doc];
/*     */       }
/*     */ 
/*     */       public String toString(int doc)
/*     */       {
/*  86 */         return IntFieldSource.this.description() + '=' + intVal(doc);
/*     */       }
/*     */ 
/*     */       Object getInnerArray()
/*     */       {
/*  91 */         return this.val$arr;
/*     */       }
/*     */     };
/*     */   }
/*     */ 
/*     */   public boolean cachedFieldSourceEquals(FieldCacheSource o)
/*     */   {
/*  99 */     if (o.getClass() != IntFieldSource.class) {
/* 100 */       return false;
/*     */     }
/* 102 */     IntFieldSource other = (IntFieldSource)o;
/* 103 */     return other.parser == null;
/*     */   }
/*     */ 
/*     */   public int cachedFieldSourceHashCode()
/*     */   {
/* 111 */     return this.parser == null ? Integer.class.hashCode() : this.parser.getClass().hashCode();
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.search.function.IntFieldSource
 * JD-Core Version:    0.6.0
 */