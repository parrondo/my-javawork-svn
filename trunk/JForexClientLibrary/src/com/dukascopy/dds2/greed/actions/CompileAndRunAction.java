/*    */ package com.dukascopy.dds2.greed.actions;
/*    */ 
/*    */ import com.dukascopy.api.impl.CustIndicatorWrapper;
/*    */ import com.dukascopy.api.impl.ServiceWrapper;
/*    */ import com.dukascopy.api.impl.StrategyWrapper;
/*    */ import com.dukascopy.api.impl.connect.StrategyListener;
/*    */ import com.dukascopy.charts.math.indicators.IndicatorsProvider;
/*    */ import com.dukascopy.dds2.greed.GreedContext;
/*    */ import com.dukascopy.dds2.greed.agent.Strategies;
/*    */ import com.dukascopy.dds2.greed.gui.JForexClientFormLayoutManager;
/*    */ import com.dukascopy.dds2.greed.gui.component.strategy.StrategyTestPanel;
/*    */ import com.dukascopy.dds2.greed.gui.component.tree.WorkspaceJTree;
/*    */ import com.dukascopy.dds2.greed.gui.component.tree.nodes.AbstractServiceTreeNode;
/*    */ import com.dukascopy.dds2.greed.gui.component.tree.nodes.StrategiesNode;
/*    */ import com.dukascopy.dds2.greed.gui.component.tree.nodes.StrategyTreeNode;
/*    */ import com.dukascopy.dds2.greed.gui.component.tree.nodes.WorkspaceRootNode;
/*    */ import com.dukascopy.dds2.greed.gui.settings.ClientSettingsStorage;
/*    */ import com.dukascopy.dds2.greed.util.CompilerUtils;
/*    */ import com.dukascopy.dds2.greed.util.NotificationUtilsProvider;
/*    */ import java.awt.Frame;
/*    */ import java.util.List;
/*    */ import org.slf4j.Logger;
/*    */ import org.slf4j.LoggerFactory;
/*    */ 
/*    */ public class CompileAndRunAction extends AppActionEvent
/*    */ {
/* 30 */   private static final Logger LOGGER = LoggerFactory.getLogger(CompileAndRunAction.class);
/*    */   private ServiceWrapper serviceWrapper;
/*    */   private boolean run;
/*    */   private AbstractServiceTreeNode strategyTreeNode;
/*    */   private StrategyListener strategyListener;
/*    */   private boolean compilationSuccessfull;
/* 38 */   private String presetName = null;
/*    */ 
/*    */   public CompileAndRunAction(Object source, ServiceWrapper serviceWrapper, AbstractServiceTreeNode strategyTreeNode, boolean run, StrategyListener strategyListener, String presetName) {
/* 41 */     this(source, serviceWrapper, strategyTreeNode, run, strategyListener);
/* 42 */     this.presetName = presetName;
/*    */   }
/*    */   public CompileAndRunAction(Object source, ServiceWrapper serviceWrapper, AbstractServiceTreeNode strategyTreeNode, boolean run, StrategyListener strategyListener) {
/* 45 */     super(source, false, true);
/* 46 */     this.serviceWrapper = serviceWrapper;
/* 47 */     this.strategyTreeNode = strategyTreeNode;
/* 48 */     this.run = run;
/* 49 */     this.strategyListener = strategyListener;
/*    */   }
/*    */ 
/*    */   public void doAction()
/*    */   {
/* 54 */     this.compilationSuccessfull = CompilerUtils.getInstance().runCompilation(this.serviceWrapper);
/*    */   }
/*    */ 
/*    */   public void updateGuiAfter()
/*    */   {
/* 60 */     if ((this.compilationSuccessfull) && (this.run)) {
/* 61 */       LOGGER.debug("Compilation successfull, running...");
/* 62 */       Strategies.get().startStrategy((Frame)GreedContext.get("clientGui"), this.serviceWrapper.getBinaryFile(), this.strategyListener, this.presetName);
/*    */     } else {
/* 64 */       if (this.compilationSuccessfull) {
/* 65 */         if ((this.serviceWrapper instanceof StrategyWrapper))
/*    */         {
/* 68 */           JForexClientFormLayoutManager layoutManager = (JForexClientFormLayoutManager)GreedContext.get("layoutManager");
/*    */ 
/* 70 */           if (this.strategyTreeNode == null) {
/* 71 */             StrategiesNode strategiesNode = layoutManager.getWorkspaceJTree().getWorkspaceRoot().getStrategiesTreeNode();
/* 72 */             this.strategyTreeNode = ((StrategyTreeNode)strategiesNode.getServiceBySourceFile(this.serviceWrapper.getSourceFile()));
/*    */           }
/*    */ 
/* 75 */           ((StrategyTreeNode)this.strategyTreeNode).setBinaryFile(this.serviceWrapper.getBinaryFile());
/*    */ 
/* 77 */           ClientSettingsStorage storage = (ClientSettingsStorage)GreedContext.get("settingsStorage");
/* 78 */           storage.saveStrategyNewBean(((StrategyTreeNode)this.strategyTreeNode).getStrategy());
/*    */ 
/* 80 */           List testPanels = layoutManager.getStrategyTestPanels();
/* 81 */           for (StrategyTestPanel testPanel : testPanels) {
/* 82 */             testPanel.reloadStrategies();
/*    */           }
/*    */         }
/*    */       }
/*    */       else {
/* 87 */         LOGGER.debug("Compilation failed");
/*    */       }
/* 89 */       if ((!this.run) || (!(this.serviceWrapper instanceof StrategyWrapper)))
/*    */       {
/* 91 */         if ((this.serviceWrapper instanceof CustIndicatorWrapper)) {
/* 92 */           CustIndicatorWrapper custIndWrapper = (CustIndicatorWrapper)this.serviceWrapper;
/*    */ 
/* 94 */           IndicatorsProvider indicatorsProvider = IndicatorsProvider.getInstance();
/* 95 */           if (!indicatorsProvider.isCustomIndicatorEnabled(custIndWrapper.getBinaryFile()))
/* 96 */             indicatorsProvider.enableIndicator(custIndWrapper, NotificationUtilsProvider.getNotificationUtils());
/*    */         }
/*    */       }
/*    */     }
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.actions.CompileAndRunAction
 * JD-Core Version:    0.6.0
 */