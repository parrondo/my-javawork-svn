/*     */ package org.apache.lucene.document;
/*     */ 
/*     */ import java.text.ParseException;
/*     */ import java.text.SimpleDateFormat;
/*     */ import java.util.Calendar;
/*     */ import java.util.Date;
/*     */ import java.util.Locale;
/*     */ import java.util.TimeZone;
/*     */ 
/*     */ public class DateTools
/*     */ {
/*  55 */   static final TimeZone GMT = TimeZone.getTimeZone("GMT");
/*     */ 
/*  57 */   private static final ThreadLocal<Calendar> TL_CAL = new ThreadLocal()
/*     */   {
/*     */     protected Calendar initialValue() {
/*  60 */       return Calendar.getInstance(DateTools.GMT, Locale.US);
/*     */     }
/*  57 */   };
/*     */ 
/*  65 */   private static final ThreadLocal<SimpleDateFormat[]> TL_FORMATS = new ThreadLocal()
/*     */   {
/*     */     protected SimpleDateFormat[] initialValue() {
/*  68 */       SimpleDateFormat[] arr = new SimpleDateFormat[DateTools.Resolution.MILLISECOND.formatLen + 1];
/*  69 */       for (DateTools.Resolution resolution : DateTools.Resolution.values()) {
/*  70 */         arr[resolution.formatLen] = ((SimpleDateFormat)resolution.format.clone());
/*     */       }
/*  72 */       return arr;
/*     */     }
/*  65 */   };
/*     */ 
/*     */   public static String dateToString(Date date, Resolution resolution)
/*     */   {
/*  89 */     return timeToString(date.getTime(), resolution);
/*     */   }
/*     */ 
/*     */   public static String timeToString(long time, Resolution resolution)
/*     */   {
/* 102 */     Date date = new Date(round(time, resolution));
/* 103 */     return ((SimpleDateFormat[])TL_FORMATS.get())[resolution.formatLen].format(date);
/*     */   }
/*     */ 
/*     */   public static long stringToTime(String dateString)
/*     */     throws ParseException
/*     */   {
/* 117 */     return stringToDate(dateString).getTime();
/*     */   }
/*     */ 
/*     */   public static Date stringToDate(String dateString)
/*     */     throws ParseException
/*     */   {
/*     */     try
/*     */     {
/* 132 */       return ((SimpleDateFormat[])TL_FORMATS.get())[dateString.length()].parse(dateString); } catch (Exception e) {
/*     */     }
/* 134 */     throw new ParseException("Input is not a valid date string: " + dateString, 0);
/*     */   }
/*     */ 
/*     */   public static Date round(Date date, Resolution resolution)
/*     */   {
/* 148 */     return new Date(round(date.getTime(), resolution));
/*     */   }
/*     */ 
/*     */   public static long round(long time, Resolution resolution)
/*     */   {
/* 163 */     Calendar calInstance = (Calendar)TL_CAL.get();
/* 164 */     calInstance.setTimeInMillis(time);
/*     */ 
/* 166 */     switch (3.$SwitchMap$org$apache$lucene$document$DateTools$Resolution[resolution.ordinal()])
/*     */     {
/*     */     case 1:
/* 169 */       calInstance.set(2, 0);
/*     */     case 2:
/* 171 */       calInstance.set(5, 1);
/*     */     case 3:
/* 173 */       calInstance.set(11, 0);
/*     */     case 4:
/* 175 */       calInstance.set(12, 0);
/*     */     case 5:
/* 177 */       calInstance.set(13, 0);
/*     */     case 6:
/* 179 */       calInstance.set(14, 0);
/*     */     case 7:
/* 182 */       break;
/*     */     default:
/* 184 */       throw new IllegalArgumentException("unknown resolution " + resolution);
/*     */     }
/* 186 */     return calInstance.getTimeInMillis();
/*     */   }
/*     */ 
/*     */   public static enum Resolution
/*     */   {
/* 192 */     YEAR(4), MONTH(6), DAY(8), HOUR(10), MINUTE(12), SECOND(14), MILLISECOND(17);
/*     */ 
/*     */     final int formatLen;
/*     */     final SimpleDateFormat format;
/*     */ 
/* 198 */     private Resolution(int formatLen) { this.formatLen = formatLen;
/*     */ 
/* 201 */       this.format = new SimpleDateFormat("yyyyMMddHHmmssSSS".substring(0, formatLen), Locale.US);
/* 202 */       this.format.setTimeZone(DateTools.GMT);
/*     */     }
/*     */ 
/*     */     public String toString()
/*     */     {
/* 209 */       return super.toString().toLowerCase(Locale.ENGLISH);
/*     */     }
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.document.DateTools
 * JD-Core Version:    0.6.0
 */