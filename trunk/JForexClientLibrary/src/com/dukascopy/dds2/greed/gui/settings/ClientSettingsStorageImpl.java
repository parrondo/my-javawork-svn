/*      */ package com.dukascopy.dds2.greed.gui.settings;
/*      */ 
/*      */ import com.dukascopy.api.IChart.Type;
/*      */ import com.dukascopy.api.IChartObject;
/*      */ import com.dukascopy.api.INewsFilter;
/*      */ import com.dukascopy.api.INewsFilter.NewsSource;
/*      */ import com.dukascopy.api.Instrument;
/*      */ import com.dukascopy.api.impl.IndicatorWrapper;
/*      */ import com.dukascopy.charts.data.datacache.JForexPeriod;
/*      */ import com.dukascopy.charts.persistence.BottomPanelBean;
/*      */ import com.dukascopy.charts.persistence.ChartBean;
/*      */ import com.dukascopy.charts.persistence.CustomIndicatorBean;
/*      */ import com.dukascopy.charts.persistence.EnabledIndicatorBean;
/*      */ import com.dukascopy.charts.persistence.IChartClient;
/*      */ import com.dukascopy.charts.persistence.ITheme;
/*      */ import com.dukascopy.charts.persistence.IndicatorBean;
/*      */ import com.dukascopy.charts.persistence.LastUsedIndicatorBean;
/*      */ import com.dukascopy.charts.persistence.SettingsStorage;
/*      */ import com.dukascopy.charts.persistence.StrategyTestBean;
/*      */ import com.dukascopy.charts.settings.ChartSettings.Option;
/*      */ import com.dukascopy.dds2.greed.GreedContext;
/*      */ import com.dukascopy.dds2.greed.export.historicaldata.HistoricalDataManagerBean;
/*      */ import com.dukascopy.dds2.greed.gui.ClientForm;
/*      */ import com.dukascopy.dds2.greed.gui.ClientFormLayoutManager;
/*      */ import com.dukascopy.dds2.greed.gui.DealPanel;
/*      */ import com.dukascopy.dds2.greed.gui.InstrumentAvailabilityManager;
/*      */ import com.dukascopy.dds2.greed.gui.JForexClientFormLayoutManager;
/*      */ import com.dukascopy.dds2.greed.gui.component.ExpandableSplitPane;
/*      */ import com.dukascopy.dds2.greed.gui.component.chart.DockedUndockedFrame;
/*      */ import com.dukascopy.dds2.greed.gui.component.chart.FramesState;
/*      */ import com.dukascopy.dds2.greed.gui.component.chart.TabsAndFramesTabbedPane;
/*      */ import com.dukascopy.dds2.greed.gui.component.file.filter.XMLFileFilter;
/*      */ import com.dukascopy.dds2.greed.gui.component.moverview.config.MarketOverviewConfig;
/*      */ import com.dukascopy.dds2.greed.gui.component.strategy.tab.mediator.StrategyNewBean;
/*      */ import com.dukascopy.dds2.greed.gui.util.lotamount.LotAmountChanger;
/*      */ import com.dukascopy.dds2.greed.util.FilePathManager;
/*      */ import java.awt.Dimension;
/*      */ import java.awt.Point;
/*      */ import java.io.File;
/*      */ import java.io.FileInputStream;
/*      */ import java.io.FileNotFoundException;
/*      */ import java.io.FileOutputStream;
/*      */ import java.io.IOException;
/*      */ import java.io.OutputStream;
/*      */ import java.io.RandomAccessFile;
/*      */ import java.math.BigDecimal;
/*      */ import java.nio.channels.FileChannel;
/*      */ import java.nio.channels.FileLock;
/*      */ import java.util.ArrayList;
/*      */ import java.util.HashSet;
/*      */ import java.util.List;
/*      */ import java.util.Map;
/*      */ import java.util.Set;
/*      */ import java.util.prefs.BackingStoreException;
/*      */ import java.util.prefs.InvalidPreferencesFormatException;
/*      */ import java.util.prefs.Preferences;
/*      */ import javax.swing.JFrame;
/*      */ import javax.swing.RowSorter.SortKey;
/*      */ import javax.swing.table.TableColumnModel;
/*      */ import org.slf4j.Logger;
/*      */ import org.slf4j.LoggerFactory;
/*      */ 
/*      */ public class ClientSettingsStorageImpl
/*      */   implements SettingsStorage, ClientSettingsStorage, IChartClient
/*      */ {
/*   66 */   private static Logger LOGGER = LoggerFactory.getLogger(ClientSettingsStorageImpl.class);
/*      */   private PreferencesStorage preferencesStorage;
/*   69 */   private boolean isUserFirstLoading = false;
/*      */ 
/*      */   public ClientSettingsStorageImpl()
/*      */   {
/*   74 */     this.preferencesStorage = new PreferencesStorage();
/*   75 */     initUserWorkspaceSettings();
/*      */   }
/*      */ 
/*      */   public static void importSystemPrefs() {
/*   79 */     String systemSettingsFolderPath = System.getProperty("jnlp.client.settings");
/*   80 */     if ((systemSettingsFolderPath == null) || (systemSettingsFolderPath.length() == 0) || (!FilePathManager.getInstance().isFolderAccessible(systemSettingsFolderPath))) {
/*   81 */       systemSettingsFolderPath = FilePathManager.getInstance().getDefaultSystemSettingsFolderPath();
/*      */ 
/*   83 */       if (!FilePathManager.getInstance().isFolderAccessible(systemSettingsFolderPath))
/*      */       {
/*   84 */         systemSettingsFolderPath = FilePathManager.getInstance().getAlternativeSystemSettingsFolderPath();
/*   85 */         if (FilePathManager.getInstance().isFolderAccessible(systemSettingsFolderPath));
/*      */       }
/*      */     }
/*   91 */     FilePathManager.getInstance().setSystemSettingsFolderPath(systemSettingsFolderPath);
/*      */ 
/*   93 */     importSystemPrefs(systemSettingsFolderPath, FilePathManager.getInstance().SYSTEM_SETTING_FILE_NAME);
/*      */   }
/*      */ 
/*      */   private static void importSystemPrefs(String systemSettingsFolderPath, String systemSettingsFilePath) {
/*   97 */     FileInputStream is = null;
/*      */     try {
/*   99 */       File systemSettingsSubfolder = new File(systemSettingsFolderPath);
/*  100 */       if (!systemSettingsSubfolder.exists()) {
/*  101 */         systemSettingsSubfolder.mkdirs();
/*      */       }
/*  103 */       File systemFile = new File(systemSettingsFolderPath + File.separator + systemSettingsFilePath);
/*  104 */       is = new FileInputStream(systemFile);
/*  105 */       Preferences.importPreferences(is);
/*      */     } catch (FileNotFoundException e) {
/*  107 */       LOGGER.info("System workspace file doesn't exists. Creating a new one.");
/*      */     } catch (IOException e) {
/*  109 */       LOGGER.error("Failed to load workspace settings from file: " + systemSettingsFilePath, e);
/*      */     } catch (InvalidPreferencesFormatException e) {
/*  111 */       LOGGER.error("Failed to retrieve workspace settings...", e);
/*      */     } finally {
/*  113 */       closeInputStream(is);
/*      */     }
/*      */   }
/*      */ 
/*      */   private void initUserWorkspaceSettings() {
/*  118 */     String userId = this.preferencesStorage.getUserAccountId((String)GreedContext.getConfig("account_name"));
/*  119 */     String userWorkspaceSettingsFilePath = this.preferencesStorage.getUserWorkspaceSettingsFilePath(userId);
/*  120 */     if (!FilePathManager.getInstance().isFileFolderAccessible(userWorkspaceSettingsFilePath)) {
/*  121 */       userWorkspaceSettingsFilePath = FilePathManager.getInstance().getAlternativeDefaultWorkspaceSettingsFilePath(this.preferencesStorage.getClientMode(), this.preferencesStorage.getUserAccountId(userId));
/*  122 */       if (!FilePathManager.getInstance().isFileFolderAccessible(userWorkspaceSettingsFilePath));
/*  125 */       this.preferencesStorage.saveUserWorkspaceSettingsFilePath(userId, userWorkspaceSettingsFilePath);
/*      */     }
/*      */ 
/*  128 */     String savedFilePath = this.preferencesStorage.getUserWorkspaceSettingsFilePath(userId);
/*  129 */     if ((savedFilePath == null) || (!new File(savedFilePath).exists())) {
/*  130 */       File userWorkspaceSettingsFile = new File(userWorkspaceSettingsFilePath);
/*  131 */       savedFilePath = userWorkspaceSettingsFile.getParentFile().getAbsolutePath() + File.separator + FilePathManager.getInstance().DEFAULT_WORKSPACE_FILE_NAME;
/*  132 */       this.preferencesStorage.saveUserWorkspaceSettingsFilePath(userId, savedFilePath);
/*  133 */       this.isUserFirstLoading = true;
/*  134 */       File file = new File(savedFilePath);
/*  135 */       dropCreateFile(file);
/*      */     }
/*      */ 
/*  138 */     this.preferencesStorage.saveUserWorkspaceSettingsFilePath(userId, savedFilePath);
/*      */   }
/*      */ 
/*      */   private void dropFile(File file) {
/*  142 */     if (file.exists())
/*  143 */       file.delete();
/*      */   }
/*      */ 
/*      */   private void dropCreateFile(File file)
/*      */   {
/*      */     try {
/*  149 */       dropFile(file);
/*  150 */       file.createNewFile();
/*      */     } catch (IOException e) {
/*  152 */       LOGGER.error(e.getMessage());
/*      */     }
/*      */   }
/*      */ 
/*      */   public String getWorkspaceSettingsFileNameWithoutPrefix() {
/*  157 */     String userId = this.preferencesStorage.getUserAccountId((String)GreedContext.getConfig("account_name"));
/*      */ 
/*  159 */     String workspaceName = this.preferencesStorage.getUserWorkspaceSettingsFilePath(userId);
/*  160 */     int dotIndex = workspaceName.lastIndexOf(".");
/*  161 */     if (dotIndex > 0) {
/*  162 */       workspaceName = workspaceName.substring(0, dotIndex);
/*      */     }
/*  164 */     int separatorIndex = workspaceName.lastIndexOf(File.separator);
/*  165 */     workspaceName = workspaceName.substring(separatorIndex + 1, workspaceName.length());
/*      */ 
/*  167 */     if (workspaceName.length() > 0) {
/*  168 */       String firstLetter = workspaceName.substring(0, 1);
/*  169 */       String theRest = workspaceName.substring(1);
/*  170 */       return firstLetter.toUpperCase() + theRest;
/*      */     }
/*      */ 
/*  173 */     return workspaceName;
/*      */   }
/*      */ 
/*      */   public boolean restoreLogState()
/*      */   {
/*  178 */     return this.preferencesStorage.restoreLogState();
/*      */   }
/*      */ 
/*      */   public void saveLogState(boolean isEnabled) {
/*  182 */     this.preferencesStorage.saveLogState(isEnabled);
/*      */   }
/*      */ 
/*      */   public boolean restoreOrderValidationOn() {
/*  186 */     return this.preferencesStorage.restoreOrderValidationOn();
/*      */   }
/*      */ 
/*      */   public void saveOrderValidationOn(boolean isEnabled) {
/*  190 */     this.preferencesStorage.saveOrderValidationOn(isEnabled);
/*      */   }
/*      */ 
/*      */   public boolean restoreApplySlippageToAllMarketOrders() {
/*  194 */     return this.preferencesStorage.restoreApplySlippageToAllMarketOrders();
/*      */   }
/*      */ 
/*      */   public void saveApplySlippageToAllMarketOrders(boolean apply) {
/*  198 */     this.preferencesStorage.saveApplySlippageToAllMarketOrders(apply);
/*      */   }
/*      */ 
/*      */   public boolean restoreApplyStopLossToAllMarketOrders() {
/*  202 */     if (GreedContext.isContest()) return true;
/*  203 */     return this.preferencesStorage.restoreApplyStopLossToAllMarketOrders();
/*      */   }
/*      */ 
/*      */   public void saveApplyStopLossToAllMarketOrders(boolean apply) {
/*  207 */     this.preferencesStorage.saveApplyStopLossToAllMarketOrders(apply);
/*      */   }
/*      */ 
/*      */   public boolean restoreApplyTakeProfitToAllMarketOrders()
/*      */   {
/*  212 */     if (GreedContext.isContest()) return true;
/*  213 */     return this.preferencesStorage.restoreApplyTakeProfitToAllMarketOrders();
/*      */   }
/*      */ 
/*      */   public void saveApplyTakeProfitToAllMarketOrders(boolean apply)
/*      */   {
/*  219 */     this.preferencesStorage.saveApplyTakeProfitToAllMarketOrders(apply);
/*      */   }
/*      */ 
/*      */   public boolean restoreApplyTimeValidationToAllMarketOrders()
/*      */   {
/*  224 */     return this.preferencesStorage.restoreApplyTimeValidationToAllMarketOrders();
/*      */   }
/*      */ 
/*      */   public void saveApplyTimeValidationToAllMarketOrders(boolean apply)
/*      */   {
/*  229 */     this.preferencesStorage.saveApplyTimeValidationToAllMarketOrders(apply);
/*      */   }
/*      */ 
/*      */   public boolean restoreFillOrKillOrders() {
/*  233 */     return this.preferencesStorage.restoreApplyFillOrKillOrders();
/*      */   }
/*      */ 
/*      */   public void saveFillOrKillOrders(boolean apply) {
/*  237 */     this.preferencesStorage.saveApplyFillOrKillOrders(apply);
/*      */   }
/*      */ 
/*      */   public void saveDefaultStrategyPath(String defaultPath)
/*      */   {
/*  242 */     this.preferencesStorage.saveDefaultStrategyPath(defaultPath);
/*      */   }
/*      */ 
/*      */   public void saveOneClickState(boolean enabled) {
/*  246 */     this.preferencesStorage.saveOneClickState(enabled);
/*      */   }
/*      */ 
/*      */   public void save(ClientForm clientForm) {
/*  250 */     this.preferencesStorage.save(clientForm);
/*      */   }
/*      */ 
/*      */   public void restore(ClientForm clientForm) {
/*  254 */     this.preferencesStorage.restore(clientForm);
/*      */   }
/*      */ 
/*      */   public void restore(DealPanel dealPanel) {
/*  258 */     this.preferencesStorage.restore(dealPanel);
/*      */   }
/*      */ 
/*      */   public void save(DealPanel dealPanel) {
/*  262 */     this.preferencesStorage.save(dealPanel);
/*      */   }
/*      */ 
/*      */   public boolean restoreChartsAlwaysOnTop() {
/*  266 */     return this.preferencesStorage.restoreChartsAlwaysOnTop();
/*      */   }
/*      */ 
/*      */   public void saveChartsAlwaysOnTop(boolean isEnabled) {
/*  270 */     this.preferencesStorage.saveChartsAlwaysOnTop(isEnabled);
/*      */   }
/*      */ 
/*      */   public void saveMOsize(Dimension size) {
/*  274 */     this.preferencesStorage.saveMOsize(size);
/*      */   }
/*      */ 
/*      */   public void saveMOlocation(Point location) {
/*  278 */     this.preferencesStorage.saveMOlocation(location);
/*      */   }
/*      */ 
/*      */   public void saveDefaultStopLossOffset(Float pipAmount) {
/*  282 */     this.preferencesStorage.saveDefaultStopLossOffset(pipAmount);
/*      */   }
/*      */ 
/*      */   public void saveDefaultOpenIfOffset(Float pipAmount) {
/*  286 */     this.preferencesStorage.saveDefaultOpenIfOffset(pipAmount);
/*      */   }
/*      */ 
/*      */   public void saveDefaultTakeProfitOffset(Float pipAmount) {
/*  290 */     this.preferencesStorage.saveDefaultTakeProfitOffset(pipAmount);
/*      */   }
/*      */ 
/*      */   public void saveDefaultAmount(Float defaultAmount) {
/*  294 */     this.preferencesStorage.saveDefaultAmount(defaultAmount);
/*      */   }
/*      */ 
/*      */   public void saveDefaultXAGAmount(Float defaultXAGAmount)
/*      */   {
/*  299 */     this.preferencesStorage.saveDefaultXAGAmount(defaultXAGAmount);
/*      */   }
/*      */ 
/*      */   public void saveDefaultXAUAmount(Float defaultXAUAmount)
/*      */   {
/*  304 */     this.preferencesStorage.saveDefaultXAUAmount(defaultXAUAmount);
/*      */   }
/*      */ 
/*      */   public void saveLotAmount(int lotAmount)
/*      */   {
/*  309 */     this.preferencesStorage.saveAmountLot(Integer.valueOf(lotAmount));
/*      */   }
/*      */ 
/*      */   public void saveDefaultSlippage(Float defaultSlippage) {
/*  313 */     this.preferencesStorage.saveDefaultSlippage(defaultSlippage);
/*      */   }
/*      */ 
/*      */   public void saveDefaultTrailingStep(Float defaultTrailingStep) {
/*  317 */     this.preferencesStorage.saveDefaultTrailingStep(defaultTrailingStep);
/*      */   }
/*      */ 
/*      */   public void saveStrategyDiscState(boolean isAccepted)
/*      */   {
/*  322 */     this.preferencesStorage.saveStartegiesDiscState(isAccepted);
/*      */   }
/*      */ 
/*      */   public void saveRemoteStrategyDiscState(boolean isAccepted)
/*      */   {
/*  327 */     this.preferencesStorage.saveRemoteStartegiesDiscState(isAccepted);
/*      */   }
/*      */ 
/*      */   public void saveOrderValidityTime(Float orderValidityTime)
/*      */   {
/*  332 */     this.preferencesStorage.saveOrderValidityTime(orderValidityTime);
/*      */   }
/*      */ 
/*      */   public void saveOredrValidityTimeUnit(int selectedIndex)
/*      */   {
/*  337 */     this.preferencesStorage.saveDefaultOrderValTimeUnit(Integer.valueOf(selectedIndex));
/*      */   }
/*      */ 
/*      */   public boolean restoreStrategyDiscState()
/*      */   {
/*  342 */     return this.preferencesStorage.restoreStartegiesDiscState();
/*      */   }
/*      */ 
/*      */   public boolean restoreRemoteStrategyDiscState()
/*      */   {
/*  347 */     return this.preferencesStorage.restoreRemoteStartegiesDiscState();
/*      */   }
/*      */ 
/*      */   public void saveFullAccessDiscState(boolean isAccepted)
/*      */   {
/*  352 */     this.preferencesStorage.saveFullAccessDiscState(isAccepted);
/*      */   }
/*      */ 
/*      */   public boolean restoreFullAccessDiscState()
/*      */   {
/*  357 */     return this.preferencesStorage.restoreFullAccessDiscState();
/*      */   }
/*      */ 
/*      */   public boolean restoreTesterDiscState()
/*      */   {
/*  362 */     return this.preferencesStorage.restoreTesterDiscState();
/*      */   }
/*      */ 
/*      */   public void saveTesterDiscState(boolean isAccepted)
/*      */   {
/*  367 */     this.preferencesStorage.saveTesterDiscState(isAccepted);
/*      */   }
/*      */ 
/*      */   public Dimension restoreMOsize() {
/*  371 */     return this.preferencesStorage.restoreMOsize();
/*      */   }
/*      */ 
/*      */   public Point restoreMOlocation() {
/*  375 */     return this.preferencesStorage.restoreMOlocation();
/*      */   }
/*      */ 
/*      */   public String restoreDefaultSlippageAsText() {
/*  379 */     return String.valueOf(restoreDefaultSlippage());
/*      */   }
/*      */ 
/*      */   public String restoreDefaultStopLossOffsetAsText() {
/*  383 */     return String.valueOf(restoreDefaultStopLossOffset());
/*      */   }
/*      */ 
/*      */   public String restoreDefaultOpenIfOffsetAsText() {
/*  387 */     return String.valueOf(restoreDefaultOpenIfOffset());
/*      */   }
/*      */ 
/*      */   public String restoreDefaultTakeProfitOffsetAsText() {
/*  391 */     return String.valueOf(restoreDefaultTakeProfitOffset());
/*      */   }
/*      */ 
/*      */   public String restoreDefaultAmountAsText() {
/*  395 */     return String.valueOf(restoreDefaultAmount());
/*      */   }
/*      */ 
/*      */   public BigDecimal restoreDefaultAmount() {
/*  399 */     return this.preferencesStorage.restoreDefaultAmount();
/*      */   }
/*      */ 
/*      */   public BigDecimal restoreDefaultXAGAmount()
/*      */   {
/*  404 */     return this.preferencesStorage.restoreDefaultXAGAmount();
/*      */   }
/*      */ 
/*      */   public BigDecimal restoreDefaultXAUAmount()
/*      */   {
/*  409 */     return this.preferencesStorage.restoreDefaultXAUAmount();
/*      */   }
/*      */ 
/*      */   public BigDecimal restoreDefaultAmountLot()
/*      */   {
/*  414 */     return BigDecimal.valueOf(ClientSettingsStorage.DEFAULT_AMOUNT_LOT_VALUE);
/*      */   }
/*      */ 
/*      */   public BigDecimal restoreAmountLot()
/*      */   {
/*  419 */     return this.preferencesStorage.restoreAmountLot();
/*      */   }
/*      */ 
/*      */   public BigDecimal restoreDefaultTrailingStep()
/*      */   {
/*  425 */     return this.preferencesStorage.restoreDefaultTrailingStep();
/*      */   }
/*      */ 
/*      */   public BigDecimal restoreOrderValidityTime()
/*      */   {
/*  430 */     return this.preferencesStorage.restoreOrderValidityTime();
/*      */   }
/*      */ 
/*      */   public int restoreOrderValidityTimeUnit()
/*      */   {
/*  435 */     return this.preferencesStorage.restoreDefaultOrderValTimeUnit().intValue();
/*      */   }
/*      */ 
/*      */   public BigDecimal restoreDefaultSlippage() {
/*  439 */     return this.preferencesStorage.restoreDefaultSlippage();
/*      */   }
/*      */ 
/*      */   public BigDecimal restoreDefaultStopLossOffset() {
/*  443 */     return this.preferencesStorage.restoreDefaultStopLossOffset();
/*      */   }
/*      */ 
/*      */   public BigDecimal restoreDefaultTakeProfitOffset() {
/*  447 */     return this.preferencesStorage.restoreDefaultTakeProfitOffset();
/*      */   }
/*      */ 
/*      */   public BigDecimal restoreDefaultOpenIfOffset() {
/*  451 */     return this.preferencesStorage.restoreDefaultOpenIfOffset();
/*      */   }
/*      */ 
/*      */   public boolean restoreOneClickState() {
/*  455 */     if (GreedContext.isContest()) return false;
/*  456 */     return this.preferencesStorage.restoreOneClickState();
/*      */   }
/*      */ 
/*      */   public void saveSpotAtMarket(boolean isVisible) {
/*  460 */     this.preferencesStorage.saveSpotAtMarket(isVisible);
/*      */   }
/*      */ 
/*      */   public boolean restoreSpotAtMarket() {
/*  464 */     return this.preferencesStorage.restoreSpotAtMarket();
/*      */   }
/*      */ 
/*      */   public void saveSplitPane(ExpandableSplitPane splitPane) {
/*  468 */     this.preferencesStorage.saveSplitPane(splitPane);
/*      */   }
/*      */ 
/*      */   public void restoreSplitPane(ExpandableSplitPane splitPane, double defaultLocation) {
/*  472 */     this.preferencesStorage.restoreSplitPane(splitPane, defaultLocation);
/*      */   }
/*      */ 
/*      */   public void saveInstrumenTabs(MarketOverviewConfig marketOverviewConfig, int selectedTab) {
/*  476 */     this.preferencesStorage.saveInstrumenTabs(marketOverviewConfig, selectedTab);
/*      */   }
/*      */ 
/*      */   public void clearInstrumentsTabs() {
/*  480 */     this.preferencesStorage.clearInstrumentsTabs();
/*      */   }
/*      */ 
/*      */   public void saveSelectedInstruments(Set<String> selectedInstruments) {
/*  484 */     this.preferencesStorage.saveSelectedInstruments(selectedInstruments);
/*      */   }
/*      */ 
/*      */   public List<String> restoreSelectedInstruments() {
/*  488 */     ArrayList instrumentsList = new ArrayList();
/*  489 */     Preferences node = this.preferencesStorage.getSelectedInstrumentsNode();
/*      */     try {
/*  491 */       String[] instruments = node.keys();
/*  492 */       for (String instrument : instruments) {
/*  493 */         if ((!node.getBoolean(instrument, true)) || 
/*  494 */           (instrument.indexOf(47) <= 0) || 
/*  495 */           (!InstrumentAvailabilityManager.getInstance().isAllowed(instrument))) continue;
/*  496 */         instrumentsList.add(instrument);
/*      */       }
/*      */     }
/*      */     catch (BackingStoreException e) {
/*  500 */       LOGGER.error(e.getMessage(), e);
/*      */     }
/*  502 */     return this.preferencesStorage.restoreSelectedInstruments(instrumentsList);
/*      */   }
/*      */ 
/*      */   public void saveLastSelectedInstrument()
/*      */   {
/*  507 */     this.preferencesStorage.saveLastSelectedInstrument(LotAmountChanger.getSelectedInstrument().toString());
/*      */   }
/*      */ 
/*      */   public String restoreLastSelectedInstrument()
/*      */   {
/*  512 */     return this.preferencesStorage.restoreLastSelectedInstrument();
/*      */   }
/*      */ 
/*      */   public void cleanAllSelectedInstruments() {
/*  516 */     this.preferencesStorage.cleanAllSelectedInstruments();
/*      */   }
/*      */ 
/*      */   public MarketOverviewConfig restoreInstrumentTabs() {
/*  520 */     MarketOverviewConfig marketOverviewConfig = MarketOverviewConfig.getConfig();
/*  521 */     return this.preferencesStorage.restoreInstrumentTabs(marketOverviewConfig);
/*      */   }
/*      */ 
/*      */   public int getSelectedInstrumentTabIndex() {
/*  525 */     return this.preferencesStorage.getSelectedInstrumentTabIndex();
/*      */   }
/*      */ 
/*      */   public void saveDetachedLocation(String instrument, Point location) {
/*  529 */     if (!restorePersistDetachedLocationState()) {
/*  530 */       return;
/*      */     }
/*  532 */     this.preferencesStorage.saveDetachedLocation(instrument, location);
/*      */   }
/*      */ 
/*      */   public Point restoreDetachedLocation(String instrument) {
/*  536 */     if (!restorePersistDetachedLocationState()) {
/*  537 */       return null;
/*      */     }
/*      */ 
/*  540 */     return this.preferencesStorage.restoreDetachedLocation(instrument);
/*      */   }
/*      */ 
/*      */   public void savePersistDetachedLocationState(boolean value) {
/*  544 */     this.preferencesStorage.savePersistDetachedLocationState(value);
/*      */   }
/*      */ 
/*      */   public boolean restorePersistDetachedLocationState() {
/*  548 */     return this.preferencesStorage.restorePersistDetachedLocationState();
/*      */   }
/*      */ 
/*      */   public void deleteAllSettings() {
/*  552 */     saveLocalCachePath(FilePathManager.getInstance().getDefaultCacheFolderPath());
/*  553 */     String userId = this.preferencesStorage.getUserAccountId((String)GreedContext.getConfig("account_name"));
/*  554 */     String workspaceFilePath = FilePathManager.getInstance().getDefaultWorkspaceSettingsFilePath(this.preferencesStorage.getClientMode(), userId);
/*  555 */     File file = new File(workspaceFilePath);
/*  556 */     dropFile(file);
/*  557 */     this.preferencesStorage.deleteAllSettings();
/*  558 */     saveSystemSettings();
/*      */   }
/*      */ 
/*      */   public String[] getDefaultInstrumentList() {
/*  562 */     return this.preferencesStorage.getDefaultInstrumentList();
/*      */   }
/*      */ 
/*      */   public void saveSelectedTheme(ITheme theme) {
/*  566 */     this.preferencesStorage.saveSelectedTheme(theme);
/*      */   }
/*      */ 
/*      */   public void removeAllThemes() {
/*  570 */     this.preferencesStorage.removeAllThemes();
/*      */   }
/*      */ 
/*      */   public void save(ITheme theme) {
/*  574 */     this.preferencesStorage.save(theme);
/*      */   }
/*      */ 
/*      */   public String restoreSelectedTheme() {
/*  578 */     return this.preferencesStorage.restoreSelectedTheme();
/*      */   }
/*      */ 
/*      */   public List<ITheme> loadThemes() {
/*  582 */     return this.preferencesStorage.loadThemes(null);
/*      */   }
/*      */ 
/*      */   public void clearChartSettings() {
/*  586 */     this.preferencesStorage.clearChartSettings();
/*      */   }
/*      */ 
/*      */   public void save(ChartSettings.Option option, String value) {
/*  590 */     this.preferencesStorage.save(option, value);
/*      */   }
/*      */ 
/*      */   public String load(ChartSettings.Option option) {
/*  594 */     return this.preferencesStorage.load(option);
/*      */   }
/*      */ 
/*      */   public void saveFrameDimension(ClientSettingsStorage.FrameType frameType, Dimension dimension) {
/*  598 */     this.preferencesStorage.saveFrameDimension(frameType, dimension);
/*      */   }
/*      */ 
/*      */   public Dimension restoreFrameDimension(ClientSettingsStorage.FrameType frameType) {
/*  602 */     return this.preferencesStorage.restoreFrameDimension(frameType);
/*      */   }
/*      */ 
/*      */   public List<ChartBean> getChartBeans() {
/*  606 */     return this.preferencesStorage.getChartBeans();
/*      */   }
/*      */ 
/*      */   public ChartBean getChartBean(int chartBeanId) {
/*  610 */     return this.preferencesStorage.getChartBean(chartBeanId);
/*      */   }
/*      */ 
/*      */   public void save(ChartBean chartBean) {
/*  614 */     this.preferencesStorage.saveChartBean(chartBean);
/*      */   }
/*      */ 
/*      */   public void remove(Integer id) {
/*  618 */     this.preferencesStorage.remove(id);
/*      */   }
/*      */ 
/*      */   public List<CustomIndicatorBean> getCustomIndicatorBeans()
/*      */   {
/*  638 */     return this.preferencesStorage.getCustomIndicatorBeans();
/*      */   }
/*      */ 
/*      */   public CustomIndicatorBean getCustomIndicatorBean(int id) {
/*  642 */     return this.preferencesStorage.getCustomIndicatorBean(id);
/*      */   }
/*      */ 
/*      */   public void save(CustomIndicatorBean customIndicatorBean) {
/*  646 */     this.preferencesStorage.save(customIndicatorBean);
/*      */   }
/*      */ 
/*      */   public void remove(CustomIndicatorBean customIndicatorBean) {
/*  650 */     this.preferencesStorage.remove(customIndicatorBean);
/*      */   }
/*      */ 
/*      */   public List<EnabledIndicatorBean> getEnabledIndicators() {
/*  654 */     return this.preferencesStorage.getEnabledIndicators();
/*      */   }
/*      */ 
/*      */   public void saveEnabledIndicator(EnabledIndicatorBean indicatorBean) {
/*  658 */     this.preferencesStorage.saveEnabledIndicator(indicatorBean);
/*      */   }
/*      */ 
/*      */   public void removeEnabledIndicator(EnabledIndicatorBean indicatorBean) {
/*  662 */     this.preferencesStorage.removeEnabledIndicator(indicatorBean);
/*      */   }
/*      */ 
/*      */   public List<IndicatorBean> getIndicatorsBeans(Integer chartId) {
/*  666 */     return this.preferencesStorage.getIndicatorBeans(chartId);
/*      */   }
/*      */ 
/*      */   public void saveIndicatorBean(Integer chartId, IndicatorBean indicatorBean) {
/*  670 */     this.preferencesStorage.saveIndicatorBean(chartId, indicatorBean);
/*      */   }
/*      */ 
/*      */   public void removeIndicator(Integer chartId, Integer indicatorId) {
/*  674 */     this.preferencesStorage.removeIndicator(chartId, indicatorId);
/*      */   }
/*      */ 
/*      */   public List<IChartObject> getDrawingsFor(Integer chartId) {
/*  678 */     return this.preferencesStorage.getDrawingsFor(chartId);
/*      */   }
/*      */ 
/*      */   public void save(Integer chartId, IChartObject chartObject) {
/*  682 */     this.preferencesStorage.saveChartObject(chartId, chartObject);
/*      */   }
/*      */ 
/*      */   public void removeDrawing(Integer chartId, IChartObject chartObject) {
/*  686 */     this.preferencesStorage.removeDrawing(chartId, chartObject);
/*      */   }
/*      */ 
/*      */   public List<IChartObject> getSubIndicatorDrawingsFor(int chartPanelId, int subIndicatorId) {
/*  690 */     return this.preferencesStorage.getSubIndicatorDrawingsFor(chartPanelId, subIndicatorId);
/*      */   }
/*      */ 
/*      */   public void save(int chartPanelId, int indicatorId, IChartObject drawing) {
/*  694 */     this.preferencesStorage.saveIndicatorChartObject(chartPanelId, indicatorId, drawing);
/*      */   }
/*      */ 
/*      */   public void removeDrawing(int chartPanelId, int indicatorId, IChartObject editedChartObject) {
/*  698 */     this.preferencesStorage.removeDrawing(chartPanelId, indicatorId, editedChartObject);
/*      */   }
/*      */ 
/*      */   public void save(BottomPanelBean bottomPanelBean) {
/*  702 */     this.preferencesStorage.save(bottomPanelBean);
/*      */   }
/*      */ 
/*      */   public void remove(BottomPanelBean bottomPanelBean) {
/*  706 */     this.preferencesStorage.remove(bottomPanelBean);
/*      */   }
/*      */ 
/*      */   public List<BottomPanelBean> getBottomPanelBeans() {
/*  710 */     return this.preferencesStorage.getBottomPanelBeans();
/*      */   }
/*      */ 
/*      */   public void setChartsExpanded(boolean isExpanded) {
/*  714 */     this.preferencesStorage.setChartsExpanded(isExpanded);
/*      */   }
/*      */ 
/*      */   public void setStrategiesExpanded(boolean isExpanded) {
/*  718 */     this.preferencesStorage.setStrategiesExpanded(isExpanded);
/*      */   }
/*      */ 
/*      */   public void setIndicatorsExpanded(boolean isExpanded) {
/*  722 */     this.preferencesStorage.setIndicatorsExpanded(isExpanded);
/*      */   }
/*      */ 
/*      */   public boolean isIndicatorsExpanded() {
/*  726 */     return this.preferencesStorage.isIndicatorsExpanded();
/*      */   }
/*      */ 
/*      */   public boolean isChartsExpanded() {
/*  730 */     return this.preferencesStorage.isChartsExpanded();
/*      */   }
/*      */ 
/*      */   public boolean isStrategiesExpanded() {
/*  734 */     return this.preferencesStorage.isStrategiesExpanded();
/*      */   }
/*      */ 
/*      */   public boolean isBodySplitExpanded() {
/*  738 */     return this.preferencesStorage.isBodySplitExpanded();
/*      */   }
/*      */ 
/*      */   public void setBodySplitExpanded(boolean value) {
/*  742 */     this.preferencesStorage.setBodySplitExpanded(value);
/*      */   }
/*      */ 
/*      */   public FramesState getFramesStateOf(Preferences nodes) {
/*  746 */     return this.preferencesStorage.getFramesStateOf(nodes);
/*      */   }
/*      */ 
/*      */   public void saveFramesState(FramesState framesState, Preferences framesNode) {
/*  750 */     this.preferencesStorage.saveFramesState(framesState, framesNode);
/*      */   }
/*      */ 
/*      */   public boolean isFramesExpandedOf(Preferences nodes) {
/*  754 */     return this.preferencesStorage.isFramesExpandedOf(nodes);
/*      */   }
/*      */ 
/*      */   public void setFramesExpanded(boolean value, Preferences framesNode) {
/*  758 */     this.preferencesStorage.setFramesExpanded(value, framesNode);
/*      */   }
/*      */ 
/*      */   public boolean isFrameUndocked(Preferences framePreferencesNode, Integer id) {
/*  762 */     return this.preferencesStorage.isFrameUndocked(framePreferencesNode, id);
/*      */   }
/*      */ 
/*      */   public void saveConsoleFontMonospaced(boolean isMonospaced) {
/*  766 */     this.preferencesStorage.saveConsoleFontMonospaced(isMonospaced);
/*      */   }
/*      */   public boolean isConsoleFontMonospaced() {
/*  769 */     return this.preferencesStorage.isConsoleFontMonospaced();
/*      */   }
/*      */ 
/*      */   public void save(DockedUndockedFrame frame, Preferences framePreferencesNode) {
/*  773 */     this.preferencesStorage.save(frame, framePreferencesNode);
/*      */   }
/*      */ 
/*      */   public void save(JFrame frame) {
/*  777 */     this.preferencesStorage.save(frame);
/*      */   }
/*      */ 
/*      */   public boolean restore(Preferences framePreferencesNode, DockedUndockedFrame frame, boolean isFrameExpanded) {
/*  781 */     return this.preferencesStorage.restore(framePreferencesNode, frame, isFrameExpanded);
/*      */   }
/*      */ 
/*      */   public void restore(JFrame frame) {
/*  785 */     this.preferencesStorage.restore(frame);
/*      */   }
/*      */ 
/*      */   public void removeServiceEditor(Integer panelId) {
/*  789 */     this.preferencesStorage.removeServiceEditor(panelId);
/*      */   }
/*      */ 
/*      */   public List<LastUsedIndicatorBean> getLastUsedIndicatorNames() {
/*  793 */     return this.preferencesStorage.getLastUsedIndicatorNames();
/*      */   }
/*      */ 
/*      */   public void addLastUsedIndicatorName(LastUsedIndicatorBean indicatorBean) {
/*  797 */     this.preferencesStorage.addLastUsedIndicatorName(indicatorBean);
/*      */   }
/*      */ 
/*      */   public void removeLastUsedIndicatorName(LastUsedIndicatorBean indicatorBean) {
/*  801 */     this.preferencesStorage.removeLastUsedIndicatorName(indicatorBean);
/*      */   }
/*      */ 
/*      */   public void clearAllLastUsedIndicatorNames()
/*      */   {
/*  806 */     this.preferencesStorage.clearAllLastUsedIndicatorNames();
/*      */   }
/*      */ 
/*      */   public void updateLastUsedIndicatorName(LastUsedIndicatorBean indicatorBean) {
/*  810 */     this.preferencesStorage.updateLastUsedIndicatorName(indicatorBean);
/*      */   }
/*      */ 
/*      */   public void save(List<StrategyTestBean> strategyTestBeans)
/*      */   {
/*  815 */     this.preferencesStorage.save(strategyTestBeans);
/*      */   }
/*      */ 
/*      */   public List<StrategyTestBean> getStrategyTestBeans()
/*      */   {
/*  820 */     return this.preferencesStorage.getStrategyTestBeans();
/*      */   }
/*      */ 
/*      */   public StrategyTestBean getStrategyTestBean(int panelId)
/*      */   {
/*  825 */     List beans = this.preferencesStorage.getStrategyTestBeans();
/*  826 */     for (StrategyTestBean strategyTestBean : beans) {
/*  827 */       if (strategyTestBean.getPanelChartId() == panelId) {
/*  828 */         return strategyTestBean;
/*      */       }
/*      */     }
/*  831 */     return null;
/*      */   }
/*      */ 
/*      */   public void saveLocalCachePath(String localCachePath) {
/*  835 */     this.preferencesStorage.saveLocalCachePath(localCachePath);
/*      */   }
/*      */ 
/*      */   public String getLocalCachePath() {
/*  839 */     return this.preferencesStorage.getLocalCachePath();
/*      */   }
/*      */ 
/*      */   public void saveMyStrategiesPath(String myStrategiesPath) {
/*  843 */     this.preferencesStorage.saveMyStrategiesPath(myStrategiesPath);
/*      */   }
/*      */ 
/*      */   public String getMyStrategiesPath() {
/*  847 */     return this.preferencesStorage.getMyStrategiesPath();
/*      */   }
/*      */ 
/*      */   public void saveMyIndicatorsPath(String myIndicatorsPath) {
/*  851 */     this.preferencesStorage.saveMyIndicatorsPath(myIndicatorsPath);
/*      */   }
/*      */ 
/*      */   public String getMyIndicatorsPath() {
/*  855 */     return this.preferencesStorage.getMyIndicatorsPath();
/*      */   }
/*      */ 
/*      */   public void saveMyWorkspaceSettingsFilePath(String myWorkspaceSettingsPath) {
/*  859 */     String userId = this.preferencesStorage.getUserAccountId((String)GreedContext.getConfig("account_name"));
/*  860 */     this.preferencesStorage.saveUserWorkspaceSettingsFilePath(userId, myWorkspaceSettingsPath);
/*      */   }
/*      */ 
/*      */   public void saveMyWorkspaceSettingsFolderPath(String localMyWorkspaceSettingsFolderPath)
/*      */   {
/*  865 */     if (!localMyWorkspaceSettingsFolderPath.endsWith(FilePathManager.getInstance().getPathSeparator())) {
/*  866 */       localMyWorkspaceSettingsFolderPath = localMyWorkspaceSettingsFolderPath + FilePathManager.getInstance().getPathSeparator();
/*      */     }
/*  868 */     String myWorkspaceSettingsPath = localMyWorkspaceSettingsFolderPath + new File(getMyWorkspaceSettingsFilePath()).getName();
/*  869 */     saveMyWorkspaceSettingsFilePath(myWorkspaceSettingsPath);
/*      */   }
/*      */ 
/*      */   public String getMyWorkspaceSettingsFilePath()
/*      */   {
/*  874 */     String userId = this.preferencesStorage.getUserAccountId((String)GreedContext.getConfig("account_name"));
/*  875 */     return this.preferencesStorage.getUserWorkspaceSettingsFilePath(userId);
/*      */   }
/*      */ 
/*      */   public String getMyWorkspaceSettingsFolderPath()
/*      */   {
/*  880 */     String fileName = getMyWorkspaceSettingsFilePath();
/*  881 */     return new File(fileName).getParentFile().getAbsolutePath();
/*      */   }
/*      */ 
/*      */   public String getMyChartTemplatesPath()
/*      */   {
/*  886 */     return this.preferencesStorage.getMyChartTemplatesPath();
/*      */   }
/*      */ 
/*      */   public void saveMyChartTemplatesPath(String myChartTemplatesPath)
/*      */   {
/*  891 */     this.preferencesStorage.saveMyChartTemplatesPath(myChartTemplatesPath);
/*      */   }
/*      */ 
/*      */   public void saveStopStrategyByException(boolean stopStrategyByException) {
/*  895 */     this.preferencesStorage.saveStopStrategyByException(stopStrategyByException);
/*      */   }
/*      */ 
/*      */   public boolean getStopStrategyByException() {
/*  899 */     return this.preferencesStorage.getStopStrategyByException();
/*      */   }
/*      */ 
/*      */   public void saveHeapSizeShown(boolean heapSizeShown) {
/*  903 */     this.preferencesStorage.saveHeapSizeShown(heapSizeShown);
/*      */   }
/*      */ 
/*      */   public boolean getHeapSizeShown() {
/*  907 */     return this.preferencesStorage.getHeapSizeShown();
/*      */   }
/*      */ 
/*      */   public void cleanUp() {
/*  911 */     this.preferencesStorage.cleanUp();
/*      */   }
/*      */ 
/*      */   public void cleanUpWorkspaceSettingsCache() {
/*  915 */     this.preferencesStorage.cleanupWorkspaceSettingsCache();
/*      */   }
/*      */ 
/*      */   public boolean isUserFirstLoading() {
/*  919 */     return this.isUserFirstLoading;
/*      */   }
/*      */ 
/*      */   public void save(INewsFilter newsFilter, INewsFilter.NewsSource newsSource) {
/*  923 */     this.preferencesStorage.save(newsFilter, newsSource);
/*      */   }
/*      */ 
/*      */   public INewsFilter load(INewsFilter.NewsSource newsSource) {
/*  927 */     return this.preferencesStorage.load(newsSource);
/*      */   }
/*      */ 
/*      */   public boolean bottomPanelExists(int panelId) {
/*  931 */     return this.preferencesStorage.bottomPanelExists(panelId);
/*      */   }
/*      */ 
/*      */   public boolean bottomPanelFramesPreferencesNodeExists() {
/*  935 */     return this.preferencesStorage.bottomPanelFramesPreferencesNodeExists();
/*      */   }
/*      */ 
/*      */   public void saveDealPanelLayout(byte[] array) {
/*  939 */     this.preferencesStorage.saveDealPanelLayout(array);
/*      */   }
/*      */ 
/*      */   public void saveMarketDepthPanelHeight(int height) {
/*  943 */     this.preferencesStorage.saveMarketDepthPanelHeight(height);
/*      */   }
/*      */ 
/*      */   public int restoreMarketDepthPanelHeight() {
/*  947 */     return this.preferencesStorage.restoreMarketDepthPanelHeight();
/*      */   }
/*      */ 
/*      */   public void saveInstrumentsPanelHeight(int height) {
/*  951 */     this.preferencesStorage.saveInstrumentsPanelHeight(height);
/*      */   }
/*      */ 
/*      */   public int restoreInstrumentsPanelHeight() {
/*  955 */     return this.preferencesStorage.restoreInstrumentsPanelHeight();
/*      */   }
/*      */ 
/*      */   public byte[] getDealPanelLayout()
/*      */   {
/*  960 */     return this.preferencesStorage.getDealPanelLayout();
/*      */   }
/*      */ 
/*      */   public static void saveSystemSettings() {
/*  964 */     ChartTemplateSettingsStorage chartTemplateSettingsStorage = (ChartTemplateSettingsStorage)GreedContext.get("chartTemplateSettingsStorage");
/*  965 */     if (chartTemplateSettingsStorage != null) {
/*  966 */       chartTemplateSettingsStorage.cleanUpChartTemplateRootNode();
/*      */     }
/*      */ 
/*  969 */     String systemSettingsFilePath = FilePathManager.getInstance().getSystemSettingsFilePath();
/*      */ 
/*  971 */     File systemFile = new File(systemSettingsFilePath);
/*  972 */     FileOutputStream os = null;
/*      */     try {
/*  974 */       os = new FileOutputStream(systemFile);
/*  975 */       Preferences.systemRoot().exportSubtree(os);
/*      */     } catch (IOException e) {
/*  977 */       LOGGER.error("Failed to save workspace settings into file: " + systemFile.getPath(), e);
/*      */     } catch (BackingStoreException e) {
/*  979 */       LOGGER.error("Failed to retrieve workspace settings...", e);
/*      */     } finally {
/*  981 */       closeOutputStream(os);
/*      */     }
/*      */   }
/*      */ 
/*      */   private void saveUserCurrentWorkspaceSettings() {
/*  986 */     String userId = this.preferencesStorage.getUserAccountId((String)GreedContext.getConfig("account_name"));
/*  987 */     File userFile = new File(this.preferencesStorage.getUserWorkspaceSettingsFilePath(userId));
/*  988 */     FileOutputStream os = null;
/*      */     try {
/*  990 */       if (!userFile.exists()) {
/*  991 */         userFile.createNewFile();
/*      */       }
/*  993 */       os = new FileOutputStream(userFile);
/*  994 */       PreferencesStorage.getUserRoot().exportSubtree(os);
/*      */     } catch (IOException e) {
/*  996 */       LOGGER.error("Failed to save workspace settings into file: " + userFile.getPath(), e);
/*      */     } catch (BackingStoreException e) {
/*  998 */       LOGGER.error("Failed to retrieve workspace settings...", e);
/*      */     } finally {
/* 1000 */       closeOutputStream(os);
/*      */     }
/*      */   }
/*      */ 
/*      */   public synchronized void saveWorkspaceSettings() {
/* 1005 */     LOGGER.trace("saveWorkspaceSettings");
/*      */ 
/* 1007 */     FileLock lock = null;
/* 1008 */     FileChannel channel = null;
/*      */     try {
/* 1010 */       String lockFilePath = FilePathManager.getInstance().addPathSeparatorIfNeeded(FilePathManager.getInstance().getCacheDirectory()) + FilePathManager.getInstance().WORKSPACE_LOCK_FILE_NAME;
/*      */ 
/* 1014 */       File file = new File(lockFilePath);
/*      */ 
/* 1016 */       channel = new RandomAccessFile(file, "rw").getChannel();
/*      */ 
/* 1021 */       lock = channel.tryLock();
/*      */ 
/* 1023 */       if (lock != null) {
/* 1024 */         ClientForm clientForm = (ClientForm)GreedContext.get("clientGui");
/* 1025 */         if (clientForm != null) {
/* 1026 */           ClientFormLayoutManager layoutManager = clientForm.getLayoutManager();
/* 1027 */           layoutManager.saveClientSettings(this);
/*      */ 
/* 1029 */           saveUserCurrentWorkspaceSettings();
/* 1030 */           saveSystemSettings();
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/*      */     }
/*      */     catch (Exception e)
/*      */     {
/* 1040 */       LOGGER.error(e.getMessage(), e);
/*      */     } finally {
/*      */       try {
/* 1043 */         if (lock != null) {
/* 1044 */           lock.release();
/*      */         }
/* 1046 */         if (channel != null)
/* 1047 */           channel.close();
/*      */       }
/*      */       catch (Throwable e)
/*      */       {
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   public <T extends OutputStream> T writeWorkspaceSettingsToOutputStream(T outputStream) throws IOException, BackingStoreException {
/* 1056 */     Preferences.systemRoot().exportSubtree(outputStream);
/* 1057 */     return outputStream;
/*      */   }
/*      */ 
/*      */   public void saveWorkspaceSettingsAs(String filePath, Long autoSavePeriod, Boolean saveOnExit)
/*      */   {
/* 1062 */     XMLFileFilter filer = new XMLFileFilter();
/* 1063 */     if (!filer.accept(new File(filePath))) {
/* 1064 */       filePath = filePath + "." + filer.getExtension();
/*      */     }
/*      */ 
/* 1068 */     Long currentAutoSavePeriod = this.preferencesStorage.restoreWorkspaceAutoSavePeriod(getDefaultWorkspaceAutoSavePeriodInMinutes());
/* 1069 */     Boolean currentSaveOnExt = this.preferencesStorage.restoreWorkspaceSaveOnExit(getDefaultWorkspaceSaveOnExitEnabled());
/*      */     try
/*      */     {
/* 1072 */       this.preferencesStorage.saveWorkspaceAutoSavePeriod(autoSavePeriod);
/* 1073 */       this.preferencesStorage.saveWorkspaceSaveOnExit(saveOnExit);
/*      */ 
/* 1075 */       saveNodeInto(PreferencesStorage.getUserRoot(), filePath);
/*      */     }
/*      */     finally
/*      */     {
/* 1079 */       if (!filePath.equals(getMyWorkspaceSettingsFilePath()))
/*      */       {
/* 1081 */         this.preferencesStorage.saveWorkspaceAutoSavePeriod(currentAutoSavePeriod);
/* 1082 */         this.preferencesStorage.saveWorkspaceSaveOnExit(currentSaveOnExt);
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   public void loadWorkspaceSettings() {
/* 1088 */     String userId = this.preferencesStorage.getUserAccountId((String)GreedContext.getConfig("account_name"));
/* 1089 */     File userFile = new File(this.preferencesStorage.getUserWorkspaceSettingsFilePath(userId));
/*      */ 
/* 1091 */     FileInputStream is = null;
/*      */     try {
/* 1093 */       is = new FileInputStream(userFile);
/* 1094 */       Preferences.importPreferences(is);
/* 1095 */       LOGGER.info("Workspace settings successfully loaded from file: " + this.preferencesStorage.getUserWorkspaceSettingsFilePath(userId));
/*      */     } catch (FileNotFoundException e) {
/* 1097 */       LOGGER.info("File " + userFile.getName() + " not found. Loading default settings");
/*      */     } catch (IOException e) {
/* 1099 */       LOGGER.error("Failed to load workspace settings from file: " + userFile.getPath(), e);
/*      */     } catch (InvalidPreferencesFormatException e) {
/* 1101 */       LOGGER.info("Workspace settings are empty or of invalid format! " + userFile.getPath());
/*      */     } finally {
/* 1103 */       closeInputStream(is);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void loadWorkspaceSettingsFrom(File workspaceSettingsFile) {
/* 1108 */     if (workspaceSettingsFile == null) {
/* 1109 */       return;
/*      */     }
/* 1111 */     String userId = this.preferencesStorage.getUserAccountId((String)GreedContext.getConfig("account_name"));
/* 1112 */     this.preferencesStorage.saveUserWorkspaceSettingsFilePath(userId, workspaceSettingsFile.getAbsolutePath());
/* 1113 */     saveNodeInto(Preferences.systemRoot(), FilePathManager.getInstance().getSystemSettingsFilePath());
/*      */   }
/*      */ 
/*      */   public Preferences getMainFramePreferencesNode() {
/* 1117 */     return this.preferencesStorage.getMainFramePreferencesNode();
/*      */   }
/*      */ 
/*      */   public Preferences getBottomFramePreferencesNode() {
/* 1121 */     return this.preferencesStorage.getBottomFramePreferencesNode();
/*      */   }
/*      */ 
/*      */   public Preferences getChartsNode() {
/* 1125 */     return this.preferencesStorage.getChartsNode();
/*      */   }
/*      */ 
/*      */   public Preferences getBottomFramesNode() {
/* 1129 */     return this.preferencesStorage.getBottomFramesNode();
/*      */   }
/*      */ 
/*      */   public void saveSystemProperties() {
/* 1133 */     this.preferencesStorage.saveSystemProperties("com.dukascopy.");
/*      */   }
/*      */ 
/*      */   public void restoreSystemProperties() {
/* 1137 */     this.preferencesStorage.restoreSystemProperties();
/*      */   }
/*      */ 
/*      */   private void saveNodeInto(Preferences node, String workspaceSettingsFilePath) {
/* 1141 */     FileOutputStream os = null;
/*      */     try {
/* 1143 */       os = new FileOutputStream(workspaceSettingsFilePath);
/* 1144 */       node.exportSubtree(os);
/* 1145 */       LOGGER.info("Workspace settings successfully exported to file: " + workspaceSettingsFilePath);
/*      */     } catch (IOException e) {
/* 1147 */       LOGGER.error("Failed to save workspace settings into file: " + workspaceSettingsFilePath, e);
/*      */     } catch (BackingStoreException e) {
/* 1149 */       LOGGER.error("Failed to retrieve workspace settings...", e);
/*      */     } finally {
/* 1151 */       closeOutputStream(os);
/*      */     }
/*      */   }
/*      */ 
/*      */   public List<IndicatorWrapper> getIndicatorWrappers(int chartPanelId, boolean dontSaveSettings) {
/* 1156 */     List indicatorWrappers = this.preferencesStorage.getIndicatorWrappers(chartPanelId, dontSaveSettings);
/* 1157 */     return indicatorWrappers;
/*      */   }
/*      */ 
/*      */   private static void closeInputStream(FileInputStream is)
/*      */   {
/* 1163 */     if (is != null)
/*      */       try {
/* 1165 */         is.close();
/*      */       } catch (IOException e) {
/* 1167 */         e.printStackTrace();
/*      */       }
/*      */   }
/*      */ 
/*      */   private static void closeOutputStream(FileOutputStream out)
/*      */   {
/* 1174 */     if (out != null)
/*      */       try {
/* 1176 */         out.close();
/*      */       }
/*      */       catch (IOException e)
/*      */       {
/*      */       }
/*      */   }
/*      */ 
/*      */   public void saveSelectedInstruments(List<Instrument> selectedInstruments)
/*      */   {
/* 1185 */     if ((selectedInstruments == null) || (selectedInstruments.isEmpty())) {
/* 1186 */       return;
/*      */     }
/*      */ 
/* 1189 */     Set strInstruments = new HashSet();
/*      */ 
/* 1191 */     for (Instrument instrument : selectedInstruments) {
/* 1192 */       strInstruments.add(instrument.toString());
/*      */     }
/*      */ 
/* 1195 */     saveSelectedInstruments(strInstruments);
/*      */   }
/*      */ 
/*      */   public List<? extends RowSorter.SortKey> restoreTableSortKeys(String tableId)
/*      */   {
/* 1200 */     return this.preferencesStorage.restoreTableKeys(tableId);
/*      */   }
/*      */ 
/*      */   public void saveTableSortKeys(String tableId, List<? extends RowSorter.SortKey> sortKeys)
/*      */   {
/* 1205 */     this.preferencesStorage.saveTableSortKeys(tableId, sortKeys);
/*      */   }
/*      */ 
/*      */   public void restoreTableColumns(String tableId, TableColumnModel tableColumnModel)
/*      */   {
/* 1210 */     this.preferencesStorage.restoreTableColumns(tableId, tableColumnModel);
/*      */   }
/*      */ 
/*      */   public void saveTableColumns(String tableId, TableColumnModel tableColumnModel)
/*      */   {
/* 1215 */     this.preferencesStorage.saveTableColumns(tableId, tableColumnModel);
/*      */   }
/*      */ 
/*      */   public Long getDefaultWorkspaceAutoSavePeriodInMinutes()
/*      */   {
/* 1220 */     return new Long(3L);
/*      */   }
/*      */ 
/*      */   public Long restoreWorkspaceAutoSavePeriodInMinutes()
/*      */   {
/* 1225 */     return this.preferencesStorage.restoreWorkspaceAutoSavePeriod(getDefaultWorkspaceAutoSavePeriodInMinutes());
/*      */   }
/*      */ 
/*      */   public void saveWorkspaceAutoSavePeriodInMinutes(Long workspaceAutoSavePeriod)
/*      */   {
/* 1230 */     this.preferencesStorage.saveWorkspaceAutoSavePeriod(workspaceAutoSavePeriod);
/*      */   }
/*      */ 
/*      */   public Boolean restoreWorkspaceSaveOnExitEnabled()
/*      */   {
/* 1235 */     return this.preferencesStorage.restoreWorkspaceSaveOnExit(getDefaultWorkspaceSaveOnExitEnabled());
/*      */   }
/*      */ 
/*      */   public Boolean getDefaultWorkspaceSaveOnExitEnabled()
/*      */   {
/* 1240 */     return new Boolean(true);
/*      */   }
/*      */ 
/*      */   public void saveWorkspaceSaveOnExitEnabled(Boolean workspaceSaveOnExitEnabled)
/*      */   {
/* 1245 */     this.preferencesStorage.saveWorkspaceSaveOnExit(workspaceSaveOnExitEnabled);
/*      */   }
/*      */ 
/*      */   public List<JForexPeriod> restoreChartPeriods()
/*      */   {
/* 1251 */     return this.preferencesStorage.restoreChartPeriods();
/*      */   }
/*      */ 
/*      */   public void saveChartPeriods(List<JForexPeriod> periods)
/*      */   {
/* 1256 */     this.preferencesStorage.saveChartPeriods(periods);
/*      */   }
/*      */ 
/*      */   public List<JForexPeriod> getDefaultChartPeriods()
/*      */   {
/* 1261 */     return this.preferencesStorage.getDefaultChartPeriods();
/*      */   }
/*      */ 
/*      */   public List<JForexPeriod> sortChartPeriods(List<JForexPeriod> periods)
/*      */   {
/* 1266 */     return this.preferencesStorage.sortChartPeriods(periods);
/*      */   }
/*      */ 
/*      */   public boolean isForceReconnectsActive() {
/* 1270 */     return this.preferencesStorage.restoreForsedReconnects();
/*      */   }
/*      */ 
/*      */   public void saveForceReconnectsActive(boolean active)
/*      */   {
/* 1275 */     this.preferencesStorage.saveForsedReconnects(active);
/*      */   }
/*      */ 
/*      */   public void saveContestRates(Map<Instrument, BigDecimal> rates)
/*      */   {
/* 1280 */     this.preferencesStorage.saveContValidationRates(rates);
/*      */   }
/*      */ 
/*      */   public Double restoreContestRateByInstrument(Instrument instrument)
/*      */   {
/* 1285 */     return this.preferencesStorage.restoreContestRate(instrument);
/*      */   }
/*      */ 
/*      */   public Map<String, List<Object[]>> restoreHorizontalRetracementPresets(IChart.Type type)
/*      */   {
/* 1290 */     return this.preferencesStorage.restoreHorizontalRetracementPresets(type);
/*      */   }
/*      */ 
/*      */   public void saveHorizontalRetracementPresets(IChart.Type type, Map<String, List<Object[]>> presets)
/*      */   {
/* 1295 */     this.preferencesStorage.saveHorizontalRetracementPresets(type, presets);
/*      */   }
/*      */ 
/*      */   public List<StrategyNewBean> getStrategyNewBeans()
/*      */   {
/* 1300 */     return this.preferencesStorage.getStrategyNewBeans();
/*      */   }
/*      */ 
/*      */   public void saveStrategyNewBean(StrategyNewBean beanToSave)
/*      */   {
/* 1305 */     this.preferencesStorage.saveStrategyNewBean(beanToSave);
/*      */   }
/*      */ 
/*      */   public void removeStrategyNewBean(StrategyNewBean beanToRemove)
/*      */   {
/* 1310 */     this.preferencesStorage.removeStrategyNewBean(beanToRemove);
/*      */   }
/*      */ 
/*      */   public HistoricalDataManagerBean getHistoricalDataManagerBean()
/*      */   {
/* 1315 */     return this.preferencesStorage.getHistoricalDataManagerBean();
/*      */   }
/*      */ 
/*      */   public void save(HistoricalDataManagerBean historicalDataManagerBean)
/*      */   {
/* 1320 */     this.preferencesStorage.save(historicalDataManagerBean);
/*      */   }
/*      */ 
/*      */   public int getLastActiveChartPanelId()
/*      */   {
/* 1325 */     return ((JForexClientFormLayoutManager)GreedContext.get("layoutManager")).getTabbedPane().getLastActiveChartPanelId();
/*      */   }
/*      */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.settings.ClientSettingsStorageImpl
 * JD-Core Version:    0.6.0
 */