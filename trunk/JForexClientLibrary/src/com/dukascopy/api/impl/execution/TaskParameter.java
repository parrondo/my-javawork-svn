/*    */ package com.dukascopy.api.impl.execution;
/*    */ 
/*    */ import com.dukascopy.api.IStrategy;
/*    */ import com.dukascopy.api.impl.StrategyMessages;
/*    */ import java.util.List;
/*    */ import javax.swing.JComponent;
/*    */ import org.slf4j.Logger;
/*    */ import org.slf4j.LoggerFactory;
/*    */ 
/*    */ public class TaskParameter
/*    */   implements Task
/*    */ {
/* 20 */   private static final Logger LOGGER = LoggerFactory.getLogger(TaskParameter.class);
/*    */ 
/* 22 */   private IControlUI parametersDialog = null;
/* 23 */   List<JComponent> componentsList = null;
/*    */   private IStrategy strategy;
/*    */   private boolean printChangeMessage;
/*    */ 
/*    */   public TaskParameter(IStrategy strategy, IControlUI parametersDialog, List<JComponent> componentsList)
/*    */   {
/* 28 */     this.parametersDialog = parametersDialog;
/* 29 */     this.componentsList = componentsList;
/* 30 */     this.strategy = strategy;
/*    */   }
/*    */ 
/*    */   public TaskParameter()
/*    */   {
/*    */   }
/*    */ 
/*    */   public Task.Type getType()
/*    */   {
/* 43 */     return Task.Type.PARAMETER;
/*    */   }
/*    */ 
/*    */   public Object call()
/*    */     throws Exception
/*    */   {
/*    */     try
/*    */     {
/* 53 */       if ((this.parametersDialog != null) && (this.componentsList != null)) {
/* 54 */         for (JComponent component : this.componentsList) {
/* 55 */           this.parametersDialog.setControlField(component, false);
/*    */         }
/* 57 */         if ((this.printChangeMessage) && 
/* 58 */           (this.parametersDialog != null) && (this.componentsList != null)) {
/* 59 */           StrategyMessages.strategyIsModified(this.strategy);
/*    */         }
/*    */       }
/*    */ 
/* 63 */       return Boolean.valueOf(true);
/*    */     } catch (Exception e1) {
/* 65 */       LOGGER.error(e1.getMessage(), e1);
/* 66 */     }return Boolean.valueOf(false);
/*    */   }
/*    */ 
/*    */   public void setPrintChangeMessage(boolean printChangeMessage)
/*    */   {
/* 72 */     this.printChangeMessage = printChangeMessage;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.api.impl.execution.TaskParameter
 * JD-Core Version:    0.6.0
 */