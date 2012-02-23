/*     */ package com.dukascopy.dds2.greed.util;
/*     */ 
/*     */ import com.dukascopy.api.ChartObjectAdapter;
/*     */ import com.dukascopy.api.ChartObjectEvent;
/*     */ import com.dukascopy.api.IChart;
/*     */ import com.dukascopy.api.IChart.Type;
/*     */ import com.dukascopy.api.IChartObject;
/*     */ import com.dukascopy.api.IEngine.OrderCommand;
/*     */ import com.dukascopy.api.Instrument;
/*     */ import com.dukascopy.charts.chartbuilder.IChartWrapper;
/*     */ import com.dukascopy.charts.main.interfaces.DDSChartsController;
/*     */ import com.dukascopy.charts.persistence.ChartBean;
/*     */ import com.dukascopy.dds2.greed.GreedContext;
/*     */ import com.dukascopy.dds2.greed.actions.CancelOrderAction;
/*     */ import com.dukascopy.dds2.greed.actions.CancelOrderAction.OrderCancelResultCode;
/*     */ import com.dukascopy.dds2.greed.actions.OrderGroupCloseAction;
/*     */ import com.dukascopy.dds2.greed.agent.DDSAgent;
/*     */ import com.dukascopy.dds2.greed.gui.ClientForm;
/*     */ import com.dukascopy.dds2.greed.gui.DealPanel;
/*     */ import com.dukascopy.dds2.greed.gui.GuiUtilsAndConstants;
/*     */ import com.dukascopy.dds2.greed.gui.component.WorkspacePanel;
/*     */ import com.dukascopy.dds2.greed.gui.component.detached.StopEditDetached;
/*     */ import com.dukascopy.dds2.greed.gui.component.dialog.CustomRequestDialog;
/*     */ import com.dukascopy.dds2.greed.gui.component.dialog.NewOrderEditDialog;
/*     */ import com.dukascopy.dds2.greed.gui.component.orders.OrderCommonTableModel;
/*     */ import com.dukascopy.dds2.greed.gui.component.orders.OrdersPanel;
/*     */ import com.dukascopy.dds2.greed.gui.component.orders.OrdersTable;
/*     */ import com.dukascopy.dds2.greed.gui.component.positions.PositionsPanel;
/*     */ import com.dukascopy.dds2.greed.gui.component.positions.PositionsTable;
/*     */ import com.dukascopy.dds2.greed.gui.component.positions.PositionsTableModel;
/*     */ import com.dukascopy.dds2.greed.gui.component.table.TableSorter;
/*     */ import com.dukascopy.dds2.greed.gui.settings.ClientSettingsStorage;
/*     */ import com.dukascopy.dds2.greed.model.AccountStatement;
/*     */ import com.dukascopy.dds2.greed.model.MarketView;
/*     */ import com.dukascopy.dds2.greed.model.OrderCondition;
/*     */ import com.dukascopy.dds2.greed.model.OrderType;
/*     */ import com.dukascopy.dds2.greed.model.StopOrderType;
/*     */ import com.dukascopy.transport.common.model.type.Money;
/*     */ import com.dukascopy.transport.common.model.type.OfferSide;
/*     */ import com.dukascopy.transport.common.model.type.OrderDirection;
/*     */ import com.dukascopy.transport.common.model.type.OrderSide;
/*     */ import com.dukascopy.transport.common.model.type.Position;
/*     */ import com.dukascopy.transport.common.model.type.PositionSide;
/*     */ import com.dukascopy.transport.common.model.type.StopDirection;
/*     */ import com.dukascopy.transport.common.msg.group.OrderGroupMessage;
/*     */ import com.dukascopy.transport.common.msg.group.OrderMessage;
/*     */ import com.dukascopy.transport.common.msg.request.AccountInfoMessage;
/*     */ import com.dukascopy.transport.common.msg.request.CurrencyOffer;
/*     */ import java.awt.Color;
/*     */ import java.awt.Stroke;
/*     */ import java.awt.Window;
/*     */ import java.awt.event.ActionListener;
/*     */ import java.math.BigDecimal;
/*     */ import java.math.MathContext;
/*     */ import java.math.RoundingMode;
/*     */ import java.text.ParseException;
/*     */ import java.util.Currency;
/*     */ import java.util.HashSet;
/*     */ import java.util.List;
/*     */ import java.util.Set;
/*     */ import javax.swing.JFrame;
/*     */ import org.slf4j.Logger;
/*     */ import org.slf4j.LoggerFactory;
/*     */ 
/*     */ public class OrderUtils
/*     */   implements IOrderUtils
/*     */ {
/*     */   private static OrderUtils orderUtils;
/*  70 */   private static Logger LOGGER = LoggerFactory.getLogger(OrderUtils.class);
/*     */ 
/*  72 */   public static final BigDecimal NEGATIVE_ONE = new BigDecimal("-1");
/*     */ 
/*     */   public static OrderUtils getInstance() {
/*  75 */     if (orderUtils == null) {
/*  76 */       orderUtils = new OrderUtils();
/*     */     }
/*  78 */     return orderUtils;
/*     */   }
/*     */ 
/*     */   public void addStopLoss(String orderGroupId, String orderId) {
/*  82 */     addTakeProfitStopLoss(orderGroupId, orderId, StopOrderType.STOP_LOSS);
/*     */   }
/*     */ 
/*     */   public void addTakeProfit(String orderGroupId, String orderId) {
/*  86 */     addTakeProfitStopLoss(orderGroupId, orderId, StopOrderType.TAKE_PROFIT);
/*     */   }
/*     */ 
/*     */   private void addTakeProfitStopLoss(String orderGroupId, String orderId, StopOrderType stopOrderType) {
/*  90 */     OrderGroupMessage orderGroup = OrderMessageUtils.getOrderGroupById(orderGroupId);
/*  91 */     OrderMessage orderMessage = orderGroup.getOrderById(orderId, null);
/*  92 */     String openingId = null;
/*  93 */     if (OrderDirection.OPEN.equals(orderMessage.getOrderDirection()))
/*  94 */       openingId = orderMessage.getOrderId();
/*  95 */     else if (OrderDirection.CLOSE.equals(orderMessage.getOrderDirection())) {
/*  96 */       openingId = orderMessage.getIfdParentOrderId();
/*     */     }
/*     */ 
/*  99 */     BigDecimal priceStop = null;
/*     */ 
/* 101 */     if (orderMessage.isPlaceOffer())
/* 102 */       priceStop = orderMessage.getPriceClient() != null ? orderMessage.getPriceClient().getValue() : null;
/*     */     else {
/* 104 */       priceStop = orderMessage.getPriceStop() != null ? orderMessage.getPriceStop().getValue() : null;
/*     */     }
/* 106 */     if (StopEditDetached.getOpenedFrame(orderMessage.getOrderId()) == null)
/* 107 */       new StopEditDetached(orderGroup, stopOrderType, null, openingId, priceStop);
/*     */   }
/*     */ 
/*     */   public boolean cancelOrder(String orderId)
/*     */   {
/* 112 */     return CancelOrderAction.cancelOrderById(this, orderId) == CancelOrderAction.OrderCancelResultCode.OK;
/*     */   }
/*     */ 
/*     */   public void closeOrder(String orderGroupId) {
/* 116 */     Position position = OrderMessageUtils.getPositionById(orderGroupId);
/* 117 */     MarketView marketView = (MarketView)GreedContext.get("marketView");
/* 118 */     OfferSide side = position.getPositionSide() == PositionSide.LONG ? OfferSide.BID : OfferSide.ASK;
/*     */ 
/* 120 */     CurrencyOffer offer = marketView.getBestOffer(position.getInstrument(), side);
/* 121 */     Money currentPrice = null != offer ? offer.getPrice() : position.getPriceCurrent();
/* 122 */     ClientForm gui = (ClientForm)GreedContext.get("clientGui");
/* 123 */     OrderGroupMessage orderGroup = gui.getOrdersPanel().getOrderGroup(position.getPositionID());
/* 124 */     if (orderGroup != null) {
/* 125 */       position.setInClosingState(true);
/* 126 */       OrderGroupCloseAction closeAction = new OrderGroupCloseAction(this, orderGroup, currentPrice);
/* 127 */       GreedContext.publishEvent(closeAction);
/*     */ 
/* 129 */       if (GreedContext.isActivityLoggingEnabled())
/*     */       {
/* 131 */         GuiUtilsAndConstants.logClosePosition((JFrame)GreedContext.get("clientGui"), orderGroup, currentPrice, true);
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public void condCloseOrder(String orderGroupId) {
/* 137 */     OrderGroupMessage orderGroup = OrderMessageUtils.getOrderGroupById(orderGroupId);
/* 138 */     OrderMessage orderMessage = orderGroup.getOpeningOrder();
/* 139 */     String openingId = orderMessage.getOrderId();
/* 140 */     if (StopEditDetached.getOpenedFrame(orderMessage.getOrderId()) == null)
/* 141 */       new StopEditDetached(orderGroup, StopOrderType.PART_CLOSE, null, openingId, null);
/*     */   }
/*     */ 
/*     */   public void editOrder(String orderId, ActionListener cancelActionListener, ChartBean chartBean)
/*     */   {
/* 146 */     editOrder(orderId, (0.0D / 0.0D), null, chartBean);
/*     */   }
/*     */ 
/*     */   public void editOrder(String orderId, double price, ActionListener cancelActionListener, ChartBean chartBean) {
/* 150 */     cancelOrderChangePreview(chartBean, orderId);
/*     */ 
/* 152 */     OrderGroupMessage orderGroup = OrderMessageUtils.getOrderGroupByOrderId(orderId);
/* 153 */     if (orderGroup == null)
/* 154 */       return;
/* 155 */     OrderMessage order = orderGroup.getOrderById(orderId, null);
/* 156 */     if (order.isPlaceOffer())
/*     */     {
/*     */       CustomRequestDialog dialog;
/* 158 */       if (Double.isNaN(price)) {
/* 159 */         CustomRequestDialog dialog = new CustomRequestDialog(order, Integer.valueOf(chartBean.getId()));
/* 160 */         dialog.setPriceDisableRefresh(price, cancelActionListener);
/*     */       } else {
/* 162 */         dialog = new CustomRequestDialog(order, BigDecimal.valueOf(price).toPlainString(), Integer.valueOf(chartBean.getId()));
/* 163 */         dialog.setPriceDisableRefresh(price, cancelActionListener);
/*     */       }
/* 165 */       dialog.setLocationRelativeTo((JFrame)GreedContext.get("clientGui"));
/* 166 */       dialog.setVisible(true);
/*     */     } else {
/* 168 */       StopOrderType stopOrderType = StopOrderType.OPEN_IF;
/* 169 */       if (order.isStopLoss()) {
/* 170 */         stopOrderType = StopOrderType.STOP_LOSS;
/*     */       }
/* 172 */       if (order.isTakeProfit()) {
/* 173 */         stopOrderType = StopOrderType.TAKE_PROFIT;
/*     */       }
/* 175 */       if (StopEditDetached.getOpenedFrame(order.getOrderId()) == null)
/* 176 */         if (Double.isNaN(price))
/* 177 */           new StopEditDetached(orderGroup, stopOrderType, order.getOrderId(), cancelActionListener);
/*     */         else
/* 179 */           new StopEditDetached(orderGroup, stopOrderType, order.getOrderId(), price, cancelActionListener);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void createNewOrder(Window window, Instrument instrument, IEngine.OrderCommand command, double price, Integer chartId)
/*     */   {
/* 187 */     if ((command == IEngine.OrderCommand.PLACE_BID) || (command == IEngine.OrderCommand.PLACE_OFFER)) {
/* 188 */       CustomRequestDialog dialog = new CustomRequestDialog(command == IEngine.OrderCommand.PLACE_BID ? OfferSide.BID : OfferSide.ASK, instrument.toString(), chartId);
/* 189 */       dialog.setLocationRelativeTo(window);
/* 190 */       dialog.setPriceDisableRefresh(price, null);
/* 191 */       dialog.setVisible(true);
/*     */     } else {
/* 193 */       NewOrderEditDialog dialog = new NewOrderEditDialog(window, instrument, command, price);
/* 194 */       dialog.setVisible(true);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void selectGroupIds(List<String> selectedGroupIds)
/*     */   {
/* 200 */     ((ClientForm)GreedContext.get("clientGui")).getOrdersPanel().getOrdersTable().setSelectedGroupIds(selectedGroupIds);
/*     */   }
/*     */ 
/*     */   public AccountInfoMessage getAccountInfo()
/*     */   {
/* 205 */     return ((AccountStatement)GreedContext.get("accountStatement")).getLastAccountState();
/*     */   }
/*     */ 
/*     */   public static void clean()
/*     */   {
/* 210 */     orderUtils = null;
/*     */   }
/*     */ 
/*     */   public static void addDefaultStopLossAndTakeProfitToMarketGroup(OrderMessage marketMainOrder, List<OrderMessage> group)
/*     */   {
/* 217 */     String tagForOrder = null;
/* 218 */     if (marketMainOrder.getExternalSysId() == null) {
/* 219 */       tagForOrder = DDSAgent.generateLabel(marketMainOrder);
/* 220 */       marketMainOrder.setExternalSysId(tagForOrder);
/*     */     }
/* 222 */     if (marketMainOrder.getSignalId() == null) {
/* 223 */       tagForOrder = DDSAgent.generateLabel(marketMainOrder);
/* 224 */       marketMainOrder.setSignalId(tagForOrder);
/*     */     }
/*     */ 
/* 227 */     OrderSide side = marketMainOrder.getSide() == OrderSide.BUY ? OrderSide.SELL : OrderSide.BUY;
/* 228 */     ClientSettingsStorage storage = (ClientSettingsStorage)GreedContext.get("settingsStorage");
/*     */ 
/* 230 */     Instrument instrument = Instrument.fromString(marketMainOrder.getInstrument());
/* 231 */     Currency secondaryCurrency = instrument.getSecondaryCurrency();
/*     */ 
/* 233 */     StopDirection stopLossStopDirection = marketMainOrder.getSide() == OrderSide.BUY ? StopDirection.BID_LESS : StopDirection.ASK_GREATER;
/* 234 */     StopDirection takeProfitStopDirection = marketMainOrder.getSide() == OrderSide.BUY ? StopDirection.BID_GREATER : StopDirection.ASK_LESS;
/*     */ 
/* 236 */     BigDecimal stopLossPrice = calculateStopLossValue(instrument, side);
/* 237 */     BigDecimal takeProfitPrice = calculateTakeProfitValue(instrument, side);
/*     */ 
/* 239 */     if (storage.restoreApplyStopLossToAllMarketOrders()) {
/* 240 */       OrderMessage stopLossClosingOrder = new OrderMessage();
/* 241 */       stopLossClosingOrder.setInstrument(marketMainOrder.getInstrument());
/* 242 */       stopLossClosingOrder.setAmount(marketMainOrder.getAmount());
/* 243 */       stopLossClosingOrder.setOrderDirection(OrderDirection.CLOSE);
/* 244 */       stopLossClosingOrder.setSide(side);
/* 245 */       stopLossClosingOrder.setOrderGroupId(marketMainOrder.getOrderGroupId());
/* 246 */       stopLossClosingOrder.setPriceStop(new Money(stopLossPrice, secondaryCurrency));
/*     */ 
/* 248 */       stopLossClosingOrder.setStopDirection(stopLossStopDirection);
/*     */ 
/* 250 */       stopLossClosingOrder.setExternalSysId(tagForOrder);
/* 251 */       stopLossClosingOrder.setSignalId(tagForOrder);
/*     */ 
/* 253 */       group.add(stopLossClosingOrder);
/*     */     }
/*     */ 
/* 256 */     if (storage.restoreApplyTakeProfitToAllMarketOrders()) {
/* 257 */       OrderMessage takeProfitClosingOrder = new OrderMessage();
/* 258 */       takeProfitClosingOrder.setInstrument(marketMainOrder.getInstrument());
/* 259 */       takeProfitClosingOrder.setAmount(marketMainOrder.getAmount());
/* 260 */       takeProfitClosingOrder.setOrderDirection(OrderDirection.CLOSE);
/* 261 */       takeProfitClosingOrder.setSide(side);
/* 262 */       takeProfitClosingOrder.setOrderGroupId(marketMainOrder.getOrderGroupId());
/* 263 */       takeProfitClosingOrder.setPriceStop(new Money(takeProfitPrice, secondaryCurrency));
/*     */ 
/* 265 */       takeProfitClosingOrder.setStopDirection(takeProfitStopDirection);
/*     */ 
/* 267 */       takeProfitClosingOrder.setPriceTrailingLimit(new Money(BigDecimal.ZERO, secondaryCurrency));
/*     */ 
/* 269 */       takeProfitClosingOrder.setExternalSysId(tagForOrder);
/* 270 */       takeProfitClosingOrder.setSignalId(tagForOrder);
/* 271 */       group.add(takeProfitClosingOrder);
/*     */     }
/*     */   }
/*     */ 
/*     */   private static BigDecimal calculateTakeProfitValue(Instrument instrument, OrderSide orderSide)
/*     */   {
/* 279 */     BigDecimal price = new BigDecimal(0);
/* 280 */     double pipDiff = 0.0D;
/* 281 */     ClientSettingsStorage storage = (ClientSettingsStorage)GreedContext.get("settingsStorage");
/*     */ 
/* 283 */     if (orderSide == OrderSide.BUY) {
/* 284 */       price = getBidPrice(instrument);
/* 285 */       pipDiff = NEGATIVE_ONE.multiply(storage.restoreDefaultTakeProfitOffset()).doubleValue();
/* 286 */     } else if (orderSide == OrderSide.SELL) {
/* 287 */       price = getAskPrice(instrument);
/* 288 */       pipDiff = storage.restoreDefaultTakeProfitOffset().doubleValue();
/*     */     }
/*     */ 
/* 291 */     if ((price != null) && (pipDiff != 0.0D)) {
/* 292 */       price = price.add(BigDecimal.valueOf(instrument.getPipValue()).multiply(new BigDecimal(pipDiff)));
/*     */     }
/*     */ 
/* 295 */     price = price.round(new MathContext(6, RoundingMode.HALF_EVEN));
/* 296 */     return price;
/*     */   }
/*     */ 
/*     */   private static BigDecimal calculateStopLossValue(Instrument instrument, OrderSide orderSide)
/*     */   {
/* 301 */     BigDecimal price = new BigDecimal(0);
/* 302 */     double pipDiff = 0.0D;
/* 303 */     ClientSettingsStorage storage = (ClientSettingsStorage)GreedContext.get("settingsStorage");
/*     */ 
/* 305 */     if (orderSide == OrderSide.BUY) {
/* 306 */       pipDiff = storage.restoreDefaultStopLossOffset().doubleValue();
/* 307 */       price = getBidPrice(instrument);
/* 308 */     } else if (orderSide == OrderSide.SELL) {
/* 309 */       pipDiff = NEGATIVE_ONE.multiply(storage.restoreDefaultStopLossOffset()).doubleValue();
/* 310 */       price = getAskPrice(instrument);
/*     */     }
/*     */ 
/* 313 */     if ((price != null) && (pipDiff != 0.0D)) {
/* 314 */       price = price.add(BigDecimal.valueOf(instrument.getPipValue()).multiply(new BigDecimal(pipDiff)));
/*     */     }
/* 316 */     price = price.round(new MathContext(6, RoundingMode.HALF_EVEN));
/* 317 */     return price;
/*     */   }
/*     */ 
/*     */   private static BigDecimal getAskPrice(Instrument instrument)
/*     */   {
/* 322 */     return new BigDecimal(getPrice(OrderSide.BUY, instrument));
/*     */   }
/*     */ 
/*     */   private static BigDecimal getBidPrice(Instrument instrument) {
/* 326 */     return new BigDecimal(getPrice(OrderSide.SELL, instrument));
/*     */   }
/*     */ 
/*     */   private static double getPrice(OrderSide side, Instrument instrument) {
/* 330 */     MarketView marketView = (MarketView)GreedContext.get("marketView");
/* 331 */     OfferSide offerSide = side == OrderSide.BUY ? OfferSide.ASK : OfferSide.BID;
/* 332 */     CurrencyOffer bestOffer = marketView.getBestOffer(instrument.toString(), offerSide);
/* 333 */     return bestOffer.getPrice().getValue().doubleValue();
/*     */   }
/*     */ 
/*     */   public void cancelOrderChangePreview(ChartBean chartBean, String orderId)
/*     */   {
/* 341 */     DDSChartsController ddsChartsController = (DDSChartsController)GreedContext.get("chartsController");
/*     */     IChart currentChart;
/* 342 */     if (ddsChartsController != null) {
/* 343 */       Set charts = ddsChartsController.getICharts(chartBean.getInstrument());
/* 344 */       currentChart = ddsChartsController.getIChartBy(Integer.valueOf(chartBean.getId()));
/* 345 */       if ((charts != null) && (currentChart != null))
/* 346 */         for (IChart chart : charts) {
/* 347 */           if (chart == currentChart) {
/*     */             continue;
/*     */           }
/* 350 */           chart.remove(orderId);
/*     */         }
/*     */     }
/*     */   }
/*     */ 
/*     */   public void setOrderLinesVisible(ChartBean chartBean, String orderId, boolean visible)
/*     */   {
/* 362 */     DDSChartsController ddsChartsController = (DDSChartsController)GreedContext.get("chartsController");
/* 363 */     if (ddsChartsController != null) {
/* 364 */       Set charts = ddsChartsController.getICharts(chartBean.getInstrument());
/* 365 */       if (charts != null)
/* 366 */         for (IChart chart : charts)
/* 367 */           ((IChartWrapper)chart).setOrderLineVisible(orderId, visible);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void orderChangePreview(ChartBean chartBean, String orderId, BigDecimal newOrderPrice, String text, Color color, Stroke stroke)
/*     */   {
/* 382 */     DDSChartsController ddsChartsController = (DDSChartsController)GreedContext.get("chartsController");
/*     */     IChart currentChart;
/* 383 */     if (ddsChartsController != null) {
/* 384 */       Set charts = ddsChartsController.getICharts(chartBean.getInstrument());
/* 385 */       currentChart = ddsChartsController.getIChartBy(Integer.valueOf(chartBean.getId()));
/* 386 */       if ((charts != null) && (currentChart != null))
/* 387 */         for (IChart chart : charts) {
/* 388 */           if (chart == currentChart)
/*     */           {
/*     */             continue;
/*     */           }
/* 392 */           IChartObject hLine = chart.get(orderId);
/* 393 */           if (hLine == null) {
/* 394 */             hLine = chart.drawUnlocked(orderId, IChart.Type.HLINE, 0L, newOrderPrice.doubleValue());
/* 395 */             if (hLine != null) {
/* 396 */               hLine.setMenuEnabled(false);
/* 397 */               hLine.setColor(color);
/* 398 */               hLine.setText(text);
/* 399 */               hLine.setSticky(false);
/* 400 */               hLine.setStroke(stroke);
/* 401 */               hLine.setChartObjectListener(new ChartObjectAdapter()
/*     */               {
/*     */                 public void deleted(ChartObjectEvent e) {
/* 404 */                   e.cancel();
/*     */                 } } );
/*     */             }
/*     */           } else {
/* 409 */             hLine.setText(text);
/* 410 */             hLine.move(0L, newOrderPrice.doubleValue());
/*     */           }
/*     */         }
/*     */     }
/*     */   }
/*     */ 
/*     */   public static boolean isInstrumentsUsedByThePlatform(Instrument instrument)
/*     */   {
/* 419 */     if (isOpenedPosOrOrdersForInstrument(instrument)) {
/* 420 */       return true;
/*     */     }
/*     */ 
/* 423 */     Set openOrdersAndPositions = new HashSet();
/* 424 */     openOrdersAndPositions.addAll(getOpenOrderInstruments());
/* 425 */     openOrdersAndPositions.addAll(getOpenPositionInstruments());
/*     */ 
/* 427 */     for (String openOrderOrPositionPair : openOrdersAndPositions) {
/* 428 */       Set dependantInstruments = fetchCurrenciesNeededForProfitlossCalculation(openOrderOrPositionPair);
/* 429 */       if (dependantInstruments.contains(instrument.toString())) {
/* 430 */         return true;
/*     */       }
/*     */     }
/*     */ 
/* 434 */     WorkspacePanel workspacePanel = ((ClientForm)GreedContext.get("clientGui")).getDealPanel().getWorkspacePanel();
/*     */ 
/* 436 */     for (String instr : workspacePanel.getInstruments()) {
/* 437 */       if (!Instrument.fromString(instr).equals(instrument)) {
/* 438 */         Set curInstrumentDependants = fetchCurrenciesNeededForProfitlossCalculation(instr);
/* 439 */         if (curInstrumentDependants.contains(instrument.toString())) {
/* 440 */           return true;
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/* 445 */     return false;
/*     */   }
/*     */ 
/*     */   public static boolean isOpenedPosOrOrdersForInstrument(Instrument instrument)
/*     */   {
/* 450 */     Set openOrdersInstruments = getOpenOrderInstruments();
/* 451 */     if (openOrdersInstruments.contains(instrument.toString())) {
/* 452 */       return true;
/*     */     }
/*     */ 
/* 455 */     Set openPositionsInstruments = getOpenPositionInstruments();
/*     */ 
/* 457 */     return openPositionsInstruments.contains(instrument.toString());
/*     */   }
/*     */ 
/*     */   static Set<String> getOpenOrderInstruments()
/*     */   {
/* 464 */     Set result = new HashSet();
/* 465 */     OrdersPanel ordersPanel = ((ClientForm)GreedContext.get("clientGui")).getOrdersPanel();
/*     */ 
/* 467 */     if (ordersPanel == null) {
/* 468 */       return result;
/*     */     }
/* 470 */     OrdersTable table = ordersPanel.getOrdersTable();
/* 471 */     if (table.getModel() == null) {
/* 472 */       return result;
/*     */     }
/* 474 */     TableSorter tableSorter = (TableSorter)table.getModel();
/* 475 */     OrderCommonTableModel orderModel = (OrderCommonTableModel)tableSorter.getTableModel();
/* 476 */     int i = 0; for (int n = orderModel.getRowCount(); i < n; i++) {
/* 477 */       OrderMessage orderMessage = orderModel.getOrder(i);
/* 478 */       String instrument = orderMessage.getInstrument();
/* 479 */       result.add(instrument);
/*     */     }
/*     */ 
/* 482 */     return result;
/*     */   }
/*     */ 
/*     */   static Set<String> getOpenPositionInstruments() {
/* 486 */     Set result = new HashSet();
/* 487 */     PositionsPanel positionsPanel = ((ClientForm)GreedContext.get("clientGui")).getPositionsPanel();
/* 488 */     if (positionsPanel == null) {
/* 489 */       LOGGER.debug("Positions panel not exists");
/* 490 */       return result;
/*     */     }
/*     */ 
/* 493 */     PositionsTableModel tableModel = (PositionsTableModel)positionsPanel.getTable().getModel();
/* 494 */     if (tableModel == null) {
/* 495 */       LOGGER.debug("Position table model not exists");
/* 496 */       return result;
/*     */     }
/*     */ 
/* 499 */     List positions = tableModel.getPositions();
/* 500 */     for (Position position : positions) {
/* 501 */       result.add(position.getInstrument());
/*     */     }
/*     */ 
/* 504 */     return result;
/*     */   }
/*     */ 
/*     */   public static Set<String> fetchCurrenciesNeededForProfitlossCalculation(String selectedInstrument) {
/* 508 */     Set missingPairs = ((MarketView)GreedContext.get("marketView")).fetchCurrenciesNeededForProfitlossCalculation(selectedInstrument);
/* 509 */     return missingPairs;
/*     */   }
/*     */ 
/*     */   public static Position calculatePositionModified(OrderGroupMessage orderGroup) {
/* 513 */     Position position = null;
/* 514 */     if ((GreedContext.isGlobalExtended()) || (GreedContext.isGlobal())) {
/* 515 */       if ((orderGroup.getAmount() != null) && (orderGroup.getAmount().getValue().compareTo(BigDecimal.ZERO) > 0))
/*     */         try
/*     */         {
/* 518 */           position = new Position(orderGroup);
/* 519 */           position.setAmount(orderGroup.getAmount());
/* 520 */           PositionSide side = orderGroup.getSide();
/* 521 */           position.setPositionID(orderGroup.getOrderGroupId());
/* 522 */           position.setInstrument(orderGroup.getInstrument());
/* 523 */           position.setPositionSide(side);
/* 524 */           position.setPriceOpen(orderGroup.getPricePosOpen());
/* 525 */           position.setTag(orderGroup.getTag());
/* 526 */           position.setOrderGroup(orderGroup);
/*     */         } catch (ParseException e1) {
/* 528 */           LOGGER.error(e1.getMessage(), e1);
/*     */         }
/*     */     }
/*     */     else {
/* 532 */       position = orderGroup.calculatePositionModified();
/*     */     }
/*     */ 
/* 535 */     if (position == null) {
/* 536 */       LOGGER.info(orderGroup.getOrderGroupId() + ": calculated position is null");
/*     */     }
/*     */ 
/* 539 */     return position;
/*     */   }
/*     */ 
/*     */   public static BigDecimal calculateConditionalPrice(String instrument, OrderType orderType, OrderSide orderSide, OrderCondition stopDirection, BigDecimal entryPrice)
/*     */   {
/* 548 */     BigDecimal price = null;
/* 549 */     int pipDiff = 0;
/*     */ 
/* 551 */     MarketView marketView = (MarketView)GreedContext.get("marketView");
/* 552 */     ClientSettingsStorage settingsSaver = (ClientSettingsStorage)GreedContext.get("settingsStorage");
/*     */ 
/* 554 */     CurrencyOffer askOffer = marketView.getBestOffer(instrument, OfferSide.ASK);
/* 555 */     CurrencyOffer bidOffer = marketView.getBestOffer(instrument, OfferSide.BID);
/*     */ 
/* 557 */     BigDecimal askPrice = askOffer.getPrice().getValue();
/* 558 */     BigDecimal bidPrice = bidOffer.getPrice().getValue();
/*     */ 
/* 560 */     int entryOffset = settingsSaver.restoreDefaultOpenIfOffset().intValue();
/* 561 */     int stopLossoffset = settingsSaver.restoreDefaultStopLossOffset().intValue();
/* 562 */     int takeProfitOffest = settingsSaver.restoreDefaultTakeProfitOffset().intValue();
/*     */ 
/* 564 */     if ((orderType == OrderType.ENTRY) && (orderSide == OrderSide.BUY)) {
/* 565 */       if ((stopDirection == OrderCondition.LIMIT_ASK) || (stopDirection == OrderCondition.MIT_ASK)) {
/* 566 */         pipDiff = -1 * entryOffset;
/* 567 */         price = askPrice;
/* 568 */       } else if (stopDirection == OrderCondition.GREATER_ASK) {
/* 569 */         pipDiff = entryOffset;
/* 570 */         price = askPrice;
/* 571 */       } else if (stopDirection == OrderCondition.GREATER_BID) {
/* 572 */         pipDiff = entryOffset;
/* 573 */         price = bidPrice;
/*     */       }
/* 575 */     } else if ((orderType == OrderType.ENTRY) && (orderSide == OrderSide.SELL))
/*     */     {
/* 577 */       if (stopDirection == OrderCondition.LESS_BID) {
/* 578 */         pipDiff = -1 * entryOffset;
/* 579 */         price = bidPrice;
/* 580 */       } else if (stopDirection == OrderCondition.LESS_ASK) {
/* 581 */         pipDiff = -1 * entryOffset;
/* 582 */         price = askPrice;
/* 583 */       } else if ((stopDirection == OrderCondition.LIMIT_BID) || (stopDirection == OrderCondition.MIT_BID)) {
/* 584 */         pipDiff = entryOffset;
/* 585 */         price = bidPrice;
/*     */       }
/*     */     }
/*     */ 
/* 589 */     if ((orderType == OrderType.STOP_LOSS) && (orderSide == OrderSide.BUY)) {
/* 590 */       if (stopDirection == OrderCondition.LESS_BID) {
/* 591 */         pipDiff = -1 * stopLossoffset;
/* 592 */         price = bidPrice;
/* 593 */       } else if (stopDirection == OrderCondition.LESS_ASK) {
/* 594 */         pipDiff = -1 * stopLossoffset;
/* 595 */         price = bidPrice;
/*     */       }
/* 597 */     } else if ((orderType == OrderType.STOP_LOSS) && (orderSide == OrderSide.SELL)) {
/* 598 */       if (stopDirection == OrderCondition.GREATER_BID) {
/* 599 */         pipDiff = stopLossoffset;
/* 600 */         price = bidPrice;
/* 601 */       } else if (stopDirection == OrderCondition.GREATER_ASK) {
/* 602 */         pipDiff = stopLossoffset;
/* 603 */         price = askPrice;
/*     */       }
/*     */     }
/*     */ 
/* 607 */     if ((orderType == OrderType.TAKE_PROFIT) && (orderSide == OrderSide.BUY)) {
/* 608 */       price = bidPrice;
/* 609 */       pipDiff = takeProfitOffest;
/* 610 */     } else if ((orderType == OrderType.TAKE_PROFIT) && (orderSide == OrderSide.SELL)) {
/* 611 */       price = askPrice;
/* 612 */       pipDiff = -1 * takeProfitOffest;
/*     */     }
/*     */ 
/* 615 */     if (entryPrice != null) {
/* 616 */       price = entryPrice;
/*     */     }
/* 618 */     if ((price != null) && (pipDiff != 0)) {
/* 619 */       price = price.add(BigDecimal.valueOf(Instrument.fromString(instrument).getPipValue()).multiply(new BigDecimal(pipDiff))).stripTrailingZeros();
/*     */     }
/*     */ 
/* 622 */     return price;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.util.OrderUtils
 * JD-Core Version:    0.6.0
 */