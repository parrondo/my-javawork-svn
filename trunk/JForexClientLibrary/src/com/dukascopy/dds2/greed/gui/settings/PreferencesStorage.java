/*      */ package com.dukascopy.dds2.greed.gui.settings;
/*      */ 
/*      */ import com.dukascopy.api.DataType;
/*      */ import com.dukascopy.api.DataType.DataPresentationType;
/*      */ import com.dukascopy.api.Filter;
/*      */ import com.dukascopy.api.IChart.Type;
/*      */ import com.dukascopy.api.IChartObject;
/*      */ import com.dukascopy.api.IIndicators.AppliedPrice;
/*      */ import com.dukascopy.api.INewsFilter;
/*      */ import com.dukascopy.api.INewsFilter.NewsSource;
/*      */ import com.dukascopy.api.Instrument;
/*      */ import com.dukascopy.api.OfferSide;
/*      */ import com.dukascopy.api.Period;
/*      */ import com.dukascopy.api.PriceRange;
/*      */ import com.dukascopy.api.ReversalAmount;
/*      */ import com.dukascopy.api.TickBarSize;
/*      */ import com.dukascopy.api.Unit;
/*      */ import com.dukascopy.api.impl.IndicatorWrapper;
/*      */ import com.dukascopy.api.impl.LevelInfo;
/*      */ import com.dukascopy.api.impl.connect.AuthorizationClient;
/*      */ import com.dukascopy.api.indicators.OutputParameterInfo.DrawingStyle;
/*      */ import com.dukascopy.api.system.ITesterClient.DataLoadingMethod;
/*      */ import com.dukascopy.charts.ChartProperties;
/*      */ import com.dukascopy.charts.data.datacache.JForexPeriod;
/*      */ import com.dukascopy.charts.drawings.ChartObject;
/*      */ import com.dukascopy.charts.main.DDSChartsActionAdapter;
/*      */ import com.dukascopy.charts.math.indicators.IndicatorsProvider;
/*      */ import com.dukascopy.charts.persistence.BottomPanelBean;
/*      */ import com.dukascopy.charts.persistence.ChartBean;
/*      */ import com.dukascopy.charts.persistence.CustomIndicatorBean;
/*      */ import com.dukascopy.charts.persistence.EnabledIndicatorBean;
/*      */ import com.dukascopy.charts.persistence.ITheme;
/*      */ import com.dukascopy.charts.persistence.ITheme.ChartElement;
/*      */ import com.dukascopy.charts.persistence.ITheme.StrokeElement;
/*      */ import com.dukascopy.charts.persistence.ITheme.TextElement;
/*      */ import com.dukascopy.charts.persistence.IdManager;
/*      */ import com.dukascopy.charts.persistence.IndicatorBean;
/*      */ import com.dukascopy.charts.persistence.LastUsedIndicatorBean;
/*      */ import com.dukascopy.charts.persistence.StrategyTestBean;
/*      */ import com.dukascopy.charts.persistence.Theme;
/*      */ import com.dukascopy.charts.persistence.ThemeManager;
/*      */ import com.dukascopy.charts.settings.ChartSettings.Option;
/*      */ import com.dukascopy.dds2.greed.GreedContext;
/*      */ import com.dukascopy.dds2.greed.export.historicaldata.HistoricalDataManagerBean;
/*      */ import com.dukascopy.dds2.greed.gui.ClientForm;
/*      */ import com.dukascopy.dds2.greed.gui.DealPanel;
/*      */ import com.dukascopy.dds2.greed.gui.GuiUtilsAndConstants;
/*      */ import com.dukascopy.dds2.greed.gui.InstrumentAvailabilityManager;
/*      */ import com.dukascopy.dds2.greed.gui.component.ExpandableSplitPane;
/*      */ import com.dukascopy.dds2.greed.gui.component.chart.DockedUndockedFrame;
/*      */ import com.dukascopy.dds2.greed.gui.component.chart.FramesState;
/*      */ import com.dukascopy.dds2.greed.gui.component.marketdepth.MarketDepthPanel;
/*      */ import com.dukascopy.dds2.greed.gui.component.moverview.config.MarketOverviewConfig;
/*      */ import com.dukascopy.dds2.greed.gui.component.moverview.config.MiniPanelConfig;
/*      */ import com.dukascopy.dds2.greed.gui.component.moverview.config.TabConfig;
/*      */ import com.dukascopy.dds2.greed.gui.component.orders.OrderEntryPanel;
/*      */ import com.dukascopy.dds2.greed.gui.component.strategy.tab.mediator.StrategyNewBean;
/*      */ import com.dukascopy.dds2.greed.gui.component.strategy.tab.mediator.StrategyStatus;
/*      */ import com.dukascopy.dds2.greed.gui.component.strategy.tab.mediator.StrategyType;
/*      */ import com.dukascopy.dds2.greed.gui.component.strategy.tab.preset.DefaultStrategyPresetsController;
/*      */ import com.dukascopy.dds2.greed.gui.component.strategy.tab.preset.IStrategyPresetsController;
/*      */ import com.dukascopy.dds2.greed.gui.component.strategy.tab.preset.StrategyPreset;
/*      */ import com.dukascopy.dds2.greed.gui.component.strategy.tab.util.IncorrectClassTypeException;
/*      */ import com.dukascopy.dds2.greed.gui.component.strategy.tab.util.StrategyBinaryLoader;
/*      */ import com.dukascopy.dds2.greed.gui.component.ticker.TickerPanel;
/*      */ import com.dukascopy.dds2.greed.gui.component.tree.WorkspaceTreePanel;
/*      */ import com.dukascopy.dds2.greed.gui.util.lotamount.LotAmountChanger;
/*      */ import com.dukascopy.dds2.greed.model.MarketView;
/*      */ import com.dukascopy.dds2.greed.util.FilePathManager;
/*      */ import com.dukascopy.dds2.greed.util.SerializableBasicStroke;
/*      */ import com.dukascopy.dds2.greed.util.SimpleAlphabeticInstrumentComparator;
/*      */ import java.awt.BasicStroke;
/*      */ import java.awt.Color;
/*      */ import java.awt.Dimension;
/*      */ import java.awt.Font;
/*      */ import java.awt.Point;
/*      */ import java.awt.Rectangle;
/*      */ import java.io.File;
/*      */ import java.io.IOException;
/*      */ import java.io.UnsupportedEncodingException;
/*      */ import java.math.BigDecimal;
/*      */ import java.security.InvalidParameterException;
/*      */ import java.security.NoSuchAlgorithmException;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Arrays;
/*      */ import java.util.Collections;
/*      */ import java.util.Comparator;
/*      */ import java.util.Enumeration;
/*      */ import java.util.HashMap;
/*      */ import java.util.Iterator;
/*      */ import java.util.LinkedHashSet;
/*      */ import java.util.LinkedList;
/*      */ import java.util.List;
/*      */ import java.util.Map;
/*      */ import java.util.Map.Entry;
/*      */ import java.util.Properties;
/*      */ import java.util.Set;
/*      */ import java.util.SortedMap;
/*      */ import java.util.SortedSet;
/*      */ import java.util.StringTokenizer;
/*      */ import java.util.TreeMap;
/*      */ import java.util.TreeSet;
/*      */ import java.util.prefs.BackingStoreException;
/*      */ import java.util.prefs.Preferences;
/*      */ import javax.swing.JFrame;
/*      */ import javax.swing.RowSorter.SortKey;
/*      */ import javax.swing.SortOrder;
/*      */ import javax.swing.table.TableColumn;
/*      */ import javax.swing.table.TableColumnModel;
/*      */ import org.slf4j.Logger;
/*      */ import org.slf4j.LoggerFactory;
/*      */ 
/*      */ public class PreferencesStorage
/*      */ {
/*  116 */   private static Logger LOGGER = LoggerFactory.getLogger(PreferencesStorage.class);
/*      */ 
/*  119 */   private static final String[] DEFAULT_INSTRUMENT_LIST_JCLIENT = { Instrument.EURUSD.toString(), Instrument.GBPUSD.toString(), Instrument.USDJPY.toString(), Instrument.USDCHF.toString(), Instrument.USDCAD.toString(), Instrument.AUDUSD.toString(), Instrument.EURCHF.toString(), Instrument.EURGBP.toString(), Instrument.GBPJPY.toString(), Instrument.GBPCHF.toString() };
/*      */ 
/*  132 */   private static final String[] DEFAULT_INSTRUMENT_LIST_JFOREX = { Instrument.EURUSD.toString(), Instrument.USDCHF.toString(), Instrument.GBPUSD.toString(), Instrument.USDJPY.toString() };
/*      */   private static final String LOCAL_CACHE_PATH = "localCachePath";
/*      */   private static final String CURRENT_SETTINGS_FILE_PATH = "currentWorkspaceSettingsFilePath";
/*      */   private static final String NULL_VALUE = "null";
/*      */   private static final String DEFAULTS = "Defaults";
/*      */   private static final String DETACHED = "detached";
/*      */   private static final String MAIN_FRAMES_NODE = "frames";
/*      */   private static final String CHART_SETTINGS_NODE = "ChartSettings";
/*      */   private static final String LOG_ENABLED = "enabled";
/*      */   private static final String ORDER_VALIDATION_ON = "orderValidationOn";
/*      */   private static final String APPLY_SLIPPAGE_TO_ALL_MARKET_ORDERS = "applySlippageToAllMarket";
/*      */   private static final String APPLY_SL_TO_ALL_MARKET_ORDERS = "applySLToAllMarket";
/*      */   private static final String APPLY_TP_TO_ALL_MARKET_ORDERS = "applyTPToAllMarket";
/*      */   private static final String APPLY_TIME_VAL_TO_ALL_MARKET_ORDERS = "applyTimeValidationToAllMarket";
/*      */   private static final String APPLY_FILL_OR_KILL_ORDERS = "applyFillOrKillOrders";
/*      */   private static final String CHECKBOXES = "Checkboxes";
/*      */   private static final String ONE_CLICK = "OneClick";
/*      */   private static final String FORSED_RECONNECTS = "ForsedReconnects";
/*      */   private static final String SHOW_HEAP_SIZE = "showHeapSize";
/*      */   private static final String FRAMES_STATE = "framesState";
/*      */   private static final String FRAMES_EXPANDED = "framesExpanded";
/*      */   private static final String FRAME_BOUNDS = "bounds";
/*      */   private static final String FRAME_SELECTED = "selected";
/*      */   private static final String FRAME_UNDOCKED = "undocked";
/*      */   private static final String DEAL_PANEL_LAYOUT = "dealPanelMultiSplitLayout";
/*      */   private static final String SYSTEM_PROPERTIES = "systemProperties";
/*      */   private static final String TABLES_SETTINGS_NODE = "TableSettings";
/*      */   private static final String TABLE_SORT = "tableSort";
/*      */   private static final String COLUMN_ID = "columnId";
/*      */   private static final String SORT_TYPE = "sortType";
/*      */   private static final String SORT_KEYS_COUNT = "sortKeysCount";
/*      */   private static final String TABLE_COLUMNS = "tableColumns";
/*      */   private static final String COLUMN_WIDTH = "columnWidth";
/*      */   private static final String COLUMN_POSITION = "columnPosition";
/*      */   private static final String PERIOD_SETTINGS_NODE = "PeriodSettings";
/*      */   private static final String SIZE_NODE = "size";
/*      */   private static final String CONTEST_RATE_NODE = "ContestRates";
/*      */   private static final String IS_CONSOLE_FONT_MONOSPACED = "isConsoleFontMonospaced";
/*      */   private static final String MARKET_PANEL_HEIGHT = "marketDepthPanelHeight";
/*      */   private static final String INSTRUMENT_PANEL_HEIGHT = "instrumentPanelHeight";
/*      */   public static final String CLIENT_NODE = "client";
/*      */   public static final String CHARTS_ALWAYS_ON_TOP = "alwaysOnTop";
/*      */   public static final String SPAM_NODE = "SpotAtMarket";
/*      */   public static final String SPAM_VISIBLE = "visible";
/*      */   public static final String PANES_NODE = "Panes";
/*      */   public static final String PANE_LOCATION_KEY = "location";
/*      */   public static final String PANE_MINIMIZED_KEY = "minimized";
/*      */   public static final String PANE_MAXIMIZED_KEY = "maximized";
/*      */   public static final String INSTRUMENTS_NODE = "InstrumentsNode";
/*      */   public static final String TAB_TITLE_KEY = "title";
/*      */   public static final String TAB_INDEX_KEY = "tabIndexKey";
/*      */   public static final String TAB_SELECTED_KEY = "selected";
/*      */   public static final String INSTRUMENT_NAME_KEY = "instrumentName";
/*      */   public static final String INSTRUMENT_INDEX_KEY = "instrumentIndex";
/*      */   public static final String SELECTED_INSTRUMENTS_KEY = "selected_instruments";
/*      */   public static final String LAST_SELECTED_INSTRUMENT_KEY = "lastSelectedInstrument";
/*      */   public static final String DETACHED_X = "deatched_x";
/*      */   public static final String DETACHED_Y = "deatched_y";
/*      */   public static final String DETACHED_I = "instrument";
/*      */   public static final String MO_HEIGHT = "moHeight";
/*      */   public static final String MO_WIDTH = "moWidth";
/*      */   public static final String MO_LOCATION_X = "moLocationX";
/*      */   public static final String MO_LOCATION_Y = "moLocationY";
/*      */   public static final String CLIENT_FORM = "ClientForm";
/*      */   public static final String CLIENT_FORM_BOUNDS = "Bounds";
/*      */   public static final String CLIENT_FORM_MAXIMIZED = "Maximized";
/*      */   public static final String DEAL_PANEL = "DealPanel";
/*      */   public static final String DIVIDER_LOCATION = "DividerLocation";
/*      */   public static final String MARKET_DEPTH_EXPANDED = "MarketDepthExpanded";
/*      */   public static final String CONDITIONAL_ORDERS_EXPANDED = "ConditionalOrdersExpanded";
/*      */   public static final String ORDER_ENTRY_EXPANDED = "OrderEntryExpanded";
/*      */   public static final String WORKSPACE_TREE_EXPANDED = "WorkspaceTreeExpanded";
/*      */   public static final String TICKER_EXPANDED = "TickerExpanded";
/*      */   public static final String DIMENSIONS_NODE = "Dimensions";
/*      */   public static final String MY_WORKSPACES_FOLDER = "Workspaces";
/*      */   private static final String JFOREX = "jforex";
/*      */   private static final String STRATEGIES = "strategies";
/*      */   private static final String STRATEGY_SOURCE_FILE_NAME = "strategySourceFileName";
/*      */   private static final String STRATEGY_BINARY_FILE_NAME = "strategyBinaryFileName";
/*      */   private static final String STRATEGY_TYPE = "strategyType";
/*      */   private static final String STRATEGY_PRESET = "strategyPreset";
/*      */   private static final String STRATEGY_REMOTE_PID = "remoteProcessId";
/*      */   private static final String STRATEGY_REMOTE_REQUEST_ID = "remoteRequestId";
/*      */   private static final String STRATEGY_COMMENTS = "strategyComments";
/*      */   private static final String CUSTOM_INDICATORS = "custIndicators";
/*      */   private static final String CUSTOM_INDICATOR_SOURCE_FILE_NAME = "custIndSourceFileName";
/*      */   private static final String CUSTOM_INDICATOR_BINARY_FILE_NAME = "custIndBinaryFileName";
/*      */   private static final String CUSTOM_ENABLED_INDICATORS = "custEnabledIndicators";
/*      */   private static final String IS_CHARTS_EXPANDED = "isChartsExpanded";
/*      */   private static final String IS_INDICATORS_EXPANDED = "isIndicatorsExpanded";
/*      */   private static final String IS_STRATEGIES_EXPANDED = "isStrategiesExpanded";
/*      */   private static final String BODY_SPLIT_EXPANDED = "bodySplitExpanded";
/*      */   private static final String BOTTOM_FRAMES_NODE = "bottomFrames";
/*      */   private static final String BOTTOM_FRAMES_PREF = "bottomFramesPref";
/*      */   private static final String STRATEGY_TEST = "strategyTest";
/*      */   private static final String STRATEGY_TEST_INSTRUMENTS = "instruments";
/*      */   private static final String STRATEGY_TEST_BINARY_PATH = "strategyFile";
/*      */   private static final String STRATEGY_TEST_INSTRUMENTS_DELIMETER = ",";
/*      */   private static final String STRATEGY_TEST_INSTRUMENTS_SPLITTER = "|";
/*      */   private static final String STRATEGY_TEST_CHART_PERIOD = "chartPeriod";
/*      */   private static final String STRATEGY_TEST_CONTENT = "content";
/*      */   private static final String STOP_STRATEGY_ON_EXCEPTION = "stopStrategyOnException";
/*      */   private static final String STRATEGIES_DISC = "StartegiesDiscAccepted";
/*      */   private static final String REMOTE_STRATEGIES_DISC = "RemoteStartegiesDiscAccepted";
/*      */   private static final String TESTER_DISC = "TesterDiscAccepted";
/*      */   private static final String FULL_ACCESS_DISC = "FullAccessDiscAccepted";
/*      */   private static final String MY_STRATEGIES_PATH = "myStrategiesPath";
/*      */   private static final String STRATEGY_DEFAULT_PATH = "strategyDefaultPath";
/*      */   private static final String MY_INDICATORS_PATH = "myIndicatorsPath";
/*      */   private static final String MY_CHART_TEMPLAES_PATH = "myChartTemplatesPath";
/*      */   private static final String FILTER_CONTENT = "content";
/*      */   private static final String HISTORICAL_DATA_MANAGER = "historicalDataManager";
/*      */   private static final String HISTORICAL_DATA_MANAGER_CONTENT = "historicalDataManagerContent";
/*      */   private static final String EXPANDED_STATE = "expandedState";
/*      */   private static final String CHARTS = "charts";
/*      */   private static final String INSTRUMENT_NAME = "instrumentName";
/*      */   private static final String PERIOD_UNIT = "periodUnit";
/*      */   private static final String PERIOD_UNITS_COUNT = "periodUnitsCount";
/*      */   private static final String PRICE_RANGE_VALUE = "priceRangeValue";
/*      */   private static final String DATA_TYPE = "dataType";
/*      */   private static final String PIP_COUNT = "pipCount";
/*      */   private static final String REVERSAL_AMOUNT = "reversalAmount";
/*      */   private static final String TICK_BAR_SIZE = "tradeBarSize";
/*      */   private static final String OFFER_SIDE_NAME = "offerSideName";
/*      */   private static final String FILTER_NAME = "filter";
/*      */   private static final String CANDLE_TYPE = "candleType";
/*      */   private static final String TICK_TYPE = "tickType";
/*      */   private static final String RANGE_BAR_TYPE = "priceRangeType";
/*      */   private static final String POINT_AND_FIGURE_TYPE = "pointAndFigureType";
/*      */   private static final String TICK_BAR_TYPE = "tradeBarType";
/*      */   private static final String RENKO_TYPE = "renkoType";
/*      */   private static final String IS_GRID_VISIBLE = "isGridVisible";
/*      */   private static final String IS_MOUSE_CURSOR_VISIBLE = "isMouseCursorVisible";
/*      */   private static final String IS_LAST_CANDLE_VISIBLE = "isLastCandleVisible";
/*      */   private static final String IS_VERTICAL_MOVEMENT_ENABLED = "isVerticalMovementEnabled";
/*      */   private static final String LAST_USED_INDICATORS = "lastUsedIndicators";
/*      */   private static final String LAST_USED_DATE = "lastUsedDate";
/*      */   private static final String HISTORICAL_TESTER_CHART = "historicalTesterChart";
/*      */   private static final String INDICATORS = "indicators";
/*      */   private static final String INDICATOR_NAME = "indicatorName";
/*      */   private static final String SUB_PANEL_ID = "subPanelId";
/*      */   private static final String INDICATOR_PARAMETERS = "indicatorParameters";
/*      */   private static final String PARAM_TYPE = "paramType";
/*      */   private static final String PARAM_VALUE = "paramValue";
/*      */   private static final String INTEGER = "INTEGER";
/*      */   private static final String DOUBLE = "DOUBLE";
/*      */   private static final String BOOLEAN = "BOOLEAN";
/*      */   private static final String INDICATORS_SIDES = "indicatorsSides";
/*      */   private static final String SIDE_VALUE = "sideValue";
/*      */   private static final String INDICATORS_PRICES_FOR_CANDLES = "indicatorsPricesForCandles";
/*      */   private static final String PRICE_VALUE = "priceValue";
/*      */   private static final String INDICATOR_COLORS = "indicatorColors";
/*      */   private static final String INDICATOR_COLORS2 = "indicatorColors2";
/*      */   private static final String INDICATOR_VALUES_ON_CHART = "indicatorValuesOnChart";
/*      */   private static final String INDICATOR_SHOW_OUTPUT = "indicatorShowOutput";
/*      */   private static final String INDICATOR_OPACITY_ALPHAS = "indicatorOpacityAlphas";
/*      */   private static final String INDICATOR_DRAWING_STYLES = "indicatorDrawingStyles";
/*      */   private static final String INDICATOR_LINE_WIDTHS = "indicatorLineWidths";
/*      */   private static final String INDICATOR_OUTPUT_SHIFTS = "indicatorOutputShifts";
/*      */   private static final String COLOR_VALUE = "colorValue";
/*      */   private static final String INDICATOR_DRAWING_STYLE_VALUE = "drawingStyleValue";
/*      */   private static final String INDICATOR_LINE_WIDTH_VALUE = "lineWidthValue";
/*      */   private static final String INDICATOR_OUTPUT_SHIFT_VALUE = "outputShiftValue";
/*      */   private static final String INDICATOR_DRAWINGS = "indicatorDrawings";
/*      */   private static final String INDICATOR_LEVELS = "indicatorLevels";
/*      */   private static final String LEVEL_CONTENT = "levelContent";
/*      */   private static final String RECALCULATE_ON_NEW_CANDLE_ONLY = "recalculateOnNewCandleOnly";
/*      */   private static final String DRAWINGS = "drawings";
/*      */   private static final String DRAWING_CONTENT = "content";
/*      */   private static final String DRAWING_TYPE = "type";
/*      */   private static final String DRAWING_STROKE = "stroke";
/*      */   private static final String END_TIME = "endTime";
/*      */   private static final String MIN_PRICE = "minPrice";
/*      */   private static final String MAX_PRICE = "maxPrice";
/*      */   private static final String IS_AUTO_SHIFT_ACTIVE = "isAutoShiftActive";
/*      */   private static final String CHART_SHIFT_IN_PX = "chartShiftInPx";
/*      */   private static final String Y_AXIS_PADDING = "yAxisPadding";
/*      */   private static final String DATA_UNIT_WIDTH = "dataUnitWidth";
/*      */   private static final String CHART_THEME = "theme";
/*      */   private static final String THEMES_NODE = "Themes";
/*      */   private static final String THEME_COLORS_NODE = "Colors";
/*      */   private static final String THEME_FONTS_NODE = "Fonts";
/*      */   private static final String THEME_STROKES_NODE = "Strokes";
/*      */   private static final String THEME_STROKE_DASH = "StrokeDash";
/*      */   private static final String THEME_STROKE_WIDTH = "StrokeWidth";
/*      */   private static final String THEME_SELECTED_KEY = "Selected";
/*      */   private static final String HORIZONTAL_RETRACEMENT_PRESETS = "HorizontalRetracementPresets";
/*      */   private static final String HORIZONTAL_RETRACEMENT_LEVELS = "HorizontalRetracementLevels";
/*      */   private static final String HORIZONTAL_RETRACEMENT_COUNT = "HorizontalRetracementLevelsCount";
/*      */   private static final String HORIZONTAL_RETRACEMENT_LEVEL_LABEL = "HorizontalRetracementLevelLabel";
/*      */   private static final String HORIZONTAL_RETRACEMENT_LEVEL_VALUE = "HorizontalRetracementLevelValue";
/*      */   private static final String HORIZONTAL_RETRACEMENT_LEVEL_COLOR = "HorizontalRetracementLevelColor";
/*      */   private static final String WORKSPACE_SETTINGS_NODE = "WorkspaceSetings";
/*      */   private static final String WORKSPACE_OPTIONS = "WorkspaceOptions";
/*      */   private static final String WORKSPACE_AUTO_SAVE_PERIOD = "WorkspaceAutoSavePeriod";
/*      */   private static final String WORKSPACE_SAVE_ON_EXIT = "WorkspaceSaveOnExit";
/*      */ 
/*      */   public void saveLocalCachePath(String localCachePath)
/*      */   {
/*  146 */     Preferences.systemRoot().put("localCachePath", localCachePath);
/*  147 */     if (localCachePath != null)
/*  148 */       FilePathManager.getInstance().setCacheFolderPath(localCachePath);
/*      */   }
/*      */ 
/*      */   public String getLocalCachePath()
/*      */   {
/*  153 */     String localCachePath = null;
/*      */ 
/*  155 */     Preferences systemRoot = Preferences.systemRoot();
/*  156 */     localCachePath = systemRoot.get("localCachePath", FilePathManager.getInstance().getDefaultCacheFolderPath());
/*      */ 
/*  159 */     if (localCachePath == null) {
/*  160 */       Preferences jForexNode = getJForexNode();
/*  161 */       localCachePath = jForexNode.get("localCachePath", FilePathManager.getInstance().getDefaultCacheFolderPath());
/*      */     }
/*      */ 
/*  164 */     if (localCachePath == null) {
/*  165 */       localCachePath = FilePathManager.getInstance().getCacheDirectory();
/*      */     }
/*      */ 
/*  168 */     if (!FilePathManager.getInstance().isFolderAccessible(localCachePath)) {
/*  169 */       localCachePath = FilePathManager.getInstance().getAlternativeCacheFolderPath();
/*      */     }
/*      */ 
/*  172 */     saveLocalCachePath(localCachePath);
/*      */ 
/*  174 */     return localCachePath;
/*      */   }
/*      */ 
/*      */   public void cleanUp()
/*      */   {
/*      */     try
/*      */     {
/*  232 */       List ids = new LinkedList(Arrays.asList(getMainFramePreferencesNode().childrenNames()));
/*  233 */       ids.removeAll(Arrays.asList(getChartsNode().childrenNames()));
/*  234 */       ids.removeAll(Arrays.asList(getStrategiesNode().childrenNames()));
/*  235 */       ids.removeAll(Arrays.asList(getCustomIndicatorsNode().childrenNames()));
/*  236 */       for (String id : ids) {
/*  237 */         removeNode(getMainFramePreferences(id));
/*      */       }
/*  239 */       flush(getMainFramePreferencesNode());
/*      */     } catch (Exception e) {
/*  241 */       LOGGER.warn(new StringBuilder().append("Failed to clean up preferences due to: ").append(e.getMessage()).toString(), e);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void cleanupWorkspaceSettingsCache() {
/*      */     try {
/*  247 */       Preferences userRoot = Preferences.userRoot();
/*  248 */       String[] ids = userRoot.childrenNames();
/*  249 */       for (String id : ids)
/*      */         try {
/*  251 */           userRoot.node(id).removeNode();
/*      */         } catch (BackingStoreException e) {
/*  253 */           LOGGER.error(new StringBuilder().append("Failed to empty: ").append(id).append(" in the workspace settings...").toString());
/*      */         }
/*      */     }
/*      */     catch (BackingStoreException e) {
/*  257 */       LOGGER.error("Failed to clean up workspace settings cache...", e);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void deleteAllSettings() {
/*  262 */     String userId = getUserAccountId((String)GreedContext.getConfig("account_name"));
/*  263 */     deleteAllSettings(userId);
/*      */   }
/*      */ 
/*      */   private void deleteAllSettings(String userId) {
/*  267 */     Preferences userNode = getSystemRoot().node(userId);
/*  268 */     userNode.remove("currentWorkspaceSettingsFilePath");
/*      */ 
/*  270 */     Preferences clientModeNode = getSystemRoot().node(getClientMode());
/*  271 */     Preferences clientModeUserNode = clientModeNode.node(userId);
/*  272 */     clientModeUserNode.remove("currentWorkspaceSettingsFilePath");
/*      */   }
/*      */ 
/*      */   public void saveSystemProperties(String propertyNamePrefix)
/*      */   {
/*  277 */     Preferences node = getCommonNode().node("systemProperties");
/*      */     try {
/*  279 */       node.clear();
/*      */ 
/*  281 */       Properties properties = System.getProperties();
/*  282 */       Enumeration propertyNames = properties.propertyNames();
/*  283 */       while (propertyNames.hasMoreElements()) {
/*  284 */         String propertyName = (String)propertyNames.nextElement();
/*  285 */         if (propertyName.startsWith(propertyNamePrefix))
/*  286 */           node.put(propertyName, properties.getProperty(propertyName));
/*      */       }
/*      */     }
/*      */     catch (Exception ex) {
/*  290 */       LOGGER.error(new StringBuilder().append("Error while saving system properties : ").append(ex.getMessage()).toString());
/*      */     }
/*      */   }
/*      */ 
/*      */   public void restoreSystemProperties() {
/*  295 */     Preferences node = getCommonNode().node("systemProperties");
/*      */     try {
/*  297 */       for (String key : node.keys())
/*  298 */         if (key.equals("com.dukascopy.ParametersDialog.size")) {
/*  299 */           String value = node.get(key, null);
/*  300 */           if ((value != null) && (!value.trim().isEmpty()))
/*  301 */             System.setProperty(key, value);
/*      */         }
/*      */     }
/*      */     catch (Exception ex)
/*      */     {
/*  306 */       LOGGER.error(new StringBuilder().append("Error while restoring system properties : ").append(ex.getMessage()).toString());
/*      */     }
/*      */   }
/*      */ 
/*      */   public void saveHeapSizeShown(boolean heapSizeShown) {
/*  311 */     Preferences jForexNode = getCommonNode();
/*  312 */     jForexNode.putBoolean("showHeapSize", heapSizeShown);
/*      */   }
/*      */ 
/*      */   public boolean getHeapSizeShown() {
/*  316 */     Preferences jForexNode = getCommonNode();
/*  317 */     return jForexNode.getBoolean("showHeapSize", false);
/*      */   }
/*      */ 
/*      */   public void saveDefaultStopLossOffset(Float pipAmount) {
/*  321 */     Preferences node = getCommonDefaultsNode();
/*  322 */     node.putFloat("slVal", pipAmount.floatValue());
/*      */   }
/*      */ 
/*      */   public void saveDefaultOpenIfOffset(Float pipAmount) {
/*  326 */     Preferences node = getCommonDefaultsNode();
/*  327 */     node.putFloat("entryVal", pipAmount.floatValue());
/*      */   }
/*      */ 
/*      */   public void saveDefaultTakeProfitOffset(Float pipAmount) {
/*  331 */     Preferences node = getCommonDefaultsNode();
/*  332 */     node.putFloat("tpVal", pipAmount.floatValue());
/*      */   }
/*      */ 
/*      */   public void saveDefaultAmount(Float defaultAmount) {
/*  336 */     Preferences node = getCommonDefaultsNode();
/*  337 */     node.putFloat("amount", defaultAmount.floatValue());
/*      */   }
/*      */ 
/*      */   public void saveDefaultXAGAmount(Float defaultXAGAmount) {
/*  341 */     Preferences node = getCommonDefaultsNode();
/*  342 */     node.putFloat("xag_amount", defaultXAGAmount.floatValue());
/*      */   }
/*      */ 
/*      */   public void saveDefaultXAUAmount(Float defaultXAUAmount) {
/*  346 */     Preferences node = getCommonDefaultsNode();
/*  347 */     node.putFloat("xau_amount", defaultXAUAmount.floatValue());
/*      */   }
/*      */ 
/*      */   public void saveAmountLot(Integer defaultAmountLot) {
/*  351 */     Preferences node = getCommonDefaultsNode();
/*  352 */     node.putInt("amountLot", defaultAmountLot.intValue());
/*      */   }
/*      */ 
/*      */   public void saveDefaultSlippage(Float defaultSlippage) {
/*  356 */     Preferences node = getCommonDefaultsNode();
/*  357 */     node.putFloat("slippageVal", defaultSlippage.floatValue());
/*      */   }
/*      */ 
/*      */   public void saveDefaultTrailingStep(Float defaultTrailingStep) {
/*  361 */     Preferences node = getCommonDefaultsNode();
/*  362 */     node.putFloat("trailingStepVal", defaultTrailingStep.floatValue());
/*      */   }
/*      */ 
/*      */   public void saveOrderValidityTime(Float defaultOrderValidityTime) {
/*  366 */     Preferences node = getCommonDefaultsNode();
/*  367 */     node.putFloat("orderValidaty", defaultOrderValidityTime.floatValue());
/*      */   }
/*      */ 
/*      */   public void saveDefaultOrderValTimeUnit(Integer defaultOrderValidatyTimeUnitIndex) {
/*  371 */     Preferences node = getCommonDefaultsNode();
/*  372 */     node.putInt("orderValidatyTimeUnit", defaultOrderValidatyTimeUnitIndex.intValue());
/*      */   }
/*      */ 
/*      */   public boolean restoreOrderValidationOn() {
/*  376 */     Preferences node = getCheckBoxesNode();
/*  377 */     return node.getBoolean("orderValidationOn", true);
/*      */   }
/*      */ 
/*      */   public void saveOrderValidationOn(boolean isEnabled) {
/*  381 */     Preferences node = getCheckBoxesNode();
/*  382 */     node.putBoolean("orderValidationOn", isEnabled);
/*      */   }
/*      */ 
/*      */   public boolean restoreApplySlippageToAllMarketOrders() {
/*  386 */     Preferences node = getCheckBoxesNode();
/*  387 */     return node.getBoolean("applySlippageToAllMarket", false);
/*      */   }
/*      */ 
/*      */   public void saveApplySlippageToAllMarketOrders(boolean apply) {
/*  391 */     Preferences node = getCheckBoxesNode();
/*  392 */     node.putBoolean("applySlippageToAllMarket", apply);
/*      */   }
/*      */ 
/*      */   public boolean restoreForsedReconnects() {
/*  396 */     Preferences node = getCheckBoxesNode();
/*  397 */     return GreedContext.isDemo() ? true : node.getBoolean("ForsedReconnects", false);
/*      */   }
/*      */ 
/*      */   public void saveForsedReconnects(boolean isForsed)
/*      */   {
/*  403 */     Preferences node = getCheckBoxesNode();
/*  404 */     node.putBoolean("ForsedReconnects", isForsed);
/*      */   }
/*      */ 
/*      */   public boolean restoreApplyStopLossToAllMarketOrders() {
/*  408 */     Preferences node = getCheckBoxesNode();
/*  409 */     return node.getBoolean("applySLToAllMarket", false);
/*      */   }
/*      */ 
/*      */   public void saveApplyStopLossToAllMarketOrders(boolean apply) {
/*  413 */     Preferences node = getCheckBoxesNode();
/*  414 */     node.putBoolean("applySLToAllMarket", apply);
/*      */   }
/*      */ 
/*      */   public boolean restoreApplyTakeProfitToAllMarketOrders() {
/*  418 */     Preferences node = getCheckBoxesNode();
/*  419 */     return node.getBoolean("applyTPToAllMarket", false);
/*      */   }
/*      */ 
/*      */   public void saveApplyTakeProfitToAllMarketOrders(boolean apply) {
/*  423 */     Preferences node = getCheckBoxesNode();
/*  424 */     node.putBoolean("applyTPToAllMarket", apply);
/*      */   }
/*      */ 
/*      */   public boolean restoreApplyTimeValidationToAllMarketOrders() {
/*  428 */     Preferences node = getCheckBoxesNode();
/*  429 */     return node.getBoolean("applyTimeValidationToAllMarket", false);
/*      */   }
/*      */ 
/*      */   public void saveApplyTimeValidationToAllMarketOrders(boolean apply) {
/*  433 */     Preferences node = getCheckBoxesNode();
/*  434 */     node.putBoolean("applyTimeValidationToAllMarket", apply);
/*      */   }
/*      */ 
/*      */   public boolean restoreApplyFillOrKillOrders() {
/*  438 */     Preferences node = getCheckBoxesNode();
/*  439 */     return node.getBoolean("applyFillOrKillOrders", false);
/*      */   }
/*      */ 
/*      */   public void saveApplyFillOrKillOrders(boolean apply) {
/*  443 */     Preferences node = getCheckBoxesNode();
/*  444 */     node.putBoolean("applyFillOrKillOrders", apply);
/*      */   }
/*      */ 
/*      */   public void saveOneClickState(boolean enabled) {
/*  448 */     Preferences node = getCheckBoxesNode();
/*  449 */     node.putBoolean("OneClick", enabled);
/*      */   }
/*      */ 
/*      */   public boolean restoreOneClickState() {
/*  453 */     Preferences node = getCheckBoxesNode();
/*  454 */     return node.getBoolean("OneClick", true);
/*      */   }
/*      */ 
/*      */   public boolean restoreLogState() {
/*  458 */     Preferences node = getCheckBoxesNode();
/*  459 */     return node.getBoolean("enabled", true);
/*      */   }
/*      */ 
/*      */   public void saveLogState(boolean isEnabled) {
/*  463 */     Preferences node = getCheckBoxesNode();
/*  464 */     node.putBoolean("enabled", isEnabled);
/*      */   }
/*      */ 
/*      */   public boolean restoreChartsAlwaysOnTop() {
/*  468 */     Preferences node = getCheckBoxesNode();
/*  469 */     return node.getBoolean("alwaysOnTop", false);
/*      */   }
/*      */ 
/*      */   public void saveChartsAlwaysOnTop(boolean isEnabled) {
/*  473 */     Preferences node = getCheckBoxesNode();
/*  474 */     node.putBoolean("alwaysOnTop", isEnabled);
/*      */   }
/*      */ 
/*      */   public void savePersistDetachedLocationState(boolean value) {
/*  478 */     Preferences node = getCheckBoxesNode();
/*  479 */     node.putBoolean("detached", value);
/*      */   }
/*      */ 
/*      */   public boolean restorePersistDetachedLocationState() {
/*  483 */     Preferences node = getCheckBoxesNode();
/*  484 */     return node.getBoolean("detached", false);
/*      */   }
/*      */ 
/*      */   public void clearChartSettings()
/*      */   {
/*      */     try {
/*  490 */       getChartSettingsNode().removeNode();
/*      */     } catch (BackingStoreException ex) {
/*  492 */       LOGGER.warn(new StringBuilder().append("Unable to remove chart settings node : ").append(ex.getMessage()).toString(), ex);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void save(ChartSettings.Option option, String value) {
/*  497 */     Preferences node = getChartSettingsNode();
/*  498 */     node.put(option.name(), value);
/*  499 */     flush(node);
/*      */   }
/*      */ 
/*      */   public String load(ChartSettings.Option option) {
/*  503 */     Preferences node = getChartSettingsNode();
/*  504 */     return node.get(option.name(), null);
/*      */   }
/*      */ 
/*      */   public FramesState getFramesStateOf(Preferences nodes) {
/*  508 */     String framesState = nodes.get("framesState", FramesState.ORDERED.name());
/*  509 */     return FramesState.valueOf(framesState);
/*      */   }
/*      */ 
/*      */   public void saveFramesState(FramesState framesState, Preferences framesNode) {
/*  513 */     framesNode.put("framesState", framesState.name());
/*  514 */     flush(framesNode);
/*      */   }
/*      */ 
/*      */   public boolean isFramesExpandedOf(Preferences nodes) {
/*  518 */     return nodes.getBoolean("framesExpanded", false);
/*      */   }
/*      */ 
/*      */   public void setFramesExpanded(boolean value, Preferences framesNode) {
/*  522 */     framesNode.putBoolean("framesExpanded", value);
/*      */   }
/*      */ 
/*      */   public boolean isFrameUndocked(Preferences framePreferencesNode, Integer id) {
/*  526 */     return framePreferencesNode.node(Integer.toString(id.intValue())).getBoolean("undocked", false);
/*      */   }
/*      */ 
/*      */   public void saveConsoleFontMonospaced(boolean isMonospaced) {
/*  530 */     getCommonNode().putBoolean("isConsoleFontMonospaced", isMonospaced);
/*      */   }
/*      */ 
/*      */   public boolean isConsoleFontMonospaced() {
/*  534 */     return getCommonNode().getBoolean("isConsoleFontMonospaced", false);
/*      */   }
/*      */ 
/*      */   public void save(DockedUndockedFrame frame, Preferences framePreferencesNode) {
/*  538 */     Preferences node = framePreferencesNode.node(Integer.toString(frame.getPanelId()));
/*  539 */     if (node == null) {
/*  540 */       return;
/*      */     }
/*      */ 
/*  543 */     node.putBoolean("undocked", frame.isUndocked());
/*  544 */     node.putBoolean("selected", frame.isSelected());
/*      */     try {
/*  546 */       node.putByteArray("bounds", object2Bytes(frame.getBounds()));
/*      */     } catch (Exception ex) {
/*  548 */       LOGGER.warn(new StringBuilder().append("Unable to save frame bounds : ").append(ex.getMessage()).toString());
/*      */     }
/*      */ 
/*  551 */     flush(node);
/*      */   }
/*      */ 
/*      */   public void saveDealPanelLayout(byte[] array) {
/*  555 */     if (GreedContext.isStrategyAllowed())
/*  556 */       getJForexNode().putByteArray("dealPanelMultiSplitLayout", array);
/*      */     else
/*  558 */       getClientNode().putByteArray("dealPanelMultiSplitLayout", array);
/*      */   }
/*      */ 
/*      */   public byte[] getDealPanelLayout()
/*      */   {
/*  563 */     if (GreedContext.isStrategyAllowed()) {
/*  564 */       return getJForexNode().getByteArray("dealPanelMultiSplitLayout", null);
/*      */     }
/*  566 */     return getClientNode().getByteArray("dealPanelMultiSplitLayout", null);
/*      */   }
/*      */ 
/*      */   public void saveMarketDepthPanelHeight(int height)
/*      */   {
/*  571 */     getCommonNode().putInt("marketDepthPanelHeight", height);
/*      */   }
/*      */   public int restoreMarketDepthPanelHeight() {
/*  574 */     return getCommonNode().getInt("marketDepthPanelHeight", -1);
/*      */   }
/*      */   public void saveInstrumentsPanelHeight(int height) {
/*  577 */     getCommonNode().putInt("instrumentPanelHeight", height);
/*      */   }
/*      */   public int restoreInstrumentsPanelHeight() {
/*  580 */     return getCommonNode().getInt("instrumentPanelHeight", -1);
/*      */   }
/*      */ 
/*      */   public static Preferences getUserRoot() {
/*  584 */     return Preferences.userRoot();
/*      */   }
/*      */ 
/*      */   public static Preferences getSystemRoot() {
/*  588 */     Preferences userNode = Preferences.systemRoot();
/*  589 */     return userNode;
/*      */   }
/*      */ 
/*      */   private static Preferences getCommonNode() {
/*  593 */     return getUserRoot().node("common");
/*      */   }
/*      */ 
/*      */   private Preferences getCommonDefaultsNode() {
/*  597 */     return getCommonNode().node("Defaults");
/*      */   }
/*      */ 
/*      */   Preferences getMainFramePreferencesNode() {
/*  601 */     return getCommonNode().node("frames");
/*      */   }
/*      */ 
/*      */   private Preferences getMainFramePreferences(String panelId) {
/*  605 */     return getMainFramePreferencesNode().node(panelId);
/*      */   }
/*      */ 
/*      */   private Preferences getCheckBoxesNode() {
/*  609 */     return getCommonNode().node("Checkboxes");
/*      */   }
/*      */ 
/*      */   private Preferences getDetachedNode() {
/*  613 */     return getCommonNode().node("detached");
/*      */   }
/*      */ 
/*      */   private Preferences getChartSettingsNode() {
/*  617 */     return getCommonNode().node("ChartSettings");
/*      */   }
/*      */ 
/*      */   private Preferences getTablesSettingsNode() {
/*  621 */     return getCommonNode().node("TableSettings");
/*      */   }
/*      */ 
/*      */   private Preferences getContestRatesNode() {
/*  625 */     return getCommonNode().node("ContestRates");
/*      */   }
/*      */ 
/*      */   private boolean isStrategyAllowed()
/*      */   {
/*  631 */     return GreedContext.isStrategyAllowed();
/*      */   }
/*      */ 
/*      */   public String[] getDefaultInstrumentList() {
/*  635 */     if (isStrategyAllowed()) {
/*  636 */       return DEFAULT_INSTRUMENT_LIST_JFOREX;
/*      */     }
/*  638 */     return DEFAULT_INSTRUMENT_LIST_JCLIENT;
/*      */   }
/*      */ 
/*      */   public void save(ClientForm clientForm)
/*      */   {
/*  696 */     Preferences clientFormNode = getClientFormNode();
/*      */     try {
/*  698 */       clientFormNode.putByteArray("Bounds", object2Bytes(clientForm.getBounds()));
/*      */     } catch (Exception ex) {
/*  700 */       LOGGER.warn(new StringBuilder().append("Unable to save client form bounds : ").append(ex.getMessage()).toString(), ex);
/*      */     }
/*  702 */     clientFormNode.putBoolean("Maximized", (clientForm.getExtendedState() & 0x6) == 6);
/*      */   }
/*      */ 
/*      */   public void restore(ClientForm clientForm) {
/*  706 */     Preferences clientFormNode = getClientFormNode();
/*      */ 
/*  708 */     byte[] boundsRaw = clientFormNode.getByteArray("Bounds", null);
/*  709 */     if ((boundsRaw != null) && (boundsRaw.length > 0)) {
/*      */       try {
/*  711 */         clientForm.setBounds((Rectangle)bytesToObject(boundsRaw));
/*      */       } catch (Exception ex) {
/*  713 */         LOGGER.warn(new StringBuilder().append("Unable to restore client form bounds : ").append(ex.getMessage()).toString(), ex);
/*      */       }
/*      */     }
/*  716 */     if (clientFormNode.getBoolean("Maximized", true))
/*  717 */       clientForm.setExtendedState(clientForm.getExtendedState() | 0x6);
/*      */   }
/*      */ 
/*      */   public void restore(DealPanel dealPanel)
/*      */   {
/*  722 */     Preferences dealPanelNode = getDealPanelNode();
/*      */ 
/*  724 */     if ((!dealPanelNode.getBoolean("ConditionalOrdersExpanded", true)) && (dealPanel.getOrderEntryPanel().isConditionalOrdersPanelExpanded())) {
/*  725 */       dealPanel.getOrderEntryPanel().switchConditionOrderVisibility();
/*      */     }
/*      */ 
/*  728 */     if (!dealPanelNode.getBoolean("MarketDepthExpanded", true)) {
/*  729 */       dealPanel.getMarketDepthPanel().switchVisibility();
/*      */     }
/*      */ 
/*  732 */     if (!dealPanelNode.getBoolean("OrderEntryExpanded", true)) {
/*  733 */       dealPanel.getOrderEntryPanel().switchVisibility();
/*      */     }
/*      */ 
/*  736 */     if ((dealPanel.getWorkspacePanel() instanceof TickerPanel)) {
/*  737 */       if (!dealPanelNode.getBoolean("TickerExpanded", true))
/*  738 */         ((TickerPanel)dealPanel.getWorkspacePanel()).switchVisibility();
/*      */     }
/*  740 */     else if (((dealPanel.getWorkspacePanel() instanceof WorkspaceTreePanel)) && 
/*  741 */       (!dealPanelNode.getBoolean("WorkspaceTreeExpanded", true)))
/*  742 */       ((WorkspaceTreePanel)dealPanel.getWorkspacePanel()).switchVisibility();
/*      */   }
/*      */ 
/*      */   public void save(DealPanel dealPanel)
/*      */   {
/*  748 */     Preferences dealPanelNode = getDealPanelNode();
/*      */ 
/*  750 */     dealPanelNode.putBoolean("ConditionalOrdersExpanded", dealPanel.getOrderEntryPanel().isConditionalOrdersPanelExpanded());
/*      */ 
/*  752 */     boolean isMarketDepthExpanded = dealPanel.getMarketDepthPanel().isExpanded();
/*      */ 
/*  754 */     dealPanelNode.putBoolean("MarketDepthExpanded", isMarketDepthExpanded);
/*      */ 
/*  762 */     dealPanelNode.putBoolean("OrderEntryExpanded", dealPanel.getOrderEntryPanel().isExpanded());
/*      */ 
/*  764 */     if ((dealPanel.getWorkspacePanel() instanceof TickerPanel))
/*  765 */       dealPanelNode.putBoolean("TickerExpanded", ((TickerPanel)dealPanel.getWorkspacePanel()).isExpanded());
/*  766 */     else if ((dealPanel.getWorkspacePanel() instanceof WorkspaceTreePanel))
/*  767 */       dealPanelNode.putBoolean("WorkspaceTreeExpanded", ((WorkspaceTreePanel)dealPanel.getWorkspacePanel()).isExpanded());
/*      */   }
/*      */ 
/*      */   public void saveMOsize(Dimension size)
/*      */   {
/*  773 */     Preferences node = getClientDefaultsNode();
/*  774 */     node.putInt("moWidth", (int)size.getWidth());
/*  775 */     node.putInt("moHeight", (int)size.getHeight());
/*      */   }
/*      */ 
/*      */   public void saveMOlocation(Point location) {
/*  779 */     Preferences node = getClientDefaultsNode();
/*  780 */     node.putInt("moLocationX", location.x);
/*  781 */     node.putInt("moLocationY", location.y);
/*      */   }
/*      */ 
/*      */   public Dimension restoreMOsize() {
/*      */     try {
/*  786 */       Preferences node = getClientDefaultsNode();
/*  787 */       int width = node.getInt("moWidth", 500);
/*  788 */       int height = node.getInt("moHeight", 500);
/*  789 */       return new Dimension(width, height);
/*      */     } catch (Exception e) {
/*  791 */       LOGGER.error(e.getMessage(), e);
/*      */     }
/*  793 */     return new Dimension(500, 500);
/*      */   }
/*      */ 
/*      */   public Point restoreMOlocation() {
/*      */     try {
/*  798 */       Preferences node = getClientDefaultsNode();
/*  799 */       int x = node.getInt("moLocationX", -1);
/*  800 */       int y = node.getInt("moLocationY", -1);
/*      */ 
/*  802 */       if ((x == -1) || (y == -1)) {
/*  803 */         return null;
/*      */       }
/*  805 */       return new Point(x, y);
/*      */     }
/*      */     catch (Exception e) {
/*  808 */       LOGGER.error(e.getMessage(), e);
/*      */     }
/*  810 */     return null;
/*      */   }
/*      */ 
/*      */   public BigDecimal restoreDefaultAmount() {
/*      */     try {
/*  815 */       Preferences node = getCommonDefaultsNode();
/*  816 */       BigDecimal val = BigDecimal.valueOf(node.getDouble("amount", LotAmountChanger.getDefaultAmount4CurrentLot().doubleValue()));
/*  817 */       if (LotAmountChanger.getMaxTradableAmount().compareTo(val) == -1) {
/*  818 */         return LotAmountChanger.getDefaultAmount4CurrentLot();
/*      */       }
/*  820 */       if (LotAmountChanger.getMinTradableAmount().compareTo(val) != -1) {
/*  821 */         return LotAmountChanger.getMinTradableAmount();
/*      */       }
/*  823 */       return val;
/*      */     } catch (Exception e) {
/*  825 */       LOGGER.error(e.getMessage(), e);
/*      */     }
/*  827 */     return LotAmountChanger.getDefaultAmount4CurrentLot();
/*      */   }
/*      */ 
/*      */   public BigDecimal restoreDefaultXAUAmount() {
/*      */     try {
/*  832 */       Preferences node = getCommonDefaultsNode();
/*  833 */       BigDecimal value = BigDecimal.valueOf(node.getDouble("xau_amount", ClientSettingsStorage.DEFAULT_XAU_AMOUNT_VALUE));
/*  834 */       if (value.compareTo(GuiUtilsAndConstants.ONE) < 0) {
/*  835 */         value = GuiUtilsAndConstants.ONE;
/*      */       }
/*  837 */       return value;
/*      */     } catch (Exception e) {
/*  839 */       LOGGER.error(e.getMessage(), e);
/*      */     }
/*  841 */     return GuiUtilsAndConstants.ONE;
/*      */   }
/*      */ 
/*      */   public BigDecimal restoreDefaultXAGAmount() {
/*      */     try {
/*  846 */       Preferences node = getCommonDefaultsNode();
/*  847 */       BigDecimal value = BigDecimal.valueOf(node.getDouble("xag_amount", ClientSettingsStorage.DEFAULT_XAG_AMOUNT_VALUE));
/*  848 */       if (value.compareTo(GuiUtilsAndConstants.MIN_XAG_AMOUNT) < 0) {
/*  849 */         value = GuiUtilsAndConstants.MIN_XAG_AMOUNT;
/*      */       }
/*  851 */       return value;
/*      */     } catch (Exception e) {
/*  853 */       LOGGER.error(e.getMessage(), e);
/*      */     }
/*  855 */     return GuiUtilsAndConstants.MIN_XAG_AMOUNT;
/*      */   }
/*      */ 
/*      */   public BigDecimal restoreAmountLot() {
/*      */     try {
/*  860 */       Preferences node = getCommonDefaultsNode();
/*  861 */       return BigDecimal.valueOf(node.getInt("amountLot", ClientSettingsStorage.DEFAULT_AMOUNT_LOT_VALUE));
/*      */     } catch (Exception e) {
/*  863 */       LOGGER.error(e.getMessage(), e);
/*      */     }
/*  865 */     return BigDecimal.valueOf(ClientSettingsStorage.DEFAULT_AMOUNT_LOT_VALUE);
/*      */   }
/*      */ 
/*      */   public BigDecimal restoreDefaultSlippage() {
/*      */     try {
/*  870 */       Preferences node = getCommonDefaultsNode();
/*  871 */       return BigDecimal.valueOf(node.getDouble("slippageVal", ClientSettingsStorage.DEFAULT_SLIPPAGE_VALUE));
/*      */     } catch (Exception e) {
/*  873 */       LOGGER.error(e.getMessage(), e);
/*      */     }
/*  875 */     return GuiUtilsAndConstants.FIFE;
/*      */   }
/*      */ 
/*      */   public BigDecimal restoreDefaultStopLossOffset() {
/*      */     try {
/*  880 */       Preferences node = getCommonDefaultsNode();
/*  881 */       BigDecimal value = BigDecimal.valueOf(node.getDouble("slVal", ClientSettingsStorage.DEFAULT_STOP_LOSS_OFFSET_VALUE));
/*  882 */       if (value.compareTo(GuiUtilsAndConstants.ONE) < 0) {
/*  883 */         value = GuiUtilsAndConstants.TEN;
/*      */       }
/*  885 */       return value;
/*      */     } catch (Exception e) {
/*  887 */       LOGGER.error(e.getMessage(), e);
/*      */     }
/*  889 */     return GuiUtilsAndConstants.TEN;
/*      */   }
/*      */ 
/*      */   public BigDecimal restoreDefaultTakeProfitOffset() {
/*      */     try {
/*  894 */       Preferences node = getCommonDefaultsNode();
/*  895 */       BigDecimal value = BigDecimal.valueOf(node.getDouble("tpVal", ClientSettingsStorage.DEFAULT_TAKE_PROFIT_OFFSET_VALUE));
/*  896 */       if (value.compareTo(GuiUtilsAndConstants.ONE) < 0) {
/*  897 */         value = GuiUtilsAndConstants.TEN;
/*      */       }
/*  899 */       return value;
/*      */     } catch (Exception e) {
/*  901 */       LOGGER.error(e.getMessage(), e);
/*      */     }
/*  903 */     return GuiUtilsAndConstants.TEN;
/*      */   }
/*      */ 
/*      */   public BigDecimal restoreDefaultOpenIfOffset() {
/*      */     try {
/*  908 */       Preferences node = getCommonDefaultsNode();
/*  909 */       return BigDecimal.valueOf(node.getDouble("entryVal", ClientSettingsStorage.DEFAULT_OPEN_IF_OFFSET_VALUE));
/*      */     } catch (Exception e) {
/*  911 */       LOGGER.error(e.getMessage(), e);
/*      */     }
/*  913 */     return GuiUtilsAndConstants.TEN;
/*      */   }
/*      */ 
/*      */   public BigDecimal restoreDefaultTrailingStep() {
/*      */     try {
/*  918 */       Preferences node = getCommonDefaultsNode();
/*  919 */       return BigDecimal.valueOf(node.getDouble("trailingStepVal", ClientSettingsStorage.DEFAULT_TRAILING_STEP_VALUE));
/*      */     } catch (Exception e) {
/*  921 */       LOGGER.error(e.getMessage(), e);
/*      */     }
/*  923 */     return GuiUtilsAndConstants.TEN;
/*      */   }
/*      */ 
/*      */   public BigDecimal restoreOrderValidityTime() {
/*      */     try {
/*  928 */       Preferences node = getCommonDefaultsNode();
/*  929 */       return BigDecimal.valueOf(node.getDouble("orderValidaty", ClientSettingsStorage.DEFAULT_ORDER_VALIDATY_VALUE));
/*      */     } catch (Exception e) {
/*  931 */       LOGGER.error(e.getMessage(), e);
/*      */     }
/*  933 */     return GuiUtilsAndConstants.TEN;
/*      */   }
/*      */ 
/*      */   public Integer restoreDefaultOrderValTimeUnit() {
/*      */     try {
/*  938 */       Preferences node = getCommonDefaultsNode();
/*  939 */       return Integer.valueOf(node.getInt("orderValidatyTimeUnit", ClientSettingsStorage.DEFAULT_VALIDATY_TIME_UNIT_VALUE));
/*      */     }
/*      */     catch (Exception e)
/*      */     {
/*  943 */       LOGGER.error(e.getMessage(), e);
/*      */     }
/*  945 */     return Integer.valueOf(GuiUtilsAndConstants.ONE.intValue());
/*      */   }
/*      */ 
/*      */   public void saveSpotAtMarket(boolean isVisible)
/*      */   {
/*  950 */     Preferences node = getSpamNode();
/*  951 */     node.putBoolean("visible", isVisible);
/*      */   }
/*      */ 
/*      */   public boolean restoreSpotAtMarket() {
/*  955 */     Preferences node = getSpamNode();
/*  956 */     return node.getBoolean("visible", true);
/*      */   }
/*      */ 
/*      */   public void saveSplitPane(ExpandableSplitPane splitPane) {
/*  960 */     Preferences node = getPanesNode().node(splitPane.getName());
/*  961 */     node.putBoolean("minimized", splitPane.isMinimized());
/*  962 */     node.putBoolean("maximized", splitPane.isMaximized());
/*  963 */     node.putInt("location", splitPane.getDividerLocation());
/*      */   }
/*      */ 
/*      */   public void restoreSplitPane(ExpandableSplitPane splitPane, double defaultLocation) {
/*  967 */     Preferences node = getPanesNode().node(splitPane.getName());
/*  968 */     if (node.getBoolean("minimized", false)) {
/*  969 */       splitPane.minimize();
/*  970 */     } else if (node.getBoolean("maximized", false)) {
/*  971 */       splitPane.maximize();
/*      */     } else {
/*  973 */       int dividerLocatation = node.getInt("location", -1);
/*  974 */       if (dividerLocatation >= 0)
/*  975 */         splitPane.setDividerLocation(dividerLocatation);
/*      */       else
/*  977 */         splitPane.setDividerLocation(defaultLocation);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void saveInstrumenTabs(MarketOverviewConfig marketOverviewConfig, int selectedTab)
/*      */   {
/*      */     try {
/*  984 */       getInstrumentsNode().removeNode();
/*  985 */       getUserRoot().flush();
/*      */     } catch (BackingStoreException bse) {
/*  987 */       LOGGER.error(bse.getMessage(), bse);
/*  988 */       return;
/*      */     }
/*      */     Preferences tabNode;
/*      */     int index;
/*  991 */     for (int i = 0; i < marketOverviewConfig.getTabs().size(); i++) {
/*  992 */       TabConfig tab = (TabConfig)marketOverviewConfig.getTabs().get(i);
/*      */ 
/*  994 */       tabNode = getInstrumentsNode().node(String.valueOf(i));
/*      */ 
/*  996 */       tabNode.put("title", tab.getTabName());
/*  997 */       tabNode.put("tabIndexKey", String.valueOf(i));
/*      */ 
/*  999 */       tabNode.putBoolean("selected", i == selectedTab);
/* 1000 */       index = 0;
/*      */ 
/* 1002 */       for (MiniPanelConfig mpc : tab.getInstrumentList()) {
/* 1003 */         String instrument = mpc.getInstrument();
/*      */ 
/* 1005 */         Preferences node = tabNode.node(String.valueOf(index));
/* 1006 */         node.put("instrumentName", instrument.replace("/", "*"));
/* 1007 */         node.put("instrumentIndex", String.valueOf(index));
/* 1008 */         index++;
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   public void clearInstrumentsTabs() {
/*      */     try {
/* 1015 */       getInstrumentsNode().removeNode();
/* 1016 */       getUserRoot().flush();
/*      */     } catch (BackingStoreException bse) {
/* 1018 */       LOGGER.error(bse.getMessage(), bse);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void saveSelectedInstruments(Set<String> selectedInstruments) {
/*      */     try {
/* 1024 */       getSelectedInstrumentsNode().removeNode();
/* 1025 */       getUserRoot().flush();
/*      */     } catch (BackingStoreException bse) {
/* 1027 */       LOGGER.error(bse.getMessage(), bse);
/* 1028 */       return;
/*      */     }
/*      */ 
/* 1031 */     LOGGER.debug(new StringBuilder().append("saving selected instruments: ").append(selectedInstruments).toString());
/*      */ 
/* 1034 */     Preferences instrumentNode = getSelectedInstrumentsNode();
/* 1035 */     for (String instrument : selectedInstruments) {
/* 1036 */       if (instrument.indexOf(47) > 0) {
/* 1037 */         instrumentNode.putBoolean(instrument, true);
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 1042 */     MarketView marketView = (MarketView)GreedContext.get("marketView");
/* 1043 */     Set allIntruments = marketView.getAllInstruments();
/* 1044 */     allIntruments.removeAll(selectedInstruments);
/* 1045 */     for (String instrument : allIntruments)
/* 1046 */       instrumentNode.putBoolean(instrument, false);
/*      */   }
/*      */ 
/*      */   public List<String> restoreSelectedInstruments(List<String> instrumentsList)
/*      */   {
/* 1051 */     if (instrumentsList.isEmpty()) {
/* 1052 */       for (int i = 0; i < getDefaultInstrumentList().length; i++) {
/* 1053 */         instrumentsList.add(getDefaultInstrumentList()[i]);
/*      */       }
/*      */     }
/*      */ 
/* 1057 */     Collections.sort(instrumentsList, new SimpleAlphabeticInstrumentComparator());
/*      */ 
/* 1059 */     return instrumentsList;
/*      */   }
/*      */ 
/*      */   public void saveLastSelectedInstrument(String instr) {
/* 1063 */     Preferences commonNode = getCommonDefaultsNode();
/* 1064 */     commonNode.put("lastSelectedInstrument", instr.toString());
/*      */   }
/*      */ 
/*      */   public String restoreLastSelectedInstrument() {
/* 1068 */     Preferences commonNode = getCommonDefaultsNode();
/* 1069 */     String instr = commonNode.get("lastSelectedInstrument", Instrument.EURUSD.toString());
/* 1070 */     if ((instr == null) || (!InstrumentAvailabilityManager.getInstance().isAllowed(instr)))
/*      */     {
/* 1072 */       instr = Instrument.EURUSD.toString();
/*      */     }
/* 1074 */     return instr;
/*      */   }
/*      */ 
/*      */   public void cleanAllSelectedInstruments() {
/* 1078 */     saveSelectedInstruments(Collections.emptySet());
/*      */   }
/*      */ 
/*      */   public MarketOverviewConfig restoreInstrumentTabs(MarketOverviewConfig marketOverviewConfig) {
/* 1082 */     List tabNodes = getChildren(getInstrumentsNode());
/*      */     TabConfig tabConfig;
/*      */     MiniPanelConfig mpc;
/* 1083 */     if (0 == tabNodes.size()) {
/* 1084 */       TabConfig tabConfig = TabConfig.getConfig("Tab 1", true);
/* 1085 */       MiniPanelConfig mpc = null;
/*      */ 
/* 1087 */       for (String instr : getDefaultInstrumentList()) {
/* 1088 */         if (instr == null) continue;
/*      */         try {
/* 1090 */           mpc = MiniPanelConfig.getConfig(instr);
/* 1091 */           tabConfig.getInstrumentList().add(mpc);
/*      */         } catch (Exception e) {
/* 1093 */           LOGGER.error(e.getMessage(), e);
/*      */         }
/*      */       }
/*      */ 
/* 1097 */       marketOverviewConfig.getTabs().add(tabConfig);
/*      */     } else {
/* 1099 */       tabConfig = null;
/* 1100 */       mpc = null;
/*      */ 
/* 1102 */       for (Preferences tabNode : tabNodes) {
/* 1103 */         tabConfig = TabConfig.getConfig();
/* 1104 */         String tabName = tabNode.get("title", null);
/* 1105 */         String tabIndex = tabNode.get("tabIndexKey", null);
/*      */ 
/* 1107 */         if (tabNode.getBoolean("selected", false)) {
/* 1108 */           tabConfig.setLastActive(true);
/*      */         }
/*      */ 
/* 1111 */         tabConfig.setTabName(tabName);
/* 1112 */         tabConfig.setIndex(Integer.valueOf(tabIndex).intValue());
/*      */ 
/* 1114 */         List instrumentNodes = getChildren(tabNode);
/*      */ 
/* 1116 */         if (0 != instrumentNodes.size()) {
/* 1117 */           SortedMap instrumentSortedMap = new TreeMap();
/*      */ 
/* 1119 */           int index = 0;
/* 1120 */           boolean wrong_settings = false;
/* 1121 */           for (Preferences instrumentNode : instrumentNodes)
/*      */           {
/* 1123 */             String name = instrumentNode.get("instrumentName", null).replace("*", "/");
/* 1124 */             index = Integer.parseInt(instrumentNode.get("instrumentIndex", "-1"));
/*      */ 
/* 1126 */             if ((-1 != index) && (!wrong_settings)) {
/* 1127 */               instrumentSortedMap.put(Integer.valueOf(index), name);
/*      */             } else {
/* 1129 */               wrong_settings = true;
/*      */ 
/* 1131 */               mpc = MiniPanelConfig.getConfig(name);
/* 1132 */               tabConfig.getInstrumentList().add(mpc);
/*      */             }
/*      */ 
/*      */           }
/*      */ 
/* 1137 */           if (!wrong_settings) {
/* 1138 */             int i = 0; for (int n = instrumentSortedMap.size(); i < n; i++) {
/* 1139 */               String instr = (String)instrumentSortedMap.get(Integer.valueOf(i));
/* 1140 */               if (null != instr) {
/* 1141 */                 mpc = MiniPanelConfig.getConfig(instr);
/* 1142 */                 tabConfig.getInstrumentList().add(mpc);
/*      */               }
/*      */             }
/*      */           }
/*      */         }
/*      */ 
/* 1148 */         marketOverviewConfig.addTab(tabConfig);
/*      */       }
/*      */     }
/* 1151 */     return marketOverviewConfig;
/*      */   }
/*      */ 
/*      */   public int getSelectedInstrumentTabIndex() {
/* 1155 */     List tabNodes = getChildren(getInstrumentsNode());
/* 1156 */     for (int i = 0; i < tabNodes.size(); i++) {
/* 1157 */       if (((Preferences)tabNodes.get(i)).getBoolean("selected", false)) {
/* 1158 */         return i;
/*      */       }
/*      */     }
/* 1161 */     return 0;
/*      */   }
/*      */ 
/*      */   public void saveDetachedLocation(String instrument, Point location) {
/* 1165 */     Preferences node = getDetachedNode().node(instrument.replace("/", "*"));
/* 1166 */     node.put("instrument", instrument.replace("/", "*"));
/* 1167 */     node.putDouble("deatched_x", location.getX());
/* 1168 */     node.putDouble("deatched_y", location.getY());
/*      */   }
/*      */ 
/*      */   public Point restoreDetachedLocation(String instrument) {
/* 1172 */     List detachedList = getChildren(getDetachedNode());
/* 1173 */     for (Preferences node : detachedList) {
/* 1174 */       String instr = node.get("instrument", null).replace("*", "/");
/* 1175 */       if (instr.equalsIgnoreCase(instrument)) {
/* 1176 */         return new Point((int)node.getDouble("deatched_x", 100.0D), (int)node.getDouble("deatched_y", 100.0D));
/*      */       }
/*      */     }
/* 1179 */     return null;
/*      */   }
/*      */ 
/*      */   public void saveFrameDimension(ClientSettingsStorage.FrameType frameType, Dimension dimension) {
/* 1183 */     Preferences node = getDimensionsNode();
/*      */     try {
/* 1185 */       if (dimension == null)
/* 1186 */         node.remove(frameType.name());
/*      */       else
/* 1188 */         node.putByteArray(frameType.name(), object2Bytes(dimension));
/*      */     }
/*      */     catch (Exception ex) {
/* 1191 */       LOGGER.warn(new StringBuilder().append("Unable to save [").append(frameType).append("] frame dimension").toString(), ex);
/*      */     }
/* 1193 */     flush(node);
/*      */   }
/*      */ 
/*      */   public Dimension restoreFrameDimension(ClientSettingsStorage.FrameType frameType) {
/* 1197 */     Preferences node = getDimensionsNode();
/*      */     try {
/* 1199 */       byte[] raw = node.getByteArray(frameType.name(), null);
/* 1200 */       if (raw != null)
/* 1201 */         return (Dimension)bytesToObject(raw);
/*      */     }
/*      */     catch (Exception ex) {
/* 1204 */       LOGGER.warn(new StringBuilder().append("Unable to restore [").append(frameType).append("] frame dimension").toString(), ex);
/*      */     }
/*      */ 
/* 1207 */     return null;
/*      */   }
/*      */ 
/*      */   private Preferences getClientNode()
/*      */   {
/* 1213 */     return getUserRoot().node("client");
/*      */   }
/*      */ 
/*      */   private Preferences getClientDefaultsNode() {
/* 1217 */     return getClientNode().node("Defaults");
/*      */   }
/*      */ 
/*      */   private Preferences getDealPanelNode() {
/* 1221 */     return getClientNode().node("DealPanel");
/*      */   }
/*      */ 
/*      */   private Preferences getSpamNode() {
/* 1225 */     return getClientNode().node("SpotAtMarket");
/*      */   }
/*      */ 
/*      */   private Preferences getPanesNode() {
/* 1229 */     return getClientNode().node("Panes");
/*      */   }
/*      */ 
/*      */   Preferences getSelectedInstrumentsNode() {
/* 1233 */     return getClientNode().node("selected_instruments");
/*      */   }
/*      */ 
/*      */   private Preferences getInstrumentsNode() {
/* 1237 */     return getClientNode().node("InstrumentsNode");
/*      */   }
/*      */ 
/*      */   private Preferences getClientFormNode() {
/* 1241 */     return getClientNode().node("ClientForm");
/*      */   }
/*      */ 
/*      */   private Preferences getDimensionsNode() {
/* 1245 */     return getClientNode().node("Dimensions");
/*      */   }
/*      */ 
/*      */   private List<Preferences> getChildren(Preferences node) {
/* 1249 */     List children = new ArrayList();
/*      */     try
/*      */     {
/* 1252 */       for (String childName : node.childrenNames())
/* 1253 */         children.add(node.node(childName));
/*      */     }
/*      */     catch (BackingStoreException bse) {
/* 1256 */       LOGGER.error(bse.getMessage(), bse);
/*      */     }
/*      */ 
/* 1259 */     return children;
/*      */   }
/*      */ 
/*      */   public void saveStartegiesDiscState(boolean isAccepted)
/*      */   {
/* 1322 */     Preferences node = getJForexNode();
/* 1323 */     node.putBoolean("StartegiesDiscAccepted", isAccepted);
/*      */   }
/*      */ 
/*      */   public boolean restoreStartegiesDiscState() {
/* 1327 */     Preferences node = getJForexNode();
/* 1328 */     return node.getBoolean("StartegiesDiscAccepted", false);
/*      */   }
/*      */ 
/*      */   public void saveRemoteStartegiesDiscState(boolean isAccepted) {
/* 1332 */     Preferences node = getJForexNode();
/* 1333 */     node.putBoolean("RemoteStartegiesDiscAccepted", isAccepted);
/*      */   }
/*      */ 
/*      */   public boolean restoreRemoteStartegiesDiscState() {
/* 1337 */     Preferences node = getJForexNode();
/* 1338 */     return node.getBoolean("RemoteStartegiesDiscAccepted", false);
/*      */   }
/*      */ 
/*      */   public boolean restoreTesterDiscState() {
/* 1342 */     Preferences node = getJForexNode();
/* 1343 */     return node.getBoolean("TesterDiscAccepted", false);
/*      */   }
/*      */ 
/*      */   public void saveTesterDiscState(boolean isAccepted) {
/* 1347 */     Preferences node = getJForexNode();
/* 1348 */     node.putBoolean("TesterDiscAccepted", isAccepted);
/*      */   }
/*      */ 
/*      */   public void saveFullAccessDiscState(boolean isAccepted) {
/* 1352 */     Preferences node = getJForexNode();
/* 1353 */     node.putBoolean("FullAccessDiscAccepted", isAccepted);
/*      */   }
/*      */ 
/*      */   public boolean restoreFullAccessDiscState() {
/* 1357 */     Preferences node = getJForexNode();
/* 1358 */     return node.getBoolean("FullAccessDiscAccepted", false);
/*      */   }
/*      */ 
/*      */   public List<StrategyNewBean> getStrategyNewBeans()
/*      */   {
/* 1363 */     Preferences strategiesNode = getStrategiesNode();
/*      */ 
/* 1365 */     String[] ids = getChildrenNames(strategiesNode);
/* 1366 */     if ((ids == null) || (ids.length == 0)) {
/* 1367 */       return new ArrayList();
/*      */     }
/*      */ 
/* 1370 */     IStrategyPresetsController presetsController = new DefaultStrategyPresetsController();
/*      */ 
/* 1372 */     List strategyBeans = new ArrayList();
/*      */ 
/* 1374 */     for (String strategyId : ids)
/*      */     {
/* 1376 */       Preferences curNode = strategiesNode.node(strategyId);
/* 1377 */       String binaryFileName = curNode.get("strategyBinaryFileName", null);
/* 1378 */       String sourceFileName = curNode.get("strategySourceFileName", null);
/* 1379 */       String type = curNode.get("strategyType", StrategyType.LOCAL.name());
/* 1380 */       String presetId = curNode.get("strategyPreset", "DEFAULT_PRESET_ID");
/* 1381 */       String remotePid = curNode.get("remoteProcessId", null);
/* 1382 */       String remoteRequestId = curNode.get("remoteRequestId", null);
/* 1383 */       String comments = curNode.get("strategyComments", "");
/*      */ 
/* 1385 */       StrategyNewBean bean = new StrategyNewBean();
/*      */ 
/* 1387 */       IdManager.getInstance().reserveServiceId(Integer.valueOf(strategyId).intValue());
/* 1388 */       bean.setId(Integer.valueOf(strategyId));
/*      */ 
/* 1390 */       bean.setType(StrategyType.getByName(type));
/* 1391 */       bean.setStatus(StrategyStatus.STOPPED);
/* 1392 */       bean.setComments(comments);
/*      */ 
/* 1394 */       if (bean.getType().equals(StrategyType.REMOTE)) {
/* 1395 */         bean.setRemoteProcessId(remotePid);
/* 1396 */         bean.setRemoteRequestId(remoteRequestId);
/*      */       }
/*      */ 
/* 1399 */       if (sourceFileName != null) {
/* 1400 */         File sourceFile = new File(sourceFileName);
/* 1401 */         if (sourceFile.exists()) {
/* 1402 */           bean.setStrategySourceFile(sourceFile);
/* 1403 */           bean.setName(sourceFile.getName().substring(0, sourceFile.getName().lastIndexOf(46)));
/*      */         }
/*      */       }
/*      */ 
/* 1407 */       if (binaryFileName != null) {
/* 1408 */         File binaryFile = new File(binaryFileName);
/* 1409 */         if (binaryFile.exists())
/*      */         {
/* 1411 */           if (bean.getName() == null) {
/* 1412 */             bean.setName(binaryFile.getName().substring(0, binaryFile.getName().lastIndexOf(46)));
/*      */           }
/*      */           try
/*      */           {
/* 1416 */             StrategyBinaryLoader.loadStrategy(binaryFile, bean);
/*      */           } catch (IncorrectClassTypeException ex) {
/* 1418 */             LOGGER.error(new StringBuilder().append("An attempt to load strategy from non-strategy file ").append(binaryFile.getName()).toString(), ex);
/* 1419 */             continue;
/*      */           }
/*      */ 
/* 1422 */           List strategyPresets = presetsController.loadPresets(bean);
/* 1423 */           bean.setStrategyPresets(strategyPresets);
/*      */ 
/* 1425 */           StrategyPreset activePreset = presetsController.getStrategyPresetBy(strategyPresets, presetId);
/* 1426 */           if (activePreset == null) {
/* 1427 */             activePreset = presetsController.getStrategyPresetBy(strategyPresets, "DEFAULT_PRESET_ID");
/*      */           }
/* 1429 */           bean.setActivePreset(activePreset);
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/* 1434 */       if ((bean.getStrategyBinaryFile() != null) || (bean.getStrategySourceFile() != null)) {
/* 1435 */         strategyBeans.add(bean);
/*      */       }
/*      */       else {
/* 1438 */         removeNode(curNode);
/*      */       }
/*      */     }
/*      */ 
/* 1442 */     return strategyBeans;
/*      */   }
/*      */ 
/*      */   public void saveStrategyNewBean(StrategyNewBean strategyBean) {
/* 1446 */     if (strategyBean != null) {
/* 1447 */       Preferences strategiesNode = getStrategiesNode();
/* 1448 */       Preferences strategyBeanNode = strategiesNode.node(String.valueOf(strategyBean.getId()));
/*      */ 
/* 1450 */       if (strategyBean.getStrategyBinaryFile() != null) {
/* 1451 */         strategyBeanNode.put("strategyBinaryFileName", strategyBean.getStrategyBinaryFile().getAbsolutePath());
/*      */       }
/* 1453 */       if (strategyBean.getStrategySourceFile() != null) {
/* 1454 */         strategyBeanNode.put("strategySourceFileName", strategyBean.getStrategySourceFile().getAbsolutePath());
/*      */       }
/*      */ 
/* 1457 */       if (strategyBean.getActivePreset() != null) {
/* 1458 */         strategyBeanNode.put("strategyPreset", strategyBean.getActivePreset().getId());
/*      */       }
/* 1460 */       strategyBeanNode.put("strategyType", strategyBean.getType().name());
/*      */ 
/* 1462 */       if (strategyBean.getType().equals(StrategyType.REMOTE)) {
/* 1463 */         if (strategyBean.getRemoteProcessId() != null)
/* 1464 */           strategyBeanNode.put("remoteProcessId", strategyBean.getRemoteProcessId());
/*      */         else {
/* 1466 */           strategyBeanNode.remove("remoteProcessId");
/*      */         }
/* 1468 */         if (strategyBean.getRemoteRequestId() != null)
/* 1469 */           strategyBeanNode.put("remoteRequestId", strategyBean.getRemoteRequestId());
/*      */         else {
/* 1471 */           strategyBeanNode.remove("remoteRequestId");
/*      */         }
/*      */       }
/*      */ 
/* 1475 */       if (strategyBean.getComments() != null) {
/* 1476 */         strategyBeanNode.put("strategyComments", strategyBean.getComments());
/*      */       }
/*      */ 
/* 1479 */       flush(strategiesNode);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void removeStrategyNewBean(StrategyNewBean strategyBean) {
/* 1484 */     Preferences strategiesNode = getStrategiesNode();
/* 1485 */     Preferences strategyBeanNode = strategiesNode.node(String.valueOf(strategyBean.getId()));
/* 1486 */     removeNode(strategyBeanNode);
/* 1487 */     flush(strategiesNode);
/*      */   }
/*      */ 
/*      */   public List<CustomIndicatorBean> getCustomIndicatorBeans()
/*      */   {
/* 1584 */     Preferences customIndicatorsNode = getCustomIndicatorsNode();
/* 1585 */     String[] ids = getChildrenNames(customIndicatorsNode);
/* 1586 */     if ((ids == null) || (ids.length == 0)) {
/* 1587 */       return Collections.emptyList();
/*      */     }
/* 1589 */     LinkedList beans = new LinkedList();
/* 1590 */     for (String customIndicatorId : ids) {
/* 1591 */       Preferences node = customIndicatorsNode.node(customIndicatorId);
/* 1592 */       String sourceFullFileName = node.get("custIndSourceFileName", null);
/* 1593 */       String binaryFullFileName = node.get("custIndBinaryFileName", null);
/* 1594 */       if (strategyFilesDontExist(sourceFullFileName, binaryFullFileName)) {
/*      */         continue;
/*      */       }
/* 1597 */       CustomIndicatorBean bean = new CustomIndicatorBean(Integer.valueOf(customIndicatorId).intValue(), sourceFullFileName, binaryFullFileName);
/* 1598 */       Preferences framePreferences = getMainFramePreferences(customIndicatorId);
/* 1599 */       bean.setEditable(containsData(framePreferences));
/* 1600 */       beans.add(bean);
/*      */     }
/*      */ 
/* 1603 */     return beans;
/*      */   }
/*      */ 
/*      */   public CustomIndicatorBean getCustomIndicatorBean(int id) {
/* 1607 */     List customIndicatorBeans = getCustomIndicatorBeans();
/* 1608 */     for (CustomIndicatorBean customIndicatorBean : customIndicatorBeans) {
/* 1609 */       if (customIndicatorBean.getId().intValue() == id) {
/* 1610 */         return customIndicatorBean;
/*      */       }
/*      */     }
/* 1613 */     return null;
/*      */   }
/*      */ 
/*      */   public void save(CustomIndicatorBean customIndicatorBean) {
/* 1617 */     Preferences nodes = getCustomIndicatorsNode();
/* 1618 */     Preferences node = nodes.node(String.valueOf(customIndicatorBean.getId()));
/* 1619 */     node.put("custIndSourceFileName", customIndicatorBean.getSourceFullFileName());
/* 1620 */     node.put("custIndBinaryFileName", customIndicatorBean.getBinaryFullFileName());
/* 1621 */     flush(nodes);
/*      */   }
/*      */ 
/*      */   public void remove(CustomIndicatorBean customIndicatorBean) {
/* 1625 */     Preferences nodes = getCustomIndicatorsNode();
/* 1626 */     removeNode(nodes.node(String.valueOf(customIndicatorBean.getId())));
/* 1627 */     removeNode(getMainFramePreferences(Integer.toString(customIndicatorBean.getId().intValue())));
/* 1628 */     flush(nodes);
/* 1629 */     flush(getMainFramePreferencesNode());
/*      */   }
/*      */ 
/*      */   public List<EnabledIndicatorBean> getEnabledIndicators() {
/* 1633 */     Preferences customIndicatorsNode = getCustomEnabledIndicatorsNode();
/* 1634 */     String[] ids = getChildrenNames(customIndicatorsNode);
/* 1635 */     if ((ids == null) || (ids.length == 0)) {
/* 1636 */       return Collections.emptyList();
/*      */     }
/* 1638 */     LinkedList beans = new LinkedList();
/* 1639 */     for (String customIndicatorName : ids) {
/* 1640 */       Preferences node = customIndicatorsNode.node(customIndicatorName);
/* 1641 */       String sourceFullFileName = node.get("custIndSourceFileName", null);
/* 1642 */       String binaryFullFileName = node.get("custIndBinaryFileName", null);
/* 1643 */       EnabledIndicatorBean bean = new EnabledIndicatorBean(customIndicatorName, sourceFullFileName, binaryFullFileName);
/* 1644 */       beans.add(bean);
/*      */     }
/*      */ 
/* 1647 */     return beans;
/*      */   }
/*      */ 
/*      */   public void saveEnabledIndicator(EnabledIndicatorBean indicatorBean) {
/* 1651 */     Preferences nodes = getCustomEnabledIndicatorsNode();
/* 1652 */     Preferences node = nodes.node(indicatorBean.getName());
/* 1653 */     node.put("custIndSourceFileName", indicatorBean.getSourceFullFileName());
/* 1654 */     node.put("custIndBinaryFileName", indicatorBean.getBinaryFullFileName());
/* 1655 */     flush(nodes);
/*      */   }
/*      */ 
/*      */   public void removeEnabledIndicator(EnabledIndicatorBean indicatorBean) {
/* 1659 */     Preferences nodes = getCustomEnabledIndicatorsNode();
/* 1660 */     Preferences node = nodes.node(indicatorBean.getName());
/* 1661 */     removeNode(node);
/* 1662 */     flush(nodes);
/*      */   }
/*      */ 
/*      */   public void save(BottomPanelBean bottomPanelBean) {
/* 1666 */     Preferences framesNode = getBottomFramesNode();
/* 1667 */     framesNode.node(String.valueOf(bottomPanelBean.getPanelId()));
/* 1668 */     flush(framesNode);
/*      */   }
/*      */ 
/*      */   public void remove(BottomPanelBean bottomPanelBean) {
/* 1672 */     Preferences framesNode = getBottomFramesNode();
/* 1673 */     Preferences framePreferencesNode = getBottomFramePreferencesNode();
/* 1674 */     removeNode(framesNode.node(String.valueOf(bottomPanelBean.getPanelId())));
/* 1675 */     removeNode(framePreferencesNode.node(String.valueOf(bottomPanelBean.getPanelId())));
/* 1676 */     flush(framesNode);
/* 1677 */     flush(framePreferencesNode);
/*      */   }
/*      */ 
/*      */   public List<BottomPanelBean> getBottomPanelBeans() {
/* 1681 */     List bottomPanelBeans = new ArrayList();
/* 1682 */     Preferences framesNode = getBottomFramesNode();
/*      */     try {
/* 1684 */       String[] frameIds = framesNode.childrenNames();
/* 1685 */       for (String frameId : frameIds)
/* 1686 */         bottomPanelBeans.add(new BottomPanelBean(Integer.valueOf(frameId).intValue()));
/*      */     }
/*      */     catch (BackingStoreException e) {
/* 1689 */       LOGGER.warn("Failed to load some bottom panel frames", e);
/*      */     }
/* 1691 */     return bottomPanelBeans;
/*      */   }
/*      */ 
/*      */   public void setChartsExpanded(boolean isExpanded) {
/* 1695 */     saveExpandedState(isExpanded, "isChartsExpanded");
/*      */   }
/*      */ 
/*      */   public void setStrategiesExpanded(boolean isExpanded) {
/* 1699 */     saveExpandedState(isExpanded, "isStrategiesExpanded");
/*      */   }
/*      */ 
/*      */   public void setIndicatorsExpanded(boolean isExpanded) {
/* 1703 */     saveExpandedState(isExpanded, "isIndicatorsExpanded");
/*      */   }
/*      */ 
/*      */   public boolean isIndicatorsExpanded() {
/* 1707 */     return isExpanded("isIndicatorsExpanded");
/*      */   }
/*      */ 
/*      */   public boolean isChartsExpanded() {
/* 1711 */     return isExpanded("isChartsExpanded");
/*      */   }
/*      */ 
/*      */   public boolean isStrategiesExpanded() {
/* 1715 */     return isExpanded("isStrategiesExpanded");
/*      */   }
/*      */ 
/*      */   public boolean isBodySplitExpanded() {
/* 1719 */     return getJForexNode().getBoolean("bodySplitExpanded", true);
/*      */   }
/*      */ 
/*      */   public void setBodySplitExpanded(boolean value) {
/* 1723 */     getJForexNode().putBoolean("bodySplitExpanded", value);
/*      */   }
/*      */ 
/*      */   public void save(JFrame frame) {
/* 1727 */     if (frame == null) {
/* 1728 */       return;
/*      */     }
/* 1730 */     Preferences node = getChartsNode();
/*      */     try
/*      */     {
/* 1733 */       node.putByteArray("bounds", object2Bytes(frame.getBounds()));
/*      */     } catch (Exception ex) {
/* 1735 */       LOGGER.warn(new StringBuilder().append("Unable to save charts frame bounds : ").append(ex.getMessage()).toString());
/*      */     }
/*      */ 
/* 1738 */     flush(node);
/*      */   }
/*      */ 
/*      */   public boolean restore(Preferences framePreferencesNode, DockedUndockedFrame frame, boolean isFrameExpanded) {
/* 1742 */     Preferences node = framePreferencesNode.node(Integer.toString(frame.getPanelId()));
/* 1743 */     boolean isSelected = node.getBoolean("selected", false);
/* 1744 */     Rectangle bounds = getBounds(node);
/* 1745 */     if (bounds != null) {
/* 1746 */       frame.setBounds(bounds);
/*      */     }
/* 1748 */     if ((isSelected) && (isFrameExpanded))
/* 1749 */       frame.setMaximum(true);
/*      */     else {
/* 1751 */       frame.setMaximum(false);
/*      */     }
/* 1753 */     return isSelected;
/*      */   }
/*      */ 
/*      */   public void restore(JFrame frame) {
/* 1757 */     Preferences node = getChartsNode();
/*      */ 
/* 1759 */     byte[] boundsRaw = node.getByteArray("bounds", null);
/* 1760 */     if ((boundsRaw != null) && (boundsRaw.length > 0))
/*      */       try {
/* 1762 */         frame.setBounds((Rectangle)bytesToObject(boundsRaw));
/*      */       } catch (Exception ex) {
/* 1764 */         LOGGER.warn(new StringBuilder().append("Unable to restore charts frame bounds : ").append(ex.getMessage()).toString());
/*      */       }
/*      */   }
/*      */ 
/*      */   public void removeServiceEditor(Integer panelId)
/*      */   {
/* 1770 */     Preferences framePreferences = getMainFramePreferences(Integer.toString(panelId.intValue()));
/* 1771 */     removeNode(framePreferences);
/* 1772 */     flush(framePreferences);
/*      */   }
/*      */ 
/*      */   public List<LastUsedIndicatorBean> getLastUsedIndicatorNames() {
/* 1776 */     Preferences lastUsedIndicatorsPrefs = getLastUsedIndicatorsNode();
/*      */     try {
/* 1778 */       String[] indicatorNames = lastUsedIndicatorsPrefs.childrenNames();
/* 1779 */       SortedSet sortedIndicators = new TreeSet();
/* 1780 */       for (String indicatorName : indicatorNames) {
/* 1781 */         Preferences indicatorNameNode = lastUsedIndicatorsPrefs.node(indicatorName);
/* 1782 */         long time = indicatorNameNode.getLong("lastUsedDate", -9223372036854775808L);
/* 1783 */         if (time != -9223372036854775808L) {
/* 1784 */           Object[] indicatorParams = getIndicatorParams(indicatorNameNode);
/* 1785 */           OfferSide[] offerSides = getIndicatorOfferSide(indicatorNameNode);
/* 1786 */           IIndicators.AppliedPrice[] appliedPrices = getIndicatorAppliedPrices(indicatorNameNode);
/* 1787 */           Color[] outputColors = getIndicatorOutputColors(indicatorNameNode);
/* 1788 */           Color[] outputColors2 = getIndicatorOutputColors2(indicatorNameNode);
/* 1789 */           boolean[] valuesOnChart = new boolean[outputColors.length];
/* 1790 */           getValuesOnChart(indicatorNameNode, valuesOnChart);
/* 1791 */           boolean[] showOutputs = new boolean[outputColors.length];
/* 1792 */           getOutputs(indicatorNameNode, showOutputs);
/* 1793 */           float[] opacityAlphas = new float[outputColors.length];
/* 1794 */           getOpacityAlphas(indicatorNameNode, opacityAlphas);
/*      */ 
/* 1796 */           OutputParameterInfo.DrawingStyle[] drawingStyles = getIndicatorDrawingStyles(indicatorNameNode);
/* 1797 */           int[] lineWidths = getIndicatorLineWidths(indicatorNameNode);
/* 1798 */           int[] outputShifts = getIndicatorOutputShifts(indicatorNameNode);
/*      */ 
/* 1800 */           sortedIndicators.add(new LastUsedIndicatorBean(0, indicatorName, offerSides, appliedPrices, indicatorParams, outputColors, outputColors2, valuesOnChart, showOutputs, opacityAlphas, drawingStyles, lineWidths, outputShifts, time, null, null, false));
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/* 1821 */       if (sortedIndicators.size() > 10)
/*      */       {
/* 1823 */         int i = 10;
/* 1824 */         for (Iterator iterator = sortedIndicators.iterator(); iterator.hasNext(); i--) {
/* 1825 */           LastUsedIndicatorBean lastUsedIndicatorBean = (LastUsedIndicatorBean)iterator.next();
/* 1826 */           if (i <= 0) {
/* 1827 */             Preferences indicatorNameNode = lastUsedIndicatorsPrefs.node(lastUsedIndicatorBean.getName());
/* 1828 */             indicatorNameNode.removeNode();
/* 1829 */             iterator.remove();
/*      */           }
/*      */         }
/*      */       }
/* 1833 */       return new ArrayList(sortedIndicators);
/*      */     } catch (BackingStoreException e) {
/* 1835 */       LOGGER.error(e.getMessage(), e);
/* 1836 */     }return new ArrayList(0);
/*      */   }
/*      */ 
/*      */   public void addLastUsedIndicatorName(LastUsedIndicatorBean indicatorBean)
/*      */   {
/* 1841 */     Preferences lastUsedIndicatorsPrefs = getLastUsedIndicatorsNode();
/* 1842 */     Preferences indicatorNameNode = lastUsedIndicatorsPrefs.node(indicatorBean.getName());
/* 1843 */     indicatorNameNode.putLong("lastUsedDate", System.currentTimeMillis());
/* 1844 */     saveIndicatorParams(indicatorBean, indicatorNameNode);
/* 1845 */     flush(lastUsedIndicatorsPrefs);
/*      */   }
/*      */ 
/*      */   public void removeLastUsedIndicatorName(LastUsedIndicatorBean indicatorBean) {
/* 1849 */     Preferences lastUsedIndicatorsPrefs = getLastUsedIndicatorsNode();
/* 1850 */     Preferences indicatorNameNode = lastUsedIndicatorsPrefs.node(indicatorBean.getName());
/* 1851 */     removeNode(indicatorNameNode);
/* 1852 */     flush(indicatorNameNode);
/*      */   }
/*      */ 
/*      */   public void clearAllLastUsedIndicatorNames()
/*      */   {
/*      */     try {
/* 1858 */       getLastUsedIndicatorsNode().removeNode();
/*      */     } catch (BackingStoreException ex) {
/* 1860 */       LOGGER.warn("Failed to clean a last used indicators list.", ex);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void updateLastUsedIndicatorName(LastUsedIndicatorBean indicatorBean)
/*      */   {
/* 1866 */     Preferences lastUsedIndicatorsPrefs = getLastUsedIndicatorsNode();
/*      */     try {
/* 1868 */       if (lastUsedIndicatorsPrefs.nodeExists(indicatorBean.getName())) {
/* 1869 */         Preferences indicatorNameNode = lastUsedIndicatorsPrefs.node(indicatorBean.getName());
/* 1870 */         saveIndicatorParams(indicatorBean, indicatorNameNode);
/* 1871 */         flush(lastUsedIndicatorsPrefs);
/*      */       }
/*      */     } catch (BackingStoreException e) {
/* 1874 */       LOGGER.error(e.getMessage(), e);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void save(List<StrategyTestBean> strategyTestBeans)
/*      */   {
/*      */     try {
/* 1881 */       getStrategyTestNode().removeNode();
/*      */     } catch (BackingStoreException ex) {
/* 1883 */       LOGGER.warn("Failed to clean user preferences.", ex);
/*      */     }
/*      */ 
/* 1887 */     Preferences node = getStrategyTestNode();
/*      */ 
/* 1890 */     for (StrategyTestBean strategyTestBean : strategyTestBeans) {
/* 1891 */       int panelId = strategyTestBean.getPanelChartId();
/* 1892 */       Preferences panelNode = node.node(Integer.toString(panelId));
/* 1893 */       saveStrategyTestBeanImpl(panelNode, strategyTestBean);
/*      */     }
/*      */ 
/* 1896 */     flush(node);
/*      */   }
/*      */ 
/*      */   protected void saveStrategyTestBeanImpl(Preferences node, StrategyTestBean bean) {
/* 1900 */     if (bean.getInstruments() != null)
/*      */     {
/* 1902 */       StringBuilder instruments = new StringBuilder();
/*      */ 
/* 1904 */       for (Instrument instrument : bean.getInstruments()) {
/* 1905 */         instruments.append(instrument.toString()).append("|");
/*      */       }
/*      */ 
/* 1908 */       instruments.deleteCharAt(instruments.length() - 1);
/* 1909 */       node.put("instruments", instruments.toString());
/*      */ 
/* 1911 */       if (bean.getStrategyBinaryPath() != null) {
/* 1912 */         node.put("strategyFile", bean.getStrategyBinaryPath());
/*      */       }
/*      */ 
/* 1915 */       if (bean.getChartPeriod() != null) {
/* 1916 */         Preferences chartPeriodNode = node.node("chartPeriod");
/* 1917 */         saveJForexPeriod(chartPeriodNode, bean.getChartPeriod());
/*      */       }
/*      */     }
/*      */     try
/*      */     {
/* 1922 */       node.putByteArray("content", object2Bytes(bean));
/*      */     } catch (Exception ex) {
/* 1924 */       LOGGER.warn("Unable to store strategy test bean");
/* 1925 */       LOGGER.error(ex.getMessage(), ex);
/* 1926 */       removeNode(node);
/*      */     }
/*      */   }
/*      */ 
/*      */   public List<StrategyTestBean> getStrategyTestBeans() {
/* 1931 */     List result = new LinkedList();
/*      */ 
/* 1933 */     Preferences node = getStrategyTestNode();
/*      */ 
/* 1936 */     StrategyTestBean oldBean = getStrategyTestBeanImpl(node);
/* 1937 */     if (oldBean != null) {
/* 1938 */       oldBean.setStrategyBinaryPath(null);
/* 1939 */       oldBean.setPanelChartId(-1);
/* 1940 */       result.add(oldBean);
/*      */     }
/*      */ 
/*      */     try
/*      */     {
/* 1945 */       String[] names = node.childrenNames();
/* 1946 */       for (String name : names) {
/* 1947 */         Preferences childNode = node.node(name);
/* 1948 */         StrategyTestBean testBean = getStrategyTestBeanImpl(childNode);
/* 1949 */         if (testBean == null) continue;
/*      */         try {
/* 1951 */           testBean.setPanelChartId(Integer.parseInt(name));
/*      */         } catch (NumberFormatException ex) {
/* 1953 */           testBean.setPanelChartId(-1);
/*      */         }
/* 1955 */         result.add(testBean);
/*      */       }
/*      */     }
/*      */     catch (BackingStoreException ex) {
/* 1959 */       LOGGER.debug("Unable to restore strategy test beans.", ex);
/*      */     }
/* 1961 */     return result;
/*      */   }
/*      */ 
/*      */   protected StrategyTestBean getStrategyTestBeanImpl(Preferences node) {
/* 1965 */     String instruments = node.get("instruments", null);
/* 1966 */     String strategyPath = node.get("strategyFile", null);
/* 1967 */     if (instruments != null) {
/* 1968 */       Set instrumentsSet = new LinkedHashSet();
/* 1969 */       StringTokenizer tokenizer = new StringTokenizer(instruments, "|");
/* 1970 */       while (tokenizer.hasMoreTokens())
/*      */       {
/* 1972 */         String token = tokenizer.nextToken();
/* 1973 */         StringTokenizer instrumentTokenizer = new StringTokenizer(token, ",");
/*      */ 
/* 1975 */         instrumentsSet.add(Instrument.fromString(instrumentTokenizer.nextToken()));
/*      */       }
/*      */ 
/* 1978 */       if (instrumentsSet.size() == 0) {
/* 1979 */         return null;
/*      */       }
/*      */ 
/* 1982 */       byte[] raw = node.getByteArray("content", null);
/*      */ 
/* 1984 */       if ((raw != null) && (raw.length > 0)) {
/*      */         try {
/* 1986 */           StrategyTestBean strategyTestBean = (StrategyTestBean)bytesToObject(raw);
/* 1987 */           strategyTestBean.setInstruments(instrumentsSet);
/* 1988 */           strategyTestBean.setStrategyBinaryPath(strategyPath);
/*      */ 
/* 1990 */           if (strategyTestBean.getDataLoadingMethod() == null)
/*      */           {
/* 1994 */             strategyTestBean.setDataLoadingMethod(ITesterClient.DataLoadingMethod.PIVOT_TICKS);
/*      */           }
/*      */ 
/* 1997 */           if (node.nodeExists("chartPeriod")) {
/* 1998 */             JForexPeriod jForexPeriod = restoreJForexPeriod(node.node("chartPeriod"));
/* 1999 */             strategyTestBean.setChartPeriod(jForexPeriod);
/*      */           }
/*      */ 
/* 2002 */           return strategyTestBean;
/*      */         }
/*      */         catch (Exception ex) {
/* 2005 */           LOGGER.warn(new StringBuilder().append("Unable to restore strategy test bean: ").append(ex.getMessage()).toString());
/* 2006 */           removeNode(node);
/*      */         }
/*      */       }
/*      */     }
/* 2010 */     return null;
/*      */   }
/*      */ 
/*      */   public void saveDefaultStrategyPath(String defaultPath) {
/* 2014 */     if (defaultPath == null) {
/* 2015 */       return;
/*      */     }
/*      */ 
/* 2018 */     int index = defaultPath.lastIndexOf(File.separatorChar);
/* 2019 */     defaultPath = defaultPath.substring(0, index);
/* 2020 */     Preferences node = getJForexDefaultsNode();
/* 2021 */     node.put("strategyDefaultPath", defaultPath);
/*      */   }
/*      */ 
/*      */   public HistoricalDataManagerBean getHistoricalDataManagerBean() {
/* 2025 */     Preferences node = getHistoricalDataManagerNode();
/* 2026 */     byte[] raw = node.getByteArray("historicalDataManagerContent", null);
/*      */ 
/* 2028 */     if ((raw != null) && (raw.length > 0)) {
/*      */       try {
/* 2030 */         HistoricalDataManagerBean historicalDataManagerBean = (HistoricalDataManagerBean)bytesToObject(raw);
/* 2031 */         return historicalDataManagerBean;
/*      */       } catch (Exception ex) {
/* 2033 */         LOGGER.warn(new StringBuilder().append("Unable to restore historical data manager bean: ").append(ex.getMessage()).toString());
/* 2034 */         removeNode(node);
/*      */       }
/*      */     }
/*      */ 
/* 2038 */     return null;
/*      */   }
/*      */ 
/*      */   public void save(HistoricalDataManagerBean historicalDataManagerBean)
/*      */   {
/*      */     try {
/* 2044 */       getHistoricalDataManagerNode().removeNode();
/*      */     } catch (BackingStoreException ex) {
/* 2046 */       LOGGER.warn("Failed to clean user preferences (historical data manager).", ex);
/*      */     }
/*      */ 
/* 2049 */     if (historicalDataManagerBean != null)
/*      */     {
/* 2051 */       Preferences node = getHistoricalDataManagerNode();
/*      */       try {
/* 2053 */         node.putByteArray("historicalDataManagerContent", object2Bytes(historicalDataManagerBean));
/* 2054 */         flush(node);
/*      */       } catch (Exception ex) {
/* 2056 */         LOGGER.warn("Unable to store historical data manager bean");
/* 2057 */         LOGGER.error(ex.getMessage(), ex);
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   public void saveUserWorkspaceSettingsFilePath(String userId, String localMyWorkspaceSettingsPath) {
/* 2063 */     String jforexMode = GreedContext.CLIENT_MODE;
/* 2064 */     Preferences jforexModeRoot = getSystemRoot().node(jforexMode);
/* 2065 */     Preferences jforexModeUserRoot = jforexModeRoot.node(userId);
/*      */ 
/* 2067 */     jforexModeUserRoot.put("currentWorkspaceSettingsFilePath", localMyWorkspaceSettingsPath);
/*      */ 
/* 2069 */     if (localMyWorkspaceSettingsPath != null)
/* 2070 */       FilePathManager.getInstance().setWorkspacesFolderPath(new File(localMyWorkspaceSettingsPath).getParentFile().getAbsolutePath());
/*      */   }
/*      */ 
/*      */   public String getUserAccountId(String userName)
/*      */   {
/* 2075 */     String hashCode = userName;
/*      */     try {
/* 2077 */       hashCode = AuthorizationClient.encodeString(userName);
/*      */     } catch (NoSuchAlgorithmException e) {
/* 2079 */       LOGGER.error(e.getLocalizedMessage(), e);
/*      */     } catch (UnsupportedEncodingException e) {
/* 2081 */       LOGGER.error(e.getLocalizedMessage(), e);
/*      */     }
/* 2083 */     return hashCode;
/*      */   }
/*      */ 
/*      */   public String getClientMode() {
/* 2087 */     return GreedContext.CLIENT_MODE;
/*      */   }
/*      */ 
/*      */   public String getUserWorkspaceSettingsFilePath(String userId) {
/* 2091 */     Preferences userRoot = getSystemRoot().node(userId);
/* 2092 */     String myWorkspaceSettingsFilePath = userRoot.get("currentWorkspaceSettingsFilePath", null);
/*      */ 
/* 2094 */     String jforexMode = GreedContext.CLIENT_MODE;
/* 2095 */     Preferences jforexModeRoot = getSystemRoot().node(jforexMode);
/* 2096 */     Preferences jforexModeUserRoot = jforexModeRoot.node(userId);
/* 2097 */     String jforexModeUserWorkspaceSettingsFilePath = jforexModeUserRoot.get("currentWorkspaceSettingsFilePath", null);
/*      */ 
/* 2100 */     if (jforexModeUserWorkspaceSettingsFilePath != null) {
/* 2101 */       myWorkspaceSettingsFilePath = jforexModeUserWorkspaceSettingsFilePath;
/*      */     }
/*      */ 
/* 2105 */     if (myWorkspaceSettingsFilePath == null) {
/* 2106 */       if (FilePathManager.getInstance().isFolderAccessible(FilePathManager.getInstance().getDefaultWorkspaceFolderPath(getClientMode(), userId))) {
/* 2107 */         myWorkspaceSettingsFilePath = FilePathManager.getInstance().getDefaultWorkspaceSettingsFilePath(getClientMode(), userId);
/*      */       }
/*      */       else {
/* 2110 */         myWorkspaceSettingsFilePath = FilePathManager.getInstance().getAlternativeDefaultWorkspacesFolderPath(getClientMode(), userId);
/*      */       }
/* 2112 */       jforexModeUserRoot.put("currentWorkspaceSettingsFilePath", myWorkspaceSettingsFilePath);
/*      */     }
/*      */ 
/* 2115 */     return myWorkspaceSettingsFilePath;
/*      */   }
/*      */ 
/*      */   public void saveMyStrategiesPath(String myStrategiesPath) {
/* 2119 */     Preferences jForexNode = getJForexNode();
/* 2120 */     jForexNode.put("myStrategiesPath", myStrategiesPath);
/* 2121 */     if (myStrategiesPath != null)
/* 2122 */       FilePathManager.getInstance().setStrategiesFolderPath(myStrategiesPath);
/*      */   }
/*      */ 
/*      */   public String getMyStrategiesPath()
/*      */   {
/* 2127 */     Preferences jForexNode = getJForexNode();
/* 2128 */     String myStrategiesPath = jForexNode.get("myStrategiesPath", FilePathManager.getInstance().getDefaultStrategiesFolderPath());
/* 2129 */     if (!FilePathManager.getInstance().isFolderAccessible(myStrategiesPath)) {
/* 2130 */       myStrategiesPath = FilePathManager.getInstance().getAlternativeStrategiesFolderPath();
/* 2131 */       FilePathManager.getInstance().checkFolderStructure(myStrategiesPath);
/* 2132 */       jForexNode.put("myStrategiesPath", myStrategiesPath);
/*      */     }
/* 2134 */     return myStrategiesPath;
/*      */   }
/*      */ 
/*      */   public void saveMyIndicatorsPath(String myIndicatorsPath) {
/* 2138 */     Preferences jForexNode = getJForexNode();
/* 2139 */     jForexNode.put("myIndicatorsPath", myIndicatorsPath);
/* 2140 */     if (myIndicatorsPath != null)
/* 2141 */       FilePathManager.getInstance().setIndicatorsFolderPath(myIndicatorsPath);
/*      */   }
/*      */ 
/*      */   public String getMyIndicatorsPath()
/*      */   {
/* 2146 */     Preferences jForexNode = getJForexNode();
/* 2147 */     String myIndicatorsPath = jForexNode.get("myIndicatorsPath", FilePathManager.getInstance().getDefaultIndicatorsFolderPath());
/* 2148 */     if (!FilePathManager.getInstance().isFolderAccessible(myIndicatorsPath)) {
/* 2149 */       myIndicatorsPath = FilePathManager.getInstance().getAlternativeDefaultIndicatorsFolderPath();
/* 2150 */       FilePathManager.getInstance().checkFolderStructure(myIndicatorsPath);
/* 2151 */       jForexNode.put("myIndicatorsPath", myIndicatorsPath);
/*      */     }
/* 2153 */     return myIndicatorsPath;
/*      */   }
/*      */ 
/*      */   public String getMyChartTemplatesPath() {
/* 2157 */     Preferences jForexNode = getJForexNode();
/* 2158 */     String myChartTemplatesPath = jForexNode.get("myChartTemplatesPath", FilePathManager.getInstance().getDefaultTemplatesFolderPath());
/* 2159 */     if (!FilePathManager.getInstance().isFolderAccessible(myChartTemplatesPath)) {
/* 2160 */       myChartTemplatesPath = FilePathManager.getInstance().getAlternativeDefaultTemplatesFolderPath();
/* 2161 */       FilePathManager.getInstance().checkFolderStructure(myChartTemplatesPath);
/* 2162 */       saveMyChartTemplatesPath(myChartTemplatesPath);
/*      */     }
/* 2164 */     return myChartTemplatesPath;
/*      */   }
/*      */ 
/*      */   public void saveMyChartTemplatesPath(String myChartTemplatesPath) {
/* 2168 */     Preferences jForexNode = getJForexNode();
/* 2169 */     jForexNode.put("myChartTemplatesPath", myChartTemplatesPath);
/* 2170 */     if (myChartTemplatesPath != null)
/* 2171 */       FilePathManager.getInstance().setTemplatesFolderPath(myChartTemplatesPath);
/*      */   }
/*      */ 
/*      */   public void saveStopStrategyByException(boolean stopStrategyByException)
/*      */   {
/* 2177 */     Preferences jForexNode = getJForexNode();
/* 2178 */     jForexNode.putBoolean("stopStrategyOnException", stopStrategyByException);
/*      */   }
/*      */ 
/*      */   public boolean getStopStrategyByException() {
/* 2182 */     Preferences jForexNode = getJForexNode();
/* 2183 */     return jForexNode.getBoolean("stopStrategyOnException", true);
/*      */   }
/*      */ 
/*      */   public void save(INewsFilter newsFilter, INewsFilter.NewsSource newsSource) {
/* 2187 */     Preferences node = getJForexNode().node(nodeName(newsSource));
/*      */ 
/* 2189 */     if (newsFilter == null) {
/* 2190 */       removeNode(node);
/* 2191 */       flush(getJForexNode());
/*      */     } else {
/*      */       try {
/* 2194 */         node.putByteArray("content", object2Bytes(newsFilter));
/*      */       } catch (Exception ex) {
/* 2196 */         LOGGER.error(ex.getMessage(), ex);
/*      */       }
/* 2198 */       flush(node);
/*      */     }
/*      */   }
/*      */ 
/*      */   public INewsFilter load(INewsFilter.NewsSource newsSource) {
/* 2203 */     String nodeName = nodeName(newsSource);
/*      */ 
/* 2205 */     if (nodeName != null) {
/*      */       try {
/* 2207 */         if (getJForexNode().nodeExists(nodeName)) {
/* 2208 */           Preferences node = getJForexNode().node(nodeName);
/*      */           try {
/* 2210 */             byte[] drawingContent = node.getByteArray("content", null);
/*      */ 
/* 2212 */             if ((drawingContent == null) || (drawingContent.length == 0)) {
/* 2213 */               throw new Exception("INewsFilter content is empty");
/*      */             }
/*      */ 
/* 2216 */             Object object = bytesToObject(drawingContent);
/*      */ 
/* 2218 */             if ((object instanceof INewsFilter)) {
/* 2219 */               return (INewsFilter)object;
/*      */             }
/* 2221 */             throw new Exception("Object stored in filter content isn't instance of INewsFilter");
/*      */           }
/*      */           catch (Exception e) {
/* 2224 */             LOGGER.debug(new StringBuilder().append("Unable to restore INewsFilter for ").append(nodeName).append(" : ").append(e.getMessage()).toString());
/* 2225 */             removeNode(node);
/*      */           }
/*      */         }
/*      */       } catch (BackingStoreException ex) {
/* 2229 */         LOGGER.error(ex.getMessage(), ex);
/*      */       }
/*      */     }
/*      */ 
/* 2233 */     return null;
/*      */   }
/*      */ 
/*      */   public boolean bottomPanelExists(int panelId) {
/*      */     try {
/* 2238 */       if (getUserRoot().nodeExists("jforex")) {
/* 2239 */         Preferences framesNode = getBottomFramesNode();
/* 2240 */         return framesNode.nodeExists(String.valueOf(panelId));
/*      */       }
/*      */     }
/*      */     catch (BackingStoreException e) {
/*      */     }
/* 2245 */     return false;
/*      */   }
/*      */ 
/*      */   public boolean bottomPanelFramesPreferencesNodeExists() {
/*      */     try {
/* 2250 */       if (getUserRoot().nodeExists("jforex"))
/* 2251 */         return getJForexNode().nodeExists("bottomFramesPref");
/*      */     }
/*      */     catch (BackingStoreException e)
/*      */     {
/*      */     }
/* 2256 */     return false;
/*      */   }
/*      */ 
/*      */   public Preferences getBottomFramePreferencesNode()
/*      */   {
/* 2262 */     return getJForexNode().node("bottomFramesPref");
/*      */   }
/*      */ 
/*      */   public Preferences getBottomFramesNode() {
/* 2266 */     return getJForexNode().node("bottomFrames");
/*      */   }
/*      */ 
/*      */   private Preferences getJForexDefaultsNode() {
/* 2270 */     return getJForexNode().node("Defaults");
/*      */   }
/*      */ 
/*      */   private Preferences getJForexNode() {
/* 2274 */     return getUserRoot().node("jforex");
/*      */   }
/*      */ 
/*      */   private Preferences getLastUsedIndicatorsNode() {
/* 2278 */     return getJForexNode().node("lastUsedIndicators");
/*      */   }
/*      */ 
/*      */   private Preferences getCustomIndicatorsNode() {
/* 2282 */     return getJForexNode().node("custIndicators");
/*      */   }
/*      */ 
/*      */   private Preferences getCustomEnabledIndicatorsNode() {
/* 2286 */     return getJForexNode().node("custEnabledIndicators");
/*      */   }
/*      */ 
/*      */   private Preferences getStrategiesNode() {
/* 2290 */     return getJForexNode().node("strategies");
/*      */   }
/*      */ 
/*      */   private Preferences getStrategyTestNode()
/*      */   {
/* 2298 */     return getJForexNode().node("strategyTest");
/*      */   }
/*      */ 
/*      */   private Preferences getHistoricalDataManagerNode() {
/* 2302 */     return getJForexNode().node("historicalDataManager");
/*      */   }
/*      */ 
/*      */   private String nodeName(INewsFilter.NewsSource newsSource) {
/* 2306 */     switch (newsSource) {
/*      */     case DJ_NEWSWIRES:
/* 2308 */       return "djNewsWires";
/*      */     case DJ_LIVE_CALENDAR:
/* 2310 */       return "djLiveCalendar";
/*      */     }
/* 2312 */     return null;
/*      */   }
/*      */ 
/*      */   public List<ChartBean> getChartBeans()
/*      */   {
/* 2409 */     Preferences chartsNode = getChartsNode();
/* 2410 */     return getChartBeans(chartsNode);
/*      */   }
/*      */ 
/*      */   public List<ChartBean> getChartBeans(Preferences chartsNode) {
/* 2414 */     String[] chartIds = getChildrenNames(chartsNode);
/* 2415 */     if (chartIds == null) {
/* 2416 */       return Collections.emptyList();
/*      */     }
/*      */ 
/* 2419 */     List chartBeans = new ArrayList();
/* 2420 */     for (String chartId : chartIds) {
/* 2421 */       Preferences chartNode = chartsNode.node(chartId);
/*      */ 
/* 2424 */       Period period = null;
/*      */       try
/*      */       {
/* 2427 */         String periodName = chartNode.get("periodName", null);
/* 2428 */         if (periodName != null)
/* 2429 */           period = Period.valueOf(periodName);
/*      */       }
/*      */       catch (Throwable t) {
/*      */       }
/* 2433 */       if (period == null) {
/* 2434 */         String unit = chartNode.get("periodUnit", null);
/* 2435 */         String unitCount = chartNode.get("periodUnitsCount", "-1");
/* 2436 */         unit = (unit != null) && ("null".equals(unit.toLowerCase())) ? null : unit;
/* 2437 */         period = Period.createCustomPeriod(unit == null ? null : Unit.valueOf(unit), Integer.valueOf(unitCount).intValue());
/*      */       }
/*      */ 
/* 2440 */       String dataTypeStr = chartNode.get("dataType", DataType.TICKS.name());
/* 2441 */       dataTypeStr = "TRADE_BAR".equals(dataTypeStr) ? DataType.TICK_BAR.name() : dataTypeStr;
/* 2442 */       DataType dataType = DataType.valueOf(dataTypeStr);
/*      */ 
/* 2444 */       JForexPeriod jForexPeriod = new JForexPeriod(dataType == null ? dataType : Period.TICK.equals(period) ? DataType.TICKS : dataType, period, PriceRange.valueOf(chartNode.get("priceRangeValue", PriceRange.ONE_PIP.getName())), ReversalAmount.valueOf(chartNode.getInt("reversalAmount", ReversalAmount.THREE.getAmount())), TickBarSize.valueOf(chartNode.getInt("tradeBarSize", 3)));
/*      */ 
/* 2452 */       ChartBean chartBean = new ChartBean(new Integer(chartId).intValue(), Instrument.fromString(chartNode.get("instrumentName", Instrument.EURUSD.toString())), jForexPeriod, OfferSide.valueOf(chartNode.get("offerSideName", OfferSide.BID.name())), Filter.valueOf(chartNode.get("filter", Filter.ALL_FLATS.name())), DataType.DataPresentationType.valueOf(chartNode.get("candleType", DataType.DataPresentationType.CANDLE.name())), DataType.DataPresentationType.valueOf(chartNode.get("tickType", DataType.DataPresentationType.LINE.name())), DataType.DataPresentationType.valueOf(chartNode.get("priceRangeType", DataType.DataPresentationType.RANGE_BAR.name())), DataType.DataPresentationType.valueOf(chartNode.get("pointAndFigureType", DataType.DataPresentationType.BOX.name())), DataType.DataPresentationType.valueOf(chartNode.get("tradeBarType", DataType.DataPresentationType.BAR.name())), DataType.DataPresentationType.valueOf(chartNode.get("renkoType", DataType.DataPresentationType.BRICK.name())));
/*      */ 
/* 2465 */       chartBean.setGridVisible(chartNode.getInt("isGridVisible", 1));
/* 2466 */       chartBean.setMouseCursorVisible(chartNode.getInt("isMouseCursorVisible", 0));
/* 2467 */       chartBean.setLastCandleVisible(chartNode.getInt("isLastCandleVisible", 1));
/* 2468 */       chartBean.setVerticalMovementEnabled(chartNode.getInt("isVerticalMovementEnabled", 0));
/* 2469 */       chartBean.setEndTime(chartNode.getLong("endTime", -1L));
/* 2470 */       chartBean.setMinPrice(chartNode.getDouble("minPrice", -1.0D));
/* 2471 */       chartBean.setMaxPrice(chartNode.getDouble("maxPrice", -1.0D));
/* 2472 */       chartBean.setAutoShiftActive(chartNode.getInt("isAutoShiftActive", 1));
/* 2473 */       chartBean.setChartShiftInPx(chartNode.getInt("chartShiftInPx", -1));
/* 2474 */       chartBean.setYAxisPadding(chartNode.getDouble("yAxisPadding", -1.0D));
/* 2475 */       chartBean.setDataUnitWidth(chartNode.getInt("dataUnitWidth", -1));
/*      */ 
/* 2477 */       ITheme theme = ThemeManager.getTheme(chartNode.get("theme", "Default"));
/* 2478 */       chartBean.setTheme(theme);
/*      */ 
/* 2480 */       chartBean.setHistoricalTesterChart(chartNode.getBoolean("historicalTesterChart", false));
/*      */ 
/* 2482 */       chartBeans.add(chartBean);
/*      */     }
/*      */ 
/* 2485 */     if (chartBeans.isEmpty()) {
/* 2486 */       return Collections.emptyList();
/*      */     }
/*      */ 
/* 2489 */     return chartBeans;
/*      */   }
/*      */ 
/*      */   public ChartBean getChartBean(int chartBeanId) {
/* 2493 */     List chartBeans = getChartBeans();
/* 2494 */     for (ChartBean chart : chartBeans) {
/* 2495 */       if (chart.getId() == chartBeanId) {
/* 2496 */         return chart;
/*      */       }
/*      */     }
/* 2499 */     return null;
/*      */   }
/*      */ 
/*      */   public void saveChartBean(ChartBean chartBean, Preferences parent, Preferences chartNode) {
/* 2503 */     Instrument instrument = chartBean.getInstrument();
/* 2504 */     if (instrument != null) {
/* 2505 */       chartNode.put("instrumentName", instrument.toString());
/*      */     }
/*      */ 
/* 2508 */     Period period = chartBean.getPeriod();
/* 2509 */     if (period != null) {
/* 2510 */       if (!Period.TICK.equals(period)) {
/* 2511 */         chartNode.put("periodUnit", period.getUnit().toString());
/* 2512 */         chartNode.put("periodUnitsCount", String.valueOf(period.getNumOfUnits()));
/*      */       } else {
/* 2514 */         chartNode.put("periodUnit", "null");
/* 2515 */         chartNode.put("periodUnitsCount", String.valueOf(period.getNumOfUnits()));
/*      */       }
/*      */     }
/*      */ 
/* 2519 */     PriceRange priceRange = chartBean.getPriceRange();
/* 2520 */     if (priceRange != null) {
/* 2521 */       chartNode.put("priceRangeValue", priceRange.getName());
/*      */     }
/*      */ 
/* 2524 */     DataType dataType = chartBean.getDataType();
/* 2525 */     if (dataType != null) {
/* 2526 */       chartNode.put("dataType", dataType.name());
/*      */     }
/*      */ 
/* 2529 */     ReversalAmount reversalAmount = chartBean.getReversalAmount();
/* 2530 */     if (reversalAmount != null) {
/* 2531 */       chartNode.putInt("reversalAmount", reversalAmount.getAmount());
/*      */     }
/*      */ 
/* 2534 */     TickBarSize tickBarSize = chartBean.getTickBarSize();
/* 2535 */     if (tickBarSize != null) {
/* 2536 */       chartNode.putInt("tradeBarSize", tickBarSize.getSize());
/*      */     }
/*      */ 
/* 2539 */     OfferSide offerSide = chartBean.getOfferSide();
/* 2540 */     if (offerSide != null) {
/* 2541 */       chartNode.put("offerSideName", offerSide.name());
/*      */     }
/*      */ 
/* 2544 */     Filter filter = chartBean.getFilter();
/* 2545 */     if (filter != null) {
/* 2546 */       chartNode.put("filter", filter.name());
/*      */     }
/*      */ 
/* 2549 */     DataType.DataPresentationType candleType = chartBean.getTimePeriodPresentationType();
/* 2550 */     if (candleType != null) {
/* 2551 */       chartNode.put("candleType", candleType.name());
/*      */     }
/*      */ 
/* 2554 */     DataType.DataPresentationType tickType = chartBean.getTicksPresentationType();
/* 2555 */     if (tickType != null) {
/* 2556 */       chartNode.put("tickType", tickType.name());
/*      */     }
/*      */ 
/* 2559 */     DataType.DataPresentationType priceRangeType = chartBean.getPriceRangePresentationType();
/* 2560 */     if (priceRangeType != null) {
/* 2561 */       chartNode.put("priceRangeType", priceRangeType.name());
/*      */     }
/*      */ 
/* 2564 */     DataType.DataPresentationType pnfType = chartBean.getPointAndFigurePresentationType();
/* 2565 */     if (pnfType != null) {
/* 2566 */       chartNode.put("pointAndFigureType", pnfType.name());
/*      */     }
/*      */ 
/* 2569 */     DataType.DataPresentationType tickBarType = chartBean.getTickBarPresentationType();
/* 2570 */     if (tickBarType != null) {
/* 2571 */       chartNode.put("tradeBarType", tickBarType.name());
/*      */     }
/*      */ 
/* 2574 */     DataType.DataPresentationType renkoBarType = chartBean.getRenkoPresentationType();
/* 2575 */     if (renkoBarType != null) {
/* 2576 */       chartNode.put("renkoType", renkoBarType.name());
/*      */     }
/*      */ 
/* 2579 */     int isGridVisible = chartBean.getGridVisible();
/* 2580 */     if (isGridVisible != -1) {
/* 2581 */       chartNode.putInt("isGridVisible", isGridVisible);
/*      */     }
/*      */ 
/* 2584 */     int isMouseCursorVisible = chartBean.getMouseCursorVisible();
/* 2585 */     if (isMouseCursorVisible != -1) {
/* 2586 */       chartNode.putInt("isMouseCursorVisible", isMouseCursorVisible);
/*      */     }
/*      */ 
/* 2589 */     int lastCandleVisible = chartBean.getLastCandleVisible();
/* 2590 */     if (lastCandleVisible != -1) {
/* 2591 */       chartNode.putInt("isLastCandleVisible", lastCandleVisible);
/*      */     }
/*      */ 
/* 2594 */     int isVerticalMovementEnabled = chartBean.getVerticalMovementEnabled();
/* 2595 */     if (isVerticalMovementEnabled != -1) {
/* 2596 */       chartNode.putInt("isVerticalMovementEnabled", isVerticalMovementEnabled);
/*      */     }
/*      */ 
/* 2599 */     long endTime = chartBean.getEndTime();
/* 2600 */     if (endTime != -1L) {
/* 2601 */       chartNode.putLong("endTime", endTime);
/*      */     }
/*      */ 
/* 2604 */     double minPrice = chartBean.getMinPrice();
/* 2605 */     if (minPrice != -1.0D) {
/* 2606 */       chartNode.putDouble("minPrice", minPrice);
/*      */     }
/*      */ 
/* 2609 */     double maxPrice = chartBean.getMaxPrice();
/* 2610 */     if (maxPrice != -1.0D) {
/* 2611 */       chartNode.putDouble("maxPrice", maxPrice);
/*      */     }
/*      */ 
/* 2614 */     int autoShiftActive = chartBean.getAutoShiftActive();
/* 2615 */     if (autoShiftActive != -1) {
/* 2616 */       chartNode.putInt("isAutoShiftActive", autoShiftActive);
/*      */     }
/*      */ 
/* 2619 */     int chartShiftInPx = chartBean.getChartShiftInPx();
/* 2620 */     if (chartShiftInPx != -1) {
/* 2621 */       chartNode.putInt("chartShiftInPx", chartShiftInPx);
/*      */     }
/*      */ 
/* 2624 */     double yAxisPadding = chartBean.getYAxisPadding();
/* 2625 */     if (yAxisPadding != -1.0D) {
/* 2626 */       chartNode.putDouble("yAxisPadding", yAxisPadding);
/*      */     }
/*      */ 
/* 2629 */     int dataUnitWidth = chartBean.getDataUnitWidth();
/* 2630 */     if (dataUnitWidth != -1) {
/* 2631 */       chartNode.putInt("dataUnitWidth", dataUnitWidth);
/*      */     }
/*      */ 
/* 2635 */     if ((chartBean.getTheme() != null) && (chartBean.getTheme().getName() != null)) {
/* 2636 */       String themeName = chartBean.getTheme().getName();
/* 2637 */       chartNode.put("theme", themeName);
/*      */     }
/*      */ 
/* 2640 */     chartNode.putBoolean("historicalTesterChart", chartBean.isHistoricalTesterChart());
/* 2641 */     flush(parent);
/*      */   }
/*      */ 
/*      */   public void saveChartBean(ChartBean chartBean) {
/* 2645 */     Preferences chartsNode = getChartsNode();
/* 2646 */     Preferences chartNode = getChartNode(Integer.valueOf(chartBean.getId()));
/*      */ 
/* 2648 */     saveChartBean(chartBean, chartsNode, chartNode);
/*      */   }
/*      */ 
/*      */   public void remove(Integer id) {
/* 2652 */     Preferences nodes = getChartsNode();
/* 2653 */     removeNode(nodes.node(id.toString()));
/* 2654 */     removeNode(getMainFramePreferences(Integer.toString(id.intValue())));
/* 2655 */     flush(nodes);
/* 2656 */     flush(getMainFramePreferencesNode());
/*      */   }
/*      */ 
/*      */   public List<IndicatorBean> getIndicatorBeans(Preferences chartNode) {
/* 2660 */     List indicatorBeans = new LinkedList();
/*      */ 
/* 2662 */     Preferences indicatorsNode = getIndicatorsNode(chartNode);
/*      */ 
/* 2664 */     String[] ids = getChildrenNames(indicatorsNode);
/*      */ 
/* 2667 */     Arrays.sort(ids, new Comparator()
/*      */     {
/*      */       public int compare(String x, String y) {
/* 2670 */         return Integer.valueOf(x).compareTo(Integer.valueOf(y));
/*      */       }
/*      */     });
/* 2673 */     for (String id : ids) {
/* 2674 */       Preferences indicatorNode = indicatorsNode.node(id);
/* 2675 */       String name = indicatorNode.get("indicatorName", null);
/* 2676 */       Integer subChartId = Integer.valueOf(indicatorNode.getInt("subPanelId", -1));
/*      */ 
/* 2679 */       if (name == null) {
/* 2680 */         removeNode(indicatorNode);
/*      */       }
/*      */       else
/*      */       {
/* 2684 */         Object[] indicatorParams = getIndicatorParams(indicatorNode);
/* 2685 */         OfferSide[] offerSides = getIndicatorOfferSide(indicatorNode);
/* 2686 */         IIndicators.AppliedPrice[] appliedPrices = getIndicatorAppliedPrices(indicatorNode);
/* 2687 */         Color[] outputColors = getIndicatorOutputColors(indicatorNode);
/* 2688 */         Color[] outputColors2 = getIndicatorOutputColors2(indicatorNode);
/* 2689 */         boolean[] valuesOnChart = new boolean[outputColors.length];
/* 2690 */         getValuesOnChart(indicatorNode, valuesOnChart);
/* 2691 */         boolean[] showOutputs = new boolean[outputColors.length];
/* 2692 */         getOutputs(indicatorNode, showOutputs);
/* 2693 */         float[] opacityAlphas = new float[outputColors.length];
/* 2694 */         getOpacityAlphas(indicatorNode, opacityAlphas);
/*      */ 
/* 2696 */         OutputParameterInfo.DrawingStyle[] drawingStyles = getIndicatorDrawingStyles(indicatorNode);
/* 2697 */         int[] lineWidths = getIndicatorLineWidths(indicatorNode);
/* 2698 */         int[] outputShifts = getIndicatorOutputShifts(indicatorNode);
/*      */ 
/* 2700 */         boolean recalculateOnNewCandleOnly = indicatorNode.getBoolean("recalculateOnNewCandleOnly", false);
/*      */ 
/* 2702 */         indicatorBeans.add(new IndicatorBean(Integer.valueOf(id).intValue(), name, offerSides, appliedPrices, indicatorParams, outputColors, outputColors2, valuesOnChart, showOutputs, opacityAlphas, drawingStyles, lineWidths, outputShifts, subChartId, null, null, recalculateOnNewCandleOnly));
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 2725 */     return indicatorBeans;
/*      */   }
/*      */ 
/*      */   public List<IndicatorBean> getIndicatorBeans(Integer chartId) {
/* 2729 */     Preferences chartNodes = getChartsNode();
/* 2730 */     Preferences chartNode = chartNodes.node(chartId.toString());
/* 2731 */     return getIndicatorBeans(chartNode);
/*      */   }
/*      */ 
/*      */   public void saveIndicatorBeans(List<IndicatorBean> indicatorBeans, Preferences chartNode) {
/* 2735 */     if ((indicatorBeans != null) && (!indicatorBeans.isEmpty()))
/* 2736 */       for (IndicatorBean indicatorBean : indicatorBeans)
/* 2737 */         saveIndicatorBean(indicatorBean, chartNode);
/*      */   }
/*      */ 
/*      */   public void saveIndicatorBean(IndicatorBean indicatorBean, Preferences chartNode)
/*      */   {
/* 2743 */     Preferences indicatorsNode = getIndicatorsNode(chartNode);
/* 2744 */     Preferences indicatorNode = indicatorsNode.node(String.valueOf(indicatorBean.getId()));
/*      */ 
/* 2746 */     indicatorNode.put("indicatorName", indicatorBean.getName());
/* 2747 */     indicatorNode.putInt("subPanelId", indicatorBean.getSubPanelId().intValue());
/*      */ 
/* 2749 */     saveIndicatorParams(indicatorBean, indicatorNode);
/* 2750 */     saveIndicatorChartObjects(indicatorBean.getId().intValue(), indicatorBean.getChartObjects(), chartNode);
/* 2751 */     saveIndicatorLevelInfoList(indicatorBean.getId().intValue(), indicatorBean.getLevelInfoList(), chartNode);
/*      */ 
/* 2753 */     indicatorNode.putBoolean("recalculateOnNewCandleOnly", indicatorBean.isRecalculateOnNewCandleOnly());
/*      */ 
/* 2755 */     flush(chartNode);
/*      */   }
/*      */ 
/*      */   public void saveIndicatorBean(Integer chartId, IndicatorBean indicatorBean)
/*      */   {
/* 2760 */     Preferences chartNode = getChartNode(chartId);
/* 2761 */     saveIndicatorBean(indicatorBean, chartNode);
/*      */   }
/*      */ 
/*      */   public List<IndicatorWrapper> getIndicatorWrappers(Preferences chartNode) {
/* 2765 */     List indicatorBeans = getIndicatorBeans(chartNode);
/* 2766 */     List indicatorWrappers = new ArrayList();
/* 2767 */     for (IndicatorBean indicatorBean : indicatorBeans) {
/* 2768 */       if (IndicatorsProvider.getInstance().getIndicatorHolder(indicatorBean.getName()) == null)
/*      */         continue;
/*      */       try
/*      */       {
/* 2772 */         IndicatorWrapper indicatorWrapper = DDSChartsActionAdapter.convertToIndicatorWraper(indicatorBean);
/* 2773 */         IdManager.reserveIndicatorId(indicatorBean.getId().intValue());
/* 2774 */         indicatorWrappers.add(indicatorWrapper);
/*      */       } catch (InvalidParameterException e) {
/* 2776 */         LOGGER.warn(e.getMessage(), e);
/* 2777 */         removeIndicator(chartNode, indicatorBean.getId());
/*      */       }
/*      */     }
/*      */ 
/* 2781 */     Collections.sort(indicatorWrappers, new Comparator() {
/*      */       public int compare(IndicatorWrapper x, IndicatorWrapper y) {
/* 2783 */         if (x.getId() > y.getId())
/* 2784 */           return 1;
/* 2785 */         if (x.getId() < y.getId()) {
/* 2786 */           return -1;
/*      */         }
/* 2788 */         return 0;
/*      */       }
/*      */     });
/* 2793 */     indicatorWrappers = populateIndicatorWrappersWithChartObjects(chartNode, indicatorWrappers);
/* 2794 */     indicatorWrappers = populateIndicatorWrappersWithLevelInfos(chartNode, indicatorWrappers);
/*      */ 
/* 2796 */     return indicatorWrappers;
/*      */   }
/*      */ 
/*      */   public List<IndicatorWrapper> getIndicatorWrappers(int chartPanelId, boolean dontSaveSettings)
/*      */   {
/* 2801 */     if (dontSaveSettings) {
/* 2802 */       return new ArrayList();
/*      */     }
/* 2804 */     Preferences chartNodes = getChartsNode();
/* 2805 */     Preferences chartNode = chartNodes.node(String.valueOf(chartPanelId));
/* 2806 */     return getIndicatorWrappers(chartNode);
/*      */   }
/*      */ 
/*      */   private List<IndicatorWrapper> populateIndicatorWrappersWithChartObjects(Preferences chartNode, List<IndicatorWrapper> indicatorWrappers) {
/* 2810 */     if ((indicatorWrappers == null) || (indicatorWrappers.isEmpty())) {
/* 2811 */       return indicatorWrappers;
/*      */     }
/*      */ 
/* 2814 */     for (IndicatorWrapper indicatorWrapper : indicatorWrappers) {
/* 2815 */       List chartObjects = getSubIndicatorDrawingsFor(chartNode, indicatorWrapper.getId());
/* 2816 */       indicatorWrapper.setChartObjects(chartObjects);
/*      */     }
/*      */ 
/* 2819 */     return indicatorWrappers;
/*      */   }
/*      */ 
/*      */   private List<IndicatorWrapper> populateIndicatorWrappersWithLevelInfos(Preferences chartNode, List<IndicatorWrapper> indicatorWrappers) {
/* 2823 */     if ((indicatorWrappers == null) || (indicatorWrappers.isEmpty())) {
/* 2824 */       return indicatorWrappers;
/*      */     }
/*      */ 
/* 2827 */     for (IndicatorWrapper indicatorWrapper : indicatorWrappers) {
/* 2828 */       List levelInfoList = getSubIndicatorLevelInfoListFor(chartNode, indicatorWrapper.getId());
/* 2829 */       indicatorWrapper.setLevelInfoList(levelInfoList);
/*      */     }
/*      */ 
/* 2832 */     return indicatorWrappers;
/*      */   }
/*      */ 
/*      */   public void removeIndicator(Preferences chartNode, Integer indicatorId)
/*      */   {
/* 2837 */     Preferences indicatorsNode = getIndicatorsNode(chartNode);
/* 2838 */     Preferences indicatorNode = indicatorsNode.node(String.valueOf(indicatorId));
/* 2839 */     removeNode(indicatorNode);
/* 2840 */     flush(indicatorsNode);
/*      */   }
/*      */ 
/*      */   public void removeIndicator(Integer chartId, Integer indicatorId) {
/* 2844 */     Preferences chartNode = getChartNode(chartId);
/* 2845 */     removeIndicator(chartNode, indicatorId);
/*      */   }
/*      */ 
/*      */   public List<IChartObject> getDrawingsFor(Integer chartId, Preferences chartNodes) {
/* 2849 */     Preferences chartNode = chartNodes.node(chartId.toString());
/* 2850 */     return getDrawingsFor(chartNode);
/*      */   }
/*      */ 
/*      */   public List<IChartObject> getDrawingsFor(Preferences chartNode) {
/* 2854 */     Preferences drawingsNode = chartNode.node("drawings");
/* 2855 */     return loadDrawingFromNode(drawingsNode);
/*      */   }
/*      */ 
/*      */   public List<IChartObject> getDrawingsFor(Integer chartId) {
/* 2859 */     Preferences chartNodes = getChartsNode();
/* 2860 */     return getDrawingsFor(chartId, chartNodes);
/*      */   }
/*      */ 
/*      */   public void saveChartObjects(List<IChartObject> chartObjects, Preferences chartNode) {
/* 2864 */     if (chartObjects != null)
/* 2865 */       for (IChartObject chartObject : chartObjects)
/* 2866 */         saveChartObject(chartObject, chartNode);
/*      */   }
/*      */ 
/*      */   private void saveChartObject(IChartObject chartObject, Preferences chartNode)
/*      */   {
/* 2872 */     Preferences drawingNode = getDrawingNode(chartNode, chartObject);
/* 2873 */     drawingNode.put("type", chartObject.getType().name());
/*      */     try
/*      */     {
/* 2876 */       drawingNode.putByteArray("content", object2Bytes(chartObject));
/*      */ 
/* 2878 */       if (chartObject.getStroke() != null) {
/* 2879 */         drawingNode.putByteArray("stroke", object2Bytes(SerializableBasicStroke.serializable((BasicStroke)chartObject.getStroke())));
/*      */       }
/*      */ 
/*      */     }
/*      */     catch (Exception ex)
/*      */     {
/* 2885 */       LOGGER.error(ex.getMessage(), ex);
/*      */     }
/*      */ 
/* 2888 */     flush(chartNode);
/*      */   }
/*      */ 
/*      */   public void saveChartObject(Integer chartId, IChartObject chartObject) {
/* 2892 */     Preferences chartNode = getChartNode(chartId);
/* 2893 */     saveChartObject(chartObject, chartNode);
/*      */   }
/*      */ 
/*      */   public void removeDrawing(Integer chartId, IChartObject chartObject) {
/* 2897 */     Preferences chartNode = getChartNode(chartId);
/* 2898 */     Preferences drawingNode = getDrawingNode(chartNode, chartObject);
/* 2899 */     removeNode(drawingNode);
/* 2900 */     flush(drawingNode);
/*      */   }
/*      */ 
/*      */   public List<IChartObject> getSubIndicatorDrawingsFor(Preferences chartNode, int subIndicatorId) {
/* 2904 */     Preferences indicatorsNode = getIndicatorsNode(chartNode);
/* 2905 */     Preferences indicatorNode = indicatorsNode.node(String.valueOf(subIndicatorId));
/* 2906 */     Preferences indicatorDrawingsNode = indicatorNode.node("indicatorDrawings");
/* 2907 */     return loadDrawingFromNode(indicatorDrawingsNode);
/*      */   }
/*      */ 
/*      */   public List<IChartObject> getSubIndicatorDrawingsFor(int chartPanelId, int subIndicatorId) {
/* 2911 */     Preferences chartNode = getChartNode(Integer.valueOf(chartPanelId));
/* 2912 */     return getSubIndicatorDrawingsFor(chartNode, subIndicatorId);
/*      */   }
/*      */ 
/*      */   public List<LevelInfo> getSubIndicatorLevelInfoListFor(Preferences chartNode, int subIndicatorId) {
/* 2916 */     Preferences indicatorsNode = getIndicatorsNode(chartNode);
/* 2917 */     Preferences indicatorNode = indicatorsNode.node(String.valueOf(subIndicatorId));
/* 2918 */     Preferences indicatorLevelsNode = indicatorNode.node("indicatorLevels");
/* 2919 */     return loadLevelInfosFromNode(indicatorLevelsNode);
/*      */   }
/*      */ 
/*      */   public void saveIndicatorChartObjects(int indicatorId, List<IChartObject> drawings, Preferences chartNode) {
/* 2923 */     if ((drawings != null) && (!drawings.isEmpty()))
/* 2924 */       for (IChartObject chartObject : drawings)
/* 2925 */         saveIndicatorChartObject(indicatorId, chartObject, chartNode);
/*      */   }
/*      */ 
/*      */   public void saveIndicatorChartObject(int indicatorId, IChartObject drawing, Preferences chartNode)
/*      */   {
/* 2931 */     Preferences indicatorsNode = getIndicatorsNode(chartNode);
/* 2932 */     Preferences indicatorNode = indicatorsNode.node(String.valueOf(indicatorId));
/* 2933 */     Preferences indicatorDrawingsNode = indicatorNode.node("indicatorDrawings");
/* 2934 */     Preferences drawingNode = indicatorDrawingsNode.node(String.valueOf(drawing.hashCode()));
/*      */ 
/* 2936 */     drawingNode.put("type", drawing.getType().name());
/*      */     try
/*      */     {
/* 2939 */       drawingNode.putByteArray("content", object2Bytes(drawing));
/*      */ 
/* 2941 */       if (drawing.getStroke() != null) {
/* 2942 */         drawingNode.putByteArray("stroke", object2Bytes(SerializableBasicStroke.serializable((BasicStroke)drawing.getStroke())));
/*      */       }
/*      */     }
/*      */     catch (Exception ex)
/*      */     {
/* 2947 */       LOGGER.error(ex.getMessage(), ex);
/*      */     }
/*      */ 
/* 2950 */     flush(chartNode);
/*      */   }
/*      */ 
/*      */   public void saveIndicatorChartObject(int chartPanelId, int indicatorId, IChartObject drawing) {
/* 2954 */     Preferences chartNode = getChartNode(Integer.valueOf(chartPanelId));
/* 2955 */     saveIndicatorChartObject(indicatorId, drawing, chartNode);
/*      */   }
/*      */ 
/*      */   private void saveIndicatorLevelInfoList(int indicatorId, List<LevelInfo> levelInfoList, Preferences chartNode) {
/* 2959 */     Preferences indicatorsNode = getIndicatorsNode(chartNode);
/* 2960 */     Preferences indicatorNode = indicatorsNode.node(String.valueOf(indicatorId));
/* 2961 */     Preferences indicatorLevelsNode = indicatorNode.node("indicatorLevels");
/* 2962 */     dropChildren(indicatorLevelsNode);
/* 2963 */     clear(indicatorLevelsNode);
/* 2964 */     if ((levelInfoList != null) && (!levelInfoList.isEmpty())) {
/* 2965 */       for (int i = 0; i < levelInfoList.size(); i++) {
/* 2966 */         LevelInfo levelInfo = (LevelInfo)levelInfoList.get(i);
/* 2967 */         Preferences levelNode = indicatorLevelsNode.node(String.valueOf(i));
/* 2968 */         levelNode.putByteArray("levelContent", object2Bytes(levelInfo));
/*      */       }
/*      */     }
/* 2971 */     flush(chartNode);
/*      */   }
/*      */ 
/*      */   public void removeDrawing(int chartPanelId, int indicatorId, IChartObject editedChartObject) {
/* 2975 */     Preferences chartNode = getChartNode(Integer.valueOf(chartPanelId));
/* 2976 */     Preferences indicatorsNode = getIndicatorsNode(chartNode);
/* 2977 */     Preferences indicatorNode = indicatorsNode.node(String.valueOf(indicatorId));
/* 2978 */     Preferences indicatorDrawingsNode = indicatorNode.node("indicatorDrawings");
/* 2979 */     Preferences drawingNode = indicatorDrawingsNode.node(String.valueOf(editedChartObject.hashCode()));
/* 2980 */     removeNode(drawingNode);
/* 2981 */     flush(drawingNode);
/*      */   }
/*      */ 
/*      */   public Preferences getChartsNode() {
/* 2985 */     return getUserRoot().node("charts");
/*      */   }
/*      */ 
/*      */   private Preferences getHorizontalRetracementNode() {
/* 2989 */     return getChartSettingsNode().node("HorizontalRetracementLevels");
/*      */   }
/*      */ 
/*      */   private Preferences getChartNode(Integer chartId) {
/* 2993 */     return getChartsNode().node(String.valueOf(chartId));
/*      */   }
/*      */ 
/*      */   private Preferences getIndicatorsNode(Preferences chartNode) {
/* 2997 */     return chartNode.node("indicators");
/*      */   }
/*      */ 
/*      */   private Preferences getDrawingNode(Preferences chartNode, IChartObject chartObject) {
/* 3001 */     return chartNode.node("drawings").node(String.valueOf(chartObject.hashCode()));
/*      */   }
/*      */ 
/*      */   private void saveExpandedState(boolean isExpanded, String key) {
/* 3005 */     Preferences chartsNode = getJForexNode().node("expandedState");
/* 3006 */     chartsNode.putBoolean(key, isExpanded);
/* 3007 */     flush(chartsNode);
/*      */   }
/*      */ 
/*      */   private boolean isExpanded(String key) {
/* 3011 */     Preferences expandedStateNode = getJForexNode().node("expandedState");
/* 3012 */     return expandedStateNode.getBoolean(key, true);
/*      */   }
/*      */ 
/*      */   private List<IChartObject> loadDrawingFromNode(Preferences drawingsNode) {
/* 3016 */     boolean flushNeeded = false;
/*      */ 
/* 3018 */     List drawings = new ArrayList();
/* 3019 */     String[] ids = getChildrenNames(drawingsNode);
/* 3020 */     for (String id : ids) {
/* 3021 */       Preferences drawingNode = drawingsNode.node(id);
/*      */       try
/*      */       {
/* 3024 */         byte[] drawingContent = drawingNode.getByteArray("content", null);
/*      */ 
/* 3026 */         if ((drawingContent == null) || (drawingContent.length == 0)) {
/* 3027 */           throw new Exception("Drawing content is empty");
/*      */         }
/*      */ 
/* 3030 */         Object object = bytesToObject(drawingContent);
/*      */ 
/* 3032 */         if ((object instanceof IChartObject)) {
/* 3033 */           IChartObject chartObject = (IChartObject)object;
/* 3034 */           chartObject.setMenuEnabled(true);
/*      */ 
/* 3037 */           byte[] strokeRaw = drawingNode.getByteArray("stroke", null);
/* 3038 */           if ((strokeRaw != null) && (strokeRaw.length > 0)) {
/* 3039 */             chartObject.setStroke((BasicStroke)bytesToObject(strokeRaw));
/*      */           }
/* 3041 */           drawings.add(chartObject);
/*      */ 
/* 3047 */           if (((chartObject instanceof ChartObject)) && (((ChartObject)chartObject).isGlobal()))
/*      */           {
/* 3049 */             removeNode(drawingNode);
/* 3050 */             flushNeeded = true;
/*      */           }
/*      */         } else {
/* 3053 */           throw new Exception("Object stored in drawing content isn't instance of IChartObject");
/*      */         }
/*      */       } catch (Exception e) {
/* 3056 */         LOGGER.debug(new StringBuilder().append("Unable to restore IChartObject : ").append(e.getMessage()).toString());
/* 3057 */         removeNode(drawingNode);
/* 3058 */         flushNeeded = true;
/*      */       }
/*      */     }
/*      */ 
/* 3062 */     if (flushNeeded) {
/* 3063 */       flush(drawingsNode);
/*      */     }
/*      */ 
/* 3066 */     return drawings;
/*      */   }
/*      */ 
/*      */   private List<LevelInfo> loadLevelInfosFromNode(Preferences indicatorLevelsNode) {
/* 3070 */     boolean flushNeeded = false;
/* 3071 */     List levelInfoList = new ArrayList();
/* 3072 */     String[] ids = getChildrenNames(indicatorLevelsNode);
/* 3073 */     for (String id : ids) {
/* 3074 */       Preferences levelNode = indicatorLevelsNode.node(id);
/*      */       try
/*      */       {
/* 3077 */         byte[] levelContent = levelNode.getByteArray("levelContent", null);
/*      */ 
/* 3079 */         if ((levelContent == null) || (levelContent.length == 0)) {
/* 3080 */           throw new Exception("Level content is empty");
/*      */         }
/*      */ 
/* 3083 */         Object object = bytesToObject(levelContent);
/*      */ 
/* 3085 */         if ((object instanceof LevelInfo)) {
/* 3086 */           LevelInfo chartObject = (LevelInfo)object;
/* 3087 */           levelInfoList.add(chartObject);
/*      */         } else {
/* 3089 */           throw new Exception("Object stored in level content isn't instance of LevelInfo");
/*      */         }
/*      */       } catch (Exception e) {
/* 3092 */         LOGGER.debug(new StringBuilder().append("Unable to restore LevelInfo : ").append(e.getMessage()).toString());
/* 3093 */         removeNode(levelNode);
/* 3094 */         flushNeeded = true;
/*      */       }
/*      */     }
/*      */ 
/* 3098 */     if (flushNeeded) {
/* 3099 */       flush(indicatorLevelsNode);
/*      */     }
/*      */ 
/* 3102 */     return levelInfoList;
/*      */   }
/*      */ 
/*      */   public void saveSelectedTheme(ITheme theme)
/*      */   {
/* 3108 */     getThemesNode().put("Selected", theme.getName());
/*      */   }
/*      */ 
/*      */   public void removeAllThemes() {
/*      */     try {
/* 3113 */       getThemesNode().removeNode();
/*      */     } catch (BackingStoreException ex) {
/* 3115 */       LOGGER.warn(new StringBuilder().append("Unable to remove themes node : ").append(ex.getMessage()).toString(), ex);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void save(ITheme theme) {
/* 3120 */     Preferences themesNode = getThemesNode();
/*      */ 
/* 3122 */     Preferences themeNode = createThemeNode(theme, themesNode);
/*      */ 
/* 3124 */     flush(themeNode);
/*      */   }
/*      */ 
/*      */   public void saveTheme(ITheme theme, Preferences parentNode) {
/* 3128 */     Preferences themesRootNode = parentNode.node("Themes");
/*      */ 
/* 3130 */     createThemeNode(theme, themesRootNode);
/*      */   }
/*      */ 
/*      */   private Preferences createThemeNode(ITheme theme, Preferences themesRootNode) {
/* 3134 */     Preferences themeNode = themesRootNode.node(theme.getName());
/*      */ 
/* 3136 */     Preferences colorsNode = getThemeColorsNode(themeNode);
/*      */ 
/* 3138 */     for (ITheme.ChartElement chartElement : ITheme.ChartElement.values()) {
/* 3139 */       colorsNode.put(chartElement.name(), Integer.toHexString(theme.getColor(chartElement).getRGB() & 0xFFFFFF | 0x1000000).substring(1));
/*      */     }
/*      */ 
/* 3145 */     Preferences fontsNode = getThemeFontsNode(themeNode);
/*      */ 
/* 3147 */     for (ITheme.TextElement textElement : ITheme.TextElement.values()) {
/*      */       try {
/* 3149 */         fontsNode.putByteArray(textElement.name(), object2Bytes(theme.getFont(textElement)));
/*      */       } catch (Exception ex) {
/* 3151 */         LOGGER.warn(new StringBuilder().append("Unable to store font for ").append(textElement.name()).append("@").append(theme.getName()).append(" : ").append(ex.getMessage()).toString(), ex);
/*      */       }
/*      */     }
/*      */ 
/* 3155 */     Preferences strokesNode = getThemeStrokesNode(themeNode);
/* 3156 */     for (ITheme.StrokeElement strokeElement : ITheme.StrokeElement.values()) {
/* 3157 */       Preferences strokeNode = strokesNode.node(strokeElement.name());
/* 3158 */       strokeNode.putFloat("StrokeWidth", theme.getStroke(strokeElement).getLineWidth());
/* 3159 */       strokeNode.putByteArray("StrokeDash", object2Bytes(theme.getStroke(strokeElement).getDashArray()));
/*      */     }
/*      */ 
/* 3162 */     return themeNode;
/*      */   }
/*      */ 
/*      */   public String restoreSelectedTheme() {
/* 3166 */     return getThemesNode().get("Selected", null);
/*      */   }
/*      */ 
/*      */   public List<ITheme> loadThemes(Preferences parentNode)
/*      */   {
/* 3173 */     List themes = new ArrayList();
/*      */ 
/* 3175 */     Preferences themesNode = parentNode == null ? getThemesNode() : parentNode.node("Themes");
/*      */     try
/*      */     {
/* 3179 */       for (String themeName : themesNode.childrenNames()) {
/* 3180 */         ITheme theme = loadTheme(themeName, themesNode);
/* 3181 */         if (theme != null)
/* 3182 */           themes.add(theme);
/*      */       }
/*      */     }
/*      */     catch (Exception ex) {
/* 3186 */       LOGGER.warn(new StringBuilder().append("Unable to load themes : ").append(ex.getClass().getName()).append(" ").append(ex.getMessage()).toString(), ex);
/*      */     }
/*      */ 
/* 3189 */     return themes;
/*      */   }
/*      */ 
/*      */   private ITheme loadTheme(String themeName, Preferences themesRootNode) {
/*      */     try {
/* 3194 */       Theme theme = new Theme(themeName);
/*      */ 
/* 3196 */       Preferences themeNode = themesRootNode.node(themeName);
/*      */ 
/* 3198 */       Preferences colorsNode = getThemeColorsNode(themeNode);
/*      */ 
/* 3200 */       for (String chartElement : colorsNode.keys()) {
/* 3201 */         theme.setColor(ITheme.ChartElement.valueOf(chartElement), Color.decode(new StringBuilder().append("0x").append(colorsNode.get(chartElement, "000000")).toString()));
/*      */       }
/*      */ 
/* 3207 */       Preferences fontsNode = getThemeFontsNode(themeNode);
/* 3208 */       for (String textElement : fontsNode.keys()) {
/*      */         try {
/* 3210 */           theme.setFont(ITheme.TextElement.valueOf(textElement), (Font)bytesToObject(fontsNode.getByteArray(textElement, null)));
/*      */         }
/*      */         catch (Exception ex)
/*      */         {
/* 3215 */           LOGGER.warn(new StringBuilder().append("Unable to load font for ").append(textElement).append("@").append(themeName).append(" : ").append(ex.getMessage()).toString(), ex);
/*      */         }
/*      */       }
/*      */ 
/* 3219 */       Preferences strokesNode = getThemeStrokesNode(themeNode);
/* 3220 */       for (ITheme.StrokeElement strokeElement : ITheme.StrokeElement.values()) {
/*      */         try {
/* 3222 */           Preferences strokeNode = strokesNode.node(strokeElement.name());
/* 3223 */           float strokeWidth = strokeNode.getFloat("StrokeWidth", ITheme.StrokeElement.BASIC_STROKE.getLineWidth());
/* 3224 */           float[] dash = (float[])(float[])bytesToObject(strokeNode.getByteArray("StrokeDash", object2Bytes(ITheme.StrokeElement.BASIC_STROKE.getDashArray())));
/* 3225 */           theme.setStroke(strokeElement, new BasicStroke(strokeWidth, ITheme.StrokeElement.BASIC_STROKE.getEndCap(), ITheme.StrokeElement.BASIC_STROKE.getLineJoin(), ITheme.StrokeElement.BASIC_STROKE.getMiterLimit(), dash, ITheme.StrokeElement.BASIC_STROKE.getDashPhase()));
/*      */         }
/*      */         catch (Exception ex)
/*      */         {
/* 3237 */           LOGGER.warn(new StringBuilder().append("Unable to load stroke for ").append(strokeElement).append("@").append(themeName).append(" : ").append(ex.getMessage()).toString(), ex);
/*      */         }
/*      */       }
/*      */ 
/* 3241 */       return theme;
/*      */     } catch (Exception ex) {
/* 3243 */       LOGGER.warn(new StringBuilder().append("Unable to load theme [").append(themeName).append("] : ").append(ex.getMessage()).toString(), ex);
/*      */     }
/*      */ 
/* 3246 */     return null;
/*      */   }
/*      */ 
/*      */   private List<Object[]> restoreLevelsFromNode(Preferences node, IChart.Type horizontalRetracementType)
/*      */   {
/* 3253 */     int levelsCount = node.getInt("HorizontalRetracementLevelsCount", 0);
/*      */ 
/* 3255 */     if (levelsCount <= 0) {
/* 3256 */       switch (5.$SwitchMap$com$dukascopy$api$IChart$Type[horizontalRetracementType.ordinal()]) { case 1:
/* 3257 */         return ChartProperties.createDefaultLevelsFiboRetracements();
/*      */       case 2:
/* 3258 */         return ChartProperties.createDefaultLevelsFibo();
/*      */       case 3:
/* 3259 */         return ChartProperties.createDefaultLevelsPercents();
/*      */       case 4:
/* 3260 */         return ChartProperties.createDefaultLevelsFiboExtensions();
/*      */       case 5:
/* 3261 */         return ChartProperties.createDefaultLevelsFiboExtensions();
/*      */       case 6:
/* 3262 */         return ChartProperties.createDefaultLevelsFiboTimes();
/*      */       }
/*      */     }
/*      */ 
/* 3266 */     List levels = new ArrayList();
/* 3267 */     for (int i = 0; i < levelsCount; i++) {
/* 3268 */       Object[] level = new Object[3];
/*      */ 
/* 3270 */       Preferences fiboLevelNode = node.node(String.valueOf(i));
/* 3271 */       level[0] = fiboLevelNode.get("HorizontalRetracementLevelLabel", "");
/* 3272 */       level[1] = Double.valueOf(fiboLevelNode.getDouble("HorizontalRetracementLevelValue", 0.0D));
/* 3273 */       level[2] = ((Color)bytesToObject(fiboLevelNode.getByteArray("HorizontalRetracementLevelColor", null)));
/*      */ 
/* 3275 */       levels.add(level);
/*      */     }
/*      */ 
/* 3278 */     return levels;
/*      */   }
/*      */ 
/*      */   private void saveLevelsToNode(Preferences node, List<Object[]> levels)
/*      */   {
/* 3283 */     int levelsCount = levels.size();
/* 3284 */     node.putInt("HorizontalRetracementLevelsCount", levelsCount);
/*      */ 
/* 3286 */     for (int i = 0; i < levelsCount; i++) {
/* 3287 */       Object[] level = (Object[])levels.get(i);
/* 3288 */       Preferences levelNode = node.node(String.valueOf(i));
/* 3289 */       levelNode.put("HorizontalRetracementLevelLabel", String.valueOf(level[0]));
/* 3290 */       levelNode.putDouble("HorizontalRetracementLevelValue", Double.valueOf(String.valueOf(level[1])).doubleValue());
/* 3291 */       levelNode.putByteArray("HorizontalRetracementLevelColor", object2Bytes(level[2]));
/*      */     }
/*      */   }
/*      */ 
/*      */   public Map<String, List<Object[]>> restoreHorizontalRetracementPresets(IChart.Type horizontalRetracementType)
/*      */   {
/* 3297 */     Map presets = new HashMap();
/*      */ 
/* 3300 */     List defaultPreset = null;
/*      */ 
/* 3302 */     switch (5.$SwitchMap$com$dukascopy$api$IChart$Type[horizontalRetracementType.ordinal()]) { case 1:
/* 3303 */       defaultPreset = ChartProperties.createDefaultLevelsFiboRetracements(); break;
/*      */     case 2:
/* 3304 */       defaultPreset = ChartProperties.createDefaultLevelsFibo(); break;
/*      */     case 3:
/* 3305 */       defaultPreset = ChartProperties.createDefaultLevelsPercents(); break;
/*      */     case 4:
/* 3306 */       defaultPreset = ChartProperties.createDefaultLevelsFiboExtensions(); break;
/*      */     case 5:
/* 3307 */       defaultPreset = ChartProperties.createDefaultLevelsFiboExtensions(); break;
/*      */     case 6:
/* 3308 */       defaultPreset = ChartProperties.createDefaultLevelsFiboTimes();
/*      */     }
/*      */ 
/* 3311 */     if (defaultPreset != null) {
/* 3312 */       presets.put("Default", defaultPreset);
/*      */     }
/*      */ 
/* 3315 */     Preferences typeNode = getHorizontalRetracementNode().node(horizontalRetracementType.toString());
/* 3316 */     Preferences presetsNode = typeNode.node("HorizontalRetracementPresets");
/*      */ 
/* 3318 */     if (presetsNode != null) {
/*      */       try
/*      */       {
/* 3321 */         String[] presetsKeys = presetsNode.childrenNames();
/*      */ 
/* 3323 */         for (String key : presetsKeys) {
/* 3324 */           Preferences presetNode = presetsNode.node(key);
/* 3325 */           List levels = restoreLevelsFromNode(presetNode, horizontalRetracementType);
/* 3326 */           presets.put(key, levels);
/*      */         }
/*      */       }
/*      */       catch (BackingStoreException ex) {
/* 3330 */         LOGGER.error(ex.getMessage(), ex);
/*      */       }
/*      */     }
/*      */ 
/* 3334 */     return presets;
/*      */   }
/*      */ 
/*      */   public void saveHorizontalRetracementPresets(IChart.Type horizontalRetracementType, Map<String, List<Object[]>> presets) {
/* 3338 */     if ((presets == null) || (presets.size() <= 0)) {
/* 3339 */       return;
/*      */     }
/*      */ 
/* 3342 */     Preferences levelsNode = getHorizontalRetracementNode().node(horizontalRetracementType.toString());
/*      */ 
/* 3344 */     Preferences presetsNode = levelsNode.node("HorizontalRetracementPresets");
/* 3345 */     dropChildren(presetsNode);
/* 3346 */     clear(presetsNode);
/*      */ 
/* 3348 */     Iterator keyIterator = presets.keySet().iterator();
/* 3349 */     while (keyIterator.hasNext()) {
/* 3350 */       String presetName = (String)keyIterator.next();
/*      */ 
/* 3352 */       if (!presetName.equals("Default"))
/*      */       {
/* 3354 */         List levels = (List)presets.get(presetName);
/* 3355 */         Preferences preset = presetsNode.node(presetName);
/*      */ 
/* 3357 */         saveLevelsToNode(preset, levels);
/*      */       }
/*      */     }
/*      */ 
/* 3361 */     flush(levelsNode);
/*      */   }
/*      */ 
/*      */   private Preferences getThemesNode()
/*      */   {
/* 3368 */     return getUserRoot().node("Themes");
/*      */   }
/*      */ 
/*      */   private Preferences getThemeColorsNode(Preferences node) {
/* 3372 */     return node.node("Colors");
/*      */   }
/*      */ 
/*      */   private Preferences getThemeFontsNode(Preferences node) {
/* 3376 */     return node.node("Fonts");
/*      */   }
/*      */ 
/*      */   private Preferences getThemeStrokesNode(Preferences node) {
/* 3380 */     return node.node("Strokes");
/*      */   }
/*      */ 
/*      */   private void saveIndicatorParams(IndicatorBean indicatorBean, Preferences indicatorNode)
/*      */   {
/* 3388 */     Object[] optParams = indicatorBean.getOptParams();
/* 3389 */     if (optParams.length > 0) {
/* 3390 */       removeNode(indicatorNode.node("indicatorParameters"));
/* 3391 */       Preferences paramsNode = indicatorNode.node("indicatorParameters");
/* 3392 */       for (int i = 0; i < optParams.length; i++) {
/* 3393 */         Preferences paramNode = paramsNode.node(Integer.toString(i));
/* 3394 */         Object param = optParams[i];
/* 3395 */         if ((param instanceof Integer)) {
/* 3396 */           paramNode.put("paramType", "INTEGER");
/* 3397 */           paramNode.putInt("paramValue", ((Integer)param).intValue());
/* 3398 */         } else if ((param instanceof Double)) {
/* 3399 */           paramNode.put("paramType", "DOUBLE");
/* 3400 */           paramNode.putDouble("paramValue", ((Double)param).doubleValue());
/* 3401 */         } else if ((param instanceof Boolean)) {
/* 3402 */           paramNode.put("paramType", "BOOLEAN");
/* 3403 */           paramNode.putBoolean("paramValue", ((Boolean)param).booleanValue());
/*      */         }
/*      */       }
/*      */     }
/*      */ 
/* 3408 */     OfferSide[] sides = indicatorBean.getSidesForTicks();
/* 3409 */     if (sides.length > 0) {
/* 3410 */       removeNode(indicatorNode.node("indicatorsSides"));
/* 3411 */       Preferences sidesNode = indicatorNode.node("indicatorsSides");
/* 3412 */       for (int i = 0; i < sides.length; i++) {
/* 3413 */         Preferences sideNode = sidesNode.node(Integer.toString(i));
/* 3414 */         sideNode.put("sideValue", sides[i].name());
/*      */       }
/*      */     }
/*      */ 
/* 3418 */     IIndicators.AppliedPrice[] pricesForCandles = indicatorBean.getAppliedPricesForCandles();
/* 3419 */     if (pricesForCandles.length > 0) {
/* 3420 */       removeNode(indicatorNode.node("indicatorsPricesForCandles"));
/* 3421 */       Preferences pricesNode = indicatorNode.node("indicatorsPricesForCandles");
/* 3422 */       for (int i = 0; i < pricesForCandles.length; i++) {
/* 3423 */         Preferences priceNode = pricesNode.node(Integer.toString(i));
/* 3424 */         priceNode.put("priceValue", pricesForCandles[i].name());
/*      */       }
/*      */     }
/*      */ 
/* 3428 */     Color[] outputColors = indicatorBean.getOutputColors();
/* 3429 */     if (outputColors.length > 0) {
/* 3430 */       removeNode(indicatorNode.node("indicatorColors"));
/* 3431 */       Preferences colorsNode = indicatorNode.node("indicatorColors");
/* 3432 */       for (int i = 0; i < outputColors.length; i++) {
/* 3433 */         Preferences colorNode = colorsNode.node(String.valueOf(i));
/* 3434 */         if (outputColors[i] == null)
/* 3435 */           colorNode.put("colorValue", "null");
/*      */         else {
/* 3437 */           colorNode.put("colorValue", Integer.toString(outputColors[i].getRGB()));
/*      */         }
/*      */       }
/*      */     }
/*      */ 
/* 3442 */     Color[] outputColors2 = indicatorBean.getOutputColors2();
/* 3443 */     if (outputColors2.length > 0) {
/* 3444 */       removeNode(indicatorNode.node("indicatorColors2"));
/* 3445 */       Preferences colorsNode = indicatorNode.node("indicatorColors2");
/* 3446 */       for (int i = 0; i < outputColors2.length; i++) {
/* 3447 */         Preferences colorNode = colorsNode.node(String.valueOf(i));
/* 3448 */         if (outputColors2[i] == null)
/* 3449 */           colorNode.put("colorValue", "null");
/*      */         else {
/* 3451 */           colorNode.put("colorValue", Integer.toString(outputColors2[i].getRGB()));
/*      */         }
/*      */       }
/*      */     }
/*      */ 
/* 3456 */     boolean[] valuesOnChart = indicatorBean.getShowValuesOnChart();
/* 3457 */     removeNode(indicatorNode.node("indicatorValuesOnChart"));
/* 3458 */     if (valuesOnChart != null) {
/* 3459 */       Preferences valuesNode = indicatorNode.node("indicatorValuesOnChart");
/* 3460 */       for (int i = 0; i < valuesOnChart.length; i++) {
/* 3461 */         valuesNode.putBoolean(Integer.toString(i), valuesOnChart[i]);
/*      */       }
/*      */     }
/*      */ 
/* 3465 */     boolean[] showOutputs = indicatorBean.getShowOutputs();
/* 3466 */     removeNode(indicatorNode.node("indicatorShowOutput"));
/* 3467 */     if (showOutputs != null) {
/* 3468 */       Preferences valuesNode = indicatorNode.node("indicatorShowOutput");
/* 3469 */       for (int i = 0; i < showOutputs.length; i++) {
/* 3470 */         valuesNode.putBoolean(Integer.toString(i), showOutputs[i]);
/*      */       }
/*      */     }
/*      */ 
/* 3474 */     float[] opacityAlphas = indicatorBean.getOpacityAlphas();
/* 3475 */     removeNode(indicatorNode.node("indicatorOpacityAlphas"));
/* 3476 */     if (null != opacityAlphas) {
/* 3477 */       Preferences valuesNode = indicatorNode.node("indicatorOpacityAlphas");
/* 3478 */       for (int i = 0; i < opacityAlphas.length; i++) {
/* 3479 */         valuesNode.putFloat(Integer.toString(i), opacityAlphas[i]);
/*      */       }
/*      */     }
/*      */ 
/* 3483 */     removeNode(indicatorNode.node("indicatorDrawingStyles"));
/* 3484 */     OutputParameterInfo.DrawingStyle[] drawingStyles = indicatorBean.getDrawingStyles();
/* 3485 */     if ((drawingStyles != null) && (drawingStyles.length > 0)) {
/* 3486 */       Preferences drawingStylesNode = indicatorNode.node("indicatorDrawingStyles");
/* 3487 */       for (int i = 0; i < drawingStyles.length; i++) {
/* 3488 */         Preferences node = drawingStylesNode.node(String.valueOf(i));
/* 3489 */         node.put("drawingStyleValue", new StringBuilder().append("").append(drawingStyles[i]).toString());
/*      */       }
/*      */     }
/*      */ 
/* 3493 */     removeNode(indicatorNode.node("indicatorLineWidths"));
/* 3494 */     int[] lineWidths = indicatorBean.getLineWidths();
/* 3495 */     if ((lineWidths != null) && (lineWidths.length > 0)) {
/* 3496 */       Preferences lineWidthsNode = indicatorNode.node("indicatorLineWidths");
/* 3497 */       for (int i = 0; i < lineWidths.length; i++) {
/* 3498 */         Preferences node = lineWidthsNode.node(String.valueOf(i));
/* 3499 */         node.putInt("lineWidthValue", lineWidths[i]);
/*      */       }
/*      */     }
/*      */ 
/* 3503 */     removeNode(indicatorNode.node("indicatorOutputShifts"));
/* 3504 */     int[] outputShifts = indicatorBean.getOutputShifts();
/* 3505 */     if ((outputShifts != null) && (outputShifts.length > 0)) {
/* 3506 */       Preferences outputShiftsNode = indicatorNode.node("indicatorOutputShifts");
/* 3507 */       for (int i = 0; i < outputShifts.length; i++) {
/* 3508 */         Preferences node = outputShiftsNode.node(String.valueOf(i));
/* 3509 */         node.putInt("outputShiftValue", outputShifts[i]);
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   private Object[] getIndicatorParams(Preferences indicatorNode) {
/* 3515 */     List params = new LinkedList();
/* 3516 */     Preferences paramsNode = indicatorNode.node("indicatorParameters");
/* 3517 */     String[] paramIds = getChildrenNames(paramsNode);
/*      */ 
/* 3519 */     if (paramIds != null) {
/* 3520 */       Collections.sort(Arrays.asList(paramIds), new Comparator()
/*      */       {
/*      */         public int compare(String s1, String s2) {
/*      */           try {
/* 3524 */             Long l1 = Long.valueOf(s1);
/* 3525 */             Long l2 = Long.valueOf(s2);
/* 3526 */             return l1.compareTo(l2); } catch (Exception e) {
/*      */           }
/* 3528 */           return 0;
/*      */         }
/*      */       });
/*      */     }
/*      */ 
/* 3534 */     for (String paramId : paramIds) {
/* 3535 */       Preferences paramNode = paramsNode.node(paramId);
/* 3536 */       String paramType = paramNode.get("paramType", null);
/* 3537 */       if (paramType != null) {
/* 3538 */         if (paramType.equals("INTEGER"))
/* 3539 */           params.add(Integer.valueOf(paramNode.getInt("paramValue", -1)));
/* 3540 */         else if (paramType.equals("DOUBLE"))
/* 3541 */           params.add(Double.valueOf(paramNode.getDouble("paramValue", -1.0D)));
/* 3542 */         else if (paramType.equals("BOOLEAN")) {
/* 3543 */           params.add(Boolean.valueOf(paramNode.getBoolean("paramValue", false)));
/*      */         }
/*      */       }
/*      */     }
/*      */ 
/* 3548 */     return params.toArray();
/*      */   }
/*      */ 
/*      */   private Color[] getIndicatorOutputColors(Preferences indicatorNode) {
/* 3552 */     List colors = new LinkedList();
/* 3553 */     Preferences colorNodes = indicatorNode.node("indicatorColors");
/* 3554 */     String[] colorIds = getChildrenNames(colorNodes);
/* 3555 */     for (String colorId : colorIds) {
/* 3556 */       Preferences sideNode = colorNodes.node(colorId);
/* 3557 */       String rgb = sideNode.get("colorValue", "null");
/* 3558 */       if ("null".equals(rgb))
/* 3559 */         colors.add(null);
/*      */       else {
/* 3561 */         colors.add(new Color(Integer.valueOf(rgb).intValue()));
/*      */       }
/*      */     }
/* 3564 */     return (Color[])colors.toArray(new Color[colors.size()]);
/*      */   }
/*      */ 
/*      */   private Color[] getIndicatorOutputColors2(Preferences indicatorNode) {
/* 3568 */     List colors = new LinkedList();
/* 3569 */     Preferences colorNodes = indicatorNode.node("indicatorColors2");
/* 3570 */     String[] colorIds = getChildrenNames(colorNodes);
/* 3571 */     if (colorIds.length == 0) {
/* 3572 */       return getIndicatorOutputColors(indicatorNode);
/*      */     }
/* 3574 */     for (String colorId : colorIds) {
/* 3575 */       Preferences sideNode = colorNodes.node(colorId);
/* 3576 */       String rgb = sideNode.get("colorValue", "null");
/* 3577 */       if ("null".equals(rgb))
/* 3578 */         colors.add(null);
/*      */       else {
/* 3580 */         colors.add(new Color(Integer.valueOf(rgb).intValue()));
/*      */       }
/*      */     }
/* 3583 */     return (Color[])colors.toArray(new Color[colors.size()]);
/*      */   }
/*      */ 
/*      */   private void getValuesOnChart(Preferences indicatorNode, boolean[] valuesOnChart)
/*      */   {
/* 3588 */     Preferences valuesNode = indicatorNode.node("indicatorValuesOnChart");
/*      */     try {
/* 3590 */       String[] outputIdxs = valuesNode.keys();
/* 3591 */       if (outputIdxs != null)
/* 3592 */         for (String outputIdx : outputIdxs)
/*      */           try {
/* 3594 */             Integer id = Integer.valueOf(Integer.parseInt(outputIdx));
/* 3595 */             valuesOnChart[id.intValue()] = valuesNode.getBoolean(outputIdx, false);
/*      */           }
/*      */           catch (NumberFormatException ex) {
/* 3598 */             LOGGER.debug("Incorrect preferences were stored.", ex);
/*      */           }
/*      */           catch (IndexOutOfBoundsException ex) {
/* 3601 */             LOGGER.debug("Incorrect preferences were stored.", ex);
/*      */           }
/*      */     }
/*      */     catch (BackingStoreException ex)
/*      */     {
/* 3606 */       LOGGER.debug(new StringBuilder().append("Error reading properties from indicator node ").append(indicatorNode.name()).toString(), ex);
/*      */     }
/*      */   }
/*      */ 
/*      */   private void getOutputs(Preferences indicatorNode, boolean[] showOutputs) {
/* 3611 */     Preferences valuesNode = indicatorNode.node("indicatorShowOutput");
/*      */     try {
/* 3613 */       String[] outputIdxs = valuesNode.keys();
/* 3614 */       if ((outputIdxs != null) && (outputIdxs.length > 0)) {
/* 3615 */         for (String outputIdx : outputIdxs)
/*      */           try {
/* 3617 */             Integer id = Integer.valueOf(Integer.parseInt(outputIdx));
/* 3618 */             showOutputs[id.intValue()] = valuesNode.getBoolean(outputIdx, true);
/*      */           }
/*      */           catch (NumberFormatException ex) {
/* 3621 */             LOGGER.debug("Incorrect preferences were stored.", ex);
/*      */           }
/*      */           catch (IndexOutOfBoundsException ex) {
/* 3624 */             LOGGER.debug("Incorrect preferences were stored.", ex);
/*      */           }
/*      */       }
/*      */       else
/* 3628 */         for (int i = 0; i < showOutputs.length; i++)
/* 3629 */           showOutputs[i] = true;
/*      */     }
/*      */     catch (BackingStoreException ex)
/*      */     {
/* 3633 */       LOGGER.debug(new StringBuilder().append("Error reading properties from indicator node ").append(indicatorNode.name()).toString(), ex);
/*      */     }
/*      */   }
/*      */ 
/*      */   private void getOpacityAlphas(Preferences indicatorNode, float[] opacityAlphas) {
/* 3638 */     Preferences valuesNode = indicatorNode.node("indicatorOpacityAlphas");
/*      */     try
/*      */     {
/* 3641 */       String[] outputIdxs = valuesNode.keys();
/* 3642 */       if ((outputIdxs != null) && (outputIdxs.length > 0)) {
/* 3643 */         for (String outputIdx : outputIdxs)
/*      */           try {
/* 3645 */             Integer id = Integer.valueOf(Integer.parseInt(outputIdx));
/* 3646 */             opacityAlphas[id.intValue()] = valuesNode.getFloat(outputIdx, 1.0F);
/*      */           }
/*      */           catch (NumberFormatException ex) {
/* 3649 */             LOGGER.debug("Incorrect preferences were stored.", ex);
/*      */           }
/*      */           catch (IndexOutOfBoundsException ex) {
/* 3652 */             LOGGER.debug("Incorrect preferences were stored.", ex);
/*      */           }
/*      */       }
/*      */       else
/* 3656 */         for (int i = 0; i < opacityAlphas.length; i++)
/* 3657 */           opacityAlphas[i] = 1.0F;
/*      */     }
/*      */     catch (BackingStoreException ex)
/*      */     {
/* 3661 */       LOGGER.debug(new StringBuilder().append("Error reading properties from indicator node ").append(indicatorNode.name()).toString(), ex);
/*      */     }
/*      */   }
/*      */ 
/*      */   private IIndicators.AppliedPrice[] getIndicatorAppliedPrices(Preferences indicatorNode) {
/* 3666 */     List prices = new LinkedList();
/* 3667 */     Preferences pricesNode = indicatorNode.node("indicatorsPricesForCandles");
/* 3668 */     String[] priceIds = getChildrenNames(pricesNode);
/* 3669 */     for (String priceId : priceIds) {
/* 3670 */       Preferences priceNode = pricesNode.node(priceId);
/* 3671 */       String priceValue = priceNode.get("priceValue", null);
/* 3672 */       prices.add(IIndicators.AppliedPrice.valueOf(priceValue));
/*      */     }
/* 3674 */     return (IIndicators.AppliedPrice[])prices.toArray(new IIndicators.AppliedPrice[prices.size()]);
/*      */   }
/*      */ 
/*      */   private OfferSide[] getIndicatorOfferSide(Preferences indicatorNode) {
/* 3678 */     List offerSides = new LinkedList();
/* 3679 */     Preferences sidesNodes = indicatorNode.node("indicatorsSides");
/* 3680 */     String[] sideIds = getChildrenNames(sidesNodes);
/* 3681 */     for (String sideId : sideIds) {
/* 3682 */       Preferences sideNode = sidesNodes.node(sideId);
/* 3683 */       String sideValue = sideNode.get("sideValue", null);
/* 3684 */       offerSides.add(OfferSide.valueOf(sideValue));
/*      */     }
/* 3686 */     return (OfferSide[])offerSides.toArray(new OfferSide[offerSides.size()]);
/*      */   }
/*      */ 
/*      */   private OutputParameterInfo.DrawingStyle[] getIndicatorDrawingStyles(Preferences indicatorNode) {
/* 3690 */     List drawingStyles = new LinkedList();
/* 3691 */     Preferences drawingStylesNode = indicatorNode.node("indicatorDrawingStyles");
/* 3692 */     String[] drawingStylesIds = getChildrenNames(drawingStylesNode);
/* 3693 */     for (String drawingStylesId : drawingStylesIds) {
/* 3694 */       Preferences node = drawingStylesNode.node(drawingStylesId);
/* 3695 */       String drawingStyle = node.get("drawingStyleValue", null);
/*      */ 
/* 3697 */       if (drawingStyle == null) {
/* 3698 */         return null;
/*      */       }
/*      */       try
/*      */       {
/* 3702 */         drawingStyles.add(OutputParameterInfo.DrawingStyle.valueOf(drawingStyle));
/*      */       } catch (Exception ex) {
/* 3704 */         return null;
/*      */       }
/*      */     }
/*      */ 
/* 3708 */     if (drawingStyles.size() == 0) {
/* 3709 */       return null;
/*      */     }
/*      */ 
/* 3712 */     return (OutputParameterInfo.DrawingStyle[])drawingStyles.toArray(new OutputParameterInfo.DrawingStyle[drawingStyles.size()]);
/*      */   }
/*      */ 
/*      */   private int[] getIndicatorLineWidths(Preferences indicatorNode) {
/* 3716 */     Preferences lineWidthsNode = indicatorNode.node("indicatorLineWidths");
/* 3717 */     String[] ids = getChildrenNames(lineWidthsNode);
/* 3718 */     if ((ids == null) || (ids.length == 0)) {
/* 3719 */       return null;
/*      */     }
/*      */ 
/* 3722 */     int[] lineWidths = new int[ids.length];
/* 3723 */     for (int i = 0; i < ids.length; i++) {
/* 3724 */       Preferences node = lineWidthsNode.node(ids[i]);
/* 3725 */       lineWidths[i] = node.getInt("lineWidthValue", 1);
/*      */     }
/*      */ 
/* 3728 */     return lineWidths;
/*      */   }
/*      */ 
/*      */   private int[] getIndicatorOutputShifts(Preferences indicatorNode) {
/* 3732 */     Preferences shiftsNode = indicatorNode.node("indicatorOutputShifts");
/* 3733 */     String[] ids = getChildrenNames(shiftsNode);
/* 3734 */     if ((ids == null) || (ids.length == 0)) {
/* 3735 */       return null;
/*      */     }
/*      */ 
/* 3738 */     int[] shifts = new int[ids.length];
/* 3739 */     for (int i = 0; i < ids.length; i++) {
/* 3740 */       Preferences node = shiftsNode.node(ids[i]);
/* 3741 */       shifts[i] = node.getInt("outputShiftValue", 0);
/*      */     }
/* 3743 */     return shifts;
/*      */   }
/*      */ 
/*      */   private Rectangle getBounds(Preferences node)
/*      */   {
/* 3752 */     byte[] boundsRaw = node.getByteArray("bounds", null);
/* 3753 */     if ((boundsRaw == null) || (boundsRaw.length <= 0))
/* 3754 */       return null;
/*      */     try
/*      */     {
/* 3757 */       return (Rectangle)bytesToObject(boundsRaw);
/*      */     } catch (Exception ex) {
/* 3759 */       LOGGER.warn(new StringBuilder().append("Unable to restore frame bounds : ").append(ex.getMessage()).toString());
/* 3760 */     }return null;
/*      */   }
/*      */ 
/*      */   private void removeNode(Preferences nodeToBeRemoved)
/*      */   {
/*      */     try {
/* 3766 */       nodeToBeRemoved.removeNode();
/*      */     } catch (BackingStoreException e) {
/* 3768 */       LOGGER.error(e.getMessage(), e);
/*      */     }
/*      */   }
/*      */ 
/*      */   private byte[] object2Bytes(Object object) {
/*      */     try {
/* 3774 */       return StorageUtils.object2bytes(object); } catch (IOException e) {
/*      */     }
/* 3776 */     throw new RuntimeException(e);
/*      */   }
/*      */ 
/*      */   private Object bytesToObject(byte[] bytes)
/*      */   {
/*      */     try {
/* 3782 */       return StorageUtils.bytes2Object(bytes); } catch (Exception e) {
/*      */     }
/* 3784 */     throw new RuntimeException(e);
/*      */   }
/*      */ 
/*      */   public void flush(Preferences node)
/*      */   {
/*      */     try {
/* 3790 */       node.flush();
/*      */     } catch (BackingStoreException e) {
/* 3792 */       LOGGER.error(e.getMessage(), e);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void clear(Preferences node) {
/*      */     try {
/* 3798 */       node.clear();
/*      */     } catch (BackingStoreException e) {
/* 3800 */       LOGGER.error(e.getMessage(), e);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void dropChildren(Preferences node) {
/*      */     try {
/* 3806 */       for (String childName : node.childrenNames())
/* 3807 */         node.node(childName).removeNode();
/*      */     }
/*      */     catch (BackingStoreException e) {
/* 3810 */       LOGGER.error(e.getMessage(), e);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void sync(Preferences node) {
/*      */     try {
/* 3816 */       node.sync();
/*      */     } catch (BackingStoreException e) {
/* 3818 */       LOGGER.error(e.getMessage(), e);
/*      */     }
/*      */   }
/*      */ 
/*      */   private boolean containsData(Preferences node) {
/*      */     try {
/* 3824 */       return node.keys().length > 0;
/*      */     } catch (BackingStoreException ex) {
/* 3826 */       LOGGER.error(ex.getMessage(), ex);
/* 3827 */     }return false;
/*      */   }
/*      */ 
/*      */   private String[] getChildrenNames(Preferences parentNode)
/*      */   {
/*      */     try {
/* 3833 */       return parentNode.childrenNames();
/*      */     } catch (BackingStoreException e) {
/* 3835 */       LOGGER.debug("Error getting children names.", e);
/* 3836 */     }return null;
/*      */   }
/*      */ 
/*      */   private boolean strategyFilesDontExist(String sourceFullFileName, String binaryFullFileName)
/*      */   {
/* 3841 */     return (!new File(sourceFullFileName).exists()) && (!new File(binaryFullFileName).exists());
/*      */   }
/*      */ 
/*      */   public List<RowSorter.SortKey> restoreTableKeys(String tableId) {
/* 3845 */     List result = new ArrayList();
/*      */ 
/* 3847 */     Preferences allTablesNode = getTablesSettingsNode();
/* 3848 */     Preferences tableNode = allTablesNode.node(tableId);
/* 3849 */     Preferences tableSortingNode = tableNode.node("tableSort");
/*      */ 
/* 3851 */     int sortKeysCount = tableSortingNode.getInt("sortKeysCount", 0);
/* 3852 */     for (int i = 0; i < sortKeysCount; i++) {
/* 3853 */       Preferences tableSortNode = tableSortingNode.node(String.valueOf(i));
/* 3854 */       int columntId = tableSortNode.getInt("columnId", -1);
/* 3855 */       String sortType = tableSortNode.get("sortType", null);
/* 3856 */       SortOrder sortOrder = SortOrder.valueOf(sortType);
/* 3857 */       if ((columntId > -1) && (sortOrder != null)) {
/* 3858 */         RowSorter.SortKey sortKey = new RowSorter.SortKey(columntId, sortOrder);
/* 3859 */         result.add(sortKey);
/*      */       }
/*      */     }
/*      */ 
/* 3863 */     return result;
/*      */   }
/*      */ 
/*      */   public void saveTableSortKeys(String tableId, List<? extends RowSorter.SortKey> sortKeys) {
/* 3867 */     if (sortKeys == null) {
/* 3868 */       return;
/*      */     }
/*      */ 
/* 3871 */     Preferences allTablesNode = getTablesSettingsNode();
/* 3872 */     Preferences tableNode = allTablesNode.node(tableId);
/* 3873 */     Preferences tableSortingNode = tableNode.node("tableSort");
/* 3874 */     tableSortingNode.putInt("sortKeysCount", sortKeys.size());
/* 3875 */     for (int i = 0; i < sortKeys.size(); i++) {
/* 3876 */       RowSorter.SortKey sortKey = (RowSorter.SortKey)sortKeys.get(i);
/* 3877 */       Preferences tableSortNode = tableSortingNode.node(String.valueOf(i));
/* 3878 */       tableSortNode.putInt("columnId", sortKey.getColumn());
/* 3879 */       tableSortNode.put("sortType", sortKey.getSortOrder().toString());
/*      */     }
/*      */   }
/*      */ 
/*      */   public void restoreTableColumns(String tableId, TableColumnModel tableColumnModel) {
/* 3884 */     Map tableColumnsMap = new HashMap();
/* 3885 */     Enumeration tableColumns = tableColumnModel.getColumns();
/*      */ 
/* 3887 */     List columns = new ArrayList();
/* 3888 */     while (tableColumns.hasMoreElements()) {
/* 3889 */       TableColumn tableColumn = (TableColumn)tableColumns.nextElement();
/* 3890 */       columns.add(tableColumn);
/*      */     }
/*      */ 
/* 3893 */     for (TableColumn tableColumn : columns) {
/* 3894 */       restoreTableColumn(tableId, tableColumn);
/* 3895 */       int position = restoreTableColumnPosition(tableId, tableColumn, tableColumnModel);
/*      */ 
/* 3897 */       tableColumnsMap.put(tableColumn, Integer.valueOf(position)); } break label128;
/* 3900 */     int iterations = 0;
/*      */     label128: 
/*      */     do { while (putColumnsOnRightPositions(columns, tableColumnsMap, tableColumnModel));
/* 3901 */       iterations++; } while (iterations > 5);
/*      */   }
/*      */ 
/*      */   private boolean putColumnsOnRightPositions(List<TableColumn> columns, Map<TableColumn, Integer> tableColumnsMap, TableColumnModel tableColumnModel)
/*      */   {
/* 3910 */     boolean moved = false;
/* 3911 */     for (TableColumn tableColumn : columns) {
/* 3912 */       int currentIndex = getCurrentColumnIndex(tableColumnModel, tableColumn);
/* 3913 */       int restoredIndex = ((Integer)tableColumnsMap.get(tableColumn)).intValue();
/* 3914 */       if (restoredIndex > currentIndex) {
/* 3915 */         for (int i = currentIndex; i < restoredIndex; i++) {
/* 3916 */           tableColumnModel.moveColumn(i, i + 1);
/*      */         }
/* 3918 */         moved = true;
/*      */       }
/* 3920 */       else if (restoredIndex < currentIndex) {
/* 3921 */         for (int i = currentIndex; i > restoredIndex; i--) {
/* 3922 */           tableColumnModel.moveColumn(i, i - 1);
/*      */         }
/* 3924 */         moved = true;
/*      */       }
/*      */     }
/* 3927 */     return moved;
/*      */   }
/*      */ 
/*      */   private int getCurrentColumnIndex(TableColumnModel tableColumnModel, TableColumn tableColumn)
/*      */   {
/* 3942 */     Enumeration tableColumns = tableColumnModel.getColumns();
/* 3943 */     int i = -1;
/* 3944 */     while (tableColumns.hasMoreElements()) {
/* 3945 */       i++;
/* 3946 */       TableColumn currentTableColumn = (TableColumn)tableColumns.nextElement();
/* 3947 */       if (currentTableColumn == tableColumn) {
/* 3948 */         return i;
/*      */       }
/*      */     }
/* 3951 */     return -1;
/*      */   }
/*      */ 
/*      */   private void restoreTableColumn(String tableId, TableColumn tableColumn) {
/* 3955 */     Preferences columnNode = getTableColumnNode(tableId, tableColumn);
/* 3956 */     int width = columnNode.getInt("columnWidth", tableColumn.getWidth());
/* 3957 */     tableColumn.setWidth(width);
/* 3958 */     tableColumn.setPreferredWidth(width);
/*      */   }
/*      */ 
/*      */   private int restoreTableColumnPosition(String tableId, TableColumn tableColumn, TableColumnModel tableColumnModel) {
/* 3962 */     Preferences columnNode = getTableColumnNode(tableId, tableColumn);
/* 3963 */     int position = columnNode.getInt("columnPosition", tableColumnModel.getColumnIndex(tableColumn.getIdentifier()));
/* 3964 */     return position;
/*      */   }
/*      */ 
/*      */   private Preferences getTableColumnNode(String tableId, TableColumn tableColumn) {
/* 3968 */     Preferences allTablesNode = getTablesSettingsNode();
/* 3969 */     Preferences tableNode = allTablesNode.node(tableId);
/* 3970 */     Preferences tableColumnsNode = tableNode.node("tableColumns");
/*      */ 
/* 3972 */     String columnIdStr = getColumnId(tableColumn);
/*      */ 
/* 3974 */     Preferences columnNode = tableColumnsNode.node(columnIdStr);
/* 3975 */     return columnNode;
/*      */   }
/*      */ 
/*      */   public void saveTableColumns(String tableId, TableColumnModel tableColumnModel) {
/* 3979 */     Enumeration tableColumns = tableColumnModel.getColumns();
/* 3980 */     while (tableColumns.hasMoreElements()) {
/* 3981 */       TableColumn tableColumn = (TableColumn)tableColumns.nextElement();
/* 3982 */       int position = getCurrentColumnIndex(tableColumnModel, tableColumn);
/* 3983 */       saveTableColumn(tableId, tableColumn, position);
/*      */     }
/*      */   }
/*      */ 
/*      */   private String getColumnId(TableColumn tableColumn) {
/* 3988 */     Object columnId = Integer.valueOf(tableColumn.getModelIndex());
/* 3989 */     if (columnId == null) {
/* 3990 */       throw new IllegalArgumentException("Can not work with columnId == null");
/*      */     }
/* 3992 */     String columnIdStr = String.valueOf(columnId);
/* 3993 */     columnIdStr = columnIdStr.replace("/", "*");
/* 3994 */     return columnIdStr;
/*      */   }
/*      */ 
/*      */   private void saveTableColumn(String tableId, TableColumn tableColumn, int position) {
/* 3998 */     Preferences allTablesNode = getTablesSettingsNode();
/* 3999 */     Preferences tableNode = allTablesNode.node(tableId);
/* 4000 */     Preferences tableColumnsNode = tableNode.node("tableColumns");
/*      */ 
/* 4002 */     String columnIdStr = getColumnId(tableColumn);
/* 4003 */     Preferences columnNode = tableColumnsNode.node(columnIdStr);
/* 4004 */     columnNode.putInt("columnWidth", tableColumn.getWidth());
/* 4005 */     columnNode.putInt("columnPosition", position);
/*      */   }
/*      */ 
/*      */   private Preferences getWorkspaceSettingsNode()
/*      */   {
/* 4018 */     return getCommonNode().node("WorkspaceSetings");
/*      */   }
/*      */ 
/*      */   private Preferences getWorkspaceOptionsNode() {
/* 4022 */     return getWorkspaceSettingsNode().node("WorkspaceOptions");
/*      */   }
/*      */ 
/*      */   public void saveWorkspaceAutoSavePeriod(Long workspaceAutoSavePeriod) {
/* 4026 */     getWorkspaceOptionsNode().putLong("WorkspaceAutoSavePeriod", workspaceAutoSavePeriod.longValue());
/*      */   }
/*      */ 
/*      */   public Long restoreWorkspaceAutoSavePeriod(Long defaultWorkspaceAutoSavePeriod) {
/* 4030 */     return Long.valueOf(getWorkspaceOptionsNode().getLong("WorkspaceAutoSavePeriod", defaultWorkspaceAutoSavePeriod.longValue()));
/*      */   }
/*      */ 
/*      */   public void saveWorkspaceSaveOnExit(Boolean workspaceSaveOnExit) {
/* 4034 */     getWorkspaceOptionsNode().putBoolean("WorkspaceSaveOnExit", workspaceSaveOnExit.booleanValue());
/*      */   }
/*      */ 
/*      */   public Boolean restoreWorkspaceSaveOnExit(Boolean defaultWorkspaceSaveOnExit) {
/* 4038 */     return Boolean.valueOf(getWorkspaceOptionsNode().getBoolean("WorkspaceSaveOnExit", defaultWorkspaceSaveOnExit.booleanValue()));
/*      */   }
/*      */ 
/*      */   private Preferences getPeriodSettingsNode()
/*      */   {
/* 4044 */     return getCommonNode().node("PeriodSettings");
/*      */   }
/*      */ 
/*      */   public List<JForexPeriod> getDefaultChartPeriods()
/*      */   {
/* 4049 */     List defaults = new ArrayList();
/*      */ 
/* 4051 */     defaults.add(new JForexPeriod(DataType.TICKS, Period.TICK));
/* 4052 */     defaults.add(new JForexPeriod(DataType.TICK_BAR, Period.TICK, null, null, TickBarSize.THREE));
/*      */ 
/* 4054 */     defaults.add(new JForexPeriod(DataType.TIME_PERIOD_AGGREGATION, Period.TEN_SECS));
/* 4055 */     defaults.add(new JForexPeriod(DataType.TIME_PERIOD_AGGREGATION, Period.ONE_MIN));
/* 4056 */     defaults.add(new JForexPeriod(DataType.TIME_PERIOD_AGGREGATION, Period.FIVE_MINS));
/* 4057 */     defaults.add(new JForexPeriod(DataType.TIME_PERIOD_AGGREGATION, Period.TEN_MINS));
/* 4058 */     defaults.add(new JForexPeriod(DataType.TIME_PERIOD_AGGREGATION, Period.FIFTEEN_MINS));
/* 4059 */     defaults.add(new JForexPeriod(DataType.TIME_PERIOD_AGGREGATION, Period.THIRTY_MINS));
/* 4060 */     defaults.add(new JForexPeriod(DataType.TIME_PERIOD_AGGREGATION, Period.ONE_HOUR));
/* 4061 */     defaults.add(new JForexPeriod(DataType.TIME_PERIOD_AGGREGATION, Period.FOUR_HOURS));
/* 4062 */     defaults.add(new JForexPeriod(DataType.TIME_PERIOD_AGGREGATION, Period.DAILY));
/* 4063 */     defaults.add(new JForexPeriod(DataType.TIME_PERIOD_AGGREGATION, Period.WEEKLY));
/* 4064 */     defaults.add(new JForexPeriod(DataType.TIME_PERIOD_AGGREGATION, Period.MONTHLY));
/*      */ 
/* 4066 */     defaults.add(new JForexPeriod(DataType.PRICE_RANGE_AGGREGATION, Period.TICK, PriceRange.ONE_PIP));
/* 4067 */     defaults.add(new JForexPeriod(DataType.RENKO, Period.TICK, PriceRange.ONE_PIP));
/* 4068 */     defaults.add(new JForexPeriod(DataType.POINT_AND_FIGURE, Period.TICK, PriceRange.ONE_PIP, ReversalAmount.THREE));
/*      */ 
/* 4070 */     return defaults;
/*      */   }
/*      */ 
/*      */   public void saveChartPeriods(List<JForexPeriod> periods) {
/* 4074 */     if ((periods == null) || (periods.isEmpty())) {
/* 4075 */       return;
/*      */     }
/*      */     try
/*      */     {
/* 4079 */       getPeriodSettingsNode().clear();
/*      */     } catch (BackingStoreException e) {
/* 4081 */       LOGGER.error(e.getLocalizedMessage(), e);
/*      */     }
/*      */ 
/* 4084 */     getPeriodSettingsNode().put("size", String.valueOf(periods.size()));
/*      */ 
/* 4086 */     for (int i = 0; i < periods.size(); i++)
/* 4087 */       saveChartPeriod(i, (JForexPeriod)periods.get(i));
/*      */   }
/*      */ 
/*      */   private void saveChartPeriod(int number, JForexPeriod period)
/*      */   {
/* 4092 */     Preferences node = getPeriodSettingsNode().node(String.valueOf(number));
/* 4093 */     saveJForexPeriod(node, period);
/*      */   }
/*      */ 
/*      */   public List<JForexPeriod> restoreChartPeriods() {
/* 4097 */     int size = getPeriodSettingsNode().getInt("size", 0);
/* 4098 */     if (size <= 0) {
/* 4099 */       return getDefaultChartPeriods();
/*      */     }
/*      */ 
/* 4102 */     List result = new ArrayList();
/*      */ 
/* 4104 */     for (int i = 0; i < size; i++) {
/*      */       try
/*      */       {
/* 4107 */         Preferences node = getPeriodSettingsNode().node(String.valueOf(i));
/* 4108 */         JForexPeriod jForexPeriod = restoreJForexPeriod(node);
/* 4109 */         if (jForexPeriod != null)
/* 4110 */           result.add(jForexPeriod);
/*      */       }
/*      */       catch (Throwable t) {
/* 4113 */         LOGGER.error(t.getLocalizedMessage(), t);
/*      */       }
/*      */     }
/*      */ 
/* 4117 */     result = removeDuplicated(result);
/* 4118 */     result = sortChartPeriods(result);
/*      */ 
/* 4120 */     return result;
/*      */   }
/*      */ 
/*      */   private JForexPeriod restoreJForexPeriod(Preferences node)
/*      */   {
/* 4125 */     String dataTypeName = node.get("dataType", DataType.TIME_PERIOD_AGGREGATION.toString());
/* 4126 */     dataTypeName = "TRADE_BAR".equals(dataTypeName) ? DataType.TICK_BAR.name() : dataTypeName;
/*      */ 
/* 4128 */     DataType dataType = DataType.valueOf(dataTypeName);
/* 4129 */     PriceRange priceRange = null;
/* 4130 */     Period period = null;
/* 4131 */     ReversalAmount reversalAmount = null;
/* 4132 */     TickBarSize tickBarSize = null;
/*      */ 
/* 4134 */     if ((DataType.PRICE_RANGE_AGGREGATION.equals(dataType)) || (DataType.POINT_AND_FIGURE.equals(dataType)) || (DataType.RENKO.equals(dataType)))
/*      */     {
/* 4139 */       priceRange = PriceRange.valueOf(node.getInt("pipCount", PriceRange.ONE_PIP.getPipCount()));
/*      */     }
/*      */ 
/* 4142 */     if (DataType.POINT_AND_FIGURE.equals(dataType)) {
/* 4143 */       reversalAmount = ReversalAmount.valueOf(node.getInt("reversalAmount", ReversalAmount.THREE.getAmount()));
/*      */     }
/*      */ 
/* 4146 */     if (DataType.TICK_BAR.equals(dataType)) {
/* 4147 */       tickBarSize = TickBarSize.valueOf(node.getInt("tradeBarSize", 3));
/*      */     }
/*      */ 
/* 4150 */     if ((DataType.TICKS.equals(dataType)) || (DataType.PRICE_RANGE_AGGREGATION.equals(dataType)) || (DataType.TICK_BAR.equals(dataType)))
/*      */     {
/* 4155 */       period = Period.TICK;
/*      */     }
/*      */     else
/*      */     {
/* 4159 */       String periodName = node.get("periodName", null);
/* 4160 */       String periodUnitsCount = node.get("periodUnitsCount", String.valueOf(10));
/* 4161 */       if (((periodName != null) && (Period.TICK.name().equals(periodName))) || ((periodName == null) && (periodUnitsCount != null) && (periodUnitsCount.equals("-1"))))
/*      */       {
/* 4165 */         period = Period.TICK;
/*      */       }
/*      */       else {
/* 4168 */         String periodUnit = node.get("periodUnit", Unit.Second.toString());
/* 4169 */         if ("null".equals(periodUnit)) {
/* 4170 */           period = Period.valueOf(periodName);
/*      */         }
/*      */         else {
/* 4173 */           period = Period.createCustomPeriod(Unit.valueOf(periodUnit), Integer.parseInt(periodUnitsCount));
/*      */         }
/*      */       }
/*      */     }
/* 4177 */     return new JForexPeriod(dataType, period, priceRange, reversalAmount, tickBarSize);
/*      */   }
/*      */ 
/*      */   private List<JForexPeriod> removeDuplicated(List<JForexPeriod> list)
/*      */   {
/* 4182 */     if ((list == null) || (list.size() <= 1)) {
/* 4183 */       return list;
/*      */     }
/* 4185 */     List result = new ArrayList();
/* 4186 */     for (JForexPeriod period : list) {
/* 4187 */       if (!result.contains(period)) {
/* 4188 */         result.add(period);
/*      */       }
/*      */     }
/* 4191 */     return result;
/*      */   }
/*      */ 
/*      */   private void saveJForexPeriod(Preferences node, JForexPeriod period) {
/* 4195 */     if ((node != null) && (period != null))
/*      */     {
/* 4197 */       node.put("dataType", period.getDataType().toString());
/*      */ 
/* 4199 */       if ((DataType.PRICE_RANGE_AGGREGATION.equals(period.getDataType())) || (DataType.POINT_AND_FIGURE.equals(period.getDataType())) || (DataType.RENKO.equals(period.getDataType())))
/*      */       {
/* 4204 */         node.putInt("pipCount", period.getPriceRange().getPipCount());
/*      */       }
/*      */ 
/* 4207 */       if (DataType.POINT_AND_FIGURE.equals(period.getDataType())) {
/* 4208 */         node.putInt("reversalAmount", period.getReversalAmount().getAmount());
/*      */       }
/*      */ 
/* 4211 */       if (DataType.TICK_BAR.equals(period.getDataType())) {
/* 4212 */         node.putInt("tradeBarSize", period.getTickBarSize().getSize());
/*      */       }
/*      */ 
/* 4215 */       node.put("periodUnit", period.getPeriod().getUnit() == null ? "null" : period.getPeriod().getUnit().toString());
/* 4216 */       node.put("periodUnitsCount", String.valueOf(period.getPeriod().getNumOfUnits()));
/*      */     }
/*      */   }
/*      */ 
/*      */   public List<JForexPeriod> sortChartPeriods(List<JForexPeriod> periods)
/*      */   {
/* 4229 */     Collections.sort(periods, new Comparator()
/*      */     {
/*      */       public int compare(JForexPeriod o1, JForexPeriod o2) {
/* 4232 */         if ((DataType.TIME_PERIOD_AGGREGATION.equals(o1.getDataType())) && (DataType.TIME_PERIOD_AGGREGATION.equals(o2.getDataType())))
/*      */         {
/* 4236 */           return o1.getPeriod().compareTo(o2.getPeriod());
/*      */         }
/* 4238 */         if (((DataType.PRICE_RANGE_AGGREGATION.equals(o1.getDataType())) && (DataType.PRICE_RANGE_AGGREGATION.equals(o2.getDataType()))) || ((DataType.RENKO.equals(o1.getDataType())) && (DataType.RENKO.equals(o2.getDataType()))))
/*      */         {
/* 4249 */           return o1.getPriceRange().compareTo(o2.getPriceRange());
/*      */         }
/* 4251 */         if ((DataType.TICK_BAR.equals(o1.getDataType())) && (DataType.TICK_BAR.equals(o2.getDataType())))
/*      */         {
/* 4255 */           return o1.getTickBarSize().compareTo(o2.getTickBarSize());
/*      */         }
/* 4257 */         if ((DataType.POINT_AND_FIGURE.equals(o1.getDataType())) && (DataType.POINT_AND_FIGURE.equals(o2.getDataType())))
/*      */         {
/* 4261 */           int result = o1.getPeriod().compareTo(o2.getPeriod());
/* 4262 */           if (result == 0) {
/* 4263 */             result = o1.getPriceRange().compareTo(o2.getPriceRange());
/* 4264 */             if (result == 0) {
/* 4265 */               result = o1.getReversalAmount().compareTo(o2.getReversalAmount());
/*      */             }
/*      */           }
/* 4268 */           return result;
/*      */         }
/*      */ 
/* 4271 */         return o1.getDataType().compareTo(o2.getDataType());
/*      */       }
/*      */     });
/* 4276 */     return periods;
/*      */   }
/*      */ 
/*      */   public static boolean isJforexMode()
/*      */   {
/* 4284 */     Preferences node = Preferences.systemRoot().node("common");
/* 4285 */     return node.getBoolean("isJforexMode", true);
/*      */   }
/*      */ 
/*      */   public static void setJForexMode(boolean isJforex) {
/* 4289 */     Preferences node = Preferences.systemRoot().node("common");
/* 4290 */     GreedContext.IS_JCLIENT_INVOKED = !isJforex;
/* 4291 */     node.putBoolean("isJforexMode", isJforex);
/*      */   }
/*      */ 
/*      */   public void saveContValidationRates(Map<Instrument, BigDecimal> rates)
/*      */   {
/*      */     try
/*      */     {
/* 4300 */       getContestRatesNode().removeNode();
/* 4301 */       getUserRoot().flush();
/*      */     } catch (BackingStoreException bse) {
/* 4303 */       LOGGER.error(bse.getMessage(), bse);
/* 4304 */       return;
/*      */     }
/*      */ 
/* 4307 */     Preferences contestRateNode = getContestRatesNode();
/*      */ 
/* 4309 */     for (Map.Entry entry : rates.entrySet())
/* 4310 */       contestRateNode.putDouble(((Instrument)entry.getKey()).toString(), ((BigDecimal)entry.getValue()).doubleValue());
/*      */   }
/*      */ 
/*      */   public Double restoreContestRate(Instrument instrument)
/*      */   {
/* 4316 */     return Double.valueOf(getContestRatesNode().getDouble(instrument.toString(), -1.0D));
/*      */   }
/*      */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.settings.PreferencesStorage
 * JD-Core Version:    0.6.0
 */