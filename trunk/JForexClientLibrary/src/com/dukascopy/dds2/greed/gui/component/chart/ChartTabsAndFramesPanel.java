/*    */ package com.dukascopy.dds2.greed.gui.component.chart;
/*    */ 
/*    */ import com.dukascopy.dds2.greed.gui.component.chart.listeners.FrameListener;
/*    */ import java.awt.BorderLayout;
/*    */ import java.awt.Dimension;
/*    */ import javax.swing.JComponent;
/*    */ import javax.swing.JLabel;
/*    */ import javax.swing.JPanel;
/*    */ 
/*    */ public class ChartTabsAndFramesPanel extends JPanel
/*    */   implements FrameListener
/*    */ {
/*    */   private final JComponent content;
/* 16 */   private final JLabel emptyLabel = new JLabel();
/* 17 */   private int lastSelectedPanelId = -1;
/* 18 */   private int lastSelectedChartPanelId = -1;
/*    */ 
/*    */   public ChartTabsAndFramesPanel(JComponent content) {
/* 21 */     super(new BorderLayout());
/* 22 */     this.content = content;
/*    */   }
/*    */ 
/*    */   public boolean isCloseAllowed(TabsAndFramePanel component) {
/* 26 */     return true;
/*    */   }
/*    */ 
/*    */   public void frameClosed(TabsAndFramePanel tabsAndFramePanel, int tabCount) {
/* 30 */     if (tabCount == 0) {
/* 31 */       remove(this.content);
/* 32 */       add(this.emptyLabel, "Center");
/*    */     }
/*    */   }
/*    */ 
/*    */   public void frameSelected(int panelId) {
/* 37 */     this.lastSelectedPanelId = panelId;
/*    */ 
/* 39 */     if ((this.content instanceof TabsAndFramesTabbedPane)) {
/* 40 */       DockedUndockedFrame frame = ((TabsAndFramesTabbedPane)this.content).getPanelByPanelId(panelId);
/* 41 */       if ((frame != null) && ((frame.getContent() instanceof ChartPanel)))
/* 42 */         this.lastSelectedChartPanelId = panelId;
/*    */     }
/*    */   }
/*    */ 
/*    */   public void frameAdded(boolean isUndocked, int tabCount)
/*    */   {
/* 48 */     if ((!isUndocked) && (tabCount == 1))
/* 49 */       add(this.content, "Center");
/*    */   }
/*    */ 
/*    */   public void frameDocked(int tabCount)
/*    */   {
/* 54 */     if (tabCount == 0) {
/* 55 */       remove(this.emptyLabel);
/* 56 */       add(this.content, "Center");
/*    */     }
/*    */   }
/*    */ 
/*    */   public void tabClosed(int tabCount) {
/* 61 */     if (tabCount == 0) {
/* 62 */       remove(this.content);
/* 63 */       add(this.emptyLabel, "Center");
/*    */     }
/*    */   }
/*    */ 
/*    */   public Dimension getMinimumSize() {
/* 68 */     Dimension minimumSize = super.getMinimumSize();
/* 69 */     minimumSize.height = 0;
/* 70 */     return minimumSize;
/*    */   }
/*    */ 
/*    */   public int getLastSelectedPanelId() {
/* 74 */     return this.lastSelectedPanelId;
/*    */   }
/*    */ 
/*    */   public int getLastSelectedChartPanelId() {
/* 78 */     return this.lastSelectedChartPanelId;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.chart.ChartTabsAndFramesPanel
 * JD-Core Version:    0.6.0
 */