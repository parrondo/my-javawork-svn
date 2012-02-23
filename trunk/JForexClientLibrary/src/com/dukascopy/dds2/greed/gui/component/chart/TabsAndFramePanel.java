/*    */ package com.dukascopy.dds2.greed.gui.component.chart;
/*    */ 
/*    */ import com.dukascopy.dds2.greed.gui.component.chart.holders.IPanelIdHolder;
/*    */ import javax.swing.JPanel;
/*    */ 
/*    */ public abstract class TabsAndFramePanel extends JPanel
/*    */   implements IPanelIdHolder
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/*    */   private final int chartPanelId;
/*    */ 
/*    */   public TabsAndFramePanel(int chartPanelId)
/*    */   {
/* 16 */     this.chartPanelId = chartPanelId;
/*    */   }
/*    */ 
/*    */   public int getPanelId() {
/* 20 */     return this.chartPanelId;
/*    */   }
/*    */ 
/*    */   public boolean isCloseAllowed() {
/* 24 */     return true;
/*    */   }
/*    */ 
/*    */   public void refreshPinBtn() {
/*    */   }
/*    */ 
/*    */   protected void placeComponentsOn(JPanel container, UndockedJFrame undockedJFrame) {
/* 31 */     container.add(this, "Center");
/*    */   }
/*    */ 
/*    */   protected void selected(ToolBarAndDesktopPane toolBarAndDesktopPanel)
/*    */   {
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.chart.TabsAndFramePanel
 * JD-Core Version:    0.6.0
 */