/*    */ package com.dukascopy.dds2.greed.console;
/*    */ 
/*    */ import com.dukascopy.dds2.greed.model.Notification;
/*    */ import com.dukascopy.dds2.greed.util.NotificationUtils;
/*    */ import java.io.ByteArrayOutputStream;
/*    */ import java.io.IOException;
/*    */ import java.io.OutputStream;
/*    */ import java.util.Calendar;
/*    */ 
/*    */ public class PlatformOutputStream extends OutputStream
/*    */ {
/* 20 */   private String priority = "ERROR";
/* 21 */   private int panelId = -1;
/*    */ 
/* 28 */   ByteArrayOutputStream baos = new ByteArrayOutputStream();
/*    */ 
/*    */   public PlatformOutputStream(String priority)
/*    */   {
/* 25 */     this.priority = priority;
/*    */   }
/*    */ 
/*    */   public void write(int b)
/*    */     throws IOException
/*    */   {
/* 33 */     this.baos.write(b);
/* 34 */     if (b == 10)
/* 35 */       flush();
/*    */   }
/*    */ 
/*    */   public void flush()
/*    */     throws IOException
/*    */   {
/* 42 */     String msg = new String(this.baos.toByteArray());
/* 43 */     if (msg.trim().length() > 0) {
/* 44 */       Calendar calendar = Calendar.getInstance();
/* 45 */       Notification notification = new Notification(calendar.getTime(), msg);
/* 46 */       notification.setPriority(this.priority);
/*    */ 
/* 48 */       if ("ERROR".equals(this.priority)) {
/* 49 */         notification.setPanelId(this.panelId);
/*    */       }
/* 51 */       ((NotificationUtils)NotificationUtils.getInstance()).postMessage(notification);
/*    */     }
/*    */ 
/* 54 */     this.baos.reset();
/*    */   }
/*    */ 
/*    */   public int getPanelId() {
/* 58 */     return this.panelId;
/*    */   }
/*    */ 
/*    */   public void setPanelId(int panelId) {
/* 62 */     this.panelId = panelId;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.console.PlatformOutputStream
 * JD-Core Version:    0.6.0
 */