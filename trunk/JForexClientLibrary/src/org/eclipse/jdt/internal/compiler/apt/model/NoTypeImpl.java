/*    */ package org.eclipse.jdt.internal.compiler.apt.model;
/*    */ 
/*    */ import javax.lang.model.type.NoType;
/*    */ import javax.lang.model.type.NullType;
/*    */ import javax.lang.model.type.TypeKind;
/*    */ import javax.lang.model.type.TypeVisitor;
/*    */ 
/*    */ public class NoTypeImpl
/*    */   implements NoType, NullType
/*    */ {
/*    */   private final TypeKind _kind;
/* 27 */   public static final NoType NO_TYPE_NONE = new NoTypeImpl(TypeKind.NONE);
/* 28 */   public static final NoType NO_TYPE_VOID = new NoTypeImpl(TypeKind.VOID);
/* 29 */   public static final NoType NO_TYPE_PACKAGE = new NoTypeImpl(TypeKind.PACKAGE);
/* 30 */   public static final NullType NULL_TYPE = new NoTypeImpl(TypeKind.NULL);
/*    */ 
/*    */   private NoTypeImpl(TypeKind kind) {
/* 33 */     this._kind = kind;
/*    */   }
/*    */ 
/*    */   public <R, P> R accept(TypeVisitor<R, P> v, P p)
/*    */   {
/* 39 */     switch ($SWITCH_TABLE$javax$lang$model$type$TypeKind()[getKind().ordinal()])
/*    */     {
/*    */     case 11:
/* 42 */       return v.visitNull(this, p);
/*    */     }
/* 44 */     return v.visitNoType(this, p);
/*    */   }
/*    */ 
/*    */   public TypeKind getKind()
/*    */   {
/* 51 */     return this._kind;
/*    */   }
/*    */ 
/*    */   public String toString()
/*    */   {
/* 56 */     switch ($SWITCH_TABLE$javax$lang$model$type$TypeKind()[this._kind.ordinal()]) { case 10:
/*    */     case 12:
/*    */     case 13:
/*    */     case 14:
/*    */     case 15:
/*    */     case 16:
/*    */     default:
/* 59 */       return "<none>";
/*    */     case 11:
/* 61 */       return "null";
/*    */     case 9:
/* 63 */       return "void";
/*    */     case 17: }
/* 65 */     return "package";
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\ecj-3.5.2.jar
 * Qualified Name:     org.eclipse.jdt.internal.compiler.apt.model.NoTypeImpl
 * JD-Core Version:    0.6.0
 */