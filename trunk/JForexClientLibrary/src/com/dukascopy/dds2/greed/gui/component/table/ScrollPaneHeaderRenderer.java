/*    */ package com.dukascopy.dds2.greed.gui.component.table;
/*    */ 
/*    */ import com.dukascopy.dds2.greed.util.LookAndFeelSpecific;
/*    */ import com.dukascopy.dds2.greed.util.PlatformSpecific;
/*    */ import java.awt.Component;
/*    */ import java.awt.Graphics;
/*    */ import javax.swing.JPanel;
/*    */ import javax.swing.JTable;
/*    */ import javax.swing.table.JTableHeader;
/*    */ import javax.swing.table.TableCellRenderer;
/*    */ 
/*    */ public class ScrollPaneHeaderRenderer extends JPanel
/*    */   implements TableCellRenderer, PlatformSpecific, LookAndFeelSpecific
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/*    */   private ScrollPaneHeaderRenderer rendererComponent;
/*    */ 
/*    */   public ScrollPaneHeaderRenderer()
/*    */   {
/* 26 */     this.rendererComponent = this;
/*    */   }
/*    */ 
/*    */   public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
/*    */   {
/* 34 */     if (table != null)
/*    */     {
/* 35 */       JTableHeader header = table.getTableHeader();
/* 36 */       if (header == null);
/*    */     }
/* 41 */     if (WINDOWS_XP) {
/* 42 */       this.rendererComponent.setBackground(HeaderUI.xpBgColor);
/*    */     }
/*    */ 
/* 48 */     if (MACOS) {
/* 49 */       this.rendererComponent.setBackground(HeaderUI.macBgColor);
/*    */     }
/* 51 */     return this.rendererComponent;
/*    */   }
/*    */ 
/*    */   public void paintComponent(Graphics g)
/*    */   {
/* 56 */     super.paintComponent(g);
/*    */ 
/* 58 */     if (WINDOWS_XP) {
/* 59 */       HeaderUI.xpHeader4ScrollerStyle(g, this);
/*    */     }
/* 61 */     if (CLASSIC) {
/* 62 */       HeaderUI.windowsClassicHeader4ScrollerStyle(g, this);
/*    */     }
/* 64 */     if (METAL) {
/* 65 */       HeaderUI.metalHeader4ScrollerStyle(g, this);
/*    */     }
/*    */ 
/* 70 */     if (MACOS)
/* 71 */       HeaderUI.macOsHeader4ScrollerStyle(g, this);
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.table.ScrollPaneHeaderRenderer
 * JD-Core Version:    0.6.0
 */