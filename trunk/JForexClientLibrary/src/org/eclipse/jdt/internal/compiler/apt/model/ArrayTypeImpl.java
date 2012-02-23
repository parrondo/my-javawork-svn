/*    */ package org.eclipse.jdt.internal.compiler.apt.model;
/*    */ 
/*    */ import javax.lang.model.type.ArrayType;
/*    */ import javax.lang.model.type.TypeKind;
/*    */ import javax.lang.model.type.TypeMirror;
/*    */ import javax.lang.model.type.TypeVisitor;
/*    */ import org.eclipse.jdt.internal.compiler.apt.dispatch.BaseProcessingEnvImpl;
/*    */ import org.eclipse.jdt.internal.compiler.lookup.ArrayBinding;
/*    */ import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;
/*    */ 
/*    */ public class ArrayTypeImpl extends TypeMirrorImpl
/*    */   implements ArrayType
/*    */ {
/*    */   ArrayTypeImpl(BaseProcessingEnvImpl env, ArrayBinding binding)
/*    */   {
/* 30 */     super(env, binding);
/*    */   }
/*    */ 
/*    */   public TypeMirror getComponentType()
/*    */   {
/* 38 */     return this._env.getFactory().newTypeMirror(((ArrayBinding)this._binding).elementsType());
/*    */   }
/*    */ 
/*    */   public <R, P> R accept(TypeVisitor<R, P> v, P p)
/*    */   {
/* 46 */     return v.visitArray(this, p);
/*    */   }
/*    */ 
/*    */   public TypeKind getKind()
/*    */   {
/* 54 */     ArrayBinding type = (ArrayBinding)this._binding;
/* 55 */     if ((!type.isValidBinding()) || ((type.leafComponentType().tagBits & 0x80) != 0L)) {
/* 56 */       return TypeKind.ERROR;
/*    */     }
/* 58 */     return TypeKind.ARRAY;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\ecj-3.5.2.jar
 * Qualified Name:     org.eclipse.jdt.internal.compiler.apt.model.ArrayTypeImpl
 * JD-Core Version:    0.6.0
 */