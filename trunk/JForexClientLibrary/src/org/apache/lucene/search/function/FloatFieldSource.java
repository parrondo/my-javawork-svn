/*     */ package org.apache.lucene.search.function;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import org.apache.lucene.index.IndexReader;
/*     */ import org.apache.lucene.search.FieldCache;
/*     */ import org.apache.lucene.search.FieldCache.FloatParser;
/*     */ 
/*     */ public class FloatFieldSource extends FieldCacheSource
/*     */ {
/*     */   private FieldCache.FloatParser parser;
/*     */ 
/*     */   public FloatFieldSource(String field)
/*     */   {
/*  51 */     this(field, null);
/*     */   }
/*     */ 
/*     */   public FloatFieldSource(String field, FieldCache.FloatParser parser)
/*     */   {
/*  58 */     super(field);
/*  59 */     this.parser = parser;
/*     */   }
/*     */ 
/*     */   public String description()
/*     */   {
/*  65 */     return "float(" + super.description() + ')';
/*     */   }
/*     */ 
/*     */   public DocValues getCachedFieldValues(FieldCache cache, String field, IndexReader reader)
/*     */     throws IOException
/*     */   {
/*  71 */     float[] arr = cache.getFloats(reader, field, this.parser);
/*  72 */     return new DocValues(arr)
/*     */     {
/*     */       public float floatVal(int doc)
/*     */       {
/*  76 */         return this.val$arr[doc];
/*     */       }
/*     */ 
/*     */       public String toString(int doc)
/*     */       {
/*  81 */         return FloatFieldSource.this.description() + '=' + this.val$arr[doc];
/*     */       }
/*     */ 
/*     */       Object getInnerArray()
/*     */       {
/*  86 */         return this.val$arr;
/*     */       }
/*     */     };
/*     */   }
/*     */ 
/*     */   public boolean cachedFieldSourceEquals(FieldCacheSource o)
/*     */   {
/*  94 */     if (o.getClass() != FloatFieldSource.class) {
/*  95 */       return false;
/*     */     }
/*  97 */     FloatFieldSource other = (FloatFieldSource)o;
/*  98 */     return other.parser == null;
/*     */   }
/*     */ 
/*     */   public int cachedFieldSourceHashCode()
/*     */   {
/* 106 */     return this.parser == null ? Float.class.hashCode() : this.parser.getClass().hashCode();
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.search.function.FloatFieldSource
 * JD-Core Version:    0.6.0
 */