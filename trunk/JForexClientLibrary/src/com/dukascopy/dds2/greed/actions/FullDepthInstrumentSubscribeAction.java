/*    */ package com.dukascopy.dds2.greed.actions;
/*    */ 
/*    */ import com.dukascopy.api.Instrument;
/*    */ import com.dukascopy.dds2.greed.GreedContext;
/*    */ import com.dukascopy.dds2.greed.agent.Strategies;
/*    */ import com.dukascopy.dds2.greed.connection.GreedTransportClient;
/*    */ import com.dukascopy.dds2.greed.gui.ClientForm;
/*    */ import com.dukascopy.transport.common.msg.request.QuoteSubscribeRequestMessage;
/*    */ import java.util.ArrayList;
/*    */ import java.util.List;
/*    */ import java.util.Set;
/*    */ import org.slf4j.Logger;
/*    */ import org.slf4j.LoggerFactory;
/*    */ 
/*    */ public class FullDepthInstrumentSubscribeAction extends AppActionEvent
/*    */ {
/* 24 */   private static final Logger LOGGER = LoggerFactory.getLogger(FullDepthInstrumentSubscribeAction.class);
/*    */   private Set<String> instruments;
/*    */ 
/*    */   public FullDepthInstrumentSubscribeAction(Object source)
/*    */   {
/* 29 */     super(source, true, false);
/*    */   }
/*    */ 
/*    */   public void updateGuiBefore()
/*    */   {
/* 34 */     this.instruments = ((ClientForm)GreedContext.get("clientGui")).getFullDepthInstruments();
/*    */   }
/*    */ 
/*    */   public void doAction()
/*    */   {
/* 39 */     GreedTransportClient transport = (GreedTransportClient)GreedContext.get("transportClient");
/*    */ 
/* 41 */     Set strategyInstruments = Strategies.get().getRequiredInstruments();
/* 42 */     for (Instrument instrument : strategyInstruments) {
/* 43 */       this.instruments.add(instrument.toString());
/*    */     }
/*    */ 
/* 47 */     QuoteSubscribeRequestMessage subscribe = new QuoteSubscribeRequestMessage();
/* 48 */     List subscribeInstrumentList = new ArrayList(this.instruments);
/* 49 */     subscribe.setInstruments(subscribeInstrumentList);
/* 50 */     subscribe.setQuotesOnly(Boolean.valueOf(false));
/* 51 */     transport.controlRequest(subscribe);
/*    */ 
/* 53 */     if (LOGGER.isDebugEnabled())
/* 54 */       LOGGER.debug("Subscribing to full depth for instruments " + this.instruments);
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.actions.FullDepthInstrumentSubscribeAction
 * JD-Core Version:    0.6.0
 */