/*     */ package org.eclipse.jdt.internal.compiler.ast;
/*     */ 
/*     */ import org.eclipse.jdt.internal.compiler.ASTVisitor;
/*     */ import org.eclipse.jdt.internal.compiler.ClassFile;
/*     */ import org.eclipse.jdt.internal.compiler.CompilationResult;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.ClassScope;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.ElementValuePair;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.MethodBinding;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.MethodScope;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;
/*     */ import org.eclipse.jdt.internal.compiler.parser.Parser;
/*     */ import org.eclipse.jdt.internal.compiler.problem.ProblemReporter;
/*     */ 
/*     */ public class AnnotationMethodDeclaration extends MethodDeclaration
/*     */ {
/*     */   public Expression defaultValue;
/*     */   public int extendedDimensions;
/*     */ 
/*     */   public AnnotationMethodDeclaration(CompilationResult compilationResult)
/*     */   {
/*  29 */     super(compilationResult);
/*     */   }
/*     */ 
/*     */   public void generateCode(ClassFile classFile) {
/*  33 */     classFile.generateMethodInfoHeader(this.binding);
/*  34 */     int methodAttributeOffset = classFile.contentsOffset;
/*  35 */     int attributeNumber = classFile.generateMethodInfoAttribute(this.binding, this);
/*  36 */     classFile.completeMethodInfo(methodAttributeOffset, attributeNumber);
/*     */   }
/*     */ 
/*     */   public boolean isAnnotationMethod()
/*     */   {
/*  41 */     return true;
/*     */   }
/*     */ 
/*     */   public boolean isMethod()
/*     */   {
/*  46 */     return false;
/*     */   }
/*     */ 
/*     */   public void parseStatements(Parser parser, CompilationUnitDeclaration unit)
/*     */   {
/*     */   }
/*     */ 
/*     */   public StringBuffer print(int tab, StringBuffer output)
/*     */   {
/*  56 */     printIndent(tab, output);
/*  57 */     printModifiers(this.modifiers, output);
/*  58 */     if (this.annotations != null) printAnnotations(this.annotations, output);
/*     */ 
/*  60 */     TypeParameter[] typeParams = typeParameters();
/*  61 */     if (typeParams != null) {
/*  62 */       output.append('<');
/*  63 */       int max = typeParams.length - 1;
/*  64 */       for (int j = 0; j < max; j++) {
/*  65 */         typeParams[j].print(0, output);
/*  66 */         output.append(", ");
/*     */       }
/*  68 */       typeParams[max].print(0, output);
/*  69 */       output.append('>');
/*     */     }
/*     */ 
/*  72 */     printReturnType(0, output).append(this.selector).append('(');
/*  73 */     if (this.arguments != null) {
/*  74 */       for (int i = 0; i < this.arguments.length; i++) {
/*  75 */         if (i > 0) output.append(", ");
/*  76 */         this.arguments[i].print(0, output);
/*     */       }
/*     */     }
/*  79 */     output.append(')');
/*  80 */     if (this.thrownExceptions != null) {
/*  81 */       output.append(" throws ");
/*  82 */       for (int i = 0; i < this.thrownExceptions.length; i++) {
/*  83 */         if (i > 0) output.append(", ");
/*  84 */         this.thrownExceptions[i].print(0, output);
/*     */       }
/*     */     }
/*     */ 
/*  88 */     if (this.defaultValue != null) {
/*  89 */       output.append(" default ");
/*  90 */       this.defaultValue.print(0, output);
/*     */     }
/*     */ 
/*  93 */     printBody(tab + 1, output);
/*  94 */     return output;
/*     */   }
/*     */ 
/*     */   public void resolveStatements()
/*     */   {
/*  99 */     super.resolveStatements();
/* 100 */     if (this.arguments != null) {
/* 101 */       this.scope.problemReporter().annotationMembersCannotHaveParameters(this);
/*     */     }
/* 103 */     if (this.typeParameters != null) {
/* 104 */       this.scope.problemReporter().annotationMembersCannotHaveTypeParameters(this);
/*     */     }
/* 106 */     if (this.extendedDimensions != 0) {
/* 107 */       this.scope.problemReporter().illegalExtendedDimensions(this);
/*     */     }
/* 109 */     if (this.binding == null) return;
/* 110 */     TypeBinding returnTypeBinding = this.binding.returnType;
/* 111 */     if (returnTypeBinding != null)
/*     */     {
/* 115 */       TypeBinding leafReturnType = returnTypeBinding.leafComponentType();
/* 116 */       if (returnTypeBinding.dimensions() <= 1)
/* 117 */         switch (leafReturnType.erasure().id) {
/*     */         case 2:
/*     */         case 3:
/*     */         case 4:
/*     */         case 5:
/*     */         case 7:
/*     */         case 8:
/*     */         case 9:
/*     */         case 10:
/*     */         case 11:
/*     */         case 16:
/* 128 */           break;
/*     */         case 6:
/*     */         case 12:
/*     */         case 13:
/*     */         case 14:
/*     */         case 15:
/*     */         default:
/* 130 */           if ((leafReturnType.isEnum()) || (leafReturnType.isAnnotationType()))
/*     */             break;
/*     */         }
/* 133 */       else this.scope.problemReporter().invalidAnnotationMemberType(this);
/*     */ 
/* 135 */       if (this.defaultValue != null) {
/* 136 */         MemberValuePair pair = new MemberValuePair(this.selector, this.sourceStart, this.sourceEnd, this.defaultValue);
/* 137 */         pair.binding = this.binding;
/* 138 */         pair.resolveTypeExpecting(this.scope, returnTypeBinding);
/* 139 */         this.binding.setDefaultValue(ElementValuePair.getValue(this.defaultValue));
/*     */       } else {
/* 141 */         this.binding.setDefaultValue(null);
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public void traverse(ASTVisitor visitor, ClassScope classScope)
/*     */   {
/* 150 */     if (visitor.visit(this, classScope)) {
/* 151 */       if (this.annotations != null) {
/* 152 */         int annotationsLength = this.annotations.length;
/* 153 */         for (int i = 0; i < annotationsLength; i++)
/* 154 */           this.annotations[i].traverse(visitor, this.scope);
/*     */       }
/* 156 */       if (this.returnType != null) {
/* 157 */         this.returnType.traverse(visitor, this.scope);
/*     */       }
/* 159 */       if (this.defaultValue != null) {
/* 160 */         this.defaultValue.traverse(visitor, this.scope);
/*     */       }
/*     */     }
/* 163 */     visitor.endVisit(this, classScope);
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\ecj-3.5.2.jar
 * Qualified Name:     org.eclipse.jdt.internal.compiler.ast.AnnotationMethodDeclaration
 * JD-Core Version:    0.6.0
 */