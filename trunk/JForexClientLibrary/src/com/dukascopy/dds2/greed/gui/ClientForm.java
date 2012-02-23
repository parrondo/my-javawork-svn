/*     */ package com.dukascopy.dds2.greed.gui;
/*     */ 
/*     */ import com.dukascopy.api.INewsFilter;
/*     */ import com.dukascopy.api.INewsFilter.NewsSource;
/*     */ import com.dukascopy.charts.settings.ChartSettings;
/*     */ import com.dukascopy.charts.settings.ChartSettings.Option;
/*     */ import com.dukascopy.dds2.greed.GreedContext;
/*     */ import com.dukascopy.dds2.greed.actions.ApplicationCloseEvent;
/*     */ import com.dukascopy.dds2.greed.actions.FullDepthInstrumentSubscribeAction;
/*     */ import com.dukascopy.dds2.greed.gui.component.AccountStatementPanel;
/*     */ import com.dukascopy.dds2.greed.gui.component.ExpandableSplitPane;
/*     */ import com.dukascopy.dds2.greed.gui.component.WorkspacePanel;
/*     */ import com.dukascopy.dds2.greed.gui.component.chart.ChartTabsAndFramesPanel;
/*     */ import com.dukascopy.dds2.greed.gui.component.chart.TabsAndFramesTabbedPane;
/*     */ import com.dukascopy.dds2.greed.gui.component.connect.ConnectStatus;
/*     */ import com.dukascopy.dds2.greed.gui.component.detached.OrderEntryDetached;
/*     */ import com.dukascopy.dds2.greed.gui.component.dialog.NewOrderEditDialog;
/*     */ import com.dukascopy.dds2.greed.gui.component.dowjones.calendar.DowJonesCalendarPanel;
/*     */ import com.dukascopy.dds2.greed.gui.component.dowjones.news.DowJonesNewsPanel;
/*     */ import com.dukascopy.dds2.greed.gui.component.exposure.ExposurePanel;
/*     */ import com.dukascopy.dds2.greed.gui.component.exposure.ExposureTable;
/*     */ import com.dukascopy.dds2.greed.gui.component.exposure.ExposureTableModel;
/*     */ import com.dukascopy.dds2.greed.gui.component.menu.MainMenu;
/*     */ import com.dukascopy.dds2.greed.gui.component.message.MessagePanel;
/*     */ import com.dukascopy.dds2.greed.gui.component.orders.OrderCommonTableModel;
/*     */ import com.dukascopy.dds2.greed.gui.component.orders.OrderEntryPanel;
/*     */ import com.dukascopy.dds2.greed.gui.component.orders.OrdersPanel;
/*     */ import com.dukascopy.dds2.greed.gui.component.positions.PositionsPanel;
/*     */ import com.dukascopy.dds2.greed.gui.component.positions.PositionsTable;
/*     */ import com.dukascopy.dds2.greed.gui.component.positions.PositionsTableModel;
/*     */ import com.dukascopy.dds2.greed.gui.component.status.GreedStatusBar;
/*     */ import com.dukascopy.dds2.greed.gui.component.strategy.StrategyTestPanel;
/*     */ import com.dukascopy.dds2.greed.gui.component.ticker.TickerPanel;
/*     */ import com.dukascopy.dds2.greed.gui.l10n.components.JLocalizableFrame;
/*     */ import com.dukascopy.dds2.greed.gui.settings.ClientSettingsStorage;
/*     */ import com.dukascopy.dds2.greed.model.AccountInfoListener;
/*     */ import com.dukascopy.dds2.greed.model.CurrencyMarketWrapper;
/*     */ import com.dukascopy.dds2.greed.model.MarketStateWrapperListener;
/*     */ import com.dukascopy.dds2.greed.model.Notification;
/*     */ import com.dukascopy.dds2.greed.util.BrowserLauncher;
/*     */ import com.dukascopy.dds2.greed.util.PlatformInitUtils;
/*     */ import com.dukascopy.dds2.greed.util.PlatformSpecific;
/*     */ import com.dukascopy.dds2.greed.util.UIContext;
/*     */ import com.dukascopy.dds2.greed.util.UIContext.OsType;
/*     */ import com.dukascopy.transport.common.msg.request.AccountInfoMessage;
/*     */ import java.awt.Color;
/*     */ import java.awt.Container;
/*     */ import java.awt.Dimension;
/*     */ import java.awt.HeadlessException;
/*     */ import java.awt.Toolkit;
/*     */ import java.awt.event.ActionEvent;
/*     */ import java.awt.event.ActionListener;
/*     */ import java.awt.event.ComponentAdapter;
/*     */ import java.awt.event.ComponentEvent;
/*     */ import java.awt.event.MouseAdapter;
/*     */ import java.awt.event.MouseEvent;
/*     */ import java.awt.event.WindowAdapter;
/*     */ import java.awt.event.WindowEvent;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.StringWriter;
/*     */ import java.util.HashSet;
/*     */ import java.util.List;
/*     */ import java.util.Set;
/*     */ import java.util.concurrent.CopyOnWriteArrayList;
/*     */ import javax.swing.Box;
/*     */ import javax.swing.BoxLayout;
/*     */ import javax.swing.ImageIcon;
/*     */ import javax.swing.JCheckBox;
/*     */ import javax.swing.JFrame;
/*     */ import javax.swing.JLabel;
/*     */ import javax.swing.JPanel;
/*     */ import javax.swing.SwingUtilities;
/*     */ import javax.swing.Timer;
/*     */ import org.slf4j.Logger;
/*     */ import org.slf4j.LoggerFactory;
/*     */ 
/*     */ public class ClientForm extends JLocalizableFrame
/*     */   implements AccountInfoListener
/*     */ {
/*  78 */   private static final Logger LOGGER = LoggerFactory.getLogger(ClientForm.class);
/*     */   public static final String ID_JF_CLIENTFORM = "ID_JF_CLIENTFORM";
/*     */   public static final int MIN_WIDTH = 850;
/*     */   public static final int MIN_HEIGH = 743;
/*     */   public static final int MIN_GLOBAL_HEIGH = 780;
/*     */   public static final int SMALL_HEIGHT = 850;
/*     */   public static final int SPLASH_SCREEN_DELAY = 4000;
/*     */   private ClientFormLayoutManager layoutManager;
/*     */   JPanel desktop;
/*     */   DealPanel dealPanel;
/*     */   ExposurePanel exposurePanel;
/*     */   PositionsPanel positionsPanel;
/*     */   MessagePanel messagePanel;
/*     */   OrdersPanel ordersPanel;
/*     */   DowJonesNewsPanel newsPanel;
/*     */   DowJonesCalendarPanel calendarPanel;
/*     */   GreedStatusBar statusBar;
/*     */   JPanel content;
/*     */   MainMenu mainMenu;
/* 107 */   private List<MarketStateWrapperListener> marketWatchers = new CopyOnWriteArrayList();
/*     */   private final ClientSettingsStorage settingsStorage;
/* 111 */   private List<AccountInfoListener> accountWatchers = new CopyOnWriteArrayList();
/*     */ 
/*     */   public ClientFormLayoutManager getLayoutManager()
/*     */   {
/*  92 */     return this.layoutManager;
/*     */   }
/*     */ 
/*     */   public ClientForm(ClientSettingsStorage storage, ClientFormLayoutManager layoutManager)
/*     */   {
/* 114 */     this.settingsStorage = storage;
/* 115 */     this.layoutManager = layoutManager;
/* 116 */     setName("ID_JF_CLIENTFORM");
/*     */   }
/*     */ 
/*     */   public void build()
/*     */     throws HeadlessException
/*     */   {
/* 128 */     setContentPane(this.content);
/* 129 */     setJMenuBar(this.mainMenu);
/*     */ 
/* 131 */     this.desktop.setMinimumSize(new Dimension(850, getMinimalHeight()));
/* 132 */     setMinimumSize(new Dimension(850, getMinimalHeight()));
/*     */ 
/* 134 */     if (!UIContext.getOperatingSystemType().equals(UIContext.OsType.WINDOWS)) {
/* 135 */       Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
/* 136 */       screenSize.setSize(screenSize.getWidth(), screenSize.getHeight() - 20.0D);
/* 137 */       this.desktop.setPreferredSize(screenSize);
/*     */     }
/*     */ 
/* 140 */     addComponentListener(new ComponentAdapter() {
/*     */       public void componentResized(ComponentEvent e) {
/* 142 */         JFrame tmp = (JFrame)e.getSource();
/*     */ 
/* 144 */         if (tmp.getWidth() < 850)
/* 145 */           tmp.setSize(850, tmp.getHeight());
/*     */       }
/*     */     });
/* 150 */     addWindowListener(new WindowAdapter() {
/*     */       public void windowClosing(WindowEvent windowEvent) {
/* 152 */         if (ApplicationCloseEvent.confirmExit())
/* 153 */           GreedContext.publishEvent(new ApplicationCloseEvent());
/*     */       }
/*     */ 
/*     */       public void windowActivated(WindowEvent e)
/*     */       {
/* 158 */         ChartTabsAndFramesPanel chartPane = (ChartTabsAndFramesPanel)((ClientFormLayoutManager)GreedContext.get("layoutManager")).getChartTabsAndFramesPanel();
/*     */ 
/* 160 */         if ((chartPane.getLastSelectedChartPanelId() == chartPane.getLastSelectedPanelId()) && ((GreedContext.get("layoutManager") instanceof JForexClientFormLayoutManager)))
/* 161 */           ((JForexClientFormLayoutManager)GreedContext.get("layoutManager")).getTabbedPane().setLastActiveChartPanelId(chartPane.getLastSelectedChartPanelId());
/*     */       } } );
/*     */   }
/*     */ 
/*     */   private int getMinimalHeight() {
/* 167 */     return GreedContext.isGlobalExtended() ? 780 : 743;
/*     */   }
/*     */ 
/*     */   public void display() {
/* 171 */     if ((!GreedContext.IS_DEVELOPMENT_MODE) && (
/* 172 */       (GreedContext.isLive()) || (GreedContext.isDemo()) || (GreedContext.isPreDemo()))) {
/* 173 */       displaySplashScreen(4000);
/*     */     }
/*     */ 
/* 177 */     setIconImage();
/* 178 */     setDefaultCloseOperation(0);
/*     */ 
/* 180 */     queryRegistryForSkype();
/*     */ 
/* 182 */     this.mainMenu.decorateMenuItems();
/*     */ 
/* 184 */     loadSettings();
/*     */ 
/* 186 */     PlatformInitUtils.initAWTListener();
/*     */ 
/* 188 */     setVisible(true);
/*     */ 
/* 190 */     getDealPanel().revalidate();
/* 191 */     getDealPanel().repaint();
/*     */ 
/* 193 */     SwingUtilities.invokeLater(new Runnable() {
/*     */       public void run() {
/* 195 */         ClientForm.this.layoutManager.resizeSplitters(ClientForm.this.settingsStorage);
/*     */       }
/*     */     });
/*     */   }
/*     */ 
/*     */   private void loadSettings() {
/* 202 */     boolean selected = this.settingsStorage.restoreOneClickState();
/*     */ 
/* 204 */     getStatusBar().getAccountStatement().getOneClickCheckbox().setSelected(selected);
/*     */ 
/* 206 */     this.settingsStorage.restore(this);
/* 207 */     this.settingsStorage.restore(this.dealPanel);
/*     */ 
/* 209 */     if (GreedContext.isStrategyAllowed()) {
/* 210 */       JForexClientFormLayoutManager jForexClientFormLayoutManager = (JForexClientFormLayoutManager)this.layoutManager;
/*     */       try
/*     */       {
/* 214 */         INewsFilter newsFilter = this.settingsStorage.load(INewsFilter.NewsSource.DJ_NEWSWIRES);
/* 215 */         if (newsFilter != null)
/* 216 */           jForexClientFormLayoutManager.getNewsPanel().setFilter(newsFilter);
/*     */       }
/*     */       catch (Exception ex) {
/* 219 */         LOGGER.warn("Unable to restore Dow Jones News panel's state");
/*     */       }
/*     */ 
/*     */       try
/*     */       {
/* 224 */         INewsFilter calendarFilter = this.settingsStorage.load(INewsFilter.NewsSource.DJ_LIVE_CALENDAR);
/* 225 */         if (calendarFilter != null)
/* 226 */           jForexClientFormLayoutManager.getCalendarPanel().setFilter(calendarFilter);
/*     */       }
/*     */       catch (Exception ex) {
/* 229 */         LOGGER.warn("Unable to restore Dow Jones Calendar panel's state");
/*     */       }
/*     */ 
/* 232 */       if (!this.settingsStorage.isBodySplitExpanded()) {
/* 233 */         jForexClientFormLayoutManager.getBodySplitPanel().collapse();
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 238 */     for (ChartSettings.Option option : ChartSettings.Option.values()) {
/* 239 */       String textValue = this.settingsStorage.load(option);
/* 240 */       if (textValue == null) continue;
/*     */       try {
/* 242 */         ChartSettings.set(option, ChartSettings.valueOf(option, textValue));
/*     */       } catch (Exception ex) {
/* 244 */         LOGGER.warn("Unable to restore chart setting [" + option + "] : " + ex.getMessage());
/*     */       }
/*     */     }
/*     */ 
/* 248 */     if (GreedContext.isContest()) {
/* 249 */       ChartSettings.set(ChartSettings.Option.TRADING, Boolean.valueOf(false));
/*     */     }
/*     */ 
/* 253 */     this.settingsStorage.restoreSystemProperties();
/*     */   }
/*     */ 
/*     */   private void queryRegistryForSkype() {
/* 257 */     String REGQUERY_UTIL = "reg query ";
/* 258 */     String DEFAULT_VALUE = " /ve";
/*     */ 
/* 260 */     String COMMAND = "\\shell\\open\\command ";
/* 261 */     String CLASSES_ROOT = "HKCR\\";
/*     */ 
/* 263 */     String service = "skype";
/* 264 */     String request = "reg query " + "HKCR\\" + service + "\\shell\\open\\command " + " /ve";
/*     */ 
/* 270 */     GreedContext.setConfig("skypeCommand", queryRegistry(request));
/*     */   }
/*     */ 
/*     */   private void setIconImage() {
/* 274 */     setIconImage(GuiUtilsAndConstants.PLATFPORM_ICON.getImage());
/*     */   }
/*     */ 
/*     */   private String queryRegistry(String request) {
/* 278 */     if (!PlatformSpecific.WINDOWS) {
/* 279 */       if (PlatformSpecific.MACOSX) {
/* 280 */         return "open %l";
/*     */       }
/* 282 */       return BrowserLauncher.getBrowserForUnix();
/*     */     }
/*     */ 
/* 286 */     String REGSTR_TOKEN = "REG_SZ";
/* 287 */     String response = "";
/*     */     try {
/* 289 */       Process process = Runtime.getRuntime().exec(request);
/* 290 */       StreamReader reader = new StreamReader(process.getInputStream());
/*     */ 
/* 292 */       reader.start();
/* 293 */       process.waitFor();
/* 294 */       reader.join();
/*     */ 
/* 296 */       String result = reader.getResult();
/* 297 */       int p = result.indexOf("REG_SZ");
/*     */ 
/* 299 */       if (p != -1)
/* 300 */         response = result.substring(p + "REG_SZ".length()).trim();
/*     */     }
/*     */     catch (Exception e) {
/* 303 */       LOGGER.error(e.getMessage(), e);
/* 304 */       response = "";
/*     */     }
/* 306 */     return response;
/*     */   }
/*     */ 
/*     */   public void resetlayout()
/*     */   {
/* 336 */     this.layoutManager.resetlayout();
/*     */   }
/*     */ 
/*     */   public DealPanel getDealPanel()
/*     */   {
/* 345 */     return this.dealPanel;
/*     */   }
/*     */ 
/*     */   public MainMenu getMainMenu() {
/* 349 */     return this.mainMenu;
/*     */   }
/*     */ 
/*     */   public PositionsPanel getPositionsPanel()
/*     */   {
/* 358 */     return this.positionsPanel;
/*     */   }
/*     */ 
/*     */   public ExposurePanel getExposurePanel() {
/* 362 */     return this.exposurePanel;
/*     */   }
/*     */ 
/*     */   public MessagePanel getMessagePanel() {
/* 366 */     return this.messagePanel;
/*     */   }
/*     */ 
/*     */   public ExpandableSplitPane getSplitPane() {
/* 370 */     return this.layoutManager.getSplitPane();
/*     */   }
/*     */ 
/*     */   public JPanel getDesktop() {
/* 374 */     return this.desktop;
/*     */   }
/*     */ 
/*     */   public void updateAmountOnMarketWatchers(String amount) {
/* 378 */     for (MarketStateWrapperListener watcher : this.marketWatchers)
/* 379 */       if ((watcher instanceof OrderEntryDetached))
/* 380 */         ((OrderEntryDetached)watcher).setAmount(amount);
/* 381 */       else if ((watcher instanceof NewOrderEditDialog))
/* 382 */         ((NewOrderEditDialog)watcher).setAmount(amount);
/*     */   }
/*     */ 
/*     */   public void updateSlippageOnMarketWatchers(String slippage)
/*     */   {
/* 387 */     for (MarketStateWrapperListener watcher : this.marketWatchers)
/* 388 */       if ((watcher instanceof OrderEntryDetached))
/* 389 */         ((OrderEntryDetached)watcher).setSlippage(slippage);
/* 390 */       else if ((watcher instanceof NewOrderEditDialog))
/* 391 */         ((NewOrderEditDialog)watcher).setSlippage(slippage);
/*     */   }
/*     */ 
/*     */   public Set<String> getFullDepthInstruments()
/*     */   {
/* 397 */     Set instruments = new HashSet();
/* 398 */     instruments.add(this.dealPanel.getSelectedInstrument());
/* 399 */     for (MarketStateWrapperListener watcher : this.marketWatchers) {
/* 400 */       if ((watcher instanceof OrderEntryDetached)) {
/* 401 */         instruments.add(((OrderEntryDetached)watcher).getInstrument());
/*     */       }
/*     */     }
/* 404 */     return instruments;
/*     */   }
/*     */ 
/*     */   public void onMarketState(CurrencyMarketWrapper market)
/*     */   {
/* 414 */     this.dealPanel.onMarketState(market);
/* 415 */     for (MarketStateWrapperListener watcher : this.marketWatchers) {
/* 416 */       watcher.onMarketState(market);
/*     */     }
/* 418 */     this.positionsPanel.onMarketState(market);
/* 419 */     this.exposurePanel.onMarketState(market);
/*     */   }
/*     */ 
/*     */   public void addMarketWatcher(MarketStateWrapperListener watcher)
/*     */   {
/* 428 */     this.marketWatchers.add(watcher);
/*     */ 
/* 430 */     FullDepthInstrumentSubscribeAction action = new FullDepthInstrumentSubscribeAction(this);
/* 431 */     GreedContext.publishEvent(action);
/*     */   }
/*     */ 
/*     */   public void removeMarketWatcher(MarketStateWrapperListener watcher)
/*     */   {
/* 440 */     this.marketWatchers.remove(watcher);
/*     */   }
/*     */ 
/*     */   public void updateAccountState(AccountInfoMessage accountInfo)
/*     */   {
/* 445 */     for (AccountInfoListener watcher : this.accountWatchers)
/* 446 */       watcher.onAccountInfo(accountInfo);
/*     */   }
/*     */ 
/*     */   public void addAccountInfoWatcher(AccountInfoListener watcher)
/*     */   {
/* 451 */     this.accountWatchers.add(watcher);
/*     */   }
/*     */ 
/*     */   public void removeAccountInfoWatcher(AccountInfoListener watcher) {
/* 455 */     this.accountWatchers.remove(watcher);
/*     */   }
/*     */ 
/*     */   public void onAccountInfo(AccountInfoMessage accountInfo)
/*     */   {
/* 460 */     getPositionsPanel().setStopOrdersVisible((!GreedContext.isGlobal()) && (!GreedContext.isContest()) && (!GreedContext.isGlobalExtended()));
/*     */ 
/* 463 */     getDealPanel().getOrderEntryPanel().setStopOrdersVisible((!GreedContext.isGlobal()) && (!GreedContext.isGlobalExtended()));
/*     */   }
/*     */ 
/*     */   public void postMessage(Notification message, boolean isLocal) {
/* 467 */     this.messagePanel.postMessage(message, isLocal);
/* 468 */     this.messagePanel.repaint();
/*     */   }
/*     */ 
/*     */   public void clearMessageLog() {
/* 472 */     this.messagePanel.clearMessageLog();
/*     */   }
/*     */ 
/*     */   public void postTesterMessage(StrategyTestPanel panel, Notification message, boolean isLocal) {
/* 476 */     if (GreedContext.isStrategyAllowed()) {
/* 477 */       panel.postMessage(message, isLocal);
/* 478 */       if (panel.isShowing())
/* 479 */         panel.repaint();
/*     */     }
/*     */   }
/*     */ 
/*     */   public void clearTesterMessages()
/*     */   {
/* 485 */     if (GreedContext.isStrategyAllowed()) {
/* 486 */       JForexClientFormLayoutManager layout = (JForexClientFormLayoutManager)getLayoutManager();
/* 487 */       List panels = layout.getStrategyTestPanels();
/* 488 */       for (StrategyTestPanel strategyTestPanel : panels)
/* 489 */         strategyTestPanel.clearMessageLog();
/*     */     }
/*     */   }
/*     */ 
/*     */   public OrdersPanel getOrdersPanel()
/*     */   {
/* 495 */     GuiUtilsAndConstants.ensureEventDispatchThread();
/* 496 */     return this.ordersPanel;
/*     */   }
/*     */ 
/*     */   public DowJonesNewsPanel getNewsPanel() {
/* 500 */     GuiUtilsAndConstants.ensureEventDispatchThread();
/* 501 */     return this.newsPanel;
/*     */   }
/*     */ 
/*     */   public DowJonesCalendarPanel getCalendarPanel() {
/* 505 */     GuiUtilsAndConstants.ensureEventDispatchThread();
/* 506 */     return this.calendarPanel;
/*     */   }
/*     */ 
/*     */   public void refresh() {
/* 510 */     this.dealPanel.refresh();
/*     */   }
/*     */ 
/*     */   public void clear() {
/* 514 */     clearOrderModels();
/*     */ 
/* 516 */     getDealPanel().getOrderEntryPanel().clearEverything(false);
/* 517 */     WorkspacePanel workspacePanel = getDealPanel().getWorkspacePanel();
/* 518 */     if ((workspacePanel instanceof TickerPanel))
/*     */     {
/* 520 */       ((TickerPanel)workspacePanel).clear(false);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void clearOrderModels() {
/* 525 */     LOGGER.debug("dumping all the orders");
/*     */ 
/* 527 */     OrderCommonTableModel otModel = getOrdersPanel().getModel();
/* 528 */     otModel.clear();
/*     */ 
/* 530 */     PositionsTableModel ptModel = (PositionsTableModel)getPositionsPanel().getTable().getModel();
/* 531 */     ptModel.clear();
/*     */ 
/* 533 */     ExposureTableModel etModel = (ExposureTableModel)getExposurePanel().getTable().getModel();
/* 534 */     etModel.clear();
/*     */   }
/*     */ 
/*     */   private void displaySplashScreen(int timeToDisplay)
/*     */   {
/* 539 */     Color splashColor = Color.white;
/* 540 */     JPanel splash = new JPanel();
/* 541 */     splash.setLayout(new BoxLayout(splash, 0));
/* 542 */     JLabel iconLabel = new JLabel();
/* 543 */     iconLabel.setIcon(GuiUtilsAndConstants.PLATFPORM_SPLASH);
/* 544 */     JPanel panel = new JPanel();
/* 545 */     panel.setLayout(new BoxLayout(panel, 1));
/* 546 */     panel.setBackground(splashColor);
/* 547 */     panel.add(Box.createVerticalGlue());
/* 548 */     panel.add(iconLabel);
/* 549 */     panel.add(Box.createVerticalGlue());
/* 550 */     splash.add(Box.createHorizontalGlue());
/* 551 */     splash.add(panel);
/* 552 */     splash.add(Box.createHorizontalGlue());
/* 553 */     splash.setBackground(splashColor);
/*     */ 
/* 555 */     JFrame splashFrame = new JFrame();
/* 556 */     splashFrame.setBackground(splashColor);
/* 557 */     splashFrame.getContentPane().add(splash);
/* 558 */     splashFrame.setUndecorated(true);
/* 559 */     splashFrame.setAlwaysOnTop(true);
/* 560 */     splashFrame.setDefaultCloseOperation(2);
/* 561 */     splashFrame.setExtendedState(6);
/* 562 */     splashFrame.setVisible(true);
/* 563 */     setLocationRelativeTo(this);
/*     */ 
/* 565 */     splash.addMouseListener(new MouseAdapter(splashFrame)
/*     */     {
/*     */       public void mouseClicked(MouseEvent e) {
/* 568 */         ClientForm.this.closeSplash(this.val$splashFrame);
/*     */       }
/*     */ 
/*     */       public void mousePressed(MouseEvent e) {
/* 572 */         ClientForm.this.closeSplash(this.val$splashFrame);
/*     */       }
/*     */     });
/* 577 */     Timer splashTimer = new Timer(timeToDisplay, new ActionListener(splashFrame)
/*     */     {
/*     */       public void actionPerformed(ActionEvent e) {
/* 580 */         ClientForm.this.closeSplash(this.val$splashFrame);
/*     */       }
/*     */     });
/* 583 */     splashTimer.setRepeats(false);
/* 584 */     splashTimer.start();
/*     */   }
/*     */ 
/*     */   private void closeSplash(JFrame splashFrame) {
/* 588 */     splashFrame.setVisible(false);
/* 589 */     splashFrame.dispose();
/*     */   }
/*     */ 
/*     */   public void setConnectStatus(ConnectStatus status) {
/* 593 */     this.statusBar.setConnectStatus(status);
/*     */   }
/*     */ 
/*     */   public void flashConnectIcon() {
/* 597 */     this.statusBar.flashConnectIcon();
/*     */   }
/*     */ 
/*     */   public GreedStatusBar getStatusBar() {
/* 601 */     return this.statusBar;
/*     */   }
/*     */ 
/*     */   public void dispose()
/*     */   {
/* 609 */     if (this.layoutManager != null) {
/* 610 */       this.layoutManager.dispose();
/*     */     }
/* 612 */     if (getStatusBar() != null) {
/* 613 */       getStatusBar().disose();
/*     */     }
/* 615 */     super.dispose();
/*     */   }
/*     */ 
/*     */   private static class StreamReader extends Thread
/*     */   {
/*     */     private InputStream is;
/*     */     private StringWriter sw;
/*     */ 
/*     */     StreamReader(InputStream is)
/*     */     {
/* 317 */       this.is = is;
/* 318 */       this.sw = new StringWriter();
/*     */     }
/*     */ 
/*     */     public void run()
/*     */     {
/*     */       try
/*     */       {
/*     */         int c;
/* 324 */         while ((c = this.is.read()) != -1)
/* 325 */           this.sw.write(c);
/*     */       } catch (IOException e) {
/*     */       }
/*     */     }
/*     */ 
/*     */     String getResult() {
/* 331 */       return this.sw.toString();
/*     */     }
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.ClientForm
 * JD-Core Version:    0.6.0
 */