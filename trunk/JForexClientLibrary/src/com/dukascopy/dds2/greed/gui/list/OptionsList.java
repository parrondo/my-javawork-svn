/*    */ package com.dukascopy.dds2.greed.gui.list;
/*    */ 
/*    */ import java.awt.event.MouseAdapter;
/*    */ import java.awt.event.MouseEvent;
/*    */ import java.util.Set;
/*    */ import java.util.Vector;
/*    */ import javax.swing.JList;
/*    */ 
/*    */ public abstract class OptionsList<I> extends JList
/*    */ {
/*    */   private final CheckBoxItem.SelectionListener<I> checkBoxItemSelectionListener;
/*    */ 
/*    */   public OptionsList()
/*    */   {
/* 24 */     setSelectionMode(0);
/* 25 */     setCellRenderer(new CheckBoxListCellRenderer());
/*    */ 
/* 27 */     addMouseListener(new MouseAdapter()
/*    */     {
/*    */       public void mouseClicked(MouseEvent e)
/*    */       {
/* 31 */         CheckBoxItem selectedItem = (CheckBoxItem)OptionsList.this.getSelectedValue();
/* 32 */         selectedItem.setSelected(!selectedItem.isSelected());
/* 33 */         OptionsList.this.repaint();
/*    */       }
/*    */     });
/* 37 */     this.checkBoxItemSelectionListener = new Object()
/*    */     {
/*    */       public void selectionChanged(CheckBoxItem<I> checkBoxItem) {
/* 40 */         Set items = OptionsList.this.getSelectedItems();
/* 41 */         Object item = checkBoxItem.getValue();
/* 42 */         if (checkBoxItem.isSelected())
/* 43 */           items.add(item);
/*    */         else
/* 45 */           items.remove(item);
/*    */       }
/*    */     };
/* 50 */     build();
/*    */   }
/*    */ 
/*    */   private void build() {
/* 54 */     Object[] items = getItems();
/* 55 */     Set selectedItems = getSelectedItems();
/* 56 */     Vector checkBoxItems = new Vector();
/*    */ 
/* 58 */     for (int i = 0; i < items.length; i++) {
/* 59 */       Object item = items[i];
/* 60 */       checkBoxItems.add(new CheckBoxItem(item, selectedItems.contains(item), this.checkBoxItemSelectionListener));
/*    */     }
/*    */ 
/* 63 */     setListData(checkBoxItems);
/*    */   }
/*    */ 
/*    */   public final void reset() {
/* 67 */     build();
/*    */   }
/*    */ 
/*    */   protected abstract I[] getItems();
/*    */ 
/*    */   protected abstract Set<I> getSelectedItems();
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.list.OptionsList
 * JD-Core Version:    0.6.0
 */