/*     */ package com.dukascopy.dds2.greed.gui.component.orders;
/*     */ 
/*     */ import com.dukascopy.dds2.greed.GreedContext;
/*     */ import com.dukascopy.dds2.greed.gui.GuiUtilsAndConstants;
/*     */ import com.dukascopy.dds2.greed.util.OrderMessageUtils;
/*     */ import com.dukascopy.transport.common.model.type.Money;
/*     */ import com.dukascopy.transport.common.model.type.OrderDirection;
/*     */ import com.dukascopy.transport.common.model.type.OrderSide;
/*     */ import com.dukascopy.transport.common.model.type.OrderState;
/*     */ import com.dukascopy.transport.common.msg.group.OrderGroupMessage;
/*     */ import com.dukascopy.transport.common.msg.group.OrderMessage;
/*     */ import java.math.BigDecimal;
/*     */ import java.math.RoundingMode;
/*     */ import java.text.SimpleDateFormat;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collection;
/*     */ import java.util.Date;
/*     */ import java.util.LinkedHashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import org.slf4j.Logger;
/*     */ import org.slf4j.LoggerFactory;
/*     */ 
/*     */ public class GlobalOrderTableModel extends OrderCommonTableModel
/*     */ {
/*  26 */   private static final Logger LOGGER = LoggerFactory.getLogger(GlobalOrderTableModel.class);
/*     */ 
/*  28 */   private final Map<String, OrderMessage> orders = new LinkedHashMap();
/*     */ 
/*     */   public List<OrderMessage> getPendingOrders()
/*     */   {
/*  32 */     if (this.cachedPendingOrders.isEmpty()) {
/*  33 */       List pendingOrders = new ArrayList();
/*     */ 
/*  35 */       Collection values = this.orders.values();
/*  36 */       boolean orderState = true;
/*  37 */       for (OrderMessage order : values) {
/*  38 */         orderState = (order.getOrderState() == OrderState.PENDING) || (order.getOrderState() == OrderState.EXECUTING);
/*     */ 
/*  40 */         if (orderState) {
/*  41 */           pendingOrders.add(order);
/*     */         }
/*     */       }
/*  44 */       this.cachedPendingOrders.addAll(pendingOrders);
/*     */     }
/*     */ 
/*  47 */     return this.cachedPendingOrders;
/*     */   }
/*     */ 
/*     */   public Object getValueAt(int rowIndex, int columnIndex)
/*     */   {
/*  52 */     if ((rowIndex >= getRowCount()) || (rowIndex < 0)) {
/*  53 */       return null;
/*     */     }
/*  55 */     OrderMessage order = (OrderMessage)getPendingOrders().get(rowIndex);
/*  56 */     if (order == null) {
/*  57 */       return null;
/*     */     }
/*  59 */     if (columnIndex == COLUMN_CHECK) {
/*  60 */       return order;
/*     */     }
/*  62 */     if (columnIndex == COLUMN_TIMESTAMP) {
/*  63 */       Date dateCreated = order.getCreatedDate();
/*  64 */       if (dateCreated != null) {
/*     */         try {
/*  66 */           return this.dateFormat.format(dateCreated);
/*     */         } catch (Exception e) {
/*  68 */           LOGGER.error(e.getMessage(), e);
/*  69 */           return null;
/*     */         }
/*     */       }
/*  72 */       return null;
/*     */     }
/*     */ 
/*  76 */     if (columnIndex == COLUMN_ID) {
/*  77 */       return order.getOrderId();
/*     */     }
/*     */ 
/*  80 */     String id = GreedContext.getOrderGroupIdForView(order.getOrderGroupId());
/*     */ 
/*  82 */     String extId = null;
/*  83 */     if (order.getExternalSysId() != null) {
/*  84 */       extId = order.getExternalSysId().toString();
/*     */     }
/*  86 */     if (!isJForexRunning) {
/*  87 */       if (columnIndex == COLUMN_POSITION) {
/*  88 */         if (extId != null) {
/*  89 */           id = "[" + extId + "] " + id;
/*     */         }
/*  91 */         return id;
/*     */       }
/*     */     } else {
/*  94 */       if (columnIndex == 2) {
/*  95 */         if (extId != null) {
/*  96 */           return extId;
/*     */         }
/*  98 */         return null;
/*     */       }
/*     */ 
/* 102 */       if (columnIndex == COLUMN_POSITION) {
/* 103 */         return id;
/*     */       }
/*     */     }
/*     */ 
/* 107 */     if (columnIndex == COLUMN_INSTRUMENT) {
/* 108 */       return order.getInstrument();
/*     */     }
/*     */ 
/* 111 */     if (columnIndex == COLUMN_SIDE) {
/* 112 */       return order.getSide().toString();
/*     */     }
/*     */ 
/* 115 */     if (columnIndex == COLUMN_REQ_AMOUNT) {
/* 116 */       BigDecimal milAmount = order.getAmount().getValue().divide(GuiUtilsAndConstants.ONE_MILLION, 6, RoundingMode.HALF_EVEN).stripTrailingZeros();
/*     */ 
/* 122 */       return order.getAmount().getCurrency() + " " + milAmount.toPlainString();
/*     */     }
/*     */ 
/* 125 */     if (columnIndex == COLUMN_TYPE) {
/* 126 */       if ((order.isIfdStop()) || (order.isIfdLimit())) {
/* 127 */         String ifDone = "";
/* 128 */         if (null != order) {
/* 129 */           OrderMessage openingOrder = (OrderMessage)this.orders.get(order.getIfdParentOrderId());
/* 130 */           if ((null != openingOrder) && (!OrderState.FILLED.equals(openingOrder.getOrderState()))) {
/* 131 */             ifDone = "IFD ";
/*     */           }
/*     */         }
/*     */ 
/* 135 */         if (order.isIfdStop()) {
/* 136 */           return ifDone + "STOP";
/*     */         }
/*     */ 
/* 139 */         if (order.isIfdLimit()) {
/* 140 */           return ifDone + "LIMIT";
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/* 145 */       if (order.isPlaceOffer()) {
/* 146 */         return OrderSide.BUY == order.getSide() ? "BID" : "OFFER";
/*     */       }
/*     */ 
/* 149 */       if ((null != order.getPriceStop()) && (order.getOrderDirection() == OrderDirection.OPEN)) {
/* 150 */         return "ENTRY";
/*     */       }
/*     */ 
/* 153 */       return "Trade";
/*     */     }
/*     */ 
/* 156 */     if (columnIndex == COLUMN_PRICE) {
/* 157 */       return order;
/*     */     }
/*     */ 
/* 160 */     if (columnIndex == COLUMN_STATE) {
/* 161 */       StringBuffer result = new StringBuffer("--/-");
/* 162 */       if (order.isPlaceOffer()) {
/* 163 */         result.replace(0, result.length(), "PLACED");
/* 164 */       } else if ((order.getIfdParentOrderId() != null) && (!"0".equals(order.getIfdParentOrderId()))) {
/* 165 */         String parentId = retrieveParentId(order);
/*     */ 
/* 167 */         if (isParentInExecuting(parentId)) {
/* 168 */           result.replace(0, result.length(), "PENDING");
/*     */         } else {
/* 170 */           result.replace(0, result.length(), "IFD (");
/* 171 */           result.append(order.getIfdParentOrderId());
/* 172 */           result.append(")");
/*     */         }
/*     */ 
/* 175 */         String relatedOcoId = getIFDOcoRelatedOrderId(order.getOrderId(), order.getIfdParentOrderId());
/* 176 */         if ((order.isOco().booleanValue()) && (order.isOpening()) && (order.getPriceStop() != null) && (OrderState.PENDING.equals(order.getOrderState())) && (relatedOcoId != null))
/*     */         {
/* 180 */           result.append("/OCO(");
/* 181 */           result.append(relatedOcoId);
/* 182 */           result.append(")");
/*     */         }
/*     */       } else {
/* 185 */         result.replace(0, result.length(), order.getOrderState().asString());
/* 186 */         String relatedOcoId = getOcoRelatedOrderId(order.getOrderId(), order.getOcoGroup());
/* 187 */         if ((order.isOco().booleanValue()) && (relatedOcoId != null)) {
/* 188 */           result.append("/OCO(");
/* 189 */           result.append(relatedOcoId);
/* 190 */           result.append(")");
/*     */         }
/*     */       }
/* 193 */       return result.toString();
/*     */     }
/*     */ 
/* 196 */     if (columnIndex == COLUMN_EXP) {
/* 197 */       StringBuffer exp = new StringBuffer();
/* 198 */       if (order.getString("execTimeoutMillis") != null)
/*     */         try {
/* 200 */           exp.append(OrderMessageUtils.getTTLAsString(order));
/*     */         } catch (Exception e) {
/* 202 */           LOGGER.error(e.getMessage(), e);
/* 203 */           exp.append("GTC");
/*     */         }
/* 205 */       else if (OrderState.PENDING == order.getOrderState()) {
/* 206 */         exp.append("GTC");
/*     */       }
/*     */ 
/* 209 */       return exp.toString();
/*     */     }
/*     */ 
/* 212 */     if (columnIndex == COLUMN_PROPS) {
/* 213 */       return constructTypeString(order);
/*     */     }
/* 215 */     return null;
/*     */   }
/*     */ 
/*     */   public OrderGroupMessage getGroup(int row)
/*     */   {
/* 221 */     GuiUtilsAndConstants.ensureEventDispatchThread();
/* 222 */     if (row < 0) {
/* 223 */       return null;
/*     */     }
/*     */ 
/* 226 */     List pendingOrders = getPendingOrders();
/* 227 */     if (row >= pendingOrders.size()) {
/* 228 */       return null;
/*     */     }
/* 230 */     if (pendingOrders.size() < 1) {
/* 231 */       return null;
/*     */     }
/*     */ 
/* 234 */     return getGroupByRowIndex(row);
/*     */   }
/*     */ 
/*     */   public void updateTable(OrderMessage order)
/*     */   {
/* 239 */     GuiUtilsAndConstants.ensureEventDispatchThread();
/*     */ 
/* 241 */     boolean orderToAdd = (order.getOrderState() == OrderState.PENDING) || (order.getOrderState() == OrderState.EXECUTING);
/* 242 */     if (orderToAdd)
/* 243 */       this.orders.put(order.getOrderId(), order);
/*     */     else {
/* 245 */       this.orders.remove(order.getOrderId());
/*     */     }
/* 247 */     this.cachedPendingOrders.clear();
/*     */ 
/* 249 */     fireTableDataChanged();
/*     */   }
/*     */ 
/*     */   public OrderGroupMessage getGroup(String orderGroupId) {
/* 253 */     return getGroupByGroupId(orderGroupId);
/*     */   }
/*     */ 
/*     */   public void clear() {
/* 257 */     super.clear();
/* 258 */     this.orders.clear();
/* 259 */     fireTableDataChanged();
/*     */   }
/*     */ 
/*     */   private String retrieveParentId(OrderMessage orderMessage) {
/* 263 */     if (orderMessage.getIfdParentOrderId() != null) {
/* 264 */       if ("ifds".equals(orderMessage.getIfdType()))
/* 265 */         return orderMessage.getIfdParentOrderId();
/* 266 */       if ("ifdm".equals(orderMessage.getIfdType())) {
/* 267 */         return orderMessage.getParentOrderId();
/*     */       }
/*     */     }
/* 270 */     return orderMessage.getExternalSysId();
/*     */   }
/*     */ 
/*     */   private OrderGroupMessage getGroupByRowIndex(int row) {
/* 274 */     OrderGroupMessage result = new OrderGroupMessage();
/*     */ 
/* 276 */     String orderGroupId = retrieveParentId((OrderMessage)getPendingOrders().get(row));
/*     */ 
/* 279 */     if (orderGroupId == null) {
/* 280 */       LOGGER.warn("Order group ID is null.");
/* 281 */       return null;
/*     */     }
/*     */ 
/* 284 */     List group = new ArrayList();
/*     */ 
/* 286 */     OrderMessage order = null;
/* 287 */     for (OrderMessage pendingOrder : getPendingOrders()) {
/* 288 */       if (orderGroupId.equals(retrieveParentId(pendingOrder)))
/*     */       {
/* 290 */         group.add(pendingOrder);
/* 291 */         order = pendingOrder;
/*     */       }
/*     */     }
/*     */ 
/* 295 */     if (order == null) return result;
/* 296 */     result.setOrderGroupId(order.getOrderGroupId());
/* 297 */     result.setInstrument(order.getInstrument());
/* 298 */     result.setAmount(order.getAmount());
/* 299 */     result.setOrders(group);
/*     */ 
/* 301 */     return result;
/*     */   }
/*     */ 
/*     */   private OrderGroupMessage getGroupByGroupId(String groupId) {
/* 305 */     OrderGroupMessage result = new OrderGroupMessage();
/*     */ 
/* 307 */     List group = new ArrayList();
/*     */ 
/* 309 */     OrderMessage order = null;
/* 310 */     for (OrderMessage orderMessage : getPendingOrders()) {
/* 311 */       if (groupId.equals(orderMessage.getOrderGroupId())) {
/* 312 */         group.add(orderMessage);
/* 313 */         order = orderMessage;
/*     */       }
/*     */     }
/*     */ 
/* 317 */     if (order == null) return result;
/* 318 */     result.setOrderGroupId(order.getOrderGroupId());
/* 319 */     result.setInstrument(order.getInstrument());
/* 320 */     result.setAmount(order.getAmount());
/* 321 */     result.setOrders(group);
/*     */ 
/* 323 */     return result;
/*     */   }
/*     */ 
/*     */   public OrderGroupMessage getGroupByOrderId(String orderId) {
/* 327 */     OrderGroupMessage result = new OrderGroupMessage();
/*     */ 
/* 329 */     List group = new ArrayList();
/*     */ 
/* 331 */     OrderMessage order = null;
/* 332 */     for (OrderMessage orderMessage : getPendingOrders()) {
/* 333 */       if (orderId.equals(orderMessage.getOrderGroupId())) {
/* 334 */         group.add(orderMessage);
/* 335 */         order = orderMessage;
/*     */       }
/*     */     }
/*     */ 
/* 339 */     if (order == null) return result;
/* 340 */     result.setOrderGroupId(order.getOrderGroupId());
/* 341 */     result.setInstrument(order.getInstrument());
/* 342 */     result.setAmount(order.getAmount());
/* 343 */     result.setOrders(group);
/*     */ 
/* 345 */     return result;
/*     */   }
/*     */ 
/*     */   private String getOcoRelatedOrderId(String orderId, String ocoGroupId) {
/* 349 */     String ocoRelatedOrderId = null;
/* 350 */     if (ocoGroupId == null) return orderId;
/* 351 */     for (OrderMessage order : getPendingOrders()) {
/* 352 */       if ((order.getOcoGroup() != null) && (order.getOcoGroup().equals(ocoGroupId)) && (orderId != order.getOrderId()))
/*     */       {
/* 355 */         ocoRelatedOrderId = order.getOrderId();
/*     */       }
/*     */     }
/* 358 */     return ocoRelatedOrderId;
/*     */   }
/*     */ 
/*     */   private String getIFDOcoRelatedOrderId(String orderId, String parentId) {
/* 362 */     if (parentId == null) return orderId;
/* 363 */     for (OrderMessage order : getPendingOrders()) {
/* 364 */       if ((parentId.equals(order.getIfdParentOrderId())) && (!order.getOrderId().equals(orderId))) {
/* 365 */         return order.getOrderId();
/*     */       }
/*     */     }
/* 368 */     return null;
/*     */   }
/*     */ 
/*     */   private boolean isParentInExecuting(String orderId) {
/* 372 */     OrderGroupMessage ogm = getGroupByOrderId(orderId);
/* 373 */     if (ogm == null) return false;
/* 374 */     for (OrderMessage message : ogm.getOrders()) {
/* 375 */       if (OrderState.EXECUTING.equals(message.getOrderState())) {
/* 376 */         return true;
/*     */       }
/*     */     }
/* 379 */     return false;
/*     */   }
/*     */ 
/*     */   public void updateTable(OrderGroupMessage orderGroup)
/*     */   {
/* 384 */     LOGGER.warn("Cannot take reaction for OrderGrupMessages on global accounts");
/* 385 */     throw new RuntimeException("Cannot be Called.");
/*     */   }
/*     */ 
/*     */   public Collection<OrderGroupMessage> getGroups()
/*     */   {
/* 390 */     throw new RuntimeException("Not implemented for JForex yet.");
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.orders.GlobalOrderTableModel
 * JD-Core Version:    0.6.0
 */