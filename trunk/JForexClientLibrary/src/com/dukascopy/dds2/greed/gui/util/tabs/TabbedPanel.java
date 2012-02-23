/*    */ package com.dukascopy.dds2.greed.gui.util.tabs;
/*    */ 
/*    */ import java.awt.BorderLayout;
/*    */ import javax.swing.Box;
/*    */ import javax.swing.ImageIcon;
/*    */ import javax.swing.JPanel;
/*    */ 
/*    */ public class TabbedPanel extends JPanel
/*    */ {
/*    */   private static final int VERTICAL_GAP = 10;
/*    */   private final JPanel contentPanel;
/*    */   private final HeadersPanel headersPanel;
/*    */ 
/*    */   public TabbedPanel()
/*    */   {
/* 19 */     setLayout(new BorderLayout());
/*    */ 
/* 21 */     this.contentPanel = new JPanel(new BorderLayout());
/* 22 */     this.headersPanel = new HeadersPanel(this)
/*    */     {
/*    */     };
/* 26 */     add(this.headersPanel, "First");
/* 27 */     add(this.contentPanel, "Center");
/*    */   }
/*    */ 
/*    */   public HeadersPanel getHeadersPanel() {
/* 31 */     return this.headersPanel;
/*    */   }
/*    */ 
/*    */   public void setContent(JPanel content) {
/* 35 */     this.contentPanel.removeAll();
/* 36 */     this.contentPanel.add(Box.createVerticalStrut(10), "North");
/* 37 */     this.contentPanel.add(content, "Center");
/* 38 */     this.contentPanel.revalidate();
/* 39 */     this.contentPanel.repaint();
/*    */   }
/*    */ 
/*    */   public void addTab(JPanel content, ImageIcon icon, String title, String toolTip) {
/* 43 */     this.headersPanel.addTabHeaderPanel(content, icon, title, toolTip);
/*    */   }
/*    */ 
/*    */   public void setSelected(int index) {
/* 47 */     this.headersPanel.deselectHeaders();
/* 48 */     this.headersPanel.setSelected(index);
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.util.tabs.TabbedPanel
 * JD-Core Version:    0.6.0
 */