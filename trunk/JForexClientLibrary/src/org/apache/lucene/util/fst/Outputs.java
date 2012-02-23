/*    */ package org.apache.lucene.util.fst;
/*    */ 
/*    */ import java.io.IOException;
/*    */ import org.apache.lucene.store.DataInput;
/*    */ import org.apache.lucene.store.DataOutput;
/*    */ 
/*    */ public abstract class Outputs<T>
/*    */ {
/*    */   public abstract T common(T paramT1, T paramT2);
/*    */ 
/*    */   public abstract T subtract(T paramT1, T paramT2);
/*    */ 
/*    */   public abstract T add(T paramT1, T paramT2);
/*    */ 
/*    */   public abstract void write(T paramT, DataOutput paramDataOutput)
/*    */     throws IOException;
/*    */ 
/*    */   public abstract T read(DataInput paramDataInput)
/*    */     throws IOException;
/*    */ 
/*    */   public abstract T getNoOutput();
/*    */ 
/*    */   public abstract String outputToString(T paramT);
/*    */ 
/*    */   public T merge(T first, T second)
/*    */   {
/* 60 */     throw new UnsupportedOperationException();
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.util.fst.Outputs
 * JD-Core Version:    0.6.0
 */