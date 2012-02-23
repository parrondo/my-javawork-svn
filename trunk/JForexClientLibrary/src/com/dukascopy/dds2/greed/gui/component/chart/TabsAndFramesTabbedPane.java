/*     */ package com.dukascopy.dds2.greed.gui.component.chart;
/*     */ 
/*     */ import com.dukascopy.api.DataType;
/*     */ import com.dukascopy.api.IChart;
/*     */ import com.dukascopy.api.Instrument;
/*     */ import com.dukascopy.api.Period;
/*     */ import com.dukascopy.api.PriceRange;
/*     */ import com.dukascopy.api.ReversalAmount;
/*     */ import com.dukascopy.api.TickBarSize;
/*     */ import com.dukascopy.api.impl.ServiceWrapper;
/*     */ import com.dukascopy.charts.data.datacache.JForexPeriod;
/*     */ import com.dukascopy.charts.main.interfaces.DDSChartsController;
/*     */ import com.dukascopy.charts.main.interfaces.ProgressListener;
/*     */ import com.dukascopy.charts.persistence.ChartBean;
/*     */ import com.dukascopy.charts.persistence.IdManager;
/*     */ import com.dukascopy.charts.utils.ChartsLocalizator;
/*     */ import com.dukascopy.dds2.greed.GreedContext;
/*     */ import com.dukascopy.dds2.greed.gui.ClientFormLayoutManager;
/*     */ import com.dukascopy.dds2.greed.gui.CommonClientFormLayoutManager;
/*     */ import com.dukascopy.dds2.greed.gui.GuiUtilsAndConstants;
/*     */ import com.dukascopy.dds2.greed.gui.JForexClientFormLayoutManager;
/*     */ import com.dukascopy.dds2.greed.gui.component.chart.listeners.FrameListener;
/*     */ import com.dukascopy.dds2.greed.gui.component.chart.toolbar.DockUndockToolBar;
/*     */ import com.dukascopy.dds2.greed.gui.component.message.AbstractMessagePanel;
/*     */ import com.dukascopy.dds2.greed.gui.component.message.TabComponent;
/*     */ import com.dukascopy.dds2.greed.gui.component.tree.FileChooserDialogHelper;
/*     */ import com.dukascopy.dds2.greed.gui.component.tree.WorkspaceJTree;
/*     */ import com.dukascopy.dds2.greed.gui.component.tree.WorkspaceTreeController;
/*     */ import com.dukascopy.dds2.greed.gui.component.tree.actions.AddChartTemplateTreeAction;
/*     */ import com.dukascopy.dds2.greed.gui.component.tree.nodes.AbstractServiceTreeNode;
/*     */ import com.dukascopy.dds2.greed.gui.component.tree.nodes.WorkspaceRootNode;
/*     */ import com.dukascopy.dds2.greed.gui.component.tree.nodes.WorkspaceTreeNode;
/*     */ import com.dukascopy.dds2.greed.gui.l10n.LocalizationManager;
/*     */ import com.dukascopy.dds2.greed.gui.l10n.components.JLocalizableTabbedPane;
/*     */ import com.dukascopy.dds2.greed.gui.settings.ClientSettingsStorage;
/*     */ import com.dukascopy.dds2.greed.gui.settings.IChartTemplateSettingsStorage;
/*     */ import java.awt.Container;
/*     */ import java.awt.Dimension;
/*     */ import java.awt.Font;
/*     */ import java.awt.Point;
/*     */ import java.awt.event.ActionEvent;
/*     */ import java.awt.event.ActionListener;
/*     */ import java.awt.event.MouseAdapter;
/*     */ import java.awt.event.MouseEvent;
/*     */ import java.io.File;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collection;
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
/*     */ import java.util.prefs.Preferences;
/*     */ import javax.swing.JComponent;
/*     */ import javax.swing.JFrame;
/*     */ import javax.swing.JInternalFrame;
/*     */ import javax.swing.JPopupMenu;
/*     */ import javax.swing.SwingUtilities;
/*     */ import javax.swing.event.ChangeEvent;
/*     */ import javax.swing.event.ChangeListener;
/*     */ import javax.swing.event.EventListenerList;
/*     */ import org.slf4j.Logger;
/*     */ import org.slf4j.LoggerFactory;
/*     */ 
/*     */ public class TabsAndFramesTabbedPane extends JLocalizableTabbedPane
/*     */   implements ActionListener
/*     */ {
/*  63 */   private static final Logger LOGGER = LoggerFactory.getLogger(TabsAndFramesTabbedPane.class);
/*     */ 
/*  65 */   protected final ToolBarAndDesktopPane toolBarAndDesktopPanel = new ToolBarAndDesktopPane();
/*  66 */   private final EventListenerList eventListeners = new EventListenerList();
/*  67 */   private final TabPanelFrameContainer frameContainer = new TabPanelFrameContainer();
/*  68 */   private final FramesDesktopPane desktopPane = new FramesDesktopPane(this, this.frameContainer);
/*     */ 
/*  71 */   private Map<Integer, Instrument> instruments = new HashMap();
/*  72 */   private Map<Integer, Period> periods = new HashMap();
/*  73 */   private Map<Integer, DataType> dataTypes = new HashMap();
/*  74 */   private Map<Integer, PriceRange> priceRanges = new HashMap();
/*  75 */   private Map<Integer, ReversalAmount> reversalAmounts = new HashMap();
/*  76 */   private Map<Integer, TickBarSize> tickBarSizes = new HashMap();
/*  77 */   private Map<Integer, Boolean> historicals = new HashMap();
/*     */   private int lastActiveChartPanelId;
/*     */ 
/*     */   public TabsAndFramesTabbedPane()
/*     */   {
/*  83 */     createListeners();
/*     */   }
/*     */ 
/*     */   public void translate()
/*     */   {
/*  88 */     if (this.frameContainer == null) return;
/*     */ 
/*  90 */     for (DockedUndockedFrame pane : this.frameContainer.getFrames())
/*  91 */       if ((pane.getContent() instanceof ChartPanel))
/*  92 */         updateChartHeader(pane.getPanelId());
/*     */   }
/*     */ 
/*     */   public final void actionPerformed(ActionEvent e)
/*     */   {
/*     */     try
/*     */     {
/*  99 */       TabsOrderingMenuContainer.Action action = TabsOrderingMenuContainer.Action.valueOf(e.getActionCommand());
/* 100 */       int selectedPanelId = getSelectedPanelId();
/* 101 */       executeAction(action, selectedPanelId);
/*     */     }
/*     */     catch (IllegalArgumentException e1)
/*     */     {
/* 106 */       if ("syncTabAndFrameSelection".equalsIgnoreCase(e.getActionCommand())) {
/* 107 */         if (this.desktopPane.isIgnoreActivationEvents()) {
/* 108 */           return;
/*     */         }
/* 110 */         syncTabAndFrameSelection(e.getID());
/* 111 */       } else if ("undockInternalFrame".equalsIgnoreCase(e.getActionCommand())) {
/* 112 */         undockFrame(e.getID());
/* 113 */       } else if ("dockFrame".equalsIgnoreCase(e.getActionCommand())) {
/* 114 */         dockFrame(e.getID());
/* 115 */       } else if ("closeTabAndInternalFrame".equalsIgnoreCase(e.getActionCommand())) {
/* 116 */         closeTabAndInternalFrame(e.getID());
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public void executeAction(TabsOrderingMenuContainer.Action action, int panelId)
/*     */   {
/* 127 */     if (action == TabsOrderingMenuContainer.Action.CLOSE) {
/* 128 */       closeTabAndInternalFrame(panelId);
/*     */     }
/* 130 */     else if (action == TabsOrderingMenuContainer.Action.CLOSE_OTHERS) {
/* 131 */       closeOthersTabAndInternalFrame(panelId);
/*     */     }
/* 133 */     else if (action == TabsOrderingMenuContainer.Action.CLOSE_ALL) {
/* 134 */       closeAllTabAndInternalFrame();
/*     */     }
/* 136 */     else if (action == TabsOrderingMenuContainer.Action.UNDOCK) {
/* 137 */       undockFrame(panelId);
/*     */     }
/* 139 */     else if (action == TabsOrderingMenuContainer.Action.SAVE_TEMPLATE) {
/* 140 */       saveTemplate(panelId);
/*     */     }
/* 142 */     else if (action == TabsOrderingMenuContainer.Action.OPEN_TEMPLATE) {
/* 143 */       openTemplate(panelId, null);
/*     */     }
/* 145 */     else if (action == TabsOrderingMenuContainer.Action.CLONE_CHART)
/* 146 */       cloneChart(panelId);
/*     */   }
/*     */ 
/*     */   public void addFrame(TabsAndFramePanel tabsAndFramePanel, String title, boolean isUndocked, boolean isExpanded)
/*     */   {
/* 151 */     addFrame(tabsAndFramePanel, title, null, isUndocked, isExpanded);
/*     */   }
/*     */ 
/*     */   public void addFrame(TabsAndFramePanel tabsAndFramePanel, String title, String toolTipText, boolean isUndocked, boolean isExpanded) {
/* 155 */     addFrame(tabsAndFramePanel, title, toolTipText, isUndocked, isExpanded, true);
/*     */   }
/*     */ 
/*     */   public void addFrame(TabsAndFramePanel tabsAndFramePanel, String title, boolean isUndocked, boolean isExpanded, boolean select) {
/* 159 */     addFrame(tabsAndFramePanel, title, null, isUndocked, isExpanded, select);
/*     */   }
/*     */ 
/*     */   public void addFrame(TabsAndFramePanel tabsAndFramePanel, String title, String toolTipText, boolean isUndocked, boolean isExpanded, boolean select)
/*     */   {
/* 171 */     int panelId = tabsAndFramePanel.getPanelId();
/* 172 */     if (isUndocked) {
/* 173 */       UndockedJFrame undockedFrame = createUndockedFrame(panelId, title, toolTipText, tabsAndFramePanel);
/* 174 */       closeFrame(panelId);
/* 175 */       this.frameContainer.addFrame(undockedFrame);
/* 176 */       tabsAndFramePanel.putClientProperty("progress", new ProgressListener() {
/*     */         public void setProgress(boolean isProgress, boolean isLoadingOrders) {
/*     */         } } );
/*     */     } else {
/* 180 */       addDockedFrame(panelId, title, toolTipText, tabsAndFramePanel, isExpanded, select);
/* 181 */       configureMessagesPanel(tabsAndFramePanel);
/*     */     }
/* 183 */     fireFrameAdded(isUndocked);
/*     */   }
/*     */ 
/*     */   public void reloadEditorFrame(int panelId, TabsAndFramePanel tabsAndFramePanel) {
/* 187 */     String title = null;
/*     */ 
/* 189 */     if ((tabsAndFramePanel instanceof ServiceSourceEditorPanel)) {
/* 190 */       title = ((ServiceSourceEditorPanel)tabsAndFramePanel).getName();
/*     */     }
/* 192 */     HeadlessJInternalFrame internalFrame = this.desktopPane.createHeadlessFrame(title, tabsAndFramePanel, this, getTabCount());
/* 193 */     internalFrame.setToolTipText(title);
/* 194 */     internalFrame.setVisible(true);
/*     */ 
/* 196 */     tabSelectionChanged(true);
/*     */   }
/*     */ 
/*     */   protected void configureMessagesPanel(TabsAndFramePanel tabsAndFramePanel)
/*     */   {
/* 201 */     int panelId = tabsAndFramePanel.getPanelId();
/*     */ 
/* 203 */     if ((tabsAndFramePanel instanceof BottomPanelForMessages))
/*     */     {
/* 205 */       BottomPanelForMessages pnlMessages = (BottomPanelForMessages)tabsAndFramePanel;
/* 206 */       TabComponent tabComponent = (TabComponent)getButtonTabPanelForId(panelId);
/* 207 */       if (tabComponent != null)
/*     */       {
/* 209 */         tabComponent.setCloseButtonEnabled(pnlMessages.isCloseButtonVisible());
/* 210 */         ((AbstractMessagePanel)pnlMessages.getWrappedPanel()).setTabLabel(tabComponent);
/*     */ 
/* 212 */         addChangeListener(new ChangeListener(panelId, tabComponent) {
/*     */           public void stateChanged(ChangeEvent e) {
/* 214 */             if (TabsAndFramesTabbedPane.this.getSelectedPanelId() == this.val$panelId)
/* 215 */               this.val$tabComponent.tabSelected();
/*     */             else
/* 217 */               this.val$tabComponent.tabDeselected();
/*     */           }
/*     */         });
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public void setTitleForPanelId(int panelId, String name) {
/* 226 */     if (this.frameContainer == null) {
/* 227 */       return;
/*     */     }
/*     */ 
/* 230 */     ButtonTabPanel buttonTabPanel = getButtonTabPanelForId(panelId);
/* 231 */     if (buttonTabPanel != null) {
/* 232 */       buttonTabPanel.setFont(LocalizationManager.getDefaultFont(buttonTabPanel.getFont().getSize()));
/* 233 */       buttonTabPanel.setTitle(name);
/*     */     }
/*     */ 
/* 236 */     DockedUndockedFrame dockedUndockedFrame = this.frameContainer.getFrameByPanelId(panelId);
/* 237 */     if (dockedUndockedFrame != null) {
/* 238 */       dockedUndockedFrame.getContent().setFont(LocalizationManager.getDefaultFont(dockedUndockedFrame.getContent().getFont().getSize()));
/*     */ 
/* 240 */       dockedUndockedFrame.setTitle(name);
/*     */     }
/*     */   }
/*     */ 
/*     */   public int getSelectedPanelId() {
/* 245 */     int selectedIndex = getSelectedIndex();
/* 246 */     if (selectedIndex == -1) {
/* 247 */       return -1;
/*     */     }
/*     */ 
/* 250 */     ButtonTabPanel buttonTabPanel = (ButtonTabPanel)getTabComponentAt(selectedIndex);
/* 251 */     if (buttonTabPanel == null) {
/* 252 */       return -1;
/*     */     }
/*     */ 
/* 255 */     return buttonTabPanel.getPanelId();
/*     */   }
/*     */ 
/*     */   void syncTabAndFrameSelection(int panelIdToBeSelected) {
/* 259 */     int alreadySelected = getSelectedIndex();
/* 260 */     int indexToBeSelected = getIndexByPanelId(panelIdToBeSelected);
/* 261 */     if (alreadySelected != indexToBeSelected) {
/* 262 */       setSelectedIndex(indexToBeSelected);
/*     */     }
/*     */ 
/* 265 */     DockedUndockedFrame selectedFrame = (DockedUndockedFrame)this.desktopPane.getSelectedFrame();
/* 266 */     if (selectedFrame == null) {
/* 267 */       return;
/*     */     }
/*     */ 
/* 270 */     if (panelIdToBeSelected != selectedFrame.getPanelId())
/* 271 */       tabSelectionChanged(true);
/*     */   }
/*     */ 
/*     */   public ButtonTabPanel getButtonTabPanelForId(int panelId)
/*     */   {
/* 276 */     for (int i = 0; i < getTabCount(); i++) {
/* 277 */       ButtonTabPanel curButtonTabPanel = (ButtonTabPanel)getTabComponentAt(i);
/* 278 */       if (curButtonTabPanel.getPanelId() == panelId) {
/* 279 */         return curButtonTabPanel;
/*     */       }
/*     */     }
/* 282 */     return null;
/*     */   }
/*     */ 
/*     */   private int getIndexByPanelId(int panelId) {
/* 286 */     for (int i = 0; i < getTabCount(); i++) {
/* 287 */       ButtonTabPanel buttonTabPanel = (ButtonTabPanel)getTabComponentAt(i);
/* 288 */       if (buttonTabPanel == null) {
/* 289 */         return -1;
/*     */       }
/* 291 */       if (buttonTabPanel.getPanelId() == panelId) {
/* 292 */         return i;
/*     */       }
/*     */     }
/* 295 */     return -1;
/*     */   }
/*     */ 
/*     */   void tabSelectionChanged(boolean onlyCheck) {
/* 299 */     this.desktopPane.setIgnoreActivationEvents(true);
/*     */     try {
/* 301 */       int panelId = getPanelIdFromGui();
/* 302 */       if (panelId < -1)
/*     */         return;
/* 305 */       DockedUndockedFrame frame = this.frameContainer.getFrameByPanelId(panelId);
/* 306 */       if (frame == null)
/*     */         return;
/* 309 */       this.toolBarAndDesktopPanel.removeCurrentToolBar();
/* 310 */       if ((frame instanceof HeadlessJInternalFrame)) {
/* 311 */         HeadlessJInternalFrame internalFrame = this.desktopPane.tabWithHeadlessFrameSelected((HeadlessJInternalFrame)frame);
/* 312 */         if (!internalFrame.isVisible()) {
/* 313 */           internalFrame.setVisible(true);
/*     */         }
/* 315 */         internalFrame.getContent().selected(this.toolBarAndDesktopPanel);
/*     */       }
/* 317 */       if (!onlyCheck)
/* 318 */         fireFrameSelected(panelId);
/*     */     }
/*     */     finally {
/* 321 */       this.desktopPane.setIgnoreActivationEvents(false);
/*     */     }
/*     */   }
/*     */ 
/*     */   private UndockedJFrame createUndockedFrame(int panelId, String title, String toolTipText, TabsAndFramePanel tabsAndFramePanel) {
/* 326 */     UndockedJFrame undockedFrame = new UndockedJFrame(title);
/* 327 */     undockedFrame.setToolTipText(toolTipText);
/* 328 */     undockedFrame.setDefaultCloseOperation(0);
/* 329 */     undockedFrame.addWindowListener(new UndockedJFrameAdapter(undockedFrame, this));
/* 330 */     undockedFrame.setFrameContent(tabsAndFramePanel);
/*     */ 
/* 332 */     if ((undockedFrame.getContent() instanceof TabsAndFramePanelWithToolBar)) {
/* 333 */       ((TabsAndFramePanelWithToolBar)undockedFrame.getContent()).getToolBar().setPinButtonVisible(false);
/*     */     }
/*     */ 
/* 336 */     setupVisualCharacteristics(undockedFrame);
/*     */ 
/* 338 */     undockedFrame.setLocationRelativeTo((JFrame)GreedContext.get("clientGui"));
/* 339 */     undockedFrame.setVisible(true);
/*     */ 
/* 341 */     return undockedFrame;
/*     */   }
/*     */ 
/*     */   protected Dimension getDefaultSize(UndockedJFrame undockedFrame)
/*     */   {
/* 346 */     return null;
/*     */   }
/*     */ 
/*     */   private void setupVisualCharacteristics(UndockedJFrame undockedFrame)
/*     */   {
/* 358 */     Dimension undockedFrameSize = getDefaultSize(undockedFrame);
/* 359 */     if (undockedFrameSize != null) {
/* 360 */       undockedFrame.pack();
/*     */     } else {
/* 362 */       undockedFrameSize = GuiUtilsAndConstants.getOneQuarterOfDisplayDimension();
/* 363 */       undockedFrame.setSize(undockedFrameSize);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void synchTabbedPanesPinBtns() {
/* 368 */     for (DockedUndockedFrame frame : this.frameContainer.getFramesList())
/* 369 */       frame.getContent().refreshPinBtn();
/*     */   }
/*     */ 
/*     */   protected void addDockedFrame(int panelId, String title, String toolTipText, TabsAndFramePanel content, boolean expanded, boolean select)
/*     */   {
/* 374 */     HeadlessJInternalFrame internalFrame = this.desktopPane.createHeadlessFrame(title, content, this, getTabCount());
/* 375 */     internalFrame.setToolTipText(toolTipText);
/* 376 */     if (getTabCount() > 0) {
/* 377 */       addFollowedTabAndFrame(title, toolTipText, panelId, content);
/*     */     } else {
/* 379 */       internalFrame.getContent().selected(this.toolBarAndDesktopPanel);
/* 380 */       addFirstTabAndFrame(title, toolTipText, panelId, content);
/*     */     }
/*     */ 
/* 383 */     SwingUtilities.invokeLater(new Runnable(internalFrame, select, expanded, panelId) {
/*     */       public void run() {
/* 385 */         this.val$internalFrame.setVisible(this.val$select);
/* 386 */         if ((TabsAndFramesTabbedPane.this.desktopPane.isExpanded()) || (this.val$expanded))
/* 387 */           TabsAndFramesTabbedPane.this.desktopPane.maximizeFrame(this.val$panelId);
/*     */         else
/* 389 */           TabsAndFramesTabbedPane.this.desktopPane.reorderFrames();
/*     */       }
/*     */     });
/*     */   }
/*     */ 
/*     */   private int getPanelIdFromGui() {
/* 396 */     int index = getSelectedIndex();
/* 397 */     ButtonTabPanel buttonTabPanel = null;
/*     */     try
/*     */     {
/* 400 */       buttonTabPanel = (ButtonTabPanel)getTabComponentAt(index);
/*     */     } catch (Exception e) {
/* 402 */       return -1;
/*     */     }
/*     */ 
/* 405 */     if (buttonTabPanel == null) {
/* 406 */       return -1;
/*     */     }
/*     */ 
/* 409 */     return buttonTabPanel.getPanelId();
/*     */   }
/*     */ 
/*     */   protected void addFollowedTabAndFrame(String title, String toolTipText, int panelId, JComponent content)
/*     */   {
/* 418 */     addTab(title, null);
/* 419 */     setToolTipTextAt(getTabCount() - 1, toolTipText);
/* 420 */     setTabComponentAt(getTabCount() - 1, ButtonTabPanel.createButtonTabPanel(panelId, title, content, this));
/* 421 */     fireFrameSelected(panelId);
/*     */   }
/*     */ 
/*     */   private void addFirstTabAndFrame(String title, String toolTipText, int panelId, JComponent content) {
/* 425 */     this.toolBarAndDesktopPanel.add(this.desktopPane, "Center");
/* 426 */     add(this.toolBarAndDesktopPanel, 0);
/* 427 */     setTabComponentAt(0, ButtonTabPanel.createButtonTabPanel(panelId, title, content, this));
/* 428 */     setToolTipTextAt(0, toolTipText);
/* 429 */     fireFrameSelected(panelId);
/*     */   }
/*     */ 
/*     */   private void createListeners() {
/* 433 */     addMouseListener(new MouseAdapter()
/*     */     {
/*     */       public void mousePressed(MouseEvent event)
/*     */       {
/* 437 */         process(event);
/*     */       }
/*     */ 
/*     */       public void mouseReleased(MouseEvent event)
/*     */       {
/* 442 */         process(event);
/*     */       }
/*     */ 
/*     */       private void process(MouseEvent event) {
/* 446 */         int mouseX = event.getX();
/* 447 */         int mouseY = event.getY();
/* 448 */         int tabIndex = TabsAndFramesTabbedPane.this.getSelectedIndex();
/* 449 */         if (tabIndex == -1) {
/* 450 */           return;
/*     */         }
/*     */ 
/* 454 */         TabsAndFramesTabbedPane.this.setSelectedIndex(tabIndex);
/*     */ 
/* 456 */         ButtonTabPanel buttonTabPanel = (ButtonTabPanel)TabsAndFramesTabbedPane.this.getTabComponentAt(tabIndex);
/* 457 */         DockedUndockedFrame frame = TabsAndFramesTabbedPane.this.frameContainer.getFrameByPanelId(buttonTabPanel.getPanelId());
/*     */ 
/* 459 */         if (((buttonTabPanel instanceof ButtonTabPanelForButtomCustomPanel)) && (event.isPopupTrigger())) {
/* 460 */           TabsAndFramesTabbedPane.this.showPopupMenu(event, mouseX, mouseY, TabsAndFramesTabbedPane.this.desktopPane.makeMenuListForMainTab(true, true, buttonTabPanel));
/*     */         } else {
/* 462 */           boolean canBeUndocked = frame instanceof HeadlessJInternalFrame;
/* 463 */           if (event.isPopupTrigger()) {
/* 464 */             if ((buttonTabPanel instanceof ButtonTabPanelForBottomPanelWithCloseButton))
/* 465 */               TabsAndFramesTabbedPane.this.openUndockAndClosePopupMenu(event, false);
/* 466 */             else if ((buttonTabPanel instanceof ButtonTabPanelForBottomPanel))
/* 467 */               TabsAndFramesTabbedPane.this.showPopupMenu(event, mouseX, mouseY, TabsAndFramesTabbedPane.this.desktopPane.makeMenuListForBottomTab());
/*     */             else
/* 469 */               TabsAndFramesTabbedPane.this.showPopupMenu(event, mouseX, mouseY, TabsAndFramesTabbedPane.this.desktopPane.makeMenuListForMainTab(canBeUndocked, TabsAndFramesTabbedPane.this.getTabCount() > 1, buttonTabPanel));
/*     */           }
/* 471 */           else if ((event.getID() == 502) && (event.getButton() == 1) && (event.getClickCount() == 2) && (canBeUndocked)) {
/* 472 */             HeadlessJInternalFrame internalFrame = (HeadlessJInternalFrame)frame;
/* 473 */             if (internalFrame.isMaximum()) {
/* 474 */               TabsAndFramesTabbedPane.this.desktopPane.makeUnexpanded();
/* 475 */               TabsAndFramesTabbedPane.this.desktopPane.setFramesState(FramesState.CUSTOM);
/* 476 */               TabsAndFramesTabbedPane.this.syncTabAndFrameSelection(frame.getPanelId());
/*     */             } else {
/* 478 */               TabsAndFramesTabbedPane.this.desktopPane.maximizeFrame(frame.getPanelId());
/*     */             }
/*     */           }
/*     */         }
/*     */       }
/*     */     });
/* 485 */     addChangeListener(new ChangeListener() {
/*     */       public void stateChanged(ChangeEvent e) {
/* 487 */         TabsAndFramesTabbedPane.this.tabSelectionChanged(false);
/* 488 */         int selectedIndex = TabsAndFramesTabbedPane.this.getSelectedIndex();
/* 489 */         int tabCount = TabsAndFramesTabbedPane.this.getTabCount();
/* 490 */         for (int i = 0; i < tabCount; i++) {
/* 491 */           ButtonTabPanel buttonTabPanel = (ButtonTabPanel)TabsAndFramesTabbedPane.this.getTabComponentAt(i);
/* 492 */           if (buttonTabPanel == null) {
/*     */             continue;
/*     */           }
/* 495 */           buttonTabPanel.setIconIsActive(i == selectedIndex);
/*     */         }
/*     */       }
/*     */     });
/*     */   }
/*     */ 
/*     */   protected void openUndockAndClosePopupMenu(MouseEvent event, boolean addCloneTesterMenu) {
/* 503 */     int mouseX = event.getX();
/* 504 */     int mouseY = event.getY();
/* 505 */     showPopupMenu(event, mouseX, mouseY, this.desktopPane.makeMenuListWithUndockAndCloseMenuItems(addCloneTesterMenu));
/*     */   }
/*     */ 
/*     */   private void showPopupMenu(MouseEvent event, int mouseX, int mouseY, JPopupMenu popupMenu) {
/* 509 */     Point popupLocation = getPopupLocation(event);
/* 510 */     if (popupLocation == null) {
/* 511 */       popupLocation = new Point(mouseX, mouseY);
/*     */     }
/*     */ 
/* 514 */     popupMenu.show(event.getComponent(), popupLocation.x, popupLocation.y);
/*     */   }
/*     */ 
/*     */   public void closeFrame(int panelId) {
/* 518 */     DockedUndockedFrame frame = this.frameContainer.getFrameByPanelId(panelId);
/* 519 */     if (frame != null) {
/* 520 */       frame.dispose();
/*     */     }
/*     */ 
/* 523 */     int tabIndex = getIndexByPanelId(panelId);
/* 524 */     this.frameContainer.removeFrameByPanelId(panelId);
/*     */ 
/* 526 */     if (tabIndex >= 0) {
/* 527 */       remove(tabIndex);
/*     */     }
/* 529 */     if ((tabIndex == 0) && (getTabCount() > 0)) {
/* 530 */       setComponentAt(0, this.toolBarAndDesktopPanel);
/* 531 */       ButtonTabPanel buttonTabPanel = (ButtonTabPanel)getTabComponentAt(0);
/* 532 */       syncTabAndFrameSelection(buttonTabPanel.getPanelId());
/*     */     }
/*     */ 
/* 535 */     if (getTabCount() == 0) {
/* 536 */       this.toolBarAndDesktopPanel.remove(this.desktopPane);
/* 537 */       this.toolBarAndDesktopPanel.removeCurrentToolBar();
/* 538 */       if (getParent() != null)
/* 539 */         getParent().repaint();
/*     */     }
/*     */     else {
/* 542 */       tabSelectionChanged(false);
/* 543 */       this.desktopPane.reorderFrames();
/*     */     }
/*     */   }
/*     */ 
/*     */   void closeTabAndInternalFrame(int panelId) {
/* 548 */     DockedUndockedFrame frame = this.frameContainer.getFrameByPanelId(panelId);
/*     */ 
/* 550 */     if ((frame != null) && ((frame.getContent() instanceof ServiceSourceEditorPanel))) {
/* 551 */       ChartTabsAndFramesController controler = (ChartTabsAndFramesController)((ClientFormLayoutManager)GreedContext.get("layoutManager")).getChartTabsController();
/* 552 */       ServiceWrapper serviceWrapper = controler.getWorkspaceController().getServiceWrapperById(panelId);
/* 553 */       if ((serviceWrapper != null) && (!FileChooserDialogHelper.conformFileSaving(serviceWrapper))) {
/* 554 */         return;
/*     */       }
/*     */ 
/* 557 */       if ((serviceWrapper != null) && (!serviceWrapper.isModified())) {
/* 558 */         controler.closeServiceEditor(panelId);
/*     */ 
/* 560 */         if (serviceWrapper.isNewUnsaved()) {
/* 561 */           WorkspaceJTree workspaceTree = ((JForexClientFormLayoutManager)GreedContext.get("layoutManager")).getWorkspaceJTree();
/*     */ 
/* 564 */           WorkspaceTreeNode treeNode = workspaceTree.getWorkspaceRoot().getServiceByPanelId(panelId);
/* 565 */           workspaceTree.removeServiceTreeNode((AbstractServiceTreeNode)treeNode);
/*     */         }
/*     */ 
/* 568 */         return;
/*     */       }
/*     */     }
/*     */ 
/* 572 */     closeFrameImpl(frame);
/*     */   }
/*     */ 
/*     */   private void closeAllTabAndInternalFrame() {
/* 576 */     Collection frames = this.frameContainer.getFramesList();
/* 577 */     closeFrames(frames);
/*     */   }
/*     */ 
/*     */   private void closeOthersTabAndInternalFrame(int panelId) {
/* 581 */     Collection frames = this.frameContainer.getFramesList();
/*     */ 
/* 583 */     if ((frames == null) || (frames.isEmpty())) {
/* 584 */       return;
/*     */     }
/*     */ 
/* 587 */     for (DockedUndockedFrame frame : frames)
/* 588 */       if (panelId != frame.getPanelId())
/* 589 */         closeFrameImpl(frame);
/*     */   }
/*     */ 
/*     */   protected void closeFrameImpl(DockedUndockedFrame frame)
/*     */   {
/* 595 */     if (frame == null) {
/* 596 */       return;
/*     */     }
/* 598 */     if (!isCloseAllowed(frame.getContent())) {
/* 599 */       return;
/*     */     }
/* 601 */     closeFrame(frame.getPanelId());
/* 602 */     fireFrameClosed(frame.getContent());
/*     */   }
/*     */ 
/*     */   private void closeFrames(Collection<DockedUndockedFrame> frames) {
/* 606 */     if ((frames == null) || (frames.isEmpty())) {
/* 607 */       return;
/*     */     }
/* 609 */     for (DockedUndockedFrame frame : frames)
/* 610 */       closeFrameImpl(frame);
/*     */   }
/*     */ 
/*     */   UndockedJFrame undockFrame(int panelId)
/*     */   {
/* 615 */     if ((!GreedContext.isStrategyAllowed()) && (getTabCount() < 2)) return null;
/*     */ 
/* 617 */     DockedUndockedFrame internalFrame = this.frameContainer.getFrameByPanelId(panelId);
/* 618 */     if (!(internalFrame instanceof HeadlessJInternalFrame)) {
/* 619 */       return null;
/*     */     }
/* 621 */     UndockedJFrame undockedJFrame = createUndockedFrame(panelId, internalFrame.getTitle_(), internalFrame.getToolTipText(), internalFrame.getContent());
/* 622 */     closeFrame(panelId);
/* 623 */     this.frameContainer.addFrame(undockedJFrame);
/* 624 */     undockedJFrame.toFront();
/* 625 */     return undockedJFrame;
/*     */   }
/*     */ 
/*     */   private void saveTemplate(int selectedPanelId) {
/* 629 */     IChartTemplateSettingsStorage chartTemplateSettingsStorage = (IChartTemplateSettingsStorage)GreedContext.get("chartTemplateSettingsStorage");
/* 630 */     chartTemplateSettingsStorage.saveChartTemplate(selectedPanelId);
/*     */   }
/*     */ 
/*     */   public void openTemplate(int selectedPanelId, File file)
/*     */   {
/* 637 */     IChartTemplateSettingsStorage chartTemplateSettingsStorage = (IChartTemplateSettingsStorage)GreedContext.get("chartTemplateSettingsStorage");
/* 638 */     DDSChartsController ddsChartsController = (DDSChartsController)GreedContext.get("chartsController");
/*     */ 
/* 640 */     IChart chart = ddsChartsController.getIChartBy(Integer.valueOf(selectedPanelId));
/* 641 */     ChartBean chartBean = null;
/* 642 */     if (file == null)
/* 643 */       chartBean = chartTemplateSettingsStorage.openChartTemplate();
/*     */     else {
/* 645 */       chartBean = chartTemplateSettingsStorage.loadChartTemplate(file);
/*     */     }
/* 647 */     if (chartBean != null) {
/* 648 */       chartBean.setInstrument(chart.getInstrument());
/*     */ 
/* 650 */       openChart(chartBean);
/*     */     }
/*     */   }
/*     */ 
/*     */   private void cloneChart(int selectedPanelId) {
/* 655 */     IChartTemplateSettingsStorage chartTemplateSettingsStorage = (IChartTemplateSettingsStorage)GreedContext.get("chartTemplateSettingsStorage");
/* 656 */     ChartBean chartBean = chartTemplateSettingsStorage.cloneChart(selectedPanelId);
/* 657 */     openChart(chartBean);
/*     */   }
/*     */ 
/*     */   private void openChart(ChartBean chartBean) {
/* 661 */     if (chartBean == null) {
/* 662 */       return;
/*     */     }
/*     */ 
/* 665 */     ClientFormLayoutManager clientFormLayoutManager = (ClientFormLayoutManager)GreedContext.get("layoutManager");
/* 666 */     if ((clientFormLayoutManager instanceof CommonClientFormLayoutManager)) {
/* 667 */       chartBean.setId(IdManager.getInstance().getNextChartId());
/* 668 */       ChartsFrame chartsFrame = ChartsFrame.getInstance();
/* 669 */       chartsFrame.addChart(chartBean);
/*     */     }
/* 671 */     else if ((clientFormLayoutManager instanceof JForexClientFormLayoutManager)) {
/* 672 */       JForexClientFormLayoutManager jforexLayoutManager = (JForexClientFormLayoutManager)clientFormLayoutManager;
/* 673 */       ClientSettingsStorage clientSettingsStorage = (ClientSettingsStorage)GreedContext.get("settingsStorage");
/* 674 */       AddChartTemplateTreeAction treeAction = new AddChartTemplateTreeAction(jforexLayoutManager.getChartTabsController(), jforexLayoutManager.getWorkspaceJTree(), jforexLayoutManager.getWorkspaceNodeFactory(), jforexLayoutManager.getWorkspaceHelper(), clientSettingsStorage);
/*     */ 
/* 680 */       treeAction.execute(new Object[] { null, chartBean.getInstrument(), chartBean });
/*     */     }
/*     */     else {
/* 683 */       throw new IllegalArgumentException(new StringBuilder().append("Unsupported type of ClientFormLayoutManager - ").append(clientFormLayoutManager).toString());
/*     */     }
/*     */   }
/*     */ 
/*     */   protected void dockFrame(int panelId) {
/* 688 */     UndockedJFrame undockedJFrame = this.frameContainer.getUndockedChartPanelById(panelId);
/* 689 */     if (undockedJFrame == null) {
/* 690 */       return;
/*     */     }
/* 692 */     TabsAndFramePanel tabsAndFramePanel = undockedJFrame.getContent();
/* 693 */     String name = undockedJFrame.getTitle_();
/* 694 */     String toolTip = undockedJFrame.getToolTipText();
/* 695 */     undockedJFrame.removeAll();
/* 696 */     undockedJFrame.dispose();
/* 697 */     this.frameContainer.removeFrameByPanelId(panelId);
/*     */ 
/* 699 */     addDockedFrame(tabsAndFramePanel.getPanelId(), name, toolTip, tabsAndFramePanel, false, true);
/* 700 */     configureMessagesPanel(tabsAndFramePanel);
/* 701 */     fireFrameAdded(false);
/*     */   }
/*     */ 
/*     */   public boolean selectPanel(int panelId) {
/* 705 */     Integer tabIndex = Integer.valueOf(getIndexByPanelId(panelId));
/* 706 */     if (tabIndex.intValue() < 0) {
/* 707 */       DockedUndockedFrame frame = this.frameContainer.getFrameByPanelId(panelId);
/* 708 */       if (frame == null) {
/* 709 */         return false;
/*     */       }
/* 711 */       frame.setVisible(true);
/* 712 */       frame.toFront();
/*     */     } else {
/* 714 */       syncTabAndFrameSelection(panelId);
/*     */     }
/*     */ 
/* 717 */     return true;
/*     */   }
/*     */ 
/*     */   public void maximizePanel(int frameId) {
/* 721 */     this.desktopPane.maximizeFrame(frameId);
/*     */   }
/*     */ 
/*     */   TabsAndFramePanel getFrameContent(int panelId) {
/* 725 */     DockedUndockedFrame frame = this.frameContainer.getFrameByPanelId(panelId);
/* 726 */     if (frame != null) {
/* 727 */       return frame.getContent();
/*     */     }
/* 729 */     return null;
/*     */   }
/*     */ 
/*     */   public void addFrameListener(FrameListener listener) {
/* 733 */     this.eventListeners.add(FrameListener.class, listener);
/*     */   }
/*     */ 
/*     */   boolean isCloseAllowed(TabsAndFramePanel tabsAndFramePanel) {
/* 737 */     FrameListener[] listeners = (FrameListener[])this.eventListeners.getListeners(FrameListener.class);
/* 738 */     boolean closeAllowed = true;
/* 739 */     for (FrameListener closeListener : listeners) {
/* 740 */       closeAllowed = (closeAllowed) && (closeListener.isCloseAllowed(tabsAndFramePanel));
/*     */     }
/* 742 */     return closeAllowed;
/*     */   }
/*     */ 
/*     */   void fireFrameClosed(TabsAndFramePanel tabsAndFramePanel) {
/* 746 */     FrameListener[] listeners = (FrameListener[])this.eventListeners.getListeners(FrameListener.class);
/* 747 */     for (FrameListener closeListener : listeners)
/* 748 */       closeListener.frameClosed(tabsAndFramePanel, getTabCount());
/*     */   }
/*     */ 
/*     */   private void fireFrameSelected(int panelId)
/*     */   {
/* 753 */     FrameListener[] listeners = (FrameListener[])this.eventListeners.getListeners(FrameListener.class);
/* 754 */     for (FrameListener frameSelectionListener : listeners)
/* 755 */       frameSelectionListener.frameSelected(panelId);
/*     */   }
/*     */ 
/*     */   private void fireFrameAdded(boolean isUndocked)
/*     */   {
/* 760 */     FrameListener[] listeners = (FrameListener[])this.eventListeners.getListeners(FrameListener.class);
/* 761 */     for (FrameListener frameSelectionListener : listeners)
/* 762 */       frameSelectionListener.frameAdded(isUndocked, getTabCount());
/*     */   }
/*     */ 
/*     */   public ServiceSourceEditorPanel getEditorPanel(ServiceWrapper service)
/*     */   {
/* 767 */     return this.frameContainer.getEditorPanel(service);
/*     */   }
/*     */ 
/*     */   public void reloadServiceSourceEditorsPanel(int panelId)
/*     */   {
/*     */   }
/*     */ 
/*     */   public DockedUndockedFrame getPanelByPanelId(int panelId)
/*     */   {
/* 794 */     return this.frameContainer.getFrameByPanelId(panelId);
/*     */   }
/*     */ 
/*     */   public int getFirstChartPanelIdFor(Instrument instrument) {
/* 798 */     return this.frameContainer.getFirstChartPanelIdFor(instrument);
/*     */   }
/*     */ 
/*     */   public void saveState(ClientSettingsStorage clientSettingsStorage, Preferences framesNode, Preferences framePreferencesNode) {
/* 802 */     clientSettingsStorage.saveFramesState(this.desktopPane.getFramesState(), framesNode);
/* 803 */     clientSettingsStorage.setFramesExpanded(this.desktopPane.isExpanded(), framesNode);
/* 804 */     Collection frames = this.frameContainer.getFrames();
/* 805 */     for (DockedUndockedFrame frame : frames)
/* 806 */       clientSettingsStorage.save(frame, framePreferencesNode);
/*     */   }
/*     */ 
/*     */   public DockedUndockedFrame restoreState(ClientSettingsStorage clientSettingsStorage, FramesState framesState, boolean framesExpanded, Preferences framePreferencesNode)
/*     */   {
/* 811 */     this.desktopPane.setFramesState(framesState);
/*     */ 
/* 813 */     Collection frames = this.frameContainer.getFrames();
/* 814 */     DockedUndockedFrame selectedFrame = null;
/* 815 */     for (DockedUndockedFrame frame : frames) {
/* 816 */       boolean isSelected = clientSettingsStorage.restore(framePreferencesNode, frame, framesExpanded);
/* 817 */       if (isSelected) {
/* 818 */         selectedFrame = frame;
/*     */       }
/*     */     }
/* 821 */     if ((framesExpanded) && (selectedFrame != null)) {
/* 822 */       this.desktopPane.maximizeFrame(selectedFrame.getPanelId());
/*     */     }
/* 824 */     return selectedFrame;
/*     */   }
/*     */ 
/*     */   public void populatePopupMenuWithMenuItems(JPopupMenu popupMenu) {
/* 828 */     this.desktopPane.makeMenuListForWorkspaceTree(popupMenu);
/*     */   }
/*     */ 
/*     */   public void closeAll() {
/*     */     try {
/* 833 */       Collection frames = this.frameContainer.getFrames();
/* 834 */       Collection ids = new ArrayList(frames.size());
/* 835 */       for (DockedUndockedFrame frame : frames) {
/* 836 */         if ((frame instanceof UndockedJFrame)) {
/* 837 */           frame.setVisible(false);
/* 838 */           frame.dispose();
/*     */         }
/* 840 */         ids.add(Integer.valueOf(frame.getPanelId()));
/*     */       }
/*     */ 
/* 843 */       for (Integer id : ids) {
/* 844 */         this.frameContainer.removeFrameByPanelId(id.intValue());
/* 845 */         closeTabAndInternalFrame(id.intValue());
/*     */       }
/*     */     } catch (Exception ex) {
/* 848 */       LOGGER.error(ex.getMessage(), ex);
/*     */     }
/*     */   }
/*     */ 
/*     */   public ToolBarAndDesktopPane getToolBarAndDesktopPane() {
/* 853 */     return this.toolBarAndDesktopPanel;
/*     */   }
/*     */ 
/*     */   public void updateInstrument(int chartPanelId, Instrument instrument) {
/* 857 */     this.instruments.put(Integer.valueOf(chartPanelId), instrument);
/* 858 */     updateChartHeader(chartPanelId);
/*     */   }
/*     */ 
/*     */   public void updateDataType(int chartPanelId, DataType dataType) {
/* 862 */     this.dataTypes.put(Integer.valueOf(chartPanelId), dataType);
/* 863 */     updateChartHeader(chartPanelId);
/*     */   }
/*     */ 
/*     */   public void updatePriceRange(int chartPanelId, PriceRange priceRange) {
/* 867 */     this.priceRanges.put(Integer.valueOf(chartPanelId), priceRange);
/* 868 */     updateChartHeader(chartPanelId);
/*     */   }
/*     */ 
/*     */   public void updatePeriod(int chartPanelId, Period newPeriod) {
/* 872 */     this.periods.put(Integer.valueOf(chartPanelId), newPeriod);
/* 873 */     updateChartHeader(chartPanelId);
/*     */   }
/*     */ 
/*     */   public void updateHistorical(int chartPanelId, boolean historicalTesterChart) {
/* 877 */     this.historicals.put(Integer.valueOf(chartPanelId), Boolean.valueOf(historicalTesterChart));
/* 878 */     updateChartHeader(chartPanelId);
/*     */   }
/*     */ 
/*     */   private void updateChartHeader(int chartPanelId) {
/* 882 */     String title = createTitle(chartPanelId);
/* 883 */     DockedUndockedFrame frame = this.frameContainer.getFrameByPanelId(chartPanelId);
/* 884 */     if ((frame instanceof JInternalFrame)) {
/* 885 */       ButtonTabPanel buttonTabPanel = getButtonTabPanelForId(chartPanelId);
/* 886 */       buttonTabPanel.setFont(LocalizationManager.getDefaultFont(buttonTabPanel.getFont().getSize()));
/* 887 */       buttonTabPanel.setTitle(title);
/*     */     }
/* 889 */     frame.setTitle(title);
/*     */   }
/*     */ 
/*     */   private String createTitle(int chartPanelId) {
/* 893 */     DataType dataType = (DataType)this.dataTypes.get(Integer.valueOf(chartPanelId));
/* 894 */     Instrument instrument = (Instrument)this.instruments.get(Integer.valueOf(chartPanelId));
/* 895 */     Period period = (Period)this.periods.get(Integer.valueOf(chartPanelId));
/* 896 */     PriceRange priceRange = (PriceRange)this.priceRanges.get(Integer.valueOf(chartPanelId));
/* 897 */     ReversalAmount reversalAmount = (ReversalAmount)this.reversalAmounts.get(Integer.valueOf(chartPanelId));
/* 898 */     TickBarSize tickBarSize = (TickBarSize)this.tickBarSizes.get(Integer.valueOf(chartPanelId));
/* 899 */     Boolean historical = (Boolean)this.historicals.get(Integer.valueOf(chartPanelId));
/*     */ 
/* 901 */     if ((dataType == null) || (instrument == null)) {
/* 902 */       return "";
/*     */     }
/*     */ 
/* 905 */     if ((DataType.PRICE_RANGE_AGGREGATION.equals(dataType)) && (priceRange == null)) {
/* 906 */       return "";
/*     */     }
/*     */ 
/* 909 */     if ((DataType.TIME_PERIOD_AGGREGATION.equals(dataType)) && (period == null)) {
/* 910 */       return "";
/*     */     }
/*     */ 
/* 913 */     if ((DataType.POINT_AND_FIGURE.equals(dataType)) && ((period == null) || (priceRange == null) || (reversalAmount == null))) {
/* 914 */       return "";
/*     */     }
/*     */ 
/* 917 */     if ((DataType.TICK_BAR.equals(dataType)) && ((period == null) || (tickBarSize == null))) {
/* 918 */       return "";
/*     */     }
/*     */ 
/* 921 */     JForexPeriod wraper = new JForexPeriod(dataType, period, priceRange, reversalAmount, tickBarSize);
/* 922 */     String title = new StringBuilder().append(Boolean.TRUE.equals(historical) ? "* " : "").append(instrument.toString()).append(", ").append(ChartsLocalizator.localize(wraper)).toString();
/*     */ 
/* 924 */     return title;
/*     */   }
/*     */ 
/*     */   public void updateHeader(int chartPanelId, ChartBean chartBean) {
/* 928 */     this.instruments.put(Integer.valueOf(chartPanelId), chartBean.getInstrument());
/* 929 */     this.dataTypes.put(Integer.valueOf(chartPanelId), chartBean.getDataType());
/* 930 */     this.priceRanges.put(Integer.valueOf(chartPanelId), chartBean.getPriceRange());
/* 931 */     this.periods.put(Integer.valueOf(chartPanelId), chartBean.getPeriod());
/* 932 */     this.reversalAmounts.put(Integer.valueOf(chartPanelId), chartBean.getReversalAmount());
/* 933 */     this.tickBarSizes.put(Integer.valueOf(chartPanelId), chartBean.getTickBarSize());
/* 934 */     this.historicals.put(Integer.valueOf(chartPanelId), Boolean.valueOf(chartBean.isHistoricalTesterChart()));
/* 935 */     updateChartHeader(chartPanelId);
/*     */   }
/*     */ 
/*     */   public void updateJForexPeriod(int chartPanelId, JForexPeriod jForexPeriod) {
/* 939 */     this.reversalAmounts.put(Integer.valueOf(chartPanelId), jForexPeriod.getReversalAmount());
/* 940 */     this.tickBarSizes.put(Integer.valueOf(chartPanelId), jForexPeriod.getTickBarSize());
/* 941 */     this.priceRanges.put(Integer.valueOf(chartPanelId), jForexPeriod.getPriceRange());
/* 942 */     this.dataTypes.put(Integer.valueOf(chartPanelId), jForexPeriod.getDataType());
/* 943 */     this.periods.put(Integer.valueOf(chartPanelId), jForexPeriod.getPeriod());
/* 944 */     updateChartHeader(chartPanelId);
/*     */   }
/*     */ 
/*     */   public void setLastActiveChartPanelId(int panelId) {
/* 948 */     this.lastActiveChartPanelId = panelId;
/*     */   }
/*     */ 
/*     */   public int getLastActiveChartPanelId() {
/* 952 */     return this.lastActiveChartPanelId;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.chart.TabsAndFramesTabbedPane
 * JD-Core Version:    0.6.0
 */