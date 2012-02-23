/*     */ package org.eclipse.jdt.internal.compiler.ast;
/*     */ 
/*     */ import org.eclipse.jdt.internal.compiler.ASTVisitor;
/*     */ import org.eclipse.jdt.internal.compiler.impl.Constant;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.Binding;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.BlockScope;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.ClassScope;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.FieldBinding;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.MethodBinding;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.ProblemFieldBinding;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.ProblemMethodBinding;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.ProblemReferenceBinding;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.ReferenceBinding;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.Scope;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;
/*     */ import org.eclipse.jdt.internal.compiler.problem.ProblemReporter;
/*     */ 
/*     */ public class JavadocFieldReference extends FieldReference
/*     */ {
/*     */   public int tagSourceStart;
/*     */   public int tagSourceEnd;
/*     */   public int tagValue;
/*     */   public MethodBinding methodBinding;
/*     */ 
/*     */   public JavadocFieldReference(char[] source, long pos)
/*     */   {
/*  24 */     super(source, pos);
/*  25 */     this.bits |= 32768;
/*     */   }
/*     */ 
/*     */   protected TypeBinding internalResolveType(Scope scope)
/*     */   {
/*  42 */     this.constant = Constant.NotAConstant;
/*  43 */     if (this.receiver == null)
/*  44 */       this.actualReceiverType = scope.enclosingReceiverType();
/*  45 */     else if (scope.kind == 3)
/*  46 */       this.actualReceiverType = this.receiver.resolveType((ClassScope)scope);
/*     */     else {
/*  48 */       this.actualReceiverType = this.receiver.resolveType((BlockScope)scope);
/*     */     }
/*  50 */     if (this.actualReceiverType == null) {
/*  51 */       return null;
/*     */     }
/*     */ 
/*  54 */     Binding fieldBinding = (this.receiver != null) && (this.receiver.isThis()) ? 
/*  55 */       scope.classScope().getBinding(this.token, this.bits & 0x7, this, true) : 
/*  56 */       scope.getField(this.actualReceiverType, this.token, this);
/*  57 */     if (!fieldBinding.isValidBinding())
/*     */     {
/*  59 */       switch (fieldBinding.problemId()) {
/*     */       case 5:
/*     */       case 6:
/*     */       case 7:
/*  63 */         FieldBinding closestMatch = ((ProblemFieldBinding)fieldBinding).closestMatch;
/*  64 */         if (closestMatch == null) break;
/*  65 */         fieldBinding = closestMatch;
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/*  70 */     if ((!fieldBinding.isValidBinding()) || (!(fieldBinding instanceof FieldBinding))) {
/*  71 */       if ((this.receiver.resolvedType instanceof ProblemReferenceBinding))
/*     */       {
/*  73 */         return null;
/*     */       }
/*  75 */       if ((this.actualReceiverType instanceof ReferenceBinding)) {
/*  76 */         ReferenceBinding refBinding = (ReferenceBinding)this.actualReceiverType;
/*  77 */         MethodBinding possibleMethod = this.receiver.isThis() ? 
/*  78 */           scope.getImplicitMethod(this.token, Binding.NO_TYPES, this) : 
/*  79 */           scope.getMethod(refBinding, this.token, Binding.NO_TYPES, this);
/*  80 */         if (possibleMethod.isValidBinding()) {
/*  81 */           this.methodBinding = possibleMethod;
/*     */         } else {
/*  83 */           ProblemMethodBinding problemMethodBinding = (ProblemMethodBinding)possibleMethod;
/*  84 */           if (problemMethodBinding.closestMatch == null) {
/*  85 */             if (fieldBinding.isValidBinding())
/*     */             {
/*  88 */               fieldBinding = new ProblemFieldBinding(refBinding, fieldBinding.readableName(), 1);
/*     */             }
/*  90 */             scope.problemReporter().javadocInvalidField(this, fieldBinding, this.actualReceiverType, scope.getDeclarationModifiers());
/*     */           } else {
/*  92 */             this.methodBinding = problemMethodBinding.closestMatch;
/*     */           }
/*     */         }
/*     */       }
/*  96 */       return null;
/*     */     }
/*  98 */     this.binding = ((FieldBinding)fieldBinding);
/*     */ 
/* 100 */     if (isFieldUseDeprecated(this.binding, scope, (this.bits & 0x2000) != 0)) {
/* 101 */       scope.problemReporter().javadocDeprecatedField(this.binding, this, scope.getDeclarationModifiers());
/*     */     }
/* 103 */     return this.resolvedType = this.binding.type;
/*     */   }
/*     */ 
/*     */   public boolean isSuperAccess() {
/* 107 */     return (this.bits & 0x4000) != 0;
/*     */   }
/*     */ 
/*     */   public StringBuffer printExpression(int indent, StringBuffer output)
/*     */   {
/* 112 */     if (this.receiver != null) {
/* 113 */       this.receiver.printExpression(0, output);
/*     */     }
/* 115 */     output.append('#').append(this.token);
/* 116 */     return output;
/*     */   }
/*     */ 
/*     */   public TypeBinding resolveType(BlockScope scope) {
/* 120 */     return internalResolveType(scope);
/*     */   }
/*     */ 
/*     */   public TypeBinding resolveType(ClassScope scope) {
/* 124 */     return internalResolveType(scope);
/*     */   }
/*     */ 
/*     */   public void traverse(ASTVisitor visitor, BlockScope scope)
/*     */   {
/* 133 */     if ((visitor.visit(this, scope)) && 
/* 134 */       (this.receiver != null)) {
/* 135 */       this.receiver.traverse(visitor, scope);
/*     */     }
/*     */ 
/* 138 */     visitor.endVisit(this, scope);
/*     */   }
/*     */ 
/*     */   public void traverse(ASTVisitor visitor, ClassScope scope) {
/* 142 */     if ((visitor.visit(this, scope)) && 
/* 143 */       (this.receiver != null)) {
/* 144 */       this.receiver.traverse(visitor, scope);
/*     */     }
/*     */ 
/* 147 */     visitor.endVisit(this, scope);
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\ecj-3.5.2.jar
 * Qualified Name:     org.eclipse.jdt.internal.compiler.ast.JavadocFieldReference
 * JD-Core Version:    0.6.0
 */