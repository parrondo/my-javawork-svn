/*     */ package org.eclipse.jdt.internal.compiler.ast;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ import org.eclipse.jdt.core.compiler.CategorizedProblem;
/*     */ import org.eclipse.jdt.core.compiler.CharOperation;
/*     */ import org.eclipse.jdt.internal.compiler.ASTVisitor;
/*     */ import org.eclipse.jdt.internal.compiler.ClassFile;
/*     */ import org.eclipse.jdt.internal.compiler.CompilationResult;
/*     */ import org.eclipse.jdt.internal.compiler.codegen.CodeStream;
/*     */ import org.eclipse.jdt.internal.compiler.codegen.StackMapFrameCodeStream;
/*     */ import org.eclipse.jdt.internal.compiler.flow.ExceptionHandlingFlowContext;
/*     */ import org.eclipse.jdt.internal.compiler.flow.FlowInfo;
/*     */ import org.eclipse.jdt.internal.compiler.flow.InitializationFlowContext;
/*     */ import org.eclipse.jdt.internal.compiler.impl.CompilerOptions;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.Binding;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.ClassScope;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.FieldBinding;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.LocalVariableBinding;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.MethodBinding;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.MethodScope;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.NestedTypeBinding;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.ReferenceBinding;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.SourceTypeBinding;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.SyntheticArgumentBinding;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;
/*     */ import org.eclipse.jdt.internal.compiler.parser.Parser;
/*     */ import org.eclipse.jdt.internal.compiler.problem.AbortMethod;
/*     */ import org.eclipse.jdt.internal.compiler.problem.ProblemReporter;
/*     */ import org.eclipse.jdt.internal.compiler.util.Util;
/*     */ 
/*     */ public class ConstructorDeclaration extends AbstractMethodDeclaration
/*     */ {
/*     */   public ExplicitConstructorCall constructorCall;
/*     */   public TypeParameter[] typeParameters;
/*     */ 
/*     */   public ConstructorDeclaration(CompilationResult compilationResult)
/*     */   {
/*  32 */     super(compilationResult);
/*     */   }
/*     */ 
/*     */   /** @deprecated */
/*     */   public void analyseCode(ClassScope classScope, InitializationFlowContext initializerFlowContext, FlowInfo flowInfo)
/*     */   {
/*  40 */     analyseCode(classScope, initializerFlowContext, flowInfo, 0);
/*     */   }
/*     */ 
/*     */   public void analyseCode(ClassScope classScope, InitializationFlowContext initializerFlowContext, FlowInfo flowInfo, int initialReachMode)
/*     */   {
/*  48 */     if (this.ignoreFurtherInvestigation) {
/*  49 */       return;
/*     */     }
/*  51 */     int nonStaticFieldInfoReachMode = flowInfo.reachMode();
/*  52 */     flowInfo.setReachMode(initialReachMode);
/*     */     MethodBinding constructorBinding;
/*  56 */     if (((constructorBinding = this.binding) != null) && 
/*  57 */       ((this.bits & 0x80) == 0) && 
/*  58 */       (!constructorBinding.isUsed()) && 
/*  59 */       (constructorBinding.isPrivate() ? 
/*  60 */       (this.binding.declaringClass.tagBits & 0x0) != 0L : 
/*  62 */       constructorBinding.isOrEnclosedByPrivateType()))
/*     */     {
/*  67 */       if (this.constructorCall != null)
/*     */       {
/*  74 */         if (this.constructorCall.accessMode != 3) {
/*  75 */           ReferenceBinding superClass = constructorBinding.declaringClass.superclass();
/*  76 */           if (superClass != null)
/*     */           {
/*  79 */             MethodBinding methodBinding = superClass.getExactConstructor(Binding.NO_PARAMETERS);
/*  80 */             if (methodBinding != null) {
/*  82 */               if (!methodBinding.canBeSeenBy(SuperReference.implicitSuperConstructorCall(), this.scope));
/*     */             }
/*     */           }
/*     */         }
/*     */         else {
/*  87 */           this.scope.problemReporter().unusedPrivateConstructor(this);
/*     */         }
/*     */       }
/*     */     }
/*  91 */     if (isRecursive(null)) {
/*  92 */       this.scope.problemReporter().recursiveConstructorInvocation(this.constructorCall);
/*     */     }
/*     */     try
/*     */     {
/*  96 */       ExceptionHandlingFlowContext constructorContext = 
/*  97 */         new ExceptionHandlingFlowContext(
/*  98 */         initializerFlowContext.parent, 
/*  99 */         this, 
/* 100 */         this.binding.thrownExceptions, 
/* 101 */         initializerFlowContext, 
/* 102 */         this.scope, 
/* 103 */         FlowInfo.DEAD_END);
/* 104 */       initializerFlowContext.checkInitializerExceptions(
/* 105 */         this.scope, 
/* 106 */         constructorContext, 
/* 107 */         flowInfo);
/*     */ 
/* 110 */       if (this.binding.declaringClass.isAnonymousType()) {
/* 111 */         ArrayList computedExceptions = constructorContext.extendedExceptions;
/* 112 */         if (computedExceptions != null)
/*     */         {
/*     */           int size;
/* 114 */           if ((size = computedExceptions.size()) > 0)
/*     */           {
/*     */             ReferenceBinding[] actuallyThrownExceptions;
/* 116 */             computedExceptions.toArray(actuallyThrownExceptions = new ReferenceBinding[size]);
/* 117 */             this.binding.thrownExceptions = actuallyThrownExceptions;
/*     */           }
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/* 123 */       if (this.arguments != null) {
/* 124 */         int i = 0; for (int count = this.arguments.length; i < count; i++) {
/* 125 */           flowInfo.markAsDefinitelyAssigned(this.arguments[i].binding);
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/* 130 */       if (this.constructorCall != null)
/*     */       {
/* 133 */         if (this.constructorCall.accessMode == 3) {
/* 134 */           FieldBinding[] fields = this.binding.declaringClass.fields();
/* 135 */           int i = 0; for (int count = fields.length; i < count; i++)
/*     */           {
/*     */             FieldBinding field;
/* 137 */             if (!(field = fields[i]).isStatic()) {
/* 138 */               flowInfo.markAsDefinitelyAssigned(field);
/*     */             }
/*     */           }
/*     */         }
/* 142 */         flowInfo = this.constructorCall.analyseCode(this.scope, constructorContext, flowInfo);
/*     */       }
/*     */ 
/* 146 */       flowInfo.setReachMode(nonStaticFieldInfoReachMode);
/*     */ 
/* 149 */       if (this.statements != null) {
/* 150 */         int complaintLevel = (nonStaticFieldInfoReachMode & 0x1) == 0 ? 0 : 1;
/* 151 */         int i = 0; for (int count = this.statements.length; i < count; i++) {
/* 152 */           Statement stat = this.statements[i];
/* 153 */           if ((complaintLevel = stat.complainIfUnreachable(flowInfo, this.scope, complaintLevel)) < 2) {
/* 154 */             flowInfo = stat.analyseCode(this.scope, constructorContext, flowInfo);
/*     */           }
/*     */         }
/*     */       }
/*     */ 
/* 159 */       if ((flowInfo.tagBits & 0x1) == 0) {
/* 160 */         this.bits |= 64;
/*     */       }
/*     */ 
/* 164 */       flowInfo.setReachMode(initialReachMode);
/*     */ 
/* 167 */       if ((this.constructorCall != null) && 
/* 168 */         (this.constructorCall.accessMode != 3)) {
/* 169 */         flowInfo = flowInfo.mergedWith(constructorContext.initsOnReturn);
/* 170 */         FieldBinding[] fields = this.binding.declaringClass.fields();
/* 171 */         int i = 0; for (int count = fields.length; i < count; i++)
/*     */         {
/*     */           FieldBinding field;
/* 173 */           if (((field = fields[i]).isStatic()) || 
/* 174 */             (!field.isFinal()) || 
/* 175 */             (flowInfo.isDefinitelyAssigned(fields[i]))) continue;
/* 176 */           this.scope.problemReporter().uninitializedBlankFinalField(
/* 177 */             field, 
/* 178 */             (this.bits & 0x80) != 0 ? this.scope.referenceType() : this);
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/* 183 */       constructorContext.complainIfUnusedExceptionHandlers(this);
/*     */ 
/* 185 */       this.scope.checkUnusedParameters(this.binding);
/*     */     } catch (AbortMethod localAbortMethod) {
/* 187 */       this.ignoreFurtherInvestigation = true;
/*     */     }
/*     */   }
/*     */ 
/*     */   public void generateCode(ClassScope classScope, ClassFile classFile)
/*     */   {
/* 198 */     int problemResetPC = 0;
/* 199 */     if (this.ignoreFurtherInvestigation) {
/* 200 */       if (this.binding == null) {
/* 201 */         return;
/*     */       }
/* 203 */       CategorizedProblem[] problems = 
/* 204 */         this.scope.referenceCompilationUnit().compilationResult.getProblems();
/*     */       int problemsLength;
/* 205 */       CategorizedProblem[] problemsCopy = new CategorizedProblem[problemsLength = problems.length];
/* 206 */       System.arraycopy(problems, 0, problemsCopy, 0, problemsLength);
/* 207 */       classFile.addProblemConstructor(this, this.binding, problemsCopy);
/* 208 */       return;
/*     */     }
/*     */     try {
/* 211 */       problemResetPC = classFile.contentsOffset;
/* 212 */       internalGenerateCode(classScope, classFile);
/*     */     } catch (AbortMethod e) {
/* 214 */       if (e.compilationResult == CodeStream.RESTART_IN_WIDE_MODE)
/*     */       {
/*     */         try {
/* 217 */           classFile.contentsOffset = problemResetPC;
/* 218 */           classFile.methodCount -= 1;
/* 219 */           classFile.codeStream.resetInWideMode();
/* 220 */           internalGenerateCode(classScope, classFile);
/*     */         }
/*     */         catch (AbortMethod localAbortMethod1) {
/* 223 */           CategorizedProblem[] problems = 
/* 224 */             this.scope.referenceCompilationUnit().compilationResult.getAllProblems();
/*     */           int problemsLength;
/* 225 */           CategorizedProblem[] problemsCopy = new CategorizedProblem[problemsLength = problems.length];
/* 226 */           System.arraycopy(problems, 0, problemsCopy, 0, problemsLength);
/* 227 */           classFile.addProblemConstructor(this, this.binding, problemsCopy, problemResetPC);
/*     */         }
/*     */       }
/*     */       else {
/* 231 */         CategorizedProblem[] problems = 
/* 232 */           this.scope.referenceCompilationUnit().compilationResult.getAllProblems();
/*     */         int problemsLength;
/* 233 */         CategorizedProblem[] problemsCopy = new CategorizedProblem[problemsLength = problems.length];
/* 234 */         System.arraycopy(problems, 0, problemsCopy, 0, problemsLength);
/* 235 */         classFile.addProblemConstructor(this, this.binding, problemsCopy, problemResetPC);
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public void generateSyntheticFieldInitializationsIfNecessary(MethodScope methodScope, CodeStream codeStream, ReferenceBinding declaringClass) {
/* 241 */     if (!declaringClass.isNestedType()) return;
/*     */ 
/* 243 */     NestedTypeBinding nestedType = (NestedTypeBinding)declaringClass;
/*     */ 
/* 245 */     SyntheticArgumentBinding[] syntheticArgs = nestedType.syntheticEnclosingInstances();
/* 246 */     int i = 0; for (int max = syntheticArgs == null ? 0 : syntheticArgs.length; i < max; i++)
/*     */     {
/*     */       SyntheticArgumentBinding syntheticArg;
/* 248 */       if ((syntheticArg = syntheticArgs[i]).matchingField != null) {
/* 249 */         codeStream.aload_0();
/* 250 */         codeStream.load(syntheticArg);
/* 251 */         codeStream.fieldAccess(-75, syntheticArg.matchingField, null);
/*     */       }
/*     */     }
/* 254 */     syntheticArgs = nestedType.syntheticOuterLocalVariables();
/* 255 */     int i = 0; for (int max = syntheticArgs == null ? 0 : syntheticArgs.length; i < max; i++)
/*     */     {
/*     */       SyntheticArgumentBinding syntheticArg;
/* 257 */       if ((syntheticArg = syntheticArgs[i]).matchingField != null) {
/* 258 */         codeStream.aload_0();
/* 259 */         codeStream.load(syntheticArg);
/* 260 */         codeStream.fieldAccess(-75, syntheticArg.matchingField, null);
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   private void internalGenerateCode(ClassScope classScope, ClassFile classFile) {
/* 266 */     classFile.generateMethodInfoHeader(this.binding);
/* 267 */     int methodAttributeOffset = classFile.contentsOffset;
/* 268 */     int attributeNumber = classFile.generateMethodInfoAttribute(this.binding);
/* 269 */     if ((!this.binding.isNative()) && (!this.binding.isAbstract()))
/*     */     {
/* 271 */       TypeDeclaration declaringType = classScope.referenceContext;
/* 272 */       int codeAttributeOffset = classFile.contentsOffset;
/* 273 */       classFile.generateCodeAttributeHeader();
/* 274 */       CodeStream codeStream = classFile.codeStream;
/* 275 */       codeStream.reset(this, classFile);
/*     */ 
/* 278 */       ReferenceBinding declaringClass = this.binding.declaringClass;
/*     */ 
/* 280 */       int enumOffset = declaringClass.isEnum() ? 2 : 0;
/* 281 */       int argSlotSize = 1 + enumOffset;
/*     */ 
/* 283 */       if (declaringClass.isNestedType()) {
/* 284 */         this.scope.extraSyntheticArguments = declaringClass.syntheticOuterLocalVariables();
/* 285 */         this.scope.computeLocalVariablePositions(
/* 286 */           declaringClass.getEnclosingInstancesSlotSize() + 1 + enumOffset, 
/* 287 */           codeStream);
/* 288 */         argSlotSize += declaringClass.getEnclosingInstancesSlotSize();
/* 289 */         argSlotSize += declaringClass.getOuterLocalVariablesSlotSize();
/*     */       } else {
/* 291 */         this.scope.computeLocalVariablePositions(1 + enumOffset, codeStream);
/*     */       }
/*     */ 
/* 294 */       if (this.arguments != null) {
/* 295 */         int i = 0; for (int max = this.arguments.length; i < max; i++)
/*     */         {
/*     */           LocalVariableBinding argBinding;
/* 298 */           codeStream.addVisibleLocalVariable(argBinding = this.arguments[i].binding);
/* 299 */           argBinding.recordInitializationStartPC(0);
/* 300 */           switch (argBinding.type.id) {
/*     */           case 7:
/*     */           case 8:
/* 303 */             argSlotSize += 2;
/* 304 */             break;
/*     */           default:
/* 306 */             argSlotSize++;
/*     */           }
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/* 312 */       MethodScope initializerScope = declaringType.initializerScope;
/* 313 */       initializerScope.computeLocalVariablePositions(argSlotSize, codeStream);
/*     */ 
/* 315 */       boolean needFieldInitializations = (this.constructorCall == null) || (this.constructorCall.accessMode != 3);
/*     */ 
/* 318 */       boolean preInitSyntheticFields = this.scope.compilerOptions().targetJDK >= 3145728L;
/*     */ 
/* 320 */       if ((needFieldInitializations) && (preInitSyntheticFields)) {
/* 321 */         generateSyntheticFieldInitializationsIfNecessary(this.scope, codeStream, declaringClass);
/*     */       }
/*     */ 
/* 324 */       if (this.constructorCall != null) {
/* 325 */         this.constructorCall.generateCode(this.scope, codeStream);
/*     */       }
/*     */ 
/* 328 */       if (needFieldInitializations) {
/* 329 */         if (!preInitSyntheticFields) {
/* 330 */           generateSyntheticFieldInitializationsIfNecessary(this.scope, codeStream, declaringClass);
/*     */         }
/*     */ 
/* 333 */         if (declaringType.fields != null) {
/* 334 */           int i = 0; for (int max = declaringType.fields.length; i < max; i++)
/*     */           {
/*     */             FieldDeclaration fieldDecl;
/* 336 */             if (!(fieldDecl = declaringType.fields[i]).isStatic()) {
/* 337 */               fieldDecl.generateCode(initializerScope, codeStream);
/*     */             }
/*     */           }
/*     */         }
/*     */       }
/*     */ 
/* 343 */       if (this.statements != null) {
/* 344 */         int i = 0; for (int max = this.statements.length; i < max; i++) {
/* 345 */           this.statements[i].generateCode(this.scope, codeStream);
/*     */         }
/*     */       }
/*     */ 
/* 349 */       if (this.ignoreFurtherInvestigation) {
/* 350 */         throw new AbortMethod(this.scope.referenceCompilationUnit().compilationResult, null);
/*     */       }
/* 352 */       if ((this.bits & 0x40) != 0) {
/* 353 */         codeStream.return_();
/*     */       }
/*     */ 
/* 356 */       codeStream.exitUserScope(this.scope);
/* 357 */       codeStream.recordPositionsFrom(0, this.bodyEnd);
/* 358 */       classFile.completeCodeAttribute(codeAttributeOffset);
/* 359 */       attributeNumber++;
/* 360 */       if (((codeStream instanceof StackMapFrameCodeStream)) && 
/* 361 */         (needFieldInitializations) && 
/* 362 */         (declaringType.fields != null)) {
/* 363 */         ((StackMapFrameCodeStream)codeStream).resetSecretLocals();
/*     */       }
/*     */     }
/* 366 */     classFile.completeMethodInfo(methodAttributeOffset, attributeNumber);
/*     */   }
/*     */ 
/*     */   public boolean isConstructor() {
/* 370 */     return true;
/*     */   }
/*     */ 
/*     */   public boolean isDefaultConstructor() {
/* 374 */     return (this.bits & 0x80) != 0;
/*     */   }
/*     */ 
/*     */   public boolean isInitializationMethod() {
/* 378 */     return true;
/*     */   }
/*     */ 
/*     */   public boolean isRecursive(ArrayList visited)
/*     */   {
/* 387 */     if ((this.binding == null) || 
/* 388 */       (this.constructorCall == null) || 
/* 389 */       (this.constructorCall.binding == null) || 
/* 390 */       (this.constructorCall.isSuperAccess()) || 
/* 391 */       (!this.constructorCall.binding.isValidBinding())) {
/* 392 */       return false;
/*     */     }
/*     */ 
/* 395 */     ConstructorDeclaration targetConstructor = 
/* 396 */       (ConstructorDeclaration)this.scope.referenceType().declarationOf(this.constructorCall.binding.original());
/* 397 */     if (this == targetConstructor) return true;
/*     */ 
/* 399 */     if (visited == null) {
/* 400 */       visited = new ArrayList(1);
/*     */     } else {
/* 402 */       int index = visited.indexOf(this);
/* 403 */       if (index >= 0) return index == 0;
/*     */     }
/* 405 */     visited.add(this);
/*     */ 
/* 407 */     return targetConstructor.isRecursive(visited);
/*     */   }
/*     */ 
/*     */   public void parseStatements(Parser parser, CompilationUnitDeclaration unit)
/*     */   {
/* 412 */     if (((this.bits & 0x80) != 0) && (this.constructorCall == null)) {
/* 413 */       this.constructorCall = SuperReference.implicitSuperConstructorCall();
/* 414 */       this.constructorCall.sourceStart = this.sourceStart;
/* 415 */       this.constructorCall.sourceEnd = this.sourceEnd;
/* 416 */       return;
/*     */     }
/* 418 */     parser.parse(this, unit, false);
/*     */   }
/*     */ 
/*     */   public StringBuffer printBody(int indent, StringBuffer output)
/*     */   {
/* 423 */     output.append(" {");
/* 424 */     if (this.constructorCall != null) {
/* 425 */       output.append('\n');
/* 426 */       this.constructorCall.printStatement(indent, output);
/*     */     }
/* 428 */     if (this.statements != null) {
/* 429 */       for (int i = 0; i < this.statements.length; i++) {
/* 430 */         output.append('\n');
/* 431 */         this.statements[i].printStatement(indent, output);
/*     */       }
/*     */     }
/* 434 */     output.append('\n');
/* 435 */     printIndent(indent == 0 ? 0 : indent - 1, output).append('}');
/* 436 */     return output;
/*     */   }
/*     */ 
/*     */   public void resolveJavadoc() {
/* 440 */     if ((this.binding == null) || (this.javadoc != null)) {
/* 441 */       super.resolveJavadoc();
/* 442 */     } else if (((this.bits & 0x80) == 0) && 
/* 443 */       (this.binding.declaringClass != null) && (!this.binding.declaringClass.isLocalType()))
/*     */     {
/* 445 */       int javadocVisibility = this.binding.modifiers & 0x7;
/* 446 */       ClassScope classScope = this.scope.classScope();
/* 447 */       ProblemReporter reporter = this.scope.problemReporter();
/* 448 */       int severity = reporter.computeSeverity(-1610612250);
/* 449 */       if (severity != -1) {
/* 450 */         if (classScope != null) {
/* 451 */           javadocVisibility = Util.computeOuterMostVisibility(classScope.referenceType(), javadocVisibility);
/*     */         }
/* 453 */         int javadocModifiers = this.binding.modifiers & 0xFFFFFFF8 | javadocVisibility;
/* 454 */         reporter.javadocMissing(this.sourceStart, this.sourceEnd, severity, javadocModifiers);
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public void resolveStatements()
/*     */   {
/* 465 */     SourceTypeBinding sourceType = this.scope.enclosingSourceType();
/* 466 */     if (!CharOperation.equals(sourceType.sourceName, this.selector)) {
/* 467 */       this.scope.problemReporter().missingReturnType(this);
/*     */     }
/* 469 */     if (this.typeParameters != null) {
/* 470 */       int i = 0; for (int length = this.typeParameters.length; i < length; i++) {
/* 471 */         this.typeParameters[i].resolve(this.scope);
/*     */       }
/*     */     }
/* 474 */     if ((this.binding != null) && (!this.binding.isPrivate())) {
/* 475 */       sourceType.tagBits |= 4503599627370496L;
/*     */     }
/*     */ 
/* 478 */     if (this.constructorCall != null) {
/* 479 */       if ((sourceType.id == 1) && 
/* 480 */         (this.constructorCall.accessMode != 3))
/*     */       {
/* 482 */         if (this.constructorCall.accessMode == 2) {
/* 483 */           this.scope.problemReporter().cannotUseSuperInJavaLangObject(this.constructorCall);
/*     */         }
/* 485 */         this.constructorCall = null;
/*     */       } else {
/* 487 */         this.constructorCall.resolve(this.scope);
/*     */       }
/*     */     }
/* 490 */     if ((this.modifiers & 0x1000000) != 0) {
/* 491 */       this.scope.problemReporter().methodNeedBody(this);
/*     */     }
/* 493 */     super.resolveStatements();
/*     */   }
/*     */ 
/*     */   public void traverse(ASTVisitor visitor, ClassScope classScope) {
/* 497 */     if (visitor.visit(this, classScope)) {
/* 498 */       if (this.javadoc != null) {
/* 499 */         this.javadoc.traverse(visitor, this.scope);
/*     */       }
/* 501 */       if (this.annotations != null) {
/* 502 */         int annotationsLength = this.annotations.length;
/* 503 */         for (int i = 0; i < annotationsLength; i++)
/* 504 */           this.annotations[i].traverse(visitor, this.scope);
/*     */       }
/* 506 */       if (this.typeParameters != null) {
/* 507 */         int typeParametersLength = this.typeParameters.length;
/* 508 */         for (int i = 0; i < typeParametersLength; i++) {
/* 509 */           this.typeParameters[i].traverse(visitor, this.scope);
/*     */         }
/*     */       }
/* 512 */       if (this.arguments != null) {
/* 513 */         int argumentLength = this.arguments.length;
/* 514 */         for (int i = 0; i < argumentLength; i++)
/* 515 */           this.arguments[i].traverse(visitor, this.scope);
/*     */       }
/* 517 */       if (this.thrownExceptions != null) {
/* 518 */         int thrownExceptionsLength = this.thrownExceptions.length;
/* 519 */         for (int i = 0; i < thrownExceptionsLength; i++)
/* 520 */           this.thrownExceptions[i].traverse(visitor, this.scope);
/*     */       }
/* 522 */       if (this.constructorCall != null)
/* 523 */         this.constructorCall.traverse(visitor, this.scope);
/* 524 */       if (this.statements != null) {
/* 525 */         int statementsLength = this.statements.length;
/* 526 */         for (int i = 0; i < statementsLength; i++)
/* 527 */           this.statements[i].traverse(visitor, this.scope);
/*     */       }
/*     */     }
/* 530 */     visitor.endVisit(this, classScope);
/*     */   }
/*     */   public TypeParameter[] typeParameters() {
/* 533 */     return this.typeParameters;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\ecj-3.5.2.jar
 * Qualified Name:     org.eclipse.jdt.internal.compiler.ast.ConstructorDeclaration
 * JD-Core Version:    0.6.0
 */