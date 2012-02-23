/*    */ package com.dukascopy.dds2.greed.gui.tree;
/*    */ 
/*    */ import java.awt.Component;
/*    */ import java.awt.event.ItemEvent;
/*    */ import java.awt.event.ItemListener;
/*    */ import java.awt.event.MouseEvent;
/*    */ import java.util.Arrays;
/*    */ import java.util.EventObject;
/*    */ import java.util.List;
/*    */ import javax.swing.AbstractCellEditor;
/*    */ import javax.swing.JCheckBox;
/*    */ import javax.swing.JTree;
/*    */ import javax.swing.tree.DefaultMutableTreeNode;
/*    */ import javax.swing.tree.TreeCellEditor;
/*    */ import javax.swing.tree.TreePath;
/*    */ 
/*    */ class CheckBoxNodeEditor extends AbstractCellEditor
/*    */   implements TreeCellEditor, ItemListener
/*    */ {
/* 23 */   private final CheckBoxNodeRenderer renderer = new CheckBoxNodeRenderer();
/*    */ 
/*    */   public Object getCellEditorValue()
/*    */   {
/* 27 */     CheckBoxNode checkBoxNode = this.renderer.getCheckBoxNode();
/* 28 */     checkBoxNode.setSelected(this.renderer.isSelected());
/* 29 */     return checkBoxNode;
/*    */   }
/*    */ 
/*    */   public boolean isCellEditable(EventObject event)
/*    */   {
/* 34 */     if ((event instanceof MouseEvent)) {
/* 35 */       MouseEvent mouseEvent = (MouseEvent)event;
/* 36 */       TreePath path = ((JTree)event.getSource()).getPathForLocation(mouseEvent.getX(), mouseEvent.getY());
/* 37 */       if (path != null) {
/* 38 */         Object node = path.getLastPathComponent();
/* 39 */         if ((node != null) && ((node instanceof DefaultMutableTreeNode))) {
/* 40 */           DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode)node;
/* 41 */           return (treeNode.isLeaf()) && ((treeNode.getUserObject() instanceof CheckBoxNode));
/*    */         }
/*    */       }
/*    */     }
/*    */ 
/* 46 */     return false;
/*    */   }
/*    */ 
/*    */   public Component getTreeCellEditorComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row)
/*    */   {
/* 51 */     Component editor = this.renderer.getTreeCellRendererComponent(tree, value, true, expanded, leaf, row, true);
/*    */ 
/* 53 */     if ((editor instanceof JCheckBox)) {
/* 54 */       JCheckBox checkBox = (JCheckBox)editor;
/*    */ 
/* 56 */       if (!Arrays.asList(checkBox.getItemListeners()).contains(this)) {
/* 57 */         checkBox.addItemListener(this);
/*    */       }
/*    */     }
/*    */ 
/* 61 */     return editor;
/*    */   }
/*    */ 
/*    */   public void itemStateChanged(ItemEvent itemEvent)
/*    */   {
/* 66 */     stopCellEditing();
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.tree.CheckBoxNodeEditor
 * JD-Core Version:    0.6.0
 */