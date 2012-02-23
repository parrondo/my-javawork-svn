/*    */ package com.dukascopy.dds2.greed.gui.settings;
/*    */ 
/*    */ import com.dukascopy.api.IChartObject;
/*    */ import com.dukascopy.api.INewsFilter;
/*    */ import com.dukascopy.api.INewsFilter.NewsSource;
/*    */ import com.dukascopy.api.Instrument;
/*    */ import com.dukascopy.api.impl.IndicatorWrapper;
/*    */ import com.dukascopy.charts.data.datacache.JForexPeriod;
/*    */ import com.dukascopy.charts.persistence.BottomPanelBean;
/*    */ import com.dukascopy.charts.persistence.ChartBean;
/*    */ import com.dukascopy.charts.persistence.CustomIndicatorBean;
/*    */ import com.dukascopy.charts.persistence.ITheme;
/*    */ import com.dukascopy.charts.persistence.IndicatorBean;
/*    */ import com.dukascopy.charts.persistence.LastUsedIndicatorBean;
/*    */ import com.dukascopy.charts.persistence.StrategyTestBean;
/*    */ import com.dukascopy.charts.settings.ChartSettings.Option;
/*    */ import com.dukascopy.dds2.greed.export.historicaldata.HistoricalDataManagerBean;
/*    */ import com.dukascopy.dds2.greed.gui.ClientForm;
/*    */ import com.dukascopy.dds2.greed.gui.DealPanel;
/*    */ import com.dukascopy.dds2.greed.gui.GuiUtilsAndConstants;
/*    */ import com.dukascopy.dds2.greed.gui.component.ExpandableSplitPane;
/*    */ import com.dukascopy.dds2.greed.gui.component.chart.DockedUndockedFrame;
/*    */ import com.dukascopy.dds2.greed.gui.component.chart.FramesState;
/*    */ import com.dukascopy.dds2.greed.gui.component.moverview.config.MarketOverviewConfig;
/*    */ import com.dukascopy.dds2.greed.gui.component.strategy.tab.mediator.StrategyNewBean;
/*    */ import java.awt.Dimension;
/*    */ import java.awt.Point;
/*    */ import java.io.File;
/*    */ import java.io.IOException;
/*    */ import java.io.OutputStream;
/*    */ import java.math.BigDecimal;
/*    */ import java.util.List;
/*    */ import java.util.Map;
/*    */ import java.util.Set;
/*    */ import java.util.prefs.BackingStoreException;
/*    */ import java.util.prefs.Preferences;
/*    */ import javax.swing.JFrame;
/*    */ import javax.swing.RowSorter.SortKey;
/*    */ import javax.swing.table.TableColumnModel;
/*    */ 
/*    */ public abstract interface ClientSettingsStorage
/*    */ {
/*    */   public static final String DEFAULT_AMOUNT = "amount";
/* 45 */   public static final float DEFAULT_AMOUNT_VALUE = GuiUtilsAndConstants.DEFAULT_MILL_AMOUNT.floatValue();
/*    */   public static final String DEFAULT_XAG_AMOUNT = "xag_amount";
/* 48 */   public static final float DEFAULT_XAG_AMOUNT_VALUE = GuiUtilsAndConstants.MIN_XAG_AMOUNT.floatValue();
/*    */   public static final String DEFAULT_XAU_AMOUNT = "xau_amount";
/* 51 */   public static final float DEFAULT_XAU_AMOUNT_VALUE = GuiUtilsAndConstants.ONE.floatValue();
/*    */   public static final String DEFAULT_AMOUNT_LOT = "amountLot";
/* 54 */   public static final int DEFAULT_AMOUNT_LOT_VALUE = GuiUtilsAndConstants.ONE_MILLION.intValue();
/*    */   public static final String DEFAULT_SLIPPAGE = "slippageVal";
/* 57 */   public static final float DEFAULT_SLIPPAGE_VALUE = GuiUtilsAndConstants.FIFE.floatValue();
/*    */   public static final String DEFAULT_OPEN_IF_OFFSET = "entryVal";
/* 60 */   public static final float DEFAULT_OPEN_IF_OFFSET_VALUE = GuiUtilsAndConstants.TEN.floatValue();
/*    */   public static final String DEFAULT_STOP_LOSS_OFFSET = "slVal";
/* 63 */   public static final float DEFAULT_STOP_LOSS_OFFSET_VALUE = GuiUtilsAndConstants.TEN.floatValue();
/*    */   public static final String DEFAULT_TAKE_PROFIT_OFFSET = "tpVal";
/* 66 */   public static final float DEFAULT_TAKE_PROFIT_OFFSET_VALUE = GuiUtilsAndConstants.TEN.floatValue();
/*    */   public static final String DEFAULT_TRAILING_STEP = "trailingStepVal";
/* 69 */   public static final float DEFAULT_TRAILING_STEP_VALUE = GuiUtilsAndConstants.TEN.floatValue();
/*    */   public static final String DEFAULT_ORDER_VALIDATY = "orderValidaty";
/* 72 */   public static final float DEFAULT_ORDER_VALIDATY_VALUE = GuiUtilsAndConstants.TEN.floatValue();
/*    */   public static final String DEFAULT_VALIDATY_TIME_UNIT = "orderValidatyTimeUnit";
/* 75 */   public static final int DEFAULT_VALIDATY_TIME_UNIT_VALUE = GuiUtilsAndConstants.ONE.intValue();
/*    */   public static final int MO_HEIGHT_DEFAULT = 500;
/*    */   public static final int MO_WIDTH_DEFAULT = 500;
/*    */   public static final String MO_LOCATION_X = "moLocationX";
/*    */   public static final String MO_LOCATION_Y = "moLocationY";
/*    */   public static final String IS_JFOREX_MODE = "isJforexMode";
/*    */   public static final boolean DEFAULT_ONE_CLICK_STATE = true;
/*    */   public static final boolean DEFAULT_ORDER_VALIDATION = true;
/*    */   public static final boolean DEFAULT_APPLY_SLIPPAGE_TO_ALL_MARKET_ORDERS = false;
/*    */   public static final boolean DEFAULT_APPLY_STOP_LOSS_TO_ALL_MARKET_ORDERS = false;
/*    */   public static final boolean DEFAULT_APPLY_TAKE_PROFIT_TO_ALL_MARKET_ORDERS = false;
/*    */   public static final boolean DEFAULT_APPLY_FILL_OR_KILL_ORDERS = false;
/*    */   public static final boolean DEFAULT_APPLY_TIME_VALIDATION_TO_ALL_MARKET_ORDERS = false;
/*    */   public static final boolean DEFAULT_HIP_SIZE_SHOWN = false;
/*    */   public static final boolean DEFAULT_STOP_STRATEGY_BY_EXCEPTION = true;
/*    */   public static final boolean DEFAULT_MONOSPACED_SHOWN = false;
/*    */ 
/*    */   public abstract String getWorkspaceSettingsFileNameWithoutPrefix();
/*    */ 
/*    */   public abstract boolean restoreLogState();
/*    */ 
/*    */   public abstract void saveLogState(boolean paramBoolean);
/*    */ 
/*    */   public abstract boolean restoreOrderValidationOn();
/*    */ 
/*    */   public abstract void saveOrderValidationOn(boolean paramBoolean);
/*    */ 
/*    */   public abstract boolean restoreApplySlippageToAllMarketOrders();
/*    */ 
/*    */   public abstract void saveApplySlippageToAllMarketOrders(boolean paramBoolean);
/*    */ 
/*    */   public abstract boolean restoreApplyStopLossToAllMarketOrders();
/*    */ 
/*    */   public abstract void saveApplyStopLossToAllMarketOrders(boolean paramBoolean);
/*    */ 
/*    */   public abstract boolean restoreApplyTakeProfitToAllMarketOrders();
/*    */ 
/*    */   public abstract boolean restoreApplyTimeValidationToAllMarketOrders();
/*    */ 
/*    */   public abstract void saveApplyTakeProfitToAllMarketOrders(boolean paramBoolean);
/*    */ 
/*    */   public abstract void saveApplyTimeValidationToAllMarketOrders(boolean paramBoolean);
/*    */ 
/*    */   public abstract boolean restoreFillOrKillOrders();
/*    */ 
/*    */   public abstract void saveFillOrKillOrders(boolean paramBoolean);
/*    */ 
/*    */   public abstract void saveDefaultStrategyPath(String paramString);
/*    */ 
/*    */   public abstract void saveOneClickState(boolean paramBoolean);
/*    */ 
/*    */   public abstract void save(ClientForm paramClientForm);
/*    */ 
/*    */   public abstract void restore(ClientForm paramClientForm);
/*    */ 
/*    */   public abstract void restore(DealPanel paramDealPanel);
/*    */ 
/*    */   public abstract void save(DealPanel paramDealPanel);
/*    */ 
/*    */   public abstract boolean restoreChartsAlwaysOnTop();
/*    */ 
/*    */   public abstract void saveChartsAlwaysOnTop(boolean paramBoolean);
/*    */ 
/*    */   public abstract void saveMOsize(Dimension paramDimension);
/*    */ 
/*    */   public abstract void saveMOlocation(Point paramPoint);
/*    */ 
/*    */   public abstract void saveDefaultStopLossOffset(Float paramFloat);
/*    */ 
/*    */   public abstract void saveDefaultOpenIfOffset(Float paramFloat);
/*    */ 
/*    */   public abstract void saveDefaultTakeProfitOffset(Float paramFloat);
/*    */ 
/*    */   public abstract void saveDefaultAmount(Float paramFloat);
/*    */ 
/*    */   public abstract void saveDefaultXAUAmount(Float paramFloat);
/*    */ 
/*    */   public abstract void saveDefaultXAGAmount(Float paramFloat);
/*    */ 
/*    */   public abstract void saveLotAmount(int paramInt);
/*    */ 
/*    */   public abstract void saveDefaultSlippage(Float paramFloat);
/*    */ 
/*    */   public abstract void saveDefaultTrailingStep(Float paramFloat);
/*    */ 
/*    */   public abstract void saveOrderValidityTime(Float paramFloat);
/*    */ 
/*    */   public abstract void saveStrategyDiscState(boolean paramBoolean);
/*    */ 
/*    */   public abstract boolean restoreStrategyDiscState();
/*    */ 
/*    */   public abstract void saveRemoteStrategyDiscState(boolean paramBoolean);
/*    */ 
/*    */   public abstract boolean restoreRemoteStrategyDiscState();
/*    */ 
/*    */   public abstract void saveFullAccessDiscState(boolean paramBoolean);
/*    */ 
/*    */   public abstract boolean restoreFullAccessDiscState();
/*    */ 
/*    */   public abstract void saveTesterDiscState(boolean paramBoolean);
/*    */ 
/*    */   public abstract boolean restoreTesterDiscState();
/*    */ 
/*    */   public abstract Dimension restoreMOsize();
/*    */ 
/*    */   public abstract Point restoreMOlocation();
/*    */ 
/*    */   public abstract String restoreDefaultAmountAsText();
/*    */ 
/*    */   public abstract BigDecimal restoreDefaultAmount();
/*    */ 
/*    */   public abstract BigDecimal restoreDefaultXAUAmount();
/*    */ 
/*    */   public abstract BigDecimal restoreDefaultXAGAmount();
/*    */ 
/*    */   public abstract BigDecimal restoreDefaultAmountLot();
/*    */ 
/*    */   public abstract BigDecimal restoreAmountLot();
/*    */ 
/*    */   public abstract BigDecimal restoreDefaultTrailingStep();
/*    */ 
/*    */   public abstract BigDecimal restoreDefaultSlippage();
/*    */ 
/*    */   public abstract String restoreDefaultSlippageAsText();
/*    */ 
/*    */   public abstract String restoreDefaultStopLossOffsetAsText();
/*    */ 
/*    */   public abstract String restoreDefaultOpenIfOffsetAsText();
/*    */ 
/*    */   public abstract String restoreDefaultTakeProfitOffsetAsText();
/*    */ 
/*    */   public abstract BigDecimal restoreDefaultStopLossOffset();
/*    */ 
/*    */   public abstract BigDecimal restoreDefaultTakeProfitOffset();
/*    */ 
/*    */   public abstract BigDecimal restoreDefaultOpenIfOffset();
/*    */ 
/*    */   public abstract BigDecimal restoreOrderValidityTime();
/*    */ 
/*    */   public abstract boolean restoreOneClickState();
/*    */ 
/*    */   public abstract void saveSpotAtMarket(boolean paramBoolean);
/*    */ 
/*    */   public abstract boolean restoreSpotAtMarket();
/*    */ 
/*    */   public abstract void saveSplitPane(ExpandableSplitPane paramExpandableSplitPane);
/*    */ 
/*    */   public abstract void restoreSplitPane(ExpandableSplitPane paramExpandableSplitPane, double paramDouble);
/*    */ 
/*    */   public abstract void saveInstrumenTabs(MarketOverviewConfig paramMarketOverviewConfig, int paramInt);
/*    */ 
/*    */   public abstract void clearInstrumentsTabs();
/*    */ 
/*    */   public abstract void saveSelectedInstruments(Set<String> paramSet);
/*    */ 
/*    */   public abstract void saveSelectedInstruments(List<Instrument> paramList);
/*    */ 
/*    */   public abstract void saveLastSelectedInstrument();
/*    */ 
/*    */   public abstract String restoreLastSelectedInstrument();
/*    */ 
/*    */   public abstract List<String> restoreSelectedInstruments();
/*    */ 
/*    */   public abstract void cleanAllSelectedInstruments();
/*    */ 
/*    */   public abstract MarketOverviewConfig restoreInstrumentTabs();
/*    */ 
/*    */   public abstract int getSelectedInstrumentTabIndex();
/*    */ 
/*    */   public abstract void saveDetachedLocation(String paramString, Point paramPoint);
/*    */ 
/*    */   public abstract Point restoreDetachedLocation(String paramString);
/*    */ 
/*    */   public abstract void savePersistDetachedLocationState(boolean paramBoolean);
/*    */ 
/*    */   public abstract boolean restorePersistDetachedLocationState();
/*    */ 
/*    */   public abstract void deleteAllSettings();
/*    */ 
/*    */   public abstract String[] getDefaultInstrumentList();
/*    */ 
/*    */   public abstract void saveSelectedTheme(ITheme paramITheme);
/*    */ 
/*    */   public abstract void removeAllThemes();
/*    */ 
/*    */   public abstract void save(ITheme paramITheme);
/*    */ 
/*    */   public abstract String restoreSelectedTheme();
/*    */ 
/*    */   public abstract List<ITheme> loadThemes();
/*    */ 
/*    */   public abstract void clearChartSettings();
/*    */ 
/*    */   public abstract void save(ChartSettings.Option paramOption, String paramString);
/*    */ 
/*    */   public abstract String load(ChartSettings.Option paramOption);
/*    */ 
/*    */   public abstract void saveFrameDimension(FrameType paramFrameType, Dimension paramDimension);
/*    */ 
/*    */   public abstract Dimension restoreFrameDimension(FrameType paramFrameType);
/*    */ 
/*    */   public abstract List<ChartBean> getChartBeans();
/*    */ 
/*    */   public abstract ChartBean getChartBean(int paramInt);
/*    */ 
/*    */   public abstract void save(ChartBean paramChartBean);
/*    */ 
/*    */   public abstract void remove(Integer paramInteger);
/*    */ 
/*    */   public abstract List<StrategyNewBean> getStrategyNewBeans();
/*    */ 
/*    */   public abstract void saveStrategyNewBean(StrategyNewBean paramStrategyNewBean);
/*    */ 
/*    */   public abstract void removeStrategyNewBean(StrategyNewBean paramStrategyNewBean);
/*    */ 
/*    */   public abstract List<CustomIndicatorBean> getCustomIndicatorBeans();
/*    */ 
/*    */   public abstract CustomIndicatorBean getCustomIndicatorBean(int paramInt);
/*    */ 
/*    */   public abstract void save(CustomIndicatorBean paramCustomIndicatorBean);
/*    */ 
/*    */   public abstract void remove(CustomIndicatorBean paramCustomIndicatorBean);
/*    */ 
/*    */   public abstract List<IndicatorBean> getIndicatorsBeans(Integer paramInteger);
/*    */ 
/*    */   public abstract void saveIndicatorBean(Integer paramInteger, IndicatorBean paramIndicatorBean);
/*    */ 
/*    */   public abstract void removeIndicator(Integer paramInteger1, Integer paramInteger2);
/*    */ 
/*    */   public abstract List<IChartObject> getDrawingsFor(Integer paramInteger);
/*    */ 
/*    */   public abstract void save(Integer paramInteger, IChartObject paramIChartObject);
/*    */ 
/*    */   public abstract void removeDrawing(Integer paramInteger, IChartObject paramIChartObject);
/*    */ 
/*    */   public abstract List<IChartObject> getSubIndicatorDrawingsFor(int paramInt1, int paramInt2);
/*    */ 
/*    */   public abstract void save(int paramInt1, int paramInt2, IChartObject paramIChartObject);
/*    */ 
/*    */   public abstract void removeDrawing(int paramInt1, int paramInt2, IChartObject paramIChartObject);
/*    */ 
/*    */   public abstract void save(BottomPanelBean paramBottomPanelBean);
/*    */ 
/*    */   public abstract void remove(BottomPanelBean paramBottomPanelBean);
/*    */ 
/*    */   public abstract List<BottomPanelBean> getBottomPanelBeans();
/*    */ 
/*    */   public abstract void setChartsExpanded(boolean paramBoolean);
/*    */ 
/*    */   public abstract void setStrategiesExpanded(boolean paramBoolean);
/*    */ 
/*    */   public abstract void setIndicatorsExpanded(boolean paramBoolean);
/*    */ 
/*    */   public abstract boolean isIndicatorsExpanded();
/*    */ 
/*    */   public abstract boolean isChartsExpanded();
/*    */ 
/*    */   public abstract boolean isStrategiesExpanded();
/*    */ 
/*    */   public abstract boolean isForceReconnectsActive();
/*    */ 
/*    */   public abstract void saveForceReconnectsActive(boolean paramBoolean);
/*    */ 
/*    */   public abstract boolean isBodySplitExpanded();
/*    */ 
/*    */   public abstract void setBodySplitExpanded(boolean paramBoolean);
/*    */ 
/*    */   public abstract FramesState getFramesStateOf(Preferences paramPreferences);
/*    */ 
/*    */   public abstract void saveFramesState(FramesState paramFramesState, Preferences paramPreferences);
/*    */ 
/*    */   public abstract boolean isFramesExpandedOf(Preferences paramPreferences);
/*    */ 
/*    */   public abstract void setFramesExpanded(boolean paramBoolean, Preferences paramPreferences);
/*    */ 
/*    */   public abstract boolean isFrameUndocked(Preferences paramPreferences, Integer paramInteger);
/*    */ 
/*    */   public abstract void saveConsoleFontMonospaced(boolean paramBoolean);
/*    */ 
/*    */   public abstract boolean isConsoleFontMonospaced();
/*    */ 
/*    */   public abstract void save(DockedUndockedFrame paramDockedUndockedFrame, Preferences paramPreferences);
/*    */ 
/*    */   public abstract void save(JFrame paramJFrame);
/*    */ 
/*    */   public abstract boolean restore(Preferences paramPreferences, DockedUndockedFrame paramDockedUndockedFrame, boolean paramBoolean);
/*    */ 
/*    */   public abstract void restore(JFrame paramJFrame);
/*    */ 
/*    */   public abstract void removeServiceEditor(Integer paramInteger);
/*    */ 
/*    */   public abstract List<LastUsedIndicatorBean> getLastUsedIndicatorNames();
/*    */ 
/*    */   public abstract void addLastUsedIndicatorName(LastUsedIndicatorBean paramLastUsedIndicatorBean);
/*    */ 
/*    */   public abstract void removeLastUsedIndicatorName(LastUsedIndicatorBean paramLastUsedIndicatorBean);
/*    */ 
/*    */   public abstract void clearAllLastUsedIndicatorNames();
/*    */ 
/*    */   public abstract void updateLastUsedIndicatorName(LastUsedIndicatorBean paramLastUsedIndicatorBean);
/*    */ 
/*    */   public abstract void save(List<StrategyTestBean> paramList);
/*    */ 
/*    */   public abstract List<StrategyTestBean> getStrategyTestBeans();
/*    */ 
/*    */   public abstract StrategyTestBean getStrategyTestBean(int paramInt);
/*    */ 
/*    */   public abstract void saveLocalCachePath(String paramString);
/*    */ 
/*    */   public abstract String getLocalCachePath();
/*    */ 
/*    */   public abstract void saveMyStrategiesPath(String paramString);
/*    */ 
/*    */   public abstract String getMyStrategiesPath();
/*    */ 
/*    */   public abstract void saveMyIndicatorsPath(String paramString);
/*    */ 
/*    */   public abstract String getMyIndicatorsPath();
/*    */ 
/*    */   public abstract void saveMyWorkspaceSettingsFilePath(String paramString);
/*    */ 
/*    */   public abstract void saveMyWorkspaceSettingsFolderPath(String paramString);
/*    */ 
/*    */   public abstract void saveMyChartTemplatesPath(String paramString);
/*    */ 
/*    */   public abstract String getMyWorkspaceSettingsFilePath();
/*    */ 
/*    */   public abstract String getMyWorkspaceSettingsFolderPath();
/*    */ 
/*    */   public abstract void saveStopStrategyByException(boolean paramBoolean);
/*    */ 
/*    */   public abstract boolean getStopStrategyByException();
/*    */ 
/*    */   public abstract void saveHeapSizeShown(boolean paramBoolean);
/*    */ 
/*    */   public abstract boolean getHeapSizeShown();
/*    */ 
/*    */   public abstract void cleanUp();
/*    */ 
/*    */   public abstract void cleanUpWorkspaceSettingsCache();
/*    */ 
/*    */   public abstract boolean isUserFirstLoading();
/*    */ 
/*    */   public abstract void save(INewsFilter paramINewsFilter, INewsFilter.NewsSource paramNewsSource);
/*    */ 
/*    */   public abstract INewsFilter load(INewsFilter.NewsSource paramNewsSource);
/*    */ 
/*    */   public abstract boolean bottomPanelExists(int paramInt);
/*    */ 
/*    */   public abstract boolean bottomPanelFramesPreferencesNodeExists();
/*    */ 
/*    */   public abstract void saveDealPanelLayout(byte[] paramArrayOfByte);
/*    */ 
/*    */   public abstract byte[] getDealPanelLayout();
/*    */ 
/*    */   public abstract void saveWorkspaceSettings();
/*    */ 
/*    */   public abstract void saveWorkspaceSettingsAs(String paramString, Long paramLong, Boolean paramBoolean);
/*    */ 
/*    */   public abstract <T extends OutputStream> T writeWorkspaceSettingsToOutputStream(T paramT)
/*    */     throws IOException, BackingStoreException;
/*    */ 
/*    */   public abstract void loadWorkspaceSettings();
/*    */ 
/*    */   public abstract void loadWorkspaceSettingsFrom(File paramFile);
/*    */ 
/*    */   public abstract Preferences getMainFramePreferencesNode();
/*    */ 
/*    */   public abstract Preferences getBottomFramePreferencesNode();
/*    */ 
/*    */   public abstract Preferences getChartsNode();
/*    */ 
/*    */   public abstract Preferences getBottomFramesNode();
/*    */ 
/*    */   public abstract void saveSystemProperties();
/*    */ 
/*    */   public abstract void restoreSystemProperties();
/*    */ 
/*    */   public abstract String getMyChartTemplatesPath();
/*    */ 
/*    */   public abstract List<IndicatorWrapper> getIndicatorWrappers(int paramInt, boolean paramBoolean);
/*    */ 
/*    */   public abstract void saveTableSortKeys(String paramString, List<? extends RowSorter.SortKey> paramList);
/*    */ 
/*    */   public abstract List<? extends RowSorter.SortKey> restoreTableSortKeys(String paramString);
/*    */ 
/*    */   public abstract void saveTableColumns(String paramString, TableColumnModel paramTableColumnModel);
/*    */ 
/*    */   public abstract void restoreTableColumns(String paramString, TableColumnModel paramTableColumnModel);
/*    */ 
/*    */   public abstract void saveMarketDepthPanelHeight(int paramInt);
/*    */ 
/*    */   public abstract int restoreMarketDepthPanelHeight();
/*    */ 
/*    */   public abstract void saveInstrumentsPanelHeight(int paramInt);
/*    */ 
/*    */   public abstract int restoreInstrumentsPanelHeight();
/*    */ 
/*    */   public abstract Long restoreWorkspaceAutoSavePeriodInMinutes();
/*    */ 
/*    */   public abstract Long getDefaultWorkspaceAutoSavePeriodInMinutes();
/*    */ 
/*    */   public abstract void saveWorkspaceAutoSavePeriodInMinutes(Long paramLong);
/*    */ 
/*    */   public abstract Boolean restoreWorkspaceSaveOnExitEnabled();
/*    */ 
/*    */   public abstract Boolean getDefaultWorkspaceSaveOnExitEnabled();
/*    */ 
/*    */   public abstract void saveWorkspaceSaveOnExitEnabled(Boolean paramBoolean);
/*    */ 
/*    */   public abstract void saveChartPeriods(List<JForexPeriod> paramList);
/*    */ 
/*    */   public abstract List<JForexPeriod> restoreChartPeriods();
/*    */ 
/*    */   public abstract List<JForexPeriod> getDefaultChartPeriods();
/*    */ 
/*    */   public abstract List<JForexPeriod> sortChartPeriods(List<JForexPeriod> paramList);
/*    */ 
/*    */   public abstract void saveContestRates(Map<Instrument, BigDecimal> paramMap);
/*    */ 
/*    */   public abstract Double restoreContestRateByInstrument(Instrument paramInstrument);
/*    */ 
/*    */   public abstract HistoricalDataManagerBean getHistoricalDataManagerBean();
/*    */ 
/*    */   public abstract void save(HistoricalDataManagerBean paramHistoricalDataManagerBean);
/*    */ 
/*    */   public abstract void saveOredrValidityTimeUnit(int paramInt);
/*    */ 
/*    */   public abstract int restoreOrderValidityTimeUnit();
/*    */ 
/*    */   public static enum FrameType
/*    */   {
/* 99 */     PREFERENCES;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.settings.ClientSettingsStorage
 * JD-Core Version:    0.6.0
 */