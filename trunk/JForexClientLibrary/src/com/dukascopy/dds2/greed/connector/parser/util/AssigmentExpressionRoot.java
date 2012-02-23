/*     */ package com.dukascopy.dds2.greed.connector.parser.util;
/*     */ 
/*     */ import com.dukascopy.dds2.greed.connector.parser.javacc.ASTNode;
/*     */ import com.dukascopy.dds2.greed.connector.parser.javacc.ParserCallback;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ 
/*     */ public class AssigmentExpressionRoot
/*     */ {
/*  11 */   ParserCallback callback = null;
/*  12 */   Declaration declRoot = null;
/*  13 */   List<AssigmentExpressionPair> pairs = new ArrayList();
/*     */ 
/*     */   public AssigmentExpressionRoot(ParserCallback callback) {
/*  16 */     this.callback = callback;
/*     */   }
/*     */ 
/*     */   public void addPair(AssigmentExpressionPair pair) {
/*  20 */     this.pairs.add(pair);
/*     */   }
/*     */ 
/*     */   private boolean isLogicSecondLevelDevider(ASTNode node) {
/*  24 */     boolean result = false;
/*  25 */     if ((node.getId() == 90) || (node.getId() == 91) || (node.getId() == 92) || (node.getId() == 94) || (node.getId() == 93) || (node.getId() == 95))
/*     */     {
/*  31 */       result = true;
/*     */     }
/*  33 */     return result;
/*     */   }
/*     */ 
/*     */   private boolean isLogicFirstLevelDevider(ASTNode node) {
/*  37 */     boolean result = false;
/*  38 */     if ((node.getId() == 86) || (node.getId() == 85))
/*     */     {
/*  40 */       result = true;
/*     */     }
/*  42 */     return result;
/*     */   }
/*     */ 
/*     */   private boolean isNodeInLRParnesis(ASTNode node) {
/*  46 */     boolean result = false;
/*  47 */     if ((node.getChildren() != null) && (node.getChildren().length == 3) && (node.getChildren()[0].getId() == 66) && (node.getChildren()[2].getId() == 67))
/*     */     {
/*  51 */       result = true;
/*     */     }
/*  53 */     return result;
/*     */   }
/*     */ 
/*     */   private boolean isUnar(ASTNode node) {
/*  57 */     boolean result = false;
/*  58 */     if ((node.getId() == 57) || (node.getId() == 103))
/*     */     {
/*  60 */       result = true;
/*     */     }
/*  62 */     return result;
/*     */   }
/*     */ 
/*     */   public void makeExpressionPairs(ASTNode expressionNode, AssigmentExpressionPair root)
/*     */   {
/*  67 */     makeExpressionPairs(expressionNode, root, false);
/*     */   }
/*     */ 
/*     */   public void makeExpressionPairs(ASTNode expressionNode, AssigmentExpressionPair root, boolean isCastExpression)
/*     */   {
/*     */     AssigmentExpressionPair currentPair;
/*     */     boolean needNextPair;
/*     */     AssigmentExpressionPair newpair;
/*     */     boolean isNewPair;
/*     */     int i;
/*  71 */     if (expressionNode.getChildren() != null) {
/*  72 */       currentPair = null;
/*  73 */       needNextPair = false;
/*  74 */       newpair = null;
/*  75 */       isNewPair = false;
/*  76 */       for (i = 0; i < expressionNode.getChildren().length; ) {
/*  77 */         needNextPair = false;
/*  78 */         AssigmentExpressionPair pair = new AssigmentExpressionPair(this);
/*  79 */         pair.setCastExpression(isCastExpression);
/*     */ 
/*  81 */         currentPair = pair;
/*  82 */         pair.setParent(root);
/*     */ 
/*  84 */         if (isNewPair) {
/*  85 */           pair.setFirstNode(newpair.getFirstNode());
/*  86 */           pair.setOperatorBetweenPairsNode(newpair.getOperatorBetweenPairsNode());
/*  87 */           isNewPair = false;
/*  88 */           needNextPair = true;
/*     */         } else {
/*  90 */           ASTNode firstNode = expressionNode.getChildren()[(i++)];
/*  91 */           pair.setFirstNode(firstNode);
/*  92 */           pair.isCastExpressionInLRParnesis = isNodeInLRParnesis(firstNode);
/*  93 */           if (i < expressionNode.getChildren().length) {
/*  94 */             ASTNode node = expressionNode.getChildren()[(i++)];
/*  95 */             while ((!isLogicFirstLevelDevider(node)) && (!isLogicSecondLevelDevider(node))) {
/*  96 */               pair.setFirstNodeToList(node);
/*  97 */               if (i >= expressionNode.getChildren().length) break;
/*  98 */               node = expressionNode.getChildren()[(i++)];
/*     */             }
/*     */ 
/* 103 */             if (isLogicSecondLevelDevider(node)) {
/* 104 */               pair.setOperatorBetweenPairsNode(node);
/* 105 */             } else if (isLogicFirstLevelDevider(node)) {
/* 106 */               pair.setOperatorAfterPairsNode(node);
/* 107 */               needNextPair = true;
/*     */             }
/*     */           }
/*     */         }
/* 111 */         if ((!needNextPair) && (i < expressionNode.getChildren().length)) {
/* 112 */           ASTNode secondNode = expressionNode.getChildren()[(i++)];
/* 113 */           pair.isCastExpressionInLRParnesis = isNodeInLRParnesis(secondNode);
/* 114 */           pair.setSecondNode(secondNode);
/*     */         }
/*     */ 
/* 117 */         if ((!needNextPair) && (i < expressionNode.getChildren().length)) {
/* 118 */           ASTNode node = expressionNode.getChildren()[(i++)];
/* 119 */           while ((!isLogicFirstLevelDevider(node)) && (!isLogicSecondLevelDevider(node))) {
/* 120 */             pair.setSecondNodeToList(node);
/* 121 */             if (i >= expressionNode.getChildren().length) break;
/* 122 */             node = expressionNode.getChildren()[(i++)];
/*     */           }
/*     */ 
/* 128 */           if ((isLogicSecondLevelDevider(node)) && (isLogicSecondLevelDevider(pair.getOperatorBetweenPairsNode()))) {
/* 129 */             ASTNode operatorAfterPairsNode = new ASTNode(86);
/* 130 */             operatorAfterPairsNode.setText("&&");
/* 131 */             pair.setOperatorAfterPairsNode(operatorAfterPairsNode);
/* 132 */             newpair = new AssigmentExpressionPair(this);
/* 133 */             newpair.setFirstNode(pair.getSecondNode());
/* 134 */             newpair.setOperatorBetweenPairsNode(node);
/* 135 */             isNewPair = true;
/*     */           }
/* 137 */           else if (isLogicSecondLevelDevider(node)) {
/* 138 */             pair.setOperatorBetweenPairsNode(node);
/* 139 */           } else if (isLogicFirstLevelDevider(node)) {
/* 140 */             pair.setOperatorAfterPairsNode(node);
/* 141 */             needNextPair = true;
/*     */           }
/*     */ 
/*     */         }
/*     */ 
/* 151 */         if (currentPair.getParent() == null)
/* 152 */           this.pairs.add(currentPair);
/*     */         else {
/* 154 */           root.addChild(currentPair);
/*     */         }
/* 156 */         makeExpressionPairChildren(currentPair);
/* 157 */         if (newpair == null);
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   private boolean nodeContainsLogic(ASTNode node) {
/* 165 */     boolean result = false;
/* 166 */     if ((node.getId() == 90) || (node.getId() == 91) || (node.getId() == 92) || (node.getId() == 93) || (node.getId() == 94) || (node.getId() == 95) || (node.getId() == 85) || (node.getId() == 86))
/*     */     {
/* 174 */       result = true;
/*     */     }
/* 176 */     else if (node.getChildren() != null) {
/* 177 */       for (ASTNode child : node.getChildren()) {
/* 178 */         if ((child.getId() != 90) && (child.getId() != 91) && (child.getId() != 92) && (child.getId() != 93) && (child.getId() != 94) && (child.getId() != 95) && (child.getId() != 85) && (child.getId() != 86))
/*     */         {
/*     */           continue;
/*     */         }
/*     */ 
/* 186 */         result = true;
/* 187 */         break;
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 193 */     return result;
/*     */   }
/*     */ 
/*     */   private ASTNode getExpressionNodeInLRParnesis(ASTNode root) {
/* 197 */     ASTNode result = root;
/* 198 */     if ((root != null) && (root.getChildren() != null) && (root.getChildren().length > 2)) {
/* 199 */       int firstIndex = 0;
/* 200 */       int lastIndex = root.getChildren().length - 1;
/* 201 */       while ((root.getChildren()[firstIndex].getId() == 66) && (root.getChildren()[lastIndex].getId() == 67)) {
/* 202 */         firstIndex++;
/* 203 */         lastIndex--;
/*     */       }
/* 205 */       if (firstIndex > 0) {
/* 206 */         result = root.getChildren()[firstIndex];
/*     */       }
/* 208 */       if ((result.getId() == 49) || (result.getId() == 50)) {
/* 209 */         result = result.getChildren()[0];
/*     */       }
/*     */     }
/* 212 */     return result;
/*     */   }
/*     */ 
/*     */   private void makeExpressionPairChildren(AssigmentExpressionPair pair) {
/* 216 */     ASTNode firstNode = getExpressionNodeInLRParnesis(pair.getFirstNode());
/* 217 */     ASTNode secondNode = getExpressionNodeInLRParnesis(pair.getSecondNode());
/* 218 */     if ((firstNode != null) && (secondNode != null)) {
/* 219 */       AssigmentExpressionPair firstExprRoot = null;
/*     */ 
/* 221 */       AssigmentExpressionPair secondExprRoot = null;
/* 222 */       if ((firstNode.getChildren() != null) && (firstNode.getChildren().length > 2)) {
/* 223 */         ASTNode expressionNode = firstNode.getChildren()[1];
/* 224 */         if ((expressionNode.getId() == 49) && (expressionNode.getChildren() != null) && (expressionNode.getChildren().length > 0) && 
/* 225 */           (expressionNode.getChildren()[0].getId() == 50) && (expressionNode.getChildren()[0].getChildren() != null) && (expressionNode.getChildren()[0].getChildren()[0].getId() != 168))
/*     */         {
/* 227 */           expressionNode = expressionNode.getChildren()[0];
/*     */         }
/*     */ 
/* 230 */         if ((expressionNode.getId() == 50) && (nodeContainsLogic(expressionNode))) {
/* 231 */           if (firstExprRoot == null) {
/* 232 */             firstExprRoot = new AssigmentExpressionPair(this);
/* 233 */             firstExprRoot.setParent(pair);
/* 234 */             firstExprRoot.setCastExpression(true);
/* 235 */             firstExprRoot.setOperatorBetweenPairsNode(pair.getOperatorBetweenPairsNode());
/* 236 */             pair.setOperatorBetweenPairsNode(null);
/* 237 */             pair.pairs.add(firstExprRoot);
/*     */           }
/*     */ 
/* 240 */           makeExpressionPairs(expressionNode, firstExprRoot);
/* 241 */         }if ((expressionNode.getId() == 50) && (expressionNode.getChildren() != null) && (expressionNode.getChildren().length < 2)) {
/* 242 */           pair.setFirstNode(expressionNode);
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/* 254 */       if ((secondNode.getChildren() != null) && (secondNode.getChildren().length > 2)) {
/* 255 */         ASTNode expressionNode = secondNode.getChildren()[1];
/* 256 */         if ((expressionNode.getId() == 49) && (expressionNode.getChildren() != null) && (expressionNode.getChildren().length > 0) && 
/* 257 */           (expressionNode.getChildren()[0].getId() == 50) && (expressionNode.getChildren()[0].getChildren() != null) && (expressionNode.getChildren()[0].getChildren()[0].getId() != 168))
/*     */         {
/* 259 */           expressionNode = expressionNode.getChildren()[0];
/*     */         }
/*     */ 
/* 262 */         if ((expressionNode.getId() == 50) && (nodeContainsLogic(expressionNode))) {
/* 263 */           if (secondExprRoot == null) {
/* 264 */             secondExprRoot = new AssigmentExpressionPair(this);
/* 265 */             secondExprRoot.setCastExpression(true);
/* 266 */             secondExprRoot.setParent(pair);
/* 267 */             secondExprRoot.setOperatorAfterPairsNode(pair.getOperatorAfterPairsNode());
/* 268 */             pair.setOperatorAfterPairsNode(null);
/* 269 */             pair.pairs.add(secondExprRoot);
/*     */           }
/* 271 */           makeExpressionPairs(expressionNode, secondExprRoot);
/* 272 */         }if ((expressionNode.getId() == 50) && (expressionNode.getChildren() != null) && (expressionNode.getChildren().length < 2))
/*     */         {
/* 274 */           pair.setSecondNode(expressionNode);
/*     */         }
/* 276 */         else if ((firstExprRoot != null) && (firstExprRoot.isCastExpression())) {
/* 277 */           firstExprRoot.setSecondNode(secondNode);
/*     */         }
/*     */ 
/*     */       }
/* 281 */       else if ((firstExprRoot != null) && (firstExprRoot.isCastExpression())) {
/* 282 */         firstExprRoot.setSecondNode(secondNode);
/*     */       }
/*     */     }
/* 285 */     else if ((firstNode != null) && (secondNode == null)) {
/* 286 */       if ((firstNode.getChildren() != null) && (firstNode.getChildren().length > 0) && (!DeclarationHelpers.isCastExpressionFunction(firstNode))) {
/* 287 */         boolean isCastExpr = false;
/* 288 */         if ((firstNode.getChildren() != null) && (firstNode.getChildren().length < 2)) {
/* 289 */           isCastExpr = true;
/*     */         }
/*     */ 
/* 294 */         makeNewPairFromSingleNode(pair, firstNode, isCastExpr);
/*     */       }
/* 296 */     } else if ((firstNode == null) && (secondNode != null) && 
/* 297 */       (secondNode.getChildren() != null) && (secondNode.getChildren().length > 0) && (!DeclarationHelpers.isCastExpressionFunction(secondNode))) {
/* 298 */       boolean isCastExpr = false;
/* 299 */       if (secondNode != pair.getSecondNode()) {
/* 300 */         isCastExpr = true;
/*     */       }
/* 302 */       makeNewPairFromSingleNode(pair, secondNode, isCastExpr);
/*     */     }
/*     */   }
/*     */ 
/*     */   private void makeNewPairFromSingleNode(AssigmentExpressionPair root, ASTNode node, boolean isCastExpression)
/*     */   {
/* 308 */     AssigmentExpressionPair newPair = new AssigmentExpressionPair(this);
/* 309 */     if ((node.getChildren() != null) && (node.getChildren().length < 2)) {
/* 310 */       isCastExpression = true;
/*     */     }
/*     */ 
/* 313 */     newPair.isCastExpressionInLRParnesis = isNodeInLRParnesis(node);
/*     */ 
/* 315 */     if (newPair.isCastExpressionInLRParnesis) {
/* 316 */       node = node.getChildren()[1];
/*     */     }
/*     */ 
/* 319 */     if ((root.isCastExpressionInLRParnesis) && (root.getParent() == null)) {
/* 320 */       newPair.setOperatorAfterPairsNode(root.getOperatorAfterPairsNode());
/* 321 */       newPair.setOperatorBetweenPairsNode(root.getOperatorBetweenPairsNode());
/*     */     }
/* 323 */     if ((!newPair.isCastExpressionInLRParnesis) && (!root.isCastExpressionInLRParnesis) && (root.getParent() == null)) {
/* 324 */       newPair.setOperatorAfterPairsNode(root.getOperatorAfterPairsNode());
/* 325 */       newPair.setOperatorBetweenPairsNode(root.getOperatorBetweenPairsNode());
/*     */     }
/*     */ 
/* 328 */     if ((node.getChildren() != null) && (node.getChildren().length > 1) && (node.getChildren()[0].getId() != 57)) {
/* 329 */       root.setCastExpression(true);
/* 330 */       root.addChild(newPair);
/* 331 */       makeExpressionPairs(node, newPair, isCastExpression);
/*     */     }
/*     */   }
/*     */ 
/*     */   public String print() {
/* 336 */     StringBuilder result = new StringBuilder();
/* 337 */     for (AssigmentExpressionPair pair : this.pairs) {
/* 338 */       result.append(pair.print());
/* 339 */       if ((this.pairs.size() > 1) && (pair.getOperatorAfterPairsNode() != null)) {
/* 340 */         result.append(" ");
/* 341 */         result.append(pair.printNode(pair.getOperatorAfterPairsNode()));
/* 342 */         result.append(" ");
/*     */       }
/*     */     }
/* 345 */     return result.toString();
/*     */   }
/*     */ 
/*     */   public ParserCallback getCallback() {
/* 349 */     return this.callback;
/*     */   }
/*     */ 
/*     */   public FunctionDeclaration getFunctionDeclaration(String name) {
/* 353 */     FunctionDeclaration result = null;
/* 354 */     if ((getCallback().functions != null) && (getCallback().functions.containsKey(name))) {
/* 355 */       result = (FunctionDeclaration)getCallback().functions.get(name);
/*     */     }
/*     */ 
/* 358 */     return result;
/*     */   }
/*     */ 
/*     */   public FunctionDeclaration getCurrentFunctionDeclaration() {
/* 362 */     return getCallback().getCurrentFunction();
/*     */   }
/*     */ 
/*     */   public FunctionDeclaration getCurrentDeclaration() {
/* 366 */     return getCallback().getCurrentFunction();
/*     */   }
/*     */ 
/*     */   public Declaration getStatementDeclaration() {
/* 370 */     return getCallback().getStatementDeclaration();
/*     */   }
/*     */ 
/*     */   public VariableDeclaration getVariableDeclaration(String name, Declaration root) {
/* 374 */     VariableDeclaration result = null;
/* 375 */     if (root == null) {
/* 376 */       if ((getCallback().variables != null) && (getCallback().variables.containsKey(name)))
/* 377 */         result = (VariableDeclaration)getCallback().variables.get(name);
/*     */       else {
/* 379 */         for (FunctionDeclaration func : getCallback().functions.values()) {
/* 380 */           if ((func.hasVariables()) && (func.variables.containsKey(name))) {
/* 381 */             result = func.getVariable(name);
/*     */           }
/* 383 */           if ((result == null) && (func.getParams() != null) && (func.getParams().size() > 0)) {
/* 384 */             result = func.getParamByName(name);
/*     */           }
/* 386 */           if ((result == null) && (func.hasChildren())) {
/* 387 */             for (IDeclaration child : func.children)
/* 388 */               result = getVariableDeclaration(name, (Declaration)child);
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/* 393 */     else if ((!(root instanceof FunctionDeclaration)) && (root.hasChildren())) {
/* 394 */       for (IDeclaration child : root.children) {
/* 395 */         if ((((Declaration)child).hasVariables()) && (((Declaration)child).variables.containsKey(name))) {
/* 396 */           result = (VariableDeclaration)((Declaration)child).variables.get(name);
/*     */         }
/* 398 */         if (((Declaration)child).hasChildren()) {
/* 399 */           result = getVariableDeclaration(name, (Declaration)child);
/*     */         }
/*     */       }
/*     */     }
/* 403 */     return result;
/*     */   }
/*     */ 
/*     */   public Declaration getDeclRoot() {
/* 407 */     return this.declRoot;
/*     */   }
/*     */ 
/*     */   public void setDeclRoot(Declaration declRoot) {
/* 411 */     this.declRoot = declRoot;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Connector-1.1.49.jar
 * Qualified Name:     com.dukascopy.dds2.greed.connector.parser.util.AssigmentExpressionRoot
 * JD-Core Version:    0.6.0
 */