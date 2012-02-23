/*     */ package com.dukascopy.dds2.greed.gui.component.strategy;
/*     */ 
/*     */ import com.dukascopy.api.Instrument;
/*     */ import com.dukascopy.api.impl.StrategyWrapper;
/*     */ import com.dukascopy.api.system.Commissions;
/*     */ import com.dukascopy.api.system.Overnights;
/*     */ import com.dukascopy.charts.persistence.StrategyTestBean;
/*     */ import com.dukascopy.dds2.greed.GreedContext;
/*     */ import com.dukascopy.dds2.greed.agent.strategy.ide.api.ServiceSourceType;
/*     */ import com.dukascopy.dds2.greed.gui.ClientFormLayoutManager;
/*     */ import com.dukascopy.dds2.greed.gui.JForexClientFormLayoutManager;
/*     */ import com.dukascopy.dds2.greed.gui.component.JRoundedBorder;
/*     */ import com.dukascopy.dds2.greed.gui.component.chart.holders.IChartTabsAndFramesController;
/*     */ import com.dukascopy.dds2.greed.gui.component.tree.WorkspaceTreeController;
/*     */ import com.dukascopy.dds2.greed.gui.component.tree.actions.OpenStrategyAction;
/*     */ import com.dukascopy.dds2.greed.gui.component.tree.nodes.StrategyTreeNode;
/*     */ import com.dukascopy.dds2.greed.gui.l10n.LocalizationManager;
/*     */ import com.dukascopy.dds2.greed.gui.l10n.components.JLocalizableButton;
/*     */ import com.dukascopy.dds2.greed.gui.l10n.components.JLocalizableCheckBox;
/*     */ import com.dukascopy.dds2.greed.gui.l10n.components.JLocalizableComboBox;
/*     */ import com.dukascopy.dds2.greed.gui.l10n.components.JLocalizableLabel;
/*     */ import com.dukascopy.dds2.greed.gui.l10n.components.JLocalizableRoundedBorder;
/*     */ import com.dukascopy.dds2.greed.gui.resizing.ResizingManager.ComponentSize;
/*     */ import com.dukascopy.dds2.greed.gui.settings.ClientSettingsStorage;
/*     */ import com.dukascopy.dds2.greed.util.AbstractCurrencyConverter;
/*     */ import com.dukascopy.dds2.greed.util.GridBagLayoutHelper;
/*     */ import java.awt.BorderLayout;
/*     */ import java.awt.GridBagConstraints;
/*     */ import java.awt.GridBagLayout;
/*     */ import java.awt.event.ActionEvent;
/*     */ import java.awt.event.ActionListener;
/*     */ import java.awt.event.ItemEvent;
/*     */ import java.awt.event.ItemListener;
/*     */ import java.io.File;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collections;
/*     */ import java.util.Currency;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Map.Entry;
/*     */ import javax.swing.ComboBoxModel;
/*     */ import javax.swing.JButton;
/*     */ import javax.swing.JCheckBox;
/*     */ import javax.swing.JComboBox;
/*     */ import javax.swing.JOptionPane;
/*     */ import javax.swing.JPanel;
/*     */ import javax.swing.event.EventListenerList;
/*     */ import javax.swing.event.ListDataEvent;
/*     */ import javax.swing.event.ListDataListener;
/*     */ 
/*     */ public class ReportingPanel extends JPanel
/*     */ {
/*     */   public static final int MINIMAL_LEVERAGE = 1;
/*     */   public static final int MINIMAL_MC = 1;
/*     */   public static final double MINIMAL_EQUITY = 0.0D;
/*     */   public static final double MINIMAL_DEPOSIT = 0.0D;
/*     */   private JComboBox strategyComboBox;
/*  81 */   private JLocalizableLabel strategyLabel = new JLocalizableLabel("label.strategy");
/*  82 */   private StrategyComboBoxModel strategyComboBoxModel = new StrategyComboBoxModel(null);
/*     */   private JButton editStrategyButton;
/*     */   private JButton openStrategyButton;
/*     */   private OpenStrategyButtonAction openStrategyAction;
/*     */   private JButton accountButton;
/*     */   private JCheckBox optimizationCheckBox;
/*     */   private JCheckBox visualModeCheckBox;
/*     */   private JCheckBox chbShowEquityGraph;
/*     */   private JCheckBox chbShowProfitLossGraph;
/*     */   private JCheckBox chbShowBalanceGraph;
/*     */   private JCheckBox reportCheckBox;
/*     */   private JCheckBox messagesCheckBox;
/*     */   private SaveMessagesPanel saveMessagesPanel;
/*     */   private IChartTabsAndFramesController chartTabsAndFramesController;
/*     */   private WorkspaceTreeController workspaceTreeController;
/*     */   private ActionListener strategyListListener;
/*     */   private ActionListener accountCurrencyListener;
/* 101 */   private EventListenerList listenerList = new EventListenerList();
/* 102 */   private Currency accountCurrency = Instrument.EURUSD.getSecondaryCurrency();
/* 103 */   private double initialDeposit = 50000.0D;
/* 104 */   private int maxLeverage = 100;
/* 105 */   private int mcLeverage = 200;
/*     */ 
/* 108 */   private int mcWeekendLeverage = 130;
/*     */ 
/* 110 */   private double mcEquity = 0.0D;
/* 111 */   private Overnights overnights = new Overnights(false);
/* 112 */   private Commissions commissions = new Commissions(false);
/*     */ 
/*     */   public ReportingPanel(IChartTabsAndFramesController chartTabsAndFramesController) {
/* 115 */     this.chartTabsAndFramesController = chartTabsAndFramesController;
/* 116 */     this.strategyListListener = new ActionListener()
/*     */     {
/*     */       public void actionPerformed(ActionEvent e) {
/* 119 */         ReportingPanel.this.strategyComboBoxModel.reloadStrategyList();
/*     */       }
/*     */     };
/* 122 */     this.openStrategyAction = new OpenStrategyButtonAction();
/*     */   }
/*     */ 
/*     */   public void addReportingPanelListener(ReportingPanelListener listener) {
/* 126 */     this.listenerList.add(ReportingPanelListener.class, listener);
/*     */   }
/*     */ 
/*     */   public void removeReportingPanelListener(ReportingPanelListener listener) {
/* 130 */     this.listenerList.remove(ReportingPanelListener.class, listener);
/*     */   }
/*     */ 
/*     */   protected void fireStateChanged() {
/* 134 */     ReportingPanelEvent event = new ReportingPanelEvent(this);
/* 135 */     ReportingPanelListener[] listeners = (ReportingPanelListener[])this.listenerList.getListeners(ReportingPanelListener.class);
/* 136 */     for (ReportingPanelListener listener : listeners)
/* 137 */       listener.stateChanged(event);
/*     */   }
/*     */ 
/*     */   public void build()
/*     */   {
/* 142 */     JRoundedBorder border = new JLocalizableRoundedBorder(this, "border.historical.testing");
/* 143 */     setBorder(border);
/*     */ 
/* 145 */     this.strategyComboBox = new JLocalizableComboBox(this.strategyComboBoxModel, null)
/*     */     {
/*     */       public void translate()
/*     */       {
/*     */       }
/*     */     };
/* 151 */     this.strategyComboBox.setToolTipText("combo.strategy.tooltip");
/* 152 */     this.strategyComboBox.setMaximumRowCount(7);
/* 153 */     this.strategyComboBox.setSelectedIndex(0);
/* 154 */     this.openStrategyButton = new JLocalizableButton("open.button.text");
/* 155 */     this.editStrategyButton = new JLocalizableButton("button.edit");
/* 156 */     this.accountButton = new JLocalizableButton("button.account");
/* 157 */     this.reportCheckBox = new JLocalizableCheckBox("button.report", true);
/* 158 */     this.optimizationCheckBox = new JLocalizableCheckBox("button.tester.optimization");
/* 159 */     this.visualModeCheckBox = new JLocalizableCheckBox("button.visual.mode", false);
/* 160 */     this.chbShowBalanceGraph = new JLocalizableCheckBox("button.show.balance");
/* 161 */     this.chbShowProfitLossGraph = new JLocalizableCheckBox("button.show.profit.loss");
/* 162 */     this.chbShowEquityGraph = new JLocalizableCheckBox("button.show.equity");
/* 163 */     this.messagesCheckBox = new JLocalizableCheckBox("button.messages");
/* 164 */     this.saveMessagesPanel = new SaveMessagesPanel();
/* 165 */     this.saveMessagesPanel.addSaveToFileListener(new ItemListener()
/*     */     {
/*     */       public void itemStateChanged(ItemEvent e) {
/* 168 */         if (ReportingPanel.this.saveMessagesPanel.isSaveMessagesToFile())
/* 169 */           ReportingPanel.this.messagesCheckBox.setSelected(true);
/*     */       }
/*     */     });
/* 178 */     GridBagConstraints gbc = new GridBagConstraints();
/* 179 */     gbc.fill = 0;
/* 180 */     gbc.anchor = 17;
/* 181 */     JPanel reportSettingsPanel = new JPanel(new GridBagLayout());
/* 182 */     GridBagLayoutHelper.add(0, 0, 0.0D, 0.0D, 1, 1, 0, 0, 0, 0, gbc, reportSettingsPanel, this.optimizationCheckBox);
/* 183 */     GridBagLayoutHelper.add(1, 0, 0.0D, 0.0D, 1, 1, 5, 0, 0, 0, gbc, reportSettingsPanel, this.reportCheckBox);
/* 184 */     GridBagLayoutHelper.add(2, 0, 0.0D, 0.0D, 1, 1, 5, 0, 0, 0, gbc, reportSettingsPanel, this.visualModeCheckBox);
/* 185 */     GridBagLayoutHelper.add(3, 0, 0.0D, 0.0D, 1, 1, 5, 0, 0, 0, gbc, reportSettingsPanel, this.chbShowEquityGraph);
/* 186 */     GridBagLayoutHelper.add(4, 0, 0.0D, 0.0D, 1, 1, 5, 0, 0, 0, gbc, reportSettingsPanel, this.chbShowProfitLossGraph);
/* 187 */     GridBagLayoutHelper.add(5, 0, 0.0D, 0.0D, 1, 1, 5, 0, 0, 0, gbc, reportSettingsPanel, this.chbShowBalanceGraph);
/* 188 */     GridBagLayoutHelper.add(0, 1, 0.0D, 0.0D, 1, 1, 0, 5, 0, 0, gbc, reportSettingsPanel, this.messagesCheckBox);
/* 189 */     gbc.fill = 2;
/* 190 */     GridBagLayoutHelper.add(1, 1, 1.0D, 0.0D, 5, 1, 5, 5, 0, 0, gbc, reportSettingsPanel, this.saveMessagesPanel);
/*     */ 
/* 192 */     GridBagLayoutHelper.add(0, 2, 1.0D, 1.0D, 6, 1, 0, 0, 0, 0, gbc, reportSettingsPanel, new JPanel(new BorderLayout()));
/*     */ 
/* 195 */     setLayout(new GridBagLayout());
/* 196 */     gbc.fill = 0;
/* 197 */     gbc.anchor = 17;
/* 198 */     GridBagLayoutHelper.add(0, 0, 0.0D, 0.0D, 1, 1, 0, 0, 0, 0, gbc, this, this.strategyLabel);
/* 199 */     gbc.fill = 2;
/* 200 */     GridBagLayoutHelper.add(1, 0, 1.0D, 0.0D, 1, 1, 5, 0, 0, 0, gbc, this, this.strategyComboBox);
/* 201 */     gbc.fill = 0;
/* 202 */     GridBagLayoutHelper.add(2, 0, 0.0D, 0.0D, 1, 1, 5, 0, 0, 0, gbc, this, this.openStrategyButton);
/* 203 */     GridBagLayoutHelper.add(3, 0, 0.0D, 0.0D, 1, 1, 5, 0, 0, 0, gbc, this, this.editStrategyButton);
/* 204 */     GridBagLayoutHelper.add(4, 0, 0.0D, 0.0D, 1, 1, 5, 0, 0, 0, gbc, this, this.accountButton);
/* 205 */     gbc.anchor = 18;
/* 206 */     gbc.fill = 2;
/* 207 */     GridBagLayoutHelper.add(0, 1, 1.0D, 1.0D, 5, 1, 0, 5, 0, 0, gbc, this, reportSettingsPanel);
/*     */ 
/* 209 */     this.editStrategyButton.addActionListener(new ActionListener() {
/*     */       public void actionPerformed(ActionEvent e) {
/* 211 */         ReportingPanel.StrategyObject strategyObject = (ReportingPanel.StrategyObject)ReportingPanel.this.strategyComboBox.getSelectedItem();
/* 212 */         StrategyWrapper strategyWrapper = strategyObject.strategyWrapper;
/* 213 */         if (strategyWrapper == null) {
/* 214 */           String title = LocalizationManager.getText("joption.pane.historical.tester");
/* 215 */           String message = LocalizationManager.getText("joption.pane.strategy.must.be.selected");
/* 216 */           JOptionPane.showMessageDialog(ReportingPanel.this, message, title, 1);
/*     */         }
/* 218 */         else if (!strategyWrapper.isEditable()) {
/* 219 */           String title = LocalizationManager.getText("joption.pane.historical.tester");
/* 220 */           String message = LocalizationManager.getText("joption.pane.strategy.is.not.editable");
/* 221 */           JOptionPane.showMessageDialog(ReportingPanel.this, message, title, 1);
/*     */         }
/* 224 */         else if (!ReportingPanel.this.chartTabsAndFramesController.selectServiceSourceEditor(strategyObject.servicePanelId)) {
/* 225 */           ReportingPanel.this.chartTabsAndFramesController.addServiceSourceEditor(strategyObject.servicePanelId, strategyWrapper.getSourceFile().getName(), strategyWrapper.getSourceFile(), ServiceSourceType.STRATEGY, false);
/*     */         }
/*     */       }
/*     */     });
/* 230 */     this.openStrategyButton.addActionListener(new ActionListener()
/*     */     {
/*     */       public void actionPerformed(ActionEvent e)
/*     */       {
/*     */       }
/*     */     });
/* 236 */     this.accountButton.addActionListener(new ActionListener()
/*     */     {
/*     */       public void actionPerformed(ActionEvent e) {
/* 239 */         AccountSettingsPanel panel = new AccountSettingsPanel();
/* 240 */         panel.set(ReportingPanel.this.initialDeposit, ReportingPanel.this.accountCurrency.getCurrencyCode());
/* 241 */         panel.set(ReportingPanel.this.maxLeverage, ReportingPanel.this.mcLeverage, ReportingPanel.this.mcEquity);
/* 242 */         panel.set(ReportingPanel.this.commissions, ReportingPanel.this.overnights);
/*     */ 
/* 244 */         if (panel.showModalDialog(ReportingPanel.this, LocalizationManager.getText("dialog.tester.account"))) {
/* 245 */           ReportingPanel.access$802(ReportingPanel.this, panel.getInitialDeposit());
/* 246 */           ReportingPanel.access$1002(ReportingPanel.this, panel.getMaxLeverage().intValue());
/* 247 */           ReportingPanel.access$1102(ReportingPanel.this, panel.getMcLeverage().intValue());
/* 248 */           ReportingPanel.access$1202(ReportingPanel.this, panel.getMcEquity());
/* 249 */           ReportingPanel.access$1302(ReportingPanel.this, panel.getCommissions());
/* 250 */           ReportingPanel.access$1402(ReportingPanel.this, panel.getOvernights());
/* 251 */           if (!panel.getAccountCurrency().equals(ReportingPanel.this.accountCurrency)) {
/* 252 */             ReportingPanel.access$902(ReportingPanel.this, panel.getAccountCurrency());
/* 253 */             ReportingPanel.this.accountCurrencyListener.actionPerformed(new ActionEvent(this, 1001, ""));
/*     */           }
/*     */         }
/*     */       }
/*     */     });
/* 259 */     this.visualModeCheckBox.addItemListener(new ItemListener()
/*     */     {
/*     */       public void itemStateChanged(ItemEvent e) {
/* 262 */         ReportingPanel.this.updateEquityBalanceState();
/*     */       }
/*     */     });
/* 265 */     this.optimizationCheckBox.addItemListener(new ItemListener()
/*     */     {
/*     */       public void itemStateChanged(ItemEvent e) {
/* 268 */         ReportingPanel.this.updateOptimizationState();
/*     */       }
/*     */     });
/* 271 */     updateEquityBalanceState();
/* 272 */     updateOptimizationState();
/*     */   }
/*     */ 
/*     */   private void updateEquityBalanceState() {
/* 276 */     boolean vmSelected = this.visualModeCheckBox.isSelected();
/* 277 */     boolean vmEnabled = this.visualModeCheckBox.isEnabled();
/* 278 */     this.chbShowBalanceGraph.setEnabled((vmSelected) && (vmEnabled));
/* 279 */     this.chbShowEquityGraph.setEnabled((vmSelected) && (vmEnabled));
/* 280 */     this.chbShowProfitLossGraph.setEnabled((vmSelected) && (vmEnabled));
/* 281 */     if (!vmSelected) {
/* 282 */       this.chbShowBalanceGraph.setSelected(false);
/* 283 */       this.chbShowEquityGraph.setSelected(false);
/* 284 */       this.chbShowProfitLossGraph.setSelected(false);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void updateOptimizationState() {
/* 289 */     boolean optimizationOn = isOptimizationOn();
/* 290 */     this.visualModeCheckBox.setEnabled(!optimizationOn);
/* 291 */     this.reportCheckBox.setEnabled(!optimizationOn);
/* 292 */     if (optimizationOn) {
/* 293 */       this.visualModeCheckBox.setSelected(false);
/* 294 */       this.reportCheckBox.setSelected(true);
/*     */     }
/* 296 */     this.messagesCheckBox.setEnabled(!optimizationOn);
/* 297 */     this.saveMessagesPanel.setSaveEnabled(!optimizationOn);
/* 298 */     if (optimizationOn) {
/* 299 */       this.messagesCheckBox.setSelected(false);
/* 300 */       this.saveMessagesPanel.setSaveMessagesToFile(false);
/*     */     }
/* 302 */     fireStateChanged();
/*     */   }
/*     */ 
/*     */   public void set(StrategyTestBean strategyTestBean)
/*     */   {
/* 307 */     String currencyCode = strategyTestBean.getAccountCurrency();
/* 308 */     if (currencyCode != null) {
/* 309 */       this.accountCurrency = AbstractCurrencyConverter.findMajor(currencyCode);
/*     */     }
/* 311 */     if (this.accountCurrency == null) {
/* 312 */       this.accountCurrency = Instrument.EURUSD.getSecondaryCurrency();
/*     */     }
/*     */ 
/* 315 */     this.initialDeposit = Math.max(strategyTestBean.getInitialDeposit(), 0.0D);
/* 316 */     this.maxLeverage = Math.max(strategyTestBean.getMaxLeverage(), 1);
/* 317 */     this.mcLeverage = Math.max(strategyTestBean.getMcLeverage(), 1);
/* 318 */     this.mcEquity = Math.max(strategyTestBean.getMcEquity(), 0.0D);
/* 319 */     if (strategyTestBean.getCommissions() != null) {
/* 320 */       this.commissions = strategyTestBean.getCommissions();
/*     */     }
/* 322 */     if (strategyTestBean.getOvernights() != null) {
/* 323 */       this.overnights = strategyTestBean.getOvernights();
/*     */     }
/*     */ 
/* 326 */     this.messagesCheckBox.setSelected(!strategyTestBean.isMessagesDisabled());
/* 327 */     this.saveMessagesPanel.set(strategyTestBean);
/*     */ 
/* 329 */     this.visualModeCheckBox.setSelected(strategyTestBean.isVisualMode());
/* 330 */     this.optimizationCheckBox.setSelected(strategyTestBean.isOptimization());
/* 331 */     this.chbShowBalanceGraph.setSelected(strategyTestBean.isShowBalance());
/* 332 */     this.chbShowEquityGraph.setSelected(strategyTestBean.isShowEquity());
/* 333 */     this.chbShowProfitLossGraph.setSelected(strategyTestBean.isShowProfitLoss());
/* 334 */     updateEquityBalanceState();
/*     */   }
/*     */ 
/*     */   public void save(StrategyTestBean strategyTestBean) {
/* 338 */     strategyTestBean.setInitialDeposit(getInitialDeposit());
/* 339 */     strategyTestBean.setAccountCurrency(getAccountCurrency());
/* 340 */     strategyTestBean.setMaxLeverage((int)getMaxLeverage());
/* 341 */     strategyTestBean.setMcLeverage(getMcLeverage());
/* 342 */     strategyTestBean.setCommissions(getCommissions());
/* 343 */     strategyTestBean.setOvernights(getOvernights());
/* 344 */     strategyTestBean.setMcEquity(getMcEquity());
/* 345 */     strategyTestBean.setMessagesDisabled(!isMessagesEnabled());
/*     */ 
/* 347 */     this.saveMessagesPanel.save(strategyTestBean);
/*     */ 
/* 349 */     strategyTestBean.setEventLogEnabled(isEventLogEnabled());
/* 350 */     strategyTestBean.setProcessingStatsEnabled(isProcessingStatsEnabled());
/* 351 */     strategyTestBean.setCutFlatTicks(false);
/* 352 */     strategyTestBean.setVisualMode(isVisualMode());
/* 353 */     strategyTestBean.setShowBalance(isShowBalance());
/* 354 */     strategyTestBean.setShowEquity(isShowEquity());
/* 355 */     strategyTestBean.setShowProfitLoss(isShowProfitLoss());
/* 356 */     strategyTestBean.setOptimization(isOptimizationOn());
/*     */   }
/*     */ 
/*     */   public void setWorkspaceController(WorkspaceTreeController workspaceTreeController) {
/* 360 */     if (this.workspaceTreeController != null) {
/* 361 */       this.workspaceTreeController.removeStrategyListChangeListener(this.strategyListListener);
/*     */     }
/*     */ 
/* 364 */     this.workspaceTreeController = workspaceTreeController;
/* 365 */     if (workspaceTreeController != null) {
/* 366 */       workspaceTreeController.addStrategyListChangeListener(this.strategyListListener);
/* 367 */       this.strategyComboBoxModel.reloadStrategyList();
/*     */     }
/*     */   }
/*     */ 
/*     */   public void setStrategy(int strategyId, StrategyWrapper strategyWrapper) {
/* 372 */     this.strategyComboBoxModel.reloadStrategyList();
/* 373 */     int indexToSelect = this.strategyComboBoxModel.getIndexOfStrategyPanelId(strategyId);
/* 374 */     if (indexToSelect != -1)
/* 375 */       this.strategyComboBox.setSelectedIndex(indexToSelect);
/*     */     else
/* 377 */       this.strategyComboBox.setSelectedIndex(0);
/*     */   }
/*     */ 
/*     */   public StrategyWrapper getStrategy()
/*     */   {
/* 382 */     StrategyObject object = (StrategyObject)this.strategyComboBox.getSelectedItem();
/* 383 */     return object == null ? null : object.strategyWrapper;
/*     */   }
/*     */ 
/*     */   public boolean isShowReport() {
/* 387 */     return this.reportCheckBox.isSelected();
/*     */   }
/*     */ 
/*     */   public boolean isEventLogEnabled() {
/* 391 */     return true;
/*     */   }
/*     */ 
/*     */   public boolean isProcessingStatsEnabled() {
/* 395 */     return true;
/*     */   }
/*     */ 
/*     */   public boolean isVisualMode() {
/* 399 */     return this.visualModeCheckBox.isSelected();
/*     */   }
/*     */ 
/*     */   public boolean isShowBalance() {
/* 403 */     return this.chbShowBalanceGraph.isSelected();
/*     */   }
/*     */ 
/*     */   public boolean isShowEquity() {
/* 407 */     return this.chbShowEquityGraph.isSelected();
/*     */   }
/*     */ 
/*     */   public boolean isShowProfitLoss() {
/* 411 */     return this.chbShowProfitLossGraph.isSelected();
/*     */   }
/*     */ 
/*     */   public void enableControls(boolean b) {
/* 415 */     this.strategyLabel.setEnabled(b);
/* 416 */     this.strategyComboBox.setEnabled(b);
/* 417 */     this.editStrategyButton.setEnabled(b);
/* 418 */     this.openStrategyButton.setEnabled(b);
/* 419 */     this.accountButton.setEnabled(b);
/* 420 */     this.reportCheckBox.setEnabled(b);
/* 421 */     this.optimizationCheckBox.setEnabled(b);
/* 422 */     this.messagesCheckBox.setEnabled(b);
/* 423 */     this.visualModeCheckBox.setEnabled(b);
/* 424 */     this.saveMessagesPanel.enableControls(b);
/*     */ 
/* 427 */     updateEquityBalanceState();
/*     */   }
/*     */ 
/*     */   public void setAccountCurrencyListener(ActionListener actionListener) {
/* 431 */     this.accountCurrencyListener = actionListener;
/*     */   }
/*     */ 
/*     */   public boolean commit()
/*     */   {
/* 579 */     return this.saveMessagesPanel.commit();
/*     */   }
/*     */ 
/*     */   public boolean isOptimizationOn() {
/* 583 */     return this.optimizationCheckBox.isSelected();
/*     */   }
/*     */ 
/*     */   public void setOptimizationOn(boolean optimizationOn) {
/* 587 */     this.optimizationCheckBox.setSelected(optimizationOn);
/*     */   }
/*     */ 
/*     */   public boolean isMessagesEnabled() {
/* 591 */     return this.messagesCheckBox.isSelected();
/*     */   }
/*     */ 
/*     */   public boolean appendMessagesFile() {
/* 595 */     return this.saveMessagesPanel.appendMessagesFile();
/*     */   }
/*     */ 
/*     */   public File getMessagesFile() {
/* 599 */     return this.saveMessagesPanel.getMessagesFile();
/*     */   }
/*     */ 
/*     */   public double getInitialDeposit() {
/* 603 */     return this.initialDeposit;
/*     */   }
/*     */ 
/*     */   public Currency getAccountCurrency() {
/* 607 */     return this.accountCurrency;
/*     */   }
/*     */ 
/*     */   public double getMaxLeverage() {
/* 611 */     return this.maxLeverage;
/*     */   }
/*     */ 
/*     */   public int getMcLeverage() {
/* 615 */     return this.mcLeverage;
/*     */   }
/*     */ 
/*     */   public int getMcWeekendLeverage() {
/* 619 */     return this.mcWeekendLeverage;
/*     */   }
/*     */ 
/*     */   public double getMcEquity() {
/* 623 */     return this.mcEquity;
/*     */   }
/*     */ 
/*     */   public Commissions getCommissions() {
/* 627 */     return this.commissions;
/*     */   }
/*     */ 
/*     */   public Overnights getOvernights() {
/* 631 */     return this.overnights;
/*     */   }
/*     */ 
/*     */   private class OpenStrategyButtonAction extends OpenStrategyAction
/*     */   {
/*     */     public OpenStrategyButtonAction()
/*     */     {
/* 558 */       super(((JForexClientFormLayoutManager)GreedContext.get("layoutManager")).getWorkspaceNodeFactory(), ((ClientFormLayoutManager)GreedContext.get("layoutManager")).getChartTabsController(), (ClientSettingsStorage)GreedContext.get("settingsStorage"), null);
/*     */     }
/*     */ 
/*     */     protected Object executeInternal(Object param)
/*     */     {
/* 569 */       StrategyTreeNode node = (StrategyTreeNode)super.executeInternal(param);
/* 570 */       if (node != null) {
/* 571 */         int id = node.getId();
/* 572 */         ReportingPanel.this.setStrategy(id, node.getServiceWrapper());
/*     */       }
/* 574 */       return node;
/*     */     }
/*     */   }
/*     */ 
/*     */   private class StrategyObject
/*     */     implements Comparable<StrategyObject>
/*     */   {
/*     */     private StrategyWrapper strategyWrapper;
/*     */     private int servicePanelId;
/*     */ 
/*     */     private StrategyObject(StrategyWrapper strategyWrapper, int servicePanelId)
/*     */     {
/* 519 */       this.strategyWrapper = strategyWrapper;
/* 520 */       this.servicePanelId = servicePanelId;
/*     */     }
/*     */ 
/*     */     public String toString()
/*     */     {
/* 525 */       return this.strategyWrapper == null ? "" : this.strategyWrapper.getName();
/*     */     }
/*     */ 
/*     */     public boolean equals(Object o)
/*     */     {
/* 530 */       if (this == o) return true;
/* 531 */       if ((o == null) || (getClass() != o.getClass())) return false;
/*     */ 
/* 533 */       StrategyObject that = (StrategyObject)o;
/* 534 */       return this.servicePanelId == that.servicePanelId;
/*     */     }
/*     */ 
/*     */     public int compareTo(StrategyObject o)
/*     */     {
/* 539 */       if (o == null) {
/* 540 */         return 1;
/*     */       }
/* 542 */       StrategyWrapper wrapper = o.strategyWrapper;
/* 543 */       if (wrapper == null)
/* 544 */         return 1;
/* 545 */       if (this.strategyWrapper == null) {
/* 546 */         return -1;
/*     */       }
/* 548 */       return this.strategyWrapper.getName().compareTo(wrapper.getName());
/*     */     }
/*     */   }
/*     */ 
/*     */   private class StrategyComboBoxModel
/*     */     implements ComboBoxModel
/*     */   {
/* 435 */     private List<ListDataListener> listeners = new ArrayList();
/*     */     private ReportingPanel.StrategyObject selectedItem;
/* 437 */     private List<ReportingPanel.StrategyObject> strategies = new ArrayList();
/*     */ 
/*     */     private StrategyComboBoxModel() {
/* 440 */       this.strategies.add(new ReportingPanel.StrategyObject(ReportingPanel.this, null, -1, null));
/*     */     }
/*     */ 
/*     */     public void setSelectedItem(Object anItem)
/*     */     {
/* 445 */       this.selectedItem = ((ReportingPanel.StrategyObject)anItem);
/*     */     }
/*     */ 
/*     */     public Object getSelectedItem()
/*     */     {
/* 450 */       return this.selectedItem;
/*     */     }
/*     */ 
/*     */     public int getSize()
/*     */     {
/* 455 */       return this.strategies.size();
/*     */     }
/*     */ 
/*     */     public Object getElementAt(int index)
/*     */     {
/* 460 */       return this.strategies.get(index);
/*     */     }
/*     */ 
/*     */     public void addListDataListener(ListDataListener l)
/*     */     {
/* 465 */       this.listeners.add(l);
/*     */     }
/*     */ 
/*     */     public void removeListDataListener(ListDataListener l)
/*     */     {
/* 470 */       this.listeners.remove(l);
/*     */     }
/*     */ 
/*     */     public void reloadStrategyList()
/*     */     {
/* 475 */       this.strategies.clear();
/* 476 */       this.strategies.add(new ReportingPanel.StrategyObject(ReportingPanel.this, null, -1, null));
/* 477 */       Map strategiesMap = ReportingPanel.this.workspaceTreeController.getStrategies();
/* 478 */       for (Map.Entry entry : strategiesMap.entrySet()) {
/* 479 */         this.strategies.add(new ReportingPanel.StrategyObject(ReportingPanel.this, (StrategyWrapper)entry.getValue(), ((Integer)entry.getKey()).intValue(), null));
/*     */       }
/*     */ 
/* 482 */       Collections.sort(this.strategies);
/*     */ 
/* 485 */       if (this.selectedItem != null) {
/* 486 */         boolean removeSelection = true;
/* 487 */         for (ReportingPanel.StrategyObject strategy : this.strategies) {
/* 488 */           if (this.selectedItem.equals(strategy)) {
/* 489 */             removeSelection = false;
/* 490 */             break;
/*     */           }
/*     */         }
/* 493 */         if (removeSelection) {
/* 494 */           setSelectedItem(null);
/*     */         }
/*     */       }
/*     */ 
/* 498 */       fireContentsChanged();
/*     */     }
/*     */ 
/*     */     public void fireContentsChanged() {
/* 502 */       for (ListDataListener listener : this.listeners)
/* 503 */         listener.contentsChanged(new ListDataEvent(this, 0, 0, this.strategies.size() - 1));
/*     */     }
/*     */ 
/*     */     public int getIndexOfStrategyPanelId(int id)
/*     */     {
/* 508 */       return this.strategies.indexOf(new ReportingPanel.StrategyObject(ReportingPanel.this, null, id, null));
/*     */     }
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.strategy.ReportingPanel
 * JD-Core Version:    0.6.0
 */