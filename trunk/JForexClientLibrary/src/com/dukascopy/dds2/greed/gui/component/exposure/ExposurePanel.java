/*     */ package com.dukascopy.dds2.greed.gui.component.exposure;
/*     */ 
/*     */ import com.dukascopy.dds2.greed.GreedContext;
/*     */ import com.dukascopy.dds2.greed.actions.MassOrderGroupCloseAction;
/*     */ import com.dukascopy.dds2.greed.actions.SignalAction;
/*     */ import com.dukascopy.dds2.greed.api.OrderFactory;
/*     */ import com.dukascopy.dds2.greed.api.OrderFactory.MergeValidationResultCode;
/*     */ import com.dukascopy.dds2.greed.gui.ClientForm;
/*     */ import com.dukascopy.dds2.greed.gui.GuiUtilsAndConstants;
/*     */ import com.dukascopy.dds2.greed.gui.component.HeaderPanel;
/*     */ import com.dukascopy.dds2.greed.gui.component.orders.OrdersPanel;
/*     */ import com.dukascopy.dds2.greed.gui.component.table.CheckBoxHeader;
/*     */ import com.dukascopy.dds2.greed.gui.component.table.CheckBoxHeaderItemListener;
/*     */ import com.dukascopy.dds2.greed.gui.component.table.ScrollPaneHeaderRenderer;
/*     */ import com.dukascopy.dds2.greed.gui.l10n.LocalizationManager;
/*     */ import com.dukascopy.dds2.greed.gui.l10n.components.JLocalizableMenuItem;
/*     */ import com.dukascopy.dds2.greed.gui.settings.ClientSettingsStorage;
/*     */ import com.dukascopy.dds2.greed.model.CurrencyMarketWrapper;
/*     */ import com.dukascopy.dds2.greed.model.MarketView;
/*     */ import com.dukascopy.dds2.greed.util.OrderMessageUtils;
/*     */ import com.dukascopy.transport.common.model.type.Money;
/*     */ import com.dukascopy.transport.common.model.type.OfferSide;
/*     */ import com.dukascopy.transport.common.model.type.OrderDirection;
/*     */ import com.dukascopy.transport.common.model.type.OrderSide;
/*     */ import com.dukascopy.transport.common.model.type.OrderState;
/*     */ import com.dukascopy.transport.common.model.type.Position;
/*     */ import com.dukascopy.transport.common.model.type.PositionSide;
/*     */ import com.dukascopy.transport.common.msg.group.OrderGroupMessage;
/*     */ import com.dukascopy.transport.common.msg.group.OrderMessage;
/*     */ import com.dukascopy.transport.common.msg.request.CurrencyOffer;
/*     */ import com.dukascopy.transport.common.msg.signals.SignalMessage;
/*     */ import java.awt.Component;
/*     */ import java.awt.Dimension;
/*     */ import java.awt.Insets;
/*     */ import java.awt.Point;
/*     */ import java.awt.event.ActionEvent;
/*     */ import java.awt.event.ActionListener;
/*     */ import java.awt.event.MouseAdapter;
/*     */ import java.awt.event.MouseEvent;
/*     */ import java.math.BigDecimal;
/*     */ import java.text.ParseException;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Comparator;
/*     */ import java.util.List;
/*     */ import javax.swing.AbstractAction;
/*     */ import javax.swing.ButtonModel;
/*     */ import javax.swing.JButton;
/*     */ import javax.swing.JOptionPane;
/*     */ import javax.swing.JPanel;
/*     */ import javax.swing.JPopupMenu;
/*     */ import javax.swing.JScrollPane;
/*     */ import javax.swing.JTable;
/*     */ import javax.swing.JViewport;
/*     */ import javax.swing.ListSelectionModel;
/*     */ import javax.swing.RowSorter;
/*     */ import javax.swing.Timer;
/*     */ import javax.swing.table.JTableHeader;
/*     */ import javax.swing.table.TableCellRenderer;
/*     */ import javax.swing.table.TableColumn;
/*     */ import javax.swing.table.TableColumnModel;
/*     */ import javax.swing.table.TableRowSorter;
/*     */ import org.slf4j.Logger;
/*     */ import org.slf4j.LoggerFactory;
/*     */ 
/*     */ public class ExposurePanel extends JPanel
/*     */ {
/*  74 */   private static final Logger LOGGER = LoggerFactory.getLogger(ExposurePanel.class);
/*     */   private ExposureTable table;
/*     */   private JScrollPane tablePane;
/*     */   private JPopupMenu popup;
/*     */   private JLocalizableMenuItem menuItemCS;
/*     */   private JLocalizableMenuItem menuItemCP;
/*     */   private JLocalizableMenuItem menuItemMP;
/*     */   private Timer timer;
/*     */   private HeaderPanel header;
/*     */ 
/*     */   public ExposurePanel()
/*     */   {
/*  87 */     this.table = new ExposureTable(new ExposureTableModel());
/*     */   }
/*     */ 
/*     */   public void build() {
/*  91 */     setMinimumSize(new Dimension(0, 0));
/*  92 */     if (GreedContext.isStrategyAllowed()) {
/*  93 */       setPreferredSize(GuiUtilsAndConstants.getOneQuarterOfDisplayDimension());
/*     */     }
/*  95 */     setupTable();
/*  96 */     add(this.tablePane);
/*     */ 
/*  98 */     this.timer = new Timer(200, new ActionListener() {
/*     */       public void actionPerformed(ActionEvent e) {
/* 100 */         GuiUtilsAndConstants.sendResetSignal(this);
/*     */       }
/*     */     });
/* 103 */     this.timer.setRepeats(false);
/*     */ 
/* 105 */     this.popup = new JPopupMenu();
/* 106 */     this.menuItemCP = new JLocalizableMenuItem("item.close.position");
/* 107 */     this.popup.add(this.menuItemCP);
/* 108 */     this.menuItemCP.setAction(new AbstractAction("item.close.position") {
/*     */       public void actionPerformed(ActionEvent e) {
/* 110 */         int selectedRow = ExposurePanel.this.table.getRowIndexFromModel();
/* 111 */         ExposureTableModel.ExposureHolder exh = ((ExposureTableModel)ExposurePanel.this.table.getModel()).getHolder(selectedRow);
/* 112 */         if (exh != null) {
/* 113 */           List positions = exh.positions;
/* 114 */           exh.setDisabled(true);
/* 115 */           ExposurePanel.this.closePositions(positions);
/*     */         }
/* 118 */         else if (ExposurePanel.LOGGER.isDebugEnabled()) {
/* 119 */           ExposurePanel.LOGGER.warn("Exposure not found for row: " + selectedRow);
/*     */         }
/*     */       }
/*     */     });
/* 124 */     this.menuItemMP = new JLocalizableMenuItem("item.merge.position");
/* 125 */     this.popup.add(this.menuItemMP);
/* 126 */     this.menuItemMP.setAction(new AbstractAction("item.merge.position") {
/*     */       public void actionPerformed(ActionEvent e) {
/* 128 */         int selectedRow = ExposurePanel.this.table.getRowIndexFromModel();
/* 129 */         ExposureTableModel.ExposureHolder exh = ((ExposureTableModel)ExposurePanel.this.table.getModel()).getHolder(selectedRow);
/* 130 */         if (exh != null) {
/* 131 */           List positions = exh.positions;
/* 132 */           if (!ExposurePanel.this.mergePositions(positions)) {
/* 133 */             ClientForm formParent = (ClientForm)GreedContext.get("clientGui");
/* 134 */             JOptionPane.showMessageDialog(formParent, LocalizationManager.getText("joption.pane.cancel.pending.orders"), GuiUtilsAndConstants.LABEL_SHORT_NAME, 1);
/*     */           }
/*     */           else
/*     */           {
/* 143 */             for (Position selectedPosition : exh.positions) {
/* 144 */               selectedPosition.setDisabled(true);
/*     */             }
/*     */           }
/*     */ 
/*     */         }
/* 149 */         else if (ExposurePanel.LOGGER.isDebugEnabled()) {
/* 150 */           ExposurePanel.LOGGER.warn("Exposure not found for row: " + selectedRow);
/*     */         }
/*     */       }
/*     */     });
/* 156 */     this.menuItemCP.addMouseListener(new MouseAdapter() {
/*     */       public void mouseEntered(MouseEvent e) {
/* 158 */         if (GreedContext.isSmspcEnabled()) {
/* 159 */           GreedContext.setConfig("timein", Long.valueOf(System.currentTimeMillis()));
/* 160 */           GreedContext.setConfig("control", ExposurePanel.this.menuItemCP);
/*     */ 
/* 162 */           int selectedRow = ExposurePanel.this.table.getRowIndexFromModel();
/* 163 */           ExposureTableModel.ExposureHolder holder = ((ExposureTableModel)ExposurePanel.this.table.getModel()).getHolder(selectedRow);
/* 164 */           ExposurePanel.this.sendIntention(holder);
/*     */         }
/*     */       }
/*     */ 
/*     */       public void mouseExited(MouseEvent e) {
/* 169 */         if (!ExposurePanel.this.timer.isRunning())
/* 170 */           ExposurePanel.this.timer.start();
/*     */         else
/* 172 */           ExposurePanel.this.timer.restart();
/*     */       }
/*     */     });
/* 176 */     this.menuItemCS = new JLocalizableMenuItem("item.close.selected");
/* 177 */     this.popup.add(this.menuItemCS);
/* 178 */     this.menuItemCS.setAction(new AbstractAction("item.close.selected") {
/*     */       public void actionPerformed(ActionEvent e) {
/* 180 */         List selectedPositions = new ArrayList();
/* 181 */         for (ExposureTableModel.ExposureHolder exh : ((ExposureTableModel)ExposurePanel.this.table.getModel()).getHolders()) {
/* 182 */           if (exh.isSelected()) {
/* 183 */             exh.setDisabled(true);
/* 184 */             selectedPositions.addAll(exh.positions);
/*     */           }
/*     */         }
/* 187 */         if (selectedPositions.size() > 0)
/* 188 */           ExposurePanel.this.closePositions(selectedPositions);
/*     */       }
/*     */     });
/* 192 */     this.menuItemCS.addMouseListener(new MouseAdapter() {
/*     */       public void mouseEntered(MouseEvent e) {
/* 194 */         GreedContext.setConfig("timein", Long.valueOf(System.currentTimeMillis()));
/* 195 */         GreedContext.setConfig("control", ExposurePanel.this.menuItemCS);
/*     */ 
/* 197 */         if (GreedContext.isSmspcEnabled())
/* 198 */           for (ExposureTableModel.ExposureHolder holder : ((ExposureTableModel)ExposurePanel.this.table.getModel()).getHolders())
/*     */           {
/* 200 */             if (holder.isSelected()) {
/* 201 */               ExposurePanel.this.sendIntention(holder);
/* 202 */               break;
/*     */             }
/*     */           }
/*     */       }
/*     */ 
/*     */       public void mouseExited(MouseEvent e)
/*     */       {
/* 209 */         if (!ExposurePanel.this.timer.isRunning())
/* 210 */           ExposurePanel.this.timer.start();
/*     */         else
/* 212 */           ExposurePanel.this.timer.restart();
/*     */       }
/*     */     });
/* 216 */     if (GreedContext.isContest())
/* 217 */       for (Component mi : this.popup.getComponents())
/* 218 */         ((JLocalizableMenuItem)mi).setEnabled(false);
/*     */   }
/*     */ 
/*     */   private void addColumnsComparators()
/*     */   {
/* 227 */     TableRowSorter sorter = new TableRowSorter((ExposureTableModel)this.table.getModel());
/* 228 */     this.table.setRowSorter(sorter);
/*     */ 
/* 230 */     Comparator longShortComparator = new Comparator()
/*     */     {
/*     */       public int compare(Object o1, Object o2)
/*     */       {
/* 235 */         if (((o1 instanceof ExposureTableModel.ExposureHolder)) && ((o2 instanceof ExposureTableModel.ExposureHolder)))
/*     */         {
/* 237 */           ExposureTableModel.ExposureHolder ex1 = (ExposureTableModel.ExposureHolder)o1;
/* 238 */           ExposureTableModel.ExposureHolder ex2 = (ExposureTableModel.ExposureHolder)o2;
/*     */ 
/* 240 */           int result = ex1.amountL.compareTo(ex2.amountL);
/* 241 */           if (result == 0) result = ex1.amountS.compareTo(ex2.amountS);
/* 242 */           return result;
/* 243 */         }return 0;
/*     */       }
/*     */     };
/* 249 */     Comparator priceComparator = new Comparator()
/*     */     {
/*     */       public int compare(Object o1, Object o2)
/*     */       {
/* 255 */         if (((o1 instanceof BigDecimal)) && (!(o2 instanceof BigDecimal)))
/* 256 */           return 1;
/* 257 */         if ((!(o1 instanceof BigDecimal)) && ((o2 instanceof BigDecimal)))
/* 258 */           return -1;
/* 259 */         if ((!(o1 instanceof BigDecimal)) && (!(o2 instanceof BigDecimal))) {
/* 260 */           return 0;
/*     */         }
/*     */ 
/* 263 */         if (((o1 instanceof BigDecimal)) && ((o2 instanceof BigDecimal)))
/* 264 */           return ((BigDecimal)o1).compareTo((BigDecimal)o2);
/* 265 */         return 0;
/*     */       }
/*     */     };
/* 270 */     Comparator pl_Comparator = new Comparator()
/*     */     {
/*     */       public int compare(Object o1, Object o2)
/*     */       {
/* 275 */         if (((o1 instanceof Money)) && (!(o2 instanceof Money)))
/* 276 */           return 1;
/* 277 */         if ((!(o1 instanceof Money)) && ((o2 instanceof Money)))
/* 278 */           return -1;
/* 279 */         if ((!(o1 instanceof Money)) && (!(o2 instanceof Money))) {
/* 280 */           return 0;
/*     */         }
/*     */ 
/* 283 */         if (((o1 instanceof Money)) && ((o2 instanceof Money)))
/* 284 */           return ((Money)o1).compareTo((Money)o2);
/* 285 */         return 0;
/*     */       }
/*     */     };
/* 290 */     sorter.setComparator(5, priceComparator);
/* 291 */     sorter.setComparator(3, longShortComparator);
/* 292 */     sorter.setComparator(6, pl_Comparator);
/* 293 */     sorter.setSortable(0, false);
/*     */ 
/* 295 */     List sortKeys = ((ClientSettingsStorage)GreedContext.get("settingsStorage")).restoreTableSortKeys(this.table.getTableId());
/* 296 */     sorter.setSortKeys(sortKeys);
/*     */   }
/*     */ 
/*     */   private boolean mergePositions(List<Position> positions) {
/* 300 */     List mergeOrderGroupIdList = new ArrayList();
/* 301 */     for (Position selectedPosition : positions) {
/* 302 */       mergeOrderGroupIdList.add(selectedPosition.getPositionID());
/*     */     }
/*     */ 
/* 306 */     return OrderFactory.mergePositions(mergeOrderGroupIdList).equals(OrderFactory.MergeValidationResultCode.OK);
/*     */   }
/*     */ 
/*     */   private void sendIntention(ExposureTableModel.ExposureHolder holder)
/*     */   {
/* 320 */     if ((holder != null) && (holder.amountL != null) && (holder.amountS != null))
/*     */     {
/*     */       BigDecimal a;
/*     */       OrderSide side;
/*     */       BigDecimal a;
/* 321 */       if (holder.amountL.compareTo(holder.amountS) >= 0) {
/* 322 */         OrderSide side = OrderSide.SELL; a = holder.amountL;
/*     */       } else {
/* 324 */         side = OrderSide.BUY; a = holder.amountS;
/*     */       }
/* 326 */       SignalMessage signal = new SignalMessage(holder.instrument, "tbcm", side, a, GreedContext.encodeAuth(), null);
/*     */ 
/* 332 */       GreedContext.publishEvent(new SignalAction(this, signal));
/*     */     }
/* 334 */     else if (holder == null) {
/* 335 */       LOGGER.debug("WARN: ss was not sent (holder is null)");
/*     */     } else {
/* 337 */       LOGGER.debug("WARN: ss was not sent (holder,holder.amountL,holder.amountS) = (" + holder + "," + holder.amountL + "," + holder.amountS + ")");
/*     */     }
/*     */ 
/* 341 */     this.timer.stop();
/*     */   }
/*     */ 
/*     */   private void closePositions(List<Position> selectedPositions) {
/* 345 */     resetTableHeaderCheckBox();
/* 346 */     ClientForm gui = (ClientForm)GreedContext.get("clientGui");
/* 347 */     MarketView marketView = (MarketView)GreedContext.get("marketView");
/*     */ 
/* 350 */     List closingOrders = new ArrayList();
/* 351 */     for (Position position : selectedPositions) {
/* 352 */       if (position.isInClosingState()) {
/* 353 */         if (LOGGER.isDebugEnabled()) {
/* 354 */           LOGGER.debug("MassClose Error: Group :" + position.getPositionID() + " Skipping position of MassClose"); continue;
/*     */         }
/*     */       }
/*     */ 
/* 358 */       OfferSide side = position.getPositionSide() == PositionSide.LONG ? OfferSide.BID : OfferSide.ASK;
/*     */ 
/* 360 */       CurrencyOffer offer = marketView.getBestOffer(position.getInstrument(), side);
/* 361 */       Money currentPrice = null != offer ? offer.getPrice() : position.getPriceCurrent();
/*     */ 
/* 363 */       OrderGroupMessage orderGroup = null;
/* 364 */       if ((GreedContext.isGlobal()) || (GreedContext.isGlobalExtended()))
/*     */       {
/* 366 */         orderGroup = getGroupFromPosition(position);
/*     */       }
/* 368 */       else orderGroup = gui.getOrdersPanel().getOrderGroup(position.getPositionID());
/*     */ 
/* 371 */       if (orderGroup != null) {
/* 372 */         position.setDisabled(true);
/* 373 */         position.setInClosingState(true);
/* 374 */         OrderMessage closingOrder = createClosingOrderFromPosition(currentPrice, position);
/* 375 */         if (null != closingOrder) {
/* 376 */           closingOrders.add(closingOrder);
/*     */           try
/*     */           {
/* 380 */             OrderGroupMessage loggingClose = OrderMessageUtils.copyOrderGroup(orderGroup);
/* 381 */             GuiUtilsAndConstants.logClosePosition(this, loggingClose, currentPrice);
/*     */           } catch (ParseException e) {
/* 383 */             LOGGER.error(e.getMessage(), e);
/*     */           }
/*     */         } else {
/* 386 */           LOGGER.debug("MassClose ERROR: Group " + position.getPositionID() + " Closing order creation failed ");
/*     */         }
/*     */       } else {
/* 389 */         LOGGER.debug("MassClose ERROR: Group " + position.getPositionID() + " not found in ORDERsPANEL");
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 394 */     OrderGroupMessage massCloseGroup = new OrderGroupMessage();
/* 395 */     massCloseGroup.setOrders(closingOrders);
/*     */ 
/* 398 */     MassOrderGroupCloseAction action = new MassOrderGroupCloseAction(this, massCloseGroup);
/* 399 */     GreedContext.publishEvent(action);
/*     */   }
/*     */ 
/*     */   private OrderMessage createClosingOrderFromPosition(Money currentPrice, Position position) {
/* 403 */     OrderSide closeSide = position.getPositionSide() == PositionSide.LONG ? OrderSide.SELL : OrderSide.BUY;
/* 404 */     OrderMessage result = new OrderMessage();
/* 405 */     result.setAmount(position.getAmount());
/* 406 */     result.setInstrument(position.getInstrument());
/* 407 */     result.setOrderDirection(OrderDirection.CLOSE);
/* 408 */     result.setOrderGroupId(position.getPositionID());
/* 409 */     result.setOrderState(OrderState.CREATED);
/* 410 */     result.setSide(closeSide);
/* 411 */     result.setPriceClient(currentPrice);
/* 412 */     result.setExternalSysId(position.getOrderGroup().getExternalSysId());
/* 413 */     result.setSignalId(position.getOrderGroup().getSignalId());
/*     */ 
/* 415 */     ClientSettingsStorage clientSettingsStorage = (ClientSettingsStorage)GreedContext.get("settingsStorage");
/* 416 */     if (clientSettingsStorage.restoreApplySlippageToAllMarketOrders())
/*     */     {
/* 418 */       String slippage = GuiUtilsAndConstants.getSlippageAmount(clientSettingsStorage.restoreDefaultSlippageAsText(), closeSide, position.getInstrument());
/* 419 */       result.setPriceTrailingLimit(slippage);
/*     */     }
/*     */ 
/* 423 */     return result;
/*     */   }
/*     */ 
/*     */   private void resetTableHeaderCheckBox() {
/* 427 */     TableColumn checkColumn = this.table.getTableHeader().getColumnModel().getColumn(this.table.convertColumnIndexToView(0));
/*     */ 
/* 429 */     CheckBoxHeader header = (CheckBoxHeader)checkColumn.getHeaderRenderer();
/* 430 */     if (header != null) {
/* 431 */       header.setSelected(false);
/*     */     }
/* 433 */     this.table.getTableHeader().repaint();
/*     */   }
/*     */ 
/*     */   public ExposureTable getTable()
/*     */   {
/* 489 */     return this.table;
/*     */   }
/*     */ 
/*     */   private void setupTable() {
/* 493 */     this.table.setSelectionForeground(GreedContext.SELECTION_FG_COLOR);
/* 494 */     this.table.getSelectionModel().setSelectionMode(0);
/* 495 */     TableColumnModel model = this.table.getTableHeader().getColumnModel();
/* 496 */     model.getColumn(0).setHeaderValue("#");
/* 497 */     model.getColumn(0).setHeaderRenderer(new CheckBoxHeader(new CheckBoxHeaderItemListener(this.table), this.table));
/* 498 */     this.tablePane = new JScrollPane(this.table);
/* 499 */     this.tablePane.getViewport().setBackground(GreedContext.GLOBAL_BACKGROUND);
/* 500 */     this.tablePane.setCorner("UPPER_RIGHT_CORNER", new ScrollPaneHeaderRenderer());
/* 501 */     this.table.setBackground(GreedContext.GLOBAL_BACKGROUND);
/* 502 */     this.table.setSelectionBackground(GreedContext.SELECTION_COLOR);
/* 503 */     this.table.setSelectionForeground(GreedContext.SELECTION_FG_COLOR);
/* 504 */     this.tablePane.setVerticalScrollBarPolicy(22);
/*     */ 
/* 506 */     addColumnsComparators();
/*     */ 
/* 508 */     this.table.addMouseListener(new MouseAdapter() {
/*     */       public void mousePressed(MouseEvent e) {
/* 510 */         int selectedRow = ExposurePanel.this.table.rowAtPoint(new Point(e.getX(), e.getY()));
/* 511 */         if ((selectedRow < 0) || (selectedRow >= ExposurePanel.this.table.getRowCount()))
/* 512 */           return;
/* 513 */         ExposurePanel.this.table.setRowSelectionInterval(selectedRow, selectedRow);
/* 514 */         mayBeShowPopup(e);
/*     */       }
/*     */ 
/*     */       public void mouseReleased(MouseEvent e) {
/* 518 */         mayBeShowPopup(e);
/*     */       }
/*     */ 
/*     */       private void mayBeShowPopup(MouseEvent e) {
/* 522 */         if (GreedContext.isReadOnly()) {
/* 523 */           ExposurePanel.LOGGER.info("Operation is not available in view mode.");
/* 524 */           return;
/*     */         }
/*     */ 
/* 527 */         int selectedRow = ExposurePanel.this.table.rowAtPoint(new Point(e.getX(), e.getY()));
/* 528 */         selectedRow = ExposurePanel.this.table.getRowSorter().convertRowIndexToModel(selectedRow);
/*     */ 
/* 530 */         if (e.isPopupTrigger()) {
/* 531 */           if ((GreedContext.isGlobal()) || (GreedContext.isGlobalExtended())) {
/* 532 */             ExposurePanel.this.menuItemCS.setEnabled(true);
/* 533 */             ExposurePanel.this.menuItemCP.setEnabled(true);
/* 534 */             ExposurePanel.this.menuItemMP.setVisible(false);
/*     */           } else {
/* 536 */             int holderCount = 0;
/* 537 */             for (ExposureTableModel.ExposureHolder exh : ((ExposureTableModel)ExposurePanel.this.table.getModel()).getHolders()) {
/* 538 */               if (exh.isSelected()) {
/* 539 */                 holderCount++;
/*     */               }
/*     */             }
/*     */ 
/* 543 */             boolean moreThenTwoPositionsOfInstrument = false;
/* 544 */             ExposureTableModel.ExposureHolder exh = ((ExposureTableModel)ExposurePanel.this.table.getModel()).getHolder(selectedRow);
/* 545 */             if ((exh != null) && (exh.positions.size() > 1)) {
/* 546 */               moreThenTwoPositionsOfInstrument = true;
/*     */             }
/*     */ 
/* 549 */             if (!GreedContext.isContest()) {
/* 550 */               ExposurePanel.this.menuItemMP.setEnabled((holderCount < 2) && (moreThenTwoPositionsOfInstrument));
/* 551 */               ExposurePanel.this.menuItemCS.setEnabled(holderCount > 0);
/*     */             }
/*     */           }
/*     */ 
/* 555 */           ExposureTableModel.ExposureHolder exh = ((ExposureTableModel)ExposurePanel.this.table.getModel()).getHolder(selectedRow);
/* 556 */           if ((exh != null) && 
/* 557 */             (exh.isDisabled())) {
/* 558 */             ExposurePanel.this.menuItemCS.setEnabled(false);
/* 559 */             ExposurePanel.this.menuItemCP.setEnabled(false);
/* 560 */             ExposurePanel.this.menuItemMP.setEnabled(false);
/*     */           }
/*     */ 
/* 564 */           ExposurePanel.this.popup.show(ExposurePanel.this.table, e.getX(), e.getY());
/*     */         }
/*     */       }
/*     */     });
/* 569 */     ((ClientSettingsStorage)GreedContext.get("settingsStorage")).restoreTableColumns(this.table.getTableId(), this.table.getColumnModel());
/*     */   }
/*     */ 
/*     */   public void onMarketState(CurrencyMarketWrapper marketState) {
/* 573 */     ((ExposureTableModel)this.table.getModel()).onMarketState(marketState);
/*     */   }
/*     */ 
/*     */   private OrderGroupMessage getGroupFromPosition(Position position)
/*     */   {
/* 600 */     OrderGroupMessage result = new OrderGroupMessage();
/*     */ 
/* 602 */     if ((GreedContext.isGlobal()) || (GreedContext.isGlobalExtended())) {
/* 603 */       result.setAmount(position.getAmount());
/*     */ 
/* 605 */       PositionSide side = position.getPositionSide();
/*     */ 
/* 607 */       result.setOrderGroupId(position.getPositionID());
/* 608 */       result.setInstrument(position.getInstrument());
/* 609 */       result.setSide(side);
/*     */ 
/* 611 */       result.setPricePosOpen(position.getPriceOpen());
/* 612 */       result.setTag(position.getTag());
/*     */     }
/*     */ 
/* 615 */     return result;
/*     */   }
/*     */ 
/*     */   public void addHeader(HeaderPanel header) {
/* 619 */     add(header, 0);
/* 620 */     this.header = header;
/*     */   }
/*     */ 
/*     */   public HeaderPanel getHeader() {
/* 624 */     return this.header;
/*     */   }
/*     */ 
/*     */   class ButtonHeaderRenderer extends JButton
/*     */     implements TableCellRenderer
/*     */   {
/*     */     int pushedColumn;
/*     */ 
/*     */     public ButtonHeaderRenderer()
/*     */     {
/* 579 */       this.pushedColumn = -1;
/* 580 */       setMargin(new Insets(0, 0, 0, 0));
/*     */     }
/*     */ 
/*     */     public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
/*     */     {
/* 586 */       setText(value == null ? "" : value.toString());
/* 587 */       boolean isPressed = column == this.pushedColumn;
/* 588 */       getModel().setPressed(isPressed);
/* 589 */       getModel().setArmed(isPressed);
/* 590 */       return this;
/*     */     }
/*     */ 
/*     */     public void setPressedColumn(int col) {
/* 594 */       this.pushedColumn = col;
/*     */     }
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.exposure.ExposurePanel
 * JD-Core Version:    0.6.0
 */