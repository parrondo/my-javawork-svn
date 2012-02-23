/*    */ package com.dukascopy.dds2.greed.actions;
/*    */ 
/*    */ import com.dukascopy.dds2.greed.gui.component.strategy.tab.StrategiesContentPane;
/*    */ import com.dukascopy.dds2.greed.gui.l10n.LocalizationManager;
/*    */ import com.dukascopy.transport.common.msg.strategy.StrategyRunErrorResponseMessage;
/*    */ import javax.swing.JOptionPane;
/*    */ 
/*    */ public class RemoteStrategyRunErrorResponseAction extends AbstractRemoteStrategyAction
/*    */ {
/*    */   private static final long serialVersionUID = 244743177317860104L;
/*    */   private StrategyRunErrorResponseMessage errorMessage;
/*    */ 
/*    */   public RemoteStrategyRunErrorResponseAction(Object source, StrategyRunErrorResponseMessage errorMessage)
/*    */   {
/* 33 */     super(source);
/* 34 */     this.errorMessage = errorMessage;
/*    */   }
/*    */ 
/*    */   public void updateGuiAfter()
/*    */   {
/* 42 */     StrategiesContentPane strategiesContentPane = getStrategiesContentPane();
/* 43 */     if (strategiesContentPane != null) {
/* 44 */       strategiesContentPane.onRemoteStrategyRunErrorResponse(this.errorMessage);
/* 45 */       JOptionPane.showMessageDialog(strategiesContentPane, LocalizationManager.getText("joption.pane.error.starting.remote.strategy"), LocalizationManager.getText("joption.pane.error"), 0);
/*    */     }
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.actions.RemoteStrategyRunErrorResponseAction
 * JD-Core Version:    0.6.0
 */