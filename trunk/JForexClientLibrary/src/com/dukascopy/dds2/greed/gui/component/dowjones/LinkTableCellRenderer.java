/*    */ package com.dukascopy.dds2.greed.gui.component.dowjones;
/*    */ 
/*    */ import com.dukascopy.dds2.greed.gui.table.renderers.Link;
/*    */ import java.awt.Color;
/*    */ import java.awt.Component;
/*    */ import java.awt.Cursor;
/*    */ import java.awt.Desktop;
/*    */ import java.awt.event.MouseAdapter;
/*    */ import java.awt.event.MouseEvent;
/*    */ import java.awt.event.MouseMotionAdapter;
/*    */ import java.net.URI;
/*    */ import javax.swing.JTable;
/*    */ import javax.swing.JTextField;
/*    */ import javax.swing.text.DefaultHighlighter.DefaultHighlightPainter;
/*    */ import org.slf4j.Logger;
/*    */ import org.slf4j.LoggerFactory;
/*    */ 
/*    */ public class LinkTableCellRenderer extends AbstractHighlightedTableCellRenderer<JTextField>
/*    */ {
/* 28 */   private static final Logger LOGGER = LoggerFactory.getLogger(LinkTableCellRenderer.class);
/*    */   private static final String URL_KEY = "[x]";
/* 31 */   protected DefaultHighlighter.DefaultHighlightPainter highlightPainter = new DefaultHighlighter.DefaultHighlightPainter(new Color(198, 198, 250));
/*    */ 
/*    */   public LinkTableCellRenderer()
/*    */   {
/* 35 */     super(new JTextField());
/*    */   }
/*    */ 
/*    */   public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
/*    */   {
/* 45 */     Link link = (Link)value;
/* 46 */     JTextField comp = (JTextField)super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
/* 47 */     putClientProperty("[x]", link.url);
/* 48 */     return comp;
/*    */   }
/*    */ 
/*    */   protected void customizeTextComponent()
/*    */   {
/* 56 */     ((JTextField)getTextComponent()).addMouseMotionListener(new MouseMotionAdapter()
/*    */     {
/*    */       public void mouseMoved(MouseEvent e) {
/* 59 */         String url = (String)LinkTableCellRenderer.this.getClientProperty("[x]");
/* 60 */         if ((url != null) && (!url.isEmpty())) {
/* 61 */           ((JTable)e.getSource()).setCursor(Cursor.getPredefinedCursor(12));
/* 62 */           return;
/*    */         }
/*    */       }
/*    */     });
/* 66 */     ((JTextField)getTextComponent()).addMouseListener(new MouseAdapter()
/*    */     {
/*    */       public void mouseClicked(MouseEvent e) {
/* 69 */         if (e.getClickCount() >= 2) {
/* 70 */           String url = (String)LinkTableCellRenderer.this.getClientProperty("[x]");
/*    */ 
/* 72 */           if ((url != null) && (!url.isEmpty())) try {
/* 73 */               Desktop.getDesktop().browse(new URI(url));
/*    */             } catch (Exception ex) {
/* 75 */               LinkTableCellRenderer.LOGGER.error("Unable to open url in browser : " + url, ex);
/*    */             }
/*    */         }
/*    */       }
/*    */     });
/*    */   }
/*    */ 
/*    */   protected String getValue(JTable table, Object value, boolean isSelected, boolean hasFocus, int rowIndex, int columnIndex)
/*    */   {
/* 87 */     return ((Link)Link.class.cast(value)).toString();
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.dowjones.LinkTableCellRenderer
 * JD-Core Version:    0.6.0
 */