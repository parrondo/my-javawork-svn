/*    */ package org.eclipse.jdt.internal.compiler.apt.model;
/*    */ 
/*    */ import javax.lang.model.type.PrimitiveType;
/*    */ import javax.lang.model.type.TypeKind;
/*    */ import javax.lang.model.type.TypeVisitor;
/*    */ import org.eclipse.jdt.internal.compiler.lookup.BaseTypeBinding;
/*    */ import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;
/*    */ 
/*    */ public class PrimitiveTypeImpl extends TypeMirrorImpl
/*    */   implements PrimitiveType
/*    */ {
/* 29 */   public static final PrimitiveTypeImpl BOOLEAN = new PrimitiveTypeImpl(TypeBinding.BOOLEAN);
/* 30 */   public static final PrimitiveTypeImpl BYTE = new PrimitiveTypeImpl(TypeBinding.BYTE);
/* 31 */   public static final PrimitiveTypeImpl CHAR = new PrimitiveTypeImpl(TypeBinding.CHAR);
/* 32 */   public static final PrimitiveTypeImpl DOUBLE = new PrimitiveTypeImpl(TypeBinding.DOUBLE);
/* 33 */   public static final PrimitiveTypeImpl FLOAT = new PrimitiveTypeImpl(TypeBinding.FLOAT);
/* 34 */   public static final PrimitiveTypeImpl INT = new PrimitiveTypeImpl(TypeBinding.INT);
/* 35 */   public static final PrimitiveTypeImpl LONG = new PrimitiveTypeImpl(TypeBinding.LONG);
/* 36 */   public static final PrimitiveTypeImpl SHORT = new PrimitiveTypeImpl(TypeBinding.SHORT);
/*    */ 
/*    */   private PrimitiveTypeImpl(BaseTypeBinding binding)
/*    */   {
/* 44 */     super(null, binding);
/*    */   }
/*    */ 
/*    */   public <R, P> R accept(TypeVisitor<R, P> v, P p)
/*    */   {
/* 50 */     return v.visitPrimitive(this, p);
/*    */   }
/*    */ 
/*    */   public TypeKind getKind()
/*    */   {
/* 58 */     return getKind((BaseTypeBinding)this._binding);
/*    */   }
/*    */ 
/*    */   public static TypeKind getKind(BaseTypeBinding binding) {
/* 62 */     switch (binding.id) {
/*    */     case 5:
/* 64 */       return TypeKind.BOOLEAN;
/*    */     case 3:
/* 66 */       return TypeKind.BYTE;
/*    */     case 2:
/* 68 */       return TypeKind.CHAR;
/*    */     case 8:
/* 70 */       return TypeKind.DOUBLE;
/*    */     case 9:
/* 72 */       return TypeKind.FLOAT;
/*    */     case 10:
/* 74 */       return TypeKind.INT;
/*    */     case 7:
/* 76 */       return TypeKind.LONG;
/*    */     case 4:
/* 78 */       return TypeKind.SHORT;
/*    */     case 6:
/* 80 */     }throw new IllegalArgumentException("BaseTypeBinding of unexpected id " + binding.id);
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\ecj-3.5.2.jar
 * Qualified Name:     org.eclipse.jdt.internal.compiler.apt.model.PrimitiveTypeImpl
 * JD-Core Version:    0.6.0
 */