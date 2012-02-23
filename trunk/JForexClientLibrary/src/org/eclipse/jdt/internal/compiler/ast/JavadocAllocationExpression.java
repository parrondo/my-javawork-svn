/*     */ package org.eclipse.jdt.internal.compiler.ast;
/*     */ 
/*     */ import org.eclipse.jdt.core.compiler.CharOperation;
/*     */ import org.eclipse.jdt.internal.compiler.ASTVisitor;
/*     */ import org.eclipse.jdt.internal.compiler.impl.Constant;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.Binding;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.BlockScope;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.ClassScope;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.LookupEnvironment;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.MethodBinding;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.ParameterizedMethodBinding;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.ProblemMethodBinding;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.ReferenceBinding;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.Scope;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.SourceTypeBinding;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;
/*     */ import org.eclipse.jdt.internal.compiler.problem.ProblemReporter;
/*     */ 
/*     */ public class JavadocAllocationExpression extends AllocationExpression
/*     */ {
/*     */   public int tagSourceStart;
/*     */   public int tagSourceEnd;
/*     */   public int tagValue;
/*     */   public int memberStart;
/*     */   public char[][] qualification;
/*     */ 
/*     */   public JavadocAllocationExpression(int start, int end)
/*     */   {
/*  25 */     this.sourceStart = start;
/*  26 */     this.sourceEnd = end;
/*  27 */     this.bits |= 32768;
/*     */   }
/*     */   public JavadocAllocationExpression(long pos) {
/*  30 */     this((int)(pos >>> 32), (int)pos);
/*     */   }
/*     */ 
/*     */   TypeBinding internalResolveType(Scope scope)
/*     */   {
/*  36 */     this.constant = Constant.NotAConstant;
/*  37 */     if (this.type == null)
/*  38 */       this.resolvedType = scope.enclosingSourceType();
/*  39 */     else if (scope.kind == 3)
/*  40 */       this.resolvedType = this.type.resolveType((ClassScope)scope);
/*     */     else {
/*  42 */       this.resolvedType = this.type.resolveType((BlockScope)scope, true);
/*     */     }
/*     */ 
/*  46 */     TypeBinding[] argumentTypes = Binding.NO_PARAMETERS;
/*  47 */     boolean hasTypeVarArgs = false;
/*  48 */     if (this.arguments != null) {
/*  49 */       boolean argHasError = false;
/*  50 */       int length = this.arguments.length;
/*  51 */       argumentTypes = new TypeBinding[length];
/*  52 */       for (int i = 0; i < length; i++) {
/*  53 */         Expression argument = this.arguments[i];
/*  54 */         if (scope.kind == 3)
/*  55 */           argumentTypes[i] = argument.resolveType((ClassScope)scope);
/*     */         else {
/*  57 */           argumentTypes[i] = argument.resolveType((BlockScope)scope);
/*     */         }
/*  59 */         if (argumentTypes[i] == null)
/*  60 */           argHasError = true;
/*  61 */         else if (!hasTypeVarArgs) {
/*  62 */           hasTypeVarArgs = argumentTypes[i].isTypeVariable();
/*     */         }
/*     */       }
/*  65 */       if (argHasError) {
/*  66 */         return null;
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/*  71 */     if (this.resolvedType == null) {
/*  72 */       return null;
/*     */     }
/*  74 */     this.resolvedType = scope.environment().convertToRawType(this.type.resolvedType, true);
/*  75 */     SourceTypeBinding enclosingType = scope.enclosingSourceType();
/*  76 */     if ((enclosingType != null) && (enclosingType.isCompatibleWith(this.resolvedType))) {
/*  77 */       this.bits |= 16384;
/*     */     }
/*     */ 
/*  80 */     ReferenceBinding allocationType = (ReferenceBinding)this.resolvedType;
/*  81 */     this.binding = scope.getConstructor(allocationType, argumentTypes, this);
/*  82 */     if (!this.binding.isValidBinding()) {
/*  83 */       ReferenceBinding enclosingTypeBinding = allocationType;
/*  84 */       MethodBinding contructorBinding = this.binding;
/*  85 */       while ((!contructorBinding.isValidBinding()) && ((enclosingTypeBinding.isMemberType()) || (enclosingTypeBinding.isLocalType()))) {
/*  86 */         enclosingTypeBinding = enclosingTypeBinding.enclosingType();
/*  87 */         contructorBinding = scope.getConstructor(enclosingTypeBinding, argumentTypes, this);
/*     */       }
/*  89 */       if (contructorBinding.isValidBinding()) {
/*  90 */         this.binding = contructorBinding;
/*     */       }
/*     */     }
/*  93 */     if (!this.binding.isValidBinding())
/*     */     {
/*  95 */       MethodBinding methodBinding = scope.getMethod(this.resolvedType, this.resolvedType.sourceName(), argumentTypes, this);
/*  96 */       if (methodBinding.isValidBinding()) {
/*  97 */         this.binding = methodBinding;
/*     */       } else {
/*  99 */         if (this.binding.declaringClass == null) {
/* 100 */           this.binding.declaringClass = allocationType;
/*     */         }
/* 102 */         scope.problemReporter().javadocInvalidConstructor(this, this.binding, scope.getDeclarationModifiers());
/*     */       }
/* 104 */       return this.resolvedType;
/* 105 */     }if (this.binding.isVarargs()) {
/* 106 */       int length = argumentTypes.length;
/* 107 */       if ((this.binding.parameters.length != length) || (!argumentTypes[(length - 1)].isArrayType())) {
/* 108 */         MethodBinding problem = new ProblemMethodBinding(this.binding, this.binding.selector, argumentTypes, 1);
/* 109 */         scope.problemReporter().javadocInvalidConstructor(this, problem, scope.getDeclarationModifiers());
/*     */       }
/* 111 */     } else if (hasTypeVarArgs) {
/* 112 */       MethodBinding problem = new ProblemMethodBinding(this.binding, this.binding.selector, argumentTypes, 1);
/* 113 */       scope.problemReporter().javadocInvalidConstructor(this, problem, scope.getDeclarationModifiers());
/* 114 */     } else if ((this.binding instanceof ParameterizedMethodBinding)) {
/* 115 */       ParameterizedMethodBinding paramMethodBinding = (ParameterizedMethodBinding)this.binding;
/* 116 */       if (paramMethodBinding.hasSubstitutedParameters()) {
/* 117 */         int length = argumentTypes.length;
/* 118 */         for (int i = 0; i < length; i++) {
/* 119 */           if ((paramMethodBinding.parameters[i] == argumentTypes[i]) || 
/* 120 */             (paramMethodBinding.parameters[i].erasure() == argumentTypes[i].erasure())) continue;
/* 121 */           MethodBinding problem = new ProblemMethodBinding(this.binding, this.binding.selector, argumentTypes, 1);
/* 122 */           scope.problemReporter().javadocInvalidConstructor(this, problem, scope.getDeclarationModifiers());
/* 123 */           break;
/*     */         }
/*     */       }
/*     */     }
/* 127 */     else if (this.resolvedType.isMemberType()) {
/* 128 */       int length = this.qualification.length;
/* 129 */       if (length > 1) {
/* 130 */         ReferenceBinding enclosingTypeBinding = allocationType;
/* 131 */         if (((this.type instanceof JavadocQualifiedTypeReference)) && (((JavadocQualifiedTypeReference)this.type).tokens.length != length)) {
/* 132 */           scope.problemReporter().javadocInvalidMemberTypeQualification(this.memberStart + 1, this.sourceEnd, scope.getDeclarationModifiers());
/*     */         } else {
/* 134 */           int idx = length;
/*     */           do { if (idx <= 0) break; idx--; } while ((CharOperation.equals(this.qualification[idx], enclosingTypeBinding.sourceName)) && ((enclosingTypeBinding = enclosingTypeBinding.enclosingType()) != null));
/*     */ 
/* 138 */           if ((idx > 0) || (enclosingTypeBinding != null)) {
/* 139 */             scope.problemReporter().javadocInvalidMemberTypeQualification(this.memberStart + 1, this.sourceEnd, scope.getDeclarationModifiers());
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/* 144 */     if (isMethodUseDeprecated(this.binding, scope, true)) {
/* 145 */       scope.problemReporter().javadocDeprecatedMethod(this.binding, this, scope.getDeclarationModifiers());
/*     */     }
/* 147 */     return allocationType;
/*     */   }
/*     */ 
/*     */   public boolean isSuperAccess() {
/* 151 */     return (this.bits & 0x4000) != 0;
/*     */   }
/*     */ 
/*     */   public TypeBinding resolveType(BlockScope scope) {
/* 155 */     return internalResolveType(scope);
/*     */   }
/*     */ 
/*     */   public TypeBinding resolveType(ClassScope scope) {
/* 159 */     return internalResolveType(scope);
/*     */   }
/*     */   public void traverse(ASTVisitor visitor, BlockScope scope) {
/* 162 */     if (visitor.visit(this, scope)) {
/* 163 */       if (this.typeArguments != null) {
/* 164 */         int i = 0; for (int typeArgumentsLength = this.typeArguments.length; i < typeArgumentsLength; i++) {
/* 165 */           this.typeArguments[i].traverse(visitor, scope);
/*     */         }
/*     */       }
/* 168 */       if (this.type != null) {
/* 169 */         this.type.traverse(visitor, scope);
/*     */       }
/* 171 */       if (this.arguments != null) {
/* 172 */         int i = 0; for (int argumentsLength = this.arguments.length; i < argumentsLength; i++)
/* 173 */           this.arguments[i].traverse(visitor, scope);
/*     */       }
/*     */     }
/* 176 */     visitor.endVisit(this, scope);
/*     */   }
/*     */   public void traverse(ASTVisitor visitor, ClassScope scope) {
/* 179 */     if (visitor.visit(this, scope)) {
/* 180 */       if (this.typeArguments != null) {
/* 181 */         int i = 0; for (int typeArgumentsLength = this.typeArguments.length; i < typeArgumentsLength; i++) {
/* 182 */           this.typeArguments[i].traverse(visitor, scope);
/*     */         }
/*     */       }
/* 185 */       if (this.type != null) {
/* 186 */         this.type.traverse(visitor, scope);
/*     */       }
/* 188 */       if (this.arguments != null) {
/* 189 */         int i = 0; for (int argumentsLength = this.arguments.length; i < argumentsLength; i++)
/* 190 */           this.arguments[i].traverse(visitor, scope);
/*     */       }
/*     */     }
/* 193 */     visitor.endVisit(this, scope);
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\ecj-3.5.2.jar
 * Qualified Name:     org.eclipse.jdt.internal.compiler.ast.JavadocAllocationExpression
 * JD-Core Version:    0.6.0
 */