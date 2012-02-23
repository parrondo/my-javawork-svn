/*    */ package org.eclipse.jdt.internal.compiler.apt.model;
/*    */ 
/*    */ import javax.lang.model.element.Element;
/*    */ import javax.lang.model.type.TypeKind;
/*    */ import javax.lang.model.type.TypeMirror;
/*    */ import javax.lang.model.type.TypeVariable;
/*    */ import javax.lang.model.type.TypeVisitor;
/*    */ import org.eclipse.jdt.internal.compiler.apt.dispatch.BaseProcessingEnvImpl;
/*    */ import org.eclipse.jdt.internal.compiler.lookup.ReferenceBinding;
/*    */ import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;
/*    */ import org.eclipse.jdt.internal.compiler.lookup.TypeVariableBinding;
/*    */ 
/*    */ public class TypeVariableImpl extends TypeMirrorImpl
/*    */   implements TypeVariable
/*    */ {
/*    */   public TypeVariableImpl(BaseProcessingEnvImpl env, TypeVariableBinding binding)
/*    */   {
/* 33 */     super(env, binding);
/*    */   }
/*    */ 
/*    */   public Element asElement()
/*    */   {
/* 40 */     return this._env.getFactory().newElement(this._binding);
/*    */   }
/*    */ 
/*    */   public TypeMirror getLowerBound()
/*    */   {
/* 49 */     return this._env.getFactory().getNullType();
/*    */   }
/*    */ 
/*    */   public TypeMirror getUpperBound()
/*    */   {
/* 57 */     TypeVariableBinding typeVariableBinding = (TypeVariableBinding)this._binding;
/* 58 */     TypeBinding firstBound = typeVariableBinding.firstBound;
/* 59 */     ReferenceBinding[] superInterfaces = typeVariableBinding.superInterfaces;
/* 60 */     if ((firstBound == null) || (superInterfaces.length == 0))
/*    */     {
/* 62 */       return this._env.getFactory().newTypeMirror(typeVariableBinding.upperBound());
/*    */     }
/* 64 */     if ((firstBound != null) && (superInterfaces.length == 1) && (superInterfaces[0] == firstBound))
/*    */     {
/* 66 */       return this._env.getFactory().newTypeMirror(typeVariableBinding.upperBound());
/*    */     }
/* 68 */     return this._env.getFactory().newDeclaredType((TypeVariableBinding)this._binding);
/*    */   }
/*    */ 
/*    */   public <R, P> R accept(TypeVisitor<R, P> v, P p)
/*    */   {
/* 76 */     return v.visitTypeVariable(this, p);
/*    */   }
/*    */ 
/*    */   public TypeKind getKind()
/*    */   {
/* 82 */     TypeVariableBinding variableBinding = (TypeVariableBinding)this._binding;
/* 83 */     if ((!variableBinding.isValidBinding()) || ((variableBinding.tagBits & 0x80) != 0L)) {
/* 84 */       return TypeKind.ERROR;
/*    */     }
/* 86 */     return TypeKind.TYPEVAR;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\ecj-3.5.2.jar
 * Qualified Name:     org.eclipse.jdt.internal.compiler.apt.model.TypeVariableImpl
 * JD-Core Version:    0.6.0
 */