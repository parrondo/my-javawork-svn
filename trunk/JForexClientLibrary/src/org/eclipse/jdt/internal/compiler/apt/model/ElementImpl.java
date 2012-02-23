/*     */ package org.eclipse.jdt.internal.compiler.apt.model;
/*     */ 
/*     */ import java.lang.annotation.Annotation;
/*     */ import java.lang.reflect.Proxy;
/*     */ import java.util.Collections;
/*     */ import java.util.List;
/*     */ import java.util.Set;
/*     */ import javax.lang.model.element.AnnotationMirror;
/*     */ import javax.lang.model.element.Element;
/*     */ import javax.lang.model.element.Modifier;
/*     */ import javax.lang.model.element.Name;
/*     */ import javax.lang.model.element.PackageElement;
/*     */ import javax.lang.model.type.TypeMirror;
/*     */ import org.eclipse.jdt.core.compiler.CharOperation;
/*     */ import org.eclipse.jdt.internal.compiler.apt.dispatch.BaseProcessingEnvImpl;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.AnnotationBinding;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.Binding;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.ReferenceBinding;
/*     */ 
/*     */ public abstract class ElementImpl
/*     */   implements Element, IElementInfo
/*     */ {
/*     */   public final BaseProcessingEnvImpl _env;
/*     */   public final Binding _binding;
/*     */ 
/*     */   protected ElementImpl(BaseProcessingEnvImpl env, Binding binding)
/*     */   {
/*  44 */     this._env = env;
/*  45 */     this._binding = binding;
/*     */   }
/*     */ 
/*     */   public TypeMirror asType()
/*     */   {
/*  50 */     return this._env.getFactory().newTypeMirror(this._binding);
/*     */   }
/*     */ 
/*     */   public <A extends Annotation> A getAnnotation(Class<A> annotationClass)
/*     */   {
/*  56 */     AnnotationBinding[] annoInstances = getAnnotationBindings();
/*  57 */     if ((annoInstances == null) || (annoInstances.length == 0) || (annotationClass == null)) {
/*  58 */       return null;
/*     */     }
/*  60 */     String annoTypeName = annotationClass.getName();
/*  61 */     if (annoTypeName == null) return null;
/*  62 */     annoTypeName = annoTypeName.replace('$', '.');
/*  63 */     for (AnnotationBinding annoInstance : annoInstances) {
/*  64 */       if (annoInstance == null)
/*     */         continue;
/*  66 */       ReferenceBinding binding = annoInstance.getAnnotationType();
/*  67 */       if ((binding == null) || (!binding.isAnnotationType()))
/*     */         continue;
/*     */       char[] qName;
/*  69 */       if (binding.isMemberType()) {
/*  70 */         char[] qName = CharOperation.concatWith(binding.enclosingType().compoundName, binding.sourceName, '.');
/*  71 */         CharOperation.replace(qName, '$', '.');
/*     */       } else {
/*  73 */         qName = CharOperation.concatWith(binding.compoundName, '.');
/*     */       }
/*  75 */       if (annoTypeName.equals(new String(qName))) {
/*  76 */         AnnotationMirrorImpl annoMirror = 
/*  77 */           (AnnotationMirrorImpl)this._env.getFactory().newAnnotationMirror(annoInstance);
/*  78 */         return (Annotation)Proxy.newProxyInstance(annotationClass.getClassLoader(), 
/*  79 */           new Class[] { annotationClass }, annoMirror);
/*     */       }
/*     */     }
/*     */ 
/*  83 */     return null;
/*     */   }
/*     */ 
/*     */   protected abstract AnnotationBinding[] getAnnotationBindings();
/*     */ 
/*     */   public List<? extends AnnotationMirror> getAnnotationMirrors()
/*     */   {
/*  93 */     return this._env.getFactory().getAnnotationMirrors(getAnnotationBindings());
/*     */   }
/*     */ 
/*     */   public Set<Modifier> getModifiers()
/*     */   {
/* 100 */     return Collections.emptySet();
/*     */   }
/*     */ 
/*     */   public Name getSimpleName()
/*     */   {
/* 105 */     return new NameImpl(this._binding.shortReadableName());
/*     */   }
/*     */ 
/*     */   public int hashCode()
/*     */   {
/* 110 */     return this._binding.hashCode();
/*     */   }
/*     */ 
/*     */   public boolean equals(Object obj)
/*     */   {
/* 117 */     if (this == obj)
/* 118 */       return true;
/* 119 */     if (obj == null)
/* 120 */       return false;
/* 121 */     if (getClass() != obj.getClass())
/* 122 */       return false;
/* 123 */     ElementImpl other = (ElementImpl)obj;
/* 124 */     if (this._binding == null) {
/* 125 */       if (other._binding != null)
/* 126 */         return false;
/* 127 */     } else if (this._binding != other._binding)
/* 128 */       return false;
/* 129 */     return true;
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/* 134 */     return this._binding.toString();
/*     */   }
/*     */ 
/*     */   public String getFileName()
/*     */   {
/* 140 */     return null;
/*     */   }
/*     */ 
/*     */   abstract PackageElement getPackage();
/*     */ 
/*     */   public boolean hides(Element hidden)
/*     */   {
/* 157 */     return false;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\ecj-3.5.2.jar
 * Qualified Name:     org.eclipse.jdt.internal.compiler.apt.model.ElementImpl
 * JD-Core Version:    0.6.0
 */