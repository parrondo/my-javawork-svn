/*     */ package com.dukascopy.dds2.greed.connector.parser.util;
/*     */ 
/*     */ import com.dukascopy.dds2.greed.connector.parser.javacc.ASTNode;
/*     */ import com.dukascopy.dds2.greed.connector.parser.util.conditions.ConditionRoot;
/*     */ 
/*     */ public class IfStatement extends Statement
/*     */ {
/*     */   ConditionRoot conditionRoot;
/*     */   AssigmentExpressionRoot pairs;
/*  15 */   IfStatement firstIfInChain = this;
/*     */ 
/*  18 */   boolean hasElse = false;
/*     */ 
/*     */   public String startText() {
/*  21 */     StringBuilder buf = new StringBuilder();
/*  22 */     buf.append("if (");
/*     */ 
/*  25 */     buf.append(getConditionRoot().print());
/*     */ 
/*  29 */     buf.append("){\r\n");
/*  30 */     return buf.toString();
/*     */   }
/*     */ 
/*     */   private String getNodeText(ASTNode root) {
/*  34 */     StringBuilder buf = new StringBuilder();
/*  35 */     if (root.getText() != null) {
/*  36 */       if (root.getId() == 64) {
/*  37 */         buf.append(root.getText());
/*  38 */         buf.append("toInt(");
/*  39 */       } else if (root.getId() == 65) {
/*  40 */         buf.append(")");
/*  41 */         buf.append(root.getText());
/*     */       } else {
/*  43 */         buf.append(root.getText());
/*     */       }
/*     */     }
/*  46 */     else for (ASTNode child : root.getChildren()) {
/*  47 */         if (child.getText() != null) {
/*  48 */           if (child.getId() == 64) {
/*  49 */             buf.append(child.getText());
/*  50 */             buf.append("toInt(");
/*  51 */           } else if (child.getId() == 65) {
/*  52 */             buf.append(")");
/*  53 */             buf.append(child.getText());
/*     */           } else {
/*  55 */             buf.append(child.getText());
/*     */           }
/*     */         }
/*  58 */         else buf.append(getNodeText(child));
/*     */       }
/*     */ 
/*     */ 
/*  62 */     return buf.toString();
/*     */   }
/*     */ 
/*     */   private String makeExpression(ASTNode root, StringBuilder buf) {
/*  66 */     for (int index = 0; index < root.getChildren().length; index++) {
/*  67 */       ASTNode child = root.getChildren()[index];
/*  68 */       if ((child.getId() != 50) && (child.getId() != 49)) {
/*  69 */         if ((child.getId() != 93) && (child.getId() != 92) && (child.getId() != 94) && (child.getId() != 95))
/*     */         {
/*  74 */           buf.append(getNodeText(child));
/*     */         }
/*  76 */         else if (root.getChildren().length > index + 2) {
/*  77 */           ASTNode nextChild = root.getChildren()[(index + 2)];
/*  78 */           if ((nextChild.getId() != 93) && (nextChild.getId() != 92) && (nextChild.getId() != 94) && (nextChild.getId() != 95))
/*     */           {
/*  82 */             buf.append(getNodeText(child));
/*     */           } else {
/*  84 */             buf.append(getNodeText(child));
/*  85 */             ASTNode expressionChild = root.getChildren()[(index + 1)];
/*     */ 
/*  87 */             if ((expressionChild.getId() != 50) && (child.getId() != 49))
/*  88 */               buf.append(getNodeText(expressionChild));
/*     */             else {
/*  90 */               makeExpression(expressionChild, buf);
/*     */             }
/*  92 */             buf.append(" && ");
/*     */ 
/*  94 */             if ((expressionChild.getId() != 50) && (child.getId() != 49))
/*  95 */               buf.append(getNodeText(expressionChild));
/*     */             else {
/*  97 */               makeExpression(expressionChild, buf);
/*     */             }
/*  99 */             index++;
/*     */           }
/*     */         } else {
/* 102 */           buf.append(getNodeText(child));
/*     */         }
/*     */       }
/*     */       else {
/* 106 */         makeExpression(child, buf);
/*     */       }
/*     */     }
/* 109 */     return buf.toString();
/*     */   }
/*     */ 
/*     */   public String endText()
/*     */   {
/* 114 */     StringBuilder buf = new StringBuilder();
/* 115 */     if (!isHasElse()) {
/* 116 */       buf.append("}");
/*     */     }
/*     */ 
/* 119 */     return buf.toString();
/*     */   }
/*     */ 
/*     */   public boolean isHasElse() {
/* 123 */     return this.hasElse;
/*     */   }
/*     */ 
/*     */   public void setHasElse(boolean hasElse) {
/* 127 */     this.hasElse = hasElse;
/*     */   }
/*     */ 
/*     */   public IfStatement getFirstIfInChain() {
/* 131 */     return this.firstIfInChain;
/*     */   }
/*     */ 
/*     */   public void setFirstIfInChain(IfStatement firstIfInChain) {
/* 135 */     this.firstIfInChain = firstIfInChain;
/*     */   }
/*     */ 
/*     */   public AssigmentExpressionRoot getPairs()
/*     */   {
/* 140 */     return this.pairs;
/*     */   }
/*     */ 
/*     */   public void setPairs(AssigmentExpressionRoot pairs)
/*     */   {
/* 145 */     this.pairs = pairs;
/*     */   }
/*     */ 
/*     */   public ConditionRoot getConditionRoot() {
/* 149 */     return this.conditionRoot;
/*     */   }
/*     */ 
/*     */   public void setConditionRoot(ConditionRoot conditionRoot) {
/* 153 */     this.conditionRoot = conditionRoot;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Connector-1.1.49.jar
 * Qualified Name:     com.dukascopy.dds2.greed.connector.parser.util.IfStatement
 * JD-Core Version:    0.6.0
 */