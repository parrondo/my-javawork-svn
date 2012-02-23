/*     */ package org.eclipse.jdt.internal.compiler.ast;
/*     */ 
/*     */ import org.eclipse.jdt.internal.compiler.ASTVisitor;
/*     */ import org.eclipse.jdt.internal.compiler.CompilationResult;
/*     */ import org.eclipse.jdt.internal.compiler.codegen.CodeStream;
/*     */ import org.eclipse.jdt.internal.compiler.flow.FlowContext;
/*     */ import org.eclipse.jdt.internal.compiler.flow.FlowInfo;
/*     */ import org.eclipse.jdt.internal.compiler.impl.CompilerOptions;
/*     */ import org.eclipse.jdt.internal.compiler.impl.Constant;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.ArrayBinding;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.Binding;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.BlockScope;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.ClassScope;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.CompilationUnitScope;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.FieldBinding;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.MethodScope;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.ReferenceBinding;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.Scope;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.SourceTypeBinding;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;
/*     */ import org.eclipse.jdt.internal.compiler.problem.ProblemReporter;
/*     */ import org.eclipse.jdt.internal.compiler.util.Util;
/*     */ 
/*     */ public class FieldDeclaration extends AbstractVariableDeclaration
/*     */ {
/*     */   public FieldBinding binding;
/*     */   public Javadoc javadoc;
/*     */   public int endPart1Position;
/*     */   public int endPart2Position;
/*     */ 
/*     */   public FieldDeclaration()
/*     */   {
/*     */   }
/*     */ 
/*     */   public FieldDeclaration(char[] name, int sourceStart, int sourceEnd)
/*     */   {
/*  45 */     this.name = name;
/*     */ 
/*  49 */     this.sourceStart = sourceStart;
/*  50 */     this.sourceEnd = sourceEnd;
/*     */   }
/*     */ 
/*     */   public FlowInfo analyseCode(MethodScope initializationScope, FlowContext flowContext, FlowInfo flowInfo) {
/*  54 */     if ((this.binding != null) && (!this.binding.isUsed()) && (this.binding.isOrEnclosedByPrivateType()) && 
/*  55 */       (!initializationScope.referenceCompilationUnit().compilationResult.hasSyntaxError)) {
/*  56 */       initializationScope.problemReporter().unusedPrivateField(this);
/*     */     }
/*     */ 
/*  60 */     if ((this.binding != null) && 
/*  61 */       (this.binding.isValidBinding()) && 
/*  62 */       (this.binding.isStatic()) && 
/*  63 */       (this.binding.constant() == Constant.NotAConstant) && 
/*  64 */       (this.binding.declaringClass.isNestedType()) && 
/*  65 */       (!this.binding.declaringClass.isStatic())) {
/*  66 */       initializationScope.problemReporter().unexpectedStaticModifierForField(
/*  67 */         (SourceTypeBinding)this.binding.declaringClass, 
/*  68 */         this);
/*     */     }
/*     */ 
/*  71 */     if (this.initialization != null) {
/*  72 */       flowInfo = 
/*  73 */         this.initialization
/*  74 */         .analyseCode(initializationScope, flowContext, flowInfo)
/*  75 */         .unconditionalInits();
/*  76 */       flowInfo.markAsDefinitelyAssigned(this.binding);
/*     */     }
/*  78 */     return flowInfo;
/*     */   }
/*     */ 
/*     */   public void generateCode(BlockScope currentScope, CodeStream codeStream)
/*     */   {
/*  89 */     if ((this.bits & 0x80000000) == 0) {
/*  90 */       return;
/*     */     }
/*     */ 
/*  94 */     int pc = codeStream.position;
/*     */     boolean isStatic;
/*  96 */     if ((this.initialization != null) && (
/*  97 */       (!(isStatic = this.binding.isStatic())) || (this.binding.constant() == Constant.NotAConstant)))
/*     */     {
/*  99 */       if (!isStatic) {
/* 100 */         codeStream.aload_0();
/*     */       }
/* 102 */       this.initialization.generateCode(currentScope, codeStream, true);
/*     */ 
/* 104 */       if (isStatic)
/* 105 */         codeStream.fieldAccess(-77, this.binding, null);
/*     */       else {
/* 107 */         codeStream.fieldAccess(-75, this.binding, null);
/*     */       }
/*     */     }
/* 110 */     codeStream.recordPositionsFrom(pc, this.sourceStart);
/*     */   }
/*     */ 
/*     */   public int getKind()
/*     */   {
/* 117 */     return this.type == null ? 3 : 1;
/*     */   }
/*     */ 
/*     */   public boolean isStatic() {
/* 121 */     if (this.binding != null)
/* 122 */       return this.binding.isStatic();
/* 123 */     return (this.modifiers & 0x8) != 0;
/*     */   }
/*     */ 
/*     */   public StringBuffer printStatement(int indent, StringBuffer output) {
/* 127 */     if (this.javadoc != null) {
/* 128 */       this.javadoc.print(indent, output);
/*     */     }
/* 130 */     return super.printStatement(indent, output);
/*     */   }
/*     */ 
/*     */   public void resolve(MethodScope initializationScope)
/*     */   {
/* 139 */     if ((this.bits & 0x10) != 0) return;
/* 140 */     if ((this.binding == null) || (!this.binding.isValidBinding())) return;
/*     */ 
/* 142 */     this.bits |= 16;
/*     */ 
/* 146 */     ClassScope classScope = initializationScope.enclosingClassScope();
/*     */ 
/* 148 */     if (classScope != null)
/*     */     {
/* 150 */       SourceTypeBinding declaringType = classScope.enclosingSourceType();
/*     */ 
/* 152 */       if (declaringType.superclass != null) {
/* 153 */         Binding existingVariable = classScope.findField(declaringType.superclass, this.name, this, false);
/* 154 */         if ((existingVariable != null) && 
/* 155 */           (existingVariable.isValidBinding())) {
/* 156 */           if ((existingVariable instanceof FieldBinding)) { FieldBinding existingField = (FieldBinding)existingVariable;
/* 158 */             if ((existingField.original() == this.binding) || 
/* 159 */               (!existingField.canBeSeenBy(declaringType, this, initializationScope)));
/*     */           } else {
/* 162 */             initializationScope.problemReporter().fieldHiding(this, existingVariable);
/* 163 */             break label277;
/*     */           }
/*     */         }
/*     */       }
/* 167 */       Scope outerScope = classScope.parent;
/* 168 */       if (outerScope.kind != 4) {
/* 169 */         Binding existingVariable = outerScope.getBinding(this.name, 3, this, false);
/* 170 */         if ((existingVariable != null) && 
/* 171 */           (existingVariable.isValidBinding()) && 
/* 172 */           (existingVariable != this.binding))
/* 173 */           if ((existingVariable instanceof FieldBinding)) { FieldBinding existingField = (FieldBinding)existingVariable;
/* 175 */             if ((existingField.original() == this.binding) || (
/* 176 */               (!existingField.isStatic()) && (declaringType.isStatic())));
/*     */           } else {
/* 179 */             initializationScope.problemReporter().fieldHiding(this, existingVariable);
/*     */           }
/*     */       }
/*     */     }
/* 183 */     label277: if (this.type != null) {
/* 184 */       this.type.resolvedType = this.binding.type;
/*     */     }
/*     */ 
/* 187 */     FieldBinding previousField = initializationScope.initializedField;
/* 188 */     int previousFieldID = initializationScope.lastVisibleFieldID;
/*     */ 
/* 190 */     label984: 
/*     */     try { initializationScope.initializedField = this.binding;
/* 191 */       initializationScope.lastVisibleFieldID = this.binding.id;
/*     */ 
/* 193 */       resolveAnnotations(initializationScope, this.annotations, this.binding);
/*     */ 
/* 195 */       if (((this.binding.getAnnotationTagBits() & 0x0) == 0L) && 
/* 196 */         ((this.binding.modifiers & 0x100000) != 0) && 
/* 197 */         (initializationScope.compilerOptions().sourceLevel >= 3211264L)) {
/* 198 */         initializationScope.problemReporter().missingDeprecatedAnnotationForField(this);
/*     */       }
/*     */ 
/* 201 */       if (this.initialization == null) {
/* 202 */         this.binding.setConstant(Constant.NotAConstant);
/*     */       }
/*     */       else {
/* 205 */         this.binding.setConstant(Constant.NotAConstant);
/*     */ 
/* 207 */         TypeBinding fieldType = this.binding.type;
/*     */ 
/* 209 */         this.initialization.setExpectedType(fieldType);
/* 210 */         if ((this.initialization instanceof ArrayInitializer))
/*     */         {
/*     */           TypeBinding initializationType;
/* 212 */           if ((initializationType = this.initialization.resolveTypeExpecting(initializationScope, fieldType)) != null) {
/* 213 */             ((ArrayInitializer)this.initialization).binding = ((ArrayBinding)initializationType);
/* 214 */             this.initialization.computeConversion(initializationScope, fieldType, initializationType);
/*     */           }
/*     */         }
/*     */         else
/*     */         {
/*     */           TypeBinding initializationType;
/* 216 */           if ((initializationType = this.initialization.resolveType(initializationScope)) != null)
/*     */           {
/* 218 */             if (fieldType != initializationType)
/* 219 */               initializationScope.compilationUnitScope().recordTypeConversion(fieldType, initializationType);
/* 220 */             if ((this.initialization.isConstantValueOfTypeAssignableToType(initializationType, fieldType)) || 
/* 221 */               (initializationType.isCompatibleWith(fieldType))) {
/* 222 */               this.initialization.computeConversion(initializationScope, fieldType, initializationType);
/* 223 */               if (initializationType.needsUncheckedConversion(fieldType)) {
/* 224 */                 initializationScope.problemReporter().unsafeTypeConversion(this.initialization, initializationType, fieldType);
/*     */               }
/* 226 */               if (((this.initialization instanceof CastExpression)) && 
/* 227 */                 ((this.initialization.bits & 0x4000) == 0))
/* 228 */                 CastExpression.checkNeedForAssignedCast(initializationScope, fieldType, (CastExpression)this.initialization);
/*     */             }
/* 230 */             else if (isBoxingCompatible(initializationType, fieldType, this.initialization, initializationScope)) {
/* 231 */               this.initialization.computeConversion(initializationScope, fieldType, initializationType);
/* 232 */               if (((this.initialization instanceof CastExpression)) && 
/* 233 */                 ((this.initialization.bits & 0x4000) == 0)) {
/* 234 */                 CastExpression.checkNeedForAssignedCast(initializationScope, fieldType, (CastExpression)this.initialization);
/*     */               }
/*     */             }
/* 237 */             else if ((fieldType.tagBits & 0x80) == 0L)
/*     */             {
/* 239 */               initializationScope.problemReporter().typeMismatchError(initializationType, fieldType, this.initialization, null);
/*     */             }
/*     */ 
/* 242 */             if (this.binding.isFinal())
/* 243 */               this.binding.setConstant(this.initialization.constant.castTo((this.binding.type.id << 4) + this.initialization.constant.typeID()));
/*     */           }
/*     */           else {
/* 246 */             this.binding.setConstant(Constant.NotAConstant);
/*     */           }
/*     */         }
/* 249 */         if (this.binding == Expression.getDirectBinding(this.initialization)) {
/* 250 */           initializationScope.problemReporter().assignmentHasNoEffect(this, this.name);
/*     */         }
/*     */       }
/*     */ 
/* 254 */       if (this.javadoc != null) {
/* 255 */         this.javadoc.resolve(initializationScope);
/* 256 */       } else if ((this.binding != null) && (this.binding.declaringClass != null) && (!this.binding.declaringClass.isLocalType()))
/*     */       {
/* 258 */         int javadocVisibility = this.binding.modifiers & 0x7;
/* 259 */         ProblemReporter reporter = initializationScope.problemReporter();
/* 260 */         int severity = reporter.computeSeverity(-1610612250);
/* 261 */         if (severity == -1) break label984; if (classScope != null) {
/* 263 */           javadocVisibility = Util.computeOuterMostVisibility(classScope.referenceType(), javadocVisibility);
/*     */         }
/* 265 */         int javadocModifiers = this.binding.modifiers & 0xFFFFFFF8 | javadocVisibility;
/* 266 */         reporter.javadocMissing(this.sourceStart, this.sourceEnd, severity, javadocModifiers);
/*     */       }
/*     */     } finally
/*     */     {
/* 270 */       initializationScope.initializedField = previousField;
/* 271 */       initializationScope.lastVisibleFieldID = previousFieldID;
/* 272 */       if (this.binding.constant() == null)
/* 273 */         this.binding.setConstant(Constant.NotAConstant);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void traverse(ASTVisitor visitor, MethodScope scope) {
/* 278 */     if (visitor.visit(this, scope)) {
/* 279 */       if (this.javadoc != null) {
/* 280 */         this.javadoc.traverse(visitor, scope);
/*     */       }
/* 282 */       if (this.annotations != null) {
/* 283 */         int annotationsLength = this.annotations.length;
/* 284 */         for (int i = 0; i < annotationsLength; i++)
/* 285 */           this.annotations[i].traverse(visitor, scope);
/*     */       }
/* 287 */       if (this.type != null) {
/* 288 */         this.type.traverse(visitor, scope);
/*     */       }
/* 290 */       if (this.initialization != null)
/* 291 */         this.initialization.traverse(visitor, scope);
/*     */     }
/* 293 */     visitor.endVisit(this, scope);
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\ecj-3.5.2.jar
 * Qualified Name:     org.eclipse.jdt.internal.compiler.ast.FieldDeclaration
 * JD-Core Version:    0.6.0
 */