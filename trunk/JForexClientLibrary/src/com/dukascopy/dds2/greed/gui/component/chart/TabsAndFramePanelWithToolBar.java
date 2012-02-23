/*    */ package com.dukascopy.dds2.greed.gui.component.chart;
/*    */ 
/*    */ import com.dukascopy.dds2.greed.gui.component.chart.holders.IToolBarHolder;
/*    */ import com.dukascopy.dds2.greed.gui.component.chart.toolbar.DockUndockToolBar;
/*    */ import javax.swing.JPanel;
/*    */ 
/*    */ abstract class TabsAndFramePanelWithToolBar extends TabsAndFramePanel
/*    */   implements IToolBarHolder
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/*    */   protected final DockUndockToolBar toolBar;
/*    */   protected final TabedPanelType panelType;
/*    */ 
/*    */   public TabsAndFramePanelWithToolBar(int chartPanelId, DockUndockToolBar toolBar, TabedPanelType panelType)
/*    */   {
/* 17 */     super(chartPanelId);
/* 18 */     this.toolBar = toolBar;
/* 19 */     this.panelType = panelType;
/*    */   }
/*    */ 
/*    */   public void placeComponentsOn(JPanel container, UndockedJFrame undockedJFrame)
/*    */   {
/* 24 */     container.add(this, "Center");
/* 25 */     if (getToolBar() != null)
/* 26 */       container.add(getToolBar(), "First");
/*    */   }
/*    */ 
/*    */   public TabedPanelType getPanelType()
/*    */   {
/* 31 */     return this.panelType;
/*    */   }
/*    */ 
/*    */   public void refreshPinBtn() {
/* 35 */     getToolBar().refreshPinBtn();
/*    */   }
/*    */ 
/*    */   protected void selected(ToolBarAndDesktopPane toolBarAndDesktopPanel) {
/* 39 */     toolBarAndDesktopPanel.setCurrentToolBar(getToolBar());
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.chart.TabsAndFramePanelWithToolBar
 * JD-Core Version:    0.6.0
 */