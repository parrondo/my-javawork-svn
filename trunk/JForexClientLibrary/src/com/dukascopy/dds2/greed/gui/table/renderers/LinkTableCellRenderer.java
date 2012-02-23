/*    */ package com.dukascopy.dds2.greed.gui.table.renderers;
/*    */ 
/*    */ import java.awt.Component;
/*    */ import java.awt.Cursor;
/*    */ import java.awt.Desktop;
/*    */ import java.awt.event.MouseAdapter;
/*    */ import java.awt.event.MouseEvent;
/*    */ import java.awt.event.MouseMotionAdapter;
/*    */ import java.net.URI;
/*    */ import javax.swing.JLabel;
/*    */ import javax.swing.JTable;
/*    */ import org.slf4j.Logger;
/*    */ import org.slf4j.LoggerFactory;
/*    */ 
/*    */ public class LinkTableCellRenderer extends AbstractTableCellRenderer
/*    */ {
/* 23 */   private static final Logger LOGGER = LoggerFactory.getLogger(LinkTableCellRenderer.class);
/*    */   private static final String URL_KEY = "[x]";
/*    */ 
/*    */   public LinkTableCellRenderer()
/*    */   {
/* 29 */     addMouseMotionListener(new MouseMotionAdapter()
/*    */     {
/*    */       public void mouseMoved(MouseEvent e) {
/* 32 */         String url = (String)LinkTableCellRenderer.this.getClientProperty("[x]");
/* 33 */         if ((url != null) && (!url.isEmpty())) {
/* 34 */           ((JTable)e.getSource()).setCursor(Cursor.getPredefinedCursor(12));
/* 35 */           return;
/*    */         }
/*    */       }
/*    */     });
/* 39 */     addMouseListener(new MouseAdapter()
/*    */     {
/*    */       public void mouseClicked(MouseEvent e) {
/* 42 */         if (e.getClickCount() >= 2) {
/* 43 */           String url = (String)LinkTableCellRenderer.this.getClientProperty("[x]");
/*    */ 
/* 45 */           if ((url != null) && (!url.isEmpty())) try {
/* 46 */               Desktop.getDesktop().browse(new URI(url));
/*    */             } catch (Exception ex) {
/* 48 */               LinkTableCellRenderer.LOGGER.error("Unable to open url in browser : " + url, ex);
/*    */             }
/*    */         }
/*    */       }
/*    */     });
/*    */   }
/*    */ 
/*    */   public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
/*    */   {
/* 62 */     Link link = (Link)value;
/*    */ 
/* 64 */     JLabel label = (JLabel)super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
/*    */ 
/* 66 */     label.setText(link.title);
/* 67 */     label.setToolTipText(link.url);
/* 68 */     putClientProperty("[x]", link.url);
/*    */ 
/* 70 */     return label;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.table.renderers.LinkTableCellRenderer
 * JD-Core Version:    0.6.0
 */