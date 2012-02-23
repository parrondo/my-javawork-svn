/*      */ package com.dukascopy.dds2.greed.gui.component.orders;
/*      */ 
/*      */ import com.dukascopy.dds2.greed.GreedContext;
/*      */ import com.dukascopy.dds2.greed.actions.CancelOrderAction;
/*      */ import com.dukascopy.dds2.greed.actions.OcoOrderGroupingAction;
/*      */ import com.dukascopy.dds2.greed.gui.ClientForm;
/*      */ import com.dukascopy.dds2.greed.gui.GuiUtilsAndConstants;
/*      */ import com.dukascopy.dds2.greed.gui.SwingWorker;
/*      */ import com.dukascopy.dds2.greed.gui.component.HeaderPanel;
/*      */ import com.dukascopy.dds2.greed.gui.component.detached.StopEditDetached;
/*      */ import com.dukascopy.dds2.greed.gui.component.dialog.CustomRequestDialog;
/*      */ import com.dukascopy.dds2.greed.gui.component.positions.PositionsPanel;
/*      */ import com.dukascopy.dds2.greed.gui.component.table.CheckBoxHeader;
/*      */ import com.dukascopy.dds2.greed.gui.component.table.ScrollPaneHeaderRenderer;
/*      */ import com.dukascopy.dds2.greed.gui.component.table.TableSorter;
/*      */ import com.dukascopy.dds2.greed.gui.l10n.components.JLocalizableMenuItem;
/*      */ import com.dukascopy.dds2.greed.gui.settings.ClientSettingsStorage;
/*      */ import com.dukascopy.dds2.greed.model.StopOrderType;
/*      */ import com.dukascopy.dds2.greed.util.EmergencyLogger;
/*      */ import com.dukascopy.dds2.greed.util.OrderMessageUtils;
/*      */ import com.dukascopy.dds2.greed.util.OrderUtils;
/*      */ import com.dukascopy.transport.common.model.type.Money;
/*      */ import com.dukascopy.transport.common.model.type.OrderDirection;
/*      */ import com.dukascopy.transport.common.model.type.OrderSide;
/*      */ import com.dukascopy.transport.common.model.type.OrderState;
/*      */ import com.dukascopy.transport.common.msg.group.OrderGroupMessage;
/*      */ import com.dukascopy.transport.common.msg.group.OrderMessage;
/*      */ import java.awt.Dimension;
/*      */ import java.awt.Point;
/*      */ import java.awt.event.ActionEvent;
/*      */ import java.awt.event.MouseAdapter;
/*      */ import java.awt.event.MouseEvent;
/*      */ import java.math.BigDecimal;
/*      */ import java.util.ArrayList;
/*      */ import java.util.HashMap;
/*      */ import java.util.HashSet;
/*      */ import java.util.Iterator;
/*      */ import java.util.List;
/*      */ import java.util.Map;
/*      */ import java.util.Set;
/*      */ import java.util.TreeSet;
/*      */ import javax.swing.AbstractAction;
/*      */ import javax.swing.Action;
/*      */ import javax.swing.JPanel;
/*      */ import javax.swing.JPopupMenu;
/*      */ import javax.swing.JPopupMenu.Separator;
/*      */ import javax.swing.JScrollPane;
/*      */ import javax.swing.JSeparator;
/*      */ import javax.swing.JViewport;
/*      */ import javax.swing.table.JTableHeader;
/*      */ import javax.swing.table.TableColumn;
/*      */ import javax.swing.table.TableColumnModel;
/*      */ import org.slf4j.Logger;
/*      */ import org.slf4j.LoggerFactory;
/*      */ 
/*      */ public class OrdersPanel extends JPanel
/*      */ {
/*   79 */   private static final Logger LOGGER = LoggerFactory.getLogger(OrdersPanel.class);
/*      */   public static final String ID_JT_ORDERPANEL = "ID_JT_ORDERSPANEL";
/*      */   public static final String ID_JMENU_EDIT = "ID_JMENU_EDIT";
/*      */   public static final String ID_JMENU_CANCEL = "ID_JMENU_CANCEL";
/*      */   public static final String ID_JMENU_OCO_GROUP = "ID_JMENU_GROUP";
/*      */   public static final String ID_JMENU_OCO_UNGROUP = "ID_JMENU_OCO_UNGROUP";
/*      */   private OrdersTable ordersTable;
/*      */   private OrderCommonTableModel notm;
/*   90 */   private final JPopupMenu popupMenu = new JPopupMenu();
/*   91 */   private final JLocalizableMenuItem editItem = new JLocalizableMenuItem();
/*   92 */   private final JLocalizableMenuItem cancelItem = new JLocalizableMenuItem();
/*   93 */   private final JLocalizableMenuItem cancelSelectedItem = new JLocalizableMenuItem();
/*   94 */   private JSeparator mainMenuSeparator = new JPopupMenu.Separator();
/*      */   private JLocalizableMenuItem menuItemSl;
/*      */   private JLocalizableMenuItem menuItemEditSl;
/*      */   private JLocalizableMenuItem menuItemCancelSl;
/*   98 */   private JSeparator SLMenuSeparator = new JPopupMenu.Separator();
/*      */   private JLocalizableMenuItem menuItemTp;
/*      */   private JLocalizableMenuItem menuItemEditTp;
/*      */   private JLocalizableMenuItem menuItemCancelTp;
/*  102 */   private JSeparator TPMenuSeparator = new JPopupMenu.Separator();
/*  103 */   private JSeparator CSMenuSeparator = new JPopupMenu.Separator();
/*      */   private JScrollPane tablePane;
/*      */   private JLocalizableMenuItem menuItemOco;
/*      */   private JLocalizableMenuItem menuItemOcoOff;
/*      */   private JLocalizableMenuItem menuItemCS;
/*      */   private JLocalizableMenuItem menuItemEditCS;
/*      */   private JLocalizableMenuItem menuItemCancelCS;
/*      */   private JLocalizableMenuItem menuItemCL;
/*      */   private JLocalizableMenuItem menuItemEditCL;
/*      */   private JLocalizableMenuItem menuItemCancelCL;
/*      */   public static final String ID_JPOPUP_MENU = "ID_JPOPUP_MENU";
/*      */   private HeaderPanel header;
/*      */ 
/*      */   public OrdersPanel()
/*      */   {
/*  125 */     OrderTableModel.reinitColumnIndexs();
/*  126 */     setName("ID_JT_ORDERSPANEL");
/*      */   }
/*      */ 
/*      */   private void initGlobalModeItems()
/*      */   {
/*  131 */     initCloseStopItems();
/*  132 */     initCloseLimitItems();
/*      */   }
/*      */ 
/*      */   private AbstractAction getAddStopOrderAction(StopOrderType stopOrderType, String key) {
/*  136 */     return new AbstractAction(key, stopOrderType) {
/*      */       public void actionPerformed(ActionEvent e) {
/*  138 */         TableSorter sorter = (TableSorter)OrdersPanel.this.ordersTable.getModel();
/*  139 */         OrderCommonTableModel model = (OrderCommonTableModel)sorter.getTableModel();
/*  140 */         int row = OrdersPanel.this.ordersTable.getSelectedRow();
/*      */ 
/*  142 */         int rowTranslated = sorter.modelIndex(row);
/*  143 */         if ((rowTranslated < 0) && (rowTranslated >= model.getRowCount())) {
/*  144 */           if (OrdersPanel.LOGGER.isDebugEnabled()) {
/*  145 */             OrdersPanel.LOGGER.warn("WARN: the row was not found in sorter");
/*      */           }
/*  147 */           return;
/*      */         }
/*      */ 
/*  150 */         if (rowTranslated != -1)
/*  151 */           OrdersPanel.this.openEditDetachedPanel(model, rowTranslated, this.val$stopOrderType);
/*      */       } } ;
/*      */   }
/*      */ 
/*      */   private AbstractAction getEditStopOrderAction(StopOrderType stopOrderType, String key) {
/*  157 */     return new AbstractAction(key, stopOrderType)
/*      */     {
/*      */       public void actionPerformed(ActionEvent e) {
/*  160 */         TableSorter sorter = (TableSorter)OrdersPanel.this.ordersTable.getModel();
/*  161 */         OrderCommonTableModel model = (OrderCommonTableModel)sorter.getTableModel();
/*  162 */         int row = OrdersPanel.this.ordersTable.getSelectedRow();
/*  163 */         if ((row < 0) && (row >= OrdersPanel.this.ordersTable.getRowCount())) {
/*  164 */           if (OrdersPanel.LOGGER.isDebugEnabled()) {
/*  165 */             OrdersPanel.LOGGER.warn("WARN: No row was selected");
/*      */           }
/*  167 */           return;
/*      */         }
/*  169 */         int rowTranslated = sorter.modelIndex(row);
/*  170 */         if ((rowTranslated < 0) && (rowTranslated >= model.getRowCount())) {
/*  171 */           if (OrdersPanel.LOGGER.isDebugEnabled()) {
/*  172 */             OrdersPanel.LOGGER.warn("WARN: the row was not found in sorter");
/*      */           }
/*  174 */           return;
/*      */         }
/*  176 */         OrderMessage order = model.getOrder(rowTranslated);
/*  177 */         OrderGroupMessage orderGroup = model.getGroup(rowTranslated);
/*  178 */         if (order == null) {
/*  179 */           return;
/*      */         }
/*  181 */         if (StopEditDetached.getOpenedFrame(order.getOrderId()) == null) {
/*  182 */           String orderId = OrdersPanel.this.getOrderIdBySOType(this.val$stopOrderType, orderGroup, order);
/*  183 */           if (orderId != null) {
/*  184 */             new StopEditDetached(orderGroup, this.val$stopOrderType, orderId);
/*      */           }
/*      */         }
/*  187 */         if (model.getSelectedOrders().size() > 0) {
/*  188 */           model.getSelectedOrders().clear();
/*  189 */           OrdersPanel.this.uncheckHeader();
/*      */         }
/*      */       } } ;
/*      */   }
/*      */ 
/*      */   private AbstractAction getCancelStopOrderAction(StopOrderType stopOrderType, String key) {
/*  195 */     return new AbstractAction(key, stopOrderType)
/*      */     {
/*      */       public void actionPerformed(ActionEvent e) {
/*  198 */         TableSorter sorter = (TableSorter)OrdersPanel.this.ordersTable.getModel();
/*  199 */         OrderCommonTableModel model = (OrderCommonTableModel)sorter.getTableModel();
/*      */ 
/*  201 */         int row = OrdersPanel.this.ordersTable.getSelectedRow();
/*      */ 
/*  203 */         int rowTranslatedId = sorter.modelIndex(row);
/*  204 */         if ((rowTranslatedId < 0) && (rowTranslatedId >= model.getRowCount())) {
/*  205 */           if (OrdersPanel.LOGGER.isDebugEnabled()) {
/*  206 */             OrdersPanel.LOGGER.warn("WARN: the row was not found in sorter");
/*      */           }
/*  208 */           return;
/*      */         }
/*      */ 
/*  211 */         if (rowTranslatedId != -1) {
/*  212 */           OrderMessage om = null;
/*  213 */           OrderGroupMessage ogm = null;
/*  214 */           if ((GreedContext.isGlobal()) || (GreedContext.isGlobalExtended()))
/*  215 */             om = model.getOrder(rowTranslatedId);
/*      */           else {
/*  217 */             ogm = model.getGroupByOrderId(model.getOrder(rowTranslatedId).getOrderId());
/*      */           }
/*      */ 
/*  220 */           String orderId = OrdersPanel.this.getOrderIdBySOType(this.val$stopOrderType, ogm, om);
/*  221 */           if (orderId != null)
/*  222 */             OrderUtils.getInstance().cancelOrder(orderId);
/*      */         }
/*      */       }
/*      */     };
/*      */   }
/*      */ 
/*      */   private String getOrderIdBySOType(StopOrderType stopOrderType, OrderGroupMessage orderGroup, OrderMessage orderMessage)
/*      */   {
/*  231 */     if (((orderGroup == null) && (!GreedContext.isGlobalExtended())) || ((orderMessage == null) && (GreedContext.isGlobalExtended())) || (stopOrderType == null))
/*      */     {
/*  233 */       return null;
/*      */     }
/*  235 */     String orderId = null;
/*  236 */     if ((StopOrderType.IFD_STOP.equals(stopOrderType)) && ((OrderMessageUtils.getCsOrder(orderMessage) != null) || ((orderMessage != null) && (orderMessage.isIfdStop()))))
/*      */     {
/*  240 */       orderId = orderMessage.isIfdStop() ? orderMessage.getOrderId() : OrderMessageUtils.getCsOrder(orderMessage).getOrderId();
/*      */     }
/*  244 */     else if (((StopOrderType.IFD_LIMIT.equals(stopOrderType)) && (OrderMessageUtils.getClOrder(orderMessage) != null)) || ((orderMessage != null) && (orderMessage.isIfdLimit())))
/*      */     {
/*  248 */       orderId = orderMessage.isIfdLimit() ? orderMessage.getOrderId() : OrderMessageUtils.getClOrder(orderMessage).getOrderId();
/*      */     }
/*  252 */     else if ((StopOrderType.STOP_LOSS.equals(stopOrderType)) && (orderGroup != null) && (orderGroup.getStopLossOrder() != null))
/*      */     {
/*  254 */       orderId = orderGroup.getStopLossOrder().getOrderId();
/*      */     }
/*  256 */     else if ((StopOrderType.TAKE_PROFIT.equals(stopOrderType)) && (orderGroup != null) && (orderGroup.getTakeProfitOrder() != null))
/*      */     {
/*  258 */       orderId = orderGroup.getTakeProfitOrder().getOrderId();
/*      */     }
/*      */ 
/*  262 */     return orderId;
/*      */   }
/*      */ 
/*      */   private void initCloseStopItems()
/*      */   {
/*  267 */     this.menuItemCS = new JLocalizableMenuItem("item.add.ifd.stop");
/*  268 */     this.menuItemCS.setAction(getAddStopOrderAction(StopOrderType.IFD_STOP, "item.add.ifd.stop"));
/*      */ 
/*  270 */     this.menuItemEditCS = new JLocalizableMenuItem("item.edit.ifd.stop");
/*  271 */     this.menuItemEditCS.setAction(getEditStopOrderAction(StopOrderType.IFD_STOP, "item.edit.ifd.stop"));
/*      */ 
/*  273 */     this.menuItemCancelCS = new JLocalizableMenuItem("item.cancel.ifd.stop");
/*  274 */     this.menuItemCancelCS.setAction(getCancelStopOrderAction(StopOrderType.IFD_STOP, "item.cancel.ifd.stop"));
/*      */   }
/*      */ 
/*      */   private void initCloseLimitItems() {
/*  278 */     this.menuItemCL = new JLocalizableMenuItem("item.add.ifd.limit");
/*  279 */     this.menuItemCL.setAction(getAddStopOrderAction(StopOrderType.IFD_LIMIT, "item.add.ifd.limit"));
/*      */ 
/*  281 */     this.menuItemEditCL = new JLocalizableMenuItem("item.edit.ifd.limit");
/*  282 */     this.menuItemEditCL.setAction(getEditStopOrderAction(StopOrderType.IFD_LIMIT, "item.edit.ifd.limit"));
/*      */ 
/*  284 */     this.menuItemCancelCL = new JLocalizableMenuItem("item.cancel.ifd.limit");
/*  285 */     this.menuItemCancelCL.setAction(getCancelStopOrderAction(StopOrderType.IFD_LIMIT, "item.cancel.ifd.limit"));
/*      */   }
/*      */ 
/*      */   private void initStopLossItems() {
/*  289 */     this.menuItemSl = new JLocalizableMenuItem("item.add.stop.loss");
/*  290 */     this.menuItemSl.setAction(getAddStopOrderAction(StopOrderType.STOP_LOSS, "item.add.stop.loss"));
/*      */ 
/*  292 */     this.menuItemEditSl = new JLocalizableMenuItem("item.edit.stop.loss");
/*  293 */     this.menuItemEditSl.setAction(getEditStopOrderAction(StopOrderType.STOP_LOSS, "item.edit.stop.loss"));
/*      */ 
/*  295 */     this.menuItemCancelSl = new JLocalizableMenuItem("item.cancel.stop.loss");
/*  296 */     this.menuItemCancelSl.setAction(getCancelStopOrderAction(StopOrderType.STOP_LOSS, "item.cancel.stop.loss"));
/*      */   }
/*      */ 
/*      */   private void initTakeProfitItems() {
/*  300 */     this.menuItemTp = new JLocalizableMenuItem("item.add.take.profit");
/*  301 */     this.menuItemTp.setAction(getAddStopOrderAction(StopOrderType.TAKE_PROFIT, "item.add.take.profit"));
/*      */ 
/*  303 */     this.menuItemEditTp = new JLocalizableMenuItem("item.edit.take.profit");
/*  304 */     this.menuItemEditTp.setAction(getEditStopOrderAction(StopOrderType.TAKE_PROFIT, "item.edit.take.profit"));
/*      */ 
/*  306 */     this.menuItemCancelTp = new JLocalizableMenuItem("item.cancel.take.profit");
/*  307 */     this.menuItemCancelTp.setAction(getCancelStopOrderAction(StopOrderType.TAKE_PROFIT, "item.cancel.take.profit"));
/*      */   }
/*      */ 
/*      */   public void updateOrderGroup(OrderGroupMessage orderGroup)
/*      */   {
/*  315 */     TableSorter sorter = (TableSorter)this.ordersTable.getModel();
/*  316 */     OrderCommonTableModel model = (OrderCommonTableModel)sorter.getTableModel();
/*      */ 
/*  318 */     if ((!GreedContext.isGlobal()) && (!GreedContext.isGlobalExtended())) {
/*  319 */       model.updateTable(orderGroup);
/*  320 */       this.ordersTable.refreshHighlight();
/*      */     }
/*      */ 
/*  323 */     if ((orderGroup.getOpeningOrder() != null) && (orderGroup.getOpeningOrder().getOrderState().equals(OrderState.FILLED)) && (!orderGroup.isInitState()));
/*  332 */     ClientForm clientForm = (ClientForm)GreedContext.get("clientGui");
/*  333 */     clientForm.getPositionsPanel().updatePositions(orderGroup);
/*      */   }
/*      */ 
/*      */   public OrderGroupMessage getOrderGroup(String orderGroupId)
/*      */   {
/*  342 */     TableSorter sorter = (TableSorter)this.ordersTable.getModel();
/*  343 */     OrderCommonTableModel model = (OrderCommonTableModel)sorter.getTableModel();
/*  344 */     return model.getGroup(orderGroupId);
/*      */   }
/*      */ 
/*      */   public void highlightOrderGroup(String higlightetGroupId)
/*      */   {
/*  352 */     this.ordersTable.highlightOrderGroup(higlightetGroupId);
/*      */   }
/*      */ 
/*      */   public OrdersTable getOrdersTable()
/*      */   {
/*  360 */     return this.ordersTable;
/*      */   }
/*      */ 
/*      */   public OrderCommonTableModel getModel() {
/*  364 */     return this.notm;
/*      */   }
/*      */ 
/*      */   public void build()
/*      */   {
/*  371 */     setMinimumSize(new Dimension(0, 0));
/*  372 */     if (GreedContext.isStrategyAllowed()) {
/*  373 */       setPreferredSize(GuiUtilsAndConstants.getOneQuarterOfDisplayDimension());
/*      */     }
/*  375 */     setupTable();
/*  376 */     add(this.tablePane);
/*      */   }
/*      */ 
/*      */   private void setupTable()
/*      */   {
/*  383 */     if ((GreedContext.isGlobal()) || (GreedContext.isGlobalExtended()))
/*  384 */       this.notm = new GlobalOrderTableModel();
/*      */     else {
/*  386 */       this.notm = new OrderTableModel();
/*      */     }
/*      */ 
/*  389 */     TableSorter sorter = new TableSorter(this.notm);
/*  390 */     List nonSortableColumnList = new ArrayList();
/*  391 */     nonSortableColumnList.add(Integer.valueOf(OrderTableModel.COLUMN_CHECK));
/*  392 */     sorter.setNoSortableColumns(nonSortableColumnList);
/*  393 */     this.ordersTable = new OrdersTable(sorter);
/*      */ 
/*  395 */     this.tablePane = new JScrollPane(this.ordersTable);
/*  396 */     this.tablePane.getViewport().setBackground(GreedContext.GLOBAL_BACKGROUND);
/*  397 */     this.tablePane.setVerticalScrollBarPolicy(22);
/*  398 */     this.tablePane.setCorner("UPPER_RIGHT_CORNER", new ScrollPaneHeaderRenderer());
/*      */ 
/*  400 */     sorter.setTableHeader(this.ordersTable.getTableHeader());
/*      */ 
/*  402 */     this.popupMenu.setName("ID_JPOPUP_MENU");
/*  403 */     this.cancelItem.setName("ID_JMENU_CANCEL");
/*  404 */     this.editItem.setName("ID_JMENU_EDIT");
/*      */ 
/*  406 */     initGlobalModeItems();
/*  407 */     initStopLossItems();
/*  408 */     initTakeProfitItems();
/*      */ 
/*  410 */     Action cancelAction = new AbstractAction("item.cancel.order") {
/*      */       public void actionPerformed(ActionEvent e) {
/*  412 */         TableSorter sorter = (TableSorter)OrdersPanel.this.ordersTable.getModel();
/*  413 */         OrderCommonTableModel model = (OrderCommonTableModel)sorter.getTableModel();
/*  414 */         int row = OrdersPanel.this.ordersTable.getSelectedRow();
/*  415 */         if ((row < 0) && (row >= OrdersPanel.this.ordersTable.getRowCount())) {
/*  416 */           if (OrdersPanel.LOGGER.isDebugEnabled()) {
/*  417 */             OrdersPanel.LOGGER.warn("WARN: Now row was selected");
/*      */           }
/*  419 */           return;
/*      */         }
/*  421 */         int rowTranslated = sorter.modelIndex(row);
/*  422 */         if ((rowTranslated < 0) && (rowTranslated >= model.getRowCount())) {
/*  423 */           if (OrdersPanel.LOGGER.isDebugEnabled()) {
/*  424 */             OrdersPanel.LOGGER.warn("WARN: the row was not found in sorter");
/*      */           }
/*  426 */           return;
/*      */         }
/*  428 */         OrderMessage order = model.getOrder(rowTranslated);
/*  429 */         OrderGroupMessage orderGroup = model.getGroup(rowTranslated);
/*      */ 
/*  431 */         if ((orderGroup != null) && (order != null)) {
/*  432 */           OrdersPanel.this.fireCancelOrder(orderGroup, order);
/*      */         }
/*  434 */         model.getSelectedOrders().remove(order);
/*      */       }
/*      */     };
/*  438 */     Action editAction = new AbstractAction("item.edit.order") {
/*      */       public void actionPerformed(ActionEvent e) {
/*  440 */         TableSorter sorter = (TableSorter)OrdersPanel.this.ordersTable.getModel();
/*  441 */         OrderCommonTableModel model = (OrderCommonTableModel)sorter.getTableModel();
/*  442 */         int row = OrdersPanel.this.ordersTable.getSelectedRow();
/*  443 */         if ((row < 0) && (row >= OrdersPanel.this.ordersTable.getRowCount())) {
/*  444 */           if (OrdersPanel.LOGGER.isDebugEnabled()) {
/*  445 */             OrdersPanel.LOGGER.warn("WARN: No row was selected");
/*      */           }
/*  447 */           return;
/*      */         }
/*  449 */         int rowTranslated = sorter.modelIndex(row);
/*  450 */         if ((rowTranslated < 0) && (rowTranslated >= model.getRowCount())) {
/*  451 */           if (OrdersPanel.LOGGER.isDebugEnabled()) {
/*  452 */             OrdersPanel.LOGGER.warn("WARN: the row was not found in sorter");
/*      */           }
/*  454 */           return;
/*      */         }
/*  456 */         OrderMessage order = model.getOrder(rowTranslated);
/*  457 */         OrderGroupMessage orderGroup = model.getGroup(rowTranslated);
/*  458 */         if (order == null) {
/*  459 */           return;
/*      */         }
/*  461 */         if (order.isPlaceOffer()) {
/*  462 */           CustomRequestDialog dialog = new CustomRequestDialog(order, null);
/*  463 */           dialog.setLocationRelativeTo(OrdersPanel.this.editItem);
/*  464 */           dialog.setVisible(true);
/*      */         } else {
/*  466 */           if (orderGroup == null)
/*  467 */             return;
/*  468 */           StopOrderType stopOrderType = StopOrderType.OPEN_IF;
/*  469 */           if (order.isStopLoss()) {
/*  470 */             stopOrderType = StopOrderType.STOP_LOSS;
/*      */           }
/*  472 */           if (order.isTakeProfit()) {
/*  473 */             stopOrderType = StopOrderType.TAKE_PROFIT;
/*      */           }
/*      */ 
/*  476 */           if (StopEditDetached.getOpenedFrame(order.getOrderId()) == null) {
/*  477 */             new StopEditDetached(orderGroup, stopOrderType, order.getOrderId());
/*      */           }
/*      */         }
/*  480 */         if (model.getSelectedOrders().size() > 0) {
/*  481 */           model.getSelectedOrders().clear();
/*  482 */           OrdersPanel.this.uncheckHeader();
/*      */         }
/*      */       }
/*      */     };
/*  487 */     Action cancelSelectedAction = new AbstractAction("item.cancel.selected")
/*      */     {
/*      */       public void actionPerformed(ActionEvent e) {
/*  490 */         OrdersPanel.this.fireCancelSelectedOrder(OrdersPanel.this.notm);
/*      */       }
/*      */     };
/*  495 */     this.ordersTable.addMouseListener(new MouseAdapter() {
/*      */       public void mousePressed(MouseEvent e) {
/*  497 */         maybeShowPopup(e);
/*      */       }
/*      */ 
/*      */       public void mouseClicked(MouseEvent e) {
/*  501 */         maybeShowPopup(e);
/*  502 */         maybeHighlightOco(e);
/*      */       }
/*      */ 
/*      */       private void maybeHighlightOco(MouseEvent e) {
/*  506 */         OrdersPanel.this.ordersTable.repaint();
/*  507 */         int selectedRow = OrdersPanel.this.ordersTable.rowAtPoint(new Point(e.getX(), e.getY()));
/*  508 */         OrdersPanel.this.ordersTable.setRowSelectionInterval(selectedRow, selectedRow);
/*  509 */         TableSorter sorter = (TableSorter)OrdersPanel.this.ordersTable.getModel();
/*  510 */         OrderCommonTableModel model = (OrderCommonTableModel)sorter.getTableModel();
/*      */ 
/*  512 */         int rowTranslated = sorter.modelIndex(selectedRow);
/*  513 */         if ((rowTranslated < 0) && (rowTranslated >= model.getRowCount())) {
/*  514 */           if (OrdersPanel.LOGGER.isDebugEnabled()) {
/*  515 */             OrdersPanel.LOGGER.warn("WARN: the row was not found in sorter");
/*      */           }
/*  517 */           return;
/*      */         }
/*  519 */         OrderMessage order = model.getOrder(rowTranslated);
/*  520 */         OrderGroupMessage ogm = model.getGroup(rowTranslated);
/*      */ 
/*  522 */         OrdersPanel.LOGGER.debug("orderId=" + order + " \nogm=" + ogm);
/*      */       }
/*      */ 
/*      */       public void mouseReleased(MouseEvent e) {
/*  526 */         maybeShowPopup(e);
/*      */       }
/*      */ 
/*      */       private void maybeShowPopup(MouseEvent e) {
/*  530 */         if (GreedContext.isReadOnly()) {
/*  531 */           OrdersPanel.LOGGER.info("Operation is not available in view mode.");
/*  532 */           return;
/*      */         }
/*  534 */         if (e.isPopupTrigger()) {
/*  535 */           int selectedRow = OrdersPanel.this.ordersTable.rowAtPoint(new Point(e.getX(), e.getY()));
/*  536 */           OrdersPanel.this.ordersTable.setRowSelectionInterval(selectedRow, selectedRow);
/*  537 */           TableSorter sorter = (TableSorter)OrdersPanel.this.ordersTable.getModel();
/*  538 */           OrderCommonTableModel model = (OrderCommonTableModel)sorter.getTableModel();
/*      */ 
/*  540 */           int rowTranslated = sorter.modelIndex(selectedRow);
/*  541 */           if ((rowTranslated < 0) && (rowTranslated >= model.getRowCount())) {
/*  542 */             if (OrdersPanel.LOGGER.isDebugEnabled()) {
/*  543 */               OrdersPanel.LOGGER.warn("WARN: the row was not found in sorter");
/*      */             }
/*  545 */             return;
/*      */           }
/*      */ 
/*  548 */           OrderMessage order = model.getOrder(rowTranslated);
/*  549 */           OrderGroupMessage ogm = model.getGroup(rowTranslated);
/*      */ 
/*  551 */           boolean cancelEnabled = ((OrderState.PENDING == order.getOrderState()) || (order.isPlaceOffer())) && (model.getSelectedOrders().size() <= 1);
/*      */ 
/*  555 */           boolean editEnabled = ((OrderState.PENDING == order.getOrderState()) || (order.isPlaceOffer())) && (model.getSelectedOrders().size() <= 1);
/*      */ 
/*  559 */           if (GreedContext.isContest()) {
/*  560 */             OrderMessage openingOrder = ogm.getOpeningOrder();
/*  561 */             if (openingOrder == null) {
/*  562 */               OrdersPanel.LOGGER.warn("Opening order dont exist");
/*  563 */               return;
/*      */             }
/*      */ 
/*  566 */             cancelEnabled = (cancelEnabled) && (!order.isStopLoss()) && (!order.isTakeProfit());
/*      */ 
/*  569 */             editEnabled = false;
/*      */           }
/*      */ 
/*  573 */           OrdersPanel.this.cancelItem.setEnabled(cancelEnabled);
/*  574 */           OrdersPanel.this.cancelItem.setName("ID_JMENU_CANCEL");
/*      */ 
/*  576 */           OrdersPanel.this.editItem.setEnabled(editEnabled);
/*  577 */           OrdersPanel.this.editItem.setName("ID_JMENU_EDIT");
/*      */ 
/*  579 */           OrdersPanel.this.cancelSelectedItem.setEnabled((model.getSelectedOrders().size() > 0) && (!GreedContext.isContest()));
/*      */ 
/*  581 */           OrdersPanel.this.menuItemSl.setEnabled((order.isOpening()) && (OrderMessageUtils.getSlOrder(ogm, order) == null) && (model.getSelectedOrders().size() <= 1) && (!GreedContext.isGlobal()) && (!GreedContext.isGlobalExtended()) && (!GreedContext.isContest()));
/*      */ 
/*  587 */           OrdersPanel.this.menuItemTp.setEnabled((order.isOpening()) && (OrderMessageUtils.getTpOrder(ogm, order) == null) && (model.getSelectedOrders().size() <= 1) && (!GreedContext.isGlobal()) && (!GreedContext.isGlobalExtended()) && (!GreedContext.isContest()));
/*      */ 
/*  594 */           OrdersPanel.this.menuItemCS.setEnabled((order.isOpening()) && (OrderMessageUtils.getCsOrder(order) == null) && (model.getSelectedOrders().size() <= 1) && (!GreedContext.isGlobal()) && (GreedContext.isGlobalExtended()));
/*      */ 
/*  600 */           OrdersPanel.this.menuItemCL.setEnabled((order.isOpening()) && (OrderMessageUtils.getClOrder(order) == null) && (model.getSelectedOrders().size() <= 1) && (!GreedContext.isGlobal()) && (GreedContext.isGlobalExtended()));
/*      */ 
/*  606 */           boolean isSLOpened = ogm.getStopLossOrder() != null;
/*  607 */           boolean isTPOpened = ogm.getTakeProfitOrder() != null;
/*  608 */           boolean isCSOpened = (OrderMessageUtils.getCsOrder(order) != null) || (order.isIfdStop());
/*  609 */           boolean isCLOpened = (OrderMessageUtils.getClOrder(order) != null) || (order.isIfdLimit());
/*      */ 
/*  611 */           if (GreedContext.isContest()) {
/*  612 */             isSLOpened = false;
/*  613 */             isTPOpened = false;
/*      */           }
/*      */ 
/*  616 */           OrdersPanel.this.menuItemEditSl.setEnabled((isSLOpened) || (GreedContext.isContest()));
/*  617 */           OrdersPanel.this.menuItemCancelSl.setEnabled(isSLOpened);
/*  618 */           OrdersPanel.this.menuItemEditTp.setEnabled((isTPOpened) || (GreedContext.isContest()));
/*  619 */           OrdersPanel.this.menuItemCancelTp.setEnabled(isTPOpened);
/*  620 */           OrdersPanel.this.menuItemEditCS.setEnabled(isCSOpened);
/*  621 */           OrdersPanel.this.menuItemCancelCS.setEnabled(isCSOpened);
/*  622 */           OrdersPanel.this.menuItemEditCL.setEnabled(isCLOpened);
/*  623 */           OrdersPanel.this.menuItemCancelCL.setEnabled(isCLOpened);
/*      */ 
/*  625 */           OrdersPanel.this.menuItemOco.setEnabled(OrdersPanel.this.testOcoGroupingCondition());
/*  626 */           OrdersPanel.this.menuItemOcoOff.setEnabled((OrdersPanel.this.testOrderUnGroupingCondition(order)) && (order.isOco().booleanValue()));
/*      */ 
/*  628 */           OrdersPanel.this.setMenuItemsVisibility(order);
/*  629 */           OrdersPanel.this.popupMenu.show(OrdersPanel.this.ordersTable, e.getX(), e.getY());
/*      */         }
/*      */       }
/*      */     });
/*  634 */     this.cancelItem.setAction(cancelAction);
/*  635 */     this.cancelItem.setName("ID_JMENU_CANCEL");
/*  636 */     this.editItem.setAction(editAction);
/*  637 */     this.editItem.setName("ID_JMENU_EDIT");
/*  638 */     this.cancelSelectedItem.setAction(cancelSelectedAction);
/*      */ 
/*  640 */     this.menuItemOco = new JLocalizableMenuItem("item.group.to.oco");
/*  641 */     this.menuItemOco.setName("ID_JMENU_GROUP");
/*  642 */     this.menuItemOco.setAction(new AbstractAction("item.group.to.oco") {
/*      */       public void actionPerformed(ActionEvent e) {
/*  644 */         TableSorter sorter = (TableSorter)OrdersPanel.this.ordersTable.getModel();
/*  645 */         OrderCommonTableModel model = (OrderCommonTableModel)sorter.getTableModel();
/*  646 */         if (model.getSelectedOrders().size() < 2) {
/*  647 */           return;
/*      */         }
/*  649 */         Set selectedOrders = new HashSet(model.getSelectedOrders());
/*      */ 
/*  651 */         OrderGroupMessage ocoGroup = new OrderGroupMessage();
/*  652 */         ocoGroup.setIsOcoMerge(Boolean.valueOf(true));
/*      */ 
/*  654 */         Map ocoOrders = new HashMap();
/*      */ 
/*  656 */         for (OrderMessage order : selectedOrders) {
/*  657 */           ocoOrders.put(order.getOrderId(), order.getOrderGroupId());
/*      */         }
/*      */ 
/*  660 */         ocoGroup.setOcoOrders(ocoOrders);
/*      */ 
/*  662 */         OrdersPanel.LOGGER.debug("ocoGroup: " + ocoGroup);
/*  663 */         GreedContext.publishEvent(new OcoOrderGroupingAction(this, ocoGroup));
/*  664 */         model.getSelectedOrders().clear();
/*  665 */         OrdersPanel.this.uncheckHeader();
/*      */       }
/*      */     });
/*  669 */     if ((GreedContext.isGlobal()) || (GreedContext.isGlobalExtended())) {
/*  670 */       this.menuItemOco.setEnabled(false);
/*      */     }
/*  672 */     this.menuItemOcoOff = new JLocalizableMenuItem("item.ungroup.oco");
/*  673 */     this.menuItemOcoOff.setName("ID_JMENU_OCO_UNGROUP");
/*  674 */     this.menuItemOcoOff.setAction(new AbstractAction("item.ungroup.oco") {
/*      */       public void actionPerformed(ActionEvent e) {
/*  676 */         TableSorter sorter = (TableSorter)OrdersPanel.this.ordersTable.getModel();
/*  677 */         OrderCommonTableModel model = (OrderCommonTableModel)sorter.getTableModel();
/*      */ 
/*  679 */         String ocoGroupId = null;
/*      */ 
/*  681 */         if (model.getSelectedOrders().size() <= 1) {
/*  682 */           int row = OrdersPanel.this.ordersTable.getSelectedRow();
/*  683 */           int rowTranslated = sorter.modelIndex(row);
/*  684 */           if ((rowTranslated < 0) && (rowTranslated >= model.getRowCount())) {
/*  685 */             if (OrdersPanel.LOGGER.isDebugEnabled()) {
/*  686 */               OrdersPanel.LOGGER.warn("WARN: the row was not found in sorter");
/*      */             }
/*  688 */             return;
/*      */           }
/*  690 */           OrderMessage order = model.getOrder(rowTranslated);
/*  691 */           ocoGroupId = order.getOcoGroup();
/*      */         } else {
/*  693 */           Set selectedOrders = new HashSet(model.getSelectedOrders());
/*  694 */           Iterator i$ = selectedOrders.iterator(); if (i$.hasNext()) { OrderMessage order = (OrderMessage)i$.next();
/*  695 */             ocoGroupId = order.getOcoGroup();
/*      */           }
/*      */ 
/*      */         }
/*      */ 
/*  700 */         OrderGroupMessage ocoGroup = new OrderGroupMessage();
/*  701 */         ocoGroup.setOrderGroupId(ocoGroupId);
/*  702 */         ocoGroup.setIsOcoMerge(Boolean.valueOf(true));
/*      */ 
/*  704 */         OrdersPanel.LOGGER.debug("ocoGroup: " + ocoGroup);
/*  705 */         GreedContext.publishEvent(new OcoOrderGroupingAction(this, ocoGroup));
/*  706 */         model.getSelectedOrders().clear();
/*  707 */         OrdersPanel.this.uncheckHeader();
/*      */       }
/*      */     });
/*  711 */     this.popupMenu.add(this.cancelItem);
/*  712 */     this.popupMenu.add(this.editItem);
/*  713 */     this.popupMenu.add(this.cancelSelectedItem);
/*  714 */     this.popupMenu.add(this.mainMenuSeparator);
/*      */ 
/*  716 */     this.popupMenu.add(this.menuItemOco);
/*  717 */     this.popupMenu.add(this.menuItemOcoOff);
/*  718 */     this.popupMenu.add(this.SLMenuSeparator);
/*      */ 
/*  720 */     this.popupMenu.add(this.menuItemSl);
/*  721 */     this.popupMenu.add(this.menuItemEditSl);
/*  722 */     this.popupMenu.add(this.menuItemCancelSl);
/*  723 */     this.popupMenu.add(this.TPMenuSeparator);
/*      */ 
/*  725 */     this.popupMenu.add(this.menuItemTp);
/*  726 */     this.popupMenu.add(this.menuItemEditTp);
/*  727 */     this.popupMenu.add(this.menuItemCancelTp);
/*      */ 
/*  729 */     this.popupMenu.add(this.menuItemCS);
/*  730 */     this.popupMenu.add(this.menuItemEditCS);
/*  731 */     this.popupMenu.add(this.menuItemCancelCS);
/*  732 */     this.popupMenu.add(this.CSMenuSeparator);
/*      */ 
/*  734 */     this.popupMenu.add(this.menuItemCL);
/*  735 */     this.popupMenu.add(this.menuItemEditCL);
/*  736 */     this.popupMenu.add(this.menuItemCancelCL);
/*      */ 
/*  739 */     List sortKeys = ((ClientSettingsStorage)GreedContext.get("settingsStorage")).restoreTableSortKeys(this.ordersTable.getTableId());
/*  740 */     sorter.setSortingStatus(sortKeys);
/*      */ 
/*  742 */     ((ClientSettingsStorage)GreedContext.get("settingsStorage")).restoreTableColumns(this.ordersTable.getTableId(), this.ordersTable.getColumnModel());
/*      */   }
/*      */ 
/*      */   private void setMenuItemsVisibility(OrderMessage order)
/*      */   {
/*  748 */     boolean notTPnotSL = (!order.isTakeProfit()) && (!order.isStopLoss()) && (!GreedContext.isGlobalExtended());
/*      */ 
/*  752 */     boolean notCLnotCS = (!order.isIfdLimit()) && (!order.isIfdStop()) && (GreedContext.isGlobalExtended());
/*      */ 
/*  756 */     boolean isSL = order.isStopLoss();
/*  757 */     boolean isTP = order.isTakeProfit();
/*  758 */     boolean isCS = order.isIfdStop();
/*  759 */     boolean isCL = order.isIfdLimit();
/*      */ 
/*  761 */     this.editItem.setVisible((notTPnotSL) || (notCLnotCS));
/*  762 */     this.cancelItem.setVisible((notTPnotSL) || (notCLnotCS));
/*  763 */     this.cancelSelectedItem.setVisible((notTPnotSL) || (isSL) || (isTP) || (notCLnotCS) || (isCS) || (isCL));
/*      */ 
/*  770 */     this.mainMenuSeparator.setVisible((notTPnotSL) || (isSL) || (isTP) || (notCLnotCS) || (isCS) || (isCL));
/*      */ 
/*  777 */     this.menuItemOco.setVisible((notTPnotSL) || (notCLnotCS));
/*  778 */     this.menuItemOcoOff.setVisible((notTPnotSL) || (notCLnotCS));
/*  779 */     this.SLMenuSeparator.setVisible((notTPnotSL) || (notCLnotCS));
/*      */ 
/*  781 */     this.menuItemSl.setVisible(notTPnotSL);
/*  782 */     this.menuItemEditSl.setVisible((isSL) || (notTPnotSL));
/*  783 */     this.menuItemCancelSl.setVisible((isSL) || (notTPnotSL));
/*      */ 
/*  785 */     this.menuItemCS.setVisible(notCLnotCS);
/*  786 */     this.menuItemEditCS.setVisible((isCS) || (notCLnotCS));
/*  787 */     this.menuItemCancelCS.setVisible((isCS) || (notCLnotCS));
/*      */ 
/*  789 */     this.TPMenuSeparator.setVisible(notTPnotSL);
/*      */ 
/*  791 */     this.menuItemTp.setVisible(notTPnotSL);
/*  792 */     this.menuItemEditTp.setVisible((isTP) || (notTPnotSL));
/*  793 */     this.menuItemCancelTp.setVisible((isTP) || (notTPnotSL));
/*      */ 
/*  795 */     this.CSMenuSeparator.setVisible(notCLnotCS);
/*      */ 
/*  797 */     this.menuItemCL.setVisible(notCLnotCS);
/*  798 */     this.menuItemEditCL.setVisible((isCL) || (notCLnotCS));
/*  799 */     this.menuItemCancelCL.setVisible((isCL) || (notCLnotCS));
/*      */   }
/*      */ 
/*      */   private void openEditDetachedPanel(OrderCommonTableModel model, int row, StopOrderType stopOrderType) {
/*  803 */     OrderGroupMessage orderGroup = model.getGroup(row);
/*  804 */     OrderMessage orderMessage = model.getOrder(row);
/*  805 */     String openingId = null;
/*      */ 
/*  808 */     if (OrderDirection.OPEN.equals(orderMessage.getOrderDirection()))
/*  809 */       openingId = orderMessage.getOrderId();
/*  810 */     else if (OrderDirection.CLOSE.equals(orderMessage.getOrderDirection())) {
/*  811 */       openingId = orderMessage.getIfdParentOrderId();
/*      */     }
/*      */ 
/*  814 */     BigDecimal priceStop = null;
/*      */ 
/*  816 */     if (orderMessage.isPlaceOffer())
/*  817 */       priceStop = orderMessage.getPriceClient() != null ? orderMessage.getPriceClient().getValue() : null;
/*      */     else {
/*  819 */       priceStop = orderMessage.getPriceStop() != null ? orderMessage.getPriceStop().getValue() : null;
/*      */     }
/*      */ 
/*  822 */     if (StopEditDetached.getOpenedFrame(orderMessage.getOrderId()) == null)
/*  823 */       new StopEditDetached(orderGroup, stopOrderType, null, openingId, priceStop);
/*      */   }
/*      */ 
/*      */   private boolean testOcoGroupingCondition()
/*      */   {
/*  828 */     TableSorter sorter = (TableSorter)this.ordersTable.getModel();
/*  829 */     OrderCommonTableModel model = (OrderCommonTableModel)sorter.getTableModel();
/*  830 */     if ((model.getSelectedOrders() == null) || (model.getSelectedOrders().size() < 2) || (model.getSelectedOrders().size() > 2))
/*      */     {
/*  832 */       LOGGER.debug("oco miss: wrong selected count " + model.getSelectedOrders().size());
/*  833 */       return false;
/*      */     }
/*      */ 
/*  836 */     boolean isFirst = true;
/*  837 */     String targetInstrument = null;
/*  838 */     OrderMessage first = null;
/*  839 */     OrderMessage second = null;
/*  840 */     for (OrderMessage order : model.getSelectedOrders()) {
/*  841 */       if (isFirst) {
/*  842 */         targetInstrument = order.getInstrument();
/*  843 */         isFirst = false;
/*  844 */         first = order;
/*      */       } else {
/*  846 */         second = order;
/*      */       }
/*  848 */       if (!testOrderViableForGrouping(order)) {
/*  849 */         LOGGER.debug("oco miss notViableForGrouping ");
/*  850 */         return false;
/*      */       }
/*  852 */       if (order.isOco().booleanValue()) {
/*  853 */         LOGGER.debug("oco miss: isOco already");
/*  854 */         return false;
/*      */       }
/*  856 */       if (!targetInstrument.equals(order.getInstrument())) {
/*  857 */         LOGGER.debug("oco miss: instrument do not match");
/*  858 */         return false;
/*      */       }
/*      */     }
/*  861 */     if ((OrderMessageUtils.isLimit(first)) && (OrderMessageUtils.isLimit(second))) {
/*  862 */       if (((!OrderSide.SELL.equals(first.getSide())) || (!OrderSide.BUY.equals(second.getSide()))) && ((!OrderSide.SELL.equals(second.getSide())) || (!OrderSide.BUY.equals(first.getSide()))))
/*      */       {
/*  867 */         LOGGER.debug("oco miss: two limits " + first.getSide() + " " + second.getSide());
/*  868 */         return false;
/*      */       }
/*  870 */     } else if ((OrderMessageUtils.isStop(first)) && (OrderMessageUtils.isStop(second))) {
/*  871 */       if (((!OrderSide.SELL.equals(first.getSide())) || (!OrderSide.BUY.equals(second.getSide()))) && ((!OrderSide.SELL.equals(second.getSide())) || (!OrderSide.BUY.equals(first.getSide()))))
/*      */       {
/*  876 */         LOGGER.debug("oco miss: two stops");
/*  877 */         return false;
/*      */       }
/*  879 */     } else if ((OrderSide.SELL.equals(first.getSide())) && (OrderSide.SELL.equals(second.getSide()))) {
/*  880 */       if (((!OrderMessageUtils.isStop(first)) || (!OrderMessageUtils.isLimit(second))) && ((!OrderMessageUtils.isStop(second)) || (!OrderMessageUtils.isLimit(first))))
/*      */       {
/*  885 */         LOGGER.debug("oco miss: two sells");
/*  886 */         return false;
/*      */       }
/*  888 */     } else if ((OrderSide.BUY.equals(first.getSide())) && (OrderSide.BUY.equals(second.getSide()))) {
/*  889 */       if (((!OrderMessageUtils.isStop(first)) || (!OrderMessageUtils.isLimit(second))) && ((!OrderMessageUtils.isStop(second)) || (!OrderMessageUtils.isLimit(first))))
/*      */       {
/*  894 */         LOGGER.debug("oco miss: two buys");
/*  895 */         return false;
/*      */       }
/*  897 */     } else if (((OrderSide.BUY.equals(first.getSide())) && (OrderSide.SELL.equals(second.getSide()))) || ((OrderSide.BUY.equals(second.getSide())) && (OrderSide.SELL.equals(first.getSide()))))
/*      */     {
/*  899 */       if (((OrderMessageUtils.isStop(first)) && (OrderMessageUtils.isLimit(second))) || ((OrderMessageUtils.isStop(second)) && (OrderMessageUtils.isLimit(first))))
/*      */       {
/*  902 */         LOGGER.debug("oco miss: buy/sell");
/*  903 */         return false;
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  908 */     return true;
/*      */   }
/*      */ 
/*      */   private boolean testOrderViableForGrouping(OrderMessage order) {
/*  912 */     return OrderMessageUtils.isStopOrLimitOpening(order);
/*      */   }
/*      */ 
/*      */   private boolean testOrderUnGroupingCondition(OrderMessage order) {
/*  916 */     TableSorter sorter = (TableSorter)this.ordersTable.getModel();
/*  917 */     OrderCommonTableModel model = (OrderCommonTableModel)sorter.getTableModel();
/*      */ 
/*  919 */     if ((model.getSelectedOrders() == null) || (model.getSelectedOrders().size() < 1))
/*  920 */       return testOrderViableForGrouping(order);
/*  921 */     if ((model.getSelectedOrders().size() == 1) || (model.getSelectedOrders().size() == 2)) {
/*  922 */       OrderMessage first = (OrderMessage)((TreeSet)model.getSelectedOrders()).first();
/*  923 */       OrderMessage second = (OrderMessage)((TreeSet)model.getSelectedOrders()).last();
/*  924 */       if ((testOrderViableForGrouping(first)) && (testOrderViableForGrouping(second)) && (first.isOco().booleanValue()) && (second.isOco().booleanValue()) && (first.getOrderGroupId().equals(second.getOrderGroupId())))
/*      */       {
/*  927 */         return true;
/*      */       }
/*      */     } else {
/*  930 */       LOGGER.debug("size: " + model.getSelectedOrders().size());
/*      */     }
/*  932 */     return false;
/*      */   }
/*      */ 
/*      */   private void fireCancelSelectedOrder(OrderCommonTableModel model) {
/*  936 */     new SwingWorker()
/*      */     {
/*      */       protected Object construct() throws Exception
/*      */       {
/*  940 */         Set selectedOrders = new HashSet(OrdersPanel.this.notm.getSelectedOrders());
/*  941 */         Set entryOrderIdSet = new HashSet();
/*      */ 
/*  944 */         for (OrderMessage order : selectedOrders) {
/*  945 */           if ((order.getIfdParentOrderId() == null) && (order.isOpening())) {
/*  946 */             entryOrderIdSet.add(order.getOrderId());
/*      */           }
/*      */         }
/*      */ 
/*  950 */         OrdersPanel.LOGGER.debug("selected :" + selectedOrders);
/*  951 */         OrdersPanel.LOGGER.debug("entryOIDS :" + entryOrderIdSet);
/*      */ 
/*  953 */         for (OrderMessage order : selectedOrders)
/*      */         {
/*  955 */           if ((order.getIfdParentOrderId() != null) && (entryOrderIdSet.contains(order.getIfdParentOrderId())))
/*      */           {
/*  957 */             OrdersPanel.LOGGER.debug("skipping cancel oid:" + order.getOrderId());
/*  958 */             continue;
/*      */           }
/*  960 */           OrderGroupMessage orderGroup = OrdersPanel.this.notm.getGroup(order.getOrderGroupId());
/*  961 */           if (orderGroup != null) {
/*  962 */             OrdersPanel.LOGGER.debug("firing cancel for: " + order.getOrderId());
/*  963 */             OrdersPanel.this.fireCancelOrder(orderGroup, order);
/*  964 */             OrdersPanel.this.notm.getSelectedOrders().remove(order);
/*      */           } else {
/*  966 */             OrdersPanel.LOGGER.debug("group is null for: " + order.getOrderId());
/*      */           }
/*      */         }
/*  969 */         OrdersPanel.this.notm.getSelectedOrders().clear();
/*  970 */         OrdersPanel.this.uncheckHeader();
/*      */ 
/*  972 */         return null;
/*      */       }
/*      */     }
/*  936 */     .start();
/*      */   }
/*      */ 
/*      */   private void fireCancelOrder(OrderGroupMessage orderGroup, OrderMessage order)
/*      */   {
/*  979 */     uncheckHeader();
/*      */ 
/*  981 */     CancelOrderAction cancelOrderAction = new CancelOrderAction(this, orderGroup, order);
/*  982 */     GreedContext.publishEvent(cancelOrderAction);
/*      */ 
/*  984 */     if (GreedContext.isActivityLoggingEnabled()) {
/*  985 */       EmergencyLogger logger = (EmergencyLogger)GreedContext.get("Logger");
/*  986 */       logger.add(order);
/*      */     }
/*      */   }
/*      */ 
/*      */   private void uncheckHeader() {
/*  991 */     TableColumn checkColumn = this.ordersTable.getTableHeader().getColumnModel().getColumn(this.ordersTable.convertColumnIndexToView(OrderTableModel.COLUMN_CHECK));
/*  992 */     CheckBoxHeader header = (CheckBoxHeader)checkColumn.getHeaderRenderer();
/*  993 */     if (header != null) {
/*  994 */       header.setSelected(false);
/*      */     }
/*  996 */     this.ordersTable.getTableHeader().repaint();
/*      */   }
/*      */ 
/*      */   public void addHeader(HeaderPanel header) {
/* 1000 */     add(header, 0);
/* 1001 */     this.header = header;
/*      */   }
/*      */ 
/*      */   public HeaderPanel getHeader() {
/* 1005 */     return this.header;
/*      */   }
/*      */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.orders.OrdersPanel
 * JD-Core Version:    0.6.0
 */