/*      */ package com.dukascopy.dds2.greed.gui.component.strategy;
/*      */ 
/*      */ import com.dukascopy.api.DataType;
/*      */ import com.dukascopy.api.IAccount.AccountState;
/*      */ import com.dukascopy.api.Instrument;
/*      */ import com.dukascopy.api.OfferSide;
/*      */ import com.dukascopy.api.Period;
/*      */ import com.dukascopy.api.Unit;
/*      */ import com.dukascopy.api.impl.StrategyWrapper;
/*      */ import com.dukascopy.api.system.ITesterClient.DataLoadingMethod;
/*      */ import com.dukascopy.api.system.ITesterClient.InterpolationMethod;
/*      */ import com.dukascopy.charts.data.datacache.JForexPeriod;
/*      */ import com.dukascopy.charts.persistence.StrategyTestBean;
/*      */ import com.dukascopy.charts.utils.ChartsLocalizator;
/*      */ import com.dukascopy.dds2.greed.GreedContext;
/*      */ import com.dukascopy.dds2.greed.actions.StrategyTesterAction;
/*      */ import com.dukascopy.dds2.greed.agent.strategy.ide.api.ServiceSourceType;
/*      */ import com.dukascopy.dds2.greed.agent.strategy.params.Variable;
/*      */ import com.dukascopy.dds2.greed.agent.strategy.tester.ExecutionControl;
/*      */ import com.dukascopy.dds2.greed.agent.strategy.tester.ExecutionControlEvent;
/*      */ import com.dukascopy.dds2.greed.agent.strategy.tester.ExecutionControlListener;
/*      */ import com.dukascopy.dds2.greed.agent.strategy.tester.TesterAccount;
/*      */ import com.dukascopy.dds2.greed.agent.strategy.tester.TesterChartData;
/*      */ import com.dukascopy.dds2.greed.gui.JForexClientFormLayoutManager;
/*      */ import com.dukascopy.dds2.greed.gui.component.chart.holders.IChartTabsAndFramesController;
/*      */ import com.dukascopy.dds2.greed.gui.component.message.MessageList;
/*      */ import com.dukascopy.dds2.greed.gui.component.message.MessagePanel;
/*      */ import com.dukascopy.dds2.greed.gui.component.strategy.tab.StrategiesContentPane;
/*      */ import com.dukascopy.dds2.greed.gui.component.strategy.tab.mediator.StrategyNewBean;
/*      */ import com.dukascopy.dds2.greed.gui.component.tree.WorkspaceTreeController;
/*      */ import com.dukascopy.dds2.greed.gui.l10n.Localizable;
/*      */ import com.dukascopy.dds2.greed.gui.l10n.LocalizationManager;
/*      */ import com.dukascopy.dds2.greed.gui.l10n.components.JLocalizableButton;
/*      */ import com.dukascopy.dds2.greed.gui.l10n.components.JLocalizableCheckBox;
/*      */ import com.dukascopy.dds2.greed.gui.l10n.components.JLocalizableComboBox;
/*      */ import com.dukascopy.dds2.greed.gui.resizing.ResizingManager.ComponentSize;
/*      */ import com.dukascopy.dds2.greed.gui.resizing.components.ResizableIcon;
/*      */ import com.dukascopy.dds2.greed.gui.settings.ClientSettingsStorage;
/*      */ import com.dukascopy.dds2.greed.model.AccountStatement;
/*      */ import com.dukascopy.dds2.greed.model.Notification;
/*      */ import com.dukascopy.dds2.greed.util.CompilerUtils;
/*      */ import com.dukascopy.dds2.greed.util.EnumConverter;
/*      */ import com.dukascopy.dds2.greed.util.GridBagLayoutHelper;
/*      */ import com.dukascopy.transport.common.msg.request.AccountInfoMessage;
/*      */ import java.awt.BorderLayout;
/*      */ import java.awt.Component;
/*      */ import java.awt.Dimension;
/*      */ import java.awt.FlowLayout;
/*      */ import java.awt.Font;
/*      */ import java.awt.GridBagConstraints;
/*      */ import java.awt.GridBagLayout;
/*      */ import java.awt.Insets;
/*      */ import java.awt.event.ActionEvent;
/*      */ import java.awt.event.ActionListener;
/*      */ import java.io.File;
/*      */ import java.text.SimpleDateFormat;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Collections;
/*      */ import java.util.HashMap;
/*      */ import java.util.LinkedHashSet;
/*      */ import java.util.List;
/*      */ import java.util.Map;
/*      */ import java.util.Set;
/*      */ import javax.swing.AbstractListModel;
/*      */ import javax.swing.BorderFactory;
/*      */ import javax.swing.ComboBoxModel;
/*      */ import javax.swing.DefaultListCellRenderer;
/*      */ import javax.swing.JButton;
/*      */ import javax.swing.JCheckBox;
/*      */ import javax.swing.JComboBox;
/*      */ import javax.swing.JFormattedTextField;
/*      */ import javax.swing.JLabel;
/*      */ import javax.swing.JList;
/*      */ import javax.swing.JOptionPane;
/*      */ import javax.swing.JPanel;
/*      */ import javax.swing.JProgressBar;
/*      */ import javax.swing.JSpinner;
/*      */ import javax.swing.JSpinner.NumberEditor;
/*      */ import javax.swing.ListCellRenderer;
/*      */ import javax.swing.SpinnerNumberModel;
/*      */ import javax.swing.SwingUtilities;
/*      */ import javax.swing.border.Border;
/*      */ import javax.swing.border.EmptyBorder;
/*      */ import javax.swing.event.ListDataEvent;
/*      */ import javax.swing.event.ListDataListener;
/*      */ import javax.swing.event.TableModelEvent;
/*      */ import javax.swing.event.TableModelListener;
/*      */ import javax.swing.table.TableModel;
/*      */ import javax.swing.text.DefaultFormatter;
/*      */ import org.slf4j.Logger;
/*      */ import org.slf4j.LoggerFactory;
/*      */ 
/*      */ public class StrategyTestPanel extends JPanel
/*      */ {
/*  136 */   private static final Logger LOGGER = LoggerFactory.getLogger(StrategyTestPanel.class);
/*      */   private static final String ID_JT_TESTER_MESSAGE_PANEL = "ID_JT_TESTER_MESSAGEPANEL";
/*      */   private static final String ID_JT_TESTER_OPTIMIZER_PANEL = "ID_JT_TESTER_OPTIMIZER_PANEL";
/*      */   private static final String ID_JT_TESTER_DATALOADING_PANEL = "ID_JT_TESTER_DATALOAD_PANEL";
/*      */   private static final String ID_JT_TESTER_INTERPOLATION_COMBO = "ID_JT_TESTER_INTERPOLATION_COMBO";
/*  144 */   private ResizableIcon MESSAGES_COPY_ICON = new ResizableIcon("messages_copy.png");
/*  145 */   private ResizableIcon MESSAGES_COPY_ICON_FADED = new ResizableIcon("messages_copy_inactive.png");
/*  146 */   private ResizableIcon MESSAGES_CLEAR_ICON = new ResizableIcon("messages_clear.png");
/*  147 */   private ResizableIcon MESSAGES_CLEAR_ICON_FADED = new ResizableIcon("messages_clear_inactive.png");
/*      */   private JComboBox strategyComboBox;
/*      */   private JSpinner pipsSpinner;
/*  152 */   private int pipsSpinnerWidth = 50;
/*  153 */   private int pipsSpinnerLeftOffset = 3;
/*      */   private SimpleLocalizableComboBox<TimeUnitType> timeUnitTypeCombo;
/*  156 */   private int timeUnitTypeComboWidth = 100;
/*  157 */   private int timeUnitTypeComboOffset = 3;
/*      */   private JSpinner timeUnitsCountSpinner;
/*  160 */   private int timeUnitsCountSpinnerWidth = 50;
/*  161 */   private int timeUnitsCountSpinnerOffset = 3;
/*      */ 
/*  163 */   private Integer prefferedComboBoxesHeight = null;
/*  164 */   private StrategyComboBoxModel strategyComboBoxModel = new StrategyComboBoxModel(null);
/*      */   private SimpleLocalizableComboBox<TesterTimeRange> rangeComboBox;
/*      */   private SimpleLocalizableComboBox<ITesterClient.DataLoadingMethod> dataLoadingMethodComboBox;
/*      */   private SimpleLocalizableComboBox<ITesterClient.InterpolationMethod> interpolMethodsComboBox;
/*      */   private SimpleLocalizableComboBox<OfferSide> offerSideComboBox;
/*      */   private SimpleLocalizableComboBox<TesterPeriod> periodComboBox;
/*      */   private JButton testerSettingsButton;
/*      */   private JButton instrumentsButton;
/*      */   private JButton accountButton;
/*      */   private JPanel dataLoadingPanel;
/*      */   private JCheckBox optimizationCheckBox;
/*      */   private JCheckBox visualModeCheckbox;
/*      */   private JCheckBox showMessagesCheckBox;
/*      */   private CardLayoutPanel cardLayoutPanel;
/*      */   private JPanel pnlMessageButtons;
/*  183 */   private TesterParameters testerParameters = new TesterParameters();
/*      */   private ExecutionControl executionControl;
/*      */   private JProgressBar progressBar;
/*      */   private JLabel progressLabel;
/*      */   private CardLayoutPanel pnlOptimizerAndMessages;
/*      */   private MessagePanel pnlMessages;
/*      */   private JLocalizableButton btnCopyMessage;
/*      */   private JLocalizableButton btnClear;
/*      */   private JLocalizableButton btnMessages;
/*      */   public OptimizerPanel pnlOptimizer;
/*      */   public TesterExecutionControlPanel testerExecutionCtrlPanel;
/*      */   private volatile boolean busy;
/*      */   private volatile boolean cancelDataLoading;
/*      */   private volatile boolean cancelStrategyTest;
/*      */   private IChartTabsAndFramesController chartTabsAndFramesController;
/*      */   private WorkspaceTreeController workspaceTreeController;
/*      */   private TesterNotificationUtils notificationUtils;
/*      */   private ActionListener cancelListener;
/*      */   private ActionListener startListener;
/*      */   private HashMap<String, Variable[]> optimizationParameters;
/*  208 */   private double balanceDropDown = 0.3D;
/*  209 */   private final Map<String, String> translatingStringConstants = new HashMap();
/*      */   private JButton btnOpen;
/*      */   private JButton btnEdit;
/*      */   private RangeSelectionDialog customRangeDialog;
/*  215 */   private boolean processCustomRangeSelection = true;
/*      */ 
/*      */   public StrategyTestPanel(IChartTabsAndFramesController chartTabsAndFramesController, WorkspaceTreeController workspaceTreeController)
/*      */   {
/*  219 */     this.translatingStringConstants.put("TICKS_WITH_PRICE_DIFFERENCE_IN_PIPS", "tester.ticks.with.price.difference.in.pips");
/*  220 */     this.translatingStringConstants.put("TICKS_WITH_TIME_INTERVAL", "tester.ticks.with.time.interval");
/*      */ 
/*  222 */     this.executionControl = new ExecutionControl();
/*      */ 
/*  224 */     this.chartTabsAndFramesController = chartTabsAndFramesController;
/*  225 */     this.workspaceTreeController = workspaceTreeController;
/*  226 */     this.notificationUtils = new TesterNotificationUtils(this);
/*      */ 
/*  228 */     Set instruments = new LinkedHashSet();
/*  229 */     instruments.add(Instrument.EURUSD);
/*  230 */     this.testerParameters.setInstruments(instruments);
/*      */   }
/*      */ 
/*      */   public ActionListener getCancelListener() {
/*  234 */     return this.cancelListener;
/*      */   }
/*      */ 
/*      */   public ActionListener getStartListener() {
/*  238 */     return this.startListener;
/*      */   }
/*      */ 
/*      */   public void selectStrategy(StrategyNewBean strategyBean)
/*      */   {
/*  243 */     if (strategyBean != null) {
/*  244 */       for (int i = 0; i < this.strategyComboBox.getItemCount(); i++) {
/*  245 */         StrategyObject so = (StrategyObject)this.strategyComboBox.getItemAt(i);
/*  246 */         if (so.getServicePanelId() == strategyBean.getId().intValue()) {
/*  247 */           this.strategyComboBox.setSelectedItem(so);
/*  248 */           break;
/*      */         }
/*      */       }
/*      */     }
/*      */ 
/*  253 */     processStrategySelection();
/*      */   }
/*      */ 
/*      */   public void reloadStrategies() {
/*  257 */     this.strategyComboBoxModel.loadCompiledStrategies();
/*      */   }
/*      */ 
/*      */   private JComboBox createStrategyComboBox() {
/*  261 */     EmptyBorder border = new EmptyBorder(new Insets(0, 3, 0, 0));
/*  262 */     StringBuffer hint = new StringBuffer(LocalizationManager.getText("tester.hint.select.strategy"));
/*  263 */     this.strategyComboBox = new JLocalizableComboBox(this.strategyComboBoxModel, null, hint)
/*      */     {
/*      */       public void translate() {
/*  266 */         this.val$hint.delete(0, this.val$hint.length());
/*  267 */         this.val$hint.append(LocalizationManager.getText("tester.hint.select.strategy"));
/*      */       }
/*      */     };
/*  271 */     this.strategyComboBox.setToolTipText("combo.strategy.tooltip");
/*  272 */     this.strategyComboBox.setMaximumRowCount(15);
/*  273 */     this.strategyComboBoxModel.loadCompiledStrategies();
/*  274 */     this.strategyComboBox.setSelectedIndex(0);
/*      */ 
/*  276 */     Font origFont = this.strategyComboBox.getFont();
/*  277 */     ListCellRenderer origRenderer = this.strategyComboBox.getRenderer();
/*      */ 
/*  279 */     this.strategyComboBox.setRenderer(new ListCellRenderer(hint, origRenderer, border, origFont)
/*      */     {
/*      */       public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
/*  282 */         if ((value == null) || ((value.toString().length() == 0) && (index == -1)))
/*      */         {
/*  284 */           return getHintComponent(list, this.val$hint.toString(), index, isSelected, cellHasFocus);
/*      */         }
/*  286 */         if ((value.toString().length() > 0) && (index == -1))
/*      */         {
/*  288 */           return getSelectedStrategyComponent(list, value, index, isSelected, cellHasFocus);
/*      */         }
/*      */ 
/*  291 */         return getStrategyComponent(list, value, index, isSelected, cellHasFocus);
/*      */       }
/*      */ 
/*      */       private Component getHintComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus)
/*      */       {
/*  297 */         JLabel label = (JLabel)this.val$origRenderer.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
/*  298 */         if (StrategyTestPanel.this.strategyComboBox.getFont().getStyle() != 2) {
/*  299 */           StrategyTestPanel.this.strategyComboBox.setFont(label.getFont().deriveFont(2));
/*      */         }
/*      */ 
/*  302 */         return label;
/*      */       }
/*      */ 
/*      */       private Component getSelectedStrategyComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
/*  306 */         StrategyTestPanel.StrategyObject strategyObject = (StrategyTestPanel.StrategyObject)value;
/*  307 */         String strategyName = strategyObject.toString();
/*  308 */         strategyName = StrategyTestPanel.this.getSelectedInstrumentsCaption(strategyName);
/*  309 */         JLabel label = (JLabel)this.val$origRenderer.getListCellRendererComponent(list, strategyName, index, isSelected, cellHasFocus);
/*      */ 
/*  311 */         if (label.getBorder() != this.val$border) {
/*  312 */           label.setBorder(this.val$border);
/*      */         }
/*  314 */         return label;
/*      */       }
/*      */ 
/*      */       private Component getStrategyComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
/*  318 */         StrategyTestPanel.StrategyObject strategyObject = (StrategyTestPanel.StrategyObject)value;
/*  319 */         if (StrategyTestPanel.this.strategyComboBox.getFont() != this.val$origFont) {
/*  320 */           StrategyTestPanel.this.strategyComboBox.setFont(this.val$origFont);
/*      */         }
/*      */ 
/*  323 */         if (strategyObject.getServicePanelId() == -1) {
/*  324 */           String caption = this.val$hint + " ...";
/*  325 */           JLabel label = (JLabel)this.val$origRenderer.getListCellRendererComponent(list, caption, index, isSelected, cellHasFocus);
/*  326 */           label.setFont(label.getFont().deriveFont(1));
/*      */ 
/*  328 */           if (label.getBorder() != this.val$border) {
/*  329 */             label.setBorder(this.val$border);
/*      */           }
/*  331 */           return label;
/*      */         }
/*  333 */         JLabel label = (JLabel)this.val$origRenderer.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
/*  334 */         return label;
/*      */       }
/*      */     });
/*  339 */     this.strategyComboBox.addActionListener(new ActionListener()
/*      */     {
/*      */       public void actionPerformed(ActionEvent e) {
/*  342 */         StrategyTestPanel.this.processStrategySelection();
/*      */       }
/*      */     });
/*  346 */     return this.strategyComboBox;
/*      */   }
/*      */ 
/*      */   private void processStrategySelection() {
/*  350 */     StrategyObject strategyObject = (StrategyObject)this.strategyComboBox.getSelectedItem();
/*      */ 
/*  352 */     if (strategyObject != null)
/*      */     {
/*  354 */       this.optimizationCheckBox.setEnabled(strategyObject.hasParameters());
/*  355 */       processOptimizationBox();
/*      */ 
/*  357 */       this.btnEdit.setEnabled((strategyObject.getWrapper() != null) && (strategyObject.getWrapper().getSourceFile() != null));
/*      */ 
/*  359 */       if (strategyObject.getServicePanelId() == -1) {
/*  360 */         this.btnOpen.requestFocus();
/*      */ 
/*  362 */         JForexClientFormLayoutManager clientFormLayoutManager = (JForexClientFormLayoutManager)GreedContext.get("layoutManager");
/*  363 */         List addedStrategies = clientFormLayoutManager.getStrategiesPanel().openStrategiesSelection(false);
/*      */ 
/*  365 */         if ((addedStrategies != null) && (addedStrategies.size() > 0))
/*      */         {
/*  367 */           selectStrategy((StrategyNewBean)addedStrategies.get(addedStrategies.size() - 1));
/*      */         }
/*      */       }
/*      */     } else {
/*  371 */       this.btnEdit.setEnabled(false);
/*      */     }
/*      */   }
/*      */ 
/*      */   private String getSelectedInstrumentsCaption(String strategyName) {
/*  376 */     StringBuffer buffer = new StringBuffer(128);
/*      */ 
/*  378 */     buffer.append(strategyName);
/*  379 */     if ((this.testerParameters != null) && (this.testerParameters.getInstruments() != null) && (this.testerParameters.getInstruments().size() > 0)) {
/*  380 */       buffer.append("  -  ");
/*  381 */       for (Instrument instrument : this.testerParameters.getInstruments()) {
/*  382 */         buffer.append(instrument.toString() + ", ");
/*      */       }
/*  384 */       buffer.delete(buffer.length() - 2, buffer.length());
/*      */     }
/*      */ 
/*  387 */     return buffer.toString();
/*      */   }
/*      */ 
/*      */   private SimpleLocalizableComboBox<TesterTimeRange> createRangeComboBox() {
/*  391 */     this.rangeComboBox = new SimpleLocalizableComboBox(TesterTimeRange.values());
/*      */ 
/*  393 */     this.rangeComboBox.setRenderer(new RangeLocalizableCellRenderer(this.rangeComboBox, "tester.hint.select.time.range") {
/*  394 */       private SimpleDateFormat format = new SimpleDateFormat("yyyy.MM.dd");
/*      */ 
/*      */       protected String getLocalizedText(Object value)
/*      */       {
/*  398 */         if (value != TesterTimeRange.CUSTOM_PERIOD_TEMPLATE) {
/*  399 */           return LocalizationManager.getText(value.toString());
/*      */         }
/*  401 */         return LocalizationManager.getText("tester.custom.period");
/*      */       }
/*      */ 
/*      */       protected String getSelectedComponentCaption()
/*      */       {
/*  407 */         if (StrategyTestPanel.this.rangeComboBox.getSelectedItem() == TesterTimeRange.CUSTOM_PERIOD_TEMPLATE) {
/*  408 */           String stringFrom = this.format.format(Long.valueOf(StrategyTestPanel.this.testerParameters.getTesterTimeRange().getDateFrom()));
/*  409 */           String stringTo = this.format.format(Long.valueOf(StrategyTestPanel.this.testerParameters.getTesterTimeRange().getDateTo()));
/*  410 */           return stringFrom + " - " + stringTo;
/*      */         }
/*  412 */         return null;
/*      */       }
/*      */     });
/*  417 */     long from = this.testerParameters.getTesterTimeRange().getDateFrom();
/*  418 */     long to = this.testerParameters.getTesterTimeRange().getDateTo();
/*  419 */     this.customRangeDialog = RangeSelectionDialog.createDialog(this, "dialog.select.range", Long.valueOf(from), Long.valueOf(to));
/*  420 */     this.rangeComboBox.addActionListener(new ActionListener()
/*      */     {
/*      */       public void actionPerformed(ActionEvent e) {
/*  423 */         if ((StrategyTestPanel.this.rangeComboBox.isLangChanged()) && (StrategyTestPanel.this.rangeComboBox.getSelectedItem() == TesterTimeRange.CUSTOM_PERIOD_TEMPLATE)) {
/*  424 */           StrategyTestPanel.this.rangeComboBox.setLangChanged(false);
/*  425 */           return;
/*      */         }
/*  427 */         if (StrategyTestPanel.this.rangeComboBox.isLangChanged()) {
/*  428 */           StrategyTestPanel.this.rangeComboBox.setLangChanged(false);
/*      */         }
/*      */ 
/*  432 */         StrategyTestPanel.SimpleLocalizableComboBox combo = (StrategyTestPanel.SimpleLocalizableComboBox)e.getSource();
/*  433 */         TesterTimeRange testerTimeRange = (TesterTimeRange)combo.getSelectedItem();
/*  434 */         StrategyTestPanel.this.testerParameters.setTesterTimeRange(testerTimeRange);
/*      */ 
/*  436 */         if ((testerTimeRange == TesterTimeRange.CUSTOM_PERIOD_TEMPLATE) && (StrategyTestPanel.this.processCustomRangeSelection))
/*      */         {
/*  438 */           if (StrategyTestPanel.this.customRangeDialog.showModal()) {
/*  439 */             StrategyTestPanel.this.testerParameters.getTesterTimeRange().setDateFrom(StrategyTestPanel.this.customRangeDialog.getDateFrom());
/*  440 */             StrategyTestPanel.this.testerParameters.getTesterTimeRange().setDateTo(StrategyTestPanel.this.customRangeDialog.getDateTo());
/*  441 */             StrategyTestPanel.this.rangeComboBox.repaint();
/*      */           }
/*  443 */           else if ((testerTimeRange.getDateFrom() == -9223372036854775808L) || (testerTimeRange.getDateTo() == -9223372036854775808L)) {
/*  444 */             TesterTimeRange tempTesterTimeRange = TesterTimeRange.LAST_DAY;
/*  445 */             tempTesterTimeRange.recalculateTimeRange();
/*  446 */             testerTimeRange.setDateFrom(tempTesterTimeRange.getDateFrom());
/*  447 */             testerTimeRange.setDateTo(tempTesterTimeRange.getDateTo());
/*      */           }
/*      */         }
/*      */       }
/*      */     });
/*  454 */     this.rangeComboBox.setPreferredSize(new Dimension(this.rangeComboBox.getPreferredSize().width, this.prefferedComboBoxesHeight.intValue()));
/*      */ 
/*  456 */     return this.rangeComboBox;
/*      */   }
/*      */ 
/*      */   private void addEmptyBorder() {
/*  460 */     Border border = BorderFactory.createEmptyBorder(15, 15, 15, 15);
/*  461 */     setBorder(border);
/*      */   }
/*      */ 
/*      */   private JPanel getProgressBarPanel() {
/*  465 */     JPanel panel = new JPanel(new BorderLayout(10, 0));
/*  466 */     this.progressBar = new JProgressBar(0, 100)
/*      */     {
/*      */       public Dimension getMaximumSize() {
/*  469 */         Dimension size = super.getMaximumSize();
/*  470 */         size.width = 0;
/*  471 */         return size;
/*      */       }
/*      */ 
/*      */       public Dimension getMinimumSize() {
/*  475 */         Dimension size = super.getMinimumSize();
/*  476 */         size.width = 0;
/*  477 */         return size;
/*      */       }
/*      */ 
/*      */       public Dimension getPreferredSize() {
/*  481 */         Dimension size = super.getPreferredSize();
/*  482 */         size.width = 0;
/*  483 */         return size;
/*      */       }
/*      */     };
/*  486 */     this.progressBar.setValue(0);
/*  487 */     this.progressBar.setString("");
/*  488 */     this.progressBar.setStringPainted(true);
/*  489 */     this.progressLabel = new JLabel("0%");
/*  490 */     panel.add(this.progressBar, "Center");
/*  491 */     panel.add(this.progressLabel, "East");
/*      */ 
/*  493 */     return panel;
/*      */   }
/*      */ 
/*      */   private JPanel getExecutionControlButtonsPanel() {
/*  497 */     this.testerExecutionCtrlPanel = new TesterExecutionControlPanel(getExecutionControl(), this.startListener, this.cancelListener);
/*  498 */     return this.testerExecutionCtrlPanel;
/*      */   }
/*      */ 
/*      */   private JPanel createCardLayoutPanel()
/*      */   {
/*  503 */     this.cardLayoutPanel = new CardLayoutPanel();
/*  504 */     this.cardLayoutPanel.add(createDataLoadingPanel(), "ID_JT_TESTER_DATALOAD_PANEL");
/*      */ 
/*  506 */     ITesterClient.InterpolationMethod[] methods = { ITesterClient.InterpolationMethod.FOUR_TICKS, ITesterClient.InterpolationMethod.CUBIC_SPLINE, ITesterClient.InterpolationMethod.OPEN_TICK, ITesterClient.InterpolationMethod.CLOSE_TICK };
/*      */ 
/*  513 */     this.interpolMethodsComboBox = new SimpleLocalizableComboBox(methods);
/*  514 */     this.interpolMethodsComboBox.setRenderer(new SimpleLocalizableCellRenderer(this.interpolMethodsComboBox, "tester.hint.select.interpolation.method"));
/*  515 */     this.interpolMethodsComboBox.setPreferredSize(new Dimension(this.interpolMethodsComboBox.getPreferredSize().width, this.prefferedComboBoxesHeight.intValue()));
/*      */ 
/*  517 */     this.cardLayoutPanel.add(this.interpolMethodsComboBox, "ID_JT_TESTER_INTERPOLATION_COMBO");
/*  518 */     this.cardLayoutPanel.showComponent("ID_JT_TESTER_DATALOAD_PANEL");
/*  519 */     return this.cardLayoutPanel;
/*      */   }
/*      */ 
/*      */   private JPanel createDataLoadingPanel() {
/*  523 */     this.dataLoadingPanel = new JPanel();
/*  524 */     createPipsSpinner();
/*      */ 
/*  526 */     createTimeUnitTypeCombo();
/*  527 */     createTimeUnitsCountSpinner();
/*      */ 
/*  529 */     this.dataLoadingPanel.setLayout(new GridBagLayout());
/*  530 */     GridBagConstraints gbc = new GridBagConstraints();
/*  531 */     gbc.fill = 2;
/*  532 */     gbc.anchor = 18;
/*  533 */     GridBagLayoutHelper.add(0, 0, 1.0D, 0.0D, 1, 1, 0, 0, 0, 0, gbc, this.dataLoadingPanel, this.dataLoadingMethodComboBox);
/*  534 */     GridBagLayoutHelper.add(1, 0, 0.0D, 0.0D, 1, 1, this.pipsSpinnerLeftOffset, 0, 0, 0, gbc, this.dataLoadingPanel, this.pipsSpinner);
/*  535 */     GridBagLayoutHelper.add(2, 0, 0.0D, 0.0D, 1, 1, this.timeUnitTypeComboOffset, 0, 0, 0, gbc, this.dataLoadingPanel, this.timeUnitTypeCombo);
/*  536 */     GridBagLayoutHelper.add(3, 0, 0.0D, 0.0D, 1, 1, this.timeUnitsCountSpinnerOffset, 0, 0, 0, gbc, this.dataLoadingPanel, this.timeUnitsCountSpinner);
/*      */ 
/*  538 */     return this.dataLoadingPanel;
/*      */   }
/*      */ 
/*      */   private void createPipsSpinner() {
/*  542 */     SpinnerNumberModel spinnerNumberModel = new SpinnerNumberModel(1, 1, 1000000, 1);
/*  543 */     this.pipsSpinner = new JSpinner(spinnerNumberModel);
/*  544 */     JSpinner.NumberEditor jsEditor = (JSpinner.NumberEditor)this.pipsSpinner.getEditor();
/*  545 */     DefaultFormatter formatter = (DefaultFormatter)jsEditor.getTextField().getFormatter();
/*  546 */     formatter.setAllowsInvalid(false);
/*      */ 
/*  548 */     this.pipsSpinner.setPreferredSize(new Dimension(this.pipsSpinnerWidth, this.prefferedComboBoxesHeight.intValue()));
/*  549 */     this.pipsSpinner.setVisible(false);
/*      */   }
/*      */ 
/*      */   private void createTimeUnitsCountSpinner() {
/*  553 */     SpinnerNumberModel spinnerNumberModel = new SpinnerNumberModel(1, 1, 100, 1);
/*  554 */     this.timeUnitsCountSpinner = new JSpinner(spinnerNumberModel);
/*  555 */     JSpinner.NumberEditor jsEditor = (JSpinner.NumberEditor)this.timeUnitsCountSpinner.getEditor();
/*  556 */     DefaultFormatter formatter = (DefaultFormatter)jsEditor.getTextField().getFormatter();
/*  557 */     formatter.setAllowsInvalid(false);
/*      */ 
/*  559 */     this.timeUnitsCountSpinner.setPreferredSize(new Dimension(this.timeUnitsCountSpinnerWidth, this.prefferedComboBoxesHeight.intValue()));
/*  560 */     this.timeUnitsCountSpinner.setVisible(false);
/*      */   }
/*      */ 
/*      */   private void createTimeUnitTypeCombo() {
/*  564 */     TimeUnitType[] timeUnits = { TimeUnitType.Seconds, TimeUnitType.Minutes, TimeUnitType.Hours, TimeUnitType.Days };
/*      */ 
/*  571 */     this.timeUnitTypeCombo = new SimpleLocalizableComboBox(timeUnits);
/*  572 */     this.timeUnitTypeCombo.setPreferredSize(new Dimension(this.timeUnitTypeComboWidth, this.prefferedComboBoxesHeight.intValue()));
/*  573 */     this.timeUnitTypeCombo.setVisible(false);
/*  574 */     this.timeUnitTypeCombo.setSelectedIndex(0);
/*      */ 
/*  576 */     this.timeUnitTypeCombo.addActionListener(new ActionListener()
/*      */     {
/*      */       public void actionPerformed(ActionEvent e) {
/*  579 */         StrategyTestPanel.this.timeUnitsCountSpinner.setValue(Integer.valueOf(1));
/*      */       }
/*      */     });
/*  583 */     this.timeUnitTypeCombo.setRenderer(new DefaultListCellRenderer()
/*      */     {
/*      */       public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus)
/*      */       {
/*      */         String labelText;
/*      */         String labelText;
/*  587 */         if (value == null) {
/*  588 */           labelText = "null";
/*      */         }
/*      */         else
/*      */         {
/*      */           String labelText;
/*  589 */           if ((value instanceof StrategyTestPanel.TimeUnitType)) {
/*  590 */             StrategyTestPanel.TimeUnitType ticksFilterTimeUnitType = (StrategyTestPanel.TimeUnitType)value;
/*  591 */             labelText = LocalizationManager.getText(ticksFilterTimeUnitType.getTimeKey());
/*      */           } else {
/*  593 */             labelText = value.toString();
/*      */           }
/*      */         }
/*  595 */         return super.getListCellRendererComponent(list, labelText, index, isSelected, cellHasFocus);
/*      */       } } );
/*      */   }
/*      */ 
/*      */   public void build() {
/*  601 */     this.startListener = new ActionListener()
/*      */     {
/*      */       public void actionPerformed(ActionEvent e) {
/*  604 */         StrategyTestPanel.this.strategyStartActionPerformed(e);
/*      */       }
/*      */     };
/*  608 */     this.cancelListener = new ActionListener()
/*      */     {
/*      */       public void actionPerformed(ActionEvent e) {
/*  611 */         StrategyTestPanel.access$1002(StrategyTestPanel.this, true);
/*      */       }
/*      */     };
/*  615 */     addEmptyBorder();
/*  616 */     setLayout(new GridBagLayout());
/*      */ 
/*  618 */     createConsoleAndOptimizerPanels();
/*      */ 
/*  620 */     GridBagConstraints gbc = new GridBagConstraints();
/*  621 */     gbc.fill = 2;
/*  622 */     gbc.anchor = 18;
/*      */ 
/*  625 */     this.strategyComboBox = createStrategyComboBox();
/*  626 */     GridBagLayoutHelper.add(0, 0, 1.0D, 0.0D, 1, 1, 0, 0, 0, 0, gbc, this, this.strategyComboBox);
/*  627 */     this.prefferedComboBoxesHeight = Integer.valueOf(this.strategyComboBox.getPreferredSize().height);
/*      */ 
/*  629 */     this.btnOpen = new JLocalizableButton("open.button.text");
/*  630 */     this.btnOpen.addActionListener(new ActionListener()
/*      */     {
/*      */       public void actionPerformed(ActionEvent e)
/*      */       {
/*  634 */         SwingUtilities.invokeLater(new Runnable()
/*      */         {
/*      */           public void run() {
/*  637 */             JForexClientFormLayoutManager clientFormLayoutManager = (JForexClientFormLayoutManager)GreedContext.get("layoutManager");
/*  638 */             List addedStrategies = clientFormLayoutManager.getStrategiesPanel().openStrategiesSelection(false);
/*      */ 
/*  640 */             if ((addedStrategies != null) && (addedStrategies.size() > 0))
/*      */             {
/*  642 */               StrategyTestPanel.this.selectStrategy((StrategyNewBean)addedStrategies.get(addedStrategies.size() - 1));
/*      */             }
/*      */           }
/*      */         });
/*      */       }
/*      */     });
/*  649 */     this.btnEdit = new JLocalizableButton("button.edit");
/*  650 */     this.btnEdit.setEnabled(false);
/*      */ 
/*  652 */     this.btnEdit.addActionListener(new ActionListener()
/*      */     {
/*      */       public void actionPerformed(ActionEvent e)
/*      */       {
/*  657 */         if ((StrategyTestPanel.this.strategyComboBox.getSelectedItem() instanceof StrategyTestPanel.StrategyObject)) {
/*  658 */           StrategyTestPanel.StrategyObject strategy = (StrategyTestPanel.StrategyObject)StrategyTestPanel.this.strategyComboBox.getSelectedItem();
/*      */ 
/*  660 */           if (!StrategyTestPanel.this.chartTabsAndFramesController.selectPanel(strategy.getServicePanelId()))
/*      */           {
/*  662 */             File sourceFile = strategy.getWrapper().getSourceFile();
/*      */ 
/*  664 */             if ((sourceFile != null) && (sourceFile.exists()))
/*      */             {
/*  666 */               ClientSettingsStorage clientSettingsStorage = (ClientSettingsStorage)GreedContext.get("settingsStorage");
/*      */ 
/*  668 */               StrategyTestPanel.this.chartTabsAndFramesController.addServiceSourceEditor(strategy.getServicePanelId(), sourceFile.getName(), sourceFile, ServiceSourceType.STRATEGY, clientSettingsStorage.isFrameUndocked(clientSettingsStorage.getMainFramePreferencesNode(), Integer.valueOf(strategy.getServicePanelId())), false);
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/*      */     });
/*  682 */     GridBagLayoutHelper.add(1, 0, 0.0D, 0.0D, 1, 1, 5, 0, 0, 0, gbc, this, this.btnOpen);
/*  683 */     GridBagLayoutHelper.add(2, 0, 0.0D, 0.0D, 1, 1, 5, 0, 0, 0, gbc, this, this.btnEdit);
/*      */ 
/*  686 */     this.rangeComboBox = createRangeComboBox();
/*  687 */     this.rangeComboBox.setPreferredSize(new Dimension(100, this.prefferedComboBoxesHeight.intValue()));
/*      */ 
/*  690 */     this.dataLoadingMethodComboBox = createDataLoadingComboBox();
/*  691 */     this.dataLoadingMethodComboBox.setPreferredSize(new Dimension(160, this.prefferedComboBoxesHeight.intValue()));
/*      */ 
/*  694 */     gbc.anchor = 21;
/*  695 */     GridBagLayoutHelper.add(0, 1, 0.0D, 0.0D, 3, 1, 0, 0, 0, 0, gbc, this, getSecondRowPanel());
/*      */ 
/*  698 */     gbc.anchor = 23;
/*  699 */     gbc.fill = 1;
/*  700 */     GridBagLayoutHelper.add(0, 3, 1.0D, 1.0D, 3, 1, 0, 5, 0, 0, gbc, this, this.pnlOptimizerAndMessages);
/*      */ 
/*  703 */     this.rangeComboBox.setSelectedItem(TesterTimeRange.LAST_WEEK);
/*  704 */     this.periodComboBox.setSelectedItem(new TesterPeriod(Period.TICK));
/*  705 */     this.dataLoadingMethodComboBox.setSelectedItem(ITesterClient.DataLoadingMethod.ALL_TICKS);
/*  706 */     this.interpolMethodsComboBox.setSelectedItem(ITesterClient.InterpolationMethod.FOUR_TICKS);
/*      */   }
/*      */ 
/*      */   public JPanel getSecondRowPanel()
/*      */   {
/*  714 */     JPanel panel = new JPanel();
/*  715 */     panel.setLayout(new GridBagLayout());
/*      */ 
/*  717 */     GridBagConstraints gbc = new GridBagConstraints();
/*  718 */     gbc.fill = 0;
/*  719 */     gbc.anchor = 21;
/*      */ 
/*  722 */     JPanel restPanel = new JPanel();
/*  723 */     restPanel.setLayout(new GridBagLayout());
/*      */ 
/*  725 */     this.testerSettingsButton = new JLocalizableButton("tester.settings");
/*  726 */     this.testerSettingsButton.addActionListener(new ActionListener()
/*      */     {
/*      */       public void actionPerformed(ActionEvent e)
/*      */       {
/*  730 */         TesterSettingsDialog visualModeDialog = new TesterSettingsDialog(StrategyTestPanel.this.testerParameters);
/*  731 */         if (!visualModeDialog.isCanceled())
/*  732 */           StrategyTestPanel.this.strategyComboBox.repaint();
/*      */       }
/*      */     });
/*  737 */     JPanel checkBoxesPanel = new JPanel();
/*  738 */     checkBoxesPanel.setLayout(new GridBagLayout());
/*      */ 
/*  740 */     this.visualModeCheckbox = new JLocalizableCheckBox("tester.visual.mode");
/*  741 */     this.visualModeCheckbox.setFocusable(false);
/*  742 */     this.visualModeCheckbox.addActionListener(new ActionListener()
/*      */     {
/*      */       public void actionPerformed(ActionEvent e)
/*      */       {
/*  746 */         StrategyTestPanel.this.testerParameters.setVisualModeEnabled(StrategyTestPanel.this.visualModeCheckbox.isSelected());
/*  747 */         StrategyTestPanel.this.testerExecutionCtrlPanel.setVMEnabled(StrategyTestPanel.this.visualModeCheckbox.isSelected());
/*      */       }
/*      */     });
/*  751 */     this.optimizationCheckBox = new JLocalizableCheckBox("tester.optimization");
/*  752 */     this.optimizationCheckBox.setFocusable(false);
/*  753 */     this.optimizationCheckBox.addActionListener(new ActionListener()
/*      */     {
/*      */       public void actionPerformed(ActionEvent e)
/*      */       {
/*  757 */         StrategyTestPanel.this.processOptimizationBox();
/*      */       }
/*      */     });
/*  761 */     this.showMessagesCheckBox = new JLocalizableCheckBox("tester.show.messages");
/*  762 */     this.showMessagesCheckBox.setFocusable(false);
/*  763 */     this.showMessagesCheckBox.addActionListener(new ActionListener()
/*      */     {
/*      */       public void actionPerformed(ActionEvent e)
/*      */       {
/*  767 */         StrategyTestPanel.this.testerParameters.setPrintMessagesToConsole(StrategyTestPanel.this.showMessagesCheckBox.isSelected());
/*      */       }
/*      */     });
/*  771 */     GridBagLayoutHelper.add(0, 0, 0.0D, 0.0D, 1, 1, 0, 7, 0, 0, gbc, checkBoxesPanel, this.visualModeCheckbox);
/*  772 */     GridBagLayoutHelper.add(0, 1, 0.0D, 0.0D, 1, 1, 0, 0, 0, 0, gbc, checkBoxesPanel, this.optimizationCheckBox);
/*  773 */     GridBagLayoutHelper.add(0, 2, 0.0D, 0.0D, 1, 1, 0, 0, 0, 4, gbc, checkBoxesPanel, this.showMessagesCheckBox);
/*      */ 
/*  775 */     GridBagLayoutHelper.add(0, 0, 0.0D, 0.0D, 1, 2, 0, 1, 0, 0, gbc, panel, checkBoxesPanel);
/*      */ 
/*  778 */     this.instrumentsButton = new JLocalizableButton("tester.instruments");
/*  779 */     this.instrumentsButton.addActionListener(new ActionListener()
/*      */     {
/*      */       public void actionPerformed(ActionEvent e) {
/*  782 */         InstrumentSelectionDialogDetail instrSelectionDialog = new InstrumentSelectionDialogDetail(StrategyTestPanel.this.testerParameters);
/*  783 */         if (!instrSelectionDialog.isCanceled())
/*      */         {
/*  785 */           StrategyTestPanel.this.strategyComboBox.repaint();
/*      */         }
/*      */       }
/*      */     });
/*  790 */     this.accountButton = new JLocalizableButton("button.account");
/*  791 */     TesterAccountSettingsListener testerAccountSettingsListener = new TesterAccountSettingsListener(this.testerParameters, this);
/*  792 */     this.accountButton.addActionListener(testerAccountSettingsListener);
/*      */ 
/*  794 */     gbc.fill = 2;
/*  795 */     gbc.anchor = 21;
/*      */ 
/*  797 */     JPanel helperPanel = new JPanel();
/*  798 */     helperPanel.setLayout(new GridBagLayout());
/*      */ 
/*  800 */     GridBagLayoutHelper.add(0, 0, 0.0D, 0.0D, 1, 1, 3, 3, 3, 3, gbc, helperPanel, this.accountButton);
/*  801 */     GridBagLayoutHelper.add(1, 0, 0.0D, 0.0D, 1, 1, 3, 3, 3, 3, gbc, helperPanel, this.instrumentsButton);
/*  802 */     GridBagLayoutHelper.add(2, 0, 0.15D, 0.0D, 1, 1, 3, 3, 3, 3, gbc, helperPanel, this.rangeComboBox);
/*  803 */     GridBagLayoutHelper.add(3, 0, 0.35D, 0.0D, 1, 1, 3, 3, 3, 3, gbc, helperPanel, initPeriodComboBox());
/*      */ 
/*  805 */     this.offerSideComboBox = new SimpleLocalizableComboBox(OfferSide.values());
/*  806 */     this.offerSideComboBox.setRenderer(new SimpleLocalizableCellRenderer(this.offerSideComboBox, "tester.hint.select.offerside"));
/*  807 */     this.offerSideComboBox.setEnabled(false);
/*  808 */     this.offerSideComboBox.setPreferredSize(new Dimension(this.offerSideComboBox.getPreferredSize().width, this.prefferedComboBoxesHeight.intValue()));
/*  809 */     this.offerSideComboBox.setSelectedItem(OfferSide.BID);
/*      */ 
/*  811 */     GridBagLayoutHelper.add(4, 0, 0.15D, 0.0D, 1, 1, 3, 3, 3, 3, gbc, helperPanel, this.offerSideComboBox);
/*  812 */     GridBagLayoutHelper.add(5, 0, 0.45D, 0.0D, 1, 1, 3, 3, 3, 3, gbc, helperPanel, createCardLayoutPanel());
/*      */ 
/*  814 */     GridBagLayoutHelper.add(0, 0, 1.0D, 0.0D, 1, 1, 0, 0, 1, 0, gbc, restPanel, helperPanel);
/*      */ 
/*  817 */     JPanel messagesPanel = new JPanel();
/*  818 */     messagesPanel.setLayout(new GridBagLayout());
/*      */ 
/*  820 */     GridBagLayoutHelper.add(0, 0, 0.0D, 0.0D, 1, 1, 2, 2, 2, 0, gbc, messagesPanel, this.btnCopyMessage);
/*  821 */     GridBagLayoutHelper.add(1, 0, 0.0D, 0.0D, 1, 1, 2, 2, 2, 0, gbc, messagesPanel, this.btnClear);
/*  822 */     GridBagLayoutHelper.add(2, 0, 0.0D, 0.0D, 1, 1, 7, 2, 3, 0, gbc, messagesPanel, this.testerSettingsButton);
/*      */ 
/*  824 */     gbc.fill = 2;
/*  825 */     gbc.anchor = 21;
/*  826 */     GridBagLayoutHelper.add(3, 0, 1.0D, 0.0D, 1, 1, 2, 2, 10, 0, gbc, messagesPanel, getProgressBarPanel());
/*  827 */     GridBagLayoutHelper.add(4, 0, 0.0D, 0.0D, 1, 1, 2, 2, 2, 0, gbc, messagesPanel, getExecutionControlButtonsPanel());
/*      */ 
/*  830 */     GridBagLayoutHelper.add(0, 1, 1.0D, 0.0D, 1, 1, 0, 4, 2, 0, gbc, restPanel, messagesPanel);
/*      */ 
/*  832 */     gbc.fill = 1;
/*  833 */     gbc.anchor = 21;
/*  834 */     GridBagLayoutHelper.add(1, 1, 1.0D, 0.0D, 1, 1, 0, 0, 0, 0, gbc, panel, restPanel);
/*      */ 
/*  836 */     return panel;
/*      */   }
/*      */ 
/*      */   private void processOptimizationBox() {
/*  840 */     boolean isOptimization = (this.optimizationCheckBox.isSelected()) && (this.optimizationCheckBox.isEnabled());
/*      */ 
/*  842 */     this.testerParameters.setOptimizationMode(isOptimization);
/*      */ 
/*  844 */     this.visualModeCheckbox.setEnabled(!isOptimization);
/*  845 */     this.showMessagesCheckBox.setEnabled(!isOptimization);
/*      */ 
/*  847 */     this.btnMessages.setEnabled(!isOptimization);
/*  848 */     this.btnCopyMessage.setEnabled((this.btnCopyMessage.isEnabled()) && (!isOptimization));
/*  849 */     this.btnClear.setEnabled((this.btnClear.isEnabled()) && (!isOptimization));
/*      */ 
/*  851 */     this.testerSettingsButton.setEnabled(!isOptimization);
/*      */ 
/*  853 */     this.testerParameters.setVisualModeEnabled((this.visualModeCheckbox.isSelected()) && (!isOptimization));
/*  854 */     this.testerExecutionCtrlPanel.setVMEnabled((this.visualModeCheckbox.isSelected()) && (!isOptimization));
/*      */   }
/*      */ 
/*      */   public void switchStrategyRunningModes() {
/*  858 */     if (this.testerParameters.isOptimizationMode())
/*  859 */       this.pnlOptimizerAndMessages.showComponent("ID_JT_TESTER_OPTIMIZER_PANEL");
/*      */     else
/*  861 */       this.pnlOptimizerAndMessages.showComponent("ID_JT_TESTER_MESSAGEPANEL");
/*      */   }
/*      */ 
/*      */   private void createConsoleAndOptimizerPanels()
/*      */   {
/*  866 */     this.pnlMessages = new MessagePanel(true)
/*      */     {
/*      */       public void build() {
/*  869 */         super.build();
/*  870 */         add(this.scroll);
/*      */       }
/*      */ 
/*      */       protected void showMessages()
/*      */       {
/*      */       }
/*      */     };
/*  877 */     this.pnlMessages.setName("ID_JT_TESTER_MESSAGEPANEL");
/*  878 */     this.pnlMessages.build();
/*      */ 
/*  880 */     this.pnlMessages.getMessageList().getModel().addTableModelListener(new TableModelListener()
/*      */     {
/*      */       public void tableChanged(TableModelEvent e)
/*      */       {
/*  884 */         StrategyTestPanel.this.btnCopyMessage.setEnabled(!StrategyTestPanel.this.pnlMessages.isEmpty());
/*  885 */         StrategyTestPanel.this.btnClear.setEnabled(!StrategyTestPanel.this.pnlMessages.isEmpty());
/*      */       }
/*      */     });
/*  889 */     this.pnlOptimizer = new OptimizerPanel();
/*  890 */     this.pnlOptimizer.setName("ID_JT_TESTER_OPTIMIZER_PANEL");
/*      */ 
/*  892 */     this.pnlOptimizerAndMessages = new CardLayoutPanel();
/*  893 */     this.pnlOptimizerAndMessages.add(this.pnlMessages, "ID_JT_TESTER_MESSAGEPANEL");
/*  894 */     this.pnlOptimizerAndMessages.add(this.pnlOptimizer, "ID_JT_TESTER_OPTIMIZER_PANEL");
/*  895 */     this.pnlOptimizerAndMessages.showComponent("ID_JT_TESTER_MESSAGEPANEL");
/*      */ 
/*  897 */     this.btnCopyMessage = new JLocalizableButton();
/*  898 */     this.btnCopyMessage.setAction(this.pnlMessages.getActionCopyMessages());
/*  899 */     this.btnCopyMessage.setToolTipKey("item.copy.message");
/*  900 */     this.btnCopyMessage.setEmptyText();
/*  901 */     this.btnCopyMessage.setIcon(this.MESSAGES_COPY_ICON);
/*  902 */     this.btnCopyMessage.setDisabledIcon(this.MESSAGES_COPY_ICON_FADED);
/*  903 */     this.btnCopyMessage.setEnabled(!this.pnlMessages.isEmpty());
/*  904 */     this.btnCopyMessage.setPreferredSize(new Dimension(this.MESSAGES_COPY_ICON.getIconWidth() + 8, this.MESSAGES_COPY_ICON.getIconHeight() + 8));
/*  905 */     this.btnCopyMessage.setMaximumSize(new Dimension(this.MESSAGES_COPY_ICON.getIconWidth() + 8, this.MESSAGES_COPY_ICON.getIconHeight() + 8));
/*      */ 
/*  907 */     this.btnClear = new JLocalizableButton();
/*  908 */     this.btnClear.setAction(this.pnlMessages.getActionClearLog());
/*  909 */     this.btnClear.setToolTipKey("item.clear.all");
/*  910 */     this.btnClear.setEmptyText();
/*  911 */     this.btnClear.setIcon(this.MESSAGES_CLEAR_ICON);
/*  912 */     this.btnClear.setDisabledIcon(this.MESSAGES_CLEAR_ICON_FADED);
/*  913 */     this.btnClear.setEnabled(!this.pnlMessages.isEmpty());
/*  914 */     this.btnClear.setPreferredSize(new Dimension(this.MESSAGES_CLEAR_ICON.getIconWidth() + 8, this.MESSAGES_CLEAR_ICON.getIconHeight() + 8));
/*  915 */     this.btnClear.setMaximumSize(new Dimension(this.MESSAGES_CLEAR_ICON.getIconWidth() + 8, this.MESSAGES_CLEAR_ICON.getIconHeight() + 8));
/*      */ 
/*  917 */     this.btnMessages = new JLocalizableButton("button.messages");
/*  918 */     this.btnMessages.addActionListener(new ActionListener()
/*      */     {
/*      */       public void actionPerformed(ActionEvent e) {
/*  921 */         TesterMessagesSettingsDialog messagesDialoge = new TesterMessagesSettingsDialog(StrategyTestPanel.this.testerParameters);
/*  922 */         if (!messagesDialoge.isCanceled())
/*  923 */           StrategyTestPanel.this.strategyComboBox.repaint();
/*      */       }
/*      */     });
/*  929 */     this.pnlMessageButtons = new JPanel(new FlowLayout(2, 2, 2));
/*  930 */     this.pnlMessageButtons.add(this.btnCopyMessage);
/*  931 */     this.pnlMessageButtons.add(this.btnClear);
/*  932 */     this.pnlMessageButtons.add(this.btnMessages);
/*      */ 
/*  934 */     this.pnlOptimizerAndMessages.setMinimumSize(new Dimension(0, 10));
/*  935 */     this.pnlOptimizerAndMessages.setPreferredSize(new Dimension(0, 10));
/*      */   }
/*      */ 
/*      */   private StrategyWrapper getStrategy() {
/*  939 */     StrategyObject object = (StrategyObject)this.strategyComboBox.getSelectedItem();
/*  940 */     return object == null ? null : object.strategyWrapper;
/*      */   }
/*      */ 
/*      */   private void strategyStartActionPerformed(ActionEvent e) {
/*  944 */     this.testerParameters.setTesterPanel(this);
/*  945 */     Set selectedInstruments = this.testerParameters.getInstruments();
/*      */ 
/*  948 */     StrategyWrapper strategyWrapper = getStrategy();
/*  949 */     if (strategyWrapper == null) {
/*  950 */       JOptionPane.showMessageDialog(this, LocalizationManager.getText("joption.pane.open.strategy.for.testing"), LocalizationManager.getText("joption.pane.historical.tester"), 1);
/*  951 */       return;
/*      */     }
/*      */ 
/*  955 */     if (selectedInstruments.size() == 0) {
/*  956 */       JOptionPane.showMessageDialog(this, LocalizationManager.getText("tester.message.select.instruments"), LocalizationManager.getText("joption.pane.historical.tester"), 1);
/*  957 */       return;
/*      */     }
/*      */ 
/*  961 */     if (this.rangeComboBox.getSelectedItem() == null) {
/*  962 */       JOptionPane.showMessageDialog(this, LocalizationManager.getText("tester.message.select.date.range"), LocalizationManager.getText("joption.pane.historical.tester"), 1);
/*  963 */       return;
/*      */     }
/*      */ 
/*  966 */     this.testerParameters.setTesterTimeRange((TesterTimeRange)this.rangeComboBox.getSelectedItem());
/*      */ 
/*  968 */     long from = this.testerParameters.getTesterTimeRange().getDateFrom();
/*  969 */     long to = this.testerParameters.getTesterTimeRange().getDateTo();
/*  970 */     if ((from == -9223372036854775808L) || (to == -9223372036854775808L)) {
/*  971 */       JOptionPane.showMessageDialog(this, LocalizationManager.getText("joption.pane.incorrect.from.to.date"), LocalizationManager.getText("joption.pane.historical.tester"), 1);
/*  972 */       return;
/*      */     }
/*      */ 
/*  975 */     if (this.periodComboBox.getSelectedItem() == null) {
/*  976 */       JOptionPane.showMessageDialog(this, LocalizationManager.getText("tester.message.select.period"), LocalizationManager.getText("joption.pane.historical.tester"), 1);
/*  977 */       return;
/*      */     }
/*  979 */     if (((TesterPeriod)this.periodComboBox.getSelectedItem()).getPeriod().equals(Period.TICK))
/*      */     {
/*  981 */       if (this.dataLoadingMethodComboBox.getSelectedItem() == null) {
/*  982 */         JOptionPane.showMessageDialog(this, LocalizationManager.getText("tester.message.select.tick.filter"), LocalizationManager.getText("joption.pane.historical.tester"), 1);
/*  983 */         return;
/*      */       }
/*      */     }
/*      */     else {
/*  987 */       if (this.offerSideComboBox.getSelectedItem() == null) {
/*  988 */         JOptionPane.showMessageDialog(this, LocalizationManager.getText("tester.message.select.offerside"), LocalizationManager.getText("joption.pane.historical.tester"), 1);
/*  989 */         return;
/*      */       }
/*      */ 
/*  992 */       if (this.interpolMethodsComboBox.getSelectedItem() == null) {
/*  993 */         JOptionPane.showMessageDialog(this, LocalizationManager.getText("tester.message.select.interpolation.method"), LocalizationManager.getText("joption.pane.historical.tester"), 1);
/*  994 */         return;
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 1000 */     if (!this.testerParameters.validateMessagesFile()) {
/* 1001 */       return;
/*      */     }
/* 1003 */     File messagesFile = this.testerParameters.getResultingMessagesFile();
/* 1004 */     boolean appendMessages = this.testerParameters.isAppendMessages();
/*      */ 
/* 1007 */     if (!this.testerParameters.validateReportFile()) {
/* 1008 */       return;
/*      */     }
/*      */ 
/* 1011 */     StrategyWrapper wrapper = (StrategyWrapper)CompilerUtils.getInstance().runTesterCompilation(strategyWrapper);
/*      */ 
/* 1013 */     if (wrapper == null)
/*      */     {
/* 1017 */       return;
/*      */     }
/*      */ 
/* 1021 */     StrategyWrapper wrapperCopy = new StrategyWrapper();
/* 1022 */     wrapperCopy.setBinaryFile(wrapper.getBinaryFile());
/*      */ 
/* 1024 */     AccountStatement accountStatement = (AccountStatement)GreedContext.get("accountStatement");
/* 1025 */     AccountInfoMessage accountInfo = accountStatement.getLastAccountState();
/*      */ 
/* 1027 */     TesterAccount account = new TesterAccount(this.testerParameters.getAccountCurrency(), this.testerParameters.getInitialDeposit(), this.testerParameters.getMaxLeverage(), this.testerParameters.getMcLeverage(), this.testerParameters.getMcWeekendLeverage(), this.testerParameters.getMcEquity(), this.testerParameters.getCommissions(), this.testerParameters.getOvernights(), "");
/*      */ 
/* 1039 */     account.update5SecDelayedValues();
/* 1040 */     account.setGlobal(accountInfo.isGlobal());
/* 1041 */     if (accountInfo.getAccountState() != null) {
/* 1042 */       account.setAccountState((IAccount.AccountState)EnumConverter.convert(accountInfo.getAccountState(), IAccount.AccountState.class));
/*      */     }
/*      */ 
/* 1046 */     String userTypeDescription = (String)GreedContext.getProperty("userTypes");
/* 1047 */     account.setUserTypeDescription(userTypeDescription);
/*      */ 
/* 1049 */     Period selectedPeriod = ((TesterPeriod)this.periodComboBox.getSelectedItem()).getPeriod();
/*      */ 
/* 1051 */     ITesterClient.DataLoadingMethod dataLoadingMethod = null;
/* 1052 */     ITesterClient.InterpolationMethod interpolationMethod = null;
/* 1053 */     OfferSide offerSide = null;
/*      */ 
/* 1055 */     if (selectedPeriod.equals(Period.TICK)) {
/* 1056 */       dataLoadingMethod = (ITesterClient.DataLoadingMethod)this.dataLoadingMethodComboBox.getSelectedItem();
/*      */ 
/* 1059 */       if (dataLoadingMethod == ITesterClient.DataLoadingMethod.TICKS_WITH_PRICE_DIFFERENCE_IN_PIPS) {
/* 1060 */         dataLoadingMethod.setPriceDifferenceInPips(((Integer)this.pipsSpinner.getValue()).intValue());
/*      */       }
/*      */ 
/* 1064 */       if (dataLoadingMethod == ITesterClient.DataLoadingMethod.TICKS_WITH_TIME_INTERVAL) {
/* 1065 */         TimeUnitType timeUnitType = (TimeUnitType)this.timeUnitTypeCombo.getSelectedItem();
/* 1066 */         if (timeUnitType != null) {
/* 1067 */           Integer unitCount = (Integer)this.timeUnitsCountSpinner.getValue();
/* 1068 */           long timeInterval = timeUnitType.getTimeInterval().longValue() * unitCount.longValue();
/* 1069 */           dataLoadingMethod.setTimeIntervalBetweenTicks(timeInterval);
/*      */         }
/*      */       }
/*      */     }
/*      */     else
/*      */     {
/* 1075 */       long selectedPeriodMs = selectedPeriod.getNumOfUnits() * selectedPeriod.getUnit().getInterval();
/*      */ 
/* 1077 */       if (selectedPeriodMs > to - from) {
/* 1078 */         JOptionPane.showMessageDialog(this, LocalizationManager.getText("joption.pane.end.day.should.be.after.start.day"), LocalizationManager.getText("joption.pane.historical.tester"), 2);
/* 1079 */         return;
/*      */       }
/*      */ 
/* 1082 */       interpolationMethod = (ITesterClient.InterpolationMethod)this.interpolMethodsComboBox.getSelectedItem();
/* 1083 */       offerSide = (OfferSide)this.offerSideComboBox.getSelectedItem();
/*      */     }
/*      */ 
/* 1086 */     StrategyTesterAction action = new StrategyTesterAction(true, getExecutionControl(), this, selectedInstruments, dataLoadingMethod, selectedPeriod, offerSide, interpolationMethod, wrapperCopy, account, this.testerParameters, messagesFile, appendMessages);
/*      */ 
/* 1102 */     getExecutionControl().addExecutionControlListener(new ExecutionControlListener(action)
/*      */     {
/*      */       public void stateChanged(ExecutionControlEvent event) {
/* 1105 */         if (event.getExecutionControl().isPaused())
/* 1106 */           this.val$action.breakRemainingPause();
/*      */       }
/*      */ 
/*      */       public void speedChanged(ExecutionControlEvent event)
/*      */       {
/*      */       }
/*      */     });
/* 1114 */     GreedContext.publishEvent(action);
/*      */   }
/*      */ 
/*      */   public void panelClosed()
/*      */   {
/*      */   }
/*      */ 
/*      */   public String getStrategyName()
/*      */   {
/* 1123 */     return null;
/*      */   }
/*      */ 
/*      */   public File getMessagesFile() {
/* 1127 */     return null;
/*      */   }
/*      */ 
/*      */   public void set(StrategyTestBean strategyTestBean) {
/* 1131 */     setBalanceDropDown(strategyTestBean.getBalanseDropDown());
/*      */ 
/* 1133 */     this.testerParameters.restoreTesterAccountSettings(strategyTestBean);
/* 1134 */     this.testerParameters.restoreSelectedInstruments(strategyTestBean);
/* 1135 */     this.testerParameters.restoreMessagesReportSettings(strategyTestBean);
/* 1136 */     this.testerParameters.restoreSettings(strategyTestBean);
/*      */ 
/* 1138 */     this.showMessagesCheckBox.setSelected(this.testerParameters.isPrintMessagesToConsole());
/* 1139 */     this.visualModeCheckbox.setSelected(this.testerParameters.isVisualModeEnabled());
/*      */ 
/* 1141 */     this.optimizationCheckBox.setSelected(this.testerParameters.isOptimizationMode());
/* 1142 */     processOptimizationBox();
/*      */ 
/* 1144 */     if (this.testerParameters.getTesterTimeRange() != null) {
/* 1145 */       this.processCustomRangeSelection = false;
/* 1146 */       this.rangeComboBox.setSelectedItem(this.testerParameters.getTesterTimeRange());
/* 1147 */       this.processCustomRangeSelection = true;
/*      */     }
/*      */ 
/* 1150 */     if (strategyTestBean.getPeriod() != null) {
/* 1151 */       Period period = strategyTestBean.getPeriod();
/* 1152 */       if (period != null) {
/* 1153 */         this.periodComboBox.setSelectedItem(new TesterPeriod(period));
/*      */       }
/*      */     }
/*      */ 
/* 1157 */     if ((strategyTestBean.getOfferSide() != null) && (this.offerSideComboBox.isEnabled())) {
/* 1158 */       this.offerSideComboBox.setSelectedItem(strategyTestBean.getOfferSide());
/*      */     }
/*      */ 
/* 1161 */     if (strategyTestBean.getDataLoadingMethod() != null) {
/* 1162 */       ITesterClient.DataLoadingMethod dataLoadingMethod = strategyTestBean.getDataLoadingMethod();
/*      */ 
/* 1164 */       this.dataLoadingMethodComboBox.setSelectedItem(dataLoadingMethod);
/*      */ 
/* 1167 */       if (strategyTestBean.getPriceDiffInPips() > 0) {
/* 1168 */         this.pipsSpinner.setValue(Integer.valueOf(strategyTestBean.getPriceDiffInPips()));
/*      */       }
/*      */ 
/* 1172 */       if (dataLoadingMethod == ITesterClient.DataLoadingMethod.TICKS_WITH_TIME_INTERVAL) {
/* 1173 */         TimeUnitType timeUnitType = TimeUnitType.getByKey(strategyTestBean.getTimeUnitType());
/* 1174 */         if (timeUnitType != null) {
/* 1175 */           this.timeUnitTypeCombo.setSelectedItem(timeUnitType);
/*      */         }
/* 1177 */         this.timeUnitsCountSpinner.setValue(Integer.valueOf(strategyTestBean.getTimeUnitCount()));
/*      */       }
/*      */     }
/*      */ 
/* 1181 */     if (strategyTestBean.getInterpolationMethod() != null) {
/* 1182 */       this.interpolMethodsComboBox.setSelectedItem(strategyTestBean.getInterpolationMethod());
/*      */     }
/*      */ 
/* 1185 */     if (strategyTestBean.getStrategyBinaryPath() != null) {
/* 1186 */       for (int i = 0; i < this.strategyComboBox.getItemCount(); i++) {
/* 1187 */         StrategyObject strategy = (StrategyObject)this.strategyComboBox.getItemAt(i);
/* 1188 */         if ((strategy.getWrapper() != null) && (strategy.getWrapper().getBinaryFile().getAbsolutePath().equals(strategyTestBean.getStrategyBinaryPath()))) {
/* 1189 */           this.strategyComboBox.setSelectedItem(strategy);
/* 1190 */           break;
/*      */         }
/*      */       }
/*      */     }
/*      */ 
/* 1195 */     if (this.testerParameters.isVisualModeEnabled())
/* 1196 */       getExecutionControl().setSpeed(strategyTestBean.getTestSpeed());
/*      */   }
/*      */ 
/*      */   public void save(StrategyTestBean strategyTestBean)
/*      */   {
/* 1201 */     strategyTestBean.setBalanseDropDown(getBalanceDropDown());
/*      */ 
/* 1203 */     this.testerParameters.saveTesterAccountSettings(strategyTestBean);
/* 1204 */     this.testerParameters.saveSelectedInstruments(strategyTestBean);
/* 1205 */     this.testerParameters.saveMessagesReportSettings(strategyTestBean);
/* 1206 */     this.testerParameters.saveSettings(strategyTestBean);
/*      */ 
/* 1208 */     if (this.strategyComboBox.getSelectedItem() != null) {
/* 1209 */       StrategyObject strategy = (StrategyObject)this.strategyComboBox.getSelectedItem();
/* 1210 */       if (strategy.getWrapper() != null) {
/* 1211 */         String binaryFilePath = strategy.getWrapper().getBinaryFile().getAbsolutePath();
/* 1212 */         strategyTestBean.setStrategyBinaryPath(binaryFilePath);
/*      */       }
/*      */     } else {
/* 1215 */       strategyTestBean.setStrategyBinaryPath(null);
/*      */     }
/*      */ 
/* 1218 */     if (this.periodComboBox.getSelectedItem() != null) {
/* 1219 */       strategyTestBean.setPeriod(((TesterPeriod)this.periodComboBox.getSelectedItem()).getPeriod());
/*      */     }
/* 1221 */     strategyTestBean.setOfferSide((OfferSide)this.offerSideComboBox.getSelectedItem());
/* 1222 */     strategyTestBean.setDataLoadingMethod((ITesterClient.DataLoadingMethod)this.dataLoadingMethodComboBox.getSelectedItem());
/* 1223 */     strategyTestBean.setInterpolationMethod((ITesterClient.InterpolationMethod)this.interpolMethodsComboBox.getSelectedItem());
/*      */     try
/*      */     {
/* 1226 */       this.pipsSpinner.commitEdit();
/*      */     } catch (Exception ex) {
/* 1228 */       LOGGER.debug(ex.getMessage(), ex.getStackTrace());
/*      */     }
/*      */ 
/* 1231 */     strategyTestBean.setPriceDiffInPips(((Integer)this.pipsSpinner.getValue()).intValue());
/*      */ 
/* 1233 */     if (this.timeUnitTypeCombo.getSelectedItem() != null)
/* 1234 */       strategyTestBean.setTimeUnitType(((TimeUnitType)this.timeUnitTypeCombo.getSelectedItem()).timeKey);
/*      */     else {
/* 1236 */       strategyTestBean.setTimeUnitType(null);
/*      */     }
/*      */     try
/*      */     {
/* 1240 */       this.timeUnitsCountSpinner.commitEdit();
/*      */     } catch (Exception ex) {
/* 1242 */       LOGGER.debug(ex.getMessage(), ex.getStackTrace());
/*      */     }
/*      */ 
/* 1245 */     strategyTestBean.setTimeUnitCount(((Integer)this.timeUnitsCountSpinner.getValue()).intValue());
/*      */ 
/* 1247 */     if (this.testerParameters.isVisualModeEnabled())
/* 1248 */       strategyTestBean.setTestSpeed(getExecutionControl().getSpeed());
/*      */   }
/*      */ 
/*      */   public void initWithStrategy(int strategyId, StrategyWrapper strategyWrapper)
/*      */   {
/*      */   }
/*      */ 
/*      */   public boolean isBusy() {
/* 1256 */     return this.busy;
/*      */   }
/*      */ 
/*      */   public void initWithStrategy(String strategyName)
/*      */   {
/*      */     Map strategies;
/* 1266 */     if (strategyName != null)
/*      */     {
/* 1268 */       strategies = this.workspaceTreeController.getStrategies();
/* 1269 */       if (strategies != null)
/* 1270 */         for (Integer strategyId : strategies.keySet()) {
/* 1271 */           StrategyWrapper wrapper = (StrategyWrapper)strategies.get(strategyId);
/* 1272 */           if ((wrapper != null) && (strategyName.equals(wrapper.getBinaryFile().getName()))) {
/* 1273 */             initWithStrategy(strategyId.intValue(), wrapper);
/* 1274 */             return;
/*      */           }
/*      */         }
/*      */     }
/*      */   }
/*      */ 
/*      */   public void disableMessagesConsoleButtons()
/*      */   {
/* 1282 */     this.btnCopyMessage.setEnabled(false);
/* 1283 */     this.btnClear.setEnabled(false);
/*      */   }
/*      */ 
/*      */   public void lockGUI(boolean runStrategy) {
/* 1287 */     this.strategyComboBox.setEnabled(false);
/* 1288 */     this.btnOpen.setEnabled(false);
/* 1289 */     this.btnEdit.setEnabled(false);
/*      */ 
/* 1291 */     this.testerSettingsButton.setEnabled(false);
/* 1292 */     this.instrumentsButton.setEnabled(false);
/* 1293 */     this.accountButton.setEnabled(false);
/* 1294 */     this.btnMessages.setEnabled(false);
/*      */ 
/* 1296 */     this.visualModeCheckbox.setEnabled(false);
/* 1297 */     this.optimizationCheckBox.setEnabled(false);
/* 1298 */     this.showMessagesCheckBox.setEnabled(false);
/*      */ 
/* 1300 */     this.rangeComboBox.setEnabled(false);
/* 1301 */     this.periodComboBox.setEnabled(false);
/* 1302 */     this.offerSideComboBox.setEnabled(false);
/* 1303 */     this.interpolMethodsComboBox.setEnabled(false);
/* 1304 */     this.dataLoadingMethodComboBox.setEnabled(false);
/* 1305 */     this.pipsSpinner.setEnabled(false);
/*      */ 
/* 1307 */     this.executionControl.setStartEnabled(false);
/* 1308 */     this.busy = true;
/* 1309 */     this.cancelDataLoading = false;
/* 1310 */     this.cancelStrategyTest = false;
/* 1311 */     if (runStrategy)
/* 1312 */       this.executionControl.startExecuting(this.testerParameters.isVisualModeEnabled());
/*      */     else
/* 1314 */       this.executionControl.stopExecuting(this.testerParameters.isVisualModeEnabled());
/*      */   }
/*      */ 
/*      */   public void unlockGUI()
/*      */   {
/* 1319 */     this.btnOpen.setEnabled(true);
/* 1320 */     this.btnEdit.setEnabled(((StrategyObject)this.strategyComboBox.getSelectedItem()).getWrapper().isEditable());
/*      */ 
/* 1322 */     this.executionControl.setStartEnabled(true);
/* 1323 */     this.strategyComboBox.setEnabled(true);
/*      */ 
/* 1325 */     this.testerSettingsButton.setEnabled(!this.testerParameters.isOptimizationMode());
/* 1326 */     this.instrumentsButton.setEnabled(true);
/* 1327 */     this.accountButton.setEnabled(true);
/*      */ 
/* 1329 */     this.visualModeCheckbox.setEnabled(!this.testerParameters.isOptimizationMode());
/* 1330 */     this.optimizationCheckBox.setEnabled(((StrategyObject)this.strategyComboBox.getSelectedItem()).hasParameters());
/* 1331 */     this.showMessagesCheckBox.setEnabled(!this.testerParameters.isOptimizationMode());
/*      */ 
/* 1333 */     this.rangeComboBox.setEnabled(true);
/* 1334 */     this.periodComboBox.setEnabled(true);
/* 1335 */     this.offerSideComboBox.setEnabled(((TesterPeriod)this.periodComboBox.getSelectedItem()).getPeriod() != Period.TICK);
/* 1336 */     this.interpolMethodsComboBox.setEnabled(true);
/* 1337 */     this.dataLoadingMethodComboBox.setEnabled(true);
/* 1338 */     this.pipsSpinner.setEnabled(true);
/*      */ 
/* 1340 */     this.btnMessages.setEnabled(!this.testerParameters.isOptimizationMode());
/* 1341 */     this.btnCopyMessage.setEnabled((!this.pnlMessages.isEmpty()) && (!this.testerParameters.isOptimizationMode()));
/* 1342 */     this.btnClear.setEnabled((!this.pnlMessages.isEmpty()) && (!this.testerParameters.isOptimizationMode()));
/*      */ 
/* 1344 */     this.busy = false;
/* 1345 */     this.executionControl.stopExecuting(this.testerParameters.isVisualModeEnabled());
/* 1346 */     synchronized (this) {
/* 1347 */       notifyAll();
/*      */     }
/*      */   }
/*      */ 
/*      */   public void updateProgressBar(int value, String text) {
/* 1352 */     this.progressBar.setValue(value);
/* 1353 */     this.progressBar.setString(text);
/* 1354 */     this.progressLabel.setText(value + "%");
/*      */   }
/*      */ 
/*      */   public void updateProgressBar(int value, String labelValue, String text) {
/* 1358 */     this.progressBar.setValue(value);
/* 1359 */     this.progressBar.setString(text);
/*      */ 
/* 1361 */     this.progressLabel.setText(labelValue);
/*      */   }
/*      */ 
/*      */   public boolean dataLoadingCancelRequested() {
/* 1365 */     return this.cancelDataLoading;
/*      */   }
/*      */ 
/*      */   public boolean strategyTestCanceled() {
/* 1369 */     return this.cancelStrategyTest;
/*      */   }
/*      */ 
/*      */   public void cancel()
/*      */   {
/* 1376 */     this.cancelDataLoading = true;
/* 1377 */     this.cancelStrategyTest = true;
/* 1378 */     synchronized (this) {
/* 1379 */       while (this.busy)
/*      */         try {
/* 1381 */           wait(1000L);
/*      */         }
/*      */         catch (InterruptedException e)
/*      */         {
/*      */         }
/*      */     }
/*      */   }
/*      */ 
/*      */   public void openTesterTabs(Map<Instrument, TesterChartData> instrumentsAndProviders, String toolTipText) {
/* 1390 */     this.workspaceTreeController.openChartsForInstruments(instrumentsAndProviders, toolTipText);
/*      */   }
/*      */ 
/*      */   public void closeTesterTab(TesterChartData chart)
/*      */   {
/* 1395 */     this.workspaceTreeController.closeChart(chart.chartPanelId);
/* 1396 */     this.chartTabsAndFramesController.closeChart(chart.chartPanelId);
/*      */   }
/*      */ 
/*      */   public TesterNotificationUtils getTesterNotification()
/*      */   {
/* 1401 */     return this.notificationUtils;
/*      */   }
/*      */ 
/*      */   public ExecutionControl getExecutionControl() {
/* 1405 */     return this.executionControl;
/*      */   }
/*      */ 
/*      */   public boolean isMessagesEnabled() {
/* 1409 */     return (this.testerParameters.isSaveMessages()) || (this.testerParameters.isPrintMessagesToConsole());
/*      */   }
/*      */ 
/*      */   public void clearMessageLog() {
/* 1413 */     this.pnlMessages.clearMessageLog();
/*      */   }
/*      */ 
/*      */   public void postMessage(Notification message, boolean isLocal) {
/* 1417 */     if (this.testerParameters.isPrintMessagesToConsole())
/* 1418 */       this.pnlMessages.postMessage(message, isLocal);
/*      */   }
/*      */ 
/*      */   public HashMap<String, Variable[]> getOptimizationParams()
/*      */   {
/* 1423 */     return this.optimizationParameters;
/*      */   }
/*      */ 
/*      */   public void setOptimizationParams(HashMap<String, Variable[]> params) {
/* 1427 */     this.optimizationParameters = params;
/*      */   }
/*      */ 
/*      */   public double getBalanceDropDown() {
/* 1431 */     return this.balanceDropDown;
/*      */   }
/*      */ 
/*      */   public void setBalanceDropDown(double dropDown) {
/* 1435 */     this.balanceDropDown = dropDown;
/*      */   }
/*      */ 
/*      */   private SimpleLocalizableComboBox<ITesterClient.DataLoadingMethod> createDataLoadingComboBox()
/*      */   {
/* 1859 */     ITesterClient.DataLoadingMethod[] methods = { ITesterClient.DataLoadingMethod.ALL_TICKS, ITesterClient.DataLoadingMethod.DIFFERENT_PRICE_TICKS, ITesterClient.DataLoadingMethod.PIVOT_TICKS, ITesterClient.DataLoadingMethod.TICKS_WITH_PRICE_DIFFERENCE_IN_PIPS, ITesterClient.DataLoadingMethod.TICKS_WITH_TIME_INTERVAL };
/*      */ 
/* 1867 */     SimpleLocalizableComboBox comboBox = new SimpleLocalizableComboBox(methods);
/* 1868 */     comboBox.setRenderer(new SimpleLocalizableCellRenderer(comboBox, "tester.hint.select.tick.filter"));
/*      */ 
/* 1870 */     comboBox.addActionListener(new ActionListener(comboBox)
/*      */     {
/*      */       public void actionPerformed(ActionEvent e) {
/* 1873 */         if (this.val$comboBox.getSelectedItem() == ITesterClient.DataLoadingMethod.TICKS_WITH_PRICE_DIFFERENCE_IN_PIPS)
/* 1874 */           StrategyTestPanel.this.showPipsSpinner();
/*      */         else {
/* 1876 */           StrategyTestPanel.this.hidePipsSpinner();
/*      */         }
/*      */ 
/* 1879 */         if (this.val$comboBox.getSelectedItem() == ITesterClient.DataLoadingMethod.TICKS_WITH_TIME_INTERVAL)
/* 1880 */           StrategyTestPanel.this.showTimeFilterComponents();
/*      */         else
/* 1882 */           StrategyTestPanel.this.hideTimeFilterComponents();
/*      */       }
/*      */     });
/* 1887 */     return comboBox;
/*      */   }
/*      */ 
/*      */   private SimpleLocalizableComboBox<TesterPeriod> initPeriodComboBox() {
/* 1891 */     TesterPeriod[] periods = getPeriods();
/* 1892 */     this.periodComboBox = new SimpleLocalizableComboBox(periods);
/* 1893 */     this.periodComboBox.setMaximumRowCount(periods.length);
/*      */ 
/* 1895 */     this.periodComboBox.setRenderer(new SimpleLocalizableCellRenderer(this.periodComboBox, "tester.hint.select.period")
/*      */     {
/*      */       protected String getLocalizedText(Object value)
/*      */       {
/* 1900 */         if ((value instanceof TesterPeriod)) {
/* 1901 */           TesterPeriod testerPeriod = (TesterPeriod)value;
/* 1902 */           String text = ChartsLocalizator.getLocalized(testerPeriod.getPeriod());
/*      */ 
/* 1904 */           if (testerPeriod.getPeriod().equals(Period.TICK)) {
/* 1905 */             text = LocalizationManager.getText("tester.combo.period.ticks");
/*      */           }
/* 1909 */           else if (testerPeriod.getPeriod() == Period.DAILY_SKIP_SUNDAY)
/* 1910 */             text = LocalizationManager.getText("tester.period.day.skip.sunday");
/* 1911 */           else if (testerPeriod.getPeriod() == Period.DAILY_SUNDAY_IN_MONDAY) {
/* 1912 */             text = LocalizationManager.getText("tester.period.day.sunday.in.monday");
/*      */           }
/*      */ 
/* 1915 */           return text;
/*      */         }
/* 1917 */         return LocalizationManager.getText(String.valueOf(value));
/*      */       }
/*      */     });
/* 1922 */     this.periodComboBox.addActionListener(new ActionListener()
/*      */     {
/*      */       public void actionPerformed(ActionEvent e)
/*      */       {
/* 1926 */         TesterPeriod period = (TesterPeriod)StrategyTestPanel.this.periodComboBox.getSelectedItem();
/*      */ 
/* 1928 */         if (period != null)
/* 1929 */           if (period.getPeriod().equals(Period.TICK)) {
/* 1930 */             StrategyTestPanel.this.offerSideComboBox.setEnabled(false);
/* 1931 */             StrategyTestPanel.this.cardLayoutPanel.showComponent("ID_JT_TESTER_DATALOAD_PANEL");
/*      */           } else {
/* 1933 */             StrategyTestPanel.this.offerSideComboBox.setEnabled(true);
/* 1934 */             StrategyTestPanel.this.pipsSpinner.setValue(Integer.valueOf(1));
/* 1935 */             StrategyTestPanel.this.timeUnitsCountSpinner.setValue(Integer.valueOf(1));
/* 1936 */             StrategyTestPanel.this.timeUnitTypeCombo.setSelectedIndex(0);
/* 1937 */             StrategyTestPanel.this.cardLayoutPanel.showComponent("ID_JT_TESTER_INTERPOLATION_COMBO");
/*      */           }
/*      */       }
/*      */     });
/* 1943 */     this.periodComboBox.setPreferredSize(new Dimension(this.periodComboBox.getPreferredSize().width, this.prefferedComboBoxesHeight.intValue()));
/*      */ 
/* 1945 */     return this.periodComboBox;
/*      */   }
/*      */ 
/*      */   private TesterPeriod[] getPeriods() {
/* 1949 */     List allPeriods = ((ClientSettingsStorage)GreedContext.get("settingsStorage")).restoreChartPeriods();
/* 1950 */     List result = new ArrayList();
/* 1951 */     for (JForexPeriod period : allPeriods) {
/* 1952 */       if ((DataType.TICKS.equals(period.getDataType())) || (DataType.TIME_PERIOD_AGGREGATION.equals(period.getDataType())))
/*      */       {
/* 1956 */         result.add(new TesterPeriod(period.getPeriod()));
/*      */       }
/*      */     }
/*      */ 
/* 1960 */     result.add(new TesterPeriod(Period.DAILY_SKIP_SUNDAY));
/* 1961 */     result.add(new TesterPeriod(Period.DAILY_SUNDAY_IN_MONDAY));
/*      */ 
/* 1963 */     return (TesterPeriod[])result.toArray(new TesterPeriod[result.size()]);
/*      */   }
/*      */ 
/*      */   private void showPipsSpinner() {
/* 1967 */     if (!this.pipsSpinner.isVisible()) {
/* 1968 */       this.pipsSpinner.setVisible(true);
/* 1969 */       Dimension tempSize = getDataLoadingMethodComboPrefSize();
/* 1970 */       int sizeCorrection = getWidthForPipsFilterComponents();
/* 1971 */       this.dataLoadingMethodComboBox.setPreferredSize(new Dimension(tempSize.width - sizeCorrection, tempSize.height));
/* 1972 */       this.dataLoadingPanel.revalidate();
/*      */     }
/*      */   }
/*      */ 
/*      */   private void hidePipsSpinner() {
/* 1977 */     if ((this.pipsSpinner != null) && (this.pipsSpinner.isVisible())) {
/* 1978 */       this.pipsSpinner.setVisible(false);
/* 1979 */       Dimension tempSize = getDataLoadingMethodComboPrefSize();
/* 1980 */       int sizeCorrection = getWidthForPipsFilterComponents();
/* 1981 */       this.dataLoadingMethodComboBox.setPreferredSize(new Dimension(tempSize.width + sizeCorrection, tempSize.height));
/* 1982 */       this.dataLoadingPanel.revalidate();
/*      */     }
/*      */   }
/*      */ 
/*      */   private void showTimeFilterComponents() {
/* 1987 */     int sizeCorrection = getWidthForTimeFilterComponents();
/* 1988 */     Dimension tempSize = getDataLoadingMethodComboPrefSize();
/*      */ 
/* 1990 */     if ((this.timeUnitTypeCombo != null) && (!this.timeUnitTypeCombo.isVisible())) {
/* 1991 */       this.timeUnitTypeCombo.setVisible(true);
/* 1992 */       this.timeUnitsCountSpinner.setVisible(true);
/* 1993 */       this.dataLoadingMethodComboBox.setPreferredSize(new Dimension(tempSize.width - sizeCorrection, tempSize.height));
/* 1994 */       this.dataLoadingPanel.revalidate();
/*      */     }
/*      */   }
/*      */ 
/*      */   private void hideTimeFilterComponents() {
/* 1999 */     int sizeCorrection = getWidthForTimeFilterComponents();
/* 2000 */     Dimension tempSize = getDataLoadingMethodComboPrefSize();
/*      */ 
/* 2002 */     if ((this.timeUnitTypeCombo != null) && (this.timeUnitTypeCombo.isVisible())) {
/* 2003 */       this.timeUnitTypeCombo.setVisible(false);
/* 2004 */       this.timeUnitsCountSpinner.setVisible(false);
/* 2005 */       this.dataLoadingMethodComboBox.setPreferredSize(new Dimension(tempSize.width + sizeCorrection, tempSize.height));
/* 2006 */       this.dataLoadingPanel.revalidate();
/*      */     }
/*      */   }
/*      */ 
/*      */   private int getWidthForTimeFilterComponents() {
/* 2011 */     int sizeCorrection = this.timeUnitTypeComboWidth + this.timeUnitTypeComboOffset + this.timeUnitsCountSpinnerWidth + this.timeUnitsCountSpinnerOffset;
/*      */ 
/* 2017 */     return sizeCorrection;
/*      */   }
/*      */ 
/*      */   private int getWidthForPipsFilterComponents() {
/* 2021 */     int sizeCorrection = this.pipsSpinnerWidth + this.pipsSpinnerLeftOffset;
/* 2022 */     return sizeCorrection;
/*      */   }
/*      */ 
/*      */   private Dimension getDataLoadingMethodComboPrefSize() {
/* 2026 */     return this.dataLoadingMethodComboBox.getPreferredSize();
/*      */   }
/*      */ 
/*      */   private static enum TimeUnitType {
/* 2030 */     Seconds(Long.valueOf(Unit.Second.getInterval()), "label.caption.seconds"), 
/* 2031 */     Minutes(Long.valueOf(Unit.Minute.getInterval()), "label.caption.minutes"), 
/* 2032 */     Hours(Long.valueOf(Unit.Hour.getInterval()), "label.caption.hours"), 
/* 2033 */     Days(Long.valueOf(Unit.Day.getInterval()), "label.caption.days");
/*      */ 
/*      */     private Long timeInterval;
/*      */     private String timeKey;
/*      */ 
/* 2036 */     private TimeUnitType(Long timeInterval, String timeKey) { this.timeInterval = timeInterval;
/* 2037 */       this.timeKey = timeKey;
/*      */     }
/*      */ 
/*      */     public Long getTimeInterval()
/*      */     {
/* 2044 */       return this.timeInterval;
/*      */     }
/*      */ 
/*      */     public String getTimeKey() {
/* 2048 */       return this.timeKey;
/*      */     }
/*      */ 
/*      */     public static TimeUnitType getByKey(String timeKey) {
/* 2052 */       for (TimeUnitType type : values()) {
/* 2053 */         if (type.timeKey.equals(timeKey)) {
/* 2054 */           return type;
/*      */         }
/*      */       }
/* 2057 */       return null;
/*      */     }
/*      */   }
/*      */ 
/*      */   private class RangeLocalizableCellRenderer extends StrategyTestPanel.SimpleLocalizableCellRenderer
/*      */   {
/* 1829 */     private final EmptyBorder border = new EmptyBorder(new Insets(0, 3, 0, 0));
/*      */ 
/*      */     public RangeLocalizableCellRenderer(JComboBox comboBox, String hint) {
/* 1832 */       super(comboBox, hint);
/*      */     }
/*      */ 
/*      */     protected Component getDefaultComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus)
/*      */     {
/* 1837 */       if (value != TesterTimeRange.CUSTOM_PERIOD_TEMPLATE) {
/* 1838 */         return super.getDefaultComponent(list, value, index, isSelected, cellHasFocus);
/*      */       }
/* 1840 */       String localizedText = getLocalizedText(value);
/* 1841 */       JLabel label = (JLabel)this.origRenderer.getListCellRendererComponent(list, localizedText, index, isSelected, cellHasFocus);
/*      */ 
/* 1844 */       if (label.getFont().getStyle() != 1) {
/* 1845 */         label.setFont(label.getFont().deriveFont(1));
/*      */       }
/* 1847 */       if (label.getBorder() != this.border) {
/* 1848 */         label.setBorder(this.border);
/*      */       }
/*      */ 
/* 1851 */       return label;
/*      */     }
/*      */   }
/*      */ 
/*      */   private class SimpleLocalizableCellRenderer extends DefaultListCellRenderer
/*      */   {
/*      */     private final Font origFont;
/*      */     protected final ListCellRenderer origRenderer;
/*      */     private final String hint;
/*      */     private final JComboBox comboBox;
/* 1752 */     private final EmptyBorder border = new EmptyBorder(new Insets(0, 3, 0, 0));
/*      */ 
/*      */     public SimpleLocalizableCellRenderer(JComboBox comboBox, String hint)
/*      */     {
/* 1757 */       this.comboBox = comboBox;
/* 1758 */       this.hint = hint;
/* 1759 */       this.origFont = comboBox.getFont();
/* 1760 */       this.origRenderer = comboBox.getRenderer();
/*      */     }
/*      */ 
/*      */     public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus)
/*      */     {
/* 1765 */       if ((value == null) || ((value.toString().length() == 0) && (index == -1)))
/*      */       {
/* 1767 */         return getHintComponent(list, this.hint, index, isSelected, cellHasFocus);
/* 1768 */       }if ((value.toString().length() > 0) && (index == -1))
/*      */       {
/* 1770 */         return getSelectedComponent(list, value, index, isSelected, cellHasFocus);
/*      */       }
/*      */ 
/* 1773 */       return getDefaultComponent(list, value, index, isSelected, cellHasFocus);
/*      */     }
/*      */ 
/*      */     protected Component getHintComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus)
/*      */     {
/* 1778 */       String localizedText = getLocalizedText(value);
/* 1779 */       Component comp = this.origRenderer.getListCellRendererComponent(list, localizedText, index, isSelected, cellHasFocus);
/*      */ 
/* 1781 */       if (this.comboBox.getFont().getStyle() != 2) {
/* 1782 */         this.comboBox.setFont(comp.getFont().deriveFont(2));
/*      */       }
/*      */ 
/* 1785 */       return comp;
/*      */     }
/*      */ 
/*      */     protected Component getDefaultComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
/* 1789 */       String localizedText = getLocalizedText(value);
/* 1790 */       if (this.comboBox.getFont() != this.origFont) {
/* 1791 */         this.comboBox.setFont(this.origFont);
/*      */       }
/*      */ 
/* 1794 */       Component component = this.origRenderer.getListCellRendererComponent(list, localizedText, index, isSelected, cellHasFocus);
/*      */ 
/* 1796 */       if ((component instanceof JLabel)) {
/* 1797 */         JLabel label = (JLabel)component;
/* 1798 */         if (label.getBorder() != this.border) {
/* 1799 */           label.setBorder(this.border);
/*      */         }
/*      */       }
/*      */ 
/* 1803 */       return component;
/*      */     }
/*      */ 
/*      */     private Component getSelectedComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
/* 1807 */       String caption = getSelectedComponentCaption();
/* 1808 */       if (caption == null) {
/* 1809 */         caption = getLocalizedText(value);
/*      */       }
/* 1811 */       return this.origRenderer.getListCellRendererComponent(list, caption, index, isSelected, cellHasFocus);
/*      */     }
/*      */ 
/*      */     protected String getSelectedComponentCaption() {
/* 1815 */       return null;
/*      */     }
/*      */ 
/*      */     protected String getLocalizedText(Object value) {
/* 1819 */       String key = (String)StrategyTestPanel.this.translatingStringConstants.get(value.toString());
/* 1820 */       if (key != null) {
/* 1821 */         return LocalizationManager.getText(key);
/*      */       }
/* 1823 */       return LocalizationManager.getText(value.toString());
/*      */     }
/*      */   }
/*      */ 
/*      */   private static class SimpleComboBoxModel<D> extends AbstractListModel
/*      */     implements ComboBoxModel
/*      */   {
/*      */     private D selectedItem;
/*      */     private List<D> elements;
/*      */ 
/*      */     public SimpleComboBoxModel(D[] items)
/*      */     {
/* 1666 */       this.elements = new ArrayList(items.length);
/* 1667 */       for (Object d : items)
/* 1668 */         this.elements.add(d);
/*      */     }
/*      */ 
/*      */     public void setItems(List<D> items)
/*      */     {
/* 1673 */       int oldSelectedIndex = getSelectedIndex();
/*      */ 
/* 1676 */       this.elements.clear();
/* 1677 */       this.elements.addAll(items);
/* 1678 */       fireContentsChanged(this, 0, this.elements.size() - 1);
/*      */ 
/* 1681 */       if (oldSelectedIndex >= 0)
/* 1682 */         if (getSize() < 1) {
/* 1683 */           this.selectedItem = null;
/* 1684 */           fireContentsChanged(this, -1, -1);
/*      */         }
/*      */         else
/*      */         {
/* 1688 */           int newSelectedIndex = getSelectedIndex();
/* 1689 */           if (oldSelectedIndex != newSelectedIndex) {
/* 1690 */             if (newSelectedIndex < 0)
/*      */             {
/* 1692 */               if (oldSelectedIndex < getSize())
/* 1693 */                 this.selectedItem = this.elements.get(oldSelectedIndex);
/*      */               else {
/* 1695 */                 this.selectedItem = this.elements.get(this.elements.size() - 1);
/*      */               }
/*      */             }
/* 1698 */             fireContentsChanged(this, -1, -1);
/*      */           }
/*      */         }
/*      */     }
/*      */ 
/*      */     private int getSelectedIndex()
/*      */     {
/* 1705 */       if (this.selectedItem == null) {
/* 1706 */         return -1;
/*      */       }
/* 1708 */       for (int i = 0; i < this.elements.size(); i++) {
/* 1709 */         Object element = this.elements.get(i);
/* 1710 */         if (this.selectedItem.equals(element)) {
/* 1711 */           return i;
/*      */         }
/*      */       }
/* 1714 */       return -1;
/*      */     }
/*      */ 
/*      */     public D getElementAt(int index)
/*      */     {
/* 1720 */       return this.elements.get(index);
/*      */     }
/*      */ 
/*      */     public int getSize()
/*      */     {
/* 1725 */       return this.elements.size();
/*      */     }
/*      */ 
/*      */     public D getSelectedItem()
/*      */     {
/* 1730 */       return this.selectedItem;
/*      */     }
/*      */ 
/*      */     public void setSelectedItem(Object anItem)
/*      */     {
/* 1737 */       if (((this.selectedItem != null) && (!this.selectedItem.equals(anItem))) || ((this.selectedItem == null) && (anItem != null)))
/*      */       {
/* 1740 */         this.selectedItem = anItem;
/* 1741 */         fireContentsChanged(this, -1, -1);
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   private static class SimpleLocalizableComboBox<D> extends JComboBox
/*      */     implements Localizable
/*      */   {
/*      */     private D[] items;
/* 1614 */     private boolean langChanged = false;
/*      */ 
/*      */     public SimpleLocalizableComboBox(D[] items) {
/* 1617 */       this.items = items;
/* 1618 */       setModel(new StrategyTestPanel.SimpleComboBoxModel(items));
/* 1619 */       LocalizationManager.addLocalizable(this);
/*      */     }
/*      */ 
/*      */     public void setItems(List<D> items)
/*      */     {
/* 1624 */       ((StrategyTestPanel.SimpleComboBoxModel)super.getModel()).setItems(items);
/*      */     }
/*      */ 
/*      */     public D getSelectedItem()
/*      */     {
/* 1630 */       return super.getSelectedItem();
/*      */     }
/*      */ 
/*      */     public D getItemAt(int index)
/*      */     {
/* 1636 */       return super.getItemAt(index);
/*      */     }
/*      */ 
/*      */     public void localize()
/*      */     {
/* 1641 */       Object selected = getSelectedItem();
/*      */ 
/* 1643 */       if (selected != null) {
/* 1644 */         this.langChanged = true;
/*      */       }
/*      */ 
/* 1647 */       setModel(new StrategyTestPanel.SimpleComboBoxModel(this.items));
/* 1648 */       setSelectedItem(selected);
/*      */     }
/*      */ 
/*      */     public boolean isLangChanged() {
/* 1652 */       return this.langChanged;
/*      */     }
/*      */ 
/*      */     public void setLangChanged(boolean langChanged) {
/* 1656 */       this.langChanged = langChanged;
/*      */     }
/*      */   }
/*      */ 
/*      */   private class StrategyObject
/*      */     implements Comparable<StrategyObject>
/*      */   {
/*      */     private StrategyWrapper strategyWrapper;
/*      */     private int servicePanelId;
/*      */     private boolean hasParameters;
/*      */ 
/*      */     private StrategyObject(StrategyWrapper strategyWrapper, int servicePanelId, boolean hasParameters)
/*      */     {
/* 1564 */       this.strategyWrapper = strategyWrapper;
/* 1565 */       this.servicePanelId = servicePanelId;
/* 1566 */       this.hasParameters = hasParameters;
/*      */     }
/*      */ 
/*      */     public String toString()
/*      */     {
/* 1571 */       return this.strategyWrapper == null ? "" : this.strategyWrapper.getBinaryFile().getName();
/*      */     }
/*      */ 
/*      */     public boolean equals(Object o)
/*      */     {
/* 1576 */       if (this == o) return true;
/* 1577 */       if ((o == null) || (getClass() != o.getClass())) return false;
/*      */ 
/* 1579 */       StrategyObject that = (StrategyObject)o;
/* 1580 */       return this.servicePanelId == that.servicePanelId;
/*      */     }
/*      */ 
/*      */     public int compareTo(StrategyObject o)
/*      */     {
/* 1585 */       if (o == null) {
/* 1586 */         return 1;
/*      */       }
/* 1588 */       StrategyWrapper wrapper = o.strategyWrapper;
/* 1589 */       if (wrapper == null)
/* 1590 */         return 1;
/* 1591 */       if (this.strategyWrapper == null) {
/* 1592 */         return -1;
/*      */       }
/* 1594 */       return this.strategyWrapper.getBinaryFile().getName().compareTo(wrapper.getBinaryFile().getName());
/*      */     }
/*      */ 
/*      */     public int getServicePanelId()
/*      */     {
/* 1600 */       return this.servicePanelId;
/*      */     }
/*      */ 
/*      */     public StrategyWrapper getWrapper() {
/* 1604 */       return this.strategyWrapper;
/*      */     }
/*      */ 
/*      */     public boolean hasParameters() {
/* 1608 */       return this.hasParameters;
/*      */     }
/*      */   }
/*      */ 
/*      */   private class StrategyComboBoxModel
/*      */     implements ComboBoxModel
/*      */   {
/* 1442 */     private List<ListDataListener> listeners = new ArrayList();
/*      */     private StrategyTestPanel.StrategyObject selectedItem;
/* 1444 */     private List<StrategyTestPanel.StrategyObject> strategies = new ArrayList();
/*      */ 
/* 1446 */     private StrategyTestPanel.StrategyObject emptyLineObject = new StrategyTestPanel.StrategyObject(StrategyTestPanel.this, null, -1, false, null);
/* 1447 */     private StrategyTestPanel.StrategyObject openStrategyCommandObject = new StrategyTestPanel.StrategyObject(StrategyTestPanel.this, null, -2, false, null);
/*      */ 
/*      */     private StrategyComboBoxModel()
/*      */     {
/*      */     }
/*      */ 
/*      */     public void setSelectedItem(Object anItem) {
/* 1454 */       this.selectedItem = ((StrategyTestPanel.StrategyObject)anItem);
/*      */     }
/*      */ 
/*      */     public Object getSelectedItem()
/*      */     {
/* 1459 */       return this.selectedItem;
/*      */     }
/*      */ 
/*      */     public int getSize()
/*      */     {
/* 1464 */       return this.strategies.size();
/*      */     }
/*      */ 
/*      */     public Object getElementAt(int index)
/*      */     {
/* 1469 */       return this.strategies.get(index);
/*      */     }
/*      */ 
/*      */     public void addListDataListener(ListDataListener l)
/*      */     {
/* 1474 */       this.listeners.add(l);
/*      */     }
/*      */ 
/*      */     public void removeListDataListener(ListDataListener l)
/*      */     {
/* 1479 */       this.listeners.remove(l);
/*      */     }
/*      */ 
/*      */     public void selectStrategy(int strategyId) {
/* 1483 */       for (StrategyTestPanel.StrategyObject strategy : this.strategies)
/* 1484 */         if (strategy.getServicePanelId() == strategyId) {
/* 1485 */           setSelectedItem(strategy);
/* 1486 */           break;
/*      */         }
/*      */     }
/*      */ 
/*      */     public void loadCompiledStrategies()
/*      */     {
/* 1519 */       this.strategies.clear();
/*      */ 
/* 1521 */       ClientSettingsStorage clientSettingsStorage = (ClientSettingsStorage)GreedContext.get("settingsStorage");
/* 1522 */       List strategiesBeans = clientSettingsStorage.getStrategyNewBeans();
/*      */ 
/* 1524 */       for (StrategyNewBean bean : strategiesBeans) {
/* 1525 */         if (bean.getStrategyBinaryFile() != null) {
/* 1526 */           StrategyWrapper strategyWrapper = new StrategyWrapper();
/* 1527 */           strategyWrapper.setBinaryFile(bean.getStrategyBinaryFile());
/* 1528 */           strategyWrapper.setSourceFile(bean.getStrategySourceFile());
/*      */ 
/* 1530 */           StrategyTestPanel.StrategyObject strategyObject = new StrategyTestPanel.StrategyObject(StrategyTestPanel.this, strategyWrapper, bean.getId().intValue(), bean.hasParameters(), null);
/* 1531 */           this.strategies.add(strategyObject);
/*      */         }
/*      */       }
/*      */ 
/* 1535 */       Collections.sort(this.strategies);
/*      */ 
/* 1537 */       addHeaderItems();
/* 1538 */       fireContentsChanged();
/*      */ 
/* 1540 */       if (!this.strategies.contains(this.selectedItem))
/* 1541 */         setSelectedItem(null);
/*      */     }
/*      */ 
/*      */     private void addHeaderItems()
/*      */     {
/* 1546 */       this.strategies.add(0, this.emptyLineObject);
/* 1547 */       this.strategies.add(0, this.openStrategyCommandObject);
/*      */     }
/*      */ 
/*      */     public void fireContentsChanged()
/*      */     {
/* 1552 */       for (ListDataListener listener : this.listeners)
/* 1553 */         listener.contentsChanged(new ListDataEvent(this, 0, 0, this.strategies.size() - 1));
/*      */     }
/*      */   }
/*      */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.strategy.StrategyTestPanel
 * JD-Core Version:    0.6.0
 */