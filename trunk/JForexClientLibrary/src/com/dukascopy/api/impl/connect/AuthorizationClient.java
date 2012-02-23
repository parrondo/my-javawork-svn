/*     */ package com.dukascopy.api.impl.connect;
/*     */ 
/*     */ import com.dukascopy.transport.util.Base64;
/*     */ import java.awt.image.BufferedImage;
/*     */ import java.io.BufferedReader;
/*     */ import java.io.ByteArrayInputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.InputStreamReader;
/*     */ import java.io.ObjectInputStream;
/*     */ import java.io.UnsupportedEncodingException;
/*     */ import java.net.MalformedURLException;
/*     */ import java.net.URL;
/*     */ import java.net.URLConnection;
/*     */ import java.net.URLEncoder;
/*     */ import java.security.MessageDigest;
/*     */ import java.security.NoSuchAlgorithmException;
/*     */ import java.util.Collection;
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
/*     */ import java.util.Properties;
/*     */ import java.util.regex.Matcher;
/*     */ import java.util.regex.Pattern;
/*     */ import javax.imageio.ImageIO;
/*     */ import javax.net.ssl.HostnameVerifier;
/*     */ import javax.net.ssl.HttpsURLConnection;
/*     */ import javax.net.ssl.SSLSession;
/*     */ import org.slf4j.Logger;
/*     */ import org.slf4j.LoggerFactory;
/*     */ 
/*     */ public class AuthorizationClient
/*     */ {
/*  32 */   private static final Logger LOGGER = LoggerFactory.getLogger(AuthorizationClient.class);
/*     */   private static final String SSL_IGNORE_ERRORS = "auth.ssl.ignore.errors";
/*     */   private static final String MESSAGE_DIGEST_ENCODING = "iso-8859-1";
/*     */   private static final String MESSAGE_DIGEST_ALGORITHM = "SHA-1";
/*     */   private static final String LOGIN_PARAM = "appello";
/*     */   private static final String PASSWORD_PARAM = "specialis";
/*     */   private static final String CAPTCHA_PARAM = "verbum_id";
/*     */   private static final String PIN_PARAM = "sententia";
/*     */   private static final String CAPTCHA_HEADER = "X-CaptchaID";
/*     */   private static final String VERSION_PARAM = "versio";
/*     */   private static final String INSTANCE_ID_PARAM = "sermo";
/*     */   private static final String TYPE_PARAM = "typus";
/*     */   private static final String STNGS_PARAM = "stngs";
/*     */   private static final String REGION_PARAM = "region";
/*     */   private static final String API_MUNUS = "&munus=api&";
/*     */   private static final String RELOGIN_MUNUS = "&munus=relogin&";
/*     */   private static final String DATA_FEED_MUNUS = "&munus=datafeed&";
/*     */   private static final String SETTINGS_MUNUS = "&munus=stngs&";
/*     */   private static final String AUTH_CONTEXT = "/auth?typus=0&munus=api&";
/*     */   private static final String RELOGIN_AUTH_CONTEXT = "/auth?typus=0&munus=relogin&";
/*     */   private static final String FEED_AUTH_CONTEXT = "/auth?typus=0&munus=datafeed&";
/*     */   private static final String SETTINGS_CONTEXT = "/auth?typus=0&munus=stngs&";
/*     */   public static final String TICKET = "licentio";
/*  62 */   public static final Pattern RESULT_PATTERN = Pattern.compile("([\\S]*)@([\\p{XDigit}]{64}+)");
/*     */   public static final int URL_GROUP = 1;
/*     */   public static final int TICKET_GROUP = 2;
/*     */   private final AuthorizationConfigurationPool configurationPool;
/*     */   private final String version;
/*     */   private Throwable error;
/*     */   private static AuthorizationClient authClient;
/*     */ 
/*     */   public static AuthorizationClient getInstance(Collection<String> authServerUrls, String version)
/*     */   {
/*  73 */     if (authClient == null)
/*     */       try {
/*  75 */         authClient = new AuthorizationClient(authServerUrls, version);
/*     */       } catch (MalformedURLException e) {
/*  77 */         LOGGER.error(e.getMessage(), e);
/*     */       }
/*     */     else {
/*     */       try {
/*  81 */         authClient.updateConfigurationPool(authServerUrls);
/*     */       } catch (MalformedURLException e) {
/*  83 */         LOGGER.error(e.getMessage(), e);
/*     */       }
/*     */     }
/*  86 */     return authClient;
/*     */   }
/*     */ 
/*     */   private AuthorizationClient(Collection<String> authServerUrls, String version) throws MalformedURLException {
/*  90 */     this.configurationPool = new AuthorizationConfigurationPool();
/*  91 */     for (String authServerUrl : authServerUrls) {
/*  92 */       this.configurationPool.add(authServerUrl);
/*     */     }
/*  94 */     this.version = version;
/*     */   }
/*     */ 
/*     */   private void updateConfigurationPool(Collection<String> authServerUrls) throws MalformedURLException {
/*  98 */     this.configurationPool.clear();
/*  99 */     for (String authServerUrl : authServerUrls)
/* 100 */       this.configurationPool.add(authServerUrl);
/*     */   }
/*     */ 
/*     */   public Map<String, BufferedImage> getImageCaptcha() throws IOException
/*     */   {
/* 105 */     HashMap output = null;
/* 106 */     setError(null);
/* 107 */     int retryCount = 0;
/* 108 */     while ((retryCount < this.configurationPool.size()) && (output == null)) {
/* 109 */       String formedUrl = new StringBuilder().append(getBaseUrl()).append("/captcha").toString();
/* 110 */       LOGGER.info(">> [{}]", formedUrl);
/* 111 */       URL captchaUrl = new URL(formedUrl);
/* 112 */       InputStream inputStream = null;
/*     */       try {
/* 114 */         URLConnection connection = getConnection(captchaUrl);
/* 115 */         String captchaId = connection.getHeaderField("X-CaptchaID");
/* 116 */         LOGGER.debug("<< [{} : {}]", "X-CaptchaID", captchaId);
/* 117 */         inputStream = connection.getInputStream();
/* 118 */         BufferedImage captchaImage = ImageIO.read(inputStream);
/* 119 */         output = new HashMap();
/* 120 */         output.put(captchaId, captchaImage);
/*     */       } catch (IOException e) {
/* 122 */         this.configurationPool.markLastUsedAsBad();
/* 123 */         LOGGER.error(e.getMessage(), e);
/* 124 */         setError(e);
/*     */       } finally {
/* 126 */         if (inputStream != null) {
/* 127 */           inputStream.close();
/*     */         }
/*     */       }
/* 130 */       retryCount++;
/*     */     }
/* 132 */     return output;
/*     */   }
/*     */ 
/*     */   public String getUrlAndTicket(String login, String password, String instanceId) throws IOException, NoSuchAlgorithmException {
/* 136 */     return getUrlAndTicket(login, password, instanceId, true);
/*     */   }
/*     */ 
/*     */   public String getUrlAndTicket(String login, String password, String instanceId, boolean encodePassword) throws IOException, NoSuchAlgorithmException
/*     */   {
/* 141 */     String result = null;
/* 142 */     int retryCount = 0;
/* 143 */     setError(null);
/* 144 */     while ((retryCount < this.configurationPool.size()) && (result == null)) {
/*     */       try {
/* 146 */         String serverRegion = System.getProperty("server.region");
/* 147 */         String formedUrl = new StringBuilder().append(getBaseUrl()).append("/auth?typus=0&munus=api&").append("appello").append("=").append(URLEncoder.encode(login, "UTF-8")).append("&").append("specialis").append("=").append(URLEncoder.encode(encodePassword ? encodeAll(password, "", login) : password, "UTF-8")).append("&").append("versio").append("=").append(URLEncoder.encode(this.version, "UTF-8")).append("&").append("sermo").append("=").append(instanceId).append(serverRegion == null ? "" : new StringBuilder().append("&region=").append(serverRegion).toString()).toString();
/*     */ 
/* 153 */         URL url = new URL(formedUrl);
/* 154 */         result = readFirstLineOfResponse(url);
/*     */       } catch (IOException e) {
/* 156 */         this.configurationPool.markLastUsedAsBad();
/* 157 */         LOGGER.error(e.getMessage(), e);
/* 158 */         setError(e);
/*     */       }
/* 160 */       retryCount++;
/*     */     }
/* 162 */     return result;
/*     */   }
/*     */ 
/*     */   public String getUrlAndTicket(String login, String password, String captchaId, String pin, String instanceId) throws IOException, NoSuchAlgorithmException {
/* 166 */     String result = null;
/* 167 */     int retryCount = 0;
/* 168 */     setError(null);
/* 169 */     while ((retryCount < this.configurationPool.size()) && (result == null)) {
/*     */       try {
/* 171 */         String serverRegion = System.getProperty("server.region");
/* 172 */         String urlString = new StringBuilder().append(getBaseUrl()).append("/auth?typus=0&munus=api&").append("appello").append("=").append(URLEncoder.encode(login, "UTF-8")).append("&").append("specialis").append("=").append(URLEncoder.encode(encodeAll(password, captchaId, login), "UTF-8")).append("&").append("versio").append("=").append(URLEncoder.encode(this.version, "UTF-8")).append("&").append("sermo").append("=").append(instanceId).append(serverRegion == null ? "" : new StringBuilder().append("&region=").append(serverRegion).toString()).toString();
/*     */ 
/* 178 */         if (captchaId != null) {
/* 179 */           urlString = new StringBuilder().append(urlString).append("&verbum_id=").append(captchaId).toString();
/*     */         }
/* 181 */         if (pin != null) {
/* 182 */           urlString = new StringBuilder().append(urlString).append("&sententia=").append(URLEncoder.encode(pin, "iso-8859-1")).toString();
/*     */         }
/* 184 */         URL url = new URL(urlString);
/* 185 */         result = readFirstLineOfResponse(url);
/*     */       } catch (IOException e) {
/* 187 */         this.configurationPool.markLastUsedAsBad();
/* 188 */         LOGGER.error(e.getMessage(), e);
/* 189 */         setError(e);
/*     */       }
/* 191 */       retryCount++;
/*     */     }
/* 193 */     return result;
/*     */   }
/*     */ 
/*     */   public String getNewTicketAfterReconnect(String login, String oldTicket, String instanceId) throws IOException, NoSuchAlgorithmException {
/* 197 */     String result = null;
/* 198 */     int retryCount = 0;
/* 199 */     setError(null);
/* 200 */     while ((retryCount < this.configurationPool.size()) && (result == null)) {
/*     */       try {
/* 202 */         String formedUrl = new StringBuilder().append(getBaseUrl()).append("/auth?typus=0&munus=relogin&").append("appello").append("=").append(URLEncoder.encode(login, "UTF-8")).append("&").append("licentio").append("=").append(oldTicket).append("&").append("sermo").append("=").append(instanceId).toString();
/*     */ 
/* 207 */         URL url = new URL(formedUrl);
/* 208 */         result = readFirstLineOfResponse(url);
/*     */       } catch (IOException e) {
/* 210 */         this.configurationPool.markLastUsedAsBad();
/* 211 */         LOGGER.error(e.getMessage(), e);
/* 212 */         setError(e);
/*     */       }
/* 214 */       retryCount++;
/*     */     }
/* 216 */     return result;
/*     */   }
/*     */ 
/*     */   public String getFeedUrlAndTicket(String login, String platformTicket, String instanceId) throws IOException, NoSuchAlgorithmException {
/* 220 */     String result = null;
/* 221 */     int retryCount = 0;
/* 222 */     while ((retryCount < 5) && (result == null)) {
/*     */       try {
/* 224 */         URL authorizationServerUrl = getBaseUrl();
/* 225 */         String url = new StringBuilder().append(authorizationServerUrl).append("/auth?typus=0&munus=datafeed&").append("appello").append("=").append(URLEncoder.encode(login, "UTF-8")).append("&").append("licentio").append("=").append(platformTicket).append("&").append("sermo").append("=").append(instanceId).toString();
/*     */ 
/* 229 */         LOGGER.debug(new StringBuilder().append("Authorizing for data feed server [").append(authorizationServerUrl).append("] attempt [").append(retryCount).append("]...").toString());
/* 230 */         result = readFirstLineOfResponse(new URL(url));
/* 231 */         if ((result == null) || (result.startsWith("-3")) || (result.startsWith("-2")) || (result.startsWith("-500"))) {
/* 232 */           LOGGER.error("Authorization for data feed server failed, system error");
/* 233 */           result = null;
/* 234 */           this.configurationPool.markLastUsedAsBad();
/*     */           try {
/* 236 */             Thread.sleep(3000L);
/*     */           } catch (InterruptedException e) {
/* 238 */             LOGGER.error(e.getMessage(), e);
/*     */           }
/* 240 */         } else if (result.startsWith("-1")) {
/* 241 */           LOGGER.error("Authorization for data feed server failed, access not authorized");
/* 242 */           result = null;
/*     */           try {
/* 244 */             Thread.sleep(3000L);
/*     */           } catch (InterruptedException e) {
/* 246 */             LOGGER.error(e.getMessage(), e);
/*     */           }
/*     */         }
/*     */       } catch (IOException e) {
/* 250 */         LOGGER.error(e.getMessage(), e);
/*     */         try {
/* 252 */           Thread.sleep(3000L);
/*     */         } catch (InterruptedException ex) {
/* 254 */           LOGGER.error(ex.getMessage(), ex);
/*     */         }
/*     */       }
/* 257 */       retryCount++;
/*     */     }
/* 259 */     if (result != null) {
/* 260 */       Matcher matcher = RESULT_PATTERN.matcher(result);
/* 261 */       if (!matcher.matches()) {
/* 262 */         LOGGER.error(new StringBuilder().append("Authorization procedure returned unexpected result [").append(result).append("]").toString());
/* 263 */         result = null;
/*     */       } else {
/* 265 */         String feedDataServerURL = matcher.group(1);
/* 266 */         LOGGER.debug(new StringBuilder().append("Authorization for data feed server successful, server url [").append(feedDataServerURL).append("]").toString());
/*     */       }
/*     */     }
/* 269 */     return result;
/*     */   }
/*     */ 
/*     */   public Properties getAllProperties(String login, String ticket, String sessionId) throws IOException, NoSuchAlgorithmException {
/* 273 */     Properties result = null;
/* 274 */     int retryCount = 0;
/* 275 */     setError(null);
/* 276 */     while ((retryCount < this.configurationPool.size()) && (result == null)) {
/*     */       try {
/* 278 */         String formedUrl = new StringBuilder().append(getBaseUrl()).append("/auth?typus=0&munus=stngs&").append("appello").append("=").append(URLEncoder.encode(login, "UTF-8")).append("&").append("licentio").append("=").append(ticket).append("&").append("sermo").append("=").append(sessionId).append("&").append("stngs").append("=1").toString();
/*     */ 
/* 284 */         URL url = new URL(formedUrl);
/*     */ 
/* 286 */         result = (Properties)getRetrievePropsFromResponse(url);
/* 287 */         if (result == null)
/* 288 */           this.configurationPool.markLastUsedAsBad();
/*     */       }
/*     */       catch (IOException e) {
/* 291 */         this.configurationPool.markLastUsedAsBad();
/* 292 */         LOGGER.error(e.getMessage(), e);
/* 293 */         setError(e);
/*     */       }
/* 295 */       retryCount++;
/*     */     }
/* 297 */     return result;
/*     */   }
/*     */ 
/*     */   private static String readFirstLineOfResponse(URL url)
/*     */     throws IOException
/*     */   {
/* 303 */     LOGGER.debug(">> [{}]", url);
/* 304 */     String result = null;
/* 305 */     BufferedReader reader = null;
/*     */     try {
/* 307 */       URLConnection connection = getConnection(url);
/* 308 */       reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
/* 309 */       result = reader.readLine();
/*     */     } finally {
/* 311 */       if (reader != null) {
/* 312 */         reader.close();
/*     */       }
/*     */     }
/* 315 */     LOGGER.debug("<< [{}]", result);
/* 316 */     return result;
/*     */   }
/*     */ 
/*     */   private static String readResponse(URL url) throws IOException {
/* 320 */     String result = null;
/* 321 */     BufferedReader reader = null;
/*     */     try { URLConnection connection = getConnection(url);
/* 324 */       reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
/*     */ 
/* 327 */       StringBuffer sb = new StringBuffer();
/*     */       String tempRes;
/*     */       do { tempRes = reader.readLine();
/* 331 */         sb.append(tempRes); }
/* 332 */       while (tempRes != null);
/*     */ 
/* 334 */       result = sb.toString();
/*     */     } finally {
/* 336 */       if (reader != null) {
/* 337 */         reader.close();
/*     */       }
/*     */     }
/*     */ 
/* 341 */     return result;
/*     */   }
/*     */ 
/*     */   private static Object getRetrievePropsFromResponse(URL url) throws IOException {
/* 345 */     LOGGER.info(">> [{}]", url);
/* 346 */     Object result = null;
/*     */ 
/* 348 */     String encodedString = readResponse(url);
/*     */ 
/* 350 */     byte[] decodedBytes = Base64.decode(new String(encodedString.getBytes()));
/* 351 */     if (decodedBytes != null) {
/*     */       try {
/* 353 */         ObjectInputStream inputStream = new ObjectInputStream(new ByteArrayInputStream(decodedBytes));
/* 354 */         result = inputStream.readObject();
/*     */       } catch (IOException e) {
/* 356 */         LOGGER.error(e.getMessage(), e);
/*     */       } catch (ClassNotFoundException e) {
/* 358 */         LOGGER.error(e.getMessage(), e);
/*     */       }
/*     */     }
/*     */ 
/* 362 */     LOGGER.warn("<< [{}]", result);
/* 363 */     return result;
/*     */   }
/*     */ 
/*     */   private static URLConnection getConnection(URL url) throws IOException {
/* 367 */     URLConnection connection = url.openConnection();
/* 368 */     if (((connection instanceof HttpsURLConnection)) && 
/* 369 */       (Boolean.getBoolean("auth.ssl.ignore.errors"))) {
/* 370 */       LOGGER.debug("Ignoring SSL errors");
/* 371 */       ((HttpsURLConnection)connection).setHostnameVerifier(new DummyHostNameVerifier(null));
/*     */     }
/*     */ 
/* 374 */     return connection;
/*     */   }
/*     */ 
/*     */   private static String convertToHex(byte[] data) {
/* 378 */     StringBuffer buf = new StringBuffer();
/* 379 */     for (byte aData : data) {
/* 380 */       int halfbyte = aData >>> 4 & 0xF;
/* 381 */       int twoHalfs = 0;
/*     */       do {
/* 383 */         if ((0 <= halfbyte) && (halfbyte <= 9))
/* 384 */           buf.append((char)(48 + halfbyte));
/*     */         else {
/* 386 */           buf.append((char)(97 + (halfbyte - 10)));
/*     */         }
/* 388 */         halfbyte = aData & 0xF;
/* 389 */       }while (twoHalfs++ < 1);
/*     */     }
/* 391 */     return buf.toString();
/*     */   }
/*     */ 
/*     */   private static String encodeAll(String password, String capthaId, String login)
/*     */     throws NoSuchAlgorithmException, UnsupportedEncodingException
/*     */   {
/*     */     String toCode;
/*     */     String toCode;
/* 396 */     if (capthaId != null)
/* 397 */       toCode = new StringBuilder().append(encodeString(password)).append(capthaId).append(login).toString();
/*     */     else {
/* 399 */       toCode = new StringBuilder().append(encodeString(password)).append(login).toString();
/*     */     }
/*     */ 
/* 402 */     return encodeString(toCode);
/*     */   }
/*     */ 
/*     */   public static String encodeString(String string) throws NoSuchAlgorithmException, UnsupportedEncodingException
/*     */   {
/* 407 */     MessageDigest md = MessageDigest.getInstance("SHA-1");
/* 408 */     md.update(string.getBytes("iso-8859-1"), 0, string.length());
/* 409 */     byte[] sha1hash = md.digest();
/* 410 */     return convertToHex(sha1hash).toUpperCase();
/*     */   }
/*     */ 
/*     */   public URL getBaseUrl() {
/* 414 */     return this.configurationPool.get();
/*     */   }
/*     */ 
/*     */   public String getLoginUrl() {
/* 418 */     return this.configurationPool.get().toString();
/*     */   }
/*     */ 
/*     */   private void setError(Throwable error) {
/* 422 */     this.error = error;
/*     */   }
/*     */ 
/*     */   public Throwable getError() {
/* 426 */     return this.error;
/*     */   }
/*     */ 
/*     */   private static class DummyHostNameVerifier
/*     */     implements HostnameVerifier
/*     */   {
/*     */     public boolean verify(String hostname, SSLSession session)
/*     */     {
/* 434 */       AuthorizationClient.LOGGER.debug("Verify : {} @ {}", hostname, session);
/* 435 */       return true;
/*     */     }
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.api.impl.connect.AuthorizationClient
 * JD-Core Version:    0.6.0
 */