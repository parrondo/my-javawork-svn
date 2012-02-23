/*    */ package com.dukascopy.dds2.greed.mt.func;
/*    */ 
/*    */ import com.dukascopy.dds2.greed.GreedContext;
/*    */ import com.dukascopy.dds2.greed.gui.ClientForm;
/*    */ import com.dukascopy.dds2.greed.gui.component.orders.OrderCommonTableModel;
/*    */ import com.dukascopy.dds2.greed.gui.component.orders.OrdersPanel;
/*    */ import com.dukascopy.dds2.greed.gui.component.orders.OrdersTable;
/*    */ import com.dukascopy.dds2.greed.gui.component.table.TableSorter;
/*    */ import com.dukascopy.dds2.greed.mt.common.AgentBase.CommonExecution;
/*    */ import com.dukascopy.dds2.greed.mt.exceptions.MTAgentException;
/*    */ import com.dukascopy.transport.common.msg.group.OrderGroupMessage;
/*    */ import java.util.ArrayList;
/*    */ import java.util.List;
/*    */ import javax.swing.SwingUtilities;
/*    */ import org.slf4j.Logger;
/*    */ import org.slf4j.LoggerFactory;
/*    */ 
/*    */ public class MOrdersTotal extends AgentBase.CommonExecution
/*    */ {
/* 23 */   private static Logger log = LoggerFactory.getLogger(MOrdersTotal.class);
/*    */ 
/*    */   public int execute(int id) throws MTAgentException {
/* 26 */     int returnValue = 0;
/*    */ 
/* 28 */     List rc = new ArrayList();
/* 29 */     ClientForm clientForm = (ClientForm)GreedContext.get("clientGui");
/*    */     try
/*    */     {
/* 32 */       SwingUtilities.invokeAndWait(new Runnable(clientForm, rc) {
/*    */         public void run() {
/*    */           try {
/* 35 */             OrdersPanel ordersPanel = this.val$clientForm.getOrdersPanel();
/* 36 */             OrdersTable ordersTable = ordersPanel.getOrdersTable();
/* 37 */             TableSorter tableSorter = (TableSorter)ordersTable.getModel();
/*    */ 
/* 39 */             OrderCommonTableModel orderTableModel = (OrderCommonTableModel)tableSorter.getTableModel();
/*    */ 
/* 44 */             this.val$rc.addAll(orderTableModel.getGroups());
/* 45 */             List groupsToRemove = new ArrayList();
/* 46 */             for (OrderGroupMessage message : this.val$rc) {
/* 47 */               if (message.getOpeningOrder() == null) {
/* 48 */                 groupsToRemove.add(message);
/* 49 */                 this.val$rc.remove(message);
/*    */               }
/*    */             }
/* 52 */             this.val$rc.removeAll(groupsToRemove);
/*    */           } catch (Throwable e) {
/* 54 */             MOrdersTotal.log.error(e.getMessage(), e);
/*    */           }
/*    */         } } );
/*    */     } catch (Exception e) {
/* 59 */       log.error(e.getMessage(), e);
/*    */     }
/* 61 */     returnValue = rc.size();
/* 62 */     setError(id, 0, "ERR_NO_ERROR_MSG");
/* 63 */     return returnValue;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.mt.func.MOrdersTotal
 * JD-Core Version:    0.6.0
 */