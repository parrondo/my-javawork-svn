/*    */ package com.dukascopy.transport.common.mina.ssl;
/*    */ 
/*    */ import java.security.KeyStoreException;
/*    */ import java.security.NoSuchAlgorithmException;
/*    */ import java.security.cert.CertificateException;
/*    */ import java.security.cert.X509Certificate;
/*    */ import javax.net.ssl.SSLException;
/*    */ import javax.net.ssl.X509TrustManager;
/*    */ 
/*    */ class DDSTrustManager
/*    */   implements X509TrustManager
/*    */ {
/*    */   private X509TrustManager sunX509TrustManager;
/*    */   private ClientSSLContextListener listener;
/* 16 */   private HostNameVerifier verifier = new HostNameVerifier();
/*    */   private String hostName;
/*    */ 
/*    */   public DDSTrustManager(ClientSSLContextListener listener, X509TrustManager manager)
/*    */     throws NoSuchAlgorithmException, KeyStoreException
/*    */   {
/* 22 */     this.listener = listener;
/* 23 */     this.sunX509TrustManager = manager;
/*    */   }
/*    */ 
/*    */   public String getHostName() {
/* 27 */     return this.hostName;
/*    */   }
/*    */ 
/*    */   public void setHostName(String hostName) {
/* 31 */     this.hostName = hostName;
/*    */   }
/*    */ 
/*    */   public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
/*    */     try {
/* 36 */       this.sunX509TrustManager.checkServerTrusted(chain, authType);
/*    */       try {
/* 38 */         this.verifier.check(new String[] { this.hostName }, chain[0]);
/*    */       } catch (SSLException e) {
/* 40 */         throw new CertificateException(e.getMessage());
/*    */       }
/*    */     } catch (CertificateException excep) {
/* 43 */       this.listener.securityException(chain, authType, excep);
/*    */     }
/*    */   }
/*    */ 
/*    */   public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
/*    */     try {
/* 49 */       this.sunX509TrustManager.checkClientTrusted(chain, authType);
/*    */     } catch (CertificateException excep) {
/* 51 */       this.listener.securityException(chain, authType, excep);
/*    */     }
/*    */   }
/*    */ 
/*    */   public X509Certificate[] getAcceptedIssuers()
/*    */   {
/* 57 */     return this.sunX509TrustManager.getAcceptedIssuers();
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\dds2-common-2.3.77.jar
 * Qualified Name:     com.dukascopy.transport.common.mina.ssl.DDSTrustManager
 * JD-Core Version:    0.6.0
 */