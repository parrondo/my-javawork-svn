/*    */ package com.dukascopy.dds2.greed.gui.list;
/*    */ 
/*    */ class CheckBoxItem<V>
/*    */ {
/*    */   private final V value;
/*    */   private boolean selected;
/*    */   private final SelectionListener<V> selectionListener;
/*    */ 
/*    */   public CheckBoxItem(V value, boolean selected, SelectionListener<V> listener)
/*    */   {
/* 18 */     this.value = value;
/* 19 */     this.selected = selected;
/* 20 */     this.selectionListener = listener;
/*    */   }
/*    */ 
/*    */   public boolean isSelected() {
/* 24 */     return this.selected;
/*    */   }
/*    */ 
/*    */   public void setSelected(boolean value) {
/* 28 */     this.selected = value;
/* 29 */     this.selectionListener.selectionChanged(this);
/*    */   }
/*    */ 
/*    */   public V getValue() {
/* 33 */     return this.value;
/*    */   }
/*    */ 
/*    */   public String toString()
/*    */   {
/* 38 */     return getClass().getName() + "[" + this.value + "/" + this.selected + "]";
/*    */   }
/*    */ 
/*    */   public static abstract interface SelectionListener<V>
/*    */   {
/*    */     public abstract void selectionChanged(CheckBoxItem<V> paramCheckBoxItem);
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.list.CheckBoxItem
 * JD-Core Version:    0.6.0
 */