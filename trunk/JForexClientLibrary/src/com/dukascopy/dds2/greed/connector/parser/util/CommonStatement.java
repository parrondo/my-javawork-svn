/*     */ package com.dukascopy.dds2.greed.connector.parser.util;
/*     */ 
/*     */ import com.dukascopy.dds2.greed.connector.parser.javacc.ASTNode;
/*     */ import com.dukascopy.dds2.greed.connector.parser.javacc.Token;
/*     */ 
/*     */ public class CommonStatement extends Statement
/*     */ {
/*   8 */   boolean isVariableInitialization = false;
/*   9 */   boolean isUnary = false;
/*     */   ASTNode operatorNode;
/*     */   VariableDeclaration variable;
/*     */ 
/*     */   public String endText()
/*     */   {
/*  15 */     return ";\r\n";
/*     */   }
/*     */ 
/*     */   public String startText()
/*     */   {
/*  20 */     if ((this.variable != null) && ((getType() == null) || (getType().isEmpty()))) {
/*  21 */       setType(this.variable.getType());
/*     */     }
/*  23 */     StringBuilder buf = new StringBuilder("");
/*     */ 
/*  26 */     String expression = null;
/*  27 */     if (isUnary()) {
/*  28 */       expression = DeclarationHelpers.getAssignmentExpression(getExpressionNode());
/*  29 */       buf.append(expression);
/*  30 */     } else if ((this.variable != null) && (!this.variable.isArray))
/*     */     {
/*  32 */       if (getExpressionNode() != null) {
/*  33 */         if (DeclarationHelpers.isiCustomAssignmentExpression(this.expressionNode))
/*  34 */           expression = DeclarationHelpers.getiCustomAssignmentExpression(this.expressionNode);
/*     */         else {
/*  36 */           expression = DeclarationHelpers.getAssignmentExpression(this.expressionNode);
/*     */         }
/*     */       }
/*  39 */       if (((expression == null) || (expression.isEmpty())) && (getVariable().getExpressionNode() != null)) {
/*  40 */         expression = DeclarationHelpers.getAssignmentExpression(getVariable().getExpressionNode());
/*     */       }
/*  42 */       if ((expression == null) || (expression.isEmpty())) {
/*  43 */         setType(getVariable().getType());
/*  44 */         if (getType().startsWith("bool")) {
/*  45 */           boolean isInteger = true;
/*     */           try {
/*  47 */             new Integer(expression);
/*     */           } catch (Exception ex) {
/*  49 */             isInteger = false;
/*     */           }
/*  51 */           if (isInteger) {
/*  52 */             setType("int");
/*     */           }
/*     */         }
/*     */       }
/*  56 */       if (expression != null) {
/*  57 */         buf.append(this.variable.getName());
/*  58 */         buf.append(" ");
/*  59 */         if (getOperatorNode() != null)
/*  60 */           buf.append(getOperatorNode().getText());
/*     */         else {
/*  62 */           buf.append("=");
/*     */         }
/*  64 */         buf.append(" ");
/*  65 */         buf.append(getTypedValue(expression));
/*     */       }
/*     */     }
/*  68 */     else if ((getOperatorNode() != null) && (getExpressionNode() != null) && (getExpressionNode().getChildren() != null) && (getExpressionNode().getChildren().length == 3))
/*     */     {
/*  70 */       buf.append(DeclarationHelpers.getAssignmentExpression(getExpressionNode().getChildren()[0]));
/*  71 */       if (getOperatorNode() != null)
/*  72 */         buf.append(getOperatorNode().getText());
/*     */       else {
/*  74 */         buf.append("=");
/*     */       }
/*  76 */       buf.append(getTypedValue(DeclarationHelpers.getAssignmentExpression(getExpressionNode().getChildren()[2])));
/*     */     } else {
/*  78 */       Token startToken = null;
/*  79 */       Token endToken = null;
/*     */ 
/*  81 */       if (getStartNode() != null) {
/*  82 */         startToken = getStartNode().getBeginToken();
/*  83 */         endToken = getStartNode().getEndToken();
/*     */       } else {
/*  85 */         startToken = getFirstToken();
/*  86 */         endToken = getLastToken();
/*     */       }
/*     */ 
/*  89 */       boolean squareOpened = false;
/*  90 */       while (startToken != endToken) {
/*  91 */         if (startToken.kind == 38) {
/*  92 */           squareOpened = true;
/*  93 */           buf.append(startToken.image);
/*  94 */           buf.append("toInt(");
/*  95 */         } else if (startToken.kind == 45) {
/*  96 */           if (squareOpened)
/*  97 */             buf.append(")][toInt(");
/*     */           else
/*  99 */             buf.append(startToken.image);
/*     */         }
/* 101 */         else if (startToken.kind == 39) {
/* 102 */           squareOpened = false;
/* 103 */           buf.append(")");
/* 104 */           buf.append(startToken.image);
/* 105 */         } else if (startToken.kind == 48) {
/* 106 */           buf.append(startToken.image);
/* 107 */           if (this.variable != null) {
/* 108 */             buf.append("(");
/* 109 */             buf.append(this.variable.getType());
/* 110 */             buf.append(")");
/*     */           }
/* 112 */         } else if ((startToken.kind == 145) || (startToken.kind == 146) || (startToken.kind == 149))
/*     */         {
/* 114 */           buf.append(DeclarationHelpers.normalizeInt(startToken.image));
/*     */         } else {
/* 116 */           buf.append(startToken.image);
/*     */         }
/* 118 */         startToken = startToken.next;
/*     */       }
/* 120 */       if (endToken.kind != 44) {
/* 121 */         buf.append(endToken.image);
/*     */       }
/*     */     }
/*     */ 
/* 125 */     return buf.toString();
/*     */   }
/*     */ 
/*     */   private String getTypedValue(String expression) {
/* 129 */     StringBuilder result = new StringBuilder();
/* 130 */     String type = getType();
/* 131 */     result.append(DeclarationHelpers.getTypedValue(expression, type));
/* 132 */     return result.toString();
/*     */   }
/*     */ 
/*     */   public VariableDeclaration getVariable() {
/* 136 */     return this.variable;
/*     */   }
/*     */ 
/*     */   public void setVariable(VariableDeclaration variable) {
/* 140 */     this.variable = variable;
/*     */   }
/*     */ 
/*     */   public boolean isVariableInitialization() {
/* 144 */     return this.isVariableInitialization;
/*     */   }
/*     */ 
/*     */   public void setVariableInitialization(boolean isVariableInitialization) {
/* 148 */     this.isVariableInitialization = isVariableInitialization;
/*     */   }
/*     */ 
/*     */   public boolean isUnary() {
/* 152 */     return this.isUnary;
/*     */   }
/*     */ 
/*     */   public void setUnary(boolean isUnary) {
/* 156 */     this.isUnary = isUnary;
/*     */   }
/*     */ 
/*     */   public ASTNode getOperatorNode() {
/* 160 */     return this.operatorNode;
/*     */   }
/*     */ 
/*     */   public void setOperatorNode(ASTNode operatorNode) {
/* 164 */     this.operatorNode = operatorNode;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Connector-1.1.49.jar
 * Qualified Name:     com.dukascopy.dds2.greed.connector.parser.util.CommonStatement
 * JD-Core Version:    0.6.0
 */