/*     */ package org.eclipse.jdt.internal.compiler.ast;
/*     */ 
/*     */ import org.eclipse.jdt.internal.compiler.ASTVisitor;
/*     */ import org.eclipse.jdt.internal.compiler.codegen.CodeStream;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.Binding;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.BlockScope;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.ClassScope;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.MethodScope;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.Scope;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.SourceTypeBinding;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.TypeVariableBinding;
/*     */ import org.eclipse.jdt.internal.compiler.problem.ProblemReporter;
/*     */ 
/*     */ public class TypeParameter extends AbstractVariableDeclaration
/*     */ {
/*     */   public TypeVariableBinding binding;
/*     */   public TypeReference[] bounds;
/*     */ 
/*     */   public int getKind()
/*     */   {
/*  30 */     return 6;
/*     */   }
/*     */ 
/*     */   public void checkBounds(Scope scope)
/*     */   {
/*  35 */     if (this.type != null) {
/*  36 */       this.type.checkBounds(scope);
/*     */     }
/*  38 */     if (this.bounds != null) {
/*  39 */       int i = 0; for (int length = this.bounds.length; i < length; i++)
/*  40 */         this.bounds[i].checkBounds(scope);
/*     */     }
/*     */   }
/*     */ 
/*     */   private void internalResolve(Scope scope, boolean staticContext)
/*     */   {
/*  47 */     if (this.binding != null) {
/*  48 */       Binding existingType = scope.parent.getBinding(this.name, 4, this, false);
/*  49 */       if ((existingType != null) && 
/*  50 */         (this.binding != existingType) && 
/*  51 */         (existingType.isValidBinding()) && (
/*  52 */         (existingType.kind() != 4100) || (!staticContext)))
/*  53 */         scope.problemReporter().typeHiding(this, existingType);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void resolve(BlockScope scope)
/*     */   {
/*  59 */     internalResolve(scope, scope.methodScope().isStatic);
/*     */   }
/*     */ 
/*     */   public void resolve(ClassScope scope) {
/*  63 */     internalResolve(scope, scope.enclosingSourceType().isStatic());
/*     */   }
/*     */ 
/*     */   public StringBuffer printStatement(int indent, StringBuffer output)
/*     */   {
/*  70 */     output.append(this.name);
/*  71 */     if (this.type != null) {
/*  72 */       output.append(" extends ");
/*  73 */       this.type.print(0, output);
/*     */     }
/*  75 */     if (this.bounds != null) {
/*  76 */       for (int i = 0; i < this.bounds.length; i++) {
/*  77 */         output.append(" & ");
/*  78 */         this.bounds[i].print(0, output);
/*     */       }
/*     */     }
/*  81 */     return output;
/*     */   }
/*     */ 
/*     */   public void generateCode(BlockScope currentScope, CodeStream codeStream)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void traverse(ASTVisitor visitor, BlockScope scope) {
/*  89 */     if (visitor.visit(this, scope)) {
/*  90 */       if (this.type != null) {
/*  91 */         this.type.traverse(visitor, scope);
/*     */       }
/*  93 */       if (this.bounds != null) {
/*  94 */         int boundsLength = this.bounds.length;
/*  95 */         for (int i = 0; i < boundsLength; i++) {
/*  96 */           this.bounds[i].traverse(visitor, scope);
/*     */         }
/*     */       }
/*     */     }
/* 100 */     visitor.endVisit(this, scope);
/*     */   }
/*     */ 
/*     */   public void traverse(ASTVisitor visitor, ClassScope scope) {
/* 104 */     if (visitor.visit(this, scope)) {
/* 105 */       if (this.type != null) {
/* 106 */         this.type.traverse(visitor, scope);
/*     */       }
/* 108 */       if (this.bounds != null) {
/* 109 */         int boundsLength = this.bounds.length;
/* 110 */         for (int i = 0; i < boundsLength; i++) {
/* 111 */           this.bounds[i].traverse(visitor, scope);
/*     */         }
/*     */       }
/*     */     }
/* 115 */     visitor.endVisit(this, scope);
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\ecj-3.5.2.jar
 * Qualified Name:     org.eclipse.jdt.internal.compiler.ast.TypeParameter
 * JD-Core Version:    0.6.0
 */