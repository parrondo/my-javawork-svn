/*    */ package com.dukascopy.dds2.greed.gui.component.dowjones;
/*    */ 
/*    */ import com.dukascopy.dds2.greed.util.ObjectUtils;
/*    */ import javax.swing.JTable;
/*    */ import javax.swing.JTextField;
/*    */ 
/*    */ public class TextTableCellRenderer extends AbstractHighlightedTableCellRenderer<JTextField>
/*    */ {
/*    */   private static final long serialVersionUID = 4316807842754051726L;
/*    */ 
/*    */   public TextTableCellRenderer(int horizontalAligment)
/*    */   {
/* 19 */     super(new JTextField());
/* 20 */     setHorizontalAlignment(horizontalAligment);
/* 21 */     ((JTextField)getTextComponent()).setHorizontalAlignment(horizontalAligment);
/*    */   }
/*    */ 
/*    */   protected void customizeTextComponent()
/*    */   {
/*    */   }
/*    */ 
/*    */   protected String getValue(JTable table, Object value, boolean isSelected, boolean hasFocus, int rowIndex, int columnIndex)
/*    */   {
/* 36 */     return ObjectUtils.isNullOrEmpty(value) ? "" : value.toString();
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.dowjones.TextTableCellRenderer
 * JD-Core Version:    0.6.0
 */