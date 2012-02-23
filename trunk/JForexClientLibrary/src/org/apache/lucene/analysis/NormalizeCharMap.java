/*    */ package org.apache.lucene.analysis;
/*    */ 
/*    */ import java.util.HashMap;
/*    */ import java.util.Map;
/*    */ 
/*    */ public class NormalizeCharMap
/*    */ {
/*    */   Map<Character, NormalizeCharMap> submap;
/*    */   String normStr;
/*    */   int diff;
/*    */ 
/*    */   public void add(String singleMatch, String replacement)
/*    */   {
/* 42 */     NormalizeCharMap currMap = this;
/* 43 */     for (int i = 0; i < singleMatch.length(); i++) {
/* 44 */       char c = singleMatch.charAt(i);
/* 45 */       if (currMap.submap == null) {
/* 46 */         currMap.submap = new HashMap(1);
/*    */       }
/* 48 */       NormalizeCharMap map = (NormalizeCharMap)currMap.submap.get(Character.valueOf(c));
/* 49 */       if (map == null) {
/* 50 */         map = new NormalizeCharMap();
/* 51 */         currMap.submap.put(Character.valueOf(c), map);
/*    */       }
/* 53 */       currMap = map;
/*    */     }
/* 55 */     if (currMap.normStr != null) {
/* 56 */       throw new RuntimeException("MappingCharFilter: there is already a mapping for " + singleMatch);
/*    */     }
/* 58 */     currMap.normStr = replacement;
/* 59 */     currMap.diff = (singleMatch.length() - replacement.length());
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.analysis.NormalizeCharMap
 * JD-Core Version:    0.6.0
 */