/*     */ package com.dukascopy.dds2.greed.gui.component.strategy.tab;
/*     */ 
/*     */ import com.dukascopy.api.IStrategy;
/*     */ import com.dukascopy.api.IStrategyListener;
/*     */ import com.dukascopy.api.JFException;
/*     */ import com.dukascopy.api.impl.StrategyMessages;
/*     */ import com.dukascopy.charts.persistence.IdManager;
/*     */ import com.dukascopy.dds2.greed.GreedContext;
/*     */ import com.dukascopy.dds2.greed.actions.RemoteStrategiesListResponseAction;
/*     */ import com.dukascopy.dds2.greed.gui.component.chart.BottomTabsAndFramesTabbedPane;
/*     */ import com.dukascopy.dds2.greed.gui.component.chart.holders.IChartTabsAndFramesController;
/*     */ import com.dukascopy.dds2.greed.gui.component.strategy.tab.mediator.StrategyNewBean;
/*     */ import com.dukascopy.dds2.greed.gui.component.strategy.tab.mediator.StrategyStatus;
/*     */ import com.dukascopy.dds2.greed.gui.component.strategy.tab.mediator.StrategyType;
/*     */ import com.dukascopy.dds2.greed.gui.component.strategy.tab.table.StrategiesTable;
/*     */ import com.dukascopy.dds2.greed.gui.component.strategy.tab.table.StrategiesTableColumnModel;
/*     */ import com.dukascopy.dds2.greed.gui.component.strategy.tab.table.StrategiesTableModel;
/*     */ import com.dukascopy.dds2.greed.gui.component.strategy.tab.table.sorting.StrategiesTableHeader;
/*     */ import com.dukascopy.dds2.greed.gui.component.strategy.tab.table.sorting.StrategyTableComparator;
/*     */ import com.dukascopy.dds2.greed.gui.component.strategy.tab.toolbar.StrategiesToolbar;
/*     */ import com.dukascopy.dds2.greed.gui.component.tree.StrategyHelper;
/*     */ import com.dukascopy.dds2.greed.gui.component.tree.WorkspaceTreeController;
/*     */ import com.dukascopy.dds2.greed.gui.component.tree.nodes.StrategyTreeNode;
/*     */ import com.dukascopy.dds2.greed.gui.settings.ClientSettingsStorage;
/*     */ import com.dukascopy.dds2.greed.util.GridBagLayoutHelper;
/*     */ import com.dukascopy.dds2.greed.util.ObjectUtils;
/*     */ import com.dukascopy.transport.common.msg.strategy.StrategyProcessDescriptor;
/*     */ import com.dukascopy.transport.common.msg.strategy.StrategyRunErrorResponseMessage;
/*     */ import com.dukascopy.transport.common.msg.strategy.StrategyState;
/*     */ import com.dukascopy.transport.common.msg.strategy.StrategyStateMessage;
/*     */ import java.awt.GridBagConstraints;
/*     */ import java.awt.GridBagLayout;
/*     */ import java.io.File;
/*     */ import java.util.Collection;
/*     */ import java.util.Date;
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import javax.swing.JPanel;
/*     */ import javax.swing.JScrollPane;
/*     */ import javax.swing.Timer;
/*     */ import org.slf4j.Logger;
/*     */ import org.slf4j.LoggerFactory;
/*     */ 
/*     */ public class StrategiesContentPane extends JPanel
/*     */ {
/*  55 */   private static final Logger LOGGER = LoggerFactory.getLogger(StrategiesContentPane.class);
/*     */   private static final String STRATEGY_SEPARATOR = ";";
/*     */   private static final String PRESETS_SEPARATOR = ",";
/*     */   private StrategiesToolbar toolbar;
/*     */   private StrategiesTable strategiesTable;
/*     */ 
/*     */   public StrategiesContentPane(ClientSettingsStorage settingsStorage, BottomTabsAndFramesTabbedPane tabbedPane, IChartTabsAndFramesController tabsAndFramesController)
/*     */   {
/*  67 */     List strategies = settingsStorage.getStrategyNewBeans();
/*     */ 
/*  69 */     for (StrategyNewBean strategy : strategies) {
/*  70 */       strategy.resetDates();
/*     */     }
/*     */ 
/*  73 */     StrategiesTableModel tableModel = new StrategiesTableModel(strategies, settingsStorage);
/*  74 */     StrategiesTableColumnModel columnModel = new StrategiesTableColumnModel();
/*     */ 
/*  76 */     this.strategiesTable = new StrategiesTable(tableModel, columnModel);
/*     */ 
/*  78 */     StrategiesTableHeader tableHeader = (StrategiesTableHeader)this.strategiesTable.getTableHeader();
/*  79 */     StrategyTableComparator comparator = tableHeader.getComparator();
/*  80 */     comparator.initSortingMap(columnModel);
/*     */ 
/*  82 */     this.toolbar = new StrategiesToolbar(settingsStorage, tabbedPane, this.strategiesTable, tabsAndFramesController);
/*     */ 
/*  84 */     setLayout(new GridBagLayout());
/*     */ 
/*  86 */     GridBagConstraints gbc = new GridBagConstraints();
/*     */ 
/*  88 */     GridBagLayoutHelper.add(0, 0, 1.0D, 0.0D, 2, gbc, this, this.toolbar);
/*  89 */     GridBagLayoutHelper.add(0, 1, 1.0D, 1.0D, 1, gbc, this, new JScrollPane(this.strategiesTable));
/*     */ 
/*  92 */     for (StrategyNewBean strBean : strategies) {
/*  93 */       settingsStorage.saveStrategyNewBean(strBean);
/*     */     }
/*     */ 
/*  96 */     settingsStorage.restoreTableColumns(this.strategiesTable.getTableId(), columnModel);
/*     */   }
/*     */ 
/*     */   public StrategiesTable getTable() {
/* 100 */     return this.strategiesTable;
/*     */   }
/*     */ 
/*     */   public void setWorkspaceTreeController(WorkspaceTreeController workspaceTreeController) {
/* 104 */     this.toolbar.setWorkspaceTreeController(workspaceTreeController);
/*     */ 
/* 106 */     List strategies = ((StrategiesTableModel)this.strategiesTable.getModel()).getStrategies();
/* 107 */     for (StrategyNewBean strategy : strategies) {
/* 108 */       workspaceTreeController.strategyAdded(strategy);
/*     */     }
/*     */ 
/* 111 */     workspaceTreeController.restoreExpandedStatus();
/*     */   }
/*     */ 
/*     */   public List<StrategyNewBean> openStrategiesSelection(boolean openEditor) {
/* 115 */     return this.toolbar.openStrategiesSelection(openEditor);
/*     */   }
/*     */ 
/*     */   public void removeStrategy(int strategyId) {
/* 119 */     this.toolbar.removeStrategy(strategyId);
/*     */   }
/*     */ 
/*     */   public void selectStrategy(int strategyId) {
/* 123 */     this.toolbar.selectStrategy(strategyId);
/*     */   }
/*     */ 
/*     */   public void addStrategyFromFile(File strategyFile, StrategyTreeNode strategyNode)
/*     */   {
/* 130 */     this.toolbar.addStrategyFromFile(strategyFile, strategyNode);
/*     */   }
/*     */ 
/*     */   public boolean isClosable() {
/* 134 */     if (this.strategiesTable != null) {
/* 135 */       List strategies = ((StrategiesTableModel)this.strategiesTable.getModel()).getStrategies();
/* 136 */       for (StrategyNewBean strategy : strategies) {
/* 137 */         if (!strategy.getStatus().equals(StrategyStatus.STOPPED)) {
/* 138 */           return false;
/*     */         }
/*     */       }
/*     */     }
/* 142 */     return true;
/*     */   }
/*     */ 
/*     */   public void onRemoteStrategyRunErrorResponse(StrategyRunErrorResponseMessage errorMessage) {
/* 146 */     String runRequestId = errorMessage.getRunRequestId();
/* 147 */     if (!ObjectUtils.isNullOrEmpty(runRequestId)) {
/* 148 */       List strategies = ((StrategiesTableModel)this.strategiesTable.getModel()).getStrategies();
/* 149 */       for (StrategyNewBean strategy : strategies)
/* 150 */         if ((strategy.getType() == StrategyType.REMOTE) && (ObjectUtils.isEqual(strategy.getRemoteRequestId(), runRequestId))) {
/* 151 */           stopStrategy(strategy, true);
/* 152 */           saveStrategy(strategy);
/* 153 */           return;
/*     */         }
/*     */     }
/*     */   }
/*     */ 
/*     */   public void onRemoteStrategyRunResponse(StrategyProcessDescriptor descriptor)
/*     */   {
/* 160 */     String runRequestId = descriptor.getRequestId();
/* 161 */     if (!ObjectUtils.isNullOrEmpty(runRequestId)) {
/* 162 */       List strategies = ((StrategiesTableModel)this.strategiesTable.getModel()).getStrategies();
/* 163 */       for (StrategyNewBean strategy : strategies)
/* 164 */         if ((strategy.getType() == StrategyType.REMOTE) && (ObjectUtils.isEqual(strategy.getRemoteRequestId(), runRequestId))) {
/* 165 */           strategy.setRemoteProcessId(descriptor.getPid());
/* 166 */           strategy.setStatus(StrategyStatus.INITIALIZING);
/* 167 */           this.toolbar.getWorkspaceTreeController().strategyUpdated(strategy.getId().intValue());
/* 168 */           ((StrategiesTableModel)this.strategiesTable.getModel()).fireTableDataChanged();
/* 169 */           saveStrategy(strategy);
/* 170 */           return;
/*     */         }
/*     */     }
/*     */   }
/*     */ 
/*     */   public void onRemoteStrategyUpdateMessage(StrategyStateMessage stateMessage)
/*     */   {
/* 178 */     StrategyState state = stateMessage.getStrategyState();
/* 179 */     LOGGER.error("STATE RECEIVED: " + state);
/*     */ 
/* 181 */     String comment = stateMessage.getComments();
/* 182 */     switch (1.$SwitchMap$com$dukascopy$transport$common$msg$strategy$StrategyState[state.ordinal()]) {
/*     */     case 1:
/* 184 */       LOGGER.debug("Error running strategy remotely : [{}]", comment);
/* 185 */       break;
/*     */     case 2:
/* 187 */       LOGGER.debug("Remotely running strategy has been terminated : [{}]", comment);
/*     */     }
/*     */ 
/* 190 */     StrategyProcessDescriptor descriptor = stateMessage.getStrategyProcessDescriptor();
/* 191 */     String runRequestId = descriptor.getRequestId();
/* 192 */     List strategies = ((StrategiesTableModel)this.strategiesTable.getModel()).getStrategies();
/* 193 */     for (StrategyNewBean strategy : strategies)
/* 194 */       if ((strategy.getType().equals(StrategyType.REMOTE)) && ((ObjectUtils.isEqual(descriptor.getPid(), strategy.getRemoteProcessId())) || ((!ObjectUtils.isNullOrEmpty(runRequestId)) && (ObjectUtils.isEqual(strategy.getRemoteRequestId(), runRequestId)))))
/*     */       {
/* 197 */         if (ObjectUtils.isNullOrEmpty(strategy.getRemoteProcessId())) {
/* 198 */           strategy.setRemoteProcessId(descriptor.getPid());
/*     */         }
/*     */ 
/* 201 */         List params = StrategyHelper.convert(descriptor.getParameters());
/*     */ 
/* 203 */         if (state == StrategyState.TERMINATED) {
/* 204 */           if ((strategy.getTimer() != null) && (strategy.getStartTimeAsDate() != null)) {
/* 205 */             strategy.getTimer().stop();
/* 206 */             long endTimeLong = strategy.getStartTimeAsDate().getTime() + strategy.getDurationTimeAsDate().getTime();
/* 207 */             strategy.setEndTime(new Date(endTimeLong));
/*     */           }
/* 209 */           stopStrategy(strategy, true);
/* 210 */           StrategyMessages.strategyIsStopped(descriptor.getFileName(), params, true, strategy.getEndTimeAsDate());
/*     */         }
/* 212 */         else if ((state == StrategyState.LAUNCHED) && (!strategy.getStatus().equals(StrategyStatus.RUNNING))) {
/* 213 */           strategy.setStatus(StrategyStatus.RUNNING);
/*     */ 
/* 215 */           strategy.setStartTime(new Date());
/* 216 */           strategy.getTimer().start();
/*     */ 
/* 218 */           ((StrategiesTableModel)this.strategiesTable.getModel()).fireTableDataChanged();
/* 219 */           this.toolbar.getWorkspaceTreeController().strategyUpdated(strategy.getId().intValue());
/*     */ 
/* 221 */           StrategyMessages.strategyIsStarted(descriptor.getFileName(), params, true, strategy.getStartTimeAsDate());
/*     */         }
/* 223 */         else if (state == StrategyState.ERROR) {
/* 224 */           stopStrategy(strategy, true);
/*     */         }
/* 226 */         saveStrategy(strategy);
/* 227 */         break;
/*     */       }
/*     */   }
/*     */ 
/*     */   public void updateRemotelyRunStrategies(Collection<StrategyProcessDescriptor> descriptors)
/*     */   {
/* 234 */     WorkspaceTreeController workspaceTreeController = this.toolbar.getWorkspaceTreeController();
/* 235 */     boolean updateTable = false;
/*     */ 
/* 237 */     List strategies = ((StrategiesTableModel)this.strategiesTable.getModel()).getStrategies();
/* 238 */     Map strategiesByPid = new HashMap(strategies.size());
/* 239 */     Map strategiesByRequestId = new HashMap(strategies.size());
/* 240 */     for (StrategyNewBean strategy : strategies) {
/* 241 */       if (!ObjectUtils.isNullOrEmpty(strategy.getRemoteProcessId())) {
/* 242 */         strategiesByPid.put(strategy.getRemoteProcessId(), strategy);
/*     */       }
/* 244 */       if (!ObjectUtils.isNullOrEmpty(strategy.getRemoteRequestId())) {
/* 245 */         strategiesByRequestId.put(strategy.getRemoteRequestId(), strategy);
/*     */       }
/*     */     }
/*     */ 
/* 249 */     Map descriptorsByPid = new HashMap(descriptors.size());
/* 250 */     Map descriptorsByRequestId = new HashMap(descriptors.size());
/* 251 */     for (StrategyProcessDescriptor descriptor : descriptors) {
/* 252 */       if (!ObjectUtils.isNullOrEmpty(descriptor.getPid())) {
/* 253 */         descriptorsByPid.put(descriptor.getPid(), descriptor);
/*     */       }
/* 255 */       if (!ObjectUtils.isNullOrEmpty(descriptor.getRequestId())) {
/* 256 */         descriptorsByRequestId.put(descriptor.getRequestId(), descriptor);
/*     */       }
/*     */     }
/*     */ 
/* 260 */     for (StrategyNewBean strategy : strategies) {
/* 261 */       if (strategy.getType().equals(StrategyType.REMOTE)) {
/* 262 */         boolean present = (descriptorsByPid.containsKey(strategy.getRemoteProcessId())) || (descriptorsByRequestId.containsKey(strategy.getRemoteRequestId()));
/* 263 */         if (present) {
/* 264 */           if (ObjectUtils.isNullOrEmpty(strategy.getRemoteProcessId())) {
/* 265 */             StrategyProcessDescriptor descriptor = (StrategyProcessDescriptor)descriptorsByRequestId.get(strategy.getRemoteRequestId());
/* 266 */             strategy.setRemoteProcessId(descriptor.getPid());
/* 267 */             saveStrategy(strategy);
/*     */           }
/* 269 */           if (strategy.getStatus() != StrategyStatus.RUNNING) {
/* 270 */             strategy.setStatus(StrategyStatus.RUNNING);
/* 271 */             workspaceTreeController.strategyUpdated(strategy.getId().intValue());
/* 272 */             updateTable = true;
/*     */           }
/* 274 */         } else if ((!strategy.getStatus().equals(StrategyStatus.STOPPED)) || (!ObjectUtils.isNullOrEmpty(strategy.getRemoteProcessId())) || (!ObjectUtils.isNullOrEmpty(strategy.getRemoteRequestId())))
/*     */         {
/* 278 */           if (strategy.getStartingTimestamp() + RemoteStrategiesListResponseAction.REMOTE_STRATEGIES_START_TIMEOUT < System.currentTimeMillis()) {
/* 279 */             updateTable = true;
/* 280 */             stopStrategy(strategy, false);
/* 281 */             saveStrategy(strategy);
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/* 287 */     for (StrategyProcessDescriptor descriptor : descriptors) {
/* 288 */       boolean present = (strategiesByPid.containsKey(descriptor.getPid())) || (strategiesByRequestId.containsKey(descriptor.getRequestId()));
/* 289 */       if (present) {
/* 290 */         StrategyNewBean strategy = null;
/* 291 */         if (!strategiesByPid.containsKey(descriptor.getPid())) {
/* 292 */           strategy = (StrategyNewBean)strategiesByRequestId.get(descriptor.getRequestId());
/* 293 */           strategy.setRemoteProcessId(descriptor.getPid());
/* 294 */           saveStrategy(strategy);
/*     */         } else {
/* 296 */           strategy = (StrategyNewBean)strategiesByPid.get(descriptor.getPid());
/*     */         }
/* 298 */         if (strategy.getStatus() != StrategyStatus.RUNNING) {
/* 299 */           strategy.setStatus(StrategyStatus.RUNNING);
/* 300 */           workspaceTreeController.strategyUpdated(strategy.getId().intValue());
/* 301 */           updateTable = true;
/*     */         }
/*     */       } else {
/* 304 */         StrategyNewBean strategy = createStrategyFromDescriptor(descriptor);
/* 305 */         strategy.setStatus(StrategyStatus.RUNNING);
/* 306 */         this.strategiesTable.addStrategy(strategy);
/* 307 */         workspaceTreeController.strategyAdded(strategy);
/* 308 */         updateTable = true;
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 313 */     if (updateTable)
/* 314 */       ((StrategiesTableModel)this.strategiesTable.getModel()).fireTableDataChanged();
/*     */   }
/*     */ 
/*     */   public long startStrategyFromAnother(File jfxFile, IStrategyListener listener, Map<String, Object> configurables, boolean fullAccess) throws JFException
/*     */   {
/* 319 */     throw new JFException("Not implemented yet");
/*     */   }
/*     */ 
/*     */   public long startStrategyFromAnother(IStrategy strategy, IStrategyListener listener, boolean fullAccess)
/*     */     throws JFException
/*     */   {
/* 331 */     throw new JFException("Not implemented yet");
/*     */   }
/*     */ 
/*     */   public void checkStrategiesRunOnStart()
/*     */   {
/* 340 */     Object config = GreedContext.getConfig("jnlp.run.strategy.on.start");
/* 341 */     if ((config != null) && (((Boolean)config).booleanValue()))
/*     */     {
/* 343 */       String strategiesPath = System.getProperty("jnlp.strategy.path");
/* 344 */       if (strategiesPath != null)
/*     */       {
/* 346 */         String[] strategyAndPresets = strategiesPath.split(";");
/*     */ 
/* 348 */         for (String strategyPath : strategyAndPresets) {
/* 349 */           if (strategyPath == null)
/*     */             continue;
/* 351 */           String[] strategyInfo = strategyPath.split(",");
/*     */ 
/* 353 */           if ((strategyInfo.length < 1) || (strategyInfo.length > 2)) {
/* 354 */             return;
/*     */           }
/*     */ 
/* 357 */           String strPath = strategyInfo[0];
/* 358 */           String presetName = null;
/*     */ 
/* 360 */           if (strategyInfo.length == 2) {
/* 361 */             presetName = strategyInfo[1].trim();
/*     */           }
/*     */ 
/* 364 */           File strategyFile = null;
/*     */           try {
/* 366 */             strategyFile = new File(strPath);
/*     */           } catch (Exception ex) {
/* 368 */             LOGGER.error("Error while opening strategy file : " + strPath, ex);
/*     */           }
/*     */ 
/* 371 */           if (!strategyFile.exists()) {
/* 372 */             LOGGER.warn("Strategy " + strategyFile.getAbsolutePath() + " was not found.");
/*     */           }
/*     */           else
/*     */           {
/* 376 */             this.toolbar.addStrategyToRun(strategyFile, presetName);
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   private StrategyNewBean createStrategyFromDescriptor(StrategyProcessDescriptor descriptor)
/*     */   {
/* 385 */     StrategyNewBean strategy = new StrategyNewBean();
/*     */ 
/* 387 */     strategy.setRemoteProcessId(descriptor.getPid());
/* 388 */     strategy.setRemoteRequestId(descriptor.getRequestId());
/* 389 */     strategy.setType(StrategyType.REMOTE);
/* 390 */     strategy.setName(descriptor.getFileName());
/* 391 */     strategy.setId(Integer.valueOf(IdManager.getInstance().getNextServiceId()));
/* 392 */     return strategy;
/*     */   }
/*     */ 
/*     */   private void stopStrategy(StrategyNewBean strategy, boolean updateTable) {
/* 396 */     strategy.setStatus(StrategyStatus.STOPPED);
/* 397 */     strategy.setRemoteProcessId(null);
/* 398 */     this.toolbar.getWorkspaceTreeController().strategyUpdated(strategy.getId().intValue());
/* 399 */     if (updateTable)
/* 400 */       ((StrategiesTableModel)this.strategiesTable.getModel()).fireTableDataChanged();
/*     */   }
/*     */ 
/*     */   private void saveStrategy(StrategyNewBean strategy)
/*     */   {
/* 405 */     ClientSettingsStorage clientSettingsStorage = (ClientSettingsStorage)GreedContext.get("settingsStorage");
/* 406 */     clientSettingsStorage.saveStrategyNewBean(strategy);
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.strategy.tab.StrategiesContentPane
 * JD-Core Version:    0.6.0
 */