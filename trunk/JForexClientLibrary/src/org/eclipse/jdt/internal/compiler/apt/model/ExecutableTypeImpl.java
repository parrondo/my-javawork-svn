/*     */ package org.eclipse.jdt.internal.compiler.apt.model;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collections;
/*     */ import java.util.List;
/*     */ import javax.lang.model.type.ExecutableType;
/*     */ import javax.lang.model.type.TypeKind;
/*     */ import javax.lang.model.type.TypeMirror;
/*     */ import javax.lang.model.type.TypeVariable;
/*     */ import javax.lang.model.type.TypeVisitor;
/*     */ import org.eclipse.jdt.internal.compiler.apt.dispatch.BaseProcessingEnvImpl;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.MethodBinding;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.ReferenceBinding;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.TypeVariableBinding;
/*     */ 
/*     */ public class ExecutableTypeImpl extends TypeMirrorImpl
/*     */   implements ExecutableType
/*     */ {
/*     */   public ExecutableTypeImpl(BaseProcessingEnvImpl env, MethodBinding binding)
/*     */   {
/*  38 */     super(env, binding);
/*     */   }
/*     */ 
/*     */   public List<? extends TypeMirror> getParameterTypes()
/*     */   {
/*  45 */     MethodBinding binding = (MethodBinding)this._binding;
/*  46 */     TypeBinding[] parameters = binding.parameters;
/*  47 */     int length = parameters.length;
/*  48 */     boolean isEnumConstructor = (binding.isConstructor()) && 
/*  49 */       (binding.declaringClass.isEnum()) && 
/*  50 */       (binding.declaringClass.isBinaryBinding()) && 
/*  51 */       ((binding.modifiers & 0x40000000) == 0);
/*  52 */     if (isEnumConstructor) {
/*  53 */       if (length == 2) {
/*  54 */         return Collections.emptyList();
/*     */       }
/*  56 */       ArrayList list = new ArrayList();
/*  57 */       for (int i = 2; i < length; i++) {
/*  58 */         list.add(this._env.getFactory().newTypeMirror(parameters[i]));
/*     */       }
/*  60 */       return Collections.unmodifiableList(list);
/*     */     }
/*  62 */     if (length != 0) {
/*  63 */       ArrayList list = new ArrayList();
/*  64 */       for (TypeBinding typeBinding : parameters) {
/*  65 */         list.add(this._env.getFactory().newTypeMirror(typeBinding));
/*     */       }
/*  67 */       return Collections.unmodifiableList(list);
/*     */     }
/*  69 */     return Collections.emptyList();
/*     */   }
/*     */ 
/*     */   public TypeMirror getReturnType()
/*     */   {
/*  77 */     return this._env.getFactory().newTypeMirror(((MethodBinding)this._binding).returnType);
/*     */   }
/*     */ 
/*     */   public List<? extends TypeMirror> getThrownTypes()
/*     */   {
/*  85 */     ArrayList list = new ArrayList();
/*  86 */     ReferenceBinding[] thrownExceptions = ((MethodBinding)this._binding).thrownExceptions;
/*  87 */     if (thrownExceptions.length != 0) {
/*  88 */       for (ReferenceBinding referenceBinding : thrownExceptions) {
/*  89 */         list.add(this._env.getFactory().newTypeMirror(referenceBinding));
/*     */       }
/*     */     }
/*  92 */     return Collections.unmodifiableList(list);
/*     */   }
/*     */ 
/*     */   public List<? extends TypeVariable> getTypeVariables()
/*     */   {
/* 100 */     ArrayList list = new ArrayList();
/* 101 */     TypeVariableBinding[] typeVariables = ((MethodBinding)this._binding).typeVariables();
/* 102 */     if (typeVariables.length != 0) {
/* 103 */       for (TypeVariableBinding typeVariableBinding : typeVariables) {
/* 104 */         list.add((TypeVariable)this._env.getFactory().newTypeMirror(typeVariableBinding));
/*     */       }
/*     */     }
/* 107 */     return Collections.unmodifiableList(list);
/*     */   }
/*     */ 
/*     */   public <R, P> R accept(TypeVisitor<R, P> v, P p)
/*     */   {
/* 115 */     return v.visitExecutable(this, p);
/*     */   }
/*     */ 
/*     */   public TypeKind getKind()
/*     */   {
/* 123 */     return TypeKind.EXECUTABLE;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\ecj-3.5.2.jar
 * Qualified Name:     org.eclipse.jdt.internal.compiler.apt.model.ExecutableTypeImpl
 * JD-Core Version:    0.6.0
 */