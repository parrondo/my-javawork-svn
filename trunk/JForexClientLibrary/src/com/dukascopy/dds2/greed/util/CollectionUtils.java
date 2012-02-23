/*    */ package com.dukascopy.dds2.greed.util;
/*    */ 
/*    */ import java.io.PrintStream;
/*    */ import java.util.ArrayList;
/*    */ import java.util.Arrays;
/*    */ import java.util.Iterator;
/*    */ import java.util.List;
/*    */ import java.util.Map;
/*    */ import java.util.TreeMap;
/*    */ 
/*    */ public class CollectionUtils
/*    */ {
/*    */   public static TreeMap<String, String> createTreeMapFromLists(List<String> keys, List<String> values)
/*    */   {
/* 13 */     assert ((keys != null) && (values != null) && (keys.size() == values.size()));
/*    */ 
/* 15 */     TreeMap result = new TreeMap();
/* 16 */     Iterator keysIt = keys.iterator(); for (Iterator valuesIt = values.iterator(); (keysIt.hasNext()) && (valuesIt.hasNext()); ) {
/* 17 */       result.put(keysIt.next(), valuesIt.next());
/*    */     }
/* 19 */     return result;
/*    */   }
/*    */ 
/*    */   public static TreeMap<String, String> createTreeMapFromArrays(String[][] keysValueArray) {
/* 23 */     return createTreeMapFromArrays(keysValueArray, false);
/*    */   }
/*    */ 
/*    */   public static TreeMap<String, String> createTreeMapFromArrays(String[][] keysValueArray, boolean isValueFirst)
/*    */   {
/* 28 */     assert (keysValueArray != null);
/*    */ 
/* 30 */     TreeMap result = new TreeMap();
/* 31 */     for (int i = 0; i < keysValueArray.length; i++) {
/* 32 */       String[] keyValue = keysValueArray[i];
/*    */ 
/* 34 */       assert (keyValue.length == 2);
/*    */ 
/* 36 */       String key = keyValue[0];
/* 37 */       String value = keyValue[1];
/* 38 */       if (isValueFirst) {
/* 39 */         key = keyValue[1];
/* 40 */         value = keyValue[0];
/*    */       }
/* 42 */       result.put(key, value);
/*    */     }
/* 44 */     return result;
/*    */   }
/*    */ 
/*    */   public static <T> List<T> except(List<T> list1, List<T> list2)
/*    */   {
/* 53 */     List list = new ArrayList();
/*    */ 
/* 55 */     for (Iterator i$ = list1.iterator(); i$.hasNext(); ) { Object t = i$.next();
/* 56 */       if (!list2.contains(t)) {
/* 57 */         list.add(t);
/*    */       }
/*    */     }
/*    */ 
/* 61 */     return list;
/*    */   }
/*    */ 
/*    */   public static void main(String[] args) {
/* 65 */     Map foobar = createTreeMapFromLists(Arrays.asList(new String[] { "1", "2" }), Arrays.asList(new String[] { "A", "B" }));
/* 66 */     System.out.println(foobar);
/* 67 */     String[][] foo = { { "1", "A" }, { "2", "B" } };
/* 68 */     Map foobaz = createTreeMapFromArrays(foo);
/* 69 */     System.out.println(foobaz);
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.util.CollectionUtils
 * JD-Core Version:    0.6.0
 */