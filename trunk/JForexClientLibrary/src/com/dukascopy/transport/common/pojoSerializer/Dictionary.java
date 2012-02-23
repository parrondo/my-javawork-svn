/*    */ package com.dukascopy.transport.common.pojoSerializer;
/*    */ 
/*    */ import java.math.BigDecimal;
/*    */ 
/*    */ public class Dictionary
/*    */ {
/*    */   protected static String encodeName(Class objectClass)
/*    */   {
/* 20 */     String name = objectClass.getCanonicalName();
/* 21 */     if (objectClass == BigDecimal.class)
/* 22 */       name = "_dc";
/* 23 */     else if ((objectClass == Long.class) || (objectClass == Long.TYPE))
/* 24 */       name = "_l";
/* 25 */     else if ((objectClass == Float.class) || (objectClass == Float.TYPE))
/* 26 */       name = "_f";
/* 27 */     else if ((objectClass == Integer.class) || (objectClass == Integer.TYPE))
/* 28 */       name = "_i";
/* 29 */     else if ((objectClass == Boolean.class) || (objectClass == Boolean.TYPE))
/* 30 */       name = "_b";
/* 31 */     else if ((objectClass == Character.class) || (objectClass == Character.TYPE))
/* 32 */       name = "_ch";
/* 33 */     else if ((objectClass == Double.class) || (objectClass == Double.TYPE))
/* 34 */       name = "_d";
/* 35 */     else if (objectClass == String.class)
/* 36 */       name = "_s";
/* 37 */     else if ((objectClass == Byte.class) || (objectClass == Byte.TYPE))
/* 38 */       name = "_bt";
/* 39 */     else if (objectClass.isArray()) {
/* 40 */       name = "_a";
/*    */     }
/* 42 */     return name;
/*    */   }
/*    */ 
/*    */   protected static Class decodeName(String objectClass)
/*    */   {
/* 50 */     String className = objectClass;
/*    */     try {
/* 52 */       if (className.equals("_dc"))
/* 53 */         className = BigDecimal.class.getCanonicalName();
/* 54 */       else if (className.equals("_l"))
/* 55 */         className = Long.class.getCanonicalName();
/* 56 */       else if (className.equals("_f"))
/* 57 */         className = Float.class.getCanonicalName();
/* 58 */       else if (className.equals("_i"))
/* 59 */         className = Integer.class.getCanonicalName();
/* 60 */       else if (className.equals("_b"))
/* 61 */         className = Boolean.class.getCanonicalName();
/* 62 */       else if (className.equals("_ch"))
/* 63 */         className = Character.class.getCanonicalName();
/* 64 */       else if (className.equals("_d"))
/* 65 */         className = Double.class.getCanonicalName();
/* 66 */       else if (className.equals("_s"))
/* 67 */         className = String.class.getCanonicalName();
/* 68 */       else if (className.equals("_bt"))
/* 69 */         className = Byte.class.getCanonicalName();
/* 70 */       else if (className.equals("_a")) {
/* 71 */         return [Ljava.lang.Object.class;
/*    */       }
/* 73 */       return Class.forName(className);
/*    */     }
/*    */     catch (ClassNotFoundException e) {
/* 76 */       e.printStackTrace();
/*    */     }
/* 78 */     return null;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\dds2-common-2.3.77.jar
 * Qualified Name:     com.dukascopy.transport.common.pojoSerializer.Dictionary
 * JD-Core Version:    0.6.0
 */