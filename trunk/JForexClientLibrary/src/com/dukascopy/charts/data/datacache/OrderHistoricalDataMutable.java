/*     */ package com.dukascopy.charts.data.datacache;
/*     */ 
/*     */ import com.dukascopy.api.IEngine.OrderCommand;
/*     */ import java.math.BigDecimal;
/*     */ import java.util.ArrayList;
/*     */ import java.util.LinkedHashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Map.Entry;
/*     */ 
/*     */ public class OrderHistoricalDataMutable extends OrderHistoricalData
/*     */ {
/*     */   public OrderHistoricalDataMutable()
/*     */   {
/*     */   }
/*     */ 
/*     */   public OrderHistoricalDataMutable(OrderHistoricalData orderHistoricalData)
/*     */   {
/* 179 */     super(orderHistoricalData);
/*     */   }
/*     */ 
/*     */   public void setMergedToGroupId(String mergedToGroupId)
/*     */   {
/* 184 */     super.setMergedToGroupId(mergedToGroupId);
/*     */   }
/*     */ 
/*     */   public void setMergedToTime(long mergedToTime)
/*     */   {
/* 189 */     super.setMergedToTime(mergedToTime);
/*     */   }
/*     */ 
/*     */   public void setOrderGroupId(String orderGroupId)
/*     */   {
/* 194 */     super.setOrderGroupId(orderGroupId);
/*     */   }
/*     */ 
/*     */   public void setCloseDataMap(Map<String, OrderHistoricalData.CloseData> closeDataMap)
/*     */   {
/* 199 */     LinkedHashMap closeDatas = new LinkedHashMap(closeDataMap.size());
/* 200 */     for (Map.Entry entry : closeDataMap.entrySet()) {
/* 201 */       OrderHistoricalData.CloseData closeData = (OrderHistoricalData.CloseData)entry.getValue();
/* 202 */       if (closeData.getClass().equals(CloseData.class))
/* 203 */         closeDatas.put(entry.getKey(), (CloseData)closeData);
/*     */       else {
/* 205 */         closeDatas.put(entry.getKey(), new CloseData(closeData));
/*     */       }
/*     */     }
/*     */ 
/* 209 */     this.closeDataMap = closeDatas;
/*     */   }
/*     */ 
/*     */   public void setEntryOrder(OrderHistoricalData.OpenData entryOrder)
/*     */   {
/* 214 */     if ((entryOrder == null) || ((entryOrder instanceof OpenData)))
/* 215 */       this.entryOrder = entryOrder;
/*     */     else
/* 217 */       this.entryOrder = new OpenData(entryOrder);
/*     */   }
/*     */ 
/*     */   public void setPendingOrders(List<OrderHistoricalData.OpenData> pendingOrders)
/*     */   {
/* 223 */     ArrayList orders = new ArrayList(pendingOrders.size());
/* 224 */     for (OrderHistoricalData.OpenData openData : pendingOrders) {
/* 225 */       if (openData.getClass().equals(OpenData.class))
/* 226 */         orders.add((OpenData)openData);
/*     */       else {
/* 228 */         orders.add(new OpenData(openData));
/*     */       }
/*     */     }
/*     */ 
/* 232 */     this.pendingOrders = orders;
/*     */   }
/*     */ 
/*     */   public void setOpened(boolean opened)
/*     */   {
/* 237 */     super.setOpened(opened);
/*     */   }
/*     */ 
/*     */   public void setClosed(boolean closed)
/*     */   {
/* 242 */     super.setClosed(closed);
/*     */   }
/*     */ 
/*     */   public void setOco(boolean oco)
/*     */   {
/* 247 */     super.setOco(oco);
/*     */   }
/*     */ 
/*     */   public void setHistoryStart(long historyStart)
/*     */   {
/* 252 */     super.setHistoryStart(historyStart);
/*     */   }
/*     */ 
/*     */   public void setHistoryEnd(long historyEnd)
/*     */   {
/* 257 */     super.setHistoryEnd(historyEnd);
/*     */   }
/*     */ 
/*     */   public OpenData getEntryOrder()
/*     */   {
/* 262 */     return (OpenData)super.getEntryOrder();
/*     */   }
/*     */ 
/*     */   public void putCloseData(String closeDataId, CloseData closeData) {
/* 266 */     this.closeDataMap.put(closeDataId, closeData);
/*     */   }
/*     */ 
/*     */   public CloseData removeCloseData(String closeDataId) {
/* 270 */     return (CloseData)this.closeDataMap.remove(closeDataId);
/*     */   }
/*     */ 
/*     */   public void addPendingOrder(OpenData openData) {
/* 274 */     this.pendingOrders.add(openData);
/*     */   }
/*     */ 
/*     */   public OpenData removePendingOrder(int index) {
/* 278 */     return (OpenData)this.pendingOrders.remove(index);
/*     */   }
/*     */ 
/*     */   public void clearPendingOrders() {
/* 282 */     this.pendingOrders.clear();
/*     */   }
/*     */ 
/*     */   public void setCommission(BigDecimal commission) {
/* 286 */     super.setCommission(commission);
/*     */   }
/*     */ 
/*     */   public static class OpenData extends OrderHistoricalData.OpenData
/*     */   {
/*     */     private boolean rollovered;
/*     */     private transient boolean fromMerges;
/*     */ 
/*     */     public OpenData()
/*     */     {
/*     */     }
/*     */ 
/*     */     public OpenData(OrderHistoricalData.OpenData openData)
/*     */     {
/*  55 */       super();
/*     */     }
/*     */ 
/*     */     public boolean isRollovered() {
/*  59 */       return this.rollovered;
/*     */     }
/*     */ 
/*     */     public void setRollovered(boolean rollovered) {
/*  63 */       this.rollovered = rollovered;
/*     */     }
/*     */ 
/*     */     public boolean isFromMerges() {
/*  67 */       return this.fromMerges;
/*     */     }
/*     */ 
/*     */     public void setFromMerges(boolean fromMerges) {
/*  71 */       this.fromMerges = fromMerges;
/*     */     }
/*     */ 
/*     */     public void setOrderId(String orderId)
/*     */     {
/*  76 */       super.setOrderId(orderId);
/*     */     }
/*     */ 
/*     */     public void setOpenPrice(BigDecimal openPrice)
/*     */     {
/*  81 */       super.setOpenPrice(openPrice);
/*     */     }
/*     */ 
/*     */     public void setCreationTime(long creationTime)
/*     */     {
/*  86 */       super.setCreationTime(creationTime);
/*     */     }
/*     */ 
/*     */     public void setFillTime(long fillTime)
/*     */     {
/*  91 */       super.setFillTime(fillTime);
/*     */     }
/*     */ 
/*     */     public void setOpenSlippage(BigDecimal openSlippage)
/*     */     {
/*  96 */       super.setOpenSlippage(openSlippage);
/*     */     }
/*     */ 
/*     */     public void setSide(IEngine.OrderCommand side)
/*     */     {
/* 101 */       super.setSide(side);
/*     */     }
/*     */ 
/*     */     public void setStopLossPrice(BigDecimal stopLossPrice)
/*     */     {
/* 106 */       super.setStopLossPrice(stopLossPrice);
/*     */     }
/*     */ 
/*     */     public void setStopLossSlippage(BigDecimal stopLossSlippage)
/*     */     {
/* 111 */       super.setStopLossSlippage(stopLossSlippage);
/*     */     }
/*     */ 
/*     */     public void setStopLossOrderId(String stopLossOrderId)
/*     */     {
/* 116 */       super.setStopLossOrderId(stopLossOrderId);
/*     */     }
/*     */ 
/*     */     public void setStopLossByBid(boolean stopLossByBid)
/*     */     {
/* 121 */       super.setStopLossByBid(stopLossByBid);
/*     */     }
/*     */ 
/*     */     public void setTrailingStep(BigDecimal trailingStep)
/*     */     {
/* 126 */       super.setTrailingStep(trailingStep);
/*     */     }
/*     */ 
/*     */     public void setTakeProfitPrice(BigDecimal takeProfitPrice)
/*     */     {
/* 131 */       super.setTakeProfitPrice(takeProfitPrice);
/*     */     }
/*     */ 
/*     */     public void setTakeProfitSlippage(BigDecimal takeProfitSlippage)
/*     */     {
/* 136 */       super.setTakeProfitSlippage(takeProfitSlippage);
/*     */     }
/*     */ 
/*     */     public void setTakeProfitOrderId(String takeProfitOrderId)
/*     */     {
/* 141 */       super.setTakeProfitOrderId(takeProfitOrderId);
/*     */     }
/*     */ 
/*     */     public void setGoodTillTime(long goodTillTime)
/*     */     {
/* 146 */       super.setGoodTillTime(goodTillTime);
/*     */     }
/*     */ 
/*     */     public void setExecuting(boolean executing)
/*     */     {
/* 151 */       super.setExecuting(executing);
/*     */     }
/*     */ 
/*     */     public void setLabel(String label)
/*     */     {
/* 156 */       super.setLabel(label);
/*     */     }
/*     */ 
/*     */     public void setComment(String comment)
/*     */     {
/* 161 */       super.setComment(comment);
/*     */     }
/*     */ 
/*     */     public void setAmount(BigDecimal amount)
/*     */     {
/* 166 */       super.setAmount(amount);
/*     */     }
/*     */ 
/*     */     public void setMergedFrom(String[] mergedFrom)
/*     */     {
/* 171 */       super.setMergedFrom(mergedFrom);
/*     */     }
/*     */   }
/*     */ 
/*     */   public static class CloseData extends OrderHistoricalData.CloseData
/*     */   {
/*     */     private static final String HEADER = "OHD.CD";
/*     */ 
/*     */     public CloseData()
/*     */     {
/*     */     }
/*     */ 
/*     */     public CloseData(OrderHistoricalData.CloseData closeData)
/*     */     {
/*  26 */       super();
/*     */     }
/*     */ 
/*     */     public void setCloseTime(long closeTime)
/*     */     {
/*  31 */       super.setCloseTime(closeTime);
/*     */     }
/*     */ 
/*     */     public void setClosePrice(BigDecimal closePrice)
/*     */     {
/*  36 */       super.setClosePrice(closePrice);
/*     */     }
/*     */ 
/*     */     public void setAmount(BigDecimal amount)
/*     */     {
/*  41 */       super.setAmount(amount);
/*     */     }
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.charts.data.datacache.OrderHistoricalDataMutable
 * JD-Core Version:    0.6.0
 */