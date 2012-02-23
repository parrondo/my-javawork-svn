/*     */ package org.eclipse.jdt.internal.compiler.ast;
/*     */ 
/*     */ import org.eclipse.jdt.internal.compiler.ASTVisitor;
/*     */ import org.eclipse.jdt.internal.compiler.codegen.CodeStream;
/*     */ import org.eclipse.jdt.internal.compiler.flow.FlowContext;
/*     */ import org.eclipse.jdt.internal.compiler.flow.FlowInfo;
/*     */ import org.eclipse.jdt.internal.compiler.impl.Constant;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.ArrayBinding;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.BlockScope;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.CompilationUnitScope;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;
/*     */ import org.eclipse.jdt.internal.compiler.problem.ProblemReporter;
/*     */ 
/*     */ public class ArrayInitializer extends Expression
/*     */ {
/*     */   public Expression[] expressions;
/*     */   public ArrayBinding binding;
/*     */ 
/*     */   public FlowInfo analyseCode(BlockScope currentScope, FlowContext flowContext, FlowInfo flowInfo)
/*     */   {
/*  34 */     if (this.expressions != null) {
/*  35 */       int i = 0; for (int max = this.expressions.length; i < max; i++) {
/*  36 */         flowInfo = this.expressions[i].analyseCode(currentScope, flowContext, flowInfo).unconditionalInits();
/*     */       }
/*     */     }
/*  39 */     return flowInfo;
/*     */   }
/*     */ 
/*     */   public void generateCode(BlockScope currentScope, CodeStream codeStream, boolean valueRequired)
/*     */   {
/*  48 */     int pc = codeStream.position;
/*  49 */     int expressionLength = this.expressions == null ? 0 : this.expressions.length;
/*  50 */     codeStream.generateInlinedValue(expressionLength);
/*  51 */     codeStream.newArray(this.binding);
/*  52 */     if (this.expressions != null)
/*     */     {
/*  54 */       int elementsTypeID = this.binding.dimensions > 1 ? -1 : this.binding.leafComponentType.id;
/*  55 */       for (int i = 0; i < expressionLength; i++)
/*     */       {
/*     */         Expression expr;
/*  57 */         if ((expr = this.expressions[i]).constant != Constant.NotAConstant) {
/*  58 */           switch (elementsTypeID) {
/*     */           case 2:
/*     */           case 3:
/*     */           case 4:
/*     */           case 7:
/*     */           case 10:
/*  64 */             if (expr.constant.longValue() == 0L) continue;
/*  65 */             codeStream.dup();
/*  66 */             codeStream.generateInlinedValue(i);
/*  67 */             expr.generateCode(currentScope, codeStream, true);
/*  68 */             codeStream.arrayAtPut(elementsTypeID, false);
/*     */ 
/*  70 */             break;
/*     */           case 8:
/*     */           case 9:
/*  73 */             double constantValue = expr.constant.doubleValue();
/*  74 */             if ((constantValue != -0.0D) && (constantValue == 0.0D)) continue;
/*  75 */             codeStream.dup();
/*  76 */             codeStream.generateInlinedValue(i);
/*  77 */             expr.generateCode(currentScope, codeStream, true);
/*  78 */             codeStream.arrayAtPut(elementsTypeID, false);
/*     */ 
/*  80 */             break;
/*     */           case 5:
/*  82 */             if (!expr.constant.booleanValue()) continue;
/*  83 */             codeStream.dup();
/*  84 */             codeStream.generateInlinedValue(i);
/*  85 */             expr.generateCode(currentScope, codeStream, true);
/*  86 */             codeStream.arrayAtPut(elementsTypeID, false);
/*     */ 
/*  88 */             break;
/*     */           case 6:
/*     */           default:
/*  90 */             if ((expr instanceof NullLiteral)) continue;
/*  91 */             codeStream.dup();
/*  92 */             codeStream.generateInlinedValue(i);
/*  93 */             expr.generateCode(currentScope, codeStream, true);
/*  94 */             codeStream.arrayAtPut(elementsTypeID, false); break;
/*     */           }
/*     */         }
/*  97 */         else if (!(expr instanceof NullLiteral)) {
/*  98 */           codeStream.dup();
/*  99 */           codeStream.generateInlinedValue(i);
/* 100 */           expr.generateCode(currentScope, codeStream, true);
/* 101 */           codeStream.arrayAtPut(elementsTypeID, false);
/*     */         }
/*     */       }
/*     */     }
/* 105 */     if (valueRequired)
/* 106 */       codeStream.generateImplicitConversion(this.implicitConversion);
/*     */     else {
/* 108 */       codeStream.pop();
/*     */     }
/* 110 */     codeStream.recordPositionsFrom(pc, this.sourceStart);
/*     */   }
/*     */ 
/*     */   public StringBuffer printExpression(int indent, StringBuffer output)
/*     */   {
/* 115 */     output.append('{');
/* 116 */     if (this.expressions != null) {
/* 117 */       int j = 20;
/* 118 */       for (int i = 0; i < this.expressions.length; i++) {
/* 119 */         if (i > 0) output.append(", ");
/* 120 */         this.expressions[i].printExpression(0, output);
/* 121 */         j--;
/* 122 */         if (j == 0) {
/* 123 */           output.append('\n');
/* 124 */           printIndent(indent + 1, output);
/* 125 */           j = 20;
/*     */         }
/*     */       }
/*     */     }
/* 129 */     return output.append('}');
/*     */   }
/*     */ 
/*     */   public TypeBinding resolveTypeExpecting(BlockScope scope, TypeBinding expectedType)
/*     */   {
/* 139 */     this.constant = Constant.NotAConstant;
/*     */ 
/* 141 */     if ((expectedType instanceof ArrayBinding))
/*     */     {
/* 143 */       if ((this.bits & 0x1) == 0)
/*     */       {
/* 145 */         TypeBinding leafComponentType = expectedType.leafComponentType();
/* 146 */         if (!leafComponentType.isReifiable()) {
/* 147 */           scope.problemReporter().illegalGenericArray(leafComponentType, this);
/*     */         }
/*     */       }
/* 150 */       this.resolvedType = (this.binding = (ArrayBinding)expectedType);
/* 151 */       if (this.expressions == null)
/* 152 */         return this.binding;
/* 153 */       TypeBinding elementType = this.binding.elementsType();
/* 154 */       int i = 0; for (int length = this.expressions.length; i < length; i++) {
/* 155 */         Expression expression = this.expressions[i];
/* 156 */         expression.setExpectedType(elementType);
/* 157 */         TypeBinding expressionType = (expression instanceof ArrayInitializer) ? 
/* 158 */           expression.resolveTypeExpecting(scope, elementType) : 
/* 159 */           expression.resolveType(scope);
/* 160 */         if (expressionType == null)
/*     */         {
/*     */           continue;
/*     */         }
/* 164 */         if (elementType != expressionType) {
/* 165 */           scope.compilationUnitScope().recordTypeConversion(elementType, expressionType);
/*     */         }
/* 167 */         if ((expression.isConstantValueOfTypeAssignableToType(expressionType, elementType)) || 
/* 168 */           (expressionType.isCompatibleWith(elementType)))
/* 169 */           expression.computeConversion(scope, elementType, expressionType);
/* 170 */         else if (isBoxingCompatible(expressionType, elementType, expression, scope))
/* 171 */           expression.computeConversion(scope, elementType, expressionType);
/*     */         else {
/* 173 */           scope.problemReporter().typeMismatchError(expressionType, elementType, expression, null);
/*     */         }
/*     */       }
/* 176 */       return this.binding;
/*     */     }
/*     */ 
/* 180 */     TypeBinding leafElementType = null;
/* 181 */     int dim = 1;
/* 182 */     if (this.expressions == null) {
/* 183 */       leafElementType = scope.getJavaLangObject();
/*     */     } else {
/* 185 */       Expression expression = this.expressions[0];
/* 186 */       while ((expression != null) && ((expression instanceof ArrayInitializer))) {
/* 187 */         dim++;
/* 188 */         Expression[] subExprs = ((ArrayInitializer)expression).expressions;
/* 189 */         if (subExprs == null) {
/* 190 */           leafElementType = scope.getJavaLangObject();
/* 191 */           expression = null;
/* 192 */           break;
/*     */         }
/* 194 */         expression = ((ArrayInitializer)expression).expressions[0];
/*     */       }
/* 196 */       if (expression != null) {
/* 197 */         leafElementType = expression.resolveType(scope);
/*     */       }
/*     */ 
/* 200 */       int i = 1; for (int length = this.expressions.length; i < length; i++) {
/* 201 */         expression = this.expressions[i];
/* 202 */         if (expression != null)
/* 203 */           expression.resolveType(scope);
/*     */       }
/*     */     }
/* 206 */     if (leafElementType != null) {
/* 207 */       this.resolvedType = scope.createArrayType(leafElementType, dim);
/* 208 */       if (expectedType != null)
/* 209 */         scope.problemReporter().typeMismatchError(this.resolvedType, expectedType, this, null);
/*     */     }
/* 211 */     return null;
/*     */   }
/*     */ 
/*     */   public void traverse(ASTVisitor visitor, BlockScope scope)
/*     */   {
/* 216 */     if ((visitor.visit(this, scope)) && 
/* 217 */       (this.expressions != null)) {
/* 218 */       int expressionsLength = this.expressions.length;
/* 219 */       for (int i = 0; i < expressionsLength; i++) {
/* 220 */         this.expressions[i].traverse(visitor, scope);
/*     */       }
/*     */     }
/* 223 */     visitor.endVisit(this, scope);
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\ecj-3.5.2.jar
 * Qualified Name:     org.eclipse.jdt.internal.compiler.ast.ArrayInitializer
 * JD-Core Version:    0.6.0
 */