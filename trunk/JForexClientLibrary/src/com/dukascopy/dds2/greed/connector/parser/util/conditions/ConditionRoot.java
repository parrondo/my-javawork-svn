/*     */ package com.dukascopy.dds2.greed.connector.parser.util.conditions;
/*     */ 
/*     */ import com.dukascopy.api.connector.helpers.ReflectionHelpers;
/*     */ import com.dukascopy.dds2.greed.connector.parser.javacc.ASTNode;
/*     */ import com.dukascopy.dds2.greed.connector.parser.javacc.ParserCallback;
/*     */ import com.dukascopy.dds2.greed.connector.parser.util.Declaration;
/*     */ import com.dukascopy.dds2.greed.connector.parser.util.FunctionDeclaration;
/*     */ import com.dukascopy.dds2.greed.connector.parser.util.IDeclaration;
/*     */ import com.dukascopy.dds2.greed.connector.parser.util.VariableDeclaration;
/*     */ import java.io.PrintStream;
/*     */ import java.lang.reflect.Method;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Map;
/*     */ 
/*     */ public class ConditionRoot
/*     */ {
/*  20 */   ParserCallback parserCallback = null;
/*  21 */   Declaration declarationRoot = null;
/*     */   IConditionItem rootItem;
/*  23 */   int loopIndex = 0;
/*     */ 
/*     */   private IConditionItem makeConditionItem(ASTNode node, IConditionItem root, IConditionItem prevItem) {
/*  26 */     IConditionItem item = null;
/*  27 */     boolean isCreated = false;
/*  28 */     if (isOperator(node)) {
/*  29 */       item = new OperatorExpressionItem();
/*  30 */       isCreated = true;
/*  31 */     } else if ((isCompoundStatement(node)) || (isCastExpression(node))) {
/*  32 */       item = new ExpressionItem();
/*  33 */       isCreated = true;
/*     */     }
/*  35 */     if (isCreated) {
/*  36 */       item.setConditionRoot(this);
/*  37 */       item.setNode(node);
/*  38 */       root.add(item);
/*  39 */       item.setParent(root);
/*  40 */       if (prevItem != null) {
/*  41 */         prevItem.setNextConditionItem(item);
/*     */       }
/*  43 */       if (nodeHasChildren(node)) {
/*  44 */         makeConditionItems(node, item);
/*     */       }
/*     */     }
/*  47 */     return item;
/*     */   }
/*     */ 
/*     */   private void makeConditionItemsLoop(ASTNode expressionNode, IConditionItem root) {
/*  51 */     IConditionItem prevItem = null;
/*  52 */     if ((expressionNode.getChildren() != null) && (expressionNode.getChildren().length > 0))
/*  53 */       for (int i = 0; i < expressionNode.getChildren().length; i++) {
/*  54 */         ASTNode node = expressionNode.getChildren()[i];
/*  55 */         prevItem = makeConditionItem(node, root, prevItem);
/*     */       }
/*     */     else
/*  58 */       prevItem = makeConditionItem(expressionNode, root, prevItem);
/*     */   }
/*     */ 
/*     */   public void makeConditionItems(ASTNode expressionNode, IConditionItem root)
/*     */   {
/*  63 */     if (root == null) {
/*  64 */       this.rootItem = new ExpressionItem();
/*  65 */       root = this.rootItem;
/*  66 */       this.rootItem.setConditionRoot(this);
/*     */     }
/*     */ 
/*  69 */     if (isNodeInLRParnesis(expressionNode)) {
/*  70 */       ASTNode node = getExpressionNodeInLRParnesis(expressionNode);
/*  71 */       IConditionItem item = new ExpressionItem();
/*  72 */       item.setConditionRoot(this);
/*  73 */       item.setInLRParnesis(true);
/*  74 */       item.setNode(node);
/*  75 */       root.add(item);
/*  76 */       if (nodeHasChildren(node))
/*  77 */         makeConditionItems(node, item);
/*     */       else
/*  79 */         makeConditionItemsLoop(node, item);
/*     */     }
/*     */     else {
/*  82 */       makeConditionItemsLoop(expressionNode, root);
/*     */     }
/*     */   }
/*     */ 
/*     */   private boolean nodeHasChildren(ASTNode node)
/*     */   {
/*  88 */     boolean result = false;
/*  89 */     if ((node != null) && (node.getChildren() != null) && (node.getChildren().length > 0)) {
/*  90 */       for (int i = 0; i < node.getChildren().length; i++) {
/*  91 */         ASTNode child = node.getChildren()[i];
/*  92 */         if ((child.getId() == 49) || (child.getId() == 50) || (child.getId() == 52)) {
/*  93 */           result = true;
/*  94 */           break;
/*     */         }
/*     */       }
/*     */     }
/*  98 */     return result;
/*     */   }
/*     */ 
/*     */   private boolean isCompoundStatement(ASTNode node) {
/* 102 */     boolean result = false;
/* 103 */     if (node.getId() == 41) {
/* 104 */       result = true;
/*     */     }
/* 106 */     return result;
/*     */   }
/*     */ 
/*     */   private boolean isCastExpression(ASTNode node) {
/* 110 */     boolean result = false;
/* 111 */     if ((node.getId() == 52) || (node.getId() == 49) || (node.getId() == 50)) {
/* 112 */       result = true;
/*     */     }
/* 114 */     return result;
/*     */   }
/*     */ 
/*     */   private boolean isOperator(ASTNode node) {
/* 118 */     return (isLogicSecondLevelDevider(node)) || (isLogicFirstLevelDevider(node)) || (isMathOperator(node)) || (isUnar(node)) || (isBinaryOperationLevelDevider(node));
/*     */   }
/*     */ 
/*     */   public boolean isLogicSecondLevelDevider(ASTNode node) {
/* 122 */     boolean result = false;
/* 123 */     if ((node.getId() == 90) || (node.getId() == 91) || (node.getId() == 92) || (node.getId() == 94) || (node.getId() == 93) || (node.getId() == 95))
/*     */     {
/* 129 */       result = true;
/*     */     }
/* 131 */     return result;
/*     */   }
/*     */ 
/*     */   public boolean isLogicFirstLevelDevider(ASTNode node) {
/* 135 */     boolean result = false;
/* 136 */     if ((node.getId() == 86) || (node.getId() == 85))
/*     */     {
/* 138 */       result = true;
/*     */     }
/* 140 */     return result;
/*     */   }
/*     */ 
/*     */   public boolean isBinaryOperationLevelDevider(ASTNode node) {
/* 144 */     boolean result = false;
/* 145 */     if ((node.getId() == 89) || (node.getId() == 87) || (node.getId() == 88))
/*     */     {
/* 148 */       result = true;
/*     */     }
/* 150 */     return result;
/*     */   }
/*     */ 
/*     */   public boolean isMathOperator(ASTNode node) {
/* 154 */     boolean result = false;
/* 155 */     if ((node.getId() == 98) || (node.getId() == 99) || (node.getId() == 100) || (node.getId() == 101) || (node.getId() == 102))
/*     */     {
/* 162 */       result = true;
/*     */     }
/* 164 */     return result;
/*     */   }
/*     */ 
/*     */   private boolean isNodeInLRParnesis(ASTNode node) {
/* 168 */     boolean result = false;
/* 169 */     if ((node.getChildren() != null) && (node.getChildren().length == 3) && (node.getChildren()[0].getId() == 66) && (node.getChildren()[2].getId() == 67))
/*     */     {
/* 173 */       result = true;
/*     */     }
/* 175 */     return result;
/*     */   }
/*     */ 
/*     */   public boolean isUnar(ASTNode node) {
/* 179 */     boolean result = false;
/* 180 */     if ((node.getId() == 57) || (node.getId() == 103) || (node.getId() == 104))
/*     */     {
/* 183 */       result = true;
/*     */     }
/* 185 */     return result;
/*     */   }
/*     */ 
/*     */   private ASTNode getExpressionNodeInLRParnesis(ASTNode root) {
/* 189 */     ASTNode result = root;
/* 190 */     if ((root != null) && (root.getChildren() != null) && (root.getChildren().length > 2)) {
/* 191 */       int firstIndex = 0;
/* 192 */       int lastIndex = root.getChildren().length - 1;
/* 193 */       while ((root.getChildren()[firstIndex].getId() == 66) && (root.getChildren()[lastIndex].getId() == 67)) {
/* 194 */         firstIndex++;
/* 195 */         lastIndex--;
/*     */       }
/* 197 */       if (firstIndex > 0) {
/* 198 */         result = root.getChildren()[firstIndex];
/*     */       }
/* 200 */       if ((result.getId() == 49) || (result.getId() == 50)) {
/* 201 */         result = result.getChildren()[0];
/*     */       }
/*     */     }
/* 204 */     return result;
/*     */   }
/*     */ 
/*     */   public FunctionDeclaration getFunctionDeclaration(String name) {
/* 208 */     FunctionDeclaration result = null;
/* 209 */     if ((getParserCallback().functions != null) && (getParserCallback().functions.containsKey(name))) {
/* 210 */       result = (FunctionDeclaration)getParserCallback().functions.get(name);
/*     */     }
/*     */ 
/* 213 */     return result;
/*     */   }
/*     */ 
/*     */   public VariableDeclaration getVariableDeclaration(String name, Declaration root) {
/* 217 */     VariableDeclaration result = null;
/* 218 */     if (root == null) {
/* 219 */       if ((getParserCallback().variables != null) && (getParserCallback().variables.containsKey(name)))
/* 220 */         result = (VariableDeclaration)getParserCallback().variables.get(name);
/*     */       else {
/* 222 */         for (FunctionDeclaration func : getParserCallback().functions.values()) {
/* 223 */           if ((func.hasVariables()) && (func.variables.containsKey(name))) {
/* 224 */             result = func.getVariable(name);
/*     */           }
/* 226 */           if ((result == null) && (func.getParams() != null) && (func.getParams().size() > 0)) {
/* 227 */             result = func.getParamByName(name);
/*     */           }
/* 229 */           if ((result == null) && (func.hasChildren())) {
/* 230 */             for (IDeclaration child : func.children)
/* 231 */               result = getVariableDeclaration(name, (Declaration)child);
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/* 236 */     else if ((!(root instanceof FunctionDeclaration)) && (root.hasChildren())) {
/* 237 */       for (IDeclaration child : root.children) {
/* 238 */         if ((((Declaration)child).hasVariables()) && (((Declaration)child).variables.containsKey(name))) {
/* 239 */           result = (VariableDeclaration)((Declaration)child).variables.get(name);
/*     */         }
/* 241 */         if (((Declaration)child).hasChildren()) {
/* 242 */           result = getVariableDeclaration(name, (Declaration)child);
/*     */         }
/*     */       }
/*     */     }
/* 246 */     return result;
/*     */   }
/*     */ 
/*     */   public StringBuilder print() {
/* 250 */     StringBuilder buf = new StringBuilder();
/*     */ 
/* 252 */     return buf.append(this.rootItem.print());
/*     */   }
/*     */ 
/*     */   public Declaration getDeclarationRoot() {
/* 256 */     return this.declarationRoot;
/*     */   }
/*     */ 
/*     */   public void setDeclarationRoot(Declaration declarationRoot) {
/* 260 */     this.declarationRoot = declarationRoot;
/*     */   }
/*     */ 
/*     */   public ParserCallback getParserCallback() {
/* 264 */     return this.parserCallback;
/*     */   }
/*     */ 
/*     */   public void setParserCallback(ParserCallback parserCallback) {
/* 268 */     this.parserCallback = parserCallback;
/*     */   }
/*     */ 
/*     */   public boolean getNodeNumeric(ASTNode root) {
/* 272 */     boolean result = false;
/* 273 */     String type = getNodeResultType(root);
/* 274 */     if ((type != null) && (!type.isEmpty()) && (
/* 275 */       (type.equals("int")) || (type.equals("long")) || (type.equals("double")) || (type.equals("double")) || (type.equals("byte"))))
/*     */     {
/* 278 */       result = true;
/*     */     }
/*     */ 
/* 282 */     return result;
/*     */   }
/*     */ 
/*     */   public String getNodeResultType(ASTNode root)
/*     */   {
/* 291 */     String type = "";
/* 292 */     boolean isVariable = false;
/* 293 */     boolean isFunction = false;
/* 294 */     boolean isConstant = false;
/* 295 */     ASTNode node = root;
/* 296 */     if ((node != null) && (node.getId() == 52) && (node.getChildren() != null)) {
/* 297 */       if (node.getChildren().length == 1) {
/* 298 */         node = node.getChildren()[0];
/* 299 */         if (node.getId() == 168) {
/* 300 */           isVariable = true;
/*     */         } else {
/* 302 */           if ((node.getChildren() != null) && (node.getChildren().length > 0)) {
/* 303 */             node = node.getChildren()[0];
/*     */           }
/* 305 */           isConstant = true;
/*     */         }
/* 307 */       } else if (node.getChildren().length > 2) {
/* 308 */         if ((node.getChildren()[0].getId() == 168) && (node.getChildren()[1].getId() == 66))
/*     */         {
/* 310 */           node = node.getChildren()[0];
/* 311 */           isFunction = true;
/* 312 */         } else if ((node.getChildren()[0].getId() == 168) && (node.getChildren()[1].getId() == 64))
/*     */         {
/* 314 */           node = node.getChildren()[0];
/* 315 */           isVariable = true;
/*     */         }
/*     */       }
/*     */     }
/* 319 */     if (isVariable) {
/* 320 */       VariableDeclaration variable = (VariableDeclaration)getParserCallback().getCurrentBranchVariableDeclaration(node.getText(), getDeclarationRoot());
/* 321 */       if (variable != null)
/* 322 */         type = variable.getType();
/*     */       else {
/* 324 */         variable = (VariableDeclaration)getParserCallback().variables.get(node.getText());
/*     */       }
/* 326 */       if (variable != null) {
/* 327 */         type = variable.getType();
/*     */       }
/*     */     }
/* 330 */     else if (isFunction) {
/* 331 */       FunctionDeclaration function = (FunctionDeclaration)getParserCallback().functions.get(node.getText());
/* 332 */       if (function != null) {
/* 333 */         type = function.getType();
/*     */       } else {
/* 335 */         Method method = ReflectionHelpers.getAbstractConnectorMethod(node.getText());
/* 336 */         if (method != null)
/* 337 */           type = method.getReturnType().getName();
/*     */       }
/*     */     }
/* 340 */     else if (isConstant) {
/* 341 */       if ((node.getId() == 161) || (node.getId() == 162))
/* 342 */         type = "bool";
/* 343 */       else if ((node.getId() == 176) || (node.getId() == 175) || (node.getId() == 171) || (node.getId() == 180) || (node.getId() == 173) || (node.getId() == 182))
/*     */       {
/* 349 */         type = "int";
/* 350 */       } else if ((node.getId() == 177) || (node.getId() == 172) || (node.getId() == 181) || (node.getId() == 174) || (node.getId() == 183))
/*     */       {
/* 355 */         type = "long";
/* 356 */       } else if ((node.getId() == 184) || (node.getId() == 185))
/* 357 */         type = "double";
/*     */       else {
/* 359 */         System.out.println("!! node.getId()" + node.getId() + " : " + node.getText());
/*     */       }
/*     */     }
/*     */ 
/* 363 */     return type;
/*     */   }
/*     */ 
/*     */   public synchronized int getLoopIndex() {
/* 367 */     return this.loopIndex;
/*     */   }
/*     */ 
/*     */   public synchronized void setLoopIndex(int loopIndex) {
/* 371 */     this.loopIndex = loopIndex;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Connector-1.1.49.jar
 * Qualified Name:     com.dukascopy.dds2.greed.connector.parser.util.conditions.ConditionRoot
 * JD-Core Version:    0.6.0
 */