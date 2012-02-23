/*    */ package com.dukascopy.dds2.greed.gui;
/*    */ 
/*    */ import java.net.InetAddress;
/*    */ import java.net.UnknownHostException;
/*    */ import org.slf4j.Logger;
/*    */ import org.slf4j.LoggerFactory;
/*    */ 
/*    */ public class HostResolver extends SwingWorker<String>
/*    */ {
/* 13 */   private static final Logger LOGGER = LoggerFactory.getLogger(HostResolver.class);
/*    */   private String hostname;
/*    */   private InetAddress inetAddress;
/*    */ 
/*    */   public HostResolver(String hostname)
/*    */   {
/* 19 */     this.hostname = hostname;
/*    */   }
/*    */   protected String construct() throws InterruptedException {
/*    */     try {
/* 23 */       this.inetAddress = InetAddress.getByName(this.hostname);
/*    */     } catch (UnknownHostException e) {
/* 25 */       LOGGER.error(e.getMessage(), e);
/*    */     }
/* 27 */     return this.inetAddress == null ? null : this.inetAddress.getHostAddress();
/*    */   }
/*    */ 
/*    */   protected void finished()
/*    */   {
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.HostResolver
 * JD-Core Version:    0.6.0
 */