/*     */ package com.dukascopy.transport.common.mina.ssl;
/*     */ 
/*     */ import java.io.BufferedReader;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.InputStreamReader;
/*     */ import java.io.PrintStream;
/*     */ import java.security.GeneralSecurityException;
/*     */ import java.security.KeyStore;
/*     */ import java.security.Security;
/*     */ import javax.net.ssl.KeyManagerFactory;
/*     */ import javax.net.ssl.SSLContext;
/*     */ import javax.net.ssl.TrustManagerFactory;
/*     */ import javax.net.ssl.X509TrustManager;
/*     */ 
/*     */ public class SSLContextFactory
/*     */ {
/*     */   private static final String PROTOCOL = "SSL";
/*     */   private static final String KEY_MANAGER_FACTORY_ALGORITHM;
/*     */   private static final String KEYSTORE = "dukascopy.cert";
/*     */   private static char[] PW;
/*     */   private static SSLContext serverInstance;
/*     */   private static SSLContext clientInstance;
/*     */ 
/*     */   public static SSLContext getInstance(boolean server, ClientSSLContextListener listener, String targetHost)
/*     */     throws GeneralSecurityException
/*     */   {
/*  63 */     SSLContext retInstance = null;
/*  64 */     if (server) {
/*  65 */       if (serverInstance == null) {
/*  66 */         synchronized (SSLContextFactory.class) {
/*  67 */           if (serverInstance == null) {
/*     */             try {
/*  69 */               serverInstance = createServerSSLContext();
/*     */             } catch (Exception ioe) {
/*  71 */               throw new GeneralSecurityException("Can't create Server SSLContext:" + ioe);
/*     */             } catch (Throwable ioe) {
/*  73 */               throw new RuntimeException(ioe);
/*     */             }
/*     */           }
/*     */         }
/*     */       }
/*  78 */       retInstance = serverInstance;
/*     */     } else {
/*  80 */       if (clientInstance == null) {
/*  81 */         synchronized (SSLContextFactory.class) {
/*  82 */           if (clientInstance == null) {
/*  83 */             clientInstance = createClientSSLContext(listener, targetHost);
/*     */           }
/*     */         }
/*     */       }
/*  87 */       retInstance = clientInstance;
/*     */     }
/*  89 */     return retInstance;
/*     */   }
/*     */ 
/*     */   private static SSLContext createServerSSLContext() throws GeneralSecurityException, IOException
/*     */   {
/*  94 */     KeyStore ks = KeyStore.getInstance("JKS");
/*     */ 
/*  96 */     readStorePass();
/*     */ 
/*  98 */     InputStream in = null;
/*     */     try {
/* 100 */       System.out.println("######################## Reading SSL certificat");
/* 101 */       in = SSLContextFactory.class.getClassLoader().getResourceAsStream("dukascopy.cert");
/* 102 */       if (in == null) {
/* 103 */         throw new RuntimeException("SSL RETIFICATE NOT FOUND");
/*     */       }
/* 105 */       ks.load(in, PW);
/*     */     } catch (Throwable e) {
/* 107 */       e.printStackTrace();
/* 108 */       throw new RuntimeException(e);
/*     */     } finally {
/* 110 */       if (in != null) {
/*     */         try {
/* 112 */           in.close();
/*     */         }
/*     */         catch (IOException exception)
/*     */         {
/*     */         }
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 121 */     KeyManagerFactory kmf = KeyManagerFactory.getInstance(KEY_MANAGER_FACTORY_ALGORITHM);
/* 122 */     kmf.init(ks, PW);
/*     */ 
/* 125 */     SSLContext sslContext = SSLContext.getInstance("SSL");
/* 126 */     sslContext.init(kmf.getKeyManagers(), null, null);
/* 127 */     return sslContext;
/*     */   }
/*     */ 
/*     */   private static void readStorePass() throws IOException {
/* 131 */     if (System.getProperty("keystore.passfile") != null) {
/* 132 */       System.out.println("Found keystore pass file in VM propeties: " + System.getProperty("keystore.passfile"));
/* 133 */       InputStream storePass = null;
/* 134 */       storePass = SSLContextFactory.class.getClassLoader().getResourceAsStream(System.getProperty("keystore.passfile"));
/* 135 */       if (storePass != null) {
/* 136 */         BufferedReader br = new BufferedReader(new InputStreamReader(storePass));
/* 137 */         String line = br.readLine();
/* 138 */         while (line != null) {
/* 139 */           line = line.trim();
/* 140 */           if (line.startsWith("#")) {
/* 141 */             line = br.readLine();
/* 142 */             continue;
/*     */           }
/* 144 */           if (line.length() < 1) {
/* 145 */             line = br.readLine();
/* 146 */             continue;
/*     */           }
/* 148 */           PW = line.toCharArray();
/* 149 */           System.out.println("Key store pass readed !!! ");
/*     */         }
/*     */ 
/* 152 */         storePass.close();
/*     */       } else {
/* 154 */         System.out.println("Store pass file not found: " + System.getProperty("keystore.passfile"));
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   private static SSLContext createClientSSLContext(ClientSSLContextListener listener, String targetHost) throws GeneralSecurityException {
/* 160 */     SSLContext context = SSLContext.getInstance("SSL");
/* 161 */     TrustManagerFactory factory = TrustManagerFactory.getInstance(KEY_MANAGER_FACTORY_ALGORITHM);
/* 162 */     KeyStore ks = null;
/* 163 */     factory.init(ks);
/* 164 */     X509TrustManager manager = (X509TrustManager)factory.getTrustManagers()[0];
/* 165 */     DDSTrustManager mg = new DDSTrustManager(listener, manager);
/* 166 */     mg.setHostName(targetHost);
/* 167 */     context.init(null, new DDSTrustManager[] { mg }, null);
/* 168 */     return context;
/*     */   }
/*     */ 
/*     */   static
/*     */   {
/*  27 */     String algorithm = Security.getProperty("ssl.KeyManagerFactory.algorithm");
/*  28 */     if (algorithm == null) {
/*  29 */       algorithm = "SunX509";
/*     */     }
/*     */ 
/*  32 */     KEY_MANAGER_FACTORY_ALGORITHM = algorithm;
/*     */ 
/*  49 */     PW = new char[] { 'd', 'u', 'k', 'a', 's', 'p', 'w' };
/*     */ 
/*  51 */     serverInstance = null;
/*     */ 
/*  53 */     clientInstance = null;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\dds2-common-2.3.77.jar
 * Qualified Name:     com.dukascopy.transport.common.mina.ssl.SSLContextFactory
 * JD-Core Version:    0.6.0
 */