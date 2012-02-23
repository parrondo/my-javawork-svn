/*    */ package org.eclipse.jdt.internal.antadapter;
/*    */ 
/*    */ import java.io.PrintStream;
/*    */ import java.text.MessageFormat;
/*    */ import java.util.Locale;
/*    */ import java.util.MissingResourceException;
/*    */ import java.util.ResourceBundle;
/*    */ 
/*    */ public class AntAdapterMessages
/*    */ {
/*    */   private static final String BUNDLE_NAME = "org.eclipse.jdt.internal.antadapter.messages";
/*    */   private static ResourceBundle RESOURCE_BUNDLE;
/*    */ 
/*    */   static
/*    */   {
/*    */     try
/*    */     {
/* 26 */       RESOURCE_BUNDLE = ResourceBundle.getBundle("org.eclipse.jdt.internal.antadapter.messages", Locale.getDefault());
/*    */     } catch (MissingResourceException e) {
/* 28 */       System.out.println("Missing resource : " + "org.eclipse.jdt.internal.antadapter.messages".replace('.', '/') + ".properties for locale " + Locale.getDefault());
/* 29 */       throw e;
/*    */     }
/*    */   }
/*    */ 
/*    */   public static String getString(String key)
/*    */   {
/*    */     try
/*    */     {
/* 39 */       return RESOURCE_BUNDLE.getString(key); } catch (MissingResourceException localMissingResourceException) {
/*    */     }
/* 41 */     return '!' + key + '!';
/*    */   }
/*    */ 
/*    */   public static String getString(String key, String argument)
/*    */   {
/*    */     try {
/* 47 */       String message = RESOURCE_BUNDLE.getString(key);
/* 48 */       MessageFormat messageFormat = new MessageFormat(message);
/* 49 */       return messageFormat.format(new String[] { argument }); } catch (MissingResourceException localMissingResourceException) {
/*    */     }
/* 51 */     return '!' + key + '!';
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\ecj-3.5.2.jar
 * Qualified Name:     org.eclipse.jdt.internal.antadapter.AntAdapterMessages
 * JD-Core Version:    0.6.0
 */