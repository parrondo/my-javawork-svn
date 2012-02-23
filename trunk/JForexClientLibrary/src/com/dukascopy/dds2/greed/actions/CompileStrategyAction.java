/*    */ package com.dukascopy.dds2.greed.actions;
/*    */ 
/*    */ import com.dukascopy.api.impl.ServiceWrapper;
/*    */ import com.dukascopy.api.impl.StrategyWrapper;
/*    */ import com.dukascopy.dds2.greed.GreedContext;
/*    */ import com.dukascopy.dds2.greed.gui.JForexClientFormLayoutManager;
/*    */ import com.dukascopy.dds2.greed.gui.component.strategy.StrategyTestPanel;
/*    */ import com.dukascopy.dds2.greed.gui.component.strategy.tab.mediator.StrategyNewBean;
/*    */ import com.dukascopy.dds2.greed.gui.settings.ClientSettingsStorage;
/*    */ import com.dukascopy.dds2.greed.util.CompilerUtils;
/*    */ import java.util.List;
/*    */ 
/*    */ public class CompileStrategyAction extends AppActionEvent
/*    */ {
/* 17 */   private ServiceWrapper strategyWraper = null;
/* 18 */   private StrategyNewBean strategy = null;
/*    */ 
/* 20 */   private AppActionEvent afterAction = null;
/* 21 */   private boolean success = false;
/*    */ 
/*    */   public CompileStrategyAction(Object source, StrategyNewBean strategy) {
/* 24 */     this(source, strategy, null);
/*    */   }
/*    */ 
/*    */   public CompileStrategyAction(Object source, StrategyNewBean strategy, AppActionEvent afterAction) {
/* 28 */     super(source, false, true);
/*    */ 
/* 30 */     if (strategy == null) throw new NullPointerException("Strategy cannot be null");
/*    */ 
/* 32 */     this.strategy = strategy;
/* 33 */     this.afterAction = afterAction;
/*    */ 
/* 35 */     this.strategyWraper = new StrategyWrapper();
/* 36 */     this.strategyWraper.setBinaryFile(strategy.getStrategyBinaryFile());
/* 37 */     this.strategyWraper.setSourceFile(strategy.getStrategySourceFile());
/* 38 */     this.strategyWraper.setNewUnsaved(false);
/*    */   }
/*    */ 
/*    */   public void doAction()
/*    */   {
/* 43 */     this.success = CompilerUtils.getInstance().runCompilation(this.strategyWraper);
/*    */   }
/*    */ 
/*    */   public void updateGuiAfter()
/*    */   {
/* 49 */     if (this.success) {
/* 50 */       if (this.afterAction != null) {
/* 51 */         this.afterAction.doAction();
/*    */       }
/*    */ 
/* 54 */       if ((this.strategyWraper instanceof StrategyWrapper)) {
/* 55 */         this.strategy.setStrategyBinaryFile(this.strategyWraper.getBinaryFile());
/*    */ 
/* 57 */         ClientSettingsStorage storage = (ClientSettingsStorage)GreedContext.get("settingsStorage");
/* 58 */         storage.saveStrategyNewBean(this.strategy);
/*    */ 
/* 60 */         JForexClientFormLayoutManager layoutManager = (JForexClientFormLayoutManager)GreedContext.get("layoutManager");
/*    */ 
/* 62 */         List testPanels = layoutManager.getStrategyTestPanels();
/* 63 */         for (StrategyTestPanel testPanel : testPanels)
/* 64 */           testPanel.reloadStrategies();
/*    */       }
/*    */     }
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.actions.CompileStrategyAction
 * JD-Core Version:    0.6.0
 */