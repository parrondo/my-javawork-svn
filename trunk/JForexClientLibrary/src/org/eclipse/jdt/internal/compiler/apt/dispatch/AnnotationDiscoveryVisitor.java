/*     */ package org.eclipse.jdt.internal.compiler.apt.dispatch;
/*     */ 
/*     */ import javax.lang.model.element.Element;
/*     */ import javax.lang.model.element.TypeElement;
/*     */ import org.eclipse.jdt.internal.compiler.ASTVisitor;
/*     */ import org.eclipse.jdt.internal.compiler.apt.model.Factory;
/*     */ import org.eclipse.jdt.internal.compiler.apt.util.ManyToMany;
/*     */ import org.eclipse.jdt.internal.compiler.ast.ASTNode;
/*     */ import org.eclipse.jdt.internal.compiler.ast.AbstractMethodDeclaration;
/*     */ import org.eclipse.jdt.internal.compiler.ast.Annotation;
/*     */ import org.eclipse.jdt.internal.compiler.ast.Argument;
/*     */ import org.eclipse.jdt.internal.compiler.ast.ConstructorDeclaration;
/*     */ import org.eclipse.jdt.internal.compiler.ast.FieldDeclaration;
/*     */ import org.eclipse.jdt.internal.compiler.ast.MethodDeclaration;
/*     */ import org.eclipse.jdt.internal.compiler.ast.TypeDeclaration;
/*     */ import org.eclipse.jdt.internal.compiler.impl.ReferenceContext;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.AnnotationBinding;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.AptSourceLocalVariableBinding;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.Binding;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.BlockScope;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.ClassScope;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.CompilationUnitScope;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.FieldBinding;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.MethodBinding;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.MethodScope;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.SourceTypeBinding;
/*     */ 
/*     */ public class AnnotationDiscoveryVisitor extends ASTVisitor
/*     */ {
/*     */   final BaseProcessingEnvImpl _env;
/*     */   final Factory _factory;
/*     */   final ManyToMany<TypeElement, Element> _annoToElement;
/*     */ 
/*     */   public AnnotationDiscoveryVisitor(BaseProcessingEnvImpl env)
/*     */   {
/*  53 */     this._env = env;
/*  54 */     this._factory = env.getFactory();
/*  55 */     this._annoToElement = new ManyToMany();
/*     */   }
/*     */ 
/*     */   public boolean visit(Argument argument, BlockScope scope)
/*     */   {
/*  60 */     Annotation[] annotations = argument.annotations;
/*  61 */     ReferenceContext referenceContext = scope.referenceContext();
/*  62 */     if ((referenceContext instanceof AbstractMethodDeclaration)) {
/*  63 */       MethodBinding binding = ((AbstractMethodDeclaration)referenceContext).binding;
/*  64 */       if (binding != null) {
/*  65 */         TypeDeclaration typeDeclaration = scope.referenceType();
/*  66 */         typeDeclaration.binding.resolveTypesFor(binding);
/*  67 */         if (argument.binding != null) {
/*  68 */           argument.binding = new AptSourceLocalVariableBinding(argument.binding, binding);
/*     */         }
/*     */       }
/*  71 */       if (annotations != null) {
/*  72 */         resolveAnnotations(
/*  73 */           scope, 
/*  74 */           annotations, 
/*  75 */           argument.binding);
/*     */       }
/*     */     }
/*  78 */     return false;
/*     */   }
/*     */ 
/*     */   public boolean visit(ConstructorDeclaration constructorDeclaration, ClassScope scope)
/*     */   {
/*  83 */     Annotation[] annotations = constructorDeclaration.annotations;
/*  84 */     if (annotations != null) {
/*  85 */       MethodBinding constructorBinding = constructorDeclaration.binding;
/*  86 */       if (constructorBinding == null) {
/*  87 */         return false;
/*     */       }
/*  89 */       ((SourceTypeBinding)constructorBinding.declaringClass).resolveTypesFor(constructorBinding);
/*  90 */       resolveAnnotations(
/*  91 */         constructorDeclaration.scope, 
/*  92 */         annotations, 
/*  93 */         constructorBinding);
/*     */     }
/*  95 */     Argument[] arguments = constructorDeclaration.arguments;
/*  96 */     if (arguments != null) {
/*  97 */       int argumentLength = arguments.length;
/*  98 */       for (int i = 0; i < argumentLength; i++) {
/*  99 */         arguments[i].traverse(this, constructorDeclaration.scope);
/*     */       }
/*     */     }
/* 102 */     return false;
/*     */   }
/*     */ 
/*     */   public boolean visit(FieldDeclaration fieldDeclaration, MethodScope scope)
/*     */   {
/* 107 */     Annotation[] annotations = fieldDeclaration.annotations;
/* 108 */     if (annotations != null) {
/* 109 */       FieldBinding fieldBinding = fieldDeclaration.binding;
/* 110 */       if (fieldBinding == null) {
/* 111 */         return false;
/*     */       }
/* 113 */       ((SourceTypeBinding)fieldBinding.declaringClass).resolveTypeFor(fieldBinding);
/* 114 */       resolveAnnotations(scope, annotations, fieldBinding);
/*     */     }
/* 116 */     return false;
/*     */   }
/*     */ 
/*     */   public boolean visit(MethodDeclaration methodDeclaration, ClassScope scope)
/*     */   {
/* 121 */     Annotation[] annotations = methodDeclaration.annotations;
/* 122 */     if (annotations != null) {
/* 123 */       MethodBinding methodBinding = methodDeclaration.binding;
/* 124 */       if (methodBinding == null) {
/* 125 */         return false;
/*     */       }
/* 127 */       ((SourceTypeBinding)methodBinding.declaringClass).resolveTypesFor(methodBinding);
/* 128 */       resolveAnnotations(
/* 129 */         methodDeclaration.scope, 
/* 130 */         annotations, 
/* 131 */         methodDeclaration.binding);
/*     */     }
/*     */ 
/* 134 */     Argument[] arguments = methodDeclaration.arguments;
/* 135 */     if (arguments != null) {
/* 136 */       int argumentLength = arguments.length;
/* 137 */       for (int i = 0; i < argumentLength; i++) {
/* 138 */         arguments[i].traverse(this, methodDeclaration.scope);
/*     */       }
/*     */     }
/* 141 */     return false;
/*     */   }
/*     */ 
/*     */   public boolean visit(TypeDeclaration memberTypeDeclaration, ClassScope scope)
/*     */   {
/* 146 */     SourceTypeBinding binding = memberTypeDeclaration.binding;
/* 147 */     if (binding == null) {
/* 148 */       return false;
/*     */     }
/* 150 */     Annotation[] annotations = memberTypeDeclaration.annotations;
/* 151 */     if (annotations != null) {
/* 152 */       resolveAnnotations(
/* 153 */         memberTypeDeclaration.staticInitializerScope, 
/* 154 */         annotations, 
/* 155 */         binding);
/*     */     }
/* 157 */     return true;
/*     */   }
/*     */ 
/*     */   public boolean visit(TypeDeclaration typeDeclaration, CompilationUnitScope scope)
/*     */   {
/* 162 */     SourceTypeBinding binding = typeDeclaration.binding;
/* 163 */     if (binding == null) {
/* 164 */       return false;
/*     */     }
/* 166 */     Annotation[] annotations = typeDeclaration.annotations;
/* 167 */     if (annotations != null) {
/* 168 */       resolveAnnotations(
/* 169 */         typeDeclaration.staticInitializerScope, 
/* 170 */         annotations, 
/* 171 */         binding);
/*     */     }
/* 173 */     return true;
/*     */   }
/*     */ 
/*     */   private void resolveAnnotations(BlockScope scope, Annotation[] annotations, Binding currentBinding)
/*     */   {
/* 180 */     ASTNode.resolveAnnotations(scope, annotations, currentBinding);
/*     */ 
/* 182 */     for (Annotation annotation : annotations) {
/* 183 */       AnnotationBinding binding = annotation.getCompilerAnnotation();
/* 184 */       if (binding != null) {
/* 185 */         TypeElement anno = (TypeElement)this._factory.newElement(binding.getAnnotationType());
/* 186 */         Element element = this._factory.newElement(currentBinding);
/* 187 */         this._annoToElement.put(anno, element);
/*     */       }
/*     */     }
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\ecj-3.5.2.jar
 * Qualified Name:     org.eclipse.jdt.internal.compiler.apt.dispatch.AnnotationDiscoveryVisitor
 * JD-Core Version:    0.6.0
 */