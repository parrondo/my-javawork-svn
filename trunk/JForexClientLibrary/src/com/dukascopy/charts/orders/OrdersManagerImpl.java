/*      */ package com.dukascopy.charts.orders;
/*      */ 
/*      */ import com.dukascopy.api.IEngine.OrderCommand;
/*      */ import com.dukascopy.api.Instrument;
/*      */ import com.dukascopy.charts.chartbuilder.ChartState;
/*      */ import com.dukascopy.charts.chartbuilder.GuiRefresher;
/*      */ import com.dukascopy.charts.data.IOrdersDataProviderManager;
/*      */ import com.dukascopy.charts.data.datacache.IFeedDataProvider;
/*      */ import com.dukascopy.charts.data.datacache.OrderHistoricalData;
/*      */ import com.dukascopy.charts.data.datacache.OrderHistoricalData.CloseData;
/*      */ import com.dukascopy.charts.data.datacache.OrderHistoricalData.OpenData;
/*      */ import com.dukascopy.charts.data.datacache.TickData;
/*      */ import com.dukascopy.charts.listeners.orders.OrdersActionListener;
/*      */ import com.dukascopy.charts.mappers.IMapper;
/*      */ import com.dukascopy.charts.orders.orderparts.ClosingLine;
/*      */ import com.dukascopy.charts.orders.orderparts.ClosingPoint;
/*      */ import com.dukascopy.charts.orders.orderparts.EntryLine;
/*      */ import com.dukascopy.charts.orders.orderparts.HorizontalLine;
/*      */ import com.dukascopy.charts.orders.orderparts.MergingLine;
/*      */ import com.dukascopy.charts.orders.orderparts.OpeningPoint;
/*      */ import com.dukascopy.charts.orders.orderparts.OrderPart;
/*      */ import com.dukascopy.charts.orders.orderparts.StopLossLine;
/*      */ import com.dukascopy.charts.orders.orderparts.TakeProfitLine;
/*      */ import com.dukascopy.charts.persistence.ChartBean;
/*      */ import com.dukascopy.charts.persistence.ITheme;
/*      */ import com.dukascopy.charts.persistence.ITheme.ChartElement;
/*      */ import com.dukascopy.charts.settings.ChartSettings;
/*      */ import com.dukascopy.charts.settings.ChartSettings.Option;
/*      */ import com.dukascopy.charts.utils.helper.LocalizedMessageHelper;
/*      */ import com.dukascopy.dds2.greed.agent.strategy.StratUtils;
/*      */ import com.dukascopy.dds2.greed.gui.l10n.LocalizationManager;
/*      */ import com.dukascopy.dds2.greed.util.IOrderUtils;
/*      */ import com.dukascopy.transport.common.msg.request.AccountInfoMessage;
/*      */ import java.awt.BasicStroke;
/*      */ import java.awt.Color;
/*      */ import java.awt.Container;
/*      */ import java.awt.Point;
/*      */ import java.awt.Window;
/*      */ import java.awt.event.ActionEvent;
/*      */ import java.awt.event.ActionListener;
/*      */ import java.awt.event.FocusEvent;
/*      */ import java.awt.event.KeyEvent;
/*      */ import java.awt.event.MouseEvent;
/*      */ import java.awt.event.MouseWheelEvent;
/*      */ import java.math.BigDecimal;
/*      */ import java.math.RoundingMode;
/*      */ import java.util.ArrayList;
/*      */ import java.util.HashMap;
/*      */ import java.util.Iterator;
/*      */ import java.util.List;
/*      */ import java.util.Map;
/*      */ import java.util.Map.Entry;
/*      */ import java.util.Set;
/*      */ import javax.swing.JComponent;
/*      */ import javax.swing.JMenu;
/*      */ import javax.swing.JMenuItem;
/*      */ import javax.swing.JPopupMenu;
/*      */ import javax.swing.JRootPane;
/*      */ import javax.swing.SwingUtilities;
/*      */ 
/*      */ public class OrdersManagerImpl
/*      */   implements OrdersDrawingManager, OrdersController, OrdersMouseController
/*      */ {
/*   60 */   private static final BigDecimal ONE_MILLION = new BigDecimal("1000000");
/*   61 */   private static final BigDecimal SIGNS_AZ_THRESHOLD = new BigDecimal("0.01");
/*      */ 
/*   63 */   private List<HorizontalLine> selectedHorizontalLines = new ArrayList();
/*      */   private boolean highlited;
/*      */   private final ChartState chartState;
/*      */   private final IMapper mapper;
/*      */   private final GuiRefresher guiRefresher;
/*      */   private final OrdersActionListener ordersActionListener;
/*      */   private final IOrdersDataProviderManager ordersDataProviderManager;
/*      */   private final ChartBean chartBean;
/*   75 */   private int paintTag = 0;
/*      */ 
/*   77 */   protected final Map<String, EntryLine> entryLines = new HashMap();
/*   78 */   protected final Map<String, StopLossLine> stopLossLines = new HashMap();
/*   79 */   protected final Map<String, TakeProfitLine> takeProfitLines = new HashMap();
/*   80 */   protected final Map<String, OpeningPoint> openingPoints = new HashMap();
/*   81 */   protected final Map<String, MergingLine> mergingLines = new HashMap();
/*   82 */   protected final Map<String, ClosingLine> closingLines = new HashMap();
/*   83 */   protected final Map<String, ClosingPoint> closingPoints = new HashMap();
/*      */ 
/*      */   public OrdersManagerImpl(ChartState chartState, IMapper mapper, GuiRefresher guiRefresher, OrdersActionListener ordersActionListener, ChartBean chartBean, IOrdersDataProviderManager iOrdersDataProviderManager)
/*      */   {
/*   93 */     this.chartState = chartState;
/*   94 */     this.mapper = mapper;
/*   95 */     this.guiRefresher = guiRefresher;
/*   96 */     this.ordersActionListener = ordersActionListener;
/*   97 */     this.chartBean = chartBean;
/*   98 */     this.ordersDataProviderManager = iOrdersDataProviderManager;
/*      */   }
/*      */ 
/*      */   public void keyPressed(KeyEvent e)
/*      */   {
/*  105 */     if (e.isConsumed()) {
/*  106 */       return;
/*      */     }
/*  108 */     if (!this.selectedHorizontalLines.isEmpty())
/*  109 */       switch (e.getKeyCode()) {
/*      */       case 38:
/*  111 */         moveHorizontalLines(BigDecimal.valueOf(getInstrument().getPipValue()));
/*  112 */         e.consume();
/*  113 */         break;
/*      */       case 40:
/*  115 */         moveHorizontalLines(BigDecimal.valueOf(getInstrument().getPipValue()).negate());
/*  116 */         e.consume();
/*  117 */         break;
/*      */       case 27:
/*  119 */         cancelChangesMadeToHorizontalLines();
/*  120 */         e.consume();
/*  121 */         break;
/*      */       case 10:
/*  123 */         commitChangesMadeToHorizontalLines();
/*  124 */         e.consume();
/*  125 */         break;
/*      */       case 127:
/*  127 */         if (LocalizedMessageHelper.showConfirmationMessage(e.getComponent(), LocalizationManager.getText("title.orders.manager.cancel.order"), LocalizationManager.getText("prompt.orders.manager.cancel.order")) != 0)
/*      */         {
/*      */           break;
/*      */         }
/*      */ 
/*  133 */         deleteHorizontalLines();
/*  134 */         e.consume();
/*      */       }
/*      */   }
/*      */ 
/*      */   public void mouseReleased(MouseEvent e)
/*      */   {
/*  143 */     if ((!e.isConsumed()) && (e.isPopupTrigger())) {
/*  144 */       showPopup(e);
/*  145 */       e.consume();
/*      */     }
/*  147 */     if (!this.selectedHorizontalLines.isEmpty()) {
/*  148 */       boolean oneIsDragged = false;
/*  149 */       for (HorizontalLine selectedHorizontalLine : this.selectedHorizontalLines) {
/*  150 */         if (selectedHorizontalLine.isDragged()) {
/*  151 */           oneIsDragged = true;
/*  152 */           selectedHorizontalLine.setDragged(false);
/*      */         }
/*      */       }
/*  155 */       if (oneIsDragged) {
/*  156 */         commitChangesMadeToHorizontalLines();
/*  157 */         e.consume();
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   public byte mouseClicked(MouseEvent e)
/*      */   {
/*  164 */     if ((SwingUtilities.isLeftMouseButton(e)) && (e.getClickCount() == 2))
/*      */     {
/*  166 */       if (((e.getModifiersEx() & 0x80) != 128) && (!this.selectedHorizontalLines.isEmpty()))
/*      */       {
/*  168 */         for (Iterator iterator = this.selectedHorizontalLines.iterator(); iterator.hasNext(); ) {
/*  169 */           HorizontalLine selectedHorizontalLine = (HorizontalLine)iterator.next();
/*  170 */           if (!selectedHorizontalLine.hitPoint(e.getX(), e.getY()))
/*      */           {
/*  172 */             selectedHorizontalLine.setSelected(false);
/*  173 */             iterator.remove();
/*  174 */             updateSelectedGroupIds();
/*      */           }
/*      */         }
/*  177 */         if ((isEnabled(ChartSettings.Option.TRADING)) && (this.selectedHorizontalLines.isEmpty()))
/*      */         {
/*  179 */           HorizontalLine selectedHorizontalLine = selectOrderElement(e.getX(), e.getY());
/*  180 */           if (selectedHorizontalLine != null) {
/*  181 */             selectedHorizontalLine.setSelectedPrice(selectedHorizontalLine.getPrice());
/*  182 */             selectedHorizontalLine.setSelected(true);
/*  183 */             this.selectedHorizontalLines.add(selectedHorizontalLine);
/*  184 */             updateSelectedGroupIds();
/*      */           }
/*      */         }
/*  187 */         if ((this.selectedHorizontalLines.isEmpty()) && (!e.isConsumed())) {
/*  188 */           this.ordersActionListener.ordersEditingEnded();
/*      */         }
/*  190 */         refreshScreen();
/*  191 */         e.consume();
/*      */       }
/*  193 */       else if (isEnabled(ChartSettings.Option.TRADING))
/*      */       {
/*  195 */         HorizontalLine selectedHorizontalLine = selectOrderElement(e.getX(), e.getY());
/*  196 */         if (selectedHorizontalLine != null) {
/*  197 */           selectedHorizontalLine.setSelectedPrice(selectedHorizontalLine.getPrice());
/*  198 */           selectedHorizontalLine.setSelected(true);
/*  199 */           if (this.selectedHorizontalLines.isEmpty()) {
/*  200 */             this.selectedHorizontalLines.add(selectedHorizontalLine);
/*  201 */             updateSelectedGroupIds();
/*      */ 
/*  203 */             this.ordersActionListener.ordersEditingStarted();
/*  204 */           } else if ((e.getModifiersEx() & 0x80) == 128) {
/*  205 */             this.selectedHorizontalLines.add(selectedHorizontalLine);
/*  206 */             updateSelectedGroupIds();
/*      */           } else {
/*  208 */             selectedHorizontalLine.setSelected(false);
/*  209 */             throw new RuntimeException("Logical error");
/*      */           }
/*  211 */           refreshScreen();
/*  212 */           e.consume();
/*      */         }
/*      */       }
/*      */     }
/*  216 */     else if ((SwingUtilities.isLeftMouseButton(e)) && (e.getClickCount() == 1))
/*      */     {
/*  218 */       if (!this.selectedHorizontalLines.isEmpty()) {
/*  219 */         boolean onSelectedLine = false;
/*  220 */         for (HorizontalLine selectedHorizontalLine : this.selectedHorizontalLines) {
/*  221 */           if (selectedHorizontalLine.hitPoint(e.getX(), e.getY())) {
/*  222 */             onSelectedLine = true;
/*      */           }
/*      */         }
/*  225 */         if (!onSelectedLine)
/*      */         {
/*  227 */           for (HorizontalLine selectedHorizontalLine : this.selectedHorizontalLines) {
/*  228 */             selectedHorizontalLine.setDragged(false);
/*      */           }
/*  230 */           commitChangesMadeToHorizontalLines();
/*  231 */           e.consume();
/*      */         }
/*      */       }
/*      */     }
/*  235 */     if (isEnabled(ChartSettings.Option.TRADING)) {
/*  236 */       HorizontalLine selectedLine = selectOrderElement(e.getX(), e.getY());
/*  237 */       if (selectedLine != null) {
/*  238 */         if (selectedLine.isSelected()) {
/*  239 */           return 1;
/*      */         }
/*  241 */         return 0;
/*      */       }
/*      */     }
/*      */ 
/*  245 */     return -1;
/*      */   }
/*      */ 
/*      */   public void mousePressed(MouseEvent e)
/*      */   {
/*  250 */     if ((!e.isConsumed()) && (e.isPopupTrigger())) {
/*  251 */       showPopup(e);
/*  252 */       e.consume();
/*      */     }
/*  254 */     if ((SwingUtilities.isLeftMouseButton(e)) && 
/*  255 */       (!this.selectedHorizontalLines.isEmpty())) {
/*  256 */       boolean hitOnSelectedLine = false;
/*  257 */       for (HorizontalLine selectedHorizontalLine : this.selectedHorizontalLines) {
/*  258 */         if (selectedHorizontalLine.hitPoint(e.getX(), e.getY())) {
/*  259 */           selectedHorizontalLine.setDragged(true);
/*  260 */           hitOnSelectedLine = true;
/*      */         }
/*      */       }
/*      */ 
/*  264 */       if (hitOnSelectedLine) {
/*  265 */         for (HorizontalLine selectedHorizontalLine : this.selectedHorizontalLines) {
/*  266 */           selectedHorizontalLine.setSelectedPrice(selectedHorizontalLine.getPrice());
/*      */         }
/*  268 */         refreshScreen();
/*  269 */         e.consume();
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   public byte mouseMoved(MouseEvent e)
/*      */   {
/*  277 */     if (e.isConsumed()) {
/*  278 */       return -1;
/*      */     }
/*      */ 
/*  281 */     if (isEnabled(ChartSettings.Option.TRADING)) {
/*  282 */       HorizontalLine selectedLine = selectOrderElement(e.getX(), e.getY());
/*  283 */       if ((selectedLine == null) && (this.highlited))
/*      */       {
/*  285 */         this.highlited = false;
/*  286 */       } else if ((selectedLine != null) && (!this.highlited)) {
/*  287 */         this.highlited = true;
/*      */       }
/*  289 */       if (selectedLine != null) {
/*  290 */         if (selectedLine.isSelected()) {
/*  291 */           return 1;
/*      */         }
/*  293 */         return 0;
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  298 */     return -1;
/*      */   }
/*      */ 
/*      */   public void mouseDragged(MouseEvent e)
/*      */   {
/*  303 */     if (e.isConsumed()) {
/*  304 */       return;
/*      */     }
/*  306 */     if (!this.selectedHorizontalLines.isEmpty()) {
/*  307 */       HorizontalLine draggedLine = null;
/*  308 */       for (HorizontalLine selectedHorizontalLine : this.selectedHorizontalLines) {
/*  309 */         if (selectedHorizontalLine.isDragged()) {
/*  310 */           draggedLine = selectedHorizontalLine;
/*      */         }
/*      */       }
/*  313 */       if (draggedLine != null) {
/*  314 */         moveHorizontalLines(draggedLine, e.getY());
/*  315 */         e.consume();
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   public void mouseWheelMoved(MouseWheelEvent e)
/*      */   {
/*  322 */     if (e.isConsumed()) {
/*  323 */       return;
/*      */     }
/*  325 */     if (!this.selectedHorizontalLines.isEmpty()) {
/*  326 */       int clickCount = e.getWheelRotation();
/*  327 */       moveHorizontalLines(BigDecimal.valueOf(getInstrument().getPipValue()).multiply(BigDecimal.valueOf(clickCount)));
/*  328 */       e.consume();
/*      */     }
/*      */   }
/*      */ 
/*      */   private void showPopup(MouseEvent e)
/*      */   {
/*  335 */     if ((this.chartState.isReadOnly()) || (this.chartBean.isHistoricalTesterChart())) {
/*  336 */       return;
/*      */     }
/*  338 */     JPopupMenu popup = createPopup(e.getX(), e.getY());
/*  339 */     if (popup != null) {
/*  340 */       Point location = ((JComponent)e.getComponent()).getPopupLocation(e);
/*  341 */       if (location == null) {
/*  342 */         location = new Point(e.getX(), e.getY());
/*      */       }
/*  344 */       popup.show(e.getComponent(), location.x, location.y);
/*      */     }
/*      */   }
/*      */ 
/*      */   private void moveHorizontalLines(BigDecimal priceDiff) {
/*  349 */     for (HorizontalLine selectedHorizontalLine : this.selectedHorizontalLines) {
/*  350 */       BigDecimal newPrice = selectedHorizontalLine.getSelectedPrice().add(priceDiff);
/*  351 */       selectedHorizontalLine.setSelectedPrice(newPrice);
/*  352 */       IOrderUtils orderUtils = this.ordersDataProviderManager.getOrderUtils();
/*      */ 
/*  354 */       BasicStroke stroke = new BasicStroke(1.0F, 0, 0, 10.0F, new float[] { 2.0F, 4.0F }, 0.0F);
/*  355 */       String text = selectedHorizontalLine.getText(newPrice);
/*  356 */       String orderId = selectedHorizontalLine.getOrderId();
/*      */ 
/*  358 */       orderUtils.setOrderLinesVisible(getChartBean(), orderId, false);
/*      */ 
/*  362 */       selectedHorizontalLine.setVisible(true);
/*  363 */       orderUtils.orderChangePreview(getChartBean(), orderId, newPrice, text, selectedHorizontalLine.getColor(), stroke);
/*      */     }
/*      */ 
/*  372 */     refreshScreen();
/*      */   }
/*      */ 
/*      */   private void moveHorizontalLines(HorizontalLine draggedLine, float y) {
/*  376 */     BigDecimal value = BigDecimal.valueOf(StratUtils.round05Pips(this.mapper.vy((int)y)));
/*  377 */     moveHorizontalLines(value.subtract(draggedLine.getSelectedPrice()));
/*      */   }
/*      */ 
/*      */   private void commitChangesMadeToHorizontalLines() {
/*  381 */     for (HorizontalLine selectedHorizontalLine : this.selectedHorizontalLines) {
/*  382 */       if ((selectedHorizontalLine != null) && (selectedHorizontalLine.getSelectedPrice().compareTo(selectedHorizontalLine.getPrice()) != 0)) {
/*  383 */         IOrderUtils orderUtils = this.ordersDataProviderManager.getOrderUtils();
/*  384 */         String orderId = selectedHorizontalLine.getOrderId();
/*  385 */         orderUtils.editOrder(orderId, StratUtils.round05Pips(selectedHorizontalLine.getSelectedPrice().doubleValue()), new ActionListener(orderUtils, orderId)
/*      */         {
/*      */           public void actionPerformed(ActionEvent e)
/*      */           {
/*  389 */             this.val$orderUtils.setOrderLinesVisible(OrdersManagerImpl.this.getChartBean(), this.val$orderId, true);
/*  390 */             OrdersManagerImpl.this.refreshScreen();
/*      */           }
/*      */         }
/*      */         , getChartBean());
/*      */ 
/*  395 */         selectedHorizontalLine.setSelectedPrice(selectedHorizontalLine.getPrice());
/*      */ 
/*  399 */         selectedHorizontalLine.setVisible(false);
/*      */       }
/*      */     }
/*  402 */     refreshScreen();
/*      */   }
/*      */ 
/*      */   public void cancelChangesMadeToHorizontalLine(HorizontalLine selectedHorizontalLine) {
/*  406 */     selectedHorizontalLine.setDragged(false);
/*  407 */     selectedHorizontalLine.setSelected(false);
/*  408 */     this.selectedHorizontalLines.remove(selectedHorizontalLine);
/*  409 */     updateSelectedGroupIds();
/*      */ 
/*  411 */     this.ordersActionListener.ordersEditingEnded();
/*  412 */     refreshScreen();
/*      */   }
/*      */ 
/*      */   public void cancelChangesMadeToHorizontalLines() {
/*  416 */     for (Iterator iterator = this.selectedHorizontalLines.iterator(); iterator.hasNext(); ) {
/*  417 */       HorizontalLine selectedHorizontalLine = (HorizontalLine)iterator.next();
/*  418 */       this.ordersDataProviderManager.getOrderUtils().cancelOrderChangePreview(getChartBean(), selectedHorizontalLine.getOrderId());
/*  419 */       selectedHorizontalLine.setDragged(false);
/*  420 */       selectedHorizontalLine.setSelected(false);
/*  421 */       iterator.remove();
/*      */     }
/*  423 */     updateSelectedGroupIds();
/*  424 */     this.ordersActionListener.ordersEditingEnded();
/*  425 */     refreshScreen();
/*      */   }
/*      */ 
/*      */   public void deleteHorizontalLines() {
/*  429 */     IOrderUtils orderUtils = this.ordersDataProviderManager.getOrderUtils();
/*  430 */     for (HorizontalLine selectedHorizontalLine : this.selectedHorizontalLines) {
/*  431 */       String orderId = selectedHorizontalLine.getOrderId();
/*  432 */       orderUtils.cancelOrderChangePreview(getChartBean(), orderId);
/*  433 */       orderUtils.cancelOrder(orderId);
/*      */     }
/*      */   }
/*      */ 
/*      */   private void updateSelectedGroupIds()
/*      */   {
/*  439 */     List groupIds = new ArrayList();
/*  440 */     for (HorizontalLine selectedHorizontalLine : this.selectedHorizontalLines) {
/*  441 */       groupIds.add(selectedHorizontalLine.getOrderGroupId());
/*      */     }
/*  443 */     getChartBean().getFeedDataProvider().getOrderUtils().selectGroupIds(groupIds);
/*      */   }
/*      */ 
/*      */   private HorizontalLine selectOrderElement(int x, int y) {
/*  447 */     HorizontalLine selectedHorizontalLine = getHitLine(this.entryLines, x, y);
/*  448 */     if (selectedHorizontalLine == null) {
/*  449 */       selectedHorizontalLine = getHitLine(this.stopLossLines, x, y);
/*      */     }
/*  451 */     if (selectedHorizontalLine == null) {
/*  452 */       selectedHorizontalLine = getHitLine(this.takeProfitLines, x, y);
/*      */     }
/*  454 */     return selectedHorizontalLine;
/*      */   }
/*      */ 
/*      */   private HorizontalLine getHitLine(Map<String, ? extends HorizontalLine> orderPartsMap, int x, int y) {
/*  458 */     for (HorizontalLine line : orderPartsMap.values()) {
/*  459 */       if ((line.hitPoint(x, y)) && (line.isEditable())) {
/*  460 */         return line;
/*      */       }
/*      */     }
/*  463 */     return null;
/*      */   }
/*      */ 
/*      */   private JPopupMenu createPopup(int x, int y) {
/*  467 */     if (!isEnabled(ChartSettings.Option.TRADING)) {
/*  468 */       return null;
/*      */     }
/*      */ 
/*  471 */     Map orderIds = new HashMap();
/*  472 */     orderIds = getHitOrderIdsHL(this.entryLines, orderIds, x, y);
/*  473 */     orderIds = getHitOrderIds(this.openingPoints, orderIds, x, y);
/*  474 */     orderIds = getHitOrderIds(this.mergingLines, orderIds, x, y);
/*  475 */     orderIds = getHitOrderIds(this.closingLines, orderIds, x, y);
/*  476 */     orderIds = getHitOrderIds(this.closingPoints, orderIds, x, y);
/*  477 */     if (!orderIds.isEmpty()) {
/*  478 */       JPopupMenu popupMenu = createPopup(orderIds, true, true, true);
/*  479 */       if (popupMenu != null) {
/*  480 */         return popupMenu;
/*      */       }
/*  482 */       orderIds.clear();
/*      */     }
/*      */ 
/*  485 */     orderIds = getHitOrderIdsHL(this.stopLossLines, orderIds, x, y);
/*  486 */     if (!orderIds.isEmpty()) {
/*  487 */       return createPopup(orderIds, false, true, false);
/*      */     }
/*  489 */     orderIds = getHitOrderIdsHL(this.takeProfitLines, orderIds, x, y);
/*  490 */     if (!orderIds.isEmpty()) {
/*  491 */       return createPopup(orderIds, false, false, true);
/*      */     }
/*      */ 
/*  495 */     return createTradingPopup(y);
/*      */   }
/*      */ 
/*      */   private JPopupMenu createPopup(Map<String, String> orderGroupIdsToOrderIds, boolean orderItems, boolean stopLossItems, boolean takeProfitItems) {
/*  499 */     JPopupMenu popupMenu = new JPopupMenu();
/*  500 */     OrderHistoricalData[] allOrders = this.ordersDataProviderManager.getOrdersData();
/*  501 */     for (OrderHistoricalData orderHistoricalData : allOrders) {
/*  502 */       if (orderGroupIdsToOrderIds.keySet().contains(orderHistoricalData.getOrderGroupId())) {
/*  503 */         if (orderGroupIdsToOrderIds.size() > 1) {
/*  504 */           JMenu jmenu = createJMenu(orderHistoricalData, (String)orderGroupIdsToOrderIds.get(orderHistoricalData.getOrderGroupId()), orderItems, stopLossItems, takeProfitItems);
/*  505 */           if (jmenu != null)
/*  506 */             popupMenu.add(jmenu);
/*      */         }
/*      */         else {
/*  509 */           JMenuItem[] jmenuItems = createJMenuItems(orderHistoricalData, (String)orderGroupIdsToOrderIds.get(orderHistoricalData.getOrderGroupId()), orderItems, stopLossItems, takeProfitItems);
/*  510 */           if (jmenuItems != null) {
/*  511 */             for (JMenuItem menuItem : jmenuItems) {
/*  512 */               if (menuItem != null)
/*  513 */                 popupMenu.add(menuItem);
/*      */               else {
/*  515 */                 popupMenu.addSeparator();
/*      */               }
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*  522 */     if (popupMenu.getComponentCount() > 0) {
/*  523 */       return popupMenu;
/*      */     }
/*  525 */     return null;
/*      */   }
/*      */ 
/*      */   private JMenu createJMenu(OrderHistoricalData orderHistoricalData, String orderId, boolean orderItems, boolean stopLossItems, boolean takeProfitItems)
/*      */   {
/*  530 */     JMenuItem[] jmenuItems = createJMenuItems(orderHistoricalData, orderId, orderItems, stopLossItems, takeProfitItems);
/*  531 */     if (jmenuItems != null) {
/*  532 */       JMenu jmenu = new JMenu(orderHistoricalData.getOrderGroupId());
/*  533 */       for (JMenuItem menuItem : jmenuItems) {
/*  534 */         if (menuItem != null)
/*  535 */           jmenu.add(menuItem);
/*      */         else {
/*  537 */           jmenu.addSeparator();
/*      */         }
/*      */       }
/*  540 */       return jmenu;
/*      */     }
/*  542 */     return null;
/*      */   }
/*      */ 
/*      */   private JMenuItem[] createJMenuItems(OrderHistoricalData orderHistoricalData, String orderId, boolean orderItems, boolean stopLossItems, boolean takeProfitItems)
/*      */   {
/*  547 */     if ((!orderHistoricalData.isOpened()) || (!orderHistoricalData.isClosed())) {
/*  548 */       IOrderUtils orderUtils = this.ordersDataProviderManager.getOrderUtils();
/*  549 */       boolean global = orderUtils.getAccountInfo().isGlobal();
/*  550 */       List menuItems = new ArrayList();
/*  551 */       StringBuilder infoItemStrBuilder = new StringBuilder();
/*  552 */       appendOrderInfo(orderHistoricalData, infoItemStrBuilder);
/*      */ 
/*  555 */       boolean addSeparator = false;
/*  556 */       if (orderItems) {
/*  557 */         if (!orderHistoricalData.isOpened())
/*      */         {
/*  559 */           JMenuItem cancelOrder = new JMenuItem(LocalizationManager.getText("menu.item.orders.manager.cancel.order"));
/*  560 */           cancelOrder.addActionListener(new ActionListener(orderId) {
/*      */             public void actionPerformed(ActionEvent e) {
/*  562 */               IOrderUtils orderUtils = OrdersManagerImpl.this.ordersDataProviderManager.getOrderUtils();
/*  563 */               orderUtils.cancelOrder(this.val$orderId);
/*      */             }
/*      */           });
/*  566 */           JMenuItem editOrder = new JMenuItem(LocalizationManager.getText("menu.item.orders.manager.edit.order"));
/*  567 */           editOrder.addActionListener(new ActionListener(orderId) {
/*      */             public void actionPerformed(ActionEvent e) {
/*  569 */               IOrderUtils orderUtils = OrdersManagerImpl.this.ordersDataProviderManager.getOrderUtils();
/*  570 */               orderUtils.editOrder(this.val$orderId, null, OrdersManagerImpl.this.getChartBean());
/*      */             }
/*      */           });
/*  573 */           if (addSeparator) {
/*  574 */             addSeparator = false;
/*  575 */             menuItems.add(null);
/*      */           }
/*  577 */           menuItems.add(cancelOrder);
/*  578 */           menuItems.add(editOrder);
/*  579 */         } else if ((!orderHistoricalData.isClosed()) && (!global)) {
/*  580 */           JMenuItem closeOrder = new JMenuItem(LocalizationManager.getText("menu.item.orders.manager.close"));
/*  581 */           closeOrder.addActionListener(new ActionListener(orderHistoricalData) {
/*      */             public void actionPerformed(ActionEvent e) {
/*  583 */               IOrderUtils orderUtils = OrdersManagerImpl.this.ordersDataProviderManager.getOrderUtils();
/*  584 */               orderUtils.closeOrder(this.val$orderHistoricalData.getOrderGroupId());
/*      */             }
/*      */           });
/*  587 */           JMenuItem condCloseOrder = new JMenuItem(LocalizationManager.getText("menu.item.orders.manager.cond.close"));
/*  588 */           condCloseOrder.addActionListener(new ActionListener(orderHistoricalData) {
/*      */             public void actionPerformed(ActionEvent e) {
/*  590 */               IOrderUtils orderUtils = OrdersManagerImpl.this.ordersDataProviderManager.getOrderUtils();
/*  591 */               orderUtils.condCloseOrder(this.val$orderHistoricalData.getOrderGroupId());
/*      */             }
/*      */           });
/*  594 */           if (addSeparator) {
/*  595 */             addSeparator = false;
/*  596 */             menuItems.add(null);
/*      */           }
/*  598 */           menuItems.add(closeOrder);
/*  599 */           menuItems.add(condCloseOrder);
/*      */         }
/*      */       }
/*  602 */       if ((stopLossItems) && (!global)) {
/*  603 */         String stopLossOrderId = orderId;
/*  604 */         for (OrderHistoricalData.OpenData pendingOpenData : orderHistoricalData.getPendingOrders()) {
/*  605 */           if (pendingOpenData.getStopLossOrderId() != null) {
/*  606 */             stopLossOrderId = pendingOpenData.getStopLossOrderId();
/*  607 */             break;
/*      */           }
/*      */         }
/*  610 */         String stopLossOrderIdFinal = stopLossOrderId == null ? orderHistoricalData.getEntryOrder().getStopLossOrderId() : stopLossOrderId;
/*  611 */         JMenuItem addStopLoss = new JMenuItem(LocalizationManager.getText("menu.item.orders.manager.add.stop.loss"));
/*  612 */         addStopLoss.addActionListener(new ActionListener(orderHistoricalData, orderId) {
/*      */           public void actionPerformed(ActionEvent e) {
/*  614 */             IOrderUtils orderUtils = OrdersManagerImpl.this.ordersDataProviderManager.getOrderUtils();
/*  615 */             orderUtils.addStopLoss(this.val$orderHistoricalData.getOrderGroupId(), this.val$orderId == null ? this.val$orderHistoricalData.getEntryOrder().getOrderId() : this.val$orderId);
/*      */           }
/*      */         });
/*  618 */         JMenuItem editStopLoss = new JMenuItem(LocalizationManager.getText("menu.item.orders.manager.edit.stop.loss"));
/*  619 */         editStopLoss.addActionListener(new ActionListener(orderId, orderHistoricalData, stopLossOrderIdFinal)
/*      */         {
/*      */           public void actionPerformed(ActionEvent e) {
/*  622 */             StopLossLine stopLossLine = (StopLossLine)OrdersManagerImpl.this.stopLossLines.get(this.val$orderId == null ? this.val$orderHistoricalData.getEntryOrder().getStopLossOrderId() : this.val$orderId);
/*  623 */             if (stopLossLine != null) {
/*  624 */               stopLossLine.hide(OrdersManagerImpl.this.guiRefresher);
/*  625 */               OrdersManagerImpl.this.refreshScreen();
/*      */             }
/*  627 */             IOrderUtils orderUtils = OrdersManagerImpl.this.ordersDataProviderManager.getOrderUtils();
/*      */ 
/*  629 */             orderUtils.editOrder(this.val$stopLossOrderIdFinal, new ActionListener(stopLossLine)
/*      */             {
/*      */               public void actionPerformed(ActionEvent e) {
/*  632 */                 this.val$stopLossLine.unhide();
/*  633 */                 OrdersManagerImpl.this.refreshScreen();
/*      */               }
/*      */             }
/*      */             , OrdersManagerImpl.this.getChartBean());
/*      */           }
/*      */         });
/*  640 */         JMenuItem cancelStopLoss = new JMenuItem(LocalizationManager.getText("menu.item.orders.manager.cancel.stop.loss"));
/*  641 */         cancelStopLoss.addActionListener(new ActionListener(stopLossOrderIdFinal) {
/*      */           public void actionPerformed(ActionEvent e) {
/*  643 */             IOrderUtils orderUtils = OrdersManagerImpl.this.ordersDataProviderManager.getOrderUtils();
/*  644 */             orderUtils.cancelOrder(this.val$stopLossOrderIdFinal);
/*      */           }
/*      */         });
/*  647 */         OrderHistoricalData.OpenData openData = null;
/*  648 */         if (orderId != null) {
/*  649 */           for (OrderHistoricalData.OpenData pendingOpenData : orderHistoricalData.getPendingOrders()) {
/*  650 */             if (((pendingOpenData.getStopLossOrderId() != null) && (pendingOpenData.getStopLossOrderId().equals(orderId))) || (pendingOpenData.getOrderId().equals(orderId))) {
/*  651 */               openData = pendingOpenData;
/*  652 */               break;
/*      */             }
/*      */           }
/*  655 */           if (openData == null)
/*  656 */             openData = orderHistoricalData.getEntryOrder();
/*      */         }
/*      */         else {
/*  659 */           openData = orderHistoricalData.getEntryOrder();
/*      */         }
/*  661 */         if ((openData != null) && (openData.getStopLossPrice().compareTo(BigDecimal.ZERO) >= 0)) {
/*  662 */           addStopLoss.setEnabled(false);
/*  663 */           editStopLoss.setEnabled(true);
/*  664 */           cancelStopLoss.setEnabled(true);
/*      */         } else {
/*  666 */           addStopLoss.setEnabled(true);
/*  667 */           editStopLoss.setEnabled(false);
/*  668 */           cancelStopLoss.setEnabled(false);
/*      */         }
/*  670 */         if (addSeparator) {
/*  671 */           addSeparator = false;
/*  672 */           menuItems.add(null);
/*      */         }
/*  674 */         if (orderItems) {
/*  675 */           menuItems.add(null);
/*  676 */           menuItems.add(addStopLoss);
/*      */         }
/*  678 */         menuItems.add(editStopLoss);
/*  679 */         menuItems.add(cancelStopLoss);
/*      */       }
/*      */ 
/*  682 */       if ((takeProfitItems) && (!global))
/*      */       {
/*  684 */         String takeProfitOrderId = orderId;
/*  685 */         for (OrderHistoricalData.OpenData pendingOpenData : orderHistoricalData.getPendingOrders()) {
/*  686 */           if (pendingOpenData.getTakeProfitOrderId() != null) {
/*  687 */             takeProfitOrderId = pendingOpenData.getTakeProfitOrderId();
/*  688 */             break;
/*      */           }
/*      */         }
/*      */ 
/*  692 */         String takeProfitOrderIdFinal = takeProfitOrderId == null ? orderHistoricalData.getEntryOrder().getTakeProfitOrderId() : takeProfitOrderId;
/*      */ 
/*  694 */         JMenuItem addTakeProfit = new JMenuItem(LocalizationManager.getText("menu.item.orders.manager.add.take.profit"));
/*  695 */         addTakeProfit.addActionListener(new ActionListener(orderHistoricalData, orderId) {
/*      */           public void actionPerformed(ActionEvent e) {
/*  697 */             IOrderUtils orderUtils = OrdersManagerImpl.this.ordersDataProviderManager.getOrderUtils();
/*  698 */             orderUtils.addTakeProfit(this.val$orderHistoricalData.getOrderGroupId(), this.val$orderId == null ? this.val$orderHistoricalData.getEntryOrder().getOrderId() : this.val$orderId);
/*      */           }
/*      */         });
/*  701 */         JMenuItem editTakeProfit = new JMenuItem(LocalizationManager.getText("menu.item.orders.manager.edit.take.profit"));
/*  702 */         editTakeProfit.addActionListener(new ActionListener(orderId, orderHistoricalData, takeProfitOrderIdFinal) {
/*      */           public void actionPerformed(ActionEvent e) {
/*  704 */             TakeProfitLine takeProfitLine = (TakeProfitLine)OrdersManagerImpl.this.takeProfitLines.get(this.val$orderId == null ? this.val$orderHistoricalData.getEntryOrder().getTakeProfitOrderId() : this.val$orderId);
/*  705 */             if (takeProfitLine != null) {
/*  706 */               takeProfitLine.hide(OrdersManagerImpl.this.guiRefresher);
/*  707 */               OrdersManagerImpl.this.refreshScreen();
/*      */             }
/*  709 */             IOrderUtils orderUtils = OrdersManagerImpl.this.ordersDataProviderManager.getOrderUtils();
/*      */ 
/*  712 */             orderUtils.editOrder(this.val$takeProfitOrderIdFinal, new ActionListener(takeProfitLine)
/*      */             {
/*      */               public void actionPerformed(ActionEvent e) {
/*  715 */                 this.val$takeProfitLine.unhide();
/*  716 */                 OrdersManagerImpl.this.refreshScreen();
/*      */               }
/*      */             }
/*      */             , OrdersManagerImpl.this.getChartBean());
/*      */           }
/*      */         });
/*  723 */         JMenuItem cancelTakeProfit = new JMenuItem(LocalizationManager.getText("menu.item.orders.manager.cancel.take.profit"));
/*  724 */         cancelTakeProfit.addActionListener(new ActionListener(takeProfitOrderIdFinal) {
/*      */           public void actionPerformed(ActionEvent e) {
/*  726 */             IOrderUtils orderUtils = OrdersManagerImpl.this.ordersDataProviderManager.getOrderUtils();
/*  727 */             orderUtils.cancelOrder(this.val$takeProfitOrderIdFinal);
/*      */           }
/*      */         });
/*  730 */         OrderHistoricalData.OpenData openData = null;
/*  731 */         if (orderId != null) {
/*  732 */           for (OrderHistoricalData.OpenData pendingOpenData : orderHistoricalData.getPendingOrders()) {
/*  733 */             if (((pendingOpenData.getTakeProfitOrderId() != null) && (pendingOpenData.getTakeProfitOrderId().equals(orderId))) || (pendingOpenData.getOrderId().equals(orderId))) {
/*  734 */               openData = pendingOpenData;
/*  735 */               break;
/*      */             }
/*      */           }
/*  738 */           if (openData == null)
/*  739 */             openData = orderHistoricalData.getEntryOrder();
/*      */         }
/*      */         else {
/*  742 */           openData = orderHistoricalData.getEntryOrder();
/*      */         }
/*  744 */         if ((openData != null) && (openData.getTakeProfitPrice().compareTo(BigDecimal.ZERO) >= 0)) {
/*  745 */           addTakeProfit.setEnabled(false);
/*  746 */           editTakeProfit.setEnabled(true);
/*  747 */           cancelTakeProfit.setEnabled(true);
/*      */         } else {
/*  749 */           addTakeProfit.setEnabled(true);
/*  750 */           editTakeProfit.setEnabled(false);
/*  751 */           cancelTakeProfit.setEnabled(false);
/*      */         }
/*  753 */         if (addSeparator) {
/*  754 */           addSeparator = false;
/*  755 */           menuItems.add(null);
/*      */         }
/*      */ 
/*  758 */         if (orderItems) {
/*  759 */           menuItems.add(null);
/*  760 */           menuItems.add(addTakeProfit);
/*      */         }
/*  762 */         menuItems.add(editTakeProfit);
/*  763 */         menuItems.add(cancelTakeProfit);
/*      */       }
/*  765 */       return (JMenuItem[])menuItems.toArray(new JMenuItem[menuItems.size()]);
/*      */     }
/*  767 */     return null;
/*      */   }
/*      */ 
/*      */   private JPopupMenu createTradingPopup(int y)
/*      */   {
/*  772 */     TickData latestTick = getChartBean().getFeedDataProvider().getLastTick(getInstrument());
/*  773 */     double mousePrice = StratUtils.round05Pips(this.mapper.vy(y));
/*  774 */     if (latestTick == null) {
/*  775 */       return null;
/*      */     }
/*  777 */     ActionListener action = new ActionListener(mousePrice)
/*      */     {
/*      */       public void actionPerformed(ActionEvent e) {
/*  780 */         IEngine.OrderCommand command = IEngine.OrderCommand.valueOf(e.getActionCommand());
/*  781 */         JComponent chartComponent = OrdersManagerImpl.this.guiRefresher.getChartsContainer();
/*  782 */         Container parent = chartComponent.getRootPane().getParent();
/*  783 */         Window window = null;
/*  784 */         if ((parent instanceof Window)) {
/*  785 */           window = (Window)parent;
/*      */         }
/*  787 */         OrdersManagerImpl.this.ordersDataProviderManager.getOrderUtils().createNewOrder(window, OrdersManagerImpl.this.getInstrument(), command, this.val$mousePrice, Integer.valueOf(OrdersManagerImpl.this.getChartBean().getId()));
/*      */       }
/*      */     };
/*  790 */     JPopupMenu popupMenu = new JPopupMenu();
/*  791 */     if (mousePrice > latestTick.bid) {
/*  792 */       JMenuItem sellLimit = new JMenuItem(LocalizationManager.getTextWithArguments("menu.item.orders.manager.sell.limit", new Object[] { String.valueOf(mousePrice) }));
/*  793 */       sellLimit.setActionCommand(IEngine.OrderCommand.SELLLIMIT.name());
/*  794 */       sellLimit.addActionListener(action);
/*  795 */       popupMenu.add(sellLimit);
/*  796 */       JMenuItem buyStopByBid = new JMenuItem(LocalizationManager.getTextWithArguments("menu.item.orders.manager.buy.stop.by.bid", new Object[] { String.valueOf(mousePrice) }));
/*  797 */       buyStopByBid.setActionCommand(IEngine.OrderCommand.BUYSTOP_BYBID.name());
/*  798 */       buyStopByBid.addActionListener(action);
/*  799 */       popupMenu.add(buyStopByBid);
/*      */     }
/*  801 */     if (mousePrice > latestTick.ask) {
/*  802 */       JMenuItem buyStopByAsk = new JMenuItem(LocalizationManager.getTextWithArguments("menu.item.orders.manager.buy.stop.by.ask", new Object[] { String.valueOf(mousePrice) }));
/*  803 */       buyStopByAsk.setActionCommand(IEngine.OrderCommand.BUYSTOP.name());
/*  804 */       buyStopByAsk.addActionListener(action);
/*  805 */       popupMenu.add(buyStopByAsk);
/*      */     }
/*  807 */     if (mousePrice < latestTick.ask) {
/*  808 */       JMenuItem buyLimit = new JMenuItem(LocalizationManager.getTextWithArguments("menu.item.orders.manager.buy.limit", new Object[] { String.valueOf(mousePrice) }));
/*  809 */       buyLimit.setActionCommand(IEngine.OrderCommand.BUYLIMIT.name());
/*  810 */       buyLimit.addActionListener(action);
/*  811 */       popupMenu.add(buyLimit);
/*  812 */       JMenuItem sellStopByAsk = new JMenuItem(LocalizationManager.getTextWithArguments("menu.item.orders.manager.sell.stop.by.ask", new Object[] { String.valueOf(mousePrice) }));
/*  813 */       sellStopByAsk.setActionCommand(IEngine.OrderCommand.SELLSTOP_BYASK.name());
/*  814 */       sellStopByAsk.addActionListener(action);
/*  815 */       popupMenu.add(sellStopByAsk);
/*      */     }
/*  817 */     if (mousePrice < latestTick.bid) {
/*  818 */       JMenuItem sellStopByBid = new JMenuItem(LocalizationManager.getTextWithArguments("menu.item.orders.manager.sell.stop.by.bid", new Object[] { String.valueOf(mousePrice) }));
/*  819 */       sellStopByBid.setActionCommand(IEngine.OrderCommand.SELLSTOP.name());
/*  820 */       sellStopByBid.addActionListener(action);
/*  821 */       popupMenu.add(sellStopByBid);
/*      */     }
/*  823 */     popupMenu.addSeparator();
/*  824 */     if (mousePrice > latestTick.bid) {
/*  825 */       JMenuItem placeBid = new JMenuItem(LocalizationManager.getTextWithArguments("menu.item.orders.manager.place.offer", new Object[] { String.valueOf(mousePrice) }));
/*  826 */       placeBid.setActionCommand(IEngine.OrderCommand.PLACE_OFFER.name());
/*  827 */       placeBid.addActionListener(action);
/*  828 */       popupMenu.add(placeBid);
/*      */     }
/*  830 */     if (mousePrice < latestTick.ask) {
/*  831 */       JMenuItem placeOffer = new JMenuItem(LocalizationManager.getTextWithArguments("menu.item.orders.manager.place.bid", new Object[] { String.valueOf(mousePrice) }));
/*  832 */       placeOffer.setActionCommand(IEngine.OrderCommand.PLACE_BID.name());
/*  833 */       placeOffer.addActionListener(action);
/*  834 */       popupMenu.add(placeOffer);
/*      */     }
/*  836 */     popupMenu.addSeparator();
/*  837 */     JMenuItem buy = new JMenuItem(LocalizationManager.getText("menu.item.orders.manager.buy.market"));
/*  838 */     buy.setActionCommand(IEngine.OrderCommand.BUY.name());
/*  839 */     buy.addActionListener(action);
/*  840 */     popupMenu.add(buy);
/*  841 */     JMenuItem sell = new JMenuItem(LocalizationManager.getText("menu.item.orders.manager.sell.marker"));
/*  842 */     sell.setActionCommand(IEngine.OrderCommand.SELL.name());
/*  843 */     sell.addActionListener(action);
/*  844 */     popupMenu.add(sell);
/*  845 */     return popupMenu;
/*      */   }
/*      */ 
/*      */   public String getToolTipText(MouseEvent event) {
/*  849 */     Map orderIds = new HashMap();
/*  850 */     orderIds = getHitOrderIds(this.entryLines, orderIds, event.getX(), event.getY());
/*  851 */     orderIds = getHitOrderIds(this.stopLossLines, orderIds, event.getX(), event.getY());
/*  852 */     orderIds = getHitOrderIds(this.takeProfitLines, orderIds, event.getX(), event.getY());
/*  853 */     orderIds = getHitOrderIds(this.openingPoints, orderIds, event.getX(), event.getY());
/*  854 */     orderIds = getHitOrderIds(this.mergingLines, orderIds, event.getX(), event.getY());
/*  855 */     orderIds = getHitOrderIds(this.closingLines, orderIds, event.getX(), event.getY());
/*  856 */     orderIds = getHitOrderIds(this.closingPoints, orderIds, event.getX(), event.getY());
/*  857 */     if (!orderIds.isEmpty()) {
/*  858 */       return createToolTipText(orderIds);
/*      */     }
/*  860 */     return null;
/*      */   }
/*      */ 
/*      */   public void start()
/*      */   {
/*  866 */     this.ordersDataProviderManager.start();
/*      */   }
/*      */ 
/*      */   public void dispose()
/*      */   {
/*  871 */     this.ordersDataProviderManager.dispose();
/*      */   }
/*      */ 
/*      */   private String createToolTipText(Map<String, String> orderGroupIds) {
/*  875 */     OrderHistoricalData[] allOrders = this.ordersDataProviderManager.getOrdersData();
/*  876 */     StringBuilder tooltipText = new StringBuilder();
/*  877 */     tooltipText.append("<html>");
/*  878 */     for (OrderHistoricalData orderHistoricalData : allOrders) {
/*  879 */       if (orderGroupIds.keySet().contains(orderHistoricalData.getOrderGroupId())) {
/*  880 */         appendOrderInfo(orderHistoricalData, tooltipText);
/*  881 */         tooltipText.append("<BR>");
/*      */       }
/*      */     }
/*  884 */     if (tooltipText.length() > 4) {
/*  885 */       tooltipText.setLength(tooltipText.length() - 4);
/*      */     }
/*  887 */     tooltipText.append("</html>");
/*  888 */     return tooltipText.toString();
/*      */   }
/*      */ 
/*      */   private void appendOrderInfo(OrderHistoricalData orderHistoricalData, StringBuilder tooltipText) {
/*  892 */     if (orderHistoricalData.getEntryOrder() != null) {
/*  893 */       tooltipText.append(orderHistoricalData.getOrderGroupId()).append(": ").append(getAmountText(orderHistoricalData.getEntryOrder()).toPlainString()).append("mil @ ").append(orderHistoricalData.getEntryOrder().getOpenPrice().toPlainString());
/*      */     }
/*  898 */     else if (orderHistoricalData.getPendingOrders().size() == 1) {
/*  899 */       tooltipText.append(orderHistoricalData.getOrderGroupId()).append(": ").append(getAmountText((OrderHistoricalData.OpenData)orderHistoricalData.getPendingOrders().get(0)).toPlainString()).append("mil @ ").append(((OrderHistoricalData.OpenData)orderHistoricalData.getPendingOrders().get(0)).getOpenPrice().toPlainString());
/*      */     }
/*      */     else
/*      */     {
/*  904 */       tooltipText.append(LocalizationManager.getText("tooltip.orders.manager.oco.orders"));
/*  905 */       for (OrderHistoricalData.OpenData pendingOpeningOrder : orderHistoricalData.getPendingOrders()) {
/*  906 */         tooltipText.append(pendingOpeningOrder.getOrderId()).append(" - ").append(getAmountText(pendingOpeningOrder).toPlainString()).append("mil @ ").append(pendingOpeningOrder.getOpenPrice().toPlainString());
/*      */ 
/*  910 */         if (orderHistoricalData.getPendingOrders().indexOf(pendingOpeningOrder) < orderHistoricalData.getPendingOrders().size() - 1)
/*  911 */           tooltipText.append(new StringBuilder().append(" ").append(LocalizationManager.getText("tooltip.orders.manager.or")).append(" ").toString());
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   private BigDecimal getAmountText(OrderHistoricalData.OpenData openData)
/*      */   {
/*  918 */     BigDecimal amount = openData.getAmount();
/*  919 */     int signAfterZero = 2;
/*  920 */     if (amount.compareTo(SIGNS_AZ_THRESHOLD) < 0) {
/*  921 */       signAfterZero = 3;
/*      */     }
/*  923 */     amount = amount.divide(ONE_MILLION, signAfterZero, RoundingMode.HALF_EVEN).stripTrailingZeros();
/*  924 */     return amount;
/*      */   }
/*      */ 
/*      */   private Map<String, String> getHitOrderIds(Map<String, ? extends OrderPart> orderPartsMap, Map<String, String> orderIds, int x, int y) {
/*  928 */     for (OrderPart orderPart : orderPartsMap.values()) {
/*  929 */       if (orderPart.hitPoint(x, y)) {
/*  930 */         orderIds.put(orderPart.getOrderGroupId(), null);
/*      */       }
/*      */     }
/*  933 */     return orderIds;
/*      */   }
/*      */ 
/*      */   private Map<String, String> getHitOrderIdsHL(Map<String, ? extends HorizontalLine> orderPartsMap, Map<String, String> orderIds, int x, int y) {
/*  937 */     for (HorizontalLine orderPart : orderPartsMap.values()) {
/*  938 */       if (orderPart.hitPoint(x, y)) {
/*  939 */         orderIds.put(orderPart.getOrderGroupId(), orderPart.getOrderId());
/*      */       }
/*      */     }
/*  942 */     return orderIds;
/*      */   }
/*      */ 
/*      */   public HorizontalLine[] getSelectedHorizontalLines() {
/*  946 */     return (HorizontalLine[])this.selectedHorizontalLines.toArray(new HorizontalLine[this.selectedHorizontalLines.size()]);
/*      */   }
/*      */ 
/*      */   public Map<String, EntryLine> getEntryLines() {
/*  950 */     return this.entryLines;
/*      */   }
/*      */ 
/*      */   public Map<String, StopLossLine> getStopLossLines() {
/*  954 */     return this.stopLossLines;
/*      */   }
/*      */ 
/*      */   public Map<String, TakeProfitLine> getTakeProfitLines() {
/*  958 */     return this.takeProfitLines;
/*      */   }
/*      */ 
/*      */   public Map<String, OpeningPoint> getOpeningPoints() {
/*  962 */     return this.openingPoints;
/*      */   }
/*      */ 
/*      */   public Map<String, MergingLine> getMergingLines() {
/*  966 */     return this.mergingLines;
/*      */   }
/*      */ 
/*      */   public Map<String, ClosingLine> getClosingLines() {
/*  970 */     return this.closingLines;
/*      */   }
/*      */ 
/*      */   public Map<String, ClosingPoint> getClosingPoints() {
/*  974 */     return this.closingPoints;
/*      */   }
/*      */ 
/*      */   public void prepareOrderParts()
/*      */   {
/*  982 */     this.paintTag += 1;
/*  983 */     prepareOrderPartsInner();
/*  984 */     removeOldParts();
/*      */   }
/*      */ 
/*      */   private void prepareOrderPartsInner() {
/*  988 */     OrderHistoricalData[] orders = this.ordersDataProviderManager.getOrdersData();
/*  989 */     for (OrderHistoricalData order : orders)
/*      */     {
/*  991 */       prepareOrderPartsInner(order);
/*      */     }
/*      */ 
/*  994 */     for (MergingLine mergingLine : this.mergingLines.values())
/*  995 */       if (mergingLine.getPaintTag() == this.paintTag)
/*      */       {
/*  997 */         if (!mergingLine.isMergedFromPointSet()) {
/*  998 */           OrderHistoricalData order = findOrder(orders, mergingLine.getOrderGroupId());
/*  999 */           if (order != null) {
/* 1000 */             prepareMergingLineFrom(mergingLine, order.getEntryOrder().getOpenPrice(), order.getEntryOrder().getFillTime(), order.getEntryOrder().getSide());
/* 1001 */           } else if ((mergingLine.getX2() > 0.0D) && (mergingLine.getY2() > 0.0D) && (mergingLine.getX2() < this.mapper.getWidth()) && (mergingLine.getY2() < this.mapper.getHeight()))
/*      */           {
/* 1003 */             order = findOrder(orders, mergingLine.getMergedToOrderGroupId());
/* 1004 */             if (order != null) {
/* 1005 */               BigDecimal price = order.getEntryOrder().getOpenPrice();
/* 1006 */               for (int i = 0; i < order.getEntryOrder().getMergedFrom().length; i++) {
/* 1007 */                 if (order.getEntryOrder().getMergedFrom()[i].equals(mergingLine.getOrderGroupId())) {
/* 1008 */                   boolean up = i % 2 == 0;
/* 1009 */                   price = price.add(BigDecimal.valueOf((up ? 1 : -1) * (1 + i / 2) * 3).multiply(BigDecimal.valueOf(getInstrument().getPipValue())));
/* 1010 */                   break;
/*      */                 }
/*      */               }
/* 1013 */               prepareMergingLineFrom(mergingLine, price, this.ordersDataProviderManager.getStartTime(), order.getEntryOrder().getSide());
/*      */             }
/*      */ 
/*      */           }
/*      */ 
/*      */         }
/* 1019 */         else if (!mergingLine.isMergedToPointSet()) {
/* 1020 */           OrderHistoricalData order = findOrder(orders, mergingLine.getMergedToOrderGroupId());
/* 1021 */           if (order != null) {
/* 1022 */             prepareMergingLineTo(mergingLine, order.getEntryOrder().getOpenPrice(), order.getEntryOrder().getFillTime());
/* 1023 */           } else if ((mergingLine.getX1() > 0.0D) && (mergingLine.getY1() > 0.0D) && (mergingLine.getX1() < this.mapper.getWidth()) && (mergingLine.getY1() < this.mapper.getHeight()))
/*      */           {
/* 1025 */             order = findOrder(orders, mergingLine.getOrderGroupId());
/* 1026 */             if (order != null) {
/* 1027 */               BigDecimal price = order.getEntryOrder().getOpenPrice();
/*      */ 
/* 1029 */               price = price.add(BigDecimal.valueOf(3L).multiply(BigDecimal.valueOf(getInstrument().getPipValue())));
/* 1030 */               prepareMergingLineTo(mergingLine, price, this.ordersDataProviderManager.getEndTime());
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/*      */   }
/*      */ 
/*      */   private void prepareOrderPartsInner(OrderHistoricalData order)
/*      */   {
/* 1042 */     OrderHistoricalData.OpenData entryOrder = order.getEntryOrder();
/* 1043 */     if ((entryOrder == null) && (order.getPendingOrders().isEmpty()))
/*      */     {
/* 1045 */       return;
/*      */     }
/*      */ 
/* 1049 */     if ((!order.isClosed()) && ((order.getMergedToGroupId() == null) || ((order.getMergedToGroupId() != null) && (isEnabled(ChartSettings.Option.CLOSED_ORDERS))))) {
/* 1050 */       if ((isEnabled(ChartSettings.Option.ENTRY_ORDERS)) && (order.getPendingOrders().size() > 0)) {
/* 1051 */         for (OrderHistoricalData.OpenData pendingOpeningOrder : order.getPendingOrders()) {
/* 1052 */           EntryLine entryLine = (EntryLine)this.entryLines.get(pendingOpeningOrder.getOrderId());
/* 1053 */           if (entryLine == null) {
/* 1054 */             entryLine = new EntryLine(order.getOrderGroupId(), this.chartState.getTheme());
/* 1055 */             this.entryLines.put(pendingOpeningOrder.getOrderId(), entryLine);
/*      */           }
/*      */ 
/* 1058 */           entryLine.setOrderGroupId(order.getOrderGroupId());
/* 1059 */           entryLine.setOpenOrderCommand(pendingOpeningOrder.getSide());
/* 1060 */           entryLine.updatePaintTag(this.paintTag);
/* 1061 */           prepareOpeningLine(entryLine, pendingOpeningOrder.getOpenPrice(), pendingOpeningOrder.getOpenSlippage(), pendingOpeningOrder.getSide(), pendingOpeningOrder.getOrderId(), pendingOpeningOrder.isExecuting());
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/* 1066 */       if (((order.isClosed()) && (isEnabled(ChartSettings.Option.CLOSED_ORDERS))) || ((!order.isClosed()) && (isEnabled(ChartSettings.Option.OPEN_POSITIONS))) || ((isEnabled(ChartSettings.Option.ENTRY_ORDERS)) && (!order.isOpened())))
/*      */       {
/* 1070 */         if (entryOrder != null) {
/* 1071 */           prepareStopLossTakeProfitLines(entryOrder, order.getOrderGroupId());
/*      */         }
/* 1073 */         for (OrderHistoricalData.OpenData pendingOpeningOrder : order.getPendingOrders()) {
/* 1074 */           prepareStopLossTakeProfitLines(pendingOpeningOrder, order.getOrderGroupId());
/*      */         }
/*      */       }
/*      */     }
/*      */ 
/* 1079 */     if (((order.isClosed()) && (isEnabled(ChartSettings.Option.CLOSED_ORDERS))) || ((!order.isClosed()) && (isEnabled(ChartSettings.Option.OPEN_POSITIONS)) && ((order.getMergedToGroupId() == null) || ((order.getMergedToGroupId() != null) && (isEnabled(ChartSettings.Option.CLOSED_ORDERS)))) && (order.isOpened())))
/*      */     {
/* 1081 */       if (prepareClosingLinesAndPoints(getInstrument(), order)) {
/* 1082 */         OpeningPoint openingPoint = (OpeningPoint)this.openingPoints.get(order.getOrderGroupId());
/* 1083 */         if (openingPoint == null) {
/* 1084 */           openingPoint = new OpeningPoint(order.getOrderGroupId(), entryOrder.getFillTime(), entryOrder.getOpenPrice().doubleValue());
/* 1085 */           this.openingPoints.put(order.getOrderGroupId(), openingPoint);
/*      */         }
/* 1087 */         openingPoint.updatePaintTag(this.paintTag);
/* 1088 */         prepareOpeningPoint(openingPoint, new StringBuilder().append(order.getOrderGroupId()).append((entryOrder.getLabel() == null) || (entryOrder.getLabel().equals("")) ? "" : new StringBuilder().append("-").append(entryOrder.getLabel()).toString()).toString(), entryOrder);
/*      */       }
/*      */ 
/* 1091 */       if ((entryOrder.getMergedFrom() != null) && (isEnabled(ChartSettings.Option.CLOSED_ORDERS))) {
/* 1092 */         for (String mergedOrderId : entryOrder.getMergedFrom()) {
/* 1093 */           prepareMergingLinesToPoints(order.getOrderGroupId(), entryOrder, mergedOrderId);
/*      */         }
/*      */       }
/*      */ 
/* 1097 */       if (order.getMergedToGroupId() != null)
/* 1098 */         prepareMergingLinesFromPoints(order, order.getMergedToGroupId());
/*      */     }
/*      */   }
/*      */ 
/*      */   private void prepareStopLossTakeProfitLines(OrderHistoricalData.OpenData openData, String orderGroupId)
/*      */   {
/* 1104 */     if ((isEnabled(ChartSettings.Option.STOP_ORDERS)) && (openData.getStopLossPrice().compareTo(BigDecimal.ZERO) >= 0)) {
/* 1105 */       StopLossLine stopLossLine = (StopLossLine)this.stopLossLines.get(openData.getOrderId());
/* 1106 */       if (stopLossLine == null) {
/* 1107 */         stopLossLine = new StopLossLine(orderGroupId, this.chartState.getTheme());
/* 1108 */         this.stopLossLines.put(openData.getOrderId(), stopLossLine);
/*      */       }
/* 1110 */       stopLossLine.updatePaintTag(this.paintTag);
/* 1111 */       prepareStopLossLine(stopLossLine, openData.getSide(), openData.getStopLossPrice(), openData.getStopLossSlippage(), openData.isStopLossByBid(), openData.getStopLossOrderId());
/*      */     }
/* 1113 */     if ((isEnabled(ChartSettings.Option.STOP_ORDERS)) && (openData.getTakeProfitPrice().compareTo(BigDecimal.ZERO) >= 0)) {
/* 1114 */       TakeProfitLine takeProfitLine = (TakeProfitLine)this.takeProfitLines.get(openData.getOrderId());
/* 1115 */       if (takeProfitLine == null) {
/* 1116 */         takeProfitLine = new TakeProfitLine(orderGroupId, this.chartState.getTheme());
/* 1117 */         this.takeProfitLines.put(openData.getOrderId(), takeProfitLine);
/*      */       }
/* 1119 */       takeProfitLine.updatePaintTag(this.paintTag);
/* 1120 */       prepareTakeProfitLine(takeProfitLine, openData.getSide(), openData.getTakeProfitPrice(), openData.getTakeProfitSlippage(), openData.getTakeProfitOrderId());
/*      */     }
/*      */   }
/*      */ 
/*      */   private boolean prepareClosingLinesAndPoints(Instrument instrument, OrderHistoricalData order) {
/* 1125 */     boolean drawOpenPoint = true;
/* 1126 */     for (Map.Entry entry : order.getCloseDataMap().entrySet()) {
/* 1127 */       OrderHistoricalData.CloseData closeData = (OrderHistoricalData.CloseData)entry.getValue();
/* 1128 */       if (order.getEntryOrder().getFillTime() != closeData.getCloseTime()) {
/* 1129 */         ClosingLine closingLine = (ClosingLine)this.closingLines.get(entry.getKey());
/* 1130 */         if (closingLine == null) {
/* 1131 */           closingLine = new ClosingLine(order.getOrderGroupId());
/* 1132 */           this.closingLines.put(entry.getKey(), closingLine);
/*      */         }
/* 1134 */         closingLine.updatePaintTag(this.paintTag);
/* 1135 */         prepareClosingLine(closingLine, order.getEntryOrder().getOpenPrice(), order.getEntryOrder().getFillTime(), closeData.getClosePrice(), closeData.getCloseTime(), order.getEntryOrder().getSide());
/*      */       }
/*      */       else {
/* 1138 */         drawOpenPoint = false;
/*      */       }
/* 1140 */       ClosingPoint closingPoint = (ClosingPoint)this.closingPoints.get(entry.getKey());
/* 1141 */       if (closingPoint == null) {
/* 1142 */         closingPoint = new ClosingPoint(order.getOrderGroupId(), closeData.getCloseTime(), closeData.getClosePrice().doubleValue());
/* 1143 */         this.closingPoints.put(entry.getKey(), closingPoint);
/*      */       }
/* 1145 */       closingPoint.updatePaintTag(this.paintTag);
/* 1146 */       prepareClosingPoint(closingPoint, closeData.getClosePrice(), closeData.getCloseTime(), order.getEntryOrder().getSide(), instrument, order.getEntryOrder().getOpenPrice());
/*      */     }
/* 1148 */     return drawOpenPoint;
/*      */   }
/*      */ 
/*      */   private OrderHistoricalData findOrder(OrderHistoricalData[] orders, String orderGroupId) {
/* 1152 */     for (OrderHistoricalData order : orders) {
/* 1153 */       if (order.getOrderGroupId().equals(orderGroupId)) {
/* 1154 */         return order;
/*      */       }
/*      */     }
/* 1157 */     return null;
/*      */   }
/*      */ 
/*      */   private void removeOldParts() {
/* 1161 */     for (Iterator iterator = this.entryLines.entrySet().iterator(); iterator.hasNext(); ) {
/* 1162 */       Map.Entry entry = (Map.Entry)iterator.next();
/* 1163 */       if (((EntryLine)entry.getValue()).getPaintTag() != this.paintTag) {
/* 1164 */         iterator.remove();
/*      */       }
/*      */     }
/* 1167 */     for (Iterator iterator = this.stopLossLines.entrySet().iterator(); iterator.hasNext(); ) {
/* 1168 */       Map.Entry entry = (Map.Entry)iterator.next();
/* 1169 */       if (((StopLossLine)entry.getValue()).getPaintTag() != this.paintTag) {
/* 1170 */         iterator.remove();
/*      */       }
/*      */     }
/* 1173 */     for (Iterator iterator = this.takeProfitLines.entrySet().iterator(); iterator.hasNext(); ) {
/* 1174 */       Map.Entry entry = (Map.Entry)iterator.next();
/* 1175 */       if (((TakeProfitLine)entry.getValue()).getPaintTag() != this.paintTag) {
/* 1176 */         iterator.remove();
/*      */       }
/*      */     }
/* 1179 */     for (Iterator iterator = this.openingPoints.entrySet().iterator(); iterator.hasNext(); ) {
/* 1180 */       Map.Entry entry = (Map.Entry)iterator.next();
/* 1181 */       if (((OpeningPoint)entry.getValue()).getPaintTag() != this.paintTag) {
/* 1182 */         iterator.remove();
/*      */       }
/*      */     }
/* 1185 */     for (Iterator iterator = this.mergingLines.entrySet().iterator(); iterator.hasNext(); ) {
/* 1186 */       Map.Entry entry = (Map.Entry)iterator.next();
/* 1187 */       if (((MergingLine)entry.getValue()).getPaintTag() != this.paintTag) {
/* 1188 */         iterator.remove();
/*      */       }
/*      */     }
/* 1191 */     for (Iterator iterator = this.closingLines.entrySet().iterator(); iterator.hasNext(); ) {
/* 1192 */       Map.Entry entry = (Map.Entry)iterator.next();
/* 1193 */       if (((ClosingLine)entry.getValue()).getPaintTag() != this.paintTag) {
/* 1194 */         iterator.remove();
/*      */       }
/*      */     }
/* 1197 */     for (Iterator iterator = this.closingPoints.entrySet().iterator(); iterator.hasNext(); ) {
/* 1198 */       Map.Entry entry = (Map.Entry)iterator.next();
/* 1199 */       if (((ClosingPoint)entry.getValue()).getPaintTag() != this.paintTag) {
/* 1200 */         iterator.remove();
/*      */       }
/*      */     }
/* 1203 */     for (HorizontalLine horizontalLine : getSelectedHorizontalLines())
/* 1204 */       if (horizontalLine.getPaintTag() != this.paintTag)
/* 1205 */         cancelChangesMadeToHorizontalLine(horizontalLine);
/*      */   }
/*      */ 
/*      */   private void prepareMergingLinesToPoints(String orderGroupId, OrderHistoricalData.OpenData openData, String mergedOrderGroupId)
/*      */   {
/* 1211 */     MergingLine mergingLine = (MergingLine)this.mergingLines.get(mergedOrderGroupId);
/* 1212 */     if (mergingLine == null) {
/* 1213 */       mergingLine = new MergingLine(mergedOrderGroupId, orderGroupId);
/* 1214 */       this.mergingLines.put(mergedOrderGroupId, mergingLine);
/*      */     }
/* 1216 */     if (mergingLine.getPaintTag() != this.paintTag) {
/* 1217 */       mergingLine.setCoordinates((0.0D / 0.0D), (0.0D / 0.0D), (0.0D / 0.0D), (0.0D / 0.0D));
/*      */     }
/* 1219 */     mergingLine.updatePaintTag(this.paintTag);
/* 1220 */     prepareMergingLineTo(mergingLine, openData.getOpenPrice(), openData.getFillTime());
/*      */   }
/*      */ 
/*      */   private void prepareMergingLinesFromPoints(OrderHistoricalData order, String mergedToOrderGroupId) {
/* 1224 */     MergingLine mergingLine = (MergingLine)this.mergingLines.get(order.getOrderGroupId());
/* 1225 */     if (mergingLine == null) {
/* 1226 */       mergingLine = new MergingLine(order.getOrderGroupId(), mergedToOrderGroupId);
/* 1227 */       this.mergingLines.put(order.getOrderGroupId(), mergingLine);
/*      */     }
/* 1229 */     if (mergingLine.getPaintTag() != this.paintTag) {
/* 1230 */       mergingLine.setCoordinates((0.0D / 0.0D), (0.0D / 0.0D), (0.0D / 0.0D), (0.0D / 0.0D));
/*      */     }
/* 1232 */     mergingLine.updatePaintTag(this.paintTag);
/* 1233 */     OrderHistoricalData.OpenData entryOrder = order.getEntryOrder();
/* 1234 */     prepareMergingLineFrom(mergingLine, entryOrder.getOpenPrice(), entryOrder.getFillTime(), entryOrder.getSide());
/*      */   }
/*      */ 
/*      */   protected void prepareOpeningLine(EntryLine entryLine, BigDecimal openPrice, BigDecimal slippage, IEngine.OrderCommand side, String orderId, boolean executing) {
/* 1238 */     float newY = this.mapper.yv(openPrice.doubleValue());
/* 1239 */     if ((entryLine.isSelected()) && (entryLine.getPrice().compareTo(openPrice) != 0)) {
/* 1240 */       cancelChangesMadeToHorizontalLine(entryLine);
/*      */     }
/* 1242 */     if ((entryLine.isHidden()) && (entryLine.getPrice().compareTo(openPrice) != 0)) {
/* 1243 */       entryLine.unhide();
/*      */     }
/* 1245 */     entryLine.setColor(this.chartState.getTheme().getColor(side.isLong() ? ITheme.ChartElement.ORDER_OPEN_BUY : ITheme.ChartElement.ORDER_OPEN_SELL));
/* 1246 */     entryLine.setY(newY);
/* 1247 */     entryLine.setPrice(openPrice);
/* 1248 */     entryLine.setSlippage(slippage);
/* 1249 */     entryLine.setOrderId(orderId);
/* 1250 */     if ((executing) && (entryLine.getOpenOrderCommand() != IEngine.OrderCommand.PLACE_BID) && (entryLine.getOpenOrderCommand() != IEngine.OrderCommand.PLACE_OFFER))
/* 1251 */       entryLine.setEditable(false);
/*      */   }
/*      */ 
/*      */   protected void prepareStopLossLine(StopLossLine stopLossLine, IEngine.OrderCommand orderCommand, BigDecimal stopLossPrice, BigDecimal stopLossSlippage, boolean stopLossByBid, String orderId)
/*      */   {
/* 1256 */     float newY = this.mapper.yv(stopLossPrice.doubleValue());
/* 1257 */     if ((stopLossLine.isSelected()) && (stopLossLine.getPrice().compareTo(stopLossPrice) != 0)) {
/* 1258 */       cancelChangesMadeToHorizontalLine(stopLossLine);
/*      */     }
/* 1260 */     if ((stopLossLine.isHidden()) && (stopLossLine.getPrice().compareTo(stopLossPrice) != 0)) {
/* 1261 */       stopLossLine.unhide();
/*      */     }
/* 1263 */     stopLossLine.setColor(this.chartState.getTheme().getColor(orderCommand.isLong() ? ITheme.ChartElement.ORDER_CLOSE_BUY : ITheme.ChartElement.ORDER_CLOSE_SELL));
/*      */ 
/* 1267 */     stopLossLine.setY(newY);
/* 1268 */     stopLossLine.setPrice(stopLossPrice);
/* 1269 */     stopLossLine.setOpenOrderCommand(orderCommand);
/* 1270 */     stopLossLine.setSlippage(stopLossSlippage);
/* 1271 */     stopLossLine.setStopLossByBid(stopLossByBid);
/* 1272 */     stopLossLine.setOrderId(orderId);
/*      */   }
/*      */ 
/*      */   protected void prepareTakeProfitLine(TakeProfitLine takeProfitLine, IEngine.OrderCommand orderCommand, BigDecimal takeProfitPrice, BigDecimal takeProfitSlippage, String orderId) {
/* 1276 */     float newY = this.mapper.yv(takeProfitPrice.doubleValue());
/* 1277 */     if ((takeProfitLine.isSelected()) && (takeProfitLine.getPrice().compareTo(takeProfitPrice) != 0)) {
/* 1278 */       cancelChangesMadeToHorizontalLine(takeProfitLine);
/*      */     }
/* 1280 */     if ((takeProfitLine.isHidden()) && (takeProfitLine.getPrice().compareTo(takeProfitPrice) != 0)) {
/* 1281 */       takeProfitLine.unhide();
/*      */     }
/* 1283 */     takeProfitLine.setColor(this.chartState.getTheme().getColor(orderCommand.isLong() ? ITheme.ChartElement.ORDER_CLOSE_BUY : ITheme.ChartElement.ORDER_CLOSE_SELL));
/*      */ 
/* 1287 */     takeProfitLine.setY(newY);
/* 1288 */     takeProfitLine.setPrice(takeProfitPrice);
/* 1289 */     takeProfitLine.setOpenOrderCommand(orderCommand);
/* 1290 */     takeProfitLine.setSlippage(takeProfitSlippage);
/* 1291 */     takeProfitLine.setOrderId(orderId);
/*      */   }
/*      */ 
/*      */   protected void prepareOpeningPoint(OpeningPoint openingPoint, String label, OrderHistoricalData.OpenData order)
/*      */   {
/* 1296 */     if (isEnabled(ChartSettings.Option.POSITIONS_LABELS))
/* 1297 */       openingPoint.setText(label);
/*      */     else {
/* 1299 */       openingPoint.setText("");
/*      */     }
/*      */ 
/* 1302 */     openingPoint.setTime(order.getFillTime());
/*      */ 
/* 1304 */     openingPoint.setArrowColor(this.chartState.getTheme().getColor(order.getSide().isLong() ? ITheme.ChartElement.ORDER_OPEN_BUY : ITheme.ChartElement.ORDER_OPEN_SELL));
/*      */ 
/* 1310 */     openingPoint.setTriangleColor(this.chartState.getTheme().getColor(order.getSide().isLong() ? ITheme.ChartElement.ORDER_OPEN_BUY : ITheme.ChartElement.ORDER_OPEN_SELL));
/*      */ 
/* 1316 */     openingPoint.setPointColor(this.chartState.getTheme().getColor(order.getSide().isLong() ? ITheme.ChartElement.ORDER_OPEN_BUY : ITheme.ChartElement.ORDER_OPEN_SELL));
/*      */ 
/* 1321 */     openingPoint.setBuy(order.getSide().isLong());
/*      */   }
/*      */ 
/*      */   protected void prepareMergingLineFrom(MergingLine mergingLine, BigDecimal startPrice, long startTime, IEngine.OrderCommand side) {
/* 1325 */     mergingLine.setColor(side.isLong() ? Color.RED : Color.BLUE);
/* 1326 */     mergingLine.setMergedFromPoint(this.mapper.xt(startTime), this.mapper.yv(startPrice.floatValue()));
/*      */   }
/*      */ 
/*      */   protected void prepareMergingLineTo(MergingLine mergingLine, BigDecimal endPrice, long endTime) {
/* 1330 */     mergingLine.setMergedToPoint(this.mapper.xt(endTime), this.mapper.yv(endPrice.floatValue()));
/*      */   }
/*      */ 
/*      */   protected void prepareClosingLine(ClosingLine closingLine, BigDecimal startPrice, long startTime, BigDecimal endPrice, long endTime, IEngine.OrderCommand side) {
/* 1334 */     Color lineColor = this.chartState.getTheme().getColor(side.isLong() ? ITheme.ChartElement.ORDER_LONG_POSITION_TRACKING_LINE : ITheme.ChartElement.ORDER_SHORT_POSITION_TRACKING_LINE);
/* 1335 */     closingLine.setColor(lineColor);
/* 1336 */     closingLine.setCoordinates(this.mapper.xt(startTime), this.mapper.yv(startPrice.floatValue()), this.mapper.xt(endTime), this.mapper.yv(endPrice.floatValue()));
/*      */   }
/*      */ 
/*      */   protected void prepareClosingPoint(ClosingPoint closingPoint, BigDecimal closePrice, long closeTime, IEngine.OrderCommand side, Instrument instrument, BigDecimal openPrice) {
/* 1340 */     BigDecimal pipsDiff = openPrice.subtract(closePrice).divide(BigDecimal.valueOf(instrument.getPipValue())).setScale(1, 6).stripTrailingZeros().abs();
/* 1341 */     if ((side != null) && (((side.isLong()) && (openPrice.compareTo(closePrice) > 0)) || ((side.isShort()) && (closePrice.compareTo(openPrice) > 0)))) {
/* 1342 */       pipsDiff = pipsDiff.negate();
/*      */     }
/*      */ 
/* 1345 */     if (isEnabled(ChartSettings.Option.POSITIONS_LABELS))
/* 1346 */       closingPoint.setText(pipsDiff.toPlainString());
/*      */     else {
/* 1348 */       closingPoint.setText("");
/*      */     }
/*      */ 
/* 1351 */     closingPoint.setArrowColor(this.chartState.getTheme().getColor((side == null) || (side.isLong()) ? ITheme.ChartElement.ORDER_CLOSE_BUY : ITheme.ChartElement.ORDER_CLOSE_SELL));
/*      */ 
/* 1357 */     closingPoint.setTriangleColor(this.chartState.getTheme().getColor((side == null) || (side.isLong()) ? ITheme.ChartElement.ORDER_CLOSE_BUY : ITheme.ChartElement.ORDER_CLOSE_SELL));
/*      */ 
/* 1363 */     closingPoint.setPointColor(this.chartState.getTheme().getColor((side == null) || (side.isLong()) ? ITheme.ChartElement.ORDER_CLOSE_BUY : ITheme.ChartElement.ORDER_CLOSE_SELL));
/*      */ 
/* 1368 */     closingPoint.setBuy((side == null) || (side.isLong()));
/*      */   }
/*      */ 
/*      */   private void refreshScreen() {
/* 1372 */     this.guiRefresher.refreshMainContent();
/*      */   }
/*      */ 
/*      */   private boolean isEnabled(ChartSettings.Option option) {
/* 1376 */     return ChartSettings.getBoolean(option);
/*      */   }
/*      */ 
/*      */   public void focusGained(FocusEvent e)
/*      */   {
/*      */   }
/*      */ 
/*      */   public void focusLost(FocusEvent e)
/*      */   {
/*      */   }
/*      */ 
/*      */   public void unselectSeletedOrders()
/*      */   {
/* 1391 */     for (Iterator iterator = this.selectedHorizontalLines.iterator(); iterator.hasNext(); ) {
/* 1392 */       HorizontalLine selectedHorizontalLine = (HorizontalLine)iterator.next();
/*      */ 
/* 1394 */       selectedHorizontalLine.setSelected(false);
/* 1395 */       iterator.remove();
/* 1396 */       updateSelectedGroupIds();
/*      */     }
/*      */ 
/* 1399 */     if (this.selectedHorizontalLines.isEmpty())
/* 1400 */       this.ordersActionListener.ordersEditingEnded();
/*      */   }
/*      */ 
/*      */   private Instrument getInstrument()
/*      */   {
/* 1405 */     return this.chartState.getInstrument();
/*      */   }
/*      */ 
/*      */   public void changeInstrument(Instrument instrument)
/*      */   {
/* 1410 */     this.chartState.setInstrument(instrument);
/* 1411 */     this.ordersDataProviderManager.changeInstrument(instrument);
/*      */   }
/*      */ 
/*      */   public ChartBean getChartBean() {
/* 1415 */     return this.chartBean;
/*      */   }
/*      */ 
/*      */   public void setOrderLineVisible(String orderId, boolean visible)
/*      */   {
/* 1420 */     HorizontalLine horizontalLine = getOrderLine(orderId);
/*      */ 
/* 1422 */     if (horizontalLine == null) {
/* 1423 */       return;
/*      */     }
/*      */ 
/* 1426 */     horizontalLine.setVisible(visible);
/*      */   }
/*      */ 
/*      */   private HorizontalLine getOrderLine(String orderId) {
/* 1430 */     for (HorizontalLine hl : getEntryLines().values()) {
/* 1431 */       if (hl.getOrderId().equals(orderId)) {
/* 1432 */         return hl;
/*      */       }
/*      */     }
/* 1435 */     for (HorizontalLine hl : getStopLossLines().values()) {
/* 1436 */       if (hl.getOrderId().equals(orderId)) {
/* 1437 */         return hl;
/*      */       }
/*      */     }
/* 1440 */     for (HorizontalLine hl : getTakeProfitLines().values()) {
/* 1441 */       if (hl.getOrderId().equals(orderId)) {
/* 1442 */         return hl;
/*      */       }
/*      */     }
/* 1445 */     return null;
/*      */   }
/*      */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Charts-5.48.jar
 * Qualified Name:     com.dukascopy.charts.orders.OrdersManagerImpl
 * JD-Core Version:    0.6.0
 */