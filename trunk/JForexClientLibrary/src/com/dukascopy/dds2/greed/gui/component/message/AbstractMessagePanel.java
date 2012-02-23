/*    */ package com.dukascopy.dds2.greed.gui.component.message;
/*    */ 
/*    */ import com.dukascopy.dds2.greed.model.Notification;
/*    */ import javax.swing.JPanel;
/*    */ 
/*    */ public abstract class AbstractMessagePanel extends JPanel
/*    */ {
/*    */   private TabComponent tabComponent;
/*    */ 
/*    */   public void setTabLabel(TabComponent tabComponent)
/*    */   {
/* 21 */     this.tabComponent = tabComponent;
/*    */   }
/*    */ 
/*    */   protected void notifyTabComponent(Notification message) {
/* 25 */     if (this.tabComponent != null)
/* 26 */       this.tabComponent.messageAdded(message);
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.message.AbstractMessagePanel
 * JD-Core Version:    0.6.0
 */