/*    */ package org.apache.lucene.document;
/*    */ 
/*    */ import java.util.Arrays;
/*    */ import java.util.HashMap;
/*    */ import java.util.List;
/*    */ import java.util.Map;
/*    */ 
/*    */ public class MapFieldSelector
/*    */   implements FieldSelector
/*    */ {
/*    */   Map<String, FieldSelectorResult> fieldSelections;
/*    */ 
/*    */   public MapFieldSelector(Map<String, FieldSelectorResult> fieldSelections)
/*    */   {
/* 37 */     this.fieldSelections = fieldSelections;
/*    */   }
/*    */ 
/*    */   public MapFieldSelector(List<String> fields)
/*    */   {
/* 44 */     this.fieldSelections = new HashMap(fields.size() * 5 / 3);
/* 45 */     for (String field : fields)
/* 46 */       this.fieldSelections.put(field, FieldSelectorResult.LOAD);
/*    */   }
/*    */ 
/*    */   public MapFieldSelector(String[] fields)
/*    */   {
/* 53 */     this(Arrays.asList(fields));
/*    */   }
/*    */ 
/*    */   public FieldSelectorResult accept(String field)
/*    */   {
/* 63 */     FieldSelectorResult selection = (FieldSelectorResult)this.fieldSelections.get(field);
/* 64 */     return selection != null ? selection : FieldSelectorResult.NO_LOAD;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.document.MapFieldSelector
 * JD-Core Version:    0.6.0
 */