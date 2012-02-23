/*    */ package com.dukascopy.dds2.greed.util.logging;
/*    */ 
/*    */ import java.io.OutputStream;
/*    */ import java.util.logging.Formatter;
/*    */ import java.util.logging.Level;
/*    */ import java.util.logging.LogRecord;
/*    */ import java.util.logging.StreamHandler;
/*    */ 
/*    */ public class GreedConsoleHandler extends StreamHandler
/*    */ {
/*    */   public GreedConsoleHandler()
/*    */   {
/* 17 */     setLevel(Level.FINEST);
/* 18 */     setFormatter(new GreedLogFormatter());
/* 19 */     setOutputStream(System.out);
/*    */   }
/*    */ 
/*    */   public GreedConsoleHandler(OutputStream out, Formatter formatter) {
/* 23 */     super(out, formatter);
/* 24 */     setLevel(Level.FINEST);
/* 25 */     setFormatter(formatter);
/* 26 */     setOutputStream(out);
/*    */   }
/*    */ 
/*    */   public synchronized void publish(LogRecord record) {
/* 30 */     super.publish(record);
/* 31 */     flush();
/*    */   }
/*    */ 
/*    */   public synchronized void close() {
/* 35 */     flush();
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.util.logging.GreedConsoleHandler
 * JD-Core Version:    0.6.0
 */