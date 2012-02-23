/*    */ package com.dukascopy.dds2.greed.gui.list;
/*    */ 
/*    */ import java.awt.Component;
/*    */ import java.awt.Rectangle;
/*    */ import javax.swing.JCheckBox;
/*    */ import javax.swing.JList;
/*    */ import javax.swing.ListCellRenderer;
/*    */ 
/*    */ class CheckBoxListCellRenderer extends JCheckBox
/*    */   implements ListCellRenderer
/*    */ {
/*    */   public CheckBoxListCellRenderer()
/*    */   {
/* 17 */     setFocusPainted(true);
/*    */   }
/*    */ 
/*    */   public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus)
/*    */   {
/* 28 */     setFont(list.getFont());
/* 29 */     setBackground(list.getBackground());
/*    */ 
/* 31 */     CheckBoxItem checkBoxItem = (CheckBoxItem)value;
/*    */ 
/* 33 */     setText(checkBoxItem.getValue().toString());
/* 34 */     setSelected(checkBoxItem.isSelected());
/* 35 */     setEnabled(list.isEnabled());
/*    */ 
/* 37 */     return this;
/*    */   }
/*    */ 
/*    */   public void validate()
/*    */   {
/*    */   }
/*    */ 
/*    */   public void invalidate()
/*    */   {
/*    */   }
/*    */ 
/*    */   public void repaint()
/*    */   {
/*    */   }
/*    */ 
/*    */   public void revalidate()
/*    */   {
/*    */   }
/*    */ 
/*    */   public void repaint(long tm, int x, int y, int width, int height)
/*    */   {
/*    */   }
/*    */ 
/*    */   public void repaint(Rectangle r)
/*    */   {
/*    */   }
/*    */ 
/*    */   public void firePropertyChange(String propertyName, byte oldValue, byte newValue)
/*    */   {
/*    */   }
/*    */ 
/*    */   public void firePropertyChange(String propertyName, char oldValue, char newValue)
/*    */   {
/*    */   }
/*    */ 
/*    */   public void firePropertyChange(String propertyName, short oldValue, short newValue)
/*    */   {
/*    */   }
/*    */ 
/*    */   public void firePropertyChange(String propertyName, int oldValue, int newValue)
/*    */   {
/*    */   }
/*    */ 
/*    */   public void firePropertyChange(String propertyName, long oldValue, long newValue)
/*    */   {
/*    */   }
/*    */ 
/*    */   public void firePropertyChange(String propertyName, float oldValue, float newValue)
/*    */   {
/*    */   }
/*    */ 
/*    */   public void firePropertyChange(String propertyName, double oldValue, double newValue)
/*    */   {
/*    */   }
/*    */ 
/*    */   public void firePropertyChange(String propertyName, boolean oldValue, boolean newValue)
/*    */   {
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.list.CheckBoxListCellRenderer
 * JD-Core Version:    0.6.0
 */