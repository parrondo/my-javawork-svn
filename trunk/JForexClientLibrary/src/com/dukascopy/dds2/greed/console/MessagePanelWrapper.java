/*    */ package com.dukascopy.dds2.greed.console;
/*    */ 
/*    */ import com.dukascopy.dds2.greed.gui.component.message.MessagePanel;
/*    */ 
/*    */ public class MessagePanelWrapper
/*    */ {
/*    */   private MessagePanel messagePanel;
/*  8 */   private int panelId = -1;
/*  9 */   private String title = "";
/*    */ 
/*    */   public MessagePanelWrapper(MessagePanel messagePanel, int panelId, String title) {
/* 12 */     this.messagePanel = messagePanel;
/* 13 */     this.panelId = panelId;
/* 14 */     this.title = title;
/*    */   }
/*    */ 
/*    */   public MessagePanel getMessagePanel() {
/* 18 */     return this.messagePanel;
/*    */   }
/*    */ 
/*    */   public int getPanelId() {
/* 22 */     return this.panelId;
/*    */   }
/*    */ 
/*    */   public String getTitle() {
/* 26 */     return this.title;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.console.MessagePanelWrapper
 * JD-Core Version:    0.6.0
 */