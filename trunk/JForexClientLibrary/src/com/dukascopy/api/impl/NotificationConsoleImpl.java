/*    */ package com.dukascopy.api.impl;
/*    */ 
/*    */ import com.dukascopy.api.IConsole;
/*    */ import com.dukascopy.dds2.greed.util.INotificationUtils;
/*    */ import com.dukascopy.dds2.greed.util.NotificationLevel;
/*    */ import java.io.PrintStream;
/*    */ 
/*    */ public class NotificationConsoleImpl
/*    */   implements IConsole
/*    */ {
/* 11 */   protected PrintStream errorStream = null;
/* 12 */   protected PrintStream outputStream = null;
/* 13 */   protected PrintStream warnStream = null;
/* 14 */   protected PrintStream infoclientStream = null;
/* 15 */   protected PrintStream notifclientStream = null;
/*    */ 
/*    */   public NotificationConsoleImpl(INotificationUtils notificationUtils)
/*    */   {
/* 19 */     this.errorStream = new PrintStream(new NotificationOutputStream(NotificationLevel.ERROR, notificationUtils));
/* 20 */     this.outputStream = new PrintStream(new NotificationOutputStream(NotificationLevel.INFO, notificationUtils));
/* 21 */     this.warnStream = new PrintStream(new NotificationOutputStream(NotificationLevel.WARNING, notificationUtils));
/* 22 */     this.infoclientStream = new PrintStream(new NotificationOutputStream(NotificationLevel.INFOCLIENT, notificationUtils));
/* 23 */     this.notifclientStream = new PrintStream(new NotificationOutputStream(NotificationLevel.NOTIFCLIENT, notificationUtils));
/*    */   }
/*    */ 
/*    */   public PrintStream getOut()
/*    */   {
/* 28 */     return this.outputStream;
/*    */   }
/*    */ 
/*    */   public PrintStream getErr()
/*    */   {
/* 33 */     return this.errorStream;
/*    */   }
/*    */ 
/*    */   public PrintStream getWarn()
/*    */   {
/* 38 */     return this.warnStream;
/*    */   }
/*    */ 
/*    */   public PrintStream getInfo()
/*    */   {
/* 43 */     return this.infoclientStream;
/*    */   }
/*    */ 
/*    */   public PrintStream getNotif()
/*    */   {
/* 48 */     return this.notifclientStream;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.api.impl.NotificationConsoleImpl
 * JD-Core Version:    0.6.0
 */