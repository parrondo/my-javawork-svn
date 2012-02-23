/*     */ package com.dukascopy.dds2.greed;
/*     */ 
/*     */ import com.dukascopy.api.impl.connect.AuthorizationClient;
/*     */ import com.dukascopy.charts.data.datacache.DataCacheException;
/*     */ import com.dukascopy.charts.data.datacache.FeedDataProvider;
/*     */ import com.dukascopy.dds2.greed.gui.ClientForm;
/*     */ import com.dukascopy.dds2.greed.gui.component.ApplicationClock;
/*     */ import com.dukascopy.dds2.greed.gui.settings.PreferencesStorage;
/*     */ import com.dukascopy.dds2.greed.gui.settings.autosaving.IClientSettingsStorageAutoSaving;
/*     */ import com.dukascopy.dds2.greed.model.MarketView;
/*     */ import com.dukascopy.dds2.greed.util.LoginPassEncoder;
/*     */ import com.dukascopy.dds2.greed.util.PlatformInitUtils.UserType;
/*     */ import com.dukascopy.dds2.greed.util.PlatformSpecific;
/*     */ import com.dukascopy.dds2.greed.util.event.ApplicationEvent;
/*     */ import com.dukascopy.dds2.greed.util.event.ApplicationEventMulticaster;
/*     */ import com.dukascopy.dds2.greed.util.event.SimpleApplicationEventMulticaster;
/*     */ import java.awt.Color;
/*     */ import java.awt.Dimension;
/*     */ import java.awt.SystemColor;
/*     */ import java.awt.Toolkit;
/*     */ import java.text.SimpleDateFormat;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import java.util.Calendar;
/*     */ import java.util.Collections;
/*     */ import java.util.Date;
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Properties;
/*     */ import java.util.Set;
/*     */ import java.util.TimeZone;
/*     */ import java.util.concurrent.ConcurrentHashMap;
/*     */ import org.slf4j.Logger;
/*     */ import org.slf4j.LoggerFactory;
/*     */ 
/*     */ public class GreedContext
/*     */   implements PlatformSpecific
/*     */ {
/*  33 */   private static final Logger LOGGER = LoggerFactory.getLogger(GreedContext.class);
/*     */ 
/*  35 */   private static final SimpleDateFormat LOGGER_DATE_FORMAT = new SimpleDateFormat() {  } ;
/*     */   public static final String POSITIONS_TOOL_BAR_MODEL = "positionsToolBarModel";
/*     */   public static final String CURRENCY_CONVERTER = "currencyConverter";
/*     */   public static final String MARKET_VIEW = "marketView";
/*     */   public static final String SETTINGS_STORAGE = "settingsStorage";
/*     */   public static final String SETTINGS_STORAGE_AUTOSAVING = "settingsStorageAutosaving";
/*     */   public static final String CHART_TEMPLATE_SETTINGS_STORAGE = "chartTemplateSettingsStorage";
/*     */   public static final String ACCOUNT_STATEMENT = "accountStatement";
/*     */   public static final String CALENDARS = "calendars";
/*     */   public static final String INSTRUMENTS = "instruments";
/*     */   public static final String PROPERTIES = "properties";
/*     */   public static final String APPLICATION_CLOCK = "applicationClock";
/*     */   public static final String CLIENT_LISTENER = "clientListener";
/*     */   public static final String TRANSPORT_CLIENT = "transportClient";
/*     */   public static final String EMERGENCY_LOGGER = "Logger";
/*     */   public static final String WINDOW_MANAGER = "windowManager";
/*     */   public static final String NEWS_ADAPTER = "newsAdapter";
/*     */   public static final String DOCK = "Dock";
/*     */   public static final String SETTINGS = "Settings";
/*     */   public static final String LOGIN_FORM = "loginForm";
/*     */   public static final String CLIENT_GUI = "clientGui";
/*     */   public static final String DDS_AGENT = "ddsAgent";
/*     */   public static final String STRATEGIES = "strategies";
/*     */   public static final String LAYOUT_MANAGER = "layoutManager";
/*     */   public static final String FIRST_ACCOUNT_INFO = "firstAccountInfo";
/*     */   public static final String FEED_COMMISSION_HISTORY = "feed.commission.history";
/*     */   public static final String SUPPORTED_INSTRUMENTS = "instruments";
/*     */   public static final String CALENDARS_BASE_URL = "base.calendar.url";
/*     */   public static final String NEWS_SOURCE = "news.source";
/*     */   public static final String NEWS_URL = "news.url";
/*     */   public static final String NEWS_DETAILS_URL = "news.details.url";
/*     */   public static final String PIN_HELP = "pin.help";
/*     */   public static final String BASE_URL = "base.url";
/*     */   public static final String SERVICES1_URL = "services1.url";
/*     */   public static final String TRADELOG_SFX = "tradelog_sfx.url";
/*     */   public static final String BASE_REPORTS_URL = "wlabel.foUrl";
/*     */   public static final String REPORTS_URL = "reports.url";
/*     */   public static final String HISTORY_SERVER_URL = "history.server.url";
/*     */   public static final String JSS_LOGSERVER_URL = "jss.logserver.url";
/*     */   public static final String CONTEST_CHART_URL = "contest.chart.url";
/*     */   public static final String PLATFORM_FAQ_URL = "platformFaq.url";
/*     */   public static final String PLATFORM_MANUAL_URL = "platformManual.url";
/*     */   public static final String BROKER_CHAT_URL = "brokerChat.url";
/*     */   public static final String NEWS_HOST_URL = "news.host.url";
/*     */   public static final String GET_HELP_URL = "get.help.url";
/*     */   public static final String ENCRYPTION_KEY = "encryptionKey";
/*     */   public static final String FEED_DATA_PROVIDER = "feedDataProvider";
/*     */   public static final String CHARTS_CONTROLLER = "chartsController";
/*     */   public static final String IUSER_INTERFACE = "iUserInterface";
/*     */   public static final String ORDERS_DATA_PROVIDER = "ordersDataProvider";
/*     */   public static final String PLATFORM_CONSOLE = "platformConsole";
/*     */   public static final String USER_PROPERTIES_PREFIX = "user.property.";
/*     */   public static final String ACCOUNT_NAME = "account_name";
/*     */   public static final String PASSWORD = " ";
/*     */   public static final String TICKET = "TICKET";
/*     */   public static final String SESSION_ID = "SESSION_ID";
/*     */   public static final String LOCAL_IP_ADDRESS = "local_ip_address";
/*     */   public static final String EXTERNAL_IP_ADDRESS = "external_ip";
/*     */   public static final String PIN_ENTERED = "PIN_ENTERED";
/*     */   public static final String LOGGING_ENABLED = "logging";
/*     */   public static final String USER_TYPES = "userTypes";
/*     */   public static final String LAYOUT_TYPES = "additionalUserTypes";
/*     */   public static final String SKYPE_COMMAND = "skypeCommand";
/*     */   public static final String SKYPE_URL_PROPERTY = "skype";
/*     */   public static final String TIMESTAMP = "timestamp";
/*     */   public static final String TIME_IN = "timein";
/*     */   public static final String CONTROL = "control";
/*     */   public static final String CHARTS_FRAME = "charts.frame";
/*     */   public static final String PREFERENCES_FRAME = "preferences.frame";
/*     */   public static final String SIGNAL_SERVER_ID = "sspId";
/*     */   public static final String LOG_FILE_KEY = "log.in.file";
/*     */   public static final String LOG_OFF = "logoff";
/* 127 */   private static final Map<String, Object> configurationData = Collections.synchronizedMap(new HashMap());
/*     */ 
/* 129 */   public static Color GLOBAL_BACKGROUND = SystemColor.window;
/* 130 */   public static Color SELECTION_COLOR = SystemColor.textHighlight;
/* 131 */   public static Color SELECTION_FG_COLOR = SystemColor.textHighlightText;
/*     */   public static final String DEVELOPMENT_MODE = "development.mode";
/*     */   public static final String CLIENT_USERNAME = "jnlp.client.username";
/*     */   public static final String CLIENT_PASSWORD = "jnlp.client.password";
/*     */   public static final String CLIENT_TICKET = "jnlp.auth.ticket";
/*     */   public static final String CLIENT_API_URL = "jnlp.api.url";
/*     */   public static final String CLIENT_SESSION_ID = "jnlp.api.sid";
/*     */   public static final String STATEGY_PATH = "jnlp.strategy.path";
/*     */   public static final String RUN_STATEGY_ON_START = "jnlp.run.strategy.on.start";
/*     */   public static final String FINE_LOGGING = "jnlp.fine.logging";
/*     */   public static final String PLATFORM_MODE = "jnlp.platform.mode";
/*     */   public static final String JNLP_HREF = "jnlp.white.label.href";
/*     */   public static boolean IS_JCLIENT_INVOKED;
/*     */   public static final String KAKAKU_LABEL = "jnlp.kakaku";
/* 152 */   public static boolean IS_KAKAKU_LABEL = (System.getProperty("jnlp.kakaku") != null) && (System.getProperty("jnlp.kakaku").equals("true"));
/*     */ 
/* 155 */   public static boolean IS_FXDD_LABEL = false;
/* 156 */   public static boolean IS_ALPARI_LABEL = false;
/* 157 */   public static boolean IS_EU_LIVE = false;
/*     */   public static final String JFOREX_MODE = "jforex";
/*     */   public static final String JCLIENT_MODE = "jclient";
/*     */   public static boolean IS_DEVELOPMENT_MODE;
/*     */   public static String CURRENT_CLIENT_JNLP_URL;
/*     */   public static final String ACCOUNT_INFO_MESSAGE = "AccountInfoMessage";
/*     */   private static Map<String, Object> singletonMap;
/*     */   private static Properties greedProperties;
/*     */   public static final String SETTINGS_FOLDER = "jnlp.client.settings";
/*     */   public static final String CLIENT_VERSION;
/*     */   public static final List<String> LOGIN_URLS;
/*     */   public static final AuthorizationClient AUTHORIZATION_CLIENT;
/*     */   public static String CLIENT_MODE;
/*     */   public static final String ORDER_GROUP_ID_PREFIX = "ordgIdPref";
/*     */   public static String WLABEL_LONG_NAME_KEY;
/*     */   public static String WLABEL_SHORT_NAME_KEY;
/*     */   public static String WLABEL_IMAGES_KEY;
/*     */   public static String WLABEL_PHONE_KEY;
/*     */   public static String WLABEL_SKYPE_KEY;
/*     */   public static String WLABEL_URL_KEY;
/*     */   public static boolean isDukascopyPlatform;
/*     */ 
/* 230 */   public static boolean isLogOff() { if (getConfig("logoff") != null) {
/* 231 */       return ((Boolean)getConfig("logoff")).booleanValue();
/*     */     }
/* 233 */     return false;
/*     */   }
/*     */ 
/*     */   public static String getAccountName()
/*     */   {
/* 242 */     return (String)getConfig("account_name");
/*     */   }
/*     */ 
/*     */   public static void setConfig(String name, Object value)
/*     */   {
/* 251 */     synchronized (configurationData) {
/* 252 */       configurationData.put(name, value);
/*     */     }
/*     */   }
/*     */ 
/*     */   public static Object getConfig(String name)
/*     */   {
/* 262 */     synchronized (configurationData) {
/* 263 */       return configurationData.get(name);
/*     */     }
/*     */   }
/*     */ 
/*     */   public static Map<String, Object> getConfiguration() {
/* 268 */     return Collections.unmodifiableMap(configurationData);
/*     */   }
/*     */ 
/*     */   public static void setUserProperties(Properties userProperties) {
/* 272 */     for (String userPropertyName : userProperties.stringPropertyNames())
/*     */       try {
/* 274 */         String userPropertyValue = userProperties.getProperty(userPropertyName);
/* 275 */         LOGGER.debug("User property [{}] changed : [{}]", userPropertyName, userPropertyValue);
/* 276 */         setUserProperty(userPropertyName, userPropertyValue);
/*     */       } catch (Exception ex) {
/* 278 */         LOGGER.error(new StringBuilder().append("Error while updating user property : ").append(userPropertyName).toString(), ex);
/*     */       }
/*     */   }
/*     */ 
/*     */   public static String getOrderGroupIdForView(String groupId)
/*     */   {
/* 284 */     if (groupId == null) return "";
/* 285 */     String orderGroupPrefix = getUserProperty("ordgIdPref");
/* 286 */     return new StringBuilder().append(null != orderGroupPrefix ? orderGroupPrefix : "").append(groupId).toString();
/*     */   }
/*     */ 
/*     */   public static void setUserProperty(String propertyName, String propertyValue)
/*     */   {
/* 293 */     configurationData.put(new StringBuilder().append("user.property.").append(propertyName).toString(), propertyValue);
/*     */   }
/*     */ 
/*     */   public static String getUserProperty(String propertyName) {
/* 297 */     return (String)configurationData.get(new StringBuilder().append("user.property.").append(propertyName).toString());
/*     */   }
/*     */ 
/*     */   public static String encodeAuth() {
/* 301 */     String login = (String)getConfig("account_name");
/* 302 */     String password = (String)getConfig(" ");
/* 303 */     if ((login != null) && (password != null)) {
/* 304 */       return LoginPassEncoder.encode(login, password);
/*     */     }
/* 306 */     return null;
/*     */   }
/*     */ 
/*     */   public static boolean isActivityLoggingEnabled()
/*     */   {
/* 311 */     return true;
/*     */   }
/*     */ 
/*     */   public static boolean isSmspcEnabled() {
/* 315 */     return true;
/*     */   }
/*     */ 
/*     */   public static boolean isFineLogging() {
/* 319 */     return System.getProperty("jnlp.fine.logging", "false").equals("true");
/*     */   }
/*     */ 
/*     */   public static void setLoggingEnabled(boolean isEnabled) {
/* 323 */     setConfig("logging", Boolean.valueOf(isEnabled));
/*     */   }
/*     */ 
/*     */   public static void setContest(boolean isContest) {
/* 327 */     setConfig(PlatformInitUtils.UserType.ANALITIC_CONTEST.toString(), Boolean.valueOf(isContest));
/*     */   }
/*     */ 
/*     */   public static void setGlobal(boolean isEnabled) {
/* 331 */     setConfig(PlatformInitUtils.UserType.GLOBAL.toString(), Boolean.valueOf(isEnabled));
/*     */   }
/*     */ 
/*     */   public static void setGlobalExtended(boolean isEnabled) {
/* 335 */     setConfig(PlatformInitUtils.UserType.GLOBAL_EXTENDED.toString(), Boolean.valueOf(isEnabled));
/*     */   }
/*     */ 
/*     */   public static void setPinEntered(boolean isPinEntered) {
/* 339 */     setConfig("PIN_ENTERED", Boolean.valueOf(isPinEntered));
/*     */   }
/*     */ 
/*     */   public static void setMiniFx(boolean isMiniFx) {
/* 343 */     setConfig(PlatformInitUtils.UserType.MINI_FX.toString(), Boolean.valueOf(isMiniFx));
/*     */   }
/*     */ 
/*     */   public static void setReadOnly(boolean viewMode) {
/* 347 */     setConfig(PlatformInitUtils.UserType.READ_ONLY.toString(), Boolean.valueOf(viewMode));
/*     */   }
/*     */ 
/*     */   public static boolean isReadOnly() {
/* 351 */     if (null != getConfig(PlatformInitUtils.UserType.READ_ONLY.toString())) {
/* 352 */       return ((Boolean)getConfig(PlatformInitUtils.UserType.READ_ONLY.toString())).booleanValue();
/*     */     }
/* 354 */     return false;
/*     */   }
/*     */ 
/*     */   public static void setHideReports(boolean hideReports)
/*     */   {
/* 359 */     setConfig(PlatformInitUtils.UserType.HIDE_REPORTS.toString(), Boolean.valueOf(hideReports));
/*     */   }
/*     */ 
/*     */   public static boolean isHideReports() {
/* 363 */     if (null != getConfig(PlatformInitUtils.UserType.HIDE_REPORTS.toString())) {
/* 364 */       return ((Boolean)getConfig(PlatformInitUtils.UserType.HIDE_REPORTS.toString())).booleanValue();
/*     */     }
/* 366 */     return false;
/*     */   }
/*     */ 
/*     */   public static void setManageStopLimits(boolean manageStopLimits)
/*     */   {
/* 371 */     setConfig(PlatformInitUtils.UserType.MANAGE_STOP_LIMIT.toString(), Boolean.valueOf(manageStopLimits));
/*     */   }
/*     */ 
/*     */   public static boolean isManageStopLimits() {
/* 375 */     if (null != getConfig(PlatformInitUtils.UserType.MANAGE_STOP_LIMIT.toString())) {
/* 376 */       return ((Boolean)getConfig(PlatformInitUtils.UserType.MANAGE_STOP_LIMIT.toString())).booleanValue();
/*     */     }
/* 378 */     return false;
/*     */   }
/*     */ 
/*     */   public static boolean isGlobal()
/*     */   {
/* 383 */     if (null != getConfig(PlatformInitUtils.UserType.GLOBAL.toString())) {
/* 384 */       return ((Boolean)getConfig(PlatformInitUtils.UserType.GLOBAL.toString())).booleanValue();
/*     */     }
/* 386 */     return false;
/*     */   }
/*     */ 
/*     */   public static boolean isGlobalExtended()
/*     */   {
/* 391 */     if (null != getConfig(PlatformInitUtils.UserType.GLOBAL_EXTENDED.toString())) {
/* 392 */       return ((Boolean)getConfig(PlatformInitUtils.UserType.GLOBAL_EXTENDED.toString())).booleanValue();
/*     */     }
/* 394 */     return false;
/*     */   }
/*     */ 
/*     */   public static boolean isContest()
/*     */   {
/* 399 */     if (null != getConfig(PlatformInitUtils.UserType.ANALITIC_CONTEST.toString())) {
/* 400 */       return ((Boolean)getConfig(PlatformInitUtils.UserType.ANALITIC_CONTEST.toString())).booleanValue();
/*     */     }
/* 402 */     return false;
/*     */   }
/*     */ 
/*     */   public static String getContestImageSendingURL()
/*     */   {
/* 407 */     if (null != getProperty("contest.chart.url")) {
/* 408 */       return (String)getProperty("contest.chart.url");
/*     */     }
/*     */ 
/* 411 */     return null;
/*     */   }
/*     */ 
/*     */   public static boolean isFirstAccountInfo()
/*     */   {
/* 416 */     if (null != getConfig("firstAccountInfo")) {
/* 417 */       return ((Boolean)getConfig("firstAccountInfo")).booleanValue();
/*     */     }
/* 419 */     return true;
/*     */   }
/*     */ 
/*     */   public static void setFirstAccountInfo(boolean isFirst)
/*     */   {
/* 424 */     setConfig("firstAccountInfo", Boolean.valueOf(isFirst));
/*     */   }
/*     */ 
/*     */   public static boolean isPinEntered() {
/* 428 */     if (null != getConfig("PIN_ENTERED")) {
/* 429 */       return ((Boolean)getConfig("PIN_ENTERED")).booleanValue();
/*     */     }
/* 431 */     return false;
/*     */   }
/*     */ 
/*     */   public static boolean isMiniFxAccount()
/*     */   {
/* 436 */     if (null != getConfig(PlatformInitUtils.UserType.MINI_FX.toString())) {
/* 437 */       return ((Boolean)getConfig(PlatformInitUtils.UserType.MINI_FX.toString())).booleanValue();
/*     */     }
/* 439 */     return false;
/*     */   }
/*     */ 
/*     */   public static Date getPlatformTime()
/*     */   {
/* 444 */     long currentPlatformTime = ((ApplicationClock)get("applicationClock")).getTime();
/* 445 */     Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
/* 446 */     cal.setTimeInMillis(currentPlatformTime);
/* 447 */     return cal.getTime();
/*     */   }
/*     */ 
/*     */   public static String getPlatformTimeForLogger() {
/* 451 */     return LOGGER_DATE_FORMAT.format(getPlatformTime());
/*     */   }
/*     */ 
/*     */   public static String getStringProperty(String key) {
/* 455 */     Properties properties = (Properties)get("properties");
/* 456 */     if (properties == null) {
/* 457 */       return null;
/*     */     }
/* 459 */     return properties.getProperty(key);
/*     */   }
/*     */ 
/*     */   public static boolean isLive() {
/* 463 */     return (CLIENT_MODE != null) && ("LIVE".equalsIgnoreCase(CLIENT_MODE));
/*     */   }
/*     */ 
/*     */   public static boolean isDemo() {
/* 467 */     return (CLIENT_MODE != null) && ("DEMO".equalsIgnoreCase(CLIENT_MODE));
/*     */   }
/*     */ 
/*     */   public static boolean isPreDemo() {
/* 471 */     return (CLIENT_MODE != null) && ("PRE-DEMO".equalsIgnoreCase(CLIENT_MODE));
/*     */   }
/*     */ 
/*     */   public static boolean isTest() {
/* 475 */     return (CLIENT_MODE != null) && ("TEST".equalsIgnoreCase(CLIENT_MODE));
/*     */   }
/*     */ 
/*     */   public static boolean isSignalServerInUse()
/*     */   {
/* 480 */     return getSignalServerId() != null;
/*     */   }
/*     */ 
/*     */   public static boolean getBooleanProperty(String key)
/*     */   {
/* 485 */     String stringValue = getStringProperty(key);
/* 486 */     return (stringValue != null) && (stringValue.toLowerCase().contains("true"));
/*     */   }
/*     */ 
/*     */   public static void publishEvent(ApplicationEvent event) {
/* 490 */     SimpleApplicationEventMulticaster.getInstance().multicastEvent(event);
/*     */   }
/*     */   public static void putInSingleton(String key, Object value) {
/* 493 */     singletonMap.put(key, value);
/*     */   }
/*     */   public static boolean isStrategyAllowed() {
/* 496 */     return !IS_JCLIENT_INVOKED;
/*     */   }
/*     */ 
/*     */   public static Object get(String beanName)
/*     */   {
/* 502 */     return singletonMap.get(beanName);
/*     */   }
/*     */ 
/*     */   public static Object getProperty(String key) {
/* 506 */     return greedProperties.get(key);
/*     */   }
/*     */ 
/*     */   public static Long getSignalServerId() {
/* 510 */     return (Long)getProperty("sspId");
/*     */   }
/*     */ 
/*     */   public static void resetDataFeedProvider() {
/* 514 */     singletonMap.remove("feedDataProvider");
/* 515 */     String cacheName = CLIENT_MODE != null ? CLIENT_MODE : "COMMON";
/*     */     try
/*     */     {
/* 518 */       List feedCommissionHistory = getFeedCommissionHistory();
/* 519 */       Set supportedInstruments = getSupportedInstrument();
/* 520 */       FeedDataProvider.createFeedDataProvider(cacheName, feedCommissionHistory, supportedInstruments);
/*     */     }
/*     */     catch (DataCacheException e) {
/* 523 */       LOGGER.error(e.getMessage(), e);
/* 524 */       System.exit(1);
/*     */     }
/*     */ 
/* 527 */     singletonMap.put("feedDataProvider", FeedDataProvider.getDefaultInstance());
/*     */   }
/*     */ 
/*     */   public static void setGreedProperties(Properties greedProperties) {
/* 531 */     greedProperties = greedProperties;
/*     */   }
/*     */ 
/*     */   public static void setSingletonMap(Map<String, Object> singletonMap) {
/* 535 */     beforeDestroySingletonMap();
/* 536 */     singletonMap = singletonMap;
/*     */   }
/*     */ 
/*     */   private static void beforeDestroySingletonMap() {
/* 540 */     IClientSettingsStorageAutoSaving clientSettingsStorageAutoSaving = (IClientSettingsStorageAutoSaving)get("settingsStorageAutosaving");
/* 541 */     if (clientSettingsStorageAutoSaving != null)
/* 542 */       clientSettingsStorageAutoSaving.stopAutoSaving();
/*     */   }
/*     */ 
/*     */   public static void putAll(Map<String, Object> singletonMap)
/*     */   {
/* 547 */     singletonMap.putAll(singletonMap);
/*     */   }
/*     */ 
/*     */   public static Properties getGreedProperties() {
/* 551 */     return greedProperties;
/*     */   }
/*     */ 
/*     */   public static MarketView getMarketView() {
/* 555 */     return (MarketView)get("marketView");
/*     */   }
/*     */ 
/*     */   public static List<String[]> getFeedCommissionHistory() {
/* 559 */     Properties props = getGreedProperties();
/* 560 */     if (props == null) {
/* 561 */       return null;
/*     */     }
/* 563 */     List list = (List)props.get("feed.commission.history");
/* 564 */     return list;
/*     */   }
/*     */ 
/*     */   public static Set<String> getSupportedInstrument() {
/* 568 */     Properties props = getGreedProperties();
/* 569 */     if (props == null) {
/* 570 */       return null;
/*     */     }
/* 572 */     Set list = (Set)props.get("instruments");
/* 573 */     return list;
/*     */   }
/*     */ 
/*     */   public static boolean isPlatformFrameSmall() {
/* 577 */     return Toolkit.getDefaultToolkit().getScreenSize().height < 850;
/*     */   }
/*     */ 
/*     */   static
/*     */   {
/* 160 */     if (IS_KAKAKU_LABEL) {
/* 161 */       PreferencesStorage.setJForexMode(true);
/*     */     }
/*     */ 
/* 169 */     IS_DEVELOPMENT_MODE = (System.getProperty("development.mode") != null) && (System.getProperty("development.mode").equals("true"));
/*     */ 
/* 172 */     CURRENT_CLIENT_JNLP_URL = null;
/*     */ 
/* 176 */     singletonMap = new ConcurrentHashMap();
/*     */ 
/* 185 */     String version = ClientForm.class.getPackage().getImplementationVersion();
/* 186 */     if ((version == null) || (version.equals("")) || (version.endsWith("SNAPSHOT")))
/* 187 */       CLIENT_VERSION = "99.99.99";
/*     */     else {
/* 189 */       CLIENT_VERSION = version;
/*     */     }
/*     */ 
/* 196 */     String commaSeparatedList = System.getProperty("jnlp.login.url");
/*     */ 
/* 198 */     if (commaSeparatedList != null) {
/* 199 */       commaSeparatedList = commaSeparatedList.trim();
/* 200 */       String[] urlList = commaSeparatedList.split(",");
/* 201 */       LOGIN_URLS = Arrays.asList(urlList);
/*     */     } else {
/* 203 */       LOGIN_URLS = new ArrayList();
/*     */     }
/*     */ 
/* 207 */     AUTHORIZATION_CLIENT = AuthorizationClient.getInstance(LOGIN_URLS, CLIENT_VERSION);
/*     */ 
/* 209 */     CLIENT_MODE = System.getProperty("jnlp.client.mode");
/*     */ 
/* 212 */     if (!WINDOWS) {
/* 213 */       GLOBAL_BACKGROUND = Color.WHITE;
/*     */     }
/*     */ 
/* 219 */     WLABEL_LONG_NAME_KEY = "wlabel.longLabel";
/* 220 */     WLABEL_SHORT_NAME_KEY = "wlabel.shortLabel";
/* 221 */     WLABEL_IMAGES_KEY = "wlabel.logo";
/* 222 */     WLABEL_PHONE_KEY = "wlabel.phone";
/* 223 */     WLABEL_SKYPE_KEY = "wlabel.skype";
/* 224 */     WLABEL_URL_KEY = "wlabel.url";
/*     */ 
/* 227 */     isDukascopyPlatform = true;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.GreedContext
 * JD-Core Version:    0.6.0
 */