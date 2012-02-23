/*     */ package org.eclipse.jdt.internal.compiler.util;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.lang.reflect.Field;
/*     */ import java.text.MessageFormat;
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashMap;
/*     */ import java.util.Locale;
/*     */ import java.util.Map;
/*     */ import java.util.Properties;
/*     */ 
/*     */ public final class Messages
/*     */ {
/*     */   private static String[] nlSuffixes;
/*     */   private static final String EXTENSION = ".properties";
/*     */   private static final String BUNDLE_NAME = "org.eclipse.jdt.internal.compiler.messages";
/*     */   public static String compilation_unresolvedProblem;
/*     */   public static String compilation_unresolvedProblems;
/*     */   public static String compilation_request;
/*     */   public static String compilation_loadBinary;
/*     */   public static String compilation_process;
/*     */   public static String compilation_write;
/*     */   public static String compilation_done;
/*     */   public static String compilation_units;
/*     */   public static String compilation_unit;
/*     */   public static String compilation_internalError;
/*     */   public static String compilation_beginningToCompile;
/*     */   public static String compilation_processing;
/*     */   public static String output_isFile;
/*     */   public static String output_notValidAll;
/*     */   public static String output_notValid;
/*     */   public static String problem_noSourceInformation;
/*     */   public static String problem_atLine;
/*     */   public static String abort_invalidAttribute;
/*     */   public static String abort_invalidExceptionAttribute;
/*     */   public static String abort_invalidOpcode;
/*     */   public static String abort_missingCode;
/*     */   public static String abort_againstSourceModel;
/*     */   public static String accept_cannot;
/*     */   public static String parser_incorrectPath;
/*     */   public static String parser_moveFiles;
/*     */   public static String parser_syntaxRecovery;
/*     */   public static String parser_regularParse;
/*     */   public static String parser_missingFile;
/*     */   public static String parser_corruptedFile;
/*     */   public static String parser_endOfFile;
/*     */   public static String parser_endOfConstructor;
/*     */   public static String parser_endOfMethod;
/*     */   public static String parser_endOfInitializer;
/*     */   public static String ast_missingCode;
/*     */   public static String constant_cannotCastedInto;
/*     */   public static String constant_cannotConvertedTo;
/*     */ 
/*     */   static
/*     */   {
/* 118 */     initializeMessages("org.eclipse.jdt.internal.compiler.messages", Messages.class);
/*     */   }
/*     */ 
/*     */   public static String bind(String message)
/*     */   {
/* 128 */     return bind(message, null);
/*     */   }
/*     */ 
/*     */   public static String bind(String message, Object binding)
/*     */   {
/* 139 */     return bind(message, new Object[] { binding });
/*     */   }
/*     */ 
/*     */   public static String bind(String message, Object binding1, Object binding2)
/*     */   {
/* 151 */     return bind(message, new Object[] { binding1, binding2 });
/*     */   }
/*     */ 
/*     */   public static String bind(String message, Object[] bindings)
/*     */   {
/* 162 */     return MessageFormat.format(message, bindings);
/*     */   }
/*     */ 
/*     */   private static String[] buildVariants(String root)
/*     */   {
/* 169 */     if (nlSuffixes == null)
/*     */     {
/* 171 */       String nl = Locale.getDefault().toString();
/* 172 */       ArrayList result = new ArrayList(4);
/*     */       while (true)
/*     */       {
/* 175 */         result.add('_' + nl + ".properties");
/* 176 */         int lastSeparator = nl.lastIndexOf('_');
/* 177 */         if (lastSeparator == -1)
/*     */           break;
/* 179 */         nl = nl.substring(0, lastSeparator);
/*     */       }
/*     */       int lastSeparator;
/* 182 */       result.add(".properties");
/* 183 */       nlSuffixes = (String[])result.toArray(new String[result.size()]);
/*     */     }
/* 185 */     root = root.replace('.', '/');
/* 186 */     String[] variants = new String[nlSuffixes.length];
/* 187 */     for (int i = 0; i < variants.length; i++)
/* 188 */       variants[i] = (root + nlSuffixes[i]);
/* 189 */     return variants;
/*     */   }
/*     */ 
/*     */   public static void initializeMessages(String bundleName, Class clazz) {
/* 193 */     Field[] fields = clazz.getDeclaredFields();
/* 194 */     load(bundleName, clazz.getClassLoader(), fields);
/*     */ 
/* 199 */     int numFields = fields.length;
/* 200 */     for (int i = 0; i < numFields; i++) {
/* 201 */       Field field = fields[i];
/* 202 */       if ((field.getModifiers() & 0x19) != 9)
/*     */       {
/*     */         continue;
/*     */       }
/*     */ 
/*     */       try
/*     */       {
/* 209 */         if (field.get(clazz) == null) {
/* 210 */           String value = "Missing message: " + field.getName() + " in: " + bundleName;
/* 211 */           field.set(null, value);
/*     */         }
/*     */       }
/*     */       catch (IllegalArgumentException localIllegalArgumentException)
/*     */       {
/*     */       }
/*     */       catch (IllegalAccessException localIllegalAccessException)
/*     */       {
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public static void load(String bundleName, ClassLoader loader, Field[] fields) {
/* 224 */     String[] variants = buildVariants(bundleName);
/*     */ 
/* 226 */     int i = variants.length;
/*     */     do { InputStream input = loader == null ? 
/* 228 */         ClassLoader.getSystemResourceAsStream(variants[i]) : 
/* 229 */         loader.getResourceAsStream(variants[i]);
/* 230 */       if (input != null)
/*     */         try {
/* 232 */           MessagesProperties properties = new MessagesProperties(fields, bundleName);
/* 233 */           properties.load(input);
/*     */         }
/*     */         catch (IOException localIOException1)
/*     */         {
/*     */           try {
/* 238 */             input.close(); } catch (IOException localIOException2) {
/*     */           } } finally { try { input.close();
/*     */           }
/*     */           catch (IOException localIOException3)
/*     */           {
/*     */           }
/*     */         }
/* 226 */       i--; } while (i >= 0);
/*     */   }
/*     */ 
/*     */   private static class MessagesProperties extends Properties
/*     */   {
/*     */     private static final int MOD_EXPECTED = 9;
/*     */     private static final int MOD_MASK = 25;
/*     */     private static final long serialVersionUID = 1L;
/*     */     private final Map fields;
/*     */ 
/*     */     public MessagesProperties(Field[] fieldArray, String bundleName)
/*     */     {
/*  35 */       int len = fieldArray.length;
/*  36 */       this.fields = new HashMap(len * 2);
/*  37 */       for (int i = 0; i < len; i++)
/*  38 */         this.fields.put(fieldArray[i].getName(), fieldArray[i]);
/*     */     }
/*     */ 
/*     */     public synchronized Object put(Object key, Object value)
/*     */     {
/*     */       try
/*     */       {
/*  47 */         Field field = (Field)this.fields.get(key);
/*  48 */         if (field == null) {
/*  49 */           return null;
/*     */         }
/*     */ 
/*  52 */         if ((field.getModifiers() & 0x19) != 9) {
/*  53 */           return null;
/*     */         }
/*     */ 
/*     */         try
/*     */         {
/*  59 */           field.set(null, value);
/*     */         }
/*     */         catch (Exception localException) {
/*     */         }
/*     */       }
/*     */       catch (SecurityException localSecurityException) {
/*     */       }
/*  66 */       return null;
/*     */     }
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\ecj-3.5.2.jar
 * Qualified Name:     org.eclipse.jdt.internal.compiler.util.Messages
 * JD-Core Version:    0.6.0
 */