/*     */ package com.dukascopy.dds2.greed.actions;
/*     */ 
/*     */ import com.dukascopy.charts.data.datacache.FeedDataProvider;
/*     */ import com.dukascopy.dds2.greed.GreedContext;
/*     */ import com.dukascopy.dds2.greed.config.GreedProperties;
/*     */ import com.dukascopy.dds2.greed.connection.GreedClientListener;
/*     */ import com.dukascopy.dds2.greed.connection.GreedConnectionUtils;
/*     */ import com.dukascopy.dds2.greed.connection.GreedTransportClient;
/*     */ import com.dukascopy.dds2.greed.gui.GuiUtilsAndConstants;
/*     */ import com.dukascopy.dds2.greed.gui.LoginForm;
/*     */ import com.dukascopy.dds2.greed.gui.component.LoginPanel;
/*     */ import com.dukascopy.dds2.greed.gui.component.dialog.disclaimers.EuLiveDisclaimerDialog;
/*     */ import com.dukascopy.dds2.greed.gui.component.message.MessagePanel;
/*     */ import com.dukascopy.dds2.greed.gui.component.news.NewsAdapter;
/*     */ import com.dukascopy.dds2.greed.gui.l10n.LocalizationManager;
/*     */ import com.dukascopy.dds2.greed.model.Notification;
/*     */ import com.dukascopy.dds2.greed.util.PlatformInitUtils;
/*     */ import java.net.InetSocketAddress;
/*     */ import javax.swing.JProgressBar;
/*     */ import javax.swing.SwingUtilities;
/*     */ import org.slf4j.Logger;
/*     */ import org.slf4j.LoggerFactory;
/*     */ 
/*     */ public class PlatformInitAction extends AppActionEvent
/*     */ {
/*  31 */   private static final Logger LOGGER = LoggerFactory.getLogger(PlatformInitAction.class);
/*     */   private GreedTransportClient transportClient;
/*     */   private GreedClientListener clientListener;
/*  36 */   private LoginPanel loginPanel = LoginForm.getInstance().getLoginPanel();
/*     */ 
/*  38 */   private String accountName = (String)GreedContext.getConfig("account_name");
/*  39 */   private String instanceId = (String)GreedContext.getConfig("SESSION_ID");
/*     */   private String ticket;
/*     */   private String apiURL;
/*     */ 
/*     */   public PlatformInitAction(Object source, String ticket, String url)
/*     */   {
/*  46 */     super(source, false, false);
/*  47 */     this.ticket = ticket;
/*  48 */     this.apiURL = url;
/*     */   }
/*     */ 
/*     */   public void doAction()
/*     */   {
/*  53 */     GreedContext.setGreedProperties(GreedProperties.getInstance(this.accountName, this.ticket, this.instanceId));
/*     */ 
/*  55 */     SwingUtilities.invokeLater(new Runnable()
/*     */     {
/*     */       public void run() {
/*  58 */         PlatformInitUtils.firstSigletonMapInit();
/*  59 */         PlatformInitAction.this.doUpdateGUI();
/*     */       }
/*     */     });
/*     */   }
/*     */ 
/*     */   private void doUpdateGUI() {
/*  66 */     if (!"api".equals(GreedContext.getStringProperty("news.source"))) {
/*  67 */       ((NewsAdapter)GreedContext.get("newsAdapter")).initHttpFeed();
/*     */     }
/*     */ 
/*  70 */     GreedConnectionUtils.resolveDnsAheadOfTheTime();
/*     */ 
/*  72 */     this.transportClient = ((GreedTransportClient)GreedContext.get("transportClient"));
/*  73 */     this.clientListener = ((GreedClientListener)GreedContext.get("clientListener"));
/*     */ 
/*  75 */     configureTransport();
/*     */ 
/*  79 */     int semicolonIndex = this.apiURL.indexOf(58);
/*     */     int port;
/*     */     String host;
/*     */     int port;
/*  80 */     if (semicolonIndex != -1) {
/*  81 */       String host = this.apiURL.substring(0, semicolonIndex);
/*     */       int port;
/*  82 */       if (semicolonIndex + 1 >= this.apiURL.length()) {
/*  83 */         LOGGER.warn("Port is not set, using default 443");
/*  84 */         port = 443;
/*     */       } else {
/*  86 */         port = Integer.parseInt(this.apiURL.substring(semicolonIndex + 1));
/*     */       }
/*     */     } else {
/*  89 */       host = this.apiURL;
/*  90 */       port = 443;
/*     */     }
/*     */ 
/*  93 */     this.transportClient.setAddress(new InetSocketAddress(host, port));
/*     */ 
/*  95 */     GreedContext.setConfig("TICKET", this.ticket);
/*  96 */     FeedDataProvider.setPlatformTicket(this.ticket);
/*     */ 
/*  98 */     this.transportClient.setPasswordTicket(this.ticket);
/*     */     try
/*     */     {
/* 101 */       this.transportClient.connect();
/*     */     } catch (Throwable e) {
/* 103 */       LOGGER.error("Error while connecting", e);
/*     */     }
/* 105 */     this.clientListener.connect();
/*     */ 
/* 107 */     if ((this.loginPanel == null) || (this.loginPanel.getMessagePanel() == null) || (this.loginPanel.getProgressBar() == null)) {
/* 108 */       return;
/*     */     }
/*     */ 
/* 111 */     this.loginPanel.getMessagePanel().postMessage("Connecting to " + GuiUtilsAndConstants.LABEL_SHORT_NAME + " Platform...");
/* 112 */     this.loginPanel.getProgressBar().setIndeterminate(true);
/*     */ 
/* 114 */     if ((GreedContext.IS_EU_LIVE) && (EuLiveDisclaimerDialog.getInstance().isAccepted())) {
/* 115 */       Notification notification = new Notification(LocalizationManager.getTextWithArgumentKeys("disclaimer.eu.live.notif.message", new Object[] { "disclaimer.eu.live.revision.version" }));
/*     */ 
/* 118 */       PostMessageAction post = new PostMessageAction(this, notification);
/* 119 */       GreedContext.publishEvent(post);
/*     */     }
/*     */   }
/*     */ 
/*     */   private void configureTransport() {
/* 124 */     this.transportClient.setUsername(this.accountName);
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.actions.PlatformInitAction
 * JD-Core Version:    0.6.0
 */