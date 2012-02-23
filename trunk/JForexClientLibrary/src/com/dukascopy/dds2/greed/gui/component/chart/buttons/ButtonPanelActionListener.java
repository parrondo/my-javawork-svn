/*     */ package com.dukascopy.dds2.greed.gui.component.chart.buttons;
/*     */ 
/*     */ import com.dukascopy.api.DataType.DataPresentationType;
/*     */ import com.dukascopy.api.IChart.Type;
/*     */ import com.dukascopy.api.Instrument;
/*     */ import com.dukascopy.api.OfferSide;
/*     */ import com.dukascopy.api.Period;
/*     */ import com.dukascopy.api.PriceRange;
/*     */ import com.dukascopy.api.impl.IndicatorWrapper;
/*     */ import com.dukascopy.charts.data.datacache.JForexPeriod;
/*     */ import com.dukascopy.charts.dialogs.ChartThemesDialog;
/*     */ import com.dukascopy.charts.main.DDSChartsActionAdapter;
/*     */ import com.dukascopy.charts.main.interfaces.DDSChartsController;
/*     */ import com.dukascopy.charts.persistence.ChartBean;
/*     */ import com.dukascopy.charts.persistence.LastUsedIndicatorBean;
/*     */ import com.dukascopy.charts.utils.file.DCFileChooser;
/*     */ import com.dukascopy.charts.utils.file.filter.CsvFileFilter;
/*     */ import com.dukascopy.charts.utils.file.filter.PngFileFilter;
/*     */ import com.dukascopy.dds2.greed.GreedContext;
/*     */ import com.dukascopy.dds2.greed.actions.AppActionEvent;
/*     */ import com.dukascopy.dds2.greed.actions.InstrumentSubscribeAction;
/*     */ import com.dukascopy.dds2.greed.gui.component.chart.toolbar.ChartToolBar;
/*     */ import com.dukascopy.dds2.greed.gui.component.chart.toolbar.ChartToolBar.Action;
/*     */ import com.dukascopy.dds2.greed.gui.settings.ClientSettingsStorage;
/*     */ import com.dukascopy.dds2.greed.model.MarketView;
/*     */ import java.awt.event.ActionEvent;
/*     */ import java.awt.event.ActionListener;
/*     */ import java.io.File;
/*     */ import java.util.HashMap;
/*     */ import java.util.HashSet;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import javax.swing.JButton;
/*     */ import javax.swing.JComboBox;
/*     */ import javax.swing.JFrame;
/*     */ 
/*     */ public class ButtonPanelActionListener
/*     */   implements ActionListener
/*     */ {
/*     */   private final Integer chartPanelId;
/*     */   private final DDSChartsController ddsChartsController;
/*  68 */   private final Map<String, IChart.Type> drawingsActionMap = new HashMap();
/*     */ 
/*     */   public ButtonPanelActionListener(Integer chartPanelId, DDSChartsController ddsChartsController)
/*     */   {
/*  74 */     this.chartPanelId = chartPanelId;
/*  75 */     this.ddsChartsController = ddsChartsController;
/*     */ 
/*  77 */     initDrawingsActionMap();
/*     */   }
/*     */ 
/*     */   private void initDrawingsActionMap() {
/*  81 */     this.drawingsActionMap.put("item.precent.lines", IChart.Type.PERCENT);
/*  82 */     this.drawingsActionMap.put("item.channel.lines", IChart.Type.CHANNEL);
/*  83 */     this.drawingsActionMap.put("item.poly.line", IChart.Type.POLY_LINE);
/*  84 */     this.drawingsActionMap.put("item.short.line", IChart.Type.SHORT_LINE);
/*  85 */     this.drawingsActionMap.put("item.long.line", IChart.Type.LONG_LINE);
/*  86 */     this.drawingsActionMap.put("item.ray.line", IChart.Type.RAY_LINE);
/*  87 */     this.drawingsActionMap.put("item.rectangle", IChart.Type.RECTANGLE);
/*  88 */     this.drawingsActionMap.put("item.triangle", IChart.Type.TRIANGLE);
/*  89 */     this.drawingsActionMap.put("item.ellipse", IChart.Type.ELLIPSE);
/*  90 */     this.drawingsActionMap.put("item.signal.up", IChart.Type.SIGNAL_UP);
/*  91 */     this.drawingsActionMap.put("item.signal.down", IChart.Type.SIGNAL_DOWN);
/*  92 */     this.drawingsActionMap.put("item.horizontal.line", IChart.Type.HLINE);
/*  93 */     this.drawingsActionMap.put("item.vertical.line", IChart.Type.VLINE);
/*  94 */     this.drawingsActionMap.put("item.text", IChart.Type.TEXT);
/*  95 */     this.drawingsActionMap.put("item.periods", IChart.Type.CYCLES);
/*     */ 
/*  98 */     this.drawingsActionMap.put("item.fibonacci.fan.lines", IChart.Type.FIBOFAN);
/*  99 */     this.drawingsActionMap.put("item.fibonacci.fan.arcs", IChart.Type.FIBOARC);
/* 100 */     this.drawingsActionMap.put("item.fibonacci.retracements", IChart.Type.FIBO);
/* 101 */     this.drawingsActionMap.put("item.fibonacci.time.zones", IChart.Type.FIBOTIMES);
/* 102 */     this.drawingsActionMap.put("item.fibonacci.expansion", IChart.Type.EXPANSION);
/* 103 */     this.drawingsActionMap.put("item.gann.angle", IChart.Type.GANNFAN);
/* 104 */     this.drawingsActionMap.put("item.gann.periods", IChart.Type.GANNGRID);
/*     */ 
/* 107 */     this.drawingsActionMap.put("item.andrews.pitchfork", IChart.Type.PITCHFORK);
/*     */   }
/*     */ 
/*     */   public void actionPerformed(ActionEvent event) {
/* 111 */     String actionCommand = event.getActionCommand();
/*     */ 
/* 113 */     if ((actionCommand == null) || (actionCommand.trim().length() == 0)) {
/* 114 */       return;
/*     */     }
/*     */ 
/* 117 */     ChartToolBar.Action action = ChartToolBar.Action.valueOf(actionCommand);
/*     */ 
/* 119 */     if (ChartToolBar.Action.CHANGE_JFOREX_PERIOD == action) {
/* 120 */       processJForexPeriodChange(event);
/* 121 */     } else if (ChartToolBar.Action.CHANGE_PERIOD == action) {
/* 122 */       processChangeAggregationPeriod(event);
/* 123 */     } else if (ChartToolBar.Action.CHANGE_OFFERSIDE == action) {
/* 124 */       processChangeBidAsk(event);
/* 125 */     } else if (ChartToolBar.Action.CHANGE_LINE_TYPE == action) {
/* 126 */       processChangeLineType(event);
/* 127 */     } else if (ChartToolBar.Action.CHANGE_TICK_TYPE == action) {
/* 128 */       processChangeTickType(event);
/* 129 */     } else if (ChartToolBar.Action.CUSTOM_RANGE == action) {
/* 130 */       selectCustomRange(event);
/* 131 */     } else if (ChartToolBar.Action.ZOOM_IN == action) {
/* 132 */       this.ddsChartsController.zoomIn(this.chartPanelId);
/* 133 */     } else if (ChartToolBar.Action.ZOOM_OUT == action) {
/* 134 */       this.ddsChartsController.zoomOut(this.chartPanelId);
/* 135 */     } else if (ChartToolBar.Action.ZOOM_TO_AREA == action) {
/* 136 */       this.ddsChartsController.startZoomingToArea(this.chartPanelId);
/* 137 */     } else if (ChartToolBar.Action.AUTOSHIFT == action) {
/* 138 */       this.ddsChartsController.shiftChartToFront(this.chartPanelId);
/* 139 */     } else if (ChartToolBar.Action.VERTICAL_SHIFT == action) {
/* 140 */       setVerticalChartMovementEnabled(event);
/* 141 */     } else if (ChartToolBar.Action.CURSOR_VISIBILITY == action) {
/* 142 */       switchMouseCursorVisibility(event);
/* 143 */     } else if (ChartToolBar.Action.PRICE_MARKER == action) {
/* 144 */       this.ddsChartsController.startDrawing(this.chartPanelId, IChart.Type.PRICEMARKER);
/* 145 */     } else if (ChartToolBar.Action.TIME_MARKER == action) {
/* 146 */       this.ddsChartsController.startDrawing(this.chartPanelId, IChart.Type.TIMEMARKER);
/* 147 */     } else if (ChartToolBar.Action.OHLC_INFO == action) {
/* 148 */       this.ddsChartsController.startDrawing(this.chartPanelId, IChart.Type.OHLC_INFORMER);
/* 149 */     } else if (ChartToolBar.Action.PATTERN_WIDGET == action) {
/* 150 */       this.ddsChartsController.startDrawing(this.chartPanelId, IChart.Type.PATTERN_WIDGET);
/*     */     }
/*     */     else
/*     */     {
/*     */       ClientSettingsStorage clientSettingsStorage;
/* 151 */       if (ChartToolBar.Action.ADD_INDICATOR == action) {
/* 152 */         List indicatorWrappers = this.ddsChartsController.createAddEditIndicatorsDialog(this.chartPanelId.intValue());
/* 153 */         if (indicatorWrappers != null) {
/* 154 */           clientSettingsStorage = (ClientSettingsStorage)GreedContext.get("settingsStorage");
/*     */ 
/* 156 */           for (IndicatorWrapper iw : indicatorWrappers) {
/* 157 */             LastUsedIndicatorBean indicatorBean = DDSChartsActionAdapter.convertToLastUsedIndicatorBean(iw);
/* 158 */             clientSettingsStorage.addLastUsedIndicatorName(indicatorBean);
/*     */           }
/*     */         }
/* 161 */       } else if (ChartToolBar.Action.CLEAR_INDICATORS == action) {
/* 162 */         ((ClientSettingsStorage)GreedContext.get("settingsStorage")).clearAllLastUsedIndicatorNames();
/* 163 */       } else if (ChartToolBar.Action.CHOOSE_DRAWING == action) {
/* 164 */         processChooseDrawing(event);
/* 165 */       } else if (ChartToolBar.Action.CHOOSE_FIBONACCI_GAN == action) {
/* 166 */         processChooseDrawing(event);
/* 167 */       } else if (ChartToolBar.Action.SAVE_CHART_IMAGE_TO_FILE == action) {
/* 168 */         processSaveWorkspaceToFile();
/* 169 */       } else if (ChartToolBar.Action.SAVE_CHART_IMAGE_TO_CLIPBOARD == action) {
/* 170 */         this.ddsChartsController.saveWorkspaceImageToClipboard(this.chartPanelId);
/* 171 */       } else if (ChartToolBar.Action.PRINT_CHART_IMAGE == action) {
/* 172 */         this.ddsChartsController.printWorkspaceImage(this.chartPanelId);
/* 173 */       } else if (ChartToolBar.Action.RANGE_SCROOL_BAR == action) {
/* 174 */         this.ddsChartsController.switchRangeScrollBarVisibility(this.chartPanelId);
/* 175 */       } else if (ChartToolBar.Action.CHANGE_INSTRUMENT == action) {
/* 176 */         processChangeInstrument(event);
/* 177 */       } else if (ChartToolBar.Action.SAVE_CHART_TABLE_TO_FILE == action) {
/* 178 */         processSaveChartTableToFile(event, this.chartPanelId);
/* 179 */       } else if (ChartToolBar.Action.PERFORM_QUICK_FILTER == action) {
/* 180 */         performQuickFilter(event);
/* 181 */       } else if (ChartToolBar.Action.CHANGE_PRICE_RANGE_PRESENTATION_TYPE == action) {
/* 182 */         changePriceRangePresentationType(event, this.chartPanelId);
/* 183 */       } else if (ChartToolBar.Action.CHANGE_PRICE_RANGE == action) {
/* 184 */         changePriceRange(event, this.chartPanelId);
/* 185 */       } else if (ChartToolBar.Action.CHANGE_POINT_AND_FIGURE_PRESENTATION_TYPE == action) {
/* 186 */         changePointAndFigureType(event, this.chartPanelId);
/* 187 */       } else if (ChartToolBar.Action.CHANGE_TICK_BAR_PRESENTATION_TYPE == action) {
/* 188 */         changeTickBarPresentationType(event, this.chartPanelId);
/* 189 */       } else if (ChartToolBar.Action.CHANGE_RENKO_PRESENTATION_TYPE == action) {
/* 190 */         changeRenkoPresentationType(event, this.chartPanelId);
/* 191 */       } else if (ChartToolBar.Action.OPEN_THEMES_DIALOG == action) {
/* 192 */         openThemesDialog(this.chartPanelId.intValue());
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   private void openThemesDialog(int chartId) {
/* 197 */     JFrame mainFrame = (JFrame)GreedContext.get("clientGui");
/* 198 */     new ChartThemesDialog(mainFrame, chartId);
/*     */   }
/*     */ 
/*     */   private void changeTickBarPresentationType(ActionEvent event, Integer chartPanelId) {
/* 202 */     JComboBox cmb = (JComboBox)event.getSource();
/* 203 */     DataType.DataPresentationType tickBarPresentationType = (DataType.DataPresentationType)cmb.getSelectedItem();
/* 204 */     this.ddsChartsController.changeTickBarPresentationType(chartPanelId, tickBarPresentationType);
/*     */   }
/*     */ 
/*     */   private void changeRenkoPresentationType(ActionEvent event, Integer chartPanelId) {
/* 208 */     JComboBox cmb = (JComboBox)event.getSource();
/* 209 */     DataType.DataPresentationType renkoPresentationType = (DataType.DataPresentationType)cmb.getSelectedItem();
/* 210 */     this.ddsChartsController.changeRenkoPresentationType(chartPanelId, renkoPresentationType);
/*     */   }
/*     */ 
/*     */   private void changePointAndFigureType(ActionEvent event, Integer chartPanelId)
/*     */   {
/* 217 */     JComboBox cmb = (JComboBox)event.getSource();
/* 218 */     DataType.DataPresentationType pointAndFigurePresentationType = (DataType.DataPresentationType)cmb.getSelectedItem();
/* 219 */     this.ddsChartsController.changePointAndFigurePresentationType(chartPanelId, pointAndFigurePresentationType);
/*     */   }
/*     */ 
/*     */   private void processJForexPeriodChange(ActionEvent event)
/*     */   {
/* 224 */     JForexPeriod jForexPeriod = (JForexPeriod)event.getSource();
/* 225 */     this.ddsChartsController.changeJForexPeriod(this.chartPanelId, jForexPeriod);
/*     */   }
/*     */ 
/*     */   private void selectCustomRange(ActionEvent event) {
/* 229 */     List allowedPeriods = ((ClientSettingsStorage)GreedContext.get("settingsStorage")).restoreChartPeriods();
/* 230 */     this.ddsChartsController.selectCustomRange(this.chartPanelId, allowedPeriods);
/*     */   }
/*     */ 
/*     */   private void changePriceRange(ActionEvent event, Integer chartPanelId) {
/* 234 */     PriceRange priceRange = (PriceRange)event.getSource();
/* 235 */     this.ddsChartsController.changePriceRange(chartPanelId, priceRange);
/*     */   }
/*     */ 
/*     */   private void changePriceRangePresentationType(ActionEvent event, Integer chartPanelId) {
/* 239 */     JComboBox cmb = (JComboBox)event.getSource();
/* 240 */     DataType.DataPresentationType priceRangePresentationType = (DataType.DataPresentationType)cmb.getSelectedItem();
/* 241 */     this.ddsChartsController.changePriceRangePresentationType(chartPanelId, priceRangePresentationType);
/*     */   }
/*     */ 
/*     */   private void performQuickFilter(ActionEvent event)
/*     */   {
/* 246 */     String txt = (String)event.getSource();
/* 247 */     this.ddsChartsController.applyQuickTableDataFilter(this.chartPanelId, txt);
/*     */   }
/*     */ 
/*     */   private void processSaveChartTableToFile(ActionEvent event, Integer chartPanelId) {
/* 251 */     String initFileName = getChartName(chartPanelId);
/* 252 */     File file = DCFileChooser.saveFileWithReplacementConfirmation((JFrame)GreedContext.get("clientGui"), null, new File(initFileName + "_data"), new CsvFileFilter());
/* 253 */     this.ddsChartsController.saveChartTableDataToFile(chartPanelId, file);
/*     */   }
/*     */ 
/*     */   private String getChartName(Integer chartPanelId) {
/* 257 */     ChartBean chartBean = this.ddsChartsController.synchronizeAndGetChartBean(chartPanelId);
/* 258 */     if (chartBean == null) {
/* 259 */       return "Chart";
/*     */     }
/* 261 */     String initFileName = "Chart_" + chartBean.getInstrument().toString() + "_" + chartBean.getPeriod().toString();
/* 262 */     initFileName = initFileName.replace('/', '_');
/* 263 */     return initFileName;
/*     */   }
/*     */ 
/*     */   private void switchMouseCursorVisibility(ActionEvent evt) {
/* 267 */     JButton button = (JButton)evt.getSource();
/* 268 */     this.ddsChartsController.switchMouseCursor(this.chartPanelId, button.getIcon() == ChartToolBar.switchCursorIconTop);
/*     */   }
/*     */ 
/*     */   private void setVerticalChartMovementEnabled(ActionEvent evt) {
/* 272 */     JButton button = (JButton)evt.getSource();
/* 273 */     this.ddsChartsController.setVerticalChartMovementEnabled(this.chartPanelId, button.getIcon() == ChartToolBar.verticalChartShiftIconTop);
/*     */   }
/*     */ 
/*     */   private void processSaveWorkspaceToFile() {
/* 277 */     String initFileName = getChartName(this.chartPanelId);
/* 278 */     File file = DCFileChooser.saveFileWithReplacementConfirmation((JFrame)GreedContext.get("clientGui"), null, new File(initFileName + "_snapshot"), new PngFileFilter());
/* 279 */     this.ddsChartsController.saveWorkspaceImageToFile(this.chartPanelId, file);
/*     */   }
/*     */ 
/*     */   private void processChooseDrawing(ActionEvent evt) {
/* 283 */     PanelMenuItem panelMenuItem = (PanelMenuItem)evt.getSource();
/* 284 */     String drawing = panelMenuItem.getTextKey();
/*     */ 
/* 286 */     IChart.Type type = (IChart.Type)this.drawingsActionMap.get(drawing);
/* 287 */     this.ddsChartsController.startDrawing(this.chartPanelId, type);
/*     */   }
/*     */ 
/*     */   private void processChangeLineType(ActionEvent evt) {
/* 291 */     JComboBox lineTypeComboBox = (JComboBox)evt.getSource();
/* 292 */     DataType.DataPresentationType selectedLineType = (DataType.DataPresentationType)lineTypeComboBox.getSelectedItem();
/* 293 */     this.ddsChartsController.changeLineType(this.chartPanelId, selectedLineType);
/*     */   }
/*     */ 
/*     */   private void processChangeTickType(ActionEvent evt) {
/* 297 */     JComboBox tickTypeComboBox = (JComboBox)evt.getSource();
/* 298 */     DataType.DataPresentationType selectedTickType = (DataType.DataPresentationType)tickTypeComboBox.getSelectedItem();
/* 299 */     this.ddsChartsController.changeTickType(this.chartPanelId, selectedTickType);
/*     */   }
/*     */ 
/*     */   private void processChangeBidAsk(ActionEvent evt) {
/* 303 */     JComboBox offerSidesComboBox = (JComboBox)evt.getSource();
/* 304 */     Object selectedItem = offerSidesComboBox.getSelectedItem();
/* 305 */     if (!(selectedItem instanceof OfferSide)) {
/* 306 */       return;
/*     */     }
/* 308 */     OfferSide selectedOfferSide = (OfferSide)selectedItem;
/* 309 */     this.ddsChartsController.switchBidAskTo(this.chartPanelId, selectedOfferSide);
/*     */   }
/*     */ 
/*     */   private void processChangeAggregationPeriod(ActionEvent evt) {
/* 313 */     JComboBox periodsComboBox = (JComboBox)evt.getSource();
/* 314 */     Object selectedItem = periodsComboBox.getSelectedItem();
/* 315 */     if (!(selectedItem instanceof JForexPeriod)) {
/* 316 */       return;
/*     */     }
/* 318 */     Period selectedPeriod = ((JForexPeriod)selectedItem).getPeriod();
/* 319 */     this.ddsChartsController.changeAggregationPeriod(this.chartPanelId, selectedPeriod);
/*     */   }
/*     */ 
/*     */   private void processChangeInstrument(ActionEvent event) {
/* 323 */     JComboBox instrumentComboBox = (JComboBox)event.getSource();
/* 324 */     Object selectedItem = instrumentComboBox.getSelectedItem();
/* 325 */     if (!(selectedItem instanceof Instrument)) {
/* 326 */       return;
/*     */     }
/* 328 */     Instrument selectedInstrument = (Instrument)selectedItem;
/*     */ 
/* 330 */     subscribeAndSave(selectedInstrument);
/*     */ 
/* 332 */     this.ddsChartsController.changeInstrument(this.chartPanelId, selectedInstrument);
/*     */   }
/*     */ 
/*     */   private void subscribeAndSave(Instrument selectedInstrument) {
/* 336 */     String strInstrument = selectedInstrument.toString();
/* 337 */     Set strInstruments = new HashSet();
/* 338 */     strInstruments.add(strInstrument);
/*     */ 
/* 340 */     MarketView marketView = (MarketView)GreedContext.get("marketView");
/* 341 */     Set dependentInstruments = marketView.fetchCurrenciesNeededForProfitlossCalculation(strInstrument);
/* 342 */     if ((dependentInstruments != null) && (!dependentInstruments.isEmpty())) {
/* 343 */       strInstruments.addAll(dependentInstruments);
/*     */     }
/*     */ 
/* 346 */     AppActionEvent e = new InstrumentSubscribeAction(this, strInstruments);
/* 347 */     GreedContext.publishEvent(e);
/*     */ 
/* 349 */     ClientSettingsStorage clientSettingsStorage = (ClientSettingsStorage)GreedContext.get("settingsStorage");
/* 350 */     List instruments = clientSettingsStorage.restoreSelectedInstruments();
/* 351 */     if ((instruments != null) && (!instruments.isEmpty()))
/* 352 */       for (String strInstr : instruments)
/* 353 */         if (!strInstruments.contains(strInstr))
/* 354 */           strInstruments.add(strInstr);
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.chart.buttons.ButtonPanelActionListener
 * JD-Core Version:    0.6.0
 */