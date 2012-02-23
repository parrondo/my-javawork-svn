/*     */ package com.dukascopy.dds2.greed.connector.parser.javacc;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ 
/*     */ public class JJTCPPParserState
/*     */ {
/*     */   private List<Node> nodes;
/*     */   private List<Integer> marks;
/*     */   private int sp;
/*     */   private int mk;
/*     */   private boolean node_created;
/*     */ 
/*     */   public JJTCPPParserState()
/*     */   {
/*  15 */     this.nodes = new ArrayList();
/*  16 */     this.marks = new ArrayList();
/*  17 */     this.sp = 0;
/*  18 */     this.mk = 0;
/*     */   }
/*     */ 
/*     */   public boolean nodeCreated()
/*     */   {
/*  25 */     return this.node_created;
/*     */   }
/*     */ 
/*     */   public void reset()
/*     */   {
/*  31 */     this.nodes.clear();
/*  32 */     this.marks.clear();
/*  33 */     this.sp = 0;
/*  34 */     this.mk = 0;
/*     */   }
/*     */ 
/*     */   public Node rootNode()
/*     */   {
/*  40 */     return (Node)this.nodes.get(0);
/*     */   }
/*     */ 
/*     */   public void pushNode(Node n)
/*     */   {
/*  45 */     this.nodes.add(n);
/*  46 */     this.sp += 1;
/*     */   }
/*     */ 
/*     */   public Node popNode()
/*     */   {
/*  52 */     if (--this.sp < this.mk) {
/*  53 */       this.mk = ((Integer)this.marks.remove(this.marks.size() - 1)).intValue();
/*     */     }
/*  55 */     return (Node)this.nodes.remove(this.nodes.size() - 1);
/*     */   }
/*     */ 
/*     */   public Node peekNode()
/*     */   {
/*  60 */     return (Node)this.nodes.get(this.nodes.size() - 1);
/*     */   }
/*     */ 
/*     */   public int nodeArity()
/*     */   {
/*  66 */     return this.sp - this.mk;
/*     */   }
/*     */ 
/*     */   public void clearNodeScope(Node n)
/*     */   {
/*  71 */     while (this.sp > this.mk) {
/*  72 */       popNode();
/*     */     }
/*  74 */     this.mk = ((Integer)this.marks.remove(this.marks.size() - 1)).intValue();
/*     */   }
/*     */ 
/*     */   public void openNodeScope(Node n)
/*     */   {
/*  79 */     this.marks.add(Integer.valueOf(this.mk));
/*  80 */     this.mk = this.sp;
/*  81 */     n.jjtOpen();
/*     */   }
/*     */ 
/*     */   public void closeNodeScope(Node n, int num)
/*     */   {
/*  90 */     this.mk = ((Integer)this.marks.remove(this.marks.size() - 1)).intValue();
/*  91 */     while (num-- > 0) {
/*  92 */       Node c = popNode();
/*  93 */       c.jjtSetParent(n);
/*  94 */       n.jjtAddChild(c, num);
/*     */     }
/*  96 */     n.jjtClose();
/*  97 */     pushNode(n);
/*  98 */     this.node_created = true;
/*     */   }
/*     */ 
/*     */   public void closeNodeScope(Node n, boolean condition)
/*     */   {
/* 108 */     if (condition) {
/* 109 */       int a = nodeArity();
/* 110 */       this.mk = ((Integer)this.marks.remove(this.marks.size() - 1)).intValue();
/* 111 */       while (a-- > 0) {
/* 112 */         Node c = popNode();
/* 113 */         c.jjtSetParent(n);
/* 114 */         n.jjtAddChild(c, a);
/*     */       }
/* 116 */       n.jjtClose();
/* 117 */       pushNode(n);
/* 118 */       this.node_created = true;
/*     */     } else {
/* 120 */       this.mk = ((Integer)this.marks.remove(this.marks.size() - 1)).intValue();
/* 121 */       this.node_created = false;
/*     */     }
/*     */   }
/*     */ 
/*     */   public List<Node> getNodes()
/*     */   {
/* 127 */     return this.nodes;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Connector-1.1.49.jar
 * Qualified Name:     com.dukascopy.dds2.greed.connector.parser.javacc.JJTCPPParserState
 * JD-Core Version:    0.6.0
 */