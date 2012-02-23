/*     */ package org.eclipse.jdt.internal.compiler.lookup;
/*     */ 
/*     */ import org.eclipse.jdt.internal.compiler.ast.ASTNode;
/*     */ import org.eclipse.jdt.internal.compiler.ast.AbstractMethodDeclaration;
/*     */ import org.eclipse.jdt.internal.compiler.ast.Argument;
/*     */ import org.eclipse.jdt.internal.compiler.ast.CompilationUnitDeclaration;
/*     */ import org.eclipse.jdt.internal.compiler.ast.ConstructorDeclaration;
/*     */ import org.eclipse.jdt.internal.compiler.ast.LocalDeclaration;
/*     */ import org.eclipse.jdt.internal.compiler.ast.QualifiedNameReference;
/*     */ import org.eclipse.jdt.internal.compiler.ast.SingleNameReference;
/*     */ import org.eclipse.jdt.internal.compiler.ast.TypeDeclaration;
/*     */ import org.eclipse.jdt.internal.compiler.ast.TypeParameter;
/*     */ import org.eclipse.jdt.internal.compiler.codegen.CodeStream;
/*     */ import org.eclipse.jdt.internal.compiler.flow.FlowInfo;
/*     */ import org.eclipse.jdt.internal.compiler.flow.UnconditionalFlowInfo;
/*     */ import org.eclipse.jdt.internal.compiler.impl.CompilerOptions;
/*     */ import org.eclipse.jdt.internal.compiler.impl.ReferenceContext;
/*     */ import org.eclipse.jdt.internal.compiler.problem.ProblemReporter;
/*     */ 
/*     */ public class MethodScope extends BlockScope
/*     */ {
/*     */   public ReferenceContext referenceContext;
/*     */   public boolean isStatic;
/*  32 */   public boolean isConstructorCall = false;
/*     */   public FieldBinding initializedField;
/*  34 */   public int lastVisibleFieldID = -1;
/*     */   public int analysisIndex;
/*     */   public boolean isPropagatingInnerClassEmulation;
/*  42 */   public int lastIndex = 0;
/*  43 */   public long[] definiteInits = new long[4];
/*  44 */   public long[][] extraDefiniteInits = new long[4][];
/*     */ 
/*  47 */   public boolean insideTypeAnnotation = false;
/*     */   public SyntheticArgumentBinding[] extraSyntheticArguments;
/*     */ 
/*     */   public MethodScope(ClassScope parent, ReferenceContext context, boolean isStatic)
/*     */   {
/*  53 */     super(2, parent);
/*  54 */     this.locals = new LocalVariableBinding[5];
/*  55 */     this.referenceContext = context;
/*  56 */     this.isStatic = isStatic;
/*  57 */     this.startIndex = 0;
/*     */   }
/*     */ 
/*     */   String basicToString(int tab) {
/*  61 */     String newLine = "\n";
/*  62 */     int i = tab;
/*     */     do { newLine = newLine + "\t";
/*     */ 
/*  62 */       i--; } while (i >= 0);
/*     */ 
/*  65 */     String s = newLine + "--- Method Scope ---";
/*  66 */     newLine = newLine + "\t";
/*  67 */     s = s + newLine + "locals:";
/*  68 */     for (int i = 0; i < this.localIndex; i++)
/*  69 */       s = s + newLine + "\t" + this.locals[i].toString();
/*  70 */     s = s + newLine + "startIndex = " + this.startIndex;
/*  71 */     s = s + newLine + "isConstructorCall = " + this.isConstructorCall;
/*  72 */     s = s + newLine + "initializedField = " + this.initializedField;
/*  73 */     s = s + newLine + "lastVisibleFieldID = " + this.lastVisibleFieldID;
/*  74 */     s = s + newLine + "referenceContext = " + this.referenceContext;
/*  75 */     return s;
/*     */   }
/*     */ 
/*     */   private void checkAndSetModifiersForConstructor(MethodBinding methodBinding)
/*     */   {
/*  82 */     int modifiers = methodBinding.modifiers;
/*  83 */     ReferenceBinding declaringClass = methodBinding.declaringClass;
/*  84 */     if ((modifiers & 0x400000) != 0) {
/*  85 */       problemReporter().duplicateModifierForMethod(declaringClass, (AbstractMethodDeclaration)this.referenceContext);
/*     */     }
/*  87 */     if ((((ConstructorDeclaration)this.referenceContext).bits & 0x80) != 0)
/*     */     {
/*     */       int flags;
/*  92 */       if ((flags = declaringClass.modifiers & 0x4005) != 0) {
/*  93 */         if ((flags & 0x4000) != 0) {
/*  94 */           modifiers &= -8;
/*  95 */           modifiers |= 2;
/*     */         } else {
/*  97 */           modifiers &= -8;
/*  98 */           modifiers |= flags;
/*     */         }
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 104 */     int realModifiers = modifiers & 0xFFFF;
/*     */ 
/* 108 */     if ((declaringClass.isEnum()) && ((((ConstructorDeclaration)this.referenceContext).bits & 0x80) == 0))
/*     */     {
/* 110 */       if ((realModifiers & 0xFFFFF7FD) != 0) {
/* 111 */         problemReporter().illegalModifierForEnumConstructor((AbstractMethodDeclaration)this.referenceContext);
/* 112 */         modifiers &= -63486;
/* 113 */       } else if ((((AbstractMethodDeclaration)this.referenceContext).modifiers & 0x800) != 0)
/*     */       {
/* 115 */         problemReporter().illegalModifierForMethod((AbstractMethodDeclaration)this.referenceContext);
/*     */       }
/* 117 */       modifiers |= 2;
/* 118 */     } else if ((realModifiers & 0xFFFFF7F8) != 0) {
/* 119 */       problemReporter().illegalModifierForMethod((AbstractMethodDeclaration)this.referenceContext);
/* 120 */       modifiers &= -63481;
/* 121 */     } else if ((((AbstractMethodDeclaration)this.referenceContext).modifiers & 0x800) != 0)
/*     */     {
/* 123 */       problemReporter().illegalModifierForMethod((AbstractMethodDeclaration)this.referenceContext);
/*     */     }
/*     */ 
/* 127 */     int accessorBits = realModifiers & 0x7;
/* 128 */     if ((accessorBits & accessorBits - 1) != 0) {
/* 129 */       problemReporter().illegalVisibilityModifierCombinationForMethod(declaringClass, (AbstractMethodDeclaration)this.referenceContext);
/*     */ 
/* 132 */       if ((accessorBits & 0x1) != 0) {
/* 133 */         if ((accessorBits & 0x4) != 0)
/* 134 */           modifiers &= -5;
/* 135 */         if ((accessorBits & 0x2) != 0)
/* 136 */           modifiers &= -3;
/* 137 */       } else if (((accessorBits & 0x4) != 0) && ((accessorBits & 0x2) != 0)) {
/* 138 */         modifiers &= -3;
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 146 */     methodBinding.modifiers = modifiers;
/*     */   }
/*     */ 
/*     */   private void checkAndSetModifiersForMethod(MethodBinding methodBinding)
/*     */   {
/* 153 */     int modifiers = methodBinding.modifiers;
/* 154 */     ReferenceBinding declaringClass = methodBinding.declaringClass;
/* 155 */     if ((modifiers & 0x400000) != 0) {
/* 156 */       problemReporter().duplicateModifierForMethod(declaringClass, (AbstractMethodDeclaration)this.referenceContext);
/*     */     }
/*     */ 
/* 159 */     int realModifiers = modifiers & 0xFFFF;
/*     */ 
/* 162 */     if (declaringClass.isInterface()) {
/* 163 */       if ((realModifiers & 0xFFFFFBFE) != 0) {
/* 164 */         if ((declaringClass.modifiers & 0x2000) != 0)
/* 165 */           problemReporter().illegalModifierForAnnotationMember((AbstractMethodDeclaration)this.referenceContext);
/*     */         else
/* 167 */           problemReporter().illegalModifierForInterfaceMethod((AbstractMethodDeclaration)this.referenceContext);
/*     */       }
/* 169 */       return;
/*     */     }
/*     */ 
/* 175 */     if ((realModifiers & 0xFFFFF2C0) != 0) {
/* 176 */       problemReporter().illegalModifierForMethod((AbstractMethodDeclaration)this.referenceContext);
/* 177 */       modifiers &= -62145;
/*     */     }
/*     */ 
/* 181 */     int accessorBits = realModifiers & 0x7;
/* 182 */     if ((accessorBits & accessorBits - 1) != 0) {
/* 183 */       problemReporter().illegalVisibilityModifierCombinationForMethod(declaringClass, (AbstractMethodDeclaration)this.referenceContext);
/*     */ 
/* 186 */       if ((accessorBits & 0x1) != 0) {
/* 187 */         if ((accessorBits & 0x4) != 0)
/* 188 */           modifiers &= -5;
/* 189 */         if ((accessorBits & 0x2) != 0)
/* 190 */           modifiers &= -3;
/* 191 */       } else if (((accessorBits & 0x4) != 0) && ((accessorBits & 0x2) != 0)) {
/* 192 */         modifiers &= -3;
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 197 */     if ((modifiers & 0x400) != 0) {
/* 198 */       int incompatibleWithAbstract = 2362;
/* 199 */       if ((modifiers & incompatibleWithAbstract) != 0)
/* 200 */         problemReporter().illegalAbstractModifierCombinationForMethod(declaringClass, (AbstractMethodDeclaration)this.referenceContext);
/* 201 */       if (!methodBinding.declaringClass.isAbstract()) {
/* 202 */         problemReporter().abstractMethodInAbstractClass((SourceTypeBinding)declaringClass, (AbstractMethodDeclaration)this.referenceContext);
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 211 */     if (((modifiers & 0x100) != 0) && ((modifiers & 0x800) != 0)) {
/* 212 */       problemReporter().nativeMethodsCannotBeStrictfp(declaringClass, (AbstractMethodDeclaration)this.referenceContext);
/*     */     }
/*     */ 
/* 215 */     if (((realModifiers & 0x8) != 0) && (declaringClass.isNestedType()) && (!declaringClass.isStatic())) {
/* 216 */       problemReporter().unexpectedStaticModifierForMethod(declaringClass, (AbstractMethodDeclaration)this.referenceContext);
/*     */     }
/* 218 */     methodBinding.modifiers = modifiers;
/*     */   }
/*     */ 
/*     */   public void checkUnusedParameters(MethodBinding method) {
/* 222 */     if ((method.isAbstract()) || 
/* 223 */       ((method.isImplementing()) && (!compilerOptions().reportUnusedParameterWhenImplementingAbstract)) || 
/* 224 */       ((method.isOverriding()) && (!method.isImplementing()) && (!compilerOptions().reportUnusedParameterWhenOverridingConcrete)) || 
/* 225 */       (method.isMain()))
/*     */     {
/* 227 */       return;
/*     */     }
/* 229 */     int i = 0; for (int maxLocals = this.localIndex; i < maxLocals; i++) {
/* 230 */       LocalVariableBinding local = this.locals[i];
/* 231 */       if ((local == null) || ((local.tagBits & 0x400) == 0L)) {
/*     */         break;
/*     */       }
/* 234 */       if (local.useFlag != 0)
/*     */         continue;
/* 236 */       if ((local.declaration.bits & 0x40000000) != 0)
/* 237 */         problemReporter().unusedArgument(local.declaration);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void computeLocalVariablePositions(int initOffset, CodeStream codeStream)
/*     */   {
/* 249 */     this.offset = initOffset;
/* 250 */     this.maxOffset = initOffset;
/*     */ 
/* 253 */     int ilocal = 0; int maxLocals = this.localIndex;
/* 254 */     while (ilocal < maxLocals) {
/* 255 */       LocalVariableBinding local = this.locals[ilocal];
/* 256 */       if ((local == null) || ((local.tagBits & 0x400) == 0L)) {
/*     */         break;
/*     */       }
/* 259 */       codeStream.record(local);
/*     */ 
/* 262 */       local.resolvedPosition = this.offset;
/*     */ 
/* 264 */       if ((local.type == TypeBinding.LONG) || (local.type == TypeBinding.DOUBLE))
/* 265 */         this.offset += 2;
/*     */       else {
/* 267 */         this.offset += 1;
/*     */       }
/*     */ 
/* 270 */       if (this.offset > 255) {
/* 271 */         problemReporter().noMoreAvailableSpaceForArgument(local, local.declaration);
/*     */       }
/* 273 */       ilocal++;
/*     */     }
/*     */ 
/* 277 */     if (this.extraSyntheticArguments != null) {
/* 278 */       int iarg = 0; for (int maxArguments = this.extraSyntheticArguments.length; iarg < maxArguments; iarg++) {
/* 279 */         SyntheticArgumentBinding argument = this.extraSyntheticArguments[iarg];
/* 280 */         argument.resolvedPosition = this.offset;
/* 281 */         if ((argument.type == TypeBinding.LONG) || (argument.type == TypeBinding.DOUBLE))
/* 282 */           this.offset += 2;
/*     */         else {
/* 284 */           this.offset += 1;
/*     */         }
/* 286 */         if (this.offset > 255) {
/* 287 */           problemReporter().noMoreAvailableSpaceForArgument(argument, (ASTNode)this.referenceContext);
/*     */         }
/*     */       }
/*     */     }
/* 291 */     computeLocalVariablePositions(ilocal, this.offset, codeStream);
/*     */   }
/*     */ 
/*     */   MethodBinding createMethod(AbstractMethodDeclaration method)
/*     */   {
/* 302 */     this.referenceContext = method;
/* 303 */     method.scope = this;
/* 304 */     SourceTypeBinding declaringClass = referenceType().binding;
/* 305 */     int modifiers = method.modifiers | 0x2000000;
/* 306 */     if (method.isConstructor()) {
/* 307 */       if (method.isDefaultConstructor())
/* 308 */         modifiers |= 67108864;
/* 309 */       method.binding = new MethodBinding(modifiers, null, null, declaringClass);
/* 310 */       checkAndSetModifiersForConstructor(method.binding);
/*     */     } else {
/* 312 */       if (declaringClass.isInterface())
/* 313 */         modifiers |= 1025;
/* 314 */       method.binding = 
/* 315 */         new MethodBinding(modifiers, method.selector, null, null, null, declaringClass);
/* 316 */       checkAndSetModifiersForMethod(method.binding);
/*     */     }
/* 318 */     this.isStatic = method.binding.isStatic();
/*     */ 
/* 320 */     Argument[] argTypes = method.arguments;
/* 321 */     int argLength = argTypes == null ? 0 : argTypes.length;
/* 322 */     if ((argLength > 0) && (compilerOptions().sourceLevel >= 3211264L)) {
/* 323 */       argLength--; if (argTypes[argLength].isVarArgs())
/* 324 */         method.binding.modifiers |= 128;
/*     */       do {
/* 326 */         if (argTypes[argLength].isVarArgs())
/* 327 */           problemReporter().illegalVararg(argTypes[argLength], method);
/* 325 */         argLength--; } while (argLength >= 0);
/*     */     }
/*     */ 
/* 331 */     TypeParameter[] typeParameters = method.typeParameters();
/*     */ 
/* 333 */     if ((typeParameters == null) || (compilerOptions().sourceLevel < 3211264L)) {
/* 334 */       method.binding.typeVariables = Binding.NO_TYPE_VARIABLES;
/*     */     } else {
/* 336 */       method.binding.typeVariables = createTypeVariables(typeParameters, method.binding);
/* 337 */       method.binding.modifiers |= 1073741824;
/*     */     }
/* 339 */     return method.binding;
/*     */   }
/*     */ 
/*     */   public FieldBinding findField(TypeBinding receiverType, char[] fieldName, InvocationSite invocationSite, boolean needResolve)
/*     */   {
/* 354 */     FieldBinding field = super.findField(receiverType, fieldName, invocationSite, needResolve);
/* 355 */     if (field == null)
/* 356 */       return null;
/* 357 */     if (!field.isValidBinding())
/* 358 */       return field;
/* 359 */     if (field.isStatic()) {
/* 360 */       return field;
/*     */     }
/* 362 */     if ((!this.isConstructorCall) || (receiverType != enclosingSourceType())) {
/* 363 */       return field;
/*     */     }
/* 365 */     if ((invocationSite instanceof SingleNameReference))
/* 366 */       return new ProblemFieldBinding(
/* 367 */         field, 
/* 368 */         field.declaringClass, 
/* 369 */         fieldName, 
/* 370 */         6);
/* 371 */     if ((invocationSite instanceof QualifiedNameReference))
/*     */     {
/* 373 */       QualifiedNameReference name = (QualifiedNameReference)invocationSite;
/* 374 */       if (name.binding == null)
/*     */       {
/* 376 */         return new ProblemFieldBinding(
/* 377 */           field, 
/* 378 */           field.declaringClass, 
/* 379 */           fieldName, 
/* 380 */           6);
/*     */       }
/*     */     }
/* 382 */     return field;
/*     */   }
/*     */ 
/*     */   public boolean isInsideConstructor() {
/* 386 */     return this.referenceContext instanceof ConstructorDeclaration;
/*     */   }
/*     */ 
/*     */   public boolean isInsideInitializer() {
/* 390 */     return this.referenceContext instanceof TypeDeclaration;
/*     */   }
/*     */ 
/*     */   public boolean isInsideInitializerOrConstructor()
/*     */   {
/* 395 */     return ((this.referenceContext instanceof TypeDeclaration)) || 
/* 395 */       ((this.referenceContext instanceof ConstructorDeclaration));
/*     */   }
/*     */ 
/*     */   public ProblemReporter problemReporter()
/*     */   {
/*     */     MethodScope outerMethodScope;
/* 407 */     if ((outerMethodScope = outerMostMethodScope()) == this) {
/* 408 */       ProblemReporter problemReporter = referenceCompilationUnit().problemReporter;
/* 409 */       problemReporter.referenceContext = this.referenceContext;
/* 410 */       return problemReporter;
/*     */     }
/* 412 */     return outerMethodScope.problemReporter();
/*     */   }
/*     */ 
/*     */   public final int recordInitializationStates(FlowInfo flowInfo) {
/* 416 */     if ((flowInfo.tagBits & 0x1) != 0) return -1; UnconditionalFlowInfo unconditionalFlowInfo = flowInfo.unconditionalInitsWithoutSideEffect();
/* 418 */     long[] extraInits = unconditionalFlowInfo.extra == null ? 
/* 419 */       null : unconditionalFlowInfo.extra[0];
/* 420 */     long inits = unconditionalFlowInfo.definiteInits;
/* 421 */     int i = this.lastIndex;
/*     */     label139: 
/*     */     do { if (this.definiteInits[i] == inits) {
/* 423 */         long[] otherInits = this.extraDefiniteInits[i];
/*     */         int j;
/*     */         int max;
/* 424 */         if ((extraInits != null) && (otherInits != null)) {
/* 425 */           if (extraInits.length != otherInits.length) break label139;
/* 427 */           j = 0; max = extraInits.length;
/* 428 */         }while (extraInits[j] == otherInits[j])
/*     */         {
/* 427 */           j++; if (j >= max)
/*     */           {
/* 432 */             return i;
/*     */ 
/* 435 */             if ((extraInits != null) || (otherInits != null)) break;
/* 436 */             return i;
/*     */           }
/*     */         }
/*     */       }
/* 421 */       i--; } while (i >= 0);
/*     */ 
/* 443 */     if (this.definiteInits.length == this.lastIndex)
/*     */     {
/* 445 */       System.arraycopy(
/* 446 */         this.definiteInits, 
/* 447 */         0, 
/* 448 */         this.definiteInits = new long[this.lastIndex + 20], 
/* 449 */         0, 
/* 450 */         this.lastIndex);
/* 451 */       System.arraycopy(
/* 452 */         this.extraDefiniteInits, 
/* 453 */         0, 
/* 454 */         this.extraDefiniteInits = new long[this.lastIndex + 20][], 
/* 455 */         0, 
/* 456 */         this.lastIndex);
/*     */     }
/* 458 */     this.definiteInits[this.lastIndex] = inits;
/* 459 */     if (extraInits != null) {
/* 460 */       this.extraDefiniteInits[this.lastIndex] = new long[extraInits.length];
/* 461 */       System.arraycopy(
/* 462 */         extraInits, 
/* 463 */         0, 
/* 464 */         this.extraDefiniteInits[this.lastIndex], 
/* 465 */         0, 
/* 466 */         extraInits.length);
/*     */     }
/* 468 */     return this.lastIndex++;
/*     */   }
/*     */ 
/*     */   public AbstractMethodDeclaration referenceMethod()
/*     */   {
/* 475 */     if ((this.referenceContext instanceof AbstractMethodDeclaration)) return (AbstractMethodDeclaration)this.referenceContext;
/* 476 */     return null;
/*     */   }
/*     */ 
/*     */   public TypeDeclaration referenceType()
/*     */   {
/* 484 */     return ((ClassScope)this.parent).referenceContext;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\ecj-3.5.2.jar
 * Qualified Name:     org.eclipse.jdt.internal.compiler.lookup.MethodScope
 * JD-Core Version:    0.6.0
 */