/*    */ package com.dukascopy.api.impl.connect;
/*    */ 
/*    */ import com.dukascopy.api.system.tester.ITesterChartController;
/*    */ import com.dukascopy.api.system.tester.ITesterGui;
/*    */ import javax.swing.JPanel;
/*    */ 
/*    */ public class ITesterGuiImpl
/*    */   implements ITesterGui
/*    */ {
/* 10 */   private JPanel chartPanel = null;
/* 11 */   private ITesterChartController testerChartController = null;
/*    */ 
/*    */   public JPanel getChartPanel()
/*    */   {
/* 15 */     return this.chartPanel;
/*    */   }
/*    */ 
/*    */   public ITesterChartController getTesterChartController()
/*    */   {
/* 20 */     return this.testerChartController;
/*    */   }
/*    */ 
/*    */   public void setChartPanel(JPanel chartPanel) {
/* 24 */     this.chartPanel = chartPanel;
/*    */   }
/*    */ 
/*    */   public void setTesterChartControl(ITesterChartController testerChartControl) {
/* 28 */     this.testerChartController = testerChartControl;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.api.impl.connect.ITesterGuiImpl
 * JD-Core Version:    0.6.0
 */