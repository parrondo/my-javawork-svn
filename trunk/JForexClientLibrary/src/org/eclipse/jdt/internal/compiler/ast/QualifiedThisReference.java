/*     */ package org.eclipse.jdt.internal.compiler.ast;
/*     */ 
/*     */ import org.eclipse.jdt.internal.compiler.ASTVisitor;
/*     */ import org.eclipse.jdt.internal.compiler.codegen.CodeStream;
/*     */ import org.eclipse.jdt.internal.compiler.flow.FlowContext;
/*     */ import org.eclipse.jdt.internal.compiler.flow.FlowInfo;
/*     */ import org.eclipse.jdt.internal.compiler.impl.Constant;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.BlockScope;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.ClassScope;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.LookupEnvironment;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.ReferenceBinding;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;
/*     */ import org.eclipse.jdt.internal.compiler.problem.ProblemReporter;
/*     */ 
/*     */ public class QualifiedThisReference extends ThisReference
/*     */ {
/*     */   public TypeReference qualification;
/*     */   ReferenceBinding currentCompatibleType;
/*     */ 
/*     */   public QualifiedThisReference(TypeReference name, int sourceStart, int sourceEnd)
/*     */   {
/*  25 */     super(sourceStart, sourceEnd);
/*  26 */     this.qualification = name;
/*  27 */     name.bits |= 1073741824;
/*  28 */     this.sourceStart = name.sourceStart;
/*     */   }
/*     */ 
/*     */   public FlowInfo analyseCode(BlockScope currentScope, FlowContext flowContext, FlowInfo flowInfo)
/*     */   {
/*  36 */     return flowInfo;
/*     */   }
/*     */ 
/*     */   public FlowInfo analyseCode(BlockScope currentScope, FlowContext flowContext, FlowInfo flowInfo, boolean valueRequired)
/*     */   {
/*  45 */     return flowInfo;
/*     */   }
/*     */ 
/*     */   public void generateCode(BlockScope currentScope, CodeStream codeStream, boolean valueRequired)
/*     */   {
/*  60 */     int pc = codeStream.position;
/*  61 */     if (valueRequired) {
/*  62 */       if ((this.bits & 0x1FE0) != 0) {
/*  63 */         Object[] emulationPath = 
/*  64 */           currentScope.getEmulationPath(this.currentCompatibleType, true, false);
/*  65 */         codeStream.generateOuterAccess(emulationPath, this, this.currentCompatibleType, currentScope);
/*     */       }
/*     */       else {
/*  68 */         codeStream.aload_0();
/*     */       }
/*     */     }
/*  71 */     codeStream.recordPositionsFrom(pc, this.sourceStart);
/*     */   }
/*     */ 
/*     */   public TypeBinding resolveType(BlockScope scope)
/*     */   {
/*  76 */     this.constant = Constant.NotAConstant;
/*     */ 
/*  78 */     TypeBinding type = this.qualification.resolveType(scope, true);
/*  79 */     if ((type == null) || (!type.isValidBinding())) return null;
/*     */ 
/*  81 */     type = type.erasure();
/*     */ 
/*  84 */     if ((type instanceof ReferenceBinding)) {
/*  85 */       this.resolvedType = scope.environment().convertToParameterizedType((ReferenceBinding)type);
/*     */     }
/*     */     else {
/*  88 */       this.resolvedType = type;
/*     */     }
/*     */ 
/*  93 */     int depth = 0;
/*  94 */     this.currentCompatibleType = scope.referenceType().binding;
/*  95 */     while ((this.currentCompatibleType != null) && (this.currentCompatibleType != type)) {
/*  96 */       depth++;
/*  97 */       this.currentCompatibleType = (this.currentCompatibleType.isStatic() ? null : this.currentCompatibleType.enclosingType());
/*     */     }
/*  99 */     this.bits &= -8161;
/* 100 */     this.bits |= (depth & 0xFF) << 5;
/*     */ 
/* 102 */     if (this.currentCompatibleType == null) {
/* 103 */       scope.problemReporter().noSuchEnclosingInstance(type, this, false);
/* 104 */       return this.resolvedType;
/*     */     }
/*     */ 
/* 108 */     if (depth == 0) {
/* 109 */       checkAccess(scope.methodScope());
/*     */     }
/*     */ 
/* 112 */     return this.resolvedType;
/*     */   }
/*     */ 
/*     */   public StringBuffer printExpression(int indent, StringBuffer output)
/*     */   {
/* 117 */     return this.qualification.print(0, output).append(".this");
/*     */   }
/*     */ 
/*     */   public void traverse(ASTVisitor visitor, BlockScope blockScope)
/*     */   {
/* 124 */     if (visitor.visit(this, blockScope)) {
/* 125 */       this.qualification.traverse(visitor, blockScope);
/*     */     }
/* 127 */     visitor.endVisit(this, blockScope);
/*     */   }
/*     */ 
/*     */   public void traverse(ASTVisitor visitor, ClassScope blockScope)
/*     */   {
/* 134 */     if (visitor.visit(this, blockScope)) {
/* 135 */       this.qualification.traverse(visitor, blockScope);
/*     */     }
/* 137 */     visitor.endVisit(this, blockScope);
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\ecj-3.5.2.jar
 * Qualified Name:     org.eclipse.jdt.internal.compiler.ast.QualifiedThisReference
 * JD-Core Version:    0.6.0
 */