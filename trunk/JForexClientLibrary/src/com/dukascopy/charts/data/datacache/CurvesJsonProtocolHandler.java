/*      */ package com.dukascopy.charts.data.datacache;
/*      */ 
/*      */ import SevenZip.Compression.LZMA.Decoder;
/*      */ import com.dukascopy.api.Instrument;
/*      */ import com.dukascopy.api.OfferSide;
/*      */ import com.dukascopy.api.Period;
/*      */ import com.dukascopy.charts.data.datacache.listener.DataFeedServerConnectionListener;
/*      */ import com.dukascopy.dds2.greed.agent.strategy.StratUtils;
/*      */ import com.dukascopy.dds2.greed.gui.component.filechooser.CancelLoadingException;
/*      */ import com.dukascopy.dds2.greed.gui.component.filechooser.FileProgressListener;
/*      */ import com.dukascopy.dds2.greed.util.FilePathManager;
/*      */ import com.dukascopy.transport.client.BlockingBinaryStream;
/*      */ import com.dukascopy.transport.client.ClientListener;
/*      */ import com.dukascopy.transport.client.DisconnectReason;
/*      */ import com.dukascopy.transport.client.SecurityExceptionHandler;
/*      */ import com.dukascopy.transport.client.StreamListener;
/*      */ import com.dukascopy.transport.client.TransportClient;
/*      */ import com.dukascopy.transport.client.events.DisconnectedEvent;
/*      */ import com.dukascopy.transport.common.datafeed.FileAlreadyExistException;
/*      */ import com.dukascopy.transport.common.datafeed.FileType;
/*      */ import com.dukascopy.transport.common.datafeed.KeyNotFoundException;
/*      */ import com.dukascopy.transport.common.datafeed.StorageException;
/*      */ import com.dukascopy.transport.common.msg.ProtocolMessage;
/*      */ import com.dukascopy.transport.common.msg.datafeed.CandleHistoryGroupMessage;
/*      */ import com.dukascopy.transport.common.msg.datafeed.CandleSubscribeRequestMessage;
/*      */ import com.dukascopy.transport.common.msg.datafeed.DFHistoryStartRequestMessage;
/*      */ import com.dukascopy.transport.common.msg.datafeed.DFHistoryStartResponseMessage;
/*      */ import com.dukascopy.transport.common.msg.datafeed.OrderGroupsBinaryMessage;
/*      */ import com.dukascopy.transport.common.msg.datafeed.OrderHistoryRequestMessage;
/*      */ import com.dukascopy.transport.common.msg.request.QuitRequestMessage;
/*      */ import com.dukascopy.transport.common.msg.response.ErrorResponseMessage;
/*      */ import com.dukascopy.transport.common.msg.strategy.FileItem;
/*      */ import com.dukascopy.transport.common.msg.strategy.FileItem.AccessType;
/*      */ import com.dukascopy.transport.common.msg.strategy.FileItem.Command;
/*      */ import com.dukascopy.transport.common.msg.strategy.FileItem.ErrorMessage;
/*      */ import com.dukascopy.transport.common.msg.strategy.FileMngRequestMessage;
/*      */ import com.dukascopy.transport.common.msg.strategy.FileMngResponseMessage;
/*      */ import com.dukascopy.transport.common.msg.strategy.StrategyParameter;
/*      */ import java.io.ByteArrayInputStream;
/*      */ import java.io.ByteArrayOutputStream;
/*      */ import java.io.File;
/*      */ import java.io.FileNotFoundException;
/*      */ import java.io.IOException;
/*      */ import java.net.InetSocketAddress;
/*      */ import java.net.MalformedURLException;
/*      */ import java.net.URL;
/*      */ import java.security.cert.CertificateException;
/*      */ import java.security.cert.X509Certificate;
/*      */ import java.text.DateFormat;
/*      */ import java.text.SimpleDateFormat;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Collections;
/*      */ import java.util.Comparator;
/*      */ import java.util.Date;
/*      */ import java.util.HashMap;
/*      */ import java.util.List;
/*      */ import java.util.Map;
/*      */ import java.util.SortedSet;
/*      */ import java.util.TimeZone;
/*      */ import java.util.Timer;
/*      */ import java.util.TimerTask;
/*      */ import java.util.TreeSet;
/*      */ import java.util.concurrent.LinkedBlockingQueue;
/*      */ import java.util.concurrent.ThreadFactory;
/*      */ import java.util.concurrent.ThreadPoolExecutor;
/*      */ import java.util.concurrent.TimeUnit;
/*      */ import java.util.concurrent.TimeoutException;
/*      */ import java.util.concurrent.atomic.AtomicInteger;
/*      */ import java.util.concurrent.atomic.AtomicLong;
/*      */ import java.util.concurrent.locks.Lock;
/*      */ import java.util.concurrent.locks.ReentrantLock;
/*      */ import java.util.regex.Matcher;
/*      */ import java.util.regex.Pattern;
/*      */ import java.util.zip.GZIPInputStream;
/*      */ import org.slf4j.Logger;
/*      */ import org.slf4j.LoggerFactory;
/*      */ 
/*      */ class CurvesJsonProtocolHandler
/*      */   implements ICurvesProtocolHandler, ClientListener, StreamListener
/*      */ {
/*      */   private static final Logger LOGGER;
/*      */   private static final DateFormat DATE_FORMAT;
/*      */   private TransportClient transportClient;
/*   68 */   private AtomicInteger inProcess = new AtomicInteger(0);
/*   69 */   private AtomicLong lastAccess = new AtomicLong();
/*      */   private String historyServerUrl;
/*      */   private IAuthenticator authenticator;
/*      */   private String authorizationResponse;
/*      */   private volatile boolean connecting;
/*      */   private volatile boolean disconnecting;
/*      */   private volatile int attempt;
/*   78 */   private AtomicInteger requestNumber = new AtomicInteger(0);
/*      */ 
/*   80 */   private final Map<Integer, SortedSet<ProtocolMessage>> requestResponse = new HashMap();
/*      */ 
/*   82 */   private final Lock thisLock = new ReentrantLock();
/*      */   private long lastAuthenticationTime;
/*      */   private Timer disconnectTimer;
/*   87 */   private Pattern httpResponse50X = Pattern.compile("HTTP response code.{1,5}5\\d\\d");
/*      */ 
/*   89 */   private final List<DataFeedServerConnectionListener> dfsConnectionListeners = new ArrayList();
/*      */ 
/*      */   public CurvesJsonProtocolHandler() {
/*   92 */     this.disconnectTimer = new Timer("Curves connection closing timer", true);
/*   93 */     this.disconnectTimer.schedule(new TimerTask()
/*      */     {
/*      */       public void run() {
/*   96 */         CurvesJsonProtocolHandler.this.thisLock.lock();
/*      */         try {
/*   98 */           if ((CurvesJsonProtocolHandler.this.transportClient != null) && (CurvesJsonProtocolHandler.this.transportClient.isOnline()) && (CurvesJsonProtocolHandler.this.inProcess.get() == 0) && (CurvesJsonProtocolHandler.this.lastAccess.get() + 75000L < System.currentTimeMillis())) {
/*   99 */             CurvesJsonProtocolHandler.LOGGER.debug("Disconnecting from history server, inactivity timeout");
/*      */             try {
/*  101 */               CurvesJsonProtocolHandler.this.transportClient.controlSynchRequest(new QuitRequestMessage(), Long.valueOf(1000L));
/*      */             }
/*      */             catch (TimeoutException e) {
/*      */             }
/*  105 */             CurvesJsonProtocolHandler.access$502(CurvesJsonProtocolHandler.this, null);
/*  106 */             CurvesJsonProtocolHandler.this.transportClient.disconnect();
/*  107 */             CurvesJsonProtocolHandler.access$602(CurvesJsonProtocolHandler.this, true);
/*      */           }
/*  109 */           if ((CurvesJsonProtocolHandler.this.inProcess.get() == 0) && (CurvesJsonProtocolHandler.this.lastAccess.get() + 75000L < System.currentTimeMillis()))
/*      */           {
/*  111 */             CurvesJsonProtocolHandler.access$502(CurvesJsonProtocolHandler.this, null);
/*      */           }
/*      */         } finally {
/*  114 */           CurvesJsonProtocolHandler.this.thisLock.unlock();
/*      */         }
/*      */ 
/*  120 */         long startTime = System.currentTimeMillis();
/*  121 */         while ((CurvesJsonProtocolHandler.this.disconnecting) && (startTime + 10000L > System.currentTimeMillis())) {
/*      */           try {
/*  123 */             Thread.sleep(100L);
/*      */           }
/*      */           catch (InterruptedException e)
/*      */           {
/*      */           }
/*      */         }
/*  129 */         CurvesJsonProtocolHandler.access$602(CurvesJsonProtocolHandler.this, false);
/*      */       }
/*      */     }
/*      */     , 10000L, 10000L);
/*      */   }
/*      */ 
/*      */   public void connect(IAuthenticator authenticator, String userName, String instanceId, String historyServerUrl)
/*      */   {
/*  136 */     synchronized (this.requestResponse) {
/*  137 */       this.thisLock.lock();
/*      */       try {
/*  139 */         if (this.transportClient != null) {
/*  140 */           disconnect();
/*      */         }
/*  142 */         this.authenticator = authenticator;
/*  143 */         if (historyServerUrl != null) {
/*  144 */           this.historyServerUrl = historyServerUrl.trim();
/*  145 */           if (!historyServerUrl.endsWith("/")) {
/*  146 */             this.historyServerUrl = new StringBuilder().append(historyServerUrl).append("/").toString();
/*      */           }
/*      */         }
/*  149 */         ThreadFactory threadFactory = new ThreadFactory() {
/*  150 */           final AtomicInteger threadNumber = new AtomicInteger(1);
/*      */ 
/*      */           public Thread newThread(Runnable r) {
/*  153 */             Thread thread = new Thread(r, "DFS_Mina_Thread_" + this.threadNumber.getAndIncrement());
/*  154 */             if (!thread.isDaemon()) {
/*  155 */               thread.setDaemon(true);
/*      */             }
/*  157 */             return thread;
/*      */           }
/*      */         };
/*  160 */         this.transportClient = new TransportClient(null, null, null, this, new ThreadPoolExecutor(1, 5, 5L, TimeUnit.SECONDS, new LinkedBlockingQueue(), threadFactory));
/*  161 */         this.transportClient.setListener(this);
/*  162 */         this.transportClient.setStreamListener(this);
/*  163 */         this.transportClient.setSecurityExceptionHandler(new SecurityExceptionHandler()
/*      */         {
/*      */           public boolean isIgnoreSecurityException(X509Certificate[] chain, String authType, CertificateException exception) {
/*  166 */             CurvesJsonProtocolHandler.LOGGER.warn("Security exception: " + exception);
/*  167 */             return true;
/*      */           }
/*      */         });
/*  170 */         String implementationVersion = CurvesJsonProtocolHandler.class.getPackage().getImplementationVersion();
/*  171 */         this.transportClient.setUserAgent(new StringBuilder().append("JForex cache (build ").append(implementationVersion == null ? "SNAPSHOT" : implementationVersion).append(")").toString());
/*  172 */         this.transportClient.setPoolSize(2);
/*  173 */         this.transportClient.setUseSsl(true);
/*      */ 
/*  175 */         LOGGER.debug(new StringBuilder().append("Setting curves server parameters to transport client, login [").append(userName).append("]").toString());
/*  176 */         this.transportClient.setLogin(new StringBuilder().append(userName).append(" ").append(instanceId).toString());
/*      */       } finally {
/*  178 */         this.thisLock.unlock();
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   public void disconnect() {
/*  184 */     this.connecting = false;
/*  185 */     synchronized (this.requestResponse) {
/*  186 */       this.thisLock.lock();
/*      */       try {
/*  188 */         this.requestResponse.clear();
/*  189 */         this.requestResponse.notifyAll();
/*  190 */         if (this.transportClient != null) {
/*  191 */           if (this.transportClient.isOnline())
/*      */             try {
/*  193 */               this.transportClient.controlSynchRequest(new QuitRequestMessage(), Long.valueOf(3000L));
/*      */             }
/*      */             catch (TimeoutException e)
/*      */             {
/*      */             }
/*  198 */           this.transportClient.disconnect();
/*  199 */           this.transportClient.terminate();
/*  200 */           this.transportClient = null;
/*      */         }
/*  202 */         this.authorizationResponse = null;
/*      */       } finally {
/*  204 */         this.thisLock.unlock();
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   public void close() {
/*  210 */     synchronized (this.requestResponse) {
/*  211 */       this.thisLock.lock();
/*      */       try {
/*  213 */         if (this.transportClient != null) {
/*  214 */           disconnect();
/*      */         }
/*  216 */         this.disconnectTimer.cancel();
/*  217 */         this.disconnectTimer = null;
/*      */       } finally {
/*  219 */         this.thisLock.unlock();
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   private boolean fastConnect() throws NotConnectedException, InterruptedException {
/*  225 */     if ((this.disconnecting) || (this.connecting))
/*      */     {
/*  228 */       Thread.sleep(20L);
/*  229 */     } else if ((this.thisLock.tryLock(20L, TimeUnit.MILLISECONDS)) && 
/*  230 */       (!this.connecting)) {
/*      */       try {
/*  232 */         if (this.transportClient == null) {
/*  233 */           throw new NotConnectedException("Cannot connect to data feed server, transport not initialized");
/*      */         }
/*  235 */         if (!this.transportClient.isOnline()) {
/*  236 */           this.connecting = true;
/*      */           try {
/*  238 */             this.attempt = 1;
/*  239 */             if (this.authorizationResponse == null) {
/*  240 */               this.authorizationResponse = this.authenticator.authenticate();
/*  241 */               if (this.authorizationResponse == null) {
/*      */                 try {
/*  243 */                   Thread.sleep(3000L);
/*      */                 }
/*      */                 catch (InterruptedException e) {
/*      */                 }
/*  247 */                 throw new NotConnectedException("Cannot connect to authorization server");
/*      */               }
/*      */             }
/*  250 */             applyAuthorizationResponse();
/*  251 */             LOGGER.debug(new StringBuilder().append("Connecting to data feed server [").append(this.transportClient.getAddress().toString()).append("]...").toString());
/*  252 */             this.transportClient.connect();
/*  253 */             long startTime = System.currentTimeMillis();
/*  254 */             while ((!this.transportClient.isOnline()) && (startTime + 60000L > System.currentTimeMillis()) && (this.connecting))
/*      */             {
/*  256 */               this.thisLock.unlock();
/*      */               try {
/*  258 */                 Thread.sleep(20L);
/*      */               } catch (InterruptedException e) {
/*      */               }
/*      */               finally {
/*  262 */                 this.thisLock.lock();
/*      */               }
/*      */             }
/*  265 */             if (!this.transportClient.isOnline())
/*      */             {
/*  267 */               this.authorizationResponse = null;
/*      */               try {
/*  269 */                 Thread.sleep(3000L);
/*      */               }
/*      */               catch (InterruptedException e) {
/*      */               }
/*  273 */               throw new NotConnectedException(new StringBuilder().append("Cannot connect to data feed server after [").append(this.attempt).append("] attempts trying for a [").append((System.currentTimeMillis() - startTime) / 1000L).append("] seconds").toString());
/*      */             }
/*      */           } finally {
/*  276 */             this.connecting = false;
/*      */           }
/*      */         }
/*      */       } finally {
/*  280 */         this.thisLock.unlock();
/*      */       }
/*  282 */       return true;
/*      */     }
/*      */ 
/*  285 */     return false;
/*      */   }
/*      */ 
/*      */   public void connect(LoadingProgressListener loadingProgress) throws NotConnectedException {
/*      */     try {
/*  290 */       while (!loadingProgress.stopJob()) {
/*  291 */         boolean connected = fastConnect();
/*  292 */         if (connected)
/*  293 */           return;
/*      */       }
/*      */     }
/*      */     catch (InterruptedException e) {
/*  297 */       LOGGER.error(e.getMessage(), e);
/*      */     }
/*      */   }
/*      */ 
/*      */   private void applyAuthorizationResponse() throws NotConnectedException {
/*  302 */     if (this.authorizationResponse == null) {
/*  303 */       throw new NotConnectedException("Cannot connect to authorization server");
/*      */     }
/*  305 */     this.authorizationResponse = this.authorizationResponse.trim();
/*      */ 
/*  307 */     int index = this.authorizationResponse.indexOf('@');
/*  308 */     if (index == -1) {
/*  309 */       throw new NotConnectedException(new StringBuilder().append("Authorization for data feed server failed, incorrect response [").append(this.authorizationResponse).append("]").toString());
/*      */     }
/*  311 */     String feedDataServerURL = this.authorizationResponse.substring(0, index);
/*  312 */     String newTicket = this.authorizationResponse.substring(index + 1);
/*      */ 
/*  314 */     if ("null".equals(feedDataServerURL)) {
/*  315 */       throw new NotConnectedException(new StringBuilder().append("Authorization for data feed server failed, incorrect response [").append(this.authorizationResponse).append("]").toString());
/*      */     }
/*      */ 
/*  321 */     int semicolonIndex = feedDataServerURL.indexOf(':');
/*      */     int port;
/*      */     String host;
/*      */     int port;
/*  322 */     if (semicolonIndex != -1) {
/*  323 */       String host = feedDataServerURL.substring(0, semicolonIndex);
/*      */       int port;
/*  324 */       if (semicolonIndex + 1 >= feedDataServerURL.length()) {
/*  325 */         LOGGER.warn("port not set, using default 443");
/*  326 */         port = 443;
/*      */       } else {
/*  328 */         port = Integer.parseInt(feedDataServerURL.substring(semicolonIndex + 1));
/*      */       }
/*      */     } else {
/*  331 */       host = feedDataServerURL;
/*  332 */       port = 443;
/*      */     }
/*      */ 
/*  335 */     this.transportClient.setAddress(new InetSocketAddress(host, port));
/*  336 */     this.transportClient.setPassword(newTicket);
/*  337 */     this.lastAuthenticationTime = System.currentTimeMillis();
/*      */   }
/*      */ 
/*      */   public Data[] loadData(Instrument instrument, Period period, OfferSide side, long from, long to, boolean forIntraperiod, LoadingProgressListener loadingProgress) throws DataCacheException
/*      */   {
/*  342 */     return loadData(instrument, period, side, from, to, false, forIntraperiod, loadingProgress);
/*      */   }
/*      */ 
/*      */   public Data[] loadCandles(String instrumentName, Period period, long from, long to, LoadingProgressListener loadingProgress) throws DataCacheException
/*      */   {
/*  347 */     wait2SecsFromStart();
/*  348 */     boolean incremented = false;
/*  349 */     while (!loadingProgress.stopJob())
/*      */       try {
/*  351 */         if (this.thisLock.tryLock(20L, TimeUnit.MILLISECONDS))
/*      */           try {
/*  353 */             this.inProcess.incrementAndGet();
/*  354 */             incremented = true;
/*      */           }
/*      */           finally {
/*  357 */             this.thisLock.unlock();
/*      */           }
/*      */       }
/*      */       catch (InterruptedException e) {
/*  361 */         LOGGER.error(e.getMessage(), e);
/*      */       }
/*      */     try
/*      */     {
/*  365 */       if (loadingProgress.stopJob()) {
/*  366 */         e = null;
/*      */         return e;
/*      */       }
/*  368 */       assert (from == DataCacheUtils.getCandleStart(period, from));
/*  369 */       assert (to == DataCacheUtils.getCandleStart(period, to));
/*      */ 
/*  371 */       int tryCount = 0;
/*  372 */       SortedSet finalResponseMessages = null;
/*  373 */       long startTime = System.nanoTime();
/*      */ 
/*  375 */       while ((tryCount < 5) && (finalResponseMessages == null)) {
/*  376 */         if (loadingProgress.stopJob()) {
/*  377 */           Object localObject2 = null;
/*      */           return localObject2;
/*      */         }
/*      */         try
/*      */         {
/*  381 */           connect(loadingProgress);
/*      */         } catch (NotConnectedException e) {
/*  383 */           LOGGER.error(e.getMessage(), e);
/*  384 */           tryCount++;
/*  385 */         }continue;
/*      */ 
/*  387 */         tryCount++;
/*  388 */         if (LOGGER.isTraceEnabled()) {
/*  389 */           Object dateFormat = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss SSS");
/*  390 */           ((DateFormat)dateFormat).setTimeZone(TimeZone.getTimeZone("GMT"));
/*  391 */           LOGGER.trace(new StringBuilder().append("Downloading data for instrument [").append(instrumentName).append("], period [").append(period).append("], from [").append(((DateFormat)dateFormat).format(new Date(from))).append("] to [").append(((DateFormat)dateFormat).format(new Date(to))).append("] attempt [").append(tryCount).append("]").toString());
/*      */         }
/*      */ 
/*  395 */         int requestId = this.requestNumber.getAndIncrement();
/*      */ 
/*  397 */         CandleSubscribeRequestMessage message = new CandleSubscribeRequestMessage();
/*      */ 
/*  399 */         message.setInstrument(instrumentName);
/*  400 */         message.setStartTime(Long.valueOf(from / 1000L));
/*  401 */         message.setEndTime(Long.valueOf(to / 1000L));
/*  402 */         message.setPeriod(Long.valueOf(period.getInterval() / 1000L));
/*  403 */         message.setRequestId(Integer.valueOf(requestId));
/*  404 */         message.setVolumesInDouble(true);
/*      */ 
/*  406 */         synchronized (this.requestResponse)
/*      */         {
/*  408 */           this.requestResponse.put(Integer.valueOf(requestId), null);
/*      */         }
/*  410 */         this.thisLock.lock();
/*      */         try {
/*  412 */           if (this.transportClient == null) {
/*  413 */             throw new NotConnectedException("Cannot connect to data feed server, transport not initialized");
/*      */           }
/*      */ 
/*  416 */           this.transportClient.controlRequest(message);
/*      */         } finally {
/*  418 */           this.thisLock.unlock();
/*      */         }
/*  420 */         label1086: synchronized (this.requestResponse) {
/*  421 */           long requestTime = System.currentTimeMillis();
/*      */ 
/*  423 */           while (this.requestResponse.containsKey(Integer.valueOf(requestId)))
/*      */           {
/*  429 */             SortedSet responseMessages = (SortedSet)this.requestResponse.get(Integer.valueOf(requestId));
/*      */             int i;
/*  430 */             if ((responseMessages != null) && (!responseMessages.isEmpty()))
/*      */             {
/*  432 */               if (!(responseMessages.first() instanceof CandleHistoryGroupMessage))
/*      */               {
/*  434 */                 this.requestResponse.remove(Integer.valueOf(requestId));
/*  435 */                 finalResponseMessages = responseMessages;
/*  436 */                 break label1086;
/*      */               }
/*  438 */               if (((responseMessages.last() instanceof CandleHistoryGroupMessage)) && (((CandleHistoryGroupMessage)responseMessages.last()).isHistoryFinished().booleanValue()))
/*      */               {
/*  442 */                 i = 0;
/*  443 */                 boolean done = true;
/*  444 */                 for (ProtocolMessage responseMessage : responseMessages) {
/*  445 */                   CandleHistoryGroupMessage candleMessage = (CandleHistoryGroupMessage)responseMessage;
/*  446 */                   if (candleMessage.getMessageOrder().intValue() != i) {
/*  447 */                     done = false;
/*  448 */                     break;
/*      */                   }
/*  450 */                   i++;
/*      */                 }
/*  452 */                 if (done) {
/*  453 */                   this.requestResponse.remove(Integer.valueOf(requestId));
/*  454 */                   finalResponseMessages = responseMessages;
/*  455 */                   break label1086;
/*      */                 }
/*      */               }
/*      */             }
/*      */ 
/*  460 */             if ((loadingProgress.stopJob()) && (requestTime + 60000L < System.currentTimeMillis()))
/*      */             {
/*  463 */               this.requestResponse.remove(Integer.valueOf(requestId));
/*  464 */               i = null;
/*      */ 
/*  554 */               if (incremented) {
/*  555 */                 this.lastAccess.set(System.currentTimeMillis());
/*  556 */                 this.inProcess.decrementAndGet(); } return i;
/*      */             }
/*  466 */             if (requestTime + 120000L < System.currentTimeMillis())
/*      */             {
/*  469 */               this.requestResponse.remove(Integer.valueOf(requestId));
/*  470 */               finalResponseMessages = new TreeSet();
/*  471 */               DateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss SSS");
/*  472 */               dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
/*  473 */               ErrorResponseMessage responseMessage = new ErrorResponseMessage(new StringBuilder().append("Request for data for instrument [").append(instrumentName).append("], period [").append(period).append("], from [").append(dateFormat.format(new Date(from))).append("], to [").append(dateFormat.format(new Date(to))).append("] attempt [").append(tryCount).append("] timed out").toString());
/*  474 */               responseMessage.setRequestId(Integer.valueOf(requestId));
/*  475 */               finalResponseMessages.add(responseMessage);
/*  476 */               break label1086;
/*      */             }
/*      */ 
/*      */             try
/*      */             {
/*  482 */               this.requestResponse.wait(1000L);
/*      */             }
/*      */             catch (InterruptedException e)
/*      */             {
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/*  490 */       if ((finalResponseMessages != null) && (!finalResponseMessages.isEmpty())) {
/*  491 */         ProtocolMessage firstMessage = (ProtocolMessage)finalResponseMessages.first();
/*  492 */         if ((firstMessage instanceof CandleHistoryGroupMessage))
/*      */           try {
/*  494 */             List data = new ArrayList();
/*  495 */             for (ProtocolMessage protocolMessage : finalResponseMessages) {
/*  496 */               CurvesProtocolUtil.parseCandles(((CandleHistoryGroupMessage)protocolMessage).getCandles(), false, data);
/*      */             }
/*  498 */             double processTime = (System.nanoTime() - startTime) / 1000000.0D;
/*  499 */             if ((LOGGER.isTraceEnabled()) || ((processTime > 3000.0D) && (LOGGER.isDebugEnabled())) || (processTime > 20000.0D)) {
/*  500 */               Object dateFormat = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss SSS");
/*  501 */               ((DateFormat)dateFormat).setTimeZone(TimeZone.getTimeZone("GMT"));
/*  502 */               String logMessage = new StringBuilder().append("Data downloaded in [").append(processTime).append("] milliseconds, size [").append(data.size()).append("]").toString();
/*  503 */               if (!data.isEmpty()) {
/*  504 */                 logMessage = new StringBuilder().append(logMessage).append(", first data time [").append(((DateFormat)dateFormat).format(new Date(((Data)data.get(0)).time))).append("], last data time [").append(((DateFormat)dateFormat).format(new Date(((Data)data.get(data.size() - 1)).time))).append("]").toString();
/*      */               }
/*  506 */               logMessage = new StringBuilder().append(logMessage).append(", request - instrument [").append(instrumentName).append("] period [").append(period).append("] from [").append(((DateFormat)dateFormat).format(new Date(from))).append("] to [").append(((DateFormat)dateFormat).format(new Date(to))).append("]").toString();
/*  507 */               if (processTime > 20000.0D)
/*  508 */                 LOGGER.warn(logMessage);
/*  509 */               else if (processTime > 3000.0D)
/*  510 */                 LOGGER.debug(logMessage);
/*      */               else {
/*  512 */                 LOGGER.trace(logMessage);
/*      */               }
/*      */             }
/*  515 */             Data[] dataArray = (Data[])data.toArray(new Data[data.size()]);
/*      */ 
/*  517 */             if (dataArray.length > 0) {
/*  518 */               long lastTime = dataArray[0].time;
/*  519 */               for (int i = 1; i < dataArray.length; i++) {
/*  520 */                 if (dataArray[i].time < lastTime) {
/*  521 */                   throw new DataCacheException("Data consistency error, not sorted");
/*      */                 }
/*      */               }
/*      */             }
/*      */ 
/*  526 */             if (dataArray.length > 0) {
/*  527 */               firstDataTime = dataArray[0].time;
/*  528 */               if (firstDataTime / 1000L < from / 1000L) {
/*  529 */                 DateFormat format = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss SSS");
/*  530 */                 format.setTimeZone(TimeZone.getTimeZone("GMT"));
/*  531 */                 throw new DataCacheException(new StringBuilder().append("Wrong data from data feed server, time of the first data element [").append(format.format(new Date(firstDataTime))).append(" (").append(firstDataTime).append(")] is less than requested interval start time , request - instrument [").append(instrumentName).append("] period [").append(period).append("] from [").append(format.format(new Date(from))).append("] to [").append(format.format(new Date(to))).append("]").toString());
/*      */               }
/*      */             }
/*      */ 
/*  535 */             long firstDataTime = dataArray;
/*      */ 
/*  554 */             if (incremented) {
/*  555 */               this.lastAccess.set(System.currentTimeMillis());
/*  556 */               this.inProcess.decrementAndGet(); } return firstDataTime;
/*      */           }
/*      */           catch (IOException e)
/*      */           {
/*  537 */             throw new DataCacheException(e);
/*      */           }
/*  539 */         if ((firstMessage instanceof ErrorResponseMessage)) {
/*  540 */           throw new DataCacheException(((ErrorResponseMessage)firstMessage).getReason());
/*      */         }
/*  542 */         throw new DataCacheException("Unknown response");
/*      */       }
/*      */ 
/*  547 */       Object dateFormat = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss SSS");
/*  548 */       ((DateFormat)dateFormat).setTimeZone(TimeZone.getTimeZone("GMT"));
/*  549 */       throw new DataCacheException(new StringBuilder().append("Cannot execute request for data for instrument [").append(instrumentName).append("], period [").append(period).append("], from [").append(((DateFormat)dateFormat).format(new Date(from))).append("] to [").append(((DateFormat)dateFormat).format(new Date(to))).append("] attempt [").append(tryCount).append("]").toString());
/*      */     }
/*      */     finally
/*      */     {
/*  554 */       if (incremented) {
/*  555 */         this.lastAccess.set(System.currentTimeMillis());
/*  556 */         this.inProcess.decrementAndGet(); } 
/*  556 */     }throw localObject6;
/*      */   }
/*      */ 
/*      */   public Data[] loadFile(Instrument instrument, Period period, OfferSide side, long chunkStart, LoadingProgressListener loadingProgress)
/*      */     throws DataCacheException
/*      */   {
/*  564 */     long firstChunkCandle = DataCacheUtils.getFirstCandleInChunkFast(period, chunkStart);
/*  565 */     if (DataCacheUtils.getChunkEnd(period, firstChunkCandle) < FeedDataProvider.getDefaultInstance().getTimeOfFirstCandle(instrument, period)) {
/*  566 */       return new Data[0];
/*      */     }
/*  568 */     if (this.historyServerUrl != null) {
/*  569 */       String fileName = DataCacheUtils.getChunkFile(instrument, period, side, firstChunkCandle, 5).getPath();
/*  570 */       int fileDownloadAttempts = 3;
/*  571 */       while (fileDownloadAttempts > 0) {
/*      */         try {
/*  573 */           URL fileUrl = new URL(new StringBuilder().append(this.historyServerUrl).append(fileName.substring(FilePathManager.getInstance().getCacheDirectory().length()).replace('\\', '/')).toString());
/*  574 */           ByteArrayOutputStream os = new ByteArrayOutputStream();
/*  575 */           StratUtils.returnURL(fileUrl, os);
/*  576 */           os.close();
/*  577 */           byte[] bytesData = os.toByteArray();
/*  578 */           if (bytesData.length != 0) {
/*  579 */             ByteArrayInputStream is = new ByteArrayInputStream(bytesData);
/*  580 */             int propertiesSize = 5;
/*  581 */             byte[] properties = new byte[propertiesSize];
/*  582 */             int readBytes = 0;
/*      */ 
/*  584 */             while (((i = is.read(properties, readBytes, properties.length - readBytes)) > -1) && (readBytes < properties.length)) {
/*  585 */               readBytes += i;
/*      */             }
/*  587 */             if (readBytes != propertiesSize) {
/*  588 */               throw new DataCacheException("7ZIP: input .lzma file is too short");
/*      */             }
/*      */ 
/*  591 */             Decoder decoder = new Decoder();
/*  592 */             if (!decoder.SetDecoderProperties(properties))
/*  593 */               throw new DataCacheException("7ZIP: Incorrect stream properties");
/*  594 */             long outSize = 0L;
/*  595 */             for (int i = 0; i < 8; i++) {
/*  596 */               int v = is.read();
/*  597 */               if (v < 0) {
/*  598 */                 throw new DataCacheException("7ZIP: Can't read stream size");
/*      */               }
/*  600 */               outSize |= v << 8 * i;
/*      */             }
/*  602 */             ByteArrayOutputStream bos = new ByteArrayOutputStream((int)outSize);
/*  603 */             if (!decoder.Code(is, bos, outSize)) {
/*  604 */               throw new DataCacheException(new StringBuilder().append("Cannot decode 7zip compressed file [").append(fileName).append("]").toString());
/*      */             }
/*  606 */             bytesData = bos.toByteArray();
/*      */           }
/*  608 */           Data[] dataArray = CurvesProtocolUtil.bytesToChunkData(bytesData, period, 5, firstChunkCandle, instrument.getPipValue());
/*  609 */           if (LOGGER.isTraceEnabled()) {
/*  610 */             DateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss SSS");
/*  611 */             dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
/*  612 */             String logMessage = new StringBuilder().append("Data file downloaded, size [").append(dataArray.length).append("]").toString();
/*  613 */             if (dataArray.length > 0) {
/*  614 */               logMessage = new StringBuilder().append(logMessage).append(", first data time [").append(dateFormat.format(new Date(dataArray[0].time))).append("], last data time [").append(dateFormat.format(new Date(dataArray[(dataArray.length - 1)].time))).append("]").toString();
/*      */             }
/*      */ 
/*  617 */             LOGGER.trace(logMessage);
/*      */           }
/*  619 */           if (dataArray.length > 0) {
/*  620 */             long lastTime = dataArray[0].time;
/*  621 */             for (int i = 1; i < dataArray.length; i++) {
/*  622 */               if (dataArray[i].time < lastTime) {
/*  623 */                 throw new DataCacheException("Data consistency error, not sorted");
/*      */               }
/*      */             }
/*      */           }
/*  627 */           return dataArray;
/*      */         } catch (MalformedURLException e) {
/*  629 */           throw new DataCacheException(e);
/*      */         } catch (FileNotFoundException e) {
/*  631 */           LOGGER.debug(new StringBuilder().append("WARN: file [").append(this.historyServerUrl).append(fileName.substring(FilePathManager.getInstance().getCacheDirectory().length()).replace('\\', '/')).append("] was not found [").append(e.getMessage()).append("]").toString());
/*      */ 
/*  633 */           return null;
/*      */         } catch (IOException e) {
/*  635 */           if ((fileDownloadAttempts > 1) && (this.httpResponse50X.matcher(e.getMessage()).find()))
/*      */           {
/*  637 */             fileDownloadAttempts--;
/*  638 */             LOGGER.debug(e.getMessage(), e);
/*      */           } else {
/*  640 */             LOGGER.warn(e.getMessage(), e);
/*      */ 
/*  642 */             return null;
/*      */           }
/*      */         }
/*      */       }
/*      */ 
/*  647 */       return null;
/*      */     }
/*      */ 
/*  650 */     return null;
/*      */   }
/*      */ 
/*      */   private Data[] loadData(Instrument instrument, Period period, OfferSide side, long from, long to, boolean inProgress, boolean forIntraperiod, LoadingProgressListener loadingProgress)
/*      */     throws DataCacheException
/*      */   {
/*  656 */     if ((!inProgress) && (to < FeedDataProvider.getDefaultInstance().getTimeOfFirstCandle(instrument, period))) {
/*  657 */       return new Data[0];
/*      */     }
/*  659 */     wait2SecsFromStart();
/*  660 */     boolean incremented = false;
/*  661 */     while (!loadingProgress.stopJob())
/*      */       try {
/*  663 */         if (this.thisLock.tryLock(20L, TimeUnit.MILLISECONDS))
/*      */           try {
/*  665 */             this.inProcess.incrementAndGet();
/*  666 */             incremented = true;
/*      */           }
/*      */           finally {
/*  669 */             this.thisLock.unlock();
/*      */           }
/*      */       }
/*      */       catch (InterruptedException e) {
/*  673 */         LOGGER.error(e.getMessage(), e);
/*      */       }
/*      */     try
/*      */     {
/*  677 */       if (loadingProgress.stopJob()) {
/*  678 */         e = null;
/*      */         return e;
/*      */       }
/*  680 */       assert ((period == Period.TICK) || (inProgress) || (from == DataCacheUtils.getCandleStart(period, from)));
/*  681 */       assert ((period == Period.TICK) || (inProgress) || (to == DataCacheUtils.getCandleStart(period, to)));
/*  682 */       int tryCount = 0;
/*  683 */       SortedSet finalResponseMessages = null;
/*  684 */       long startTime = System.nanoTime();
/*      */ 
/*  686 */       while ((tryCount < 5) && (finalResponseMessages == null)) {
/*  687 */         if (loadingProgress.stopJob()) {
/*  688 */           Object localObject2 = null;
/*      */           return localObject2;
/*      */         }
/*      */         try
/*      */         {
/*  692 */           connect(loadingProgress);
/*      */         } catch (NotConnectedException e) {
/*  694 */           LOGGER.error(e.getMessage(), e);
/*  695 */           tryCount++;
/*  696 */         }continue;
/*      */ 
/*  698 */         tryCount++;
/*  699 */         if (LOGGER.isTraceEnabled()) {
/*  700 */           Object dateFormat = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss SSS");
/*  701 */           ((DateFormat)dateFormat).setTimeZone(TimeZone.getTimeZone("GMT"));
/*  702 */           LOGGER.trace(new StringBuilder().append("Downloading ").append(inProgress ? "in-progress candle " : "").append("data ").append("for instrument [").append(instrument.toString()).append("] period [").append(period).append("] side [").append(side).append(inProgress ? "" : new StringBuilder().append("] from [").append(((DateFormat)dateFormat).format(new Date(from))).toString()).append("] to [").append(((DateFormat)dateFormat).format(new Date(to))).append("] attempt [").append(tryCount).append("]").toString());
/*      */         }
/*      */ 
/*  709 */         int requestId = this.requestNumber.getAndIncrement();
/*      */ 
/*  711 */         CandleSubscribeRequestMessage message = new CandleSubscribeRequestMessage();
/*      */ 
/*  719 */         message.setInstrument(new StringBuilder().append("*").append(instrument.toString()).append(side == OfferSide.ASK ? "_Ask" : "_Bid").toString());
/*      */ 
/*  721 */         if (inProgress)
/*      */         {
/*  723 */           message.setEndTime(Long.valueOf(to));
/*      */         } else {
/*  725 */           message.setStartTime(Long.valueOf(from / 1000L));
/*  726 */           message.setEndTime(Long.valueOf(to / 1000L));
/*      */         }
/*  728 */         if (!inProgress)
/*      */         {
/*  730 */           if (period == Period.TICK)
/*  731 */             message.setPeriod(Long.valueOf(0L));
/*      */           else
/*  733 */             message.setPeriod(Long.valueOf(period.getInterval() / 1000L));
/*      */         }
/*  735 */         if (inProgress) {
/*  736 */           message.setLastCandleRequest(true);
/*      */         }
/*  738 */         message.setRequestId(Integer.valueOf(requestId));
/*  739 */         message.setVolumesInDouble(true);
/*      */ 
/*  741 */         synchronized (this.requestResponse)
/*      */         {
/*  743 */           this.requestResponse.put(Integer.valueOf(requestId), null);
/*      */         }
/*  745 */         this.thisLock.lock();
/*      */         try {
/*  747 */           if (this.transportClient == null) {
/*  748 */             throw new NotConnectedException("Cannot connect to data feed server, transport not initialized");
/*      */           }
/*      */ 
/*  751 */           this.transportClient.controlRequest(message);
/*      */         } finally {
/*  753 */           this.thisLock.unlock();
/*      */         }
/*  755 */         label1370: synchronized (this.requestResponse) {
/*  756 */           long requestTime = System.currentTimeMillis();
/*      */ 
/*  758 */           while (this.requestResponse.containsKey(Integer.valueOf(requestId)))
/*      */           {
/*  763 */             SortedSet responseMessages = (SortedSet)this.requestResponse.get(Integer.valueOf(requestId));
/*      */             int i;
/*  764 */             if ((responseMessages != null) && (!responseMessages.isEmpty()))
/*      */             {
/*  766 */               if (!(responseMessages.first() instanceof CandleHistoryGroupMessage))
/*      */               {
/*  768 */                 this.requestResponse.remove(Integer.valueOf(requestId));
/*  769 */                 finalResponseMessages = responseMessages;
/*  770 */                 break label1370;
/*      */               }
/*  772 */               if (((responseMessages.last() instanceof CandleHistoryGroupMessage)) && (((CandleHistoryGroupMessage)responseMessages.last()).isHistoryFinished().booleanValue()))
/*      */               {
/*  775 */                 i = 0;
/*  776 */                 boolean done = true;
/*  777 */                 for (ProtocolMessage responseMessage : responseMessages) {
/*  778 */                   CandleHistoryGroupMessage candleMessage = (CandleHistoryGroupMessage)responseMessage;
/*  779 */                   if (candleMessage.getMessageOrder().intValue() != i) {
/*  780 */                     done = false;
/*  781 */                     break;
/*      */                   }
/*  783 */                   i++;
/*      */                 }
/*  785 */                 if (done) {
/*  786 */                   this.requestResponse.remove(Integer.valueOf(requestId));
/*  787 */                   finalResponseMessages = responseMessages;
/*  788 */                   break label1370;
/*      */                 }
/*      */               }
/*      */             }
/*      */ 
/*  793 */             if ((loadingProgress.stopJob()) && (requestTime + 60000L < System.currentTimeMillis()))
/*      */             {
/*  795 */               this.requestResponse.remove(Integer.valueOf(requestId));
/*  796 */               i = null;
/*      */ 
/*  899 */               if (incremented) {
/*  900 */                 this.lastAccess.set(System.currentTimeMillis());
/*  901 */                 this.inProcess.decrementAndGet(); } return i;
/*      */             }
/*  798 */             if (requestTime + 120000L < System.currentTimeMillis())
/*      */             {
/*  801 */               this.requestResponse.remove(Integer.valueOf(requestId));
/*  802 */               finalResponseMessages = new TreeSet();
/*  803 */               DateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss SSS");
/*  804 */               dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
/*  805 */               ErrorResponseMessage responseMessage = new ErrorResponseMessage(new StringBuilder().append("Request for ").append(inProgress ? "in-progress candle " : "").append("data ").append("for instrument [").append(instrument.toString()).append("] period [").append(period).append("] side [").append(side).append(inProgress ? "" : new StringBuilder().append("] from [").append(dateFormat.format(new Date(from))).toString()).append("] to [").append(dateFormat.format(new Date(to))).append("] attempt [").append(tryCount).append("] timed out").toString());
/*      */ 
/*  809 */               responseMessage.setRequestId(Integer.valueOf(requestId));
/*  810 */               finalResponseMessages.add(responseMessage);
/*  811 */               break label1370;
/*      */             }
/*      */ 
/*      */             try
/*      */             {
/*  816 */               this.requestResponse.wait(1000L);
/*      */             }
/*      */             catch (InterruptedException e)
/*      */             {
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/*  824 */       if ((finalResponseMessages != null) && (!finalResponseMessages.isEmpty())) {
/*  825 */         ProtocolMessage firstMessage = (ProtocolMessage)finalResponseMessages.first();
/*  826 */         if ((firstMessage instanceof CandleHistoryGroupMessage))
/*      */           try {
/*  828 */             List data = new ArrayList();
/*  829 */             for (ProtocolMessage protocolMessage : finalResponseMessages) {
/*  830 */               if (period == Period.TICK)
/*  831 */                 CurvesProtocolUtil.parseTicks(((CandleHistoryGroupMessage)protocolMessage).getCandles(), data);
/*      */               else {
/*  833 */                 CurvesProtocolUtil.parseCandles(((CandleHistoryGroupMessage)protocolMessage).getCandles(), forIntraperiod, data);
/*      */               }
/*      */             }
/*  836 */             double processTime = (System.nanoTime() - startTime) / 1000000.0D;
/*  837 */             if ((LOGGER.isTraceEnabled()) || ((processTime > 3000.0D) && (LOGGER.isDebugEnabled())) || (processTime > 20000.0D)) {
/*  838 */               Object dateFormat = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss SSS");
/*  839 */               ((DateFormat)dateFormat).setTimeZone(TimeZone.getTimeZone("GMT"));
/*  840 */               String logMessage = new StringBuilder().append("Data downloaded in [").append(processTime).append("] milliseconds, size [").append(data.size()).append("]").toString();
/*  841 */               if (!data.isEmpty()) {
/*  842 */                 logMessage = new StringBuilder().append(logMessage).append(", first data time [").append(((DateFormat)dateFormat).format(new Date(((Data)data.get(0)).time))).append("], last data time [").append(((DateFormat)dateFormat).format(new Date(((Data)data.get(data.size() - 1)).time))).append("]").toString();
/*      */               }
/*      */ 
/*  845 */               logMessage = new StringBuilder().append(logMessage).append(", request - instrument [").append(instrument).append("] period [").append(period).append("] side [").append(side).append("] from [").append(((DateFormat)dateFormat).format(new Date(from))).append("] to [").append(((DateFormat)dateFormat).format(new Date(to))).append("]").toString();
/*      */ 
/*  847 */               if (processTime > 20000.0D)
/*  848 */                 LOGGER.warn(logMessage);
/*  849 */               else if (processTime > 3000.0D)
/*  850 */                 LOGGER.debug(logMessage);
/*      */               else {
/*  852 */                 LOGGER.trace(logMessage);
/*      */               }
/*      */             }
/*  855 */             Data[] dataArray = (Data[])data.toArray(new Data[data.size()]);
/*  856 */             if ((!inProgress) && 
/*  857 */               (dataArray.length > 0)) {
/*  858 */               long lastTime = dataArray[0].time;
/*  859 */               for (int i = 1; i < dataArray.length; i++) {
/*  860 */                 if (dataArray[i].time < lastTime) {
/*  861 */                   throw new DataCacheException("Data consistency error, not sorted");
/*      */                 }
/*      */               }
/*      */             }
/*      */ 
/*  866 */             if ((!inProgress) && 
/*  867 */               (dataArray.length > 0)) {
/*  868 */               firstDataTime = dataArray[0].time;
/*  869 */               if (firstDataTime / 1000L < from / 1000L) {
/*  870 */                 DateFormat format = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss SSS");
/*  871 */                 format.setTimeZone(TimeZone.getTimeZone("GMT"));
/*  872 */                 throw new DataCacheException(new StringBuilder().append("Wrong data from data feed server, time of the first data element [").append(format.format(new Date(firstDataTime))).append(" (").append(firstDataTime).append(")] is less than requested interval start time , request - instrument [").append(instrument).append("] period [").append(period).append("] side [").append(side).append("] from [").append(format.format(new Date(from))).append("] to [").append(format.format(new Date(to))).append("]").toString());
/*      */               }
/*      */ 
/*      */             }
/*      */ 
/*  880 */             long firstDataTime = dataArray;
/*      */ 
/*  899 */             if (incremented) {
/*  900 */               this.lastAccess.set(System.currentTimeMillis());
/*  901 */               this.inProcess.decrementAndGet(); } return firstDataTime;
/*      */           }
/*      */           catch (IOException e)
/*      */           {
/*  882 */             throw new DataCacheException(e);
/*      */           }
/*  884 */         if ((firstMessage instanceof ErrorResponseMessage)) {
/*  885 */           throw new DataCacheException(((ErrorResponseMessage)firstMessage).getReason());
/*      */         }
/*  887 */         throw new DataCacheException("Unknown response");
/*      */       }
/*      */ 
/*  891 */       Object dateFormat = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss SSS");
/*  892 */       ((DateFormat)dateFormat).setTimeZone(TimeZone.getTimeZone("GMT"));
/*  893 */       throw new DataCacheException(new StringBuilder().append("Cannot execute request for ").append(inProgress ? "in-progress candle " : "").append("data ").append("for instrument [").append(instrument.toString()).append("] period [").append(period).append("] side [").append(side).append(inProgress ? "" : new StringBuilder().append("] from [").append(((DateFormat)dateFormat).format(new Date(from))).toString()).append("] to [").append(((DateFormat)dateFormat).format(new Date(to))).append("] attempt [").append(tryCount).append("]").toString());
/*      */     }
/*      */     finally
/*      */     {
/*  899 */       if (incremented) {
/*  900 */         this.lastAccess.set(System.currentTimeMillis());
/*  901 */         this.inProcess.decrementAndGet(); } 
/*  901 */     }throw localObject6;
/*      */   }
/*      */ 
/*      */   public Data[] loadInProgressCandle(Instrument instrument, long to, LoadingProgressListener loadingProgress)
/*      */     throws DataCacheException
/*      */   {
/*  907 */     return loadData(instrument, null, null, -9223372036854775808L, to, true, false, loadingProgress);
/*      */   }
/*      */ 
/*      */   public ICurvesProtocolHandler.OrdersDataStruct loadOrders(Instrument instrument, long from, long to, LoadingProgressListener loadingProgress)
/*      */     throws DataCacheException
/*      */   {
/*  913 */     wait2SecsFromStart();
/*  914 */     boolean incremented = false;
/*  915 */     while (!loadingProgress.stopJob())
/*      */       try {
/*  917 */         if (this.thisLock.tryLock(20L, TimeUnit.MILLISECONDS))
/*      */           try {
/*  919 */             this.inProcess.incrementAndGet();
/*  920 */             incremented = true;
/*      */           }
/*      */           finally {
/*  923 */             this.thisLock.unlock();
/*      */           }
/*      */       }
/*      */       catch (InterruptedException e) {
/*  927 */         LOGGER.error(e.getMessage(), e);
/*      */       }
/*      */     try
/*      */     {
/*  931 */       if (loadingProgress.stopJob()) {
/*  932 */         e = null;
/*      */         return e;
/*      */       }
/*  934 */       int tryCount = 0;
/*  935 */       SortedSet finalResponseMessages = null;
/*  936 */       long startTime = System.nanoTime();
/*      */ 
/*  938 */       while ((tryCount < 5) && (finalResponseMessages == null)) {
/*  939 */         if (loadingProgress.stopJob()) {
/*  940 */           Object localObject2 = null;
/*      */           return localObject2;
/*      */         }
/*      */         try
/*      */         {
/*  944 */           connect(loadingProgress);
/*      */         } catch (NotConnectedException e) {
/*  946 */           LOGGER.error(e.getMessage(), e);
/*  947 */           tryCount++;
/*  948 */         }continue;
/*      */ 
/*  950 */         tryCount++;
/*  951 */         if (LOGGER.isTraceEnabled()) {
/*  952 */           Object dateFormat = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss SSS");
/*  953 */           ((DateFormat)dateFormat).setTimeZone(TimeZone.getTimeZone("GMT"));
/*  954 */           LOGGER.trace(new StringBuilder().append("Downloading orders data for instrument [").append(instrument.toString()).append("] from [").append(((DateFormat)dateFormat).format(new Date(from))).append("] to [").append(((DateFormat)dateFormat).format(new Date(to))).append("] attempt [").append(tryCount).append("]").toString());
/*      */         }
/*      */ 
/*  959 */         int requestId = this.requestNumber.getAndIncrement();
/*      */ 
/*  961 */         OrderHistoryRequestMessage orderRequestMessage = new OrderHistoryRequestMessage();
/*  962 */         orderRequestMessage.setCurrencyPrimary(instrument.toString().substring(0, 3));
/*  963 */         orderRequestMessage.setCurrencySecondary(instrument.toString().substring(4, 7));
/*  964 */         orderRequestMessage.setStartTime(Long.valueOf(from / 1000L));
/*  965 */         orderRequestMessage.setEndTime(Long.valueOf(to / 1000L));
/*  966 */         orderRequestMessage.setGetRolloveredOrders(true);
/*  967 */         orderRequestMessage.setGetRejectedOrders(false);
/*  968 */         orderRequestMessage.setGetMergedPoss(true);
/*  969 */         orderRequestMessage.setRequestId(Integer.valueOf(requestId));
/*      */ 
/*  971 */         synchronized (this.requestResponse)
/*      */         {
/*  973 */           this.requestResponse.put(Integer.valueOf(requestId), null);
/*      */         }
/*  975 */         this.thisLock.lock();
/*      */         try {
/*  977 */           if (this.transportClient == null) {
/*  978 */             throw new NotConnectedException("Cannot connect to data feed server, transport not initialized");
/*      */           }
/*      */ 
/*  981 */           this.transportClient.controlRequest(orderRequestMessage);
/*      */         } finally {
/*  983 */           this.thisLock.unlock();
/*      */         }
/*  985 */         label1093: synchronized (this.requestResponse) {
/*  986 */           long requestTime = System.currentTimeMillis();
/*      */ 
/*  988 */           while (this.requestResponse.containsKey(Integer.valueOf(requestId)))
/*      */           {
/*  993 */             SortedSet responseMessages = (SortedSet)this.requestResponse.get(Integer.valueOf(requestId));
/*      */             ErrorResponseMessage error;
/*  994 */             if ((responseMessages != null) && (!responseMessages.isEmpty()))
/*      */             {
/*  996 */               if ((responseMessages.first() instanceof OrderGroupsBinaryMessage)) {
/*  997 */                 if (((responseMessages.last() instanceof OrderGroupsBinaryMessage)) && (((OrderGroupsBinaryMessage)responseMessages.last()).isHistoryFinished().booleanValue()))
/*      */                 {
/* 1000 */                   int i = 0;
/* 1001 */                   boolean done = true;
/* 1002 */                   for (ProtocolMessage responseMessage : responseMessages) {
/* 1003 */                     OrderGroupsBinaryMessage candleMessage = (OrderGroupsBinaryMessage)responseMessage;
/* 1004 */                     if (candleMessage.getMessageOrder().intValue() != i) {
/* 1005 */                       done = false;
/* 1006 */                       break;
/*      */                     }
/* 1008 */                     i++;
/*      */                   }
/* 1010 */                   if (done) {
/* 1011 */                     this.requestResponse.remove(Integer.valueOf(requestId));
/* 1012 */                     finalResponseMessages = responseMessages;
/* 1013 */                     break label1093;
/*      */                   }
/* 1015 */                 } else if ((responseMessages.last() instanceof ErrorResponseMessage)) {
/* 1016 */                   error = (ErrorResponseMessage)responseMessages.last();
/* 1017 */                   responseMessages.clear();
/* 1018 */                   responseMessages.add(error);
/*      */                 }
/*      */               }
/*      */               else {
/* 1022 */                 this.requestResponse.remove(Integer.valueOf(requestId));
/* 1023 */                 finalResponseMessages = responseMessages;
/* 1024 */                 break label1093;
/*      */               }
/*      */             }
/* 1027 */             if ((loadingProgress.stopJob()) && (requestTime + 180000L < System.currentTimeMillis()))
/*      */             {
/* 1029 */               this.requestResponse.remove(Integer.valueOf(requestId));
/* 1030 */               error = null;
/*      */ 
/* 1108 */               if (incremented) {
/* 1109 */                 this.lastAccess.set(System.currentTimeMillis());
/* 1110 */                 this.inProcess.decrementAndGet(); } return error;
/*      */             }
/* 1032 */             if (requestTime + 600000L < System.currentTimeMillis())
/*      */             {
/* 1035 */               this.requestResponse.remove(Integer.valueOf(requestId));
/* 1036 */               finalResponseMessages = new TreeSet();
/* 1037 */               DateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss SSS");
/* 1038 */               dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
/* 1039 */               ErrorResponseMessage responseMessage = new ErrorResponseMessage(new StringBuilder().append("Request for orders data for instrument [").append(instrument.toString()).append("] from [").append(dateFormat.format(new Date(from))).append("] to [").append(dateFormat.format(new Date(to))).append("] attempt [").append(tryCount).append("] timed out").toString());
/*      */ 
/* 1042 */               responseMessage.setRequestId(Integer.valueOf(requestId));
/* 1043 */               finalResponseMessages.add(responseMessage);
/* 1044 */               break label1093;
/*      */             }
/*      */ 
/*      */             try
/*      */             {
/* 1049 */               this.requestResponse.wait(1000L);
/*      */             }
/*      */             catch (InterruptedException e)
/*      */             {
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/* 1057 */       if ((finalResponseMessages != null) && (!finalResponseMessages.isEmpty())) {
/* 1058 */         ProtocolMessage firstMessage = (ProtocolMessage)finalResponseMessages.first();
/* 1059 */         if ((firstMessage instanceof OrderGroupsBinaryMessage)) {
/* 1060 */           ICurvesProtocolHandler.OrdersDataStruct ordersDataStruct = new ICurvesProtocolHandler.OrdersDataStruct();
/* 1061 */           List groupsData = new ArrayList();
/* 1062 */           Object mergesData = new ArrayList();
/* 1063 */           Object ordersData = new ArrayList();
/*      */ 
/* 1065 */           for (ProtocolMessage protocolMessage : finalResponseMessages) {
/*      */             try {
/* 1067 */               groupsData.addAll(((OrderGroupsBinaryMessage)protocolMessage).getOrderGroups());
/* 1068 */               ((List)mergesData).addAll(((OrderGroupsBinaryMessage)protocolMessage).getMerges());
/* 1069 */               ((List)ordersData).addAll(((OrderGroupsBinaryMessage)protocolMessage).getOrders());
/*      */             } catch (IOException e) {
/* 1071 */               throw new DataCacheException(e.getMessage(), e);
/*      */             }
/*      */           }
/*      */ 
/* 1075 */           ordersDataStruct.groups = groupsData;
/* 1076 */           ordersDataStruct.orders = ((List)ordersData);
/* 1077 */           ordersDataStruct.merges = ((List)mergesData);
/*      */ 
/* 1079 */           double processTime = (System.nanoTime() - startTime) / 1000000.0D;
/* 1080 */           if ((LOGGER.isTraceEnabled()) || ((processTime > 3000.0D) && (LOGGER.isDebugEnabled())) || (processTime > 60000.0D)) {
/* 1081 */             dateFormat = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss SSS");
/* 1082 */             dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
/* 1083 */             String logMessage = new StringBuilder().append("Orders data downloaded in [").append(processTime).append("] milliseconds, order groups size [").append(groupsData.size()).append("], orders size [").append(((List)ordersData).size()).append("], merges size [").append(((List)mergesData).size()).append("]").toString();
/*      */ 
/* 1085 */             if (processTime > 60000.0D)
/* 1086 */               LOGGER.warn(logMessage);
/* 1087 */             else if (processTime > 3000.0D)
/* 1088 */               LOGGER.debug(logMessage);
/*      */             else {
/* 1090 */               LOGGER.trace(logMessage);
/*      */             }
/*      */           }
/*      */ 
/* 1094 */           DateFormat dateFormat = ordersDataStruct;
/*      */           return dateFormat;
/*      */         }
/* 1095 */         if ((firstMessage instanceof ErrorResponseMessage)) {
/* 1096 */           throw new DataCacheException(((ErrorResponseMessage)firstMessage).getReason());
/*      */         }
/* 1098 */         throw new DataCacheException("Unknown response");
/*      */       }
/*      */ 
/* 1102 */       Object dateFormat = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss SSS");
/* 1103 */       ((DateFormat)dateFormat).setTimeZone(TimeZone.getTimeZone("GMT"));
/* 1104 */       throw new DataCacheException(new StringBuilder().append("Cannot execute request orders data for instrument [").append(instrument.toString()).append("] from [").append(((DateFormat)dateFormat).format(new Date(from))).append("] to [").append(((DateFormat)dateFormat).format(new Date(to))).append("] attempt [").append(tryCount).append("]").toString());
/*      */     }
/*      */     finally
/*      */     {
/* 1108 */       if (incremented) {
/* 1109 */         this.lastAccess.set(System.currentTimeMillis());
/* 1110 */         this.inProcess.decrementAndGet(); } 
/* 1110 */     }throw localObject6;
/*      */   }
/*      */ 
/*      */   private void wait2SecsFromStart()
/*      */   {
/* 1116 */     if (FeedDataProvider.getDefaultInstance().getFirstTickLocalTime() + 2000L > System.currentTimeMillis())
/*      */       try {
/* 1118 */         Thread.sleep(FeedDataProvider.getDefaultInstance().getFirstTickLocalTime() + 2000L - System.currentTimeMillis());
/*      */       }
/*      */       catch (InterruptedException e)
/*      */       {
/*      */       }
/*      */   }
/*      */ 
/*      */   public void authorized(TransportClient arg0) {
/* 1126 */     LOGGER.debug("Authorized to data feed server");
/* 1127 */     fireConnected();
/*      */   }
/*      */ 
/*      */   public void disconnected(DisconnectedEvent arg0) {
/* 1131 */     DisconnectReason reason = DisconnectReason.values()[arg0.getReason().getReason()];
/* 1132 */     if (reason == DisconnectReason.CLIENT_APP_REQUEST)
/* 1133 */       LOGGER.debug(new StringBuilder().append("Disconnected from data feed server, reason - ").append(reason.toString()).toString());
/* 1134 */     else if (reason == DisconnectReason.AUTHORIZATION_FAILED) {
/* 1135 */       LOGGER.warn(new StringBuilder().append("Disconnected from data feed server, reason - ").append(reason.toString()).append(". User + session id [").append(this.transportClient.getLogin()).append("], ticket [").append(this.transportClient.getPassword()).append("], last successful authorization time [").append(this.lastAuthenticationTime).append("], current time [").append(System.currentTimeMillis()).append("]").toString());
/*      */     }
/*      */     else
/*      */     {
/* 1140 */       LOGGER.warn(new StringBuilder().append("Disconnected from data feed server, reason - ").append(reason.toString()).toString());
/*      */     }
/* 1142 */     this.thisLock.lock();
/*      */     try {
/* 1144 */       if (this.connecting) {
/* 1145 */         this.attempt += 1;
/* 1146 */         if ((this.attempt % 3 == 1) || (reason == DisconnectReason.AUTHORIZATION_FAILED) || (reason == DisconnectReason.AUTHORIZATION_TIMEOUT) || (reason == DisconnectReason.CERTIFICATE_EXCEPTION))
/*      */         {
/* 1148 */           this.authorizationResponse = null;
/* 1149 */           while ((this.connecting) && (this.authorizationResponse == null)) {
/* 1150 */             this.authorizationResponse = this.authenticator.authenticate();
/* 1151 */             if (this.authorizationResponse == null) {
/* 1152 */               LOGGER.error("Cannot connect to authorization server");
/*      */               try {
/* 1154 */                 Thread.sleep(10000L);
/*      */               } catch (InterruptedException e1) {
/* 1156 */                 LOGGER.error(e1.getMessage(), e1);
/*      */               }
/*      */             }
/*      */           }
/*      */           try {
/* 1161 */             applyAuthorizationResponse();
/*      */           } catch (NotConnectedException e) {
/* 1163 */             LOGGER.error(e.getMessage(), e);
/*      */           }
/*      */         }
/* 1166 */         if (this.connecting) {
/* 1167 */           LOGGER.info(new StringBuilder().append("Connecting to data feed server [").append(this.transportClient.getAddress().toString()).append("] attempt [").append(this.attempt).append("] ...").toString());
/* 1168 */           this.transportClient.connect();
/*      */         }
/*      */       }
/*      */     } finally {
/* 1172 */       this.thisLock.unlock();
/*      */     }
/* 1174 */     synchronized (this.requestResponse) {
/* 1175 */       this.thisLock.lock();
/*      */       try {
/* 1177 */         if (!this.connecting) {
/* 1178 */           int requestsCount = this.inProcess.get();
/* 1179 */           if (requestsCount > 0)
/*      */           {
/* 1182 */             this.requestResponse.clear();
/* 1183 */             this.requestResponse.notifyAll();
/*      */           }
/*      */         }
/*      */       } finally {
/* 1187 */         this.thisLock.unlock();
/*      */       }
/*      */     }
/* 1190 */     this.disconnecting = false;
/*      */ 
/* 1192 */     fireDisconnected();
/*      */   }
/*      */ 
/*      */   public void feedbackMessageReceived(TransportClient transportClient, ProtocolMessage message)
/*      */   {
/* 1199 */     if ((message instanceof CandleHistoryGroupMessage))
/*      */     {
/* 1201 */       CandleHistoryGroupMessage historyGroupMessage = (CandleHistoryGroupMessage)message;
/* 1202 */       synchronized (this.requestResponse) {
/* 1203 */         if (this.requestResponse.containsKey(historyGroupMessage.getRequestId())) {
/* 1204 */           SortedSet responseMessages = getResponseMessages(historyGroupMessage.getRequestId());
/* 1205 */           responseMessages.add(historyGroupMessage);
/* 1206 */           ProtocolMessage protocolMessage = (ProtocolMessage)responseMessages.last();
/* 1207 */           if ((!(protocolMessage instanceof CandleHistoryGroupMessage)) || (((CandleHistoryGroupMessage)protocolMessage).isHistoryFinished().booleanValue()) || ((responseMessages.first() instanceof ErrorResponseMessage)))
/*      */           {
/* 1210 */             LOGGER.trace("Final CandleHistoryGroupMessage response received from curves server");
/* 1211 */             this.requestResponse.notifyAll();
/*      */           }
/*      */         } else {
/* 1214 */           LOGGER.warn("CandleHistoryGroup response received from history server, but none is waiting for it");
/*      */         }
/*      */       }
/* 1217 */     } else if ((message instanceof OrderGroupsBinaryMessage)) {
/* 1218 */       OrderGroupsBinaryMessage orderGroupsMessage = (OrderGroupsBinaryMessage)message;
/* 1219 */       synchronized (this.requestResponse) {
/* 1220 */         if (this.requestResponse.containsKey(orderGroupsMessage.getRequestId())) {
/* 1221 */           SortedSet responseMessages = getResponseMessages(orderGroupsMessage.getRequestId());
/* 1222 */           responseMessages.add(orderGroupsMessage);
/* 1223 */           if ((orderGroupsMessage.isHistoryFinished().booleanValue()) || ((responseMessages.first() instanceof ErrorResponseMessage))) {
/* 1224 */             LOGGER.trace("Final OrderGroupsBinaryMessage response received from curves server");
/* 1225 */             this.requestResponse.notifyAll();
/*      */           }
/*      */         } else {
/* 1228 */           LOGGER.warn("OrderGroup response received from history server, but none is waiting for it");
/*      */         }
/*      */       }
/* 1231 */     } else if ((message instanceof FileMngResponseMessage)) {
/* 1232 */       FileMngResponseMessage respMsg = (FileMngResponseMessage)message;
/* 1233 */       synchronized (this.requestResponse)
/*      */       {
/* 1235 */         if (this.requestResponse.containsKey(respMsg.getRequestId())) {
/* 1236 */           SortedSet responseMessages = getResponseMessages(respMsg.getRequestId());
/* 1237 */           responseMessages.add(respMsg);
/* 1238 */           this.requestResponse.notifyAll();
/*      */         } else {
/* 1240 */           LOGGER.warn(new StringBuilder().append("File response message received from history server, but none is waiting for it: ").append(respMsg).toString());
/*      */         }
/*      */       }
/*      */     }
/* 1244 */     else if ((message instanceof ErrorResponseMessage)) {
/* 1245 */       LOGGER.debug("ErrorResponseMessage response received from curves server");
/* 1246 */       ErrorResponseMessage errorMessage = (ErrorResponseMessage)message;
/* 1247 */       synchronized (this.requestResponse) {
/* 1248 */         if (this.requestResponse.containsKey(errorMessage.getRequestId())) {
/* 1249 */           SortedSet responseMessages = getResponseMessages(errorMessage.getRequestId());
/* 1250 */           responseMessages.add(errorMessage);
/* 1251 */           this.requestResponse.notifyAll();
/*      */         } else {
/* 1253 */           LOGGER.warn(new StringBuilder().append("Error response received from history server, but none is waiting for it: ").append(errorMessage.getReason()).toString());
/*      */         }
/*      */       }
/* 1256 */     } else if ((message instanceof DFHistoryStartResponseMessage)) {
/* 1257 */       LOGGER.debug("DFHistoryStartResponseMessage response received from curves server");
/* 1258 */       DFHistoryStartResponseMessage dfHistoryStartResponseMessage = (DFHistoryStartResponseMessage)message;
/* 1259 */       synchronized (this.requestResponse) {
/* 1260 */         if (this.requestResponse.containsKey(dfHistoryStartResponseMessage.getRequestId())) {
/* 1261 */           SortedSet responseMessages = getResponseMessages(dfHistoryStartResponseMessage.getRequestId());
/* 1262 */           responseMessages.add(dfHistoryStartResponseMessage);
/* 1263 */           this.requestResponse.notifyAll();
/*      */         } else {
/* 1265 */           LOGGER.warn(new StringBuilder().append("DFHistoryStartResponseMessage received from history server, but none is waiting for it: ").append(dfHistoryStartResponseMessage).toString());
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   public void handleStream(String requestIdStr, BlockingBinaryStream stream) {
/*      */     ProtocolMessage responseMessage;
/*      */     try {
/* 1274 */       ByteArrayOutputStream baos = new ByteArrayOutputStream();
/* 1275 */       byte[] buff = new byte[512];
/*      */       int i;
/* 1277 */       while ((i = stream.read(buff)) != -1) {
/* 1278 */         baos.write(buff, 0, i);
/*      */       }
/* 1280 */       byte[] compressedByteArray = baos.toByteArray();
/* 1281 */       if (compressedByteArray.length != 0) {
/* 1282 */         ByteArrayInputStream bais = new ByteArrayInputStream(compressedByteArray);
/* 1283 */         baos.close();
/* 1284 */         baos = new ByteArrayOutputStream();
/* 1285 */         GZIPInputStream gzis = new GZIPInputStream(bais);
/*      */         try {
/* 1287 */           while ((i = gzis.read(buff)) != -1)
/* 1288 */             baos.write(buff, 0, i);
/*      */         }
/*      */         finally {
/* 1291 */           gzis.close();
/*      */         }
/* 1293 */         ProtocolMessage responseMessage = new FileResponseMessage(null);
/* 1294 */         byte[] byteData = baos.toByteArray();
/* 1295 */         ((FileResponseMessage)responseMessage).setData(byteData);
/*      */       } else {
/* 1297 */         responseMessage = new FileResponseMessage(null);
/* 1298 */         ((FileResponseMessage)responseMessage).setData(compressedByteArray);
/*      */       }
/*      */     } catch (Exception e) {
/* 1301 */       responseMessage = new ErrorResponseMessage(new StringBuilder().append(e.getClass().getName()).append(": ").append(e.getMessage()).toString());
/*      */     }
/*      */ 
/* 1304 */     synchronized (this.requestResponse) {
/* 1305 */       int requestId = Integer.parseInt(requestIdStr);
/* 1306 */       if (this.requestResponse.containsKey(Integer.valueOf(requestId))) {
/* 1307 */         SortedSet responseMessages = getResponseMessages(Integer.valueOf(requestId));
/* 1308 */         responseMessages.add(responseMessage);
/* 1309 */         this.requestResponse.notifyAll();
/*      */       } else {
/* 1311 */         LOGGER.debug("Response received from history server, but none is waiting for it");
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   private SortedSet<ProtocolMessage> getResponseMessages(Integer requestId) {
/* 1317 */     SortedSet responseMessages = (SortedSet)this.requestResponse.get(requestId);
/* 1318 */     if (responseMessages == null) {
/* 1319 */       responseMessages = new TreeSet(new Comparator()
/*      */       {
/*      */         public int compare(ProtocolMessage o1, ProtocolMessage o2)
/*      */         {
/*      */           int messageOrder1;
/*      */           int messageOrder1;
/* 1323 */           if ((o1 instanceof CandleHistoryGroupMessage)) {
/* 1324 */             messageOrder1 = ((CandleHistoryGroupMessage)o1).getMessageOrder().intValue();
/*      */           }
/*      */           else
/*      */           {
/*      */             int messageOrder1;
/* 1325 */             if ((o1 instanceof OrderGroupsBinaryMessage))
/* 1326 */               messageOrder1 = ((OrderGroupsBinaryMessage)o1).getMessageOrder().intValue();
/*      */             else
/* 1328 */               messageOrder1 = -1;
/*      */           }
/*      */           int messageOrder2;
/*      */           int messageOrder2;
/* 1330 */           if ((o2 instanceof CandleHistoryGroupMessage)) {
/* 1331 */             messageOrder2 = ((CandleHistoryGroupMessage)o2).getMessageOrder().intValue();
/*      */           }
/*      */           else
/*      */           {
/*      */             int messageOrder2;
/* 1332 */             if ((o2 instanceof OrderGroupsBinaryMessage))
/* 1333 */               messageOrder2 = ((OrderGroupsBinaryMessage)o2).getMessageOrder().intValue();
/*      */             else
/* 1335 */               messageOrder2 = -1;
/*      */           }
/* 1337 */           return messageOrder1 - messageOrder2;
/*      */         }
/*      */       });
/* 1340 */       this.requestResponse.put(requestId, responseMessages);
/*      */     }
/* 1342 */     return responseMessages;
/*      */   }
/*      */ 
/*      */   public List<FileItem> getFileList(FileType fileType, FileItem.AccessType accessType, FileProgressListener loadingProgress)
/*      */     throws StorageException, CancelLoadingException
/*      */   {
/* 1360 */     List fileItems = null;
/*      */ 
/* 1362 */     boolean incremented = false;
/* 1363 */     while (!loadingProgress.stopJob()) {
/*      */       try {
/* 1365 */         if (this.thisLock.tryLock(20L, TimeUnit.MILLISECONDS))
/*      */           try {
/* 1367 */             this.inProcess.incrementAndGet();
/* 1368 */             incremented = true;
/*      */           }
/*      */           finally {
/* 1371 */             this.thisLock.unlock();
/*      */           }
/*      */       }
/*      */       catch (InterruptedException e) {
/* 1375 */         LOGGER.error(e.getMessage(), e);
/*      */       }
/*      */     }
/*      */ 
/*      */     try
/*      */     {
/* 1381 */       if (loadingProgress.stopJob()) {
/* 1382 */         e = null;
/*      */         return e;
/*      */       }
/* 1385 */       int tryCount = 0;
/*      */ 
/* 1387 */       while ((tryCount < 5) && (fileItems == null))
/*      */       {
/* 1389 */         tryCount++;
/*      */         try
/*      */         {
/* 1392 */           connect(loadingProgress);
/*      */         } catch (NotConnectedException e) {
/* 1394 */           LOGGER.error(e.getMessage(), e);
/* 1395 */         }continue;
/*      */ 
/* 1398 */         requestId = this.requestNumber.getAndIncrement();
/*      */ 
/* 1400 */         FileItem fi = new FileItem();
/* 1401 */         fi.setFileType(fileType);
/* 1402 */         fi.setAccessType(accessType);
/*      */ 
/* 1405 */         FileMngRequestMessage msg = new FileMngRequestMessage();
/* 1406 */         msg.setRequestId(Integer.valueOf(requestId));
/* 1407 */         msg.setCommand(FileItem.Command.LIST);
/* 1408 */         msg.setFileItem(fi);
/*      */ 
/* 1410 */         synchronized (this.requestResponse) {
/* 1411 */           this.requestResponse.put(Integer.valueOf(requestId), null);
/*      */         }
/*      */ 
/* 1414 */         this.thisLock.lock();
/*      */         try {
/* 1416 */           if (this.transportClient == null) {
/* 1417 */             throw new NotConnectedException("Cannot connect to data feed server, transport not initialized");
/*      */           }
/*      */ 
/* 1421 */           this.transportClient.controlRequest(msg);
/*      */         } finally {
/* 1423 */           this.thisLock.unlock();
/*      */         }
/*      */ 
/* 1426 */         label628: synchronized (this.requestResponse) {
/* 1427 */           long requestTime = System.currentTimeMillis();
/*      */           while (true)
/*      */           {
/* 1430 */             if (loadingProgress.stopJob()) {
/* 1431 */               this.requestResponse.remove(Integer.valueOf(requestId));
/* 1432 */               throw new CancelLoadingException("Getting list of remote files cancelled");
/*      */             }
/*      */ 
/* 1435 */             if (!this.requestResponse.containsKey(Integer.valueOf(requestId)))
/*      */             {
/*      */               break;
/*      */             }
/*      */ 
/* 1442 */             SortedSet responseMessages = (SortedSet)this.requestResponse.get(Integer.valueOf(requestId));
/* 1443 */             if ((responseMessages != null) && (!responseMessages.isEmpty()))
/*      */             {
/* 1445 */               ProtocolMessage m = (ProtocolMessage)responseMessages.first();
/*      */ 
/* 1447 */               if ((m instanceof FileMngResponseMessage)) {
/* 1448 */                 FileMngResponseMessage respMsg = (FileMngResponseMessage)m;
/* 1449 */                 fileItems = respMsg.getFileList();
/* 1450 */                 this.requestResponse.remove(Integer.valueOf(requestId));
/* 1451 */                 break label628;
/* 1452 */               }if ((m instanceof ErrorResponseMessage)) {
/* 1453 */                 throw new StorageException(((ErrorResponseMessage)m).getReason());
/*      */               }
/* 1455 */               throw new StorageException(new StringBuilder().append("Unknown response: ").append(m).toString());
/*      */             }
/*      */ 
/* 1459 */             if (requestTime + 10000L < System.currentTimeMillis())
/*      */             {
/* 1461 */               this.requestResponse.remove(Integer.valueOf(requestId));
/*      */ 
/* 1463 */               throw new StorageException(new StringBuilder().append("Request for file list ").append(fileType).append(" timed out").toString());
/*      */             }
/*      */ 
/*      */             try
/*      */             {
/* 1469 */               this.requestResponse.wait(1000L);
/*      */             }
/*      */             catch (InterruptedException e)
/*      */             {
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/* 1477 */       int requestId = fileItems;
/*      */       return requestId;
/*      */     }
/*      */     catch (NotConnectedException nce)
/*      */     {
/* 1479 */       throw new StorageException(nce);
/*      */     } finally {
/* 1481 */       if (incremented) {
/* 1482 */         this.lastAccess.set(System.currentTimeMillis());
/* 1483 */         this.inProcess.decrementAndGet();
/*      */       }
/*      */ 
/* 1486 */       loadingProgress.setThreadStopped(); } throw localObject5;
/*      */   }
/*      */ 
/*      */   public Long uploadFile(FileItem item, String clientMode, LoadingProgressListener loadingProgress)
/*      */     throws StorageException, FileAlreadyExistException
/*      */   {
/* 1492 */     boolean responseReceived = false;
/*      */ 
/* 1494 */     boolean incremented = false;
/* 1495 */     while (!loadingProgress.stopJob()) {
/*      */       try {
/* 1497 */         if (this.thisLock.tryLock(20L, TimeUnit.MILLISECONDS))
/*      */           try {
/* 1499 */             this.inProcess.incrementAndGet();
/* 1500 */             incremented = true;
/*      */           }
/*      */           finally {
/* 1503 */             this.thisLock.unlock();
/*      */           }
/*      */       }
/*      */       catch (InterruptedException e) {
/* 1507 */         LOGGER.error(e.getMessage(), e);
/*      */       }
/*      */     }
/*      */     try
/*      */     {
/* 1512 */       if (loadingProgress.stopJob()) {
/* 1513 */         e = null;
/*      */         return e;
/*      */       }
/* 1516 */       int tryCount = 0;
/*      */ 
/* 1518 */       while ((tryCount < 5) && (!responseReceived)) {
/* 1519 */         tryCount++;
/*      */         try
/*      */         {
/* 1522 */           connect(loadingProgress);
/*      */         } catch (NotConnectedException e) {
/* 1524 */           LOGGER.error(e.getMessage(), e);
/* 1525 */         }continue;
/*      */ 
/* 1528 */         int requestId = this.requestNumber.getAndIncrement();
/*      */ 
/* 1530 */         FileMngRequestMessage msg = new FileMngRequestMessage();
/*      */ 
/* 1532 */         item.setUserSchema(clientMode);
/* 1533 */         item.setOwnerSchema(clientMode);
/*      */ 
/* 1535 */         msg.setFileItem(item);
/* 1536 */         msg.setCommand(FileItem.Command.UPLOAD);
/* 1537 */         msg.setRequestId(Integer.valueOf(requestId));
/*      */ 
/* 1539 */         synchronized (this.requestResponse) {
/* 1540 */           this.requestResponse.put(Integer.valueOf(requestId), null);
/*      */         }
/*      */ 
/* 1543 */         this.thisLock.lock();
/*      */         try {
/* 1545 */           if (this.transportClient == null) {
/* 1546 */             throw new NotConnectedException("Cannot connect to data feed server, transport not initialized");
/*      */           }
/*      */ 
/* 1550 */           this.transportClient.controlRequest(msg);
/*      */         } finally {
/* 1552 */           this.thisLock.unlock();
/*      */         }
/*      */ 
/* 1555 */         synchronized (this.requestResponse) {
/* 1556 */           long requestTime = System.currentTimeMillis();
/*      */ 
/* 1558 */           if (loadingProgress.stopJob()) {
/* 1559 */             Object localObject4 = null;
/*      */ 
/* 1622 */             if (incremented) {
/* 1623 */               this.lastAccess.set(System.currentTimeMillis());
/* 1624 */               this.inProcess.decrementAndGet(); } return localObject4;
/*      */           }
/* 1562 */           if (!this.requestResponse.containsKey(Integer.valueOf(requestId)))
/*      */           {
/*      */             continue;
/*      */           }
/*      */ 
/* 1569 */           SortedSet responseMessages = (SortedSet)this.requestResponse.get(Integer.valueOf(requestId));
/* 1570 */           if ((responseMessages != null) && (!responseMessages.isEmpty()))
/*      */           {
/* 1572 */             ProtocolMessage m = (ProtocolMessage)responseMessages.first();
/*      */ 
/* 1574 */             if ((m instanceof FileMngResponseMessage)) {
/* 1575 */               FileMngResponseMessage respMsg = (FileMngResponseMessage)m;
/* 1576 */               this.requestResponse.remove(Integer.valueOf(requestId));
/*      */ 
/* 1578 */               responseReceived = true;
/*      */ 
/* 1580 */               FileItem retItem = respMsg.getFileItem();
/*      */ 
/* 1582 */               if (retItem != null) {
/* 1583 */                 LOGGER.info(new StringBuilder().append("File: ").append(retItem.getFileName()).append(" uploaded successfully.").toString());
/* 1584 */                 Long localLong = retItem.getFileId();
/*      */ 
/* 1622 */                 if (incremented) {
/* 1623 */                   this.lastAccess.set(System.currentTimeMillis());
/* 1624 */                   this.inProcess.decrementAndGet(); } return localLong;
/*      */               }
/* 1586 */               throw new StorageException("Upload returned NULL file item");
/*      */             }
/* 1588 */             if ((m instanceof ErrorResponseMessage)) {
/* 1589 */               ErrorResponseMessage err = (ErrorResponseMessage)m;
/*      */ 
/* 1591 */               String reason = err.getReason();
/*      */ 
/* 1593 */               if ((reason != null) && (reason.startsWith(FileItem.ErrorMessage.FILE_ALREADY_EXIST.toString())))
/* 1594 */                 throw new FileAlreadyExistException(reason);
/*      */             }
/*      */             else {
/* 1597 */               throw new StorageException(new StringBuilder().append("Unknown response: ").append(m).toString());
/*      */             }
/*      */           }
/*      */ 
/* 1601 */           if (requestTime + 10000L < System.currentTimeMillis())
/*      */           {
/* 1603 */             this.requestResponse.remove(Integer.valueOf(requestId));
/*      */ 
/* 1605 */             throw new StorageException(new StringBuilder().append("Request for ").append(item.getFileName()).append(" upload timed out").toString());
/*      */           }
/*      */ 
/*      */           try
/*      */           {
/* 1611 */             this.requestResponse.wait(1000L);
/*      */           }
/*      */           catch (InterruptedException e)
/*      */           {
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */     catch (NotConnectedException e) {
/* 1620 */       throw new StorageException(e);
/*      */     } finally {
/* 1622 */       if (incremented) {
/* 1623 */         this.lastAccess.set(System.currentTimeMillis());
/* 1624 */         this.inProcess.decrementAndGet();
/*      */       }
/*      */     }
/*      */ 
/* 1628 */     return null;
/*      */   }
/*      */ 
/*      */   public FileItem downloadFile(long fileId, LoadingProgressListener loadingProgress) throws StorageException
/*      */   {
/* 1633 */     FileMngRequestMessage msg = new FileMngRequestMessage();
/*      */ 
/* 1635 */     FileItem f = new FileItem();
/* 1636 */     f.setFileId(Long.valueOf(fileId));
/*      */ 
/* 1638 */     msg.setFileItem(f);
/* 1639 */     msg.setCommand(FileItem.Command.DOWNLOAD);
/*      */     try
/*      */     {
/* 1643 */       connect(loadingProgress);
/*      */ 
/* 1645 */       ProtocolMessage response = null;
/*      */ 
/* 1647 */       this.thisLock.lock();
/*      */       try {
/* 1649 */         if (this.transportClient == null) {
/* 1650 */           throw new NotConnectedException("Cannot connect to data feed server, transport not initialized");
/*      */         }
/* 1652 */         response = this.transportClient.controlSynchRequest(msg, Long.valueOf(10000L));
/*      */       } finally {
/* 1654 */         this.thisLock.unlock();
/*      */       }
/*      */ 
/* 1657 */       if ((response instanceof FileMngResponseMessage)) {
/* 1658 */         FileMngResponseMessage fileMngResponseMessage = (FileMngResponseMessage)response;
/* 1659 */         return fileMngResponseMessage.getFileItem();
/* 1660 */       }if ((response instanceof ErrorResponseMessage)) {
/* 1661 */         throw new StorageException(((ErrorResponseMessage)response).getReason());
/*      */       }
/* 1663 */       throw new StorageException(new StringBuilder().append("Unknown response: ").append(response).toString());
/*      */     }
/*      */     catch (TimeoutException e) {
/* 1666 */       throw new StorageException(e); } catch (NotConnectedException e) {
/*      */     }
/* 1668 */     throw new StorageException(e);
/*      */   }
/*      */ 
/*      */   public FileItem useKey(String key, FileType fileType, LoadingProgressListener loadingProgress, String clientType) throws StorageException, KeyNotFoundException
/*      */   {
/*      */     try
/*      */     {
/* 1675 */       connect(loadingProgress);
/*      */ 
/* 1677 */       FileMngRequestMessage msg = new FileMngRequestMessage();
/* 1678 */       FileItem fi = new FileItem();
/*      */ 
/* 1680 */       fi.setShareKey(key);
/* 1681 */       fi.setFileType(fileType);
/* 1682 */       fi.setUserSchema(clientType);
/*      */ 
/* 1684 */       msg.setFileItem(fi);
/* 1685 */       msg.setCommand(FileItem.Command.USE_KEY);
/*      */ 
/* 1687 */       ProtocolMessage ret = null;
/*      */ 
/* 1689 */       this.thisLock.lock();
/*      */       try {
/* 1691 */         if (this.transportClient == null) {
/* 1692 */           throw new NotConnectedException("Cannot connect to data feed server, transport not initialized");
/*      */         }
/*      */ 
/* 1695 */         ret = this.transportClient.controlSynchRequest(msg, Long.valueOf(10000L));
/*      */       } finally {
/* 1697 */         this.thisLock.unlock();
/*      */       }
/*      */ 
/* 1700 */       if ((ret instanceof FileMngResponseMessage))
/*      */       {
/* 1702 */         FileItem item = ((FileMngResponseMessage)ret).getFileItem();
/*      */ 
/* 1704 */         if (item == null) {
/* 1705 */           throw new StorageException(new StringBuilder().append("Cannot find file by key: ").append(key).append(". Server error.").toString());
/*      */         }
/* 1707 */         return item;
/*      */       }
/* 1709 */       if ((ret instanceof ErrorResponseMessage)) {
/* 1710 */         ErrorResponseMessage err = (ErrorResponseMessage)ret;
/* 1711 */         if ((err.getReason() != null) && (err.getReason().startsWith(FileItem.ErrorMessage.KEY_NOT_FOUND.toString()))) {
/* 1712 */           throw new KeyNotFoundException(key);
/*      */         }
/* 1714 */         throw new StorageException(err.getReason());
/*      */       }
/*      */ 
/* 1717 */       throw new StorageException(new StringBuilder().append("Unknown response: ").append(ret).toString());
/*      */     }
/*      */     catch (TimeoutException e) {
/* 1720 */       throw new StorageException(e); } catch (NotConnectedException e) {
/*      */     }
/* 1722 */     throw new StorageException(e);
/*      */   }
/*      */ 
/*      */   public List<StrategyParameter> listStrategyParameters(long fileId, LoadingProgressListener loadingProgress)
/*      */     throws StorageException
/*      */   {
/* 1728 */     List ret = new ArrayList();
/*      */ 
/* 1730 */     FileMngRequestMessage msg = new FileMngRequestMessage();
/*      */ 
/* 1732 */     FileItem f = new FileItem();
/* 1733 */     f.setFileId(Long.valueOf(fileId));
/*      */ 
/* 1735 */     msg.setFileItem(f);
/* 1736 */     msg.setCommand(FileItem.Command.LIST_PARAMETERS);
/*      */     try
/*      */     {
/* 1740 */       connect(loadingProgress);
/*      */ 
/* 1742 */       ProtocolMessage response = null;
/*      */ 
/* 1744 */       this.thisLock.lock();
/*      */       try {
/* 1746 */         if (this.transportClient == null) {
/* 1747 */           throw new NotConnectedException("Cannot connect to data feed server, transport not initialized");
/*      */         }
/* 1749 */         response = this.transportClient.controlSynchRequest(msg, Long.valueOf(10000L));
/*      */       } finally {
/* 1751 */         this.thisLock.unlock();
/*      */       }
/*      */ 
/* 1754 */       if ((response instanceof FileMngResponseMessage)) {
/* 1755 */         FileMngResponseMessage fileMngResponseMessage = (FileMngResponseMessage)response;
/* 1756 */         ret = fileMngResponseMessage.getFileItem().getParameters(); } else {
/* 1757 */         if ((response instanceof ErrorResponseMessage)) {
/* 1758 */           throw new StorageException(((ErrorResponseMessage)response).getReason());
/*      */         }
/* 1760 */         throw new StorageException(new StringBuilder().append("Unknown response: ").append(response).toString());
/*      */       }
/*      */     } catch (TimeoutException e) {
/* 1763 */       throw new StorageException(e);
/*      */     } catch (NotConnectedException e) {
/* 1765 */       throw new StorageException(e);
/*      */     }
/*      */ 
/* 1768 */     return ret;
/*      */   }
/*      */ 
/*      */   public Map<Instrument, Map<Period, Long>> loadDataFeedStartTimes(List<Instrument> instruments, LoadingProgressListener loadingProgress) throws DataCacheException
/*      */   {
/* 1773 */     Map result = new HashMap();
/*      */ 
/* 1775 */     boolean incremented = false;
/* 1776 */     while (!loadingProgress.stopJob()) {
/*      */       try {
/* 1778 */         if (this.thisLock.tryLock(20L, TimeUnit.MILLISECONDS))
/*      */           try {
/* 1780 */             this.inProcess.incrementAndGet();
/* 1781 */             incremented = true;
/*      */           }
/*      */           finally {
/* 1784 */             this.thisLock.unlock();
/*      */           }
/*      */       }
/*      */       catch (InterruptedException e) {
/* 1788 */         LOGGER.error(e.getMessage(), e);
/*      */       }
/*      */     }
/*      */     try
/*      */     {
/* 1793 */       if (loadingProgress.stopJob()) {
/* 1794 */         e = null;
/*      */         return e;
/*      */       }
/* 1796 */       int tryCount = 0;
/*      */ 
/* 1799 */       while (tryCount < 5)
/*      */       {
/*      */         try {
/* 1802 */           connect(loadingProgress);
/*      */         } catch (NotConnectedException e) {
/* 1804 */           LOGGER.error(e.getMessage(), e);
/* 1805 */           tryCount++;
/* 1806 */         }continue;
/*      */ 
/* 1808 */         tryCount++;
/*      */ 
/* 1811 */         int requestId = this.requestNumber.getAndIncrement();
/*      */ 
/* 1814 */         String instrumentsAsString = convertInstrumentsForDFHistoryStartRequest(instruments);
/*      */ 
/* 1816 */         DFHistoryStartRequestMessage dfHistoryStartRequestMessage = new DFHistoryStartRequestMessage();
/* 1817 */         dfHistoryStartRequestMessage.setInstruments(instrumentsAsString);
/* 1818 */         dfHistoryStartRequestMessage.setRequestId(Integer.valueOf(requestId));
/*      */ 
/* 1820 */         synchronized (this.requestResponse)
/*      */         {
/* 1822 */           this.requestResponse.put(Integer.valueOf(requestId), null);
/*      */         }
/* 1824 */         this.thisLock.lock();
/*      */         try {
/* 1826 */           if (this.transportClient == null) {
/* 1827 */             throw new NotConnectedException("Cannot connect to data feed server, transport not initialized");
/*      */           }
/*      */ 
/* 1830 */           this.transportClient.controlRequest(dfHistoryStartRequestMessage);
/*      */         } finally {
/* 1832 */           this.thisLock.unlock();
/*      */         }
/*      */ 
/* 1835 */         synchronized (this.requestResponse) {
/* 1836 */           long requestTime = System.currentTimeMillis();
/*      */ 
/* 1838 */           if (!this.requestResponse.containsKey(Integer.valueOf(requestId)))
/*      */           {
/*      */             continue;
/*      */           }
/*      */ 
/* 1843 */           SortedSet responseMessages = (SortedSet)this.requestResponse.get(Integer.valueOf(requestId));
/* 1844 */           if ((responseMessages != null) && (responseMessages.size() > 0))
/*      */           {
/* 1848 */             ProtocolMessage msg = (ProtocolMessage)responseMessages.first();
/* 1849 */             if ((msg instanceof ErrorResponseMessage)) {
/* 1850 */               throw new DataCacheException(((ErrorResponseMessage)msg).getReason());
/*      */             }
/*      */ 
/* 1853 */             DFHistoryStartResponseMessage dfHistoryStartResponseMessage = (DFHistoryStartResponseMessage)msg;
/* 1854 */             Map localMap1 = covertDFHistoryStartResponse(dfHistoryStartResponseMessage.getHistoryStart());
/*      */ 
/* 1875 */             if (incremented) {
/* 1876 */               this.lastAccess.set(System.currentTimeMillis());
/* 1877 */               this.inProcess.decrementAndGet(); } return localMap1;
/*      */           }
/* 1858 */           if (requestTime + 10000L < System.currentTimeMillis())
/*      */           {
/* 1860 */             this.requestResponse.remove(Integer.valueOf(requestId));
/*      */ 
/* 1862 */             throw new DataCacheException("Request for first feed data times timed out");
/*      */           }
/*      */ 
/*      */           try
/*      */           {
/* 1867 */             this.requestResponse.wait(1000L);
/*      */           }
/*      */           catch (InterruptedException e) {
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */     finally {
/* 1875 */       if (incremented) {
/* 1876 */         this.lastAccess.set(System.currentTimeMillis());
/* 1877 */         this.inProcess.decrementAndGet();
/*      */       }
/*      */     }
/*      */ 
/* 1881 */     return result;
/*      */   }
/*      */ 
/*      */   private Map<Instrument, Map<Period, Long>> covertDFHistoryStartResponse(String historyStart) {
/* 1885 */     Map result = new HashMap();
/* 1886 */     if (historyStart != null) {
/* 1887 */       String[] instrumentsHistoryStarts = historyStart.split(";");
/* 1888 */       for (String byInstrument : instrumentsHistoryStarts) {
/* 1889 */         String[] periodsHistoryStarts = byInstrument.split(",");
/*      */ 
/* 1891 */         Instrument instrument = Instrument.fromString(periodsHistoryStarts[0]);
/* 1892 */         Map periodMap = new HashMap();
/* 1893 */         result.put(instrument, periodMap);
/*      */ 
/* 1895 */         for (int i = 1; i < periodsHistoryStarts.length; i += 2) {
/* 1896 */           Period period = DataCacheUtils.getOldBasicPeriodFromInterval(Long.valueOf(periodsHistoryStarts[i]).longValue());
/* 1897 */           Long time = Long.valueOf(periodsHistoryStarts[(i + 1)]);
/* 1898 */           periodMap.put(period, time);
/*      */         }
/*      */       }
/*      */     }
/*      */ 
/* 1903 */     return result;
/*      */   }
/*      */ 
/*      */   private String convertInstrumentsForDFHistoryStartRequest(List<Instrument> instruments) {
/* 1907 */     if ((instruments == null) || (instruments.isEmpty())) {
/* 1908 */       return null;
/*      */     }
/*      */ 
/* 1911 */     StringBuilder builder = new StringBuilder(100);
/* 1912 */     for (int i = 0; i < instruments.size(); i++) {
/* 1913 */       Instrument instrument = (Instrument)instruments.get(i);
/* 1914 */       builder.append(instrument.toString());
/* 1915 */       if (i + 1 < instruments.size()) {
/* 1916 */         builder.append(",");
/*      */       }
/*      */     }
/* 1919 */     return builder.toString();
/*      */   }
/*      */ 
/*      */   private void fireConnected() {
/* 1923 */     List listeners = getDFSConnectionListeners();
/* 1924 */     for (DataFeedServerConnectionListener listener : listeners)
/*      */       try {
/* 1926 */         listener.connected();
/*      */       } catch (Throwable t) {
/* 1928 */         LOGGER.error(t.getLocalizedMessage(), t);
/*      */       }
/*      */   }
/*      */ 
/*      */   private void fireDisconnected()
/*      */   {
/* 1934 */     List listeners = getDFSConnectionListeners();
/* 1935 */     for (DataFeedServerConnectionListener listener : listeners)
/*      */       try {
/* 1937 */         listener.disconnected();
/*      */       } catch (Throwable t) {
/* 1939 */         LOGGER.error(t.getLocalizedMessage(), t);
/*      */       }
/*      */   }
/*      */ 
/*      */   public void addDFSConnectionListener(DataFeedServerConnectionListener listener)
/*      */   {
/* 1946 */     if (listener == null) {
/* 1947 */       throw new NullPointerException("listener is null");
/*      */     }
/* 1949 */     synchronized (this.dfsConnectionListeners) {
/* 1950 */       this.dfsConnectionListeners.add(listener);
/*      */     }
/*      */   }
/*      */ 
/*      */   public List<DataFeedServerConnectionListener> getDFSConnectionListeners()
/*      */   {
/* 1956 */     synchronized (this.dfsConnectionListeners) {
/* 1957 */       List copy = new ArrayList();
/* 1958 */       copy.addAll(this.dfsConnectionListeners);
/* 1959 */       return Collections.unmodifiableList(copy);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void removeDFSConnectionListener(DataFeedServerConnectionListener listener)
/*      */   {
/* 1965 */     if (listener == null) {
/* 1966 */       throw new NullPointerException("listener is null");
/*      */     }
/* 1968 */     synchronized (this.dfsConnectionListeners) {
/* 1969 */       this.dfsConnectionListeners.remove(listener);
/*      */     }
/*      */   }
/*      */ 
/*      */   public boolean isDFSOnline()
/*      */   {
/* 1975 */     if (this.transportClient != null) {
/* 1976 */       return this.transportClient.isOnline();
/*      */     }
/* 1978 */     return false;
/*      */   }
/*      */ 
/*      */   public void removeAllDFSConnectionListeners()
/*      */   {
/* 1983 */     synchronized (this.dfsConnectionListeners) {
/* 1984 */       this.dfsConnectionListeners.clear();
/*      */     }
/*      */   }
/*      */ 
/*      */   public boolean pingDFS()
/*      */   {
/* 1990 */     if (isDFSOnline())
/* 1991 */       return true;
/*      */     try
/*      */     {
/* 1994 */       boolean connected = fastConnect();
/* 1995 */       return connected;
/*      */     } catch (Throwable e) {
/* 1997 */       LOGGER.error(e.getMessage(), e);
/*      */     }
/* 1999 */     return false;
/*      */   }
/*      */ 
/*      */   static
/*      */   {
/*   59 */     LOGGER = LoggerFactory.getLogger(CurvesJsonProtocolHandler.class);
/*      */ 
/*   61 */     DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
/*      */ 
/*   63 */     DATE_FORMAT.setTimeZone(TimeZone.getTimeZone("GMT"));
/*      */   }
/*      */ 
/*      */   private static class FileResponseMessage extends ProtocolMessage
/*      */   {
/*      */     private byte[] data;
/*      */ 
/*      */     public byte[] getData()
/*      */     {
/* 1349 */       return this.data;
/*      */     }
/*      */ 
/*      */     public void setData(byte[] data) {
/* 1353 */       this.data = data;
/*      */     }
/*      */   }
/*      */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.charts.data.datacache.CurvesJsonProtocolHandler
 * JD-Core Version:    0.6.0
 */