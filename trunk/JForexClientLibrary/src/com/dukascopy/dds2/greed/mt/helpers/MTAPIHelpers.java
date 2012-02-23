/*     */ package com.dukascopy.dds2.greed.mt.helpers;
/*     */ 
/*     */ import com.dukascopy.api.Instrument;
/*     */ import com.dukascopy.api.JFException;
/*     */ import com.dukascopy.charts.data.datacache.OrderHistoricalData;
/*     */ import com.dukascopy.charts.data.orders.OrdersProvider;
/*     */ import com.dukascopy.dds2.greed.GreedContext;
/*     */ import com.dukascopy.dds2.greed.gui.ClientForm;
/*     */ import com.dukascopy.dds2.greed.gui.component.orders.GlobalOrderTableModel;
/*     */ import com.dukascopy.dds2.greed.gui.component.orders.OrderCommonTableModel;
/*     */ import com.dukascopy.dds2.greed.gui.component.orders.OrderTableModel;
/*     */ import com.dukascopy.dds2.greed.gui.component.orders.OrdersPanel;
/*     */ import com.dukascopy.dds2.greed.gui.component.orders.OrdersTable;
/*     */ import com.dukascopy.dds2.greed.gui.component.positions.PositionsPanel;
/*     */ import com.dukascopy.dds2.greed.gui.component.positions.PositionsTable;
/*     */ import com.dukascopy.dds2.greed.gui.component.positions.PositionsTableModel;
/*     */ import com.dukascopy.dds2.greed.gui.component.table.TableSorter;
/*     */ import com.dukascopy.dds2.greed.mt.func.MOrderClose;
/*     */ import com.dukascopy.transport.common.model.type.Position;
/*     */ import com.dukascopy.transport.common.msg.group.OrderGroupMessage;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collection;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import javax.swing.SwingUtilities;
/*     */ import org.slf4j.Logger;
/*     */ import org.slf4j.LoggerFactory;
/*     */ 
/*     */ public class MTAPIHelpers
/*     */ {
/*  28 */   private static Logger log = LoggerFactory.getLogger(MOrderClose.class);
/*     */ 
/*     */   public static Instrument fromMTString(String instrumentAsString)
/*     */   {
/*  39 */     return Instrument.fromString(instrumentAsString.substring(0, 3) + Instrument.getPairsSeparator() + instrumentAsString.substring(3));
/*     */   }
/*     */ 
/*     */   public static String toMTString(Instrument instrument)
/*     */   {
/*  52 */     StringBuffer buff = new StringBuffer();
/*  53 */     buff.append(instrument.toString().substring(0, 3));
/*  54 */     buff.append(instrument.toString().substring(4, instrument.toString().length()));
/*     */ 
/*  57 */     return buff.toString();
/*     */   }
/*     */ 
/*     */   public static OrderHistoricalData getOrderGroupHistoricalData(String orderGroupId, Instrument instrument)
/*     */   {
/*  62 */     OrderHistoricalData returnValue = null;
/*     */ 
/*  64 */     OrdersProvider ordersProvider = (OrdersProvider)GreedContext.get("ordersDataProvider");
/*     */ 
/*  68 */     Collection openOrders = null;
/*     */ 
/*  70 */     openOrders = ordersProvider.getOrdersForInstrument(instrument).values();
/*  71 */     for (OrderHistoricalData historicalData : openOrders) {
/*  72 */       if (historicalData.getOrderGroupId().equals(orderGroupId)) {
/*  73 */         returnValue = historicalData;
/*  74 */         break;
/*     */       }
/*     */     }
/*  77 */     return returnValue;
/*     */   }
/*     */ 
/*     */   public static Collection<OrderHistoricalData> getOrderHistoricalDataById(String orderGroupId, Instrument instrument)
/*     */   {
/*  82 */     List returnValue = new ArrayList();
/*     */ 
/*  84 */     OrdersProvider ordersProvider = (OrdersProvider)GreedContext.get("ordersDataProvider");
/*     */ 
/*  88 */     Collection openOrders = null;
/*     */ 
/*  90 */     openOrders = ordersProvider.getOrdersForInstrument(instrument).values();
/*  91 */     for (OrderHistoricalData historicalData : openOrders) {
/*  92 */       if (historicalData.getOrderGroupId().equals(orderGroupId)) {
/*  93 */         returnValue.add(historicalData);
/*     */       }
/*     */     }
/*  96 */     return returnValue;
/*     */   }
/*     */ 
/*     */   public static Collection<OrderHistoricalData> getAllHistoricalData() {
/* 100 */     OrdersProvider ordersProvider = (OrdersProvider)GreedContext.get("ordersDataProvider");
/*     */ 
/* 102 */     Collection returnValue = ordersProvider.getAllOrders();
/*     */ 
/* 106 */     return returnValue;
/*     */   }
/*     */ 
/*     */   public static OrderGroupMessage[] getOrderGroupById(long index)
/*     */     throws JFException
/*     */   {
/* 141 */     return getOrderGroupById(String.valueOf(index));
/*     */   }
/*     */ 
/*     */   public static OrderGroupMessage[] getOrderGroupById(int index) throws JFException {
/* 145 */     return getOrderGroupById(String.valueOf(index));
/*     */   }
/*     */ 
/*     */   public static Position getPosutionById(int index) throws JFException {
/* 149 */     return SelectPosutionById(String.valueOf(index));
/*     */   }
/*     */ 
/*     */   public static synchronized Position SelectPosutionById(String index) throws JFException
/*     */   {
/* 154 */     Position group = null;
/* 155 */     ClientForm clientForm = (ClientForm)GreedContext.get("clientGui");
/*     */     try {
/* 157 */       SwingUtilities.invokeAndWait(new Runnable(clientForm, index) {
/*     */         public void run() {
/* 159 */           PositionsTableModel model = (PositionsTableModel)this.val$clientForm.getPositionsPanel().getTable().getModel();
/*     */ 
/* 161 */           model.getPosition(this.val$index);
/*     */         } } );
/*     */     } catch (Exception e) {
/* 165 */       throw new JFException(e.getMessage());
/*     */     }
/* 167 */     return group;
/*     */   }
/*     */ 
/*     */   public static synchronized Position SelectPosutionByRow(int index) throws JFException
/*     */   {
/* 172 */     Position group = null;
/* 173 */     ClientForm clientForm = (ClientForm)GreedContext.get("clientGui");
/*     */     try
/*     */     {
/* 176 */       SwingUtilities.invokeAndWait(new Runnable(clientForm, index) {
/*     */         public void run() {
/* 178 */           PositionsTableModel model = (PositionsTableModel)this.val$clientForm.getPositionsPanel().getTable().getModel();
/*     */ 
/* 180 */           model.getPosition(this.val$index);
/*     */         } } );
/*     */     } catch (Exception e) {
/* 184 */       throw new JFException(e.getMessage());
/*     */     }
/* 186 */     return group;
/*     */   }
/*     */ 
/*     */   public static synchronized OrderGroupMessage[] getOrderGroupById(String index) throws JFException {
/* 190 */     OrderGroupMessage[] group = { null };
/* 191 */     ClientForm clientForm = (ClientForm)GreedContext.get("clientGui");
/*     */     try
/*     */     {
/* 194 */       SwingUtilities.invokeAndWait(new Runnable(group, clientForm, index) {
/*     */         public void run() {
/* 196 */           this.val$group[0] = this.val$clientForm.getOrdersPanel().getOrderGroup(this.val$index);
/*     */         } } );
/*     */     } catch (Exception e) {
/* 200 */       throw new JFException(e.getMessage());
/*     */     }
/* 202 */     return group;
/*     */   }
/*     */ 
/*     */   public static synchronized OrderGroupMessage[] getOrderGroupByRow(int index, int pool)
/*     */   {
/* 207 */     OrderGroupMessage[] group = { null };
/* 208 */     List list = null;
/*     */ 
/* 210 */     if (pool <= 0)
/*     */     {
/* 215 */       list = getOrderMessageList();
/* 216 */       if ((index > -1) && (list.size() > 0) && (list.size() > index)) {
/* 217 */         group[0] = ((OrderGroupMessage)list.get(index));
/*     */       }
/*     */     }
/*     */ 
/* 221 */     return group;
/*     */   }
/*     */ 
/*     */   public static synchronized OrderGroupMessage[] getOrderGroupByRow(int groupIndex) {
/* 225 */     OrderGroupMessage[] group = { null };
/*     */ 
/* 227 */     ClientForm clientForm = (ClientForm)GreedContext.get("clientGui");
/*     */     try
/*     */     {
/* 230 */       SwingUtilities.invokeAndWait(new Runnable(clientForm, group, groupIndex)
/*     */       {
/*     */         public void run()
/*     */         {
/* 237 */           OrderCommonTableModel orderModel = null;
/*     */           try {
/* 239 */             orderModel = (OrderCommonTableModel)((TableSorter)this.val$clientForm.getOrdersPanel().getOrdersTable().getModel()).getTableModel();
/*     */ 
/* 242 */             if ((orderModel instanceof OrderTableModel)) {
/* 243 */               this.val$group[0] = ((OrderTableModel)orderModel).getGroup(this.val$groupIndex);
/*     */             }
/*     */ 
/* 246 */             if ((orderModel instanceof GlobalOrderTableModel))
/* 247 */               this.val$group[0] = ((GlobalOrderTableModel)orderModel).getGroup(this.val$groupIndex);
/*     */           }
/*     */           catch (Exception ex)
/*     */           {
/* 251 */             ex.printStackTrace();
/*     */           }
/* 253 */           this.val$group[0] = orderModel.getGroup(this.val$groupIndex);
/*     */         }
/*     */       });
/*     */     }
/*     */     catch (Exception e) {
/* 259 */       log.error(e.getMessage(), e);
/*     */     }
/* 261 */     return group;
/*     */   }
/*     */ 
/*     */   public static synchronized List<OrderGroupMessage> getOrderMessageList() {
/* 265 */     List rc = new ArrayList();
/* 266 */     ClientForm clientForm = (ClientForm)GreedContext.get("clientGui");
/*     */     try
/*     */     {
/* 269 */       SwingUtilities.invokeAndWait(new Runnable(clientForm, rc) {
/*     */         public void run() {
/*     */           try {
/* 272 */             OrdersPanel ordersPanel = this.val$clientForm.getOrdersPanel();
/* 273 */             OrdersTable ordersTable = ordersPanel.getOrdersTable();
/* 274 */             TableSorter tableSorter = (TableSorter)ordersTable.getModel();
/*     */ 
/* 276 */             OrderCommonTableModel orderTableModel = (OrderCommonTableModel)tableSorter.getTableModel();
/*     */ 
/* 281 */             this.val$rc.addAll(orderTableModel.getGroups());
/* 282 */             List groupsToRemove = new ArrayList();
/* 283 */             for (OrderGroupMessage message : this.val$rc) {
/* 284 */               if (message.getOpeningOrder() == null) {
/* 285 */                 groupsToRemove.add(message);
/* 286 */                 this.val$rc.remove(message);
/*     */               }
/*     */             }
/* 289 */             this.val$rc.removeAll(groupsToRemove);
/*     */           } catch (Throwable e) {
/* 291 */             MTAPIHelpers.log.error(e.getMessage(), e);
/*     */           }
/*     */         } } );
/*     */     } catch (Exception e) {
/* 296 */       log.error(e.getMessage(), e);
/*     */     }
/* 298 */     return rc;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.mt.helpers.MTAPIHelpers
 * JD-Core Version:    0.6.0
 */