/*    */ package org.apache.lucene.search.function;
/*    */ 
/*    */ import java.io.IOException;
/*    */ import org.apache.lucene.index.IndexReader;
/*    */ import org.apache.lucene.search.FieldCache;
/*    */ 
/*    */ public abstract class FieldCacheSource extends ValueSource
/*    */ {
/*    */   private String field;
/*    */ 
/*    */   public FieldCacheSource(String field)
/*    */   {
/* 53 */     this.field = field;
/*    */   }
/*    */ 
/*    */   public final DocValues getValues(IndexReader reader)
/*    */     throws IOException
/*    */   {
/* 59 */     return getCachedFieldValues(FieldCache.DEFAULT, this.field, reader);
/*    */   }
/*    */ 
/*    */   public String description()
/*    */   {
/* 65 */     return this.field;
/*    */   }
/*    */ 
/*    */   public abstract DocValues getCachedFieldValues(FieldCache paramFieldCache, String paramString, IndexReader paramIndexReader)
/*    */     throws IOException;
/*    */ 
/*    */   public final boolean equals(Object o)
/*    */   {
/* 79 */     if (!(o instanceof FieldCacheSource)) {
/* 80 */       return false;
/*    */     }
/* 82 */     FieldCacheSource other = (FieldCacheSource)o;
/* 83 */     return (this.field.equals(other.field)) && (cachedFieldSourceEquals(other));
/*    */   }
/*    */ 
/*    */   public final int hashCode()
/*    */   {
/* 91 */     return this.field.hashCode() + cachedFieldSourceHashCode();
/*    */   }
/*    */ 
/*    */   public abstract boolean cachedFieldSourceEquals(FieldCacheSource paramFieldCacheSource);
/*    */ 
/*    */   public abstract int cachedFieldSourceHashCode();
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.search.function.FieldCacheSource
 * JD-Core Version:    0.6.0
 */