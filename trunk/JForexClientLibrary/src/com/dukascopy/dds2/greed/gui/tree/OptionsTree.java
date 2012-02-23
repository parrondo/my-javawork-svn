/*     */ package com.dukascopy.dds2.greed.gui.tree;
/*     */ 
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Set;
/*     */ import javax.swing.JTree;
/*     */ import javax.swing.event.TreeExpansionEvent;
/*     */ import javax.swing.event.TreeExpansionListener;
/*     */ import javax.swing.tree.DefaultMutableTreeNode;
/*     */ import javax.swing.tree.DefaultTreeModel;
/*     */ import javax.swing.tree.TreeNode;
/*     */ import javax.swing.tree.TreePath;
/*     */ 
/*     */ public abstract class OptionsTree<R, I> extends JTree
/*     */ {
/*     */   private final DefaultMutableTreeNode rootNode;
/*     */   private final CheckBoxNode.SelectionListener<I> checkBoxNodeSelectionListener;
/*     */ 
/*     */   protected OptionsTree()
/*     */   {
/*  27 */     this.rootNode = new DefaultMutableTreeNode();
/*     */ 
/*  29 */     this.checkBoxNodeSelectionListener = new Object()
/*     */     {
/*     */       public void selectionChanged(CheckBoxNode<I> checkBoxNode) {
/*  32 */         Set items = OptionsTree.this.getSelectedItems();
/*  33 */         Object item = checkBoxNode.getValue();
/*  34 */         if (checkBoxNode.isSelected())
/*  35 */           items.add(item);
/*     */         else
/*  37 */           items.remove(item);
/*     */       }
/*     */     };
/*  41 */     TreePath expandTreePath = build();
/*     */ 
/*  43 */     setModel(new DefaultTreeModel(this.rootNode));
/*  44 */     setRootVisible(false);
/*  45 */     setToggleClickCount(1);
/*  46 */     setAutoscrolls(true);
/*  47 */     setScrollsOnExpand(true);
/*  48 */     setExpandsSelectedPaths(true);
/*  49 */     setEditable(true);
/*     */ 
/*  51 */     setCellRenderer(new CheckBoxNodeRenderer());
/*  52 */     setCellEditor(new CheckBoxNodeEditor());
/*     */ 
/*  54 */     addTreeExpansionListener(new TreeExpansionListener()
/*     */     {
/*     */       public void treeExpanded(TreeExpansionEvent event) {
/*  57 */         collapse(event.getPath().getLastPathComponent());
/*     */       }
/*     */ 
/*     */       public void treeCollapsed(TreeExpansionEvent event)
/*     */       {
/*     */       }
/*     */ 
/*     */       private void collapse(Object excludeNode)
/*     */       {
/*  66 */         for (int i = 0; i < OptionsTree.this.rootNode.getChildCount(); i++) {
/*  67 */           TreeNode node = OptionsTree.this.rootNode.getChildAt(i);
/*     */ 
/*  69 */           if (node != excludeNode)
/*  70 */             OptionsTree.this.collapsePath(new TreePath(new Object[] { OptionsTree.access$000(OptionsTree.this), node }));
/*     */         }
/*     */       }
/*     */     });
/*  76 */     expandPath(expandTreePath);
/*     */   }
/*     */ 
/*     */   protected final TreePath build() {
/*  80 */     TreePath pathToExpand = null;
/*     */ 
/*  82 */     this.rootNode.removeAllChildren();
/*  83 */     for (Object root : getRoots()) {
/*  84 */       DefaultMutableTreeNode node = new DefaultMutableTreeNode(String.valueOf(root));
/*     */ 
/*  86 */       for (Iterator i$ = getItems(root).iterator(); i$.hasNext(); ) { Object item = i$.next();
/*  87 */         boolean selected = getSelectedItems().contains(item);
/*  88 */         DefaultMutableTreeNode childNode = new DefaultMutableTreeNode(new CheckBoxNode(item, selected, this.checkBoxNodeSelectionListener));
/*     */ 
/*  95 */         node.add(childNode);
/*     */ 
/*  97 */         if ((pathToExpand == null) && (selected)) {
/*  98 */           pathToExpand = new TreePath(new Object[] { this.rootNode, node });
/*     */         }
/*     */       }
/*     */ 
/* 102 */       this.rootNode.add(node);
/*     */     }
/*     */ 
/* 105 */     return pathToExpand;
/*     */   }
/*     */ 
/*     */   public final void reset() {
/* 109 */     build();
/* 110 */     setModel(new DefaultTreeModel(this.rootNode));
/*     */   }
/*     */ 
/*     */   protected abstract Set<I> getSelectedItems();
/*     */ 
/*     */   protected abstract R[] getRoots();
/*     */ 
/*     */   protected abstract List<I> getItems(R paramR);
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.dds2.greed.gui.tree.OptionsTree
 * JD-Core Version:    0.6.0
 */