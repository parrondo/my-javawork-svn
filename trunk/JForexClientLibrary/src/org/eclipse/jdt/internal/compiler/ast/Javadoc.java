/*     */ package org.eclipse.jdt.internal.compiler.ast;
/*     */ 
/*     */ import org.eclipse.jdt.core.compiler.CharOperation;
/*     */ import org.eclipse.jdt.internal.compiler.ASTVisitor;
/*     */ import org.eclipse.jdt.internal.compiler.CompilationResult;
/*     */ import org.eclipse.jdt.internal.compiler.impl.CompilerOptions;
/*     */ import org.eclipse.jdt.internal.compiler.impl.ReferenceContext;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.Binding;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.BlockScope;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.ClassScope;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.CompilationUnitScope;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.FieldBinding;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.ImportBinding;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.LocalVariableBinding;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.LookupEnvironment;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.MethodBinding;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.MethodScope;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.MethodVerifier;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.PackageBinding;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.ReferenceBinding;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.Scope;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.SourceTypeBinding;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.TypeVariableBinding;
/*     */ import org.eclipse.jdt.internal.compiler.problem.ProblemReporter;
/*     */ 
/*     */ public class Javadoc extends ASTNode
/*     */ {
/*     */   public JavadocSingleNameReference[] paramReferences;
/*     */   public JavadocSingleTypeReference[] paramTypeParameters;
/*     */   public TypeReference[] exceptionReferences;
/*     */   public JavadocReturnStatement returnStatement;
/*     */   public Expression[] seeReferences;
/*  30 */   public long[] inheritedPositions = null;
/*     */   public JavadocSingleNameReference[] invalidParameters;
/*  36 */   public long valuePositions = -1L;
/*     */ 
/*     */   public Javadoc(int sourceStart, int sourceEnd) {
/*  39 */     this.sourceStart = sourceStart;
/*  40 */     this.sourceEnd = sourceEnd;
/*     */   }
/*     */ 
/*     */   boolean canBeSeen(int visibility, int modifiers)
/*     */   {
/*  50 */     if (modifiers < 0) return true;
/*  51 */     switch (modifiers & 0x7) {
/*     */     case 1:
/*  53 */       return true;
/*     */     case 4:
/*  55 */       return visibility != 1;
/*     */     case 0:
/*  57 */       return (visibility == 0) || (visibility == 2);
/*     */     case 2:
/*  59 */       return visibility == 2;
/*     */     case 3:
/*  61 */     }return true;
/*     */   }
/*     */ 
/*     */   public ASTNode getNodeStartingAt(int start)
/*     */   {
/*  68 */     int length = 0;
/*     */ 
/*  70 */     if (this.paramReferences != null) {
/*  71 */       length = this.paramReferences.length;
/*  72 */       for (int i = 0; i < length; i++) {
/*  73 */         JavadocSingleNameReference param = this.paramReferences[i];
/*  74 */         if (param.sourceStart == start) {
/*  75 */           return param;
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/*  80 */     if (this.invalidParameters != null) {
/*  81 */       length = this.invalidParameters.length;
/*  82 */       for (int i = 0; i < length; i++) {
/*  83 */         JavadocSingleNameReference param = this.invalidParameters[i];
/*  84 */         if (param.sourceStart == start) {
/*  85 */           return param;
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/*  90 */     if (this.paramTypeParameters != null) {
/*  91 */       length = this.paramTypeParameters.length;
/*  92 */       for (int i = 0; i < length; i++) {
/*  93 */         JavadocSingleTypeReference param = this.paramTypeParameters[i];
/*  94 */         if (param.sourceStart == start) {
/*  95 */           return param;
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/* 100 */     if (this.exceptionReferences != null) {
/* 101 */       length = this.exceptionReferences.length;
/* 102 */       for (int i = 0; i < length; i++) {
/* 103 */         TypeReference typeRef = this.exceptionReferences[i];
/* 104 */         if (typeRef.sourceStart == start) {
/* 105 */           return typeRef;
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/* 110 */     if (this.seeReferences != null) {
/* 111 */       length = this.seeReferences.length;
/* 112 */       for (int i = 0; i < length; i++) {
/* 113 */         Expression expression = this.seeReferences[i];
/* 114 */         if (expression.sourceStart == start)
/* 115 */           return expression;
/* 116 */         if ((expression instanceof JavadocAllocationExpression)) {
/* 117 */           JavadocAllocationExpression allocationExpr = (JavadocAllocationExpression)this.seeReferences[i];
/*     */ 
/* 119 */           if ((allocationExpr.binding == null) || (!allocationExpr.binding.isValidBinding()) || 
/* 120 */             (allocationExpr.arguments == null)) continue;
/* 121 */           int j = 0; for (int l = allocationExpr.arguments.length; j < l; j++) {
/* 122 */             if (allocationExpr.arguments[j].sourceStart == start) {
/* 123 */               return allocationExpr.arguments[j];
/*     */             }
/*     */           }
/*     */ 
/*     */         }
/* 128 */         else if ((expression instanceof JavadocMessageSend)) {
/* 129 */           JavadocMessageSend messageSend = (JavadocMessageSend)this.seeReferences[i];
/*     */ 
/* 131 */           if ((messageSend.binding == null) || (!messageSend.binding.isValidBinding()) || 
/* 132 */             (messageSend.arguments == null)) continue;
/* 133 */           int j = 0; for (int l = messageSend.arguments.length; j < l; j++) {
/* 134 */             if (messageSend.arguments[j].sourceStart == start) {
/* 135 */               return messageSend.arguments[j];
/*     */             }
/*     */           }
/*     */         }
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 143 */     return null;
/*     */   }
/*     */ 
/*     */   public StringBuffer print(int indent, StringBuffer output)
/*     */   {
/* 150 */     printIndent(indent, output).append("/**\n");
/* 151 */     if (this.paramReferences != null) {
/* 152 */       int i = 0; for (int length = this.paramReferences.length; i < length; i++) {
/* 153 */         printIndent(indent + 1, output).append(" * @param ");
/* 154 */         this.paramReferences[i].print(indent, output).append('\n');
/*     */       }
/*     */     }
/* 157 */     if (this.paramTypeParameters != null) {
/* 158 */       int i = 0; for (int length = this.paramTypeParameters.length; i < length; i++) {
/* 159 */         printIndent(indent + 1, output).append(" * @param <");
/* 160 */         this.paramTypeParameters[i].print(indent, output).append(">\n");
/*     */       }
/*     */     }
/* 163 */     if (this.returnStatement != null) {
/* 164 */       printIndent(indent + 1, output).append(" * @");
/* 165 */       this.returnStatement.print(indent, output).append('\n');
/*     */     }
/* 167 */     if (this.exceptionReferences != null) {
/* 168 */       int i = 0; for (int length = this.exceptionReferences.length; i < length; i++) {
/* 169 */         printIndent(indent + 1, output).append(" * @throws ");
/* 170 */         this.exceptionReferences[i].print(indent, output).append('\n');
/*     */       }
/*     */     }
/* 173 */     if (this.seeReferences != null) {
/* 174 */       int i = 0; for (int length = this.seeReferences.length; i < length; i++) {
/* 175 */         printIndent(indent + 1, output).append(" * @see ");
/* 176 */         this.seeReferences[i].print(indent, output).append('\n');
/*     */       }
/*     */     }
/* 179 */     printIndent(indent, output).append(" */\n");
/* 180 */     return output;
/*     */   }
/*     */ 
/*     */   public void resolve(ClassScope scope)
/*     */   {
/* 189 */     if (this.inheritedPositions != null) {
/* 190 */       int length = this.inheritedPositions.length;
/* 191 */       for (int i = 0; i < length; i++) {
/* 192 */         int start = (int)(this.inheritedPositions[i] >>> 32);
/* 193 */         int end = (int)this.inheritedPositions[i];
/* 194 */         scope.problemReporter().javadocUnexpectedTag(start, end);
/*     */       }
/*     */     }
/*     */ 
/* 198 */     int paramTagsSize = this.paramReferences == null ? 0 : this.paramReferences.length;
/* 199 */     for (int i = 0; i < paramTagsSize; i++) {
/* 200 */       JavadocSingleNameReference param = this.paramReferences[i];
/* 201 */       scope.problemReporter().javadocUnexpectedTag(param.tagSourceStart, param.tagSourceEnd);
/*     */     }
/* 203 */     resolveTypeParameterTags(scope, true);
/*     */ 
/* 206 */     if (this.returnStatement != null) {
/* 207 */       scope.problemReporter().javadocUnexpectedTag(this.returnStatement.sourceStart, this.returnStatement.sourceEnd);
/*     */     }
/*     */ 
/* 211 */     int throwsTagsLength = this.exceptionReferences == null ? 0 : this.exceptionReferences.length;
/* 212 */     for (int i = 0; i < throwsTagsLength; i++) {
/* 213 */       TypeReference typeRef = this.exceptionReferences[i];
/*     */       int end;
/*     */       int start;
/*     */       int end;
/* 215 */       if ((typeRef instanceof JavadocSingleTypeReference)) {
/* 216 */         JavadocSingleTypeReference singleRef = (JavadocSingleTypeReference)typeRef;
/* 217 */         int start = singleRef.tagSourceStart;
/* 218 */         end = singleRef.tagSourceEnd;
/*     */       }
/*     */       else
/*     */       {
/*     */         int end;
/* 219 */         if ((typeRef instanceof JavadocQualifiedTypeReference)) {
/* 220 */           JavadocQualifiedTypeReference qualifiedRef = (JavadocQualifiedTypeReference)typeRef;
/* 221 */           int start = qualifiedRef.tagSourceStart;
/* 222 */           end = qualifiedRef.tagSourceEnd;
/*     */         } else {
/* 224 */           start = typeRef.sourceStart;
/* 225 */           end = typeRef.sourceEnd;
/*     */         }
/*     */       }
/* 227 */       scope.problemReporter().javadocUnexpectedTag(start, end);
/*     */     }
/*     */ 
/* 231 */     int seeTagsLength = this.seeReferences == null ? 0 : this.seeReferences.length;
/* 232 */     for (int i = 0; i < seeTagsLength; i++) {
/* 233 */       resolveReference(this.seeReferences[i], scope);
/*     */     }
/*     */ 
/* 237 */     boolean source15 = scope.compilerOptions().sourceLevel >= 3211264L;
/* 238 */     if ((!source15) && (this.valuePositions != -1L))
/* 239 */       scope.problemReporter().javadocUnexpectedTag((int)(this.valuePositions >>> 32), (int)this.valuePositions);
/*     */   }
/*     */ 
/*     */   public void resolve(CompilationUnitScope unitScope)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void resolve(MethodScope methScope)
/*     */   {
/* 258 */     AbstractMethodDeclaration methDecl = methScope.referenceMethod();
/* 259 */     boolean overriding = (methDecl != null) && (methDecl.binding != null);
/*     */ 
/* 264 */     int seeTagsLength = this.seeReferences == null ? 0 : this.seeReferences.length;
/* 265 */     boolean superRef = false;
/* 266 */     for (int i = 0; i < seeTagsLength; i++)
/*     */     {
/* 269 */       resolveReference(this.seeReferences[i], methScope);
/*     */ 
/* 272 */       if ((methDecl != null) && (!superRef)) {
/* 273 */         if (!methDecl.isConstructor()) {
/* 274 */           if ((overriding) && ((this.seeReferences[i] instanceof JavadocMessageSend))) {
/* 275 */             JavadocMessageSend messageSend = (JavadocMessageSend)this.seeReferences[i];
/*     */ 
/* 277 */             if ((messageSend.binding != null) && (messageSend.binding.isValidBinding()) && ((messageSend.actualReceiverType instanceof ReferenceBinding))) {
/* 278 */               ReferenceBinding methodReceiverType = (ReferenceBinding)messageSend.actualReceiverType;
/* 279 */               TypeBinding superType = methDecl.binding.declaringClass.findSuperTypeOriginatingFrom(methodReceiverType);
/* 280 */               if ((superType == null) || (superType.original() == methDecl.binding.declaringClass) || (!CharOperation.equals(messageSend.selector, methDecl.selector)) || 
/* 281 */                 (!methScope.environment().methodVerifier().doesMethodOverride(methDecl.binding, messageSend.binding.original()))) continue;
/* 282 */               superRef = true;
/*     */             }
/*     */           }
/*     */ 
/*     */         }
/* 287 */         else if ((this.seeReferences[i] instanceof JavadocAllocationExpression)) {
/* 288 */           JavadocAllocationExpression allocationExpr = (JavadocAllocationExpression)this.seeReferences[i];
/*     */ 
/* 290 */           if ((allocationExpr.binding != null) && (allocationExpr.binding.isValidBinding())) {
/* 291 */             ReferenceBinding allocType = (ReferenceBinding)allocationExpr.resolvedType.original();
/* 292 */             ReferenceBinding superType = (ReferenceBinding)methDecl.binding.declaringClass.findSuperTypeOriginatingFrom(allocType);
/* 293 */             if ((superType != null) && (superType.original() != methDecl.binding.declaringClass)) {
/* 294 */               MethodBinding superConstructor = methScope.getConstructor(superType, methDecl.binding.parameters, allocationExpr);
/* 295 */               if ((!superConstructor.isValidBinding()) || (superConstructor.original() != allocationExpr.binding.original()) || 
/* 296 */                 (!superConstructor.areParametersEqual(methDecl.binding))) continue;
/* 297 */               superRef = true;
/*     */             }
/*     */           }
/*     */ 
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 307 */     if ((!superRef) && (methDecl != null) && (methDecl.annotations != null)) {
/* 308 */       int length = methDecl.annotations.length;
/* 309 */       for (int i = 0; (i < length) && (!superRef); i++) {
/* 310 */         superRef = (methDecl.binding.tagBits & 0x0) != 0L;
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 315 */     boolean reportMissing = (methDecl == null) || (((!overriding) || (this.inheritedPositions == null)) && (!superRef) && ((methDecl.binding.declaringClass == null) || (!methDecl.binding.declaringClass.isLocalType())));
/* 316 */     if ((!overriding) && (this.inheritedPositions != null)) {
/* 317 */       int length = this.inheritedPositions.length;
/* 318 */       for (int i = 0; i < length; i++) {
/* 319 */         int start = (int)(this.inheritedPositions[i] >>> 32);
/* 320 */         int end = (int)this.inheritedPositions[i];
/* 321 */         methScope.problemReporter().javadocUnexpectedTag(start, end);
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 326 */     CompilerOptions compilerOptions = methScope.compilerOptions();
/* 327 */     resolveParamTags(methScope, reportMissing, compilerOptions.reportUnusedParameterIncludeDocCommentReference);
/* 328 */     resolveTypeParameterTags(methScope, reportMissing);
/*     */ 
/* 331 */     if (this.returnStatement == null) {
/* 332 */       if ((reportMissing) && (methDecl != null) && 
/* 333 */         (methDecl.isMethod())) {
/* 334 */         MethodDeclaration meth = (MethodDeclaration)methDecl;
/* 335 */         if (meth.binding.returnType != TypeBinding.VOID)
/*     */         {
/* 337 */           methScope.problemReporter().javadocMissingReturnTag(meth.returnType.sourceStart, meth.returnType.sourceEnd, methDecl.binding.modifiers);
/*     */         }
/*     */       }
/*     */     }
/*     */     else {
/* 342 */       this.returnStatement.resolve(methScope);
/*     */     }
/*     */ 
/* 346 */     resolveThrowsTags(methScope, reportMissing);
/*     */ 
/* 349 */     boolean source15 = compilerOptions.sourceLevel >= 3211264L;
/* 350 */     if ((!source15) && (methDecl != null) && (this.valuePositions != -1L)) {
/* 351 */       methScope.problemReporter().javadocUnexpectedTag((int)(this.valuePositions >>> 32), (int)this.valuePositions);
/*     */     }
/*     */ 
/* 355 */     int length = this.invalidParameters == null ? 0 : this.invalidParameters.length;
/* 356 */     for (int i = 0; i < length; i++)
/* 357 */       this.invalidParameters[i].resolve(methScope, false, false);
/*     */   }
/*     */ 
/*     */   private void resolveReference(Expression reference, Scope scope)
/*     */   {
/* 364 */     int problemCount = scope.referenceContext().compilationResult().problemCount;
/* 365 */     switch (scope.kind) {
/*     */     case 2:
/* 367 */       reference.resolveType((MethodScope)scope);
/* 368 */       break;
/*     */     case 3:
/* 370 */       reference.resolveType((ClassScope)scope);
/*     */     }
/*     */ 
/* 373 */     boolean hasProblems = scope.referenceContext().compilationResult().problemCount > problemCount;
/*     */ 
/* 376 */     boolean source15 = scope.compilerOptions().sourceLevel >= 3211264L;
/* 377 */     int scopeModifiers = -1;
/* 378 */     if ((reference instanceof JavadocFieldReference)) {
/* 379 */       JavadocFieldReference fieldRef = (JavadocFieldReference)reference;
/*     */ 
/* 383 */       if (fieldRef.methodBinding != null)
/*     */       {
/* 385 */         if (fieldRef.tagValue == 10) {
/* 386 */           if (scopeModifiers == -1) scopeModifiers = scope.getDeclarationModifiers();
/* 387 */           scope.problemReporter().javadocInvalidValueReference(fieldRef.sourceStart, fieldRef.sourceEnd, scopeModifiers);
/*     */         }
/* 389 */         else if (fieldRef.actualReceiverType != null) {
/* 390 */           if (scope.enclosingSourceType().isCompatibleWith(fieldRef.actualReceiverType)) {
/* 391 */             fieldRef.bits |= 16384;
/*     */           }
/* 393 */           fieldRef.methodBinding = scope.findMethod((ReferenceBinding)fieldRef.actualReceiverType, fieldRef.token, new TypeBinding[0], fieldRef);
/*     */         }
/*     */ 
/*     */       }
/* 398 */       else if ((source15) && (fieldRef.binding != null) && (fieldRef.binding.isValidBinding()) && 
/* 399 */         (fieldRef.tagValue == 10) && (!fieldRef.binding.isStatic())) {
/* 400 */         if (scopeModifiers == -1) scopeModifiers = scope.getDeclarationModifiers();
/* 401 */         scope.problemReporter().javadocInvalidValueReference(fieldRef.sourceStart, fieldRef.sourceEnd, scopeModifiers);
/*     */       }
/*     */ 
/* 406 */       if ((!hasProblems) && (fieldRef.binding != null) && (fieldRef.binding.isValidBinding()) && ((fieldRef.actualReceiverType instanceof ReferenceBinding))) {
/* 407 */         ReferenceBinding resolvedType = (ReferenceBinding)fieldRef.actualReceiverType;
/* 408 */         verifyTypeReference(fieldRef, fieldRef.receiver, scope, source15, resolvedType, fieldRef.binding.modifiers);
/*     */       }
/*     */ 
/* 412 */       return;
/*     */     }
/*     */ 
/* 416 */     if ((!hasProblems) && (((reference instanceof JavadocSingleTypeReference)) || ((reference instanceof JavadocQualifiedTypeReference))) && ((reference.resolvedType instanceof ReferenceBinding))) {
/* 417 */       ReferenceBinding resolvedType = (ReferenceBinding)reference.resolvedType;
/* 418 */       verifyTypeReference(reference, reference, scope, source15, resolvedType, resolvedType.modifiers);
/*     */     }
/*     */ 
/* 422 */     if ((reference instanceof JavadocMessageSend)) {
/* 423 */       JavadocMessageSend msgSend = (JavadocMessageSend)reference;
/*     */ 
/* 426 */       if ((source15) && (msgSend.tagValue == 10)) {
/* 427 */         if (scopeModifiers == -1) scopeModifiers = scope.getDeclarationModifiers();
/* 428 */         scope.problemReporter().javadocInvalidValueReference(msgSend.sourceStart, msgSend.sourceEnd, scopeModifiers);
/*     */       }
/*     */ 
/* 432 */       if ((!hasProblems) && (msgSend.binding != null) && (msgSend.binding.isValidBinding()) && ((msgSend.actualReceiverType instanceof ReferenceBinding))) {
/* 433 */         ReferenceBinding resolvedType = (ReferenceBinding)msgSend.actualReceiverType;
/* 434 */         verifyTypeReference(msgSend, msgSend.receiver, scope, source15, resolvedType, msgSend.binding.modifiers);
/*     */       }
/*     */ 
/*     */     }
/* 439 */     else if ((reference instanceof JavadocAllocationExpression)) {
/* 440 */       JavadocAllocationExpression alloc = (JavadocAllocationExpression)reference;
/*     */ 
/* 443 */       if ((source15) && (alloc.tagValue == 10)) {
/* 444 */         if (scopeModifiers == -1) scopeModifiers = scope.getDeclarationModifiers();
/* 445 */         scope.problemReporter().javadocInvalidValueReference(alloc.sourceStart, alloc.sourceEnd, scopeModifiers);
/*     */       }
/*     */ 
/* 449 */       if ((!hasProblems) && (alloc.binding != null) && (alloc.binding.isValidBinding()) && ((alloc.resolvedType instanceof ReferenceBinding))) {
/* 450 */         ReferenceBinding resolvedType = (ReferenceBinding)alloc.resolvedType;
/* 451 */         verifyTypeReference(alloc, alloc.type, scope, source15, resolvedType, alloc.binding.modifiers);
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 457 */     if ((reference.resolvedType != null) && (reference.resolvedType.isTypeVariable()))
/* 458 */       scope.problemReporter().javadocInvalidReference(reference.sourceStart, reference.sourceEnd);
/*     */   }
/*     */ 
/*     */   private void resolveParamTags(MethodScope scope, boolean reportMissing, boolean considerParamRefAsUsage)
/*     */   {
/* 466 */     AbstractMethodDeclaration methodDecl = scope.referenceMethod();
/* 467 */     int paramTagsSize = this.paramReferences == null ? 0 : this.paramReferences.length;
/*     */ 
/* 470 */     if (methodDecl == null) {
/* 471 */       for (int i = 0; i < paramTagsSize; i++) {
/* 472 */         JavadocSingleNameReference param = this.paramReferences[i];
/* 473 */         scope.problemReporter().javadocUnexpectedTag(param.tagSourceStart, param.tagSourceEnd);
/*     */       }
/* 475 */       return;
/*     */     }
/*     */ 
/* 479 */     int argumentsSize = methodDecl.arguments == null ? 0 : methodDecl.arguments.length;
/* 480 */     if (paramTagsSize == 0) {
/* 481 */       if (reportMissing)
/* 482 */         for (int i = 0; i < argumentsSize; i++) {
/* 483 */           Argument arg = methodDecl.arguments[i];
/* 484 */           scope.problemReporter().javadocMissingParamTag(arg.name, arg.sourceStart, arg.sourceEnd, methodDecl.binding.modifiers);
/*     */         }
/*     */     }
/*     */     else {
/* 488 */       LocalVariableBinding[] bindings = new LocalVariableBinding[paramTagsSize];
/* 489 */       int maxBindings = 0;
/*     */ 
/* 492 */       for (int i = 0; i < paramTagsSize; i++) {
/* 493 */         JavadocSingleNameReference param = this.paramReferences[i];
/* 494 */         param.resolve(scope, true, considerParamRefAsUsage);
/* 495 */         if ((param.binding == null) || (!param.binding.isValidBinding()))
/*     */           continue;
/* 497 */         boolean found = false;
/* 498 */         for (int j = 0; (j < maxBindings) && (!found); j++) {
/* 499 */           if (bindings[j] == param.binding) {
/* 500 */             scope.problemReporter().javadocDuplicatedParamTag(param.token, param.sourceStart, param.sourceEnd, methodDecl.binding.modifiers);
/* 501 */             found = true;
/*     */           }
/*     */         }
/* 504 */         if (!found) {
/* 505 */           bindings[(maxBindings++)] = ((LocalVariableBinding)param.binding);
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/* 511 */       if (reportMissing)
/* 512 */         for (int i = 0; i < argumentsSize; i++) {
/* 513 */           Argument arg = methodDecl.arguments[i];
/* 514 */           boolean found = false;
/* 515 */           for (int j = 0; (j < maxBindings) && (!found); j++) {
/* 516 */             LocalVariableBinding binding = bindings[j];
/* 517 */             if (arg.binding == binding) {
/* 518 */               found = true;
/*     */             }
/*     */           }
/* 521 */           if (!found)
/* 522 */             scope.problemReporter().javadocMissingParamTag(arg.name, arg.sourceStart, arg.sourceEnd, methodDecl.binding.modifiers);
/*     */         }
/*     */     }
/*     */   }
/*     */ 
/*     */   private void resolveTypeParameterTags(Scope scope, boolean reportMissing)
/*     */   {
/* 533 */     int paramTypeParamLength = this.paramTypeParameters == null ? 0 : this.paramTypeParameters.length;
/*     */ 
/* 536 */     TypeParameter[] parameters = (TypeParameter[])null;
/* 537 */     TypeVariableBinding[] typeVariables = (TypeVariableBinding[])null;
/* 538 */     int modifiers = -1;
/* 539 */     switch (scope.kind) {
/*     */     case 2:
/* 541 */       AbstractMethodDeclaration methodDeclaration = ((MethodScope)scope).referenceMethod();
/*     */ 
/* 543 */       if (methodDeclaration == null) {
/* 544 */         for (int i = 0; i < paramTypeParamLength; i++) {
/* 545 */           JavadocSingleTypeReference param = this.paramTypeParameters[i];
/* 546 */           scope.problemReporter().javadocUnexpectedTag(param.tagSourceStart, param.tagSourceEnd);
/*     */         }
/* 548 */         return;
/*     */       }
/* 550 */       parameters = methodDeclaration.typeParameters();
/* 551 */       typeVariables = methodDeclaration.binding.typeVariables;
/* 552 */       modifiers = methodDeclaration.binding.modifiers;
/* 553 */       break;
/*     */     case 3:
/* 555 */       TypeDeclaration typeDeclaration = ((ClassScope)scope).referenceContext;
/* 556 */       parameters = typeDeclaration.typeParameters;
/* 557 */       typeVariables = typeDeclaration.binding.typeVariables;
/* 558 */       modifiers = typeDeclaration.binding.modifiers;
/*     */     }
/*     */ 
/* 563 */     if ((typeVariables == null) || (typeVariables.length == 0)) {
/* 564 */       for (int i = 0; i < paramTypeParamLength; i++) {
/* 565 */         JavadocSingleTypeReference param = this.paramTypeParameters[i];
/* 566 */         scope.problemReporter().javadocUnexpectedTag(param.tagSourceStart, param.tagSourceEnd);
/*     */       }
/* 568 */       return;
/*     */     }
/*     */ 
/* 572 */     if (parameters != null) {
/* 573 */       int typeParametersLength = parameters.length;
/* 574 */       if (paramTypeParamLength == 0) {
/* 575 */         if (reportMissing) {
/* 576 */           int i = 0; for (int l = typeParametersLength; i < l; i++) {
/* 577 */             scope.problemReporter().javadocMissingParamTag(parameters[i].name, parameters[i].sourceStart, parameters[i].sourceEnd, modifiers);
/*     */           }
/*     */         }
/*     */ 
/*     */       }
/* 582 */       else if (typeVariables.length == typeParametersLength) {
/* 583 */         TypeVariableBinding[] bindings = new TypeVariableBinding[paramTypeParamLength];
/*     */ 
/* 586 */         for (int i = 0; i < paramTypeParamLength; i++) {
/* 587 */           JavadocSingleTypeReference param = this.paramTypeParameters[i];
/* 588 */           TypeBinding paramBindind = param.internalResolveType(scope);
/* 589 */           if ((paramBindind != null) && (paramBindind.isValidBinding())) {
/* 590 */             if (paramBindind.isTypeVariable())
/*     */             {
/* 592 */               boolean duplicate = false;
/* 593 */               for (int j = 0; (j < i) && (!duplicate); j++) {
/* 594 */                 if (bindings[j] == param.resolvedType) {
/* 595 */                   scope.problemReporter().javadocDuplicatedParamTag(param.token, param.sourceStart, param.sourceEnd, modifiers);
/* 596 */                   duplicate = true;
/*     */                 }
/*     */               }
/* 599 */               if (!duplicate)
/* 600 */                 bindings[i] = ((TypeVariableBinding)param.resolvedType);
/*     */             }
/*     */             else {
/* 603 */               scope.problemReporter().javadocUndeclaredParamTagName(param.token, param.sourceStart, param.sourceEnd, modifiers);
/*     */             }
/*     */           }
/*     */ 
/*     */         }
/*     */ 
/* 609 */         for (int i = 0; i < typeParametersLength; i++) {
/* 610 */           TypeParameter parameter = parameters[i];
/* 611 */           boolean found = false;
/* 612 */           for (int j = 0; (j < paramTypeParamLength) && (!found); j++) {
/* 613 */             if (parameter.binding == bindings[j]) {
/* 614 */               found = true;
/* 615 */               bindings[j] = null;
/*     */             }
/*     */           }
/* 618 */           if ((!found) && (reportMissing)) {
/* 619 */             scope.problemReporter().javadocMissingParamTag(parameter.name, parameter.sourceStart, parameter.sourceEnd, modifiers);
/*     */           }
/*     */ 
/*     */         }
/*     */ 
/* 624 */         for (int i = 0; i < paramTypeParamLength; i++)
/* 625 */           if (bindings[i] != null) {
/* 626 */             JavadocSingleTypeReference param = this.paramTypeParameters[i];
/* 627 */             scope.problemReporter().javadocUndeclaredParamTagName(param.token, param.sourceStart, param.sourceEnd, modifiers);
/*     */           }
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   private void resolveThrowsTags(MethodScope methScope, boolean reportMissing)
/*     */   {
/* 638 */     AbstractMethodDeclaration md = methScope.referenceMethod();
/* 639 */     int throwsTagsLength = this.exceptionReferences == null ? 0 : this.exceptionReferences.length;
/*     */ 
/* 642 */     if (md == null) {
/* 643 */       for (int i = 0; i < throwsTagsLength; i++) {
/* 644 */         TypeReference typeRef = this.exceptionReferences[i];
/* 645 */         int start = typeRef.sourceStart;
/* 646 */         int end = typeRef.sourceEnd;
/* 647 */         if ((typeRef instanceof JavadocQualifiedTypeReference)) {
/* 648 */           start = ((JavadocQualifiedTypeReference)typeRef).tagSourceStart;
/* 649 */           end = ((JavadocQualifiedTypeReference)typeRef).tagSourceEnd;
/* 650 */         } else if ((typeRef instanceof JavadocSingleTypeReference)) {
/* 651 */           start = ((JavadocSingleTypeReference)typeRef).tagSourceStart;
/* 652 */           end = ((JavadocSingleTypeReference)typeRef).tagSourceEnd;
/*     */         }
/* 654 */         methScope.problemReporter().javadocUnexpectedTag(start, end);
/*     */       }
/* 656 */       return;
/*     */     }
/*     */ 
/* 660 */     int boundExceptionLength = md.binding == null ? 0 : md.binding.thrownExceptions.length;
/* 661 */     int thrownExceptionLength = md.thrownExceptions == null ? 0 : md.thrownExceptions.length;
/* 662 */     if (throwsTagsLength == 0) {
/* 663 */       if (reportMissing)
/* 664 */         for (int i = 0; i < boundExceptionLength; i++) {
/* 665 */           ReferenceBinding exceptionBinding = md.binding.thrownExceptions[i];
/* 666 */           if ((exceptionBinding != null) && (exceptionBinding.isValidBinding())) {
/* 667 */             int j = i;
/* 668 */             while ((j < thrownExceptionLength) && (exceptionBinding != md.thrownExceptions[j].resolvedType)) j++;
/* 669 */             if (j < thrownExceptionLength)
/* 670 */               methScope.problemReporter().javadocMissingThrowsTag(md.thrownExceptions[j], md.binding.modifiers);
/*     */           }
/*     */         }
/*     */     }
/*     */     else
/*     */     {
/* 676 */       int maxRef = 0;
/* 677 */       TypeReference[] typeReferences = new TypeReference[throwsTagsLength];
/*     */ 
/* 680 */       for (int i = 0; i < throwsTagsLength; i++) {
/* 681 */         TypeReference typeRef = this.exceptionReferences[i];
/* 682 */         typeRef.resolve(methScope);
/* 683 */         TypeBinding typeBinding = typeRef.resolvedType;
/*     */ 
/* 685 */         if ((typeBinding == null) || (!typeBinding.isValidBinding()) || (!typeBinding.isClass()))
/*     */           continue;
/* 687 */         typeReferences[(maxRef++)] = typeRef;
/*     */       }
/*     */ 
/* 692 */       for (int i = 0; i < boundExceptionLength; i++) {
/* 693 */         ReferenceBinding exceptionBinding = md.binding.thrownExceptions[i];
/* 694 */         if (exceptionBinding != null) exceptionBinding = (ReferenceBinding)exceptionBinding.erasure();
/* 695 */         boolean found = false;
/* 696 */         for (int j = 0; (j < maxRef) && (!found); j++) {
/* 697 */           if (typeReferences[j] != null) {
/* 698 */             TypeBinding typeBinding = typeReferences[j].resolvedType;
/* 699 */             if (exceptionBinding == typeBinding) {
/* 700 */               found = true;
/* 701 */               typeReferences[j] = null;
/*     */             }
/*     */           }
/*     */         }
/* 705 */         if ((found) || (!reportMissing) || 
/* 706 */           (exceptionBinding == null) || (!exceptionBinding.isValidBinding())) continue;
/* 707 */         int k = i;
/* 708 */         while ((k < thrownExceptionLength) && (exceptionBinding != md.thrownExceptions[k].resolvedType)) k++;
/* 709 */         if (k < thrownExceptionLength) {
/* 710 */           methScope.problemReporter().javadocMissingThrowsTag(md.thrownExceptions[k], md.binding.modifiers);
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/* 717 */       for (int i = 0; i < maxRef; i++) {
/* 718 */         TypeReference typeRef = typeReferences[i];
/* 719 */         if (typeRef != null) {
/* 720 */           boolean compatible = false;
/*     */ 
/* 722 */           for (int j = 0; (j < thrownExceptionLength) && (!compatible); j++) {
/* 723 */             TypeBinding exceptionBinding = md.thrownExceptions[j].resolvedType;
/* 724 */             if (exceptionBinding != null) {
/* 725 */               compatible = typeRef.resolvedType.isCompatibleWith(exceptionBinding);
/*     */             }
/*     */ 
/*     */           }
/*     */ 
/* 730 */           if ((!compatible) && (!typeRef.resolvedType.isUncheckedException(false)))
/* 731 */             methScope.problemReporter().javadocInvalidThrowsClassName(typeRef, md.binding.modifiers);
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   private void verifyTypeReference(Expression reference, Expression typeReference, Scope scope, boolean source15, ReferenceBinding resolvedType, int modifiers)
/*     */   {
/* 739 */     if (resolvedType.isValidBinding()) {
/* 740 */       int scopeModifiers = -1;
/*     */ 
/* 743 */       if (!canBeSeen(scope.problemReporter().options.reportInvalidJavadocTagsVisibility, modifiers)) {
/* 744 */         scope.problemReporter().javadocHiddenReference(typeReference.sourceStart, reference.sourceEnd, scope, modifiers);
/* 745 */         return;
/*     */       }
/*     */ 
/* 749 */       if ((reference != typeReference) && 
/* 750 */         (!canBeSeen(scope.problemReporter().options.reportInvalidJavadocTagsVisibility, resolvedType.modifiers))) {
/* 751 */         scope.problemReporter().javadocHiddenReference(typeReference.sourceStart, typeReference.sourceEnd, scope, resolvedType.modifiers);
/* 752 */         return;
/*     */       }
/*     */ 
/* 757 */       if (resolvedType.isMemberType()) {
/* 758 */         ReferenceBinding topLevelType = resolvedType;
/*     */ 
/* 760 */         int packageLength = topLevelType.fPackage.compoundName.length;
/* 761 */         int depth = resolvedType.depth();
/* 762 */         int idx = depth + packageLength;
/* 763 */         char[][] computedCompoundName = new char[idx + 1][];
/* 764 */         computedCompoundName[idx] = topLevelType.sourceName;
/* 765 */         while (topLevelType.enclosingType() != null) {
/* 766 */           topLevelType = topLevelType.enclosingType();
/* 767 */           idx--; computedCompoundName[idx] = topLevelType.sourceName;
/*     */         }
/*     */ 
/* 771 */         int i = packageLength;
/*     */         do { idx--; computedCompoundName[idx] = topLevelType.fPackage.compoundName[i];
/*     */ 
/* 771 */           i--; } while (i >= 0);
/*     */ 
/* 775 */         ClassScope topLevelScope = scope.classScope();
/*     */ 
/* 777 */         if ((topLevelScope.parent.kind != 4) || 
/* 778 */           (!CharOperation.equals(topLevelType.sourceName, topLevelScope.referenceContext.name))) {
/* 779 */           topLevelScope = topLevelScope.outerMostClassScope();
/* 780 */           if ((typeReference instanceof JavadocSingleTypeReference))
/*     */           {
/* 782 */             if (((!source15) && (depth == 1)) || (topLevelType != topLevelScope.referenceContext.binding))
/*     */             {
/* 784 */               boolean hasValidImport = false;
/* 785 */               if (source15) {
/* 786 */                 CompilationUnitScope unitScope = topLevelScope.compilationUnitScope();
/* 787 */                 ImportBinding[] imports = unitScope.imports;
/* 788 */                 int length = imports == null ? 0 : imports.length;
/* 789 */                 for (int i = 0; i < length; i++) {
/* 790 */                   char[][] compoundName = imports[i].compoundName;
/* 791 */                   int compoundNameLength = compoundName.length;
/* 792 */                   if (((!imports[i].onDemand) || (compoundNameLength != computedCompoundName.length - 1)) && 
/* 793 */                     (compoundNameLength != computedCompoundName.length)) continue;
/* 794 */                   int j = compoundNameLength;
/*     */                   do { if (!CharOperation.equals(imports[i].compoundName[j], computedCompoundName[j])) break;
/* 796 */                     if (j == 0) {
/* 797 */                       hasValidImport = true;
/* 798 */                       ImportReference importReference = imports[i].reference;
/* 799 */                       if (importReference == null) break label464; importReference.bits |= 2;
/*     */ 
/* 802 */                       break label464;
/*     */                     }
/* 794 */                     j--; } while (j >= 0);
/*     */                 }
/*     */ 
/* 810 */                 label464: if (!hasValidImport) {
/* 811 */                   if (scopeModifiers == -1) scopeModifiers = scope.getDeclarationModifiers();
/* 812 */                   scope.problemReporter().javadocInvalidMemberTypeQualification(typeReference.sourceStart, typeReference.sourceEnd, scopeModifiers);
/*     */                 }
/*     */               } else {
/* 815 */                 if (scopeModifiers == -1) scopeModifiers = scope.getDeclarationModifiers();
/* 816 */                 scope.problemReporter().javadocInvalidMemberTypeQualification(typeReference.sourceStart, typeReference.sourceEnd, scopeModifiers);
/* 817 */                 return;
/*     */               }
/*     */             }
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public void traverse(ASTVisitor visitor, BlockScope scope) {
/* 827 */     if (visitor.visit(this, scope)) {
/* 828 */       if (this.paramReferences != null) {
/* 829 */         int i = 0; for (int length = this.paramReferences.length; i < length; i++) {
/* 830 */           this.paramReferences[i].traverse(visitor, scope);
/*     */         }
/*     */       }
/* 833 */       if (this.paramTypeParameters != null) {
/* 834 */         int i = 0; for (int length = this.paramTypeParameters.length; i < length; i++) {
/* 835 */           this.paramTypeParameters[i].traverse(visitor, scope);
/*     */         }
/*     */       }
/* 838 */       if (this.returnStatement != null) {
/* 839 */         this.returnStatement.traverse(visitor, scope);
/*     */       }
/* 841 */       if (this.exceptionReferences != null) {
/* 842 */         int i = 0; for (int length = this.exceptionReferences.length; i < length; i++) {
/* 843 */           this.exceptionReferences[i].traverse(visitor, scope);
/*     */         }
/*     */       }
/* 846 */       if (this.seeReferences != null) {
/* 847 */         int i = 0; for (int length = this.seeReferences.length; i < length; i++) {
/* 848 */           this.seeReferences[i].traverse(visitor, scope);
/*     */         }
/*     */       }
/*     */     }
/* 852 */     visitor.endVisit(this, scope);
/*     */   }
/*     */   public void traverse(ASTVisitor visitor, ClassScope scope) {
/* 855 */     if (visitor.visit(this, scope)) {
/* 856 */       if (this.paramReferences != null) {
/* 857 */         int i = 0; for (int length = this.paramReferences.length; i < length; i++) {
/* 858 */           this.paramReferences[i].traverse(visitor, scope);
/*     */         }
/*     */       }
/* 861 */       if (this.paramTypeParameters != null) {
/* 862 */         int i = 0; for (int length = this.paramTypeParameters.length; i < length; i++) {
/* 863 */           this.paramTypeParameters[i].traverse(visitor, scope);
/*     */         }
/*     */       }
/* 866 */       if (this.returnStatement != null) {
/* 867 */         this.returnStatement.traverse(visitor, scope);
/*     */       }
/* 869 */       if (this.exceptionReferences != null) {
/* 870 */         int i = 0; for (int length = this.exceptionReferences.length; i < length; i++) {
/* 871 */           this.exceptionReferences[i].traverse(visitor, scope);
/*     */         }
/*     */       }
/* 874 */       if (this.seeReferences != null) {
/* 875 */         int i = 0; for (int length = this.seeReferences.length; i < length; i++) {
/* 876 */           this.seeReferences[i].traverse(visitor, scope);
/*     */         }
/*     */       }
/*     */     }
/* 880 */     visitor.endVisit(this, scope);
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\ecj-3.5.2.jar
 * Qualified Name:     org.eclipse.jdt.internal.compiler.ast.Javadoc
 * JD-Core Version:    0.6.0
 */