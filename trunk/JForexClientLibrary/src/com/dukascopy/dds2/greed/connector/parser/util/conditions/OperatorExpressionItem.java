/*     */ package com.dukascopy.dds2.greed.connector.parser.util.conditions;
/*     */ 
/*     */ import com.dukascopy.dds2.greed.connector.parser.javacc.ASTNode;
/*     */ import com.dukascopy.dds2.greed.connector.parser.util.Declaration;
/*     */ import com.dukascopy.dds2.greed.connector.parser.util.DeclarationHelpers;
/*     */ 
/*     */ public class OperatorExpressionItem extends ConditionItem
/*     */ {
/*     */   public void add(IConditionItem item)
/*     */   {
/*     */   }
/*     */ 
/*     */   public IConditionItem get(int index)
/*     */   {
/*  19 */     return null;
/*     */   }
/*     */ 
/*     */   public StringBuilder print(int index)
/*     */   {
/*  24 */     StringBuilder buf = new StringBuilder();
/*  25 */     if (!getConditionRoot().isUnar(getNode())) {
/*  26 */       buf.append(" ");
/*     */     }
/*  28 */     buf.append(printNode(getNode()));
/*  29 */     if (!getConditionRoot().isUnar(getNode())) {
/*  30 */       buf.append(" ");
/*     */     }
/*  32 */     return buf;
/*     */   }
/*     */ 
/*     */   public int size()
/*     */   {
/*  38 */     return 0;
/*     */   }
/*     */ 
/*     */   public boolean hasChildren()
/*     */   {
/*  44 */     return false;
/*     */   }
/*     */ 
/*     */   public Declaration getDeclarationRoot()
/*     */   {
/*  50 */     return null;
/*     */   }
/*     */ 
/*     */   public StringBuilder print()
/*     */   {
/*  56 */     return null;
/*     */   }
/*     */ 
/*     */   protected String printNode(ASTNode node)
/*     */   {
/*  67 */     StringBuilder result = new StringBuilder();
/*  68 */     if (node != null) {
/*  69 */       if ((node.getText() != null) && (!node.getText().isEmpty())) {
/*  70 */         result.append(node.getText());
/*     */       }
/*  72 */       else if ((node.getChildren() != null) && (node.getChildren().length > 0)) {
/*  73 */         for (int i = 0; i < node.getChildren().length; i++) {
/*  74 */           ASTNode child = node.getChildren()[i];
/*  75 */           if (child.getId() == 64)
/*     */           {
/*  78 */             result.append(child.getText());
/*  79 */             result.append("toInt(");
/*  80 */             i++; child = node.getChildren()[i];
/*  81 */             result.append(printArrayNode(child));
/*  82 */             result.append(")");
/*  83 */             i++; child = node.getChildren()[i];
/*     */           }
/*  85 */           if (child.getId() == 186) {
/*  86 */             result.append("convertDatetimeToLong(\"");
/*  87 */             result.append(printNode(child));
/*  88 */             result.append("\"");
/*  89 */             result.append(")");
/*     */           }
/*     */           else {
/*  92 */             result.append(printNode(child));
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*  97 */     return result.toString();
/*     */   }
/*     */ 
/*     */   protected String printArrayNode(ASTNode node) {
/* 101 */     StringBuilder result = new StringBuilder();
/* 102 */     if (node != null) {
/* 103 */       for (int i = 0; i < node.getChildren().length; i++) {
/* 104 */         ASTNode child = node.getChildren()[i];
/* 105 */         if (child.getId() == 50)
/* 106 */           result.append(DeclarationHelpers.getAssignmentExpression(child));
/* 107 */         else if (child.getId() == 71)
/* 108 */           result.append(")][toInt(");
/* 109 */         else if (child.getId() == 49) {
/* 110 */           result.append(printArrayNode(child));
/*     */         }
/*     */       }
/*     */     }
/* 114 */     return result.toString();
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Connector-1.1.49.jar
 * Qualified Name:     com.dukascopy.dds2.greed.connector.parser.util.conditions.OperatorExpressionItem
 * JD-Core Version:    0.6.0
 */