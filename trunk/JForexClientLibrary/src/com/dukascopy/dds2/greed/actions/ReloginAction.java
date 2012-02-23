/*     */ package com.dukascopy.dds2.greed.actions;
/*     */ 
/*     */ import com.dukascopy.charts.data.datacache.FeedDataProvider;
/*     */ import com.dukascopy.dds2.greed.GreedContext;
/*     */ import com.dukascopy.dds2.greed.connection.GreedTransportClient;
/*     */ import com.dukascopy.dds2.greed.gui.ClientForm;
/*     */ import com.dukascopy.dds2.greed.gui.DealPanel;
/*     */ import com.dukascopy.dds2.greed.gui.component.WorkspacePanel;
/*     */ import com.dukascopy.dds2.greed.gui.component.menu.MainMenu;
/*     */ import com.dukascopy.dds2.greed.gui.component.orders.OrderEntryPanel;
/*     */ import com.dukascopy.dds2.greed.model.CurrencyMarketWrapper;
/*     */ import com.dukascopy.dds2.greed.model.MarketView;
/*     */ import com.dukascopy.dds2.greed.model.Notification;
/*     */ import com.dukascopy.transport.common.model.type.OrderSide;
/*     */ import com.dukascopy.transport.common.msg.ProtocolMessage;
/*     */ import com.dukascopy.transport.common.msg.request.CurrencyMarket;
/*     */ import com.dukascopy.transport.common.msg.request.InitRequestMessage;
/*     */ import com.dukascopy.transport.common.msg.request.QuoteSubscribeRequestMessage;
/*     */ import com.dukascopy.transport.common.msg.request.QuoteUnsubscribeRequestMessage;
/*     */ import java.lang.reflect.InvocationTargetException;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Calendar;
/*     */ import java.util.List;
/*     */ import javax.swing.JMenuItem;
/*     */ import javax.swing.SwingUtilities;
/*     */ import org.slf4j.Logger;
/*     */ import org.slf4j.LoggerFactory;
/*     */ 
/*     */ public class ReloginAction extends AppActionEvent
/*     */ {
/*  34 */   private static final Logger LOGGER = LoggerFactory.getLogger(ReloginAction.class);
/*     */   private ClientForm gui;
/*     */ 
/*     */   public ReloginAction(Object source)
/*     */   {
/*  39 */     super(source, true, true);
/*  40 */     this.source = source;
/*  41 */     this.gui = ((ClientForm)GreedContext.get("clientGui"));
/*     */   }
/*     */ 
/*     */   public void doAction()
/*     */   {
/*  46 */     GreedTransportClient transport = (GreedTransportClient)GreedContext.get("transportClient");
/*     */ 
/*  48 */     GreedContext.setConfig("logoff", Boolean.valueOf(true));
/*     */ 
/*  50 */     transport.disconnect();
/*     */ 
/*  52 */     Notification notification = new Notification(Calendar.getInstance().getTime(), "Reconnecting...");
/*  53 */     PostMessageAction post = new PostMessageAction(this, notification);
/*  54 */     GreedContext.publishEvent(post);
/*     */     try
/*     */     {
/*  57 */       Thread.sleep(3000L);
/*     */     } catch (InterruptedException ie) {
/*     */     }
/*  60 */     transport.connect();
/*     */ 
/*  64 */     if (transport.isOnline())
/*     */     {
/*  66 */       List instruments = new ArrayList();
/*  67 */       instruments.add("ALL");
/*     */ 
/*  69 */       QuoteUnsubscribeRequestMessage unsubscribe = new QuoteUnsubscribeRequestMessage();
/*  70 */       unsubscribe.setInstruments(instruments);
/*  71 */       ProtocolMessage response = transport.controlRequest(unsubscribe);
/*     */ 
/*  73 */       QuoteSubscribeRequestMessage subscribeRequest = new QuoteSubscribeRequestMessage();
/*     */       try
/*     */       {
/*  76 */         SwingUtilities.invokeAndWait(new Runnable(subscribeRequest, instruments) {
/*     */           public void run() {
/*  78 */             WorkspacePanel tickerPanel = ((ClientForm)GreedContext.get("clientGui")).getDealPanel().getWorkspacePanel();
/*  79 */             if ((tickerPanel == null) || (tickerPanel.getInstruments() == null) || (tickerPanel.getInstruments().size() == 0))
/*     */             {
/*  82 */               this.val$subscribeRequest.setInstruments(this.val$instruments);
/*     */             }
/*  84 */             else this.val$subscribeRequest.setInstruments(tickerPanel.getInstruments()); 
/*     */           }
/*     */         });
/*     */       }
/*     */       catch (InterruptedException e) {
/*  89 */         LOGGER.error(e.getMessage(), e);
/*     */       } catch (InvocationTargetException e) {
/*  91 */         LOGGER.error(e.getMessage(), e);
/*     */       }
/*     */ 
/*  94 */       subscribeRequest.setQuotesOnly(Boolean.valueOf(true));
/*  95 */       LOGGER.debug("Initial subscribing to instruments: " + subscribeRequest.getInstruments());
/*  96 */       transport.controlRequest(subscribeRequest);
/*     */ 
/*  99 */       FeedDataProvider feedDataProvider = (FeedDataProvider)GreedContext.get("feedDataProvider");
/*     */ 
/* 101 */       if (feedDataProvider != null) {
/* 102 */         feedDataProvider.setInstrumentNamesSubscribed(subscribeRequest.getInstruments());
/*     */       }
/*     */ 
/* 106 */       InitRequestMessage initRequest = new InitRequestMessage();
/* 107 */       response = transport.controlRequest(initRequest);
/*     */ 
/* 109 */       enableRecconectMenuItem(true);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void updateGuiAfter()
/*     */   {
/* 115 */     this.gui.clearOrderModels();
/*     */ 
/* 117 */     String CUR_PRIM = "EUR";
/* 118 */     String CUR_SECD = "USD";
/* 119 */     String defaultInstriment = "EUR" + "/" + "USD";
/* 120 */     OrderEntryPanel oep = this.gui.getDealPanel().getOrderEntryPanel();
/* 121 */     if ((this.gui.getDealPanel().getSelectedInstrument() != null) && (!this.gui.getDealPanel().getSelectedInstrument().isEmpty()))
/* 122 */       oep.setInstrument(this.gui.getDealPanel().getSelectedInstrument());
/*     */     else {
/* 124 */       oep.setInstrument(defaultInstriment);
/*     */     }
/*     */ 
/* 127 */     List emptyList = new ArrayList();
/* 128 */     CurrencyMarket market = new CurrencyMarket("EUR", "USD", emptyList, emptyList);
/* 129 */     this.gui.getDealPanel().onMarketState(CurrencyMarketWrapper.valueOf(market));
/* 130 */     MarketView marketView = (MarketView)GreedContext.get("marketView");
/* 131 */     oep.setSubmitEnabled(0);
/* 132 */     oep.setOrderSide(OrderSide.BUY);
/* 133 */     oep.setDefaultStopConditionLabels();
/* 134 */     if (null != marketView.getLastMarketState(defaultInstriment))
/* 135 */       oep.onMarketState(marketView.getLastMarketState(defaultInstriment));
/*     */   }
/*     */ 
/*     */   public void updateGuiBefore()
/*     */   {
/* 140 */     enableRecconectMenuItem(false);
/*     */ 
/* 142 */     ReconnectInfoAction reconnectInfo = new ReconnectInfoAction(this);
/* 143 */     GreedContext.publishEvent(reconnectInfo);
/*     */   }
/*     */ 
/*     */   private void enableRecconectMenuItem(boolean enabled) {
/* 147 */     ClientForm clientGui = (ClientForm)GreedContext.get("clientGui");
/* 148 */     clientGui.getMainMenu().getReconnect().setEnabled(enabled);
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.actions.ReloginAction
 * JD-Core Version:    0.6.0
 */