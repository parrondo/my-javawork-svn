/*    */ package org.apache.lucene.util;
/*    */ 
/*    */ import java.io.ObjectStreamException;
/*    */ import java.io.Serializable;
/*    */ import java.io.StreamCorruptedException;
/*    */ import java.util.HashMap;
/*    */ import java.util.Map;
/*    */ 
/*    */ @Deprecated
/*    */ public abstract class Parameter
/*    */   implements Serializable
/*    */ {
/* 34 */   static Map<String, Parameter> allParameters = new HashMap();
/*    */   private String name;
/*    */ 
/*    */   protected Parameter(String name)
/*    */   {
/* 40 */     this.name = name;
/* 41 */     String key = makeKey(name);
/*    */ 
/* 43 */     if (allParameters.containsKey(key)) {
/* 44 */       throw new IllegalArgumentException("Parameter name " + key + " already used!");
/*    */     }
/* 46 */     allParameters.put(key, this);
/*    */   }
/*    */ 
/*    */   private String makeKey(String name) {
/* 50 */     return getClass() + " " + name;
/*    */   }
/*    */ 
/*    */   public String toString()
/*    */   {
/* 55 */     return this.name;
/*    */   }
/*    */ 
/*    */   protected Object readResolve()
/*    */     throws ObjectStreamException
/*    */   {
/* 66 */     Object par = allParameters.get(makeKey(this.name));
/*    */ 
/* 68 */     if (par == null) {
/* 69 */       throw new StreamCorruptedException("Unknown parameter value: " + this.name);
/*    */     }
/* 71 */     return par;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.util.Parameter
 * JD-Core Version:    0.6.0
 */