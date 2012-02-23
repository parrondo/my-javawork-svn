/*    */ package com.dukascopy.dds2.greed.agent.strategy.tester;
/*    */ 
/*    */ import com.dukascopy.dds2.greed.util.INotificationUtils;
/*    */ import java.io.ByteArrayOutputStream;
/*    */ import java.io.IOException;
/*    */ import java.io.OutputStream;
/*    */ 
/*    */ public class TesterOutputStream extends OutputStream
/*    */ {
/* 14 */   private String priority = "ERROR";
/*    */   private INotificationUtils notificationUtils;
/* 17 */   private ByteArrayOutputStream baos = new ByteArrayOutputStream();
/*    */ 
/*    */   public TesterOutputStream(String priority, INotificationUtils notificationUtils) {
/* 20 */     this.priority = priority;
/* 21 */     this.notificationUtils = notificationUtils;
/*    */   }
/*    */ 
/*    */   public void write(int b)
/*    */     throws IOException
/*    */   {
/* 27 */     this.baos.write(b);
/* 28 */     if (b == 10)
/* 29 */       flush();
/*    */   }
/*    */ 
/*    */   public void flush()
/*    */     throws IOException
/*    */   {
/* 36 */     String msg = new String(this.baos.toByteArray());
/* 37 */     if (msg.trim().length() > 0) {
/* 38 */       if (this.priority.equals("ERROR"))
/* 39 */         this.notificationUtils.postErrorMessage(msg, true);
/*    */       else {
/* 41 */         this.notificationUtils.postInfoMessage(msg, true);
/*    */       }
/*    */     }
/* 44 */     this.baos.reset();
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.dds2.greed.agent.strategy.tester.TesterOutputStream
 * JD-Core Version:    0.6.0
 */