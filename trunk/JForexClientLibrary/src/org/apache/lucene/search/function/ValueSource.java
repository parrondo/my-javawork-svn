/*    */ package org.apache.lucene.search.function;
/*    */ 
/*    */ import java.io.IOException;
/*    */ import java.io.Serializable;
/*    */ import org.apache.lucene.index.IndexReader;
/*    */ 
/*    */ public abstract class ValueSource
/*    */   implements Serializable
/*    */ {
/*    */   public abstract DocValues getValues(IndexReader paramIndexReader)
/*    */     throws IOException;
/*    */ 
/*    */   public abstract String description();
/*    */ 
/*    */   public String toString()
/*    */   {
/* 56 */     return description();
/*    */   }
/*    */ 
/*    */   public abstract boolean equals(Object paramObject);
/*    */ 
/*    */   public abstract int hashCode();
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.search.function.ValueSource
 * JD-Core Version:    0.6.0
 */