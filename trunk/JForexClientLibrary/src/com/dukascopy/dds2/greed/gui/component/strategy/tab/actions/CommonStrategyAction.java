/*    */ package com.dukascopy.dds2.greed.gui.component.strategy.tab.actions;
/*    */ 
/*    */ import com.dukascopy.dds2.greed.gui.component.chart.ButtonTabPanelForBottomPanelWithCloseButton;
/*    */ import com.dukascopy.dds2.greed.gui.component.chart.TabsAndFramesTabbedPane;
/*    */ import com.dukascopy.dds2.greed.gui.component.strategy.tab.mediator.StrategyNewBean;
/*    */ import com.dukascopy.dds2.greed.gui.component.strategy.tab.mediator.StrategyStatus;
/*    */ import com.dukascopy.dds2.greed.gui.component.strategy.tab.table.StrategiesTableModel;
/*    */ import com.dukascopy.dds2.greed.gui.component.tree.WorkspaceTreeController;
/*    */ import java.util.List;
/*    */ import org.slf4j.Logger;
/*    */ import org.slf4j.LoggerFactory;
/*    */ 
/*    */ public abstract class CommonStrategyAction
/*    */   implements IStrategyAction
/*    */ {
/* 18 */   private static final Logger LOGGER = LoggerFactory.getLogger(CommonStrategyAction.class);
/*    */   protected TabsAndFramesTabbedPane tabbedPane;
/*    */   protected StrategiesTableModel strategiesModel;
/*    */   protected WorkspaceTreeController workspaceTreeController;
/*    */ 
/*    */   protected CommonStrategyAction(StrategiesTableModel model, TabsAndFramesTabbedPane tabbedPane, WorkspaceTreeController workspaceTreeController)
/*    */   {
/* 25 */     this.strategiesModel = model;
/* 26 */     this.tabbedPane = tabbedPane;
/* 27 */     this.workspaceTreeController = workspaceTreeController;
/*    */   }
/*    */ 
/*    */   public Object execute(Object param)
/*    */   {
/*    */     try {
/* 33 */       return executeInternal(param);
/*    */     } catch (Throwable thr) {
/* 35 */       LOGGER.warn(thr.getMessage(), thr);
/*    */     }
/* 37 */     return null;
/*    */   }
/*    */ 
/*    */   protected abstract Object executeInternal(Object paramObject);
/*    */ 
/*    */   protected void updateTabClosable() {
/* 44 */     boolean isClosable = true;
/*    */ 
/* 46 */     List strategies = this.strategiesModel.getStrategies();
/* 47 */     for (StrategyNewBean strategy : strategies) {
/* 48 */       if (!strategy.getStatus().equals(StrategyStatus.STOPPED)) {
/* 49 */         isClosable = false;
/* 50 */         break;
/*    */       }
/*    */     }
/*    */ 
/* 54 */     ButtonTabPanelForBottomPanelWithCloseButton tabComponent = (ButtonTabPanelForBottomPanelWithCloseButton)this.tabbedPane.getButtonTabPanelForId(5);
/*    */ 
/* 57 */     if (tabComponent != null)
/* 58 */       tabComponent.setCloseBtnEnabled(isClosable);
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.strategy.tab.actions.CommonStrategyAction
 * JD-Core Version:    0.6.0
 */