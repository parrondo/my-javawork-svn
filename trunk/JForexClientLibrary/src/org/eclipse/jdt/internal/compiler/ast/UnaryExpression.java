/*     */ package org.eclipse.jdt.internal.compiler.ast;
/*     */ 
/*     */ import org.eclipse.jdt.internal.compiler.ASTVisitor;
/*     */ import org.eclipse.jdt.internal.compiler.codegen.BranchLabel;
/*     */ import org.eclipse.jdt.internal.compiler.codegen.CodeStream;
/*     */ import org.eclipse.jdt.internal.compiler.flow.FlowContext;
/*     */ import org.eclipse.jdt.internal.compiler.flow.FlowInfo;
/*     */ import org.eclipse.jdt.internal.compiler.impl.BooleanConstant;
/*     */ import org.eclipse.jdt.internal.compiler.impl.CompilerOptions;
/*     */ import org.eclipse.jdt.internal.compiler.impl.Constant;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.BlockScope;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.LookupEnvironment;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;
/*     */ import org.eclipse.jdt.internal.compiler.problem.ProblemReporter;
/*     */ 
/*     */ public class UnaryExpression extends OperatorExpression
/*     */ {
/*     */   public Expression expression;
/*     */   public Constant optimizedBooleanConstant;
/*     */ 
/*     */   public UnaryExpression(Expression expression, int operator)
/*     */   {
/*  26 */     this.expression = expression;
/*  27 */     this.bits |= operator << 6;
/*     */   }
/*     */ 
/*     */   public FlowInfo analyseCode(BlockScope currentScope, FlowContext flowContext, FlowInfo flowInfo)
/*     */   {
/*  34 */     this.expression.checkNPE(currentScope, flowContext, flowInfo);
/*  35 */     if ((this.bits & 0xFC0) >> 6 == 11) {
/*  36 */       return this.expression
/*  37 */         .analyseCode(currentScope, flowContext, flowInfo)
/*  38 */         .asNegatedCondition();
/*     */     }
/*  40 */     return this.expression
/*  41 */       .analyseCode(currentScope, flowContext, flowInfo);
/*     */   }
/*     */ 
/*     */   public Constant optimizedBooleanConstant()
/*     */   {
/*  47 */     return this.optimizedBooleanConstant == null ? 
/*  48 */       this.constant : 
/*  49 */       this.optimizedBooleanConstant;
/*     */   }
/*     */ 
/*     */   public void generateCode(BlockScope currentScope, CodeStream codeStream, boolean valueRequired)
/*     */   {
/*  64 */     int pc = codeStream.position;
/*     */ 
/*  66 */     if (this.constant != Constant.NotAConstant)
/*     */     {
/*  68 */       if (valueRequired) {
/*  69 */         codeStream.generateConstant(this.constant, this.implicitConversion);
/*     */       }
/*  71 */       codeStream.recordPositionsFrom(pc, this.sourceStart);
/*  72 */       return;
/*     */     }
/*  74 */     switch ((this.bits & 0xFC0) >> 6) {
/*     */     case 11:
/*  76 */       switch ((this.expression.implicitConversion & 0xFF) >> 4)
/*     */       {
/*     */       case 5:
/*     */         BranchLabel falseLabel;
/*  80 */         this.expression.generateOptimizedBoolean(
/*  81 */           currentScope, 
/*  82 */           codeStream, 
/*  83 */           null, 
/*  84 */           falseLabel = new BranchLabel(codeStream), 
/*  85 */           valueRequired);
/*  86 */         if (valueRequired) {
/*  87 */           codeStream.iconst_0();
/*  88 */           if (falseLabel.forwardReferenceCount() > 0)
/*     */           {
/*     */             BranchLabel endifLabel;
/*  89 */             codeStream.goto_(endifLabel = new BranchLabel(codeStream));
/*  90 */             codeStream.decrStackSize(1);
/*  91 */             falseLabel.place();
/*  92 */             codeStream.iconst_1();
/*  93 */             endifLabel.place();
/*     */           }
/*     */         } else {
/*  96 */           falseLabel.place();
/*     */         }
/*     */       }
/*     */ 
/* 100 */       break;
/*     */     case 12:
/* 102 */       switch ((this.expression.implicitConversion & 0xFF) >> 4)
/*     */       {
/*     */       case 10:
/* 105 */         this.expression.generateCode(currentScope, codeStream, valueRequired);
/* 106 */         if (!valueRequired) break;
/* 107 */         codeStream.iconst_m1();
/* 108 */         codeStream.ixor();
/*     */ 
/* 110 */         break;
/*     */       case 7:
/* 112 */         this.expression.generateCode(currentScope, codeStream, valueRequired);
/* 113 */         if (!valueRequired) break;
/* 114 */         codeStream.ldc2_w(-1L);
/* 115 */         codeStream.lxor();
/*     */       case 8:
/*     */       case 9:
/* 118 */       }break;
/*     */     case 13:
/* 121 */       if (this.constant != Constant.NotAConstant) {
/* 122 */         if (!valueRequired) break;
/* 123 */         switch ((this.expression.implicitConversion & 0xFF) >> 4) {
/*     */         case 10:
/* 125 */           codeStream.generateInlinedValue(this.constant.intValue() * -1);
/* 126 */           break;
/*     */         case 9:
/* 128 */           codeStream.generateInlinedValue(this.constant.floatValue() * -1.0F);
/* 129 */           break;
/*     */         case 7:
/* 131 */           codeStream.generateInlinedValue(this.constant.longValue() * -1L);
/* 132 */           break;
/*     */         case 8:
/* 134 */           codeStream.generateInlinedValue(this.constant.doubleValue() * -1.0D);
/*     */         default:
/* 135 */           break;
/*     */         }
/*     */       } else {
/* 138 */         this.expression.generateCode(currentScope, codeStream, valueRequired);
/* 139 */         if (!valueRequired) break;
/* 140 */         switch ((this.expression.implicitConversion & 0xFF) >> 4) {
/*     */         case 10:
/* 142 */           codeStream.ineg();
/* 143 */           break;
/*     */         case 9:
/* 145 */           codeStream.fneg();
/* 146 */           break;
/*     */         case 7:
/* 148 */           codeStream.lneg();
/* 149 */           break;
/*     */         case 8:
/* 151 */           codeStream.dneg();
/*     */         }
/*     */       }
/*     */ 
/* 155 */       break;
/*     */     case 14:
/* 157 */       this.expression.generateCode(currentScope, codeStream, valueRequired);
/*     */     }
/* 159 */     if (valueRequired) {
/* 160 */       codeStream.generateImplicitConversion(this.implicitConversion);
/*     */     }
/* 162 */     codeStream.recordPositionsFrom(pc, this.sourceStart);
/*     */   }
/*     */ 
/*     */   public void generateOptimizedBoolean(BlockScope currentScope, CodeStream codeStream, BranchLabel trueLabel, BranchLabel falseLabel, boolean valueRequired)
/*     */   {
/* 176 */     if ((this.constant != Constant.NotAConstant) && (this.constant.typeID() == 5)) {
/* 177 */       super.generateOptimizedBoolean(
/* 178 */         currentScope, 
/* 179 */         codeStream, 
/* 180 */         trueLabel, 
/* 181 */         falseLabel, 
/* 182 */         valueRequired);
/* 183 */       return;
/*     */     }
/* 185 */     if ((this.bits & 0xFC0) >> 6 == 11)
/* 186 */       this.expression.generateOptimizedBoolean(
/* 187 */         currentScope, 
/* 188 */         codeStream, 
/* 189 */         falseLabel, 
/* 190 */         trueLabel, 
/* 191 */         valueRequired);
/*     */     else
/* 193 */       super.generateOptimizedBoolean(
/* 194 */         currentScope, 
/* 195 */         codeStream, 
/* 196 */         trueLabel, 
/* 197 */         falseLabel, 
/* 198 */         valueRequired);
/*     */   }
/*     */ 
/*     */   public StringBuffer printExpressionNoParenthesis(int indent, StringBuffer output)
/*     */   {
/* 204 */     output.append(operatorToString()).append(' ');
/* 205 */     return this.expression.printExpression(0, output);
/*     */   }
/*     */ 
/*     */   public TypeBinding resolveType(BlockScope scope)
/*     */   {
/* 210 */     boolean expressionIsCast;
/* 210 */     if ((expressionIsCast = this.expression instanceof CastExpression)) this.expression.bits |= 32;
/* 211 */     TypeBinding expressionType = this.expression.resolveType(scope);
/* 212 */     if (expressionType == null) {
/* 213 */       this.constant = Constant.NotAConstant;
/* 214 */       return null;
/*     */     }
/* 216 */     int expressionTypeID = expressionType.id;
/*     */ 
/* 218 */     boolean use15specifics = scope.compilerOptions().sourceLevel >= 3211264L;
/* 219 */     if ((use15specifics) && 
/* 220 */       (!expressionType.isBaseType())) {
/* 221 */       expressionTypeID = scope.environment().computeBoxingType(expressionType).id;
/*     */     }
/*     */ 
/* 224 */     if (expressionTypeID > 15) {
/* 225 */       this.constant = Constant.NotAConstant;
/* 226 */       scope.problemReporter().invalidOperator(this, expressionType);
/* 227 */       return null;
/*     */     }
/*     */     int tableId;
/*     */     int tableId;
/*     */     int tableId;
/* 231 */     switch ((this.bits & 0xFC0) >> 6) {
/*     */     case 11:
/* 233 */       tableId = 0;
/* 234 */       break;
/*     */     case 12:
/* 236 */       tableId = 10;
/* 237 */       break;
/*     */     default:
/* 239 */       tableId = 13;
/*     */     }
/*     */ 
/* 246 */     int operatorSignature = OperatorSignatures[tableId][((expressionTypeID << 4) + expressionTypeID)];
/* 247 */     this.expression.computeConversion(scope, TypeBinding.wellKnownType(scope, operatorSignature >>> 16 & 0xF), expressionType);
/* 248 */     this.bits |= operatorSignature & 0xF;
/* 249 */     switch (operatorSignature & 0xF) {
/*     */     case 5:
/* 251 */       this.resolvedType = TypeBinding.BOOLEAN;
/* 252 */       break;
/*     */     case 3:
/* 254 */       this.resolvedType = TypeBinding.BYTE;
/* 255 */       break;
/*     */     case 2:
/* 257 */       this.resolvedType = TypeBinding.CHAR;
/* 258 */       break;
/*     */     case 8:
/* 260 */       this.resolvedType = TypeBinding.DOUBLE;
/* 261 */       break;
/*     */     case 9:
/* 263 */       this.resolvedType = TypeBinding.FLOAT;
/* 264 */       break;
/*     */     case 10:
/* 266 */       this.resolvedType = TypeBinding.INT;
/* 267 */       break;
/*     */     case 7:
/* 269 */       this.resolvedType = TypeBinding.LONG;
/* 270 */       break;
/*     */     case 4:
/*     */     case 6:
/*     */     default:
/* 272 */       this.constant = Constant.NotAConstant;
/* 273 */       if (expressionTypeID != 0)
/* 274 */         scope.problemReporter().invalidOperator(this, expressionType);
/* 275 */       return null;
/*     */     }
/*     */ 
/* 278 */     if (this.expression.constant != Constant.NotAConstant) {
/* 279 */       this.constant = 
/* 280 */         Constant.computeConstantOperation(
/* 281 */         this.expression.constant, 
/* 282 */         expressionTypeID, 
/* 283 */         (this.bits & 0xFC0) >> 6);
/*     */     } else {
/* 285 */       this.constant = Constant.NotAConstant;
/* 286 */       if ((this.bits & 0xFC0) >> 6 == 11) {
/* 287 */         Constant cst = this.expression.optimizedBooleanConstant();
/* 288 */         if (cst != Constant.NotAConstant)
/* 289 */           this.optimizedBooleanConstant = BooleanConstant.fromValue(!cst.booleanValue());
/*     */       }
/*     */     }
/* 292 */     if (expressionIsCast)
/*     */     {
/* 294 */       CastExpression.checkNeedForArgumentCast(scope, tableId, operatorSignature, this.expression, expressionTypeID);
/*     */     }
/* 296 */     return this.resolvedType;
/*     */   }
/*     */ 
/*     */   public void traverse(ASTVisitor visitor, BlockScope blockScope)
/*     */   {
/* 303 */     if (visitor.visit(this, blockScope)) {
/* 304 */       this.expression.traverse(visitor, blockScope);
/*     */     }
/* 306 */     visitor.endVisit(this, blockScope);
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\ecj-3.5.2.jar
 * Qualified Name:     org.eclipse.jdt.internal.compiler.ast.UnaryExpression
 * JD-Core Version:    0.6.0
 */