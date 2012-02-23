/*     */ package org.eclipse.jdt.internal.compiler.apt.model;
/*     */ 
/*     */ import javax.lang.model.type.TypeKind;
/*     */ import javax.lang.model.type.TypeMirror;
/*     */ import javax.lang.model.type.TypeVisitor;
/*     */ import org.eclipse.jdt.internal.compiler.apt.dispatch.BaseProcessingEnvImpl;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.Binding;
/*     */ 
/*     */ public class TypeMirrorImpl
/*     */   implements TypeMirror
/*     */ {
/*     */   protected final BaseProcessingEnvImpl _env;
/*     */   protected final Binding _binding;
/*     */ 
/*     */   TypeMirrorImpl(BaseProcessingEnvImpl env, Binding binding)
/*     */   {
/*  34 */     this._env = env;
/*  35 */     this._binding = binding;
/*     */   }
/*     */ 
/*     */   Binding binding() {
/*  39 */     return this._binding;
/*     */   }
/*     */ 
/*     */   public <R, P> R accept(TypeVisitor<R, P> v, P p)
/*     */   {
/*  47 */     return v.visit(this, p);
/*     */   }
/*     */ 
/*     */   public TypeKind getKind()
/*     */   {
/*  55 */     switch (this._binding.kind())
/*     */     {
/*     */     case 1:
/*     */     case 2:
/*     */     case 3:
/*     */     case 32:
/*  72 */       throw new IllegalArgumentException("Invalid binding kind: " + this._binding.kind());
/*     */     }
/*  74 */     return null;
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/*  82 */     return new String(this._binding.readableName());
/*     */   }
/*     */ 
/*     */   public int hashCode()
/*     */   {
/*  91 */     int result = 1;
/*  92 */     result = 31 * result + (this._binding == null ? 0 : this._binding.hashCode());
/*  93 */     return result;
/*     */   }
/*     */ 
/*     */   public boolean equals(Object obj)
/*     */   {
/* 101 */     if (this == obj)
/* 102 */       return true;
/* 103 */     if (!(obj instanceof TypeMirrorImpl))
/* 104 */       return false;
/* 105 */     TypeMirrorImpl other = (TypeMirrorImpl)obj;
/* 106 */     return this._binding == other._binding;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\ecj-3.5.2.jar
 * Qualified Name:     org.eclipse.jdt.internal.compiler.apt.model.TypeMirrorImpl
 * JD-Core Version:    0.6.0
 */