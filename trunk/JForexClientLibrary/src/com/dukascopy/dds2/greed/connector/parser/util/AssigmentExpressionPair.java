/*     */ package com.dukascopy.dds2.greed.connector.parser.util;
/*     */ 
/*     */ import com.dukascopy.api.connector.helpers.ReflectionHelpers;
/*     */ import com.dukascopy.dds2.greed.connector.helpers.ArrayHelpers;
/*     */ import com.dukascopy.dds2.greed.connector.parser.javacc.ASTNode;
/*     */ import com.dukascopy.dds2.greed.connector.parser.javacc.ParserCallback;
/*     */ import java.io.PrintStream;
/*     */ import java.lang.reflect.Method;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ 
/*     */ public class AssigmentExpressionPair
/*     */ {
/*  18 */   private static final String[] SYMBOL_FUNCTIONS = { "Symbol", "Instrument", "OrderSymbol" };
/*  19 */   private static final String[] SYMBOL_OBJECT_MASK = { "ymbol", "nstrument", "sy" };
/*  20 */   List<AssigmentExpressionPair> pairs = new ArrayList();
/*     */ 
/*  22 */   boolean isCastExpression = false;
/*  23 */   boolean isCastExpressionInLRParnesis = false;
/*     */   AssigmentExpressionRoot pairRoot;
/*     */   AssigmentExpressionPair parent;
/*  27 */   List<ASTNode> firstNodeList = new ArrayList();
/*  28 */   List<ASTNode> secondNodeList = new ArrayList();
/*     */   ASTNode operatorBetweenPairsNode;
/*     */   ASTNode operatorAfterPairsNode;
/*     */ 
/*     */   public AssigmentExpressionPair(AssigmentExpressionRoot root)
/*     */   {
/*  34 */     this.pairRoot = root;
/*     */   }
/*     */ 
/*     */   public ASTNode getFirstNode() {
/*  38 */     if (this.firstNodeList.size() > 0) {
/*  39 */       return (ASTNode)this.firstNodeList.get(0);
/*     */     }
/*  41 */     return null;
/*     */   }
/*     */ 
/*     */   public void setFirstNode(ASTNode firstNode) {
/*  45 */     this.firstNodeList.add(0, firstNode);
/*     */   }
/*     */   public void setFirstNodeToList(ASTNode firstNode) {
/*  48 */     this.firstNodeList.add(firstNode);
/*     */   }
/*     */   public ASTNode getSecondNode() {
/*  51 */     if (this.secondNodeList.size() > 0) {
/*  52 */       return (ASTNode)this.secondNodeList.get(0);
/*     */     }
/*  54 */     return null;
/*     */   }
/*     */ 
/*     */   public void setSecondNode(ASTNode secondNode) {
/*  58 */     this.secondNodeList.add(0, secondNode);
/*     */   }
/*     */   public void setSecondNodeToList(ASTNode secondNode) {
/*  61 */     this.secondNodeList.add(secondNode);
/*     */   }
/*     */ 
/*     */   public boolean hasChildren() {
/*  65 */     return (this.pairs != null) && (this.pairs.size() > 0);
/*     */   }
/*     */   public void addChild(AssigmentExpressionPair pair) {
/*  68 */     this.pairs.add(pair);
/*     */   }
/*     */   public ASTNode getOperatorBetweenPairsNode() {
/*  71 */     return this.operatorBetweenPairsNode;
/*     */   }
/*     */   public void setOperatorBetweenPairsNode(ASTNode operatorBetweenPairsNode) {
/*  74 */     this.operatorBetweenPairsNode = operatorBetweenPairsNode;
/*     */   }
/*     */   public ASTNode getOperatorAfterPairsNode() {
/*  77 */     return this.operatorAfterPairsNode;
/*     */   }
/*     */   public void setOperatorAfterPairsNode(ASTNode operatorAfterPairsNode) {
/*  80 */     this.operatorAfterPairsNode = operatorAfterPairsNode;
/*     */   }
/*     */ 
/*     */   protected String printNode(ASTNode node) {
/*  84 */     StringBuilder result = new StringBuilder();
/*  85 */     if (node != null) {
/*  86 */       if ((node.getText() != null) && (!node.getText().isEmpty())) {
/*  87 */         result.append(node.getText());
/*     */       }
/*  89 */       else if ((node.getChildren() != null) && (node.getChildren().length > 0)) {
/*  90 */         for (int i = 0; i < node.getChildren().length; i++) {
/*  91 */           ASTNode child = node.getChildren()[i];
/*  92 */           if (child.getId() == 64)
/*     */           {
/*  95 */             result.append(child.getText());
/*  96 */             result.append("toInt(");
/*  97 */             i++; child = node.getChildren()[i];
/*  98 */             result.append(printArrayNode(child));
/*  99 */             result.append(")");
/* 100 */             i++; child = node.getChildren()[i];
/*     */           }
/* 102 */           if (child.getId() == 186) {
/* 103 */             result.append("convertDatetimeToLong(\"");
/* 104 */             result.append(printNode(child));
/* 105 */             result.append("\"");
/* 106 */             result.append(")");
/*     */           }
/*     */           else {
/* 109 */             result.append(printNode(child));
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/* 114 */     return result.toString();
/*     */   }
/*     */ 
/*     */   protected String printArrayNode(ASTNode node) {
/* 118 */     StringBuilder result = new StringBuilder();
/* 119 */     if (node != null) {
/* 120 */       for (int i = 0; i < node.getChildren().length; i++) {
/* 121 */         ASTNode child = node.getChildren()[i];
/* 122 */         if (child.getId() == 50)
/* 123 */           result.append(DeclarationHelpers.getAssignmentExpression(child));
/* 124 */         else if (child.getId() == 71)
/* 125 */           result.append(")][toInt(");
/* 126 */         else if (child.getId() == 49) {
/* 127 */           result.append(printArrayNode(child));
/*     */         }
/*     */       }
/*     */     }
/* 131 */     return result.toString();
/*     */   }
/*     */ 
/*     */   protected String printNode(List<ASTNode> nodes) {
/* 135 */     StringBuilder result = new StringBuilder();
/* 136 */     if (nodes != null) {
/* 137 */       for (ASTNode child : nodes) {
/* 138 */         result.append(printNode(child));
/*     */       }
/*     */     }
/* 141 */     return result.toString();
/*     */   }
/*     */ 
/*     */   private boolean getNodeNumeric(ASTNode root)
/*     */   {
/* 146 */     boolean result = false;
/* 147 */     String type = getNodeResultType(root);
/* 148 */     if ((type != null) && (!type.isEmpty()) && (
/* 149 */       (type.equals("int")) || (type.equals("long")) || (type.equals("double")) || (type.equals("double")) || (type.equals("byte"))))
/*     */     {
/* 152 */       result = true;
/*     */     }
/*     */ 
/* 156 */     return result;
/*     */   }
/*     */ 
/*     */   private String getNodeResultType(ASTNode root)
/*     */   {
/* 165 */     String type = "";
/* 166 */     boolean isVariable = false;
/* 167 */     boolean isFunction = false;
/* 168 */     boolean isConstant = false;
/* 169 */     ASTNode node = root;
/* 170 */     if ((node != null) && (node.getId() == 52) && (node.getChildren() != null)) {
/* 171 */       if (node.getChildren().length == 1) {
/* 172 */         node = node.getChildren()[0];
/* 173 */         if (node.getId() == 168) {
/* 174 */           isVariable = true;
/*     */         } else {
/* 176 */           if ((node.getChildren() != null) && (node.getChildren().length > 0)) {
/* 177 */             node = node.getChildren()[0];
/*     */           }
/* 179 */           isConstant = true;
/*     */         }
/* 181 */       } else if (node.getChildren().length > 2) {
/* 182 */         if ((node.getChildren()[0].getId() == 168) && (node.getChildren()[1].getId() == 66))
/*     */         {
/* 184 */           node = node.getChildren()[0];
/* 185 */           isFunction = true;
/* 186 */         } else if ((node.getChildren()[0].getId() == 168) && (node.getChildren()[1].getId() == 64))
/*     */         {
/* 188 */           node = node.getChildren()[0];
/* 189 */           isVariable = true;
/*     */         }
/*     */       }
/*     */     }
/* 193 */     if (isVariable) {
/* 194 */       VariableDeclaration variable = (VariableDeclaration)this.pairRoot.getCallback().getCurrentBranchVariableDeclaration(node.getText(), this.pairRoot.getDeclRoot());
/* 195 */       if (variable != null)
/* 196 */         type = variable.getType();
/*     */       else {
/* 198 */         variable = (VariableDeclaration)this.pairRoot.getCallback().variables.get(node.getText());
/*     */       }
/* 200 */       if (variable != null) {
/* 201 */         type = variable.getType();
/*     */       }
/*     */     }
/* 204 */     else if (isFunction) {
/* 205 */       FunctionDeclaration function = (FunctionDeclaration)this.pairRoot.getCallback().functions.get(node.getText());
/* 206 */       if (function != null) {
/* 207 */         type = function.getType();
/*     */       } else {
/* 209 */         Method method = ReflectionHelpers.getAbstractConnectorMethod(node.getText());
/* 210 */         if (method != null)
/* 211 */           type = method.getReturnType().getName();
/*     */       }
/*     */     }
/* 214 */     else if (isConstant) {
/* 215 */       if ((node.getId() == 161) || (node.getId() == 162))
/* 216 */         type = "bool";
/* 217 */       else if ((node.getId() == 176) || (node.getId() == 175) || (node.getId() == 171) || (node.getId() == 180) || (node.getId() == 173) || (node.getId() == 182))
/*     */       {
/* 223 */         type = "int";
/* 224 */       } else if ((node.getId() == 177) || (node.getId() == 172) || (node.getId() == 181) || (node.getId() == 174) || (node.getId() == 183))
/*     */       {
/* 229 */         type = "long";
/* 230 */       } else if ((node.getId() == 184) || (node.getId() == 185))
/* 231 */         type = "double";
/*     */       else {
/* 233 */         System.out.println(new StringBuilder().append("!! node.getId()").append(node.getId()).append(" : ").append(node.getText()).toString());
/*     */       }
/*     */     }
/*     */ 
/* 237 */     return type;
/*     */   }
/*     */ 
/*     */   private boolean isNodeInstrument(ASTNode root) {
/* 241 */     boolean result = false;
/* 242 */     ASTNode node = root;
/* 243 */     if ((node != null) && (node.getId() == 52) && (node.getChildren() != null))
/*     */     {
/* 246 */       if (node.getChildren().length == 1)
/* 247 */         node = node.getChildren()[0];
/* 248 */       else if ((node.getChildren().length > 2) && 
/* 249 */         (node.getChildren()[0].getId() == 168) && (node.getChildren()[1].getId() == 66))
/*     */       {
/* 251 */         node = node.getChildren()[0];
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 258 */     if ((node != null) && (node.getText() != null) && (!node.getText().isEmpty())) {
/* 259 */       VariableDeclaration variable = (VariableDeclaration)this.pairRoot.getCallback().getCurrentBranchVariableDeclaration(node.getText(), this.pairRoot.getDeclRoot());
/* 260 */       if (variable != null)
/* 261 */         result = variable.isObjectType();
/*     */       else {
/* 263 */         result = isNodeInstrument_Old(node);
/*     */       }
/*     */     }
/*     */ 
/* 267 */     return result;
/*     */   }
/*     */ 
/*     */   private boolean isNodeInstrument_Old(ASTNode root) {
/* 271 */     boolean result = false;
/* 272 */     ASTNode node = root;
/* 273 */     if ((node != null) && (node.getText() != null) && (!node.getText().isEmpty())) {
/* 274 */       if ((node.getText().indexOf("ymbol") > 0) || (node.getText().indexOf("nstrument") > 0)) {
/* 275 */         FunctionDeclaration fun = this.pairRoot.getFunctionDeclaration(node.getText());
/* 276 */         if ((fun != null) && (!fun.isNumeric())) {
/* 277 */           result = true;
/* 278 */         } else if (ArrayHelpers.binarySearch(SYMBOL_FUNCTIONS, node.getText()) > -1) {
/* 279 */           result = true;
/*     */         } else {
/* 281 */           VariableDeclaration var = this.pairRoot.getVariableDeclaration(node.getText(), null);
/* 282 */           if ((var != null) && (!var.isNumeric())) {
/* 283 */             result = true;
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/* 288 */     else if ((node != null) && (node.getChildren() != null)) {
/* 289 */       for (ASTNode child : node.getChildren()) {
/* 290 */         if (child.getId() == 62)
/*     */           continue;
/* 292 */         if (child.getId() != 168)
/*     */         {
/*     */           continue;
/*     */         }
/*     */ 
/* 298 */         if ((child.getText().indexOf("ymbol") <= 0) && (child.getText().indexOf("nstrument") <= 0)) break;
/* 299 */         FunctionDeclaration fun = this.pairRoot.getFunctionDeclaration(child.getText());
/* 300 */         if ((fun != null) && (!fun.isNumeric())) {
/* 301 */           result = true;
/* 302 */           break;
/* 303 */         }if (ArrayHelpers.binarySearch(SYMBOL_FUNCTIONS, child.getText()) > -1) {
/* 304 */           result = true;
/* 305 */           break;
/*     */         }
/* 307 */         VariableDeclaration var = this.pairRoot.getVariableDeclaration(child.getText(), null);
/* 308 */         if ((var != null) && (!var.isNumeric())) {
/* 309 */           result = true;
/* 310 */           break;
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 320 */     return result;
/*     */   }
/*     */ 
/*     */   private String printNodeList(List<ASTNode> list) {
/* 324 */     StringBuilder result = new StringBuilder();
/* 325 */     for (int i = 0; i < list.size(); i++) {
/* 326 */       ASTNode node = (ASTNode)list.get(i);
/* 327 */       if (node.getId() == 168) {
/* 328 */         ASTNode operatorNode = (ASTNode)list.get(i + 1);
/* 329 */         ASTNode nextNode = (ASTNode)list.get(i + 2);
/* 330 */         if (isNodeInstrument(node)) {
/* 331 */           if (operatorNode.getId() == 91) {
/* 332 */             result.append("!");
/*     */           }
/* 334 */           if ((operatorNode.getId() == 90) || (operatorNode.getId() == 91)) {
/* 335 */             result.append(printNode(node));
/* 336 */             result.append(".equals(");
/* 337 */             result.append(printNode(nextNode));
/* 338 */             result.append(") ");
/*     */           }
/* 340 */         } else if (isNodeInstrument(nextNode)) {
/* 341 */           if (operatorNode.getId() == 91) {
/* 342 */             result.append("!");
/*     */           }
/* 344 */           if ((operatorNode.getId() == 90) || (operatorNode.getId() == 91)) {
/* 345 */             result.append(printNode(nextNode));
/* 346 */             result.append(".equals(");
/* 347 */             result.append(printNode(node));
/* 348 */             result.append(") ");
/*     */           }
/*     */         }
/*     */       } else {
/* 352 */         result.append(printNode(node));
/* 353 */         result.append(" ");
/*     */       }
/*     */     }
/* 356 */     return result.toString();
/*     */   }
/*     */ 
/*     */   public String print(ASTNode node) {
/* 360 */     StringBuilder result = new StringBuilder();
/* 361 */     if ((node != null) && 
/* 362 */       (hasChildren()));
/* 368 */     return result.toString();
/*     */   }
/*     */ 
/*     */   public String print() {
/* 372 */     StringBuilder result = new StringBuilder();
/*     */     int i;
/* 373 */     if (this.pairs.size() > 0) {
/* 374 */       i = 0;
/* 375 */       for (AssigmentExpressionPair pair : this.pairs) {
/* 376 */         if (pair.isCastExpression()) {
/* 377 */           result.append("(");
/*     */         }
/* 379 */         result.append(pair.print());
/* 380 */         if (pair.isCastExpression()) {
/* 381 */           result.append(") ");
/* 382 */           if (pair.getOperatorBetweenPairsNode() != null) {
/* 383 */             result.append(printNode(pair.getOperatorBetweenPairsNode()));
/* 384 */             if (pair.getSecondNode() != null)
/* 385 */               result.append(printNode(pair.getSecondNode()));
/* 386 */             else if (pair.getOperatorAfterPairsNode() != null)
/* 387 */               result.append(printNode(pair.getOperatorAfterPairsNode()));
/*     */           }
/* 389 */           else if (pair.getOperatorAfterPairsNode() != null) {
/* 390 */             result.append(printNode(pair.getOperatorAfterPairsNode()));
/* 391 */             if (pair.getSecondNode() != null)
/* 392 */               result.append(printNode(pair.getSecondNode()));
/*     */           }
/*     */         }
/*     */         else
/*     */         {
/* 397 */           if ((pair.getFirstNode() == null) && (pair.getSecondNode() == null) && (pair.getOperatorBetweenPairsNode() != null)) {
/* 398 */             result.append(" ");
/* 399 */             result.append(printNode(pair.getOperatorBetweenPairsNode()));
/* 400 */             result.append(" ");
/*     */           }
/* 402 */           if ((pair.getFirstNode() == null) && (pair.getSecondNode() == null) && (pair.getOperatorAfterPairsNode() != null)) {
/* 403 */             result.append(" ");
/* 404 */             result.append(printNode(pair.getOperatorAfterPairsNode()));
/* 405 */             result.append(" ");
/*     */           }
/*     */         }
/* 408 */         i++;
/*     */       }
/*     */     } else {
/* 411 */       String firstExpression = "";
/* 412 */       boolean isOneExpressionObject = false;
/* 413 */       boolean isOneExpressionBoolean = false;
/* 414 */       String secondExpression = "";
/*     */ 
/* 418 */       if ((getFirstNode() != null) && (getFirstNodeList().size() < 2) && (getSecondNodeList().size() < 2)) {
/* 419 */         firstExpression = printNode(getFirstNodeList());
/* 420 */         if (isNodeInstrument(getFirstNode()))
/* 421 */           isOneExpressionObject = true;
/*     */       }
/*     */       else {
/* 424 */         result.append(printNodeList(getFirstNodeList()));
/* 425 */         if (getOperatorBetweenPairsNode() != null) {
/* 426 */           result.append(printNode(getOperatorBetweenPairsNode()));
/*     */         }
/*     */       }
/*     */ 
/* 430 */       if ((getSecondNode() != null) && (getSecondNodeList().size() < 2) && (getFirstNodeList().size() < 2)) {
/* 431 */         secondExpression = printNode(getSecondNode());
/* 432 */         if (isNodeInstrument(getSecondNode()))
/*     */         {
/* 434 */           if (!isOneExpressionObject) {
/* 435 */             firstExpression = printNode(getSecondNode());
/* 436 */             secondExpression = printNode(getFirstNode());
/*     */ 
/* 438 */             isOneExpressionObject = true;
/*     */           }
/*     */         }
/*     */       }
/*     */       else {
/* 443 */         result.append(printNodeList(getSecondNodeList()));
/*     */       }
/* 445 */       if ((getOperatorBetweenPairsNode() != null) && (getSecondNodeList().size() < 2) && (getFirstNodeList().size() < 2)) {
/* 446 */         if (isOneExpressionObject) {
/* 447 */           if (getOperatorBetweenPairsNode().getId() == 90) {
/* 448 */             result.append(firstExpression);
/* 449 */             result.append(".equals(");
/* 450 */             result.append(secondExpression);
/* 451 */             result.append(")");
/* 452 */           } else if (getOperatorBetweenPairsNode().getId() == 91) {
/* 453 */             result.append("!");
/* 454 */             result.append(firstExpression);
/* 455 */             result.append(".equals(");
/* 456 */             result.append(secondExpression);
/* 457 */             result.append(")"); } else if ((getOperatorBetweenPairsNode().getId() == 92) || 
/* 459 */             (getOperatorBetweenPairsNode().getId() == 93) || 
/* 460 */             (getOperatorBetweenPairsNode().getId() == 94) || 
/* 461 */             (getOperatorBetweenPairsNode().getId() != 95));
/*     */         }
/*     */         else {
/* 464 */           boolean isCompareBoolAndNumber = false;
/* 465 */           if ((getSecondNode() != null) && (getNodeResultType(getSecondNode()).equals("bool")) && (getNodeNumeric(getFirstNode()))) {
/* 466 */             firstExpression = printNode(getFirstNode());
/* 467 */             secondExpression = printNode(getSecondNode());
/* 468 */             isCompareBoolAndNumber = true;
/* 469 */           } else if ((getNodeResultType(getFirstNode()).equals("bool")) && (getSecondNode() != null) && (getNodeNumeric(getSecondNode()))) {
/* 470 */             secondExpression = printNode(getFirstNode());
/* 471 */             firstExpression = printNode(getSecondNode());
/* 472 */             isCompareBoolAndNumber = true;
/*     */           }
/* 474 */           if (isCompareBoolAndNumber)
/*     */           {
/* 476 */             if (getOperatorBetweenPairsNode().getId() == 90)
/* 477 */               result.append("compareEquals(");
/* 478 */             else if (getOperatorBetweenPairsNode().getId() == 91)
/* 479 */               result.append("!compareEquals(");
/* 480 */             else if ((getOperatorBetweenPairsNode().getId() == 92) || 
/* 481 */               (getOperatorBetweenPairsNode().getId() == 93) || 
/* 482 */               (getOperatorBetweenPairsNode().getId() == 94) || 
/* 483 */               (getOperatorBetweenPairsNode().getId() != 95));
/* 485 */             result.append(firstExpression);
/* 486 */             result.append(",");
/* 487 */             result.append(secondExpression);
/* 488 */             result.append(")");
/*     */           } else {
/* 490 */             result.append("Bool(");
/* 491 */             result.append(printNode(getFirstNode()));
/* 492 */             if (getOperatorBetweenPairsNode() != null) {
/* 493 */               result.append(" ");
/* 494 */               result.append(printNode(getOperatorBetweenPairsNode()));
/* 495 */               result.append(" ");
/*     */             }
/* 497 */             if (getSecondNode() != null) {
/* 498 */               result.append(printNode(getSecondNode()));
/*     */             }
/* 500 */             result.append(")");
/*     */           }
/*     */         }
/*     */       }
/* 504 */       else if ((firstExpression != null) && (!firstExpression.isEmpty())) {
/* 505 */         result.append("Bool(");
/* 506 */         result.append(firstExpression);
/* 507 */         result.append(")");
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 517 */     return result.toString();
/*     */   }
/*     */   public AssigmentExpressionPair getParent() {
/* 520 */     return this.parent;
/*     */   }
/*     */ 
/*     */   public void setParent(AssigmentExpressionPair parent) {
/* 524 */     this.parent = parent;
/*     */   }
/*     */ 
/*     */   public boolean isCastExpression() {
/* 528 */     boolean localCastExpression = this.isCastExpression;
/*     */ 
/* 537 */     return localCastExpression;
/*     */   }
/*     */ 
/*     */   public void setCastExpression(boolean isCastExpression) {
/* 541 */     this.isCastExpression = isCastExpression;
/*     */   }
/*     */   public List<ASTNode> getFirstNodeList() {
/* 544 */     return this.firstNodeList;
/*     */   }
/*     */   public List<ASTNode> getSecondNodeList() {
/* 547 */     return this.secondNodeList;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Connector-1.1.49.jar
 * Qualified Name:     com.dukascopy.dds2.greed.connector.parser.util.AssigmentExpressionPair
 * JD-Core Version:    0.6.0
 */