/*     */ package com.dukascopy.dds2.greed.gui.component.strategy;
/*     */ 
/*     */ import com.dukascopy.api.DataType;
/*     */ import com.dukascopy.api.Instrument;
/*     */ import com.dukascopy.api.Period;
/*     */ import com.dukascopy.api.system.Commissions;
/*     */ import com.dukascopy.api.system.Overnights;
/*     */ import com.dukascopy.charts.data.datacache.JForexPeriod;
/*     */ import com.dukascopy.charts.persistence.StrategyTestBean;
/*     */ import com.dukascopy.dds2.greed.gui.l10n.LocalizationManager;
/*     */ import com.dukascopy.dds2.greed.util.AbstractCurrencyConverter;
/*     */ import java.io.File;
/*     */ import java.text.MessageFormat;
/*     */ import java.util.Currency;
/*     */ import java.util.Date;
/*     */ import java.util.LinkedHashSet;
/*     */ import java.util.Set;
/*     */ import javax.swing.JOptionPane;
/*     */ import org.slf4j.Logger;
/*     */ import org.slf4j.LoggerFactory;
/*     */ 
/*     */ public class TesterParameters
/*     */ {
/*  38 */   private static final Logger LOGGER = LoggerFactory.getLogger(TesterParameters.class);
/*     */ 
/*  40 */   private Set<Instrument> instruments = new LinkedHashSet();
/*  41 */   private boolean optimizationMode = false;
/*  42 */   private boolean visualModeEnabled = false;
/*  43 */   private boolean processingStatsEnabled = true;
/*  44 */   private boolean eventLogEnabled = true;
/*  45 */   private TesterTimeRange testerTimeRange = TesterTimeRange.LAST_WEEK;
/*     */ 
/*  48 */   private boolean showEquityIndicator = true;
/*  49 */   private boolean showPlIndicator = true;
/*  50 */   private boolean showBalanceIndicator = false;
/*     */   private String chartTemplate;
/*  52 */   private JForexPeriod chartPeriod = new JForexPeriod(DataType.TIME_PERIOD_AGGREGATION, Period.ONE_MIN);
/*     */ 
/*  55 */   private boolean showReport = true;
/*  56 */   private boolean saveReportFile = false;
/*  57 */   private File reportFile = null;
/*  58 */   private File resultingReportFile = null;
/*     */ 
/*  61 */   private boolean printMessagesToConsole = false;
/*  62 */   private boolean saveMessages = false;
/*  63 */   private File messagesFile = null;
/*  64 */   private File resultingMessagesFile = null;
/*  65 */   private boolean appendMessages = false;
/*     */   public static final int MINIMAL_LEVERAGE = 1;
/*     */   public static final int MINIMAL_MC = 1;
/*     */   public static final double MINIMAL_EQUITY = 0.0D;
/*     */   public static final double MINIMAL_DEPOSIT = 0.0D;
/*  74 */   private double initialDeposit = 50000.0D;
/*  75 */   private int maxLeverage = 100;
/*  76 */   private int mcLeverage = 200;
/*  77 */   private double mcEquity = 0.0D;
/*  78 */   private Commissions commissions = new Commissions(false);
/*  79 */   private Overnights overnights = new Overnights(false);
/*  80 */   private int mcWeekendLeverage = 130;
/*  81 */   private Currency accountCurrency = Instrument.EURUSD.getSecondaryCurrency();
/*     */   private StrategyTestPanel testerPanel;
/*  84 */   private int lastSelectedOptionOnMessagesDialog = 0;
/*  85 */   private int lastSelectedOptionOnReportDialog = 0;
/*     */ 
/*     */   public TesterParameters() {
/*  88 */     this.testerTimeRange.recalculateTimeRange();
/*     */   }
/*     */ 
/*     */   public Set<Instrument> getInstruments() {
/*  92 */     return this.instruments;
/*     */   }
/*     */ 
/*     */   public void setInstruments(Set<Instrument> instruments) {
/*  96 */     this.instruments = instruments;
/*     */   }
/*     */ 
/*     */   public boolean isShowReport() {
/* 100 */     return this.showReport;
/*     */   }
/*     */ 
/*     */   public void setShowReport(boolean showReport) {
/* 104 */     this.showReport = showReport;
/*     */   }
/*     */ 
/*     */   public boolean isSaveReportFile() {
/* 108 */     return this.saveReportFile;
/*     */   }
/*     */ 
/*     */   public void setSaveReportFile(boolean saveReportFile) {
/* 112 */     this.saveReportFile = saveReportFile;
/*     */   }
/*     */ 
/*     */   public boolean isPrintMessagesToConsole() {
/* 116 */     return this.printMessagesToConsole;
/*     */   }
/*     */ 
/*     */   public void setPrintMessagesToConsole(boolean printMessagesToConsole) {
/* 120 */     this.printMessagesToConsole = printMessagesToConsole;
/*     */   }
/*     */ 
/*     */   public boolean isSaveMessages() {
/* 124 */     return this.saveMessages;
/*     */   }
/*     */ 
/*     */   public void setSaveMessages(boolean saveMessages) {
/* 128 */     this.saveMessages = saveMessages;
/*     */   }
/*     */ 
/*     */   public boolean isOptimizationMode() {
/* 132 */     return this.optimizationMode;
/*     */   }
/*     */ 
/*     */   public void setOptimizationMode(boolean optimizationMode) {
/* 136 */     this.optimizationMode = optimizationMode;
/*     */   }
/*     */ 
/*     */   public boolean isVisualModeEnabled() {
/* 140 */     return this.visualModeEnabled;
/*     */   }
/*     */ 
/*     */   public void setVisualModeEnabled(boolean visualModeEnabled) {
/* 144 */     this.visualModeEnabled = visualModeEnabled;
/*     */   }
/*     */ 
/*     */   public boolean isProcessingStatsEnabled() {
/* 148 */     return this.processingStatsEnabled;
/*     */   }
/*     */ 
/*     */   public void setProcessingStatsEnabled(boolean processingStatsEnabled) {
/* 152 */     this.processingStatsEnabled = processingStatsEnabled;
/*     */   }
/*     */ 
/*     */   public boolean isEventLogEnabled() {
/* 156 */     return this.eventLogEnabled;
/*     */   }
/*     */ 
/*     */   public void setEventLogEnabled(boolean eventLogEnabled) {
/* 160 */     this.eventLogEnabled = eventLogEnabled;
/*     */   }
/*     */ 
/*     */   public TesterTimeRange getTesterTimeRange() {
/* 164 */     return this.testerTimeRange;
/*     */   }
/*     */ 
/*     */   public void setTesterTimeRange(TesterTimeRange testerTimeRange) {
/* 168 */     this.testerTimeRange = testerTimeRange;
/* 169 */     if (this.testerTimeRange != null)
/* 170 */       this.testerTimeRange.recalculateTimeRange();
/*     */   }
/*     */ 
/*     */   public File getReportFile()
/*     */   {
/* 175 */     return this.reportFile;
/*     */   }
/*     */ 
/*     */   public void setReportFile(File reportFile) {
/* 179 */     this.reportFile = reportFile;
/*     */   }
/*     */ 
/*     */   public File getMessagesFile() {
/* 183 */     return this.messagesFile;
/*     */   }
/*     */ 
/*     */   public void setMessagesFile(File messagesFile) {
/* 187 */     this.messagesFile = messagesFile;
/*     */   }
/*     */ 
/*     */   public boolean isAppendMessages() {
/* 191 */     return this.appendMessages;
/*     */   }
/*     */ 
/*     */   public void setAppendMessages(boolean appendMessages) {
/* 195 */     this.appendMessages = appendMessages;
/*     */   }
/*     */ 
/*     */   public File getResultingReportFile() {
/* 199 */     return this.resultingReportFile;
/*     */   }
/*     */ 
/*     */   public void setResultingReportFile(File resultingReportFile) {
/* 203 */     this.resultingReportFile = resultingReportFile;
/*     */   }
/*     */ 
/*     */   public File getResultingMessagesFile() {
/* 207 */     return this.resultingMessagesFile;
/*     */   }
/*     */ 
/*     */   public double getInitialDeposit() {
/* 211 */     return this.initialDeposit;
/*     */   }
/*     */ 
/*     */   public void setInitialDeposit(double initialDeposit) {
/* 215 */     this.initialDeposit = initialDeposit;
/*     */   }
/*     */ 
/*     */   public int getMaxLeverage() {
/* 219 */     return this.maxLeverage;
/*     */   }
/*     */ 
/*     */   public void setMaxLeverage(int maxLeverage) {
/* 223 */     this.maxLeverage = maxLeverage;
/*     */   }
/*     */ 
/*     */   public int getMcLeverage() {
/* 227 */     return this.mcLeverage;
/*     */   }
/*     */ 
/*     */   public void setMcLeverage(int mcLeverage) {
/* 231 */     this.mcLeverage = mcLeverage;
/*     */   }
/*     */ 
/*     */   public double getMcEquity() {
/* 235 */     return this.mcEquity;
/*     */   }
/*     */ 
/*     */   public void setMcEquity(double mcEquity) {
/* 239 */     this.mcEquity = mcEquity;
/*     */   }
/*     */ 
/*     */   public Commissions getCommissions() {
/* 243 */     return this.commissions;
/*     */   }
/*     */ 
/*     */   public void setCommissions(Commissions commissions) {
/* 247 */     this.commissions = commissions;
/*     */   }
/*     */ 
/*     */   public Overnights getOvernights() {
/* 251 */     return this.overnights;
/*     */   }
/*     */ 
/*     */   public void setOvernights(Overnights overnights) {
/* 255 */     this.overnights = overnights;
/*     */   }
/*     */ 
/*     */   public int getMcWeekendLeverage() {
/* 259 */     return this.mcWeekendLeverage;
/*     */   }
/*     */ 
/*     */   public void setMcWeekendLeverage(int mcWeekendLeverage) {
/* 263 */     this.mcWeekendLeverage = mcWeekendLeverage;
/*     */   }
/*     */ 
/*     */   public Currency getAccountCurrency() {
/* 267 */     return this.accountCurrency;
/*     */   }
/*     */ 
/*     */   public void setAccountCurrency(Currency accountCurrency) {
/* 271 */     this.accountCurrency = accountCurrency;
/*     */   }
/*     */ 
/*     */   public StrategyTestPanel getTesterPanel() {
/* 275 */     return this.testerPanel;
/*     */   }
/*     */ 
/*     */   public void setTesterPanel(StrategyTestPanel testerPanel) {
/* 279 */     this.testerPanel = testerPanel;
/*     */   }
/*     */ 
/*     */   public boolean isShowEquityIndicator() {
/* 283 */     return this.showEquityIndicator;
/*     */   }
/*     */   public void setShowEquityIndicator(boolean showEquityIndicator) {
/* 286 */     this.showEquityIndicator = showEquityIndicator;
/*     */   }
/*     */   public boolean isShowPlIndicator() {
/* 289 */     return this.showPlIndicator;
/*     */   }
/*     */   public void setShowPlIndicator(boolean showPlIndicator) {
/* 292 */     this.showPlIndicator = showPlIndicator;
/*     */   }
/*     */   public boolean isShowBalanceIndicator() {
/* 295 */     return this.showBalanceIndicator;
/*     */   }
/*     */   public void setShowBalanceIndicator(boolean showBalanceIndicator) {
/* 298 */     this.showBalanceIndicator = showBalanceIndicator;
/*     */   }
/*     */   public String getChartTemplate() {
/* 301 */     return this.chartTemplate;
/*     */   }
/*     */   public void setChartTemplate(String chartTemplate) {
/* 304 */     this.chartTemplate = chartTemplate;
/*     */   }
/*     */   public JForexPeriod getChartPeriod() {
/* 307 */     return this.chartPeriod;
/*     */   }
/*     */   public void setChartPeriod(JForexPeriod chartPeriod) {
/* 310 */     this.chartPeriod = chartPeriod;
/*     */   }
/*     */ 
/*     */   public boolean isAnyIndicatorShown() {
/* 314 */     boolean anyIndicatorShown = false;
/*     */ 
/* 316 */     if ((this.instruments != null) && (this.instruments.size() > 0)) {
/* 317 */       anyIndicatorShown = (isVisualModeEnabled()) && ((isShowEquityIndicator()) || (isShowBalanceIndicator()) || (isShowPlIndicator()));
/*     */     }
/*     */ 
/* 320 */     return anyIndicatorShown;
/*     */   }
/*     */ 
/*     */   public boolean validateReportFile()
/*     */   {
/* 328 */     if (this.saveReportFile) {
/* 329 */       File reportFile = getReportFile();
/* 330 */       if (reportFile == null) {
/* 331 */         JOptionPane.showMessageDialog(this.testerPanel, LocalizationManager.getText("tester.message.invalid.report.file"), LocalizationManager.getText("joption.pane.historical.tester"), 1);
/*     */ 
/* 335 */         return false;
/*     */       }
/* 337 */       if (reportFile.exists()) {
/* 338 */         if (!reportFile.isDirectory())
/*     */         {
/* 341 */           String message = MessageFormat.format(LocalizationManager.getText("tester.message.overwrite.report.file"), new Object[] { reportFile.getName() });
/*     */ 
/* 343 */           Object[] options = { LocalizationManager.getText("button.save.option.overwrite"), LocalizationManager.getText("button.cancel") };
/*     */ 
/* 345 */           int option = JOptionPane.showOptionDialog(this.testerPanel, message, LocalizationManager.getText("joption.pane.historical.tester"), 0, 3, null, options, options[this.lastSelectedOptionOnReportDialog]);
/*     */ 
/* 347 */           if (option == 0)
/*     */           {
/* 349 */             this.lastSelectedOptionOnReportDialog = option;
/*     */           }
/*     */           else {
/* 352 */             return false;
/*     */           }
/*     */         }
/*     */ 
/*     */       }
/* 357 */       else if (reportFile.getName().endsWith(".html")) {
/* 358 */         if (!reportFile.getParentFile().exists())
/*     */         {
/* 360 */           if (!reportFile.getParentFile().mkdirs()) {
/* 361 */             String title = LocalizationManager.getText("joption.pane.historical.tester");
/*     */ 
/* 363 */             String pattern = LocalizationManager.getText("tester.message.invalid.report.file.path");
/* 364 */             String message = MessageFormat.format(pattern, new Object[] { reportFile.getAbsolutePath() });
/* 365 */             JOptionPane.showMessageDialog(this.testerPanel, message, title, 1);
/*     */ 
/* 367 */             return false;
/*     */           }
/*     */         }
/*     */       }
/*     */       else try {
/* 372 */           if (!reportFile.mkdirs()) {
/* 373 */             String title = LocalizationManager.getText("joption.pane.historical.tester");
/*     */ 
/* 375 */             String pattern = LocalizationManager.getText("tester.message.invalid.report.file.path");
/* 376 */             String message = MessageFormat.format(pattern, new Object[] { reportFile.getAbsolutePath() });
/* 377 */             JOptionPane.showMessageDialog(this.testerPanel, message, title, 1);
/*     */ 
/* 379 */             return false;
/*     */           }
/*     */         } catch (Exception ex) {
/* 382 */           LOGGER.warn(ex.getMessage());
/* 383 */           return false;
/*     */         }
/*     */ 
/*     */ 
/*     */     }
/*     */     else
/*     */     {
/* 390 */       String tempFolder = System.getProperty("java.io.tmpdir");
/*     */ 
/* 392 */       if (tempFolder != null) {
/* 393 */         File reportFile = new File(tempFolder);
/* 394 */         if (!reportFile.exists())
/*     */         {
/* 396 */           String pattern = LocalizationManager.getText("tester.message.invalid.report.file.path");
/* 397 */           String message = MessageFormat.format(pattern, new Object[] { reportFile.getAbsolutePath() });
/* 398 */           JOptionPane.showMessageDialog(this.testerPanel, message, LocalizationManager.getText("joption.pane.historical.tester"), 1);
/*     */ 
/* 402 */           return false;
/*     */         }
/* 404 */         setReportFile(reportFile);
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 409 */     return true;
/*     */   }
/*     */ 
/*     */   public boolean validateMessagesFile()
/*     */   {
/* 420 */     boolean appendMessages = false;
/*     */     File messagesFile;
/* 422 */     if (this.saveMessages) {
/* 423 */       File messagesFile = getMessagesFile();
/* 424 */       if (messagesFile == null) {
/* 425 */         JOptionPane.showMessageDialog(this.testerPanel, LocalizationManager.getText("joption.pane.invalid.file.name"), LocalizationManager.getText("joption.pane.historical.tester"), 1);
/* 426 */         return false;
/*     */       }
/*     */ 
/* 429 */       if (messagesFile.exists())
/*     */       {
/* 431 */         if (!messagesFile.isDirectory())
/*     */         {
/* 434 */           String message = MessageFormat.format(LocalizationManager.getText("label.append.overwrite.confirmation"), new Object[] { messagesFile.getName() });
/* 435 */           Object[] options = { LocalizationManager.getText("button.save.option.append"), LocalizationManager.getText("button.save.option.overwrite"), LocalizationManager.getText("button.cancel") };
/*     */ 
/* 441 */           int option = JOptionPane.showOptionDialog(this.testerPanel, message, LocalizationManager.getText("joption.pane.historical.tester"), 0, 3, null, options, options[this.lastSelectedOptionOnMessagesDialog]);
/* 442 */           if (option == 0)
/*     */           {
/* 444 */             appendMessages = true;
/* 445 */             this.lastSelectedOptionOnMessagesDialog = option;
/* 446 */           } else if (option == 1)
/*     */           {
/* 448 */             appendMessages = false;
/* 449 */             this.lastSelectedOptionOnMessagesDialog = option;
/*     */           }
/*     */           else {
/* 452 */             return false;
/*     */           }
/*     */         }
/*     */       }
/*     */       else {
/* 457 */         appendMessages = false;
/*     */ 
/* 459 */         if (messagesFile.getName().endsWith(".csv")) {
/* 460 */           if (!messagesFile.getParentFile().exists())
/*     */           {
/* 462 */             if (!messagesFile.getParentFile().mkdirs()) {
/* 463 */               String pattern = LocalizationManager.getText("joption.pane.invalid.dir");
/* 464 */               String message = MessageFormat.format(pattern, new Object[] { messagesFile.getAbsolutePath() });
/* 465 */               String title = LocalizationManager.getText("joption.pane.historical.tester");
/* 466 */               JOptionPane.showMessageDialog(this.testerPanel, message, title, 1);
/*     */ 
/* 468 */               return false;
/*     */             }
/*     */           }
/*     */         }
/*     */         else try {
/* 473 */             if (!messagesFile.mkdirs()) {
/* 474 */               String pattern = LocalizationManager.getText("joption.pane.error.saving.messages");
/* 475 */               String message = MessageFormat.format(pattern, new Object[] { messagesFile.getAbsolutePath() });
/* 476 */               String title = LocalizationManager.getText("joption.pane.historical.tester");
/* 477 */               JOptionPane.showMessageDialog(this.testerPanel, message, title, 1);
/*     */ 
/* 479 */               return false;
/*     */             }
/*     */           } catch (Exception ex) {
/* 482 */             LOGGER.warn(ex.getMessage());
/* 483 */             return false;
/*     */           }
/*     */ 
/*     */       }
/*     */ 
/*     */     }
/*     */     else
/*     */     {
/* 491 */       appendMessages = false;
/* 492 */       messagesFile = null;
/*     */     }
/*     */ 
/* 495 */     this.resultingMessagesFile = messagesFile;
/* 496 */     this.appendMessages = appendMessages;
/* 497 */     return true;
/*     */   }
/*     */ 
/*     */   public void saveTesterAccountSettings(StrategyTestBean strategyTestBean) {
/* 501 */     strategyTestBean.setInitialDeposit(getInitialDeposit());
/* 502 */     strategyTestBean.setAccountCurrency(getAccountCurrency());
/* 503 */     strategyTestBean.setMaxLeverage(getMaxLeverage());
/* 504 */     strategyTestBean.setMcLeverage(getMcLeverage());
/* 505 */     strategyTestBean.setCommissions(getCommissions());
/* 506 */     strategyTestBean.setOvernights(getOvernights());
/* 507 */     strategyTestBean.setMcEquity(getMcEquity());
/*     */   }
/*     */ 
/*     */   public void restoreTesterAccountSettings(StrategyTestBean strategyTestBean) {
/* 511 */     String currencyCode = strategyTestBean.getAccountCurrency();
/* 512 */     if (currencyCode != null) {
/* 513 */       this.accountCurrency = AbstractCurrencyConverter.findMajor(currencyCode);
/*     */     }
/* 515 */     if (this.accountCurrency == null) {
/* 516 */       this.accountCurrency = Instrument.EURUSD.getSecondaryCurrency();
/*     */     }
/*     */ 
/* 519 */     this.initialDeposit = Math.max(strategyTestBean.getInitialDeposit(), 0.0D);
/* 520 */     this.maxLeverage = Math.max(strategyTestBean.getMaxLeverage(), 1);
/* 521 */     this.mcLeverage = Math.max(strategyTestBean.getMcLeverage(), 1);
/* 522 */     this.mcEquity = Math.max(strategyTestBean.getMcEquity(), 0.0D);
/* 523 */     if (strategyTestBean.getCommissions() != null) {
/* 524 */       this.commissions = strategyTestBean.getCommissions();
/*     */     }
/* 526 */     if (strategyTestBean.getOvernights() != null)
/* 527 */       this.overnights = strategyTestBean.getOvernights();
/*     */   }
/*     */ 
/*     */   public void saveSelectedInstruments(StrategyTestBean strategyTestBean)
/*     */   {
/* 533 */     Set selectedInstruments = getInstruments();
/*     */ 
/* 535 */     if ((selectedInstruments != null) && (selectedInstruments.size() > 0)) {
/* 536 */       strategyTestBean.setInstruments(selectedInstruments);
/*     */     } else {
/* 538 */       if (selectedInstruments == null) {
/* 539 */         selectedInstruments = new LinkedHashSet();
/*     */       }
/* 541 */       selectedInstruments.add(Instrument.EURUSD);
/* 542 */       strategyTestBean.setInstruments(selectedInstruments);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void restoreSelectedInstruments(StrategyTestBean strategyTestBean) {
/* 547 */     Set instrumentsToRestore = strategyTestBean.getInstruments();
/* 548 */     setInstruments(instrumentsToRestore);
/*     */   }
/*     */ 
/*     */   public void saveMessagesReportSettings(StrategyTestBean strategyTestBean)
/*     */   {
/* 553 */     strategyTestBean.setMessagesDisabled(!isPrintMessagesToConsole());
/* 554 */     strategyTestBean.setSaveMessagesToFile(!isSaveMessages());
/* 555 */     File messagesFile = getMessagesFile();
/* 556 */     if ((messagesFile != null) && (messagesFile.getAbsolutePath().trim().length() > 0)) {
/* 557 */       strategyTestBean.setMessagesFilePath(messagesFile.getAbsolutePath().trim());
/*     */     }
/*     */ 
/* 561 */     strategyTestBean.setReportsDisabled(!isShowReport());
/* 562 */     strategyTestBean.setSaveReportFile(isSaveReportFile());
/* 563 */     File reportFile = getReportFile();
/* 564 */     if ((reportFile != null) && (reportFile.getAbsolutePath().trim().length() > 0))
/* 565 */       strategyTestBean.setReportFilePath(reportFile.getAbsolutePath().trim());
/*     */   }
/*     */ 
/*     */   public void restoreMessagesReportSettings(StrategyTestBean strategyTestBean)
/*     */   {
/* 571 */     this.printMessagesToConsole = (!strategyTestBean.isMessagesDisabled());
/* 572 */     this.saveMessages = (!strategyTestBean.isSaveMessagesToFile());
/* 573 */     if ((strategyTestBean.getMessagesFilePath() != null) && (strategyTestBean.getMessagesFilePath().trim().length() > 0)) {
/* 574 */       this.messagesFile = new File(strategyTestBean.getMessagesFilePath().trim());
/*     */     }
/*     */     else {
/* 577 */       this.saveMessages = false;
/*     */     }
/*     */ 
/* 580 */     this.showReport = (!strategyTestBean.isReportsDisabled());
/*     */ 
/* 582 */     this.saveReportFile = strategyTestBean.isSaveReportFile();
/* 583 */     if ((strategyTestBean.getReportFilePath() != null) && (strategyTestBean.getReportFilePath().trim().length() > 0)) {
/* 584 */       this.reportFile = new File(strategyTestBean.getReportFilePath().trim());
/*     */     }
/*     */     else
/* 587 */       this.saveReportFile = false;
/*     */   }
/*     */ 
/*     */   public void saveSettings(StrategyTestBean strategyTestBean)
/*     */   {
/* 592 */     strategyTestBean.setVisualMode(isVisualModeEnabled());
/* 593 */     strategyTestBean.setOptimization(isOptimizationMode());
/*     */ 
/* 595 */     strategyTestBean.setShowEquity(isShowEquityIndicator());
/* 596 */     strategyTestBean.setShowProfitLoss(isShowPlIndicator());
/* 597 */     strategyTestBean.setShowBalance(isShowBalanceIndicator());
/*     */ 
/* 599 */     if (getChartPeriod() != null) {
/* 600 */       strategyTestBean.setChartPeriod(getChartPeriod());
/*     */     }
/* 602 */     if (getChartTemplate() != null) {
/* 603 */       strategyTestBean.setChartTemplate(getChartTemplate());
/*     */     }
/*     */ 
/* 606 */     if (getTesterTimeRange() != null) {
/* 607 */       strategyTestBean.setRange(getTesterTimeRange().name());
/* 608 */       strategyTestBean.setRangeSelected(getTesterTimeRange() != TesterTimeRange.CUSTOM_PERIOD_TEMPLATE);
/* 609 */       strategyTestBean.setFromDate(getTesterTimeRange().getDateFrom());
/* 610 */       strategyTestBean.setToDate(getTesterTimeRange().getDateTo());
/*     */     }
/*     */   }
/*     */ 
/*     */   public void restoreSettings(StrategyTestBean strategyTestBean) {
/* 615 */     setOptimizationMode(strategyTestBean.isOptimization());
/* 616 */     setVisualModeEnabled(strategyTestBean.isVisualMode());
/* 617 */     setShowEquityIndicator(strategyTestBean.isShowEquity());
/* 618 */     setShowPlIndicator(strategyTestBean.isShowProfitLoss());
/* 619 */     setShowBalanceIndicator(strategyTestBean.isShowBalance());
/*     */ 
/* 621 */     if (strategyTestBean.getChartPeriod() != null) {
/* 622 */       setChartPeriod(strategyTestBean.getChartPeriod());
/*     */     }
/*     */ 
/* 625 */     if (strategyTestBean.getChartTemplate() != null) {
/* 626 */       setChartTemplate(strategyTestBean.getChartTemplate());
/*     */     }
/*     */ 
/* 629 */     if ((strategyTestBean.getRange() != null) && (!strategyTestBean.getRange().isEmpty())) {
/* 630 */       setTesterTimeRange(TesterTimeRange.valueOf(strategyTestBean.getRange()));
/* 631 */       getTesterTimeRange().setDateFrom(new Date(strategyTestBean.getFromDate()));
/* 632 */       getTesterTimeRange().setDateTo(new Date(strategyTestBean.getToDate()));
/*     */     }
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.strategy.TesterParameters
 * JD-Core Version:    0.6.0
 */