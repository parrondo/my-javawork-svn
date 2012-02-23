/*    */ package com.dukascopy.dds2.greed.console;
/*    */ 
/*    */ import com.dukascopy.api.ConsoleAdapter;
/*    */ import com.dukascopy.dds2.greed.GreedContext;
/*    */ import com.dukascopy.dds2.greed.actions.PostMessageAction;
/*    */ import com.dukascopy.dds2.greed.model.Notification;
/*    */ import java.awt.Color;
/*    */ import java.io.PrintStream;
/*    */ import java.io.Reader;
/*    */ import java.util.Calendar;
/*    */ import org.slf4j.Logger;
/*    */ import org.slf4j.LoggerFactory;
/*    */ 
/*    */ public class PlatformConsoleImpl extends ConsoleAdapter
/*    */ {
/* 25 */   private static final Logger LOGGER = LoggerFactory.getLogger(PlatformConsoleImpl.class);
/*    */ 
/* 27 */   private PrintStream errorStream = null;
/*    */ 
/* 29 */   private PrintStream infoStream = null;
/*    */ 
/* 31 */   private int panelId = -1;
/*    */ 
/* 33 */   PlatformOutputStream errorsOutput = new PlatformOutputStream("ERROR");
/* 34 */   PlatformOutputStream infoOutput = new PlatformOutputStream("INFO");
/*    */ 
/*    */   public PlatformConsoleImpl() {
/* 37 */     this.errorStream = new PrintStream(this.errorsOutput);
/* 38 */     this.infoStream = new PrintStream(this.infoOutput);
/*    */   }
/*    */ 
/*    */   public void error(Object error)
/*    */   {
/* 44 */     Calendar calendar = Calendar.getInstance();
/* 45 */     Notification notification = new Notification(calendar.getTime(), error.toString());
/* 46 */     notification.setPriority("ERROR");
/* 47 */     PostMessageAction pma = new PostMessageAction(this, notification, true);
/* 48 */     GreedContext.publishEvent(pma);
/*    */   }
/*    */ 
/*    */   public PrintStream getErr() {
/* 52 */     return this.errorStream;
/*    */   }
/*    */ 
/*    */   public Reader getIn() {
/* 56 */     return null;
/*    */   }
/*    */ 
/*    */   public PrintStream getOut() {
/* 60 */     return this.infoStream;
/*    */   }
/*    */ 
/*    */   public void print(Object print) {
/* 64 */     getOut().print(print);
/*    */   }
/*    */ 
/*    */   public void println(Object print) {
/* 68 */     if ((print != null) && (print.toString().trim().length() == 0)) {
/* 69 */       return;
/*    */     }
/* 71 */     print(print);
/*    */   }
/*    */ 
/*    */   public void print(Object arg0, Color arg1) {
/* 75 */     getOut().print(arg0.toString());
/* 76 */     getOut().flush();
/*    */   }
/*    */ 
/*    */   public int getPanelId() {
/* 80 */     return this.panelId;
/*    */   }
/*    */   public void setPanelId(int panelId) {
/* 83 */     this.panelId = panelId;
/* 84 */     this.errorsOutput.setPanelId(panelId);
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.console.PlatformConsoleImpl
 * JD-Core Version:    0.6.0
 */