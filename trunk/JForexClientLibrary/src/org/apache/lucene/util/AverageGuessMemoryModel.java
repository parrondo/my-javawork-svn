/*    */ package org.apache.lucene.util;
/*    */ 
/*    */ import java.util.IdentityHashMap;
/*    */ import java.util.Map;
/*    */ 
/*    */ public class AverageGuessMemoryModel extends MemoryModel
/*    */ {
/* 29 */   private final Map<Class<?>, Integer> sizes = new IdentityHashMap() { } ;
/*    */ 
/*    */   public int getArraySize()
/*    */   {
/* 49 */     return 16;
/*    */   }
/*    */ 
/*    */   public int getClassSize()
/*    */   {
/* 59 */     return 8;
/*    */   }
/*    */ 
/*    */   public int getPrimitiveSize(Class<?> clazz)
/*    */   {
/* 67 */     return ((Integer)this.sizes.get(clazz)).intValue();
/*    */   }
/*    */ 
/*    */   public int getReferenceSize()
/*    */   {
/* 75 */     return 4;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.util.AverageGuessMemoryModel
 * JD-Core Version:    0.6.0
 */