/*    */ package com.dukascopy.dds2.greed.gui.component.chart;
/*    */ 
/*    */ import java.awt.event.ActionEvent;
/*    */ import java.awt.event.ActionListener;
/*    */ import javax.swing.JPanel;
/*    */ import javax.swing.event.InternalFrameAdapter;
/*    */ import javax.swing.event.InternalFrameEvent;
/*    */ 
/*    */ class HeadlessJInternalFrameAdapter extends InternalFrameAdapter
/*    */ {
/*    */   ActionListener actionListener;
/*    */   HeadlessJInternalFrame internalFrame;
/*    */   JPanel content;
/*    */ 
/*    */   HeadlessJInternalFrameAdapter(HeadlessJInternalFrame internalFrame, JPanel content, ActionListener actionListener)
/*    */   {
/* 17 */     this.internalFrame = internalFrame;
/* 18 */     this.content = content;
/* 19 */     this.actionListener = actionListener;
/*    */   }
/*    */ 
/*    */   public void internalFrameActivated(InternalFrameEvent e)
/*    */   {
/* 24 */     this.actionListener.actionPerformed(new ActionEvent(this.internalFrame, this.internalFrame.getPanelId(), "syncTabAndFrameSelection"));
/*    */   }
/*    */ 
/*    */   public void internalFrameClosing(InternalFrameEvent e) {
/* 28 */     if ((this.content instanceof BottomPanelWithoutProfitLossLabel))
/* 29 */       this.actionListener.actionPerformed(new ActionEvent(this.internalFrame, this.internalFrame.getPanelId(), "closeTabAndInternalFrame"));
/* 30 */     else if (!(this.content instanceof BottomPanelWithProfitLossLabel))
/*    */     {
/* 33 */       this.actionListener.actionPerformed(new ActionEvent(this.internalFrame, this.internalFrame.getPanelId(), "closeTabAndInternalFrame"));
/*    */     }
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.chart.HeadlessJInternalFrameAdapter
 * JD-Core Version:    0.6.0
 */