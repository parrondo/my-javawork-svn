/*     */ package com.dukascopy.transport.client;
/*     */ 
/*     */ import com.dukascopy.transport.common.mina.RemoteCallSupport;
/*     */ import com.dukascopy.transport.common.mina.ssl.ClientSSLContextListener;
/*     */ import com.dukascopy.transport.common.mina.ssl.SSLContextFactory;
/*     */ import com.dukascopy.transport.common.msg.ChildSocketAuthAcceptorMessage;
/*     */ import com.dukascopy.transport.common.msg.ProtocolMessage;
/*     */ import com.dukascopy.transport.common.msg.request.HaloRequestMessage;
/*     */ import com.dukascopy.transport.common.msg.request.LoginRequestMessage;
/*     */ import com.dukascopy.transport.common.msg.response.ErrorResponseMessage;
/*     */ import com.dukascopy.transport.common.msg.response.HaloResponseMessage;
/*     */ import com.dukascopy.transport.common.msg.response.OkResponseMessage;
/*     */ import com.dukascopy.transport.common.protocol.json.IoSessionState;
/*     */ import com.dukascopy.transport.util.MD5;
/*     */ import java.net.InetSocketAddress;
/*     */ import java.net.SocketAddress;
/*     */ import java.security.GeneralSecurityException;
/*     */ import java.security.NoSuchAlgorithmException;
/*     */ import java.security.cert.CertificateException;
/*     */ import java.security.cert.X509Certificate;
/*     */ import java.util.Set;
/*     */ import org.apache.mina.common.CloseFuture;
/*     */ import org.apache.mina.common.ConnectFuture;
/*     */ import org.apache.mina.common.DefaultIoFilterChainBuilder;
/*     */ import org.apache.mina.common.IdleStatus;
/*     */ import org.apache.mina.common.IoHandler;
/*     */ import org.apache.mina.common.IoSession;
/*     */ import org.apache.mina.filter.SSLFilter;
/*     */ import org.apache.mina.transport.socket.nio.SocketConnector;
/*     */ import org.slf4j.Logger;
/*     */ import org.slf4j.LoggerFactory;
/*     */ 
/*     */ public class ClientConnector extends Thread
/*     */   implements IoHandler, ClientSSLContextListener
/*     */ {
/*     */   private static final long AUTHORIZATION_TIMEOUT = 10000L;
/*  35 */   private static final Logger log = LoggerFactory.getLogger(ClientConnector.class);
/*     */ 
/*  37 */   private ClientState clientState = new ClientState();
/*     */ 
/*  39 */   private boolean terminating = false;
/*     */   private SocketConnector connector;
/*     */   private IoSession primarySession;
/*     */   private IoSession childSession;
/*     */   private TransportClient client;
/*     */   private Long authorizationStartTime;
/*     */   private HaloResponseMessage haloResponse;
/*     */   private ChildSocketAuthAcceptorMessage childSocketAuthAcceptorMessage;
/*     */   private ClientProtocolHandler protocolHandler;
/*  59 */   private int childSessionReconnectionAttempts = 0;
/*     */ 
/*     */   public ClientConnector(SocketConnector connector, TransportClient client, ClientProtocolHandler protocolHandler) {
/*  62 */     super("ClientConnector");
/*  63 */     this.connector = connector;
/*  64 */     this.client = client;
/*  65 */     this.protocolHandler = protocolHandler;
/*     */   }
/*     */ 
/*     */   public void connect() {
/*  69 */     setState(2);
/*     */   }
/*     */ 
/*     */   public void disconnect() {
/*  73 */     setState(1, DisconnectReason.CLIENT_APP_REQUEST);
/*     */   }
/*     */ 
/*     */   public void run()
/*     */   {
/*  83 */     while (!this.terminating)
/*     */       try {
/*  85 */         Thread.sleep(100L);
/*  86 */         if (this.clientState.getState() == 1) {
/*  87 */           closeAllSessions();
/*  88 */           this.haloResponse = null;
/*  89 */           this.childSocketAuthAcceptorMessage = null;
/*  90 */           this.primarySession = null;
/*  91 */           this.authorizationStartTime = null;
/*  92 */           setState(0, this.clientState.getDisconnectReason());
/*  93 */           fireDiconnected(this.clientState.getDisconnectReason());
/*     */         }
/*  97 */         else if (this.clientState.getState() == 2) {
/*  98 */           closeAllSessions();
/*  99 */           this.primarySession = null;
/* 100 */           this.childSession = null;
/* 101 */           this.haloResponse = null;
/* 102 */           this.childSocketAuthAcceptorMessage = null;
/* 103 */           this.primarySession = null;
/* 104 */           this.authorizationStartTime = null;
/* 105 */           if ((this.client.isUseSsl()) && (!this.connector.getFilterChain().contains("ssl_filter")))
/*     */             try {
/* 107 */               SSLFilter filter = new SSLFilter(SSLContextFactory.getInstance(false, this, this.client.getAddress().getHostName()));
/* 108 */               filter.setUseClientMode(true);
/* 109 */               this.connector.getFilterChain().addFirst("ssl_filter", filter);
/*     */             }
/*     */             catch (NoSuchAlgorithmException e) {
/* 112 */               e.printStackTrace();
/* 113 */               System.exit(0);
/*     */             }
/*     */             catch (GeneralSecurityException e) {
/* 116 */               e.printStackTrace();
/*     */             }
/* 118 */           else if ((!this.client.isUseSsl()) && (this.connector.getFilterChain().contains("ssl_filter"))) {
/* 119 */             this.connector.getFilterChain().remove("ssl_filter");
/*     */           }
/* 121 */           ConnectFuture connectFuture = null;
/* 122 */           connectFuture = this.connector.connect(this.client.getAddress(), this);
/*     */ 
/* 124 */           connectFuture.join();
/*     */ 
/* 126 */           if (connectFuture.isConnected()) {
/* 127 */             this.primarySession = connectFuture.getSession();
/* 128 */             this.primarySession.setAttribute("sessionState", new IoSessionState());
/* 129 */             HaloRequestMessage halo = new HaloRequestMessage();
/*     */ 
/* 131 */             halo.setUseragent(this.client.getUserAgent() + "-" + this.client.getTransportClientVersion());
/* 132 */             halo.setPingable(true);
/* 133 */             this.primarySession.write(halo);
/* 134 */             this.authorizationStartTime = Long.valueOf(System.currentTimeMillis());
/* 135 */             setState(3);
/* 136 */             this.childSessionReconnectionAttempts = 0;
/*     */           } else {
/* 138 */             log.info("Connect call failed");
/* 139 */             setState(1, DisconnectReason.CONNECTION_PROBLEM);
/*     */           }
/*     */ 
/*     */         }
/* 144 */         else if (this.clientState.getState() == 3) {
/* 145 */           if (this.authorizationStartTime.longValue() + 10000L < System.currentTimeMillis()) {
/* 146 */             log.info("Authorization timeout");
/*     */ 
/* 148 */             setState(1, DisconnectReason.AUTHORIZATION_TIMEOUT);
/*     */           }
/* 150 */         } else if ((this.clientState.getState() == 4) && 
/* 151 */           (this.client.isCreateFeederSocket()) && (this.childSocketAuthAcceptorMessage != null) && (
/* 152 */           (this.childSession == null) || (!this.childSession.isConnected()))) {
/* 153 */           if (this.childSessionReconnectionAttempts >= 10) {
/* 154 */             log.warn("Child session max connection attemts reached. disconnecting client");
/* 155 */             setState(1, DisconnectReason.CONNECTION_PROBLEM);
/*     */           } else {
/* 157 */             Thread.sleep(1000L);
/* 158 */             log.debug("Trying to connect child session. attempt " + this.childSessionReconnectionAttempts);
/* 159 */             this.childSessionReconnectionAttempts += 1;
/* 160 */             ConnectFuture connectFuture = null;
/* 161 */             connectFuture = this.connector.connect(this.client.getAddress(), this);
/* 162 */             connectFuture.join();
/* 163 */             if ((connectFuture.isConnected()) && (connectFuture.getSession() != null)) {
/* 164 */               log.debug("Child session opened");
/* 165 */               this.childSessionReconnectionAttempts = 0;
/* 166 */               this.childSession = connectFuture.getSession();
/* 167 */               this.childSession.setAttribute("sessionState", new IoSessionState());
/* 168 */               this.childSession.write(this.childSocketAuthAcceptorMessage);
/*     */             }
/*     */           }
/*     */         }
/*     */       }
/*     */       catch (InterruptedException e)
/*     */       {
/*     */       }
/*     */   }
/*     */ 
/*     */   private void closeAllSessions()
/*     */   {
/* 183 */     Set socks = this.connector.getManagedServiceAddresses();
/* 184 */     for (SocketAddress sa : socks) {
/* 185 */       Set sessions = this.connector.getManagedSessions(sa);
/* 186 */       for (IoSession s : sessions) {
/* 187 */         CloseFuture cf = s.close();
/* 188 */         cf.join();
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public int getClientState()
/*     */   {
/* 198 */     synchronized (this.clientState) {
/* 199 */       return this.clientState.getState();
/*     */     }
/*     */   }
/*     */ 
/*     */   protected void setState(int newstate)
/*     */   {
/* 208 */     setState(newstate, DisconnectReason.UNKNOWN);
/*     */   }
/*     */ 
/*     */   protected void setState(int newstate, DisconnectReason info)
/*     */   {
/* 216 */     synchronized (this.clientState) {
/* 217 */       log.debug("State changed to " + newstate);
/* 218 */       this.clientState.setState(newstate, info);
/*     */     }
/*     */   }
/*     */ 
/*     */   public boolean isTerminating()
/*     */   {
/* 226 */     return this.terminating;
/*     */   }
/*     */ 
/*     */   public void setTerminating(boolean terminating)
/*     */   {
/* 234 */     this.terminating = terminating;
/*     */   }
/*     */ 
/*     */   public boolean isOnline() {
/* 238 */     return this.clientState.getState() == 4;
/*     */   }
/*     */ 
/*     */   public void exceptionCaught(IoSession arg0, Throwable arg1)
/*     */     throws Exception
/*     */   {
/* 249 */     setState(1, DisconnectReason.EXCEPTION_CAUGHT);
/* 250 */     log.error("Exception caught", arg1);
/*     */   }
/*     */ 
/*     */   public void messageReceived(IoSession arg0, Object arg1)
/*     */     throws Exception
/*     */   {
/* 261 */     ProtocolMessage message = (ProtocolMessage)arg1;
/* 262 */     if (((message instanceof HaloResponseMessage)) && (this.clientState.getState() == 3)) {
/* 263 */       log.debug("Sent login");
/* 264 */       this.haloResponse = ((HaloResponseMessage)message);
/* 265 */       sendLoginInformation(arg0, this.haloResponse, this.client);
/* 266 */     } else if (((message instanceof OkResponseMessage)) && (this.haloResponse != null) && (this.clientState.getState() == 3)) {
/* 267 */       setState(4);
/* 268 */       this.client.remoteCallSupport.setSession(this.primarySession);
/* 269 */       fireAuthorized();
/* 270 */     } else if (((message instanceof ErrorResponseMessage)) && (this.clientState.getState() == 3)) {
/* 271 */       setState(1, DisconnectReason.AUTHORIZATION_FAILED);
/* 272 */     } else if (((message instanceof ChildSocketAuthAcceptorMessage)) && (this.clientState.getState() == 4)) {
/* 273 */       this.childSocketAuthAcceptorMessage = ((ChildSocketAuthAcceptorMessage)message);
/* 274 */     } else if (this.clientState.getState() == 4) {
/* 275 */       this.protocolHandler.messageReceived(arg0, arg1);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void messageSent(IoSession arg0, Object arg1)
/*     */     throws Exception
/*     */   {
/*     */   }
/*     */ 
/*     */   public void sessionClosed(IoSession arg0)
/*     */     throws Exception
/*     */   {
/* 300 */     log.debug("Session closed " + arg0);
/* 301 */     if ((arg0 == this.primarySession) && 
/* 302 */       (this.clientState.getState() == 4)) {
/* 303 */       log.error("Last encoded msg:  " + arg0.getAttribute("lastEncodedMessage"));
/* 304 */       log.error("(" + arg0.getRemoteAddress() + ") lastIoTime: " + (System.currentTimeMillis() - arg0.getLastIoTime()));
/* 305 */       log.error("(" + arg0.getRemoteAddress() + ") lastReadTime: " + (System.currentTimeMillis() - arg0.getLastReadTime()));
/* 306 */       log.error("(" + arg0.getRemoteAddress() + ") lastWriteTime: " + (System.currentTimeMillis() - arg0.getLastWriteTime()));
/* 307 */       log.error("(" + arg0.getRemoteAddress() + ") scheduled write bytes: " + arg0.getScheduledWriteBytes());
/* 308 */       log.error("(" + arg0.getRemoteAddress() + ") scheduled write requests: " + arg0.getScheduledWriteRequests());
/* 309 */       setState(1, DisconnectReason.CONNECTION_PROBLEM);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void sessionCreated(IoSession arg0)
/*     */     throws Exception
/*     */   {
/* 323 */     log.debug("Session created " + arg0);
/*     */   }
/*     */ 
/*     */   public void sessionIdle(IoSession arg0, IdleStatus arg1)
/*     */     throws Exception
/*     */   {
/*     */   }
/*     */ 
/*     */   public void sessionOpened(IoSession arg0)
/*     */     throws Exception
/*     */   {
/*     */   }
/*     */ 
/*     */   public void fireDiconnected(DisconnectReason reason)
/*     */   {
/* 352 */     this.protocolHandler.fireDisconnected(reason);
/*     */   }
/*     */ 
/*     */   public void fireAuthorized() {
/* 356 */     this.protocolHandler.fireAuthorized();
/*     */   }
/*     */ 
/*     */   private void sendLoginInformation(IoSession session, ProtocolMessage message, TransportClient client)
/*     */   {
/* 365 */     HaloResponseMessage hrm = (HaloResponseMessage)message;
/* 366 */     client.setServerSessionId(hrm.getSessionId());
/* 367 */     String challenge = hrm.getChallenge();
/* 368 */     LoginRequestMessage lrm = new LoginRequestMessage();
/* 369 */     lrm.setUsername(client.getLogin());
/* 370 */     lrm.setPasswordHash(client.getPasswordHashMode() ? MD5.getDigest(challenge + client.getPassword()) : MD5.getDigest(challenge + MD5.getDigest(client.getPassword())));
/*     */ 
/* 372 */     lrm.setMode(Integer.valueOf(client.getPasswordHashMode() ? 1 : 0));
/* 373 */     session.write(lrm);
/* 374 */     log.debug("Login information sent " + lrm);
/*     */   }
/*     */ 
/*     */   public void securityException(X509Certificate[] chain, String authType, CertificateException certificateException) {
/* 378 */     log.error("CERTIFICATE_EXCEPTION ", certificateException);
/* 379 */     if (this.client.getSecurityExceptionHandler() == null)
/* 380 */       setState(1, DisconnectReason.CERTIFICATE_EXCEPTION);
/* 381 */     else if (!this.client.getSecurityExceptionHandler().isIgnoreSecurityException(chain, authType, certificateException))
/* 382 */       setState(1, DisconnectReason.CERTIFICATE_EXCEPTION);
/*     */   }
/*     */ 
/*     */   public IoSession getPrimarySession()
/*     */   {
/* 390 */     return this.primarySession;
/*     */   }
/*     */ 
/*     */   public IoSession getChildSession()
/*     */   {
/* 397 */     return this.childSession;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\transport-client-2.3.78.jar
 * Qualified Name:     com.dukascopy.transport.client.ClientConnector
 * JD-Core Version:    0.6.0
 */