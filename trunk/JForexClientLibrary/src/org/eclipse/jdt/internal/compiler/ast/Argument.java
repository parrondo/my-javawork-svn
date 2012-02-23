/*     */ package org.eclipse.jdt.internal.compiler.ast;
/*     */ 
/*     */ import org.eclipse.jdt.core.compiler.CharOperation;
/*     */ import org.eclipse.jdt.internal.compiler.ASTVisitor;
/*     */ import org.eclipse.jdt.internal.compiler.impl.Constant;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.ArrayBinding;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.Binding;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.BlockScope;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.ClassScope;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.FieldBinding;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.LocalVariableBinding;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.MethodScope;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;
/*     */ import org.eclipse.jdt.internal.compiler.problem.ProblemReporter;
/*     */ 
/*     */ public class Argument extends LocalDeclaration
/*     */ {
/*  21 */   private static final char[] SET = "set".toCharArray();
/*     */ 
/*     */   public Argument(char[] name, long posNom, TypeReference tr, int modifiers)
/*     */   {
/*  25 */     super(name, (int)(posNom >>> 32), (int)posNom);
/*  26 */     this.declarationSourceEnd = (int)posNom;
/*  27 */     this.modifiers = modifiers;
/*  28 */     this.type = tr;
/*  29 */     this.bits |= 1073741824;
/*     */   }
/*     */ 
/*     */   public void bind(MethodScope scope, TypeBinding typeBinding, boolean used)
/*     */   {
/*  35 */     Binding existingVariable = scope.getBinding(this.name, 3, this, false);
/*  36 */     if ((existingVariable != null) && (existingVariable.isValidBinding())) {
/*  37 */       if (((existingVariable instanceof LocalVariableBinding)) && (this.hiddenVariableDepth == 0)) {
/*  38 */         scope.problemReporter().redefineArgument(this);
/*     */       } else {
/*  40 */         boolean isSpecialArgument = false;
/*  41 */         if ((existingVariable instanceof FieldBinding)) {
/*  42 */           if (scope.isInsideConstructor()) {
/*  43 */             isSpecialArgument = true;
/*     */           } else {
/*  45 */             AbstractMethodDeclaration methodDecl = scope.referenceMethod();
/*  46 */             if ((methodDecl != null) && (CharOperation.prefixEquals(SET, methodDecl.selector))) {
/*  47 */               isSpecialArgument = true;
/*     */             }
/*     */           }
/*     */         }
/*  51 */         scope.problemReporter().localVariableHiding(this, existingVariable, isSpecialArgument);
/*     */       }
/*     */     }
/*     */ 
/*  55 */     if (this.binding == null) {
/*  56 */       this.binding = new LocalVariableBinding(this, typeBinding, this.modifiers, true);
/*     */     }
/*  58 */     scope.addLocalVariable(this.binding);
/*  59 */     resolveAnnotations(scope, this.annotations, this.binding);
/*     */ 
/*  61 */     this.binding.declaration = this;
/*  62 */     this.binding.useFlag = (used ? 1 : 0);
/*     */   }
/*     */ 
/*     */   public int getKind()
/*     */   {
/*  69 */     return 5;
/*     */   }
/*     */ 
/*     */   public boolean isVarArgs() {
/*  73 */     return (this.type != null) && ((this.type.bits & 0x4000) != 0);
/*     */   }
/*     */ 
/*     */   public StringBuffer print(int indent, StringBuffer output)
/*     */   {
/*  78 */     printIndent(indent, output);
/*  79 */     printModifiers(this.modifiers, output);
/*  80 */     if (this.annotations != null) printAnnotations(this.annotations, output);
/*     */ 
/*  82 */     if (this.type == null)
/*  83 */       output.append("<no type> ");
/*     */     else {
/*  85 */       this.type.print(0, output).append(' ');
/*     */     }
/*  87 */     return output.append(this.name);
/*     */   }
/*     */ 
/*     */   public StringBuffer printStatement(int indent, StringBuffer output)
/*     */   {
/*  92 */     return print(indent, output).append(';');
/*     */   }
/*     */ 
/*     */   public TypeBinding resolveForCatch(BlockScope scope)
/*     */   {
/* 100 */     TypeBinding exceptionType = this.type.resolveType(scope, true);
/*     */     boolean hasError;
/*     */     boolean hasError;
/* 102 */     if (exceptionType == null) {
/* 103 */       hasError = true;
/*     */     } else {
/* 105 */       hasError = false;
/* 106 */       switch (exceptionType.kind()) {
/*     */       case 260:
/* 108 */         if (!exceptionType.isBoundParameterizedType()) break;
/* 109 */         hasError = true;
/* 110 */         scope.problemReporter().invalidParameterizedExceptionType(exceptionType, this);
/*     */ 
/* 113 */         break;
/*     */       case 4100:
/* 115 */         scope.problemReporter().invalidTypeVariableAsException(exceptionType, this);
/* 116 */         hasError = true;
/*     */ 
/* 118 */         break;
/*     */       case 68:
/* 120 */         if (((ArrayBinding)exceptionType).leafComponentType != TypeBinding.VOID) break;
/* 121 */         scope.problemReporter().variableTypeCannotBeVoidArray(this);
/* 122 */         hasError = true;
/*     */       }
/*     */ 
/* 127 */       if ((exceptionType.findSuperTypeOriginatingFrom(21, true) == null) && (exceptionType.isValidBinding())) {
/* 128 */         scope.problemReporter().cannotThrowType(this.type, exceptionType);
/* 129 */         hasError = true;
/*     */       }
/*     */     }
/*     */ 
/* 133 */     Binding existingVariable = scope.getBinding(this.name, 3, this, false);
/* 134 */     if ((existingVariable != null) && (existingVariable.isValidBinding())) {
/* 135 */       if (((existingVariable instanceof LocalVariableBinding)) && (this.hiddenVariableDepth == 0))
/* 136 */         scope.problemReporter().redefineArgument(this);
/*     */       else {
/* 138 */         scope.problemReporter().localVariableHiding(this, existingVariable, false);
/*     */       }
/*     */     }
/*     */ 
/* 142 */     this.binding = new LocalVariableBinding(this, exceptionType, this.modifiers, false);
/* 143 */     resolveAnnotations(scope, this.annotations, this.binding);
/*     */ 
/* 145 */     scope.addLocalVariable(this.binding);
/* 146 */     this.binding.setConstant(Constant.NotAConstant);
/* 147 */     if (hasError) return null;
/* 148 */     return exceptionType;
/*     */   }
/*     */ 
/*     */   public void traverse(ASTVisitor visitor, BlockScope scope)
/*     */   {
/* 153 */     if (visitor.visit(this, scope)) {
/* 154 */       if (this.annotations != null) {
/* 155 */         int annotationsLength = this.annotations.length;
/* 156 */         for (int i = 0; i < annotationsLength; i++)
/* 157 */           this.annotations[i].traverse(visitor, scope);
/*     */       }
/* 159 */       if (this.type != null)
/* 160 */         this.type.traverse(visitor, scope);
/*     */     }
/* 162 */     visitor.endVisit(this, scope);
/*     */   }
/*     */ 
/*     */   public void traverse(ASTVisitor visitor, ClassScope scope) {
/* 166 */     if (visitor.visit(this, scope)) {
/* 167 */       if (this.annotations != null) {
/* 168 */         int annotationsLength = this.annotations.length;
/* 169 */         for (int i = 0; i < annotationsLength; i++)
/* 170 */           this.annotations[i].traverse(visitor, scope);
/*     */       }
/* 172 */       if (this.type != null)
/* 173 */         this.type.traverse(visitor, scope);
/*     */     }
/* 175 */     visitor.endVisit(this, scope);
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\ecj-3.5.2.jar
 * Qualified Name:     org.eclipse.jdt.internal.compiler.ast.Argument
 * JD-Core Version:    0.6.0
 */