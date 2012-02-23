/*    */ package com.dukascopy.dds2.greed.connector.parser.javacc;
/*    */ 
/*    */ import java.util.Iterator;
/*    */ 
/*    */ public class ASTNodeIterator
/*    */   implements Iterator<ASTNode>
/*    */ {
/*    */   private Node[] children;
/*    */   int i;
/*    */ 
/*    */   public ASTNodeIterator(Node[] _children)
/*    */   {
/* 12 */     this.children = _children;
/* 13 */     this.i = 0;
/*    */   }
/*    */ 
/*    */   public boolean hasNext()
/*    */   {
/* 18 */     return this.i < this.children.length;
/*    */   }
/*    */ 
/*    */   public ASTNode next()
/*    */   {
/* 23 */     this.i += 1;
/* 24 */     return (ASTNode)this.children[(this.i - 1)];
/*    */   }
/*    */ 
/*    */   public void remove()
/*    */   {
/* 29 */     throw new RuntimeException("not implemented");
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Connector-1.1.49.jar
 * Qualified Name:     com.dukascopy.dds2.greed.connector.parser.javacc.ASTNodeIterator
 * JD-Core Version:    0.6.0
 */