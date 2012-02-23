/*     */ package com.dukascopy.dds2.greed.gui.component.chart;
/*     */ 
/*     */ import com.dukascopy.api.DataType;
/*     */ import com.dukascopy.api.Instrument;
/*     */ import com.dukascopy.api.OfferSide;
/*     */ import com.dukascopy.api.Period;
/*     */ import com.dukascopy.charts.data.datacache.FeedDataProvider;
/*     */ import com.dukascopy.charts.data.datacache.JForexPeriod;
/*     */ import com.dukascopy.charts.persistence.ChartBean;
/*     */ import com.dukascopy.charts.persistence.IdManager;
/*     */ import com.dukascopy.dds2.greed.GreedContext;
/*     */ import com.dukascopy.dds2.greed.gui.ClientForm;
/*     */ import com.dukascopy.dds2.greed.gui.ClientFormLayoutManager;
/*     */ import com.dukascopy.dds2.greed.gui.GuiUtilsAndConstants;
/*     */ import com.dukascopy.dds2.greed.gui.InstrumentAvailabilityManager;
/*     */ import com.dukascopy.dds2.greed.gui.component.chart.holders.IChartTabsAndFramesController;
/*     */ import com.dukascopy.dds2.greed.gui.component.chart.listeners.FrameListenerAdapter;
/*     */ import com.dukascopy.dds2.greed.gui.component.chart.toolbar.ChartToolBar;
/*     */ import com.dukascopy.dds2.greed.gui.component.chart.toolbar.DockUndockToolBar;
/*     */ import com.dukascopy.dds2.greed.gui.l10n.LocalizationManager;
/*     */ import com.dukascopy.dds2.greed.gui.resizing.components.JResizableButton;
/*     */ import com.dukascopy.dds2.greed.gui.settings.ClientSettingsStorage;
/*     */ import com.dukascopy.dds2.greed.util.PlatformInitUtils;
/*     */ import java.awt.Dimension;
/*     */ import java.awt.event.WindowAdapter;
/*     */ import java.awt.event.WindowEvent;
/*     */ import java.util.List;
/*     */ import javax.swing.BoxLayout;
/*     */ import javax.swing.ImageIcon;
/*     */ import javax.swing.JFrame;
/*     */ import javax.swing.JPanel;
/*     */ import org.slf4j.Logger;
/*     */ import org.slf4j.LoggerFactory;
/*     */ 
/*     */ public class ChartsFrame extends JFrame
/*     */ {
/*  43 */   private static final Logger LOGGER = LoggerFactory.getLogger(ChartsFrame.class);
/*     */ 
/*  45 */   private static final Dimension DEFAULT_SIZE = new Dimension(800, 700);
/*  46 */   private static final String TITLE = LocalizationManager.getText("frame.charts");
/*     */ 
/*  48 */   private static ChartsFrame instance = null;
/*     */ 
/*  50 */   private ClientSettingsStorage storage = (ClientSettingsStorage)GreedContext.get("settingsStorage");
/*  51 */   private IChartTabsAndFramesController chartTabsAndFramesController = ((ClientFormLayoutManager)GreedContext.get("layoutManager")).getChartTabsController();
/*     */ 
/*     */   public static ChartsFrame getInstance()
/*     */   {
/*  55 */     instance = (ChartsFrame)GreedContext.get("charts.frame");
/*  56 */     if (instance == null) {
/*  57 */       instance = new ChartsFrame();
/*  58 */       GreedContext.putInSingleton("charts.frame", instance);
/*     */     }
/*     */ 
/*  61 */     instance.setLocationRelativeTo((ClientForm)GreedContext.get("clientGui"));
/*  62 */     return instance;
/*     */   }
/*     */ 
/*     */   private ChartsFrame() {
/*  66 */     super(TITLE);
/*     */ 
/*  68 */     PlatformInitUtils.initChartsInfrastructure();
/*  69 */     doConnectHistoryServer();
/*     */ 
/*  71 */     JPanel innerPanel = new JPanel();
/*  72 */     innerPanel.setLayout(new BoxLayout(innerPanel, 1));
/*     */ 
/*  74 */     innerPanel.add(((ClientFormLayoutManager)GreedContext.get("layoutManager")).getChartTabsAndFramesPanel());
/*  75 */     add(innerPanel);
/*     */ 
/*  77 */     setAlwaysOnTop(this.storage.restoreChartsAlwaysOnTop());
/*     */ 
/*  79 */     setSize(DEFAULT_SIZE);
/*  80 */     setDefaultCloseOperation(1);
/*     */     try
/*     */     {
/*  83 */       setIconImage(GuiUtilsAndConstants.PLATFPORM_ICON.getImage());
/*     */     } catch (Exception e) {
/*  85 */       LOGGER.error(e.getMessage(), e);
/*     */     }
/*     */ 
/*  88 */     addWindowListener(new WindowAdapter()
/*     */     {
/*     */       public void windowClosing(WindowEvent e) {
/*  91 */         ChartsFrame.getInstance().setVisible(false);
/*     */       }
/*     */     });
/*  96 */     this.chartTabsAndFramesController.addFrameListener(new FrameListenerAdapter()
/*     */     {
/*     */       public boolean isCloseAllowed(TabsAndFramePanel component) {
/*  99 */         return true;
/*     */       }
/*     */ 
/*     */       public void frameClosed(TabsAndFramePanel tabsAndFramePanel, int tabCount) {
/* 103 */         if (tabCount <= 0)
/* 104 */           ChartsFrame.instance.setVisible(false);
/*     */       }
/*     */ 
/*     */       public void frameAdded(boolean isUndocked, int tabCount)
/*     */       {
/* 109 */         if ((!isUndocked) && (tabCount == 1) && (!ChartsFrame.instance.isVisible()))
/* 110 */           ChartsFrame.instance.setVisible(true);
/*     */       }
/*     */     });
/*     */   }
/*     */ 
/*     */   private void doConnectHistoryServer() {
/* 117 */     if (!GreedContext.isStrategyAllowed())
/*     */     {
/* 119 */       Runnable connectToHistory = new Runnable()
/*     */       {
/*     */         public void run() {
/* 122 */           List urls = GreedContext.LOGIN_URLS;
/*     */ 
/* 124 */           String login = (String)GreedContext.getConfig("account_name");
/* 125 */           String historyServerUrl = (String)GreedContext.getProperty("history.server.url");
/* 126 */           String encryptionKey = (String)GreedContext.getProperty("encryptionKey");
/* 127 */           String version = GreedContext.CLIENT_VERSION;
/*     */ 
/* 129 */           FeedDataProvider feedDataProvider = (FeedDataProvider)GreedContext.get("feedDataProvider");
/* 130 */           String sessionId = (String)GreedContext.getConfig("SESSION_ID");
/* 131 */           feedDataProvider.connectToHistoryServer(urls, login, sessionId, historyServerUrl, encryptionKey, version);
/*     */ 
/* 133 */           feedDataProvider.setInstrumentNamesSubscribed(ChartsFrame.this.storage.restoreSelectedInstruments());
/*     */         }
/*     */       };
/* 136 */       connectToHistory.run();
/*     */     }
/*     */   }
/*     */ 
/*     */   public void addChart(Instrument instrument) {
/* 141 */     addChart(IdManager.getInstance().getNextChartId(), instrument, new JForexPeriod(DataType.TICKS, Period.TICK), OfferSide.BID);
/*     */   }
/*     */ 
/*     */   public void addChart(ChartBean chartBean, boolean isUndocked, boolean isExpanded)
/*     */   {
/* 146 */     if (!InstrumentAvailabilityManager.getInstance().isAllowed(chartBean.getInstrument())) return;
/*     */ 
/* 148 */     setVisible(true);
/* 149 */     IdManager.getInstance().reserveChartId(chartBean.getId());
/*     */ 
/* 151 */     this.chartTabsAndFramesController.addChart(chartBean, isUndocked, isExpanded);
/*     */ 
/* 153 */     finishChartAddding(chartBean.getId());
/*     */   }
/*     */ 
/*     */   public void addChart(ChartBean chartBean) {
/* 157 */     addChart(chartBean, false, true);
/*     */   }
/*     */ 
/*     */   public void addChart(int panelId, Instrument instrument, JForexPeriod period, OfferSide offerSide) {
/* 161 */     setVisible(true);
/* 162 */     IdManager.getInstance().reserveChartId(panelId);
/*     */ 
/* 164 */     this.chartTabsAndFramesController.addChart(panelId, instrument, period, offerSide, false, true);
/*     */ 
/* 166 */     finishChartAddding(panelId);
/*     */   }
/*     */ 
/*     */   private void finishChartAddding(int panelId) {
/* 170 */     ChartPanel chartPanel = this.chartTabsAndFramesController.getChartPanelByPanelId(panelId);
/* 171 */     ChartToolBar toolBar = chartPanel.getToolBar();
/* 172 */     toolBar.getPinButton().setIcon(isAlwaysOnTop() ? DockUndockToolBar.ON_TOP_ON : DockUndockToolBar.ON_TOP_OFF);
/*     */   }
/*     */ 
/*     */   public void restoreFrames() {
/* 176 */     this.chartTabsAndFramesController.restoreState();
/*     */   }
/*     */ 
/*     */   public static void cleanChartsFrame() {
/* 180 */     instance = null;
/*     */   }
/*     */ 
/*     */   public void refreshPinBtns() {
/* 184 */     this.chartTabsAndFramesController.updatePinUnpinBtnState();
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.chart.ChartsFrame
 * JD-Core Version:    0.6.0
 */