/*    */ package org.eclipse.jdt.internal.compiler.apt.model;
/*    */ 
/*    */ import javax.lang.model.element.Name;
/*    */ 
/*    */ public class NameImpl
/*    */   implements Name
/*    */ {
/*    */   private final String _name;
/*    */ 
/*    */   private NameImpl()
/*    */   {
/* 28 */     this._name = null;
/*    */   }
/*    */ 
/*    */   public NameImpl(CharSequence cs)
/*    */   {
/* 33 */     this._name = cs.toString();
/*    */   }
/*    */ 
/*    */   public NameImpl(char[] chars)
/*    */   {
/* 38 */     this._name = String.valueOf(chars);
/*    */   }
/*    */ 
/*    */   public boolean contentEquals(CharSequence cs)
/*    */   {
/* 46 */     return this._name.equals(cs.toString());
/*    */   }
/*    */ 
/*    */   public char charAt(int index)
/*    */   {
/* 54 */     return this._name.charAt(index);
/*    */   }
/*    */ 
/*    */   public int length()
/*    */   {
/* 62 */     return this._name.length();
/*    */   }
/*    */ 
/*    */   public CharSequence subSequence(int start, int end)
/*    */   {
/* 70 */     return this._name.subSequence(start, end);
/*    */   }
/*    */ 
/*    */   public String toString()
/*    */   {
/* 75 */     return this._name;
/*    */   }
/*    */ 
/*    */   public int hashCode()
/*    */   {
/* 80 */     return this._name.hashCode();
/*    */   }
/*    */ 
/*    */   public boolean equals(Object obj)
/*    */   {
/* 85 */     if (this == obj)
/* 86 */       return true;
/* 87 */     if (obj == null)
/* 88 */       return false;
/* 89 */     if (getClass() != obj.getClass())
/* 90 */       return false;
/* 91 */     NameImpl other = (NameImpl)obj;
/* 92 */     return this._name.equals(other._name);
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\ecj-3.5.2.jar
 * Qualified Name:     org.eclipse.jdt.internal.compiler.apt.model.NameImpl
 * JD-Core Version:    0.6.0
 */