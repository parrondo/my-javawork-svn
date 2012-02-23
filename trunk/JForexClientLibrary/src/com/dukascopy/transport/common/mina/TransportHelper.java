/*    */ package com.dukascopy.transport.common.mina;
/*    */ 
/*    */ import java.lang.reflect.InvocationTargetException;
/*    */ import java.lang.reflect.Method;
/*    */ import java.util.ArrayList;
/*    */ import java.util.List;
/*    */ 
/*    */ public class TransportHelper
/*    */ {
/*    */   public static Object invokeRemoteRequest(InvocationRequest request, Object target)
/*    */     throws Exception
/*    */   {
/* 11 */     Object res = null;
/* 12 */     Object interfaceImpl = target;
/* 13 */     Class[] paramClasses = new Class[0];
/* 14 */     if (request.getParams() != null) {
/* 15 */       paramClasses = new Class[request.getParams().length];
/* 16 */       for (int i = 0; i < request.getParams().length; i++)
/* 17 */         if (request.getParams()[i] != null)
/*    */         {
/* 19 */           paramClasses[i] = request.getParams()[i].getClass();
/*    */         }
/* 21 */         else paramClasses[i] = null;
/*    */     }
/*    */     Class ss;
/* 26 */     for (ss : paramClasses);
/* 29 */     List equalsMethods = new ArrayList();
/* 30 */     Method m = null;
/*    */     try {
/* 32 */       Method[] methods = interfaceImpl.getClass().getMethods();
/* 33 */       for (Method method : methods) {
/* 34 */         if ((!method.getName().equals(request.getMethodName())) || (method.getParameterTypes().length != paramClasses.length))
/*    */           continue;
/* 36 */         equalsMethods.add(method);
/*    */       }
/*    */ 
/* 39 */       if ((equalsMethods.size() > 0) && (equalsMethods.size() < 2))
/* 40 */         m = (Method)equalsMethods.get(0);
/*    */       else {
/* 42 */         for (Method method : equalsMethods) {
/* 43 */           Class[] params = method.getParameterTypes();
/* 44 */           boolean noConfilct = true;
/* 45 */           for (int i = 0; i < params.length; i++) {
/* 46 */             if (!params[i].isAssignableFrom(paramClasses[i])) {
/* 47 */               noConfilct = false;
/* 48 */               break;
/*    */             }
/*    */           }
/* 51 */           if (noConfilct) {
/* 52 */             m = method;
/* 53 */             break;
/*    */           }
/*    */         }
/*    */       }
/* 57 */       if (m == null) {
/* 58 */         throw new NoSuchMethodException(request.getMethodName());
/*    */       }
/* 60 */       res = m.invoke(interfaceImpl, request.getParams());
/*    */     } catch (SecurityException e) {
/* 62 */       throw new Exception("Security exception:" + e.getMessage());
/*    */     } catch (NoSuchMethodException e) {
/* 64 */       throw new Exception("NoSuchMethodException: " + e.getMessage());
/*    */     } catch (IllegalArgumentException e) {
/* 66 */       throw new Exception("Illegal argument exception: " + e.getMessage());
/*    */     } catch (IllegalAccessException e) {
/* 68 */       throw new Exception("IllegalAccess exception: " + e.getMessage());
/*    */     } catch (InvocationTargetException e) {
/* 70 */       throw new Exception("Invocation target exception: " + e.getMessage());
/*    */     }
/*    */ 
/* 74 */     return res;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\dds2-common-2.3.77.jar
 * Qualified Name:     com.dukascopy.transport.common.mina.TransportHelper
 * JD-Core Version:    0.6.0
 */