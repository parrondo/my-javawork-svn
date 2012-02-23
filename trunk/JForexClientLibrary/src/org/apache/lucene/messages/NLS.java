/*     */ package org.apache.lucene.messages;
/*     */ 
/*     */ import java.io.PrintStream;
/*     */ import java.lang.reflect.Field;
/*     */ import java.security.AccessController;
/*     */ import java.security.PrivilegedAction;
/*     */ import java.text.MessageFormat;
/*     */ import java.util.HashMap;
/*     */ import java.util.Iterator;
/*     */ import java.util.Locale;
/*     */ import java.util.Map;
/*     */ import java.util.MissingResourceException;
/*     */ import java.util.ResourceBundle;
/*     */ import java.util.Set;
/*     */ 
/*     */ public class NLS
/*     */ {
/*  48 */   private static Map<String, Class<? extends NLS>> bundles = new HashMap(0);
/*     */ 
/*     */   public static String getLocalizedMessage(String key)
/*     */   {
/*  56 */     return getLocalizedMessage(key, Locale.getDefault());
/*     */   }
/*     */ 
/*     */   public static String getLocalizedMessage(String key, Locale locale) {
/*  60 */     Object message = getResourceBundleObject(key, locale);
/*  61 */     if (message == null) {
/*  62 */       return "Message with key:" + key + " and locale: " + locale + " not found.";
/*     */     }
/*     */ 
/*  65 */     return message.toString();
/*     */   }
/*     */ 
/*     */   public static String getLocalizedMessage(String key, Locale locale, Object[] args)
/*     */   {
/*  70 */     String str = getLocalizedMessage(key, locale);
/*     */ 
/*  72 */     if (args.length > 0) {
/*  73 */       str = MessageFormat.format(str, args);
/*     */     }
/*     */ 
/*  76 */     return str;
/*     */   }
/*     */ 
/*     */   public static String getLocalizedMessage(String key, Object[] args) {
/*  80 */     return getLocalizedMessage(key, Locale.getDefault(), args);
/*     */   }
/*     */ 
/*     */   protected static void initializeMessages(String bundleName, Class<? extends NLS> clazz)
/*     */   {
/*     */     try
/*     */     {
/*  94 */       load(clazz);
/*  95 */       if (!bundles.containsKey(bundleName))
/*  96 */         bundles.put(bundleName, clazz);
/*     */     }
/*     */     catch (Throwable e)
/*     */     {
/*     */     }
/*     */   }
/*     */ 
/*     */   private static Object getResourceBundleObject(String messageKey, Locale locale)
/*     */   {
/* 107 */     for (Iterator it = bundles.keySet().iterator(); it.hasNext(); ) {
/* 108 */       Class clazz = (Class)bundles.get(it.next());
/* 109 */       ResourceBundle resourceBundle = ResourceBundle.getBundle(clazz.getName(), locale);
/*     */ 
/* 111 */       if (resourceBundle != null) {
/*     */         try {
/* 113 */           Object obj = resourceBundle.getObject(messageKey);
/* 114 */           if (obj != null)
/* 115 */             return obj;
/*     */         }
/*     */         catch (MissingResourceException e)
/*     */         {
/*     */         }
/*     */       }
/*     */     }
/* 122 */     return null;
/*     */   }
/*     */ 
/*     */   private static void load(Class<? extends NLS> clazz)
/*     */   {
/* 129 */     Field[] fieldArray = clazz.getDeclaredFields();
/*     */ 
/* 131 */     boolean isFieldAccessible = (clazz.getModifiers() & 0x1) != 0;
/*     */ 
/* 134 */     int len = fieldArray.length;
/* 135 */     Map fields = new HashMap(len * 2);
/* 136 */     for (int i = 0; i < len; i++) {
/* 137 */       fields.put(fieldArray[i].getName(), fieldArray[i]);
/* 138 */       loadfieldValue(fieldArray[i], isFieldAccessible, clazz);
/*     */     }
/*     */   }
/*     */ 
/*     */   private static void loadfieldValue(Field field, boolean isFieldAccessible, Class<? extends NLS> clazz)
/*     */   {
/* 148 */     int MOD_EXPECTED = 9;
/* 149 */     int MOD_MASK = MOD_EXPECTED | 0x10;
/* 150 */     if ((field.getModifiers() & MOD_MASK) != MOD_EXPECTED) {
/* 151 */       return;
/*     */     }
/*     */ 
/* 154 */     if (!isFieldAccessible)
/* 155 */       makeAccessible(field);
/*     */     try {
/* 157 */       field.set(null, field.getName());
/* 158 */       validateMessage(field.getName(), clazz);
/*     */     }
/*     */     catch (IllegalArgumentException e)
/*     */     {
/*     */     }
/*     */     catch (IllegalAccessException e)
/*     */     {
/*     */     }
/*     */   }
/*     */ 
/*     */   private static void validateMessage(String key, Class<? extends NLS> clazz)
/*     */   {
/*     */     try
/*     */     {
/* 173 */       ResourceBundle resourceBundle = ResourceBundle.getBundle(clazz.getName(), Locale.getDefault());
/*     */ 
/* 175 */       if (resourceBundle != null) {
/* 176 */         Object obj = resourceBundle.getObject(key);
/* 177 */         if (obj == null)
/* 178 */           System.err.println("WARN: Message with key:" + key + " and locale: " + Locale.getDefault() + " not found.");
/*     */       }
/*     */     }
/*     */     catch (MissingResourceException e) {
/* 182 */       System.err.println("WARN: Message with key:" + key + " and locale: " + Locale.getDefault() + " not found.");
/*     */     }
/*     */     catch (Throwable e)
/*     */     {
/*     */     }
/*     */   }
/*     */ 
/*     */   private static void makeAccessible(Field field)
/*     */   {
/* 195 */     if (System.getSecurityManager() == null)
/* 196 */       field.setAccessible(true);
/*     */     else
/* 198 */       AccessController.doPrivileged(new PrivilegedAction(field) {
/*     */         public Void run() {
/* 200 */           this.val$field.setAccessible(true);
/* 201 */           return null;
/*     */         }
/*     */       });
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.messages.NLS
 * JD-Core Version:    0.6.0
 */