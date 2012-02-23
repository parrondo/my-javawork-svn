/*     */ package com.dukascopy.charts.data.datacache.feed;
/*     */ 
/*     */ import com.dukascopy.api.Instrument;
/*     */ import com.dukascopy.api.OfferSide;
/*     */ import com.dukascopy.api.Period;
/*     */ import com.dukascopy.charts.data.datacache.CandleData;
/*     */ import com.dukascopy.charts.data.datacache.Data;
/*     */ import com.dukascopy.charts.data.datacache.DataCacheUtils;
/*     */ import com.dukascopy.charts.data.datacache.TickData;
/*     */ import com.dukascopy.transport.common.msg.request.FeedCommission;
/*     */ import java.math.BigDecimal;
/*     */ import java.text.SimpleDateFormat;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import java.util.Calendar;
/*     */ import java.util.Comparator;
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.TimeZone;
/*     */ import org.slf4j.Logger;
/*     */ import org.slf4j.LoggerFactory;
/*     */ 
/*     */ public class FeedCommissionManager
/*     */   implements IFeedCommissionManager
/*     */ {
/*  28 */   private static final Logger LOGGER = LoggerFactory.getLogger(FeedCommissionManager.class);
/*     */ 
/*  30 */   protected static final TimeZone GMT_TIME_ZONE = TimeZone.getTimeZone("GMT 0");
/*  31 */   protected static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");
/*     */   private static final String NULL_TOKEN = "null";
/*  38 */   private final Map<Instrument, IInstrumentFeedCommissionInfo[]> instrumentFeedCommissionMap = new HashMap();
/*  39 */   private final double ZERO_COMMISSION = 0.0D;
/*     */ 
/*  41 */   private final Comparator<IInstrumentFeedCommissionInfo> FEED_COMMISSION_COMPARATOR = new Comparator()
/*     */   {
/*     */     public int compare(IInstrumentFeedCommissionInfo o1, IInstrumentFeedCommissionInfo o2) {
/*  44 */       if (o1.getStart() > o2.getStart()) {
/*  45 */         return 1;
/*     */       }
/*  47 */       if (o1.getStart() < o2.getStart()) {
/*  48 */         return -1;
/*     */       }
/*     */ 
/*  51 */       if (o1.getEnd() == o2.getEnd()) {
/*  52 */         if (o1.getPriority() > o2.getPriority()) {
/*  53 */           return 1;
/*     */         }
/*     */ 
/*  56 */         return -1;
/*     */       }
/*     */ 
/*  60 */       return 0;
/*     */     }
/*  41 */   };
/*     */ 
/*     */   public FeedCommissionManager()
/*     */   {
/*     */   }
/*     */ 
/*     */   public FeedCommissionManager(List<String[]> feedCommissionsFromAuthServer)
/*     */   {
/*  73 */     setupFeedCommissionsFromAuthServer(feedCommissionsFromAuthServer);
/*     */   }
/*     */ 
/*     */   public void addFeedCommissions(Map<String, FeedCommission> feedCommissions)
/*     */   {
/*  78 */     addFeedCommissions(feedCommissions, null);
/*     */   }
/*     */ 
/*     */   public void addFeedCommissions(Map<String, FeedCommission> feedCommissions, Long time)
/*     */   {
/*  83 */     if ((feedCommissions == null) || (feedCommissions.isEmpty())) {
/*  84 */       return;
/*     */     }
/*     */ 
/*  87 */     List fc = convert(feedCommissions, time);
/*  88 */     addFeedCommissions(fc);
/*     */   }
/*     */ 
/*     */   public void addFeedCommissions(Map<String, FeedCommission> feedCommissions, long time)
/*     */   {
/*  93 */     addFeedCommissions(feedCommissions, new Long(time));
/*     */   }
/*     */ 
/*     */   public void addFeedCommissions(List<IInstrumentFeedCommissionInfo> feedCommissions)
/*     */   {
/*  99 */     if ((feedCommissions == null) || (feedCommissions.isEmpty())) {
/* 100 */       return;
/*     */     }
/*     */ 
/* 103 */     synchronized (feedCommissions) {
/* 104 */       addToFeedCommissionsMap(feedCommissions);
/*     */     }
/*     */   }
/*     */ 
/*     */   public double getFeedCommission(Instrument instrument, long time)
/*     */   {
/* 110 */     if (!hasCommission(instrument)) {
/* 111 */       return 0.0D;
/*     */     }
/*     */ 
/* 114 */     IInstrumentFeedCommissionInfo[] feedCommissions = null;
/*     */ 
/* 116 */     synchronized (this.instrumentFeedCommissionMap) {
/* 117 */       feedCommissions = (IInstrumentFeedCommissionInfo[])this.instrumentFeedCommissionMap.get(instrument);
/*     */ 
/* 119 */       if ((feedCommissions != null) && (feedCommissions.length > 0))
/*     */       {
/* 128 */         for (int i = feedCommissions.length - 1; i >= 0; i--) {
/* 129 */           IInstrumentFeedCommissionInfo fci = feedCommissions[i];
/* 130 */           if (fci.isInInterval(time)) {
/* 131 */             return fci.getFeedCommission() * instrument.getPipValue();
/*     */           }
/*     */         }
/*     */       }
/*     */ 
/* 136 */       return 0.0D;
/*     */     }
/*     */   }
/*     */ 
/*     */   public void setupFeedCommissions(List<IInstrumentFeedCommissionInfo> feedCommissions)
/*     */   {
/* 143 */     if ((feedCommissions == null) || (feedCommissions.isEmpty())) {
/* 144 */       return;
/*     */     }
/*     */ 
/* 147 */     synchronized (this.instrumentFeedCommissionMap) {
/* 148 */       this.instrumentFeedCommissionMap.clear();
/*     */ 
/* 150 */       addToFeedCommissionsMap(feedCommissions);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void setupFeedCommissionsFromAuthServer(List<String[]> feedCommissions)
/*     */   {
/* 156 */     if ((feedCommissions == null) || (feedCommissions.isEmpty())) {
/* 157 */       return;
/*     */     }
/*     */     try
/*     */     {
/* 161 */       List result = convert(feedCommissions);
/* 162 */       setupFeedCommissions(result);
/*     */     } catch (Throwable t) {
/* 164 */       LOGGER.error("Failed to setup feed commission history " + t.getMessage(), t);
/*     */     }
/*     */   }
/*     */ 
/*     */   public TickData applyFeedCommissionToTick(Instrument instrument, TickData tick)
/*     */   {
/* 170 */     synchronized (this.instrumentFeedCommissionMap) {
/* 171 */       if (!doWeHaveCommission(instrument)) {
/* 172 */         return tick;
/*     */       }
/*     */ 
/* 175 */       double feedCommission = getFeedCommission(instrument, tick.time);
/*     */ 
/* 177 */       if (feedCommission != 0.0D) {
/* 178 */         applyCommission(instrument, tick, feedCommission);
/*     */       }
/*     */ 
/* 181 */       return tick;
/*     */     }
/*     */   }
/*     */ 
/*     */   public void clear()
/*     */   {
/* 188 */     synchronized (this.instrumentFeedCommissionMap) {
/* 189 */       this.instrumentFeedCommissionMap.clear();
/*     */     }
/*     */   }
/*     */ 
/*     */   public double getPriceWithCommission(Instrument instrument, OfferSide side, double price, long time)
/*     */   {
/* 200 */     synchronized (this.instrumentFeedCommissionMap) {
/* 201 */       if (!doWeHaveCommission(instrument)) {
/* 202 */         return price;
/*     */       }
/*     */ 
/* 205 */       double feedCommission = getFeedCommission(instrument, time);
/* 206 */       double priceWithCommission = price;
/*     */ 
/* 208 */       if (feedCommission != 0.0D) {
/* 209 */         priceWithCommission = DataCacheUtils.getPriceWithCommission(instrument, side, price, feedCommission);
/*     */       }
/*     */ 
/* 212 */       return priceWithCommission;
/*     */     }
/*     */   }
/*     */ 
/*     */   public CandleData applyFeedCommissionToCandle(Instrument instrument, OfferSide side, CandleData candle)
/*     */   {
/* 223 */     synchronized (this.instrumentFeedCommissionMap) {
/* 224 */       if (!doWeHaveCommission(instrument)) {
/* 225 */         return candle;
/*     */       }
/*     */ 
/* 228 */       double feedCommission = getFeedCommission(instrument, candle.time);
/*     */ 
/* 230 */       if (feedCommission != 0.0D) {
/* 231 */         applyCommission(instrument, side, candle, feedCommission);
/*     */       }
/*     */ 
/* 234 */       return candle;
/*     */     }
/*     */   }
/*     */ 
/*     */   public Data[] applyFeedCommissionToData(Instrument instrument, Period period, OfferSide offerSide, Data[] data)
/*     */   {
/* 246 */     synchronized (this.instrumentFeedCommissionMap) {
/* 247 */       if (!doWeHaveCommission(instrument)) {
/* 248 */         return data;
/*     */       }
/*     */ 
/* 251 */       if (period == Period.TICK) {
/* 252 */         for (Data dataElem : data) {
/* 253 */           TickData tick = (TickData)dataElem;
/* 254 */           double feedCommission = getFeedCommission(instrument, tick.time);
/* 255 */           if (feedCommission != 0.0D) {
/* 256 */             applyCommission(instrument, tick, feedCommission);
/*     */           }
/*     */         }
/*     */       }
/*     */       else {
/* 261 */         for (Data dataElem : data) {
/* 262 */           CandleData candle = (CandleData)dataElem;
/* 263 */           double feedCommission = getFeedCommission(instrument, candle.time);
/* 264 */           if (feedCommission != 0.0D) {
/* 265 */             applyCommission(instrument, offerSide, candle, feedCommission);
/*     */           }
/*     */         }
/*     */       }
/* 269 */       return data;
/*     */     }
/*     */   }
/*     */ 
/*     */   public boolean hasCommission(Instrument instrument)
/*     */   {
/* 276 */     synchronized (this.instrumentFeedCommissionMap) {
/* 277 */       return doWeHaveCommission(instrument);
/*     */     }
/*     */   }
/*     */ 
/*     */   private boolean doWeHaveCommission(Instrument instrument) {
/* 282 */     boolean contains = this.instrumentFeedCommissionMap.containsKey(instrument);
/* 283 */     return contains;
/*     */   }
/*     */ 
/*     */   private List<IInstrumentFeedCommissionInfo> convert(Map<String, FeedCommission> feedCommissions, Long time)
/*     */   {
/* 288 */     if ((feedCommissions == null) || (feedCommissions.isEmpty())) {
/* 289 */       return null;
/*     */     }
/*     */ 
/* 292 */     Calendar calendar = Calendar.getInstance(GMT_TIME_ZONE);
/*     */ 
/* 294 */     if ((time == null) || (time.longValue() <= 0L)) {
/* 295 */       time = new Long(System.currentTimeMillis());
/*     */     }
/*     */ 
/* 298 */     calendar.setTimeInMillis(time.longValue());
/* 299 */     List result = new ArrayList();
/*     */ 
/* 301 */     for (String key : feedCommissions.keySet()) {
/*     */       try
/*     */       {
/* 304 */         FeedCommission fc = (FeedCommission)feedCommissions.get(key);
/* 305 */         BigDecimal feedCommissionBD = fc.getFeedCommssion();
/*     */ 
/* 307 */         if (feedCommissionBD == null)
/*     */         {
/*     */           continue;
/*     */         }
/* 311 */         Instrument instrument = parseInstrument(key);
/* 312 */         double feedCommission = feedCommissionBD.doubleValue();
/*     */ 
/* 314 */         IInstrumentFeedCommissionInfo info = new InstrumentFeedCommissionInfo(instrument, feedCommission, calendar.getTimeInMillis(), 9223372036854775807L);
/* 315 */         result.add(info);
/*     */       }
/*     */       catch (Throwable t) {
/* 318 */         LOGGER.error("Failed to add feed commission history for instrument " + key + " " + t.getMessage(), t);
/*     */       }
/*     */     }
/*     */ 
/* 322 */     return result;
/*     */   }
/*     */ 
/*     */   private List<IInstrumentFeedCommissionInfo> convert(List<String[]> feedCommissions) {
/* 326 */     List result = new ArrayList();
/*     */ 
/* 328 */     for (String[] feedCommissionArray : feedCommissions) {
/* 329 */       if (feedCommissionArray != null) {
/* 330 */         int LENGTH = 6;
/* 331 */         if (feedCommissionArray.length != 6) {
/* 332 */           throw new IllegalArgumentException("Wrong commission record length! Desired <6> got <" + feedCommissionArray.length + "> for commission record <" + Arrays.toString(feedCommissionArray) + ">");
/*     */         }
/*     */ 
/* 338 */         Instrument instrument = parseInstrument(feedCommissionArray[1]);
/* 339 */         Double feedCommission = parseDouble(feedCommissionArray[2]);
/* 340 */         Long from = parseLong(feedCommissionArray[3]);
/* 341 */         Long to = parseLong(feedCommissionArray[4]);
/* 342 */         Long priority = parseLong(feedCommissionArray[5]);
/*     */ 
/* 344 */         if (feedCommission == null) {
/* 345 */           throw new IllegalArgumentException("Feed commission value is null");
/*     */         }
/*     */ 
/* 348 */         if (priority == null) {
/* 349 */           throw new IllegalArgumentException("Priority value is null");
/*     */         }
/*     */ 
/* 352 */         if (from == null) {
/* 353 */           from = Long.valueOf(-9223372036854775808L);
/*     */         }
/*     */ 
/* 356 */         if (to == null) {
/* 357 */           to = Long.valueOf(9223372036854775807L);
/*     */         }
/*     */ 
/* 360 */         if (from.longValue() > to.longValue()) {
/* 361 */           throw new IllegalArgumentException("from > to ");
/*     */         }
/*     */ 
/* 364 */         IInstrumentFeedCommissionInfo instrumentFeedCommissionInfo = new InstrumentFeedCommissionInfo(instrument, feedCommission.doubleValue(), from.longValue(), to.longValue(), priority.longValue());
/*     */ 
/* 372 */         result.add(instrumentFeedCommissionInfo);
/*     */       }
/*     */     }
/*     */ 
/* 376 */     return result;
/*     */   }
/*     */ 
/*     */   private Double parseDouble(String str) {
/* 380 */     if ((str == null) || ("null".equalsIgnoreCase(str))) {
/* 381 */       return null;
/*     */     }
/*     */ 
/* 384 */     return Double.valueOf(str);
/*     */   }
/*     */ 
/*     */   private Instrument parseInstrument(String str)
/*     */   {
/* 389 */     if ((str == null) || ("null".equalsIgnoreCase(str))) {
/* 390 */       return null;
/*     */     }
/*     */ 
/* 393 */     Instrument result = Instrument.fromString(str);
/* 394 */     if (result == null) {
/* 395 */       result = Instrument.valueOf(str);
/*     */     }
/*     */ 
/* 398 */     return result;
/*     */   }
/*     */ 
/*     */   private Long parseLong(String str) {
/* 402 */     if ((str == null) || ("null".equalsIgnoreCase(str))) {
/* 403 */       return null;
/*     */     }
/*     */ 
/* 406 */     return Long.valueOf(str);
/*     */   }
/*     */ 
/*     */   private void addToFeedCommissionsMap(List<IInstrumentFeedCommissionInfo> instrumentFeedCommissions)
/*     */   {
/* 411 */     if (instrumentFeedCommissions == null) {
/* 412 */       return;
/*     */     }
/*     */ 
/* 415 */     long maxPriority = getMaxPriority(this.instrumentFeedCommissionMap, instrumentFeedCommissions);
/* 416 */     setUpPriorities(instrumentFeedCommissions, maxPriority);
/*     */ 
/* 418 */     Map commissionsMap = createCommissionsMap(instrumentFeedCommissions);
/* 419 */     commissionsMap = copyValuesFromSourceToTargetMap(commissionsMap, this.instrumentFeedCommissionMap);
/*     */ 
/* 421 */     this.instrumentFeedCommissionMap.clear();
/*     */ 
/* 423 */     for (Instrument key : commissionsMap.keySet()) {
/* 424 */       List value = (List)commissionsMap.get(key);
/* 425 */       IInstrumentFeedCommissionInfo[] arrayValue = (IInstrumentFeedCommissionInfo[])value.toArray(new IInstrumentFeedCommissionInfo[value.size()]);
/*     */ 
/* 427 */       Arrays.sort(arrayValue, this.FEED_COMMISSION_COMPARATOR);
/* 428 */       this.instrumentFeedCommissionMap.put(key, arrayValue);
/*     */     }
/*     */   }
/*     */ 
/*     */   private void setUpPriorities(List<IInstrumentFeedCommissionInfo> instrumentFeedCommissions, long maxPriority)
/*     */   {
/* 437 */     for (IInstrumentFeedCommissionInfo info : instrumentFeedCommissions)
/* 438 */       if (info.getPriority() < 0L) {
/* 439 */         maxPriority += 1L;
/* 440 */         info.setPriority(maxPriority);
/*     */       }
/*     */   }
/*     */ 
/*     */   private long getMaxPriority(Map<Instrument, IInstrumentFeedCommissionInfo[]> instrumentFeedCommissionMap, List<IInstrumentFeedCommissionInfo> instrumentFeedCommissions)
/*     */   {
/* 449 */     long maxPriority = -9223372036854775808L;
/*     */ 
/* 451 */     for (Instrument key : instrumentFeedCommissionMap.keySet()) {
/* 452 */       for (IInstrumentFeedCommissionInfo info : (IInstrumentFeedCommissionInfo[])instrumentFeedCommissionMap.get(key)) {
/* 453 */         if (info.getPriority() > maxPriority) {
/* 454 */           maxPriority = info.getPriority();
/*     */         }
/*     */       }
/*     */     }
/* 458 */     for (IInstrumentFeedCommissionInfo info : instrumentFeedCommissions) {
/* 459 */       if (info.getPriority() > maxPriority) {
/* 460 */         maxPriority = info.getPriority();
/*     */       }
/*     */     }
/*     */ 
/* 464 */     return maxPriority;
/*     */   }
/*     */ 
/*     */   private Map<Instrument, List<IInstrumentFeedCommissionInfo>> copyValuesFromSourceToTargetMap(Map<Instrument, List<IInstrumentFeedCommissionInfo>> target, Map<Instrument, IInstrumentFeedCommissionInfo[]> source)
/*     */   {
/* 472 */     for (Instrument instrument : source.keySet()) {
/* 473 */       List list = (List)target.get(instrument);
/* 474 */       if (list == null) {
/* 475 */         list = new ArrayList();
/* 476 */         target.put(instrument, list);
/*     */       }
/*     */ 
/* 479 */       IInstrumentFeedCommissionInfo[] infos = (IInstrumentFeedCommissionInfo[])source.get(instrument);
/*     */ 
/* 481 */       if (infos != null) {
/* 482 */         for (IInstrumentFeedCommissionInfo info : infos) {
/* 483 */           list.add(info);
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/* 488 */     return target;
/*     */   }
/*     */ 
/*     */   private Map<Instrument, List<IInstrumentFeedCommissionInfo>> createCommissionsMap(List<IInstrumentFeedCommissionInfo> instrumentFeedCommissions) {
/* 492 */     Map commissiondMap = new HashMap();
/*     */ 
/* 494 */     Instrument[] allInstruments = Instrument.values();
/*     */ 
/* 496 */     for (IInstrumentFeedCommissionInfo fci : instrumentFeedCommissions) {
/* 497 */       if (fci.getFeedCommission() == 0.0D)
/*     */       {
/*     */         continue;
/*     */       }
/* 501 */       if (fci.getInstrument() == null) {
/* 502 */         for (Instrument instrument : allInstruments) {
/* 503 */           putFeedCommission(commissiondMap, instrument, fci);
/*     */         }
/*     */       }
/*     */       else {
/* 507 */         putFeedCommission(commissiondMap, fci.getInstrument(), fci);
/*     */       }
/*     */     }
/* 510 */     return commissiondMap;
/*     */   }
/*     */ 
/*     */   private void putFeedCommission(Map<Instrument, List<IInstrumentFeedCommissionInfo>> commissiondMap, Instrument instrument, IInstrumentFeedCommissionInfo commission)
/*     */   {
/* 518 */     List commissions = (List)commissiondMap.get(instrument);
/* 519 */     if (commissions == null) {
/* 520 */       commissions = new ArrayList();
/* 521 */       commissiondMap.put(instrument, commissions);
/*     */     }
/*     */ 
/* 524 */     commissions.add(commission);
/*     */   }
/*     */ 
/*     */   private void applyCommission(Instrument instrument, OfferSide side, CandleData candle, double feedCommission) {
/* 528 */     candle.open = DataCacheUtils.getPriceWithCommission(instrument, side, candle.open, feedCommission);
/* 529 */     candle.close = DataCacheUtils.getPriceWithCommission(instrument, side, candle.close, feedCommission);
/* 530 */     candle.high = DataCacheUtils.getPriceWithCommission(instrument, side, candle.high, feedCommission);
/* 531 */     candle.low = DataCacheUtils.getPriceWithCommission(instrument, side, candle.low, feedCommission);
/*     */   }
/*     */ 
/*     */   private void applyCommission(Instrument instrument, TickData tick, double feedCommission) {
/* 535 */     double askPriceWithCommission = DataCacheUtils.getPriceWithCommission(instrument, OfferSide.ASK, tick.ask, feedCommission);
/* 536 */     double bidPriceWithCommission = DataCacheUtils.getPriceWithCommission(instrument, OfferSide.BID, tick.bid, feedCommission);
/*     */ 
/* 538 */     tick.ask = askPriceWithCommission;
/* 539 */     tick.bid = bidPriceWithCommission;
/*     */   }
/*     */ 
/*     */   static
/*     */   {
/*  33 */     DATE_FORMAT.setTimeZone(GMT_TIME_ZONE);
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.charts.data.datacache.feed.FeedCommissionManager
 * JD-Core Version:    0.6.0
 */