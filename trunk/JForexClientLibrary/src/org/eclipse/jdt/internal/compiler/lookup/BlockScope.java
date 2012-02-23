/*     */ package org.eclipse.jdt.internal.compiler.lookup;
/*     */ 
/*     */ import org.eclipse.jdt.core.compiler.CharOperation;
/*     */ import org.eclipse.jdt.internal.compiler.ast.ASTNode;
/*     */ import org.eclipse.jdt.internal.compiler.ast.AbstractMethodDeclaration;
/*     */ import org.eclipse.jdt.internal.compiler.ast.Argument;
/*     */ import org.eclipse.jdt.internal.compiler.ast.CaseStatement;
/*     */ import org.eclipse.jdt.internal.compiler.ast.LocalDeclaration;
/*     */ import org.eclipse.jdt.internal.compiler.ast.TypeDeclaration;
/*     */ import org.eclipse.jdt.internal.compiler.codegen.CodeStream;
/*     */ import org.eclipse.jdt.internal.compiler.impl.CompilerOptions;
/*     */ import org.eclipse.jdt.internal.compiler.impl.Constant;
/*     */ import org.eclipse.jdt.internal.compiler.problem.ProblemReporter;
/*     */ 
/*     */ public class BlockScope extends Scope
/*     */ {
/*     */   public LocalVariableBinding[] locals;
/*     */   public int localIndex;
/*     */   public int startIndex;
/*     */   public int offset;
/*     */   public int maxOffset;
/*     */   public BlockScope[] shiftScopes;
/*  33 */   public Scope[] subscopes = new Scope[1];
/*  34 */   public int subscopeCount = 0;
/*     */   public CaseStatement enclosingCase;
/*  38 */   public static final VariableBinding[] EmulationPathToImplicitThis = new VariableBinding[0];
/*  39 */   public static final VariableBinding[] NoEnclosingInstanceInConstructorCall = new VariableBinding[0];
/*     */ 
/*  41 */   public static final VariableBinding[] NoEnclosingInstanceInStaticContext = new VariableBinding[0];
/*     */ 
/*     */   public BlockScope(BlockScope parent) {
/*  44 */     this(parent, true);
/*     */   }
/*     */ 
/*     */   public BlockScope(BlockScope parent, boolean addToParentScope) {
/*  48 */     this(1, parent);
/*  49 */     this.locals = new LocalVariableBinding[5];
/*  50 */     if (addToParentScope) parent.addSubscope(this);
/*  51 */     this.startIndex = parent.localIndex;
/*     */   }
/*     */ 
/*     */   public BlockScope(BlockScope parent, int variableCount) {
/*  55 */     this(1, parent);
/*  56 */     this.locals = new LocalVariableBinding[variableCount];
/*  57 */     parent.addSubscope(this);
/*  58 */     this.startIndex = parent.localIndex;
/*     */   }
/*     */ 
/*     */   protected BlockScope(int kind, Scope parent) {
/*  62 */     super(kind, parent);
/*     */   }
/*     */ 
/*     */   public final void addAnonymousType(TypeDeclaration anonymousType, ReferenceBinding superBinding)
/*     */   {
/*  68 */     ClassScope anonymousClassScope = new ClassScope(this, anonymousType);
/*  69 */     anonymousClassScope.buildAnonymousTypeBinding(
/*  70 */       enclosingSourceType(), 
/*  71 */       superBinding);
/*     */   }
/*     */ 
/*     */   public final void addLocalType(TypeDeclaration localType)
/*     */   {
/*  77 */     ClassScope localTypeScope = new ClassScope(this, localType);
/*  78 */     addSubscope(localTypeScope);
/*  79 */     localTypeScope.buildLocalTypeBinding(enclosingSourceType());
/*     */   }
/*     */ 
/*     */   public final void addLocalVariable(LocalVariableBinding binding)
/*     */   {
/*  86 */     checkAndSetModifiersForVariable(binding);
/*     */ 
/*  88 */     if (this.localIndex == this.locals.length)
/*  89 */       System.arraycopy(
/*  90 */         this.locals, 
/*  91 */         0, 
/*  92 */         this.locals = new LocalVariableBinding[this.localIndex * 2], 
/*  93 */         0, 
/*  94 */         this.localIndex);
/*  95 */     this.locals[(this.localIndex++)] = binding;
/*     */ 
/*  98 */     binding.declaringScope = this;
/*  99 */     binding.id = (outerMostMethodScope().analysisIndex++);
/*     */   }
/*     */ 
/*     */   public void addSubscope(Scope childScope)
/*     */   {
/* 104 */     if (this.subscopeCount == this.subscopes.length)
/* 105 */       System.arraycopy(
/* 106 */         this.subscopes, 
/* 107 */         0, 
/* 108 */         this.subscopes = new Scope[this.subscopeCount * 2], 
/* 109 */         0, 
/* 110 */         this.subscopeCount);
/* 111 */     this.subscopes[(this.subscopeCount++)] = childScope;
/*     */   }
/*     */ 
/*     */   public final boolean allowBlankFinalFieldAssignment(FieldBinding binding)
/*     */   {
/* 119 */     if (enclosingReceiverType() != binding.declaringClass) {
/* 120 */       return false;
/*     */     }
/* 122 */     MethodScope methodScope = methodScope();
/* 123 */     if (methodScope.isStatic != binding.isStatic()) {
/* 124 */       return false;
/*     */     }
/* 126 */     return (methodScope.isInsideInitializer()) || 
/* 126 */       (((AbstractMethodDeclaration)methodScope.referenceContext).isInitializationMethod());
/*     */   }
/*     */ 
/*     */   String basicToString(int tab) {
/* 130 */     String newLine = "\n";
/* 131 */     int i = tab;
/*     */     do { newLine = newLine + "\t";
/*     */ 
/* 131 */       i--; } while (i >= 0);
/*     */ 
/* 134 */     String s = newLine + "--- Block Scope ---";
/* 135 */     newLine = newLine + "\t";
/* 136 */     s = s + newLine + "locals:";
/* 137 */     for (int i = 0; i < this.localIndex; i++)
/* 138 */       s = s + newLine + "\t" + this.locals[i].toString();
/* 139 */     s = s + newLine + "startIndex = " + this.startIndex;
/* 140 */     return s;
/*     */   }
/*     */ 
/*     */   private void checkAndSetModifiersForVariable(LocalVariableBinding varBinding) {
/* 144 */     int modifiers = varBinding.modifiers;
/* 145 */     if (((modifiers & 0x400000) != 0) && (varBinding.declaration != null)) {
/* 146 */       problemReporter().duplicateModifierForVariable(varBinding.declaration, this instanceof MethodScope);
/*     */     }
/* 148 */     int realModifiers = modifiers & 0xFFFF;
/*     */ 
/* 150 */     int unexpectedModifiers = -17;
/* 151 */     if (((realModifiers & unexpectedModifiers) != 0) && (varBinding.declaration != null)) {
/* 152 */       problemReporter().illegalModifierForVariable(varBinding.declaration, this instanceof MethodScope);
/*     */     }
/* 154 */     varBinding.modifiers = modifiers;
/*     */   }
/*     */ 
/*     */   void computeLocalVariablePositions(int ilocal, int initOffset, CodeStream codeStream)
/*     */   {
/* 164 */     this.offset = initOffset;
/* 165 */     this.maxOffset = initOffset;
/*     */ 
/* 168 */     int maxLocals = this.localIndex;
/* 169 */     boolean hasMoreVariables = ilocal < maxLocals;
/*     */ 
/* 172 */     int iscope = 0; int maxScopes = this.subscopeCount;
/* 173 */     boolean hasMoreScopes = maxScopes > 0;
/*     */ 
/* 176 */     while ((hasMoreVariables) || (hasMoreScopes)) {
/* 177 */       if ((hasMoreScopes) && (
/* 178 */         (!hasMoreVariables) || (this.subscopes[iscope].startIndex() <= ilocal)))
/*     */       {
/* 180 */         if ((this.subscopes[iscope] instanceof BlockScope)) {
/* 181 */           BlockScope subscope = (BlockScope)this.subscopes[iscope];
/* 182 */           int subOffset = subscope.shiftScopes == null ? this.offset : subscope.maxShiftedOffset();
/* 183 */           subscope.computeLocalVariablePositions(0, subOffset, codeStream);
/* 184 */           if (subscope.maxOffset > this.maxOffset)
/* 185 */             this.maxOffset = subscope.maxOffset;
/*     */         }
/* 187 */         iscope++; hasMoreScopes = iscope < maxScopes;
/*     */       }
/*     */       else
/*     */       {
/* 191 */         LocalVariableBinding local = this.locals[ilocal];
/*     */ 
/* 194 */         boolean generateCurrentLocalVar = (local.useFlag == 1) && (local.constant() == Constant.NotAConstant);
/*     */ 
/* 197 */         if ((local.useFlag == 0) && 
/* 198 */           (local.declaration != null) && 
/* 199 */           ((local.declaration.bits & 0x40000000) != 0))
/*     */         {
/* 201 */           if (!(local.declaration instanceof Argument)) {
/* 202 */             problemReporter().unusedLocalVariable(local.declaration);
/*     */           }
/*     */         }
/*     */ 
/* 206 */         if ((!generateCurrentLocalVar) && 
/* 207 */           (local.declaration != null) && (compilerOptions().preserveAllLocalVariables)) {
/* 208 */           generateCurrentLocalVar = true;
/* 209 */           local.useFlag = 1;
/*     */         }
/*     */ 
/* 214 */         if (generateCurrentLocalVar)
/*     */         {
/* 216 */           if (local.declaration != null) {
/* 217 */             codeStream.record(local);
/*     */           }
/*     */ 
/* 220 */           local.resolvedPosition = this.offset;
/*     */ 
/* 222 */           if ((local.type == TypeBinding.LONG) || (local.type == TypeBinding.DOUBLE))
/* 223 */             this.offset += 2;
/*     */           else {
/* 225 */             this.offset += 1;
/*     */           }
/* 227 */           if (this.offset > 65535)
/* 228 */             problemReporter().noMoreAvailableSpaceForLocal(
/* 229 */               local, 
/* 230 */               local.declaration == null ? (ASTNode)methodScope().referenceContext : local.declaration);
/*     */         }
/*     */         else {
/* 233 */           local.resolvedPosition = -1;
/*     */         }
/* 235 */         ilocal++; hasMoreVariables = ilocal < maxLocals;
/*     */       }
/*     */     }
/* 238 */     if (this.offset > this.maxOffset)
/* 239 */       this.maxOffset = this.offset;
/*     */   }
/*     */ 
/*     */   public void emulateOuterAccess(LocalVariableBinding outerLocalVariable)
/*     */   {
/* 250 */     BlockScope outerVariableScope = outerLocalVariable.declaringScope;
/* 251 */     if (outerVariableScope == null)
/* 252 */       return;
/* 253 */     MethodScope currentMethodScope = methodScope();
/* 254 */     if (outerVariableScope.methodScope() != currentMethodScope) {
/* 255 */       NestedTypeBinding currentType = (NestedTypeBinding)enclosingSourceType();
/*     */ 
/* 258 */       if (!currentType.isLocalType()) {
/* 259 */         return;
/*     */       }
/*     */ 
/* 262 */       if (!currentMethodScope.isInsideInitializerOrConstructor())
/* 263 */         currentType.addSyntheticArgumentAndField(outerLocalVariable);
/*     */       else
/* 265 */         currentType.addSyntheticArgument(outerLocalVariable);
/*     */     }
/*     */   }
/*     */ 
/*     */   public final ReferenceBinding findLocalType(char[] name)
/*     */   {
/* 290 */     long compliance = compilerOptions().complianceLevel;
/* 291 */     for (int i = this.subscopeCount - 1; i >= 0; i--) {
/* 292 */       if ((this.subscopes[i] instanceof ClassScope)) {
/* 293 */         LocalTypeBinding sourceType = (LocalTypeBinding)((ClassScope)this.subscopes[i]).referenceContext.binding;
/*     */ 
/* 295 */         if ((compliance >= 3145728L) && (sourceType.enclosingCase != null) && 
/* 296 */           (!isInsideCase(sourceType.enclosingCase)))
/*     */         {
/*     */           continue;
/*     */         }
/* 300 */         if (CharOperation.equals(sourceType.sourceName(), name))
/* 301 */           return sourceType;
/*     */       }
/*     */     }
/* 304 */     return null;
/*     */   }
/*     */ 
/*     */   public LocalDeclaration[] findLocalVariableDeclarations(int position)
/*     */   {
/* 314 */     int ilocal = 0; int maxLocals = this.localIndex;
/* 315 */     boolean hasMoreVariables = maxLocals > 0;
/* 316 */     LocalDeclaration[] localDeclarations = (LocalDeclaration[])null;
/* 317 */     int declPtr = 0;
/*     */ 
/* 320 */     int iscope = 0; int maxScopes = this.subscopeCount;
/* 321 */     boolean hasMoreScopes = maxScopes > 0;
/*     */ 
/* 324 */     while ((hasMoreVariables) || (hasMoreScopes)) {
/* 325 */       if ((hasMoreScopes) && (
/* 326 */         (!hasMoreVariables) || (this.subscopes[iscope].startIndex() <= ilocal)))
/*     */       {
/* 328 */         Scope subscope = this.subscopes[iscope];
/* 329 */         if (subscope.kind == 1) {
/* 330 */           localDeclarations = ((BlockScope)subscope).findLocalVariableDeclarations(position);
/* 331 */           if (localDeclarations != null) {
/* 332 */             return localDeclarations;
/*     */           }
/*     */         }
/* 335 */         iscope++; hasMoreScopes = iscope < maxScopes;
/*     */       }
/*     */       else {
/* 338 */         LocalVariableBinding local = this.locals[ilocal];
/* 339 */         if (local != null) {
/* 340 */           LocalDeclaration localDecl = local.declaration;
/* 341 */           if (localDecl != null) {
/* 342 */             if (localDecl.declarationSourceStart <= position) {
/* 343 */               if (position <= localDecl.declarationSourceEnd) {
/* 344 */                 if (localDeclarations == null) {
/* 345 */                   localDeclarations = new LocalDeclaration[maxLocals];
/*     */                 }
/* 347 */                 localDeclarations[(declPtr++)] = localDecl;
/*     */               }
/*     */             }
/* 350 */             else return localDeclarations;
/*     */           }
/*     */         }
/*     */ 
/* 354 */         ilocal++; hasMoreVariables = ilocal < maxLocals;
/* 355 */         if ((!hasMoreVariables) && (localDeclarations != null)) {
/* 356 */           return localDeclarations;
/*     */         }
/*     */       }
/*     */     }
/* 360 */     return null;
/*     */   }
/*     */ 
/*     */   public LocalVariableBinding findVariable(char[] variableName) {
/* 364 */     int varLength = variableName.length;
/* 365 */     for (int i = this.localIndex - 1; i >= 0; i--)
/*     */     {
/*     */       LocalVariableBinding local;
/*     */       char[] localName;
/* 368 */       if (((localName = (local = this.locals[i]).name).length == varLength) && (CharOperation.equals(localName, variableName)))
/* 369 */         return local;
/*     */     }
/* 371 */     return null;
/*     */   }
/*     */ 
/*     */   public Binding getBinding(char[][] compoundName, int mask, InvocationSite invocationSite, boolean needResolve)
/*     */   {
/* 406 */     Binding binding = getBinding(compoundName[0], mask | 0x4 | 0x10, invocationSite, needResolve);
/* 407 */     invocationSite.setFieldIndex(1);
/* 408 */     if ((binding instanceof VariableBinding)) return binding;
/* 409 */     CompilationUnitScope unitScope = compilationUnitScope();
/*     */ 
/* 412 */     unitScope.recordQualifiedReference(compoundName);
/* 413 */     if (!binding.isValidBinding()) return binding;
/*     */ 
/* 415 */     int length = compoundName.length;
/* 416 */     int currentIndex = 1;
/* 417 */     if ((binding instanceof PackageBinding)) {
/* 418 */       PackageBinding packageBinding = (PackageBinding)binding;
/*     */       while (true) {
/* 420 */         unitScope.recordReference(packageBinding.compoundName, compoundName[currentIndex]);
/* 421 */         binding = packageBinding.getTypeOrPackage(compoundName[(currentIndex++)]);
/* 422 */         invocationSite.setFieldIndex(currentIndex);
/* 423 */         if (binding == null) {
/* 424 */           if (currentIndex == length)
/*     */           {
/* 426 */             return new ProblemReferenceBinding(
/* 427 */               CharOperation.subarray(compoundName, 0, currentIndex), 
/* 428 */               null, 
/* 429 */               1);
/*     */           }
/* 431 */           return new ProblemBinding(
/* 432 */             CharOperation.subarray(compoundName, 0, currentIndex), 
/* 433 */             1);
/*     */         }
/* 435 */         if ((binding instanceof ReferenceBinding)) {
/* 436 */           if (!binding.isValidBinding())
/* 437 */             return new ProblemReferenceBinding(
/* 438 */               CharOperation.subarray(compoundName, 0, currentIndex), 
/* 439 */               (ReferenceBinding)((ReferenceBinding)binding).closestMatch(), 
/* 440 */               binding.problemId());
/* 441 */           if (((ReferenceBinding)binding).canBeSeenBy(this)) break;
/* 442 */           return new ProblemReferenceBinding(
/* 443 */             CharOperation.subarray(compoundName, 0, currentIndex), 
/* 444 */             (ReferenceBinding)binding, 
/* 445 */             2);
/*     */         }
/*     */         else {
/* 448 */           packageBinding = (PackageBinding)binding;
/*     */ 
/* 419 */           if (currentIndex >= length)
/*     */           {
/* 452 */             return new ProblemReferenceBinding(
/* 453 */               CharOperation.subarray(compoundName, 0, currentIndex), 
/* 454 */               null, 
/* 455 */               1);
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/* 459 */     ReferenceBinding referenceBinding = (ReferenceBinding)binding;
/* 460 */     binding = environment().convertToRawType(referenceBinding, false);
/* 461 */     if ((invocationSite instanceof ASTNode)) {
/* 462 */       ASTNode invocationNode = (ASTNode)invocationSite;
/* 463 */       if (invocationNode.isTypeUseDeprecated(referenceBinding, this)) {
/* 464 */         problemReporter().deprecatedType(referenceBinding, invocationNode);
/*     */       }
/*     */     }
/* 467 */     while (currentIndex < length) {
/* 468 */       referenceBinding = (ReferenceBinding)binding;
/* 469 */       char[] nextName = compoundName[(currentIndex++)];
/* 470 */       invocationSite.setFieldIndex(currentIndex);
/* 471 */       invocationSite.setActualReceiverType(referenceBinding);
/* 472 */       if (((mask & 0x1) != 0) && ((binding = findField(referenceBinding, nextName, invocationSite, true)) != null)) {
/* 473 */         if (binding.isValidBinding()) break;
/* 474 */         return new ProblemFieldBinding(
/* 475 */           ((ProblemFieldBinding)binding).closestMatch, 
/* 476 */           ((ProblemFieldBinding)binding).declaringClass, 
/* 477 */           CharOperation.concatWith(CharOperation.subarray(compoundName, 0, currentIndex), '.'), 
/* 478 */           binding.problemId());
/*     */       }
/*     */ 
/* 482 */       if ((binding = findMemberType(nextName, referenceBinding)) == null) {
/* 483 */         if ((mask & 0x1) != 0) {
/* 484 */           return new ProblemBinding(
/* 485 */             CharOperation.subarray(compoundName, 0, currentIndex), 
/* 486 */             referenceBinding, 
/* 487 */             1);
/*     */         }
/* 489 */         return new ProblemReferenceBinding(
/* 490 */           CharOperation.subarray(compoundName, 0, currentIndex), 
/* 491 */           referenceBinding, 
/* 492 */           1);
/*     */       }
/*     */ 
/* 495 */       if (!binding.isValidBinding())
/* 496 */         return new ProblemReferenceBinding(
/* 497 */           CharOperation.subarray(compoundName, 0, currentIndex), 
/* 498 */           (ReferenceBinding)((ReferenceBinding)binding).closestMatch(), 
/* 499 */           binding.problemId());
/* 500 */       if ((invocationSite instanceof ASTNode)) {
/* 501 */         referenceBinding = (ReferenceBinding)binding;
/* 502 */         ASTNode invocationNode = (ASTNode)invocationSite;
/* 503 */         if (invocationNode.isTypeUseDeprecated(referenceBinding, this)) {
/* 504 */           problemReporter().deprecatedType(referenceBinding, invocationNode);
/*     */         }
/*     */       }
/*     */     }
/* 508 */     if (((mask & 0x1) != 0) && ((binding instanceof FieldBinding)))
/*     */     {
/* 510 */       FieldBinding field = (FieldBinding)binding;
/* 511 */       if (!field.isStatic())
/* 512 */         return new ProblemFieldBinding(
/* 513 */           field, 
/* 514 */           field.declaringClass, 
/* 515 */           CharOperation.concatWith(CharOperation.subarray(compoundName, 0, currentIndex), '.'), 
/* 516 */           7);
/* 517 */       return binding;
/*     */     }
/* 519 */     if (((mask & 0x4) != 0) && ((binding instanceof ReferenceBinding)))
/*     */     {
/* 521 */       return binding;
/*     */     }
/*     */ 
/* 525 */     return new ProblemBinding(
/* 526 */       CharOperation.subarray(compoundName, 0, currentIndex), 
/* 527 */       1);
/*     */   }
/*     */ 
/*     */   public final Binding getBinding(char[][] compoundName, InvocationSite invocationSite)
/*     */   {
/* 532 */     int currentIndex = 0;
/* 533 */     int length = compoundName.length;
/* 534 */     Binding binding = 
/* 535 */       getBinding(
/* 536 */       compoundName[(currentIndex++)], 
/* 537 */       23, 
/* 538 */       invocationSite, 
/* 539 */       true);
/* 540 */     if (!binding.isValidBinding()) {
/* 541 */       return binding;
/*     */     }
/* 543 */     if ((binding instanceof PackageBinding))
/*     */       while (true) {
/* 545 */         PackageBinding packageBinding = (PackageBinding)binding;
/* 546 */         binding = packageBinding.getTypeOrPackage(compoundName[(currentIndex++)]);
/* 547 */         if (binding == null) {
/* 548 */           if (currentIndex == length)
/*     */           {
/* 550 */             return new ProblemReferenceBinding(
/* 551 */               CharOperation.subarray(compoundName, 0, currentIndex), 
/* 552 */               null, 
/* 553 */               1);
/*     */           }
/* 555 */           return new ProblemBinding(
/* 556 */             CharOperation.subarray(compoundName, 0, currentIndex), 
/* 557 */             1);
/*     */         }
/* 559 */         if ((binding instanceof ReferenceBinding)) {
/* 560 */           if (!binding.isValidBinding())
/* 561 */             return new ProblemReferenceBinding(
/* 562 */               CharOperation.subarray(compoundName, 0, currentIndex), 
/* 563 */               (ReferenceBinding)((ReferenceBinding)binding).closestMatch(), 
/* 564 */               binding.problemId());
/* 565 */           if (((ReferenceBinding)binding).canBeSeenBy(this)) break;
/* 566 */           return new ProblemReferenceBinding(
/* 567 */             CharOperation.subarray(compoundName, 0, currentIndex), 
/* 568 */             (ReferenceBinding)binding, 
/* 569 */             2);
/*     */         }
/* 544 */         else if (currentIndex >= length)
/*     */         {
/* 573 */           return binding;
/*     */         }
/*     */       }
/* 576 */     if ((binding instanceof ReferenceBinding))
/*     */       while (true) {
/* 578 */         ReferenceBinding typeBinding = (ReferenceBinding)binding;
/* 579 */         char[] nextName = compoundName[(currentIndex++)];
/* 580 */         TypeBinding receiverType = typeBinding.capture(this, invocationSite.sourceEnd());
/* 581 */         if ((binding = findField(receiverType, nextName, invocationSite, true)) != null) {
/* 582 */           if (!binding.isValidBinding()) {
/* 583 */             return new ProblemFieldBinding(
/* 584 */               (FieldBinding)binding, 
/* 585 */               ((FieldBinding)binding).declaringClass, 
/* 586 */               CharOperation.concatWith(CharOperation.subarray(compoundName, 0, currentIndex), '.'), 
/* 587 */               binding.problemId());
/*     */           }
/* 589 */           if (((FieldBinding)binding).isStatic()) break;
/* 590 */           return new ProblemFieldBinding(
/* 591 */             (FieldBinding)binding, 
/* 592 */             ((FieldBinding)binding).declaringClass, 
/* 593 */             CharOperation.concatWith(CharOperation.subarray(compoundName, 0, currentIndex), '.'), 
/* 594 */             7);
/*     */         }
/*     */         else {
/* 597 */           if ((binding = findMemberType(nextName, typeBinding)) == null) {
/* 598 */             return new ProblemBinding(
/* 599 */               CharOperation.subarray(compoundName, 0, currentIndex), 
/* 600 */               typeBinding, 
/* 601 */               1);
/*     */           }
/* 603 */           if (!binding.isValidBinding())
/* 604 */             return new ProblemReferenceBinding(
/* 605 */               CharOperation.subarray(compoundName, 0, currentIndex), 
/* 606 */               (ReferenceBinding)((ReferenceBinding)binding).closestMatch(), 
/* 607 */               binding.problemId());
/* 577 */           if (currentIndex >= length)
/*     */           {
/* 610 */             return binding;
/*     */           }
/*     */         }
/*     */       }
/* 613 */     VariableBinding variableBinding = (VariableBinding)binding;
/* 614 */     while (currentIndex < length) {
/* 615 */       TypeBinding typeBinding = variableBinding.type;
/* 616 */       if (typeBinding == null) {
/* 617 */         return new ProblemFieldBinding(
/* 618 */           null, 
/* 619 */           null, 
/* 620 */           CharOperation.concatWith(CharOperation.subarray(compoundName, 0, currentIndex), '.'), 
/* 621 */           1);
/*     */       }
/* 623 */       TypeBinding receiverType = typeBinding.capture(this, invocationSite.sourceEnd());
/* 624 */       variableBinding = findField(receiverType, compoundName[(currentIndex++)], invocationSite, true);
/* 625 */       if (variableBinding == null) {
/* 626 */         return new ProblemFieldBinding(
/* 627 */           null, 
/* 628 */           (receiverType instanceof ReferenceBinding) ? (ReferenceBinding)receiverType : null, 
/* 629 */           CharOperation.concatWith(CharOperation.subarray(compoundName, 0, currentIndex), '.'), 
/* 630 */           1);
/*     */       }
/* 632 */       if (!variableBinding.isValidBinding())
/* 633 */         return variableBinding;
/*     */     }
/* 635 */     return variableBinding;
/*     */   }
/*     */ 
/*     */   public VariableBinding[] getEmulationPath(LocalVariableBinding outerLocalVariable)
/*     */   {
/* 656 */     MethodScope currentMethodScope = methodScope();
/* 657 */     SourceTypeBinding sourceType = currentMethodScope.enclosingSourceType();
/*     */ 
/* 660 */     BlockScope variableScope = outerLocalVariable.declaringScope;
/* 661 */     if ((variableScope == null) || (currentMethodScope == variableScope.methodScope())) {
/* 662 */       return new VariableBinding[] { outerLocalVariable };
/*     */     }
/*     */ 
/* 666 */     if ((currentMethodScope.isInsideInitializerOrConstructor()) && 
/* 667 */       (sourceType.isNestedType()))
/*     */     {
/*     */       SyntheticArgumentBinding syntheticArg;
/* 669 */       if ((syntheticArg = ((NestedTypeBinding)sourceType).getSyntheticArgument(outerLocalVariable)) != null) {
/* 670 */         return new VariableBinding[] { syntheticArg };
/*     */       }
/*     */     }
/*     */ 
/* 674 */     if (!currentMethodScope.isStatic)
/*     */     {
/*     */       FieldBinding syntheticField;
/* 676 */       if ((syntheticField = sourceType.getSyntheticField(outerLocalVariable)) != null) {
/* 677 */         return new VariableBinding[] { syntheticField };
/*     */       }
/*     */     }
/* 680 */     return null;
/*     */   }
/*     */ 
/*     */   public Object[] getEmulationPath(ReferenceBinding targetEnclosingType, boolean onlyExactMatch, boolean denyEnclosingArgInConstructorCall)
/*     */   {
/* 695 */     MethodScope currentMethodScope = methodScope();
/* 696 */     SourceTypeBinding sourceType = currentMethodScope.enclosingSourceType();
/*     */ 
/* 699 */     if ((!currentMethodScope.isStatic) && (!currentMethodScope.isConstructorCall) && (
/* 700 */       (sourceType == targetEnclosingType) || ((!onlyExactMatch) && (sourceType.findSuperTypeOriginatingFrom(targetEnclosingType) != null)))) {
/* 701 */       return EmulationPathToImplicitThis;
/*     */     }
/*     */ 
/* 704 */     if ((!sourceType.isNestedType()) || (sourceType.isStatic())) {
/* 705 */       if (currentMethodScope.isConstructorCall)
/* 706 */         return NoEnclosingInstanceInConstructorCall;
/* 707 */       if (currentMethodScope.isStatic) {
/* 708 */         return NoEnclosingInstanceInStaticContext;
/*     */       }
/* 710 */       return null;
/*     */     }
/* 712 */     boolean insideConstructor = currentMethodScope.isInsideInitializerOrConstructor();
/*     */ 
/* 714 */     if (insideConstructor)
/*     */     {
/*     */       SyntheticArgumentBinding syntheticArg;
/* 716 */       if ((syntheticArg = ((NestedTypeBinding)sourceType).getSyntheticArgument(targetEnclosingType, onlyExactMatch)) != null)
/*     */       {
/* 718 */         if ((denyEnclosingArgInConstructorCall) && 
/* 719 */           (currentMethodScope.isConstructorCall) && (
/* 720 */           (sourceType == targetEnclosingType) || ((!onlyExactMatch) && (sourceType.findSuperTypeOriginatingFrom(targetEnclosingType) != null)))) {
/* 721 */           return NoEnclosingInstanceInConstructorCall;
/*     */         }
/* 723 */         return new Object[] { syntheticArg };
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 728 */     if (currentMethodScope.isStatic) {
/* 729 */       return NoEnclosingInstanceInStaticContext;
/*     */     }
/* 731 */     if (sourceType.isAnonymousType()) {
/* 732 */       ReferenceBinding enclosingType = sourceType.enclosingType();
/* 733 */       if (enclosingType.isNestedType()) {
/* 734 */         NestedTypeBinding nestedEnclosingType = (NestedTypeBinding)enclosingType;
/* 735 */         SyntheticArgumentBinding enclosingArgument = nestedEnclosingType.getSyntheticArgument(nestedEnclosingType.enclosingType(), onlyExactMatch);
/* 736 */         if (enclosingArgument != null) {
/* 737 */           FieldBinding syntheticField = sourceType.getSyntheticField(enclosingArgument);
/* 738 */           if ((syntheticField != null) && (
/* 739 */             (syntheticField.type == targetEnclosingType) || ((!onlyExactMatch) && (((ReferenceBinding)syntheticField.type).findSuperTypeOriginatingFrom(targetEnclosingType) != null)))) {
/* 740 */             return new Object[] { syntheticField };
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/* 745 */     FieldBinding syntheticField = sourceType.getSyntheticField(targetEnclosingType, onlyExactMatch);
/* 746 */     if (syntheticField != null) {
/* 747 */       if (currentMethodScope.isConstructorCall) {
/* 748 */         return NoEnclosingInstanceInConstructorCall;
/*     */       }
/* 750 */       return new Object[] { syntheticField };
/*     */     }
/*     */ 
/* 754 */     Object[] path = new Object[2];
/* 755 */     ReferenceBinding currentType = sourceType.enclosingType();
/* 756 */     if (insideConstructor) {
/* 757 */       path[0] = ((NestedTypeBinding)sourceType).getSyntheticArgument(currentType, onlyExactMatch);
/*     */     } else {
/* 759 */       if (currentMethodScope.isConstructorCall) {
/* 760 */         return NoEnclosingInstanceInConstructorCall;
/*     */       }
/* 762 */       path[0] = sourceType.getSyntheticField(currentType, onlyExactMatch);
/*     */     }
/* 764 */     if (path[0] != null)
/*     */     {
/* 766 */       int count = 1;
/*     */       ReferenceBinding currentEnclosingType;
/* 768 */       while ((currentEnclosingType = currentType.enclosingType()) != null)
/*     */       {
/*     */         ReferenceBinding currentEnclosingType;
/* 771 */         if ((currentType == targetEnclosingType) || (
/* 772 */           (!onlyExactMatch) && (currentType.findSuperTypeOriginatingFrom(targetEnclosingType) != null)))
/*     */           break;
/* 774 */         if (currentMethodScope != null) {
/* 775 */           currentMethodScope = currentMethodScope.enclosingMethodScope();
/* 776 */           if ((currentMethodScope != null) && (currentMethodScope.isConstructorCall)) {
/* 777 */             return NoEnclosingInstanceInConstructorCall;
/*     */           }
/* 779 */           if ((currentMethodScope != null) && (currentMethodScope.isStatic)) {
/* 780 */             return NoEnclosingInstanceInStaticContext;
/*     */           }
/*     */         }
/*     */ 
/* 784 */         syntheticField = ((NestedTypeBinding)currentType).getSyntheticField(currentEnclosingType, onlyExactMatch);
/* 785 */         if (syntheticField == null) {
/*     */           break;
/*     */         }
/* 788 */         if (count == path.length) {
/* 789 */           System.arraycopy(path, 0, path = new Object[count + 1], 0, count);
/*     */         }
/*     */ 
/* 792 */         path[(count++)] = ((SourceTypeBinding)syntheticField.declaringClass).addSyntheticMethod(syntheticField, true, false);
/* 793 */         currentType = currentEnclosingType;
/*     */       }
/* 795 */       if ((currentType == targetEnclosingType) || (
/* 796 */         (!onlyExactMatch) && (currentType.findSuperTypeOriginatingFrom(targetEnclosingType) != null))) {
/* 797 */         return path;
/*     */       }
/*     */     }
/* 800 */     return null;
/*     */   }
/*     */ 
/*     */   public final boolean isDuplicateLocalVariable(char[] name)
/*     */   {
/* 806 */     BlockScope current = this;
/*     */     while (true) {
/* 808 */       for (int i = 0; i < this.localIndex; i++) {
/* 809 */         if (CharOperation.equals(name, current.locals[i].name))
/* 810 */           return true;
/*     */       }
/* 812 */       if (current.kind != 1) return false;
/* 813 */       current = (BlockScope)current.parent;
/*     */     }
/*     */   }
/*     */ 
/*     */   public int maxShiftedOffset() {
/* 818 */     int max = -1;
/* 819 */     if (this.shiftScopes != null) {
/* 820 */       int i = 0; for (int length = this.shiftScopes.length; i < length; i++) {
/* 821 */         int subMaxOffset = this.shiftScopes[i].maxOffset;
/* 822 */         if (subMaxOffset <= max) continue; max = subMaxOffset;
/*     */       }
/*     */     }
/* 825 */     return max;
/*     */   }
/*     */ 
/*     */   public final boolean needBlankFinalFieldInitializationCheck(FieldBinding binding)
/*     */   {
/* 833 */     boolean isStatic = binding.isStatic();
/* 834 */     ReferenceBinding fieldDeclaringClass = binding.declaringClass;
/*     */ 
/* 836 */     MethodScope methodScope = methodScope();
/* 837 */     while (methodScope != null) {
/* 838 */       if (methodScope.isStatic != isStatic)
/* 839 */         return false;
/* 840 */       if ((!methodScope.isInsideInitializer()) && 
/* 841 */         (!((AbstractMethodDeclaration)methodScope.referenceContext).isInitializationMethod())) {
/* 842 */         return false;
/*     */       }
/* 844 */       ReferenceBinding enclosingType = methodScope.enclosingReceiverType();
/* 845 */       if (enclosingType == fieldDeclaringClass) {
/* 846 */         return true;
/*     */       }
/* 848 */       if (!enclosingType.erasure().isAnonymousType()) {
/* 849 */         return false;
/*     */       }
/* 851 */       methodScope = methodScope.enclosingMethodScope();
/*     */     }
/* 853 */     return false;
/*     */   }
/*     */ 
/*     */   public ProblemReporter problemReporter()
/*     */   {
/* 863 */     return outerMostMethodScope().problemReporter();
/*     */   }
/*     */ 
/*     */   public void propagateInnerEmulation(ReferenceBinding targetType, boolean isEnclosingInstanceSupplied)
/*     */   {
/*     */     SyntheticArgumentBinding[] syntheticArguments;
/* 874 */     if ((syntheticArguments = targetType.syntheticOuterLocalVariables()) != null) {
/* 875 */       int i = 0; for (int max = syntheticArguments.length; i < max; i++) {
/* 876 */         SyntheticArgumentBinding syntheticArg = syntheticArguments[i];
/*     */ 
/* 878 */         if ((isEnclosingInstanceSupplied) && 
/* 879 */           (syntheticArg.type == targetType.enclosingType())) continue;
/* 880 */         emulateOuterAccess(syntheticArg.actualOuterLocalVariable);
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public TypeDeclaration referenceType()
/*     */   {
/* 891 */     return methodScope().referenceType();
/*     */   }
/*     */ 
/*     */   public int scopeIndex()
/*     */   {
/* 899 */     if ((this instanceof MethodScope)) return -1;
/* 900 */     BlockScope parentScope = (BlockScope)this.parent;
/* 901 */     Scope[] parentSubscopes = parentScope.subscopes;
/* 902 */     int i = 0; for (int max = parentScope.subscopeCount; i < max; i++) {
/* 903 */       if (parentSubscopes[i] == this) return i;
/*     */     }
/* 905 */     return -1;
/*     */   }
/*     */ 
/*     */   int startIndex()
/*     */   {
/* 910 */     return this.startIndex;
/*     */   }
/*     */ 
/*     */   public String toString() {
/* 914 */     return toString(0);
/*     */   }
/*     */ 
/*     */   public String toString(int tab) {
/* 918 */     String s = basicToString(tab);
/* 919 */     for (int i = 0; i < this.subscopeCount; i++)
/* 920 */       if ((this.subscopes[i] instanceof BlockScope))
/* 921 */         s = s + ((BlockScope)this.subscopes[i]).toString(tab + 1) + "\n";
/* 922 */     return s;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\ecj-3.5.2.jar
 * Qualified Name:     org.eclipse.jdt.internal.compiler.lookup.BlockScope
 * JD-Core Version:    0.6.0
 */