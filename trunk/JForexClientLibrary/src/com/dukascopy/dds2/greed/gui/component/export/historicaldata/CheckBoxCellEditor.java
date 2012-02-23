/*    */ package com.dukascopy.dds2.greed.gui.component.export.historicaldata;
/*    */ 
/*    */ import java.awt.Color;
/*    */ import java.awt.Component;
/*    */ import java.awt.Dimension;
/*    */ import java.awt.Insets;
/*    */ import javax.swing.DefaultCellEditor;
/*    */ import javax.swing.JCheckBox;
/*    */ import javax.swing.JComponent;
/*    */ import javax.swing.JTable;
/*    */ import javax.swing.table.TableCellEditor;
/*    */ import javax.swing.table.TableCellRenderer;
/*    */ 
/*    */ public class CheckBoxCellEditor extends DefaultCellEditor
/*    */   implements TableCellRenderer, TableCellEditor
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/* 17 */   private JCheckBox checkBox = new JCheckBox();
/*    */ 
/*    */   public CheckBoxCellEditor(int align) {
/* 20 */     super(new JCheckBox());
/*    */ 
/* 22 */     Insets margin = new Insets(0, 0, 0, 0);
/* 23 */     ((JCheckBox)this.editorComponent).setHorizontalAlignment(align);
/* 24 */     ((JCheckBox)this.editorComponent).setMargin(margin);
/*    */ 
/* 26 */     this.checkBox.setBackground(Color.WHITE);
/* 27 */     this.checkBox.setHorizontalAlignment(align);
/* 28 */     this.checkBox.setMargin(margin);
/*    */   }
/*    */ 
/*    */   public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
/*    */   {
/* 33 */     this.checkBox.setFont(table.getFont());
/*    */ 
/* 35 */     if ((value instanceof Boolean))
/* 36 */       this.checkBox.setSelected(((Boolean)value).booleanValue());
/*    */     else {
/* 38 */       this.checkBox.setSelected(false);
/*    */     }
/*    */ 
/* 41 */     return this.checkBox;
/*    */   }
/*    */ 
/*    */   public Dimension getPreferredSize() {
/* 45 */     return this.editorComponent.getPreferredSize();
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-jClient-JForex-2.14.21.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.component.export.historicaldata.CheckBoxCellEditor
 * JD-Core Version:    0.6.0
 */