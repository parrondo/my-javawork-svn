/*     */ package org.eclipse.jdt.internal.compiler.ast;
/*     */ 
/*     */ import org.eclipse.jdt.core.compiler.CharOperation;
/*     */ import org.eclipse.jdt.internal.compiler.ASTVisitor;
/*     */ import org.eclipse.jdt.internal.compiler.CompilationResult;
/*     */ import org.eclipse.jdt.internal.compiler.flow.ExceptionHandlingFlowContext;
/*     */ import org.eclipse.jdt.internal.compiler.flow.FlowInfo;
/*     */ import org.eclipse.jdt.internal.compiler.flow.InitializationFlowContext;
/*     */ import org.eclipse.jdt.internal.compiler.impl.CompilerOptions;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.ClassScope;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.MethodBinding;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.MethodScope;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.ReferenceBinding;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.SourceTypeBinding;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.TypeConstants;
/*     */ import org.eclipse.jdt.internal.compiler.parser.Parser;
/*     */ import org.eclipse.jdt.internal.compiler.problem.AbortMethod;
/*     */ import org.eclipse.jdt.internal.compiler.problem.ProblemReporter;
/*     */ 
/*     */ public class MethodDeclaration extends AbstractMethodDeclaration
/*     */ {
/*     */   public TypeReference returnType;
/*     */   public TypeParameter[] typeParameters;
/*     */ 
/*     */   public MethodDeclaration(CompilationResult compilationResult)
/*     */   {
/*  39 */     super(compilationResult);
/*     */   }
/*     */ 
/*     */   public void analyseCode(ClassScope classScope, InitializationFlowContext initializationContext, FlowInfo flowInfo)
/*     */   {
/*  44 */     if (this.ignoreFurtherInvestigation)
/*  45 */       return;
/*     */     try {
/*  47 */       if (this.binding == null) {
/*  48 */         return;
/*     */       }
/*  50 */       if ((!this.binding.isUsed()) && (!this.binding.isAbstract()) && 
/*  51 */         ((this.binding.isPrivate()) || (
/*  52 */         ((this.binding.modifiers & 0x30000000) == 0) && 
/*  53 */         (this.binding.isOrEnclosedByPrivateType()))) && 
/*  54 */         (!classScope.referenceCompilationUnit().compilationResult.hasSyntaxError)) {
/*  55 */         this.scope.problemReporter().unusedPrivateMethod(this);
/*     */       }
/*     */ 
/*  61 */       if ((this.binding.declaringClass.isEnum()) && ((this.selector == TypeConstants.VALUES) || (this.selector == TypeConstants.VALUEOF))) {
/*  62 */         return;
/*     */       }
/*     */ 
/*  65 */       if ((this.binding.isAbstract()) || (this.binding.isNative())) {
/*  66 */         return;
/*     */       }
/*  68 */       ExceptionHandlingFlowContext methodContext = 
/*  69 */         new ExceptionHandlingFlowContext(
/*  70 */         initializationContext, 
/*  71 */         this, 
/*  72 */         this.binding.thrownExceptions, 
/*  73 */         null, 
/*  74 */         this.scope, 
/*  75 */         FlowInfo.DEAD_END);
/*     */ 
/*  78 */       if (this.arguments != null) {
/*  79 */         int i = 0; for (int count = this.arguments.length; i < count; i++) {
/*  80 */           flowInfo.markAsDefinitelyAssigned(this.arguments[i].binding);
/*     */         }
/*     */       }
/*     */ 
/*  84 */       if (this.statements != null) {
/*  85 */         int complaintLevel = (flowInfo.reachMode() & 0x1) == 0 ? 0 : 1;
/*  86 */         int i = 0; for (int count = this.statements.length; i < count; i++) {
/*  87 */           Statement stat = this.statements[i];
/*  88 */           if ((complaintLevel = stat.complainIfUnreachable(flowInfo, this.scope, complaintLevel)) < 2) {
/*  89 */             flowInfo = stat.analyseCode(this.scope, methodContext, flowInfo);
/*     */           }
/*     */         }
/*     */       }
/*     */ 
/*  94 */       TypeBinding returnTypeBinding = this.binding.returnType;
/*  95 */       if ((returnTypeBinding == TypeBinding.VOID) || (isAbstract())) {
/*  96 */         if ((flowInfo.tagBits & 0x1) == 0) {
/*  97 */           this.bits |= 64;
/*     */         }
/*     */       }
/* 100 */       else if (flowInfo != FlowInfo.DEAD_END) {
/* 101 */         this.scope.problemReporter().shouldReturn(returnTypeBinding, this);
/*     */       }
/*     */ 
/* 105 */       methodContext.complainIfUnusedExceptionHandlers(this);
/*     */ 
/* 107 */       this.scope.checkUnusedParameters(this.binding);
/*     */     } catch (AbortMethod localAbortMethod) {
/* 109 */       this.ignoreFurtherInvestigation = true;
/*     */     }
/*     */   }
/*     */ 
/*     */   public boolean isMethod() {
/* 114 */     return true;
/*     */   }
/*     */ 
/*     */   public void parseStatements(Parser parser, CompilationUnitDeclaration unit)
/*     */   {
/* 119 */     parser.parse(this, unit);
/*     */   }
/*     */ 
/*     */   public StringBuffer printReturnType(int indent, StringBuffer output) {
/* 123 */     if (this.returnType == null) return output;
/* 124 */     return this.returnType.printExpression(0, output).append(' ');
/*     */   }
/*     */ 
/*     */   public void resolveStatements()
/*     */   {
/* 129 */     if ((this.returnType != null) && (this.binding != null)) {
/* 130 */       this.returnType.resolvedType = this.binding.returnType;
/*     */     }
/*     */ 
/* 134 */     if (CharOperation.equals(this.scope.enclosingSourceType().sourceName, this.selector)) {
/* 135 */       this.scope.problemReporter().methodWithConstructorName(this);
/*     */     }
/*     */ 
/* 138 */     if (this.typeParameters != null) {
/* 139 */       int i = 0; for (int length = this.typeParameters.length; i < length; i++) {
/* 140 */         this.typeParameters[i].resolve(this.scope);
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 145 */     CompilerOptions compilerOptions = this.scope.compilerOptions();
/*     */ 
/* 147 */     if (this.binding != null) {
/* 148 */       long sourceLevel = compilerOptions.sourceLevel;
/* 149 */       if (sourceLevel >= 3211264L) {
/* 150 */         int bindingModifiers = this.binding.modifiers;
/* 151 */         boolean hasOverrideAnnotation = (this.binding.tagBits & 0x0) != 0L;
/* 152 */         if (hasOverrideAnnotation)
/*     */         {
/* 154 */           if ((bindingModifiers & 0x10000008) != 268435456)
/*     */           {
/* 158 */             if ((sourceLevel < 3276800L) || 
/* 159 */               ((bindingModifiers & 0x20000008) != 536870912))
/*     */             {
/* 162 */               this.scope.problemReporter().methodMustOverride(this);
/*     */             }
/*     */           }
/* 163 */         } else if ((!this.binding.declaringClass.isInterface()) && 
/* 164 */           ((bindingModifiers & 0x10000008) == 268435456))
/*     */         {
/* 166 */           this.scope.problemReporter().missingOverrideAnnotation(this);
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/* 171 */     switch (TypeDeclaration.kind(this.scope.referenceType().modifiers)) {
/*     */     case 3:
/* 173 */       if ((this.selector == TypeConstants.VALUES) || 
/* 174 */         (this.selector == TypeConstants.VALUEOF))
/*     */       {
/*     */         break;
/*     */       }
/*     */     case 1:
/* 179 */       if ((this.modifiers & 0x1000000) != 0) {
/* 180 */         if (((this.modifiers & 0x100) != 0) || 
/* 181 */           ((this.modifiers & 0x400) != 0)) break;
/* 182 */         this.scope.problemReporter().methodNeedBody(this);
/*     */       }
/*     */       else {
/* 185 */         if (((this.modifiers & 0x100) == 0) && ((this.modifiers & 0x400) == 0)) break;
/* 186 */         this.scope.problemReporter().methodNeedingNoBody(this);
/*     */       }case 2:
/*     */     }
/* 189 */     super.resolveStatements();
/*     */ 
/* 192 */     if ((compilerOptions.getSeverity(537919488) != -1) && 
/* 193 */       (this.binding != null)) {
/* 194 */       int bindingModifiers = this.binding.modifiers;
/* 195 */       if (((bindingModifiers & 0x30000000) == 268435456) && 
/* 196 */         ((this.bits & 0x10) == 0))
/* 197 */         this.scope.problemReporter().overridesMethodWithoutSuperInvocation(this.binding);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void traverse(ASTVisitor visitor, ClassScope classScope)
/*     */   {
/* 207 */     if (visitor.visit(this, classScope)) {
/* 208 */       if (this.javadoc != null) {
/* 209 */         this.javadoc.traverse(visitor, this.scope);
/*     */       }
/* 211 */       if (this.annotations != null) {
/* 212 */         int annotationsLength = this.annotations.length;
/* 213 */         for (int i = 0; i < annotationsLength; i++)
/* 214 */           this.annotations[i].traverse(visitor, this.scope);
/*     */       }
/* 216 */       if (this.typeParameters != null) {
/* 217 */         int typeParametersLength = this.typeParameters.length;
/* 218 */         for (int i = 0; i < typeParametersLength; i++) {
/* 219 */           this.typeParameters[i].traverse(visitor, this.scope);
/*     */         }
/*     */       }
/* 222 */       if (this.returnType != null)
/* 223 */         this.returnType.traverse(visitor, this.scope);
/* 224 */       if (this.arguments != null) {
/* 225 */         int argumentLength = this.arguments.length;
/* 226 */         for (int i = 0; i < argumentLength; i++)
/* 227 */           this.arguments[i].traverse(visitor, this.scope);
/*     */       }
/* 229 */       if (this.thrownExceptions != null) {
/* 230 */         int thrownExceptionsLength = this.thrownExceptions.length;
/* 231 */         for (int i = 0; i < thrownExceptionsLength; i++)
/* 232 */           this.thrownExceptions[i].traverse(visitor, this.scope);
/*     */       }
/* 234 */       if (this.statements != null) {
/* 235 */         int statementsLength = this.statements.length;
/* 236 */         for (int i = 0; i < statementsLength; i++)
/* 237 */           this.statements[i].traverse(visitor, this.scope);
/*     */       }
/*     */     }
/* 240 */     visitor.endVisit(this, classScope);
/*     */   }
/*     */   public TypeParameter[] typeParameters() {
/* 243 */     return this.typeParameters;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\ecj-3.5.2.jar
 * Qualified Name:     org.eclipse.jdt.internal.compiler.ast.MethodDeclaration
 * JD-Core Version:    0.6.0
 */