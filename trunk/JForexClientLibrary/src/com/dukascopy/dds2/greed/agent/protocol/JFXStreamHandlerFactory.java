/*    */ package com.dukascopy.dds2.greed.agent.protocol;
/*    */ 
/*    */ import java.net.URL;
/*    */ import java.net.URLStreamHandler;
/*    */ import java.net.URLStreamHandlerFactory;
/*    */ import org.slf4j.Logger;
/*    */ import org.slf4j.LoggerFactory;
/*    */ 
/*    */ public class JFXStreamHandlerFactory
/*    */   implements URLStreamHandlerFactory
/*    */ {
/* 18 */   private static final Logger LOGGER = LoggerFactory.getLogger(JFXStreamHandlerFactory.class);
/* 19 */   private static boolean registered = false;
/*    */ 
/*    */   public static final void registerFactory() {
/* 22 */     if (!registered)
/*    */       try {
/* 24 */         URL.setURLStreamHandlerFactory(new JFXStreamHandlerFactory());
/* 25 */         registered = true;
/*    */       } catch (Error e) {
/* 27 */         LOGGER.error(e.getMessage(), e);
/*    */       }
/*    */   }
/*    */ 
/*    */   public URLStreamHandler createURLStreamHandler(String protocol)
/*    */   {
/* 36 */     if ("jfx".equals(protocol)) {
/* 37 */       return new JFXStreamHandler(Thread.currentThread().getContextClassLoader());
/*    */     }
/* 39 */     return null;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.dds2.greed.agent.protocol.JFXStreamHandlerFactory
 * JD-Core Version:    0.6.0
 */