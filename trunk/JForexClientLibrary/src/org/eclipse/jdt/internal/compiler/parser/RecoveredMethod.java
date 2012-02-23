/*     */ package org.eclipse.jdt.internal.compiler.parser;
/*     */ 
/*     */ import java.util.HashSet;
/*     */ import java.util.Set;
/*     */ import org.eclipse.jdt.core.compiler.CharOperation;
/*     */ import org.eclipse.jdt.internal.compiler.ast.ASTNode;
/*     */ import org.eclipse.jdt.internal.compiler.ast.AbstractMethodDeclaration;
/*     */ import org.eclipse.jdt.internal.compiler.ast.Annotation;
/*     */ import org.eclipse.jdt.internal.compiler.ast.Argument;
/*     */ import org.eclipse.jdt.internal.compiler.ast.Block;
/*     */ import org.eclipse.jdt.internal.compiler.ast.ConstructorDeclaration;
/*     */ import org.eclipse.jdt.internal.compiler.ast.ExplicitConstructorCall;
/*     */ import org.eclipse.jdt.internal.compiler.ast.FieldDeclaration;
/*     */ import org.eclipse.jdt.internal.compiler.ast.LocalDeclaration;
/*     */ import org.eclipse.jdt.internal.compiler.ast.MemberValuePair;
/*     */ import org.eclipse.jdt.internal.compiler.ast.MethodDeclaration;
/*     */ import org.eclipse.jdt.internal.compiler.ast.Statement;
/*     */ import org.eclipse.jdt.internal.compiler.ast.SuperReference;
/*     */ import org.eclipse.jdt.internal.compiler.ast.TypeDeclaration;
/*     */ import org.eclipse.jdt.internal.compiler.ast.TypeParameter;
/*     */ import org.eclipse.jdt.internal.compiler.ast.TypeReference;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.BaseTypeBinding;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;
/*     */ import org.eclipse.jdt.internal.compiler.util.Util;
/*     */ 
/*     */ public class RecoveredMethod extends RecoveredElement
/*     */   implements TerminalTokens
/*     */ {
/*     */   public AbstractMethodDeclaration methodDeclaration;
/*     */   public RecoveredAnnotation[] annotations;
/*     */   public int annotationCount;
/*     */   public int modifiers;
/*     */   public int modifiersStart;
/*     */   public RecoveredType[] localTypes;
/*     */   public int localTypeCount;
/*     */   public RecoveredBlock methodBody;
/*  55 */   public boolean discardBody = true;
/*     */   int pendingModifiers;
/*  58 */   int pendingModifersSourceStart = -1;
/*     */   RecoveredAnnotation[] pendingAnnotations;
/*     */   int pendingAnnotationCount;
/*     */ 
/*     */   public RecoveredMethod(AbstractMethodDeclaration methodDeclaration, RecoveredElement parent, int bracketBalance, Parser parser)
/*     */   {
/*  63 */     super(parent, bracketBalance, parser);
/*  64 */     this.methodDeclaration = methodDeclaration;
/*  65 */     this.foundOpeningBrace = (!bodyStartsAtHeaderEnd());
/*  66 */     if (this.foundOpeningBrace)
/*  67 */       this.bracketBalance += 1;
/*     */   }
/*     */ 
/*     */   public RecoveredElement add(Block nestedBlockDeclaration, int bracketBalanceValue)
/*     */   {
/*  78 */     if (this.methodDeclaration.declarationSourceEnd > 0)
/*     */     {
/*  80 */       if (nestedBlockDeclaration.sourceStart > 
/*  80 */         this.methodDeclaration.declarationSourceEnd) {
/*  81 */         resetPendingModifiers();
/*  82 */         if (this.parent == null) {
/*  83 */           return this;
/*     */         }
/*  85 */         return this.parent.add(nestedBlockDeclaration, bracketBalanceValue);
/*     */       }
/*     */     }
/*     */ 
/*  89 */     if (!this.foundOpeningBrace) {
/*  90 */       this.foundOpeningBrace = true;
/*  91 */       this.bracketBalance += 1;
/*     */     }
/*     */ 
/*  94 */     this.methodBody = new RecoveredBlock(nestedBlockDeclaration, this, bracketBalanceValue);
/*  95 */     if (nestedBlockDeclaration.sourceEnd == 0) return this.methodBody;
/*  96 */     return this;
/*     */   }
/*     */ 
/*     */   public RecoveredElement add(FieldDeclaration fieldDeclaration, int bracketBalanceValue)
/*     */   {
/* 102 */     resetPendingModifiers();
/*     */     char[][] fieldTypeName;
/* 106 */     if (((fieldDeclaration.modifiers & 0xFFFFFFEF) != 0) || 
/* 107 */       (fieldDeclaration.type == null) || (
/* 108 */       ((fieldTypeName = fieldDeclaration.type.getTypeName()).length == 1) && 
/* 109 */       (CharOperation.equals(fieldTypeName[0], TypeBinding.VOID.sourceName())))) {
/* 110 */       if (this.parent == null) {
/* 111 */         return this;
/*     */       }
/* 113 */       updateSourceEndIfNecessary(previousAvailableLineEnd(fieldDeclaration.declarationSourceStart - 1));
/* 114 */       return this.parent.add(fieldDeclaration, bracketBalanceValue);
/*     */     }
/*     */     char[][] fieldTypeName;
/* 121 */     if (this.methodDeclaration.declarationSourceEnd > 0)
/*     */     {
/* 123 */       if (fieldDeclaration.declarationSourceStart > 
/* 123 */         this.methodDeclaration.declarationSourceEnd) {
/* 124 */         if (this.parent == null) {
/* 125 */           return this;
/*     */         }
/* 127 */         return this.parent.add(fieldDeclaration, bracketBalanceValue);
/*     */       }
/*     */     }
/*     */ 
/* 131 */     if (!this.foundOpeningBrace) {
/* 132 */       this.foundOpeningBrace = true;
/* 133 */       this.bracketBalance += 1;
/*     */     }
/*     */ 
/* 136 */     return this;
/*     */   }
/*     */ 
/*     */   public RecoveredElement add(LocalDeclaration localDeclaration, int bracketBalanceValue)
/*     */   {
/* 142 */     resetPendingModifiers();
/*     */ 
/* 162 */     if ((this.methodDeclaration.declarationSourceEnd != 0) && 
/* 163 */       (localDeclaration.declarationSourceStart > this.methodDeclaration.declarationSourceEnd))
/*     */     {
/* 165 */       if (this.parent == null) {
/* 166 */         return this;
/*     */       }
/* 168 */       return this.parent.add(localDeclaration, bracketBalanceValue);
/*     */     }
/*     */ 
/* 171 */     if (this.methodBody == null) {
/* 172 */       Block block = new Block(0);
/* 173 */       block.sourceStart = this.methodDeclaration.bodyStart;
/* 174 */       RecoveredElement currentBlock = add(block, 1);
/* 175 */       if (this.bracketBalance > 0) {
/* 176 */         for (int i = 0; i < this.bracketBalance - 1; i++) {
/* 177 */           currentBlock = currentBlock.add(new Block(0), 1);
/*     */         }
/* 179 */         this.bracketBalance = 1;
/*     */       }
/* 181 */       return currentBlock.add(localDeclaration, bracketBalanceValue);
/*     */     }
/* 183 */     return this.methodBody.add(localDeclaration, bracketBalanceValue, true);
/*     */   }
/*     */ 
/*     */   public RecoveredElement add(Statement statement, int bracketBalanceValue)
/*     */   {
/* 189 */     resetPendingModifiers();
/*     */ 
/* 193 */     if ((this.methodDeclaration.declarationSourceEnd != 0) && 
/* 194 */       (statement.sourceStart > this.methodDeclaration.declarationSourceEnd))
/*     */     {
/* 196 */       if (this.parent == null) {
/* 197 */         return this;
/*     */       }
/* 199 */       return this.parent.add(statement, bracketBalanceValue);
/*     */     }
/*     */ 
/* 202 */     if (this.methodBody == null) {
/* 203 */       Block block = new Block(0);
/* 204 */       block.sourceStart = this.methodDeclaration.bodyStart;
/* 205 */       RecoveredElement currentBlock = add(block, 1);
/* 206 */       if (this.bracketBalance > 0) {
/* 207 */         for (int i = 0; i < this.bracketBalance - 1; i++) {
/* 208 */           currentBlock = currentBlock.add(new Block(0), 1);
/*     */         }
/* 210 */         this.bracketBalance = 1;
/*     */       }
/* 212 */       return currentBlock.add(statement, bracketBalanceValue);
/*     */     }
/* 214 */     return this.methodBody.add(statement, bracketBalanceValue, true);
/*     */   }
/*     */ 
/*     */   public RecoveredElement add(TypeDeclaration typeDeclaration, int bracketBalanceValue)
/*     */   {
/* 220 */     if ((this.methodDeclaration.declarationSourceEnd != 0) && 
/* 221 */       (typeDeclaration.declarationSourceStart > this.methodDeclaration.declarationSourceEnd))
/*     */     {
/* 223 */       if (this.parent == null) {
/* 224 */         return this;
/*     */       }
/* 226 */       return this.parent.add(typeDeclaration, bracketBalanceValue);
/*     */     }
/* 228 */     if (((typeDeclaration.bits & 0x100) != 0) || (parser().methodRecoveryActivated) || (parser().statementRecoveryActivated)) {
/* 229 */       if (this.methodBody == null) {
/* 230 */         Block block = new Block(0);
/* 231 */         block.sourceStart = this.methodDeclaration.bodyStart;
/* 232 */         add(block, 1);
/*     */       }
/* 234 */       this.methodBody.attachPendingModifiers(
/* 235 */         this.pendingAnnotations, 
/* 236 */         this.pendingAnnotationCount, 
/* 237 */         this.pendingModifiers, 
/* 238 */         this.pendingModifersSourceStart);
/* 239 */       resetPendingModifiers();
/* 240 */       return this.methodBody.add(typeDeclaration, bracketBalanceValue, true);
/*     */     }
/* 242 */     switch (TypeDeclaration.kind(typeDeclaration.modifiers)) {
/*     */     case 2:
/*     */     case 4:
/* 245 */       resetPendingModifiers();
/* 246 */       updateSourceEndIfNecessary(previousAvailableLineEnd(typeDeclaration.declarationSourceStart - 1));
/* 247 */       if (this.parent == null) {
/* 248 */         return this;
/*     */       }
/*     */ 
/* 251 */       return this.parent.add(typeDeclaration, bracketBalanceValue);
/*     */     case 3:
/* 253 */     }if (this.localTypes == null) {
/* 254 */       this.localTypes = new RecoveredType[5];
/* 255 */       this.localTypeCount = 0;
/*     */     }
/* 257 */     else if (this.localTypeCount == this.localTypes.length) {
/* 258 */       System.arraycopy(
/* 259 */         this.localTypes, 
/* 260 */         0, 
/* 261 */         this.localTypes = new RecoveredType[2 * this.localTypeCount], 
/* 262 */         0, 
/* 263 */         this.localTypeCount);
/*     */     }
/*     */ 
/* 266 */     RecoveredType element = new RecoveredType(typeDeclaration, this, bracketBalanceValue);
/* 267 */     this.localTypes[(this.localTypeCount++)] = element;
/*     */ 
/* 269 */     if (this.pendingAnnotationCount > 0) {
/* 270 */       element.attach(
/* 271 */         this.pendingAnnotations, 
/* 272 */         this.pendingAnnotationCount, 
/* 273 */         this.pendingModifiers, 
/* 274 */         this.pendingModifersSourceStart);
/*     */     }
/* 276 */     resetPendingModifiers();
/*     */ 
/* 279 */     if (!this.foundOpeningBrace) {
/* 280 */       this.foundOpeningBrace = true;
/* 281 */       this.bracketBalance += 1;
/*     */     }
/* 283 */     return element;
/*     */   }
/*     */   public boolean bodyStartsAtHeaderEnd() {
/* 286 */     return this.methodDeclaration.bodyStart == this.methodDeclaration.sourceEnd + 1;
/*     */   }
/*     */ 
/*     */   public ASTNode parseTree()
/*     */   {
/* 292 */     return this.methodDeclaration;
/*     */   }
/*     */   public void resetPendingModifiers() {
/* 295 */     this.pendingAnnotations = null;
/* 296 */     this.pendingAnnotationCount = 0;
/* 297 */     this.pendingModifiers = 0;
/* 298 */     this.pendingModifersSourceStart = -1;
/*     */   }
/*     */ 
/*     */   public int sourceEnd()
/*     */   {
/* 304 */     return this.methodDeclaration.declarationSourceEnd;
/*     */   }
/*     */   public String toString(int tab) {
/* 307 */     StringBuffer result = new StringBuffer(tabString(tab));
/* 308 */     result.append("Recovered method:\n");
/* 309 */     this.methodDeclaration.print(tab + 1, result);
/* 310 */     if (this.annotations != null) {
/* 311 */       for (int i = 0; i < this.annotationCount; i++) {
/* 312 */         result.append("\n");
/* 313 */         result.append(this.annotations[i].toString(tab + 1));
/*     */       }
/*     */     }
/* 316 */     if (this.localTypes != null) {
/* 317 */       for (int i = 0; i < this.localTypeCount; i++) {
/* 318 */         result.append("\n");
/* 319 */         result.append(this.localTypes[i].toString(tab + 1));
/*     */       }
/*     */     }
/* 322 */     if (this.methodBody != null) {
/* 323 */       result.append("\n");
/* 324 */       result.append(this.methodBody.toString(tab + 1));
/*     */     }
/* 326 */     return result.toString();
/*     */   }
/*     */ 
/*     */   public void updateBodyStart(int bodyStart)
/*     */   {
/* 332 */     this.foundOpeningBrace = true;
/* 333 */     this.methodDeclaration.bodyStart = bodyStart;
/*     */   }
/*     */ 
/*     */   public AbstractMethodDeclaration updatedMethodDeclaration(int depth, Set knownTypes) {
/* 337 */     if (this.modifiers != 0) {
/* 338 */       this.methodDeclaration.modifiers |= this.modifiers;
/* 339 */       if (this.modifiersStart < this.methodDeclaration.declarationSourceStart) {
/* 340 */         this.methodDeclaration.declarationSourceStart = this.modifiersStart;
/*     */       }
/*     */     }
/*     */ 
/* 344 */     if (this.annotationCount > 0) {
/* 345 */       int existingCount = this.methodDeclaration.annotations == null ? 0 : this.methodDeclaration.annotations.length;
/* 346 */       Annotation[] annotationReferences = new Annotation[existingCount + this.annotationCount];
/* 347 */       if (existingCount > 0) {
/* 348 */         System.arraycopy(this.methodDeclaration.annotations, 0, annotationReferences, this.annotationCount, existingCount);
/*     */       }
/* 350 */       for (int i = 0; i < this.annotationCount; i++) {
/* 351 */         annotationReferences[i] = this.annotations[i].updatedAnnotationReference();
/*     */       }
/* 353 */       this.methodDeclaration.annotations = annotationReferences;
/*     */ 
/* 355 */       int start = this.annotations[0].annotation.sourceStart;
/* 356 */       if (start < this.methodDeclaration.declarationSourceStart) {
/* 357 */         this.methodDeclaration.declarationSourceStart = start;
/*     */       }
/*     */     }
/*     */ 
/* 361 */     if (this.methodBody != null) {
/* 362 */       Block block = this.methodBody.updatedBlock(depth, knownTypes);
/* 363 */       if (block != null) {
/* 364 */         this.methodDeclaration.statements = block.statements;
/*     */ 
/* 366 */         if (this.methodDeclaration.declarationSourceEnd == 0) {
/* 367 */           this.methodDeclaration.declarationSourceEnd = block.sourceEnd;
/* 368 */           this.methodDeclaration.bodyEnd = block.sourceEnd;
/*     */         }
/*     */ 
/* 372 */         if (this.methodDeclaration.isConstructor()) {
/* 373 */           ConstructorDeclaration constructor = (ConstructorDeclaration)this.methodDeclaration;
/* 374 */           if ((this.methodDeclaration.statements != null) && 
/* 375 */             ((this.methodDeclaration.statements[0] instanceof ExplicitConstructorCall))) {
/* 376 */             constructor.constructorCall = ((ExplicitConstructorCall)this.methodDeclaration.statements[0]);
/* 377 */             int length = this.methodDeclaration.statements.length;
/* 378 */             System.arraycopy(
/* 379 */               this.methodDeclaration.statements, 
/* 380 */               1, 
/* 381 */               this.methodDeclaration.statements = new Statement[length - 1], 
/* 382 */               0, 
/* 383 */               length - 1);
/*     */           }
/* 385 */           if (constructor.constructorCall == null) {
/* 386 */             constructor.constructorCall = SuperReference.implicitSuperConstructorCall();
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/* 391 */     else if (this.methodDeclaration.declarationSourceEnd == 0) {
/* 392 */       if (this.methodDeclaration.sourceEnd + 1 == this.methodDeclaration.bodyStart)
/*     */       {
/* 394 */         this.methodDeclaration.declarationSourceEnd = this.methodDeclaration.sourceEnd;
/* 395 */         this.methodDeclaration.bodyStart = this.methodDeclaration.sourceEnd;
/* 396 */         this.methodDeclaration.bodyEnd = this.methodDeclaration.sourceEnd;
/*     */       } else {
/* 398 */         this.methodDeclaration.declarationSourceEnd = this.methodDeclaration.bodyStart;
/* 399 */         this.methodDeclaration.bodyEnd = this.methodDeclaration.bodyStart;
/*     */       }
/*     */     }
/*     */ 
/* 403 */     if (this.localTypeCount > 0) this.methodDeclaration.bits |= 2;
/* 404 */     return this.methodDeclaration;
/*     */   }
/*     */ 
/*     */   public void updateFromParserState()
/*     */   {
/* 412 */     if ((bodyStartsAtHeaderEnd()) && (this.parent != null)) {
/* 413 */       Parser parser = parser();
/*     */ 
/* 415 */       if ((parser.listLength > 0) && (parser.astLengthPtr > 0))
/*     */       {
/* 417 */         if (this.methodDeclaration.sourceEnd == parser.rParenPos)
/*     */         {
/* 420 */           int length = parser.astLengthStack[parser.astLengthPtr];
/* 421 */           int astPtr = parser.astPtr - length;
/* 422 */           boolean canConsume = astPtr >= 0;
/* 423 */           if (canConsume) {
/* 424 */             if (!(parser.astStack[astPtr] instanceof AbstractMethodDeclaration)) {
/* 425 */               canConsume = false;
/*     */             }
/* 427 */             int i = 1; for (int max = length + 1; i < max; i++) {
/* 428 */               if (!(parser.astStack[(astPtr + i)] instanceof TypeReference)) {
/* 429 */                 canConsume = false;
/*     */               }
/*     */             }
/*     */           }
/* 433 */           if (canConsume) {
/* 434 */             parser.consumeMethodHeaderThrowsClause();
/*     */           }
/*     */           else
/*     */           {
/* 438 */             parser.listLength = 0;
/*     */           }
/*     */         }
/*     */         else {
/* 442 */           if ((parser.currentToken == 28) || (parser.currentToken == 27))
/*     */           {
/* 444 */             parser.astLengthStack[parser.astLengthPtr] -= 1;
/* 445 */             parser.astPtr -= 1;
/* 446 */             parser.listLength -= 1;
/* 447 */             parser.currentToken = 0;
/*     */           }
/* 449 */           int argLength = parser.astLengthStack[parser.astLengthPtr];
/* 450 */           int argStart = parser.astPtr - argLength + 1;
/* 451 */           boolean needUpdateRParenPos = parser.rParenPos < parser.lParenPos;
/*     */ 
/* 454 */           MemberValuePair[] memberValuePairs = (MemberValuePair[])null;
/* 455 */           while ((argLength > 0) && ((parser.astStack[parser.astPtr] instanceof MemberValuePair))) {
/* 456 */             System.arraycopy(parser.astStack, argStart, memberValuePairs = new MemberValuePair[argLength], 0, argLength);
/* 457 */             parser.astLengthPtr -= 1;
/* 458 */             parser.astPtr -= argLength;
/*     */ 
/* 460 */             argLength = parser.astLengthStack[parser.astLengthPtr];
/* 461 */             argStart = parser.astPtr - argLength + 1;
/* 462 */             needUpdateRParenPos = true;
/*     */           }
/*     */ 
/* 467 */           for (int count = 0; count < argLength; count++) {
/* 468 */             ASTNode aNode = parser.astStack[(argStart + count)];
/* 469 */             if ((aNode instanceof Argument)) {
/* 470 */               Argument argument = (Argument)aNode;
/*     */ 
/* 472 */               char[][] argTypeName = argument.type.getTypeName();
/* 473 */               if (((argument.modifiers & 0xFFFFFFEF) != 0) || (
/* 474 */                 (argTypeName.length == 1) && 
/* 475 */                 (CharOperation.equals(argTypeName[0], TypeBinding.VOID.sourceName())))) {
/* 476 */                 parser.astLengthStack[parser.astLengthPtr] = count;
/* 477 */                 parser.astPtr = (argStart + count - 1);
/* 478 */                 parser.listLength = count;
/* 479 */                 parser.currentToken = 0;
/* 480 */                 break;
/*     */               }
/* 482 */               if (!needUpdateRParenPos) continue; parser.rParenPos = (argument.sourceEnd + 1);
/*     */             } else {
/* 484 */               parser.astLengthStack[parser.astLengthPtr] = count;
/* 485 */               parser.astPtr = (argStart + count - 1);
/* 486 */               parser.listLength = count;
/* 487 */               parser.currentToken = 0;
/* 488 */               break;
/*     */             }
/*     */           }
/* 491 */           if ((parser.listLength > 0) && (parser.astLengthPtr > 0))
/*     */           {
/* 494 */             int length = parser.astLengthStack[parser.astLengthPtr];
/* 495 */             int astPtr = parser.astPtr - length;
/* 496 */             boolean canConsume = astPtr >= 0;
/* 497 */             if (canConsume) {
/* 498 */               if (!(parser.astStack[astPtr] instanceof AbstractMethodDeclaration)) {
/* 499 */                 canConsume = false;
/*     */               }
/* 501 */               int i = 1; for (int max = length + 1; i < max; i++) {
/* 502 */                 if (!(parser.astStack[(astPtr + i)] instanceof Argument)) {
/* 503 */                   canConsume = false;
/*     */                 }
/*     */               }
/*     */             }
/* 507 */             if (canConsume) {
/* 508 */               parser.consumeMethodHeaderRightParen();
/*     */ 
/* 510 */               if (parser.currentElement == this) {
/* 511 */                 this.methodDeclaration.sourceEnd = this.methodDeclaration.arguments[(this.methodDeclaration.arguments.length - 1)].sourceEnd;
/* 512 */                 this.methodDeclaration.bodyStart = (this.methodDeclaration.sourceEnd + 1);
/* 513 */                 parser.lastCheckPoint = this.methodDeclaration.bodyStart;
/*     */               }
/*     */             }
/*     */           }
/*     */ 
/* 518 */           if (memberValuePairs != null) {
/* 519 */             System.arraycopy(memberValuePairs, 0, parser.astStack, parser.astPtr + 1, memberValuePairs.length);
/* 520 */             parser.astPtr += memberValuePairs.length;
/* 521 */             parser.astLengthStack[(++parser.astLengthPtr)] = memberValuePairs.length;
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public RecoveredElement updateOnClosingBrace(int braceStart, int braceEnd) {
/* 528 */     if (this.methodDeclaration.isAnnotationMethod()) {
/* 529 */       updateSourceEndIfNecessary(braceStart, braceEnd);
/* 530 */       if ((!this.foundOpeningBrace) && (this.parent != null)) {
/* 531 */         return this.parent.updateOnClosingBrace(braceStart, braceEnd);
/*     */       }
/* 533 */       return this;
/*     */     }
/* 535 */     if ((this.parent != null) && ((this.parent instanceof RecoveredType))) {
/* 536 */       int mods = ((RecoveredType)this.parent).typeDeclaration.modifiers;
/* 537 */       if ((TypeDeclaration.kind(mods) == 2) && 
/* 538 */         (!this.foundOpeningBrace)) {
/* 539 */         updateSourceEndIfNecessary(braceStart - 1, braceStart - 1);
/* 540 */         return this.parent.updateOnClosingBrace(braceStart, braceEnd);
/*     */       }
/*     */     }
/*     */ 
/* 544 */     return super.updateOnClosingBrace(braceStart, braceEnd);
/*     */   }
/*     */ 
/*     */   public RecoveredElement updateOnOpeningBrace(int braceStart, int braceEnd)
/*     */   {
/* 553 */     if (this.bracketBalance == 0)
/*     */     {
/* 558 */       switch (parser().lastIgnoredToken) {
/*     */       case -1:
/*     */       case 105:
/* 561 */         break;
/*     */       default:
/* 563 */         this.foundOpeningBrace = true;
/* 564 */         this.bracketBalance = 1;
/*     */       }
/*     */     }
/* 567 */     return super.updateOnOpeningBrace(braceStart, braceEnd);
/*     */   }
/*     */   public void updateParseTree() {
/* 570 */     updatedMethodDeclaration(0, new HashSet());
/*     */   }
/*     */ 
/*     */   public void updateSourceEndIfNecessary(int braceStart, int braceEnd)
/*     */   {
/* 576 */     if (this.methodDeclaration.declarationSourceEnd == 0)
/* 577 */       if (parser().rBraceSuccessorStart >= braceEnd) {
/* 578 */         this.methodDeclaration.declarationSourceEnd = parser().rBraceEnd;
/* 579 */         this.methodDeclaration.bodyEnd = parser().rBraceStart;
/*     */       } else {
/* 581 */         this.methodDeclaration.declarationSourceEnd = braceEnd;
/* 582 */         this.methodDeclaration.bodyEnd = (braceStart - 1);
/*     */       }
/*     */   }
/*     */ 
/*     */   public RecoveredElement addAnnotationName(int identifierPtr, int identifierLengthPtr, int annotationStart, int bracketBalanceValue) {
/* 587 */     if (this.pendingAnnotations == null) {
/* 588 */       this.pendingAnnotations = new RecoveredAnnotation[5];
/* 589 */       this.pendingAnnotationCount = 0;
/*     */     }
/* 591 */     else if (this.pendingAnnotationCount == this.pendingAnnotations.length) {
/* 592 */       System.arraycopy(
/* 593 */         this.pendingAnnotations, 
/* 594 */         0, 
/* 595 */         this.pendingAnnotations = new RecoveredAnnotation[2 * this.pendingAnnotationCount], 
/* 596 */         0, 
/* 597 */         this.pendingAnnotationCount);
/*     */     }
/*     */ 
/* 601 */     RecoveredAnnotation element = new RecoveredAnnotation(identifierPtr, identifierLengthPtr, annotationStart, this, bracketBalanceValue);
/*     */ 
/* 603 */     this.pendingAnnotations[(this.pendingAnnotationCount++)] = element;
/*     */ 
/* 605 */     return element;
/*     */   }
/*     */   public void addModifier(int flag, int modifiersSourceStart) {
/* 608 */     this.pendingModifiers |= flag;
/*     */ 
/* 610 */     if (this.pendingModifersSourceStart < 0)
/* 611 */       this.pendingModifersSourceStart = modifiersSourceStart;
/*     */   }
/*     */ 
/*     */   void attach(TypeParameter[] parameters, int startPos) {
/* 615 */     if (this.methodDeclaration.modifiers != 0) return;
/*     */ 
/* 617 */     int lastParameterEnd = parameters[(parameters.length - 1)].sourceEnd;
/*     */ 
/* 619 */     Parser parser = parser();
/* 620 */     Scanner scanner = parser.scanner;
/* 621 */     if (Util.getLineNumber(this.methodDeclaration.declarationSourceStart, scanner.lineEnds, 0, scanner.linePtr) != 
/* 622 */       Util.getLineNumber(lastParameterEnd, scanner.lineEnds, 0, scanner.linePtr)) return;
/*     */ 
/* 624 */     if ((parser.modifiersSourceStart > lastParameterEnd) && 
/* 625 */       (parser.modifiersSourceStart < this.methodDeclaration.declarationSourceStart)) return;
/*     */ 
/* 627 */     if ((this.methodDeclaration instanceof MethodDeclaration)) {
/* 628 */       ((MethodDeclaration)this.methodDeclaration).typeParameters = parameters;
/* 629 */       this.methodDeclaration.declarationSourceStart = startPos;
/* 630 */     } else if ((this.methodDeclaration instanceof ConstructorDeclaration)) {
/* 631 */       ((ConstructorDeclaration)this.methodDeclaration).typeParameters = parameters;
/* 632 */       this.methodDeclaration.declarationSourceStart = startPos;
/*     */     }
/*     */   }
/*     */ 
/*     */   public void attach(RecoveredAnnotation[] annots, int annotCount, int mods, int modsSourceStart) {
/* 636 */     if (annotCount > 0) {
/* 637 */       Annotation[] existingAnnotations = this.methodDeclaration.annotations;
/* 638 */       if (existingAnnotations != null) {
/* 639 */         this.annotations = new RecoveredAnnotation[annotCount];
/* 640 */         this.annotationCount = 0;
/* 641 */         for (int i = 0; i < annotCount; i++) {
/* 642 */           int j = 0;
/* 643 */           while (annots[i].annotation != existingAnnotations[j])
/*     */           {
/* 642 */             j++; if (j < existingAnnotations.length) {
/*     */               continue;
/*     */             }
/* 645 */             this.annotations[(this.annotationCount++)] = annots[i];
/*     */           }
/*     */         }
/*     */       } else {
/* 648 */         this.annotations = annots;
/* 649 */         this.annotationCount = annotCount;
/*     */       }
/*     */     }
/*     */ 
/* 653 */     if (mods != 0) {
/* 654 */       this.modifiers = mods;
/* 655 */       this.modifiersStart = modsSourceStart;
/*     */     }
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\ecj-3.5.2.jar
 * Qualified Name:     org.eclipse.jdt.internal.compiler.parser.RecoveredMethod
 * JD-Core Version:    0.6.0
 */