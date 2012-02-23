/*    */ package com.dukascopy.dds2.greed.util;
/*    */ 
/*    */ import org.slf4j.Logger;
/*    */ import org.slf4j.LoggerFactory;
/*    */ 
/*    */ public final class BrowserLauncher
/*    */ {
/* 14 */   private static final Logger LOGGER = LoggerFactory.getLogger(BrowserLauncher.class);
/* 15 */   private static final String[] BROWSERS = { "firefox", "opera", "konqueror", "epiphany", "seamonkey", "galeon", "kazehakase", "mozilla", "netscape" };
/*    */ 
/*    */   public static String getBrowserForUnix()
/*    */   {
/*    */     try
/*    */     {
/* 50 */       for (String browser : BROWSERS)
/* 51 */         if (Runtime.getRuntime().exec(new String[] { "which", browser }).waitFor() == 0)
/* 52 */           return browser;
/*    */     }
/*    */     catch (Exception e)
/*    */     {
/* 56 */       LOGGER.error(e.getMessage());
/*    */     }
/*    */ 
/* 59 */     return null;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.dds2.greed.util.BrowserLauncher
 * JD-Core Version:    0.6.0
 */