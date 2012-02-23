/*     */ package com.dukascopy.api.impl.connect;
/*     */ 
/*     */ import com.dukascopy.api.IAccount;
/*     */ import com.dukascopy.api.IBar;
/*     */ import com.dukascopy.api.IContext;
/*     */ import com.dukascopy.api.IMessage;
/*     */ import com.dukascopy.api.IOrder.State;
/*     */ import com.dukascopy.api.IStrategy;
/*     */ import com.dukascopy.api.ITick;
/*     */ import com.dukascopy.api.Instrument;
/*     */ import com.dukascopy.api.JFException;
/*     */ import com.dukascopy.api.Period;
/*     */ import com.dukascopy.api.impl.execution.ScienceQueue;
/*     */ import com.dukascopy.api.impl.execution.ScienceRejectedExecutionHandler;
/*     */ import com.dukascopy.api.impl.execution.ScienceThreadFactory;
/*     */ import com.dukascopy.api.impl.execution.ScienceThreadFactory.ScienceThread;
/*     */ import com.dukascopy.api.impl.execution.ScienceThreadPoolExecutor;
/*     */ import com.dukascopy.api.impl.execution.Task;
/*     */ import com.dukascopy.api.impl.execution.TaskAccount;
/*     */ import com.dukascopy.api.impl.execution.TaskCustom;
/*     */ import com.dukascopy.api.impl.execution.TaskMessage;
/*     */ import com.dukascopy.api.impl.execution.TaskOnBar;
/*     */ import com.dukascopy.api.impl.execution.TaskOrderError;
/*     */ import com.dukascopy.api.impl.execution.TaskOrderGroupUpdate;
/*     */ import com.dukascopy.api.impl.execution.TaskOrderNotify;
/*     */ import com.dukascopy.api.impl.execution.TaskOrderUpdate;
/*     */ import com.dukascopy.api.impl.execution.TaskOrdersMerged;
/*     */ import com.dukascopy.api.impl.execution.TaskStart;
/*     */ import com.dukascopy.api.impl.execution.TaskStop;
/*     */ import com.dukascopy.api.impl.execution.TaskTick;
/*     */ import com.dukascopy.api.system.IStrategyExceptionHandler;
/*     */ import com.dukascopy.api.system.IStrategyExceptionHandler.Source;
/*     */ import com.dukascopy.dds2.greed.util.INotificationUtils;
/*     */ import com.dukascopy.dds2.greed.util.NotificationUtilsProvider;
/*     */ import com.dukascopy.transport.common.msg.group.OrderGroupMessage;
/*     */ import com.dukascopy.transport.common.msg.group.OrderMessage;
/*     */ import com.dukascopy.transport.common.msg.request.MergePositionsMessage;
/*     */ import com.dukascopy.transport.common.msg.response.ErrorResponseMessage;
/*     */ import com.dukascopy.transport.common.msg.response.NotificationMessage;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import java.util.Set;
/*     */ import java.util.concurrent.BlockingQueue;
/*     */ import java.util.concurrent.Callable;
/*     */ import java.util.concurrent.Future;
/*     */ import java.util.concurrent.FutureTask;
/*     */ import java.util.concurrent.TimeUnit;
/*     */ import java.util.concurrent.TimeoutException;
/*     */ import org.slf4j.Logger;
/*     */ import org.slf4j.LoggerFactory;
/*     */ 
/*     */ public class StrategyProcessor
/*     */ {
/*  55 */   private static final Logger LOGGER = LoggerFactory.getLogger(StrategyProcessor.class);
/*     */ 
/*  57 */   private ScienceThreadPoolExecutor executorService = null;
/*     */ 
/*  59 */   private IStrategy strategy = null;
/*  60 */   private JForexTaskManager taskManager = null;
/*     */ 
/*  62 */   private boolean onBarImplemented = true;
/*     */   private boolean fullAccessGranted;
/*     */ 
/*     */   public StrategyProcessor(JForexTaskManager taskManager, IStrategy strategy, boolean fullAccessGranted)
/*     */   {
/*  67 */     this.strategy = strategy;
/*  68 */     this.taskManager = taskManager;
/*  69 */     this.fullAccessGranted = fullAccessGranted;
/*     */ 
/*  71 */     ScienceQueue strategyQueue = new ScienceQueue();
/*     */ 
/*  73 */     this.executorService = new ScienceThreadPoolExecutor(1, 1, 0L, TimeUnit.MILLISECONDS, strategyQueue);
/*  74 */     this.executorService.setThreadFactory(new ScienceThreadFactory(strategy.getClass().getClassLoader(), strategy.getClass().getSimpleName()));
/*  75 */     this.executorService.prestartAllCoreThreads();
/*     */   }
/*     */ 
/*     */   public void onMessage(IMessage message, boolean asynch) {
/*  79 */     Task task = new TaskMessage(this.taskManager, this.strategy, message, this.taskManager.getExceptionHandler());
/*  80 */     executeTask(task, asynch);
/*     */   }
/*     */ 
/*     */   public void updateAccountInfo(IAccount account) {
/*  84 */     Task task = new TaskAccount(this.taskManager, this.strategy, account, this.taskManager.getExceptionHandler());
/*  85 */     executeTask(task, false);
/*     */   }
/*     */ 
/*     */   public void onMarket(Instrument instrument, ITick tick) {
/*  89 */     Task task = new TaskTick(this.taskManager, this.strategy, instrument, tick, this.taskManager.getExceptionHandler());
/*  90 */     executeTask(task, false);
/*     */   }
/*     */ 
/*     */   public void updateOrder(OrderGroupMessage orderGroup) {
/*  94 */     Task task = new TaskOrderGroupUpdate(this.taskManager, this.strategy, orderGroup);
/*  95 */     executeTask(task, false);
/*     */   }
/*     */ 
/*     */   public void updateOrder(OrderMessage orderMessage) {
/*  99 */     Task task = new TaskOrderUpdate(this.taskManager, this.strategy, orderMessage);
/* 100 */     executeTask(task, false);
/*     */   }
/*     */ 
/*     */   public void updateOrder(NotificationMessage notificationMessage) {
/* 104 */     Task task = new TaskOrderNotify(this.taskManager, this.strategy, notificationMessage);
/* 105 */     executeTask(task, false);
/*     */   }
/*     */ 
/*     */   public void updateOrder(MergePositionsMessage mergePositionsMessage) {
/* 109 */     Task task = new TaskOrdersMerged(this.taskManager, this.strategy, mergePositionsMessage);
/* 110 */     executeTask(task, false);
/*     */   }
/*     */ 
/*     */   public void updateOrder(PlatformOrderImpl order, ErrorResponseMessage errorResponseMessage) {
/* 114 */     if (order != null) {
/* 115 */       Task task = new TaskOrderError(this.taskManager, this.strategy, errorResponseMessage, order, this.taskManager.getExceptionHandler());
/* 116 */       executeTask(task, true);
/*     */     } else {
/* 118 */       LOGGER.warn("WARNING[onErrorMessage]:" + errorResponseMessage);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void waitForUpdate(PlatformOrderImpl platformOrderImpl, long timeout, TimeUnit unit) throws InterruptedException {
/* 123 */     this.executorService.runExceptTicksAndBars(platformOrderImpl, timeout, unit);
/*     */   }
/*     */ 
/*     */   public void waitForUpdate(PlatformOrderImpl platformOrderImpl, long timeout, TimeUnit unit, IOrder.State[] expectedStates) throws InterruptedException, JFException {
/* 127 */     List states = new ArrayList();
/* 128 */     if ((expectedStates != null) && (expectedStates.length > 0)) {
/* 129 */       for (IOrder.State expectedState : expectedStates) {
/* 130 */         states.add(expectedState.name());
/*     */       }
/*     */     }
/* 133 */     this.executorService.runExceptTicksAndBars(platformOrderImpl, timeout, unit, (String[])states.toArray(new String[states.size()]));
/*     */   }
/*     */ 
/*     */   public long getStrategyId() {
/* 137 */     return ((ScienceThreadFactory)this.executorService.getThreadFactory()).getThreadId();
/*     */   }
/*     */ 
/*     */   public long onStart(IContext platformConfigImpl) {
/* 141 */     ScienceRejectedExecutionHandler rejectedExecutionHandler = new ScienceRejectedExecutionHandler(platformConfigImpl, ((ScienceThreadFactory)this.executorService.getThreadFactory()).getThread());
/* 142 */     this.executorService.setRejectedExecutionHandler(rejectedExecutionHandler);
/*     */ 
/* 144 */     Task task = new TaskStart(platformConfigImpl, this.strategy);
/*     */     try
/*     */     {
/* 147 */       task.call();
/*     */     } catch (Exception ex) {
/* 149 */       String msg = representError(this.strategy, ex);
/* 150 */       NotificationUtilsProvider.getNotificationUtils().postErrorMessage(msg, ex, false);
/* 151 */       this.taskManager.getExceptionHandler().onException(this.taskManager.getStrategyId(), IStrategyExceptionHandler.Source.ON_START, ex);
/* 152 */       return 0L;
/*     */     }
/*     */ 
/* 155 */     return getStrategyId();
/*     */   }
/*     */ 
/*     */   public void onStop() {
/* 159 */     if (this.executorService == null) {
/* 160 */       return;
/*     */     }
/*     */ 
/* 163 */     this.executorService.getQueue().clear();
/*     */ 
/* 165 */     Task stopCallable = new TaskStop(this.strategy);
/*     */ 
/* 167 */     if (Thread.currentThread().getId() == getStrategyId()) {
/*     */       try {
/* 169 */         stopCallable.call();
/*     */       } catch (Exception e) {
/* 171 */         String msg = representError(this.strategy, e);
/* 172 */         NotificationUtilsProvider.getNotificationUtils().postErrorMessage(msg, e, false);
/* 173 */         LOGGER.error(e.getMessage(), e);
/*     */       }
/*     */     } else {
/* 176 */       Future future = this.executorService.submit(stopCallable);
/*     */       try {
/* 178 */         future.get(5L, TimeUnit.SECONDS);
/*     */       } catch (TimeoutException e) {
/* 180 */         ((ScienceThreadFactory)this.executorService.getThreadFactory()).getThread().interrupt();
/*     */         try {
/* 182 */           future.get(5L, TimeUnit.SECONDS);
/*     */         } catch (Exception e1) {
/* 184 */           LOGGER.error(e.getMessage(), e);
/*     */         }
/*     */       } catch (Exception e) {
/* 187 */         String msg = representError(this.strategy, e);
/* 188 */         NotificationUtilsProvider.getNotificationUtils().postErrorMessage(msg, e, false);
/* 189 */         LOGGER.error(e.getMessage(), e);
/*     */       }
/*     */     }
/*     */ 
/* 193 */     this.executorService.shutdown();
/*     */   }
/*     */ 
/*     */   public void halt() {
/* 197 */     this.executorService.shutdownNow();
/*     */     try {
/* 199 */       this.executorService.awaitTermination(5L, TimeUnit.SECONDS);
/*     */     }
/*     */     catch (InterruptedException e) {
/*     */     }
/* 203 */     if (!this.executorService.isTerminated()) {
/* 204 */       LOGGER.warn("Killing strategy thread [" + this.strategy.getClass().getSimpleName() + "]");
/* 205 */       NotificationUtilsProvider.getNotificationUtils().postWarningMessage("Killing strategy thread [" + this.strategy.getClass().getSimpleName() + "]");
/* 206 */       this.executorService.kill();
/*     */     }
/*     */   }
/*     */ 
/*     */   public boolean isStopping() {
/* 211 */     return this.taskManager.isStrategyStopping();
/*     */   }
/*     */ 
/*     */   public <T> Future<T> executeTask(Callable<T> callable, boolean asynch)
/*     */   {
/* 216 */     if ((this.taskManager.isThreadOk(Thread.currentThread().getId())) && (!asynch)) {
/* 217 */       FutureTask future = new FutureTask(callable);
/* 218 */       future.run();
/* 219 */       return future;
/* 220 */     }if (!this.taskManager.isStrategyStopping()) {
/* 221 */       if ((callable instanceof Task)) {
/* 222 */         return this.executorService.submit(callable);
/*     */       }
/* 224 */       return this.executorService.submit(new TaskCustom(this.taskManager, callable, false));
/*     */     }
/*     */ 
/* 227 */     return null;
/*     */   }
/*     */ 
/*     */   public Future<Object> executeStop(JForexTaskManager.StopCallable callable) {
/* 231 */     return this.executorService.submit(new TaskCustom(this.taskManager, callable, true));
/*     */   }
/*     */ 
/*     */   public static String representError(Object str, Throwable ex)
/*     */   {
/*     */     Throwable throwable;
/*     */     Throwable throwable;
/* 237 */     if (ex.getCause() != null)
/* 238 */       throwable = ex.getCause();
/*     */     else {
/* 240 */       throwable = ex;
/*     */     }
/*     */ 
/* 243 */     String msg = throwable.toString();
/*     */ 
/* 245 */     StackTraceElement[] elements = throwable.getStackTrace();
/* 246 */     StackTraceElement element = elements[0];
/* 247 */     for (StackTraceElement stackTraceElement : elements) {
/* 248 */       if (stackTraceElement.getClassName().equals(str.getClass().getName())) {
/* 249 */         element = stackTraceElement;
/* 250 */         break;
/*     */       }
/*     */     }
/* 253 */     if (element != null) {
/* 254 */       msg = msg + " @ " + element;
/*     */     }
/*     */ 
/* 257 */     return msg;
/*     */   }
/*     */ 
/*     */   public void onBar(Instrument instrument, Period period, IBar askBar, IBar bidBar)
/*     */   {
/* 263 */     Task task = new TaskOnBar(this.taskManager, this, instrument, period, askBar, bidBar, this.taskManager.getExceptionHandler());
/* 264 */     executeTask(task, false);
/*     */   }
/*     */ 
/*     */   public JForexTaskManager getTaskManager()
/*     */   {
/* 269 */     return this.taskManager;
/*     */   }
/*     */ 
/*     */   public IStrategy getStrategy() {
/* 273 */     return this.strategy;
/*     */   }
/*     */ 
/*     */   public boolean isOnBarImplemented() {
/* 277 */     return this.onBarImplemented;
/*     */   }
/*     */ 
/*     */   public void setOnBarImplemented(boolean onBarImplemented) {
/* 281 */     this.onBarImplemented = onBarImplemented;
/*     */   }
/*     */ 
/*     */   public boolean isFullAccessGranted() {
/* 285 */     return this.fullAccessGranted;
/*     */   }
/*     */ 
/*     */   public void setSubscribedInstruments(Set<Instrument> requiredInstruments) {
/* 289 */     this.taskManager.setSubscribedInstruments(requiredInstruments);
/*     */   }
/*     */ 
/*     */   public Set<Instrument> getSubscribedInstruments() {
/* 293 */     return this.taskManager.getSubscribedInstruments();
/*     */   }
/*     */ 
/*     */   public IAccount getAccount() {
/* 297 */     return this.taskManager.getAccount();
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.api.impl.connect.StrategyProcessor
 * JD-Core Version:    0.6.0
 */