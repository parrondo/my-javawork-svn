/*     */ package com.dukascopy.dds2.events.handler;
/*     */ 
/*     */ import com.dukascopy.dds2.events.ChangeOrderConditionEvent;
/*     */ import com.dukascopy.dds2.events.ClientEvent;
/*     */ import com.dukascopy.dds2.events.Event;
/*     */ import com.dukascopy.dds2.events.EventType;
/*     */ import com.dukascopy.dds2.events.LoginEvent;
/*     */ import com.dukascopy.dds2.events.NotificationEvent;
/*     */ import com.dukascopy.dds2.events.OrderRelatedEvent;
/*     */ import com.dukascopy.dds2.events.OrderStateChangeEvent;
/*     */ import com.dukascopy.dds2.events.SubmitOrderEvent;
/*     */ import com.dukascopy.dds2.events.TradeEvent;
/*     */ import com.dukascopy.transport.common.model.type.Money;
/*     */ import com.dukascopy.transport.common.msg.group.OrderMessage;
/*     */ import java.math.BigDecimal;
/*     */ import java.sql.Connection;
/*     */ import java.sql.PreparedStatement;
/*     */ import java.sql.ResultSet;
/*     */ import java.sql.SQLException;
/*     */ import java.sql.Statement;
/*     */ import java.sql.Timestamp;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Date;
/*     */ import java.util.HashMap;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Map.Entry;
/*     */ import java.util.Set;
/*     */ import javax.sql.DataSource;
/*     */ import org.slf4j.Logger;
/*     */ import org.slf4j.LoggerFactory;
/*     */ 
/*     */ public class EventDBWriterTask
/*     */   implements Runnable
/*     */ {
/*  43 */   private static final Logger log = LoggerFactory.getLogger(EventDBWriterTask.class);
/*     */   public static final String EVENT_SEQUENCE = "SQN_EVENTS";
/*     */   public static final String SUBMIT_ORDER_SEQUENCE = "SQN_EVENTS_OS";
/*     */   private Event event;
/*     */   private DataSource ds;
/*  53 */   private String serviceId = null;
/*     */   private Connection connection;
/*     */ 
/*     */   public EventDBWriterTask(Event event, String serviceId, DataSource ds)
/*     */   {
/*  58 */     this.event = event;
/*  59 */     this.ds = ds;
/*  60 */     this.serviceId = serviceId;
/*     */   }
/*     */ 
/*     */   private Connection getConnection() throws SQLException {
/*  64 */     if (this.connection == null) {
/*  65 */       this.connection = this.ds.getConnection();
/*     */     }
/*  67 */     return this.connection;
/*     */   }
/*     */ 
/*     */   public void run() {
/*     */     try {
/*  72 */       Event event = this.event;
/*  73 */       int eventId = storeGenericEvent(event);
/*     */ 
/*  76 */       if (eventId > 0)
/*  77 */         while (event != null) {
/*  78 */           storeEvent(eventId, event);
/*  79 */           event = event.getNextEvent();
/*     */         }
/*     */     }
/*     */     catch (Exception e) {
/*  83 */       log.error("Failed to store event: " + this.event, e);
/*     */     }
/*     */     try {
/*  86 */       getConnection().close();
/*     */     } catch (Exception e) {
/*  88 */       throw new RuntimeException(e);
/*     */     }
/*     */   }
/*     */ 
/*     */   private void storeEvent(int eventId, Event event) throws SQLException {
/*  93 */     if ((event instanceof ClientEvent))
/*  94 */       storeClientEvent(eventId, (ClientEvent)event);
/*  95 */     else if ((event instanceof LoginEvent))
/*  96 */       storeLoginEvent(eventId, (LoginEvent)event);
/*  97 */     else if ((event instanceof NotificationEvent))
/*  98 */       storeNotificationEvent(eventId, (NotificationEvent)event);
/*  99 */     else if ((event instanceof SubmitOrderEvent))
/* 100 */       storeSubmitOrderEvent(eventId, (SubmitOrderEvent)event);
/* 101 */     else if ((event instanceof OrderStateChangeEvent))
/* 102 */       storeOrderStateChangeEvent(eventId, (OrderStateChangeEvent)event);
/* 103 */     else if ((event instanceof TradeEvent))
/* 104 */       storeTradeEvent(eventId, (TradeEvent)event);
/* 105 */     else if ((event instanceof ChangeOrderConditionEvent))
/* 106 */       storeChangeOrderConditionEvent(eventId, (ChangeOrderConditionEvent)event);
/* 107 */     else if ((event instanceof OrderRelatedEvent))
/* 108 */       storeOrderRelatedEvent(eventId, (OrderRelatedEvent)event);
/*     */   }
/*     */ 
/*     */   private int getNextEventId()
/*     */     throws SQLException
/*     */   {
/* 120 */     ResultSet result = getConnection().createStatement().executeQuery("select SQN_EVENTS.nextval from dual");
/* 121 */     if (result.next()) {
/* 122 */       return result.getInt(1);
/*     */     }
/* 124 */     return -1;
/*     */   }
/*     */ 
/*     */   private int storeGenericEvent(Event event)
/*     */     throws SQLException
/*     */   {
/* 135 */     int eventId = getNextEventId();
/* 136 */     event.setId(eventId);
/* 137 */     if (eventId > 0) {
/* 138 */       Map values = new HashMap();
/* 139 */       values.put("id", Long.valueOf(event.getId()));
/* 140 */       values.put("type", EventType.getInstance(event.getClass()));
/* 141 */       values.put("timestmp", event.getTimestamp());
/* 142 */       values.put("service_id", this.serviceId == null ? event.getServiceId() : this.serviceId);
/* 143 */       values.put("target_service_id", event.getTargetServiceId());
/* 144 */       values.put("comments", event.getComments());
/* 145 */       storeNonNullColumnValues(getConnection(), values, EventType.GENERIC.getTableName());
/*     */     }
/* 147 */     return eventId;
/*     */   }
/*     */ 
/*     */   private void storeClientEvent(int eventId, ClientEvent clientEvent)
/*     */     throws SQLException
/*     */   {
/* 157 */     Map values = new HashMap();
/* 158 */     values.put("event_id", Integer.valueOf(eventId));
/* 159 */     values.put("user_id", Integer.valueOf(clientEvent.getUserId()));
/* 160 */     values.put("client_time", clientEvent.getTimestamp());
/* 161 */     values.put("inbound", Boolean.valueOf(clientEvent.isInbound()));
/* 162 */     storeNonNullColumnValues(getConnection(), values, EventType.CLIENT.getTableName());
/*     */   }
/*     */ 
/*     */   private void storeLoginEvent(int eventId, LoginEvent loginEvent) throws SQLException {
/* 166 */     Map values = new HashMap();
/* 167 */     values.put("event_id", Integer.valueOf(eventId));
/* 168 */     values.put("ip", loginEvent.getIp());
/* 169 */     values.put("host", loginEvent.getHost());
/* 170 */     values.put("logon_ip", loginEvent.getLogonIp());
/* 171 */     values.put("os", loginEvent.getOperatingSystem());
/* 172 */     values.put("http_agent", loginEvent.getHttpAgent());
/* 173 */     values.put("client_type", loginEvent.getClientType());
/* 174 */     storeNonNullColumnValues(getConnection(), values, EventType.LOGIN.getTableName());
/*     */   }
/*     */ 
/*     */   private void storeNotificationEvent(int eventId, NotificationEvent notificationEvent)
/*     */     throws SQLException
/*     */   {
/* 185 */     Map values = new HashMap();
/* 186 */     values.put("event_id", Integer.valueOf(eventId));
/* 187 */     values.put("message_level", notificationEvent.getLevel());
/* 188 */     values.put("message", notificationEvent.getMessage());
/* 189 */     storeNonNullColumnValues(getConnection(), values, EventType.NOTIFICATION.getTableName());
/*     */   }
/*     */ 
/*     */   private void storeSubmitOrderEvent(int eventId, SubmitOrderEvent submitOrderEvent)
/*     */     throws SQLException
/*     */   {
/* 200 */     for (OrderMessage order : submitOrderEvent.getOrders()) {
/* 201 */       int orderSubmitionId = -1;
/*     */ 
/* 203 */       ResultSet rs = getConnection().createStatement().executeQuery("SELECT SQN_EVENTS_OS.nextval FROM dual");
/* 204 */       if (rs.next()) {
/* 205 */         orderSubmitionId = rs.getInt(1);
/*     */       }
/*     */ 
/* 209 */       if (orderSubmitionId > 0) {
/* 210 */         Map values = new HashMap();
/* 211 */         values.put("event_id", Integer.valueOf(eventId));
/* 212 */         values.put("id", Integer.valueOf(orderSubmitionId));
/* 213 */         values.put("root_order_id", order.getRootOrderId());
/* 214 */         values.put("order_id", order.getOrderId());
/* 215 */         values.put("parent_order_id", order.getParentOrderId());
/* 216 */         values.put("order_group_id", order.getOrderGroupId());
/* 217 */         values.put("order_state", order.getOrderState());
/* 218 */         values.put("direction", order.getOrderDirection());
/* 219 */         values.put("side", order.getSide());
/* 220 */         values.put("instrument", order.getInstrument());
/* 221 */         values.put("amount", Double.valueOf(order.getAmount().getValue().doubleValue()));
/* 222 */         values.put("is_place_offer", Boolean.valueOf(order.isPlaceOffer()));
/* 223 */         values.put("exec_timeout_millis", order.getExecTimeoutMillis());
/* 224 */         values.put("stop_direction", order.getStopDirection());
/* 225 */         values.put("price_stop", order.getPriceStop() == null ? null : Double.valueOf(order.getPriceStop().getValue().doubleValue()));
/* 226 */         values.put("price_trailing_limit", order.getPriceTrailingLimit() == null ? null : Double.valueOf(order.getPriceTrailingLimit().getValue().doubleValue()));
/* 227 */         storeNonNullColumnValues(getConnection(), values, EventType.SUBMIT_ORDER.getTableName());
/*     */       } else {
/* 229 */         throw new RuntimeException("No order sumbition id was retrieved");
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   private void storeOrderStateChangeEvent(int eventId, OrderStateChangeEvent stateChangeEvent)
/*     */     throws SQLException
/*     */   {
/* 242 */     Map values = new HashMap();
/* 243 */     values.put("event_id", Integer.valueOf(eventId));
/* 244 */     values.put("order_id", stateChangeEvent.getOrderId());
/* 245 */     values.put("order_state", stateChangeEvent.getOrderState());
/* 246 */     values.put("reason", stateChangeEvent.getReason());
/* 247 */     storeNonNullColumnValues(getConnection(), values, EventType.ORDER_STATE_CHANGE.getTableName());
/*     */   }
/*     */ 
/*     */   private void storeTradeEvent(int eventId, TradeEvent tradeEvent)
/*     */     throws SQLException
/*     */   {
/* 258 */     Map values = new HashMap();
/* 259 */     values.put("event_id", Integer.valueOf(eventId));
/* 260 */     values.put("account", tradeEvent.getAccount());
/* 261 */     values.put("order_id", tradeEvent.getOrderId());
/* 262 */     values.put("trade_id", tradeEvent.getTradeId());
/* 263 */     values.put("amount_pri", Double.valueOf(tradeEvent.getAmountPrimary().doubleValue()));
/* 264 */     values.put("price", Double.valueOf(tradeEvent.getPrice().doubleValue()));
/* 265 */     values.put("amount_sec", Double.valueOf(tradeEvent.getAmountSecondary().doubleValue()));
/* 266 */     storeNonNullColumnValues(getConnection(), values, EventType.TRADE.getTableName());
/*     */   }
/*     */ 
/*     */   private void storeChangeOrderConditionEvent(int eventId, ChangeOrderConditionEvent changeEvent)
/*     */     throws SQLException
/*     */   {
/* 277 */     Map values = new HashMap();
/* 278 */     values.put("event_id", Integer.valueOf(eventId));
/* 279 */     values.put("order_id", changeEvent.getOrderId());
/* 280 */     values.put("old_price_stop", changeEvent.getOldPriceStop());
/* 281 */     values.put("old_price_trailing_limit", changeEvent.getOldPriceTrailingLimit());
/* 282 */     values.put("old_exec_timeout_millis", changeEvent.getOldExecTimeoutMillis());
/* 283 */     values.put("old_stop_direction", changeEvent.getOldStopDirection());
/* 284 */     values.put("price_stop", changeEvent.getPriceStop());
/* 285 */     values.put("price_trailing_limit", changeEvent.getPriceTrailingLimit());
/* 286 */     values.put("exec_timeout_millis", changeEvent.getExecTimeoutMillis());
/* 287 */     values.put("stop_direction", changeEvent.getStopDirection());
/* 288 */     storeNonNullColumnValues(getConnection(), values, EventType.CHANGE_CONDITION.getTableName());
/*     */   }
/*     */ 
/*     */   private void storeOrderRelatedEvent(int eventId, OrderRelatedEvent orderRelatedEvent)
/*     */     throws SQLException
/*     */   {
/* 299 */     Map values = new HashMap();
/* 300 */     values.put("event_id", Integer.valueOf(eventId));
/* 301 */     values.put("order_id", orderRelatedEvent.getOrderId());
/* 302 */     storeNonNullColumnValues(getConnection(), values, EventType.ORDER_RELATED.getTableName());
/*     */   }
/*     */ 
/*     */   private boolean storeNonNullColumnValues(Connection connection, Map<String, Object> values, String table)
/*     */     throws SQLException
/*     */   {
/* 312 */     StringBuffer sb = new StringBuffer();
/* 313 */     StringBuffer sbNames = new StringBuffer();
/* 314 */     StringBuffer sbValues = new StringBuffer();
/*     */ 
/* 316 */     List listOfValues = new ArrayList();
/* 317 */     Iterator i = values.entrySet().iterator();
/* 318 */     while (i.hasNext()) {
/* 319 */       Map.Entry entry = (Map.Entry)i.next();
/* 320 */       if ((entry.getKey() != null) && (entry.getValue() != null)) {
/* 321 */         if (listOfValues.size() > 0) {
/* 322 */           sbNames.append(", ");
/* 323 */           sbValues.append(", ");
/*     */         }
/* 325 */         sbNames.append((String)entry.getKey());
/* 326 */         sbValues.append("?");
/* 327 */         listOfValues.add(entry.getValue());
/*     */       }
/*     */     }
/*     */ 
/* 331 */     sb.append("INSERT INTO ").append(table).append(" (").append(sbNames.toString()).append(") VALUES (").append(sbValues.toString()).append(")");
/* 332 */     PreparedStatement ps = connection.prepareStatement(sb.toString());
/*     */ 
/* 334 */     int pos = 0;
/* 335 */     for (Iterator i$ = listOfValues.iterator(); i$.hasNext(); ) { Object valueObject = i$.next();
/* 336 */       if ((valueObject instanceof Date))
/* 337 */         valueObject = new Timestamp(((Date)valueObject).getTime());
/* 338 */       else if ((valueObject instanceof Boolean))
/* 339 */         valueObject = Integer.valueOf(((Boolean)valueObject).booleanValue() == true ? 1 : 0);
/* 340 */       else if ((valueObject instanceof Enum))
/* 341 */         valueObject = valueObject.toString();
/* 342 */       else if ((valueObject instanceof BigDecimal))
/* 343 */         valueObject = Double.valueOf(((BigDecimal)valueObject).doubleValue());
/* 344 */       else if ((valueObject instanceof EventType)) {
/* 345 */         valueObject = valueObject.toString();
/*     */       }
/* 347 */       pos++; ps.setObject(pos, valueObject);
/*     */     }
/*     */ 
/* 350 */     return ps.execute();
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\dds2-common-2.3.77.jar
 * Qualified Name:     com.dukascopy.dds2.events.handler.EventDBWriterTask
 * JD-Core Version:    0.6.0
 */