/*     */ package com.dukascopy.api.impl.connect;
/*     */ 
/*     */ import com.dukascopy.charts.data.datacache.FeedDataProvider;
/*     */ import java.io.DataOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.UnsupportedEncodingException;
/*     */ import java.net.HttpURLConnection;
/*     */ import java.net.URL;
/*     */ import java.net.URLEncoder;
/*     */ import java.security.MessageDigest;
/*     */ import java.security.NoSuchAlgorithmException;
/*     */ import java.util.Calendar;
/*     */ import java.util.concurrent.TimeUnit;
/*     */ import org.json.JSONArray;
/*     */ import org.json.JSONObject;
/*     */ import org.slf4j.Logger;
/*     */ import org.slf4j.LoggerFactory;
/*     */ 
/*     */ public class ActivityLogger extends Thread
/*     */ {
/*  30 */   private static final Logger LOGGER = LoggerFactory.getLogger(ActivityLogger.class);
/*     */   private static final String THREAD_NAME = "ActivityLogger";
/*  32 */   private static final long PERIOD = TimeUnit.SECONDS.toMillis(10L);
/*     */   private static final String DIGEST_ALGORITHM = "MD5";
/*     */   private static final String CHARSET = "ISO-8859-1";
/*     */   private static final String AUTH_REQUEST_FORMAT = "check=%1$s&%2$s=%3$s";
/*     */   private static final String LOGIN_PARAM_KEY = "login";
/*     */   private static final String LOGIN_ID_PARAM_KEY = "loginId";
/*     */   private static final String REQUEST_URL_FORMAT = "%1$s?%2$s&tzOffsetSeconds=%3$s";
/*     */   private static final String APPLICATION_X_WWW_FORM_URLENCODED = "application/x-www-form-urlencoded";
/*     */   private static final String CONTENT_TYPE_REQUEST_PROPERTY = "Content-Type";
/*     */   private static final String CONTENT_LENGTH_REQUEST_PROPERTY = "Content-Length";
/*     */   private static final String POST_REQUEST_METHOD = "POST";
/*     */   private static final String DATA_KEY = "data=";
/*     */   private static final String SECRETTO = "secretto";
/*     */   private static final String DATUM_TYPE = "datumType";
/*     */   private static final String DATUM_MESSAGE = "message";
/*     */   private static final String TIMESTAMP_KEY = "timestamp";
/*     */   private static final String CLIENT_TIME_KEY = "clientTime";
/*     */   private static final String MESSAGE_KEY = "message";
/*     */   private static ActivityLogger instance;
/*     */   private final String serviceUrl;
/*     */   private final long timeZoneOffset;
/*     */   private final String login;
/*     */   private final String loginId;
/*  60 */   private JSONArray jaForImmediateSend = new JSONArray();
/*     */   private boolean stop;
/*     */ 
/*     */   private ActivityLogger(String serviceUrl, String login, String loginId)
/*     */   {
/*  64 */     super("ActivityLogger");
/*     */ 
/*  66 */     this.serviceUrl = serviceUrl;
/*  67 */     this.login = login;
/*  68 */     this.loginId = loginId;
/*     */ 
/*  70 */     Calendar calendar = Calendar.getInstance();
/*  71 */     this.timeZoneOffset = (-(calendar.get(15) + calendar.get(16)));
/*     */ 
/*  73 */     Runtime.getRuntime().addShutdownHook(new Thread()
/*     */     {
/*     */       public void run() {
/*  76 */         ActivityLogger.this.flush();
/*     */       }
/*     */     });
/*  80 */     setDaemon(true);
/*  81 */     start();
/*     */   }
/*     */ 
/*     */   public static synchronized void init(String serviceUrl, String login) {
/*  85 */     init(serviceUrl, login, null);
/*     */   }
/*     */ 
/*     */   public static synchronized void init(String serviceUrl, String login, String loginId) {
/*  89 */     if (instance != null) {
/*  90 */       instance.stopLogger();
/*     */     }
/*  92 */     instance = new ActivityLogger(serviceUrl, login, loginId);
/*     */   }
/*     */ 
/*     */   public static synchronized ActivityLogger getInstance() {
/*  96 */     return instance;
/*     */   }
/*     */ 
/*     */   public void run()
/*     */   {
/* 101 */     while (!this.stop)
/*     */       try {
/* 103 */         Thread.sleep(PERIOD);
/* 104 */         sendLog();
/*     */       } catch (Exception e) {
/* 106 */         LOGGER.error(e.getMessage(), e);
/*     */       }
/*     */   }
/*     */ 
/*     */   public void flush()
/*     */   {
/*     */     try {
/* 113 */       sendLog();
/*     */     } catch (IOException e) {
/* 115 */       LOGGER.error(e.getMessage(), e);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void add(String message) {
/* 120 */     add(message, getTimeStamp());
/*     */   }
/*     */ 
/*     */   public void sendLog()
/*     */     throws IOException
/*     */   {
/*     */     String messageString;
/* 125 */     synchronized (this) {
/* 126 */       int size = this.jaForImmediateSend.length();
/* 127 */       if (0 == size) {
/* 128 */         return;
/*     */       }
/* 130 */       messageString = this.jaForImmediateSend.toString();
/* 131 */       this.jaForImmediateSend = new JSONArray();
/*     */     }
/*     */ 
/* 134 */     String jameson = "";
/*     */     try {
/* 136 */       jameson = URLEncoder.encode(messageString, "ISO-8859-1");
/*     */     } catch (UnsupportedEncodingException e) {
/* 138 */       LOGGER.error(e.getMessage(), e);
/*     */     }
/*     */ 
/* 141 */     if (LOGGER.isDebugEnabled()) {
/* 142 */       LOGGER.debug("jah : {}", messageString);
/*     */     }
/*     */ 
/* 145 */     String authorization = buildAuthorizationRequest(this.login, this.loginId);
/* 146 */     if (null == authorization) {
/* 147 */       authorization = "";
/*     */     }
/*     */ 
/* 150 */     String requestUrl = String.format("%1$s?%2$s&tzOffsetSeconds=%3$s", new Object[] { this.serviceUrl, authorization, Long.toString(TimeUnit.MILLISECONDS.toSeconds(this.timeZoneOffset)) });
/*     */ 
/* 156 */     doPostLog(requestUrl, jameson);
/*     */   }
/*     */ 
/*     */   private synchronized void stopLogger() {
/* 160 */     flush();
/* 161 */     this.stop = true;
/* 162 */     interrupt();
/*     */     try {
/* 164 */       join();
/*     */     } catch (InterruptedException e) {
/* 166 */       LOGGER.error(e.getMessage(), e);
/*     */     }
/*     */   }
/*     */ 
/*     */   private static String buildAuthorizationRequest(String login, String loginId) {
/* 171 */     String check = encode(login != null ? login : loginId);
/*     */ 
/* 173 */     if (check == null) {
/* 174 */       LOGGER.warn("Unable to generate [{}] hash", "MD5");
/* 175 */       return null;
/*     */     }
/*     */ 
/* 178 */     return String.format("check=%1$s&%2$s=%3$s", new Object[] { check, login != null ? "login" : "loginId", login != null ? login : loginId });
/*     */   }
/*     */ 
/*     */   private static String encode(String key)
/*     */   {
/*     */     MessageDigest digest;
/*     */     try
/*     */     {
/* 188 */       digest = MessageDigest.getInstance("MD5");
/*     */     } catch (NoSuchAlgorithmException nsae) {
/* 190 */       LOGGER.error(nsae.getMessage(), nsae);
/* 191 */       return null;
/*     */     }
/*     */ 
/* 194 */     char[] encodedChars = null;
/*     */     try {
/* 196 */       byte[] encodedBytes = digest.digest(new StringBuilder().append(key).append("secretto").toString().getBytes("ISO-8859-1"));
/* 197 */       encodedChars = new String(encodedBytes, "ISO-8859-1").toCharArray();
/*     */     } catch (UnsupportedEncodingException uee) {
/* 199 */       LOGGER.error(uee.getMessage(), uee);
/* 200 */       return null;
/*     */     }
/* 202 */     return new String(toHexString(encodedChars));
/*     */   }
/*     */ 
/*     */   private static char[] toHexString(char[] chars) {
/* 206 */     StringBuilder result = new StringBuilder("");
/* 207 */     for (char symbol : chars) {
/* 208 */       String charHexStr = Integer.toHexString(symbol);
/* 209 */       if (charHexStr.length() == 1) {
/* 210 */         charHexStr = new StringBuilder().append('0').append(charHexStr).toString();
/*     */       }
/* 212 */       result.append(charHexStr);
/*     */     }
/* 214 */     return result.toString().toCharArray();
/*     */   }
/*     */ 
/*     */   private static int doPostLog(String requestUrl, String message) throws IOException {
/* 218 */     URL url = new URL(requestUrl);
/*     */ 
/* 220 */     HttpURLConnection urlConnection = (HttpURLConnection)url.openConnection();
/* 221 */     urlConnection.setDoInput(true);
/* 222 */     urlConnection.setDoOutput(true);
/* 223 */     urlConnection.setUseCaches(false);
/* 224 */     urlConnection.setRequestMethod("POST");
/* 225 */     urlConnection.setRequestProperty("Content-Length", new StringBuilder().append("data=".length()).append(Integer.toString(message.getBytes().length)).toString());
/* 226 */     urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
/*     */ 
/* 228 */     DataOutputStream outStream = new DataOutputStream(urlConnection.getOutputStream());
/* 229 */     outStream.writeBytes("data=");
/* 230 */     outStream.writeBytes(message);
/* 231 */     outStream.flush();
/* 232 */     outStream.close();
/* 233 */     int responseCode = urlConnection.getResponseCode();
/* 234 */     urlConnection.disconnect();
/*     */ 
/* 236 */     if (LOGGER.isDebugEnabled()) {
/* 237 */       LOGGER.debug("Trade Log ({}) : {}", Integer.valueOf(responseCode), requestUrl);
/*     */     }
/*     */ 
/* 240 */     return responseCode;
/*     */   }
/*     */ 
/*     */   private synchronized void add(String message, String serverTimestamp) {
/* 244 */     JSONObject json = new JSONObject();
/* 245 */     json.put("datumType", "message");
/* 246 */     json.put("message", message);
/* 247 */     json.put("timestamp", serverTimestamp);
/* 248 */     json.put("clientTime", getClientTime());
/* 249 */     this.jaForImmediateSend.put(json);
/*     */   }
/*     */ 
/*     */   private String getClientTime() {
/* 253 */     return Long.toString(System.currentTimeMillis());
/*     */   }
/*     */ 
/*     */   private String getTimeStamp() {
/* 257 */     FeedDataProvider feedDataProvider = FeedDataProvider.getDefaultInstance();
/* 258 */     long timeStamp = feedDataProvider != null ? feedDataProvider.getEstimatedServerTime() : System.currentTimeMillis();
/* 259 */     return Long.toString(timeStamp);
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.api.impl.connect.ActivityLogger
 * JD-Core Version:    0.6.0
 */