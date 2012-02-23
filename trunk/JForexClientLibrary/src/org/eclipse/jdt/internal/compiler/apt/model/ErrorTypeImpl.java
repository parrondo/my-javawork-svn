/*    */ package org.eclipse.jdt.internal.compiler.apt.model;
/*    */ 
/*    */ import java.util.Collections;
/*    */ import java.util.List;
/*    */ import javax.lang.model.element.Element;
/*    */ import javax.lang.model.type.ErrorType;
/*    */ import javax.lang.model.type.TypeKind;
/*    */ import javax.lang.model.type.TypeMirror;
/*    */ import javax.lang.model.type.TypeVisitor;
/*    */ import org.eclipse.jdt.internal.compiler.apt.dispatch.BaseProcessingEnvImpl;
/*    */ 
/*    */ public class ErrorTypeImpl extends DeclaredTypeImpl
/*    */   implements ErrorType
/*    */ {
/*    */   ErrorTypeImpl(BaseProcessingEnvImpl env)
/*    */   {
/* 30 */     super(env, null);
/*    */   }
/*    */ 
/*    */   public Element asElement()
/*    */   {
/* 38 */     return this._env.getFactory().newElement(null);
/*    */   }
/*    */ 
/*    */   public TypeMirror getEnclosingType()
/*    */   {
/* 46 */     return NoTypeImpl.NO_TYPE_NONE;
/*    */   }
/*    */ 
/*    */   public List<? extends TypeMirror> getTypeArguments()
/*    */   {
/* 54 */     return Collections.emptyList();
/*    */   }
/*    */ 
/*    */   public <R, P> R accept(TypeVisitor<R, P> v, P p)
/*    */   {
/* 62 */     return v.visitError(this, p);
/*    */   }
/*    */ 
/*    */   public TypeKind getKind()
/*    */   {
/* 70 */     return TypeKind.ERROR;
/*    */   }
/*    */ 
/*    */   public String toString()
/*    */   {
/* 75 */     return "<any>";
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\ecj-3.5.2.jar
 * Qualified Name:     org.eclipse.jdt.internal.compiler.apt.model.ErrorTypeImpl
 * JD-Core Version:    0.6.0
 */