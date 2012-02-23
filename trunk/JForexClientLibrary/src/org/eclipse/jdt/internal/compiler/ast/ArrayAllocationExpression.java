/*     */ package org.eclipse.jdt.internal.compiler.ast;
/*     */ 
/*     */ import org.eclipse.jdt.internal.compiler.ASTVisitor;
/*     */ import org.eclipse.jdt.internal.compiler.codegen.CodeStream;
/*     */ import org.eclipse.jdt.internal.compiler.flow.FlowContext;
/*     */ import org.eclipse.jdt.internal.compiler.flow.FlowInfo;
/*     */ import org.eclipse.jdt.internal.compiler.impl.Constant;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.ArrayBinding;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.BlockScope;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;
/*     */ import org.eclipse.jdt.internal.compiler.problem.ProblemReporter;
/*     */ 
/*     */ public class ArrayAllocationExpression extends Expression
/*     */ {
/*     */   public TypeReference type;
/*     */   public Expression[] dimensions;
/*     */   public ArrayInitializer initializer;
/*     */ 
/*     */   public FlowInfo analyseCode(BlockScope currentScope, FlowContext flowContext, FlowInfo flowInfo)
/*     */   {
/*  29 */     int i = 0; for (int max = this.dimensions.length; i < max; i++)
/*     */     {
/*     */       Expression dim;
/*  31 */       if ((dim = this.dimensions[i]) != null) {
/*  32 */         flowInfo = dim.analyseCode(currentScope, flowContext, flowInfo);
/*     */       }
/*     */     }
/*  35 */     if (this.initializer != null) {
/*  36 */       return this.initializer.analyseCode(currentScope, flowContext, flowInfo);
/*     */     }
/*  38 */     return flowInfo;
/*     */   }
/*     */ 
/*     */   public void generateCode(BlockScope currentScope, CodeStream codeStream, boolean valueRequired)
/*     */   {
/*  46 */     int pc = codeStream.position;
/*     */ 
/*  48 */     if (this.initializer != null) {
/*  49 */       this.initializer.generateCode(currentScope, codeStream, valueRequired);
/*  50 */       return;
/*     */     }
/*     */ 
/*  53 */     int explicitDimCount = 0;
/*  54 */     int i = 0; for (int max = this.dimensions.length; i < max; i++)
/*     */     {
/*     */       Expression dimExpression;
/*  56 */       if ((dimExpression = this.dimensions[i]) == null) break;
/*  57 */       dimExpression.generateCode(currentScope, codeStream, true);
/*  58 */       explicitDimCount++;
/*     */     }
/*     */ 
/*  62 */     if (explicitDimCount == 1)
/*     */     {
/*  64 */       codeStream.newArray((ArrayBinding)this.resolvedType);
/*     */     }
/*     */     else {
/*  67 */       codeStream.multianewarray(this.resolvedType, explicitDimCount);
/*     */     }
/*  69 */     if (valueRequired)
/*  70 */       codeStream.generateImplicitConversion(this.implicitConversion);
/*     */     else {
/*  72 */       codeStream.pop();
/*     */     }
/*  74 */     codeStream.recordPositionsFrom(pc, this.sourceStart);
/*     */   }
/*     */ 
/*     */   public StringBuffer printExpression(int indent, StringBuffer output)
/*     */   {
/*  79 */     output.append("new ");
/*  80 */     this.type.print(0, output);
/*  81 */     for (int i = 0; i < this.dimensions.length; i++) {
/*  82 */       if (this.dimensions[i] == null) {
/*  83 */         output.append("[]");
/*     */       } else {
/*  85 */         output.append('[');
/*  86 */         this.dimensions[i].printExpression(0, output);
/*  87 */         output.append(']');
/*     */       }
/*     */     }
/*  90 */     if (this.initializer != null) this.initializer.printExpression(0, output);
/*  91 */     return output;
/*     */   }
/*     */ 
/*     */   public TypeBinding resolveType(BlockScope scope)
/*     */   {
/* 100 */     TypeBinding referenceType = this.type.resolveType(scope, true);
/*     */ 
/* 103 */     this.constant = Constant.NotAConstant;
/* 104 */     if (referenceType == TypeBinding.VOID) {
/* 105 */       scope.problemReporter().cannotAllocateVoidArray(this);
/* 106 */       referenceType = null;
/*     */     }
/*     */ 
/* 110 */     int explicitDimIndex = -1;
/* 111 */     int i = this.dimensions.length;
/*     */     do { if (this.dimensions[i] != null) {
/* 113 */         if (explicitDimIndex < 0) explicitDimIndex = i; 
/*     */       }
/* 114 */       else if (explicitDimIndex > 0)
/*     */       {
/* 116 */         scope.problemReporter().incorrectLocationForNonEmptyDimension(this, explicitDimIndex);
/* 117 */         break;
/*     */       }
/* 111 */       i--; } while (i >= 0);
/*     */ 
/* 123 */     if (this.initializer == null) {
/* 124 */       if (explicitDimIndex < 0) {
/* 125 */         scope.problemReporter().mustDefineDimensionsOrInitializer(this);
/*     */       }
/*     */ 
/* 128 */       if ((referenceType != null) && (!referenceType.isReifiable()))
/* 129 */         scope.problemReporter().illegalGenericArray(referenceType, this);
/*     */     }
/* 131 */     else if (explicitDimIndex >= 0) {
/* 132 */       scope.problemReporter().cannotDefineDimensionsAndInitializer(this);
/*     */     }
/*     */ 
/* 136 */     for (int i = 0; i <= explicitDimIndex; i++)
/*     */     {
/*     */       Expression dimExpression;
/* 138 */       if ((dimExpression = this.dimensions[i]) != null) {
/* 139 */         TypeBinding dimensionType = dimExpression.resolveTypeExpecting(scope, TypeBinding.INT);
/* 140 */         if (dimensionType != null) {
/* 141 */           this.dimensions[i].computeConversion(scope, TypeBinding.INT, dimensionType);
/*     */         }
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 147 */     if (referenceType != null) {
/* 148 */       if (this.dimensions.length > 255) {
/* 149 */         scope.problemReporter().tooManyDimensions(this);
/*     */       }
/* 151 */       this.resolvedType = scope.createArrayType(referenceType, this.dimensions.length);
/*     */ 
/* 154 */       if ((this.initializer != null) && 
/* 155 */         (this.initializer.resolveTypeExpecting(scope, this.resolvedType) != null)) {
/* 156 */         this.initializer.binding = ((ArrayBinding)this.resolvedType);
/*     */       }
/* 158 */       if ((referenceType.tagBits & 0x80) != 0L) {
/* 159 */         return null;
/*     */       }
/*     */     }
/* 162 */     return this.resolvedType;
/*     */   }
/*     */ 
/*     */   public void traverse(ASTVisitor visitor, BlockScope scope)
/*     */   {
/* 167 */     if (visitor.visit(this, scope)) {
/* 168 */       int dimensionsLength = this.dimensions.length;
/* 169 */       this.type.traverse(visitor, scope);
/* 170 */       for (int i = 0; i < dimensionsLength; i++) {
/* 171 */         if (this.dimensions[i] != null)
/* 172 */           this.dimensions[i].traverse(visitor, scope);
/*     */       }
/* 174 */       if (this.initializer != null)
/* 175 */         this.initializer.traverse(visitor, scope);
/*     */     }
/* 177 */     visitor.endVisit(this, scope);
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\ecj-3.5.2.jar
 * Qualified Name:     org.eclipse.jdt.internal.compiler.ast.ArrayAllocationExpression
 * JD-Core Version:    0.6.0
 */