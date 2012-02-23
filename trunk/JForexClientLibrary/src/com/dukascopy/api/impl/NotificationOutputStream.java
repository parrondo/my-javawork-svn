/*    */ package com.dukascopy.api.impl;
/*    */ 
/*    */ import com.dukascopy.dds2.greed.util.INotificationUtils;
/*    */ import com.dukascopy.dds2.greed.util.NotificationLevel;
/*    */ import java.io.ByteArrayOutputStream;
/*    */ import java.io.IOException;
/*    */ import java.io.OutputStream;
/*    */ 
/*    */ public class NotificationOutputStream extends OutputStream
/*    */ {
/* 15 */   private NotificationLevel level = NotificationLevel.INFO;
/*    */   private INotificationUtils notificationUtils;
/* 18 */   private ByteArrayOutputStream baos = new ByteArrayOutputStream();
/*    */ 
/*    */   public NotificationOutputStream(NotificationLevel level, INotificationUtils notificationUtils) {
/* 21 */     this.level = level;
/* 22 */     this.notificationUtils = notificationUtils;
/*    */   }
/*    */ 
/*    */   public void write(int b)
/*    */     throws IOException
/*    */   {
/* 28 */     this.baos.write(b);
/* 29 */     if (b == 10)
/* 30 */       flush();
/*    */   }
/*    */ 
/*    */   public void flush()
/*    */     throws IOException
/*    */   {
/* 37 */     String msg = new String(this.baos.toByteArray());
/* 38 */     if (msg.trim().length() > 0) {
/* 39 */       this.notificationUtils.postMessage(msg, this.level);
/*    */     }
/*    */ 
/* 42 */     this.baos.reset();
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.api.impl.NotificationOutputStream
 * JD-Core Version:    0.6.0
 */