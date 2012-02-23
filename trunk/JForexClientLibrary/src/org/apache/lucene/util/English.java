/*     */ package org.apache.lucene.util;
/*     */ 
/*     */ import java.io.PrintStream;
/*     */ 
/*     */ public final class English
/*     */ {
/*     */   public static String longToEnglish(long i)
/*     */   {
/*  28 */     StringBuilder result = new StringBuilder();
/*  29 */     longToEnglish(i, result);
/*  30 */     return result.toString();
/*     */   }
/*     */ 
/*     */   public static void longToEnglish(long i, StringBuilder result) {
/*  34 */     if (i == 0L) {
/*  35 */       result.append("zero");
/*  36 */       return;
/*     */     }
/*  38 */     if (i < 0L) {
/*  39 */       result.append("minus ");
/*  40 */       i = -i;
/*     */     }
/*  42 */     if (i >= 1000000000000000000L) {
/*  43 */       longToEnglish(i / 1000000000000000000L, result);
/*  44 */       result.append("quintillion, ");
/*  45 */       i %= 1000000000000000000L;
/*     */     }
/*  47 */     if (i >= 1000000000000000L) {
/*  48 */       longToEnglish(i / 1000000000000000L, result);
/*  49 */       result.append("quadrillion, ");
/*  50 */       i %= 1000000000000000L;
/*     */     }
/*  52 */     if (i >= 1000000000000L) {
/*  53 */       longToEnglish(i / 1000000000000L, result);
/*  54 */       result.append("trillion, ");
/*  55 */       i %= 1000000000000L;
/*     */     }
/*  57 */     if (i >= 1000000000L) {
/*  58 */       longToEnglish(i / 1000000000L, result);
/*  59 */       result.append("billion, ");
/*  60 */       i %= 1000000000L;
/*     */     }
/*  62 */     if (i >= 1000000L) {
/*  63 */       longToEnglish(i / 1000000L, result);
/*  64 */       result.append("million, ");
/*  65 */       i %= 1000000L;
/*     */     }
/*  67 */     if (i >= 1000L) {
/*  68 */       longToEnglish(i / 1000L, result);
/*  69 */       result.append("thousand, ");
/*  70 */       i %= 1000L;
/*     */     }
/*  72 */     if (i >= 100L) {
/*  73 */       longToEnglish(i / 100L, result);
/*  74 */       result.append("hundred ");
/*  75 */       i %= 100L;
/*     */     }
/*     */ 
/*  78 */     if (i >= 20L) {
/*  79 */       switch ((int)i / 10) {
/*     */       case 9:
/*  81 */         result.append("ninety");
/*  82 */         break;
/*     */       case 8:
/*  84 */         result.append("eighty");
/*  85 */         break;
/*     */       case 7:
/*  87 */         result.append("seventy");
/*  88 */         break;
/*     */       case 6:
/*  90 */         result.append("sixty");
/*  91 */         break;
/*     */       case 5:
/*  93 */         result.append("fifty");
/*  94 */         break;
/*     */       case 4:
/*  96 */         result.append("forty");
/*  97 */         break;
/*     */       case 3:
/*  99 */         result.append("thirty");
/* 100 */         break;
/*     */       case 2:
/* 102 */         result.append("twenty");
/*     */       }
/*     */ 
/* 105 */       i %= 10L;
/* 106 */       if (i == 0L)
/* 107 */         result.append(" ");
/*     */       else
/* 109 */         result.append("-");
/*     */     }
/* 111 */     switch ((int)i) {
/*     */     case 19:
/* 113 */       result.append("nineteen ");
/* 114 */       break;
/*     */     case 18:
/* 116 */       result.append("eighteen ");
/* 117 */       break;
/*     */     case 17:
/* 119 */       result.append("seventeen ");
/* 120 */       break;
/*     */     case 16:
/* 122 */       result.append("sixteen ");
/* 123 */       break;
/*     */     case 15:
/* 125 */       result.append("fifteen ");
/* 126 */       break;
/*     */     case 14:
/* 128 */       result.append("fourteen ");
/* 129 */       break;
/*     */     case 13:
/* 131 */       result.append("thirteen ");
/* 132 */       break;
/*     */     case 12:
/* 134 */       result.append("twelve ");
/* 135 */       break;
/*     */     case 11:
/* 137 */       result.append("eleven ");
/* 138 */       break;
/*     */     case 10:
/* 140 */       result.append("ten ");
/* 141 */       break;
/*     */     case 9:
/* 143 */       result.append("nine ");
/* 144 */       break;
/*     */     case 8:
/* 146 */       result.append("eight ");
/* 147 */       break;
/*     */     case 7:
/* 149 */       result.append("seven ");
/* 150 */       break;
/*     */     case 6:
/* 152 */       result.append("six ");
/* 153 */       break;
/*     */     case 5:
/* 155 */       result.append("five ");
/* 156 */       break;
/*     */     case 4:
/* 158 */       result.append("four ");
/* 159 */       break;
/*     */     case 3:
/* 161 */       result.append("three ");
/* 162 */       break;
/*     */     case 2:
/* 164 */       result.append("two ");
/* 165 */       break;
/*     */     case 1:
/* 167 */       result.append("one ");
/* 168 */       break;
/*     */     case 0:
/* 170 */       result.append("");
/*     */     }
/*     */   }
/*     */ 
/*     */   public static String intToEnglish(int i)
/*     */   {
/* 177 */     StringBuilder result = new StringBuilder();
/* 178 */     longToEnglish(i, result);
/* 179 */     return result.toString();
/*     */   }
/*     */ 
/*     */   public static void intToEnglish(int i, StringBuilder result) {
/* 183 */     longToEnglish(i, result);
/*     */   }
/*     */ 
/*     */   public static void main(String[] args) {
/* 187 */     System.out.println(longToEnglish(Long.parseLong(args[0])));
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.util.English
 * JD-Core Version:    0.6.0
 */