/*     */ package com.dukascopy.dds2.greed.mt.common;
/*     */ 
/*     */ import com.dukascopy.api.IOrder;
/*     */ import com.dukascopy.api.Instrument;
/*     */ import com.dukascopy.charts.data.datacache.FeedDataProvider;
/*     */ import com.dukascopy.charts.data.datacache.TickData;
/*     */ import com.dukascopy.dds2.greed.mt.exceptions.MTAgentException;
/*     */ import com.dukascopy.dds2.greed.mt.helpers.MTAPIHelpers;
/*     */ import com.dukascopy.transport.common.msg.ProtocolMessage;
/*     */ import com.dukascopy.transport.common.msg.group.OrderGroupMessage;
/*     */ import com.dukascopy.transport.common.msg.response.NotificationMessage;
/*     */ import java.io.PrintStream;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.concurrent.ConcurrentHashMap;
/*     */ import org.slf4j.Logger;
/*     */ import org.slf4j.LoggerFactory;
/*     */ 
/*     */ public abstract class AgentBase
/*     */   implements IAgent
/*     */ {
/*  22 */   private static Logger log = LoggerFactory.getLogger(AgentBase.class);
/*     */ 
/*  24 */   protected static Map<String, ProtocolMessage> notifMsgs = new ConcurrentHashMap();
/*  25 */   protected static Map<String, List<IOrder>> ordersHistory = new ConcurrentHashMap();
/*  26 */   protected static Map<Integer, OrderGroupMessage> openedOrders = new ConcurrentHashMap();
/*  27 */   protected static Map<Integer, Integer> errorCodes = new ConcurrentHashMap();
/*  28 */   protected static Map<Integer, String> errorMsgs = new ConcurrentHashMap();
/*     */   protected long agentExecutorThreadId;
/*     */   private boolean isTestMode;
/*     */ 
/*     */   public AgentBase()
/*     */   {
/*  30 */     this.agentExecutorThreadId = 0L;
/*     */ 
/*  34 */     this.isTestMode = false;
/*     */   }
/*     */   public void setTestMode(boolean testmode) {
/*  37 */     this.isTestMode = testmode;
/*     */   }
/*     */ 
/*     */   public boolean isTestMode()
/*     */   {
/*  42 */     return this.isTestMode;
/*     */   }
/*     */ 
/*     */   public void setError(Integer mtId, int errorCode, String errorMsg)
/*     */   {
/*  47 */     errorCodes.put(mtId, new Integer(errorCode));
/*  48 */     errorMsgs.put(mtId, new String(errorMsg));
/*     */   }
/*     */ 
/*     */   void setError(int id, int errorCode, String errorMsg) {
/*  52 */     Integer mtId = new Integer(id);
/*  53 */     setError(mtId, errorCode, errorMsg);
/*     */   }
/*     */ 
/*     */   public void putNotifMsg(String key, ProtocolMessage msg)
/*     */   {
/*  58 */     notifMsgs.put(key, msg);
/*     */   }
/*     */ 
/*     */   public ProtocolMessage getNotifMsg(String key)
/*     */   {
/*  63 */     return (ProtocolMessage)notifMsgs.get(key);
/*     */   }
/*     */ 
/*     */   public boolean existNotifMsg(String key) {
/*  67 */     return notifMsgs.containsKey(key);
/*     */   }
/*     */ 
/*     */   public void removeNotifMsg(String key) {
/*  71 */     notifMsgs.remove(key);
/*     */   }
/*     */ 
/*     */   private double getAsk(String symbol)
/*     */   {
/*  86 */     double rc = 0.0D;
/*  87 */     Instrument instrument = null;
/*  88 */     if (symbol.length() > 6)
/*  89 */       instrument = Instrument.fromString(symbol);
/*     */     else {
/*  91 */       instrument = MTAPIHelpers.fromMTString(symbol);
/*     */     }
/*  93 */     if (instrument != null) {
/*  94 */       TickData lastTick = FeedDataProvider.getDefaultInstance().getLastTick(instrument);
/*     */ 
/*  96 */       if (lastTick != null) {
/*  97 */         synchronized (lastTick) {
/*  98 */           rc = lastTick.getAsk();
/*     */         }
/*     */       }
/*     */     }
/* 102 */     return rc;
/*     */   }
/*     */ 
/*     */   public double MAsk(int id, String instrument) {
/* 106 */     double rc = 0.0D;
/* 107 */     if ((Thread.currentThread().getId() != this.agentExecutorThreadId) && (!isTestMode())) {
/* 108 */       throw new MTAgentException(-19);
/*     */     }
/* 110 */     if ((instrument != null) && (!instrument.isEmpty())) {
/* 111 */       rc = getBid(instrument);
/*     */     } else {
/* 113 */       Integer mtId = new Integer(id);
/* 114 */       if (!openedOrders.containsKey(mtId)) {
/* 115 */         setError(mtId, 4105, "ERR_NO_ORDER_SELECTED_MSG");
/*     */ 
/* 117 */         throw new MTAgentException(-13, "ERR_NO_ORDER_SELECTED_MSG");
/*     */       }
/*     */ 
/* 121 */       if (openedOrders.containsKey(mtId)) {
/*     */         try {
/* 123 */           OrderGroupMessage cachedMsg = (OrderGroupMessage)openedOrders.get(mtId);
/* 124 */           OrderGroupMessage msg = MTAPIHelpers.getOrderGroupById(cachedMsg.getOrderGroupId())[0];
/*     */ 
/* 126 */           rc = getAsk(msg.getInstrument());
/*     */         } catch (Exception ex) {
/* 128 */           log.error(ex.getMessage(), ex);
/*     */         }
/*     */       }
/*     */     }
/* 132 */     return rc;
/*     */   }
/*     */ 
/*     */   private double getBid(String symbol) {
/* 136 */     double rc = 0.0D;
/* 137 */     Instrument instrument = null;
/* 138 */     if (symbol.length() > 6)
/* 139 */       instrument = Instrument.fromString(symbol);
/*     */     else {
/* 141 */       instrument = MTAPIHelpers.fromMTString(symbol);
/*     */     }
/* 143 */     if (instrument != null) {
/* 144 */       TickData lastTick = FeedDataProvider.getDefaultInstance().getLastTick(instrument);
/* 145 */       if (lastTick != null) {
/* 146 */         synchronized (lastTick) {
/* 147 */           rc = lastTick.getBid();
/*     */         }
/*     */       }
/*     */     }
/* 151 */     return rc;
/*     */   }
/*     */ 
/*     */   public double MBid(int id, String instrument) {
/* 155 */     double rc = 0.0D;
/* 156 */     if ((Thread.currentThread().getId() != this.agentExecutorThreadId) && (!isTestMode())) {
/* 157 */       throw new MTAgentException(-19);
/*     */     }
/* 159 */     if ((instrument != null) && (!instrument.isEmpty())) {
/* 160 */       rc = getBid(instrument);
/*     */     } else {
/* 162 */       Integer mtId = new Integer(id);
/* 163 */       if (!openedOrders.containsKey(mtId)) {
/* 164 */         setError(mtId, 4105, "ERR_NO_ORDER_SELECTED_MSG");
/*     */ 
/* 166 */         throw new MTAgentException(-13, "ERR_NO_ORDER_SELECTED_MSG");
/*     */       }
/*     */ 
/* 170 */       if (openedOrders.containsKey(mtId)) {
/*     */         try {
/* 172 */           OrderGroupMessage cachedMsg = (OrderGroupMessage)openedOrders.get(mtId);
/* 173 */           OrderGroupMessage msg = MTAPIHelpers.getOrderGroupById(cachedMsg.getOrderGroupId())[0];
/*     */ 
/* 175 */           rc = getBid(msg.getInstrument());
/*     */         } catch (Exception ex) {
/* 177 */           log.error(ex.getMessage(), ex);
/*     */         }
/*     */       }
/*     */     }
/* 181 */     return rc;
/*     */   }
/*     */ 
/*     */   private long getTime(String symbol)
/*     */   {
/* 186 */     long rc = 0L;
/* 187 */     Instrument instrument = Instrument.fromString(symbol);
/* 188 */     TickData lastTick = FeedDataProvider.getDefaultInstance().getLastTick(instrument);
/*     */ 
/* 190 */     if (lastTick != null) {
/* 191 */       synchronized (lastTick) {
/* 192 */         rc = lastTick.getTime();
/*     */       }
/*     */     }
/* 195 */     return rc / 1000L;
/*     */   }
/*     */ 
/*     */   public long MTime(int id, String instrument) {
/* 199 */     long rc = 0L;
/* 200 */     if ((Thread.currentThread().getId() != this.agentExecutorThreadId) && (!isTestMode()))
/*     */     {
/* 202 */       throw new MTAgentException(-19);
/*     */     }
/* 204 */     if ((instrument != null) && (!instrument.isEmpty())) {
/* 205 */       rc = getTime(instrument);
/*     */     } else {
/* 207 */       Integer mtId = new Integer(id);
/* 208 */       if (!openedOrders.containsKey(mtId)) {
/* 209 */         setError(mtId, 4105, "ERR_NO_ORDER_SELECTED_MSG");
/*     */ 
/* 211 */         throw new MTAgentException(-13, "ERR_NO_ORDER_SELECTED_MSG");
/*     */       }
/*     */ 
/* 215 */       if (openedOrders.containsKey(mtId)) {
/*     */         try {
/* 217 */           OrderGroupMessage cachedMsg = (OrderGroupMessage)openedOrders.get(mtId);
/* 218 */           OrderGroupMessage msg = MTAPIHelpers.getOrderGroupById(cachedMsg.getOrderGroupId())[0];
/*     */ 
/* 220 */           rc = getTime(msg.getInstrument());
/*     */         } catch (Exception ex) {
/* 222 */           log.error(ex.getMessage(), ex);
/*     */         }
/*     */       }
/*     */     }
/* 226 */     return rc;
/*     */   }
/*     */ 
/*     */   public static class CommonExecution
/*     */   {
/*     */     public Logger getParentLogger()
/*     */     {
/* 272 */       return AgentBase.log;
/*     */     }
/*     */ 
/*     */     public void setError(Integer mtId, int errorCode, String errorMsg) {
/* 276 */       AgentBase.errorCodes.put(mtId, new Integer(errorCode));
/* 277 */       AgentBase.errorMsgs.put(mtId, new String(errorMsg));
/*     */     }
/*     */ 
/*     */     public void setError(int id, int errorCode, String errorMsg) {
/* 281 */       Integer mtId = new Integer(id);
/* 282 */       setError(mtId, errorCode, errorMsg);
/*     */     }
/*     */ 
/*     */     public boolean isOrderSelected(Integer key) {
/* 286 */       return AgentBase.openedOrders.containsKey(key);
/*     */     }
/*     */ 
/*     */     public void putOrderGroup(Integer key, OrderGroupMessage value) {
/* 290 */       AgentBase.openedOrders.put(key, value);
/*     */     }
/*     */ 
/*     */     public synchronized OrderGroupMessage getOrderGroup(Integer key) {
/* 294 */       OrderGroupMessage msg = null;
/* 295 */       synchronized (AgentBase.openedOrders) {
/* 296 */         if (AgentBase.openedOrders.containsKey(key)) {
/* 297 */           OrderGroupMessage cachedMsg = (OrderGroupMessage)AgentBase.openedOrders.get(key);
/*     */           try {
/* 299 */             msg = MTAPIHelpers.getOrderGroupById(cachedMsg.getOrderGroupId())[0];
/*     */ 
/* 301 */             if (msg == null) {
/* 302 */               setError(key, 4105, "ERR_NO_ORDER_SELECTED_MSG");
/*     */ 
/* 304 */               MTAgentException ex = new MTAgentException(-13, "ERR_NO_ORDER_SELECTED_MSG");
/*     */ 
/* 306 */               AgentBase.log.error(ex.getMessage(), ex);
/*     */ 
/* 308 */               throw ex;
/*     */             }
/*     */           } catch (Exception ex) {
/* 311 */             AgentBase.log.error(ex.getMessage(), ex);
/*     */           }
/*     */         } else {
/* 314 */           setError(key, 4105, "ERR_NO_ORDER_SELECTED_MSG");
/*     */ 
/* 316 */           throw new MTAgentException(-13, "ERR_NO_ORDER_SELECTED_MSG");
/*     */         }
/*     */       }
/*     */ 
/* 320 */       return msg;
/*     */     }
/*     */ 
/*     */     public synchronized OrderGroupMessage getOrderGroup(int ticket)
/*     */     {
/* 325 */       OrderGroupMessage msg = null;
/*     */       try {
/* 327 */         msg = MTAPIHelpers.getOrderGroupById(ticket)[0];
/* 328 */         if (msg == null) {
/* 329 */           MTAgentException ex = new MTAgentException(-13, "ERR_NO_ORDER_SELECTED_MSG");
/*     */ 
/* 331 */           AgentBase.log.error(ex.getMessage(), ex);
/*     */ 
/* 333 */           throw ex;
/*     */         }
/*     */       } catch (Exception ex) {
/* 336 */         AgentBase.log.error(ex.getMessage(), ex);
/* 337 */         throw new MTAgentException(-13, "ERR_NO_ORDER_SELECTED_MSG");
/*     */       }
/*     */ 
/* 340 */       return msg;
/*     */     }
/*     */ 
/*     */     public void putNotifMsg(String key, NotificationMessage msg) {
/* 344 */       AgentBase.notifMsgs.put(key, msg);
/*     */     }
/*     */ 
/*     */     public ProtocolMessage getNotifMsg(String key) {
/* 348 */       return (ProtocolMessage)AgentBase.notifMsgs.get(key);
/*     */     }
/*     */ 
/*     */     public boolean existNotifMsg(String key) {
/* 352 */       return AgentBase.notifMsgs.containsKey(key);
/*     */     }
/*     */ 
/*     */     public void removeNotifMsg(String key) {
/* 356 */       AgentBase.notifMsgs.remove(key);
/*     */     }
/*     */   }
/*     */ 
/*     */   public class SweetLock
/*     */   {
/* 230 */     private int returnId = 0;
/*     */ 
/* 236 */     private String id = "";
/*     */ 
/*     */     public SweetLock() {  }
/*     */ 
/* 239 */     int freeze(String id) { this.id = id;
/* 240 */       this.returnId = -11;
/* 241 */       synchronized (this) {
/*     */         try {
/* 243 */           wait(15000L);
/*     */         }
/*     */         catch (InterruptedException ie) {
/* 246 */           System.out.println("Interrupted.");
/*     */         } catch (Exception e) {
/* 248 */           e.printStackTrace();
/*     */         }
/*     */       }
/* 251 */       return this.returnId;
/*     */     }
/*     */ 
/*     */     public void unfreeze(int returnId)
/*     */     {
/* 258 */       this.returnId = returnId;
/* 259 */       synchronized (this) {
/* 260 */         notify();
/*     */       }
/*     */     }
/*     */ 
/*     */     public String getId() {
/* 265 */       return this.id;
/*     */     }
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.mt.common.AgentBase
 * JD-Core Version:    0.6.0
 */