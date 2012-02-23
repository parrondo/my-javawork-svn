/*    */ package com.dukascopy.dds2.greed.gui.component.strategy.tab.actions;
/*    */ 
/*    */ import com.dukascopy.api.impl.StrategyMessages;
/*    */ import com.dukascopy.dds2.greed.GreedContext;
/*    */ import com.dukascopy.dds2.greed.actions.AppActionEvent;
/*    */ import com.dukascopy.dds2.greed.agent.Strategies;
/*    */ import com.dukascopy.dds2.greed.connection.GreedTransportClient;
/*    */ import com.dukascopy.dds2.greed.gui.JForexClientFormLayoutManager;
/*    */ import com.dukascopy.dds2.greed.gui.component.strategy.tab.mediator.RemoteStrategiesUtil;
/*    */ import com.dukascopy.dds2.greed.gui.component.strategy.tab.mediator.StrategyNewBean;
/*    */ import com.dukascopy.dds2.greed.gui.l10n.LocalizationManager;
/*    */ import com.dukascopy.dds2.greed.util.ObjectUtils;
/*    */ import com.dukascopy.transport.common.msg.ProtocolMessage;
/*    */ import com.dukascopy.transport.common.msg.response.ErrorResponseMessage;
/*    */ import com.dukascopy.transport.common.msg.strategy.StrategyUpdateRequestMessage;
/*    */ import java.beans.PropertyChangeEvent;
/*    */ import java.util.Collection;
/*    */ import javax.swing.JOptionPane;
/*    */ 
/*    */ public class ChangeRemoteStrategyParamsAppAction extends AppActionEvent
/*    */ {
/*    */   private StrategyNewBean strategy;
/*    */   private String errorMessageKey;
/*    */   private Collection<PropertyChangeEvent> propertyChangeEvents;
/*    */ 
/*    */   public ChangeRemoteStrategyParamsAppAction(Object source, StrategyNewBean strategy)
/*    */   {
/* 35 */     super(source, false, true);
/* 36 */     this.strategy = strategy;
/*    */   }
/*    */ 
/*    */   public void doAction()
/*    */   {
/* 42 */     this.errorMessageKey = null;
/*    */ 
/* 44 */     this.propertyChangeEvents = RemoteStrategiesUtil.getPropertyChangeEvents(this.strategy);
/*    */ 
/* 46 */     StrategyUpdateRequestMessage message = new StrategyUpdateRequestMessage();
/* 47 */     message.setPid(this.strategy.getRemoteProcessId());
/*    */ 
/* 49 */     Collection newParameters = RemoteStrategiesUtil.getConvertedParams(this.strategy);
/* 50 */     message.setParameters(newParameters);
/*    */ 
/* 52 */     GreedTransportClient transport = (GreedTransportClient)GreedContext.get("transportClient");
/* 53 */     ProtocolMessage response = transport.controlRequest(message);
/* 54 */     if ((response instanceof ErrorResponseMessage))
/* 55 */       this.errorMessageKey = "Error while changing parameters of remotely running strategy.";
/*    */   }
/*    */ 
/*    */   public void updateGuiAfter()
/*    */   {
/* 62 */     if (this.errorMessageKey == null) {
/* 63 */       if (!ObjectUtils.isNullOrEmpty(this.propertyChangeEvents)) {
/* 64 */         for (PropertyChangeEvent propertyChangeEvent : this.propertyChangeEvents) {
/* 65 */           Strategies.get().fireConfigurationPropertyChange(this.strategy.getRunningProcessId(), propertyChangeEvent);
/*    */         }
/*    */       }
/* 68 */       StrategyMessages.strategyIsModified(true, this.strategy.getName(), this.strategy.getStrategy());
/*    */     } else {
/* 70 */       JForexClientFormLayoutManager clientFormLayoutManager = (JForexClientFormLayoutManager)GreedContext.get("layoutManager");
/*    */ 
/* 73 */       String message = LocalizationManager.getTextWithArgumentKeys(this.errorMessageKey, new Object[0]);
/* 74 */       String title = LocalizationManager.getText("error.title");
/* 75 */       JOptionPane.showMessageDialog(clientFormLayoutManager.getStrategiesPanel(), message, title, 0);
/*    */     }
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.strategy.tab.actions.ChangeRemoteStrategyParamsAppAction
 * JD-Core Version:    0.6.0
 */