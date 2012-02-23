/*      */ package com.dukascopy.dds2.greed.connector.helpers;
/*      */ 
/*      */ import com.dukascopy.api.IBar;
/*      */ import com.dukascopy.api.IChart.Type;
/*      */ import com.dukascopy.api.IChartObject.ATTR_BOOLEAN;
/*      */ import com.dukascopy.api.IChartObject.ATTR_COLOR;
/*      */ import com.dukascopy.api.IChartObject.ATTR_DOUBLE;
/*      */ import com.dukascopy.api.IChartObject.ATTR_INT;
/*      */ import com.dukascopy.api.IChartObject.ATTR_LONG;
/*      */ import com.dukascopy.api.IEngine.OrderCommand;
/*      */ import com.dukascopy.api.IIndicators.AppliedPrice;
/*      */ import com.dukascopy.api.IIndicators.MaType;
/*      */ import com.dukascopy.api.Instrument;
/*      */ import com.dukascopy.api.JFException;
/*      */ import com.dukascopy.api.Period;
/*      */ import com.dukascopy.api.connector.IConst;
/*      */ import com.dukascopy.api.indicators.InputParameterInfo;
/*      */ import com.dukascopy.api.indicators.InputParameterInfo.Type;
/*      */ import java.awt.Color;
/*      */ import java.text.DecimalFormat;
/*      */ import java.text.DecimalFormatSymbols;
/*      */ import java.text.SimpleDateFormat;
/*      */ import java.util.Arrays;
/*      */ import java.util.BitSet;
/*      */ import java.util.Locale;
/*      */ 
/*      */ public class CommonHelpers
/*      */   implements IConst
/*      */ {
/*      */   public static final DecimalFormat storeFormat;
/*      */   public static final SimpleDateFormat dateFormat1;
/*      */   public static final SimpleDateFormat dateFormatTIME_SECONDS;
/*      */   public static final SimpleDateFormat dateFormatTIME_MINUTES;
/*      */   public static final SimpleDateFormat dateFormatTIME_DATE;
/*      */   public static final DecimalFormat dfPrint;
/*      */ 
/*      */   public static final Color colorFromHex(String hex)
/*      */   {
/*   26 */     int intValue = Integer.parseInt(hex, 16);
/*   27 */     Color color = new Color(intValue);
/*   28 */     return color;
/*      */   }
/*      */ 
/*      */   public static final IChartObject.ATTR_BOOLEAN getObjectPropBoolean(int index)
/*      */   {
/*   33 */     switch (index) {
/*      */     case 9:
/*   35 */       return IChartObject.ATTR_BOOLEAN.BACK;
/*      */     case 11:
/*   37 */       return IChartObject.ATTR_BOOLEAN.ELLIPSE;
/*      */     case 10:
/*   39 */       return IChartObject.ATTR_BOOLEAN.RAY;
/*      */     }
/*   41 */     return null;
/*      */   }
/*      */ 
/*      */   public static final IChartObject.ATTR_COLOR getObjectPropColor(int index)
/*      */   {
/*   46 */     switch (index) {
/*      */     case 6:
/*   48 */       return IChartObject.ATTR_COLOR.COLOR;
/*      */     }
/*   50 */     return null;
/*      */   }
/*      */ 
/*      */   public static final IChartObject.ATTR_DOUBLE getObjectPropDouble(int index)
/*      */   {
/*   55 */     switch (index) {
/*      */     case 13:
/*   57 */       return IChartObject.ATTR_DOUBLE.ANGLE;
/*      */     case 16:
/*   59 */       return IChartObject.ATTR_DOUBLE.DEVIATION;
/*      */     case 1:
/*   61 */       return IChartObject.ATTR_DOUBLE.PRICE1;
/*      */     case 3:
/*   63 */       return IChartObject.ATTR_DOUBLE.PRICE2;
/*      */     case 5:
/*   65 */       return IChartObject.ATTR_DOUBLE.PRICE3;
/*      */     case 12:
/*   67 */       return IChartObject.ATTR_DOUBLE.SCALE;
/*      */     case 2:
/*      */     case 4:
/*      */     case 6:
/*      */     case 7:
/*      */     case 8:
/*      */     case 9:
/*      */     case 10:
/*      */     case 11:
/*      */     case 14:
/*   69 */     case 15: } return null;
/*      */   }
/*      */ 
/*      */   public static final IChartObject.ATTR_INT getObjectPropInt(int index)
/*      */   {
/*   74 */     switch (index) {
/*      */     case 14:
/*   76 */       return IChartObject.ATTR_INT.ARROWCODE;
/*      */     case 101:
/*   78 */       return IChartObject.ATTR_INT.CORNER;
/*      */     case 100:
/*   80 */       return IChartObject.ATTR_INT.FONTSIZE;
/*      */     case 202:
/*   82 */       return IChartObject.ATTR_INT.LEVELSTYLE;
/*      */     case 203:
/*   84 */       return IChartObject.ATTR_INT.LEVELWIDTH;
/*      */     case 7:
/*   86 */       return IChartObject.ATTR_INT.STYLE;
/*      */     case 15:
/*   88 */       return IChartObject.ATTR_INT.TIMEFRAMES;
/*      */     case 8:
/*   90 */       return IChartObject.ATTR_INT.WIDTH;
/*      */     case 102:
/*   92 */       return IChartObject.ATTR_INT.XDISTANCE;
/*      */     case 103:
/*   94 */       return IChartObject.ATTR_INT.YDISTANCE;
/*      */     }
/*      */ 
/*   97 */     return null;
/*      */   }
/*      */ 
/*      */   public static final IChartObject.ATTR_LONG getObjectPropLong(int index)
/*      */   {
/*  102 */     switch (index) {
/*      */     case 0:
/*  104 */       return IChartObject.ATTR_LONG.TIME1;
/*      */     case 2:
/*  106 */       return IChartObject.ATTR_LONG.TIME2;
/*      */     case 4:
/*  108 */       return IChartObject.ATTR_LONG.TIME3;
/*      */     case 1:
/*  110 */     case 3: } return null;
/*      */   }
/*      */ 
/*      */   public static final Period convertPeriod(int timeframe)
/*      */   {
/*  116 */     Period rc = null;
/*  117 */     switch (timeframe) {
/*      */     case 1:
/*  119 */       rc = Period.ONE_MIN;
/*  120 */       break;
/*      */     case 5:
/*  122 */       rc = Period.FIVE_MINS;
/*  123 */       break;
/*      */     case 15:
/*  125 */       rc = Period.FIFTEEN_MINS;
/*  126 */       break;
/*      */     case 30:
/*  128 */       rc = Period.THIRTY_MINS;
/*  129 */       break;
/*      */     case 60:
/*  131 */       rc = Period.ONE_HOUR;
/*  132 */       break;
/*      */     case 240:
/*  134 */       rc = Period.FOUR_HOURS;
/*  135 */       break;
/*      */     case 1440:
/*  137 */       rc = Period.DAILY;
/*  138 */       break;
/*      */     case 10080:
/*  141 */       rc = Period.WEEKLY;
/*  142 */       break;
/*      */     case 43200:
/*  144 */       rc = Period.MONTHLY;
/*  145 */       break;
/*      */     default:
/*  147 */       notSupported();
/*      */     }
/*      */ 
/*  151 */     return rc;
/*      */   }
/*      */ 
/*      */   public static final int convertFromChartType(IChart.Type type) {
/*  155 */     int rc = -1;
/*  156 */     if (type.equals(IChart.Type.VLINE))
/*  157 */       rc = 0;
/*  158 */     else if (type.equals(IChart.Type.HLINE))
/*  159 */       rc = 1;
/*  160 */     else if (type.equals(IChart.Type.TREND))
/*  161 */       rc = 2;
/*  162 */     else if (type.equals(IChart.Type.TRENDBYANGLE))
/*  163 */       rc = 3;
/*  164 */     else if (type == IChart.Type.REGRESSION)
/*  165 */       rc = 4;
/*  166 */     else if (type == IChart.Type.CHANNEL)
/*  167 */       rc = 5;
/*  168 */     else if (type == IChart.Type.STDDEVCHANNEL)
/*  169 */       rc = 6;
/*  170 */     else if (type == IChart.Type.GANNLINE)
/*  171 */       rc = 7;
/*  172 */     else if (type == IChart.Type.GANNLINE)
/*  173 */       rc = 7;
/*  174 */     else if (type == IChart.Type.GANNFAN)
/*  175 */       rc = 8;
/*  176 */     else if (type == IChart.Type.FIBO)
/*  177 */       rc = 10;
/*  178 */     else if (type == IChart.Type.FIBOTIMES)
/*  179 */       rc = 11;
/*  180 */     else if (type == IChart.Type.FIBOFAN)
/*  181 */       rc = 12;
/*  182 */     else if (type == IChart.Type.FIBOARC)
/*  183 */       rc = 13;
/*  184 */     else if (type == IChart.Type.EXPANSION)
/*  185 */       rc = 14;
/*  186 */     else if (type == IChart.Type.FIBOCHANNEL)
/*  187 */       rc = 15;
/*  188 */     else if (type == IChart.Type.RECTANGLE)
/*  189 */       rc = 16;
/*  190 */     else if (type == IChart.Type.TRIANGLE)
/*  191 */       rc = 17;
/*  192 */     else if (type == IChart.Type.ELLIPSE)
/*  193 */       rc = 18;
/*  194 */     else if (type == IChart.Type.PITCHFORK)
/*  195 */       rc = 19;
/*  196 */     else if (type == IChart.Type.CYCLES)
/*  197 */       rc = 20;
/*  198 */     else if (type == IChart.Type.TEXT)
/*  199 */       rc = 21;
/*  200 */     else if (type.equals(IChart.Type.SIGNAL_DOWN))
/*  201 */       rc = 22;
/*  202 */     else if (type == IChart.Type.LABEL) {
/*  203 */       rc = 23;
/*      */     }
/*  205 */     return rc;
/*      */   }
/*      */ 
/*      */   public static final IChart.Type convertChartType(int type) {
/*  209 */     IChart.Type rc = null;
/*      */ 
/*  211 */     switch (type) {
/*      */     case 0:
/*  213 */       rc = IChart.Type.VLINE;
/*  214 */       break;
/*      */     case 1:
/*  216 */       rc = IChart.Type.HLINE;
/*  217 */       break;
/*      */     case 2:
/*  219 */       rc = IChart.Type.TREND;
/*  220 */       break;
/*      */     case 3:
/*  222 */       rc = IChart.Type.TRENDBYANGLE;
/*  223 */       break;
/*      */     case 4:
/*  225 */       rc = IChart.Type.REGRESSION;
/*  226 */       break;
/*      */     case 5:
/*  228 */       rc = IChart.Type.CHANNEL;
/*  229 */       break;
/*      */     case 6:
/*  231 */       rc = IChart.Type.STDDEVCHANNEL;
/*  232 */       break;
/*      */     case 7:
/*  234 */       rc = IChart.Type.GANNLINE;
/*  235 */       break;
/*      */     case 8:
/*  237 */       rc = IChart.Type.GANNFAN;
/*  238 */       break;
/*      */     case 9:
/*  240 */       rc = IChart.Type.GANNGRID;
/*  241 */       break;
/*      */     case 10:
/*  243 */       rc = IChart.Type.FIBO;
/*  244 */       break;
/*      */     case 11:
/*  246 */       rc = IChart.Type.FIBOTIMES;
/*  247 */       break;
/*      */     case 12:
/*  249 */       rc = IChart.Type.FIBOFAN;
/*  250 */       break;
/*      */     case 13:
/*  252 */       rc = IChart.Type.FIBOARC;
/*  253 */       break;
/*      */     case 14:
/*  255 */       rc = IChart.Type.EXPANSION;
/*  256 */       break;
/*      */     case 15:
/*  258 */       rc = IChart.Type.FIBOCHANNEL;
/*  259 */       break;
/*      */     case 16:
/*  261 */       rc = IChart.Type.RECTANGLE;
/*  262 */       break;
/*      */     case 17:
/*  264 */       rc = IChart.Type.TRIANGLE;
/*  265 */       break;
/*      */     case 18:
/*  267 */       rc = IChart.Type.ELLIPSE;
/*  268 */       break;
/*      */     case 19:
/*  270 */       rc = IChart.Type.PITCHFORK;
/*  271 */       break;
/*      */     case 20:
/*  273 */       rc = IChart.Type.CYCLES;
/*  274 */       break;
/*      */     case 21:
/*  276 */       rc = IChart.Type.TEXT;
/*  277 */       break;
/*      */     case 22:
/*  279 */       rc = IChart.Type.SIGNAL_DOWN;
/*  280 */       break;
/*      */     case 23:
/*  282 */       rc = IChart.Type.LABEL;
/*  283 */       break;
/*      */     }
/*      */ 
/*  289 */     return rc;
/*      */   }
/*      */ 
/*      */   public static final IIndicators.AppliedPrice convertAppliedPrice(long applied_price) {
/*  293 */     IIndicators.AppliedPrice rc = null;
/*  294 */     int sw = Long.valueOf(applied_price).intValue();
/*  295 */     switch (sw) {
/*      */     case 0:
/*  297 */       rc = IIndicators.AppliedPrice.CLOSE;
/*  298 */       break;
/*      */     case 1:
/*  300 */       rc = IIndicators.AppliedPrice.OPEN;
/*  301 */       break;
/*      */     case 2:
/*  303 */       rc = IIndicators.AppliedPrice.HIGH;
/*  304 */       break;
/*      */     case 3:
/*  306 */       rc = IIndicators.AppliedPrice.LOW;
/*  307 */       break;
/*      */     case 4:
/*  309 */       rc = IIndicators.AppliedPrice.MEDIAN_PRICE;
/*  310 */       break;
/*      */     case 5:
/*  312 */       rc = IIndicators.AppliedPrice.TYPICAL_PRICE;
/*  313 */       break;
/*      */     case 6:
/*  315 */       rc = IIndicators.AppliedPrice.WEIGHTED_CLOSE;
/*  316 */       break;
/*      */     default:
/*  319 */       notSupported();
/*      */     }
/*      */ 
/*  323 */     return rc;
/*      */   }
/*      */ 
/*      */   public static final IIndicators.MaType convertMaType(long ma_method) {
/*  327 */     IIndicators.MaType rc = null;
/*  328 */     int sw = Long.valueOf(ma_method).intValue();
/*  329 */     switch (sw) {
/*      */     case 0:
/*  331 */       rc = IIndicators.MaType.SMA;
/*  332 */       break;
/*      */     case 1:
/*  334 */       rc = IIndicators.MaType.EMA;
/*  335 */       break;
/*      */     case 2:
/*  337 */       rc = IIndicators.MaType.SMMA;
/*  338 */       break;
/*      */     case 3:
/*  340 */       rc = IIndicators.MaType.LWMA;
/*  341 */       break;
/*      */     default:
/*  344 */       notSupported();
/*      */     }
/*      */ 
/*  348 */     return rc;
/*      */   }
/*      */ 
/*      */   public static final BitSet bitSetFromInt(int integer) {
/*  352 */     BitSet bitSet = new BitSet();
/*  353 */     String binarStr = Integer.toBinaryString(integer);
/*  354 */     for (int i = 0; i < binarStr.length(); i++) {
/*  355 */       boolean bin = binarStr.charAt(i) == '1';
/*  356 */       bitSet.set(binarStr.length() - i - 1, bin);
/*      */     }
/*      */ 
/*  363 */     return bitSet;
/*      */   }
/*      */ 
/*      */   public static final int intFromBitset(BitSet bitSet)
/*      */   {
/*  368 */     StringBuilder builder = new StringBuilder();
/*  369 */     for (int i = bitSet.length() - 1; i >= 0; i--) {
/*  370 */       builder.append(bitSet.get(i) ? '1' : '0');
/*      */     }
/*  372 */     String str = builder.toString();
/*  373 */     if (str.length() == 0) {
/*  374 */       str = "0";
/*      */     }
/*  376 */     return Integer.valueOf(str, 2).intValue();
/*      */   }
/*      */ 
/*      */   protected int bool2int(boolean bool) {
/*  380 */     return bool ? 1 : 0;
/*      */   }
/*      */ 
/*      */   public static final double parseDouble(String str)
/*      */   {
/*      */     try
/*      */     {
/*  394 */       if (str == null) {
/*  395 */         return 0.0D;
/*      */       }
/*  397 */       char c = storeFormat.getDecimalFormatSymbols().getGroupingSeparator();
/*  398 */       str = str.replace(c, ',');
/*  399 */       str = str.replaceAll(" ", "");
/*  400 */       str = str.replaceAll(",", "");
/*  401 */       return Double.parseDouble(str);
/*      */     } catch (Exception e) {
/*      */     }
/*  404 */     return 0.0D;
/*      */   }
/*      */ 
/*      */   public static final String formatDouble(double dd) {
/*  408 */     return storeFormat.format(dd);
/*      */   }
/*      */ 
/*      */   public static final void notOpperationSupported() throws JFException {
/*  412 */     throw new JFException("connector.err.operation.not.supported.message");
/*      */   }
/*      */   public static final void notSupported() {
/*  415 */     throw new RuntimeException("connector.err.operation.not.supported.message");
/*      */   }
/*      */ 
/*      */   public static final void notFunctionSupported() {
/*  419 */     throw new RuntimeException("Function unsupported");
/*      */   }
/*      */ 
/*      */   public static final void notNullStrategy() {
/*  423 */     throw new RuntimeException("Strategy for MQLConnector is NULL");
/*      */   }
/*      */ 
/*      */   public static final void notNullIndicator() {
/*  427 */     throw new RuntimeException("Indicator for MQLConnector is NULL");
/*      */   }
/*      */ 
/*      */   public static final int toInt(double value) {
/*  431 */     Double d = new Double(value);
/*  432 */     return d.intValue();
/*      */   }
/*      */ 
/*      */   public static final long toLong(double value) {
/*  436 */     Double d = new Double(value);
/*  437 */     return d.longValue();
/*      */   }
/*      */ 
/*      */   public static final double toDouble(long value) {
/*  441 */     Double d = new Double(value);
/*  442 */     return d.doubleValue();
/*      */   }
/*      */ 
/*      */   public static final Instrument fromString(String symbol, String defSymbol) throws JFException {
/*  446 */     String newSymbol = null;
/*  447 */     if (symbol != null) {
/*  448 */       newSymbol = new String(defSymbol);
/*      */     }
/*  450 */     return fromString(newSymbol);
/*      */   }
/*      */ 
/*      */   public static final Instrument fromString(String symbol) throws JFException {
/*  454 */     Instrument instrument = null;
/*  455 */     if (symbol != null) {
/*  456 */       String newSymbol = new String(symbol);
/*  457 */       if (newSymbol.length() == 6) {
/*  458 */         newSymbol = new StringBuilder().append(newSymbol.substring(0, 3)).append("/").append(newSymbol.substring(3)).toString();
/*      */       }
/*      */       else {
/*  461 */         throw new JFException("Instrument illegal size");
/*      */       }
/*  463 */       instrument = Instrument.fromString(newSymbol);
/*      */     }
/*  465 */     return instrument;
/*      */   }
/*      */ 
/*      */   public static final int OrderCommand2OrderType(IEngine.OrderCommand cmd)
/*      */   {
/*  479 */     int rc = -1;
/*  480 */     if (cmd == IEngine.OrderCommand.BUY)
/*  481 */       rc = 0;
/*  482 */     else if (cmd == IEngine.OrderCommand.SELL)
/*  483 */       rc = 1;
/*  484 */     else if (cmd == IEngine.OrderCommand.BUYLIMIT)
/*  485 */       rc = 2;
/*  486 */     else if (cmd == IEngine.OrderCommand.BUYSTOP)
/*  487 */       rc = 4;
/*  488 */     else if (cmd == IEngine.OrderCommand.SELLLIMIT)
/*  489 */       rc = 3;
/*  490 */     else if (cmd == IEngine.OrderCommand.SELLSTOP)
/*  491 */       rc = 5;
/*  492 */     else if (cmd == IEngine.OrderCommand.PLACE_BID)
/*  493 */       rc = 6;
/*  494 */     else if (cmd == IEngine.OrderCommand.PLACE_OFFER) {
/*  495 */       rc = 7;
/*      */     }
/*  497 */     return rc;
/*      */   }
/*      */ 
/*      */   public static final IEngine.OrderCommand getOrderCommand(int cmd) {
/*  501 */     IEngine.OrderCommand oCommand = null;
/*  502 */     switch (cmd) {
/*      */     case 0:
/*  504 */       oCommand = IEngine.OrderCommand.BUY;
/*  505 */       break;
/*      */     case 1:
/*  507 */       oCommand = IEngine.OrderCommand.SELL;
/*  508 */       break;
/*      */     case 2:
/*  510 */       oCommand = IEngine.OrderCommand.BUYLIMIT;
/*  511 */       break;
/*      */     case 3:
/*  513 */       oCommand = IEngine.OrderCommand.SELLLIMIT;
/*  514 */       break;
/*      */     case 4:
/*  516 */       oCommand = IEngine.OrderCommand.BUYSTOP;
/*  517 */       break;
/*      */     case 5:
/*  519 */       oCommand = IEngine.OrderCommand.SELLSTOP;
/*  520 */       break;
/*      */     case 6:
/*  522 */       oCommand = IEngine.OrderCommand.PLACE_BID;
/*  523 */       break;
/*      */     case 7:
/*  525 */       oCommand = IEngine.OrderCommand.PLACE_OFFER;
/*  526 */       break;
/*      */     default:
/*  528 */       notSupported();
/*      */     }
/*      */ 
/*  531 */     return oCommand;
/*      */   }
/*      */ 
/*      */   public static String ErrorDescription(int error_code)
/*      */     throws JFException
/*      */   {
/*  537 */     String error_String = "";
/*      */ 
/*  539 */     switch (toInt(error_code)) {
/*      */     case 0:
/*      */     case 1:
/*  542 */       error_String = "no error";
/*      */ 
/*  544 */       break;
/*      */     case 2:
/*  546 */       error_String = "common error";
/*      */ 
/*  548 */       break;
/*      */     case 3:
/*  550 */       error_String = "invalid trade parameters";
/*      */ 
/*  552 */       break;
/*      */     case 4:
/*  554 */       error_String = "trade server is busy";
/*      */ 
/*  556 */       break;
/*      */     case 5:
/*  558 */       error_String = "old version of the client terminal";
/*      */ 
/*  560 */       break;
/*      */     case 6:
/*  562 */       error_String = "no connection with trade server";
/*      */ 
/*  564 */       break;
/*      */     case 7:
/*  566 */       error_String = "not enough rights";
/*      */ 
/*  568 */       break;
/*      */     case 8:
/*  570 */       error_String = "too frequent requests";
/*      */ 
/*  572 */       break;
/*      */     case 9:
/*  574 */       error_String = "malfunctional trade operation";
/*      */ 
/*  576 */       break;
/*      */     case 64:
/*  578 */       error_String = "account disabled";
/*      */ 
/*  580 */       break;
/*      */     case 65:
/*  582 */       error_String = "invalid account";
/*      */ 
/*  584 */       break;
/*      */     case 128:
/*  586 */       error_String = "trade timeout";
/*      */ 
/*  588 */       break;
/*      */     case 129:
/*  590 */       error_String = "invalid price";
/*      */ 
/*  592 */       break;
/*      */     case 130:
/*  594 */       error_String = "invalid stops";
/*      */ 
/*  596 */       break;
/*      */     case 131:
/*  598 */       error_String = "invalid trade volume";
/*      */ 
/*  600 */       break;
/*      */     case 132:
/*  602 */       error_String = "market is closed";
/*      */ 
/*  604 */       break;
/*      */     case 133:
/*  606 */       error_String = "trade is disabled";
/*      */ 
/*  608 */       break;
/*      */     case 134:
/*  610 */       error_String = "not enough money";
/*      */ 
/*  612 */       break;
/*      */     case 135:
/*  614 */       error_String = "price changed";
/*      */ 
/*  616 */       break;
/*      */     case 136:
/*  618 */       error_String = "off quotes";
/*      */ 
/*  620 */       break;
/*      */     case 137:
/*  622 */       error_String = "broker is busy";
/*      */ 
/*  624 */       break;
/*      */     case 138:
/*  626 */       error_String = "requote";
/*      */ 
/*  628 */       break;
/*      */     case 139:
/*  630 */       error_String = "order is locked";
/*      */ 
/*  632 */       break;
/*      */     case 140:
/*  634 */       error_String = "long positions only allowed";
/*      */ 
/*  636 */       break;
/*      */     case 141:
/*  638 */       error_String = "too many requests";
/*      */ 
/*  640 */       break;
/*      */     case 145:
/*  642 */       error_String = "modification denied because order too close to market";
/*      */ 
/*  644 */       break;
/*      */     case 146:
/*  646 */       error_String = "trade context is busy";
/*      */ 
/*  648 */       break;
/*      */     case 4000:
/*  650 */       error_String = "no error";
/*      */ 
/*  652 */       break;
/*      */     case 4001:
/*  654 */       error_String = "wrong function pointer";
/*      */ 
/*  656 */       break;
/*      */     case 4002:
/*  658 */       error_String = "array index is out of range";
/*      */ 
/*  660 */       break;
/*      */     case 4003:
/*  662 */       error_String = "no memory for function call stack";
/*      */ 
/*  664 */       break;
/*      */     case 4004:
/*  666 */       error_String = "recursive stack overflow";
/*      */ 
/*  668 */       break;
/*      */     case 4005:
/*  670 */       error_String = "not enough stack for parameter";
/*      */ 
/*  672 */       break;
/*      */     case 4006:
/*  674 */       error_String = "no memory for parameter string";
/*      */ 
/*  676 */       break;
/*      */     case 4007:
/*  678 */       error_String = "no memory for temp string";
/*      */ 
/*  680 */       break;
/*      */     case 4008:
/*  682 */       error_String = "not initialized string";
/*      */ 
/*  684 */       break;
/*      */     case 4009:
/*  686 */       error_String = "not initialized String in array";
/*      */ 
/*  688 */       break;
/*      */     case 4010:
/*  690 */       error_String = "no memory for array' string";
/*      */ 
/*  692 */       break;
/*      */     case 4011:
/*  694 */       error_String = "too long string";
/*      */ 
/*  696 */       break;
/*      */     case 4012:
/*  698 */       error_String = "remainder from zero divide";
/*      */ 
/*  700 */       break;
/*      */     case 4013:
/*  702 */       error_String = "zero divide";
/*      */ 
/*  704 */       break;
/*      */     case 4014:
/*  706 */       error_String = "unknown command";
/*      */ 
/*  708 */       break;
/*      */     case 4015:
/*  710 */       error_String = "wrong jump (never generated error)";
/*      */ 
/*  712 */       break;
/*      */     case 4016:
/*  714 */       error_String = "not initialized array";
/*      */ 
/*  716 */       break;
/*      */     case 4017:
/*  718 */       error_String = "dll calls are not allowed";
/*      */ 
/*  720 */       break;
/*      */     case 4018:
/*  722 */       error_String = "cannot load library";
/*      */ 
/*  724 */       break;
/*      */     case 4019:
/*  726 */       error_String = "cannot call function";
/*      */ 
/*  728 */       break;
/*      */     case 4020:
/*  730 */       error_String = "expert function calls are not allowed";
/*      */ 
/*  732 */       break;
/*      */     case 4021:
/*  734 */       error_String = "not enough memory for temp String returned from function";
/*      */ 
/*  736 */       break;
/*      */     case 4022:
/*  738 */       error_String = "system is busy (never generated error)";
/*      */ 
/*  740 */       break;
/*      */     case 4050:
/*  742 */       error_String = "invalid function parameters count";
/*      */ 
/*  744 */       break;
/*      */     case 4051:
/*  746 */       error_String = "invalid function parameter value";
/*      */ 
/*  748 */       break;
/*      */     case 4052:
/*  750 */       error_String = "String function internal error";
/*      */ 
/*  752 */       break;
/*      */     case 4053:
/*  754 */       error_String = "some array error";
/*      */ 
/*  756 */       break;
/*      */     case 4054:
/*  758 */       error_String = "incorrect series array using";
/*      */ 
/*  760 */       break;
/*      */     case 4055:
/*  762 */       error_String = "custom indicator error";
/*      */ 
/*  764 */       break;
/*      */     case 4056:
/*  766 */       error_String = "arrays are incompatible";
/*      */ 
/*  768 */       break;
/*      */     case 4057:
/*  770 */       error_String = "global variables processing error";
/*      */ 
/*  772 */       break;
/*      */     case 4058:
/*  774 */       error_String = "global variable not found";
/*      */ 
/*  776 */       break;
/*      */     case 4059:
/*  778 */       error_String = "function is not allowed in testing mode";
/*      */ 
/*  780 */       break;
/*      */     case 4060:
/*  782 */       error_String = "function is not confirmed";
/*      */ 
/*  784 */       break;
/*      */     case 4061:
/*  786 */       error_String = "send mail error";
/*      */ 
/*  788 */       break;
/*      */     case 4062:
/*  790 */       error_String = "String parameter expected";
/*      */ 
/*  792 */       break;
/*      */     case 4063:
/*  794 */       error_String = "integer parameter expected";
/*      */ 
/*  796 */       break;
/*      */     case 4064:
/*  798 */       error_String = "double parameter expected";
/*      */ 
/*  800 */       break;
/*      */     case 4065:
/*  802 */       error_String = "array as parameter expected";
/*      */ 
/*  804 */       break;
/*      */     case 4066:
/*  806 */       error_String = "requested history data in update state";
/*      */ 
/*  808 */       break;
/*      */     case 4099:
/*  810 */       error_String = "end of file";
/*      */ 
/*  812 */       break;
/*      */     case 4100:
/*  814 */       error_String = "some file error";
/*      */ 
/*  816 */       break;
/*      */     case 4101:
/*  818 */       error_String = "wrong file name";
/*      */ 
/*  820 */       break;
/*      */     case 4102:
/*  822 */       error_String = "too many opened files";
/*      */ 
/*  824 */       break;
/*      */     case 4103:
/*  826 */       error_String = "cannot open file";
/*      */ 
/*  828 */       break;
/*      */     case 4104:
/*  830 */       error_String = "incompatible access to a file";
/*      */ 
/*  832 */       break;
/*      */     case 4105:
/*  834 */       error_String = "no order selected";
/*      */ 
/*  836 */       break;
/*      */     case 4106:
/*  838 */       error_String = "unknown symbol";
/*      */ 
/*  840 */       break;
/*      */     case 4107:
/*  842 */       error_String = "invalid price parameter for trade function";
/*      */ 
/*  844 */       break;
/*      */     case 4108:
/*  846 */       error_String = "invalid ticket";
/*      */ 
/*  848 */       break;
/*      */     case 4109:
/*  850 */       error_String = "trade is not allowed";
/*      */ 
/*  852 */       break;
/*      */     case 4110:
/*  854 */       error_String = "longs are not allowed";
/*      */ 
/*  856 */       break;
/*      */     case 4111:
/*  858 */       error_String = "shorts are not allowed";
/*      */ 
/*  860 */       break;
/*      */     case 4200:
/*  862 */       error_String = "void is already exist";
/*      */ 
/*  864 */       break;
/*      */     case 4201:
/*  866 */       error_String = "unknown void property";
/*      */ 
/*  868 */       break;
/*      */     case 4202:
/*  870 */       error_String = "void is not exist";
/*      */ 
/*  872 */       break;
/*      */     case 4203:
/*  874 */       error_String = "unknown void type";
/*      */ 
/*  876 */       break;
/*      */     case 4204:
/*  878 */       error_String = "no void name";
/*      */ 
/*  880 */       break;
/*      */     case 4205:
/*  882 */       error_String = "void coordinates error";
/*      */ 
/*  884 */       break;
/*      */     case 4206:
/*  886 */       error_String = "no specified subwindow";
/*      */ 
/*  888 */       break;
/*      */     default:
/*  890 */       error_String = "unknown error";
/*      */     }
/*  892 */     return error_String;
/*      */   }
/*      */ 
/*      */   public static double[][] getIndicatorInputDataReverse(IBar[] timeData)
/*      */   {
/*  903 */     double[][] data = new double[timeData.length][6];
/*  904 */     int k = 0;
/*  905 */     for (IBar candle : timeData) {
/*  906 */       data[k][0] = candle.getTime();
/*  907 */       data[k][1] = candle.getOpen();
/*  908 */       data[k][2] = candle.getLow();
/*  909 */       data[k][3] = candle.getHigh();
/*  910 */       data[k][4] = candle.getClose();
/*  911 */       data[k][5] = candle.getVolume();
/*  912 */       k++;
/*      */     }
/*  914 */     return data;
/*      */   }
/*      */ 
/*      */   public static double[][] getIndicatorInputData(IBar[] timeData)
/*      */   {
/*  928 */     int dataSize = timeData.length;
/*  929 */     double[] time = new double[dataSize];
/*  930 */     double[] open = new double[dataSize];
/*  931 */     double[] high = new double[dataSize];
/*  932 */     double[] low = new double[dataSize];
/*  933 */     double[] close = new double[dataSize];
/*  934 */     double[] volume = new double[dataSize];
/*  935 */     int k = 0;
/*  936 */     for (IBar candle : timeData) {
/*  937 */       time[k] = candle.getTime();
/*  938 */       open[k] = candle.getOpen();
/*  939 */       high[k] = candle.getHigh();
/*  940 */       low[k] = candle.getLow();
/*  941 */       close[k] = candle.getClose();
/*  942 */       volume[k] = candle.getVolume();
/*  943 */       k++;
/*      */     }
/*  945 */     return new double[][] { time, open, low, high, close, volume };
/*      */   }
/*      */ 
/*      */   public static double[] getIndicatorInputData(IBar[] timeData, int index) {
/*  949 */     int dataSize = timeData.length;
/*  950 */     double[] data = new double[dataSize];
/*  951 */     Arrays.fill(data, (0.0D / 0.0D));
/*      */     int i;
/*  952 */     switch (index) {
/*      */     case 0:
/*  954 */       i = 0;
/*  955 */       for (IBar bar : timeData) {
/*  956 */         data[(i++)] = bar.getOpen();
/*      */       }
/*      */     case 2:
/*  959 */       i = 0;
/*  960 */       for (IBar bar : timeData) {
/*  961 */         data[(i++)] = bar.getHigh();
/*      */       }
/*      */     case 1:
/*  964 */       i = 0;
/*  965 */       for (IBar bar : timeData) {
/*  966 */         data[(i++)] = bar.getLow();
/*      */       }
/*      */     case 3:
/*  969 */       i = 0;
/*  970 */       for (IBar bar : timeData) {
/*  971 */         data[(i++)] = bar.getClose();
/*      */       }
/*      */     case 4:
/*  974 */       i = 0;
/*  975 */       for (IBar bar : timeData) {
/*  976 */         data[(i++)] = bar.getVolume();
/*      */       }
/*      */     case 5:
/*  979 */       i = 0;
/*  980 */       for (IBar bar : timeData) {
/*  981 */         data[(i++)] = bar.getTime();
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  986 */     return data;
/*      */   }
/*      */ 
/*      */   public static Object getIndicatorInputData(InputParameterInfo inputParameterInfo, IIndicators.AppliedPrice appliedPrice, IBar[] timeData)
/*      */   {
/*  991 */     int dataSize = timeData.length;
/*      */     int k;
/*  992 */     switch (1.$SwitchMap$com$dukascopy$api$indicators$InputParameterInfo$Type[inputParameterInfo.getType().ordinal()]) {
/*      */     case 1:
/*  994 */       double[] open = new double[dataSize];
/*  995 */       double[] high = new double[dataSize];
/*  996 */       double[] low = new double[dataSize];
/*  997 */       double[] close = new double[dataSize];
/*  998 */       double[] volume = new double[dataSize];
/*  999 */       k = 0;
/* 1000 */       for (IBar candle : timeData) {
/* 1001 */         open[k] = candle.getOpen();
/* 1002 */         high[k] = candle.getHigh();
/* 1003 */         low[k] = candle.getLow();
/* 1004 */         close[k] = candle.getClose();
/* 1005 */         volume[k] = candle.getVolume();
/* 1006 */         k++;
/*      */       }
/* 1008 */       return new double[][] { open, close, high, low, volume };
/*      */     case 2:
/* 1010 */       double[] data = new double[dataSize];
/* 1011 */       switch (1.$SwitchMap$com$dukascopy$api$IIndicators$AppliedPrice[appliedPrice.ordinal()]) {
/*      */       case 1:
/* 1013 */         k = 0;
/* 1014 */         for (IBar candle : timeData) {
/* 1015 */           data[k] = candle.getClose();
/* 1016 */           k++;
/*      */         }
/* 1018 */         break;
/*      */       case 2:
/* 1020 */         k = 0;
/* 1021 */         for (IBar candle : timeData) {
/* 1022 */           data[k] = candle.getHigh();
/* 1023 */           k++;
/*      */         }
/* 1025 */         break;
/*      */       case 3:
/* 1027 */         k = 0;
/* 1028 */         for (IBar candle : timeData) {
/* 1029 */           data[k] = candle.getLow();
/* 1030 */           k++;
/*      */         }
/* 1032 */         break;
/*      */       case 4:
/* 1034 */         k = 0;
/* 1035 */         for (IBar candle : timeData) {
/* 1036 */           data[k] = candle.getOpen();
/* 1037 */           k++;
/*      */         }
/* 1039 */         break;
/*      */       case 5:
/* 1041 */         k = 0;
/* 1042 */         for (IBar candle : timeData) {
/* 1043 */           data[k] = ((candle.getHigh() + candle.getLow()) / 2.0D);
/* 1044 */           k++;
/*      */         }
/* 1046 */         break;
/*      */       case 6:
/* 1048 */         k = 0;
/* 1049 */         for (IBar candle : timeData) {
/* 1050 */           data[k] = ((candle.getHigh() + candle.getLow() + candle.getClose()) / 3.0D);
/* 1051 */           k++;
/*      */         }
/* 1053 */         break;
/*      */       case 7:
/* 1055 */         k = 0;
/* 1056 */         for (IBar candle : timeData) {
/* 1057 */           data[k] = ((candle.getHigh() + candle.getLow() + candle.getClose() + candle.getClose()) / 4.0D);
/* 1058 */           k++;
/*      */         }
/* 1060 */         break;
/*      */       case 8:
/* 1062 */         k = 0;
/* 1063 */         for (IBar candle : timeData) {
/* 1064 */           data[k] = candle.getTime();
/* 1065 */           k++;
/*      */         }
/* 1067 */         break;
/*      */       case 9:
/* 1069 */         k = 0;
/* 1070 */         for (IBar candle : timeData) {
/* 1071 */           data[k] = candle.getVolume();
/* 1072 */           k++;
/*      */         }
/*      */       }
/*      */ 
/* 1076 */       return data;
/*      */     case 3:
/* 1078 */       return timeData;
/*      */     }
/* 1080 */     if (!$assertionsDisabled) throw new AssertionError("shouldn't be here");
/* 1081 */     return null;
/*      */   }
/*      */ 
/*      */   public static final String multiargToString(String delim, Object[] str) {
/* 1085 */     StringBuilder builder = new StringBuilder();
/* 1086 */     for (Object object : str) {
/* 1087 */       if ((object instanceof Number)) {
/* 1088 */         Number number = (Number)object;
/* 1089 */         if (Math.abs(number.doubleValue()) > 1.0E-006D)
/* 1090 */           builder.append(new StringBuilder().append(delim).append(dfPrint.format(object)).toString());
/*      */         else
/* 1092 */           builder.append(new StringBuilder().append(delim).append(object).toString());
/*      */       }
/*      */       else {
/* 1095 */         builder.append(new StringBuilder().append(delim).append(object).toString());
/*      */       }
/*      */     }
/* 1098 */     return builder.substring(delim.length());
/*      */   }
/*      */ 
/*      */   static
/*      */   {
/*  383 */     storeFormat = new DecimalFormat("#,##0.#", new DecimalFormatSymbols(Locale.US));
/*      */ 
/*  896 */     dateFormat1 = new SimpleDateFormat("yyyy.MM.dd HH:mm");
/*  897 */     dateFormatTIME_SECONDS = new SimpleDateFormat("HH:mm:ss");
/*  898 */     dateFormatTIME_MINUTES = new SimpleDateFormat("HH:mm");
/*  899 */     dateFormatTIME_DATE = new SimpleDateFormat("yyyy.MM.dd");
/*  900 */     dfPrint = new DecimalFormat("#,##0.##########");
/*      */   }
/*      */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Connector-1.1.49.jar
 * Qualified Name:     com.dukascopy.dds2.greed.connector.helpers.CommonHelpers
 * JD-Core Version:    0.6.0
 */