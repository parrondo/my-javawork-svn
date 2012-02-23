/*     */ package com.dukascopy.dds2.greed.gui.component.orders;
/*     */ 
/*     */ import com.dukascopy.dds2.greed.GreedContext;
/*     */ import com.dukascopy.dds2.greed.gui.GuiUtilsAndConstants;
/*     */ import com.dukascopy.transport.common.model.type.Money;
/*     */ import com.dukascopy.transport.common.model.type.OrderSide;
/*     */ import com.dukascopy.transport.common.model.type.StopDirection;
/*     */ import com.dukascopy.transport.common.msg.group.OrderGroupMessage;
/*     */ import com.dukascopy.transport.common.msg.group.OrderMessage;
/*     */ import java.lang.reflect.InvocationTargetException;
/*     */ import java.math.BigDecimal;
/*     */ import java.text.SimpleDateFormat;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collection;
/*     */ import java.util.Comparator;
/*     */ import java.util.HashSet;
/*     */ import java.util.List;
/*     */ import java.util.ListIterator;
/*     */ import java.util.Set;
/*     */ import java.util.TimeZone;
/*     */ import java.util.TreeSet;
/*     */ import javax.swing.SwingUtilities;
/*     */ import javax.swing.table.AbstractTableModel;
/*     */ import org.slf4j.Logger;
/*     */ import org.slf4j.LoggerFactory;
/*     */ 
/*     */ public abstract class OrderCommonTableModel extends AbstractTableModel
/*     */ {
/*  29 */   private static final Logger LOGGER = LoggerFactory.getLogger(OrderCommonTableModel.class);
/*     */   private static final long serialVersionUID = -5466169557892249051L;
/*  33 */   protected static boolean isJForexRunning = GreedContext.isStrategyAllowed();
/*  34 */   protected static int TOTAL_COLUMNS = isJForexRunning ? 13 : 12;
/*     */ 
/*  36 */   public static int COLUMN_CHECK = 0;
/*  37 */   public static int COLUMN_TIMESTAMP = 1;
/*  38 */   public static int COLUMN_POSITION = isJForexRunning ? 3 : 2;
/*  39 */   public static int COLUMN_ID = isJForexRunning ? 4 : 3;
/*  40 */   public static int COLUMN_INSTRUMENT = isJForexRunning ? 5 : 4;
/*  41 */   public static int COLUMN_SIDE = isJForexRunning ? 6 : 5;
/*  42 */   public static int COLUMN_REQ_AMOUNT = isJForexRunning ? 7 : 6;
/*  43 */   public static int COLUMN_TYPE = isJForexRunning ? 8 : 7;
/*  44 */   public static int COLUMN_PRICE = isJForexRunning ? 9 : 8;
/*  45 */   public static int COLUMN_PROPS = isJForexRunning ? 10 : 9;
/*  46 */   public static int COLUMN_STATE = isJForexRunning ? 11 : 10;
/*  47 */   public static int COLUMN_EXP = isJForexRunning ? 12 : 11;
/*     */   public static final int EXT_ID = 2;
/*  51 */   protected final Set<String> closedGroups = new HashSet();
/*  52 */   protected final List<OrderMessage> cachedPendingOrders = new ArrayList();
/*  53 */   protected final Set<OrderMessage> selectedOrders = new TreeSet(new Comparator() {
/*     */     public int compare(OrderMessage o1, OrderMessage o2) {
/*  55 */       return o1.getOrderId().compareTo(o2.getOrderId());
/*     */     }
/*     */   });
/*     */ 
/*  59 */   protected final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
/*     */ 
/*     */   public abstract OrderGroupMessage getGroup(int paramInt);
/*     */ 
/*     */   public abstract OrderGroupMessage getGroup(String paramString);
/*     */ 
/*     */   public abstract OrderGroupMessage getGroupByOrderId(String paramString);
/*     */ 
/*     */   public abstract void updateTable(OrderGroupMessage paramOrderGroupMessage);
/*     */ 
/*     */   public abstract void updateTable(OrderMessage paramOrderMessage);
/*     */ 
/*     */   public abstract Collection<OrderGroupMessage> getGroups();
/*     */ 
/*     */   public abstract List<OrderMessage> getPendingOrders();
/*     */ 
/*  73 */   public OrderCommonTableModel() { this.dateFormat.setTimeZone(TimeZone.getTimeZone("GMT")); }
/*     */ 
/*     */   public static void reinitColumnIndexs()
/*     */   {
/*  77 */     isJForexRunning = GreedContext.isStrategyAllowed();
/*  78 */     TOTAL_COLUMNS = isJForexRunning ? 13 : 12;
/*     */ 
/*  80 */     COLUMN_CHECK = 0;
/*  81 */     COLUMN_TIMESTAMP = 1;
/*  82 */     COLUMN_POSITION = isJForexRunning ? 3 : 2;
/*  83 */     COLUMN_ID = isJForexRunning ? 4 : 3;
/*  84 */     COLUMN_INSTRUMENT = isJForexRunning ? 5 : 4;
/*  85 */     COLUMN_SIDE = isJForexRunning ? 6 : 5;
/*  86 */     COLUMN_REQ_AMOUNT = isJForexRunning ? 7 : 6;
/*  87 */     COLUMN_TYPE = isJForexRunning ? 8 : 7;
/*  88 */     COLUMN_PRICE = isJForexRunning ? 9 : 8;
/*  89 */     COLUMN_PROPS = isJForexRunning ? 10 : 9;
/*  90 */     COLUMN_STATE = isJForexRunning ? 11 : 10;
/*  91 */     COLUMN_EXP = isJForexRunning ? 12 : 11;
/*     */   }
/*     */ 
/*     */   public int getColumnCount()
/*     */   {
/*  96 */     return TOTAL_COLUMNS;
/*     */   }
/*     */ 
/*     */   public Set<String> getClosedPositionIdSet() {
/* 100 */     return this.closedGroups;
/*     */   }
/*     */ 
/*     */   public Set<OrderMessage> getSelectedOrders()
/*     */   {
/* 108 */     return this.selectedOrders;
/*     */   }
/*     */ 
/*     */   public Set<String> getClosedGroups() throws InterruptedException, InvocationTargetException {
/* 112 */     Set closedGroupIdSet = new HashSet();
/* 113 */     SwingUtilities.invokeAndWait(new Runnable(closedGroupIdSet) {
/*     */       public void run() {
/* 115 */         this.val$closedGroupIdSet.addAll(OrderCommonTableModel.this.closedGroups);
/*     */       }
/*     */     });
/* 118 */     return closedGroupIdSet;
/*     */   }
/*     */ 
/*     */   public Class<?> getColumnClass(int columnIndex)
/*     */   {
/* 128 */     Class aClass = super.getColumnClass(columnIndex);
/* 129 */     if (columnIndex == COLUMN_CHECK) {
/* 130 */       return OrderMessage.class;
/*     */     }
/* 132 */     return aClass;
/*     */   }
/*     */ 
/*     */   public boolean isCellEditable(int rowIndex, int columnIndex)
/*     */   {
/* 144 */     OrderMessage order = getOrder(rowIndex);
/* 145 */     if (order.isDisabled()) {
/* 146 */       return false;
/*     */     }
/* 148 */     return columnIndex == COLUMN_CHECK;
/*     */   }
/*     */ 
/*     */   public int getRowCount() {
/* 152 */     return getPendingOrders().size();
/*     */   }
/*     */ 
/*     */   public void setValueAt(Object aValue, int rowIndex, int columnIndex)
/*     */   {
/* 162 */     if (COLUMN_CHECK == columnIndex) {
/* 163 */       Boolean checked = (Boolean)aValue;
/* 164 */       OrderMessage order = getOrder(rowIndex);
/* 165 */       if (order != null) {
/* 166 */         if (order.isDisabled()) {
/* 167 */           return;
/*     */         }
/* 169 */         if (checked.booleanValue()) {
/* 170 */           order.setSelected(true);
/* 171 */           this.selectedOrders.add(order);
/*     */         } else {
/* 173 */           order.setSelected(false);
/* 174 */           this.selectedOrders.remove(order);
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public boolean orderExists(String orderId)
/*     */   {
/* 185 */     List pending = getPendingOrders();
/* 186 */     boolean result = false;
/*     */ 
/* 188 */     for (OrderMessage om : pending) {
/* 189 */       if (om.getOrderId().equals(orderId)) {
/* 190 */         result = true;
/* 191 */         break;
/*     */       }
/*     */     }
/* 194 */     return result;
/*     */   }
/*     */ 
/*     */   public int[] getGroupRows(String highlightedGroupId, List<OrderMessage> orderList) {
/* 198 */     int[] result = new int[2];
/* 199 */     result[0] = -1;
/* 200 */     result[1] = -1;
/* 201 */     for (ListIterator iterator = orderList.listIterator(); iterator.hasNext(); ) {
/* 202 */       int nextIndex = iterator.nextIndex();
/* 203 */       OrderMessage orderMessage = (OrderMessage)iterator.next();
/* 204 */       if (orderMessage.getOrderGroupId().equals(highlightedGroupId)) {
/* 205 */         if (result[1] == -1) {
/* 206 */           result[0] = nextIndex;
/* 207 */           result[1] = nextIndex;
/*     */         } else {
/* 209 */           result[1] = nextIndex;
/*     */         }
/*     */       }
/*     */     }
/* 213 */     return result;
/*     */   }
/*     */ 
/*     */   public Set<Integer> getGroupRowsAsSet(String highlightedGroupId, List<OrderMessage> orderList) {
/* 217 */     Set result = new HashSet();
/* 218 */     for (ListIterator iterator = orderList.listIterator(); iterator.hasNext(); ) {
/* 219 */       int nextIndex = iterator.nextIndex();
/* 220 */       OrderMessage orderMessage = (OrderMessage)iterator.next();
/* 221 */       if (orderMessage.getOrderGroupId().equals(highlightedGroupId)) {
/* 222 */         result.add(Integer.valueOf(nextIndex));
/*     */       }
/*     */     }
/* 225 */     return result;
/*     */   }
/*     */ 
/*     */   protected String constructTypeString(OrderMessage order)
/*     */   {
/* 232 */     if (null != order.getPriceLimit())
/*     */     {
/* 235 */       return "Trail. STOP";
/*     */     }
/*     */ 
/* 238 */     if (order.isPlaceOffer()) return "LIMIT";
/*     */ 
/* 240 */     String PREFIX = null == order.getStopDirection() ? "TRADE" : "STOP";
/*     */ 
/* 242 */     if (null == order.getPriceTrailingLimit())
/* 243 */       return order.isTakeProfit() ? "LIMIT" : PREFIX;
/* 244 */     if ((order.getPriceStop() != null) && (((StopDirection.ASK_LESS.equals(order.getStopDirection())) && (OrderSide.BUY.equals(order.getSide()))) || ((StopDirection.BID_GREATER.equals(order.getStopDirection())) && (OrderSide.SELL.equals(order.getSide())) && (order.getPriceTrailingLimit().getValue().compareTo(BigDecimal.ZERO) > 0))))
/*     */     {
/* 249 */       return "MIT";
/* 250 */     }if (order.getPriceTrailingLimit().getValue().compareTo(BigDecimal.ZERO) == 0) {
/* 251 */       if (((StopDirection.ASK_LESS.equals(order.getStopDirection())) && (OrderSide.BUY.equals(order.getSide()))) || ((StopDirection.BID_GREATER.equals(order.getStopDirection())) && (OrderSide.SELL.equals(order.getSide()))))
/*     */       {
/* 254 */         return "LIMIT";
/*     */       }
/* 256 */       return PREFIX + " (LIMIT)";
/*     */     }
/*     */ 
/* 259 */     return PREFIX + " (LIMIT)";
/*     */   }
/*     */ 
/*     */   public OrderMessage getOrder(int row)
/*     */   {
/* 264 */     GuiUtilsAndConstants.ensureEventDispatchThread();
/* 265 */     if (row < 0) {
/* 266 */       return null;
/*     */     }
/* 268 */     return (OrderMessage)getPendingOrders().get(row);
/*     */   }
/*     */ 
/*     */   public void clear() {
/* 272 */     this.selectedOrders.clear();
/* 273 */     this.cachedPendingOrders.clear();
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.orders.OrderCommonTableModel
 * JD-Core Version:    0.6.0
 */