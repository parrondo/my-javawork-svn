/*      */ package com.dukascopy.dds2.greed.connector.parser.javacc;
/*      */ 
/*      */ import com.dukascopy.api.JFException;
/*      */ import com.dukascopy.api.connector.helpers.ColorHelpers;
/*      */ import com.dukascopy.dds2.greed.connector.helpers.ArrayHelpers;
/*      */ import com.dukascopy.dds2.greed.connector.helpers.ExternalEngine;
/*      */ import com.dukascopy.dds2.greed.connector.helpers.MathHelpers;
/*      */ import com.dukascopy.dds2.greed.connector.parser.util.AccessSpecifierDeclaration;
/*      */ import com.dukascopy.dds2.greed.connector.parser.util.BreakStatement;
/*      */ import com.dukascopy.dds2.greed.connector.parser.util.CaseStatement;
/*      */ import com.dukascopy.dds2.greed.connector.parser.util.ClassDeclaration;
/*      */ import com.dukascopy.dds2.greed.connector.parser.util.CommonStatement;
/*      */ import com.dukascopy.dds2.greed.connector.parser.util.ContinueStatement;
/*      */ import com.dukascopy.dds2.greed.connector.parser.util.Declaration;
/*      */ import com.dukascopy.dds2.greed.connector.parser.util.DeclarationHelpers;
/*      */ import com.dukascopy.dds2.greed.connector.parser.util.DefaultStatement;
/*      */ import com.dukascopy.dds2.greed.connector.parser.util.DoStatement;
/*      */ import com.dukascopy.dds2.greed.connector.parser.util.ElseStatement;
/*      */ import com.dukascopy.dds2.greed.connector.parser.util.ForStatement;
/*      */ import com.dukascopy.dds2.greed.connector.parser.util.FunctionDeclaration;
/*      */ import com.dukascopy.dds2.greed.connector.parser.util.GotoStatement;
/*      */ import com.dukascopy.dds2.greed.connector.parser.util.IDeclaration;
/*      */ import com.dukascopy.dds2.greed.connector.parser.util.IfStatement;
/*      */ import com.dukascopy.dds2.greed.connector.parser.util.ImportDeclaration;
/*      */ import com.dukascopy.dds2.greed.connector.parser.util.ReturnStatement;
/*      */ import com.dukascopy.dds2.greed.connector.parser.util.Statement;
/*      */ import com.dukascopy.dds2.greed.connector.parser.util.SwitchStatement;
/*      */ import com.dukascopy.dds2.greed.connector.parser.util.VariableDeclaration;
/*      */ import com.dukascopy.dds2.greed.connector.parser.util.WhileStatement;
/*      */ import com.dukascopy.dds2.greed.connector.parser.util.conditions.ConditionRoot;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Arrays;
/*      */ import java.util.Collection;
/*      */ import java.util.HashMap;
/*      */ import java.util.Iterator;
/*      */ import java.util.LinkedHashMap;
/*      */ import java.util.List;
/*      */ import java.util.Map;
/*      */ import java.util.Set;
/*      */ import java.util.StringTokenizer;
/*      */ 
/*      */ public final class ParserCallback
/*      */ {
/*   54 */   private JJTCPPParserState jjtree = null;
/*   55 */   private int indicatorBuffers = -1;
/*      */   private LinePositionInputStream fLinePositions;
/*      */   private IStructurizerCallback fCallback;
/*   68 */   Declaration lastParent = null;
/*   69 */   Declaration prevParent = null;
/*      */   public Map<String, String> properties;
/*      */   public Map<String, VariableDeclaration> variables;
/*      */   public Map<String, ImportDeclaration> imports;
/*      */   public Map<String, FunctionDeclaration> functions;
/*      */   public Map<String, ClassDeclaration> classes;
/*   75 */   private String currentFunctionName = null;
/*   76 */   private String currentClassName = null;
/*   77 */   private String currentImportName = null;
/*      */ 
/*      */   public JJTCPPParserState getJjtree()
/*      */   {
/*   58 */     return this.jjtree;
/*      */   }
/*      */ 
/*      */   public void setJjtree(JJTCPPParserState jjtree) {
/*   62 */     this.jjtree = jjtree;
/*      */   }
/*      */ 
/*      */   public FunctionDeclaration getCurrentFunction()
/*      */   {
/*   80 */     FunctionDeclaration func = null;
/*   81 */     if (getCurrentFunctionName() != null) {
/*   82 */       func = (FunctionDeclaration)this.functions.get(getCurrentFunctionName());
/*      */     }
/*   84 */     return func;
/*      */   }
/*      */   public ParserCallback(LinePositionInputStream lpiStream, IStructurizerCallback callback) {
/*   87 */     this.fLinePositions = lpiStream;
/*   88 */     this.fCallback = callback;
/*   89 */     this.variables = new LinkedHashMap();
/*   90 */     this.functions = new LinkedHashMap();
/*   91 */     this.classes = new LinkedHashMap();
/*      */   }
/*      */ 
/*      */   public void functionDeclBegin(Token nameToken, Token firstToken) throws ParseException {
/*   95 */     FunctionDeclaration function = new FunctionDeclaration();
/*   96 */     function.setName(nameToken.image);
/*   97 */     function.setType(firstToken.image);
/*   98 */     function.setFirstToken(firstToken);
/*      */ 
/*  100 */     function.setVisibility("public");
/*      */ 
/*  102 */     if (this.currentImportName != null) {
/*  103 */       function.setVisibility("public");
/*  104 */       ImportDeclaration importDecl = (ImportDeclaration)this.imports.get(this.currentImportName);
/*  105 */       importDecl.addFunction(function);
/*  106 */       setCurrentFunctionName(function.getName());
/*      */     } else {
/*  108 */       if (this.prevParent != null)
/*  109 */         this.prevParent.getFunctions().put(nameToken.image, function);
/*      */       else {
/*  111 */         this.functions.put(nameToken.image, function);
/*      */       }
/*  113 */       setCurrentFunctionName(nameToken.image);
/*  114 */       this.prevParent = this.lastParent;
/*  115 */       this.lastParent = function;
/*      */       try {
/*  117 */         function = DeclarationHelpers.fillFunctionDeclaration(function, firstToken);
/*      */       } catch (Exception e) {
/*  119 */         e.printStackTrace();
/*  120 */         ParseException pex = new ParseException(e.getMessage());
/*  121 */         pex.setStackTrace(e.getStackTrace());
/*  122 */         throw pex;
/*      */       }
/*      */ 
/*  125 */       int declStart = this.fLinePositions.getPosition(firstToken.beginLine, firstToken.beginColumn);
/*      */ 
/*  127 */       int nameStart = this.fLinePositions.getPosition(nameToken.beginLine, nameToken.beginColumn);
/*      */ 
/*  129 */       int nameEnd = this.fLinePositions.getPosition(nameToken.endLine, nameToken.endColumn);
/*      */ 
/*  131 */       function.setDeclBegin(declStart);
/*  132 */       this.fCallback.functionDeclBegin(nameToken.image, nameStart, nameEnd, declStart);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void functionDeclEnd(Token nameToken, Token lastToken) throws ParseException {
/*  137 */     FunctionDeclaration function = null;
/*  138 */     if (this.currentImportName != null) {
/*  139 */       ImportDeclaration importDeclaration = (ImportDeclaration)this.imports.get(this.currentImportName);
/*  140 */       function = (FunctionDeclaration)importDeclaration.getFunctions().get(getCurrentFunctionName());
/*  141 */       ASTNode startNode = DeclarationHelpers.getTokenNode(this.jjtree.getNodes(), function.getFirstToken());
/*      */       try {
/*  143 */         function = DeclarationHelpers.fillFunctionDeclaration(function, startNode);
/*      */       } catch (Exception e) {
/*  145 */         e.printStackTrace();
/*  146 */         ParseException pex = new ParseException(e.getMessage());
/*  147 */         pex.setStackTrace(e.getStackTrace());
/*  148 */         throw pex;
/*      */       }
/*  150 */       function.setVisibility("public");
/*  151 */       importDeclaration.addFunction(function);
/*      */     } else {
/*  153 */       if (this.prevParent != null)
/*  154 */         function = (FunctionDeclaration)this.prevParent.getFunctions().get(nameToken.image);
/*      */       else {
/*  156 */         function = (FunctionDeclaration)this.functions.get(nameToken.image);
/*      */       }
/*      */ 
/*  159 */       if (function != null) {
/*  160 */         function.setLastToken(lastToken);
/*  161 */         setCurrentFunctionName(nameToken.image);
/*  162 */         ASTNode startNode = DeclarationHelpers.getTokenNode(this.jjtree.getNodes(), function.getFirstToken());
/*      */         try {
/*  164 */           function = DeclarationHelpers.fillFunctionDeclaration(function, startNode);
/*      */         } catch (Exception e) {
/*  166 */           e.printStackTrace();
/*  167 */           ParseException pex = new ParseException(e.getMessage());
/*  168 */           pex.setStackTrace(e.getStackTrace());
/*  169 */           throw pex;
/*      */         }
/*      */ 
/*  172 */         this.lastParent = this.prevParent;
/*  173 */         int declEnd = this.fLinePositions.getPosition(lastToken.endLine, lastToken.endColumn);
/*  174 */         function.setDeclEnd(declEnd);
/*  175 */         this.fCallback.functionDeclEnd(declEnd);
/*      */       }
/*      */     }
/*  178 */     setCurrentFunctionName(null);
/*      */   }
/*      */ 
/*      */   public void accessSpecifierDecl(Token firstToken, Token endToken) {
/*  182 */     AccessSpecifierDeclaration accessSpecifier = new AccessSpecifierDeclaration();
/*  183 */     accessSpecifier.setName(firstToken.image);
/*  184 */     setDeclarationParent(accessSpecifier);
/*  185 */     ClassDeclaration classDeclaration = null;
/*  186 */     if (this.currentClassName != null) {
/*  187 */       classDeclaration = (ClassDeclaration)this.classes.get(this.currentClassName);
/*      */     }
/*  189 */     if (classDeclaration != null)
/*  190 */       classDeclaration.getAccessSpecifiers().add(accessSpecifier);
/*      */   }
/*      */ 
/*      */   public void structDeclBegin(Token nameToken, int kind, Token firstToken)
/*      */   {
/*  195 */     ClassDeclaration struct = new ClassDeclaration();
/*  196 */     struct.setName(nameToken.image);
/*  197 */     struct.setFirstToken(firstToken);
/*  198 */     if (kind == 5)
/*  199 */       struct.setType("class");
/*  200 */     else if (kind == 3)
/*  201 */       struct.setType("struct");
/*  202 */     else if (kind == 4) {
/*  203 */       struct.setType("union");
/*      */     }
/*      */ 
/*  206 */     this.classes.put(nameToken.image, struct);
/*  207 */     if ((kind == 5) || (kind == 3)) {
/*  208 */       this.currentClassName = struct.getName();
/*      */     }
/*  210 */     this.prevParent = this.lastParent;
/*  211 */     this.lastParent = struct;
/*      */ 
/*  213 */     int declStart = this.fLinePositions.getPosition(firstToken.beginLine, firstToken.beginColumn);
/*      */ 
/*  215 */     int nameStart = this.fLinePositions.getPosition(nameToken.beginLine, nameToken.beginColumn);
/*      */ 
/*  217 */     int nameEnd = this.fLinePositions.getPosition(nameToken.endLine, nameToken.endColumn);
/*      */ 
/*  220 */     this.fCallback.structDeclBegin(nameToken.image, kind, nameStart, nameEnd, declStart);
/*      */   }
/*      */ 
/*      */   public void structDeclEnd(Token nameToken, Token lastToken)
/*      */   {
/*  225 */     ClassDeclaration struct = (ClassDeclaration)this.classes.get(nameToken.image);
/*  226 */     struct.setLastToken(lastToken);
/*  227 */     this.lastParent = this.prevParent;
/*  228 */     int declEnd = this.fLinePositions.getPosition(lastToken.endLine, lastToken.endColumn);
/*  229 */     this.currentClassName = null;
/*  230 */     this.fCallback.structDeclEnd(declEnd);
/*      */   }
/*      */ 
/*      */   public void defineDecl(String value, String defineString, int line, int column) {
/*  234 */     VariableDeclaration variable = new VariableDeclaration();
/*  235 */     variable.setDefine(true);
/*  236 */     StringTokenizer st = new StringTokenizer(defineString);
/*  237 */     int index = 0;
/*  238 */     String variableValue = "";
/*  239 */     while (st.hasMoreElements()) {
/*  240 */       String token = st.nextToken();
/*  241 */       if ((token.trim().startsWith("#")) || (token.trim().startsWith("define"))) {
/*  242 */         index++;
/*  243 */         continue;
/*      */       }
/*  245 */       variable.setName(token);
/*  246 */       if (st.hasMoreElements()) {
/*  247 */         token = st.nextToken();
/*  248 */         if (token.startsWith("\"")) {
/*  249 */           variableValue = new StringBuilder().append(variableValue).append(defineString.substring(defineString.indexOf("\""), defineString.length())).toString();
/*  250 */           break;
/*  251 */         }if (token.startsWith("'")) {
/*  252 */           variableValue = new StringBuilder().append(variableValue).append(defineString.substring(defineString.indexOf("'"), defineString.length())).toString();
/*  253 */           break;
/*      */         }
/*  255 */         variableValue = new StringBuilder().append(variableValue).append(token).toString();
/*  256 */         break;
/*      */       }
/*      */ 
/*  260 */       index++;
/*      */     }
/*      */ 
/*  263 */     if ((variableValue.equalsIgnoreCase("true")) || (variableValue.equalsIgnoreCase("false")))
/*      */     {
/*  265 */       variable.setType("bool");
/*  266 */     } else if ((variableValue.matches("((-|\\+)?[0-9]+([0-9]+)?)+")) || (variableValue.matches("0x[0-9A-Fa-f]+"))) {
/*  267 */       Long l = Long.decode(variableValue);
/*  268 */       if (l.longValue() < 2147483647L)
/*  269 */         variable.setType("int");
/*      */       else
/*  271 */         variable.setType("long");
/*      */     }
/*  273 */     else if (variableValue.matches("((-|\\+)?[0-9A-Fa-f]+(\\.[0-9A-Fa-f]+)?)+")) {
/*  274 */       Double d = Double.valueOf(variableValue);
/*  275 */       variable.setType("double");
/*  276 */     } else if (variableValue.startsWith("\"")) {
/*  277 */       variable.setType("string");
/*  278 */       if (!variableValue.endsWith("\""))
/*  279 */         variableValue = new StringBuilder().append(variableValue).append("\"").toString();
/*      */     }
/*  281 */     else if (variableValue.startsWith("'")) {
/*  282 */       variable.setType("char");
/*  283 */       if (!variableValue.endsWith("'"))
/*  284 */         variableValue = new StringBuilder().append(variableValue).append("'").toString();
/*      */     }
/*  286 */     else if (ColorHelpers.isContainsColor(variableValue)) {
/*  287 */       variable.setType("Color");
/*      */     }
/*      */ 
/*  291 */     variable.setValue(variableValue);
/*      */ 
/*  293 */     if (this.lastParent == null) {
/*  294 */       this.variables.put(variable.getName(), variable);
/*      */     } else {
/*  296 */       variable.setParent(this.lastParent);
/*  297 */       this.lastParent.variables.put(variable.getName(), variable);
/*      */     }
/*  299 */     int start = this.fLinePositions.getPosition(line, column);
/*  300 */     int end = this.fLinePositions.getPosition(line, column + variable.getName().length()) - 1;
/*      */ 
/*  302 */     this.fCallback.defineDecl(variable.getName(), start, end);
/*      */   }
/*      */ 
/*      */   public void fieldMemberListDeclBegin(boolean isTypedef, Token firstToken, Token lasrToken) {
/*  306 */     ClassDeclaration classDeclaration = null;
/*  307 */     AccessSpecifierDeclaration accessSpecifier = null;
/*  308 */     if (this.currentClassName != null) {
/*  309 */       classDeclaration = (ClassDeclaration)this.classes.get(this.currentClassName);
/*  310 */       if ((classDeclaration != null) && (classDeclaration.getAccessSpecifiers().size() > 0))
/*  311 */         accessSpecifier = (AccessSpecifierDeclaration)classDeclaration.getAccessSpecifiers().get(classDeclaration.getAccessSpecifiers().size() - 1);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void fieldMemberListDeclEnd(boolean isTypedef, Token firstToken, Token lastToken)
/*      */   {
/*  317 */     ClassDeclaration classDeclaration = null;
/*  318 */     AccessSpecifierDeclaration accessSpecifier = null;
/*  319 */     if (this.currentClassName != null) {
/*  320 */       classDeclaration = (ClassDeclaration)this.classes.get(this.currentClassName);
/*  321 */       if ((classDeclaration != null) && (classDeclaration.getAccessSpecifiers().size() > 0))
/*  322 */         accessSpecifier = (AccessSpecifierDeclaration)classDeclaration.getAccessSpecifiers().get(classDeclaration.getAccessSpecifiers().size() - 1);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void fieldMemberDecl(Token nameToken, Token firstToken, Token lastToken)
/*      */   {
/*  355 */     ClassDeclaration classDeclaration = null;
/*  356 */     AccessSpecifierDeclaration accessSpecifier = null;
/*  357 */     if (this.currentClassName != null) {
/*  358 */       classDeclaration = (ClassDeclaration)this.classes.get(this.currentClassName);
/*  359 */       if ((classDeclaration != null) && (classDeclaration.getAccessSpecifiers().size() > 0)) {
/*  360 */         accessSpecifier = (AccessSpecifierDeclaration)classDeclaration.getAccessSpecifiers().get(classDeclaration.getAccessSpecifiers().size() - 1);
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  367 */     if (accessSpecifier != null) {
/*  368 */       VariableDeclaration variable = initVariableDeclaration(nameToken, firstToken, lastToken, false);
/*  369 */       if (variable != null)
/*  370 */         accessSpecifier.variables.put(variable.getName(), variable);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void fieldDecl(Token nameToken, Token firstToken, Token lastToken) throws JFException
/*      */   {
/*  376 */     if (nameToken.kind != 164) {
/*  377 */       expressionSelection(firstToken, lastToken);
/*      */     } else {
/*  379 */       ASTNode node = DeclarationHelpers.getTokenNode(this.jjtree.getNodes(), firstToken);
/*  380 */       VariableDeclaration variable = null;
/*  381 */       ASTNode declarationListNode = DeclarationHelpers.getFirstIdNode(node, 15);
/*  382 */       if (declarationListNode != null) {
/*  383 */         variable = initVariableDeclaration(declarationListNode, firstToken, nameToken);
/*  384 */         variable.setParent(this.lastParent);
/*  385 */         variable.setLastToken(lastToken);
/*      */       }
/*      */ 
/*  388 */       if ((this.lastParent != null) && (variable != null) && (variable.getValue() != null) && (!variable.getValue().isEmpty())) {
/*  389 */         CommonStatement statement = new CommonStatement();
/*  390 */         statement.setVariableInitialization(true);
/*  391 */         statement.setVariable(variable);
/*  392 */         variable.setChangeParent(true);
/*  393 */         statement.setParent(this.lastParent);
/*      */ 
/*  395 */         ASTNode startNode = DeclarationHelpers.getTokenNode(this.jjtree.getNodes(), nameToken);
/*  396 */         statement.setExpressionNode(variable.getExpressionNode());
/*      */ 
/*  398 */         List names = getVariableNamesInExpression(variable.getExpressionNode(), null);
/*  399 */         if (names.size() > 0) {
/*  400 */           List variableList = fillVariableList(names);
/*  401 */           if (variableList.size() > 0) {
/*  402 */             getExpressionHighestLevelParent(variableList, this.lastParent);
/*      */           }
/*      */         }
/*  405 */         statement.setStartNode(startNode);
/*  406 */         statement.setFirstToken(nameToken);
/*  407 */         statement.setLastToken(lastToken);
/*  408 */         statement.setParent(this.lastParent);
/*      */ 
/*  410 */         if (this.lastParent != null) {
/*  411 */           this.lastParent.children.add(statement);
/*      */         }
/*      */       }
/*      */ 
/*  415 */       int declStart = this.fLinePositions.getPosition(firstToken.beginLine, firstToken.beginColumn);
/*      */ 
/*  417 */       int declEnd = this.fLinePositions.getPosition(lastToken.endLine, lastToken.endColumn);
/*      */ 
/*  419 */       int nameStart = this.fLinePositions.getPosition(nameToken.beginLine, nameToken.beginColumn);
/*      */ 
/*  421 */       int nameEnd = this.fLinePositions.getPosition(nameToken.endLine, nameToken.endColumn);
/*      */ 
/*  424 */       this.fCallback.fieldDecl(nameToken.image, nameStart, nameEnd, declStart, declEnd);
/*      */ 
/*  427 */       if (this.lastParent == null) {
/*  428 */         this.variables.put(nameToken.image, variable);
/*      */       }
/*  430 */       else if (getCurrentFunction().variables.get(variable.getName()) == null) {
/*  431 */         getCurrentFunction().variables.put(variable.getName(), variable);
/*  432 */         getCurrentFunction().addDeclaredVariable(variable);
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   public void fieldListDecl(Token nameToken, Token firstToken, Token lastToken)
/*      */     throws JFException
/*      */   {
/*  445 */     ASTNode startNode = DeclarationHelpers.getTokenNode(this.jjtree.getNodes(), firstToken);
/*  446 */     ASTNode declarationListNode = DeclarationHelpers.getFirstIdNode(startNode, 14);
/*  447 */     for (ASTNode node : declarationListNode.getChildren())
/*      */     {
/*  449 */       if (node.getId() == 15) {
/*  450 */         VariableDeclaration variable = initVariableDeclaration(node, firstToken, nameToken);
/*      */ 
/*  452 */         variable.setLastToken(lastToken);
/*  453 */         if (this.lastParent != null)
/*      */         {
/*  455 */           if (getCurrentFunction().variables.get(variable.getName()) == null) {
/*  456 */             variable.setParent(getCurrentFunction());
/*  457 */             getCurrentFunction().variables.put(variable.getName(), variable);
/*      */           }
/*      */         }
/*  460 */         else this.variables.put(variable.getName(), variable);
/*      */ 
/*  462 */         if (getCurrentFunction() != null)
/*  463 */           getCurrentFunction().addDeclaredVariable(variable);
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   public void fieldCounterDecl(Token nameToken, Token firstToken, Token lastToken)
/*      */   {
/*  470 */     VariableDeclaration variable = null;
/*  471 */     if (firstToken.kind == 40) {
/*  472 */       firstToken = firstToken.next;
/*      */     }
/*  474 */     if ((firstToken.kind != 164) && (firstToken.kind != 44)) {
/*  475 */       nameToken = firstToken.next;
/*      */ 
/*  477 */       variable = initVariableDeclaration(nameToken, firstToken, lastToken, true);
/*  478 */       int declStart = this.fLinePositions.getPosition(firstToken.beginLine, firstToken.beginColumn);
/*      */ 
/*  480 */       int declEnd = this.fLinePositions.getPosition(lastToken.endLine, lastToken.endColumn);
/*      */ 
/*  482 */       int nameStart = this.fLinePositions.getPosition(nameToken.beginLine, nameToken.beginColumn);
/*      */ 
/*  484 */       int nameEnd = this.fLinePositions.getPosition(nameToken.endLine, nameToken.endColumn);
/*      */ 
/*  487 */       this.fCallback.fieldDecl(nameToken.image, nameStart, nameEnd, declStart, declEnd);
/*      */     }
/*      */ 
/*  490 */     if ((this.lastParent != null) && (variable != null))
/*      */     {
/*  492 */       this.lastParent.variables.clear();
/*  493 */       variable.setParent(getCurrentFunction());
/*      */ 
/*  495 */       getCurrentFunction().variables.put(variable.getName(), variable);
/*  496 */       if (getCurrentFunction() != null)
/*  497 */         getCurrentFunction().addDeclaredVariable(variable);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void fieldCounterDeclOld111(Token nameToken, Token firstToken, Token lastToken)
/*      */   {
/*  503 */     ASTNode startNode = DeclarationHelpers.getTokenNode(this.jjtree.getNodes(), firstToken);
/*  504 */     VariableDeclaration variable = null;
/*      */ 
/*  506 */     if (firstToken.kind == 40) {
/*  507 */       firstToken = firstToken.next;
/*  508 */       if ((firstToken.kind != 164) && (firstToken.kind != 44)) {
/*  509 */         nameToken = firstToken.next;
/*      */ 
/*  511 */         variable = initVariableDeclaration(nameToken, firstToken, lastToken, true);
/*      */ 
/*  514 */         int declStart = this.fLinePositions.getPosition(firstToken.beginLine, firstToken.beginColumn);
/*      */ 
/*  516 */         int declEnd = this.fLinePositions.getPosition(lastToken.endLine, lastToken.endColumn);
/*      */ 
/*  518 */         int nameStart = this.fLinePositions.getPosition(nameToken.beginLine, nameToken.beginColumn);
/*      */ 
/*  520 */         int nameEnd = this.fLinePositions.getPosition(nameToken.endLine, nameToken.endColumn);
/*      */ 
/*  523 */         this.fCallback.fieldDecl(nameToken.image, nameStart, nameEnd, declStart, declEnd);
/*      */       }
/*      */       else
/*      */       {
/*  527 */         VariableDeclaration var = null;
/*  528 */         FunctionDeclaration function = null;
/*  529 */         if (getCurrentFunctionName() != null) {
/*  530 */           function = (FunctionDeclaration)this.functions.get(getCurrentFunctionName());
/*      */         }
/*  532 */         String name = nameToken.image;
/*  533 */         if ((!this.lastParent.getVariables().containsKey(name)) && (!this.variables.containsKey(name)) && (!function.containVariable(name))) {
/*  534 */           Declaration parent = null;
/*      */ 
/*  536 */           Declaration variableParent = getVariableDeclaration(function, name);
/*  537 */           if (variableParent != null) {
/*  538 */             parent = getCommonParent(this.lastParent, variableParent);
/*  539 */             var = (VariableDeclaration)variableParent.getVariables().get(name);
/*      */           }
/*      */ 
/*  542 */           if ((parent != null) && (var != null) && 
/*  543 */             ((variableParent instanceof ForStatement)) && (var.isCounter())) {
/*  544 */             VariableDeclaration newVar = new VariableDeclaration();
/*  545 */             newVar.setType(var.getType());
/*  546 */             newVar.setName(var.getName());
/*  547 */             newVar.setValue(var.getValue());
/*  548 */             newVar.setCounter(var.isCounter());
/*  549 */             newVar.setParent(this.lastParent);
/*  550 */             this.lastParent.variables.put(name, newVar);
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */     else {
/*  556 */       variable = initVariableDeclaration(nameToken, firstToken, lastToken, true);
/*  557 */       variable.setParent(this.lastParent);
/*  558 */       if ((variable.getType() == null) || (variable.getType().isEmpty())) {
/*  559 */         variable.setType("int");
/*      */       }
/*      */ 
/*  562 */       FunctionDeclaration function = null;
/*  563 */       if (getCurrentFunctionName() != null) {
/*  564 */         function = (FunctionDeclaration)this.functions.get(getCurrentFunctionName());
/*      */       }
/*  566 */       if ((this.lastParent.getVariables().containsKey(variable.getName())) || (this.variables.containsKey(variable.getName())) || (function.containVariable(variable.getName())))
/*      */       {
/*  568 */         variable.setDeclaredUpper(true);
/*      */       }
/*  570 */       int declStart = this.fLinePositions.getPosition(firstToken.beginLine, firstToken.beginColumn);
/*      */ 
/*  572 */       int declEnd = this.fLinePositions.getPosition(lastToken.endLine, lastToken.endColumn);
/*      */ 
/*  574 */       int nameStart = this.fLinePositions.getPosition(nameToken.beginLine, nameToken.beginColumn);
/*      */ 
/*  576 */       int nameEnd = this.fLinePositions.getPosition(nameToken.endLine, nameToken.endColumn);
/*      */ 
/*  579 */       this.fCallback.fieldDecl(nameToken.image, nameStart, nameEnd, declStart, declEnd);
/*      */     }
/*      */ 
/*  585 */     if ((this.lastParent != null) && (variable != null))
/*      */     {
/*  587 */       getCurrentFunction().variables.put(variable.getName(), variable);
/*  588 */       if (getCurrentFunction() != null)
/*  589 */         getCurrentFunction().addDeclaredVariable(variable);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void fieldCounterListDecl(Token nameToken, Token firstToken, Token lastToken) throws JFException
/*      */   {
/*  595 */     if (firstToken != null) {
/*  596 */       ASTNode startNode = DeclarationHelpers.getTokenNode(this.jjtree.getNodes(), nameToken);
/*  597 */       ASTNode declarationListNode = DeclarationHelpers.getFirstIdNode(startNode, 14);
/*      */ 
/*  599 */       for (ASTNode node : declarationListNode.getChildren())
/*      */       {
/*  601 */         if (node.getId() == 15) {
/*  602 */           VariableDeclaration variable = initVariableDeclaration(node, firstToken, lastToken);
/*  603 */           variable.setType("int");
/*  604 */           variable.setPrefix("");
/*  605 */           variable.setParent(getCurrentFunction());
/*  606 */           if (node.getChildren() != null) {
/*  607 */             for (ASTNode child : node.getChildren()) {
/*  608 */               if (child.getId() == 29) {
/*  609 */                 variable.setName(child.getText());
/*      */               }
/*  611 */               if ((child.getId() == 50) || (child.getId() == 49)) {
/*  612 */                 variable.setExpressionNode(child);
/*      */               }
/*      */             }
/*      */           }
/*  616 */           if (this.lastParent != null) {
/*  617 */             this.lastParent.variables.clear();
/*  618 */             getCurrentFunction().variables.put(variable.getName(), variable);
/*  619 */             if (getCurrentFunction() != null) {
/*  620 */               getCurrentFunction().addDeclaredVariable(variable);
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/*  625 */       int declStart = this.fLinePositions.getPosition(firstToken.beginLine, firstToken.beginColumn);
/*      */ 
/*  627 */       int declEnd = this.fLinePositions.getPosition(lastToken.endLine, lastToken.endColumn);
/*      */ 
/*  629 */       int nameStart = this.fLinePositions.getPosition(nameToken.beginLine, nameToken.beginColumn);
/*      */ 
/*  631 */       int nameEnd = this.fLinePositions.getPosition(nameToken.endLine, nameToken.endColumn);
/*      */ 
/*  634 */       this.fCallback.fieldListDecl(nameToken.image, nameStart, nameEnd, declStart, declEnd);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void fieldCounterListDecl_Old(Token nameToken, Token firstToken, Token lastToken)
/*      */   {
/*  640 */     if ((firstToken != null) && (firstToken.kind == 40)) {
/*  641 */       firstToken = firstToken.next;
/*  642 */       if ((this.lastParent instanceof Statement)) {
/*  643 */         ((Statement)this.lastParent).setHasDeclarationList(true);
/*      */       }
/*  645 */       if (firstToken.kind != 178) {
/*  646 */         nameToken = firstToken.next;
/*      */       }
/*  648 */       VariableDeclaration variable = initVariableDeclaration(nameToken, firstToken, lastToken, false);
/*      */ 
/*  651 */       variable.setParent(this.lastParent);
/*  652 */       if (this.lastParent != null)
/*      */       {
/*  654 */         getCurrentFunction().variables.put(variable.getName(), variable);
/*  655 */         if (getCurrentFunction() != null)
/*  656 */           getCurrentFunction().addDeclaredVariable(variable);
/*      */       }
/*      */     }
/*  659 */     else if (firstToken != null) {
/*  660 */       ASTNode startNode = DeclarationHelpers.getTokenNode(this.jjtree.getNodes(), nameToken);
/*      */ 
/*  665 */       ASTNode declarationListNode = DeclarationHelpers.getFirstIdNode(startNode, 14);
/*      */ 
/*  667 */       for (ASTNode node : declarationListNode.getChildren())
/*      */       {
/*  669 */         if (node.getId() == 15) {
/*  670 */           VariableDeclaration variable = initVariableDeclaration(nameToken, firstToken, lastToken, false);
/*      */ 
/*  672 */           variable.setParent(getCurrentFunction());
/*      */ 
/*  674 */           if (node.getChildren() != null) {
/*  675 */             for (ASTNode child : node.getChildren()) {
/*  676 */               if (child.getId() == 29) {
/*  677 */                 variable.setName(child.getText());
/*      */               }
/*  679 */               if ((child.getId() == 50) || (child.getId() == 49)) {
/*  680 */                 variable.setExpressionNode(child);
/*      */               }
/*      */             }
/*      */ 
/*      */           }
/*      */ 
/*  686 */           if (this.lastParent == null)
/*      */             continue;
/*  688 */           this.lastParent.variables.clear();
/*  689 */           getCurrentFunction().variables.put(variable.getName(), variable);
/*  690 */           if (getCurrentFunction() != null) {
/*  691 */             getCurrentFunction().addDeclaredVariable(variable);
/*      */           }
/*      */         }
/*      */       }
/*      */ 
/*  696 */       int declStart = this.fLinePositions.getPosition(firstToken.beginLine, firstToken.beginColumn);
/*      */ 
/*  698 */       int declEnd = this.fLinePositions.getPosition(lastToken.endLine, lastToken.endColumn);
/*      */ 
/*  700 */       int nameStart = this.fLinePositions.getPosition(nameToken.beginLine, nameToken.beginColumn);
/*      */ 
/*  702 */       int nameEnd = this.fLinePositions.getPosition(nameToken.endLine, nameToken.endColumn);
/*      */ 
/*  705 */       this.fCallback.fieldListDecl(nameToken.image, nameStart, nameEnd, declStart, declEnd);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void superDecl(String name)
/*      */   {
/*  711 */     this.fCallback.superDecl(name);
/*      */   }
/*      */ 
/*      */   public void includeDecl(String name, int line, int column) {
/*  715 */     int start = this.fLinePositions.getPosition(line, column);
/*  716 */     int end = this.fLinePositions.getPosition(line, column + name.length()) - 1;
/*  717 */     this.fCallback.includeDecl(name, start, end);
/*      */   }
/*      */ 
/*      */   public void importDecl(String name, StringBuilder value, int line, int column) {
/*  721 */     if ((name != null) && (!name.trim().isEmpty())) {
/*  722 */       ImportDeclaration importDecl = new ImportDeclaration();
/*  723 */       importDecl.setName(name.trim());
/*  724 */       if (this.imports == null) {
/*  725 */         this.imports = new LinkedHashMap();
/*      */       }
/*  727 */       this.imports.put(name.trim(), importDecl);
/*  728 */       this.currentImportName = name.trim();
/*      */     } else {
/*  730 */       this.currentImportName = null;
/*      */     }
/*      */   }
/*      */ 
/*      */   private String getTokenWithoutComma(String value)
/*      */   {
/*  738 */     String result = "";
/*  739 */     if (value.trim().length() > 2) {
/*  740 */       result = value.trim();
/*  741 */       if ((value.startsWith("\"")) || (value.startsWith("'"))) {
/*  742 */         result = result.substring(1);
/*      */       }
/*  744 */       if ((value.endsWith("\"")) || (value.endsWith("'"))) {
/*  745 */         result = result.substring(0, result.length() - 1);
/*      */       }
/*      */     }
/*  748 */     else if ((!value.startsWith("\"")) && (!value.startsWith("'")) && (!value.endsWith("\"")) && (!value.endsWith("'")))
/*      */     {
/*  750 */       result = value;
/*      */     }
/*      */ 
/*  753 */     return result;
/*      */   }
/*      */ 
/*      */   public void propertyDecl(String propertyName, String defineString, int line, int column) {
/*  757 */     if (this.properties == null) {
/*  758 */       this.properties = new HashMap();
/*      */     }
/*  760 */     StringTokenizer st = new StringTokenizer(defineString);
/*      */ 
/*  762 */     int index = 0;
/*  763 */     StringBuilder propertyValue = new StringBuilder();
/*  764 */     while (st.hasMoreElements()) {
/*  765 */       String token = st.nextToken();
/*  766 */       if ((token.trim().startsWith("#")) || (token.trim().startsWith("property"))) {
/*  767 */         index++;
/*  768 */         continue;
/*      */       }
/*  770 */       if (index < 2) {
/*  771 */         propertyName = token;
/*      */       } else {
/*  773 */         if ((token.startsWith("//")) || (token.startsWith("/*")))
/*      */           break;
/*  775 */         if (token.contains("//")) {
/*  776 */           propertyValue.append(getTokenWithoutComma(token.substring(0, token.indexOf("//"))));
/*  777 */           index++;
/*  778 */           break;
/*  779 */         }if (token.contains("/*")) {
/*  780 */           propertyValue.append(getTokenWithoutComma(token.substring(0, token.indexOf("/*"))));
/*  781 */           index++;
/*  782 */           break;
/*      */         }
/*  784 */         propertyValue.append(getTokenWithoutComma(token));
/*      */       }
/*      */ 
/*  787 */       index++;
/*  788 */       if (index > 2) {
/*  789 */         propertyValue.append(" ");
/*      */       }
/*      */     }
/*      */ 
/*  793 */     if (index < 3)
/*  794 */       this.properties.put(propertyName, "true");
/*      */     else {
/*  796 */       this.properties.put(propertyName, propertyValue.toString().trim());
/*      */     }
/*      */ 
/*  799 */     int start = this.fLinePositions.getPosition(line, column);
/*  800 */     int end = this.fLinePositions.getPosition(line, column + propertyName.length()) - 1;
/*  801 */     this.fCallback.propertyDecl(propertyName, start, end);
/*      */   }
/*      */ 
/*      */   public void whileIterationBegin(Token token) {
/*  805 */     WhileStatement statement = new WhileStatement();
/*  806 */     statement.setFirstToken(token);
/*  807 */     setDeclarationParent(statement);
/*  808 */     this.fCallback.whileIterationBegin(token);
/*      */   }
/*      */ 
/*      */   public void whileIterationEnd(Token token) {
/*  812 */     WhileStatement statement = (WhileStatement)this.prevParent.getChildren().get(this.prevParent.getChildren().size() - 1);
/*      */ 
/*  814 */     ASTNode startNode = DeclarationHelpers.getTokenNode(this.jjtree.getNodes(), statement.getFirstToken());
/*      */ 
/*  816 */     statement.setStartNode(startNode);
/*  817 */     this.lastParent.setLastToken(token);
/*      */ 
/*  819 */     ASTNode expressionNode = DeclarationHelpers.getFirstIdNode(startNode, 50);
/*  820 */     if (expressionNode == null) {
/*  821 */       expressionNode = DeclarationHelpers.getFirstIdNode(startNode, 49);
/*      */     }
/*      */ 
/*  824 */     ConditionRoot conditionRoot = new ConditionRoot();
/*  825 */     conditionRoot.makeConditionItems(expressionNode, null);
/*  826 */     conditionRoot.setParserCallback(this);
/*  827 */     conditionRoot.setDeclarationRoot(statement);
/*  828 */     statement.setConditionRoot(conditionRoot);
/*      */ 
/*  836 */     this.lastParent = this.prevParent;
/*  837 */     this.prevParent = this.lastParent.getParent();
/*  838 */     this.fCallback.whileIterationEnd(token);
/*      */   }
/*      */ 
/*      */   public void forIterationBegin(Token token) {
/*  842 */     ForStatement statement = new ForStatement();
/*  843 */     ASTNode startNode = DeclarationHelpers.getTokenNode(this.jjtree.getNodes(), statement.getFirstToken());
/*      */ 
/*  845 */     statement.setStartNode(startNode);
/*  846 */     statement.setFirstToken(token);
/*  847 */     setDeclarationParent(statement);
/*  848 */     this.fCallback.forIterationBegin(token);
/*      */   }
/*      */ 
/*      */   public void forIterationEnd(Token token) {
/*  852 */     ForStatement statement = (ForStatement)this.prevParent.getChildren().get(this.prevParent.getChildren().size() - 1);
/*      */ 
/*  854 */     ASTNode startNode = DeclarationHelpers.getTokenNode(this.jjtree.getNodes(), statement.getFirstToken());
/*      */ 
/*  856 */     statement.setStartNode(startNode);
/*  857 */     this.lastParent.setLastToken(token);
/*  858 */     FunctionDeclaration function = null;
/*  859 */     if (getCurrentFunctionName() != null) {
/*  860 */       function = (FunctionDeclaration)this.functions.get(getCurrentFunctionName());
/*      */     }
/*      */ 
/*  864 */     boolean isCommonStatement = false;
/*  865 */     boolean parentLevelUpper = false;
/*  866 */     ASTNode countersNode = DeclarationHelpers.getFirstIdNode(startNode, 14);
/*      */ 
/*  868 */     if (countersNode != null)
/*      */     {
/*  870 */       for (int i = 0; i < countersNode.getChildren().length; i++) {
/*  871 */         parentLevelUpper = false;
/*  872 */         ASTNode child = countersNode.getChildren()[i];
/*  873 */         if (child.getId() == 15) {
/*  874 */           int index = 0;
/*  875 */           VariableDeclaration newVar = new VariableDeclaration();
/*  876 */           newVar.setParent(statement);
/*      */ 
/*  878 */           if (child.getChildren()[index].getId() == 13) {
/*  879 */             if (this.lastParent.variables.size() > 0) {
/*  880 */               newVar.setCounter(false);
/*  881 */               parentLevelUpper = true;
/*      */             }
/*  883 */             newVar.setType(child.getChildren()[(index++)].getText());
/*  884 */             if (!newVar.getType().equals("int")) {
/*  885 */               newVar.setType("int");
/*      */             }
/*      */ 
/*  888 */             newVar.setName(child.getChildren()[(index++)].getText());
/*      */           }
/*  890 */           isCommonStatement = false;
/*      */ 
/*  892 */           if (child.getChildren()[index].getId() == 29)
/*      */           {
/*  896 */             String name = child.getChildren()[(index++)].getText();
/*      */ 
/*  898 */             newVar.setName(name);
/*  899 */             VariableDeclaration var = null;
/*      */ 
/*  901 */             if ((!this.lastParent.getVariables().containsKey(name)) && (!this.variables.containsKey(name)) && (!function.containVariable(name)))
/*      */             {
/*  904 */               Declaration parent = null;
/*  905 */               Declaration variableParent = getVariableDeclaration(function, name);
/*      */ 
/*  907 */               if (variableParent != null) {
/*  908 */                 parent = getCommonParent(statement, variableParent);
/*      */ 
/*  910 */                 var = (VariableDeclaration)variableParent.getVariables().get(name);
/*      */               }
/*      */ 
/*  913 */               if ((parent != null) && (var != null)) {
/*  914 */                 newVar.setDeclaredUpper(true);
/*  915 */                 if (((variableParent instanceof ForStatement)) && (var.isCounter()))
/*      */                 {
/*  917 */                   newVar.setType(var.getType());
/*  918 */                   newVar.setName(var.getName());
/*      */ 
/*  921 */                   newVar.setParent(this.lastParent);
/*  922 */                   this.lastParent.variables.put(name, newVar);
/*      */                 } else {
/*  924 */                   variableParent.getVariables().remove(name);
/*      */ 
/*  926 */                   var.setParent(parent);
/*  927 */                   parent.variables.put(name, var);
/*      */                 }
/*      */               }
/*      */             } else {
/*  931 */               var = (VariableDeclaration)this.lastParent.getVariables().get(name);
/*      */ 
/*  933 */               if (var == null) {
/*  934 */                 var = (VariableDeclaration)function.getVariables().get(name);
/*      */               }
/*      */ 
/*  937 */               if (var == null) {
/*  938 */                 var = (VariableDeclaration)this.variables.get(name);
/*      */               }
/*      */ 
/*  942 */               newVar.setDeclaredUpper(true);
/*  943 */               newVar.setType(var.getType());
/*  944 */               newVar.setName(var.getName());
/*  945 */               isCommonStatement = true;
/*  946 */               CommonStatement commonStatement = new CommonStatement();
/*  947 */               commonStatement.setVariable(var);
/*      */ 
/*  949 */               commonStatement.setParent(this.lastParent.getParent());
/*      */ 
/*  951 */               this.lastParent.variables.remove(var.getName());
/*  952 */               if (this.lastParent.getParent() != null) {
/*  953 */                 this.lastParent.getParent().children.remove(statement);
/*  954 */                 this.lastParent.getParent().children.add(commonStatement);
/*  955 */                 this.lastParent.getParent().children.add(statement);
/*      */               }
/*      */             }
/*      */           }
/*  959 */           if ((index < child.getChildren().length) && (child.getChildren()[index].getId() == 74))
/*      */           {
/*  961 */             index++;
/*      */           }
/*  963 */           if ((index < child.getChildren().length) && ((child.getChildren()[index].getId() == 50) || (child.getId() == 49)))
/*      */           {
/*  965 */             newVar.setValue(DeclarationHelpers.getAssignmentExpression(child.getChildren()[index]));
/*      */           }
/*      */ 
/*  974 */           if (getCurrentFunction() != null) {
/*  975 */             getCurrentFunction().addDeclaredVariable(newVar);
/*      */           }
/*      */ 
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  984 */     this.lastParent = this.prevParent;
/*  985 */     this.prevParent = this.lastParent.getParent();
/*  986 */     this.fCallback.forIterationEnd(token);
/*      */   }
/*      */ 
/*      */   public void doIterationBegin(Token token) {
/*  990 */     DoStatement statement = new DoStatement();
/*  991 */     statement.setFirstToken(token);
/*  992 */     setDeclarationParent(statement);
/*  993 */     this.fCallback.doIterationBegin(token);
/*      */   }
/*      */ 
/*      */   public void doIterationEnd(Token token) {
/*  997 */     DoStatement statement = (DoStatement)this.prevParent.getChildren().get(this.prevParent.getChildren().size() - 1);
/*      */ 
/*  999 */     ASTNode startNode = DeclarationHelpers.getTokenNode(this.jjtree.getNodes(), statement.getFirstToken());
/*      */ 
/* 1001 */     statement.setStartNode(startNode);
/* 1002 */     this.lastParent.setLastToken(token);
/* 1003 */     this.lastParent = this.prevParent;
/* 1004 */     this.prevParent = this.lastParent.getParent();
/* 1005 */     this.fCallback.doIterationEnd(token);
/*      */   }
/*      */ 
/*      */   public void ifSelectionBegin(Token token) {
/* 1009 */     IfStatement statement = new IfStatement();
/* 1010 */     statement.setFirstToken(token);
/* 1011 */     if ((this.lastParent instanceof ElseStatement)) {
/* 1012 */       IfStatement ifParent = ((IfStatement)((ElseStatement)this.lastParent).getParent()).getFirstIfInChain();
/* 1013 */       statement.setFirstIfInChain(ifParent);
/*      */     }
/*      */ 
/* 1016 */     setDeclarationParent(statement);
/* 1017 */     this.fCallback.ifSelectionBegin(token);
/*      */   }
/*      */ 
/*      */   public void ifSelectionEnd(Token token) {
/* 1021 */     IfStatement statement = (IfStatement)this.prevParent.getChildren().get(this.prevParent.getChildren().size() - 1);
/* 1022 */     ASTNode startNode = DeclarationHelpers.getTokenNode(this.jjtree.getNodes(), statement.getFirstToken());
/* 1023 */     statement.setStartNode(startNode);
/* 1024 */     this.lastParent.setLastToken(token);
/* 1025 */     ASTNode expressionNode = DeclarationHelpers.getFirstIdNode(startNode, 50);
/* 1026 */     if (expressionNode == null) {
/* 1027 */       expressionNode = DeclarationHelpers.getFirstIdNode(startNode, 49);
/*      */     }
/* 1029 */     ConditionRoot conditionRoot = new ConditionRoot();
/* 1030 */     conditionRoot.makeConditionItems(expressionNode, null);
/* 1031 */     conditionRoot.setParserCallback(this);
/* 1032 */     conditionRoot.setDeclarationRoot(statement);
/* 1033 */     statement.setConditionRoot(conditionRoot);
/*      */ 
/* 1041 */     this.lastParent = this.prevParent;
/* 1042 */     this.prevParent = this.lastParent.getParent();
/* 1043 */     this.fCallback.ifSelectionEnd(token);
/*      */   }
/*      */ 
/*      */   public void elseSelectionBegin(Token token) {
/* 1047 */     IfStatement ifstatement = (IfStatement)this.prevParent.getChildren().get(this.prevParent.getChildren().size() - 1);
/* 1048 */     ElseStatement statement = new ElseStatement();
/* 1049 */     ifstatement.setHasElse(true);
/* 1050 */     statement.setFirstToken(token);
/* 1051 */     setDeclarationParent(statement);
/* 1052 */     this.fCallback.elseSelectionBegin(token);
/*      */   }
/*      */ 
/*      */   public void elseSelectionEnd(Token token) {
/* 1056 */     ElseStatement statement = (ElseStatement)this.prevParent.getChildren().get(this.prevParent.getChildren().size() - 1);
/*      */ 
/* 1058 */     ASTNode startNode = DeclarationHelpers.getTokenNode(this.jjtree.getNodes(), statement.getFirstToken());
/*      */ 
/* 1060 */     statement.setStartNode(startNode);
/* 1061 */     this.lastParent.setLastToken(token);
/*      */ 
/* 1063 */     this.lastParent = this.prevParent;
/* 1064 */     this.prevParent = this.lastParent.getParent();
/* 1065 */     this.fCallback.elseSelectionEnd(token);
/*      */   }
/*      */ 
/*      */   public void switchSelectionBegin(Token token) {
/* 1069 */     SwitchStatement statement = new SwitchStatement();
/* 1070 */     statement.setFirstToken(token);
/* 1071 */     setDeclarationParent(statement);
/* 1072 */     this.fCallback.switchSelectionBegin(token);
/*      */   }
/*      */ 
/*      */   public void switchSelectionEnd(Token token) {
/* 1076 */     boolean existDefault = false;
/* 1077 */     SwitchStatement statement = (SwitchStatement)this.prevParent.getChildren().get(this.prevParent.getChildren().size() - 1);
/* 1078 */     ASTNode startNode = DeclarationHelpers.getTokenNode(this.jjtree.getNodes(), statement.getFirstToken());
/* 1079 */     statement.setStartNode(startNode);
/* 1080 */     this.lastParent.setLastToken(token);
/* 1081 */     DefaultStatement defaultStatement = statement.getDefaultStatement();
/* 1082 */     if (defaultStatement == null) {
/* 1083 */       for (IDeclaration child : statement.children) {
/* 1084 */         if ((child instanceof DefaultStatement)) {
/* 1085 */           defaultStatement = (DefaultStatement)child;
/* 1086 */           existDefault = true;
/* 1087 */           break;
/*      */         }
/*      */       }
/*      */     }
/* 1091 */     if (defaultStatement == null) {
/* 1092 */       defaultStatement = new DefaultStatement();
/* 1093 */       statement.children.add(defaultStatement);
/*      */     }
/*      */ 
/* 1096 */     ReturnStatement returnStatement = null;
/* 1097 */     for (IDeclaration child : defaultStatement.children) {
/* 1098 */       if ((child instanceof ReturnStatement)) {
/* 1099 */         returnStatement = (ReturnStatement)child;
/* 1100 */         break;
/*      */       }
/*      */     }
/*      */ 
/* 1104 */     if ((returnStatement == null) && (!existDefault)) {
/* 1105 */       returnStatement = new ReturnStatement();
/* 1106 */       defaultStatement.children.add(returnStatement);
/* 1107 */       returnStatement.setFunctionDeclaration(getFunctionDeclarationPatent(this.lastParent));
/*      */     }
/*      */ 
/* 1110 */     ASTNode expressionNode = DeclarationHelpers.getFirstIdNode(startNode, 50);
/* 1111 */     if (expressionNode == null) {
/* 1112 */       expressionNode = DeclarationHelpers.getFirstIdNode(startNode, 49);
/*      */     }
/* 1114 */     statement.setExpressionNode(expressionNode);
/*      */ 
/* 1116 */     this.lastParent = this.prevParent;
/* 1117 */     this.prevParent = this.lastParent.getParent();
/* 1118 */     this.fCallback.switchSelectionEnd(token);
/*      */   }
/*      */ 
/*      */   private FunctionDeclaration getFunctionDeclarationPatent(Declaration parent) {
/* 1122 */     Declaration result = parent;
/* 1123 */     while ((!(result instanceof FunctionDeclaration)) && (result != null)) {
/* 1124 */       result = result.getParent();
/*      */     }
/* 1126 */     return (FunctionDeclaration)result;
/*      */   }
/*      */ 
/*      */   public void returnStatement(Token beginToken, Token endToken) {
/* 1130 */     FunctionDeclaration function = null;
/* 1131 */     if (this.lastParent.getChildren().size() > 0) {
/* 1132 */       IDeclaration prevStmt = (IDeclaration)this.lastParent.getChildren().get(this.lastParent.getChildren().size() - 1);
/* 1133 */       if (((prevStmt instanceof BreakStatement)) || ((prevStmt instanceof ContinueStatement))) {
/* 1134 */         this.lastParent.getChildren().remove(prevStmt);
/*      */       }
/*      */     }
/*      */ 
/* 1138 */     if (getCurrentFunctionName() != null) {
/* 1139 */       function = (FunctionDeclaration)this.functions.get(getCurrentFunctionName());
/* 1140 */       if (this.lastParent == function) {
/* 1141 */         function.setHasReturn(true);
/*      */       }
/*      */ 
/* 1144 */       if (((this.lastParent instanceof CaseStatement)) && ((this.lastParent.getParent() instanceof SwitchStatement)) && (this.lastParent.getParent().getParent() == function)) {
/* 1145 */         function.setHasReturn(true);
/*      */       }
/*      */ 
/* 1148 */       if (((this.lastParent instanceof ElseStatement)) && (((IfStatement)this.lastParent.getParent()).getFirstIfInChain().getParent() == function)) {
/* 1149 */         function.setHasReturn(true);
/*      */       }
/*      */ 
/* 1152 */       if (((this.lastParent instanceof DefaultStatement)) && ((this.lastParent.getParent() == function) || (this.lastParent.getParent().getParent() == function))) {
/* 1153 */         function.setHasReturn(true);
/*      */       }
/*      */     }
/* 1156 */     boolean hasReturnBefore = false;
/* 1157 */     for (IDeclaration prevStatement : this.lastParent.children) {
/* 1158 */       if ((prevStatement != null) && ((prevStatement instanceof ReturnStatement))) {
/* 1159 */         hasReturnBefore = true;
/*      */       }
/*      */     }
/* 1162 */     if (!hasReturnBefore) {
/* 1163 */       ReturnStatement statement = new ReturnStatement();
/* 1164 */       ASTNode startNode = DeclarationHelpers.getTokenNode(this.jjtree.getNodes(), beginToken);
/* 1165 */       statement.setStartNode(startNode);
/* 1166 */       statement.setFirstToken(beginToken);
/* 1167 */       statement.setLastToken(endToken);
/* 1168 */       statement.setParent(this.lastParent);
/* 1169 */       statement.setFunctionDeclaration(getFunctionDeclarationPatent(this.lastParent));
/* 1170 */       this.lastParent.children.add(statement);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void breakStatement(Token beginToken, Token endToken) {
/* 1175 */     IDeclaration prevStatement = null;
/* 1176 */     if (this.lastParent.getChildren().size() > 0) {
/* 1177 */       prevStatement = (IDeclaration)this.lastParent.getChildren().get(this.lastParent.getChildren().size() - 1);
/*      */     }
/* 1179 */     if (!(prevStatement instanceof ReturnStatement)) {
/* 1180 */       BreakStatement statement = new BreakStatement();
/* 1181 */       ASTNode startNode = DeclarationHelpers.getTokenNode(this.jjtree.getNodes(), beginToken);
/*      */ 
/* 1183 */       statement.setStartNode(startNode);
/* 1184 */       statement.setFirstToken(beginToken);
/* 1185 */       statement.setLastToken(endToken);
/* 1186 */       statement.setParent(this.lastParent);
/* 1187 */       this.lastParent.children.add(statement);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void continueStatement(Token beginToken, Token endToken) {
/* 1192 */     IDeclaration prevStatement = null;
/* 1193 */     if (this.lastParent.getChildren().size() > 0) {
/* 1194 */       prevStatement = (IDeclaration)this.lastParent.getChildren().get(this.lastParent.getChildren().size() - 1);
/*      */     }
/* 1196 */     if (!(prevStatement instanceof ReturnStatement)) {
/* 1197 */       ContinueStatement statement = new ContinueStatement();
/* 1198 */       ASTNode startNode = DeclarationHelpers.getTokenNode(this.jjtree.getNodes(), beginToken);
/* 1199 */       statement.setStartNode(startNode);
/* 1200 */       statement.setFirstToken(beginToken);
/* 1201 */       statement.setLastToken(endToken);
/* 1202 */       statement.setParent(this.lastParent);
/* 1203 */       this.lastParent.children.add(statement);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void gotoStatement(Token beginToken, Token endToken) {
/* 1208 */     GotoStatement statement = new GotoStatement();
/* 1209 */     ASTNode startNode = DeclarationHelpers.getTokenNode(this.jjtree.getNodes(), beginToken);
/* 1210 */     statement.setStartNode(startNode);
/* 1211 */     statement.setFirstToken(beginToken);
/* 1212 */     statement.setLastToken(endToken);
/* 1213 */     statement.setParent(this.lastParent);
/* 1214 */     this.lastParent.children.add(statement);
/*      */   }
/*      */ 
/*      */   public void caseStatementBegin(Token beginToken) {
/* 1218 */     CaseStatement statement = new CaseStatement();
/* 1219 */     statement.setFirstToken(beginToken);
/* 1220 */     setDeclarationParent(statement);
/*      */   }
/*      */ 
/*      */   public void caseStatementEnd(Token endToken) {
/* 1224 */     CaseStatement statement = (CaseStatement)this.prevParent.getChildren().get(this.prevParent.getChildren().size() - 1);
/*      */ 
/* 1226 */     ASTNode startNode = DeclarationHelpers.getTokenNode(this.jjtree.getNodes(), statement.getFirstToken());
/*      */ 
/* 1228 */     statement.setStartNode(startNode);
/* 1229 */     statement.setLastToken(endToken);
/*      */ 
/* 1231 */     this.lastParent = this.prevParent;
/* 1232 */     this.prevParent = this.lastParent.getParent();
/*      */   }
/*      */ 
/*      */   public void defaultStatementBegin(Token beginToken) {
/* 1236 */     DefaultStatement statement = new DefaultStatement();
/* 1237 */     statement.setFirstToken(beginToken);
/* 1238 */     if ((this.lastParent instanceof SwitchStatement)) {
/* 1239 */       ((SwitchStatement)this.lastParent).setDefaultStatement(statement);
/*      */     }
/* 1241 */     Declaration switchStatement = this.lastParent.getParent();
/* 1242 */     while ((switchStatement != null) && (!(switchStatement instanceof SwitchStatement))) {
/* 1243 */       switchStatement = switchStatement.getParent();
/*      */     }
/* 1245 */     if ((switchStatement != null) && ((switchStatement instanceof SwitchStatement))) {
/* 1246 */       ((SwitchStatement)switchStatement).setDefaultStatement(statement);
/*      */     }
/* 1248 */     setDeclarationParent(statement);
/*      */   }
/*      */ 
/*      */   public void defaultStatementEnd(Token endToken) {
/* 1252 */     DefaultStatement statement = (DefaultStatement)this.prevParent.getChildren().get(this.prevParent.getChildren().size() - 1);
/*      */ 
/* 1254 */     ASTNode startNode = DeclarationHelpers.getTokenNode(this.jjtree.getNodes(), statement.getFirstToken());
/*      */ 
/* 1256 */     statement.setStartNode(startNode);
/* 1257 */     statement.setLastToken(endToken);
/*      */ 
/* 1259 */     this.lastParent = this.prevParent;
/* 1260 */     this.prevParent = this.lastParent.getParent();
/*      */   }
/*      */ 
/*      */   private List<String> getExpressionVariableList(ASTNode expressionNode, List<String> idList)
/*      */   {
/* 1288 */     if (idList == null) {
/* 1289 */       idList = new ArrayList();
/*      */     }
/* 1291 */     if ((expressionNode != null) && (expressionNode.getChildren() != null) && (expressionNode.getChildren().length > 0)) {
/* 1292 */       for (int i = 0; i < expressionNode.getChildren().length; i++) {
/* 1293 */         ASTNode node = expressionNode.getChildren()[i];
/* 1294 */         if (node.getId() == 168) {
/* 1295 */           i++; if (i < expressionNode.getChildren().length) {
/* 1296 */             ASTNode nextNode = expressionNode.getChildren()[i];
/* 1297 */             if (nextNode.getId() != 66) {
/* 1298 */               idList.add(node.getText());
/*      */             }
/*      */           }
/*      */         }
/* 1302 */         getExpressionVariableList(node, idList);
/*      */       }
/*      */     }
/* 1305 */     return idList;
/*      */   }
/*      */ 
/*      */   private boolean isNodeUnaryExpression(ASTNode node) {
/* 1309 */     boolean result = false;
/* 1310 */     if ((node != null) && (node.getChildren() != null) && (node.getChildren().length == 2) && (
/* 1311 */       (node.getChildren()[1].getId() == 103) || (node.getChildren()[1].getId() == 104))) {
/* 1312 */       result = true;
/*      */     }
/*      */ 
/* 1315 */     return result;
/*      */   }
/*      */ 
/*      */   public void expressionSelection(Token beginToken, Token endToken)
/*      */   {
/* 1320 */     String varname = beginToken.image;
/* 1321 */     CommonStatement statement = new CommonStatement();
/* 1322 */     boolean isCall = false;
/* 1323 */     if (beginToken.next.kind == 40) {
/* 1324 */       isCall = true;
/* 1325 */       if (beginToken.next.next.kind == 41) {
/* 1326 */         statement.setUnary(isCall);
/*      */       }
/*      */     }
/*      */ 
/* 1330 */     ASTNode startNode = DeclarationHelpers.getTokenNode(this.jjtree.getNodes(), beginToken);
/*      */ 
/* 1332 */     List list = null;
/* 1333 */     if (!isCall) {
/* 1334 */       list = getCurrentFunction().getDeclaredVariableList(varname);
/* 1335 */       if ((list == null) && (this.variables.containsKey(varname))) {
/* 1336 */         list = new ArrayList();
/* 1337 */         list.add(this.variables.get(varname));
/*      */       }
/*      */ 
/* 1340 */       if (list == null) {
/* 1341 */         VariableDeclaration param = getCurrentFunction().getParamByName(varname);
/* 1342 */         if (param != null) {
/* 1343 */           list = new ArrayList();
/* 1344 */           list.add(param);
/*      */         }
/*      */       }
/*      */     }
/*      */ 
/* 1349 */     if ((list != null) && (list.size() > 0)) {
/* 1350 */       for (VariableDeclaration variable : list) {
/* 1351 */         if (variable.isCounter()) {
/* 1352 */           Declaration commonParent = getCommonParent(variable.getParent(), this.lastParent);
/* 1353 */           if (commonParent == null) break;
/* 1354 */           VariableDeclaration newVar = (VariableDeclaration)variable.clone();
/* 1355 */           newVar.setCounter(false);
/* 1356 */           variable.setDeclaredUpper(true);
/* 1357 */           newVar.setParent(commonParent);
/* 1358 */           newVar.setPrefix("");
/* 1359 */           commonParent.variables.put(varname, newVar);
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/* 1365 */       if (list.size() == 1) {
/* 1366 */         statement.setVariable((VariableDeclaration)list.get(0));
/*      */       }
/*      */     }
/* 1369 */     statement.setOperatorNode(DeclarationHelpers.getOperatorNode(startNode));
/*      */ 
/* 1371 */     ASTNode expressionNode = null;
/* 1372 */     if (startNode != null) {
/* 1373 */       expressionNode = DeclarationHelpers.getFirstIdNode(startNode, 50);
/*      */     }
/* 1375 */     if (expressionNode == null) {
/* 1376 */       expressionNode = DeclarationHelpers.getFirstIdNode(startNode, 49);
/*      */     }
/* 1378 */     if (expressionNode != null) {
/* 1379 */       statement.setExpressionNode(expressionNode);
/* 1380 */       if ((statement.getVariable() == null) || (!statement.getVariable().isArray()))
/*      */       {
/* 1383 */         expressionNode = DeclarationHelpers.getFirstIdNode(expressionNode, 50);
/*      */ 
/* 1386 */         if (expressionNode == null) {
/* 1387 */           expressionNode = DeclarationHelpers.getFirstIdNode(startNode, 49);
/*      */         }
/*      */ 
/* 1390 */         if (expressionNode != null) {
/* 1391 */           statement.setExpressionNode(expressionNode);
/* 1392 */           ASTNode castExpr = DeclarationHelpers.getFirstIdNode(expressionNode, 52);
/*      */ 
/* 1395 */           statement.setUnary(isNodeUnaryExpression(castExpr));
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 1403 */     statement.setStartNode(startNode);
/* 1404 */     statement.setFirstToken(beginToken);
/* 1405 */     statement.setLastToken(endToken);
/* 1406 */     statement.setParent(this.lastParent);
/* 1407 */     FunctionDeclaration function = null;
/* 1408 */     if (getCurrentFunctionName() != null) {
/* 1409 */       function = (FunctionDeclaration)this.functions.get(getCurrentFunctionName());
/*      */     }
/*      */ 
/* 1415 */     List idList = new ArrayList();
/* 1416 */     if (!isCall) {
/* 1417 */       idList = getExpressionVariableList(expressionNode, null);
/* 1418 */       idList.add(varname);
/*      */     }
/* 1420 */     for (int i = 0; i < idList.size(); i++) {
/* 1421 */       String name = (String)idList.get(i);
/* 1422 */       VariableDeclaration var = null;
/* 1423 */       if ((!this.lastParent.getVariables().containsKey(name)) && (!this.variables.containsKey(name)) && (!function.containVariable(name))) {
/* 1424 */         Declaration parent = null;
/* 1425 */         Declaration variableParent = getVariableDeclaration(function, name);
/* 1426 */         if (variableParent != null) {
/* 1427 */           parent = getCommonParent(statement, variableParent);
/* 1428 */           var = (VariableDeclaration)variableParent.getVariables().get(name);
/*      */         }
/* 1430 */         if ((parent != null) && (var != null))
/* 1431 */           if (((variableParent instanceof ForStatement)) && (var.isCounter())) {
/* 1432 */             VariableDeclaration newVar = new VariableDeclaration();
/* 1433 */             newVar.setType(var.getType());
/* 1434 */             newVar.setName(var.getName());
/* 1435 */             newVar.setValue(var.getValue());
/* 1436 */             newVar.setCounter(var.isCounter());
/* 1437 */             newVar.setParent(this.lastParent);
/* 1438 */             this.lastParent.variables.put(name, newVar);
/*      */           } else {
/* 1440 */             statement.setVariableInitialization(true);
/* 1441 */             variableParent.getVariables().remove(name);
/* 1442 */             if ((variableParent instanceof SwitchStatement)) {
/* 1443 */               parent = parent.getParent();
/*      */             }
/* 1445 */             var.setParent(parent);
/* 1446 */             parent.variables.put(name, var);
/*      */           }
/*      */       }
/*      */       else {
/* 1450 */         var = (VariableDeclaration)this.lastParent.getVariables().get(name);
/*      */ 
/* 1452 */         if (var == null) {
/* 1453 */           var = function.getVariable(name);
/*      */         }
/* 1455 */         if (var == null) {
/* 1456 */           var = (VariableDeclaration)this.variables.get(name);
/*      */         }
/*      */       }
/* 1459 */       if (var != null) {
/* 1460 */         statement.setVariable(var);
/*      */       }
/*      */     }
/* 1463 */     this.lastParent.children.add(statement);
/*      */   }
/*      */ 
/*      */   public Declaration getVariableDeclaration(Declaration root, String name)
/*      */   {
/* 1472 */     Declaration result = null;
/*      */ 
/* 1474 */     if (root != null) {
/* 1475 */       for (int i = 0; i < root.getChildren().size(); i++) {
/* 1476 */         root.getVariables().keySet();
/* 1477 */         Declaration child = (Declaration)root.getChildren().get(i);
/* 1478 */         child.getVariables().keySet();
/* 1479 */         if (child.getVariables().containsKey(name)) {
/* 1480 */           result = child;
/* 1481 */           break;
/*      */         }
/* 1483 */         result = getVariableDeclaration(child, name);
/*      */ 
/* 1485 */         if (result != null) {
/*      */           break;
/*      */         }
/*      */       }
/*      */     }
/* 1490 */     return result;
/*      */   }
/*      */ 
/*      */   public Declaration getCurrentBranchVariableDeclaration(String name, Declaration root)
/*      */   {
/* 1498 */     Declaration result = null;
/* 1499 */     FunctionDeclaration func = getFunctionDeclarationPatent(root);
/* 1500 */     if (this.variables.containsKey(name)) {
/* 1501 */       result = (Declaration)this.variables.get(name);
/*      */     }
/* 1503 */     if ((result == null) && (func != null)) {
/* 1504 */       result = func.getVariable(name);
/*      */     }
/* 1506 */     if (result == null) {
/* 1507 */       result = getVariableDeclaration(this.lastParent, name);
/*      */     }
/* 1509 */     return result;
/*      */   }
/*      */ 
/*      */   public boolean isStorageClassSpecifier(Token token)
/*      */   {
/* 1514 */     String str = token.image;
/* 1515 */     if (str != null) {
/* 1516 */       if ("JNIEXPORT".equals(str)) {
/* 1517 */         return true;
/*      */       }
/* 1519 */       if (str.startsWith("__declspec")) {
/* 1520 */         return true;
/*      */       }
/*      */     }
/* 1523 */     return false;
/*      */   }
/*      */ 
/*      */   public boolean overreadBlocks() {
/* 1527 */     return true;
/*      */   }
/*      */ 
/*      */   public static Token createToken(String name, Token positions)
/*      */   {
/* 1532 */     Token res = new Token();
/* 1533 */     res.image = name;
/* 1534 */     res.beginColumn = positions.beginColumn;
/* 1535 */     res.beginLine = positions.beginLine;
/* 1536 */     res.endColumn = positions.endColumn;
/* 1537 */     res.endLine = positions.endLine;
/* 1538 */     return res;
/*      */   }
/*      */ 
/*      */   public static Token createToken(String name, Token positionBegin, Token positionEnd)
/*      */   {
/* 1543 */     Token res = new Token();
/* 1544 */     res.image = name;
/* 1545 */     res.beginColumn = positionBegin.beginColumn;
/* 1546 */     res.beginLine = positionBegin.beginLine;
/* 1547 */     res.endColumn = positionEnd.endColumn;
/* 1548 */     res.endLine = positionEnd.endLine;
/* 1549 */     return res;
/*      */   }
/*      */ 
/*      */   private String printChildren(IDeclaration decl)
/*      */   {
/* 1555 */     StringBuilder result = new StringBuilder("");
/* 1556 */     result.append("\r\n");
/* 1557 */     result.append(decl.startText());
/* 1558 */     for (int i = 0; i < decl.getVariables().size(); i++) {
/* 1559 */       VariableDeclaration var = (VariableDeclaration)decl.getVariables().values().toArray()[i];
/*      */ 
/* 1561 */       if (!var.isCounter()) {
/* 1562 */         result.append(var.startText());
/* 1563 */         result.append(var.endText());
/*      */       }
/*      */     }
/* 1566 */     for (IDeclaration child : decl.getChildren()) {
/* 1567 */       result.append(printChildren(child));
/*      */     }
/* 1569 */     result.append(decl.endText());
/* 1570 */     return result.toString();
/*      */   }
/*      */ 
/*      */   private String functionFinePrint(FunctionDeclaration fun) {
/* 1574 */     StringBuilder result = new StringBuilder("");
/* 1575 */     HashMap fnNameList = new HashMap();
/* 1576 */     List instrumentList = DeclarationHelpers.getTypeParams(fun, "string");
/* 1577 */     List colorList = DeclarationHelpers.getColorParamList(fun);
/* 1578 */     List numericList = DeclarationHelpers.getNumericParamList(fun);
/*      */ 
/* 1580 */     int[] instrumentIndexes = null;
/* 1581 */     if (fun.isDefaultParameters()) {
/* 1582 */       if (instrumentList.size() > 0) {
/* 1583 */         int factorialIndex = (int)MathHelpers.factorial(instrumentList.size());
/* 1584 */         instrumentIndexes = new int[instrumentList.size()];
/* 1585 */         Arrays.fill(instrumentIndexes, -1);
/* 1586 */         for (int i = 0; i < factorialIndex; i++)
/*      */         {
/* 1588 */           while (i < instrumentList.size()) {
/* 1589 */             instrumentIndexes[i] = ((Integer)instrumentList.get(i)).intValue();
/* 1590 */             result.append(DeclarationHelpers.functionDefaultParameters(fun, instrumentIndexes, fnNameList));
/* 1591 */             instrumentIndexes[i] = -1;
/* 1592 */             i++;
/*      */           }
/* 1594 */           instrumentIndexes[0] = ((Integer)instrumentList.get(0)).intValue();
/* 1595 */           while (i < factorialIndex - 1) {
/* 1596 */             for (int j = 1; j < instrumentList.size() - 1; j++) {
/* 1597 */               instrumentIndexes[j] = ((Integer)instrumentList.get(j)).intValue();
/* 1598 */               result.append(DeclarationHelpers.functionDefaultParameters(fun, instrumentIndexes, fnNameList));
/* 1599 */               instrumentIndexes = ArrayHelpers.reverse(instrumentIndexes);
/* 1600 */               result.append(DeclarationHelpers.functionDefaultParameters(fun, instrumentIndexes, fnNameList));
/* 1601 */               instrumentIndexes = ArrayHelpers.reverse(instrumentIndexes);
/*      */             }
/* 1603 */             i++; i++;
/*      */           }
/* 1605 */           instrumentIndexes[(instrumentIndexes.length - 1)] = ((Integer)instrumentList.get(instrumentList.size() - 1)).intValue();
/* 1606 */           result.append(DeclarationHelpers.functionDefaultParameters(fun, instrumentIndexes, fnNameList));
/*      */         }
/*      */ 
/* 1612 */         int[] emptyInstrumentIndexes = new int[instrumentList.size()];
/* 1613 */         Arrays.fill(emptyInstrumentIndexes, -1);
/* 1614 */         result.append(DeclarationHelpers.functionDefaultParameters(fun, emptyInstrumentIndexes, fnNameList));
/*      */       } else {
/* 1616 */         result.append(DeclarationHelpers.functionDefaultParameters(fun, -1));
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 1622 */     if ((fun.getParams() != null) && (fun.getParams().size() > 0)) {
/* 1623 */       String functionOriginalClearDefParam = DeclarationHelpers.functionOriginalClearDefParam(fun, instrumentList, colorList);
/* 1624 */       if (!functionOriginalClearDefParam.isEmpty()) {
/* 1625 */         result.append(functionOriginalClearDefParam);
/* 1626 */         result.append(" throws JFException {");
/* 1627 */         for (int i = 0; i < instrumentList.size(); i++) {
/* 1628 */           VariableDeclaration instrumentParam = (VariableDeclaration)fun.getParams().get(((Integer)instrumentList.get(i)).intValue());
/* 1629 */           result.append("\r\n if(");
/* 1630 */           result.append(instrumentParam.getName());
/* 1631 */           result.append("==null) {\r\n");
/* 1632 */           result.append(instrumentParam.getName());
/* 1633 */           result.append("=Instrument();\r\n}\r\n");
/*      */         }
/*      */ 
/* 1642 */         if (!fun.getType().equals("void")) {
/* 1643 */           result.append("return ");
/*      */         }
/* 1645 */         result.append(DeclarationHelpers.functionCallDefParam(fun, -1, instrumentList, colorList));
/* 1646 */         result.append(";}\r\n");
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 1652 */     if ((fun.getParams() != null) && (fun.getParams().size() > 0) && (numericList.size() > 0)) {
/* 1653 */       int[][] paramVariants = MathHelpers.elementArray(numericList.size());
/* 1654 */       int[] numericIndexes = new int[fun.getParams().size()];
/* 1655 */       for (int j = 0; j < paramVariants.length; j++) {
/* 1656 */         Arrays.fill(numericIndexes, -1);
/* 1657 */         int[] paramVariant = paramVariants[j];
/* 1658 */         for (int k = 0; k < paramVariant.length; k++) {
/* 1659 */           numericIndexes[((Integer)numericList.get(k)).intValue()] = paramVariant[k];
/*      */         }
/* 1661 */         String fnPrefix = DeclarationHelpers.functionOriginalClearDefParam(fun, fnNameList, instrumentList, colorList, numericIndexes, true);
/* 1662 */         if ((fnPrefix != null) && (!fnPrefix.isEmpty())) {
/* 1663 */           result.append(fnPrefix);
/* 1664 */           result.append(" throws JFException {\r\n");
/* 1665 */           if (!fun.getType().equals("void")) {
/* 1666 */             result.append("return ");
/*      */           }
/* 1668 */           result.append(DeclarationHelpers.functionCallDefParam(fun, -1, instrumentList, colorList, numericIndexes));
/* 1669 */           result.append(";}\r\n");
/*      */         }
/*      */       }
/*      */     }
/* 1673 */     result.append(DeclarationHelpers.functionOriginalClearDefParam(fun));
/* 1674 */     if (fun.isHasBody()) {
/* 1675 */       result.append(" throws JFException {");
/* 1676 */       result.append(printChildren(fun));
/*      */     } else {
/* 1678 */       result.append(";");
/*      */     }
/* 1680 */     return result.toString();
/*      */   }
/*      */ 
/*      */   public String printProperties() {
/* 1684 */     StringBuilder result = new StringBuilder("");
/* 1685 */     if ((this.properties != null) && (this.properties.size() > 0)) {
/* 1686 */       result.append("protected void initProperties() {\r\n");
/* 1687 */       result.append("if (properties == null) {\r\n");
/* 1688 */       result.append("properties = new Properties();");
/* 1689 */       result.append("\r\n}\r\n");
/*      */ 
/* 1691 */       Iterator it = this.properties.keySet().iterator();
/* 1692 */       while (it.hasNext()) {
/* 1693 */         String key = (String)it.next();
/* 1694 */         String value = (String)this.properties.get(key);
/* 1695 */         result.append(new StringBuilder().append("properties.setProperty(\"").append(key).append("\",\"").append(value).append("\");\r\n").toString());
/*      */       }
/*      */ 
/* 1698 */       result.append("\r\n}\r\n");
/* 1699 */       it = this.properties.keySet().iterator();
/* 1700 */       while (it.hasNext()) {
/* 1701 */         String key = (String)it.next();
/* 1702 */         String value = (String)this.properties.get(key);
/* 1703 */         if (key.startsWith("indicator_width")) {
/* 1704 */           result.append("protected int ");
/* 1705 */           result.append(key);
/* 1706 */           result.append(" = ");
/* 1707 */           result.append(value);
/* 1708 */           result.append(";\r\n");
/*      */         }
/* 1710 */         if (key.startsWith("indicator_color")) {
/* 1711 */           result.append("protected Color ");
/* 1712 */           result.append(key);
/* 1713 */           result.append(" = ");
/* 1714 */           result.append(value);
/* 1715 */           result.append(";\r\n");
/*      */         }
/*      */       }
/*      */     }
/*      */ 
/* 1720 */     return result.toString();
/*      */   }
/*      */ 
/*      */   public String printImports() {
/* 1724 */     StringBuilder result = new StringBuilder("");
/* 1725 */     if ((this.imports != null) && (this.imports.size() > 0)) {
/* 1726 */       Iterator it = this.imports.keySet().iterator();
/* 1727 */       while (it.hasNext()) {
/* 1728 */         String key = (String)it.next();
/* 1729 */         ImportDeclaration value = (ImportDeclaration)this.imports.get(key);
/* 1730 */         result.append(new StringBuilder().append(value.getInterfaceName()).append(" ").append(value.getInterfaceName()).append(" = NLink.create(").append(value.getInterfaceName()).append(".class);\n").toString());
/* 1731 */         result.append("@DllClass\n");
/*      */ 
/* 1733 */         result.append("public interface ");
/* 1734 */         result.append(value.getInterfaceName());
/* 1735 */         result.append(" {\r\n");
/* 1736 */         result.append(value.startText());
/* 1737 */         result.append("\r\n}\r\n");
/* 1738 */         result.append(value.endText());
/*      */       }
/*      */     }
/* 1741 */     return result.toString();
/*      */   }
/*      */ 
/*      */   public String printClasses(ExternalEngine engine) {
/* 1745 */     StringBuilder result = new StringBuilder("");
/* 1746 */     for (ClassDeclaration classDeclaration : this.classes.values()) {
/* 1747 */       result.append(classDeclaration.startText());
/*      */ 
/* 1749 */       result.append(printVariables(engine, classDeclaration.variables.values()));
/*      */ 
/* 1751 */       if (classDeclaration.hasChildren()) {
/* 1752 */         for (IDeclaration child : classDeclaration.getChildren()) {
/* 1753 */           result.append(printChildren(child));
/*      */         }
/*      */       }
/* 1756 */       result.append(classDeclaration.endText());
/*      */     }
/* 1758 */     return result.toString();
/*      */   }
/*      */ 
/*      */   public String printVariables(ExternalEngine engine, Collection variables) {
/* 1762 */     StringBuilder result = new StringBuilder("");
/* 1763 */     for (int i = 0; i < variables.size(); i++) {
/* 1764 */       VariableDeclaration var = (VariableDeclaration)variables.toArray()[i];
/* 1765 */       if (!var.isCounter()) {
/* 1766 */         result.append(var.startText());
/* 1767 */         result.append(var.endText());
/*      */       }
/*      */     }
/* 1770 */     return result.toString();
/*      */   }
/*      */ 
/*      */   public String printFunctions(ExternalEngine engine, Collection functions) {
/* 1774 */     StringBuilder result = new StringBuilder("");
/* 1775 */     for (int i = 0; i < functions.size(); i++) {
/* 1776 */       FunctionDeclaration fun = (FunctionDeclaration)functions.toArray()[i];
/* 1777 */       result.append("\r\n");
/* 1778 */       result.append(functionFinePrint(fun));
/*      */     }
/* 1780 */     return result.toString();
/*      */   }
/*      */ 
/*      */   public String print(ExternalEngine engine) {
/* 1784 */     StringBuilder result = new StringBuilder("");
/*      */ 
/* 1786 */     result.append(printImports());
/* 1787 */     result.append(printProperties());
/*      */ 
/* 1789 */     addFunction("start");
/* 1790 */     result.append(printVariables(engine, this.variables.values()));
/* 1791 */     result.append(printFunctions(engine, this.functions.values()));
/* 1792 */     result.append(printClasses(engine));
/* 1793 */     return result.toString();
/*      */   }
/*      */ 
/*      */   private void addFunction(String name) {
/* 1797 */     FunctionDeclaration existingFunction = (FunctionDeclaration)this.functions.get(name);
/* 1798 */     if ((existingFunction != null) && (existingFunction.getParams() != null) && (existingFunction.getParams().size() > 0)) {
/* 1799 */       FunctionDeclaration fun = new FunctionDeclaration();
/* 1800 */       ArrayList params = new ArrayList();
/* 1801 */       fun.setParams(params);
/* 1802 */       fun.setName(name);
/* 1803 */       fun.setType("int");
/* 1804 */       fun.setVisibility("protected");
/* 1805 */       fun.setHasBody(true);
/* 1806 */       fun.setHasReturn(false);
/* 1807 */       this.functions.put(name, fun);
/*      */     }
/*      */   }
/*      */ 
/*      */   private Declaration getCommonParent(Declaration statement, Declaration initChild) {
/* 1812 */     Declaration result = null;
/* 1813 */     Declaration initChildRuntime = initChild;
/* 1814 */     Declaration statementRuntime = statement;
/* 1815 */     while ((initChildRuntime != null) && (statementRuntime != null)) {
/* 1816 */       if (statementRuntime.getParent() == initChildRuntime) {
/* 1817 */         result = initChildRuntime;
/* 1818 */         break;
/*      */       }
/* 1820 */       if (statementRuntime.getParent() != null) {
/* 1821 */         statementRuntime = statementRuntime.getParent(); continue;
/*      */       }
/* 1823 */       initChildRuntime = initChildRuntime.getParent();
/* 1824 */       statementRuntime = statement;
/*      */     }
/*      */ 
/* 1828 */     return result;
/*      */   }
/*      */ 
/*      */   private List<VariableDeclaration> fillVariableList(List<String> names) {
/* 1832 */     FunctionDeclaration function = (FunctionDeclaration)this.functions.get(getCurrentFunctionName());
/* 1833 */     List list = new ArrayList();
/* 1834 */     for (String name : names) {
/* 1835 */       if ((!this.lastParent.getVariables().containsKey(name)) && (!this.variables.containsKey(name)) && (!function.containVariable(name)))
/*      */       {
/* 1838 */         list.add(findVariable(function, name));
/*      */       }
/*      */     }
/* 1841 */     return list;
/*      */   }
/*      */ 
/*      */   private VariableDeclaration findVariable(Declaration root, String name) {
/* 1845 */     VariableDeclaration result = null;
/* 1846 */     if (result == null) {
/* 1847 */       if (!root.variables.containsKey(name)) {
/* 1848 */         for (IDeclaration decl : root.getChildren()) {
/* 1849 */           result = findVariable((Declaration)decl, name);
/* 1850 */           if (result != null) {
/*      */             break;
/*      */           }
/*      */         }
/*      */       }
/* 1855 */       else if (result == null) {
/* 1856 */         result = (VariableDeclaration)root.getVariables().get(name);
/*      */       }
/*      */     }
/*      */ 
/* 1860 */     return result;
/*      */   }
/*      */ 
/*      */   private List<String> getVariableNamesInExpression(ASTNode expr, List<String> list) {
/* 1864 */     if (list == null) {
/* 1865 */       list = new ArrayList();
/*      */     }
/* 1867 */     if (expr.getChildren() != null) {
/* 1868 */       for (int i = 0; i < expr.getChildren().length; i++) {
/* 1869 */         ASTNode node = expr.getChildren()[i];
/* 1870 */         if (node.getId() == 168) {
/* 1871 */           if (i + 1 < expr.getChildren().length) {
/* 1872 */             i++; ASTNode next = expr.getChildren()[i];
/* 1873 */             if (next.getId() != 62)
/* 1874 */               list.add(node.getText());
/*      */           }
/*      */           else {
/* 1877 */             list.add(node.getText());
/*      */           }
/*      */         }
/* 1880 */         if (node.getChildren() != null) {
/* 1881 */           getVariableNamesInExpression(node, list);
/*      */         }
/*      */       }
/*      */     }
/* 1885 */     return list;
/*      */   }
/*      */ 
/*      */   private VariableDeclaration initVariableArrayInitValue(ASTNode initDeclaration, VariableDeclaration currentVariable) {
/* 1889 */     int dimentionCount = 0;
/* 1890 */     if ((currentVariable.isArray()) && (initDeclaration.getChildren() != null) && (initDeclaration.getChildren().length > 0)) {
/* 1891 */       int currentArrayPos = 0;
/* 1892 */       ASTNode prevNode = null;
/* 1893 */       boolean dimentionCountCalculationDone = false;
/* 1894 */       boolean lastDimensionElementCountDone = false;
/* 1895 */       boolean canNDimentionSizeCalculating = true;
/* 1896 */       int index = 0;
/*      */ 
/* 1898 */       if ((currentVariable.getArrayDimentions() > 0) && 
/* 1899 */         (currentVariable.getArrayDimentionAsInt(currentVariable.getArrayDimentions() - 1) > 0)) {
/* 1900 */         lastDimensionElementCountDone = true;
/*      */       }
/*      */ 
/* 1903 */       for (int j = 0; j < initDeclaration.getChildren().length; j++) {
/* 1904 */         ASTNode node = initDeclaration.getChildren()[j];
/*      */ 
/* 1906 */         if (node.getId() == 134) {
/* 1907 */           currentVariable.setHasNew(true);
/* 1908 */           break;
/*      */         }
/*      */ 
/* 1911 */         if (node.getId() == 62) {
/* 1912 */           if (((prevNode.getId() == 74) || (prevNode.getId() == 62)) && (!dimentionCountCalculationDone)) {
/* 1913 */             dimentionCount++;
/* 1914 */           } else if (prevNode.getId() == 71) {
/* 1915 */             int i = 1;
/* 1916 */             while (initDeclaration.getChildren()[(index + i)].getId() == 62) {
/* 1917 */               i++;
/*      */             }
/* 1919 */             i++;
/* 1920 */             int dimentionValue = currentVariable.getArrayDimentionAsInt(dimentionCount - i);
/* 1921 */             dimentionValue++;
/* 1922 */             currentVariable.setArrayDimention(dimentionCount - i, new String(new StringBuilder().append(dimentionValue).append("").toString()));
/*      */           }
/*      */         }
/*      */ 
/* 1926 */         if ((node.getId() == 71) || (node.getId() == 63)) {
/* 1927 */           dimentionCountCalculationDone = true;
/* 1928 */           if ((prevNode.getId() != 62) && (prevNode.getId() != 71) && (!lastDimensionElementCountDone)) {
/* 1929 */             int dimentionValue = currentVariable.getArrayDimentionAsInt(dimentionCount - 1);
/* 1930 */             dimentionValue++;
/* 1931 */             currentVariable.setArrayDimention(dimentionCount - 1, new String(new StringBuilder().append(dimentionValue).append("").toString()));
/* 1932 */             if (node.getId() == 63) {
/* 1933 */               lastDimensionElementCountDone = true;
/*      */             }
/*      */           }
/*      */ 
/*      */         }
/*      */ 
/* 1939 */         if ((node.getId() == 50) || (node.getId() == 49)) {
/* 1940 */           dimentionCountCalculationDone = true;
/* 1941 */           currentVariable.setArrayValue(currentArrayPos, DeclarationHelpers.getAssignmentExpression(node));
/* 1942 */           currentArrayPos++;
/*      */         }
/* 1944 */         prevNode = node;
/* 1945 */         index++;
/*      */       }
/* 1947 */       currentVariable.setEmptyArray(false);
/*      */     }
/*      */ 
/* 1950 */     return currentVariable;
/*      */   }
/*      */ 
/*      */   private VariableDeclaration initVariableDeclaration(ASTNode initDeclaration, Token firstToken, Token nameToken) throws JFException {
/* 1954 */     VariableDeclaration variable = new VariableDeclaration();
/* 1955 */     String prefix = "";
/* 1956 */     String type = "";
/* 1957 */     boolean isAssignedValue = false;
/* 1958 */     if (nameToken != firstToken) {
/* 1959 */       type = firstToken.image;
/*      */     }
/* 1961 */     variable.setFirstToken(firstToken);
/* 1962 */     if (firstToken.next != nameToken) {
/* 1963 */       Token t = firstToken;
/* 1964 */       while ((t != null) && (t.next != nameToken)) {
/* 1965 */         prefix = new StringBuilder().append(prefix).append(t.image).toString();
/* 1966 */         t = t.next;
/*      */       }
/* 1968 */       if (t != null) {
/* 1969 */         type = t.image;
/*      */       }
/*      */     }
/*      */ 
/* 1973 */     variable.setPrefix(prefix);
/*      */ 
/* 1975 */     variable.setType(type);
/* 1976 */     variable.setName(initDeclaration.getText());
/* 1977 */     for (ASTNode child : initDeclaration.getChildren()) {
/* 1978 */       if (child.id == 29) {
/* 1979 */         int dimentionCount = 0;
/* 1980 */         for (ASTNode declarationChild : child.getChildren()) {
/* 1981 */           if (declarationChild.id == 64) {
/* 1982 */             variable.setArray(true);
/* 1983 */             dimentionCount++;
/*      */           }
/* 1985 */           if ((declarationChild.id == 65) && 
/* 1986 */             (dimentionCount > 0) && (variable.getArrayDimentions() < dimentionCount)) {
/* 1987 */             variable.addArrayDimention("");
/*      */           }
/*      */ 
/* 1990 */           if (declarationChild.id == 51) {
/* 1991 */             variable.addArrayDimention(DeclarationHelpers.getAssignmentExpression(declarationChild));
/*      */           }
/*      */         }
/*      */       }
/*      */ 
/* 1996 */       if ((child.id == 50) || (child.id == 49)) {
/* 1997 */         variable.setExpressionNode(child);
/* 1998 */         variable.setValue(DeclarationHelpers.getAssignmentExpression(child));
/*      */       }
/* 2000 */       if (child.id == 187) {
/* 2001 */         variable.setExpressionNode(child);
/* 2002 */         variable.setValue(DeclarationHelpers.getAssignmentExpression(child));
/*      */       }
/* 2004 */       if (child.id == 186) {
/* 2005 */         variable.setExpressionNode(child);
/* 2006 */         variable.setValue(DeclarationHelpers.getDatetimeInitionalizerExpression(child));
/*      */       }
/*      */ 
/* 2009 */       if (child.getId() == 74) {
/* 2010 */         isAssignedValue = true;
/*      */       }
/*      */     }
/* 2013 */     if ((variable.isArray()) && (isAssignedValue)) {
/* 2014 */       variable = initVariableArrayInitValue(initDeclaration, variable);
/*      */     }
/* 2016 */     return variable;
/*      */   }
/*      */ 
/*      */   private VariableDeclaration initVariableDeclaration(Token nameToken, Token firstToken, Token lastToken, boolean isCounter) {
/* 2020 */     VariableDeclaration variable = new VariableDeclaration();
/* 2021 */     String prefix = "";
/* 2022 */     String type = "";
/*      */ 
/* 2028 */     if (nameToken != firstToken) {
/* 2029 */       type = firstToken.image;
/*      */     }
/* 2031 */     variable.setFirstToken(firstToken);
/* 2032 */     variable.setLastToken(lastToken);
/* 2033 */     if (firstToken.next != nameToken) {
/* 2034 */       Token t = firstToken;
/* 2035 */       while ((t != null) && (t.next != nameToken)) {
/* 2036 */         prefix = new StringBuilder().append(prefix).append(t.image).toString();
/* 2037 */         t = t.next;
/*      */       }
/* 2039 */       if (t != null) {
/* 2040 */         type = t.image;
/*      */       }
/*      */     }
/*      */ 
/* 2044 */     variable.setPrefix(prefix);
/* 2045 */     variable.setType(type);
/* 2046 */     if ((isCounter) && (!variable.getType().equals("int"))) {
/* 2047 */       variable.setType("int");
/*      */     }
/* 2049 */     variable.setName(nameToken.image);
/*      */ 
/* 2051 */     if ((nameToken.next != null) && (nameToken.next.kind == 38)) {
/* 2052 */       Token t = nameToken.next.next;
/* 2053 */       variable.setArray(true);
/*      */ 
/* 2055 */       if ((t == lastToken) && (t.kind == 39)) {
/* 2056 */         variable.addArrayDimention(new String(""));
/*      */       }
/*      */ 
/* 2061 */       while ((t != null) && (t != lastToken) && (t.kind != 48) && (t.kind != 44) && (t.kind != 36)) {
/* 2062 */         if (t.kind != 39) {
/* 2063 */           variable.addArrayDimention(new String(t.image));
/* 2064 */           t = t.next;
/*      */         } else {
/* 2066 */           variable.addArrayDimention(new String(""));
/*      */         }
/* 2068 */         if (t.kind == 39) {
/* 2069 */           t = t.next;
/* 2070 */           if (t != lastToken) {
/* 2071 */             t = t.next; continue;
/*      */           }
/*      */         }
/* 2074 */         t = t.next;
/*      */       }
/*      */ 
/* 2077 */       if ((t != null) && (t.kind == 48)) {
/* 2078 */         t = t.next;
/*      */       }
/*      */ 
/* 2082 */       if ((t != null) && (t.kind == 36)) {
/* 2083 */         while ((t != null) && (t.kind != 44)) {
/* 2084 */           t = t.next;
/*      */         }
/*      */       }
/*      */ 
/*      */     }
/* 2089 */     else if (nameToken != lastToken) {
/* 2090 */       Token t = nameToken.next;
/* 2091 */       StringBuilder expression = new StringBuilder("");
/* 2092 */       if (t.kind == 48) {
/* 2093 */         t = t.next;
/*      */       }
/*      */ 
/* 2096 */       if ((t == null) || (t.kind != 164) || (t.next.kind != 40))
/*      */       {
/* 2099 */         if ((t != null) && (t.kind != 44) && (t.kind != 45))
/*      */         {
/*      */           do {
/* 2102 */             if (t.kind == 38) {
/* 2103 */               expression.append(t.image);
/* 2104 */               expression.append("toInt(");
/*      */             } else {
/* 2106 */               expression.append(t.image);
/*      */             }
/* 2108 */             t = t.next;
/*      */           }
/*      */ 
/* 2111 */           while ((t != null) && (t != lastToken) && (t.kind != 44) && (t.kind != 45));
/* 2112 */           if ((t.kind == 39) || (t.kind > 164))
/*      */           {
/* 2114 */             expression.append(")");
/* 2115 */             expression.append(t.image);
/*      */           }
/* 2117 */           if (t.kind >= 145)
/* 2118 */             expression.append(t.image);
/*      */         }
/*      */       }
/* 2121 */       variable.setValue(expression.toString());
/*      */     }
/*      */ 
/* 2127 */     return variable;
/*      */   }
/*      */ 
/*      */   private void getExpressionHighestLevelParent(List<VariableDeclaration> variables, Declaration parent)
/*      */   {
/* 2133 */     for (VariableDeclaration var : variables) {
/* 2134 */       Declaration commonParent = getCommonParent(var, parent);
/* 2135 */       if (commonParent != null) {
/* 2136 */         Declaration variableParent = var.getParent();
/* 2137 */         variableParent.variables.remove(var.getName());
/* 2138 */         var.setChangeParent(true);
/* 2139 */         var.setParent(commonParent);
/* 2140 */         commonParent.variables.put(var.getName(), var);
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   private void setDeclarationParent(Declaration decl) {
/* 2146 */     decl.setParent(this.lastParent);
/* 2147 */     this.prevParent = this.lastParent;
/* 2148 */     this.lastParent = decl;
/* 2149 */     this.lastParent.setParentLevel(this.prevParent.getParentLevel() + 1);
/* 2150 */     this.prevParent.children.add(decl);
/*      */   }
/*      */ 
/*      */   public Declaration getStatementDeclaration() {
/* 2154 */     return this.lastParent;
/*      */   }
/*      */ 
/*      */   public int getIndicatorBuffers() {
/* 2158 */     return this.indicatorBuffers;
/*      */   }
/*      */ 
/*      */   public void setIndicatorBuffers(int indicatorBuffers) {
/* 2162 */     this.indicatorBuffers = indicatorBuffers;
/*      */   }
/*      */ 
/*      */   public synchronized String getCurrentFunctionName() {
/* 2166 */     return this.currentFunctionName;
/*      */   }
/*      */ 
/*      */   public synchronized void setCurrentFunctionName(String currentFunctionName) {
/* 2170 */     this.currentFunctionName = currentFunctionName;
/*      */   }
/*      */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Connector-1.1.49.jar
 * Qualified Name:     com.dukascopy.dds2.greed.connector.parser.javacc.ParserCallback
 * JD-Core Version:    0.6.0
 */