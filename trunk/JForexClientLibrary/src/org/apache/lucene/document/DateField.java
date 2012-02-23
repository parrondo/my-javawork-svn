/*     */ package org.apache.lucene.document;
/*     */ 
/*     */ import java.util.Date;
/*     */ 
/*     */ @Deprecated
/*     */ public class DateField
/*     */ {
/*  66 */   private static int DATE_LEN = Long.toString(31536000000000L, 36).length();
/*     */ 
/*     */   public static String MIN_DATE_STRING()
/*     */   {
/*  70 */     return timeToString(0L);
/*     */   }
/*     */ 
/*     */   public static String MAX_DATE_STRING() {
/*  74 */     char[] buffer = new char[DATE_LEN];
/*  75 */     char c = Character.forDigit(35, 36);
/*  76 */     for (int i = 0; i < DATE_LEN; i++)
/*  77 */       buffer[i] = c;
/*  78 */     return new String(buffer);
/*     */   }
/*     */ 
/*     */   public static String dateToString(Date date)
/*     */   {
/*  87 */     return timeToString(date.getTime());
/*     */   }
/*     */ 
/*     */   public static String timeToString(long time)
/*     */   {
/*  95 */     if (time < 0L) {
/*  96 */       throw new RuntimeException("time '" + time + "' is too early, must be >= 0");
/*     */     }
/*  98 */     String s = Long.toString(time, 36);
/*     */ 
/* 100 */     if (s.length() > DATE_LEN) {
/* 101 */       throw new RuntimeException("time '" + time + "' is too late, length of string " + "representation must be <= " + DATE_LEN);
/*     */     }
/*     */ 
/* 105 */     if (s.length() < DATE_LEN) {
/* 106 */       StringBuilder sb = new StringBuilder(s);
/* 107 */       while (sb.length() < DATE_LEN)
/* 108 */         sb.insert(0, 0);
/* 109 */       s = sb.toString();
/*     */     }
/*     */ 
/* 112 */     return s;
/*     */   }
/*     */ 
/*     */   public static long stringToTime(String s)
/*     */   {
/* 117 */     return Long.parseLong(s, 36);
/*     */   }
/*     */ 
/*     */   public static Date stringToDate(String s) {
/* 121 */     return new Date(stringToTime(s));
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.document.DateField
 * JD-Core Version:    0.6.0
 */