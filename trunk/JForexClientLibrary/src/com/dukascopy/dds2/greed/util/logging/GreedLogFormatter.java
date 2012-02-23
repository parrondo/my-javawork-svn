/*    */ package com.dukascopy.dds2.greed.util.logging;
/*    */ 
/*    */ import java.io.PrintStream;
/*    */ import java.io.PrintWriter;
/*    */ import java.io.StringWriter;
/*    */ import java.security.AccessController;
/*    */ import java.text.SimpleDateFormat;
/*    */ import java.util.Date;
/*    */ import java.util.Locale;
/*    */ import java.util.logging.Level;
/*    */ import java.util.logging.LogRecord;
/*    */ import sun.security.action.GetPropertyAction;
/*    */ 
/*    */ public class GreedLogFormatter extends java.util.logging.Formatter
/*    */ {
/* 22 */   Date dat = new Date();
/* 23 */   private SimpleDateFormat df = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss.SSS");
/*    */ 
/* 25 */   private Object[] args = new Object[1];
/*    */ 
/* 29 */   private String lineSeparator = (String)AccessController.doPrivileged(new GetPropertyAction("line.separator"));
/*    */ 
/* 31 */   private static int maxLoggerLength = 30;
/*    */ 
/*    */   public synchronized String format(LogRecord record)
/*    */   {
/* 41 */     StringBuilder sb = new StringBuilder();
/*    */ 
/* 43 */     this.dat.setTime(record.getMillis());
/* 44 */     this.args[0] = this.dat;
/* 45 */     sb.append(this.df.format(this.dat));
/* 46 */     sb.append(" ");
/* 47 */     StringBuilder levelBldr = new StringBuilder(8);
/* 48 */     java.util.Formatter levelFormatter = new java.util.Formatter(levelBldr, Locale.US);
/* 49 */     levelFormatter.format("%8s", new Object[] { record.getLevel().getLocalizedName() });
/* 50 */     sb.append(levelBldr.toString());
/* 51 */     sb.append(" ");
/* 52 */     String loggerName = record.getLoggerName();
/* 53 */     if (loggerName != null) {
/* 54 */       loggerName = trimmerLoggerName(loggerName);
/* 55 */       if (loggerName.length() > maxLoggerLength) {
/* 56 */         maxLoggerLength = loggerName.length();
/*    */       }
/*    */     }
/* 59 */     StringBuilder loggerBldr = new StringBuilder(maxLoggerLength);
/* 60 */     java.util.Formatter loggerFormatter = new java.util.Formatter(loggerBldr, Locale.US);
/* 61 */     loggerFormatter.format(new StringBuilder().append("%").append(maxLoggerLength).append("s").toString(), new Object[] { loggerName });
/* 62 */     sb.append(loggerFormatter.toString());
/* 63 */     sb.append(" ");
/* 64 */     String message = formatMessage(record);
/* 65 */     sb.append("] ");
/* 66 */     sb.append(message);
/* 67 */     sb.append(this.lineSeparator);
/* 68 */     if (record.getThrown() != null) {
/*    */       try {
/* 70 */         StringWriter sw = new StringWriter();
/* 71 */         PrintWriter pw = new PrintWriter(sw);
/* 72 */         record.getThrown().printStackTrace(pw);
/* 73 */         pw.close();
/* 74 */         sb.append(sw.toString());
/*    */       }
/*    */       catch (Exception ex) {
/* 77 */         System.err.println(new StringBuilder().append("e ").append(ex.getMessage()).toString());
/*    */       }
/*    */     }
/* 80 */     return sb.toString();
/*    */   }
/*    */ 
/*    */   private String trimmerLoggerName(String loggerName) {
/* 84 */     int lastDotIndex = loggerName.lastIndexOf(46);
/* 85 */     if (lastDotIndex > -1) {
/* 86 */       int secondLastDotIndex = loggerName.substring(0, lastDotIndex).lastIndexOf(46);
/* 87 */       if (secondLastDotIndex > -1) {
/* 88 */         loggerName = loggerName.substring(secondLastDotIndex + 1, loggerName.length());
/*    */       }
/*    */     }
/* 91 */     return loggerName;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.util.logging.GreedLogFormatter
 * JD-Core Version:    0.6.0
 */