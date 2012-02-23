/*    */ package com.dukascopy.dds2.greed.gui.component.message;
/*    */ 
/*    */ import java.awt.event.ActionEvent;
/*    */ import javax.swing.AbstractAction;
/*    */ 
/*    */ public class ActionClearMessage extends AbstractAction
/*    */ {
/*    */   private MessagePanel messagePanel;
/*    */ 
/*    */   public ActionClearMessage(MessagePanel messagePanel)
/*    */   {
/* 24 */     super("item.clear.all");
/* 25 */     this.messagePanel = messagePanel;
/*    */   }
/*    */ 
/*    */   public void actionPerformed(ActionEvent e)
/*    */   {
/* 32 */     this.messagePanel.clearMessageLog();
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.message.ActionClearMessage
 * JD-Core Version:    0.6.0
 */