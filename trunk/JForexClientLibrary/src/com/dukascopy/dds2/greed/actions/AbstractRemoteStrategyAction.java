/*    */ package com.dukascopy.dds2.greed.actions;
/*    */ 
/*    */ import com.dukascopy.dds2.greed.GreedContext;
/*    */ import com.dukascopy.dds2.greed.gui.ClientForm;
/*    */ import com.dukascopy.dds2.greed.gui.ClientFormLayoutManager;
/*    */ import com.dukascopy.dds2.greed.gui.JForexClientFormLayoutManager;
/*    */ import com.dukascopy.dds2.greed.gui.component.strategy.tab.StrategiesContentPane;
/*    */ 
/*    */ public abstract class AbstractRemoteStrategyAction extends AppActionEvent
/*    */ {
/*    */   private static final long serialVersionUID = -8796384308051565126L;
/*    */ 
/*    */   public AbstractRemoteStrategyAction(Object source)
/*    */   {
/* 27 */     super(source, false, true);
/*    */   }
/*    */ 
/*    */   protected StrategiesContentPane getStrategiesContentPane() {
/* 31 */     ClientForm gui = (ClientForm)GreedContext.get("clientGui");
/* 32 */     if (gui != null) {
/* 33 */       ClientFormLayoutManager layout = gui.getLayoutManager();
/* 34 */       if ((layout instanceof JForexClientFormLayoutManager)) {
/* 35 */         return ((JForexClientFormLayoutManager)JForexClientFormLayoutManager.class.cast(layout)).getStrategiesPanel();
/*    */       }
/*    */     }
/* 38 */     return null;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.actions.AbstractRemoteStrategyAction
 * JD-Core Version:    0.6.0
 */