/*      */ package com.dukascopy.api.connector;
/*      */ 
/*      */ import com.dukascopy.api.Configurable;
/*      */ import com.dukascopy.api.IAccount;
/*      */ import com.dukascopy.api.IChart;
/*      */ import com.dukascopy.api.IHistory;
/*      */ import com.dukascopy.api.Instrument;
/*      */ import com.dukascopy.api.JFException;
/*      */ import com.dukascopy.api.OfferSide;
/*      */ import com.dukascopy.api.Period;
/*      */ import com.dukascopy.api.Properties;
/*      */ import com.dukascopy.api.connector.helpers.ColorHelpers;
/*      */ import com.dukascopy.api.connector.helpers.ReflectionHelpers;
/*      */ import com.dukascopy.dds2.greed.connector.ConnectorManager;
/*      */ import com.dukascopy.dds2.greed.connector.helpers.ArrayHelpers;
/*      */ import com.dukascopy.dds2.greed.connector.helpers.CommonHelpers;
/*      */ import com.dukascopy.dds2.greed.connector.helpers.MathHelpers;
/*      */ import com.dukascopy.dds2.greed.connector.parser.util.DeclarationHelpers;
/*      */ import java.awt.Color;
/*      */ import java.lang.reflect.Array;
/*      */ import java.lang.reflect.Field;
/*      */ import java.text.DecimalFormat;
/*      */ import java.text.ParseException;
/*      */ import java.text.SimpleDateFormat;
/*      */ import java.util.Arrays;
/*      */ import java.util.Calendar;
/*      */ import java.util.Collections;
/*      */ import java.util.Date;
/*      */ import java.util.Random;
/*      */ import java.util.TimeZone;
/*      */ 
/*      */ public abstract class AbstractConnectorImpl
/*      */   implements IConst
/*      */ {
/*      */   protected static final int ARRAY_MAX_SIZE = 9501;
/*   46 */   private static DecimalFormat dfPrint = new DecimalFormat("#,##0.##########");
/*      */ 
/*      */   @Configurable("Chart")
/*   48 */   public IChart currentChart = null;
/*      */ 
/*      */   @Configurable("Instrument")
/*   51 */   public Instrument currentInstrument = Instrument.EURUSD;
/*      */ 
/*      */   @Configurable("Period")
/*   54 */   public Period currentPeriod = Period.ONE_MIN;
/*      */ 
/*   61 */   protected Properties properties = new Properties();
/*   62 */   private IConnector connector = null;
/*      */ 
/*   64 */   protected boolean isInitialized = false;
/*   65 */   private boolean isRunning = false;
/*      */ 
/*   75 */   protected double Ask = 0.0D;
/*      */ 
/*   80 */   protected int Bars = 0;
/*      */ 
/*   87 */   protected double Bid = 0.0D;
/*      */ 
/*  106 */   protected int Digits = 0;
/*      */ 
/*  151 */   protected double Point = 0.0D;
/*      */ 
/*  185 */   protected volatile long lastTickTime = 0L;
/*  186 */   protected IBox box = null;
/*  187 */   protected IAccount account = null;
/*      */ 
/*  811 */   private Random randomMath = new Random();
/*      */ 
/*      */   public abstract Instrument getCurrentInstrument();
/*      */ 
/*      */   private Instrument getInstrument(Object symbol)
/*      */   {
/*  195 */     Instrument instrument = null;
/*  196 */     if ((symbol instanceof Instrument))
/*  197 */       instrument = (Instrument)symbol;
/*  198 */     else if ((symbol instanceof String)) {
/*  199 */       instrument = fromString((String)symbol);
/*      */     }
/*      */ 
/*  205 */     return instrument;
/*      */   }
/*      */ 
/*      */   private int getPeriod(Number period) {
/*  209 */     int result = 0;
/*  210 */     if (period != null)
/*  211 */       result = period.intValue();
/*      */     else {
/*  213 */       result = (int)(this.currentPeriod.getInterval() / 60000L);
/*      */     }
/*  215 */     return result;
/*      */   }
/*      */ 
/*      */   public boolean isPeriodAllowed(int currentPeriodMQL) {
/*  219 */     boolean result = true;
/*  220 */     if ((currentPeriodMQL != 1440) && (currentPeriodMQL != 60) && (currentPeriodMQL != 240) && (currentPeriodMQL != 1) && (currentPeriodMQL != 10) && (currentPeriodMQL != 15) && (currentPeriodMQL != 30) && (currentPeriodMQL != 5) && (currentPeriodMQL != 43200) && (currentPeriodMQL != 10080))
/*      */     {
/*  230 */       result = false;
/*      */     }
/*  232 */     return result;
/*      */   }
/*      */ 
/*      */   public static final String toString(Instrument value) {
/*  236 */     if (value != null) {
/*  237 */       return value.toString();
/*      */     }
/*  239 */     return "";
/*      */   }
/*      */ 
/*      */   public static final String toString(Boolean value)
/*      */   {
/*  244 */     return value.toString();
/*      */   }
/*      */ 
/*      */   public static final String toString(Number value) {
/*  248 */     return value.toString();
/*      */   }
/*      */ 
/*      */   public static final String toString(String value) {
/*  252 */     return value;
/*      */   }
/*      */ 
/*      */   public static final int toInt(Boolean value) {
/*  256 */     return value.booleanValue() != true ? 1 : 0;
/*      */   }
/*      */ 
/*      */   public static final int toInt(Number value) {
/*  260 */     return value.intValue();
/*      */   }
/*      */ 
/*      */   public static final int toInt(Color value) {
/*  264 */     return value.getRGB();
/*      */   }
/*      */ 
/*      */   public static final long toLong(Number value) {
/*  268 */     return value.longValue();
/*      */   }
/*      */ 
/*      */   public static final long toLong(Boolean value) {
/*  272 */     return value.booleanValue() != true ? 1L : 0L;
/*      */   }
/*      */ 
/*      */   public static final double toDouble(Number value) {
/*  276 */     return value.doubleValue();
/*      */   }
/*      */ 
/*      */   public static final double toDouble(Boolean value) {
/*  280 */     return value.booleanValue() != true ? 1.0D : 0.0D;
/*      */   }
/*      */ 
/*      */   public static final boolean Bool(Number value) {
/*  284 */     return value.doubleValue() != 0.0D;
/*      */   }
/*      */ 
/*      */   public static final boolean Bool(Boolean value) {
/*  288 */     return value.booleanValue();
/*      */   }
/*      */ 
/*      */   public static final boolean Bool(boolean value) {
/*  292 */     return value;
/*      */   }
/*      */ 
/*      */   public static final Color toColor(Number value) {
/*  296 */     return ColorHelpers.colorFromInt(value.intValue());
/*      */   }
/*      */ 
/*      */   public static final Color toColor(Color value) {
/*  300 */     return value;
/*      */   }
/*      */ 
/*      */   public static final boolean compareEquals(Number value1, boolean value2) {
/*  304 */     return Bool(value1) == value2;
/*      */   }
/*      */ 
/*      */   public static final Instrument fromString(String symbol, String defSymbol)
/*      */   {
/*  319 */     String newSymbol = null;
/*  320 */     if (symbol != null) {
/*  321 */       newSymbol = new String(defSymbol);
/*      */     }
/*  323 */     return fromString(newSymbol);
/*      */   }
/*      */ 
/*      */   public static final Instrument fromString(String symbol) {
/*  327 */     Instrument instrument = null;
/*  328 */     if (symbol != null) {
/*  329 */       String newSymbol = new String(symbol);
/*  330 */       if (newSymbol.length() == 6) {
/*  331 */         newSymbol = new StringBuilder().append(newSymbol.substring(0, 3)).append("/").append(newSymbol.substring(3)).toString();
/*      */       }
/*      */ 
/*  334 */       if (newSymbol.length() != 7)
/*      */       {
/*  337 */         throw new RuntimeException("Instrument illegal size");
/*      */       }
/*  339 */       instrument = Instrument.fromString(newSymbol);
/*      */     }
/*  341 */     return instrument;
/*      */   }
/*      */ 
/*      */   public IConnector getConnector()
/*      */   {
/*  346 */     if (this.connector == null) {
/*  347 */       this.connector = getConnectorInstance();
/*      */     }
/*  349 */     return this.connector;
/*      */   }
/*      */ 
/*      */   protected void setConnector(IConnector connector) throws JFException {
/*  353 */     this.connector = connector;
/*      */   }
/*      */ 
/*      */   public void onStop() throws JFException {
/*  357 */     getConnector().onDeinit();
/*      */   }
/*      */ 
/*      */   private String objectToString(Object item) {
/*  361 */     String result = "";
/*  362 */     if ((item instanceof Instrument))
/*  363 */       result = ((Instrument)item).toString();
/*  364 */     else if ((item instanceof String))
/*  365 */       result = (String)item;
/*      */     else {
/*  367 */       result = item.toString();
/*      */     }
/*  369 */     return result;
/*      */   }
/*      */ 
/*      */   protected String StringTrimLeft(Object item)
/*      */     throws JFException
/*      */   {
/*  380 */     return objectToString(item).trim();
/*      */   }
/*      */ 
/*      */   protected String StringTrimRight(Object item)
/*      */     throws JFException
/*      */   {
/*  389 */     return objectToString(item).trim();
/*      */   }
/*      */ 
/*      */   protected String StringSubstr(Object text, Number start)
/*      */     throws JFException
/*      */   {
/*  397 */     return StringSubstr(text, start, Integer.valueOf(0));
/*      */   }
/*      */   protected String StringSubstr(Object text, Number start, Number length) throws JFException {
/*  400 */     if (text == null) {
/*  401 */       text = Instrument();
/*      */     }
/*  403 */     String value = objectToString(text);
/*  404 */     int len = length.intValue() > 0 ? length.intValue() : value.length();
/*  405 */     return value.substring(start.intValue(), start.intValue() + len);
/*      */   }
/*      */ 
/*      */   protected String StringConcatenate(Object[] str)
/*      */     throws JFException
/*      */   {
/*  422 */     String delim = "";
/*  423 */     StringBuilder builder = new StringBuilder();
/*  424 */     for (Object object : str)
/*      */     {
/*  437 */       builder.append(new StringBuilder().append(delim).append(object).toString());
/*      */     }
/*  439 */     return builder.substring(delim.length());
/*      */   }
/*      */ 
/*      */   protected int StringFind(Object text_object, Object matched_text_object)
/*      */     throws JFException
/*      */   {
/*  448 */     return StringFind(text_object, matched_text_object, 0);
/*      */   }
/*      */   protected int StringFind(Object text_object, Object matched_text_object, int start) throws JFException {
/*  451 */     String text = objectToString(text_object);
/*  452 */     String matched_text = objectToString(matched_text_object);
/*  453 */     int rc = -1;
/*  454 */     if ((text != null) && (matched_text != null)) {
/*  455 */       rc = text.indexOf(matched_text, start);
/*      */     }
/*  457 */     return rc;
/*      */   }
/*      */ 
/*      */   protected int StringLen(Object text_object)
/*      */   {
/*  464 */     String text = objectToString(text_object);
/*  465 */     return text != null ? text.length() : 0;
/*      */   }
/*      */ 
/*      */   protected int StringGetChar(Object text_object, Number pos)
/*      */   {
/*  472 */     String text = objectToString(text_object);
/*  473 */     return text.charAt(pos.intValue());
/*      */   }
/*      */ 
/*      */   protected String StringSetChar(Object text_object, Number pos, Number ch)
/*      */   {
/*  480 */     return StringSetChar(text_object, pos, (char)ch.intValue());
/*      */   }
/*      */   protected String StringSetChar(Object text_object, Number pos, char ch) {
/*  483 */     String text = objectToString(text_object);
/*  484 */     return new StringBuilder().append(text.substring(0, pos.intValue())).append(String.valueOf(ch)).append(text.substring(pos.intValue() + 1, text.length())).toString();
/*      */   }
/*      */ 
/*      */   protected double Close(Number shift, OfferSide offerSide)
/*      */     throws JFException
/*      */   {
/*  493 */     return getConnector().iClose(null, 0, shift.intValue(), offerSide);
/*      */   }
/*      */ 
/*      */   protected abstract double Close(Number paramNumber) throws JFException;
/*      */ 
/*      */   protected double iClose(Object symbol, Number timeframe, Number shift) throws JFException
/*      */   {
/*  501 */     return getConnector().iClose(getInstrument(symbol), getPeriod(timeframe), shift.intValue(), OfferSide.BID);
/*      */   }
/*      */ 
/*      */   protected double iClose(Object symbol, Number timeframe, Number shift, OfferSide offerSide)
/*      */     throws JFException
/*      */   {
/*  507 */     return getConnector().iClose(getInstrument(symbol), getPeriod(timeframe), shift.intValue(), offerSide);
/*      */   }
/*      */ 
/*      */   protected double High(Number shift, OfferSide offerSide) throws JFException
/*      */   {
/*  512 */     return getConnector().iHigh(null, 0, shift.intValue(), offerSide);
/*      */   }
/*      */ 
/*      */   protected abstract double High(Number paramNumber) throws JFException;
/*      */ 
/*      */   protected double iHigh(Object symbol, Number timeframe, Number shift, OfferSide offerSide) throws JFException {
/*  519 */     return getConnector().iHigh(getInstrument(symbol), getPeriod(timeframe), shift.intValue(), offerSide);
/*      */   }
/*      */ 
/*      */   protected double iHigh(Object symbol, Number timeframe, Number shift)
/*      */     throws JFException
/*      */   {
/*  525 */     return getConnector().iHigh(getInstrument(symbol), getPeriod(timeframe), shift.intValue(), OfferSide.BID);
/*      */   }
/*      */ 
/*      */   protected double Low(Number shift, OfferSide offerSide) throws JFException
/*      */   {
/*  530 */     return getConnector().iLow(null, 0, shift.intValue(), offerSide);
/*      */   }
/*      */ 
/*      */   protected abstract double Low(Number paramNumber) throws JFException;
/*      */ 
/*      */   protected double iLow(Object symbol, Number timeframe, Number shift, OfferSide offerSide) throws JFException {
/*  537 */     return getConnector().iLow(getInstrument(symbol), getPeriod(timeframe), shift.intValue(), offerSide);
/*      */   }
/*      */ 
/*      */   protected double iLow(Object symbol, Number timeframe, Number shift)
/*      */     throws JFException
/*      */   {
/*  543 */     return getConnector().iLow(getInstrument(symbol), getPeriod(timeframe), shift.intValue(), OfferSide.BID);
/*      */   }
/*      */ 
/*      */   protected double Open(Number shift, OfferSide offerSide) throws JFException
/*      */   {
/*  548 */     return getConnector().iOpen(null, 0, shift.intValue(), offerSide);
/*      */   }
/*      */ 
/*      */   protected abstract double Open(Number paramNumber) throws JFException;
/*      */ 
/*      */   protected double iOpen(Object symbol, Number timeframe, Number shift, OfferSide offerSide) throws JFException {
/*  555 */     return getConnector().iOpen(getInstrument(symbol), getPeriod(timeframe), shift.intValue(), offerSide);
/*      */   }
/*      */ 
/*      */   protected double iOpen(Object symbol, Number timeframe, Number shift)
/*      */     throws JFException
/*      */   {
/*  561 */     return getConnector().iOpen(getInstrument(symbol), getPeriod(timeframe), shift.intValue(), OfferSide.BID);
/*      */   }
/*      */ 
/*      */   protected abstract long Time(Number paramNumber)
/*      */     throws JFException;
/*      */ 
/*      */   protected long iTime(Object symbol, Number timeframe, Number shift) throws JFException
/*      */   {
/*  570 */     return getConnector().iTime(getInstrument(symbol), getPeriod(timeframe), shift.intValue());
/*      */   }
/*      */ 
/*      */   protected abstract double Volume(Number paramNumber) throws JFException;
/*      */ 
/*      */   protected double iVolume(Object symbol, Number timeframe, Number shift) throws JFException
/*      */   {
/*  578 */     return getConnector().iVolume(getInstrument(symbol), getPeriod(timeframe), shift.intValue());
/*      */   }
/*      */ 
/*      */   protected int iLowest(Object symbol, Number timeframe, Number type, Number count, Number start)
/*      */     throws JFException
/*      */   {
/*  584 */     return getConnector().iLowest(getInstrument(symbol), getPeriod(timeframe), type.intValue(), count.intValue(), start.intValue(), OfferSide.BID);
/*      */   }
/*      */ 
/*      */   protected int iLowest(Object symbol, Number timeframe, Number type, Number count, Number start, OfferSide offerSide)
/*      */     throws JFException
/*      */   {
/*  591 */     return getConnector().iLowest(getInstrument(symbol), getPeriod(timeframe), type.intValue(), count.intValue(), start.intValue(), offerSide);
/*      */   }
/*      */ 
/*      */   protected int Lowest(Object symbol, Number timeframe, Number type, Number count, Number start)
/*      */     throws JFException
/*      */   {
/*  598 */     return getConnector().iLowest(getInstrument(symbol), getPeriod(timeframe), type.intValue(), count.intValue(), start.intValue(), OfferSide.BID);
/*      */   }
/*      */ 
/*      */   protected int Lowest(Object symbol, Number timeframe, Number type, Number count, Number start, OfferSide offerSide)
/*      */     throws JFException
/*      */   {
/*  605 */     return getConnector().iLowest(getInstrument(symbol), getPeriod(timeframe), type.intValue(), count.intValue(), start.intValue(), offerSide);
/*      */   }
/*      */ 
/*      */   protected int iHighest(Object symbol, Number timeframe, Number type, Number count, Number start, OfferSide offerSide)
/*      */     throws JFException
/*      */   {
/*  612 */     return getConnector().iHighest(getInstrument(symbol), getPeriod(timeframe), type.intValue(), count.intValue(), start.intValue(), offerSide);
/*      */   }
/*      */ 
/*      */   protected int iHighest(Object symbol, Number timeframe, Number type, Number count, Number start)
/*      */     throws JFException
/*      */   {
/*  619 */     return getConnector().iHighest(getInstrument(symbol), getPeriod(timeframe), type.intValue(), count.intValue(), start.intValue(), OfferSide.BID);
/*      */   }
/*      */ 
/*      */   protected int Highest(Object symbol, Number timeframe, Number type, Number count, Number start, OfferSide offerSide)
/*      */     throws JFException
/*      */   {
/*  626 */     return getConnector().iHighest(getInstrument(symbol), getPeriod(timeframe), type.intValue(), count.intValue(), start.intValue(), offerSide);
/*      */   }
/*      */ 
/*      */   protected int Highest(Object symbol, Number timeframe, Number type, Number count, Number start)
/*      */     throws JFException
/*      */   {
/*  633 */     return getConnector().iHighest(getInstrument(symbol), getPeriod(timeframe), type.intValue(), count.intValue(), start.intValue(), OfferSide.BID);
/*      */   }
/*      */ 
/*      */   protected int iBarShift(Object symbol, Number timeframe, Number time, Number exact)
/*      */     throws JFException
/*      */   {
/*  640 */     return getConnector().iBarShift(getInstrument(symbol), getPeriod(timeframe), time.intValue(), Bool(exact));
/*      */   }
/*      */ 
/*      */   protected int iBarShift(Object symbol, Number timeframe, Number time, Boolean exact)
/*      */     throws JFException
/*      */   {
/*  646 */     return getConnector().iBarShift(getInstrument(symbol), getPeriod(timeframe), time.intValue(), exact.booleanValue());
/*      */   }
/*      */ 
/*      */   protected int iBarShift(Object symbol, Number timeframe, Number time)
/*      */     throws JFException
/*      */   {
/*  652 */     return getConnector().iBarShift(getInstrument(symbol), getPeriod(timeframe), time.intValue(), true);
/*      */   }
/*      */ 
/*      */   protected int iBars(Object symbol, Number timeframe) throws JFException {
/*  656 */     return getConnector().iBars(getInstrument(symbol), getPeriod(timeframe));
/*      */   }
/*      */ 
/*      */   protected void SendMail(String subject, String some_text)
/*      */     throws JFException
/*      */   {
/*  665 */     getConnector().SendMail(subject, some_text);
/*      */   }
/*      */ 
/*      */   protected int MessageBox(String message) throws JFException {
/*  669 */     return getConnector().MessageBox(message, "Message", 0);
/*      */   }
/*      */ 
/*      */   protected int MessageBox(String message, String title) throws JFException {
/*  673 */     return getConnector().MessageBox(message, title, 0);
/*      */   }
/*      */ 
/*      */   protected int MessageBox(String message, String title, Number flags) throws JFException
/*      */   {
/*  678 */     return getConnector().MessageBox(message, title, flags.intValue());
/*      */   }
/*      */ 
/*      */   protected void Alert(Object[] str) throws JFException {
/*  682 */     getConnector().Alert(str);
/*      */   }
/*      */ 
/*      */   protected void Comment(Object[] obj) throws JFException {
/*  686 */     getConnector().Comment(obj);
/*      */   }
/*      */ 
/*      */   protected void Print(Object[] str) throws JFException {
/*  690 */     getConnector().Print(str);
/*      */   }
/*      */ 
/*      */   protected double MarketInfo(Object symbol, short type) throws JFException {
/*  694 */     return getConnector().MarketInfo(getInstrument(symbol), type);
/*      */   }
/*      */ 
/*      */   protected long MarketInfo(Object symbol, long type) throws JFException {
/*  698 */     return toLong(Double.valueOf(getConnector().MarketInfo(getInstrument(symbol), (short)(int)type)));
/*      */   }
/*      */ 
/*      */   protected int MarketInfo(Object symbol, int type) throws JFException
/*      */   {
/*  703 */     return toInt(Double.valueOf(getConnector().MarketInfo(getInstrument(symbol), (short)type)));
/*      */   }
/*      */ 
/*      */   protected double MarketInfo(Object symbol, Number type) throws JFException
/*      */   {
/*  708 */     return getConnector().MarketInfo(getInstrument(symbol), type.shortValue());
/*      */   }
/*      */ 
/*      */   protected void Sleep(Number milliseconds) throws JFException
/*      */   {
/*  713 */     getConnector().Sleep(milliseconds.intValue());
/*      */   }
/*      */ 
/*      */   protected boolean PlaySound(String filename) throws JFException {
/*  717 */     return getConnector().PlaySound(filename);
/*      */   }
/*      */ 
/*      */   protected boolean GlobalVariableCheck(String name)
/*      */     throws JFException
/*      */   {
/*  724 */     return getConnector().GlobalVariableCheck(name);
/*      */   }
/*      */ 
/*      */   protected boolean GlobalVariableDel(String name) throws JFException {
/*  728 */     return getConnector().GlobalVariableDel(name);
/*      */   }
/*      */ 
/*      */   protected double GlobalVariableGet(String name) throws JFException {
/*  732 */     return getConnector().GlobalVariableGet(name);
/*      */   }
/*      */ 
/*      */   protected boolean GlobalVariableGet_Boolean(String name) throws JFException {
/*  736 */     return getConnector().GlobalVariableCheck(name);
/*      */   }
/*      */ 
/*      */   protected String GlobalVariableName(Number index) throws JFException {
/*  740 */     return getConnector().GlobalVariableName(index.intValue());
/*      */   }
/*      */ 
/*      */   protected long GlobalVariableSet(String name, double value) throws JFException
/*      */   {
/*  745 */     return getConnector().GlobalVariableSet(name, value);
/*      */   }
/*      */ 
/*      */   protected long GlobalVariableSet(String name, boolean value) throws JFException
/*      */   {
/*  750 */     return getConnector().GlobalVariableSet(name, value);
/*      */   }
/*      */ 
/*      */   protected boolean GlobalVariableSetOnCondition__(String name, double value, double check_value) throws JFException
/*      */   {
/*  755 */     return getConnector().GlobalVariableSetOnCondition__(name, value, check_value);
/*      */   }
/*      */ 
/*      */   protected int GlobalVariablesDeleteAll(String prefix_name)
/*      */     throws JFException
/*      */   {
/*  761 */     return getConnector().GlobalVariablesDeleteAll(prefix_name);
/*      */   }
/*      */ 
/*      */   protected int GlobalVariablesTotal() throws JFException {
/*  765 */     return getConnector().GlobalVariablesTotal();
/*      */   }
/*      */ 
/*      */   public double MathAbs(Number value)
/*      */   {
/*  775 */     return Math.abs(value.doubleValue());
/*      */   }
/*      */ 
/*      */   public double MathMax(Number value1, Number value2) {
/*  779 */     return Math.max(value1.doubleValue(), value2.doubleValue());
/*      */   }
/*      */ 
/*      */   public double MathMin(Number value1, Number value2) {
/*  783 */     return Math.min(value1.doubleValue(), value2.doubleValue());
/*      */   }
/*      */ 
/*      */   public double MathFloor(Number value) {
/*  787 */     return Math.floor(value.doubleValue());
/*      */   }
/*      */ 
/*      */   public double MathCeil(Number value) {
/*  791 */     return Math.ceil(value.doubleValue());
/*      */   }
/*      */ 
/*      */   public int MathRound(Number value)
/*      */   {
/*  799 */     return (int)Math.round(value.doubleValue());
/*      */   }
/*      */ 
/*      */   public void MathSrand(Number seed)
/*      */   {
/*  813 */     if (seed.longValue() == 1L)
/*  814 */       this.randomMath.setSeed(System.currentTimeMillis());
/*      */     else
/*  816 */       this.randomMath.setSeed(seed.longValue());
/*      */   }
/*      */ 
/*      */   public double MathLog(Number x)
/*      */   {
/*  829 */     return Math.log(x.doubleValue());
/*      */   }
/*      */ 
/*      */   protected int MathRand()
/*      */   {
/*  840 */     return this.randomMath.nextInt(32767);
/*      */   }
/*      */ 
/*      */   public double MathPow(Number base, Number exponent)
/*      */   {
/*  846 */     return Math.pow(base.doubleValue(), exponent.doubleValue());
/*      */   }
/*      */ 
/*      */   protected double MathSqrt(Number x)
/*      */   {
/*  852 */     if (x.doubleValue() <= 0.0D) {
/*  853 */       return (0.0D / 0.0D);
/*      */     }
/*  855 */     return Math.sqrt(x.doubleValue());
/*      */   }
/*      */ 
/*      */   protected double MathArccos(Number x) {
/*  859 */     return Math.acos(x.doubleValue());
/*      */   }
/*      */ 
/*      */   protected double MathArcsin(Number x) {
/*  863 */     return Math.asin(x.doubleValue());
/*      */   }
/*      */ 
/*      */   protected double MathArctan(Number x) {
/*  867 */     return Math.atan(x.doubleValue());
/*      */   }
/*      */ 
/*      */   protected double MathCos(Number x) {
/*  871 */     return Math.cos(x.doubleValue());
/*      */   }
/*      */ 
/*      */   protected double MathExp(Number value) {
/*  875 */     return Math.exp(value.doubleValue());
/*      */   }
/*      */ 
/*      */   protected double MathMod(Number x, Number y) {
/*  879 */     return x.doubleValue() % y.doubleValue();
/*      */   }
/*      */ 
/*      */   protected double MathSin(Number x) {
/*  883 */     return Math.sin(x.doubleValue());
/*      */   }
/*      */ 
/*      */   protected double MathTan(Number x) {
/*  887 */     return Math.tan(x.doubleValue());
/*      */   }
/*      */ 
/*      */   protected int ArrayCopy(Object dest, Object source, int start_dest, Number start_source, Number count)
/*      */   {
/*  902 */     int result = -1;
/*  903 */     System.arraycopy(source, start_source.intValue(), dest, start_dest, count.intValue());
/*  904 */     result = count.intValue();
/*  905 */     return result;
/*      */   }
/*      */ 
/*      */   protected int ArrayCopy(Object dest, Object source) throws JFException {
/*  909 */     return ArrayCopy(dest, source, 0, Integer.valueOf(0), Integer.valueOf(Array.getLength(source)));
/*      */   }
/*      */ 
/*      */   protected int ArraySort(double[] array)
/*      */   {
/*  918 */     return ArraySort(array, Integer.valueOf(array.length));
/*      */   }
/*      */ 
/*      */   protected int ArraySort(double[] array, Number count) {
/*  922 */     return ArraySort(array, count, Integer.valueOf(0));
/*      */   }
/*      */ 
/*      */   protected int ArraySort(double[] array, Number count, Number start) {
/*  926 */     return ArraySort(array, count, start, Integer.valueOf(0));
/*      */   }
/*      */ 
/*      */   protected int ArraySort(double[] array, Number count, Number start, Number sort_dir) {
/*  930 */     if (sort_dir.intValue() == 1) {
/*  931 */       Double[] doubleArray = ArrayHelpers.toObject(array);
/*  932 */       Arrays.sort(doubleArray, start.intValue(), start.intValue() + count.intValue(), Collections.reverseOrder());
/*      */ 
/*  934 */       System.arraycopy(ArrayHelpers.toPrimitive(doubleArray), 0, array, 0, doubleArray.length);
/*      */     }
/*      */     else {
/*  937 */       Arrays.sort(array, start.intValue(), start.intValue() + count.intValue());
/*      */     }
/*  939 */     return 0;
/*      */   }
/*      */ 
/*      */   protected int ArrayBsearch(double[] array, Number value)
/*      */   {
/*  971 */     return Arrays.binarySearch(array, value.doubleValue());
/*      */   }
/*      */ 
/*      */   protected int ArrayBsearch(double[] array, Number value, Number count) {
/*  975 */     return ArrayBsearch(array, value, count, Integer.valueOf(0), Integer.valueOf(0));
/*      */   }
/*      */ 
/*      */   protected int ArrayBsearch(double[] array, Number value, Number count, Number start) {
/*  979 */     return ArrayBsearch(array, value, count, start, Integer.valueOf(0));
/*      */   }
/*      */ 
/*      */   protected int ArrayBsearch(double[] array, Number value, Number count, Number start, Number direction) {
/*  983 */     int result = -1;
/*  984 */     int end = start.intValue() + count.intValue();
/*  985 */     if (count.intValue() == 0) {
/*  986 */       end = array.length - 1;
/*      */     }
/*  988 */     if (direction.intValue() == 0) {
/*  989 */       result = Arrays.binarySearch(array, start.intValue(), end, value.doubleValue());
/*      */     } else {
/*  991 */       Double[] doubleArray = ArrayHelpers.toObject(array);
/*  992 */       result = Arrays.binarySearch(doubleArray, start.intValue(), end, Double.valueOf(value.doubleValue()), Collections.reverseOrder());
/*      */ 
/*  994 */       array = ArrayHelpers.toPrimitive(doubleArray);
/*      */     }
/*  996 */     return result;
/*      */   }
/*      */ 
/*      */   protected int ArrayCopyRates(Object dest_array, Object symbol, Number timeframe)
/*      */     throws JFException
/*      */   {
/* 1033 */     return getConnector().ArrayCopyRates(dest_array, getInstrument(symbol), timeframe.intValue());
/*      */   }
/*      */ 
/*      */   protected int ArrayCopySeries(Object array, Number series_index, Object symbol, Number timeframe)
/*      */     throws JFException
/*      */   {
/* 1039 */     return getConnector().ArrayCopySeries(array, series_index.intValue(), getInstrument(symbol), timeframe.intValue());
/*      */   }
/*      */ 
/*      */   public int ArrayDimension(Object array)
/*      */   {
/* 1051 */     return ArrayHelpers.ArrayDimensionInfo(array)[0];
/*      */   }
/*      */ 
/*      */   protected boolean ArrayGetAsSeries(Object array)
/*      */     throws JFException
/*      */   {
/* 1058 */     return getConnector().ArrayGetAsSeries(array);
/*      */   }
/*      */ 
/*      */   protected int ArrayInitialize(Object obj, int value)
/*      */     throws JFException
/*      */   {
/* 1076 */     int count = 0;
/* 1077 */     int[] dim = ArrayHelpers.ArrayDimensionInfo(obj);
/*      */ 
/* 1079 */     if ((dim == null) || (dim.length < 1)) {
/* 1080 */       CommonHelpers.notOpperationSupported();
/*      */     }
/*      */ 
/* 1083 */     if (dim.length == 1) {
/* 1084 */       for (int i = 0; i < dim[0]; count++) {
/* 1085 */         Array.set(obj, i, Integer.valueOf(value));
/*      */ 
/* 1084 */         i++;
/*      */       }
/*      */     }
/* 1087 */     else if (dim.length == 2) {
/* 1088 */       for (int i = 0; i < dim[0]; i++) {
/* 1089 */         Object firstLevelObject = Array.get(obj, i);
/* 1090 */         for (int j = 0; j < dim[1]; count++) {
/* 1091 */           Array.set(firstLevelObject, j, Integer.valueOf(value));
/*      */ 
/* 1090 */           j++;
/*      */         }
/*      */       }
/*      */     }
/* 1094 */     else if (dim.length == 3) {
/* 1095 */       for (int i = 0; i < dim[0]; i++) {
/* 1096 */         Object firstLevelObject = Array.get(obj, i);
/* 1097 */         for (int j = 0; j < dim[1]; j++) {
/* 1098 */           Object secondtLevelObject = Array.get(firstLevelObject, i);
/* 1099 */           for (int k = 0; k < dim[2]; count++) {
/* 1100 */             Array.set(secondtLevelObject, k, Integer.valueOf(value));
/*      */ 
/* 1099 */             k++;
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */ 
/* 1105 */     return count;
/*      */   }
/*      */   protected int ArrayInitialize(Object obj, double value) throws JFException {
/* 1108 */     int count = 0;
/* 1109 */     int[] dim = ArrayHelpers.ArrayDimensionInfo(obj);
/*      */ 
/* 1111 */     if ((dim == null) || (dim.length < 1)) {
/* 1112 */       CommonHelpers.notOpperationSupported();
/*      */     }
/*      */ 
/* 1115 */     if (dim.length == 1) {
/* 1116 */       for (int i = 0; i < dim[0]; count++) {
/* 1117 */         Array.set(obj, i, Double.valueOf(value));
/*      */ 
/* 1116 */         i++;
/*      */       }
/*      */     }
/* 1119 */     else if (dim.length == 2) {
/* 1120 */       for (int i = 0; i < dim[0]; i++) {
/* 1121 */         Object firstLevelObject = Array.get(obj, i);
/* 1122 */         for (int j = 0; j < dim[1]; count++) {
/* 1123 */           Array.set(firstLevelObject, j, Double.valueOf(value));
/*      */ 
/* 1122 */           j++;
/*      */         }
/*      */       }
/*      */     }
/* 1126 */     else if (dim.length == 3) {
/* 1127 */       for (int i = 0; i < dim[0]; i++) {
/* 1128 */         Object firstLevelObject = Array.get(obj, i);
/* 1129 */         for (int j = 0; j < dim[1]; j++) {
/* 1130 */           Object secondtLevelObject = Array.get(firstLevelObject, i);
/* 1131 */           for (int k = 0; k < dim[2]; count++) {
/* 1132 */             Array.set(secondtLevelObject, k, Double.valueOf(value));
/*      */ 
/* 1131 */             k++;
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */ 
/* 1137 */     return count;
/*      */   }
/*      */ 
/*      */   protected boolean ArrayIsSeries(Object array) throws JFException {
/* 1141 */     return getConnector().ArrayIsSeries(array);
/*      */   }
/*      */ 
/*      */   protected int ArrayMaximum(double[] array, Number count, Number start)
/*      */   {
/* 1157 */     int pos = -1;
/* 1158 */     if ((array != null) && (array.length > 0)) {
/* 1159 */       int end = start.intValue() + count.intValue();
/* 1160 */       int length = count.intValue();
/* 1161 */       if (count.intValue() == 0) {
/* 1162 */         end = array.length - 1;
/* 1163 */         length = array.length;
/*      */       }
/* 1165 */       double[] arrayCopy = new double[length];
/* 1166 */       arrayCopy = Arrays.copyOfRange(array, start.intValue(), end);
/* 1167 */       Arrays.sort(arrayCopy);
/* 1168 */       double max = arrayCopy[(arrayCopy.length - 1)];
/* 1169 */       pos = Arrays.binarySearch(array, max);
/*      */     }
/* 1171 */     return pos;
/*      */   }
/*      */ 
/*      */   protected int ArrayMaximum(double[] array) throws JFException {
/* 1175 */     return ArrayMaximum(array, Integer.valueOf(0), Integer.valueOf(0));
/*      */   }
/*      */   protected int ArrayMaximum(double[] array, Number count) throws JFException {
/* 1178 */     return ArrayMaximum(array, count, Integer.valueOf(0));
/*      */   }
/*      */ 
/*      */   protected int ArrayMinimum(double[] array, Number count, Number start)
/*      */   {
/* 1194 */     int pos = -1;
/* 1195 */     int end = start.intValue() + count.intValue();
/* 1196 */     int length = count.intValue();
/* 1197 */     if (count.intValue() == 0) {
/* 1198 */       end = array.length - 1;
/* 1199 */       length = array.length;
/*      */     }
/* 1201 */     double[] arrayCopy = new double[length];
/* 1202 */     arrayCopy = Arrays.copyOfRange(array, start.intValue(), end);
/* 1203 */     Arrays.sort(arrayCopy);
/* 1204 */     double min = arrayCopy[0];
/* 1205 */     pos = Arrays.binarySearch(array, start.intValue(), start.intValue() + count.intValue(), min);
/* 1206 */     return pos;
/*      */   }
/*      */   protected int ArrayMinimum(double[] array) throws JFException {
/* 1209 */     return ArrayMinimum(array, Integer.valueOf(0), Integer.valueOf(0));
/*      */   }
/*      */   protected int ArrayMinimum(double[] array, Number count) throws JFException {
/* 1212 */     return ArrayMinimum(array, count, Integer.valueOf(0));
/*      */   }
/*      */ 
/*      */   protected int ArrayRange(Object array, int range_index)
/*      */   {
/* 1228 */     int rc = 0;
/* 1229 */     int dimention = 0;
/* 1230 */     if (array != null) {
/* 1231 */       Class c = array.getClass();
/* 1232 */       while ((c.isArray()) && (dimention <= range_index)) {
/* 1233 */         rc = Array.getLength(c);
/* 1234 */         c = c.getComponentType();
/*      */       }
/*      */     }
/* 1237 */     return rc;
/*      */   }
/*      */ 
/*      */   protected int ArrayResize(Object array, Number new_size)
/*      */     throws JFException
/*      */   {
/* 1257 */     int result = -1;
/* 1258 */     Field array_field = ReflectionHelpers.getVariableField(this, array);
/* 1259 */     if (array_field != null) {
/* 1260 */       Object new_array = ArrayHelpers.resizeArray(array, new_size.intValue());
/*      */       try {
/* 1262 */         array_field.set(this, new_array);
/*      */       }
/*      */       catch (IllegalArgumentException e) {
/* 1265 */         e.printStackTrace();
/*      */       }
/*      */       catch (IllegalAccessException e) {
/* 1268 */         e.printStackTrace();
/*      */       }
/* 1270 */       result = new_size.intValue();
/*      */     }
/* 1272 */     return result;
/*      */   }
/*      */ 
/*      */   protected boolean ArraySetAsSeries(Object array, boolean set)
/*      */   {
/* 1277 */     return set;
/*      */   }
/*      */ 
/*      */   public int ArraySize(Object array)
/*      */   {
/* 1287 */     return ArrayHelpers.ArraySize(array);
/*      */   }
/*      */ 
/*      */   protected boolean ObjectDelete(String name)
/*      */     throws JFException
/*      */   {
/* 1294 */     return getConnector().ObjectDelete(name);
/*      */   }
/*      */ 
/*      */   protected int ObjectsTotal(Number type) throws JFException {
/* 1298 */     return getConnector().ObjectsTotal(type.intValue());
/*      */   }
/*      */ 
/*      */   protected int ObjectsTotal() throws JFException {
/* 1302 */     return getConnector().ObjectsTotal(-1);
/*      */   }
/*      */ 
/*      */   protected String ObjectName(Number index) throws JFException {
/* 1306 */     return getConnector().ObjectName(index.intValue());
/*      */   }
/*      */ 
/*      */   protected int ObjectFind(String name) throws JFException {
/* 1310 */     return getConnector().ObjectFind(name);
/*      */   }
/*      */ 
/*      */   protected boolean ObjectCreate(String name, Number objFibo, Number window, Number price1, Number fibProjection1, Number fibProjection2, Number fibProjection3, Number fibProjection4)
/*      */     throws JFException
/*      */   {
/* 1329 */     return getConnector().ObjectCreate(name, objFibo.intValue(), window.intValue(), price1.longValue(), fibProjection1.doubleValue(), 0L, fibProjection2.doubleValue(), 0L, fibProjection3.doubleValue());
/*      */   }
/*      */ 
/*      */   protected boolean ObjectCreate(String name, Number type, Number window, Number time1, Number price1, Number time2, Number price2, Number time3, Number price3)
/*      */     throws JFException
/*      */   {
/* 1336 */     return getConnector().ObjectCreate(name, type.intValue(), window.intValue(), time1.intValue(), price1.doubleValue(), time2.intValue(), price2.doubleValue(), time3.intValue(), price3.doubleValue());
/*      */   }
/*      */ 
/*      */   protected boolean ObjectCreate(String name, Number type, Number window, Number time1, Number price1, Number time2, Number price2)
/*      */     throws JFException
/*      */   {
/* 1345 */     return getConnector().ObjectCreate(name, type.intValue(), window.intValue(), time1.intValue(), price1.doubleValue(), time2.intValue(), price2.doubleValue(), 0L, 0.0D);
/*      */   }
/*      */ 
/*      */   protected boolean ObjectCreate(String name, Number type, Number window, Number time1, Number price1)
/*      */     throws JFException
/*      */   {
/* 1352 */     return getConnector().ObjectCreate(name, type.intValue(), window.intValue(), time1.intValue(), price1.doubleValue(), 0L, 0.0D, 0L, 0.0D);
/*      */   }
/*      */ 
/*      */   protected boolean ObjectSetText(String name, String text, Number font_size, String font, Color text_color)
/*      */     throws JFException
/*      */   {
/* 1359 */     return getConnector().ObjectSetText(name, text, font_size.intValue(), font, text_color);
/*      */   }
/*      */ 
/*      */   protected boolean ObjectSetText(String name, String text)
/*      */     throws JFException
/*      */   {
/* 1365 */     return getConnector().ObjectSetText(name, text, 10, null, IColor.White);
/*      */   }
/*      */ 
/*      */   protected boolean ObjectSetText(String name, String text, Number font_size, String font, long text_color) throws JFException
/*      */   {
/* 1370 */     return getConnector().ObjectSetText(name, text, font_size.intValue(), font, IColor.White);
/*      */   }
/*      */ 
/*      */   protected void ObjectsRedraw() throws JFException
/*      */   {
/* 1375 */     getConnector().ObjectsRedraw();
/*      */   }
/*      */ 
/*      */   protected boolean ObjectSet(String name, Number index, Color value) throws JFException
/*      */   {
/* 1380 */     return getConnector().ObjectSet(name, index.intValue(), 16777215.0D);
/*      */   }
/*      */ 
/*      */   protected boolean ObjectSet(String name, Number index, boolean value) throws JFException
/*      */   {
/* 1385 */     return getConnector().ObjectSet(name, index.intValue(), value ? 1.0D : 0.0D);
/*      */   }
/*      */ 
/*      */   protected boolean ObjectSet(String name, Number index, Number value) throws JFException
/*      */   {
/* 1390 */     return getConnector().ObjectSet(name, index.intValue(), value.doubleValue());
/*      */   }
/*      */ 
/*      */   protected String ObjectDescription(String name) throws JFException
/*      */   {
/* 1395 */     return getConnector().ObjectDescription(name);
/*      */   }
/*      */ 
/*      */   protected double ObjectGet(String name, Number index) throws JFException {
/* 1399 */     return getConnector().ObjectGet(name, index.intValue());
/*      */   }
/*      */ 
/*      */   protected String ObjectGetFiboDescription(String name, Number index) throws JFException
/*      */   {
/* 1404 */     return getConnector().ObjectGetFiboDescription(name, index.intValue());
/*      */   }
/*      */ 
/*      */   protected int ObjectGetShiftByValue(String name, Number value) throws JFException
/*      */   {
/* 1409 */     return getConnector().ObjectGetShiftByValue(name, value.doubleValue());
/*      */   }
/*      */ 
/*      */   protected double ObjectGetValueByShift(String name, Number shift) throws JFException
/*      */   {
/* 1414 */     return getConnector().ObjectGetValueByShift(name, shift.intValue());
/*      */   }
/*      */ 
/*      */   protected boolean ObjectMove(String name, Number point, Number time, Number price) throws JFException
/*      */   {
/* 1419 */     return getConnector().ObjectMove(name, point.intValue(), time.intValue(), price.doubleValue());
/*      */   }
/*      */ 
/*      */   protected int ObjectsDeleteAll(Number window, Number type)
/*      */     throws JFException
/*      */   {
/* 1425 */     return getConnector().ObjectsDeleteAll(window.intValue(), type.intValue());
/*      */   }
/*      */ 
/*      */   protected int ObjectsDeleteAll(Number window) throws JFException
/*      */   {
/* 1430 */     return getConnector().ObjectsDeleteAll(window.intValue(), -1);
/*      */   }
/*      */ 
/*      */   protected int ObjectsDeleteAll() throws JFException {
/* 1434 */     return getConnector().ObjectsDeleteAll(-1, -1);
/*      */   }
/*      */ 
/*      */   protected boolean ObjectSetFiboDescription(String name, int index, String text) throws JFException
/*      */   {
/* 1439 */     return getConnector().ObjectSetFiboDescription(name, index, text);
/*      */   }
/*      */ 
/*      */   protected int ObjectType(String name) throws JFException {
/* 1443 */     return getConnector().ObjectType(name);
/*      */   }
/*      */ 
/*      */   protected double AccountFreeMargin()
/*      */     throws JFException
/*      */   {
/* 1450 */     return getConnector().AccountFreeMargin();
/*      */   }
/*      */ 
/*      */   protected double AccountFreeMarginCheck(Object symbol, Number cmd, Number volume) throws JFException
/*      */   {
/* 1455 */     return getConnector().AccountFreeMarginCheck(getInstrument(symbol), cmd.intValue(), volume.doubleValue());
/*      */   }
/*      */ 
/*      */   protected double AccountMargin() throws JFException
/*      */   {
/* 1460 */     return getConnector().AccountMargin();
/*      */   }
/*      */ 
/*      */   protected int AccountLeverage() throws JFException {
/* 1464 */     return getConnector().AccountLeverage();
/*      */   }
/*      */ 
/*      */   protected String AccountCompany() throws JFException {
/* 1468 */     return getConnector().AccountCompany();
/*      */   }
/*      */ 
/*      */   protected double AccountCredit() throws JFException {
/* 1472 */     return getConnector().AccountCredit();
/*      */   }
/*      */ 
/*      */   protected String AccountCurrency() throws JFException {
/* 1476 */     return getConnector().AccountCurrency();
/*      */   }
/*      */ 
/*      */   protected double AccountEquity() throws JFException {
/* 1480 */     return getConnector().AccountEquity();
/*      */   }
/*      */ 
/*      */   protected String AccountName() throws JFException {
/* 1484 */     return getConnector().AccountName();
/*      */   }
/*      */ 
/*      */   protected int AccountNumber() throws JFException {
/* 1488 */     return getConnector().AccountNumber();
/*      */   }
/*      */ 
/*      */   protected double AccountProfit() throws JFException {
/* 1492 */     return getConnector().AccountProfit();
/*      */   }
/*      */ 
/*      */   protected double AccountFreeMarginMode() throws JFException {
/* 1496 */     return getConnector().AccountFreeMarginMode();
/*      */   }
/*      */ 
/*      */   protected String AccountServer() throws JFException {
/* 1500 */     return getConnector().AccountServer();
/*      */   }
/*      */ 
/*      */   protected int AccountStopoutLevel() throws JFException {
/* 1504 */     return getConnector().AccountStopoutLevel();
/*      */   }
/*      */ 
/*      */   protected int AccountStopoutMode() throws JFException {
/* 1508 */     return getConnector().AccountStopoutMode();
/*      */   }
/*      */ 
/*      */   protected double AccountBalance() throws JFException {
/* 1512 */     return getConnector().AccountFreeMargin();
/*      */   }
/*      */ 
/*      */   public long TimeLocal()
/*      */   {
/* 1524 */     return System.currentTimeMillis() / 1000L;
/*      */   }
/*      */ 
/*      */   protected int DayOfYear()
/*      */     throws JFException
/*      */   {
/* 1531 */     Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
/* 1532 */     calendar.setTimeInMillis(getLastTickTime());
/* 1533 */     return calendar.get(6);
/*      */   }
/*      */ 
/*      */   public int TimeYear(Number time)
/*      */   {
/* 1541 */     Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
/* 1542 */     calendar.setTimeInMillis(time.longValue() * 1000L);
/* 1543 */     return calendar.get(1);
/*      */   }
/*      */ 
/*      */   public int TimeMonth(Number time)
/*      */   {
/* 1550 */     Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
/* 1551 */     calendar.setTimeInMillis(time.longValue() * 1000L);
/* 1552 */     return calendar.get(2) + 1;
/*      */   }
/*      */ 
/*      */   public int TimeHour(Number time)
/*      */   {
/* 1557 */     Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
/* 1558 */     calendar.setTimeInMillis(time.longValue() * 1000L);
/* 1559 */     return calendar.get(11);
/*      */   }
/*      */ 
/*      */   public int TimeDay(Number time)
/*      */   {
/* 1564 */     Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
/* 1565 */     calendar.setTimeInMillis(time.longValue() * 1000L);
/* 1566 */     return calendar.get(5);
/*      */   }
/*      */ 
/*      */   public int TimeMinute(Number time)
/*      */   {
/* 1583 */     Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
/* 1584 */     calendar.setTimeInMillis(time.longValue() * 1000L);
/* 1585 */     return calendar.get(12);
/*      */   }
/*      */ 
/*      */   public int TimeSeconds(Number time)
/*      */   {
/* 1597 */     Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
/* 1598 */     calendar.setTimeInMillis(time.longValue() * 1000L);
/* 1599 */     return calendar.get(13);
/*      */   }
/*      */ 
/*      */   public int Year()
/*      */   {
/* 1608 */     Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
/* 1609 */     calendar.setTimeInMillis(getLastTickTime());
/* 1610 */     return calendar.get(1);
/*      */   }
/*      */ 
/*      */   public int Month()
/*      */   {
/* 1619 */     Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
/* 1620 */     calendar.setTimeInMillis(getLastTickTime());
/* 1621 */     return calendar.get(2) + 1;
/*      */   }
/*      */ 
/*      */   public int Day()
/*      */   {
/* 1630 */     Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
/* 1631 */     calendar.setTimeInMillis(getLastTickTime());
/* 1632 */     return calendar.get(5);
/*      */   }
/*      */ 
/*      */   public int Hour()
/*      */   {
/* 1640 */     Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
/* 1641 */     calendar.setTimeInMillis(getLastTickTime());
/* 1642 */     return calendar.get(11);
/*      */   }
/*      */ 
/*      */   public int Minute()
/*      */   {
/* 1650 */     Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
/* 1651 */     calendar.setTimeInMillis(getLastTickTime());
/* 1652 */     return calendar.get(12);
/*      */   }
/*      */ 
/*      */   public int Seconds()
/*      */   {
/* 1663 */     Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
/* 1664 */     calendar.setTimeInMillis(getLastTickTime());
/* 1665 */     return calendar.get(13);
/*      */   }
/*      */ 
/*      */   public int DayOfWeek()
/*      */   {
/* 1672 */     Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
/* 1673 */     calendar.setTimeInMillis(getLastTickTime());
/* 1674 */     return calendar.get(7) - 1;
/*      */   }
/*      */ 
/*      */   public int TimeDayOfWeek(long date)
/*      */   {
/* 1680 */     Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
/* 1681 */     calendar.setTimeInMillis(date * 1000L);
/* 1682 */     return calendar.get(7) - 1;
/*      */   }
/*      */ 
/*      */   public int TimeDayOfYear(long date) {
/* 1686 */     Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
/* 1687 */     calendar.setTimeInMillis(getLastTickTime());
/* 1688 */     return calendar.get(6) - 1;
/*      */   }
/*      */ 
/*      */   public long TimeCurrent()
/*      */     throws JFException
/*      */   {
/* 1699 */     return getLastTickTime() / 1000L;
/*      */   }
/*      */ 
/*      */   public long CurTime()
/*      */     throws JFException
/*      */   {
/* 1709 */     return TimeCurrent();
/*      */   }
/*      */ 
/*      */   protected double OrderClosePrice()
/*      */     throws JFException
/*      */   {
/* 1716 */     return getConnector().OrderClosePrice();
/*      */   }
/*      */ 
/*      */   protected long OrderCloseTime() throws JFException {
/* 1720 */     return getConnector().OrderCloseTime();
/*      */   }
/*      */ 
/*      */   protected boolean OrderDelete(Number ticket) throws JFException {
/* 1724 */     return getConnector().OrderDelete(ticket.intValue(), IColor.White);
/*      */   }
/*      */ 
/*      */   protected boolean OrderDelete(Number ticket, Color color) throws JFException
/*      */   {
/* 1729 */     return getConnector().OrderDelete(ticket.intValue(), color);
/*      */   }
/*      */ 
/*      */   protected int OrdersHistoryTotal() throws JFException {
/* 1733 */     return getConnector().OrdersHistoryTotal();
/*      */   }
/*      */ 
/*      */   protected int HistoryTotal() throws JFException {
/* 1737 */     return getConnector().OrdersHistoryTotal();
/*      */   }
/*      */ 
/*      */   protected double OrderCommission() throws JFException {
/* 1741 */     return getConnector().OrderCommission();
/*      */   }
/*      */ 
/*      */   protected boolean OrderClose(Number ticket, Number lots, Number price, Number slippage) throws JFException
/*      */   {
/* 1746 */     return getConnector().OrderClose(ticket.intValue(), lots.doubleValue(), price.doubleValue(), slippage.intValue(), IColor.White);
/*      */   }
/*      */ 
/*      */   protected boolean OrderClose(Number ticket, Number lots, Number price, Number slippage, Color color)
/*      */     throws JFException
/*      */   {
/* 1752 */     return getConnector().OrderClose(ticket.intValue(), lots.doubleValue(), price.doubleValue(), slippage.intValue(), color);
/*      */   }
/*      */ 
/*      */   protected int OrderTicket() throws JFException
/*      */   {
/* 1757 */     return getConnector().OrderTicket();
/*      */   }
/*      */ 
/*      */   protected double OrderStopLoss() throws JFException {
/* 1761 */     return getConnector().OrderStopLoss();
/*      */   }
/*      */ 
/*      */   protected double OrderTakeProfit() throws JFException {
/* 1765 */     return getConnector().OrderTakeProfit();
/*      */   }
/*      */ 
/*      */   protected double OrderLots() throws JFException {
/* 1769 */     return getConnector().OrderLots();
/*      */   }
/*      */ 
/*      */   protected double OrderOpenPrice() throws JFException {
/* 1773 */     return getConnector().OrderOpenPrice();
/*      */   }
/*      */ 
/*      */   protected String OrderComment() throws JFException {
/* 1777 */     return getConnector().OrderComment();
/*      */   }
/*      */ 
/*      */   protected double OrderProfit() throws JFException {
/* 1781 */     return getConnector().OrderProfit();
/*      */   }
/*      */ 
/*      */   protected long OrderOpenTime() throws JFException {
/* 1785 */     return getConnector().OrderOpenTime();
/*      */   }
/*      */ 
/*      */   protected Instrument OrderSymbol() throws JFException {
/* 1789 */     return getConnector().OrderSymbol();
/*      */   }
/*      */ 
/*      */   protected int OrdersTotal() throws JFException {
/* 1793 */     return getConnector().OrdersTotal();
/*      */   }
/*      */ 
/*      */   protected int OrderSelect(Number index, Instrument select) throws JFException {
/* 1797 */     getConnector().OrderSelect(index.intValue(), 0, 0);
/* 1798 */     return index.intValue();
/*      */   }
/*      */ 
/*      */   protected boolean OrderSelect(Number index, Number select) throws JFException {
/* 1802 */     return getConnector().OrderSelect(index.intValue(), select.intValue(), 0);
/*      */   }
/*      */ 
/*      */   protected boolean OrderSelect(Number index, Number select, Number pool) throws JFException {
/* 1806 */     return getConnector().OrderSelect(index.intValue(), select.intValue(), pool.intValue());
/*      */   }
/*      */ 
/*      */   protected int OrderType() throws JFException
/*      */   {
/* 1811 */     return getConnector().OrderType();
/*      */   }
/*      */ 
/*      */   protected int OrderMagicNumber() throws JFException {
/* 1815 */     return getConnector().OrderMagicNumber();
/*      */   }
/*      */ 
/*      */   protected int OrderSend(Object symbol, Number cmd, Number volume, Number price, Number slippage, Number stoploss, Number takeprofit, Object comment, Color color)
/*      */     throws JFException
/*      */   {
/* 1821 */     return getConnector().OrderSend(getInstrument(symbol), cmd.intValue(), volume.doubleValue(), price.doubleValue(), slippage.intValue(), stoploss.doubleValue(), takeprofit.doubleValue(), comment != null ? comment.toString() : "", 0, 0L, color);
/*      */   }
/*      */ 
/*      */   protected int OrderSend(Object symbol, Number cmd, Number volume, Number price, Number slippage, Number stoploss, Number takeprofit, Object comment, Number magic, Number expiration, Color color)
/*      */     throws JFException
/*      */   {
/* 1831 */     return getConnector().OrderSend(getInstrument(symbol), cmd.intValue(), volume.doubleValue(), price.doubleValue(), slippage.intValue(), stoploss.doubleValue(), takeprofit.doubleValue(), comment != null ? comment.toString() : "", magic.intValue(), expiration.intValue(), color);
/*      */   }
/*      */ 
/*      */   protected int OrderSend(Object symbol, Number cmd, Number volume, Number price, Number slippage, Number stoploss, Number takeprofit, Number magic)
/*      */     throws JFException
/*      */   {
/* 1841 */     return getConnector().OrderSend(getInstrument(symbol), cmd.intValue(), volume.doubleValue(), price.doubleValue(), slippage.intValue(), stoploss.doubleValue(), takeprofit.doubleValue(), "", magic.intValue(), 0L, IColor.White);
/*      */   }
/*      */ 
/*      */   protected int OrderSend(Object symbol, Number cmd, Number volume, Number price, Number slippage, Number stoploss, Number takeprofit, Object comment, String magicnumber, Number expiration, Color color)
/*      */     throws JFException
/*      */   {
/* 1850 */     return getConnector().OrderSend(getInstrument(symbol), cmd.intValue(), volume.doubleValue(), price.doubleValue(), slippage.intValue(), stoploss.doubleValue(), takeprofit.doubleValue(), comment != null ? comment.toString() : "", Integer.valueOf(magicnumber).intValue(), 0L, color);
/*      */   }
/*      */ 
/*      */   protected int OrderSend(Object symbol, Number cmd, Number volume, Number price, Number slippage, Number stoploss, Number takeprofit, String comment, Number magic, Color clBuy)
/*      */     throws JFException
/*      */   {
/* 1858 */     return getConnector().OrderSend(getInstrument(symbol), cmd.intValue(), volume.doubleValue(), price.doubleValue(), slippage.intValue(), stoploss.doubleValue(), takeprofit.doubleValue(), comment != null ? comment.toString() : "", magic.intValue(), 0L, clBuy);
/*      */   }
/*      */ 
/*      */   protected int OrderSend(Object symbol, Number cmd, Number volume, Number price, Number slippage, Number stoploss, Number takeprofit, Number expiration, Number magic, Color clBuy)
/*      */     throws JFException
/*      */   {
/* 1866 */     return getConnector().OrderSend(getInstrument(symbol), cmd.intValue(), volume.doubleValue(), price.doubleValue(), slippage.intValue(), stoploss.doubleValue(), takeprofit.doubleValue(), "", magic.intValue(), expiration.longValue(), clBuy);
/*      */   }
/*      */ 
/*      */   protected int OrderSend(Object symbol, Number cmd, Number volume, Number price, Number slippage, Number stoploss, Number takeprofit, String comment, Number magic, Number expiration, Number clBuy)
/*      */     throws JFException
/*      */   {
/* 1875 */     return getConnector().OrderSend(getInstrument(symbol), cmd.intValue(), volume.doubleValue(), price.doubleValue(), slippage.intValue(), stoploss.doubleValue(), takeprofit.doubleValue(), comment != null ? comment.toString() : "", magic.intValue(), expiration.intValue(), IColor.White);
/*      */   }
/*      */ 
/*      */   protected int OrderSend(Object symbol, Number cmd, Number volume, Number price, Number slippage, Number stoploss, Number takeprofit, String comment, Number magic, Number expiration)
/*      */     throws JFException
/*      */   {
/* 1884 */     return getConnector().OrderSend(getInstrument(symbol), cmd.intValue(), volume.doubleValue(), price.doubleValue(), slippage.intValue(), stoploss.doubleValue(), takeprofit.doubleValue(), comment != null ? comment.toString() : "", magic.intValue(), expiration.intValue(), IColor.White);
/*      */   }
/*      */ 
/*      */   protected int OrderSend(Object symbol, Number cmd, Number volume, Number price, Number slippage, Number stoploss, Number takeprofit, String comment, Number magic)
/*      */     throws JFException
/*      */   {
/* 1893 */     return getConnector().OrderSend(getInstrument(symbol), cmd.intValue(), volume.doubleValue(), price.doubleValue(), slippage.intValue(), stoploss.doubleValue(), takeprofit.doubleValue(), comment != null ? comment.toString() : "", magic.intValue(), 0L, IColor.White);
/*      */   }
/*      */ 
/*      */   protected int OrderSend(Object symbol, Number cmd, Number volume, Number price, Number slippage, Number stoploss, Number takeprofit, Number magic, Color color)
/*      */     throws JFException
/*      */   {
/* 1900 */     return getConnector().OrderSend(getInstrument(symbol), cmd.intValue(), volume.doubleValue(), price.doubleValue(), slippage.intValue(), stoploss.doubleValue(), takeprofit.doubleValue(), "", magic.intValue(), 0L, color);
/*      */   }
/*      */ 
/*      */   protected int OrderSend(Object symbol, Number cmd, Number volume, Number price, Number slippage, Number stoploss, Number takeprofit, String comment)
/*      */     throws JFException
/*      */   {
/* 1908 */     return getConnector().OrderSend(getInstrument(symbol), cmd.intValue(), volume.doubleValue(), price.doubleValue(), slippage.intValue(), stoploss.doubleValue(), takeprofit.doubleValue(), comment != null ? comment.toString() : "", 0, 0L, IColor.White);
/*      */   }
/*      */ 
/*      */   protected int OrderSend(Object symbol, Number cmd, Number volume, Number price, Number slippage, Number stoploss, Number takeprofit)
/*      */     throws JFException
/*      */   {
/* 1917 */     return getConnector().OrderSend(getInstrument(symbol), cmd.intValue(), volume.doubleValue(), price.doubleValue(), slippage.intValue(), stoploss.doubleValue(), takeprofit.doubleValue(), "", 0, 0L, IColor.White);
/*      */   }
/*      */ 
/*      */   protected boolean OrderModify(Number ticket, Number price, Number stoploss, Number takeprofit, Number expiration, Color arrow_color)
/*      */     throws JFException
/*      */   {
/* 1926 */     return getConnector().OrderModify(ticket.intValue(), price.doubleValue(), stoploss.doubleValue(), takeprofit.doubleValue(), expiration.intValue(), arrow_color);
/*      */   }
/*      */ 
/*      */   protected boolean OrderModify(Number ticket, Number price, Number stoploss, Number takeprofit, Number expiration, Number arrow_color)
/*      */     throws JFException
/*      */   {
/* 1934 */     return getConnector().OrderModify(ticket.intValue(), price.doubleValue(), stoploss.doubleValue(), takeprofit.doubleValue(), expiration.intValue(), IColor.White);
/*      */   }
/*      */ 
/*      */   protected boolean OrderModify(Number ticket, Boolean price, Number stoploss, Number takeprofit, Color arrow_color)
/*      */     throws JFException
/*      */   {
/* 1951 */     return getConnector().OrderModify(ticket.intValue(), toDouble(price), stoploss.doubleValue(), takeprofit.doubleValue(), 0L, arrow_color);
/*      */   }
/*      */ 
/*      */   protected boolean OrderModify(Number ticket, Number price, Number stoploss, Number takeprofit, Color arrow_color)
/*      */     throws JFException
/*      */   {
/* 1958 */     return getConnector().OrderModify(ticket.intValue(), price.doubleValue(), stoploss.doubleValue(), takeprofit.doubleValue(), 0L, arrow_color);
/*      */   }
/*      */ 
/*      */   protected boolean OrderModify(Number ticket, Number price, Number stoploss, Number takeprofit, Number expiration)
/*      */     throws JFException
/*      */   {
/* 1965 */     return getConnector().OrderModify(ticket.intValue(), price.doubleValue(), stoploss.doubleValue(), takeprofit.doubleValue(), expiration.intValue(), IColor.White);
/*      */   }
/*      */ 
/*      */   protected double OrderSwap()
/*      */     throws JFException
/*      */   {
/* 1971 */     return getConnector().OrderSwap();
/*      */   }
/*      */ 
/*      */   protected long OrderExpiration() throws JFException {
/* 1975 */     return getConnector().OrderExpiration();
/*      */   }
/*      */ 
/*      */   protected int GetLastError() throws JFException {
/* 1979 */     return getConnector().GetLastError();
/*      */   }
/*      */ 
/*      */   protected String ErrorDescription(int errCode) throws JFException {
/* 1983 */     return getConnector().ErrorDescription(errCode);
/*      */   }
/*      */ 
/*      */   protected void OrderPrint() throws JFException {
/* 1987 */     getConnector().OrderPrint();
/*      */   }
/*      */ 
/*      */   protected boolean OrderCloseBy(Number ticket, Number opposite, Color color) throws JFException
/*      */   {
/* 1992 */     return getConnector().OrderCloseBy(ticket.intValue(), opposite.intValue(), color);
/*      */   }
/*      */ 
/*      */   protected boolean OrderCloseBy(Number ticket, Number opposite)
/*      */     throws JFException
/*      */   {
/* 1998 */     return getConnector().OrderCloseBy(ticket.intValue(), opposite.intValue(), IColor.White);
/*      */   }
/*      */ 
/*      */   protected boolean IsTradeAllowed()
/*      */     throws JFException
/*      */   {
/* 2007 */     return getConnector().IsTradeAllowed();
/*      */   }
/*      */ 
/*      */   protected boolean IsTradeContextBusy() throws JFException {
/* 2011 */     return getConnector().IsTradeContextBusy();
/*      */   }
/*      */ 
/*      */   protected boolean IsStopped() throws JFException {
/* 2015 */     return getConnector().IsStopped();
/*      */   }
/*      */ 
/*      */   protected boolean IsConnected() throws JFException {
/* 2019 */     return getConnector().IsConnected();
/*      */   }
/*      */ 
/*      */   protected boolean IsOptimization() throws JFException {
/* 2023 */     return getConnector().IsOptimization();
/*      */   }
/*      */ 
/*      */   protected boolean IsTesting() throws JFException {
/* 2027 */     return getConnector().IsTesting();
/*      */   }
/*      */ 
/*      */   protected boolean IsDemo() throws JFException {
/* 2031 */     return getConnector().IsDemo();
/*      */   }
/*      */ 
/*      */   protected boolean IsExpertEnabled() throws JFException {
/* 2035 */     return getConnector().IsExpertEnabled();
/*      */   }
/*      */ 
/*      */   protected boolean IsDllsAllowed() throws JFException {
/* 2039 */     return getConnector().IsDllsAllowed();
/*      */   }
/*      */ 
/*      */   protected boolean IsLibrariesAllowed() throws JFException {
/* 2043 */     return getConnector().IsLibrariesAllowed();
/*      */   }
/*      */ 
/*      */   protected boolean IsVisualMode() throws JFException {
/* 2047 */     return getConnector().IsVisualMode();
/*      */   }
/*      */ 
/*      */   protected int UninitializeReason() throws JFException {
/* 2051 */     return getConnector().UninitializeReason();
/*      */   }
/*      */ 
/*      */   protected int Period()
/*      */     throws JFException
/*      */   {
/* 2058 */     if (this.isRunning) {
/* 2059 */       return getConnector().Period();
/*      */     }
/* 2061 */     return (int)this.currentPeriod.getInterval() / 60000;
/*      */   }
/*      */ 
/*      */   protected boolean RefreshRates() throws JFException
/*      */   {
/* 2066 */     return getConnector().RefreshRates();
/*      */   }
/*      */ 
/*      */   protected String Symbol() throws JFException {
/* 2070 */     return getConnector().Symbol();
/*      */   }
/*      */ 
/*      */   protected Instrument Instrument() throws JFException {
/* 2074 */     return getCurrentInstrument();
/*      */   }
/*      */ 
/*      */   protected String WindowExpertName() throws JFException {
/* 2078 */     return getConnector().WindowExpertName();
/*      */   }
/*      */ 
/*      */   protected void HideTestIndicators(boolean hide) throws JFException {
/* 2082 */     getConnector().HideTestIndicators(hide);
/*      */   }
/*      */ 
/*      */   protected Color RGB(Number r, Number g, Number b) throws JFException {
/* 2086 */     return getConnector().RGB(r.intValue(), g.intValue(), b.intValue());
/*      */   }
/*      */ 
/*      */   protected double WindowPriceMax() throws JFException {
/* 2090 */     return getConnector().WindowPriceMax(0);
/*      */   }
/*      */ 
/*      */   protected double WindowPriceMax(Number index) throws JFException {
/* 2094 */     return getConnector().WindowPriceMax(index.intValue());
/*      */   }
/*      */ 
/*      */   protected double WindowPriceMin() throws JFException {
/* 2098 */     return getConnector().WindowPriceMin(0);
/*      */   }
/*      */ 
/*      */   protected double WindowPriceMin(Number index) throws JFException {
/* 2102 */     return getConnector().WindowPriceMin(index.intValue());
/*      */   }
/*      */ 
/*      */   protected int WindowBarsPerChart() throws JFException {
/* 2106 */     return getConnector().WindowBarsPerChart();
/*      */   }
/*      */ 
/*      */   protected int BarsPerWindow()
/*      */     throws JFException
/*      */   {
/* 2117 */     return getConnector().WindowBarsPerChart();
/*      */   }
/*      */ 
/*      */   protected int WindowFind(String name) throws JFException {
/* 2121 */     return getConnector().WindowFind(name);
/*      */   }
/*      */ 
/*      */   protected int WindowFirstVisibleBar() throws JFException {
/* 2125 */     return getConnector().WindowFirstVisibleBar();
/*      */   }
/*      */ 
/*      */   protected int WindowHandle(Object symbol, Number timeframe) throws JFException
/*      */   {
/* 2130 */     return getConnector().WindowHandle(getInstrument(symbol), getPeriod(timeframe));
/*      */   }
/*      */ 
/*      */   protected boolean WindowIsVisible(Number index) throws JFException
/*      */   {
/* 2135 */     return getConnector().WindowIsVisible(index.intValue());
/*      */   }
/*      */ 
/*      */   protected int WindowOnDropped() throws JFException {
/* 2139 */     return getConnector().WindowOnDropped();
/*      */   }
/*      */ 
/*      */   protected double WindowPriceOnDropped() throws JFException {
/* 2143 */     return getConnector().WindowPriceOnDropped();
/*      */   }
/*      */ 
/*      */   protected boolean WindowScreenShot(String filename, Number size_x, Number size_y, Number start_bar, Number chart_scale, Number chart_mode)
/*      */     throws JFException
/*      */   {
/* 2149 */     return getConnector().WindowScreenShot(filename, size_x.intValue(), size_y.intValue(), start_bar.intValue(), chart_scale.intValue(), chart_mode.intValue());
/*      */   }
/*      */ 
/*      */   protected void WindowRedraw()
/*      */     throws JFException
/*      */   {
/* 2155 */     getConnector().WindowRedraw();
/*      */   }
/*      */ 
/*      */   protected long WindowTimeOnDropped() throws JFException {
/* 2159 */     return getConnector().WindowTimeOnDropped();
/*      */   }
/*      */ 
/*      */   protected int WindowsTotal() throws JFException {
/* 2163 */     return getConnector().WindowsTotal();
/*      */   }
/*      */ 
/*      */   protected int WindowXOnDropped() throws JFException {
/* 2167 */     return getConnector().WindowXOnDropped();
/*      */   }
/*      */ 
/*      */   protected int WindowYOnDropped() throws JFException {
/* 2171 */     return getConnector().WindowYOnDropped();
/*      */   }
/*      */ 
/*      */   protected int FileOpenHistory(String filename, int mode, char delimiter)
/*      */     throws JFException
/*      */   {
/* 2179 */     return getConnector().FileOpenHistory(filename, mode, delimiter);
/*      */   }
/*      */ 
/*      */   protected int FileOpenHistory(String filename, int mode) throws JFException {
/* 2183 */     return getConnector().FileOpenHistory(filename, mode, ';');
/*      */   }
/*      */ 
/*      */   protected int FileWriteString(int handle, String value) throws JFException {
/* 2187 */     return getConnector().FileWriteString(handle, value, -1);
/*      */   }
/*      */ 
/*      */   protected int FileWriteString(int handle, String value, int length) throws JFException
/*      */   {
/* 2192 */     return getConnector().FileWriteString(handle, value, length);
/*      */   }
/*      */ 
/*      */   protected int FileWriteArray(int handle, Object array, int start, int count) throws JFException
/*      */   {
/* 2197 */     return getConnector().FileWriteArray(handle, array, start, count);
/*      */   }
/*      */ 
/*      */   protected void FileFlush(int handle) throws JFException {
/* 2201 */     getConnector().FileFlush(handle);
/*      */   }
/*      */ 
/*      */   protected void FileDelete(String filename) throws JFException {
/* 2205 */     getConnector().FileDelete(filename);
/*      */   }
/*      */ 
/*      */   protected boolean FileIsEnding(int handle) throws JFException {
/* 2209 */     return getConnector().FileIsEnding(handle);
/*      */   }
/*      */ 
/*      */   protected boolean FileIsLineEnding(int handle) throws JFException {
/* 2213 */     return getConnector().FileIsLineEnding(handle);
/*      */   }
/*      */ 
/*      */   protected int FileReadArray(int handle, Object array, int start, int count) throws JFException
/*      */   {
/* 2218 */     return getConnector().FileReadArray(handle, array, start, count);
/*      */   }
/*      */ 
/*      */   protected double FileReadDouble(int handle) throws JFException {
/* 2222 */     return getConnector().FileReadDouble(handle, 8);
/*      */   }
/*      */ 
/*      */   protected double FileReadDouble(int handle, int size) throws JFException {
/* 2226 */     return getConnector().FileReadDouble(handle, size);
/*      */   }
/*      */ 
/*      */   protected int FileReadInteger(int handle) throws JFException {
/* 2230 */     return getConnector().FileReadInteger(handle, 4);
/*      */   }
/*      */ 
/*      */   protected int FileReadInteger(int handle, int size) throws JFException {
/* 2234 */     return getConnector().FileReadInteger(handle, size);
/*      */   }
/*      */ 
/*      */   protected double FileReadNumber(int handle) throws JFException {
/* 2238 */     return getConnector().FileReadNumber(handle);
/*      */   }
/*      */ 
/*      */   protected int FileTell(int handle) throws JFException {
/* 2242 */     return getConnector().FileTell(handle);
/*      */   }
/*      */ 
/*      */   protected int FileWriteDouble(int handle, double value) throws JFException {
/* 2246 */     return getConnector().FileWriteDouble(handle, value, 8);
/*      */   }
/*      */ 
/*      */   protected int FileWriteDouble(int handle, double value, int size)
/*      */     throws JFException
/*      */   {
/* 2252 */     return getConnector().FileWriteDouble(handle, value, size);
/*      */   }
/*      */ 
/*      */   protected int FileWriteInteger(int handle, int value) throws JFException {
/* 2256 */     return getConnector().FileWriteInteger(handle, value, 4);
/*      */   }
/*      */ 
/*      */   protected int FileWriteInteger(Number handle, Number value, Number size)
/*      */     throws JFException
/*      */   {
/* 2262 */     return getConnector().FileWriteInteger(handle.intValue(), value.intValue(), size.intValue());
/*      */   }
/*      */ 
/*      */   protected int FileOpen(String filename, Number mode, Number i) throws JFException
/*      */   {
/* 2267 */     return getConnector().FileOpen(filename, mode.intValue(), String.valueOf(i.intValue()));
/*      */   }
/*      */ 
/*      */   protected int FileOpen(String filename, int mode, String delimiter) throws JFException
/*      */   {
/* 2272 */     return getConnector().FileOpen(filename, mode, delimiter);
/*      */   }
/*      */ 
/*      */   protected int FileOpen(String filename, int mode) throws JFException {
/* 2276 */     return getConnector().FileOpen(filename, mode);
/*      */   }
/*      */ 
/*      */   protected void FileClose(int handle) throws JFException {
/* 2280 */     getConnector().FileClose(handle);
/*      */   }
/*      */ 
/*      */   protected int FileSize(int handle) throws JFException {
/* 2284 */     return getConnector().FileSize(handle);
/*      */   }
/*      */ 
/*      */   protected String FileReadString(int handle) throws JFException {
/* 2288 */     return getConnector().FileReadString(handle);
/*      */   }
/*      */ 
/*      */   protected String FileReadString(int handle, int length) throws JFException {
/* 2292 */     return getConnector().FileReadString(handle, length);
/*      */   }
/*      */ 
/*      */   protected boolean FileSeek(int handle, int offset, int origin) throws JFException
/*      */   {
/* 2297 */     return getConnector().FileSeek(handle, offset, origin);
/*      */   }
/*      */ 
/*      */   protected int FileWrite(int handle, Object[] objs) throws JFException {
/* 2301 */     return getConnector().FileWrite(handle, objs);
/*      */   }
/*      */ 
/*      */   protected double iRSI(Object symbol, Number timeframe, Number period, Number applied_price, Number shift)
/*      */     throws JFException
/*      */   {
/* 2309 */     return getConnector().iRSI(getInstrument(symbol), getPeriod(timeframe), period.intValue(), applied_price.intValue(), shift.intValue());
/*      */   }
/*      */ 
/*      */   protected double iMA(Object symbol, Number timeframe, Number timePeriod, Number ma_shift, Number ma_method, Number applied_price, Number shift)
/*      */     throws JFException
/*      */   {
/* 2316 */     return getConnector().iMA(getInstrument(symbol), getPeriod(timeframe), timePeriod.intValue(), ma_shift.intValue(), ma_method.intValue(), applied_price.intValue(), shift.intValue());
/*      */   }
/*      */ 
/*      */   protected double iMAOnArray(double[] array, Number total, Number period, Number ma_shift, Number ma_method, Number shift)
/*      */     throws JFException
/*      */   {
/* 2322 */     return getConnector().iMAOnArray(array, total.intValue(), period.intValue(), ma_shift.intValue(), ma_method.intValue(), shift.intValue());
/*      */   }
/*      */ 
/*      */   protected double iMomentum(Object symbol, Number timeframe, Number period, Number applied_price, Number shift)
/*      */     throws JFException
/*      */   {
/* 2329 */     return getConnector().iMomentum(getInstrument(symbol), getPeriod(timeframe), period.intValue(), applied_price.intValue(), shift.intValue());
/*      */   }
/*      */ 
/*      */   protected double iSAR(Object symbol, Number timeframe, Number step, Number maximum, Number shift)
/*      */     throws JFException
/*      */   {
/* 2336 */     return getConnector().iSAR(getInstrument(symbol), getPeriod(timeframe), step.doubleValue(), maximum.doubleValue(), shift.intValue());
/*      */   }
/*      */ 
/*      */   protected double iMACD(Object symbol, Number timeframe, Number fast_ema_period, Number slow_ema_period, Number signal_period, Number applied_price, Number mode, Number shift)
/*      */     throws JFException
/*      */   {
/* 2344 */     return getConnector().iMACD(getInstrument(symbol), getPeriod(timeframe), fast_ema_period.intValue(), slow_ema_period.intValue(), signal_period.intValue(), applied_price.intValue(), mode.intValue(), shift.intValue());
/*      */   }
/*      */ 
/*      */   protected double iStdDev(Object symbol, Number timeframe, Number ma_period, Number ma_shift, Number ma_method, Number applied_price, Number shift)
/*      */     throws JFException
/*      */   {
/* 2353 */     return getConnector().iStdDev(getInstrument(symbol), getPeriod(timeframe), ma_period.intValue(), ma_shift.intValue(), ma_method.intValue(), applied_price.intValue(), shift.intValue());
/*      */   }
/*      */ 
/*      */   protected double iStdDevOnArray(double[] array, Number total, Number ma_period, Number ma_shift, Number ma_method, Number shift)
/*      */     throws JFException
/*      */   {
/* 2362 */     return getConnector().iMAOnArray(array, total.intValue(), ma_period.intValue(), ma_shift.intValue(), ma_method.intValue(), shift.intValue());
/*      */   }
/*      */ 
/*      */   protected double iATR(Object symbol, Number timeframe, Number period, Number shift)
/*      */     throws JFException
/*      */   {
/* 2369 */     return getConnector().iATR(getInstrument(symbol), getPeriod(timeframe), period.intValue(), shift.intValue());
/*      */   }
/*      */ 
/*      */   protected double iADX(Object symbol, Number timeframe, Number period, Number applied_price, Number mode, Number shift)
/*      */     throws JFException
/*      */   {
/* 2375 */     return getConnector().iADX(getInstrument(symbol), getPeriod(timeframe), period.intValue(), applied_price.intValue(), mode.intValue(), shift.intValue());
/*      */   }
/*      */ 
/*      */   protected double iDeMarker(Object symbol, Number timeframe, Number timePeriod, Number shift)
/*      */     throws JFException
/*      */   {
/* 2382 */     return getConnector().iDeMarker(getInstrument(symbol), getPeriod(timeframe), timePeriod.intValue(), shift.intValue());
/*      */   }
/*      */ 
/*      */   protected double iBands(Object symbol, Number timeframe, Number period, Number deviation, Number bands_shift, Number applied_price, Number mode, Number shift)
/*      */     throws JFException
/*      */   {
/* 2389 */     return getConnector().iBands(getInstrument(symbol), getPeriod(timeframe), period.intValue(), deviation.intValue(), bands_shift.intValue(), applied_price.intValue(), mode.intValue(), shift.intValue());
/*      */   }
/*      */ 
/*      */   protected double iStochastic(Object symbol, Number timeframe, Number kPeriod, Number dPeriod, Number slowing, Number method, Number price_field, Number mode, Number shift)
/*      */     throws JFException
/*      */   {
/* 2398 */     return getConnector().iStochastic(getInstrument(symbol), getPeriod(timeframe), kPeriod.intValue(), dPeriod.intValue(), slowing.intValue(), method.intValue(), price_field.intValue(), mode.intValue(), shift.intValue());
/*      */   }
/*      */ 
/*      */   protected double iAC(Object symbol, Number timeframe, Number shift)
/*      */     throws JFException
/*      */   {
/* 2406 */     return getConnector().iAC(getInstrument(symbol), getPeriod(timeframe), shift.intValue());
/*      */   }
/*      */ 
/*      */   protected double iAD(Object symbol, Number timeframe, Number shift)
/*      */     throws JFException
/*      */   {
/* 2412 */     return getConnector().iAD(getInstrument(symbol), getPeriod(timeframe), shift.intValue());
/*      */   }
/*      */ 
/*      */   protected double iAlligator(Object symbol, Number timeframe, Number jaw_period, Number jaw_shift, Number teeth_period, Number teeth_shift, Number lips_period, Number lips_shift, Number ma_method, Number applied_price, Number mode, Number shift)
/*      */     throws JFException
/*      */   {
/* 2421 */     return getConnector().iAlligator(getInstrument(symbol), getPeriod(timeframe), jaw_period.intValue(), jaw_shift.intValue(), teeth_period.intValue(), teeth_shift.intValue(), lips_period.intValue(), lips_shift.intValue(), ma_method.intValue(), applied_price.intValue(), mode.intValue(), shift.intValue());
/*      */   }
/*      */ 
/*      */   protected double iAO(Object symbol, Number timeframe, Number shift)
/*      */     throws JFException
/*      */   {
/* 2431 */     return getConnector().iAO(getInstrument(symbol), getPeriod(timeframe), shift.intValue());
/*      */   }
/*      */ 
/*      */   protected double iBearsPower(Object symbol, Number timeframe, Number period, Number applied_price, Number shift)
/*      */     throws JFException
/*      */   {
/* 2438 */     return getConnector().iBearsPower(getInstrument(symbol), getPeriod(timeframe), period.intValue(), applied_price.intValue(), shift.intValue());
/*      */   }
/*      */ 
/*      */   protected double iBandsOnArray(double[] array, Number total, Number period, Number deviation, Number bands_shift, Number mode, Number shift)
/*      */     throws JFException
/*      */   {
/* 2446 */     return getConnector().iBandsOnArray(array, total.intValue(), period.intValue(), deviation.intValue(), bands_shift.intValue(), mode.intValue(), shift.intValue());
/*      */   }
/*      */ 
/*      */   protected double iBullsPower(Object symbol, Number timeframe, Number period, Number applied_price, Number shift)
/*      */     throws JFException
/*      */   {
/* 2454 */     return getConnector().iBullsPower(getInstrument(symbol), getPeriod(timeframe), period.intValue(), applied_price.intValue(), shift.intValue());
/*      */   }
/*      */ 
/*      */   protected double iCCI(Object symbol, Number timeframe, Number period, Number applied_price, Number shift)
/*      */     throws JFException
/*      */   {
/* 2461 */     return getConnector().iCCI(getInstrument(symbol), getPeriod(timeframe), period.intValue(), applied_price.intValue(), shift.intValue());
/*      */   }
/*      */ 
/*      */   protected double iCCIOnArray(double[] array, Number total, Number period, Number shift)
/*      */     throws JFException
/*      */   {
/* 2467 */     return getConnector().iCCIOnArray(array, total.intValue(), period.intValue(), shift.intValue());
/*      */   }
/*      */ 
/*      */   protected double iCustom(Object symbol, Number timeframe, String name) throws JFException
/*      */   {
/* 2472 */     return getConnector().iCustom(getInstrument(symbol), getPeriod(timeframe), name, 0, 0, new Object[] { new double[0] });
/*      */   }
/*      */ 
/*      */   protected double iCustom(Object symbol, Number timeframe, String name, Object[] custom, Number mode, Number shift)
/*      */     throws JFException
/*      */   {
/* 2478 */     return getConnector().iCustom(getInstrument(symbol), getPeriod(timeframe), name, mode.intValue(), shift.intValue(), custom);
/*      */   }
/*      */ 
/*      */   protected double iCustom(Object symbol, Number timeframe, String name, Number mode, Number shift, Object[] custom)
/*      */     throws JFException
/*      */   {
/* 2485 */     return getConnector().iCustom(getInstrument(symbol), getPeriod(timeframe), name, mode.intValue(), shift.intValue(), custom);
/*      */   }
/*      */ 
/*      */   protected double iEnvelopes(Object symbol, Number timeframe, Number ma_period, Number ma_method, Number ma_shift, Number applied_price, Number deviation, Number mode, Number shift)
/*      */     throws JFException
/*      */   {
/* 2494 */     return getConnector().iEnvelopes(getInstrument(symbol), getPeriod(timeframe), ma_period.intValue(), ma_method.intValue(), ma_shift.intValue(), applied_price.intValue(), deviation.doubleValue(), mode.intValue(), shift.intValue());
/*      */   }
/*      */ 
/*      */   protected double iEnvelopesOnArray(double[] array, Number total, Number ma_period, Number ma_method, Number ma_shift, Number deviation, Number mode, Number shift)
/*      */     throws JFException
/*      */   {
/* 2504 */     return getConnector().iEnvelopesOnArray(array, total.intValue(), ma_period.intValue(), ma_method.intValue(), ma_shift.intValue(), deviation.doubleValue(), mode.intValue(), shift.intValue());
/*      */   }
/*      */ 
/*      */   protected double iForce(Object symbol, Number timeframe, Number period, Number ma_method, Number applied_price, Number shift)
/*      */     throws JFException
/*      */   {
/* 2513 */     return getConnector().iForce(getInstrument(symbol), getPeriod(timeframe), period.intValue(), ma_method.intValue(), applied_price.intValue(), shift.intValue());
/*      */   }
/*      */ 
/*      */   protected double iFractals(Object symbol, Number timeframe, Number mode, Number shift)
/*      */     throws JFException
/*      */   {
/* 2520 */     return getConnector().iFractals(getInstrument(symbol), getPeriod(timeframe), mode.intValue(), shift.intValue());
/*      */   }
/*      */ 
/*      */   protected double iGator(Object symbol, Number timeframe, Number jaw_period, Number jaw_shift, Number teeth_period, Number teeth_shift, Number lips_period, Number lips_shift, Number ma_method, Number applied_price, Number mode, Number shift)
/*      */     throws JFException
/*      */   {
/* 2528 */     return getConnector().iGator(getInstrument(symbol), getPeriod(timeframe), jaw_period.intValue(), jaw_shift.intValue(), teeth_period.intValue(), teeth_shift.intValue(), lips_period.intValue(), lips_shift.intValue(), ma_method.intValue(), applied_price.intValue(), mode.intValue(), shift.intValue());
/*      */   }
/*      */ 
/*      */   protected double iIchimoku(Object symbol, Number timeframe, Number tenkan_sen, Number kijun_sen, Number senkou_span_b, Number mode, Number shift)
/*      */     throws JFException
/*      */   {
/* 2539 */     return getConnector().iIchimoku(getInstrument(symbol), getPeriod(timeframe), tenkan_sen.intValue(), kijun_sen.intValue(), senkou_span_b.intValue(), mode.intValue(), shift.intValue());
/*      */   }
/*      */ 
/*      */   protected double iBWMFI(Object symbol, Number timeframe, Number shift)
/*      */     throws JFException
/*      */   {
/* 2547 */     return getConnector().iBWMFI(getInstrument(symbol), getPeriod(timeframe), shift.intValue());
/*      */   }
/*      */ 
/*      */   protected double iMomentumOnArray(double[] array, Number total, Number period, Number shift)
/*      */     throws JFException
/*      */   {
/* 2553 */     return getConnector().iMomentumOnArray(array, total.intValue(), period.intValue(), shift.intValue());
/*      */   }
/*      */ 
/*      */   protected double iMFI(Object symbol, Number timeframe, Number period, Number shift)
/*      */     throws JFException
/*      */   {
/* 2559 */     return getConnector().iMFI(getInstrument(symbol), getPeriod(timeframe), period.intValue(), shift.intValue());
/*      */   }
/*      */ 
/*      */   protected double iOsMA(Object symbol, Number timeframe, Number fast_ema_period, Number slow_ema_period, Number signal_period, Number applied_price, Number shift)
/*      */     throws JFException
/*      */   {
/* 2567 */     return getConnector().iOsMA(getInstrument(symbol), getPeriod(timeframe), fast_ema_period.intValue(), slow_ema_period.intValue(), signal_period.intValue(), applied_price.intValue(), shift.intValue());
/*      */   }
/*      */ 
/*      */   protected double iOBV(Object symbol, Number timeframe, Number applied_price, Number shift)
/*      */     throws JFException
/*      */   {
/* 2575 */     return getConnector().iOBV(getInstrument(symbol), getPeriod(timeframe), applied_price.intValue(), shift.intValue());
/*      */   }
/*      */ 
/*      */   protected double iRSIOnArray(double[] array, Number total, Number period, Number shift)
/*      */     throws JFException
/*      */   {
/* 2581 */     return getConnector().iRSIOnArray(array, total.intValue(), period.intValue(), shift.intValue());
/*      */   }
/*      */ 
/*      */   protected double iRVI(Object symbol, Number timeframe, Number period, Number mode, Number shift)
/*      */     throws JFException
/*      */   {
/* 2587 */     return getConnector().iRVI(getInstrument(symbol), getPeriod(timeframe), period.intValue(), mode.intValue(), shift.intValue());
/*      */   }
/*      */ 
/*      */   protected double iWPR(Object symbol, Number timeframe, Number period, Number shift)
/*      */     throws JFException
/*      */   {
/* 2593 */     return getConnector().iWPR(getInstrument(symbol), getPeriod(timeframe), period.intValue(), shift.intValue());
/*      */   }
/*      */ 
/*      */   public IBox getBox()
/*      */   {
/* 2605 */     return this.box;
/*      */   }
/*      */ 
/*      */   public void setBox(IBox box) {
/* 2609 */     this.box = box;
/*      */   }
/*      */ 
/*      */   public double getAsk() {
/* 2613 */     return this.Ask;
/*      */   }
/*      */ 
/*      */   public void setAsk(double ask) {
/* 2617 */     this.Ask = ask;
/*      */   }
/*      */ 
/*      */   public double getBid() {
/* 2621 */     return this.Bid;
/*      */   }
/*      */ 
/*      */   public void setBid(double bid) {
/* 2625 */     this.Bid = bid;
/*      */   }
/*      */ 
/*      */   public IHistory getHistory() {
/* 2629 */     return getConnector().getIHistory();
/*      */   }
/*      */ 
/*      */   public void setHistory(IHistory history) {
/* 2633 */     getConnector().setIHistory(history);
/*      */   }
/*      */ 
/*      */   public boolean isInitialized() {
/* 2637 */     return this.isInitialized;
/*      */   }
/*      */ 
/*      */   public void setInitialized(boolean isInitialized) {
/* 2641 */     this.isInitialized = isInitialized;
/*      */   }
/*      */ 
/*      */   public IConnector getConnectorInstance() {
/* 2645 */     IConnectorManager manager = ConnectorManager.getInstance();
/* 2646 */     return (IConnector)manager.get(IConnectorManager.ConnectorKeys.CONNECTOR_INSTANCE);
/*      */   }
/*      */ 
/*      */   public IAccount getIAccount()
/*      */   {
/* 2651 */     return this.account;
/*      */   }
/*      */ 
/*      */   public void setIAccount(IAccount account) {
/* 2655 */     this.account = account;
/*      */   }
/*      */ 
/*      */   public IConnectorManager getConnectorManager() {
/* 2659 */     return getConnector().getConnectorManager();
/*      */   }
/*      */ 
/*      */   public synchronized boolean isRunning() {
/* 2663 */     synchronized (this) {
/* 2664 */       return this.isRunning;
/*      */     }
/*      */   }
/*      */ 
/*      */   public synchronized void setRunning(boolean isRunning) {
/* 2669 */     synchronized (this) {
/* 2670 */       this.isRunning = isRunning;
/*      */     }
/*      */   }
/*      */ 
/*      */   public synchronized Period getCurrentPeriod() {
/* 2675 */     synchronized (this.currentPeriod) {
/* 2676 */       return this.currentPeriod;
/*      */     }
/*      */   }
/*      */ 
/*      */   public synchronized void setCurrentPeriod(Period currentPeriod)
/*      */   {
/* 2682 */     synchronized (this.currentPeriod) {
/* 2683 */       this.currentPeriod = currentPeriod;
/*      */     }
/*      */   }
/*      */ 
/*      */   public IChart getCurrentChart() {
/* 2688 */     synchronized (this) {
/* 2689 */       return this.currentChart;
/*      */     }
/*      */   }
/*      */ 
/*      */   public void setCurrentChart(IChart currentChart) {
/* 2694 */     synchronized (this) {
/* 2695 */       this.currentChart = currentChart;
/*      */     }
/*      */   }
/*      */ 
/*      */   public long getLastTickTime() {
/* 2700 */     synchronized (this) {
/* 2701 */       return this.lastTickTime;
/*      */     }
/*      */   }
/*      */ 
/*      */   public void setLastTickTime(long lastTickTime) {
/* 2706 */     synchronized (this) {
/* 2707 */       this.lastTickTime = lastTickTime;
/*      */     }
/*      */   }
/*      */ 
/*      */   protected double NormalizeDouble(Number value, Number precision)
/*      */     throws JFException
/*      */   {
/* 2761 */     return MathHelpers.normalizeDouble(value.doubleValue(), precision.intValue());
/*      */   }
/*      */ 
/*      */   protected double StrToDouble(String str)
/*      */     throws JFException
/*      */   {
/* 2779 */     return MathHelpers.strToDouble(str);
/*      */   }
/*      */ 
/*      */   protected String DoubleToStr(Number value, Number digits)
/*      */     throws JFException
/*      */   {
/* 2785 */     return CommonHelpers.dfPrint.format(NormalizeDouble(value, digits));
/*      */   }
/*      */   protected String DoubleToStr(Boolean value, Number digits) throws JFException {
/* 2788 */     return value.booleanValue() != true ? "false" : "true";
/*      */   }
/*      */ 
/*      */   protected String CharToStr(Number char_code)
/*      */     throws JFException
/*      */   {
/* 2799 */     Character ch = new Character((char)char_code.intValue());
/* 2800 */     return ch.toString();
/*      */   }
/*      */ 
/*      */   protected long StrToTime(String value)
/*      */     throws JFException
/*      */   {
/* 2808 */     long rc = 0L;
/* 2809 */     Date date = null;
/*      */     try {
/* 2811 */       date = CommonHelpers.dateFormat1.parse(value);
/*      */     }
/*      */     catch (ParseException e) {
/*      */     }
/* 2815 */     if (date != null) {
/* 2816 */       rc = date.getTime() / 1000L;
/*      */     }
/* 2818 */     return rc;
/*      */   }
/*      */ 
/*      */   protected String TimeToStr(Number value)
/*      */     throws JFException
/*      */   {
/* 2835 */     return TimeToStr(Long.valueOf(value.longValue()), Integer.valueOf(3));
/*      */   }
/*      */ 
/*      */   protected String TimeToStr(Number value, Number mode) throws JFException {
/* 2839 */     Date date = new Date(value.longValue() * 1000L);
/* 2840 */     String rc = null;
/*      */ 
/* 2842 */     switch (mode.intValue()) {
/*      */     case 1:
/* 2844 */       rc = CommonHelpers.dateFormatTIME_DATE.format(date);
/* 2845 */       break;
/*      */     case 2:
/* 2847 */       rc = CommonHelpers.dateFormatTIME_MINUTES.format(date);
/* 2848 */       break;
/*      */     case 4:
/* 2850 */       rc = CommonHelpers.dateFormatTIME_SECONDS.format(date);
/* 2851 */       break;
/*      */     case 3:
/* 2853 */       rc = CommonHelpers.dateFormat1.format(date);
/* 2854 */       break;
/*      */     default:
/* 2856 */       rc = CommonHelpers.dateFormat1.format(date);
/*      */     }
/*      */ 
/* 2859 */     return rc;
/*      */   }
/*      */ 
/*      */   protected int StrToInteger(String value)
/*      */     throws JFException
/*      */   {
/* 2871 */     if ((value == null) || (value.isEmpty())) {
/* 2872 */       value = "0";
/*      */     }
/* 2874 */     value = value.replaceAll(",", ".");
/* 2875 */     return Integer.parseInt(value);
/*      */   }
/*      */ 
/*      */   protected long convertDatetimeToLong(String macro)
/*      */     throws JFException
/*      */   {
/* 2882 */     return DeclarationHelpers.getCalendarInitionalizerExpression(macro).getTimeInMillis();
/*      */   }
/*      */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Connector-1.1.49.jar
 * Qualified Name:     com.dukascopy.api.connector.AbstractConnectorImpl
 * JD-Core Version:    0.6.0
 */