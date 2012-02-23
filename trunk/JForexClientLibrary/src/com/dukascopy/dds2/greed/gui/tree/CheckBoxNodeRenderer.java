/*    */ package com.dukascopy.dds2.greed.gui.tree;
/*    */ 
/*    */ import java.awt.Component;
/*    */ import java.awt.Font;
/*    */ import javax.swing.JCheckBox;
/*    */ import javax.swing.JTree;
/*    */ import javax.swing.UIManager;
/*    */ import javax.swing.tree.DefaultMutableTreeNode;
/*    */ import javax.swing.tree.DefaultTreeCellRenderer;
/*    */ import javax.swing.tree.TreeCellRenderer;
/*    */ 
/*    */ class CheckBoxNodeRenderer extends JCheckBox
/*    */   implements TreeCellRenderer
/*    */ {
/*    */   private static final String CHECKBOX_NODE_KEY = "[X]";
/* 20 */   private final TreeCellRenderer defaultCellRenderer = new DefaultTreeCellRenderer();
/*    */ 
/*    */   public CheckBoxNodeRenderer() {
/* 23 */     Font font = UIManager.getFont("Tree.font");
/* 24 */     if (font != null) {
/* 25 */       setFont(font);
/*    */     }
/*    */ 
/* 28 */     Boolean drawsFocusBorder = (Boolean)UIManager.get("Tree.drawsFocusBorderAroundIcon");
/* 29 */     setFocusPainted((drawsFocusBorder != null) && (drawsFocusBorder.booleanValue()));
/*    */   }
/*    */ 
/*    */   public CheckBoxNode<?> getCheckBoxNode() {
/* 33 */     return (CheckBoxNode)getClientProperty("[X]");
/*    */   }
/*    */ 
/*    */   public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus)
/*    */   {
/* 45 */     String text = tree.convertValueToText(value, selected, expanded, leaf, row, false);
/*    */ 
/* 47 */     if (!leaf) {
/* 48 */       return this.defaultCellRenderer.getTreeCellRendererComponent(tree, text, selected, expanded, leaf, row, hasFocus);
/*    */     }
/*    */ 
/* 51 */     setText(text);
/* 52 */     setSelected(false);
/* 53 */     setEnabled(tree.isEnabled());
/*    */ 
/* 55 */     if (selected) {
/* 56 */       setForeground(UIManager.getColor("Tree.selectionForeground"));
/* 57 */       setBackground(UIManager.getColor("Tree.selectionBackground"));
/*    */     } else {
/* 59 */       setForeground(UIManager.getColor("Tree.textForeground"));
/* 60 */       setBackground(UIManager.getColor("Tree.textBackground"));
/*    */     }
/*    */ 
/* 63 */     if ((value != null) && ((value instanceof DefaultMutableTreeNode))) {
/* 64 */       Object userObject = ((DefaultMutableTreeNode)value).getUserObject();
/* 65 */       if ((userObject instanceof CheckBoxNode)) {
/* 66 */         CheckBoxNode node = (CheckBoxNode)userObject;
/* 67 */         setText(String.valueOf(node.getValue()));
/* 68 */         setSelected(node.isSelected());
/* 69 */         putClientProperty("[X]", node);
/*    */       }
/*    */     }
/*    */ 
/* 73 */     return this;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.tree.CheckBoxNodeRenderer
 * JD-Core Version:    0.6.0
 */