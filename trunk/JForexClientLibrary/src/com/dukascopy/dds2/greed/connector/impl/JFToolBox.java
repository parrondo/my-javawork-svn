/*      */ package com.dukascopy.dds2.greed.connector.impl;
/*      */ 
/*      */ import com.dukascopy.api.IBar;
/*      */ import com.dukascopy.api.IConsole;
/*      */ import com.dukascopy.api.IContext;
/*      */ import com.dukascopy.api.IEngine;
/*      */ import com.dukascopy.api.IEngine.OrderCommand;
/*      */ import com.dukascopy.api.IEngine.Type;
/*      */ import com.dukascopy.api.IHistory;
/*      */ import com.dukascopy.api.IOrder;
/*      */ import com.dukascopy.api.IOrder.State;
/*      */ import com.dukascopy.api.ITick;
/*      */ import com.dukascopy.api.Instrument;
/*      */ import com.dukascopy.api.JFException;
/*      */ import com.dukascopy.api.OfferSide;
/*      */ import com.dukascopy.api.Period;
/*      */ import com.dukascopy.api.connector.IBox;
/*      */ import com.dukascopy.api.connector.IConnector;
/*      */ import com.dukascopy.api.connector.ISettings;
/*      */ import com.dukascopy.api.indicators.IIndicatorContext;
/*      */ import java.awt.Image;
/*      */ import java.awt.Toolkit;
/*      */ import java.io.ByteArrayOutputStream;
/*      */ import java.io.File;
/*      */ import java.io.FileNotFoundException;
/*      */ import java.io.InputStream;
/*      */ import java.io.OutputStream;
/*      */ import java.io.PrintStream;
/*      */ import java.text.DecimalFormat;
/*      */ import java.text.ParseException;
/*      */ import java.text.SimpleDateFormat;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Collections;
/*      */ import java.util.Currency;
/*      */ import java.util.Date;
/*      */ import java.util.HashMap;
/*      */ import java.util.List;
/*      */ import java.util.Map;
/*      */ import java.util.Random;
/*      */ import java.util.Scanner;
/*      */ import java.util.regex.Matcher;
/*      */ import java.util.regex.Pattern;
/*      */ import javax.swing.Icon;
/*      */ import javax.swing.ImageIcon;
/*      */ 
/*      */ public class JFToolBox
/*      */   implements IBox
/*      */ {
/*   72 */   private IIndicatorContext indicatorContext = null;
/*   73 */   private IContext context = null;
/*      */ 
/*   75 */   private IEngine engine = null;
/*      */ 
/*   77 */   private IConsole console = null;
/*      */ 
/*   79 */   private IHistory history = null;
/*      */ 
/*   81 */   private CurrConverter converter = null;
/*      */ 
/*   83 */   static DecimalFormat dfPrint = new DecimalFormat("#,##0.##########");
/*      */ 
/*  444 */   private static Random random = new Random();
/*  445 */   private String MAGIC_DELIM = "__";
/*  446 */   private String MAGIC_PREFIX = "MTJF";
/*      */ 
/*  597 */   private Map<String, HighAvailabilityStore> highStores = new HashMap();
/*      */ 
/*  615 */   private AudioPlayer audioPlayer = new AudioPlayer();
/*      */ 
/*  817 */   private Instrument lastSureSignedInstrument = Instrument.EURUSD;
/*      */ 
/*  844 */   private SimpleDateFormat evaluationDateFormat = new SimpleDateFormat("yyyy.MM.dd");
/*  845 */   private boolean evaluationChecked = false;
/*      */ 
/*  881 */   Pattern timeePattern = Pattern.compile("([0-2][0-9]):([0-5][0-9])");
/*      */ 
/*  901 */   Pattern timeRangePattern = Pattern.compile("([0-2][0-9]):([0-5][0-9])-([0-2][0-9]):([0-5][0-9])");
/*      */ 
/*      */   public JFToolBox(IContext context)
/*      */   {
/*   86 */     if (context != null) {
/*   87 */       this.context = context;
/*   88 */       this.engine = context.getEngine();
/*   89 */       this.history = context.getHistory();
/*   90 */       this.console = context.getConsole();
/*   91 */       this.converter = new CurrConverter(context);
/*      */     }
/*      */   }
/*      */ 
/*      */   public JFToolBox(IIndicatorContext context, IEngine engine, IHistory history) {
/*   96 */     if (context != null) {
/*   97 */       this.indicatorContext = context;
/*   98 */       this.console = context.getConsole();
/*   99 */       this.engine = engine;
/*  100 */       this.history = history;
/*  101 */       this.converter = new CurrConverter(history);
/*      */     }
/*      */   }
/*      */ 
/*      */   public JFToolBox(IIndicatorContext context, IConnector connector) {
/*  106 */     if (context != null) {
/*  107 */       this.console = context.getConsole();
/*      */ 
/*  109 */       this.history = connector.getIHistory();
/*  110 */       this.converter = new CurrConverter(this.history);
/*      */     }
/*      */   }
/*      */ 
/*      */   public File getFilesDir()
/*      */   {
/*  120 */     File file = null;
/*  121 */     if (this.context != null) {
/*  122 */       file = this.context.getFilesDir();
/*      */     }
/*  124 */     return file;
/*      */   }
/*      */ 
/*      */   public Scanner getScanner(String scannerName)
/*      */   {
/*  135 */     return getScanner(scannerName, "\r\n|[\n\r  ]");
/*      */   }
/*      */ 
/*      */   public Scanner getScanner(File scannedFile)
/*      */   {
/*  147 */     return getScanner(scannedFile, "\r\n|[\n\r  ]");
/*      */   }
/*      */ 
/*      */   public Scanner getScanner(String scannerName, String delimiterPattern)
/*      */   {
/*  159 */     Scanner rc = null;
/*  160 */     if ((scannerName == null) || (scannerName.indexOf(File.separator) != -1) || (scannerName.indexOf(File.pathSeparator) != -1)) {
/*  161 */       return rc;
/*      */     }
/*  163 */     File theFile = new File(new StringBuilder().append(getFilesDir()).append(File.separator).append(scannerName).toString());
/*  164 */     rc = getScanner(theFile, delimiterPattern);
/*      */ 
/*  166 */     return rc;
/*      */   }
/*      */ 
/*      */   public Scanner getScanner(File theFile, String delimiterPattern)
/*      */   {
/*  179 */     Scanner rc = null;
/*  180 */     if ((theFile == null) || (!theFile.isFile()) || (!theFile.exists())) {
/*  181 */       return rc;
/*      */     }
/*      */     try
/*      */     {
/*  185 */       rc = new Scanner(theFile);
/*  186 */       if (delimiterPattern != null)
/*  187 */         rc.useDelimiter(delimiterPattern);
/*      */     }
/*      */     catch (FileNotFoundException e) {
/*  190 */       e.printStackTrace();
/*      */     }
/*  192 */     return rc;
/*      */   }
/*      */ 
/*      */   public List<IBar> getLastBars(Instrument instrument, Period period, OfferSide offerSide, int bars, boolean takeUnformedBar)
/*      */     throws JFException
/*      */   {
/*  212 */     long lastTickTime = this.history.getLastTick(instrument).getTime();
/*      */ 
/*  214 */     long prevBarStart = this.history.getPreviousBarStart(period, lastTickTime);
/*  215 */     long timeRange = period.getInterval() * (bars + 1);
/*  216 */     long oldestBarStart = this.history.getBarStart(period, lastTickTime - timeRange);
/*      */ 
/*  218 */     List rc = this.history.getBars(instrument, period, offerSide, oldestBarStart, prevBarStart);
/*      */ 
/*  220 */     if (takeUnformedBar) {
/*  221 */       IBar onProgressBar = this.history.getBar(instrument, period, offerSide, 0);
/*  222 */       if (((IBar)rc.get(rc.size() - 1)).getTime() != onProgressBar.getTime())
/*      */       {
/*  224 */         rc.add(onProgressBar);
/*      */       }
/*      */     }
/*  227 */     while (rc.size() > bars) {
/*  228 */       rc.remove(0);
/*      */     }
/*      */ 
/*  231 */     if (rc.size() != bars) {
/*  232 */       throw new JFException(new StringBuilder().append("getLastBars() assertion error [").append(rc.size()).append("]").toString());
/*      */     }
/*  234 */     Collections.reverse(rc);
/*  235 */     return rc;
/*      */   }
/*      */ 
/*      */   public double round(double value, int precision)
/*      */   {
/*  246 */     if (Double.isNaN(value)) {
/*  247 */       return value;
/*      */     }
/*  249 */     boolean negative = false;
/*  250 */     if (value < 0.0D) {
/*  251 */       negative = true;
/*  252 */       value = -value;
/*      */     }
/*  254 */     if (value == 0.0D) {
/*  255 */       return value;
/*      */     }
/*  257 */     double multiplier = 1.0D;
/*  258 */     while (precision > 0) {
/*  259 */       multiplier *= 10.0D;
/*  260 */       precision--;
/*      */     }
/*  262 */     while ((precision < 0) && (value * multiplier / 10.0D >= 1.0D)) {
/*  263 */       multiplier /= 10.0D;
/*  264 */       precision++;
/*      */     }
/*  266 */     while (value * multiplier < 1.0D) {
/*  267 */       multiplier *= 10.0D;
/*      */     }
/*  269 */     value *= multiplier;
/*  270 */     long longValue = ()(value + 0.5D);
/*  271 */     value = longValue / multiplier;
/*  272 */     return negative ? -value : value;
/*      */   }
/*      */ 
/*      */   public double roundHalfPip(double price)
/*      */   {
/*  277 */     int pipsMultiplier = price <= 20.0D ? 10000 : 100;
/*  278 */     int rounded = (int)(price * pipsMultiplier * 10.0D + 0.5D);
/*  279 */     rounded *= 2;
/*  280 */     rounded = (int)(rounded / 10.0D + 0.5D);
/*  281 */     price = rounded / 2.0D;
/*  282 */     price /= pipsMultiplier;
/*  283 */     return price;
/*      */   }
/*      */ 
/*      */   public void print(Object[] str)
/*      */   {
/*  288 */     this.console.getOut().println(multiargToString("", str));
/*      */   }
/*      */ 
/*      */   public String multiargToString(String delim, Object[] str) {
/*  292 */     StringBuilder builder = new StringBuilder();
/*  293 */     for (Object object : str) {
/*  294 */       if ((object instanceof Number)) {
/*  295 */         Number number = (Number)object;
/*  296 */         if (Math.abs(number.doubleValue()) > 1.0E-006D)
/*  297 */           builder.append(new StringBuilder().append(delim).append(dfPrint.format(object)).toString());
/*      */         else
/*  299 */           builder.append(new StringBuilder().append(delim).append(object).toString());
/*      */       }
/*      */       else {
/*  302 */         builder.append(new StringBuilder().append(delim).append(object).toString());
/*      */       }
/*      */     }
/*  305 */     return builder.substring(delim.length());
/*      */   }
/*      */ 
/*      */   public double convertMoney(double amount, Currency sourceCurrency, Currency targetCurrency) throws JFException {
/*  309 */     return this.converter.convert(amount, sourceCurrency, targetCurrency);
/*      */   }
/*      */ 
/*      */   public double calculateProfitPips(IOrder order)
/*      */     throws JFException
/*      */   {
/*  322 */     return calculateProfitPips(order.getOpenPrice(), orderBasePriceFormCalculations(order), order.isLong(), order.getInstrument().getPipValue());
/*      */   }
/*      */ 
/*      */   public double calculateProfitPips(double openPrice, double closePrice, boolean isLong, double pipValue)
/*      */   {
/*  336 */     double profitPips = (closePrice - openPrice) * (isLong ? 1 : -1);
/*  337 */     profitPips /= pipValue;
/*  338 */     return round(profitPips, 7);
/*      */   }
/*      */ 
/*      */   public double calculateProfitMoney(IOrder order) throws JFException {
/*  342 */     return calculateProfitMoney(order.getOpenPrice(), orderBasePriceFormCalculations(order), order.isLong(), order.getAmount());
/*      */   }
/*      */ 
/*      */   public double calculateProfitMoney(double openPrice, double closePrice, boolean isLong, double amountInMil)
/*      */   {
/*  347 */     double profit = (closePrice - openPrice) * (isLong ? 1 : -1);
/*  348 */     profit = profit * amountInMil * 1000000.0D;
/*  349 */     return round(profit, 7);
/*      */   }
/*      */ 
/*      */   private double orderBasePriceFormCalculations(IOrder order) throws JFException {
/*  353 */     double basePrice = 0.0D;
/*  354 */     if (order.getState() == IOrder.State.FILLED) {
/*  355 */       basePrice = this.converter.getLastMarketPrice(order.getInstrument(), order.isLong() ? OfferSide.BID : OfferSide.ASK);
/*  356 */       if (basePrice == 0.0D)
/*  357 */         throw new JFException(new StringBuilder().append("No price for ").append(order.getInstrument()).toString());
/*      */     }
/*  359 */     else if (order.getState() == IOrder.State.CLOSED) {
/*  360 */       basePrice = order.getClosePrice();
/*  361 */       if (basePrice == 0.0D)
/*  362 */         basePrice = (0.0D / 0.0D);
/*      */     }
/*      */     else {
/*  365 */       basePrice = (0.0D / 0.0D);
/*      */     }
/*  367 */     return basePrice;
/*      */   }
/*      */ 
/*      */   public double getCurrentExposure(Instrument instr)
/*      */     throws JFException
/*      */   {
/*  379 */     List list = this.engine.getOrders(instr);
/*  380 */     double rc = 0.0D;
/*  381 */     for (IOrder order : list) {
/*  382 */       if (order.getState() == IOrder.State.FILLED) {
/*  383 */         double amo = order.getAmount();
/*  384 */         if (order.isLong())
/*  385 */           rc += amo;
/*      */         else {
/*  387 */           rc -= amo;
/*      */         }
/*      */       }
/*      */     }
/*  391 */     return rc;
/*      */   }
/*      */ 
/*      */   public List<IOrder> getOrders(Instrument instrument, IOrder.State state, Boolean isLong, int magic)
/*      */     throws JFException
/*      */   {
/*  410 */     List rc = new ArrayList();
/*  411 */     if (instrument == null) {
/*  412 */       for (IOrder order : this.engine.getOrders()) {
/*  413 */         if (((state == null) || (state == order.getState())) && 
/*  414 */           ((magic <= 0) || (magic == getMagicNumber(order))) && (
/*  415 */           (isLong == null) || (isLong.booleanValue() == order.isLong()))) {
/*  416 */           rc.add(order);
/*      */         }
/*      */       }
/*      */     }
/*      */     else
/*      */     {
/*  422 */       for (IOrder order : this.engine.getOrders(instrument)) {
/*  423 */         if (((state == null) || (state == order.getState())) && 
/*  424 */           ((magic <= 0) || (magic == getMagicNumber(order))) && (
/*  425 */           (isLong == null) || (isLong.booleanValue() == order.isLong()))) {
/*  426 */           rc.add(order);
/*      */         }
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  432 */     return rc;
/*      */   }
/*      */ 
/*      */   public List<IOrder> getOrders(Instrument instrument, IOrder.State state, int magic) throws JFException {
/*  436 */     return getOrders(instrument, state, null, 0);
/*      */   }
/*      */ 
/*      */   public List<IOrder> getOrders(Instrument instrument, IOrder.State state) throws JFException {
/*  440 */     return getOrders(instrument, state, 0);
/*      */   }
/*      */ 
/*      */   private static String generateRandom()
/*      */   {
/*  453 */     String label = "";
/*  454 */     while (label.length() < 10) {
/*  455 */       label = new StringBuilder().append(label).append(Integer.toString(random.nextInt(100000000), 32)).toString();
/*      */     }
/*  457 */     label = label.substring(0, 9);
/*      */ 
/*  459 */     return label;
/*      */   }
/*      */ 
/*      */   public static void main(String[] args) throws JFException
/*      */   {
/*  464 */     JFToolBox box = new JFToolBox(null);
/*      */ 
/*  472 */     for (int i = 0; i < 10000; i++)
/*  473 */       System.out.println(generateRandom());
/*      */   }
/*      */ 
/*      */   public String generateLabel(int magic)
/*      */   {
/*  479 */     String label = new StringBuilder().append(this.MAGIC_PREFIX).append(magic).append(this.MAGIC_DELIM).append(generateRandom()).toString();
/*  480 */     return label;
/*      */   }
/*      */ 
/*      */   public int getMagicNumber(IOrder order) {
/*  484 */     int rc = 0;
/*  485 */     String label = order.getLabel();
/*  486 */     if ((label.startsWith(this.MAGIC_PREFIX)) && (label.indexOf(this.MAGIC_DELIM) != -1)) {
/*      */       try {
/*  488 */         label = label.substring(this.MAGIC_PREFIX.length(), label.indexOf(this.MAGIC_DELIM));
/*  489 */         rc = Integer.parseInt(label);
/*      */       } catch (Exception e) {
/*  491 */         System.out.println(new StringBuilder().append("LABEL=").append(label).toString());
/*  492 */         e.printStackTrace();
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  497 */     return rc;
/*      */   }
/*      */ 
/*      */   public List<IOrder> mergeAllInstrumentsOrdersSync(int magic, List<IOrder> positions)
/*      */     throws JFException
/*      */   {
/*  503 */     Map allByInstruments = new HashMap();
/*      */ 
/*  505 */     List rc = new ArrayList();
/*  506 */     if ((positions != null) && (positions.size() > 1))
/*      */     {
/*  508 */       for (IOrder iOrder : positions) {
/*  509 */         if (iOrder.getState() == IOrder.State.FILLED)
/*      */         {
/*  511 */           if (iOrder.getStopLossPrice() > 0.0D) {
/*  512 */             iOrder.setStopLossPrice(0.0D);
/*  513 */             iOrder.waitForUpdate(5000L);
/*      */           }
/*  515 */           if (iOrder.getTakeProfitPrice() > 0.0D) {
/*  516 */             iOrder.setTakeProfitPrice(0.0D);
/*  517 */             iOrder.waitForUpdate(5000L);
/*      */           }
/*      */ 
/*  520 */           Instrument instr = iOrder.getInstrument();
/*  521 */           List orders = (List)allByInstruments.get(instr);
/*  522 */           if (orders == null) {
/*  523 */             orders = new ArrayList();
/*  524 */             allByInstruments.put(instr, orders);
/*      */           }
/*      */ 
/*  527 */           if (iOrder.getState() == IOrder.State.FILLED) {
/*  528 */             orders.add(iOrder);
/*      */           }
/*      */         }
/*      */       }
/*  532 */       for (List orders : allByInstruments.values()) {
/*  533 */         if (orders.size() > 2) {
/*  534 */           IOrder iOrder = this.engine.mergeOrders(generateLabel(magic), (IOrder[])orders.toArray(new IOrder[0]));
/*  535 */           if (iOrder.getState() == IOrder.State.CREATED) {
/*  536 */             iOrder.waitForUpdate(5000L);
/*      */           }
/*  538 */           rc.add(iOrder);
/*  539 */         } else if (orders.size() == 1) {
/*  540 */           rc.add(orders.get(0));
/*      */         }
/*      */       }
/*      */     }
/*      */ 
/*  545 */     return rc;
/*      */   }
/*      */ 
/*      */   public IOrder mergeOrdersSync(String label, List<IOrder> positions) throws JFException
/*      */   {
/*  550 */     IOrder rc = null;
/*  551 */     if ((positions != null) && (positions.size() > 1)) {
/*  552 */       for (IOrder iOrder : positions) {
/*  553 */         if (iOrder.getState() == IOrder.State.FILLED)
/*      */         {
/*  555 */           if (iOrder.getStopLossPrice() > 0.0D) {
/*  556 */             iOrder.setStopLossPrice(0.0D);
/*  557 */             iOrder.waitForUpdate(5000L);
/*      */           }
/*  559 */           if (iOrder.getTakeProfitPrice() > 0.0D) {
/*  560 */             iOrder.setTakeProfitPrice(0.0D);
/*  561 */             iOrder.waitForUpdate(5000L);
/*      */           }
/*      */         }
/*      */       }
/*      */ 
/*  566 */       if (positions.size() >= 2) {
/*  567 */         IOrder iOrder = this.engine.mergeOrders(label, (IOrder[])positions.toArray(new IOrder[0]));
/*  568 */         if (iOrder.getState() == IOrder.State.CREATED) {
/*  569 */           iOrder.waitForUpdate(5000L);
/*      */         }
/*  571 */         rc = iOrder;
/*  572 */       } else if (positions.size() == 1) {
/*  573 */         rc = (IOrder)positions.get(0);
/*      */       }
/*      */     }
/*      */ 
/*  577 */     return rc;
/*      */   }
/*      */ 
/*      */   public void sleep(int milliseconds)
/*      */   {
/*  586 */     if (this.context.getEngine().getType() != IEngine.Type.TEST)
/*      */       try {
/*  588 */         Thread.sleep(milliseconds);
/*      */       }
/*      */       catch (Exception e) {
/*  591 */         e.printStackTrace();
/*      */       }
/*      */   }
/*      */ 
/*      */   public ISettings getSettings(String settingsName)
/*      */   {
/*  601 */     if ((settingsName == null) || (settingsName.length() == 0)) {
/*  602 */       settingsName = "global";
/*      */     }
/*  604 */     File lf = new File(new StringBuilder().append(getFilesDir()).append(File.separator).append(settingsName).toString());
/*  605 */     String lockFileName = lf.getAbsolutePath();
/*  606 */     HighAvailabilityStore store = (HighAvailabilityStore)this.highStores.get(lockFileName);
/*  607 */     if (store == null) {
/*  608 */       store = new HighAvailabilityStore(lockFileName);
/*      */     }
/*  610 */     this.highStores.put(settingsName, store);
/*  611 */     return store;
/*      */   }
/*      */ 
/*      */   public boolean playSound(String soundName)
/*      */   {
/*  620 */     String sound = new StringBuilder().append(getFilesDir()).append(File.separator).append(soundName).toString();
/*      */ 
/*  622 */     return this.audioPlayer.play(sound);
/*      */   }
/*      */ 
/*      */   private int turboPipe(InputStream inputStream, OutputStream outputStream)
/*      */   {
/*  628 */     if ((inputStream == null) || (outputStream == null)) {
/*  629 */       return 0;
/*      */     }
/*  631 */     byte[] buffer = new byte[4096];
/*      */ 
/*  633 */     int counter = 0;
/*      */     try {
/*      */       while (true) {
/*  636 */         int bytes_read = inputStream.read(buffer);
/*  637 */         if (bytes_read == -1) {
/*      */           break;
/*      */         }
/*  640 */         outputStream.write(buffer, 0, bytes_read);
/*  641 */         counter += bytes_read;
/*      */       }
/*      */     } catch (Exception e) {
/*  644 */       e.printStackTrace();
/*      */     } finally {
/*      */     }
/*  647 */     return counter;
/*      */   }
/*      */ 
/*      */   public Image loadImage(String path)
/*      */   {
/*  652 */     Image rc = null;
/*  653 */     rc = Toolkit.getDefaultToolkit().createImage(loadResource(path));
/*  654 */     return rc;
/*      */   }
/*      */ 
/*      */   public byte[] loadResource(String path)
/*      */   {
/*  659 */     byte[] rc = null;
/*      */     try {
/*  661 */       InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(path);
/*  662 */       ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();
/*  663 */       turboPipe(inputStream, arrayOutputStream);
/*  664 */       rc = arrayOutputStream.toByteArray();
/*  665 */       inputStream.close();
/*      */     } catch (Throwable e) {
/*  667 */       e.printStackTrace();
/*  668 */       System.out.println(e.getMessage());
/*      */     }
/*  670 */     return rc;
/*      */   }
/*      */ 
/*      */   public Icon loadIcon(String path)
/*      */   {
/*  675 */     Icon icon = new ImageIcon(loadImage(path));
/*  676 */     return icon;
/*      */   }
/*      */ 
/*      */   public IOrder submitOrderSync(Instrument instrument, IEngine.OrderCommand cmd, double lot, double price, double slippage, int sl, int tp, int magic) throws JFException {
/*  680 */     return submitOrderSync(instrument, cmd, lot, price, slippage, sl, tp, magic, null);
/*      */   }
/*      */ 
/*      */   public IOrder submitOrderSync(Instrument instrument, IEngine.OrderCommand cmd, double lot, double price, double slippage, int sl, int tp, int magic, String comment) throws JFException
/*      */   {
/*  685 */     double slPrice = 0.0D;
/*  686 */     double tpPrice = 0.0D;
/*      */ 
/*  688 */     if (price > 0.0D) {
/*  689 */       if (sl > 0) {
/*  690 */         slPrice = calculateStopLoss(instrument, cmd, price, sl);
/*      */       }
/*  692 */       if (tp > 0) {
/*  693 */         tpPrice = calculateTakeProfit(instrument, cmd, price, tp);
/*      */       }
/*      */     }
/*      */ 
/*  697 */     boolean isMarket = (cmd == IEngine.OrderCommand.BUY) || (cmd == IEngine.OrderCommand.SELL);
/*  698 */     IOrder order = this.engine.submitOrder(generateLabel(magic), instrument, cmd, lot, price, slippage, slPrice, tpPrice, 0L, comment);
/*  699 */     order.waitForUpdate(2000L);
/*  700 */     if (isMarket) {
/*  701 */       if ((order.getState() != IOrder.State.FILLED) && (order.getState() != IOrder.State.CLOSED) && (order.getState() != IOrder.State.CANCELED))
/*      */       {
/*  704 */         System.out.println(new StringBuilder().append("IBox waiting fill for [").append(order.getState()).append("]").toString());
/*  705 */         order.waitForUpdate(5000L);
/*      */       }
/*      */     }
/*  708 */     else if ((order.getState() != IOrder.State.OPENED) && (order.getState() != IOrder.State.FILLED) && (order.getState() != IOrder.State.CLOSED) && (order.getState() != IOrder.State.CANCELED))
/*      */     {
/*  711 */       System.out.println(new StringBuilder().append("IBox waiting open for [").append(order.getState()).append("]").toString());
/*  712 */       order.waitForUpdate(5000L);
/*      */     }
/*      */ 
/*  717 */     if ((order.getState() == IOrder.State.FILLED) && (price <= 0.0D)) {
/*  718 */       if (sl > 0) {
/*  719 */         slPrice = calculateStopLoss(instrument, cmd, order.getOpenPrice(), sl);
/*  720 */         order.setStopLossPrice(slPrice);
/*  721 */         order.waitForUpdate(5000L);
/*      */       }
/*  723 */       if (tp > 0) {
/*  724 */         tpPrice = calculateTakeProfit(instrument, cmd, order.getOpenPrice(), tp);
/*  725 */         order.setTakeProfitPrice(tpPrice);
/*  726 */         order.waitForUpdate(5000L);
/*      */       }
/*      */     }
/*      */ 
/*  730 */     return order;
/*      */   }
/*      */ 
/*      */   public double calculateStopLoss(Instrument currentInstrument, IEngine.OrderCommand cmd, double openPrice, int pips)
/*      */     throws JFException
/*      */   {
/*  736 */     double rc = 0.0D;
/*  737 */     boolean isLong = cmd.isLong();
/*      */ 
/*  739 */     if (openPrice <= 0.0D) {
/*  740 */       throw new JFException("Unable to calculate SL for 0 price");
/*      */     }
/*      */ 
/*  745 */     if (isLong)
/*  746 */       rc = openPrice - pips * currentInstrument.getPipValue();
/*      */     else {
/*  748 */       rc = openPrice + pips * currentInstrument.getPipValue();
/*      */     }
/*  750 */     rc = round(rc, 5);
/*      */ 
/*  752 */     return rc;
/*      */   }
/*      */ 
/*      */   public double calculateTakeProfit(Instrument currentInstrument, IEngine.OrderCommand cmd, double openPrice, int pips)
/*      */     throws JFException
/*      */   {
/*  758 */     double rc = 0.0D;
/*  759 */     boolean isLong = cmd.isLong();
/*      */ 
/*  761 */     if (openPrice <= 0.0D) {
/*  762 */       throw new JFException("Unable to calculate TP for 0 price");
/*      */     }
/*      */ 
/*  768 */     if (isLong)
/*  769 */       rc = openPrice + pips * currentInstrument.getPipValue();
/*      */     else {
/*  771 */       rc = openPrice - pips * currentInstrument.getPipValue();
/*      */     }
/*  773 */     rc = round(rc, 5);
/*      */ 
/*  775 */     return rc;
/*      */   }
/*      */ 
/*      */   public IOrder closeOrderSync(IOrder order) throws JFException {
/*  779 */     order.close();
/*  780 */     order.waitForUpdate(5000L);
/*  781 */     return order;
/*      */   }
/*      */ 
/*      */   public void closeAllSync() throws JFException {
/*  785 */     List positions = getOrders(null, IOrder.State.FILLED);
/*  786 */     List entrys = getOrders(null, IOrder.State.OPENED);
/*  787 */     closeAllSync(positions);
/*  788 */     closeAllSync(entrys);
/*      */   }
/*      */ 
/*      */   public void closeAllSync(Instrument instrument) throws JFException {
/*  792 */     List positions = getOrders(instrument, IOrder.State.FILLED);
/*  793 */     List entrys = getOrders(instrument, IOrder.State.OPENED);
/*  794 */     closeAllSync(positions);
/*  795 */     closeAllSync(entrys);
/*      */   }
/*      */ 
/*      */   public void closeAllSync(Instrument instrument, int magic) throws JFException {
/*  799 */     List positions = getOrders(instrument, IOrder.State.FILLED, magic);
/*  800 */     List entrys = getOrders(instrument, IOrder.State.OPENED, magic);
/*  801 */     closeAllSync(positions);
/*  802 */     closeAllSync(entrys);
/*      */   }
/*      */ 
/*      */   public void closeAllSync(List<IOrder> orders) throws JFException {
/*  806 */     for (IOrder order : orders)
/*      */       try {
/*  808 */         if ((order.getState() == IOrder.State.OPENED) || (order.getState() == IOrder.State.FILLED))
/*  809 */           closeOrderSync(order);
/*      */       }
/*      */       catch (JFException e) {
/*  812 */         e.printStackTrace();
/*      */       }
/*      */   }
/*      */ 
/*      */   public long getLastTime()
/*      */   {
/*  820 */     long rc = 0L;
/*      */     try
/*      */     {
/*  823 */       rc = this.history.getLastTick(this.lastSureSignedInstrument).getTime();
/*      */     }
/*      */     catch (JFException e)
/*      */     {
/*      */     }
/*  828 */     if (rc == 0L) {
/*  829 */       for (Instrument instrument : Instrument.values()) {
/*      */         try {
/*  831 */           rc = this.history.getLastTick(instrument).getTime();
/*  832 */           this.lastSureSignedInstrument = instrument;
/*      */         }
/*      */         catch (JFException e)
/*      */         {
/*      */         }
/*      */       }
/*      */     }
/*  839 */     return rc;
/*      */   }
/*      */ 
/*      */   public boolean evaluation(boolean onlyDemo, String endDate)
/*      */   {
/*  848 */     if (this.evaluationChecked) {
/*  849 */       return true;
/*      */     }
/*      */ 
/*  852 */     if (this.context.getEngine().getType() != IEngine.Type.TEST)
/*      */     {
/*  854 */       if (onlyDemo) {
/*  855 */         print(new Object[] { "Service evaluation. Working only in DEMO mode." });
/*  856 */         if (this.context.getEngine().getType() == IEngine.Type.LIVE) {
/*  857 */           print(new Object[] { new StringBuilder().append(getClass().getSimpleName()).append(" stopped.").toString() });
/*  858 */           this.context.stop();
/*  859 */           return false;
/*      */         }
/*      */       }
/*  862 */       if (endDate != null) {
/*  863 */         print(new Object[] { new StringBuilder().append("Service evaluation. Working only until ").append(endDate).toString() });
/*      */         try {
/*  865 */           long evtime = this.evaluationDateFormat.parse(endDate).getTime();
/*      */ 
/*  867 */           if (getLastTime() > evtime) {
/*  868 */             print(new Object[] { new StringBuilder().append(getClass().getSimpleName()).append(" expired.").toString() });
/*  869 */             this.context.stop();
/*  870 */             return false;
/*      */           }
/*      */         } catch (ParseException e) {
/*      */         }
/*      */       }
/*      */     }
/*  876 */     print(new Object[] { "Evaluation version. Proceed." });
/*  877 */     this.evaluationChecked = true;
/*  878 */     return true;
/*      */   }
/*      */ 
/*      */   public boolean isBeforeTime(long nowTime, String timeCompareTo)
/*      */     throws JFException
/*      */   {
/*  884 */     boolean rc = false;
/*  885 */     Matcher matcher = this.timeePattern.matcher(timeCompareTo);
/*  886 */     long lTo = 0L;
/*  887 */     if (matcher.find()) {
/*  888 */       long hrsTo = Long.parseLong(matcher.group(1));
/*  889 */       long minTo = Long.parseLong(matcher.group(2));
/*  890 */       lTo = hrsTo * 3600000L + minTo * 60000L;
/*      */     } else {
/*  892 */       throw new JFException("TimeRange format incorrect");
/*      */     }
/*  894 */     long nowIntraDay = nowTime % 86400000L;
/*  895 */     if (nowIntraDay < lTo) {
/*  896 */       rc = true;
/*      */     }
/*  898 */     return rc;
/*      */   }
/*      */ 
/*      */   public boolean isInTimeRange(long nowTime, String timeRange)
/*      */     throws JFException
/*      */   {
/*  906 */     if ("-".equals(timeRange)) {
/*  907 */       return true;
/*      */     }
/*      */ 
/*  910 */     Matcher matcher = this.timeRangePattern.matcher(timeRange);
/*      */ 
/*  912 */     long lFrom = 0L;
/*  913 */     long lTo = 0L;
/*      */ 
/*  915 */     if (matcher.find()) {
/*  916 */       long hrsFrom = Long.parseLong(matcher.group(1));
/*  917 */       long minFrom = Long.parseLong(matcher.group(2));
/*      */ 
/*  919 */       long hrsTo = Long.parseLong(matcher.group(3));
/*  920 */       long minTo = Long.parseLong(matcher.group(4));
/*      */ 
/*  922 */       lFrom = hrsFrom * 3600000L + minFrom * 60000L;
/*  923 */       lTo = hrsTo * 3600000L + minTo * 60000L;
/*      */     }
/*      */     else {
/*  926 */       throw new JFException("TimeRange format incorrect");
/*      */     }
/*      */ 
/*  929 */     if (lFrom >= lTo) {
/*  930 */       throw new JFException("TimeRange incorrect");
/*      */     }
/*      */ 
/*  935 */     boolean rc = false;
/*      */ 
/*  937 */     long nowIntraDay = nowTime % 86400000L;
/*      */ 
/*  939 */     if ((lFrom < lTo) && 
/*  940 */       (nowIntraDay >= lFrom) && (nowIntraDay <= lTo)) {
/*  941 */       rc = true;
/*      */     }
/*      */ 
/*  945 */     return rc;
/*      */   }
/*      */ 
/*      */   public IBar[] getNBars(Instrument symbol, Period convertedPeriod, OfferSide offerSide, int start, int count) throws JFException
/*      */   {
/*  950 */     IBar[] bars = new IBar[count];
/*  951 */     for (int i = 0; i < count; i++) {
/*  952 */       IBar bar = this.history.getBar(symbol, convertedPeriod, offerSide, i + start);
/*  953 */       bars[i] = bar;
/*      */     }
/*  955 */     return bars;
/*      */   }
/*      */ 
/*      */   public double weightedPrice(List<IOrder> orders) throws JFException
/*      */   {
/*  960 */     double rc = 0.0D;
/*  961 */     double amoPrimarySum = 0.0D;
/*  962 */     double amoSecondarySum = 0.0D;
/*  963 */     Instrument instrument = null;
/*  964 */     for (IOrder iOrder : orders) {
/*  965 */       if (iOrder.getOpenPrice() <= 0.0D) {
/*      */         continue;
/*      */       }
/*  968 */       if (instrument == null) {
/*  969 */         instrument = iOrder.getInstrument();
/*      */       }
/*  971 */       else if (instrument != iOrder.getInstrument()) {
/*  972 */         throw new JFException("weightedPrice can not be calculated for different instruments.");
/*      */       }
/*      */ 
/*  976 */       double amoPrimary = iOrder.getAmount();
/*  977 */       double amoSecondary = -amoPrimary * iOrder.getOpenPrice();
/*  978 */       amoPrimarySum += amoPrimary;
/*  979 */       amoSecondarySum += amoSecondary;
/*      */     }
/*  981 */     if (amoSecondarySum != 0.0D) {
/*  982 */       rc = Math.abs(amoSecondarySum / amoPrimarySum);
/*  983 */       rc = round(rc, 7);
/*      */     }
/*  985 */     return rc;
/*      */   }
/*      */ 
/*      */   public double getExposureByInstrument(Instrument instrument) throws JFException
/*      */   {
/*  990 */     return getExposureByInstrument(instrument, this.engine.getOrders());
/*      */   }
/*      */ 
/*      */   public double getExposureByInstrument(Instrument instrument, List<IOrder> orders)
/*      */   {
/*  995 */     double rc = 0.0D;
/*  996 */     for (IOrder iOrder : orders) {
/*  997 */       if ((iOrder.getInstrument() == instrument) && (iOrder.getState() == IOrder.State.FILLED)) {
/*  998 */         rc += iOrder.getAmount();
/*      */       }
/*      */     }
/* 1001 */     rc = round(rc, 2);
/* 1002 */     return rc;
/*      */   }
/*      */ 
/*      */   public double getExposureByCurrency(Currency currency) throws JFException
/*      */   {
/* 1007 */     return getExposureByCurrency(currency, this.engine.getOrders());
/*      */   }
/*      */ 
/*      */   public double getExposureByCurrency(Currency currency, List<IOrder> orders)
/*      */   {
/* 1012 */     double rc = 0.0D;
/* 1013 */     for (IOrder order : orders) {
/* 1014 */       if (order.getState() == IOrder.State.FILLED) {
/* 1015 */         if (order.getInstrument().getPrimaryCurrency() == currency) {
/* 1016 */           double primaryAmount = order.getAmount();
/* 1017 */           if (order.isLong())
/* 1018 */             rc += primaryAmount;
/*      */           else {
/* 1020 */             rc -= primaryAmount;
/*      */           }
/*      */         }
/* 1023 */         if (order.getInstrument().getSecondaryCurrency() == currency) {
/* 1024 */           double secondaryAmount = order.getAmount() * order.getOpenPrice();
/* 1025 */           if (order.isLong())
/* 1026 */             rc -= secondaryAmount;
/*      */           else {
/* 1028 */             rc += secondaryAmount;
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/* 1033 */     return rc;
/*      */   }
/*      */ 
/*      */   public Map<Currency, Double> getExposureByCurrency() throws JFException
/*      */   {
/* 1038 */     Map rc = new HashMap();
/* 1039 */     for (IOrder order : this.engine.getOrders()) {
/* 1040 */       if (order.getState() == IOrder.State.FILLED)
/*      */       {
/* 1042 */         Currency primaryCurrency = order.getInstrument().getPrimaryCurrency();
/* 1043 */         double primaryAmount = order.getAmount();
/* 1044 */         if (!order.isLong()) {
/* 1045 */           primaryAmount = -primaryAmount;
/*      */         }
/*      */ 
/* 1048 */         if (rc.containsKey(primaryCurrency)) {
/* 1049 */           double exposure = ((Double)rc.get(primaryCurrency)).doubleValue();
/* 1050 */           rc.put(primaryCurrency, Double.valueOf(exposure + primaryAmount));
/*      */         } else {
/* 1052 */           rc.put(primaryCurrency, Double.valueOf(primaryAmount));
/*      */         }
/*      */ 
/* 1055 */         Currency secondaryCurrency = order.getInstrument().getSecondaryCurrency();
/* 1056 */         double secondaryAmount = order.getAmount() * order.getOpenPrice();
/* 1057 */         if (order.isLong()) {
/* 1058 */           secondaryAmount = -secondaryAmount;
/*      */         }
/*      */ 
/* 1061 */         if (rc.containsKey(secondaryCurrency)) {
/* 1062 */           double exposure = ((Double)rc.get(secondaryCurrency)).doubleValue();
/* 1063 */           rc.put(secondaryCurrency, Double.valueOf(exposure + secondaryAmount));
/*      */         } else {
/* 1065 */           rc.put(secondaryCurrency, Double.valueOf(secondaryAmount));
/*      */         }
/*      */       }
/*      */     }
/*      */ 
/* 1070 */     return rc;
/*      */   }
/*      */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Connector-1.1.49.jar
 * Qualified Name:     com.dukascopy.dds2.greed.connector.impl.JFToolBox
 * JD-Core Version:    0.6.0
 */