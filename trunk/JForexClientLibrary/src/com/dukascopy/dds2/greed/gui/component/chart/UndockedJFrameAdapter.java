/*    */ package com.dukascopy.dds2.greed.gui.component.chart;
/*    */ 
/*    */ import java.awt.event.ActionEvent;
/*    */ import java.awt.event.ActionListener;
/*    */ import java.awt.event.WindowAdapter;
/*    */ import java.awt.event.WindowEvent;
/*    */ 
/*    */ class UndockedJFrameAdapter extends WindowAdapter
/*    */ {
/*    */   UndockedJFrame undockedFrame;
/*    */   ActionListener actionListener;
/*    */ 
/*    */   UndockedJFrameAdapter(UndockedJFrame undockedFrame, ActionListener actionListener)
/*    */   {
/* 14 */     this.undockedFrame = undockedFrame;
/* 15 */     this.actionListener = actionListener;
/*    */   }
/*    */ 
/*    */   public void windowClosing(WindowEvent e) {
/* 19 */     this.actionListener.actionPerformed(new ActionEvent(this.undockedFrame, this.undockedFrame.getPanelId(), "dockFrame"));
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.chart.UndockedJFrameAdapter
 * JD-Core Version:    0.6.0
 */