/*     */ package com.dukascopy.dds2.greed.connector.helpers;
/*     */ 
/*     */ import java.text.DecimalFormat;
/*     */ import java.text.DecimalFormatSymbols;
/*     */ import java.util.Arrays;
/*     */ 
/*     */ public class MathHelpers
/*     */ {
/*     */   public static long factorial(int n)
/*     */   {
/*  17 */     return n == 0 ? 1L : n * factorial(n - 1);
/*     */   }
/*     */ 
/*     */   public static int fibonacci(int n)
/*     */   {
/*  34 */     return n <= 2 ? 1 : fibonacci(n - 1) + fibonacci(n - 2);
/*     */   }
/*     */ 
/*     */   public static double strToDouble(String str) {
/*  38 */     double result = (0.0D / 0.0D);
/*  39 */     if ((str != null) && (!str.isEmpty())) {
/*  40 */       char c = CommonHelpers.dfPrint.getDecimalFormatSymbols().getGroupingSeparator();
/*  41 */       str = str.replace(c, ',');
/*  42 */       str = str.replaceAll(" ", "");
/*  43 */       str = str.replaceAll(",", "");
/*  44 */       result = Double.parseDouble(str);
/*     */     }
/*  46 */     return result;
/*     */   }
/*     */ 
/*     */   public static double normalizeDouble(double value, int precision) {
/*  50 */     boolean negative = false;
/*  51 */     if (value < 0.0D) {
/*  52 */       negative = true;
/*  53 */       value = -value;
/*     */     }
/*  55 */     if (value == 0.0D) {
/*  56 */       return value;
/*     */     }
/*  58 */     if (value != value) {
/*  59 */       return 0.0D;
/*     */     }
/*  61 */     double multiplier = 1.0D;
/*  62 */     while (precision > 0) {
/*  63 */       multiplier *= 10.0D;
/*  64 */       precision--;
/*     */     }
/*  66 */     while ((precision < 0) && (value * multiplier / 10.0D >= 1.0D)) {
/*  67 */       multiplier /= 10.0D;
/*  68 */       precision++;
/*     */     }
/*  70 */     while (value * multiplier < 1.0D) {
/*  71 */       multiplier *= 10.0D;
/*     */     }
/*  73 */     value *= multiplier;
/*  74 */     long longValue = ()(value + 0.5D);
/*  75 */     value = longValue / multiplier;
/*  76 */     return negative ? -value : value;
/*     */   }
/*     */ 
/*     */   public static long sumN(int n)
/*     */   {
/*  85 */     return n == 0 ? 0L : n + sumN(n - 1);
/*     */   }
/*     */ 
/*     */   public static int[][] elementArray(int n)
/*     */   {
/*  94 */     int size = (int)sumN(n);
/*  95 */     int[][] result = new int[size][n];
/*  96 */     for (int i = 0; i < size; i++) {
/*  97 */       if (i < n) {
/*  98 */         for (int j = 0; j < n; i++) {
/*  99 */           int[] item = new int[n];
/* 100 */           Arrays.fill(item, -1);
/* 101 */           item[j] = 1;
/* 102 */           result[i] = item;
/*     */ 
/*  98 */           j++;
/*     */         }
/*     */ 
/* 104 */         if (i < result.length)
/* 105 */           Arrays.fill(result[i], 1);
/*     */       }
/*     */       else {
/* 108 */         int j = 2; for (int k = 0; j < n; j++) {
/* 109 */           for (int pos = 0; pos < n / 2; pos++) {
/* 110 */             int[] item = new int[n];
/* 111 */             Arrays.fill(item, -1);
/* 112 */             while (k < j) {
/* 113 */               if (pos + k < item.length - 1) {
/* 114 */                 item[(pos + k)] = 1;
/*     */               }
/* 116 */               k++;
/*     */             }
/* 118 */             if (i < size) {
/* 119 */               result[(i++)] = item;
/*     */             }
/* 121 */             int[] reverseItem = ArrayHelpers.reverse(item);
/* 122 */             if ((!ArrayHelpers.isIdentical(item, reverseItem)) && 
/* 123 */               (i < size)) {
/* 124 */               result[(i++)] = reverseItem;
/*     */             }
/*     */ 
/* 127 */             k = 0;
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/* 132 */     return result;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Connector-1.1.49.jar
 * Qualified Name:     com.dukascopy.dds2.greed.connector.helpers.MathHelpers
 * JD-Core Version:    0.6.0
 */