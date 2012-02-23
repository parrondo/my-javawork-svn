/*      */ package com.dukascopy.api;
/*      */ 
/*      */ import com.dukascopy.api.connector.AbstractConnectorImpl;
/*      */ import com.dukascopy.api.connector.IBox;
/*      */ import com.dukascopy.api.connector.IColor;
/*      */ import com.dukascopy.api.connector.IConnector;
/*      */ import com.dukascopy.api.connector.IConnectorManager;
/*      */ import com.dukascopy.api.connector.IConst;
/*      */ import com.dukascopy.api.connector.ISettings;
/*      */ import com.dukascopy.api.connector.helpers.RangeHelper;
/*      */ import com.dukascopy.api.impl.History;
/*      */ import com.dukascopy.api.impl.Indicators;
/*      */ import com.dukascopy.api.impl.connect.JForexAPI;
/*      */ import com.dukascopy.api.impl.connect.PlatformAccountImpl;
/*      */ import com.dukascopy.api.indicators.IIndicator;
/*      */ import com.dukascopy.api.indicators.IIndicatorContext;
/*      */ import com.dukascopy.api.indicators.IndicatorInfo;
/*      */ import com.dukascopy.api.indicators.OptInputParameterInfo;
/*      */ import com.dukascopy.api.system.Commissions;
/*      */ import com.dukascopy.api.system.Overnights;
/*      */ import com.dukascopy.charts.data.CandlesDataSequenceProvider;
/*      */ import com.dukascopy.charts.data.datacache.DataCacheException;
/*      */ import com.dukascopy.charts.data.datacache.DataCacheUtils;
/*      */ import com.dukascopy.charts.data.datacache.FeedDataProvider;
/*      */ import com.dukascopy.charts.data.datacache.OrderHistoricalData;
/*      */ import com.dukascopy.charts.data.datacache.OrderHistoricalData.CloseData;
/*      */ import com.dukascopy.charts.data.orders.OrdersProvider;
/*      */ import com.dukascopy.charts.main.interfaces.DDSChartsController;
/*      */ import com.dukascopy.charts.main.nulls.NullIChart;
/*      */ import com.dukascopy.charts.mappers.time.GeometryCalculator;
/*      */ import com.dukascopy.charts.math.indicators.IndicatorsProvider;
/*      */ import com.dukascopy.dds2.greed.GreedContext;
/*      */ import com.dukascopy.dds2.greed.agent.strategy.tester.TesterAccount;
/*      */ import com.dukascopy.dds2.greed.connection.GreedTransportClient;
/*      */ import com.dukascopy.dds2.greed.connector.ConnectorManager;
/*      */ import com.dukascopy.dds2.greed.connector.MQLAlertDialog;
/*      */ import com.dukascopy.dds2.greed.connector.helpers.CommonHelpers;
/*      */ import com.dukascopy.dds2.greed.connector.helpers.FileHelpers;
/*      */ import com.dukascopy.dds2.greed.connector.helpers.MathHelpers;
/*      */ import com.dukascopy.dds2.greed.connector.impl.HighAvailabilityStore;
/*      */ import com.dukascopy.dds2.greed.connector.impl.JFToolBox;
/*      */ import com.dukascopy.dds2.greed.gui.ClientForm;
/*      */ import com.dukascopy.dds2.greed.gui.window.WindowManager;
/*      */ import com.dukascopy.dds2.greed.model.AccountStatement;
/*      */ import com.dukascopy.dds2.greed.model.MarketView;
/*      */ import com.dukascopy.dds2.greed.mt.exceptions.MTAgentException;
/*      */ import com.dukascopy.dds2.greed.mt.helpers.MTAPIHelpers;
/*      */ import com.dukascopy.dds2.greed.util.CurrencyConverter;
/*      */ import com.dukascopy.dds2.greed.util.INotificationUtils;
/*      */ import com.dukascopy.dds2.greed.util.NotificationUtilsProvider;
/*      */ import com.dukascopy.dds2.greed.util.OrderMessageUtils;
/*      */ import com.dukascopy.transport.common.model.type.Money;
/*      */ import com.dukascopy.transport.common.model.type.Position;
/*      */ import com.dukascopy.transport.common.msg.ProtocolMessage;
/*      */ import com.dukascopy.transport.common.msg.group.OrderGroupMessage;
/*      */ import com.dukascopy.transport.common.msg.group.OrderMessage;
/*      */ import com.dukascopy.transport.common.msg.request.AccountInfoMessage;
/*      */ import com.dukascopy.transport.common.msg.request.MergePositionsMessage;
/*      */ import com.dukascopy.transport.common.msg.response.ErrorResponseMessage;
/*      */ import com.dukascopy.transport.common.msg.response.InstrumentStatusUpdateMessage;
/*      */ import java.awt.Color;
/*      */ import java.io.File;
/*      */ import java.io.FileOutputStream;
/*      */ import java.io.IOException;
/*      */ import java.io.OutputStreamWriter;
/*      */ import java.io.PrintStream;
/*      */ import java.io.RandomAccessFile;
/*      */ import java.io.Writer;
/*      */ import java.lang.reflect.Array;
/*      */ import java.lang.reflect.Field;
/*      */ import java.math.BigDecimal;
/*      */ import java.nio.channels.FileChannel;
/*      */ import java.text.DecimalFormat;
/*      */ import java.text.SimpleDateFormat;
/*      */ import java.util.BitSet;
/*      */ import java.util.Calendar;
/*      */ import java.util.Currency;
/*      */ import java.util.Date;
/*      */ import java.util.HashMap;
/*      */ import java.util.HashSet;
/*      */ import java.util.List;
/*      */ import java.util.Map;
/*      */ import java.util.Set;
/*      */ import javax.swing.JDialog;
/*      */ import javax.swing.JFrame;
/*      */ import javax.swing.JOptionPane;
/*      */ import org.slf4j.Logger;
/*      */ import org.slf4j.LoggerFactory;
/*      */ 
/*      */ public class MQLConnector
/*      */   implements IConnector, IConst, IColor
/*      */ {
/*  101 */   private static final Logger LOGGER = LoggerFactory.getLogger(MQLConnector.class);
/*      */ 
/*  103 */   String csvDelimiter = ";";
/*      */ 
/*  105 */   ConnectorStrategy strategy = null;
/*  106 */   ConnectorIndicator indicator = null;
/*  107 */   AccountInfoMessage accountInfo = null;
/*  108 */   IIndicators indicators = null;
/*  109 */   IConnector instance = null;
/*  110 */   IConnectorManager connectorManager = null;
/*  111 */   protected IHistory history = null;
/*  112 */   CandlesDataSequenceProvider candlesDataSequenceProvider = null;
/*      */ 
/*  115 */   private int chartPanelId = -1;
/*  116 */   private int lastError = 0;
/*      */ 
/*  492 */   private Map<Integer, Character> filesDelimiters = new HashMap();
/*  493 */   private Map<Integer, RandomAccessFile> filesMap = new HashMap();
/*  494 */   private MQLAlertDialog alertDialog = null;
/*      */ 
/* 1027 */   static DecimalFormat dfPrint = new DecimalFormat("#,##0.##########");
/*      */ 
/* 2410 */   private IOrder selectedOrder = null;
/* 2411 */   private OrderMessage selectedOrderMessage = null;
/* 2412 */   private OrderGroupMessage selectedGroup = null;
/*      */ 
/*      */   public double getAsk()
/*      */     throws JFException
/*      */   {
/*  120 */     return getAbstractConnector().getAsk();
/*      */   }
/*      */ 
/*      */   public double getBid() throws JFException {
/*  124 */     return getAbstractConnector().getBid();
/*      */   }
/*      */ 
/*      */   public IConnectorManager getConnectorManager()
/*      */   {
/*  129 */     if (this.connectorManager == null) {
/*  130 */       this.connectorManager = ConnectorManager.getInstance();
/*      */     }
/*  132 */     return this.connectorManager;
/*      */   }
/*      */ 
/*      */   public void setStrategy(IStrategy strategy)
/*      */   {
/*  137 */     this.strategy = ((ConnectorStrategy)strategy);
/*      */   }
/*      */ 
/*      */   public void setIndicator(IIndicator indicator)
/*      */   {
/*  142 */     this.indicator = ((ConnectorIndicator)indicator);
/*      */   }
/*      */ 
/*      */   public ConnectorStrategy getStrategy() {
/*  146 */     if (this.strategy == null) {
/*  147 */       CommonHelpers.notNullStrategy();
/*      */     }
/*  149 */     return this.strategy;
/*      */   }
/*      */ 
/*      */   public ConnectorIndicator getIndicator() {
/*  153 */     if (this.indicator == null) {
/*  154 */       CommonHelpers.notNullStrategy();
/*      */     }
/*  156 */     return this.indicator;
/*      */   }
/*      */ 
/*      */   public AbstractConnectorImpl getAbstractConnector() {
/*  160 */     if (this.strategy != null)
/*  161 */       return this.strategy;
/*  162 */     if (this.indicator != null) {
/*  163 */       return this.indicator;
/*      */     }
/*  165 */     CommonHelpers.notNullStrategy();
/*      */ 
/*  167 */     return null;
/*      */   }
/*      */ 
/*      */   public IConnector getInstance()
/*      */   {
/*  172 */     return this;
/*      */   }
/*      */ 
/*      */   private Instrument getCurrentInstrument(Instrument instrument)
/*      */   {
/*      */     Instrument result;
/*      */     Instrument result;
/*  179 */     if (this.strategy != null)
/*      */     {
/*      */       Instrument result;
/*  180 */       if (instrument == null)
/*  181 */         result = this.strategy.currentInstrument;
/*      */       else
/*  183 */         result = instrument;
/*      */     }
/*      */     else {
/*  186 */       result = getChartSelectedInstrument();
/*      */     }
/*      */ 
/*  189 */     return result;
/*      */   }
/*      */ 
/*      */   protected IBox getIBox() {
/*  193 */     return getAbstractConnector().getBox();
/*      */   }
/*      */ 
/*      */   public IChart getChart() {
/*  197 */     return getIChart();
/*      */   }
/*      */ 
/*      */   protected IChart getIChart() {
/*  201 */     IChart result = null;
/*      */ 
/*  206 */     result = getChartsController().getIChartBy(Integer.valueOf(getChartPanelId()));
/*  207 */     return result;
/*      */   }
/*      */ 
/*      */   protected IChart getIChart(Instrument instrument) {
/*  211 */     IChart result = new NullIChart();
/*  212 */     if (this.strategy != null)
/*  213 */       result = getStrategy().getChart(getCurrentInstrument(instrument));
/*      */     else {
/*  215 */       result = getChartsController().getIChartBy(Integer.valueOf(getChartPanelId()));
/*      */     }
/*  217 */     return result;
/*      */   }
/*      */ 
/*      */   public IEngine getIEngine()
/*      */   {
/*  222 */     if (this.strategy != null) {
/*  223 */       return getStrategy().engine;
/*      */     }
/*  225 */     throw new RuntimeException("Try call getIEngine() from Indicator");
/*      */   }
/*      */ 
/*      */   public IHistory getIHistory()
/*      */   {
/*  230 */     if (this.history == null) {
/*  231 */       this.history = getHistoryProvider();
/*      */     }
/*  233 */     return this.history;
/*      */   }
/*      */ 
/*      */   public void setIHistory(IHistory history) {
/*  237 */     this.history = history;
/*      */   }
/*      */ 
/*      */   protected IIndicators getIndicators() {
/*  241 */     if (this.indicators == null) {
/*  242 */       if (this.strategy != null)
/*  243 */         this.indicators = getStrategy().indicators;
/*      */       else {
/*  245 */         this.indicators = new Indicators((History)getIHistory());
/*      */       }
/*      */     }
/*  248 */     return this.indicators;
/*      */   }
/*      */ 
/*      */   protected IndicatorsProvider getIndicatorsProvider() {
/*  252 */     return IndicatorsProvider.getInstance();
/*      */   }
/*      */ 
/*      */   protected IContext getContext() {
/*  256 */     return getStrategy().context;
/*      */   }
/*      */ 
/*      */   protected IIndicatorContext getIIndicatorContext() {
/*  260 */     return getIndicator().getContext();
/*      */   }
/*      */ 
/*      */   protected IConsole getIConsole() {
/*  264 */     if (this.strategy != null) {
/*  265 */       return getContext().getConsole();
/*      */     }
/*  267 */     return getIIndicatorContext().getConsole();
/*      */   }
/*      */ 
/*      */   private void stopStrategy()
/*      */   {
/*  272 */     if (this.strategy != null)
/*  273 */       getContext().stop();
/*      */   }
/*      */ 
/*      */   private IBar getBar(Instrument symbol, int timeframe, OfferSide offerSide, int shift)
/*      */     throws JFException
/*      */   {
/*  279 */     IBar bar = null;
/*  280 */     if (this.strategy != null) {
/*  281 */       if (shift < 0) {
/*  282 */         getIConsole().getErr().println(new StringBuilder().append("Parameter 'shift' is < 0 [").append(shift).append("]").toString());
/*  283 */         shift = 0;
/*      */       }
/*  285 */       bar = getIHistory().getBar(getCurrentInstrument(symbol), selectPeriod(timeframe), offerSide, shift);
/*      */ 
/*  287 */       if (bar == null) {
/*  288 */         Print(new Object[] { new StringBuilder().append("Instrument ").append(symbol).append(" not subscribed.").toString() });
/*      */       }
/*      */ 
/*      */     }
/*      */     else
/*      */     {
/*  297 */       bar = this.indicator.Bar(Integer.valueOf(shift));
/*      */     }
/*  299 */     return bar;
/*      */   }
/*      */ 
/*      */   private IBar[] getBars(Instrument symbol, int timeframe, OfferSide offerSide, int start, int count) throws JFException
/*      */   {
/*  304 */     IBar[] bars = null;
/*  305 */     if (this.strategy != null) {
/*  306 */       bars = getIBox().getNBars(getCurrentInstrument(symbol), selectPeriod(timeframe), offerSide, start, count);
/*      */     }
/*      */     else {
/*  309 */       bars = this.indicator.Bars(Integer.valueOf(start), Integer.valueOf(count));
/*      */     }
/*  311 */     return bars;
/*      */   }
/*      */ 
/*      */   private long getBarTime(Instrument symbol, int timeframe, OfferSide offerSide, int shift) throws JFException
/*      */   {
/*  316 */     long time = 0L;
/*  317 */     long start = System.currentTimeMillis();
/*  318 */     IBar bar = getBar(symbol, timeframe, offerSide, shift);
/*  319 */     long end = System.currentTimeMillis();
/*      */ 
/*  322 */     if (bar == null) {
/*  323 */       time = getIHistory().getLastTick(getCurrentInstrument(symbol)).getTime();
/*      */     }
/*      */     else {
/*  326 */       time = bar.getTime();
/*      */     }
/*  328 */     return time;
/*      */   }
/*      */ 
/*      */   protected ClientForm getClientForm()
/*      */   {
/*  336 */     return (ClientForm)GreedContext.get("clientGui");
/*      */   }
/*      */ 
/*      */   protected MarketView getMarketView()
/*      */   {
/*  343 */     return (MarketView)GreedContext.get("marketView");
/*      */   }
/*      */ 
/*      */   public CandlesDataSequenceProvider getCandlesDataSequenceProvider()
/*      */     throws JFException
/*      */   {
/*  349 */     if (this.candlesDataSequenceProvider == null) {
/*  350 */       this.candlesDataSequenceProvider = new CandlesDataSequenceProvider(Instrument(), Period.TICK == getPeriod() ? Period.TEN_SECS : getPeriod(), getOfferSide(), 0L, 2000, FeedDataProvider.getDefaultInstance());
/*      */     }
/*      */ 
/*  359 */     return this.candlesDataSequenceProvider;
/*      */   }
/*      */ 
/*      */   public int getBars() {
/*  363 */     int bars = -1;
/*  364 */     GeometryCalculator geometryCalculator = new GeometryCalculator();
/*      */ 
/*  367 */     geometryCalculator.paneWidthChanged(getUserInterface().getMainFrame().getWidth());
/*      */ 
/*  369 */     int geomCandlesCount = geometryCalculator.getDataUnitsCount();
/*      */ 
/*  372 */     int recalculatedCandlesCount = geometryCalculator.recalculate(20);
/*      */ 
/*  375 */     int flats = -1;
/*      */     try {
/*  377 */       flats = DataCacheUtils.getCandlesCountBetween(getPeriod(), getFeedDataProvider().getFirstTickLocalTime() / 1000000L * 1000000L, getFeedDataProvider().getLastTickTime(Instrument())) - 1;
/*      */     }
/*      */     catch (DataCacheException e)
/*      */     {
/*  385 */       e.printStackTrace();
/*  386 */       bars = recalculatedCandlesCount;
/*      */     }
/*      */     catch (JFException e) {
/*  389 */       e.printStackTrace();
/*      */     }
/*      */ 
/*  392 */     return bars;
/*      */   }
/*      */ 
/*      */   protected History getHistoryProvider() {
/*  396 */     if (GreedContext.getConfig("AccountInfoMessage") != null) {
/*  397 */       return new History(OrdersProvider.getInstance(), ((AccountInfoMessage)GreedContext.getConfig("AccountInfoMessage")).getCurrency());
/*      */     }
/*  399 */     return null;
/*      */   }
/*      */ 
/*      */   public DDSChartsController getChartsController() {
/*  403 */     return (DDSChartsController)GreedContext.get("chartsController");
/*      */   }
/*      */ 
/*      */   protected OrdersProvider getOrdersProvider()
/*      */   {
/*  408 */     return (OrdersProvider)GreedContext.get("ordersDataProvider");
/*      */   }
/*      */ 
/*      */   public Period getChartSelectedPeriod()
/*      */   {
/*  413 */     Period defaultPeriod = getAbstractConnector().getCurrentPeriod();
/*  414 */     IChart chart = null;
/*      */ 
/*  416 */     if ((getAbstractConnector().getCurrentChart() != null) && (!(getAbstractConnector().getCurrentChart() instanceof NullIChart)))
/*      */     {
/*  418 */       chart = getAbstractConnector().getCurrentChart();
/*      */     }
/*      */ 
/*  421 */     if ((chart == null) && (getChartsController() != null) && (this.chartPanelId > 0)) {
/*  422 */       chart = getChartsController().getIChartBy(Integer.valueOf(getChartPanelId()));
/*  423 */       if (chart != null) {
/*  424 */         defaultPeriod = chart.getSelectedPeriod();
/*      */       }
/*      */     }
/*  427 */     return defaultPeriod;
/*      */   }
/*      */ 
/*      */   public Instrument getChartSelectedInstrument() {
/*  431 */     Instrument instrument = getAbstractConnector().getCurrentInstrument();
/*  432 */     IChart chart = null;
/*  433 */     if ((getAbstractConnector().getCurrentChart() != null) && (!(getAbstractConnector().getCurrentChart() instanceof NullIChart)))
/*      */     {
/*  435 */       chart = getAbstractConnector().getCurrentChart();
/*      */     }
/*  437 */     if ((chart == null) && (getChartsController() != null) && (this.chartPanelId > 0)) {
/*  438 */       chart = getChartsController().getIChartBy(Integer.valueOf(getChartPanelId()));
/*  439 */       if (chart != null) {
/*  440 */         instrument = chart.getInstrument();
/*      */       }
/*      */     }
/*  443 */     return instrument;
/*      */   }
/*      */ 
/*      */   protected WindowManager getWindowManager() {
/*  447 */     return (WindowManager)GreedContext.get("windowManager");
/*      */   }
/*      */ 
/*      */   protected IUserInterface getUserInterface() {
/*  451 */     return (IUserInterface)GreedContext.get("iUserInterface");
/*      */   }
/*      */ 
/*      */   protected CurrencyConverter getCurrencyConverter() {
/*  455 */     return (CurrencyConverter)GreedContext.get("currencyConverter");
/*      */   }
/*      */ 
/*      */   protected AccountStatement getAccountStatement()
/*      */   {
/*  460 */     return (AccountStatement)GreedContext.get("accountStatement");
/*      */   }
/*      */ 
/*      */   protected AccountInfoMessage getAccountInfoMessage() throws JFException
/*      */   {
/*  465 */     return getAccountStatement().getLastAccountState();
/*      */   }
/*      */ 
/*      */   protected FeedDataProvider getFeedDataProvider() throws JFException {
/*  469 */     return (FeedDataProvider)GreedContext.get("feedDataProvider");
/*      */   }
/*      */ 
/*      */   protected IAccount getIAccount() throws JFException
/*      */   {
/*  474 */     if (getAbstractConnector().getIAccount() == null) {
/*  475 */       Print(new Object[] { "Account not selected" });
/*  476 */       IAccount account = null;
/*  477 */       if (getIEngine().getType() != IEngine.Type.TEST)
/*  478 */         account = new PlatformAccountImpl(getAccountInfoMessage());
/*      */       else {
/*  480 */         account = new TesterAccount(getAccountInfoMessage().getCurrency(), getAccountInfoMessage().getEquity().getValue().doubleValue(), getAccountInfoMessage().getLeverage().intValue(), getAccountInfoMessage().getMcLeverageUse().intValue(), getAccountInfoMessage().getMCEquityLimit().getValue().doubleValue(), new Commissions(false), new Overnights(), getAccountInfoMessage().getAcountLoginId());
/*      */       }
/*      */ 
/*  485 */       return account;
/*      */     }
/*      */ 
/*  489 */     return getAbstractConnector().getIAccount();
/*      */   }
/*      */ 
/*      */   public void onDeinit()
/*      */     throws JFException
/*      */   {
/*  498 */     for (RandomAccessFile accessFile : this.filesMap.values()) {
/*  499 */       if (accessFile != null) {
/*      */         try {
/*  501 */           accessFile.close();
/*      */         }
/*      */         catch (Exception e) {
/*  504 */           this.lastError = 2;
/*  505 */           throw new JFException(e);
/*      */         }
/*      */       }
/*      */     }
/*      */ 
/*  510 */     this.filesMap.clear();
/*  511 */     this.filesDelimiters.clear();
/*      */   }
/*      */ 
/*      */   public void onInit(IContext context) throws JFException
/*      */   {
/*  516 */     this.csvDelimiter = System.getProperty("csv.delimiter", ";");
/*  517 */     getStrategy().setBox(new JFToolBox(context));
/*      */ 
/*  520 */     this.lastError = 0;
/*      */   }
/*      */ 
/*      */   public void onInit(IIndicatorContext context)
/*      */     throws JFException
/*      */   {
/*  531 */     this.csvDelimiter = System.getProperty("csv.delimiter", ";");
/*  532 */     if (getIHistory() != null) {
/*  533 */       getIndicator().setBox(new JFToolBox(context, null, getIHistory()));
/*      */     }
/*      */ 
/*  536 */     this.lastError = 0;
/*      */   }
/*      */ 
/*      */   public void onAccount(IAccount account)
/*      */     throws JFException
/*      */   {
/*  547 */     getIAccount();
/*  548 */     this.csvDelimiter = System.getProperty("csv.delimiter", ";");
/*      */   }
/*      */ 
/*      */   public double iClose(Instrument symbol, int timeframe, int shift, OfferSide offerSide)
/*      */     throws JFException
/*      */   {
/*  562 */     double rc = 0.0D;
/*  563 */     IBar bar = null;
/*      */     try {
/*  565 */       bar = getBar(symbol, timeframe, OfferSide.BID, shift);
/*      */     }
/*      */     catch (JFException e) {
/*  568 */       this.lastError = 2;
/*  569 */       stopStrategy();
/*  570 */       throw e;
/*      */     }
/*  572 */     if (bar != null) {
/*  573 */       rc = bar.getClose();
/*      */     }
/*  575 */     return rc;
/*      */   }
/*      */ 
/*      */   public double iHigh(Instrument symbol, int timeframe, int shift, OfferSide offerSide)
/*      */     throws JFException
/*      */   {
/*  586 */     double rc = 0.0D;
/*  587 */     IBar bar = null;
/*      */     try {
/*  589 */       bar = getBar(symbol, timeframe, OfferSide.BID, shift);
/*      */     }
/*      */     catch (JFException e) {
/*  592 */       this.lastError = 2;
/*  593 */       stopStrategy();
/*  594 */       throw e;
/*      */     }
/*  596 */     if (bar != null) {
/*  597 */       rc = bar.getHigh();
/*      */     }
/*  599 */     return rc;
/*      */   }
/*      */ 
/*      */   public double iLow(Instrument symbol, int timeframe, int shift, OfferSide offerSide)
/*      */     throws JFException
/*      */   {
/*  610 */     double rc = 0.0D;
/*  611 */     IBar bar = null;
/*      */     try {
/*  613 */       bar = getBar(symbol, timeframe, offerSide, shift);
/*      */     }
/*      */     catch (JFException e)
/*      */     {
/*  618 */       this.lastError = 2;
/*  619 */       stopStrategy();
/*  620 */       throw e;
/*      */     }
/*  622 */     if (bar != null) {
/*  623 */       rc = bar.getLow();
/*      */     }
/*  625 */     return rc;
/*      */   }
/*      */ 
/*      */   public double iOpen(Instrument symbol, int timeframe, int shift, OfferSide offerSide) throws JFException
/*      */   {
/*  630 */     double rc = 0.0D;
/*  631 */     IBar bar = null;
/*      */     try {
/*  633 */       bar = getBar(symbol, timeframe, OfferSide.BID, shift);
/*      */     }
/*      */     catch (JFException e) {
/*  636 */       this.lastError = 2;
/*  637 */       stopStrategy();
/*  638 */       throw e;
/*      */     }
/*  640 */     if (bar != null) {
/*  641 */       rc = bar.getOpen();
/*      */     }
/*  643 */     return rc;
/*      */   }
/*      */ 
/*      */   public long iTime(Instrument symbol, int timeframe, int shift)
/*      */     throws JFException
/*      */   {
/*  654 */     long rc = 0L;
/*  655 */     IBar bar = null;
/*      */     try
/*      */     {
/*  659 */       bar = getBar(symbol, timeframe, OfferSide.BID, shift);
/*      */     }
/*      */     catch (JFException e) {
/*  662 */       this.lastError = 2;
/*  663 */       stopStrategy();
/*  664 */       throw e;
/*      */     }
/*  666 */     if (bar != null) {
/*  667 */       rc = bar.getTime() / 1000L;
/*      */     }
/*  669 */     return rc;
/*      */   }
/*      */ 
/*      */   public double iVolume(Instrument symbol, int timeframe, int shift)
/*      */     throws JFException
/*      */   {
/*  680 */     double rc = 0.0D;
/*  681 */     IBar bar = null;
/*      */     try {
/*  683 */       bar = getBar(symbol, timeframe, OfferSide.BID, shift);
/*      */     }
/*      */     catch (JFException e) {
/*  686 */       this.lastError = 2;
/*  687 */       stopStrategy();
/*  688 */       throw e;
/*      */     }
/*  690 */     if (bar != null) {
/*  691 */       rc = bar.getVolume();
/*      */     }
/*  693 */     return rc;
/*      */   }
/*      */ 
/*      */   public int iLowest(Instrument symbol, int timeframe, int type, int count, int start, OfferSide offerSide)
/*      */     throws JFException
/*      */   {
/*  702 */     int rc = -1;
/*      */     try {
/*  704 */       IBar[] bars = getBars(symbol, timeframe, offerSide, start, count);
/*      */ 
/*  707 */       double lowValue = 1.7976931348623157E+308D;
/*  708 */       for (int i = 0; i < bars.length; i++)
/*      */       {
/*  710 */         IBar bar = bars[i];
/*      */ 
/*  712 */         double value = 0.0D;
/*  713 */         switch (type) {
/*      */         case 0:
/*  715 */           value = bar.getOpen();
/*  716 */           break;
/*      */         case 1:
/*  718 */           value = bar.getLow();
/*  719 */           break;
/*      */         case 2:
/*  721 */           value = bar.getHigh();
/*  722 */           break;
/*      */         case 3:
/*  724 */           value = bar.getClose();
/*  725 */           break;
/*      */         case 4:
/*  727 */           value = bar.getVolume();
/*  728 */           break;
/*      */         case 5:
/*  730 */           value = bar.getTime() / 1000L;
/*  731 */           break;
/*      */         default:
/*  733 */           value = bar.getOpen();
/*      */         }
/*      */ 
/*  736 */         if (value < lowValue) {
/*  737 */           rc = i + start;
/*  738 */           lowValue = value;
/*      */         }
/*      */       }
/*      */     }
/*      */     catch (JFException e)
/*      */     {
/*  744 */       this.lastError = 2;
/*  745 */       stopStrategy();
/*  746 */       throw e;
/*      */     }
/*  748 */     return rc;
/*      */   }
/*      */ 
/*      */   public int iHighest(Instrument symbol, int timeframe, int type, int count, int start, OfferSide offerSide) throws JFException
/*      */   {
/*  753 */     int rc = -1;
/*      */     try {
/*  755 */       IBar[] bars = getBars(symbol, timeframe, offerSide, start, count);
/*      */ 
/*  758 */       double highValue = 4.9E-324D;
/*  759 */       for (int i = 0; i < bars.length; i++)
/*      */       {
/*  761 */         IBar bar = bars[i];
/*      */ 
/*  763 */         double value = 0.0D;
/*  764 */         switch (type) {
/*      */         case 0:
/*  766 */           value = bar.getOpen();
/*  767 */           break;
/*      */         case 1:
/*  769 */           value = bar.getLow();
/*  770 */           break;
/*      */         case 2:
/*  772 */           value = bar.getHigh();
/*  773 */           break;
/*      */         case 3:
/*  775 */           value = bar.getClose();
/*  776 */           break;
/*      */         case 4:
/*  778 */           value = bar.getVolume();
/*  779 */           break;
/*      */         case 5:
/*  781 */           value = bar.getTime() / 1000L;
/*  782 */           break;
/*      */         default:
/*  784 */           value = bar.getOpen();
/*      */         }
/*      */ 
/*  787 */         if (value > highValue) {
/*  788 */           rc = i + start;
/*  789 */           highValue = value;
/*      */         }
/*      */       }
/*      */     }
/*      */     catch (JFException e) {
/*  794 */       this.lastError = 2;
/*  795 */       stopStrategy();
/*  796 */       throw e;
/*      */     }
/*  798 */     return rc;
/*      */   }
/*      */ 
/*      */   public int iBarShift(Instrument symbol, int timeframe, long time, boolean exact)
/*      */     throws JFException
/*      */   {
/*  810 */     int indexer = 0;
/*      */     try {
/*  812 */       IBar bar = null;
/*      */       while (true) {
/*  814 */         bar = getBar(symbol, timeframe, OfferSide.BID, indexer);
/*  815 */         if ((bar == null) || (bar.getTime() <= time * 1000L))
/*      */           break;
/*  817 */         indexer++;
/*      */       }
/*  819 */       if ((bar == null) && (indexer > 0)) {
/*  820 */         indexer--;
/*  821 */         bar = getBar(symbol, timeframe, OfferSide.BID, indexer);
/*      */       }
/*  823 */       if (bar == null)
/*  824 */         return -1;
/*  825 */       if (exact) {
/*  826 */         if (bar.getTime() == time * 1000L)
/*  827 */           return indexer;
/*  828 */         return -1;
/*      */       }
/*  830 */       return indexer;
/*      */     }
/*      */     catch (JFException e) {
/*  833 */       this.lastError = 2;
/*  834 */       stopStrategy();
/*  835 */     }throw e;
/*      */   }
/*      */ 
/*      */   public int iBars(Instrument symbol, int timeframe)
/*      */   {
/*  845 */     int currentBars = 0;
/*  846 */     IChart chart = null;
/*      */ 
/*  848 */     chart = getIChart();
/*  849 */     if (chart != null) {
/*  850 */       currentBars = chart.getBarsCount();
/*      */     }
/*  852 */     return currentBars;
/*      */   }
/*      */ 
/*      */   public void SendMail(String subject, String some_text)
/*      */   {
/*  876 */     String msg = new StringBuilder().append("Mail system unavailable. Trying to send an email:[subj:").append(subject).append("][text:").append(some_text).append("]").toString();
/*      */ 
/*  878 */     if (getIConsole() != null) {
/*  879 */       getIConsole().getErr().println(msg);
/*      */     }
/*  881 */     else if ((msg != null) && (!msg.isEmpty()))
/*  882 */       System.out.println(msg);
/*      */   }
/*      */ 
/*      */   public int MessageBox(String message, String title, int flags)
/*      */   {
/*  899 */     int rc = -1;
/*  900 */     int optionType = -1;
/*      */ 
/*  902 */     switch (flags) {
/*      */     case 0:
/*  904 */       optionType = -1;
/*  905 */       break;
/*      */     case 1:
/*  907 */       optionType = 2;
/*  908 */       break;
/*      */     case 4:
/*  910 */       optionType = 0;
/*  911 */       break;
/*      */     case 3:
/*  913 */       optionType = 1;
/*  914 */       break;
/*      */     case 2:
/*      */     default:
/*  917 */       CommonHelpers.notSupported();
/*      */     }
/*      */ 
/*  921 */     JOptionPane pane = new JOptionPane(message, 1, optionType);
/*      */ 
/*  924 */     JDialog dialog = pane.createDialog((JFrame)GreedContext.get("clientGui"), title);
/*      */ 
/*  926 */     dialog.setVisible(true);
/*  927 */     Object selectedValue = pane.getValue();
/*  928 */     if (selectedValue == null) {
/*  929 */       return rc;
/*      */     }
/*      */ 
/*  933 */     if ((selectedValue instanceof Integer)) {
/*  934 */       rc = ((Integer)selectedValue).intValue();
/*  935 */       if (rc == -1)
/*  936 */         rc = 0;
/*  937 */       else if (rc == 0)
/*  938 */         rc = 6;
/*  939 */       else if (rc == 1)
/*  940 */         rc = 7;
/*  941 */       else if (rc == 2)
/*  942 */         rc = 2;
/*  943 */       else if (rc == 0)
/*  944 */         rc = 1;
/*      */       else {
/*  946 */         CommonHelpers.notSupported();
/*      */       }
/*      */     }
/*  949 */     return rc;
/*      */   }
/*      */ 
/*      */   public void Alert(Object[] str)
/*      */   {
/*  976 */     if (this.alertDialog == null) {
/*  977 */       this.alertDialog = new MQLAlertDialog(getStrategy().getBox(), getClass().getSimpleName());
/*      */     }
/*  979 */     if (this.indicator != null) {
/*  980 */       this.alertDialog.show(getAbstractConnector().getLastTickTime(), getIBox().multiargToString("", str));
/*      */     }
/*  982 */     else if ((getIEngine() != null) && (getIEngine().getType() != IEngine.Type.TEST))
/*      */     {
/*  984 */       this.alertDialog.show(getAbstractConnector().getLastTickTime(), getIBox().multiargToString("", str));
/*      */     }
/*      */   }
/*      */ 
/*      */   public void Comment(Object[] obj)
/*      */   {
/* 1004 */     if (getIChart() != null)
/* 1005 */       if (getIBox() != null) {
/* 1006 */         getIChart().comment(getIBox().multiargToString("", obj));
/*      */       }
/* 1008 */       else if (obj != null)
/* 1009 */         getIChart().comment(new StringBuilder().append(obj[0]).append("").toString());
/*      */   }
/*      */ 
/*      */   public void Print(Object[] str)
/*      */   {
/* 1017 */     if (getIBox() != null)
/* 1018 */       getIBox().print(str);
/* 1019 */     else if (getIConsole() != null)
/* 1020 */       getIConsole().getOut().println(multiargToString("", str));
/*      */     else
/* 1022 */       for (Object object : str)
/* 1023 */         System.out.println(object);
/*      */   }
/*      */ 
/*      */   public String multiargToString(String delim, Object[] str)
/*      */   {
/* 1029 */     StringBuilder builder = new StringBuilder();
/* 1030 */     for (Object object : str) {
/* 1031 */       if ((object instanceof Number)) {
/* 1032 */         Number number = (Number)object;
/* 1033 */         if (Math.abs(number.doubleValue()) > 1.0E-006D)
/* 1034 */           builder.append(new StringBuilder().append(delim).append(dfPrint.format(object)).toString());
/*      */         else
/* 1036 */           builder.append(new StringBuilder().append(delim).append(object).toString());
/*      */       }
/*      */       else {
/* 1039 */         builder.append(new StringBuilder().append(delim).append(object).toString());
/*      */       }
/*      */     }
/* 1042 */     return builder.substring(delim.length());
/*      */   }
/*      */ 
/*      */   int GetTickCount__()
/*      */   {
/* 1050 */     return 0;
/*      */   }
/*      */ 
/*      */   public double MarketInfo(Instrument symbol, short type)
/*      */     throws JFException
/*      */   {
/* 1065 */     IBar bar = null;
/* 1066 */     this.lastError = 0;
/*      */     ITick iTick;
/* 1067 */     switch (type) {
/*      */     case 1:
/*      */       try {
/* 1070 */         bar = getBar(symbol, (int)(Period.DAILY.getInterval() / 60000L), OfferSide.BID, 0);
/*      */       }
/*      */       catch (JFException e)
/*      */       {
/* 1077 */         this.lastError = 2;
/* 1078 */         throw e;
/*      */       }
/* 1080 */       if (bar != null) {
/* 1081 */         return bar.getLow();
/*      */       }
/* 1083 */       return 0.0D;
/*      */     case 2:
/*      */       try
/*      */       {
/* 1087 */         bar = getIHistory().getBar(getCurrentInstrument(symbol), Period.DAILY, OfferSide.BID, 0);
/*      */       }
/*      */       catch (JFException e)
/*      */       {
/* 1091 */         this.lastError = 2;
/* 1092 */         throw e;
/*      */       }
/* 1094 */       if (bar != null) {
/* 1095 */         return bar.getHigh();
/*      */       }
/* 1097 */       return 0.0D;
/*      */     case 5:
/* 1100 */       return getAbstractConnector().getLastTickTime() / 1000L;
/*      */     case 9:
/* 1108 */       iTick = null;
/*      */       try
/*      */       {
/* 1111 */         iTick = getIHistory().getLastTick(getCurrentInstrument(symbol));
/*      */       }
/*      */       catch (JFException e) {
/* 1114 */         this.lastError = 2;
/* 1115 */         throw e;
/*      */       }
/* 1117 */       if (iTick == null) break;
/* 1118 */       return iTick.getBid();
/*      */     case 10:
/* 1124 */       iTick = null;
/*      */       try
/*      */       {
/* 1127 */         iTick = getIHistory().getLastTick(getCurrentInstrument(symbol));
/*      */       }
/*      */       catch (JFException e) {
/* 1130 */         this.lastError = 2;
/* 1131 */         throw e;
/*      */       }
/* 1133 */       if (iTick != null) {
/* 1134 */         return iTick.getAsk();
/*      */       }
/* 1136 */       return 0.0D;
/*      */     case 11:
/* 1140 */       return getCurrentInstrument(symbol).getPipValue();
/*      */     case 12:
/* 1148 */       return getCurrentInstrument(symbol).getPipScale();
/*      */     case 13:
/* 1154 */       ITick tick = null;
/* 1155 */       double tAsk = getStrategy().getAsk();
/* 1156 */       double tBid = getStrategy().getBid();
/*      */       try {
/* 1158 */         tick = getIHistory().getLastTick(getCurrentInstrument(symbol));
/* 1159 */         tAsk = tick.getAsk();
/* 1160 */         tBid = tick.getBid();
/*      */       }
/*      */       catch (JFException e) {
/* 1163 */         this.lastError = 2;
/* 1164 */         throw e;
/*      */       }
/* 1166 */       return MathHelpers.normalizeDouble((tAsk - tBid) / getCurrentInstrument(symbol).getPipValue(), 7);
/*      */     case 14:
/* 1173 */       return 1.0D;
/*      */     case 15:
/* 1176 */       return 1000000.0D;
/*      */     case 16:
/* 1179 */       double rc = 0.0D;
/* 1180 */       double lot = MarketInfo(getCurrentInstrument(symbol), 15);
/* 1181 */       double pip = getCurrentInstrument(symbol).getPipValue();
/*      */       try {
/* 1183 */         rc = getIBox().convertMoney(lot * pip, getCurrentInstrument(symbol).getSecondaryCurrency(), getIAccount().getCurrency());
/*      */       }
/*      */       catch (JFException e)
/*      */       {
/* 1188 */         this.lastError = 2;
/* 1189 */         throw e;
/*      */       }
/*      */ 
/* 1192 */       return rc;
/*      */     case 17:
/* 1195 */       return getCurrentInstrument(symbol).getPipValue() / 10.0D;
/*      */     case 18:
/* 1200 */       return 0.0D;
/*      */     case 19:
/* 1203 */       return 0.0D;
/*      */     case 20:
/* 1206 */       Calendar cal = Calendar.getInstance();
/* 1207 */       cal.setTime(new Date());
/* 1208 */       cal.set(11, 0);
/* 1209 */       cal.set(12, 0);
/* 1210 */       cal.set(13, 0);
/* 1211 */       cal.set(14, 0);
/* 1212 */       cal.set(5, 1);
/* 1213 */       cal.set(2, 1);
/*      */ 
/* 1215 */       return cal.getTimeInMillis() / 100L;
/*      */     case 21:
/* 1219 */       return 0.0D;
/*      */     case 22:
/* 1222 */       return getMarketView().getInstrumentState(getCurrentInstrument(symbol).toString()).getTradable();
/*      */     case 23:
/* 1231 */       return 0.1D;
/*      */     case 24:
/* 1234 */       return 0.1D;
/*      */     case 25:
/* 1237 */       return 500.0D;
/*      */     case 26:
/* 1240 */       return 0.0D;
/*      */     case 27:
/* 1245 */       return 0.0D;
/*      */     case 28:
/* 1250 */       return 0.0D;
/*      */     case 29:
/* 1254 */       double lotsize = MarketInfo(getCurrentInstrument(symbol), 15);
/*      */ 
/* 1256 */       return lotsize / 100.0D;
/*      */     case 30:
/* 1259 */       double lotsize1 = MarketInfo(symbol, 15);
/* 1260 */       return lotsize1 / 200.0D;
/*      */     case 31:
/* 1264 */       return 0.0D;
/*      */     case 32:
/* 1267 */       return 0.0D;
/*      */     case 33:
/* 1270 */       return 0.0D;
/*      */     case 3:
/*      */     case 4:
/*      */     case 6:
/*      */     case 7:
/* 1275 */     case 8: } CommonHelpers.notSupported();
/* 1276 */     return 0.0D;
/*      */   }
/*      */ 
/*      */   public void Sleep(int milliseconds)
/*      */   {
/*      */     try
/*      */     {
/* 1289 */       Thread.sleep(milliseconds);
/*      */     } catch (Exception e) {
/* 1291 */       LOGGER.error("", e);
/*      */     }
/*      */   }
/*      */ 
/*      */   public boolean PlaySound(String filename)
/*      */   {
/* 1303 */     return getIBox().playSound(new StringBuilder().append("sounds").append(File.separator).append(filename).toString());
/*      */   }
/*      */ 
/*      */   private int generateFileHandler(boolean isBinFile)
/*      */   {
/* 1312 */     BitSet bitSet = new BitSet();
/* 1313 */     bitSet.set(7, isBinFile);
/* 1314 */     int handler = CommonHelpers.intFromBitset(bitSet);
/* 1315 */     while ((this.filesMap.containsKey(Integer.valueOf(handler))) || (handler <= 0)) {
/* 1316 */       handler++;
/*      */     }
/* 1318 */     return handler;
/*      */   }
/*      */ 
/*      */   public int FileOpen(String filename, int mode, char delimiter)
/*      */   {
/* 1349 */     return FileOpenPath(filename, FileHelpers.getFilesForStrategiesDir(), mode, delimiter);
/*      */   }
/*      */ 
/*      */   public int FileOpen(String filename, int mode, String delimiter)
/*      */   {
/* 1354 */     return FileOpen(filename, mode, delimiter.charAt(0));
/*      */   }
/*      */ 
/*      */   public int FileOpen(String filename, int mode) {
/* 1358 */     return FileOpen(filename, mode, this.csvDelimiter);
/*      */   }
/*      */ 
/*      */   public void FileClose(int handle)
/*      */   {
/* 1365 */     RandomAccessFile accessFile = (RandomAccessFile)this.filesMap.get(Integer.valueOf(handle));
/* 1366 */     if (accessFile != null) {
/*      */       try {
/* 1368 */         accessFile.close();
/*      */       } catch (Exception e) {
/* 1370 */         e.printStackTrace();
/*      */       }
/*      */     }
/* 1373 */     this.filesMap.remove(Integer.valueOf(handle));
/* 1374 */     this.filesDelimiters.remove(Integer.valueOf(handle));
/*      */   }
/*      */ 
/*      */   public int FileSize(int handle)
/*      */   {
/* 1382 */     int rc = -1;
/*      */ 
/* 1384 */     RandomAccessFile accessFile = (RandomAccessFile)this.filesMap.get(Integer.valueOf(handle));
/*      */ 
/* 1386 */     if (accessFile != null) {
/*      */       try {
/* 1388 */         rc = (int)accessFile.length();
/*      */       } catch (Exception e) {
/* 1390 */         e.printStackTrace();
/*      */       }
/*      */     }
/* 1393 */     return rc;
/*      */   }
/*      */ 
/*      */   public String FileReadString(int handle)
/*      */   {
/* 1404 */     return FileReadString(handle, 0);
/*      */   }
/*      */ 
/*      */   public String FileReadString(int handle, int length) {
/* 1408 */     RandomAccessFile accessFile = (RandomAccessFile)this.filesMap.get(Integer.valueOf(handle));
/* 1409 */     Character delimiter = null;
/* 1410 */     if (!FileHelpers.isHandlerForBinFile(handle)) {
/* 1411 */       delimiter = (Character)this.filesDelimiters.get(Integer.valueOf(handle));
/*      */     }
/* 1413 */     return FileHelpers.FileReadString(accessFile, length, delimiter);
/*      */   }
/*      */ 
/*      */   public boolean FileSeek(int handle, int offset, int origin)
/*      */     throws JFException
/*      */   {
/* 1436 */     boolean rc = false;
/* 1437 */     RandomAccessFile accessFile = (RandomAccessFile)this.filesMap.get(Integer.valueOf(handle));
/* 1438 */     if (accessFile != null) {
/*      */       try {
/* 1440 */         long originPosition = 0L;
/* 1441 */         if (origin == 0)
/* 1442 */           originPosition = 0L;
/* 1443 */         else if (origin == 1)
/* 1444 */           originPosition = accessFile.getFilePointer();
/* 1445 */         else if (origin == 2)
/* 1446 */           originPosition = accessFile.length();
/*      */         else {
/* 1448 */           CommonHelpers.notSupported();
/*      */         }
/* 1450 */         accessFile.seek(originPosition + offset);
/* 1451 */         rc = true;
/*      */       }
/*      */       catch (Exception ex) {
/* 1454 */         throw new JFException(ex);
/*      */       }
/*      */     }
/* 1457 */     return rc;
/*      */   }
/*      */ 
/*      */   public int FileWrite(int handle, Object[] objs)
/*      */     throws JFException
/*      */   {
/* 1480 */     int rc = -1;
/* 1481 */     if (FileHelpers.isHandlerForBinFile(handle)) {
/* 1482 */       return -1;
/*      */     }
/* 1484 */     RandomAccessFile accessFile = (RandomAccessFile)this.filesMap.get(Integer.valueOf(handle));
/* 1485 */     Character delimiter = (Character)this.filesDelimiters.get(Integer.valueOf(handle));
/* 1486 */     if (accessFile != null) {
/*      */       try {
/* 1488 */         String out = new StringBuilder().append(getIBox().multiargToString(new StringBuilder().append(delimiter).append("").toString(), objs)).append("\r\n").toString();
/*      */ 
/* 1490 */         rc = out.length();
/* 1491 */         accessFile.write(out.getBytes("UTF-8"));
/*      */       }
/*      */       catch (Exception e) {
/* 1494 */         throw new JFException(e);
/*      */       }
/*      */     }
/* 1497 */     return rc;
/*      */   }
/*      */ 
/*      */   public boolean GlobalVariableCheck(String name)
/*      */   {
/* 1517 */     ISettings settings = getIBox().getSettings("global");
/* 1518 */     return settings.get(name) != null;
/*      */   }
/*      */ 
/*      */   public boolean GlobalVariableDel(String name)
/*      */   {
/* 1526 */     ISettings settings = getIBox().getSettings("global");
/* 1527 */     settings.remove(name);
/* 1528 */     return true;
/*      */   }
/*      */ 
/*      */   public double GlobalVariableGet(String name)
/*      */   {
/* 1535 */     ISettings settings = getIBox().getSettings("global");
/* 1536 */     return CommonHelpers.parseDouble(settings.get(name));
/*      */   }
/*      */ 
/*      */   public boolean GlobalVariableGet_Boolean(String name) {
/* 1540 */     ISettings settings = getIBox().getSettings("global");
/* 1541 */     double dbl = CommonHelpers.parseDouble(settings.get(name));
/* 1542 */     boolean rc = false;
/* 1543 */     if (dbl > 0.0D) {
/* 1544 */       rc = true;
/*      */     }
/* 1546 */     return rc;
/*      */   }
/*      */ 
/*      */   public String GlobalVariableName(int index)
/*      */   {
/* 1553 */     ISettings settings = getIBox().getSettings("global");
/* 1554 */     return null;
/*      */   }
/*      */ 
/*      */   public long GlobalVariableSet(String name, double value)
/*      */   {
/* 1563 */     ISettings settings = getIBox().getSettings("global");
/* 1564 */     settings.put(name, CommonHelpers.formatDouble(value));
/* 1565 */     return 0L;
/*      */   }
/*      */ 
/*      */   public long GlobalVariableSet(String name, boolean value) {
/* 1569 */     ISettings settings = getIBox().getSettings("global");
/* 1570 */     settings.put(name, value ? "1" : "0");
/* 1571 */     return 0L;
/*      */   }
/*      */ 
/*      */   public boolean GlobalVariableSetOnCondition__(String name, double value, double check_value)
/*      */   {
/* 1587 */     return false;
/*      */   }
/*      */ 
/*      */   public int GlobalVariablesDeleteAll__(String prefix_name)
/*      */   {
/* 1595 */     return 0;
/*      */   }
/*      */ 
/*      */   public int GlobalVariablesDeleteAll(String prefix_name) {
/* 1599 */     return 0;
/*      */   }
/*      */ 
/*      */   public int GlobalVariablesTotal__()
/*      */   {
/* 1604 */     return 0;
/*      */   }
/*      */ 
/*      */   public int GlobalVariablesTotal() {
/* 1608 */     ISettings settings = getIBox().getSettings("global");
/*      */     HighAvailabilityStore store;
/* 1609 */     if ((settings instanceof HighAvailabilityStore)) {
/* 1610 */       store = (HighAvailabilityStore)settings;
/*      */     }
/* 1612 */     return 0;
/*      */   }
/*      */ 
/*      */   public int ArrayCopyRates(Object dest_array, Instrument symbol, int timeframe)
/*      */     throws JFException
/*      */   {
/* 1650 */     int size = -1;
/* 1651 */     if ((dest_array instanceof double[][])) {
/* 1652 */       double[][] array = (double[][])(double[][])dest_array;
/* 1653 */       size = array[0].length;
/* 1654 */       int dimentions = array.length;
/* 1655 */       IBar[] bars = getBars(symbol, timeframe, OfferSide.BID, 0, size);
/* 1656 */       double[][] src = CommonHelpers.getIndicatorInputDataReverse(bars);
/* 1657 */       for (int i = 0; i < dimentions; i++) {
/* 1658 */         System.arraycopy(src[i], 0, array[i], 0, size);
/*      */       }
/* 1660 */       size *= dimentions;
/*      */     }
/* 1662 */     return size;
/*      */   }
/*      */ 
/*      */   public int ArrayCopySeries(Object array, int series_index, Instrument symbol, int timeframe)
/*      */     throws JFException
/*      */   {
/* 1694 */     int size = -1;
/* 1695 */     if ((array instanceof double[])) {
/* 1696 */       size = ((double[])(double[])array).length;
/* 1697 */       IBar[] bars = getBars(symbol, timeframe, OfferSide.BID, 0, ((double[])(double[])array).length);
/*      */ 
/* 1699 */       double[] src = CommonHelpers.getIndicatorInputData(bars, series_index);
/*      */ 
/* 1701 */       System.arraycopy(src, 0, array, 0, size);
/*      */     }
/* 1703 */     return size;
/*      */   }
/*      */ 
/*      */   public boolean ArrayGetAsSeries(Object array)
/*      */   {
/* 1713 */     throw new RuntimeException("This function not allowed");
/*      */   }
/*      */ 
/*      */   public boolean ArrayIsSeries(Object array)
/*      */   {
/* 1725 */     throw new RuntimeException("This function not allowed");
/*      */   }
/*      */ 
/*      */   public boolean ArraySetAsSeries(Object array, boolean set)
/*      */   {
/* 1741 */     throw new RuntimeException("This function not allowed");
/*      */   }
/*      */ 
/*      */   public Color RGB(int r, int g, int b)
/*      */   {
/* 1748 */     return new Color(r, g, b);
/*      */   }
/*      */ 
/*      */   public boolean ObjectDelete(String name)
/*      */   {
/* 1757 */     IChartObject obj = null;
/* 1758 */     if (getIChart() != null) {
/* 1759 */       obj = getIChart().remove(name);
/*      */     }
/* 1761 */     return obj != null;
/*      */   }
/*      */ 
/*      */   public int ObjectsTotal(int type)
/*      */   {
/* 1769 */     int rc = 0;
/* 1770 */     if (getIChart() != null) {
/* 1771 */       if (type == -1)
/* 1772 */         rc = getIChart().getAll().size();
/*      */       else {
/* 1774 */         for (IChartObject chartObject : getIChart().getAll()) {
/* 1775 */           if (chartObject.getType() == CommonHelpers.convertChartType(type))
/*      */           {
/* 1777 */             rc++;
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/* 1782 */     return rc;
/*      */   }
/*      */ 
/*      */   public String ObjectName(int index)
/*      */   {
/* 1793 */     String rc = null;
/* 1794 */     if ((getIChart() != null) && 
/* 1795 */       (getIChart().getAll().size() > index) && (index >= 0)) {
/* 1796 */       IChartObject chartObject = (IChartObject)getIChart().getAll().get(index);
/* 1797 */       rc = chartObject.getKey();
/*      */     }
/*      */ 
/* 1800 */     return rc;
/*      */   }
/*      */ 
/*      */   public int ObjectFind(String name)
/*      */   {
/* 1813 */     int rc = 0;
/*      */     int counter;
/* 1814 */     if (getIChart() != null) {
/* 1815 */       counter = 1;
/* 1816 */       for (IChartObject chartObject : getIChart().getAll()) {
/* 1817 */         if (chartObject.getKey().equals(name)) {
/* 1818 */           rc = counter;
/* 1819 */           break;
/*      */         }
/* 1821 */         counter++;
/*      */       }
/*      */     }
/* 1824 */     return rc;
/*      */   }
/*      */ 
/*      */   public boolean ObjectCreate(String name, int type, int window, long time1, double price1, long time2, double price2, long time3, double price3)
/*      */   {
/* 1867 */     IChart.Type chartType = CommonHelpers.convertChartType(type);
/* 1868 */     IChartObject chartObject = null;
/* 1869 */     if (getIChart() != null) {
/* 1870 */       chartObject = getIChart().draw(name, chartType, time1 * 1000L, price1, time2 * 1000L, price2, time3 * 1000L, price3);
/*      */     }
/*      */ 
/* 1873 */     return chartObject != null;
/*      */   }
/*      */ 
/*      */   public boolean ObjectSetText(String name, String text, int font_size, String font, Color text_color)
/*      */   {
/* 1900 */     IChartObject chartObject = null;
/* 1901 */     if (getIChart() != null) {
/* 1902 */       chartObject = getIChart().get(name);
/*      */     }
/* 1904 */     if (chartObject != null) {
/* 1905 */       chartObject.setText(text);
/* 1906 */       return true;
/*      */     }
/* 1908 */     return false;
/*      */   }
/*      */ 
/*      */   public void ObjectsRedraw() {
/* 1912 */     throw new RuntimeException("This function not allowed");
/*      */   }
/*      */ 
/*      */   public boolean ObjectSet(String name, int index, double value)
/*      */   {
/* 1933 */     IChartObject chartObject = null;
/* 1934 */     if (getIChart() != null) {
/* 1935 */       chartObject = getIChart().get(name);
/*      */     }
/* 1937 */     if (chartObject != null) {
/* 1938 */       IChartObject.ATTR_BOOLEAN attr_boolean = CommonHelpers.getObjectPropBoolean(index);
/*      */ 
/* 1941 */       IChartObject.ATTR_COLOR attr_color = CommonHelpers.getObjectPropColor(index);
/* 1942 */       IChartObject.ATTR_DOUBLE attr_double = CommonHelpers.getObjectPropDouble(index);
/* 1943 */       IChartObject.ATTR_INT attr_int = CommonHelpers.getObjectPropInt(index);
/* 1944 */       IChartObject.ATTR_LONG attr_long = CommonHelpers.getObjectPropLong(index);
/*      */ 
/* 1946 */       if (attr_boolean != null) {
/* 1947 */         chartObject.setAttrBoolean(attr_boolean, value > 0.0D);
/*      */       }
/* 1949 */       else if (attr_color != null)
/* 1950 */         chartObject.setAttrColor(attr_color, new Color((int)value));
/* 1951 */       else if (attr_double != null)
/* 1952 */         chartObject.setAttrDouble(attr_double, value);
/* 1953 */       else if (attr_int != null)
/* 1954 */         chartObject.setAttrInt(attr_int, (int)value);
/* 1955 */       else if (attr_long != null)
/* 1956 */         chartObject.setAttrLong(attr_long, (int)value);
/*      */       else {
/* 1958 */         throw new RuntimeException(new StringBuilder().append("unknown object attribute. ").append(index).toString());
/*      */       }
/*      */     }
/*      */ 
/* 1962 */     return true;
/*      */   }
/*      */ 
/*      */   public double WindowPriceMax(int index)
/*      */   {
/* 1978 */     if (index < 0) {
/* 1979 */       CommonHelpers.notSupported();
/*      */     }
/* 1981 */     if (getIChart() != null) {
/* 1982 */       return getIChart().priceMax(index);
/*      */     }
/* 1984 */     return 0.0D;
/*      */   }
/*      */ 
/*      */   public double WindowPriceMin(int index)
/*      */     throws JFException
/*      */   {
/* 2000 */     if (index < 0) {
/* 2001 */       CommonHelpers.notOpperationSupported();
/*      */     }
/* 2003 */     if (getIChart() != null) {
/* 2004 */       return getIChart().priceMin(index);
/*      */     }
/* 2006 */     return 0.0D;
/*      */   }
/*      */ 
/*      */   public synchronized double AccountFreeMargin()
/*      */     throws JFException
/*      */   {
/* 2015 */     if (getAbstractConnector().isInitialized()) {
/* 2016 */       if (getIAccount().getCreditLine() == 0.0D) {
/* 2017 */         this.lastError = 64;
/* 2018 */         new JFException("Account CreditLine = 0");
/*      */       }
/* 2020 */       return getIAccount().getCreditLine() / getIAccount().getLeverage();
/*      */     }
/* 2022 */     return 0.0D;
/*      */   }
/*      */ 
/*      */   public double AccountFreeMarginCheck(Instrument symbol, int cmd, double volume)
/*      */     throws JFException
/*      */   {
/* 2032 */     ITick lastTick = null;
/*      */     try {
/* 2034 */       lastTick = getIHistory().getLastTick(symbol);
/*      */     }
/*      */     catch (JFException e) {
/* 2037 */       this.lastError = 64;
/* 2038 */       throw e;
/*      */     }
/*      */ 
/* 2041 */     double rc = 0.0D;
/* 2042 */     if (lastTick != null)
/*      */     {
/* 2044 */       rc = AccountFreeMargin() - lastTick.getBid() * volume * 1000000.0D;
/* 2045 */       if (rc < 0.0D) {
/* 2046 */         rc = 0.0D;
/* 2047 */         this.lastError = 134;
/*      */       }
/*      */     }
/* 2050 */     return rc;
/*      */   }
/*      */ 
/*      */   public synchronized double AccountMargin() throws JFException
/*      */   {
/* 2055 */     if (getAbstractConnector().isInitialized()) {
/* 2056 */       return getIAccount().getCreditLine() / getIAccount().getLeverage();
/*      */     }
/* 2058 */     return 0.0D;
/*      */   }
/*      */ 
/*      */   public synchronized int AccountLeverage()
/*      */     throws JFException
/*      */   {
/* 2064 */     return (int)getIAccount().getLeverage();
/*      */   }
/*      */ 
/*      */   public String AccountCompany()
/*      */   {
/* 2070 */     return "Dukascopy (Suisse) SA";
/*      */   }
/*      */ 
/*      */   public double AccountCredit() throws JFException
/*      */   {
/* 2075 */     if (getAbstractConnector().isInitialized()) {
/* 2076 */       return getIAccount().getCreditLine();
/*      */     }
/* 2078 */     return 0.0D;
/*      */   }
/*      */ 
/*      */   public synchronized String AccountCurrency() throws JFException
/*      */   {
/* 2083 */     return getIAccount().getCurrency().toString();
/*      */   }
/*      */ 
/*      */   public synchronized double AccountEquity() throws JFException {
/* 2087 */     return getIAccount().getEquity();
/*      */   }
/*      */ 
/*      */   public String AccountName() throws JFException {
/* 2091 */     return getIEngine().getAccount();
/*      */   }
/*      */ 
/*      */   public int AccountNumber()
/*      */   {
/* 2096 */     return 1;
/*      */   }
/*      */ 
/*      */   public double AccountProfit() {
/* 2100 */     return 0.0D;
/*      */   }
/*      */ 
/*      */   public double AccountFreeMarginMode()
/*      */   {
/* 2116 */     throw new RuntimeException("This function not allowed");
/*      */   }
/*      */ 
/*      */   public String AccountServer()
/*      */   {
/* 2124 */     throw new RuntimeException("This function not allowed");
/*      */   }
/*      */ 
/*      */   public int AccountStopoutLevel()
/*      */   {
/* 2132 */     throw new RuntimeException("This function not allowed");
/*      */   }
/*      */ 
/*      */   public int AccountStopoutMode()
/*      */   {
/* 2143 */     throw new RuntimeException("This function not allowed");
/*      */   }
/*      */ 
/*      */   public synchronized double AccountBalance()
/*      */     throws JFException
/*      */   {
/* 2154 */     if (getIAccount().getEquity() == 0.0D)
/*      */     {
/* 2157 */       this.lastError = 64;
/* 2158 */       Print(new Object[] { "AccountBalance can not be used in init() function. Equity information available only on first onAccount message" });
/* 2159 */       stopStrategy();
/* 2160 */       throw new JFException("Account Equity = 0");
/*      */     }
/*      */ 
/* 2163 */     double pnlInBaseCurrency = 0.0D;
/*      */     try {
/* 2165 */       for (IOrder order : getIEngine().getOrders()) {
/* 2166 */         if (order.getState() == IOrder.State.FILLED) {
/* 2167 */           double profit = getIBox().calculateProfitMoney(order);
/* 2168 */           pnlInBaseCurrency += getIBox().convertMoney(profit, order.getInstrument().getSecondaryCurrency(), getIAccount().getCurrency());
/*      */         }
/*      */       }
/*      */ 
/*      */     }
/*      */     catch (JFException e)
/*      */     {
/* 2175 */       stopStrategy();
/* 2176 */       throw e;
/*      */     }
/* 2178 */     return getIAccount().getEquity() - pnlInBaseCurrency;
/*      */   }
/*      */ 
/*      */   public double OrderClosePrice()
/*      */     throws JFException
/*      */   {
/* 2189 */     double rc = 0.0D;
/* 2190 */     rc = getSelectedOrder().getClosePrice();
/* 2191 */     if (rc == 0.0D) {
/*      */       try {
/* 2193 */         ITick iTick = getIHistory().getLastTick(getSelectedOrder().getInstrument());
/*      */ 
/* 2195 */         if (getSelectedOrder().isLong())
/* 2196 */           rc = iTick.getBid();
/*      */         else
/* 2198 */           rc = iTick.getAsk();
/*      */       }
/*      */       catch (JFException e) {
/* 2201 */         this.lastError = 129;
/* 2202 */         throw e;
/*      */       }
/*      */     }
/*      */ 
/* 2206 */     return rc;
/*      */   }
/*      */ 
/*      */   public long OrderCloseTime()
/*      */     throws JFException
/*      */   {
/* 2217 */     return getSelectedOrder().getCloseTime() / 1000L;
/*      */   }
/*      */ 
/*      */   public boolean OrderDelete(int ticket, Color color)
/*      */     throws JFException
/*      */   {
/* 2225 */     boolean rc = false;
/* 2226 */     IOrder order = getIEngine().getOrderById(new StringBuilder().append(ticket).append("").toString());
/* 2227 */     if (order != null) {
/*      */       try {
/* 2229 */         order.close();
/* 2230 */         rc = true;
/*      */       } catch (JFException e) {
/* 2232 */         this.lastError = 3;
/* 2233 */         throw e;
/*      */       }
/*      */     }
/*      */ 
/* 2237 */     return rc;
/*      */   }
/*      */ 
/*      */   public int OrdersHistoryTotal()
/*      */     throws JFException
/*      */   {
/* 2244 */     return getStrategy().closeOrders.size();
/*      */   }
/*      */ 
/*      */   public double OrderCommission()
/*      */     throws JFException
/*      */   {
/* 2252 */     double commission = 0.0D;
/* 2253 */     if (this.selectedGroup != null) {
/* 2254 */       commission = this.selectedGroup.getOpeningOrder().getOrderCommission().getValue().doubleValue();
/*      */     }
/*      */ 
/* 2257 */     return commission;
/*      */   }
/*      */ 
/*      */   public boolean OrderClose(int ticket, double lots, double price, int slippage, Color color)
/*      */     throws JFException
/*      */   {
/* 2265 */     boolean rc = false;
/* 2266 */     IOrder order = getIEngine().getOrderById(new StringBuilder().append(ticket).append("").toString());
/* 2267 */     if (order != null) {
/* 2268 */       if (this.selectedOrder == null) {
/* 2269 */         this.selectedOrder = order;
/* 2270 */         this.selectedGroup = MTAPIHelpers.getOrderGroupById(this.selectedOrder.getId())[0];
/*      */       }
/*      */ 
/* 2273 */       if (order.getState() == IOrder.State.OPENED)
/*      */       {
/* 2275 */         if (lots == order.getRequestedAmount())
/* 2276 */           lots = 0.0D;
/*      */       }
/*      */       try
/*      */       {
/* 2280 */         if (order.getState() == IOrder.State.OPENED)
/* 2281 */           order.close();
/*      */         else {
/* 2283 */           order.close(lots, price, slippage);
/*      */         }
/* 2285 */         order.waitForUpdate(2000L);
/* 2286 */         rc = true;
/*      */       }
/*      */       catch (JFException e) {
/* 2289 */         this.lastError = 3;
/* 2290 */         throw e;
/*      */       }
/*      */     }
/* 2293 */     return rc;
/*      */   }
/*      */ 
/*      */   public int OrderTicket()
/*      */     throws JFException
/*      */   {
/* 2300 */     return Integer.parseInt(getSelectedOrder().getId());
/*      */   }
/*      */ 
/*      */   public double OrderStopLoss()
/*      */     throws JFException
/*      */   {
/* 2307 */     return getSelectedOrder().getStopLossPrice();
/*      */   }
/*      */ 
/*      */   public double OrderTakeProfit()
/*      */     throws JFException
/*      */   {
/* 2314 */     return getSelectedOrder().getTakeProfitPrice();
/*      */   }
/*      */ 
/*      */   public double OrderLots()
/*      */     throws JFException
/*      */   {
/* 2321 */     return getSelectedOrder().getAmount();
/*      */   }
/*      */ 
/*      */   public double OrderOpenPrice()
/*      */     throws JFException
/*      */   {
/* 2327 */     return getSelectedOrder().getOpenPrice();
/*      */   }
/*      */ 
/*      */   public String OrderComment()
/*      */     throws JFException
/*      */   {
/* 2334 */     return getSelectedOrder().getComment();
/*      */   }
/*      */ 
/*      */   public synchronized double OrderProfit()
/*      */     throws JFException
/*      */   {
/* 2345 */     double rc = 0.0D;
/* 2346 */     if (getIAccount() != null) {
/*      */       try {
/* 2348 */         rc = getIBox().calculateProfitMoney(getSelectedOrder());
/* 2349 */         rc = getIBox().convertMoney(rc, getSelectedOrder().getInstrument().getSecondaryCurrency(), getIAccount().getCurrency());
/*      */       }
/*      */       catch (JFException e)
/*      */       {
/* 2355 */         this.lastError = 1;
/* 2356 */         throw e;
/*      */       }
/*      */     }
/*      */ 
/* 2360 */     return rc;
/*      */   }
/*      */ 
/*      */   public long OrderOpenTime()
/*      */     throws JFException
/*      */   {
/* 2367 */     return getSelectedOrder().getFillTime() / 1000L;
/*      */   }
/*      */ 
/*      */   public Instrument OrderSymbol()
/*      */     throws JFException
/*      */   {
/* 2374 */     return getSelectedOrder().getInstrument();
/*      */   }
/*      */ 
/*      */   public int OrdersTotal() throws JFException
/*      */   {
/* 2379 */     int rc = 0;
/*      */     try {
/* 2381 */       List orders = getIEngine().getOrders();
/*      */ 
/* 2383 */       rc = orders.size();
/* 2384 */       LOGGER.info(new StringBuilder().append(rc).append("=OrdersTotal()").toString());
/*      */     } catch (JFException e) {
/* 2386 */       this.lastError = 1;
/* 2387 */       throw e;
/*      */     }
/*      */ 
/* 2390 */     return rc;
/*      */   }
/*      */ 
/*      */   public boolean OrderSelect(int index, int select, int pool)
/*      */     throws JFException
/*      */   {
/* 2416 */     LOGGER.info(new StringBuilder().append("OrderSelect(").append(index).append(", ").append(select).append(", ").append(pool).append(")").toString());
/* 2417 */     String indexStr = String.valueOf(index);
/* 2418 */     Print(new Object[] { new StringBuilder().append("Try select Order [ ").append(indexStr).append(" ]").toString() });
/*      */ 
/* 2420 */     List list = null;
/* 2421 */     if (pool == 1) {
/* 2422 */       list = getStrategy().closeOrders;
/* 2423 */     } else if (pool == 0) {
/*      */       try {
/* 2425 */         list = getIEngine().getOrders();
/*      */       } catch (JFException e) {
/* 2427 */         this.lastError = 1;
/* 2428 */         throw e;
/*      */       }
/*      */     }
/*      */     else {
/* 2432 */       this.lastError = 3;
/* 2433 */       CommonHelpers.notSupported();
/*      */     }
/*      */ 
/* 2436 */     if (select == 0) {
/* 2437 */       if (getStrategy().mqldbg) {
/* 2438 */         for (IOrder order : list) {
/* 2439 */           LOGGER.info(new StringBuilder().append("order-").append(order).toString());
/*      */         }
/*      */       }
/* 2442 */       if ((index >= list.size()) || (index < 0)) {
/* 2443 */         LOGGER.info(new StringBuilder().append("!!! - index out of bounds index=").append(index).append(" list.size()=").append(list.size()).toString());
/*      */ 
/* 2445 */         this.selectedOrder = null;
/*      */       } else {
/* 2447 */         this.selectedOrder = ((IOrder)list.get(index));
/*      */       }
/*      */ 
/* 2450 */       this.selectedGroup = MTAPIHelpers.getOrderGroupByRow(index, pool)[0];
/*      */ 
/* 2452 */       LOGGER.info(new StringBuilder().append("selected - ").append(this.selectedOrder).toString());
/* 2453 */     } else if (select == 1)
/*      */     {
/* 2455 */       for (IOrder order : list) {
/* 2456 */         if (order.getId().equals(indexStr)) {
/* 2457 */           this.selectedOrder = order;
/* 2458 */           LOGGER.info(new StringBuilder().append("selected-").append(this.selectedOrder).toString());
/* 2459 */           break;
/*      */         }
/*      */       }
/*      */ 
/* 2463 */       this.selectedGroup = OrderMessageUtils.getOrderGroupByOrderId(String.valueOf(index));
/*      */     }
/*      */     else {
/* 2466 */       this.lastError = 1;
/* 2467 */       CommonHelpers.notSupported();
/*      */     }
/*      */ 
/* 2470 */     if ((this.selectedOrder == null) && (this.selectedGroup != null))
/*      */     {
/* 2472 */       this.selectedOrder = ((IOrder)this.selectedGroup.getOpeningOrder());
/*      */     }
/*      */ 
/* 2479 */     if (this.selectedOrder != null) {
/* 2480 */       Print(new Object[] { new StringBuilder().append("Order selected [ ").append(indexStr).append(" ]").toString() });
/* 2481 */       this.lastError = 0;
/* 2482 */       return true;
/*      */     }
/* 2484 */     this.lastError = 1;
/* 2485 */     Print(new Object[] { new StringBuilder().append("Order not selected [ ").append(indexStr).append(" ]").toString() });
/* 2486 */     return false;
/*      */   }
/*      */ 
/*      */   public int OrderType() throws JFException {
/* 2490 */     return CommonHelpers.OrderCommand2OrderType(getSelectedOrder().getOrderCommand());
/*      */   }
/*      */ 
/*      */   public int OrderMagicNumber()
/*      */     throws JFException
/*      */   {
/* 2498 */     return getIBox().getMagicNumber(this.selectedOrder);
/*      */   }
/*      */ 
/*      */   public int OrderSend(Instrument symbol, int cmd, double volume, double price, int slippage, double stoploss, double takeprofit, String comment, int magic, long expiration, Color arrow_color)
/*      */     throws JFException
/*      */   {
/* 2581 */     price = getIBox().roundHalfPip(price);
/* 2582 */     stoploss = getIBox().roundHalfPip(stoploss);
/* 2583 */     takeprofit = getIBox().roundHalfPip(takeprofit);
/* 2584 */     LOGGER.info(new StringBuilder().append("OrderSend(symbol=").append(symbol).append(", cmd=").append(cmd).append(", volume=").append(volume).append(", price=").append(price).append(", slippage=").append(slippage).append(", stoploss=").append(stoploss).append(", takeprofit=").append(takeprofit).append(", comment=").append(comment).append(", magic=").append(magic).append(", expiration=").append(expiration).append(", arrow_color=").append(arrow_color).append(")").toString());
/*      */ 
/* 2589 */     int rc = -1;
/* 2590 */     String label = getIBox().generateLabel(magic);
/* 2591 */     IEngine.OrderCommand oCommand = CommonHelpers.getOrderCommand(cmd);
/*      */ 
/* 2593 */     if ((expiration > 0L) && (cmd != 7) && (cmd != 6))
/*      */     {
/* 2595 */       CommonHelpers.notOpperationSupported();
/* 2596 */       return rc;
/*      */     }
/*      */ 
/* 2599 */     expiration *= 1000L;
/*      */     try
/*      */     {
/* 2602 */       LOGGER.info(new StringBuilder().append("submitOrder(").append(label).append(", ").append(symbol).append(", ").append(oCommand).append(", ").append(volume).append(",").append(price).append(", ").append(slippage).append(", ").append(stoploss).append(", ").append(takeprofit).append(", ").append(0).append(", ").append(comment).append(")").toString());
/*      */ 
/* 2606 */       IOrder order = getIEngine().submitOrder(label != null ? label : "", getCurrentInstrument(symbol), oCommand, volume, price, slippage, stoploss, takeprofit, expiration, comment);
/*      */ 
/* 2611 */       order.waitForUpdate(2000L);
/*      */ 
/* 2613 */       if ((order.getOrderCommand() == IEngine.OrderCommand.BUY) || (order.getOrderCommand() == IEngine.OrderCommand.SELL))
/*      */       {
/* 2615 */         if (order.getState() == IOrder.State.OPENED)
/* 2616 */           order.waitForUpdate(2000L);
/*      */       }
/*      */       try
/*      */       {
/* 2620 */         String group = order.getId();
/* 2621 */         if (group != null) {
/* 2622 */           rc = Integer.parseInt(group);
/* 2623 */           this.lastError = 0;
/*      */         }
/*      */       }
/*      */       catch (Exception e) {
/* 2627 */         this.lastError = 1;
/* 2628 */         throw new JFException(e);
/*      */       }
/*      */     }
/*      */     catch (JFException e)
/*      */     {
/* 2633 */       this.lastError = 1;
/* 2634 */       throw e;
/*      */     }
/* 2636 */     return rc;
/*      */   }
/*      */ 
/*      */   public boolean OrderModify(int ticket, double price, double stoploss, double takeprofit, long expiration, Color arrow_color)
/*      */     throws JFException
/*      */   {
/* 2654 */     price = getIBox().roundHalfPip(price);
/* 2655 */     stoploss = getIBox().roundHalfPip(stoploss);
/* 2656 */     takeprofit = getIBox().roundHalfPip(takeprofit);
/* 2657 */     expiration *= 1000L;
/*      */ 
/* 2659 */     boolean rc = false;
/* 2660 */     IOrder toModify = getIEngine().getOrderById(new StringBuilder().append(ticket).append("").toString());
/* 2661 */     if (toModify != null) {
/* 2662 */       double oldPrice = toModify.getOpenPrice();
/* 2663 */       double oldStopLoss = toModify.getStopLossPrice();
/* 2664 */       double oldTakeProfit = toModify.getTakeProfitPrice();
/* 2665 */       double oldExpiration = toModify.getGoodTillTime() / 1000L;
/*      */ 
/* 2667 */       if ((oldPrice == price) && (oldStopLoss == stoploss) && (oldTakeProfit == takeprofit))
/*      */       {
/* 2669 */         this.lastError = 1;
/* 2670 */         return rc;
/*      */       }
/* 2672 */       if ((toModify.getState() != IOrder.State.FILLED) && (oldPrice != price)) {
/* 2673 */         LOGGER.info(new StringBuilder().append("OrderModify - price change to ").append(price).toString());
/*      */         try {
/* 2675 */           toModify.setOpenPrice(price);
/* 2676 */           toModify.waitForUpdate(5000L);
/* 2677 */           rc = true;
/*      */         } catch (JFException e) {
/* 2679 */           this.lastError = 1;
/*      */ 
/* 2681 */           throw e;
/*      */         }
/*      */       }
/* 2684 */       if (oldStopLoss != stoploss) {
/* 2685 */         LOGGER.info(new StringBuilder().append("OrderModify - stoploss change to ").append(stoploss).toString());
/*      */         try {
/* 2687 */           toModify.setStopLossPrice(stoploss);
/* 2688 */           toModify.waitForUpdate(5000L);
/* 2689 */           rc = true;
/*      */         } catch (JFException e) {
/* 2691 */           this.lastError = 1;
/*      */ 
/* 2693 */           throw e;
/*      */         }
/*      */       }
/* 2696 */       if (oldTakeProfit != takeprofit) {
/* 2697 */         LOGGER.info(new StringBuilder().append("OrderModify - takeprofit change to ").append(takeprofit).toString());
/*      */         try {
/* 2699 */           toModify.setTakeProfitPrice(takeprofit);
/* 2700 */           toModify.waitForUpdate(5000L);
/* 2701 */           rc = true;
/*      */         } catch (JFException e) {
/* 2703 */           throw e;
/*      */         }
/*      */       }
/*      */ 
/* 2707 */       if ((expiration != oldExpiration) && ((toModify.getOrderCommand() == IEngine.OrderCommand.PLACE_BID) || (toModify.getOrderCommand() == IEngine.OrderCommand.PLACE_OFFER)))
/*      */       {
/* 2710 */         LOGGER.info(new StringBuilder().append("OrderModify - expiration change to ").append(expiration).toString());
/*      */         try {
/* 2712 */           toModify.setGoodTillTime(expiration);
/* 2713 */           toModify.waitForUpdate(5000L);
/* 2714 */           rc = true;
/*      */         } catch (JFException e) {
/* 2716 */           this.lastError = 1;
/*      */ 
/* 2718 */           throw e;
/*      */         }
/*      */       }
/* 2721 */       rc = true;
/*      */     }
/*      */ 
/* 2724 */     this.lastError = 0;
/* 2725 */     return rc;
/*      */   }
/*      */ 
/*      */   public double OrderSwap()
/*      */   {
/* 2734 */     return 0.0D;
/*      */   }
/*      */ 
/*      */   public long OrderExpiration()
/*      */   {
/* 2743 */     return 0L;
/*      */   }
/*      */ 
/*      */   public int GetLastError()
/*      */   {
/* 2754 */     return this.lastError;
/*      */   }
/*      */ 
/*      */   public boolean IsTradeAllowed()
/*      */   {
/* 2766 */     return true;
/*      */   }
/*      */ 
/*      */   public boolean IsTradeContextBusy()
/*      */   {
/* 2776 */     return false;
/*      */   }
/*      */ 
/*      */   public boolean IsStopped()
/*      */   {
/* 2786 */     return false;
/*      */   }
/*      */ 
/*      */   public boolean IsConnected()
/*      */   {
/* 2798 */     return true;
/*      */   }
/*      */ 
/*      */   public boolean IsOptimization()
/*      */   {
/* 2807 */     return false;
/*      */   }
/*      */ 
/*      */   public boolean IsTesting()
/*      */   {
/* 2816 */     return getIEngine().getType() == IEngine.Type.TEST;
/*      */   }
/*      */ 
/*      */   public boolean IsDemo()
/*      */   {
/* 2825 */     return getIEngine().getType() == IEngine.Type.DEMO;
/*      */   }
/*      */ 
/*      */   public boolean IsExpertEnabled()
/*      */   {
/* 2833 */     return true;
/*      */   }
/*      */ 
/*      */   public double iRSI(Instrument symbol, int timeframe, int timePeriod, int applied_price, int shift)
/*      */     throws JFException
/*      */   {
/* 2846 */     double rc = 0.0D;
/* 2847 */     int numberOfCandlesBefore = shift + 1;
/* 2848 */     long time = getBarTime(symbol, timeframe, OfferSide.BID, shift);
/* 2849 */     int numberOfCandlesAfter = 0;
/*      */     try {
/* 2851 */       IIndicators.AppliedPrice convertedAppliedPrice = CommonHelpers.convertAppliedPrice(applied_price);
/*      */ 
/* 2853 */       rc = getIndicators().rsi(getCurrentInstrument(symbol), selectPeriod(timeframe), OfferSide.BID, convertedAppliedPrice, timePeriod, Filter.ALL_FLATS, numberOfCandlesBefore, time, numberOfCandlesAfter)[0];
/*      */     }
/*      */     catch (JFException e)
/*      */     {
/* 2862 */       stopStrategy();
/* 2863 */       throw e;
/*      */     }
/* 2865 */     return rc;
/*      */   }
/*      */ 
/*      */   public double iMA(Instrument symbol, int timeframe, int timePeriod, int ma_shift, int ma_method, int applied_price, int shift)
/*      */     throws JFException
/*      */   {
/* 2896 */     double rc = (0.0D / 0.0D);
/* 2897 */     int numberOfCandlesBefore = shift + 1;
/* 2898 */     long time = getBarTime(symbol, timeframe, OfferSide.BID, shift);
/* 2899 */     int numberOfCandlesAfter = 0;
/*      */     try
/*      */     {
/* 2905 */       IIndicators.AppliedPrice convertedAppliedPrice = CommonHelpers.convertAppliedPrice(applied_price);
/*      */ 
/* 2907 */       IIndicators.MaType maType = CommonHelpers.convertMaType(ma_method);
/* 2908 */       long start = System.currentTimeMillis();
/*      */ 
/* 2910 */       double[] values = getIndicators().ma(getCurrentInstrument(symbol), selectPeriod(timeframe), OfferSide.BID, convertedAppliedPrice, timePeriod, maType, Filter.ALL_FLATS, numberOfCandlesBefore, time, numberOfCandlesAfter);
/*      */ 
/* 2915 */       long end = System.currentTimeMillis();
/*      */ 
/* 2918 */       if ((values != null) && (values.length > 0)) {
/* 2919 */         rc = values[0];
/*      */       }
/*      */ 
/*      */     }
/*      */     catch (JFException e)
/*      */     {
/* 2926 */       stopStrategy();
/* 2927 */       throw e;
/*      */     }
/* 2929 */     return rc;
/*      */   }
/*      */ 
/*      */   public double iMAOnArray(double[] array, int total, int period, int ma_shift, int ma_method, int shift)
/*      */     throws JFException
/*      */   {
/* 2959 */     double rc = 0.0D;
/*      */ 
/* 2964 */     IIndicators.MaType maType = CommonHelpers.convertMaType(ma_method);
/*      */     try
/*      */     {
/* 2967 */       IIndicator maInd = getIndicatorsProvider().getIndicator("MA");
/* 2968 */       maInd.setOptInputParameter(0, Integer.valueOf(period));
/* 2969 */       maInd.setOptInputParameter(1, Integer.valueOf(maType.ordinal()));
/* 2970 */       int lookback = maInd.getLookback();
/* 2971 */       maInd.setInputParameter(0, array);
/* 2972 */       if (total < 1) {
/* 2973 */         total = array.length;
/*      */       }
/* 2975 */       double[] outArray = new double[total];
/* 2976 */       maInd.setOutputParameter(0, outArray);
/* 2977 */       long start = System.currentTimeMillis();
/* 2978 */       maInd.calculate(lookback, total - 1);
/* 2979 */       long end = System.currentTimeMillis();
/*      */ 
/* 2982 */       rc = outArray[shift];
/*      */     }
/*      */     catch (Exception e) {
/* 2985 */       stopStrategy();
/* 2986 */       throw new JFException(e);
/*      */     }
/* 2988 */     return rc;
/*      */   }
/*      */ 
/*      */   public double iMomentum(Instrument symbol, int timeframe, int period, int applied_price, int shift)
/*      */     throws JFException
/*      */   {
/* 3012 */     double rc = 0.0D;
/* 3013 */     int numberOfCandlesBefore = shift + 1;
/* 3014 */     long time = getBarTime(symbol, timeframe, OfferSide.BID, shift);
/* 3015 */     int numberOfCandlesAfter = 0;
/* 3016 */     int timePeriod = 0;
/*      */     try {
/* 3018 */       IIndicators.AppliedPrice convertedAppliedPrice = CommonHelpers.convertAppliedPrice(applied_price);
/*      */ 
/* 3020 */       rc = getIndicators().mom(getCurrentInstrument(symbol), selectPeriod(timeframe), OfferSide.BID, convertedAppliedPrice, period, Filter.ALL_FLATS, numberOfCandlesBefore, time, numberOfCandlesAfter)[0];
/*      */     }
/*      */     catch (JFException e)
/*      */     {
/* 3029 */       stopStrategy();
/* 3030 */       throw e;
/*      */     }
/* 3032 */     return rc;
/*      */   }
/*      */ 
/*      */   public double iSAR(Instrument symbol, int timeframe, double step, double maximum, int shift)
/*      */     throws JFException
/*      */   {
/* 3038 */     double rc = 0.0D;
/* 3039 */     int numberOfCandlesBefore = shift + 1;
/* 3040 */     long time = getBarTime(symbol, timeframe, OfferSide.BID, shift);
/* 3041 */     int numberOfCandlesAfter = 0;
/* 3042 */     int timePeriod = 0;
/*      */     try {
/* 3044 */       rc = getIndicators().sar(getCurrentInstrument(symbol), selectPeriod(timeframe), OfferSide.BID, step, maximum, Filter.ALL_FLATS, numberOfCandlesBefore, time, numberOfCandlesAfter)[0];
/*      */     }
/*      */     catch (JFException e)
/*      */     {
/* 3053 */       stopStrategy();
/* 3054 */       throw e;
/*      */     }
/* 3056 */     return rc;
/*      */   }
/*      */ 
/*      */   public double iMACD(Instrument symbol, int timeframe, int fast_ema_period, int slow_ema_period, int signal_period, int applied_price, int mode, int shift)
/*      */     throws JFException
/*      */   {
/* 3067 */     double rc = 0.0D;
/* 3068 */     int numberOfCandlesBefore = shift + 1;
/* 3069 */     long time = getBarTime(symbol, timeframe, OfferSide.BID, shift);
/* 3070 */     int numberOfCandlesAfter = 0;
/* 3071 */     int timePeriod = 0;
/*      */ 
/* 3073 */     double[][] res = (double[][])null;
/*      */     try
/*      */     {
/* 3076 */       IIndicators.AppliedPrice convertedAppliedPrice = CommonHelpers.convertAppliedPrice(applied_price);
/*      */ 
/* 3080 */       signal_period = RangeHelper.getRangeAdjustedInputValue(getIndicators().getIndicator("MACD"), 2, signal_period);
/*      */ 
/* 3082 */       res = getIndicators().macd(getCurrentInstrument(symbol), selectPeriod(timeframe), OfferSide.BID, convertedAppliedPrice, fast_ema_period, slow_ema_period, signal_period, Filter.ALL_FLATS, numberOfCandlesBefore, time, numberOfCandlesAfter);
/*      */     }
/*      */     catch (JFException e)
/*      */     {
/* 3093 */       stopStrategy();
/* 3094 */       throw e;
/*      */     }
/*      */ 
/* 3097 */     if (res != null) {
/* 3098 */       if (mode == 0)
/*      */       {
/* 3100 */         rc = res[0][0];
/* 3101 */       } else if (mode == 1)
/*      */       {
/* 3103 */         rc = res[1][0];
/*      */       }
/* 3105 */       else CommonHelpers.notSupported();
/*      */     }
/*      */ 
/* 3108 */     return rc;
/*      */   }
/*      */ 
/*      */   public double iStdDev(Instrument symbol, int timeframe, int ma_period, int ma_shift, int ma_method, int applied_price, int shift)
/*      */     throws JFException
/*      */   {
/* 3126 */     double rc = 0.0D;
/* 3127 */     int numberOfCandlesBefore = shift + 1;
/* 3128 */     long time = getBarTime(symbol, timeframe, OfferSide.BID, shift);
/* 3129 */     int numberOfCandlesAfter = 0;
/* 3130 */     int timePeriod = 0;
/*      */     try {
/* 3132 */       IIndicators.AppliedPrice convertedAppliedPrice = CommonHelpers.convertAppliedPrice(applied_price);
/*      */ 
/* 3134 */       IIndicators.MaType maType = CommonHelpers.convertMaType(ma_method);
/* 3135 */       rc = getIndicators().stdDev(getCurrentInstrument(symbol), selectPeriod(timeframe), OfferSide.BID, convertedAppliedPrice, ma_period, 1.0D, Filter.ALL_FLATS, numberOfCandlesBefore, time, numberOfCandlesAfter)[0];
/*      */     }
/*      */     catch (JFException e)
/*      */     {
/* 3144 */       stopStrategy();
/* 3145 */       throw e;
/*      */     }
/* 3147 */     return rc;
/*      */   }
/*      */ 
/*      */   public double iStdDevOnArray(double[] array, int total, int ma_period, int ma_shift, int ma_method, int shift)
/*      */     throws JFException
/*      */   {
/* 3176 */     double rc = 0.0D;
/* 3177 */     int numberOfCandlesBefore = shift + 1;
/*      */ 
/* 3181 */     int numberOfCandlesAfter = 0;
/* 3182 */     int timePeriod = 0;
/*      */     try {
/* 3184 */       IIndicator maInd = getIndicators().getIndicator("STDDEV");
/* 3185 */       maInd.setOptInputParameter(0, Integer.valueOf(ma_period));
/* 3186 */       maInd.setOptInputParameter(1, Integer.valueOf(ma_shift));
/* 3187 */       maInd.setOptInputParameter(2, Integer.valueOf(ma_method));
/* 3188 */       int lookback = maInd.getLookback();
/* 3189 */       maInd.setInputParameter(0, array);
/* 3190 */       double[] outArray = new double[total];
/* 3191 */       maInd.setOutputParameter(0, outArray);
/* 3192 */       maInd.calculate(lookback, total);
/* 3193 */       rc = outArray[shift];
/*      */     }
/*      */     catch (Exception e) {
/* 3196 */       stopStrategy();
/* 3197 */       throw new JFException(e);
/*      */     }
/* 3199 */     return rc;
/*      */   }
/*      */ 
/*      */   public double iATR(Instrument symbol, int timeframe, int timePeriod, int shift)
/*      */     throws JFException
/*      */   {
/* 3221 */     double rc = 0.0D;
/* 3222 */     int numberOfCandlesBefore = shift + 1;
/* 3223 */     long time = getBarTime(symbol, timeframe, OfferSide.BID, shift);
/* 3224 */     int numberOfCandlesAfter = 0;
/*      */     try {
/* 3226 */       rc = getIndicators().atr(getCurrentInstrument(symbol), selectPeriod(timeframe), OfferSide.BID, timePeriod, Filter.ALL_FLATS, numberOfCandlesBefore, time, numberOfCandlesAfter)[0];
/*      */     }
/*      */     catch (JFException e)
/*      */     {
/* 3234 */       stopStrategy();
/* 3235 */       throw e;
/*      */     }
/* 3237 */     return rc;
/*      */   }
/*      */ 
/*      */   public double iADX(Instrument symbol, int timeframe, int timePeriod, int applied_price, int mode, int shift)
/*      */     throws JFException
/*      */   {
/* 3264 */     double rc = 0.0D;
/* 3265 */     int numberOfCandlesBefore = shift + 1;
/* 3266 */     long time = getBarTime(symbol, timeframe, OfferSide.BID, shift);
/* 3267 */     int numberOfCandlesAfter = 0;
/*      */     try {
/* 3269 */       rc = getIndicators().adx(getCurrentInstrument(symbol), selectPeriod(timeframe), OfferSide.BID, timePeriod, Filter.ALL_FLATS, numberOfCandlesBefore, time, numberOfCandlesAfter)[0];
/*      */     }
/*      */     catch (JFException e)
/*      */     {
/* 3277 */       stopStrategy();
/* 3278 */       throw e;
/*      */     }
/* 3280 */     return rc;
/*      */   }
/*      */ 
/*      */   public double iDeMarker(Instrument symbol, int timeframe, int timePeriod, int shift)
/*      */     throws JFException
/*      */   {
/* 3301 */     double rc = 0.0D;
/* 3302 */     int numberOfCandlesBefore = shift + 1;
/* 3303 */     long time = getBarTime(symbol, timeframe, OfferSide.BID, shift);
/* 3304 */     int numberOfCandlesAfter = 0;
/*      */     try
/*      */     {
/* 3307 */       rc = getIndicators().dema(getCurrentInstrument(symbol), selectPeriod(timeframe), OfferSide.BID, IIndicators.AppliedPrice.MEDIAN_PRICE, timePeriod, Filter.ALL_FLATS, numberOfCandlesBefore, time, numberOfCandlesAfter)[0];
/*      */     }
/*      */     catch (JFException e)
/*      */     {
/* 3321 */       stopStrategy();
/* 3322 */       throw e;
/*      */     }
/* 3324 */     return rc;
/*      */   }
/*      */ 
/*      */   public double iBands(Instrument symbol, int timeframe, int timePeriod, int deviation, int bands_shift, int applied_price, int mode, int shift)
/*      */     throws JFException
/*      */   {
/* 3356 */     double[] rc = { (0.0D / 0.0D) };
/* 3357 */     int numberOfCandlesBefore = shift + 1;
/* 3358 */     long time = getBarTime(symbol, timeframe, OfferSide.BID, shift);
/* 3359 */     int numberOfCandlesAfter = 0;
/* 3360 */     double[][] res = (double[][])null;
/*      */     try {
/* 3362 */       IIndicators.AppliedPrice convertedAppliedPrice = CommonHelpers.convertAppliedPrice(applied_price);
/*      */ 
/* 3364 */       res = getIndicators().bbands(getCurrentInstrument(symbol), selectPeriod(timeframe), OfferSide.BID, convertedAppliedPrice, timePeriod, deviation, deviation, IIndicators.MaType.SMA, Filter.ALL_FLATS, numberOfCandlesBefore, time, numberOfCandlesAfter);
/*      */     }
/*      */     catch (JFException e)
/*      */     {
/* 3371 */       stopStrategy();
/* 3372 */       throw e;
/*      */     }
/* 3374 */     if (res != null) {
/* 3375 */       if (mode == 1)
/* 3376 */         rc = res[0];
/* 3377 */       else if (mode == 2)
/* 3378 */         rc = res[2];
/*      */       else {
/* 3380 */         CommonHelpers.notSupported();
/*      */       }
/*      */     }
/* 3383 */     return rc[0];
/*      */   }
/*      */ 
/*      */   public double iStochastic(Instrument symbol, int timeframe, int kPeriod, int dPeriod, int slowing, int method, int price_field, int mode, int shift)
/*      */     throws JFException
/*      */   {
/* 3419 */     double rc = 0.0D;
/* 3420 */     int numberOfCandlesBefore = shift + 1;
/* 3421 */     long time = getBarTime(symbol, timeframe, OfferSide.BID, shift);
/* 3422 */     int numberOfCandlesAfter = 0;
/* 3423 */     int timePeriod = 0;
/*      */ 
/* 3425 */     IIndicators.MaType maType = CommonHelpers.convertMaType(method);
/*      */ 
/* 3427 */     double[][] resArray = (double[][])null;
/*      */     try {
/* 3429 */       resArray = getIndicators().stoch(getCurrentInstrument(symbol), selectPeriod(timeframe), OfferSide.BID, kPeriod, slowing, maType, dPeriod, maType, Filter.ALL_FLATS, numberOfCandlesBefore, time, numberOfCandlesAfter);
/*      */     }
/*      */     catch (JFException e)
/*      */     {
/* 3437 */       e.printStackTrace();
/* 3438 */       stopStrategy();
/*      */     }
/*      */ 
/* 3441 */     if (resArray != null) {
/* 3442 */       if ((mode == 0) && (resArray.length > 0)) {
/* 3443 */         rc = resArray[0][0];
/*      */       }
/* 3445 */       else if ((mode == 1) && (resArray.length > 1))
/* 3446 */         rc = resArray[1][0];
/*      */       else {
/* 3448 */         CommonHelpers.notSupported();
/*      */       }
/*      */     }
/* 3451 */     return rc;
/*      */   }
/*      */ 
/*      */   public int Period()
/*      */   {
/* 3461 */     return (int)getPeriod().getInterval() / 60000;
/*      */   }
/*      */ 
/*      */   public Period getPeriod() {
/* 3465 */     Period period = Period.TICK;
/* 3466 */     if (this.strategy != null) {
/* 3467 */       period = this.strategy.getCurrentPeriod();
/*      */     }
/* 3469 */     if (this.indicator != null) {
/* 3470 */       period = getChartSelectedPeriod();
/*      */     }
/* 3472 */     return period;
/*      */   }
/*      */ 
/*      */   public boolean RefreshRates()
/*      */   {
/* 3484 */     return false;
/*      */   }
/*      */ 
/*      */   public Instrument Instrument()
/*      */   {
/* 3489 */     if (this.strategy != null) {
/* 3490 */       return getCurrentInstrument(null);
/*      */     }
/* 3492 */     return getChartSelectedInstrument();
/*      */   }
/*      */ 
/*      */   public String Symbol()
/*      */   {
/* 3497 */     return Instrument().toString();
/*      */   }
/*      */ 
/*      */   public OfferSide getOfferSide() {
/* 3501 */     return OfferSide.ASK;
/*      */   }
/*      */ 
/*      */   public String WindowExpertName()
/*      */   {
/* 3511 */     return getClass().getSimpleName();
/*      */   }
/*      */ 
/*      */   public void HideTestIndicators(boolean hide)
/*      */   {
/*      */   }
/*      */ 
/*      */   private Period selectPeriod(int timeframe)
/*      */   {
/* 3530 */     Period rc = null;
/* 3531 */     rc = convertPeriod(timeframe);
/* 3532 */     if (rc == null) {
/* 3533 */       rc = getAbstractConnector().getCurrentPeriod();
/*      */     }
/* 3535 */     return rc;
/*      */   }
/*      */ 
/*      */   private Period convertPeriod(int timeframe) {
/* 3539 */     Period rc = null;
/* 3540 */     if (this.strategy != null) {
/* 3541 */       if (timeframe != 0)
/* 3542 */         rc = CommonHelpers.convertPeriod(timeframe);
/*      */       else {
/* 3544 */         rc = this.strategy.getCurrentPeriod();
/*      */       }
/*      */     }
/*      */     else {
/* 3548 */       rc = getChartsController().getIChartBy(Integer.valueOf(getChartPanelId())).getSelectedPeriod();
/*      */     }
/*      */ 
/* 3551 */     return rc;
/*      */   }
/*      */ 
/*      */   private int FileOpenPath(String filename, File path, int mode, char delimiter)
/*      */   {
/* 3558 */     int rc = -1;
/* 3559 */     if ((filename == null) || (filename.indexOf(File.separator) != -1) || (filename.indexOf(File.pathSeparator) != -1))
/*      */     {
/* 3561 */       return rc;
/*      */     }
/*      */     try
/*      */     {
/* 3565 */       File theFile = new File(new StringBuilder().append(path).append(File.separator).append(filename).toString());
/*      */ 
/* 3567 */       String modeStr = "r";
/*      */ 
/* 3569 */       BitSet bitSet = CommonHelpers.bitSetFromInt(mode);
/*      */ 
/* 3571 */       if (((bitSet.get(2)) && (bitSet.get(3))) || ((!bitSet.get(2)) && (!bitSet.get(3))))
/*      */       {
/* 3573 */         int i = -1;
/*      */         return i;
/*      */       }
/*      */       File dir;
/*      */       int j;
/* 3577 */       if ((bitSet.get(0)) && (bitSet.get(1)))
/*      */       {
/* 3579 */         if (!theFile.exists())
/*      */         {
/* 3581 */           if (bitSet.get(1)) {
/* 3582 */             dir = theFile.getParentFile();
/* 3583 */             dir.mkdirs();
/* 3584 */             if (!theFile.createNewFile()) {
/* 3585 */               int k = rc;
/*      */               return k;
/*      */             }
/*      */           } else {
/* 3588 */             j = rc;
/*      */             return j;
/*      */           }
/*      */         }
/*      */ 
/* 3592 */         if (bitSet.get(1)) {
/* 3593 */           modeStr = "rw";
/*      */         }
/*      */       }
/* 3596 */       else if (bitSet.get(1))
/*      */       {
/* 3598 */         if ((theFile.exists()) && 
/* 3599 */           (!theFile.delete())) {
/* 3600 */           j = rc;
/*      */           return j;
/*      */         }
/*      */ 
/* 3603 */         if (!theFile.createNewFile()) {
/* 3604 */           j = rc;
/*      */           return j;
/*      */         }
/* 3606 */         modeStr = "rw";
/*      */       }
/*      */ 
/* 3611 */       rc = generateFileHandler(bitSet.get(2));
/* 3612 */       if (this.filesMap.size() > 32) {
/* 3613 */         j = -1;
/*      */         return j;
/*      */       }
/*      */ 
/* 3616 */       RandomAccessFile accessFile = new RandomAccessFile(theFile, modeStr);
/* 3617 */       this.filesMap.put(Integer.valueOf(rc), accessFile);
/* 3618 */       if (bitSet.get(3))
/* 3619 */         this.filesDelimiters.put(Integer.valueOf(rc), Character.valueOf(delimiter));
/*      */     }
/*      */     catch (Exception e) {
/* 3622 */       LOGGER.error("", e);
/*      */     }
/*      */     finally {
/*      */     }
/* 3626 */     return rc;
/*      */   }
/*      */ 
/*      */   public int FileOpenHistory(String filename, int mode, char delimiter)
/*      */   {
/* 3655 */     return FileOpenPath(filename, FileHelpers.getFilesForHistoryDir(), mode, delimiter);
/*      */   }
/*      */ 
/*      */   public int FileWriteString(int handle, String value, int length)
/*      */   {
/* 3677 */     int result = -1;
/*      */     try {
/* 3679 */       Character delimiter = null;
/* 3680 */       if (FileHelpers.isHandlerForBinFile(handle));
/* 3682 */       RandomAccessFile accessFile = (RandomAccessFile)this.filesMap.get(Integer.valueOf(handle));
/* 3683 */       delimiter = (Character)this.filesDelimiters.get(Integer.valueOf(handle));
/*      */ 
/* 3685 */       result = FileHelpers.FileWriteString(accessFile, value, length, delimiter);
/*      */     }
/*      */     catch (IOException e)
/*      */     {
/* 3689 */       e.printStackTrace();
/*      */     }
/* 3691 */     return result;
/*      */   }
/*      */ 
/*      */   public int FileWriteArray(int handle, Object array, int start, int count)
/*      */   {
/* 3716 */     return FileHelpers.FileWriteArray((RandomAccessFile)this.filesMap.get(Integer.valueOf(handle)), array, start, count);
/*      */   }
/*      */ 
/*      */   public void FileFlush(int handle)
/*      */   {
/* 3731 */     RandomAccessFile accessFile = (RandomAccessFile)this.filesMap.get(Integer.valueOf(handle));
/*      */ 
/* 3733 */     if (accessFile != null)
/*      */       try {
/* 3735 */         Writer out = new OutputStreamWriter(new FileOutputStream(accessFile.getFD()), "UTF-8");
/*      */ 
/* 3737 */         out.flush();
/*      */       } catch (Exception e) {
/* 3739 */         e.printStackTrace();
/*      */       }
/*      */   }
/*      */ 
/*      */   public void FileDelete(String filename)
/*      */   {
/* 3754 */     this.lastError = 0;
/* 3755 */     boolean success = new File(filename).delete();
/* 3756 */     if (!success)
/* 3757 */       this.lastError = 2;
/*      */   }
/*      */ 
/*      */   public boolean FileIsEnding(int handle)
/*      */   {
/* 3771 */     boolean result = false;
/* 3772 */     RandomAccessFile accessFile = (RandomAccessFile)this.filesMap.get(Integer.valueOf(handle));
/*      */ 
/* 3774 */     if (accessFile != null) {
/*      */       try {
/* 3776 */         if (accessFile.getChannel().size() == accessFile.getChannel().position())
/*      */         {
/* 3778 */           result = true;
/*      */         }
/*      */       } catch (Exception e) {
/* 3781 */         e.printStackTrace();
/*      */       }
/*      */     }
/* 3784 */     return result;
/*      */   }
/*      */ 
/*      */   public boolean FileIsLineEnding(int handle)
/*      */   {
/* 3797 */     boolean result = false;
/* 3798 */     RandomAccessFile accessFile = (RandomAccessFile)this.filesMap.get(Integer.valueOf(handle));
/*      */ 
/* 3800 */     if (accessFile != null) {
/*      */       try {
/* 3802 */         long current_possition = accessFile.getChannel().position();
/* 3803 */         char c = (char)accessFile.read();
/* 3804 */         if (c == '\n') {
/* 3805 */           result = true;
/*      */         }
/* 3807 */         accessFile.getChannel().position(current_possition);
/*      */       } catch (Exception e) {
/* 3809 */         e.printStackTrace();
/*      */       }
/*      */     }
/* 3812 */     return result;
/*      */   }
/*      */ 
/*      */   public int FileReadArray(int handle, Object array, int start, int count)
/*      */   {
/* 3831 */     return FileHelpers.FileReadArray((RandomAccessFile)this.filesMap.get(Integer.valueOf(handle)), array, start, count);
/*      */   }
/*      */ 
/*      */   public double FileReadDouble(int handle, int size)
/*      */   {
/* 3849 */     double result = 0.0D;
/* 3850 */     RandomAccessFile accessFile = (RandomAccessFile)this.filesMap.get(Integer.valueOf(handle));
/*      */ 
/* 3852 */     if (accessFile != null) {
/*      */       try {
/* 3854 */         if (size == 8)
/* 3855 */           result = accessFile.readDouble();
/*      */         else
/* 3857 */           result = accessFile.readFloat();
/*      */       }
/*      */       catch (Exception e) {
/* 3860 */         e.printStackTrace();
/*      */       }
/*      */     }
/* 3863 */     return result;
/*      */   }
/*      */ 
/*      */   public int FileReadInteger(int handle, int size)
/*      */   {
/* 3880 */     int result = 0;
/* 3881 */     RandomAccessFile accessFile = (RandomAccessFile)this.filesMap.get(Integer.valueOf(handle));
/*      */ 
/* 3883 */     if (accessFile != null) {
/*      */       try {
/* 3885 */         if (size == 1)
/* 3886 */           result = accessFile.readByte();
/* 3887 */         else if (size == 2)
/* 3888 */           result = accessFile.readShort();
/*      */         else
/* 3890 */           result = accessFile.readInt();
/*      */       }
/*      */       catch (Exception e) {
/* 3893 */         e.printStackTrace();
/*      */       }
/*      */     }
/* 3896 */     return result;
/*      */   }
/*      */ 
/*      */   public double FileReadNumber(int handle)
/*      */   {
/* 3909 */     double result = 0.0D;
/* 3910 */     if (FileHelpers.isHandlerForBinFile(handle)) {
/* 3911 */       return result;
/*      */     }
/* 3913 */     RandomAccessFile accessFile = (RandomAccessFile)this.filesMap.get(Integer.valueOf(handle));
/* 3914 */     Character delimiter = (Character)this.filesDelimiters.get(Integer.valueOf(handle));
/* 3915 */     StringBuilder rc = new StringBuilder();
/* 3916 */     if (accessFile != null) {
/*      */       try {
/* 3918 */         char c = 65535;
/*      */         do {
/* 3920 */           if (c != 65535) {
/* 3921 */             rc.append(c);
/*      */           }
/* 3923 */           c = (char)accessFile.read();
/*      */         }
/*      */ 
/* 3926 */         while ((c != delimiter.charValue()) && (c != '\n') && (c != 65535));
/* 3927 */         result = MathHelpers.strToDouble(rc.toString().trim());
/*      */       } catch (Exception e) {
/* 3929 */         e.printStackTrace();
/*      */       }
/*      */     }
/*      */ 
/* 3933 */     return result;
/*      */   }
/*      */ 
/*      */   public int FileTell(int handle)
/*      */   {
/* 3944 */     Long result = new Long(0L);
/* 3945 */     RandomAccessFile accessFile = (RandomAccessFile)this.filesMap.get(Integer.valueOf(handle));
/*      */ 
/* 3947 */     if (accessFile != null) {
/*      */       try {
/* 3949 */         result = Long.valueOf(accessFile.getChannel().position());
/*      */       } catch (Exception e) {
/* 3951 */         e.printStackTrace();
/*      */       }
/*      */     }
/* 3954 */     return result.intValue();
/*      */   }
/*      */ 
/*      */   public int FileWriteDouble(int handle, double value, int size)
/*      */   {
/* 3975 */     int result = 0;
/* 3976 */     RandomAccessFile accessFile = (RandomAccessFile)this.filesMap.get(Integer.valueOf(handle));
/*      */ 
/* 3978 */     if (accessFile != null) {
/*      */       try {
/* 3980 */         if (size == 8)
/* 3981 */           accessFile.writeDouble(value);
/*      */         else
/* 3983 */           accessFile.writeFloat(Double.valueOf(value).floatValue());
/*      */       }
/*      */       catch (Exception e) {
/* 3986 */         e.printStackTrace();
/*      */       }
/*      */     }
/* 3989 */     return result;
/*      */   }
/*      */ 
/*      */   public int FileWriteInteger(int handle, int value, int size)
/*      */   {
/* 4011 */     int result = 0;
/* 4012 */     RandomAccessFile accessFile = (RandomAccessFile)this.filesMap.get(Integer.valueOf(handle));
/*      */ 
/* 4014 */     if (accessFile != null) {
/*      */       try {
/* 4016 */         if (size == 4)
/* 4017 */           accessFile.writeInt(value);
/*      */         else
/* 4019 */           accessFile.writeShort(value);
/*      */       }
/*      */       catch (Exception e) {
/* 4022 */         e.printStackTrace();
/*      */       }
/*      */     }
/* 4025 */     return result;
/*      */   }
/*      */ 
/*      */   public double iAC(Instrument symbol, int timeframe, int shift)
/*      */     throws JFException
/*      */   {
/* 4049 */     double result = (0.0D / 0.0D);
/*      */     try {
/* 4051 */       int numberOfCandlesBefore = shift + 1;
/* 4052 */       long time = getBarTime(symbol, timeframe, OfferSide.BID, shift);
/* 4053 */       int numberOfCandlesAfter = 0;
/* 4054 */       int timePeriod = 5;
/* 4055 */       int slowPeriod = 34;
/* 4056 */       result = getIndicators().ac(getCurrentInstrument(symbol), selectPeriod(timeframe), OfferSide.BID, IIndicators.AppliedPrice.MEDIAN_PRICE, timePeriod, slowPeriod, Filter.ALL_FLATS, numberOfCandlesBefore, time, numberOfCandlesAfter)[0][0];
/*      */     }
/*      */     catch (JFException e)
/*      */     {
/* 4065 */       stopStrategy();
/* 4066 */       throw e;
/*      */     }
/* 4068 */     return result;
/*      */   }
/*      */ 
/*      */   public double iAD(Instrument symbol, int timeframe, int shift)
/*      */     throws JFException
/*      */   {
/* 4088 */     double result = (0.0D / 0.0D);
/*      */     try {
/* 4090 */       int numberOfCandlesBefore = shift + 1;
/* 4091 */       long time = getBarTime(symbol, timeframe, OfferSide.BID, shift);
/* 4092 */       int numberOfCandlesAfter = 0;
/* 4093 */       result = getIndicators().ad(getCurrentInstrument(symbol), selectPeriod(timeframe), OfferSide.BID, Filter.ALL_FLATS, numberOfCandlesBefore, time, numberOfCandlesAfter)[0];
/*      */     }
/*      */     catch (JFException e)
/*      */     {
/* 4101 */       stopStrategy();
/* 4102 */       throw e;
/*      */     }
/* 4104 */     return result;
/*      */   }
/*      */ 
/*      */   public double iAlligator(Instrument symbol, int timeframe, int jaw_period, int jaw_shift, int teeth_period, int teeth_shift, int lips_period, int lips_shift, int ma_method, int applied_price, int mode, int shift)
/*      */     throws JFException
/*      */   {
/* 4149 */     double result = (0.0D / 0.0D);
/* 4150 */     IIndicators.AppliedPrice appliedPrice = CommonHelpers.convertAppliedPrice(applied_price);
/*      */ 
/* 4154 */     int numberOfCandlesBefore = shift + 1;
/* 4155 */     long time = getBarTime(symbol, timeframe, OfferSide.BID, shift);
/* 4156 */     int numberOfCandlesAfter = 0;
/*      */     try {
/* 4158 */       result = getIndicators().alligator(getCurrentInstrument(symbol), selectPeriod(timeframe), OfferSide.BID, appliedPrice, jaw_period, teeth_period, lips_period, Filter.ALL_FLATS, numberOfCandlesBefore, time, numberOfCandlesAfter)[0][0];
/*      */     }
/*      */     catch (JFException e)
/*      */     {
/* 4167 */       stopStrategy();
/* 4168 */       throw e;
/*      */     }
/* 4170 */     return result;
/*      */   }
/*      */ 
/*      */   public double iAO(Instrument symbol, int timeframe, int shift)
/*      */     throws JFException
/*      */   {
/* 4190 */     double result = (0.0D / 0.0D);
/* 4191 */     int numberOfCandlesBefore = shift + 1;
/* 4192 */     long time = getBarTime(symbol, timeframe, OfferSide.BID, shift);
/* 4193 */     int numberOfCandlesAfter = 0;
/*      */     try {
/* 4195 */       result = getIndicators().awesome(getCurrentInstrument(symbol), selectPeriod(timeframe), OfferSide.BID, IIndicators.AppliedPrice.MEDIAN_PRICE, 4, IIndicators.MaType.SMA, 10, IIndicators.MaType.SMA, Filter.ALL_FLATS, numberOfCandlesBefore, time, numberOfCandlesAfter)[0][0];
/*      */     }
/*      */     catch (JFException e)
/*      */     {
/* 4206 */       stopStrategy();
/* 4207 */       throw e;
/*      */     }
/* 4209 */     return result;
/*      */   }
/*      */ 
/*      */   public double iBearsPower(Instrument symbol, int timeframe, int period, int applied_price, int shift)
/*      */     throws JFException
/*      */   {
/* 4234 */     double result = (0.0D / 0.0D);
/* 4235 */     IIndicators.AppliedPrice appliedPrice = CommonHelpers.convertAppliedPrice(applied_price);
/*      */ 
/* 4237 */     int numberOfCandlesBefore = shift + 1;
/* 4238 */     long time = getBarTime(symbol, timeframe, OfferSide.BID, shift);
/* 4239 */     int numberOfCandlesAfter = 0;
/*      */     try {
/* 4241 */       result = getIndicators().bear(getCurrentInstrument(symbol), selectPeriod(timeframe), OfferSide.BID, period, Filter.ALL_FLATS, numberOfCandlesBefore, time, numberOfCandlesAfter)[0];
/*      */     }
/*      */     catch (JFException e)
/*      */     {
/* 4247 */       stopStrategy();
/* 4248 */       throw e;
/*      */     }
/* 4250 */     return result;
/*      */   }
/*      */ 
/*      */   public double iBandsOnArray(double[] array, int total, int period, int deviation, int bands_shift, int mode, int shift)
/*      */     throws JFException
/*      */   {
/* 4285 */     double rc = (0.0D / 0.0D);
/*      */     try
/*      */     {
/* 4288 */       IIndicator maInd = getIndicators().getIndicator("BBANDS");
/* 4289 */       maInd.setOptInputParameter(0, Integer.valueOf(period));
/* 4290 */       int lookback = maInd.getLookback();
/* 4291 */       maInd.setInputParameter(0, array);
/* 4292 */       if (total < 1) {
/* 4293 */         total = array.length;
/*      */       }
/* 4295 */       double[] outArray = new double[total];
/* 4296 */       maInd.setOutputParameter(0, outArray);
/* 4297 */       maInd.calculate(lookback, total - 1);
/* 4298 */       rc = outArray[shift];
/*      */     }
/*      */     catch (Exception e)
/*      */     {
/* 4302 */       stopStrategy();
/* 4303 */       throw new JFException(e);
/*      */     }
/* 4305 */     return rc;
/*      */   }
/*      */ 
/*      */   public double iBullsPower(Instrument symbol, int timeframe, int period, int applied_price, int shift)
/*      */     throws JFException
/*      */   {
/* 4330 */     double result = (0.0D / 0.0D);
/* 4331 */     IIndicators.AppliedPrice appliedPrice = CommonHelpers.convertAppliedPrice(applied_price);
/*      */ 
/* 4333 */     int numberOfCandlesBefore = shift + 1;
/* 4334 */     long time = getBarTime(symbol, timeframe, OfferSide.BID, shift);
/* 4335 */     int numberOfCandlesAfter = 0;
/* 4336 */     int timePeriod = 0;
/*      */     try {
/* 4338 */       result = getIndicators().bull(getCurrentInstrument(symbol), selectPeriod(timeframe), OfferSide.BID, period, Filter.ALL_FLATS, numberOfCandlesBefore, time, numberOfCandlesAfter)[0];
/*      */     }
/*      */     catch (JFException e)
/*      */     {
/* 4346 */       stopStrategy();
/* 4347 */       throw e;
/*      */     }
/* 4349 */     return result;
/*      */   }
/*      */ 
/*      */   public double iCCI(Instrument symbol, int timeframe, int period, int applied_price, int shift)
/*      */     throws JFException
/*      */   {
/* 4374 */     double result = (0.0D / 0.0D);
/* 4375 */     IIndicators.AppliedPrice appliedPrice = CommonHelpers.convertAppliedPrice(applied_price);
/*      */ 
/* 4377 */     int numberOfCandlesBefore = shift + 1;
/* 4378 */     long time = getBarTime(symbol, timeframe, OfferSide.BID, shift);
/* 4379 */     int numberOfCandlesAfter = 0;
/* 4380 */     int timePeriod = 0;
/*      */     try {
/* 4382 */       result = getIndicators().cci(getCurrentInstrument(symbol), selectPeriod(timeframe), OfferSide.BID, period, Filter.ALL_FLATS, numberOfCandlesBefore, time, numberOfCandlesAfter)[0];
/*      */     }
/*      */     catch (JFException e)
/*      */     {
/* 4391 */       stopStrategy();
/* 4392 */       throw e;
/*      */     }
/* 4394 */     return result;
/*      */   }
/*      */ 
/*      */   public double iCCIOnArray(double[] array, int total, int period, int shift)
/*      */     throws JFException
/*      */   {
/* 4418 */     double rc = (0.0D / 0.0D);
/*      */     try {
/* 4420 */       IIndicator maInd = getIndicators().getIndicator("CCI");
/* 4421 */       maInd.setOptInputParameter(0, Integer.valueOf(period));
/* 4422 */       int lookback = maInd.getLookback();
/* 4423 */       maInd.setInputParameter(0, array);
/* 4424 */       if (total < 1) {
/* 4425 */         total = array.length;
/*      */       }
/* 4427 */       double[] outArray = new double[total];
/* 4428 */       maInd.setOutputParameter(0, outArray);
/* 4429 */       maInd.calculate(lookback, total - 1);
/* 4430 */       rc = outArray[shift];
/*      */     }
/*      */     catch (Exception e) {
/* 4433 */       stopStrategy();
/* 4434 */       throw new JFException(e);
/*      */     }
/* 4436 */     return rc;
/*      */   }
/*      */ 
/*      */   public double iCustom(Instrument symbol, int timeframe, String name, int mode, int shift, Object[] custom)
/*      */     throws JFException
/*      */   {
/* 4468 */     double result = (0.0D / 0.0D);
/*      */ 
/* 4475 */     IIndicator indicator = null;
/*      */     try {
/* 4477 */       indicator = getIndicators().getIndicator(name);
/*      */     } catch (Exception ex) {
/* 4479 */       throw new JFException(new StringBuilder().append("Cant't find [").append(name).append("] indicator.").toString());
/*      */     }
/*      */ 
/* 4482 */     int numberOfInputs = indicator.getIndicatorInfo().getNumberOfOptionalInputs();
/* 4483 */     Object[] optParams = new Object[numberOfInputs];
/* 4484 */     if ((custom != null) && (custom.length > 0)) {
/* 4485 */       for (int i = 0; i < numberOfInputs; i++) {
/* 4486 */         if (custom.length > i)
/* 4487 */           optParams[i] = custom[i];
/*      */         else
/*      */           try {
/* 4490 */             optParams[i] = indicator.getClass().getField(indicator.getOptInputParameterInfo(i).getName()).get(indicator);
/*      */           }
/*      */           catch (IllegalArgumentException e) {
/* 4493 */             e.printStackTrace();
/*      */           }
/*      */           catch (SecurityException e) {
/* 4496 */             e.printStackTrace();
/*      */           }
/*      */           catch (IllegalAccessException e) {
/* 4499 */             e.printStackTrace();
/*      */           }
/*      */           catch (NoSuchFieldException e) {
/* 4502 */             e.printStackTrace();
/*      */           }
/*      */       }
/*      */     }
/*      */     else {
/* 4507 */       for (int i = 0; i < numberOfInputs; i++) {
/*      */         try {
/* 4509 */           optParams[i] = indicator.getClass().getField(indicator.getOptInputParameterInfo(i).getName()).get(indicator);
/*      */         }
/*      */         catch (IllegalArgumentException e) {
/* 4512 */           e.printStackTrace();
/*      */         }
/*      */         catch (SecurityException e) {
/* 4515 */           e.printStackTrace();
/*      */         }
/*      */         catch (IllegalAccessException e) {
/* 4518 */           e.printStackTrace();
/*      */         }
/*      */         catch (NoSuchFieldException e) {
/* 4521 */           e.printStackTrace();
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*      */     try
/*      */     {
/* 4535 */       Object[] ret = getIndicators().calculateIndicator(getCurrentInstrument(symbol), selectPeriod(timeframe), new OfferSide[] { OfferSide.BID }, name, new IIndicators.AppliedPrice[] { IIndicators.AppliedPrice.TYPICAL_PRICE }, optParams, shift);
/*      */ 
/* 4544 */       if ((ret != null) && (ret.length > 0)) {
/* 4545 */         double[] indicatorResult = new double[ret.length];
/* 4546 */         for (int i = 0; i < ret.length; i++) {
/*      */           try {
/* 4548 */             indicatorResult[i] = Array.getDouble(ret, i);
/* 4549 */             System.out.println(new StringBuilder().append("iCuston result [").append(i).append("] = ").append(indicatorResult[i]).toString());
/*      */           } catch (Exception ex) {
/* 4551 */             indicatorResult[i] = 0.0D;
/*      */           }
/*      */         }
/* 4554 */         result = indicatorResult[0];
/*      */       } else {
/* 4556 */         throw new JFException("iCustom indicator result is null");
/*      */       }
/*      */     } catch (JFException e) {
/* 4559 */       stopStrategy();
/* 4560 */       throw e;
/*      */     }
/* 4562 */     return result;
/*      */   }
/*      */ 
/*      */   public double iEnvelopes(Instrument symbol, int timeframe, int ma_period, int ma_method, int ma_shift, int applied_price, double deviation, int mode, int shift)
/*      */     throws JFException
/*      */   {
/* 4599 */     double result = (0.0D / 0.0D);
/*      */ 
/* 4602 */     int numberOfCandlesBefore = shift + 1;
/* 4603 */     long time = getBarTime(symbol, timeframe, OfferSide.BID, shift);
/* 4604 */     int numberOfCandlesAfter = 0;
/* 4605 */     int timePeriod = 0;
/*      */ 
/* 4607 */     Object[] optParams = new Object[2];
/* 4608 */     optParams[0] = Integer.valueOf(ma_period);
/* 4609 */     optParams[1] = Double.valueOf(deviation);
/*      */     try
/*      */     {
/* 4612 */       Object[] ret = getIndicators().calculateIndicator(getCurrentInstrument(symbol), selectPeriod(timeframe), new OfferSide[] { OfferSide.BID }, "MAEnvelope", new IIndicators.AppliedPrice[] { IIndicators.AppliedPrice.TYPICAL_PRICE }, optParams, shift);
/*      */ 
/* 4620 */       if ((ret != null) && (ret.length > 0)) {
/* 4621 */         double[] maEnvelope = { ((Double)ret[0]).doubleValue(), ((Double)ret[1]).doubleValue() };
/*      */ 
/* 4623 */         result = maEnvelope[0];
/*      */       } else {
/* 4625 */         throw new JFException("iEnvelopes operation result is null");
/*      */       }
/*      */     } catch (JFException e) {
/* 4628 */       stopStrategy();
/* 4629 */       throw e;
/*      */     }
/* 4631 */     return result;
/*      */   }
/*      */ 
/*      */   public double iEnvelopesOnArray(double[] array, int total, int ma_period, int ma_method, int ma_shift, double deviation, int mode, int shift)
/*      */     throws JFException
/*      */   {
/* 4667 */     double rc = (0.0D / 0.0D);
/*      */     try {
/* 4669 */       IIndicator maInd = getIndicators().getIndicator("MAEnvelope");
/* 4670 */       maInd.setOptInputParameter(0, Integer.valueOf(ma_period));
/* 4671 */       maInd.setOptInputParameter(1, Double.valueOf(deviation));
/* 4672 */       int lookback = maInd.getLookback();
/* 4673 */       maInd.setInputParameter(0, array);
/* 4674 */       if (total < 1) {
/* 4675 */         total = array.length;
/*      */       }
/* 4677 */       double[] outArray = new double[total];
/* 4678 */       maInd.setOutputParameter(0, outArray);
/* 4679 */       maInd.calculate(lookback, total - 1);
/* 4680 */       rc = outArray[shift];
/*      */     }
/*      */     catch (Exception e) {
/* 4683 */       stopStrategy();
/* 4684 */       throw new JFException(e);
/*      */     }
/* 4686 */     return rc;
/*      */   }
/*      */ 
/*      */   public double iForce(Instrument symbol, int timeframe, int period, int ma_method, int applied_price, int shift)
/*      */     throws JFException
/*      */   {
/* 4714 */     double[] result = new double[0];
/* 4715 */     int numberOfCandlesBefore = shift + 1;
/* 4716 */     long time = getBarTime(symbol, timeframe, OfferSide.BID, shift);
/* 4717 */     int numberOfCandlesAfter = 0;
/* 4718 */     int timePeriod = 0;
/*      */ 
/* 4720 */     IIndicators.AppliedPrice appliedPrice = CommonHelpers.convertAppliedPrice(applied_price);
/*      */ 
/* 4722 */     IIndicators.MaType maType = CommonHelpers.convertMaType(ma_method);
/*      */     try {
/* 4724 */       result = getIndicators().force(getCurrentInstrument(symbol), selectPeriod(timeframe), OfferSide.BID, appliedPrice, period, maType, Filter.ALL_FLATS, numberOfCandlesBefore, time, numberOfCandlesAfter);
/*      */     }
/*      */     catch (JFException e)
/*      */     {
/* 4733 */       stopStrategy();
/* 4734 */       throw e;
/*      */     }
/* 4736 */     if ((shift < 0) || (shift > result.length - 1)) {
/* 4737 */       shift = 0;
/*      */     }
/* 4739 */     return result[shift];
/*      */   }
/*      */ 
/*      */   public double iFractals(Instrument symbol, int timeframe, int mode, int shift)
/*      */     throws JFException
/*      */   {
/* 4762 */     double result = (0.0D / 0.0D);
/* 4763 */     int numberOfCandlesBefore = shift + 1;
/* 4764 */     long time = getBarTime(symbol, timeframe, OfferSide.BID, shift);
/* 4765 */     int numberOfCandlesAfter = 0;
/* 4766 */     int timePeriod = 0;
/*      */     try {
/* 4768 */       result = getIndicators().fractal(getCurrentInstrument(symbol), selectPeriod(timeframe), OfferSide.BID, mode, Filter.ALL_FLATS, numberOfCandlesBefore, time, numberOfCandlesAfter)[0][0];
/*      */     }
/*      */     catch (JFException e)
/*      */     {
/* 4776 */       stopStrategy();
/* 4777 */       throw e;
/*      */     }
/* 4779 */     return result;
/*      */   }
/*      */ 
/*      */   public double iGator(Instrument symbol, int timeframe, int jaw_period, int jaw_shift, int teeth_period, int teeth_shift, int lips_period, int lips_shift, int ma_method, int applied_price, int mode, int shift)
/*      */     throws JFException
/*      */   {
/* 4824 */     double result = (0.0D / 0.0D);
/* 4825 */     int numberOfCandlesBefore = shift + 1;
/* 4826 */     long time = getBarTime(symbol, timeframe, OfferSide.BID, shift);
/* 4827 */     int numberOfCandlesAfter = 0;
/* 4828 */     IIndicators.AppliedPrice appliedPrice = CommonHelpers.convertAppliedPrice(applied_price);
/*      */     try
/*      */     {
/* 4831 */       result = getIndicators().gator(getCurrentInstrument(symbol), selectPeriod(timeframe), OfferSide.BID, appliedPrice, jaw_period, teeth_period, lips_period, Filter.ALL_FLATS, numberOfCandlesBefore, time, numberOfCandlesAfter)[0][0];
/*      */     }
/*      */     catch (JFException e)
/*      */     {
/* 4840 */       stopStrategy();
/* 4841 */       throw e;
/*      */     }
/* 4843 */     return result;
/*      */   }
/*      */ 
/*      */   public double iIchimoku(Instrument symbol, int timeframe, int tenkan_sen, int kijun_sen, int senkou_span_b, int mode, int shift)
/*      */     throws JFException
/*      */   {
/* 4872 */     double result = (0.0D / 0.0D);
/* 4873 */     int numberOfCandlesBefore = shift + 1;
/* 4874 */     long time = getBarTime(symbol, timeframe, OfferSide.BID, shift);
/* 4875 */     int numberOfCandlesAfter = 0;
/* 4876 */     int timePeriod = 0;
/*      */ 
/* 4878 */     Integer[] optParams = new Integer[3];
/* 4879 */     optParams[0] = Integer.valueOf(tenkan_sen);
/* 4880 */     optParams[1] = Integer.valueOf(kijun_sen);
/* 4881 */     optParams[2] = Integer.valueOf(senkou_span_b);
/* 4882 */     Object[] test = getIndicators().calculateIndicator(getCurrentInstrument(symbol), selectPeriod(timeframe), new OfferSide[] { OfferSide.ASK }, "ICHIMOKU", new IIndicators.AppliedPrice[] { IIndicators.AppliedPrice.OPEN }, optParams, 0);
/*      */ 
/* 4890 */     double[] point = (double[])(double[])test[5];
/*      */ 
/* 4899 */     return result;
/*      */   }
/*      */ 
/*      */   public double iBWMFI(Instrument symbol, int timeframe, int shift)
/*      */     throws JFException
/*      */   {
/* 4920 */     int numberOfCandlesBefore = shift + 1;
/* 4921 */     long time = getBarTime(symbol, timeframe, OfferSide.BID, shift);
/* 4922 */     int numberOfCandlesAfter = 0;
/* 4923 */     int timePeriod = 0;
/* 4924 */     double result = (0.0D / 0.0D);
/*      */     try {
/* 4926 */       result = getIndicators().bwmfi(getCurrentInstrument(symbol), selectPeriod(timeframe), OfferSide.BID, Filter.ALL_FLATS, numberOfCandlesBefore, time, numberOfCandlesAfter)[0][0];
/*      */     }
/*      */     catch (JFException e)
/*      */     {
/* 4934 */       stopStrategy();
/* 4935 */       throw e;
/*      */     }
/* 4937 */     return result;
/*      */   }
/*      */ 
/*      */   public double iMomentumOnArray(double[] array, int total, int period, int shift)
/*      */     throws JFException
/*      */   {
/* 4961 */     double rc = (0.0D / 0.0D);
/*      */     try {
/* 4963 */       IIndicator maInd = getIndicators().getIndicator("MOM");
/* 4964 */       maInd.setOptInputParameter(0, Integer.valueOf(period));
/* 4965 */       int lookback = maInd.getLookback();
/* 4966 */       maInd.setInputParameter(0, array);
/* 4967 */       double[] outArray = new double[total];
/* 4968 */       maInd.setOutputParameter(0, outArray);
/* 4969 */       maInd.calculate(lookback, total - 1);
/* 4970 */       rc = outArray[shift];
/*      */     }
/*      */     catch (Exception e) {
/* 4973 */       stopStrategy();
/* 4974 */       throw new JFException(e);
/*      */     }
/* 4976 */     return rc;
/*      */   }
/*      */ 
/*      */   public double iMFI(Instrument symbol, int timeframe, int period, int shift)
/*      */     throws JFException
/*      */   {
/* 4998 */     double result = (0.0D / 0.0D);
/* 4999 */     int numberOfCandlesBefore = shift + 1;
/* 5000 */     long time = getBarTime(symbol, timeframe, OfferSide.BID, shift);
/* 5001 */     int numberOfCandlesAfter = 0;
/* 5002 */     int timePeriod = 0;
/*      */     try {
/* 5004 */       result = getIndicators().mfi(getCurrentInstrument(symbol), selectPeriod(timeframe), OfferSide.BID, period, Filter.ALL_FLATS, numberOfCandlesBefore, time, numberOfCandlesAfter)[0];
/*      */     }
/*      */     catch (JFException e)
/*      */     {
/* 5012 */       stopStrategy();
/* 5013 */       throw e;
/*      */     }
/* 5015 */     return result;
/*      */   }
/*      */ 
/*      */   public double iOsMA(Instrument symbol, int timeframe, int fast_ema_period, int slow_ema_period, int signal_period, int applied_price, int shift)
/*      */     throws JFException
/*      */   {
/* 5046 */     double result = (0.0D / 0.0D);
/* 5047 */     IIndicators.AppliedPrice appliedPrice = CommonHelpers.convertAppliedPrice(applied_price);
/*      */ 
/* 5049 */     int numberOfCandlesBefore = shift + 1;
/* 5050 */     long time = getBarTime(symbol, timeframe, OfferSide.BID, shift);
/* 5051 */     int numberOfCandlesAfter = 0;
/* 5052 */     int timePeriod = 0;
/*      */     try {
/* 5054 */       result = getIndicators().osma(getCurrentInstrument(symbol), selectPeriod(timeframe), OfferSide.BID, fast_ema_period, slow_ema_period, signal_period, appliedPrice, Filter.ALL_FLATS, numberOfCandlesBefore, time, numberOfCandlesAfter)[0];
/*      */     }
/*      */     catch (JFException e)
/*      */     {
/* 5064 */       stopStrategy();
/* 5065 */       throw e;
/*      */     }
/* 5067 */     return result;
/*      */   }
/*      */ 
/*      */   public double iOBV(Instrument symbol, int timeframe, int applied_price, int shift)
/*      */     throws JFException
/*      */   {
/* 5090 */     double result = (0.0D / 0.0D);
/* 5091 */     IIndicators.AppliedPrice appliedPrice = CommonHelpers.convertAppliedPrice(applied_price);
/*      */ 
/* 5093 */     int numberOfCandlesBefore = shift + 1;
/* 5094 */     long time = getBarTime(symbol, timeframe, OfferSide.BID, shift);
/* 5095 */     int numberOfCandlesAfter = 0;
/* 5096 */     Period period = getAbstractConnector().getCurrentPeriod();
/*      */ 
/* 5098 */     int timePeriod = 0;
/*      */     try {
/* 5100 */       result = getIndicators().obv(getCurrentInstrument(symbol), selectPeriod(timeframe), OfferSide.BID, appliedPrice, OfferSide.ASK, Filter.ALL_FLATS, numberOfCandlesBefore, time, numberOfCandlesAfter)[0];
/*      */     }
/*      */     catch (JFException e)
/*      */     {
/* 5108 */       e.printStackTrace();
/* 5109 */       stopStrategy();
/*      */     }
/* 5111 */     return result;
/*      */   }
/*      */ 
/*      */   public double iRSIOnArray(double[] array, int total, int period, int shift)
/*      */     throws JFException
/*      */   {
/* 5135 */     double rc = (0.0D / 0.0D);
/* 5136 */     int numberOfCandlesBefore = shift + 1;
/*      */ 
/* 5139 */     int numberOfCandlesAfter = 0;
/* 5140 */     int timePeriod = 0;
/*      */     try {
/* 5142 */       IIndicator maInd = getIndicators().getIndicator("RSI");
/* 5143 */       maInd.setOptInputParameter(0, Integer.valueOf(period));
/* 5144 */       int lookback = maInd.getLookback();
/* 5145 */       maInd.setInputParameter(0, array);
/* 5146 */       double[] outArray = new double[total];
/* 5147 */       maInd.setOutputParameter(0, outArray);
/* 5148 */       maInd.calculate(lookback, total - 1);
/* 5149 */       rc = outArray[shift];
/*      */     }
/*      */     catch (Exception e) {
/* 5152 */       stopStrategy();
/* 5153 */       throw new JFException(e);
/*      */     }
/* 5155 */     return rc;
/*      */   }
/*      */ 
/*      */   public double iRVI(Instrument symbol, int timeframe, int period, int mode, int shift)
/*      */     throws JFException
/*      */   {
/* 5180 */     double result = (0.0D / 0.0D);
/* 5181 */     int numberOfCandlesBefore = shift + 1;
/* 5182 */     long time = getBarTime(symbol, timeframe, OfferSide.BID, shift);
/* 5183 */     int numberOfCandlesAfter = 0;
/* 5184 */     int timePeriod = 0;
/*      */     try {
/* 5186 */       result = getIndicators().rvi(getCurrentInstrument(symbol), selectPeriod(timeframe), OfferSide.BID, period, Filter.ALL_FLATS, numberOfCandlesBefore, time, numberOfCandlesAfter)[0][0];
/*      */     }
/*      */     catch (JFException e)
/*      */     {
/* 5194 */       stopStrategy();
/* 5195 */       throw e;
/*      */     }
/* 5197 */     return result;
/*      */   }
/*      */ 
/*      */   public double iWPR(Instrument symbol, int timeframe, int period, int shift)
/*      */     throws JFException
/*      */   {
/* 5220 */     double result = (0.0D / 0.0D);
/* 5221 */     int numberOfCandlesBefore = shift + 1;
/* 5222 */     long time = getBarTime(symbol, timeframe, OfferSide.BID, shift);
/* 5223 */     int numberOfCandlesAfter = 0;
/* 5224 */     int timePeriod = 0;
/*      */     try {
/* 5226 */       result = getIndicators().willr(getCurrentInstrument(symbol), selectPeriod(timeframe), OfferSide.BID, period, Filter.ALL_FLATS, numberOfCandlesBefore, time, numberOfCandlesAfter)[0];
/*      */     }
/*      */     catch (JFException e)
/*      */     {
/* 5234 */       stopStrategy();
/* 5235 */       throw e;
/*      */     }
/* 5237 */     return result;
/*      */   }
/*      */ 
/*      */   public void OrderPrint()
/*      */     throws JFException
/*      */   {
/* 5252 */     String swap = "0";
/*      */ 
/* 5254 */     String closeTime = "";
/* 5255 */     String closePrice = "";
/* 5256 */     OrderHistoricalData historicalData = MTAPIHelpers.getOrderGroupHistoricalData(this.selectedGroup.getOrderGroupId(), getCurrentInstrument(null));
/*      */ 
/* 5259 */     if ((historicalData != null) && (historicalData.isClosed())) {
/* 5260 */       OrderHistoricalData.CloseData closeData = (OrderHistoricalData.CloseData)historicalData.getCloseDataMap().get(this.selectedGroup.getOrderGroupId());
/*      */ 
/* 5263 */       closeTime = CommonHelpers.dateFormat1.format(new Date(closeData.getCloseTime()));
/*      */ 
/* 5265 */       closePrice = this.selectedGroup.getOpeningOrder().getPriceClient().getValue().toString();
/*      */     }
/*      */ 
/* 5269 */     String openTime = CommonHelpers.dateFormat1.format(this.selectedGroup.getOpeningOrderTimestamp());
/*      */ 
/* 5271 */     IEngine.OrderCommand tradeOperation = OrdersProvider.convert(this.selectedGroup.getOpeningOrder().getSide(), this.selectedGroup.getOpeningOrder().getStopDirection(), false, this.selectedGroup.getOpeningOrder().isPlaceOffer());
/*      */ 
/* 5276 */     String commission = this.selectedGroup.getOpeningOrder().getOrderCommission() != null ? this.selectedGroup.getOpeningOrder().getOrderCommission().toString() : "0.0";
/*      */ 
/* 5280 */     NotificationUtilsProvider.getNotificationUtils().postInfoMessage(new StringBuilder().append("#").append(this.selectedGroup.getOpeningOrder().getOrderId()).append(" ").append(openTime).append(" ").append(tradeOperation.name()).append(" ").append(this.selectedGroup.getOpeningOrder().getAmount().getValue().toString()).append(" ").append(this.selectedGroup.getOpeningOrder().getPriceClient().getValue().toString()).append(" ").append(this.selectedGroup.getStopLossOrder() != null ? new StringBuilder().append(this.selectedGroup.getStopLossOrder().getAmount().getValue().toString()).append(" ").toString() : "").append(this.selectedGroup.getTakeProfitOrder() != null ? new StringBuilder().append(this.selectedGroup.getStopLossOrder().getAmount().getValue().toString()).append(" ").toString() : "").append(closeTime).append(" ").append(closePrice).append(" ").append(commission).append(" ").append(swap).append(" ").append(this.selectedGroup.getOpeningOrder().getProperty("trailingLimit")).append(" ").append(this.selectedGroup.getOpeningOrder().getTag()).append(" ").append(this.selectedGroup.getOpeningOrder().getExternalSysId()).append(" ").append("").toString());
/*      */   }
/*      */ 
/*      */   public boolean OrderCloseBy(int ticket, int opposite, Color color)
/*      */   {
/* 5368 */     boolean rc = false;
/*      */ 
/* 5370 */     Set mergeOrderGroupIdList = new HashSet();
/* 5371 */     OrderGroupMessage ticketGroup = null;
/* 5372 */     OrderGroupMessage oppositeGroup = null;
/*      */     try {
/* 5374 */       ticketGroup = MTAPIHelpers.getOrderGroupById(ticket)[0];
/* 5375 */       oppositeGroup = MTAPIHelpers.getOrderGroupById(opposite)[0];
/*      */     } catch (JFException ex) {
/* 5377 */       throw new RuntimeException("Order not selected.");
/*      */     }
/* 5379 */     if ((ticketGroup == null) || (oppositeGroup == null)) {
/* 5380 */       throw new RuntimeException("Order not selected.");
/*      */     }
/*      */ 
/* 5383 */     mergeOrderGroupIdList.add(ticketGroup.getPosition().getPositionID());
/* 5384 */     mergeOrderGroupIdList.add(oppositeGroup.getPosition().getPositionID());
/*      */ 
/* 5386 */     MergePositionsMessage mpm = JForexAPI.merge(null, ticketGroup.getExternalSysId(), mergeOrderGroupIdList);
/*      */ 
/* 5388 */     GreedTransportClient client = (GreedTransportClient)GreedContext.get("transportClient");
/*      */     try
/*      */     {
/* 5391 */       ProtocolMessage submitResult = client.controlRequest(mpm);
/* 5392 */       if ((submitResult instanceof ErrorResponseMessage)) {
/* 5393 */         ErrorResponseMessage error = (ErrorResponseMessage)submitResult;
/* 5394 */         throw new MTAgentException(-99, error.getReason());
/*      */       }
/*      */     }
/*      */     catch (Exception ex)
/*      */     {
/* 5399 */       LOGGER.error(ex.getMessage(), ex);
/*      */     }
/*      */ 
/* 5402 */     return rc;
/*      */   }
/*      */ 
/*      */   private IChartObject getChartObject(String name, int index)
/*      */   {
/* 5407 */     IChartObject obj = null;
/* 5408 */     index = -1;
/*      */     int i;
/* 5409 */     if (getIChart() != null) {
/* 5410 */       i = 0;
/* 5411 */       for (IChartObject chartObject : getIChart().getAll()) {
/* 5412 */         if (chartObject.getKey().equals(name)) {
/* 5413 */           obj = chartObject;
/* 5414 */           index = i;
/* 5415 */           break;
/*      */         }
/* 5417 */         i++;
/*      */       }
/*      */     }
/* 5420 */     return obj;
/*      */   }
/*      */ 
/*      */   private IChartObject getChartObject(int index) {
/* 5424 */     IChartObject obj = null;
/* 5425 */     if (getIChart() != null) {
/* 5426 */       obj = (IChartObject)getIChart().getAll().get(index);
/*      */     }
/* 5428 */     return obj;
/*      */   }
/*      */ 
/*      */   public String ObjectDescription(String name)
/*      */   {
/* 5442 */     String result = "";
/* 5443 */     int index = 0;
/* 5444 */     IChartObject obj = getChartObject(name, index);
/* 5445 */     if (obj != null) {
/* 5446 */       result = obj.getKey();
/*      */     }
/* 5448 */     return result;
/*      */   }
/*      */ 
/*      */   public double ObjectGet(String name, int index)
/*      */   {
/* 5462 */     int rc = 0;
/*      */     int counter;
/* 5463 */     if (getIChart() != null) {
/* 5464 */       counter = 1;
/* 5465 */       for (IChartObject chartObject : getIChart().getAll()) {
/* 5466 */         if (chartObject.getKey().equals(name)) {
/* 5467 */           rc = counter;
/* 5468 */           break;
/*      */         }
/* 5470 */         counter++;
/*      */       }
/*      */     }
/* 5473 */     return rc;
/*      */   }
/*      */ 
/*      */   public String ObjectGetFiboDescription(String name, int index)
/*      */   {
/* 5489 */     throw new RuntimeException("This function not allowed");
/*      */   }
/*      */ 
/*      */   public int ObjectGetShiftByValue(String name, double value)
/*      */   {
/* 5507 */     throw new RuntimeException("This function not allowed");
/*      */   }
/*      */ 
/*      */   public double ObjectGetValueByShift(String name, int shift)
/*      */   {
/* 5525 */     throw new RuntimeException("This function not allowed");
/*      */   }
/*      */ 
/*      */   public boolean ObjectMove(String name, int point, long time, double price)
/*      */   {
/* 5547 */     boolean result = false;
/* 5548 */     int index = 0;
/* 5549 */     IChartObject obj = getChartObject(name, index);
/* 5550 */     if (obj != null) {
/* 5551 */       obj.move(time, price);
/* 5552 */       result = true;
/*      */     }
/* 5554 */     return result;
/*      */   }
/*      */ 
/*      */   public int ObjectsDeleteAll(int window, int type)
/*      */   {
/* 5580 */     return 0;
/*      */   }
/*      */ 
/*      */   public boolean ObjectSetFiboDescription(String name, int index, String text)
/*      */   {
/* 5598 */     boolean result = false;
/*      */ 
/* 5605 */     throw new RuntimeException("This function not allowed");
/*      */   }
/*      */ 
/*      */   public int ObjectType(String name)
/*      */   {
/* 5617 */     int result = 0;
/* 5618 */     int index = 0;
/* 5619 */     IChartObject obj = getChartObject(name, index);
/* 5620 */     if (obj != null) {
/* 5621 */       result = obj.getType().ordinal();
/*      */     }
/* 5623 */     return result;
/*      */   }
/*      */ 
/*      */   public int WindowBarsPerChart()
/*      */   {
/* 5633 */     int result = 0;
/* 5634 */     if (getIChart() != null) {
/* 5635 */       result = getIChart().getBarsCount();
/*      */     }
/* 5637 */     return result;
/*      */   }
/*      */ 
/*      */   public int WindowFind(String name)
/*      */   {
/* 5654 */     throw new RuntimeException("This function not allowed");
/*      */   }
/*      */ 
/*      */   public int WindowFirstVisibleBar()
/*      */   {
/* 5669 */     throw new RuntimeException("This function not allowed");
/*      */   }
/*      */ 
/*      */   public int WindowHandle(Instrument symbol, int timeframe)
/*      */   {
/* 5685 */     throw new RuntimeException("This function not allowed");
/*      */   }
/*      */ 
/*      */   public boolean WindowIsVisible(int index)
/*      */   {
/* 5697 */     throw new RuntimeException("This function not allowed");
/*      */   }
/*      */ 
/*      */   public int WindowOnDropped()
/*      */   {
/* 5716 */     throw new RuntimeException("This function not allowed");
/*      */   }
/*      */ 
/*      */   public double WindowPriceOnDropped()
/*      */   {
/* 5727 */     throw new RuntimeException("This function not allowed");
/*      */   }
/*      */ 
/*      */   public boolean WindowScreenShot(String filename, int size_x, int size_y, int start_bar, int chart_scale, int chart_mode)
/*      */   {
/* 5764 */     throw new RuntimeException("This function not allowed");
/*      */   }
/*      */ 
/*      */   public void WindowRedraw()
/*      */   {
/* 5772 */     throw new RuntimeException("This function not allowed");
/*      */   }
/*      */ 
/*      */   public long WindowTimeOnDropped()
/*      */   {
/* 5782 */     throw new RuntimeException("This function not allowed");
/*      */   }
/*      */ 
/*      */   public int WindowsTotal()
/*      */   {
/* 5789 */     if (getIChart() != null) {
/* 5790 */       return getIChart().windowsTotal();
/*      */     }
/* 5792 */     return 0;
/*      */   }
/*      */ 
/*      */   public int WindowXOnDropped()
/*      */   {
/* 5805 */     throw new RuntimeException("This function not allowed");
/*      */   }
/*      */ 
/*      */   public int WindowYOnDropped()
/*      */   {
/* 5818 */     throw new RuntimeException("This function not allowed");
/*      */   }
/*      */ 
/*      */   public boolean IsDllsAllowed()
/*      */   {
/* 5831 */     return true;
/*      */   }
/*      */ 
/*      */   public boolean IsLibrariesAllowed()
/*      */   {
/* 5841 */     return true;
/*      */   }
/*      */ 
/*      */   public boolean IsVisualMode()
/*      */   {
/* 5849 */     return true;
/*      */   }
/*      */ 
/*      */   public int UninitializeReason()
/*      */   {
/* 5862 */     throw new RuntimeException("This function not allowed");
/*      */   }
/*      */ 
/*      */   private IOrder getSelectedOrder() throws JFException {
/* 5866 */     if (this.selectedOrder == null) {
/* 5867 */       this.lastError = 2;
/* 5868 */       throw new JFException("Order not selected.");
/*      */     }
/* 5870 */     return this.selectedOrder;
/*      */   }
/*      */ 
/*      */   public String ErrorDescription(int errCode)
/*      */     throws JFException
/*      */   {
/* 5876 */     return CommonHelpers.ErrorDescription(errCode);
/*      */   }
/*      */ 
/*      */   public int getChartPanelId()
/*      */   {
/* 5881 */     synchronized (this) {
/* 5882 */       return this.chartPanelId;
/*      */     }
/*      */   }
/*      */ 
/*      */   public void setChartPanelId(int chartPanelId)
/*      */   {
/* 5888 */     synchronized (this) {
/* 5889 */       this.chartPanelId = chartPanelId;
/*      */     }
/*      */   }
/*      */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.api.MQLConnector
 * JD-Core Version:    0.6.0
 */