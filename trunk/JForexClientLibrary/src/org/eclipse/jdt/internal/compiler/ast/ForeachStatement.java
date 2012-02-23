/*     */ package org.eclipse.jdt.internal.compiler.ast;
/*     */ 
/*     */ import org.eclipse.jdt.internal.compiler.ASTVisitor;
/*     */ import org.eclipse.jdt.internal.compiler.codegen.BranchLabel;
/*     */ import org.eclipse.jdt.internal.compiler.codegen.CodeStream;
/*     */ import org.eclipse.jdt.internal.compiler.flow.FlowContext;
/*     */ import org.eclipse.jdt.internal.compiler.flow.FlowInfo;
/*     */ import org.eclipse.jdt.internal.compiler.flow.LoopingFlowContext;
/*     */ import org.eclipse.jdt.internal.compiler.flow.UnconditionalFlowInfo;
/*     */ import org.eclipse.jdt.internal.compiler.impl.CompilerOptions;
/*     */ import org.eclipse.jdt.internal.compiler.impl.Constant;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.ArrayBinding;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.BlockScope;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.LocalVariableBinding;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.LookupEnvironment;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.MethodScope;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.ParameterizedTypeBinding;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.ReferenceBinding;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;
/*     */ import org.eclipse.jdt.internal.compiler.problem.ProblemReporter;
/*     */ 
/*     */ public class ForeachStatement extends Statement
/*     */ {
/*     */   public LocalDeclaration elementVariable;
/*  33 */   public int elementVariableImplicitWidening = -1;
/*     */   public Expression collection;
/*     */   public Statement action;
/*     */   private int kind;
/*     */   private static final int ARRAY = 0;
/*     */   private static final int RAW_ITERABLE = 1;
/*     */   private static final int GENERIC_ITERABLE = 2;
/*     */   private TypeBinding iteratorReceiverType;
/*     */   private TypeBinding collectionElementType;
/*     */   private BranchLabel breakLabel;
/*     */   private BranchLabel continueLabel;
/*     */   public BlockScope scope;
/*     */   public LocalVariableBinding indexVariable;
/*     */   public LocalVariableBinding collectionVariable;
/*     */   public LocalVariableBinding maxVariable;
/*  58 */   private static final char[] SecretIteratorVariableName = " iterator".toCharArray();
/*  59 */   private static final char[] SecretIndexVariableName = " index".toCharArray();
/*  60 */   private static final char[] SecretCollectionVariableName = " collection".toCharArray();
/*  61 */   private static final char[] SecretMaxVariableName = " max".toCharArray();
/*     */ 
/*  63 */   int postCollectionInitStateIndex = -1;
/*  64 */   int mergedInitStateIndex = -1;
/*     */ 
/*     */   public ForeachStatement(LocalDeclaration elementVariable, int start)
/*     */   {
/*  70 */     this.elementVariable = elementVariable;
/*  71 */     this.sourceStart = start;
/*  72 */     this.kind = -1;
/*     */   }
/*     */ 
/*     */   public FlowInfo analyseCode(BlockScope currentScope, FlowContext flowContext, FlowInfo flowInfo)
/*     */   {
/*  77 */     this.breakLabel = new BranchLabel();
/*  78 */     this.continueLabel = new BranchLabel();
/*  79 */     int initialComplaintLevel = (flowInfo.reachMode() & 0x1) != 0 ? 1 : 0;
/*     */ 
/*  82 */     this.collection.checkNPE(currentScope, flowContext, flowInfo);
/*  83 */     flowInfo = this.elementVariable.analyseCode(this.scope, flowContext, flowInfo);
/*  84 */     FlowInfo condInfo = this.collection.analyseCode(this.scope, flowContext, flowInfo.copy());
/*     */ 
/*  87 */     condInfo.markAsDefinitelyAssigned(this.elementVariable.binding);
/*     */ 
/*  89 */     this.postCollectionInitStateIndex = currentScope.methodScope().recordInitializationStates(condInfo);
/*     */ 
/*  92 */     LoopingFlowContext loopingContext = 
/*  93 */       new LoopingFlowContext(flowContext, flowInfo, this, this.breakLabel, 
/*  94 */       this.continueLabel, this.scope);
/*  95 */     UnconditionalFlowInfo actionInfo = 
/*  96 */       condInfo.nullInfoLessUnconditionalCopy();
/*  97 */     actionInfo.markAsDefinitelyUnknown(this.elementVariable.binding);
/*     */     FlowInfo exitBranch;
/*  99 */     if ((this.action != null) && ((!this.action.isEmptyBlock()) || 
/* 100 */       (currentScope.compilerOptions().complianceLevel > 3080192L)))
/*     */     {
/* 102 */       if (this.action.complainIfUnreachable(actionInfo, this.scope, initialComplaintLevel) < 2) {
/* 103 */         actionInfo = this.action.analyseCode(this.scope, loopingContext, actionInfo).unconditionalCopy();
/*     */       }
/*     */ 
/* 107 */       FlowInfo exitBranch = flowInfo.unconditionalCopy()
/* 108 */         .addInitializationsFrom(condInfo.initsWhenFalse());
/*     */ 
/* 110 */       if ((actionInfo.tagBits & loopingContext.initsOnContinue.tagBits & 
/* 111 */         0x1) != 0) {
/* 112 */         this.continueLabel = null;
/*     */       } else {
/* 114 */         actionInfo = actionInfo.mergedWith(loopingContext.initsOnContinue);
/* 115 */         loopingContext.complainOnDeferredFinalChecks(this.scope, actionInfo);
/* 116 */         exitBranch.addPotentialInitializationsFrom(actionInfo);
/*     */       }
/*     */     } else {
/* 119 */       exitBranch = condInfo.initsWhenFalse();
/*     */     }
/*     */ 
/* 124 */     boolean hasEmptyAction = (this.action == null) || 
/* 125 */       (this.action.isEmptyBlock()) || 
/* 126 */       ((this.action.bits & 0x1) != 0);
/*     */ 
/* 128 */     switch (this.kind) {
/*     */     case 0:
/* 130 */       if ((hasEmptyAction) && 
/* 131 */         (this.elementVariable.binding.resolvedPosition == -1)) break;
/* 132 */       this.collectionVariable.useFlag = 1;
/* 133 */       if (this.continueLabel == null) break;
/* 134 */       this.indexVariable.useFlag = 1;
/* 135 */       this.maxVariable.useFlag = 1;
/*     */ 
/* 138 */       break;
/*     */     case 1:
/*     */     case 2:
/* 141 */       this.indexVariable.useFlag = 1;
/*     */     }
/*     */ 
/* 145 */     loopingContext.complainOnDeferredNullChecks(currentScope, actionInfo);
/*     */ 
/* 147 */     FlowInfo mergedInfo = FlowInfo.mergedOptimizedBranches(
/* 148 */       (loopingContext.initsOnBreak.tagBits & 
/* 149 */       0x1) != 0 ? 
/* 150 */       loopingContext.initsOnBreak : 
/* 151 */       flowInfo.addInitializationsFrom(loopingContext.initsOnBreak), 
/* 152 */       false, 
/* 153 */       exitBranch, 
/* 154 */       false, 
/* 155 */       true);
/* 156 */     this.mergedInitStateIndex = currentScope.methodScope().recordInitializationStates(mergedInfo);
/* 157 */     return mergedInfo;
/*     */   }
/*     */ 
/*     */   public void generateCode(BlockScope currentScope, CodeStream codeStream)
/*     */   {
/* 168 */     if ((this.bits & 0x80000000) == 0) {
/* 169 */       return;
/*     */     }
/* 171 */     int pc = codeStream.position;
/* 172 */     boolean hasEmptyAction = (this.action == null) || 
/* 173 */       (this.action.isEmptyBlock()) || 
/* 174 */       ((this.action.bits & 0x1) != 0);
/*     */ 
/* 176 */     if ((hasEmptyAction) && 
/* 177 */       (this.elementVariable.binding.resolvedPosition == -1) && 
/* 178 */       (this.kind == 0)) {
/* 179 */       this.collection.generateCode(this.scope, codeStream, false);
/* 180 */       codeStream.exitUserScope(this.scope);
/* 181 */       if (this.mergedInitStateIndex != -1) {
/* 182 */         codeStream.removeNotDefinitelyAssignedVariables(currentScope, this.mergedInitStateIndex);
/* 183 */         codeStream.addDefinitelyAssignedVariables(currentScope, this.mergedInitStateIndex);
/*     */       }
/* 185 */       codeStream.recordPositionsFrom(pc, this.sourceStart);
/* 186 */       return;
/*     */     }
/*     */ 
/* 190 */     switch (this.kind) {
/*     */     case 0:
/* 192 */       this.collection.generateCode(this.scope, codeStream, true);
/* 193 */       codeStream.store(this.collectionVariable, true);
/* 194 */       codeStream.addVariable(this.collectionVariable);
/* 195 */       if (this.continueLabel == null)
/*     */         break;
/* 197 */       codeStream.arraylength();
/* 198 */       codeStream.store(this.maxVariable, false);
/* 199 */       codeStream.addVariable(this.maxVariable);
/* 200 */       codeStream.iconst_0();
/* 201 */       codeStream.store(this.indexVariable, false);
/* 202 */       codeStream.addVariable(this.indexVariable);
/*     */ 
/* 206 */       break;
/*     */     case 1:
/*     */     case 2:
/* 209 */       this.collection.generateCode(this.scope, codeStream, true);
/*     */ 
/* 211 */       codeStream.invokeIterableIterator(this.iteratorReceiverType);
/* 212 */       codeStream.store(this.indexVariable, false);
/* 213 */       codeStream.addVariable(this.indexVariable);
/*     */     }
/*     */ 
/* 217 */     BranchLabel actionLabel = new BranchLabel(codeStream);
/* 218 */     actionLabel.tagBits |= 2;
/* 219 */     BranchLabel conditionLabel = new BranchLabel(codeStream);
/* 220 */     conditionLabel.tagBits |= 2;
/* 221 */     this.breakLabel.initialize(codeStream);
/* 222 */     if (this.continueLabel == null)
/*     */     {
/* 224 */       conditionLabel.place();
/* 225 */       int conditionPC = codeStream.position;
/* 226 */       switch (this.kind)
/*     */       {
/*     */       case 0:
/* 230 */         codeStream.arraylength();
/* 231 */         codeStream.ifeq(this.breakLabel);
/* 232 */         break;
/*     */       case 1:
/*     */       case 2:
/* 235 */         codeStream.load(this.indexVariable);
/* 236 */         codeStream.invokeJavaUtilIteratorHasNext();
/* 237 */         codeStream.ifeq(this.breakLabel);
/*     */       }
/*     */ 
/* 240 */       codeStream.recordPositionsFrom(conditionPC, this.elementVariable.sourceStart);
/*     */     } else {
/* 242 */       this.continueLabel.initialize(codeStream);
/* 243 */       this.continueLabel.tagBits |= 2;
/*     */ 
/* 245 */       codeStream.goto_(conditionLabel);
/*     */     }
/*     */ 
/* 249 */     actionLabel.place();
/*     */ 
/* 252 */     switch (this.kind) {
/*     */     case 0:
/* 254 */       if (this.elementVariable.binding.resolvedPosition == -1) break;
/* 255 */       codeStream.load(this.collectionVariable);
/* 256 */       if (this.continueLabel == null)
/* 257 */         codeStream.iconst_0();
/*     */       else {
/* 259 */         codeStream.load(this.indexVariable);
/*     */       }
/* 261 */       codeStream.arrayAt(this.collectionElementType.id);
/* 262 */       if (this.elementVariableImplicitWidening != -1) {
/* 263 */         codeStream.generateImplicitConversion(this.elementVariableImplicitWidening);
/*     */       }
/* 265 */       codeStream.store(this.elementVariable.binding, false);
/* 266 */       codeStream.addVisibleLocalVariable(this.elementVariable.binding);
/* 267 */       if (this.postCollectionInitStateIndex == -1) break;
/* 268 */       codeStream.addDefinitelyAssignedVariables(
/* 269 */         currentScope, 
/* 270 */         this.postCollectionInitStateIndex);
/*     */ 
/* 273 */       break;
/*     */     case 1:
/*     */     case 2:
/* 276 */       codeStream.load(this.indexVariable);
/* 277 */       codeStream.invokeJavaUtilIteratorNext();
/* 278 */       if (this.elementVariable.binding.type.id != 1) {
/* 279 */         if (this.elementVariableImplicitWidening != -1) {
/* 280 */           codeStream.checkcast(this.collectionElementType);
/* 281 */           codeStream.generateImplicitConversion(this.elementVariableImplicitWidening);
/*     */         } else {
/* 283 */           codeStream.checkcast(this.elementVariable.binding.type);
/*     */         }
/*     */       }
/* 286 */       if (this.elementVariable.binding.resolvedPosition == -1) {
/* 287 */         codeStream.pop();
/*     */       } else {
/* 289 */         codeStream.store(this.elementVariable.binding, false);
/* 290 */         codeStream.addVisibleLocalVariable(this.elementVariable.binding);
/* 291 */         if (this.postCollectionInitStateIndex == -1) break;
/* 292 */         codeStream.addDefinitelyAssignedVariables(
/* 293 */           currentScope, 
/* 294 */           this.postCollectionInitStateIndex);
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 300 */     if (!hasEmptyAction) {
/* 301 */       this.action.generateCode(this.scope, codeStream);
/*     */     }
/* 303 */     codeStream.removeVariable(this.elementVariable.binding);
/* 304 */     if (this.postCollectionInitStateIndex != -1) {
/* 305 */       codeStream.removeNotDefinitelyAssignedVariables(currentScope, this.postCollectionInitStateIndex);
/*     */     }
/*     */ 
/* 308 */     if (this.continueLabel != null) {
/* 309 */       this.continueLabel.place();
/* 310 */       int continuationPC = codeStream.position;
/*     */ 
/* 312 */       switch (this.kind) {
/*     */       case 0:
/* 314 */         if ((!hasEmptyAction) || (this.elementVariable.binding.resolvedPosition >= 0)) {
/* 315 */           codeStream.iinc(this.indexVariable.resolvedPosition, 1);
/*     */         }
/*     */ 
/* 318 */         conditionLabel.place();
/* 319 */         codeStream.load(this.indexVariable);
/* 320 */         codeStream.load(this.maxVariable);
/* 321 */         codeStream.if_icmplt(actionLabel);
/* 322 */         break;
/*     */       case 1:
/*     */       case 2:
/* 326 */         conditionLabel.place();
/* 327 */         codeStream.load(this.indexVariable);
/* 328 */         codeStream.invokeJavaUtilIteratorHasNext();
/* 329 */         codeStream.ifne(actionLabel);
/*     */       }
/*     */ 
/* 332 */       codeStream.recordPositionsFrom(continuationPC, this.elementVariable.sourceStart);
/*     */     }
/* 334 */     switch (this.kind) {
/*     */     case 0:
/* 336 */       codeStream.removeVariable(this.indexVariable);
/* 337 */       codeStream.removeVariable(this.maxVariable);
/* 338 */       codeStream.removeVariable(this.collectionVariable);
/* 339 */       break;
/*     */     case 1:
/*     */     case 2:
/* 343 */       codeStream.removeVariable(this.indexVariable);
/*     */     }
/*     */ 
/* 346 */     codeStream.exitUserScope(this.scope);
/* 347 */     if (this.mergedInitStateIndex != -1) {
/* 348 */       codeStream.removeNotDefinitelyAssignedVariables(currentScope, this.mergedInitStateIndex);
/* 349 */       codeStream.addDefinitelyAssignedVariables(currentScope, this.mergedInitStateIndex);
/*     */     }
/* 351 */     this.breakLabel.place();
/* 352 */     codeStream.recordPositionsFrom(pc, this.sourceStart);
/*     */   }
/*     */ 
/*     */   public StringBuffer printStatement(int indent, StringBuffer output)
/*     */   {
/* 357 */     printIndent(indent, output).append("for (");
/* 358 */     this.elementVariable.printAsExpression(0, output);
/* 359 */     output.append(" : ");
/* 360 */     if (this.collection != null)
/* 361 */       this.collection.print(0, output).append(") ");
/*     */     else {
/* 363 */       output.append(')');
/*     */     }
/*     */ 
/* 366 */     if (this.action == null) {
/* 367 */       output.append(';');
/*     */     } else {
/* 369 */       output.append('\n');
/* 370 */       this.action.printStatement(indent + 1, output);
/*     */     }
/* 372 */     return output;
/*     */   }
/*     */ 
/*     */   public void resolve(BlockScope upperScope)
/*     */   {
/* 377 */     this.scope = new BlockScope(upperScope);
/* 378 */     this.elementVariable.resolve(this.scope);
/* 379 */     TypeBinding elementType = this.elementVariable.type.resolvedType;
/* 380 */     TypeBinding collectionType = this.collection == null ? null : this.collection.resolveType(this.scope);
/*     */ 
/* 382 */     TypeBinding expectedCollectionType = null;
/* 383 */     if ((elementType != null) && (collectionType != null)) {
/* 384 */       if (collectionType.isArrayType()) {
/* 385 */         this.kind = 0;
/* 386 */         this.collectionElementType = ((ArrayBinding)collectionType).elementsType();
/* 387 */         if ((!this.collectionElementType.isCompatibleWith(elementType)) && 
/* 388 */           (!this.scope.isBoxingCompatibleWith(this.collectionElementType, elementType))) {
/* 389 */           this.scope.problemReporter().notCompatibleTypesErrorInForeach(this.collection, this.collectionElementType, elementType);
/*     */         }
/*     */ 
/* 392 */         int compileTimeTypeID = this.collectionElementType.id;
/* 393 */         if (elementType.isBaseType()) {
/* 394 */           this.collection.computeConversion(this.scope, collectionType, collectionType);
/* 395 */           if (!this.collectionElementType.isBaseType()) {
/* 396 */             compileTimeTypeID = this.scope.environment().computeBoxingType(this.collectionElementType).id;
/* 397 */             this.elementVariableImplicitWidening = 1024;
/* 398 */             if (elementType.isBaseType()) {
/* 399 */               this.elementVariableImplicitWidening |= (elementType.id << 4) + compileTimeTypeID;
/* 400 */               this.scope.problemReporter().autoboxing(this.collection, this.collectionElementType, elementType);
/*     */             }
/*     */           } else {
/* 403 */             this.elementVariableImplicitWidening = ((elementType.id << 4) + compileTimeTypeID);
/*     */           }
/* 405 */         } else if (this.collectionElementType.isBaseType()) {
/* 406 */           this.collection.computeConversion(this.scope, collectionType, collectionType);
/* 407 */           int boxedID = this.scope.environment().computeBoxingType(this.collectionElementType).id;
/* 408 */           this.elementVariableImplicitWidening = (0x200 | compileTimeTypeID << 4 | compileTimeTypeID);
/* 409 */           compileTimeTypeID = boxedID;
/* 410 */           this.scope.problemReporter().autoboxing(this.collection, this.collectionElementType, elementType);
/*     */         } else {
/* 412 */           expectedCollectionType = upperScope.createArrayType(elementType, 1);
/* 413 */           this.collection.computeConversion(this.scope, expectedCollectionType, collectionType);
/*     */         }
/* 415 */       } else if ((collectionType instanceof ReferenceBinding)) {
/* 416 */         ReferenceBinding iterableType = ((ReferenceBinding)collectionType).findSuperTypeOriginatingFrom(38, false);
/* 417 */         boolean isTargetJsr14 = upperScope.compilerOptions().targetJDK == 3145728L;
/* 418 */         if ((iterableType == null) && (isTargetJsr14)) {
/* 419 */           iterableType = ((ReferenceBinding)collectionType).findSuperTypeOriginatingFrom(59, false);
/*     */         }
/*     */ 
/* 422 */         if (iterableType != null)
/*     */         {
/* 424 */           this.iteratorReceiverType = collectionType.erasure();
/* 425 */           if (isTargetJsr14) {
/* 426 */             if (((ReferenceBinding)this.iteratorReceiverType).findSuperTypeOriginatingFrom(59, false) == null) {
/* 427 */               this.iteratorReceiverType = iterableType;
/* 428 */               this.collection.computeConversion(this.scope, iterableType, collectionType);
/*     */             } else {
/* 430 */               this.collection.computeConversion(this.scope, collectionType, collectionType);
/*     */             }
/* 432 */           } else if (((ReferenceBinding)this.iteratorReceiverType).findSuperTypeOriginatingFrom(38, false) == null) {
/* 433 */             this.iteratorReceiverType = iterableType;
/* 434 */             this.collection.computeConversion(this.scope, iterableType, collectionType);
/*     */           } else {
/* 436 */             this.collection.computeConversion(this.scope, collectionType, collectionType);
/*     */           }
/*     */ 
/* 439 */           TypeBinding[] arguments = (TypeBinding[])null;
/* 440 */           switch (iterableType.kind()) {
/*     */           case 1028:
/* 442 */             this.kind = 1;
/* 443 */             this.collectionElementType = this.scope.getJavaLangObject();
/* 444 */             if ((this.collectionElementType.isCompatibleWith(elementType)) || 
/* 445 */               (this.scope.isBoxingCompatibleWith(this.collectionElementType, elementType))) break label910; this.scope.problemReporter().notCompatibleTypesErrorInForeach(this.collection, this.collectionElementType, elementType);
/*     */ 
/* 449 */             break;
/*     */           case 2052:
/* 452 */             arguments = iterableType.typeVariables();
/* 453 */             break;
/*     */           case 260:
/* 456 */             arguments = ((ParameterizedTypeBinding)iterableType).arguments;
/* 457 */             break;
/*     */           default:
/* 460 */             break;
/*     */           }
/*     */ 
/* 463 */           if (arguments.length == 1) {
/* 464 */             this.kind = 2;
/*     */ 
/* 466 */             this.collectionElementType = arguments[0];
/* 467 */             if ((!this.collectionElementType.isCompatibleWith(elementType)) && 
/* 468 */               (!this.scope.isBoxingCompatibleWith(this.collectionElementType, elementType))) {
/* 469 */               this.scope.problemReporter().notCompatibleTypesErrorInForeach(this.collection, this.collectionElementType, elementType);
/*     */             }
/* 471 */             int compileTimeTypeID = this.collectionElementType.id;
/*     */ 
/* 473 */             if (elementType.isBaseType()) {
/* 474 */               if (!this.collectionElementType.isBaseType()) {
/* 475 */                 compileTimeTypeID = this.scope.environment().computeBoxingType(this.collectionElementType).id;
/* 476 */                 this.elementVariableImplicitWidening = 1024;
/* 477 */                 if (elementType.isBaseType())
/* 478 */                   this.elementVariableImplicitWidening |= (elementType.id << 4) + compileTimeTypeID;
/*     */               }
/*     */               else {
/* 481 */                 this.elementVariableImplicitWidening = ((elementType.id << 4) + compileTimeTypeID);
/*     */               }
/*     */             }
/* 484 */             else if (this.collectionElementType.isBaseType()) {
/* 485 */               int boxedID = this.scope.environment().computeBoxingType(this.collectionElementType).id;
/* 486 */               this.elementVariableImplicitWidening = (0x200 | compileTimeTypeID << 4 | compileTimeTypeID);
/* 487 */               compileTimeTypeID = boxedID;
/*     */             }
/*     */           }
/*     */         }
/*     */       }
/* 492 */       label910: switch (this.kind)
/*     */       {
/*     */       case 0:
/* 495 */         this.indexVariable = new LocalVariableBinding(SecretIndexVariableName, TypeBinding.INT, 0, false);
/* 496 */         this.scope.addLocalVariable(this.indexVariable);
/* 497 */         this.indexVariable.setConstant(Constant.NotAConstant);
/*     */ 
/* 499 */         this.maxVariable = new LocalVariableBinding(SecretMaxVariableName, TypeBinding.INT, 0, false);
/* 500 */         this.scope.addLocalVariable(this.maxVariable);
/* 501 */         this.maxVariable.setConstant(Constant.NotAConstant);
/*     */ 
/* 503 */         if (expectedCollectionType == null)
/* 504 */           this.collectionVariable = new LocalVariableBinding(SecretCollectionVariableName, collectionType, 0, false);
/*     */         else {
/* 506 */           this.collectionVariable = new LocalVariableBinding(SecretCollectionVariableName, expectedCollectionType, 0, false);
/*     */         }
/* 508 */         this.scope.addLocalVariable(this.collectionVariable);
/* 509 */         this.collectionVariable.setConstant(Constant.NotAConstant);
/* 510 */         break;
/*     */       case 1:
/*     */       case 2:
/* 514 */         this.indexVariable = new LocalVariableBinding(SecretIteratorVariableName, this.scope.getJavaUtilIterator(), 0, false);
/* 515 */         this.scope.addLocalVariable(this.indexVariable);
/* 516 */         this.indexVariable.setConstant(Constant.NotAConstant);
/* 517 */         break;
/*     */       default:
/* 519 */         this.scope.problemReporter().invalidTypeForCollection(this.collection);
/*     */       }
/*     */     }
/* 522 */     if (this.action != null)
/* 523 */       this.action.resolve(this.scope);
/*     */   }
/*     */ 
/*     */   public void traverse(ASTVisitor visitor, BlockScope blockScope)
/*     */   {
/* 531 */     if (visitor.visit(this, blockScope)) {
/* 532 */       this.elementVariable.traverse(visitor, this.scope);
/* 533 */       if (this.collection != null) {
/* 534 */         this.collection.traverse(visitor, this.scope);
/*     */       }
/* 536 */       if (this.action != null) {
/* 537 */         this.action.traverse(visitor, this.scope);
/*     */       }
/*     */     }
/* 540 */     visitor.endVisit(this, blockScope);
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\ecj-3.5.2.jar
 * Qualified Name:     org.eclipse.jdt.internal.compiler.ast.ForeachStatement
 * JD-Core Version:    0.6.0
 */