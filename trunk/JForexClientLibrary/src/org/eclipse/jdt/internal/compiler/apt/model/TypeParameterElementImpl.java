/*     */ package org.eclipse.jdt.internal.compiler.apt.model;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collections;
/*     */ import java.util.List;
/*     */ import javax.lang.model.element.Element;
/*     */ import javax.lang.model.element.ElementKind;
/*     */ import javax.lang.model.element.ElementVisitor;
/*     */ import javax.lang.model.element.PackageElement;
/*     */ import javax.lang.model.element.TypeParameterElement;
/*     */ import javax.lang.model.type.TypeMirror;
/*     */ import org.eclipse.jdt.internal.compiler.apt.dispatch.BaseProcessingEnvImpl;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.AnnotationBinding;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.Binding;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.LookupEnvironment;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.ReferenceBinding;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.TypeVariableBinding;
/*     */ 
/*     */ public class TypeParameterElementImpl extends ElementImpl
/*     */   implements TypeParameterElement
/*     */ {
/*     */   private final Element _declaringElement;
/*  40 */   private List<? extends TypeMirror> _bounds = null;
/*     */ 
/*     */   TypeParameterElementImpl(BaseProcessingEnvImpl env, TypeVariableBinding binding, Element declaringElement) {
/*  43 */     super(env, binding);
/*  44 */     this._declaringElement = declaringElement;
/*     */   }
/*     */ 
/*     */   TypeParameterElementImpl(BaseProcessingEnvImpl env, TypeVariableBinding binding) {
/*  48 */     super(env, binding);
/*  49 */     this._declaringElement = this._env.getFactory().newElement(binding.declaringElement);
/*     */   }
/*     */ 
/*     */   public List<? extends TypeMirror> getBounds()
/*     */   {
/*  55 */     if (this._bounds == null) {
/*  56 */       this._bounds = calculateBounds();
/*     */     }
/*  58 */     return this._bounds;
/*     */   }
/*     */ 
/*     */   private List<? extends TypeMirror> calculateBounds()
/*     */   {
/*  63 */     TypeVariableBinding typeVariableBinding = (TypeVariableBinding)this._binding;
/*  64 */     ReferenceBinding varSuperclass = typeVariableBinding.superclass();
/*  65 */     TypeBinding firstClassOrArrayBound = typeVariableBinding.firstBound;
/*  66 */     int boundsLength = 0;
/*  67 */     if (firstClassOrArrayBound != null) {
/*  68 */       if (firstClassOrArrayBound == varSuperclass)
/*  69 */         boundsLength++;
/*  70 */       else if (firstClassOrArrayBound.isArrayType())
/*  71 */         boundsLength++;
/*     */       else {
/*  73 */         firstClassOrArrayBound = null;
/*     */       }
/*     */     }
/*  76 */     ReferenceBinding[] superinterfaces = typeVariableBinding.superInterfaces();
/*  77 */     int superinterfacesLength = 0;
/*  78 */     if (superinterfaces != null) {
/*  79 */       superinterfacesLength = superinterfaces.length;
/*  80 */       boundsLength += superinterfacesLength;
/*     */     }
/*  82 */     List typeBounds = new ArrayList(boundsLength);
/*  83 */     if (boundsLength != 0) {
/*  84 */       if (firstClassOrArrayBound != null) {
/*  85 */         TypeMirror typeBinding = this._env.getFactory().newTypeMirror(firstClassOrArrayBound);
/*  86 */         if (typeBinding == null) {
/*  87 */           return Collections.emptyList();
/*     */         }
/*  89 */         typeBounds.add(typeBinding);
/*     */       }
/*  91 */       if (superinterfaces != null)
/*  92 */         for (int i = 0; i < superinterfacesLength; i++) {
/*  93 */           TypeMirror typeBinding = this._env.getFactory().newTypeMirror(superinterfaces[i]);
/*  94 */           if (typeBinding == null) {
/*  95 */             return Collections.emptyList();
/*     */           }
/*  97 */           typeBounds.add(typeBinding);
/*     */         }
/*     */     }
/*     */     else
/*     */     {
/* 102 */       typeBounds.add(this._env.getFactory().newTypeMirror(this._env.getLookupEnvironment().getType(LookupEnvironment.JAVA_LANG_OBJECT)));
/*     */     }
/* 104 */     return Collections.unmodifiableList(typeBounds);
/*     */   }
/*     */ 
/*     */   public Element getGenericElement()
/*     */   {
/* 110 */     return this._declaringElement;
/*     */   }
/*     */ 
/*     */   public <R, P> R accept(ElementVisitor<R, P> v, P p)
/*     */   {
/* 116 */     return v.visitTypeParameter(this, p);
/*     */   }
/*     */ 
/*     */   protected AnnotationBinding[] getAnnotationBindings()
/*     */   {
/* 127 */     return null;
/*     */   }
/*     */ 
/*     */   public List<? extends Element> getEnclosedElements()
/*     */   {
/* 138 */     return Collections.emptyList();
/*     */   }
/*     */ 
/*     */   public Element getEnclosingElement()
/*     */   {
/* 149 */     return null;
/*     */   }
/*     */ 
/*     */   public ElementKind getKind()
/*     */   {
/* 155 */     return ElementKind.TYPE_PARAMETER;
/*     */   }
/*     */ 
/*     */   PackageElement getPackage()
/*     */   {
/* 162 */     return null;
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/* 167 */     return new String(this._binding.readableName());
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\ecj-3.5.2.jar
 * Qualified Name:     org.eclipse.jdt.internal.compiler.apt.model.TypeParameterElementImpl
 * JD-Core Version:    0.6.0
 */