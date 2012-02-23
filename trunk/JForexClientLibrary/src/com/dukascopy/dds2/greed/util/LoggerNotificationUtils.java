/*    */ package com.dukascopy.dds2.greed.util;
/*    */ 
/*    */ import org.slf4j.Logger;
/*    */ 
/*    */ public class LoggerNotificationUtils
/*    */   implements INotificationUtils
/*    */ {
/*    */   private final Logger logger;
/*    */ 
/*    */   public LoggerNotificationUtils(Logger logger)
/*    */   {
/* 19 */     this.logger = logger;
/*    */   }
/*    */ 
/*    */   public void postErrorMessage(String message)
/*    */   {
/* 24 */     this.logger.error(message);
/*    */   }
/*    */ 
/*    */   public void postErrorMessage(String message, boolean localMessage)
/*    */   {
/* 29 */     this.logger.error(message);
/*    */   }
/*    */ 
/*    */   public void postErrorMessage(String message, Throwable t)
/*    */   {
/* 34 */     this.logger.error(message, t);
/*    */   }
/*    */ 
/*    */   public void postErrorMessage(String message, Throwable t, boolean localMessage)
/*    */   {
/* 39 */     this.logger.error(message, t);
/*    */   }
/*    */ 
/*    */   public void postFatalMessage(String message)
/*    */   {
/* 44 */     this.logger.error(message);
/*    */   }
/*    */ 
/*    */   public void postFatalMessage(String message, boolean localMessage)
/*    */   {
/* 49 */     this.logger.error(message);
/*    */   }
/*    */ 
/*    */   public void postFatalMessage(String message, Throwable t)
/*    */   {
/* 54 */     this.logger.error(message, t);
/*    */   }
/*    */ 
/*    */   public void postFatalMessage(String message, Throwable t, boolean localMessage)
/*    */   {
/* 59 */     this.logger.error(message, t);
/*    */   }
/*    */ 
/*    */   public void postInfoMessage(String message)
/*    */   {
/*    */   }
/*    */ 
/*    */   public void postInfoMessage(String message, boolean localMessage)
/*    */   {
/*    */   }
/*    */ 
/*    */   public void postInfoMessage(String message, Throwable t)
/*    */   {
/*    */   }
/*    */ 
/*    */   public void postInfoMessage(String message, Throwable t, boolean localMessage)
/*    */   {
/*    */   }
/*    */ 
/*    */   public void postWarningMessage(String message)
/*    */   {
/*    */   }
/*    */ 
/*    */   public void postWarningMessage(String message, boolean localMessage)
/*    */   {
/*    */   }
/*    */ 
/*    */   public void postWarningMessage(String message, Throwable t)
/*    */   {
/*    */   }
/*    */ 
/*    */   public void postWarningMessage(String message, Throwable t, boolean localMessage)
/*    */   {
/*    */   }
/*    */ 
/*    */   public void postMessage(String message, NotificationLevel level)
/*    */   {
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.dds2.greed.util.LoggerNotificationUtils
 * JD-Core Version:    0.6.0
 */