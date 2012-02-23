/*     */ package com.dukascopy.api.impl.connect;
/*     */ 
/*     */ import com.dukascopy.dds2.greed.util.INotificationUtils;
/*     */ import com.dukascopy.dds2.greed.util.NotificationLevel;
/*     */ import java.io.PrintStream;
/*     */ 
/*     */ public class PrintStreamNotificationUtils
/*     */   implements INotificationUtils
/*     */ {
/*     */   private final PrintStream out;
/*     */   private final PrintStream err;
/*     */ 
/*     */   public PrintStreamNotificationUtils(PrintStream out, PrintStream err)
/*     */   {
/*  16 */     this.out = out;
/*  17 */     this.err = err;
/*     */   }
/*     */ 
/*     */   public void postInfoMessage(String message)
/*     */   {
/*  22 */     postInfoMessage(message, null, false);
/*     */   }
/*     */ 
/*     */   public void postInfoMessage(String message, boolean localMessage)
/*     */   {
/*  27 */     postInfoMessage(message, null, localMessage);
/*     */   }
/*     */ 
/*     */   public void postInfoMessage(String message, Throwable t)
/*     */   {
/*  32 */     postInfoMessage(message, t, false);
/*     */   }
/*     */ 
/*     */   public void postInfoMessage(String message, Throwable t, boolean localMessage)
/*     */   {
/*  37 */     this.out.println(message);
/*  38 */     if (t != null) {
/*  39 */       this.out.print(t.getMessage() + ": ");
/*  40 */       t.printStackTrace(this.err);
/*     */     }
/*  42 */     if ((!localMessage) && (ActivityLogger.getInstance() != null))
/*  43 */       ActivityLogger.getInstance().add(message);
/*     */   }
/*     */ 
/*     */   public void postWarningMessage(String message)
/*     */   {
/*  49 */     postErrorMessage(message, null, false);
/*     */   }
/*     */ 
/*     */   public void postWarningMessage(String message, boolean localMessage)
/*     */   {
/*  54 */     postErrorMessage(message, null, localMessage);
/*     */   }
/*     */ 
/*     */   public void postWarningMessage(String message, Throwable t)
/*     */   {
/*  59 */     postErrorMessage(message, t, false);
/*     */   }
/*     */ 
/*     */   public void postWarningMessage(String message, Throwable t, boolean localMessage)
/*     */   {
/*  64 */     postErrorMessage(message, t, localMessage);
/*     */   }
/*     */ 
/*     */   public void postErrorMessage(String message)
/*     */   {
/*  69 */     postErrorMessage(message, null, false);
/*     */   }
/*     */ 
/*     */   public void postErrorMessage(String message, boolean localMessage)
/*     */   {
/*  74 */     postErrorMessage(message, null, localMessage);
/*     */   }
/*     */ 
/*     */   public void postErrorMessage(String message, Throwable t)
/*     */   {
/*  79 */     postErrorMessage(message, t, false);
/*     */   }
/*     */ 
/*     */   public void postErrorMessage(String message, Throwable t, boolean localMessage)
/*     */   {
/*  84 */     this.err.println(message);
/*  85 */     if (t != null) {
/*  86 */       this.err.print(t.getMessage() + ": ");
/*  87 */       t.printStackTrace(this.err);
/*     */     }
/*  89 */     if ((!localMessage) && (ActivityLogger.getInstance() != null))
/*  90 */       ActivityLogger.getInstance().add(message);
/*     */   }
/*     */ 
/*     */   public void postFatalMessage(String message)
/*     */   {
/*  96 */     postErrorMessage(message, null, false);
/*     */   }
/*     */ 
/*     */   public void postFatalMessage(String message, boolean localMessage)
/*     */   {
/* 101 */     postErrorMessage(message, null, localMessage);
/*     */   }
/*     */ 
/*     */   public void postFatalMessage(String message, Throwable t)
/*     */   {
/* 106 */     postErrorMessage(message, t, false);
/*     */   }
/*     */ 
/*     */   public void postFatalMessage(String message, Throwable t, boolean localMessage)
/*     */   {
/* 111 */     postErrorMessage(message, t, localMessage);
/*     */   }
/*     */ 
/*     */   public void postMessage(String message, NotificationLevel level)
/*     */   {
/* 116 */     switch (1.$SwitchMap$com$dukascopy$dds2$greed$util$NotificationLevel[level.ordinal()]) {
/*     */     case 1:
/* 118 */       postInfoMessage(message);
/* 119 */       break;
/*     */     case 2:
/* 121 */       postWarningMessage(message);
/* 122 */       break;
/*     */     case 3:
/* 124 */       postErrorMessage(message);
/* 125 */       break;
/*     */     case 4:
/* 127 */       postInfoMessage(message);
/* 128 */       break;
/*     */     case 5:
/* 130 */       postInfoMessage(message);
/* 131 */       break;
/*     */     default:
/* 133 */       throw new IllegalArgumentException("Unknown Notification Level: " + level);
/*     */     }
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.api.impl.connect.PrintStreamNotificationUtils
 * JD-Core Version:    0.6.0
 */