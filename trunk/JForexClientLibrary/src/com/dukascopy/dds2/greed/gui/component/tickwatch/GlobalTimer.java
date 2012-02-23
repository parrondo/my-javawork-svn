/*    */ package com.dukascopy.dds2.greed.gui.component.tickwatch;
/*    */ 
/*    */ import java.util.GregorianCalendar;
/*    */ import java.util.SimpleTimeZone;
/*    */ 
/*    */ public class GlobalTimer
/*    */ {
/*    */   private GregorianCalendar calendar;
/*    */ 
/*    */   public GlobalTimer()
/*    */   {
/* 13 */     SimpleTimeZone zone = new SimpleTimeZone(0, "GMT");
/* 14 */     this.calendar = new GregorianCalendar(zone);
/*    */   }
/*    */ 
/*    */   public int getHourOfDay(long ctime)
/*    */   {
/* 19 */     this.calendar.setTimeInMillis(ctime);
/* 20 */     int dhour = this.calendar.get(11);
/* 21 */     return dhour;
/*    */   }
/*    */ 
/*    */   public int getMinutesOfHour(long ctime)
/*    */   {
/* 26 */     this.calendar.setTimeInMillis(ctime);
/* 27 */     int dmin = this.calendar.get(12);
/* 28 */     return dmin;
/*    */   }
/*    */ 
/*    */   public String getDayOfWeek(long ctime)
/*    */   {
/* 33 */     this.calendar.setTimeInMillis(ctime);
/* 34 */     int dofw = this.calendar.get(7);
/*    */     String sdofw;
/* 37 */     switch (dofw) {
/*    */     case 1:
/* 39 */       sdofw = "SUN";
/* 40 */       break;
/*    */     case 2:
/* 42 */       sdofw = "MON";
/* 43 */       break;
/*    */     case 3:
/* 45 */       sdofw = "TUE";
/* 46 */       break;
/*    */     case 4:
/* 48 */       sdofw = "WED";
/* 49 */       break;
/*    */     case 5:
/* 51 */       sdofw = "THU";
/* 52 */       break;
/*    */     case 6:
/* 54 */       sdofw = "FRI";
/* 55 */       break;
/*    */     case 7:
/* 57 */       sdofw = "SAT";
/* 58 */       break;
/*    */     default:
/* 60 */       sdofw = "XYZ";
/*    */     }
/*    */ 
/* 64 */     return sdofw;
/*    */   }
/*    */ 
/*    */   public int getSeconds(long time)
/*    */   {
/* 69 */     this.calendar.setTimeInMillis(time);
/* 70 */     return this.calendar.get(13);
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.tickwatch.GlobalTimer
 * JD-Core Version:    0.6.0
 */