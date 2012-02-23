/*     */ package org.eclipse.jdt.internal.compiler.ast;
/*     */ 
/*     */ import org.eclipse.jdt.core.compiler.CharOperation;
/*     */ import org.eclipse.jdt.internal.compiler.ASTVisitor;
/*     */ import org.eclipse.jdt.internal.compiler.env.AccessRestriction;
/*     */ import org.eclipse.jdt.internal.compiler.impl.CompilerOptions;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.AnnotationBinding;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.ArrayBinding;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.Binding;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.BlockScope;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.FieldBinding;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.InvocationSite;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.LocalVariableBinding;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.LookupEnvironment;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.MethodBinding;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.PackageBinding;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.ParameterizedGenericMethodBinding;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.ReferenceBinding;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.Scope;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.TypeConstants;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.TypeIds;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.WildcardBinding;
/*     */ import org.eclipse.jdt.internal.compiler.problem.ProblemReporter;
/*     */ 
/*     */ public abstract class ASTNode
/*     */   implements TypeConstants, TypeIds
/*     */ {
/*     */   public int sourceStart;
/*     */   public int sourceEnd;
/*     */   public static final int Bit1 = 1;
/*     */   public static final int Bit2 = 2;
/*     */   public static final int Bit3 = 4;
/*     */   public static final int Bit4 = 8;
/*     */   public static final int Bit5 = 16;
/*     */   public static final int Bit6 = 32;
/*     */   public static final int Bit7 = 64;
/*     */   public static final int Bit8 = 128;
/*     */   public static final int Bit9 = 256;
/*     */   public static final int Bit10 = 512;
/*     */   public static final int Bit11 = 1024;
/*     */   public static final int Bit12 = 2048;
/*     */   public static final int Bit13 = 4096;
/*     */   public static final int Bit14 = 8192;
/*     */   public static final int Bit15 = 16384;
/*     */   public static final int Bit16 = 32768;
/*     */   public static final int Bit17 = 65536;
/*     */   public static final int Bit18 = 131072;
/*     */   public static final int Bit19 = 262144;
/*     */   public static final int Bit20 = 524288;
/*     */   public static final int Bit21 = 1048576;
/*     */   public static final int Bit22 = 2097152;
/*     */   public static final int Bit23 = 4194304;
/*     */   public static final int Bit24 = 8388608;
/*     */   public static final int Bit25 = 16777216;
/*     */   public static final int Bit26 = 33554432;
/*     */   public static final int Bit27 = 67108864;
/*     */   public static final int Bit28 = 134217728;
/*     */   public static final int Bit29 = 268435456;
/*     */   public static final int Bit30 = 536870912;
/*     */   public static final int Bit31 = 1073741824;
/*     */   public static final int Bit32 = -2147483648;
/*     */   public static final long Bit32L = 2147483648L;
/*     */   public static final long Bit33L = 4294967296L;
/*     */   public static final long Bit34L = 8589934592L;
/*     */   public static final long Bit35L = 17179869184L;
/*     */   public static final long Bit36L = 34359738368L;
/*     */   public static final long Bit37L = 68719476736L;
/*     */   public static final long Bit38L = 137438953472L;
/*     */   public static final long Bit39L = 274877906944L;
/*     */   public static final long Bit40L = 549755813888L;
/*     */   public static final long Bit41L = 1099511627776L;
/*     */   public static final long Bit42L = 2199023255552L;
/*     */   public static final long Bit43L = 4398046511104L;
/*     */   public static final long Bit44L = 8796093022208L;
/*     */   public static final long Bit45L = 17592186044416L;
/*     */   public static final long Bit46L = 35184372088832L;
/*     */   public static final long Bit47L = 70368744177664L;
/*     */   public static final long Bit48L = 140737488355328L;
/*     */   public static final long Bit49L = 281474976710656L;
/*     */   public static final long Bit50L = 562949953421312L;
/*     */   public static final long Bit51L = 1125899906842624L;
/*     */   public static final long Bit52L = 2251799813685248L;
/*     */   public static final long Bit53L = 4503599627370496L;
/*     */   public static final long Bit54L = 9007199254740992L;
/*     */   public static final long Bit55L = 18014398509481984L;
/*     */   public static final long Bit56L = 36028797018963968L;
/*     */   public static final long Bit57L = 72057594037927936L;
/*     */   public static final long Bit58L = 144115188075855872L;
/*     */   public static final long Bit59L = 288230376151711744L;
/*     */   public static final long Bit60L = 576460752303423488L;
/*     */   public static final long Bit61L = 1152921504606846976L;
/*     */   public static final long Bit62L = 2305843009213693952L;
/*     */   public static final long Bit63L = 4611686018427387904L;
/*     */   public static final long Bit64L = -9223372036854775808L;
/*  93 */   public int bits = -2147483648;
/*     */   public static final int ReturnTypeIDMASK = 15;
/*     */   public static final int OperatorSHIFT = 6;
/*     */   public static final int OperatorMASK = 4032;
/*     */   public static final int IsReturnedValue = 16;
/*     */   public static final int UnnecessaryCast = 16384;
/*     */   public static final int DisableUnnecessaryCastCheck = 32;
/*     */   public static final int GenerateCheckcast = 64;
/*     */   public static final int UnsafeCast = 128;
/*     */   public static final int RestrictiveFlagMASK = 7;
/*     */   public static final int FirstAssignmentToLocal = 8;
/*     */   public static final int NeedReceiverGenericCast = 262144;
/*     */   public static final int IsImplicitThis = 4;
/*     */   public static final int DepthSHIFT = 5;
/*     */   public static final int DepthMASK = 8160;
/*     */   public static final int IsReachable = -2147483648;
/*     */   public static final int LabelUsed = 64;
/*     */   public static final int DocumentedFallthrough = 536870912;
/*     */   public static final int IsLocalDeclarationReachable = 1073741824;
/*     */   public static final int IsSubRoutineEscaping = 16384;
/*     */   public static final int IsTryBlockExiting = 536870912;
/*     */   public static final int ContainsAssertion = 1;
/*     */   public static final int IsLocalType = 256;
/*     */   public static final int IsAnonymousType = 512;
/*     */   public static final int IsMemberType = 1024;
/*     */   public static final int HasAbstractMethods = 2048;
/*     */   public static final int IsSecondaryType = 4096;
/*     */   public static final int HasBeenGenerated = 8192;
/*     */   public static final int HasLocalType = 2;
/*     */   public static final int HasBeenResolved = 16;
/*     */   public static final int ParenthesizedSHIFT = 21;
/*     */   public static final int ParenthesizedMASK = 534773760;
/*     */   public static final int IgnoreNoEffectAssignCheck = 536870912;
/*     */   public static final int IsStrictlyAssigned = 8192;
/*     */   public static final int IsCompoundAssigned = 65536;
/*     */   public static final int DiscardEnclosingInstance = 8192;
/*     */   public static final int Unchecked = 65536;
/*     */   public static final int IsUsefulEmptyStatement = 1;
/*     */   public static final int UndocumentedEmptyBlock = 8;
/*     */   public static final int OverridingMethodWithSupercall = 16;
/*     */   public static final int ErrorInSignature = 32;
/*     */   public static final int NeedFreeReturn = 64;
/*     */   public static final int IsDefaultConstructor = 128;
/*     */   public static final int HasAllMethodBodies = 16;
/*     */   public static final int IsImplicitUnit = 1;
/*     */   public static final int InsideJavadoc = 32768;
/*     */   public static final int SuperAccess = 16384;
/*     */   public static final int Empty = 262144;
/*     */   public static final int IsElseIfStatement = 536870912;
/*     */   public static final int ThenExit = 1073741824;
/*     */   public static final int IsSuperType = 16;
/*     */   public static final int IsVarArgs = 16384;
/*     */   public static final int IgnoreRawTypeCheck = 1073741824;
/*     */   public static final int IsAnnotationDefaultValue = 1;
/*     */   public static final int IsNonNull = 131072;
/*     */   public static final int NeededScope = 536870912;
/*     */   public static final int OnDemand = 131072;
/*     */   public static final int Used = 2;
/*     */   public static final int DidResolve = 262144;
/*     */   public static final int IsAnySubRoutineEscaping = 536870912;
/*     */   public static final int IsSynchronized = 1073741824;
/*     */   public static final int BlockExit = 536870912;
/*     */   public static final int IsRecovered = 32;
/*     */   public static final int HasSyntaxErrors = 524288;
/*     */   public static final int INVOCATION_ARGUMENT_OK = 0;
/*     */   public static final int INVOCATION_ARGUMENT_UNCHECKED = 1;
/*     */   public static final int INVOCATION_ARGUMENT_WILDCARD = 2;
/*     */ 
/*     */   private static int checkInvocationArgument(BlockScope scope, Expression argument, TypeBinding parameterType, TypeBinding argumentType, TypeBinding originalParameterType)
/*     */   {
/* 242 */     argument.computeConversion(scope, parameterType, argumentType);
/*     */ 
/* 244 */     if ((argumentType != TypeBinding.NULL) && (parameterType.kind() == 516)) {
/* 245 */       WildcardBinding wildcard = (WildcardBinding)parameterType;
/* 246 */       if (wildcard.boundKind != 2) {
/* 247 */         return 2;
/*     */       }
/*     */     }
/* 250 */     TypeBinding checkedParameterType = parameterType;
/* 251 */     if ((argumentType != checkedParameterType) && (argumentType.needsUncheckedConversion(checkedParameterType))) {
/* 252 */       scope.problemReporter().unsafeTypeConversion(argument, argumentType, checkedParameterType);
/* 253 */       return 1;
/*     */     }
/* 255 */     return 0;
/*     */   }
/*     */   public static boolean checkInvocationArguments(BlockScope scope, Expression receiver, TypeBinding receiverType, MethodBinding method, Expression[] arguments, TypeBinding[] argumentTypes, boolean argsContainCast, InvocationSite invocationSite) {
/* 258 */     TypeBinding[] params = method.parameters;
/* 259 */     int paramLength = params.length;
/* 260 */     boolean isRawMemberInvocation = (!method.isStatic()) && 
/* 261 */       (!receiverType.isUnboundWildcard()) && 
/* 262 */       (method.declaringClass.isRawType()) && 
/* 263 */       (method.hasSubstitutedParameters());
/*     */ 
/* 265 */     boolean uncheckedBoundCheck = (method.tagBits & 0x100) != 0L;
/* 266 */     MethodBinding rawOriginalGenericMethod = null;
/* 267 */     if ((!isRawMemberInvocation) && 
/* 268 */       ((method instanceof ParameterizedGenericMethodBinding))) {
/* 269 */       ParameterizedGenericMethodBinding paramMethod = (ParameterizedGenericMethodBinding)method;
/* 270 */       if ((paramMethod.isRaw) && (method.hasSubstitutedParameters())) {
/* 271 */         rawOriginalGenericMethod = method.original();
/*     */       }
/*     */     }
/*     */ 
/* 275 */     int invocationStatus = 0;
/* 276 */     if (arguments == null) {
/* 277 */       if (method.isVarargs()) {
/* 278 */         TypeBinding parameterType = ((ArrayBinding)params[(paramLength - 1)]).elementsType();
/* 279 */         if (!parameterType.isReifiable())
/* 280 */           scope.problemReporter().unsafeGenericArrayForVarargs(parameterType, (ASTNode)invocationSite);
/*     */       }
/*     */     }
/*     */     else {
/* 284 */       if (method.isVarargs())
/*     */       {
/* 286 */         int lastIndex = paramLength - 1;
/* 287 */         for (int i = 0; i < lastIndex; i++) {
/* 288 */           TypeBinding originalRawParam = rawOriginalGenericMethod == null ? null : rawOriginalGenericMethod.parameters[i];
/* 289 */           invocationStatus |= checkInvocationArgument(scope, arguments[i], params[i], argumentTypes[i], originalRawParam);
/*     */         }
/* 291 */         int argLength = arguments.length;
/* 292 */         if (lastIndex < argLength) {
/* 293 */           TypeBinding parameterType = params[lastIndex];
/* 294 */           TypeBinding originalRawParam = null;
/*     */ 
/* 296 */           if ((paramLength != argLength) || (parameterType.dimensions() != argumentTypes[lastIndex].dimensions())) {
/* 297 */             parameterType = ((ArrayBinding)parameterType).elementsType();
/* 298 */             if (!parameterType.isReifiable()) {
/* 299 */               scope.problemReporter().unsafeGenericArrayForVarargs(parameterType, (ASTNode)invocationSite);
/*     */             }
/* 301 */             originalRawParam = rawOriginalGenericMethod == null ? null : ((ArrayBinding)rawOriginalGenericMethod.parameters[lastIndex]).elementsType();
/*     */           }
/* 303 */           for (int i = lastIndex; i < argLength; i++) {
/* 304 */             invocationStatus |= checkInvocationArgument(scope, arguments[i], parameterType, argumentTypes[i], originalRawParam);
/*     */           }
/*     */         }
/* 307 */         if (paramLength == argLength) {
/* 308 */           int varargsIndex = paramLength - 1;
/* 309 */           ArrayBinding varargsType = (ArrayBinding)params[varargsIndex];
/* 310 */           TypeBinding lastArgType = argumentTypes[varargsIndex];
/*     */ 
/* 312 */           if (lastArgType == TypeBinding.NULL) {
/* 313 */             if ((!varargsType.leafComponentType().isBaseType()) || (varargsType.dimensions() != 1))
/* 314 */               scope.problemReporter().varargsArgumentNeedCast(method, lastArgType, invocationSite);
/*     */           }
/*     */           else
/*     */           {
/*     */             int dimensions;
/* 315 */             if (varargsType.dimensions <= (dimensions = lastArgType.dimensions())) {
/* 316 */               if (lastArgType.leafComponentType().isBaseType()) {
/* 317 */                 dimensions--;
/*     */               }
/* 319 */               if (varargsType.dimensions < dimensions)
/* 320 */                 scope.problemReporter().varargsArgumentNeedCast(method, lastArgType, invocationSite);
/* 321 */               else if ((varargsType.dimensions == dimensions) && 
/* 322 */                 (lastArgType != varargsType) && 
/* 323 */                 (lastArgType.leafComponentType().erasure() != varargsType.leafComponentType.erasure()) && 
/* 324 */                 (lastArgType.isCompatibleWith(varargsType.elementsType())) && 
/* 325 */                 (lastArgType.isCompatibleWith(varargsType)))
/* 326 */                 scope.problemReporter().varargsArgumentNeedCast(method, lastArgType, invocationSite);
/*     */             }
/*     */           }
/*     */         }
/*     */       } else {
/* 331 */         for (int i = 0; i < paramLength; i++) {
/* 332 */           TypeBinding originalRawParam = rawOriginalGenericMethod == null ? null : rawOriginalGenericMethod.parameters[i];
/* 333 */           invocationStatus |= checkInvocationArgument(scope, arguments[i], params[i], argumentTypes[i], originalRawParam);
/*     */         }
/*     */       }
/* 336 */       if (argsContainCast) {
/* 337 */         CastExpression.checkNeedForArgumentCasts(scope, receiver, receiverType, method, arguments, argumentTypes, invocationSite);
/*     */       }
/*     */     }
/* 340 */     if ((invocationStatus & 0x2) != 0) {
/* 341 */       scope.problemReporter().wildcardInvocation((ASTNode)invocationSite, receiverType, method, argumentTypes);
/* 342 */     } else if ((!method.isStatic()) && (!receiverType.isUnboundWildcard()) && (method.declaringClass.isRawType()) && (method.hasSubstitutedParameters())) {
/* 343 */       scope.problemReporter().unsafeRawInvocation((ASTNode)invocationSite, method);
/* 344 */     } else if ((rawOriginalGenericMethod != null) || 
/* 345 */       (uncheckedBoundCheck) || (
/* 346 */       ((invocationStatus & 0x1) != 0) && 
/* 347 */       ((method instanceof ParameterizedGenericMethodBinding))))
/*     */     {
/* 349 */       scope.problemReporter().unsafeRawGenericMethodInvocation((ASTNode)invocationSite, method, argumentTypes);
/* 350 */       return true;
/*     */     }
/* 352 */     return false;
/*     */   }
/*     */   public ASTNode concreteStatement() {
/* 355 */     return this;
/*     */   }
/*     */ 
/*     */   public final boolean isFieldUseDeprecated(FieldBinding field, Scope scope, boolean isStrictlyAssigned)
/*     */   {
/* 360 */     if (((this.bits & 0x8000) == 0) && (!isStrictlyAssigned) && (field.isOrEnclosedByPrivateType()) && (!scope.isDefinedInField(field)))
/*     */     {
/* 362 */       field.original().modifiers |= 134217728;
/*     */     }
/*     */ 
/* 365 */     if ((field.modifiers & 0x40000) != 0) {
/* 366 */       AccessRestriction restriction = 
/* 367 */         scope.environment().getAccessRestriction(field.declaringClass.erasure());
/* 368 */       if (restriction != null) {
/* 369 */         scope.problemReporter().forbiddenReference(field, this, 
/* 370 */           restriction.classpathEntryType, restriction.classpathEntryName, 
/* 371 */           restriction.getProblemId());
/*     */       }
/*     */     }
/*     */ 
/* 375 */     if (!field.isViewedAsDeprecated()) return false;
/*     */ 
/* 378 */     if (scope.isDefinedInSameUnit(field.declaringClass)) return false;
/*     */ 
/* 381 */     return (scope.compilerOptions().reportDeprecationInsideDeprecatedCode) || (!scope.isInsideDeprecatedCode());
/*     */   }
/*     */ 
/*     */   public boolean isImplicitThis()
/*     */   {
/* 387 */     return false;
/*     */   }
/*     */ 
/*     */   public final boolean isMethodUseDeprecated(MethodBinding method, Scope scope, boolean isExplicitUse)
/*     */   {
/* 396 */     if (((this.bits & 0x8000) == 0) && (method.isOrEnclosedByPrivateType()) && (!scope.isDefinedInMethod(method)))
/*     */     {
/* 398 */       method.original().modifiers |= 134217728;
/*     */     }
/*     */ 
/* 403 */     if ((isExplicitUse) && ((method.modifiers & 0x40000) != 0))
/*     */     {
/* 406 */       AccessRestriction restriction = 
/* 407 */         scope.environment().getAccessRestriction(method.declaringClass.erasure());
/* 408 */       if (restriction != null) {
/* 409 */         scope.problemReporter().forbiddenReference(method, this, 
/* 410 */           restriction.classpathEntryType, restriction.classpathEntryName, 
/* 411 */           restriction.getProblemId());
/*     */       }
/*     */     }
/*     */ 
/* 415 */     if (!method.isViewedAsDeprecated()) return false;
/*     */ 
/* 418 */     if (scope.isDefinedInSameUnit(method.declaringClass)) return false;
/*     */ 
/* 421 */     if ((!isExplicitUse) && 
/* 422 */       ((method.modifiers & 0x100000) == 0)) {
/* 423 */       return false;
/*     */     }
/*     */ 
/* 427 */     return (scope.compilerOptions().reportDeprecationInsideDeprecatedCode) || (!scope.isInsideDeprecatedCode());
/*     */   }
/*     */ 
/*     */   public boolean isSuper()
/*     */   {
/* 433 */     return false;
/*     */   }
/*     */ 
/*     */   public boolean isThis()
/*     */   {
/* 438 */     return false;
/*     */   }
/*     */ 
/*     */   public final boolean isTypeUseDeprecated(TypeBinding type, Scope scope)
/*     */   {
/* 446 */     if (type.isArrayType()) {
/* 447 */       type = ((ArrayBinding)type).leafComponentType;
/*     */     }
/* 449 */     if (type.isBaseType()) {
/* 450 */       return false;
/*     */     }
/* 452 */     ReferenceBinding refType = (ReferenceBinding)type;
/*     */ 
/* 454 */     if (((this.bits & 0x8000) == 0) && (refType.isOrEnclosedByPrivateType()) && (!scope.isDefinedInType(refType)))
/*     */     {
/* 456 */       ((ReferenceBinding)refType.erasure()).modifiers |= 134217728;
/*     */     }
/*     */ 
/* 459 */     if (refType.hasRestrictedAccess()) {
/* 460 */       AccessRestriction restriction = scope.environment().getAccessRestriction(type.erasure());
/* 461 */       if (restriction != null) {
/* 462 */         scope.problemReporter().forbiddenReference(type, this, restriction.classpathEntryType, 
/* 463 */           restriction.classpathEntryName, restriction.getProblemId());
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 468 */     refType.initializeDeprecatedAnnotationTagBits();
/*     */ 
/* 470 */     if (!refType.isViewedAsDeprecated()) return false;
/*     */ 
/* 473 */     if (scope.isDefinedInSameUnit(refType)) return false;
/*     */ 
/* 476 */     return (scope.compilerOptions().reportDeprecationInsideDeprecatedCode) || (!scope.isInsideDeprecatedCode());
/*     */   }
/*     */ 
/*     */   public abstract StringBuffer print(int paramInt, StringBuffer paramStringBuffer);
/*     */ 
/*     */   public static StringBuffer printAnnotations(Annotation[] annotations, StringBuffer output) {
/* 483 */     int length = annotations.length;
/* 484 */     for (int i = 0; i < length; i++) {
/* 485 */       annotations[i].print(0, output);
/* 486 */       output.append(" ");
/*     */     }
/* 488 */     return output;
/*     */   }
/*     */ 
/*     */   public static StringBuffer printIndent(int indent, StringBuffer output)
/*     */   {
/* 493 */     for (int i = indent; i > 0; i--) output.append("  ");
/* 494 */     return output;
/*     */   }
/*     */ 
/*     */   public static StringBuffer printModifiers(int modifiers, StringBuffer output)
/*     */   {
/* 499 */     if ((modifiers & 0x1) != 0)
/* 500 */       output.append("public ");
/* 501 */     if ((modifiers & 0x2) != 0)
/* 502 */       output.append("private ");
/* 503 */     if ((modifiers & 0x4) != 0)
/* 504 */       output.append("protected ");
/* 505 */     if ((modifiers & 0x8) != 0)
/* 506 */       output.append("static ");
/* 507 */     if ((modifiers & 0x10) != 0)
/* 508 */       output.append("final ");
/* 509 */     if ((modifiers & 0x20) != 0)
/* 510 */       output.append("synchronized ");
/* 511 */     if ((modifiers & 0x40) != 0)
/* 512 */       output.append("volatile ");
/* 513 */     if ((modifiers & 0x80) != 0)
/* 514 */       output.append("transient ");
/* 515 */     if ((modifiers & 0x100) != 0)
/* 516 */       output.append("native ");
/* 517 */     if ((modifiers & 0x400) != 0)
/* 518 */       output.append("abstract ");
/* 519 */     return output;
/*     */   }
/*     */ 
/*     */   public static void resolveAnnotations(BlockScope scope, Annotation[] sourceAnnotations, Binding recipient)
/*     */   {
/* 527 */     AnnotationBinding[] annotations = (AnnotationBinding[])null;
/* 528 */     int length = sourceAnnotations == null ? 0 : sourceAnnotations.length;
/* 529 */     if (recipient != null) {
/* 530 */       switch (recipient.kind()) {
/*     */       case 16:
/* 532 */         PackageBinding packageBinding = (PackageBinding)recipient;
/* 533 */         if ((packageBinding.tagBits & 0x0) != 0L) return;
/* 534 */         packageBinding.tagBits |= 25769803776L;
/* 535 */         break;
/*     */       case 4:
/*     */       case 2052:
/* 538 */         ReferenceBinding type = (ReferenceBinding)recipient;
/* 539 */         if ((type.tagBits & 0x0) != 0L) return;
/* 540 */         type.tagBits |= 25769803776L;
/* 541 */         if (length <= 0) break;
/* 542 */         annotations = new AnnotationBinding[length];
/* 543 */         type.setAnnotations(annotations);
/*     */ 
/* 545 */         break;
/*     */       case 8:
/* 547 */         MethodBinding method = (MethodBinding)recipient;
/* 548 */         if ((method.tagBits & 0x0) != 0L) return;
/* 549 */         method.tagBits |= 25769803776L;
/* 550 */         if (length <= 0) break;
/* 551 */         annotations = new AnnotationBinding[length];
/* 552 */         method.setAnnotations(annotations);
/*     */ 
/* 554 */         break;
/*     */       case 1:
/* 556 */         FieldBinding field = (FieldBinding)recipient;
/* 557 */         if ((field.tagBits & 0x0) != 0L) return;
/* 558 */         field.tagBits |= 25769803776L;
/* 559 */         if (length <= 0) break;
/* 560 */         annotations = new AnnotationBinding[length];
/* 561 */         field.setAnnotations(annotations);
/*     */ 
/* 563 */         break;
/*     */       case 2:
/* 565 */         LocalVariableBinding local = (LocalVariableBinding)recipient;
/* 566 */         if ((local.tagBits & 0x0) != 0L) return;
/* 567 */         local.tagBits |= 25769803776L;
/* 568 */         if (length <= 0) break;
/* 569 */         annotations = new AnnotationBinding[length];
/* 570 */         local.setAnnotations(annotations);
/*     */ 
/* 572 */         break;
/*     */       default:
/* 574 */         return;
/*     */       }
/*     */     }
/* 577 */     if (sourceAnnotations == null)
/* 578 */       return;
/* 579 */     for (int i = 0; i < length; i++) {
/* 580 */       Annotation annotation = sourceAnnotations[i];
/* 581 */       Binding annotationRecipient = annotation.recipient;
/* 582 */       if ((annotationRecipient != null) && (recipient != null))
/*     */       {
/* 584 */         switch (recipient.kind()) {
/*     */         case 1:
/* 586 */           FieldBinding field = (FieldBinding)recipient;
/* 587 */           field.tagBits = ((FieldBinding)annotationRecipient).tagBits;
/* 588 */           break;
/*     */         case 2:
/* 590 */           LocalVariableBinding local = (LocalVariableBinding)recipient;
/* 591 */           local.tagBits = ((LocalVariableBinding)annotationRecipient).tagBits;
/*     */         }
/*     */ 
/* 594 */         if (annotations != null)
/*     */         {
/* 596 */           annotations[0] = annotation.getCompilerAnnotation();
/* 597 */           for (int j = 1; j < length; j++) {
/* 598 */             Annotation annot = sourceAnnotations[j];
/* 599 */             annotations[j] = annot.getCompilerAnnotation();
/*     */           }
/*     */         }
/* 602 */         return;
/*     */       }
/* 604 */       annotation.recipient = recipient;
/* 605 */       annotation.resolveType(scope);
/*     */ 
/* 607 */       if (annotations != null) {
/* 608 */         annotations[i] = annotation.getCompilerAnnotation();
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 613 */     if (annotations != null) {
/* 614 */       AnnotationBinding[] distinctAnnotations = annotations;
/* 615 */       for (int i = 0; i < length; i++) {
/* 616 */         AnnotationBinding annotation = distinctAnnotations[i];
/* 617 */         if (annotation != null) {
/* 618 */           TypeBinding annotationType = annotation.getAnnotationType();
/* 619 */           boolean foundDuplicate = false;
/* 620 */           for (int j = i + 1; j < length; j++) {
/* 621 */             AnnotationBinding otherAnnotation = distinctAnnotations[j];
/* 622 */             if ((otherAnnotation == null) || 
/* 623 */               (otherAnnotation.getAnnotationType() != annotationType)) continue;
/* 624 */             foundDuplicate = true;
/* 625 */             if (distinctAnnotations == annotations) {
/* 626 */               System.arraycopy(distinctAnnotations, 0, distinctAnnotations = new AnnotationBinding[length], 0, length);
/*     */             }
/* 628 */             distinctAnnotations[j] = null;
/* 629 */             scope.problemReporter().duplicateAnnotation(sourceAnnotations[j]);
/*     */           }
/*     */ 
/* 632 */           if (foundDuplicate)
/* 633 */             scope.problemReporter().duplicateAnnotation(sourceAnnotations[i]);
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public static void resolveDeprecatedAnnotations(BlockScope scope, Annotation[] annotations, Binding recipient)
/*     */   {
/* 643 */     if (recipient != null) {
/* 644 */       int kind = recipient.kind();
/* 645 */       if (annotations != null)
/*     */       {
/*     */         int length;
/* 647 */         if ((length = annotations.length) >= 0) {
/* 648 */           switch (kind) {
/*     */           case 16:
/* 650 */             PackageBinding packageBinding = (PackageBinding)recipient;
/* 651 */             if ((packageBinding.tagBits & 0x0) == 0L) break; return;
/*     */           case 4:
/*     */           case 2052:
/* 655 */             ReferenceBinding type = (ReferenceBinding)recipient;
/* 656 */             if ((type.tagBits & 0x0) == 0L) break; return;
/*     */           case 8:
/* 659 */             MethodBinding method = (MethodBinding)recipient;
/* 660 */             if ((method.tagBits & 0x0) == 0L) break; return;
/*     */           case 1:
/* 663 */             FieldBinding field = (FieldBinding)recipient;
/* 664 */             if ((field.tagBits & 0x0) == 0L) break; return;
/*     */           case 2:
/* 667 */             LocalVariableBinding local = (LocalVariableBinding)recipient;
/* 668 */             if ((local.tagBits & 0x0) == 0L) break; return;
/*     */           default:
/* 671 */             return;
/*     */           }
/* 673 */           for (int i = 0; i < length; i++) {
/* 674 */             TypeReference annotationTypeRef = annotations[i].type;
/*     */ 
/* 676 */             if (!CharOperation.equals(TypeIds.JAVA_LANG_DEPRECATED[2], annotationTypeRef.getLastToken())) return;
/* 677 */             TypeBinding annotationType = annotations[i].type.resolveType(scope);
/* 678 */             if ((annotationType != null) && (annotationType.isValidBinding()) && (annotationType.id == 44)) {
/* 679 */               switch (kind) {
/*     */               case 16:
/* 681 */                 PackageBinding packageBinding = (PackageBinding)recipient;
/* 682 */                 packageBinding.tagBits |= 70385924046848L;
/* 683 */                 return;
/*     */               case 4:
/*     */               case 2052:
/*     */               case 4100:
/* 687 */                 ReferenceBinding type = (ReferenceBinding)recipient;
/* 688 */                 type.tagBits |= 70385924046848L;
/* 689 */                 return;
/*     */               case 8:
/* 691 */                 MethodBinding method = (MethodBinding)recipient;
/* 692 */                 method.tagBits |= 70385924046848L;
/* 693 */                 return;
/*     */               case 1:
/* 695 */                 FieldBinding field = (FieldBinding)recipient;
/* 696 */                 field.tagBits |= 70385924046848L;
/* 697 */                 return;
/*     */               case 2:
/* 699 */                 LocalVariableBinding local = (LocalVariableBinding)recipient;
/* 700 */                 local.tagBits |= 70385924046848L;
/* 701 */                 return;
/*     */               }
/* 703 */               return;
/*     */             }
/*     */           }
/*     */         }
/*     */       }
/*     */ 
/* 709 */       switch (kind) {
/*     */       case 16:
/* 711 */         PackageBinding packageBinding = (PackageBinding)recipient;
/* 712 */         packageBinding.tagBits |= 17179869184L;
/* 713 */         return;
/*     */       case 4:
/*     */       case 2052:
/*     */       case 4100:
/* 717 */         ReferenceBinding type = (ReferenceBinding)recipient;
/* 718 */         type.tagBits |= 17179869184L;
/* 719 */         return;
/*     */       case 8:
/* 721 */         MethodBinding method = (MethodBinding)recipient;
/* 722 */         method.tagBits |= 17179869184L;
/* 723 */         return;
/*     */       case 1:
/* 725 */         FieldBinding field = (FieldBinding)recipient;
/* 726 */         field.tagBits |= 17179869184L;
/* 727 */         return;
/*     */       case 2:
/* 729 */         LocalVariableBinding local = (LocalVariableBinding)recipient;
/* 730 */         local.tagBits |= 17179869184L;
/* 731 */         return;
/*     */       }
/* 733 */       return;
/*     */     }
/*     */   }
/*     */ 
/*     */   public int sourceStart()
/*     */   {
/* 739 */     return this.sourceStart;
/*     */   }
/*     */   public int sourceEnd() {
/* 742 */     return this.sourceEnd;
/*     */   }
/*     */ 
/*     */   public String toString() {
/* 746 */     return print(0, new StringBuffer(30)).toString();
/*     */   }
/*     */ 
/*     */   public void traverse(ASTVisitor visitor, BlockScope scope)
/*     */   {
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\ecj-3.5.2.jar
 * Qualified Name:     org.eclipse.jdt.internal.compiler.ast.ASTNode
 * JD-Core Version:    0.6.0
 */