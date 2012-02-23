/*      */ package com.dukascopy.charts.data.datacache;
/*      */ 
/*      */ import com.dukascopy.api.Instrument;
/*      */ import com.dukascopy.api.OfferSide;
/*      */ import com.dukascopy.api.Period;
/*      */ import com.dukascopy.api.Unit;
/*      */ import com.dukascopy.dds2.greed.agent.strategy.StratUtils;
/*      */ import com.dukascopy.dds2.greed.util.FilePathManager;
/*      */ import com.dukascopy.transport.util.MD5;
/*      */ import java.io.File;
/*      */ import java.io.FileFilter;
/*      */ import java.text.DateFormat;
/*      */ import java.text.SimpleDateFormat;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Calendar;
/*      */ import java.util.Date;
/*      */ import java.util.List;
/*      */ import java.util.TimeZone;
/*      */ import org.slf4j.Logger;
/*      */ import org.slf4j.LoggerFactory;
/*      */ 
/*      */ public class DataCacheUtils
/*      */ {
/*   28 */   protected static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");
/*      */   private static final DateFormat intraPeriodFileDateFormat;
/*   34 */   private static final ThreadLocal<Calendar> gmtCalendarThreadLocal = new ThreadLocal() {
/*      */     protected Calendar initialValue() {
/*   36 */       return Calendar.getInstance(TimeZone.getTimeZone("GMT"));
/*      */     }
/*   34 */   };
/*      */ 
/*   40 */   protected static Logger LOGGER = LoggerFactory.getLogger(DataCacheUtils.class);
/*      */   public static final String ORDERS_FILE_PREFIX = "o_";
/*      */ 
/*      */   public DataCacheUtils()
/*      */   {
/*   30 */     DATE_FORMAT.setTimeZone(TimeZone.getTimeZone("GMT"));
/*      */   }
/*      */ 
/*      */   public static boolean isCandleBasic(Period period)
/*      */     throws DataCacheException
/*      */   {
/*   50 */     if (period == Period.TICK) {
/*   51 */       throw new DataCacheException("Tick period can't be candle");
/*      */     }
/*   53 */     Period basicPeriod = Period.isPeriodBasic(period);
/*   54 */     return basicPeriod != null;
/*      */   }
/*      */ 
/*      */   public static boolean isCandleBasicFast(Period period) {
/*   58 */     if (period == Period.TICK) {
/*   59 */       return false;
/*      */     }
/*   61 */     Period basicPeriod = Period.isPeriodBasic(period);
/*   62 */     return basicPeriod != null;
/*      */   }
/*      */ 
/*      */   public static boolean isIntervalValid(Period period, long from, long to) throws DataCacheException {
/*   66 */     if (period == Period.TICK) {
/*   67 */       return true;
/*      */     }
/*   69 */     if (period == null) {
/*   70 */       throw new DataCacheException("Period is null");
/*      */     }
/*   72 */     switch (3.$SwitchMap$com$dukascopy$api$Unit[period.getUnit().ordinal()]) {
/*      */     case 1:
/*   74 */       throw new DataCacheException("No intervals with Millisecond time unit allowed except TICK");
/*      */     case 2:
/*      */     case 3:
/*      */     case 4:
/*   78 */       if ((to - from != 0L) && (to - from < period.getInterval())) break;
/*   79 */       long dayStart = getClosestDayStartBefore(from);
/*   80 */       if ((from - dayStart) % period.getInterval() == 0L) {
/*   81 */         dayStart = getClosestDayStartBefore(to);
/*   82 */         if ((to - dayStart) % period.getInterval() == 0L) {
/*   83 */           return true;
/*      */         }
/*      */       }
/*   86 */       break;
/*      */     case 5:
/*   89 */       if ((to - from != 0L) && (to - from < period.getInterval())) break;
/*   90 */       long dayStart = getClosestDayStartBefore(from);
/*   91 */       if (dayStart == from) {
/*   92 */         dayStart = getClosestDayStartBefore(to);
/*   93 */         if (dayStart == to) {
/*   94 */           return true;
/*      */         }
/*      */       }
/*   97 */       break;
/*      */     case 6:
/*  100 */       if ((to - from != 0L) && (to - from < period.getInterval()))
/*      */         break;
/*  102 */       Calendar gmtCalendar = (Calendar)gmtCalendarThreadLocal.get();
/*  103 */       gmtCalendar.setTimeInMillis(System.currentTimeMillis());
/*  104 */       gmtCalendar.set(14, 0);
/*  105 */       gmtCalendar.set(13, 0);
/*  106 */       gmtCalendar.set(12, 0);
/*  107 */       gmtCalendar.set(11, 0);
/*  108 */       gmtCalendar.set(7, 2);
/*  109 */       long weekStart = gmtCalendar.getTimeInMillis();
/*      */ 
/*  111 */       boolean isWeekCorrectlySelected = false;
/*      */ 
/*  113 */       if (((from - weekStart) % Period.WEEKLY.getInterval() == 0L) && 
/*  114 */         ((to - weekStart) % Period.WEEKLY.getInterval() == 0L)) {
/*  115 */         isWeekCorrectlySelected = true;
/*      */       }
/*      */ 
/*  118 */       if ((isWeekCorrectlySelected) && 
/*  119 */         ((from - to) % period.getInterval() == 0L)) {
/*  120 */         return true;
/*      */       }
/*      */ 
/*  123 */       break;
/*      */     case 7:
/*  127 */       boolean isMonthCorrectlySelected = false;
/*  128 */       if (to - from < 0L) break;
/*  129 */       Calendar gmtCalendar = (Calendar)gmtCalendarThreadLocal.get();
/*  130 */       gmtCalendar.setTimeInMillis(from);
/*  131 */       if ((gmtCalendar.get(14) == 0) && (gmtCalendar.get(13) == 0) && (gmtCalendar.get(12) == 0) && (gmtCalendar.get(11) == 0) && (gmtCalendar.get(5) == 1))
/*      */       {
/*  133 */         gmtCalendar.setTimeInMillis(to);
/*  134 */         if ((gmtCalendar.get(14) == 0) && (gmtCalendar.get(13) == 0) && (gmtCalendar.get(12) == 0) && (gmtCalendar.get(11) == 0) && (gmtCalendar.get(5) == 1))
/*      */         {
/*  136 */           isMonthCorrectlySelected = true;
/*      */         }
/*      */       }
/*  139 */       if (isMonthCorrectlySelected) {
/*  140 */         Calendar fromDate = Calendar.getInstance();
/*  141 */         fromDate.setTimeInMillis(from);
/*  142 */         Calendar toDate = Calendar.getInstance();
/*  143 */         toDate.setTimeInMillis(to);
/*      */ 
/*  145 */         int months = (toDate.get(1) - fromDate.get(1)) * 12 + (toDate.get(2) - fromDate.get(2)) + (toDate.get(5) >= fromDate.get(5) ? 0 : -1);
/*      */ 
/*  150 */         if (months % period.getNumOfUnits() == 0) {
/*  151 */           return true;
/*      */         }
/*      */       }
/*  154 */       break;
/*      */     case 8:
/*  157 */       throw new DataCacheException("Periods with year unit not supported yet");
/*      */     }
/*  159 */     return false;
/*      */   }
/*      */ 
/*      */   public static long getClosestHourStartBefore(long from) {
/*  163 */     return from - from % 3600000L;
/*      */   }
/*      */ 
/*      */   public static long getClosestDayStartBefore(long from) {
/*  167 */     return from - from % 86400000L;
/*      */   }
/*      */ 
/*      */   public static long getClosestMonthStartBefore(long from) {
/*  171 */     Calendar gmtCalendar = (Calendar)gmtCalendarThreadLocal.get();
/*  172 */     gmtCalendar.setTimeInMillis(from);
/*  173 */     gmtCalendar.set(14, 0);
/*  174 */     gmtCalendar.set(13, 0);
/*  175 */     gmtCalendar.set(12, 0);
/*  176 */     gmtCalendar.set(11, 0);
/*  177 */     gmtCalendar.set(5, 1);
/*  178 */     return gmtCalendar.getTimeInMillis();
/*      */   }
/*      */ 
/*      */   public static long getClosestYearStartBefore(long from) {
/*  182 */     Calendar gmtCalendar = (Calendar)gmtCalendarThreadLocal.get();
/*  183 */     gmtCalendar.setTimeInMillis(from);
/*  184 */     gmtCalendar.set(14, 0);
/*  185 */     gmtCalendar.set(13, 0);
/*  186 */     gmtCalendar.set(12, 0);
/*  187 */     gmtCalendar.set(11, 0);
/*  188 */     gmtCalendar.set(5, 1);
/*  189 */     gmtCalendar.set(2, 0);
/*  190 */     return gmtCalendar.getTimeInMillis();
/*      */   }
/*      */ 
/*      */   public static long getChunkStart(Period period, long time) throws DataCacheException {
/*  194 */     long value = getChunkStartFast(period, time);
/*  195 */     if (value == -9223372036854775808L) {
/*  196 */       switch (3.$SwitchMap$com$dukascopy$api$Unit[period.getUnit().ordinal()]) {
/*      */       case 8:
/*  198 */         throw new DataCacheException("Periods with year unit not supported yet");
/*      */       }
/*  200 */       throw new DataCacheException("Period not supported");
/*      */     }
/*      */ 
/*  203 */     return value;
/*      */   }
/*      */ 
/*      */   public static long getChunkStartFast(Period period, long time)
/*      */   {
/*  208 */     if (period == Period.TICK) {
/*  209 */       return getClosestHourStartBefore(time);
/*      */     }
/*  211 */     switch (3.$SwitchMap$com$dukascopy$api$Unit[period.getUnit().ordinal()]) {
/*      */     case 2:
/*  213 */       return getClosestDayStartBefore(time);
/*      */     case 3:
/*  215 */       if (period.getNumOfUnits() >= 10) {
/*  216 */         return getClosestMonthStartBefore(time);
/*      */       }
/*  218 */       return getClosestDayStartBefore(time);
/*      */     case 4:
/*  221 */       if (period.getNumOfUnits() >= 4) {
/*  222 */         return getClosestYearStartBefore(time);
/*      */       }
/*  224 */       return getClosestMonthStartBefore(time);
/*      */     case 5:
/*      */     case 6:
/*      */     case 7:
/*  229 */       return getClosestYearStartBefore(time);
/*      */     }
/*  231 */     return -9223372036854775808L;
/*      */   }
/*      */ 
/*      */   public static long getOrdersChunkStart(long time)
/*      */   {
/*  237 */     return getClosestMonthStartBefore(time);
/*      */   }
/*      */ 
/*      */   public static Unit getChunkLength(Period period) throws DataCacheException {
/*  241 */     Unit value = getChunkLengthFast(period);
/*  242 */     if (value == null) {
/*  243 */       switch (3.$SwitchMap$com$dukascopy$api$Unit[period.getUnit().ordinal()]) {
/*      */       case 8:
/*  245 */         throw new DataCacheException("Periods with year unit not supported yet");
/*      */       }
/*  247 */       throw new DataCacheException("Period not supported");
/*      */     }
/*      */ 
/*  250 */     return value;
/*      */   }
/*      */ 
/*      */   public static Unit getChunkLengthFast(Period period)
/*      */   {
/*  255 */     if (period == Period.TICK) {
/*  256 */       return Unit.Hour;
/*      */     }
/*  258 */     switch (3.$SwitchMap$com$dukascopy$api$Unit[period.getUnit().ordinal()]) {
/*      */     case 2:
/*  260 */       return Unit.Day;
/*      */     case 3:
/*  262 */       if (period.getNumOfUnits() >= 10) {
/*  263 */         return Unit.Month;
/*      */       }
/*  265 */       return Unit.Day;
/*      */     case 4:
/*  268 */       if (period.getNumOfUnits() >= 4) {
/*  269 */         return Unit.Year;
/*      */       }
/*  271 */       return Unit.Month;
/*      */     case 5:
/*      */     case 6:
/*      */     case 7:
/*  276 */       return Unit.Year;
/*      */     }
/*  278 */     return null;
/*      */   }
/*      */ 
/*      */   public static long getChunkEnd(Period period, long time)
/*      */     throws DataCacheException
/*      */   {
/*  284 */     long value = getChunkEndFast(period, time);
/*  285 */     if (value == -9223372036854775808L) {
/*  286 */       switch (3.$SwitchMap$com$dukascopy$api$Unit[period.getUnit().ordinal()]) {
/*      */       case 8:
/*  288 */         throw new DataCacheException("Periods with year unit not supported yet");
/*      */       }
/*  290 */       throw new DataCacheException("Period not supported");
/*      */     }
/*      */ 
/*  293 */     return value;
/*      */   }
/*      */ 
/*      */   public static long getChunkEndFast(Period period, long time)
/*      */   {
/*  298 */     long chunkStart = getChunkStartFast(period, time);
/*  299 */     if (period == Period.TICK)
/*  300 */       return chunkStart + 3600000L;
/*      */     Calendar gmtCalendar;
/*  302 */     switch (3.$SwitchMap$com$dukascopy$api$Unit[period.getUnit().ordinal()]) {
/*      */     case 2:
/*  304 */       return chunkStart + 86400000L - period.getInterval();
/*      */     case 3:
/*  306 */       if (period.getNumOfUnits() >= 10) {
/*  307 */         Calendar gmtCalendar = (Calendar)gmtCalendarThreadLocal.get();
/*  308 */         gmtCalendar.setTimeInMillis(chunkStart);
/*  309 */         gmtCalendar.add(2, 1);
/*  310 */         return gmtCalendar.getTimeInMillis() - period.getInterval();
/*      */       }
/*  312 */       return chunkStart + 86400000L - period.getInterval();
/*      */     case 4:
/*  315 */       gmtCalendar = (Calendar)gmtCalendarThreadLocal.get();
/*  316 */       if (period.getNumOfUnits() >= 4) {
/*  317 */         gmtCalendar.setTimeInMillis(chunkStart);
/*  318 */         gmtCalendar.add(1, 1);
/*  319 */         return gmtCalendar.getTimeInMillis() - period.getInterval();
/*      */       }
/*  321 */       gmtCalendar.setTimeInMillis(chunkStart);
/*  322 */       gmtCalendar.add(2, 1);
/*  323 */       return gmtCalendar.getTimeInMillis() - period.getInterval();
/*      */     case 5:
/*  326 */       gmtCalendar = (Calendar)gmtCalendarThreadLocal.get();
/*  327 */       gmtCalendar.setTimeInMillis(chunkStart);
/*  328 */       gmtCalendar.add(1, 1);
/*  329 */       gmtCalendar.add(6, -1);
/*  330 */       return gmtCalendar.getTimeInMillis();
/*      */     case 6:
/*  332 */       gmtCalendar = (Calendar)gmtCalendarThreadLocal.get();
/*  333 */       gmtCalendar.setTimeInMillis(chunkStart);
/*  334 */       gmtCalendar.add(1, 1);
/*  335 */       long yearEnd = gmtCalendar.getTimeInMillis();
/*  336 */       gmtCalendar.set(7, 2);
/*  337 */       if (yearEnd <= gmtCalendar.getTimeInMillis()) {
/*  338 */         gmtCalendar.add(3, -1);
/*      */       }
/*  340 */       return gmtCalendar.getTimeInMillis();
/*      */     case 7:
/*  342 */       gmtCalendar = (Calendar)gmtCalendarThreadLocal.get();
/*  343 */       gmtCalendar.setTimeInMillis(chunkStart);
/*  344 */       gmtCalendar.add(1, 1);
/*  345 */       gmtCalendar.add(2, -period.getNumOfUnits());
/*  346 */       return gmtCalendar.getTimeInMillis();
/*      */     }
/*  348 */     return -9223372036854775808L;
/*      */   }
/*      */ 
/*      */   public static long getOrdersChunkEnd(long time)
/*      */   {
/*  354 */     long chunkStart = getOrdersChunkStart(time);
/*  355 */     Calendar gmtCalendar = (Calendar)gmtCalendarThreadLocal.get();
/*  356 */     gmtCalendar.setTimeInMillis(chunkStart);
/*  357 */     gmtCalendar.add(2, 1);
/*  358 */     return gmtCalendar.getTimeInMillis();
/*      */   }
/*      */ 
/*      */   public static long[][] separateChunksForCache(Period period, long from, long to) {
/*  362 */     List intervals = new ArrayList();
/*      */ 
/*  364 */     if (period == Period.TICK)
/*      */     {
/*  366 */       long hourStart = getClosestHourStartBefore(from);
/*      */       do
/*      */       {
/*  369 */         long hourEnd = hourStart + 3600000L;
/*  370 */         intervals.add(new long[] { hourStart, hourEnd });
/*  371 */         hourStart = hourEnd;
/*  372 */       }while (hourStart < to);
/*      */     }
/*      */     else
/*      */     {
/*      */       long dayStart;
/*      */       long dayEnd;
/*      */       Calendar cal;
/*      */       long yearStart;
/*      */       long yearEnd;
/*  374 */       switch (3.$SwitchMap$com$dukascopy$api$Unit[period.getUnit().ordinal()])
/*      */       {
/*      */       case 2:
/*  377 */         dayStart = getClosestDayStartBefore(from);
/*      */         do
/*      */         {
/*  380 */           dayEnd = dayStart + 86400000L;
/*  381 */           intervals.add(new long[] { dayStart, dayEnd - period.getInterval() });
/*      */ 
/*  383 */           dayStart = dayEnd;
/*  384 */         }while (dayStart <= to);
/*  385 */         break;
/*      */       case 3:
/*  387 */         if (period.getNumOfUnits() >= 10)
/*      */         {
/*  389 */           Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
/*  390 */           long monthStart = getClosestMonthStartBefore(from);
/*      */           do
/*      */           {
/*  393 */             cal.setTimeInMillis(monthStart);
/*  394 */             cal.add(2, 1);
/*  395 */             long monthEnd = cal.getTimeInMillis();
/*  396 */             intervals.add(new long[] { monthStart, monthEnd - period.getInterval() });
/*  397 */             monthStart = monthEnd;
/*  398 */           }while (monthStart <= to);
/*      */         }
/*      */         else {
/*  401 */           dayStart = getClosestDayStartBefore(from);
/*      */           do {
/*  403 */             dayEnd = dayStart + 86400000L;
/*  404 */             intervals.add(new long[] { dayStart, dayEnd - period.getInterval() });
/*      */ 
/*  406 */             dayStart = dayEnd;
/*  407 */           }while (dayStart <= to);
/*      */         }
/*  409 */         break;
/*      */       case 4:
/*  411 */         if (period.getNumOfUnits() >= 4)
/*      */         {
/*  413 */           Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
/*  414 */           long yearStart = getClosestYearStartBefore(from);
/*      */           do {
/*  416 */             cal.setTimeInMillis(yearStart);
/*  417 */             cal.add(1, 1);
/*  418 */             long yearEnd = cal.getTimeInMillis();
/*  419 */             intervals.add(new long[] { yearStart, cal.getTimeInMillis() - period.getInterval() });
/*  420 */             yearStart = yearEnd;
/*  421 */           }while (yearStart <= to);
/*      */         }
/*      */         else
/*      */         {
/*  425 */           Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
/*  426 */           long monthStart = getClosestMonthStartBefore(from);
/*      */           do
/*      */           {
/*  429 */             cal.setTimeInMillis(monthStart);
/*  430 */             cal.add(2, 1);
/*  431 */             long monthEnd = cal.getTimeInMillis();
/*  432 */             intervals.add(new long[] { monthStart, monthEnd - period.getInterval() });
/*  433 */             monthStart = monthEnd;
/*  434 */           }while (monthStart <= to);
/*      */         }
/*  436 */         break;
/*      */       case 6:
/*  439 */         cal = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
/*  440 */         yearStart = getClosestYearStartBefore(from);
/*      */         do
/*      */         {
/*  443 */           cal.setTimeInMillis(yearStart);
/*  444 */           cal.add(1, 1);
/*  445 */           yearEnd = cal.getTimeInMillis();
/*  446 */           cal.set(7, 2);
/*  447 */           if (yearEnd <= cal.getTimeInMillis()) {
/*  448 */             cal.add(3, -1);
/*      */           }
/*  450 */           intervals.add(new long[] { yearStart, cal.getTimeInMillis() });
/*  451 */           yearStart = yearEnd;
/*  452 */           cal.add(3, 1);
/*  453 */         }while (cal.getTimeInMillis() <= to);
/*  454 */         break;
/*      */       case 5:
/*  457 */         cal = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
/*  458 */         yearStart = getClosestYearStartBefore(from);
/*      */         do {
/*  460 */           cal.setTimeInMillis(yearStart);
/*  461 */           cal.add(1, 1);
/*  462 */           yearEnd = cal.getTimeInMillis();
/*  463 */           cal.add(6, -1);
/*  464 */           intervals.add(new long[] { yearStart, cal.getTimeInMillis() });
/*  465 */           yearStart = yearEnd;
/*  466 */         }while (yearStart <= to);
/*  467 */         break;
/*      */       case 7:
/*  470 */         cal = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
/*  471 */         yearStart = getClosestYearStartBefore(from);
/*      */         do {
/*  473 */           cal.setTimeInMillis(yearStart);
/*  474 */           cal.add(1, 1);
/*  475 */           yearEnd = cal.getTimeInMillis();
/*  476 */           cal.add(2, -period.getNumOfUnits());
/*  477 */           intervals.add(new long[] { yearStart, cal.getTimeInMillis() });
/*  478 */           yearStart = yearEnd;
/*  479 */         }while (yearStart <= to);
/*      */       }
/*      */     }
/*      */ 
/*  483 */     return (long[][])intervals.toArray(new long[intervals.size()][]);
/*      */   }
/*      */ 
/*      */   public static long[][] separateOrderChunksForCache(long from, long to) {
/*  487 */     List intervals = new ArrayList();
/*      */ 
/*  490 */     Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
/*  491 */     long monthStart = getClosestMonthStartBefore(from);
/*      */     do
/*      */     {
/*  494 */       cal.setTimeInMillis(monthStart);
/*  495 */       cal.add(2, 1);
/*  496 */       long monthEnd = cal.getTimeInMillis();
/*  497 */       intervals.add(new long[] { monthStart, monthEnd });
/*  498 */       monthStart = monthEnd;
/*  499 */     }while (monthStart <= to);
/*      */ 
/*  501 */     return (long[][])intervals.toArray(new long[intervals.size()][]);
/*      */   }
/*      */ 
/*      */   public static long getCandleStart(Period period, long time) throws DataCacheException {
/*  505 */     if (period == Period.TICK) {
/*  506 */       throw new DataCacheException("getCandleStart() called with TICK period");
/*      */     }
/*  508 */     if (period.getUnit() == Unit.Millisecond) {
/*  509 */       throw new DataCacheException("Cannot get candle start for period in milliseconds");
/*      */     }
/*  511 */     return getCandleStartFast(period, time);
/*      */   }
/*      */ 
/*      */   public static long getCandleStartFast(Period period, long time)
/*      */   {
/*      */     Calendar gmtCalendar;
/*  518 */     switch (3.$SwitchMap$com$dukascopy$api$Unit[period.getUnit().ordinal()]) {
/*      */     case 1:
/*      */     case 2:
/*      */     case 3:
/*      */     case 4:
/*      */     case 5:
/*  524 */       return time - time % period.getInterval();
/*      */     case 6:
/*  526 */       gmtCalendar = (Calendar)gmtCalendarThreadLocal.get();
/*  527 */       gmtCalendar.setTimeInMillis(time);
/*  528 */       gmtCalendar.set(14, 0);
/*  529 */       gmtCalendar.set(13, 0);
/*  530 */       gmtCalendar.set(12, 0);
/*  531 */       gmtCalendar.set(11, 0);
/*  532 */       gmtCalendar.set(7, 2);
/*  533 */       if (gmtCalendar.getTimeInMillis() > time) {
/*  534 */         gmtCalendar.add(3, -1);
/*      */       }
/*      */ 
/*  537 */       if (period.getNumOfUnits() > 1) {
/*  538 */         int currentWeek = gmtCalendar.get(3);
/*  539 */         int shiftedWeek = currentWeek / period.getNumOfUnits() * period.getNumOfUnits();
/*  540 */         gmtCalendar.set(3, shiftedWeek);
/*      */       }
/*      */ 
/*  543 */       return gmtCalendar.getTimeInMillis();
/*      */     case 7:
/*  545 */       gmtCalendar = (Calendar)gmtCalendarThreadLocal.get();
/*  546 */       gmtCalendar.setTimeInMillis(time);
/*  547 */       gmtCalendar.set(14, 0);
/*  548 */       gmtCalendar.set(13, 0);
/*  549 */       gmtCalendar.set(12, 0);
/*  550 */       gmtCalendar.set(11, 0);
/*  551 */       gmtCalendar.set(5, 1);
/*      */ 
/*  553 */       if (period.getNumOfUnits() > 1) {
/*  554 */         int currentMonth = gmtCalendar.get(2);
/*  555 */         int shiftedMonth = currentMonth / period.getNumOfUnits() * period.getNumOfUnits();
/*  556 */         gmtCalendar.set(2, shiftedMonth);
/*      */       }
/*      */ 
/*  559 */       return gmtCalendar.getTimeInMillis();
/*      */     case 8:
/*  561 */       gmtCalendar = (Calendar)gmtCalendarThreadLocal.get();
/*  562 */       gmtCalendar.setTimeInMillis(time);
/*  563 */       gmtCalendar.set(14, 0);
/*  564 */       gmtCalendar.set(13, 0);
/*  565 */       gmtCalendar.set(12, 0);
/*  566 */       gmtCalendar.set(11, 0);
/*  567 */       gmtCalendar.set(5, 1);
/*      */ 
/*  569 */       if (period.getNumOfUnits() > 1) {
/*  570 */         int currentYear = gmtCalendar.get(1);
/*  571 */         int shiftedYear = currentYear / period.getNumOfUnits() * period.getNumOfUnits();
/*  572 */         gmtCalendar.set(1, shiftedYear);
/*      */       }
/*      */ 
/*  575 */       return gmtCalendar.getTimeInMillis();
/*      */     }
/*  577 */     return -9223372036854775808L;
/*      */   }
/*      */ 
/*      */   public static long getFirstCandleInChunk(Period period, long time) throws DataCacheException {
/*  581 */     if (period == Period.TICK) {
/*  582 */       throw new DataCacheException("getCandleStart() called with TICK period");
/*      */     }
/*  584 */     if (!isCandleBasic(period)) {
/*  585 */       throw new DataCacheException("Only basic periods supported atm");
/*      */     }
/*  587 */     if (period.getUnit() == Unit.Millisecond) {
/*  588 */       throw new DataCacheException("Cannot get candle start for period in milliseconds");
/*      */     }
/*  590 */     time = getChunkStart(period, time);
/*  591 */     return getFirstCandleInChunkFast(period, time);
/*      */   }
/*      */ 
/*      */   public static long getFirstCandleInChunkFast(Period period, long chunkStartTime) {
/*  595 */     if (period == Period.WEEKLY) {
/*  596 */       long candleStart = getCandleStartFast(period, chunkStartTime);
/*  597 */       if (candleStart < chunkStartTime) {
/*  598 */         candleStart = getNextCandleStartFast(period, candleStart);
/*      */       }
/*  600 */       return candleStart;
/*      */     }
/*  602 */     return chunkStartTime;
/*      */   }
/*      */ 
/*      */   public static File getChunkTempDirectory() throws DataCacheException {
/*  606 */     return new File(FilePathManager.getInstance().getCacheTempDirectory());
/*      */   }
/*      */ 
/*      */   public static File getOrdersChunkFile(String scheme, String accountId, Instrument instrument, long from, int version) throws DataCacheException {
/*  610 */     StringBuilder fileName = new StringBuilder(FilePathManager.getInstance().getCacheDirectory());
/*  611 */     fileName.append(instrument.name()).append(File.separatorChar);
/*      */ 
/*  614 */     Calendar gmtCalendar = (Calendar)gmtCalendarThreadLocal.get();
/*  615 */     gmtCalendar.setTimeInMillis(getOrdersChunkStart(from));
/*  616 */     int month = gmtCalendar.get(2);
/*  617 */     fileName.append(gmtCalendar.get(1)).append(File.separatorChar);
/*  618 */     if (month < 10) {
/*  619 */       fileName.append("0");
/*      */     }
/*  621 */     fileName.append(month).append(File.separatorChar);
/*  622 */     fileName.append("o_").append(scheme.charAt(0)).append("_").append(MD5.getDigest(accountId)).append(".bin");
/*  623 */     return new File(fileName.toString());
/*      */   }
/*      */ 
/*      */   public static File getChunkFile(Instrument instrument, Period period, OfferSide side, long from, int version) throws DataCacheException {
/*  627 */     StringBuilder fileName = new StringBuilder(FilePathManager.getInstance().getCacheDirectory());
/*  628 */     fileName.append(instrument.name()).append(File.separatorChar);
/*      */ 
/*  633 */     Calendar gmtCalendar = (Calendar)gmtCalendarThreadLocal.get();
/*  634 */     gmtCalendar.setTimeInMillis(getChunkStart(period, from));
/*  635 */     int month = gmtCalendar.get(2);
/*  636 */     int day = gmtCalendar.get(5);
/*  637 */     int hour = gmtCalendar.get(11);
/*  638 */     fileName.append(gmtCalendar.get(1)).append(File.separatorChar);
/*      */ 
/*  640 */     if (period == Period.TICK) {
/*  641 */       if (month < 10) {
/*  642 */         fileName.append("0");
/*      */       }
/*  644 */       fileName.append(month).append(File.separatorChar);
/*  645 */       if (day < 10) {
/*  646 */         fileName.append("0");
/*      */       }
/*  648 */       fileName.append(day).append(File.separatorChar);
/*  649 */       if (hour < 10) {
/*  650 */         fileName.append("0");
/*      */       }
/*  652 */       fileName.append(hour).append("h").append("_").append("ticks");
/*      */     } else {
/*  654 */       switch (3.$SwitchMap$com$dukascopy$api$Unit[period.getUnit().ordinal()]) {
/*      */       case 8:
/*  656 */         throw new DataCacheException("Periods with year unit not supported yet");
/*      */       case 7:
/*  658 */         fileName.append(side.name()).append("_").append("candles").append("_").append("month").append("_").append(period.getNumOfUnits());
/*  659 */         break;
/*      */       case 6:
/*  661 */         fileName.append(side.name()).append("_").append("candles").append("_").append("week").append("_").append(period.getNumOfUnits());
/*  662 */         break;
/*      */       case 5:
/*  664 */         fileName.append(side.name()).append("_").append("candles").append("_").append("day").append("_").append(period.getNumOfUnits());
/*  665 */         break;
/*      */       case 4:
/*  667 */         if (period.getNumOfUnits() < 4) {
/*  668 */           if (month < 10) {
/*  669 */             fileName.append("0");
/*      */           }
/*  671 */           fileName.append(month).append(File.separatorChar);
/*      */         }
/*  673 */         fileName.append(side.name()).append("_").append("candles").append("_").append("hour").append("_").append(period.getNumOfUnits());
/*  674 */         break;
/*      */       case 3:
/*  676 */         if (month < 10) {
/*  677 */           fileName.append("0");
/*      */         }
/*  679 */         fileName.append(month).append(File.separatorChar);
/*  680 */         if (period.getNumOfUnits() < 10) {
/*  681 */           if (day < 10) {
/*  682 */             fileName.append("0");
/*      */           }
/*  684 */           fileName.append(day).append(File.separatorChar);
/*      */         }
/*  686 */         fileName.append(side.name()).append("_").append("candles").append("_").append("min").append("_").append(period.getNumOfUnits());
/*  687 */         break;
/*      */       case 2:
/*  689 */         if (month < 10) {
/*  690 */           fileName.append("0");
/*      */         }
/*  692 */         fileName.append(month).append(File.separatorChar);
/*  693 */         if (day < 10) {
/*  694 */           fileName.append("0");
/*      */         }
/*  696 */         fileName.append(day).append(File.separatorChar);
/*  697 */         fileName.append(side.name()).append("_").append("candles").append("_").append("sec").append("_").append(period.getNumOfUnits());
/*  698 */         break;
/*      */       case 1:
/*  700 */         throw new DataCacheException("Periods with millisecond unit not supported yet");
/*      */       }
/*      */     }
/*  703 */     if (version >= 5)
/*  704 */       fileName.append(".bi5");
/*      */     else {
/*  706 */       fileName.append(".bin");
/*      */     }
/*  708 */     return new File(fileName.toString());
/*      */   }
/*      */ 
/*      */   public static File getIntraPeriodFile(int intraperiodNum, Instrument instrument, Period period, OfferSide side, long from) throws DataCacheException {
/*  712 */     StringBuilder fileName = new StringBuilder(FilePathManager.getInstance().getCacheDirectory());
/*  713 */     fileName.append(instrument.name()).append(File.separatorChar);
/*  714 */     fileName.append("intraperiod");
/*  715 */     if (intraperiodNum != 0) {
/*  716 */       fileName.append(intraperiodNum);
/*      */     }
/*  718 */     fileName.append(File.separatorChar);
/*      */ 
/*  720 */     synchronized (intraPeriodFileDateFormat) {
/*  721 */       fileName.append(intraPeriodFileDateFormat.format(Long.valueOf(from)));
/*      */     }
/*      */ 
/*  724 */     if (period == Period.TICK)
/*  725 */       fileName.append("ticks");
/*      */     else {
/*  727 */       switch (3.$SwitchMap$com$dukascopy$api$Unit[period.getUnit().ordinal()]) {
/*      */       case 8:
/*  729 */         throw new DataCacheException("Periods with year unit not supported yet");
/*      */       case 7:
/*  731 */         fileName.append(side.name()).append("_").append("candles").append("_").append("month").append("_").append(period.getNumOfUnits());
/*  732 */         break;
/*      */       case 6:
/*  734 */         fileName.append(side.name()).append("_").append("candles").append("_").append("week").append("_").append(period.getNumOfUnits());
/*  735 */         break;
/*      */       case 5:
/*  737 */         fileName.append(side.name()).append("_").append("candles").append("_").append("day").append("_").append(period.getNumOfUnits());
/*  738 */         break;
/*      */       case 4:
/*  740 */         fileName.append(side.name()).append("_").append("candles").append("_").append("hour").append("_").append(period.getNumOfUnits());
/*  741 */         break;
/*      */       case 3:
/*  743 */         fileName.append(side.name()).append("_").append("candles").append("_").append("min").append("_").append(period.getNumOfUnits());
/*  744 */         break;
/*      */       case 2:
/*  746 */         fileName.append(side.name()).append("_").append("candles").append("_").append("sec").append("_").append(period.getNumOfUnits());
/*  747 */         break;
/*      */       case 1:
/*  749 */         throw new DataCacheException("Periods with millisecond unit not supported yet");
/*      */       }
/*      */     }
/*  752 */     fileName.append(".bi5");
/*  753 */     return new File(fileName.toString());
/*      */   }
/*      */ 
/*      */   public static File[] getIntraPeriodTickFiles(int intraperiodNum, Instrument instrument) {
/*  757 */     StringBuilder fileName = new StringBuilder(FilePathManager.getInstance().getCacheDirectory());
/*  758 */     fileName.append(instrument.name()).append(File.separatorChar);
/*  759 */     fileName.append("intraperiod");
/*  760 */     if (intraperiodNum != 0) {
/*  761 */       fileName.append(intraperiodNum);
/*      */     }
/*  763 */     fileName.append(File.separatorChar);
/*  764 */     File dirFile = new File(fileName.toString());
/*  765 */     if (dirFile.exists())
/*  766 */       return dirFile.listFiles(new FileFilter() {
/*      */         public boolean accept(File pathname) {
/*  768 */           return (pathname.isFile()) && ((pathname.getName().endsWith("_ticks.bin")) || (pathname.getName().endsWith("_ticks.bi5")));
/*      */         }
/*      */       });
/*  772 */     return new File[0];
/*      */   }
/*      */ 
/*      */   public static double getPriceWithCommission(Instrument instrument, OfferSide side, double price, double commission)
/*      */   {
/*  786 */     double priceChange = commission;
/*  787 */     if (side == OfferSide.ASK) {
/*  788 */       return StratUtils.roundHalfUp(price + priceChange, instrument.getPipScale() + 1);
/*      */     }
/*  790 */     return StratUtils.roundHalfDown(price - priceChange, instrument.getPipScale() + 1);
/*      */   }
/*      */ 
/*      */   public static long getNextChunkStart(Period period, long time) throws DataCacheException
/*      */   {
/*  795 */     long chunkStart = getChunkStart(period, time);
/*  796 */     if (period == Period.TICK) {
/*  797 */       return chunkStart + 3600000L;
/*      */     }
/*  799 */     switch (3.$SwitchMap$com$dukascopy$api$Unit[period.getUnit().ordinal()]) {
/*      */     case 2:
/*  801 */       return chunkStart + 86400000L;
/*      */     case 3:
/*  803 */       if (period.getNumOfUnits() >= 10) {
/*  804 */         Calendar gmtCalendar = (Calendar)gmtCalendarThreadLocal.get();
/*  805 */         gmtCalendar.setTimeInMillis(chunkStart);
/*  806 */         gmtCalendar.add(2, 1);
/*  807 */         return gmtCalendar.getTimeInMillis();
/*      */       }
/*  809 */       return chunkStart + 86400000L;
/*      */     case 4:
/*  812 */       if (period.getNumOfUnits() >= 4) break;
/*  813 */       Calendar gmtCalendar = (Calendar)gmtCalendarThreadLocal.get();
/*  814 */       gmtCalendar.setTimeInMillis(chunkStart);
/*  815 */       gmtCalendar.add(2, 1);
/*  816 */       return gmtCalendar.getTimeInMillis();
/*      */     case 5:
/*      */     case 6:
/*      */     case 7:
/*  822 */       Calendar gmtCalendar = (Calendar)gmtCalendarThreadLocal.get();
/*  823 */       gmtCalendar.setTimeInMillis(chunkStart);
/*  824 */       gmtCalendar.add(1, 1);
/*  825 */       return gmtCalendar.getTimeInMillis();
/*      */     case 8:
/*  827 */       throw new DataCacheException("Periods with year unit not supported yet");
/*      */     }
/*  829 */     throw new DataCacheException("Period not supported");
/*      */   }
/*      */ 
/*      */   public static long getPreviousChunkStart(Period period, long time)
/*      */     throws DataCacheException
/*      */   {
/*  835 */     long chunkStart = getChunkStart(period, time);
/*  836 */     if (period == Period.TICK) {
/*  837 */       return chunkStart - 3600000L;
/*      */     }
/*  839 */     switch (3.$SwitchMap$com$dukascopy$api$Unit[period.getUnit().ordinal()]) {
/*      */     case 2:
/*  841 */       return chunkStart - 86400000L;
/*      */     case 3:
/*  843 */       if (period.getNumOfUnits() >= 10) {
/*  844 */         Calendar gmtCalendar = (Calendar)gmtCalendarThreadLocal.get();
/*  845 */         gmtCalendar.setTimeInMillis(chunkStart);
/*  846 */         gmtCalendar.add(2, -1);
/*  847 */         return gmtCalendar.getTimeInMillis();
/*      */       }
/*  849 */       return chunkStart - 86400000L;
/*      */     case 4:
/*  852 */       if (period.getNumOfUnits() >= 4) break;
/*  853 */       Calendar gmtCalendar = (Calendar)gmtCalendarThreadLocal.get();
/*  854 */       gmtCalendar.setTimeInMillis(chunkStart);
/*  855 */       gmtCalendar.add(2, -1);
/*  856 */       return gmtCalendar.getTimeInMillis();
/*      */     case 5:
/*      */     case 6:
/*      */     case 7:
/*  862 */       Calendar gmtCalendar = (Calendar)gmtCalendarThreadLocal.get();
/*  863 */       gmtCalendar.setTimeInMillis(chunkStart);
/*  864 */       gmtCalendar.add(1, -1);
/*  865 */       return gmtCalendar.getTimeInMillis();
/*      */     case 8:
/*  867 */       throw new DataCacheException("Periods with year unit not supported yet");
/*      */     }
/*  869 */     throw new DataCacheException("Period not supported");
/*      */   }
/*      */ 
/*      */   public static int getCandleCountInChunk(Period period, long time)
/*      */     throws DataCacheException
/*      */   {
/*  875 */     long chunkStart = getChunkStart(period, time);
/*  876 */     long chunkEnd = getChunkEnd(period, time);
/*  877 */     if (period == Period.TICK) {
/*  878 */       throw new DataCacheException("Not a candle");
/*      */     }
/*  880 */     switch (3.$SwitchMap$com$dukascopy$api$Unit[period.getUnit().ordinal()]) {
/*      */     case 2:
/*      */     case 3:
/*      */     case 4:
/*      */     case 5:
/*  885 */       return (int)((chunkEnd - chunkStart) / period.getInterval() + 1L);
/*      */     case 6:
/*  887 */       Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
/*  888 */       cal.setTimeInMillis(chunkStart);
/*  889 */       cal.set(7, 2);
/*  890 */       if (cal.getTimeInMillis() < chunkStart) {
/*  891 */         cal.add(3, 1);
/*      */       }
/*  893 */       int count = 0;
/*      */       do {
/*  895 */         cal.add(3, 1);
/*  896 */         count++;
/*  897 */       }while (cal.getTimeInMillis() <= chunkEnd);
/*  898 */       return count;
/*      */     case 7:
/*  900 */       return 12;
/*      */     case 8:
/*  902 */       throw new DataCacheException("Periods with year unit not supported yet");
/*      */     }
/*  904 */     throw new DataCacheException("Period not supported");
/*      */   }
/*      */ 
/*      */   public static long getNextCandleStart(Period period, long candleTime)
/*      */     throws DataCacheException
/*      */   {
/*  910 */     long candleStart = getCandleStart(period, candleTime);
/*  911 */     if (period == Period.TICK) {
/*  912 */       throw new DataCacheException("Not a candle");
/*      */     }
/*  914 */     if (period.getUnit() == Unit.Millisecond) {
/*  915 */       throw new DataCacheException("Period not supported");
/*      */     }
/*  917 */     if (period.getUnit() == Unit.Year) {
/*  918 */       throw new DataCacheException("Periods with year unit not supported yet");
/*      */     }
/*  920 */     return getNextCandleStartFast(period, candleStart);
/*      */   }
/*      */ 
/*      */   public static long getNextCandleStartFast(Period period, long candleStart)
/*      */   {
/*  928 */     switch (3.$SwitchMap$com$dukascopy$api$Unit[period.getUnit().ordinal()]) {
/*      */     case 1:
/*      */     case 2:
/*      */     case 3:
/*      */     case 4:
/*      */     case 5:
/*      */     case 6:
/*  935 */       return candleStart + period.getInterval();
/*      */     case 7:
/*  937 */       Calendar gmtCalendar = (Calendar)gmtCalendarThreadLocal.get();
/*  938 */       gmtCalendar.setTimeInMillis(candleStart);
/*  939 */       gmtCalendar.add(2, period.getNumOfUnits());
/*  940 */       return gmtCalendar.getTimeInMillis();
/*      */     }
/*  942 */     return -9223372036854775808L;
/*      */   }
/*      */ 
/*      */   public static long getPreviousCandleStart(Period period, long candleTime) throws DataCacheException
/*      */   {
/*  947 */     long candleStart = getCandleStart(period, candleTime);
/*  948 */     if (period == Period.TICK) {
/*  949 */       throw new DataCacheException("Not a candle");
/*      */     }
/*  951 */     if (period.getUnit() == Unit.Millisecond) {
/*  952 */       throw new DataCacheException("Period not supported");
/*      */     }
/*  954 */     if (period.getUnit() == Unit.Year) {
/*  955 */       throw new DataCacheException("Periods with year unit not supported yet");
/*      */     }
/*  957 */     return getPreviousCandleStartFast(period, candleStart);
/*      */   }
/*      */ 
/*      */   public static long getPreviousCandleStartFast(Period period, long candleStart)
/*      */   {
/*  965 */     switch (3.$SwitchMap$com$dukascopy$api$Unit[period.getUnit().ordinal()]) {
/*      */     case 1:
/*      */     case 2:
/*      */     case 3:
/*      */     case 4:
/*      */     case 5:
/*      */     case 6:
/*  972 */       return candleStart - period.getInterval();
/*      */     case 7:
/*  974 */       Calendar gmtCalendar = (Calendar)gmtCalendarThreadLocal.get();
/*  975 */       gmtCalendar.setTimeInMillis(candleStart);
/*  976 */       gmtCalendar.add(2, -period.getNumOfUnits());
/*  977 */       return gmtCalendar.getTimeInMillis();
/*      */     }
/*  979 */     return -9223372036854775808L;
/*      */   }
/*      */ 
/*      */   public static int getCandlesCountBetween(Period period, long from, long to) throws DataCacheException
/*      */   {
/*  984 */     if (period == Period.TICK) {
/*  985 */       throw new DataCacheException("Not a candle");
/*      */     }
/*  987 */     long fromCandleStart = getCandleStart(period, from);
/*  988 */     long toCandleStart = getCandleStart(period, to);
/*  989 */     if (period.getUnit() == Unit.Millisecond) {
/*  990 */       throw new DataCacheException("Period not supported");
/*      */     }
/*  992 */     if (period.getUnit() == Unit.Year) {
/*  993 */       throw new DataCacheException("Periods with year unit not supported yet");
/*      */     }
/*  995 */     return getCandlesCountBetweenFast(period, fromCandleStart, toCandleStart);
/*      */   }
/*      */ 
/*      */   public static int getCandlesCountBetweenFast(Period period, long from, long to)
/*      */   {
/* 1000 */     switch (3.$SwitchMap$com$dukascopy$api$Unit[period.getUnit().ordinal()]) {
/*      */     case 2:
/*      */     case 3:
/*      */     case 4:
/*      */     case 5:
/*      */     case 6:
/* 1006 */       return (int)((to - from) / period.getInterval()) + 1;
/*      */     case 7:
/* 1008 */       int count = 0;
/* 1009 */       Calendar gmtCalendar = (Calendar)gmtCalendarThreadLocal.get();
/* 1010 */       gmtCalendar.setTimeInMillis(from);
/* 1011 */       while (gmtCalendar.getTimeInMillis() <= to) {
/* 1012 */         count++;
/* 1013 */         gmtCalendar.add(2, period.getNumOfUnits());
/*      */       }
/* 1015 */       return count;
/*      */     }
/* 1017 */     return -2147483648;
/*      */   }
/*      */ 
/*      */   public static long getTimeForNCandlesBack(Period period, long to, int numberOfCandles)
/*      */     throws DataCacheException
/*      */   {
/* 1030 */     if (period == Period.TICK) {
/* 1031 */       throw new DataCacheException("Not a candle");
/*      */     }
/* 1033 */     long candleStart = getCandleStart(period, to);
/* 1034 */     if (period.getUnit() == Unit.Millisecond) {
/* 1035 */       throw new DataCacheException("Period not supported");
/*      */     }
/* 1037 */     if (period.getUnit() == Unit.Year) {
/* 1038 */       throw new DataCacheException("Periods with year unit not supported yet");
/*      */     }
/* 1040 */     return getTimeForNCandlesBackFast(period, candleStart, numberOfCandles);
/*      */   }
/*      */ 
/*      */   public static long getTimeForNCandlesBackFast(Period period, long candleStart, int numberOfCandles)
/*      */   {
/* 1048 */     switch (3.$SwitchMap$com$dukascopy$api$Unit[period.getUnit().ordinal()]) {
/*      */     case 2:
/*      */     case 3:
/*      */     case 4:
/*      */     case 5:
/*      */     case 6:
/* 1054 */       return candleStart - (numberOfCandles - 1) * period.getInterval();
/*      */     case 7:
/* 1056 */       Calendar gmtCalendar = (Calendar)gmtCalendarThreadLocal.get();
/* 1057 */       gmtCalendar.setTimeInMillis(candleStart);
/* 1058 */       gmtCalendar.add(2, -(numberOfCandles - 1) * period.getNumOfUnits());
/* 1059 */       return gmtCalendar.getTimeInMillis();
/*      */     }
/* 1061 */     return -9223372036854775808L;
/*      */   }
/*      */ 
/*      */   public static long getTimeForNCandlesForward(Period period, long from, int numberOfCandles) throws DataCacheException
/*      */   {
/* 1066 */     return getTimeForNCandlesBack(period, from, -(numberOfCandles - 2));
/*      */   }
/*      */ 
/*      */   public static long getTimeForNCandlesForwardFast(Period period, long from, int numberOfCandles) {
/* 1070 */     return getTimeForNCandlesBackFast(period, from, -(numberOfCandles - 2));
/*      */   }
/*      */ 
/*      */   public static ToLoad[] getIntervalsToLoadForCandleFilling(Period period, long time) {
/* 1074 */     List toLoad = new ArrayList();
/* 1075 */     tryToLoadWith(Period.MONTHLY, getCandleStartFast(period, time), time, toLoad);
/* 1076 */     return (ToLoad[])toLoad.toArray(new ToLoad[toLoad.size()]);
/*      */   }
/*      */ 
/*      */   private static void tryToLoadWith(Period period, long from, long to, List<ToLoad> toLoad) {
/* 1080 */     if (from == to)
/* 1081 */       return;
/* 1082 */     if (period == Period.TICK) {
/* 1083 */       ToLoad load = new ToLoad(period, from, to);
/* 1084 */       toLoad.add(load);
/* 1085 */       return;
/* 1086 */     }if ((period != Period.MONTHLY) && (to - from >= period.getInterval()) && (from == getCandleStartFast(period, from)))
/*      */     {
/* 1088 */       ToLoad load = new ToLoad(period, from, getPreviousCandleStartFast(period, getCandleStartFast(period, to)));
/* 1089 */       toLoad.add(load);
/* 1090 */       from = getNextCandleStartFast(period, load.to);
/*      */     }
/*      */ 
/* 1093 */     if (period == Period.MONTHLY) {
/* 1094 */       tryToLoadWith(Period.DAILY, from, to, toLoad);
/*      */     }
/*      */     else {
/* 1097 */       Period[] valuesForIndicator = Period.valuesForIndicator();
/* 1098 */       for (int index = valuesForIndicator.length - 1; (index >= 0) && 
/* 1099 */         (!period.equals(valuesForIndicator[index])); index--);
/* 1103 */       tryToLoadWith(valuesForIndicator[(index - 1)], from, to, toLoad);
/*      */     }
/*      */   }
/*      */ 
/*      */   public static long getPreviousPriceAggregationBarStart(long time)
/*      */   {
/* 1126 */     return time - 1L;
/*      */   }
/*      */ 
/*      */   public static long getNextPriceAggregationBarStart(long time)
/*      */   {
/* 1136 */     return time + 1L;
/*      */   }
/*      */ 
/*      */   public static long getTradingSessionStart(long time)
/*      */   {
/* 1146 */     Calendar gmtCalendar = (Calendar)gmtCalendarThreadLocal.get();
/* 1147 */     gmtCalendar.setTimeInMillis(time);
/*      */ 
/* 1149 */     int dayOfWeek = gmtCalendar.get(7);
/*      */ 
/* 1151 */     gmtCalendar.add(7, -(dayOfWeek - 1));
/*      */ 
/* 1153 */     gmtCalendar.set(11, 21);
/* 1154 */     gmtCalendar.set(12, 0);
/* 1155 */     gmtCalendar.set(13, 0);
/* 1156 */     gmtCalendar.set(14, 0);
/*      */ 
/* 1158 */     long result = gmtCalendar.getTime().getTime();
/*      */ 
/* 1160 */     return result;
/*      */   }
/*      */ 
/*      */   public static long getNTradingSessionStart(long time, int n) {
/* 1164 */     time = getTradingSessionStart(time);
/* 1165 */     time += n;
/* 1166 */     time = getTradingSessionStart(time);
/* 1167 */     return time;
/*      */   }
/*      */ 
/*      */   public static long getPreviousTradingSessionStart(long time) {
/* 1171 */     time = getNTradingSessionStart(time, -1);
/* 1172 */     return time;
/*      */   }
/*      */ 
/*      */   public static long getNextTradingSessionStart(long time) {
/* 1176 */     time = getNTradingSessionStart(time, -1);
/* 1177 */     return time;
/*      */   }
/*      */ 
/*      */   public static boolean isTheSameTradingSession(long time1, long time2)
/*      */   {
/* 1189 */     if (time1 == time2) {
/* 1190 */       return true;
/*      */     }
/*      */ 
/* 1193 */     long time = time1;
/* 1194 */     if (time1 < time2) {
/* 1195 */       time1 = time2;
/* 1196 */       time2 = time;
/*      */     }
/*      */ 
/* 1199 */     long tradingSessionStart1 = getTradingSessionStart(time1);
/* 1200 */     if (tradingSessionStart1 <= time2) {
/* 1201 */       return true;
/*      */     }
/*      */ 
/* 1204 */     long tradingSessionStart2 = getTradingSessionStart(time2);
/* 1205 */     return tradingSessionStart1 == tradingSessionStart2;
/*      */   }
/*      */ 
/*      */   public static List<Period> getOldBasicPeriods()
/*      */   {
/* 1216 */     List result = new ArrayList();
/*      */ 
/* 1218 */     result.add(Period.TICK);
/* 1219 */     result.add(Period.TEN_SECS);
/* 1220 */     result.add(Period.ONE_MIN);
/* 1221 */     result.add(Period.FIVE_MINS);
/* 1222 */     result.add(Period.TEN_MINS);
/* 1223 */     result.add(Period.FIFTEEN_MINS);
/* 1224 */     result.add(Period.THIRTY_MINS);
/* 1225 */     result.add(Period.ONE_HOUR);
/* 1226 */     result.add(Period.FOUR_HOURS);
/* 1227 */     result.add(Period.DAILY);
/* 1228 */     result.add(Period.WEEKLY);
/* 1229 */     result.add(Period.MONTHLY);
/*      */ 
/* 1231 */     return result;
/*      */   }
/*      */ 
/*      */   public static Period getOldBasicPeriodFromInterval(long interval) {
/* 1235 */     if ((interval == 0L) || (interval == -1L)) {
/* 1236 */       return Period.TICK;
/*      */     }
/*      */ 
/* 1239 */     for (Period period : getOldBasicPeriods()) {
/* 1240 */       if (period.getInterval() == interval) {
/* 1241 */         return period;
/*      */       }
/*      */     }
/* 1244 */     return null;
/*      */   }
/*      */ 
/*      */   static
/*      */   {
/*   45 */     intraPeriodFileDateFormat = new SimpleDateFormat("yyyy_MM_dd_HH_");
/*   46 */     intraPeriodFileDateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
/*      */   }
/*      */ 
/*      */   public static class ToLoad
/*      */   {
/*      */     public Period period;
/*      */     public long from;
/*      */     public long to;
/*      */ 
/*      */     public ToLoad(Period period, long from, long to)
/*      */     {
/* 1113 */       this.period = period;
/* 1114 */       this.from = from;
/* 1115 */       this.to = to;
/*      */     }
/*      */   }
/*      */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.charts.data.datacache.DataCacheUtils
 * JD-Core Version:    0.6.0
 */