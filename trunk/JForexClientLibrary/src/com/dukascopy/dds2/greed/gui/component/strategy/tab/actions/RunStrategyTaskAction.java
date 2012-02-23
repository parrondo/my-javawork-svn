/*     */ package com.dukascopy.dds2.greed.gui.component.strategy.tab.actions;
/*     */ 
/*     */ import com.dukascopy.api.Configurable;
/*     */ import com.dukascopy.api.IStrategy;
/*     */ import com.dukascopy.api.impl.connect.StrategyListener;
/*     */ import com.dukascopy.dds2.greed.GreedContext;
/*     */ import com.dukascopy.dds2.greed.agent.Strategies;
/*     */ import com.dukascopy.dds2.greed.agent.compiler.JFXPack;
/*     */ import com.dukascopy.dds2.greed.gui.JForexClientFormLayoutManager;
/*     */ import com.dukascopy.dds2.greed.gui.component.chart.TabsAndFramesTabbedPane;
/*     */ import com.dukascopy.dds2.greed.gui.component.dialog.disclaimers.StrategyDisclaimerDialog;
/*     */ import com.dukascopy.dds2.greed.gui.component.strategy.tab.mediator.RemoteStrategiesUtil;
/*     */ import com.dukascopy.dds2.greed.gui.component.strategy.tab.mediator.StrategyNewBean;
/*     */ import com.dukascopy.dds2.greed.gui.component.strategy.tab.mediator.StrategyStatus;
/*     */ import com.dukascopy.dds2.greed.gui.component.strategy.tab.mediator.StrategyType;
/*     */ import com.dukascopy.dds2.greed.gui.component.strategy.tab.parameter.StrategyParameterLocal;
/*     */ import com.dukascopy.dds2.greed.gui.component.strategy.tab.preset.StrategyPreset;
/*     */ import com.dukascopy.dds2.greed.gui.component.strategy.tab.table.StrategiesTableModel;
/*     */ import com.dukascopy.dds2.greed.gui.component.strategy.tab.util.CollectionParameterWrapper;
/*     */ import com.dukascopy.dds2.greed.gui.component.tree.WorkspaceTreeController;
/*     */ import com.dukascopy.dds2.greed.gui.helpers.IWorkspaceHelper;
/*     */ import com.dukascopy.dds2.greed.gui.l10n.LocalizationManager;
/*     */ import com.dukascopy.dds2.greed.util.FullAccessDisclaimerProvider;
/*     */ import com.dukascopy.dds2.greed.util.IFullAccessDisclaimer;
/*     */ import com.dukascopy.dds2.greed.util.NotificationUtilsProvider;
/*     */ import java.awt.Frame;
/*     */ import java.io.File;
/*     */ import java.lang.reflect.Field;
/*     */ import java.util.Date;
/*     */ import java.util.List;
/*     */ import java.util.Set;
/*     */ import javax.swing.JOptionPane;
/*     */ import javax.swing.SwingUtilities;
/*     */ import javax.swing.Timer;
/*     */ import org.slf4j.Logger;
/*     */ import org.slf4j.LoggerFactory;
/*     */ 
/*     */ public class RunStrategyTaskAction extends CommonStrategyAction
/*     */   implements IStrategyRunnable
/*     */ {
/*  45 */   private static final Logger LOGGER = LoggerFactory.getLogger(RunStrategyTaskAction.class);
/*     */   private static final String USER_PROPERTY_JSS_QUOTA = "jss.quota";
/*     */   private int strategyRow;
/*     */   private StrategyNewBean strategyBean;
/*     */ 
/*     */   public RunStrategyTaskAction(StrategiesTableModel model, int strategyRow, TabsAndFramesTabbedPane tabbedPane, WorkspaceTreeController workspaceTreeController)
/*     */   {
/*  53 */     super(model, tabbedPane, workspaceTreeController);
/*  54 */     this.strategyRow = strategyRow;
/*     */   }
/*     */ 
/*     */   protected Object executeInternal(Object param)
/*     */   {
/*  60 */     if ((param instanceof StrategyNewBean)) {
/*  61 */       this.strategyBean = ((StrategyNewBean)param);
/*     */ 
/*  63 */       if ((!this.strategyBean.getStatus().equals(StrategyStatus.STOPPED)) || (this.strategyBean.getStrategyBinaryFile() == null))
/*  64 */         return null;
/*     */       IStrategy iStrategy;
/*  68 */       if (this.strategyBean.getActivePreset() != null)
/*     */       {
/*  70 */         this.strategyBean.setRunningPresetName(this.strategyBean.getActivePreset().getName());
/*     */ 
/*  72 */         iStrategy = this.strategyBean.getStrategy();
/*  73 */         StrategyPreset activePreset = this.strategyBean.getActivePreset();
/*  74 */         List presetParams = activePreset.getStrategyParameters();
/*     */ 
/*  76 */         if (iStrategy != null) {
/*  77 */           for (StrategyParameterLocal presetParam : presetParams) {
/*     */             try {
/*  79 */               Field field = iStrategy.getClass().getField(presetParam.getId());
/*  80 */               if (field != null) {
/*  81 */                 Configurable configurable = (Configurable)field.getAnnotation(Configurable.class);
/*  82 */                 if (configurable != null)
/*  83 */                   if (field.getType().equals(File.class))
/*  84 */                     field.set(iStrategy, new File(String.valueOf(presetParam.getValue())));
/*  85 */                   else if ((presetParam.getValue() instanceof CollectionParameterWrapper))
/*  86 */                     field.set(iStrategy, ((CollectionParameterWrapper)presetParam.getValue()).getSelectedValue());
/*     */                   else
/*  88 */                     field.set(iStrategy, presetParam.getValue());
/*     */               }
/*     */             }
/*     */             catch (Exception ex)
/*     */             {
/*  93 */               LOGGER.error(ex.getMessage(), ex);
/*  94 */             }continue;
/*     */           }
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/* 100 */       StrategyDisclaimerDialog.getInstance().showDialog(this, this.strategyBean.getType().equals(StrategyType.REMOTE));
/*     */     }
/*     */ 
/* 103 */     return null;
/*     */   }
/*     */ 
/*     */   private StrategyListener createStrategyListener(StrategyNewBean strategy) {
/* 107 */     return new StrategyListener(strategy)
/*     */     {
/*     */       public void strategyStarted(long processId) {
/* 110 */         SwingUtilities.invokeLater(new Runnable(processId) {
/*     */           public void run() {
/* 112 */             RunStrategyTaskAction.1.this.val$strategy.setRunningProcessId(this.val$processId);
/* 113 */             RunStrategyTaskAction.1.this.val$strategy.setStatus(StrategyStatus.RUNNING);
/*     */ 
/* 115 */             RunStrategyTaskAction.this.updateTabClosable();
/*     */ 
/* 117 */             RunStrategyTaskAction.1.this.val$strategy.setStartTime(new Date());
/* 118 */             RunStrategyTaskAction.1.this.val$strategy.getTimer().start();
/*     */ 
/* 120 */             RunStrategyTaskAction.this.strategiesModel.fireTableCellUpdated(RunStrategyTaskAction.this.strategyRow, 2);
/* 121 */             RunStrategyTaskAction.this.strategiesModel.fireTableCellUpdated(RunStrategyTaskAction.this.strategyRow, 7);
/* 122 */             RunStrategyTaskAction.this.strategiesModel.fireTableCellUpdated(RunStrategyTaskAction.this.strategyRow, 0);
/*     */ 
/* 124 */             RunStrategyTaskAction.this.workspaceTreeController.strategyUpdated(RunStrategyTaskAction.1.this.val$strategy.getId().intValue());
/*     */           }
/*     */         });
/*     */       }
/*     */ 
/*     */       public void strategyStopped(long processId) {
/* 131 */         SwingUtilities.invokeLater(new Runnable() {
/*     */           public void run() {
/* 133 */             RunStrategyTaskAction.1.this.val$strategy.setStatus(StrategyStatus.STOPPED);
/*     */ 
/* 135 */             RunStrategyTaskAction.1.this.val$strategy.getTimer().stop();
/*     */ 
/* 138 */             if (RunStrategyTaskAction.1.this.val$strategy.getDurationTimeAsDate() == null)
/*     */             {
/* 140 */               RunStrategyTaskAction.1.this.val$strategy.setEndTime(new Date());
/* 141 */               long duration = RunStrategyTaskAction.1.this.val$strategy.getEndTimeAsDate().getTime() - RunStrategyTaskAction.1.this.val$strategy.getStartTimeAsDate().getTime();
/* 142 */               RunStrategyTaskAction.1.this.val$strategy.setDurationTime(new Date(duration));
/*     */             }
/*     */             else
/*     */             {
/* 146 */               long endTimeLong = RunStrategyTaskAction.1.this.val$strategy.getStartTimeAsDate().getTime() + RunStrategyTaskAction.1.this.val$strategy.getDurationTimeAsDate().getTime();
/* 147 */               RunStrategyTaskAction.1.this.val$strategy.setEndTime(new Date(endTimeLong));
/*     */             }
/* 149 */             RunStrategyTaskAction.1.this.val$strategy.setTimer(null);
/*     */ 
/* 151 */             RunStrategyTaskAction.this.strategiesModel.fireTableCellUpdated(RunStrategyTaskAction.this.strategyRow, 3);
/* 152 */             RunStrategyTaskAction.this.strategiesModel.fireTableCellUpdated(RunStrategyTaskAction.this.strategyRow, 4);
/* 153 */             RunStrategyTaskAction.this.strategiesModel.fireTableCellUpdated(RunStrategyTaskAction.this.strategyRow, 7);
/* 154 */             RunStrategyTaskAction.this.strategiesModel.fireTableCellUpdated(RunStrategyTaskAction.this.strategyRow, 0);
/*     */ 
/* 156 */             RunStrategyTaskAction.this.workspaceTreeController.strategyUpdated(RunStrategyTaskAction.1.this.val$strategy.getId().intValue());
/*     */ 
/* 158 */             RunStrategyTaskAction.this.updateTabClosable();
/*     */           }
/*     */         });
/*     */       }
/*     */ 
/*     */       public void strategyStartingFailed() {
/* 165 */         this.val$strategy.setStatus(StrategyStatus.STOPPED);
/* 166 */         RunStrategyTaskAction.this.strategiesModel.fireTableCellUpdated(RunStrategyTaskAction.this.strategyRow, 7);
/* 167 */         RunStrategyTaskAction.this.updateTabClosable();
/*     */       }
/*     */     };
/*     */   }
/*     */ 
/*     */   public void runStrategy() {
/* 174 */     if (this.strategyBean == null) return;
/*     */ 
/* 176 */     if (this.strategyBean.getType().equals(StrategyType.REMOTE))
/*     */     {
/* 178 */       if (this.strategyBean.getPack().isFullAccessRequested())
/*     */       {
/* 180 */         JOptionPane.showMessageDialog(null, LocalizationManager.getText("joption.pane.error.remote.full.access"), LocalizationManager.getText("joption.pane.error"), 0);
/*     */ 
/* 186 */         return;
/*     */       }
/*     */ 
/* 189 */       int remoteExecutionQuota = getRemoteExecutionQuota();
/*     */ 
/* 191 */       if (remoteExecutionQuota == 0) {
/* 192 */         JOptionPane.showMessageDialog(null, LocalizationManager.getText("joption.pane.jforex.strategy.server.no.active.slots"), LocalizationManager.getText("joption.pane.message"), 2);
/*     */ 
/* 198 */         return;
/*     */       }
/* 200 */       int runningRemotelyCount = getRunningRemotelyCount();
/*     */ 
/* 202 */       if (runningRemotelyCount >= remoteExecutionQuota) {
/* 203 */         JOptionPane.showMessageDialog(null, LocalizationManager.getText("joption.pane.jforex.strategy.server.no.free.slots"), LocalizationManager.getText("joption.pane.message"), 2);
/*     */ 
/* 209 */         return;
/*     */       }
/*     */ 
/* 213 */       if (RemoteStrategiesUtil.isRemotelySupported(this.strategyBean, NotificationUtilsProvider.getNotificationUtils()))
/*     */       {
/* 215 */         JForexClientFormLayoutManager clientFormLayoutManager = (JForexClientFormLayoutManager)GreedContext.get("layoutManager");
/* 216 */         IWorkspaceHelper workspaceHelper = clientFormLayoutManager.getWorkspaceHelper();
/* 217 */         Set instruments = workspaceHelper.getSubscribedInstruments();
/*     */ 
/* 220 */         GreedContext.publishEvent(new RunRemoteStrategyAppActionOld(this.workspaceTreeController, this.strategiesModel, this.strategyRow, this.strategyBean, instruments));
/*     */       }
/*     */     }
/*     */     else
/*     */     {
/* 225 */       if ((this.strategyBean.getPack().isFullAccessRequested()) && 
/* 226 */         (!FullAccessDisclaimerProvider.getDisclaimer().showDialog(this.strategyBean.getPack()))) {
/* 227 */         return;
/*     */       }
/*     */ 
/* 231 */       this.strategyBean.setStatus(StrategyStatus.STARTING);
/* 232 */       this.strategiesModel.fireTableCellUpdated(this.strategyRow, 7);
/*     */ 
/* 234 */       updateTabClosable();
/*     */ 
/* 236 */       Strategies.get().startStrategy((Frame)GreedContext.get("clientGui"), this.strategyBean, createStrategyListener(this.strategyBean));
/*     */     }
/*     */   }
/*     */ 
/*     */   private int getRemoteExecutionQuota()
/*     */   {
/* 244 */     String userPropertyValue = GreedContext.getUserProperty("jss.quota");
/* 245 */     if ((userPropertyValue != null) && (!userPropertyValue.isEmpty())) {
/*     */       try {
/* 247 */         return Integer.parseInt(userPropertyValue);
/*     */       } catch (Exception ex) {
/* 249 */         LOGGER.error("Unable to parse user property : jss.quota", ex);
/*     */       }
/*     */     }
/* 252 */     return 0;
/*     */   }
/*     */ 
/*     */   private int getRunningRemotelyCount() {
/* 256 */     int count = 0;
/*     */ 
/* 258 */     List strategies = this.strategiesModel.getStrategies();
/* 259 */     for (StrategyNewBean strategy : strategies) {
/* 260 */       if ((strategy.getStatus().equals(StrategyStatus.RUNNING)) && (strategy.getType().equals(StrategyType.REMOTE))) {
/* 261 */         count++;
/*     */       }
/*     */     }
/*     */ 
/* 265 */     return count;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.strategy.tab.actions.RunStrategyTaskAction
 * JD-Core Version:    0.6.0
 */