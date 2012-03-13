/*      */ package com.dukascopy.api.impl.connect;
/*      */ 
/*      */ import com.dukascopy.api.Configurable;
/*      */ import com.dukascopy.api.ConsoleAdapter;
/*      */ import com.dukascopy.api.IConsole;
/*      */ import com.dukascopy.api.INewsFilter;
/*      */ import com.dukascopy.api.INewsFilter.NewsSource;
/*      */ import com.dukascopy.api.IStrategy;
/*      */ import com.dukascopy.api.IStrategyListener;
/*      */ import com.dukascopy.api.Instrument;
/*      */ import com.dukascopy.api.JFException;
/*      */ import com.dukascopy.api.Period;
/*      */ import com.dukascopy.api.impl.StrategyEventsCallback;
/*      */ import com.dukascopy.api.system.IClient;
/*      */ import com.dukascopy.api.system.IStrategyExceptionHandler;
/*      */ import com.dukascopy.api.system.IStrategyExceptionHandler.Source;
/*      */ import com.dukascopy.api.system.ISystemListener;
/*      */ import com.dukascopy.api.system.JFAuthenticationException;
/*      */ import com.dukascopy.api.system.JFVersionException;
/*      */ import com.dukascopy.charts.data.datacache.DataCacheException;
/*      */ import com.dukascopy.charts.data.datacache.FeedDataProvider;
/*      */ import com.dukascopy.charts.data.datacache.InstrumentSubscriptionListener;
/*      */ import com.dukascopy.charts.data.datacache.LiveFeedListener;
/*      */ import com.dukascopy.charts.data.datacache.LoadingProgressListener;
/*      */ import com.dukascopy.charts.data.datacache.TickData;
/*      */ import com.dukascopy.charts.data.datacache.feed.IFeedCommissionManager;
/*      */ import com.dukascopy.charts.data.orders.OrdersProvider;
/*      */ import com.dukascopy.charts.math.indicators.IndicatorsProvider;
/*      */ import com.dukascopy.dds2.greed.agent.compiler.JFXPack;
/*      */ import com.dukascopy.dds2.greed.util.AbstractCurrencyConverter;
/*      */ import com.dukascopy.dds2.greed.util.EnumConverter;
/*      */ import com.dukascopy.dds2.greed.util.FilePathManager;
/*      */ import com.dukascopy.dds2.greed.util.INotificationUtils;
/*      */ import com.dukascopy.dds2.greed.util.NotificationUtilsProvider;
/*      */ import com.dukascopy.dds2.greed.util.ObjectUtils;
/*      */ import com.dukascopy.transport.client.ClientListener;
/*      */ import com.dukascopy.transport.client.SecurityExceptionHandler;
/*      */ import com.dukascopy.transport.client.TransportClient;
/*      */ import com.dukascopy.transport.client.events.DisconnectedEvent;
/*      */ import com.dukascopy.transport.common.model.type.Money;
/*      */ import com.dukascopy.transport.common.msg.ProtocolMessage;
/*      */ import com.dukascopy.transport.common.msg.group.MarketNewsMessageGroup;
/*      */ import com.dukascopy.transport.common.msg.group.OrderGroupMessage;
/*      */ import com.dukascopy.transport.common.msg.group.OrderMessage;
/*      */ import com.dukascopy.transport.common.msg.group.OrderSyncMessage;
/*      */ import com.dukascopy.transport.common.msg.news.CalendarEvent.CalendarType;
/*      */ import com.dukascopy.transport.common.msg.news.EventCategory;
/*      */ import com.dukascopy.transport.common.msg.news.GeoRegion;
/*      */ import com.dukascopy.transport.common.msg.news.MarketSector;
/*      */ import com.dukascopy.transport.common.msg.news.NewsRequestType;
/*      */ import com.dukascopy.transport.common.msg.news.NewsSource;
/*      */ import com.dukascopy.transport.common.msg.news.NewsStoryMessage;
/*      */ import com.dukascopy.transport.common.msg.news.NewsSubscribeRequest;
/*      */ import com.dukascopy.transport.common.msg.news.StockIndex;
/*      */ import com.dukascopy.transport.common.msg.request.AccountInfoMessage;
/*      */ import com.dukascopy.transport.common.msg.request.CurrencyMarket;
/*      */ import com.dukascopy.transport.common.msg.request.CurrencyOffer;
/*      */ import com.dukascopy.transport.common.msg.request.InitRequestMessage;
/*      */ import com.dukascopy.transport.common.msg.request.MergePositionsMessage;
/*      */ import com.dukascopy.transport.common.msg.request.QuitRequestMessage;
/*      */ import com.dukascopy.transport.common.msg.request.QuoteSubscribeRequestMessage;
/*      */ import com.dukascopy.transport.common.msg.response.InstrumentStatusUpdateMessage;
/*      */ import com.dukascopy.transport.common.msg.response.NotificationMessage;
/*      */ import com.dukascopy.transport.common.msg.response.OkResponseMessage;
/*      */ import com.dukascopy.transport.common.msg.strategy.StrategyBroadcastMessage;
/*      */ import com.dukascopy.transport.util.Hex;
/*      */ import java.awt.image.BufferedImage;
/*      */ import java.io.File;
/*      */ import java.io.IOException;
/*      */ import java.io.InputStream;
/*      */ import java.io.PrintStream;
/*      */ import java.lang.reflect.Field;
/*      */ import java.math.BigDecimal;
/*      */ import java.net.InetAddress;
/*      */ import java.net.InetSocketAddress;
/*      */ import java.net.URL;
/*      */ import java.net.URLConnection;
/*      */ import java.net.UnknownHostException;
/*      */ import java.security.AccessController;
/*      */ import java.security.GeneralSecurityException;
/*      */ import java.security.MessageDigest;
/*      */ import java.security.PrivilegedActionException;
/*      */ import java.security.PrivilegedExceptionAction;
/*      */ import java.security.cert.CertificateException;
/*      */ import java.security.cert.X509Certificate;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Arrays;
/*      */ import java.util.Calendar;
/*      */ import java.util.Collection;
/*      */ import java.util.Date;
/*      */ import java.util.HashMap;
/*      */ import java.util.HashSet;
/*      */ import java.util.Iterator;
/*      */ import java.util.List;
/*      */ import java.util.Map;
/*      */ import java.util.Map.Entry;
/*      */ import java.util.Properties;
/*      */ import java.util.Set;
/*      */ import java.util.TimeZone;
/*      */ import java.util.UUID;
/*      */ import java.util.concurrent.Executor;
/*      */ import java.util.concurrent.LinkedBlockingQueue;
/*      */ import java.util.concurrent.ThreadFactory;
/*      */ import java.util.concurrent.ThreadPoolExecutor;
/*      */ import java.util.concurrent.TimeUnit;
/*      */ import java.util.concurrent.atomic.AtomicInteger;
/*      */ import java.util.regex.Matcher;
/*      */ import java.util.regex.Pattern;
/*      */ import javax.xml.parsers.DocumentBuilder;
/*      */ import javax.xml.parsers.DocumentBuilderFactory;
/*      */ import org.slf4j.Logger;
/*      */ import org.slf4j.LoggerFactory;
/*      */ import org.w3c.dom.Document;
/*      */ import org.w3c.dom.Element;
/*      */ import org.w3c.dom.NodeList;
/*      */ 
/*      */ public class DCClientImpl
/*      */   implements IClient, ClientListener
/*      */ {
/*   66 */   private static final Logger LOGGER = LoggerFactory.getLogger(DCClientImpl.class);
/*      */ 
/*   68 */   private static final long TRANSPORT_PING_TIMEOUT = TimeUnit.SECONDS.toMillis(10L);
/*      */   private static final String SNAPSHOT = "SNAPSHOT";
/*      */   private static final String DEFAULT_VERSION = "99.99.99";
/*      */   private static final String MD5 = "MD5";
/*      */   private static final String EXTENSION_JFX = ".jfx";
/*      */   private static final String EXTENSION_CLASS = ".class";
/*      */   private static final String FEED_COMMISSION_HISTORY = "feed.commission.history";
/*      */   private static final String SUPPORTED_INSTRUMENTS = "instruments";
/*      */   private static final String HISTORY_SERVER_URL = "history.server.url";
/*      */   private static final String ENCRYPTION_KEY = "encryptionKey";
/*      */   private static final String SERVICES1_URL = "services1.url";
/*      */   private static final String TRADELOG_SFX = "tradelog_sfx.url";
/*      */   private static final String EXTERNAL_IP_ADDRESS = "external_ip";
/*      */   private static final String INFO = "INFO";
/*      */   private static final String WARNING = "WARNING";
/*      */   private static final String ERROR = "ERROR";
/*      */   private volatile ISystemListenerExtended systemListener;
/*      */   private TransportClient transportClient;
/*   90 */   private boolean initialized = false;
/*      */   private boolean live;
/*      */   private String accountName;
/*      */   private String version;
/*      */   private Executor minaExecutor;
/*   96 */   private Set<Instrument> instruments = new HashSet();
/*      */   private PrintStream out;
/*      */   private PrintStream err;
/*      */   private Properties serverProperties;
/*      */   private String internalIP;
/*      */   private String sessionID;
/*      */   private String captchaId;
/*  104 */   private long temporaryKeys = -1L;
/*  105 */   private Map<Long, IStrategy> runningStrategies = new HashMap();
/*  106 */   private Map<Long, JForexTaskManager> strategyEngines = new HashMap();
/*      */ 
/*  108 */   private Map<INewsFilter.NewsSource, INewsFilter> newsFilters = new HashMap();
/*      */   private AccountInfoMessage lastAccountInfoMessage;
/*  112 */   private IConsole console = new ConsoleAdapter() {
/*      */     public PrintStream getOut() {
/*  114 */       return DCClientImpl.this.out;
/*      */     }
/*      */ 
/*      */     public PrintStream getErr() {
/*  118 */       return DCClientImpl.this.err;
/*      */     }
/*  112 */   };
/*      */ 
/*      */   public DCClientImpl()
/*      */   {
/*  123 */     ThreadFactory threadFactory = new ThreadFactory() {
/*      */       final AtomicInteger threadNumber;
/*      */ 
/*      */       public Thread newThread(Runnable r) {
/*  127 */         Thread thread = new Thread(r, "Mina_Thread_" + this.threadNumber.getAndIncrement());
/*  128 */         if (!(thread.isDaemon())) {
/*  129 */           thread.setDaemon(true);
/*      */         }
/*  131 */         return thread;
/*      */       }
/*      */     };
/*  134 */     this.minaExecutor = new ThreadPoolExecutor(1, 5, 5L, TimeUnit.SECONDS, new LinkedBlockingQueue(), threadFactory);
/*  135 */     this.out = System.out;
/*  136 */     this.err = System.err;
/*  137 */     NotificationUtilsProvider.setNotificationUtils(new PrintStreamNotificationUtils(this.out, this.err));
/*      */ 
/*  139 */     this.version = super.getClass().getPackage().getImplementationVersion();
/*  140 */     if ((this.version == null) || (this.version.endsWith("SNAPSHOT"))) {
/*  141 */       this.version = "99.99.99";
/*      */     }
/*  143 */     this.serverProperties = new Properties();
/*      */   }
/*      */ 
/*      */   public synchronized void connect(String jnlpUrl, String username, String password) throws Exception
/*      */   {
/*  148 */     connect(jnlpUrl, username, password, null);
/*      */   }
/*      */ 
/*      */   public synchronized void connect(String jnlpUrl, String username, String password, String pin) throws Exception
/*      */   {
/*  153 */     if (this.transportClient == null) {
/*  154 */       String authServersCsv = getAuthServers(jnlpUrl);
/*  155 */       this.sessionID = UUID.randomUUID().toString();
/*  156 */       String[] urlList = authServersCsv.split(",");
/*  157 */       List authServers = Arrays.asList(urlList);
/*  158 */       String ticket = authenticate(authServers, this.sessionID, username, password, true, pin);
/*      */ 
/*  160 */       this.accountName = username;
/*      */ 
/*  165 */       FilePathManager fmanager = FilePathManager.getInstance();
/*  166 */       fmanager.setStrategiesFolderPath(fmanager.getDefaultStrategiesFolderPath());
/*      */ 
/*  168 */       OrdersProvider.createInstance(null);
/*      */ 
/*  170 */       String servicesUrl = new StringBuilder().append(this.serverProperties.getProperty("services1.url", "")).append(this.serverProperties.getProperty("tradelog_sfx.url", "")).toString();
/*  171 */       ActivityLogger.init(servicesUrl, username);
/*  172 */       List feedCommissions = (List)this.serverProperties.get("feed.commission.history");
/*  173 */       Set supportedInstruments = (Set)this.serverProperties.get("instruments");
/*  174 */       FeedDataProvider.createFeedDataProvider("SINGLEJAR", feedCommissions, supportedInstruments);
/*  175 */       FeedDataProvider.setPlatformTicket(ticket);
/*  176 */       FeedDataProvider.getDefaultInstance().connectToHistoryServer(authServers, username, this.sessionID, this.serverProperties.getProperty("history.server.url", null), this.serverProperties.getProperty("encryptionKey", null), this.version);
/*      */ 
/*  184 */       FeedDataProvider.getDefaultInstance().addInstrumentSubscriptionListener(new InstrumentSubscriptionListener()
/*      */       {
/*      */         public void subscribedToInstrument(Instrument instrument) {
/*  187 */           DCClientImpl.this.fakeTickOnWeekends(instrument);
/*      */         }
/*      */ 
/*      */         public void unsubscribedFromInstrument(Instrument instrument)
/*      */         {
/*      */         }
/*      */       });
/*  193 */       FeedDataProvider.getDefaultInstance().setInstrumentsSubscribed(this.instruments);
/*      */ 
/*  195 */       IndicatorsProvider.createInstance(new IndicatorsSettingsStorage(username));
/*      */ 
/*  197 */       this.transportClient.connect();
/*      */     } else {
/*  199 */       if (this.transportClient.isOnline())
/*      */         return;
/*  201 */       this.lastAccountInfoMessage = null;
/*  202 */       this.initialized = false;
/*  203 */       String authServersCsv = getAuthServers(jnlpUrl);
/*  204 */       this.sessionID = UUID.randomUUID().toString();
/*  205 */       String[] urlList = authServersCsv.split(",");
/*  206 */       List authServerUrls = Arrays.asList(urlList);
/*  207 */       String ticket = authenticate(authServerUrls, this.sessionID, username, password, true, pin);
/*  208 */       String servicesUrl = new StringBuilder().append(this.serverProperties.getProperty("services1.url", "")).append(this.serverProperties.getProperty("tradelog_sfx.url", "")).toString();
/*  209 */       ActivityLogger.init(servicesUrl, username);
/*  210 */       FeedDataProvider.setPlatformTicket(ticket);
/*  211 */       FeedDataProvider.getDefaultInstance().getFeedCommissionManager().clear();
/*  212 */       FeedDataProvider.getDefaultInstance().connectToHistoryServer(authServerUrls, username, this.sessionID, this.serverProperties.getProperty("history.server.url", null), this.serverProperties.getProperty("encryptionKey", null), this.version);
/*      */ 
/*  220 */       this.transportClient.connect();
/*      */     }
/*      */   }
/*      */ 
/*      */   public synchronized void connect(Collection<String> authServerUrls, boolean live, String username, String password, boolean encodePassword) throws Exception
/*      */   {
/*  226 */     connect(authServerUrls, live, username, password, encodePassword, null);
/*      */   }
/*      */ 
/*      */   public synchronized void connect(Collection<String> authServerUrls, boolean live, String username, String password, boolean encodePassword, String pin) throws Exception {
/*  230 */     this.live = live;
/*  231 */     if (this.transportClient == null) {
/*  232 */       String session = UUID.randomUUID().toString();
/*  233 */       String ticket = authenticate(authServerUrls, session, username, password, encodePassword, pin);
/*      */ 
/*  235 */       this.accountName = username;
/*      */ 
/*  239 */       OrdersProvider.createInstance(null);
/*      */ 
/*  241 */       String servicesUrl = new StringBuilder().append(this.serverProperties.getProperty("services1.url", "")).append(this.serverProperties.getProperty("tradelog_sfx.url", "")).toString();
/*  242 */       ActivityLogger.init(servicesUrl, username);
/*  243 */       List feedCommissions = (List)this.serverProperties.get("feed.commission.history");
/*  244 */       Set supportedInstruments = (Set)this.serverProperties.get("instruments");
/*  245 */       FeedDataProvider.createFeedDataProvider("SINGLEJAR", feedCommissions, supportedInstruments);
/*  246 */       FeedDataProvider.setPlatformTicket(ticket);
/*  247 */       FeedDataProvider.getDefaultInstance().connectToHistoryServer(authServerUrls, username, session, this.serverProperties.getProperty("history.server.url", null), this.serverProperties.getProperty("encryptionKey", null), this.version);
/*      */ 
/*  255 */       FeedDataProvider.getDefaultInstance().addInstrumentSubscriptionListener(new InstrumentSubscriptionListener()
/*      */       {
/*      */         public void subscribedToInstrument(Instrument instrument) {
/*  258 */           DCClientImpl.this.fakeTickOnWeekends(instrument);
/*      */         }
/*      */ 
/*      */         public void unsubscribedFromInstrument(Instrument instrument)
/*      */         {
/*      */         }
/*      */       });
/*  264 */       FeedDataProvider.getDefaultInstance().setInstrumentsSubscribed(this.instruments);
/*      */ 
/*  266 */       IndicatorsProvider.createInstance(new IndicatorsSettingsStorage(username));
/*      */ 
/*  268 */       this.transportClient.connect();
/*      */     } else {
/*  270 */       if (this.transportClient.isOnline())
/*      */         return;
/*  272 */       this.lastAccountInfoMessage = null;
/*  273 */       this.initialized = false;
/*  274 */       this.sessionID = UUID.randomUUID().toString();
/*  275 */       String ticket = authenticate(authServerUrls, this.sessionID, username, password, encodePassword, pin);
/*  276 */       String servicesUrl = new StringBuilder().append(this.serverProperties.getProperty("services1.url", "")).append(this.serverProperties.getProperty("tradelog_sfx.url", "")).toString();
/*  277 */       ActivityLogger.init(servicesUrl, username);
/*  278 */       FeedDataProvider.getDefaultInstance().getFeedCommissionManager().clear();
/*  279 */       FeedDataProvider.setPlatformTicket(ticket);
/*  280 */       FeedDataProvider.getDefaultInstance().connectToHistoryServer(authServerUrls, username, this.sessionID, this.serverProperties.getProperty("history.server.url", null), this.serverProperties.getProperty("encryptionKey", null), this.version);
/*      */ 
/*  288 */       this.transportClient.connect();
/*      */     }
/*      */   }
/*      */ 
/*      */   private String getAuthServers(String jnlp) throws Exception
/*      */   {
/*  294 */     URL jnlpUrl = new URL(jnlp);
/*      */ 
/*  296 */     InputStream jnlpIs = jnlpUrl.openConnection().getInputStream();
/*      */     Document doc;
/*      */     try {
/*  299 */       DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
/*  300 */       doc = builder.parse(jnlpIs);
/*      */     } finally {
/*  302 */       jnlpIs.close();
/*      */     }
/*      */ 
/*  305 */     String authServersCsv = null;
/*  306 */     NodeList jnlpNodes = doc.getElementsByTagName("jnlp");
/*  307 */     if (jnlpNodes.getLength() < 1) {
/*  308 */       throw new Exception("Can't find jnlp element");
/*      */     }
/*  310 */     Element jnlpNode = (Element)jnlpNodes.item(0);
/*  311 */     NodeList resourcesNodes = jnlpNode.getElementsByTagName("resources");
/*  312 */     if (resourcesNodes.getLength() < 1) {
/*  313 */       throw new Exception("Can't find resources element");
/*      */     }
/*  315 */     Element resourcesNode = (Element)resourcesNodes.item(0);
/*  316 */     NodeList propertyNodes = resourcesNode.getElementsByTagName("property");
/*  317 */     for (int i = 0; i < propertyNodes.getLength(); ++i) {
/*  318 */       Element propertyElement = (Element)propertyNodes.item(i);
/*      */ 
/*  320 */       String nameAttribute = propertyElement.getAttribute("name");
/*  321 */       if ((nameAttribute != null) && (nameAttribute.trim().equals("jnlp.client.mode"))) {
/*  322 */         String clientMode = propertyElement.getAttribute("value").trim();
/*  323 */         this.live = ((clientMode != null) && (clientMode.equals("LIVE")));
/*  324 */       } else if ((nameAttribute != null) && (nameAttribute.trim().equals("jnlp.login.url"))) {
/*  325 */         authServersCsv = propertyElement.getAttribute("value").trim();
/*      */       }
/*      */     }
/*  328 */     if (authServersCsv == null) {
/*  329 */       throw new Exception("Can't find property with name attribute equals to jnlp.login.url");
/*      */     }
/*  331 */     return authServersCsv;
/*      */   }
/*      */ 
/*      */   public BufferedImage getCaptchaImage(String jnlp) throws Exception
/*      */   {
/*  336 */     String authServersCsv = getAuthServers(jnlp);
/*  337 */     String[] urlList = authServersCsv.split(",");
/*  338 */     List authServers = Arrays.asList(urlList);
/*  339 */     AuthorizationClient authorizationClient = AuthorizationClient.getInstance(authServers, this.version);
/*  340 */     Map imageCaptchaMap = authorizationClient.getImageCaptcha();
/*  341 */     if (!(imageCaptchaMap.isEmpty())) {
/*  342 */       Map.Entry imageCaptchaEntry = (Map.Entry)imageCaptchaMap.entrySet().iterator().next();
/*  343 */       this.captchaId = ((String)imageCaptchaEntry.getKey());
/*  344 */       return ((BufferedImage)imageCaptchaEntry.getValue());
/*      */     }
/*  346 */     return null;
/*      */   }
/*      */ 
/*      */   private String authenticate(Collection<String> authServerUrls, String session, String username, String password, boolean encodePassword, String pin) throws Exception
/*      */   {
/*  351 */     AuthorizationClient authorizationClient = AuthorizationClient.getInstance(authServerUrls, this.version);
/*      */ 
/*  353 */     if ((!(encodePassword)) && (pin != null))
/*  354 */       throw new Exception("(EncodePassword == false && pin != null) == NOT SUPPORTED");
/*      */     String authResponse;
/*      */     String authResponse;
/*  357 */     if ((!(encodePassword)) || (pin == null))
/*  358 */       authResponse = authorizationClient.getUrlAndTicket(username, password, session, encodePassword);
/*      */     else {
/*  360 */       authResponse = authorizationClient.getUrlAndTicket(username, password, this.captchaId, pin, session);
/*      */     }
/*      */ 
/*  363 */     LOGGER.debug(authResponse);
/*      */ 
/*  365 */     if ("-1".equals(authResponse)) {
/*  366 */       throw new JFAuthenticationException("Incorrect username or password");
/*      */     }
/*      */ 
/*  369 */     if ("-2".equals(authResponse)) {
/*  370 */       throw new JFVersionException("Incorrect version");
/*      */     }
/*      */ 
/*  373 */     if ("-3".equals(authResponse)) {
/*  374 */       throw new JFVersionException("System offline");
/*      */     }
/*      */ 
/*  377 */     if ("-500".equals(authResponse)) {
/*  378 */       throw new JFVersionException("System error");
/*      */     }
/*      */ 
/*  381 */     if (authResponse == null) {
/*  382 */       throw new IOException("Authentication failed");
/*      */     }
/*      */ 
/*  385 */     Matcher matcher = AuthorizationClient.RESULT_PATTERN.matcher(authResponse);
/*  386 */     if (!(matcher.matches())) {
/*  387 */       throw new IOException(new StringBuilder().append("Authentication procedure returned unexpected result [").append(authResponse).append("]").toString());
/*      */     }
/*      */ 
/*  392 */     String url = matcher.group(1);
/*  393 */     int semicolonIndex = url.indexOf(58);
/*      */     int port;
/*      */     String host;
/*      */     int port;
/*  394 */     if (semicolonIndex != -1) {
/*  395 */       String host = url.substring(0, semicolonIndex);
/*      */       int port;
/*  396 */       if (semicolonIndex + 1 >= url.length()) {
/*  397 */         LOGGER.warn("port not set, using default 443");
/*  398 */         port = 443;
/*      */       } else {
/*  400 */         port = Integer.parseInt(url.substring(semicolonIndex + 1));
/*      */       }
/*      */     } else {
/*  403 */       host = url;
/*  404 */       port = 443;
/*      */     }
/*      */ 
/*  407 */     InetSocketAddress address = new InetSocketAddress(InetAddress.getByName(host), port);
/*      */ 
/*  409 */     String ticket = matcher.group(2);
/*  410 */     Properties properties = authorizationClient.getAllProperties(username, ticket, session);
/*  411 */     if (properties != null) {
/*  412 */       this.serverProperties.putAll(properties);
/*      */     }
/*      */     try
/*      */     {
/*  416 */       InetAddress localhost = InetAddress.getLocalHost();
/*  417 */       this.internalIP = ((localhost != null) ? localhost.getHostAddress() : null);
/*      */     } catch (UnknownHostException e) {
/*  419 */       LOGGER.error(new StringBuilder().append("Can't detect local IP : ").append(e.getMessage()).toString());
/*  420 */       this.internalIP = "";
/*      */     }
/*      */ 
/*  423 */     if (this.transportClient == null) {
/*  424 */       this.transportClient = new TransportClient(address, null, null, this, this.minaExecutor);
/*  425 */       String userAgent = getUserAgent(this.version);
/*  426 */       LOGGER.debug(new StringBuilder().append("UserAgent: ").append(userAgent).toString());
/*  427 */       this.transportClient.setUserAgent(userAgent);
/*      */ 
/*  429 */       this.transportClient.setPingConnection(true);
/*  430 */       this.transportClient.setPingTimeout(Long.valueOf(TRANSPORT_PING_TIMEOUT));
/*  431 */       this.transportClient.setPoolSize(2);
/*      */ 
/*  433 */       this.transportClient.setUseSsl(true);
/*  434 */       this.transportClient.setSecurityExceptionHandler(new SecurityExceptionHandler()
/*      */       {
/*      */         public boolean isIgnoreSecurityException(X509Certificate[] chain, String authType, CertificateException ex) {
/*  437 */           DCClientImpl.LOGGER.warn("Security exception : " + ex);
/*  438 */           return true;
/*      */         } } );
/*      */     }
/*      */     else {
/*  442 */       this.transportClient.setAddress(address);
/*      */     }
/*      */ 
/*  445 */     this.transportClient.setLogin(new StringBuilder().append(username).append(" ").append(session).toString());
/*  446 */     this.transportClient.setPassword(ticket);
/*      */ 
/*  448 */     return ticket;
/*      */   }
/*      */ 
/*      */   public static String getUserAgent(String version) {
/*  452 */     String javaVersion = System.getProperty("java.vm.version");
/*  453 */     String osName = System.getProperty("os.name");
/*      */ 
/*  455 */     String stratString = " Strategy Enabled";
/*  456 */     String stratBuildVersionNr = IStrategy.class.getPackage().getImplementationVersion();
/*  457 */     if (stratBuildVersionNr == null) {
/*  458 */       stratBuildVersionNr = "0";
/*      */     }
/*  460 */     stratBuildVersionNr = new StringBuilder().append(".").append(stratBuildVersionNr).toString();
/*  461 */     String minaString = "";
/*      */     try {
/*  463 */       Class.forName("org.apache.mina.common.IoSession");
/*  464 */       minaString = " Mina ";
/*      */     }
/*      */     catch (ClassNotFoundException e)
/*      */     {
/*      */     }
/*  469 */     return new StringBuilder().append("DCClientImpl/").append(version).append(stratBuildVersionNr).append(" (JVM/").append(javaVersion).append("; ").append(osName).append(") ").append(stratString).append(minaString).toString();
/*      */   }
/*      */ 
/*      */   public synchronized void reconnect()
/*      */   {
/*  477 */     if ((this.transportClient != null) && (!(this.transportClient.isOnline())))
/*  478 */       this.transportClient.connect();
/*      */   }
/*      */ 
/*      */   public synchronized void disconnect()
/*      */   {
/*  486 */     for (Long processId : getStartedStrategies().keySet()) {
/*  487 */       stopStrategy(processId.longValue());
/*      */     }
/*  489 */     if (FeedDataProvider.getDefaultInstance() != null)
/*  490 */       FeedDataProvider.getDefaultInstance().close();
/*      */     try
/*      */     {
/*  493 */       TimeUnit.SECONDS.sleep(1L); } catch (InterruptedException e) {
/*      */     }
/*  495 */     if (this.transportClient != null) {
/*  496 */       if (this.transportClient.isOnline()) {
/*      */         try {
/*  498 */           this.transportClient.controlRequest(new QuitRequestMessage());
/*      */         } catch (Throwable e) {
/*  500 */           LOGGER.error(e.getMessage(), e);
/*      */         }
/*      */       }
/*  503 */       this.transportClient.disconnect();
/*  504 */       this.transportClient.terminate();
/*  505 */       this.transportClient = null;
/*      */     }
/*  507 */     this.newsFilters.clear();
/*  508 */     this.instruments.clear();
/*  509 */     this.captchaId = null;
/*  510 */     this.lastAccountInfoMessage = null;
/*  511 */     this.accountName = null;
/*  512 */     this.internalIP = null;
/*  513 */     this.sessionID = null;
/*  514 */     this.live = false;
/*  515 */     this.serverProperties = new Properties();
/*  516 */     this.initialized = false;
/*      */   }
/*      */ 
/*      */   public boolean isConnected()
/*      */   {
/*  521 */     return ((this.transportClient != null) && (this.transportClient.isOnline()) && (this.initialized));
/*      */   }
/*      */ 
/*      */   private void connectedInit()
/*      */   {
/*  526 */     InitRequestMessage initRequestMessage = new InitRequestMessage();
/*  527 */     initRequestMessage.setSendGroups(true);
/*  528 */     this.transportClient.controlRequest(initRequestMessage);
/*  529 */     setSubscribedInstruments(this.instruments);
/*      */ 
/*  531 */     for (INewsFilter newsFilter : this.newsFilters.values()) {
/*  532 */       NewsSubscribeRequest newsSubscribeRequest = new NewsSubscribeRequest();
/*      */ 
/*  534 */       newsSubscribeRequest.setNewsSource(NewsSource.valueOf(newsFilter.getNewsSource().name()));
/*  535 */       newsSubscribeRequest.setHot(newsFilter.isOnlyHot());
/*  536 */       newsSubscribeRequest.setGeoRegions(EnumConverter.convert(newsFilter.getCountries(), GeoRegion.class));
/*  537 */       newsSubscribeRequest.setMarketSectors(EnumConverter.convert(newsFilter.getMarketSectors(), MarketSector.class));
/*  538 */       newsSubscribeRequest.setIndicies(EnumConverter.convert(newsFilter.getStockIndicies(), StockIndex.class));
/*  539 */       newsSubscribeRequest.setCurrencies(EnumConverter.convert(newsFilter.getCurrencies(), com.dukascopy.transport.common.msg.news.Currency.class));
/*  540 */       newsSubscribeRequest.setEventCategories(EnumConverter.convert(newsFilter.getEventCategories(), EventCategory.class));
/*      */ 
/*  542 */       newsSubscribeRequest.setKeywords(newsFilter.getKeywords());
/*  543 */       newsSubscribeRequest.setFromDate(newsFilter.getFrom());
/*  544 */       newsSubscribeRequest.setToDate(newsFilter.getTo());
/*  545 */       newsSubscribeRequest.setCalendarType((CalendarEvent.CalendarType)EnumConverter.convert(newsFilter.getType(), CalendarEvent.CalendarType.class));
/*      */ 
/*  547 */       LOGGER.debug(new StringBuilder().append("Subscribing : ").append(newsSubscribeRequest).toString());
/*      */ 
/*  549 */       this.transportClient.controlRequest(newsSubscribeRequest);
/*      */     }
/*      */   }
/*      */ 
/*      */   public synchronized void setSystemListener(ISystemListener userSystemListener) {
/*  554 */     this.systemListener = new Object (userSystemListener)
/*      */     {
/*      */       public void onStart(long processId) {
/*  557 */         this.val$userSystemListener.onStart(processId);
/*      */       }
/*      */ 
/*      */       public void onStop(long processId) {
/*  561 */         synchronized (DCClientImpl.this) {
/*  562 */           DCClientImpl.this.strategyEngines.remove(Long.valueOf(processId));
/*  563 */           DCClientImpl.this.runningStrategies.remove(Long.valueOf(processId));
/*      */         }
/*  565 */         this.val$userSystemListener.onStop(processId);
/*  566 */         ActivityLogger.getInstance().flush();
/*      */       }
/*      */ 
/*      */       public void onConnect() {
/*  570 */         this.val$userSystemListener.onConnect();
/*      */       }
/*      */ 
/*      */       public void onDisconnect() {
/*  574 */         this.val$userSystemListener.onDisconnect();
/*      */       }
/*      */ 
/*      */       public void subscribeToInstruments(Set<Instrument> instruments)
/*      */       {
/*  579 */         Set combinedInstruments = new HashSet(DCClientImpl.this.instruments);
/*  580 */         combinedInstruments.addAll(instruments);
/*  581 */         DCClientImpl.this.setSubscribedInstruments(instruments);
/*      */       }
/*      */ 
/*      */       public Set<Instrument> getSubscribedInstruments()
/*      */       {
/*  586 */         synchronized (DCClientImpl.this) {
/*  587 */           return new HashSet(DCClientImpl.this.instruments);
/*      */         }
/*      */       }
/*      */ 
/*      */       public long startStrategy(File jfxFile, IStrategyListener listener, Map<String, Object> configurables, boolean fullAccess) throws JFException
/*      */       {
/*  593 */         if (!(jfxFile.exists()))
/*  594 */           throw new JFException("File [" + jfxFile + "] does not exist");
/*      */         IStrategy iStrategy;
/*      */         try
/*      */         {
/*  598 */           iStrategy = DCClientImpl.this.loadStrategy(jfxFile);
/*      */         } catch (Exception e) {
/*  600 */           throw new JFException(e);
/*      */         }
/*      */ 
/*  603 */         if (configurables != null) {
/*  604 */           Field[] fields = iStrategy.getClass().getFields();
/*  605 */           for (Field field : fields) {
/*  606 */             Configurable configurable = (Configurable)field.getAnnotation(Configurable.class);
/*  607 */             if (configurable == null) continue;
/*      */             try {
/*  609 */               if (configurables.containsKey(field.getName()))
/*  610 */                 field.set(iStrategy, configurables.get(field.getName()));
/*      */             }
/*      */             catch (Exception e) {
/*  613 */               throw new JFException("Error while setting value for the field [" + field.getName() + "]", e);
/*      */             }
/*      */           }
/*      */ 
/*      */         }
/*      */ 
/*  619 */         if ((System.getSecurityManager() == null) && (!(fullAccess))) {
/*  620 */           throw new JFException("Strategy tries to start another strategy with fullAccess disabled, this will not work without security manager. Either start the strategy with full access or set up security manager");
/*      */         }
/*      */ 
/*  624 */         return DCClientImpl.this.startStrategy(iStrategy, listener, null, JForexTaskManager.Environment.LOCAL_EMBEDDED, true, null);
/*      */       }
/*      */ 
/*      */       public long startStrategy(IStrategy strategy, IStrategyListener listener, boolean fullAccess) throws JFException
/*      */       {
/*  629 */         if ((System.getSecurityManager() == null) && (!(fullAccess))) {
/*  630 */           throw new JFException("Strategy tries to start another strategy with fullAccess disabled, this will not work without security manager. Either start the strategy with full access or set up security manager");
/*      */         }
/*      */ 
/*  633 */         return DCClientImpl.this.startStrategy(strategy, listener, null, JForexTaskManager.Environment.LOCAL_EMBEDDED, fullAccess, null);
/*      */       }
/*      */ 
/*      */       public void stopStrategy(long strategyId)
/*      */       {
/*  638 */         DCClientImpl.this.stopStrategy(strategyId);
/*      */       }
/*      */     };
/*  641 */     for (JForexTaskManager taskManager : this.strategyEngines.values())
/*  642 */       taskManager.setSystemListener(this.systemListener);
/*      */   }
/*      */ 
/*      */   public synchronized void setStrategyEventsListener(StrategyEventsCallback strategyEventsCallback)
/*      */   {
/*  647 */     for (JForexTaskManager taskManager : this.strategyEngines.values())
/*  648 */       taskManager.setStrategyEventsCallback(strategyEventsCallback);
/*      */   }
/*      */ 
/*      */   public synchronized void feedbackMessageReceived(TransportClient client, ProtocolMessage message)
/*      */   {
/*  654 */     if ((client != null) && ((
/*  655 */       (!(client.isOnline())) || (client.isTerminated()) || (ObjectUtils.isNullOrEmpty(this.transportClient)) || (!(ObjectUtils.isEqual(client.getSessionId(), this.transportClient.getSessionId()))))))
/*      */     {
/*  658 */       return;
/*      */     }
/*      */ 
/*  661 */     if (message instanceof AccountInfoMessage) {
/*  662 */       PlatformAccountImpl.updateStaticValues((AccountInfoMessage)message);
/*  663 */       FeedDataProvider.getDefaultInstance().setCurrentTime(message.getTimestamp().getTime());
/*  664 */       OrdersProvider.getInstance().updateAccountInfoData((AccountInfoMessage)message);
/*  665 */       this.lastAccountInfoMessage = ((AccountInfoMessage)message);
/*  666 */       Map feedCommissions = this.lastAccountInfoMessage.getFeedCommissions();
/*  667 */       if (feedCommissions != null) {
/*  668 */         FeedDataProvider.getDefaultInstance().getFeedCommissionManager().addFeedCommissions(feedCommissions, message.getTimeSyncMs());
/*      */       }
/*  670 */       if (!(this.initialized))
/*      */       {
/*  672 */         setSubscribedInstruments(this.instruments);
/*  673 */         this.initialized = true;
/*  674 */         fireConnected();
/*      */       }
/*  676 */     } else if (message instanceof OrderGroupMessage) {
/*  677 */       OrderGroupMessage orderGroupMessage = (OrderGroupMessage)message;
/*  678 */       OrdersProvider.getInstance().updateOrderGroup(orderGroupMessage);
/*  679 */       String instrumentStr = orderGroupMessage.getInstrument();
/*  680 */       if (instrumentStr != null) {
/*  681 */         Instrument instrument = Instrument.fromString(instrumentStr);
/*  682 */         if (!(this.instruments.contains(instrument))) {
/*  683 */           LOGGER.info(new StringBuilder().append("Order group received for instrument [").append(instrument).append("], adding instrument to the list of the subscribed instruments").toString());
/*  684 */           setSubscribedInstruments(this.instruments);
/*      */         }
/*      */       } else {
/*  687 */         OrderMessage openingOrder = orderGroupMessage.getOpeningOrder();
/*  688 */         instrumentStr = (openingOrder == null) ? null : openingOrder.getInstrument();
/*  689 */         if (instrumentStr != null) {
/*  690 */           Instrument instrument = Instrument.fromString(instrumentStr);
/*  691 */           if (!(this.instruments.contains(instrument))) {
/*  692 */             LOGGER.info(new StringBuilder().append("Order group received for instrument [").append(instrument).append("], adding instrument to the list of the subscribed instruments").toString());
/*  693 */             setSubscribedInstruments(this.instruments);
/*      */           }
/*      */         }
/*      */       }
/*  697 */     } else if (message instanceof OrderMessage) {
/*  698 */       OrdersProvider.getInstance().updateOrder((OrderMessage)message);
/*  699 */       String instrumentStr = ((OrderMessage)message).getInstrument();
/*  700 */       if (instrumentStr != null) {
/*  701 */         Instrument instrument = Instrument.fromString(instrumentStr);
/*  702 */         if (!(this.instruments.contains(instrument))) {
/*  703 */           LOGGER.info(new StringBuilder().append("Order received for instrument [").append(instrument).append("], adding instrument to the list of the subscribed instruments").toString());
/*  704 */           setSubscribedInstruments(this.instruments);
/*      */         }
/*      */       }
/*  707 */     } else if (message instanceof MergePositionsMessage) {
/*  708 */       OrdersProvider.getInstance().groupsMerged((MergePositionsMessage)message);
/*  709 */     } else if ((message instanceof CurrencyMarket) && 
/*  710 */       (this.lastAccountInfoMessage != null)) {
/*  711 */       CurrencyMarket currencyMarket = (CurrencyMarket)message;
/*  712 */       if (this.instruments.contains(Instrument.fromString(currencyMarket.getInstrument()))) {
/*  713 */         FeedDataProvider.getDefaultInstance().tickReceived(currencyMarket);
/*      */       }
/*      */     }
/*      */ 
/*  717 */     for (JForexTaskManager taskManager : this.strategyEngines.values())
/*  718 */       if (message instanceof CurrencyMarket) {
/*  719 */         if (this.lastAccountInfoMessage != null) {
/*  720 */           CurrencyMarket currencyMarket = (CurrencyMarket)message;
/*  721 */           if (this.instruments.contains(Instrument.fromString(currencyMarket.getInstrument())))
/*  722 */             taskManager.onMarketState(currencyMarket);
/*      */         }
/*      */       }
/*  725 */       else if (message instanceof OrderGroupMessage) {
/*  726 */         taskManager.onOrderGroupReceived((OrderGroupMessage)message);
/*  727 */       } else if (message instanceof OrderMessage) {
/*  728 */         taskManager.onOrderReceived((OrderMessage)message);
/*  729 */       } else if (message instanceof NotificationMessage) {
/*  730 */         NotificationMessage notificationMessage = (NotificationMessage)message;
/*  731 */         if ((notificationMessage.getLevel() == null) || (notificationMessage.getLevel().equals("INFO")))
/*  732 */           NotificationUtilsProvider.getNotificationUtils().postInfoMessage(notificationMessage.getText());
/*  733 */         else if (notificationMessage.getLevel().equals("WARNING"))
/*  734 */           NotificationUtilsProvider.getNotificationUtils().postWarningMessage(notificationMessage.getText());
/*  735 */         else if (notificationMessage.getLevel().equals("ERROR"))
/*  736 */           NotificationUtilsProvider.getNotificationUtils().postErrorMessage(notificationMessage.getText());
/*      */         else {
/*  738 */           NotificationUtilsProvider.getNotificationUtils().postErrorMessage(notificationMessage.getText());
/*      */         }
/*  740 */         taskManager.onNotifyMessage(notificationMessage);
/*  741 */       } else if (message instanceof AccountInfoMessage) {
/*  742 */         taskManager.updateAccountInfo((AccountInfoMessage)message);
/*  743 */       } else if (message instanceof MergePositionsMessage) {
/*  744 */         taskManager.onOrdersMergedMessage((MergePositionsMessage)message);
/*  745 */       } else if (message instanceof OrderSyncMessage) {
/*  746 */         OrdersProvider.getInstance().orderSynch((OrderSyncMessage)message);
/*  747 */         taskManager.orderSynch((OrderSyncMessage)message);
/*  748 */         taskManager.onConnect(true);
/*  749 */       } else if (message instanceof InstrumentStatusUpdateMessage) {
/*  750 */         Instrument instrument = Instrument.fromString(((InstrumentStatusUpdateMessage)message).getInstrument());
/*  751 */         boolean tradable = ((InstrumentStatusUpdateMessage)message).getTradable() == 0;
/*  752 */         taskManager.onIntrumentUpdate(instrument, tradable, (message.getTimestamp() == null) ? FeedDataProvider.getDefaultInstance().getCurrentTime() : message.getTimestamp().getTime());
/*  753 */       } else if (message instanceof NewsStoryMessage) {
/*  754 */         taskManager.onNewsMessage((NewsStoryMessage)message);
/*  755 */       } else if (message instanceof StrategyBroadcastMessage) {
/*  756 */         onStrategyBroadcast(taskManager, (StrategyBroadcastMessage)message);
/*  757 */       } else if (!(message instanceof OkResponseMessage)) {
/*  758 */         if (!(message instanceof MarketNewsMessageGroup))
/*      */         {
/*  762 */           LOGGER.debug(new StringBuilder().append("Unrecognized protocol message : ").append(message.getClass()).append(" / ").append(message).toString());
/*  763 */           return;
/*      */         }
/*      */       }
/*      */   }
/*      */ 
/*      */   public synchronized void authorized(TransportClient client)
/*      */   {
/*  770 */     connectedInit();
/*      */ 
/*  772 */     if (this.initialized)
/*  773 */       fireConnected();
/*      */   }
/*      */ 
/*      */   private void fireConnected()
/*      */   {
/*  779 */     ISystemListener systemListener = this.systemListener;
/*  780 */     if (systemListener != null) {
/*  781 */       systemListener.onConnect();
/*      */     }
/*      */ 
/*  784 */     FeedDataProvider.getDefaultInstance().connected();
/*      */   }
/*      */ 
/*      */   public synchronized void disconnected(DisconnectedEvent event)
/*      */   {
/*  789 */     if (FeedDataProvider.getDefaultInstance() != null) {
/*  790 */       FeedDataProvider.getDefaultInstance().disconnected();
/*      */     }
/*      */ 
/*  793 */     ISystemListener systemListener = this.systemListener;
/*  794 */     if (systemListener != null) {
/*  795 */       systemListener.onDisconnect();
/*      */     }
/*  797 */     for (JForexTaskManager taskManager : this.strategyEngines.values())
/*  798 */       taskManager.onConnect(false);
/*      */   }
/*      */ 
/*      */   public long startStrategy(IStrategy strategy) throws IllegalStateException, NullPointerException
/*      */   {
/*  803 */     return startStrategy(strategy, null, null, JForexTaskManager.Environment.LOCAL_EMBEDDED, true, null);
/*      */   }
/*      */ 
/*      */   public long startStrategy(IStrategy strategy, IStrategyExceptionHandler exceptionHandler)
/*      */   {
/*  808 */     return startStrategy(strategy, null, exceptionHandler, JForexTaskManager.Environment.LOCAL_EMBEDDED, true, null);
/*      */   }
/*      */ 
/*      */   public long startStrategy(IStrategy strategy, IStrategyListener strategyListener, IStrategyExceptionHandler exceptionHandler, JForexTaskManager.Environment taskManagerEnvironment, boolean fullAccessGranted, String strategyHash)
/*      */     throws IllegalStateException, NullPointerException
/*      */   {
/*      */     JForexTaskManager taskManager;
/*      */     long temporaryProcessId;
/*  821 */     synchronized (this) {
/*  822 */       if (!(this.transportClient.isOnline())) {
/*  823 */         throw new IllegalStateException("Not connected");
/*      */       }
/*  825 */       if (!(this.initialized)) {
/*  826 */         throw new IllegalStateException("Not initialized");
/*      */       }
/*  828 */       if (strategy == null) {
/*  829 */         throw new NullPointerException("Strategy is null");
/*      */       }
/*  831 */       if (exceptionHandler == null) {
/*  832 */         exceptionHandler = new DefaultStrategyExceptionHandler(null);
/*      */       }
/*      */ 
/*  836 */       taskManager = new JForexTaskManager(taskManagerEnvironment, this.live, this.accountName, this.console, this.transportClient, null, new FacelessUserInterface(), exceptionHandler, this.lastAccountInfoMessage, this.serverProperties.getProperty("external_ip"), this.internalIP, this.sessionID);
/*      */ 
/*  851 */       taskManager.setSystemListener(this.systemListener);
/*  852 */       if (exceptionHandler instanceof DefaultStrategyExceptionHandler) {
/*  853 */         ((DefaultStrategyExceptionHandler)exceptionHandler).setTaskManager(taskManager);
/*      */       }
/*      */ 
/*  856 */       temporaryProcessId = this.temporaryKeys;
/*  857 */       this.temporaryKeys -= 1L;
/*  858 */       this.strategyEngines.put(Long.valueOf(temporaryProcessId), taskManager);
/*  859 */       this.runningStrategies.put(Long.valueOf(temporaryProcessId), strategy);
/*      */     }
/*      */ 
/*  863 */     String strategyKey = strategy.getClass().getSimpleName();
/*  864 */     if (strategyHash == null) {
/*  865 */       String classMD5 = getClassMD5(strategy);
/*  866 */       strategyKey = new StringBuilder().append(strategyKey).append(".class").append((classMD5 == null) ? "" : new StringBuilder().append(" ").append(classMD5).toString()).toString();
/*      */     } else {
/*  868 */       strategyKey = new StringBuilder().append(strategyKey).append(".jfx ").append(strategyHash).toString();
/*      */     }
/*      */ 
/*  871 */     long processId = taskManager.startStrategy(strategy, strategyListener, strategyKey, fullAccessGranted);
/*  872 */     synchronized (this) {
/*  873 */       if (processId == 0L) {
/*  874 */         this.strategyEngines.remove(Long.valueOf(temporaryProcessId));
/*  875 */         this.runningStrategies.remove(Long.valueOf(temporaryProcessId));
/*      */       } else {
/*  877 */         FeedDataProvider.getDefaultInstance().subscribeToAllCandlePeriods(taskManager);
/*  878 */         this.strategyEngines.remove(Long.valueOf(temporaryProcessId));
/*  879 */         this.runningStrategies.remove(Long.valueOf(temporaryProcessId));
/*      */ 
/*  881 */         this.strategyEngines.put(Long.valueOf(processId), taskManager);
/*  882 */         this.runningStrategies.put(Long.valueOf(processId), strategy);
/*      */       }
/*  884 */       return processId;
/*      */     }
/*      */   }
/*      */ 
/*      */   public String getClassMD5(IStrategy strategy) {
/*      */     try {
/*  890 */       String className = new StringBuilder().append(strategy.getClass().getName().replace('.', '/')).append(".class").toString();
/*  891 */       MessageDigest md = MessageDigest.getInstance("MD5");
/*  892 */       InputStream is = strategy.getClass().getClassLoader().getResourceAsStream(className);
/*  893 */       if (is != null)
/*      */       {
/*  895 */         byte[] buff = new byte[16384];
/*  896 */         while ((i = is.read(buff)) != -1)
/*      */         {
/*      */           int i;
/*  897 */           md.update(buff, 0, i);
/*      */         }
/*  899 */         return Hex.encodeHexString(md.digest()).toUpperCase();
/*      */       }
/*  901 */       return null;
/*      */     }
/*      */     catch (Exception e) {
/*  904 */       LOGGER.error(e.getMessage(), e); }
/*  905 */     return null;
/*      */   }
/*      */ 
/*      */   public IStrategy loadStrategy(File strategyBinaryFile) throws IOException, GeneralSecurityException
/*      */   {
/*  910 */     JFXPack jfxPack = JFXPack.loadFromPack(strategyBinaryFile);
/*  911 */     return ((IStrategy)jfxPack.getTarget());
/*      */   }
/*      */ 
/*      */   public synchronized void stopStrategy(long processId) {
/*  915 */     if (!(this.runningStrategies.containsKey(Long.valueOf(processId)))) {
/*  916 */       return;
/*      */     }
/*  918 */     JForexTaskManager taskManager = (JForexTaskManager)this.strategyEngines.remove(Long.valueOf(processId));
/*  919 */     taskManager.stopStrategy();
/*  920 */     this.runningStrategies.remove(Long.valueOf(processId));
/*      */   }
/*      */ 
/*      */   public synchronized ISystemListener getSystemListener() {
/*  924 */     return this.systemListener;
/*      */   }
/*      */ 
/*      */   public synchronized Map<Long, IStrategy> getStartedStrategies()
/*      */   {
/*  929 */     return new HashMap(this.runningStrategies);
/*      */   }
/*      */ 
/*      */   public synchronized void setSubscribedInstruments(Set<Instrument> instruments)
/*      */   {
/*      */     try {
/*  935 */       AccessController.doPrivileged(new PrivilegedExceptionAction(instruments)
/*      */       {
/*      */         public Void run() throws Exception {
/*  938 */           for (JForexTaskManager engine : DCClientImpl.this.strategyEngines.values()) {
/*  939 */             this.val$instruments.addAll(engine.getRequiredInstruments());
/*      */           }
/*  941 */           if (DCClientImpl.this.transportClient != null) {
/*  942 */             this.val$instruments.addAll(OrdersProvider.getInstance().getOrderInstruments());
/*      */           }
/*  944 */           for (Instrument instrument : new HashSet(this.val$instruments)) {
/*  945 */             this.val$instruments.addAll(AbstractCurrencyConverter.getConversionDeps(instrument.getSecondaryCurrency(), Instrument.EURUSD.getSecondaryCurrency()));
/*      */           }
/*  947 */           if (DCClientImpl.this.lastAccountInfoMessage != null) {
/*  948 */             this.val$instruments.addAll(AbstractCurrencyConverter.getConversionDeps(Instrument.EURUSD.getSecondaryCurrency(), DCClientImpl.this.lastAccountInfoMessage.getCurrency()));
/*      */           }
/*  950 */           if (DCClientImpl.this.transportClient != null) {
/*  951 */             QuoteSubscribeRequestMessage quoteSubscribeRequestMessage = new QuoteSubscribeRequestMessage();
/*  952 */             quoteSubscribeRequestMessage.setInstruments(new ArrayList(Instrument.toStringSet(this.val$instruments)));
/*  953 */             quoteSubscribeRequestMessage.setQuotesOnly(Boolean.valueOf(false));
/*  954 */             DCClientImpl.this.transportClient.controlRequest(quoteSubscribeRequestMessage);
/*      */           }
/*  956 */           if (FeedDataProvider.getDefaultInstance() != null) {
/*  957 */             FeedDataProvider.getDefaultInstance().setInstrumentsSubscribed(this.val$instruments);
/*      */           }
/*  959 */           DCClientImpl.access$602(DCClientImpl.this, new HashSet(this.val$instruments));
/*      */ 
/*  961 */           return null;
/*      */         } } );
/*      */     }
/*      */     catch (PrivilegedActionException ex) {
/*  965 */       throw new RuntimeException("Instruments subscription error", ex.getException());
/*      */     }
/*      */   }
/*      */ 
/*      */   public synchronized INewsFilter getNewsFilter(INewsFilter.NewsSource newsSource)
/*      */   {
/*  971 */     return ((INewsFilter)this.newsFilters.get(newsSource));
/*      */   }
/*      */ 
/*      */   public synchronized INewsFilter removeNewsFilter(INewsFilter.NewsSource newsSource)
/*      */   {
/*  976 */     if ((this.transportClient != null) && (this.transportClient.isOnline())) {
/*  977 */       NewsSubscribeRequest newsUnsubscribeRequest = new NewsSubscribeRequest();
/*  978 */       newsUnsubscribeRequest.setRequestType(NewsRequestType.UNSUBSCRIBE);
/*  979 */       newsUnsubscribeRequest.setNewsSource(NewsSource.valueOf(newsSource.name()));
/*      */ 
/*  981 */       LOGGER.debug(new StringBuilder().append("Unsubscribing : ").append(newsUnsubscribeRequest).toString());
/*      */ 
/*  983 */       this.transportClient.controlRequest(newsUnsubscribeRequest);
/*  984 */       return ((INewsFilter)this.newsFilters.remove(newsSource));
/*      */     }
/*  986 */     return null;
/*      */   }
/*      */ 
/*      */   public synchronized Set<Instrument> getSubscribedInstruments()
/*      */   {
/*  992 */     return new HashSet(this.instruments);
/*      */   }
/*      */ 
/*      */   public synchronized void addNewsFilter(INewsFilter newsFilter)
/*      */   {
/*  997 */     if ((this.transportClient != null) && (this.transportClient.isOnline())) {
/*  998 */       this.newsFilters.put(newsFilter.getNewsSource(), newsFilter);
/*      */ 
/* 1000 */       NewsSubscribeRequest newsSubscribeRequest = new NewsSubscribeRequest();
/*      */ 
/* 1002 */       newsSubscribeRequest.setRequestType(NewsRequestType.SUBSCRIBE);
/* 1003 */       newsSubscribeRequest.setNewsSource(NewsSource.valueOf(newsFilter.getNewsSource().name()));
/* 1004 */       newsSubscribeRequest.setHot(newsFilter.isOnlyHot());
/* 1005 */       newsSubscribeRequest.setGeoRegions(EnumConverter.convert(newsFilter.getCountries(), GeoRegion.class));
/* 1006 */       newsSubscribeRequest.setMarketSectors(EnumConverter.convert(newsFilter.getMarketSectors(), MarketSector.class));
/* 1007 */       newsSubscribeRequest.setIndicies(EnumConverter.convert(newsFilter.getStockIndicies(), StockIndex.class));
/* 1008 */       newsSubscribeRequest.setCurrencies(EnumConverter.convert(newsFilter.getCurrencies(), com.dukascopy.transport.common.msg.news.Currency.class));
/* 1009 */       newsSubscribeRequest.setEventCategories(EnumConverter.convert(newsFilter.getEventCategories(), EventCategory.class));
/* 1010 */       newsSubscribeRequest.setKeywords(newsFilter.getKeywords());
/* 1011 */       newsSubscribeRequest.setFromDate(newsFilter.getFrom());
/* 1012 */       newsSubscribeRequest.setToDate(newsFilter.getTo());
/* 1013 */       newsSubscribeRequest.setCalendarType((CalendarEvent.CalendarType)EnumConverter.convert(newsFilter.getType(), CalendarEvent.CalendarType.class));
/*      */ 
/* 1015 */       LOGGER.debug(new StringBuilder().append("Subscribing : ").append(newsSubscribeRequest).toString());
/*      */ 
/* 1017 */       this.transportClient.controlRequest(newsSubscribeRequest);
/*      */     }
/*      */   }
/*      */ 
/*      */   public synchronized void setOut(PrintStream out)
/*      */   {
/* 1023 */     this.out = out;
/* 1024 */     NotificationUtilsProvider.setNotificationUtils(new PrintStreamNotificationUtils(out, this.err));
/*      */   }
/*      */ 
/*      */   public synchronized void setErr(PrintStream err)
/*      */   {
/* 1029 */     this.err = err;
/* 1030 */     NotificationUtilsProvider.setNotificationUtils(new PrintStreamNotificationUtils(this.out, err));
/*      */   }
/*      */ 
/*      */   public void setCacheDirectory(File cacheDirectory)
/*      */   {
/* 1035 */     if (!(cacheDirectory.exists())) {
/* 1036 */       LOGGER.warn(new StringBuilder().append("Cache directory [").append(cacheDirectory).append("] doesn't exist, trying to create").toString());
/* 1037 */       if (!(cacheDirectory.mkdirs())) {
/* 1038 */         LOGGER.error(new StringBuilder().append("Cannot create cache directory [").append(cacheDirectory).append("], default cache directory will be used").toString());
/* 1039 */         return;
/*      */       }
/*      */     }
/* 1042 */     FilePathManager.getInstance().setCacheFolderPath(cacheDirectory.getAbsolutePath());
/*      */   }
/*      */ 
/*      */   private void fakeTickOnWeekends(Instrument instrument)
/*      */   {
/* 1049 */     Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
/* 1050 */     calendar.setFirstDayOfWeek(2);
/* 1051 */     calendar.set(7, 6);
/* 1052 */     calendar.set(11, 21);
/* 1053 */     calendar.set(12, 0);
/* 1054 */     calendar.set(13, 0);
/* 1055 */     calendar.set(14, 0);
/* 1056 */     long weekendStart = calendar.getTimeInMillis();
/* 1057 */     calendar.set(7, 1);
/* 1058 */     calendar.set(11, 22);
/* 1059 */     long weekendEnd = calendar.getTimeInMillis();
/* 1060 */     long currentTime = System.currentTimeMillis();
/* 1061 */     if ((currentTime <= weekendStart) || (currentTime >= weekendEnd))
/*      */       return;
/* 1063 */     Thread fakeTickThread = new Thread("FirstTickOnWeekends", instrument, weekendStart)
/*      */     {
/*      */       public void run() {
/*      */         try {
/* 1067 */           Thread.sleep(10000L);
/*      */         } catch (InterruptedException e) {
/* 1069 */           DCClientImpl.LOGGER.error(e.getMessage(), e);
/*      */         }
/* 1071 */         TickData[] lastTick = { FeedDataProvider.getDefaultInstance().getLastTick(this.val$instrument) };
/* 1072 */         boolean[] loadedSuccessfully = new boolean[1];
/* 1073 */         Exception[] loadingException = new Exception[1];
/*      */ 
/* 1076 */         if (lastTick[0] != null)
/*      */           return;
/*      */         try {
/* 1079 */           FeedDataProvider.getDefaultInstance().loadTicksDataSynched(this.val$instrument, this.val$weekendStart - 600000L, this.val$weekendStart + 4500000L, new LiveFeedListener(lastTick)
/*      */           {
/*      */             public void newTick(Instrument instrument, long time, double ask, double bid, double askVol, double bidVol) {
/* 1082 */               this.val$lastTick[0] = new TickData(time, ask, bid, askVol, bidVol);
/*      */             }
/*      */ 
/*      */             public void newCandle(Instrument instrument, Period period, com.dukascopy.api.OfferSide side, long time, double open, double close, double low, double high, double vol)
/*      */             {
/*      */             }
/*      */           }
/*      */           , new LoadingProgressListener(loadedSuccessfully, loadingException)
/*      */           {
/*      */             public void dataLoaded(long start, long end, long currentPosition, String information)
/*      */             {
/*      */             }
/*      */ 
/*      */             public void loadingFinished(boolean allDataLoaded, long start, long end, long currentPosition, Exception e)
/*      */             {
/* 1095 */               this.val$loadedSuccessfully[0] = allDataLoaded;
/* 1096 */               this.val$loadingException[0] = e;
/*      */             }
/*      */ 
/*      */             public boolean stopJob()
/*      */             {
/* 1101 */               return false;
/*      */             } } );
/*      */         }
/*      */         catch (DataCacheException e) {
/* 1105 */           loadedSuccessfully[0] = false;
/* 1106 */           loadingException[0] = e;
/*      */         }
/* 1108 */         if ((loadedSuccessfully[0] != 0) && (lastTick[0] != null))
/*      */         {
/* 1110 */           if (!(FeedDataProvider.getDefaultInstance().isSubscribedToInstrument(this.val$instrument)))
/*      */             return;
/* 1112 */           TickData maybeLastTick = FeedDataProvider.getDefaultInstance().getLastTick(this.val$instrument);
/* 1113 */           if (maybeLastTick == null)
/*      */           {
/* 1115 */             String currencyPrimary = this.val$instrument.getPrimaryCurrency().getCurrencyCode();
/* 1116 */             String currencySecondary = this.val$instrument.getSecondaryCurrency().getCurrencyCode();
/* 1117 */             List bids = new ArrayList(1);
/* 1118 */             List asks = new ArrayList(1);
/* 1119 */             bids.add(new CurrencyOffer(currencyPrimary, currencySecondary, com.dukascopy.transport.common.model.type.OfferSide.BID, new Money(BigDecimal.valueOf(lastTick[0].bidVol), this.val$instrument.getPrimaryCurrency()), new Money(BigDecimal.valueOf(lastTick[0].bid), this.val$instrument.getPrimaryCurrency())));
/*      */ 
/* 1123 */             asks.add(new CurrencyOffer(currencyPrimary, currencySecondary, com.dukascopy.transport.common.model.type.OfferSide.ASK, new Money(BigDecimal.valueOf(lastTick[0].askVol), this.val$instrument.getPrimaryCurrency()), new Money(BigDecimal.valueOf(lastTick[0].ask), this.val$instrument.getPrimaryCurrency())));
/*      */ 
/* 1127 */             CurrencyMarket currencyMarket = new CurrencyMarket(currencyPrimary, currencySecondary, bids, asks);
/* 1128 */             currencyMarket.setCreationTimestamp(Long.valueOf(lastTick[0].getTime()));
/* 1129 */             currencyMarket.setIsBackup(true);
/* 1130 */             DCClientImpl.this.feedbackMessageReceived(null, currencyMarket);
/*      */           }
/*      */         }
/*      */         else {
/* 1134 */           DCClientImpl.LOGGER.error("Error while loading last tick for instrument [" + this.val$instrument + "]");
/* 1135 */           if (loadingException[0] != null)
/* 1136 */             DCClientImpl.LOGGER.error(loadingException[0].getMessage(), loadingException[0]);
/*      */         }
/*      */       }
/*      */     };
/* 1143 */     fakeTickThread.start();
/*      */   }
/*      */ 
/*      */   private void onStrategyBroadcast(JForexTaskManager taskManager, StrategyBroadcastMessage message)
/*      */   {
/* 1148 */     taskManager.onBroadcastMessage(message.getTransactionId(), new StrategyBroadcastMessageImpl(message.getTopic(), message.getMessage(), System.currentTimeMillis()));
/*      */   }
/*      */ 
/*      */   public String getSessionID()
/*      */   {
/* 1175 */     return this.sessionID;
/*      */   }
/*      */ 
/*      */   public JForexTaskManager getTaskManager(long processID) {
/* 1179 */     return ((JForexTaskManager)this.strategyEngines.get(Long.valueOf(processID)));
/*      */   }
/*      */ 
/*      */   private static class DefaultStrategyExceptionHandler
/*      */     implements IStrategyExceptionHandler
/*      */   {
/* 1159 */     private static final Logger LOGGER = LoggerFactory.getLogger(DefaultStrategyExceptionHandler.class);
/*      */     private JForexTaskManager taskManager;
/*      */ 
/*      */     public void setTaskManager(JForexTaskManager taskManager)
/*      */     {
/* 1163 */       this.taskManager = taskManager;
/*      */     }
/*      */ 
/*      */     public void onException(long strategyId, IStrategyExceptionHandler.Source source, Throwable t)
/*      */     {
/* 1168 */       LOGGER.error("Exception thrown while running " + source + " method: " + t.getMessage(), t);
/* 1169 */       this.taskManager.stopStrategy();
/*      */     }
/*      */   }
/*      */ }

/* Location:           J:\javaworksvn\JForexClientLibrary\libs\greed-common-177.jar
 * Qualified Name:     com.dukascopy.api.impl.connect.DCClientImpl
 * Java Class Version: 6 (50.0)
 * JD-Core Version:    0.5.3
 */