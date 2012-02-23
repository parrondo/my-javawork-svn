/*    */ package com.dukascopy.dds2.greed.util;
/*    */ 
/*    */ import java.util.Comparator;
/*    */ import java.util.HashSet;
/*    */ import java.util.Set;
/*    */ 
/*    */ public class AlternativeInstrumentComparator
/*    */   implements Comparator<String>
/*    */ {
/*    */   private static final int MAGNIFIER = 20;
/*    */   private static final String CHF = "CHF";
/*    */   private static final String JPY = "JPY";
/*    */   private static final String GBP = "GBP";
/*    */   private static final String USD = "USD";
/*    */   private static final String EUR = "EUR";
/* 17 */   static Set<String> usdLess = new HashSet();
/* 18 */   static Set<String> gbpLess = new HashSet();
/* 19 */   static Set<String> jpyLess = new HashSet();
/* 20 */   static Set<String> chfLess = new HashSet();
/* 21 */   static Set<String> leftLess = new HashSet();
/*    */ 
/*    */   public int compare(String pair1, String pair2)
/*    */   {
/* 32 */     if (pair1.equals(pair2)) {
/* 33 */       return 0;
/*    */     }
/* 35 */     String[] pair1s = pair1.split("/");
/* 36 */     String[] pair2s = pair2.split("/");
/*    */ 
/* 38 */     int result = 2147483647;
/*    */ 
/* 40 */     if ((pair1.startsWith("EUR")) && (pair2.startsWith("EUR"))) {
/* 41 */       result = 20 * pair1.compareTo(pair2);
/*    */     } else {
/* 43 */       if (pair1.startsWith("EUR")) {
/* 44 */         result = -2147483648;
/*    */       }
/* 46 */       if ((pair1.startsWith("USD")) && (pair2.startsWith("USD"))) {
/* 47 */         result = 20 * pair1.compareTo(pair2);
/*    */       } else {
/* 49 */         if ((pair1.startsWith("USD")) && (usdLess.contains(pair2s[0])))
/* 50 */           result = Math.abs(pair1s[0].compareTo(pair2s[0]));
/* 51 */         else if (pair1.startsWith("USD")) {
/* 52 */           result = -4000;
/*    */         }
/* 54 */         if ((pair1.startsWith("GBP")) && (pair2.startsWith("GBP"))) {
/* 55 */           result = 20 * pair1.compareTo(pair2);
/*    */         } else {
/* 57 */           if ((pair1.startsWith("GBP")) && (gbpLess.contains(pair2s[0])))
/* 58 */             result = Math.abs(pair1s[0].compareTo(pair2s[0]));
/* 59 */           else if (pair1.startsWith("GBP")) {
/* 60 */             result = -3000;
/*    */           }
/* 62 */           if ((pair1.startsWith("JPY")) && (pair2.startsWith("JPY"))) {
/* 63 */             result = 20 * pair1.compareTo(pair2);
/*    */           } else {
/* 65 */             if ((pair1.startsWith("JPY")) && (jpyLess.contains(pair2s[0])))
/* 66 */               result = Math.abs(pair1s[0].compareTo(pair2s[0]));
/* 67 */             else if (pair1.startsWith("JPY")) {
/* 68 */               result = -2000;
/*    */             }
/* 70 */             if ((pair1.startsWith("CHF")) && (pair2.startsWith("CHF"))) {
/* 71 */               result = 20 * pair1.compareTo(pair2);
/*    */             }
/* 73 */             else if ((pair1.startsWith("CHF")) && (chfLess.contains(pair2s[0])))
/* 74 */               result = Math.abs(pair1s[0].compareTo(pair2s[0]));
/* 75 */             else if (pair1.startsWith("CHF")) {
/* 76 */               result = -1000;
/*    */             }
/*    */           }
/*    */         }
/*    */       }
/*    */     }
/*    */ 
/* 83 */     if (result == 2147483647) {
/* 84 */       if ((!leftLess.contains(pair1s[0])) && (leftLess.contains(pair2s[0])))
/* 85 */         result = 1000;
/*    */       else {
/* 87 */         result = pair1.compareTo(pair2);
/*    */       }
/*    */     }
/* 90 */     return result;
/*    */   }
/*    */ 
/*    */   static
/*    */   {
/* 24 */     usdLess.add("EUR");
/* 25 */     gbpLess.addAll(usdLess); gbpLess.add("USD");
/* 26 */     jpyLess.addAll(gbpLess); jpyLess.add("GBP");
/* 27 */     chfLess.addAll(jpyLess); chfLess.add("JPY");
/* 28 */     leftLess.addAll(chfLess); leftLess.add("CHF");
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.util.AlternativeInstrumentComparator
 * JD-Core Version:    0.6.0
 */