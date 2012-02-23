/*      */ package com.dukascopy.dds2.greed.util;
/*      */ 
/*      */ import com.dukascopy.api.Instrument;
/*      */ import com.dukascopy.api.impl.History;
/*      */ import com.dukascopy.charts.chartbuilder.ChartBuilder;
/*      */ import com.dukascopy.charts.data.AbstractDataSequenceProvider;
/*      */ import com.dukascopy.charts.data.CandlesDataSequenceProvider;
/*      */ import com.dukascopy.charts.data.TicksDataSequenceProvider;
/*      */ import com.dukascopy.charts.data.datacache.CacheManager;
/*      */ import com.dukascopy.charts.data.datacache.DataCacheException;
/*      */ import com.dukascopy.charts.data.datacache.FeedDataProvider;
/*      */ import com.dukascopy.charts.data.datacache.InstrumentSubscriptionListener;
/*      */ import com.dukascopy.charts.data.datacache.LocalCacheManager;
/*      */ import com.dukascopy.charts.data.datacache.listener.DataFeedServerConnectionListener;
/*      */ import com.dukascopy.charts.data.orders.OrdersProvider;
/*      */ import com.dukascopy.charts.main.DDSChartsControllerImpl;
/*      */ import com.dukascopy.charts.main.interfaces.DDSChartsController;
/*      */ import com.dukascopy.charts.main.interfaces.IChartController;
/*      */ import com.dukascopy.charts.math.dataprovider.AbstractDataProvider;
/*      */ import com.dukascopy.charts.math.dataprovider.CandlesDataProvider;
/*      */ import com.dukascopy.charts.math.dataprovider.OrdersDataProvider;
/*      */ import com.dukascopy.charts.math.indicators.IndicatorsProvider;
/*      */ import com.dukascopy.charts.persistence.IChartClient;
/*      */ import com.dukascopy.charts.persistence.IdManager;
/*      */ import com.dukascopy.charts.persistence.SettingsStorage;
/*      */ import com.dukascopy.charts.view.drawingstrategies.AbstractDrawingStrategyFactory;
/*      */ import com.dukascopy.dds2.greed.GreedContext;
/*      */ import com.dukascopy.dds2.greed.actions.AutoConnectAction;
/*      */ import com.dukascopy.dds2.greed.actions.FakeTickOnWeekendsAction;
/*      */ import com.dukascopy.dds2.greed.agent.Strategies;
/*      */ import com.dukascopy.dds2.greed.agent.protocol.JFXStreamHandlerFactory;
/*      */ import com.dukascopy.dds2.greed.agent.strategy.ide.EditorFactory;
/*      */ import com.dukascopy.dds2.greed.connection.GreedClientListener;
/*      */ import com.dukascopy.dds2.greed.connection.GreedTransportClient;
/*      */ import com.dukascopy.dds2.greed.console.PlatformConsoleImpl;
/*      */ import com.dukascopy.dds2.greed.gui.ClientForm;
/*      */ import com.dukascopy.dds2.greed.gui.ClientFormLayoutManager;
/*      */ import com.dukascopy.dds2.greed.gui.GuiUtilsAndConstants;
/*      */ import com.dukascopy.dds2.greed.gui.InstrumentAvailabilityManager;
/*      */ import com.dukascopy.dds2.greed.gui.JForexClientFormLayoutManager;
/*      */ import com.dukascopy.dds2.greed.gui.component.ApplicationClock;
/*      */ import com.dukascopy.dds2.greed.gui.component.JRoundedButtonUI;
/*      */ import com.dukascopy.dds2.greed.gui.component.chart.ChartsFrame;
/*      */ import com.dukascopy.dds2.greed.gui.component.dialog.disclaimers.FullAccessDisclaimDialog;
/*      */ import com.dukascopy.dds2.greed.gui.component.export.historicaldata.HistoricalDataManagerPanel;
/*      */ import com.dukascopy.dds2.greed.gui.component.moverview.MarketOverviewFrame;
/*      */ import com.dukascopy.dds2.greed.gui.component.news.NewsAdapter;
/*      */ import com.dukascopy.dds2.greed.gui.component.orders.OrderTableModel;
/*      */ import com.dukascopy.dds2.greed.gui.component.orders.OrdersPanel;
/*      */ import com.dukascopy.dds2.greed.gui.component.positions.PositionsTableModel;
/*      */ import com.dukascopy.dds2.greed.gui.component.strategy.StrategyTestPanel;
/*      */ import com.dukascopy.dds2.greed.gui.helpers.IWorkspaceHelper;
/*      */ import com.dukascopy.dds2.greed.gui.l10n.LocalizationManager;
/*      */ import com.dukascopy.dds2.greed.gui.settings.ChartTemplateSettingsStorage;
/*      */ import com.dukascopy.dds2.greed.gui.settings.ClientSettingsStorage;
/*      */ import com.dukascopy.dds2.greed.gui.settings.ClientSettingsStorageImpl;
/*      */ import com.dukascopy.dds2.greed.gui.settings.IChartTemplateSettingsStorage;
/*      */ import com.dukascopy.dds2.greed.gui.settings.PreferencesStorage;
/*      */ import com.dukascopy.dds2.greed.gui.settings.autosaving.ClientSettingsStorageAutoSaving;
/*      */ import com.dukascopy.dds2.greed.gui.settings.autosaving.IClientSettingsStorageAutoSaving;
/*      */ import com.dukascopy.dds2.greed.gui.util.lotamount.LotAmountChanger;
/*      */ import com.dukascopy.dds2.greed.gui.window.WindowManager;
/*      */ import com.dukascopy.dds2.greed.model.AccountStatement;
/*      */ import com.dukascopy.dds2.greed.model.MarketView;
/*      */ import com.dukascopy.dds2.greed.mt.AgentManager;
/*      */ import com.dukascopy.dds2.greed.util.event.ApplicationEventMulticaster;
/*      */ import com.dukascopy.dds2.greed.util.event.SimpleApplicationEventMulticaster;
/*      */ import com.dukascopy.dds2.greed.util.event.WindowCountAWTListener;
/*      */ import com.dukascopy.transport.client.ClientListener;
/*      */ import com.dukascopy.transport.client.TransportClient;
/*      */ import com.dukascopy.transport.common.msg.group.OrderGroupMessage;
/*      */ import com.dukascopy.transport.common.msg.group.OrderMessage;
/*      */ import com.dukascopy.transport.common.msg.request.MergePositionsMessage;
/*      */ import com.dukascopy.transport.common.msg.request.QuitRequestMessage;
/*      */ import java.awt.Container;
/*      */ import java.awt.Font;
/*      */ import java.awt.Frame;
/*      */ import java.awt.Toolkit;
/*      */ import java.awt.image.BufferedImage;
/*      */ import java.io.ByteArrayInputStream;
/*      */ import java.io.IOException;
/*      */ import java.math.BigDecimal;
/*      */ import java.math.MathContext;
/*      */ import java.math.RoundingMode;
/*      */ import java.net.InetAddress;
/*      */ import java.net.InetSocketAddress;
/*      */ import java.net.UnknownHostException;
/*      */ import java.text.MessageFormat;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Currency;
/*      */ import java.util.HashMap;
/*      */ import java.util.List;
/*      */ import java.util.Map;
/*      */ import java.util.Map.Entry;
/*      */ import java.util.Set;
/*      */ import java.util.UUID;
/*      */ import java.util.concurrent.ConcurrentHashMap;
/*      */ import java.util.logging.Level;
/*      */ import java.util.regex.Matcher;
/*      */ import java.util.regex.Pattern;
/*      */ import javax.imageio.ImageIO;
/*      */ import javax.swing.ImageIcon;
/*      */ import javax.swing.JFrame;
/*      */ import javax.swing.JOptionPane;
/*      */ import javax.swing.JRootPane;
/*      */ import javax.swing.SwingUtilities;
/*      */ import javax.swing.UIDefaults;
/*      */ import javax.swing.UIDefaults.ProxyLazyValue;
/*      */ import javax.swing.UIManager;
/*      */ import javax.swing.UIManager.LookAndFeelInfo;
/*      */ import org.slf4j.LoggerFactory;
/*      */ 
/*      */ public class PlatformInitUtils
/*      */   implements PlatformSpecific, LookAndFeelSpecific
/*      */ {
/*      */   private static final org.slf4j.Logger LOGGER;
/*      */   private static WindowCountAWTListener windowCountAWTListener;
/*      */ 
/*      */   public static void logOut()
/*      */   {
/*  143 */     doExit(1);
/*      */   }
/*      */ 
/*      */   public static void logSwitch() {
/*  147 */     doExit(2);
/*      */   }
/*      */ 
/*      */   public static void doExit(int mode)
/*      */   {
/*  153 */     GreedTransportClient client = (GreedTransportClient)GreedContext.get("transportClient");
/*      */ 
/*  155 */     IClientSettingsStorageAutoSaving storageAutoSaving = (IClientSettingsStorageAutoSaving)GreedContext.get("settingsStorageAutosaving");
/*  156 */     storageAutoSaving.stopAutoSaving();
/*      */ 
/*  158 */     saveClientSettingsOnExit();
/*      */ 
/*  160 */     if (GreedContext.isStrategyAllowed()) {
/*  161 */       DDSChartsController chartsController = (DDSChartsController)GreedContext.get("chartsController");
/*  162 */       chartsController.dispose();
/*      */     }
/*      */ 
/*  165 */     MarketOverviewFrame dock = (MarketOverviewFrame)GreedContext.get("Dock");
/*  166 */     if (dock.isVisible()) {
/*  167 */       dock.saveBeforeColsing();
/*      */     }
/*      */ 
/*  170 */     if (mode == 1) {
/*      */       try {
/*  172 */         client.controlRequest(new QuitRequestMessage());
/*      */       } catch (Throwable e) {
/*  174 */         LOGGER.error(e.getMessage(), e);
/*      */       }
/*      */     }
/*      */     try
/*      */     {
/*  179 */       Thread.sleep(500L);
/*      */     } catch (InterruptedException e) {
/*      */     }
/*      */     try {
/*  183 */       client.disconnect();
/*      */     } catch (Throwable e) {
/*  185 */       LOGGER.error(e.getMessage(), e);
/*      */     }
/*      */ 
/*  188 */     if (mode == 1) {
/*      */       try {
/*  190 */         client.terminate();
/*      */       } catch (Throwable e) {
/*  192 */         LOGGER.error(e.getMessage(), e);
/*      */       }
/*      */     }
/*      */ 
/*  196 */     if (GreedContext.isActivityLoggingEnabled()) {
/*  197 */       EmergencyLogger logger = (EmergencyLogger)GreedContext.get("Logger");
/*  198 */       logger.sendLog();
/*      */     }
/*      */ 
/*  201 */     if (mode == 1) {
/*  202 */       reloadJNLP();
/*      */     }
/*      */ 
/*  205 */     System.exit(0);
/*      */   }
/*      */ 
/*      */   private static void reloadJNLP() {
/*  209 */     String params = "";
/*  210 */     if (GreedContext.IS_KAKAKU_LABEL) {
/*  211 */       String _J_D = "-J-D";
/*  212 */       params = "-J-Djnlp.kakaku=true";
/*      */     }
/*      */     try {
/*  215 */       Runtime.getRuntime().exec("javaws " + params + " " + GreedContext.CURRENT_CLIENT_JNLP_URL);
/*      */     } catch (IOException e) {
/*  217 */       LOGGER.error(e.getMessage(), e);
/*      */     }
/*      */   }
/*      */ 
/*      */   public static void deleteCacheAndSettingsFilesAndRestartPlatform(boolean deleteCache)
/*      */   {
/*  223 */     ((ClientSettingsStorage)GreedContext.get("settingsStorage")).saveWorkspaceSettings();
/*      */ 
/*  225 */     if (deleteCache) {
/*  226 */       LocalCacheManager.deleteCacheBeforeHalt();
/*      */     }
/*      */ 
/*  229 */     GreedTransportClient client = (GreedTransportClient)GreedContext.get("transportClient");
/*      */     try {
/*  231 */       client.controlRequest(new QuitRequestMessage());
/*      */     } catch (Throwable ex) {
/*  233 */       LOGGER.error(ex.getMessage(), ex);
/*      */     }
/*      */     try
/*      */     {
/*  237 */       Thread.sleep(500L);
/*      */     } catch (InterruptedException ex) {
/*      */     }
/*      */     try {
/*  241 */       client.disconnect();
/*      */     } catch (Throwable ex) {
/*  243 */       LOGGER.error(ex.getMessage(), ex);
/*      */     }
/*      */     try
/*      */     {
/*  247 */       client.terminate();
/*      */     } catch (Throwable ex) {
/*  249 */       LOGGER.error(ex.getMessage(), ex);
/*      */     }
/*      */ 
/*  252 */     if (GreedContext.isActivityLoggingEnabled()) {
/*  253 */       EmergencyLogger logger = (EmergencyLogger)GreedContext.get("Logger");
/*  254 */       logger.sendLog();
/*      */     }
/*      */ 
/*  257 */     openPlatformInstance();
/*      */ 
/*  259 */     Runtime.getRuntime().halt(0);
/*      */   }
/*      */ 
/*      */   public static void reloadOpenedPlatformFromSite() {
/*  263 */     GreedTransportClient client = (GreedTransportClient)GreedContext.get("transportClient");
/*      */ 
/*  265 */     saveClientSettingsOnExit();
/*      */ 
/*  267 */     if (GreedContext.isStrategyAllowed()) {
/*  268 */       DDSChartsController chartsController = (DDSChartsController)GreedContext.get("chartsController");
/*  269 */       chartsController.dispose();
/*      */     }
/*      */ 
/*  272 */     MarketOverviewFrame dock = (MarketOverviewFrame)GreedContext.get("Dock");
/*  273 */     if (dock.isVisible()) {
/*  274 */       dock.saveBeforeColsing();
/*      */     }
/*      */     try
/*      */     {
/*  278 */       client.controlRequest(new QuitRequestMessage());
/*      */     } catch (Throwable e) {
/*  280 */       LOGGER.error(e.getMessage(), e);
/*      */     }
/*      */     try
/*      */     {
/*  284 */       Thread.sleep(500L);
/*      */     } catch (InterruptedException ex) {
/*      */     }
/*      */     try {
/*  288 */       client.disconnect();
/*      */     } catch (Throwable e) {
/*  290 */       LOGGER.error("Error while disconnecting client", e);
/*      */     }
/*      */     try
/*      */     {
/*  294 */       client.terminate();
/*      */     } catch (Throwable e) {
/*  296 */       LOGGER.error("Error while terminating client", e);
/*      */     }
/*      */ 
/*  299 */     if (GreedContext.isActivityLoggingEnabled()) {
/*  300 */       EmergencyLogger logger = (EmergencyLogger)GreedContext.get("Logger");
/*  301 */       logger.sendLog();
/*      */     }
/*      */ 
/*  304 */     openPlatformInstance();
/*      */ 
/*  306 */     System.exit(0);
/*      */   }
/*      */ 
/*      */   private static void openPlatformInstance() {
/*      */     try {
/*  311 */       GreedTransportClient transportClient = (GreedTransportClient)GreedContext.get("transportClient");
/*  312 */       InetSocketAddress inetSocketAddress = transportClient.getTransportClient().getAddress();
/*      */ 
/*  314 */       String login = (String)GreedContext.getConfig("account_name");
/*  315 */       String oldTicket = (String)GreedContext.getConfig("TICKET");
/*  316 */       String instanceId = (String)GreedContext.getConfig("SESSION_ID");
/*  317 */       String password = (String)GreedContext.getConfig(" ");
/*  318 */       String apiUrl = inetSocketAddress.getHostName() + ":" + inetSocketAddress.getPort();
/*      */ 
/*  320 */       String _J_D = "-J-D";
/*  321 */       String params = "-J-Djnlp.client.username=" + login + " " + "-J-D" + "jnlp.api.sid" + "=" + instanceId + " " + "-J-D" + "jnlp.auth.ticket" + "=" + oldTicket + " " + "-J-D" + "jnlp.client.password" + "=" + password + " " + "-J-D" + "jnlp.api.url" + "=" + apiUrl;
/*      */ 
/*  327 */       Runtime.getRuntime().exec("javaws " + params + " " + GreedContext.CURRENT_CLIENT_JNLP_URL);
/*      */     } catch (Exception e) {
/*  329 */       LOGGER.error(e.getMessage(), e);
/*      */     }
/*      */   }
/*      */ 
/*      */   public static String crypt(String pwd) {
/*  334 */     String res = "";
/*      */ 
/*  336 */     String key = ",";
/*  337 */     for (int t = 0; t < pwd.length(); t++) {
/*  338 */       char tmp = ' ';
/*  339 */       for (int i = 0; i < ",".length(); i++) {
/*  340 */         int code = ",".codePointAt(i);
/*  341 */         tmp = (char)((byte)pwd.charAt(t) ^ code);
/*      */       }
/*  343 */       res = res + tmp;
/*      */     }
/*  345 */     return res;
/*      */   }
/*      */ 
/*      */   public static void doAutoLogin(String userName, String password) {
/*  349 */     AutoConnectAction autoConnectAction = new AutoConnectAction(userName, password);
/*  350 */     GreedContext.publishEvent(autoConnectAction);
/*      */   }
/*      */ 
/*      */   public static void switchPlatform() {
/*  354 */     reinitPlatform(0);
/*      */   }
/*      */ 
/*      */   public static void reinitPlatform() {
/*  358 */     reinitPlatform(1);
/*      */   }
/*      */ 
/*      */   private static void reinitPlatform(int mode)
/*      */   {
/*  366 */     LOGGER.info("Platform reload started. JClient running : " + GreedContext.IS_JCLIENT_INVOKED);
/*      */ 
/*  368 */     ClientForm cf = (ClientForm)GreedContext.get("clientGui");
/*  369 */     cf.setVisible(false);
/*  370 */     GreedContext.setFirstAccountInfo(true);
/*      */ 
/*  372 */     ClientSettingsStorage clientSettingsStorage = (ClientSettingsStorage)GreedContext.get("settingsStorage");
/*  373 */     if (mode != 1)
/*  374 */       clientSettingsStorage.saveWorkspaceSettings();
/*      */     else {
/*  376 */       clientSettingsStorage.cleanUpWorkspaceSettingsCache();
/*      */     }
/*      */ 
/*  379 */     GreedTransportClient transportClient = (GreedTransportClient)GreedContext.get("transportClient");
/*  380 */     transportClient.disconnect();
/*  381 */     LOGGER.info("Transport stopped");
/*      */ 
/*  383 */     cleanAll();
/*  384 */     LOGGER.info("Cleanup done");
/*      */ 
/*  386 */     GreedContext.putInSingleton("properties", GreedContext.getGreedProperties());
/*  387 */     GreedContext.putInSingleton("calendars", GreedContext.getGreedProperties());
/*      */ 
/*  389 */     if (mode == 0) {
/*  390 */       PreferencesStorage.setJForexMode(GreedContext.IS_JCLIENT_INVOKED);
/*      */     }
/*      */ 
/*  393 */     reinitSingletonMap();
/*      */ 
/*  395 */     LOGGER.info("Reinit done");
/*      */     try
/*      */     {
/*  398 */       Thread.sleep(500L);
/*      */     } catch (InterruptedException e) {
/*      */     }
/*  401 */     transportClient.connect();
/*      */   }
/*      */ 
/*      */   public static void cleanAll()
/*      */   {
/*  408 */     Object previousClientForm = GreedContext.get("clientGui");
/*  409 */     if ((previousClientForm instanceof ClientForm)) {
/*  410 */       ((ClientForm)previousClientForm).getRootPane().removeAll();
/*  411 */       ((ClientForm)previousClientForm).getContentPane().removeAll();
/*      */     }
/*      */ 
/*  415 */     for (int i = 0; i < JFrame.getFrames().length; i++) {
/*  416 */       JFrame.getFrames()[i].dispose();
/*      */     }
/*      */ 
/*  419 */     resetActions();
/*      */ 
/*  421 */     IdManager.cleanManager();
/*      */ 
/*  423 */     cleanJForexPlatformStuff();
/*  424 */     LocalizationManager.clearCache();
/*  425 */     LotAmountChanger.clearCache();
/*      */ 
/*  427 */     GreedContext.setSingletonMap(new ConcurrentHashMap());
/*      */ 
/*  429 */     ChartsFrame.cleanChartsFrame();
/*      */ 
/*  431 */     System.gc();
/*      */   }
/*      */ 
/*      */   private static void cleanJForexPlatformStuff() {
/*  435 */     DDSChartsController chartsController = (DDSChartsController)GreedContext.get("chartsController");
/*  436 */     if (chartsController != null) {
/*  437 */       chartsController.dispose();
/*      */     }
/*      */ 
/*  440 */     if ((GreedContext.get("layoutManager") instanceof JForexClientFormLayoutManager)) {
/*  441 */       JForexClientFormLayoutManager jforexLayoutManager = (JForexClientFormLayoutManager)GreedContext.get("layoutManager");
/*  442 */       List panels = jforexLayoutManager.getStrategyTestPanels();
/*  443 */       for (StrategyTestPanel strategyTestPanel : panels) {
/*  444 */         strategyTestPanel.cancel();
/*      */       }
/*  446 */       jforexLayoutManager.getWorkspaceHelper().dispose();
/*      */     }
/*      */ 
/*  449 */     if (GreedContext.isStrategyAllowed())
/*  450 */       Strategies.get().stopAll();
/*      */   }
/*      */ 
/*      */   private static void resetActions()
/*      */   {
/*  455 */     SimpleApplicationEventMulticaster.getInstance().removeAllListeners();
/*  456 */     SimpleApplicationEventMulticaster.removeInstance();
/*      */   }
/*      */ 
/*      */   public static void firstSigletonMapInit() {
/*  460 */     GreedContext.putInSingleton("properties", GreedContext.getGreedProperties());
/*  461 */     GreedContext.putInSingleton("calendars", GreedContext.getGreedProperties());
/*      */ 
/*  463 */     initIPinfo();
/*  464 */     initAccountTypes();
/*  465 */     initLayoutTypes();
/*  466 */     initLabelInfo();
/*      */ 
/*  468 */     setupLogging();
/*  469 */     reinitSingletonMap();
/*      */   }
/*      */ 
/*      */   private static void initIPinfo() {
/*  473 */     InetAddress localhost = null;
/*      */     try
/*      */     {
/*  476 */       localhost = InetAddress.getLocalHost();
/*      */     } catch (UnknownHostException e) {
/*  478 */       LOGGER.error("Can't detect local IP : " + e.getMessage());
/*      */     }
/*      */ 
/*  481 */     String externalIp = (String)GreedContext.getProperty("external_ip");
/*  482 */     if (externalIp == null)
/*  483 */       LOGGER.error("Can't detect external IP");
/*      */     else {
/*  485 */       GreedContext.setConfig("external_ip", externalIp);
/*      */     }
/*      */ 
/*  488 */     GreedContext.setConfig("local_ip_address", localhost != null ? localhost.getHostAddress() : localhost);
/*      */   }
/*      */ 
/*      */   private static void initLabelInfo() {
/*  492 */     if (GreedContext.getProperty(GreedContext.WLABEL_SHORT_NAME_KEY) == null) {
/*  493 */       return;
/*      */     }
/*      */ 
/*  496 */     GuiUtilsAndConstants.LABEL_SHORT_NAME = (String)GreedContext.getProperty(GreedContext.WLABEL_SHORT_NAME_KEY);
/*  497 */     GuiUtilsAndConstants.LABEL_LONG_NAME = entityCheck((String)GreedContext.getProperty(GreedContext.WLABEL_LONG_NAME_KEY));
/*      */ 
/*  499 */     if (!GreedContext.isDukascopyPlatform) {
/*  500 */       GuiUtilsAndConstants.LABEL_TITLE_NAME = GuiUtilsAndConstants.LABEL_LONG_NAME;
/*      */     }
/*  502 */     GuiUtilsAndConstants.LABEL_URL = (String)GreedContext.getProperty(GreedContext.WLABEL_URL_KEY);
/*  503 */     GuiUtilsAndConstants.LABEL_PHONE = (String)GreedContext.getProperty(GreedContext.WLABEL_PHONE_KEY);
/*  504 */     GuiUtilsAndConstants.LABEL_SKYPE_ID = (String)GreedContext.getProperty(GreedContext.WLABEL_SKYPE_KEY);
/*      */ 
/*  506 */     GuiUtilsAndConstants.SKYPE_TO_PARTNER = MessageFormat.format(GuiUtilsAndConstants.SKYPE_TO_PARTNER_TEMPLATE, new Object[] { GuiUtilsAndConstants.LABEL_SKYPE_ID });
/*      */ 
/*  508 */     if (GuiUtilsAndConstants.LABEL_URL.contains("kakaku"))
/*  509 */       GreedContext.IS_KAKAKU_LABEL = true;
/*  510 */     else if (GuiUtilsAndConstants.LABEL_URL.contains("FXDD"))
/*  511 */       GreedContext.IS_FXDD_LABEL = true;
/*  512 */     else if (GuiUtilsAndConstants.LABEL_URL.contains("alpari"))
/*  513 */       GreedContext.IS_ALPARI_LABEL = true;
/*  514 */     else if (GuiUtilsAndConstants.LABEL_URL.contains("eu-live")) {
/*  515 */       GreedContext.IS_EU_LIVE = true;
/*      */     }
/*      */ 
/*  518 */     if (!GuiUtilsAndConstants.LABEL_SHORT_NAME.startsWith("Dukascopy")) {
/*  519 */       GreedContext.isDukascopyPlatform = false;
/*      */     }
/*      */ 
/*  522 */     byte[] bytes = (byte[])(byte[])GreedContext.getProperty(GreedContext.WLABEL_IMAGES_KEY);
/*      */     try
/*      */     {
/*  529 */       int ICON_WIDTH = 32;
/*  530 */       int ICON_HEIGTH = 32;
/*      */ 
/*  532 */       int LOGO_WIDTH = 240;
/*  533 */       int LOGO_HEIGTH = 60;
/*      */ 
/*  535 */       int SPLASH_WIDTH = 600;
/*  536 */       int SPLASH_HEIGTH = 370;
/*      */ 
/*  538 */       if (bytes == null) {
/*  539 */         throw new IOException("Image is not received");
/*      */       }
/*  541 */       BufferedImage image = ImageIO.read(new ByteArrayInputStream(bytes));
/*      */ 
/*  547 */       BufferedImage icon = image.getSubimage(0, 0, 32, 32);
/*  548 */       BufferedImage logo = image.getSubimage(0, 32, 240, 60);
/*  549 */       BufferedImage splash = image.getSubimage(0, 92, 600, 370);
/*      */ 
/*  551 */       GuiUtilsAndConstants.PLATFPORM_ICON = new ImageIcon(icon);
/*  552 */       GuiUtilsAndConstants.PLATFPORM_LOGO = new ImageIcon(logo);
/*  553 */       GuiUtilsAndConstants.PLATFPORM_SPLASH = new ImageIcon(splash);
/*      */     }
/*      */     catch (IOException e) {
/*  556 */       e.printStackTrace();
/*      */     }
/*      */   }
/*      */ 
/*      */   private static List<String> splitUserTypes(String input)
/*      */   {
/*  562 */     Pattern p = Pattern.compile("[,\\s]+");
/*  563 */     String[] result = p.split(input);
/*      */ 
/*  565 */     List accountTypes = new ArrayList();
/*      */ 
/*  567 */     for (int i = 0; i < result.length; i++) {
/*  568 */       accountTypes.add(result[i]);
/*      */     }
/*  570 */     return accountTypes;
/*      */   }
/*      */ 
/*      */   private static void initAccountTypes() {
/*  574 */     String typesList = (String)GreedContext.getProperty("userTypes");
/*  575 */     if (typesList == null) {
/*  576 */       LOGGER.warn("Types list was not found");
/*  577 */       return;
/*      */     }
/*      */ 
/*  580 */     List accountTypeList = splitUserTypes(typesList);
/*      */ 
/*  582 */     boolean isGlobal = accountTypeList.contains(UserType.GLOBAL.toString());
/*  583 */     GreedContext.setGlobal(isGlobal);
/*      */ 
/*  585 */     boolean isMiniFx = accountTypeList.contains(UserType.MINI_FX.toString());
/*  586 */     GreedContext.setMiniFx(isMiniFx);
/*      */ 
/*  588 */     boolean isContest = accountTypeList.contains(UserType.ANALITIC_CONTEST.toString());
/*  589 */     GreedContext.setContest(isContest);
/*      */ 
/*  591 */     boolean isReadOnly = accountTypeList.contains(UserType.READ_ONLY.toString());
/*  592 */     GreedContext.setReadOnly(isReadOnly);
/*      */ 
/*  594 */     boolean isHideReports = accountTypeList.contains(UserType.HIDE_REPORTS.toString());
/*  595 */     GreedContext.setHideReports(isHideReports);
/*      */ 
/*  597 */     boolean isManageStopLimit = accountTypeList.contains(UserType.MANAGE_STOP_LIMIT.toString());
/*  598 */     GreedContext.setManageStopLimits(isManageStopLimit);
/*      */   }
/*      */ 
/*      */   private static void initLayoutTypes()
/*      */   {
/*  604 */     Set typesSet = (Set)GreedContext.getProperty("additionalUserTypes");
/*      */ 
/*  606 */     if (typesSet == null) {
/*  607 */       LOGGER.warn("Layout Types list was not found");
/*  608 */       return;
/*      */     }
/*      */ 
/*  611 */     boolean isGlobalExtended = typesSet.contains(Integer.valueOf(LayoutType.GLOBAL_EXTENDED.getId()));
/*  612 */     GreedContext.setGlobalExtended(isGlobalExtended);
/*      */   }
/*      */ 
/*      */   private static void reinitSingletonMap()
/*      */   {
/*  618 */     assert (SwingUtilities.isEventDispatchThread());
/*  619 */     AccountStatement accountStatement = new AccountStatement();
/*  620 */     GreedContext.putInSingleton("accountStatement", accountStatement);
/*      */ 
/*  622 */     ClientSettingsStorageImpl settingsStorage = new ClientSettingsStorageImpl();
/*  623 */     GreedContext.putInSingleton("settingsStorage", settingsStorage);
/*      */ 
/*  625 */     ((ClientSettingsStorage)GreedContext.get("settingsStorage")).loadWorkspaceSettings();
/*      */ 
/*  627 */     IClientSettingsStorageAutoSaving clientSettingsStorageAutoSaving = new ClientSettingsStorageAutoSaving(settingsStorage);
/*  628 */     GreedContext.putInSingleton("settingsStorageAutosaving", clientSettingsStorageAutoSaving);
/*  629 */     clientSettingsStorageAutoSaving.startAutoSaving(180000L);
/*      */ 
/*  631 */     FilePathManager.reset();
/*      */ 
/*  633 */     IChartTemplateSettingsStorage chartTemplateSettingsStorage = new ChartTemplateSettingsStorage();
/*  634 */     GreedContext.putInSingleton("chartTemplateSettingsStorage", chartTemplateSettingsStorage);
/*      */ 
/*  636 */     MarketView marketView = new MarketView();
/*  637 */     GreedContext.putInSingleton("marketView", marketView);
/*  638 */     CurrencyConverter currencyConverter = CurrencyConverter.getCurrencyConverter();
/*  639 */     GreedContext.putInSingleton("currencyConverter", currencyConverter);
/*      */ 
/*  641 */     ApplicationClock applicationClock = new ApplicationClock();
/*  642 */     GreedContext.putInSingleton("applicationClock", applicationClock);
/*      */ 
/*  644 */     OrdersProvider.createInstance(OrderUtils.getInstance());
/*  645 */     GreedContext.putInSingleton("ordersDataProvider", OrdersProvider.getInstance());
/*      */ 
/*  647 */     initAutoTradingInfrastructure();
/*      */ 
/*  649 */     ClientFormLayoutManager layoutManager = ClientFormLayoutManager.getLayoutManager();
/*  650 */     layoutManager.build();
/*  651 */     GreedContext.putInSingleton("layoutManager", layoutManager);
/*      */ 
/*  653 */     ClientForm clientForm = new ClientForm(settingsStorage, layoutManager);
/*  654 */     layoutManager.setGuiElements(clientForm);
/*  655 */     clientForm.build();
/*  656 */     clientForm.addAccountInfoWatcher(clientForm);
/*  657 */     clientForm.addAccountInfoWatcher(clientForm.getStatusBar());
/*  658 */     clientForm.addAccountInfoWatcher(accountStatement);
/*  659 */     GreedContext.putInSingleton("clientGui", clientForm);
/*      */ 
/*  661 */     if (GreedContext.isStrategyAllowed()) {
/*  662 */       EditorFactory.init(clientForm);
/*  663 */       GreedContext.putInSingleton("iUserInterface", ((JForexClientFormLayoutManager)layoutManager).getIGUIManager());
/*      */     }
/*      */ 
/*  666 */     MarketOverviewFrame instrumentFrame = new MarketOverviewFrame(705, 700);
/*  667 */     instrumentFrame.setSettingsSaver(settingsStorage);
/*  668 */     GreedContext.putInSingleton("Dock", instrumentFrame);
/*  669 */     GreedContext.putInSingleton("newsAdapter", new NewsAdapter(20));
/*  670 */     GreedContext.putInSingleton("windowManager", new WindowManager(100, 100, true));
/*      */ 
/*  672 */     int logSendPeriodInSecs = 30;
/*  673 */     if (GreedContext.isFineLogging()) {
/*  674 */       logSendPeriodInSecs = 3;
/*      */     }
/*  676 */     String tradeLogUrlSuffix = (String)GreedContext.getProperty("tradelog_sfx.url");
/*  677 */     GreedContext.putInSingleton("Logger", new EmergencyLogger(logSendPeriodInSecs, GreedContext.getProperty("services1.url") + tradeLogUrlSuffix));
/*      */ 
/*  679 */     initTransport();
/*      */ 
/*  682 */     if (GreedContext.isStrategyAllowed()) {
/*  683 */       GreedContext.putInSingleton("ddsAgent", new AgentManager());
/*  684 */       Strategies.get();
/*      */ 
/*  686 */       FeedDataProvider.getDefaultInstance().unsubscribeFromAllCandlePeriods(Strategies.get());
/*  687 */       FeedDataProvider.getDefaultInstance().subscribeToAllCandlePeriods(Strategies.get());
/*  688 */       FeedDataProvider.getDefaultInstance().startInBackgroundFeedPreloadingToLocalCache();
/*      */     }
/*      */ 
/*  691 */     InstrumentAvailabilityManager.getInstance().updateWhiteList();
/*      */ 
/*  693 */     FeedDataProvider.getDefaultInstance().addDataFeedServerConnectionListener(new DataFeedServerConnectionListener(layoutManager)
/*      */     {
/*      */       public void disconnected()
/*      */       {
/*      */       }
/*      */ 
/*      */       public void connected()
/*      */       {
/*      */         try {
/*  702 */           this.val$layoutManager.getHistoricalDataManagerPanel().initTradableInstruments();
/*  703 */           FeedDataProvider.getDefaultInstance().removeDataFeedServerConnectionListener(this);
/*      */         }
/*      */         catch (Exception ex) {
/*  706 */           PlatformInitUtils.LOGGER.debug(ex.getMessage(), ex);
/*      */         }
/*      */       }
/*      */     });
/*      */   }
/*      */ 
/*      */   private static GreedTransportClient initTransport() {
/*  714 */     if (GreedContext.get("transportClient") != null) {
/*  715 */       GreedTransportClient transportClient = (GreedTransportClient)GreedContext.get("transportClient");
/*  716 */       LOGGER.debug("Shutting down the transport ...");
/*      */       try {
/*  718 */         transportClient.setListener(null);
/*  719 */         transportClient.terminate();
/*      */       } catch (Throwable e) {
/*  721 */         if (LOGGER.isDebugEnabled()) {
/*  722 */           LOGGER.error(e.getMessage(), e);
/*      */         }
/*      */       }
/*  725 */       LOGGER.debug("Transport shut downed");
/*      */     }
/*      */ 
/*  728 */     ClientListener clientListener = GreedClientListener.getInstance();
/*  729 */     GreedTransportClient transportClient = GreedTransportClient.getInstance(clientListener);
/*  730 */     Map tempMap = new ConcurrentHashMap();
/*  731 */     tempMap.put("clientListener", clientListener);
/*  732 */     tempMap.put("transportClient", transportClient);
/*  733 */     GreedContext.putAll(tempMap);
/*      */ 
/*  735 */     return transportClient;
/*      */   }
/*      */ 
/*      */   private static void initAutoTradingInfrastructure() {
/*  739 */     initChartsController();
/*  740 */     initCache();
/*      */ 
/*  742 */     String strategiesPath = ((ClientSettingsStorage)GreedContext.get("settingsStorage")).getMyStrategiesPath();
/*  743 */     if ((strategiesPath != null) && (!strategiesPath.equals(""))) {
/*  744 */       FilePathManager.getInstance().setStrategiesFolderPath(strategiesPath);
/*      */     }
/*  746 */     String indicatorsPath = ((ClientSettingsStorage)GreedContext.get("settingsStorage")).getMyIndicatorsPath();
/*  747 */     if ((indicatorsPath != null) && (!indicatorsPath.equals(""))) {
/*  748 */       FilePathManager.getInstance().setIndicatorsFolderPath(indicatorsPath);
/*      */     }
/*  750 */     String chartTemplatesPath = ((ClientSettingsStorage)GreedContext.get("settingsStorage")).getMyChartTemplatesPath();
/*  751 */     if ((chartTemplatesPath != null) && (!chartTemplatesPath.equals(""))) {
/*  752 */       FilePathManager.getInstance().setTemplatesFolderPath(chartTemplatesPath);
/*      */     }
/*      */ 
/*  755 */     String systemSettingsFolderPath = System.getProperty("jnlp.client.settings");
/*  756 */     if ((systemSettingsFolderPath != null) && (!systemSettingsFolderPath.equals("")) && (FilePathManager.getInstance().isFolderAccessible(systemSettingsFolderPath))) {
/*  757 */       FilePathManager.getInstance().setSystemSettingsFolderPath(systemSettingsFolderPath);
/*      */     }
/*      */ 
/*  760 */     NotificationUtilsProvider.setNotificationUtils(NotificationUtils.getInstance());
/*  761 */     initIndicatorsProvider();
/*      */ 
/*  763 */     FullAccessDisclaimDialog.reset();
/*  764 */     FullAccessDisclaimerProvider.setDisclaimer(FullAccessDisclaimDialog.getInstance());
/*      */ 
/*  766 */     GreedContext.putInSingleton("platformConsole", new PlatformConsoleImpl());
/*  767 */     JFXStreamHandlerFactory.registerFactory();
/*      */   }
/*      */ 
/*      */   public static void initChartsInfrastructure() {
/*  771 */     initChartsController();
/*  772 */     initCache();
/*  773 */     initIndicatorsProvider();
/*      */   }
/*      */ 
/*      */   private static void initCache() {
/*  777 */     String localCachePath = ((ClientSettingsStorage)GreedContext.get("settingsStorage")).getLocalCachePath();
/*  778 */     if ((localCachePath != null) && (!localCachePath.equals(""))) {
/*  779 */       FilePathManager.getInstance().setCacheFolderPath(localCachePath);
/*      */     }
/*  781 */     String cacheDir = FilePathManager.getInstance().getCacheDirectory();
/*  782 */     while (!FilePathManager.getInstance().isFolderAccessible(cacheDir)) {
/*  783 */       cacheDir = requestNewCacheDir(cacheDir);
/*  784 */       if (cacheDir == null) {
/*  785 */         System.exit(0);
/*  786 */         return;
/*  787 */       }if (!cacheDir.equals("")) {
/*  788 */         FilePathManager.getInstance().setCacheFolderPath(cacheDir);
/*      */       }
/*  790 */       FilePathManager.getInstance().setCacheFolderPath(cacheDir);
/*  791 */       ((ClientSettingsStorage)GreedContext.get("settingsStorage")).saveLocalCachePath(cacheDir);
/*      */     }
/*  793 */     initFeedDataProvider();
/*  794 */     DDSChartsController ddscc = getChartsController();
/*  795 */     GreedContext.putInSingleton("chartsController", ddscc);
/*  796 */     GreedContext.putInSingleton("feedDataProvider", FeedDataProvider.getDefaultInstance());
/*  797 */     FeedDataProvider.getDefaultInstance().addInstrumentSubscriptionListener(new InstrumentSubscriptionListener()
/*      */     {
/*      */       public void subscribedToInstrument(Instrument instrument) {
/*  800 */         FakeTickOnWeekendsAction fakeTickAction = new FakeTickOnWeekendsAction(this, instrument);
/*  801 */         GreedContext.publishEvent(fakeTickAction);
/*      */       }
/*      */ 
/*      */       public void unsubscribedFromInstrument(Instrument instrument) {
/*      */       } } );
/*      */   }
/*      */ 
/*      */   private static String requestNewCacheDir(String oldDir) {
/*  810 */     return JOptionPane.showInputDialog(null, "Cannot create cache directory, please provide new path", oldDir);
/*      */   }
/*      */ 
/*      */   private static void initIndicatorsProvider() {
/*  814 */     SettingsStorage clientSettingsStorage = (SettingsStorage)GreedContext.get("settingsStorage");
/*  815 */     IndicatorsProvider.createInstance(clientSettingsStorage);
/*      */   }
/*      */ 
/*      */   private static void initChartsController() {
/*  819 */     IChartClient chartClient = (IChartClient)GreedContext.get("settingsStorage");
/*  820 */     DDSChartsControllerImpl.initialize(chartClient);
/*      */   }
/*      */ 
/*      */   private static void initFeedDataProvider() {
/*      */     try {
/*  825 */       String cacheName = GreedContext.CLIENT_MODE != null ? GreedContext.CLIENT_MODE : "COMMON";
/*      */ 
/*  827 */       List feedCommissionHistory = GreedContext.getFeedCommissionHistory();
/*  828 */       Set supportedInstruments = GreedContext.getSupportedInstrument();
/*  829 */       FeedDataProvider.createFeedDataProvider(cacheName, feedCommissionHistory, supportedInstruments);
/*      */     }
/*      */     catch (DataCacheException e) {
/*  832 */       LOGGER.error(e.getMessage(), e);
/*  833 */       System.exit(1);
/*      */     }
/*      */   }
/*      */ 
/*      */   public static void initLookAndFeel() {
/*      */     try {
/*  839 */       if (LINUX) {
/*  840 */         boolean set = false;
/*  841 */         for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
/*  842 */           if ("Nimbus".equals(info.getName())) {
/*  843 */             UIManager.setLookAndFeel(info.getClassName());
/*  844 */             set = true;
/*  845 */             break;
/*      */           }
/*      */         }
/*  848 */         if (!set)
/*  849 */           UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
/*      */       }
/*      */       else {
/*  852 */         UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
/*      */       }
/*      */     } catch (Exception e) {
/*      */     }
/*  856 */     Toolkit.getDefaultToolkit().setDynamicLayout(true);
/*      */ 
/*  858 */     if (CLASSIC)
/*  859 */       UIManager.getDefaults().putDefaults(new Object[] { "ButtonUI", JRoundedButtonUI.class.getName() });
/*  860 */     else if (MACOSX)
/*      */     {
/*  862 */       for (Map.Entry entry : UIManager.getDefaults().entrySet()) {
/*  863 */         Object key = entry.getKey();
/*  864 */         Font font = UIManager.getFont(key);
/*  865 */         if (font != null)
/*  866 */           UIManager.put(key, new UIDefaults.ProxyLazyValue("javax.swing.plaf.FontUIResource", null, new Object[] { font.getName(), Integer.valueOf(font.getStyle()), Integer.valueOf(font.getSize() - 2) }));
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   public static void initStaticValues()
/*      */   {
/*  873 */     String sessionId = String.valueOf(UUID.randomUUID());
/*  874 */     GreedContext.setConfig("SESSION_ID", sessionId);
/*      */   }
/*      */ 
/*      */   public static void reinitStaticValues() {
/*  878 */     initStaticValues();
/*      */   }
/*      */ 
/*      */   private static DDSChartsController getChartsController() {
/*  882 */     return DDSChartsControllerImpl.getInstance();
/*      */   }
/*      */ 
/*      */   public static void initAWTListener() {
/*  886 */     if (windowCountAWTListener != null) {
/*  887 */       Toolkit.getDefaultToolkit().removeAWTEventListener(windowCountAWTListener);
/*      */     }
/*      */ 
/*  890 */     ClientForm clientForm = (ClientForm)GreedContext.get("clientGui");
/*  891 */     windowCountAWTListener = new WindowCountAWTListener(clientForm);
/*  892 */     Toolkit.getDefaultToolkit().addAWTEventListener(windowCountAWTListener, 64L);
/*      */   }
/*      */ 
/*      */   public static void setupLogging() {
/*  896 */     NotificationUtilsProvider.setNotificationUtils(NotificationUtils.getInstance());
/*      */ 
/*  898 */     if (GreedContext.isFineLogging())
/*      */     {
/*  900 */       java.util.logging.Logger.getLogger("com").setLevel(Level.FINER);
/*  901 */       java.util.logging.Logger.getLogger(TransportClient.class.getPackage().getName()).setLevel(Level.INFO);
/*  902 */       java.util.logging.Logger.getLogger(CacheManager.class.getPackage().getName()).setLevel(Level.FINER);
/*  903 */       java.util.logging.Logger.getLogger(ChartBuilder.class.getPackage().getName()).setLevel(Level.INFO);
/*  904 */       java.util.logging.Logger.getLogger(AbstractDrawingStrategyFactory.class.getPackage().getName()).setLevel(Level.INFO);
/*      */ 
/*  910 */       java.util.logging.Logger.getLogger(GreedClientListener.class.getName()).setLevel(Level.FINE);
/*  911 */       java.util.logging.Logger.getLogger(GreedContext.class.getName()).setLevel(Level.FINE);
/*  912 */       java.util.logging.Logger.getLogger(PositionsTableModel.class.getName()).setLevel(Level.WARNING);
/*  913 */       java.util.logging.Logger.getLogger(OrderTableModel.class.getName()).setLevel(Level.WARNING);
/*  914 */       java.util.logging.Logger.getLogger(OrdersPanel.class.getName()).setLevel(Level.WARNING);
/*  915 */       java.util.logging.Logger.getLogger(EmergencyLogger.class.getName()).setLevel(Level.WARNING);
/*      */ 
/*  917 */       java.util.logging.Logger.getLogger(IChartController.class.getName()).setLevel(Level.FINER);
/*  918 */       java.util.logging.Logger.getLogger(CandlesDataProvider.class.getName()).setLevel(Level.INFO);
/*  919 */       java.util.logging.Logger.getLogger(CandlesDataProvider.class.getName()).setLevel(Level.INFO);
/*  920 */       java.util.logging.Logger.getLogger(AbstractDataProvider.class.getName()).setLevel(Level.INFO);
/*  921 */       java.util.logging.Logger.getLogger(OrdersDataProvider.class.getName()).setLevel(Level.INFO);
/*  922 */       java.util.logging.Logger.getLogger(AbstractDataSequenceProvider.class.getName()).setLevel(Level.INFO);
/*  923 */       java.util.logging.Logger.getLogger(CandlesDataSequenceProvider.class.getName()).setLevel(Level.INFO);
/*  924 */       java.util.logging.Logger.getLogger(TicksDataSequenceProvider.class.getName()).setLevel(Level.INFO);
/*  925 */       java.util.logging.Logger.getLogger(ChartBuilder.class.getName()).setLevel(Level.INFO);
/*  926 */       java.util.logging.Logger.getLogger(DDSChartsControllerImpl.class.getName()).setLevel(Level.INFO);
/*  927 */       java.util.logging.Logger.getLogger("com.dukascopy.charts.view.drawingstrategies.AbstractCandleVisualisationDrawingStrategy").setLevel(Level.INFO);
/*  928 */       java.util.logging.Logger.getLogger("com.dukascopy.charts.view.drawingstrategies.AbstractTickVisualisationDrawingStrategy").setLevel(Level.INFO);
/*  929 */       java.util.logging.Logger.getLogger("com.dukascopy.charts.chartbuilder.GeometryOperationManagerImpl").setLevel(Level.INFO);
/*  930 */       java.util.logging.Logger.getLogger("com.dukascopy.charts.chartbuilder.MainDataOperationManager").setLevel(Level.INFO);
/*  931 */       java.util.logging.Logger.getLogger("com.dukascopy.charts.chartbuilder.ChartsGuiManager").setLevel(Level.INFO);
/*  932 */       java.util.logging.Logger.getLogger(History.class.getName()).setLevel(Level.INFO);
/*      */     } else {
/*  934 */       java.util.logging.Logger.getLogger("com").setLevel(Level.WARNING);
/*  935 */       java.util.logging.Logger.getLogger("com.dukascopy.transport").setLevel(Level.INFO);
/*      */     }
/*      */   }
/*      */ 
/*      */   private static void saveClientSettingsOnExit() {
/*  940 */     LOGGER.debug("Saving client settings");
/*  941 */     ClientSettingsStorage settingsStorage = (ClientSettingsStorage)GreedContext.get("settingsStorage");
/*      */ 
/*  943 */     if (settingsStorage.restoreWorkspaceSaveOnExitEnabled().booleanValue())
/*  944 */       settingsStorage.saveWorkspaceSettings();
/*      */   }
/*      */ 
/*      */   public static void closeApplication()
/*      */   {
/*  949 */     ClientForm clientForm = (ClientForm)GreedContext.get("clientGui");
/*      */ 
/*  951 */     clientForm.setVisible(false);
/*      */ 
/*  953 */     saveClientSettingsOnExit();
/*      */ 
/*  955 */     if (GreedContext.isStrategyAllowed()) {
/*  956 */       DDSChartsController chartsController = (DDSChartsController)GreedContext.get("chartsController");
/*  957 */       chartsController.dispose();
/*      */     }
/*      */ 
/*  960 */     MarketOverviewFrame dock = (MarketOverviewFrame)GreedContext.get("Dock");
/*  961 */     if (dock.isVisible()) {
/*  962 */       dock.saveBeforeColsing();
/*      */     }
/*      */ 
/*  966 */     GreedContext.setConfig("logoff", Boolean.valueOf(true));
/*      */ 
/*  968 */     LOGGER.debug("Disconnecting");
/*  969 */     GreedTransportClient client = (GreedTransportClient)GreedContext.get("transportClient");
/*      */     try {
/*  971 */       client.controlRequest(new QuitRequestMessage());
/*      */     } catch (Throwable ex) {
/*  973 */       LOGGER.error("Error while sending quit request", ex);
/*      */     }
/*      */     try
/*      */     {
/*  977 */       Thread.sleep(1000L);
/*      */     } catch (InterruptedException ex) {
/*      */     }
/*      */     try {
/*  981 */       client.disconnect();
/*      */     } catch (Throwable ex) {
/*  983 */       LOGGER.error("Error while disconnecting client", ex);
/*      */     }
/*      */     try
/*      */     {
/*  987 */       client.terminate();
/*      */     } catch (Throwable ex) {
/*  989 */       LOGGER.error("Error while teminating client", ex);
/*      */     }
/*      */ 
/*  992 */     if (GreedContext.isActivityLoggingEnabled()) {
/*  993 */       EmergencyLogger emergencyLogger = (EmergencyLogger)GreedContext.get("Logger");
/*  994 */       emergencyLogger.sendLog();
/*      */     }
/*      */     try
/*      */     {
/*  998 */       Thread.sleep(1000L);
/*      */     } catch (InterruptedException ex) {
/*      */     }
/* 1001 */     System.exit(0);
/*      */   }
/*      */ 
/*      */   private static String entityCheck(String fullName) {
/* 1005 */     Pattern entityPattern = Pattern.compile("(&#[0-9]*;)");
/* 1006 */     StringBuffer buffer = new StringBuffer();
/* 1007 */     Matcher matcher = entityPattern.matcher(fullName);
/* 1008 */     while (matcher.find()) {
/* 1009 */       matcher.appendReplacement(buffer, String.valueOf((char)Integer.parseInt(matcher.group().substring(matcher.group().lastIndexOf(35) + 1, matcher.group().lastIndexOf(59)))));
/*      */     }
/*      */ 
/* 1019 */     matcher.appendTail(buffer);
/* 1020 */     return buffer.toString();
/*      */   }
/*      */ 
/*      */   public static void setSecurityInfo4Order(OrderMessage orderMessage) {
/* 1024 */     String externalIp = (String)GreedContext.getConfig("external_ip");
/* 1025 */     if (externalIp != null)
/* 1026 */       orderMessage.setExternalIp(externalIp);
/*      */     else {
/* 1028 */       LOGGER.info("External IP = {}", externalIp);
/*      */     }
/*      */ 
/* 1031 */     String localIp = (String)GreedContext.getConfig("local_ip_address");
/* 1032 */     if (localIp != null)
/* 1033 */       orderMessage.setInternalIp(localIp);
/*      */     else {
/* 1035 */       LOGGER.info("Local IP = {}", localIp);
/*      */     }
/*      */ 
/* 1038 */     String sessionId = (String)GreedContext.getConfig("SESSION_ID");
/* 1039 */     if (sessionId != null)
/* 1040 */       orderMessage.setSessionId(sessionId);
/*      */     else
/* 1042 */       LOGGER.info("Session ID = {}", sessionId);
/*      */   }
/*      */ 
/*      */   public static void setExtSysIdForContest(OrderGroupMessage orderGroupMessage)
/*      */   {
/* 1047 */     if ((GreedContext.isContest()) && (
/* 1048 */       (orderGroupMessage.getExternalSysId() == null) || ("".equals(orderGroupMessage.getExternalSysId().trim())))) {
/* 1049 */       String id = String.valueOf(System.currentTimeMillis());
/* 1050 */       orderGroupMessage.setExternalSysId(id);
/* 1051 */       orderGroupMessage.setSignalId(id);
/*      */     }
/*      */   }
/*      */ 
/*      */   public static void setExtSysIdForOrderGroup(OrderGroupMessage orderGroupMessage)
/*      */   {
/* 1057 */     if (((GreedContext.isSignalServerInUse()) || (GreedContext.isContest())) && (
/* 1058 */       (orderGroupMessage.getExternalSysId() == null) || ("".equals(orderGroupMessage.getExternalSysId().trim())))) {
/* 1059 */       String id = GreedContext.getSignalServerId() != null ? GreedContext.getSignalServerId() + "@" + System.currentTimeMillis() : String.valueOf(System.currentTimeMillis());
/*      */ 
/* 1062 */       orderGroupMessage.setExternalSysId(id);
/* 1063 */       orderGroupMessage.setSignalId(id);
/*      */ 
/* 1065 */       setExtIdforOrderMessages(orderGroupMessage, id);
/*      */     }
/*      */   }
/*      */ 
/*      */   public static void setExtSysIdJUSTForOrderGroup(OrderGroupMessage orderGroupMessage)
/*      */   {
/* 1071 */     if ((GreedContext.isSignalServerInUse()) && (
/* 1072 */       (orderGroupMessage.getExternalSysId() == null) || ("".equals(orderGroupMessage.getExternalSysId().trim())))) {
/* 1073 */       String id = GreedContext.getSignalServerId() + "@" + System.currentTimeMillis();
/* 1074 */       orderGroupMessage.setExternalSysId(id);
/* 1075 */       orderGroupMessage.setSignalId(id);
/*      */     }
/*      */   }
/*      */ 
/*      */   public static void setExtSysIdForMergeMessage(MergePositionsMessage mergePositionsMessage)
/*      */   {
/* 1081 */     if (GreedContext.isSignalServerInUse()) {
/* 1082 */       String id = GreedContext.getSignalServerId() + "@" + System.currentTimeMillis();
/* 1083 */       mergePositionsMessage.setExternalSysId(id);
/*      */     }
/*      */   }
/*      */ 
/*      */   private static void setSignalIdforOrderMessages(OrderGroupMessage orderGroupMessage, String id) {
/* 1088 */     int counter = 0;
/* 1089 */     for (OrderMessage order : orderGroupMessage.getOrders())
/* 1090 */       order.setSignalId(id + "_" + System.currentTimeMillis() + counter++);
/*      */   }
/*      */ 
/*      */   private static void setExtIdforOrderMessages(OrderGroupMessage orderGroupMessage, String id)
/*      */   {
/* 1095 */     int counter = 0;
/* 1096 */     for (OrderMessage order : orderGroupMessage.getOrders()) {
/* 1097 */       order.setExternalSysId(id + "_" + System.currentTimeMillis() + counter);
/* 1098 */       order.setSignalId(id + "_" + System.currentTimeMillis() + counter++);
/*      */     }
/*      */   }
/*      */ 
/*      */   public static void setExtIdforOrderMessages(OrderGroupMessage orderGroupMessage) {
/* 1103 */     int counter = 0;
/* 1104 */     for (OrderMessage order : orderGroupMessage.getOrders())
/* 1105 */       if ((null == order.getExternalSysId()) || ("".equals(order.getExternalSysId())) || (null == order.getSignalId()) || ("".equals(order.getSignalId())))
/*      */       {
/* 1107 */         order.setExternalSysId(orderGroupMessage.getExternalSysId() + "_" + System.currentTimeMillis() + counter);
/* 1108 */         order.setSignalId(orderGroupMessage.getSignalId() + "_" + System.currentTimeMillis() + counter++);
/*      */       }
/*      */   }
/*      */ 
/*      */   public static Map<Instrument, BigDecimal> calculateSettelmentRates(Map<Instrument, BigDecimal> avgPriceMap)
/*      */   {
/* 1114 */     BigDecimal etalonPrice = (BigDecimal)avgPriceMap.get(Instrument.EURUSD);
/*      */ 
/* 1116 */     Map points = new HashMap();
/*      */ 
/* 1118 */     for (Map.Entry entry : avgPriceMap.entrySet()) {
/* 1119 */       BigDecimal value = etalonPrice.divide((BigDecimal)entry.getValue(), 2, RoundingMode.HALF_UP);
/* 1120 */       if ((Currency.getInstance("JPY").equals(((Instrument)entry.getKey()).getSecondaryCurrency())) || (Currency.getInstance("HUF").equals(((Instrument)entry.getKey()).getSecondaryCurrency())))
/*      */       {
/* 1122 */         value = value.multiply(new BigDecimal(100));
/*      */       }
/*      */ 
/* 1126 */       if (value.compareTo(new BigDecimal(0)) == 1) {
/* 1127 */         value = new BigDecimal(1).divide(value, 5, RoundingMode.HALF_UP).multiply(new BigDecimal(2), new MathContext(5));
/*      */       }
/* 1129 */       points.put((Instrument)entry.getKey(), value);
/*      */     }
/*      */ 
/* 1132 */     return points;
/*      */   }
/*      */ 
/*      */   static
/*      */   {
/*   96 */     LOGGER = LoggerFactory.getLogger(PlatformInitUtils.class);
/*      */   }
/*      */ 
/*      */   public static enum LayoutType
/*      */   {
/*  126 */     GLOBAL_EXTENDED(14);
/*      */ 
/*      */     private int layoutId;
/*      */ 
/*  131 */     private LayoutType(int layoutId) { this.layoutId = layoutId; }
/*      */ 
/*      */     public int getId()
/*      */     {
/*  135 */       return this.layoutId;
/*      */     }
/*      */   }
/*      */ 
/*      */   public static enum UserType
/*      */   {
/*  101 */     REGULAR, 
/*  102 */     GLOBAL, 
/*  103 */     GLOBAL_EXTENDED, 
/*  104 */     MINI_FX, 
/*  105 */     ANALITIC_CONTEST, 
/*  106 */     READ_ONLY, 
/*  107 */     MANAGE_STOP_LIMIT, 
/*  108 */     HIDE_REPORTS;
/*      */   }
/*      */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.util.PlatformInitUtils
 * JD-Core Version:    0.6.0
 */