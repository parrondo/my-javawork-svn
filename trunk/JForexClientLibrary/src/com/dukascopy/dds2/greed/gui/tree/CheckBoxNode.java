/*    */ package com.dukascopy.dds2.greed.gui.tree;
/*    */ 
/*    */ class CheckBoxNode<V>
/*    */ {
/*    */   private final V value;
/*    */   private boolean selected;
/*    */   private final SelectionListener<V> selectionListener;
/*    */ 
/*    */   public CheckBoxNode(V value, boolean selected, SelectionListener<V> listener)
/*    */   {
/* 17 */     this.value = value;
/* 18 */     this.selected = selected;
/* 19 */     this.selectionListener = listener;
/*    */   }
/*    */ 
/*    */   public boolean isSelected() {
/* 23 */     return this.selected;
/*    */   }
/*    */ 
/*    */   public void setSelected(boolean value) {
/* 27 */     this.selected = value;
/* 28 */     this.selectionListener.selectionChanged(this);
/*    */   }
/*    */ 
/*    */   public V getValue() {
/* 32 */     return this.value;
/*    */   }
/*    */ 
/*    */   public String toString()
/*    */   {
/* 37 */     return getClass().getName() + "[" + this.value + "/" + this.selected + "]";
/*    */   }
/*    */ 
/*    */   public static abstract interface SelectionListener<V>
/*    */   {
/*    */     public abstract void selectionChanged(CheckBoxNode<V> paramCheckBoxNode);
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.tree.CheckBoxNode
 * JD-Core Version:    0.6.0
 */