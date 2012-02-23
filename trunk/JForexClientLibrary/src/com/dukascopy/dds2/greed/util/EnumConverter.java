/*    */ package com.dukascopy.dds2.greed.util;
/*    */ 
/*    */ import java.util.Collections;
/*    */ import java.util.EnumSet;
/*    */ import java.util.HashSet;
/*    */ import java.util.Set;
/*    */ 
/*    */ public final class EnumConverter
/*    */ {
/*    */   public static <X extends Enum<X>, Y extends Enum<Y>> Y convert(X x, Class<Y> yClass)
/*    */   {
/* 14 */     if (x != null) {
/* 15 */       return Enum.valueOf(yClass, x.name());
/*    */     }
/*    */ 
/* 18 */     return null;
/*    */   }
/*    */ 
/*    */   public static <X extends Enum<X>, Y extends Enum<Y>> Set<Y> convert(Set<X> xSet, Class<Y> yClass) {
/* 22 */     Set enumSet = EnumSet.noneOf(yClass);
/*    */ 
/* 24 */     if (xSet != null) {
/* 25 */       for (Enum element : xSet) {
/* 26 */         enumSet.add(Enum.valueOf(yClass, element.name()));
/*    */       }
/*    */     }
/*    */ 
/* 30 */     return enumSet;
/*    */   }
/*    */ 
/*    */   public static <X extends Enum<X>> Set<String> convert(Set<X> xSet) {
/* 34 */     if (xSet == null) {
/* 35 */       return Collections.emptySet();
/*    */     }
/*    */ 
/* 38 */     Set set = new HashSet();
/*    */ 
/* 40 */     for (Enum element : xSet) {
/* 41 */       set.add(element.toString());
/*    */     }
/*    */ 
/* 44 */     return set;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.dds2.greed.util.EnumConverter
 * JD-Core Version:    0.6.0
 */