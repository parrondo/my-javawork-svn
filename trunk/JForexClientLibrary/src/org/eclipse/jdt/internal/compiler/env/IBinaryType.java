/*    */ package org.eclipse.jdt.internal.compiler.env;
/*    */ 
/*    */ import org.eclipse.jdt.core.compiler.CharOperation;
/*    */ 
/*    */ public abstract interface IBinaryType extends IGenericType
/*    */ {
/* 17 */   public static final char[][] NoInterface = CharOperation.NO_CHAR_CHAR;
/* 18 */   public static final IBinaryNestedType[] NoNestedType = new IBinaryNestedType[0];
/* 19 */   public static final IBinaryField[] NoField = new IBinaryField[0];
/* 20 */   public static final IBinaryMethod[] NoMethod = new IBinaryMethod[0];
/*    */ 
/*    */   public abstract IBinaryAnnotation[] getAnnotations();
/*    */ 
/*    */   public abstract char[] getEnclosingMethod();
/*    */ 
/*    */   public abstract char[] getEnclosingTypeName();
/*    */ 
/*    */   public abstract IBinaryField[] getFields();
/*    */ 
/*    */   public abstract char[] getGenericSignature();
/*    */ 
/*    */   public abstract char[][] getInterfaceNames();
/*    */ 
/*    */   public abstract IBinaryNestedType[] getMemberTypes();
/*    */ 
/*    */   public abstract IBinaryMethod[] getMethods();
/*    */ 
/*    */   public abstract char[][][] getMissingTypeNames();
/*    */ 
/*    */   public abstract char[] getName();
/*    */ 
/*    */   public abstract char[] getSourceName();
/*    */ 
/*    */   public abstract char[] getSuperclassName();
/*    */ 
/*    */   public abstract long getTagBits();
/*    */ 
/*    */   public abstract boolean isAnonymous();
/*    */ 
/*    */   public abstract boolean isLocal();
/*    */ 
/*    */   public abstract boolean isMember();
/*    */ 
/*    */   public abstract char[] sourceFileName();
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\ecj-3.5.2.jar
 * Qualified Name:     org.eclipse.jdt.internal.compiler.env.IBinaryType
 * JD-Core Version:    0.6.0
 */