/*    */ package com.dukascopy.dds2.events.handler;
/*    */ 
/*    */ import com.dukascopy.dds2.events.Event;
/*    */ import java.util.concurrent.LinkedBlockingQueue;
/*    */ import java.util.concurrent.ThreadPoolExecutor;
/*    */ import java.util.concurrent.TimeUnit;
/*    */ import javax.sql.DataSource;
/*    */ 
/*    */ public class EventDBWriter
/*    */   implements EventWriter
/*    */ {
/*    */   private DataSource dataSource;
/* 20 */   private ThreadPoolExecutor executor = initializeThreadPool();
/*    */ 
/*    */   public EventDBWriter(DataSource dataSource)
/*    */   {
/* 24 */     this.dataSource = dataSource;
/*    */   }
/*    */ 
/*    */   private ThreadPoolExecutor initializeThreadPool() {
/* 28 */     ThreadPoolExecutor executor = new ThreadPoolExecutor(2, 2, 5000L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue());
/* 29 */     return executor;
/*    */   }
/*    */ 
/*    */   public void store(Event event, EventHandler handler)
/*    */   {
/*    */     try
/*    */     {
/* 39 */       EventDBWriterTask eventWriter = new EventDBWriterTask(event, handler.getServiceId(), this.dataSource);
/* 40 */       this.executor.execute(eventWriter);
/*    */     } catch (Exception e) {
/* 42 */       e.printStackTrace();
/*    */     }
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\dds2-common-2.3.77.jar
 * Qualified Name:     com.dukascopy.dds2.events.handler.EventDBWriter
 * JD-Core Version:    0.6.0
 */