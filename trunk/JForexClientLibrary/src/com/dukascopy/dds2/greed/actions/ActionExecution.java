/*    */ package com.dukascopy.dds2.greed.actions;
/*    */ 
/*    */ import com.dukascopy.dds2.greed.util.event.ApplicationEvent;
/*    */ import java.lang.reflect.InvocationTargetException;
/*    */ import javax.swing.SwingUtilities;
/*    */ import org.slf4j.Logger;
/*    */ import org.slf4j.LoggerFactory;
/*    */ 
/*    */ public class ActionExecution
/*    */   implements Runnable
/*    */ {
/* 11 */   private static final Logger LOGGER = LoggerFactory.getLogger(ActionExecution.class);
/*    */   public ApplicationEvent handedOverExecutionEvent;
/*    */   public String actionName;
/*    */ 
/*    */   public ActionExecution(ApplicationEvent event)
/*    */   {
/* 17 */     this.handedOverExecutionEvent = event;
/* 18 */     this.actionName = event.getClass().getName();
/*    */   }
/*    */ 
/*    */   public String toString() {
/* 22 */     return this.actionName;
/*    */   }
/*    */ 
/*    */   public void run() {
/* 26 */     if ((this.handedOverExecutionEvent instanceof AppActionEvent)) {
/* 27 */       AppActionEvent event = (AppActionEvent)this.handedOverExecutionEvent;
/* 28 */       if (event.isUpdateGuiBefore()) {
/* 29 */         if (SwingUtilities.isEventDispatchThread())
/*    */         {
/* 31 */           event.updateGuiBefore();
/*    */         }
/*    */         else try {
/* 34 */             SwingUtilities.invokeAndWait(new Runnable(event) {
/*    */               public void run() {
/* 36 */                 this.val$event.updateGuiBefore();
/*    */               } } );
/*    */           } catch (InterruptedException e) {
/* 40 */             LOGGER.error(e.getMessage(), e);
/*    */           } catch (InvocationTargetException e) {
/* 42 */             LOGGER.error(e.getMessage(), e);
/*    */           }
/*    */       }
/*    */       try
/*    */       {
/* 47 */         event.doAction();
/*    */       }
/*    */       catch (RuntimeException e) {
/* 49 */         LOGGER.error(e.getMessage(), e);
/* 50 */         if ((e.getMessage() == null) || 
/* 51 */           (!e.getMessage().startsWith("Fatal error response")) || (e.getMessage().indexOf("Bad session") <= -1));
/*    */       }
/* 56 */       if (event.isUpdateGuiAfter())
/* 57 */         if (SwingUtilities.isEventDispatchThread())
/*    */         {
/* 59 */           event.updateGuiAfter();
/*    */         }
/* 61 */         else SwingUtilities.invokeLater(new Runnable(event) {
/*    */             public void run() {
/* 63 */               this.val$event.updateGuiAfter();
/*    */             }
/*    */           });
/*    */     }
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.actions.ActionExecution
 * JD-Core Version:    0.6.0
 */