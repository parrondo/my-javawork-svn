/*    */ package com.dukascopy.dds2.greed.gui;
/*    */ 
/*    */ import com.dukascopy.api.impl.connect.AuthorizationClient;
/*    */ import com.dukascopy.dds2.greed.GreedContext;
/*    */ import java.awt.image.BufferedImage;
/*    */ import java.io.IOException;
/*    */ import java.net.MalformedURLException;
/*    */ import java.util.Map;
/*    */ import org.slf4j.Logger;
/*    */ import org.slf4j.LoggerFactory;
/*    */ 
/*    */ public class CaptchaObtainer extends SwingWorker<Map<String, BufferedImage>>
/*    */ {
/* 14 */   private static final Logger LOGGER = LoggerFactory.getLogger(CaptchaObtainer.class);
/*    */   private Throwable error;
/*    */ 
/*    */   protected Map<String, BufferedImage> construct()
/*    */     throws InterruptedException, IOException
/*    */   {
/* 22 */     Map captcha = null;
/*    */     try {
/* 24 */       AuthorizationClient authorizationClient = GreedContext.AUTHORIZATION_CLIENT;
/* 25 */       captcha = authorizationClient.getImageCaptcha();
/*    */     } catch (MalformedURLException e) {
/* 27 */       LOGGER.error(e.getMessage(), e);
/* 28 */       setError(e);
/*    */     } catch (IOException e) {
/* 30 */       LOGGER.error(e.getMessage(), e);
/* 31 */       setError(e);
/*    */     }
/* 33 */     return captcha;
/*    */   }
/*    */ 
/*    */   protected void finished() {
/*    */   }
/*    */ 
/*    */   private void setError(Throwable error) {
/* 40 */     this.error = error;
/*    */   }
/*    */ 
/*    */   public Throwable getError() {
/* 44 */     return this.error;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.CaptchaObtainer
 * JD-Core Version:    0.6.0
 */