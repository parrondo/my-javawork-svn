/*    */ package org.apache.lucene.util;
/*    */ 
/*    */ import org.apache.lucene.LucenePackage;
/*    */ 
/*    */ public final class Constants
/*    */ {
/* 30 */   public static final String JAVA_VERSION = System.getProperty("java.version");
/*    */ 
/* 32 */   public static final boolean JAVA_1_1 = JAVA_VERSION.startsWith("1.1.");
/*    */ 
/* 34 */   public static final boolean JAVA_1_2 = JAVA_VERSION.startsWith("1.2.");
/*    */ 
/* 36 */   public static final boolean JAVA_1_3 = JAVA_VERSION.startsWith("1.3.");
/*    */ 
/* 39 */   public static final String OS_NAME = System.getProperty("os.name");
/*    */ 
/* 41 */   public static final boolean LINUX = OS_NAME.startsWith("Linux");
/*    */ 
/* 43 */   public static final boolean WINDOWS = OS_NAME.startsWith("Windows");
/*    */ 
/* 45 */   public static final boolean SUN_OS = OS_NAME.startsWith("SunOS");
/*    */ 
/* 47 */   public static final boolean MAC_OS_X = OS_NAME.startsWith("Mac OS X");
/*    */ 
/* 49 */   public static final String OS_ARCH = System.getProperty("os.arch");
/* 50 */   public static final String OS_VERSION = System.getProperty("os.version");
/* 51 */   public static final String JAVA_VENDOR = System.getProperty("java.vendor");
/*    */   public static final boolean JRE_IS_64BIT;
/*    */   public static final String LUCENE_MAIN_VERSION;
/*    */   public static final String LUCENE_VERSION;
/*    */ 
/*    */   private static String ident(String s)
/*    */   {
/* 72 */     return s.toString();
/*    */   }
/*    */ 
/*    */   static
/*    */   {
/* 57 */     String x = System.getProperty("sun.arch.data.model");
/* 58 */     if (x != null) {
/* 59 */       JRE_IS_64BIT = x.indexOf("64") != -1;
/*    */     }
/* 61 */     else if ((OS_ARCH != null) && (OS_ARCH.indexOf("64") != -1))
/* 62 */       JRE_IS_64BIT = true;
/*    */     else {
/* 64 */       JRE_IS_64BIT = false;
/*    */     }
/*    */ 
/* 78 */     LUCENE_MAIN_VERSION = ident("3.4");
/*    */ 
/* 82 */     Package pkg = LucenePackage.get();
/* 83 */     String v = pkg == null ? null : pkg.getImplementationVersion();
/* 84 */     if (v == null)
/* 85 */       v = LUCENE_MAIN_VERSION + "-SNAPSHOT";
/* 86 */     else if (!v.startsWith(LUCENE_MAIN_VERSION)) {
/* 87 */       v = LUCENE_MAIN_VERSION + "-SNAPSHOT " + v;
/*    */     }
/* 89 */     LUCENE_VERSION = ident(v);
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.util.Constants
 * JD-Core Version:    0.6.0
 */