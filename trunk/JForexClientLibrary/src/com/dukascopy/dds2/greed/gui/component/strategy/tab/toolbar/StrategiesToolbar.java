/*      */ package com.dukascopy.dds2.greed.gui.component.strategy.tab.toolbar;
/*      */ 
/*      */ import com.dukascopy.api.impl.StrategyWrapper;
/*      */ import com.dukascopy.dds2.greed.GreedContext;
/*      */ import com.dukascopy.dds2.greed.actions.AppActionEvent;
/*      */ import com.dukascopy.dds2.greed.actions.CompileStrategyAction;
/*      */ import com.dukascopy.dds2.greed.agent.strategy.ide.api.ServiceSourceType;
/*      */ import com.dukascopy.dds2.greed.console.MessagePanelManager;
/*      */ import com.dukascopy.dds2.greed.gui.JForexClientFormLayoutManager;
/*      */ import com.dukascopy.dds2.greed.gui.component.chart.BottomTabsAndFramesTabbedPane;
/*      */ import com.dukascopy.dds2.greed.gui.component.chart.ServiceSourceEditorPanel;
/*      */ import com.dukascopy.dds2.greed.gui.component.chart.holders.IChartTabsAndFramesController;
/*      */ import com.dukascopy.dds2.greed.gui.component.menu.ViewRemoteStrategyLogAction;
/*      */ import com.dukascopy.dds2.greed.gui.component.strategy.StrategyTestPanel;
/*      */ import com.dukascopy.dds2.greed.gui.component.strategy.tab.actions.ChangeStrategyParametersTaskAction;
/*      */ import com.dukascopy.dds2.greed.gui.component.strategy.tab.actions.IStrategyAction;
/*      */ import com.dukascopy.dds2.greed.gui.component.strategy.tab.actions.RunStrategyTaskAction;
/*      */ import com.dukascopy.dds2.greed.gui.component.strategy.tab.actions.StopStrategyTaskAction;
/*      */ import com.dukascopy.dds2.greed.gui.component.strategy.tab.customUI.ResizableComboBoxUI;
/*      */ import com.dukascopy.dds2.greed.gui.component.strategy.tab.customUI.StrategiesButton;
/*      */ import com.dukascopy.dds2.greed.gui.component.strategy.tab.customUI.StrategiesComboBoxUI;
/*      */ import com.dukascopy.dds2.greed.gui.component.strategy.tab.customUI.StrategiesToggleButton;
/*      */ import com.dukascopy.dds2.greed.gui.component.strategy.tab.mediator.ElapsedTimeActionListener;
/*      */ import com.dukascopy.dds2.greed.gui.component.strategy.tab.mediator.StrategyNewBean;
/*      */ import com.dukascopy.dds2.greed.gui.component.strategy.tab.mediator.StrategyStatus;
/*      */ import com.dukascopy.dds2.greed.gui.component.strategy.tab.mediator.StrategyType;
/*      */ import com.dukascopy.dds2.greed.gui.component.strategy.tab.preset.DefaultStrategyPresetsController;
/*      */ import com.dukascopy.dds2.greed.gui.component.strategy.tab.preset.IStrategyPresetsController;
/*      */ import com.dukascopy.dds2.greed.gui.component.strategy.tab.preset.StrategyPreset;
/*      */ import com.dukascopy.dds2.greed.gui.component.strategy.tab.preset.StrategyPresetsComboBoxModel;
/*      */ import com.dukascopy.dds2.greed.gui.component.strategy.tab.preset.StrategyPresetsDialog;
/*      */ import com.dukascopy.dds2.greed.gui.component.strategy.tab.table.StrategiesTable;
/*      */ import com.dukascopy.dds2.greed.gui.component.strategy.tab.table.StrategiesTableModel;
/*      */ import com.dukascopy.dds2.greed.gui.component.strategy.tab.util.IncorrectClassTypeException;
/*      */ import com.dukascopy.dds2.greed.gui.component.strategy.tab.util.StrategyBinaryLoader;
/*      */ import com.dukascopy.dds2.greed.gui.component.tree.WorkspaceTreeController;
/*      */ import com.dukascopy.dds2.greed.gui.component.tree.nodes.StrategyTreeNode;
/*      */ import com.dukascopy.dds2.greed.gui.l10n.components.JLocalizableButton;
/*      */ import com.dukascopy.dds2.greed.gui.l10n.components.JLocalizableMenuItem;
/*      */ import com.dukascopy.dds2.greed.gui.settings.ClientSettingsStorage;
/*      */ import com.dukascopy.dds2.greed.util.INotificationUtils;
/*      */ import com.dukascopy.dds2.greed.util.NotificationUtilsProvider;
/*      */ import java.awt.Dimension;
/*      */ import java.awt.FlowLayout;
/*      */ import java.awt.event.ActionEvent;
/*      */ import java.awt.event.ActionListener;
/*      */ import java.awt.event.ItemEvent;
/*      */ import java.awt.event.ItemListener;
/*      */ import java.awt.event.MouseAdapter;
/*      */ import java.awt.event.MouseEvent;
/*      */ import java.awt.event.WindowAdapter;
/*      */ import java.awt.event.WindowEvent;
/*      */ import java.io.File;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Date;
/*      */ import java.util.EventObject;
/*      */ import java.util.List;
/*      */ import javax.swing.Box;
/*      */ import javax.swing.JComboBox;
/*      */ import javax.swing.JMenuItem;
/*      */ import javax.swing.JPanel;
/*      */ import javax.swing.JPopupMenu;
/*      */ import javax.swing.JSeparator;
/*      */ import javax.swing.Timer;
/*      */ import javax.swing.border.EmptyBorder;
/*      */ import javax.swing.event.TableModelEvent;
/*      */ import javax.swing.event.TableModelListener;
/*      */ import javax.swing.table.TableCellEditor;
/*      */ 
/*      */ public class StrategiesToolbar extends JPanel
/*      */ {
/*   86 */   public static final Dimension SEPARATOR_SIZE = new Dimension(2, 24);
/*      */   private StrategiesTable table;
/*      */   private JPanel presetContainerPanel;
/*      */   private JPanel startContainerPanel;
/*      */   private JPopupMenu strategiesPopupMenu;
/*      */   private JLocalizableButton startStrategyButton;
/*      */   private JLocalizableButton stopStrategyButton;
/*      */   private JLocalizableButton stopAllStrategiesButton;
/*      */   private JLocalizableButton editStrategyButton;
/*      */   private JLocalizableButton compileStrategyButton;
/*      */   private JLocalizableButton testerStrategyButton;
/*      */   private JLocalizableButton openStrategyButton;
/*      */   private JLocalizableButton removeStrategyButton;
/*      */   private JLocalizableButton propertiesButton;
/*      */   private JLocalizableButton strategyLogButton;
/*      */   private JMenuItem startStrategyLocallyMenuItem;
/*      */   private JMenuItem startStrategyRemotelyMenuItem;
/*      */   private JMenuItem stopStrategyMenuItem;
/*      */   private JMenuItem editStrategyMenuItem;
/*      */   private JMenuItem compileStrategyMenuItem;
/*      */   private JMenuItem testerStrategyMenuItem;
/*      */   private JMenuItem commentStrategyMenuItem;
/*      */   private JMenuItem removeStrategyMenuItem;
/*      */   private JComboBox startModeComboBox;
/*      */   private JComboBox presetComboBox;
/*      */   private ClientSettingsStorage settingsStorage;
/*      */   private IChartTabsAndFramesController tabsAndFramesController;
/*      */   private BottomTabsAndFramesTabbedPane tabbedPane;
/*      */   private WorkspaceTreeController workspaceTreeController;
/*      */   private IStrategiesToolbarController toolbarController;
/*      */   private IStrategyPresetsController presetsController;
/*      */   private List<StrategyNewBean> selectedStrategies;
/*      */   private int[] selectedRows;
/*      */ 
/*      */   public StrategiesToolbar(ClientSettingsStorage settingsStorage, BottomTabsAndFramesTabbedPane tabbedPane, StrategiesTable table, IChartTabsAndFramesController tabsAndFramesController)
/*      */   {
/*  137 */     this.table = table;
/*  138 */     this.settingsStorage = settingsStorage;
/*  139 */     this.tabsAndFramesController = tabsAndFramesController;
/*  140 */     this.tabbedPane = tabbedPane;
/*  141 */     this.toolbarController = new StrategiesToolbarController(settingsStorage);
/*  142 */     this.presetsController = new DefaultStrategyPresetsController();
/*  143 */     this.selectedStrategies = new ArrayList();
/*      */ 
/*  145 */     setLayout(new FlowLayout(0, 1, 1));
/*  146 */     setPreferredSize(new Dimension(2147483647, 30));
/*      */ 
/*  148 */     build();
/*      */   }
/*      */ 
/*      */   public void setWorkspaceTreeController(WorkspaceTreeController workspaceTreeController) {
/*  152 */     this.workspaceTreeController = workspaceTreeController;
/*      */   }
/*      */ 
/*      */   public WorkspaceTreeController getWorkspaceTreeController() {
/*  156 */     return this.workspaceTreeController;
/*      */   }
/*      */ 
/*      */   public List<StrategyNewBean> openStrategiesSelection(boolean openEditor) {
/*  160 */     return selectStrategiesFiles(openEditor);
/*      */   }
/*      */ 
/*      */   public void removeStrategy(int strategyId) {
/*  164 */     selectStrategy(strategyId);
/*  165 */     this.removeStrategyButton.doClick();
/*      */   }
/*      */ 
/*      */   public void selectStrategy(int strategyId)
/*      */   {
/*  170 */     StrategiesTableModel tableModel = (StrategiesTableModel)this.table.getModel();
/*  171 */     List strategies = tableModel.getStrategies();
/*      */ 
/*  173 */     boolean strategyExists = false;
/*      */ 
/*  175 */     for (StrategyNewBean strategy : strategies) {
/*  176 */       if (strategy.getId().intValue() == strategyId) {
/*  177 */         strategyExists = true;
/*  178 */         break;
/*      */       }
/*      */     }
/*      */ 
/*  182 */     if (strategyExists)
/*      */     {
/*  184 */       for (StrategyNewBean strategy : this.selectedStrategies) {
/*  185 */         if (strategy.getId().intValue() == strategyId) {
/*  186 */           return;
/*      */         }
/*      */       }
/*      */ 
/*  190 */       this.selectedStrategies.clear();
/*      */ 
/*  192 */       for (int i = 0; i < strategies.size(); i++)
/*      */       {
/*  194 */         StrategyNewBean strategy = (StrategyNewBean)strategies.get(i);
/*      */ 
/*  196 */         if (strategy.getId().intValue() == strategyId) {
/*  197 */           this.table.setRowSelectionInterval(i, i);
/*      */ 
/*  199 */           this.selectedRows = new int[] { i };
/*  200 */           this.selectedStrategies.add(strategy);
/*      */ 
/*  202 */           processStrategiesSelection();
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   public void addStrategyFromFile(File strategyFile, StrategyTreeNode strategyNode)
/*      */   {
/*  210 */     if ((strategyFile != null) && (strategyNode != null)) {
/*  211 */       StrategyNewBean strategyBean = strategyNode.getStrategy();
/*  212 */       strategyBean.setName(strategyFile.getName().substring(0, strategyFile.getName().lastIndexOf('.')));
/*  213 */       strategyBean.setStrategySourceFile(strategyFile);
/*      */ 
/*  215 */       this.table.addStrategy(strategyBean);
/*  216 */       reloadStrategy(strategyBean);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void addStrategyToRun(File strategyFile, String presetName)
/*      */   {
/*  229 */     if ((strategyFile != null) && (strategyFile.exists()))
/*      */     {
/*  231 */       StrategyNewBean strategyBean = null;
/*  232 */       int strategyRow = -1;
/*      */ 
/*  234 */       List strategies = ((StrategiesTableModel)this.table.getModel()).getStrategies();
/*      */ 
/*  236 */       for (int i = 0; i < strategies.size(); i++)
/*      */       {
/*  238 */         StrategyNewBean checkStrategy = (StrategyNewBean)strategies.get(i);
/*  239 */         String activePresetName = checkStrategy.getActivePreset().getName();
/*      */ 
/*  242 */         if ((strategyFile.compareTo(checkStrategy.getStrategyBinaryFile()) != 0) || ((presetName != null) && (!presetName.equals(activePresetName))) || (!checkStrategy.getStatus().equals(StrategyStatus.STOPPED)))
/*      */         {
/*      */           continue;
/*      */         }
/*  246 */         strategyRow = i;
/*  247 */         strategyBean = checkStrategy;
/*  248 */         break;
/*      */       }
/*      */ 
/*  252 */       if ((strategyBean == null) || (strategyRow < 0)) {
/*  253 */         strategyBean = this.toolbarController.createStrategyBean(strategyFile, this.presetsController, presetName);
/*      */ 
/*  255 */         if (strategyBean == null) {
/*  256 */           NotificationUtilsProvider.getNotificationUtils().postErrorMessage("Could not retrieve strategy from file " + strategyFile.getName());
/*  257 */           return;
/*      */         }
/*      */ 
/*  260 */         this.settingsStorage.saveStrategyNewBean(strategyBean);
/*  261 */         strategyRow = this.table.addStrategy(strategyBean);
/*  262 */         this.workspaceTreeController.strategyAdded(strategyBean);
/*      */       }
/*      */ 
/*  265 */       if ((presetName != null) && (!strategyBean.getActivePreset().getId().equals(presetName))) {
/*  266 */         StrategyPreset activePreset = this.presetsController.getStrategyPresetBy(strategyBean.getStrategyPresets(), presetName);
/*  267 */         if (activePreset == null)
/*      */         {
/*  269 */           NotificationUtilsProvider.getNotificationUtils().postErrorMessage("Could not start strategy " + strategyBean.getName() + " locally, preset " + presetName + " not found.");
/*  270 */           return;
/*      */         }
/*  272 */         strategyBean.setActivePreset(activePreset);
/*      */       }
/*      */ 
/*  276 */       startStrategy(strategyBean, strategyRow);
/*      */     }
/*      */   }
/*      */ 
/*      */   private void build()
/*      */   {
/*  282 */     addStartButton();
/*  283 */     addStopButton();
/*  284 */     addEditButton();
/*  285 */     addOpenButton();
/*  286 */     addRemoveButton();
/*  287 */     addStrategyLogButton();
/*  288 */     addPropertiesButton();
/*  289 */     addPresetsContainerPanel();
/*      */ 
/*  291 */     createPopupMenu();
/*      */ 
/*  293 */     this.table.addMouseListener(new MouseAdapter()
/*      */     {
/*      */       public void mousePressed(MouseEvent e)
/*      */       {
/*  298 */         StrategiesToolbar.access$002(StrategiesToolbar.this, StrategiesToolbar.this.table.getSelectedRows());
/*      */ 
/*  300 */         if ((StrategiesToolbar.this.selectedRows != null) && (StrategiesToolbar.this.selectedRows.length > 0))
/*      */         {
/*  302 */           StrategiesToolbar.this.selectedStrategies.clear();
/*      */ 
/*  304 */           StrategiesTableModel tableModel = (StrategiesTableModel)StrategiesToolbar.this.table.getModel();
/*  305 */           List strategies = tableModel.getStrategies();
/*      */ 
/*  307 */           for (int indx : StrategiesToolbar.this.selectedRows) {
/*  308 */             StrategyNewBean strategy = (StrategyNewBean)strategies.get(indx);
/*  309 */             StrategiesToolbar.this.selectedStrategies.add(strategy);
/*      */           }
/*  311 */           StrategiesToolbar.this.processStrategiesSelection();
/*      */         }
/*      */         else {
/*  314 */           StrategiesToolbar.this.disableAllToolbar();
/*      */         }
/*      */       }
/*      */ 
/*      */       public void mouseReleased(MouseEvent e)
/*      */       {
/*  320 */         showPopup(e);
/*      */       }
/*      */ 
/*      */       private void showPopup(MouseEvent e) {
/*  324 */         if ((e.isPopupTrigger()) && (StrategiesToolbar.this.table.getSelectedRow() != -1))
/*  325 */           StrategiesToolbar.this.strategiesPopupMenu.show(e.getComponent(), e.getX(), e.getY());
/*      */       }
/*      */     });
/*  334 */     StrategiesTableModel tableModel = (StrategiesTableModel)this.table.getModel();
/*  335 */     tableModel.addTableModelListener(new TableModelListener()
/*      */     {
/*      */       public void tableChanged(TableModelEvent e)
/*      */       {
/*  339 */         StrategiesToolbar.this.updateButtonsAndMenus(StrategiesToolbar.this.selectedStrategies);
/*      */       }
/*      */     });
/*      */   }
/*      */ 
/*      */   private void processStrategiesSelection()
/*      */   {
/*  347 */     boolean isSingleSelection = this.selectedStrategies.size() == 1;
/*      */ 
/*  349 */     for (StrategyNewBean selectedStrategy : this.selectedStrategies)
/*      */     {
/*  351 */       reloadStrategy(selectedStrategy);
/*      */ 
/*  353 */       if ((selectedStrategy.hasParameters()) && (isSingleSelection))
/*      */       {
/*  355 */         StrategyPresetsComboBoxModel comboBoxModel = new StrategyPresetsComboBoxModel(selectedStrategy.getStrategyPresets());
/*  356 */         comboBoxModel.setSelectedItem(selectedStrategy.getActivePreset());
/*      */ 
/*  358 */         this.presetComboBox.setModel(comboBoxModel);
/*  359 */         this.presetContainerPanel.setVisible(true);
/*  360 */         this.propertiesButton.setVisible(true);
/*      */       }
/*      */       else {
/*  363 */         this.presetContainerPanel.setVisible(false);
/*  364 */         this.propertiesButton.setVisible(false);
/*      */       }
/*      */ 
/*  367 */       if (isSingleSelection) {
/*  368 */         this.tabsAndFramesController.selectPanel(selectedStrategy.getId().intValue());
/*  369 */         this.startModeComboBox.setSelectedItem(selectedStrategy.getType());
/*      */       }
/*      */     }
/*      */ 
/*  373 */     updateButtonsAndMenus(this.selectedStrategies);
/*      */   }
/*      */ 
/*      */   private void createPopupMenu()
/*      */   {
/*  378 */     this.strategiesPopupMenu = new JPopupMenu();
/*      */ 
/*  380 */     this.startStrategyLocallyMenuItem = new JLocalizableMenuItem("strategies.button.start.locally");
/*      */ 
/*  382 */     this.startStrategyLocallyMenuItem.addActionListener(new ActionListener()
/*      */     {
/*      */       public void actionPerformed(ActionEvent e)
/*      */       {
/*  386 */         if (StrategiesToolbar.this.selectedRows.length > 0) {
/*  387 */           StrategiesToolbar.this.startModeComboBox.setSelectedItem(StrategyType.LOCAL);
/*      */ 
/*  389 */           for (int i = 0; i < StrategiesToolbar.this.selectedRows.length; i++) {
/*  390 */             int selectedRow = StrategiesToolbar.this.selectedRows[i];
/*  391 */             StrategyNewBean selectedStrategy = (StrategyNewBean)StrategiesToolbar.this.selectedStrategies.get(i);
/*  392 */             StrategiesToolbar.this.checkAndStartStrategy(selectedStrategy, selectedRow);
/*      */           }
/*      */         }
/*      */       }
/*      */     });
/*  397 */     this.strategiesPopupMenu.add(this.startStrategyLocallyMenuItem);
/*      */ 
/*  399 */     this.startStrategyRemotelyMenuItem = new JLocalizableMenuItem("strategies.button.start.remotely");
/*      */ 
/*  401 */     this.startStrategyRemotelyMenuItem.addActionListener(new ActionListener()
/*      */     {
/*      */       public void actionPerformed(ActionEvent e)
/*      */       {
/*  405 */         if (StrategiesToolbar.this.selectedRows.length > 0) {
/*  406 */           StrategiesToolbar.this.startModeComboBox.setSelectedItem(StrategyType.REMOTE);
/*      */ 
/*  408 */           for (int i = 0; i < StrategiesToolbar.this.selectedRows.length; i++) {
/*  409 */             int selectedRow = StrategiesToolbar.this.selectedRows[i];
/*  410 */             StrategyNewBean selectedStrategy = (StrategyNewBean)StrategiesToolbar.this.selectedStrategies.get(i);
/*  411 */             StrategiesToolbar.this.checkAndStartStrategy(selectedStrategy, selectedRow);
/*      */           }
/*      */         }
/*      */       }
/*      */     });
/*  416 */     this.strategiesPopupMenu.add(this.startStrategyRemotelyMenuItem);
/*      */ 
/*  418 */     this.stopStrategyMenuItem = new JLocalizableMenuItem("strategies.button.stop");
/*      */ 
/*  420 */     this.stopStrategyMenuItem.addActionListener(new ActionListener()
/*      */     {
/*      */       public void actionPerformed(ActionEvent e) {
/*  423 */         if (StrategiesToolbar.this.selectedRows.length > 0)
/*      */         {
/*  425 */           StrategiesTableModel tableModel = (StrategiesTableModel)StrategiesToolbar.this.table.getModel();
/*      */ 
/*  427 */           for (int i = 0; i < StrategiesToolbar.this.selectedRows.length; i++) {
/*  428 */             StrategyNewBean selectedStrategy = (StrategyNewBean)StrategiesToolbar.this.selectedStrategies.get(i);
/*  429 */             new StopStrategyTaskAction(tableModel, StrategiesToolbar.this.tabbedPane, StrategiesToolbar.this.workspaceTreeController).execute(selectedStrategy);
/*      */           }
/*      */         }
/*      */       }
/*      */     });
/*  434 */     this.strategiesPopupMenu.add(this.stopStrategyMenuItem);
/*      */ 
/*  436 */     JMenuItem strategyLogMenuItem = new JLocalizableMenuItem("menu.item.view.remote.strategy.log");
/*  437 */     strategyLogMenuItem.addActionListener(new ActionListener()
/*      */     {
/*      */       public void actionPerformed(ActionEvent e)
/*      */       {
/*  441 */         StrategiesToolbar.this.openStrategyLog();
/*      */       }
/*      */     });
/*  445 */     this.strategiesPopupMenu.add(strategyLogMenuItem);
/*  446 */     this.strategiesPopupMenu.add(new JSeparator());
/*      */ 
/*  448 */     this.editStrategyMenuItem = new JLocalizableMenuItem("strategies.button.edit");
/*  449 */     this.editStrategyMenuItem.addActionListener(new ActionListener()
/*      */     {
/*      */       public void actionPerformed(ActionEvent e)
/*      */       {
/*  454 */         for (StrategyNewBean selectedStrategy : StrategiesToolbar.this.selectedStrategies)
/*  455 */           StrategiesToolbar.this.openEditor(selectedStrategy);
/*      */       }
/*      */     });
/*  459 */     this.strategiesPopupMenu.add(this.editStrategyMenuItem);
/*      */ 
/*  461 */     this.compileStrategyMenuItem = new JLocalizableMenuItem("strategies.button.compile");
/*  462 */     this.compileStrategyMenuItem.addActionListener(new ActionListener()
/*      */     {
/*      */       public void actionPerformed(ActionEvent e)
/*      */       {
/*  467 */         for (StrategyNewBean selectedStrategy : StrategiesToolbar.this.selectedStrategies)
/*  468 */           StrategiesToolbar.this.compileStrategy(selectedStrategy, null);
/*      */       }
/*      */     });
/*  472 */     this.strategiesPopupMenu.add(this.compileStrategyMenuItem);
/*      */ 
/*  474 */     this.testerStrategyMenuItem = new JLocalizableMenuItem("strategies.button.test");
/*  475 */     this.testerStrategyMenuItem.addActionListener(new ActionListener()
/*      */     {
/*      */       public void actionPerformed(ActionEvent e)
/*      */       {
/*  479 */         for (StrategyNewBean selectedStrategy : StrategiesToolbar.this.selectedStrategies)
/*  480 */           StrategiesToolbar.this.openTester(selectedStrategy);
/*      */       }
/*      */     });
/*  484 */     this.strategiesPopupMenu.add(this.testerStrategyMenuItem);
/*      */ 
/*  487 */     this.commentStrategyMenuItem = new JLocalizableMenuItem("strategies.button.comment");
/*  488 */     this.commentStrategyMenuItem.addActionListener(new ActionListener()
/*      */     {
/*      */       public void actionPerformed(ActionEvent e)
/*      */       {
/*  493 */         if (StrategiesToolbar.this.selectedRows.length > 0) {
/*  494 */           int selectedRow = StrategiesToolbar.this.selectedRows[0];
/*  495 */           int columnIndx = StrategiesToolbar.this.table.convertColumnIndexToView(8);
/*      */ 
/*  497 */           if (StrategiesToolbar.this.table.editCellAt(selectedRow, columnIndx))
/*  498 */             StrategiesToolbar.this.table.getCellEditor(selectedRow, columnIndx).shouldSelectCell(new EventObject(this));
/*      */         }
/*      */       }
/*      */     });
/*  503 */     this.strategiesPopupMenu.add(this.commentStrategyMenuItem);
/*  504 */     this.strategiesPopupMenu.add(new JSeparator());
/*      */ 
/*  506 */     this.removeStrategyMenuItem = new JLocalizableMenuItem("strategies.button.remove");
/*      */ 
/*  508 */     this.removeStrategyMenuItem.addActionListener(new ActionListener()
/*      */     {
/*      */       public void actionPerformed(ActionEvent e)
/*      */       {
/*  512 */         List removed = StrategiesToolbar.this.toolbarController.deleteStrategies(StrategiesToolbar.this.table);
/*      */ 
/*  514 */         for (StrategyNewBean removedStrategy : removed) {
/*  515 */           StrategiesToolbar.this.workspaceTreeController.strategyRemoved(removedStrategy);
/*      */         }
/*      */ 
/*  518 */         StrategiesToolbar.this.propertiesButton.setVisible(false);
/*  519 */         StrategiesToolbar.this.presetContainerPanel.setVisible(false);
/*      */ 
/*  521 */         StrategiesToolbar.access$002(StrategiesToolbar.this, null);
/*  522 */         StrategiesToolbar.this.selectedStrategies.clear();
/*      */ 
/*  524 */         StrategiesToolbar.this.disableAllToolbar();
/*      */       }
/*      */     });
/*  527 */     this.strategiesPopupMenu.add(this.removeStrategyMenuItem);
/*      */   }
/*      */ 
/*      */   private void addPresetsContainerPanel() {
/*  531 */     this.presetContainerPanel = new JPanel();
/*  532 */     this.presetContainerPanel.setOpaque(false);
/*  533 */     this.presetContainerPanel.setLayout(new FlowLayout(0, 2, 2));
/*      */ 
/*  535 */     this.presetComboBox = new JComboBox();
/*  536 */     this.presetComboBox.setUI(new StrategiesComboBoxUI());
/*  537 */     this.presetComboBox.setPreferredSize(new Dimension(100, 22));
/*      */ 
/*  539 */     this.presetComboBox.addItemListener(new ItemListener()
/*      */     {
/*      */       public void itemStateChanged(ItemEvent e) {
/*  542 */         if (e.getStateChange() == 1)
/*      */         {
/*  544 */           Object selectedItem = StrategiesToolbar.this.presetComboBox.getSelectedItem();
/*  545 */           if (((selectedItem instanceof StrategyPreset)) && (StrategiesToolbar.this.selectedStrategies.size() == 1))
/*      */           {
/*  547 */             int selectedRow = StrategiesToolbar.this.selectedRows[0];
/*  548 */             StrategyNewBean selectedStrategy = (StrategyNewBean)StrategiesToolbar.this.selectedStrategies.get(0);
/*      */ 
/*  550 */             selectedStrategy.setActivePreset((StrategyPreset)selectedItem);
/*  551 */             StrategiesToolbar.this.settingsStorage.saveStrategyNewBean(selectedStrategy);
/*      */ 
/*  553 */             StrategiesTableModel tableModel = (StrategiesTableModel)StrategiesToolbar.this.table.getModel();
/*  554 */             tableModel.fireTableCellUpdated(selectedRow, 6);
/*      */           }
/*      */         }
/*      */       }
/*      */     });
/*  560 */     this.presetContainerPanel.add(this.presetComboBox);
/*      */ 
/*  562 */     this.presetContainerPanel.setVisible(false);
/*      */ 
/*  564 */     add(this.presetContainerPanel);
/*      */   }
/*      */ 
/*      */   private void addPropertiesButton() {
/*  568 */     this.propertiesButton = new StrategiesToggleButton(StrategiesToolbarUIConstants.STRATEGIES_PROPERTIES_ICON, StrategiesToolbarUIConstants.STRATEGIES_PROPERTIES_FADED_ICON);
/*      */ 
/*  570 */     this.propertiesButton.setToolTipKey("strategies.button.parameters");
/*      */ 
/*  572 */     this.propertiesButton.addActionListener(new ActionListener()
/*      */     {
/*      */       public void actionPerformed(ActionEvent e)
/*      */       {
/*  577 */         if ((StrategiesToolbar.this.selectedRows.length == 1) && (StrategiesToolbar.this.selectedStrategies.size() == 1)) {
/*  578 */           int selectedRow = StrategiesToolbar.this.selectedRows[0];
/*  579 */           StrategyNewBean selectedStrategy = (StrategyNewBean)StrategiesToolbar.this.selectedStrategies.get(0);
/*      */ 
/*  581 */           StrategiesToolbar.this.reloadStrategy(selectedStrategy);
/*      */ 
/*  583 */           StrategyPresetsDialog dialog = new StrategyPresetsDialog(selectedStrategy, false);
/*      */ 
/*  585 */           dialog.addWindowListener(new WindowAdapter(dialog, selectedStrategy, selectedRow)
/*      */           {
/*      */             public void windowClosed(WindowEvent e)
/*      */             {
/*  589 */               if (this.val$dialog.doSetParams())
/*      */               {
/*  591 */                 StrategiesToolbar.this.presetComboBox.setModel(new StrategyPresetsComboBoxModel(this.val$selectedStrategy.getStrategyPresets()));
/*  592 */                 StrategiesToolbar.this.presetComboBox.setSelectedItem(this.val$selectedStrategy.getActivePreset());
/*      */ 
/*  594 */                 StrategiesTableModel tableModel = (StrategiesTableModel)StrategiesToolbar.this.table.getModel();
/*  595 */                 tableModel.fireTableCellUpdated(this.val$selectedRow, 6);
/*      */ 
/*  597 */                 if (this.val$selectedStrategy.isStartingOrRunning()) {
/*  598 */                   IStrategyAction newAction = new ChangeStrategyParametersTaskAction(tableModel, StrategiesToolbar.this.tabbedPane, StrategiesToolbar.this.workspaceTreeController);
/*  599 */                   newAction.execute(this.val$selectedStrategy);
/*      */                 }
/*      */               }
/*      */             }
/*      */           });
/*      */         }
/*      */       }
/*      */     });
/*  609 */     this.propertiesButton.setVisible(false);
/*      */ 
/*  611 */     add(this.propertiesButton);
/*      */   }
/*      */ 
/*      */   private void addRemoveButton()
/*      */   {
/*  616 */     this.removeStrategyButton = new StrategiesButton(StrategiesToolbarUIConstants.STRATEGIES_DELETE_ICON, StrategiesToolbarUIConstants.STRATEGIES_DELETE_FADED_ICON);
/*      */ 
/*  618 */     this.removeStrategyButton.setToolTipKey("strategies.button.remove");
/*  619 */     this.removeStrategyButton.addActionListener(new ActionListener()
/*      */     {
/*      */       public void actionPerformed(ActionEvent e) {
/*  622 */         List removed = StrategiesToolbar.this.toolbarController.deleteStrategies(StrategiesToolbar.this.table);
/*  623 */         for (StrategyNewBean removedStrategy : removed) {
/*  624 */           StrategiesToolbar.this.workspaceTreeController.strategyRemoved(removedStrategy);
/*  625 */           MessagePanelManager.getInstance().removePanel(removedStrategy.getId().toString());
/*      */         }
/*      */ 
/*  628 */         if (removed.size() > 0) {
/*  629 */           StrategiesToolbar.this.refreshTesters();
/*      */         }
/*      */ 
/*  632 */         StrategiesToolbar.this.propertiesButton.setVisible(false);
/*  633 */         StrategiesToolbar.this.presetContainerPanel.setVisible(false);
/*      */ 
/*  635 */         StrategiesToolbar.access$002(StrategiesToolbar.this, null);
/*  636 */         StrategiesToolbar.this.selectedStrategies.clear();
/*      */ 
/*  638 */         StrategiesToolbar.this.disableAllToolbar();
/*      */       }
/*      */     });
/*  642 */     this.removeStrategyButton.setEnabled(false);
/*      */ 
/*  644 */     add(this.removeStrategyButton);
/*  645 */     add(Box.createHorizontalStrut(10));
/*      */   }
/*      */ 
/*      */   private void addStrategyLogButton() {
/*  649 */     this.strategyLogButton = new StrategiesToggleButton(StrategiesToolbarUIConstants.STRATEGIES_REMOTE_LOG_ICON, StrategiesToolbarUIConstants.STRATEGIES_REMOTE_LOG_FADED_ICON);
/*      */ 
/*  651 */     this.strategyLogButton.setToolTipKey("menu.item.view.remote.strategy.log");
/*  652 */     this.strategyLogButton.addActionListener(new ActionListener()
/*      */     {
/*      */       public void actionPerformed(ActionEvent e)
/*      */       {
/*  656 */         StrategiesToolbar.this.openStrategyLog();
/*      */       }
/*      */     });
/*  660 */     this.strategyLogButton.setEnabled(false);
/*      */ 
/*  662 */     add(this.strategyLogButton);
/*  663 */     add(Box.createHorizontalStrut(10));
/*      */   }
/*      */ 
/*      */   private void addEditButton() {
/*  667 */     this.editStrategyButton = new StrategiesButton(StrategiesToolbarUIConstants.STRATEGIES_PROPERTIES_EDIT_ICON, StrategiesToolbarUIConstants.STRATEGIES_PROPERTIES_EDIT_FADED_ICON);
/*      */ 
/*  669 */     this.editStrategyButton.setToolTipKey("strategies.button.edit");
/*  670 */     this.editStrategyButton.setEnabled(false);
/*  671 */     this.editStrategyButton.addActionListener(new ActionListener()
/*      */     {
/*      */       public void actionPerformed(ActionEvent e)
/*      */       {
/*  675 */         for (StrategyNewBean strategy : StrategiesToolbar.this.selectedStrategies)
/*  676 */           StrategiesToolbar.this.openEditor(strategy);
/*      */       }
/*      */     });
/*  681 */     this.compileStrategyButton = new StrategiesButton(StrategiesToolbarUIConstants.STRATEGIES_COMPILE_ICON, StrategiesToolbarUIConstants.STRATEGIES_COMPILE_FADED_ICON);
/*      */ 
/*  684 */     this.compileStrategyButton.setToolTipKey("strategies.button.compile");
/*  685 */     this.compileStrategyButton.setEnabled(false);
/*      */ 
/*  687 */     this.compileStrategyButton.addActionListener(new ActionListener()
/*      */     {
/*      */       public void actionPerformed(ActionEvent e)
/*      */       {
/*  691 */         for (StrategyNewBean strategy : StrategiesToolbar.this.selectedStrategies)
/*  692 */           StrategiesToolbar.this.compileStrategy(strategy, null);
/*      */       }
/*      */     });
/*  697 */     this.testerStrategyButton = new StrategiesButton(StrategiesToolbarUIConstants.STRATEGIES_TESTER_ICON, StrategiesToolbarUIConstants.STRATEGIES_TESTER_FADED_ICON);
/*      */ 
/*  699 */     this.testerStrategyButton.setToolTipKey("strategies.button.test");
/*  700 */     this.testerStrategyButton.setEnabled(false);
/*      */ 
/*  702 */     this.testerStrategyButton.addActionListener(new ActionListener()
/*      */     {
/*      */       public void actionPerformed(ActionEvent e)
/*      */       {
/*  706 */         for (StrategyNewBean strategy : StrategiesToolbar.this.selectedStrategies)
/*  707 */           StrategiesToolbar.this.openTester(strategy);
/*      */       }
/*      */     });
/*  712 */     add(this.editStrategyButton);
/*  713 */     add(this.compileStrategyButton);
/*  714 */     add(this.testerStrategyButton);
/*  715 */     add(Box.createHorizontalStrut(10));
/*      */   }
/*      */ 
/*      */   private void addOpenButton() {
/*  719 */     this.openStrategyButton = new StrategiesButton(StrategiesToolbarUIConstants.STRATEGIES_NEW_ICON, StrategiesToolbarUIConstants.STRATEGIES_NEW_ICON);
/*      */ 
/*  721 */     this.openStrategyButton.setToolTipKey("strategies.button.open");
/*  722 */     this.openStrategyButton.addActionListener(new ActionListener()
/*      */     {
/*      */       public void actionPerformed(ActionEvent e)
/*      */       {
/*  726 */         StrategiesToolbar.this.selectStrategiesFiles(true);
/*      */       }
/*      */     });
/*  730 */     add(this.openStrategyButton);
/*      */   }
/*      */ 
/*      */   private void addStartButton()
/*      */   {
/*  735 */     this.startContainerPanel = new JPanel();
/*  736 */     this.startContainerPanel.setOpaque(false);
/*  737 */     this.startContainerPanel.setLayout(new FlowLayout(0, 0, 2));
/*  738 */     this.startContainerPanel.setBorder(new EmptyBorder(0, 5, 0, 0));
/*      */ 
/*  740 */     this.startStrategyButton = new StrategiesButton(StrategiesToolbarUIConstants.STRATEGIES_PLAY_ICON, StrategiesToolbarUIConstants.STRATEGIES_PLAY_FADED_ICON);
/*      */ 
/*  742 */     this.startStrategyButton.setToolTipKey("strategies.button.start");
/*      */ 
/*  744 */     this.startStrategyButton.setEnabled(false);
/*      */ 
/*  746 */     this.startStrategyButton.addActionListener(new ActionListener()
/*      */     {
/*      */       public void actionPerformed(ActionEvent e) {
/*  749 */         for (int i = 0; i < StrategiesToolbar.this.selectedRows.length; i++)
/*  750 */           StrategiesToolbar.this.checkAndStartStrategy((StrategyNewBean)StrategiesToolbar.this.selectedStrategies.get(i), StrategiesToolbar.this.selectedRows[i]);
/*      */       }
/*      */     });
/*  755 */     this.startModeComboBox = new JComboBox(StrategyType.values());
/*  756 */     this.startModeComboBox.setUI(new ResizableComboBoxUI(new Dimension(92, 22)));
/*      */ 
/*  758 */     this.startModeComboBox.addItemListener(new Object()
/*      */     {
/*      */       public void itemStateChanged(ItemEvent e) {
/*  761 */         if (e.getStateChange() == 1) {
/*  762 */           Object selectedItem = StrategiesToolbar.this.startModeComboBox.getSelectedItem();
/*  763 */           if (((selectedItem instanceof StrategyType)) && 
/*  764 */             (StrategiesToolbar.this.selectedRows != null) && (StrategiesToolbar.this.selectedRows.length > 0) && (StrategiesToolbar.this.selectedStrategies.size() > 0))
/*      */           {
/*  766 */             StrategiesTableModel tableModel = (StrategiesTableModel)StrategiesToolbar.this.table.getModel();
/*      */ 
/*  768 */             for (int i = 0; i < StrategiesToolbar.this.selectedRows.length; i++)
/*      */             {
/*  770 */               StrategyNewBean selectedStrategy = (StrategyNewBean)StrategiesToolbar.this.selectedStrategies.get(i);
/*  771 */               selectedStrategy.setType((StrategyType)selectedItem);
/*  772 */               StrategiesToolbar.this.settingsStorage.saveStrategyNewBean(selectedStrategy);
/*      */ 
/*  774 */               tableModel.fireTableCellUpdated(StrategiesToolbar.this.selectedRows[i], 5);
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/*      */     });
/*  782 */     this.startContainerPanel.add(this.startModeComboBox);
/*  783 */     this.startContainerPanel.add(Box.createHorizontalStrut(2));
/*  784 */     this.startContainerPanel.add(this.startStrategyButton);
/*      */ 
/*  786 */     add(this.startContainerPanel);
/*      */   }
/*      */ 
/*      */   private void addStopButton() {
/*  790 */     this.stopStrategyButton = new StrategiesButton(StrategiesToolbarUIConstants.STRATEGIES_STOP_ICON, StrategiesToolbarUIConstants.STRATEGIES_STOP_FADED_ICON);
/*      */ 
/*  792 */     this.stopStrategyButton.setToolTipKey("strategies.button.stop");
/*  793 */     this.stopStrategyButton.setEnabled(false);
/*      */ 
/*  795 */     this.stopStrategyButton.addActionListener(new ActionListener()
/*      */     {
/*      */       public void actionPerformed(ActionEvent e)
/*      */       {
/*  799 */         StrategiesTableModel tableModel = (StrategiesTableModel)StrategiesToolbar.this.table.getModel();
/*      */ 
/*  801 */         for (StrategyNewBean selectedStrategy : StrategiesToolbar.this.selectedStrategies)
/*  802 */           if (selectedStrategy.isStartingOrRunning())
/*  803 */             new StopStrategyTaskAction(tableModel, StrategiesToolbar.this.tabbedPane, StrategiesToolbar.this.workspaceTreeController).execute(selectedStrategy);
/*      */       }
/*      */     });
/*  809 */     this.stopAllStrategiesButton = new StrategiesButton(StrategiesToolbarUIConstants.STRATEGIES_STOP_ALL_ICON, StrategiesToolbarUIConstants.STRATEGIES_STOP_ALL_FADED_ICON);
/*      */ 
/*  811 */     this.stopAllStrategiesButton.setToolTipKey("strategies.button.stop.all");
/*  812 */     this.stopAllStrategiesButton.setEnabled(false);
/*      */ 
/*  814 */     this.stopAllStrategiesButton.addActionListener(new ActionListener()
/*      */     {
/*      */       public void actionPerformed(ActionEvent e)
/*      */       {
/*  819 */         StrategiesTableModel tableModel = (StrategiesTableModel)StrategiesToolbar.this.table.getModel();
/*      */ 
/*  821 */         for (StrategyNewBean selectedStrategy : tableModel.getStrategies())
/*  822 */           if (selectedStrategy.isStartingOrRunning())
/*  823 */             new StopStrategyTaskAction(tableModel, StrategiesToolbar.this.tabbedPane, StrategiesToolbar.this.workspaceTreeController).execute(selectedStrategy);
/*      */       }
/*      */     });
/*  829 */     add(this.stopStrategyButton);
/*  830 */     add(this.stopAllStrategiesButton);
/*  831 */     add(Box.createHorizontalStrut(10));
/*      */   }
/*      */ 
/*      */   private List<StrategyNewBean> selectStrategiesFiles(boolean openEditor)
/*      */   {
/*  836 */     List addedStrategies = this.toolbarController.addStrategies(this.table, this, this.presetsController);
/*      */ 
/*  839 */     for (StrategyNewBean strategy : addedStrategies) {
/*  840 */       this.workspaceTreeController.strategyAdded(strategy);
/*  841 */       if (openEditor) {
/*  842 */         openEditor(strategy);
/*      */       }
/*      */     }
/*      */ 
/*  846 */     if (addedStrategies.size() > 0) {
/*  847 */       refreshTesters();
/*      */     }
/*      */ 
/*  850 */     return addedStrategies;
/*      */   }
/*      */ 
/*      */   private void checkAndStartStrategy(StrategyNewBean strategy, int strategyRow)
/*      */   {
/*  855 */     if (strategy != null)
/*      */     {
/*  857 */       if ((strategy.getStrategyBinaryFile() == null) || ((strategy.getStrategySourceFile() != null) && (strategy.getStrategySourceFile().lastModified() > strategy.getStrategyBinaryFile().lastModified())))
/*      */       {
/*  860 */         compileStrategy(strategy, new AppActionEvent(this, false, true, strategy, strategyRow)
/*      */         {
/*      */           public void doAction() {
/*  863 */             StrategiesToolbar.this.startStrategy(this.val$strategy, this.val$strategyRow);
/*      */           } } );
/*      */       }
/*  867 */       else startStrategy(strategy, strategyRow);
/*      */     }
/*      */   }
/*      */ 
/*      */   private void startStrategy(StrategyNewBean strategy, int strategyRow)
/*      */   {
/*  874 */     if ((strategyRow != -1) && (strategy != null))
/*      */     {
/*  876 */       reloadStrategy(strategy);
/*      */ 
/*  878 */       if (strategy.hasParameters())
/*      */       {
/*  880 */         StrategyPresetsDialog dialog = new StrategyPresetsDialog(strategy, true);
/*  881 */         dialog.addWindowListener(new WindowAdapter(dialog, strategy, strategyRow)
/*      */         {
/*      */           public void windowClosed(WindowEvent e)
/*      */           {
/*  885 */             if (this.val$dialog.doSetParams()) {
/*  886 */               StrategiesToolbar.this.presetComboBox.setSelectedItem(this.val$strategy.getActivePreset());
/*  887 */               StrategiesToolbar.this.startStrategyTimer(this.val$strategy, this.val$strategyRow);
/*      */             }
/*      */           } } );
/*      */       } else {
/*  892 */         startStrategyTimer(strategy, strategyRow);
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   private void startStrategyTimer(StrategyNewBean strategy, int strategyRow) {
/*  898 */     strategy.resetDates();
/*      */ 
/*  901 */     if (strategy.getType().equals(StrategyType.LOCAL)) {
/*  902 */       strategy.setStartTime(new Date());
/*      */     }
/*      */ 
/*  905 */     StrategiesTableModel tableModel = (StrategiesTableModel)this.table.getModel();
/*      */ 
/*  907 */     Timer timer = new Timer(1000, new ElapsedTimeActionListener(strategyRow, tableModel, strategy));
/*  908 */     timer.setInitialDelay(0);
/*  909 */     strategy.setTimer(timer);
/*      */ 
/*  911 */     new RunStrategyTaskAction(tableModel, strategyRow, this.tabbedPane, this.workspaceTreeController).execute(strategy);
/*      */   }
/*      */ 
/*      */   private boolean reloadStrategy(StrategyNewBean strategy)
/*      */   {
/*  916 */     if (((strategy.getStrategyBinaryFile() == null) || (!strategy.getStrategyBinaryFile().exists())) && (strategy.getStrategySourceFile() != null))
/*      */     {
/*  918 */       String binaryFilePath = strategy.getStrategySourceFile().getAbsolutePath().substring(0, strategy.getStrategySourceFile().getAbsolutePath().lastIndexOf('.')) + ".jfx";
/*  919 */       File binaryFile = new File(binaryFilePath);
/*  920 */       if (!binaryFile.exists()) {
/*  921 */         binaryFile = null;
/*      */       }
/*      */ 
/*  924 */       strategy.setStrategyBinaryFile(binaryFile);
/*  925 */       this.settingsStorage.saveStrategyNewBean(strategy);
/*      */     }
/*      */ 
/*  929 */     if ((strategy.getStatus().equals(StrategyStatus.STOPPED)) && (strategy.getStrategyBinaryFile() != null) && (strategy.getStrategyBinaryFile().lastModified() != strategy.getLastModifiedDate()))
/*      */     {
/*      */       try
/*      */       {
/*  934 */         StrategyBinaryLoader.loadStrategy(strategy.getStrategyBinaryFile(), strategy);
/*      */       }
/*      */       catch (IncorrectClassTypeException ex) {
/*  937 */         return false;
/*      */       }
/*      */ 
/*  940 */       List updatePresets = this.presetsController.loadPresets(strategy);
/*  941 */       strategy.setStrategyPresets(updatePresets);
/*      */ 
/*  943 */       if (strategy.getActivePreset() == null) {
/*  944 */         strategy.setActivePreset(this.presetsController.getStrategyPresetBy(updatePresets, "DEFAULT_PRESET_ID"));
/*      */       }
/*      */       else {
/*  947 */         StrategyPreset activePreset = this.presetsController.getStrategyPresetBy(updatePresets, strategy.getActivePreset().getId());
/*      */ 
/*  949 */         strategy.setActivePreset(activePreset);
/*      */       }
/*      */ 
/*  952 */       return true;
/*      */     }
/*      */ 
/*  955 */     return false;
/*      */   }
/*      */ 
/*      */   private void openEditor(StrategyNewBean strategy)
/*      */   {
/*  960 */     if (!this.tabsAndFramesController.selectPanel(strategy.getId().intValue()))
/*      */     {
/*  963 */       if ((strategy.getStrategySourceFile() != null) && (strategy.getStrategySourceFile().exists()))
/*      */       {
/*  965 */         this.tabsAndFramesController.addServiceSourceEditor(strategy.getId().intValue(), strategy.getStrategySourceFile().getName(), strategy.getStrategySourceFile(), ServiceSourceType.STRATEGY, this.settingsStorage.isFrameUndocked(this.settingsStorage.getMainFramePreferencesNode(), strategy.getId()), false);
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   private void openStrategyLog()
/*      */   {
/*  979 */     boolean remoteOpenedFlag = false;
/*      */ 
/*  981 */     for (StrategyNewBean strategyBean : this.selectedStrategies)
/*  982 */       if (StrategyType.LOCAL.equals(strategyBean.getType())) {
/*  983 */         MessagePanelManager.getInstance().getPanel(strategyBean.getId().toString(), strategyBean.getName(), true);
/*  984 */       } else if (!remoteOpenedFlag) {
/*  985 */         new ViewRemoteStrategyLogAction().actionPerformed(null);
/*  986 */         remoteOpenedFlag = true;
/*      */       }
/*      */   }
/*      */ 
/*      */   private void openTester(StrategyNewBean strategy)
/*      */   {
/*  993 */     StrategyWrapper strategyWrapper = new StrategyWrapper();
/*  994 */     strategyWrapper.setBinaryFile(strategy.getStrategyBinaryFile());
/*  995 */     strategyWrapper.setSourceFile(strategy.getStrategySourceFile());
/*  996 */     strategyWrapper.setNewUnsaved(false);
/*      */ 
/*  998 */     JForexClientFormLayoutManager layoutManager = (JForexClientFormLayoutManager)GreedContext.get("layoutManager");
/*      */ 
/* 1001 */     StrategyTestPanel strategyTestPanel = layoutManager.getStrategyTestPanel(strategy.getName(), false);
/* 1002 */     if ((strategyTestPanel == null) || (strategyTestPanel.isBusy())) {
/* 1003 */       strategyTestPanel = layoutManager.addStrategyTesterPanel(-1, false, true);
/*      */     }
/*      */ 
/* 1006 */     strategyTestPanel.selectStrategy(strategy);
/* 1007 */     layoutManager.selectStrategyTestPanel(strategyTestPanel);
/*      */   }
/*      */ 
/*      */   private void compileStrategy(StrategyNewBean strategy, AppActionEvent action)
/*      */   {
/* 1012 */     ServiceSourceEditorPanel serviceSourceEditorPanel = this.tabsAndFramesController.getEditorPanelByPanelId(strategy.getId().intValue());
/* 1013 */     if ((serviceSourceEditorPanel != null) && (serviceSourceEditorPanel.save())) {
/* 1014 */       CompileStrategyAction compileAction = new CompileStrategyAction(this, strategy, action);
/* 1015 */       GreedContext.publishEvent(compileAction);
/*      */     }
/*      */   }
/*      */ 
/*      */   private void updateButtonsAndMenus(List<StrategyNewBean> strategies)
/*      */   {
/* 1021 */     boolean isInProgress = true;
/* 1022 */     for (StrategyNewBean strategy : strategies) {
/* 1023 */       isInProgress &= !strategy.getStatus().equals(StrategyStatus.STOPPED);
/*      */     }
/*      */ 
/* 1026 */     boolean hasBinaryFile = true;
/* 1027 */     for (StrategyNewBean strategy : strategies) {
/* 1028 */       hasBinaryFile &= strategy.getStrategyBinaryFile() != null;
/*      */     }
/*      */ 
/* 1031 */     boolean isEditable = true;
/* 1032 */     for (StrategyNewBean strategy : strategies) {
/* 1033 */       isEditable &= ((strategy.getStrategySourceFile() != null) && (strategy.getStrategySourceFile().exists()));
/*      */     }
/*      */ 
/* 1036 */     this.startStrategyButton.setEnabled(!isInProgress);
/* 1037 */     this.startStrategyLocallyMenuItem.setEnabled(!isInProgress);
/* 1038 */     this.startStrategyRemotelyMenuItem.setEnabled(!isInProgress);
/*      */ 
/* 1040 */     this.removeStrategyButton.setEnabled(!isInProgress);
/* 1041 */     this.removeStrategyMenuItem.setEnabled(!isInProgress);
/*      */ 
/* 1043 */     this.stopStrategyButton.setEnabled(isInProgress);
/* 1044 */     this.stopStrategyMenuItem.setEnabled(isInProgress);
/*      */ 
/* 1046 */     this.startModeComboBox.setEnabled(!isInProgress);
/*      */ 
/* 1048 */     this.editStrategyButton.setEnabled(isEditable);
/* 1049 */     this.editStrategyMenuItem.setEnabled(isEditable);
/*      */ 
/* 1051 */     this.compileStrategyButton.setEnabled((isEditable) && (!isInProgress));
/* 1052 */     this.compileStrategyMenuItem.setEnabled((isEditable) && (!isInProgress));
/*      */ 
/* 1054 */     this.testerStrategyButton.setEnabled(hasBinaryFile);
/* 1055 */     this.testerStrategyMenuItem.setEnabled(hasBinaryFile);
/*      */ 
/* 1057 */     this.stopAllStrategiesButton.setEnabled(false);
/*      */ 
/* 1059 */     StrategiesTableModel model = (StrategiesTableModel)this.table.getModel();
/* 1060 */     for (StrategyNewBean strategyBean : model.getStrategies()) {
/* 1061 */       if (strategyBean.isStartingOrRunning()) {
/* 1062 */         this.stopAllStrategiesButton.setEnabled(true);
/* 1063 */         break;
/*      */       }
/*      */     }
/*      */ 
/* 1067 */     this.commentStrategyMenuItem.setEnabled(strategies.size() == 1);
/* 1068 */     this.strategyLogButton.setEnabled(strategies.size() > 0);
/*      */   }
/*      */ 
/*      */   private void disableAllToolbar() {
/* 1072 */     this.startStrategyButton.setEnabled(false);
/* 1073 */     this.removeStrategyButton.setEnabled(false);
/* 1074 */     this.stopStrategyButton.setEnabled(false);
/* 1075 */     this.startModeComboBox.setEnabled(false);
/* 1076 */     this.editStrategyButton.setEnabled(false);
/* 1077 */     this.compileStrategyButton.setEnabled(false);
/* 1078 */     this.testerStrategyButton.setEnabled(false);
/* 1079 */     this.stopAllStrategiesButton.setEnabled(false);
/* 1080 */     this.propertiesButton.setVisible(false);
/* 1081 */     this.presetContainerPanel.setVisible(false);
/* 1082 */     this.strategyLogButton.setEnabled(false);
/*      */   }
/*      */ 
/*      */   private void refreshTesters() {
/* 1086 */     List testPanels = this.tabbedPane.getStrategyTestPanels();
/* 1087 */     for (StrategyTestPanel testPanel : testPanels)
/* 1088 */       testPanel.reloadStrategies();
/*      */   }
/*      */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.strategy.tab.toolbar.StrategiesToolbar
 * JD-Core Version:    0.6.0
 */