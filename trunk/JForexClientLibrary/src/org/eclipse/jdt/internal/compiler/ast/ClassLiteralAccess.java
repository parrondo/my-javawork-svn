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
/*     */ import org.eclipse.jdt.internal.compiler.lookup.ClassScope;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.FieldBinding;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.LookupEnvironment;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.ReferenceBinding;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.SourceTypeBinding;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.TypeVariableBinding;
/*     */ import org.eclipse.jdt.internal.compiler.problem.ProblemReporter;
/*     */ 
/*     */ public class ClassLiteralAccess extends Expression
/*     */ {
/*     */   public TypeReference type;
/*     */   public TypeBinding targetType;
/*     */   FieldBinding syntheticField;
/*     */ 
/*     */   public ClassLiteralAccess(int sourceEnd, TypeReference type)
/*     */   {
/*  27 */     this.type = type;
/*  28 */     type.bits |= 1073741824;
/*  29 */     this.sourceStart = type.sourceStart;
/*  30 */     this.sourceEnd = sourceEnd;
/*     */   }
/*     */ 
/*     */   public FlowInfo analyseCode(BlockScope currentScope, FlowContext flowContext, FlowInfo flowInfo)
/*     */   {
/*  39 */     SourceTypeBinding sourceType = currentScope.outerMostClassScope().enclosingSourceType();
/*     */ 
/*  41 */     if ((!sourceType.isInterface()) && 
/*  42 */       (!this.targetType.isBaseType()) && 
/*  43 */       (currentScope.compilerOptions().sourceLevel < 3211264L)) {
/*  44 */       this.syntheticField = sourceType.addSyntheticFieldForClassLiteral(this.targetType, currentScope);
/*     */     }
/*  46 */     return flowInfo;
/*     */   }
/*     */ 
/*     */   public void generateCode(BlockScope currentScope, CodeStream codeStream, boolean valueRequired)
/*     */   {
/*  60 */     int pc = codeStream.position;
/*     */ 
/*  63 */     if (valueRequired) {
/*  64 */       codeStream.generateClassLiteralAccessForType(this.type.resolvedType, this.syntheticField);
/*  65 */       codeStream.generateImplicitConversion(this.implicitConversion);
/*     */     }
/*  67 */     codeStream.recordPositionsFrom(pc, this.sourceStart);
/*     */   }
/*     */ 
/*     */   public StringBuffer printExpression(int indent, StringBuffer output)
/*     */   {
/*  72 */     return this.type.print(0, output).append(".class");
/*     */   }
/*     */ 
/*     */   public TypeBinding resolveType(BlockScope scope)
/*     */   {
/*  77 */     this.constant = Constant.NotAConstant;
/*  78 */     if ((this.targetType = this.type.resolveType(scope, true)) == null) {
/*  79 */       return null;
/*     */     }
/*  81 */     if (this.targetType.isArrayType()) {
/*  82 */       ArrayBinding arrayBinding = (ArrayBinding)this.targetType;
/*  83 */       TypeBinding leafComponentType = arrayBinding.leafComponentType;
/*  84 */       if (leafComponentType == TypeBinding.VOID) {
/*  85 */         scope.problemReporter().cannotAllocateVoidArray(this);
/*  86 */         return null;
/*  87 */       }if (leafComponentType.isTypeVariable())
/*  88 */         scope.problemReporter().illegalClassLiteralForTypeVariable((TypeVariableBinding)leafComponentType, this);
/*     */     }
/*  90 */     else if (this.targetType.isTypeVariable()) {
/*  91 */       scope.problemReporter().illegalClassLiteralForTypeVariable((TypeVariableBinding)this.targetType, this);
/*     */     }
/*  93 */     ReferenceBinding classType = scope.getJavaLangClass();
/*  94 */     if (classType.isGenericType())
/*     */     {
/*  96 */       TypeBinding boxedType = null;
/*  97 */       if (this.targetType.id == 6)
/*  98 */         boxedType = scope.environment().getResolvedType(JAVA_LANG_VOID, scope);
/*     */       else {
/* 100 */         boxedType = scope.boxing(this.targetType);
/*     */       }
/* 102 */       this.resolvedType = scope.environment().createParameterizedType(classType, new TypeBinding[] { boxedType }, null);
/*     */     } else {
/* 104 */       this.resolvedType = classType;
/*     */     }
/* 106 */     return this.resolvedType;
/*     */   }
/*     */ 
/*     */   public void traverse(ASTVisitor visitor, BlockScope blockScope)
/*     */   {
/* 113 */     if (visitor.visit(this, blockScope)) {
/* 114 */       this.type.traverse(visitor, blockScope);
/*     */     }
/* 116 */     visitor.endVisit(this, blockScope);
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\ecj-3.5.2.jar
 * Qualified Name:     org.eclipse.jdt.internal.compiler.ast.ClassLiteralAccess
 * JD-Core Version:    0.6.0
 */