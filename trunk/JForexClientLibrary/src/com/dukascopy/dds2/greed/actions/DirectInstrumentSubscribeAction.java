/*    */ package com.dukascopy.dds2.greed.actions;
/*    */ 
/*    */ import com.dukascopy.charts.data.datacache.FeedDataProvider;
/*    */ import com.dukascopy.dds2.greed.GreedContext;
/*    */ import com.dukascopy.dds2.greed.connection.GreedTransportClient;
/*    */ import com.dukascopy.dds2.greed.gui.ClientForm;
/*    */ import com.dukascopy.dds2.greed.gui.DealPanel;
/*    */ import com.dukascopy.dds2.greed.gui.component.WorkspacePanel;
/*    */ import com.dukascopy.dds2.greed.gui.settings.ClientSettingsStorage;
/*    */ import com.dukascopy.transport.common.msg.request.QuoteSubscribeRequestMessage;
/*    */ import java.util.ArrayList;
/*    */ import java.util.HashSet;
/*    */ import java.util.Set;
/*    */ import org.slf4j.Logger;
/*    */ import org.slf4j.LoggerFactory;
/*    */ 
/*    */ public class DirectInstrumentSubscribeAction extends AppActionEvent
/*    */ {
/* 19 */   private static final Logger LOGGER = LoggerFactory.getLogger(DirectInstrumentSubscribeAction.class);
/*    */   private final Set<String> subscribeInstrumentSet;
/*    */ 
/*    */   public DirectInstrumentSubscribeAction(Object source, Set<String> newInstrumentList)
/*    */   {
/* 24 */     super(source, false, true);
/* 25 */     this.subscribeInstrumentSet = newInstrumentList;
/*    */   }
/*    */ 
/*    */   public void doAction()
/*    */   {
/* 31 */     GreedTransportClient transport = (GreedTransportClient)GreedContext.get("transportClient");
/*    */ 
/* 33 */     if (this.subscribeInstrumentSet.size() > 0) {
/* 34 */       QuoteSubscribeRequestMessage subscribe = new QuoteSubscribeRequestMessage();
/* 35 */       ArrayList subscribeInstrumentList = new ArrayList(this.subscribeInstrumentSet);
/* 36 */       subscribe.setQuotesOnly(Boolean.valueOf(true));
/* 37 */       subscribe.setInstruments(subscribeInstrumentList);
/*    */ 
/* 39 */       transport.controlRequest(subscribe);
/*    */ 
/* 41 */       if (LOGGER.isDebugEnabled())
/* 42 */         LOGGER.debug("Subscribe to " + this.subscribeInstrumentSet);
/*    */     }
/*    */   }
/*    */ 
/*    */   public void updateGuiAfter()
/*    */   {
/* 49 */     FeedDataProvider feedDataProvider = (FeedDataProvider)GreedContext.get("feedDataProvider");
/*    */ 
/* 51 */     if (feedDataProvider != null) {
/* 52 */       feedDataProvider.addInstrumentNamesSubscribed(this.subscribeInstrumentSet);
/*    */     }
/*    */ 
/* 56 */     ClientForm clientGui = (ClientForm)GreedContext.get("clientGui");
/* 57 */     ClientSettingsStorage clientSettingsStorage = (ClientSettingsStorage)GreedContext.get("settingsStorage");
/*    */ 
/* 59 */     Set unionInstrumentSet = new HashSet(clientSettingsStorage.restoreSelectedInstruments());
/* 60 */     unionInstrumentSet.addAll(this.subscribeInstrumentSet);
/*    */ 
/* 62 */     clientSettingsStorage.saveSelectedInstruments(unionInstrumentSet);
/* 63 */     clientGui.refresh();
/*    */ 
/* 65 */     WorkspacePanel workspacePanel = clientGui.getDealPanel().getWorkspacePanel();
/*    */ 
/* 67 */     if (LOGGER.isDebugEnabled()) {
/* 68 */       LOGGER.debug("WorkspacePanel set instruments : " + unionInstrumentSet);
/*    */     }
/*    */ 
/* 71 */     workspacePanel.setInstruments(new ArrayList(unionInstrumentSet));
/*    */ 
/* 73 */     if (LOGGER.isDebugEnabled())
/* 74 */       LOGGER.debug("WorkspacePanel instruments after set : " + workspacePanel.getInstruments());
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.actions.DirectInstrumentSubscribeAction
 * JD-Core Version:    0.6.0
 */