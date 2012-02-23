/*     */ package com.dukascopy.dds2.greed.actions;
/*     */ 
/*     */ import com.dukascopy.charts.data.datacache.FeedDataProvider;
/*     */ import com.dukascopy.dds2.greed.GreedContext;
/*     */ import com.dukascopy.dds2.greed.agent.Strategies;
/*     */ import com.dukascopy.dds2.greed.connection.GreedTransportClient;
/*     */ import com.dukascopy.dds2.greed.gui.ClientForm;
/*     */ import com.dukascopy.dds2.greed.gui.DealPanel;
/*     */ import com.dukascopy.dds2.greed.gui.JForexClientFormLayoutManager;
/*     */ import com.dukascopy.dds2.greed.gui.LoginForm;
/*     */ import com.dukascopy.dds2.greed.gui.component.LoginPanel;
/*     */ import com.dukascopy.dds2.greed.gui.component.message.MessagePanel;
/*     */ import com.dukascopy.dds2.greed.gui.component.strategy.StrategyTestPanel;
/*     */ import com.dukascopy.dds2.greed.model.Notification;
/*     */ import com.dukascopy.dds2.greed.util.PlatformInitUtils;
/*     */ import java.util.List;
/*     */ import javax.swing.Action;
/*     */ import javax.swing.JButton;
/*     */ import javax.swing.JCheckBox;
/*     */ import javax.swing.JTextField;
/*     */ import org.slf4j.Logger;
/*     */ import org.slf4j.LoggerFactory;
/*     */ 
/*     */ public class DisconnectAction extends AppActionEvent
/*     */ {
/*  25 */   private static final Logger LOGGER = LoggerFactory.getLogger(DisconnectAction.class);
/*     */ 
/*  27 */   private boolean networkFailure = false;
/*     */ 
/*  29 */   public DisconnectAction(Object source) { super(source, false, true); }
/*     */ 
/*     */   public DisconnectAction(Object source, boolean networkFailure)
/*     */   {
/*  33 */     this(source);
/*  34 */     this.networkFailure = networkFailure;
/*     */   }
/*     */ 
/*     */   public void doAction()
/*     */   {
/*  41 */     if (LOGGER.isDebugEnabled()) {
/*  42 */       LOGGER.warn("!!!! DISCONNECTING...");
/*     */     }
/*     */ 
/*  46 */     GreedTransportClient client = (GreedTransportClient)GreedContext.get("transportClient");
/*  47 */     if (client != null) client.disconnect();
/*     */ 
/*  49 */     closeAllConnections();
/*     */     try
/*     */     {
/*  52 */       GreedContext.resetDataFeedProvider();
/*     */     } catch (Exception e) {
/*  54 */       LOGGER.error("Data Feed Provider was not initialized. Message: " + e.getMessage(), e);
/*     */     }
/*     */ 
/*  57 */     PlatformInitUtils.cleanAll();
/*  58 */     PlatformInitUtils.reinitStaticValues();
/*     */ 
/*  60 */     GreedContext.setFirstAccountInfo(true);
/*     */   }
/*     */ 
/*     */   public void updateGuiAfter()
/*     */   {
/*  69 */     ClientForm gui = (ClientForm)GreedContext.get("clientGui");
/*  70 */     LoginForm loginForm = LoginForm.getInstance();
/*     */ 
/*  72 */     if (loginForm == null) {
/*  73 */       return;
/*     */     }
/*     */ 
/*  76 */     loginForm.display();
/*  77 */     loginForm.getLoginPanel().getLoginButton().getAction().setEnabled(true);
/*  78 */     loginForm.getLoginPanel().getExitButton().getAction().setEnabled(true);
/*     */ 
/*  80 */     prepareLoginPanel();
/*     */ 
/*  82 */     if (LOGGER.isDebugEnabled()) {
/*  83 */       LOGGER.debug("CLEARING ...");
/*     */     }
/*     */ 
/*  86 */     if (this.networkFailure) {
/*  87 */       Notification notification = new Notification("Network failure. Please relogin.");
/*  88 */       notification.setPriority("ERROR");
/*  89 */       loginForm.getLoginPanel().getMessagePanel().postMessage(notification);
/*     */     }
/*     */ 
/*  92 */     if (gui != null) {
/*  93 */       gui.setVisible(false);
/*     */ 
/*  96 */       gui.clearMessageLog();
/*  97 */       gui.clearTesterMessages();
/*     */ 
/*  99 */       gui.getDealPanel().clear();
/*     */     }
/*     */ 
/* 105 */     PlatformInitUtils.reinitStaticValues();
/*     */ 
/* 107 */     loginForm.setVisible(true);
/*     */   }
/*     */ 
/*     */   private void prepareLoginPanel() {
/* 111 */     LoginForm loginForm = LoginForm.getInstance();
/* 112 */     LoginPanel loginPanel = loginForm.getLoginPanel();
/* 113 */     loginPanel.getAccountNameField().setText("");
/* 114 */     loginPanel.getPasswordField().setText("");
/* 115 */     loginPanel.getSecurePinCheckBox().setSelected(false);
/* 116 */     loginPanel.getLoginButton().requestFocus();
/*     */   }
/*     */ 
/*     */   private void closeAllConnections() {
/* 120 */     if (GreedContext.isStrategyAllowed()) {
/* 121 */       doPrepareJForexClientForLogout();
/*     */     }
/*     */ 
/* 124 */     FeedDataProvider feedDataProvider = (FeedDataProvider)GreedContext.get("feedDataProvider");
/* 125 */     if (feedDataProvider != null)
/* 126 */       feedDataProvider.close();
/*     */   }
/*     */ 
/*     */   private void doPrepareJForexClientForLogout()
/*     */   {
/* 131 */     Strategies.get().stopAll();
/*     */ 
/* 133 */     JForexClientFormLayoutManager layout = (JForexClientFormLayoutManager)GreedContext.get("layoutManager");
/* 134 */     if (layout == null) return;
/*     */ 
/* 136 */     List panels = layout.getStrategyTestPanels();
/* 137 */     for (StrategyTestPanel strategyTestPanel : panels)
/* 138 */       strategyTestPanel.cancel();
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.actions.DisconnectAction
 * JD-Core Version:    0.6.0
 */