/*     */ package org.eclipse.jdt.internal.compiler.ast;
/*     */ 
/*     */ import org.eclipse.jdt.internal.compiler.ASTVisitor;
/*     */ import org.eclipse.jdt.internal.compiler.codegen.CodeStream;
/*     */ import org.eclipse.jdt.internal.compiler.flow.FlowContext;
/*     */ import org.eclipse.jdt.internal.compiler.flow.FlowInfo;
/*     */ import org.eclipse.jdt.internal.compiler.impl.Constant;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.BlockScope;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.ClassScope;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.MethodScope;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;
/*     */ import org.eclipse.jdt.internal.compiler.problem.ProblemReporter;
/*     */ 
/*     */ public class ThisReference extends Reference
/*     */ {
/*     */   public static ThisReference implicitThis()
/*     */   {
/*  24 */     ThisReference implicitThis = new ThisReference(0, 0);
/*  25 */     implicitThis.bits |= 4;
/*  26 */     return implicitThis;
/*     */   }
/*     */ 
/*     */   public ThisReference(int sourceStart, int sourceEnd)
/*     */   {
/*  31 */     this.sourceStart = sourceStart;
/*  32 */     this.sourceEnd = sourceEnd;
/*     */   }
/*     */ 
/*     */   public FlowInfo analyseAssignment(BlockScope currentScope, FlowContext flowContext, FlowInfo flowInfo, Assignment assignment, boolean isCompound)
/*     */   {
/*  40 */     return flowInfo;
/*     */   }
/*     */ 
/*     */   public boolean checkAccess(MethodScope methodScope)
/*     */   {
/*  46 */     if (methodScope.isConstructorCall) {
/*  47 */       methodScope.problemReporter().fieldsOrThisBeforeConstructorInvocation(this);
/*  48 */       return false;
/*     */     }
/*     */ 
/*  52 */     if (methodScope.isStatic) {
/*  53 */       methodScope.problemReporter().errorThisSuperInStatic(this);
/*  54 */       return false;
/*     */     }
/*  56 */     return true;
/*     */   }
/*     */ 
/*     */   public void generateAssignment(BlockScope currentScope, CodeStream codeStream, Assignment assignment, boolean valueRequired)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void generateCode(BlockScope currentScope, CodeStream codeStream, boolean valueRequired)
/*     */   {
/*  69 */     int pc = codeStream.position;
/*  70 */     if (valueRequired)
/*  71 */       codeStream.aload_0();
/*  72 */     if ((this.bits & 0x4) == 0) codeStream.recordPositionsFrom(pc, this.sourceStart);
/*     */   }
/*     */ 
/*     */   public void generateCompoundAssignment(BlockScope currentScope, CodeStream codeStream, Expression expression, int operator, int assignmentImplicitConversion, boolean valueRequired)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void generatePostIncrement(BlockScope currentScope, CodeStream codeStream, CompoundAssignment postIncrement, boolean valueRequired)
/*     */   {
/*     */   }
/*     */ 
/*     */   public boolean isImplicitThis()
/*     */   {
/*  93 */     return (this.bits & 0x4) != 0;
/*     */   }
/*     */ 
/*     */   public boolean isThis()
/*     */   {
/*  98 */     return true;
/*     */   }
/*     */ 
/*     */   public int nullStatus(FlowInfo flowInfo) {
/* 102 */     return -1;
/*     */   }
/*     */ 
/*     */   public StringBuffer printExpression(int indent, StringBuffer output)
/*     */   {
/* 107 */     if (isImplicitThis()) return output;
/* 108 */     return output.append("this");
/*     */   }
/*     */ 
/*     */   public TypeBinding resolveType(BlockScope scope)
/*     */   {
/* 113 */     this.constant = Constant.NotAConstant;
/* 114 */     if ((!isImplicitThis()) && (!checkAccess(scope.methodScope()))) {
/* 115 */       return null;
/*     */     }
/* 117 */     return this.resolvedType = scope.enclosingReceiverType();
/*     */   }
/*     */ 
/*     */   public void traverse(ASTVisitor visitor, BlockScope blockScope)
/*     */   {
/* 122 */     visitor.visit(this, blockScope);
/* 123 */     visitor.endVisit(this, blockScope);
/*     */   }
/*     */ 
/*     */   public void traverse(ASTVisitor visitor, ClassScope blockScope) {
/* 127 */     visitor.visit(this, blockScope);
/* 128 */     visitor.endVisit(this, blockScope);
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\ecj-3.5.2.jar
 * Qualified Name:     org.eclipse.jdt.internal.compiler.ast.ThisReference
 * JD-Core Version:    0.6.0
 */