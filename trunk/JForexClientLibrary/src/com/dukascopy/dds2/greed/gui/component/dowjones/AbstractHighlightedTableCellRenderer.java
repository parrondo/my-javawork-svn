/*    */ package com.dukascopy.dds2.greed.gui.component.dowjones;
/*    */ 
/*    */ import com.dukascopy.dds2.greed.gui.table.renderers.AbstractTableCellRenderer;
/*    */ import com.dukascopy.dds2.greed.util.ObjectUtils;
/*    */ import java.awt.Color;
/*    */ import java.awt.Component;
/*    */ import javax.swing.BorderFactory;
/*    */ import javax.swing.JLabel;
/*    */ import javax.swing.JTable;
/*    */ import javax.swing.text.BadLocationException;
/*    */ import javax.swing.text.DefaultHighlighter;
/*    */ import javax.swing.text.DefaultHighlighter.DefaultHighlightPainter;
/*    */ import javax.swing.text.Highlighter;
/*    */ import javax.swing.text.JTextComponent;
/*    */ import org.slf4j.Logger;
/*    */ import org.slf4j.LoggerFactory;
/*    */ 
/*    */ public abstract class AbstractHighlightedTableCellRenderer<T extends JTextComponent> extends AbstractTableCellRenderer
/*    */ {
/*    */   private static final long serialVersionUID = 7254136129046705929L;
/* 32 */   private static final Logger LOGGER = LoggerFactory.getLogger(AbstractHighlightedTableCellRenderer.class);
/*    */ 
/* 34 */   protected final DefaultHighlighter.DefaultHighlightPainter highlightPainter = new DefaultHighlighter.DefaultHighlightPainter(new Color(198, 198, 250));
/*    */   private String pattern;
/*    */   private T textComponent;
/*    */ 
/*    */   public AbstractHighlightedTableCellRenderer(T textComponent)
/*    */   {
/* 40 */     this.textComponent = textComponent;
/* 41 */     textComponent.setBorder(BorderFactory.createEmptyBorder());
/*    */ 
/* 43 */     DefaultHighlighter highlighter = new DefaultHighlighter();
/* 44 */     textComponent.setHighlighter(highlighter);
/*    */ 
/* 46 */     customizeTextComponent();
/*    */   }
/*    */ 
/*    */   public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
/*    */   {
/* 54 */     JLabel label = (JLabel)super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
/* 55 */     JTextComponent component = getTextComponent();
/* 56 */     component.setText(getValue(table, value, isSelected, hasFocus, row, column));
/* 57 */     component.setForeground(label.getForeground());
/* 58 */     component.setBackground(label.getBackground());
/* 59 */     component.setBorder(label.getBorder());
/* 60 */     component.setFont(label.getFont());
/* 61 */     component.getHighlighter().removeAllHighlights();
/* 62 */     highlightTableCellRendererComponent(component);
/* 63 */     return component;
/*    */   }
/*    */ 
/*    */   public String getPattern()
/*    */   {
/* 70 */     return this.pattern;
/*    */   }
/*    */ 
/*    */   public void setPattern(String pattern)
/*    */   {
/* 77 */     this.pattern = pattern;
/*    */   }
/*    */ 
/*    */   public T getTextComponent()
/*    */   {
/* 84 */     return this.textComponent;
/*    */   }
/*    */ 
/*    */   public void setTextComponent(T textComponent)
/*    */   {
/* 91 */     this.textComponent = textComponent;
/*    */   }
/*    */ 
/*    */   protected void highlightTableCellRendererComponent(JTextComponent component) {
/* 95 */     if ((component != null) && (!ObjectUtils.isNullOrEmpty(component.getText())) && (!ObjectUtils.isNullOrEmpty(this.pattern))) {
/* 96 */       String text = component.getText().toLowerCase();
/* 97 */       this.pattern = this.pattern.toLowerCase();
/*    */       try {
/* 99 */         if (text.contains(this.pattern)) {
/* 100 */           int i = 0;
/* 101 */           int patternLength = this.pattern.length();
/* 102 */           while (i + patternLength <= text.length()) {
/* 103 */             int start = text.indexOf(this.pattern, i);
/* 104 */             if (start < 0) break;
/* 105 */             int end = start + patternLength;
/* 106 */             component.getHighlighter().addHighlight(start, end, this.highlightPainter);
/* 107 */             i = end;
/*    */           }
/*    */         }
/*    */ 
/*    */       }
/*    */       catch (BadLocationException e)
/*    */       {
/* 114 */         LOGGER.error(e.getLocalizedMessage(), e);
/*    */       }
/*    */     }
/*    */   }
/*    */ 
/*    */   protected abstract void customizeTextComponent();
/*    */ 
/*    */   protected abstract String getValue(JTable paramJTable, Object paramObject, boolean paramBoolean1, boolean paramBoolean2, int paramInt1, int paramInt2);
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.dowjones.AbstractHighlightedTableCellRenderer
 * JD-Core Version:    0.6.0
 */