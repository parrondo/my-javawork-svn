/*     */ package com.dukascopy.api.impl.connect;
/*     */ 
/*     */ import com.dukascopy.api.IEngine.OrderCommand;
/*     */ import com.dukascopy.api.IOrder;
/*     */ import com.dukascopy.api.IOrder.State;
/*     */ import com.dukascopy.api.Instrument;
/*     */ import com.dukascopy.api.JFException;
/*     */ import com.dukascopy.api.JFException.Error;
/*     */ import com.dukascopy.dds2.greed.agent.strategy.StratUtils;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import org.slf4j.Logger;
/*     */ import org.slf4j.LoggerFactory;
/*     */ 
/*     */ public class OrdersInternalCollection
/*     */ {
/*  19 */   private static final Logger LOGGER = LoggerFactory.getLogger(OrdersInternalCollection.class);
/*     */ 
/*  21 */   private static final List<OrdersInternalCollection> instances = new ArrayList();
/*     */ 
/*  23 */   private List<PlatformOrderImpl> orders = new ArrayList();
/*  24 */   private List<String> removedIds = new ArrayList(30);
/*     */   private JForexTaskManager taskManager;
/*     */ 
/*     */   public OrdersInternalCollection(JForexTaskManager taskManager)
/*     */   {
/*  28 */     this.taskManager = taskManager;
/*  29 */     synchronized (instances) {
/*  30 */       instances.add(this);
/*     */     }
/*     */   }
/*     */ 
/*     */   public synchronized PlatformOrderImpl getOrderByLabel(String label) {
/*  35 */     for (PlatformOrderImpl platformOrderImpl : this.orders) {
/*  36 */       String posLabel = platformOrderImpl.getLabel();
/*  37 */       if ((posLabel != null) && (posLabel.equals(label))) {
/*  38 */         return platformOrderImpl;
/*     */       }
/*     */     }
/*  41 */     return null;
/*     */   }
/*     */ 
/*     */   public synchronized PlatformOrderImpl removeByLabel(String label) {
/*  45 */     for (Iterator iterator = this.orders.iterator(); iterator.hasNext(); ) {
/*  46 */       PlatformOrderImpl platformOrderImpl = (PlatformOrderImpl)iterator.next();
/*  47 */       String posLabel = platformOrderImpl.getLabel();
/*  48 */       if ((posLabel != null) && (posLabel.equals(label))) {
/*  49 */         iterator.remove();
/*  50 */         return platformOrderImpl;
/*     */       }
/*     */     }
/*  53 */     return null;
/*     */   }
/*     */ 
/*     */   public synchronized PlatformOrderImpl removeById(String positionId) {
/*  57 */     for (Iterator iterator = this.orders.iterator(); iterator.hasNext(); ) {
/*  58 */       PlatformOrderImpl platformOrderImpl = (PlatformOrderImpl)iterator.next();
/*  59 */       String posId = platformOrderImpl.getId();
/*  60 */       if ((posId != null) && (posId.equals(positionId))) {
/*  61 */         if (this.removedIds.size() >= 29) {
/*  62 */           this.removedIds.remove(0);
/*     */         }
/*  64 */         this.removedIds.add(posId);
/*  65 */         iterator.remove();
/*  66 */         return platformOrderImpl;
/*     */       }
/*     */     }
/*  69 */     return null;
/*     */   }
/*     */ 
/*     */   public void put(String label, PlatformOrderImpl platformOrderImpl, boolean checkLabel) throws JFException {
/*  73 */     synchronized (instances) {
/*  74 */       if (checkLabel)
/*     */       {
/*  76 */         if (getOrderByLabel(label) != null) {
/*  77 */           throw new JFException(JFException.Error.LABEL_NOT_UNIQUE, "Label not unique(code 1). (Order already exists) [" + label + "]" + ":" + getOrderByLabel(label).toStringDetail());
/*     */         }
/*     */ 
/*  81 */         for (OrdersInternalCollection instance : instances) {
/*  82 */           if (instance != this)
/*     */           {
/*  85 */             PlatformOrderImpl otherInstanceOrder = instance.getOrderByLabel(label);
/*  86 */             if (otherInstanceOrder != null)
/*     */             {
/*  89 */               if (otherInstanceOrder.getState() == IOrder.State.CREATED)
/*     */               {
/*  91 */                 throw new JFException(JFException.Error.LABEL_NOT_UNIQUE, "Label not unique(code 2). (Order already exists) [" + label + "]" + ":" + otherInstanceOrder.toStringDetail());
/*     */               }
/*  93 */               boolean flushed = instance.taskManager.flushQueue(200L);
/*  94 */               if ((!instance.taskManager.isStrategyStopping()) && (instance.getOrderByLabel(label) != null)) {
/*  95 */                 if (flushed)
/*     */                 {
/*  97 */                   throw new JFException(JFException.Error.LABEL_NOT_UNIQUE, "Label not unique(code 3). (Order already exists) [" + label + "]" + ":" + otherInstanceOrder.toStringDetail());
/*     */                 }
/*     */ 
/* 101 */                 if (!this.removedIds.contains(otherInstanceOrder.getId()))
/*     */                 {
/* 105 */                   LOGGER.warn("Rejecting order submit with label [" + label + "] while not completely " + "sure it exists, another strategy too slow with processing messages");
/*     */ 
/* 107 */                   throw new JFException(JFException.Error.LABEL_NOT_UNIQUE, "Label not unique(code 4). (Order already exists) [" + label + "]" + ":" + otherInstanceOrder.toStringDetail());
/*     */                 }
/*     */               }
/*     */             }
/*     */           }
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/* 116 */       synchronized (this)
/*     */       {
/* 118 */         if ((checkLabel) && 
/* 119 */           (getOrderByLabel(label) != null)) {
/* 120 */           throw new JFException(JFException.Error.LABEL_NOT_UNIQUE, "Label not unique(code 5). (Order already exists) [" + label + "]" + ":" + getOrderByLabel(label).toStringDetail());
/*     */         }
/*     */ 
/* 124 */         if (platformOrderImpl != null) {
/* 125 */           this.orders.add(platformOrderImpl);
/* 126 */           this.removedIds.remove(platformOrderImpl.getId());
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public synchronized boolean isLongExposure(Instrument instrument)
/*     */   {
/* 134 */     double exposure = 0.0D;
/* 135 */     for (PlatformOrderImpl platformOrderImpl : this.orders) {
/* 136 */       if (platformOrderImpl.getState() == IOrder.State.FILLED) {
/* 137 */         if (platformOrderImpl.getOrderCommand().isLong())
/* 138 */           exposure += platformOrderImpl.getAmount();
/*     */         else {
/* 140 */           exposure -= platformOrderImpl.getAmount();
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/* 145 */     double placeBidsAmount = 0.0D;
/* 146 */     double placeOffersAmount = 0.0D;
/* 147 */     for (PlatformOrderImpl order : this.orders) {
/* 148 */       if (order.getState() == IOrder.State.OPENED) {
/* 149 */         if (order.getOrderCommand() == IEngine.OrderCommand.PLACE_BID)
/* 150 */           placeBidsAmount += order.getAmount();
/* 151 */         else if (order.getOrderCommand() == IEngine.OrderCommand.PLACE_OFFER) {
/* 152 */           placeOffersAmount -= order.getAmount();
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/* 157 */     placeBidsAmount += exposure;
/* 158 */     placeOffersAmount += exposure;
/* 159 */     if ((placeBidsAmount <= 0.0D ? -placeBidsAmount : placeBidsAmount) > (placeOffersAmount <= 0.0D ? -placeOffersAmount : placeOffersAmount))
/*     */     {
/* 161 */       exposure = placeBidsAmount;
/*     */     }
/* 163 */     else exposure = placeOffersAmount;
/*     */ 
/* 166 */     return StratUtils.roundHalfEven(exposure, 2) > 0.0D;
/*     */   }
/*     */ 
/*     */   public synchronized void add(PlatformOrderImpl platformOrderImpl)
/*     */   {
/* 171 */     if (platformOrderImpl != null) {
/* 172 */       this.orders.add(platformOrderImpl);
/* 173 */       this.removedIds.remove(platformOrderImpl.getId());
/*     */     }
/*     */   }
/*     */ 
/*     */   public synchronized List<IOrder> allAsOrders() {
/* 178 */     if (this.orders == null) {
/* 179 */       return new ArrayList(0);
/*     */     }
/* 181 */     return new ArrayList(this.orders);
/*     */   }
/*     */ 
/*     */   public synchronized PlatformOrderImpl getOrderById(String positionId)
/*     */   {
/* 186 */     for (PlatformOrderImpl platformOrderImpl : this.orders) {
/* 187 */       String posId = platformOrderImpl.getId();
/* 188 */       if ((posId != null) && (posId.equals(positionId))) {
/* 189 */         return platformOrderImpl;
/*     */       }
/*     */     }
/* 192 */     return null;
/*     */   }
/*     */ 
/*     */   public synchronized PlatformOrderImpl getOrderByOpeningOrderId(String positionId) {
/* 196 */     for (PlatformOrderImpl platformOrderImpl : this.orders) {
/* 197 */       String orderId = platformOrderImpl.getOpeningOrderId();
/* 198 */       if ((orderId != null) && (orderId.equals(positionId))) {
/* 199 */         return platformOrderImpl;
/*     */       }
/*     */     }
/* 202 */     return null;
/*     */   }
/*     */ 
/*     */   public synchronized List<PlatformOrderImpl> getAllMergeTargets() {
/* 206 */     List rc = new ArrayList();
/* 207 */     for (PlatformOrderImpl platformOrderImpl : this.orders) {
/* 208 */       if (platformOrderImpl.lastServerRequest == PlatformOrderImpl.ServerRequest.MERGE_TARGET) {
/* 209 */         rc.add(platformOrderImpl);
/*     */       }
/*     */     }
/* 212 */     return rc;
/*     */   }
/*     */ 
/*     */   public void dispose() {
/* 216 */     synchronized (instances) {
/* 217 */       instances.remove(this);
/* 218 */       synchronized (this) {
/* 219 */         this.orders = null;
/*     */       }
/*     */     }
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.api.impl.connect.OrdersInternalCollection
 * JD-Core Version:    0.6.0
 */