/*    */ package org.apache.lucene.util.fst;
/*    */ 
/*    */ import org.apache.lucene.store.DataInput;
/*    */ import org.apache.lucene.store.DataOutput;
/*    */ 
/*    */ public final class NoOutputs extends Outputs<Object>
/*    */ {
/*    */   static final Object NO_OUTPUT;
/*    */   private static final NoOutputs singleton;
/*    */ 
/*    */   public static NoOutputs getSingleton()
/*    */   {
/* 51 */     return singleton;
/*    */   }
/*    */ 
/*    */   public Object common(Object output1, Object output2)
/*    */   {
/* 56 */     assert (output1 == NO_OUTPUT);
/* 57 */     assert (output2 == NO_OUTPUT);
/* 58 */     return NO_OUTPUT;
/*    */   }
/*    */ 
/*    */   public Object subtract(Object output, Object inc)
/*    */   {
/* 63 */     assert (output == NO_OUTPUT);
/* 64 */     assert (inc == NO_OUTPUT);
/* 65 */     return NO_OUTPUT;
/*    */   }
/*    */ 
/*    */   public Object add(Object prefix, Object output)
/*    */   {
/* 70 */     assert (prefix == NO_OUTPUT) : ("got " + prefix);
/* 71 */     assert (output == NO_OUTPUT);
/* 72 */     return NO_OUTPUT;
/*    */   }
/*    */ 
/*    */   public void write(Object prefix, DataOutput out)
/*    */   {
/*    */   }
/*    */ 
/*    */   public Object read(DataInput in)
/*    */   {
/* 84 */     return NO_OUTPUT;
/*    */   }
/*    */ 
/*    */   public Object getNoOutput()
/*    */   {
/* 89 */     return NO_OUTPUT;
/*    */   }
/*    */ 
/*    */   public String outputToString(Object output)
/*    */   {
/* 94 */     return "";
/*    */   }
/*    */ 
/*    */   static
/*    */   {
/* 31 */     NO_OUTPUT = new Object()
/*    */     {
/*    */       public int hashCode()
/*    */       {
/* 36 */         return 42;
/*    */       }
/*    */ 
/*    */       public boolean equals(Object other)
/*    */       {
/* 41 */         return other == this;
/*    */       }
/*    */     };
/* 45 */     singleton = new NoOutputs();
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.util.fst.NoOutputs
 * JD-Core Version:    0.6.0
 */