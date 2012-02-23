/*    */ package com.dukascopy.dds2.greed.util;
/*    */ 
/*    */ import com.dukascopy.dds2.greed.GreedContext;
/*    */ import com.dukascopy.dds2.greed.gui.GuiUtilsAndConstants;
/*    */ import com.dukascopy.dds2.greed.model.AccountStatement;
/*    */ import com.dukascopy.transport.common.msg.request.AccountInfoMessage;
/*    */ import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;
/*    */ import java.awt.image.BufferedImage;
/*    */ import java.io.ByteArrayOutputStream;
/*    */ import java.io.DataOutputStream;
/*    */ import java.io.IOException;
/*    */ import java.net.HttpURLConnection;
/*    */ import java.net.URL;
/*    */ import java.util.HashMap;
/*    */ import java.util.Map;
/*    */ import javax.imageio.ImageIO;
/*    */ import org.slf4j.Logger;
/*    */ import org.slf4j.LoggerFactory;
/*    */ 
/*    */ public class ScreenSendingUtilities
/*    */ {
/* 24 */   private static final Logger LOGGER = LoggerFactory.getLogger(ScreenSendingUtilities.class);
/* 25 */   public static final Map<String, BufferedImage> IMAGE_CACHE = new HashMap();
/*    */ 
/*    */   public static void sendImage2PhpServer(String extSysId, String orderGroupId, String idList, int isOpening) throws IOException {
/* 28 */     if (!GreedContext.isContest()) return;
/*    */ 
/* 30 */     if (IMAGE_CACHE.get(extSysId) == null) return;
/*    */ 
/* 32 */     BufferedImage bufferedImage = (BufferedImage)IMAGE_CACHE.get(extSysId);
/* 33 */     IMAGE_CACHE.remove(extSysId);
/*    */ 
/* 35 */     String base64image = null;
/*    */     try {
/* 37 */       base64image = base64str(getByteArray(bufferedImage));
/*    */     } catch (Exception e) {
/* 39 */       LOGGER.warn(e.getMessage());
/* 40 */       return;
/*    */     }
/*    */ 
/* 43 */     URL url = new URL(GreedContext.getContestImageSendingURL());
/*    */ 
/* 45 */     HttpURLConnection httpConnection = (HttpURLConnection)url.openConnection();
/* 46 */     httpConnection.setRequestMethod("POST");
/*    */ 
/* 48 */     httpConnection.setDoOutput(true);
/* 49 */     httpConnection.setDoInput(true);
/*    */ 
/* 51 */     AccountInfoMessage accountState = ((AccountStatement)GreedContext.get("accountStatement")).getLastAccountState();
/* 52 */     String userId = null;
/* 53 */     if (accountState != null) {
/* 54 */       userId = accountState.getUserId();
/*    */     }
/*    */ 
/* 57 */     String imageParam = "&image=" + base64image;
/* 58 */     String orderGroupIdParam = "&orderGroupId=" + orderGroupId;
/* 59 */     String orderIdListParam = "&orderIds=" + idList;
/* 60 */     String isOpeningParam = "&isOpening=" + isOpening;
/* 61 */     userId = "&account_id=" + userId;
/*    */ 
/* 63 */     String login = (String)GreedContext.getConfig("account_name");
/* 64 */     String authorization = GuiUtilsAndConstants.buildAuthorizationRequest(login);
/* 65 */     if (null == authorization) {
/* 66 */       authorization = "";
/*    */     }
/*    */ 
/* 69 */     String request = authorization + orderGroupIdParam + orderIdListParam + userId + isOpeningParam + imageParam;
/*    */ 
/* 71 */     httpConnection.setRequestProperty("Content-Length", Integer.toString(request.length()));
/* 72 */     httpConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
/*    */ 
/* 74 */     httpConnection.connect();
/*    */     try
/*    */     {
/* 77 */       DataOutputStream outStream = new DataOutputStream(httpConnection.getOutputStream());
/* 78 */       outStream.writeBytes(request);
/* 79 */       outStream.flush();
/* 80 */       outStream.close();
/*    */     } catch (Exception e) {
/* 82 */       LOGGER.warn(e.getMessage());
/*    */     } finally {
/* 84 */       httpConnection.getResponseCode();
/* 85 */       httpConnection.disconnect();
/*    */     }
/*    */   }
/*    */ 
/*    */   private static ByteArrayOutputStream getByteArray(BufferedImage img) throws Exception
/*    */   {
/* 91 */     ByteArrayOutputStream baos = new ByteArrayOutputStream(1000);
/* 92 */     ImageIO.write(img, "jpg", baos);
/* 93 */     baos.flush();
/* 94 */     return baos;
/*    */   }
/*    */ 
/*    */   private static String base64str(ByteArrayOutputStream baos) {
/* 98 */     return Base64.encode(baos.toByteArray());
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.util.ScreenSendingUtilities
 * JD-Core Version:    0.6.0
 */