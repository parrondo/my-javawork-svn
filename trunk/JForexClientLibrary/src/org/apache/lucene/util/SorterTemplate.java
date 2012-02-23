/*     */ package org.apache.lucene.util;
/*     */ 
/*     */ public abstract class SorterTemplate
/*     */ {
/*     */   private static final int MERGESORT_THRESHOLD = 12;
/*     */   private static final int QUICKSORT_THRESHOLD = 7;
/*     */ 
/*     */   protected abstract void swap(int paramInt1, int paramInt2);
/*     */ 
/*     */   protected abstract int compare(int paramInt1, int paramInt2);
/*     */ 
/*     */   protected abstract void setPivot(int paramInt);
/*     */ 
/*     */   protected abstract int comparePivot(int paramInt);
/*     */ 
/*     */   public final void insertionSort(int lo, int hi)
/*     */   {
/*  52 */     for (int i = lo + 1; i <= hi; i++)
/*  53 */       for (int j = i; (j > lo) && 
/*  54 */         (compare(j - 1, j) > 0); j--)
/*     */       {
/*  55 */         swap(j - 1, j);
/*     */       }
/*     */   }
/*     */ 
/*     */   public final void quickSort(int lo, int hi)
/*     */   {
/*  66 */     if (hi <= lo) return;
/*     */ 
/*  68 */     quickSort(lo, hi, 32 - Integer.numberOfLeadingZeros(hi - lo) << 1);
/*     */   }
/*     */ 
/*     */   private void quickSort(int lo, int hi, int maxDepth)
/*     */   {
/*  73 */     int diff = hi - lo;
/*  74 */     if (diff <= 7) {
/*  75 */       insertionSort(lo, hi);
/*  76 */       return;
/*     */     }
/*     */ 
/*  80 */     maxDepth--; if (maxDepth == 0) {
/*  81 */       mergeSort(lo, hi);
/*  82 */       return;
/*     */     }
/*     */ 
/*  85 */     int mid = lo + (diff >>> 1);
/*     */ 
/*  87 */     if (compare(lo, mid) > 0) {
/*  88 */       swap(lo, mid);
/*     */     }
/*     */ 
/*  91 */     if (compare(mid, hi) > 0) {
/*  92 */       swap(mid, hi);
/*  93 */       if (compare(lo, mid) > 0) {
/*  94 */         swap(lo, mid);
/*     */       }
/*     */     }
/*     */ 
/*  98 */     int left = lo + 1;
/*  99 */     int right = hi - 1;
/*     */ 
/* 101 */     setPivot(mid);
/*     */     while (true) {
/* 103 */       if (comparePivot(right) < 0) {
/* 104 */         right--; continue;
/*     */       }
/* 106 */       while ((left < right) && (comparePivot(left) >= 0)) {
/* 107 */         left++;
/*     */       }
/* 109 */       if (left >= right) break;
/* 110 */       swap(left, right);
/* 111 */       right--;
/*     */     }
/*     */ 
/* 117 */     quickSort(lo, left, maxDepth);
/* 118 */     quickSort(left + 1, hi, maxDepth);
/*     */   }
/*     */ 
/*     */   public final void mergeSort(int lo, int hi)
/*     */   {
/* 124 */     int diff = hi - lo;
/* 125 */     if (diff <= 12) {
/* 126 */       insertionSort(lo, hi);
/* 127 */       return;
/*     */     }
/*     */ 
/* 130 */     int mid = lo + (diff >>> 1);
/*     */ 
/* 132 */     mergeSort(lo, mid);
/* 133 */     mergeSort(mid, hi);
/* 134 */     merge(lo, mid, hi, mid - lo, hi - mid);
/*     */   }
/*     */ 
/*     */   private void merge(int lo, int pivot, int hi, int len1, int len2) {
/* 138 */     if ((len1 == 0) || (len2 == 0)) {
/* 139 */       return;
/*     */     }
/* 141 */     if (len1 + len2 == 2) {
/* 142 */       if (compare(pivot, lo) < 0) {
/* 143 */         swap(pivot, lo);
/*     */       }
/* 145 */       return;
/*     */     }
/*     */     int len22;
/*     */     int len22;
/*     */     int second_cut;
/*     */     int first_cut;
/*     */     int len11;
/* 149 */     if (len1 > len2) {
/* 150 */       int len11 = len1 >>> 1;
/* 151 */       int first_cut = lo + len11;
/* 152 */       int second_cut = lower(pivot, hi, first_cut);
/* 153 */       len22 = second_cut - pivot;
/*     */     } else {
/* 155 */       len22 = len2 >>> 1;
/* 156 */       second_cut = pivot + len22;
/* 157 */       first_cut = upper(lo, pivot, second_cut);
/* 158 */       len11 = first_cut - lo;
/*     */     }
/* 160 */     rotate(first_cut, pivot, second_cut);
/* 161 */     int new_mid = first_cut + len22;
/* 162 */     merge(lo, first_cut, new_mid, len11, len22);
/* 163 */     merge(new_mid, second_cut, hi, len1 - len11, len2 - len22);
/*     */   }
/*     */ 
/*     */   private void rotate(int lo, int mid, int hi) {
/* 167 */     int lot = lo;
/* 168 */     int hit = mid - 1;
/* 169 */     while (lot < hit) {
/* 170 */       swap(lot++, hit--);
/*     */     }
/* 172 */     lot = mid; hit = hi - 1;
/* 173 */     while (lot < hit) {
/* 174 */       swap(lot++, hit--);
/*     */     }
/* 176 */     lot = lo; hit = hi - 1;
/* 177 */     while (lot < hit)
/* 178 */       swap(lot++, hit--);
/*     */   }
/*     */ 
/*     */   private int lower(int lo, int hi, int val)
/*     */   {
/* 183 */     int len = hi - lo;
/* 184 */     while (len > 0) {
/* 185 */       int half = len >>> 1;
/* 186 */       int mid = lo + half;
/* 187 */       if (compare(mid, val) < 0) {
/* 188 */         lo = mid + 1;
/* 189 */         len = len - half - 1;
/*     */       } else {
/* 191 */         len = half;
/*     */       }
/*     */     }
/* 194 */     return lo;
/*     */   }
/*     */ 
/*     */   private int upper(int lo, int hi, int val) {
/* 198 */     int len = hi - lo;
/* 199 */     while (len > 0) {
/* 200 */       int half = len >>> 1;
/* 201 */       int mid = lo + half;
/* 202 */       if (compare(val, mid) < 0) {
/* 203 */         len = half;
/*     */       } else {
/* 205 */         lo = mid + 1;
/* 206 */         len = len - half - 1;
/*     */       }
/*     */     }
/* 209 */     return lo;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.util.SorterTemplate
 * JD-Core Version:    0.6.0
 */