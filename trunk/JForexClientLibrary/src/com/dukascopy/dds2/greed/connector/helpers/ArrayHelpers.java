/*     */ package com.dukascopy.dds2.greed.connector.helpers;
/*     */ 
/*     */ import java.lang.reflect.Array;
/*     */ import java.util.ArrayList;
/*     */ 
/*     */ public class ArrayHelpers
/*     */ {
/*  10 */   public static final double[] EMPTY_DOUBLE_ARRAY = new double[0];
/*     */ 
/*  14 */   public static final Double[] EMPTY_DOUBLE_OBJECT_ARRAY = new Double[0];
/*     */ 
/*     */   public static int ArrayElementSize(Object array)
/*     */   {
/*  18 */     int result = 0;
/*  19 */     if (array.getClass().isArray()) {
/*  20 */       Class c = array.getClass().getComponentType();
/*  21 */       if (c.equals(Integer.TYPE))
/*  22 */         result = 32;
/*  23 */       else if (c.equals(Byte.TYPE))
/*  24 */         result = 8;
/*  25 */       else if (c.equals(Long.TYPE))
/*  26 */         result = 64;
/*  27 */       else if (c.equals(Double.TYPE))
/*  28 */         result = 64;
/*  29 */       else if (c.equals(Float.TYPE)) {
/*  30 */         result = 32;
/*     */       }
/*     */     }
/*  33 */     return result;
/*     */   }
/*     */   public static Class ArrayElementType(Object array) {
/*  36 */     if (array.getClass().isArray()) {
/*  37 */       return array.getClass().getComponentType();
/*     */     }
/*  39 */     return null;
/*     */   }
/*     */ 
/*     */   public static double[] toPrimitive(Double[] array)
/*     */   {
/*  54 */     if (array == null)
/*  55 */       return null;
/*  56 */     if (array.length == 0) {
/*  57 */       return EMPTY_DOUBLE_ARRAY;
/*     */     }
/*  59 */     double[] result = new double[array.length];
/*  60 */     for (int i = 0; i < array.length; i++) {
/*  61 */       result[i] = array[i].doubleValue();
/*     */     }
/*  63 */     return result;
/*     */   }
/*     */ 
/*     */   public static double[] toPrimitive(Double[] array, double valueForNull)
/*     */   {
/*  76 */     if (array == null)
/*  77 */       return null;
/*  78 */     if (array.length == 0) {
/*  79 */       return EMPTY_DOUBLE_ARRAY;
/*     */     }
/*  81 */     double[] result = new double[array.length];
/*  82 */     for (int i = 0; i < array.length; i++) {
/*  83 */       Double b = array[i];
/*  84 */       result[i] = (b == null ? valueForNull : b.doubleValue());
/*     */     }
/*  86 */     return result;
/*     */   }
/*     */ 
/*     */   public static Double[] toObject(double[] array)
/*     */   {
/*  98 */     if (array == null)
/*  99 */       return null;
/* 100 */     if (array.length == 0) {
/* 101 */       return EMPTY_DOUBLE_OBJECT_ARRAY;
/*     */     }
/* 103 */     Double[] result = new Double[array.length];
/* 104 */     for (int i = 0; i < array.length; i++) {
/* 105 */       result[i] = new Double(array[i]);
/*     */     }
/* 107 */     return result;
/*     */   }
/*     */ 
/*     */   public static int[] ArrayDimensionInfo(Object array) {
/* 111 */     ArrayList dimSize = new ArrayList();
/* 112 */     int dim = 0;
/* 113 */     Class c = array.getClass();
/* 114 */     Object item = array;
/* 115 */     while (c.isArray()) {
/* 116 */       int size = Array.getLength(item);
/* 117 */       dimSize.add(Integer.valueOf(size));
/* 118 */       c = c.getComponentType();
/* 119 */       dim++;
/* 120 */       if (size <= 0) break;
/* 121 */       item = Array.get(item, 0);
/*     */     }
/*     */ 
/* 126 */     int[] info = new int[dimSize.size()];
/* 127 */     info[0] = dim;
/* 128 */     for (int i = 0; i < info.length; i++) {
/* 129 */       info[i] = ((Integer)dimSize.get(i)).intValue();
/*     */     }
/* 131 */     return info;
/*     */   }
/*     */ 
/*     */   public static Object resizeArray(Object oldArray, int newSize)
/*     */   {
/* 142 */     Object newArray = null;
/* 143 */     if (oldArray != null) {
/* 144 */       int oldSize = Array.getLength(oldArray);
/* 145 */       Class elementType = oldArray.getClass().getComponentType();
/* 146 */       newArray = Array.newInstance(elementType, newSize);
/* 147 */       int preserveLength = Math.min(oldSize, newSize);
/* 148 */       if (preserveLength > 0) {
/* 149 */         System.arraycopy(oldArray, 0, newArray, 0, preserveLength);
/*     */       }
/*     */     }
/* 152 */     return newArray;
/*     */   }
/*     */ 
/*     */   public static int[] reverse(int[] a)
/*     */   {
/* 164 */     int[] rev = new int[a.length];
/*     */ 
/* 170 */     for (int i = 0; i < a.length; i++) {
/* 171 */       rev[i] = a[(a.length - i - 1)];
/*     */     }
/* 173 */     return rev;
/*     */   }
/*     */ 
/*     */   public static int ArraySize(Object array)
/*     */   {
/* 182 */     int rc = 0;
/* 183 */     if (array != null) {
/* 184 */       Class c = array.getClass();
/* 185 */       Object item = array;
/* 186 */       if (c.isArray()) {
/* 187 */         rc += Array.getLength(item);
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 195 */     return rc;
/*     */   }
/*     */ 
/*     */   protected int ArrayInitialize(Object obj, double value) {
/* 199 */     int[] dim = ArrayDimensionInfo(obj);
/* 200 */     int[] count = new int[dim.length];
/* 201 */     int[] index = new int[dim.length];
/* 202 */     for (int i = 0; i < count.length; i++) {
/* 203 */       index[i] = 0;
/* 204 */       count[i] = dim[i];
/*     */     }
/* 206 */     for (int i = 0; i < dim.length; i++) {
/* 207 */       Object arr = Array.get(obj, i);
/* 208 */       Array.set(obj, i, Double.valueOf(value));
/*     */     }
/* 210 */     return dim[0];
/*     */   }
/*     */ 
/*     */   public static int binarySearch(String[] array, String item)
/*     */   {
/* 215 */     int result = -1;
/* 216 */     int index = 0;
/* 217 */     for (String element : array) {
/* 218 */       if (element.equals(item)) {
/* 219 */         result = index;
/* 220 */         break;
/*     */       }
/* 222 */       index++;
/*     */     }
/* 224 */     return result;
/*     */   }
/*     */ 
/*     */   public static boolean isIdentical(int[] array, int[] compare)
/*     */   {
/* 233 */     boolean result = false;
/* 234 */     if ((array != null) && (compare != null) && (array.length == compare.length)) {
/* 235 */       result = true;
/* 236 */       for (int i = 0; i < array.length; i++) {
/* 237 */         if ((array[i] > 0) && (compare[i] < 0)) {
/* 238 */           result = false;
/* 239 */           break;
/*     */         }
/* 241 */         if ((array[i] < 0) && (compare[i] > 0)) {
/* 242 */           result = false;
/* 243 */           break;
/*     */         }
/*     */       }
/*     */     }
/* 247 */     return result;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Connector-1.1.49.jar
 * Qualified Name:     com.dukascopy.dds2.greed.connector.helpers.ArrayHelpers
 * JD-Core Version:    0.6.0
 */