/*     */ package com.dukascopy.dds2.greed.connector.parser.util;
/*     */ 
/*     */ import com.dukascopy.dds2.greed.connector.parser.javacc.ASTNode;
/*     */ import com.dukascopy.dds2.greed.connector.parser.javacc.Token;
/*     */ import java.util.Map;
/*     */ 
/*     */ public class ForStatement extends Statement
/*     */ {
/*   9 */   boolean counterDeclaratedInParent = false;
/*     */ 
/*  11 */   private ASTNode[] getAssignmentExpressions(ASTNode root) { ASTNode[] expressions = new ASTNode[3];
/*  12 */     int count = 0;
/*  13 */     int index = 2;
/*     */ 
/*  15 */     if (root.getChildren()[index].getId() != 70) {
/*  16 */       if ((root.getChildren()[index].getChildren() != null) && (root.getChildren()[index].getChildren().length > 1)) {
/*  17 */         expressions[count] = root.getChildren()[index];
/*     */       }
/*  19 */       if (root.getChildren()[(index + 1)].getId() == 70) {
/*  20 */         index++;
/*     */       }
/*     */     }
/*  23 */     count++;
/*  24 */     index++;
/*  25 */     if (root.getChildren()[index].getId() != 70) {
/*  26 */       if ((root.getChildren()[index].getId() == 49) || (root.getChildren()[index].getId() == 50)) {
/*  27 */         expressions[count] = root.getChildren()[index];
/*     */       }
/*  29 */       if (root.getChildren()[(index + 1)].getId() == 70) {
/*  30 */         index++;
/*     */       }
/*     */     }
/*  33 */     count++;
/*  34 */     index++;
/*  35 */     if ((root.getChildren()[index].getId() != 67) && (
/*  36 */       (root.getChildren()[index].getId() == 49) || (root.getChildren()[index].getId() == 50))) {
/*  37 */       expressions[count] = root.getChildren()[index];
/*     */     }
/*     */ 
/*  41 */     return expressions; }
/*     */ 
/*     */   private String getAssignmentExpression(ASTNode root)
/*     */   {
/*  45 */     StringBuilder buf = new StringBuilder("");
/*  46 */     if (root != null) {
/*  47 */       Token token = root.getBeginToken();
/*  48 */       Token endToken = root.getEndToken();
/*     */ 
/*  52 */       if ((getType() == null) && (token.kind == 40)) {
/*  53 */         token = token.next;
/*     */       }
/*     */ 
/*  58 */       while ((token != null) && (token != endToken) && (token.kind != 44) && (token.kind != 36))
/*     */       {
/*  60 */         if ((token.kind != 108) && (token.kind != 97) && (token.kind != 117) && (token.kind != 109)) {
/*  61 */           buf.append(token.image);
/*     */         }
/*  63 */         if (token.kind == 48) {
/*  64 */           buf.append("(int)");
/*     */         }
/*  66 */         token = token.next;
/*     */       }
/*  68 */       if (endToken != null) {
/*  69 */         buf.append(endToken.image);
/*     */       }
/*     */     }
/*     */ 
/*  73 */     return buf.toString();
/*     */   }
/*     */ 
/*     */   public String startText()
/*     */   {
/*  78 */     StringBuilder buf = new StringBuilder("");
/*  79 */     buf.append("for(");
/*  80 */     boolean moreThatOne = false;
/*  81 */     int counterCount = 0;
/*  82 */     for (int i = 0; i < getVariables().size(); i++) {
/*  83 */       VariableDeclaration var = (VariableDeclaration)getVariables().values().toArray()[i];
/*     */ 
/*  86 */       if (moreThatOne) {
/*  87 */         buf.append(", ");
/*     */       }
/*  89 */       counterCount++;
/*  90 */       if (!var.isDeclaredUpper())
/*     */       {
/*  92 */         buf.append(" ");
/*     */       }
/*  94 */       buf.append(var.getName());
/*  95 */       if ((var.getValue() != null) && (!var.getValue().isEmpty())) {
/*  96 */         buf.append(new StringBuilder().append("= (int)").append(var.getValue()).toString());
/*     */       }
/*  98 */       moreThatOne = true;
/*     */     }
/*     */ 
/* 101 */     ASTNode[] expressions = getAssignmentExpressions(getStartNode());
/* 102 */     if (counterCount < 1) {
/* 103 */       buf.append(getAssignmentExpression(expressions[0]));
/*     */     }
/* 105 */     buf.append(";");
/* 106 */     buf.append(getAssignmentExpression(expressions[1]));
/* 107 */     buf.append(";");
/* 108 */     buf.append(getAssignmentExpression(expressions[2]));
/* 109 */     buf.append("){\r\n");
/* 110 */     return buf.toString();
/*     */   }
/*     */ 
/*     */   public String endText()
/*     */   {
/* 115 */     return "\r\n}\r\n";
/*     */   }
/*     */ 
/*     */   public boolean isCounterDeclaratedInParent() {
/* 119 */     return this.counterDeclaratedInParent;
/*     */   }
/*     */ 
/*     */   public void setCounterDeclaratedInParent(boolean counterDeclaratedInParent) {
/* 123 */     this.counterDeclaratedInParent = counterDeclaratedInParent;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Connector-1.1.49.jar
 * Qualified Name:     com.dukascopy.dds2.greed.connector.parser.util.ForStatement
 * JD-Core Version:    0.6.0
 */