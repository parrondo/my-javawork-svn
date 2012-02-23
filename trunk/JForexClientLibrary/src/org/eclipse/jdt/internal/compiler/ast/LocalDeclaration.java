/*     */ package org.eclipse.jdt.internal.compiler.ast;
/*     */ 
/*     */ import org.eclipse.jdt.internal.compiler.ASTVisitor;
/*     */ import org.eclipse.jdt.internal.compiler.codegen.CodeStream;
/*     */ import org.eclipse.jdt.internal.compiler.flow.FlowContext;
/*     */ import org.eclipse.jdt.internal.compiler.flow.FlowInfo;
/*     */ import org.eclipse.jdt.internal.compiler.impl.Constant;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.ArrayBinding;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.Binding;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.BlockScope;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.CompilationUnitScope;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.LocalVariableBinding;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;
/*     */ import org.eclipse.jdt.internal.compiler.problem.ProblemReporter;
/*     */ 
/*     */ public class LocalDeclaration extends AbstractVariableDeclaration
/*     */ {
/*     */   public LocalVariableBinding binding;
/*     */ 
/*     */   public LocalDeclaration(char[] name, int sourceStart, int sourceEnd)
/*     */   {
/*  29 */     this.name = name;
/*  30 */     this.sourceStart = sourceStart;
/*  31 */     this.sourceEnd = sourceEnd;
/*  32 */     this.declarationEnd = sourceEnd;
/*     */   }
/*     */ 
/*     */   public FlowInfo analyseCode(BlockScope currentScope, FlowContext flowContext, FlowInfo flowInfo)
/*     */   {
/*  37 */     if ((flowInfo.tagBits & 0x1) == 0) {
/*  38 */       this.bits |= 1073741824;
/*     */     }
/*  40 */     if (this.initialization == null) {
/*  41 */       return flowInfo;
/*     */     }
/*  43 */     int nullStatus = this.initialization.nullStatus(flowInfo);
/*  44 */     flowInfo = 
/*  45 */       this.initialization
/*  46 */       .analyseCode(currentScope, flowContext, flowInfo)
/*  47 */       .unconditionalInits();
/*  48 */     if (!flowInfo.isDefinitelyAssigned(this.binding))
/*  49 */       this.bits |= 8;
/*     */     else {
/*  51 */       this.bits &= -9;
/*     */     }
/*  53 */     flowInfo.markAsDefinitelyAssigned(this.binding);
/*  54 */     if ((this.binding.type.tagBits & 0x2) == 0L) {
/*  55 */       switch (nullStatus) {
/*     */       case 1:
/*  57 */         flowInfo.markAsDefinitelyNull(this.binding);
/*  58 */         break;
/*     */       case -1:
/*  60 */         flowInfo.markAsDefinitelyNonNull(this.binding);
/*  61 */         break;
/*     */       case 0:
/*     */       default:
/*  63 */         flowInfo.markAsDefinitelyUnknown(this.binding);
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/*  68 */     return flowInfo;
/*     */   }
/*     */ 
/*     */   public void checkModifiers()
/*     */   {
/*  74 */     if ((this.modifiers & 0xFFFF & 0xFFFFFFEF) != 0)
/*     */     {
/*  79 */       this.modifiers = (this.modifiers & 0xFFBFFFFF | 0x800000);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void generateCode(BlockScope currentScope, CodeStream codeStream)
/*     */   {
/*  89 */     if (this.binding.resolvedPosition != -1) {
/*  90 */       codeStream.addVisibleLocalVariable(this.binding);
/*     */     }
/*  92 */     if ((this.bits & 0x80000000) == 0) {
/*  93 */       return;
/*     */     }
/*  95 */     int pc = codeStream.position;
/*     */ 
/*  99 */     if (this.initialization != null)
/*     */     {
/* 102 */       if (this.binding.resolvedPosition < 0) {
/* 103 */         if (this.initialization.constant == Constant.NotAConstant)
/*     */         {
/* 106 */           this.initialization.generateCode(currentScope, codeStream, false);
/*     */         }
/*     */       } else {
/* 109 */         this.initialization.generateCode(currentScope, codeStream, true);
/*     */ 
/* 111 */         if ((this.binding.type.isArrayType()) && (
/* 112 */           (this.initialization.resolvedType == TypeBinding.NULL) || (
/* 113 */           ((this.initialization instanceof CastExpression)) && 
/* 114 */           (((CastExpression)this.initialization).innermostCastedExpression().resolvedType == TypeBinding.NULL)))) {
/* 115 */           codeStream.checkcast(this.binding.type);
/*     */         }
/* 117 */         codeStream.store(this.binding, false);
/* 118 */         if ((this.bits & 0x8) != 0)
/*     */         {
/* 122 */           this.binding.recordInitializationStartPC(codeStream.position);
/*     */         }
/*     */       }
/*     */     }
/* 125 */     codeStream.recordPositionsFrom(pc, this.sourceStart);
/*     */   }
/*     */ 
/*     */   public int getKind()
/*     */   {
/* 132 */     return 4;
/*     */   }
/*     */ 
/*     */   public void resolve(BlockScope scope)
/*     */   {
/* 138 */     TypeBinding variableType = this.type.resolveType(scope, true);
/*     */ 
/* 140 */     checkModifiers();
/* 141 */     if (variableType != null) {
/* 142 */       if (variableType == TypeBinding.VOID) {
/* 143 */         scope.problemReporter().variableTypeCannotBeVoid(this);
/* 144 */         return;
/*     */       }
/* 146 */       if ((variableType.isArrayType()) && (((ArrayBinding)variableType).leafComponentType == TypeBinding.VOID)) {
/* 147 */         scope.problemReporter().variableTypeCannotBeVoidArray(this);
/* 148 */         return;
/*     */       }
/*     */     }
/*     */ 
/* 152 */     Binding existingVariable = scope.getBinding(this.name, 3, this, false);
/* 153 */     if ((existingVariable != null) && (existingVariable.isValidBinding())) {
/* 154 */       if (((existingVariable instanceof LocalVariableBinding)) && (this.hiddenVariableDepth == 0))
/* 155 */         scope.problemReporter().redefineLocal(this);
/*     */       else {
/* 157 */         scope.problemReporter().localVariableHiding(this, existingVariable, false);
/*     */       }
/*     */     }
/*     */ 
/* 161 */     if (((this.modifiers & 0x10) != 0) && (this.initialization == null)) {
/* 162 */       this.modifiers |= 67108864;
/*     */     }
/* 164 */     this.binding = new LocalVariableBinding(this, variableType, this.modifiers, false);
/* 165 */     scope.addLocalVariable(this.binding);
/* 166 */     this.binding.setConstant(Constant.NotAConstant);
/*     */ 
/* 170 */     if (variableType == null) {
/* 171 */       if (this.initialization != null)
/* 172 */         this.initialization.resolveType(scope);
/* 173 */       return;
/*     */     }
/*     */ 
/* 177 */     if (this.initialization != null) {
/* 178 */       if ((this.initialization instanceof ArrayInitializer)) {
/* 179 */         TypeBinding initializationType = this.initialization.resolveTypeExpecting(scope, variableType);
/* 180 */         if (initializationType != null) {
/* 181 */           ((ArrayInitializer)this.initialization).binding = ((ArrayBinding)initializationType);
/* 182 */           this.initialization.computeConversion(scope, variableType, initializationType);
/*     */         }
/*     */       } else {
/* 185 */         this.initialization.setExpectedType(variableType);
/* 186 */         TypeBinding initializationType = this.initialization.resolveType(scope);
/* 187 */         if (initializationType != null) {
/* 188 */           if (variableType != initializationType)
/* 189 */             scope.compilationUnitScope().recordTypeConversion(variableType, initializationType);
/* 190 */           if ((this.initialization.isConstantValueOfTypeAssignableToType(initializationType, variableType)) || 
/* 191 */             (initializationType.isCompatibleWith(variableType))) {
/* 192 */             this.initialization.computeConversion(scope, variableType, initializationType);
/* 193 */             if (initializationType.needsUncheckedConversion(variableType)) {
/* 194 */               scope.problemReporter().unsafeTypeConversion(this.initialization, initializationType, variableType);
/*     */             }
/* 196 */             if (((this.initialization instanceof CastExpression)) && 
/* 197 */               ((this.initialization.bits & 0x4000) == 0))
/* 198 */               CastExpression.checkNeedForAssignedCast(scope, variableType, (CastExpression)this.initialization);
/*     */           }
/* 200 */           else if (isBoxingCompatible(initializationType, variableType, this.initialization, scope)) {
/* 201 */             this.initialization.computeConversion(scope, variableType, initializationType);
/* 202 */             if (((this.initialization instanceof CastExpression)) && 
/* 203 */               ((this.initialization.bits & 0x4000) == 0)) {
/* 204 */               CastExpression.checkNeedForAssignedCast(scope, variableType, (CastExpression)this.initialization);
/*     */             }
/*     */           }
/* 207 */           else if ((variableType.tagBits & 0x80) == 0L)
/*     */           {
/* 209 */             scope.problemReporter().typeMismatchError(initializationType, variableType, this.initialization, null);
/*     */           }
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/* 215 */       if (this.binding == Expression.getDirectBinding(this.initialization)) {
/* 216 */         scope.problemReporter().assignmentHasNoEffect(this, this.name);
/*     */       }
/*     */ 
/* 221 */       this.binding.setConstant(
/* 222 */         this.binding.isFinal() ? 
/* 223 */         this.initialization.constant.castTo((variableType.id << 4) + this.initialization.constant.typeID()) : 
/* 224 */         Constant.NotAConstant);
/*     */     }
/*     */ 
/* 227 */     resolveAnnotations(scope, this.annotations, this.binding);
/*     */   }
/*     */ 
/*     */   public void traverse(ASTVisitor visitor, BlockScope scope)
/*     */   {
/* 232 */     if (visitor.visit(this, scope)) {
/* 233 */       if (this.annotations != null) {
/* 234 */         int annotationsLength = this.annotations.length;
/* 235 */         for (int i = 0; i < annotationsLength; i++)
/* 236 */           this.annotations[i].traverse(visitor, scope);
/*     */       }
/* 238 */       this.type.traverse(visitor, scope);
/* 239 */       if (this.initialization != null)
/* 240 */         this.initialization.traverse(visitor, scope);
/*     */     }
/* 242 */     visitor.endVisit(this, scope);
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\ecj-3.5.2.jar
 * Qualified Name:     org.eclipse.jdt.internal.compiler.ast.LocalDeclaration
 * JD-Core Version:    0.6.0
 */