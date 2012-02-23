/*    */ package com.dukascopy.dds2.greed.connector.parser.javacc;
/*    */ 
/*    */ import java.io.PrintStream;
/*    */ 
/*    */ public class SimpleNode
/*    */   implements Node
/*    */ {
/*    */   protected Node parent;
/*    */   protected Node[] children;
/*    */   protected int id;
/*    */   protected Object value;
/*    */   protected CPPParser parser;
/*    */ 
/*    */   public SimpleNode(int i)
/*    */   {
/* 15 */     this.id = i;
/*    */   }
/*    */ 
/*    */   public SimpleNode(CPPParser p, int i) {
/* 19 */     this(i);
/* 20 */     this.parser = p;
/*    */   }
/*    */   public void jjtOpen() {
/*    */   }
/*    */ 
/*    */   public void jjtClose() {
/*    */   }
/*    */ 
/*    */   public void jjtSetParent(Node n) {
/* 29 */     this.parent = n; } 
/* 30 */   public Node jjtGetParent() { return this.parent; }
/*    */ 
/*    */   public void jjtAddChild(Node n, int i) {
/* 33 */     if (this.children == null) {
/* 34 */       this.children = new Node[i + 1];
/* 35 */     } else if (i >= this.children.length) {
/* 36 */       Node[] c = new Node[i + 1];
/* 37 */       System.arraycopy(this.children, 0, c, 0, this.children.length);
/* 38 */       this.children = c;
/*    */     }
/* 40 */     this.children[i] = n;
/*    */   }
/*    */ 
/*    */   public Node jjtGetChild(int i) {
/* 44 */     return this.children[i];
/*    */   }
/*    */ 
/*    */   public int jjtGetNumChildren() {
/* 48 */     return this.children == null ? 0 : this.children.length;
/*    */   }
/*    */   public void jjtSetValue(Object value) {
/* 51 */     this.value = value; } 
/* 52 */   public Object jjtGetValue() { return this.value;
/*    */   }
/*    */ 
/*    */   public String toString()
/*    */   {
/* 60 */     return CPPParserTreeConstants.jjtNodeName[this.id]; } 
/* 61 */   public String toString(String prefix) { return prefix + toString();
/*    */   }
/*    */ 
/*    */   public void dump(String prefix)
/*    */   {
/* 67 */     System.out.println(toString(prefix));
/* 68 */     if (this.children != null)
/* 69 */       for (int i = 0; i < this.children.length; i++) {
/* 70 */         SimpleNode n = (SimpleNode)this.children[i];
/* 71 */         if (n != null)
/* 72 */           n.dump(prefix + " ");
/*    */       }
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Connector-1.1.49.jar
 * Qualified Name:     com.dukascopy.dds2.greed.connector.parser.javacc.SimpleNode
 * JD-Core Version:    0.6.0
 */