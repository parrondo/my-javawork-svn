/*    */ package com.dukascopy.dds2.greed.console;
/*    */ 
/*    */ import com.dukascopy.dds2.greed.gui.component.message.MessagePanel;
/*    */ import com.dukascopy.dds2.greed.model.Notification;
/*    */ import com.dukascopy.dds2.greed.util.NotificationUtils;
/*    */ import java.io.ByteArrayOutputStream;
/*    */ import java.io.IOException;
/*    */ import java.io.OutputStream;
/*    */ import javax.swing.SwingUtilities;
/*    */ 
/*    */ public class StrategyOutputStream extends OutputStream
/*    */ {
/* 15 */   private String priority = "INFO";
/* 16 */   private String key = null;
/* 17 */   private String title = null;
/*    */ 
/* 19 */   ByteArrayOutputStream baos = new ByteArrayOutputStream();
/*    */ 
/*    */   public StrategyOutputStream(String key, String title, String priority)
/*    */   {
/* 23 */     this.key = key;
/* 24 */     this.title = title;
/* 25 */     this.priority = priority;
/*    */   }
/*    */ 
/*    */   public void write(int b) throws IOException
/*    */   {
/* 30 */     this.baos.write(b);
/* 31 */     if (b == 10)
/* 32 */       flush();
/*    */   }
/*    */ 
/*    */   public void flush() throws IOException
/*    */   {
/* 37 */     String msg = new String(this.baos.toByteArray());
/* 38 */     if (msg.trim().length() > 0)
/*    */     {
/* 40 */       Notification notification = new Notification(msg);
/* 41 */       notification.setPriority(this.priority);
/*    */ 
/* 44 */       SwingUtilities.invokeLater(new Runnable(notification) {
/*    */         public void run() {
/* 46 */           MessagePanel pnl = MessagePanelManager.getInstance().getPanel(StrategyOutputStream.this.key, StrategyOutputStream.this.title);
/* 47 */           if (pnl != null) {
/* 48 */             pnl.postMessage(this.val$notification);
/* 49 */             pnl.repaint();
/*    */           }
/*    */           else {
/* 52 */             ((NotificationUtils)NotificationUtils.getInstance()).postMessage(this.val$notification);
/*    */           }
/*    */         }
/*    */       });
/*    */     }
/*    */ 
/* 59 */     this.baos.reset();
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.console.StrategyOutputStream
 * JD-Core Version:    0.6.0
 */