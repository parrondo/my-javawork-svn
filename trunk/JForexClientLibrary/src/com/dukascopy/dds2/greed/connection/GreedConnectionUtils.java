/*     */ package com.dukascopy.dds2.greed.connection;
/*     */ 
/*     */ import com.dukascopy.api.impl.connect.AuthorizationClient;
/*     */ import com.dukascopy.dds2.greed.GreedContext;
/*     */ import com.dukascopy.dds2.greed.actions.AuthFailedAction;
/*     */ import com.dukascopy.dds2.greed.gui.HostResolver;
/*     */ import com.dukascopy.dds2.greed.gui.LoginForm;
/*     */ import java.awt.image.BufferedImage;
/*     */ import java.io.IOException;
/*     */ import java.net.MalformedURLException;
/*     */ import java.net.URL;
/*     */ import java.security.NoSuchAlgorithmException;
/*     */ import java.util.Iterator;
/*     */ import java.util.Map;
/*     */ import java.util.Properties;
/*     */ import java.util.Set;
/*     */ import java.util.regex.Matcher;
/*     */ import java.util.regex.Pattern;
/*     */ import org.slf4j.Logger;
/*     */ import org.slf4j.LoggerFactory;
/*     */ 
/*     */ public class GreedConnectionUtils
/*     */ {
/*     */   private static final Logger LOGGER;
/*     */ 
/*     */   public static String getTicketAndAPIUrl(Map<String, BufferedImage> captchaMap, String pin)
/*     */   {
/*  35 */     String accountName = (String)GreedContext.getConfig("account_name");
/*  36 */     String instanceId = (String)GreedContext.getConfig("SESSION_ID");
/*  37 */     String password = (String)GreedContext.getConfig(" ");
/*     */     String responseParts;
/*     */     try
/*     */     {
/*  45 */       String captchaId = null;
/*     */ 
/*  47 */       if ((captchaMap != null) && (captchaMap.size() > 0)) {
/*  48 */         assert ((pin != null) && (pin.trim().length() > 0));
/*  49 */         captchaId = (String)captchaMap.keySet().iterator().next();
/*     */       }
/*     */ 
/*  52 */       AuthorizationClient ac = GreedContext.AUTHORIZATION_CLIENT;
/*     */       String responseParts;
/*  54 */       if ((captchaId != null) && (!captchaId.trim().isEmpty()) && (pin != null) && (!pin.trim().isEmpty()))
/*     */       {
/*  57 */         responseParts = ac.getUrlAndTicket(accountName, password, captchaId, pin, instanceId);
/*     */       }
/*  59 */       else responseParts = ac.getUrlAndTicket(accountName, password, instanceId);
/*     */ 
/*  62 */       if ((ac.getError() != null) && ((ac.getError() instanceof NoSuchAlgorithmException)))
/*  63 */         return GreedTransportClient.TRANSPORT_RC.ERROR_INIT.toString();
/*  64 */       if ((ac.getError() != null) && ((ac.getError() instanceof MalformedURLException)))
/*  65 */         return GreedTransportClient.TRANSPORT_RC.ERROR_BAD_URL.toString();
/*  66 */       if ((ac.getError() != null) && ((ac.getError() instanceof IOException)))
/*  67 */         return GreedTransportClient.TRANSPORT_RC.ERROR_IO.toString();
/*     */     } catch (NoSuchAlgorithmException e) {
/*  69 */       LOGGER.error(e.getMessage(), e);
/*  70 */       return GreedTransportClient.TRANSPORT_RC.ERROR_INIT.toString();
/*     */     } catch (MalformedURLException e) {
/*  72 */       LOGGER.error("Bad URL: " + e.getMessage(), e);
/*  73 */       return GreedTransportClient.TRANSPORT_RC.ERROR_BAD_URL.toString();
/*     */     } catch (IOException e) {
/*  75 */       LOGGER.error("Service unavailable: " + e.getMessage(), e);
/*  76 */       return GreedTransportClient.TRANSPORT_RC.ERROR_IO.toString();
/*     */     }
/*     */ 
/*  79 */     return responseParts;
/*     */   }
/*     */ 
/*     */   public static String getTicketAndAPIUrl()
/*     */   {
/*  84 */     return getTicketAndAPIUrl(null, null);
/*     */   }
/*     */ 
/*     */   public static GreedTransportClient.TRANSPORT_RC validateResponse(String httpResponse)
/*     */   {
/*  89 */     GreedTransportClient.TRANSPORT_RC response = GreedTransportClient.TRANSPORT_RC.OK;
/*     */ 
/*  91 */     if (httpResponse == null) {
/*  92 */       response = GreedTransportClient.TRANSPORT_RC.ERROR_INIT;
/*  93 */     } else if ("-1".equals(httpResponse)) {
/*  94 */       response = GreedTransportClient.TRANSPORT_RC.ERROR_AUTH;
/*  95 */     } else if ("-2".equals(httpResponse)) {
/*  96 */       response = GreedTransportClient.TRANSPORT_RC.WRONG_VERSION;
/*  97 */     } else if ("-3".equals(httpResponse)) {
/*  98 */       response = GreedTransportClient.TRANSPORT_RC.NO_SERVERS;
/*  99 */     } else if ("-500".equals(httpResponse)) {
/* 100 */       response = GreedTransportClient.TRANSPORT_RC.SYSTEM_ERROR;
/* 101 */     } else if (GreedTransportClient.TRANSPORT_RC.ERROR_INIT.toString().equals(httpResponse)) {
/* 102 */       response = GreedTransportClient.TRANSPORT_RC.ERROR_INIT;
/* 103 */     } else if (GreedTransportClient.TRANSPORT_RC.ERROR_BAD_URL.toString().equals(httpResponse)) {
/* 104 */       response = GreedTransportClient.TRANSPORT_RC.ERROR_BAD_URL;
/* 105 */     } else if (GreedTransportClient.TRANSPORT_RC.ERROR_IO.toString().equals(httpResponse)) {
/* 106 */       response = GreedTransportClient.TRANSPORT_RC.ERROR_IO;
/*     */     } else {
/* 108 */       Matcher matcher = AuthorizationClient.RESULT_PATTERN.matcher(httpResponse);
/* 109 */       if (!matcher.matches()) {
/* 110 */         LOGGER.error("Authorization procedure returned unexpected result [" + httpResponse + "]");
/* 111 */         response = GreedTransportClient.TRANSPORT_RC.ERROR_IO;
/*     */       }
/*     */     }
/*     */ 
/* 115 */     return response;
/*     */   }
/*     */ 
/*     */   public static void wrongAuth(GreedTransportClient.TRANSPORT_RC response)
/*     */   {
/* 120 */     if ((LoginForm.getInstance() != null) && (!LoginForm.getInstance().isVisible())) {
/* 121 */       LoginForm.getInstance().display();
/*     */     }
/* 123 */     GreedContext.publishEvent(new AuthFailedAction(response));
/*     */   }
/*     */ 
/*     */   public static void resolveDnsAheadOfTheTime()
/*     */   {
/*     */     try
/*     */     {
/* 130 */       Properties properties = (Properties)GreedContext.get("properties");
/* 131 */       URL url = new URL(properties.getProperty("services1.url"));
/* 132 */       HostResolver hr = new HostResolver(url.getHost());
/* 133 */       hr.start();
/*     */     }
/*     */     catch (Exception e) {
/* 136 */       LOGGER.warn("GET to services1.url failed:" + e.getMessage());
/*     */     }
/*     */   }
/*     */ 
/*     */   static
/*     */   {
/*  23 */     LOGGER = LoggerFactory.getLogger(GreedConnectionUtils.class);
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.connection.GreedConnectionUtils
 * JD-Core Version:    0.6.0
 */