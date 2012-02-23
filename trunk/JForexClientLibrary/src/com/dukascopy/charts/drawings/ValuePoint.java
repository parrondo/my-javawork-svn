/*    */ package com.dukascopy.charts.drawings;
/*    */ 
/*    */ import java.io.Serializable;
/*    */ import java.text.SimpleDateFormat;
/*    */ import java.util.TimeZone;
/*    */ 
/*    */ public final class ValuePoint
/*    */   implements Serializable
/*    */ {
/* 14 */   private static final long serialVersionUID = 1L;
/* 14 */   private static final SimpleDateFormat FORMATTER = new SimpleDateFormat() {  } ;
/*    */   public long time;
/*    */   public double value;
/*    */ 
/*    */   public ValuePoint() {
/*    */   }
/*    */ 
/*    */   public ValuePoint(long time, double value) {
/* 24 */     set(time, value);
/*    */   }
/*    */ 
/*    */   public void set(long time, double value) {
/* 28 */     this.time = time;
/* 29 */     this.value = value;
/*    */   }
/*    */ 
/*    */   public final void copy(ValuePoint src) {
/* 33 */     this.time = src.time;
/* 34 */     this.value = src.value;
/*    */   }
/*    */ 
/*    */   public boolean isValid() {
/* 38 */     return isValid(this.time, this.value);
/*    */   }
/*    */ 
/*    */   public static boolean isValid(long time, double value) {
/* 42 */     return (isValidTime(time)) && (isValidValue(value));
/*    */   }
/*    */ 
/*    */   public static boolean isValidValue(double value) {
/* 46 */     return value != -1.0D;
/*    */   }
/*    */ 
/*    */   public static boolean isValidTime(long time) {
/* 50 */     return time != -1L;
/*    */   }
/*    */ 
/*    */   public String toString()
/*    */   {
/* 55 */     return "ValuePoint(" + FORMATTER.format(Long.valueOf(this.time)) + ", " + this.value + ")";
/*    */   }
/*    */ 
/*    */   public final boolean equals(Object other)
/*    */   {
/* 60 */     if (!(other instanceof ValuePoint)) {
/* 61 */       return false;
/*    */     }
/*    */ 
/* 64 */     ValuePoint another = (ValuePoint)other;
/* 65 */     if (this.time != another.time) {
/* 66 */       return false;
/*    */     }
/*    */ 
/* 70 */     return this.value == another.value;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Charts-5.48.jar
 * Qualified Name:     com.dukascopy.charts.drawings.ValuePoint
 * JD-Core Version:    0.6.0
 */