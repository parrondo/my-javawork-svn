/*    */ package com.dukascopy.dds2.greed.util;
/*    */ 
/*    */ public class NotificationUtilsProvider
/*    */ {
/*    */   private static INotificationUtils notificationUtils;
/*    */ 
/*    */   public static synchronized void setNotificationUtils(INotificationUtils notificationUtils)
/*    */   {
/* 10 */     notificationUtils = notificationUtils;
/*    */   }
/*    */ 
/*    */   public static synchronized INotificationUtils getNotificationUtils() {
/* 14 */     return notificationUtils;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.dds2.greed.util.NotificationUtilsProvider
 * JD-Core Version:    0.6.0
 */