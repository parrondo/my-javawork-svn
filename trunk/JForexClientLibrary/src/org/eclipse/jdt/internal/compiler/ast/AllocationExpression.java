/*     */ package org.eclipse.jdt.internal.compiler.ast;
/*     */ 
/*     */ import org.eclipse.jdt.internal.compiler.ASTVisitor;
/*     */ import org.eclipse.jdt.internal.compiler.codegen.CodeStream;
/*     */ import org.eclipse.jdt.internal.compiler.flow.FlowContext;
/*     */ import org.eclipse.jdt.internal.compiler.flow.FlowInfo;
/*     */ import org.eclipse.jdt.internal.compiler.impl.CompilerOptions;
/*     */ import org.eclipse.jdt.internal.compiler.impl.Constant;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.Binding;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.BlockScope;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.FieldBinding;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.InvocationSite;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.LocalTypeBinding;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.LocalVariableBinding;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.LookupEnvironment;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.MethodBinding;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.NestedTypeBinding;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.ProblemMethodBinding;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.ReferenceBinding;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.SourceTypeBinding;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.SyntheticArgumentBinding;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.TypeConstants;
/*     */ import org.eclipse.jdt.internal.compiler.problem.ProblemReporter;
/*     */ 
/*     */ public class AllocationExpression extends Expression
/*     */   implements InvocationSite
/*     */ {
/*     */   public TypeReference type;
/*     */   public Expression[] arguments;
/*     */   public MethodBinding binding;
/*     */   MethodBinding syntheticAccessor;
/*     */   public TypeReference[] typeArguments;
/*     */   public TypeBinding[] genericTypeArguments;
/*     */   public FieldDeclaration enumConstant;
/*     */ 
/*     */   public FlowInfo analyseCode(BlockScope currentScope, FlowContext flowContext, FlowInfo flowInfo)
/*     */   {
/*  32 */     checkCapturedLocalInitializationIfNecessary((ReferenceBinding)this.binding.declaringClass.erasure(), currentScope, flowInfo);
/*     */ 
/*  35 */     if (this.arguments != null) {
/*  36 */       int i = 0; for (int count = this.arguments.length; i < count; i++)
/*  37 */         flowInfo = 
/*  38 */           this.arguments[i]
/*  39 */           .analyseCode(currentScope, flowContext, flowInfo)
/*  40 */           .unconditionalInits();
/*     */     }
/*     */     ReferenceBinding[] thrownExceptions;
/*  45 */     if ((thrownExceptions = this.binding.thrownExceptions).length != 0) {
/*  46 */       if (((this.bits & 0x10000) != 0) && (this.genericTypeArguments == null))
/*     */       {
/*  48 */         thrownExceptions = currentScope.environment().convertToRawTypes(this.binding.thrownExceptions, true, true);
/*     */       }
/*     */ 
/*  51 */       flowContext.checkExceptionHandlers(
/*  52 */         thrownExceptions, 
/*  53 */         this, 
/*  54 */         flowInfo.unconditionalCopy(), 
/*  55 */         currentScope);
/*     */     }
/*  57 */     manageEnclosingInstanceAccessIfNecessary(currentScope, flowInfo);
/*  58 */     manageSyntheticAccessIfNecessary(currentScope, flowInfo);
/*     */ 
/*  60 */     return flowInfo;
/*     */   }
/*     */ 
/*     */   public void checkCapturedLocalInitializationIfNecessary(ReferenceBinding checkedType, BlockScope currentScope, FlowInfo flowInfo) {
/*  64 */     if (((checkedType.tagBits & 0x834) == 2068L) && 
/*  65 */       (!currentScope.isDefinedInType(checkedType))) {
/*  66 */       NestedTypeBinding nestedType = (NestedTypeBinding)checkedType;
/*  67 */       SyntheticArgumentBinding[] syntheticArguments = nestedType.syntheticOuterLocalVariables();
/*  68 */       if (syntheticArguments != null) {
/*  69 */         int i = 0; for (int count = syntheticArguments.length; i < count; i++) {
/*  70 */           SyntheticArgumentBinding syntheticArgument = syntheticArguments[i];
/*     */           LocalVariableBinding targetLocal;
/*  72 */           if (((targetLocal = syntheticArgument.actualOuterLocalVariable) == null) || 
/*  73 */             (targetLocal.declaration == null) || (flowInfo.isDefinitelyAssigned(targetLocal))) continue;
/*  74 */           currentScope.problemReporter().uninitializedLocalVariable(targetLocal, this);
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public Expression enclosingInstance() {
/*  81 */     return null;
/*     */   }
/*     */ 
/*     */   public void generateCode(BlockScope currentScope, CodeStream codeStream, boolean valueRequired) {
/*  85 */     int pc = codeStream.position;
/*  86 */     MethodBinding codegenBinding = this.binding.original();
/*  87 */     ReferenceBinding allocatedType = codegenBinding.declaringClass;
/*     */ 
/*  89 */     codeStream.new_(allocatedType);
/*  90 */     boolean isUnboxing = (this.implicitConversion & 0x400) != 0;
/*  91 */     if ((valueRequired) || (isUnboxing)) {
/*  92 */       codeStream.dup();
/*     */     }
/*     */ 
/*  95 */     if (this.type != null) {
/*  96 */       codeStream.recordPositionsFrom(pc, this.type.sourceStart);
/*     */     }
/*     */     else {
/*  99 */       codeStream.ldc(String.valueOf(this.enumConstant.name));
/* 100 */       codeStream.generateInlinedValue(this.enumConstant.binding.id);
/*     */     }
/*     */ 
/* 104 */     if (allocatedType.isNestedType()) {
/* 105 */       codeStream.generateSyntheticEnclosingInstanceValues(
/* 106 */         currentScope, 
/* 107 */         allocatedType, 
/* 108 */         enclosingInstance(), 
/* 109 */         this);
/*     */     }
/*     */ 
/* 112 */     generateArguments(this.binding, this.arguments, currentScope, codeStream);
/*     */ 
/* 114 */     if (allocatedType.isNestedType()) {
/* 115 */       codeStream.generateSyntheticOuterArgumentValues(
/* 116 */         currentScope, 
/* 117 */         allocatedType, 
/* 118 */         this);
/*     */     }
/*     */ 
/* 121 */     if (this.syntheticAccessor == null) {
/* 122 */       codeStream.invoke(-73, codegenBinding, null);
/*     */     }
/*     */     else {
/* 125 */       int i = 0;
/* 126 */       int max = this.syntheticAccessor.parameters.length - codegenBinding.parameters.length;
/* 127 */       while (i < max)
/*     */       {
/* 129 */         codeStream.aconst_null();
/*     */ 
/* 128 */         i++;
/*     */       }
/*     */ 
/* 131 */       codeStream.invoke(-73, this.syntheticAccessor, null);
/*     */     }
/* 133 */     if (valueRequired) {
/* 134 */       codeStream.generateImplicitConversion(this.implicitConversion);
/* 135 */     } else if (isUnboxing)
/*     */     {
/* 137 */       codeStream.generateImplicitConversion(this.implicitConversion);
/* 138 */       switch (postConversionType(currentScope).id) {
/*     */       case 7:
/*     */       case 8:
/* 141 */         codeStream.pop2();
/* 142 */         break;
/*     */       default:
/* 144 */         codeStream.pop();
/*     */       }
/*     */     }
/* 147 */     codeStream.recordPositionsFrom(pc, this.sourceStart);
/*     */   }
/*     */ 
/*     */   public TypeBinding[] genericTypeArguments()
/*     */   {
/* 154 */     return this.genericTypeArguments;
/*     */   }
/*     */ 
/*     */   public boolean isSuperAccess() {
/* 158 */     return false;
/*     */   }
/*     */ 
/*     */   public boolean isTypeAccess() {
/* 162 */     return true;
/*     */   }
/*     */ 
/*     */   public void manageEnclosingInstanceAccessIfNecessary(BlockScope currentScope, FlowInfo flowInfo)
/*     */   {
/* 173 */     if ((flowInfo.tagBits & 0x1) != 0) return;
/* 174 */     ReferenceBinding allocatedTypeErasure = (ReferenceBinding)this.binding.declaringClass.erasure();
/*     */ 
/* 177 */     if ((allocatedTypeErasure.isNestedType()) && 
/* 178 */       (currentScope.enclosingSourceType().isLocalType()))
/*     */     {
/* 180 */       if (allocatedTypeErasure.isLocalType()) {
/* 181 */         ((LocalTypeBinding)allocatedTypeErasure).addInnerEmulationDependent(currentScope, false);
/*     */       }
/*     */       else
/*     */       {
/* 185 */         currentScope.propagateInnerEmulation(allocatedTypeErasure, false);
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public void manageSyntheticAccessIfNecessary(BlockScope currentScope, FlowInfo flowInfo)
/*     */   {
/* 192 */     if ((flowInfo.tagBits & 0x1) != 0) return;
/*     */ 
/* 194 */     MethodBinding codegenBinding = this.binding.original();
/*     */     ReferenceBinding declaringClass;
/* 197 */     if ((codegenBinding.isPrivate()) && (currentScope.enclosingSourceType() != (declaringClass = codegenBinding.declaringClass)))
/*     */     {
/* 200 */       if (((declaringClass.tagBits & 0x10) != 0L) && (currentScope.compilerOptions().complianceLevel >= 3145728L))
/*     */       {
/* 202 */         codegenBinding.tagBits |= 1024L;
/*     */       } else {
/* 204 */         this.syntheticAccessor = ((SourceTypeBinding)declaringClass).addSyntheticMethod(codegenBinding, isSuperAccess());
/* 205 */         currentScope.problemReporter().needToEmulateMethodAccess(codegenBinding, this);
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public StringBuffer printExpression(int indent, StringBuffer output) {
/* 211 */     if (this.type != null) {
/* 212 */       output.append("new ");
/*     */     }
/* 214 */     if (this.typeArguments != null) {
/* 215 */       output.append('<');
/* 216 */       int max = this.typeArguments.length - 1;
/* 217 */       for (int j = 0; j < max; j++) {
/* 218 */         this.typeArguments[j].print(0, output);
/* 219 */         output.append(", ");
/*     */       }
/* 221 */       this.typeArguments[max].print(0, output);
/* 222 */       output.append('>');
/*     */     }
/* 224 */     if (this.type != null) {
/* 225 */       this.type.printExpression(0, output);
/*     */     }
/* 227 */     output.append('(');
/* 228 */     if (this.arguments != null) {
/* 229 */       for (int i = 0; i < this.arguments.length; i++) {
/* 230 */         if (i > 0) output.append(", ");
/* 231 */         this.arguments[i].printExpression(0, output);
/*     */       }
/*     */     }
/* 234 */     return output.append(')');
/*     */   }
/*     */ 
/*     */   public TypeBinding resolveType(BlockScope scope)
/*     */   {
/* 239 */     this.constant = Constant.NotAConstant;
/* 240 */     if (this.type == null)
/*     */     {
/* 242 */       this.resolvedType = scope.enclosingReceiverType();
/*     */     } else {
/* 244 */       this.resolvedType = this.type.resolveType(scope, true);
/*     */ 
/* 246 */       if ((this.type instanceof ParameterizedQualifiedTypeReference)) {
/* 247 */         ReferenceBinding currentType = (ReferenceBinding)this.resolvedType;
/* 248 */         if (currentType == null) return currentType;
/*     */ 
/* 251 */         while (((currentType.modifiers & 0x8) == 0) && 
/* 252 */           (!currentType.isRawType())) {
/* 253 */           if ((currentType = currentType.enclosingType()) == null) {
/* 254 */             ParameterizedQualifiedTypeReference qRef = (ParameterizedQualifiedTypeReference)this.type;
/* 255 */             for (int i = qRef.typeArguments.length - 2; i >= 0; i--) {
/* 256 */               if (qRef.typeArguments[i] != null) {
/* 257 */                 scope.problemReporter().illegalQualifiedParameterizedTypeAllocation(this.type, this.resolvedType);
/* 258 */                 break;
/*     */               }
/*     */             }
/*     */           }
/*     */         }
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 267 */     if (this.typeArguments != null) {
/* 268 */       int length = this.typeArguments.length;
/* 269 */       boolean argHasError = scope.compilerOptions().sourceLevel < 3211264L;
/* 270 */       this.genericTypeArguments = new TypeBinding[length];
/* 271 */       for (int i = 0; i < length; i++) {
/* 272 */         TypeReference typeReference = this.typeArguments[i];
/* 273 */         if ((this.genericTypeArguments[i] =  = typeReference.resolveType(scope, true)) == null) {
/* 274 */           argHasError = true;
/*     */         }
/* 276 */         if ((argHasError) && ((typeReference instanceof Wildcard))) {
/* 277 */           scope.problemReporter().illegalUsageOfWildcard(typeReference);
/*     */         }
/*     */       }
/* 280 */       if (argHasError) {
/* 281 */         if (this.arguments != null) {
/* 282 */           int i = 0; for (int max = this.arguments.length; i < max; i++) {
/* 283 */             this.arguments[i].resolveType(scope);
/*     */           }
/*     */         }
/* 286 */         return null;
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 291 */     boolean argsContainCast = false;
/* 292 */     TypeBinding[] argumentTypes = Binding.NO_PARAMETERS;
/* 293 */     if (this.arguments != null) {
/* 294 */       boolean argHasError = false;
/* 295 */       int length = this.arguments.length;
/* 296 */       argumentTypes = new TypeBinding[length];
/* 297 */       for (int i = 0; i < length; i++) {
/* 298 */         Expression argument = this.arguments[i];
/* 299 */         if ((argument instanceof CastExpression)) {
/* 300 */           argument.bits |= 32;
/* 301 */           argsContainCast = true;
/*     */         }
/* 303 */         if ((argumentTypes[i] =  = argument.resolveType(scope)) == null) {
/* 304 */           argHasError = true;
/*     */         }
/*     */       }
/* 307 */       if (argHasError) {
/* 308 */         if ((this.resolvedType instanceof ReferenceBinding))
/*     */         {
/* 310 */           TypeBinding[] pseudoArgs = new TypeBinding[length];
/* 311 */           int i = length;
/*     */           do { pseudoArgs[i] = (argumentTypes[i] == null ? TypeBinding.NULL : argumentTypes[i]);
/*     */ 
/* 311 */             i--; } while (i >= 0);
/*     */ 
/* 314 */           this.binding = scope.findMethod((ReferenceBinding)this.resolvedType, TypeConstants.INIT, pseudoArgs, this);
/* 315 */           if ((this.binding != null) && (!this.binding.isValidBinding())) {
/* 316 */             MethodBinding closestMatch = ((ProblemMethodBinding)this.binding).closestMatch;
/*     */ 
/* 318 */             if (closestMatch != null) {
/* 319 */               if (closestMatch.original().typeVariables != Binding.NO_TYPE_VARIABLES)
/*     */               {
/* 321 */                 closestMatch = scope.environment().createParameterizedGenericMethod(closestMatch.original(), null);
/*     */               }
/* 323 */               this.binding = closestMatch;
/* 324 */               MethodBinding closestMatchOriginal = closestMatch.original();
/* 325 */               if ((closestMatchOriginal.isOrEnclosedByPrivateType()) && (!scope.isDefinedInMethod(closestMatchOriginal)))
/*     */               {
/* 327 */                 closestMatchOriginal.modifiers |= 134217728;
/*     */               }
/*     */             }
/*     */           }
/*     */         }
/* 332 */         return this.resolvedType;
/*     */       }
/*     */     }
/* 335 */     if ((this.resolvedType == null) || (!this.resolvedType.isValidBinding())) {
/* 336 */       return null;
/*     */     }
/*     */ 
/* 340 */     if ((this.type != null) && (!this.resolvedType.canBeInstantiated())) {
/* 341 */       scope.problemReporter().cannotInstantiate(this.type, this.resolvedType);
/* 342 */       return this.resolvedType;
/*     */     }
/* 344 */     ReferenceBinding allocationType = (ReferenceBinding)this.resolvedType;
/* 345 */     if (!(this.binding = scope.getConstructor(allocationType, argumentTypes, this)).isValidBinding()) {
/* 346 */       if (this.binding.declaringClass == null) {
/* 347 */         this.binding.declaringClass = allocationType;
/*     */       }
/* 349 */       if ((this.type != null) && (!this.type.resolvedType.isValidBinding())) {
/* 350 */         return null;
/*     */       }
/* 352 */       scope.problemReporter().invalidConstructor(this, this.binding);
/* 353 */       return this.resolvedType;
/*     */     }
/* 355 */     if ((this.binding.tagBits & 0x80) != 0L) {
/* 356 */       scope.problemReporter().missingTypeInConstructor(this, this.binding);
/*     */     }
/* 358 */     if (isMethodUseDeprecated(this.binding, scope, true))
/* 359 */       scope.problemReporter().deprecatedMethod(this.binding, this);
/* 360 */     if (checkInvocationArguments(scope, null, allocationType, this.binding, this.arguments, argumentTypes, argsContainCast, this)) {
/* 361 */       this.bits |= 65536;
/*     */     }
/* 363 */     if ((this.typeArguments != null) && (this.binding.original().typeVariables == Binding.NO_TYPE_VARIABLES)) {
/* 364 */       scope.problemReporter().unnecessaryTypeArgumentsForMethodInvocation(this.binding, this.genericTypeArguments, this.typeArguments);
/*     */     }
/* 366 */     return allocationType;
/*     */   }
/*     */ 
/*     */   public void setActualReceiverType(ReferenceBinding receiverType)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void setDepth(int i)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void setFieldIndex(int i)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void traverse(ASTVisitor visitor, BlockScope scope) {
/* 382 */     if (visitor.visit(this, scope)) {
/* 383 */       if (this.typeArguments != null) {
/* 384 */         int i = 0; for (int typeArgumentsLength = this.typeArguments.length; i < typeArgumentsLength; i++) {
/* 385 */           this.typeArguments[i].traverse(visitor, scope);
/*     */         }
/*     */       }
/* 388 */       if (this.type != null) {
/* 389 */         this.type.traverse(visitor, scope);
/*     */       }
/* 391 */       if (this.arguments != null) {
/* 392 */         int i = 0; for (int argumentsLength = this.arguments.length; i < argumentsLength; i++)
/* 393 */           this.arguments[i].traverse(visitor, scope);
/*     */       }
/*     */     }
/* 396 */     visitor.endVisit(this, scope);
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\ecj-3.5.2.jar
 * Qualified Name:     org.eclipse.jdt.internal.compiler.ast.AllocationExpression
 * JD-Core Version:    0.6.0
 */