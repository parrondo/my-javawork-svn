/*     */ package org.eclipse.jdt.internal.compiler.parser;
/*     */ 
/*     */ import java.util.HashSet;
/*     */ import java.util.Set;
/*     */ import org.eclipse.jdt.core.compiler.CharOperation;
/*     */ import org.eclipse.jdt.internal.compiler.ast.ASTNode;
/*     */ import org.eclipse.jdt.internal.compiler.ast.AbstractMethodDeclaration;
/*     */ import org.eclipse.jdt.internal.compiler.ast.Argument;
/*     */ import org.eclipse.jdt.internal.compiler.ast.Block;
/*     */ import org.eclipse.jdt.internal.compiler.ast.FieldDeclaration;
/*     */ import org.eclipse.jdt.internal.compiler.ast.LocalDeclaration;
/*     */ import org.eclipse.jdt.internal.compiler.ast.Statement;
/*     */ import org.eclipse.jdt.internal.compiler.ast.TypeDeclaration;
/*     */ import org.eclipse.jdt.internal.compiler.ast.TypeReference;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.BaseTypeBinding;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;
/*     */ 
/*     */ public class RecoveredBlock extends RecoveredStatement
/*     */   implements TerminalTokens
/*     */ {
/*     */   public Block blockDeclaration;
/*     */   public RecoveredStatement[] statements;
/*     */   public int statementCount;
/*  33 */   public boolean preserveContent = false;
/*     */   public RecoveredLocalVariable pendingArgument;
/*     */   int pendingModifiers;
/*  37 */   int pendingModifersSourceStart = -1;
/*     */   RecoveredAnnotation[] pendingAnnotations;
/*     */   int pendingAnnotationCount;
/*     */ 
/*     */   public RecoveredBlock(Block block, RecoveredElement parent, int bracketBalance)
/*     */   {
/*  42 */     super(block, parent, bracketBalance);
/*  43 */     this.blockDeclaration = block;
/*  44 */     this.foundOpeningBrace = true;
/*     */ 
/*  46 */     this.preserveContent = ((parser().methodRecoveryActivated) || (parser().statementRecoveryActivated));
/*     */   }
/*     */   public RecoveredElement add(AbstractMethodDeclaration methodDeclaration, int bracketBalanceValue) {
/*  49 */     if ((this.parent != null) && ((this.parent instanceof RecoveredMethod))) {
/*  50 */       RecoveredMethod enclosingRecoveredMethod = (RecoveredMethod)this.parent;
/*  51 */       if ((enclosingRecoveredMethod.methodBody == this) && (enclosingRecoveredMethod.parent == null)) {
/*  52 */         resetPendingModifiers();
/*     */ 
/*  54 */         return this;
/*     */       }
/*     */     }
/*  57 */     return super.add(methodDeclaration, bracketBalanceValue);
/*     */   }
/*     */ 
/*     */   public RecoveredElement add(Block nestedBlockDeclaration, int bracketBalanceValue)
/*     */   {
/*  63 */     resetPendingModifiers();
/*     */ 
/*  67 */     if ((this.blockDeclaration.sourceEnd != 0) && 
/*  68 */       (nestedBlockDeclaration.sourceStart > this.blockDeclaration.sourceEnd)) {
/*  69 */       return this.parent.add(nestedBlockDeclaration, bracketBalanceValue);
/*     */     }
/*     */ 
/*  72 */     RecoveredBlock element = new RecoveredBlock(nestedBlockDeclaration, this, bracketBalanceValue);
/*     */ 
/*  75 */     if (this.pendingArgument != null) {
/*  76 */       element.attach(this.pendingArgument);
/*  77 */       this.pendingArgument = null;
/*     */     }
/*  79 */     if (parser().statementRecoveryActivated) {
/*  80 */       addBlockStatement(element);
/*     */     }
/*  82 */     attach(element);
/*  83 */     if (nestedBlockDeclaration.sourceEnd == 0) return element;
/*  84 */     return this;
/*     */   }
/*     */ 
/*     */   public RecoveredElement add(LocalDeclaration localDeclaration, int bracketBalanceValue)
/*     */   {
/*  90 */     return add(localDeclaration, bracketBalanceValue, false);
/*     */   }
/*     */ 
/*     */   public RecoveredElement add(LocalDeclaration localDeclaration, int bracketBalanceValue, boolean delegatedByParent)
/*     */   {
/* 115 */     if ((this.blockDeclaration.sourceEnd != 0) && 
/* 116 */       (localDeclaration.declarationSourceStart > this.blockDeclaration.sourceEnd)) {
/* 117 */       resetPendingModifiers();
/* 118 */       if (delegatedByParent) return this;
/* 119 */       return this.parent.add(localDeclaration, bracketBalanceValue);
/*     */     }
/*     */ 
/* 122 */     RecoveredLocalVariable element = new RecoveredLocalVariable(localDeclaration, this, bracketBalanceValue);
/*     */ 
/* 124 */     if (this.pendingAnnotationCount > 0) {
/* 125 */       element.attach(
/* 126 */         this.pendingAnnotations, 
/* 127 */         this.pendingAnnotationCount, 
/* 128 */         this.pendingModifiers, 
/* 129 */         this.pendingModifersSourceStart);
/*     */     }
/* 131 */     resetPendingModifiers();
/*     */ 
/* 133 */     if ((localDeclaration instanceof Argument)) {
/* 134 */       this.pendingArgument = element;
/* 135 */       return this;
/*     */     }
/*     */ 
/* 138 */     attach(element);
/* 139 */     if (localDeclaration.declarationSourceEnd == 0) return element;
/* 140 */     return this;
/*     */   }
/*     */ 
/*     */   public RecoveredElement add(Statement stmt, int bracketBalanceValue)
/*     */   {
/* 146 */     return add(stmt, bracketBalanceValue, false);
/*     */   }
/*     */ 
/*     */   public RecoveredElement add(Statement stmt, int bracketBalanceValue, boolean delegatedByParent)
/*     */   {
/* 153 */     resetPendingModifiers();
/*     */ 
/* 157 */     if ((this.blockDeclaration.sourceEnd != 0) && 
/* 158 */       (stmt.sourceStart > this.blockDeclaration.sourceEnd)) {
/* 159 */       if (delegatedByParent) return this;
/* 160 */       return this.parent.add(stmt, bracketBalanceValue);
/*     */     }
/*     */ 
/* 163 */     RecoveredStatement element = new RecoveredStatement(stmt, this, bracketBalanceValue);
/* 164 */     attach(element);
/* 165 */     if (stmt.sourceEnd == 0) return element;
/* 166 */     return this;
/*     */   }
/*     */ 
/*     */   public RecoveredElement add(TypeDeclaration typeDeclaration, int bracketBalanceValue)
/*     */   {
/* 172 */     return add(typeDeclaration, bracketBalanceValue, false);
/*     */   }
/*     */ 
/*     */   public RecoveredElement add(TypeDeclaration typeDeclaration, int bracketBalanceValue, boolean delegatedByParent)
/*     */   {
/* 181 */     if ((this.blockDeclaration.sourceEnd != 0) && 
/* 182 */       (typeDeclaration.declarationSourceStart > this.blockDeclaration.sourceEnd)) {
/* 183 */       resetPendingModifiers();
/* 184 */       if (delegatedByParent) return this;
/* 185 */       return this.parent.add(typeDeclaration, bracketBalanceValue);
/*     */     }
/*     */ 
/* 188 */     RecoveredType element = new RecoveredType(typeDeclaration, this, bracketBalanceValue);
/* 189 */     if (this.pendingAnnotationCount > 0) {
/* 190 */       element.attach(
/* 191 */         this.pendingAnnotations, 
/* 192 */         this.pendingAnnotationCount, 
/* 193 */         this.pendingModifiers, 
/* 194 */         this.pendingModifersSourceStart);
/*     */     }
/* 196 */     resetPendingModifiers();
/* 197 */     attach(element);
/* 198 */     if (typeDeclaration.declarationSourceEnd == 0) return element;
/* 199 */     return this;
/*     */   }
/*     */   public RecoveredElement addAnnotationName(int identifierPtr, int identifierLengthPtr, int annotationStart, int bracketBalanceValue) {
/* 202 */     if (this.pendingAnnotations == null) {
/* 203 */       this.pendingAnnotations = new RecoveredAnnotation[5];
/* 204 */       this.pendingAnnotationCount = 0;
/*     */     }
/* 206 */     else if (this.pendingAnnotationCount == this.pendingAnnotations.length) {
/* 207 */       System.arraycopy(
/* 208 */         this.pendingAnnotations, 
/* 209 */         0, 
/* 210 */         this.pendingAnnotations = new RecoveredAnnotation[2 * this.pendingAnnotationCount], 
/* 211 */         0, 
/* 212 */         this.pendingAnnotationCount);
/*     */     }
/*     */ 
/* 216 */     RecoveredAnnotation element = new RecoveredAnnotation(identifierPtr, identifierLengthPtr, annotationStart, this, bracketBalanceValue);
/*     */ 
/* 218 */     this.pendingAnnotations[(this.pendingAnnotationCount++)] = element;
/*     */ 
/* 220 */     return element;
/*     */   }
/*     */   public void addModifier(int flag, int modifiersSourceStart) {
/* 223 */     this.pendingModifiers |= flag;
/*     */ 
/* 225 */     if (this.pendingModifersSourceStart < 0)
/* 226 */       this.pendingModifersSourceStart = modifiersSourceStart;
/*     */   }
/*     */ 
/*     */   void attach(RecoveredStatement recoveredStatement)
/*     */   {
/* 234 */     if (this.statements == null) {
/* 235 */       this.statements = new RecoveredStatement[5];
/* 236 */       this.statementCount = 0;
/*     */     }
/* 238 */     else if (this.statementCount == this.statements.length) {
/* 239 */       System.arraycopy(
/* 240 */         this.statements, 
/* 241 */         0, 
/* 242 */         this.statements = new RecoveredStatement[2 * this.statementCount], 
/* 243 */         0, 
/* 244 */         this.statementCount);
/*     */     }
/*     */ 
/* 247 */     this.statements[(this.statementCount++)] = recoveredStatement;
/*     */   }
/*     */   void attachPendingModifiers(RecoveredAnnotation[] pendingAnnots, int pendingAnnotCount, int pendingMods, int pendingModsSourceStart) {
/* 250 */     this.pendingAnnotations = pendingAnnots;
/* 251 */     this.pendingAnnotationCount = pendingAnnotCount;
/* 252 */     this.pendingModifiers = pendingMods;
/* 253 */     this.pendingModifersSourceStart = pendingModsSourceStart;
/*     */   }
/*     */ 
/*     */   public ASTNode parseTree()
/*     */   {
/* 259 */     return this.blockDeclaration;
/*     */   }
/*     */   public void resetPendingModifiers() {
/* 262 */     this.pendingAnnotations = null;
/* 263 */     this.pendingAnnotationCount = 0;
/* 264 */     this.pendingModifiers = 0;
/* 265 */     this.pendingModifersSourceStart = -1;
/*     */   }
/*     */   public String toString(int tab) {
/* 268 */     StringBuffer result = new StringBuffer(tabString(tab));
/* 269 */     result.append("Recovered block:\n");
/* 270 */     this.blockDeclaration.print(tab + 1, result);
/* 271 */     if (this.statements != null) {
/* 272 */       for (int i = 0; i < this.statementCount; i++) {
/* 273 */         result.append("\n");
/* 274 */         result.append(this.statements[i].toString(tab + 1));
/*     */       }
/*     */     }
/* 277 */     return result.toString();
/*     */   }
/*     */ 
/*     */   public Block updatedBlock(int depth, Set knownTypes)
/*     */   {
/* 285 */     if ((!this.preserveContent) || (this.statementCount == 0)) return null;
/*     */ 
/* 287 */     Statement[] updatedStatements = new Statement[this.statementCount];
/* 288 */     int updatedCount = 0;
/*     */ 
/* 292 */     RecoveredStatement lastStatement = this.statements[(this.statementCount - 1)];
/* 293 */     RecoveredMethod enclosingMethod = enclosingMethod();
/* 294 */     RecoveredInitializer enclosingIntializer = enclosingInitializer();
/* 295 */     int bodyEndValue = 0;
/* 296 */     if (enclosingMethod != null) {
/* 297 */       bodyEndValue = enclosingMethod.methodDeclaration.bodyEnd;
/* 298 */       if ((enclosingIntializer != null) && (enclosingMethod.methodDeclaration.sourceStart < enclosingIntializer.fieldDeclaration.sourceStart))
/* 299 */         bodyEndValue = enclosingIntializer.fieldDeclaration.declarationSourceEnd;
/*     */     }
/* 301 */     else if (enclosingIntializer != null) {
/* 302 */       bodyEndValue = enclosingIntializer.fieldDeclaration.declarationSourceEnd;
/*     */     } else {
/* 304 */       bodyEndValue = this.blockDeclaration.sourceEnd - 1;
/*     */     }
/*     */ 
/* 307 */     if ((lastStatement instanceof RecoveredLocalVariable)) {
/* 308 */       RecoveredLocalVariable lastLocalVariable = (RecoveredLocalVariable)lastStatement;
/* 309 */       if (lastLocalVariable.localDeclaration.declarationSourceEnd == 0) {
/* 310 */         lastLocalVariable.localDeclaration.declarationSourceEnd = bodyEndValue;
/* 311 */         lastLocalVariable.localDeclaration.declarationEnd = bodyEndValue;
/*     */       }
/* 313 */     } else if ((lastStatement instanceof RecoveredBlock)) {
/* 314 */       RecoveredBlock lastBlock = (RecoveredBlock)lastStatement;
/* 315 */       if (lastBlock.blockDeclaration.sourceEnd == 0)
/* 316 */         lastBlock.blockDeclaration.sourceEnd = bodyEndValue;
/*     */     }
/* 318 */     else if ((!(lastStatement instanceof RecoveredType)) && 
/* 319 */       (lastStatement.statement.sourceEnd == 0)) {
/* 320 */       lastStatement.statement.sourceEnd = bodyEndValue;
/*     */     }
/*     */ 
/* 324 */     int lastEnd = this.blockDeclaration.sourceStart;
/*     */ 
/* 327 */     for (int i = 0; i < this.statementCount; i++) {
/* 328 */       Statement updatedStatement = this.statements[i].updatedStatement(depth, knownTypes);
/* 329 */       if (updatedStatement != null) {
/* 330 */         updatedStatements[(updatedCount++)] = updatedStatement;
/*     */ 
/* 332 */         if ((updatedStatement instanceof LocalDeclaration)) {
/* 333 */           LocalDeclaration localDeclaration = (LocalDeclaration)updatedStatement;
/* 334 */           if (localDeclaration.declarationSourceEnd > lastEnd)
/* 335 */             lastEnd = localDeclaration.declarationSourceEnd;
/*     */         }
/* 337 */         else if ((updatedStatement instanceof TypeDeclaration)) {
/* 338 */           TypeDeclaration typeDeclaration = (TypeDeclaration)updatedStatement;
/* 339 */           if (typeDeclaration.declarationSourceEnd > lastEnd) {
/* 340 */             lastEnd = typeDeclaration.declarationSourceEnd;
/*     */           }
/*     */         }
/* 343 */         else if (updatedStatement.sourceEnd > lastEnd) {
/* 344 */           lastEnd = updatedStatement.sourceEnd;
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/* 349 */     if (updatedCount == 0) return null;
/*     */ 
/* 352 */     if (updatedCount != this.statementCount) {
/* 353 */       this.blockDeclaration.statements = new Statement[updatedCount];
/* 354 */       System.arraycopy(updatedStatements, 0, this.blockDeclaration.statements, 0, updatedCount);
/*     */     } else {
/* 356 */       this.blockDeclaration.statements = updatedStatements;
/*     */     }
/*     */ 
/* 359 */     if (this.blockDeclaration.sourceEnd == 0) {
/* 360 */       if (lastEnd < bodyEndValue)
/* 361 */         this.blockDeclaration.sourceEnd = bodyEndValue;
/*     */       else {
/* 363 */         this.blockDeclaration.sourceEnd = lastEnd;
/*     */       }
/*     */     }
/*     */ 
/* 367 */     return this.blockDeclaration;
/*     */   }
/*     */ 
/*     */   public Statement updatedStatement(int depth, Set knownTypes)
/*     */   {
/* 374 */     return updatedBlock(depth, knownTypes);
/*     */   }
/*     */ 
/*     */   public RecoveredElement updateOnClosingBrace(int braceStart, int braceEnd)
/*     */   {
/* 381 */     if ((--this.bracketBalance <= 0) && (this.parent != null)) {
/* 382 */       updateSourceEndIfNecessary(braceStart, braceEnd);
/*     */ 
/* 385 */       RecoveredMethod method = enclosingMethod();
/* 386 */       if ((method != null) && (method.methodBody == this)) {
/* 387 */         return this.parent.updateOnClosingBrace(braceStart, braceEnd);
/*     */       }
/* 389 */       RecoveredInitializer initializer = enclosingInitializer();
/* 390 */       if ((initializer != null) && (initializer.initializerBody == this)) {
/* 391 */         return this.parent.updateOnClosingBrace(braceStart, braceEnd);
/*     */       }
/* 393 */       return this.parent;
/*     */     }
/* 395 */     return this;
/*     */   }
/*     */ 
/*     */   public RecoveredElement updateOnOpeningBrace(int braceStart, int braceEnd)
/*     */   {
/* 404 */     Block block = new Block(0);
/* 405 */     block.sourceStart = parser().scanner.startPosition;
/* 406 */     return add(block, 1);
/*     */   }
/*     */ 
/*     */   public void updateParseTree()
/*     */   {
/* 413 */     updatedBlock(0, new HashSet());
/*     */   }
/*     */ 
/*     */   public Statement updateStatement(int depth, Set knownTypes)
/*     */   {
/* 421 */     if ((this.blockDeclaration.sourceEnd != 0) || (this.statementCount == 0)) return null;
/*     */ 
/* 423 */     Statement[] updatedStatements = new Statement[this.statementCount];
/* 424 */     int updatedCount = 0;
/*     */ 
/* 427 */     for (int i = 0; i < this.statementCount; i++) {
/* 428 */       Statement updatedStatement = this.statements[i].updatedStatement(depth, knownTypes);
/* 429 */       if (updatedStatement != null) {
/* 430 */         updatedStatements[(updatedCount++)] = updatedStatement;
/*     */       }
/*     */     }
/* 433 */     if (updatedCount == 0) return null;
/*     */ 
/* 436 */     if (updatedCount != this.statementCount) {
/* 437 */       this.blockDeclaration.statements = new Statement[updatedCount];
/* 438 */       System.arraycopy(updatedStatements, 0, this.blockDeclaration.statements, 0, updatedCount);
/*     */     } else {
/* 440 */       this.blockDeclaration.statements = updatedStatements;
/*     */     }
/*     */ 
/* 443 */     return this.blockDeclaration;
/*     */   }
/*     */ 
/*     */   public RecoveredElement add(FieldDeclaration fieldDeclaration, int bracketBalanceValue)
/*     */   {
/* 450 */     resetPendingModifiers();
/*     */     char[][] fieldTypeName;
/* 454 */     if (((fieldDeclaration.modifiers & 0xFFFFFFEF) != 0) || 
/* 455 */       (fieldDeclaration.type == null) || (
/* 456 */       ((fieldTypeName = fieldDeclaration.type.getTypeName()).length == 1) && 
/* 457 */       (CharOperation.equals(fieldTypeName[0], TypeBinding.VOID.sourceName())))) {
/* 458 */       updateSourceEndIfNecessary(previousAvailableLineEnd(fieldDeclaration.declarationSourceStart - 1));
/* 459 */       return this.parent.add(fieldDeclaration, bracketBalanceValue);
/*     */     }
/*     */     char[][] fieldTypeName;
/* 464 */     if ((this.blockDeclaration.sourceEnd != 0) && 
/* 465 */       (fieldDeclaration.declarationSourceStart > this.blockDeclaration.sourceEnd)) {
/* 466 */       return this.parent.add(fieldDeclaration, bracketBalanceValue);
/*     */     }
/*     */ 
/* 472 */     return this;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\ecj-3.5.2.jar
 * Qualified Name:     org.eclipse.jdt.internal.compiler.parser.RecoveredBlock
 * JD-Core Version:    0.6.0
 */