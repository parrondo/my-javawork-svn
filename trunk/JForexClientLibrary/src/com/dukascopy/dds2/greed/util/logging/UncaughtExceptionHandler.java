/*    */ package com.dukascopy.dds2.greed.util.logging;
/*    */ 
/*    */ import org.slf4j.Logger;
/*    */ import org.slf4j.LoggerFactory;
/*    */ 
/*    */ public class UncaughtExceptionHandler
/*    */   implements Thread.UncaughtExceptionHandler
/*    */ {
/*  7 */   private static Logger LOGGER = LoggerFactory.getLogger(UncaughtExceptionHandler.class);
/*    */ 
/*    */   public void uncaughtException(Thread t, Throwable e) {
/* 10 */     LOGGER.error("Uncaught exception in [" + t.getName() + "] thread: " + e.getMessage(), e);
/*    */   }
/*    */ 
/*    */   public static class AwtHandler {
/*    */     public void handle(Throwable t) {
/* 15 */       UncaughtExceptionHandler.LOGGER.error("Uncaught AWT exception: " + t.getMessage(), t);
/*    */     }
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.util.logging.UncaughtExceptionHandler
 * JD-Core Version:    0.6.0
 */