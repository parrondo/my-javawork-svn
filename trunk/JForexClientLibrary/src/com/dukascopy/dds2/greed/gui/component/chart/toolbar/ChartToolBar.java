/*      */ package com.dukascopy.dds2.greed.gui.component.chart.toolbar;
/*      */ 
/*      */ import com.dukascopy.api.DataType;
/*      */ import com.dukascopy.api.DataType.DataPresentationType;
/*      */ import com.dukascopy.api.IChart;
/*      */ import com.dukascopy.api.Instrument;
/*      */ import com.dukascopy.api.OfferSide;
/*      */ import com.dukascopy.api.Period;
/*      */ import com.dukascopy.api.feed.FeedDescriptor;
/*      */ import com.dukascopy.api.impl.IndicatorContext;
/*      */ import com.dukascopy.api.impl.IndicatorHolder;
/*      */ import com.dukascopy.api.impl.IndicatorWrapper;
/*      */ import com.dukascopy.api.indicators.IIndicator;
/*      */ import com.dukascopy.api.indicators.OutputParameterInfo;
/*      */ import com.dukascopy.charts.data.datacache.JForexPeriod;
/*      */ import com.dukascopy.charts.listener.ChartModeChangeListener;
/*      */ import com.dukascopy.charts.listener.ChartModeChangeListener.ChartMode;
/*      */ import com.dukascopy.charts.listener.DisableEnableListener;
/*      */ import com.dukascopy.charts.main.DDSChartsActionAdapter;
/*      */ import com.dukascopy.charts.main.interfaces.DDSChartsController;
/*      */ import com.dukascopy.charts.math.indicators.IndicatorsProvider;
/*      */ import com.dukascopy.charts.persistence.ChartBean;
/*      */ import com.dukascopy.charts.persistence.LastUsedIndicatorBean;
/*      */ import com.dukascopy.charts.utils.ChartsLocalizator;
/*      */ import com.dukascopy.dds2.greed.GreedContext;
/*      */ import com.dukascopy.dds2.greed.agent.Strategies;
/*      */ import com.dukascopy.dds2.greed.gui.ClientForm;
/*      */ import com.dukascopy.dds2.greed.gui.ClientFormLayoutManager;
/*      */ import com.dukascopy.dds2.greed.gui.DealPanel;
/*      */ import com.dukascopy.dds2.greed.gui.InstrumentAvailabilityManager;
/*      */ import com.dukascopy.dds2.greed.gui.component.WorkspacePanel;
/*      */ import com.dukascopy.dds2.greed.gui.component.chart.TabsAndFramesTabbedPane;
/*      */ import com.dukascopy.dds2.greed.gui.component.chart.TabsOrderingMenuContainer.Action;
/*      */ import com.dukascopy.dds2.greed.gui.component.chart.buttons.ButtonMouseListener;
/*      */ import com.dukascopy.dds2.greed.gui.component.chart.buttons.PanelMenuItem;
/*      */ import com.dukascopy.dds2.greed.gui.component.chart.listeners.IEventHandler;
/*      */ import com.dukascopy.dds2.greed.gui.component.chart.listeners.IEventHandler.Event;
/*      */ import com.dukascopy.dds2.greed.gui.component.chart.listeners.ToolBarButtonBehaviourControlListener;
/*      */ import com.dukascopy.dds2.greed.gui.component.ticker.TickerPanel;
/*      */ import com.dukascopy.dds2.greed.gui.helpers.ChartTemplatesListManager;
/*      */ import com.dukascopy.dds2.greed.gui.helpers.CustomIndicatorListManager;
/*      */ import com.dukascopy.dds2.greed.gui.helpers.IWorkspaceHelper;
/*      */ import com.dukascopy.dds2.greed.gui.l10n.components.JLocalizableButton;
/*      */ import com.dukascopy.dds2.greed.gui.l10n.components.JLocalizableLabel;
/*      */ import com.dukascopy.dds2.greed.gui.l10n.components.JLocalizableMenu;
/*      */ import com.dukascopy.dds2.greed.gui.l10n.components.JLocalizableMenuItem;
/*      */ import com.dukascopy.dds2.greed.gui.resizing.ResizingManager.ComponentSize;
/*      */ import com.dukascopy.dds2.greed.gui.resizing.components.JResizableButton;
/*      */ import com.dukascopy.dds2.greed.gui.resizing.components.JResizableComboBox;
/*      */ import com.dukascopy.dds2.greed.gui.resizing.components.JResizableLabel;
/*      */ import com.dukascopy.dds2.greed.gui.resizing.components.ResizableIcon;
/*      */ import com.dukascopy.dds2.greed.gui.settings.ClientSettingsStorage;
/*      */ import com.dukascopy.dds2.greed.model.MarketView;
/*      */ import com.dukascopy.dds2.greed.util.NotificationUtilsProvider;
/*      */ import com.dukascopy.dds2.greed.util.PlatformSpecific;
/*      */ import java.awt.Color;
/*      */ import java.awt.Component;
/*      */ import java.awt.Cursor;
/*      */ import java.awt.Insets;
/*      */ import java.awt.event.ActionEvent;
/*      */ import java.awt.event.ActionListener;
/*      */ import java.awt.event.KeyAdapter;
/*      */ import java.awt.event.KeyEvent;
/*      */ import java.awt.event.MouseAdapter;
/*      */ import java.awt.event.MouseEvent;
/*      */ import java.io.File;
/*      */ import java.security.InvalidParameterException;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Collections;
/*      */ import java.util.EnumMap;
/*      */ import java.util.HashMap;
/*      */ import java.util.List;
/*      */ import java.util.Map;
/*      */ import javax.swing.AbstractAction;
/*      */ import javax.swing.BorderFactory;
/*      */ import javax.swing.Box;
/*      */ import javax.swing.BoxLayout;
/*      */ import javax.swing.DefaultListCellRenderer;
/*      */ import javax.swing.Icon;
/*      */ import javax.swing.JButton;
/*      */ import javax.swing.JComboBox;
/*      */ import javax.swing.JLabel;
/*      */ import javax.swing.JList;
/*      */ import javax.swing.JMenuItem;
/*      */ import javax.swing.JPopupMenu;
/*      */ import javax.swing.JSeparator;
/*      */ import javax.swing.JTextField;
/*      */ import javax.swing.SwingUtilities;
/*      */ import javax.swing.Timer;
/*      */ import javax.swing.border.Border;
/*      */ 
/*      */ public class ChartToolBar extends DockUndockToolBar
/*      */   implements DisableEnableListener, IEventHandler, PlatformSpecific, ChartModeChangeListener
/*      */ {
/*  153 */   private static final Insets DEFAULT_INSETS = new Insets(3, 14, 5, 14);
/*      */ 
/*  155 */   private static final Border BUTTON_PANEL_BORDER = BorderFactory.createCompoundBorder(BorderFactory.createEtchedBorder(), BorderFactory.createEmptyBorder(2, 2, 2, 2));
/*  156 */   private static final Border BUTTON_BORDER = BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Color.BLACK, 1), BorderFactory.createEmptyBorder(4, 4, 4, 4));
/*      */ 
/*  196 */   private static final String[] drawings1_1 = { "item.fibonacci.fan.lines", "item.fibonacci.fan.arcs", "item.fibonacci.retracements", "item.fibonacci.time.zones", "item.fibonacci.expansion" };
/*      */ 
/*  204 */   private static final String[] drawings1_2 = { "item.gann.angle", "item.gann.periods" };
/*      */ 
/*  209 */   private static final String[] drawings1_3 = { "item.andrews.pitchfork" };
/*      */ 
/*  213 */   private static final String[] drawings2_1 = { "item.precent.lines", "item.channel.lines", "item.poly.line", "item.short.line", "item.long.line", "item.ray.line" };
/*      */ 
/*  222 */   private static final String[] drawings2_2 = { "item.rectangle", "item.triangle", "item.ellipse" };
/*      */ 
/*  228 */   private static final String[] drawings2_3 = { "item.signal.up", "item.signal.down" };
/*      */ 
/*  233 */   private static final String[] drawings2_4 = { "item.horizontal.line", "item.vertical.line" };
/*      */ 
/*  238 */   private static final String[] drawings2_5 = { "item.text", "item.periods" };
/*      */   private final ActionListener actionListener;
/*      */   private final TabsAndFramesTabbedPane templatesOperator;
/*  264 */   private final Map<ComboBoxType, JComboBox> comboBoxes = new EnumMap(ComboBoxType.class);
/*      */ 
/*  295 */   private final Map<LabelType, JLabel> labels = new EnumMap(LabelType.class);
/*      */ 
/*  301 */   private final Map<TextFieldType, JTextField> textFields = new EnumMap(TextFieldType.class);
/*      */ 
/*  307 */   private Map<ButtonType, JButton> buttons = new EnumMap(ButtonType.class);
/*      */ 
/*  310 */   private static final Map<String, Icon> drawingIcons = new HashMap();
/*      */ 
/*  312 */   private static final Map<DataType.DataPresentationType, Icon> lineTypeIconsTop = new HashMap();
/*  313 */   private static final Map<DataType.DataPresentationType, Icon> lineTypeIcons = new HashMap();
/*      */ 
/*  315 */   private static final Map<DataType.DataPresentationType, Icon> tickTypeIconsTop = new HashMap();
/*  316 */   private static final Map<DataType.DataPresentationType, Icon> tickTypeIcons = new HashMap();
/*      */ 
/*  318 */   private static final Map<DataType.DataPresentationType, Icon> priceAggregationTypeIconsTop = new HashMap();
/*  319 */   private static final Map<DataType.DataPresentationType, Icon> priceAggregationTypeIcons = new HashMap();
/*      */ 
/*  321 */   private static final Map<DataType.DataPresentationType, Icon> pointAndFigureTypeIconsTop = new HashMap();
/*  322 */   private static final Map<DataType.DataPresentationType, Icon> pointAndFigureTypeIcons = new HashMap();
/*      */ 
/*  324 */   private static final Map<DataType.DataPresentationType, Icon> tickBarTypeIconsTop = new HashMap();
/*  325 */   private static final Map<DataType.DataPresentationType, Icon> tickBarTypeIcons = new HashMap();
/*      */ 
/*  327 */   private static final Map<DataType.DataPresentationType, Icon> renkoTypeIconsTop = new HashMap();
/*  328 */   private static final Map<DataType.DataPresentationType, Icon> renkoTypeIcons = new HashMap();
/*      */ 
/*  330 */   private static final ResizableIcon zoomToAreaIcon = new ResizableIcon("toolbar_zoom_area_active.png");
/*  331 */   private static final ResizableIcon zoomToAreaIconDisabled = new ResizableIcon("toolbar_zoom_area_disable.png");
/*      */ 
/*  333 */   private static final ResizableIcon zoomInIconTop = new ResizableIcon("toolbar_zoom_in_active.png");
/*  334 */   private static final ResizableIcon zoomInIcon = new ResizableIcon("toolbar_zoom_in_inactive.png");
/*  335 */   private static final ResizableIcon zoomInIconDisabled = new ResizableIcon("toolbar_zoom_in_disable.png");
/*      */ 
/*  337 */   private static final ResizableIcon zoomOutIconTop = new ResizableIcon("toolbar_zoom_out_active.png");
/*  338 */   private static final ResizableIcon zoomOutIcon = new ResizableIcon("toolbar_zoom_out_inactive.png");
/*  339 */   private static final ResizableIcon zoomOutIconDisabled = new ResizableIcon("toolbar_zoom_out_disable.png");
/*      */ 
/*  341 */   private static final ResizableIcon customRangeIcon = new ResizableIcon("toolbar_zoom_custom_active.png");
/*  342 */   private static final ResizableIcon customRangeIconDisabled = new ResizableIcon("toolbar_zoom_custom_disable.png");
/*      */ 
/*  344 */   private static final ResizableIcon autoshiftIconTop = new ResizableIcon("toolbar_shift_horizontal_active.png");
/*  345 */   private static final ResizableIcon autoshiftIconDisabled = new ResizableIcon("toolbar_shift_horizontal_disable.png");
/*      */ 
/*  347 */   public static final ResizableIcon verticalChartShiftIconTop = new ResizableIcon("toolbar_shift_vertical_active.png");
/*  348 */   private static final ResizableIcon verticalChartShiftIcon = new ResizableIcon("toolbar_shift_vertical_inactive.png");
/*  349 */   private static final ResizableIcon verticalChartShiftIconDisabled = new ResizableIcon("toolbar_shift_vertical_disable.png");
/*      */ 
/*  351 */   public static final ResizableIcon switchCursorIconTop = new ResizableIcon("toolbar_cursor_active.png");
/*  352 */   public static final ResizableIcon switchCursorIconDisabled = new ResizableIcon("toolbar_cursor_disable.png");
/*  353 */   public static final ResizableIcon switchCursorIcon = new ResizableIcon("toolbar_cursor_inactive.png");
/*      */ 
/*  355 */   public static final ResizableIcon priceMarkerIcon = new ResizableIcon("drawing_marker_price_inactive.png");
/*  356 */   public static final ResizableIcon priceMarkerIconTop = new ResizableIcon("drawing_marker_price_active.png");
/*  357 */   public static final ResizableIcon priceMarkerIconDisabled = new ResizableIcon("drawing_marker_price_disable.png");
/*      */ 
/*  359 */   public static final ResizableIcon timeMarkerIcon = new ResizableIcon("drawing_marker_time_inactive.png");
/*  360 */   public static final ResizableIcon timeMarkerIconTop = new ResizableIcon("drawing_marker_time_active.png");
/*  361 */   public static final ResizableIcon timeMarkerIconDisabled = new ResizableIcon("drawing_marker_time_disable.png");
/*      */ 
/*  363 */   private static final ResizableIcon saveToImageIcon = new ResizableIcon("toolbar_save_to_image_active.png");
/*  364 */   private static final ResizableIcon saveToImageIconDisabled = new ResizableIcon("toolbar_save_to_image_disable.png");
/*      */ 
/*  366 */   private static final ResizableIcon drawingIconTop = new ResizableIcon("toolbar_drawings_active.png");
/*  367 */   private static final ResizableIcon drawingIconDisabled = new ResizableIcon("toolbar_drawings_disable.png");
/*      */ 
/*  369 */   private static final ResizableIcon patternsIconTop = new ResizableIcon("toolbar_patterns_active.png");
/*      */ 
/*  371 */   private static final ResizableIcon ohlcIconTop = new ResizableIcon("toolbar_ohlc_active.png");
/*      */ 
/*  373 */   private static final ResizableIcon fibonacciIcon = new ResizableIcon("toolbar_fibonacci_active.png");
/*  374 */   private static final ResizableIcon fibonacciIconDisabled = new ResizableIcon("toolbar_fibonacci_disable.png");
/*      */ 
/*  376 */   private static final ResizableIcon indicatorIconTop = new ResizableIcon("toolbar_indicators_active.png");
/*  377 */   private static final ResizableIcon indicatorIconDisabled = new ResizableIcon("toolbar_indicators_disable.png");
/*      */ 
/*  379 */   private static final ResizableIcon themesIconTop = new ResizableIcon("toolbar_themes_active.png");
/*      */ 
/*  381 */   private static final ResizableIcon templatesIconTop = new ResizableIcon("toolbar_templates_active.png");
/*      */ 
/*  383 */   private static final Icon addChartIcon = new ResizableIcon("toolbar_chart_add_active.png");
/*      */ 
/*  385 */   private static final Icon toolbarChartViewBarInactiveIcon = new ResizableIcon("toolbar_chart_view_bar_inactive.png");
/*  386 */   private static final Icon toolbarChartViewBarActiveIcon = new ResizableIcon("toolbar_chart_view_bar_active.png");
/*      */ 
/*  388 */   private static final Icon toolbarChartViewCandleInactiveIcon = new ResizableIcon("toolbar_chart_view_candle_inactive.png");
/*  389 */   private static final Icon toolbarChartViewCandleActiveIcon = new ResizableIcon("toolbar_chart_view_candle_active.png");
/*      */ 
/*  391 */   private static final Icon toolbarChartViewLineInactiveIcon = new ResizableIcon("toolbar_chart_view_line_inactive.png");
/*  392 */   private static final Icon toolbarChartViewLineActiveIcon = new ResizableIcon("toolbar_chart_view_line_active.png");
/*      */ 
/*  394 */   private static final Icon toolbarChartViewTableActiveIcon = new ResizableIcon("toolbar_chart_view_table_active.png");
/*      */   private final ChartBean chartBean;
/*  398 */   private Component[] tableModeInvisibleStruts = new Component[2];
/*      */ 
/*  400 */   private ChartModeChangeListener.ChartMode currentChartMode = ChartModeChangeListener.ChartMode.CHART;
/*      */ 
/*      */   public ChartToolBar(ChartBean chartBean, ActionListener actionListener, TabsAndFramesTabbedPane templatesOperator)
/*      */   {
/*  407 */     this.chartBean = chartBean;
/*  408 */     this.actionListener = actionListener;
/*  409 */     this.templatesOperator = templatesOperator;
/*      */ 
/*  411 */     build();
/*      */   }
/*      */ 
/*      */   private void build() {
/*  415 */     setLayout(new BoxLayout(this, 0));
/*      */ 
/*  417 */     initBorders();
/*  418 */     initIcons();
/*      */ 
/*  420 */     if (!GreedContext.isStrategyAllowed()) {
/*  421 */       addChartsButton();
/*      */     }
/*      */ 
/*  424 */     initCommonButtons();
/*  425 */     initAnalyticsButtons();
/*      */ 
/*  427 */     initInfoButtons();
/*      */ 
/*  429 */     initChartPropertiesButtons();
/*      */ 
/*  431 */     addAlwaysOntopButton();
/*      */ 
/*  433 */     initQuickSearch();
/*      */ 
/*  435 */     initListeners();
/*      */ 
/*  437 */     setCursor(new Cursor(12));
/*      */   }
/*      */ 
/*      */   public void applyChartBean(ChartBean chartBean)
/*      */   {
/*  442 */     jForexPeriodSelected(chartBean.getJForexPeriod());
/*      */ 
/*  444 */     DataType dataType = chartBean.getDataType();
/*  445 */     switch (29.$SwitchMap$com$dukascopy$api$DataType[dataType.ordinal()]) {
/*      */     case 1:
/*  447 */       JComboBox cmb = getComboBox(ComboBoxType.PRICE_RANGE_PRESENTATION_TYPE);
/*  448 */       cmb.setSelectedItem(chartBean.getPriceRangePresentationType());
/*  449 */       if (DataType.DataPresentationType.TABLE.equals(chartBean.getPriceRangePresentationType())) {
/*  450 */         chartModeChanged(ChartModeChangeListener.ChartMode.TABLE);
/*      */       }
/*      */       else {
/*  453 */         chartModeChanged(ChartModeChangeListener.ChartMode.CHART);
/*      */       }
/*  455 */       break;
/*      */     case 2:
/*  458 */       JComboBox cmb = getComboBox(ComboBoxType.TIME_PERIOD_PRESENTATION_TYPE);
/*  459 */       cmb.setSelectedItem(chartBean.getTimePeriodPresentationType());
/*  460 */       if (DataType.DataPresentationType.TABLE.equals(chartBean.getTimePeriodPresentationType())) {
/*  461 */         chartModeChanged(ChartModeChangeListener.ChartMode.TABLE);
/*      */       }
/*      */       else {
/*  464 */         chartModeChanged(ChartModeChangeListener.ChartMode.CHART);
/*      */       }
/*  466 */       break;
/*      */     case 3:
/*  469 */       JComboBox cmb = getComboBox(ComboBoxType.TICKS_PRESENTATION_TYPE);
/*  470 */       cmb.setSelectedItem(chartBean.getTicksPresentationType());
/*  471 */       if (DataType.DataPresentationType.TABLE.equals(chartBean.getTicksPresentationType())) {
/*  472 */         chartModeChanged(ChartModeChangeListener.ChartMode.TABLE);
/*      */       }
/*      */       else {
/*  475 */         chartModeChanged(ChartModeChangeListener.ChartMode.CHART);
/*      */       }
/*  477 */       break;
/*      */     case 4:
/*  480 */       JResizableComboBox cmb = (JResizableComboBox)getComboBox(ComboBoxType.POINT_AND_FOGURE_PRESENTATION_TYPE);
/*  481 */       cmb.setSelectedItem(chartBean.getPointAndFigurePresentationType());
/*  482 */       if (DataType.DataPresentationType.TABLE.equals(chartBean.getPointAndFigurePresentationType())) {
/*  483 */         chartModeChanged(ChartModeChangeListener.ChartMode.TABLE);
/*      */       }
/*      */       else {
/*  486 */         chartModeChanged(ChartModeChangeListener.ChartMode.CHART);
/*      */       }
/*  488 */       break;
/*      */     case 5:
/*  491 */       JComboBox cmb = getComboBox(ComboBoxType.TICK_BAR_PRESENTATION_TYPE);
/*  492 */       cmb.setSelectedItem(chartBean.getTickBarPresentationType());
/*  493 */       if (DataType.DataPresentationType.TABLE.equals(chartBean.getTickBarPresentationType())) {
/*  494 */         chartModeChanged(ChartModeChangeListener.ChartMode.TABLE);
/*      */       }
/*      */       else {
/*  497 */         chartModeChanged(ChartModeChangeListener.ChartMode.CHART);
/*      */       }
/*  499 */       break;
/*      */     case 6:
/*  502 */       JComboBox cmb = getComboBox(ComboBoxType.RENKO_PRESENTATION_TYPE);
/*  503 */       cmb.setSelectedItem(chartBean.getRenkoPresentationType());
/*  504 */       if (DataType.DataPresentationType.TABLE.equals(chartBean.getRenkoPresentationType())) {
/*  505 */         chartModeChanged(ChartModeChangeListener.ChartMode.TABLE);
/*      */       }
/*      */       else {
/*  508 */         chartModeChanged(ChartModeChangeListener.ChartMode.CHART);
/*      */       }
/*  510 */       break;
/*      */     default:
/*  512 */       throw new IllegalArgumentException("Unsupported data type - " + dataType);
/*      */     }
/*      */   }
/*      */ 
/*      */   private void setComboBox(ComboBoxType comboBoxType, JComboBox comboBox)
/*      */   {
/*  518 */     this.comboBoxes.put(comboBoxType, comboBox);
/*      */   }
/*      */ 
/*      */   public JComboBox getComboBox(ComboBoxType comboBoxType) {
/*  522 */     return (JComboBox)this.comboBoxes.get(comboBoxType);
/*      */   }
/*      */ 
/*      */   public void setButton(ButtonType buttonType, JButton button) {
/*  526 */     this.buttons.put(buttonType, button);
/*      */   }
/*      */ 
/*      */   public JButton getButton(ButtonType buttonType) {
/*  530 */     return (JButton)this.buttons.get(buttonType);
/*      */   }
/*      */ 
/*      */   public void setLabel(LabelType labelType, JLabel label) {
/*  534 */     this.labels.put(labelType, label);
/*      */   }
/*      */ 
/*      */   public JLabel getLabel(LabelType labelType) {
/*  538 */     return (JLabel)this.labels.get(labelType);
/*      */   }
/*      */ 
/*      */   public void setTextField(TextFieldType textFieldType, JTextField textField) {
/*  542 */     this.textFields.put(textFieldType, textField);
/*      */   }
/*      */ 
/*      */   public JTextField getTextField(TextFieldType textFieldType) {
/*  546 */     return (JTextField)this.textFields.get(textFieldType);
/*      */   }
/*      */ 
/*      */   private JSeparator createJSeparator() {
/*  550 */     JSeparator separator = new JSeparator(1);
/*  551 */     setSize(separator, SEPARATOR_SIZE);
/*  552 */     return separator;
/*      */   }
/*      */ 
/*      */   void addChartsButton()
/*      */   {
/*  557 */     JLocalizableButton button = new JLocalizableButton(addChartIcon);
/*  558 */     button.setToolTipText("tooltip.add.chart");
/*  559 */     button.setBorder(BUTTON_BORDER);
/*  560 */     button.setMargin(DEFAULT_INSETS);
/*  561 */     button.setBorderPainted(false);
/*      */ 
/*  563 */     JPopupMenu popupMenu = new JPopupMenu();
/*      */ 
/*  565 */     button.addMouseListener(new ButtonMouseListener(button, popupMenu));
/*  566 */     button.addActionListener(new ActionListener(popupMenu)
/*      */     {
/*      */       public void actionPerformed(ActionEvent e) {
/*  569 */         ChartToolBar.this.updateTickerList(this.val$popupMenu);
/*      */       }
/*      */     });
/*  572 */     button.setVisible(true);
/*  573 */     button.setEnabled(true);
/*      */ 
/*  575 */     setButton(ButtonType.CHARTS, button);
/*  576 */     add(button);
/*      */   }
/*      */ 
/*      */   void initCommonButtons() {
/*  580 */     initInstrumentComboBox();
/*  581 */     add(Box.createHorizontalStrut(2));
/*      */ 
/*  583 */     initPeriodsComboBox();
/*  584 */     add(Box.createHorizontalStrut(2));
/*      */ 
/*  586 */     initLineTypeComboBox();
/*  587 */     add(Box.createHorizontalStrut(2));
/*      */ 
/*  589 */     initOfferSideButton(ComboBoxType.TIME_AGGREAGTION_OFFER_SIDE);
/*  590 */     initOfferSideButton(ComboBoxType.PRICE_RANGE_OFFER_SIDE);
/*  591 */     initOfferSideButton(ComboBoxType.POINT_AND_FIGURE_OFFER_SIDE);
/*  592 */     initOfferSideButton(ComboBoxType.TICK_BAR_OFFER_SIDE);
/*  593 */     initOfferSideButton(ComboBoxType.RENKO_OFFER_SIDE);
/*      */ 
/*  595 */     initZoomToFitMenu();
/*  596 */     initVerticalChartShiftMenu();
/*      */ 
/*  598 */     initCustomRangeMenu();
/*  599 */     initZoomInMenu();
/*  600 */     initZoomOutMenu();
/*  601 */     initZoomToAreaMenu();
/*      */ 
/*  603 */     add(Box.createHorizontalStrut(10));
/*      */   }
/*      */ 
/*      */   void initAnalyticsButtons() {
/*  607 */     initPriceMarkersMenu();
/*  608 */     initTimeMarkersMenu();
/*  609 */     initIndicatorsMenu();
/*  610 */     initDrawingsMenu();
/*  611 */     initFibonacciGanMenu();
/*  612 */     initPatternsButton();
/*      */ 
/*  614 */     this.tableModeInvisibleStruts[0] = Box.createHorizontalStrut(10);
/*  615 */     add(this.tableModeInvisibleStruts[0]);
/*      */   }
/*      */ 
/*      */   void initInfoButtons() {
/*  619 */     initOhlcButton();
/*  620 */     initSwitchCursorMenu();
/*      */ 
/*  622 */     this.tableModeInvisibleStruts[1] = Box.createHorizontalStrut(10);
/*  623 */     add(this.tableModeInvisibleStruts[1]);
/*      */   }
/*      */ 
/*      */   void initChartPropertiesButtons() {
/*  627 */     initThemeButton();
/*  628 */     initSaveToImageButton();
/*  629 */     initTemplatesMenu();
/*      */   }
/*      */ 
/*      */   void initBorders() {
/*  633 */     setBorder(BUTTON_PANEL_BORDER);
/*      */   }
/*      */ 
/*      */   private void initIcons() {
/*  637 */     drawingIcons.put(drawings2_1[0], new ResizableIcon("drawing_line_percent_active.png"));
/*  638 */     drawingIcons.put(drawings2_1[1], new ResizableIcon("drawing_line_parallel_active.png"));
/*  639 */     drawingIcons.put(drawings2_1[2], new ResizableIcon("drawing_line_poly_active.png"));
/*  640 */     drawingIcons.put(drawings2_1[3], new ResizableIcon("drawing_line_short_active.png"));
/*  641 */     drawingIcons.put(drawings2_1[4], new ResizableIcon("drawing_line_long_active.png"));
/*  642 */     drawingIcons.put(drawings2_1[5], new ResizableIcon("drawing_line_ray_active.png"));
/*      */ 
/*  644 */     drawingIcons.put(drawings2_2[0], new ResizableIcon("drawing_rectangle_active.png"));
/*  645 */     drawingIcons.put(drawings2_2[1], new ResizableIcon("drawing_triangle_active.png"));
/*  646 */     drawingIcons.put(drawings2_2[2], new ResizableIcon("drawing_ellipse_active.png"));
/*      */ 
/*  648 */     drawingIcons.put(drawings2_3[0], new ResizableIcon("drawing_arrow_up_green_active.png"));
/*  649 */     drawingIcons.put(drawings2_3[1], new ResizableIcon("drawing_arrow_down_red_active.png"));
/*      */ 
/*  651 */     drawingIcons.put(drawings2_4[0], new ResizableIcon("drawing_line_horizontal_active.png"));
/*  652 */     drawingIcons.put(drawings2_4[1], new ResizableIcon("drawing_line_vertical_active.png"));
/*      */ 
/*  654 */     drawingIcons.put(drawings2_5[0], new ResizableIcon("drawing_text_active.png"));
/*  655 */     drawingIcons.put(drawings2_5[1], new ResizableIcon("drawing_periods_active.png"));
/*      */ 
/*  657 */     lineTypeIcons.put(DataType.DataPresentationType.BAR, toolbarChartViewBarInactiveIcon);
/*  658 */     lineTypeIcons.put(DataType.DataPresentationType.CANDLE, toolbarChartViewCandleInactiveIcon);
/*  659 */     lineTypeIcons.put(DataType.DataPresentationType.LINE, toolbarChartViewLineInactiveIcon);
/*  660 */     lineTypeIcons.put(DataType.DataPresentationType.TABLE, toolbarChartViewTableActiveIcon);
/*      */ 
/*  662 */     lineTypeIconsTop.put(DataType.DataPresentationType.BAR, toolbarChartViewBarActiveIcon);
/*  663 */     lineTypeIconsTop.put(DataType.DataPresentationType.CANDLE, toolbarChartViewCandleActiveIcon);
/*  664 */     lineTypeIconsTop.put(DataType.DataPresentationType.LINE, toolbarChartViewLineActiveIcon);
/*  665 */     lineTypeIconsTop.put(DataType.DataPresentationType.TABLE, toolbarChartViewTableActiveIcon);
/*      */ 
/*  667 */     tickTypeIcons.put(DataType.DataPresentationType.BAR, toolbarChartViewBarInactiveIcon);
/*  668 */     tickTypeIcons.put(DataType.DataPresentationType.LINE, toolbarChartViewLineInactiveIcon);
/*  669 */     tickTypeIcons.put(DataType.DataPresentationType.TABLE, toolbarChartViewTableActiveIcon);
/*      */ 
/*  671 */     tickTypeIconsTop.put(DataType.DataPresentationType.BAR, toolbarChartViewBarActiveIcon);
/*  672 */     tickTypeIconsTop.put(DataType.DataPresentationType.LINE, toolbarChartViewLineActiveIcon);
/*  673 */     tickTypeIconsTop.put(DataType.DataPresentationType.TABLE, toolbarChartViewTableActiveIcon);
/*      */ 
/*  675 */     priceAggregationTypeIcons.put(DataType.DataPresentationType.RANGE_BAR, toolbarChartViewBarInactiveIcon);
/*  676 */     priceAggregationTypeIcons.put(DataType.DataPresentationType.CANDLE, toolbarChartViewCandleInactiveIcon);
/*  677 */     priceAggregationTypeIcons.put(DataType.DataPresentationType.TABLE, toolbarChartViewTableActiveIcon);
/*      */ 
/*  679 */     priceAggregationTypeIconsTop.put(DataType.DataPresentationType.RANGE_BAR, toolbarChartViewBarActiveIcon);
/*  680 */     priceAggregationTypeIconsTop.put(DataType.DataPresentationType.CANDLE, toolbarChartViewCandleActiveIcon);
/*  681 */     priceAggregationTypeIconsTop.put(DataType.DataPresentationType.TABLE, toolbarChartViewTableActiveIcon);
/*      */ 
/*  683 */     pointAndFigureTypeIcons.put(DataType.DataPresentationType.BOX, new ResizableIcon("toolbar_chart_view_pf.png"));
/*  684 */     pointAndFigureTypeIcons.put(DataType.DataPresentationType.BAR, new ResizableIcon("toolbar_chart_view_pf_bar.png"));
/*  685 */     pointAndFigureTypeIcons.put(DataType.DataPresentationType.TABLE, toolbarChartViewTableActiveIcon);
/*      */ 
/*  687 */     pointAndFigureTypeIconsTop.put(DataType.DataPresentationType.BOX, new ResizableIcon("toolbar_chart_view_pf.png"));
/*  688 */     pointAndFigureTypeIconsTop.put(DataType.DataPresentationType.BAR, new ResizableIcon("toolbar_chart_view_pf_bar.png"));
/*  689 */     pointAndFigureTypeIconsTop.put(DataType.DataPresentationType.TABLE, toolbarChartViewTableActiveIcon);
/*      */ 
/*  691 */     tickBarTypeIconsTop.put(DataType.DataPresentationType.BAR, toolbarChartViewBarActiveIcon);
/*  692 */     tickBarTypeIconsTop.put(DataType.DataPresentationType.CANDLE, toolbarChartViewCandleActiveIcon);
/*  693 */     tickBarTypeIconsTop.put(DataType.DataPresentationType.TABLE, toolbarChartViewTableActiveIcon);
/*      */ 
/*  695 */     tickBarTypeIcons.put(DataType.DataPresentationType.BAR, toolbarChartViewBarInactiveIcon);
/*  696 */     tickBarTypeIcons.put(DataType.DataPresentationType.TABLE, toolbarChartViewTableActiveIcon);
/*      */ 
/*  698 */     tickBarTypeIconsTop.put(DataType.DataPresentationType.BAR, toolbarChartViewBarActiveIcon);
/*  699 */     tickBarTypeIconsTop.put(DataType.DataPresentationType.TABLE, toolbarChartViewTableActiveIcon);
/*      */ 
/*  701 */     renkoTypeIcons.put(DataType.DataPresentationType.BRICK, toolbarChartViewBarInactiveIcon);
/*  702 */     renkoTypeIcons.put(DataType.DataPresentationType.TABLE, toolbarChartViewTableActiveIcon);
/*      */ 
/*  704 */     renkoTypeIconsTop.put(DataType.DataPresentationType.BRICK, toolbarChartViewBarActiveIcon);
/*  705 */     renkoTypeIconsTop.put(DataType.DataPresentationType.TABLE, toolbarChartViewTableActiveIcon);
/*      */   }
/*      */ 
/*      */   public void setEnabled(boolean value, ButtonType[] buttonTypes)
/*      */   {
/*  710 */     for (ButtonType buttonType : buttonTypes)
/*  711 */       getButton(buttonType).setEnabled(value);
/*      */   }
/*      */ 
/*      */   void initListeners()
/*      */   {
/*  716 */     JComboBox periodComboBox = getComboBox(ComboBoxType.PERIODS);
/*  717 */     periodComboBox.addActionListener(new ActionListener(periodComboBox)
/*      */     {
/*      */       public void actionPerformed(ActionEvent e) {
/*  720 */         Object selectedValue = this.val$periodComboBox.getSelectedItem();
/*  721 */         JForexPeriod jForexPeriod = (JForexPeriod)selectedValue;
/*      */ 
/*  723 */         ChartToolBar.this.jForexPeriodSelected(jForexPeriod);
/*      */       } } );
/*      */   }
/*      */ 
/*      */   private void jForexPeriodSelected(JForexPeriod jForexPeriod) {
/*  729 */     DataType dataType = jForexPeriod.getDataType();
/*  730 */     switch (29.$SwitchMap$com$dukascopy$api$DataType[dataType.ordinal()]) {
/*      */     case 3:
/*  732 */       setPriceRangeCombosVisible(false);
/*  733 */       setTimePeriodCombosVisible(false);
/*  734 */       setPointAndFigureCombosVisible(false);
/*  735 */       setTicksCombosVisible(true);
/*  736 */       setTickBarCombosVisible(false);
/*  737 */       setRenkoCombosVisible(false);
/*  738 */       break;
/*      */     case 1:
/*  741 */       setTimePeriodCombosVisible(false);
/*  742 */       setTicksCombosVisible(false);
/*  743 */       setPointAndFigureCombosVisible(false);
/*  744 */       setPriceRangeCombosVisible(true);
/*  745 */       setTickBarCombosVisible(false);
/*  746 */       setRenkoCombosVisible(false);
/*  747 */       break;
/*      */     case 2:
/*  750 */       setTicksCombosVisible(false);
/*  751 */       setPriceRangeCombosVisible(false);
/*  752 */       setPointAndFigureCombosVisible(false);
/*  753 */       setTimePeriodCombosVisible(true);
/*  754 */       setTickBarCombosVisible(false);
/*  755 */       setRenkoCombosVisible(false);
/*  756 */       break;
/*      */     case 4:
/*  759 */       setTicksCombosVisible(false);
/*  760 */       setPriceRangeCombosVisible(false);
/*  761 */       setTimePeriodCombosVisible(false);
/*  762 */       setPointAndFigureCombosVisible(true);
/*  763 */       setTickBarCombosVisible(false);
/*  764 */       setRenkoCombosVisible(false);
/*  765 */       break;
/*      */     case 5:
/*  768 */       setTicksCombosVisible(false);
/*  769 */       setPriceRangeCombosVisible(false);
/*  770 */       setTimePeriodCombosVisible(false);
/*  771 */       setPointAndFigureCombosVisible(false);
/*  772 */       setTickBarCombosVisible(true);
/*  773 */       setRenkoCombosVisible(false);
/*  774 */       break;
/*      */     case 6:
/*  777 */       setTicksCombosVisible(false);
/*  778 */       setPriceRangeCombosVisible(false);
/*  779 */       setTimePeriodCombosVisible(false);
/*  780 */       setPointAndFigureCombosVisible(false);
/*  781 */       setTickBarCombosVisible(false);
/*  782 */       setRenkoCombosVisible(true);
/*  783 */       break;
/*      */     default:
/*  786 */       throw new IllegalArgumentException("Unsupported data type - " + dataType);
/*      */     }
/*      */ 
/*  790 */     ActionEvent ae = new ActionEvent(jForexPeriod, -1, Action.CHANGE_JFOREX_PERIOD.toString());
/*  791 */     this.actionListener.actionPerformed(ae);
/*      */   }
/*      */ 
/*      */   private void setTicksCombosVisible(boolean flag) {
/*  795 */     for (Component c : getTicksCombos())
/*  796 */       c.setVisible(flag);
/*      */   }
/*      */ 
/*      */   private void setTimePeriodCombosVisible(boolean flag)
/*      */   {
/*  801 */     for (Component c : getTimePeriodCombos())
/*  802 */       c.setVisible(flag);
/*      */   }
/*      */ 
/*      */   private void setPriceRangeCombosVisible(boolean flag)
/*      */   {
/*  807 */     for (Component c : getPriceRangeCombos())
/*  808 */       c.setVisible(flag);
/*      */   }
/*      */ 
/*      */   private void setPointAndFigureCombosVisible(boolean flag)
/*      */   {
/*  813 */     for (Component c : getPointAndFigureCombos())
/*  814 */       c.setVisible(flag);
/*      */   }
/*      */ 
/*      */   private void setTickBarCombosVisible(boolean flag)
/*      */   {
/*  819 */     for (Component c : getTickBarCombos())
/*  820 */       c.setVisible(flag);
/*      */   }
/*      */ 
/*      */   private void setRenkoCombosVisible(boolean flag)
/*      */   {
/*  825 */     for (Component c : getRenkoCombos())
/*  826 */       c.setVisible(flag);
/*      */   }
/*      */ 
/*      */   private Component[] getTicksCombos()
/*      */   {
/*  831 */     return new Component[] { getComboBox(ComboBoxType.TICKS_PRESENTATION_TYPE) };
/*      */   }
/*      */ 
/*      */   private Component[] getTimePeriodCombos()
/*      */   {
/*  837 */     return new Component[] { getComboBox(ComboBoxType.TIME_AGGREAGTION_OFFER_SIDE), getComboBox(ComboBoxType.TIME_PERIOD_PRESENTATION_TYPE) };
/*      */   }
/*      */ 
/*      */   private Component[] getPriceRangeCombos()
/*      */   {
/*  844 */     return new Component[] { getComboBox(ComboBoxType.PRICE_RANGE_OFFER_SIDE), getComboBox(ComboBoxType.PRICE_RANGE_PRESENTATION_TYPE) };
/*      */   }
/*      */ 
/*      */   private Component[] getPointAndFigureCombos()
/*      */   {
/*  851 */     return new Component[] { getComboBox(ComboBoxType.POINT_AND_FIGURE_OFFER_SIDE), getComboBox(ComboBoxType.POINT_AND_FOGURE_PRESENTATION_TYPE) };
/*      */   }
/*      */ 
/*      */   private Component[] getTickBarCombos()
/*      */   {
/*  858 */     return new Component[] { getComboBox(ComboBoxType.TICK_BAR_OFFER_SIDE), getComboBox(ComboBoxType.TICK_BAR_PRESENTATION_TYPE) };
/*      */   }
/*      */ 
/*      */   private Component[] getRenkoCombos()
/*      */   {
/*  865 */     return new Component[] { getComboBox(ComboBoxType.RENKO_OFFER_SIDE), getComboBox(ComboBoxType.RENKO_PRESENTATION_TYPE) };
/*      */   }
/*      */ 
/*      */   private JLocalizableButton createResizableMenuButton(ResizableIcon icon, ResizableIcon disabledIcon, String toolTip, Action action)
/*      */   {
/*  872 */     JLocalizableButton resizableMenuButton = new JLocalizableButton(icon, ResizingManager.ComponentSize.TOLBAR_BTN_SIZE);
/*  873 */     resizableMenuButton.setDisabledIcon(disabledIcon);
/*  874 */     resizableMenuButton.setBorder(BUTTON_BORDER);
/*  875 */     resizableMenuButton.setMargin(DEFAULT_INSETS);
/*  876 */     resizableMenuButton.setBorderPainted(false);
/*  877 */     resizableMenuButton.setName(toolTip);
/*      */ 
/*  879 */     resizableMenuButton.setToolTipText(toolTip);
/*      */ 
/*  881 */     if (action != null) {
/*  882 */       resizableMenuButton.setActionCommand(action.name());
/*      */     }
/*  884 */     resizableMenuButton.addActionListener(this.actionListener);
/*      */ 
/*  886 */     ToolBarButtonBehaviourControlListener behaviourControlListener = new ToolBarButtonBehaviourControlListener();
/*  887 */     resizableMenuButton.addMouseListener(behaviourControlListener);
/*  888 */     resizableMenuButton.addFocusListener(behaviourControlListener);
/*  889 */     resizableMenuButton.addPropertyChangeListener("enabled", behaviourControlListener);
/*  890 */     return resizableMenuButton;
/*      */   }
/*      */ 
/*      */   void initCustomRangeMenu() {
/*  894 */     JButton customRangeButton = createResizableMenuButton(customRangeIcon, customRangeIconDisabled, "tooltip.custom.range", Action.CUSTOM_RANGE);
/*      */ 
/*  901 */     setButton(ButtonType.CUSTOM_RANGE, customRangeButton);
/*  902 */     add(customRangeButton);
/*      */   }
/*      */ 
/*      */   void initZoomToFitMenu() {
/*  906 */     JButton autoshift = createResizableMenuButton(autoshiftIconTop, autoshiftIconDisabled, "tooltip.auto.shift", Action.AUTOSHIFT);
/*      */ 
/*  912 */     autoshift.setEnabled(true);
/*      */ 
/*  914 */     setButton(ButtonType.AUTOSHIFT, autoshift);
/*  915 */     add(autoshift);
/*      */   }
/*      */ 
/*      */   void initVerticalChartShiftMenu() {
/*  919 */     JButton verticalChartShift = createResizableMenuButton(verticalChartShiftIcon, verticalChartShiftIconDisabled, "tooltip.vertical", Action.VERTICAL_SHIFT);
/*      */ 
/*  926 */     verticalChartShift.addActionListener(new ActionListener(verticalChartShift) {
/*      */       public void actionPerformed(ActionEvent e) {
/*  928 */         if (this.val$verticalChartShift.getIcon().equals(ChartToolBar.verticalChartShiftIcon))
/*  929 */           this.val$verticalChartShift.setIcon(ChartToolBar.verticalChartShiftIconTop);
/*      */         else
/*  931 */           this.val$verticalChartShift.setIcon(ChartToolBar.verticalChartShiftIcon);
/*      */       }
/*      */     });
/*  936 */     verticalChartShift.setSelected(this.chartBean.getVerticalMovementEnabledAsBoolean());
/*      */ 
/*  938 */     setButton(ButtonType.VERTICAL_SHIFT, verticalChartShift);
/*  939 */     add(verticalChartShift);
/*      */   }
/*      */ 
/*      */   void initZoomInMenu() {
/*  943 */     JResizableButton button = createResizableMenuButton(zoomInIconTop, zoomInIconDisabled, "tooltip.zoom.in", Action.ZOOM_IN);
/*      */ 
/*  950 */     button.setInactiveIcon(zoomInIcon);
/*  951 */     addTimerToButton(button);
/*      */ 
/*  953 */     setButton(ButtonType.ZOOM_IN, button);
/*  954 */     add(button);
/*      */   }
/*      */ 
/*      */   void initZoomOutMenu() {
/*  958 */     JLocalizableButton button = createResizableMenuButton(zoomOutIconTop, zoomOutIconDisabled, "tooltip.zoom.out", Action.ZOOM_OUT);
/*      */ 
/*  965 */     button.setInactiveIcon(zoomOutIcon);
/*  966 */     addTimerToButton(button);
/*      */ 
/*  968 */     setButton(ButtonType.ZOOM_OUT, button);
/*  969 */     add(button);
/*      */   }
/*      */ 
/*      */   void addTimerToButton(JButton button) {
/*  973 */     button.addMouseListener(new MouseAdapter(button)
/*      */     {
/*  975 */       Timer timer = new Timer(100, new ActionListener() {
/*      */         public void actionPerformed(ActionEvent event) {
/*  977 */           SwingUtilities.invokeLater(new Runnable() {
/*      */             public void run() {
/*  979 */               ChartToolBar.4.this.val$button.doClick();
/*      */             }
/*      */           });
/*      */         }
/*      */       });
/*      */ 
/*      */       public void mousePressed(MouseEvent event)
/*      */       {
/*  986 */         this.val$button.doClick();
/*  987 */         if (!this.timer.isRunning())
/*  988 */           this.timer.start();
/*      */       }
/*      */ 
/*      */       public void mouseReleased(MouseEvent event)
/*      */       {
/*  993 */         if (this.timer.isRunning())
/*  994 */           this.timer.stop();
/*      */       }
/*      */ 
/*      */       public void mouseEntered(MouseEvent event)
/*      */       {
/*  999 */         if (this.timer.isRunning())
/* 1000 */           this.timer.stop();
/*      */       }
/*      */ 
/*      */       public void mouseExited(MouseEvent event)
/*      */       {
/* 1005 */         if (this.timer.isRunning())
/* 1006 */           this.timer.stop();
/*      */       }
/*      */     });
/*      */   }
/*      */ 
/*      */   void initZoomToAreaMenu()
/*      */   {
/* 1014 */     JButton button = createResizableMenuButton(zoomToAreaIcon, zoomToAreaIconDisabled, "zoom.area.tooltip", Action.ZOOM_TO_AREA);
/*      */ 
/* 1021 */     setButton(ButtonType.ZOOM_TO_AREA, button);
/* 1022 */     add(button);
/*      */   }
/*      */ 
/*      */   void initSwitchCursorMenu() {
/* 1026 */     JButton button = createResizableMenuButton(switchCursorIcon, switchCursorIconDisabled, "tooltip.switch.cursor", Action.CURSOR_VISIBILITY);
/*      */ 
/* 1033 */     button.setSelected(false);
/*      */ 
/* 1035 */     button.addActionListener(new ActionListener(button) {
/*      */       public void actionPerformed(ActionEvent event) {
/* 1037 */         ChartToolBar.this.setCursorVisibility(this.val$button.getIcon() != ChartToolBar.switchCursorIconTop);
/*      */       }
/*      */     });
/* 1041 */     setButton(ButtonType.CURSOR, button);
/* 1042 */     add(button);
/*      */   }
/*      */ 
/*      */   public void setCursorVisibility(boolean visible) {
/* 1046 */     JButton button = getButton(ButtonType.CURSOR);
/* 1047 */     button.setIcon(visible ? switchCursorIconTop : switchCursorIcon);
/*      */   }
/*      */ 
/*      */   void initPriceMarkersMenu() {
/* 1051 */     JButton button = createResizableMenuButton(priceMarkerIcon, priceMarkerIconDisabled, "tooltip.price.marker", Action.PRICE_MARKER);
/*      */ 
/* 1058 */     button.addActionListener(new ActionListener(button) {
/*      */       public void actionPerformed(ActionEvent e) {
/* 1060 */         if (this.val$button.getIcon().equals(ChartToolBar.priceMarkerIconTop))
/* 1061 */           this.val$button.setIcon(ChartToolBar.priceMarkerIcon);
/*      */         else
/* 1063 */           this.val$button.setIcon(ChartToolBar.priceMarkerIconTop);
/*      */       }
/*      */     });
/* 1068 */     setButton(ButtonType.PRICE_MARKER, button);
/* 1069 */     add(button);
/*      */   }
/*      */ 
/*      */   void initTimeMarkersMenu() {
/* 1073 */     JButton button = createResizableMenuButton(timeMarkerIcon, timeMarkerIconDisabled, "tooltip.time.marker", Action.TIME_MARKER);
/*      */ 
/* 1080 */     button.addActionListener(new ActionListener(button) {
/*      */       public void actionPerformed(ActionEvent e) {
/* 1082 */         if (this.val$button.getIcon().equals(ChartToolBar.timeMarkerIconTop))
/* 1083 */           this.val$button.setIcon(ChartToolBar.timeMarkerIcon);
/*      */         else
/* 1085 */           this.val$button.setIcon(ChartToolBar.timeMarkerIconTop);
/*      */       }
/*      */     });
/* 1090 */     setButton(ButtonType.TIME_MARKER, button);
/* 1091 */     add(button);
/*      */   }
/*      */ 
/*      */   void initThemeButton() {
/* 1095 */     JButton button = createResizableMenuButton(themesIconTop, themesIconTop, "tooltip.themes", Action.OPEN_THEMES_DIALOG);
/*      */ 
/* 1102 */     setButton(ButtonType.THEMES, button);
/* 1103 */     add(button);
/*      */   }
/*      */ 
/*      */   void initSaveToImageButton() {
/* 1107 */     JButton button = new JLocalizableButton(saveToImageIcon, ResizingManager.ComponentSize.TOLBAR_BTN_SIZE);
/* 1108 */     button.setToolTipText("tooltip.save.chart.as.image");
/*      */ 
/* 1110 */     button.setDisabledIcon(saveToImageIconDisabled);
/*      */ 
/* 1112 */     button.setBorder(BUTTON_BORDER);
/* 1113 */     button.setMargin(DEFAULT_INSETS);
/* 1114 */     button.setBorderPainted(false);
/*      */ 
/* 1116 */     button.addMouseListener(new ButtonMouseListener(button, new MouseAdapter(button)
/*      */     {
/*      */       public void mouseReleased(MouseEvent e) {
/* 1119 */         JPopupMenu popupMenu = ChartToolBar.this.createPopupForChartMode(ChartToolBar.this.getCurrentChartMode());
/* 1120 */         popupMenu.show(this.val$button, 0, this.val$button.getHeight());
/*      */       }
/*      */     }));
/* 1123 */     button.setVisible(true);
/*      */ 
/* 1125 */     setButton(ButtonType.SAVE_CHART_AS_IMAGE, button);
/* 1126 */     add(button);
/*      */   }
/*      */ 
/*      */   private JPopupMenu createPopupForChartMode(ChartModeChangeListener.ChartMode currentChartMode) {
/* 1130 */     JPopupMenu popupMenu = new JPopupMenu();
/*      */ 
/* 1132 */     if (ChartModeChangeListener.ChartMode.CHART.equals(currentChartMode)) {
/* 1133 */       JMenuItem saveToClipboardMenuItem = new JLocalizableMenuItem("save.to.clipboard");
/* 1134 */       JMenuItem saveToFileMenuItem = new JLocalizableMenuItem("save.as.file");
/* 1135 */       JMenuItem printMenuItem = new JLocalizableMenuItem("print");
/*      */ 
/* 1137 */       saveToClipboardMenuItem.setActionCommand(Action.SAVE_CHART_IMAGE_TO_CLIPBOARD.name());
/* 1138 */       saveToFileMenuItem.setActionCommand(Action.SAVE_CHART_IMAGE_TO_FILE.name());
/* 1139 */       printMenuItem.setActionCommand(Action.PRINT_CHART_IMAGE.name());
/*      */ 
/* 1141 */       saveToClipboardMenuItem.addActionListener(this.actionListener);
/* 1142 */       saveToFileMenuItem.addActionListener(this.actionListener);
/* 1143 */       printMenuItem.addActionListener(this.actionListener);
/*      */ 
/* 1145 */       popupMenu.add(saveToClipboardMenuItem);
/* 1146 */       popupMenu.add(saveToFileMenuItem);
/*      */     }
/* 1148 */     else if (ChartModeChangeListener.ChartMode.TABLE.equals(currentChartMode)) {
/* 1149 */       JMenuItem saveToFileMenuItem = new JLocalizableMenuItem("save.as.file");
/* 1150 */       saveToFileMenuItem.setActionCommand(Action.SAVE_CHART_TABLE_TO_FILE.name());
/* 1151 */       saveToFileMenuItem.addActionListener(this.actionListener);
/* 1152 */       popupMenu.add(saveToFileMenuItem);
/*      */     }
/*      */     else {
/* 1155 */       throw new IllegalArgumentException("Unsupported Chart Mode - " + currentChartMode);
/*      */     }
/*      */ 
/* 1158 */     return popupMenu;
/*      */   }
/*      */ 
/*      */   void initFibonacciGanMenu() {
/* 1162 */     JButton button = new JLocalizableButton(fibonacciIcon, ResizingManager.ComponentSize.TOLBAR_BTN_SIZE);
/* 1163 */     button.setToolTipText("tooltip.retracements");
/*      */ 
/* 1165 */     button.setDisabledIcon(fibonacciIconDisabled);
/*      */ 
/* 1167 */     button.setBorder(BUTTON_BORDER);
/* 1168 */     button.setMargin(DEFAULT_INSETS);
/* 1169 */     button.setBorderPainted(false);
/*      */ 
/* 1171 */     JPopupMenu popupMenu = new JPopupMenu();
/*      */ 
/* 1173 */     addMenuElements(drawings1_1, drawingIcons, Action.CHOOSE_FIBONACCI_GAN, popupMenu);
/* 1174 */     popupMenu.addSeparator();
/* 1175 */     addMenuElements(drawings1_2, drawingIcons, Action.CHOOSE_FIBONACCI_GAN, popupMenu);
/* 1176 */     popupMenu.addSeparator();
/* 1177 */     addMenuElements(drawings1_3, drawingIcons, Action.CHOOSE_DRAWING, popupMenu);
/*      */ 
/* 1179 */     button.addMouseListener(new ButtonMouseListener(button, popupMenu));
/*      */ 
/* 1181 */     button.setVisible(true);
/* 1182 */     button.setEnabled(true);
/*      */ 
/* 1184 */     setButton(ButtonType.FIBONACCI_GAN, button);
/* 1185 */     add(button);
/*      */   }
/*      */ 
/*      */   void initDrawingsMenu()
/*      */   {
/* 1191 */     JButton button = new JLocalizableButton(drawingIconTop, ResizingManager.ComponentSize.TOLBAR_BTN_SIZE);
/* 1192 */     button.setToolTipText("tooltip.drawings");
/*      */ 
/* 1194 */     button.setDisabledIcon(drawingIconDisabled);
/*      */ 
/* 1196 */     button.setBorder(BUTTON_BORDER);
/* 1197 */     button.setMargin(DEFAULT_INSETS);
/* 1198 */     button.setBorderPainted(false);
/*      */ 
/* 1200 */     JPopupMenu popupMenu = new JPopupMenu();
/*      */ 
/* 1202 */     addMenuElements(drawings2_1, drawingIcons, Action.CHOOSE_DRAWING, popupMenu);
/* 1203 */     popupMenu.addSeparator();
/* 1204 */     addMenuElements(drawings2_2, drawingIcons, Action.CHOOSE_DRAWING, popupMenu);
/* 1205 */     popupMenu.addSeparator();
/* 1206 */     addMenuElements(drawings2_3, drawingIcons, Action.CHOOSE_DRAWING, popupMenu);
/* 1207 */     popupMenu.addSeparator();
/* 1208 */     addMenuElements(drawings2_4, drawingIcons, Action.CHOOSE_DRAWING, popupMenu);
/* 1209 */     popupMenu.addSeparator();
/* 1210 */     addMenuElements(drawings2_5, drawingIcons, Action.CHOOSE_DRAWING, popupMenu);
/*      */ 
/* 1212 */     button.addMouseListener(new ButtonMouseListener(button, popupMenu));
/* 1213 */     button.setVisible(true);
/* 1214 */     button.setEnabled(true);
/*      */ 
/* 1216 */     setButton(ButtonType.DRAWINGS, button);
/* 1217 */     add(button);
/*      */   }
/*      */ 
/*      */   void initPatternsButton() {
/* 1221 */     JButton button = createResizableMenuButton(patternsIconTop, patternsIconTop, "tooltip.patterns", Action.PATTERN_WIDGET);
/*      */ 
/* 1223 */     setButton(ButtonType.PATTERNS, button);
/* 1224 */     add(button);
/*      */   }
/*      */ 
/*      */   void initOhlcButton() {
/* 1228 */     JButton button = createResizableMenuButton(ohlcIconTop, ohlcIconTop, "tooltip.ohlc", Action.OHLC_INFO);
/*      */ 
/* 1230 */     setButton(ButtonType.OHLC_INFORMER, button);
/* 1231 */     add(button);
/*      */   }
/*      */ 
/*      */   void initTemplatesMenu() {
/* 1235 */     JButton button = new JLocalizableButton(templatesIconTop, ResizingManager.ComponentSize.TOLBAR_BTN_SIZE);
/* 1236 */     button.setToolTipText("tooltip.templates");
/*      */ 
/* 1238 */     button.setBorder(BUTTON_BORDER);
/* 1239 */     button.setMargin(DEFAULT_INSETS);
/* 1240 */     button.setBorderPainted(false);
/*      */ 
/* 1242 */     button.addMouseListener(new MouseAdapter(button)
/*      */     {
/*      */       public void mouseReleased(MouseEvent e) {
/* 1245 */         JPopupMenu templatesPopupMenu = ChartToolBar.this.createTemplatesPopup();
/* 1246 */         templatesPopupMenu.show(this.val$button, 0, this.val$button.getHeight());
/*      */       }
/*      */     });
/* 1249 */     button.setVisible(true);
/*      */ 
/* 1251 */     setButton(ButtonType.TEMPLATES, button);
/* 1252 */     add(button);
/*      */   }
/*      */ 
/*      */   private JPopupMenu createTemplatesPopup() {
/* 1256 */     JPopupMenu popupMenu = new JPopupMenu();
/*      */ 
/* 1258 */     JLocalizableMenu recent = new JLocalizableMenu("item.recent.templates");
/* 1259 */     List templateNames = ChartTemplatesListManager.getChartTemplates();
/* 1260 */     for (String chartTemplateName : templateNames) {
/* 1261 */       String templateName = chartTemplateName;
/* 1262 */       JMenuItem templateMenuItem = new JMenuItem(templateName);
/* 1263 */       templateMenuItem.addActionListener(new ActionListener(templateName) {
/*      */         public void actionPerformed(ActionEvent event) {
/* 1265 */           File templateFile = ChartTemplatesListManager.getCustomIndicator(this.val$templateName);
/* 1266 */           ChartToolBar.this.templatesOperator.openTemplate(ChartToolBar.this.chartBean.getId(), templateFile);
/*      */         }
/*      */       });
/* 1269 */       recent.add(templateMenuItem);
/*      */     }
/*      */ 
/* 1272 */     if (recent.getMenuComponentCount() > 0) {
/* 1273 */       popupMenu.add(recent);
/* 1274 */       popupMenu.addSeparator();
/*      */     }
/*      */ 
/* 1279 */     JLocalizableMenuItem openTemplatesMenu = new JLocalizableMenuItem("open.template.popup.menu.item");
/* 1280 */     openTemplatesMenu.addActionListener(new ActionListener()
/*      */     {
/*      */       public void actionPerformed(ActionEvent e)
/*      */       {
/* 1284 */         ChartToolBar.this.templatesOperator.executeAction(TabsOrderingMenuContainer.Action.OPEN_TEMPLATE, ChartToolBar.this.chartBean.getId());
/*      */       }
/*      */     });
/* 1288 */     popupMenu.add(openTemplatesMenu);
/*      */ 
/* 1290 */     JLocalizableMenuItem saveTemplatesMenu = new JLocalizableMenuItem("save.template.popup.menu.item");
/* 1291 */     saveTemplatesMenu.addActionListener(new ActionListener()
/*      */     {
/*      */       public void actionPerformed(ActionEvent e)
/*      */       {
/* 1295 */         ChartToolBar.this.templatesOperator.executeAction(TabsOrderingMenuContainer.Action.SAVE_TEMPLATE, ChartToolBar.this.chartBean.getId());
/*      */       }
/*      */     });
/* 1299 */     popupMenu.add(saveTemplatesMenu);
/*      */ 
/* 1302 */     return popupMenu;
/*      */   }
/*      */ 
/*      */   void initIndicatorsMenu() {
/* 1306 */     JButton button = new JLocalizableButton(indicatorIconTop, ResizingManager.ComponentSize.TOLBAR_BTN_SIZE);
/* 1307 */     button.setToolTipText("tooltip.indicators");
/*      */ 
/* 1309 */     button.setDisabledIcon(indicatorIconDisabled);
/*      */ 
/* 1311 */     button.setBorder(BUTTON_BORDER);
/* 1312 */     button.setMargin(DEFAULT_INSETS);
/* 1313 */     button.setBorderPainted(false);
/*      */ 
/* 1315 */     button.addMouseListener(new MouseAdapter(button)
/*      */     {
/*      */       public void mouseReleased(MouseEvent e) {
/* 1318 */         JPopupMenu indicatorsPopupMenu = ChartToolBar.this.createIndicatorsPopup();
/* 1319 */         indicatorsPopupMenu.show(this.val$button, 0, this.val$button.getHeight());
/*      */       }
/*      */     });
/* 1322 */     button.setVisible(true);
/*      */ 
/* 1324 */     setButton(ButtonType.INDICATORS, button);
/* 1325 */     add(button);
/*      */   }
/*      */ 
/*      */   private JPopupMenu createIndicatorsPopup()
/*      */   {
/* 1330 */     JPopupMenu popupMenu = new JPopupMenu();
/* 1331 */     JLocalizableMenu lastUsedMenu = new JLocalizableMenu("item.last.used.indicator");
/*      */ 
/* 1333 */     ClientSettingsStorage clientSettingsStorage = (ClientSettingsStorage)GreedContext.get("settingsStorage");
/* 1334 */     List lastUsedIndicators = clientSettingsStorage.getLastUsedIndicatorNames();
/* 1335 */     IndicatorsProvider provider = IndicatorsProvider.getInstance();
/* 1336 */     boolean atLeastOneAdded = false;
/* 1337 */     for (LastUsedIndicatorBean lastUsedIndicator : lastUsedIndicators) {
/* 1338 */       if (provider.isIndicatorRegistered(lastUsedIndicator.getName())) {
/*      */         IndicatorWrapper indWrapper;
/*      */         try { indWrapper = new IndicatorWrapper(lastUsedIndicator.getName(), lastUsedIndicator.getSidesForTicks(), lastUsedIndicator.getAppliedPricesForCandles(), lastUsedIndicator.getOptParams(), lastUsedIndicator.getOutputColors(), lastUsedIndicator.getOutputColors2(), lastUsedIndicator.getShowValuesOnChart(), lastUsedIndicator.getShowOutputs(), lastUsedIndicator.getOpacityAlphas(), lastUsedIndicator.getDrawingStyles(), lastUsedIndicator.getLineWidths(), lastUsedIndicator.getOutputShifts());
/*      */ 
/* 1356 */           for (int i = 0; i < indWrapper.getShowOutputs().length; i++)
/* 1357 */             indWrapper.getIndicator().getOutputParameterInfo(i).setShowOutput(indWrapper.showOutput(i));
/*      */         }
/*      */         catch (InvalidParameterException e)
/*      */         {
/* 1361 */           indWrapper = new IndicatorWrapper(lastUsedIndicator.getName());
/*      */         }
/* 1363 */         IndicatorWrapper indicatorWrapper = indWrapper;
/* 1364 */         String props = indicatorWrapper.getPropsStr();
/*      */         String indicatorLabel;
/*      */         String indicatorLabel;
/* 1366 */         if (props != null)
/* 1367 */           indicatorLabel = indicatorWrapper.getName() + "(" + props + ")";
/*      */         else {
/* 1369 */           indicatorLabel = indicatorWrapper.getName();
/*      */         }
/* 1371 */         JMenuItem menuItem = new JMenuItem(indicatorLabel);
/* 1372 */         menuItem.setEnabled(true);
/* 1373 */         menuItem.addActionListener(new ActionListener(indicatorWrapper) {
/*      */           public void actionPerformed(ActionEvent event) {
/* 1375 */             DDSChartsController ddsChartsController = (DDSChartsController)GreedContext.get("chartsController");
/* 1376 */             ddsChartsController.addIndicator(ChartToolBar.this.getChartPanelId(), this.val$indicatorWrapper);
/* 1377 */             ClientSettingsStorage clientSettingsStorage = (ClientSettingsStorage)GreedContext.get("settingsStorage");
/* 1378 */             LastUsedIndicatorBean indicatorBean = DDSChartsActionAdapter.convertToLastUsedIndicatorBean(this.val$indicatorWrapper);
/* 1379 */             clientSettingsStorage.addLastUsedIndicatorName(indicatorBean);
/*      */           }
/*      */         });
/* 1382 */         lastUsedMenu.add(menuItem);
/* 1383 */         atLeastOneAdded = true;
/*      */       }
/*      */     }
/*      */ 
/* 1387 */     if (atLeastOneAdded) {
/* 1388 */       lastUsedMenu.addSeparator();
/*      */ 
/* 1390 */       JLocalizableMenuItem clearItem = new JLocalizableMenuItem("item.clear.indicator");
/* 1391 */       clearItem.setActionCommand(Action.CLEAR_INDICATORS.name());
/* 1392 */       clearItem.addActionListener(this.actionListener);
/*      */ 
/* 1394 */       lastUsedMenu.add(clearItem);
/* 1395 */       popupMenu.add(lastUsedMenu);
/*      */     }
/*      */ 
/* 1398 */     JLocalizableMenu customs = new JLocalizableMenu("item.custom.indicator");
/* 1399 */     for (String custIndiName : CustomIndicatorListManager.getCustomIndicators()) {
/* 1400 */       String indName = custIndiName;
/* 1401 */       JMenuItem indMenuItem = new JMenuItem(indName);
/* 1402 */       indMenuItem.addActionListener(new ActionListener(indName) {
/*      */         public void actionPerformed(ActionEvent event) {
/* 1404 */           String name = IndicatorsProvider.getInstance().enableIndicator(CustomIndicatorListManager.getCustomIndicator(this.val$indName), NotificationUtilsProvider.getNotificationUtils());
/*      */ 
/* 1406 */           if (name != null) {
/* 1407 */             IndicatorWrapper indicatorWrapper = new IndicatorWrapper(name);
/* 1408 */             DDSChartsController ddsChartsController = (DDSChartsController)GreedContext.get("chartsController");
/*      */ 
/* 1411 */             FeedDescriptor descriptor = new FeedDescriptor();
/* 1412 */             IChart chart = ddsChartsController.getIChartBy(ChartToolBar.this.getChartPanelId());
/* 1413 */             if (Period.TICK.equals(chart.getSelectedPeriod())) {
/* 1414 */               descriptor.setDataType(DataType.TICKS);
/*      */             }
/*      */             else {
/* 1417 */               descriptor.setDataType(DataType.TIME_PERIOD_AGGREGATION);
/*      */             }
/* 1419 */             descriptor.setInstrument(chart.getInstrument());
/* 1420 */             descriptor.setPeriod(chart.getSelectedPeriod());
/* 1421 */             descriptor.setOfferSide(chart.getSelectedOfferSide());
/* 1422 */             indicatorWrapper.getIndicatorHolder().getIndicatorContext().setFeedDescriptor(descriptor);
/*      */ 
/* 1424 */             ddsChartsController.getIChartBy(ChartToolBar.this.getChartPanelId()).addIndicator(indicatorWrapper.getIndicator());
/* 1425 */             ClientSettingsStorage clientSettingsStorage = (ClientSettingsStorage)GreedContext.get("settingsStorage");
/* 1426 */             LastUsedIndicatorBean indicatorBean = DDSChartsActionAdapter.convertToLastUsedIndicatorBean(indicatorWrapper);
/* 1427 */             clientSettingsStorage.addLastUsedIndicatorName(indicatorBean);
/*      */           }
/*      */         }
/*      */       });
/* 1431 */       customs.add(indMenuItem);
/*      */     }
/*      */ 
/* 1434 */     if (customs.getMenuComponentCount() > 0) {
/* 1435 */       popupMenu.add(customs);
/*      */     }
/*      */ 
/* 1438 */     if ((customs.getMenuComponentCount() > 0) || (atLeastOneAdded)) {
/* 1439 */       popupMenu.addSeparator();
/*      */     }
/* 1441 */     addMenuElements(new String[] { "item.add.indicator" }, Collections.emptyMap(), Action.ADD_INDICATOR, popupMenu);
/* 1442 */     return popupMenu;
/*      */   }
/*      */ 
/*      */   void addMenuElements(String[] menuElements, Map<String, Icon> icons, Action action, JPopupMenu menu) {
/* 1446 */     for (String menuElement : menuElements)
/*      */     {
/* 1448 */       Icon icon = (Icon)icons.get(menuElement);
/*      */       PanelMenuItem panelMenuItem;
/*      */       PanelMenuItem panelMenuItem;
/* 1449 */       if (icon != null)
/* 1450 */         panelMenuItem = new PanelMenuItem(menuElement, icon);
/*      */       else {
/* 1452 */         panelMenuItem = new PanelMenuItem(menuElement);
/*      */       }
/* 1454 */       panelMenuItem.setChartPanelId(getChartPanelId());
/* 1455 */       panelMenuItem.setActionCommand(action.name());
/* 1456 */       panelMenuItem.addActionListener(this.actionListener);
/*      */ 
/* 1462 */       menu.add(panelMenuItem);
/*      */     }
/*      */   }
/*      */ 
/*      */   void initLineTypeComboBox() {
/* 1467 */     initTickTypeComboBox();
/* 1468 */     initCandleTypeComboBox();
/* 1469 */     initPriceRangesPresentationTypeComboBox();
/* 1470 */     initPointAndFigurePresentationTypeComboBox();
/* 1471 */     initTickBarPresentationTypeComboBox();
/* 1472 */     initRenkoPresentationTypeComboBox();
/*      */   }
/*      */ 
/*      */   void initPriceRangesPresentationTypeComboBox()
/*      */   {
/* 1477 */     JComboBox comboBox = new JResizableComboBox(ResizingManager.ComponentSize.SIZE_100X24);
/* 1478 */     comboBox.setActionCommand(Action.CHANGE_PRICE_RANGE_PRESENTATION_TYPE.name());
/* 1479 */     comboBox.addActionListener(this.actionListener);
/*      */ 
/* 1481 */     comboBox.setVisible(DataType.PRICE_RANGE_AGGREGATION.toString().equals(this.chartBean.getDataType()));
/*      */ 
/* 1483 */     comboBox.setEnabled(true);
/*      */ 
/* 1485 */     comboBox.setRenderer(new DefaultListCellRenderer(comboBox)
/*      */     {
/*      */       public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
/* 1488 */         Component comp = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
/* 1489 */         JLocalizableLabel label = new JLocalizableLabel();
/* 1490 */         label.setTextKeyParams(new Object[] { value.toString() });
/* 1491 */         label.setText("tabs.period.tamplate");
/*      */ 
/* 1493 */         label.setOpaque(true);
/* 1494 */         label.setForeground(comp.getForeground());
/* 1495 */         label.setBackground(comp.getBackground());
/*      */ 
/* 1497 */         if (this.val$comboBox.isEnabled())
/* 1498 */           label.setIcon((Icon)ChartToolBar.priceAggregationTypeIconsTop.get(value));
/*      */         else {
/* 1500 */           label.setIcon((Icon)ChartToolBar.priceAggregationTypeIcons.get(value));
/*      */         }
/*      */ 
/* 1503 */         return label;
/*      */       }
/*      */     });
/* 1507 */     for (DataType.DataPresentationType type : DataType.PRICE_RANGE_AGGREGATION.getSupportedPresentationTypes()) {
/* 1508 */       comboBox.addItem(type);
/*      */     }
/* 1510 */     comboBox.setSelectedItem(this.chartBean.getPriceRangePresentationType());
/*      */ 
/* 1513 */     setComboBox(ComboBoxType.PRICE_RANGE_PRESENTATION_TYPE, comboBox);
/* 1514 */     add(comboBox);
/*      */   }
/*      */ 
/*      */   private void initPointAndFigurePresentationTypeComboBox() {
/* 1518 */     JComboBox comboBox = new JResizableComboBox(ResizingManager.ComponentSize.SIZE_100X24);
/* 1519 */     comboBox.setActionCommand(Action.CHANGE_POINT_AND_FIGURE_PRESENTATION_TYPE.name());
/* 1520 */     comboBox.addActionListener(this.actionListener);
/*      */ 
/* 1522 */     comboBox.setVisible(DataType.POINT_AND_FIGURE.toString().equals(this.chartBean.getDataType()));
/*      */ 
/* 1524 */     comboBox.setEnabled(true);
/*      */ 
/* 1526 */     comboBox.setRenderer(new DefaultListCellRenderer(comboBox)
/*      */     {
/*      */       public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
/* 1529 */         Component comp = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
/* 1530 */         JLocalizableLabel label = new JLocalizableLabel();
/* 1531 */         label.setTextKeyParams(new Object[] { value.toString() });
/* 1532 */         label.setText("tabs.period.tamplate");
/*      */ 
/* 1534 */         label.setOpaque(true);
/* 1535 */         label.setForeground(comp.getForeground());
/* 1536 */         label.setBackground(comp.getBackground());
/*      */ 
/* 1538 */         if (this.val$comboBox.isEnabled())
/* 1539 */           label.setIcon((Icon)ChartToolBar.pointAndFigureTypeIconsTop.get(value));
/*      */         else {
/* 1541 */           label.setIcon((Icon)ChartToolBar.pointAndFigureTypeIcons.get(value));
/*      */         }
/*      */ 
/* 1544 */         return label;
/*      */       }
/*      */     });
/* 1548 */     for (DataType.DataPresentationType type : DataType.POINT_AND_FIGURE.getSupportedPresentationTypes()) {
/* 1549 */       comboBox.addItem(type);
/*      */     }
/* 1551 */     comboBox.setSelectedItem(this.chartBean.getPointAndFigurePresentationType());
/*      */ 
/* 1554 */     setComboBox(ComboBoxType.POINT_AND_FOGURE_PRESENTATION_TYPE, comboBox);
/* 1555 */     add(comboBox);
/*      */   }
/*      */ 
/*      */   private void initTickBarPresentationTypeComboBox() {
/* 1559 */     JComboBox comboBox = new JComboBox();
/* 1560 */     comboBox.setActionCommand(Action.CHANGE_TICK_BAR_PRESENTATION_TYPE.name());
/* 1561 */     comboBox.addActionListener(this.actionListener);
/*      */ 
/* 1563 */     comboBox.setVisible(DataType.TICK_BAR.equals(this.chartBean.getDataType()));
/*      */ 
/* 1565 */     comboBox.setEnabled(true);
/*      */ 
/* 1567 */     setSize(comboBox, ResizingManager.ComponentSize.SIZE_100X24.getSize());
/*      */ 
/* 1569 */     comboBox.setRenderer(new DefaultListCellRenderer(comboBox)
/*      */     {
/*      */       public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
/* 1572 */         Component comp = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
/* 1573 */         JLocalizableLabel label = new JLocalizableLabel();
/* 1574 */         label.setTextKeyParams(new Object[] { value.toString() });
/* 1575 */         label.setText("tabs.period.tamplate");
/*      */ 
/* 1577 */         label.setOpaque(true);
/* 1578 */         label.setForeground(comp.getForeground());
/* 1579 */         label.setBackground(comp.getBackground());
/*      */ 
/* 1581 */         if (this.val$comboBox.isEnabled())
/* 1582 */           label.setIcon((Icon)ChartToolBar.tickBarTypeIconsTop.get(value));
/*      */         else {
/* 1584 */           label.setIcon((Icon)ChartToolBar.tickBarTypeIcons.get(value));
/*      */         }
/*      */ 
/* 1587 */         return label;
/*      */       }
/*      */     });
/* 1591 */     for (DataType.DataPresentationType type : DataType.TICK_BAR.getSupportedPresentationTypes()) {
/* 1592 */       comboBox.addItem(type);
/*      */     }
/* 1594 */     comboBox.setSelectedItem(this.chartBean.getTickBarPresentationType());
/*      */ 
/* 1597 */     setComboBox(ComboBoxType.TICK_BAR_PRESENTATION_TYPE, comboBox);
/* 1598 */     add(comboBox);
/*      */   }
/*      */ 
/*      */   private void initRenkoPresentationTypeComboBox() {
/* 1602 */     JComboBox comboBox = new JComboBox();
/* 1603 */     comboBox.setActionCommand(Action.CHANGE_RENKO_PRESENTATION_TYPE.name());
/* 1604 */     comboBox.addActionListener(this.actionListener);
/*      */ 
/* 1606 */     comboBox.setVisible(DataType.RENKO.equals(this.chartBean.getDataType()));
/*      */ 
/* 1608 */     comboBox.setEnabled(true);
/*      */ 
/* 1610 */     setSize(comboBox, ResizingManager.ComponentSize.SIZE_100X24.getSize());
/*      */ 
/* 1612 */     comboBox.setRenderer(new DefaultListCellRenderer(comboBox)
/*      */     {
/*      */       public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
/* 1615 */         Component comp = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
/* 1616 */         JLocalizableLabel label = new JLocalizableLabel();
/* 1617 */         label.setTextKeyParams(new Object[] { value.toString() });
/* 1618 */         label.setText("tabs.period.tamplate");
/*      */ 
/* 1620 */         label.setOpaque(true);
/* 1621 */         label.setForeground(comp.getForeground());
/* 1622 */         label.setBackground(comp.getBackground());
/*      */ 
/* 1624 */         if (this.val$comboBox.isEnabled())
/* 1625 */           label.setIcon((Icon)ChartToolBar.renkoTypeIconsTop.get(value));
/*      */         else {
/* 1627 */           label.setIcon((Icon)ChartToolBar.renkoTypeIcons.get(value));
/*      */         }
/*      */ 
/* 1630 */         return label;
/*      */       }
/*      */     });
/* 1634 */     for (DataType.DataPresentationType type : DataType.RENKO.getSupportedPresentationTypes()) {
/* 1635 */       comboBox.addItem(type);
/*      */     }
/* 1637 */     comboBox.setSelectedItem(this.chartBean.getRenkoPresentationType());
/*      */ 
/* 1640 */     setComboBox(ComboBoxType.RENKO_PRESENTATION_TYPE, comboBox);
/* 1641 */     add(comboBox);
/*      */   }
/*      */ 
/*      */   void initTickTypeComboBox() {
/* 1645 */     JComboBox comboBox = new JResizableComboBox(ResizingManager.ComponentSize.SIZE_90X24);
/* 1646 */     comboBox.setActionCommand(Action.CHANGE_TICK_TYPE.name());
/* 1647 */     comboBox.addActionListener(this.actionListener);
/*      */ 
/* 1649 */     comboBox.setVisible(DataType.TICKS.toString().equals(this.chartBean.getDataType()));
/* 1650 */     comboBox.setEnabled(true);
/* 1651 */     comboBox.setRenderer(new DefaultListCellRenderer(comboBox)
/*      */     {
/*      */       public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
/* 1654 */         Component comp = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
/*      */ 
/* 1656 */         JLocalizableLabel label = new JLocalizableLabel();
/* 1657 */         label.setTextKeyParams(new Object[] { value.toString() });
/* 1658 */         label.setText("tabs.period.tamplate");
/*      */ 
/* 1660 */         label.setOpaque(true);
/* 1661 */         label.setForeground(comp.getForeground());
/* 1662 */         label.setBackground(comp.getBackground());
/*      */ 
/* 1664 */         if (this.val$comboBox.isEnabled())
/* 1665 */           label.setIcon((Icon)ChartToolBar.tickTypeIconsTop.get(value));
/*      */         else {
/* 1667 */           label.setIcon((Icon)ChartToolBar.tickTypeIcons.get(value));
/*      */         }
/*      */ 
/* 1670 */         return label;
/*      */       }
/*      */     });
/* 1674 */     for (DataType.DataPresentationType tickType : DataType.TICKS.getSupportedPresentationTypes()) {
/* 1675 */       comboBox.addItem(tickType);
/*      */     }
/*      */ 
/* 1678 */     comboBox.setSelectedItem(this.chartBean.getTicksPresentationType());
/*      */ 
/* 1680 */     setComboBox(ComboBoxType.TICKS_PRESENTATION_TYPE, comboBox);
/* 1681 */     add(comboBox);
/*      */   }
/*      */ 
/*      */   void initCandleTypeComboBox() {
/* 1685 */     JComboBox comboBox = new JResizableComboBox(ResizingManager.ComponentSize.SIZE_90X24);
/* 1686 */     comboBox.setActionCommand(Action.CHANGE_LINE_TYPE.name());
/* 1687 */     comboBox.addActionListener(this.actionListener);
/* 1688 */     comboBox.setVisible(DataType.TIME_PERIOD_AGGREGATION.toString().equals(this.chartBean.getDataType()));
/*      */ 
/* 1690 */     if (MACOSX) {
/* 1691 */       setSize(comboBox, ResizingManager.ComponentSize.SIZE_100X24.getSize());
/*      */     }
/*      */ 
/* 1701 */     comboBox.setRenderer(new DefaultListCellRenderer(comboBox)
/*      */     {
/*      */       public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
/* 1704 */         Component comp = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
/*      */ 
/* 1706 */         JLocalizableLabel label = new JLocalizableLabel();
/* 1707 */         label.setTextKeyParams(new Object[] { value.toString() });
/* 1708 */         label.setText("tabs.period.tamplate");
/*      */ 
/* 1710 */         label.setOpaque(true);
/* 1711 */         label.setForeground(comp.getForeground());
/* 1712 */         label.setBackground(comp.getBackground());
/*      */ 
/* 1714 */         if (this.val$comboBox.isEnabled())
/* 1715 */           label.setIcon((Icon)ChartToolBar.lineTypeIconsTop.get(value));
/*      */         else {
/* 1717 */           label.setIcon((Icon)ChartToolBar.lineTypeIcons.get(value));
/*      */         }
/*      */ 
/* 1720 */         return label;
/*      */       }
/*      */     });
/* 1724 */     for (DataType.DataPresentationType type : DataType.TIME_PERIOD_AGGREGATION.getSupportedPresentationTypes()) {
/* 1725 */       comboBox.addItem(type);
/*      */     }
/* 1727 */     comboBox.setSelectedItem(this.chartBean.getTimePeriodPresentationType());
/*      */ 
/* 1729 */     setComboBox(ComboBoxType.TIME_PERIOD_PRESENTATION_TYPE, comboBox);
/* 1730 */     add(comboBox);
/*      */   }
/*      */ 
/*      */   void initOfferSideButton(ComboBoxType comboBoxType) {
/* 1734 */     JComboBox comboBox = new JResizableComboBox(ResizingManager.ComponentSize.SIZE_80X24);
/*      */ 
/* 1736 */     comboBox.setRenderer(new DefaultListCellRenderer() {
/*      */       public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
/* 1738 */         Component comp = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
/*      */ 
/* 1740 */         JLocalizableLabel label = new JLocalizableLabel();
/* 1741 */         label.setTextKeyParams(new Object[] { value.toString() });
/* 1742 */         label.setText("tabs.period.tamplate");
/*      */ 
/* 1744 */         label.setOpaque(true);
/* 1745 */         label.setForeground(comp.getForeground());
/* 1746 */         label.setBackground(comp.getBackground());
/* 1747 */         return label;
/*      */       }
/*      */     });
/* 1751 */     comboBox.setVisible(Period.TICK != this.chartBean.getPeriod());
/* 1752 */     comboBox.setEnabled(true);
/* 1753 */     comboBox.addItem(OfferSide.ASK);
/* 1754 */     comboBox.addItem(OfferSide.BID);
/* 1755 */     comboBox.setSelectedItem(this.chartBean.getOfferSide());
/* 1756 */     comboBox.setActionCommand(Action.CHANGE_OFFERSIDE.name());
/* 1757 */     comboBox.addActionListener(this.actionListener);
/*      */ 
/* 1766 */     setComboBox(comboBoxType, comboBox);
/* 1767 */     add(comboBox);
/*      */   }
/*      */ 
/*      */   private void initInstrumentComboBox() {
/* 1771 */     JComboBox instrumentComboBox = new JResizableComboBox(ResizingManager.ComponentSize.SIZE_80X24);
/*      */ 
/* 1773 */     instrumentComboBox.setActionCommand(Action.CHANGE_INSTRUMENT.name());
/* 1774 */     instrumentComboBox.addActionListener(this.actionListener);
/* 1775 */     if ((MACOSX) || (LINUX)) {
/* 1776 */       setSize(instrumentComboBox, ResizingManager.ComponentSize.SIZE_100X24.getSize());
/*      */     }
/*      */ 
/* 1779 */     setComboBox(ComboBoxType.INSTRUMENTS, instrumentComboBox);
/* 1780 */     reloadInstrumentComboBoxEntrySet();
/*      */ 
/* 1782 */     instrumentComboBox.setMaximumRowCount(33);
/*      */ 
/* 1784 */     instrumentComboBox.setSelectedItem(getInstrument());
/*      */ 
/* 1786 */     add(instrumentComboBox);
/*      */   }
/*      */ 
/*      */   private void reloadInstrumentComboBoxEntrySet() {
/* 1790 */     JComboBox instrumentComboBox = getComboBox(ComboBoxType.INSTRUMENTS);
/* 1791 */     List availableInstruments = getSelectedInstruments();
/*      */ 
/* 1793 */     if (availableInstruments != null) {
/* 1794 */       instrumentComboBox.removeActionListener(this.actionListener);
/*      */ 
/* 1796 */       instrumentComboBox.removeAllItems();
/*      */ 
/* 1798 */       for (Instrument instrument : availableInstruments) {
/* 1799 */         if (InstrumentAvailabilityManager.getInstance().isAllowed(instrument)) {
/* 1800 */           instrumentComboBox.addItem(instrument);
/*      */         }
/*      */       }
/* 1803 */       if ((getInstrument() != null) && (availableInstruments.contains(getInstrument()))) {
/* 1804 */         instrumentComboBox.setSelectedItem(getInstrument());
/*      */       }
/*      */ 
/* 1807 */       instrumentComboBox.addActionListener(this.actionListener);
/*      */     }
/*      */   }
/*      */ 
/*      */   public List<Instrument> getSelectedInstruments() {
/* 1812 */     MarketView marketView = (MarketView)GreedContext.get("marketView");
/* 1813 */     List instruments = new ArrayList();
/*      */ 
/* 1815 */     Instrument[] activeInstruments = marketView.getActiveInstruments();
/*      */ 
/* 1817 */     if ((activeInstruments != null) && (activeInstruments.length > 0)) {
/* 1818 */       for (Instrument activeInstrument : activeInstruments) {
/* 1819 */         instruments.add(activeInstrument);
/*      */       }
/*      */     }
/*      */ 
/* 1823 */     return instruments;
/*      */   }
/*      */ 
/*      */   private void initPeriodsComboBox() {
/* 1827 */     JComboBox comboBox = new JResizableComboBox(ResizingManager.ComponentSize.SIZE_110X24);
/* 1828 */     comboBox.setRenderer(new DefaultListCellRenderer()
/*      */     {
/*      */       public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
/* 1831 */         Component comp = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
/*      */ 
/* 1833 */         if ((value instanceof JForexPeriod)) {
/* 1834 */           JForexPeriod dtpw = (JForexPeriod)value;
/* 1835 */           JLabel label = new JResizableLabel();
/* 1836 */           label.setText(ChartsLocalizator.localize(dtpw));
/* 1837 */           label.setOpaque(true);
/* 1838 */           label.setForeground(comp.getForeground());
/* 1839 */           label.setBackground(comp.getBackground());
/* 1840 */           return label;
/*      */         }
/* 1842 */         return comp;
/*      */       }
/*      */     });
/* 1846 */     comboBox.setVisible(true);
/* 1847 */     comboBox.setEnabled(true);
/* 1848 */     comboBox.setMaximumRowCount(15);
/*      */ 
/* 1850 */     fillWithJForexPeriods(comboBox);
/*      */ 
/* 1852 */     comboBox.setSelectedItem(new JForexPeriod(this.chartBean.getDataType(), this.chartBean.getPeriod(), this.chartBean.getPriceRange(), this.chartBean.getReversalAmount(), this.chartBean.getTickBarSize()));
/* 1853 */     comboBox.setActionCommand(Action.CHANGE_PERIOD.name());
/*      */ 
/* 1861 */     setComboBox(ComboBoxType.PERIODS, comboBox);
/* 1862 */     add(comboBox);
/*      */   }
/*      */ 
/*      */   private void fillWithJForexPeriods(JComboBox periodsComboBox) {
/* 1866 */     List periods = ((ClientSettingsStorage)GreedContext.get("settingsStorage")).restoreChartPeriods();
/* 1867 */     JForexPeriod dtpw = new JForexPeriod(this.chartBean.getDataType(), this.chartBean.getPeriod(), this.chartBean.getPriceRange(), this.chartBean.getReversalAmount(), this.chartBean.getTickBarSize());
/*      */ 
/* 1869 */     if (!periods.contains(dtpw)) {
/* 1870 */       periods.add(dtpw);
/*      */     }
/*      */ 
/* 1873 */     periods = ((ClientSettingsStorage)GreedContext.get("settingsStorage")).sortChartPeriods(periods);
/*      */ 
/* 1875 */     for (JForexPeriod wraper : periods)
/* 1876 */       periodsComboBox.addItem(wraper);
/*      */   }
/*      */ 
/*      */   public Integer getChartPanelId()
/*      */   {
/* 1881 */     return new Integer(this.chartBean.getId());
/*      */   }
/*      */ 
/*      */   public void disabled() {
/* 1885 */     enableButtons(false);
/*      */   }
/*      */ 
/*      */   public void enabled() {
/* 1889 */     enableButtons(true);
/*      */   }
/*      */ 
/*      */   void enableButtons(boolean isEnabled) {
/* 1893 */     SwingUtilities.invokeLater(new Runnable(isEnabled) {
/*      */       public void run() {
/* 1895 */         ChartToolBar.this.setEnabled(this.val$isEnabled, new ChartToolBar.ButtonType[] { ChartToolBar.ButtonType.ZOOM_IN, ChartToolBar.ButtonType.ZOOM_OUT, ChartToolBar.ButtonType.ZOOM_TO_AREA, ChartToolBar.ButtonType.CURSOR, ChartToolBar.ButtonType.PRICE_MARKER, ChartToolBar.ButtonType.TIME_MARKER, ChartToolBar.ButtonType.INDICATORS, ChartToolBar.ButtonType.FIBONACCI_GAN, ChartToolBar.ButtonType.DRAWINGS, ChartToolBar.ButtonType.PATTERNS, ChartToolBar.ButtonType.OHLC_INFORMER, ChartToolBar.ButtonType.CUSTOM_RANGE, ChartToolBar.ButtonType.VERTICAL_SHIFT, ChartToolBar.ButtonType.SAVE_CHART_AS_IMAGE });
/*      */       }
/*      */     });
/*      */   }
/*      */ 
/*      */   void updateTickerList(JPopupMenu chartPopupMenu)
/*      */   {
/* 1917 */     WorkspacePanel workspacePanel = ((ClientForm)GreedContext.get("clientGui")).getDealPanel().getWorkspacePanel();
/* 1918 */     if ((workspacePanel instanceof TickerPanel)) {
/* 1919 */       ClientSettingsStorage clientSettingsStorage = (ClientSettingsStorage)GreedContext.get("settingsStorage");
/* 1920 */       List selectedInstruments = clientSettingsStorage.restoreSelectedInstruments();
/*      */ 
/* 1922 */       chartPopupMenu.removeAll();
/*      */ 
/* 1924 */       for (String instrument : selectedInstruments) {
/* 1925 */         JMenuItem item = new JMenuItem(instrument);
/* 1926 */         item.setAction(new AbstractAction(instrument, instrument)
/*      */         {
/*      */           public void actionPerformed(ActionEvent e) {
/* 1929 */             ((ClientFormLayoutManager)GreedContext.get("layoutManager")).getWorkspaceHelper().showChart(Instrument.fromString(this.val$instrument));
/*      */           }
/*      */         });
/* 1934 */         chartPopupMenu.add(item);
/*      */       }
/*      */ 
/* 1937 */       if (!GreedContext.isStrategyAllowed()) {
/* 1938 */         chartPopupMenu.addSeparator();
/*      */ 
/* 1940 */         JMenuItem addSelectorItem = new JMenuItem();
/* 1941 */         addSelectorItem.setAction(new AbstractAction("Selector") {
/*      */           public void actionPerformed(ActionEvent e) {
/* 1943 */             ClientForm cf = (ClientForm)GreedContext.get("clientGui");
/* 1944 */             DealPanel dp = cf.getDealPanel();
/* 1945 */             WorkspacePanel ttp = dp.getWorkspacePanel();
/* 1946 */             ((TickerPanel)ttp).openInstrumentSelectionDialog((TickerPanel)ttp);
/*      */           }
/*      */         });
/* 1950 */         chartPopupMenu.add(addSelectorItem);
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   public Component[] getTableModeObservers() {
/* 1956 */     return new Component[] { (Component)this.buttons.get(ButtonType.FIBONACCI_GAN), (Component)this.buttons.get(ButtonType.DRAWINGS), (Component)this.buttons.get(ButtonType.PATTERNS), (Component)this.buttons.get(ButtonType.OHLC_INFORMER), (Component)this.buttons.get(ButtonType.INDICATORS), (Component)this.buttons.get(ButtonType.TIME_MARKER), (Component)this.buttons.get(ButtonType.PRICE_MARKER), (Component)this.buttons.get(ButtonType.CURSOR), (Component)this.buttons.get(ButtonType.ZOOM_TO_AREA), (Component)this.buttons.get(ButtonType.VERTICAL_SHIFT) };
/*      */   }
/*      */ 
/*      */   public Component[] getTableModeONLYObservers()
/*      */   {
/* 1971 */     return new Component[] { getLabel(LabelType.QUICK_SEARCH), getTextField(TextFieldType.QUICK_SEARCH), getButton(ButtonType.RESET_QUICK_FILTER) };
/*      */   }
/*      */ 
/*      */   public void handle(IEventHandler.Event event, Object params)
/*      */   {
/* 1981 */     if ((GreedContext.isStrategyAllowed()) && (event == IEventHandler.Event.STRATEGY_STATE_CHANGED))
/*      */     {
/* 1983 */       getButton(ButtonType.STOP_ALL_STRATEGIES).setEnabled(Strategies.get().getRunningStrategiesCount() > 0);
/*      */     }
/* 1985 */     else if (event == IEventHandler.Event.PRESENTED_INSTRUMENTS_CHANGED)
/* 1986 */       reloadInstrumentComboBoxEntrySet();
/*      */   }
/*      */ 
/*      */   public Instrument getInstrument()
/*      */   {
/* 1991 */     return this.chartBean.getInstrument();
/*      */   }
/*      */ 
/*      */   public void chartModeChanged(ChartModeChangeListener.ChartMode chartMode)
/*      */   {
/* 1996 */     setCurrentChartMode(chartMode);
/*      */ 
/* 1998 */     Component[] tableModeObservers = getTableModeObservers();
/*      */ 
/* 2000 */     boolean visibleForChartMode = ChartModeChangeListener.ChartMode.CHART.equals(chartMode);
/*      */ 
/* 2003 */     for (Component jc : tableModeObservers) {
/* 2004 */       jc.setVisible(visibleForChartMode);
/*      */     }
/* 2006 */     for (Component strut : this.tableModeInvisibleStruts) {
/* 2007 */       strut.setVisible(visibleForChartMode);
/*      */     }
/*      */ 
/* 2010 */     for (Component jc : getTableModeONLYObservers())
/* 2011 */       jc.setVisible(!visibleForChartMode);
/*      */   }
/*      */ 
/*      */   public ChartModeChangeListener.ChartMode getCurrentChartMode()
/*      */   {
/* 2016 */     return this.currentChartMode;
/*      */   }
/*      */ 
/*      */   public void setCurrentChartMode(ChartModeChangeListener.ChartMode currentChartMode) {
/* 2020 */     this.currentChartMode = currentChartMode;
/*      */   }
/*      */ 
/*      */   private void initQuickSearch() {
/* 2024 */     JLabel lblQuickSearch = new JLocalizableLabel("title.quick.search");
/* 2025 */     setLabel(LabelType.QUICK_SEARCH, lblQuickSearch);
/*      */ 
/* 2027 */     add(Box.createHorizontalStrut(10));
/*      */ 
/* 2029 */     add(lblQuickSearch);
/*      */ 
/* 2031 */     JTextField txtQuickSearch = new JTextField();
/* 2032 */     setTextField(TextFieldType.QUICK_SEARCH, txtQuickSearch);
/*      */ 
/* 2034 */     txtQuickSearch.addKeyListener(new KeyAdapter(txtQuickSearch)
/*      */     {
/*      */       public void keyReleased(KeyEvent e) {
/* 2037 */         ChartToolBar.this.actionListener.actionPerformed(new ActionEvent(this.val$txtQuickSearch.getText(), 1, ChartToolBar.Action.PERFORM_QUICK_FILTER.name()));
/*      */       }
/*      */     });
/* 2041 */     setSize(txtQuickSearch, ResizingManager.ComponentSize.SIZE_100X24.getSize());
/*      */ 
/* 2043 */     add(txtQuickSearch);
/*      */ 
/* 2045 */     JButton btnRefresh = new JButton(new ResizableIcon("toolbar_table_refresh.png"));
/* 2046 */     setButton(ButtonType.RESET_QUICK_FILTER, btnRefresh);
/*      */ 
/* 2048 */     setSize(btnRefresh, ResizingManager.ComponentSize.TOLBAR_BTN_SIZE.getSize());
/* 2049 */     btnRefresh.addActionListener(new ActionListener(txtQuickSearch)
/*      */     {
/*      */       public void actionPerformed(ActionEvent e) {
/* 2052 */         this.val$txtQuickSearch.setText("");
/* 2053 */         ChartToolBar.this.actionListener.actionPerformed(new ActionEvent("", 1, ChartToolBar.Action.PERFORM_QUICK_FILTER.name()));
/* 2054 */         this.val$txtQuickSearch.requestFocus();
/*      */       }
/*      */     });
/* 2058 */     add(btnRefresh);
/*      */   }
/*      */ 
/*      */   public void changeInstrument(Instrument instrument) {
/* 2062 */     JComboBox checkBox = getComboBox(ComboBoxType.INSTRUMENTS);
/* 2063 */     checkBox.setSelectedItem(instrument);
/*      */   }
/*      */ 
/*      */   public static enum ComponentType
/*      */   {
/*  304 */     QUICK_SEARCH;
/*      */   }
/*      */ 
/*      */   public static enum TextFieldType
/*      */   {
/*  298 */     QUICK_SEARCH;
/*      */   }
/*      */ 
/*      */   public static enum LabelType
/*      */   {
/*  292 */     QUICK_SEARCH;
/*      */   }
/*      */ 
/*      */   public static enum ButtonType
/*      */   {
/*  267 */     CUSTOM_RANGE, 
/*  268 */     AUTOSHIFT, 
/*  269 */     VERTICAL_SHIFT, 
/*  270 */     ZOOM_IN, 
/*  271 */     ZOOM_OUT, 
/*  272 */     ZOOM_TO_AREA, 
/*  273 */     CURSOR, 
/*  274 */     PRICE_MARKER, 
/*  275 */     TIME_MARKER, 
/*  276 */     OHLC_INFORMER, 
/*  277 */     SAVE, 
/*  278 */     SAVE_CHART_AS_IMAGE, 
/*  279 */     FIBONACCI_GAN, 
/*  280 */     DRAWINGS, 
/*  281 */     PATTERNS, 
/*  282 */     INDICATORS, 
/*  283 */     STOP_ALL_STRATEGIES, 
/*  284 */     CHARTS, 
/*  285 */     TRADING, 
/*  286 */     RESET_QUICK_FILTER, 
/*  287 */     THEMES, 
/*  288 */     TEMPLATES;
/*      */   }
/*      */ 
/*      */   public static enum ComboBoxType
/*      */   {
/*  248 */     PERIODS, 
/*  249 */     TIME_AGGREAGTION_OFFER_SIDE, 
/*  250 */     PRICE_RANGE_OFFER_SIDE, 
/*  251 */     POINT_AND_FIGURE_OFFER_SIDE, 
/*  252 */     TIME_PERIOD_PRESENTATION_TYPE, 
/*  253 */     TICKS_PRESENTATION_TYPE, 
/*  254 */     INSTRUMENTS, 
/*  255 */     DATA_TYPE, 
/*  256 */     PRICE_RANGE_PRESENTATION_TYPE, 
/*  257 */     POINT_AND_FOGURE_PRESENTATION_TYPE, 
/*  258 */     TICK_BAR_PRESENTATION_TYPE, 
/*  259 */     TICK_BAR_OFFER_SIDE, 
/*  260 */     RENKO_OFFER_SIDE, 
/*  261 */     RENKO_PRESENTATION_TYPE;
/*      */   }
/*      */ 
/*      */   public static enum Action
/*      */   {
/*  159 */     CHANGE_JFOREX_PERIOD, 
/*  160 */     CHANGE_PERIOD, 
/*  161 */     CHANGE_OFFERSIDE, 
/*  162 */     CUSTOM_INTERVAL, 
/*  163 */     CUSTOM_RANGE, 
/*  164 */     AUTOSHIFT, 
/*  165 */     VERTICAL_SHIFT, 
/*  166 */     ZOOM_IN, 
/*  167 */     ZOOM_OUT, 
/*  168 */     ZOOM_TO_AREA, 
/*  169 */     CURSOR_VISIBILITY, 
/*  170 */     PRICE_MARKER, 
/*  171 */     TIME_MARKER, 
/*  172 */     OHLC_INFO, 
/*  173 */     PATTERN_WIDGET, 
/*  174 */     CHANGE_LINE_TYPE, 
/*  175 */     CHANGE_TICK_TYPE, 
/*  176 */     ADD_INDICATOR, 
/*  177 */     CLEAR_INDICATORS, 
/*  178 */     SAVE_CHART_IMAGE_TO_FILE, 
/*  179 */     SAVE_CHART_IMAGE_TO_CLIPBOARD, 
/*  180 */     PRINT_CHART_IMAGE, 
/*  181 */     SAVE_CHART_TABLE_TO_FILE, 
/*  182 */     CHOOSE_DRAWING, 
/*  183 */     CHOOSE_FIBONACCI_GAN, 
/*  184 */     RANGE_SCROOL_BAR, 
/*  185 */     CHANGE_INSTRUMENT, 
/*  186 */     PERFORM_QUICK_FILTER, 
/*  187 */     CHANGE_PRICE_RANGE_PRESENTATION_TYPE, 
/*  188 */     CHANGE_PRICE_RANGE, 
/*  189 */     CHANGE_POINT_AND_FIGURE_PRESENTATION_TYPE, 
/*  190 */     CHANGE_TICK_BAR_PRESENTATION_TYPE, 
/*  191 */     OPEN_THEMES_DIALOG, 
/*  192 */     CHANGE_RENKO_PRESENTATION_TYPE;
/*      */   }
/*      */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.chart.toolbar.ChartToolBar
 * JD-Core Version:    0.6.0
 */