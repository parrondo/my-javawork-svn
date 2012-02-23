/*    */ package com.dukascopy.dds2.greed.gui.component.strategy.tab.actions;
/*    */ 
/*    */ import com.dukascopy.api.impl.StrategyMessages;
/*    */ import com.dukascopy.dds2.greed.GreedContext;
/*    */ import com.dukascopy.dds2.greed.actions.AppActionEvent;
/*    */ import com.dukascopy.dds2.greed.connection.GreedTransportClient;
/*    */ import com.dukascopy.dds2.greed.gui.JForexClientFormLayoutManager;
/*    */ import com.dukascopy.dds2.greed.gui.component.strategy.tab.mediator.StrategyNewBean;
/*    */ import com.dukascopy.dds2.greed.gui.l10n.LocalizationManager;
/*    */ import com.dukascopy.transport.common.msg.ProtocolMessage;
/*    */ import com.dukascopy.transport.common.msg.response.ErrorResponseMessage;
/*    */ import com.dukascopy.transport.common.msg.strategy.StrategyStopRequestMessage;
/*    */ import javax.swing.JOptionPane;
/*    */ import org.slf4j.Logger;
/*    */ import org.slf4j.LoggerFactory;
/*    */ 
/*    */ public class StopRemoteStrategyAppAction extends AppActionEvent
/*    */ {
/* 26 */   private static final Logger LOGGER = LoggerFactory.getLogger(StopRemoteStrategyAppAction.class);
/*    */   private StrategyNewBean strategy;
/*    */   private String errorMessageKey;
/*    */ 
/*    */   public StopRemoteStrategyAppAction(Object source, StrategyNewBean strategy)
/*    */   {
/* 33 */     super(source, true, true);
/* 34 */     this.strategy = strategy;
/*    */   }
/*    */ 
/*    */   public void updateGuiBefore()
/*    */   {
/* 39 */     StrategyMessages.stoppingStrategy(true, this.strategy.getName());
/*    */   }
/*    */ 
/*    */   public void doAction()
/*    */   {
/* 44 */     this.errorMessageKey = null;
/*    */ 
/* 46 */     StrategyStopRequestMessage request = new StrategyStopRequestMessage();
/* 47 */     request.setPid(this.strategy.getRemoteProcessId());
/*    */ 
/* 49 */     GreedTransportClient transport = (GreedTransportClient)GreedContext.get("transportClient");
/*    */ 
/* 52 */     ProtocolMessage response = transport.controlRequest(request);
/* 53 */     if ((response instanceof ErrorResponseMessage)) {
/* 54 */       LOGGER.error("Cannot stop remotely running strategy. " + ((ErrorResponseMessage)response).getReason());
/* 55 */       this.errorMessageKey = "joption.pane.error.stop.remote.strategy";
/*    */     }
/*    */   }
/*    */ 
/*    */   public void updateGuiAfter()
/*    */   {
/* 61 */     if (this.errorMessageKey != null)
/*    */     {
/* 63 */       JForexClientFormLayoutManager clientFormLayoutManager = (JForexClientFormLayoutManager)GreedContext.get("layoutManager");
/*    */ 
/* 66 */       JOptionPane.showMessageDialog(clientFormLayoutManager.getStrategiesPanel(), LocalizationManager.getText(this.errorMessageKey), LocalizationManager.getText("joption.pane.error"), 1);
/*    */     }
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.strategy.tab.actions.StopRemoteStrategyAppAction
 * JD-Core Version:    0.6.0
 */