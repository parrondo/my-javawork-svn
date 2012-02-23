/*     */ package org.apache.lucene.util;
/*     */ 
/*     */ import java.util.Collections;
/*     */ import java.util.Comparator;
/*     */ import java.util.List;
/*     */ import java.util.RandomAccess;
/*     */ 
/*     */ public final class CollectionUtil
/*     */ {
/*     */   private static <T> SorterTemplate getSorter(List<T> list, Comparator<? super T> comp)
/*     */   {
/*  40 */     if (!(list instanceof RandomAccess))
/*  41 */       throw new IllegalArgumentException("CollectionUtil can only sort random access lists in-place.");
/*  42 */     return new SorterTemplate(list, comp) { private T pivot;
/*     */ 
/*  45 */       protected void swap(int i, int j) { Collections.swap(this.val$list, i, j);
/*     */       }
/*     */ 
/*     */       protected int compare(int i, int j)
/*     */       {
/*  50 */         return this.val$comp.compare(this.val$list.get(i), this.val$list.get(j));
/*     */       }
/*     */ 
/*     */       protected void setPivot(int i)
/*     */       {
/*  55 */         this.pivot = this.val$list.get(i);
/*     */       }
/*     */ 
/*     */       protected int comparePivot(int j)
/*     */       {
/*  60 */         return this.val$comp.compare(this.pivot, this.val$list.get(j));
/*     */       }
/*     */     };
/*     */   }
/*     */ 
/*     */   private static <T extends Comparable<? super T>> SorterTemplate getSorter(List<T> list)
/*     */   {
/*  69 */     if (!(list instanceof RandomAccess))
/*  70 */       throw new IllegalArgumentException("CollectionUtil can only sort random access lists in-place.");
/*  71 */     return new SorterTemplate(list) { private T pivot;
/*     */ 
/*  74 */       protected void swap(int i, int j) { Collections.swap(this.val$list, i, j);
/*     */       }
/*     */ 
/*     */       protected int compare(int i, int j)
/*     */       {
/*  79 */         return ((Comparable)this.val$list.get(i)).compareTo(this.val$list.get(j));
/*     */       }
/*     */ 
/*     */       protected void setPivot(int i)
/*     */       {
/*  84 */         this.pivot = ((Comparable)this.val$list.get(i));
/*     */       }
/*     */ 
/*     */       protected int comparePivot(int j)
/*     */       {
/*  89 */         return this.pivot.compareTo(this.val$list.get(j));
/*     */       }
/*     */     };
/*     */   }
/*     */ 
/*     */   public static <T> void quickSort(List<T> list, Comparator<? super T> comp)
/*     */   {
/* 103 */     int size = list.size();
/* 104 */     if (size <= 1) return;
/* 105 */     getSorter(list, comp).quickSort(0, size - 1);
/*     */   }
/*     */ 
/*     */   public static <T extends Comparable<? super T>> void quickSort(List<T> list)
/*     */   {
/* 115 */     int size = list.size();
/* 116 */     if (size <= 1) return;
/* 117 */     getSorter(list).quickSort(0, size - 1);
/*     */   }
/*     */ 
/*     */   public static <T> void mergeSort(List<T> list, Comparator<? super T> comp)
/*     */   {
/* 129 */     int size = list.size();
/* 130 */     if (size <= 1) return;
/* 131 */     getSorter(list, comp).mergeSort(0, size - 1);
/*     */   }
/*     */ 
/*     */   public static <T extends Comparable<? super T>> void mergeSort(List<T> list)
/*     */   {
/* 141 */     int size = list.size();
/* 142 */     if (size <= 1) return;
/* 143 */     getSorter(list).mergeSort(0, size - 1);
/*     */   }
/*     */ 
/*     */   public static <T> void insertionSort(List<T> list, Comparator<? super T> comp)
/*     */   {
/* 155 */     int size = list.size();
/* 156 */     if (size <= 1) return;
/* 157 */     getSorter(list, comp).insertionSort(0, size - 1);
/*     */   }
/*     */ 
/*     */   public static <T extends Comparable<? super T>> void insertionSort(List<T> list)
/*     */   {
/* 167 */     int size = list.size();
/* 168 */     if (size <= 1) return;
/* 169 */     getSorter(list).insertionSort(0, size - 1);
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.util.CollectionUtil
 * JD-Core Version:    0.6.0
 */