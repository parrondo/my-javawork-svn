/*    */ package org.eclipse.jdt.internal.compiler.classfmt;
/*    */ 
/*    */ import org.eclipse.jdt.internal.compiler.env.IBinaryElementValuePair;
/*    */ 
/*    */ public class ElementValuePairInfo
/*    */   implements IBinaryElementValuePair
/*    */ {
/* 15 */   static final ElementValuePairInfo[] NoMembers = new ElementValuePairInfo[0];
/*    */   private char[] name;
/*    */   private Object value;
/*    */ 
/*    */   ElementValuePairInfo(char[] name, Object value)
/*    */   {
/* 21 */     this.name = name;
/* 22 */     this.value = value;
/*    */   }
/*    */   public char[] getName() {
/* 25 */     return this.name;
/*    */   }
/*    */   public Object getValue() {
/* 28 */     return this.value;
/*    */   }
/*    */   public String toString() {
/* 31 */     StringBuffer buffer = new StringBuffer();
/* 32 */     buffer.append(this.name);
/* 33 */     buffer.append('=');
/* 34 */     if ((this.value instanceof Object[])) {
/* 35 */       Object[] values = (Object[])this.value;
/* 36 */       buffer.append('{');
/* 37 */       int i = 0; for (int l = values.length; i < l; i++) {
/* 38 */         if (i > 0)
/* 39 */           buffer.append(", ");
/* 40 */         buffer.append(values[i]);
/*    */       }
/* 42 */       buffer.append('}');
/*    */     } else {
/* 44 */       buffer.append(this.value);
/*    */     }
/* 46 */     return buffer.toString();
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\ecj-3.5.2.jar
 * Qualified Name:     org.eclipse.jdt.internal.compiler.classfmt.ElementValuePairInfo
 * JD-Core Version:    0.6.0
 */