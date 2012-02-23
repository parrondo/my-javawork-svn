/*    */ package com.dukascopy.dds2.greed.console;
/*    */ 
/*    */ import com.dukascopy.api.impl.NotificationConsoleImpl;
/*    */ import com.dukascopy.dds2.greed.util.NotificationUtilsProvider;
/*    */ import java.io.PrintStream;
/*    */ 
/*    */ public class StrategyConsoleImpl extends NotificationConsoleImpl
/*    */ {
/*    */   public StrategyConsoleImpl(String key, String title)
/*    */   {
/* 12 */     super(NotificationUtilsProvider.getNotificationUtils());
/*    */ 
/* 15 */     this.errorStream = new PrintStream(new StrategyOutputStream(key, title, "ERROR"));
/* 16 */     this.outputStream = new PrintStream(new StrategyOutputStream(key, title, "INFO"));
/* 17 */     this.warnStream = new PrintStream(new StrategyOutputStream(key, title, "WARNING"));
/* 18 */     this.infoclientStream = new PrintStream(new StrategyOutputStream(key, title, "INFOCLIENT"));
/* 19 */     this.notifclientStream = new PrintStream(new StrategyOutputStream(key, title, "NOTIFCLIENT"));
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.console.StrategyConsoleImpl
 * JD-Core Version:    0.6.0
 */