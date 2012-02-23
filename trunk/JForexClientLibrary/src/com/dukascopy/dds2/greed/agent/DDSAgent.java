/*     */ package com.dukascopy.dds2.greed.agent;
/*     */ 
/*     */ import com.dukascopy.api.ITick;
/*     */ import com.dukascopy.dds2.greed.GreedContext;
/*     */ import com.dukascopy.dds2.greed.actions.CancelOrderAction;
/*     */ import com.dukascopy.dds2.greed.actions.OrderGroupCloseAction;
/*     */ import com.dukascopy.dds2.greed.agent.history.HTick;
/*     */ import com.dukascopy.dds2.greed.agent.strategy.StratUtils;
/*     */ import com.dukascopy.dds2.greed.agent.strategy.objects.Market;
/*     */ import com.dukascopy.dds2.greed.api.OrderFactory;
/*     */ import com.dukascopy.dds2.greed.api.OrderFactory.EntryOrderResultCode;
/*     */ import com.dukascopy.dds2.greed.gui.ClientForm;
/*     */ import com.dukascopy.dds2.greed.gui.component.AccountStatementPanel;
/*     */ import com.dukascopy.dds2.greed.gui.component.dialog.CustomRequestDialog;
/*     */ import com.dukascopy.dds2.greed.gui.component.orders.OrderCommonTableModel;
/*     */ import com.dukascopy.dds2.greed.gui.component.orders.OrdersPanel;
/*     */ import com.dukascopy.dds2.greed.gui.component.orders.OrdersTable;
/*     */ import com.dukascopy.dds2.greed.gui.component.positions.PositionsPanel;
/*     */ import com.dukascopy.dds2.greed.gui.component.positions.PositionsTable;
/*     */ import com.dukascopy.dds2.greed.gui.component.positions.PositionsTableModel;
/*     */ import com.dukascopy.dds2.greed.gui.component.status.GreedStatusBar;
/*     */ import com.dukascopy.dds2.greed.gui.component.table.TableSorter;
/*     */ import com.dukascopy.dds2.greed.model.MarketView;
/*     */ import com.dukascopy.transport.common.model.type.Money;
/*     */ import com.dukascopy.transport.common.model.type.OfferSide;
/*     */ import com.dukascopy.transport.common.model.type.OrderDirection;
/*     */ import com.dukascopy.transport.common.model.type.OrderSide;
/*     */ import com.dukascopy.transport.common.model.type.OrderState;
/*     */ import com.dukascopy.transport.common.model.type.Position;
/*     */ import com.dukascopy.transport.common.model.type.StopDirection;
/*     */ import com.dukascopy.transport.common.msg.ProtocolMessage;
/*     */ import com.dukascopy.transport.common.msg.group.OrderGroupMessage;
/*     */ import com.dukascopy.transport.common.msg.group.OrderMessage;
/*     */ import com.dukascopy.transport.common.msg.request.CurrencyOffer;
/*     */ import com.dukascopy.transport.common.msg.response.NotificationMessage;
/*     */ import java.io.File;
/*     */ import java.io.FileNotFoundException;
/*     */ import java.io.FileOutputStream;
/*     */ import java.io.InputStream;
/*     */ import java.math.BigDecimal;
/*     */ import java.net.BindException;
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.concurrent.Callable;
/*     */ import java.util.concurrent.ExecutorService;
/*     */ import java.util.concurrent.Executors;
/*     */ import java.util.concurrent.Future;
/*     */ import java.util.concurrent.ThreadFactory;
/*     */ import javax.swing.JCheckBox;
/*     */ import javax.swing.SwingUtilities;
/*     */ import org.slf4j.Logger;
/*     */ import org.slf4j.LoggerFactory;
/*     */ 
/*     */ public class DDSAgent extends BaseAgent
/*     */ {
/*  70 */   private static final Logger LOGGER = LoggerFactory.getLogger(DDSAgent.class);
/*     */ 
/* 117 */   private ClientForm clientForm = null;
/*     */ 
/* 121 */   private final SweetLock cancelStopLock = new SweetLock(null);
/*     */ 
/* 124 */   private final SweetLock closePositionLock = new SweetLock(null);
/*     */ 
/* 127 */   private final SweetLock fillPositionLock = new SweetLock(null);
/*     */ 
/* 129 */   private final SweetLock placeOfferLock = new SweetLock(null);
/*     */ 
/* 131 */   private final SweetLock pendingPositionLock = new SweetLock(null);
/*     */ 
/* 135 */   private final SweetLock stopPositionSLLock = new SweetLock(null);
/*     */ 
/* 137 */   private final SweetLock stopPositionTPLock = new SweetLock(null);
/*     */ 
/* 139 */   private PositionsTable positionsTable = null;
/*     */ 
/* 141 */   private Runnable serverRunnable = null;
/*     */ 
/* 143 */   private Thread serverThread = null;
/*     */ 
/* 187 */   private long agentExecutorThreadId = 0L;
/*     */ 
/* 569 */   private ExecutorService executorService = null;
/*     */ 
/* 724 */   private Map<String, ITick> lastTick = new HashMap();
/*     */ 
/* 744 */   private String waitingForQuote = null;
/* 745 */   private SweetLock tickLock = new SweetLock(null);
/*     */ 
/*     */   public DDSAgent()
/*     */   {
/*     */     try
/*     */     {
/* 147 */       updateDll();
/*     */ 
/* 149 */       this.clientForm = ((ClientForm)GreedContext.get("clientGui"));
/*     */ 
/* 154 */       PositionsPanel positionsPanel = this.clientForm.getPositionsPanel();
/* 155 */       this.positionsTable = positionsPanel.getTable();
/*     */ 
/* 166 */       JCheckBox oneClickCheckbox = this.clientForm.getStatusBar().getAccountStatement().getOneClickCheckbox();
/* 167 */       while (!oneClickCheckbox.isVisible()) {
/* 168 */         Thread.sleep(1000L);
/*     */       }
/* 170 */       if (oneClickCheckbox.isSelected()) {
/* 171 */         oneClickCheckbox.setSelected(false);
/*     */       }
/*     */ 
/* 174 */       restartExecutorService();
/* 175 */       this.serverRunnable = new AgentServerRunnable(this);
/* 176 */       restart();
/*     */     }
/*     */     catch (BindException bindException)
/*     */     {
/* 181 */       LOGGER.info("Unable to load DDS Agent: " + bindException.getMessage());
/*     */     } catch (Throwable e) {
/* 183 */       LOGGER.error("Unable to load DDS Agent: " + e.getMessage(), e);
/*     */     }
/*     */   }
/*     */ 
/*     */   private void restartExecutorService()
/*     */   {
/* 190 */     this.cancelStopLock.unfreeze(-12);
/* 191 */     this.closePositionLock.unfreeze(-12);
/* 192 */     this.fillPositionLock.unfreeze(-12);
/* 193 */     this.placeOfferLock.unfreeze(-12);
/* 194 */     this.pendingPositionLock.unfreeze(-12);
/*     */ 
/* 196 */     this.stopPositionSLLock.unfreeze(-12);
/* 197 */     this.stopPositionTPLock.unfreeze(-12);
/*     */ 
/* 199 */     if (this.executorService != null) {
/* 200 */       this.executorService.shutdownNow();
/*     */     }
/* 202 */     this.executorService = Executors.newSingleThreadExecutor(new ThreadFactory() {
/*     */       public Thread newThread(Runnable r) {
/* 204 */         Thread thread = new Thread(r);
/* 205 */         thread.setName("Agent Executor");
/* 206 */         DDSAgent.access$202(DDSAgent.this, thread.getId());
/* 207 */         return thread;
/*     */       }
/*     */     });
/*     */   }
/*     */ 
/*     */   protected void checkLiquidity(String symbol) throws AgentException
/*     */   {
/* 215 */     MarketView marketView = (MarketView)GreedContext.get("marketView");
/* 216 */     CurrencyOffer bestASKOffer = marketView.getBestOffer(symbol, OfferSide.ASK);
/* 217 */     CurrencyOffer bestBIDOffer = marketView.getBestOffer(symbol, OfferSide.BID);
/*     */ 
/* 219 */     if ((bestASKOffer == null) || (bestASKOffer.getPrice() == null) || (bestBIDOffer == null) || (bestBIDOffer.getPrice() == null))
/*     */     {
/* 222 */       throw new AgentException(-17);
/*     */     }
/*     */   }
/*     */ 
/*     */   protected OrderGroupMessage getEntryOrderByLabelImpl(String label)
/*     */   {
/* 229 */     if (Thread.currentThread().getId() != this.agentExecutorThreadId) {
/* 230 */       throw new AgentException(-19);
/*     */     }
/* 232 */     OrderGroupMessage[] rc = { null };
/* 233 */     if (label != null) {
/*     */       try {
/* 235 */         SwingUtilities.invokeAndWait(new Runnable(label, rc)
/*     */         {
/*     */           public void run() {
/* 238 */             OrdersPanel ordersPanel = DDSAgent.this.clientForm.getOrdersPanel();
/* 239 */             OrdersTable ordersTable = ordersPanel.getOrdersTable();
/* 240 */             TableSorter tableSorter = (TableSorter)ordersTable.getModel();
/* 241 */             OrderCommonTableModel orderTableModel = (OrderCommonTableModel)tableSorter.getTableModel();
/* 242 */             for (int i = 0; i < orderTableModel.getRowCount(); i++) {
/* 243 */               OrderGroupMessage ogm = orderTableModel.getGroup(i);
/* 244 */               OrderMessage orderMessage = ogm.getOpeningOrder();
/* 245 */               if ((orderMessage == null) || 
/* 246 */                 (!this.val$label.equals(orderMessage.getExternalSysId()))) continue;
/* 247 */               ordersTable.setRowSelectionInterval(i, i);
/* 248 */               this.val$rc[0] = ogm;
/* 249 */               break;
/*     */             }
/*     */           }
/*     */         });
/*     */       }
/*     */       catch (Exception e) {
/* 256 */         LOGGER.error(e.getMessage(), e);
/*     */       }
/*     */     }
/* 259 */     return rc[0];
/*     */   }
/*     */ 
/*     */   protected Position getPositionByLabelImpl(String label)
/*     */   {
/* 264 */     if (Thread.currentThread().getId() != this.agentExecutorThreadId) {
/* 265 */       throw new AgentException(-19);
/*     */     }
/* 267 */     Position rc = null;
/* 268 */     for (Position position : getPositionsListImpl(false, null)) {
/* 269 */       OrderGroupMessage ogm = position.getOrderGroup();
/* 270 */       OrderMessage orderMessage = ogm.getOpeningOrder();
/* 271 */       if ((orderMessage != null) && (
/* 272 */         (ogm.getOrderGroupId().equals(label)) || (label.equals(orderMessage.getExternalSysId()))))
/*     */       {
/* 274 */         rc = position;
/* 275 */         break;
/*     */       }
/*     */     }
/*     */ 
/* 279 */     return rc;
/*     */   }
/*     */ 
/*     */   private void restart()
/*     */   {
/* 284 */     this.serverThread = new Thread(this.serverRunnable);
/* 285 */     this.serverThread.setDaemon(true);
/* 286 */     this.serverThread.setName("DDS Agent");
/* 287 */     this.serverThread.start();
/*     */   }
/*     */ 
/*     */   protected int submitEntryOrderImpl(String label, String symbol, int cmd, OrderSide orderSide, double amount, double price, StopDirection stopDirection, BigDecimal stopLossPrice, BigDecimal takeProfitPrice, String buttonLabeli, String comment)
/*     */   {
/* 293 */     if (Thread.currentThread().getId() != this.agentExecutorThreadId) {
/* 294 */       throw new AgentException(-19);
/*     */     }
/* 296 */     StopDirection stopLossDirecton = orderSide == OrderSide.BUY ? StopDirection.BID_LESS : StopDirection.ASK_GREATER;
/* 297 */     StopDirection takeProfitDirecton = orderSide == OrderSide.BUY ? StopDirection.BID_GREATER : StopDirection.ASK_LESS;
/* 298 */     String slippage = this.slipageControl >= 0.0D ? normalizePriceString(format(this.slipageControl)) : null;
/* 299 */     if ((cmd == 2) || (cmd == 3) || (cmd == 6) || (cmd == 7)) {
/* 300 */       slippage = "0";
/*     */     }
/* 302 */     OrderFactory.createEntryOrder(this, slippage, BigDecimal.valueOf(amount), orderSide, symbol, null, BigDecimal.valueOf(price), stopDirection, stopLossPrice, null, takeProfitPrice, null, label, null, "DLL Agent");
/* 303 */     int rc = this.pendingPositionLock.freeze("");
/* 304 */     if (rc > 0) {
/* 305 */       waitForEntry(label);
/*     */     }
/* 307 */     return rc;
/*     */   }
/*     */ 
/*     */   protected int submitMarketOrderImpl(String label, String symbol, double amount, double price, OrderSide orderSide, String comment)
/*     */   {
/* 312 */     if (Thread.currentThread().getId() != this.agentExecutorThreadId) {
/* 313 */       throw new AgentException(-19);
/*     */     }
/*     */ 
/* 316 */     String slippage = "5";
/* 317 */     if (this.slipageControl >= 0.0D) {
/* 318 */       slippage = normalizePriceString(format(this.slipageControl));
/*     */     }
/* 320 */     OrderFactory.quickie(this, slippage, BigDecimal.valueOf(amount * 1000000.0D), BigDecimal.valueOf(price), orderSide, symbol, label, null, comment);
/*     */ 
/* 325 */     int id = this.fillPositionLock.freeze("");
/*     */ 
/* 327 */     if (id > 0) {
/* 328 */       waitForPosition(label);
/*     */     }
/*     */ 
/* 331 */     return id;
/*     */   }
/*     */ 
/*     */   protected void submitStopImpl(OrderGroupMessage orderGroup, String openingOrderId, String stopOrderType, StopDirection stopDirection, String normailzedPriceStopStr)
/*     */   {
/* 336 */     if (Thread.currentThread().getId() != this.agentExecutorThreadId) {
/* 337 */       throw new AgentException(-19);
/*     */     }
/*     */ 
/* 340 */     OrderFactory.EntryOrderResultCode rc = null;
/*     */ 
/* 342 */     if ("STOP_LOSS".equals(stopOrderType)) {
/* 343 */       rc = OrderFactory.addStopLoss(this, orderGroup.getOrderGroupId(), openingOrderId, new BigDecimal(normailzedPriceStopStr), stopDirection, null);
/* 344 */       if (rc == OrderFactory.EntryOrderResultCode.OK) {
/* 345 */         int rci = this.stopPositionSLLock.freeze(orderGroup.getOrderGroupId());
/* 346 */         if (rci > 0)
/*     */         {
/* 348 */           waitForStop(rci + "", true);
/*     */         }
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 354 */     if ("TAKE_PROFIT".equals(stopOrderType)) {
/* 355 */       rc = OrderFactory.addTakeProfit(this, orderGroup.getOrderGroupId(), openingOrderId, new BigDecimal(normailzedPriceStopStr), stopDirection);
/* 356 */       int rci = this.stopPositionTPLock.freeze(orderGroup.getOrderGroupId());
/* 357 */       if (rci > 0)
/*     */       {
/* 359 */         waitForStop(rci + "", false);
/*     */       }
/*     */     }
/*     */ 
/* 363 */     if (rc != OrderFactory.EntryOrderResultCode.OK)
/* 364 */       LOGGER.warn("DDSAgent.submitStopImpl return " + rc);
/*     */   }
/*     */ 
/*     */   private void waitForStop(String groupId, boolean needStopLoss)
/*     */   {
/* 373 */     for (int i = 0; i < 20; i++) {
/* 374 */       List list = getPositionsListImpl(true, null);
/* 375 */       for (Position position : list) {
/* 376 */         OrderGroupMessage group = position.getOrderGroup();
/* 377 */         if (group.getOrderGroupId().equals(groupId)) {
/* 378 */           OrderMessage order = needStopLoss ? group.getStopLossOrder() : group.getTakeProfitOrder();
/* 379 */           if (order != null)
/*     */             return;
/*     */         }
/*     */       }
/*     */       try {
/* 385 */         Thread.sleep(100L);
/*     */       } catch (InterruptedException e) {
/* 387 */         LOGGER.error(e.getMessage(), e);
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   protected void submitStopCancelImpl(String groupId, int command)
/*     */   {
/* 394 */     if (Thread.currentThread().getId() != this.agentExecutorThreadId) {
/* 395 */       throw new AgentException(-19);
/*     */     }
/*     */ 
/* 398 */     OrderGroupMessage groupMessage = getOrderGroupByIdImpl(groupId);
/* 399 */     OrderMessage slMessage = groupMessage.getStopLossOrder();
/* 400 */     OrderMessage tpMessage = groupMessage.getTakeProfitOrder();
/*     */ 
/* 402 */     if ((command == 0) && (slMessage != null))
/*     */     {
/* 404 */       CancelOrderAction.cancelOrderById(this, slMessage.getOrderId());
/*     */     }
/* 406 */     if ((command == 1) && (tpMessage != null))
/*     */     {
/* 408 */       CancelOrderAction.cancelOrderById(this, tpMessage.getOrderId());
/*     */     }
/*     */   }
/*     */ 
/*     */   protected OrderGroupMessage getOrderGroupByIdImpl(String groupId)
/*     */   {
/* 415 */     if (Thread.currentThread().getId() != this.agentExecutorThreadId) {
/* 416 */       throw new AgentException(-19);
/*     */     }
/* 418 */     OrderGroupMessage[] group = { null };
/*     */     try {
/* 420 */       SwingUtilities.invokeAndWait(new Runnable(group, groupId) {
/*     */         public void run() {
/* 422 */           this.val$group[0] = DDSAgent.access$300(DDSAgent.this).getOrdersPanel().getOrderGroup(this.val$groupId);
/*     */         } } );
/*     */     } catch (Exception e) {
/* 426 */       LOGGER.error(e.getMessage(), e);
/*     */     }
/* 428 */     return group[0];
/*     */   }
/*     */ 
/*     */   private void updateDll()
/*     */   {
/* 433 */     String jlp = System.getProperty("java.library.path");
/* 434 */     String[] paths = jlp.split(File.pathSeparator);
/* 435 */     File targetFile = null;
/* 436 */     for (int i = 0; i < paths.length; i++) {
/* 437 */       if (paths[i].endsWith("system32")) {
/* 438 */         targetFile = new File(paths[i] + File.separator + "DDS2Agent.dll");
/* 439 */         break;
/*     */       }
/*     */     }
/* 442 */     if (targetFile != null)
/*     */       try
/*     */       {
/* 445 */         InputStream stream = Thread.currentThread().getContextClassLoader().getResourceAsStream("DDS2Agent.dll");
/* 446 */         if ((stream != null) && (stream.available() > 0)) {
/* 447 */           if (targetFile.exists()) {
/* 448 */             targetFile.delete();
/* 449 */             targetFile.createNewFile();
/*     */           }
/* 451 */           FileOutputStream outputStream = new FileOutputStream(targetFile);
/* 452 */           StratUtils.turboPipe(stream, outputStream);
/* 453 */           outputStream.close();
/* 454 */           stream.close();
/*     */         }
/*     */       }
/*     */       catch (FileNotFoundException fe) {
/* 458 */         LOGGER.warn(fe.getMessage());
/*     */       } catch (Exception e) {
/* 460 */         LOGGER.error(e.getMessage(), e);
/*     */       }
/*     */   }
/*     */ 
/*     */   private void waitForPosition(String label)
/*     */   {
/*     */     try
/*     */     {
/* 472 */       int counter = 0;
/*     */ 
/* 474 */       while (counter++ < 300) {
/* 475 */         Position position = getPositionByLabelImpl(label);
/* 476 */         if (position != null) {
/* 477 */           if (position.getOrderGroup().getOpeningOrder().getOrderState() == OrderState.FILLED) {
/* 478 */             break;
/*     */           }
/*     */         }
/*     */         else
/* 482 */           Thread.sleep(100L);
/*     */       }
/*     */     }
/*     */     catch (Exception e) {
/*     */     }
/*     */   }
/*     */ 
/*     */   private void waitForEntry(String label) {
/*     */     try {
/* 491 */       int counter = 0;
/*     */ 
/* 493 */       while (counter++ < 300) {
/* 494 */         OrderGroupMessage entry = getEntryOrderByLabelImpl(label);
/* 495 */         if (entry != null) {
/* 496 */           if (entry.getOpeningOrder().getOrderState() == OrderState.PENDING) {
/* 497 */             break;
/*     */           }
/*     */         }
/*     */         else
/* 501 */           Thread.sleep(100L);
/*     */       }
/*     */     }
/*     */     catch (Exception e) {
/*     */     }
/*     */   }
/*     */ 
/*     */   public int cancelOrder(String label) {
/* 509 */     if (Thread.currentThread().getId() != this.agentExecutorThreadId) {
/* 510 */       throw new AgentException(-19);
/*     */     }
/* 512 */     OrderGroupMessage entry = getEntryOrderByLabelImpl(label);
/* 513 */     if (entry != null) {
/*     */       try {
/* 515 */         CancelOrderAction.cancelOrderById(this, entry.getOpeningOrder().getOrderId());
/*     */       } catch (Exception e) {
/* 517 */         LOGGER.error(e.getMessage(), e);
/*     */       }
/*     */ 
/* 520 */       return this.closePositionLock.freeze(entry.getOrderGroupId());
/*     */     }
/* 522 */     return -13;
/*     */   }
/*     */ 
/*     */   public int closePosition(String label) {
/* 526 */     return closePosition(label, 0.0D, 0.0D);
/*     */   }
/*     */ 
/*     */   public int closePosition(String label, double price, double amount) {
/* 530 */     if (Thread.currentThread().getId() != this.agentExecutorThreadId) {
/* 531 */       throw new AgentException(-19);
/*     */     }
/*     */ 
/* 534 */     String positionId = getPositionIdByLabel(label);
/* 535 */     if (positionId != null)
/*     */     {
/* 537 */       Money priceM = null;
/* 538 */       Money amountM = null;
/* 539 */       if (amount > 0.0D) {
/* 540 */         amount *= 1000000.0D;
/* 541 */         amountM = new Money(amount + "", "USD");
/*     */       }
/* 543 */       if (price > 0.0D) {
/* 544 */         priceM = new Money(price + "", "USD");
/*     */       }
/*     */ 
/* 547 */       OrderGroupCloseAction.closePositionById(this, positionId, priceM, amountM, null);
/* 548 */       return this.closePositionLock.freeze(positionId);
/*     */     }
/* 550 */     return -13;
/*     */   }
/*     */ 
/*     */   public void onErrorMessage(ProtocolMessage protocolMessage)
/*     */   {
/* 555 */     if ((protocolMessage instanceof NotificationMessage)) {
/* 556 */       NotificationMessage notificationMessage = (NotificationMessage)protocolMessage;
/* 557 */       if (notificationMessage.getLevel().equals("WARNING")) {
/* 558 */         if (this.closePositionLock.getId().equals(notificationMessage.getPositionId())) {
/* 559 */           this.closePositionLock.unfreeze(-17);
/* 560 */           return;
/*     */         }
/*     */ 
/* 563 */         this.fillPositionLock.unfreeze(-17);
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public void onNotifyMessage(ProtocolMessage protocolMessage)
/*     */   {
/* 572 */     NotificationMessage notificationMessage = (NotificationMessage)protocolMessage;
/* 573 */     if (notificationMessage.getLevel().equals("WARNING"))
/* 574 */       this.fillPositionLock.unfreeze(-17);
/*     */   }
/*     */ 
/*     */   public static String generateLabel(OrderMessage orderMessage)
/*     */   {
/* 581 */     return StratUtils.generateLabel();
/*     */   }
/*     */ 
/*     */   public void onOrderGroupReceived(OrderGroupMessage orderGroup)
/*     */   {
/* 594 */     if (orderGroup == null) {
/* 595 */       return;
/*     */     }
/*     */ 
/* 598 */     for (OrderMessage order : orderGroup.getOrders()) {
/* 599 */       if ((order.getOrderDirection() == OrderDirection.CLOSE) && (order.getPriceStop() != null) && (OrderState.PENDING.equals(order.getOrderState()))) {
/* 600 */         int fillPositionId = 0;
/*     */         try {
/* 602 */           fillPositionId = Integer.parseInt(orderGroup.getOrderGroupId());
/*     */         } catch (Exception e) {
/* 604 */           LOGGER.error(e.getMessage(), e);
/*     */         }
/* 606 */         if (order.isStopLoss()) {
/* 607 */           this.stopPositionSLLock.unfreeze(fillPositionId);
/*     */         }
/* 609 */         if (order.isTakeProfit()) {
/* 610 */           this.stopPositionTPLock.unfreeze(fillPositionId);
/*     */         }
/*     */       }
/* 613 */       if ((order.getOrderDirection() == OrderDirection.CLOSE) && (order.getPriceStop() != null) && (OrderState.CANCELLED.equals(order.getOrderState()))) {
/* 614 */         int fillPositionId = 0;
/*     */         try {
/* 616 */           fillPositionId = Integer.parseInt(orderGroup.getOrderGroupId());
/*     */         } catch (Exception e) {
/* 618 */           LOGGER.error(e.getMessage(), e);
/*     */         }
/* 620 */         this.cancelStopLock.unfreeze(fillPositionId);
/*     */       }
/*     */     }
/* 623 */     OrderMessage orderMessage = orderGroup.getOpeningOrder();
/* 624 */     if ((orderMessage != null) && (orderMessage.getOrderDirection() == OrderDirection.OPEN) && (orderMessage.getOrderState() == OrderState.FILLED)) {
/* 625 */       int fillPositionId = 0;
/*     */       try {
/* 627 */         fillPositionId = Integer.parseInt(orderGroup.getOrderGroupId());
/*     */       } catch (Exception e) {
/* 629 */         LOGGER.error(e.getMessage(), e);
/*     */       }
/* 631 */       if (orderMessage.getExternalSysId() == null) {
/* 632 */         orderMessage.setExternalSysId(generateLabel(orderMessage));
/*     */       }
/* 634 */       this.fillPositionLock.unfreeze(fillPositionId);
/*     */     }
/* 636 */     if ((orderMessage != null) && (orderMessage.getOrderDirection() == OrderDirection.OPEN) && ((orderMessage.getOrderState() == OrderState.PENDING) || (orderMessage.getOrderState() == OrderState.EXECUTING))) {
/* 637 */       int pendingPositionId = 0;
/*     */       try {
/* 639 */         pendingPositionId = Integer.parseInt(orderGroup.getOrderGroupId());
/*     */       } catch (Exception e) {
/* 641 */         LOGGER.error(e.getMessage(), e);
/*     */       }
/* 643 */       this.pendingPositionLock.unfreeze(pendingPositionId);
/*     */     }
/* 645 */     if ((orderGroup.getOrders() == null) || (orderGroup.getOrders().size() == 0)) {
/* 646 */       int closePositionId = 0;
/*     */       try {
/* 648 */         closePositionId = Integer.parseInt(orderGroup.getOrderGroupId());
/*     */       } catch (Exception e) {
/* 650 */         LOGGER.error(e.getMessage(), e);
/*     */       }
/* 652 */       this.closePositionLock.unfreeze(closePositionId);
/*     */     }
/*     */ 
/* 655 */     if ((orderMessage != null) && (orderMessage.getOrderDirection() == OrderDirection.OPEN) && (orderMessage.getOrderState() == OrderState.EXECUTING) && (orderMessage.isPlaceOffer())) {
/* 656 */       int fillPositionId = 0;
/*     */       try {
/* 658 */         fillPositionId = Integer.parseInt(orderGroup.getOrderGroupId());
/*     */       } catch (Exception e) {
/* 660 */         LOGGER.error(e.getMessage(), e);
/*     */       }
/* 662 */       this.placeOfferLock.unfreeze(fillPositionId);
/*     */     }
/*     */   }
/*     */ 
/*     */   public int placeOffer(String label, String symbol, int cmd, double amount, double price, int goodForMinutes) throws AgentException {
/* 667 */     if (Thread.currentThread().getId() != this.agentExecutorThreadId) {
/* 668 */       throw new AgentException(-19);
/*     */     }
/*     */ 
/* 671 */     checkLabelUniqueness(label);
/* 672 */     String symbol_1 = StratUtils.normalizeSymbol(symbol);
/* 673 */     validateAmount(amount);
/* 674 */     checkLiquidity(symbol);
/*     */ 
/* 676 */     OfferSide[] requestSide = { null };
/* 677 */     if (cmd == 0)
/* 678 */       requestSide[0] = OfferSide.BID;
/* 679 */     else if (cmd == 1)
/* 680 */       requestSide[0] = OfferSide.ASK;
/*     */     else {
/* 682 */       return -10;
/*     */     }
/*     */ 
/* 685 */     if (goodForMinutes < 0) {
/* 686 */       return -18;
/*     */     }
/*     */ 
/* 689 */     boolean[] isOk = { false };
/*     */     try {
/* 691 */       SwingUtilities.invokeAndWait(new Runnable(requestSide, symbol_1, amount, price, goodForMinutes, isOk, label)
/*     */       {
/*     */         public void run()
/*     */         {
/* 695 */           CustomRequestDialog customRequestDialog = new CustomRequestDialog(this.val$requestSide[0], this.val$symbol_1, null);
/* 696 */           customRequestDialog.setPlaceData(DDSAgent.this.normalizePriceString(BaseAgent.format(this.val$amount)), DDSAgent.this.normalizePriceString(BaseAgent.format(this.val$price)), this.val$goodForMinutes);
/* 697 */           this.val$isOk[0] = customRequestDialog.placeOrder(this.val$label);
/*     */         } } );
/*     */     }
/*     */     catch (Exception e) {
/* 702 */       LOGGER.error(e.getMessage(), e);
/*     */     }
/*     */ 
/* 705 */     if (isOk[0] == 0) {
/* 706 */       return -16;
/*     */     }
/* 708 */     int rc = this.placeOfferLock.freeze("");
/* 709 */     return rc;
/*     */   }
/*     */ 
/*     */   public double getAsk(String instrument)
/*     */   {
/* 727 */     double rc = 0.0D;
/* 728 */     ITick tick = (ITick)this.lastTick.get(instrument);
/* 729 */     if (tick != null) {
/* 730 */       rc = tick.getAsk();
/*     */     }
/* 732 */     return rc;
/*     */   }
/*     */ 
/*     */   public double getBid(String instrument) {
/* 736 */     double rc = 0.0D;
/* 737 */     ITick tick = (ITick)this.lastTick.get(instrument);
/* 738 */     if (tick != null) {
/* 739 */       rc = tick.getBid();
/*     */     }
/* 741 */     return rc;
/*     */   }
/*     */ 
/*     */   public long getTime(String instrument)
/*     */   {
/* 748 */     long rc = 0L;
/* 749 */     this.waitingForQuote = instrument;
/* 750 */     this.tickLock.freeze("");
/* 751 */     ITick tick = (ITick)this.lastTick.get(instrument);
/* 752 */     if (tick != null) {
/* 753 */       rc = tick.getTime();
/*     */     }
/* 755 */     return rc / 1000L;
/*     */   }
/*     */ 
/*     */   protected List<OrderGroupMessage> getEntryOrdersListImpl(boolean filterWithNoTag, String filterSymbol)
/*     */   {
/* 761 */     if (Thread.currentThread().getId() != this.agentExecutorThreadId) {
/* 762 */       throw new AgentException(-19);
/*     */     }
/* 764 */     List rc = new ArrayList();
/*     */     try {
/* 766 */       SwingUtilities.invokeAndWait(new Runnable(rc)
/*     */       {
/*     */         public void run() {
/*     */           try {
/* 770 */             OrdersPanel ordersPanel = DDSAgent.this.clientForm.getOrdersPanel();
/* 771 */             OrdersTable ordersTable = ordersPanel.getOrdersTable();
/* 772 */             TableSorter tableSorter = (TableSorter)ordersTable.getModel();
/* 773 */             OrderCommonTableModel orderTableModel = (OrderCommonTableModel)tableSorter.getTableModel();
/* 774 */             this.val$rc.addAll(orderTableModel.getGroups());
/* 775 */             List groupsToRemove = new ArrayList();
/* 776 */             for (OrderGroupMessage message : this.val$rc) {
/* 777 */               if (message.getOpeningOrder() == null) {
/* 778 */                 groupsToRemove.add(message);
/*     */               }
/*     */             }
/* 781 */             this.val$rc.removeAll(groupsToRemove);
/*     */           } catch (Throwable e) {
/* 783 */             DDSAgent.LOGGER.error(e.getMessage(), e);
/*     */           }
/*     */         } } );
/*     */     } catch (Exception e) {
/* 788 */       LOGGER.error(e.getMessage(), e);
/*     */     }
/* 790 */     List toRemove = new ArrayList();
/* 791 */     if (filterWithNoTag) {
/* 792 */       for (OrderGroupMessage group : rc) {
/* 793 */         if (group.getOpeningOrder().getExternalSysId() == null) {
/* 794 */           toRemove.add(group);
/*     */         }
/*     */       }
/*     */     }
/* 798 */     if (filterSymbol != null) {
/* 799 */       for (OrderGroupMessage group : rc) {
/* 800 */         if (!filterSymbol.equals(group.getInstrument())) {
/* 801 */           toRemove.add(group);
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/* 806 */     rc.removeAll(toRemove);
/* 807 */     return rc;
/*     */   }
/*     */ 
/*     */   protected List<Position> getPositionsListImpl(boolean filterWithNoTag, String filterSymbol)
/*     */   {
/* 814 */     if (Thread.currentThread().getId() != this.agentExecutorThreadId) {
/* 815 */       throw new AgentException(-19);
/*     */     }
/* 817 */     List rc = new ArrayList();
/*     */     try {
/* 819 */       SwingUtilities.invokeAndWait(new Runnable(rc) {
/*     */         public void run() {
/* 821 */           PositionsTableModel positionsTableModel = (PositionsTableModel)DDSAgent.this.positionsTable.getModel();
/* 822 */           this.val$rc.addAll(positionsTableModel.getPositions());
/*     */         } } );
/*     */     } catch (Exception e) {
/* 826 */       LOGGER.error(e.getMessage(), e);
/*     */     }
/*     */ 
/* 829 */     List toRemove = new ArrayList();
/* 830 */     if (filterWithNoTag) {
/* 831 */       for (Position position : rc) {
/* 832 */         if (position.getOrderGroup().getOpeningOrder().getExternalSysId() == null) {
/* 833 */           toRemove.add(position);
/*     */         }
/*     */       }
/*     */     }
/* 837 */     if (filterSymbol != null) {
/* 838 */       for (Position position : rc) {
/* 839 */         if (!filterSymbol.equals(position.getInstrument())) {
/* 840 */           toRemove.add(position);
/*     */         }
/*     */       }
/*     */     }
/* 844 */     rc.removeAll(toRemove);
/* 845 */     return rc;
/*     */   }
/*     */ 
/*     */   public boolean onMarketStateImpl_dllsupport_depricated(String instrument, ITick tick)
/*     */   {
/* 850 */     this.lastTick.put(instrument, tick);
/* 851 */     if (instrument.equals(this.waitingForQuote)) {
/* 852 */       this.tickLock.unfreeze(0);
/*     */     }
/* 854 */     return false;
/*     */   }
/*     */ 
/*     */   public boolean onMarketStateImpl(String instrument, HTick marketState, Market market)
/*     */   {
/* 861 */     return false;
/*     */   }
/*     */ 
/*     */   public int getImplementation()
/*     */   {
/* 866 */     return 0;
/*     */   }
/*     */ 
/*     */   public void setConsole(Object console)
/*     */   {
/*     */   }
/*     */ 
/*     */   public <T> Future<T> executeTask(Callable<T> callable)
/*     */   {
/* 875 */     return this.executorService.submit(callable);
/*     */   }
/*     */ 
/*     */   private class SweetLock
/*     */   {
/*  73 */     private int returnId = 0;
/*     */ 
/*  80 */     private String id = "";
/*     */ 
/*     */     private SweetLock() {  }
/*     */ 
/*  83 */     int freeze(String id) { this.id = id;
/*  84 */       this.returnId = -11;
/*  85 */       synchronized (this) {
/*     */         try {
/*  87 */           wait(15000L);
/*     */         } catch (InterruptedException ie) {
/*  89 */           DDSAgent.LOGGER.warn("Interrupted.");
/*     */         } catch (Exception e) {
/*  91 */           DDSAgent.LOGGER.error(e.getMessage(), e);
/*     */         }
/*     */       }
/*  94 */       return this.returnId;
/*     */     }
/*     */ 
/*     */     void unfreeze(int returnId)
/*     */     {
/* 101 */       this.returnId = returnId;
/* 102 */       synchronized (this) {
/* 103 */         notifyAll();
/*     */       }
/*     */     }
/*     */ 
/*     */     public String getId() {
/* 108 */       return this.id;
/*     */     }
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.agent.DDSAgent
 * JD-Core Version:    0.6.0
 */