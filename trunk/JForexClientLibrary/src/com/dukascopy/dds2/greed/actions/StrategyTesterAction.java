/*      */ package com.dukascopy.dds2.greed.actions;
/*      */ 
/*      */ import com.dukascopy.api.DataType;
/*      */ import com.dukascopy.api.IStrategy;
/*      */ import com.dukascopy.api.ITick;
/*      */ import com.dukascopy.api.IUserInterface;
/*      */ import com.dukascopy.api.Instrument;
/*      */ import com.dukascopy.api.OfferSide;
/*      */ import com.dukascopy.api.Period;
/*      */ import com.dukascopy.api.impl.IndicatorContext;
/*      */ import com.dukascopy.api.impl.StrategyWrapper;
/*      */ import com.dukascopy.api.impl.execution.Task;
/*      */ import com.dukascopy.api.indicators.IIndicator;
/*      */ import com.dukascopy.api.system.IStrategyExceptionHandler;
/*      */ import com.dukascopy.api.system.IStrategyExceptionHandler.Source;
/*      */ import com.dukascopy.api.system.ITesterClient.DataLoadingMethod;
/*      */ import com.dukascopy.api.system.ITesterClient.InterpolationMethod;
/*      */ import com.dukascopy.charts.data.datacache.DataCacheException;
/*      */ import com.dukascopy.charts.data.datacache.DataCacheUtils;
/*      */ import com.dukascopy.charts.data.datacache.JForexPeriod;
/*      */ import com.dukascopy.charts.data.datacache.LoadingProgressListener;
/*      */ import com.dukascopy.charts.data.orders.IOrdersProvider;
/*      */ import com.dukascopy.charts.main.interfaces.DDSChartsController;
/*      */ import com.dukascopy.charts.persistence.ITheme;
/*      */ import com.dukascopy.charts.persistence.ITheme.ChartElement;
/*      */ import com.dukascopy.charts.persistence.ThemeManager;
/*      */ import com.dukascopy.dds2.greed.GreedContext;
/*      */ import com.dukascopy.dds2.greed.agent.IGUIManagerImpl;
/*      */ import com.dukascopy.dds2.greed.agent.strategy.StrategyParameters;
/*      */ import com.dukascopy.dds2.greed.agent.strategy.params.Variable;
/*      */ import com.dukascopy.dds2.greed.agent.strategy.tester.AbstractHistoricalTesterIndicator;
/*      */ import com.dukascopy.dds2.greed.agent.strategy.tester.AbstractStrategyRunner;
/*      */ import com.dukascopy.dds2.greed.agent.strategy.tester.BalanceHistoricalTesterIndicator;
/*      */ import com.dukascopy.dds2.greed.agent.strategy.tester.EquityHistoricalTesterIndicator;
/*      */ import com.dukascopy.dds2.greed.agent.strategy.tester.ExecutionControl;
/*      */ import com.dukascopy.dds2.greed.agent.strategy.tester.ExecutionControlEvent;
/*      */ import com.dukascopy.dds2.greed.agent.strategy.tester.ExecutionControlListener;
/*      */ import com.dukascopy.dds2.greed.agent.strategy.tester.ITesterReport;
/*      */ import com.dukascopy.dds2.greed.agent.strategy.tester.MinTradableAmounts;
/*      */ import com.dukascopy.dds2.greed.agent.strategy.tester.ProfLossHistoricalTesterIndicator;
/*      */ import com.dukascopy.dds2.greed.agent.strategy.tester.StrategyDataStorageImpl;
/*      */ import com.dukascopy.dds2.greed.agent.strategy.tester.StrategyOptimizerEvent;
/*      */ import com.dukascopy.dds2.greed.agent.strategy.tester.StrategyOptimizerListener;
/*      */ import com.dukascopy.dds2.greed.agent.strategy.tester.StrategyOptimizerRunner;
/*      */ import com.dukascopy.dds2.greed.agent.strategy.tester.StrategyReport;
/*      */ import com.dukascopy.dds2.greed.agent.strategy.tester.StrategyRunner;
/*      */ import com.dukascopy.dds2.greed.agent.strategy.tester.TesterAccount;
/*      */ import com.dukascopy.dds2.greed.agent.strategy.tester.TesterChartData;
/*      */ import com.dukascopy.dds2.greed.agent.strategy.tester.TesterDataLoader;
/*      */ import com.dukascopy.dds2.greed.agent.strategy.tester.TesterFeedDataProvider;
/*      */ import com.dukascopy.dds2.greed.agent.strategy.tester.TesterIndicatorWrapper;
/*      */ import com.dukascopy.dds2.greed.agent.strategy.tester.TesterOrdersProvider;
/*      */ import com.dukascopy.dds2.greed.agent.strategy.tester.TesterReportData;
/*      */ import com.dukascopy.dds2.greed.agent.strategy.tester.TesterReportData.TesterEvent;
/*      */ import com.dukascopy.dds2.greed.gui.component.chart.ChartPanel;
/*      */ import com.dukascopy.dds2.greed.gui.component.chart.holders.IChartTabsAndFramesController;
/*      */ import com.dukascopy.dds2.greed.gui.component.chart.toolbar.ChartToolBar;
/*      */ import com.dukascopy.dds2.greed.gui.component.chart.toolbar.ChartToolBar.ComboBoxType;
/*      */ import com.dukascopy.dds2.greed.gui.component.dialog.disclaimers.TesterDisclaimDialog;
/*      */ import com.dukascopy.dds2.greed.gui.component.strategy.OptimizerPanel;
/*      */ import com.dukascopy.dds2.greed.gui.component.strategy.StrategyTestPanel;
/*      */ import com.dukascopy.dds2.greed.gui.component.strategy.StrategyTesterParametersDialog;
/*      */ import com.dukascopy.dds2.greed.gui.component.strategy.TesterNotificationUtils;
/*      */ import com.dukascopy.dds2.greed.gui.component.strategy.TesterParameters;
/*      */ import com.dukascopy.dds2.greed.gui.component.strategy.TesterTimeRange;
/*      */ import com.dukascopy.dds2.greed.gui.component.strategy.optimizer.ParameterOptimizationData;
/*      */ import com.dukascopy.dds2.greed.gui.l10n.LocalizationManager;
/*      */ import com.dukascopy.dds2.greed.gui.util.lotamount.LotAmountChanger;
/*      */ import com.dukascopy.dds2.greed.util.AbstractCurrencyConverter;
/*      */ import com.dukascopy.dds2.greed.util.INotificationUtils;
/*      */ import com.dukascopy.dds2.greed.util.IndicatorHelper;
/*      */ import com.dukascopy.dds2.greed.util.LoggerNotificationUtils;
/*      */ import java.awt.Color;
/*      */ import java.awt.Desktop;
/*      */ import java.awt.event.ActionEvent;
/*      */ import java.awt.event.ActionListener;
/*      */ import java.io.File;
/*      */ import java.io.FileNotFoundException;
/*      */ import java.io.FileOutputStream;
/*      */ import java.io.IOException;
/*      */ import java.lang.reflect.Field;
/*      */ import java.lang.reflect.Method;
/*      */ import java.math.BigDecimal;
/*      */ import java.text.DecimalFormat;
/*      */ import java.text.MessageFormat;
/*      */ import java.text.NumberFormat;
/*      */ import java.text.SimpleDateFormat;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Calendar;
/*      */ import java.util.Date;
/*      */ import java.util.HashMap;
/*      */ import java.util.HashSet;
/*      */ import java.util.Iterator;
/*      */ import java.util.LinkedList;
/*      */ import java.util.List;
/*      */ import java.util.Map;
/*      */ import java.util.Map.Entry;
/*      */ import java.util.Set;
/*      */ import java.util.TimeZone;
/*      */ import javax.swing.JComboBox;
/*      */ import javax.swing.JFrame;
/*      */ import javax.swing.JOptionPane;
/*      */ import javax.swing.SwingUtilities;
/*      */ import javax.swing.Timer;
/*      */ import org.slf4j.Logger;
/*      */ import org.slf4j.LoggerFactory;
/*      */ 
/*      */ public class StrategyTesterAction extends AppActionEvent
/*      */ {
/*  110 */   private static final Logger LOGGER = LoggerFactory.getLogger(StrategyTesterAction.class);
/*      */   private StrategyTestPanel testerPanel;
/*      */   private boolean runTester;
/*      */   private long from;
/*      */   private long to;
/*      */   private Set<Instrument> instruments;
/*      */   private Set<Instrument> instrumentsForConversion;
/*      */   private Period period;
/*      */   private ITesterClient.DataLoadingMethod dataLoadingMethod;
/*      */   private OfferSide offerSide;
/*      */   private ITesterClient.InterpolationMethod interpolationMethod;
/*      */   private StrategyWrapper strategyWrapper;
/*      */   private TesterAccount account;
/*      */   private ITesterReport singleTesterReport;
/*      */   private volatile boolean doNotRunMe;
/*      */   private boolean loadData;
/*      */   private TesterParameters testerParameters;
/*      */   private TesterFeedDataProvider testerFeedDataProvider;
/*      */   private ExecutionControl executionControl;
/*      */   private File messagesFile;
/*      */   private boolean appendMessages;
/*  135 */   private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm") { } ;
/*  136 */   private AbstractStrategyRunner strategyRunner = null;
/*      */   private long testStartDate;
/*      */ 
/*      */   public StrategyTesterAction(ExecutionControl executionControl, StrategyTestPanel testerPanel, long from, long to, List<Instrument> instruments, ITesterClient.DataLoadingMethod dataLoadingMethod)
/*      */   {
/*  151 */     super(testerPanel, false, true);
/*      */   }
/*      */ 
/*      */   public StrategyTesterAction(boolean runTester, ExecutionControl executionControl, StrategyTestPanel testerPanel, Set<Instrument> instruments, ITesterClient.DataLoadingMethod dataLoadingMethod, Period period, OfferSide offerSide, ITesterClient.InterpolationMethod interpolationMethod, StrategyWrapper strategyWrapper, TesterAccount account, TesterParameters testerParameters, File messagesFile, boolean appendMessages)
/*      */   {
/*  171 */     super(testerPanel, false, true);
/*  172 */     this.executionControl = executionControl;
/*  173 */     this.runTester = runTester;
/*  174 */     this.testerPanel = testerPanel;
/*      */ 
/*  176 */     this.from = testerParameters.getTesterTimeRange().getDateFrom();
/*  177 */     this.to = testerParameters.getTesterTimeRange().getDateTo();
/*  178 */     if (!checkRange(this.from, this.to)) {
/*  179 */       this.doNotRunMe = true;
/*  180 */       return;
/*      */     }
/*      */ 
/*  183 */     if ((period != null) && (period != Period.TICK)) {
/*  184 */       this.from = DataCacheUtils.getCandleStartFast(period, this.from);
/*  185 */       this.to = DataCacheUtils.getCandleStartFast(period, this.to);
/*      */     }
/*      */ 
/*  188 */     this.instruments = new HashSet(instruments);
/*  189 */     this.instrumentsForConversion = new HashSet();
/*  190 */     for (Instrument instrument : this.instruments) {
/*  191 */       Set conversionDeps = AbstractCurrencyConverter.getConversionDeps(instrument.getSecondaryCurrency(), Instrument.EURUSD.getSecondaryCurrency());
/*  192 */       for (Instrument instrumentDep : conversionDeps) {
/*  193 */         this.instrumentsForConversion.add(instrumentDep);
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  198 */     if (account != null) {
/*  199 */       Set conversionDeps = AbstractCurrencyConverter.getConversionDeps(Instrument.EURUSD.getSecondaryCurrency(), account.getCurrency());
/*  200 */       if (!conversionDeps.isEmpty()) {
/*  201 */         this.instrumentsForConversion.add(conversionDeps.iterator().next());
/*      */       }
/*      */     }
/*      */ 
/*  205 */     this.instrumentsForConversion.removeAll(this.instruments);
/*  206 */     if (!this.instrumentsForConversion.isEmpty()) {
/*  207 */       this.instruments.addAll(this.instrumentsForConversion);
/*      */     }
/*      */ 
/*  210 */     this.dataLoadingMethod = dataLoadingMethod;
/*  211 */     this.period = period;
/*  212 */     this.offerSide = offerSide;
/*  213 */     this.interpolationMethod = interpolationMethod;
/*  214 */     this.strategyWrapper = strategyWrapper;
/*  215 */     this.account = account;
/*  216 */     this.testerParameters = testerParameters;
/*  217 */     this.messagesFile = messagesFile;
/*  218 */     this.appendMessages = appendMessages;
/*      */ 
/*  220 */     this.executionControl.setOptimization(this.testerParameters.isOptimizationMode());
/*  221 */     testerPanel.lockGUI(runTester);
/*      */   }
/*      */ 
/*      */   public void doAction()
/*      */   {
/*  226 */     if (this.doNotRunMe) {
/*  227 */       return;
/*      */     }
/*  229 */     if (this.runTester)
/*      */     {
/*  231 */       this.testStartDate = System.currentTimeMillis();
/*      */ 
/*  233 */       FileOutputStream messagesStream = null;
/*  234 */       if (this.messagesFile != null)
/*      */       {
/*  236 */         if (this.messagesFile.isDirectory()) {
/*  237 */           SimpleDateFormat sdf = new SimpleDateFormat("ddMMyyyy_HHmmss");
/*  238 */           String strategyName = this.strategyWrapper.getName().substring(0, this.strategyWrapper.getName().indexOf(46));
/*  239 */           String fileName = strategyName + "_" + sdf.format(new Date(this.testStartDate)) + ".csv";
/*  240 */           this.messagesFile = new File(this.messagesFile, fileName);
/*      */         }
/*      */ 
/*  248 */         int attempts = 0;
/*  249 */         while (messagesStream == null) { attempts++; if (attempts > 5) break;
/*      */           try {
/*  251 */             messagesStream = new FileOutputStream(this.messagesFile, this.appendMessages);
/*      */           } catch (FileNotFoundException ex) {
/*  253 */             if (attempts == 5)
/*  254 */               LOGGER.error("Cannot store messages.", ex);
/*      */             try
/*      */             {
/*  257 */               Thread.sleep(200L);
/*      */             }
/*      */             catch (InterruptedException e)
/*      */             {
/*      */             }
/*      */           } }
/*      */       }
/*  264 */       this.testerPanel.getTesterNotification().setFileStream(messagesStream);
/*      */       try {
/*  266 */         startTest();
/*      */       }
/*      */       finally
/*      */       {
/*      */         CloseStreamEvent closeStreamEvent;
/*  268 */         this.testerPanel.getTesterNotification().setFileStream(null);
/*  269 */         if (messagesStream != null) {
/*  270 */           CloseStreamEvent closeStreamEvent = new CloseStreamEvent(this.testerPanel, messagesStream);
/*  271 */           GreedContext.publishEvent(closeStreamEvent);
/*      */         }
/*      */       }
/*      */     }
/*      */     else {
/*  276 */       loadData();
/*      */     }
/*      */   }
/*      */ 
/*      */   private void loadData()
/*      */   {
/*  327 */     this.loadData = true;
/*  328 */     this.runTester = true;
/*      */     try {
/*  330 */       SwingUtilities.invokeAndWait(new Runnable() {
/*      */         public void run() {
/*  332 */           if (!TesterDisclaimDialog.isAcceptState()) {
/*  333 */             TesterDisclaimDialog disclaimer = TesterDisclaimDialog.getInstance();
/*  334 */             disclaimer.showDialog();
/*  335 */             if (!disclaimer.isAccepted()) {
/*  336 */               StrategyTesterAction.access$102(StrategyTesterAction.this, false);
/*  337 */               StrategyTesterAction.access$202(StrategyTesterAction.this, false);
/*      */             }
/*      */           }
/*      */         } } );
/*      */     } catch (Exception e) {
/*  343 */       LOGGER.error(e.getMessage(), e);
/*      */     }
/*      */ 
/*  346 */     if (!this.loadData) {
/*  347 */       return;
/*      */     }
/*      */ 
/*  350 */     TesterDataLoader testerDataLoader = new TesterDataLoader(this.from, this.to, this.instruments, new LoadingProgressListener()
/*      */     {
/*      */       public void dataLoaded(long startTime, long endTime, long currentTime, String information) {
/*      */         try {
/*  354 */           SwingUtilities.invokeAndWait(new Runnable(currentTime, information) {
/*      */             public void run() {
/*  356 */               StrategyTesterAction.this.testerPanel.updateProgressBar((int)this.val$currentTime, this.val$information);
/*      */             } } );
/*      */         } catch (Exception e) {
/*  360 */           StrategyTesterAction.LOGGER.error(e.getMessage(), e);
/*      */         }
/*      */       }
/*      */ 
/*      */       public void loadingFinished(boolean allDataLoaded, long startTime, long endTime, long currentTime, Exception e)
/*      */       {
/*      */         try {
/*  367 */           SwingUtilities.invokeAndWait(new Runnable(currentTime, allDataLoaded) {
/*      */             public void run() {
/*  369 */               StrategyTesterAction.this.testerPanel.updateProgressBar((int)this.val$currentTime, StrategyTesterAction.3.this.stopJob() ? "Downloading canceled" : this.val$allDataLoaded ? "Downloading finished" : "Downloading failed");
/*      */             } } );
/*      */         } catch (Exception ex) {
/*  373 */           StrategyTesterAction.LOGGER.error(ex.getMessage(), ex);
/*      */         }
/*      */       }
/*      */ 
/*      */       public boolean stopJob()
/*      */       {
/*  379 */         return StrategyTesterAction.this.testerPanel.dataLoadingCancelRequested();
/*      */       }
/*      */     });
/*  382 */     testerDataLoader.loadData();
/*      */   }
/*      */ 
/*      */   private void startTest() {
/*  386 */     this.runTester = true;
/*      */     try {
/*  388 */       SwingUtilities.invokeAndWait(new Runnable() {
/*      */         public void run() {
/*  390 */           if (!TesterDisclaimDialog.isAcceptState()) {
/*  391 */             TesterDisclaimDialog disclaimer = TesterDisclaimDialog.getInstance();
/*  392 */             disclaimer.showDialog();
/*  393 */             if (!disclaimer.isAccepted())
/*  394 */               StrategyTesterAction.access$202(StrategyTesterAction.this, false);
/*      */           }
/*      */         } } );
/*      */     } catch (Exception e) {
/*  399 */       LOGGER.error(e.getMessage(), e);
/*      */     }
/*      */ 
/*  402 */     if (!this.runTester) {
/*  403 */       return;
/*      */     }
/*      */ 
/*  575 */     1LoadingProgressListenerTimeRemaining loadingProgressListenerTimeRemaining = new Object()
/*      */     {
/*  408 */       NumberFormat twoDigitFormatter = new DecimalFormat("00");
/*  409 */       long m_startTime = 0L;
/*  410 */       long m_endTime = 0L;
/*  411 */       long m_currentTime = 0L;
/*  412 */       int m_value = 0;
/*  413 */       String m_syncObj = "";
/*  414 */       long m_remaining = 0L;
/*  415 */       long m_startProcessing = 0L;
/*      */       String m_information;
/*  417 */       final String zeroRemainingTime = "0 00:00:00";
/*  418 */       String m_remainingFormatted = "0 00:00:00";
/*  419 */       String m_percentDone = "";
/*  420 */       long m_correctingCounter = 0L;
/*      */ 
/*  426 */       Timer timer = new Timer(1000, new Object() {
/*      */         public void actionPerformed(ActionEvent e) {
/*  428 */           if (StrategyTesterAction.this.executionControl.isPaused()) {
/*  429 */             StrategyTesterAction.1LoadingProgressListenerTimeRemaining.this.m_startProcessing = 0L;
/*  430 */             return;
/*      */           }
/*  432 */           if (StrategyTesterAction.1LoadingProgressListenerTimeRemaining.this.m_startProcessing == 0L) {
/*  433 */             StrategyTesterAction.1LoadingProgressListenerTimeRemaining.this.m_startProcessing = System.currentTimeMillis();
/*  434 */             StrategyTesterAction.1LoadingProgressListenerTimeRemaining.this.m_startTime = StrategyTesterAction.1LoadingProgressListenerTimeRemaining.this.m_currentTime;
/*  435 */             return;
/*      */           }
/*      */ 
/*  438 */           synchronized (StrategyTesterAction.1LoadingProgressListenerTimeRemaining.this.m_syncObj) {
/*  439 */             long timeElapsed = System.currentTimeMillis() - StrategyTesterAction.1LoadingProgressListenerTimeRemaining.this.m_startProcessing;
/*  440 */             if (StrategyTesterAction.1LoadingProgressListenerTimeRemaining.this.m_currentTime > StrategyTesterAction.1LoadingProgressListenerTimeRemaining.this.m_startTime) {
/*  441 */               double temp = (StrategyTesterAction.1LoadingProgressListenerTimeRemaining.this.m_endTime - StrategyTesterAction.1LoadingProgressListenerTimeRemaining.this.m_currentTime) / (StrategyTesterAction.1LoadingProgressListenerTimeRemaining.this.m_currentTime - StrategyTesterAction.1LoadingProgressListenerTimeRemaining.this.m_startTime);
/*  442 */               StrategyTesterAction.1LoadingProgressListenerTimeRemaining.this.m_remaining = ()(timeElapsed * temp);
/*      */             }
/*      */ 
/*  445 */             if (StrategyTesterAction.1LoadingProgressListenerTimeRemaining.this.m_remaining < 0L) {
/*  446 */               StrategyTesterAction.1LoadingProgressListenerTimeRemaining.this.m_remaining = 0L;
/*      */             }
/*  448 */             StrategyTesterAction.1LoadingProgressListenerTimeRemaining.this.m_remainingFormatted = StrategyTesterAction.1LoadingProgressListenerTimeRemaining.this.formatRemainingTime(StrategyTesterAction.1LoadingProgressListenerTimeRemaining.this.m_remaining);
/*  449 */             StrategyTesterAction.this.testerPanel.updateProgressBar(StrategyTesterAction.1LoadingProgressListenerTimeRemaining.this.m_value, StrategyTesterAction.1LoadingProgressListenerTimeRemaining.this.getProgressInfo(), StrategyTesterAction.1LoadingProgressListenerTimeRemaining.this.m_information);
/*      */           }
/*      */         }
/*      */       });
/*      */ 
/*      */       public void stateChanged(ExecutionControlEvent event)
/*      */       {
/*      */       }
/*      */ 
/*      */       public void speedChanged(ExecutionControlEvent event)
/*      */       {
/*  458 */         this.m_correctingCounter = 0L;
/*      */ 
/*  460 */         this.m_startProcessing = System.currentTimeMillis();
/*  461 */         this.m_startTime = this.m_currentTime;
/*      */ 
/*  463 */         if (this.timer.isRunning())
/*  464 */           this.timer.restart();
/*      */       }
/*      */ 
/*      */       public void dataLoaded(long startTime, long endTime, long currentTime, String information)
/*      */       {
/*  470 */         synchronized (this.m_syncObj) {
/*  471 */           this.m_endTime = endTime;
/*  472 */           this.m_currentTime = currentTime;
/*  473 */           this.m_information = information;
/*      */ 
/*  475 */           if (!this.timer.isRunning()) {
/*  476 */             if (StrategyTesterAction.this.executionControl.getSpeed() != 7) {
/*  477 */               this.timer.setInitialDelay(0);
/*      */             }
/*      */ 
/*  480 */             this.m_startProcessing = System.currentTimeMillis();
/*  481 */             this.m_startTime = startTime;
/*  482 */             this.timer.start();
/*      */           }
/*      */         }
/*      */         try
/*      */         {
/*  487 */           StrategyTesterAction.LOGGER.debug("Strategy execution progress: " + information);
/*  488 */           SwingUtilities.invokeLater(new Runnable(currentTime, startTime, endTime, information) {
/*      */             public void run() {
/*  490 */               long value = (this.val$currentTime - this.val$startTime) * 100L / (this.val$endTime - this.val$startTime);
/*  491 */               StrategyTesterAction.1LoadingProgressListenerTimeRemaining.this.m_percentDone = ((int)value + "%");
/*      */ 
/*  493 */               StrategyTesterAction.1LoadingProgressListenerTimeRemaining.this.m_value = (int)value;
/*  494 */               StrategyTesterAction.this.testerPanel.updateProgressBar((int)value, StrategyTesterAction.1LoadingProgressListenerTimeRemaining.this.getProgressInfo(), this.val$information);
/*      */             } } );
/*      */         } catch (Exception e) {
/*  498 */           StrategyTesterAction.LOGGER.error(e.getMessage(), e);
/*      */         }
/*      */       }
/*      */ 
/*      */       private String formatRemainingTime(long remaining)
/*      */       {
/*  504 */         long secsIn = remaining / 1000L;
/*  505 */         long days = secsIn / 86400L;
/*  506 */         long remainder = secsIn % 86400L;
/*  507 */         long hours = remainder / 3600L;
/*  508 */         remainder = secsIn % 3600L;
/*  509 */         long minutes = remainder / 60L;
/*  510 */         long seconds = remainder % 60L;
/*      */ 
/*  512 */         StringBuffer buff = new StringBuffer(128).append(days).append(" ").append(this.twoDigitFormatter.format(hours)).append(":").append(this.twoDigitFormatter.format(minutes)).append(":").append(this.twoDigitFormatter.format(seconds));
/*      */ 
/*  521 */         return buff.toString();
/*      */       }
/*      */ 
/*      */       private void changeRemainingTime(long remaining, double factor) {
/*  525 */         if (--this.m_correctingCounter <= 0L) {
/*  526 */           this.m_correctingCounter = 5L;
/*  527 */           this.m_remaining = ()(remaining / factor);
/*      */         } else {
/*  529 */           this.m_remaining -= 1000L;
/*      */         }
/*      */       }
/*      */ 
/*      */       private String getProgressInfo() {
/*  534 */         return this.m_remainingFormatted + "  " + this.m_percentDone;
/*      */       }
/*      */ 
/*      */       public void loadingFinished(boolean allDataLoaded, long startTime, long endTime, long currentTime, Exception ex) {
/*  538 */         this.timer.stop();
/*      */ 
/*  540 */         if ((!allDataLoaded) && (ex != null))
/*  541 */           StrategyTesterAction.LOGGER.error(ex.getMessage(), ex);
/*      */         try
/*      */         {
/*      */           long value;
/*      */           long value;
/*  545 */           if (StrategyTesterAction.this.testerPanel.strategyTestCanceled()) {
/*  546 */             value = 0L;
/*      */           }
/*      */           else
/*      */           {
/*      */             long value;
/*  547 */             if (allDataLoaded)
/*  548 */               value = 100L;
/*      */             else {
/*  550 */               value = (currentTime - startTime) * 100L / (endTime - startTime);
/*      */             }
/*      */           }
/*  553 */           this.m_remainingFormatted = "0 00:00:00";
/*  554 */           this.m_percentDone = "0%";
/*      */ 
/*  556 */           SwingUtilities.invokeAndWait(new Runnable(value) {
/*      */             public void run() {
/*  558 */               StrategyTesterAction.this.testerPanel.updateProgressBar((int)this.val$value, StrategyTesterAction.1LoadingProgressListenerTimeRemaining.this.getProgressInfo(), "");
/*      */             }
/*      */           });
/*  562 */           if (StrategyTesterAction.this.executionControl != null)
/*  563 */             StrategyTesterAction.this.executionControl.removeExecutionControlListener(this);
/*      */         }
/*      */         catch (Exception e) {
/*  566 */           StrategyTesterAction.LOGGER.error(e.getMessage(), e);
/*      */         }
/*      */       }
/*      */ 
/*      */       public boolean stopJob() {
/*  571 */         return StrategyTesterAction.this.testerPanel.strategyTestCanceled();
/*      */       }
/*      */     };
/*  577 */     LoadingProgressListener loadingProgressListenerSimple = new LoadingProgressListener()
/*      */     {
/*      */       public void dataLoaded(long startTime, long endTime, long currentTime, String information) {
/*  580 */         StrategyTesterAction.LOGGER.debug("Strategy execution progress: " + information);
/*      */         try {
/*  582 */           SwingUtilities.invokeLater(new Runnable(currentTime, startTime, endTime, information) {
/*      */             public void run() {
/*  584 */               long value = (this.val$currentTime - this.val$startTime) * 100L / (this.val$endTime - this.val$startTime);
/*  585 */               StrategyTesterAction.this.testerPanel.updateProgressBar((int)value, this.val$information);
/*      */             } } );
/*      */         } catch (Exception e) {
/*  589 */           StrategyTesterAction.LOGGER.error(e.getMessage(), e);
/*      */         }
/*      */       }
/*      */ 
/*      */       public void loadingFinished(boolean allDataLoaded, long startTime, long endTime, long currentTime, Exception ex) {
/*  594 */         if ((!allDataLoaded) && (ex != null))
/*  595 */           StrategyTesterAction.LOGGER.error(ex.getMessage(), ex);
/*      */         try
/*      */         {
/*      */           long value;
/*      */           long value;
/*  599 */           if (StrategyTesterAction.this.testerPanel.strategyTestCanceled()) {
/*  600 */             value = 0L;
/*      */           }
/*      */           else
/*      */           {
/*      */             long value;
/*  601 */             if (allDataLoaded)
/*  602 */               value = 100L;
/*      */             else {
/*  604 */               value = (currentTime - startTime) * 100L / (endTime - startTime);
/*      */             }
/*      */           }
/*  607 */           SwingUtilities.invokeAndWait(new Runnable(value) {
/*      */             public void run() {
/*  609 */               StrategyTesterAction.this.testerPanel.updateProgressBar((int)this.val$value, "");
/*      */             } } );
/*      */         } catch (Exception e) {
/*  613 */           StrategyTesterAction.LOGGER.error(e.getMessage(), e);
/*      */         }
/*      */       }
/*      */ 
/*      */       public boolean stopJob() {
/*  618 */         return StrategyTesterAction.this.testerPanel.strategyTestCanceled();
/*      */       }
/*      */     };
/*  622 */     if ((!this.testerPanel.strategyTestCanceled()) && (this.strategyWrapper.isRunnable()))
/*      */     {
/*  624 */       LoadingProgressListener loadingProgressListener = null;
/*      */ 
/*  626 */       if (this.testerParameters.isOptimizationMode()) {
/*  627 */         loadingProgressListener = loadingProgressListenerTimeRemaining;
/*      */       }
/*      */       else {
/*  630 */         loadingProgressListener = loadingProgressListenerTimeRemaining;
/*      */       }
/*      */ 
/*  633 */       IStrategy strategy = null;
/*      */       try {
/*  635 */         strategy = this.strategyWrapper.getStrategy(false);
/*      */       } catch (Exception e) {
/*  637 */         LOGGER.error(e.getMessage(), e);
/*  638 */         this.testerPanel.getTesterNotification().postErrorMessage("Error while loading strategy: " + e.getMessage(), true);
/*  639 */         return;
/*      */       }
/*  641 */       if (strategy == null) {
/*  642 */         return;
/*      */       }
/*      */ 
/*  645 */       IStrategyExceptionHandler exceptionHandler = new IStrategyExceptionHandler()
/*      */       {
/*      */         public void onException(long strategyId, IStrategyExceptionHandler.Source source, Throwable t) {
/*  648 */           StrategyTesterAction.LOGGER.error("Exception thrown whiler running " + source + " method: " + t.getMessage(), t);
/*      */         }
/*      */       };
/*      */       try
/*      */       {
/*  654 */         SwingUtilities.invokeAndWait(new Runnable() {
/*      */           public void run() {
/*  656 */             if ((StrategyTesterAction.this.testerParameters.isOptimizationMode()) || (StrategyTesterAction.this.period != Period.TICK))
/*  657 */               StrategyTesterAction.this.testerPanel.updateProgressBar(0, "");
/*      */             else
/*  659 */               StrategyTesterAction.this.testerPanel.updateProgressBar(0, "0 00:00:00  0%", "");
/*      */           } } );
/*      */       }
/*      */       catch (Exception e) {
/*  664 */         LOGGER.error(e.getMessage(), e);
/*      */       }
/*      */ 
/*  670 */       JFrame parentFrame = (JFrame)GreedContext.get("clientGui");
/*      */ 
/*  672 */       HashMap tempParams = StrategyParameters.getParameters(this.strategyWrapper.getName());
/*  673 */       StrategyTesterParametersDialog strategyTesterParametersDialog = new StrategyTesterParametersDialog(parentFrame, strategy, false, getInputOptimizationData(), tempParams, this.strategyWrapper.getBinaryFile(), this.testerParameters.isOptimizationMode());
/*      */ 
/*  685 */       MinTradableAmounts minTradableAmounts = new MinTradableAmounts(Double.valueOf(LotAmountChanger.getMinTradableAmountInUnits().doubleValue()));
/*      */ 
/*  688 */       minTradableAmounts.put(Instrument.XAUUSD, Double.valueOf(LotAmountChanger.getMinTradableAmountInUnits(Instrument.XAUUSD).doubleValue()));
/*  689 */       minTradableAmounts.put(Instrument.XAGUSD, Double.valueOf(LotAmountChanger.getMinTradableAmountInUnits(Instrument.XAGUSD).doubleValue()));
/*      */ 
/*  691 */       if (this.testerParameters.isOptimizationMode())
/*      */       {
/*  697 */         this.testerPanel.disableMessagesConsoleButtons();
/*      */ 
/*  699 */         ParameterOptimizationData parameterOptimizationData = strategyTesterParametersDialog.getParameterOptimizationData();
/*  700 */         if (parameterOptimizationData != null) {
/*  701 */           HashMap params = parameterOptimizationData.getParameters();
/*  702 */           double dropDown = parameterOptimizationData.getDropDown();
/*      */ 
/*  704 */           this.testerPanel.switchStrategyRunningModes();
/*  705 */           this.testerPanel.setOptimizationParams(params);
/*  706 */           this.testerPanel.setBalanceDropDown(dropDown);
/*  707 */           List parameters = getCombinations(params);
/*      */ 
/*  710 */           this.testerPanel.pnlOptimizer.clearData();
/*      */ 
/*  712 */           String strategyName = this.strategyWrapper.getName();
/*  713 */           boolean fullAccessGranted = this.strategyWrapper.isFullAccessGranted();
/*      */ 
/*  715 */           List strategies = new ArrayList();
/*  716 */           List accounts = new LinkedList();
/*  717 */           List reports = new LinkedList();
/*      */ 
/*  719 */           for (int i = 0; i < Math.max(parameters.size(), 1); i++) { IStrategy aStrategy;
/*      */             List valuesAsString;
/*      */             HashMap variables;
/*      */             try { aStrategy = this.strategyWrapper.getStrategy(true);
/*      */ 
/*  727 */               valuesAsString = new LinkedList();
/*      */               HashMap variables;
/*  729 */               if (i < parameters.size())
/*      */               {
/*  731 */                 variables = (HashMap)parameters.get(i);
/*  732 */                 for (String name : variables.keySet()) {
/*  733 */                   Variable variable = (Variable)variables.get(name);
/*      */ 
/*  735 */                   Field field = aStrategy.getClass().getField(name);
/*  736 */                   if (field.getType().isEnum()) {
/*  737 */                     Object parameter = variable.getValue();
/*  738 */                     Method method = parameter.getClass().getMethod("ordinal", new Class[0]);
/*  739 */                     Integer ordinal = (Integer)method.invoke(parameter, new Object[0]);
/*  740 */                     Object[] enumConstants = field.getType().getEnumConstants();
/*  741 */                     field.set(aStrategy, enumConstants[ordinal.intValue()]);
/*      */                   } else {
/*  743 */                     field.set(aStrategy, variable.getValue());
/*      */                   }
/*      */ 
/*  746 */                   String[] nameValue = { name, getValueAsString(variable) };
/*      */ 
/*  750 */                   valuesAsString.add(nameValue);
/*      */                 }
/*      */               } else {
/*  753 */                 variables = null;
/*      */               }
/*      */             } catch (Exception ex)
/*      */             {
/*  757 */               LOGGER.error("Error starting strategy.", ex);
/*  758 */               break;
/*      */             }
/*      */ 
/*  761 */             OptimizerAccount optimizerAccount = new OptimizerAccount(this.account, this.testerPanel.pnlOptimizer, this.executionControl);
/*  762 */             optimizerAccount.update5SecDelayedValues();
/*      */ 
/*  764 */             OptimizerReportData report = new OptimizerReportData(strategyName, this.from, this.to, optimizerAccount.getDeposit(), valuesAsString, this.testerPanel.pnlOptimizer);
/*      */ 
/*  773 */             this.testerPanel.pnlOptimizer.addTesterReport(optimizerAccount, report, variables, aStrategy);
/*      */ 
/*  780 */             strategies.add(aStrategy);
/*  781 */             accounts.add(optimizerAccount);
/*  782 */             reports.add(report);
/*      */           }
/*      */ 
/*  785 */           this.testerPanel.pnlOptimizer.setExecutionControl(this.executionControl);
/*      */ 
/*  787 */           this.strategyRunner = new StrategyOptimizerRunner(strategies, fullAccessGranted, this.dataLoadingMethod, this.period, this.offerSide, this.interpolationMethod, this.from, this.to, this.instruments, this.instrumentsForConversion, loadingProgressListener, minTradableAmounts, accounts, this.executionControl, exceptionHandler, dropDown, reports);
/*      */ 
/*  804 */           ((StrategyOptimizerRunner)this.strategyRunner).addListener(new StrategyOptimizerListener()
/*      */           {
/*      */             public void strategyRemoved(StrategyOptimizerEvent event) {
/*  807 */               IStrategy removedStrategy = event.getStrategy();
/*  808 */               StrategyTesterAction.this.testerPanel.pnlOptimizer.removeReport(removedStrategy);
/*      */             }
/*      */           });
/*  813 */           ClassLoader jfxClassLoader = this.strategyWrapper.getClassLoader();
/*  814 */           ClassLoader oldClassLoader = Thread.currentThread().getContextClassLoader();
/*  815 */           Thread.currentThread().setContextClassLoader(jfxClassLoader);
/*      */           try
/*      */           {
/*  818 */             this.strategyRunner.startSynched();
/*      */           } finally {
/*  820 */             Thread.currentThread().setContextClassLoader(oldClassLoader);
/*      */           }
/*      */         }
/*      */       }
/*      */       else
/*      */       {
/*  826 */         List strategyParameters = null;
/*  827 */         Task taskParameter = strategyTesterParametersDialog.getTaskParameter();
/*  828 */         boolean canProceed = false;
/*  829 */         if (taskParameter != null) {
/*      */           try
/*      */           {
/*  832 */             canProceed = ((Boolean)taskParameter.call()).booleanValue();
/*      */           } catch (Exception e) {
/*  834 */             LOGGER.error(e.getMessage(), e);
/*      */           }
/*      */         }
/*  837 */         if (!canProceed) {
/*  838 */           loadingProgressListener.loadingFinished(false, this.from, this.to, this.from, null);
/*  839 */           return;
/*      */         }
/*  841 */         strategyParameters = strategyTesterParametersDialog.getValues();
/*      */ 
/*  844 */         this.testerPanel.switchStrategyRunningModes();
/*      */         INotificationUtils notificationUtils;
/*      */         INotificationUtils notificationUtils;
/*  847 */         if (this.testerPanel.isMessagesEnabled())
/*  848 */           notificationUtils = this.testerPanel.getTesterNotification();
/*      */         else {
/*  850 */           notificationUtils = new LoggerNotificationUtils(LOGGER);
/*      */         }
/*      */ 
/*  853 */         TesterOrdersProvider testerOrdersProvider = new TesterOrdersProvider();
/*  854 */         LinkedList indicators = new LinkedList();
/*      */ 
/*  856 */         String date_from = this.dateFormat.format(new Date(this.from));
/*  857 */         String date_to = this.dateFormat.format(new Date(this.to));
/*  858 */         String toolTip = MessageFormat.format("{0}, {1} - {2}", new Object[] { this.strategyWrapper.getName(), date_from, date_to });
/*  859 */         Map chartPanels = new HashMap();
/*      */ 
/*  861 */         if ((this.testerParameters.isVisualModeEnabled()) && (!this.testerParameters.isOptimizationMode()))
/*      */           try {
/*  863 */             SwingUtilities.invokeAndWait(new Runnable(toolTip, testerOrdersProvider, chartPanels, indicators, notificationUtils) {
/*      */               public void run() {
/*  865 */                 StrategyTesterAction.this.openTesterTabs(this.val$toolTip, this.val$testerOrdersProvider, this.val$chartPanels, StrategyTesterAction.this.testerParameters);
/*  866 */                 StrategyTesterAction.this.addTesterIndicators(this.val$chartPanels, StrategyTesterAction.this.testerParameters, this.val$indicators, StrategyTesterAction.this.from, StrategyTesterAction.this.account.getDeposit(), this.val$notificationUtils);
/*      */               } } );
/*      */           } catch (Exception e) {
/*  870 */             LOGGER.error(e.getMessage(), e);
/*  871 */             chartPanels.clear();
/*      */           }
/*      */         StrategyDataStorageImpl indicatorStorage;
/*      */         StrategyDataStorageImpl indicatorStorage;
/*  876 */         if (this.testerParameters.isAnyIndicatorShown())
/*  877 */           indicatorStorage = new StrategyDataStorageImpl();
/*      */         else {
/*  879 */           indicatorStorage = null;
/*      */         }
/*      */ 
/*  882 */         for (IIndicator iIndicator : indicators) {
/*  883 */           ((AbstractHistoricalTesterIndicator)iIndicator).setIndicatorStorage(indicatorStorage);
/*      */         }
/*      */ 
/*  886 */         this.strategyRunner = new StrategyRunner(parentFrame, this.strategyWrapper.getName(), strategy, this.strategyWrapper.isFullAccessGranted(), this.period, this.offerSide, this.interpolationMethod, this.dataLoadingMethod, this.from, this.to, this.instruments, this.instrumentsForConversion, loadingProgressListener, notificationUtils, minTradableAmounts, this.account, this.testerFeedDataProvider, testerOrdersProvider, chartPanels, this.executionControl, this.testerParameters.isProcessingStatsEnabled(), exceptionHandler, indicatorStorage, strategyParameters);
/*      */         try
/*      */         {
/*  916 */           ClassLoader jfxClassLoader = this.strategyWrapper.getClassLoader();
/*  917 */           ClassLoader oldClassLoader = Thread.currentThread().getContextClassLoader();
/*  918 */           Thread.currentThread().setContextClassLoader(jfxClassLoader);
/*      */           try
/*      */           {
/*  921 */             this.strategyRunner.startSynched();
/*  922 */             if ((this.testerPanel.strategyTestCanceled()) || (this.strategyRunner.wasCanceled()))
/*      */             {
/*  924 */               if ((this.strategyRunner.wasCanceled()) && (this.testerParameters.isVisualModeEnabled())) {
/*  925 */                 for (TesterChartData chart : chartPanels.values()) {
/*  926 */                   this.testerPanel.closeTesterTab(chart);
/*      */                 }
/*      */               }
/*      */             }
/*      */ 
/*  931 */             this.singleTesterReport = ((StrategyRunner)this.strategyRunner).getReportData();
/*      */           }
/*      */           finally {
/*  934 */             Thread.currentThread().setContextClassLoader(oldClassLoader);
/*      */           }
/*      */         }
/*      */         finally {
/*  938 */           if (indicatorStorage != null)
/*  939 */             indicatorStorage.close();
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   private ParameterOptimizationData getInputOptimizationData()
/*      */   {
/*      */     HashMap params;
/*      */     HashMap inputParameters;
/*      */     HashMap params;
/*  950 */     if (StrategyParameters.areParametersSet(this.strategyWrapper.getName())) {
/*  951 */       params = new HashMap();
/*      */ 
/*  953 */       inputParameters = StrategyParameters.getParameters(this.strategyWrapper.getName());
/*  954 */       for (String name : inputParameters.keySet())
/*  955 */         params.put(name, new Variable[] { (Variable)inputParameters.get(name) });
/*      */     }
/*      */     else
/*      */     {
/*  959 */       params = this.testerPanel.getOptimizationParams();
/*      */     }
/*  961 */     double dropDown = this.testerPanel.getBalanceDropDown();
/*      */ 
/*  963 */     return new ParameterOptimizationData(params, dropDown);
/*      */   }
/*      */ 
/*      */   private String getValueAsString(Variable variable) {
/*  967 */     Object value = variable.getValue();
/*  968 */     if (value == null) {
/*  969 */       return "";
/*      */     }
/*  971 */     if ((value instanceof Calendar)) {
/*  972 */       return this.dateFormat.format(((Calendar)value).getTime());
/*      */     }
/*  974 */     if ((value instanceof Date)) {
/*  975 */       return this.dateFormat.format((Date)value);
/*      */     }
/*      */ 
/*  978 */     return value.toString();
/*      */   }
/*      */ 
/*      */   private List<HashMap<String, Variable>> getCombinations(HashMap<String, Variable[]> params)
/*      */   {
/*  984 */     List result = new LinkedList();
/*      */ 
/*  986 */     for (String fieldName : params.keySet()) {
/*  987 */       List mapList = new LinkedList();
/*      */ 
/*  989 */       Variable[] values = (Variable[])params.get(fieldName);
/*      */       Variable variable;
/*  990 */       for (variable : values)
/*      */       {
/*  992 */         if (result.size() < 1) {
/*  993 */           HashMap map = new HashMap();
/*  994 */           map.put(fieldName, variable);
/*      */ 
/*  996 */           mapList.add(map);
/*      */         }
/*      */         else {
/*  999 */           for (HashMap parameters : result) {
/* 1000 */             HashMap map = new HashMap();
/* 1001 */             map.putAll(parameters);
/*      */ 
/* 1003 */             map.put(fieldName, variable);
/*      */ 
/* 1005 */             mapList.add(map);
/*      */           }
/*      */         }
/*      */       }
/*      */ 
/* 1010 */       result = mapList;
/*      */     }
/*      */ 
/* 1013 */     return result;
/*      */   }
/*      */ 
/*      */   private JForexPeriod getDefaultJForexPeriod()
/*      */   {
/* 1018 */     JForexPeriod jForexPeriod = new JForexPeriod();
/*      */ 
/* 1020 */     long HOURS = 3600000L;
/* 1021 */     long DAYS = HOURS * 24L;
/* 1022 */     long range = Math.abs(this.to - this.from);
/*      */ 
/* 1024 */     if (range < HOURS * 22L)
/*      */     {
/* 1026 */       jForexPeriod.setPeriod(Period.FIVE_MINS);
/* 1027 */       jForexPeriod.setDataType(DataType.TIME_PERIOD_AGGREGATION);
/*      */     }
/* 1029 */     else if (range < DAYS * 6L)
/*      */     {
/* 1031 */       jForexPeriod.setPeriod(Period.TEN_MINS);
/* 1032 */       jForexPeriod.setDataType(DataType.TIME_PERIOD_AGGREGATION);
/*      */     }
/* 1034 */     else if (range < DAYS * 25L)
/*      */     {
/* 1036 */       jForexPeriod.setPeriod(Period.THIRTY_MINS);
/* 1037 */       jForexPeriod.setDataType(DataType.TIME_PERIOD_AGGREGATION);
/*      */     }
/*      */     else
/*      */     {
/* 1041 */       jForexPeriod.setPeriod(Period.ONE_HOUR);
/* 1042 */       jForexPeriod.setDataType(DataType.TIME_PERIOD_AGGREGATION);
/*      */     }
/*      */ 
/* 1045 */     return jForexPeriod;
/*      */   }
/*      */ 
/*      */   private JForexPeriod getJForexPeriod() {
/* 1049 */     JForexPeriod jForexPeriod = this.testerParameters.getChartPeriod();
/*      */ 
/* 1051 */     if (jForexPeriod == null) {
/* 1052 */       jForexPeriod = getDefaultJForexPeriod();
/*      */     }
/*      */ 
/* 1055 */     return jForexPeriod;
/*      */   }
/*      */ 
/*      */   protected void openTesterTabs(String toolTipText, IOrdersProvider ordersProvider, Map<Instrument, TesterChartData> chartPanels, TesterParameters testerParameters) {
/*      */     try {
/* 1060 */       String cacheName = GreedContext.CLIENT_MODE != null ? GreedContext.CLIENT_MODE : "COMMON";
/*      */ 
/* 1062 */       this.testerFeedDataProvider = new TesterFeedDataProvider(cacheName, ordersProvider);
/* 1063 */       this.testerFeedDataProvider.setInstrumentsSubscribed(this.instruments);
/*      */     } catch (DataCacheException e) {
/* 1065 */       LOGGER.error(e.getMessage(), e);
/* 1066 */       return;
/*      */     }
/*      */ 
/* 1069 */     for (Instrument instrument : this.instruments) {
/* 1070 */       if (this.instrumentsForConversion.contains(instrument))
/*      */       {
/*      */         continue;
/*      */       }
/* 1074 */       if (testerParameters.isVisualModeEnabled()) {
/* 1075 */         TesterChartData chartData = new TesterChartData();
/* 1076 */         chartData.instrument = instrument;
/* 1077 */         chartData.jForexPeriod = getJForexPeriod();
/* 1078 */         chartData.offerSide = OfferSide.BID;
/* 1079 */         chartData.feedDataProvider = this.testerFeedDataProvider;
/* 1080 */         chartData.templateName = testerParameters.getChartTemplate();
/* 1081 */         chartPanels.put(instrument, chartData);
/*      */       }
/*      */     }
/*      */ 
/* 1085 */     this.testerPanel.openTesterTabs(chartPanels, toolTipText);
/*      */ 
/* 1088 */     for (Iterator iter = chartPanels.entrySet().iterator(); iter.hasNext(); ) {
/* 1089 */       Map.Entry en = (Map.Entry)iter.next();
/* 1090 */       int chartPanelId = ((TesterChartData)en.getValue()).chartPanelId;
/*      */ 
/* 1092 */       IUserInterface ui = (IUserInterface)GreedContext.get("iUserInterface");
/* 1093 */       if ((ui instanceof IGUIManagerImpl)) {
/* 1094 */         IGUIManagerImpl uiImpl = (IGUIManagerImpl)ui;
/* 1095 */         IChartTabsAndFramesController chartTabsAndFramesController = uiImpl.getiChartTabsAndFramesController();
/* 1096 */         if (chartTabsAndFramesController != null) {
/* 1097 */           ChartPanel chartPanel = chartTabsAndFramesController.getChartPanelByPanelId(chartPanelId);
/* 1098 */           if (chartPanel != null) {
/* 1099 */             ChartToolBar chartToolBar = chartPanel.getToolBar();
/* 1100 */             if (chartToolBar != null) {
/* 1101 */               JComboBox combo = chartToolBar.getComboBox(ChartToolBar.ComboBoxType.INSTRUMENTS);
/* 1102 */               if (combo != null) {
/* 1103 */                 combo.setEnabled(false);
/*      */               }
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 1112 */     IUserInterface ui = (IUserInterface)GreedContext.get("iUserInterface");
/* 1113 */     if ((ui != null) && (ui.getMainFrame() != null))
/* 1114 */       ui.getMainFrame().validate();
/*      */     else
/* 1116 */       LOGGER.error("Cannot get the MainFrame");
/*      */   }
/*      */ 
/*      */   protected void addTesterIndicators(Map<Instrument, TesterChartData> chartPanels, TesterParameters testerParameters, List<IIndicator> indicators, long initialTime, double initialDeposit, INotificationUtils notificationUtils)
/*      */   {
/* 1121 */     DDSChartsController chartsController = (DDSChartsController)GreedContext.get("chartsController");
/*      */ 
/* 1123 */     for (Map.Entry entry : chartPanels.entrySet())
/*      */     {
/* 1125 */       TesterChartData chartData = (TesterChartData)entry.getValue();
/* 1126 */       chartData.chart = chartsController.getIChartBy(Integer.valueOf(chartData.chartPanelId));
/*      */ 
/* 1128 */       if ((testerParameters.isShowEquityIndicator()) || (testerParameters.isShowBalanceIndicator()) || (testerParameters.isShowPlIndicator()))
/*      */       {
/* 1130 */         ITheme theme = ThemeManager.getTheme(chartsController.getTheme(chartData.chartPanelId));
/* 1131 */         if (testerParameters.isShowBalanceIndicator()) {
/* 1132 */           BalanceHistoricalTesterIndicator indicator = new BalanceHistoricalTesterIndicator(initialTime, initialDeposit);
/*      */ 
/* 1134 */           indicator.setInstrument((Instrument)entry.getKey());
/*      */ 
/* 1136 */           IndicatorContext ctx = IndicatorHelper.createIndicatorContext();
/* 1137 */           TesterIndicatorWrapper indicatorWrapper = new TesterIndicatorWrapper(indicator, ctx);
/*      */ 
/* 1139 */           indicatorWrapper.setChangeTreeSelection(false);
/*      */ 
/* 1141 */           indicatorWrapper.setLineWidth(0, 1);
/*      */ 
/* 1143 */           Color balanceColor = theme.getColor(ITheme.ChartElement.HT_BALANCE);
/* 1144 */           indicatorWrapper.setOutputColor(0, balanceColor);
/* 1145 */           indicatorWrapper.setOutputColor2(0, balanceColor);
/*      */ 
/* 1147 */           chartsController.addIndicator(Integer.valueOf(chartData.chartPanelId), indicatorWrapper);
/* 1148 */           indicators.add(indicator);
/*      */         }
/*      */ 
/* 1151 */         if (testerParameters.isShowEquityIndicator()) {
/* 1152 */           EquityHistoricalTesterIndicator indicator = new EquityHistoricalTesterIndicator(initialTime, initialDeposit);
/*      */ 
/* 1154 */           indicator.setInstrument((Instrument)entry.getKey());
/*      */ 
/* 1156 */           IndicatorContext ctx = IndicatorHelper.createIndicatorContext();
/* 1157 */           TesterIndicatorWrapper indicatorWrapper = new TesterIndicatorWrapper(indicator, ctx);
/*      */ 
/* 1159 */           indicatorWrapper.setChangeTreeSelection(false);
/*      */ 
/* 1161 */           indicatorWrapper.setLineWidth(0, 1);
/*      */ 
/* 1163 */           Color equityColor = theme.getColor(ITheme.ChartElement.HT_EQUITY);
/* 1164 */           indicatorWrapper.setOutputColor(0, equityColor);
/* 1165 */           indicatorWrapper.setOutputColor2(0, equityColor);
/*      */ 
/* 1167 */           chartsController.addIndicator(Integer.valueOf(chartData.chartPanelId), indicatorWrapper);
/* 1168 */           indicators.add(indicator);
/*      */         }
/*      */ 
/* 1171 */         if (testerParameters.isShowPlIndicator()) {
/* 1172 */           ProfLossHistoricalTesterIndicator indicator = new ProfLossHistoricalTesterIndicator(initialTime, initialDeposit);
/*      */ 
/* 1174 */           indicator.setInstrument((Instrument)entry.getKey());
/*      */ 
/* 1176 */           IndicatorContext ctx = IndicatorHelper.createIndicatorContext();
/* 1177 */           TesterIndicatorWrapper indicatorWrapper = new TesterIndicatorWrapper(indicator, ctx);
/*      */ 
/* 1179 */           indicatorWrapper.setChangeTreeSelection(false);
/*      */ 
/* 1181 */           indicatorWrapper.setLineWidth(0, 1);
/*      */ 
/* 1183 */           Color plColor = theme.getColor(ITheme.ChartElement.HT_PROFIT_LOSS);
/* 1184 */           indicatorWrapper.setOutputColor(0, plColor);
/* 1185 */           indicatorWrapper.setOutputColor2(0, plColor);
/*      */ 
/* 1187 */           chartsController.addIndicator(Integer.valueOf(chartData.chartPanelId), indicatorWrapper);
/* 1188 */           indicators.add(indicator);
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   private boolean checkRange(long from, long to)
/*      */   {
/* 1196 */     if (to <= from) {
/* 1197 */       JOptionPane.showMessageDialog((JFrame)GreedContext.get("clientGui"), LocalizationManager.getText("joption.pane.end.day.should.be.after.start.day"), LocalizationManager.getText("joption.pane.wrong"), 1);
/*      */ 
/* 1203 */       return false;
/*      */     }
/*      */ 
/* 1206 */     return true;
/*      */   }
/*      */ 
/*      */   public void updateGuiBefore()
/*      */   {
/* 1211 */     this.singleTesterReport = null;
/*      */   }
/*      */ 
/*      */   public void updateGuiAfter()
/*      */   {
/* 1219 */     this.testerPanel.unlockGUI();
/* 1220 */     if (this.singleTesterReport != null)
/*      */       try {
/* 1222 */         File file = this.testerParameters.getReportFile();
/*      */ 
/* 1224 */         if (file != null)
/*      */         {
/* 1226 */           if (file.isDirectory()) {
/* 1227 */             SimpleDateFormat sdf = new SimpleDateFormat("ddMMyyyy_HHmmss");
/* 1228 */             String strategyName = this.strategyWrapper.getName().substring(0, this.strategyWrapper.getName().indexOf(46));
/* 1229 */             String fileName = strategyName + "_" + sdf.format(new Date(this.testStartDate)) + ".html";
/* 1230 */             file = new File(file, fileName);
/*      */           }
/*      */ 
/* 1233 */           StrategyReport.createReport(file, this.singleTesterReport, this.account.getCurrency(), this.testerParameters.isEventLogEnabled());
/*      */ 
/* 1235 */           if (this.testerParameters.isShowReport())
/* 1236 */             Desktop.getDesktop().browse(file.toURI());
/*      */         }
/*      */       }
/*      */       catch (IOException e) {
/* 1240 */         LOGGER.error(e.getMessage(), e);
/*      */       }
/*      */   }
/*      */ 
/*      */   public void breakRemainingPause()
/*      */   {
/* 1246 */     if (this.strategyRunner != null)
/* 1247 */       this.strategyRunner.breakRemainingPause();
/*      */   }
/*      */ 
/*      */   private static class OptimizerReportData extends TesterReportData
/*      */   {
/*      */     private OptimizerPanel optimizerPanel;
/*      */ 
/*      */     public OptimizerReportData(String strategyName, long from, long to, double deposit, List<String[]> parameterValues, OptimizerPanel optimizerPanel)
/*      */     {
/* 1306 */       super(from, to, deposit, parameterValues);
/* 1307 */       setFinishDeposit(deposit);
/* 1308 */       this.optimizerPanel = optimizerPanel;
/*      */     }
/*      */ 
/*      */     public void addCommission(double value)
/*      */     {
/* 1313 */       super.addCommission(value);
/* 1314 */       fireDataUpdated(this);
/*      */     }
/*      */ 
/*      */     public void addEvent(TesterReportData.TesterEvent event)
/*      */     {
/* 1319 */       super.addEvent(event);
/* 1320 */       fireDataUpdated(this);
/*      */     }
/*      */ 
/*      */     public void addTurnover(double value)
/*      */     {
/* 1325 */       super.addTurnover(value);
/* 1326 */       fireDataUpdated(this);
/*      */     }
/*      */ 
/*      */     public void setFinishDeposit(double value)
/*      */     {
/* 1331 */       super.setFinishDeposit(value);
/* 1332 */       fireDataUpdated(this);
/*      */     }
/*      */ 
/*      */     public void setLastTick(Instrument instr, ITick tick)
/*      */     {
/* 1337 */       super.setLastTick(instr, tick);
/*      */     }
/*      */ 
/*      */     protected void fireDataUpdated(TesterReportData report) {
/* 1341 */       if (this.optimizerPanel != null)
/* 1342 */         this.optimizerPanel.fireDataUpdated(report);
/*      */     }
/*      */   }
/*      */ 
/*      */   private static class OptimizerAccount extends TesterAccount
/*      */   {
/*      */     private OptimizerPanel optimizerPanel;
/* 1253 */     private long previousTimeStamp = 0L;
/* 1254 */     private final int refreshingUIInterval = 200;
/*      */     private ExecutionControl executionControl;
/*      */ 
/*      */     protected OptimizerAccount(TesterAccount parent, OptimizerPanel optimizerPanel, ExecutionControl executionControl)
/*      */     {
/* 1258 */       super();
/* 1259 */       this.optimizerPanel = optimizerPanel;
/* 1260 */       this.executionControl = executionControl;
/*      */ 
/* 1262 */       this.executionControl.addExecutionControlListener(new ExecutionControlListener(optimizerPanel)
/*      */       {
/*      */         public void stateChanged(ExecutionControlEvent event) {
/* 1265 */           if ((!event.getExecutionControl().isExecuting()) && 
/* 1266 */             (this.val$optimizerPanel != null))
/* 1267 */             this.val$optimizerPanel.fireDataUpdated(StrategyTesterAction.OptimizerAccount.this);
/*      */         }
/*      */ 
/*      */         public void speedChanged(ExecutionControlEvent event)
/*      */         {
/*      */         }
/*      */       });
/*      */     }
/*      */ 
/*      */     public void setDeposit(double deposit)
/*      */     {
/* 1279 */       super.setDeposit(deposit);
/* 1280 */       if (this.optimizerPanel != null)
/* 1281 */         this.optimizerPanel.fireDataUpdated(this);
/*      */     }
/*      */ 
/*      */     public void setEquity(double equity)
/*      */     {
/* 1287 */       super.setEquity(equity);
/* 1288 */       if (this.optimizerPanel != null)
/* 1289 */         if (this.previousTimeStamp != 0L) {
/* 1290 */           if (System.currentTimeMillis() - this.previousTimeStamp > 200L) {
/* 1291 */             this.optimizerPanel.fireDataUpdated(this);
/* 1292 */             this.previousTimeStamp = System.currentTimeMillis();
/*      */           }
/*      */         }
/* 1295 */         else this.previousTimeStamp = System.currentTimeMillis();
/*      */     }
/*      */   }
/*      */ 
/*      */   private static class CloseStreamEvent extends PostMessageAction
/*      */   {
/*      */     private FileOutputStream stream;
/*      */ 
/*      */     CloseStreamEvent(Object source, FileOutputStream stream)
/*      */     {
/*  291 */       super(null);
/*  292 */       this.stream = stream;
/*      */     }
/*      */ 
/*      */     public void doAction()
/*      */     {
/*      */       try {
/*  298 */         this.stream.flush();
/*      */       } catch (IOException ex) {
/*  300 */         StrategyTesterAction.LOGGER.error("Error saving messages to file.", ex);
/*      */       }
/*      */       try {
/*  303 */         this.stream.close();
/*      */       } catch (IOException ex) {
/*  305 */         StrategyTesterAction.LOGGER.error("Error closing messages file.", ex);
/*      */       }
/*      */     }
/*      */ 
/*      */     public void updateGuiAfter()
/*      */     {
/*      */     }
/*      */ 
/*      */     public void updateGuiBefore()
/*      */     {
/*      */     }
/*      */   }
/*      */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.actions.StrategyTesterAction
 * JD-Core Version:    0.6.0
 */