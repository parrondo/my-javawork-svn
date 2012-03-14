/*      */ package com.dukascopy.api.impl.connect;
/*      */ 
/*      */ import com.dukascopy.api.ConsoleAdapter;
/*      */ import com.dukascopy.api.DataType;
/*      */ import com.dukascopy.api.IChart;
/*      */ import com.dukascopy.api.IConsole;
/*      */ import com.dukascopy.api.INewsFilter;
/*      */ import com.dukascopy.api.INewsFilter.NewsSource;
/*      */ import com.dukascopy.api.IStrategy;
/*      */ import com.dukascopy.api.Instrument;
/*      */ import com.dukascopy.api.OfferSide;
/*      */ import com.dukascopy.api.Period;
/*      */ import com.dukascopy.api.impl.IndicatorContext;
/*      */ import com.dukascopy.api.impl.StrategyEventsCallback;
/*      */ import com.dukascopy.api.indicators.IIndicator;
/*      */ import com.dukascopy.api.system.Commissions;
/*      */ import com.dukascopy.api.system.IStrategyExceptionHandler;
/*      */ import com.dukascopy.api.system.IStrategyExceptionHandler.Source;
/*      */ import com.dukascopy.api.system.ISystemListener;
/*      */ import com.dukascopy.api.system.ITesterClient;
/*      */ import com.dukascopy.api.system.ITesterClient.DataLoadingMethod;
/*      */ import com.dukascopy.api.system.ITesterClient.InterpolationMethod;
/*      */ import com.dukascopy.api.system.JFAuthenticationException;
/*      */ import com.dukascopy.api.system.JFVersionException;
/*      */ import com.dukascopy.api.system.Overnights;
/*      */ import com.dukascopy.api.system.tester.ITesterChartController;
/*      */ import com.dukascopy.api.system.tester.ITesterExecution;
/*      */ import com.dukascopy.api.system.tester.ITesterExecutionControl;
/*      */ import com.dukascopy.api.system.tester.ITesterGui;
/*      */ import com.dukascopy.api.system.tester.ITesterIndicatorsParameters;
/*      */ import com.dukascopy.api.system.tester.ITesterUserInterface;
/*      */ import com.dukascopy.api.system.tester.ITesterVisualModeParameters;
/*      */ import com.dukascopy.charts.data.datacache.DataCacheException;
/*      */ import com.dukascopy.charts.data.datacache.DataCacheUtils;
/*      */ import com.dukascopy.charts.data.datacache.FeedDataProvider;
/*      */ import com.dukascopy.charts.data.datacache.IAuthenticator;
/*      */ import com.dukascopy.charts.data.datacache.IFeedDataProvider;
/*      */ import com.dukascopy.charts.data.datacache.JForexPeriod;
/*      */ import com.dukascopy.charts.data.orders.OrdersProvider;
/*      */ import com.dukascopy.charts.main.interfaces.DDSChartsController;
/*      */ import com.dukascopy.charts.math.indicators.IndicatorsProvider;
/*      */ import com.dukascopy.charts.persistence.ChartBean;
/*      */ import com.dukascopy.charts.persistence.ITheme;
/*      */ import com.dukascopy.charts.persistence.ITheme.ChartElement;
/*      */ import com.dukascopy.charts.persistence.ThemeManager;
/*      */ import com.dukascopy.dds2.greed.agent.compiler.JFXPack;
/*      */ import com.dukascopy.dds2.greed.agent.strategy.tester.AbstractHistoricalTesterIndicator;
/*      */ import com.dukascopy.dds2.greed.agent.strategy.tester.BalanceHistoricalTesterIndicator;
/*      */ import com.dukascopy.dds2.greed.agent.strategy.tester.EquityHistoricalTesterIndicator;
/*      */ import com.dukascopy.dds2.greed.agent.strategy.tester.ExecutionControl;
/*      */ import com.dukascopy.dds2.greed.agent.strategy.tester.IStrategyRunner;
/*      */ import com.dukascopy.dds2.greed.agent.strategy.tester.MinTradableAmounts;
/*      */ import com.dukascopy.dds2.greed.agent.strategy.tester.ProfLossHistoricalTesterIndicator;
/*      */ import com.dukascopy.dds2.greed.agent.strategy.tester.StrategyDataStorageImpl;
/*      */ import com.dukascopy.dds2.greed.agent.strategy.tester.StrategyReport;
/*      */ import com.dukascopy.dds2.greed.agent.strategy.tester.StrategyRunner;
/*      */ import com.dukascopy.dds2.greed.agent.strategy.tester.TesterAccount;
/*      */ import com.dukascopy.dds2.greed.agent.strategy.tester.TesterChartData;
/*      */ import com.dukascopy.dds2.greed.agent.strategy.tester.TesterDataLoader;
/*      */ import com.dukascopy.dds2.greed.agent.strategy.tester.TesterFeedDataProvider;
/*      */ import com.dukascopy.dds2.greed.agent.strategy.tester.TesterIndicatorWrapper;
/*      */ import com.dukascopy.dds2.greed.agent.strategy.tester.TesterOrdersProvider;
/*      */ import com.dukascopy.dds2.greed.agent.strategy.tester.TesterReportData;
/*      */ import com.dukascopy.dds2.greed.util.AbstractCurrencyConverter;
/*      */ import com.dukascopy.dds2.greed.util.FilePathManager;
/*      */ import com.dukascopy.dds2.greed.util.IndicatorHelper;
/*      */ import com.dukascopy.dds2.greed.util.NotificationUtilsProvider;
/*      */ import java.awt.Color;
/*      */ import java.awt.Component;
/*      */ import java.awt.Container;
/*      */ import java.awt.image.BufferedImage;
/*      */ import java.io.BufferedInputStream;
/*      */ import java.io.BufferedOutputStream;
/*      */ import java.io.File;
/*      */ import java.io.FileInputStream;
/*      */ import java.io.FileOutputStream;
/*      */ import java.io.IOException;
/*      */ import java.io.InputStream;
/*      */ import java.io.OutputStream;
/*      */ import java.io.PrintStream;
/*      */ import java.lang.reflect.Method;
/*      */ import java.net.URL;
/*      */ import java.net.URLConnection;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Arrays;
/*      */ import java.util.Calendar;
/*      */ import java.util.Collections;
/*      */ import java.util.Currency;
/*      */ import java.util.HashMap;
/*      */ import java.util.HashSet;
/*      */ import java.util.Iterator;
/*      */ import java.util.LinkedList;
/*      */ import java.util.List;
/*      */ import java.util.Map;
/*      */ import java.util.Map.Entry;
/*      */ import java.util.Properties;
/*      */ import java.util.Set;
/*      */ import java.util.TimeZone;
/*      */ import java.util.UUID;
/*      */ import java.util.concurrent.Future;
/*      */ import java.util.concurrent.FutureTask;
/*      */ import java.util.concurrent.TimeUnit;
/*      */ import java.util.regex.Matcher;
/*      */ import java.util.regex.Pattern;
/*      */ import javax.swing.JComponent;
/*      */ import javax.swing.JPanel;
/*      */ import javax.xml.parsers.DocumentBuilder;
/*      */ import javax.xml.parsers.DocumentBuilderFactory;
/*      */ import org.slf4j.Logger;
/*      */ import org.slf4j.LoggerFactory;
/*      */ import org.w3c.dom.Document;
/*      */ import org.w3c.dom.Element;
/*      */ import org.w3c.dom.NodeList;
/*      */ 
/*      */ public class TesterClientImpl
/*      */   implements ITesterClient
/*      */ {
/*   54 */   private static final Logger LOGGER = LoggerFactory.getLogger(TesterClientImpl.class);
/*      */   public static final String WATERMARK = "watermark";
/*      */   public static final String HISTORY_SERVER_URL = "history.server.url";
/*      */   public static final String ENCRYPTION_KEY = "encryptionKey";
/*   60 */   private int chart_id = 0;
/*      */   private volatile ISystemListener systemListener;
/*   64 */   private Set<Instrument> instruments = new HashSet();
/*      */   private PrintStream out;
/*      */   private PrintStream err;
/*      */   private Properties serverProperties;
/*      */   private AuthorizationClient authorizationClient;
/*      */   private String sessionID;
/*      */   private String captchaId;
/*   73 */   private boolean connected = false;
/*      */   private long strategyIdCounter;
/*   76 */   private Map<Long, StrategyStuff> strategies = new HashMap();
/*      */ 
/*   79 */   private Period period = Period.TICK;
/*   80 */   private OfferSide offerSide = OfferSide.BID;
/*      */   private ITesterClient.InterpolationMethod interpolationMethod;
/*      */   private ITesterClient.DataLoadingMethod dataLoadingMethod;
/*      */   private long from;
/*      */   private long to;
/*   87 */   private Currency accountCurrency = Instrument.EURUSD.getSecondaryCurrency();
/*   88 */   private double deposit = 50000.0D;
/*   89 */   private int leverage = 100;
/*   90 */   private Commissions commission = new Commissions(false);
/*   91 */   private Overnights overnights = new Overnights(false);
/*      */   private boolean gatherReportData;
/*      */   private boolean eventLogEnabled;
/*      */   private boolean processingStatsEnabled;
/*   95 */   private int marginCutLevel = 200;
/*   96 */   private double mcEquity = 0.0D;
/*      */   private LinkedList<TesterIndicatorWrapper> indicatorsWrappers;
/*   99 */   private IConsole console = new ConsoleAdapter() {
/*      */     public PrintStream getOut() {
/*  101 */       return TesterClientImpl.this.out;
/*      */     }
/*      */ 
/*      */     public PrintStream getErr() {
/*  105 */       return TesterClientImpl.this.err;
/*      */     }
/*   99 */   };
/*      */ 
/*      */   public TesterClientImpl()
/*      */   {
/*  110 */     this.out = System.out;
/*  111 */     this.err = System.err;
/*  112 */     NotificationUtilsProvider.setNotificationUtils(new PrintStreamNotificationUtils(this.out, this.err));
/*      */ 
/*  114 */     Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
/*  115 */     calendar.setTimeInMillis(System.currentTimeMillis());
/*  116 */     calendar.set(11, 0);
/*  117 */     calendar.set(12, 0);
/*  118 */     calendar.set(13, 0);
/*  119 */     calendar.set(14, 0);
/*  120 */     this.to = calendar.getTimeInMillis();
/*  121 */     calendar.add(6, -3);
/*  122 */     this.from = calendar.getTimeInMillis();
/*      */ 
/*  124 */     this.dataLoadingMethod = ITesterClient.DataLoadingMethod.ALL_TICKS;
/*      */   }
/*      */ 
/*      */   public synchronized void connect(String jnlpUrl, String username, String password) throws Exception
/*      */   {
/*  129 */     connect(jnlpUrl, username, password, null);
/*      */   }
/*      */ 
/*      */   public synchronized void connect(String jnlp, String username, String password, String pin) throws Exception
/*      */   {
/*  134 */     if (!(this.connected)) {
/*  135 */       String authServersCsv = getAuthServers(jnlp);
/*  136 */       if (this.sessionID == null) {
/*  137 */         this.sessionID = UUID.randomUUID().toString();
/*      */       }
/*  139 */       authenticate(authServersCsv, this.sessionID, username, password, pin);
/*      */ 
/*  145 */       FilePathManager fmanager = FilePathManager.getInstance();
/*  146 */       fmanager.setStrategiesFolderPath(fmanager.getDefaultStrategiesFolderPath());
/*      */ 
/*  148 */       OrdersProvider.createInstance(null);
/*      */ 
/*  150 */       FeedDataProvider.createFeedDataProvider("SINGLEJAR");
/*  151 */       FeedDataProvider.getDefaultInstance().connectToHistoryServer(new IAuthenticator(username, password, pin)
/*      */       {
/*      */         public String authenticate() {
/*      */           try {
/*  155 */             String ticket = TesterClientImpl.this.authenticateAPI(this.val$username, this.val$password, TesterClientImpl.this.sessionID, this.val$pin);
/*  156 */             return TesterClientImpl.this.authorizationClient.getFeedUrlAndTicket(this.val$username, ticket, TesterClientImpl.this.sessionID);
/*      */           } catch (Exception e) {
/*  158 */             TesterClientImpl.LOGGER.error(e.getMessage(), e); }
/*  159 */           return null;
/*      */         }
/*      */       }
/*      */       , username, this.sessionID, this.serverProperties.getProperty("history.server.url", null), this.serverProperties.getProperty("encryptionKey", null));
/*      */ 
/*  163 */       FeedDataProvider.getDefaultInstance().setInstrumentsSubscribed(this.instruments);
/*      */ 
/*  165 */       IndicatorsProvider.createInstance(new IndicatorsSettingsStorage(username));
/*      */ 
/*  167 */       this.connected = true;
/*  168 */       fireConnected();
/*      */     }
/*      */   }
/*      */ 
/*      */   private String getAuthServers(String jnlp) throws Exception {
/*  173 */     URL jnlpUrl = new URL(jnlp);
/*      */ 
/*  175 */     InputStream jnlpIs = jnlpUrl.openConnection().getInputStream();
/*      */     Document doc;
/*      */     try {
/*  178 */       DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
/*  179 */       doc = builder.parse(jnlpIs);
/*      */     } finally {
/*  181 */       jnlpIs.close();
/*      */     }
/*      */ 
/*  184 */     String authServersCsv = null;
/*  185 */     NodeList jnlpNodes = doc.getElementsByTagName("jnlp");
/*  186 */     if (jnlpNodes.getLength() < 1) {
/*  187 */       throw new Exception("Can't find jnlp element");
/*      */     }
/*  189 */     Element jnlpNode = (Element)jnlpNodes.item(0);
/*  190 */     NodeList resourcesNodes = jnlpNode.getElementsByTagName("resources");
/*  191 */     if (resourcesNodes.getLength() < 1) {
/*  192 */       throw new Exception("Can't find resources element");
/*      */     }
/*  194 */     Element resourcesNode = (Element)resourcesNodes.item(0);
/*  195 */     NodeList propertyNodes = resourcesNode.getElementsByTagName("property");
/*  196 */     for (int i = 0; i < propertyNodes.getLength(); ++i) {
/*  197 */       Element propertyElement = (Element)propertyNodes.item(i);
/*      */ 
/*  199 */       String nameAttribute = propertyElement.getAttribute("name");
/*  200 */       if ((nameAttribute != null) && (nameAttribute.trim().equals("jnlp.login.url"))) {
/*  201 */         authServersCsv = propertyElement.getAttribute("value").trim();
/*      */       }
/*      */     }
/*  204 */     if (authServersCsv == null) {
/*  205 */       throw new Exception("Can't find property with name attribute equals to jnlp.login.url");
/*      */     }
/*  207 */     return authServersCsv;
/*      */   }
/*      */ 
/*      */   public BufferedImage getCaptchaImage(String jnlp) throws Exception
/*      */   {
/*  212 */     String authServersCsv = getAuthServers(jnlp);
/*  213 */     initAuthorizationClient(authServersCsv);
/*  214 */     Map imageCaptchaMap = this.authorizationClient.getImageCaptcha();
/*  215 */     if (!(imageCaptchaMap.isEmpty())) {
/*  216 */       Map.Entry imageCaptchaEntry = (Map.Entry)imageCaptchaMap.entrySet().iterator().next();
/*  217 */       this.captchaId = ((String)imageCaptchaEntry.getKey());
/*  218 */       return ((BufferedImage)imageCaptchaEntry.getValue());
/*      */     }
/*  220 */     return null;
/*      */   }
/*      */ 
/*      */   private void authenticate(String authServersCsv, String session, String username, String password, String pin) throws Exception
/*      */   {
/*  225 */     initAuthorizationClient(authServersCsv);
/*  226 */     String ticket = authenticateAPI(username, password, session, pin);
/*  227 */     this.serverProperties = this.authorizationClient.getAllProperties(username, ticket, session);
/*      */   }
/*      */ 
/*      */   private void initAuthorizationClient(String authServersCsv) {
/*  231 */     if (this.authorizationClient != null) {
/*  232 */       return;
/*      */     }
/*  234 */     String version = super.getClass().getPackage().getImplementationVersion();
/*  235 */     if (version == null) {
/*  236 */       version = "99.99.99";
/*      */     }
/*  238 */     if (version.endsWith("SNAPSHOT")) {
/*  239 */       version = "99.99.99";
/*      */     }
/*      */ 
/*  242 */     String[] urlList = authServersCsv.split(",");
/*  243 */     List authServers = Arrays.asList(urlList);
/*  244 */     this.authorizationClient = AuthorizationClient.getInstance(authServers, version);
/*      */   }
/*      */ 
/*      */   private String authenticateAPI(String username, String password, String session, String pin)
/*      */     throws Exception
/*      */   {
/*      */     String authResponse;
/*      */     String authResponse;
/*  249 */     if (pin == null)
/*  250 */       authResponse = this.authorizationClient.getUrlAndTicket(username, password, session);
/*      */     else {
/*  252 */       authResponse = this.authorizationClient.getUrlAndTicket(username, password, this.captchaId, pin, session);
/*      */     }
/*      */ 
/*  255 */     LOGGER.debug(authResponse);
/*      */ 
/*  257 */     if ("-1".equals(authResponse)) {
/*  258 */       throw new JFAuthenticationException("Incorrect username or password");
/*      */     }
/*      */ 
/*  261 */     if ("-2".equals(authResponse)) {
/*  262 */       throw new JFVersionException("Incorrect version");
/*      */     }
/*      */ 
/*  265 */     if ("-3".equals(authResponse)) {
/*  266 */       throw new JFVersionException("System offline");
/*      */     }
/*      */ 
/*  269 */     if ("-500".equals(authResponse)) {
/*  270 */       throw new JFVersionException("System error");
/*      */     }
/*      */ 
/*  273 */     if (authResponse == null) {
/*  274 */       throw new IOException("Authentication failed");
/*      */     }
/*      */ 
/*  277 */     Matcher matcher = AuthorizationClient.RESULT_PATTERN.matcher(authResponse);
/*  278 */     if (!(matcher.matches())) {
/*  279 */       throw new IOException("Authentication procedure returned unexpected result [" + authResponse + "]");
/*      */     }
/*      */ 
/*  282 */     return matcher.group(2);
/*      */   }
/*      */ 
/*      */   public synchronized void reconnect()
/*      */   {
/*      */   }
/*      */ 
/*      */   public void disconnect()
/*      */   {
/*  293 */     for (Long processId : getStartedStrategies().keySet()) {
/*  294 */       stopStrategy(processId.longValue());
/*      */     }
/*  296 */     FeedDataProvider.getDefaultInstance().close();
/*      */     try {
/*  298 */       TimeUnit.SECONDS.sleep(5L); } catch (InterruptedException e) {
/*      */     }
/*  300 */     this.captchaId = null;
/*  301 */     this.sessionID = null;
/*  302 */     this.authorizationClient = null;
/*  303 */     this.serverProperties = new Properties();
/*  304 */     this.strategyIdCounter = 0L;
/*      */ 
/*  306 */     this.period = Period.TICK;
/*  307 */     this.offerSide = OfferSide.BID;
/*  308 */     this.interpolationMethod = null;
/*  309 */     this.dataLoadingMethod = ITesterClient.DataLoadingMethod.ALL_TICKS;
/*  310 */     this.accountCurrency = Instrument.EURUSD.getSecondaryCurrency();
/*  311 */     this.deposit = 50000.0D;
/*  312 */     this.leverage = 100;
/*  313 */     this.commission = new Commissions(false);
/*  314 */     this.overnights = new Overnights(false);
/*  315 */     this.gatherReportData = false;
/*  316 */     this.eventLogEnabled = false;
/*  317 */     this.processingStatsEnabled = false;
/*  318 */     this.marginCutLevel = 200;
/*  319 */     this.mcEquity = 0.0D;
/*  320 */     this.connected = false;
/*      */   }
/*      */ 
/*      */   public boolean isConnected()
/*      */   {
/*  325 */     return this.connected;
/*      */   }
/*      */ 
/*      */   public synchronized void setSystemListener(ISystemListener userSystemListener) {
/*  329 */     this.systemListener = new ISystemListener(userSystemListener)
/*      */     {
/*      */       public void onStart(long processId) {
/*  332 */         this.val$userSystemListener.onStart(processId);
/*      */       }
/*      */ 
/*      */       public void onStop(long processId) {
/*  336 */         this.val$userSystemListener.onStop(processId);
/*      */       }
/*      */ 
/*      */       public void onConnect() {
/*  340 */         this.val$userSystemListener.onConnect();
/*      */       }
/*      */ 
/*      */       public void onDisconnect() {
/*  344 */         this.val$userSystemListener.onDisconnect();
/*      */       }
/*      */     };
/*      */   }
/*      */ 
/*      */   public synchronized void setStrategyEventsCallback(StrategyEventsCallback strategyEventsCallback) {
/*  350 */     for (StrategyStuff strategyStuff : this.strategies.values())
/*  351 */       strategyStuff.strategyRunner.addStrategyEventsCallback(strategyEventsCallback);
/*      */   }
/*      */ 
/*      */   private void fireConnected()
/*      */   {
/*  357 */     ISystemListener systemListener = this.systemListener;
/*  358 */     if (systemListener != null) {
/*  359 */       systemListener.onConnect();
/*      */     }
/*      */ 
/*  362 */     FeedDataProvider.getDefaultInstance().connected();
/*      */   }
/*      */ 
/*      */   public synchronized long startStrategy(IStrategy strategy) throws IllegalStateException, IllegalArgumentException
/*      */   {
/*  367 */     if (!(this.connected)) {
/*  368 */       throw new IllegalStateException("Not connected");
/*      */     }
/*  370 */     if (strategy == null) {
/*  371 */       throw new IllegalArgumentException("Strategy is null");
/*      */     }
/*  373 */     return startTest(strategy, null, null);
/*      */   }
/*      */ 
/*      */   public synchronized long startStrategy(IStrategy strategy, com.dukascopy.api.LoadingProgressListener testerProgressListener) throws IllegalStateException, IllegalArgumentException
/*      */   {
/*  378 */     if (!(this.connected)) {
/*  379 */       throw new IllegalStateException("Not connected");
/*      */     }
/*  381 */     if (strategy == null) {
/*  382 */       throw new IllegalArgumentException("Strategy is null");
/*      */     }
/*  384 */     return startTest(strategy, null, testerProgressListener);
/*      */   }
/*      */ 
/*      */   public synchronized long startStrategy(IStrategy strategy, IStrategyExceptionHandler exceptionHandler) throws IllegalStateException, IllegalArgumentException
/*      */   {
/*  389 */     if (!(this.connected)) {
/*  390 */       throw new IllegalStateException("Not connected");
/*      */     }
/*  392 */     if (strategy == null) {
/*  393 */       throw new IllegalArgumentException("Strategy is null");
/*      */     }
/*  395 */     if (exceptionHandler == null) {
/*  396 */       throw new IllegalArgumentException("Exception handler is null");
/*      */     }
/*  398 */     return startTest(strategy, exceptionHandler, null);
/*      */   }
/*      */ 
/*      */   public synchronized long startStrategy(IStrategy strategy, IStrategyExceptionHandler exceptionHandler, com.dukascopy.api.LoadingProgressListener testerProgressListener) throws IllegalStateException, IllegalArgumentException
/*      */   {
/*  403 */     if (!(this.connected)) {
/*  404 */       throw new IllegalStateException("Not connected");
/*      */     }
/*  406 */     if (strategy == null) {
/*  407 */       throw new IllegalArgumentException("Strategy is null");
/*      */     }
/*  409 */     if (exceptionHandler == null) {
/*  410 */       throw new IllegalArgumentException("Exception handler is null");
/*      */     }
/*  412 */     return startTest(strategy, exceptionHandler, testerProgressListener);
/*      */   }
/*      */ 
/*      */   public synchronized long startStrategy(IStrategy strategy, com.dukascopy.api.LoadingProgressListener testerProgressListener, ITesterExecution testerExecution, ITesterUserInterface testerUserInterface)
/*      */     throws IllegalStateException, IllegalArgumentException
/*      */   {
/*  422 */     return startTest(strategy, testerProgressListener, null, testerExecution, testerUserInterface);
/*      */   }
/*      */ 
/*      */   public synchronized long startStrategy(IStrategy strategy, com.dukascopy.api.LoadingProgressListener testerProgressListener, ITesterVisualModeParameters testerVisualModeParameters, ITesterExecution testerExecution, ITesterUserInterface testerUserInterface)
/*      */     throws IllegalStateException, IllegalArgumentException
/*      */   {
/*  439 */     if (!(this.connected)) {
/*  440 */       throw new IllegalStateException("Not connected");
/*      */     }
/*  442 */     if (strategy == null) {
/*  443 */       throw new IllegalArgumentException("Strategy is null");
/*      */     }
/*  445 */     if (testerProgressListener == null) {
/*  446 */       throw new IllegalArgumentException("TesterProgressListener is null");
/*      */     }
/*  448 */     if (testerUserInterface == null) {
/*  449 */       throw new IllegalArgumentException("TesterUserInterface is null");
/*      */     }
/*  451 */     if (testerExecution == null) {
/*  452 */       throw new IllegalArgumentException("TesterExecution is null");
/*      */     }
/*      */ 
/*  455 */     if (testerVisualModeParameters != null) {
/*  456 */       Map testerIndicatorsParameters = testerVisualModeParameters.getTesterIndicatorsParameters();
/*  457 */       if (testerIndicatorsParameters == null) {
/*  458 */         throw new IllegalArgumentException("TesterIndicatorsParameters is null");
/*      */       }
/*  460 */       for (Map.Entry entry : testerIndicatorsParameters.entrySet()) {
/*  461 */         Instrument instrument = (Instrument)entry.getKey();
/*  462 */         if (instrument == null) {
/*  463 */           throw new IllegalArgumentException("TesterIndicatorsParameters: instrument is null");
/*      */         }
/*      */ 
/*  466 */         ITesterIndicatorsParameters parameters = (ITesterIndicatorsParameters)entry.getValue();
/*  467 */         if (parameters == null) {
/*  468 */           throw new IllegalArgumentException("IndicatorsParameters is null for instrument:" + instrument);
/*      */         }
/*      */       }
/*      */     }
/*      */ 
/*  473 */     return startTest(strategy, testerProgressListener, testerVisualModeParameters, testerExecution, testerUserInterface);
/*      */   }
/*      */ 
/*      */   private long startTest(IStrategy strategy, IStrategyExceptionHandler exceptionHandler, com.dukascopy.api.LoadingProgressListener testerProgressListener)
/*      */   {
/*  536 */     FeedDataProvider.getDefaultInstance().setCurrentTime(System.currentTimeMillis());
/*      */ 
/*  538 */     StrategyStuff strategyStuff = new StrategyStuff(null);
/*  539 */     this.strategyIdCounter += 1L;
/*  540 */     StrategyStuff.access$802(strategyStuff, this.strategyIdCounter);
/*  541 */     StrategyStuff.access$902(strategyStuff, new TesterLoadingProgressListener(strategyStuff, testerProgressListener, this.accountCurrency, this.eventLogEnabled));
/*  542 */     StrategyStuff.access$1002(strategyStuff, new IStrategy[] { strategy });
/*      */ 
/*  545 */     TesterOrdersProvider testerOrdersProvider = new TesterOrdersProvider();
/*  546 */     TesterAccount account = new TesterAccount(this.accountCurrency, this.deposit, this.leverage, this.marginCutLevel, this.mcEquity, this.commission, this.overnights, "");
/*      */ 
/*  548 */     if (exceptionHandler == null) {
/*  549 */       exceptionHandler = new DefaultStrategyExceptionHandler(strategyStuff, null);
/*      */     }
/*      */ 
/*  552 */     MinTradableAmounts minTradableAmounts = new MinTradableAmounts(Double.valueOf(1000.0D));
/*      */ 
/*  555 */     minTradableAmounts.put(Instrument.XAUUSD, Double.valueOf(1.0D));
/*  556 */     minTradableAmounts.put(Instrument.XAGUSD, Double.valueOf(50.0D));
/*      */ 
/*  558 */     StrategyStuff.access$602(strategyStuff, new StrategyRunner(null, strategy.getClass().getName(), strategy, true, this.period, this.offerSide, this.interpolationMethod, this.dataLoadingMethod, this.from, this.to, new HashSet(this.instruments), strategyStuff.loadingProgressListener, NotificationUtilsProvider.getNotificationUtils(), minTradableAmounts, account, null, testerOrdersProvider, new HashMap(0), new ExecutionControl()
/*      */     {
/*      */       public void pause()
/*      */       {
/*      */       }
/*      */ 
/*      */       public void setSpeed(int value)
/*      */       {
/*      */       }
/*      */     }
/*      */     , this.processingStatsEnabled, exceptionHandler, null));
/*      */ 
/*  593 */     StrategyStuff.access$1202(strategyStuff, new TesterReportData[] { ((StrategyRunner)StrategyStuff.access$600(strategyStuff)).getReportData() });
/*  594 */     this.strategies.put(Long.valueOf(strategyStuff.processId), strategyStuff);
/*  595 */     if (this.systemListener != null) {
/*  596 */       this.systemListener.onStart(strategyStuff.processId);
/*      */     }
/*  598 */     ((StrategyRunner)strategyStuff.strategyRunner).start();
/*  599 */     return strategyStuff.processId;
/*      */   }
/*      */ 
/*      */   private long startTest(IStrategy strategy, com.dukascopy.api.LoadingProgressListener testerProgressListener, ITesterVisualModeParameters testerVisualModeParameters, ITesterExecution testerExecution, ITesterUserInterface testerUserInterface)
/*      */   {
/*  609 */     this.indicatorsWrappers = new LinkedList();
/*  610 */     TesterOrdersProvider testerOrdersProvider = new TesterOrdersProvider();
/*  611 */     TesterFeedDataProvider testerFeedDataProvider = null;
/*      */     try
/*      */     {
/*  614 */       testerFeedDataProvider = new TesterFeedDataProvider("jnlp.client.mode", testerOrdersProvider);
/*      */     } catch (DataCacheException e) {
/*  616 */       LOGGER.error(e.getMessage(), e);
/*  617 */       e.printStackTrace();
/*  618 */       throw new RuntimeException("Cannot create TesterFeedDataProvider in the TesterClientImpl:" + e.getMessage());
/*      */     }
/*      */ 
/*  621 */     testerFeedDataProvider.setInstrumentsSubscribed(this.instruments);
/*      */ 
/*  623 */     JForexPeriod jForexPeriod = getJForexPeriod();
/*  624 */     DDSChartsController ddsChartsController = getDDSChartsController();
/*      */ 
/*  626 */     List chartBeans = createChartBeans(ddsChartsController, testerFeedDataProvider, jForexPeriod);
/*      */ 
/*  628 */     Map chartsPanels = createChartsPanels(ddsChartsController, chartBeans);
/*  629 */     testerUserInterface.setChartPanels(chartsPanels);
/*      */ 
/*  632 */     StrategyDataStorageImpl strategyDataStorage = getStrategyDataStorage(testerVisualModeParameters, chartBeans);
/*  633 */     addTesterIndicators(testerVisualModeParameters, chartBeans, ddsChartsController, strategyDataStorage);
/*      */ 
/*  635 */     ExecutionControl executionControl = new ExecutionControl();
/*  636 */     executionControl.startExecuting(true);
/*  637 */     TesterExecutionControl testerExecutionControl = new TesterExecutionControl(executionControl);
/*  638 */     testerExecution.setExecutionControl(testerExecutionControl);
/*      */ 
/*  640 */     Map testerChartsData = createTesterChartData(ddsChartsController, chartBeans);
/*      */ 
/*  642 */     StrategyStuff strategyStuff = new StrategyStuff(null);
/*  643 */     this.strategyIdCounter += 1L;
/*  644 */     StrategyStuff.access$802(strategyStuff, this.strategyIdCounter);
/*  645 */     StrategyStuff.access$902(strategyStuff, new TesterLoadingProgressListener(strategyStuff, testerProgressListener, testerExecutionControl, this.accountCurrency, this.eventLogEnabled));
/*      */ 
/*  653 */     StrategyStuff.access$1002(strategyStuff, new IStrategy[] { strategy });
/*      */ 
/*  655 */     TesterAccount account = new TesterAccount(this.accountCurrency, this.deposit, this.leverage, this.marginCutLevel, this.mcEquity, this.commission, this.overnights, "");
/*      */ 
/*  666 */     MinTradableAmounts minTradableAmounts = new MinTradableAmounts(Double.valueOf(1000.0D));
/*      */ 
/*  669 */     minTradableAmounts.put(Instrument.XAUUSD, Double.valueOf(1.0D));
/*  670 */     minTradableAmounts.put(Instrument.XAGUSD, Double.valueOf(50.0D));
/*      */ 
/*  672 */     IStrategyExceptionHandler exceptionHandler = new DefaultStrategyExceptionHandler(strategyStuff, null);
/*  673 */     StrategyStuff.access$602(strategyStuff, new StrategyRunner(null, strategy.getClass().getName(), strategy, true, this.period, this.offerSide, this.interpolationMethod, this.dataLoadingMethod, this.from, this.to, new HashSet(this.instruments), strategyStuff.loadingProgressListener, NotificationUtilsProvider.getNotificationUtils(), minTradableAmounts, account, testerFeedDataProvider, testerOrdersProvider, testerChartsData, executionControl, this.processingStatsEnabled, exceptionHandler, strategyDataStorage));
/*      */ 
/*  698 */     StrategyStuff.access$1202(strategyStuff, new TesterReportData[] { ((StrategyRunner)StrategyStuff.access$600(strategyStuff)).getReportData() });
/*  699 */     this.strategies.put(Long.valueOf(strategyStuff.processId), strategyStuff);
/*  700 */     if (this.systemListener != null) {
/*  701 */       this.systemListener.onStart(strategyStuff.processId);
/*      */     }
/*  703 */     ((StrategyRunner)strategyStuff.strategyRunner).start();
/*      */ 
/*  705 */     return strategyStuff.processId;
/*      */   }
/*      */ 
/*      */   private List<ChartBean> createChartBeans(DDSChartsController ddsChartsController, IFeedDataProvider feedDataProvider, JForexPeriod jForexPeriod) {
/*  709 */     List chartBeans = new ArrayList();
/*      */ 
/*  711 */     for (Instrument instrument : this.instruments) {
/*  712 */       ChartBean chartBean = new ChartBean(getNextChartId(), instrument, jForexPeriod, OfferSide.BID);
/*  713 */       chartBean.setHistoricalTesterChart(true);
/*  714 */       chartBean.setReadOnly(true);
/*  715 */       chartBean.setFeedDataProvider(feedDataProvider);
/*      */ 
/*  717 */       Runnable dataLoadingStarter = new Runnable(ddsChartsController, chartBean) {
/*      */         public void run() {
/*  719 */           if (this.val$ddsChartsController != null)
/*  720 */             this.val$ddsChartsController.startLoadingData(Integer.valueOf(this.val$chartBean.getId()), this.val$chartBean.getAutoShiftActiveAsBoolean(), this.val$chartBean.getChartShiftInPx());
/*      */         }
/*      */       };
/*  724 */       chartBean.setStartLoadingDataRunnable(dataLoadingStarter);
/*  725 */       chartBeans.add(chartBean);
/*      */     }
/*      */ 
/*  728 */     return chartBeans;
/*      */   }
/*      */ 
/*      */   private Map<IChart, ITesterGui> createChartsPanels(DDSChartsController ddsChartsController, List<ChartBean> chartBeans) {
/*  732 */     Map chartPanels = new HashMap();
/*      */ 
/*  734 */     for (ChartBean chartBean : chartBeans) {
/*  735 */       ITesterGuiImpl chartGuiImpl = new ITesterGuiImpl();
/*      */ 
/*  738 */       JPanel chartPanel = ddsChartsController.createNewChartOrGetById(chartBean);
/*  739 */       setWaterMarkSign(chartPanel);
/*      */ 
/*  741 */       chartGuiImpl.setChartPanel(chartPanel);
/*      */ 
/*  744 */       ITesterChartController testerChartControl = new TesterChartControllerImpl(ddsChartsController, chartBean.getId(), this.indicatorsWrappers);
/*      */ 
/*  750 */       chartGuiImpl.setTesterChartControl(testerChartControl);
/*      */ 
/*  752 */       IChart chart = ddsChartsController.getIChartBy(Integer.valueOf(chartBean.getId()));
/*  753 */       chartPanels.put(chart, chartGuiImpl);
/*      */     }
/*      */ 
/*  756 */     return chartPanels;
/*      */   }
/*      */ 
/*      */   private void addTesterIndicators(ITesterVisualModeParameters testerVisualModeParameters, List<ChartBean> chartBeans, DDSChartsController ddsChartsController, StrategyDataStorageImpl strategyDataStorage)
/*      */   {
/*  765 */     if (testerVisualModeParameters == null) {
/*  766 */       return;
/*      */     }
/*      */ 
/*  769 */     LinkedList indicators = new LinkedList();
/*  770 */     Map testerIndicatorsParameters = testerVisualModeParameters.getTesterIndicatorsParameters();
/*  771 */     if (testerIndicatorsParameters == null) {
/*  772 */       return;
/*      */     }
/*      */ 
/*  775 */     for (ChartBean chartBean : chartBeans) {
/*  776 */       Instrument instrument = chartBean.getInstrument();
/*  777 */       ITesterIndicatorsParameters indicatorsParameters = (ITesterIndicatorsParameters)testerIndicatorsParameters.get(instrument);
/*      */ 
/*  779 */       if (indicatorShouldBeAdded(indicatorsParameters)) {
/*  780 */         ITheme theme = ThemeManager.getTheme(ddsChartsController.getTheme(chartBean.getId()));
/*      */ 
/*  782 */         if (indicatorsParameters.isEquityIndicatorEnabled()) {
/*  783 */           EquityHistoricalTesterIndicator indicator = new EquityHistoricalTesterIndicator(this.from, this.deposit);
/*  784 */           indicator.setInstrument(chartBean.getInstrument());
/*  785 */           IndicatorContext ctx = IndicatorHelper.createIndicatorContext();
/*  786 */           TesterIndicatorWrapper indicatorWrapper = new TesterIndicatorWrapper(indicator, ctx);
/*      */ 
/*  789 */           indicatorWrapper.setChangeTreeSelection(false);
/*      */ 
/*  791 */           indicatorWrapper.setLineWidth(0, 1);
/*  792 */           Color equityColor = theme.getColor(ITheme.ChartElement.HT_EQUITY);
/*  793 */           indicatorWrapper.setOutputColor(0, equityColor);
/*  794 */           indicatorWrapper.setOutputColor2(0, equityColor);
/*      */ 
/*  796 */           ddsChartsController.addIndicator(Integer.valueOf(chartBean.getId()), indicatorWrapper);
/*  797 */           indicators.add(indicator);
/*      */ 
/*  799 */           this.indicatorsWrappers.add(indicatorWrapper);
/*      */         }
/*      */ 
/*  802 */         if (indicatorsParameters.isBalanceIndicatorEnabled()) {
/*  803 */           BalanceHistoricalTesterIndicator indicator = new BalanceHistoricalTesterIndicator(this.from, this.deposit);
/*  804 */           indicator.setInstrument(chartBean.getInstrument());
/*  805 */           IndicatorContext ctx = IndicatorHelper.createIndicatorContext();
/*  806 */           TesterIndicatorWrapper indicatorWrapper = new TesterIndicatorWrapper(indicator, ctx);
/*      */ 
/*  809 */           indicatorWrapper.setChangeTreeSelection(false);
/*      */ 
/*  811 */           indicatorWrapper.setLineWidth(0, 1);
/*      */ 
/*  813 */           Color balanceColor = theme.getColor(ITheme.ChartElement.HT_BALANCE);
/*  814 */           indicatorWrapper.setOutputColor(0, balanceColor);
/*  815 */           indicatorWrapper.setOutputColor2(0, balanceColor);
/*      */ 
/*  817 */           ddsChartsController.addIndicator(Integer.valueOf(chartBean.getId()), indicatorWrapper);
/*  818 */           indicators.add(indicator);
/*      */ 
/*  820 */           this.indicatorsWrappers.add(indicatorWrapper);
/*      */         }
/*      */ 
/*  823 */         if (indicatorsParameters.isProfitLossIndicatorEnabled()) {
/*  824 */           ProfLossHistoricalTesterIndicator indicator = new ProfLossHistoricalTesterIndicator(this.from, this.deposit);
/*  825 */           indicator.setInstrument(chartBean.getInstrument());
/*  826 */           IndicatorContext ctx = IndicatorHelper.createIndicatorContext();
/*  827 */           TesterIndicatorWrapper indicatorWrapper = new TesterIndicatorWrapper(indicator, ctx);
/*      */ 
/*  830 */           indicatorWrapper.setChangeTreeSelection(false);
/*      */ 
/*  832 */           indicatorWrapper.setLineWidth(0, 1);
/*      */ 
/*  834 */           Color plColor = theme.getColor(ITheme.ChartElement.HT_PROFIT_LOSS);
/*  835 */           indicatorWrapper.setOutputColor(0, plColor);
/*  836 */           indicatorWrapper.setOutputColor2(0, plColor);
/*      */ 
/*  838 */           ddsChartsController.addIndicator(Integer.valueOf(chartBean.getId()), indicatorWrapper);
/*  839 */           indicators.add(indicator);
/*      */ 
/*  841 */           this.indicatorsWrappers.add(indicatorWrapper);
/*      */         }
/*      */       }
/*      */     }
/*      */ 
/*  846 */     if ((indicators.size() > 0) && (strategyDataStorage != null))
/*  847 */       for (IIndicator iIndicator : indicators)
/*  848 */         ((AbstractHistoricalTesterIndicator)iIndicator).setIndicatorStorage(strategyDataStorage);
/*      */   }
/*      */ 
/*      */   private boolean indicatorShouldBeAdded(ITesterIndicatorsParameters indicatorsParameters)
/*      */   {
/*  854 */     boolean addIndicator = false;
/*  855 */     if ((indicatorsParameters != null) && ((
/*  856 */       (indicatorsParameters.isEquityIndicatorEnabled()) || (indicatorsParameters.isBalanceIndicatorEnabled()) || (indicatorsParameters.isProfitLossIndicatorEnabled()))))
/*      */     {
/*  860 */       addIndicator = true;
/*      */     }
/*      */ 
/*  864 */     return addIndicator;
/*      */   }
/*      */ 
/*      */   private StrategyDataStorageImpl getStrategyDataStorage(ITesterVisualModeParameters testerVisualModeParameters, List<ChartBean> chartBeans)
/*      */   {
/*  871 */     StrategyDataStorageImpl strategyDataStorage = null;
/*  872 */     if (testerVisualModeParameters == null) {
/*  873 */       return strategyDataStorage;
/*      */     }
/*      */ 
/*  876 */     Map testerIndicatorsParameters = testerVisualModeParameters.getTesterIndicatorsParameters();
/*  877 */     if (testerIndicatorsParameters == null) {
/*  878 */       return strategyDataStorage;
/*      */     }
/*      */ 
/*  881 */     for (ChartBean chartBean : chartBeans) {
/*  882 */       ITesterIndicatorsParameters indicatorsParameters = (ITesterIndicatorsParameters)testerIndicatorsParameters.get(chartBean.getInstrument());
/*  883 */       if (indicatorShouldBeAdded(indicatorsParameters)) {
/*  884 */         strategyDataStorage = new StrategyDataStorageImpl();
/*  885 */         break;
/*      */       }
/*      */     }
/*      */ 
/*  889 */     return strategyDataStorage;
/*      */   }
/*      */ 
/*      */   private void setWaterMarkSign(Container panel) {
/*  893 */     for (Component c : panel.getComponents()) {
/*  894 */       if ((c.getName() != null) && (c.getName().equals("MainChartPanel")) && (c instanceof JComponent)) {
/*  895 */         ((JComponent)c).putClientProperty("watermark", Boolean.valueOf(true));
/*  896 */         return;
/*      */       }
/*  898 */       if (c instanceof Container)
/*  899 */         setWaterMarkSign((Container)c);
/*      */     }
/*      */   }
/*      */ 
/*      */   private Map<Instrument, TesterChartData> createTesterChartData(DDSChartsController ddsChartsController, List<ChartBean> chartBeans)
/*      */   {
/*  906 */     Map testerChartsData = new HashMap();
/*      */ 
/*  908 */     for (ChartBean chartBean : chartBeans) {
/*  909 */       IChart chart = ddsChartsController.getIChartBy(Integer.valueOf(chartBean.getId()));
/*      */ 
/*  911 */       TesterChartData chartData = new TesterChartData();
/*  912 */       chartData.instrument = chartBean.getInstrument();
/*  913 */       chartData.jForexPeriod = chartBean.getJForexPeriod();
/*  914 */       chartData.offerSide = chartBean.getOfferSide();
/*  915 */       chartData.feedDataProvider = chartBean.getFeedDataProvider();
/*  916 */       chartData.chartPanelId = chartBean.getId();
/*  917 */       chartData.chart = chart;
/*  918 */       testerChartsData.put(chartBean.getInstrument(), chartData);
/*      */     }
/*      */ 
/*  921 */     return testerChartsData;
/*      */   }
/*      */ 
/*      */   public DDSChartsController getDDSChartsController() {
/*  925 */     DDSChartsController ddsChartsController = null;
/*      */     try {
/*  927 */       Class ddsChartsControllerImpl = Thread.currentThread().getContextClassLoader().loadClass("com.dukascopy.charts.main.DDSChartsControllerImpl");
/*  928 */       Method method = ddsChartsControllerImpl.getMethod("getInstance", null);
/*  929 */       ddsChartsController = (DDSChartsController)method.invoke(ddsChartsControllerImpl, null);
/*      */     } catch (Exception e) {
/*  931 */       LOGGER.error(e.getMessage(), e);
/*  932 */       throw new RuntimeException("Cannot create DDSChartsController:" + e.getMessage());
/*      */     }
/*      */ 
/*  935 */     if (ddsChartsController == null) {
/*  936 */       throw new IllegalArgumentException("DDSChartsController is null");
/*      */     }
/*      */ 
/*  939 */     return ddsChartsController;
/*      */   }
/*      */ 
/*      */   private JForexPeriod getJForexPeriod()
/*      */   {
/*  944 */     JForexPeriod jForexPeriod = new JForexPeriod();
/*      */ 
/*  946 */     long HOURS = 3600000L;
/*  947 */     long DAYS = HOURS * 24L;
/*  948 */     long range = Math.abs(this.to - this.from);
/*      */ 
/*  950 */     if (range < HOURS * 22L)
/*      */     {
/*  952 */       jForexPeriod.setPeriod(Period.FIVE_MINS);
/*  953 */       jForexPeriod.setDataType(DataType.TIME_PERIOD_AGGREGATION);
/*      */     }
/*  955 */     else if (range < DAYS * 6L)
/*      */     {
/*  957 */       jForexPeriod.setPeriod(Period.TEN_MINS);
/*  958 */       jForexPeriod.setDataType(DataType.TIME_PERIOD_AGGREGATION);
/*      */     }
/*  960 */     else if (range < DAYS * 25L)
/*      */     {
/*  962 */       jForexPeriod.setPeriod(Period.THIRTY_MINS);
/*  963 */       jForexPeriod.setDataType(DataType.TIME_PERIOD_AGGREGATION);
/*      */     }
/*      */     else
/*      */     {
/*  967 */       jForexPeriod.setPeriod(Period.ONE_HOUR);
/*  968 */       jForexPeriod.setDataType(DataType.TIME_PERIOD_AGGREGATION);
/*      */     }
/*      */ 
/*  971 */     return jForexPeriod;
/*      */   }
/*      */ 
/*      */   public void setEventLogEnabled(boolean eventLogEnabled)
/*      */   {
/* 1022 */     this.eventLogEnabled = eventLogEnabled;
/*      */   }
/*      */ 
/*      */   public boolean getEventLogEnabled()
/*      */   {
/* 1027 */     return this.eventLogEnabled;
/*      */   }
/*      */ 
/*      */   public void setProcessingStatsEnabled(boolean processingStats)
/*      */   {
/* 1032 */     this.processingStatsEnabled = processingStats;
/*      */   }
/*      */ 
/*      */   public boolean getProcessingStats()
/*      */   {
/* 1037 */     return this.processingStatsEnabled;
/*      */   }
/*      */ 
/*      */   public IStrategy loadStrategy(File strategyBinaryFile) throws Exception {
/* 1041 */     JFXPack jfxPack = JFXPack.loadFromPack(strategyBinaryFile);
/* 1042 */     return ((IStrategy)jfxPack.getTarget());
/*      */   }
/*      */ 
/*      */   public synchronized void stopStrategy(long processId) {
/* 1046 */     if (!(this.strategies.containsKey(Long.valueOf(processId)))) {
/* 1047 */       return;
/*      */     }
/* 1049 */     ((StrategyStuff)this.strategies.remove(Long.valueOf(processId))).loadingProgressListener.cancel();
/*      */   }
/*      */ 
/*      */   public synchronized ISystemListener getSystemListener() {
/* 1053 */     return this.systemListener;
/*      */   }
/*      */ 
/*      */   public synchronized Map<Long, IStrategy> getStartedStrategies()
/*      */   {
/* 1058 */     HashMap startedStrategies = new HashMap();
/* 1059 */     for (StrategyStuff strategy : this.strategies.values()) {
/* 1060 */       if (strategy.strategyRunner != null) {
/* 1061 */         startedStrategies.put(Long.valueOf(strategy.processId), strategy.strategies[0]);
/*      */       }
/*      */     }
/* 1064 */     return startedStrategies;
/*      */   }
/*      */ 
/*      */   public synchronized void setSubscribedInstruments(Set<Instrument> instruments)
/*      */   {
/* 1069 */     for (Instrument instrument : new HashSet(instruments)) {
/* 1070 */       instruments.addAll(AbstractCurrencyConverter.getConversionDeps(instrument.getSecondaryCurrency(), Instrument.EURUSD.getSecondaryCurrency()));
/*      */     }
/* 1072 */     instruments.addAll(AbstractCurrencyConverter.getConversionDeps(Instrument.EURUSD.getSecondaryCurrency(), this.accountCurrency));
/* 1073 */     this.instruments = instruments;
/*      */   }
/*      */ 
/*      */   public synchronized INewsFilter getNewsFilter(INewsFilter.NewsSource newsSource)
/*      */   {
/* 1078 */     return null;
/*      */   }
/*      */ 
/*      */   public synchronized INewsFilter removeNewsFilter(INewsFilter.NewsSource newsSource)
/*      */   {
/* 1083 */     return null;
/*      */   }
/*      */ 
/*      */   public synchronized Set<Instrument> getSubscribedInstruments()
/*      */   {
/* 1088 */     return Collections.unmodifiableSet(this.instruments);
/*      */   }
/*      */ 
/*      */   public synchronized void addNewsFilter(INewsFilter newsFilter)
/*      */   {
/*      */   }
/*      */ 
/*      */   public synchronized void setOut(PrintStream out)
/*      */   {
/* 1097 */     this.out = out;
/* 1098 */     NotificationUtilsProvider.setNotificationUtils(new PrintStreamNotificationUtils(out, this.err));
/*      */   }
/*      */ 
/*      */   public synchronized void setErr(PrintStream err)
/*      */   {
/* 1103 */     this.err = err;
/* 1104 */     NotificationUtilsProvider.setNotificationUtils(new PrintStreamNotificationUtils(this.out, err));
/*      */   }
/*      */ 
/*      */   public void setDataInterval(Period period, OfferSide offerSide, ITesterClient.InterpolationMethod interpolationMethod, long from, long to)
/*      */   {
/* 1110 */     if (period == null) {
/* 1111 */       this.period = Period.TICK;
/*      */     }
/*      */ 
/* 1114 */     if (period == Period.TICK) {
/* 1115 */       setDataInterval(ITesterClient.DataLoadingMethod.ALL_TICKS, from, to);
/* 1116 */       return;
/*      */     }
/*      */ 
/* 1119 */     if (offerSide == null) {
/* 1120 */       offerSide = OfferSide.BID;
/*      */     }
/*      */ 
/* 1123 */     if ((period != Period.TICK) && (interpolationMethod == null)) {
/* 1124 */       throw new IllegalArgumentException("InterpolationMethod is null");
/*      */     }
/*      */ 
/* 1127 */     this.period = period;
/* 1128 */     this.offerSide = offerSide;
/* 1129 */     this.interpolationMethod = interpolationMethod;
/*      */ 
/* 1131 */     this.from = DataCacheUtils.getCandleStartFast(period, from);
/* 1132 */     this.to = DataCacheUtils.getCandleStartFast(period, to);
/* 1133 */     this.dataLoadingMethod = null;
/*      */   }
/*      */ 
/*      */   public void setDataInterval(ITesterClient.DataLoadingMethod dataLoadingMethod, long from, long to)
/*      */   {
/* 1138 */     this.dataLoadingMethod = dataLoadingMethod;
/* 1139 */     this.from = DataCacheUtils.getCandleStartFast(Period.ONE_SEC, from);
/* 1140 */     this.to = DataCacheUtils.getCandleStartFast(Period.ONE_SEC, to);
/* 1141 */     this.interpolationMethod = null;
/*      */   }
/*      */ 
/*      */   public Future<?> downloadData(com.dukascopy.api.LoadingProgressListener loadingProgressListener)
/*      */   {
/* 1146 */     TesterDataLoader testerDataLoader = new TesterDataLoader(this.from, this.to, this.instruments, new com.dukascopy.charts.data.datacache.LoadingProgressListener(loadingProgressListener)
/*      */     {
/*      */       public void dataLoaded(long startTime, long endTime, long currentTime, String information) {
/* 1149 */         if (this.val$loadingProgressListener != null)
/* 1150 */           this.val$loadingProgressListener.dataLoaded(startTime, endTime, currentTime, information);
/*      */       }
/*      */ 
/*      */       public void loadingFinished(boolean allDataLoaded, long startTime, long endTime, long currentTime, Exception e)
/*      */       {
/* 1156 */         if (this.val$loadingProgressListener != null)
/* 1157 */           this.val$loadingProgressListener.loadingFinished(allDataLoaded, startTime, endTime, currentTime);
/*      */       }
/*      */ 
/*      */       public boolean stopJob()
/*      */       {
/* 1163 */         return ((this.val$loadingProgressListener != null) && (this.val$loadingProgressListener.stopJob()));
/*      */       }
/*      */     });
/* 1167 */     FutureTask future = new FutureTask(new Runnable(testerDataLoader)
/*      */     {
/*      */       public void run() {
/* 1170 */         this.val$testerDataLoader.loadData();
/*      */       }
/*      */     }
/*      */     , null);
/*      */ 
/* 1174 */     Thread thread = new Thread(future, "Data loading thread");
/* 1175 */     thread.start();
/* 1176 */     return future;
/*      */   }
/*      */ 
/*      */   public void setGatherReportData(boolean gatherReportData)
/*      */   {
/* 1181 */     this.gatherReportData = gatherReportData;
/*      */   }
/*      */ 
/*      */   public boolean getGatherReportData()
/*      */   {
/* 1186 */     return this.gatherReportData;
/*      */   }
/*      */ 
/*      */   public synchronized void createReport(File file) throws IOException, IllegalStateException
/*      */   {
/* 1191 */     StrategyStuff strategy = (StrategyStuff)this.strategies.get(Long.valueOf(1L));
/* 1192 */     if ((strategy == null) || (strategy.reportFiles == null)) {
/* 1193 */       throw new IllegalStateException("Report data is not available");
/*      */     }
/* 1195 */     OutputStream os = new BufferedOutputStream(new FileOutputStream(file));
/*      */     try {
/* 1197 */       InputStream is = new BufferedInputStream(new FileInputStream(strategy.reportFiles[0]));
/*      */       try {
/* 1199 */         byte[] buff = new byte[8192];
/*      */ 
/* 1201 */         while ((i = is.read(buff)) != -1)
/*      */         {
/*      */           int i;
/* 1202 */           os.write(buff, 0, i);
/*      */         }
/*      */       } finally {
/* 1205 */         is.close();
/*      */       }
/*      */     } finally {
/* 1208 */       os.close();
/*      */     }
/*      */   }
/*      */ 
/*      */   public synchronized void createReport(long processId, File file)
/*      */     throws IOException, IllegalStateException
/*      */   {
/* 1215 */     StrategyStuff strategy = (StrategyStuff)this.strategies.get(Long.valueOf(processId));
/* 1216 */     if ((strategy != null) && (strategy.reportFiles != null)) {
/* 1217 */       OutputStream os = new BufferedOutputStream(new FileOutputStream(file));
/*      */       try {
/* 1219 */         InputStream is = new BufferedInputStream(new FileInputStream(strategy.reportFiles[0]));
/*      */         try {
/* 1221 */           byte[] buff = new byte[8192];
/*      */ 
/* 1223 */           while ((i = is.read(buff)) != -1)
/*      */           {
/*      */             int i;
/* 1224 */             os.write(buff, 0, i);
/*      */           }
/*      */         } finally {
/* 1227 */           is.close();
/*      */         }
/*      */       } finally {
/* 1230 */         os.close();
/*      */       }
/*      */     } else {
/* 1233 */       throw new IllegalStateException("Report data is not available");
/*      */     }
/*      */   }
/*      */ 
/*      */   public File saveReportToFile(TesterReportData reportData, Currency accountCurrency, boolean eventLogEnabled)
/*      */     throws IOException
/*      */   {
/* 1264 */     File tempFile = File.createTempFile("jfrep", null);
/* 1265 */     tempFile.deleteOnExit();
/* 1266 */     StrategyReport.createReport(tempFile, reportData, accountCurrency, eventLogEnabled);
/* 1267 */     return tempFile;
/*      */   }
/*      */ 
/*      */   public void setInitialDeposit(Currency currency, double deposit) throws IllegalArgumentException
/*      */   {
/* 1272 */     Set majors = AbstractCurrencyConverter.getMajors();
/* 1273 */     if (currency == null) {
/* 1274 */       this.accountCurrency = Instrument.EURUSD.getSecondaryCurrency(); } else {
/* 1275 */       if (!(majors.contains(currency))) {
/* 1276 */         throw new IllegalArgumentException("Currency [" + currency.getCurrencyCode() + "] cannot be set as an account currency");
/*      */       }
/* 1278 */       this.accountCurrency = currency;
/*      */     }
/*      */ 
/* 1281 */     setSubscribedInstruments(this.instruments);
/*      */ 
/* 1283 */     this.deposit = deposit;
/*      */   }
/*      */ 
/*      */   public double getInitialDeposit()
/*      */   {
/* 1288 */     return this.deposit;
/*      */   }
/*      */ 
/*      */   public Currency getInitialDepositCurrency()
/*      */   {
/* 1293 */     return this.accountCurrency;
/*      */   }
/*      */ 
/*      */   public void setLeverage(int leverage)
/*      */   {
/* 1298 */     this.leverage = leverage;
/*      */   }
/*      */ 
/*      */   public int getLeverage()
/*      */   {
/* 1303 */     return this.leverage;
/*      */   }
/*      */ 
/*      */   public void setCommissions(Commissions commission)
/*      */   {
/* 1308 */     this.commission = commission;
/*      */   }
/*      */ 
/*      */   public Commissions getCommissions()
/*      */   {
/* 1313 */     return this.commission;
/*      */   }
/*      */ 
/*      */   public void setOvernights(Overnights overnights)
/*      */   {
/* 1318 */     this.overnights = overnights;
/*      */   }
/*      */ 
/*      */   public Overnights getOvernights()
/*      */   {
/* 1323 */     return this.overnights;
/*      */   }
/*      */ 
/*      */   public void setMarginCutLevel(int marginCutLevel)
/*      */   {
/* 1328 */     this.marginCutLevel = marginCutLevel;
/*      */   }
/*      */ 
/*      */   public int getMarginCutLevel()
/*      */   {
/* 1333 */     return this.marginCutLevel;
/*      */   }
/*      */ 
/*      */   public void setMCEquity(double mcEquity)
/*      */   {
/* 1338 */     this.mcEquity = mcEquity;
/*      */   }
/*      */ 
/*      */   public double getMCEquity()
/*      */   {
/* 1343 */     return this.mcEquity;
/*      */   }
/*      */ 
/*      */   public void setCacheDirectory(File cacheDirectory)
/*      */   {
/* 1348 */     if (!(cacheDirectory.exists())) {
/* 1349 */       LOGGER.warn("Cache directory [" + cacheDirectory + "] doesn't exist, trying to create");
/* 1350 */       if (!(cacheDirectory.mkdirs())) {
/* 1351 */         LOGGER.error("Cannot create cache directory [" + cacheDirectory + "], default cache directory will be used");
/* 1352 */         return;
/*      */       }
/*      */     }
/* 1355 */     FilePathManager.getInstance().setCacheFolderPath(cacheDirectory.getAbsolutePath());
/*      */   }
/*      */ 
/*      */   private int getNextChartId()
/*      */   {
/* 1492 */     return (this.chart_id++);
/*      */   }
/*      */ 
/*      */   public String getSessionID() {
/* 1496 */     return this.sessionID;
/*      */   }
/*      */ 
/*      */   public StrategyRunner getStrategyRunner(long processID) {
/* 1500 */     return ((StrategyRunner)((StrategyStuff)this.strategies.get(Long.valueOf(processID))).strategyRunner);
/*      */   }
/*      */ 
/*      */   private class TesterExecutionControl
/*      */     implements ITesterExecutionControl
/*      */   {
/* 1459 */     ExecutionControl executionControl = null;
/*      */ 
/*      */     public TesterExecutionControl(ExecutionControl paramExecutionControl) {
/* 1462 */       this.executionControl = paramExecutionControl;
/*      */     }
/*      */ 
/*      */     public void pauseExecution()
/*      */     {
/* 1467 */       this.executionControl.pause();
/*      */     }
/*      */ 
/*      */     public boolean isExecutionPaused()
/*      */     {
/* 1472 */       return this.executionControl.isPaused();
/*      */     }
/*      */ 
/*      */     public void continueExecution()
/*      */     {
/* 1477 */       this.executionControl.run();
/*      */     }
/*      */ 
/*      */     public void cancelExecution()
/*      */     {
/* 1482 */       this.executionControl.stopExecuting(true);
/*      */     }
/*      */ 
/*      */     public boolean isExecutionCanceled()
/*      */     {
/* 1487 */       return (!(this.executionControl.isExecuting()));
/*      */     }
/*      */   }
/*      */ 
/*      */   private static class StrategyStuff
/*      */   {
/*      */     private long processId;
/*      */     private IStrategy[] strategies;
/*      */     private TesterReportData[] reportDatas;
/*      */     private IStrategyRunner strategyRunner;
/*      */     private TesterClientImpl.TesterLoadingProgressListener loadingProgressListener;
/*      */     private File[] reportFiles;
/*      */   }
/*      */ 
/*      */   private class TesterLoadingProgressListener
/*      */     implements com.dukascopy.charts.data.datacache.LoadingProgressListener
/*      */   {
/*      */     private boolean cancel;
/*      */     private TesterClientImpl.StrategyStuff strategy;
/*      */     private com.dukascopy.api.LoadingProgressListener testerProgressListener;
/*      */     private TesterClientImpl.TesterExecutionControl testerExecutionControl;
/*      */     private Currency accountCurrency;
/*      */     private boolean eventLogEnabled;
/*      */ 
/*      */     public TesterLoadingProgressListener(TesterClientImpl.StrategyStuff paramStrategyStuff, com.dukascopy.api.LoadingProgressListener paramLoadingProgressListener, Currency paramCurrency, boolean paramBoolean)
/*      */     {
/* 1385 */       this.accountCurrency = accountCurrency;
/* 1386 */       this.eventLogEnabled = paramBoolean;
/* 1387 */       this.strategy = paramStrategyStuff;
/* 1388 */       this.testerProgressListener = paramLoadingProgressListener;
/*      */     }
/*      */ 
/*      */     public TesterLoadingProgressListener(TesterClientImpl.StrategyStuff paramStrategyStuff, com.dukascopy.api.LoadingProgressListener paramLoadingProgressListener, TesterClientImpl.TesterExecutionControl paramTesterExecutionControl, Currency paramCurrency, boolean paramBoolean)
/*      */     {
/* 1393 */       this.accountCurrency = paramCurrency;
/* 1394 */       this.eventLogEnabled = paramBoolean;
/* 1395 */       this.strategy = paramStrategyStuff;
/* 1396 */       this.testerProgressListener = paramLoadingProgressListener;
/* 1397 */       this.testerExecutionControl = paramTesterExecutionControl;
/*      */     }
/*      */ 
/*      */     public void dataLoaded(long startTime, long endTime, long currentTime, String information)
/*      */     {
/* 1402 */       if (this.testerProgressListener != null)
/* 1403 */         this.testerProgressListener.dataLoaded(startTime, endTime, currentTime, information);
/*      */     }
/*      */ 
/*      */     public void loadingFinished(boolean allDataLoaded, long startTime, long endTime, long currentTime, Exception ex)
/*      */     {
/* 1408 */       synchronized (TesterClientImpl.this) {
/* 1409 */         if ((!(allDataLoaded)) && (ex != null)) {
/* 1410 */           TesterClientImpl.LOGGER.error(ex.getMessage(), ex);
/*      */         }
/* 1412 */         if ((((this.testerProgressListener == null) || (!(this.testerProgressListener.stopJob())))) && (!(this.strategy.strategyRunner.wasCanceled()))) {
/* 1413 */           TesterClientImpl.StrategyStuff.access$1302(this.strategy, new File[this.strategy.strategies.length]);
/* 1414 */           for (int i = 0; i < this.strategy.strategies.length; ++i) {
/*      */             try {
/* 1416 */               this.strategy.reportFiles[i] = TesterClientImpl.this.saveReportToFile(TesterClientImpl.StrategyStuff.access$1200(this.strategy)[i], this.accountCurrency, this.eventLogEnabled);
/*      */             } catch (IOException e) {
/* 1418 */               TesterClientImpl.LOGGER.error(e.getMessage(), e);
/*      */             }
/*      */           }
/*      */         }
/* 1422 */         TesterClientImpl.StrategyStuff.access$1002(this.strategy, null);
/* 1423 */         TesterClientImpl.StrategyStuff.access$1202(this.strategy, null);
/* 1424 */         TesterClientImpl.StrategyStuff.access$902(this.strategy, null);
/* 1425 */         TesterClientImpl.StrategyStuff.access$602(this.strategy, null);
/* 1426 */         if (TesterClientImpl.this.systemListener != null) {
/* 1427 */           TesterClientImpl.this.systemListener.onStop(this.strategy.processId);
/*      */         }
/* 1429 */         if (this.testerProgressListener != null) {
/* 1430 */           this.testerProgressListener.loadingFinished(allDataLoaded, startTime, endTime, currentTime);
/* 1431 */           this.testerProgressListener = null;
/*      */         }
/*      */       }
/*      */     }
/*      */ 
/*      */     public boolean stopJob() {
/* 1437 */       return ((this.cancel) || ((this.testerProgressListener != null) && (this.testerProgressListener.stopJob())) || ((this.testerExecutionControl != null) && (this.testerExecutionControl.isExecutionCanceled())));
/*      */     }
/*      */ 
/*      */     public void cancel()
/*      */     {
/* 1444 */       this.cancel = true;
/*      */     }
/*      */   }
/*      */ 
/*      */   private class DefaultStrategyExceptionHandler
/*      */     implements IStrategyExceptionHandler
/*      */   {
/* 1359 */     private final Logger LOGGER = LoggerFactory.getLogger(DefaultStrategyExceptionHandler.class);
/*      */     private TesterClientImpl.StrategyStuff strategy;
/*      */ 
/*      */     private DefaultStrategyExceptionHandler(TesterClientImpl.StrategyStuff paramStrategyStuff)
/*      */     {
/* 1364 */       this.strategy = paramStrategyStuff;
/*      */     }
/*      */ 
/*      */     public void onException(long strategyId, IStrategyExceptionHandler.Source source, Throwable t)
/*      */     {
/* 1369 */       this.LOGGER.error("Exception thrown while running " + source + " method: " + t.getMessage(), t);
/* 1370 */       this.strategy.loadingProgressListener.cancel();
/*      */     }
/*      */   }
/*      */ }

/* Location:           G:\javaworksvn\JForexClientLibrary\libs\greed-common-177.jar
 * Qualified Name:     com.dukascopy.api.impl.connect.TesterClientImpl
 * Java Class Version: 6 (50.0)
 * JD-Core Version:    0.5.3
 */