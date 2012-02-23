/*    */ package com.dukascopy.dds2.greed.export.historicaldata;
/*    */ 
/*    */ import com.dukascopy.dds2.greed.GreedContext;
/*    */ import com.dukascopy.dds2.greed.actions.ExportHistoricalDataAction;
/*    */ import java.awt.event.ActionEvent;
/*    */ import java.awt.event.ActionListener;
/*    */ import javax.swing.JPanel;
/*    */ 
/*    */ public class StartExportDataActionListener
/*    */   implements ActionListener
/*    */ {
/*    */   private ExportProcessControl exportProcessControl;
/*    */   private ExportDataParameters exportDataParameters;
/*    */   private JPanel mainPanel;
/*    */ 
/*    */   public StartExportDataActionListener(ExportProcessControl exportProcessControl, ExportDataParameters exportDataParameters, JPanel mainPanel)
/*    */   {
/* 22 */     this.exportProcessControl = exportProcessControl;
/* 23 */     this.exportDataParameters = exportDataParameters;
/* 24 */     this.mainPanel = mainPanel;
/*    */   }
/*    */ 
/*    */   public void actionPerformed(ActionEvent e)
/*    */   {
/* 29 */     ExportHistoricalDataAction exportHistoricalDataAction = new ExportHistoricalDataAction(e.getSource(), this.mainPanel, this.exportProcessControl, this.exportDataParameters);
/*    */ 
/* 36 */     GreedContext.publishEvent(exportHistoricalDataAction);
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.export.historicaldata.StartExportDataActionListener
 * JD-Core Version:    0.6.0
 */