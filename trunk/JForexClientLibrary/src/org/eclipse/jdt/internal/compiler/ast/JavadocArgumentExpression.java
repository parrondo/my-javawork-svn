/*     */ package org.eclipse.jdt.internal.compiler.ast;
/*     */ 
/*     */ import org.eclipse.jdt.internal.compiler.ASTVisitor;
/*     */ import org.eclipse.jdt.internal.compiler.impl.CompilerOptions;
/*     */ import org.eclipse.jdt.internal.compiler.impl.Constant;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.BlockScope;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.ClassScope;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.LookupEnvironment;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.PackageBinding;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.Scope;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;
/*     */ import org.eclipse.jdt.internal.compiler.problem.ProblemReporter;
/*     */ 
/*     */ public class JavadocArgumentExpression extends Expression
/*     */ {
/*     */   public char[] token;
/*     */   public Argument argument;
/*     */ 
/*     */   public JavadocArgumentExpression(char[] name, int startPos, int endPos, TypeReference typeRef)
/*     */   {
/*  23 */     this.token = name;
/*  24 */     this.sourceStart = startPos;
/*  25 */     this.sourceEnd = endPos;
/*  26 */     long pos = (startPos << 32) + endPos;
/*  27 */     this.argument = new Argument(name, pos, typeRef, 0);
/*  28 */     this.bits |= 32768;
/*     */   }
/*     */ 
/*     */   private TypeBinding internalResolveType(Scope scope)
/*     */   {
/*  35 */     this.constant = Constant.NotAConstant;
/*  36 */     if (this.resolvedType != null) {
/*  37 */       return this.resolvedType.isValidBinding() ? this.resolvedType : null;
/*     */     }
/*  39 */     if (this.argument != null) {
/*  40 */       TypeReference typeRef = this.argument.type;
/*  41 */       if (typeRef != null) {
/*  42 */         this.resolvedType = typeRef.getTypeBinding(scope);
/*  43 */         typeRef.resolvedType = this.resolvedType;
/*     */ 
/*  46 */         if (((typeRef instanceof SingleTypeReference)) && 
/*  47 */           (this.resolvedType.leafComponentType().enclosingType() != null) && 
/*  48 */           (scope.compilerOptions().complianceLevel <= 3145728L)) {
/*  49 */           scope.problemReporter().javadocInvalidMemberTypeQualification(this.sourceStart, this.sourceEnd, scope.getDeclarationModifiers());
/*     */         }
/*  52 */         else if ((typeRef instanceof QualifiedTypeReference)) {
/*  53 */           TypeBinding enclosingType = this.resolvedType.leafComponentType().enclosingType();
/*  54 */           if (enclosingType != null)
/*     */           {
/*  57 */             int compoundLength = 2;
/*  58 */             while ((enclosingType = enclosingType.enclosingType()) != null) compoundLength++;
/*  59 */             int typeNameLength = typeRef.getTypeName().length;
/*  60 */             if ((typeNameLength != compoundLength) && (typeNameLength != compoundLength + this.resolvedType.getPackage().compoundName.length)) {
/*  61 */               scope.problemReporter().javadocInvalidMemberTypeQualification(typeRef.sourceStart, typeRef.sourceEnd, scope.getDeclarationModifiers());
/*     */             }
/*     */           }
/*     */         }
/*  65 */         if (!this.resolvedType.isValidBinding()) {
/*  66 */           scope.problemReporter().javadocInvalidType(typeRef, this.resolvedType, scope.getDeclarationModifiers());
/*  67 */           return null;
/*     */         }
/*  69 */         if (isTypeUseDeprecated(this.resolvedType, scope)) {
/*  70 */           scope.problemReporter().javadocDeprecatedType(this.resolvedType, typeRef, scope.getDeclarationModifiers());
/*     */         }
/*  72 */         return this.resolvedType = scope.environment().convertToRawType(this.resolvedType, true);
/*     */       }
/*     */     }
/*  75 */     return null;
/*     */   }
/*     */ 
/*     */   public StringBuffer printExpression(int indent, StringBuffer output) {
/*  79 */     if (this.argument == null) {
/*  80 */       if (this.token != null) {
/*  81 */         output.append(this.token);
/*     */       }
/*     */     }
/*     */     else {
/*  85 */       this.argument.print(indent, output);
/*     */     }
/*  87 */     return output;
/*     */   }
/*     */ 
/*     */   public void resolve(BlockScope scope) {
/*  91 */     if (this.argument != null)
/*  92 */       this.argument.resolve(scope);
/*     */   }
/*     */ 
/*     */   public TypeBinding resolveType(BlockScope scope)
/*     */   {
/*  97 */     return internalResolveType(scope);
/*     */   }
/*     */ 
/*     */   public TypeBinding resolveType(ClassScope scope) {
/* 101 */     return internalResolveType(scope);
/*     */   }
/*     */ 
/*     */   public void traverse(ASTVisitor visitor, BlockScope blockScope)
/*     */   {
/* 109 */     if ((visitor.visit(this, blockScope)) && 
/* 110 */       (this.argument != null)) {
/* 111 */       this.argument.traverse(visitor, blockScope);
/*     */     }
/*     */ 
/* 114 */     visitor.endVisit(this, blockScope);
/*     */   }
/*     */   public void traverse(ASTVisitor visitor, ClassScope blockScope) {
/* 117 */     if ((visitor.visit(this, blockScope)) && 
/* 118 */       (this.argument != null)) {
/* 119 */       this.argument.traverse(visitor, blockScope);
/*     */     }
/*     */ 
/* 122 */     visitor.endVisit(this, blockScope);
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\ecj-3.5.2.jar
 * Qualified Name:     org.eclipse.jdt.internal.compiler.ast.JavadocArgumentExpression
 * JD-Core Version:    0.6.0
 */