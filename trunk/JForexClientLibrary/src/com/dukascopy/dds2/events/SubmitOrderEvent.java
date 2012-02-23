/*    */ package com.dukascopy.dds2.events;
/*    */ 
/*    */ import com.dukascopy.transport.common.msg.group.OrderGroupMessage;
/*    */ import com.dukascopy.transport.common.msg.group.OrderMessage;
/*    */ import java.util.ArrayList;
/*    */ import java.util.Collection;
/*    */ import java.util.Collections;
/*    */ import java.util.HashMap;
/*    */ import java.util.List;
/*    */ import java.util.Map;
/*    */ 
/*    */ public class SubmitOrderEvent extends Event
/*    */ {
/*    */   public static final String TYPE = "requestOrderEvent";
/*    */   public static final String ORDERS = "orders";
/*    */   public static final String ID = "id";
/*    */   public static final String EVENT_ID = "event_id";
/*    */   public static final String ROOT_ORDER_ID = "root_order_id";
/*    */   public static final String ORDER_ID = "order_id";
/*    */   public static final String PARENT_ORDER_ID = "parent_order_id";
/*    */   public static final String ORDER_GROUP_ID = "order_group_id";
/*    */   public static final String ORDER_STATE = "order_state";
/*    */   public static final String DIRECTION = "direction";
/*    */   public static final String SIDE = "side";
/*    */   public static final String INSTRUMENT = "instrument";
/*    */   public static final String AMOUNT = "amount";
/*    */   public static final String IS_PLACE_OFFER = "is_place_offer";
/*    */   public static final String EXEC_TIMEOUT_MILLIS = "exec_timeout_millis";
/*    */   public static final String STOP_DIRECTION = "stop_direction";
/*    */   public static final String PRICE_STOP = "price_stop";
/*    */   public static final String PRICE_TRAILING_LIMIT = "price_trailing_limit";
/*    */   public static final String CLIENT_BID = "client_bid";
/*    */   public static final String CLIENT_ASK = "client_ask";
/* 55 */   private List<OrderMessage> orders = new ArrayList();
/*    */ 
/*    */   protected SubmitOrderEvent()
/*    */   {
/*    */   }
/*    */ 
/*    */   public SubmitOrderEvent(OrderGroupMessage orderGroup) {
/* 62 */     this();
/* 63 */     setOrders(orderGroup.getOrders());
/*    */   }
/*    */ 
/*    */   public SubmitOrderEvent(OrderMessage order) {
/* 67 */     this();
/* 68 */     this.orders.add(order);
/*    */   }
/*    */ 
/*    */   public void setOrders(Collection<OrderMessage> orders) {
/* 72 */     this.orders.clear();
/* 73 */     this.orders.addAll(orders);
/*    */   }
/*    */ 
/*    */   public List<OrderMessage> getOrders() {
/* 77 */     return Collections.unmodifiableList(this.orders);
/*    */   }
/*    */ 
/*    */   public Map<String, Object> getAttributes()
/*    */   {
/* 82 */     Map attributes = new HashMap();
/* 83 */     attributes.put("event_id", Long.valueOf(super.getId()));
/* 84 */     attributes.put("orders", this.orders);
/* 85 */     return Collections.unmodifiableMap(attributes);
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\dds2-common-2.3.77.jar
 * Qualified Name:     com.dukascopy.dds2.events.SubmitOrderEvent
 * JD-Core Version:    0.6.0
 */