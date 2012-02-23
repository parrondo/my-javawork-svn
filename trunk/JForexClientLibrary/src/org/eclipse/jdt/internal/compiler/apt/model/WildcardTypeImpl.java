/*    */ package org.eclipse.jdt.internal.compiler.apt.model;
/*    */ 
/*    */ import javax.lang.model.type.TypeKind;
/*    */ import javax.lang.model.type.TypeMirror;
/*    */ import javax.lang.model.type.TypeVisitor;
/*    */ import javax.lang.model.type.WildcardType;
/*    */ import org.eclipse.jdt.internal.compiler.apt.dispatch.BaseProcessingEnvImpl;
/*    */ import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;
/*    */ import org.eclipse.jdt.internal.compiler.lookup.WildcardBinding;
/*    */ 
/*    */ public class WildcardTypeImpl extends TypeMirrorImpl
/*    */   implements WildcardType
/*    */ {
/*    */   public WildcardTypeImpl(BaseProcessingEnvImpl env, WildcardBinding binding)
/*    */   {
/* 31 */     super(env, binding);
/*    */   }
/*    */ 
/*    */   public TypeMirror getExtendsBound()
/*    */   {
/* 39 */     WildcardBinding wildcardBinding = (WildcardBinding)this._binding;
/* 40 */     if (wildcardBinding.boundKind != 1) return null;
/* 41 */     TypeBinding bound = wildcardBinding.bound;
/* 42 */     if (bound == null) return null;
/* 43 */     return this._env.getFactory().newTypeMirror(bound);
/*    */   }
/*    */ 
/*    */   public TypeKind getKind()
/*    */   {
/* 51 */     WildcardBinding wildcardBinding = (WildcardBinding)this._binding;
/* 52 */     if ((!wildcardBinding.isValidBinding()) || ((wildcardBinding.tagBits & 0x80) != 0L)) {
/* 53 */       return TypeKind.ERROR;
/*    */     }
/* 55 */     return TypeKind.WILDCARD;
/*    */   }
/*    */ 
/*    */   public TypeMirror getSuperBound()
/*    */   {
/* 62 */     WildcardBinding wildcardBinding = (WildcardBinding)this._binding;
/* 63 */     if (wildcardBinding.boundKind != 2) return null;
/* 64 */     TypeBinding bound = wildcardBinding.bound;
/* 65 */     if (bound == null) return null;
/* 66 */     return this._env.getFactory().newTypeMirror(bound);
/*    */   }
/*    */ 
/*    */   public <R, P> R accept(TypeVisitor<R, P> v, P p)
/*    */   {
/* 71 */     return v.visitWildcard(this, p);
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\ecj-3.5.2.jar
 * Qualified Name:     org.eclipse.jdt.internal.compiler.apt.model.WildcardTypeImpl
 * JD-Core Version:    0.6.0
 */