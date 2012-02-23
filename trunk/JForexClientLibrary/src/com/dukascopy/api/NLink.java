/*    */ package com.dukascopy.api;
/*    */ 
/*    */ import com.dukascopy.api.nlink.NLinkException;
/*    */ import com.dukascopy.api.nlink.Wrapper;
/*    */ import java.io.File;
/*    */ import java.lang.reflect.Proxy;
/*    */ 
/*    */ public abstract class NLink
/*    */ {
/*    */   public static <T> T create(Class<T> dllInterface)
/*    */   {
/* 27 */     return create(dllInterface, null);
/*    */   }
/*    */ 
/*    */   public static <T> T create(Class<T> dllInterface, File library)
/*    */   {
/* 47 */     if (!dllInterface.isInterface()) {
/* 48 */       throw new NLinkException(dllInterface + " is not an interface");
/*    */     }
/* 50 */     DllClass dc = (DllClass)dllInterface.getAnnotation(DllClass.class);
/* 51 */     if (dc == null)
/* 52 */       throw new NLinkException(dllInterface + " does not have @DllClass annotation");
/*    */     String libPath;
/*    */     String libPath;
/* 56 */     if (library != null) {
/* 57 */       libPath = library.toString();
/*    */     } else {
/* 59 */       libPath = dc.value();
/* 60 */       if (libPath.equals(""))
/*    */       {
/* 62 */         libPath = dllInterface.getSimpleName();
/*    */       }
/*    */     }
/*    */ 
/* 66 */     return dllInterface.cast(Proxy.newProxyInstance(dllInterface.getClassLoader(), new Class[] { dllInterface }, new Wrapper(dllInterface, libPath)));
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Connector-1.1.49.jar
 * Qualified Name:     com.dukascopy.api.NLink
 * JD-Core Version:    0.6.0
 */