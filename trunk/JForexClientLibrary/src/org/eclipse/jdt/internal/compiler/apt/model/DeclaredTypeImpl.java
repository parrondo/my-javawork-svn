/*     */ package org.eclipse.jdt.internal.compiler.apt.model;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collections;
/*     */ import java.util.List;
/*     */ import javax.lang.model.element.Element;
/*     */ import javax.lang.model.element.ElementKind;
/*     */ import javax.lang.model.type.DeclaredType;
/*     */ import javax.lang.model.type.TypeKind;
/*     */ import javax.lang.model.type.TypeMirror;
/*     */ import javax.lang.model.type.TypeVisitor;
/*     */ import org.eclipse.jdt.internal.compiler.apt.dispatch.BaseProcessingEnvImpl;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.Binding;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.ParameterizedTypeBinding;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.ReferenceBinding;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.TypeVariableBinding;
/*     */ 
/*     */ public class DeclaredTypeImpl extends TypeMirrorImpl
/*     */   implements DeclaredType
/*     */ {
/*     */   private final ElementKind _elementKindHint;
/*     */ 
/*     */   DeclaredTypeImpl(BaseProcessingEnvImpl env, ReferenceBinding binding)
/*     */   {
/*  43 */     super(env, binding);
/*  44 */     this._elementKindHint = null;
/*     */   }
/*     */ 
/*     */   DeclaredTypeImpl(BaseProcessingEnvImpl env, ReferenceBinding binding, ElementKind elementKindHint)
/*     */   {
/*  53 */     super(env, binding);
/*  54 */     this._elementKindHint = elementKindHint;
/*     */   }
/*     */ 
/*     */   public Element asElement()
/*     */   {
/*  60 */     return this._env.getFactory().newElement((ReferenceBinding)this._binding, this._elementKindHint);
/*     */   }
/*     */ 
/*     */   public TypeMirror getEnclosingType()
/*     */   {
/*  65 */     ReferenceBinding binding = (ReferenceBinding)this._binding;
/*  66 */     ReferenceBinding enclosingType = binding.enclosingType();
/*  67 */     if (enclosingType != null) return this._env.getFactory().newDeclaredType(enclosingType);
/*  68 */     return this._env.getFactory().getNoType(TypeKind.NONE);
/*     */   }
/*     */ 
/*     */   public List<? extends TypeMirror> getTypeArguments()
/*     */   {
/*  78 */     ReferenceBinding binding = (ReferenceBinding)this._binding;
/*     */     List args;
/*     */     TypeBinding arg;
/*  79 */     if (binding.isParameterizedType()) {
/*  80 */       ParameterizedTypeBinding ptb = (ParameterizedTypeBinding)this._binding;
/*  81 */       TypeBinding[] arguments = ptb.arguments;
/*  82 */       int length = arguments == null ? 0 : arguments.length;
/*  83 */       if (length == 0) return Collections.emptyList();
/*  84 */       args = new ArrayList(length);
/*  85 */       for (arg : arguments) {
/*  86 */         args.add(this._env.getFactory().newTypeMirror(arg));
/*     */       }
/*  88 */       return Collections.unmodifiableList(args);
/*     */     }
/*  90 */     if (binding.isGenericType()) {
/*  91 */       TypeVariableBinding[] typeVariables = binding.typeVariables();
/*  92 */       List args = new ArrayList(typeVariables.length);
/*     */       TypeVariableBinding[] arrayOfTypeVariableBinding1;
/*  93 */       arg = (arrayOfTypeVariableBinding1 = typeVariables).length; for (args = 0; args < arg; args++) { TypeBinding arg = arrayOfTypeVariableBinding1[args];
/*  94 */         args.add(this._env.getFactory().newTypeMirror(arg));
/*     */       }
/*  96 */       return Collections.unmodifiableList(args);
/*     */     }
/*  98 */     return Collections.emptyList();
/*     */   }
/*     */ 
/*     */   public <R, P> R accept(TypeVisitor<R, P> v, P p)
/*     */   {
/* 106 */     return v.visitDeclared(this, p);
/*     */   }
/*     */ 
/*     */   public TypeKind getKind()
/*     */   {
/* 114 */     ReferenceBinding type = (ReferenceBinding)this._binding;
/* 115 */     if (!type.isValidBinding()) {
/* 116 */       return TypeKind.ERROR;
/*     */     }
/* 118 */     return TypeKind.DECLARED;
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/* 123 */     return new String(this._binding.readableName());
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\ecj-3.5.2.jar
 * Qualified Name:     org.eclipse.jdt.internal.compiler.apt.model.DeclaredTypeImpl
 * JD-Core Version:    0.6.0
 */