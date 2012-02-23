/*      */ package org.eclipse.jdt.internal.compiler.ast;
/*      */ 
/*      */ import org.eclipse.jdt.core.compiler.CharOperation;
/*      */ import org.eclipse.jdt.internal.compiler.ASTVisitor;
/*      */ import org.eclipse.jdt.internal.compiler.codegen.CodeStream;
/*      */ import org.eclipse.jdt.internal.compiler.flow.FlowContext;
/*      */ import org.eclipse.jdt.internal.compiler.flow.FlowInfo;
/*      */ import org.eclipse.jdt.internal.compiler.flow.NullInfoRegistry;
/*      */ import org.eclipse.jdt.internal.compiler.impl.CompilerOptions;
/*      */ import org.eclipse.jdt.internal.compiler.impl.Constant;
/*      */ import org.eclipse.jdt.internal.compiler.lookup.Binding;
/*      */ import org.eclipse.jdt.internal.compiler.lookup.BlockScope;
/*      */ import org.eclipse.jdt.internal.compiler.lookup.ClassScope;
/*      */ import org.eclipse.jdt.internal.compiler.lookup.FieldBinding;
/*      */ import org.eclipse.jdt.internal.compiler.lookup.LocalVariableBinding;
/*      */ import org.eclipse.jdt.internal.compiler.lookup.LookupEnvironment;
/*      */ import org.eclipse.jdt.internal.compiler.lookup.MethodBinding;
/*      */ import org.eclipse.jdt.internal.compiler.lookup.MethodScope;
/*      */ import org.eclipse.jdt.internal.compiler.lookup.MissingTypeBinding;
/*      */ import org.eclipse.jdt.internal.compiler.lookup.ProblemFieldBinding;
/*      */ import org.eclipse.jdt.internal.compiler.lookup.ProblemReferenceBinding;
/*      */ import org.eclipse.jdt.internal.compiler.lookup.ReferenceBinding;
/*      */ import org.eclipse.jdt.internal.compiler.lookup.Scope;
/*      */ import org.eclipse.jdt.internal.compiler.lookup.SourceTypeBinding;
/*      */ import org.eclipse.jdt.internal.compiler.lookup.SyntheticMethodBinding;
/*      */ import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;
/*      */ import org.eclipse.jdt.internal.compiler.lookup.VariableBinding;
/*      */ import org.eclipse.jdt.internal.compiler.problem.ProblemReporter;
/*      */ 
/*      */ public class QualifiedNameReference extends NameReference
/*      */ {
/*      */   public char[][] tokens;
/*      */   public long[] sourcePositions;
/*      */   public FieldBinding[] otherBindings;
/*      */   int[] otherDepths;
/*      */   public int indexOfFirstFieldBinding;
/*      */   public SyntheticMethodBinding syntheticWriteAccessor;
/*      */   public SyntheticMethodBinding[] syntheticReadAccessors;
/*      */   public TypeBinding genericCast;
/*      */   public TypeBinding[] otherGenericCasts;
/*      */ 
/*      */   public QualifiedNameReference(char[][] tokens, long[] positions, int sourceStart, int sourceEnd)
/*      */   {
/*   56 */     this.tokens = tokens;
/*   57 */     this.sourcePositions = positions;
/*   58 */     this.sourceStart = sourceStart;
/*   59 */     this.sourceEnd = sourceEnd;
/*      */   }
/*      */ 
/*      */   public FlowInfo analyseAssignment(BlockScope currentScope, FlowContext flowContext, FlowInfo flowInfo, Assignment assignment, boolean isCompound)
/*      */   {
/*   64 */     int otherBindingsCount = this.otherBindings == null ? 0 : this.otherBindings.length;
/*   65 */     boolean needValue = (otherBindingsCount == 0) || (!this.otherBindings[0].isStatic());
/*   66 */     boolean complyTo14 = currentScope.compilerOptions().complianceLevel >= 3145728L;
/*   67 */     FieldBinding lastFieldBinding = null;
/*   68 */     switch (this.bits & 0x7) {
/*      */     case 1:
/*   70 */       lastFieldBinding = (FieldBinding)this.binding;
/*   71 */       if ((needValue) || (complyTo14)) {
/*   72 */         manageSyntheticAccessIfNecessary(currentScope, lastFieldBinding, 0, flowInfo);
/*      */       }
/*      */ 
/*   75 */       if ((!lastFieldBinding.isBlankFinal()) || 
/*   76 */         (this.otherBindings == null) || 
/*   77 */         (!currentScope.needBlankFinalFieldInitializationCheck(lastFieldBinding))) break;
/*   78 */       FlowInfo fieldInits = flowContext.getInitsForFinalBlankInitializationCheck(lastFieldBinding.declaringClass.original(), flowInfo);
/*   79 */       if (fieldInits.isDefinitelyAssigned(lastFieldBinding)) break;
/*   80 */       currentScope.problemReporter().uninitializedBlankFinalField(lastFieldBinding, this);
/*      */ 
/*   83 */       break;
/*      */     case 2:
/*      */       LocalVariableBinding localBinding;
/*   88 */       if (!flowInfo
/*   88 */         .isDefinitelyAssigned(localBinding = (LocalVariableBinding)this.binding)) {
/*   89 */         currentScope.problemReporter().uninitializedLocalVariable(localBinding, this);
/*      */       }
/*   91 */       if ((flowInfo.tagBits & 0x1) == 0)
/*   92 */         localBinding.useFlag = 1;
/*   93 */       else if (localBinding.useFlag == 0) {
/*   94 */         localBinding.useFlag = 2;
/*      */       }
/*   96 */       checkNPE(currentScope, flowContext, flowInfo, true);
/*      */     }
/*      */ 
/*   99 */     if (needValue) {
/*  100 */       manageEnclosingInstanceAccessIfNecessary(currentScope, flowInfo);
/*      */     }
/*      */ 
/*  104 */     if (this.otherBindings != null) {
/*  105 */       for (int i = 0; i < otherBindingsCount - 1; i++) {
/*  106 */         lastFieldBinding = this.otherBindings[i];
/*  107 */         needValue = !this.otherBindings[(i + 1)].isStatic();
/*  108 */         if ((needValue) || (complyTo14)) {
/*  109 */           manageSyntheticAccessIfNecessary(currentScope, lastFieldBinding, i + 1, flowInfo);
/*      */         }
/*      */       }
/*  112 */       lastFieldBinding = this.otherBindings[(otherBindingsCount - 1)];
/*      */     }
/*      */ 
/*  115 */     if (isCompound) {
/*  116 */       if ((otherBindingsCount == 0) && 
/*  117 */         (lastFieldBinding.isBlankFinal()) && 
/*  118 */         (currentScope.needBlankFinalFieldInitializationCheck(lastFieldBinding))) {
/*  119 */         FlowInfo fieldInits = flowContext.getInitsForFinalBlankInitializationCheck(lastFieldBinding.declaringClass, flowInfo);
/*  120 */         if (!fieldInits.isDefinitelyAssigned(lastFieldBinding)) {
/*  121 */           currentScope.problemReporter().uninitializedBlankFinalField(lastFieldBinding, this);
/*      */         }
/*      */       }
/*  124 */       manageSyntheticAccessIfNecessary(currentScope, lastFieldBinding, otherBindingsCount, flowInfo);
/*      */     }
/*      */ 
/*  127 */     if (assignment.expression != null) {
/*  128 */       flowInfo = 
/*  129 */         assignment.expression
/*  131 */         .analyseCode(currentScope, flowContext, flowInfo)
/*  132 */         .unconditionalInits();
/*      */     }
/*      */ 
/*  136 */     if (lastFieldBinding.isFinal())
/*      */     {
/*  138 */       if ((otherBindingsCount == 0) && 
/*  139 */         (this.indexOfFirstFieldBinding == 1) && 
/*  140 */         (lastFieldBinding.isBlankFinal()) && 
/*  141 */         (!isCompound) && 
/*  142 */         (currentScope.allowBlankFinalFieldAssignment(lastFieldBinding))) {
/*  143 */         if (flowInfo.isPotentiallyAssigned(lastFieldBinding))
/*  144 */           currentScope.problemReporter().duplicateInitializationOfBlankFinalField(lastFieldBinding, this);
/*      */         else {
/*  146 */           flowContext.recordSettingFinal(lastFieldBinding, this, flowInfo);
/*      */         }
/*  148 */         flowInfo.markAsDefinitelyAssigned(lastFieldBinding);
/*      */       } else {
/*  150 */         currentScope.problemReporter().cannotAssignToFinalField(lastFieldBinding, this);
/*  151 */         if ((otherBindingsCount == 0) && (currentScope.allowBlankFinalFieldAssignment(lastFieldBinding))) {
/*  152 */           flowInfo.markAsDefinitelyAssigned(lastFieldBinding);
/*      */         }
/*      */       }
/*      */     }
/*  156 */     manageSyntheticAccessIfNecessary(currentScope, lastFieldBinding, -1, flowInfo);
/*      */ 
/*  158 */     return flowInfo;
/*      */   }
/*      */ 
/*      */   public FlowInfo analyseCode(BlockScope currentScope, FlowContext flowContext, FlowInfo flowInfo) {
/*  162 */     return analyseCode(currentScope, flowContext, flowInfo, true);
/*      */   }
/*      */ 
/*      */   public FlowInfo analyseCode(BlockScope currentScope, FlowContext flowContext, FlowInfo flowInfo, boolean valueRequired)
/*      */   {
/*  167 */     int otherBindingsCount = this.otherBindings == null ? 0 : this.otherBindings.length;
/*      */ 
/*  169 */     boolean needValue = this.otherBindings[0].isStatic() ? false : otherBindingsCount == 0 ? valueRequired : true;
/*  170 */     boolean complyTo14 = currentScope.compilerOptions().complianceLevel >= 3145728L;
/*  171 */     switch (this.bits & 0x7) {
/*      */     case 1:
/*  173 */       if ((needValue) || (complyTo14)) {
/*  174 */         manageSyntheticAccessIfNecessary(currentScope, (FieldBinding)this.binding, 0, flowInfo);
/*      */       }
/*  176 */       if (this.indexOfFirstFieldBinding != 1) break;
/*  177 */       FieldBinding fieldBinding = (FieldBinding)this.binding;
/*      */ 
/*  179 */       if ((!fieldBinding.isBlankFinal()) || 
/*  180 */         (!currentScope.needBlankFinalFieldInitializationCheck(fieldBinding))) break;
/*  181 */       FlowInfo fieldInits = flowContext.getInitsForFinalBlankInitializationCheck(fieldBinding.declaringClass.original(), flowInfo);
/*  182 */       if (fieldInits.isDefinitelyAssigned(fieldBinding)) break;
/*  183 */       currentScope.problemReporter().uninitializedBlankFinalField(fieldBinding, this);
/*      */ 
/*  187 */       break;
/*      */     case 2:
/*      */       LocalVariableBinding localBinding;
/*  190 */       if (!flowInfo.isDefinitelyAssigned(localBinding = (LocalVariableBinding)this.binding)) {
/*  191 */         currentScope.problemReporter().uninitializedLocalVariable(localBinding, this);
/*      */       }
/*  193 */       if ((flowInfo.tagBits & 0x1) == 0)
/*  194 */         localBinding.useFlag = 1;
/*  195 */       else if (localBinding.useFlag == 0) {
/*  196 */         localBinding.useFlag = 2;
/*      */       }
/*  198 */       checkNPE(currentScope, flowContext, flowInfo, true);
/*      */     }
/*  200 */     if (needValue) {
/*  201 */       manageEnclosingInstanceAccessIfNecessary(currentScope, flowInfo);
/*      */     }
/*      */ 
/*  204 */     if (this.otherBindings != null) {
/*  205 */       for (int i = 0; i < otherBindingsCount; i++) {
/*  206 */         needValue = i < otherBindingsCount - 1 ? true : this.otherBindings[(i + 1)].isStatic() ? false : valueRequired;
/*  207 */         if ((needValue) || (complyTo14)) {
/*  208 */           manageSyntheticAccessIfNecessary(currentScope, this.otherBindings[i], i + 1, flowInfo);
/*      */         }
/*      */       }
/*      */     }
/*  212 */     return flowInfo;
/*      */   }
/*      */ 
/*      */   public void checkNPE(BlockScope scope, FlowContext flowContext, FlowInfo flowInfo, boolean checkString)
/*      */   {
/*  218 */     if ((this.bits & 0x7) == 2) {
/*  219 */       LocalVariableBinding local = (LocalVariableBinding)this.binding;
/*  220 */       if ((local != null) && 
/*  221 */         ((local.type.tagBits & 0x2) == 0L) && (
/*  222 */         (checkString) || (local.type.id != 11))) {
/*  223 */         if ((this.bits & 0x20000) == 0) {
/*  224 */           flowContext.recordUsingNullReference(scope, local, this, 
/*  225 */             3, flowInfo);
/*      */         }
/*  227 */         flowInfo.markAsComparedEqualToNonNull(local);
/*      */ 
/*  229 */         if (flowContext.initsOnFinally != null)
/*  230 */           flowContext.initsOnFinally.markAsComparedEqualToNonNull(local);
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   public void computeConversion(Scope scope, TypeBinding runtimeTimeType, TypeBinding compileTimeType)
/*      */   {
/*  240 */     if ((runtimeTimeType == null) || (compileTimeType == null)) {
/*  241 */       return;
/*      */     }
/*  243 */     FieldBinding field = null;
/*  244 */     int length = this.otherBindings == null ? 0 : this.otherBindings.length;
/*  245 */     if (length == 0) {
/*  246 */       if (((this.bits & 0x1) != 0) && (this.binding != null) && (this.binding.isValidBinding()))
/*  247 */         field = (FieldBinding)this.binding;
/*      */     }
/*      */     else {
/*  250 */       field = this.otherBindings[(length - 1)];
/*      */     }
/*  252 */     if (field != null) {
/*  253 */       FieldBinding originalBinding = field.original();
/*  254 */       TypeBinding originalType = originalBinding.type;
/*      */ 
/*  256 */       if (originalType.leafComponentType().isTypeVariable()) {
/*  257 */         TypeBinding targetType = (!compileTimeType.isBaseType()) && (runtimeTimeType.isBaseType()) ? 
/*  258 */           compileTimeType : 
/*  259 */           runtimeTimeType;
/*  260 */         TypeBinding typeCast = originalType.genericCast(targetType);
/*  261 */         setGenericCast(length, typeCast);
/*  262 */         if ((typeCast instanceof ReferenceBinding)) {
/*  263 */           ReferenceBinding referenceCast = (ReferenceBinding)typeCast;
/*  264 */           if (!referenceCast.canBeSeenBy(scope)) {
/*  265 */             scope.problemReporter().invalidType(this, 
/*  266 */               new ProblemReferenceBinding(
/*  267 */               CharOperation.splitOn('.', referenceCast.shortReadableName()), 
/*  268 */               referenceCast, 
/*  269 */               2));
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*  274 */     super.computeConversion(scope, runtimeTimeType, compileTimeType);
/*      */   }
/*      */ 
/*      */   public void generateAssignment(BlockScope currentScope, CodeStream codeStream, Assignment assignment, boolean valueRequired) {
/*  278 */     int pc = codeStream.position;
/*  279 */     FieldBinding lastFieldBinding = generateReadSequence(currentScope, codeStream);
/*  280 */     codeStream.recordPositionsFrom(pc, this.sourceStart);
/*  281 */     assignment.expression.generateCode(currentScope, codeStream, true);
/*  282 */     fieldStore(currentScope, codeStream, lastFieldBinding, this.syntheticWriteAccessor, getFinalReceiverType(), false, valueRequired);
/*      */ 
/*  284 */     if (valueRequired)
/*  285 */       codeStream.generateImplicitConversion(assignment.implicitConversion);
/*      */   }
/*      */ 
/*      */   public void generateCode(BlockScope currentScope, CodeStream codeStream, boolean valueRequired)
/*      */   {
/*  290 */     int pc = codeStream.position;
/*  291 */     if (this.constant != Constant.NotAConstant) {
/*  292 */       if (valueRequired)
/*  293 */         codeStream.generateConstant(this.constant, this.implicitConversion);
/*      */     }
/*      */     else {
/*  296 */       FieldBinding lastFieldBinding = generateReadSequence(currentScope, codeStream);
/*  297 */       if (lastFieldBinding != null) {
/*  298 */         boolean isStatic = lastFieldBinding.isStatic();
/*  299 */         Constant fieldConstant = lastFieldBinding.constant();
/*  300 */         if (fieldConstant != Constant.NotAConstant) {
/*  301 */           if (!isStatic) {
/*  302 */             codeStream.invokeObjectGetClass();
/*  303 */             codeStream.pop();
/*      */           }
/*  305 */           if (valueRequired)
/*  306 */             codeStream.generateConstant(fieldConstant, this.implicitConversion);
/*      */         }
/*      */         else {
/*  309 */           boolean isFirst = (lastFieldBinding == this.binding) && 
/*  310 */             ((this.indexOfFirstFieldBinding == 1) || (lastFieldBinding.declaringClass == currentScope.enclosingReceiverType())) && 
/*  311 */             (this.otherBindings == null);
/*  312 */           TypeBinding requiredGenericCast = getGenericCast(this.otherBindings == null ? 0 : this.otherBindings.length);
/*  313 */           if ((valueRequired) || 
/*  314 */             ((!isFirst) && (currentScope.compilerOptions().complianceLevel >= 3145728L)) || 
/*  315 */             ((this.implicitConversion & 0x400) != 0) || 
/*  316 */             (requiredGenericCast != null)) {
/*  317 */             int lastFieldPc = codeStream.position;
/*  318 */             if (lastFieldBinding.declaringClass == null) {
/*  319 */               codeStream.arraylength();
/*  320 */               if (valueRequired) {
/*  321 */                 codeStream.generateImplicitConversion(this.implicitConversion);
/*      */               }
/*      */               else
/*  324 */                 codeStream.pop();
/*      */             }
/*      */             else {
/*  327 */               SyntheticMethodBinding accessor = this.syntheticReadAccessors == null ? null : this.syntheticReadAccessors[(this.syntheticReadAccessors.length - 1)];
/*  328 */               if (accessor == null) {
/*  329 */                 TypeBinding constantPoolDeclaringClass = CodeStream.getConstantPoolDeclaringClass(currentScope, lastFieldBinding, getFinalReceiverType(), isFirst);
/*  330 */                 if (isStatic)
/*  331 */                   codeStream.fieldAccess(-78, lastFieldBinding, constantPoolDeclaringClass);
/*      */                 else
/*  333 */                   codeStream.fieldAccess(-76, lastFieldBinding, constantPoolDeclaringClass);
/*      */               }
/*      */               else {
/*  336 */                 codeStream.invoke(-72, accessor, null);
/*      */               }
/*  338 */               if (requiredGenericCast != null) codeStream.checkcast(requiredGenericCast);
/*  339 */               if (valueRequired) {
/*  340 */                 codeStream.generateImplicitConversion(this.implicitConversion);
/*      */               } else {
/*  342 */                 boolean isUnboxing = (this.implicitConversion & 0x400) != 0;
/*      */ 
/*  344 */                 if (isUnboxing) codeStream.generateImplicitConversion(this.implicitConversion);
/*  345 */                 switch (isUnboxing ? postConversionType(currentScope).id : lastFieldBinding.type.id) {
/*      */                 case 7:
/*      */                 case 8:
/*  348 */                   codeStream.pop2();
/*  349 */                   break;
/*      */                 default:
/*  351 */                   codeStream.pop();
/*      */                 }
/*      */               }
/*      */ 
/*      */             }
/*      */ 
/*  357 */             int fieldPosition = (int)(this.sourcePositions[(this.sourcePositions.length - 1)] >>> 32);
/*  358 */             codeStream.recordPositionsFrom(lastFieldPc, fieldPosition);
/*      */           }
/*  360 */           else if (!isStatic) {
/*  361 */             codeStream.invokeObjectGetClass();
/*  362 */             codeStream.pop();
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */ 
/*  368 */     codeStream.recordPositionsFrom(pc, this.sourceStart);
/*      */   }
/*      */ 
/*      */   public void generateCompoundAssignment(BlockScope currentScope, CodeStream codeStream, Expression expression, int operator, int assignmentImplicitConversion, boolean valueRequired) {
/*  372 */     FieldBinding lastFieldBinding = generateReadSequence(currentScope, codeStream);
/*  373 */     boolean isFirst = (lastFieldBinding == this.binding) && 
/*  374 */       ((this.indexOfFirstFieldBinding == 1) || (lastFieldBinding.declaringClass == currentScope.enclosingReceiverType())) && 
/*  375 */       (this.otherBindings == null);
/*  376 */     TypeBinding constantPoolDeclaringClass = CodeStream.getConstantPoolDeclaringClass(currentScope, lastFieldBinding, getFinalReceiverType(), isFirst);
/*  377 */     SyntheticMethodBinding accessor = this.syntheticReadAccessors == null ? null : this.syntheticReadAccessors[(this.syntheticReadAccessors.length - 1)];
/*  378 */     if (lastFieldBinding.isStatic()) {
/*  379 */       if (accessor == null)
/*  380 */         codeStream.fieldAccess(-78, lastFieldBinding, constantPoolDeclaringClass);
/*      */       else
/*  382 */         codeStream.invoke(-72, accessor, null);
/*      */     }
/*      */     else {
/*  385 */       codeStream.dup();
/*  386 */       if (accessor == null)
/*  387 */         codeStream.fieldAccess(-76, lastFieldBinding, constantPoolDeclaringClass);
/*      */       else
/*  389 */         codeStream.invoke(-72, accessor, null);
/*      */     }
/*      */     int operationTypeID;
/*  395 */     switch (operationTypeID = (this.implicitConversion & 0xFF) >> 4) {
/*      */     case 0:
/*      */     case 1:
/*      */     case 11:
/*  399 */       codeStream.generateStringConcatenationAppend(currentScope, null, expression);
/*  400 */       break;
/*      */     default:
/*  402 */       TypeBinding requiredGenericCast = getGenericCast(this.otherBindings == null ? 0 : this.otherBindings.length);
/*  403 */       if (requiredGenericCast != null) codeStream.checkcast(requiredGenericCast);
/*      */ 
/*  405 */       codeStream.generateImplicitConversion(this.implicitConversion);
/*      */ 
/*  407 */       if (expression == IntLiteral.One)
/*  408 */         codeStream.generateConstant(expression.constant, this.implicitConversion);
/*      */       else {
/*  410 */         expression.generateCode(currentScope, codeStream, true);
/*      */       }
/*      */ 
/*  413 */       codeStream.sendOperator(operator, operationTypeID);
/*      */ 
/*  415 */       codeStream.generateImplicitConversion(assignmentImplicitConversion);
/*      */     }
/*      */ 
/*  418 */     fieldStore(currentScope, codeStream, lastFieldBinding, this.syntheticWriteAccessor, getFinalReceiverType(), false, valueRequired);
/*      */   }
/*      */ 
/*      */   public void generatePostIncrement(BlockScope currentScope, CodeStream codeStream, CompoundAssignment postIncrement, boolean valueRequired)
/*      */   {
/*  423 */     FieldBinding lastFieldBinding = generateReadSequence(currentScope, codeStream);
/*  424 */     boolean isFirst = (lastFieldBinding == this.binding) && 
/*  425 */       ((this.indexOfFirstFieldBinding == 1) || (lastFieldBinding.declaringClass == currentScope.enclosingReceiverType())) && 
/*  426 */       (this.otherBindings == null);
/*  427 */     TypeBinding constantPoolDeclaringClass = CodeStream.getConstantPoolDeclaringClass(currentScope, lastFieldBinding, getFinalReceiverType(), isFirst);
/*  428 */     SyntheticMethodBinding accessor = this.syntheticReadAccessors == null ? 
/*  429 */       null : 
/*  430 */       this.syntheticReadAccessors[(this.syntheticReadAccessors.length - 1)];
/*  431 */     if (lastFieldBinding.isStatic()) {
/*  432 */       if (accessor == null)
/*  433 */         codeStream.fieldAccess(-78, lastFieldBinding, constantPoolDeclaringClass);
/*      */       else
/*  435 */         codeStream.invoke(-72, accessor, constantPoolDeclaringClass);
/*      */     }
/*      */     else {
/*  438 */       codeStream.dup();
/*  439 */       if (accessor == null)
/*  440 */         codeStream.fieldAccess(-76, lastFieldBinding, null);
/*      */       else {
/*  442 */         codeStream.invoke(-72, accessor, null);
/*      */       }
/*      */     }
/*  445 */     TypeBinding requiredGenericCast = getGenericCast(this.otherBindings == null ? 0 : this.otherBindings.length);
/*      */     TypeBinding operandType;
/*      */     TypeBinding operandType;
/*  447 */     if (requiredGenericCast != null) {
/*  448 */       codeStream.checkcast(requiredGenericCast);
/*  449 */       operandType = requiredGenericCast;
/*      */     } else {
/*  451 */       operandType = lastFieldBinding.type;
/*      */     }
/*      */ 
/*  454 */     if (valueRequired) {
/*  455 */       if (lastFieldBinding.isStatic())
/*  456 */         switch (operandType.id) {
/*      */         case 7:
/*      */         case 8:
/*  459 */           codeStream.dup2();
/*  460 */           break;
/*      */         default:
/*  462 */           codeStream.dup();
/*  463 */           break;
/*      */         }
/*      */       else {
/*  466 */         switch (operandType.id) {
/*      */         case 7:
/*      */         case 8:
/*  469 */           codeStream.dup2_x1();
/*  470 */           break;
/*      */         default:
/*  472 */           codeStream.dup_x1();
/*      */         }
/*      */       }
/*      */     }
/*      */ 
/*  477 */     codeStream.generateImplicitConversion(this.implicitConversion);
/*  478 */     codeStream.generateConstant(
/*  479 */       postIncrement.expression.constant, 
/*  480 */       this.implicitConversion);
/*  481 */     codeStream.sendOperator(postIncrement.operator, this.implicitConversion & 0xF);
/*  482 */     codeStream.generateImplicitConversion(
/*  483 */       postIncrement.preAssignImplicitConversion);
/*  484 */     fieldStore(currentScope, codeStream, lastFieldBinding, this.syntheticWriteAccessor, getFinalReceiverType(), false, false);
/*      */   }
/*      */ 
/*      */   public FieldBinding generateReadSequence(BlockScope currentScope, CodeStream codeStream)
/*      */   {
/*  493 */     int otherBindingsCount = this.otherBindings == null ? 0 : this.otherBindings.length;
/*  494 */     boolean needValue = (otherBindingsCount == 0) || (!this.otherBindings[0].isStatic());
/*      */ 
/*  498 */     boolean complyTo14 = currentScope.compilerOptions().complianceLevel >= 3145728L;
/*      */ 
/*  500 */     switch (this.bits & 0x7) {
/*      */     case 1:
/*  502 */       FieldBinding lastFieldBinding = ((FieldBinding)this.binding).original();
/*  503 */       TypeBinding lastGenericCast = this.genericCast;
/*  504 */       TypeBinding lastReceiverType = this.actualReceiverType;
/*      */ 
/*  506 */       if (lastFieldBinding.constant() != Constant.NotAConstant) {
/*      */         break;
/*      */       }
/*  509 */       if (((!needValue) || (lastFieldBinding.isStatic())) && (lastGenericCast == null)) break;
/*  510 */       int pc = codeStream.position;
/*  511 */       if ((this.bits & 0x1FE0) != 0) {
/*  512 */         ReferenceBinding targetType = currentScope.enclosingSourceType().enclosingTypeAt((this.bits & 0x1FE0) >> 5);
/*  513 */         Object[] emulationPath = currentScope.getEmulationPath(targetType, true, false);
/*  514 */         codeStream.generateOuterAccess(emulationPath, this, targetType, currentScope);
/*      */       } else {
/*  516 */         generateReceiver(codeStream);
/*      */       }
/*  518 */       codeStream.recordPositionsFrom(pc, this.sourceStart);
/*      */ 
/*  520 */       break;
/*      */     case 2:
/*  522 */       FieldBinding lastFieldBinding = null;
/*  523 */       TypeBinding lastGenericCast = null;
/*  524 */       LocalVariableBinding localBinding = (LocalVariableBinding)this.binding;
/*  525 */       TypeBinding lastReceiverType = localBinding.type;
/*  526 */       if (!needValue)
/*      */         break;
/*  528 */       Constant localConstant = localBinding.constant();
/*  529 */       if (localConstant != Constant.NotAConstant) {
/*  530 */         codeStream.generateConstant(localConstant, 0);
/*      */       }
/*  534 */       else if ((this.bits & 0x1FE0) != 0)
/*      */       {
/*  536 */         VariableBinding[] path = currentScope.getEmulationPath(localBinding);
/*  537 */         codeStream.generateOuterAccess(path, this, localBinding, currentScope);
/*      */       } else {
/*  539 */         codeStream.load(localBinding);
/*      */       }
/*      */ 
/*  542 */       break;
/*      */     default:
/*  544 */       return null;
/*      */     }
/*      */     TypeBinding lastReceiverType;
/*      */     TypeBinding lastGenericCast;
/*      */     FieldBinding lastFieldBinding;
/*  549 */     int positionsLength = this.sourcePositions.length;
/*  550 */     FieldBinding initialFieldBinding = lastFieldBinding;
/*  551 */     if (this.otherBindings != null) {
/*  552 */       for (int i = 0; i < otherBindingsCount; i++) {
/*  553 */         int pc = codeStream.position;
/*  554 */         FieldBinding nextField = this.otherBindings[i].original();
/*  555 */         TypeBinding nextGenericCast = this.otherGenericCasts == null ? null : this.otherGenericCasts[i];
/*  556 */         if (lastFieldBinding != null) {
/*  557 */           needValue = !nextField.isStatic();
/*  558 */           Constant fieldConstant = lastFieldBinding.constant();
/*  559 */           if (fieldConstant != Constant.NotAConstant) {
/*  560 */             if ((i > 0) && (!lastFieldBinding.isStatic())) {
/*  561 */               codeStream.invokeObjectGetClass();
/*  562 */               codeStream.pop();
/*      */             }
/*  564 */             if (needValue)
/*  565 */               codeStream.generateConstant(fieldConstant, 0);
/*      */           }
/*      */           else {
/*  568 */             if ((needValue) || ((i > 0) && (complyTo14)) || (lastGenericCast != null)) {
/*  569 */               MethodBinding accessor = this.syntheticReadAccessors == null ? null : this.syntheticReadAccessors[i];
/*  570 */               if (accessor == null) {
/*  571 */                 TypeBinding constantPoolDeclaringClass = CodeStream.getConstantPoolDeclaringClass(currentScope, lastFieldBinding, lastReceiverType, (i == 0) && (this.indexOfFirstFieldBinding == 1));
/*  572 */                 if (lastFieldBinding.isStatic())
/*  573 */                   codeStream.fieldAccess(-78, lastFieldBinding, constantPoolDeclaringClass);
/*      */                 else
/*  575 */                   codeStream.fieldAccess(-76, lastFieldBinding, constantPoolDeclaringClass);
/*      */               }
/*      */               else {
/*  578 */                 codeStream.invoke(-72, accessor, null);
/*      */               }
/*  580 */               if (lastGenericCast != null) {
/*  581 */                 codeStream.checkcast(lastGenericCast);
/*  582 */                 lastReceiverType = lastGenericCast;
/*      */               } else {
/*  584 */                 lastReceiverType = lastFieldBinding.type;
/*      */               }
/*  586 */               if (!needValue) codeStream.pop(); 
/*      */             }
/*      */             else {
/*  588 */               if (lastFieldBinding == initialFieldBinding) {
/*  589 */                 if (lastFieldBinding.isStatic())
/*      */                 {
/*  591 */                   if (initialFieldBinding.declaringClass != this.actualReceiverType.erasure()) {
/*  592 */                     MethodBinding accessor = this.syntheticReadAccessors == null ? null : this.syntheticReadAccessors[i];
/*  593 */                     if (accessor == null) {
/*  594 */                       TypeBinding constantPoolDeclaringClass = CodeStream.getConstantPoolDeclaringClass(currentScope, lastFieldBinding, lastReceiverType, (i == 0) && (this.indexOfFirstFieldBinding == 1));
/*  595 */                       codeStream.fieldAccess(-78, lastFieldBinding, constantPoolDeclaringClass);
/*      */                     } else {
/*  597 */                       codeStream.invoke(-72, accessor, null);
/*      */                     }
/*  599 */                     codeStream.pop();
/*      */                   }
/*      */                 }
/*  602 */               } else if (!lastFieldBinding.isStatic()) {
/*  603 */                 codeStream.invokeObjectGetClass();
/*  604 */                 codeStream.pop();
/*      */               }
/*  606 */               lastReceiverType = lastFieldBinding.type;
/*      */             }
/*  608 */             if (positionsLength - otherBindingsCount + i - 1 >= 0) {
/*  609 */               int fieldPosition = (int)(this.sourcePositions[(positionsLength - otherBindingsCount + i - 1)] >>> 32);
/*  610 */               codeStream.recordPositionsFrom(pc, fieldPosition);
/*      */             }
/*      */           }
/*      */         }
/*  614 */         lastFieldBinding = nextField;
/*  615 */         lastGenericCast = nextGenericCast;
/*      */       }
/*      */     }
/*  618 */     return lastFieldBinding;
/*      */   }
/*      */ 
/*      */   public void generateReceiver(CodeStream codeStream) {
/*  622 */     codeStream.aload_0();
/*      */   }
/*      */ 
/*      */   public TypeBinding[] genericTypeArguments()
/*      */   {
/*  629 */     return null;
/*      */   }
/*      */ 
/*      */   protected FieldBinding getCodegenBinding(int index) {
/*  633 */     if (index == 0) {
/*  634 */       return ((FieldBinding)this.binding).original();
/*      */     }
/*  636 */     return this.otherBindings[(index - 1)].original();
/*      */   }
/*      */ 
/*      */   protected TypeBinding getFinalReceiverType()
/*      */   {
/*  645 */     int otherBindingsCount = this.otherBindings == null ? 0 : this.otherBindings.length;
/*  646 */     switch (otherBindingsCount) {
/*      */     case 0:
/*  648 */       return this.actualReceiverType;
/*      */     case 1:
/*  650 */       return this.genericCast != null ? this.genericCast : ((VariableBinding)this.binding).type;
/*      */     }
/*  652 */     TypeBinding previousGenericCast = this.otherGenericCasts == null ? null : this.otherGenericCasts[(otherBindingsCount - 2)];
/*  653 */     return previousGenericCast != null ? previousGenericCast : this.otherBindings[(otherBindingsCount - 2)].type;
/*      */   }
/*      */ 
/*      */   protected TypeBinding getGenericCast(int index)
/*      */   {
/*  659 */     if (index == 0) {
/*  660 */       return this.genericCast;
/*      */     }
/*  662 */     if (this.otherGenericCasts == null) return null;
/*  663 */     return this.otherGenericCasts[(index - 1)];
/*      */   }
/*      */ 
/*      */   public TypeBinding getOtherFieldBindings(BlockScope scope)
/*      */   {
/*  668 */     int length = this.tokens.length;
/*  669 */     FieldBinding field = (this.bits & 0x1) != 0 ? (FieldBinding)this.binding : null;
/*  670 */     TypeBinding type = ((VariableBinding)this.binding).type;
/*  671 */     int index = this.indexOfFirstFieldBinding;
/*  672 */     if (index == length) {
/*  673 */       this.constant = ((FieldBinding)this.binding).constant();
/*      */ 
/*  675 */       return (type != null) && ((this.bits & 0x2000) == 0) ? 
/*  676 */         type.capture(scope, this.sourceEnd) : 
/*  677 */         type;
/*      */     }
/*      */ 
/*  680 */     int otherBindingsLength = length - index;
/*  681 */     this.otherBindings = new FieldBinding[otherBindingsLength];
/*  682 */     this.otherDepths = new int[otherBindingsLength];
/*      */ 
/*  685 */     this.constant = ((VariableBinding)this.binding).constant();
/*      */ 
/*  687 */     int firstDepth = (this.bits & 0x1FE0) >> 5;
/*      */ 
/*  689 */     while (index < length) {
/*  690 */       char[] token = this.tokens[index];
/*  691 */       if (type == null) {
/*  692 */         return null;
/*      */       }
/*  694 */       this.bits &= -8161;
/*  695 */       FieldBinding previousField = field;
/*  696 */       field = scope.getField(type.capture(scope, (int)this.sourcePositions[index]), token, this);
/*  697 */       int place = index - this.indexOfFirstFieldBinding;
/*  698 */       this.otherBindings[place] = field;
/*  699 */       this.otherDepths[place] = ((this.bits & 0x1FE0) >> 5);
/*  700 */       if (field.isValidBinding())
/*      */       {
/*  702 */         if (previousField != null) {
/*  703 */           TypeBinding fieldReceiverType = type;
/*  704 */           TypeBinding oldReceiverType = fieldReceiverType;
/*  705 */           fieldReceiverType = fieldReceiverType.getErasureCompatibleType(field.declaringClass);
/*  706 */           FieldBinding originalBinding = previousField.original();
/*  707 */           if ((fieldReceiverType != oldReceiverType) || (originalBinding.type.leafComponentType().isTypeVariable())) {
/*  708 */             setGenericCast(index - 1, originalBinding.type.genericCast(fieldReceiverType));
/*      */           }
/*      */         }
/*      */ 
/*  712 */         if (isFieldUseDeprecated(field, scope, ((this.bits & 0x2000) != 0) && (index + 1 == length))) {
/*  713 */           scope.problemReporter().deprecatedField(field, this);
/*      */         }
/*      */ 
/*  716 */         if (this.constant != Constant.NotAConstant) {
/*  717 */           this.constant = field.constant();
/*      */         }
/*      */ 
/*  720 */         if (field.isStatic()) {
/*  721 */           if ((field.modifiers & 0x4000) != 0) {
/*  722 */             ReferenceBinding declaringClass = field.original().declaringClass;
/*  723 */             MethodScope methodScope = scope.methodScope();
/*  724 */             SourceTypeBinding sourceType = methodScope.enclosingSourceType();
/*  725 */             if (((this.bits & 0x2000) == 0) && 
/*  726 */               (sourceType == declaringClass) && 
/*  727 */               (methodScope.lastVisibleFieldID >= 0) && 
/*  728 */               (field.id >= methodScope.lastVisibleFieldID) && (
/*  729 */               (!field.isStatic()) || (methodScope.isStatic))) {
/*  730 */               scope.problemReporter().forwardReference(this, index, field);
/*      */             }
/*      */ 
/*  733 */             if (((sourceType == declaringClass) || (sourceType.superclass == declaringClass)) && 
/*  734 */               (field.constant() == Constant.NotAConstant) && 
/*  735 */               (!methodScope.isStatic) && 
/*  736 */               (methodScope.isInsideInitializerOrConstructor())) {
/*  737 */               scope.problemReporter().enumStaticFieldUsedDuringInitialization(field, this);
/*      */             }
/*      */           }
/*      */ 
/*  741 */           scope.problemReporter().nonStaticAccessToStaticField(this, field, index);
/*      */ 
/*  743 */           if (field.declaringClass != type) {
/*  744 */             scope.problemReporter().indirectAccessToStaticField(this, field);
/*      */           }
/*      */         }
/*  747 */         type = field.type;
/*  748 */         index++;
/*      */       } else {
/*  750 */         this.constant = Constant.NotAConstant;
/*  751 */         scope.problemReporter().invalidField(this, field, index, type);
/*  752 */         setDepth(firstDepth);
/*  753 */         return null;
/*      */       }
/*      */     }
/*  756 */     setDepth(firstDepth);
/*  757 */     type = this.otherBindings[(otherBindingsLength - 1)].type;
/*      */ 
/*  759 */     return (type != null) && ((this.bits & 0x2000) == 0) ? 
/*  760 */       type.capture(scope, this.sourceEnd) : 
/*  761 */       type;
/*      */   }
/*      */ 
/*      */   public void manageEnclosingInstanceAccessIfNecessary(BlockScope currentScope, FlowInfo flowInfo) {
/*  765 */     if ((flowInfo.tagBits & 0x1) == 0)
/*      */     {
/*  767 */       if (((this.bits & 0x1FE0) == 0) || (this.constant != Constant.NotAConstant)) {
/*  768 */         return;
/*      */       }
/*  770 */       if ((this.bits & 0x7) == 2)
/*  771 */         currentScope.emulateOuterAccess((LocalVariableBinding)this.binding);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void manageSyntheticAccessIfNecessary(BlockScope currentScope, FieldBinding fieldBinding, int index, FlowInfo flowInfo)
/*      */   {
/*  780 */     if ((flowInfo.tagBits & 0x1) != 0) return;
/*      */ 
/*  782 */     if (fieldBinding.constant() != Constant.NotAConstant) {
/*  783 */       return;
/*      */     }
/*  785 */     if (fieldBinding.isPrivate()) {
/*  786 */       FieldBinding codegenField = getCodegenBinding(index < 0 ? this.otherBindings.length : this.otherBindings == null ? 0 : index);
/*  787 */       ReferenceBinding declaringClass = codegenField.declaringClass;
/*  788 */       if (declaringClass != currentScope.enclosingSourceType()) {
/*  789 */         setSyntheticAccessor(fieldBinding, index, ((SourceTypeBinding)declaringClass).addSyntheticMethod(codegenField, index >= 0, false));
/*  790 */         currentScope.problemReporter().needToEmulateFieldAccess(codegenField, this, index >= 0);
/*  791 */         return;
/*      */       }
/*  793 */     } else if (fieldBinding.isProtected()) {
/*  794 */       int depth = (index == 0) || ((index < 0) && (this.otherDepths == null)) ? 
/*  795 */         (this.bits & 0x1FE0) >> 5 : 
/*  796 */         this.otherDepths[(index - 1)];
/*      */ 
/*  799 */       if ((depth > 0) && (fieldBinding.declaringClass.getPackage() != currentScope.enclosingSourceType().getPackage())) {
/*  800 */         FieldBinding codegenField = getCodegenBinding(index < 0 ? this.otherBindings.length : this.otherBindings == null ? 0 : index);
/*  801 */         setSyntheticAccessor(fieldBinding, index, 
/*  802 */           ((SourceTypeBinding)currentScope.enclosingSourceType().enclosingTypeAt(depth)).addSyntheticMethod(codegenField, index >= 0, false));
/*  803 */         currentScope.problemReporter().needToEmulateFieldAccess(codegenField, this, index >= 0);
/*  804 */         return;
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   public int nullStatus(FlowInfo flowInfo) {
/*  810 */     return 0;
/*      */   }
/*      */ 
/*      */   public Constant optimizedBooleanConstant() {
/*  814 */     switch (this.resolvedType.id) {
/*      */     case 5:
/*      */     case 33:
/*  817 */       if (this.constant != Constant.NotAConstant) return this.constant;
/*  818 */       switch (this.bits & 0x7) {
/*      */       case 1:
/*  820 */         if (this.otherBindings != null) break;
/*  821 */         return ((FieldBinding)this.binding).constant();
/*      */       case 2:
/*  824 */         return this.otherBindings[(this.otherBindings.length - 1)].constant();
/*      */       }
/*      */     }
/*  827 */     return Constant.NotAConstant;
/*      */   }
/*      */ 
/*      */   public TypeBinding postConversionType(Scope scope)
/*      */   {
/*  834 */     TypeBinding convertedType = this.resolvedType;
/*  835 */     TypeBinding requiredGenericCast = getGenericCast(this.otherBindings == null ? 0 : this.otherBindings.length);
/*  836 */     if (requiredGenericCast != null)
/*  837 */       convertedType = requiredGenericCast;
/*  838 */     int runtimeType = (this.implicitConversion & 0xFF) >> 4;
/*  839 */     switch (runtimeType) {
/*      */     case 5:
/*  841 */       convertedType = TypeBinding.BOOLEAN;
/*  842 */       break;
/*      */     case 3:
/*  844 */       convertedType = TypeBinding.BYTE;
/*  845 */       break;
/*      */     case 4:
/*  847 */       convertedType = TypeBinding.SHORT;
/*  848 */       break;
/*      */     case 2:
/*  850 */       convertedType = TypeBinding.CHAR;
/*  851 */       break;
/*      */     case 10:
/*  853 */       convertedType = TypeBinding.INT;
/*  854 */       break;
/*      */     case 9:
/*  856 */       convertedType = TypeBinding.FLOAT;
/*  857 */       break;
/*      */     case 7:
/*  859 */       convertedType = TypeBinding.LONG;
/*  860 */       break;
/*      */     case 8:
/*  862 */       convertedType = TypeBinding.DOUBLE;
/*      */     case 6:
/*      */     }
/*      */ 
/*  866 */     if ((this.implicitConversion & 0x200) != 0) {
/*  867 */       convertedType = scope.environment().computeBoxingType(convertedType);
/*      */     }
/*  869 */     return convertedType;
/*      */   }
/*      */ 
/*      */   public StringBuffer printExpression(int indent, StringBuffer output) {
/*  873 */     for (int i = 0; i < this.tokens.length; i++) {
/*  874 */       if (i > 0) output.append('.');
/*  875 */       output.append(this.tokens[i]);
/*      */     }
/*  877 */     return output;
/*      */   }
/*      */ 
/*      */   public TypeBinding reportError(BlockScope scope)
/*      */   {
/*  884 */     if ((this.binding instanceof ProblemFieldBinding))
/*  885 */       scope.problemReporter().invalidField(this, (FieldBinding)this.binding);
/*  886 */     else if (((this.binding instanceof ProblemReferenceBinding)) || ((this.binding instanceof MissingTypeBinding)))
/*  887 */       scope.problemReporter().invalidType(this, (TypeBinding)this.binding);
/*      */     else {
/*  889 */       scope.problemReporter().unresolvableReference(this, this.binding);
/*      */     }
/*  891 */     return null;
/*      */   }
/*      */ 
/*      */   public TypeBinding resolveType(BlockScope scope)
/*      */   {
/*  898 */     this.actualReceiverType = scope.enclosingReceiverType();
/*  899 */     this.constant = Constant.NotAConstant;
/*  900 */     if ((this.binding = scope.getBinding(this.tokens, this.bits & 0x7, this, true)).isValidBinding())
/*  901 */       switch (this.bits & 0x7) {
/*      */       case 3:
/*      */       case 7:
/*  904 */         if ((this.binding instanceof LocalVariableBinding)) {
/*  905 */           this.bits &= -8;
/*  906 */           this.bits |= 2;
/*  907 */           LocalVariableBinding local = (LocalVariableBinding)this.binding;
/*  908 */           if ((!local.isFinal()) && ((this.bits & 0x1FE0) != 0)) {
/*  909 */             scope.problemReporter().cannotReferToNonFinalOuterLocal((LocalVariableBinding)this.binding, this);
/*      */           }
/*  911 */           if ((local.type != null) && ((local.type.tagBits & 0x80) != 0L))
/*      */           {
/*  913 */             return null;
/*      */           }
/*  915 */           this.resolvedType = getOtherFieldBindings(scope);
/*  916 */           if ((this.resolvedType != null) && ((this.resolvedType.tagBits & 0x80) != 0L)) {
/*  917 */             FieldBinding lastField = this.otherBindings[(this.otherBindings.length - 1)];
/*  918 */             scope.problemReporter().invalidField(this, new ProblemFieldBinding(lastField.declaringClass, lastField.name, 1), this.tokens.length, this.resolvedType.leafComponentType());
/*  919 */             return null;
/*      */           }
/*  921 */           return this.resolvedType;
/*      */         }
/*  923 */         if ((this.binding instanceof FieldBinding)) {
/*  924 */           this.bits &= -8;
/*  925 */           this.bits |= 1;
/*  926 */           FieldBinding fieldBinding = (FieldBinding)this.binding;
/*  927 */           MethodScope methodScope = scope.methodScope();
/*  928 */           ReferenceBinding declaringClass = fieldBinding.original().declaringClass;
/*  929 */           SourceTypeBinding sourceType = methodScope.enclosingSourceType();
/*      */ 
/*  931 */           if (((this.indexOfFirstFieldBinding == 1) || ((fieldBinding.modifiers & 0x4000) != 0)) && 
/*  932 */             (sourceType == declaringClass) && 
/*  933 */             (methodScope.lastVisibleFieldID >= 0) && 
/*  934 */             (fieldBinding.id >= methodScope.lastVisibleFieldID) && (
/*  935 */             (!fieldBinding.isStatic()) || (methodScope.isStatic))) {
/*  936 */             scope.problemReporter().forwardReference(this, this.indexOfFirstFieldBinding - 1, fieldBinding);
/*      */           }
/*  938 */           if (isFieldUseDeprecated(fieldBinding, scope, ((this.bits & 0x2000) != 0) && (this.indexOfFirstFieldBinding == this.tokens.length))) {
/*  939 */             scope.problemReporter().deprecatedField(fieldBinding, this);
/*      */           }
/*  941 */           if (fieldBinding.isStatic())
/*      */           {
/*  944 */             if ((declaringClass.isEnum()) && 
/*  945 */               ((sourceType == declaringClass) || (sourceType.superclass == declaringClass)) && 
/*  946 */               (fieldBinding.constant() == Constant.NotAConstant) && 
/*  947 */               (!methodScope.isStatic) && 
/*  948 */               (methodScope.isInsideInitializerOrConstructor())) {
/*  949 */               scope.problemReporter().enumStaticFieldUsedDuringInitialization(fieldBinding, this);
/*      */             }
/*      */ 
/*  952 */             if ((this.indexOfFirstFieldBinding > 1) && 
/*  953 */               (fieldBinding.declaringClass != this.actualReceiverType) && 
/*  954 */               (fieldBinding.declaringClass.canBeSeenBy(scope)))
/*  955 */               scope.problemReporter().indirectAccessToStaticField(this, fieldBinding);
/*      */           }
/*      */           else {
/*  958 */             if ((this.indexOfFirstFieldBinding == 1) && (scope.compilerOptions().getSeverity(4194304) != -1)) {
/*  959 */               scope.problemReporter().unqualifiedFieldAccess(this, fieldBinding);
/*      */             }
/*      */ 
/*  962 */             if ((this.indexOfFirstFieldBinding > 1) || 
/*  963 */               (scope.methodScope().isStatic)) {
/*  964 */               scope.problemReporter().staticFieldAccessToNonStaticVariable(this, fieldBinding);
/*  965 */               return null;
/*      */             }
/*      */           }
/*      */ 
/*  969 */           this.resolvedType = getOtherFieldBindings(scope);
/*  970 */           if ((this.resolvedType != null) && 
/*  971 */             ((this.resolvedType.tagBits & 0x80) != 0L)) {
/*  972 */             FieldBinding lastField = this.indexOfFirstFieldBinding == this.tokens.length ? (FieldBinding)this.binding : this.otherBindings[(this.otherBindings.length - 1)];
/*  973 */             scope.problemReporter().invalidField(this, new ProblemFieldBinding(lastField.declaringClass, lastField.name, 1), this.tokens.length, this.resolvedType.leafComponentType());
/*  974 */             return null;
/*      */           }
/*  976 */           return this.resolvedType;
/*      */         }
/*      */ 
/*  979 */         this.bits &= -8;
/*  980 */         this.bits |= 4;
/*      */       case 4:
/*  983 */         TypeBinding type = (TypeBinding)this.binding;
/*      */ 
/*  986 */         type = scope.environment().convertToRawType(type, false);
/*  987 */         return this.resolvedType = type;
/*      */       case 5:
/*      */       case 6:
/*      */       }
/*  991 */     return this.resolvedType = reportError(scope);
/*      */   }
/*      */ 
/*      */   public void setFieldIndex(int index) {
/*  995 */     this.indexOfFirstFieldBinding = index;
/*      */   }
/*      */ 
/*      */   protected void setGenericCast(int index, TypeBinding someGenericCast)
/*      */   {
/* 1000 */     if (someGenericCast == null) return;
/* 1001 */     if (index == 0) {
/* 1002 */       this.genericCast = someGenericCast;
/*      */     } else {
/* 1004 */       if (this.otherGenericCasts == null) {
/* 1005 */         this.otherGenericCasts = new TypeBinding[this.otherBindings.length];
/*      */       }
/* 1007 */       this.otherGenericCasts[(index - 1)] = someGenericCast;
/*      */     }
/*      */   }
/*      */ 
/*      */   protected void setSyntheticAccessor(FieldBinding fieldBinding, int index, SyntheticMethodBinding syntheticAccessor)
/*      */   {
/* 1013 */     if (index < 0) {
/* 1014 */       this.syntheticWriteAccessor = syntheticAccessor;
/*      */     } else {
/* 1016 */       if (this.syntheticReadAccessors == null) {
/* 1017 */         this.syntheticReadAccessors = new SyntheticMethodBinding[this.otherBindings == null ? 1 : this.otherBindings.length + 1];
/*      */       }
/* 1019 */       this.syntheticReadAccessors[index] = syntheticAccessor;
/*      */     }
/*      */   }
/*      */ 
/*      */   public void traverse(ASTVisitor visitor, BlockScope scope) {
/* 1024 */     visitor.visit(this, scope);
/* 1025 */     visitor.endVisit(this, scope);
/*      */   }
/*      */ 
/*      */   public void traverse(ASTVisitor visitor, ClassScope scope) {
/* 1029 */     visitor.visit(this, scope);
/* 1030 */     visitor.endVisit(this, scope);
/*      */   }
/*      */ 
/*      */   public String unboundReferenceErrorName() {
/* 1034 */     return new String(this.tokens[0]);
/*      */   }
/*      */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\ecj-3.5.2.jar
 * Qualified Name:     org.eclipse.jdt.internal.compiler.ast.QualifiedNameReference
 * JD-Core Version:    0.6.0
 */