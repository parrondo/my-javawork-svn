/*    */ package com.dukascopy.dds2.greed.connector.parser.util;
/*    */ 
/*    */ import com.dukascopy.dds2.greed.connector.parser.javacc.ASTNode;
/*    */ import com.dukascopy.dds2.greed.connector.parser.javacc.Node;
/*    */ import java.util.List;
/*    */ 
/*    */ public class JJTreeHelpers
/*    */ {
/*    */   public static void getNodes(int id, Node root, List<Node> result)
/*    */   {
/* 12 */     for (int i = 0; i < root.jjtGetNumChildren(); i++) {
/* 13 */       ASTNode astNode = (ASTNode)root.jjtGetChild(i);
/* 14 */       if ((astNode.getId() == id) && (astNode.getName() != null)) {
/* 15 */         result.add(astNode);
/*    */       }
/* 17 */       if (astNode.jjtGetNumChildren() > 0)
/* 18 */         getNodes(id, astNode, result);
/*    */     }
/*    */   }
/*    */ 
/*    */   public static void getLevelNodes(int id, Node root, List<Node> result)
/*    */   {
/* 27 */     for (int i = 0; i < root.jjtGetNumChildren(); i++) {
/* 28 */       ASTNode astNode = (ASTNode)root.jjtGetChild(i);
/* 29 */       if ((astNode.getId() == id) && (astNode.getName() != null)) {
/* 30 */         result.add(astNode);
/*    */       }
/* 32 */       else if (astNode.jjtGetNumChildren() > 0)
/* 33 */         getLevelNodes(id, astNode, result);
/*    */     }
/*    */   }
/*    */ 
/*    */   public static void getRootNodes(int id, Node root, List<Node> result)
/*    */   {
/* 42 */     if (((ASTNode)root).getId() == 0)
/* 43 */       for (int i = 0; i < root.jjtGetNumChildren(); i++) {
/* 44 */         ASTNode astNode = (ASTNode)root.jjtGetChild(i);
/* 45 */         if ((astNode.getId() == id) && (astNode.getName() != null)) {
/* 46 */           result.add(astNode);
/*    */         }
/*    */         else
/* 49 */           for (int j = 0; j < astNode.jjtGetNumChildren(); j++) {
/* 50 */             ASTNode astNode2 = (ASTNode)astNode.jjtGetChild(j);
/* 51 */             if ((astNode2.getId() == id) && (astNode2.getName() != null))
/* 52 */               result.add(astNode2);
/*    */           }
/*    */       }
/*    */   }
/*    */ 
/*    */   public static void getLevelNodes(Node root, List<Node> result)
/*    */   {
/* 62 */     for (int i = 0; i < root.jjtGetNumChildren(); i++) {
/* 63 */       ASTNode astNode = (ASTNode)root.jjtGetChild(i);
/* 64 */       if (astNode.getName() != null)
/* 65 */         result.add(astNode);
/* 66 */       else if (astNode.jjtGetNumChildren() > 0)
/* 67 */         getLevelNodes(astNode, result);
/*    */     }
/*    */   }
/*    */ 
/*    */   public static ASTNode getParentNode(Node node)
/*    */   {
/* 73 */     ASTNode parent = (ASTNode)node;
/* 74 */     while (parent.jjtGetParent() != null) {
/* 75 */       parent = (ASTNode)parent.jjtGetParent();
/* 76 */       if (parent.getName() != null)
/* 77 */         break;
/*    */     }
/* 79 */     return parent;
/*    */   }
/*    */ 
/*    */   public static void getChildTypes(Node root, List<Integer> types) {
/* 83 */     for (int i = 0; i < root.jjtGetNumChildren(); i++) {
/* 84 */       ASTNode astNode = (ASTNode)root.jjtGetChild(i);
/* 85 */       if (astNode.getName() != null) {
/* 86 */         types.add(new Integer(astNode.getId()));
/*    */       }
/* 88 */       if (astNode.jjtGetNumChildren() > 0)
/* 89 */         getChildTypes(astNode, types);
/*    */     }
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Connector-1.1.49.jar
 * Qualified Name:     com.dukascopy.dds2.greed.connector.parser.util.JJTreeHelpers
 * JD-Core Version:    0.6.0
 */