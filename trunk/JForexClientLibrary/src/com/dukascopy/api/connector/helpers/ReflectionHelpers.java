/*    */ package com.dukascopy.api.connector.helpers;
/*    */ 
/*    */ import java.lang.reflect.Field;
/*    */ import java.lang.reflect.Method;
/*    */ 
/*    */ public class ReflectionHelpers
/*    */ {
/*    */   public static Field getVariableField(Object owner, Object variable)
/*    */   {
/* 10 */     Field result = null;
/* 11 */     Field[] fields = owner.getClass().getFields();
/* 12 */     for (Field field : fields) {
/*    */       try {
/* 14 */         Object fieldValue = field.get(owner);
/* 15 */         if (fieldValue == variable) {
/* 16 */           result = field;
/* 17 */           break;
/*    */         }
/*    */       }
/*    */       catch (IllegalArgumentException e) {
/* 21 */         e.printStackTrace();
/*    */       }
/*    */       catch (IllegalAccessException e) {
/* 24 */         e.printStackTrace();
/*    */       }
/*    */ 
/*    */     }
/*    */ 
/* 41 */     return result;
/*    */   }
/*    */ 
/*    */   public static Method getAbstractConnectorMethod(String name)
/*    */   {
/* 46 */     Object abstractConnector = null;
/*    */     try {
/* 48 */       abstractConnector = Class.forName("com.dukascopy.api.connector.AbstractConnectorImpl");
/*    */     }
/*    */     catch (ClassNotFoundException e1)
/*    */     {
/* 52 */       e1.printStackTrace();
/*    */     }
/* 54 */     Method result = null;
/* 55 */     Method[] methods = null;
/* 56 */     if (abstractConnector != null) {
/* 57 */       methods = abstractConnector.getClass().getMethods();
/*    */     }
/* 59 */     if (methods != null) {
/* 60 */       for (Method method : methods) {
/*    */         try {
/* 62 */           if (method.getName().equals(name)) {
/* 63 */             result = method;
/* 64 */             break;
/*    */           }
/*    */         }
/*    */         catch (IllegalArgumentException e) {
/* 68 */           e.printStackTrace();
/*    */         }
/*    */       }
/*    */     }
/* 72 */     return result;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Connector-1.1.49.jar
 * Qualified Name:     com.dukascopy.api.connector.helpers.ReflectionHelpers
 * JD-Core Version:    0.6.0
 */