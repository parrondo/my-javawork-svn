/*    */ package com.dukascopy.dds2.greed.agent.strategy.tester;
/*    */ 
/*    */ import com.dukascopy.api.IOrder;
/*    */ import com.dukascopy.api.ITick;
/*    */ import java.util.ArrayList;
/*    */ import java.util.List;
/*    */ 
/*    */ public class InstrumentReportData
/*    */ {
/*    */   public ITick firstTick;
/*    */   public ITick lastTick;
/*    */   public int positionsTotal;
/*    */   public int ordersTotal;
/* 21 */   public List<IOrder> openedOrders = new ArrayList();
/* 22 */   public List<IOrder> closedOrders = new ArrayList();
/*    */   public double turnover;
/*    */   public double commission;
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.dds2.greed.agent.strategy.tester.InstrumentReportData
 * JD-Core Version:    0.6.0
 */