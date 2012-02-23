/*     */ package com.dukascopy.dds2.greed.gui.component.ticker;
/*     */ 
/*     */ import com.dukascopy.api.Instrument;
/*     */ import com.dukascopy.charts.persistence.ChartBean;
/*     */ import com.dukascopy.charts.persistence.IdManager;
/*     */ import com.dukascopy.dds2.greed.GreedContext;
/*     */ import com.dukascopy.dds2.greed.gui.ClientFormLayoutManager;
/*     */ import com.dukascopy.dds2.greed.gui.DealPanel;
/*     */ import com.dukascopy.dds2.greed.gui.InstrumentAvailabilityManager;
/*     */ import com.dukascopy.dds2.greed.gui.JForexClientFormLayoutManager;
/*     */ import com.dukascopy.dds2.greed.gui.component.WorkspacePanel;
/*     */ import com.dukascopy.dds2.greed.gui.component.chart.ChartsFrame;
/*     */ import com.dukascopy.dds2.greed.gui.component.detached.OrderEntryDetached;
/*     */ import com.dukascopy.dds2.greed.gui.component.dialog.InstrumentSelectorDialog;
/*     */ import com.dukascopy.dds2.greed.gui.component.moverview.MarketOverviewFrame;
/*     */ import com.dukascopy.dds2.greed.gui.component.orders.OrderEntryPanel;
/*     */ import com.dukascopy.dds2.greed.gui.component.splitPane.MultiSplitPane;
/*     */ import com.dukascopy.dds2.greed.gui.component.splitPane.MultiSplitable;
/*     */ import com.dukascopy.dds2.greed.gui.component.table.ScrollPaneHeaderRenderer;
/*     */ import com.dukascopy.dds2.greed.gui.component.ticker.actions.CloseSlectedItemsAction;
/*     */ import com.dukascopy.dds2.greed.gui.component.tree.WorkspaceJTree;
/*     */ import com.dukascopy.dds2.greed.gui.component.tree.actions.ITreeAction;
/*     */ import com.dukascopy.dds2.greed.gui.component.tree.actions.TreeActionFactory;
/*     */ import com.dukascopy.dds2.greed.gui.component.tree.actions.TreeActionType;
/*     */ import com.dukascopy.dds2.greed.gui.helpers.IWorkspaceHelper;
/*     */ import com.dukascopy.dds2.greed.gui.l10n.components.JLocalizableMenu;
/*     */ import com.dukascopy.dds2.greed.gui.l10n.components.JLocalizableMenuItem;
/*     */ import com.dukascopy.dds2.greed.gui.l10n.components.JLocalizableRoundedBorder;
/*     */ import com.dukascopy.dds2.greed.gui.settings.ClientSettingsStorage;
/*     */ import com.dukascopy.dds2.greed.gui.settings.IChartTemplateSettingsStorage;
/*     */ import com.dukascopy.dds2.greed.gui.util.lotamount.LotAmountChanger;
/*     */ import com.dukascopy.dds2.greed.model.MarketView;
/*     */ import com.dukascopy.dds2.greed.util.ParentWatcher;
/*     */ import com.dukascopy.dds2.greed.util.PlatformSpecific;
/*     */ import com.dukascopy.transport.common.model.type.Money;
/*     */ import com.dukascopy.transport.common.model.type.OfferSide;
/*     */ import com.dukascopy.transport.common.model.type.OrderSide;
/*     */ import com.dukascopy.transport.common.msg.request.CurrencyOffer;
/*     */ import com.dukascopy.transport.common.msg.response.InstrumentStatusUpdateMessage;
/*     */ import java.awt.Color;
/*     */ import java.awt.Cursor;
/*     */ import java.awt.Dimension;
/*     */ import java.awt.Font;
/*     */ import java.awt.Point;
/*     */ import java.awt.SystemColor;
/*     */ import java.awt.event.ActionEvent;
/*     */ import java.awt.event.ActionListener;
/*     */ import java.awt.event.MouseAdapter;
/*     */ import java.awt.event.MouseEvent;
/*     */ import java.math.BigDecimal;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Comparator;
/*     */ import java.util.Currency;
/*     */ import java.util.HashSet;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import java.util.TreeMap;
/*     */ import javax.swing.AbstractAction;
/*     */ import javax.swing.Box;
/*     */ import javax.swing.BoxLayout;
/*     */ import javax.swing.JFrame;
/*     */ import javax.swing.JMenu;
/*     */ import javax.swing.JMenuItem;
/*     */ import javax.swing.JPanel;
/*     */ import javax.swing.JPopupMenu;
/*     */ import javax.swing.JPopupMenu.Separator;
/*     */ import javax.swing.JScrollPane;
/*     */ import javax.swing.JSeparator;
/*     */ import javax.swing.JViewport;
/*     */ import javax.swing.ListSelectionModel;
/*     */ import javax.swing.RowSorter;
/*     */ import javax.swing.SwingUtilities;
/*     */ import javax.swing.event.MenuEvent;
/*     */ import javax.swing.event.MenuListener;
/*     */ import javax.swing.table.JTableHeader;
/*     */ import javax.swing.table.TableRowSorter;
/*     */ import org.slf4j.Logger;
/*     */ import org.slf4j.LoggerFactory;
/*     */ 
/*     */ public class TickerPanel extends WorkspacePanel
/*     */   implements ParentWatcher, PlatformSpecific, MultiSplitable
/*     */ {
/*  99 */   private static final Logger LOGGER = LoggerFactory.getLogger(TickerPanel.class);
/*     */ 
/* 101 */   private MarketView marketView = null;
/*     */   private TickerTable tickerTable;
/* 103 */   private final TickerTableModel tickerModel = new TickerTableModel();
/* 104 */   private JPanel inner = new JPanel();
/*     */   private JScrollPane scroll;
/*     */   private static final int MIN_HEIGHT = 74;
/*     */   private static final int MAX_HEIGHT = 999;
/*     */   private static final int PANEL_OFFSET = 75;
/* 111 */   private int activeY = 25;
/* 112 */   private int activeX = 10;
/* 113 */   private int startY = 0;
/*     */   public static final int INSTRUMENT_COLUMN = 0;
/*     */   public static final int BID_COLUMN = 1;
/*     */   public static final int ASK_COLUMN = 2;
/* 119 */   private static final Color COLOR_TRADING_ALLOWED = Color.WHITE;
/* 120 */   private static final Color COLOR_TRADING_TEMPORARY_BLOCKED = Color.LIGHT_GRAY;
/* 121 */   private static final Color COLOR_TRADING_RESTRICED = new Color(250, 190, 180);
/* 122 */   private static final Color COLOR_WHITE = new Color(200, 200, 200);
/* 123 */   private static final Color COLOR_GREEN = new Color(0, 180, 0);
/* 124 */   private static final Color COLOR_RED = new Color(200, 0, 0);
/*     */   private JPopupMenu tickerPopupMenu;
/*     */   private JLocalizableMenuItem showOnChartItem;
/*     */   private JLocalizableMenuItem openChartTemplateItem;
/*     */   private JLocalizableMenuItem showOrderPanelItem;
/*     */   private JLocalizableMenuItem showMoItem;
/*     */   private JLocalizableMenuItem closeInstrumentItem;
/*     */   private JLocalizableMenuItem addAllCurrencysItem;
/*     */   private JLocalizableMenuItem removeAllCurrencysItem;
/*     */   private JLocalizableMenuItem addAllToMarketItem;
/*     */   private JLocalizableMenuItem editCurrencyListItem;
/*     */   private JLocalizableMenu currencyMenu;
/* 138 */   private JSeparator menuSeparator = new JPopupMenu.Separator();
/*     */ 
/* 140 */   private Set<String> orderEntryDetachedOpened = new HashSet();
/* 141 */   private boolean isDuplicateFrameOpeningAllowed = false;
/*     */   private InstrumentSelectorDialog instrumentSelectorDialog;
/*     */   private boolean instrumentsLoaded;
/*     */   private JLocalizableRoundedBorder myBorder;
/*     */ 
/*     */   public TickerPanel(DealPanel dealPanel)
/*     */   {
/* 148 */     super(dealPanel);
/* 149 */     build();
/*     */   }
/*     */ 
/*     */   private void build() {
/* 153 */     this.inner.setLayout(new BoxLayout(this.inner, 1));
/*     */ 
/* 155 */     this.tickerTable = new TickerTable(this.tickerModel);
/* 156 */     this.tickerTable.setSelectionBackground(GreedContext.SELECTION_COLOR);
/* 157 */     this.tickerTable.getSelectionModel().setSelectionMode(2);
/*     */ 
/* 159 */     this.marketView = ((MarketView)GreedContext.get("marketView"));
/*     */ 
/* 161 */     this.myBorder = new JLocalizableRoundedBorder(this.inner, "header.cerrency.selector", true);
/*     */ 
/* 163 */     this.myBorder.setTopBorder(8);
/* 164 */     this.myBorder.setTopInset(14);
/* 165 */     this.myBorder.setRightInset(9);
/* 166 */     this.myBorder.setLeftInset(11);
/* 167 */     this.myBorder.setBottomInset(7);
/*     */ 
/* 169 */     setLayout(new BoxLayout(this, 1));
/* 170 */     this.inner.setBorder(this.myBorder);
/*     */ 
/* 172 */     TickerTableMouseAdapter mouseAdapter = new TickerTableMouseAdapter(this);
/*     */ 
/* 174 */     this.tickerTable.addMouseListener(mouseAdapter);
/* 175 */     this.inner.addMouseListener(mouseAdapter);
/* 176 */     this.tickerTable.getTableHeader().setReorderingAllowed(false);
/* 177 */     this.tickerTable.getTableHeader().setResizingAllowed(false);
/* 178 */     this.tickerTable.setDragEnabled(true);
/*     */ 
/* 180 */     this.scroll = new JScrollPane(this.tickerTable, 22, 31);
/*     */ 
/* 182 */     this.scroll.getViewport().setBackground(SystemColor.window);
/* 183 */     this.scroll.getViewport().setBackground(GreedContext.GLOBAL_BACKGROUND);
/* 184 */     this.scroll.setVerticalScrollBarPolicy(20);
/* 185 */     this.scroll.setCorner("UPPER_RIGHT_CORNER", new ScrollPaneHeaderRenderer());
/* 186 */     this.scroll.setPreferredSize(new Dimension(getPreferredSize().width, getPrefHeight() - 28));
/* 187 */     this.tickerTable.setBackground(GreedContext.GLOBAL_BACKGROUND);
/*     */ 
/* 189 */     this.myBorder.setSwitch(this.scroll.isVisible());
/*     */ 
/* 191 */     this.inner.add(Box.createRigidArea(new Dimension(250, 10)));
/* 192 */     this.inner.add(this.scroll);
/* 193 */     add(this.inner);
/*     */ 
/* 195 */     addTableContextMenu();
/* 196 */     addColumnsComparators();
/*     */   }
/*     */ 
/*     */   private boolean isSelectedNotTradInstr() {
/* 200 */     for (String instr : getSelectedInstruments()) {
/* 201 */       if (!InstrumentAvailabilityManager.getInstance().isAllowed(instr)) {
/* 202 */         return true;
/*     */       }
/*     */     }
/* 205 */     return false;
/*     */   }
/*     */ 
/*     */   private void configurePopupMenu()
/*     */   {
/* 210 */     boolean isRowsSelected = getSelectedInstruments().length > 0;
/* 211 */     boolean hasNotTradInstr = !isSelectedNotTradInstr();
/*     */ 
/* 213 */     this.showOrderPanelItem.setEnabled(hasNotTradInstr);
/* 214 */     this.showOnChartItem.setEnabled(hasNotTradInstr);
/* 215 */     this.openChartTemplateItem.setEnabled(hasNotTradInstr);
/* 216 */     this.editCurrencyListItem.setEnabled(hasNotTradInstr);
/* 217 */     this.closeInstrumentItem.setEnabled(hasNotTradInstr);
/*     */ 
/* 219 */     this.showOrderPanelItem.setVisible((!GreedContext.isReadOnly()) && (isRowsSelected));
/* 220 */     this.showMoItem.setVisible(isRowsSelected);
/* 221 */     this.showOnChartItem.setVisible(isRowsSelected);
/* 222 */     this.openChartTemplateItem.setVisible((!GreedContext.isReadOnly()) && (isRowsSelected));
/* 223 */     this.menuSeparator.setVisible((!GreedContext.isReadOnly()) && (isRowsSelected));
/* 224 */     this.editCurrencyListItem.setVisible((!GreedContext.isReadOnly()) && (!GreedContext.isStrategyAllowed()));
/* 225 */     this.currencyMenu.setVisible((!GreedContext.isReadOnly()) && (GreedContext.isStrategyAllowed()));
/* 226 */     this.closeInstrumentItem.setVisible((!GreedContext.isReadOnly()) && (isRowsSelected) && (GreedContext.isStrategyAllowed()));
/* 227 */     getRemoveAllCurrencysItem().setVisible((!GreedContext.isReadOnly()) && (GreedContext.isStrategyAllowed()));
/*     */   }
/*     */ 
/*     */   public void openInstrumentSelectionDialog(TickerPanel panel)
/*     */   {
/* 234 */     if (this.instrumentSelectorDialog == null)
/* 235 */       this.instrumentSelectorDialog = new InstrumentSelectorDialog(panel);
/*     */     else {
/* 237 */       this.instrumentSelectorDialog.initModels();
/*     */     }
/*     */ 
/* 240 */     this.instrumentSelectorDialog.setSize(InstrumentSelectorDialog.preferredSize);
/* 241 */     this.instrumentSelectorDialog.setLocationRelativeTo((JFrame)GreedContext.get("clientGui"));
/* 242 */     this.instrumentSelectorDialog.setVisible(true);
/*     */   }
/*     */ 
/*     */   private void openChartTemplate(String[] selectedInstruments) {
/* 246 */     if ((selectedInstruments != null) && (selectedInstruments.length > 0)) {
/* 247 */       IChartTemplateSettingsStorage chartTemplateSettingsStorage = (IChartTemplateSettingsStorage)GreedContext.get("chartTemplateSettingsStorage");
/* 248 */       ChartBean mainChartBean = chartTemplateSettingsStorage.openChartTemplate();
/*     */ 
/* 250 */       for (int i = 0; i < selectedInstruments.length; i++) {
/* 251 */         String instrument = selectedInstruments[i];
/* 252 */         if (instrument == null) {
/*     */           continue;
/*     */         }
/* 255 */         ChartBean chartBeanForOpening = chartTemplateSettingsStorage.cloneChartBean(mainChartBean);
/*     */ 
/* 257 */         if (chartBeanForOpening == null)
/*     */         {
/*     */           continue;
/*     */         }
/* 261 */         chartBeanForOpening.setInstrument(Instrument.fromString(instrument));
/* 262 */         chartBeanForOpening.setId(IdManager.getInstance().getNextChartId());
/*     */ 
/* 264 */         ChartsFrame chartsFrame = ChartsFrame.getInstance();
/* 265 */         chartsFrame.addChart(chartBeanForOpening);
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public void clearSelection() {
/* 271 */     this.tickerTable.getSelectionModel().clearSelection();
/*     */   }
/*     */ 
/*     */   public void update(String instrument, OfferSide side, BigDecimal value) {
/* 275 */     if (!this.instrumentsLoaded)
/*     */     {
/* 277 */       ClientSettingsStorage clientSettingsStorage = (ClientSettingsStorage)GreedContext.get("settingsStorage");
/* 278 */       Set bogusSet = new HashSet();
/*     */ 
/* 280 */       for (String name : clientSettingsStorage.restoreSelectedInstruments()) {
/* 281 */         if ((name != null) && (name.indexOf(47) > 0))
/* 282 */           this.tickerModel.addInstrument(name);
/*     */         else {
/* 284 */           bogusSet.add(name);
/*     */         }
/*     */       }
/* 287 */       List currentInstruments = clientSettingsStorage.restoreSelectedInstruments();
/* 288 */       if ((bogusSet != null) && (bogusSet.size() > 0)) {
/* 289 */         currentInstruments.removeAll(new ArrayList(bogusSet));
/* 290 */         clientSettingsStorage.saveSelectedInstruments(new HashSet(currentInstruments));
/*     */       }
/* 292 */       this.instrumentsLoaded = true;
/*     */ 
/* 295 */       OrderEntryPanel oep = this.dealPanel.getOrderEntryPanel();
/*     */ 
/* 299 */       String defaultInstrument = ((ClientSettingsStorage)GreedContext.get("settingsStorage")).restoreLastSelectedInstrument();
/* 300 */       oep.setInstrument(defaultInstrument);
/*     */ 
/* 304 */       oep.setSubmitEnabled(0);
/* 305 */       oep.setOrderSide(OrderSide.BUY);
/* 306 */       oep.setDefaultStopConditionLabels();
/*     */ 
/* 308 */       if (null != this.marketView.getLastMarketState(defaultInstrument)) {
/* 309 */         oep.onMarketState(this.marketView.getLastMarketState(defaultInstrument));
/*     */       }
/*     */ 
/* 313 */       int i = 0; for (int n = this.tickerModel.getRowCount(); i < n; i++) {
/* 314 */         adjustRowColor(i, OfferSide.ASK, null);
/*     */       }
/*     */ 
/* 317 */       this.tickerModel.fireTableDataChanged();
/*     */     }
/*     */ 
/* 320 */     int row = this.tickerModel.getInstrumentIndex(instrument);
/* 321 */     if (-1 == row) {
/* 322 */       return;
/*     */     }
/*     */ 
/* 325 */     adjustRowColor(row, side, value);
/* 326 */     this.tickerModel.updateInstrument(instrument, side, value);
/* 327 */     this.tickerModel.fireTableRowsUpdated(row, row);
/* 328 */     ((TableRowSorter)this.tickerTable.getRowSorter()).sort();
/*     */   }
/*     */ 
/*     */   public void clear(boolean everything)
/*     */   {
/* 334 */     this.tickerTable.getSelectionModel().setSelectionInterval(1, 0);
/* 335 */     if (!everything)
/* 336 */       return;
/* 337 */     TickerTableModel model = (TickerTableModel)this.tickerTable.getModel();
/* 338 */     model.clearInstruments();
/* 339 */     model.fireTableDataChanged();
/*     */   }
/*     */ 
/*     */   public void updateTradability(InstrumentStatusUpdateMessage status)
/*     */   {
/* 348 */     String instrument = status.getInstrument();
/* 349 */     Integer row = Integer.valueOf(this.tickerModel.getInstrumentIndex(instrument));
/* 350 */     if (null == row) {
/* 351 */       return;
/*     */     }
/* 353 */     InstrumentStatusUpdateMessage state = this.marketView.getInstrumentState(instrument);
/* 354 */     Color bgColor = COLOR_TRADING_ALLOWED;
/*     */ 
/* 356 */     if ((state == null) || (state.getTradable() == 1)) {
/* 357 */       this.tickerTable.setRowColor(row.intValue(), COLOR_WHITE);
/* 358 */       bgColor = COLOR_TRADING_TEMPORARY_BLOCKED;
/*     */     }
/* 360 */     if ((state != null) && (state.getTradable() == 2)) {
/* 361 */       this.tickerTable.setRowColor(row.intValue(), Color.BLACK);
/* 362 */       bgColor = COLOR_TRADING_RESTRICED;
/*     */     }
/* 364 */     this.tickerTable.setRowBgColor(row.intValue(), bgColor);
/* 365 */     this.tickerTable.repaint();
/*     */   }
/*     */ 
/*     */   private void adjustRowColor(int row, OfferSide side, BigDecimal value)
/*     */   {
/* 380 */     int column = side == OfferSide.ASK ? 2 : 1;
/*     */ 
/* 382 */     BigDecimal currentValue = this.tickerModel.getPriceAt(row, column);
/*     */ 
/* 384 */     String instrument = this.tickerModel.getInstrumentName(row);
/* 385 */     InstrumentStatusUpdateMessage state = this.marketView.getInstrumentState(instrument);
/*     */ 
/* 387 */     Color bgColor = COLOR_TRADING_ALLOWED;
/* 388 */     if ((state == null) || (state.getTradable() == 1)) {
/* 389 */       this.tickerTable.setRowColor(row, COLOR_WHITE);
/* 390 */       bgColor = COLOR_TRADING_TEMPORARY_BLOCKED;
/*     */     }
/* 392 */     if ((state != null) && (state.getTradable() == 2)) {
/* 393 */       this.tickerTable.setRowColor(row, Color.BLACK);
/* 394 */       bgColor = COLOR_TRADING_RESTRICED;
/*     */     }
/* 396 */     else if ((value != null) && (currentValue != null)) {
/* 397 */       if (value.floatValue() > currentValue.floatValue())
/* 398 */         this.tickerTable.setRowColor(row, COLOR_GREEN);
/* 399 */       else if (value.floatValue() < currentValue.floatValue())
/* 400 */         this.tickerTable.setRowColor(row, COLOR_RED);
/*     */       else
/* 402 */         this.tickerTable.setRowColor(row, this.tickerTable.getDefaultColor());
/*     */     }
/*     */     else {
/* 405 */       this.tickerTable.setRowColor(row, this.tickerTable.getDefaultColor());
/*     */     }
/*     */ 
/* 408 */     this.tickerTable.setRowBgColor(row, bgColor);
/*     */   }
/*     */ 
/*     */   public String getSelectedInstrument(MouseEvent e) {
/* 412 */     int row = this.tickerTable.getMouseSelectedRowIndexFromModel(e);
/* 413 */     return getSelectedInstrByRowIndex(row);
/*     */   }
/*     */ 
/*     */   public String getSelectedInstrument() {
/* 417 */     int row = this.tickerTable.getRowIndexFromModel();
/* 418 */     return getSelectedInstrByRowIndex(row);
/*     */   }
/*     */ 
/*     */   public String getSelectedInstrByRowIndex(int row) {
/* 422 */     if (row != -1) {
/* 423 */       String result = this.tickerModel.getInstrumentName(row);
/* 424 */       if (null == result) {
/* 425 */         result = this.tickerModel.getInstrumentName(0);
/*     */       }
/* 427 */       return result;
/*     */     }
/* 429 */     if ((row != -1) && (row < this.tickerModel.getRowCount())) {
/* 430 */       return this.tickerModel.getInstrumentName(row);
/*     */     }
/* 432 */     return this.dealPanel.getOrderEntryPanel().getInstrument();
/*     */   }
/*     */ 
/*     */   public String[] getSelectedInstruments()
/*     */   {
/* 438 */     int[] selectedRows = getSelectedRows();
/* 439 */     String[] result = new String[selectedRows.length];
/*     */ 
/* 441 */     for (int i = 0; i < selectedRows.length; i++) {
/* 442 */       result[i] = getSelectedInstrByRowIndex(selectedRows[i]);
/*     */     }
/*     */ 
/* 445 */     return result;
/*     */   }
/*     */ 
/*     */   public TickerTable getTickerTable() {
/* 449 */     return this.tickerTable;
/*     */   }
/*     */ 
/*     */   public void setIsDuplicateFrameOpeningAllowed(boolean isDuplicateFrameOpeningAllowed) {
/* 453 */     this.isDuplicateFrameOpeningAllowed = isDuplicateFrameOpeningAllowed;
/*     */   }
/*     */ 
/*     */   public boolean getIsDuplicateFrameOpeningAllowed() {
/* 457 */     return this.isDuplicateFrameOpeningAllowed;
/*     */   }
/*     */ 
/*     */   public void onNestedFrameClose(JFrame frame) {
/* 461 */     if (this.isDuplicateFrameOpeningAllowed)
/* 462 */       return;
/* 463 */     if (frame == null)
/* 464 */       return;
/* 465 */     if ((frame instanceof OrderEntryDetached)) {
/* 466 */       String instrument = ((OrderEntryDetached)frame).getInstrument();
/* 467 */       this.orderEntryDetachedOpened.remove(instrument);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void setInstruments(List<String> instruments) {
/* 472 */     this.tickerModel.clearInstruments();
/* 473 */     for (String name : instruments) {
/* 474 */       this.tickerModel.addInstrument(name);
/*     */     }
/*     */ 
/* 477 */     BigDecimal value = null;
/*     */ 
/* 480 */     int i = 0; for (int n = this.tickerModel.getRowCount(); i < n; i++) {
/* 481 */       String instrument = this.tickerModel.getInstrumentName(i);
/*     */ 
/* 483 */       CurrencyOffer bestOffer = this.marketView.getBestOffer(instrument, OfferSide.BID);
/*     */       try
/*     */       {
/* 486 */         if ((bestOffer != null) && (bestOffer.getPrice() != null)) {
/* 487 */           value = bestOffer.getPrice().getValue().stripTrailingZeros();
/* 488 */           this.tickerModel.updateInstrument(instrument, OfferSide.BID, value);
/*     */         }
/*     */       } catch (NullPointerException e) {
/* 491 */         LOGGER.error(e.getMessage(), e);
/* 492 */         break;
/*     */       }
/*     */ 
/* 495 */       bestOffer = this.marketView.getBestOffer(instrument, OfferSide.ASK);
/*     */       try
/*     */       {
/* 498 */         if ((bestOffer != null) && (bestOffer.getPrice() != null)) {
/* 499 */           value = bestOffer.getPrice().getValue().stripTrailingZeros();
/* 500 */           this.tickerModel.updateInstrument(instrument, OfferSide.ASK, value);
/*     */         }
/*     */       } catch (NullPointerException e) {
/* 503 */         LOGGER.error(e.getMessage(), e);
/* 504 */         break;
/*     */       }
/*     */ 
/* 508 */       if (value != null) {
/* 509 */         adjustRowColor(i, OfferSide.ASK, value);
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 515 */     this.tickerTable.getSelectionModel().setSelectionInterval(this.tickerTable.getSelectedRow(), this.tickerTable.getSelectedRow());
/* 516 */     OrderEntryPanel oep = this.dealPanel.getOrderEntryPanel();
/* 517 */     oep.clearEverything(false);
/* 518 */     String defaultInstriment = getSelectedInstrument();
/* 519 */     oep.setInstrument(defaultInstriment);
/* 520 */     InstrumentStatusUpdateMessage instrumentState = this.marketView.getInstrumentState(defaultInstriment);
/* 521 */     oep.setSubmitEnabled(null == instrumentState ? 1 : instrumentState.getTradable());
/* 522 */     oep.setOrderSide(OrderSide.BUY);
/* 523 */     oep.setDefaultStopConditionLabels();
/*     */ 
/* 526 */     if (this.marketView.getLastMarketState(defaultInstriment) != null) {
/* 527 */       oep.onMarketState(this.marketView.getLastMarketState(defaultInstriment));
/*     */     }
/*     */ 
/* 530 */     this.tickerModel.fireTableDataChanged();
/* 531 */     this.tickerModel.fireTableRowsUpdated(0, this.tickerModel.getRowCount() - 1);
/* 532 */     ((TableRowSorter)this.tickerTable.getRowSorter()).sort();
/*     */   }
/*     */ 
/*     */   public List<String> getInstruments() {
/* 536 */     return this.tickerModel.getInstrumentList();
/*     */   }
/*     */ 
/*     */   public boolean isExpanded() {
/* 540 */     return this.scroll.isVisible();
/*     */   }
/*     */ 
/*     */   public void switchVisibility() {
/* 544 */     this.scroll.setVisible(!this.scroll.isVisible());
/* 545 */     this.myBorder.setSwitch(this.scroll.isVisible());
/*     */ 
/* 547 */     if ((getParent() instanceof MultiSplitPane)) {
/* 548 */       ((MultiSplitPane)getParent()).switchVisibility("ticker");
/*     */     }
/*     */ 
/* 551 */     this.inner.repaint();
/* 552 */     this.inner.revalidate();
/*     */   }
/*     */ 
/*     */   private int[] getSelectedRows() {
/* 556 */     int[] res = this.tickerTable.getSelectedRows();
/*     */ 
/* 558 */     for (int i = 0; i < res.length; i++) {
/* 559 */       res[i] = this.tickerTable.getRowSorter().convertRowIndexToModel(res[i]);
/*     */     }
/*     */ 
/* 562 */     return res;
/*     */   }
/*     */ 
/*     */   private void showOrdersPanel(String instrument) {
/* 566 */     if (!this.isDuplicateFrameOpeningAllowed) {
/* 567 */       if (!this.orderEntryDetachedOpened.contains(instrument)) {
/* 568 */         this.orderEntryDetachedOpened.add(instrument);
/* 569 */         OrderEntryDetached detachedPanel = new OrderEntryDetached(instrument);
/* 570 */         detachedPanel.setParentWatcher(this);
/* 571 */         detachedPanel.onMarketState(this.marketView.getLastMarketState(instrument));
/* 572 */         detachedPanel.display();
/*     */       }
/*     */     } else {
/* 575 */       OrderEntryDetached detachedPanel = new OrderEntryDetached(instrument);
/* 576 */       detachedPanel.onMarketState(this.marketView.getLastMarketState(instrument));
/* 577 */       detachedPanel.display();
/*     */     }
/*     */   }
/*     */ 
/*     */   private void addTableContextMenu() {
/* 582 */     this.tickerPopupMenu = new JPopupMenu();
/*     */ 
/* 584 */     if (this.showOnChartItem == null) {
/* 585 */       this.showOnChartItem = new JLocalizableMenuItem("item.show.on.chert");
/* 586 */       this.showOnChartItem.setAction(new AbstractAction("item.show.on.chert") {
/*     */         public void actionPerformed(ActionEvent e) {
/* 588 */           String[] selectedInstruments = TickerPanel.this.getSelectedInstruments();
/* 589 */           for (int i = 0; i < selectedInstruments.length; i++) {
/* 590 */             ((ClientFormLayoutManager)GreedContext.get("layoutManager")).getWorkspaceHelper().showChart(Instrument.fromString(selectedInstruments[i]));
/*     */           }
/*     */         }
/*     */       });
/*     */     }
/*     */ 
/* 597 */     if (this.openChartTemplateItem == null) {
/* 598 */       this.openChartTemplateItem = new JLocalizableMenuItem();
/* 599 */       this.openChartTemplateItem.setAction(new AbstractAction("item.open.chart.template") {
/*     */         public void actionPerformed(ActionEvent e) {
/* 601 */           String[] selectedInstruments = TickerPanel.this.getSelectedInstruments();
/* 602 */           if (GreedContext.isStrategyAllowed()) {
/* 603 */             IChartTemplateSettingsStorage chartTemplateSettingsStorage = (IChartTemplateSettingsStorage)GreedContext.get("chartTemplateSettingsStorage");
/*     */ 
/* 606 */             JForexClientFormLayoutManager jfLayoutManager = (JForexClientFormLayoutManager)GreedContext.get("layoutManager");
/*     */ 
/* 609 */             ChartBean chartBean = chartTemplateSettingsStorage.openChartTemplate();
/* 610 */             if (chartBean != null) {
/* 611 */               jfLayoutManager.getTreeActionFactory().createAction(TreeActionType.OPEN_CHART_TEMPLATE, jfLayoutManager.getWorkspaceJTree()).execute(new Object[] { jfLayoutManager.getWorkspaceJTree().getWorkspaceRoot(), Instrument.fromString(TickerPanel.this.getSelectedInstrument()), chartBean });
/*     */             }
/*     */ 
/*     */           }
/*     */           else
/*     */           {
/* 617 */             TickerPanel.this.openChartTemplate(selectedInstruments);
/*     */           }
/*     */         }
/*     */       });
/*     */     }
/* 623 */     if (this.openChartTemplateItem == null) {
/* 624 */       this.openChartTemplateItem = new JLocalizableMenuItem();
/* 625 */       this.openChartTemplateItem.setAction(new AbstractAction("open.template.popup.menu.item")
/*     */       {
/*     */         public void actionPerformed(ActionEvent e) {
/* 628 */           String[] selectedInstruments = TickerPanel.this.getSelectedInstruments();
/* 629 */           if (GreedContext.isStrategyAllowed()) {
/* 630 */             IChartTemplateSettingsStorage chartTemplateSettingsStorage = (IChartTemplateSettingsStorage)GreedContext.get("chartTemplateSettingsStorage");
/*     */ 
/* 633 */             JForexClientFormLayoutManager jfLayoutManager = (JForexClientFormLayoutManager)GreedContext.get("layoutManager");
/*     */ 
/* 636 */             ChartBean chartBean = chartTemplateSettingsStorage.openChartTemplate();
/* 637 */             if (chartBean != null) {
/* 638 */               jfLayoutManager.getTreeActionFactory().createAction(TreeActionType.OPEN_CHART_TEMPLATE, jfLayoutManager.getWorkspaceJTree()).execute(new Object[] { jfLayoutManager.getWorkspaceJTree().getWorkspaceRoot(), Instrument.fromString(TickerPanel.this.getSelectedInstrument()), chartBean });
/*     */             }
/*     */ 
/*     */           }
/*     */           else
/*     */           {
/* 644 */             TickerPanel.this.openChartTemplate(selectedInstruments);
/*     */           }
/*     */         }
/*     */       });
/*     */     }
/* 650 */     if (this.showOrderPanelItem == null) {
/* 651 */       this.showOrderPanelItem = new JLocalizableMenuItem();
/* 652 */       this.showOrderPanelItem.setAction(new AbstractAction("item.show.orders.panel") {
/*     */         public void actionPerformed(ActionEvent e) {
/* 654 */           String[] instruments = TickerPanel.this.getSelectedInstruments();
/* 655 */           for (int i = 0; i < instruments.length; i++) {
/* 656 */             TickerPanel.this.showOrdersPanel(instruments[i]);
/*     */           }
/*     */         }
/*     */ 
/*     */       });
/*     */     }
/*     */ 
/* 664 */     if (this.showMoItem == null) {
/* 665 */       this.showMoItem = new JLocalizableMenuItem();
/* 666 */       this.showMoItem.setAction(new AbstractAction("item.show.market.overview") {
/*     */         public void actionPerformed(ActionEvent e) {
/* 668 */           ((MarketOverviewFrame)GreedContext.get("Dock")).addMiniPanelByInstruments(TickerPanel.this.getSelectedInstruments());
/*     */         }
/*     */       });
/*     */     }
/*     */ 
/* 674 */     if (this.closeInstrumentItem == null) {
/* 675 */       this.closeInstrumentItem = new JLocalizableMenuItem("item.close.currency");
/* 676 */       this.closeInstrumentItem.addActionListener(new CloseSlectedItemsAction());
/* 677 */       this.closeInstrumentItem.setActionCommand("item.close.currency");
/*     */     }
/*     */ 
/* 680 */     if (this.editCurrencyListItem == null) {
/* 681 */       this.editCurrencyListItem = new JLocalizableMenuItem();
/* 682 */       this.editCurrencyListItem.setAction(new AbstractAction("item.edit.currencies") {
/*     */         public void actionPerformed(ActionEvent e) {
/* 684 */           TickerPanel.this.openInstrumentSelectionDialog(TickerPanel.this);
/*     */         }
/*     */       });
/*     */     }
/* 689 */     if (this.addAllToMarketItem == null) {
/* 690 */       this.addAllToMarketItem = new JLocalizableMenuItem("item.add.all.to.market");
/* 691 */       this.addAllToMarketItem.setAction(new AbstractAction("item.add.all.to.market") {
/*     */         public void actionPerformed(ActionEvent e) {
/* 693 */           List instruments = TickerPanel.this.tickerModel.getInstrumentList();
/* 694 */           ((MarketOverviewFrame)GreedContext.get("Dock")).addMiniPanelByInstruments((String[])instruments.toArray(new String[instruments.size()]));
/*     */         }
/*     */       });
/*     */     }
/*     */ 
/* 700 */     this.currencyMenu = getCurrencyMenuItem();
/*     */ 
/* 702 */     this.tickerPopupMenu.add(this.showOnChartItem);
/* 703 */     this.tickerPopupMenu.add(this.openChartTemplateItem);
/* 704 */     this.tickerPopupMenu.add(this.menuSeparator);
/*     */ 
/* 706 */     this.tickerPopupMenu.add(this.currencyMenu);
/* 707 */     this.tickerPopupMenu.add(this.editCurrencyListItem);
/* 708 */     this.tickerPopupMenu.add(this.closeInstrumentItem);
/* 709 */     this.tickerPopupMenu.add(getAddAllCurrencysItem());
/* 710 */     this.tickerPopupMenu.add(getRemoveAllCurrencysItem());
/* 711 */     this.tickerPopupMenu.addSeparator();
/*     */ 
/* 713 */     this.tickerPopupMenu.add(this.showOrderPanelItem);
/* 714 */     this.tickerPopupMenu.add(this.showMoItem);
/* 715 */     this.tickerPopupMenu.add(this.addAllToMarketItem);
/*     */   }
/*     */ 
/*     */   private JLocalizableMenuItem getAddAllCurrencysItem()
/*     */   {
/* 720 */     if (this.addAllCurrencysItem == null) {
/* 721 */       this.addAllCurrencysItem = new JLocalizableMenuItem("item.add.currency.all");
/* 722 */       this.addAllCurrencysItem.addActionListener(new ActionListener()
/*     */       {
/*     */         public void actionPerformed(ActionEvent e) {
/* 725 */           IWorkspaceHelper workspaceHellper = ((JForexClientFormLayoutManager)GreedContext.get("layoutManager")).getWorkspaceHelper();
/* 726 */           workspaceHellper.addDependantCurrenciesAndSubscribe(workspaceHellper.getUnsubscribedInstruments());
/*     */         } } );
/*     */     }
/* 730 */     return this.addAllCurrencysItem;
/*     */   }
/*     */ 
/*     */   private JLocalizableMenuItem getRemoveAllCurrencysItem() {
/* 734 */     if (this.removeAllCurrencysItem == null) {
/* 735 */       this.removeAllCurrencysItem = new JLocalizableMenuItem("item.remove.currency.all");
/* 736 */       this.removeAllCurrencysItem.addActionListener(new CloseSlectedItemsAction());
/* 737 */       this.removeAllCurrencysItem.setActionCommand("item.remove.currency.all");
/*     */     }
/* 739 */     return this.removeAllCurrencysItem;
/*     */   }
/*     */ 
/*     */   private JLocalizableMenu getCurrencyMenuItem() {
/* 743 */     JLocalizableMenu currenciesMenu = new JLocalizableMenu("item.add.currency");
/* 744 */     currenciesMenu.addMenuListener(new MenuListener(currenciesMenu) {
/*     */       public void menuCanceled(MenuEvent e) {
/*     */       }
/*     */ 
/*     */       public void menuDeselected(MenuEvent e) {
/* 749 */         this.val$currenciesMenu.removeAll();
/*     */       }
/*     */ 
/*     */       public void menuSelected(MenuEvent e)
/*     */       {
/* 754 */         IWorkspaceHelper workspaceHellper = ((ClientFormLayoutManager)GreedContext.get("layoutManager")).getWorkspaceHelper();
/* 755 */         Instrument[] instruments = workspaceHellper.getAvailableInstrumentsAsArray();
/*     */ 
/* 757 */         Map currencysSubmenu = new TreeMap();
/*     */ 
/* 759 */         for (int i = 0; i < instruments.length; i++) {
/* 760 */           if (!workspaceHellper.isInstrumentSubscribed(instruments[i])) {
/* 761 */             String firstCurrency = instruments[i].getPrimaryCurrency().toString();
/*     */ 
/* 763 */             if (currencysSubmenu.containsKey(firstCurrency)) {
/* 764 */               ((List)currencysSubmenu.get(firstCurrency)).add(instruments[i]);
/*     */             } else {
/* 766 */               List currSubcol = new ArrayList();
/* 767 */               currSubcol.add(instruments[i]);
/* 768 */               currencysSubmenu.put(firstCurrency, currSubcol);
/*     */             }
/*     */           }
/*     */         }
/*     */ 
/* 773 */         for (String cur : currencysSubmenu.keySet()) {
/* 774 */           JMenu subMenu = new JMenu(cur);
/* 775 */           for (Instrument instrument : (List)currencysSubmenu.get(cur))
/*     */           {
/* 777 */             JMenuItem instrumentItem = new JMenuItem(instrument.toString());
/* 778 */             if (GreedContext.getMarketView().getInstrumentState(instrument.toString()).getTradable() == 1)
/*     */             {
/* 780 */               instrumentItem.setForeground(Color.GRAY);
/* 781 */               instrumentItem.setFont(new Font(instrumentItem.getFont().getName(), 2, instrumentItem.getFont().getSize()));
/*     */             }
/* 783 */             if (!workspaceHellper.isInstrumentSubscribed(instrument)) {
/* 784 */               if (LotAmountChanger.isCommodity(instrument))
/* 785 */                 this.val$currenciesMenu.add(instrumentItem);
/*     */               else {
/* 787 */                 subMenu.add(instrumentItem);
/*     */               }
/* 789 */               instrumentItem.addActionListener(new ActionListener(workspaceHellper)
/*     */               {
/*     */                 public void actionPerformed(ActionEvent e) {
/* 792 */                   Instrument selectedInstrument = Instrument.fromString(((JMenuItem)e.getSource()).getText());
/* 793 */                   this.val$workspaceHellper.addDependantCurrenciesAndSubscribe(selectedInstrument);
/*     */                 } } );
/*     */             }
/*     */           }
/* 798 */           if ((!((List)currencysSubmenu.get(cur)).contains(Instrument.XAGUSD)) && (!((List)currencysSubmenu.get(cur)).contains(Instrument.XAUUSD)))
/*     */           {
/* 800 */             this.val$currenciesMenu.add(subMenu);
/*     */           }
/*     */         }
/*     */ 
/* 804 */         if (this.val$currenciesMenu.getItemCount() < 1) {
/* 805 */           JMenuItem noInstrAvailable = new JMenuItem("<Empty List>");
/* 806 */           noInstrAvailable.setEnabled(false);
/* 807 */           this.val$currenciesMenu.add(noInstrAvailable);
/*     */         }
/*     */       }
/*     */     });
/* 812 */     return currenciesMenu;
/*     */   }
/*     */ 
/*     */   private void addColumnsComparators() {
/* 816 */     this.tickerTable.setFillsViewportHeight(true);
/* 817 */     this.tickerTable.setAutoCreateRowSorter(true);
/* 818 */     TableRowSorter sorter = new TableRowSorter((TickerTableModel)this.tickerTable.getModel());
/* 819 */     this.tickerTable.setRowSorter(sorter);
/*     */ 
/* 821 */     Comparator instrumentComparator = new Comparator()
/*     */     {
/*     */       public int compare(String o1, String o2) {
/* 824 */         return o1.compareTo(o2);
/*     */       }
/*     */     };
/* 828 */     Comparator bidAskComparator = new Comparator()
/*     */     {
/*     */       public int compare(String o1, String o2) {
/* 831 */         BigDecimal id1 = null;
/* 832 */         BigDecimal id2 = null;
/*     */         try
/*     */         {
/* 835 */           id1 = BigDecimal.valueOf(Double.valueOf(o1).doubleValue());
/*     */         } catch (Exception e) {
/* 837 */           return -1;
/*     */         }
/*     */         try
/*     */         {
/* 841 */           id2 = BigDecimal.valueOf(Double.valueOf(o2).doubleValue());
/*     */         } catch (Exception e) {
/* 843 */           return 1;
/*     */         }
/*     */ 
/* 846 */         return id1.compareTo(id2);
/*     */       }
/*     */     };
/* 850 */     sorter.setComparator(0, instrumentComparator);
/* 851 */     sorter.setComparator(1, bidAskComparator);
/* 852 */     sorter.setComparator(2, bidAskComparator);
/*     */ 
/* 855 */     List sortKeys = ((ClientSettingsStorage)GreedContext.get("settingsStorage")).restoreTableSortKeys(this.tickerTable.getTableId());
/* 856 */     sorter.setSortKeys(sortKeys);
/*     */   }
/*     */ 
/*     */   public int getPrefHeight()
/*     */   {
/* 974 */     int storedHeight = ((ClientSettingsStorage)GreedContext.get("settingsStorage")).restoreInstrumentsPanelHeight();
/* 975 */     return GreedContext.isPlatformFrameSmall() ? this.tickerTable.getRowHeight() * 3 + 75 : storedHeight != -1 ? storedHeight : this.tickerTable.getRowHeight() * 4 + 75;
/*     */   }
/*     */ 
/*     */   public int getMaxHeight()
/*     */   {
/* 984 */     return 999;
/*     */   }
/*     */ 
/*     */   public int getMinHeight()
/*     */   {
/* 989 */     if (isExpanded()) {
/* 990 */       return 74;
/*     */     }
/* 992 */     return 30;
/*     */   }
/*     */ 
/*     */   private class TickerTableMouseAdapter extends MouseAdapter
/*     */   {
/*     */     TickerPanel parent;
/*     */ 
/*     */     TickerTableMouseAdapter(TickerPanel parent)
/*     */     {
/* 864 */       this.parent = parent;
/*     */     }
/*     */ 
/*     */     public void mouseEntered(MouseEvent e) {
/* 868 */       if (showHand(e))
/* 869 */         TickerPanel.this.inner.setCursor(Cursor.getPredefinedCursor(12));
/*     */     }
/*     */ 
/*     */     public void mouseMoved(MouseEvent e)
/*     */     {
/* 874 */       if (showHand(e))
/* 875 */         TickerPanel.this.inner.setCursor(Cursor.getPredefinedCursor(12));
/*     */       else
/* 877 */         TickerPanel.this.inner.setCursor(Cursor.getDefaultCursor());
/*     */     }
/*     */ 
/*     */     public void mouseExited(MouseEvent e)
/*     */     {
/* 882 */       TickerPanel.this.inner.setCursor(Cursor.getDefaultCursor());
/*     */     }
/*     */ 
/*     */     public void mousePressed(MouseEvent e) {
/* 886 */       mayBeShowPopup(e);
/*     */     }
/*     */ 
/*     */     public void mouseReleased(MouseEvent e) {
/* 890 */       refreshDealPanel(e);
/* 891 */       mayBeShowPopup(e);
/*     */ 
/* 893 */       if ((showHand(e)) && ((e.getSource() instanceof JPanel)) && (!e.isPopupTrigger()))
/*     */       {
/* 896 */         TickerPanel.this.switchVisibility();
/*     */       }
/*     */     }
/*     */ 
/*     */     private void mayBeShowPopup(MouseEvent e) {
/* 901 */       if (SwingUtilities.isRightMouseButton(e)) {
/* 902 */         Point p = e.getPoint();
/* 903 */         int rowNumber = TickerPanel.this.tickerTable.rowAtPoint(p);
/* 904 */         ListSelectionModel model = TickerPanel.this.tickerTable.getSelectionModel();
/* 905 */         model.setSelectionInterval(rowNumber, rowNumber);
/*     */       }
/*     */ 
/* 909 */       String instrument = this.parent.getSelectedInstrument();
/* 910 */       InstrumentStatusUpdateMessage state = TickerPanel.this.marketView.getInstrumentState(instrument);
/* 911 */       int y = e.getY();
/* 912 */       if ((null != state) && (state.getTradable() == 0)) {
/* 913 */         TickerPanel.this.dealPanel.getOrderEntryPanel().clearEverything(false);
/*     */       }
/* 915 */       TickerPanel.this.configurePopupMenu();
/* 916 */       if ((e.isPopupTrigger()) && (this.parent.isExpanded())) {
/* 917 */         if ((e.getSource() instanceof JPanel)) {
/* 918 */           y = e.getY() - TickerPanel.this.activeY * 2;
/*     */         }
/* 920 */         TickerPanel.this.tickerPopupMenu.show(TickerPanel.this.tickerTable, e.getX(), y);
/*     */       }
/*     */     }
/*     */ 
/*     */     private boolean showHand(MouseEvent e) {
/* 925 */       return (e.getY() < TickerPanel.this.activeY) && (e.getY() > TickerPanel.this.startY) && (e.getX() > TickerPanel.this.activeX) && (e.getX() < TickerPanel.this.inner.getSize().width - TickerPanel.this.activeX);
/*     */     }
/*     */ 
/*     */     public void mouseClicked(MouseEvent e)
/*     */     {
/* 930 */       if ((this.parent == null) || (showHand(e))) return;
/*     */ 
/* 933 */       refreshDealPanel(e);
/*     */ 
/* 935 */       String instrument = this.parent.getSelectedInstrument();
/* 936 */       InstrumentStatusUpdateMessage state = TickerPanel.this.marketView.getInstrumentState(instrument);
/* 937 */       if ((null == state) || (state.getTradable() != 0)) {
/* 938 */         return;
/*     */       }
/*     */ 
/* 941 */       if (e.getButton() == 1)
/*     */       {
/* 943 */         if (e.getClickCount() == 2) {
/* 944 */           if ((!GreedContext.isGlobal()) && (!GreedContext.isContest()) && (!GreedContext.isGlobalExtended()))
/*     */           {
/* 947 */             TickerPanel.this.showOrdersPanel(instrument);
/* 948 */             return;
/*     */           }
/*     */         }
/*     */         else {
/* 952 */           OrderEntryPanel oep = TickerPanel.this.dealPanel.getOrderEntryPanel();
/* 953 */           switch (TickerPanel.this.tickerTable.columnAtPoint(e.getPoint())) {
/*     */           case 2:
/* 955 */             oep.setOrderSide(OrderSide.BUY);
/* 956 */             break;
/*     */           case 1:
/* 958 */             oep.setOrderSide(OrderSide.SELL);
/*     */           }
/*     */         }
/*     */       }
/*     */ 
/* 963 */       mayBeShowPopup(e);
/*     */     }
/*     */ 
/*     */     private void refreshDealPanel(MouseEvent e) {
/* 967 */       TickerPanel.this.tickerModel.fireTableRowsUpdated(TickerPanel.this.tickerTable.getRowIndexFromModel(), TickerPanel.this.tickerTable.getRowIndexFromModel());
/* 968 */       TickerPanel.this.dealPanel.refresh();
/*     */     }
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.ticker.TickerPanel
 * JD-Core Version:    0.6.0
 */