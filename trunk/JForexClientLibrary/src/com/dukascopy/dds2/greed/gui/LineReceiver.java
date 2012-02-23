/*    */ package com.dukascopy.dds2.greed.gui;
/*    */ 
/*    */ import java.io.IOException;
/*    */ import java.net.MalformedURLException;
/*    */ import java.net.URL;
/*    */ import org.slf4j.Logger;
/*    */ import org.slf4j.LoggerFactory;
/*    */ 
/*    */ public class LineReceiver extends SwingWorker<String>
/*    */ {
/* 14 */   private static final Logger LOGGER = LoggerFactory.getLogger(LineReceiver.class);
/*    */   private URL url;
/*    */   private String error;
/*    */ 
/*    */   public LineReceiver(URL url)
/*    */   {
/* 20 */     this.url = url;
/*    */   }
/*    */ 
/*    */   protected String construct() throws InterruptedException {
/* 24 */     String receivedLine = null;
/*    */     try {
/* 26 */       receivedLine = GuiUtilsAndConstants.receiveLineViaHttpOrHttps(this.url);
/*    */     } catch (MalformedURLException e) {
/* 28 */       LOGGER.error(e.getMessage(), e);
/* 29 */       this.error = "Malformed Address";
/*    */     } catch (IOException e) {
/* 31 */       LOGGER.error(e.getMessage(), e);
/* 32 */       this.error = "Network Error";
/*    */     }
/* 34 */     return receivedLine;
/*    */   }
/*    */ 
/*    */   public String getError() {
/* 38 */     return this.error;
/*    */   }
/*    */ 
/*    */   protected void finished()
/*    */   {
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.LineReceiver
 * JD-Core Version:    0.6.0
 */