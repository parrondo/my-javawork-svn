/*     */ package com.dukascopy.dds2.greed.util;
/*     */ 
/*     */ import java.util.Collection;
/*     */ import java.util.Comparator;
/*     */ import java.util.Map;
/*     */ 
/*     */ public class ObjectUtils
/*     */ {
/*     */   public static boolean isEqual(Object obj1, Object obj2)
/*     */   {
/*  22 */     if (obj1 == null) {
/*  23 */       return obj2 == null;
/*     */     }
/*     */ 
/*  26 */     return (obj2 != null) && (obj1.equals(obj2));
/*     */   }
/*     */ 
/*     */   public static int getHash(Object obj)
/*     */   {
/*  36 */     return obj == null ? 0 : obj.hashCode();
/*     */   }
/*     */ 
/*     */   public static boolean isNullOrEmpty(Object o)
/*     */   {
/*  46 */     if (o == null) return true;
/*  47 */     if ((o instanceof String)) {
/*  48 */       return ((String)o).isEmpty();
/*     */     }
/*  50 */     if ((o instanceof Collection)) {
/*  51 */       return ((Collection)o).isEmpty();
/*     */     }
/*  53 */     if ((o instanceof Map)) {
/*  54 */       return ((Map)o).isEmpty();
/*     */     }
/*  56 */     return false;
/*     */   }
/*     */ 
/*     */   public static <T> int compare(Comparable<T> comp1, T comp2)
/*     */   {
/*  68 */     if (comp1 == null) {
/*  69 */       if (comp2 == null) {
/*  70 */         return 0;
/*     */       }
/*     */ 
/*  73 */       return -1;
/*     */     }
/*     */ 
/*  77 */     if (comp2 == null) {
/*  78 */       return 1;
/*     */     }
/*     */ 
/*  81 */     return comp1.compareTo(comp2);
/*     */   }
/*     */ 
/*     */   public static <T> int compare(T o1, T o2, Comparator<T> comparator)
/*     */   {
/*  95 */     if (o1 == null) {
/*  96 */       if (o2 == null) {
/*  97 */         return 0;
/*     */       }
/*     */ 
/* 100 */       return -1;
/*     */     }
/*     */ 
/* 104 */     if (o2 == null) {
/* 105 */       return 1;
/*     */     }
/*     */ 
/* 108 */     return comparator.compare(o1, o2);
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.dds2.greed.util.ObjectUtils
 * JD-Core Version:    0.6.0
 */