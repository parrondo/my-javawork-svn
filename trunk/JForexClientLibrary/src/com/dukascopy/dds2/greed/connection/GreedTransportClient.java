/*     */ package com.dukascopy.dds2.greed.connection;
/*     */ 
/*     */ import com.dukascopy.dds2.greed.GreedContext;
/*     */ import com.dukascopy.dds2.greed.gui.GuiUtilsAndConstants;
/*     */ import com.dukascopy.dds2.greed.gui.l10n.components.JLocalizableButton;
/*     */ import com.dukascopy.dds2.greed.gui.l10n.components.JLocalizableDialog;
/*     */ import com.dukascopy.dds2.greed.gui.l10n.components.JLocalizableLabel;
/*     */ import com.dukascopy.transport.client.ClientListener;
/*     */ import com.dukascopy.transport.client.SecurityExceptionHandler;
/*     */ import com.dukascopy.transport.client.TransportClient;
/*     */ import com.dukascopy.transport.common.msg.ProtocolMessage;
/*     */ import java.awt.Container;
/*     */ import java.awt.event.ActionEvent;
/*     */ import java.awt.event.ActionListener;
/*     */ import java.net.InetSocketAddress;
/*     */ import java.security.cert.CertificateException;
/*     */ import java.security.cert.X509Certificate;
/*     */ import java.util.concurrent.TimeoutException;
/*     */ import java.util.concurrent.locks.ReentrantLock;
/*     */ import javax.swing.ImageIcon;
/*     */ import javax.swing.JPanel;
/*     */ import javax.swing.SwingUtilities;
/*     */ import org.slf4j.Logger;
/*     */ import org.slf4j.LoggerFactory;
/*     */ 
/*     */ public class GreedTransportClient
/*     */ {
/*  33 */   private static Logger LOGGER = LoggerFactory.getLogger(GreedTransportClient.class);
/*     */ 
/*  35 */   private TransportClient transportClient = null;
/*     */ 
/*  37 */   private static GreedTransportClient singleInstance = null;
/*     */   private String username;
/*  41 */   private static ReentrantLock lock = new ReentrantLock();
/*     */ 
/*  43 */   private static boolean showWarning = true;
/*     */ 
/*     */   public static GreedTransportClient getInstance(ClientListener listener)
/*     */   {
/*  50 */     if (singleInstance == null) {
/*  51 */       singleInstance = new GreedTransportClient(listener);
/*     */     }
/*  53 */     return singleInstance;
/*     */   }
/*     */ 
/*     */   private GreedTransportClient(ClientListener listener) {
/*  57 */     lock.lock();
/*     */     try
/*     */     {
/*  60 */       if (this.transportClient == null) {
/*  61 */         this.transportClient = new TransportClient();
/*  62 */         this.transportClient.setListener(listener);
/*     */       }
/*     */ 
/*  65 */       this.transportClient.setUserAgent(GuiUtilsAndConstants.getUserAgent());
/*  66 */       this.transportClient.setPingConnection(true);
/*  67 */       this.transportClient.setPingTimeout(Long.valueOf(10000L));
/*  68 */       this.transportClient.setPoolSize(5);
/*     */ 
/*  70 */       this.transportClient.setSecurityExceptionHandler(new SecurityExceptionHandler()
/*     */       {
/*     */         public boolean isIgnoreSecurityException(X509Certificate[] chain, String authType, CertificateException exception)
/*     */         {
/*  76 */           GreedTransportClient.this.showWarning();
/*  77 */           return true;
/*     */         }
/*     */       });
/*  81 */       if (GreedContext.isTest())
/*  82 */         this.transportClient.setUseSsl(false);
/*     */       else {
/*  84 */         this.transportClient.setUseSsl(true);
/*     */       }
/*     */     }
/*     */     finally
/*     */     {
/*  89 */       lock.unlock();
/*     */     }
/*     */   }
/*     */ 
/*     */   public void connect() {
/*  94 */     if (LOGGER.isDebugEnabled());
/*  97 */     this.transportClient.connect();
/*  98 */     if (LOGGER.isDebugEnabled());
/*     */   }
/*     */ 
/*     */   public ProtocolMessage controlRequest(ProtocolMessage message)
/*     */   {
/* 104 */     return this.transportClient.controlRequest(message);
/*     */   }
/*     */ 
/*     */   public ProtocolMessage controlSynchRequest(ProtocolMessage message, long timeout) throws TimeoutException {
/* 108 */     return this.transportClient.controlSynchRequest(message, Long.valueOf(timeout));
/*     */   }
/*     */ 
/*     */   public void disconnect() {
/* 112 */     this.transportClient.disconnect();
/*     */   }
/*     */ 
/*     */   public boolean equals(Object obj) {
/* 116 */     return this.transportClient.equals(obj);
/*     */   }
/*     */ 
/*     */   public ClientListener getListener() {
/* 120 */     return this.transportClient.getListener();
/*     */   }
/*     */ 
/*     */   public final String getLogin() {
/* 124 */     return this.transportClient.getLogin();
/*     */   }
/*     */ 
/*     */   public String getPassword() {
/* 128 */     return this.transportClient.getPassword();
/*     */   }
/*     */ 
/*     */   public boolean getPasswordHashMode() {
/* 132 */     return this.transportClient.getPasswordHashMode();
/*     */   }
/*     */ 
/*     */   public Object getProperty(String key) {
/* 136 */     return this.transportClient.getProperty(key);
/*     */   }
/*     */ 
/*     */   public int hashCode() {
/* 140 */     return this.transportClient.hashCode();
/*     */   }
/*     */ 
/*     */   public boolean isOnline() {
/* 144 */     return this.transportClient.isOnline();
/*     */   }
/*     */ 
/*     */   public boolean isTerminated() {
/* 148 */     return this.transportClient.isTerminated();
/*     */   }
/*     */ 
/*     */   public void setAddress(InetSocketAddress address) {
/* 152 */     if (LOGGER.isDebugEnabled()) {
/* 153 */       LOGGER.debug("Set API socket address : " + address);
/*     */     }
/* 155 */     this.transportClient.setAddress(address);
/*     */   }
/*     */ 
/*     */   public void setUseSSL(boolean useSSL) {
/* 159 */     this.transportClient.setUseSsl(useSSL);
/*     */   }
/*     */ 
/*     */   public boolean isUseSSL() {
/* 163 */     return this.transportClient.isUseSsl();
/*     */   }
/*     */ 
/*     */   public void setListener(ClientListener listener) {
/* 167 */     this.transportClient.setListener(listener);
/*     */   }
/*     */ 
/*     */   public void setPasswordTicket(String passwordTicket) {
/* 171 */     this.transportClient.setPassword(passwordTicket);
/*     */   }
/*     */ 
/*     */   public void setPasswordHashMode(boolean mode) {
/* 175 */     this.transportClient.setPasswordHashMode(mode);
/*     */   }
/*     */ 
/*     */   public void setProperty(String key, Object value) {
/* 179 */     this.transportClient.setProperty(key, value);
/*     */   }
/*     */ 
/*     */   public void setUsername(String username) {
/* 183 */     this.username = username;
/* 184 */     this.transportClient.setLogin(this.username + " " + getSessionId());
/*     */   }
/*     */ 
/*     */   public void terminate() {
/* 188 */     this.transportClient.terminate();
/* 189 */     singleInstance = null;
/*     */   }
/*     */ 
/*     */   public void killTransportClient() {
/* 193 */     GreedClientListener.killListener();
/*     */ 
/* 195 */     this.transportClient.setListener(null);
/* 196 */     this.transportClient.disconnect();
/* 197 */     this.transportClient.terminate();
/*     */ 
/* 199 */     singleInstance = null;
/*     */   }
/*     */ 
/*     */   public String toString() {
/* 203 */     return this.transportClient.toString();
/*     */   }
/*     */ 
/*     */   public TransportClient getTransportClient()
/*     */   {
/* 209 */     return this.transportClient;
/*     */   }
/*     */ 
/*     */   private String getSessionId() {
/* 213 */     return (String)GreedContext.getConfig("SESSION_ID");
/*     */   }
/*     */ 
/*     */   private void showWarning() {
/* 217 */     if (showWarning) {
/* 218 */       showWarning = false;
/* 219 */       SwingUtilities.invokeLater(new Runnable()
/*     */       {
/*     */         public void run() {
/* 222 */           JLocalizableDialog warning = new JLocalizableDialog();
/* 223 */           warning.setTitle("security.certificate.warning");
/* 224 */           warning.setModal(true);
/*     */           try {
/* 226 */             warning.setIconImage(GuiUtilsAndConstants.PLATFPORM_ICON.getImage());
/*     */           } catch (Exception e) {
/* 228 */             GreedTransportClient.LOGGER.error(e.getMessage(), e);
/*     */           }
/*     */ 
/* 231 */           JPanel messagePane = new JPanel();
/* 232 */           messagePane.add(new JLocalizableLabel("secutity.certificate.text", new String[] { GuiUtilsAndConstants.LABEL_SHORT_NAME }));
/* 233 */           warning.getContentPane().add(messagePane);
/* 234 */           JPanel buttonPane = new JPanel();
/*     */ 
/* 236 */           JLocalizableButton button = new JLocalizableButton("button.ok");
/* 237 */           buttonPane.add(button);
/*     */ 
/* 239 */           button.addActionListener(new ActionListener(warning)
/*     */           {
/*     */             public void actionPerformed(ActionEvent e) {
/* 242 */               this.val$warning.setVisible(false);
/* 243 */               this.val$warning.dispose();
/*     */             }
/*     */           });
/* 247 */           warning.getContentPane().add(buttonPane, "South");
/* 248 */           warning.setDefaultCloseOperation(2);
/* 249 */           warning.pack();
/* 250 */           warning.setLocationRelativeTo(null);
/* 251 */           warning.setResizable(false);
/* 252 */           warning.setVisible(true);
/*     */         }
/*     */       });
/*     */     }
/*     */   }
/*     */ 
/*     */   public static enum TRANSPORT_RC
/*     */   {
/* 206 */     ERROR_INIT, ERROR_BAD_URL, ERROR_AUTH, ERROR_IO, WRONG_VERSION, OK, NO_SERVERS, SYSTEM_ERROR;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.connection.GreedTransportClient
 * JD-Core Version:    0.6.0
 */