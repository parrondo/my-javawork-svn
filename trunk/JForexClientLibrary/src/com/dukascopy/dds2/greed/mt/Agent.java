/*     */ package com.dukascopy.dds2.greed.mt;
/*     */ 
/*     */ import com.dukascopy.dds2.greed.mt.common.AgentBase;
/*     */ import com.dukascopy.dds2.greed.mt.common.IAgent;
/*     */ import com.dukascopy.dds2.greed.mt.exceptions.MTAgentException;
/*     */ import com.dukascopy.dds2.greed.mt.func.MOrderClose;
/*     */ import com.dukascopy.dds2.greed.mt.func.MOrderCloseBy;
/*     */ import com.dukascopy.dds2.greed.mt.func.MOrderClosePrice;
/*     */ import com.dukascopy.dds2.greed.mt.func.MOrderCloseTime;
/*     */ import com.dukascopy.dds2.greed.mt.func.MOrderComment;
/*     */ import com.dukascopy.dds2.greed.mt.func.MOrderCommission;
/*     */ import com.dukascopy.dds2.greed.mt.func.MOrderDelete;
/*     */ import com.dukascopy.dds2.greed.mt.func.MOrderExpiration;
/*     */ import com.dukascopy.dds2.greed.mt.func.MOrderLots;
/*     */ import com.dukascopy.dds2.greed.mt.func.MOrderMagicNumber;
/*     */ import com.dukascopy.dds2.greed.mt.func.MOrderModify;
/*     */ import com.dukascopy.dds2.greed.mt.func.MOrderOpenPrice;
/*     */ import com.dukascopy.dds2.greed.mt.func.MOrderOpenTime;
/*     */ import com.dukascopy.dds2.greed.mt.func.MOrderPrint;
/*     */ import com.dukascopy.dds2.greed.mt.func.MOrderProfit;
/*     */ import com.dukascopy.dds2.greed.mt.func.MOrderSelect;
/*     */ import com.dukascopy.dds2.greed.mt.func.MOrderSend;
/*     */ import com.dukascopy.dds2.greed.mt.func.MOrderStopLoss;
/*     */ import com.dukascopy.dds2.greed.mt.func.MOrderSwap;
/*     */ import com.dukascopy.dds2.greed.mt.func.MOrderSymbol;
/*     */ import com.dukascopy.dds2.greed.mt.func.MOrderTakeProfit;
/*     */ import com.dukascopy.dds2.greed.mt.func.MOrderTicket;
/*     */ import com.dukascopy.dds2.greed.mt.func.MOrderType;
/*     */ import com.dukascopy.dds2.greed.mt.func.MOrdersHistoryTotal;
/*     */ import com.dukascopy.dds2.greed.mt.func.MOrdersTotal;
/*     */ import com.dukascopy.transport.common.model.type.NotificationMessageCode;
/*     */ import com.dukascopy.transport.common.msg.ProtocolMessage;
/*     */ import com.dukascopy.transport.common.msg.group.OrderGroupMessage;
/*     */ import com.dukascopy.transport.common.msg.response.NotificationMessage;
/*     */ import java.util.Map;
/*     */ import java.util.concurrent.Callable;
/*     */ import java.util.concurrent.ExecutorService;
/*     */ import java.util.concurrent.Executors;
/*     */ import java.util.concurrent.Future;
/*     */ import java.util.concurrent.ThreadFactory;
/*     */ import org.slf4j.Logger;
/*     */ import org.slf4j.LoggerFactory;
/*     */ 
/*     */ public class Agent extends AgentBase
/*     */ {
/*  46 */   private static Logger log = LoggerFactory.getLogger(Agent.class);
/*  47 */   private static long WAIT_PERIOD = 15000L;
/*  48 */   private ExecutorService executorService = null;
/*     */ 
/*  50 */   private static Agent instance = null;
/*     */ 
/*     */   public static final IAgent getInstance() {
/*  53 */     if (instance == null)
/*  54 */       instance = new Agent();
/*  55 */     return instance;
/*     */   }
/*     */ 
/*     */   public Agent() {
/*  59 */     restartExecutorService();
/*     */   }
/*     */ 
/*     */   private void restartExecutorService() {
/*  63 */     if (this.executorService != null) {
/*  64 */       this.executorService.shutdownNow();
/*     */     }
/*  66 */     this.executorService = Executors.newSingleThreadExecutor(new ThreadFactory()
/*     */     {
/*     */       public Thread newThread(Runnable r) {
/*  69 */         Thread thread = new Thread(r);
/*  70 */         thread.setName("Agent Executor");
/*  71 */         Agent.access$002(Agent.this, thread.getId());
/*  72 */         return thread;
/*     */       } } );
/*     */   }
/*     */ 
/*     */   public <T> Future<T> executeTask(Callable<T> callable) {
/*  78 */     return this.executorService.submit(callable);
/*     */   }
/*     */ 
/*     */   public synchronized boolean MOrderClose(int id, int ticket, double lots, double price, int slippage, long Color)
/*     */     throws MTAgentException
/*     */   {
/* 104 */     boolean returnValue = false;
/* 105 */     if ((Thread.currentThread().getId() != this.agentExecutorThreadId) && (!isTestMode())) {
/* 106 */       throw new MTAgentException(-19);
/*     */     }
/*     */ 
/* 109 */     MOrderClose func = new MOrderClose();
/* 110 */     String label = func.execute(id, ticket, lots, price, slippage, Color);
/* 111 */     synchronized (this) {
/*     */       try {
/* 113 */         long start = System.currentTimeMillis();
/* 114 */         long end = System.currentTimeMillis();
/*     */ 
/* 116 */         wait(WAIT_PERIOD);
/* 117 */         end = System.currentTimeMillis();
/*     */       }
/*     */       catch (InterruptedException ex) {
/* 120 */         setError(Integer.valueOf(id), 128, "ERR_TRADE_TIMEOUT_MSG");
/* 121 */         log.error("Interrupt MOrderClose", ex);
/*     */       }
/* 123 */       if (existNotifMsg(label))
/*     */       {
/* 126 */         ProtocolMessage msg = getNotifMsg(label);
/* 127 */         if ((msg instanceof OrderGroupMessage)) {
/* 128 */           OrderGroupMessage groupMessage = (OrderGroupMessage)msg;
/* 129 */           setError(Integer.valueOf(id), 0, "ERR_NO_ERROR_MSG");
/* 130 */           returnValue = true;
/* 131 */         } else if ((msg instanceof NotificationMessage)) {
/* 132 */           NotificationMessage notificationMessage = (NotificationMessage)msg;
/* 133 */           int errorCode = notificationMessage.getNotificationCode().ordinal();
/* 134 */           String errorMsg = notificationMessage.getText();
/* 135 */           setError(Integer.valueOf(id), errorCode, errorMsg);
/*     */         }
/* 137 */         removeNotifMsg(label);
/*     */       }
/*     */     }
/* 140 */     return returnValue;
/*     */   }
/*     */ 
/*     */   public boolean MOrderCloseBy(int id, int ticket, int opposite, long Color)
/*     */     throws MTAgentException
/*     */   {
/* 163 */     boolean returnValue = false;
/* 164 */     if ((Thread.currentThread().getId() != this.agentExecutorThreadId) && (!isTestMode())) {
/* 165 */       setError(Integer.valueOf(id), 4017, "ERR_DLL_CALLS_NOT_ALLOWED_MSG");
/* 166 */       throw new MTAgentException(-19);
/*     */     }
/* 168 */     String label = "";
/* 169 */     MOrderCloseBy func = new MOrderCloseBy();
/*     */     try {
/* 171 */       label = func.execute(id, ticket, opposite, Color);
/*     */     } catch (Exception ex) {
/* 173 */       log.error(ex.getMessage(), ex);
/* 174 */       setError(Integer.valueOf(id), 2, "ERR_COMMON_ERROR_MSG");
/*     */ 
/* 176 */       throw new MTAgentException(-4);
/*     */     }
/*     */ 
/* 179 */     synchronized (this) {
/*     */       try {
/* 181 */         long start = System.currentTimeMillis();
/* 182 */         long end = System.currentTimeMillis();
/*     */ 
/* 184 */         wait(WAIT_PERIOD);
/* 185 */         end = System.currentTimeMillis();
/*     */       }
/*     */       catch (InterruptedException ex) {
/* 188 */         setError(Integer.valueOf(id), 128, "ERR_TRADE_TIMEOUT_MSG");
/*     */ 
/* 190 */         log.error("Interrupt MOrderCloseBy", ex);
/*     */       }
/* 192 */       if (notifMsgs.containsKey(label)) {
/* 193 */         ProtocolMessage msg = (ProtocolMessage)notifMsgs.get(label);
/* 194 */         if ((msg instanceof OrderGroupMessage)) {
/* 195 */           OrderGroupMessage orderGroupMessage = (OrderGroupMessage)msg;
/* 196 */           returnValue = true;
/* 197 */           setError(Integer.valueOf(id), 0, "ERR_NO_ERROR_MSG");
/*     */         }
/* 199 */         else if ((msg instanceof NotificationMessage)) {
/* 200 */           NotificationMessage notificationMessage = (NotificationMessage)msg;
/* 201 */           setError(Integer.valueOf(id), 1, notificationMessage.getText());
/*     */         }
/*     */ 
/* 204 */         notifMsgs.remove(label);
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 209 */     return returnValue;
/*     */   }
/*     */ 
/*     */   public double MOrderClosePrice(int id)
/*     */     throws MTAgentException
/*     */   {
/* 219 */     double returnValue = 0.0D;
/*     */ 
/* 221 */     if ((Thread.currentThread().getId() != this.agentExecutorThreadId) && (!isTestMode())) {
/* 222 */       throw new MTAgentException(-19);
/*     */     }
/*     */ 
/* 225 */     MOrderClosePrice func = new MOrderClosePrice();
/*     */     try {
/* 227 */       returnValue = func.execute(id);
/*     */     } catch (Exception ex) {
/* 229 */       log.error(ex.getMessage(), ex);
/*     */     }
/*     */ 
/* 232 */     return returnValue;
/*     */   }
/*     */ 
/*     */   public long MOrderCloseTime(int id)
/*     */     throws MTAgentException
/*     */   {
/* 246 */     long returnValue = 0L;
/* 247 */     if ((Thread.currentThread().getId() != this.agentExecutorThreadId) && (!isTestMode())) {
/* 248 */       throw new MTAgentException(-19);
/*     */     }
/* 250 */     MOrderCloseTime func = new MOrderCloseTime();
/*     */     try {
/* 252 */       returnValue = func.execute(id);
/*     */     } catch (Exception ex) {
/* 254 */       log.error(ex.getMessage(), ex);
/*     */     }
/* 256 */     return returnValue;
/*     */   }
/*     */ 
/*     */   public String MOrderComment(int id)
/*     */     throws MTAgentException
/*     */   {
/* 265 */     if ((Thread.currentThread().getId() != this.agentExecutorThreadId) && (!isTestMode())) {
/* 266 */       throw new MTAgentException(-19, "ARSP_THREAD_INCORRECT_MSG");
/*     */     }
/*     */ 
/* 269 */     MOrderComment finc = new MOrderComment();
/* 270 */     return finc.execute(id);
/*     */   }
/*     */ 
/*     */   public double MOrderCommission(int id)
/*     */     throws MTAgentException
/*     */   {
/* 279 */     double returnValue = 0.0D;
/* 280 */     if ((Thread.currentThread().getId() != this.agentExecutorThreadId) && (!isTestMode())) {
/* 281 */       throw new MTAgentException(-19);
/*     */     }
/*     */ 
/* 284 */     MOrderCommission func = new MOrderCommission();
/*     */     try {
/* 286 */       returnValue = func.execute(id);
/*     */     } catch (Exception ex) {
/* 288 */       log.error(ex.getMessage(), ex);
/*     */     }
/* 290 */     return returnValue;
/*     */   }
/*     */ 
/*     */   public boolean MOrderDelete(int id, int ticket, long Color)
/*     */     throws MTAgentException
/*     */   {
/* 309 */     boolean returnValue = false;
/* 310 */     if ((Thread.currentThread().getId() != this.agentExecutorThreadId) && (!isTestMode())) {
/* 311 */       throw new MTAgentException(-19);
/*     */     }
/*     */ 
/* 314 */     String label = "";
/* 315 */     MOrderDelete func = new MOrderDelete();
/* 316 */     label = func.execute(id, ticket, Color);
/*     */ 
/* 318 */     synchronized (this) {
/*     */       try {
/* 320 */         long start = System.currentTimeMillis();
/* 321 */         long end = System.currentTimeMillis();
/*     */ 
/* 323 */         wait(WAIT_PERIOD);
/* 324 */         end = System.currentTimeMillis();
/*     */       }
/*     */       catch (InterruptedException ex) {
/* 327 */         setError(Integer.valueOf(id), 128, "ERR_TRADE_TIMEOUT_MSG");
/*     */ 
/* 329 */         log.error("Interrupt MOrderDelete", ex);
/*     */       }
/* 331 */       if (notifMsgs.containsKey(label)) {
/* 332 */         ProtocolMessage msg = (ProtocolMessage)notifMsgs.get(label);
/* 333 */         if ((msg instanceof OrderGroupMessage)) {
/* 334 */           OrderGroupMessage orderGroupMessage = (OrderGroupMessage)msg;
/* 335 */           returnValue = true;
/* 336 */           setError(Integer.valueOf(id), 0, "ERR_NO_ERROR_MSG");
/*     */         }
/* 338 */         else if ((msg instanceof NotificationMessage)) {
/* 339 */           NotificationMessage notificationMessage = (NotificationMessage)msg;
/* 340 */           setError(Integer.valueOf(id), 1, notificationMessage.getText());
/*     */         }
/*     */ 
/* 343 */         notifMsgs.remove(label);
/*     */       }
/*     */     }
/* 346 */     return returnValue;
/*     */   }
/*     */ 
/*     */   public long MOrderExpiration(int id)
/*     */     throws MTAgentException
/*     */   {
/* 356 */     long returnValue = 0L;
/* 357 */     if ((Thread.currentThread().getId() != this.agentExecutorThreadId) && (!isTestMode())) {
/* 358 */       throw new MTAgentException(-19);
/*     */     }
/*     */ 
/* 361 */     MOrderExpiration func = new MOrderExpiration();
/* 362 */     returnValue = func.execute(id);
/* 363 */     return returnValue;
/*     */   }
/*     */ 
/*     */   public double MOrderLots(int id)
/*     */     throws MTAgentException
/*     */   {
/* 373 */     double returnValue = 0.0D;
/* 374 */     if ((Thread.currentThread().getId() != this.agentExecutorThreadId) && (!isTestMode())) {
/* 375 */       throw new MTAgentException(-19);
/*     */     }
/*     */ 
/* 378 */     MOrderLots func = new MOrderLots();
/* 379 */     returnValue = func.execute(id);
/* 380 */     return returnValue;
/*     */   }
/*     */ 
/*     */   public int MOrderMagicNumber(int id)
/*     */     throws MTAgentException
/*     */   {
/* 390 */     int returnValue = 0;
/* 391 */     if ((Thread.currentThread().getId() != this.agentExecutorThreadId) && (!isTestMode())) {
/* 392 */       throw new MTAgentException(-19);
/*     */     }
/*     */ 
/* 395 */     MOrderMagicNumber func = new MOrderMagicNumber();
/* 396 */     returnValue = func.execute(id);
/* 397 */     return returnValue;
/*     */   }
/*     */ 
/*     */   public boolean MOrderModify(int id, int ticket, double price, double stoploss, double takeprofit, long expiration, long arrow_color)
/*     */     throws MTAgentException
/*     */   {
/* 426 */     boolean returnValue = false;
/* 427 */     if ((Thread.currentThread().getId() != this.agentExecutorThreadId) && (!isTestMode())) {
/* 428 */       throw new MTAgentException(-19);
/*     */     }
/*     */ 
/* 431 */     Integer mtId = new Integer(id);
/* 432 */     String label = "";
/* 433 */     MOrderModify func = new MOrderModify();
/*     */     try {
/* 435 */       label = func.execute(id, ticket, price, stoploss, takeprofit, expiration, arrow_color);
/*     */     }
/*     */     catch (CloneNotSupportedException cnse) {
/* 438 */       throw new MTAgentException(-12, "ERR_COMMON_ERROR_MSG");
/*     */     } catch (MTAgentException mte) {
/* 440 */       throw mte;
/*     */     } catch (Exception ex) {
/* 442 */       throw new MTAgentException(-12, "ERR_COMMON_ERROR_MSG");
/*     */     }
/*     */ 
/* 445 */     synchronized (this) {
/*     */       try {
/* 447 */         long start = System.currentTimeMillis();
/* 448 */         long end = System.currentTimeMillis();
/*     */ 
/* 450 */         wait(WAIT_PERIOD);
/* 451 */         end = System.currentTimeMillis();
/*     */       }
/*     */       catch (InterruptedException ex) {
/* 454 */         setError(Integer.valueOf(id), 128, "ERR_TRADE_TIMEOUT_MSG");
/*     */ 
/* 456 */         log.error("Interrupt MOrderModify", ex);
/*     */       }
/* 458 */       if (notifMsgs.containsKey(label)) {
/* 459 */         ProtocolMessage msg = (ProtocolMessage)notifMsgs.get(label);
/* 460 */         if ((msg instanceof OrderGroupMessage)) {
/* 461 */           OrderGroupMessage orderGroupMessage = (OrderGroupMessage)msg;
/* 462 */           returnValue = true;
/* 463 */           setError(Integer.valueOf(id), 0, "ERR_NO_ERROR_MSG");
/*     */         }
/* 465 */         else if ((msg instanceof NotificationMessage)) {
/* 466 */           NotificationMessage notificationMessage = (NotificationMessage)msg;
/* 467 */           setError(Integer.valueOf(id), 1, notificationMessage.getText());
/*     */         }
/*     */ 
/* 470 */         notifMsgs.remove(label);
/*     */       }
/*     */     }
/* 473 */     return returnValue;
/*     */   }
/*     */ 
/*     */   public double MOrderOpenPrice(int id)
/*     */     throws MTAgentException
/*     */   {
/* 483 */     double returnValue = 0.0D;
/* 484 */     if ((Thread.currentThread().getId() != this.agentExecutorThreadId) && (!isTestMode())) {
/* 485 */       throw new MTAgentException(-19);
/*     */     }
/* 487 */     MOrderOpenPrice func = new MOrderOpenPrice();
/* 488 */     returnValue = func.execute(id);
/* 489 */     return returnValue;
/*     */   }
/*     */ 
/*     */   public long MOrderOpenTime(int id)
/*     */     throws MTAgentException
/*     */   {
/* 498 */     long returnValue = 0L;
/* 499 */     if ((Thread.currentThread().getId() != this.agentExecutorThreadId) && (!isTestMode())) {
/* 500 */       throw new MTAgentException(-19);
/*     */     }
/*     */ 
/* 503 */     MOrderOpenTime func = new MOrderOpenTime();
/* 504 */     returnValue = func.execute(id);
/* 505 */     return returnValue;
/*     */   }
/*     */ 
/*     */   public void MOrderPrint(int id)
/*     */     throws MTAgentException
/*     */   {
/* 518 */     if ((Thread.currentThread().getId() != this.agentExecutorThreadId) && (!isTestMode())) {
/* 519 */       throw new MTAgentException(-19, "ARSP_THREAD_INCORRECT_MSG");
/*     */     }
/* 521 */     MOrderPrint func = new MOrderPrint();
/* 522 */     func.execute(id);
/*     */   }
/*     */ 
/*     */   public double MOrderProfit(int id)
/*     */     throws MTAgentException
/*     */   {
/* 533 */     double returnValue = 0.0D;
/* 534 */     if ((Thread.currentThread().getId() != this.agentExecutorThreadId) && (!isTestMode())) {
/* 535 */       throw new MTAgentException(-19);
/*     */     }
/*     */ 
/* 538 */     MOrderProfit func = new MOrderProfit();
/* 539 */     returnValue = func.execute(id);
/* 540 */     return returnValue;
/*     */   }
/*     */ 
/*     */   public synchronized boolean MOrderSelect(int id, int index, int select, int pool)
/*     */     throws MTAgentException
/*     */   {
/* 568 */     boolean returnValue = false;
/* 569 */     if ((Thread.currentThread().getId() != this.agentExecutorThreadId) && (!isTestMode())) {
/* 570 */       throw new MTAgentException(-19, "ARSP_THREAD_INCORRECT_MSG");
/*     */     }
/*     */ 
/* 573 */     MOrderSelect func = new MOrderSelect();
/*     */     try {
/* 575 */       returnValue = func.execute(id, index, select, pool);
/*     */     }
/*     */     catch (Exception ex) {
/* 578 */       log.error(ex.getMessage(), ex);
/*     */     }
/* 580 */     return returnValue;
/*     */   }
/*     */ 
/*     */   public int MOrderSend(int id, String symbol, int cmd, double volume, double price, int slippage, double stoploss, double takeprofit, String comment, int magic, long expiration, long arrow_color)
/*     */     throws MTAgentException
/*     */   {
/* 615 */     if ((Thread.currentThread().getId() != this.agentExecutorThreadId) && (!isTestMode())) {
/* 616 */       throw new MTAgentException(-19, "ARSP_THREAD_INCORRECT_MSG");
/*     */     }
/*     */ 
/* 619 */     MOrderSend func = new MOrderSend();
/*     */ 
/* 621 */     String label = func.execute(id, symbol, cmd, volume, price, slippage, stoploss, takeprofit, comment, magic, expiration, arrow_color);
/*     */ 
/* 624 */     int orderId = -1;
/* 625 */     long start = System.currentTimeMillis();
/* 626 */     synchronized (this) {
/*     */       try {
/* 628 */         long end = System.currentTimeMillis();
/*     */ 
/* 630 */         wait(WAIT_PERIOD);
/* 631 */         end = System.currentTimeMillis();
/*     */       }
/*     */       catch (InterruptedException ex) {
/* 634 */         setError(Integer.valueOf(id), 4060, "ERR_FUNCTION_NOT_CONFIRMED_MSG");
/*     */ 
/* 636 */         log.error("Interrupt MOrderSend", ex);
/*     */       }
/* 638 */       if (notifMsgs.containsKey(label)) {
/* 639 */         ProtocolMessage msg = (ProtocolMessage)notifMsgs.get(label);
/* 640 */         if ((msg instanceof OrderGroupMessage)) {
/* 641 */           OrderGroupMessage orderGroupMessage = (OrderGroupMessage)msg;
/* 642 */           orderId = Integer.valueOf(orderGroupMessage.getOrderGroupId()).intValue();
/*     */ 
/* 644 */           setError(Integer.valueOf(id), 0, "ERR_NO_ERROR_MSG");
/*     */         }
/* 647 */         else if ((msg instanceof NotificationMessage)) {
/* 648 */           NotificationMessage notificationMessage = (NotificationMessage)msg;
/* 649 */           setError(Integer.valueOf(id), 1, notificationMessage.getText());
/*     */         }
/* 651 */         notifMsgs.remove(label);
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 656 */     return orderId;
/*     */   }
/*     */ 
/*     */   public synchronized int MOrdersHistoryTotal(int id)
/*     */     throws MTAgentException
/*     */   {
/* 666 */     int returnValue = 0;
/* 667 */     if ((Thread.currentThread().getId() != this.agentExecutorThreadId) && (!isTestMode())) {
/* 668 */       throw new MTAgentException(-19);
/*     */     }
/*     */ 
/* 671 */     MOrdersHistoryTotal func = new MOrdersHistoryTotal();
/* 672 */     returnValue = func.execute(id);
/* 673 */     return returnValue;
/*     */   }
/*     */ 
/*     */   public double MOrderStopLoss(int id)
/*     */     throws MTAgentException
/*     */   {
/* 682 */     double returnValue = 0.0D;
/* 683 */     if ((Thread.currentThread().getId() != this.agentExecutorThreadId) && (!isTestMode())) {
/* 684 */       throw new MTAgentException(-19);
/*     */     }
/*     */ 
/* 687 */     MOrderStopLoss func = new MOrderStopLoss();
/* 688 */     returnValue = func.execute(id);
/* 689 */     return returnValue;
/*     */   }
/*     */ 
/*     */   public int MOrdersTotal(int id)
/*     */     throws MTAgentException
/*     */   {
/* 698 */     int returnValue = 0;
/* 699 */     if ((Thread.currentThread().getId() != this.agentExecutorThreadId) && (!isTestMode())) {
/* 700 */       throw new MTAgentException(-19);
/*     */     }
/*     */ 
/* 703 */     MOrdersTotal func = new MOrdersTotal();
/* 704 */     returnValue = func.execute(id);
/* 705 */     return returnValue;
/*     */   }
/*     */ 
/*     */   public double MOrderSwap(int id)
/*     */     throws MTAgentException
/*     */   {
/* 714 */     double returnValue = 0.0D;
/* 715 */     if ((Thread.currentThread().getId() != this.agentExecutorThreadId) && (!isTestMode())) {
/* 716 */       throw new MTAgentException(-19, "ARSP_THREAD_INCORRECT_MSG");
/*     */     }
/* 718 */     MOrderSwap func = new MOrderSwap();
/* 719 */     returnValue = func.execute(id);
/* 720 */     return returnValue;
/*     */   }
/*     */ 
/*     */   public String MOrderSymbol(int id)
/*     */     throws MTAgentException
/*     */   {
/* 730 */     String returnValue = "";
/* 731 */     if ((Thread.currentThread().getId() != this.agentExecutorThreadId) && (!isTestMode())) {
/* 732 */       throw new MTAgentException(-19);
/*     */     }
/*     */ 
/* 735 */     MOrderSymbol func = new MOrderSymbol();
/* 736 */     returnValue = func.execute(id);
/* 737 */     return returnValue;
/*     */   }
/*     */ 
/*     */   public double MOrderTakeProfit(int id)
/*     */     throws MTAgentException
/*     */   {
/* 746 */     double returnValue = 0.0D;
/* 747 */     if ((Thread.currentThread().getId() != this.agentExecutorThreadId) && (!isTestMode())) {
/* 748 */       throw new MTAgentException(-19);
/*     */     }
/*     */ 
/* 751 */     MOrderTakeProfit func = new MOrderTakeProfit();
/* 752 */     returnValue = func.execute(id);
/* 753 */     return returnValue;
/*     */   }
/*     */ 
/*     */   public int MOrderTicket(int id)
/*     */     throws MTAgentException
/*     */   {
/* 762 */     int returnValue = 0;
/* 763 */     if ((Thread.currentThread().getId() != this.agentExecutorThreadId) && (!isTestMode())) {
/* 764 */       throw new MTAgentException(-19);
/*     */     }
/* 766 */     MOrderTicket func = new MOrderTicket();
/* 767 */     returnValue = func.execute(id);
/* 768 */     return returnValue;
/*     */   }
/*     */ 
/*     */   public int MOrderType(int id)
/*     */     throws MTAgentException
/*     */   {
/* 782 */     int returnValue = 0;
/* 783 */     if ((Thread.currentThread().getId() != this.agentExecutorThreadId) && (!isTestMode())) {
/* 784 */       throw new MTAgentException(-19);
/*     */     }
/* 786 */     MOrderType func = new MOrderType();
/* 787 */     returnValue = func.execute(id);
/* 788 */     return returnValue;
/*     */   }
/*     */ 
/*     */   public int MGetLastError(int id)
/*     */     throws MTAgentException
/*     */   {
/* 799 */     int returnValue = 0;
/* 800 */     if ((Thread.currentThread().getId() != this.agentExecutorThreadId) && (!isTestMode())) {
/* 801 */       returnValue = 4105;
/* 802 */       throw new MTAgentException(-19);
/*     */     }
/*     */ 
/* 805 */     Integer mtId = new Integer(id);
/* 806 */     synchronized (errorCodes) {
/* 807 */       if (!errorCodes.containsKey(mtId)) {
/* 808 */         setError(mtId, 4105, "ERR_NO_ORDER_SELECTED_MSG");
/*     */ 
/* 810 */         throw new MTAgentException(-13, "ERR_NO_ORDER_SELECTED_MSG");
/*     */       }
/*     */ 
/* 814 */       if (errorCodes.containsKey(mtId)) {
/* 815 */         Integer code = (Integer)errorCodes.get(mtId);
/* 816 */         returnValue = code.intValue();
/*     */       }
/*     */     }
/* 819 */     return returnValue;
/*     */   }
/*     */ 
/*     */   public boolean MIsConnected(int id) throws MTAgentException {
/* 823 */     return true;
/*     */   }
/*     */ 
/*     */   public boolean MIsDemo(int id) throws MTAgentException {
/* 827 */     return true;
/*     */   }
/*     */ 
/*     */   public boolean MIsDllsAllowed(int id) throws MTAgentException {
/* 831 */     return true;
/*     */   }
/*     */ 
/*     */   public boolean MIsExpertEnabled(int id) throws MTAgentException {
/* 835 */     return true;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.mt.Agent
 * JD-Core Version:    0.6.0
 */