/*     */ package com.dukascopy.dds2.greed.connector.parser.util.conditions;
/*     */ 
/*     */ import com.dukascopy.dds2.greed.connector.helpers.ArrayHelpers;
/*     */ import com.dukascopy.dds2.greed.connector.parser.javacc.ASTNode;
/*     */ import com.dukascopy.dds2.greed.connector.parser.javacc.ParserCallback;
/*     */ import com.dukascopy.dds2.greed.connector.parser.util.Declaration;
/*     */ import com.dukascopy.dds2.greed.connector.parser.util.FunctionDeclaration;
/*     */ import com.dukascopy.dds2.greed.connector.parser.util.VariableDeclaration;
/*     */ import java.util.LinkedList;
/*     */ import java.util.List;
/*     */ 
/*     */ public class ExpressionItem extends OperatorExpressionItem
/*     */ {
/*  14 */   private static final String[] SYMBOL_FUNCTIONS = { "Symbol", "Instrument", "OrderSymbol" };
/*     */ 
/*  17 */   List<IConditionItem> children = new LinkedList();
/*     */ 
/*     */   public void add(IConditionItem item)
/*     */   {
/*  21 */     this.children.add(item);
/*     */   }
/*     */ 
/*     */   public IConditionItem get(int index)
/*     */   {
/*  26 */     return (IConditionItem)this.children.get(index);
/*     */   }
/*     */ 
/*     */   public int size()
/*     */   {
/*  31 */     return this.children.size();
/*     */   }
/*     */ 
/*     */   public StringBuilder print()
/*     */   {
/*  36 */     StringBuilder buf = new StringBuilder();
/*     */ 
/*  38 */     for (IConditionItem item : this.children) {
/*  39 */       if (item.getNode() != null)
/*  40 */         buf.append(item.getNode().print());
/*     */       else {
/*  42 */         item.print();
/*     */       }
/*     */     }
/*     */ 
/*  46 */     return buf;
/*     */   }
/*     */ 
/*     */   private StringBuilder printConditionItem(IConditionItem item, int index)
/*     */   {
/*  51 */     StringBuilder buf = new StringBuilder();
/*     */ 
/*  53 */     if (item.hasChildren())
/*  54 */       buf.append(item.print(index));
/*     */     else {
/*  56 */       buf.append(printNode(item.getNode()));
/*     */     }
/*  58 */     return buf;
/*     */   }
/*     */ 
/*     */   private StringBuilder printThreeConditionItems(IConditionItem firstItem, IConditionItem operatorItem, IConditionItem secondItem, int index)
/*     */   {
/*  63 */     StringBuilder buf = new StringBuilder();
/*  64 */     boolean printSimpleNode = true;
/*  65 */     if ((operatorItem != null) && (isNodeInstrument(firstItem.getNode()))) {
/*  66 */       printSimpleNode = false;
/*  67 */     } else if ((operatorItem != null) && (isNodeInstrument(secondItem.getNode())))
/*     */     {
/*  69 */       printSimpleNode = false;
/*  70 */       IConditionItem tmp = firstItem;
/*  71 */       firstItem = secondItem;
/*  72 */       secondItem = tmp;
/*     */     }
/*     */ 
/*  75 */     if (!printSimpleNode) {
/*  76 */       printSimpleNode = true;
/*  77 */       if (operatorItem.getNode().getId() == 90) {
/*  78 */         printSimpleNode = false;
/*  79 */         buf.append(printConditionItem(firstItem, index));
/*  80 */         buf.append(".equals(");
/*  81 */         buf.append(printConditionItem(secondItem, index));
/*  82 */         buf.append(")");
/*  83 */       } else if (operatorItem.getNode().getId() == 91) {
/*  84 */         printSimpleNode = false;
/*  85 */         buf.append("!");
/*  86 */         buf.append(printConditionItem(firstItem, index));
/*  87 */         buf.append(".equals(");
/*  88 */         buf.append(printConditionItem(secondItem, index));
/*  89 */         buf.append(")"); } else if ((operatorItem.getNode().getId() == 92) || 
/*  91 */         (operatorItem.getNode().getId() == 93) || 
/*  92 */         (operatorItem.getNode().getId() == 94) || 
/*  93 */         (operatorItem.getNode().getId() != 95));
/*     */     }
/*  95 */     else if (operatorItem.getNode() != null) {
/*  96 */       boolean isCompareBoolAndNumber = false;
/*  97 */       StringBuilder firstExpression = null; StringBuilder secondExpression = null;
/*  98 */       if ((firstItem != null) && (secondItem != null) && (getConditionRoot().getNodeResultType(secondItem.getNode()).equals("bool")) && (getConditionRoot().getNodeNumeric(firstItem.getNode())))
/*     */       {
/* 103 */         firstExpression = printConditionItem(firstItem, index);
/* 104 */         secondExpression = printConditionItem(secondItem, index);
/* 105 */         isCompareBoolAndNumber = true;
/* 106 */       } else if ((firstItem != null) && (secondItem != null) && (getConditionRoot().getNodeResultType(firstItem.getNode()).equals("bool")) && (getConditionRoot().getNodeNumeric(secondItem.getNode())))
/*     */       {
/* 112 */         secondExpression = printConditionItem(firstItem, index);
/* 113 */         firstExpression = printConditionItem(secondItem, index);
/* 114 */         isCompareBoolAndNumber = true;
/*     */       }
/*     */ 
/* 117 */       if (isCompareBoolAndNumber)
/*     */       {
/* 119 */         if (operatorItem.getNode().getId() == 90) {
/* 120 */           buf.append("compareEquals(");
/* 121 */           printSimpleNode = false;
/* 122 */         } else if (operatorItem.getNode().getId() == 91) {
/* 123 */           buf.append("!compareEquals(");
/* 124 */           printSimpleNode = false;
/* 125 */         } else if ((operatorItem.getNode().getId() == 92) || 
/* 126 */           (operatorItem.getNode().getId() == 93) || 
/* 127 */           (operatorItem.getNode().getId() == 94) || 
/* 128 */           (operatorItem.getNode().getId() != 95));
/* 130 */         if (!printSimpleNode) {
/* 131 */           buf.append(firstExpression);
/* 132 */           buf.append(",");
/* 133 */           buf.append(secondExpression);
/* 134 */           buf.append(")");
/*     */         }
/*     */       }
/*     */     }
/* 138 */     return buf;
/*     */   }
/*     */ 
/*     */   private StringBuilder printFiveNonLogicConditionItems(IConditionItem firstItem, IConditionItem logicOperatorItem, IConditionItem secondItem, IConditionItem assignOperatorItem, IConditionItem resultItem, int index)
/*     */   {
/* 145 */     StringBuilder buf = new StringBuilder();
/*     */ 
/* 148 */     ASTNode logicOperatorNode = null;
/* 149 */     if (((logicOperatorItem instanceof OperatorExpressionItem)) && 
/* 150 */       (getConditionRoot().isBinaryOperationLevelDevider(logicOperatorItem.getNode())))
/*     */     {
/* 152 */       logicOperatorNode = logicOperatorItem.getNode();
/*     */     }
/*     */ 
/* 155 */     ASTNode assignOperatorNode = null;
/* 156 */     if (((assignOperatorItem instanceof OperatorExpressionItem)) && 
/* 157 */       (getConditionRoot().isLogicSecondLevelDevider(assignOperatorItem.getNode())))
/*     */     {
/* 159 */       assignOperatorNode = assignOperatorItem.getNode();
/*     */     }
/*     */ 
/* 163 */     if ((logicOperatorNode != null) && (assignOperatorNode != null)) {
/* 164 */       buf.append("(");
/* 165 */       buf.append(printConditionItem(firstItem, index));
/* 166 */       buf.append(" ");
/* 167 */       buf.append(printConditionItem(logicOperatorItem, index));
/* 168 */       buf.append(" ");
/* 169 */       buf.append(printConditionItem(secondItem, index));
/* 170 */       buf.append(") ");
/* 171 */       buf.append(printConditionItem(assignOperatorItem, index));
/* 172 */       buf.append(" ");
/* 173 */       buf.append(printConditionItem(resultItem, index));
/*     */     }
/*     */ 
/* 176 */     return buf;
/*     */   }
/*     */ 
/*     */   private StringBuilder printFiveLogicConditionItems(IConditionItem firstItem, IConditionItem logicOperatorItem, IConditionItem secondItem, IConditionItem assignOperatorItem, IConditionItem resultItem, int index)
/*     */   {
/* 183 */     StringBuilder buf = new StringBuilder();
/*     */ 
/* 185 */     ASTNode logicOperatorNode = null;
/* 186 */     if (((logicOperatorItem instanceof OperatorExpressionItem)) && 
/* 187 */       (getConditionRoot().isLogicFirstLevelDevider(logicOperatorItem.getNode())))
/*     */     {
/* 189 */       logicOperatorNode = logicOperatorItem.getNode();
/*     */     }
/*     */ 
/* 192 */     ASTNode assignOperatorNode = null;
/* 193 */     if (((assignOperatorItem instanceof OperatorExpressionItem)) && 
/* 194 */       (getConditionRoot().isLogicSecondLevelDevider(assignOperatorItem.getNode())))
/*     */     {
/* 196 */       assignOperatorNode = assignOperatorItem.getNode();
/*     */     }
/*     */ 
/* 200 */     if ((logicOperatorNode != null) && (assignOperatorNode != null)) {
/* 201 */       buf.append(printConditionItem(firstItem, index));
/* 202 */       buf.append(" ");
/* 203 */       buf.append(printConditionItem(logicOperatorItem, index));
/* 204 */       buf.append(" ");
/* 205 */       buf.append(printConditionItem(secondItem, index));
/* 206 */       buf.append(" ");
/* 207 */       buf.append(printConditionItem(assignOperatorItem, index));
/* 208 */       buf.append(" ");
/* 209 */       buf.append(printConditionItem(resultItem, index));
/*     */     }
/*     */ 
/* 212 */     return buf;
/*     */   }
/*     */ 
/*     */   public StringBuilder print(int index)
/*     */   {
/* 217 */     StringBuilder buf = new StringBuilder();
/* 218 */     if (this.children.size() > 0) {
/* 219 */       for (int i = 0; i < this.children.size(); i++) {
/* 220 */         boolean printSimpleNode = true;
/* 221 */         if ((i == 0) && (this.children.size() == 3)) {
/* 222 */           StringBuilder itemBuf = printThreeConditionItems((IConditionItem)this.children.get(0), (IConditionItem)this.children.get(1), (IConditionItem)this.children.get(2), i);
/*     */ 
/* 225 */           if (itemBuf.length() > 0) {
/* 226 */             printSimpleNode = false;
/* 227 */             buf.append(itemBuf);
/* 228 */             i += 3;
/*     */           }
/*     */         }
/* 231 */         if ((i == 0) && (this.children.size() == 5)) {
/* 232 */           StringBuilder itemBuf = printFiveLogicConditionItems((IConditionItem)this.children.get(0), (IConditionItem)this.children.get(1), (IConditionItem)this.children.get(2), (IConditionItem)this.children.get(3), (IConditionItem)this.children.get(4), i);
/*     */ 
/* 236 */           if (itemBuf.length() < 1) {
/* 237 */             itemBuf = printFiveNonLogicConditionItems((IConditionItem)this.children.get(0), (IConditionItem)this.children.get(1), (IConditionItem)this.children.get(2), (IConditionItem)this.children.get(3), (IConditionItem)this.children.get(4), i);
/*     */           }
/*     */ 
/* 243 */           if (itemBuf.length() > 0) {
/* 244 */             printSimpleNode = false;
/* 245 */             buf.append(itemBuf);
/* 246 */             i += 5;
/*     */           }
/*     */         }
/*     */ 
/* 250 */         if (printSimpleNode) {
/* 251 */           if ((i == 0) && (((IConditionItem)this.children.get(i)).isInLRParnesis())) {
/* 252 */             buf.append("(");
/*     */           }
/* 254 */           if (i < this.children.size()) {
/* 255 */             buf.append(((IConditionItem)this.children.get(i)).print(i));
/* 256 */             if (getConditionRoot().getLoopIndex() > 0) {
/* 257 */               i += getConditionRoot().getLoopIndex() - 1;
/* 258 */               getConditionRoot().setLoopIndex(-1);
/*     */             }
/*     */           }
/* 261 */           if ((i != this.children.size() - 1) || (!((IConditionItem)this.children.get(i)).isInLRParnesis()))
/*     */             continue;
/* 263 */           buf.append(")");
/*     */         }
/*     */       }
/*     */     }
/*     */     else {
/* 268 */       boolean printSimpleNode = true;
/* 269 */       IConditionItem firstItem = this;
/* 270 */       IConditionItem logicOperatorItem = null;
/* 271 */       IConditionItem secondItem = null;
/* 272 */       IConditionItem assignOperatorItem = null;
/* 273 */       IConditionItem resultItem = null;
/*     */ 
/* 275 */       if (firstItem.next() != null) {
/* 276 */         logicOperatorItem = firstItem.next();
/*     */       }
/* 278 */       if (logicOperatorItem != null) {
/* 279 */         secondItem = logicOperatorItem.next();
/*     */       }
/* 281 */       if (secondItem != null) {
/* 282 */         assignOperatorItem = secondItem.next();
/*     */       }
/* 284 */       if (assignOperatorItem != null) {
/* 285 */         resultItem = assignOperatorItem.next();
/*     */       }
/*     */ 
/* 288 */       if ((logicOperatorItem != null) && (getConditionRoot().isLogicSecondLevelDevider(logicOperatorItem.getNode())))
/*     */       {
/* 291 */         StringBuilder itemBuf = printThreeConditionItems(firstItem, logicOperatorItem, secondItem, index);
/*     */ 
/* 293 */         if (itemBuf.length() > 0) {
/* 294 */           printSimpleNode = false;
/* 295 */           buf.append(itemBuf);
/* 296 */           getConditionRoot().setLoopIndex(3);
/*     */         }
/*     */       }
/*     */ 
/* 300 */       if ((index == 0) && (printSimpleNode) && (resultItem != null) && (getParent() != null) && (getParent().isInLRParnesis()))
/*     */       {
/* 303 */         StringBuilder itemBuf = printFiveLogicConditionItems(firstItem, logicOperatorItem, secondItem, assignOperatorItem, resultItem, index);
/*     */ 
/* 306 */         if (itemBuf.length() < 1) {
/* 307 */           itemBuf = printFiveNonLogicConditionItems(firstItem, logicOperatorItem, secondItem, assignOperatorItem, resultItem, index);
/*     */         }
/*     */ 
/* 311 */         if (itemBuf.length() > 0) {
/* 312 */           printSimpleNode = false;
/* 313 */           buf.append(itemBuf);
/* 314 */           getConditionRoot().setLoopIndex(5);
/*     */         }
/*     */       }
/*     */ 
/* 318 */       if (printSimpleNode) {
/* 319 */         if (isBoolResult()) {
/* 320 */           buf.append("Bool(");
/*     */         }
/* 322 */         buf.append(printNode(getNode()));
/* 323 */         if (isBoolResult()) {
/* 324 */           buf.append(")");
/*     */         }
/*     */       }
/*     */     }
/* 328 */     return buf;
/*     */   }
/*     */ 
/*     */   public boolean hasChildren()
/*     */   {
/* 333 */     return this.children.size() > 0;
/*     */   }
/*     */ 
/*     */   private boolean isNodeInstrument(ASTNode root) {
/* 337 */     boolean result = false;
/* 338 */     ASTNode node = root;
/* 339 */     if ((node != null) && (node.getId() == 52) && (node.getChildren() != null))
/*     */     {
/* 342 */       if (node.getChildren().length == 1)
/* 343 */         node = node.getChildren()[0];
/* 344 */       else if ((node.getChildren().length > 2) && 
/* 345 */         (node.getChildren()[0].getId() == 168) && (node.getChildren()[1].getId() == 66))
/*     */       {
/* 347 */         node = node.getChildren()[0];
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 357 */     if ((node != null) && (node.getText() != null) && (!node.getText().isEmpty())) {
/* 358 */       VariableDeclaration variable = (VariableDeclaration)getConditionRoot().getParserCallback().getCurrentBranchVariableDeclaration(node.getText(), getConditionRoot().getDeclarationRoot());
/*     */ 
/* 362 */       if (variable != null)
/* 363 */         result = variable.isObjectType();
/*     */       else {
/* 365 */         result = isNodeSymbol(node);
/*     */       }
/*     */     }
/*     */ 
/* 369 */     return result;
/*     */   }
/*     */ 
/*     */   private boolean isNodeSymbol(ASTNode root) {
/* 373 */     boolean result = false;
/* 374 */     ASTNode node = root;
/* 375 */     if ((node != null) && (node.getText() != null) && (!node.getText().isEmpty())) {
/* 376 */       if ((node.getText().indexOf("ymbol") > 0) || (node.getText().indexOf("nstrument") > 0))
/*     */       {
/* 378 */         FunctionDeclaration fun = getConditionRoot().getFunctionDeclaration(node.getText());
/*     */ 
/* 380 */         if ((fun != null) && (!fun.isNumeric())) {
/* 381 */           result = true;
/* 382 */         } else if (ArrayHelpers.binarySearch(SYMBOL_FUNCTIONS, node.getText()) > -1)
/*     */         {
/* 384 */           result = true;
/*     */         } else {
/* 386 */           VariableDeclaration var = getConditionRoot().getVariableDeclaration(node.getText(), (Declaration)null);
/*     */ 
/* 389 */           if ((var != null) && (!var.isNumeric())) {
/* 390 */             result = true;
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/* 395 */     else if ((node != null) && (node.getChildren() != null)) {
/* 396 */       for (ASTNode child : node.getChildren()) {
/* 397 */         if (child.getId() == 62)
/*     */           continue;
/* 399 */         if (child.getId() != 168)
/*     */         {
/*     */           continue;
/*     */         }
/*     */ 
/* 405 */         if ((child.getText().indexOf("ymbol") <= 0) && (child.getText().indexOf("nstrument") <= 0))
/*     */           break;
/* 407 */         FunctionDeclaration fun = getConditionRoot().getFunctionDeclaration(child.getText());
/*     */ 
/* 409 */         if ((fun != null) && (!fun.isNumeric())) {
/* 410 */           result = true;
/* 411 */           break;
/* 412 */         }if (ArrayHelpers.binarySearch(SYMBOL_FUNCTIONS, child.getText()) > -1)
/*     */         {
/* 414 */           result = true;
/* 415 */           break;
/*     */         }
/* 417 */         VariableDeclaration var = getConditionRoot().getVariableDeclaration(child.getText(), (Declaration)null);
/*     */ 
/* 420 */         if ((var != null) && (!var.isNumeric())) {
/* 421 */           result = true;
/* 422 */           break;
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 432 */     return result;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Connector-1.1.49.jar
 * Qualified Name:     com.dukascopy.dds2.greed.connector.parser.util.conditions.ExpressionItem
 * JD-Core Version:    0.6.0
 */