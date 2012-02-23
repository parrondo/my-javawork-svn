/*     */ package com.dukascopy.charts.tablebuilder.component.table.renderer;
/*     */ 
/*     */ import com.dukascopy.charts.chartbuilder.ChartState;
/*     */ import com.dukascopy.charts.persistence.ITheme;
/*     */ import com.dukascopy.charts.persistence.ITheme.ChartElement;
/*     */ import com.dukascopy.dds2.greed.gui.table.renderers.AbstractTableCellRenderer;
/*     */ import java.awt.Color;
/*     */ import java.awt.Component;
/*     */ import java.text.SimpleDateFormat;
/*     */ import java.util.Date;
/*     */ import javax.swing.BorderFactory;
/*     */ import javax.swing.JTable;
/*     */ import javax.swing.JTextField;
/*     */ import javax.swing.text.BadLocationException;
/*     */ import javax.swing.text.DefaultHighlighter;
/*     */ import javax.swing.text.DefaultHighlighter.DefaultHighlightPainter;
/*     */ import javax.swing.text.Highlighter;
/*     */ import org.slf4j.Logger;
/*     */ import org.slf4j.LoggerFactory;
/*     */ 
/*     */ public abstract class AbstractDataTableBackgroundRenderer extends AbstractTableCellRenderer
/*     */ {
/*     */   private static final long serialVersionUID = 1L;
/*  29 */   private static final Logger LOGGER = LoggerFactory.getLogger(AbstractDataTableBackgroundRenderer.class);
/*     */ 
/*  31 */   protected DefaultHighlighter.DefaultHighlightPainter highlightPainter = new DefaultHighlighter.DefaultHighlightPainter(new Color(198, 198, 250));
/*     */   private String pattern;
/*     */   private JTextField textField;
/*     */   protected ChartState chartState;
/*     */ 
/*     */   public AbstractDataTableBackgroundRenderer(ChartState chartState)
/*     */   {
/*  41 */     this.chartState = chartState;
/*  42 */     this.textField = createTextField();
/*     */   }
/*     */ 
/*     */   private JTextField createTextField() {
/*  46 */     JTextField txtRendererComponent = new JTextField();
/*  47 */     txtRendererComponent.setBorder(BorderFactory.createEmptyBorder());
/*     */ 
/*  49 */     DefaultHighlighter highlighter = new DefaultHighlighter();
/*  50 */     txtRendererComponent.setHighlighter(highlighter);
/*     */ 
/*  52 */     return txtRendererComponent;
/*     */   }
/*     */ 
/*     */   public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
/*     */   {
/*  63 */     JTextField comp = this.textField;
/*     */ 
/*  65 */     String text = getRenderedText(value);
/*  66 */     comp.setText(text);
/*     */ 
/*  68 */     comp.getHighlighter().removeAllHighlights();
/*     */     try {
/*  70 */       if ((text != null) && (getPattern() != null) && (!text.isEmpty()) && (!getPattern().isEmpty()) && (text.toLowerCase().contains(getPattern().toLowerCase()))) {
/*  71 */         int start = text.indexOf(getPattern());
/*  72 */         int end = start + getPattern().length();
/*  73 */         comp.getHighlighter().addHighlight(start, end, this.highlightPainter);
/*     */       }
/*     */     } catch (BadLocationException e) {
/*  76 */       LOGGER.error(e.getLocalizedMessage(), e);
/*     */     }
/*     */ 
/*  79 */     if (hasFocus) {
/*  80 */       comp.setForeground(Color.YELLOW);
/*  81 */       comp.setBackground(Color.BLUE);
/*  82 */       return comp;
/*     */     }
/*     */ 
/*  85 */     Color TEXT = this.chartState.getTheme().getColor(ITheme.ChartElement.TEXT);
/*  86 */     Color ODD_ROW = this.chartState.getTheme().getColor(ITheme.ChartElement.ODD_ROW);
/*  87 */     Color EVEN_ROW = this.chartState.getTheme().getColor(ITheme.ChartElement.EVEN_ROW);
/*     */ 
/*  89 */     comp.setForeground(TEXT);
/*     */ 
/*  91 */     Color backgroundColor = null;
/*     */ 
/*  93 */     if (row % 2 == 0) {
/*  94 */       backgroundColor = EVEN_ROW;
/*     */     }
/*     */     else {
/*  97 */       backgroundColor = ODD_ROW;
/*     */     }
/*     */ 
/* 100 */     comp.setBackground(backgroundColor);
/*     */ 
/* 102 */     return comp;
/*     */   }
/*     */ 
/*     */   public String getPattern() {
/* 106 */     return this.pattern;
/*     */   }
/*     */ 
/*     */   public void setPattern(String pattern)
/*     */   {
/* 111 */     this.pattern = pattern;
/*     */   }
/*     */   protected abstract SimpleDateFormat getDateFormat();
/*     */ 
/*     */   public String getRenderedText(Object value) {
/* 117 */     String text = "";
/* 118 */     if ((value instanceof Date)) {
/* 119 */       text = getDateFormat().format((Date)value);
/*     */     }
/*     */     else {
/* 122 */       text = String.valueOf(value);
/*     */     }
/* 124 */     return text;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Charts-5.48.jar
 * Qualified Name:     com.dukascopy.charts.tablebuilder.component.table.renderer.AbstractDataTableBackgroundRenderer
 * JD-Core Version:    0.6.0
 */