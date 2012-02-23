/*     */ package com.dukascopy.dds2.greed.actions;
/*     */ 
/*     */ import com.dukascopy.dds2.greed.GreedContext;
/*     */ import com.dukascopy.dds2.greed.gui.ClientForm;
/*     */ import com.dukascopy.dds2.greed.gui.component.dialog.MailDialogFactory;
/*     */ import com.dukascopy.dds2.greed.gui.component.dialog.MailDialogFactory.MailDialog;
/*     */ import com.dukascopy.dds2.greed.model.Notification;
/*     */ import com.dukascopy.dds2.greed.util.EmergencyLogger;
/*     */ import java.util.Calendar;
/*     */ import java.util.HashSet;
/*     */ import java.util.Set;
/*     */ import java.util.regex.Matcher;
/*     */ import java.util.regex.Pattern;
/*     */ import javax.swing.JFrame;
/*     */ import javax.swing.JOptionPane;
/*     */ import org.slf4j.Logger;
/*     */ import org.slf4j.LoggerFactory;
/*     */ 
/*     */ public class PostMessageAction extends AppActionEvent
/*     */ {
/*  28 */   private static final Logger LOGGER = LoggerFactory.getLogger(PostMessageAction.class);
/*     */   protected Notification message;
/*  31 */   protected boolean isLocal = false;
/*  32 */   private final Pattern pattern = Pattern.compile("\\s#\\d+\\s");
/*  33 */   private long ttl = -1L;
/*     */ 
/*  35 */   private static final Set<String> openedAlerts = new HashSet();
/*     */ 
/*     */   public static void postLocalNotification(Object source, String message) {
/*  38 */     Calendar calendar = Calendar.getInstance();
/*  39 */     Notification notification = new Notification(calendar.getTime(), message);
/*  40 */     PostMessageAction action = new PostMessageAction(source, notification, true);
/*  41 */     GreedContext.publishEvent(action);
/*     */   }
/*     */ 
/*     */   public PostMessageAction(Object source, Notification notification)
/*     */   {
/*  50 */     super(source, false, true);
/*  51 */     this.message = notification;
/*     */   }
/*     */   public PostMessageAction(Object source, Notification notification, boolean isLocal) {
/*  54 */     this(source, notification);
/*  55 */     this.isLocal = isLocal;
/*     */   }
/*     */ 
/*     */   public PostMessageAction(Object source, Notification notification, String ttl) {
/*  59 */     this(source, notification);
/*     */ 
/*  61 */     if (ttl == null) return;
/*     */     try
/*     */     {
/*  64 */       this.ttl = Long.parseLong(ttl.trim());
/*     */     } catch (Exception e) {
/*  66 */       LOGGER.warn("illegal TTL :" + ttl);
/*     */     }
/*     */ 
/*  69 */     if (this.ttl < 3000L)
/*  70 */       this.ttl = -1L;
/*     */   }
/*     */ 
/*     */   public void updateGuiAfter()
/*     */   {
/*  80 */     ClientForm form = (ClientForm)GreedContext.get("clientGui");
/*  81 */     if (!"ALERT".equals(this.message.getPriority())) {
/*  82 */       form.postMessage(this.message, this.isLocal);
/*     */     }
/*     */     else
/*     */     {
/*  88 */       String multiLineMessage = this.message.getContent().replaceAll("##", "\n");
/*     */ 
/*  90 */       if (openedAlerts.contains(multiLineMessage)) {
/*  91 */         return;
/*     */       }
/*  93 */       openedAlerts.add(multiLineMessage);
/*     */ 
/*  96 */       if ((!this.isLocal) && (GreedContext.isActivityLoggingEnabled())) {
/*  97 */         EmergencyLogger logger = (EmergencyLogger)GreedContext.get("Logger");
/*  98 */         logger.add(this.message.getContent());
/*     */       }
/*     */ 
/* 101 */       MailDialogFactory.MailDialog mailDialog = MailDialogFactory.createMailDialog(form, multiLineMessage, this.ttl, GreedContext.getAccountName(), GreedContext.CLIENT_MODE);
/*     */ 
/* 103 */       mailDialog.updateWithResult();
/*     */ 
/* 105 */       openedAlerts.remove(multiLineMessage);
/*     */     }
/*     */ 
/* 108 */     checkPositionNotFound();
/*     */   }
/*     */ 
/*     */   protected void checkPositionNotFound()
/*     */   {
/* 113 */     String NOT_FOUND = "Position was not found";
/* 114 */     String message = this.message.getContent();
/* 115 */     message = this.pattern.matcher(message).replaceAll(" ").trim();
/* 116 */     if (message.startsWith("Position was not found")) {
/* 117 */       int reply = JOptionPane.showConfirmDialog((JFrame)GreedContext.get("clientGui"), "Deal station state appears to be non-consistent.\nRelogin recommended.\nPerform immediately?", "Communication error", 0, 0);
/*     */ 
/* 124 */       if (0 == reply) {
/* 125 */         ReloginAction reloginAction = new ReloginAction(this);
/* 126 */         GreedContext.publishEvent(reloginAction);
/*     */       }
/*     */     }
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.actions.PostMessageAction
 * JD-Core Version:    0.6.0
 */