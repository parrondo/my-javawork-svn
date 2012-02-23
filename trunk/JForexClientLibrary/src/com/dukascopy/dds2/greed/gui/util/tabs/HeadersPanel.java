/*    */ package com.dukascopy.dds2.greed.gui.util.tabs;
/*    */ 
/*    */ import java.awt.Color;
/*    */ import java.awt.FlowLayout;
/*    */ import java.awt.GradientPaint;
/*    */ import java.awt.Graphics;
/*    */ import java.awt.Graphics2D;
/*    */ import java.util.ArrayList;
/*    */ import java.util.List;
/*    */ import java.util.Map;
/*    */ import javax.swing.BorderFactory;
/*    */ import javax.swing.ImageIcon;
/*    */ import javax.swing.JPanel;
/*    */ 
/*    */ public class HeadersPanel extends JPanel
/*    */ {
/* 20 */   private static final Color BORDER_COLOR = new Color(173, 173, 173);
/*    */   private final TabbedPanel parent;
/* 23 */   private final List<TabHeaderPanel> headers = new ArrayList();
/*    */ 
/*    */   public HeadersPanel(TabbedPanel tabbedPanel) {
/* 26 */     this.parent = tabbedPanel;
/*    */ 
/* 28 */     setLayout(new FlowLayout()
/*    */     {
/*    */     });
/* 36 */     setBorder(BorderFactory.createLineBorder(BORDER_COLOR));
/*    */   }
/*    */ 
/*    */   public void addTabHeaderPanel(JPanel content, ImageIcon icon, String title, String toolTip) {
/* 40 */     TabHeaderPanel tabHeaderPanel = new TabHeaderPanel(this.parent, content, icon, title, toolTip);
/* 41 */     deselectHeaders();
/* 42 */     tabHeaderPanel.setSelected(true);
/* 43 */     this.headers.add(tabHeaderPanel);
/* 44 */     add(tabHeaderPanel);
/* 45 */     repaint();
/*    */   }
/*    */ 
/*    */   protected void paintComponent(Graphics g)
/*    */   {
/* 50 */     super.paintComponent(g);
/* 51 */     Graphics2D graphics2D = (Graphics2D)g;
/* 52 */     drawHeader(graphics2D);
/*    */   }
/*    */ 
/*    */   private void drawHeader(Graphics2D graphics2D) {
/* 56 */     Color[] colors = (Color[])TabHeaderPanel.COLORS.get(TabHeaderPanel.State.DEFAULT);
/*    */ 
/* 58 */     graphics2D.setPaint(new GradientPaint(getWidth() / 2, 0.0F, colors[0], getWidth() / 2, getHeight(), colors[1], false));
/*    */ 
/* 65 */     graphics2D.fillRect(0, 0, getWidth(), getHeight());
/*    */   }
/*    */ 
/*    */   public void setSelected(int selected) {
/* 69 */     TabHeaderPanel panelToSelect = (TabHeaderPanel)this.headers.get(selected);
/* 70 */     if (!panelToSelect.isSelected())
/* 71 */       panelToSelect.setSelected(true);
/*    */   }
/*    */ 
/*    */   public void deselectHeaders()
/*    */   {
/* 76 */     for (TabHeaderPanel header : this.headers)
/* 77 */       if (header.isSelected()) {
/* 78 */         header.setSelected(false);
/* 79 */         header.repaint();
/* 80 */         return;
/*    */       }
/*    */   }
/*    */ 
/*    */   public int getSelectedIndex()
/*    */   {
/* 86 */     for (TabHeaderPanel header : this.headers) {
/* 87 */       if (header.isSelected()) {
/* 88 */         return this.headers.indexOf(header);
/*    */       }
/*    */     }
/* 91 */     return -1;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.util.tabs.HeadersPanel
 * JD-Core Version:    0.6.0
 */