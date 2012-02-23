/*    */ package org.apache.lucene.document;
/*    */ 
/*    */ public class LoadFirstFieldSelector
/*    */   implements FieldSelector
/*    */ {
/*    */   public FieldSelectorResult accept(String fieldName)
/*    */   {
/* 27 */     return FieldSelectorResult.LOAD_AND_BREAK;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.document.LoadFirstFieldSelector
 * JD-Core Version:    0.6.0
 */