/*      */ package com.dukascopy.dds2.greed.gui.component.menu;
/*      */ 
/*      */ import com.dukascopy.api.Instrument;
/*      */ import com.dukascopy.dds2.greed.GreedContext;
/*      */ import com.dukascopy.dds2.greed.actions.ApplicationCloseEvent;
/*      */ import com.dukascopy.dds2.greed.actions.ReloginAction;
/*      */ import com.dukascopy.dds2.greed.gui.ClientForm;
/*      */ import com.dukascopy.dds2.greed.gui.ClientFormLayoutManager;
/*      */ import com.dukascopy.dds2.greed.gui.DealPanel;
/*      */ import com.dukascopy.dds2.greed.gui.GuiUtilsAndConstants;
/*      */ import com.dukascopy.dds2.greed.gui.JForexClientFormLayoutManager;
/*      */ import com.dukascopy.dds2.greed.gui.component.MouseController;
/*      */ import com.dukascopy.dds2.greed.gui.component.WorkspacePanel;
/*      */ import com.dukascopy.dds2.greed.gui.component.detached.CurrencyExposureFrame;
/*      */ import com.dukascopy.dds2.greed.gui.component.dowjones.calendar.CalendarFrame;
/*      */ import com.dukascopy.dds2.greed.gui.component.dowjones.news.NewsFrame;
/*      */ import com.dukascopy.dds2.greed.gui.component.moverview.MarketOverviewFrame;
/*      */ import com.dukascopy.dds2.greed.gui.component.orders.OrderEntryPanel;
/*      */ import com.dukascopy.dds2.greed.gui.component.settings.SettingsTabbedFrame;
/*      */ import com.dukascopy.dds2.greed.gui.component.ticker.TickerPanel;
/*      */ import com.dukascopy.dds2.greed.gui.component.tree.actions.ITreeAction;
/*      */ import com.dukascopy.dds2.greed.gui.component.tree.actions.TreeActionFactory;
/*      */ import com.dukascopy.dds2.greed.gui.component.tree.actions.TreeActionType;
/*      */ import com.dukascopy.dds2.greed.gui.helpers.IWorkspaceHelper;
/*      */ import com.dukascopy.dds2.greed.gui.l10n.LocalizationManager;
/*      */ import com.dukascopy.dds2.greed.gui.l10n.LocalizationManager.Language;
/*      */ import com.dukascopy.dds2.greed.gui.l10n.components.JLocalizableCheckBoxMenuItem;
/*      */ import com.dukascopy.dds2.greed.gui.l10n.components.JLocalizableMenu;
/*      */ import com.dukascopy.dds2.greed.gui.l10n.components.JLocalizableMenuItem;
/*      */ import com.dukascopy.dds2.greed.gui.l10n.components.JLocalizableRadioButtonMenuItem;
/*      */ import com.dukascopy.dds2.greed.gui.resizing.ResizingManager;
/*      */ import com.dukascopy.dds2.greed.gui.resizing.ResizingManager.SizeMode;
/*      */ import com.dukascopy.dds2.greed.gui.settings.ClientSettingsStorage;
/*      */ import com.dukascopy.dds2.greed.gui.util.VersionChecker;
/*      */ import com.dukascopy.dds2.greed.util.CollectionUtils;
/*      */ import com.dukascopy.dds2.greed.util.PlatformInitUtils;
/*      */ import com.dukascopy.dds2.greed.util.PlatformSpecific;
/*      */ import java.awt.Font;
/*      */ import java.awt.event.ActionEvent;
/*      */ import java.awt.event.ActionListener;
/*      */ import java.awt.event.ItemEvent;
/*      */ import java.awt.event.ItemListener;
/*      */ import java.text.DateFormat;
/*      */ import java.text.SimpleDateFormat;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Arrays;
/*      */ import java.util.LinkedList;
/*      */ import java.util.List;
/*      */ import java.util.Locale;
/*      */ import java.util.Properties;
/*      */ import java.util.TimeZone;
/*      */ import javax.swing.AbstractAction;
/*      */ import javax.swing.Box;
/*      */ import javax.swing.ButtonGroup;
/*      */ import javax.swing.JLabel;
/*      */ import javax.swing.JMenu;
/*      */ import javax.swing.JMenuBar;
/*      */ import javax.swing.JMenuItem;
/*      */ import javax.swing.JOptionPane;
/*      */ import javax.swing.JPopupMenu;
/*      */ import javax.swing.KeyStroke;
/*      */ import javax.swing.Timer;
/*      */ import javax.swing.event.MenuEvent;
/*      */ import javax.swing.event.MenuListener;
/*      */ import org.slf4j.Logger;
/*      */ import org.slf4j.LoggerFactory;
/*      */ 
/*      */ public class MainMenu extends JMenuBar
/*      */ {
/*  136 */   private static final Logger LOGGER = LoggerFactory.getLogger(MainMenu.class);
/*      */   public static final String ID_JM_FILE = "ID_JM_FILE";
/*      */   public static final String ID_JM_MAINMENU = "ID_JM_MAINMENU";
/*      */   public static final String ID_JM_SETTINGS = "ID_JM_SETTINGS";
/*      */   public static final String ID_JMI_CLEAR_MESSAGE = "ID_JMI_CLEAR_MESSAGE";
/*      */   private static final String SMS_NOTIF_KEY = "sms_notificator";
/*      */   private static final String SIGNAL_SERVER_KEY = "signal_server_consumer";
/*      */   private static final String SEPARATOR = "separator";
/*  148 */   private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
/*      */ 
/*  153 */   List<String> reports = new ArrayList();
/*      */   private JMenu fileMenu;
/*      */   private JMenu toolsMenu;
/*      */   private JMenu chartsMenu;
/*      */   private JMenu helpMenu;
/*      */   private JMenu servicesMenu;
/*      */   private JMenu calendarsMenu;
/*  162 */   private JLocalizableMenu tools = new JLocalizableMenu("menu.item.tools");
/*  163 */   private JLocalizableMenuItem settings = new JLocalizableMenuItem("menu.item.preferences");
/*      */   private JLocalizableCheckBoxMenuItem newsMenuItem;
/*      */   private JLocalizableCheckBoxMenuItem calendarMenuItem;
/*      */   private JLocalizableCheckBoxMenuItem alerterMenuItem;
/*      */   private JLocalizableCheckBoxMenuItem strategiesMenuItem;
/*      */   private JLocalizableCheckBoxMenuItem historicalDataManagerMenuItem;
/*      */   private JLocalizableMenu portfolioMenu;
/*      */   private JLocalizableMenuItem skypeUs;
/*      */   private JLocalizableMenuItem reportAnIssue;
/*      */   private JLocalizableMenuItem chatUs;
/*      */   private JLocalizableMenuItem callLevelRequest;
/*  177 */   private JLocalizableMenuItem marketOverview = new JLocalizableMenuItem("menu.item.market.overview");
/*      */ 
/*  179 */   private JLocalizableMenuItem reconnect = new JLocalizableMenuItem("menu.item.reconnect");
/*      */ 
/*  181 */   final JLocalizableMenuItem versionCheckItem = new JLocalizableMenuItem("menu.item.check.for.updates") { } ;
/*      */   private JMenuItem helpAbout;
/*  199 */   private final Font fontPlain = this.tools.getFont();
/*  200 */   private final Font fontBold = new Font(this.tools.getFont().getName(), 1, 12);
/*  201 */   private Properties properties = (Properties)GreedContext.get("properties");
/*      */ 
/*      */   public MainMenu()
/*      */   {
/*  205 */     setName("ID_JM_MAINMENU");
/*  206 */     build();
/*      */   }
/*      */ 
/*      */   private void build() {
/*  210 */     this.fileMenu = buildFileMenu();
/*  211 */     this.portfolioMenu = buildPortfolioMenu();
/*  212 */     this.portfolioMenu.setEnabled(!GreedContext.isHideReports());
/*      */ 
/*  214 */     this.toolsMenu = buildToolsMenu();
/*  215 */     this.chartsMenu = buildChartsMenu();
/*  216 */     this.helpMenu = buildHelpMenu();
/*  217 */     this.servicesMenu = buildServicesMenu();
/*  218 */     this.calendarsMenu = buildNewsCalendarsMenu();
/*      */ 
/*  220 */     add(this.fileMenu);
/*  221 */     add(this.portfolioMenu);
/*  222 */     if (!GreedContext.isStrategyAllowed()) {
/*  223 */       add(this.chartsMenu);
/*      */     }
/*  225 */     add(this.calendarsMenu);
/*      */ 
/*  227 */     if (this.servicesMenu.getPopupMenu().getComponentCount() > 0) {
/*  228 */       add(this.servicesMenu);
/*      */     }
/*  230 */     add(this.toolsMenu);
/*  231 */     add(this.helpMenu);
/*      */ 
/*  233 */     registerForMacOSXEvents();
/*      */ 
/*  235 */     add(Box.createHorizontalGlue());
/*      */ 
/*  237 */     add(initLabelTime());
/*  238 */     add(Box.createHorizontalStrut(5));
/*  239 */     JLabel gmt = new JLabel("GMT");
/*  240 */     gmt.setFont(this.fontBold);
/*  241 */     add(gmt);
/*  242 */     add(Box.createHorizontalStrut(5));
/*      */ 
/*  244 */     addVersionCheckListeners();
/*      */ 
/*  246 */     if (GreedContext.isReadOnly())
/*  247 */       disableMenuForViewOnly();
/*      */   }
/*      */ 
/*      */   private void disableMenuForViewOnly()
/*      */   {
/*  252 */     this.servicesMenu.setEnabled(false);
/*  253 */     this.settings.setEnabled(false);
/*      */ 
/*  255 */     if (GreedContext.isStrategyAllowed()) {
/*  256 */       this.newsMenuItem.setEnabled(false);
/*  257 */       this.calendarMenuItem.setEnabled(false);
/*      */     }
/*      */   }
/*      */ 
/*      */   private JLabel initLabelTime() {
/*  262 */     JLabel time = new JLabel();
/*  263 */     time.setFont(this.fontBold);
/*  264 */     time.setText(DATE_FORMAT.format(Long.valueOf(System.currentTimeMillis())));
/*  265 */     ActionListener taskPerformer = new ActionListener(time) {
/*      */       public void actionPerformed(ActionEvent evt) {
/*  267 */         this.val$time.setText(MainMenu.DATE_FORMAT.format(Long.valueOf(System.currentTimeMillis())));
/*      */       }
/*      */     };
/*  270 */     Timer myTimer = new Timer(1000, taskPerformer);
/*  271 */     myTimer.start();
/*  272 */     return time;
/*      */   }
/*      */ 
/*      */   private JMenu buildChartsMenu() {
/*  276 */     JMenu chartsMenu = new JLocalizableMenu("menu.item.charts");
/*  277 */     chartsMenu.addItemListener(new ItemListener(chartsMenu)
/*      */     {
/*      */       public void itemStateChanged(ItemEvent e) {
/*  280 */         this.val$chartsMenu.removeAll();
/*  281 */         MainMenu.this.updateTickerList(this.val$chartsMenu);
/*      */       }
/*      */     });
/*  285 */     return chartsMenu;
/*      */   }
/*      */ 
/*      */   private JMenu buildHelpMenu() {
/*  289 */     JLocalizableMenuItem onLineHelp = new JLocalizableMenuItem("menu.item.platform.manual")
/*      */     {
/*      */     };
/*  298 */     JLocalizableMenuItem faqHelp = new JLocalizableMenuItem("menu.item.faq")
/*      */     {
/*      */     };
/*  306 */     this.helpAbout = new JLocalizableMenuItem("menu.item.about")
/*      */     {
/*      */     };
/*  312 */     JLocalizableMenuItem getHelp = new JLocalizableMenuItem("menu.item.get.help")
/*      */     {
/*      */     };
/*  320 */     VersionChecker.startPeriodicalCheck();
/*      */ 
/*  322 */     return new JLocalizableMenu("menu.item.help", onLineHelp, faqHelp, getHelp)
/*      */     {
/*      */     };
/*      */   }
/*      */ 
/*      */   private void addVersionCheckListeners()
/*      */   {
/*  337 */     this.helpMenu.addMenuListener(new MenuListener()
/*      */     {
/*      */       public void menuSelected(MenuEvent e) {
/*  340 */         MainMenu.this.helpMenu.setFont(MainMenu.this.fontPlain);
/*  341 */         MainMenu.this.helpMenu.repaint();
/*      */       }
/*      */ 
/*      */       public void menuDeselected(MenuEvent e)
/*      */       {
/*      */       }
/*      */ 
/*      */       public void menuCanceled(MenuEvent e)
/*      */       {
/*      */       }
/*      */     });
/*  347 */     this.versionCheckItem.addActionListener(new ActionListener()
/*      */     {
/*      */       public void actionPerformed(ActionEvent arg0) {
/*  350 */         MainMenu.this.versionCheckItem.setFont(MainMenu.this.fontPlain);
/*  351 */         MainMenu.this.versionCheckItem.repaint();
/*      */       } } );
/*      */   }
/*      */ 
/*      */   private JMenu buildServicesMenu() {
/*  357 */     JLocalizableMenu result = new JLocalizableMenu("menu.item.services");
/*      */ 
/*  359 */     this.skypeUs = new JLocalizableMenuItem("menu.item.skype.broker");
/*  360 */     this.skypeUs.setMnemonic(75);
/*  361 */     this.skypeUs.setAction(new AbstractAction("menu.item.skype.broker") {
/*      */       public void actionPerformed(ActionEvent e) {
/*  363 */         GuiUtilsAndConstants.skypeUs();
/*      */       }
/*      */     });
/*  367 */     this.reportAnIssue = new JLocalizableMenuItem("menu.item.report.issue");
/*  368 */     this.reportAnIssue.setMnemonic(75);
/*  369 */     this.reportAnIssue.setAction(new CalendarMenuAction("menu.item.report.issue"));
/*      */ 
/*  371 */     this.chatUs = new JLocalizableMenuItem("menu.item.chat.with.broker");
/*  372 */     this.chatUs.setAction(new AbstractAction("menu.item.chat.with.broker")
/*      */     {
/*      */       public void actionPerformed(ActionEvent e) {
/*  375 */         String login = (String)GreedContext.getConfig("account_name");
/*  376 */         String authorization = GuiUtilsAndConstants.buildAuthorizationRequest(login);
/*  377 */         if (null == authorization) {
/*  378 */           ClientForm clientForm = (ClientForm)GreedContext.get("clientGui");
/*  379 */           JOptionPane.showMessageDialog(clientForm, LocalizationManager.getText("joption.pane.service.not.available"), GuiUtilsAndConstants.LABEL_SHORT_NAME, 0);
/*      */ 
/*  384 */           return;
/*      */         }
/*      */ 
/*  387 */         String chatUrl = (String)MainMenu.this.properties.get("brokerChat.url");
/*  388 */         String url = chatUrl + "/?" + authorization;
/*  389 */         GuiUtilsAndConstants.openURL(url);
/*      */       }
/*      */     });
/*  393 */     this.callLevelRequest = new JLocalizableMenuItem();
/*  394 */     this.callLevelRequest.setAction(new AbstractAction("menu.item.call.level.request") {
/*      */       public void actionPerformed(ActionEvent e) {
/*  396 */         String login = (String)GreedContext.getConfig("account_name");
/*  397 */         String baseUrl = (String)MainMenu.this.properties.get("services1.url");
/*  398 */         String authorization = GuiUtilsAndConstants.buildAuthorizationRequest(login);
/*  399 */         if (null == authorization) {
/*  400 */           ClientForm clientForm = (ClientForm)GreedContext.get("clientGui");
/*  401 */           JOptionPane.showMessageDialog(clientForm, LocalizationManager.getText("joption.pane.request.not.available"), GuiUtilsAndConstants.LABEL_SHORT_NAME, 0);
/*      */ 
/*  407 */           return;
/*      */         }
/*  409 */         String url = baseUrl + "/aDDS/export/callback.php?" + authorization;
/*      */ 
/*  413 */         GuiUtilsAndConstants.openURL(url);
/*      */       }
/*      */     });
/*  417 */     if (GreedContext.IS_KAKAKU_LABEL) {
/*  418 */       JLocalizableMenuItem kakakuFXMk = new JLocalizableMenuItem("menu.item.kakaku.fx")
/*      */       {
/*      */       };
/*  426 */       result.add(kakakuFXMk);
/*      */     }
/*  428 */     else if (GuiUtilsAndConstants.LABEL_SKYPE_ID != null) {
/*  429 */       result.add(this.skypeUs);
/*      */     }
/*      */ 
/*  433 */     if (GreedContext.isDukascopyPlatform) {
/*  434 */       result.add(this.chatUs);
/*  435 */       result.add(this.callLevelRequest);
/*  436 */       result.add(this.reportAnIssue);
/*      */     }
/*  438 */     else if (GreedContext.IS_FXDD_LABEL)
/*      */     {
/*  440 */       JMenuItem liveChat = new JMenuItem("Live Chat");
/*  441 */       liveChat.setAction(new AbstractAction("Live Chat") {
/*      */         public void actionPerformed(ActionEvent e) {
/*  443 */           GuiUtilsAndConstants.openURL("https://secure.fxdd.com/en/live-talk.html");
/*      */         }
/*      */       });
/*  448 */       JMenuItem callUs = new JMenuItem("Call Us");
/*  449 */       callUs.setAction(new AbstractAction("Call Us") {
/*      */         public void actionPerformed(ActionEvent e) {
/*  451 */           GuiUtilsAndConstants.openURL("http://www.fxdd.com/en/forex-trading/contact-us.html");
/*      */         }
/*      */       });
/*  454 */       result.add(liveChat);
/*  455 */       result.add(callUs);
/*      */     }
/*      */ 
/*  459 */     String list = this.properties.getProperty("reports");
/*  460 */     if ((list != null) && (!list.isEmpty())) {
/*  461 */       this.reports = Arrays.asList(list.split(","));
/*  462 */       if (this.reports.size() > 0) {
/*  463 */         for (String reportKey : this.reports) {
/*  464 */           if (("sms_notificator".equals(reportKey)) || ("signal_server_consumer".equals(reportKey)))
/*      */           {
/*  466 */             JLocalizableMenuItem menuItem = new JLocalizableMenuItem(new ReportMenuAction(reportKey));
/*  467 */             result.add(menuItem);
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */ 
/*  473 */     return result;
/*      */   }
/*      */ 
/*      */   private JMenu buildNewsCalendarsMenu()
/*      */   {
/*  485 */     JLocalizableMenuItem calEconMenu = new JLocalizableMenuItem("menu.item.economic.calendar")
/*      */     {
/*      */     };
/*  488 */     JLocalizableMenuItem calHldsMenu = new JLocalizableMenuItem("menu.item.holidays.calendar")
/*      */     {
/*      */     };
/*  491 */     JLocalizableMenuItem calRatsMenu = new JLocalizableMenuItem("menu.item.interest.rates.calendar")
/*      */     {
/*      */     };
/*  495 */     JLocalizableMenuItem dailyHL = new JLocalizableMenuItem("menu.item.dayly.hl")
/*      */     {
/*      */     };
/*  498 */     JLocalizableMenuItem forexCalc = new JLocalizableMenuItem("menu.item.forex.calculator")
/*      */     {
/*      */     };
/*  502 */     JLocalizableMenuItem marginLevelCalc = new JLocalizableMenuItem("menu.item.margin.level.calculator")
/*      */     {
/*      */     };
/*  506 */     this.newsMenuItem = new JLocalizableCheckBoxMenuItem("tab.dowjones.news")
/*      */     {
/*      */     };
/*  532 */     this.calendarMenuItem = new JLocalizableCheckBoxMenuItem("tab.dowjones.calendar")
/*      */     {
/*      */     };
/*  559 */     if (GreedContext.isStrategyAllowed()) {
/*  560 */       this.newsMenuItem.setSelected(((ClientSettingsStorage)GreedContext.get("settingsStorage")).bottomPanelExists(6));
/*  561 */       this.calendarMenuItem.setSelected(((ClientSettingsStorage)GreedContext.get("settingsStorage")).bottomPanelExists(7));
/*      */     }
/*      */ 
/*  564 */     return new JLocalizableMenu("menu.item.news", calEconMenu, calHldsMenu, calRatsMenu, dailyHL, forexCalc, marginLevelCalc)
/*      */     {
/*      */     };
/*      */   }
/*      */ 
/*      */   private JLocalizableMenu buildToolsMenu()
/*      */   {
/*  607 */     this.marketOverview.setAction(new AbstractAction("menu.item.market.overview") {
/*      */       public void actionPerformed(ActionEvent actionEvent) {
/*  609 */         ((MarketOverviewFrame)GreedContext.get("Dock")).setVisible(true);
/*      */       }
/*      */     });
/*  613 */     if ((GreedContext.isGlobal()) || (GreedContext.isContest()) || (GreedContext.isGlobalExtended()))
/*      */     {
/*  616 */       this.marketOverview.setEnabled(false);
/*      */     }
/*      */ 
/*  619 */     this.tools.setMnemonic(87);
/*  620 */     this.tools.add(buildLaunguagesSubMenu());
/*      */ 
/*  622 */     this.tools.addSeparator();
/*      */ 
/*  624 */     this.tools.add(this.marketOverview);
/*  625 */     this.tools.addSeparator();
/*      */ 
/*  627 */     JLocalizableMenuItem strategyTesterMenuItem = new JLocalizableMenuItem("tab.historical.tester");
/*  628 */     if (!GreedContext.isStrategyAllowed()) strategyTesterMenuItem.setVisible(false);
/*  629 */     strategyTesterMenuItem.addActionListener(new ActionListener() {
/*      */       public void actionPerformed(ActionEvent e) {
/*  631 */         JForexClientFormLayoutManager layoutManager = (JForexClientFormLayoutManager)GreedContext.get("layoutManager");
/*  632 */         layoutManager.addStrategyTesterPanel(-1, false, true);
/*      */       }
/*      */     });
/*  635 */     this.tools.add(strategyTesterMenuItem);
/*      */ 
/*  637 */     this.historicalDataManagerMenuItem = new JLocalizableCheckBoxMenuItem("tab.historical.data.manager")
/*      */     {
/*      */     };
/*  650 */     this.tools.add(this.historicalDataManagerMenuItem);
/*      */ 
/*  652 */     if ((GreedContext.isStrategyAllowed()) && (!GreedContext.isContest())) {
/*  653 */       JLocalizableMenuItem strategyEditor = new JLocalizableMenuItem("menu.item.converters");
/*  654 */       strategyEditor.addActionListener(new ActionListener() {
/*      */         public void actionPerformed(ActionEvent e) {
/*  656 */           ((JForexClientFormLayoutManager)GreedContext.get("layoutManager")).getTreeActionFactory().createAction(TreeActionType.ADD_TASK, ((JForexClientFormLayoutManager)GreedContext.get("layoutManager")).getWorkspaceJTree()).execute(null);
/*      */         }
/*      */       });
/*  661 */       this.tools.add(strategyEditor);
/*      */     }
/*      */ 
/*  664 */     if (GreedContext.isStrategyAllowed())
/*      */     {
/*  678 */       this.tools.addSeparator();
/*      */     }
/*      */ 
/*  681 */     if (!GreedContext.isContest()) {
/*  682 */       this.strategiesMenuItem = new JLocalizableCheckBoxMenuItem("tab.strategies")
/*      */       {
/*      */       };
/*  696 */       this.tools.add(this.strategiesMenuItem);
/*      */     }
/*      */ 
/*  699 */     this.alerterMenuItem = new JLocalizableCheckBoxMenuItem("tab.price.alerter")
/*      */     {
/*      */     };
/*  713 */     this.alerterMenuItem.setSelected(((ClientSettingsStorage)GreedContext.get("settingsStorage")).bottomPanelExists(9));
/*  714 */     this.tools.add(this.alerterMenuItem);
/*  715 */     this.tools.addSeparator();
/*      */ 
/*  717 */     this.settings.addActionListener(new ActionListener() {
/*      */       public void actionPerformed(ActionEvent arg0) {
/*  719 */         SettingsTabbedFrame stf = SettingsTabbedFrame.getInstance();
/*  720 */         stf.setVisible(true);
/*      */       }
/*      */     });
/*  724 */     this.tools.add(this.settings);
/*  725 */     return this.tools;
/*      */   }
/*      */ 
/*      */   private JLocalizableMenu buildLaunguagesSubMenu() {
/*  729 */     JLocalizableMenu languages = new JLocalizableMenu("menu.item.languages");
/*  730 */     ButtonGroup group = new ButtonGroup();
/*      */ 
/*  732 */     for (int i = 0; i < LocalizationManager.Language.values().length; i++) {
/*  733 */       addToMenu(craeteLangItem(LocalizationManager.Language.values()[i]), languages, group);
/*      */     }
/*      */ 
/*  736 */     return languages;
/*      */   }
/*      */ 
/*      */   private JLocalizableMenu buildResizingSubMenu()
/*      */   {
/*  741 */     JLocalizableMenu sizeModes = new JLocalizableMenu("menu.item.resizing.modes");
/*  742 */     ButtonGroup group = new ButtonGroup();
/*      */ 
/*  744 */     for (int i = 0; i < ResizingManager.SizeMode.values().length; i++) {
/*  745 */       addToMenu(createSizeModeItem(ResizingManager.SizeMode.values()[i]), sizeModes, group);
/*      */     }
/*      */ 
/*  748 */     return sizeModes;
/*      */   }
/*      */ 
/*      */   private void addToMenu(JMenuItem item, JMenu menu, ButtonGroup group) {
/*  752 */     menu.add(item);
/*  753 */     group.add(item);
/*      */   }
/*      */ 
/*      */   private JLocalizableRadioButtonMenuItem craeteLangItem(LocalizationManager.Language language) {
/*  757 */     JLocalizableRadioButtonMenuItem menuItem = new JLocalizableRadioButtonMenuItem(language.longKey);
/*  758 */     menuItem.setSelected(language.locale.equals(LocalizationManager.getSelectedLocale()));
/*  759 */     menuItem.setAction(new AbstractAction(language.longKey, language) {
/*      */       public void actionPerformed(ActionEvent actionEvent) {
/*  761 */         LocalizationManager.changeLanguage(this.val$language);
/*  762 */         ClientForm clientForm = (ClientForm)GreedContext.get("clientGui");
/*  763 */         DealPanel dealPanel = clientForm.getDealPanel();
/*  764 */         OrderEntryPanel orderEntryPanel = dealPanel.getOrderEntryPanel();
/*  765 */         orderEntryPanel.clearEverything(false);
/*      */       }
/*      */     });
/*  769 */     menuItem.setIcon(language.getIcon());
/*      */ 
/*  771 */     return menuItem;
/*      */   }
/*      */ 
/*      */   private JLocalizableRadioButtonMenuItem createSizeModeItem(ResizingManager.SizeMode sizeMode) {
/*  775 */     JLocalizableRadioButtonMenuItem menuItem = new JLocalizableRadioButtonMenuItem(sizeMode.key);
/*  776 */     menuItem.setSelected(sizeMode.equals(ResizingManager.SizeMode.SMALL));
/*  777 */     menuItem.setAction(new AbstractAction(sizeMode.key, sizeMode) {
/*      */       public void actionPerformed(ActionEvent actionEvent) {
/*  779 */         ResizingManager.changeSize(this.val$sizeMode);
/*      */       }
/*      */     });
/*  783 */     return menuItem;
/*      */   }
/*      */ 
/*      */   private JLocalizableMenu buildPortfolioMenu() {
/*  787 */     this.portfolioMenu = new JLocalizableMenu("menu.item.portfolio");
/*  788 */     addPortfolioMenuItems();
/*  789 */     return this.portfolioMenu;
/*      */   }
/*      */ 
/*      */   private JMenu buildFileMenu() {
/*  793 */     this.reconnect.setName("menu.item.reconnect");
/*  794 */     this.reconnect.setAction(new AbstractAction("menu.item.reconnect") {
/*      */       public void actionPerformed(ActionEvent e) {
/*  796 */         ReloginAction reloginAction = new ReloginAction(this);
/*  797 */         GreedContext.publishEvent(reloginAction);
/*      */       }
/*      */     });
/*  801 */     String switchMode = GreedContext.isStrategyAllowed() ? "menu.item.standard.mode" : "menu.item.jforex.mode";
/*  802 */     JLocalizableMenuItem switchItem = new JLocalizableMenuItem(switchMode, switchMode)
/*      */     {
/*      */     };
/*  810 */     JLocalizableMenuItem openWorkspaceItem = new JLocalizableMenuItem("menu.item.open.workspace")
/*      */     {
/*      */     };
/*  818 */     JLocalizableMenuItem saveWorkspaceItem = new JLocalizableMenuItem("menu.item.save.workspace")
/*      */     {
/*      */     };
/*  826 */     JLocalizableMenuItem saveAsWorkspaceItem = new JLocalizableMenuItem("menu.item.save.as.workspace")
/*      */     {
/*      */     };
/*  834 */     JLocalizableMenuItem deleteSavedSettingsItem = new JLocalizableMenuItem("label.delete.settings")
/*      */     {
/*      */     };
/*  842 */     JLocalizableMenuItem logout = new JLocalizableMenuItem("menu.item.logout")
/*      */     {
/*      */     };
/*  850 */     JLocalizableMenuItem itemExit = new JLocalizableMenuItem("menu.item.exit")
/*      */     {
/*      */     };
/*  861 */     return new JLocalizableMenu("menu.item.file", switchItem, openWorkspaceItem, saveWorkspaceItem, saveAsWorkspaceItem, deleteSavedSettingsItem, logout, itemExit)
/*      */     {
/*      */     };
/*      */   }
/*      */ 
/*      */   private void addPortfolioMenuItems()
/*      */   {
/*  884 */     if ((GreedContext.isLive()) && (GreedContext.IS_FXDD_LABEL)) {
/*  885 */       String fxddUrl = "https://fxlive.fxdd.com/c/reportviewer/login/startReportViewer";
/*  886 */       JLocalizableMenuItem menuItem = new JLocalizableMenuItem("menu.item.fxdd.report.viewer")
/*      */       {
/*      */       };
/*  894 */       this.portfolioMenu.add(menuItem);
/*      */     }
/*      */ 
/*  897 */     LinkedList patern = new LinkedList();
/*      */ 
/*  899 */     patern.add("portfolio_client");
/*  900 */     patern.add("portfolio_manager");
/*  901 */     patern.add("portfolio_self_kakaku");
/*  902 */     patern.add("trader_sll");
/*  903 */     patern.add("separator");
/*  904 */     patern.add("intraday_manager");
/*  905 */     patern.add("intraday_client");
/*  906 */     patern.add("position_manager");
/*  907 */     patern.add("position_client");
/*  908 */     patern.add("annual_report");
/*  909 */     patern.add("self_cust_traded_volume");
/*  910 */     patern.add("self_traded_volume");
/*  911 */     patern.add("spread_commission");
/*  912 */     patern.add("deposit_receipt");
/*  913 */     patern.add("separator");
/*  914 */     patern.add("activity_log");
/*  915 */     patern.add("merge_log");
/*  916 */     patern.add("action_log_manager");
/*  917 */     patern.add("trade_log");
/*  918 */     patern.add("trader_sll_action_log");
/*  919 */     patern.add("menu.item.view.remote.strategy.log");
/*      */ 
/*  921 */     String list = this.properties.getProperty("reports");
/*  922 */     List reports = Arrays.asList(list.split(","));
/*  923 */     LinkedList buildedList = new LinkedList();
/*  924 */     boolean isPrewElemSep = true;
/*      */ 
/*  926 */     if (reports.size() > 0) {
/*  927 */       for (String repName : patern) {
/*  928 */         if ((reports.contains(repName)) || ("menu.item.view.remote.strategy.log".equals(repName)))
/*      */         {
/*  930 */           buildedList.add(repName);
/*  931 */           isPrewElemSep = false;
/*  932 */         } else if (("separator".equals(repName)) && (!isPrewElemSep)) {
/*  933 */           buildedList.add("separator");
/*  934 */           isPrewElemSep = true;
/*      */         }
/*      */       }
/*      */ 
/*  938 */       if (((String)buildedList.get(buildedList.size() - 1)).equals("separator")) {
/*  939 */         buildedList.remove(buildedList.size() - 1);
/*      */       }
/*      */ 
/*  942 */       List remainElems = CollectionUtils.except(reports, buildedList);
/*  943 */       if (!remainElems.isEmpty()) {
/*  944 */         buildedList.addAll(remainElems);
/*      */       }
/*      */     }
/*      */ 
/*  948 */     for (String reportElem : buildedList) {
/*  949 */       if ("separator".equals(reportElem)) {
/*  950 */         this.portfolioMenu.addSeparator();
/*      */       }
/*  952 */       else if ((!"sms_notificator".equals(reportElem)) && (!"signal_server_consumer".equals(reportElem)) && (!"menu.item.view.remote.strategy.log".equals(reportElem)))
/*      */       {
/*  956 */         JLocalizableMenuItem menuItem = new JLocalizableMenuItem(new ReportMenuAction(reportElem));
/*  957 */         this.portfolioMenu.add(menuItem);
/*  958 */       } else if (("menu.item.view.remote.strategy.log".equals(reportElem)) && (GreedContext.getStringProperty("jss.logserver.url") != null) && (!GreedContext.IS_KAKAKU_LABEL))
/*      */       {
/*  961 */         this.portfolioMenu.add(new JLocalizableMenuItem(new ViewRemoteStrategyLogAction()));
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  966 */     addCurrencyExposureItem();
/*      */ 
/*  968 */     if (GreedContext.IS_KAKAKU_LABEL) {
/*  969 */       JLocalizableMenuItem pwdChangeItem = new JLocalizableMenuItem()
/*      */       {
/*      */       };
/*  972 */       this.portfolioMenu.addSeparator();
/*  973 */       this.portfolioMenu.add(pwdChangeItem);
/*      */     }
/*      */ 
/*  976 */     validate();
/*      */   }
/*      */ 
/*      */   private void addCurrencyExposureItem() {
/*  980 */     JMenuItem item = new JLocalizableMenuItem();
/*  981 */     item.setAction(new AbstractAction("menu.item.currency.exposure") {
/*      */       public void actionPerformed(ActionEvent e) {
/*  983 */         CurrencyExposureFrame exposure = new CurrencyExposureFrame();
/*  984 */         exposure.display();
/*      */       }
/*      */     });
/*  987 */     this.portfolioMenu.addSeparator();
/*  988 */     this.portfolioMenu.add(item);
/*      */   }
/*      */ 
/*      */   private void updateTickerList(JMenu chartsMenu) {
/*  992 */     WorkspacePanel workspacePanel = ((ClientForm)GreedContext.get("clientGui")).getDealPanel().getWorkspacePanel();
/*  993 */     if ((workspacePanel instanceof TickerPanel)) {
/*  994 */       chartsMenu.removeAll();
/*      */ 
/*  996 */       ClientSettingsStorage clientSettingsStorage = (ClientSettingsStorage)GreedContext.get("settingsStorage");
/*  997 */       List currentInstruments = clientSettingsStorage.restoreSelectedInstruments();
/*      */ 
/*  999 */       for (String instrument : currentInstruments) {
/* 1000 */         JMenuItem item = new JMenuItem(instrument);
/* 1001 */         item.setAction(new AbstractAction(instrument) {
/*      */           public void actionPerformed(ActionEvent e) {
/* 1003 */             ((ClientFormLayoutManager)GreedContext.get("layoutManager")).getWorkspaceHelper().showChart(Instrument.fromString(e.getActionCommand()));
/*      */           }
/*      */         });
/* 1007 */         chartsMenu.add(item);
/*      */       }
/*      */ 
/* 1010 */       if ((!GreedContext.isStrategyAllowed()) && (!GreedContext.isReadOnly())) {
/* 1011 */         chartsMenu.addSeparator();
/*      */ 
/* 1013 */         JMenuItem addSelectorItem = new JMenuItem();
/* 1014 */         addSelectorItem.setAction(new AbstractAction("Selector") {
/*      */           public void actionPerformed(ActionEvent e) {
/* 1016 */             ClientForm clientForm = (ClientForm)GreedContext.get("clientGui");
/* 1017 */             DealPanel dealPanel = clientForm.getDealPanel();
/* 1018 */             WorkspacePanel workspacePanel = dealPanel.getWorkspacePanel();
/* 1019 */             ((TickerPanel)workspacePanel).openInstrumentSelectionDialog((TickerPanel)workspacePanel);
/*      */           }
/*      */         });
/* 1024 */         chartsMenu.add(addSelectorItem);
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   public void registerForMacOSXEvents()
/*      */   {
/* 1033 */     if (PlatformSpecific.MACOSX)
/*      */     {
/*      */       try
/*      */       {
/* 1037 */         OSXAdapter.setQuitHandler(this, getClass().getDeclaredMethod("quit", (Class[])null));
/* 1038 */         OSXAdapter.setAboutHandler(this, getClass().getDeclaredMethod("about", (Class[])null));
/*      */       }
/*      */       catch (Exception e)
/*      */       {
/* 1043 */         LOGGER.error("Error while loading the OSXAdapter:", e);
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   public void about()
/*      */   {
/* 1050 */     this.helpAbout.doClick();
/*      */   }
/*      */ 
/*      */   public boolean quit()
/*      */   {
/* 1056 */     return ApplicationCloseEvent.confirmExit();
/*      */   }
/*      */ 
/*      */   public void decorateMenuItems() {
/* 1060 */     this.skypeUs.setIcon(GuiUtilsAndConstants.SKYPE_ICON);
/* 1061 */     String skypeCommand = (String)GreedContext.getConfig("skypeCommand");
/* 1062 */     String skypeURL = GreedContext.getStringProperty("skype");
/* 1063 */     this.skypeUs.setText(null == skypeURL ? "menu.item.skype.call" : "menu.item.skype.broker");
/* 1064 */     this.skypeUs.setEnabled((null != skypeCommand) && (!skypeCommand.isEmpty()));
/* 1065 */     this.chatUs.setEnabled(null != skypeURL);
/* 1066 */     this.callLevelRequest.setEnabled(null != skypeURL);
/*      */   }
/*      */ 
/*      */   private void doSwitch() {
/* 1070 */     PlatformInitUtils.switchPlatform();
/*      */   }
/*      */ 
/*      */   public JMenu getHelpMenu() {
/* 1074 */     return this.helpMenu;
/*      */   }
/*      */ 
/*      */   public JLocalizableMenuItem getVersionCheckItem() {
/* 1078 */     return this.versionCheckItem;
/*      */   }
/*      */ 
/*      */   public JMenuItem getReconnect() {
/* 1082 */     return this.reconnect;
/*      */   }
/*      */ 
/*      */   public JLocalizableCheckBoxMenuItem getCalendarMenuItem() {
/* 1086 */     return this.calendarMenuItem;
/*      */   }
/*      */   public JLocalizableCheckBoxMenuItem getNewsMenuItem() {
/* 1089 */     return this.newsMenuItem;
/*      */   }
/*      */   public JLocalizableCheckBoxMenuItem getAlerterMenuItem() {
/* 1092 */     return this.alerterMenuItem;
/*      */   }
/*      */   public JLocalizableCheckBoxMenuItem getStrategiesMenuItem() {
/* 1095 */     return this.strategiesMenuItem;
/*      */   }
/*      */ 
/*      */   public JLocalizableCheckBoxMenuItem getHistoricalDataManagerMenuItem() {
/* 1099 */     return this.historicalDataManagerMenuItem;
/*      */   }
/*      */ 
/*      */   public void setReconnect(JLocalizableMenuItem reconnect) {
/* 1103 */     this.reconnect = reconnect;
/*      */   }
/*      */ 
/*      */   static
/*      */   {
/*  150 */     DATE_FORMAT.setTimeZone(TimeZone.getTimeZone("GMT"));
/*      */   }
/*      */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.menu.MainMenu
 * JD-Core Version:    0.6.0
 */