/*     */ package org.eclipse.jdt.internal.compiler.ast;
/*     */ 
/*     */ import org.eclipse.jdt.internal.compiler.ASTVisitor;
/*     */ import org.eclipse.jdt.internal.compiler.codegen.CodeStream;
/*     */ import org.eclipse.jdt.internal.compiler.flow.FlowContext;
/*     */ import org.eclipse.jdt.internal.compiler.flow.FlowInfo;
/*     */ import org.eclipse.jdt.internal.compiler.impl.CompilerOptions;
/*     */ import org.eclipse.jdt.internal.compiler.impl.Constant;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.ArrayBinding;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.BlockScope;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.InvocationSite;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.LocalVariableBinding;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.LookupEnvironment;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.MethodBinding;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.ParameterizedGenericMethodBinding;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.ParameterizedTypeBinding;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.ReferenceBinding;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.Scope;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;
/*     */ import org.eclipse.jdt.internal.compiler.problem.ProblemReporter;
/*     */ 
/*     */ public class CastExpression extends Expression
/*     */ {
/*     */   public Expression expression;
/*     */   public Expression type;
/*     */   public TypeBinding expectedType;
/*     */ 
/*     */   public CastExpression(Expression expression, Expression type)
/*     */   {
/*  44 */     this.expression = expression;
/*  45 */     this.type = type;
/*  46 */     type.bits |= 1073741824;
/*     */   }
/*     */ 
/*     */   public FlowInfo analyseCode(BlockScope currentScope, FlowContext flowContext, FlowInfo flowInfo) {
/*  50 */     return this.expression
/*  51 */       .analyseCode(currentScope, flowContext, flowInfo)
/*  52 */       .unconditionalInits();
/*     */   }
/*     */ 
/*     */   public static void checkNeedForAssignedCast(BlockScope scope, TypeBinding expectedType, CastExpression rhs)
/*     */   {
/*  59 */     if (scope.compilerOptions().getSeverity(67108864) == -1) return;
/*     */ 
/*  61 */     TypeBinding castedExpressionType = rhs.expression.resolvedType;
/*     */ 
/*  64 */     if ((castedExpressionType == null) || (rhs.resolvedType.isBaseType())) return;
/*     */ 
/*  66 */     if (castedExpressionType.isCompatibleWith(expectedType))
/*  67 */       scope.problemReporter().unnecessaryCast(rhs);
/*     */   }
/*     */ 
/*     */   public static void checkNeedForCastCast(BlockScope scope, CastExpression enclosingCast)
/*     */   {
/*  77 */     if (scope.compilerOptions().getSeverity(67108864) == -1) return;
/*     */ 
/*  79 */     CastExpression nestedCast = (CastExpression)enclosingCast.expression;
/*  80 */     if ((nestedCast.bits & 0x4000) == 0) return;
/*     */ 
/*  82 */     CastExpression alternateCast = new CastExpression(null, enclosingCast.type);
/*  83 */     alternateCast.resolvedType = enclosingCast.resolvedType;
/*  84 */     if (!alternateCast.checkCastTypesCompatibility(scope, enclosingCast.resolvedType, nestedCast.expression.resolvedType, null)) return;
/*  85 */     scope.problemReporter().unnecessaryCast(nestedCast);
/*     */   }
/*     */ 
/*     */   public static void checkNeedForEnclosingInstanceCast(BlockScope scope, Expression enclosingInstance, TypeBinding enclosingInstanceType, TypeBinding memberType)
/*     */   {
/*  93 */     if (scope.compilerOptions().getSeverity(67108864) == -1) return;
/*     */ 
/*  95 */     TypeBinding castedExpressionType = ((CastExpression)enclosingInstance).expression.resolvedType;
/*  96 */     if (castedExpressionType == null) return;
/*     */ 
/*  98 */     if (castedExpressionType == enclosingInstanceType) {
/*  99 */       scope.problemReporter().unnecessaryCast((CastExpression)enclosingInstance); } else {
/* 100 */       if (castedExpressionType == TypeBinding.NULL) {
/* 101 */         return;
/*     */       }
/* 103 */       TypeBinding alternateEnclosingInstanceType = castedExpressionType;
/* 104 */       if ((castedExpressionType.isBaseType()) || (castedExpressionType.isArrayType())) return;
/* 105 */       if (memberType == scope.getMemberType(memberType.sourceName(), (ReferenceBinding)alternateEnclosingInstanceType))
/* 106 */         scope.problemReporter().unnecessaryCast((CastExpression)enclosingInstance);
/*     */     }
/*     */   }
/*     */ 
/*     */   public static void checkNeedForArgumentCast(BlockScope scope, int operator, int operatorSignature, Expression expression, int expressionTypeId)
/*     */   {
/* 115 */     if (scope.compilerOptions().getSeverity(67108864) == -1) return;
/*     */ 
/* 118 */     int alternateLeftTypeId = expressionTypeId;
/* 119 */     if (((expression.bits & 0x4000) == 0) && (expression.resolvedType.isBaseType()))
/*     */     {
/* 121 */       return;
/*     */     }
/* 123 */     TypeBinding alternateLeftType = ((CastExpression)expression).expression.resolvedType;
/* 124 */     if (alternateLeftType == null) return;
/* 125 */     if ((alternateLeftTypeId = alternateLeftType.id) == expressionTypeId) {
/* 126 */       scope.problemReporter().unnecessaryCast((CastExpression)expression);
/* 127 */       return;
/* 128 */     }if (alternateLeftTypeId == 12) {
/* 129 */       alternateLeftTypeId = expressionTypeId;
/* 130 */       return;
/*     */     }
/*     */   }
/*     */ 
/*     */   public static void checkNeedForArgumentCasts(BlockScope scope, Expression receiver, TypeBinding receiverType, MethodBinding binding, Expression[] arguments, TypeBinding[] argumentTypes, InvocationSite invocationSite)
/*     */   {
/* 150 */     if (scope.compilerOptions().getSeverity(67108864) == -1) return;
/*     */ 
/* 152 */     int length = argumentTypes.length;
/*     */ 
/* 155 */     TypeBinding[] rawArgumentTypes = argumentTypes;
/* 156 */     for (int i = 0; i < length; i++) {
/* 157 */       Expression argument = arguments[i];
/* 158 */       if (!(argument instanceof CastExpression))
/*     */         continue;
/* 160 */       if (((argument.bits & 0x4000) == 0) && (argument.resolvedType.isBaseType())) {
/*     */         continue;
/*     */       }
/* 163 */       TypeBinding castedExpressionType = ((CastExpression)argument).expression.resolvedType;
/* 164 */       if (castedExpressionType == null) return;
/*     */ 
/* 166 */       if (castedExpressionType == argumentTypes[i]) {
/* 167 */         scope.problemReporter().unnecessaryCast((CastExpression)argument); } else {
/* 168 */         if (castedExpressionType == TypeBinding.NULL)
/*     */           continue;
/* 170 */         if ((argument.implicitConversion & 0x200) != 0) {
/*     */           continue;
/*     */         }
/* 173 */         if (rawArgumentTypes == argumentTypes) {
/* 174 */           System.arraycopy(rawArgumentTypes, 0, rawArgumentTypes = new TypeBinding[length], 0, length);
/*     */         }
/*     */ 
/* 177 */         rawArgumentTypes[i] = castedExpressionType;
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 182 */     if (rawArgumentTypes != argumentTypes)
/* 183 */       checkAlternateBinding(scope, receiver, receiverType, binding, arguments, argumentTypes, rawArgumentTypes, invocationSite);
/*     */   }
/*     */ 
/*     */   public static void checkNeedForArgumentCasts(BlockScope scope, int operator, int operatorSignature, Expression left, int leftTypeId, boolean leftIsCast, Expression right, int rightTypeId, boolean rightIsCast)
/*     */   {
/* 191 */     if (scope.compilerOptions().getSeverity(67108864) == -1) return;
/*     */ 
/* 194 */     int alternateLeftTypeId = leftTypeId;
/* 195 */     if (leftIsCast) {
/* 196 */       if (((left.bits & 0x4000) == 0) && (left.resolvedType.isBaseType()))
/*     */       {
/* 198 */         leftIsCast = false;
/*     */       } else {
/* 200 */         TypeBinding alternateLeftType = ((CastExpression)left).expression.resolvedType;
/* 201 */         if (alternateLeftType == null) return;
/* 202 */         if ((alternateLeftTypeId = alternateLeftType.id) == leftTypeId) {
/* 203 */           scope.problemReporter().unnecessaryCast((CastExpression)left);
/* 204 */           leftIsCast = false;
/* 205 */         } else if (alternateLeftTypeId == 12) {
/* 206 */           alternateLeftTypeId = leftTypeId;
/* 207 */           leftIsCast = false;
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/* 212 */     int alternateRightTypeId = rightTypeId;
/* 213 */     if (rightIsCast) {
/* 214 */       if (((right.bits & 0x4000) == 0) && (right.resolvedType.isBaseType()))
/*     */       {
/* 216 */         rightIsCast = false;
/*     */       } else {
/* 218 */         TypeBinding alternateRightType = ((CastExpression)right).expression.resolvedType;
/* 219 */         if (alternateRightType == null) return;
/* 220 */         if ((alternateRightTypeId = alternateRightType.id) == rightTypeId) {
/* 221 */           scope.problemReporter().unnecessaryCast((CastExpression)right);
/* 222 */           rightIsCast = false;
/* 223 */         } else if (alternateRightTypeId == 12) {
/* 224 */           alternateRightTypeId = rightTypeId;
/* 225 */           rightIsCast = false;
/*     */         }
/*     */       }
/*     */     }
/* 229 */     if ((leftIsCast) || (rightIsCast)) {
/* 230 */       if ((alternateLeftTypeId > 15) || (alternateRightTypeId > 15)) {
/* 231 */         if (alternateLeftTypeId == 11)
/* 232 */           alternateRightTypeId = 1;
/* 233 */         else if (alternateRightTypeId == 11)
/* 234 */           alternateLeftTypeId = 1;
/*     */         else {
/* 236 */           return;
/*     */         }
/*     */       }
/* 239 */       int alternateOperatorSignature = OperatorExpression.OperatorSignatures[operator][((alternateLeftTypeId << 4) + alternateRightTypeId)];
/*     */ 
/* 244 */       if ((operatorSignature & 0xF0F0F) == (alternateOperatorSignature & 0xF0F0F)) {
/* 245 */         if (leftIsCast) scope.problemReporter().unnecessaryCast((CastExpression)left);
/* 246 */         if (rightIsCast) scope.problemReporter().unnecessaryCast((CastExpression)right); 
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   private static void checkAlternateBinding(BlockScope scope, Expression receiver, TypeBinding receiverType, MethodBinding binding, Expression[] arguments, TypeBinding[] originalArgumentTypes, TypeBinding[] alternateArgumentTypes, InvocationSite invocationSite)
/*     */   {
/* 252 */     InvocationSite fakeInvocationSite = new InvocationSite(invocationSite) {
/* 253 */       public TypeBinding[] genericTypeArguments() { return null; } 
/* 254 */       public boolean isSuperAccess() { return CastExpression.this.isSuperAccess(); } 
/* 255 */       public boolean isTypeAccess() { return CastExpression.this.isTypeAccess(); } 
/*     */       public void setActualReceiverType(ReferenceBinding actualReceiverType) {  }
/*     */ 
/*     */       public void setDepth(int depth) {  }
/*     */ 
/*     */       public void setFieldIndex(int depth) {  }
/*     */ 
/* 259 */       public int sourceStart() { return 0; } 
/* 260 */       public int sourceEnd() { return 0;
/*     */       }
/*     */     };
/*     */     MethodBinding bindingIfNoCast;
/*     */     MethodBinding bindingIfNoCast;
/* 263 */     if (binding.isConstructor())
/* 264 */       bindingIfNoCast = scope.getConstructor((ReferenceBinding)receiverType, alternateArgumentTypes, fakeInvocationSite);
/*     */     else {
/* 266 */       bindingIfNoCast = receiver.isImplicitThis() ? 
/* 267 */         scope.getImplicitMethod(binding.selector, alternateArgumentTypes, fakeInvocationSite) : 
/* 268 */         scope.getMethod(receiverType, binding.selector, alternateArgumentTypes, fakeInvocationSite);
/*     */     }
/* 270 */     if (bindingIfNoCast == binding) {
/* 271 */       int argumentLength = originalArgumentTypes.length;
/* 272 */       if (binding.isVarargs()) {
/* 273 */         int paramLength = binding.parameters.length;
/* 274 */         if (paramLength == argumentLength) {
/* 275 */           int varargsIndex = paramLength - 1;
/* 276 */           ArrayBinding varargsType = (ArrayBinding)binding.parameters[varargsIndex];
/* 277 */           TypeBinding lastArgType = alternateArgumentTypes[varargsIndex];
/*     */ 
/* 280 */           if (varargsType.dimensions != lastArgType.dimensions()) {
/* 281 */             return;
/*     */           }
/* 283 */           if ((lastArgType.isCompatibleWith(varargsType.elementsType())) && 
/* 284 */             (lastArgType.isCompatibleWith(varargsType))) {
/* 285 */             return;
/*     */           }
/*     */         }
/*     */       }
/* 289 */       for (int i = 0; i < argumentLength; i++) {
/* 290 */         if (originalArgumentTypes[i] == alternateArgumentTypes[i])
/*     */           continue;
/* 292 */         scope.problemReporter().unnecessaryCast((CastExpression)arguments[i]);
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public boolean checkUnsafeCast(Scope scope, TypeBinding castType, TypeBinding expressionType, TypeBinding match, boolean isNarrowing)
/*     */   {
/* 299 */     if (match == castType) {
/* 300 */       if ((!isNarrowing) && (match == this.resolvedType.leafComponentType())) {
/* 301 */         tagAsUnnecessaryCast(scope, castType);
/*     */       }
/* 303 */       return true;
/*     */     }
/* 305 */     if ((match != null) && 
/* 306 */       (isNarrowing ? 
/* 307 */       match.isProvablyDistinct(expressionType) : 
/* 308 */       castType.isProvablyDistinct(match))) {
/* 309 */       return false;
/*     */     }
/*     */ 
/* 312 */     switch (castType.kind()) {
/*     */     case 260:
/* 314 */       if (!castType.isBoundParameterizedType()) break;
/* 315 */       if (match == null) {
/* 316 */         this.bits |= 128;
/* 317 */         return true;
/*     */       }
/* 319 */       switch (match.kind()) {
/*     */       case 260:
/* 321 */         if (isNarrowing)
/*     */         {
/* 323 */           if ((expressionType.isRawType()) || (!expressionType.isEquivalentTo(match))) {
/* 324 */             this.bits |= 128;
/* 325 */             return true;
/*     */           }
/*     */ 
/* 329 */           ParameterizedTypeBinding paramCastType = (ParameterizedTypeBinding)castType;
/* 330 */           ParameterizedTypeBinding paramMatch = (ParameterizedTypeBinding)match;
/*     */ 
/* 332 */           TypeBinding[] castArguments = paramCastType.arguments;
/* 333 */           int length = castArguments.length;
/* 334 */           if ((paramMatch.arguments == null) || (length > paramMatch.arguments.length))
/* 335 */             this.bits |= 128;
/* 336 */           else if ((paramCastType.tagBits & 0x60000000) != 0L)
/*     */           {
/* 338 */             for (int i = 0; i < length; i++) {
/* 339 */               switch (castArguments[i].kind()) {
/*     */               case 516:
/*     */               case 4100:
/* 342 */                 break;
/*     */               default:
/* 344 */                 break;
/*     */               }
/*     */               TypeBinding[] alternateArguments;
/* 348 */               System.arraycopy(paramCastType.arguments, 0, alternateArguments = new TypeBinding[length], 0, length);
/* 349 */               alternateArguments[i] = scope.getJavaLangObject();
/* 350 */               LookupEnvironment environment = scope.environment();
/* 351 */               ParameterizedTypeBinding alternateCastType = environment.createParameterizedType((ReferenceBinding)castType.erasure(), alternateArguments, castType.enclosingType());
/* 352 */               if (alternateCastType.findSuperTypeOriginatingFrom(expressionType) == match) {
/* 353 */                 this.bits |= 128;
/* 354 */                 break;
/*     */               }
/*     */             }
/*     */           }
/* 358 */           return true;
/*     */         }
/*     */ 
/* 361 */         if (match.isEquivalentTo(castType)) break;
/* 362 */         this.bits |= 128;
/* 363 */         return true;
/*     */       case 1028:
/* 368 */         this.bits |= 128;
/* 369 */         return true;
/*     */       default:
/* 371 */         if (!isNarrowing)
/*     */           break;
/* 373 */         this.bits |= 128;
/* 374 */         return true;
/*     */       }
/*     */ 
/*     */     case 68:
/* 381 */       TypeBinding leafType = castType.leafComponentType();
/* 382 */       if ((!isNarrowing) || ((!leafType.isBoundParameterizedType()) && (!leafType.isTypeVariable()))) break;
/* 383 */       this.bits |= 128;
/* 384 */       return true;
/*     */     case 4100:
/* 388 */       this.bits |= 128;
/* 389 */       return true;
/*     */     }
/*     */ 
/* 398 */     if ((!isNarrowing) && (match == this.resolvedType.leafComponentType())) {
/* 399 */       tagAsUnnecessaryCast(scope, castType);
/*     */     }
/* 401 */     return true;
/*     */   }
/*     */ 
/*     */   public void generateCode(BlockScope currentScope, CodeStream codeStream, boolean valueRequired)
/*     */   {
/* 412 */     int pc = codeStream.position;
/* 413 */     boolean needRuntimeCheckcast = (this.bits & 0x40) != 0;
/* 414 */     if (this.constant != Constant.NotAConstant) {
/* 415 */       if ((valueRequired) || (needRuntimeCheckcast)) {
/* 416 */         codeStream.generateConstant(this.constant, this.implicitConversion);
/* 417 */         if (needRuntimeCheckcast) {
/* 418 */           codeStream.checkcast(this.resolvedType);
/*     */         }
/* 420 */         if (!valueRequired)
/*     */         {
/* 422 */           codeStream.pop();
/*     */         }
/*     */       }
/* 425 */       codeStream.recordPositionsFrom(pc, this.sourceStart);
/* 426 */       return;
/*     */     }
/* 428 */     this.expression.generateCode(currentScope, codeStream, (valueRequired) || (needRuntimeCheckcast));
/* 429 */     if ((needRuntimeCheckcast) && (this.expression.postConversionType(currentScope) != this.resolvedType.erasure())) {
/* 430 */       codeStream.checkcast(this.resolvedType);
/*     */     }
/* 432 */     if (valueRequired)
/* 433 */       codeStream.generateImplicitConversion(this.implicitConversion);
/* 434 */     else if (needRuntimeCheckcast) {
/* 435 */       codeStream.pop();
/*     */     }
/* 437 */     codeStream.recordPositionsFrom(pc, this.sourceStart);
/*     */   }
/*     */ 
/*     */   public Expression innermostCastedExpression() {
/* 441 */     Expression current = this.expression;
/* 442 */     while ((current instanceof CastExpression)) {
/* 443 */       current = ((CastExpression)current).expression;
/*     */     }
/* 445 */     return current;
/*     */   }
/*     */ 
/*     */   public LocalVariableBinding localVariableBinding()
/*     */   {
/* 452 */     return this.expression.localVariableBinding();
/*     */   }
/*     */ 
/*     */   public int nullStatus(FlowInfo flowInfo) {
/* 456 */     return this.expression.nullStatus(flowInfo);
/*     */   }
/*     */ 
/*     */   public Constant optimizedBooleanConstant()
/*     */   {
/* 463 */     switch (this.resolvedType.id) {
/*     */     case 5:
/*     */     case 33:
/* 466 */       return this.expression.optimizedBooleanConstant();
/*     */     }
/* 468 */     return Constant.NotAConstant;
/*     */   }
/*     */ 
/*     */   public StringBuffer printExpression(int indent, StringBuffer output) {
/* 472 */     output.append('(');
/* 473 */     this.type.print(0, output).append(") ");
/* 474 */     return this.expression.printExpression(0, output);
/*     */   }
/*     */ 
/*     */   public TypeBinding resolveType(BlockScope scope)
/*     */   {
/* 484 */     this.constant = Constant.NotAConstant;
/* 485 */     this.implicitConversion = 0;
/*     */ 
/* 487 */     if (((this.type instanceof TypeReference)) || (((this.type instanceof NameReference)) && 
/* 488 */       ((this.type.bits & 0x1FE00000) >> 21 == 0)))
/*     */     {
/* 490 */       boolean exprContainCast = false;
/*     */ 
/* 492 */       TypeBinding castType = this.resolvedType = this.type.resolveType(scope);
/*     */ 
/* 494 */       if ((this.expression instanceof CastExpression)) {
/* 495 */         this.expression.bits |= 32;
/* 496 */         exprContainCast = true;
/*     */       }
/* 498 */       TypeBinding expressionType = this.expression.resolveType(scope);
/* 499 */       if (castType != null) {
/* 500 */         if (expressionType != null) {
/* 501 */           boolean isLegal = checkCastTypesCompatibility(scope, castType, expressionType, this.expression);
/* 502 */           if (isLegal) {
/* 503 */             this.expression.computeConversion(scope, castType, expressionType);
/* 504 */             if ((this.bits & 0x80) != 0) {
/* 505 */               scope.problemReporter().unsafeCast(this, scope);
/*     */             } else {
/* 507 */               if ((castType.isRawType()) && (scope.compilerOptions().getSeverity(536936448) != -1)) {
/* 508 */                 scope.problemReporter().rawTypeReference(this.type, castType);
/*     */               }
/* 510 */               if (((this.bits & 0x4020) == 16384) && 
/* 511 */                 (!isIndirectlyUsed()))
/* 512 */                 scope.problemReporter().unnecessaryCast(this);
/*     */             }
/*     */           }
/*     */           else {
/* 516 */             if ((castType.tagBits & 0x80) == 0L) {
/* 517 */               scope.problemReporter().typeCastError(this, castType, expressionType);
/*     */             }
/* 519 */             this.bits |= 32;
/*     */           }
/*     */         }
/* 522 */         this.resolvedType = castType.capture(scope, this.sourceEnd);
/* 523 */         if (exprContainCast) {
/* 524 */           checkNeedForCastCast(scope, this);
/*     */         }
/*     */       }
/* 527 */       return this.resolvedType;
/*     */     }
/* 529 */     TypeBinding expressionType = this.expression.resolveType(scope);
/* 530 */     if (expressionType == null) return null;
/* 531 */     scope.problemReporter().invalidTypeReference(this.type);
/* 532 */     return null;
/*     */   }
/*     */ 
/*     */   public void setExpectedType(TypeBinding expectedType)
/*     */   {
/* 540 */     this.expectedType = expectedType;
/*     */   }
/*     */ 
/*     */   private boolean isIndirectlyUsed()
/*     */   {
/* 548 */     if ((this.expression instanceof MessageSend)) {
/* 549 */       MethodBinding method = ((MessageSend)this.expression).binding;
/* 550 */       if (((method instanceof ParameterizedGenericMethodBinding)) && 
/* 551 */         (((ParameterizedGenericMethodBinding)method).inferredReturnType)) {
/* 552 */         if (this.expectedType == null)
/* 553 */           return true;
/* 554 */         if (this.resolvedType != this.expectedType) {
/* 555 */           return true;
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/* 560 */     return (this.expectedType != null) && (this.resolvedType.isBaseType()) && (!this.resolvedType.isCompatibleWith(this.expectedType));
/*     */   }
/*     */ 
/*     */   public void tagAsNeedCheckCast()
/*     */   {
/* 569 */     this.bits |= 64;
/*     */   }
/*     */ 
/*     */   public void tagAsUnnecessaryCast(Scope scope, TypeBinding castType)
/*     */   {
/* 576 */     this.bits |= 16384;
/*     */   }
/*     */ 
/*     */   public void traverse(ASTVisitor visitor, BlockScope blockScope) {
/* 580 */     if (visitor.visit(this, blockScope)) {
/* 581 */       this.type.traverse(visitor, blockScope);
/* 582 */       this.expression.traverse(visitor, blockScope);
/*     */     }
/* 584 */     visitor.endVisit(this, blockScope);
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\ecj-3.5.2.jar
 * Qualified Name:     org.eclipse.jdt.internal.compiler.ast.CastExpression
 * JD-Core Version:    0.6.0
 */