/*      */ package com.dukascopy.api.impl.execution;
/*      */ 
/*      */ import com.dukascopy.api.ITick;
/*      */ import com.dukascopy.api.Instrument;
/*      */ import com.dukascopy.dds2.greed.util.INotificationUtils;
/*      */ import com.dukascopy.dds2.greed.util.NotificationUtilsProvider;
/*      */ import java.io.IOException;
/*      */ import java.io.ObjectInputStream;
/*      */ import java.io.ObjectOutputStream;
/*      */ import java.io.Serializable;
/*      */ import java.lang.reflect.Array;
/*      */ import java.util.AbstractQueue;
/*      */ import java.util.Collection;
/*      */ import java.util.Iterator;
/*      */ import java.util.NoSuchElementException;
/*      */ import java.util.concurrent.BlockingDeque;
/*      */ import java.util.concurrent.TimeUnit;
/*      */ import java.util.concurrent.locks.Condition;
/*      */ import java.util.concurrent.locks.ReentrantLock;
/*      */ import org.slf4j.Logger;
/*      */ import org.slf4j.LoggerFactory;
/*      */ 
/*      */ public class ScienceQueue<E extends ScienceFuture<Task>> extends AbstractQueue<E>
/*      */   implements BlockingDeque<E>, Serializable
/*      */ {
/*      */   private static final long TICK_SURVIVE_TIME = 1000L;
/*   59 */   private static final int INSTRUMENTS_LENGTH = Instrument.values().length;
/*      */ 
/*   61 */   private static final Logger LOGGER = LoggerFactory.getLogger(ScienceQueue.class);
/*      */   private Thread dropTicksThread;
/*      */   private boolean stopThread;
/*   66 */   private long lastOverloadWarning = -9223372036854775808L;
/*      */   private static final long serialVersionUID = -387911632671998426L;
/*      */   private transient Node<E> first;
/*      */   private transient Node<E> last;
/*      */   private transient int count;
/*      */   private final int capacity;
/*  209 */   private final ReentrantLock lock = new ReentrantLock();
/*      */ 
/*  211 */   private final Condition notEmpty = this.lock.newCondition();
/*      */ 
/*  213 */   private final Condition notFull = this.lock.newCondition();
/*      */ 
/*      */   private void dropOldTicks(E e)
/*      */   {
/*   76 */     Task task = e.getTask();
/*   77 */     if (task.getType() == Task.Type.TICK)
/*      */     {
/*   79 */       if (this.count > 0)
/*      */       {
/*   81 */         TaskTick taskTick = (TaskTick)task;
/*   82 */         long[][] latestTicks = new long[2][INSTRUMENTS_LENGTH];
/*   83 */         long currentTime = taskTick.getTick().getTime();
/*      */ 
/*   85 */         latestTicks[0][taskTick.getInstrument().ordinal()] = currentTime;
/*   86 */         latestTicks[1][taskTick.getInstrument().ordinal()] = 1L;
/*      */ 
/*   88 */         for (Node p = this.last; p != null; p = p.prev) {
/*   89 */           ScienceFuture future = (ScienceFuture)p.item;
/*   90 */           Task queueTask = future.getTask();
/*   91 */           if (queueTask.getType() == Task.Type.TICK) {
/*   92 */             TaskTick queueTaskTick = (TaskTick)queueTask;
/*   93 */             if (latestTicks[0][queueTaskTick.getInstrument().ordinal()] != 0L)
/*      */             {
/*   95 */               if ((currentTime - queueTaskTick.getTick().getTime() > 1000L) || (latestTicks[1][queueTaskTick.getInstrument().ordinal()] >= 3L)) {
/*   96 */                 unlink(p);
/*   97 */                 if (LOGGER.isDebugEnabled())
/*   98 */                   LOGGER.debug("old tick removed from the queue [" + queueTaskTick.getTick() + "], current time [" + currentTime + "], diff [" + (currentTime - queueTaskTick.getTick().getTime()) + "] tick waiting in queue [" + latestTicks[1][queueTaskTick.getInstrument().ordinal()] + "]");
/*      */               }
/*      */               else {
/*  101 */                 latestTicks[1][queueTaskTick.getInstrument().ordinal()] += 1L;
/*      */               }
/*      */             }
/*      */             else {
/*  105 */               latestTicks[0][queueTaskTick.getInstrument().ordinal()] = queueTaskTick.getTick().getTime();
/*  106 */               latestTicks[1][queueTaskTick.getInstrument().ordinal()] = 1L;
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/*  111 */     } else if (task.getType() == Task.Type.ACCOUNT)
/*      */     {
/*  113 */       if (this.count > 0)
/*      */       {
/*  115 */         for (Node p = this.last; p != null; p = p.prev) {
/*  116 */           ScienceFuture future = (ScienceFuture)p.item;
/*  117 */           Task queueTask = future.getTask();
/*  118 */           if (queueTask.getType() == Task.Type.ACCOUNT)
/*  119 */             unlink(p);
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   private void dropOldTicksByCurrentTime()
/*      */   {
/*  127 */     long currentLocalTime = System.currentTimeMillis();
/*  128 */     boolean[] latestTicksExist = new boolean[INSTRUMENTS_LENGTH];
/*  129 */     for (Node p = this.last; p != null; p = p.prev) {
/*  130 */       ScienceFuture future = (ScienceFuture)p.item;
/*  131 */       Task queueTask = future.getTask();
/*  132 */       if (queueTask.getType() == Task.Type.TICK) {
/*  133 */         TaskTick queueTaskTick = (TaskTick)queueTask;
/*  134 */         if (latestTicksExist[queueTaskTick.getInstrument().ordinal()] != 0)
/*      */         {
/*  136 */           if (currentLocalTime - queueTaskTick.getAddedTime() > 1000L) {
/*  137 */             unlink(p);
/*  138 */             if (LOGGER.isDebugEnabled()) {
/*  139 */               LOGGER.debug("old tick removed from the queue [" + queueTaskTick.getTick() + "], current local time [" + currentLocalTime + "], diff [" + (currentLocalTime - queueTaskTick.getAddedTime()) + "]");
/*      */             }
/*      */           }
/*      */         }
/*      */         else {
/*  144 */           latestTicksExist[queueTaskTick.getInstrument().ordinal()] = true;
/*      */         }
/*      */       }
/*      */     }
/*  148 */     if ((this.count > 500) && (this.lastOverloadWarning + 5000L < System.currentTimeMillis())) {
/*  149 */       int ticks = 0;
/*  150 */       int bars = 0;
/*  151 */       int tasks = 0;
/*  152 */       for (Node p = this.first; p != null; p = p.next) {
/*  153 */         ScienceFuture future = (ScienceFuture)p.item;
/*  154 */         Task queueTask = future.getTask();
/*  155 */         if (queueTask.getType() == Task.Type.TICK)
/*  156 */           ticks++;
/*  157 */         else if (queueTask.getType() == Task.Type.BAR)
/*  158 */           bars++;
/*      */         else {
/*  160 */           tasks++;
/*      */         }
/*      */       }
/*  163 */       if (this.count > 2000) {
/*  164 */         NotificationUtilsProvider.getNotificationUtils().postErrorMessage("Strategy thread queue overloaded with tasks. Ticks in queue - " + ticks + ", bars - " + bars + ", other tasks - " + tasks, true);
/*      */       }
/*      */       else {
/*  167 */         NotificationUtilsProvider.getNotificationUtils().postWarningMessage("Strategy thread queue overloaded with tasks. Ticks in queue - " + ticks + ", bars - " + bars + ", other tasks - " + tasks, true);
/*      */       }
/*      */ 
/*  170 */       this.lastOverloadWarning = System.currentTimeMillis();
/*      */     }
/*      */   }
/*      */ 
/*      */   public ScienceQueue()
/*      */   {
/*  220 */     this(2147483647);
/*      */   }
/*      */ 
/*      */   public ScienceQueue(int capacity)
/*      */   {
/*  230 */     if (capacity <= 0) throw new IllegalArgumentException();
/*  231 */     this.capacity = capacity;
/*  232 */     this.dropTicksThread = new Thread("SQOTD")
/*      */     {
/*      */       public void run() {
/*  235 */         while (!ScienceQueue.this.stopThread) {
/*  236 */           ReentrantLock lock = ScienceQueue.this.lock;
/*  237 */           lock.lock();
/*      */           try {
/*  239 */             ScienceQueue.this.dropOldTicksByCurrentTime();
/*      */           } finally {
/*  241 */             lock.unlock();
/*      */           }
/*      */           try {
/*  244 */             Thread.sleep(100L);
/*      */           }
/*      */           catch (InterruptedException e)
/*      */           {
/*      */           }
/*      */         }
/*      */       }
/*      */     };
/*  251 */     this.dropTicksThread.setDaemon(true);
/*  252 */     this.dropTicksThread.start();
/*      */   }
/*      */ 
/*      */   protected void stop() {
/*  256 */     this.stopThread = true;
/*  257 */     this.dropTicksThread.interrupt();
/*      */   }
/*      */ 
/*      */   public ScienceQueue(Collection<? extends E> c)
/*      */   {
/*  271 */     this(2147483647);
/*  272 */     for (ScienceFuture e : c)
/*  273 */       add(e);
/*      */   }
/*      */ 
/*      */   private boolean linkFirst(E e)
/*      */   {
/*  283 */     if (this.count >= this.capacity)
/*  284 */       return false;
/*  285 */     this.count += 1;
/*  286 */     Node f = this.first;
/*  287 */     Node x = new Node(e, null, f);
/*  288 */     this.first = x;
/*  289 */     if (this.last == null)
/*  290 */       this.last = x;
/*      */     else
/*  292 */       f.prev = x;
/*  293 */     this.notEmpty.signal();
/*  294 */     return true;
/*      */   }
/*      */ 
/*      */   private boolean linkLast(E e)
/*      */   {
/*  301 */     if (this.count >= this.capacity)
/*  302 */       return false;
/*  303 */     this.count += 1;
/*  304 */     Node l = this.last;
/*  305 */     Node x = new Node(e, l, null);
/*  306 */     this.last = x;
/*  307 */     if (this.first == null)
/*  308 */       this.first = x;
/*      */     else
/*  310 */       l.next = x;
/*  311 */     this.notEmpty.signal();
/*  312 */     return true;
/*      */   }
/*      */ 
/*      */   private E unlinkFirst()
/*      */   {
/*  319 */     Node f = this.first;
/*  320 */     if (f == null)
/*  321 */       return null;
/*  322 */     Node n = f.next;
/*  323 */     this.first = n;
/*  324 */     if (n == null)
/*  325 */       this.last = null;
/*      */     else
/*  327 */       n.prev = null;
/*  328 */     this.count -= 1;
/*  329 */     this.notFull.signal();
/*      */ 
/*  342 */     return (ScienceFuture)f.item;
/*      */   }
/*      */ 
/*      */   private E unlinkLast()
/*      */   {
/*  349 */     Node l = this.last;
/*  350 */     if (l == null)
/*  351 */       return null;
/*  352 */     Node p = l.prev;
/*  353 */     this.last = p;
/*  354 */     if (p == null)
/*  355 */       this.first = null;
/*      */     else
/*  357 */       p.next = null;
/*  358 */     this.count -= 1;
/*  359 */     this.notFull.signal();
/*  360 */     return (ScienceFuture)l.item;
/*      */   }
/*      */ 
/*      */   private void unlink(Node<E> x)
/*      */   {
/*  367 */     Node p = x.prev;
/*  368 */     Node n = x.next;
/*  369 */     if (p == null) {
/*  370 */       if (n == null) {
/*  371 */         this.first = (this.last = null);
/*      */       } else {
/*  373 */         n.prev = null;
/*  374 */         this.first = n;
/*      */       }
/*  376 */     } else if (n == null) {
/*  377 */       p.next = null;
/*  378 */       this.last = p;
/*      */     } else {
/*  380 */       p.next = n;
/*  381 */       n.prev = p;
/*      */     }
/*  383 */     this.count -= 1;
/*  384 */     this.notFull.signalAll();
/*      */   }
/*      */ 
/*      */   public void addFirst(E e)
/*      */   {
/*  394 */     if (!offerFirst(e))
/*  395 */       throw new IllegalStateException("Deque full");
/*      */   }
/*      */ 
/*      */   public void addLast(E e)
/*      */   {
/*  403 */     if (!offerLast(e))
/*  404 */       throw new IllegalStateException("Deque full");
/*      */   }
/*      */ 
/*      */   public boolean offerFirst(E e)
/*      */   {
/*  411 */     if (e == null) throw new NullPointerException();
/*  412 */     this.lock.lock();
/*      */     try {
/*  414 */       dropOldTicks(e);
/*  415 */       boolean bool = linkFirst(e);
/*      */       return bool; } finally { this.lock.unlock(); } throw localObject;
/*      */   }
/*      */ 
/*      */   public boolean offerLast(E e)
/*      */   {
/*  425 */     if (e == null) throw new NullPointerException();
/*  426 */     this.lock.lock();
/*      */     try {
/*  428 */       dropOldTicks(e);
/*  429 */       boolean bool = linkLast(e);
/*      */       return bool; } finally { this.lock.unlock(); } throw localObject;
/*      */   }
/*      */ 
/*      */   public void putFirst(E e)
/*      */     throws InterruptedException
/*      */   {
/*  440 */     if (e == null) throw new NullPointerException();
/*  441 */     this.lock.lock();
/*      */     try {
/*  443 */       dropOldTicks(e);
/*  444 */       while (!linkFirst(e))
/*  445 */         this.notFull.await();
/*      */     } finally {
/*  447 */       this.lock.unlock();
/*      */     }
/*      */   }
/*      */ 
/*      */   public void putLast(E e)
/*      */     throws InterruptedException
/*      */   {
/*  456 */     if (e == null) throw new NullPointerException();
/*  457 */     this.lock.lock();
/*      */     try {
/*  459 */       dropOldTicks(e);
/*  460 */       while (!linkLast(e))
/*  461 */         this.notFull.await();
/*      */     } finally {
/*  463 */       this.lock.unlock();
/*      */     }
/*      */   }
/*      */ 
/*      */   public boolean offerFirst(E e, long timeout, TimeUnit unit)
/*      */     throws InterruptedException
/*      */   {
/*  473 */     if (e == null) throw new NullPointerException();
/*  474 */     long nanos = unit.toNanos(timeout);
/*  475 */     this.lock.lockInterruptibly();
/*      */     try {
/*  477 */       dropOldTicks(e);
/*      */       while (true)
/*      */       {
/*      */         int i;
/*  479 */         if (linkFirst(e)) {
/*  480 */           i = 1;
/*      */           return i;
/*      */         }
/*  481 */         if (nanos <= 0L) {
/*  482 */           i = 0;
/*      */           return i;
/*      */         }
/*  483 */         nanos = this.notFull.awaitNanos(nanos);
/*      */       }
/*      */     } finally {
/*  486 */       this.lock.unlock(); } throw localObject;
/*      */   }
/*      */ 
/*      */   public boolean offerLast(E e, long timeout, TimeUnit unit)
/*      */     throws InterruptedException
/*      */   {
/*  496 */     if (e == null) throw new NullPointerException();
/*  497 */     long nanos = unit.toNanos(timeout);
/*  498 */     this.lock.lockInterruptibly();
/*      */     try {
/*  500 */       dropOldTicks(e);
/*      */       while (true)
/*      */       {
/*      */         int i;
/*  502 */         if (linkLast(e)) {
/*  503 */           i = 1;
/*      */           return i;
/*      */         }
/*  504 */         if (nanos <= 0L) {
/*  505 */           i = 0;
/*      */           return i;
/*      */         }
/*  506 */         nanos = this.notFull.awaitNanos(nanos);
/*      */       }
/*      */     } finally {
/*  509 */       this.lock.unlock(); } throw localObject;
/*      */   }
/*      */ 
/*      */   public E removeFirst()
/*      */   {
/*  517 */     ScienceFuture x = pollFirst();
/*  518 */     if (x == null) throw new NoSuchElementException();
/*  519 */     return x;
/*      */   }
/*      */ 
/*      */   public E removeLast()
/*      */   {
/*  526 */     ScienceFuture x = pollLast();
/*  527 */     if (x == null) throw new NoSuchElementException();
/*  528 */     return x;
/*      */   }
/*      */ 
/*      */   public E pollFirst() {
/*  532 */     this.lock.lock();
/*      */     try {
/*  534 */       ScienceFuture localScienceFuture = unlinkFirst();
/*      */       return localScienceFuture; } finally { this.lock.unlock(); } throw localObject;
/*      */   }
/*      */ 
/*      */   public E pollLast()
/*      */   {
/*  541 */     this.lock.lock();
/*      */     try {
/*  543 */       ScienceFuture localScienceFuture = unlinkLast();
/*      */       return localScienceFuture; } finally { this.lock.unlock(); } throw localObject;
/*      */   }
/*      */ 
/*      */   public E takeFirst() throws InterruptedException
/*      */   {
/*  550 */     this.lock.lock();
/*      */     try
/*      */     {
/*      */       ScienceFuture x;
/*  553 */       while ((x = unlinkFirst()) == null)
/*  554 */         this.notEmpty.await();
/*  555 */       ScienceFuture localScienceFuture1 = x;
/*      */       return localScienceFuture1; } finally { this.lock.unlock(); } throw localObject;
/*      */   }
/*      */ 
/*      */   public E takeLast() throws InterruptedException
/*      */   {
/*  562 */     this.lock.lock();
/*      */     try
/*      */     {
/*      */       ScienceFuture x;
/*  565 */       while ((x = unlinkLast()) == null)
/*  566 */         this.notEmpty.await();
/*  567 */       ScienceFuture localScienceFuture1 = x;
/*      */       return localScienceFuture1; } finally { this.lock.unlock(); } throw localObject;
/*      */   }
/*      */ 
/*      */   public E pollFirst(long timeout, TimeUnit unit)
/*      */     throws InterruptedException
/*      */   {
/*  575 */     long nanos = unit.toNanos(timeout);
/*  576 */     this.lock.lockInterruptibly();
/*      */     try {
/*      */       while (true) {
/*  579 */         ScienceFuture x = unlinkFirst();
/*      */         ScienceFuture localScienceFuture1;
/*  580 */         if (x != null) {
/*  581 */           localScienceFuture1 = x;
/*      */           return localScienceFuture1;
/*      */         }
/*  582 */         if (nanos <= 0L) {
/*  583 */           localScienceFuture1 = null;
/*      */           return localScienceFuture1;
/*      */         }
/*  584 */         nanos = this.notEmpty.awaitNanos(nanos);
/*      */       }
/*      */     } finally {
/*  587 */       this.lock.unlock(); } throw localObject;
/*      */   }
/*      */ 
/*      */   public E pollLast(long timeout, TimeUnit unit)
/*      */     throws InterruptedException
/*      */   {
/*  593 */     long nanos = unit.toNanos(timeout);
/*  594 */     this.lock.lockInterruptibly();
/*      */     try {
/*      */       while (true) {
/*  597 */         ScienceFuture x = unlinkLast();
/*      */         ScienceFuture localScienceFuture1;
/*  598 */         if (x != null) {
/*  599 */           localScienceFuture1 = x;
/*      */           return localScienceFuture1;
/*      */         }
/*  600 */         if (nanos <= 0L) {
/*  601 */           localScienceFuture1 = null;
/*      */           return localScienceFuture1;
/*      */         }
/*  602 */         nanos = this.notEmpty.awaitNanos(nanos);
/*      */       }
/*      */     } finally {
/*  605 */       this.lock.unlock(); } throw localObject;
/*      */   }
/*      */ 
/*      */   public E getFirst()
/*      */   {
/*  613 */     ScienceFuture x = peekFirst();
/*  614 */     if (x == null) throw new NoSuchElementException();
/*  615 */     return x;
/*      */   }
/*      */ 
/*      */   public E getLast()
/*      */   {
/*  622 */     ScienceFuture x = peekLast();
/*  623 */     if (x == null) throw new NoSuchElementException();
/*  624 */     return x;
/*      */   }
/*      */ 
/*      */   public E peekFirst() {
/*  628 */     this.lock.lock();
/*      */     try {
/*  630 */       ScienceFuture localScienceFuture = this.first == null ? null : (ScienceFuture)this.first.item;
/*      */       return localScienceFuture; } finally { this.lock.unlock(); } throw localObject;
/*      */   }
/*      */ 
/*      */   public E peekLast()
/*      */   {
/*  637 */     this.lock.lock();
/*      */     try {
/*  639 */       ScienceFuture localScienceFuture = this.last == null ? null : (ScienceFuture)this.last.item;
/*      */       return localScienceFuture; } finally { this.lock.unlock(); } throw localObject;
/*      */   }
/*      */ 
/*      */   public boolean removeFirstOccurrence(Object o)
/*      */   {
/*  646 */     if (o == null) return false;
/*  647 */     this.lock.lock();
/*      */     try {
/*  649 */       for (Node p = this.first; p != null; p = p.next)
/*  650 */         if (o.equals(p.item)) {
/*  651 */           unlink(p);
/*  652 */           int i = 1;
/*      */           return i;
/*      */         }
/*  655 */       p = 0;
/*      */       return p; } finally { this.lock.unlock(); } throw localObject;
/*      */   }
/*      */ 
/*      */   public boolean removeLastOccurrence(Object o)
/*      */   {
/*  662 */     if (o == null) return false;
/*  663 */     this.lock.lock();
/*      */     try {
/*  665 */       for (Node p = this.last; p != null; p = p.prev)
/*  666 */         if (o.equals(p.item)) {
/*  667 */           unlink(p);
/*  668 */           int i = 1;
/*      */           return i;
/*      */         }
/*  671 */       p = 0;
/*      */       return p; } finally { this.lock.unlock(); } throw localObject;
/*      */   }
/*      */ 
/*      */   public boolean add(E e)
/*      */   {
/*  691 */     addLast(e);
/*  692 */     return true;
/*      */   }
/*      */ 
/*      */   public boolean offer(E e)
/*      */   {
/*  699 */     return offerLast(e);
/*      */   }
/*      */ 
/*      */   public void put(E e)
/*      */     throws InterruptedException
/*      */   {
/*  707 */     putLast(e);
/*      */   }
/*      */ 
/*      */   public boolean offer(E e, long timeout, TimeUnit unit)
/*      */     throws InterruptedException
/*      */   {
/*  716 */     return offerLast(e, timeout, unit);
/*      */   }
/*      */ 
/*      */   public E remove()
/*      */   {
/*  730 */     return removeFirst();
/*      */   }
/*      */ 
/*      */   public E poll() {
/*  734 */     return pollFirst();
/*      */   }
/*      */ 
/*      */   public E take() throws InterruptedException {
/*  738 */     return takeFirst();
/*      */   }
/*      */ 
/*      */   public E poll(long timeout, TimeUnit unit) throws InterruptedException {
/*  742 */     return pollFirst(timeout, unit);
/*      */   }
/*      */ 
/*      */   public E element()
/*      */   {
/*  756 */     return getFirst();
/*      */   }
/*      */ 
/*      */   public E peek() {
/*  760 */     return peekFirst();
/*      */   }
/*      */ 
/*      */   public int remainingCapacity()
/*      */   {
/*  775 */     this.lock.lock();
/*      */     try {
/*  777 */       int i = this.capacity - this.count;
/*      */       return i; } finally { this.lock.unlock(); } throw localObject;
/*      */   }
/*      */ 
/*      */   public int drainTo(Collection<? super E> c)
/*      */   {
/*  790 */     if (c == null)
/*  791 */       throw new NullPointerException();
/*  792 */     if (c == this)
/*  793 */       throw new IllegalArgumentException();
/*  794 */     this.lock.lock();
/*      */     try {
/*  796 */       for (Node p = this.first; p != null; p = p.next)
/*  797 */         c.add(p.item);
/*  798 */       int n = this.count;
/*  799 */       this.count = 0;
/*  800 */       this.first = (this.last = null);
/*  801 */       this.notFull.signalAll();
/*  802 */       int i = n;
/*      */       return i; } finally { this.lock.unlock(); } throw localObject;
/*      */   }
/*      */ 
/*      */   public int drainTo(Collection<? super E> c, int maxElements)
/*      */   {
/*  815 */     if (c == null)
/*  816 */       throw new NullPointerException();
/*  817 */     if (c == this)
/*  818 */       throw new IllegalArgumentException();
/*  819 */     this.lock.lock();
/*      */     try {
/*  821 */       int n = 0;
/*  822 */       while ((n < maxElements) && (this.first != null)) {
/*  823 */         c.add(this.first.item);
/*  824 */         this.first.prev = null;
/*  825 */         this.first = this.first.next;
/*  826 */         this.count -= 1;
/*  827 */         n++;
/*      */       }
/*  829 */       if (this.first == null)
/*  830 */         this.last = null;
/*  831 */       this.notFull.signalAll();
/*  832 */       int i = n;
/*      */       return i; } finally { this.lock.unlock(); } throw localObject;
/*      */   }
/*      */ 
/*      */   public void push(E e)
/*      */   {
/*  845 */     addFirst(e);
/*      */   }
/*      */ 
/*      */   public E pop()
/*      */   {
/*  852 */     return removeFirst();
/*      */   }
/*      */ 
/*      */   public boolean remove(Object o)
/*      */   {
/*  872 */     return removeFirstOccurrence(o);
/*      */   }
/*      */ 
/*      */   public int size()
/*      */   {
/*  881 */     this.lock.lock();
/*      */     try {
/*  883 */       int i = this.count;
/*      */       return i; } finally { this.lock.unlock(); } throw localObject;
/*      */   }
/*      */ 
/*      */   public boolean contains(Object o)
/*      */   {
/*  898 */     if (o == null) return false;
/*  899 */     this.lock.lock();
/*      */     try {
/*  901 */       for (Node p = this.first; p != null; p = p.next)
/*  902 */         if (o.equals(p.item)) {
/*  903 */           int i = 1;
/*      */           return i;
/*      */         }
/*  904 */       p = 0;
/*      */       return p; } finally { this.lock.unlock(); } throw localObject;
/*      */   }
/*      */ 
/*      */   boolean removeNode(Node<E> e)
/*      */   {
/*  915 */     this.lock.lock();
/*      */     try {
/*  917 */       for (Node p = this.first; p != null; p = p.next)
/*  918 */         if (p == e) {
/*  919 */           unlink(p);
/*  920 */           int i = 1;
/*      */           return i;
/*      */         }
/*  923 */       p = 0;
/*      */       return p; } finally { this.lock.unlock(); } throw localObject;
/*      */   }
/*      */ 
/*      */   public Object[] toArray()
/*      */   {
/*  943 */     this.lock.lock();
/*      */     try {
/*  945 */       Object[] a = new Object[this.count];
/*  946 */       int k = 0;
/*  947 */       for (Node p = this.first; p != null; p = p.next)
/*  948 */         a[(k++)] = p.item;
/*  949 */       p = a;
/*      */       return p; } finally { this.lock.unlock(); } throw localObject;
/*      */   }
/*      */ 
/*      */   public <T> T[] toArray(T[] a)
/*      */   {
/*  992 */     this.lock.lock();
/*      */     try {
/*  994 */       if (a.length < this.count) {
/*  995 */         a = (Object[])(Object[])Array.newInstance(a.getClass().getComponentType(), this.count);
/*      */       }
/*      */ 
/* 1000 */       int k = 0;
/* 1001 */       for (Node p = this.first; p != null; p = p.next)
/* 1002 */         a[(k++)] = p.item;
/* 1003 */       if (a.length > k)
/* 1004 */         a[k] = null;
/* 1005 */       p = a;
/*      */       return p; } finally { this.lock.unlock(); } throw localObject;
/*      */   }
/*      */ 
/*      */   public String toString()
/*      */   {
/* 1012 */     this.lock.lock();
/*      */     try {
/* 1014 */       String str = super.toString();
/*      */       return str; } finally { this.lock.unlock(); } throw localObject;
/*      */   }
/*      */ 
/*      */   public void clear()
/*      */   {
/* 1025 */     this.lock.lock();
/*      */     try {
/* 1027 */       this.first = (this.last = null);
/* 1028 */       this.count = 0;
/* 1029 */       this.notFull.signalAll();
/*      */     } finally {
/* 1031 */       this.lock.unlock();
/*      */     }
/*      */   }
/*      */ 
/*      */   public Iterator<E> iterator()
/*      */   {
/* 1047 */     return new Itr(null);
/*      */   }
/*      */ 
/*      */   public Iterator<E> descendingIterator()
/*      */   {
/* 1061 */     return new DescendingItr(null);
/*      */   }
/*      */ 
/*      */   private void writeObject(ObjectOutputStream s)
/*      */     throws IOException
/*      */   {
/* 1161 */     this.lock.lock();
/*      */     try
/*      */     {
/* 1164 */       s.defaultWriteObject();
/*      */ 
/* 1166 */       for (Node p = this.first; p != null; p = p.next) {
/* 1167 */         s.writeObject(p.item);
/*      */       }
/* 1169 */       s.writeObject(null);
/*      */     } finally {
/* 1171 */       this.lock.unlock();
/*      */     }
/*      */   }
/*      */ 
/*      */   private void readObject(ObjectInputStream s)
/*      */     throws IOException, ClassNotFoundException
/*      */   {
/* 1182 */     s.defaultReadObject();
/* 1183 */     this.count = 0;
/* 1184 */     this.first = null;
/* 1185 */     this.last = null;
/*      */     while (true)
/*      */     {
/* 1188 */       ScienceFuture item = (ScienceFuture)s.readObject();
/* 1189 */       if (item == null)
/*      */         break;
/* 1191 */       add(item);
/*      */     }
/*      */   }
/*      */ 
/*      */   private class DescendingItr extends ScienceQueue.AbstractItr
/*      */   {
/*      */     private DescendingItr()
/*      */     {
/* 1139 */       super();
/*      */     }
/* 1141 */     void advance() { ReentrantLock lock = ScienceQueue.this.lock;
/* 1142 */       lock.lock();
/*      */       try {
/* 1144 */         this.next = (this.next == null ? ScienceQueue.this.last : this.next.prev);
/* 1145 */         this.nextItem = (this.next == null ? null : (ScienceFuture)this.next.item);
/*      */       } finally {
/* 1147 */         lock.unlock();
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   private class Itr extends ScienceQueue.AbstractItr
/*      */   {
/*      */     private Itr()
/*      */     {
/* 1123 */       super();
/*      */     }
/* 1125 */     void advance() { ReentrantLock lock = ScienceQueue.this.lock;
/* 1126 */       lock.lock();
/*      */       try {
/* 1128 */         this.next = (this.next == null ? ScienceQueue.this.first : this.next.next);
/* 1129 */         this.nextItem = (this.next == null ? null : (ScienceFuture)this.next.item);
/*      */       } finally {
/* 1131 */         lock.unlock();
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   private abstract class AbstractItr
/*      */     implements Iterator<E>
/*      */   {
/*      */     ScienceQueue.Node<E> next;
/*      */     E nextItem;
/*      */     private ScienceQueue.Node<E> lastRet;
/*      */ 
/*      */     AbstractItr()
/*      */     {
/* 1088 */       advance();
/*      */     }
/*      */ 
/*      */     abstract void advance();
/*      */ 
/*      */     public boolean hasNext()
/*      */     {
/* 1098 */       return this.next != null;
/*      */     }
/*      */ 
/*      */     public E next() {
/* 1102 */       if (this.next == null)
/* 1103 */         throw new NoSuchElementException();
/* 1104 */       this.lastRet = this.next;
/* 1105 */       ScienceFuture x = this.nextItem;
/* 1106 */       advance();
/* 1107 */       return x;
/*      */     }
/*      */ 
/*      */     public void remove() {
/* 1111 */       ScienceQueue.Node n = this.lastRet;
/* 1112 */       if (n == null)
/* 1113 */         throw new IllegalStateException();
/* 1114 */       this.lastRet = null;
/*      */ 
/* 1118 */       ScienceQueue.this.removeNode(n);
/*      */     }
/*      */   }
/*      */ 
/*      */   static final class Node<E>
/*      */   {
/*      */     E item;
/*      */     Node<E> prev;
/*      */     Node<E> next;
/*      */ 
/*      */     Node(E x, Node<E> p, Node<E> n)
/*      */     {
/*  194 */       this.item = x;
/*  195 */       this.prev = p;
/*  196 */       this.next = n;
/*      */     }
/*      */   }
/*      */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.api.impl.execution.ScienceQueue
 * JD-Core Version:    0.6.0
 */