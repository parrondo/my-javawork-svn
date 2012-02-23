/*     */ package org.eclipse.jdt.internal.compiler.ast;
/*     */ 
/*     */ import org.eclipse.jdt.internal.compiler.ASTVisitor;
/*     */ import org.eclipse.jdt.internal.compiler.codegen.CodeStream;
/*     */ import org.eclipse.jdt.internal.compiler.flow.FlowContext;
/*     */ import org.eclipse.jdt.internal.compiler.flow.FlowInfo;
/*     */ import org.eclipse.jdt.internal.compiler.impl.Constant;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.BlockScope;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;
/*     */ 
/*     */ public class CombinedBinaryExpression extends BinaryExpression
/*     */ {
/*     */   public int arity;
/*     */   public int arityMax;
/*     */   public static final int ARITY_MAX_MAX = 160;
/*     */   public static final int ARITY_MAX_MIN = 20;
/*  84 */   public static int defaultArityMaxStartingValue = 20;
/*     */   public BinaryExpression[] referencesTable;
/*     */ 
/*     */   public CombinedBinaryExpression(Expression left, Expression right, int operator, int arity)
/*     */   {
/* 110 */     super(left, right, operator);
/* 111 */     initArity(left, arity);
/*     */   }
/*     */   public CombinedBinaryExpression(CombinedBinaryExpression expression) {
/* 114 */     super(expression);
/* 115 */     initArity(expression.left, expression.arity);
/*     */   }
/*     */ 
/*     */   public FlowInfo analyseCode(BlockScope currentScope, FlowContext flowContext, FlowInfo flowInfo)
/*     */   {
/* 121 */     if (this.referencesTable == null)
/* 122 */       return super.analyseCode(currentScope, flowContext, flowInfo);
/*     */     BinaryExpression cursor;
/* 125 */     if ((cursor = this.referencesTable[0]).resolvedType.id != 
/* 126 */       11) {
/* 127 */       cursor.left.checkNPE(currentScope, flowContext, flowInfo);
/*     */     }
/* 129 */     flowInfo = cursor.left.analyseCode(currentScope, flowContext, flowInfo)
/* 130 */       .unconditionalInits();
/* 131 */     int i = 0; for (int end = this.arity; i < end; i++) {
/* 132 */       if ((cursor = this.referencesTable[i]).resolvedType.id != 
/* 133 */         11) {
/* 134 */         cursor.right.checkNPE(currentScope, flowContext, flowInfo);
/*     */       }
/* 136 */       flowInfo = cursor.right
/* 137 */         .analyseCode(currentScope, flowContext, flowInfo)
/* 138 */         .unconditionalInits();
/*     */     }
/* 140 */     if (this.resolvedType.id != 11) {
/* 141 */       this.right.checkNPE(currentScope, flowContext, flowInfo);
/*     */     }
/* 143 */     return this.right.analyseCode(currentScope, flowContext, flowInfo)
/* 144 */       .unconditionalInits();
/*     */   }
/*     */ 
/*     */   public void generateOptimizedStringConcatenation(BlockScope blockScope, CodeStream codeStream, int typeID)
/*     */   {
/* 151 */     if (this.referencesTable == null) {
/* 152 */       super.generateOptimizedStringConcatenation(blockScope, codeStream, 
/* 153 */         typeID);
/*     */     } else {
/* 155 */       if ((this.bits & 0xFC0) >> 6 == 
/* 156 */         14) {
/* 157 */         if ((this.bits & 0xF) == 11) {
/* 158 */           if (this.constant != Constant.NotAConstant) {
/* 159 */             codeStream.generateConstant(this.constant, this.implicitConversion);
/* 160 */             codeStream.invokeStringConcatenationAppendForType(
/* 161 */               this.implicitConversion & 0xF);
/*     */ 
/* 160 */             return;
/*     */           }
/*     */ 
/* 163 */           BinaryExpression cursor = this.referencesTable[0];
/*     */ 
/* 165 */           int restart = 0;
/*     */ 
/* 167 */           int pc = codeStream.position;
/* 168 */           for (restart = this.arity - 1; restart >= 0; restart--) {
/* 169 */             if ((cursor = this.referencesTable[restart]).constant == 
/* 170 */               Constant.NotAConstant) continue;
/* 171 */             codeStream.generateConstant(cursor.constant, 
/* 172 */               cursor.implicitConversion);
/* 173 */             codeStream.invokeStringConcatenationAppendForType(
/* 174 */               cursor.implicitConversion & 0xF);
/* 175 */             break;
/*     */           }
/*     */ 
/* 195 */           restart++;
/* 196 */           if (restart == 0) {
/* 197 */             cursor.left.generateOptimizedStringConcatenation(
/* 198 */               blockScope, 
/* 199 */               codeStream, 
/* 200 */               cursor.left.implicitConversion & 0xF);
/*     */           }
/*     */ 
/* 203 */           for (int i = restart; i < this.arity; i++) {
/* 204 */             codeStream.recordPositionsFrom(pc, 
/* 205 */               (cursor = this.referencesTable[i]).left.sourceStart);
/* 206 */             int pcAux = codeStream.position;
/* 207 */             cursor.right.generateOptimizedStringConcatenation(blockScope, 
/* 208 */               codeStream, cursor.right.implicitConversion & 
/* 209 */               0xF);
/* 210 */             codeStream.recordPositionsFrom(pcAux, cursor.right.sourceStart);
/*     */           }
/* 212 */           codeStream.recordPositionsFrom(pc, this.left.sourceStart);
/* 213 */           pc = codeStream.position;
/* 214 */           this.right.generateOptimizedStringConcatenation(
/* 215 */             blockScope, 
/* 216 */             codeStream, 
/* 217 */             this.right.implicitConversion & 0xF);
/* 218 */           codeStream.recordPositionsFrom(pc, this.right.sourceStart); return;
/*     */         }
/*     */       }
/* 221 */       super.generateOptimizedStringConcatenation(blockScope, codeStream, 
/* 222 */         typeID);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void generateOptimizedStringConcatenationCreation(BlockScope blockScope, CodeStream codeStream, int typeID)
/*     */   {
/* 231 */     if (this.referencesTable == null) {
/* 232 */       super.generateOptimizedStringConcatenationCreation(blockScope, 
/* 233 */         codeStream, typeID);
/*     */     } else {
/* 235 */       if ((this.bits & 0xFC0) >> 6 == 
/* 236 */         14)
/* 237 */         if ((this.bits & 0xF) == 
/* 238 */           11)
/* 239 */           if (this.constant == Constant.NotAConstant) {
/* 240 */             int pc = codeStream.position;
/* 241 */             BinaryExpression cursor = this.referencesTable[(this.arity - 1)];
/*     */ 
/* 243 */             int restart = 0;
/* 244 */             for (restart = this.arity - 1; restart >= 0; restart--) {
/* 245 */               if (((cursor = this.referencesTable[restart]).bits & 
/* 246 */                 0xFC0) >> 6 == 
/* 247 */                 14) {
/* 248 */                 if ((cursor.bits & 0xF) == 
/* 249 */                   11) {
/* 250 */                   if (cursor.constant == Constant.NotAConstant) continue;
/* 251 */                   codeStream.newStringContatenation();
/* 252 */                   codeStream.dup();
/* 253 */                   codeStream.ldc(cursor.constant.stringValue());
/* 254 */                   codeStream.invokeStringConcatenationStringConstructor();
/*     */ 
/* 256 */                   break;
/*     */                 }
/*     */               }
/* 259 */               cursor.generateOptimizedStringConcatenationCreation(blockScope, 
/* 260 */                 codeStream, cursor.implicitConversion & 
/* 261 */                 0xF);
/* 262 */               break;
/*     */             }
/*     */ 
/* 265 */             restart++;
/* 266 */             if (restart == 0) {
/* 267 */               cursor.left.generateOptimizedStringConcatenationCreation(
/* 268 */                 blockScope, 
/* 269 */                 codeStream, 
/* 270 */                 cursor.left.implicitConversion & 0xF);
/*     */             }
/*     */ 
/* 273 */             for (int i = restart; i < this.arity; i++) {
/* 274 */               codeStream.recordPositionsFrom(pc, 
/* 275 */                 (cursor = this.referencesTable[i]).left.sourceStart);
/* 276 */               int pcAux = codeStream.position;
/* 277 */               cursor.right.generateOptimizedStringConcatenation(blockScope, 
/* 278 */                 codeStream, cursor.right.implicitConversion & 
/* 279 */                 0xF);
/* 280 */               codeStream.recordPositionsFrom(pcAux, cursor.right.sourceStart);
/*     */             }
/* 282 */             codeStream.recordPositionsFrom(pc, this.left.sourceStart);
/* 283 */             pc = codeStream.position;
/* 284 */             this.right.generateOptimizedStringConcatenation(
/* 285 */               blockScope, 
/* 286 */               codeStream, 
/* 287 */               this.right.implicitConversion & 0xF);
/* 288 */             codeStream.recordPositionsFrom(pc, this.right.sourceStart); return;
/*     */           }
/* 290 */       super.generateOptimizedStringConcatenationCreation(blockScope, 
/* 291 */         codeStream, typeID);
/*     */     }
/*     */   }
/*     */ 
/*     */   private void initArity(Expression expression, int value) {
/* 296 */     this.arity = value;
/* 297 */     if (value > 1) {
/* 298 */       this.referencesTable = new BinaryExpression[value];
/* 299 */       this.referencesTable[(value - 1)] = ((BinaryExpression)expression);
/* 300 */       for (int i = value - 1; i > 0; i--)
/* 301 */         this.referencesTable[(i - 1)] = 
/* 302 */           ((BinaryExpression)this.referencesTable[i].left);
/*     */     }
/*     */     else {
/* 305 */       this.arityMax = defaultArityMaxStartingValue;
/*     */     }
/*     */   }
/*     */ 
/*     */   public StringBuffer printExpressionNoParenthesis(int indent, StringBuffer output)
/*     */   {
/* 314 */     if (this.referencesTable == null) {
/* 315 */       return super.printExpressionNoParenthesis(indent, output);
/*     */     }
/* 317 */     String operatorString = operatorToString();
/* 318 */     for (int i = this.arity - 1; i >= 0; i--) {
/* 319 */       output.append('(');
/*     */     }
/* 321 */     output = this.referencesTable[0].left
/* 322 */       .printExpression(indent, output);
/* 323 */     int i = 0; int end = this.arity;
/* 324 */     for (; i < end; i++) {
/* 325 */       output.append(' ').append(operatorString).append(' ');
/* 326 */       output = this.referencesTable[i].right
/* 327 */         .printExpression(0, output);
/* 328 */       output.append(')');
/*     */     }
/* 330 */     output.append(' ').append(operatorString).append(' ');
/* 331 */     return this.right.printExpression(0, output);
/*     */   }
/*     */ 
/*     */   public TypeBinding resolveType(BlockScope scope)
/*     */   {
/* 336 */     if (this.referencesTable == null)
/* 337 */       return super.resolveType(scope);
/*     */     BinaryExpression cursor;
/* 340 */     if (((cursor = this.referencesTable[0]).left instanceof CastExpression)) {
/* 341 */       cursor.left.bits |= 32;
/*     */     }
/*     */ 
/* 344 */     cursor.left.resolveType(scope);
/* 345 */     int i = 0; for (int end = this.arity; i < end; i++) {
/* 346 */       this.referencesTable[i].nonRecursiveResolveTypeUpwards(scope);
/*     */     }
/* 348 */     nonRecursiveResolveTypeUpwards(scope);
/* 349 */     return this.resolvedType;
/*     */   }
/*     */ 
/*     */   public void traverse(ASTVisitor visitor, BlockScope scope) {
/* 353 */     if (this.referencesTable == null) {
/* 354 */       super.traverse(visitor, scope);
/*     */     } else {
/* 356 */       if (visitor.visit(this, scope))
/*     */       {
/* 358 */         int restart = this.arity - 1;
/* 359 */         while (restart >= 0)
/*     */         {
/* 361 */           if (!visitor.visit(
/* 362 */             this.referencesTable[restart], scope)) {
/* 363 */             visitor.endVisit(
/* 364 */               this.referencesTable[restart], scope);
/* 365 */             break;
/*     */           }
/* 360 */           restart--;
/*     */         }
/*     */ 
/* 368 */         restart++;
/*     */ 
/* 371 */         if (restart == 0) {
/* 372 */           this.referencesTable[0].left.traverse(visitor, scope);
/*     */         }
/* 374 */         int i = restart; int end = this.arity;
/* 375 */         for (; i < end; i++) {
/* 376 */           this.referencesTable[i].right.traverse(visitor, scope);
/* 377 */           visitor.endVisit(this.referencesTable[i], scope);
/*     */         }
/* 379 */         this.right.traverse(visitor, scope);
/*     */       }
/* 381 */       visitor.endVisit(this, scope);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void tuneArityMax()
/*     */   {
/* 395 */     if (this.arityMax < 160)
/* 396 */       this.arityMax *= 2;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\ecj-3.5.2.jar
 * Qualified Name:     org.eclipse.jdt.internal.compiler.ast.CombinedBinaryExpression
 * JD-Core Version:    0.6.0
 */