/*      */ package com.dukascopy.dds2.greed.gui.component.positions;
/*      */ 
/*      */ import com.dukascopy.dds2.greed.GreedContext;
/*      */ import com.dukascopy.dds2.greed.actions.MassOrderGroupCloseAction;
/*      */ import com.dukascopy.dds2.greed.actions.OrderGroupCloseAction;
/*      */ import com.dukascopy.dds2.greed.actions.SignalAction;
/*      */ import com.dukascopy.dds2.greed.api.OrderFactory;
/*      */ import com.dukascopy.dds2.greed.api.OrderFactory.MergeValidationResultCode;
/*      */ import com.dukascopy.dds2.greed.gui.ClientForm;
/*      */ import com.dukascopy.dds2.greed.gui.GuiUtilsAndConstants;
/*      */ import com.dukascopy.dds2.greed.gui.component.HeaderPanel;
/*      */ import com.dukascopy.dds2.greed.gui.component.JRoundedBorder;
/*      */ import com.dukascopy.dds2.greed.gui.component.detached.StopEditDetached;
/*      */ import com.dukascopy.dds2.greed.gui.component.dialog.OrderConfirmationDialog;
/*      */ import com.dukascopy.dds2.greed.gui.component.orders.OrdersPanel;
/*      */ import com.dukascopy.dds2.greed.gui.component.orders.OrdersTable;
/*      */ import com.dukascopy.dds2.greed.gui.component.table.CheckBoxHeader;
/*      */ import com.dukascopy.dds2.greed.gui.component.table.MoneyCellRenderer;
/*      */ import com.dukascopy.dds2.greed.gui.component.table.ScrollPaneHeaderRenderer;
/*      */ import com.dukascopy.dds2.greed.gui.component.table.TableScrollPaneUI;
/*      */ import com.dukascopy.dds2.greed.gui.l10n.LocalizationManager;
/*      */ import com.dukascopy.dds2.greed.gui.l10n.components.JLocalizableLabel;
/*      */ import com.dukascopy.dds2.greed.gui.l10n.components.JLocalizableMenuItem;
/*      */ import com.dukascopy.dds2.greed.gui.settings.ClientSettingsStorage;
/*      */ import com.dukascopy.dds2.greed.model.AccountStatement;
/*      */ import com.dukascopy.dds2.greed.model.CurrencyMarketWrapper;
/*      */ import com.dukascopy.dds2.greed.model.MarketView;
/*      */ import com.dukascopy.dds2.greed.model.StopOrderType;
/*      */ import com.dukascopy.dds2.greed.util.OrderMessageUtils;
/*      */ import com.dukascopy.dds2.greed.util.OrderUtils;
/*      */ import com.dukascopy.transport.common.model.type.Money;
/*      */ import com.dukascopy.transport.common.model.type.OfferSide;
/*      */ import com.dukascopy.transport.common.model.type.OrderDirection;
/*      */ import com.dukascopy.transport.common.model.type.OrderSide;
/*      */ import com.dukascopy.transport.common.model.type.OrderState;
/*      */ import com.dukascopy.transport.common.model.type.Position;
/*      */ import com.dukascopy.transport.common.model.type.PositionSide;
/*      */ import com.dukascopy.transport.common.msg.group.OrderGroupMessage;
/*      */ import com.dukascopy.transport.common.msg.group.OrderMessage;
/*      */ import com.dukascopy.transport.common.msg.request.AccountInfoMessage;
/*      */ import com.dukascopy.transport.common.msg.request.CurrencyOffer;
/*      */ import com.dukascopy.transport.common.msg.signals.SignalMessage;
/*      */ import java.awt.Color;
/*      */ import java.awt.Dimension;
/*      */ import java.awt.Font;
/*      */ import java.awt.Point;
/*      */ import java.awt.event.ActionEvent;
/*      */ import java.awt.event.ActionListener;
/*      */ import java.awt.event.MouseAdapter;
/*      */ import java.awt.event.MouseEvent;
/*      */ import java.math.BigDecimal;
/*      */ import java.text.DecimalFormat;
/*      */ import java.text.ParseException;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Comparator;
/*      */ import java.util.Currency;
/*      */ import java.util.List;
/*      */ import javax.swing.AbstractAction;
/*      */ import javax.swing.BorderFactory;
/*      */ import javax.swing.Box;
/*      */ import javax.swing.BoxLayout;
/*      */ import javax.swing.JButton;
/*      */ import javax.swing.JLabel;
/*      */ import javax.swing.JOptionPane;
/*      */ import javax.swing.JPanel;
/*      */ import javax.swing.JPopupMenu;
/*      */ import javax.swing.JPopupMenu.Separator;
/*      */ import javax.swing.JScrollPane;
/*      */ import javax.swing.JSeparator;
/*      */ import javax.swing.JViewport;
/*      */ import javax.swing.ListSelectionModel;
/*      */ import javax.swing.RowSorter;
/*      */ import javax.swing.Timer;
/*      */ import javax.swing.event.ListSelectionEvent;
/*      */ import javax.swing.event.ListSelectionListener;
/*      */ import javax.swing.table.JTableHeader;
/*      */ import javax.swing.table.TableColumn;
/*      */ import javax.swing.table.TableColumnModel;
/*      */ import javax.swing.table.TableRowSorter;
/*      */ import org.slf4j.Logger;
/*      */ import org.slf4j.LoggerFactory;
/*      */ 
/*      */ public class PositionsPanel extends JPanel
/*      */ {
/*  101 */   private static Logger LOGGER = LoggerFactory.getLogger(PositionsPanel.class);
/*      */   private final PositionsTable table;
/*      */   private JPanel disclaimerPanel;
/*  105 */   private JLabel plValue = new JLabel();
/*      */   private JScrollPane tablePane;
/*      */   private JPopupMenu popup;
/*      */   private JRoundedBorder roundedBorder;
/*      */   private JLocalizableMenuItem menuItemSL;
/*      */   private JLocalizableMenuItem menuItemEditSL;
/*      */   private JLocalizableMenuItem menuItemCancelSL;
/*      */   private JLocalizableMenuItem menuItemTP;
/*      */   private JLocalizableMenuItem menuItemEditTP;
/*      */   private JLocalizableMenuItem menuItemCancelTP;
/*      */   private JLocalizableMenuItem menuItemCA;
/*      */   private JLocalizableMenuItem menuItemMP;
/*      */   private JLocalizableMenuItem menuItemPCC;
/*      */   private JLocalizableMenuItem menuItemCP;
/*  120 */   private JSeparator menuSeparatorSL = new JPopupMenu.Separator();
/*  121 */   private JSeparator menuSeparatorTP = new JPopupMenu.Separator();
/*      */   private Timer timer;
/*  125 */   private AccountStatement accountStatement = (AccountStatement)GreedContext.get("accountStatement");
/*      */   private Currency prevCurrency;
/*      */   private String prevSymbol;
/*  128 */   private DecimalFormat profitLossFormat = new DecimalFormat("#,##0.00");
/*      */ 
/*  131 */   private AbstractAction closePositionActionCopy = null;
/*      */   private HeaderPanel header;
/*      */ 
/*      */   public PositionsPanel()
/*      */   {
/*  138 */     PositionsTableModel.reinitColumnIndexs();
/*  139 */     this.table = new PositionsTable(new PositionsTableModel());
/*      */   }
/*      */ 
/*      */   public void build()
/*      */   {
/*  146 */     setMinimumSize(new Dimension(0, 0));
/*  147 */     if (GreedContext.isStrategyAllowed()) {
/*  148 */       setPreferredSize(GuiUtilsAndConstants.getOneQuarterOfDisplayDimension());
/*      */     }
/*  150 */     setupTable();
/*      */ 
/*  152 */     setLayout(new BoxLayout(this, 1));
/*      */ 
/*  154 */     if ((GreedContext.IS_FXDD_LABEL) || (GreedContext.IS_ALPARI_LABEL)) {
/*  155 */       add(getDisclaimerPanel());
/*      */     }
/*  157 */     add(this.tablePane);
/*      */ 
/*  159 */     this.timer = new Timer(200, new ActionListener() {
/*      */       public void actionPerformed(ActionEvent e) {
/*  161 */         GuiUtilsAndConstants.sendResetSignal(this);
/*      */       }
/*      */     });
/*  164 */     this.timer.setRepeats(false);
/*      */ 
/*  166 */     this.popup = new JPopupMenu();
/*  167 */     this.menuItemCP = new JLocalizableMenuItem();
/*  168 */     this.closePositionActionCopy = new AbstractAction("item.close.position") {
/*      */       public void actionPerformed(ActionEvent e) {
/*  170 */         GuiUtilsAndConstants.ensureEventDispatchThread();
/*  171 */         PositionsTableModel model = (PositionsTableModel)PositionsPanel.this.table.getModel();
/*  172 */         int row = PositionsPanel.this.table.getRowIndexFromModel();
/*  173 */         if (row != -1) {
/*  174 */           Position position = model.getPosition(row);
/*  175 */           boolean inSelList = model.getSelectedPositions().contains(position);
/*  176 */           MarketView marketView = (MarketView)GreedContext.get("marketView");
/*  177 */           OfferSide side = position.getPositionSide() == PositionSide.LONG ? OfferSide.BID : OfferSide.ASK;
/*  178 */           CurrencyOffer offer = marketView.getBestOffer(position.getInstrument(), side);
/*  179 */           Money currentPrice = null != offer ? offer.getPrice() : position.getPriceCurrent();
/*  180 */           if (((inSelList) && (!position.isInClosingState())) || (!inSelList))
/*  181 */             PositionsPanel.this.fireCloseAction(position, currentPrice);
/*      */         }
/*      */       }
/*      */     };
/*  186 */     this.menuItemCP.setAction(this.closePositionActionCopy);
/*  187 */     this.menuItemCP.addMouseListener(new MouseAdapter() {
/*      */       public void mouseEntered(MouseEvent e) {
/*  189 */         GreedContext.setConfig("timein", Long.valueOf(System.currentTimeMillis()));
/*  190 */         GreedContext.setConfig("control", PositionsPanel.this.menuItemCP);
/*      */ 
/*  193 */         if (GreedContext.isSmspcEnabled()) {
/*  194 */           PositionsTableModel model = (PositionsTableModel)PositionsPanel.this.table.getModel();
/*  195 */           int row = PositionsPanel.this.table.getRowIndexFromModel();
/*  196 */           if (-1 != row) {
/*  197 */             Position position = model.getPosition(row);
/*  198 */             boolean isSelected = model.getSelectedPositions().contains(position);
/*  199 */             OrderSide side = position.getPositionSide() == PositionSide.SHORT ? OrderSide.BUY : OrderSide.SELL;
/*  200 */             if (((isSelected) && (!position.isInClosingState())) || (!isSelected)) {
/*  201 */               SignalMessage signal = new SignalMessage(position.getInstrument(), "tbcm", side, position.getAmount().getValue(), GreedContext.encodeAuth(), null);
/*      */ 
/*  209 */               GreedContext.publishEvent(new SignalAction(this, signal));
/*  210 */               PositionsPanel.this.timer.stop();
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/*      */ 
/*      */       public void mouseExited(MouseEvent e) {
/*  217 */         if (!PositionsPanel.this.timer.isRunning())
/*  218 */           PositionsPanel.this.timer.start();
/*      */         else
/*  220 */           PositionsPanel.this.timer.restart();
/*      */       }
/*      */     });
/*  225 */     this.menuItemCA = new JLocalizableMenuItem();
/*  226 */     this.menuItemCA.setAction(new AbstractAction("item.close.selected") {
/*      */       public void actionPerformed(ActionEvent e) {
/*  228 */         PositionsTableModel model = (PositionsTableModel)PositionsPanel.this.table.getModel();
/*  229 */         List selectedPositions = model.getSelectedPositions();
/*  230 */         if (selectedPositions.size() > 0)
/*  231 */           PositionsPanel.this.fireMassCloseAction(selectedPositions);
/*      */       }
/*      */     });
/*  235 */     this.menuItemCA.addMouseListener(new MouseAdapter() {
/*      */       public void mouseEntered(MouseEvent e) {
/*  237 */         if (GreedContext.isSmspcEnabled()) {
/*  238 */           GreedContext.setConfig("timein", Long.valueOf(System.currentTimeMillis()));
/*  239 */           GreedContext.setConfig("control", PositionsPanel.this.menuItemCA);
/*      */ 
/*  242 */           if (GreedContext.isSmspcEnabled()) {
/*  243 */             PositionsTableModel model = (PositionsTableModel)PositionsPanel.this.table.getModel();
/*  244 */             List selectedPositions = model.getSelectedPositions();
/*  245 */             String instrument = null;
/*  246 */             BigDecimal amountLong = BigDecimal.ZERO;
/*  247 */             BigDecimal amountShort = BigDecimal.ZERO;
/*  248 */             for (Position position : selectedPositions) {
/*  249 */               if (position.isInClosingState()) {
/*      */                 continue;
/*      */               }
/*  252 */               if (null == instrument) {
/*  253 */                 instrument = position.getInstrument();
/*      */               }
/*  255 */               if (!instrument.equals(position.getInstrument())) {
/*      */                 continue;
/*      */               }
/*  258 */               Money m = position.getAmount();
/*  259 */               BigDecimal amount = null == m ? BigDecimal.ZERO : m.getValue();
/*  260 */               if (PositionSide.LONG == position.getPositionSide())
/*  261 */                 amountLong = amountLong.add(amount);
/*      */               else {
/*  263 */                 amountShort = amountShort.add(amount);
/*      */               }
/*      */             }
/*  266 */             if (null == instrument)
/*  267 */               return;
/*      */             BigDecimal amount;
/*      */             OrderSide side;
/*      */             BigDecimal amount;
/*  271 */             if (amountLong.compareTo(amountShort) >= 0) {
/*  272 */               OrderSide side = OrderSide.SELL; amount = amountLong;
/*      */             } else {
/*  274 */               side = OrderSide.BUY; amount = amountShort;
/*      */             }
/*  276 */             SignalMessage signal = new SignalMessage(instrument, "tbcm", side, amount, GreedContext.encodeAuth(), null);
/*      */ 
/*  284 */             GreedContext.publishEvent(new SignalAction(this, signal));
/*  285 */             PositionsPanel.this.timer.stop();
/*      */           }
/*      */         }
/*      */       }
/*      */ 
/*      */       public void mouseExited(MouseEvent e) {
/*  291 */         if (!PositionsPanel.this.timer.isRunning())
/*  292 */           PositionsPanel.this.timer.start();
/*      */         else
/*  294 */           PositionsPanel.this.timer.restart();
/*      */       }
/*      */     });
/*  299 */     this.menuItemPCC = new JLocalizableMenuItem();
/*  300 */     this.menuItemPCC.setAction(new AbstractAction("item.cond.close")
/*      */     {
/*      */       public void actionPerformed(ActionEvent actionEvent) {
/*  303 */         PositionsTableModel model = PositionsPanel.this.table.getPositionsModel();
/*  304 */         int row = PositionsPanel.this.table.getRowIndexFromModel();
/*  305 */         if (-1 != row)
/*  306 */           PositionsPanel.this.openEditDetachedPanel(model, row, StopOrderType.PART_CLOSE);
/*      */       }
/*      */     });
/*  311 */     this.menuItemSL = new JLocalizableMenuItem("item.add.stop.loss");
/*  312 */     this.menuItemSL.setAction(new AbstractAction("item.add.stop.loss") {
/*      */       public void actionPerformed(ActionEvent e) {
/*  314 */         PositionsTableModel model = PositionsPanel.this.table.getPositionsModel();
/*  315 */         int row = PositionsPanel.this.table.getRowIndexFromModel();
/*  316 */         if (row != -1)
/*  317 */           PositionsPanel.this.openEditDetachedPanel(model, row, StopOrderType.STOP_LOSS, false);
/*      */       }
/*      */     });
/*  322 */     this.menuItemEditSL = new JLocalizableMenuItem("item.edit.stop.loss");
/*  323 */     this.menuItemEditSL.setAction(new AbstractAction("item.edit.stop.loss") {
/*      */       public void actionPerformed(ActionEvent e) {
/*  325 */         PositionsTableModel model = PositionsPanel.this.table.getPositionsModel();
/*  326 */         int row = PositionsPanel.this.table.getRowIndexFromModel();
/*  327 */         if (row != -1)
/*  328 */           PositionsPanel.this.openEditDetachedPanel(model, row, StopOrderType.STOP_LOSS, true);
/*      */       }
/*      */     });
/*  333 */     this.menuItemCancelSL = new JLocalizableMenuItem("item.cancel.stop.loss");
/*  334 */     this.menuItemCancelSL.setAction(new AbstractAction("item.cancel.stop.loss") {
/*      */       public void actionPerformed(ActionEvent e) {
/*  336 */         PositionsTableModel model = PositionsPanel.this.table.getPositionsModel();
/*  337 */         int row = PositionsPanel.this.table.getRowIndexFromModel();
/*  338 */         if (row != -1)
/*  339 */           OrderUtils.getInstance().cancelOrder(model.getPosition(row).getOrderGroup().getStopLossOrder().getOrderId());
/*      */       }
/*      */     });
/*  344 */     this.menuItemTP = new JLocalizableMenuItem("item.add.take.profit");
/*  345 */     this.menuItemTP.setAction(new AbstractAction("item.add.take.profit") {
/*      */       public void actionPerformed(ActionEvent e) {
/*  347 */         PositionsTableModel model = (PositionsTableModel)PositionsPanel.this.table.getModel();
/*  348 */         int row = PositionsPanel.this.table.getRowIndexFromModel();
/*  349 */         if (row != -1)
/*  350 */           PositionsPanel.this.openEditDetachedPanel(model, row, StopOrderType.TAKE_PROFIT, false);
/*      */       }
/*      */     });
/*  355 */     this.menuItemEditTP = new JLocalizableMenuItem("item.edit.take.profit");
/*  356 */     this.menuItemEditTP.setAction(new AbstractAction("item.edit.take.profit") {
/*      */       public void actionPerformed(ActionEvent e) {
/*  358 */         PositionsTableModel model = PositionsPanel.this.table.getPositionsModel();
/*  359 */         int row = PositionsPanel.this.table.getRowIndexFromModel();
/*  360 */         if (row != -1)
/*  361 */           PositionsPanel.this.openEditDetachedPanel(model, row, StopOrderType.TAKE_PROFIT, true);
/*      */       }
/*      */     });
/*  366 */     this.menuItemCancelTP = new JLocalizableMenuItem("item.cancel.take.profit");
/*  367 */     this.menuItemCancelTP.setAction(new AbstractAction("item.cancel.take.profit") {
/*      */       public void actionPerformed(ActionEvent e) {
/*  369 */         PositionsTableModel model = PositionsPanel.this.table.getPositionsModel();
/*  370 */         int row = PositionsPanel.this.table.getRowIndexFromModel();
/*  371 */         if (row != -1)
/*  372 */           OrderUtils.getInstance().cancelOrder(model.getPosition(row).getOrderGroup().getTakeProfitOrder().getOrderId());
/*      */       }
/*      */     });
/*  377 */     this.menuItemMP = new JLocalizableMenuItem("item.merge.selected");
/*  378 */     this.menuItemMP.setAction(new AbstractAction("item.merge.selected") {
/*      */       public void actionPerformed(ActionEvent e) {
/*  380 */         if (!PositionsPanel.this.fireMergeAction()) {
/*  381 */           ClientForm formParent = (ClientForm)GreedContext.get("clientGui");
/*  382 */           JOptionPane.showMessageDialog(formParent, LocalizationManager.getText("joption.pane.cancel.pending.orders"), GuiUtilsAndConstants.LABEL_SHORT_NAME, 1);
/*      */         }
/*      */       }
/*      */     });
/*  392 */     this.popup.add(this.menuItemCP);
/*  393 */     this.popup.add(this.menuItemPCC);
/*  394 */     this.popup.add(this.menuItemCA);
/*  395 */     this.popup.add(this.menuItemMP);
/*  396 */     this.popup.add(this.menuSeparatorSL);
/*  397 */     this.popup.add(this.menuItemSL);
/*  398 */     this.popup.add(this.menuItemEditSL);
/*  399 */     this.popup.add(this.menuItemCancelSL);
/*  400 */     this.popup.add(this.menuSeparatorTP);
/*  401 */     this.popup.add(this.menuItemTP);
/*  402 */     this.popup.add(this.menuItemEditTP);
/*  403 */     this.popup.add(this.menuItemCancelTP);
/*      */   }
/*      */ 
/*      */   private void addColumnsComparators() {
/*  407 */     this.table.setFillsViewportHeight(true);
/*  408 */     this.table.setAutoCreateRowSorter(true);
/*  409 */     TableRowSorter sorter = new TableRowSorter(this.table.getModel());
/*  410 */     this.table.setRowSorter(sorter);
/*      */ 
/*  412 */     Comparator posIdComparator = new Comparator()
/*      */     {
/*      */       public int compare(String o1, String o2) {
/*  415 */         Long id1 = null;
/*  416 */         Long id2 = null;
/*      */         try {
/*  418 */           id1 = Long.valueOf(o1);
/*      */         } catch (Exception e) {
/*  420 */           return -1;
/*      */         }
/*      */         try
/*      */         {
/*  424 */           id2 = Long.valueOf(o2);
/*      */         } catch (Exception e) {
/*  426 */           return 1;
/*      */         }
/*      */ 
/*  429 */         return id1.compareTo(id2);
/*      */       }
/*      */     };
/*  433 */     Comparator amountComparator = new Comparator()
/*      */     {
/*      */       public int compare(String o1, String o2) {
/*  436 */         return o1.compareTo(o2);
/*      */       }
/*      */     };
/*  440 */     Comparator plPipsComparator = new Comparator()
/*      */     {
/*      */       public int compare(Object o1, Object o2) {
/*  443 */         if (((o1 instanceof BigDecimal)) && (!(o2 instanceof BigDecimal)))
/*  444 */           return 1;
/*  445 */         if ((!(o1 instanceof BigDecimal)) && ((o2 instanceof BigDecimal)))
/*  446 */           return -1;
/*  447 */         if ((!(o1 instanceof BigDecimal)) && (!(o2 instanceof BigDecimal))) {
/*  448 */           return 0;
/*      */         }
/*      */ 
/*  451 */         if (((o1 instanceof BigDecimal)) && ((o2 instanceof BigDecimal))) {
/*  452 */           return ((BigDecimal)o1).compareTo((BigDecimal)o2);
/*      */         }
/*  454 */         return 0;
/*      */       }
/*      */     };
/*  459 */     Comparator pl_Comparator = new Comparator()
/*      */     {
/*      */       public int compare(Object o1, Object o2) {
/*  462 */         if (((o1 instanceof Money)) && (!(o2 instanceof Money)))
/*  463 */           return 1;
/*  464 */         if ((!(o1 instanceof Money)) && ((o2 instanceof Money)))
/*  465 */           return -1;
/*  466 */         if ((!(o1 instanceof Money)) && (!(o2 instanceof Money))) {
/*  467 */           return 0;
/*      */         }
/*      */ 
/*  470 */         if (((o1 instanceof Money)) && ((o2 instanceof Money))) {
/*  471 */           return ((Money)o1).compareTo((Money)o2);
/*      */         }
/*  473 */         return 0;
/*      */       }
/*      */     };
/*  478 */     sorter.setComparator(PositionsTableModel.COLUMN_ID, posIdComparator);
/*  479 */     sorter.setComparator(PositionsTableModel.COLUMN_AMOUNT, amountComparator);
/*  480 */     sorter.setComparator(PositionsTableModel.COLUMN_PROFIT_LOSS_PIP, plPipsComparator);
/*  481 */     sorter.setComparator(PositionsTableModel.COLUMN_PROFIT_LOSS, pl_Comparator);
/*  482 */     sorter.setSortable(0, false);
/*      */ 
/*  484 */     List sortKeys = ((ClientSettingsStorage)GreedContext.get("settingsStorage")).restoreTableSortKeys(this.table.getTableId());
/*  485 */     sorter.setSortKeys(sortKeys);
/*      */   }
/*      */ 
/*      */   public void setProfitLost(BigDecimal value) {
/*  489 */     if (value == null) {
/*  490 */       return;
/*      */     }
/*  492 */     Color color = value.compareTo(BigDecimal.ZERO) < 0 ? MoneyCellRenderer.COLOR_NEGATIVE : MoneyCellRenderer.COLOR_POSITIVE;
/*  493 */     if (!color.equals(this.plValue.getForeground()))
/*      */     {
/*  495 */       this.plValue.setForeground(color);
/*      */     }
/*      */ 
/*  499 */     if ((this.accountStatement.getLastAccountState() != null) && (this.accountStatement.getLastAccountState().getCurrency() != null))
/*      */     {
/*  501 */       Currency currency = this.accountStatement.getLastAccountState().getCurrency();
/*      */ 
/*  503 */       if ((this.prevCurrency == null) || (currency != this.prevCurrency)) {
/*  504 */         this.prevCurrency = currency;
/*  505 */         this.prevSymbol = currency.getSymbol();
/*      */       }
/*  507 */       String formatted = this.prevSymbol + " " + this.profitLossFormat.format(value);
/*  508 */       if (!formatted.equals(this.plValue.getText()))
/*  509 */         this.plValue.setText(formatted);
/*      */     }
/*      */   }
/*      */ 
/*      */   protected JRoundedBorder getRoundedBorder()
/*      */   {
/*  515 */     if (this.roundedBorder == null) {
/*  516 */       this.roundedBorder = new JRoundedBorder(getDisclaimerPanel());
/*      */     }
/*  518 */     return this.roundedBorder;
/*      */   }
/*      */ 
/*      */   private JPanel getDisclaimerPanel() {
/*  522 */     if (this.disclaimerPanel == null) {
/*  523 */       JLocalizableLabel profitLossLabel = new JLocalizableLabel("label.profit.loss");
/*  524 */       this.disclaimerPanel = new JPanel();
/*  525 */       this.disclaimerPanel.setLayout(new BoxLayout(this.disclaimerPanel, 0));
/*      */ 
/*  527 */       JLocalizableLabel label = new JLocalizableLabel("disclaimer.exposure.structure");
/*  528 */       label.setBorder(getRoundedBorder());
/*  529 */       label.setFont(label.getFont().deriveFont(1));
/*      */ 
/*  531 */       JPanel container = new JPanel();
/*  532 */       container.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
/*  533 */       container.setLayout(new BoxLayout(container, 1));
/*      */ 
/*  535 */       profitLossLabel.setAlignmentX(0.5F);
/*  536 */       profitLossLabel.setFont(label.getFont().deriveFont(12.0F));
/*  537 */       container.add(profitLossLabel, container);
/*  538 */       this.plValue.setFont(this.plValue.getFont().deriveFont(1));
/*  539 */       this.plValue.setFont(this.plValue.getFont().deriveFont(14.0F));
/*  540 */       this.plValue.setAlignmentX(0.5F);
/*  541 */       container.add(this.plValue, container);
/*      */ 
/*  543 */       this.disclaimerPanel.add(label);
/*  544 */       this.disclaimerPanel.add(Box.createHorizontalGlue());
/*  545 */       this.disclaimerPanel.add(container);
/*      */ 
/*  547 */       setPreferredSize(new Dimension(10, 10));
/*      */     }
/*      */ 
/*  550 */     return this.disclaimerPanel;
/*      */   }
/*      */ 
/*      */   private void fireMassCloseAction(List<Position> selectedPositions) {
/*  554 */     resetHeaderCheckBox();
/*  555 */     MarketView marketView = (MarketView)GreedContext.get("marketView");
/*  556 */     ClientForm gui = (ClientForm)GreedContext.get("clientGui");
/*      */ 
/*  559 */     List closingOrders = new ArrayList();
/*      */ 
/*  561 */     for (Position position : selectedPositions) {
/*  562 */       if (position.isInClosingState()) {
/*  563 */         if (LOGGER.isDebugEnabled()) {
/*  564 */           LOGGER.debug("MassClose Error: Group :" + position.getPositionID() + " Skipping position of MassClose"); continue;
/*      */         }
/*      */       }
/*      */ 
/*  568 */       OfferSide side = position.getPositionSide() == PositionSide.LONG ? OfferSide.BID : OfferSide.ASK;
/*      */ 
/*  570 */       CurrencyOffer offer = marketView.getBestOffer(position.getInstrument(), side);
/*  571 */       Money currentPrice = null != offer ? offer.getPrice() : position.getPriceCurrent();
/*      */ 
/*  573 */       OrderGroupMessage orderGroup = null;
/*  574 */       if ((GreedContext.isGlobal()) || (GreedContext.isGlobalExtended()))
/*  575 */         orderGroup = getGroupFromPosition(position);
/*      */       else {
/*  577 */         orderGroup = gui.getOrdersPanel().getOrderGroup(position.getPositionID());
/*      */       }
/*      */ 
/*  580 */       if (orderGroup != null) {
/*  581 */         position.setDisabled(true);
/*  582 */         position.setInClosingState(true);
/*  583 */         OrderMessage closingOrder = createClosingOrderFromPosition(currentPrice, position);
/*  584 */         closingOrder.setExternalSysId(orderGroup.getExternalSysId());
/*  585 */         closingOrder.setSignalId(orderGroup.getSignalId());
/*  586 */         if (null != closingOrder) {
/*  587 */           closingOrders.add(closingOrder);
/*      */           try
/*      */           {
/*  591 */             OrderGroupMessage loggingClose = OrderMessageUtils.copyOrderGroup(orderGroup);
/*  592 */             GuiUtilsAndConstants.logClosePosition(this, loggingClose, currentPrice);
/*      */           } catch (ParseException e) {
/*  594 */             LOGGER.error(e.getMessage(), e);
/*      */           }
/*      */         } else {
/*  597 */           LOGGER.debug("MassClose ERROR: Group " + position.getPositionID() + " Closing order creation failed ");
/*      */         }
/*      */       } else {
/*  600 */         LOGGER.debug("MassClose ERROR: Group " + position.getPositionID() + " not found in ORDERsPANEL");
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  605 */     OrderGroupMessage massCloseGroup = new OrderGroupMessage();
/*  606 */     massCloseGroup.setOrders(closingOrders);
/*      */ 
/*  609 */     MassOrderGroupCloseAction action = new MassOrderGroupCloseAction(this, massCloseGroup);
/*  610 */     GreedContext.publishEvent(action);
/*      */   }
/*      */ 
/*      */   private OrderMessage createClosingOrderFromPosition(Money currentPrice, Position position) {
/*  614 */     OrderSide closeSide = position.getPositionSide() == PositionSide.LONG ? OrderSide.SELL : OrderSide.BUY;
/*  615 */     OrderMessage result = new OrderMessage();
/*  616 */     result.setAmount(position.getAmount());
/*  617 */     result.setInstrument(position.getInstrument());
/*  618 */     result.setOrderDirection(OrderDirection.CLOSE);
/*  619 */     result.setOrderGroupId(position.getPositionID());
/*  620 */     result.setOrderState(OrderState.CREATED);
/*  621 */     result.setSide(closeSide);
/*  622 */     result.setPriceClient(currentPrice);
/*  623 */     result.setExternalSysId(position.getOrderGroup().getExternalSysId());
/*  624 */     result.setSignalId(position.getOrderGroup().getSignalId());
/*      */ 
/*  626 */     ClientSettingsStorage clientSettingsStorage = (ClientSettingsStorage)GreedContext.get("settingsStorage");
/*      */ 
/*  628 */     if (clientSettingsStorage.restoreApplySlippageToAllMarketOrders())
/*      */     {
/*  630 */       String slippage = GuiUtilsAndConstants.getSlippageAmount(clientSettingsStorage.restoreDefaultSlippageAsText(), closeSide, position.getInstrument());
/*      */ 
/*  632 */       result.setPriceTrailingLimit(slippage);
/*      */     }
/*      */ 
/*  637 */     return result;
/*      */   }
/*      */ 
/*      */   private boolean fireCloseAction(Position position, Money currentPrice) {
/*  641 */     resetHeaderCheckBox();
/*      */ 
/*  643 */     OrderGroupMessage orderGroup = null;
/*      */ 
/*  645 */     if ((GreedContext.isGlobalExtended()) || (GreedContext.isGlobal())) {
/*  646 */       orderGroup = getGroupFromPosition(position);
/*      */     } else {
/*  648 */       ClientForm gui = (ClientForm)GreedContext.get("clientGui");
/*  649 */       orderGroup = gui.getOrdersPanel().getOrderGroup(position.getPositionID());
/*      */     }
/*      */ 
/*  652 */     if (orderGroup != null)
/*      */     {
/*  656 */       if (GreedContext.isContest()) {
/*  657 */         OrderConfirmationDialog confirmationDialog = new OrderConfirmationDialog(orderGroup, currentPrice);
/*  658 */         confirmationDialog.setDeferedTradeLog(false);
/*  659 */         confirmationDialog.setVisible(true);
/*  660 */         return true;
/*      */       }
/*      */ 
/*  663 */       OrderGroupCloseAction closeAction = new OrderGroupCloseAction(this, orderGroup, currentPrice);
/*  664 */       GreedContext.publishEvent(closeAction);
/*      */ 
/*  666 */       if (GreedContext.isActivityLoggingEnabled())
/*      */       {
/*  668 */         GuiUtilsAndConstants.logClosePosition(this, orderGroup, currentPrice, true);
/*      */       }
/*  670 */       return true;
/*      */     }
/*  672 */     return false;
/*      */   }
/*      */ 
/*      */   private OrderGroupMessage getGroupFromPosition(Position position)
/*      */   {
/*  677 */     OrderGroupMessage result = new OrderGroupMessage();
/*      */ 
/*  679 */     if ((GreedContext.isGlobal()) || (GreedContext.isGlobalExtended())) {
/*  680 */       result.setAmount(position.getAmount());
/*      */ 
/*  682 */       PositionSide side = position.getPositionSide();
/*      */ 
/*  684 */       result.setOrderGroupId(position.getPositionID());
/*  685 */       result.setInstrument(position.getInstrument());
/*  686 */       result.setSide(side);
/*      */ 
/*  688 */       result.setPricePosOpen(position.getPriceOpen());
/*  689 */       result.setTag(position.getTag());
/*  690 */       result.setExternalSysId(position.getOrderGroup().getExternalSysId());
/*  691 */       result.setSignalId(position.getOrderGroup().getSignalId());
/*      */     }
/*      */ 
/*  694 */     return result;
/*      */   }
/*      */ 
/*      */   private boolean fireMergeAction() {
/*  698 */     PositionsTableModel model = (PositionsTableModel)this.table.getModel();
/*      */ 
/*  700 */     List selectedPositions = model.getSelectedPositions();
/*  701 */     if ((null == selectedPositions) || (selectedPositions.size() == 0)) {
/*  702 */       LOGGER.debug("MERGE: no positions selected");
/*  703 */       return false;
/*      */     }
/*  705 */     resetHeaderCheckBox();
/*      */ 
/*  707 */     List mergeOrderGroupIdList = new ArrayList();
/*  708 */     for (Position selectedPosition : selectedPositions) {
/*  709 */       mergeOrderGroupIdList.add(selectedPosition.getPositionID());
/*      */     }
/*      */ 
/*  712 */     if (!OrderFactory.mergePositions(mergeOrderGroupIdList).equals(OrderFactory.MergeValidationResultCode.OK)) {
/*  713 */       return false;
/*      */     }
/*      */ 
/*  717 */     for (Position selectedPosition : selectedPositions) {
/*  718 */       selectedPosition.setDisabled(true);
/*      */     }
/*  720 */     return true;
/*      */   }
/*      */ 
/*      */   private void openEditDetachedPanel(PositionsTableModel model, int row, StopOrderType stopOrderType, boolean isOpenedFromSLTPMenu) {
/*  724 */     ClientForm gui = (ClientForm)GreedContext.get("clientGui");
/*  725 */     Position position = model.getPosition(row);
/*  726 */     OrderGroupMessage orderGroup = gui.getOrdersPanel().getOrderGroup(position.getPositionID());
/*  727 */     OrderMessage orderMessage = orderGroup.getOpeningOrder();
/*  728 */     String openingId = null;
/*  729 */     openingId = orderMessage.getOrderId();
/*      */ 
/*  731 */     String ediatbleOrderId = null;
/*  732 */     if ((isOpenedFromSLTPMenu) && (orderGroup != null)) {
/*  733 */       if (StopOrderType.STOP_LOSS.equals(stopOrderType))
/*  734 */         ediatbleOrderId = orderGroup.getStopLossOrder().getOrderId();
/*  735 */       else if (StopOrderType.TAKE_PROFIT.equals(stopOrderType))
/*  736 */         ediatbleOrderId = orderGroup.getTakeProfitOrder().getOrderId();
/*      */     }
/*  738 */     if (StopEditDetached.getOpenedFrame(orderMessage.getOrderId()) == null)
/*  739 */       new StopEditDetached(orderGroup, stopOrderType, ediatbleOrderId, openingId, null);
/*      */   }
/*      */ 
/*      */   private void openEditDetachedPanel(PositionsTableModel model, int row, StopOrderType stopOrderType)
/*      */   {
/*  744 */     Position position = model.getPosition(row);
/*  745 */     if (StopOrderType.PART_CLOSE.equals(stopOrderType))
/*  746 */       new StopEditDetached(position.getOrderGroup(), stopOrderType);
/*      */   }
/*      */ 
/*      */   private void setupTable()
/*      */   {
/*  754 */     this.tablePane = new JScrollPane(this.table);
/*  755 */     this.tablePane.setUI(new TableScrollPaneUI(this.table));
/*  756 */     this.tablePane.getViewport().setBackground(GreedContext.GLOBAL_BACKGROUND);
/*  757 */     this.tablePane.setVerticalScrollBarPolicy(22);
/*  758 */     this.tablePane.setCorner("UPPER_RIGHT_CORNER", new ScrollPaneHeaderRenderer());
/*      */ 
/*  760 */     JButton ULcorner = new JButton("");
/*  761 */     this.tablePane.setCorner("UPPER_LEFT_CORNER", ULcorner);
/*  762 */     JButton LLcorner = new JButton("");
/*  763 */     this.tablePane.setCorner("LOWER_LEFT_CORNER", LLcorner);
/*      */ 
/*  765 */     PositionsTableModel model = (PositionsTableModel)this.table.getModel();
/*      */ 
/*  767 */     this.table.addMouseListener(new MouseAdapter() {
/*      */       public void mousePressed(MouseEvent e) {
/*  769 */         int selectedRow = PositionsPanel.this.table.rowAtPoint(new Point(e.getX(), e.getY()));
/*  770 */         if (selectedRow > -1)
/*  771 */           PositionsPanel.this.table.setRowSelectionInterval(selectedRow, selectedRow);
/*      */       }
/*      */     });
/*  776 */     this.table.getSelectionModel().addListSelectionListener(new ListSelectionListener(model) {
/*      */       public void valueChanged(ListSelectionEvent e) {
/*  778 */         int selectedRow = PositionsPanel.this.table.getRowIndexFromModel();
/*  779 */         if ((selectedRow > -1) && (selectedRow < PositionsPanel.this.table.getRowCount())) {
/*  780 */           ((ClientForm)GreedContext.get("clientGui")).getOrdersPanel().getOrdersTable().clearHighlight();
/*  781 */           Position position = this.val$model.getPosition(selectedRow);
/*  782 */           if (position != null) {
/*  783 */             ((ClientForm)GreedContext.get("clientGui")).getOrdersPanel().highlightOrderGroup(position.getPositionID());
/*      */           }
/*  785 */           PositionsPanel.this.enableMenuItems(position);
/*      */         }
/*      */       }
/*      */     });
/*  790 */     addColumnsComparators();
/*      */ 
/*  792 */     this.table.addMouseListener(new MouseAdapter() {
/*      */       public void mousePressed(MouseEvent e) {
/*  794 */         mayBeShowPopup(e);
/*      */       }
/*      */ 
/*      */       public void mouseReleased(MouseEvent e) {
/*  798 */         mayBeShowPopup(e);
/*      */       }
/*      */ 
/*      */       private void mayBeShowPopup(MouseEvent e) {
/*  802 */         if (GreedContext.isReadOnly()) {
/*  803 */           PositionsPanel.LOGGER.info("Operation is not available in view mode.");
/*  804 */           return;
/*      */         }
/*      */ 
/*  807 */         if (e.isPopupTrigger()) {
/*  808 */           PositionsTable table = (PositionsTable)e.getComponent();
/*  809 */           PositionsTableModel model = (PositionsTableModel)table.getModel();
/*      */ 
/*  811 */           int selectedRow = table.rowAtPoint(e.getPoint());
/*  812 */           if ((selectedRow <= -1) || (selectedRow >= table.getRowCount())) {
/*  813 */             return;
/*      */           }
/*  815 */           selectedRow = table.getRowSorter().convertRowIndexToModel(selectedRow);
/*      */ 
/*  817 */           if ((GreedContext.isGlobal()) || (GreedContext.isGlobalExtended())) {
/*  818 */             PositionsPanel.this.menuItemCA.setEnabled(true);
/*  819 */             PositionsPanel.this.menuItemMP.setVisible(false);
/*  820 */             PositionsPanel.this.menuItemSL.setEnabled(true);
/*  821 */             PositionsPanel.this.menuItemEditSL.setEnabled(false);
/*  822 */             PositionsPanel.this.menuItemCancelSL.setEnabled(false);
/*  823 */             PositionsPanel.this.menuItemTP.setEnabled(false);
/*  824 */             PositionsPanel.this.menuItemEditTP.setEnabled(false);
/*  825 */             PositionsPanel.this.menuItemCancelTP.setEnabled(false);
/*  826 */             PositionsPanel.this.menuItemCP.setEnabled(true);
/*  827 */             PositionsPanel.this.menuItemPCC.setEnabled(true);
/*  828 */             PositionsPanel.this.popup.show(table, e.getX(), e.getY());
/*  829 */             return;
/*      */           }
/*      */ 
/*  832 */           if ((selectedRow > -1) && (selectedRow < table.getRowCount())) {
/*  833 */             Position selectedPosition = model.getPosition(selectedRow);
/*  834 */             int pendingCount = 0;
/*  835 */             List selected = model.getSelectedPositions();
/*  836 */             for (Position position : selected) {
/*  837 */               if (!position.isInClosingState()) {
/*  838 */                 pendingCount++;
/*      */               }
/*      */             }
/*      */ 
/*  842 */             if (selectedPosition != null) {
/*  843 */               PositionsPanel.this.menuItemCA.setEnabled(0 != pendingCount);
/*  844 */               PositionsPanel.this.menuItemMP.setEnabled(1 < pendingCount);
/*  845 */               PositionsPanel.this.menuItemCP.setEnabled(!selectedPosition.isDisabled());
/*  846 */               PositionsPanel.this.menuItemPCC.setEnabled(!selectedPosition.isDisabled());
/*  847 */               ClientForm gui = (ClientForm)GreedContext.get("clientGui");
/*  848 */               OrderGroupMessage orderGroup = gui.getOrdersPanel().getOrderGroup(selectedPosition.getPositionID());
/*  849 */               if (orderGroup != null) {
/*  850 */                 OrderMessage stopOrder = orderGroup.getStopLossOrder();
/*      */ 
/*  853 */                 if (null != stopOrder) {
/*  854 */                   OrderState state = stopOrder.getOrderState();
/*  855 */                   PositionsPanel.this.menuItemSL.setEnabled((!selectedPosition.isDisabled()) && (!orderGroup.isOcoMerge()) && ((OrderState.CANCELLED.equals(state)) || (OrderState.REJECTED.equals(state))));
/*      */                 }
/*      */                 else
/*      */                 {
/*  862 */                   PositionsPanel.this.menuItemSL.setEnabled((!selectedPosition.isDisabled()) && (!orderGroup.isOcoMerge()));
/*      */                 }
/*      */ 
/*  869 */                 boolean isSLOpened = orderGroup.getStopLossOrder() != null;
/*  870 */                 PositionsPanel.this.menuItemEditSL.setEnabled(isSLOpened);
/*  871 */                 PositionsPanel.this.menuItemCancelSL.setEnabled(isSLOpened);
/*      */ 
/*  873 */                 stopOrder = orderGroup.getTakeProfitOrder();
/*  874 */                 if (null != stopOrder) {
/*  875 */                   OrderState state = stopOrder.getOrderState();
/*  876 */                   PositionsPanel.this.menuItemTP.setEnabled((!selectedPosition.isDisabled()) && (!orderGroup.isOcoMerge()) && ((OrderState.CANCELLED.equals(state)) || (OrderState.REJECTED.equals(state))));
/*      */                 }
/*      */                 else
/*      */                 {
/*  883 */                   PositionsPanel.this.menuItemTP.setEnabled((!selectedPosition.isDisabled()) && (!orderGroup.isOcoMerge()));
/*      */                 }
/*      */ 
/*  889 */                 boolean isTPOpened = orderGroup.getTakeProfitOrder() != null;
/*  890 */                 PositionsPanel.this.menuItemEditTP.setEnabled(isTPOpened);
/*  891 */                 PositionsPanel.this.menuItemCancelTP.setEnabled(isTPOpened);
/*      */ 
/*  893 */                 PositionsPanel.this.popup.show(table, e.getX(), e.getY());
/*      */               } else {
/*  895 */                 PositionsPanel.LOGGER.warn("GROUP NOT FOUND IN OPanel!");
/*      */               }
/*      */             }
/*      */           }
/*      */         }
/*  900 */         if (GreedContext.isContest()) {
/*  901 */           PositionsPanel.this.menuItemPCC.setVisible(false);
/*  902 */           PositionsPanel.this.menuItemSL.setEnabled(false);
/*  903 */           PositionsPanel.this.menuItemEditSL.setEnabled(true);
/*  904 */           PositionsPanel.this.menuItemCancelSL.setEnabled(false);
/*  905 */           PositionsPanel.this.menuItemTP.setEnabled(false);
/*  906 */           PositionsPanel.this.menuItemEditTP.setEnabled(true);
/*  907 */           PositionsPanel.this.menuItemCancelTP.setEnabled(false);
/*  908 */           PositionsPanel.this.menuItemCA.setVisible(false);
/*  909 */           PositionsPanel.this.menuItemMP.setVisible(false);
/*      */         }
/*      */       }
/*      */     });
/*  914 */     ((ClientSettingsStorage)GreedContext.get("settingsStorage")).restoreTableColumns(this.table.getTableId(), this.table.getColumnModel());
/*      */   }
/*      */ 
/*      */   private void enableMenuItems(Position position) {
/*  918 */     if (null == position) {
/*  919 */       this.menuItemSL.setEnabled(false);
/*  920 */       this.menuItemCancelSL.setEnabled(false);
/*  921 */       this.menuItemTP.setEnabled(false);
/*  922 */       this.menuItemCancelTP.setEnabled(false);
/*  923 */       return;
/*      */     }
/*  925 */     this.menuItemSL.setEnabled(true);
/*  926 */     this.menuItemCancelSL.setEnabled(true);
/*  927 */     this.menuItemTP.setEnabled(true);
/*  928 */     this.menuItemCancelTP.setEnabled(true);
/*      */   }
/*      */ 
/*      */   public void onMarketState(CurrencyMarketWrapper marketState) {
/*  932 */     PositionsTableModel model = (PositionsTableModel)this.table.getModel();
/*  933 */     int row = this.table.getSelectedRow();
/*  934 */     model.onMarketState(marketState);
/*  935 */     if ((row > -1) && (row < this.table.getRowCount()))
/*      */       try {
/*  937 */         this.table.setRowSelectionInterval(row, row);
/*      */       } catch (Exception iae) {
/*  939 */         LOGGER.warn("Row index is out of bounds : " + row + " " + iae.getMessage());
/*      */       }
/*      */   }
/*      */ 
/*      */   public void updatePositions(OrderGroupMessage orderGroup)
/*      */   {
/*  945 */     this.table.getPositionsModel().updatePositions(orderGroup);
/*      */   }
/*      */ 
/*      */   public void clearPositionSelection()
/*      */   {
/*  959 */     this.table.getSelectionModel().clearSelection();
/*      */   }
/*      */ 
/*      */   public PositionsTable getTable() {
/*  963 */     return this.table;
/*      */   }
/*      */ 
/*      */   public void setStopOrdersVisible(boolean visible) {
/*  967 */     if ((this.popup.isVisible()) && (!visible)) {
/*  968 */       this.popup.setVisible(false);
/*      */     }
/*  970 */     this.menuItemSL.setVisible(visible);
/*  971 */     this.menuItemCancelSL.setVisible(visible);
/*  972 */     if (!GreedContext.isContest()) this.menuItemEditSL.setVisible(visible);
/*  973 */     this.menuItemTP.setVisible(visible);
/*  974 */     if (!GreedContext.isContest()) this.menuItemEditTP.setVisible(visible);
/*  975 */     this.menuItemCancelTP.setVisible(visible);
/*  976 */     this.menuSeparatorSL.setVisible(visible);
/*  977 */     this.menuSeparatorTP.setVisible(visible);
/*      */   }
/*      */ 
/*      */   public AbstractAction getClosePositionAction() {
/*  981 */     return this.closePositionActionCopy;
/*      */   }
/*      */ 
/*      */   private void resetHeaderCheckBox() {
/*  985 */     TableColumn checkColumn = this.table.getTableHeader().getColumnModel().getColumn(this.table.convertColumnIndexToView(0));
/*  986 */     CheckBoxHeader header = (CheckBoxHeader)checkColumn.getHeaderRenderer();
/*  987 */     if (header != null) {
/*  988 */       header.setSelected(false);
/*      */     }
/*  990 */     this.table.getTableHeader().repaint();
/*      */   }
/*      */ 
/*      */   public Position getPositionsByGroupId(String orderGroupId) {
/*  994 */     PositionsTableModel model = (PositionsTableModel)this.table.getModel();
/*  995 */     return model.getPosition(orderGroupId);
/*      */   }
/*      */ 
/*      */   public void addHeader(HeaderPanel header) {
/*  999 */     add(header, 0);
/* 1000 */     this.header = header;
/*      */   }
/*      */ 
/*      */   public HeaderPanel getHeader() {
/* 1004 */     return this.header;
/*      */   }
/*      */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.positions.PositionsPanel
 * JD-Core Version:    0.6.0
 */