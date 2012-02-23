/*     */ package com.dukascopy.dds2.greed.util;
/*     */ 
/*     */ import com.dukascopy.dds2.greed.GreedContext;
/*     */ import com.dukascopy.dds2.greed.actions.PostMessageAction;
/*     */ import com.dukascopy.dds2.greed.gui.ClientForm;
/*     */ import com.dukascopy.dds2.greed.gui.component.ApplicationClock;
/*     */ import com.dukascopy.dds2.greed.gui.component.orders.OrderCommonTableModel;
/*     */ import com.dukascopy.dds2.greed.gui.component.orders.OrdersPanel;
/*     */ import com.dukascopy.dds2.greed.gui.component.orders.OrdersTable;
/*     */ import com.dukascopy.dds2.greed.gui.component.positions.PositionsPanel;
/*     */ import com.dukascopy.dds2.greed.gui.component.table.TableSorter;
/*     */ import com.dukascopy.dds2.greed.model.MarketView;
/*     */ import com.dukascopy.dds2.greed.model.Notification;
/*     */ import com.dukascopy.dds2.greed.model.OrderGoodTillType;
/*     */ import com.dukascopy.transport.common.model.type.Money;
/*     */ import com.dukascopy.transport.common.model.type.OfferSide;
/*     */ import com.dukascopy.transport.common.model.type.OrderDirection;
/*     */ import com.dukascopy.transport.common.model.type.OrderSide;
/*     */ import com.dukascopy.transport.common.model.type.OrderState;
/*     */ import com.dukascopy.transport.common.model.type.Position;
/*     */ import com.dukascopy.transport.common.model.type.PositionSide;
/*     */ import com.dukascopy.transport.common.model.type.StopDirection;
/*     */ import com.dukascopy.transport.common.msg.ProtocolMessage;
/*     */ import com.dukascopy.transport.common.msg.group.OrderGroupMessage;
/*     */ import com.dukascopy.transport.common.msg.group.OrderMessage;
/*     */ import com.dukascopy.transport.common.msg.request.CurrencyOffer;
/*     */ import java.lang.reflect.InvocationTargetException;
/*     */ import java.math.BigDecimal;
/*     */ import java.text.ParseException;
/*     */ import java.text.SimpleDateFormat;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Calendar;
/*     */ import java.util.Date;
/*     */ import java.util.HashSet;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Set;
/*     */ import java.util.TimeZone;
/*     */ import javax.swing.SwingUtilities;
/*     */ import org.slf4j.Logger;
/*     */ import org.slf4j.LoggerFactory;
/*     */ 
/*     */ public class OrderMessageUtils
/*     */ {
/*     */   private static Logger LOGGER;
/*     */   public static final long GTC_THRESHOLD = 1576800000000L;
/*     */   private static Set<Integer> printMessages;
/*     */ 
/*     */   public static Long convertGoodTill2ExecTimeoutMIllis(OrderGoodTillType goodTill, Long goodTillAmount)
/*     */   {
/*  57 */     Long result = null;
/*  58 */     long currentPlatformTime = ((ApplicationClock)GreedContext.get("applicationClock")).getTime();
/*     */ 
/*  60 */     switch (4.$SwitchMap$com$dukascopy$dds2$greed$model$OrderGoodTillType[goodTill.ordinal()])
/*     */     {
/*     */     case 1:
/*  64 */       result = Long.valueOf(goodTillAmount.longValue() * 3600000L + currentPlatformTime);
/*  65 */       break;
/*     */     case 2:
/*  68 */       result = Long.valueOf(goodTillAmount.longValue() * 60000L + currentPlatformTime);
/*  69 */       break;
/*     */     case 3:
/*  72 */       result = goodTillAmount;
/*  73 */       break;
/*     */     case 4:
/*  75 */       result = Long.valueOf(currentPlatformTime + 3153600000000L);
/*  76 */       break;
/*     */     default:
/*  79 */       throw new IllegalArgumentException("Unrecognized value of OrderGooTillType");
/*     */     }
/*  81 */     return result;
/*     */   }
/*     */ 
/*     */   public static String getTTLAsString(OrderMessage order)
/*     */   {
/*  91 */     if (order.getString("execTimeoutMillis") == null) {
/*  92 */       return "";
/*     */     }
/*  94 */     long currentPlatformTime = ((ApplicationClock)GreedContext.get("applicationClock")).getTime();
/*     */ 
/*  98 */     if (order.getExecTimeoutMillis().longValue() > currentPlatformTime) {
/*  99 */       long deltaMillis = order.getExecTimeoutMillis().longValue() - currentPlatformTime;
/* 100 */       if (deltaMillis > 1576800000000L)
/*     */       {
/* 102 */         return "GTC";
/*     */       }
/* 104 */       Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
/* 105 */       cal.setTimeInMillis(order.getExecTimeoutMillis().longValue());
/*     */ 
/* 107 */       SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
/* 108 */       simpleDateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
/*     */ 
/* 110 */       return simpleDateFormat.format(cal.getTime());
/*     */     }
/*     */ 
/* 115 */     Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
/* 116 */     cal.setTimeInMillis(order.getExecTimeoutMillis().longValue() + order.getCreatedDate().getTime());
/*     */ 
/* 118 */     SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
/* 119 */     simpleDateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
/*     */ 
/* 121 */     return simpleDateFormat.format(cal.getTime());
/*     */   }
/*     */ 
/*     */   public static PositionSide getOgmSide(OrderGroupMessage ogm)
/*     */   {
/* 127 */     if (ogm == null) {
/* 128 */       return null;
/*     */     }
/*     */ 
/* 131 */     PositionSide side = null;
/* 132 */     Position pos = ogm.calculatePositionModified();
/* 133 */     if (pos != null)
/* 134 */       side = pos.getPositionSide();
/*     */     else {
/* 136 */       for (OrderMessage order : ogm.getOrders()) {
/* 137 */         if (order.getOrderDirection().equals(OrderDirection.OPEN)) {
/* 138 */           if (order.getSide() == OrderSide.BUY) {
/* 139 */             side = PositionSide.LONG; break;
/*     */           }
/* 141 */           side = PositionSide.SHORT;
/*     */ 
/* 143 */           break;
/* 144 */         }if (order.getOrderDirection().equals(OrderDirection.CLOSE)) {
/* 145 */           if (order.getSide() == OrderSide.BUY) {
/* 146 */             side = PositionSide.SHORT; break;
/*     */           }
/* 148 */           side = PositionSide.LONG;
/*     */ 
/* 150 */           break;
/*     */         }
/*     */       }
/*     */     }
/* 154 */     return side;
/*     */   }
/*     */ 
/*     */   public static List<OrderMessage> getOpenIfOrderList(OrderGroupMessage ogm) {
/* 158 */     List result = new ArrayList();
/* 159 */     for (OrderMessage order : ogm.getOrders()) {
/* 160 */       if ((order.getOrderDirection() == OrderDirection.OPEN) && (order.getPriceStop() != null)) {
/* 161 */         result.add(order);
/*     */       }
/*     */     }
/* 164 */     return result;
/*     */   }
/*     */ 
/*     */   public static OrderMessage getSlOrder(OrderGroupMessage ogm, OrderMessage om) {
/* 168 */     OrderMessage slOrder = null;
/* 169 */     for (Iterator it = ogm.getOrders().iterator(); it.hasNext(); ) {
/* 170 */       OrderMessage currentOrder = (OrderMessage)it.next();
/* 171 */       if ((currentOrder.isStopLoss()) && (currentOrder.getIfdParentOrderId() != null) && (currentOrder.getIfdParentOrderId().equals(om.getOrderId())))
/*     */       {
/* 173 */         slOrder = currentOrder;
/*     */       }
/*     */     }
/* 176 */     return slOrder;
/*     */   }
/*     */ 
/*     */   public static OrderMessage getTpOrder(OrderGroupMessage ogm, OrderMessage om) {
/* 180 */     OrderMessage tpOrder = null;
/* 181 */     for (Iterator it = ogm.getOrders().iterator(); it.hasNext(); ) {
/* 182 */       OrderMessage currentOrder = (OrderMessage)it.next();
/* 183 */       if ((currentOrder.isTakeProfit()) && (currentOrder.getIfdParentOrderId() != null) && (currentOrder.getIfdParentOrderId().equals(om.getOrderId())))
/*     */       {
/* 186 */         tpOrder = currentOrder;
/*     */       }
/*     */     }
/* 189 */     return tpOrder;
/*     */   }
/*     */ 
/*     */   public static OrderMessage getCsOrder(OrderMessage om) {
/* 193 */     OrderMessage csOrder = null;
/* 194 */     for (Iterator it = getIfDoneRelatedOrdersByOrderId(om.getOrderId()).iterator(); it.hasNext(); ) {
/* 195 */       OrderMessage currentOrder = (OrderMessage)it.next();
/* 196 */       if ((currentOrder.isIfdStop()) && (currentOrder.getIfdParentOrderId() != null) && (currentOrder.getIfdParentOrderId().equals(om.getOrderId())))
/*     */       {
/* 199 */         csOrder = currentOrder;
/*     */       }
/*     */     }
/* 202 */     return csOrder;
/*     */   }
/*     */ 
/*     */   public static OrderMessage getClOrder(OrderMessage om) {
/* 206 */     OrderMessage clOrder = null;
/* 207 */     for (Iterator it = getIfDoneRelatedOrdersByOrderId(om.getOrderId()).iterator(); it.hasNext(); ) {
/* 208 */       OrderMessage currentOrder = (OrderMessage)it.next();
/* 209 */       if ((currentOrder.isIfdLimit()) && (currentOrder.getIfdParentOrderId() != null) && (currentOrder.getIfdParentOrderId().equals(om.getOrderId())))
/*     */       {
/* 212 */         clOrder = currentOrder;
/*     */       }
/*     */     }
/* 215 */     return clOrder;
/*     */   }
/*     */ 
/*     */   public static OrderMessage getOpeningOrder(OrderGroupMessage ogm, OrderMessage slTp) {
/* 219 */     if (slTp.getIfdParentOrderId() != null) {
/* 220 */       OrderMessage openingOrder = ogm.getOrderById(slTp.getIfdParentOrderId(), null);
/* 221 */       if ((openingOrder != null) && (openingOrder.isOpening())) {
/* 222 */         return openingOrder;
/*     */       }
/*     */     }
/* 225 */     return null;
/*     */   }
/*     */ 
/*     */   public static boolean validateSlTp(OrderGroupMessage ogm) {
/* 229 */     boolean result = true;
/*     */ 
/* 231 */     OrderMessage opening = ogm.getOpeningOrder();
/* 232 */     OrderSide side = opening.getSide();
/* 233 */     String instrument = opening.getInstrument();
/*     */ 
/* 235 */     OrderMessage stop = ogm.getStopLossOrder();
/* 236 */     OrderMessage tp = ogm.getTakeProfitOrder();
/*     */ 
/* 238 */     if ((stop == null) && (tp == null)) {
/* 239 */       return true;
/*     */     }
/*     */ 
/* 242 */     MarketView marketView = (MarketView)GreedContext.get("marketView");
/*     */ 
/* 244 */     Money marketPrice = null;
/* 245 */     if (opening.getPriceStop() != null) {
/* 246 */       marketPrice = opening.getPriceStop();
/*     */     }
/*     */ 
/* 249 */     if (OrderSide.BUY.equals(side)) {
/* 250 */       if (marketPrice == null) {
/* 251 */         marketPrice = marketView.getBestOffer(instrument, OfferSide.ASK).getAmount();
/*     */       }
/* 253 */       if ((stop != null) && (marketPrice != null) && (stop.getPriceStop().getValue().compareTo(marketPrice.getValue()) >= 0))
/*     */       {
/* 256 */         return false;
/*     */       }
/* 258 */       if ((tp != null) && (marketPrice != null) && (tp.getPriceStop().getValue().compareTo(marketPrice.getValue()) <= 0))
/*     */       {
/* 261 */         return false;
/*     */       }
/* 263 */     } else if (OrderSide.SELL.equals(side)) {
/* 264 */       if (marketPrice == null) {
/* 265 */         marketPrice = marketView.getBestOffer(instrument, OfferSide.BID).getAmount();
/*     */       }
/* 267 */       if ((stop != null) && (marketPrice != null) && (stop.getPriceStop().getValue().compareTo(marketPrice.getValue()) <= 0))
/*     */       {
/* 270 */         return false;
/*     */       }
/* 272 */       if ((tp != null) && (marketPrice != null) && (tp.getPriceStop().getValue().compareTo(marketPrice.getValue()) >= 0))
/*     */       {
/* 275 */         return false;
/*     */       }
/*     */     }
/* 278 */     return result;
/*     */   }
/*     */ 
/*     */   public static OrderGroupMessage getOrderGroupById(String orderGroupId)
/*     */   {
/* 283 */     OrderGroupMessage ogm = null;
/*     */ 
/* 285 */     List ogmEdtResult = new ArrayList(1);
/*     */ 
/* 287 */     if (SwingUtilities.isEventDispatchThread())
/* 288 */       getOrderGroupById(orderGroupId, ogmEdtResult);
/*     */     else {
/*     */       try {
/* 291 */         SwingUtilities.invokeAndWait(new Runnable(orderGroupId, ogmEdtResult) {
/*     */           public void run() {
/* 293 */             OrderMessageUtils.access$000(this.val$orderGroupId, this.val$ogmEdtResult);
/*     */           } } );
/*     */       } catch (InterruptedException e) {
/* 297 */         LOGGER.error(e.getMessage(), e);
/*     */       } catch (InvocationTargetException e) {
/* 299 */         LOGGER.error(e.getMessage(), e);
/*     */       }
/*     */     }
/*     */ 
/* 303 */     if (ogmEdtResult.size() > 0) {
/* 304 */       ogm = (OrderGroupMessage)ogmEdtResult.get(0);
/*     */     }
/* 306 */     return ogm;
/*     */   }
/*     */ 
/*     */   public static OrderGroupMessage getOrderGroupByOrderId(String orderId) {
/* 310 */     OrderGroupMessage ogm = null;
/*     */ 
/* 312 */     List ogmEdtResult = new ArrayList(1);
/*     */ 
/* 314 */     if (SwingUtilities.isEventDispatchThread())
/* 315 */       getOrderGroupByOrderId(orderId, ogmEdtResult);
/*     */     else {
/*     */       try {
/* 318 */         SwingUtilities.invokeAndWait(new Runnable(orderId, ogmEdtResult) {
/*     */           public void run() {
/* 320 */             OrderMessageUtils.access$100(this.val$orderId, this.val$ogmEdtResult);
/*     */           } } );
/*     */       } catch (InterruptedException e) {
/* 324 */         LOGGER.error(e.getMessage(), e);
/*     */       } catch (InvocationTargetException e) {
/* 326 */         LOGGER.error(e.getMessage(), e);
/*     */       }
/*     */     }
/*     */ 
/* 330 */     if (ogmEdtResult.size() > 0) {
/* 331 */       ogm = (OrderGroupMessage)ogmEdtResult.get(0);
/*     */     }
/* 333 */     return ogm;
/*     */   }
/*     */ 
/*     */   public static Position getPositionById(String orderGroupId)
/*     */   {
/* 338 */     Position ogm = null;
/*     */ 
/* 340 */     List ogmEdtResult = new ArrayList(1);
/*     */ 
/* 342 */     if (SwingUtilities.isEventDispatchThread())
/* 343 */       getPositionById(orderGroupId, ogmEdtResult);
/*     */     else {
/*     */       try {
/* 346 */         SwingUtilities.invokeAndWait(new Runnable(orderGroupId, ogmEdtResult) {
/*     */           public void run() {
/* 348 */             OrderMessageUtils.access$200(this.val$orderGroupId, this.val$ogmEdtResult);
/*     */           } } );
/*     */       } catch (InterruptedException e) {
/* 352 */         LOGGER.error(e.getMessage(), e);
/*     */       } catch (InvocationTargetException e) {
/* 354 */         LOGGER.error(e.getMessage(), e);
/*     */       }
/*     */     }
/*     */ 
/* 358 */     if (ogmEdtResult.size() > 0) {
/* 359 */       ogm = (Position)ogmEdtResult.get(0);
/*     */     }
/* 361 */     return ogm;
/*     */   }
/*     */ 
/*     */   public static OrderCommonTableModel getOrderTableModel() {
/* 365 */     ClientForm clientForm = (ClientForm)GreedContext.get("clientGui");
/* 366 */     TableSorter tableSorter = (TableSorter)clientForm.getOrdersPanel().getOrdersTable().getModel();
/* 367 */     OrderCommonTableModel orderTableModel = (OrderCommonTableModel)tableSorter.getTableModel();
/* 368 */     return orderTableModel;
/*     */   }
/*     */ 
/*     */   private static void getOrderGroupById(String orderGroupId, List<OrderGroupMessage> ogmEdtResult) {
/* 372 */     OrderCommonTableModel orderTableModel = getOrderTableModel();
/*     */     try
/*     */     {
/* 375 */       OrderGroupMessage orderGroupMessage = orderTableModel.getGroup(orderGroupId);
/* 376 */       if (orderGroupMessage != null) {
/* 377 */         OrderGroupMessage groupCopy = copyOrderGroup(orderGroupMessage);
/* 378 */         ogmEdtResult.add(groupCopy);
/*     */       }
/*     */     } catch (ParseException e) {
/* 381 */       LOGGER.error(e.getMessage(), e);
/*     */     }
/*     */   }
/*     */ 
/*     */   public OrderMessage getIfDoneLimitOrderByOredrId(OrderMessage orderMessage) {
/* 386 */     if ((orderMessage != null) && (!orderMessage.isOpening())) {
/* 387 */       for (OrderMessage order : getIfDoneRelatedOrdersByOrderId(orderMessage.getOrderId())) {
/* 388 */         if (order.isIfdLimit()) {
/* 389 */           return order;
/*     */         }
/*     */       }
/*     */     }
/* 393 */     return null;
/*     */   }
/*     */ 
/*     */   public static OrderMessage getIfDoneOrderById(String orderId) {
/* 397 */     if (orderId != null) {
/* 398 */       for (OrderMessage om : getOrderTableModel().getPendingOrders()) {
/* 399 */         if (orderId.equals(om.getOrderId())) {
/* 400 */           return om;
/*     */         }
/*     */       }
/*     */     }
/* 404 */     return null;
/*     */   }
/*     */ 
/*     */   public OrderMessage getIfDoneStopOrder(OrderMessage orderMessage) {
/* 408 */     if ((orderMessage != null) && (!orderMessage.isOpening())) {
/* 409 */       for (OrderMessage order : getIfDoneRelatedOrdersByOrderId(orderMessage.getOrderId())) {
/* 410 */         if (order.isIfdStop()) {
/* 411 */           return order;
/*     */         }
/*     */       }
/*     */     }
/* 415 */     return null;
/*     */   }
/*     */ 
/*     */   public static List<OrderMessage> getIfDoneRelatedOrdersByOrderId(String orderId) {
/* 419 */     List ordersList = new ArrayList();
/* 420 */     if (orderId != null) {
/* 421 */       for (OrderMessage om : getOrderTableModel().getPendingOrders()) {
/* 422 */         if ((orderId.equals(om.getIfdParentOrderId())) || (orderId.equals(om.getOrderId())))
/*     */         {
/* 424 */           ordersList.add(om);
/*     */         }
/*     */       }
/*     */     }
/* 428 */     return ordersList;
/*     */   }
/*     */ 
/*     */   private static void getOrderGroupByOrderId(String orderId, List<OrderGroupMessage> ogmEdtResult) {
/* 432 */     OrderCommonTableModel orderTableModel = getOrderTableModel();
/*     */     try
/*     */     {
/* 435 */       OrderGroupMessage orderGroupMessage = orderTableModel.getGroupByOrderId(orderId);
/* 436 */       if (orderGroupMessage != null) {
/* 437 */         OrderGroupMessage groupCopy = copyOrderGroup(orderGroupMessage);
/* 438 */         ogmEdtResult.add(groupCopy);
/*     */       }
/*     */     } catch (ParseException e) {
/* 441 */       LOGGER.error(e.getMessage(), e);
/*     */     }
/*     */   }
/*     */ 
/*     */   private static void getPositionById(String orderGroupId, List<Position> ogmEdtResult) {
/* 446 */     ClientForm clientForm = (ClientForm)GreedContext.get("clientGui");
/* 447 */     Position position = clientForm.getPositionsPanel().getPositionsByGroupId(orderGroupId);
/*     */     try {
/* 449 */       if (position != null) {
/* 450 */         Position positionCopy = copyPosition(position);
/* 451 */         ogmEdtResult.add(positionCopy);
/*     */       }
/*     */     } catch (ParseException e) {
/* 454 */       LOGGER.error(e.getMessage(), e);
/*     */     }
/*     */   }
/*     */ 
/*     */   public static boolean isLimit(OrderMessage order)
/*     */   {
/* 463 */     return ((order.getStopDirection().equals(StopDirection.ASK_LESS)) && (OrderSide.BUY.equals(order.getSide())) && (null != order.getPriceTrailingLimit()) && (order.getPriceTrailingLimit().getValue().compareTo(BigDecimal.ZERO) == 0)) || ((order.getStopDirection().equals(StopDirection.BID_GREATER)) && (OrderSide.SELL.equals(order.getSide())) && (null != order.getPriceTrailingLimit()) && (order.getPriceTrailingLimit().getValue().compareTo(BigDecimal.ZERO) == 0));
/*     */   }
/*     */ 
/*     */   public static boolean isMit(OrderMessage order)
/*     */   {
/* 478 */     return ((order.getStopDirection().equals(StopDirection.ASK_LESS)) && (OrderSide.BUY.equals(order.getSide())) && (null != order.getPriceTrailingLimit()) && (order.getPriceTrailingLimit().getValue().compareTo(BigDecimal.ZERO) > 0)) || ((order.getStopDirection().equals(StopDirection.BID_GREATER)) && (OrderSide.SELL.equals(order.getSide())) && (null != order.getPriceTrailingLimit()) && (order.getPriceTrailingLimit().getValue().compareTo(BigDecimal.ZERO) > 0));
/*     */   }
/*     */ 
/*     */   public static boolean isStop(OrderMessage order)
/*     */   {
/* 494 */     return (isStopOrLimitOpening(order)) && (!isLimit(order)) && (!isMit(order));
/*     */   }
/*     */ 
/*     */   public static boolean isStopOrLimitOpening(OrderMessage order)
/*     */   {
/* 501 */     if (order.isPlaceOffer()) {
/* 502 */       return false;
/*     */     }
/* 504 */     if (!order.getOrderState().equals(OrderState.PENDING)) {
/* 505 */       return false;
/*     */     }
/* 507 */     if (!order.isOpening()) {
/* 508 */       return false;
/*     */     }
/*     */ 
/* 511 */     return order.getPriceStop() != null;
/*     */   }
/*     */ 
/*     */   public static OrderGroupMessage copyOrderGroup(OrderGroupMessage groupToCopy)
/*     */     throws ParseException
/*     */   {
/* 517 */     return copyOrderGroup(groupToCopy.toString());
/*     */   }
/*     */ 
/*     */   public static OrderGroupMessage copyOrderGroup(String groupStringToCopy) throws ParseException {
/* 521 */     return new OrderGroupMessage(new ProtocolMessage(groupStringToCopy));
/*     */   }
/*     */ 
/*     */   public static Position copyPosition(Position positionToCopy) throws ParseException
/*     */   {
/* 526 */     return copyPosition(positionToCopy.toString(0));
/*     */   }
/*     */ 
/*     */   public static Position copyPosition(String positionStringToCopy) throws ParseException {
/* 530 */     return new Position(new ProtocolMessage(positionStringToCopy));
/*     */   }
/*     */ 
/*     */   public static void postNotification(Object source, Date ts, String message)
/*     */   {
/* 536 */     if (!printMessages.contains(Integer.valueOf(message.hashCode())))
/*     */     {
/* 538 */       Notification notification = new Notification(ts, message);
/*     */ 
/* 541 */       PostMessageAction action = new PostMessageAction(source, notification);
/* 542 */       GreedContext.publishEvent(action);
/*     */ 
/* 544 */       printMessages.add(Integer.valueOf(message.hashCode()));
/*     */     }
/*     */   }
/*     */ 
/*     */   public static void missNotification(String message) {
/* 549 */     if (!printMessages.contains(Integer.valueOf(message.hashCode())))
/* 550 */       printMessages.add(Integer.valueOf(message.hashCode()));
/*     */   }
/*     */ 
/*     */   public static void roughGroupValidation(OrderGroupMessage group)
/*     */   {
/* 555 */     for (OrderMessage om : group.getOrders())
/* 556 */       assert (om != null) : ("group " + group.getOrderGroupId() + " contains null order!");
/*     */   }
/*     */ 
/*     */   public static String getIFDOpositeOrderForOCO(OrderMessage order)
/*     */   {
/* 561 */     if (order != null) {
/* 562 */       List ordersList = getOpenIfOrderList(getOrderGroupByOrderId(order.getOrderId()));
/* 563 */       for (OrderMessage o : ordersList) {
/* 564 */         if (!o.equals(order))
/* 565 */           return o.getOrderId();
/*     */       }
/*     */     }
/* 568 */     return "";
/*     */   }
/*     */ 
/*     */   static
/*     */   {
/*  47 */     LOGGER = LoggerFactory.getLogger(OrderMessageUtils.class);
/*     */ 
/* 533 */     printMessages = new HashSet();
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.util.OrderMessageUtils
 * JD-Core Version:    0.6.0
 */