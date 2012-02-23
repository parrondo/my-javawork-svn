/*    */ package com.dukascopy.dds2.greed.gui.component;
/*    */ 
/*    */ import com.dukascopy.dds2.greed.GreedContext;
/*    */ import com.dukascopy.dds2.greed.actions.FullDepthInstrumentSubscribeAction;
/*    */ import com.dukascopy.dds2.greed.gui.ClientFormLayoutManager;
/*    */ import com.dukascopy.dds2.greed.gui.DealPanel;
/*    */ import com.dukascopy.dds2.greed.gui.helpers.IWorkspaceHelper;
/*    */ import com.dukascopy.dds2.greed.model.AccountStatement;
/*    */ import com.dukascopy.transport.common.msg.request.AccountInfoMessage;
/*    */ import com.dukascopy.transport.common.msg.response.InstrumentStatusUpdateMessage;
/*    */ import java.util.List;
/*    */ import javax.swing.JPanel;
/*    */ 
/*    */ public abstract class WorkspacePanel extends JPanel
/*    */ {
/*    */   protected final DealPanel dealPanel;
/* 16 */   private boolean instrumentsLoaded = false;
/*    */ 
/*    */   protected WorkspacePanel(DealPanel dealPanel) {
/* 19 */     this.dealPanel = dealPanel;
/*    */   }
/*    */ 
/*    */   public final void setInitialInstruments() {
/* 23 */     AccountStatement accountStatement = (AccountStatement)GreedContext.get("accountStatement");
/*    */ 
/* 25 */     if (accountStatement == null) return;
/* 26 */     if (accountStatement.getLastAccountState() == null) return;
/*    */ 
/* 28 */     if ("YES".equals(accountStatement.getLastAccountState().opt("DUMMY"))) {
/* 29 */       return;
/*    */     }
/* 31 */     if (this.instrumentsLoaded) {
/* 32 */       return;
/*    */     }
/*    */ 
/* 35 */     this.instrumentsLoaded = true;
/* 36 */     ((ClientFormLayoutManager)GreedContext.get("layoutManager")).getWorkspaceHelper().populateWorkspace();
/*    */ 
/* 38 */     FullDepthInstrumentSubscribeAction action = new FullDepthInstrumentSubscribeAction(this);
/* 39 */     GreedContext.publishEvent(action);
/*    */   }
/*    */ 
/*    */   public abstract void setInstruments(List<String> paramList);
/*    */ 
/*    */   public abstract List<String> getInstruments();
/*    */ 
/*    */   public abstract String getSelectedInstrument();
/*    */ 
/*    */   public abstract String[] getSelectedInstruments();
/*    */ 
/*    */   public abstract void updateTradability(InstrumentStatusUpdateMessage paramInstrumentStatusUpdateMessage);
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.WorkspacePanel
 * JD-Core Version:    0.6.0
 */