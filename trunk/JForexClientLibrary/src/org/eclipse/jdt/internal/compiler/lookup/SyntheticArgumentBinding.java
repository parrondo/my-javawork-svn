/*    */ package org.eclipse.jdt.internal.compiler.lookup;
/*    */ 
/*    */ import org.eclipse.jdt.core.compiler.CharOperation;
/*    */ 
/*    */ public class SyntheticArgumentBinding extends LocalVariableBinding
/*    */ {
/*    */   public LocalVariableBinding actualOuterLocalVariable;
/*    */   public FieldBinding matchingField;
/*    */ 
/*    */   public SyntheticArgumentBinding(LocalVariableBinding actualOuterLocalVariable)
/*    */   {
/* 45 */     super(CharOperation.concat(TypeConstants.SYNTHETIC_OUTER_LOCAL_PREFIX, actualOuterLocalVariable.name), 
/* 43 */       actualOuterLocalVariable.type, 
/* 44 */       16, 
/* 45 */       true);
/*    */ 
/* 30 */     this.tagBits |= 1024L;
/* 31 */     this.useFlag = 1;
/*    */ 
/* 46 */     this.actualOuterLocalVariable = actualOuterLocalVariable;
/*    */   }
/*    */ 
/*    */   public SyntheticArgumentBinding(ReferenceBinding enclosingType)
/*    */   {
/* 57 */     super(CharOperation.concat(
/* 53 */       TypeConstants.SYNTHETIC_ENCLOSING_INSTANCE_PREFIX, 
/* 54 */       String.valueOf(enclosingType.depth()).toCharArray()), 
/* 55 */       enclosingType, 
/* 56 */       16, 
/* 57 */       true);
/*    */ 
/* 30 */     this.tagBits |= 1024L;
/* 31 */     this.useFlag = 1;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\ecj-3.5.2.jar
 * Qualified Name:     org.eclipse.jdt.internal.compiler.lookup.SyntheticArgumentBinding
 * JD-Core Version:    0.6.0
 */