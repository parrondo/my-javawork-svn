/*    */ package com.dukascopy.dds2.greed.api;
/*    */ 
/*    */ import com.dukascopy.dds2.greed.GreedContext;
/*    */ import com.dukascopy.dds2.greed.gui.ClientForm;
/*    */ import com.dukascopy.dds2.greed.gui.GuiUtilsAndConstants;
/*    */ import com.dukascopy.dds2.greed.gui.component.orders.OrderCommonTableModel;
/*    */ import com.dukascopy.dds2.greed.gui.component.orders.OrdersPanel;
/*    */ import com.dukascopy.dds2.greed.gui.component.orders.OrdersTable;
/*    */ import com.dukascopy.dds2.greed.gui.component.table.TableSorter;
/*    */ import com.dukascopy.dds2.greed.util.OrderMessageUtils;
/*    */ import com.dukascopy.transport.common.msg.group.OrderGroupMessage;
/*    */ import java.lang.reflect.InvocationTargetException;
/*    */ import java.text.ParseException;
/*    */ import java.util.ArrayList;
/*    */ import java.util.Collection;
/*    */ import java.util.Iterator;
/*    */ import java.util.LinkedList;
/*    */ import java.util.List;
/*    */ import javax.swing.SwingUtilities;
/*    */ import org.slf4j.Logger;
/*    */ import org.slf4j.LoggerFactory;
/*    */ 
/*    */ public class OrdersModel
/*    */ {
/* 26 */   private static final Logger LOGGER = LoggerFactory.getLogger(OrdersModel.class);
/*    */ 
/* 28 */   private static int MAX_LAST_OGM_COUNT = 20;
/* 29 */   private LinkedList<OrderGroupMessage> lastOrders = new LinkedList();
/*    */   private static OrdersModel ordersModel;
/*    */ 
/*    */   public static OrdersModel getInstance()
/*    */   {
/* 34 */     if (ordersModel == null) {
/* 35 */       ordersModel = new OrdersModel();
/*    */     }
/* 37 */     return ordersModel;
/*    */   }
/*    */ 
/*    */   public void updateLastOrders(OrderGroupMessage recentOgm) {
/* 41 */     if (this.lastOrders.size() >= MAX_LAST_OGM_COUNT) {
/* 42 */       this.lastOrders.removeFirst();
/*    */     }
/* 44 */     this.lastOrders.addLast(recentOgm);
/*    */   }
/*    */ 
/*    */   public List<OrderGroupMessage> getLastOrderGroupMessageList() {
/* 48 */     return this.lastOrders;
/*    */   }
/*    */ 
/*    */   public List<OrderGroupMessage> getOrderGroupsEdtLess()
/*    */   {
/* 57 */     List holder = new ArrayList();
/*    */     try {
/* 59 */       SwingUtilities.invokeAndWait(new Runnable(holder) {
/*    */         public void run() {
/* 61 */           this.val$holder.add(OrdersModel.this.getOrderGroups());
/*    */         } } );
/*    */     } catch (InterruptedException consumaro) {
/* 65 */       return null;
/*    */     } catch (InvocationTargetException e) {
/* 67 */       LOGGER.error(e.getMessage(), e);
/* 68 */       return null;
/*    */     }
/* 70 */     return (List)holder.get(0);
/*    */   }
/*    */ 
/*    */   public List<OrderGroupMessage> getOrderGroups()
/*    */   {
/* 76 */     GuiUtilsAndConstants.ensureEventDispatchThread();
/*    */ 
/* 78 */     List result = new ArrayList();
/*    */ 
/* 80 */     ClientForm clientForm = (ClientForm)GreedContext.get("clientGui");
/* 81 */     OrdersPanel ordersPanel = clientForm.getOrdersPanel();
/* 82 */     OrdersTable ordersTable = ordersPanel.getOrdersTable();
/* 83 */     TableSorter tableSorter = (TableSorter)ordersTable.getModel();
/* 84 */     OrderCommonTableModel orderTableModel = (OrderCommonTableModel)tableSorter.getTableModel();
/* 85 */     Collection allGroups = orderTableModel.getGroups();
/* 86 */     for (Iterator groupsIt = allGroups.iterator(); groupsIt.hasNext(); ) {
/* 87 */       OrderGroupMessage ogm = (OrderGroupMessage)groupsIt.next();
/*    */       try {
/* 89 */         result.add(OrderMessageUtils.copyOrderGroup(ogm));
/*    */       } catch (ParseException e) {
/* 91 */         LOGGER.error("Failed to parse orderGroup: " + ogm.getOrderGroupId());
/* 92 */         throw new RuntimeException(e);
/*    */       }
/*    */     }
/* 95 */     return result;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.api.OrdersModel
 * JD-Core Version:    0.6.0
 */