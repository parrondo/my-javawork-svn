/*     */ package com.dukascopy.dds2.greed.actions;
/*     */ 
/*     */ import com.dukascopy.charts.data.datacache.FeedDataProvider;
/*     */ import com.dukascopy.dds2.greed.GreedContext;
/*     */ import com.dukascopy.dds2.greed.gui.ClientForm;
/*     */ import com.dukascopy.dds2.greed.gui.DealPanel;
/*     */ import com.dukascopy.dds2.greed.gui.GuiUtilsAndConstants;
/*     */ import com.dukascopy.dds2.greed.gui.LoginForm;
/*     */ import com.dukascopy.dds2.greed.gui.component.WorkspacePanel;
/*     */ import com.dukascopy.dds2.greed.gui.component.connect.ConnectStatus;
/*     */ import com.dukascopy.dds2.greed.gui.component.dowjones.calendar.DowJonesCalendarPanel;
/*     */ import com.dukascopy.dds2.greed.gui.component.dowjones.news.DowJonesNewsPanel;
/*     */ import com.dukascopy.dds2.greed.gui.component.message.MessagePanel;
/*     */ import com.dukascopy.dds2.greed.gui.component.status.GreedStatusBar;
/*     */ import com.dukascopy.dds2.greed.gui.l10n.LocalizationManager;
/*     */ import com.dukascopy.dds2.greed.gui.settings.ClientSettingsStorage;
/*     */ import com.dukascopy.dds2.greed.model.Notification;
/*     */ import com.dukascopy.transport.client.TransportClient;
/*     */ import com.dukascopy.transport.common.msg.ProtocolMessage;
/*     */ import com.dukascopy.transport.common.msg.properties.UserPropertiesRequestMessage;
/*     */ import com.dukascopy.transport.common.msg.properties.UserPropertiesResponseMessage;
/*     */ import com.dukascopy.transport.common.msg.request.InitRequestMessage;
/*     */ import com.dukascopy.transport.common.msg.request.QuoteSubscribeRequestMessage;
/*     */ import com.dukascopy.transport.common.msg.response.OkResponseMessage;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import java.util.Properties;
/*     */ import java.util.concurrent.TimeUnit;
/*     */ import javax.swing.JOptionPane;
/*     */ import javax.swing.SwingUtilities;
/*     */ import org.slf4j.Logger;
/*     */ import org.slf4j.LoggerFactory;
/*     */ 
/*     */ public class AuthenticatedAction extends AppActionEvent
/*     */ {
/*  44 */   private static final Logger LOGGER = LoggerFactory.getLogger(AuthenticatedAction.class);
/*  45 */   private static final long REQUEST_TIMEOUT = TimeUnit.SECONDS.toMillis(5L);
/*     */   private final TransportClient transportClient;
/*  48 */   private final LoginForm loginForm = LoginForm.getInstance();
/*     */   private MessagePanel messagePanel;
/*     */   private ProtocolMessage subscriptionResult;
/*     */   private List<String> subscribeToInstruments;
/*     */   private DowJonesNewsPanel newsPanel;
/*     */   private DowJonesCalendarPanel calendarPanel;
/*     */ 
/*     */   public AuthenticatedAction(TransportClient transportClient)
/*     */   {
/*  58 */     super("Connect", true, true);
/*     */ 
/*  60 */     LOGGER.debug("Connected to API");
/*     */ 
/*  62 */     this.transportClient = transportClient;
/*     */ 
/*  64 */     if ((this.loginForm == null) || (this.loginForm.getLoginPanel() == null)) {
/*  65 */       if (GreedContext.get("clientGui") != null)
/*  66 */         this.messagePanel = ((ClientForm)GreedContext.get("clientGui")).getMessagePanel();
/*     */     }
/*     */     else {
/*  69 */       this.messagePanel = new MessagePanel(false);
/*  70 */       this.messagePanel.build();
/*     */     }
/*     */   }
/*     */ 
/*     */   public void updateGuiBefore()
/*     */   {
/*  76 */     if ((this.loginForm != null) && (this.loginForm.getLoginPanel() != null)) {
/*  77 */       Notification authNote = new Notification(null, "Authenticated successfully!");
/*  78 */       Notification authSubscribe = new Notification("Subscribing to market data feed...");
/*  79 */       this.messagePanel.postMessage(authNote);
/*  80 */       this.messagePanel.postMessage(authSubscribe);
/*  81 */       this.messagePanel.repaint();
/*     */     }
/*     */ 
/*  84 */     ClientForm clientForm = (ClientForm)GreedContext.get("clientGui");
/*  85 */     clientForm.clearOrderModels();
/*     */ 
/*  87 */     this.newsPanel = clientForm.getNewsPanel();
/*  88 */     this.calendarPanel = clientForm.getCalendarPanel();
/*     */   }
/*     */ 
/*     */   public void doAction()
/*     */   {
/*  93 */     if (GreedContext.isStrategyAllowed()) {
/*  94 */       connectToHistoryServer();
/*     */     }
/*     */ 
/*  97 */     subscribeToInstruments();
/*  98 */     subscribeToNews();
/*     */ 
/* 100 */     if (GreedContext.isStrategyAllowed())
/* 101 */       requestUserProperties();
/*     */   }
/*     */ 
/*     */   public void updateGuiAfter()
/*     */   {
/* 107 */     doUpdateGui();
/* 108 */     if (GreedContext.get("feedDataProvider") != null) {
/* 109 */       FeedDataProvider feedDataProvider = (FeedDataProvider)GreedContext.get("feedDataProvider");
/* 110 */       feedDataProvider.setInstrumentNamesSubscribed(this.subscribeToInstruments);
/* 111 */       feedDataProvider.connected();
/*     */     }
/*     */   }
/*     */ 
/*     */   private void connectToHistoryServer()
/*     */   {
/* 118 */     List urls = GreedContext.LOGIN_URLS;
/*     */ 
/* 120 */     String login = (String)GreedContext.getConfig("account_name");
/* 121 */     String historyServerUrl = (String)GreedContext.getProperty("history.server.url");
/* 122 */     String encryptionKey = (String)GreedContext.getProperty("encryptionKey");
/* 123 */     String version = GreedContext.CLIENT_VERSION;
/*     */ 
/* 125 */     FeedDataProvider feedDataProvider = (FeedDataProvider)GreedContext.get("feedDataProvider");
/* 126 */     String sessionId = (String)GreedContext.getConfig("SESSION_ID");
/* 127 */     feedDataProvider.connectToHistoryServer(urls, login, sessionId, historyServerUrl, encryptionKey, version);
/*     */   }
/*     */ 
/*     */   private void subscribeToInstruments() {
/* 131 */     List tickerInstruments = new ArrayList();
/*     */     try {
/* 133 */       SwingUtilities.invokeAndWait(new Runnable(tickerInstruments) {
/*     */         public void run() {
/* 135 */           WorkspacePanel tickerPanel = ((ClientForm)GreedContext.get("clientGui")).getDealPanel().getWorkspacePanel();
/* 136 */           if ((tickerPanel != null) && (tickerPanel.getInstruments() != null) && (tickerPanel.getInstruments().isEmpty()))
/*     */           {
/* 140 */             this.val$tickerInstruments.addAll(tickerPanel.getInstruments());
/*     */           }
/*     */         } } );
/*     */     } catch (Exception ex) {
/* 145 */       LOGGER.error("Error while obtaining ticker instruments", ex);
/*     */     }
/*     */ 
/* 148 */     QuoteSubscribeRequestMessage subscribeRequest = new QuoteSubscribeRequestMessage();
/* 149 */     if (tickerInstruments.size() > 0) {
/* 150 */       subscribeRequest.setInstruments(tickerInstruments);
/*     */     } else {
/* 152 */       ClientSettingsStorage clientSettingsStorage = (ClientSettingsStorage)GreedContext.get("settingsStorage");
/* 153 */       List restoredSelectedInstruments = clientSettingsStorage.restoreSelectedInstruments();
/*     */ 
/* 155 */       for (String instrument : restoredSelectedInstruments) {
/* 156 */         if (instrument.indexOf(47) < 0) {
/* 157 */           restoredSelectedInstruments.remove(instrument);
/*     */         }
/*     */       }
/*     */ 
/* 161 */       if (restoredSelectedInstruments.size() == 0) {
/* 162 */         restoredSelectedInstruments.add("ALL");
/*     */       }
/*     */ 
/* 165 */       subscribeRequest.setInstruments(restoredSelectedInstruments);
/*     */     }
/*     */ 
/* 168 */     if (this.transportClient.isOnline()) {
/* 169 */       InitRequestMessage initRequest = new InitRequestMessage();
/* 170 */       LOGGER.debug("Sending init request : {}", initRequest);
/* 171 */       this.transportClient.controlRequest(initRequest);
/* 172 */       subscribeRequest.setQuotesOnly(Boolean.valueOf(true));
/* 173 */       LOGGER.debug("Initial subscribing to instruments : {}", subscribeRequest.getInstruments());
/* 174 */       this.subscriptionResult = this.transportClient.controlRequest(subscribeRequest);
/*     */     } else {
/* 176 */       LOGGER.warn("Client isn't online : unable subscribe to instruments");
/*     */     }
/*     */ 
/* 179 */     this.subscribeToInstruments = subscribeRequest.getInstruments();
/*     */   }
/*     */ 
/*     */   private void requestUserProperties()
/*     */   {
/* 184 */     if (this.transportClient.isOnline()) {
/* 185 */       new Thread(new Runnable()
/*     */       {
/*     */         public void run() {
/*     */           try {
/* 189 */             AuthenticatedAction.LOGGER.debug("Requesting user properties");
/* 190 */             UserPropertiesRequestMessage request = new UserPropertiesRequestMessage();
/* 191 */             ProtocolMessage response = AuthenticatedAction.this.transportClient.controlSynchRequest(request, Long.valueOf(AuthenticatedAction.REQUEST_TIMEOUT));
/* 192 */             if ((response instanceof UserPropertiesResponseMessage)) {
/* 193 */               Properties userProperties = ((UserPropertiesResponseMessage)response).getUserProperties();
/* 194 */               AuthenticatedAction.LOGGER.debug("User properties : {}", userProperties);
/* 195 */               GreedContext.setUserProperties(userProperties);
/*     */             } else {
/* 197 */               AuthenticatedAction.LOGGER.warn("Wrong response on user properties request : {}", response);
/*     */             }
/*     */           } catch (Exception ex) {
/* 200 */             AuthenticatedAction.LOGGER.error("Error while requesting user properties : {}", ex.getMessage());
/*     */           }
/*     */         }
/*     */       }
/*     */       , "User properties request").start();
/*     */     }
/*     */     else
/*     */     {
/* 205 */       LOGGER.warn("Client isn't online : unable to request user properties");
/*     */     }
/*     */   }
/*     */ 
/*     */   private void subscribeToNews() {
/* 210 */     if (this.newsPanel.isSubscribed()) {
/* 211 */       LOGGER.debug("Subscribing to DJ news");
/* 212 */       this.newsPanel.subscribe();
/*     */     }
/* 214 */     if (this.calendarPanel.isSubscribed()) {
/* 215 */       LOGGER.debug("Subscribing to DJ calendar");
/* 216 */       this.calendarPanel.subscribe();
/*     */     }
/*     */   }
/*     */ 
/*     */   private void doUpdateGui() {
/* 221 */     ClientForm clientForm = (ClientForm)GreedContext.get("clientGui");
/*     */ 
/* 223 */     if ((this.subscriptionResult instanceof OkResponseMessage)) {
/* 224 */       clientForm.getStatusBar().setConnectStatus(ConnectStatus.ONLINE);
/*     */ 
/* 226 */       String mode = GreedContext.CLIENT_MODE;
/*     */ 
/* 228 */       Object[] params = null;
/*     */ 
/* 230 */       if (GreedContext.isReadOnly())
/* 231 */         params = new Object[] { GreedContext.getAccountName(), mode, "", GuiUtilsAndConstants.LABEL_TITLE_NAME, LocalizationManager.getText("view.only") };
/* 232 */       else if (GreedContext.isContest())
/* 233 */         params = new Object[] { GreedContext.getAccountName(), mode, LocalizationManager.getText("contest"), GuiUtilsAndConstants.LABEL_TITLE_NAME, "" };
/*     */       else {
/* 235 */         params = new Object[] { GreedContext.getAccountName(), mode, "", GuiUtilsAndConstants.LABEL_TITLE_NAME, "" };
/*     */       }
/*     */ 
/* 238 */       clientForm.setParams(params);
/* 239 */       clientForm.setTitle("frame.client.form");
/*     */     }
/* 241 */     else if (this.messagePanel != null) {
/* 242 */       this.messagePanel.postMessage(new Notification(null, "Subscription failed. Please check connection."));
/*     */     }
/*     */ 
/* 245 */     if (this.messagePanel != null) {
/* 246 */       this.messagePanel.repaint();
/*     */     }
/*     */ 
/* 249 */     if (GreedContext.isReadOnly()) {
/* 250 */       showViewModeConfirmation();
/*     */     }
/*     */ 
/* 253 */     if (GreedContext.isContest()) {
/* 254 */       showContestModeConfirmation();
/*     */     }
/*     */ 
/* 257 */     if (!clientForm.isVisible()) {
/* 258 */       clientForm.clearMessageLog();
/*     */     }
/*     */ 
/* 261 */     Notification notification = new Notification("Connected.");
/* 262 */     PostMessageAction post = new PostMessageAction(this, notification);
/* 263 */     GreedContext.publishEvent(post);
/*     */   }
/*     */ 
/*     */   private static void showViewModeConfirmation() {
/* 267 */     JOptionPane.showMessageDialog((ClientForm)GreedContext.get("clientGui"), LocalizationManager.getText("joption.pane.view.mode.message"), LocalizationManager.getText("joption.pane.message"), 1);
/*     */   }
/*     */ 
/*     */   private static void showContestModeConfirmation()
/*     */   {
/* 276 */     JOptionPane.showMessageDialog((ClientForm)GreedContext.get("clientGui"), LocalizationManager.getText("joption.pane.contest.mode.message"), LocalizationManager.getText("joption.pane.message"), 1);
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.actions.AuthenticatedAction
 * JD-Core Version:    0.6.0
 */