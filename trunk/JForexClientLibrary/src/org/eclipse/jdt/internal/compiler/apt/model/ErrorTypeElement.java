/*     */ package org.eclipse.jdt.internal.compiler.apt.model;
/*     */ 
/*     */ import java.lang.annotation.Annotation;
/*     */ import java.util.Collections;
/*     */ import java.util.List;
/*     */ import java.util.Set;
/*     */ import javax.lang.model.element.AnnotationMirror;
/*     */ import javax.lang.model.element.Element;
/*     */ import javax.lang.model.element.ElementKind;
/*     */ import javax.lang.model.element.Modifier;
/*     */ import javax.lang.model.element.Name;
/*     */ import javax.lang.model.element.NestingKind;
/*     */ import javax.lang.model.element.TypeParameterElement;
/*     */ import javax.lang.model.type.TypeKind;
/*     */ import javax.lang.model.type.TypeMirror;
/*     */ import org.eclipse.jdt.internal.compiler.apt.dispatch.BaseProcessingEnvImpl;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.LookupEnvironment;
/*     */ import org.eclipse.jdt.internal.compiler.util.Util;
/*     */ 
/*     */ public class ErrorTypeElement extends TypeElementImpl
/*     */ {
/*     */   ErrorTypeElement(BaseProcessingEnvImpl env)
/*     */   {
/*  38 */     super(env, null, null);
/*     */   }
/*     */ 
/*     */   public List<? extends TypeMirror> getInterfaces()
/*     */   {
/*  45 */     return Collections.emptyList();
/*     */   }
/*     */ 
/*     */   public NestingKind getNestingKind()
/*     */   {
/*  53 */     return NestingKind.TOP_LEVEL;
/*     */   }
/*     */ 
/*     */   public Name getQualifiedName()
/*     */   {
/*  61 */     return new NameImpl(Util.EMPTY_STRING);
/*     */   }
/*     */ 
/*     */   public TypeMirror getSuperclass()
/*     */   {
/*  69 */     return this._env.getFactory().getNoType(TypeKind.NONE);
/*     */   }
/*     */ 
/*     */   public List<? extends TypeParameterElement> getTypeParameters()
/*     */   {
/*  77 */     return Collections.emptyList();
/*     */   }
/*     */ 
/*     */   public TypeMirror asType()
/*     */   {
/*  85 */     return this._env.getFactory().getErrorType();
/*     */   }
/*     */ 
/*     */   public <A extends Annotation> A getAnnotation(Class<A> annotationType)
/*     */   {
/*  93 */     return null;
/*     */   }
/*     */ 
/*     */   public List<? extends AnnotationMirror> getAnnotationMirrors()
/*     */   {
/* 101 */     return Collections.emptyList();
/*     */   }
/*     */ 
/*     */   public List<? extends Element> getEnclosedElements()
/*     */   {
/* 109 */     return Collections.emptyList();
/*     */   }
/*     */ 
/*     */   public Element getEnclosingElement()
/*     */   {
/* 117 */     return this._env.getFactory().newPackageElement(this._env.getLookupEnvironment().defaultPackage);
/*     */   }
/*     */ 
/*     */   public ElementKind getKind()
/*     */   {
/* 125 */     return ElementKind.CLASS;
/*     */   }
/*     */ 
/*     */   public Set<Modifier> getModifiers()
/*     */   {
/* 133 */     return Collections.emptySet();
/*     */   }
/*     */ 
/*     */   public Name getSimpleName()
/*     */   {
/* 141 */     return new NameImpl(Util.EMPTY_STRING);
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\ecj-3.5.2.jar
 * Qualified Name:     org.eclipse.jdt.internal.compiler.apt.model.ErrorTypeElement
 * JD-Core Version:    0.6.0
 */