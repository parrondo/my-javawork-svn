/*    */ package com.dukascopy.api.nlink.win32;
/*    */ 
/*    */ import com.dukascopy.api.nlink.win32.engine.MethodInfo;
/*    */ import java.lang.reflect.Method;
/*    */ 
/*    */ public final class Callback
/*    */ {
/*    */   public long fp;
/*    */   MethodInfo mi;
/*    */   Class clazz;
/*    */   String methodname;
/*    */ 
/*    */   public Callback(Class clazz, String methodname, CallingConvention cc)
/*    */   {
/* 16 */     Method[] methods = clazz.getMethods();
/* 17 */     Method method = null;
/* 18 */     for (Method m : methods) {
/* 19 */       if (!m.getName().equals(methodname))
/*    */         continue;
/* 21 */       method = m;
/* 22 */       break;
/*    */     }
/* 24 */     if (method == null) {
/* 25 */       throw new RuntimeException("Method by name " + methodname + " not found in " + clazz.getName());
/*    */     }
/*    */ 
/* 28 */     if ((method.getModifiers() & 0x8) == 0) {
/* 29 */       throw new RuntimeException("Method " + methodname + " in " + clazz.getName() + " is not a static method. Callbacks specified using this method are required to be static.");
/*    */     }
/*    */ 
/* 36 */     MethodInfo mi = JInvoke.getMethodInfo(method);
/* 37 */     this.fp = CreateCallbackThunk(clazz, null, methodname, mi.signature, cc.getIntCode(), mi.nargsize, mi.argtypes);
/*    */   }
/*    */ 
/*    */   public Callback(Class clazz, String methodname)
/*    */   {
/* 42 */     this(clazz, methodname, CallingConvention.STDCALL);
/*    */   }
/*    */ 
/*    */   public Callback(Object callbackobj, String methodname, CallingConvention cc) {
/* 46 */     Class clazz = callbackobj.getClass();
/* 47 */     Method[] methods = clazz.getMethods();
/* 48 */     Method method = null;
/* 49 */     for (Method m : methods) {
/* 50 */       if (!m.getName().equals(methodname))
/*    */         continue;
/* 52 */       method = m;
/* 53 */       break;
/*    */     }
/* 55 */     if (method == null) {
/* 56 */       throw new RuntimeException("Method by name " + methodname + " not found in " + clazz.getName());
/*    */     }
/*    */ 
/* 59 */     MethodInfo mi = JInvoke.getMethodInfo(method);
/* 60 */     if ((method.getModifiers() & 0x8) != 0) {
/* 61 */       this.fp = CreateCallbackThunk(clazz, null, methodname, mi.signature, cc.getIntCode(), mi.nargsize, mi.argtypes);
/*    */     }
/*    */     else
/* 64 */       this.fp = CreateCallbackThunk(clazz, callbackobj, methodname, mi.signature, cc.getIntCode(), mi.nargsize, mi.argtypes);
/*    */   }
/*    */ 
/*    */   public Callback(Object callbackobj, String methodname)
/*    */   {
/* 69 */     this(callbackobj, methodname, CallingConvention.STDCALL);
/*    */   }
/*    */ 
/*    */   protected void finalize()
/*    */     throws Throwable
/*    */   {
/*    */   }
/*    */ 
/*    */   private static synchronized native void ReleaseCallbackThunk(Class paramClass, String paramString1, String paramString2, int paramInt1, int paramInt2, int[] paramArrayOfInt);
/*    */ 
/*    */   private static synchronized native long CreateCallbackThunk(Class paramClass, Object paramObject, String paramString1, String paramString2, int paramInt1, int paramInt2, int[] paramArrayOfInt);
/*    */ 
/*    */   static
/*    */   {
/* 12 */     JInvoke.loadNativeLib();
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Connector-1.1.49.jar
 * Qualified Name:     com.dukascopy.api.nlink.win32.Callback
 * JD-Core Version:    0.6.0
 */