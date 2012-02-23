/*     */ package com.dukascopy.dds2.greed.gui.component.strategy;
/*     */ 
/*     */ import com.dukascopy.dds2.greed.GreedContext;
/*     */ import com.dukascopy.dds2.greed.actions.PostMessageAction;
/*     */ import com.dukascopy.dds2.greed.gui.ClientForm;
/*     */ import com.dukascopy.dds2.greed.model.Notification;
/*     */ import com.dukascopy.dds2.greed.util.NotificationUtils;
/*     */ import java.io.FileOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.PrintWriter;
/*     */ import java.io.StringWriter;
/*     */ import java.text.DateFormat;
/*     */ import java.text.SimpleDateFormat;
/*     */ import java.util.Date;
/*     */ import java.util.TimeZone;
/*     */ import org.slf4j.Logger;
/*     */ import org.slf4j.LoggerFactory;
/*     */ 
/*     */ public class TesterNotificationUtils extends NotificationUtils
/*     */ {
/*     */   private FileOutputStream messagesStream;
/*     */   private final DateFormat dateFormat;
/*     */   private final StrategyTestPanel testerPanel;
/*     */ 
/*     */   public TesterNotificationUtils(StrategyTestPanel panel)
/*     */   {
/*  36 */     this.dateFormat = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");
/*  37 */     this.dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
/*  38 */     this.testerPanel = panel;
/*     */   }
/*     */ 
/*     */   public void setFileStream(FileOutputStream messagesStream) {
/*  42 */     this.messagesStream = messagesStream;
/*     */   }
/*     */ 
/*     */   protected void postMessageImpl(String priority, String message, boolean localMessage)
/*     */   {
/*  47 */     postMessageImpl(priority, message, null, localMessage);
/*     */   }
/*     */ 
/*     */   protected void postMessageImpl(String priority, String message, Throwable t, boolean localMessage)
/*     */   {
/*  52 */     Notification notification = new Notification(new Date(), message);
/*  53 */     notification.setPriority(priority);
/*     */ 
/*  55 */     if (t != null) {
/*  56 */       StringWriter sw = new StringWriter();
/*  57 */       t.printStackTrace(new PrintWriter(sw));
/*  58 */       String stacktrace = sw.toString();
/*  59 */       notification.setFullStackTrace(stacktrace);
/*     */     }
/*     */ 
/*  62 */     PostTesterMessageAction post = new PostTesterMessageAction(this, this.testerPanel, notification, localMessage, this.messagesStream, this.dateFormat);
/*  63 */     GreedContext.publishEvent(post);
/*     */   }
/*     */ 
/*     */   public void postMessage(Notification notification)
/*     */   {
/*  68 */     PostTesterMessageAction post = new PostTesterMessageAction(this, this.testerPanel, notification, true, this.messagesStream, this.dateFormat);
/*  69 */     GreedContext.publishEvent(post);
/*     */   }
/*  80 */   private static class PostTesterMessageAction extends PostMessageAction { private static final Logger LOGGER = LoggerFactory.getLogger(PostTesterMessageAction.class);
/*     */     private FileOutputStream messagesStream;
/*     */     private final DateFormat dateFormat;
/*     */     private final StrategyTestPanel testerPanel;
/*     */ 
/*  86 */     public PostTesterMessageAction(Object source, StrategyTestPanel panel, Notification notification, boolean isLocal, FileOutputStream messagesStream, DateFormat dateFormat) { super(notification, isLocal);
/*  87 */       this.messagesStream = messagesStream;
/*  88 */       this.dateFormat = dateFormat;
/*  89 */       this.testerPanel = panel;
/*     */     }
/*     */ 
/*     */     public void doAction()
/*     */     {
/*  94 */       if (this.messagesStream != null) {
/*  95 */         StringBuffer buffer = new StringBuffer();
/*     */ 
/*  97 */         buffer.append(this.dateFormat.format(this.message.getTimestamp()));
/*  98 */         buffer.append(",");
/*  99 */         buffer.append(this.message.getContent());
/*     */         try
/*     */         {
/* 104 */           this.messagesStream.write(buffer.toString().getBytes());
/*     */         } catch (IOException ex) {
/* 106 */           LOGGER.error("Cannot store message: " + buffer, ex);
/*     */ 
/* 108 */           this.messagesStream = null;
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/*     */     public void updateGuiAfter()
/*     */     {
/* 122 */       ClientForm form = (ClientForm)GreedContext.get("clientGui");
/* 123 */       if (!"ALERT".equals(this.message.getPriority())) {
/* 124 */         form.postTesterMessage(this.testerPanel, this.message, this.isLocal);
/* 125 */         checkPositionNotFound();
/*     */       } else {
/* 127 */         super.updateGuiAfter();
/*     */       }
/*     */     }
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.strategy.TesterNotificationUtils
 * JD-Core Version:    0.6.0
 */