/*     */ package com.dukascopy.dds2.greed.gui.component.orders;
/*     */ 
/*     */ import com.dukascopy.api.Instrument;
/*     */ import com.dukascopy.dds2.greed.GreedContext;
/*     */ import com.dukascopy.dds2.greed.gui.GuiUtilsAndConstants;
/*     */ import com.dukascopy.dds2.greed.gui.util.lotamount.LotAmountChanger;
/*     */ import com.dukascopy.dds2.greed.model.AccountStatement;
/*     */ import com.dukascopy.dds2.greed.util.OrderMessageUtils;
/*     */ import com.dukascopy.transport.common.model.type.Money;
/*     */ import com.dukascopy.transport.common.model.type.OrderDirection;
/*     */ import com.dukascopy.transport.common.model.type.OrderSide;
/*     */ import com.dukascopy.transport.common.model.type.OrderState;
/*     */ import com.dukascopy.transport.common.msg.group.OrderGroupMessage;
/*     */ import com.dukascopy.transport.common.msg.group.OrderMessage;
/*     */ import com.dukascopy.transport.common.msg.request.AccountInfoMessage;
/*     */ import java.math.BigDecimal;
/*     */ import java.math.RoundingMode;
/*     */ import java.text.SimpleDateFormat;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collection;
/*     */ import java.util.Date;
/*     */ import java.util.HashSet;
/*     */ import java.util.LinkedHashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Map.Entry;
/*     */ import java.util.Set;
/*     */ import org.slf4j.Logger;
/*     */ import org.slf4j.LoggerFactory;
/*     */ 
/*     */ public class OrderTableModel extends OrderCommonTableModel
/*     */ {
/*  35 */   private static Logger LOGGER = LoggerFactory.getLogger(OrderTableModel.class);
/*     */ 
/*  37 */   private final Map<String, OrderGroupMessage> groups = new LinkedHashMap();
/*     */ 
/*  39 */   private final Set<String> filledOrders = new HashSet();
/*     */ 
/*  41 */   private final Set<String> executingOrders = new HashSet();
/*     */ 
/*     */   public List<OrderMessage> getPendingOrders()
/*     */   {
/*  45 */     if (this.cachedPendingOrders.isEmpty()) {
/*  46 */       List pendingOrders = new ArrayList();
/*     */ 
/*  48 */       Collection values = this.groups.values();
/*  49 */       boolean orderState = true;
/*  50 */       boolean orderDirection = true;
/*  51 */       for (OrderGroupMessage group : values) {
/*  52 */         for (OrderMessage order : group.getOrders()) {
/*  53 */           orderState = (order.getOrderState() == OrderState.PENDING) || (order.getOrderState() == OrderState.EXECUTING);
/*     */ 
/*  58 */           if ((orderState) && (orderDirection) && (!this.filledOrders.contains(order.getOrderId())))
/*     */           {
/*  61 */             pendingOrders.add(order);
/*     */           }
/*     */         }
/*     */       }
/*  65 */       this.cachedPendingOrders.addAll(pendingOrders);
/*     */     }
/*     */ 
/*  68 */     return this.cachedPendingOrders;
/*     */   }
/*     */ 
/*     */   public Object getValueAt(int rowIndex, int columnIndex)
/*     */   {
/*  73 */     if ((rowIndex >= getRowCount()) || (rowIndex < 0)) {
/*  74 */       return null;
/*     */     }
/*  76 */     OrderMessage order = (OrderMessage)getPendingOrders().get(rowIndex);
/*  77 */     if (order == null) {
/*  78 */       return null;
/*     */     }
/*  80 */     if (columnIndex == COLUMN_CHECK) {
/*  81 */       return order;
/*     */     }
/*  83 */     if (columnIndex == COLUMN_TIMESTAMP) {
/*  84 */       Date dateCreated = order.getCreatedDate();
/*  85 */       if (dateCreated != null) {
/*     */         try {
/*  87 */           return this.dateFormat.format(dateCreated);
/*     */         } catch (Exception e) {
/*  89 */           LOGGER.error(e.getMessage(), e);
/*  90 */           return null;
/*     */         }
/*     */       }
/*  93 */       return null;
/*     */     }
/*     */ 
/*  97 */     if (columnIndex == COLUMN_ID) {
/*  98 */       return order.getOrderId();
/*     */     }
/* 100 */     String extId = null;
/* 101 */     if (order.getExternalSysId() != null) {
/* 102 */       extId = order.getExternalSysId().toString();
/*     */     }
/* 104 */     if (columnIndex == COLUMN_POSITION) {
/* 105 */       return GreedContext.getOrderGroupIdForView(order.getOrderGroupId());
/*     */     }
/* 107 */     if ((isJForexRunning) && 
/* 108 */       (columnIndex == 2)) {
/* 109 */       if (extId != null) {
/* 110 */         return extId;
/*     */       }
/* 112 */       return null;
/*     */     }
/*     */ 
/* 117 */     if (columnIndex == COLUMN_INSTRUMENT) {
/* 118 */       return order.getInstrument();
/*     */     }
/*     */ 
/* 121 */     if (columnIndex == COLUMN_SIDE) {
/* 122 */       return order.getSide().toString();
/*     */     }
/*     */ 
/* 125 */     if (columnIndex == COLUMN_REQ_AMOUNT)
/*     */     {
/* 127 */       BigDecimal divider = LotAmountChanger.getLotAmountForInstrument(Instrument.fromString(order.getInstrument()));
/*     */ 
/* 129 */       BigDecimal milAmount = order.getAmount().getValue().divide(divider, 6, RoundingMode.HALF_EVEN).stripTrailingZeros();
/*     */ 
/* 135 */       return order.getAmount().getCurrency() + " " + milAmount.toPlainString();
/*     */     }
/*     */ 
/* 138 */     if (columnIndex == COLUMN_TYPE)
/*     */     {
/* 140 */       if ((order.isStopLoss()) || (order.isTakeProfit())) {
/* 141 */         OrderGroupMessage group = (OrderGroupMessage)this.groups.get(order.getOrderGroupId());
/* 142 */         if (null != group) {
/* 143 */           return getSlAandTpType(group, order.isTakeProfit());
/*     */         }
/*     */       }
/*     */ 
/* 147 */       if (order.isPlaceOffer()) {
/* 148 */         return OrderSide.BUY == order.getSide() ? "BID" : "OFFER";
/*     */       }
/*     */ 
/* 151 */       if ((null != order.getPriceStop()) && (order.getOrderDirection() == OrderDirection.OPEN)) {
/* 152 */         return "ENTRY";
/*     */       }
/*     */ 
/* 155 */       return "Trade";
/*     */     }
/*     */ 
/* 158 */     if (columnIndex == COLUMN_PRICE) {
/* 159 */       return order;
/*     */     }
/*     */ 
/* 162 */     if (columnIndex == COLUMN_STATE)
/*     */     {
/* 164 */       if (order.isPlaceOffer()) {
/* 165 */         return "PLACED";
/*     */       }
/*     */ 
/* 168 */       return order.getOrderState().asString();
/*     */     }
/*     */ 
/* 171 */     if (columnIndex == COLUMN_EXP) {
/* 172 */       StringBuffer exp = new StringBuffer();
/* 173 */       if (order.getString("execTimeoutMillis") != null)
/*     */         try {
/* 175 */           exp.append(OrderMessageUtils.getTTLAsString(order));
/*     */         } catch (Exception e) {
/* 177 */           LOGGER.error(e.getMessage(), e);
/* 178 */           exp.append("GTC");
/*     */         }
/* 180 */       else if (OrderState.PENDING == order.getOrderState()) {
/* 181 */         exp.append("GTC");
/*     */       }
/*     */ 
/* 184 */       if ((order.isOco().booleanValue()) && (order.isOpening()) && (order.getPriceStop() != null) && (OrderState.PENDING.equals(order.getOrderState())))
/*     */       {
/* 187 */         exp.append("/OCO");
/*     */       }
/*     */ 
/* 190 */       return exp.toString();
/*     */     }
/*     */ 
/* 193 */     if (columnIndex == COLUMN_PROPS) {
/* 194 */       return constructTypeString(order);
/*     */     }
/* 196 */     return null;
/*     */   }
/*     */ 
/*     */   public OrderGroupMessage getGroup(int row)
/*     */   {
/* 202 */     GuiUtilsAndConstants.ensureEventDispatchThread();
/* 203 */     if (row < 0) {
/* 204 */       return null;
/*     */     }
/* 206 */     String orderGroupId = null;
/* 207 */     List pendingOrders = getPendingOrders();
/* 208 */     if (row >= pendingOrders.size()) {
/* 209 */       return null;
/*     */     }
/* 211 */     if (pendingOrders.size() < 1) {
/* 212 */       return null;
/*     */     }
/* 214 */     orderGroupId = ((OrderMessage)pendingOrders.get(row)).getOrderGroupId();
/* 215 */     return (OrderGroupMessage)this.groups.get(orderGroupId);
/*     */   }
/*     */ 
/*     */   public void updateTable(OrderGroupMessage group)
/*     */   {
/* 222 */     if (LOGGER.isDebugEnabled()) {
/* 223 */       OrderMessageUtils.roughGroupValidation(group);
/*     */     }
/*     */ 
/* 226 */     OrderGroupMessage ogm = group;
/* 227 */     if ((ogm != null) && ((ogm.getOrders() == null) || (group.getOrders().size() < 1)))
/*     */     {
/* 230 */       this.groups.remove(group.getOrderGroupId());
/*     */ 
/* 232 */       if ((!GreedContext.isGlobal()) && (!GreedContext.isGlobalExtended())) {
/* 233 */         LOGGER.debug("remember the closed group: " + group.getOrderGroupId());
/* 234 */         this.closedGroups.add(group.getOrderGroupId());
/*     */       }
/*     */     }
/*     */     else {
/* 238 */       preparePartialFill(group);
/*     */ 
/* 240 */       OrderMessage openingOrder = group.getOpeningOrder();
/*     */ 
/* 242 */       if ((null != openingOrder) && (!openingOrder.isStopLoss()) && (!openingOrder.isTakeProfit()))
/*     */       {
/* 244 */         if ((null != openingOrder) && (OrderState.FILLED.equals(openingOrder.getOrderState()))) {
/* 245 */           this.filledOrders.add(openingOrder.getOrderId());
/*     */         }
/*     */       }
/* 248 */       if ((openingOrder != null) && (OrderState.EXECUTING.equals(openingOrder.getOrderState())) && (this.filledOrders.contains(openingOrder.getOrderId())))
/*     */       {
/* 253 */         LOGGER.debug("OGMM : " + openingOrder.getOrderId() + ": " + group.getOrderGroupId() + " : ogm is skipped 1 ");
/* 254 */         return;
/*     */       }
/*     */ 
/* 257 */       if (!this.closedGroups.contains(group.getOrderGroupId())) {
/* 258 */         this.groups.put(group.getOrderGroupId(), group);
/*     */       }
/*     */     }
/* 261 */     this.cachedPendingOrders.clear();
/*     */ 
/* 264 */     boolean orderState = true;
/* 265 */     for (OrderMessage order : group.getOrders()) {
/* 266 */       orderState = (order.getOrderState() == OrderState.PENDING) || (order.getOrderState() == OrderState.EXECUTING);
/*     */ 
/* 268 */       if ((orderState) && 
/* 269 */         (!this.executingOrders.contains(order.getOrderGroupId()))) {
/* 270 */         LOGGER.debug("adding group to executing: " + order.getOrderGroupId());
/* 271 */         this.executingOrders.add(order.getOrderGroupId());
/* 272 */         if (this.filledOrders.contains(order.getOrderGroupId())) {
/* 273 */           LOGGER.warn("ALARM!!!: EXECUTING AFTER FILLED FOR #" + order.getOrderGroupId());
/*     */         }
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 279 */     fireTableDataChanged();
/*     */   }
/*     */ 
/*     */   private void preparePartialFill(OrderGroupMessage orderGroupMessage) {
/* 283 */     if ((orderGroupMessage == null) || (orderGroupMessage.getOrders().size() < 3)) return;
/*     */ 
/* 285 */     boolean isFilled = false;
/* 286 */     boolean isExecuting = false;
/* 287 */     boolean isPending = false;
/*     */ 
/* 289 */     for (OrderMessage order : orderGroupMessage.getOrders()) {
/* 290 */       if ((order.getOrderState() == OrderState.FILLED) && (isOpenOrder(order)) && (notSlorTP(order)))
/* 291 */         isFilled = true;
/* 292 */       else if ((order.getOrderState() == OrderState.EXECUTING) && (isOpenOrder(order)) && (notSlorTP(order)))
/* 293 */         isExecuting = true;
/* 294 */       else if ((order.getOrderState() == OrderState.PENDING) && (isOpenOrder(order)) && (notSlorTP(order))) {
/* 295 */         isPending = true;
/*     */       }
/*     */     }
/*     */ 
/* 299 */     if (!isFilled) return;
/*     */ 
/* 301 */     BigDecimal openingAmount = null;
/* 302 */     if (isExecuting) {
/* 303 */       openingAmount = getOpenBidOfferAmount(orderGroupMessage);
/*     */     }
/*     */ 
/* 306 */     if (isPending) {
/* 307 */       openingAmount = getOpenConditionalAmount(orderGroupMessage);
/*     */     }
/*     */ 
/* 310 */     if ((isPending) || (isExecuting))
/* 311 */       for (OrderMessage order : orderGroupMessage.getOrders())
/* 312 */         if ((order.isStopLoss()) || (order.isTakeProfit())) {
/* 313 */           AccountStatement accountStatement = (AccountStatement)GreedContext.get("accountStatement");
/* 314 */           Money value = new Money(openingAmount, accountStatement.getLastAccountState().getCurrency());
/* 315 */           order.setAmount(value);
/*     */         }
/*     */   }
/*     */ 
/*     */   private boolean notSlorTP(OrderMessage order)
/*     */   {
/* 323 */     return (!order.isStopLoss()) && (!order.isTakeProfit());
/*     */   }
/*     */ 
/*     */   private boolean isOpenOrder(OrderMessage order) {
/* 327 */     return order.getOrderDirection() == OrderDirection.OPEN;
/*     */   }
/*     */ 
/*     */   private String getSlAandTpType(OrderGroupMessage group, boolean isTP)
/*     */   {
/* 332 */     boolean hasPending = false;
/* 333 */     boolean hasFilled = false;
/*     */ 
/* 335 */     for (OrderMessage order : group.getOrders()) {
/* 336 */       if ((order.getOrderDirection() == OrderDirection.OPEN) && (order.getOrderState() == OrderState.PENDING))
/*     */       {
/* 338 */         hasPending = true;
/* 339 */       } else if ((order.getOrderDirection() == OrderDirection.OPEN) && (order.getOrderState() == OrderState.FILLED))
/*     */       {
/* 341 */         hasFilled = true;
/*     */       }
/*     */     }
/*     */ 
/* 345 */     if ((hasFilled) && (!hasPending)) {
/* 346 */       if (isTP) return "TP"; return "SL";
/* 347 */     }if ((!hasFilled) && (hasPending)) {
/* 348 */       if (isTP) return "IFD TP"; return "IFD SL";
/* 349 */     }if ((hasFilled) && (hasPending)) {
/* 350 */       if (isTP) return "TP/IFD TP"; return "SL/IFD SL";
/*     */     }
/*     */ 
/* 353 */     return "";
/*     */   }
/*     */ 
/*     */   private BigDecimal getOpenConditionalAmount(OrderGroupMessage group) {
/* 357 */     BigDecimal amount = BigDecimal.ZERO;
/* 358 */     for (OrderMessage order : group.getOrders()) {
/* 359 */       if ((order.getOrderDirection() == OrderDirection.OPEN) && ((order.getOrderState() == OrderState.PENDING) || (order.getOrderState() == OrderState.FILLED)))
/*     */       {
/* 362 */         amount = amount.add(order.getAmount().getValue());
/*     */       }
/*     */     }
/* 365 */     return amount;
/*     */   }
/*     */ 
/*     */   private BigDecimal getOpenBidOfferAmount(OrderGroupMessage group) {
/* 369 */     BigDecimal amount = BigDecimal.ZERO;
/* 370 */     for (OrderMessage order : group.getOrders()) {
/* 371 */       if ((order.getOrderDirection() == OrderDirection.OPEN) && ((order.getOrderState() == OrderState.PENDING) || (order.getOrderState() == OrderState.EXECUTING) || (order.getOrderState() == OrderState.FILLED)))
/*     */       {
/* 375 */         amount = amount.add(order.getAmount().getValue());
/*     */       }
/*     */     }
/* 378 */     return amount;
/*     */   }
/*     */ 
/*     */   public OrderGroupMessage getGroup(String orderGroupId)
/*     */   {
/* 384 */     return (OrderGroupMessage)this.groups.get(orderGroupId);
/*     */   }
/*     */ 
/*     */   public OrderGroupMessage getGroupByOrderId(String orderId)
/*     */   {
/* 389 */     for (Map.Entry entry : this.groups.entrySet()) {
/* 390 */       group = (OrderGroupMessage)entry.getValue();
/* 391 */       if (group != null)
/* 392 */         for (OrderMessage order : group.getOrders())
/* 393 */           if ((order.getOrderId() != null) && (order.getOrderId().equals(orderId)))
/* 394 */             return group;
/*     */     }
/*     */     OrderGroupMessage group;
/* 399 */     return null;
/*     */   }
/*     */ 
/*     */   public Collection<OrderGroupMessage> getGroups() {
/* 403 */     return this.groups.values();
/*     */   }
/*     */ 
/*     */   public void clear()
/*     */   {
/* 408 */     super.clear();
/* 409 */     this.groups.clear();
/* 410 */     fireTableDataChanged();
/*     */   }
/*     */ 
/*     */   public void updateTable(OrderMessage order) {
/* 414 */     LOGGER.warn("Cannot take reaction for OrderMessages on not global accounts");
/* 415 */     throw new RuntimeException("Cannot be Called.");
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.orders.OrderTableModel
 * JD-Core Version:    0.6.0
 */