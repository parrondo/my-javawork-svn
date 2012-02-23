/*     */ package com.dukascopy.dds2.greed.connector.parser.util;
/*     */ 
/*     */ import com.dukascopy.dds2.greed.connector.parser.javacc.ASTNode;
/*     */ import com.dukascopy.dds2.greed.connector.parser.util.conditions.ConditionRoot;
/*     */ 
/*     */ public class WhileStatement extends Statement
/*     */ {
/*     */   ConditionRoot conditionRoot;
/*     */   AssigmentExpressionRoot pairs;
/*     */ 
/*     */   public String endText()
/*     */   {
/*  13 */     StringBuilder buf = new StringBuilder();
/*  14 */     buf.append("}");
/*  15 */     return buf.toString();
/*     */   }
/*     */ 
/*     */   public String startText()
/*     */   {
/*  20 */     StringBuilder buf = new StringBuilder();
/*  21 */     buf.append("while (");
/*     */ 
/*  23 */     buf.append(getConditionRoot().print());
/*     */ 
/*  26 */     buf.append("){\r\n");
/*  27 */     return buf.toString();
/*     */   }
/*     */ 
/*     */   public String startTextOld() {
/*  31 */     StringBuilder buf = new StringBuilder();
/*  32 */     buf.append("while (");
/*  33 */     ASTNode expressionNode = null;
/*  34 */     ASTNode lparentnesisNode = null;
/*  35 */     for (int i = 0; i < getStartNode().getChildren().length; i++) {
/*  36 */       if ((getStartNode().getChildren()[i].getId() == 50) || (getStartNode().getChildren()[i].getId() == 49)) {
/*  37 */         expressionNode = getStartNode().getChildren()[i];
/*     */       }
/*  39 */       if (getStartNode().getChildren()[i].getId() == 67) {
/*  40 */         lparentnesisNode = getStartNode().getChildren()[i];
/*     */       }
/*  42 */       if ((expressionNode != null) && (lparentnesisNode != null))
/*     */       {
/*     */         break;
/*     */       }
/*     */     }
/*  47 */     buf.append("Bool(");
/*  48 */     makeExpression(expressionNode, buf);
/*  49 */     buf.append(")){\r\n");
/*  50 */     return buf.toString();
/*     */   }
/*     */ 
/*     */   private String getNodeText(ASTNode root) {
/*  54 */     StringBuilder buf = new StringBuilder();
/*  55 */     if (root.getText() != null) {
/*  56 */       if (root.getId() == 64) {
/*  57 */         buf.append(root.getText());
/*  58 */         buf.append("toInt(");
/*  59 */       } else if (root.getId() == 65) {
/*  60 */         buf.append(")");
/*  61 */         buf.append(root.getText());
/*     */       } else {
/*  63 */         buf.append(root.getText());
/*     */       }
/*     */     }
/*  66 */     else for (ASTNode child : root.getChildren()) {
/*  67 */         if (child.getText() != null) {
/*  68 */           if (child.getId() == 64) {
/*  69 */             buf.append(child.getText());
/*  70 */             buf.append("toInt(");
/*  71 */           } else if (child.getId() == 65) {
/*  72 */             buf.append(")");
/*  73 */             buf.append(child.getText());
/*     */           } else {
/*  75 */             buf.append(child.getText());
/*     */           }
/*     */         }
/*  78 */         else buf.append(getNodeText(child));
/*     */       }
/*     */ 
/*     */ 
/*  82 */     return buf.toString();
/*     */   }
/*     */ 
/*     */   private String makeExpression(ASTNode root, StringBuilder buf) {
/*  86 */     if ((root != null) && (root.getChildren() != null)) {
/*  87 */       for (int index = 0; index < root.getChildren().length; index++) {
/*  88 */         ASTNode child = root.getChildren()[index];
/*  89 */         if ((child.getId() != 50) && (child.getId() != 49)) {
/*  90 */           if ((child.getId() != 93) && (child.getId() != 92) && (child.getId() != 94) && (child.getId() != 95))
/*     */           {
/*  95 */             buf.append(getNodeText(child));
/*     */           }
/*  97 */           else if (root.getChildren().length > index + 2) {
/*  98 */             ASTNode nextChild = root.getChildren()[(index + 2)];
/*  99 */             if ((nextChild.getId() != 93) && (nextChild.getId() != 92) && (nextChild.getId() != 94) && (nextChild.getId() != 95))
/*     */             {
/* 103 */               buf.append(getNodeText(child));
/*     */             } else {
/* 105 */               buf.append(getNodeText(child));
/* 106 */               ASTNode expressionChild = root.getChildren()[(index + 1)];
/*     */ 
/* 108 */               if ((expressionChild.getId() != 50) || (expressionChild.getId() != 49))
/* 109 */                 buf.append(getNodeText(expressionChild));
/*     */               else {
/* 111 */                 makeExpression(expressionChild, buf);
/*     */               }
/* 113 */               buf.append(" && ");
/*     */ 
/* 115 */               if ((expressionChild.getId() != 50) || (expressionChild.getId() != 49))
/* 116 */                 buf.append(getNodeText(expressionChild));
/*     */               else {
/* 118 */                 makeExpression(expressionChild, buf);
/*     */               }
/* 120 */               index++;
/*     */             }
/*     */           } else {
/* 123 */             buf.append(getNodeText(child));
/*     */           }
/*     */         }
/*     */         else {
/* 127 */           makeExpression(child, buf);
/*     */         }
/*     */       }
/*     */     }
/* 131 */     return buf.toString();
/*     */   }
/*     */ 
/*     */   public AssigmentExpressionRoot getPairs() {
/* 135 */     return this.pairs;
/*     */   }
/*     */ 
/*     */   public void setPairs(AssigmentExpressionRoot pairs)
/*     */   {
/* 140 */     this.pairs = pairs;
/*     */   }
/*     */ 
/*     */   public ConditionRoot getConditionRoot() {
/* 144 */     return this.conditionRoot;
/*     */   }
/*     */ 
/*     */   public void setConditionRoot(ConditionRoot conditionRoot) {
/* 148 */     this.conditionRoot = conditionRoot;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Connector-1.1.49.jar
 * Qualified Name:     com.dukascopy.dds2.greed.connector.parser.util.WhileStatement
 * JD-Core Version:    0.6.0
 */