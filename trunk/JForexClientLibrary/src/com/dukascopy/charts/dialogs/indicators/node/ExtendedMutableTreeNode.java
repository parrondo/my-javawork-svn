/*    */ package com.dukascopy.charts.dialogs.indicators.node;
/*    */ 
/*    */ import java.util.Collections;
/*    */ import java.util.Comparator;
/*    */ import javax.swing.tree.DefaultMutableTreeNode;
/*    */ 
/*    */ public class ExtendedMutableTreeNode extends DefaultMutableTreeNode
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/*    */ 
/*    */   public ExtendedMutableTreeNode()
/*    */   {
/*    */   }
/*    */ 
/*    */   public ExtendedMutableTreeNode(Object userObject)
/*    */   {
/* 21 */     super(userObject);
/*    */   }
/*    */ 
/*    */   public void sortChildren() {
/* 25 */     Collections.sort(this.children, new Comparator()
/*    */     {
/*    */       public int compare(Object o1, Object o2) {
/* 28 */         if ((o1 == null) || (o2 == null)) {
/* 29 */           return 0;
/*    */         }
/*    */ 
/* 32 */         return o1.toString().compareTo(o2.toString());
/*    */       }
/*    */     });
/*    */   }
/*    */ 
/*    */   public void sortChildren(Comparator comparator) {
/* 39 */     Collections.sort(this.children, comparator);
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Charts-5.48.jar
 * Qualified Name:     com.dukascopy.charts.dialogs.indicators.node.ExtendedMutableTreeNode
 * JD-Core Version:    0.6.0
 */