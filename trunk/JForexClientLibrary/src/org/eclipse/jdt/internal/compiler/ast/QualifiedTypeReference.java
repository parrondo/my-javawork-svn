/*     */ package org.eclipse.jdt.internal.compiler.ast;
/*     */ 
/*     */ import org.eclipse.jdt.core.compiler.CharOperation;
/*     */ import org.eclipse.jdt.internal.compiler.ASTVisitor;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.Binding;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.BlockScope;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.ClassScope;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.LookupEnvironment;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.PackageBinding;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.ProblemReferenceBinding;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.ReferenceBinding;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.Scope;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.TypeVariableBinding;
/*     */ import org.eclipse.jdt.internal.compiler.problem.AbortCompilation;
/*     */ import org.eclipse.jdt.internal.compiler.problem.ProblemReporter;
/*     */ 
/*     */ public class QualifiedTypeReference extends TypeReference
/*     */ {
/*     */   public char[][] tokens;
/*     */   public long[] sourcePositions;
/*     */ 
/*     */   public QualifiedTypeReference(char[][] sources, long[] poss)
/*     */   {
/*  25 */     this.tokens = sources;
/*  26 */     this.sourcePositions = poss;
/*  27 */     this.sourceStart = (int)(this.sourcePositions[0] >>> 32);
/*  28 */     this.sourceEnd = (int)(this.sourcePositions[(this.sourcePositions.length - 1)] & 0xFFFFFFFF);
/*     */   }
/*     */ 
/*     */   public TypeReference copyDims(int dim)
/*     */   {
/*  34 */     return new ArrayQualifiedTypeReference(this.tokens, dim, this.sourcePositions);
/*     */   }
/*     */ 
/*     */   protected TypeBinding findNextTypeBinding(int tokenIndex, Scope scope, PackageBinding packageBinding) {
/*  38 */     LookupEnvironment env = scope.environment();
/*     */     try {
/*  40 */       env.missingClassFileLocation = this;
/*  41 */       if (this.resolvedType == null) {
/*  42 */         this.resolvedType = scope.getType(this.tokens[tokenIndex], packageBinding);
/*     */       } else {
/*  44 */         this.resolvedType = scope.getMemberType(this.tokens[tokenIndex], (ReferenceBinding)this.resolvedType);
/*  45 */         if (!this.resolvedType.isValidBinding()) {
/*  46 */           this.resolvedType = 
/*  49 */             new ProblemReferenceBinding(CharOperation.subarray(this.tokens, 0, tokenIndex + 1), 
/*  48 */             (ReferenceBinding)this.resolvedType.closestMatch(), 
/*  49 */             this.resolvedType.problemId());
/*     */         }
/*     */       }
/*  52 */       TypeBinding localTypeBinding = this.resolvedType;
/*     */       return localTypeBinding;
/*     */     } catch (AbortCompilation e) {
/*  54 */       e.updateContext(this, scope.referenceCompilationUnit().compilationResult);
/*  55 */       throw e;
/*     */     } finally {
/*  57 */       env.missingClassFileLocation = null;
/*  58 */     }throw localObject;
/*     */   }
/*     */ 
/*     */   public char[] getLastToken() {
/*  62 */     return this.tokens[(this.tokens.length - 1)];
/*     */   }
/*     */ 
/*     */   protected TypeBinding getTypeBinding(Scope scope) {
/*  66 */     if (this.resolvedType != null) {
/*  67 */       return this.resolvedType;
/*     */     }
/*  69 */     Binding binding = scope.getPackage(this.tokens);
/*  70 */     if ((binding != null) && (!binding.isValidBinding())) {
/*  71 */       if (((binding instanceof ProblemReferenceBinding)) && (binding.problemId() == 1)) {
/*  72 */         ProblemReferenceBinding problemBinding = (ProblemReferenceBinding)binding;
/*  73 */         Binding pkg = scope.getTypeOrPackage(this.tokens);
/*  74 */         return new ProblemReferenceBinding(problemBinding.compoundName, (pkg instanceof PackageBinding) ? null : scope.environment().createMissingType(null, this.tokens), 1);
/*     */       }
/*  76 */       return (ReferenceBinding)binding;
/*     */     }
/*  78 */     PackageBinding packageBinding = binding == null ? null : (PackageBinding)binding;
/*  79 */     boolean isClassScope = scope.kind == 3;
/*  80 */     ReferenceBinding qualifiedType = null;
/*  81 */     int i = packageBinding == null ? 0 : packageBinding.compoundName.length; int max = this.tokens.length; for (int last = max - 1; i < max; i++) {
/*  82 */       findNextTypeBinding(i, scope, packageBinding);
/*  83 */       if (!this.resolvedType.isValidBinding())
/*  84 */         return this.resolvedType;
/*  85 */       if ((i == 0) && (this.resolvedType.isTypeVariable()) && (((TypeVariableBinding)this.resolvedType).firstBound == null)) {
/*  86 */         scope.problemReporter().illegalAccessFromTypeVariable((TypeVariableBinding)this.resolvedType, this);
/*  87 */         return null;
/*     */       }
/*  89 */       if ((i < last) && (isTypeUseDeprecated(this.resolvedType, scope))) {
/*  90 */         reportDeprecatedType(this.resolvedType, scope);
/*     */       }
/*  92 */       if ((isClassScope) && 
/*  93 */         (((ClassScope)scope).detectHierarchyCycle(this.resolvedType, this)))
/*  94 */         return null;
/*  95 */       ReferenceBinding currentType = (ReferenceBinding)this.resolvedType;
/*  96 */       if (qualifiedType != null) {
/*  97 */         ReferenceBinding enclosingType = currentType.enclosingType();
/*  98 */         if ((enclosingType != null) && (enclosingType.erasure() != qualifiedType.erasure())) {
/*  99 */           qualifiedType = enclosingType;
/*     */         }
/*     */ 
/* 102 */         if (currentType.isGenericType()) {
/* 103 */           qualifiedType = scope.environment().createRawType(currentType, qualifiedType);
/*     */         }
/*     */         else
/*     */         {
/*     */           boolean rawQualified;
/* 104 */           if (((rawQualified = qualifiedType.isRawType())) && (!currentType.isStatic()))
/* 105 */             qualifiedType = scope.environment().createRawType((ReferenceBinding)currentType.erasure(), qualifiedType);
/* 106 */           else if (((rawQualified) || (qualifiedType.isParameterizedType())) && (qualifiedType.erasure() == currentType.enclosingType().erasure()))
/* 107 */             qualifiedType = scope.environment().createParameterizedType((ReferenceBinding)currentType.erasure(), null, qualifiedType);
/*     */           else
/* 109 */             qualifiedType = currentType;
/*     */         }
/*     */       } else {
/* 112 */         qualifiedType = currentType.isGenericType() ? (ReferenceBinding)scope.environment().convertToRawType(currentType, false) : currentType;
/*     */       }
/*     */     }
/* 115 */     this.resolvedType = qualifiedType;
/* 116 */     return this.resolvedType;
/*     */   }
/*     */ 
/*     */   public char[][] getTypeName()
/*     */   {
/* 121 */     return this.tokens;
/*     */   }
/*     */ 
/*     */   public StringBuffer printExpression(int indent, StringBuffer output)
/*     */   {
/* 126 */     for (int i = 0; i < this.tokens.length; i++) {
/* 127 */       if (i > 0) output.append('.');
/* 128 */       output.append(this.tokens[i]);
/*     */     }
/* 130 */     return output;
/*     */   }
/*     */ 
/*     */   public void traverse(ASTVisitor visitor, BlockScope scope)
/*     */   {
/* 135 */     visitor.visit(this, scope);
/* 136 */     visitor.endVisit(this, scope);
/*     */   }
/*     */ 
/*     */   public void traverse(ASTVisitor visitor, ClassScope scope)
/*     */   {
/* 141 */     visitor.visit(this, scope);
/* 142 */     visitor.endVisit(this, scope);
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\ecj-3.5.2.jar
 * Qualified Name:     org.eclipse.jdt.internal.compiler.ast.QualifiedTypeReference
 * JD-Core Version:    0.6.0
 */