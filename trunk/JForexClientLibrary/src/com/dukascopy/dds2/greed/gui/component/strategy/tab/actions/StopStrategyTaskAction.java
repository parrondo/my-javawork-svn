/*    */ package com.dukascopy.dds2.greed.gui.component.strategy.tab.actions;
/*    */ 
/*    */ import com.dukascopy.dds2.greed.GreedContext;
/*    */ import com.dukascopy.dds2.greed.agent.Strategies;
/*    */ import com.dukascopy.dds2.greed.gui.component.chart.TabsAndFramesTabbedPane;
/*    */ import com.dukascopy.dds2.greed.gui.component.strategy.tab.mediator.StrategyNewBean;
/*    */ import com.dukascopy.dds2.greed.gui.component.strategy.tab.mediator.StrategyType;
/*    */ import com.dukascopy.dds2.greed.gui.component.strategy.tab.table.StrategiesTableModel;
/*    */ import com.dukascopy.dds2.greed.gui.component.tree.WorkspaceTreeController;
/*    */ import com.dukascopy.dds2.greed.util.ObjectUtils;
/*    */ 
/*    */ public class StopStrategyTaskAction extends CommonStrategyAction
/*    */ {
/*    */   public StopStrategyTaskAction(StrategiesTableModel model, TabsAndFramesTabbedPane tabbedPane, WorkspaceTreeController workspaceTreeController)
/*    */   {
/* 15 */     super(model, tabbedPane, workspaceTreeController);
/*    */   }
/*    */ 
/*    */   protected Object executeInternal(Object param)
/*    */   {
/* 20 */     if ((param instanceof StrategyNewBean)) {
/* 21 */       StrategyNewBean strategy = (StrategyNewBean)param;
/*    */ 
/* 23 */       if ((strategy.getType().equals(StrategyType.REMOTE)) && (!ObjectUtils.isNullOrEmpty(strategy.getRemoteProcessId()))) {
/* 24 */         StopRemoteStrategyAppAction action = new StopRemoteStrategyAppAction(this.strategiesModel, strategy);
/* 25 */         GreedContext.publishEvent(action);
/*    */       } else {
/* 27 */         Strategies.get().stopStrategy(strategy.getRunningProcessId());
/* 28 */         updateTabClosable();
/*    */       }
/*    */     }
/* 31 */     return null;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.strategy.tab.actions.StopStrategyTaskAction
 * JD-Core Version:    0.6.0
 */