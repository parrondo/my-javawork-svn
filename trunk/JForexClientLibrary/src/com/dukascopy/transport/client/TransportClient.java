/*     */ package com.dukascopy.transport.client;
/*     */ 
/*     */ import com.dukascopy.transport.common.mina.IoFutureListenerImpl;
/*     */ import com.dukascopy.transport.common.mina.MessageSentListener;
/*     */ import com.dukascopy.transport.common.mina.ProxyInterfaceFactory;
/*     */ import com.dukascopy.transport.common.mina.RemoteCallSupport;
/*     */ import com.dukascopy.transport.common.mina.SynchRequestFuture;
/*     */ import com.dukascopy.transport.common.msg.ProtocolMessage;
/*     */ import com.dukascopy.transport.common.msg.request.CurrencyMarket;
/*     */ import com.dukascopy.transport.common.msg.request.HeartbeatRequestMessage;
/*     */ import com.dukascopy.transport.common.msg.response.ErrorResponseMessage;
/*     */ import com.dukascopy.transport.common.msg.response.HeartbeatOkResponseMessage;
/*     */ import com.dukascopy.transport.common.msg.response.OkResponseMessage;
/*     */ import java.io.IOException;
/*     */ import java.net.InetSocketAddress;
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
/*     */ import java.util.Properties;
/*     */ import java.util.concurrent.Executor;
/*     */ import java.util.concurrent.ThreadFactory;
/*     */ import java.util.concurrent.ThreadPoolExecutor;
/*     */ import java.util.concurrent.TimeoutException;
/*     */ import org.apache.mina.common.IoSession;
/*     */ import org.apache.mina.common.WriteFuture;
/*     */ import org.slf4j.Logger;
/*     */ import org.slf4j.LoggerFactory;
/*     */ 
/*     */ public class TransportClient
/*     */ {
/*  38 */   private int maxSynchRequestInProcess = 60;
/*     */ 
/*  40 */   private static final Logger log = LoggerFactory.getLogger(TransportClient.class);
/*     */ 
/*  43 */   private InetSocketAddress address = null;
/*     */   private ClientProtocolHandler protocolHandler;
/*  47 */   protected final int maxMessageSizeBytes = 10240000;
/*     */   private String login;
/*     */   private String password;
/*  53 */   protected boolean isPasswordHashMode = false;
/*     */   private ClientListener listener;
/*  57 */   private int maxScheduledMessagesCountBeforeDisconnect = 30;
/*     */ 
/*  59 */   private boolean createFeederSocket = true;
/*     */   private String serverSessionId;
/*  63 */   private Properties properties = new Properties();
/*     */ 
/*  65 */   private boolean terminated = false;
/*     */ 
/*  67 */   private boolean multiThreadListenerInvocation = true;
/*     */ 
/*  69 */   private static final Long COMMUNICATION_TIMEOUT = Long.valueOf(5000L);
/*     */ 
/*  71 */   protected RemoteCallSupport remoteCallSupport = new RemoteCallSupport(new ProxyInterfaceFactory());
/*     */ 
/*  74 */   private Map<Long, SynchRequestFuture> synchFutureMap = new HashMap();
/*     */ 
/*  76 */   private Long requestId = Long.valueOf(0L);
/*     */ 
/*  78 */   private boolean pingConnection = true;
/*     */ 
/*  80 */   private Long pingTimeout = Long.valueOf(10000L);
/*     */ 
/*  82 */   private boolean useSsl = false;
/*     */   private CriteriaIsolationExecutor eventExecutor;
/*  86 */   private int poolSize = 30;
/*     */ 
/*  88 */   private int minaPoolSize = 16;
/*     */ 
/*  90 */   private String userAgent = "MinaTransportClient";
/*     */   private StreamListener streamListener;
/*  94 */   private String transportClientVersion = "2.3.78";
/*     */   private Thread monitorThread;
/*     */   private SecurityExceptionHandler securityExceptionHandler;
/*     */ 
/*     */   protected String getTransportClientVersion()
/*     */   {
/* 104 */     return this.transportClientVersion;
/*     */   }
/*     */ 
/*     */   public String getUserAgent()
/*     */   {
/* 111 */     return this.userAgent;
/*     */   }
/*     */ 
/*     */   public void setUserAgent(String userAgent)
/*     */   {
/* 121 */     this.userAgent = userAgent;
/*     */   }
/*     */ 
/*     */   public void setProperty(String key, Object value) {
/* 125 */     this.properties.put(key, value);
/*     */   }
/*     */ 
/*     */   public SecurityExceptionHandler getSecurityExceptionHandler() {
/* 129 */     return this.securityExceptionHandler;
/*     */   }
/*     */ 
/*     */   public void setSecurityExceptionHandler(SecurityExceptionHandler securityExceptionHandler)
/*     */   {
/* 134 */     this.securityExceptionHandler = securityExceptionHandler;
/*     */   }
/*     */ 
/*     */   public boolean isMultiThreadListenerInvocation()
/*     */   {
/* 142 */     return this.multiThreadListenerInvocation;
/*     */   }
/*     */ 
/*     */   public int getMinaPoolSize() {
/* 146 */     return this.minaPoolSize;
/*     */   }
/*     */ 
/*     */   public void setMinaPoolSize(int minaPoolSize) {
/* 150 */     this.minaPoolSize = minaPoolSize;
/* 151 */     if ((this.protocolHandler.getMinaThreadPoolExecutor() instanceof ThreadPoolExecutor)) {
/* 152 */       ((ThreadPoolExecutor)this.protocolHandler.getMinaThreadPoolExecutor()).setMaximumPoolSize(minaPoolSize);
/*     */ 
/* 154 */       ((ThreadPoolExecutor)this.protocolHandler.getMinaThreadPoolExecutor()).setCorePoolSize(minaPoolSize);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void setMultiThreadListenerInvocation(boolean multiThreadListenerInvocation)
/*     */   {
/* 167 */     this.multiThreadListenerInvocation = multiThreadListenerInvocation;
/* 168 */     if (!multiThreadListenerInvocation) {
/* 169 */       this.eventExecutor.setCorePoolSize(1);
/* 170 */       this.eventExecutor.setMaximumPoolSize(1);
/*     */     } else {
/* 172 */       this.eventExecutor.setCorePoolSize(this.poolSize);
/* 173 */       this.eventExecutor.setMaximumPoolSize(this.poolSize);
/*     */     }
/*     */   }
/*     */ 
/*     */   public int getPoolSize()
/*     */   {
/* 181 */     return this.poolSize;
/*     */   }
/*     */ 
/*     */   public void setPoolSize(int poolSize)
/*     */   {
/* 191 */     this.poolSize = poolSize;
/* 192 */     this.eventExecutor.setCorePoolSize(poolSize);
/* 193 */     this.eventExecutor.setMaximumPoolSize(poolSize);
/*     */   }
/*     */ 
/*     */   public Object getProperty(String key) {
/* 197 */     return this.properties.get(key);
/*     */   }
/*     */ 
/*     */   public void clearProperty() {
/* 201 */     this.properties.clear();
/*     */   }
/*     */ 
/*     */   public TransportClient()
/*     */   {
/* 208 */     this.eventExecutor = initEventExecutor();
/* 209 */     this.protocolHandler = new ClientProtocolHandler(this, this.eventExecutor);
/* 210 */     startMonitorThread();
/*     */   }
/*     */ 
/*     */   private CriteriaIsolationExecutor initEventExecutor()
/*     */   {
/* 215 */     if (!isMultiThreadListenerInvocation()) {
/* 216 */       this.poolSize = 1;
/*     */     }
/*     */ 
/* 219 */     CriteriaIsolationExecutor executor = new CriteriaIsolationExecutor(this.poolSize);
/*     */ 
/* 221 */     executor.setThreadFactory(new ThreadFactory() {
/* 222 */       volatile int counter = 0;
/*     */ 
/*     */       public Thread newThread(Runnable r)
/*     */       {
/* 231 */         this.counter += 1;
/* 232 */         Thread t = new Thread(r, "(" + hashCode() + ") Client listeners invocation thread - " + this.counter);
/*     */ 
/* 234 */         return t;
/*     */       }
/*     */     });
/* 238 */     executor.addIsolationCriteria(new IsolationCriteria(ProtocolMessage.class)
/*     */     {
/*     */       public Object getCheckParameter(ProtocolMessage object)
/*     */       {
/* 250 */         return object.getUserId();
/*     */       }
/*     */     });
/* 254 */     executor.addIsolationCriteria(new IsolationCriteria(CurrencyMarket.class)
/*     */     {
/*     */       public Object getCheckParameter(ProtocolMessage object)
/*     */       {
/* 266 */         if ((object instanceof CurrencyMarket)) {
/* 267 */           CurrencyMarket cm = (CurrencyMarket)object;
/* 268 */           return cm.getInstrument();
/*     */         }
/* 270 */         return null;
/*     */       }
/*     */     });
/* 274 */     return executor;
/*     */   }
/*     */ 
/*     */   public TransportClient(InetSocketAddress address, String login, String password, ClientListener listener)
/*     */   {
/*     */     try
/*     */     {
/* 292 */       this.eventExecutor = initEventExecutor();
/* 293 */       this.listener = listener;
/* 294 */       this.login = login;
/* 295 */       this.password = password;
/* 296 */       this.address = address;
/* 297 */       this.protocolHandler = new ClientProtocolHandler(this, this.eventExecutor);
/*     */ 
/* 299 */       startMonitorThread();
/*     */     } catch (Exception e) {
/* 301 */       e.printStackTrace();
/*     */     }
/*     */   }
/*     */ 
/*     */   public TransportClient(InetSocketAddress address, String login, String password, ClientListener listener, Executor minaThreadPoolExecutor)
/*     */   {
/*     */     try
/*     */     {
/* 316 */       this.eventExecutor = initEventExecutor();
/* 317 */       this.listener = listener;
/* 318 */       this.login = login;
/* 319 */       this.password = password;
/* 320 */       this.address = address;
/* 321 */       this.protocolHandler = new ClientProtocolHandler(this, this.eventExecutor, minaThreadPoolExecutor);
/*     */ 
/* 323 */       startMonitorThread();
/*     */     } catch (Exception e) {
/* 325 */       e.printStackTrace();
/*     */     }
/*     */   }
/*     */ 
/*     */   private void startMonitorThread()
/*     */   {
/* 331 */     this.monitorThread = new Thread(new Runnable()
/*     */     {
/*     */       public void run()
/*     */       {
/* 339 */         int c = 0;
/* 340 */         while (!TransportClient.this.terminated)
/*     */           try {
/* 342 */             TransportClient.this.checkConnection();
/* 343 */             Thread.sleep(100L);
/* 344 */             if (c >= 100) {
/* 345 */               c = 0;
/* 346 */               if ((TransportClient.this.isOnline()) && (TransportClient.this.isPingConnection())) {
/* 347 */                 TransportClient.this.pingConnection();
/*     */               }
/*     */             }
/* 350 */             c++;
/*     */           } catch (InterruptedException e) {
/*     */           }
/*     */           catch (Exception e) {
/* 354 */             TransportClient.log.error("Error in Connection monitor thread", e);
/*     */           }
/*     */       }
/*     */     }
/*     */     , "Connection monitor");
/*     */ 
/* 359 */     this.monitorThread.start();
/*     */   }
/*     */ 
/*     */   public TransportClient(String ip, int port, String login, String password, ClientListener listener)
/*     */   {
/* 378 */     this(new InetSocketAddress(ip, port), login, password, listener);
/*     */   }
/*     */ 
/*     */   public String getServerSessionId()
/*     */   {
/* 385 */     return this.serverSessionId;
/*     */   }
/*     */ 
/*     */   public String getSessionId()
/*     */   {
/* 392 */     return this.serverSessionId;
/*     */   }
/*     */ 
/*     */   public void setServerSessionId(String serverSessionId)
/*     */   {
/* 402 */     this.serverSessionId = serverSessionId;
/*     */   }
/*     */ 
/*     */   public int getMaxScheduledMessagesCountBeforeDisconnect() {
/* 406 */     return this.maxScheduledMessagesCountBeforeDisconnect;
/*     */   }
/*     */ 
/*     */   public void setMaxScheduledMessagesCountBeforeDisconnect(int maxScheduledMessagesCountBeforeDisconnect)
/*     */   {
/* 414 */     this.maxScheduledMessagesCountBeforeDisconnect = maxScheduledMessagesCountBeforeDisconnect;
/*     */   }
/*     */ 
/*     */   public ClientListener getListener() {
/* 418 */     return this.listener;
/*     */   }
/*     */ 
/*     */   public boolean isTerminated() {
/* 422 */     return this.terminated;
/*     */   }
/*     */ 
/*     */   public void setPasswordHashMode(boolean mode)
/*     */   {
/* 433 */     this.isPasswordHashMode = mode;
/*     */   }
/*     */ 
/*     */   public boolean getPasswordHashMode() {
/* 437 */     return this.isPasswordHashMode;
/*     */   }
/*     */ 
/*     */   public void setListener(ClientListener listener) {
/* 441 */     this.listener = listener;
/*     */   }
/*     */ 
/*     */   public void connect()
/*     */   {
/* 448 */     this.protocolHandler.connect();
/*     */   }
/*     */ 
/*     */   public void disconnect()
/*     */   {
/* 455 */     if (this.protocolHandler != null)
/* 456 */       this.protocolHandler.disconnect(DisconnectReason.CLIENT_APP_REQUEST);
/*     */   }
/*     */ 
/*     */   public void controlRequest(ProtocolMessage message, MessageSentListener messageSentListener)
/*     */     throws IOException
/*     */   {
/* 471 */     controlRequest(message, messageSentListener, false);
/*     */   }
/*     */ 
/*     */   public ProtocolMessage controlNonBlockingRequest(ProtocolMessage message) throws IOException
/*     */   {
/* 476 */     controlRequest(message, null, true);
/* 477 */     return new OkResponseMessage();
/*     */   }
/*     */ 
/*     */   private void controlRequest(ProtocolMessage message, MessageSentListener messageSentListener, boolean notLock)
/*     */     throws IOException
/*     */   {
/* 483 */     if ((isOnline()) && (!this.terminated)) {
/* 484 */       boolean isSent = false;
/*     */ 
/* 486 */       while ((!isSent) && (isOnline())) {
/* 487 */         if (this.protocolHandler.getSession().getScheduledWriteRequests() < this.maxScheduledMessagesCountBeforeDisconnect) {
/* 488 */           WriteFuture wf = null;
/* 489 */           wf = this.protocolHandler.getSession().write(message);
/*     */ 
/* 491 */           if (messageSentListener != null) {
/* 492 */             wf.addListener(new IoFutureListenerImpl(message, messageSentListener));
/*     */           }
/*     */ 
/* 495 */           isSent = true;
/* 496 */           continue;
/* 497 */         }if (notLock) {
/* 498 */           throw new IOException("ScheduledWriteRequests > maxScheduledMessagesCount");
/*     */         }
/*     */         try
/*     */         {
/* 502 */           Thread.sleep(10L);
/*     */         }
/*     */         catch (InterruptedException ie) {
/*     */         }
/*     */       }
/*     */     }
/*     */     else {
/* 509 */       throw new IOException("Client not online");
/*     */     }
/*     */   }
/*     */ 
/*     */   public ProtocolMessage controlBlockingRequest(ProtocolMessage message, Long timeoutTime)
/*     */     throws InterruptedException, IOException
/*     */   {
/* 525 */     if ((isOnline()) && (!this.terminated)) {
/* 526 */       boolean isSent = false;
/*     */ 
/* 528 */       while ((!isSent) && (isOnline())) {
/* 529 */         if (this.protocolHandler.getSession().getScheduledWriteRequests() <= this.maxScheduledMessagesCountBeforeDisconnect) {
/* 530 */           WriteFuture wf = null;
/* 531 */           wf = this.protocolHandler.getSession().write(message);
/* 532 */           if (wf != null) {
/* 533 */             wf.join(timeoutTime.longValue());
/* 534 */             if (wf.isWritten()) {
/* 535 */               isSent = true;
/* 536 */               return new OkResponseMessage();
/*     */             }
/* 538 */             throw new InterruptedException("Comunication error");
/*     */           }
/*     */ 
/* 541 */           throw new InterruptedException("Missed write future");
/*     */         }
/*     */ 
/*     */         try
/*     */         {
/* 546 */           Thread.sleep(10L);
/*     */         }
/*     */         catch (InterruptedException ie)
/*     */         {
/*     */         }
/*     */       }
/* 552 */       if (!isSent)
/* 553 */         throw new InterruptedException("Communication timeout");
/*     */     }
/*     */     else {
/* 556 */       throw new IOException("Client disconnected");
/*     */     }
/* 558 */     return new OkResponseMessage();
/*     */   }
/*     */ 
/*     */   public ProtocolMessage controlRequest(ProtocolMessage message)
/*     */   {
/*     */     try
/*     */     {
/* 570 */       controlRequest(message, null);
/* 571 */       return new OkResponseMessage();
/*     */     } catch (IOException e) {
/* 573 */       log.error("Control request exception", e);
/* 574 */       e.printStackTrace();
/* 575 */     }return new ErrorResponseMessage(e.getMessage());
/*     */   }
/*     */ 
/*     */   public boolean isOnline()
/*     */   {
/* 585 */     if (this.protocolHandler != null) {
/* 586 */       return this.protocolHandler.isOnline();
/*     */     }
/* 588 */     return false;
/*     */   }
/*     */ 
/*     */   private void checkConnection()
/*     */   {
/* 593 */     if ((this.protocolHandler != null) && (this.protocolHandler.isOnline())) {
/* 594 */       IoSession session = this.protocolHandler.getSession();
/* 595 */       if ((session != null) && (session.getScheduledWriteRequests() > this.maxScheduledMessagesCountBeforeDisconnect) && (session.getLastWriteTime() < System.currentTimeMillis() - COMMUNICATION_TIMEOUT.longValue()))
/*     */       {
/* 599 */         log.error("Client disconnected due to bad connection");
/* 600 */         this.protocolHandler.disconnect(DisconnectReason.SLOW_CONNECTION_SENDING_QUEUE_OVERLOADED);
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   private void pingIoSession(IoSession session) throws TimeoutException
/*     */   {
/* 607 */     long ioTimeout = 30000L;
/* 608 */     if (session == null) {
/* 609 */       return;
/*     */     }
/* 611 */     long lastIoTime = session.getLastIoTime();
/* 612 */     if (lastIoTime + 30000L > System.currentTimeMillis()) {
/* 613 */       log.debug("Bytes IO processing ok, ping not required ( last io time " + (System.currentTimeMillis() - lastIoTime) + " ms )");
/*     */ 
/* 615 */       return;
/*     */     }
/* 617 */     HeartbeatRequestMessage heartbeat = new HeartbeatRequestMessage();
/* 618 */     Long startTime = Long.valueOf(System.currentTimeMillis());
/*     */     try {
/* 620 */       ProtocolMessage response = controlSynchRequest(heartbeat, this.pingTimeout, session);
/*     */ 
/* 622 */       Long endTime = Long.valueOf(System.currentTimeMillis());
/* 623 */       if ((response instanceof HeartbeatOkResponseMessage)) {
/* 624 */         log.debug("Connection ping ok! Time - " + (endTime.longValue() - startTime.longValue()));
/*     */       }
/*     */       else {
/* 627 */         log.debug("Server has returned unknown response type on ping request! Time - " + (endTime.longValue() - startTime.longValue()));
/*     */       }
/*     */ 
/*     */     }
/*     */     catch (TimeoutException e)
/*     */     {
/* 634 */       if (e.getMessage().contains("busy")) {
/* 635 */         log.debug("Server sent a response that it cannot answer to the request because it is busy");
/*     */       }
/*     */       else
/* 638 */         throw e;
/*     */     }
/*     */   }
/*     */ 
/*     */   private void pingConnection()
/*     */   {
/*     */     try
/*     */     {
/* 646 */       if ((this.protocolHandler.getChildSession() != null) && (this.protocolHandler.getChildSession().isConnected()) && (this.protocolHandler.getChildSession().getLastIoTime() < System.currentTimeMillis() - 30000L))
/*     */       {
/* 650 */         log.debug("Pinging secondary connection ...");
/* 651 */         pingIoSession(this.protocolHandler.getChildSession());
/*     */       }
/*     */     } catch (TimeoutException e) {
/* 654 */       log.error("Client disconnected, secondary connection ping failed", e);
/*     */ 
/* 656 */       if (this.protocolHandler.getChildSession() != null) {
/* 657 */         this.protocolHandler.getChildSession().close();
/*     */       }
/*     */     }
/* 660 */     if ((this.protocolHandler.getSession() != null) && (this.protocolHandler.getSession().isConnected()) && (this.protocolHandler.getSession().getLastIoTime() < System.currentTimeMillis() - 30000L))
/*     */     {
/* 664 */       log.debug("Pinging primary connection ...");
/* 665 */       pingPrimarySession();
/*     */     }
/*     */   }
/*     */ 
/*     */   private void pingPrimarySession() {
/*     */     try {
/* 671 */       log.debug("Pinging primary connection ...");
/* 672 */       pingIoSession(this.protocolHandler.getSession());
/*     */     } catch (TimeoutException e) {
/* 674 */       log.error("Client disconnected, primary connection ping failed", e);
/* 675 */       if (this.protocolHandler.getSession() != null)
/* 676 */         this.protocolHandler.getSession().close();
/*     */     }
/*     */   }
/*     */ 
/*     */   public String getLogin()
/*     */   {
/* 682 */     return this.login;
/*     */   }
/*     */ 
/*     */   public String getPassword() {
/* 686 */     return this.password;
/*     */   }
/*     */ 
/*     */   public InetSocketAddress getAddress() {
/* 690 */     return this.address;
/*     */   }
/*     */ 
/*     */   public void setAddress(InetSocketAddress address) {
/* 694 */     this.address = address;
/*     */   }
/*     */ 
/*     */   public void terminate()
/*     */   {
/* 701 */     this.terminated = true;
/* 702 */     this.protocolHandler.terminate();
/* 703 */     this.protocolHandler = null;
/* 704 */     this.eventExecutor.shutdown();
/* 705 */     if (this.monitorThread != null) {
/* 706 */       this.monitorThread.interrupt();
/*     */       try {
/* 708 */         this.monitorThread.join(5000L);
/*     */       }
/*     */       catch (InterruptedException e) {
/*     */       }
/*     */     }
/* 713 */     this.listener = null;
/*     */   }
/*     */ 
/*     */   public void setLogin(String login) {
/* 717 */     this.login = login;
/*     */   }
/*     */ 
/*     */   public void setPassword(String password) {
/* 721 */     this.password = password;
/*     */   }
/*     */ 
/*     */   public boolean isCreateFeederSocket() {
/* 725 */     return this.createFeederSocket;
/*     */   }
/*     */ 
/*     */   public void setCreateFeederSocket(boolean createFeederSocket) {
/* 729 */     this.createFeederSocket = createFeederSocket;
/*     */   }
/*     */ 
/*     */   public void exportRemotingInterface(Class interfaceClass, Object interfaceImpl)
/*     */   {
/* 741 */     this.remoteCallSupport.exportInterface(interfaceClass, interfaceImpl);
/*     */   }
/*     */ 
/*     */   protected Object getInterfaceImplementation(String className)
/*     */   {
/* 751 */     return this.remoteCallSupport.getInterfaceImplementation(className);
/*     */   }
/*     */ 
/*     */   public Object getRemoteInterface(Class remoteInterfaceClass, Long requestTimeout)
/*     */     throws IllegalArgumentException
/*     */   {
/* 767 */     return this.remoteCallSupport.getRemoteInterface(remoteInterfaceClass, this.protocolHandler.getSession(), requestTimeout);
/*     */   }
/*     */ 
/*     */   public ProtocolMessage controlSynchRequest(ProtocolMessage message, Long timeoutTime)
/*     */     throws TimeoutException
/*     */   {
/* 781 */     if (this.synchFutureMap.size() > this.maxSynchRequestInProcess) {
/* 782 */       throw new TimeoutException("Too many requests in process");
/*     */     }
/* 784 */     if ((isOnline()) && (this.protocolHandler.getSession() != null) && (this.protocolHandler.getSession().isConnected()))
/*     */     {
/* 786 */       return controlSynchRequest(message, timeoutTime, this.protocolHandler.getSession());
/*     */     }
/*     */ 
/* 789 */     log.error("Client not online");
/* 790 */     throw new TimeoutException("Client not connected");
/*     */   }
/*     */ 
/*     */   private ProtocolMessage controlSynchRequest(ProtocolMessage message, Long timeoutTime, IoSession session)
/*     */     throws TimeoutException
/*     */   {
/* 796 */     message.put("reqid", String.valueOf(getNextId()));
/* 797 */     SynchRequestFuture srf = new SynchRequestFuture();
/* 798 */     boolean isError = false;
/*     */     try {
/* 800 */       synchronized (this.synchFutureMap) {
/* 801 */         this.synchFutureMap.put(message.getSynchRequestId(), srf);
/*     */       }
/* 803 */       WriteFuture future = session.write(message);
/* 804 */       future.join(timeoutTime.longValue());
/* 805 */       if (future.isWritten()) {
/* 806 */         srf.setContinueProcess(true);
/* 807 */         while (srf.isContinueProcess()) {
/* 808 */           synchronized (srf) {
/* 809 */             srf.setContinueProcess(false);
/*     */             try {
/* 811 */               srf.wait(timeoutTime.longValue());
/*     */             }
/*     */             catch (InterruptedException e) {
/* 814 */               e.printStackTrace();
/*     */             }
/*     */           }
/*     */         }
/* 818 */         synchronized (this.synchFutureMap) {
/* 819 */           this.synchFutureMap.remove(message.getSynchRequestId());
/*     */         }
/* 821 */         if ((srf.getResponse() == null) && (srf.isRequestInProcess())) {
/* 822 */           throw new TimeoutException("Server operation timeout. Request: " + message);
/*     */         }
/* 824 */         if (srf.getResponse() == null) {
/* 825 */           throw new TimeoutException("No response from server. Request: " + message);
/*     */         }
/*     */ 
/* 828 */         if ((srf.getResponse() instanceof ErrorResponseMessage)) {
/* 829 */           error = (ErrorResponseMessage)srf.getResponse();
/*     */ 
/* 831 */           if ((((ErrorResponseMessage)error).getReason() != null) && (((ErrorResponseMessage)error).getReason().equals("busy.00")))
/*     */           {
/* 833 */             throw new TimeoutException("Server is busy. Request: " + message);
/*     */           }
/*     */         }
/*     */ 
/* 837 */         Object error = srf.getResponse();
/*     */         return error;
/*     */       }
/* 840 */       throw new TimeoutException("Client can't send synch request in timout time. Request: " + message);
/*     */     }
/*     */     catch (Exception e)
/*     */     {
/* 845 */       log.error("Error send synch request", e);
/* 846 */       throw new TimeoutException(e.getMessage());
/*     */     } finally {
/* 848 */       synchronized (this.synchFutureMap) {
/* 849 */         this.synchFutureMap.remove(message.getSynchRequestId());
/*     */       }
/* 850 */     }throw localObject5;
/*     */   }
/*     */ 
/*     */   public Map<Long, SynchRequestFuture> getSynchFutureMap()
/*     */   {
/* 855 */     return this.synchFutureMap;
/*     */   }
/*     */ 
/*     */   private Long getNextId() {
/* 859 */     synchronized (this.requestId) {
/* 860 */       Long localLong1 = this.requestId; Long localLong2 = this.requestId = Long.valueOf(this.requestId.longValue() + 1L);
/* 861 */       return this.requestId;
/*     */     }
/*     */   }
/*     */ 
/*     */   protected SynchRequestFuture getFuture(Long requestId) {
/* 866 */     synchronized (this.synchFutureMap) {
/* 867 */       return (SynchRequestFuture)this.synchFutureMap.get(requestId);
/*     */     }
/*     */   }
/*     */ 
/*     */   public boolean isPingConnection()
/*     */   {
/* 875 */     return this.pingConnection;
/*     */   }
/*     */ 
/*     */   public void setPingConnection(boolean pingConnection)
/*     */   {
/* 885 */     this.pingConnection = pingConnection;
/*     */   }
/*     */ 
/*     */   public Long getPingTimeout()
/*     */   {
/* 892 */     return this.pingTimeout;
/*     */   }
/*     */ 
/*     */   public void setPingTimeout(Long pingTimeout)
/*     */   {
/* 902 */     this.pingTimeout = pingTimeout;
/*     */   }
/*     */ 
/*     */   public boolean isUseSsl()
/*     */   {
/* 909 */     return this.useSsl;
/*     */   }
/*     */ 
/*     */   public void setUseSsl(boolean useSsl)
/*     */   {
/* 919 */     this.useSsl = useSsl;
/*     */   }
/*     */ 
/*     */   public void addIsolationCriteria(IsolationCriteria criteria)
/*     */   {
/* 932 */     this.eventExecutor.addIsolationCriteria(criteria);
/*     */   }
/*     */ 
/*     */   public void clearThreadIsolationCriteria()
/*     */   {
/* 939 */     this.eventExecutor.clearIsolationCriterias();
/*     */   }
/*     */ 
/*     */   public StreamListener getStreamListener()
/*     */   {
/* 949 */     return this.streamListener;
/*     */   }
/*     */ 
/*     */   public void setStreamListener(StreamListener streamListener)
/*     */   {
/* 961 */     this.streamListener = streamListener;
/*     */   }
/*     */ 
/*     */   public int getMaxSynchRequestInProcess() {
/* 965 */     return this.maxSynchRequestInProcess;
/*     */   }
/*     */ 
/*     */   public void setMaxSynchRequestInProcess(int maxSynchRequestInProcess) {
/* 969 */     this.maxSynchRequestInProcess = maxSynchRequestInProcess;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\transport-client-2.3.78.jar
 * Qualified Name:     com.dukascopy.transport.client.TransportClient
 * JD-Core Version:    0.6.0
 */