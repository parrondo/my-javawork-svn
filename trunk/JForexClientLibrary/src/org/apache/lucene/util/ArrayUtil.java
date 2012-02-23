/*     */ package org.apache.lucene.util;
/*     */ 
/*     */ import java.util.Collection;
/*     */ import java.util.Comparator;
/*     */ import java.util.Iterator;
/*     */ 
/*     */ public final class ArrayUtil
/*     */ {
/*     */   public static int parseInt(char[] chars)
/*     */     throws NumberFormatException
/*     */   {
/*  56 */     return parseInt(chars, 0, chars.length, 10);
/*     */   }
/*     */ 
/*     */   public static int parseInt(char[] chars, int offset, int len)
/*     */     throws NumberFormatException
/*     */   {
/*  68 */     return parseInt(chars, offset, len, 10);
/*     */   }
/*     */ 
/*     */   public static int parseInt(char[] chars, int offset, int len, int radix)
/*     */     throws NumberFormatException
/*     */   {
/*  84 */     if ((chars == null) || (radix < 2) || (radix > 36))
/*     */     {
/*  86 */       throw new NumberFormatException();
/*     */     }
/*  88 */     int i = 0;
/*  89 */     if (len == 0) {
/*  90 */       throw new NumberFormatException("chars length is 0");
/*     */     }
/*  92 */     boolean negative = chars[(offset + i)] == '-';
/*  93 */     if (negative) { i++; if (i == len)
/*  94 */         throw new NumberFormatException("can't convert to an int");
/*     */     }
/*  96 */     if (negative == true) {
/*  97 */       offset++;
/*  98 */       len--;
/*     */     }
/* 100 */     return parse(chars, offset, len, radix, negative);
/*     */   }
/*     */ 
/*     */   private static int parse(char[] chars, int offset, int len, int radix, boolean negative)
/*     */     throws NumberFormatException
/*     */   {
/* 106 */     int max = -2147483648 / radix;
/* 107 */     int result = 0;
/* 108 */     for (int i = 0; i < len; i++) {
/* 109 */       int digit = Character.digit(chars[(i + offset)], radix);
/* 110 */       if (digit == -1) {
/* 111 */         throw new NumberFormatException("Unable to parse");
/*     */       }
/* 113 */       if (max > result) {
/* 114 */         throw new NumberFormatException("Unable to parse");
/*     */       }
/* 116 */       int next = result * radix - digit;
/* 117 */       if (next > result) {
/* 118 */         throw new NumberFormatException("Unable to parse");
/*     */       }
/* 120 */       result = next;
/*     */     }
/*     */ 
/* 125 */     if (!negative) {
/* 126 */       result = -result;
/* 127 */       if (result < 0) {
/* 128 */         throw new NumberFormatException("Unable to parse");
/*     */       }
/*     */     }
/* 131 */     return result;
/*     */   }
/*     */ 
/*     */   public static int oversize(int minTargetSize, int bytesPerElement)
/*     */   {
/* 160 */     if (minTargetSize < 0)
/*     */     {
/* 162 */       throw new IllegalArgumentException("invalid array size " + minTargetSize);
/*     */     }
/*     */ 
/* 165 */     if (minTargetSize == 0)
/*     */     {
/* 167 */       return 0;
/*     */     }
/*     */ 
/* 173 */     int extra = minTargetSize >> 3;
/*     */ 
/* 175 */     if (extra < 3)
/*     */     {
/* 179 */       extra = 3;
/*     */     }
/*     */ 
/* 182 */     int newSize = minTargetSize + extra;
/*     */ 
/* 185 */     if (newSize + 7 < 0)
/*     */     {
/* 187 */       return 2147483647;
/*     */     }
/*     */ 
/* 190 */     if (Constants.JRE_IS_64BIT)
/*     */     {
/* 192 */       switch (bytesPerElement)
/*     */       {
/*     */       case 4:
/* 195 */         return newSize + 1 & 0x7FFFFFFE;
/*     */       case 2:
/* 198 */         return newSize + 3 & 0x7FFFFFFC;
/*     */       case 1:
/* 201 */         return newSize + 7 & 0x7FFFFFF8;
/*     */       case 3:
/*     */       case 5:
/*     */       case 6:
/*     */       case 7:
/* 206 */       case 8: } return newSize;
/*     */     }
/*     */ 
/* 210 */     switch (bytesPerElement)
/*     */     {
/*     */     case 2:
/* 213 */       return newSize + 1 & 0x7FFFFFFE;
/*     */     case 1:
/* 216 */       return newSize + 3 & 0x7FFFFFFC;
/*     */     case 3:
/*     */     case 4:
/*     */     case 5:
/*     */     case 6:
/*     */     case 7:
/* 222 */     case 8: } return newSize;
/*     */   }
/*     */ 
/*     */   public static int getShrinkSize(int currentSize, int targetSize, int bytesPerElement)
/*     */   {
/* 228 */     int newSize = oversize(targetSize, bytesPerElement);
/*     */ 
/* 232 */     if (newSize < currentSize / 2) {
/* 233 */       return newSize;
/*     */     }
/* 235 */     return currentSize;
/*     */   }
/*     */ 
/*     */   public static short[] grow(short[] array, int minSize) {
/* 239 */     assert (minSize >= 0) : ("size must be positive (got " + minSize + "): likely integer overflow?");
/* 240 */     if (array.length < minSize) {
/* 241 */       short[] newArray = new short[oversize(minSize, 2)];
/* 242 */       System.arraycopy(array, 0, newArray, 0, array.length);
/* 243 */       return newArray;
/*     */     }
/* 245 */     return array;
/*     */   }
/*     */ 
/*     */   public static short[] grow(short[] array) {
/* 249 */     return grow(array, 1 + array.length);
/*     */   }
/*     */ 
/*     */   public static float[] grow(float[] array, int minSize) {
/* 253 */     assert (minSize >= 0) : ("size must be positive (got " + minSize + "): likely integer overflow?");
/* 254 */     if (array.length < minSize) {
/* 255 */       float[] newArray = new float[oversize(minSize, 4)];
/* 256 */       System.arraycopy(array, 0, newArray, 0, array.length);
/* 257 */       return newArray;
/*     */     }
/* 259 */     return array;
/*     */   }
/*     */ 
/*     */   public static float[] grow(float[] array) {
/* 263 */     return grow(array, 1 + array.length);
/*     */   }
/*     */ 
/*     */   public static double[] grow(double[] array, int minSize) {
/* 267 */     assert (minSize >= 0) : ("size must be positive (got " + minSize + "): likely integer overflow?");
/* 268 */     if (array.length < minSize) {
/* 269 */       double[] newArray = new double[oversize(minSize, 8)];
/* 270 */       System.arraycopy(array, 0, newArray, 0, array.length);
/* 271 */       return newArray;
/*     */     }
/* 273 */     return array;
/*     */   }
/*     */ 
/*     */   public static double[] grow(double[] array) {
/* 277 */     return grow(array, 1 + array.length);
/*     */   }
/*     */ 
/*     */   public static short[] shrink(short[] array, int targetSize) {
/* 281 */     assert (targetSize >= 0) : ("size must be positive (got " + targetSize + "): likely integer overflow?");
/* 282 */     int newSize = getShrinkSize(array.length, targetSize, 2);
/* 283 */     if (newSize != array.length) {
/* 284 */       short[] newArray = new short[newSize];
/* 285 */       System.arraycopy(array, 0, newArray, 0, newSize);
/* 286 */       return newArray;
/*     */     }
/* 288 */     return array;
/*     */   }
/*     */ 
/*     */   public static int[] grow(int[] array, int minSize) {
/* 292 */     assert (minSize >= 0) : ("size must be positive (got " + minSize + "): likely integer overflow?");
/* 293 */     if (array.length < minSize) {
/* 294 */       int[] newArray = new int[oversize(minSize, 4)];
/* 295 */       System.arraycopy(array, 0, newArray, 0, array.length);
/* 296 */       return newArray;
/*     */     }
/* 298 */     return array;
/*     */   }
/*     */ 
/*     */   public static int[] grow(int[] array) {
/* 302 */     return grow(array, 1 + array.length);
/*     */   }
/*     */ 
/*     */   public static int[] shrink(int[] array, int targetSize) {
/* 306 */     assert (targetSize >= 0) : ("size must be positive (got " + targetSize + "): likely integer overflow?");
/* 307 */     int newSize = getShrinkSize(array.length, targetSize, 4);
/* 308 */     if (newSize != array.length) {
/* 309 */       int[] newArray = new int[newSize];
/* 310 */       System.arraycopy(array, 0, newArray, 0, newSize);
/* 311 */       return newArray;
/*     */     }
/* 313 */     return array;
/*     */   }
/*     */ 
/*     */   public static long[] grow(long[] array, int minSize) {
/* 317 */     assert (minSize >= 0) : ("size must be positive (got " + minSize + "): likely integer overflow?");
/* 318 */     if (array.length < minSize) {
/* 319 */       long[] newArray = new long[oversize(minSize, 8)];
/* 320 */       System.arraycopy(array, 0, newArray, 0, array.length);
/* 321 */       return newArray;
/*     */     }
/* 323 */     return array;
/*     */   }
/*     */ 
/*     */   public static long[] grow(long[] array) {
/* 327 */     return grow(array, 1 + array.length);
/*     */   }
/*     */ 
/*     */   public static long[] shrink(long[] array, int targetSize) {
/* 331 */     assert (targetSize >= 0) : ("size must be positive (got " + targetSize + "): likely integer overflow?");
/* 332 */     int newSize = getShrinkSize(array.length, targetSize, 8);
/* 333 */     if (newSize != array.length) {
/* 334 */       long[] newArray = new long[newSize];
/* 335 */       System.arraycopy(array, 0, newArray, 0, newSize);
/* 336 */       return newArray;
/*     */     }
/* 338 */     return array;
/*     */   }
/*     */ 
/*     */   public static byte[] grow(byte[] array, int minSize) {
/* 342 */     assert (minSize >= 0) : ("size must be positive (got " + minSize + "): likely integer overflow?");
/* 343 */     if (array.length < minSize) {
/* 344 */       byte[] newArray = new byte[oversize(minSize, 1)];
/* 345 */       System.arraycopy(array, 0, newArray, 0, array.length);
/* 346 */       return newArray;
/*     */     }
/* 348 */     return array;
/*     */   }
/*     */ 
/*     */   public static byte[] grow(byte[] array) {
/* 352 */     return grow(array, 1 + array.length);
/*     */   }
/*     */ 
/*     */   public static byte[] shrink(byte[] array, int targetSize) {
/* 356 */     assert (targetSize >= 0) : ("size must be positive (got " + targetSize + "): likely integer overflow?");
/* 357 */     int newSize = getShrinkSize(array.length, targetSize, 1);
/* 358 */     if (newSize != array.length) {
/* 359 */       byte[] newArray = new byte[newSize];
/* 360 */       System.arraycopy(array, 0, newArray, 0, newSize);
/* 361 */       return newArray;
/*     */     }
/* 363 */     return array;
/*     */   }
/*     */ 
/*     */   public static boolean[] grow(boolean[] array, int minSize) {
/* 367 */     assert (minSize >= 0) : ("size must be positive (got " + minSize + "): likely integer overflow?");
/* 368 */     if (array.length < minSize) {
/* 369 */       boolean[] newArray = new boolean[oversize(minSize, 1)];
/* 370 */       System.arraycopy(array, 0, newArray, 0, array.length);
/* 371 */       return newArray;
/*     */     }
/* 373 */     return array;
/*     */   }
/*     */ 
/*     */   public static boolean[] grow(boolean[] array) {
/* 377 */     return grow(array, 1 + array.length);
/*     */   }
/*     */ 
/*     */   public static boolean[] shrink(boolean[] array, int targetSize) {
/* 381 */     assert (targetSize >= 0) : ("size must be positive (got " + targetSize + "): likely integer overflow?");
/* 382 */     int newSize = getShrinkSize(array.length, targetSize, 1);
/* 383 */     if (newSize != array.length) {
/* 384 */       boolean[] newArray = new boolean[newSize];
/* 385 */       System.arraycopy(array, 0, newArray, 0, newSize);
/* 386 */       return newArray;
/*     */     }
/* 388 */     return array;
/*     */   }
/*     */ 
/*     */   public static char[] grow(char[] array, int minSize) {
/* 392 */     assert (minSize >= 0) : ("size must be positive (got " + minSize + "): likely integer overflow?");
/* 393 */     if (array.length < minSize) {
/* 394 */       char[] newArray = new char[oversize(minSize, 2)];
/* 395 */       System.arraycopy(array, 0, newArray, 0, array.length);
/* 396 */       return newArray;
/*     */     }
/* 398 */     return array;
/*     */   }
/*     */ 
/*     */   public static char[] grow(char[] array) {
/* 402 */     return grow(array, 1 + array.length);
/*     */   }
/*     */ 
/*     */   public static char[] shrink(char[] array, int targetSize) {
/* 406 */     assert (targetSize >= 0) : ("size must be positive (got " + targetSize + "): likely integer overflow?");
/* 407 */     int newSize = getShrinkSize(array.length, targetSize, 2);
/* 408 */     if (newSize != array.length) {
/* 409 */       char[] newArray = new char[newSize];
/* 410 */       System.arraycopy(array, 0, newArray, 0, newSize);
/* 411 */       return newArray;
/*     */     }
/* 413 */     return array;
/*     */   }
/*     */ 
/*     */   public static int[][] grow(int[][] array, int minSize) {
/* 417 */     assert (minSize >= 0) : ("size must be positive (got " + minSize + "): likely integer overflow?");
/* 418 */     if (array.length < minSize) {
/* 419 */       int[][] newArray = new int[oversize(minSize, RamUsageEstimator.NUM_BYTES_OBJECT_REF)][];
/* 420 */       System.arraycopy(array, 0, newArray, 0, array.length);
/* 421 */       return newArray;
/*     */     }
/* 423 */     return array;
/*     */   }
/*     */ 
/*     */   public static int[][] grow(int[][] array)
/*     */   {
/* 428 */     return grow(array, 1 + array.length);
/*     */   }
/*     */ 
/*     */   public static int[][] shrink(int[][] array, int targetSize) {
/* 432 */     assert (targetSize >= 0) : ("size must be positive (got " + targetSize + "): likely integer overflow?");
/* 433 */     int newSize = getShrinkSize(array.length, targetSize, RamUsageEstimator.NUM_BYTES_OBJECT_REF);
/* 434 */     if (newSize != array.length) {
/* 435 */       int[][] newArray = new int[newSize][];
/* 436 */       System.arraycopy(array, 0, newArray, 0, newSize);
/* 437 */       return newArray;
/*     */     }
/* 439 */     return array;
/*     */   }
/*     */ 
/*     */   public static float[][] grow(float[][] array, int minSize)
/*     */   {
/* 444 */     assert (minSize >= 0) : ("size must be positive (got " + minSize + "): likely integer overflow?");
/* 445 */     if (array.length < minSize) {
/* 446 */       float[][] newArray = new float[oversize(minSize, RamUsageEstimator.NUM_BYTES_OBJECT_REF)][];
/* 447 */       System.arraycopy(array, 0, newArray, 0, array.length);
/* 448 */       return newArray;
/*     */     }
/* 450 */     return array;
/*     */   }
/*     */ 
/*     */   public static float[][] grow(float[][] array)
/*     */   {
/* 455 */     return grow(array, 1 + array.length);
/*     */   }
/*     */ 
/*     */   public static float[][] shrink(float[][] array, int targetSize) {
/* 459 */     assert (targetSize >= 0) : ("size must be positive (got " + targetSize + "): likely integer overflow?");
/* 460 */     int newSize = getShrinkSize(array.length, targetSize, RamUsageEstimator.NUM_BYTES_OBJECT_REF);
/* 461 */     if (newSize != array.length) {
/* 462 */       float[][] newArray = new float[newSize][];
/* 463 */       System.arraycopy(array, 0, newArray, 0, newSize);
/* 464 */       return newArray;
/*     */     }
/* 466 */     return array;
/*     */   }
/*     */ 
/*     */   public static int hashCode(char[] array, int start, int end)
/*     */   {
/* 475 */     int code = 0;
/* 476 */     for (int i = end - 1; i >= start; i--)
/* 477 */       code = code * 31 + array[i];
/* 478 */     return code;
/*     */   }
/*     */ 
/*     */   public static int hashCode(byte[] array, int start, int end)
/*     */   {
/* 486 */     int code = 0;
/* 487 */     for (int i = end - 1; i >= start; i--)
/* 488 */       code = code * 31 + array[i];
/* 489 */     return code;
/*     */   }
/*     */ 
/*     */   public static boolean equals(char[] left, int offsetLeft, char[] right, int offsetRight, int length)
/*     */   {
/* 507 */     if ((offsetLeft + length <= left.length) && (offsetRight + length <= right.length)) {
/* 508 */       for (int i = 0; i < length; i++) {
/* 509 */         if (left[(offsetLeft + i)] != right[(offsetRight + i)]) {
/* 510 */           return false;
/*     */         }
/*     */       }
/*     */ 
/* 514 */       return true;
/*     */     }
/* 516 */     return false;
/*     */   }
/*     */ 
/*     */   public static boolean equals(int[] left, int offsetLeft, int[] right, int offsetRight, int length)
/*     */   {
/* 533 */     if ((offsetLeft + length <= left.length) && (offsetRight + length <= right.length)) {
/* 534 */       for (int i = 0; i < length; i++) {
/* 535 */         if (left[(offsetLeft + i)] != right[(offsetRight + i)]) {
/* 536 */           return false;
/*     */         }
/*     */       }
/*     */ 
/* 540 */       return true;
/*     */     }
/* 542 */     return false;
/*     */   }
/*     */ 
/*     */   public static int[] toIntArray(Collection<Integer> ints)
/*     */   {
/* 547 */     int[] result = new int[ints.size()];
/* 548 */     int upto = 0;
/* 549 */     for (Iterator i$ = ints.iterator(); i$.hasNext(); ) { int v = ((Integer)i$.next()).intValue();
/* 550 */       result[(upto++)] = v;
/*     */     }
/*     */ 
/* 554 */     assert (upto == result.length);
/*     */ 
/* 556 */     return result;
/*     */   }
/*     */ 
/*     */   private static <T> SorterTemplate getSorter(T[] a, Comparator<? super T> comp)
/*     */   {
/* 561 */     return new SorterTemplate(a, comp) { private T pivot;
/*     */ 
/* 564 */       protected void swap(int i, int j) { Object o = this.val$a[i];
/* 565 */         this.val$a[i] = this.val$a[j];
/* 566 */         this.val$a[j] = o;
/*     */       }
/*     */ 
/*     */       protected int compare(int i, int j)
/*     */       {
/* 571 */         return this.val$comp.compare(this.val$a[i], this.val$a[j]);
/*     */       }
/*     */ 
/*     */       protected void setPivot(int i)
/*     */       {
/* 576 */         this.pivot = this.val$a[i];
/*     */       }
/*     */ 
/*     */       protected int comparePivot(int j)
/*     */       {
/* 581 */         return this.val$comp.compare(this.pivot, this.val$a[j]);
/*     */       }
/*     */     };
/*     */   }
/*     */ 
/*     */   private static <T extends Comparable<? super T>> SorterTemplate getSorter(T[] a)
/*     */   {
/* 590 */     return new SorterTemplate(a) { private T pivot;
/*     */ 
/* 593 */       protected void swap(int i, int j) { Comparable o = this.val$a[i];
/* 594 */         this.val$a[i] = this.val$a[j];
/* 595 */         this.val$a[j] = o;
/*     */       }
/*     */ 
/*     */       protected int compare(int i, int j)
/*     */       {
/* 600 */         return this.val$a[i].compareTo(this.val$a[j]);
/*     */       }
/*     */ 
/*     */       protected void setPivot(int i)
/*     */       {
/* 605 */         this.pivot = this.val$a[i];
/*     */       }
/*     */ 
/*     */       protected int comparePivot(int j)
/*     */       {
/* 610 */         return this.pivot.compareTo(this.val$a[j]);
/*     */       }
/*     */     };
/*     */   }
/*     */ 
/*     */   public static <T> void quickSort(T[] a, int fromIndex, int toIndex, Comparator<? super T> comp)
/*     */   {
/* 626 */     if (toIndex - fromIndex <= 1) return;
/* 627 */     getSorter(a, comp).quickSort(fromIndex, toIndex - 1);
/*     */   }
/*     */ 
/*     */   public static <T> void quickSort(T[] a, Comparator<? super T> comp)
/*     */   {
/* 635 */     quickSort(a, 0, a.length, comp);
/*     */   }
/*     */ 
/*     */   public static <T extends Comparable<? super T>> void quickSort(T[] a, int fromIndex, int toIndex)
/*     */   {
/* 645 */     if (toIndex - fromIndex <= 1) return;
/* 646 */     getSorter(a).quickSort(fromIndex, toIndex - 1);
/*     */   }
/*     */ 
/*     */   public static <T extends Comparable<? super T>> void quickSort(T[] a)
/*     */   {
/* 654 */     quickSort(a, 0, a.length);
/*     */   }
/*     */ 
/*     */   public static <T> void mergeSort(T[] a, int fromIndex, int toIndex, Comparator<? super T> comp)
/*     */   {
/* 666 */     if (toIndex - fromIndex <= 1) return;
/*     */ 
/* 668 */     getSorter(a, comp).mergeSort(fromIndex, toIndex - 1);
/*     */   }
/*     */ 
/*     */   public static <T> void mergeSort(T[] a, Comparator<? super T> comp)
/*     */   {
/* 676 */     mergeSort(a, 0, a.length, comp);
/*     */   }
/*     */ 
/*     */   public static <T extends Comparable<? super T>> void mergeSort(T[] a, int fromIndex, int toIndex)
/*     */   {
/* 686 */     if (toIndex - fromIndex <= 1) return;
/* 687 */     getSorter(a).mergeSort(fromIndex, toIndex - 1);
/*     */   }
/*     */ 
/*     */   public static <T extends Comparable<? super T>> void mergeSort(T[] a)
/*     */   {
/* 695 */     mergeSort(a, 0, a.length);
/*     */   }
/*     */ 
/*     */   public static <T> void insertionSort(T[] a, int fromIndex, int toIndex, Comparator<? super T> comp)
/*     */   {
/* 707 */     if (toIndex - fromIndex <= 1) return;
/* 708 */     getSorter(a, comp).insertionSort(fromIndex, toIndex - 1);
/*     */   }
/*     */ 
/*     */   public static <T> void insertionSort(T[] a, Comparator<? super T> comp)
/*     */   {
/* 716 */     insertionSort(a, 0, a.length, comp);
/*     */   }
/*     */ 
/*     */   public static <T extends Comparable<? super T>> void insertionSort(T[] a, int fromIndex, int toIndex)
/*     */   {
/* 726 */     if (toIndex - fromIndex <= 1) return;
/* 727 */     getSorter(a).insertionSort(fromIndex, toIndex - 1);
/*     */   }
/*     */ 
/*     */   public static <T extends Comparable<? super T>> void insertionSort(T[] a)
/*     */   {
/* 735 */     insertionSort(a, 0, a.length);
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.util.ArrayUtil
 * JD-Core Version:    0.6.0
 */