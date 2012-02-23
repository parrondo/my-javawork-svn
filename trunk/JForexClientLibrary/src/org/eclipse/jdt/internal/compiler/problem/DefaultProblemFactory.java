/*     */ package org.eclipse.jdt.internal.compiler.problem;
/*     */ 
/*     */ import java.io.PrintStream;
/*     */ import java.util.Enumeration;
/*     */ import java.util.Locale;
/*     */ import java.util.MissingResourceException;
/*     */ import java.util.ResourceBundle;
/*     */ import org.eclipse.jdt.core.compiler.CategorizedProblem;
/*     */ import org.eclipse.jdt.core.compiler.CharOperation;
/*     */ import org.eclipse.jdt.internal.compiler.IProblemFactory;
/*     */ import org.eclipse.jdt.internal.compiler.util.HashtableOfInt;
/*     */ import org.eclipse.jdt.internal.compiler.util.Util;
/*     */ 
/*     */ public class DefaultProblemFactory
/*     */   implements IProblemFactory
/*     */ {
/*     */   public HashtableOfInt messageTemplates;
/*     */   private Locale locale;
/*     */   private static HashtableOfInt DEFAULT_LOCALE_TEMPLATES;
/*  29 */   private static final char[] DOUBLE_QUOTES = "''".toCharArray();
/*  30 */   private static final char[] SINGLE_QUOTE = "'".toCharArray();
/*  31 */   private static final char[] FIRST_ARGUMENT = "{0}".toCharArray();
/*     */ 
/*     */   public DefaultProblemFactory() {
/*  34 */     this(Locale.getDefault());
/*     */   }
/*     */ 
/*     */   public DefaultProblemFactory(Locale loc)
/*     */   {
/*  40 */     setLocale(loc);
/*     */   }
/*     */ 
/*     */   public CategorizedProblem createProblem(char[] originatingFileName, int problemId, String[] problemArguments, String[] messageArguments, int severity, int startPosition, int endPosition, int lineNumber, int columnNumber)
/*     */   {
/*  75 */     return new DefaultProblem(
/*  76 */       originatingFileName, 
/*  77 */       getLocalizedMessage(problemId, messageArguments), 
/*  78 */       problemId, 
/*  79 */       problemArguments, 
/*  80 */       severity, 
/*  81 */       startPosition, 
/*  82 */       endPosition, 
/*  83 */       lineNumber, 
/*  84 */       columnNumber);
/*     */   }
/*     */ 
/*     */   public CategorizedProblem createProblem(char[] originatingFileName, int problemId, String[] problemArguments, int elaborationId, String[] messageArguments, int severity, int startPosition, int endPosition, int lineNumber, int columnNumber)
/*     */   {
/*  97 */     return new DefaultProblem(
/*  98 */       originatingFileName, 
/*  99 */       getLocalizedMessage(problemId, elaborationId, messageArguments), 
/* 100 */       problemId, 
/* 101 */       problemArguments, 
/* 102 */       severity, 
/* 103 */       startPosition, 
/* 104 */       endPosition, 
/* 105 */       lineNumber, 
/* 106 */       columnNumber);
/*     */   }
/*     */   private static final int keyFromID(int id) {
/* 109 */     return id + 1;
/*     */   }
/*     */ 
/*     */   public Locale getLocale()
/*     */   {
/* 116 */     return this.locale;
/*     */   }
/*     */   public void setLocale(Locale locale) {
/* 119 */     if (locale == this.locale) return;
/* 120 */     this.locale = locale;
/* 121 */     if (Locale.getDefault().equals(locale)) {
/* 122 */       if (DEFAULT_LOCALE_TEMPLATES == null) {
/* 123 */         DEFAULT_LOCALE_TEMPLATES = loadMessageTemplates(locale);
/*     */       }
/* 125 */       this.messageTemplates = DEFAULT_LOCALE_TEMPLATES;
/*     */     } else {
/* 127 */       this.messageTemplates = loadMessageTemplates(locale);
/*     */     }
/*     */   }
/*     */ 
/*     */   public final String getLocalizedMessage(int id, String[] problemArguments) {
/* 131 */     return getLocalizedMessage(id, 0, problemArguments);
/*     */   }
/*     */   public final String getLocalizedMessage(int id, int elaborationId, String[] problemArguments) {
/* 134 */     String rawMessage = (String)this.messageTemplates.get(keyFromID(id & 0xFFFFFF));
/* 135 */     if (rawMessage == null) {
/* 136 */       return "Unable to retrieve the error message for problem id: " + (
/* 137 */         id & 0xFFFFFF) + ". Check compiler resources.";
/*     */     }
/* 139 */     char[] message = rawMessage.toCharArray();
/* 140 */     if (elaborationId != 0) {
/* 141 */       String elaboration = (String)this.messageTemplates.get(keyFromID(elaborationId));
/* 142 */       if (elaboration == null) {
/* 143 */         return "Unable to retrieve the error message elaboration for elaboration id: " + 
/* 144 */           elaborationId + ". Check compiler resources.";
/*     */       }
/* 146 */       message = CharOperation.replace(message, FIRST_ARGUMENT, elaboration.toCharArray());
/*     */     }
/*     */ 
/* 150 */     message = CharOperation.replace(message, DOUBLE_QUOTES, SINGLE_QUOTE);
/*     */ 
/* 152 */     if (problemArguments == null) {
/* 153 */       return new String(message);
/*     */     }
/*     */ 
/* 156 */     int length = message.length;
/* 157 */     int start = 0;
/* 158 */     int end = length;
/* 159 */     StringBuffer output = null;
/* 160 */     if ((id & 0x80000000) != 0) {
/* 161 */       output = new StringBuffer(10 + length + problemArguments.length * 20);
/* 162 */       output.append((String)this.messageTemplates.get(keyFromID(514)));
/*     */     }
/*     */ 
/* 165 */     while ((end = CharOperation.indexOf('{', message, start)) > -1) {
/* 166 */       if (output == null) output = new StringBuffer(length + problemArguments.length * 20);
/* 167 */       output.append(message, start, end - start);
/* 168 */       if ((start = CharOperation.indexOf('}', message, end + 1)) > -1) {
/*     */         try {
/* 170 */           output.append(problemArguments[CharOperation.parseInt(message, end + 1, start - end - 1)]);
/*     */         } catch (NumberFormatException localNumberFormatException) {
/* 172 */           output.append(message, end + 1, start - end);
/*     */         } catch (ArrayIndexOutOfBoundsException localArrayIndexOutOfBoundsException) {
/* 174 */           return "Cannot bind message for problem (id: " + (
/* 175 */             id & 0xFFFFFF) + 
/* 176 */             ") \"" + 
/* 177 */             new String(message) + 
/* 178 */             "\" with arguments: {" + 
/* 179 */             Util.toString(problemArguments) + 
/* 180 */             "}";
/*     */         }
/* 182 */         start++; continue;
/*     */       }
/* 184 */       output.append(message, end, length);
/* 185 */       break label434;
/*     */     }
/*     */ 
/* 188 */     if (output == null) {
/* 189 */       return new String(message);
/*     */     }
/* 191 */     output.append(message, start, length - start);
/*     */ 
/* 197 */     label434: return new String(output.toString());
/*     */   }
/*     */ 
/*     */   public final String localizedMessage(CategorizedProblem problem)
/*     */   {
/* 204 */     return getLocalizedMessage(problem.getID(), problem.getArguments());
/*     */   }
/*     */ 
/*     */   public static HashtableOfInt loadMessageTemplates(Locale loc)
/*     */   {
/* 214 */     ResourceBundle bundle = null;
/* 215 */     String bundleName = "org.eclipse.jdt.internal.compiler.problem.messages";
/*     */     try {
/* 217 */       bundle = ResourceBundle.getBundle(bundleName, loc);
/*     */     } catch (MissingResourceException e) {
/* 219 */       System.out.println("Missing resource : " + bundleName.replace('.', '/') + ".properties for locale " + loc);
/* 220 */       throw e;
/*     */     }
/* 222 */     HashtableOfInt templates = new HashtableOfInt(700);
/* 223 */     Enumeration keys = bundle.getKeys();
/* 224 */     while (keys.hasMoreElements()) {
/* 225 */       String key = (String)keys.nextElement();
/*     */       try {
/* 227 */         int messageID = Integer.parseInt(key);
/* 228 */         templates.put(keyFromID(messageID), bundle.getString(key));
/*     */       }
/*     */       catch (NumberFormatException localNumberFormatException) {
/*     */       }
/*     */       catch (MissingResourceException localMissingResourceException1) {
/*     */       }
/*     */     }
/* 235 */     return templates;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\ecj-3.5.2.jar
 * Qualified Name:     org.eclipse.jdt.internal.compiler.problem.DefaultProblemFactory
 * JD-Core Version:    0.6.0
 */