/*    */ package com.dukascopy.dds2.greed.export.historicaldata;
/*    */ 
/*    */ import java.awt.event.ActionEvent;
/*    */ import java.awt.event.ActionListener;
/*    */ 
/*    */ public class CancelExportDataActionListener
/*    */   implements ActionListener
/*    */ {
/*    */   private ExportProcessControl exportProcessControl;
/*    */ 
/*    */   public CancelExportDataActionListener(ExportProcessControl exportProcessControl)
/*    */   {
/* 11 */     this.exportProcessControl = exportProcessControl;
/*    */   }
/*    */ 
/*    */   public void actionPerformed(ActionEvent e)
/*    */   {
/* 16 */     this.exportProcessControl.cancel();
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.export.historicaldata.CancelExportDataActionListener
 * JD-Core Version:    0.6.0
 */