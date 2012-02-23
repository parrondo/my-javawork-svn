/*     */ package com.dukascopy.transport.client;
/*     */ 
/*     */ import com.dukascopy.transport.client.events.AuthorizedEvent;
/*     */ import com.dukascopy.transport.client.events.DisconnectedEvent;
/*     */ import com.dukascopy.transport.client.events.FeedbackMessageReceivedEvent;
/*     */ import com.dukascopy.transport.client.events.StreamReceivedEvent;
/*     */ import com.dukascopy.transport.common.mina.InvocationRequest;
/*     */ import com.dukascopy.transport.common.mina.InvocationResult;
/*     */ import com.dukascopy.transport.common.mina.RemoteCallSupport;
/*     */ import com.dukascopy.transport.common.mina.SynchRequestFuture;
/*     */ import com.dukascopy.transport.common.mina.TransportHelper;
/*     */ import com.dukascopy.transport.common.msg.BinaryPartMessage;
/*     */ import com.dukascopy.transport.common.msg.JSonSerializableWrapper;
/*     */ import com.dukascopy.transport.common.msg.ProtocolMessage;
/*     */ import com.dukascopy.transport.common.msg.StreamHeaderMessage;
/*     */ import com.dukascopy.transport.common.msg.StreamingStatus;
/*     */ import com.dukascopy.transport.common.msg.request.CurrencyMarket;
/*     */ import com.dukascopy.transport.common.msg.request.HeartbeatRequestMessage;
/*     */ import com.dukascopy.transport.common.msg.response.HeartbeatOkResponseMessage;
/*     */ import com.dukascopy.transport.common.msg.response.RequestInProcessMessage;
/*     */ import com.dukascopy.transport.common.protocol.json.JSonProtocolCodecFactory;
/*     */ import java.io.IOException;
/*     */ import java.io.Serializable;
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.concurrent.ArrayBlockingQueue;
/*     */ import java.util.concurrent.BlockingQueue;
/*     */ import java.util.concurrent.Executor;
/*     */ import java.util.concurrent.ThreadFactory;
/*     */ import java.util.concurrent.ThreadPoolExecutor;
/*     */ import java.util.concurrent.TimeUnit;
/*     */ import org.apache.mina.common.ByteBuffer;
/*     */ import org.apache.mina.common.DefaultIoFilterChainBuilder;
/*     */ import org.apache.mina.common.ExecutorThreadModel;
/*     */ import org.apache.mina.common.IoSession;
/*     */ import org.apache.mina.common.SimpleByteBufferAllocator;
/*     */ import org.apache.mina.filter.codec.ProtocolCodecFactory;
/*     */ import org.apache.mina.filter.codec.ProtocolCodecFilter;
/*     */ import org.apache.mina.transport.socket.nio.SocketConnector;
/*     */ import org.apache.mina.transport.socket.nio.SocketConnectorConfig;
/*     */ import org.apache.mina.transport.socket.nio.SocketSessionConfig;
/*     */ import org.slf4j.Logger;
/*     */ import org.slf4j.LoggerFactory;
/*     */ 
/*     */ public class ClientProtocolHandler
/*     */ {
/*  51 */   private static final Logger log = LoggerFactory.getLogger(ClientProtocolHandler.class);
/*     */   private TransportClient client;
/*     */   protected ProtocolCodecFactory codecFactory;
/*     */   private CriteriaIsolationExecutor eventExecutor;
/*     */   private ClientConnector clientConnector;
/*     */   private SocketConnector connector;
/*  64 */   private Map<String, BlockingBinaryStream> streams = new HashMap();
/*     */ 
/*  66 */   private static int counter = 0;
/*     */   private Executor minaThreadPoolExecutor;
/*     */ 
/*     */   public ClientProtocolHandler(TransportClient client, CriteriaIsolationExecutor eventExecutor, Executor minaThreadPoolExecutor)
/*     */   {
/*  73 */     this.minaThreadPoolExecutor = minaThreadPoolExecutor;
/*  74 */     this.client = client;
/*  75 */     this.eventExecutor = eventExecutor;
/*  76 */     init();
/*     */   }
/*     */ 
/*     */   private void init() {
/*  80 */     this.client.getClass(); this.codecFactory = new JSonProtocolCodecFactory(10240000);
/*     */ 
/*  82 */     this.connector = new SocketConnector();
/*  83 */     ByteBuffer.setUseDirectBuffers(false);
/*  84 */     ByteBuffer.setAllocator(new SimpleByteBufferAllocator());
/*  85 */     if (this.minaThreadPoolExecutor == null) {
/*  86 */       this.minaThreadPoolExecutor = new ThreadPoolExecutor(this.client.getMinaPoolSize(), this.client.getMinaPoolSize(), 60L, TimeUnit.SECONDS, new ArrayBlockingQueue(receiveQueueSize()));
/*     */ 
/*  90 */       ((ThreadPoolExecutor)this.minaThreadPoolExecutor).setThreadFactory(new ThreadFactory()
/*     */       {
/*     */         public Thread newThread(Runnable r) {
/*  93 */           Thread thread = new Thread(r, "Mina-thread-" + ClientProtocolHandler.counter);
/*     */ 
/*  95 */           ClientProtocolHandler.access$008();
/*  96 */           return thread;
/*     */         } } );
/*     */     }
/* 100 */     ((ExecutorThreadModel)this.connector.getDefaultConfig().getThreadModel()).setExecutor(this.minaThreadPoolExecutor);
/*     */ 
/* 103 */     this.connector.getFilterChain().addFirst("codec", new ProtocolCodecFilter(this.codecFactory));
/*     */ 
/* 106 */     SocketConnectorConfig conf = this.connector.getDefaultConfig();
/* 107 */     conf.getSessionConfig().setTcpNoDelay(false);
/* 108 */     conf.getSessionConfig().setSoLinger(-1);
/* 109 */     this.clientConnector = new ClientConnector(this.connector, this.client, this);
/* 110 */     this.clientConnector.start();
/*     */   }
/*     */ 
/*     */   public Executor getMinaThreadPoolExecutor() {
/* 114 */     return this.minaThreadPoolExecutor;
/*     */   }
/*     */ 
/*     */   public ClientProtocolHandler(TransportClient client, CriteriaIsolationExecutor eventExecutor)
/*     */   {
/* 119 */     this.client = client;
/* 120 */     this.eventExecutor = eventExecutor;
/* 121 */     init();
/*     */   }
/*     */ 
/*     */   private int receiveQueueSize() {
/* 125 */     return 100;
/*     */   }
/*     */ 
/*     */   public void connect() {
/* 129 */     this.clientConnector.connect();
/*     */   }
/*     */ 
/*     */   public void messageReceived(IoSession session, Object msg) {
/* 133 */     if (!(msg instanceof CurrencyMarket))
/* 134 */       log.debug("RECEIVE : " + msg);
/*     */     try
/*     */     {
/* 137 */       if ((msg instanceof HeartbeatRequestMessage)) {
/* 138 */         HeartbeatOkResponseMessage ok = new HeartbeatOkResponseMessage();
/* 139 */         ok.put("receiveTime", String.valueOf(System.currentTimeMillis()));
/*     */ 
/* 141 */         ok.put("reqid", String.valueOf(((HeartbeatRequestMessage)msg).getSynchRequestId()));
/*     */ 
/* 143 */         session.write(ok);
/* 144 */         return;
/*     */       }
/* 146 */       if ((msg instanceof BinaryPartMessage)) {
/* 147 */         BinaryPartMessage binaryPart = (BinaryPartMessage)msg;
/* 148 */         streamPartReceived(session, msg, binaryPart);
/* 149 */         return;
/*     */       }
/* 151 */       if ((msg instanceof StreamHeaderMessage)) {
/* 152 */         StreamHeaderMessage binaryPart = (StreamHeaderMessage)msg;
/* 153 */         createStream(binaryPart, session);
/*     */ 
/* 155 */         return;
/*     */       }
/* 157 */       if ((msg instanceof StreamingStatus)) {
/* 158 */         StreamingStatus status = (StreamingStatus)msg;
/* 159 */         streamStatusReceived(status);
/* 160 */         return;
/*     */       }
/* 162 */       ProtocolMessage message = (ProtocolMessage)msg;
/* 163 */       if ((message.getSynchRequestId() != null) && (processSynchResponse(message)))
/*     */       {
/* 165 */         return;
/*     */       }
/* 167 */       fireFeedbackMessageReceived(message, session);
/*     */     } catch (Exception e) {
/* 169 */       e.printStackTrace();
/*     */     }
/*     */   }
/*     */ 
/*     */   private void streamStatusReceived(StreamingStatus status) {
/* 174 */     BlockingBinaryStream stream = getStream(status.getStreamId());
/*     */ 
/* 176 */     if (stream != null) {
/* 177 */       stream.ioTerminate("Server error");
/* 178 */       synchronized (this.streams) {
/* 179 */         this.streams.remove(stream.getStreamId());
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   private void streamPartReceived(IoSession session, Object msg, BinaryPartMessage binaryPart)
/*     */   {
/* 186 */     BlockingBinaryStream stream = getStream(binaryPart.getStreamId());
/* 187 */     if (stream == null) {
/* 188 */       StreamingStatus ss = new StreamingStatus(binaryPart.getStreamId(), "err");
/*     */ 
/* 190 */       session.write(ss);
/*     */     } else {
/*     */       try {
/* 193 */         stream.binaryPartReceived(binaryPart);
/*     */       } catch (IOException e) {
/* 195 */         log.error(e.getMessage(), e);
/* 196 */         stream.ioTerminate(e.getMessage());
/*     */       }
/*     */ 
/* 199 */       if (binaryPart.isEOF())
/* 200 */         synchronized (this.streams) {
/* 201 */           this.streams.remove(binaryPart.getStreamId());
/*     */         }
/*     */     }
/*     */   }
/*     */ 
/*     */   protected void disconnect(DisconnectReason reason)
/*     */   {
/* 212 */     this.clientConnector.setState(1, reason);
/*     */   }
/*     */ 
/*     */   public IoSession getSession() {
/* 216 */     return this.clientConnector.getPrimarySession();
/*     */   }
/*     */ 
/*     */   public IoSession getChildSession() {
/* 220 */     return this.clientConnector.getChildSession();
/*     */   }
/*     */ 
/*     */   protected void fireAuthorized() {
/* 224 */     if (this.client.getListener() != null)
/* 225 */       this.eventExecutor.execute(new AuthorizedEvent(this.client));
/*     */   }
/*     */ 
/*     */   public void terminate()
/*     */   {
/* 230 */     this.clientConnector.setTerminating(true);
/* 231 */     this.clientConnector.interrupt();
/*     */     try {
/* 233 */       this.clientConnector.join(5000L);
/*     */     }
/*     */     catch (InterruptedException e)
/*     */     {
/*     */     }
/*     */   }
/*     */ 
/*     */   protected void fireFeedbackMessageReceived(ProtocolMessage message, IoSession session)
/*     */   {
/* 243 */     if ((message instanceof JSonSerializableWrapper)) {
/* 244 */       processSerializableRequest(session, message);
/* 245 */       return;
/*     */     }
/* 247 */     long procStartTime = System.currentTimeMillis();
/* 248 */     if (this.client.getListener() != null) {
/* 249 */       boolean taskInqueue = false;
/* 250 */       int loggerFilter = 0;
/* 251 */       while (!taskInqueue) {
/* 252 */         if (this.eventExecutor.getScheduledSize() < 30) {
/* 253 */           this.eventExecutor.executeFeedbackEvent(new FeedbackMessageReceivedEvent(this.client, message));
/*     */ 
/* 256 */           taskInqueue = true;
/* 257 */           loggerFilter = 0; continue;
/*     */         }
/* 259 */         if ((message instanceof CurrencyMarket)) {
/* 260 */           return;
/*     */         }
/* 262 */         if ((System.currentTimeMillis() - procStartTime > 10000L) && (loggerFilter % 100 == 0))
/*     */         {
/* 264 */           log.warn("fireFeedbackMessageReceived: TOO LARGE EXECUTION WAIT TIME: " + (System.currentTimeMillis() - procStartTime) + ". User application operations is too slow. Can't wait any more, It's block Mina thread. Drop message:  " + message);
/*     */ 
/* 268 */           return;
/*     */         }
/* 270 */         if ((System.currentTimeMillis() - procStartTime > 3000L) && (loggerFilter % 100 == 0))
/*     */         {
/* 272 */           log.warn("fireFeedbackMessageReceived: TOO LARGE EXECUTION WAIT TIME: " + (System.currentTimeMillis() - procStartTime) + ". User application operations is too slow.");
/*     */         }
/*     */ 
/* 276 */         loggerFilter++;
/*     */         try {
/* 278 */           Thread.sleep(10L);
/*     */         }
/*     */         catch (InterruptedException e)
/*     */         {
/*     */         }
/* 283 */         if (!(((ExecutorThreadModel)this.connector.getDefaultConfig().getThreadModel()).getExecutor() instanceof ThreadPoolExecutor))
/*     */           continue;
/* 285 */         ThreadPoolExecutor ex = (ThreadPoolExecutor)((ExecutorThreadModel)this.connector.getDefaultConfig().getThreadModel()).getExecutor();
/*     */ 
/* 288 */         if (ex.getQueue().size() > 500) {
/* 289 */           log.warn("MINA Thread executor queue is overloaded. User application operations is too slow. Disconnecting");
/* 290 */           disconnect(DisconnectReason.CLIENT_LISTENER_THREAD_POOL_QUEUE_OVERLOADED);
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   private void processSerializableRequest(IoSession session, Object msg)
/*     */   {
/* 299 */     JSonSerializableWrapper wrapper = (JSonSerializableWrapper)msg;
/* 300 */     Serializable ser = wrapper.getData();
/* 301 */     if ((ser != null) && ((ser instanceof InvocationResult))) {
/* 302 */       InvocationResult res = (InvocationResult)ser;
/* 303 */       this.client.remoteCallSupport.invocationResultReceived(res);
/* 304 */       return;
/*     */     }
/* 306 */     if ((ser != null) && ((ser instanceof InvocationRequest))) {
/* 307 */       InvocationRequest res = (InvocationRequest)ser;
/* 308 */       JSonSerializableWrapper responseMessage = new JSonSerializableWrapper();
/* 309 */       Object impl = this.client.getInterfaceImplementation(res.getInterfaceClass());
/*     */ 
/* 311 */       if (impl != null) {
/*     */         try {
/* 313 */           Serializable invocationResult = (Serializable)TransportHelper.invokeRemoteRequest(res, impl);
/*     */ 
/* 315 */           InvocationResult result = new InvocationResult(invocationResult, res.getRequestId());
/*     */ 
/* 317 */           result.setState(0);
/* 318 */           responseMessage.setData(result);
/*     */         } catch (Exception e) {
/* 320 */           InvocationResult result = new InvocationResult(null, res.getRequestId());
/*     */ 
/* 322 */           result.setState(1);
/* 323 */           result.setErrorReason(e.getMessage());
/* 324 */           responseMessage.setData(result);
/*     */         }
/*     */       } else {
/* 327 */         InvocationResult result = new InvocationResult(null, res.getRequestId());
/*     */ 
/* 329 */         result.setState(1);
/* 330 */         result.setErrorReason("Client not provide interface: " + res.getInterfaceClass());
/*     */ 
/* 332 */         responseMessage.setData(result);
/*     */       }
/* 334 */       session.write(responseMessage);
/* 335 */       return;
/*     */     }
/*     */   }
/*     */ 
/*     */   protected boolean processSynchResponse(ProtocolMessage message) {
/* 340 */     boolean res = false;
/* 341 */     SynchRequestFuture synchRequestFuture = this.client.getFuture(message.getSynchRequestId());
/*     */ 
/* 343 */     if (synchRequestFuture != null) {
/* 344 */       res = true;
/* 345 */       synchronized (synchRequestFuture) {
/* 346 */         if (((message instanceof RequestInProcessMessage)) && (synchRequestFuture != null))
/*     */         {
/* 348 */           synchRequestFuture.setLastResponseTime(Long.valueOf(System.currentTimeMillis()));
/*     */ 
/* 350 */           synchRequestFuture.setRequestInProcess(true);
/* 351 */           synchRequestFuture.setContinueProcess(true);
/* 352 */           synchRequestFuture.notifyAll();
/* 353 */           return res;
/*     */         }
/* 355 */         synchRequestFuture.setContinueProcess(false);
/* 356 */         synchRequestFuture.setResponse(message);
/* 357 */         synchRequestFuture.notifyAll();
/*     */       }
/*     */     }
/* 360 */     return res;
/*     */   }
/*     */ 
/*     */   public boolean isOnline() {
/* 364 */     return this.clientConnector.isOnline();
/*     */   }
/*     */ 
/*     */   protected void fireDisconnected(DisconnectReason reason) {
/* 368 */     log.debug("Terminating streams with IoException");
/* 369 */     terminateStreams();
/* 370 */     log.debug("Firing disconnect ...");
/* 371 */     if (this.client.getListener() != null) {
/* 372 */       this.eventExecutor.execute(new DisconnectedEvent(this.client, reason));
/* 373 */       log.debug("Disconnect task in queue ...");
/*     */     } else {
/* 375 */       log.error("Can't fire disconnected event: listener is null");
/*     */     }
/*     */   }
/*     */ 
/*     */   protected BlockingBinaryStream getStream(String streamId) {
/* 380 */     BlockingBinaryStream bbs = null;
/* 381 */     synchronized (this.streams) {
/* 382 */       bbs = (BlockingBinaryStream)this.streams.get(streamId);
/*     */     }
/* 384 */     return bbs;
/*     */   }
/*     */ 
/*     */   protected BlockingBinaryStream createStream(StreamHeaderMessage message, IoSession session)
/*     */   {
/* 389 */     BlockingBinaryStream bbs = new BlockingBinaryStream(message.getStreamId(), session);
/*     */ 
/* 391 */     synchronized (this.streams) {
/* 392 */       this.streams.put(message.getStreamId(), bbs);
/* 393 */       StreamReceivedEvent event = new StreamReceivedEvent(this.client, bbs);
/*     */ 
/* 395 */       this.eventExecutor.execute(event);
/*     */     }
/* 397 */     return bbs;
/*     */   }
/*     */ 
/*     */   protected void terminateStreams() {
/* 401 */     List st = new ArrayList();
/* 402 */     synchronized (this.streams) {
/* 403 */       st.addAll(this.streams.keySet());
/* 404 */       for (String key : st) {
/* 405 */         BlockingBinaryStream bbs = (BlockingBinaryStream)this.streams.remove(key);
/* 406 */         bbs.ioTerminate("Connection error");
/*     */       }
/*     */     }
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\transport-client-2.3.78.jar
 * Qualified Name:     com.dukascopy.transport.client.ClientProtocolHandler
 * JD-Core Version:    0.6.0
 */