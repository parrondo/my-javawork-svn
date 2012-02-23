/*     */ package com.dukascopy.dds2.greed.gui.component.moverview;
/*     */ 
/*     */ import com.dukascopy.dds2.greed.GreedContext;
/*     */ import com.dukascopy.dds2.greed.gui.ClientForm;
/*     */ import com.dukascopy.dds2.greed.gui.DealPanel;
/*     */ import com.dukascopy.dds2.greed.gui.GuiUtilsAndConstants;
/*     */ import com.dukascopy.dds2.greed.gui.InstrumentAvailabilityManager;
/*     */ import com.dukascopy.dds2.greed.gui.SwingWorker;
/*     */ import com.dukascopy.dds2.greed.gui.component.WorkspacePanel;
/*     */ import com.dukascopy.dds2.greed.gui.component.moverview.config.MarketOverviewConfig;
/*     */ import com.dukascopy.dds2.greed.gui.component.moverview.config.MiniPanelConfig;
/*     */ import com.dukascopy.dds2.greed.gui.component.moverview.config.TabConfig;
/*     */ import com.dukascopy.dds2.greed.gui.component.ticker.TickerPanel;
/*     */ import com.dukascopy.dds2.greed.gui.l10n.components.JLocalizableFrame;
/*     */ import com.dukascopy.dds2.greed.gui.l10n.components.JLocalizableMenu;
/*     */ import com.dukascopy.dds2.greed.gui.l10n.components.JLocalizableMenuItem;
/*     */ import com.dukascopy.dds2.greed.gui.settings.ClientSettingsStorage;
/*     */ import com.dukascopy.dds2.greed.model.MarketView;
/*     */ import com.dukascopy.transport.common.msg.response.InstrumentStatusUpdateMessage;
/*     */ import java.awt.Component;
/*     */ import java.awt.ComponentOrientation;
/*     */ import java.awt.Container;
/*     */ import java.awt.Dimension;
/*     */ import java.awt.Point;
/*     */ import java.awt.event.ActionEvent;
/*     */ import java.awt.event.ComponentEvent;
/*     */ import java.awt.event.ComponentListener;
/*     */ import java.awt.event.MouseAdapter;
/*     */ import java.awt.event.MouseEvent;
/*     */ import java.awt.event.WindowAdapter;
/*     */ import java.awt.event.WindowEvent;
/*     */ import java.lang.reflect.Method;
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashSet;
/*     */ import java.util.List;
/*     */ import java.util.Set;
/*     */ import javax.swing.AbstractAction;
/*     */ import javax.swing.ActionMap;
/*     */ import javax.swing.BorderFactory;
/*     */ import javax.swing.ImageIcon;
/*     */ import javax.swing.InputMap;
/*     */ import javax.swing.JButton;
/*     */ import javax.swing.JMenuBar;
/*     */ import javax.swing.JMenuItem;
/*     */ import javax.swing.JPopupMenu;
/*     */ import javax.swing.JScrollPane;
/*     */ import javax.swing.JTabbedPane;
/*     */ import javax.swing.KeyStroke;
/*     */ import javax.swing.event.MenuEvent;
/*     */ import javax.swing.event.MenuListener;
/*     */ import javax.swing.text.TextAction;
/*     */ import org.slf4j.Logger;
/*     */ import org.slf4j.LoggerFactory;
/*     */ 
/*     */ public class MarketOverviewFrame extends JLocalizableFrame
/*     */   implements ComponentListener
/*     */ {
/*  46 */   private static final Logger LOGGER = LoggerFactory.getLogger(MarketOverviewFrame.class);
/*     */   private int width;
/*     */   private int height;
/*     */   private int WIDTH;
/*     */   private int HEIGHT;
/*     */   private int miniPanelWidth;
/*     */   private int miniPanelHeight;
/*  54 */   private List<TabContentPanel> tabsContentPanes = new ArrayList();
/*     */   private JTabbedPane tabbedPane;
/*     */   private SwingWorker<?> worker;
/*  58 */   private Set<String> openedInstruments = new HashSet();
/*  59 */   private boolean duplicateAllowed = true;
/*     */   private ClientSettingsStorage settingsSaver;
/*     */   private JMenuItem removeTabItem;
/*     */   private JMenuItem renameTabItem;
/*  66 */   final JLocalizableMenuItem closeTabItem = new JLocalizableMenuItem();
/*     */   private JPopupMenu actionPopupMenu;
/*     */   private MarketOverviewConfig marketOverviewConfig;
/*     */ 
/*     */   public MarketOverviewFrame(int width, int height)
/*     */   {
/*  73 */     String modeKey = GreedContext.CLIENT_MODE;
/*     */ 
/*  75 */     setParams(new String[] { modeKey });
/*  76 */     setTitle("frame.market.overview");
/*     */ 
/*  78 */     this.WIDTH = width; this.HEIGHT = height;
/*  79 */     this.width = width; this.height = height;
/*     */ 
/*  81 */     this.tabbedPane = new JTabbedPane();
/*  82 */     setContentPane(this.tabbedPane);
/*     */ 
/*  84 */     addListenersToTabbedPane();
/*     */ 
/*  86 */     initContextMenu();
/*  87 */     initTabMenu();
/*     */ 
/*  89 */     addWindowListener(new WindowAdapter() {
/*     */       public void windowClosed(WindowEvent e) {
/*  91 */         MarketOverviewFrame.this.saveInstruments();
/*     */       }
/*     */ 
/*     */       public void windowClosing(WindowEvent e)
/*     */       {
/*  96 */         MarketOverviewFrame.this.saveBeforeColsing();
/*     */       }
/*     */     });
/*  99 */     addComponentListener(this);
/* 100 */     addMouseListener(new MouseAdapter()
/*     */     {
/*     */       public void mousePressed(MouseEvent e) {
/* 103 */         MarketOverviewFrame.this.requestFocus();
/*     */       }
/*     */     });
/* 107 */     setDefaultCloseOperation(1);
/*     */   }
/*     */ 
/*     */   private void _init(boolean runOnStart)
/*     */   {
/* 113 */     this.tabsContentPanes.clear();
/* 114 */     this.tabbedPane.removeAll();
/* 115 */     this.marketOverviewConfig = this.settingsSaver.restoreInstrumentTabs();
/* 116 */     createGuiFromConfig(runOnStart);
/*     */   }
/*     */ 
/*     */   public void init(boolean firstRun)
/*     */   {
/* 121 */     this.worker = new SwingWorker(firstRun)
/*     */     {
/*     */       protected Object construct() throws Exception {
/* 124 */         MarketOverviewFrame.this._init(this.val$firstRun);
/* 125 */         return null;
/*     */       }
/*     */ 
/*     */       protected void finished() {
/* 129 */         MarketOverviewFrame.access$102(MarketOverviewFrame.this, null);
/* 130 */         MarketOverviewFrame.this.display();
/* 131 */         MarketOverviewFrame.this.setInitSize();
/*     */       }
/*     */     };
/* 134 */     this.worker.start();
/*     */   }
/*     */ 
/*     */   public void init() {
/* 138 */     init(false);
/*     */   }
/*     */ 
/*     */   private void initContextMenu()
/*     */   {
/* 143 */     JLocalizableMenu instrumentMenu = new JLocalizableMenu();
/* 144 */     instrumentMenu.setText("item.currencies");
/*     */ 
/* 146 */     instrumentMenu.addMenuListener(new MenuListener(instrumentMenu) {
/*     */       public void menuSelected(MenuEvent e) {
/* 148 */         updateInstrumentList();
/*     */       }
/*     */       public void menuDeselected(MenuEvent e) {
/* 151 */         this.val$instrumentMenu.removeAll();
/*     */       }
/*     */       public void menuCanceled(MenuEvent e) {
/* 154 */         this.val$instrumentMenu.removeAll();
/*     */       }
/*     */ 
/*     */       private void updateInstrumentList() {
/* 158 */         ClientSettingsStorage clientSettingsStorage = (ClientSettingsStorage)GreedContext.get("settingsStorage");
/* 159 */         List currentInstruments = clientSettingsStorage.restoreSelectedInstruments();
/*     */ 
/* 162 */         for (String instrument : currentInstruments) {
/* 163 */           JMenuItem item = new JMenuItem(instrument);
/* 164 */           item.setAction(new AbstractAction(instrument, instrument) {
/*     */             public void actionPerformed(ActionEvent e) {
/* 166 */               MarketOverviewFrame.this.addMiniPanelByInstruments(new String[] { this.val$instrument });
/*     */             }
/*     */           });
/* 169 */           this.val$instrumentMenu.add(item);
/*     */         }
/*     */ 
/* 172 */         if ((!GreedContext.isReadOnly()) && (!GreedContext.isStrategyAllowed()))
/*     */         {
/* 174 */           this.val$instrumentMenu.addSeparator();
/*     */ 
/* 176 */           JLocalizableMenuItem addSelectorItem = new JLocalizableMenuItem("item.selector");
/* 177 */           addSelectorItem.setAction(new AbstractAction("item.selector") {
/*     */             public void actionPerformed(ActionEvent e) {
/* 179 */               ClientForm cf = (ClientForm)GreedContext.get("clientGui");
/* 180 */               DealPanel dp = cf.getDealPanel();
/* 181 */               WorkspacePanel ttp = dp.getWorkspacePanel();
/* 182 */               ((TickerPanel)ttp).openInstrumentSelectionDialog((TickerPanel)ttp);
/*     */             }
/*     */           });
/* 186 */           this.val$instrumentMenu.add(addSelectorItem);
/*     */         }
/*     */       }
/*     */     });
/* 193 */     JLocalizableMenu tabMenu = new JLocalizableMenu();
/* 194 */     tabMenu.setText("item.tabs");
/*     */ 
/* 196 */     JLocalizableMenuItem addTabItem = new JLocalizableMenuItem("item.new.tab");
/* 197 */     addTabItem.setAction(new AbstractAction("item.new.tab") {
/*     */       public void actionPerformed(ActionEvent actionEvent) {
/* 199 */         MarketOverviewFrame.this.openNewTab();
/*     */       }
/*     */     });
/* 203 */     this.removeTabItem = new JLocalizableMenuItem("item.close.tab");
/* 204 */     this.removeTabItem.setAction(new AbstractAction("item.close.tab")
/*     */     {
/*     */       public void actionPerformed(ActionEvent actionEvent) {
/* 207 */         MarketOverviewFrame.this.closeTab();
/*     */       }
/*     */     });
/* 212 */     this.renameTabItem = new JLocalizableMenuItem("item.rename.tab");
/* 213 */     this.renameTabItem.setAction(new AbstractAction("item.rename.tab")
/*     */     {
/*     */       public void actionPerformed(ActionEvent actionEvent) {
/* 216 */         TabPanel tp = (TabPanel)MarketOverviewFrame.this.tabbedPane.getTabComponentAt(MarketOverviewFrame.this.tabbedPane.getSelectedIndex());
/* 217 */         tp.startRenaming();
/*     */       }
/*     */     });
/* 222 */     JLocalizableMenuItem closeItem = new JLocalizableMenuItem("item.close");
/* 223 */     closeItem.setAction(new AbstractAction("item.close") {
/*     */       public void actionPerformed(ActionEvent actionEvent) {
/* 225 */         MarketOverviewFrame.this.setVisible(false);
/*     */       }
/*     */     });
/* 229 */     tabMenu.add(addTabItem);
/* 230 */     tabMenu.add(this.removeTabItem);
/* 231 */     tabMenu.add(this.renameTabItem);
/* 232 */     tabMenu.addSeparator();
/* 233 */     tabMenu.add(closeItem);
/*     */ 
/* 235 */     JMenuBar menuBar = new JMenuBar();
/* 236 */     menuBar.add(tabMenu);
/* 237 */     menuBar.add(instrumentMenu);
/*     */ 
/* 239 */     setJMenuBar(menuBar);
/*     */   }
/*     */ 
/*     */   private void initTabMenu()
/*     */   {
/* 245 */     this.actionPopupMenu = new JPopupMenu();
/*     */ 
/* 247 */     JLocalizableMenuItem ranameTabItem = new JLocalizableMenuItem();
/* 248 */     ranameTabItem.setAction(new AbstractAction("item.rename")
/*     */     {
/*     */       public void actionPerformed(ActionEvent arg0) {
/* 251 */         TabPanel selectedTabPanel = (TabPanel)MarketOverviewFrame.this.tabbedPane.getTabComponentAt(MarketOverviewFrame.this.tabbedPane.getSelectedIndex());
/* 252 */         selectedTabPanel.startRenaming();
/*     */       }
/*     */     });
/* 258 */     this.closeTabItem.setAction(new AbstractAction("item.close")
/*     */     {
/*     */       public void actionPerformed(ActionEvent arg0)
/*     */       {
/* 262 */         MarketOverviewFrame.this.closeTab();
/*     */       }
/*     */     });
/* 268 */     this.actionPopupMenu.add(ranameTabItem);
/* 269 */     this.actionPopupMenu.addSeparator();
/* 270 */     this.actionPopupMenu.add(this.closeTabItem);
/*     */   }
/*     */ 
/*     */   private void addListenersToTabbedPane()
/*     */   {
/* 275 */     this.tabbedPane.getInputMap().put(KeyStroke.getKeyStroke("control T"), "openNewTab");
/* 276 */     this.tabbedPane.getInputMap().put(KeyStroke.getKeyStroke("control W"), "closeTab");
/*     */ 
/* 279 */     this.tabbedPane.getActionMap().put("openNewTab", new TextAction("openNewTab")
/*     */     {
/*     */       public void actionPerformed(ActionEvent e) {
/* 282 */         MarketOverviewFrame.this.openNewTab();
/*     */       }
/*     */     });
/* 286 */     this.tabbedPane.getActionMap().put("closeTab", new TextAction("closeTab")
/*     */     {
/*     */       public void actionPerformed(ActionEvent e) {
/* 289 */         if (MarketOverviewFrame.this.tabbedPane.getTabCount() > 1) MarketOverviewFrame.this.closeTab();
/*     */       }
/*     */     });
/* 294 */     this.tabbedPane.addMouseListener(new MouseAdapter() {
/*     */       public void mousePressed(MouseEvent event) {
/* 296 */         process(event);
/* 297 */         MarketOverviewFrame.this.tabbedPane.requestFocus();
/*     */       }
/*     */ 
/*     */       public void mouseReleased(MouseEvent event) {
/* 301 */         process(event);
/*     */       }
/*     */ 
/*     */       private void process(MouseEvent event)
/*     */       {
/* 306 */         if (event.isPopupTrigger())
/*     */         {
/* 308 */           int mouseX = event.getX();
/* 309 */           int mouseY = event.getY();
/*     */ 
/* 311 */           int tabIndex = MarketOverviewFrame.this.tabbedPane.indexAtLocation(mouseX, mouseY);
/*     */ 
/* 313 */           if (tabIndex < 0) {
/* 314 */             return;
/*     */           }
/*     */ 
/* 317 */           TabPanel tabPanel = (TabPanel)MarketOverviewFrame.this.tabbedPane.getTabComponentAt(tabIndex);
/* 318 */           Point mousePosition = tabPanel.getMousePosition();
/*     */ 
/* 320 */           if (mousePosition == null) MarketOverviewFrame.LOGGER.error(" mousePosition == " + mousePosition);
/*     */ 
/* 322 */           if (mousePosition == null) return;
/* 323 */           MarketOverviewFrame.this.actionPopupMenu.show(tabPanel, mousePosition.x, mousePosition.y);
/*     */         }
/*     */       }
/*     */     });
/*     */   }
/*     */ 
/*     */   private int setTabComponentAt(JTabbedPane tabbedPane, int index, Component tabComponent)
/*     */   {
/* 334 */     int rc = 0;
/*     */     try {
/* 336 */       Method method = tabbedPane.getClass().getMethod("setTabComponentAt", new Class[] { Integer.TYPE, Component.class });
/* 337 */       rcObj = method.invoke(tabbedPane, new Object[] { Integer.valueOf(index), tabComponent });
/*     */     }
/*     */     catch (Exception e)
/*     */     {
/*     */       Object rcObj;
/* 339 */       LOGGER.error(e.getMessage(), e);
/*     */     }
/* 341 */     return rc;
/*     */   }
/*     */ 
/*     */   private void clearEverything() {
/* 345 */     this.width = this.WIDTH; this.height = this.HEIGHT;
/* 346 */     for (Component c : getSelectedTabContentPanel().getComponents()) {
/* 347 */       c.setVisible(false);
/*     */     }
/* 349 */     saveInstruments();
/*     */   }
/*     */ 
/*     */   public void display() {
/* 353 */     if (isShowing()) {
/* 354 */       setVisible(true);
/* 355 */       return;
/*     */     }
/*     */     try {
/* 358 */       setIconImage(GuiUtilsAndConstants.PLATFPORM_ICON.getImage());
/*     */     } catch (Exception e) {
/* 360 */       LOGGER.error(e.getMessage(), e);
/*     */     }
/* 362 */     pack();
/* 363 */     setVisible(true);
/* 364 */     setSize(this.width, this.height);
/* 365 */     setLocationRelativeTo(null);
/*     */   }
/*     */ 
/*     */   public void saveBeforeColsing() {
/* 369 */     saveInstruments();
/* 370 */     this.settingsSaver.saveMOsize(getSize());
/* 371 */     this.settingsSaver.saveMOlocation(getLocation());
/*     */   }
/*     */ 
/*     */   public void setInitSize()
/*     */   {
/* 376 */     if (this.settingsSaver.restoreMOlocation() != null)
/* 377 */       setLocation(this.settingsSaver.restoreMOlocation());
/*     */     else {
/* 379 */       setLocationRelativeTo(null);
/*     */     }
/* 381 */     setSize(this.settingsSaver.restoreMOsize());
/*     */   }
/*     */ 
/*     */   private void openNewTab()
/*     */   {
/* 388 */     TabContentPanel cont = new TabContentPanel(this);
/*     */ 
/* 390 */     this.tabsContentPanes.add(cont);
/* 391 */     JScrollPane scrollPane = new JScrollPane(cont, 20, 31);
/* 392 */     scrollPane.setBorder(BorderFactory.createEmptyBorder());
/*     */ 
/* 394 */     String tabName = generateNextTabTitle();
/*     */ 
/* 396 */     this.tabbedPane.addTab(generateNextTabTitle(), scrollPane);
/* 397 */     TabPanel buttonTabPanel = new TabPanel(this);
/* 398 */     setTabComponentAt(this.tabbedPane, this.tabbedPane.getTabCount() - 1, buttonTabPanel);
/*     */ 
/* 400 */     this.tabbedPane.setSelectedIndex(this.tabbedPane.getTabCount() - 1);
/*     */ 
/* 402 */     TabConfig tabConfig = TabConfig.getConfig(tabName, true);
/* 403 */     this.marketOverviewConfig.addTab(tabConfig);
/*     */ 
/* 405 */     enableOrDisableAllCloseButtons();
/*     */   }
/*     */ 
/*     */   private void closeTab() {
/* 409 */     int selectedPane = this.tabbedPane.getSelectedIndex();
/* 410 */     if (selectedPane >= 0)
/*     */     {
/* 413 */       this.marketOverviewConfig.getTabs().remove(selectedPane);
/* 414 */       this.tabbedPane.removeTabAt(selectedPane);
/* 415 */       this.tabsContentPanes.remove(selectedPane);
/* 416 */       saveInstruments();
/*     */     }
/*     */ 
/* 419 */     enableOrDisableAllCloseButtons();
/*     */   }
/*     */ 
/*     */   public void setDuplicateAllowed(boolean duplicateAllowed) {
/* 423 */     this.duplicateAllowed = duplicateAllowed;
/*     */   }
/*     */ 
/*     */   public boolean getDuplicateAllowed() {
/* 427 */     return this.duplicateAllowed;
/*     */   }
/*     */ 
/*     */   private void createNewTab(String title)
/*     */   {
/* 432 */     TabContentPanel content = new TabContentPanel(this);
/* 433 */     this.tabsContentPanes.add(content);
/*     */ 
/* 435 */     content.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
/* 436 */     JScrollPane scrollPane = new JScrollPane(content, 20, 31);
/*     */ 
/* 438 */     this.tabbedPane.addTab(title, scrollPane);
/*     */ 
/* 440 */     TabPanel buttonTabPanel = new TabPanel(this);
/* 441 */     setTabComponentAt(this.tabbedPane, this.tabbedPane.getTabCount() - 1, buttonTabPanel);
/*     */ 
/* 443 */     enableOrDisableAllCloseButtons();
/*     */   }
/*     */ 
/*     */   private void createGuiFromConfig(boolean runFromStart)
/*     */   {
/* 450 */     int selectedIndex = 0;
/* 451 */     boolean panelSizeSaved = false;
/* 452 */     for (TabConfig tabConfig : this.marketOverviewConfig.getTabs())
/*     */     {
/* 454 */       createNewTab(tabConfig.getTabName());
/* 455 */       this.tabbedPane.setSelectedIndex(selectedIndex++);
/*     */ 
/* 457 */       for (MiniPanelConfig mpc : tabConfig.getInstrumentList())
/*     */       {
/* 459 */         if (!InstrumentAvailabilityManager.getInstance().isAllowed(mpc.getInstrument())) return;
/*     */ 
/* 461 */         MiniPanel panel = new MiniPanel(mpc.getInstrument(), false);
/*     */ 
/* 463 */         if (!panelSizeSaved)
/*     */         {
/* 465 */           this.miniPanelHeight = (int)panel.getPreferredSize().getHeight();
/* 466 */           this.miniPanelWidth = (int)panel.getPreferredSize().getWidth();
/* 467 */           panelSizeSaved = true;
/*     */         }
/*     */ 
/* 470 */         setStatusForPanel(panel);
/* 471 */         getSelectedTabContentPanel().addMiniPanel(panel);
/*     */       }
/*     */ 
/* 477 */       if (!tabConfig.isLastActive());
/*     */     }
/*     */ 
/* 483 */     this.tabbedPane.setSelectedIndex(this.marketOverviewConfig.getSelectedTabIndex());
/*     */ 
/* 485 */     enableOrDisableAllCloseButtons();
/*     */ 
/* 487 */     if (runFromStart) {
/*     */       try {
/* 489 */         Thread.sleep(3000L);
/*     */       } catch (InterruptedException e) {
/* 491 */         LOGGER.error(e.getMessage(), e);
/*     */       }
/*     */     }
/*     */ 
/* 495 */     setTradabilityStatus();
/* 496 */     updateSubscription();
/*     */   }
/*     */ 
/*     */   public void addMiniPanelByInstruments(String[] instruments)
/*     */   {
/* 502 */     if (instruments != null) {
/* 503 */       for (String instr : instruments) {
/* 504 */         MiniPanel panel = new MiniPanel(instr);
/* 505 */         if ((this.duplicateAllowed) || (!this.openedInstruments.contains(instr))) {
/* 506 */           this.openedInstruments.add(panel.getInstrument());
/* 507 */           setStatusForPanel(panel);
/* 508 */           getSelectedTabContentPanel().addMiniPanel(panel);
/*     */         }
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 514 */     this.worker = new SwingWorker() {
/*     */       protected Object construct() throws Exception {
/* 516 */         MarketOverviewFrame.this.saveInstruments();
/* 517 */         return null;
/*     */       }
/*     */ 
/*     */       protected void finished() {
/* 521 */         MarketOverviewFrame.access$102(MarketOverviewFrame.this, null);
/* 522 */         MarketOverviewFrame.this.display();
/*     */       }
/*     */     };
/* 526 */     this.worker.start();
/*     */   }
/*     */ 
/*     */   public void removeMiniPanel(MiniPanel panel)
/*     */   {
/* 531 */     this.openedInstruments.remove(panel.getInstrument());
/* 532 */     getSelectedTabContentPanel().removeMiniPanel(panel);
/* 533 */     this.worker = new SwingWorker() {
/*     */       protected Object construct() throws Exception {
/* 535 */         MarketOverviewFrame.this.saveInstruments();
/* 536 */         return null;
/*     */       }
/*     */ 
/*     */       protected void finished() {
/* 540 */         MarketOverviewFrame.access$102(MarketOverviewFrame.this, null);
/*     */       }
/*     */     };
/* 543 */     this.worker.start();
/*     */   }
/*     */ 
/*     */   public TabContentPanel getSelectedTabContentPanel()
/*     */   {
/* 548 */     if (this.tabbedPane.getTabCount() < 1) {
/* 549 */       _init(false);
/*     */     }
/* 551 */     return (TabContentPanel)this.tabsContentPanes.get(this.tabbedPane.getSelectedIndex());
/*     */   }
/*     */ 
/*     */   public void componentResized(ComponentEvent componentEvent)
/*     */   {
/* 557 */     this.width = getContentPane().getWidth(); this.height = getContentPane().getHeight();
/* 558 */     if (this.tabsContentPanes.size() == 0) init();
/*     */ 
/* 560 */     for (TabContentPanel contentPane : this.tabsContentPanes)
/* 561 */       contentPane.resize();
/*     */   }
/*     */ 
/*     */   public void componentMoved(ComponentEvent componentEvent) {
/*     */   }
/*     */ 
/*     */   public void componentShown(ComponentEvent componentEvent) {
/*     */   }
/*     */ 
/*     */   public void componentHidden(ComponentEvent componentEvent) {
/*     */   }
/*     */ 
/*     */   public void saveInstruments() {
/* 574 */     if (this.marketOverviewConfig == null) return;
/*     */ 
/* 576 */     this.marketOverviewConfig.clear();
/*     */ 
/* 578 */     TabConfig tabConf = null;
/* 579 */     MiniPanelConfig miniPanelConfig = null;
/*     */ 
/* 581 */     int i = 0; for (int n = this.tabbedPane.getTabCount(); i < n; i++)
/*     */     {
/* 583 */       tabConf = TabConfig.getConfig(this.tabbedPane.getTitleAt(i), false);
/*     */ 
/* 585 */       int index = 0; for (int count = ((TabContentPanel)this.tabsContentPanes.get(i)).getComponentCount(); index < count; index++)
/*     */       {
/* 587 */         if (!(((TabContentPanel)this.tabsContentPanes.get(i)).getComponent(index) instanceof MiniPanel))
/*     */           continue;
/* 589 */         MiniPanel miniPanel = (MiniPanel)((TabContentPanel)this.tabsContentPanes.get(i)).getComponent(index);
/* 590 */         miniPanelConfig = MiniPanelConfig.getConfig(miniPanel.getInstrument());
/* 591 */         tabConf.addMiniPanelConfig(miniPanelConfig);
/*     */       }
/*     */ 
/* 594 */       this.marketOverviewConfig.addTab(tabConf);
/*     */     }
/*     */ 
/* 599 */     this.settingsSaver.saveInstrumenTabs(this.marketOverviewConfig, this.tabbedPane.getSelectedIndex());
/*     */   }
/*     */ 
/*     */   public void setSettingsSaver(ClientSettingsStorage settingsSaver) {
/* 603 */     this.settingsSaver = settingsSaver;
/*     */   }
/*     */ 
/*     */   private String generateNextTabTitle()
/*     */   {
/* 622 */     int tabsCount = this.marketOverviewConfig.getTabs().size();
/*     */ 
/* 624 */     tabsCount++; String result = "Tab " + tabsCount;
/* 625 */     return result;
/*     */   }
/*     */ 
/*     */   public void updateSubscription()
/*     */   {
/* 631 */     int i = 0; for (int n = this.tabbedPane.getTabCount(); i < n; i++) {
/* 632 */       int index = 0; for (int count = ((TabContentPanel)this.tabsContentPanes.get(i)).getComponentCount(); index < count; index++)
/*     */       {
/* 634 */         MiniPanel miniPanel = (MiniPanel)((TabContentPanel)this.tabsContentPanes.get(i)).getComponent(index);
/* 635 */         String instrument = miniPanel.getInstrument();
/* 636 */         miniPanel.setTradable(isInstrumentSubscribed(instrument));
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   private boolean isInstrumentSubscribed(String instrument)
/*     */   {
/* 644 */     List subscribedList = this.settingsSaver.restoreSelectedInstruments();
/*     */ 
/* 646 */     for (String instr : subscribedList) {
/* 647 */       if (instrument.equals(instr)) return true;
/*     */     }
/*     */ 
/* 650 */     return false;
/*     */   }
/*     */ 
/*     */   public void updateTradability(InstrumentStatusUpdateMessage status) {
/* 654 */     int i = 0; for (int n = this.tabbedPane.getTabCount(); i < n; i++) {
/* 655 */       int index = 0; for (int count = ((TabContentPanel)this.tabsContentPanes.get(i)).getComponentCount(); index < count; index++) {
/* 656 */         MiniPanel miniPanel = (MiniPanel)((TabContentPanel)this.tabsContentPanes.get(i)).getComponent(index);
/*     */ 
/* 658 */         String instrument = status.getInstrument();
/* 659 */         if (instrument.equals(miniPanel.getInstrument()))
/* 660 */           miniPanel.setTradable(status.getTradable() == 0);
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public void setTradabilityStatus()
/*     */   {
/* 668 */     MarketView marketView = (MarketView)GreedContext.get("marketView");
/*     */ 
/* 670 */     int i = 0; for (int n = this.tabbedPane.getTabCount(); i < n; i++)
/*     */     {
/* 672 */       int count = ((TabContentPanel)this.tabsContentPanes.get(i)).getComponentCount();
/*     */ 
/* 674 */       for (int index = 0; index < count; index++)
/*     */       {
/* 676 */         MiniPanel miniPanel = (MiniPanel)((TabContentPanel)this.tabsContentPanes.get(i)).getComponent(index);
/* 677 */         String instrument = miniPanel.getInstrument();
/*     */ 
/* 679 */         InstrumentStatusUpdateMessage lastTradability = marketView.getInstrumentState(instrument);
/*     */ 
/* 681 */         if (lastTradability == null)
/* 682 */           miniPanel.setTradable(false);
/*     */         else
/* 684 */           miniPanel.setTradable(lastTradability.getTradable() == 0);
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public void enableOrDisableAllCloseButtons()
/*     */   {
/* 694 */     if (this.tabbedPane.getTabCount() > 1)
/*     */     {
/* 696 */       for (int i = 0; i < this.tabbedPane.getTabCount(); i++) {
/* 697 */         TabPanel tp = (TabPanel)this.tabbedPane.getTabComponentAt(i);
/* 698 */         tp.getCloseButton().setEnabled(true);
/* 699 */         tp.getCloseButton().repaint();
/*     */       }
/* 701 */       this.removeTabItem.setEnabled(true);
/* 702 */       this.closeTabItem.setEnabled(true);
/* 703 */     } else if (this.tabbedPane.getTabCount() == 1)
/*     */     {
/* 705 */       for (int i = 0; i < this.tabbedPane.getTabCount(); i++) {
/* 706 */         TabPanel tp = (TabPanel)this.tabbedPane.getTabComponentAt(i);
/* 707 */         if (tp != null) {
/* 708 */           tp.getCloseButton().setEnabled(false);
/* 709 */           tp.getCloseButton().repaint();
/*     */         }
/*     */       }
/* 711 */       this.removeTabItem.setEnabled(false);
/* 712 */       this.closeTabItem.setEnabled(false);
/* 713 */     } else if (this.tabbedPane.getTabCount() < 1)
/*     */     {
/* 715 */       for (int i = 0; i < this.tabbedPane.getTabCount(); i++) {
/* 716 */         TabPanel tp = (TabPanel)this.tabbedPane.getTabComponentAt(i);
/* 717 */         tp.getCloseButton().setEnabled(false);
/* 718 */         tp.getCloseButton().repaint();
/*     */       }
/* 720 */       this.removeTabItem.setEnabled(false);
/* 721 */       this.closeTabItem.setEnabled(false);
/*     */     }
/*     */   }
/*     */ 
/*     */   private void setStatusForPanel(MiniPanel panel)
/*     */   {
/* 736 */     MarketView mv = (MarketView)GreedContext.get("marketView");
/*     */ 
/* 738 */     InstrumentStatusUpdateMessage isum = mv.getInstrumentState(panel.getInstrument());
/*     */     try
/*     */     {
/* 741 */       isum.getTradable();
/*     */     }
/*     */     catch (Exception e) {
/* 744 */       panel.setTradable(false);
/* 745 */       return;
/*     */     }
/* 747 */     panel.setTradable(0 == isum.getTradable());
/*     */   }
/*     */ 
/*     */   public void setDefaultSlippage() {
/* 751 */     int i = 0; for (int n = this.tabbedPane.getTabCount(); i < n; i++) {
/* 752 */       int index = 0; for (int count = ((TabContentPanel)this.tabsContentPanes.get(i)).getComponentCount(); index < count; index++)
/* 753 */         ((MiniPanel)((TabContentPanel)this.tabsContentPanes.get(i)).getComponent(index)).setSlippage();
/*     */     }
/*     */   }
/*     */ 
/*     */   public void setDefaultAmount() {
/* 758 */     int i = 0; for (int n = this.tabbedPane.getTabCount(); i < n; i++) {
/* 759 */       int index = 0; for (int count = ((TabContentPanel)this.tabsContentPanes.get(i)).getComponentCount(); index < count; index++)
/* 760 */         ((MiniPanel)((TabContentPanel)this.tabsContentPanes.get(i)).getComponent(index)).setAmount();
/*     */     }
/*     */   }
/*     */ 
/*     */   public List<TabContentPanel> getContentPanes() {
/* 765 */     return this.tabsContentPanes;
/*     */   }
/*     */ 
/*     */   public void setContentPanes(List<TabContentPanel> contentPanes)
/*     */   {
/* 770 */     this.tabsContentPanes = contentPanes;
/*     */   }
/*     */ 
/*     */   public JTabbedPane getTPane()
/*     */   {
/* 775 */     return this.tabbedPane;
/*     */   }
/*     */ 
/*     */   public void setTPane(JTabbedPane pane)
/*     */   {
/* 780 */     this.tabbedPane = pane;
/*     */   }
/*     */ 
/*     */   public MarketOverviewConfig getMarketOverviewConfig() {
/* 784 */     return this.marketOverviewConfig;
/*     */   }
/*     */ 
/*     */   public void setMarketOverviewConfig(MarketOverviewConfig marketOverviewConfig)
/*     */   {
/* 789 */     this.marketOverviewConfig = marketOverviewConfig;
/*     */   }
/*     */ 
/*     */   public int getMiniPanelHeight() {
/* 793 */     return this.miniPanelHeight;
/*     */   }
/*     */ 
/*     */   public int getMiniPanelWidth() {
/* 797 */     return this.miniPanelWidth;
/*     */   }
/*     */ 
/*     */   public void setVisible(boolean visible)
/*     */   {
/* 802 */     super.setVisible(visible);
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.moverview.MarketOverviewFrame
 * JD-Core Version:    0.6.0
 */