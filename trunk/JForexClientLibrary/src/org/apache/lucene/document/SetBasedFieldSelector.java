/*    */ package org.apache.lucene.document;
/*    */ 
/*    */ import java.util.Set;
/*    */ 
/*    */ public class SetBasedFieldSelector
/*    */   implements FieldSelector
/*    */ {
/*    */   private Set<String> fieldsToLoad;
/*    */   private Set<String> lazyFieldsToLoad;
/*    */ 
/*    */   public SetBasedFieldSelector(Set<String> fieldsToLoad, Set<String> lazyFieldsToLoad)
/*    */   {
/* 36 */     this.fieldsToLoad = fieldsToLoad;
/* 37 */     this.lazyFieldsToLoad = lazyFieldsToLoad;
/*    */   }
/*    */ 
/*    */   public FieldSelectorResult accept(String fieldName)
/*    */   {
/* 49 */     FieldSelectorResult result = FieldSelectorResult.NO_LOAD;
/* 50 */     if (this.fieldsToLoad.contains(fieldName) == true) {
/* 51 */       result = FieldSelectorResult.LOAD;
/*    */     }
/* 53 */     if (this.lazyFieldsToLoad.contains(fieldName) == true) {
/* 54 */       result = FieldSelectorResult.LAZY_LOAD;
/*    */     }
/* 56 */     return result;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.document.SetBasedFieldSelector
 * JD-Core Version:    0.6.0
 */