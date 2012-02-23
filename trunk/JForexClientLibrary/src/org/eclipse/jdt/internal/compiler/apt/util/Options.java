/*     */ package org.eclipse.jdt.internal.compiler.apt.util;
/*     */ 
/*     */ import java.util.HashSet;
/*     */ import java.util.Set;
/*     */ import java.util.StringTokenizer;
/*     */ 
/*     */ public final class Options
/*     */ {
/*  25 */   private static final Set<String> ZERO_ARGUMENT_OPTIONS = new HashSet();
/*     */   private static final Set<String> ONE_ARGUMENT_OPTIONS;
/*     */   private static final Set<String> FILE_MANAGER_OPTIONS;
/*     */ 
/*     */   static
/*     */   {
/*  26 */     ZERO_ARGUMENT_OPTIONS.add("-progress");
/*  27 */     ZERO_ARGUMENT_OPTIONS.add("-proceedOnError");
/*  28 */     ZERO_ARGUMENT_OPTIONS.add("-time");
/*  29 */     ZERO_ARGUMENT_OPTIONS.add("-v");
/*  30 */     ZERO_ARGUMENT_OPTIONS.add("-version");
/*  31 */     ZERO_ARGUMENT_OPTIONS.add("-showversion");
/*  32 */     ZERO_ARGUMENT_OPTIONS.add("-deprecation");
/*  33 */     ZERO_ARGUMENT_OPTIONS.add("-help");
/*  34 */     ZERO_ARGUMENT_OPTIONS.add("-?");
/*  35 */     ZERO_ARGUMENT_OPTIONS.add("-help:warn");
/*  36 */     ZERO_ARGUMENT_OPTIONS.add("-?:warn");
/*  37 */     ZERO_ARGUMENT_OPTIONS.add("-noExit");
/*  38 */     ZERO_ARGUMENT_OPTIONS.add("-verbose");
/*  39 */     ZERO_ARGUMENT_OPTIONS.add("-referenceInfo");
/*  40 */     ZERO_ARGUMENT_OPTIONS.add("-inlineJSR");
/*  41 */     ZERO_ARGUMENT_OPTIONS.add("-g");
/*  42 */     ZERO_ARGUMENT_OPTIONS.add("-g:none");
/*  43 */     ZERO_ARGUMENT_OPTIONS.add("-nowarn");
/*  44 */     ZERO_ARGUMENT_OPTIONS.add("-warn:none");
/*  45 */     ZERO_ARGUMENT_OPTIONS.add("-preserveAllLocals");
/*  46 */     ZERO_ARGUMENT_OPTIONS.add("-enableJavadoc");
/*  47 */     ZERO_ARGUMENT_OPTIONS.add("-Xemacs");
/*  48 */     ZERO_ARGUMENT_OPTIONS.add("-X");
/*  49 */     ZERO_ARGUMENT_OPTIONS.add("-O");
/*  50 */     ZERO_ARGUMENT_OPTIONS.add("-1.3");
/*  51 */     ZERO_ARGUMENT_OPTIONS.add("-1.4");
/*  52 */     ZERO_ARGUMENT_OPTIONS.add("-1.5");
/*  53 */     ZERO_ARGUMENT_OPTIONS.add("-5");
/*  54 */     ZERO_ARGUMENT_OPTIONS.add("-5.0");
/*  55 */     ZERO_ARGUMENT_OPTIONS.add("-1.6");
/*  56 */     ZERO_ARGUMENT_OPTIONS.add("-6");
/*  57 */     ZERO_ARGUMENT_OPTIONS.add("-6.0");
/*  58 */     ZERO_ARGUMENT_OPTIONS.add("-proc:only");
/*  59 */     ZERO_ARGUMENT_OPTIONS.add("-proc:none");
/*  60 */     ZERO_ARGUMENT_OPTIONS.add("-XprintProcessorInfo");
/*  61 */     ZERO_ARGUMENT_OPTIONS.add("-XprintRounds");
/*     */ 
/*  63 */     FILE_MANAGER_OPTIONS = new HashSet();
/*  64 */     FILE_MANAGER_OPTIONS.add("-bootclasspath");
/*  65 */     FILE_MANAGER_OPTIONS.add("-encoding");
/*  66 */     FILE_MANAGER_OPTIONS.add("-d");
/*  67 */     FILE_MANAGER_OPTIONS.add("-classpath");
/*  68 */     FILE_MANAGER_OPTIONS.add("-cp");
/*  69 */     FILE_MANAGER_OPTIONS.add("-sourcepath");
/*  70 */     FILE_MANAGER_OPTIONS.add("-extdirs");
/*  71 */     FILE_MANAGER_OPTIONS.add("-endorseddirs");
/*  72 */     FILE_MANAGER_OPTIONS.add("-s");
/*  73 */     FILE_MANAGER_OPTIONS.add("-processorpath");
/*     */ 
/*  75 */     ONE_ARGUMENT_OPTIONS = new HashSet();
/*  76 */     ONE_ARGUMENT_OPTIONS.addAll(FILE_MANAGER_OPTIONS);
/*  77 */     ONE_ARGUMENT_OPTIONS.add("-log");
/*  78 */     ONE_ARGUMENT_OPTIONS.add("-repeat");
/*  79 */     ONE_ARGUMENT_OPTIONS.add("-maxProblems");
/*  80 */     ONE_ARGUMENT_OPTIONS.add("-source");
/*  81 */     ONE_ARGUMENT_OPTIONS.add("-target");
/*  82 */     ONE_ARGUMENT_OPTIONS.add("-processor");
/*  83 */     ONE_ARGUMENT_OPTIONS.add("-classNames");
/*     */   }
/*     */   public static int processOptionsFileManager(String option) {
/*  86 */     if (option == null) return -1;
/*  87 */     if (FILE_MANAGER_OPTIONS.contains(option)) {
/*  88 */       return 1;
/*     */     }
/*  90 */     return -1;
/*     */   }
/*     */ 
/*     */   public static int processOptions(String option) {
/*  94 */     if (option == null) return -1;
/*  95 */     if (ZERO_ARGUMENT_OPTIONS.contains(option)) {
/*  96 */       return 0;
/*     */     }
/*  98 */     if (ONE_ARGUMENT_OPTIONS.contains(option)) {
/*  99 */       return 1;
/*     */     }
/* 101 */     if (option.startsWith("-g")) {
/* 102 */       int length = option.length();
/* 103 */       if (length > 3) {
/* 104 */         StringTokenizer tokenizer = 
/* 105 */           new StringTokenizer(option.substring(3, option.length()), ",");
/* 106 */         while (tokenizer.hasMoreTokens()) {
/* 107 */           String token = tokenizer.nextToken();
/* 108 */           if ((!"vars".equals(token)) && (!"lines".equals(token)) && (!"source".equals(token)))
/*     */           {
/* 111 */             return -1;
/*     */           }
/*     */         }
/* 113 */         return 0;
/*     */       }
/* 115 */       return -1;
/*     */     }
/* 117 */     if (option.startsWith("-warn")) {
/* 118 */       int length = option.length();
/* 119 */       if (length <= 6)
/* 120 */         return -1;
/*     */       int warnTokenStart;
/*     */       int warnTokenStart;
/*     */       int warnTokenStart;
/* 123 */       switch (option.charAt(6)) {
/*     */       case '+':
/* 125 */         warnTokenStart = 7;
/* 126 */         break;
/*     */       case '-':
/* 128 */         warnTokenStart = 7;
/* 129 */         break;
/*     */       case ',':
/*     */       default:
/* 131 */         warnTokenStart = 6;
/*     */       }
/*     */ 
/* 134 */       StringTokenizer tokenizer = 
/* 135 */         new StringTokenizer(option.substring(warnTokenStart, option.length()), ",");
/* 136 */       int tokenCounter = 0;
/*     */ 
/* 138 */       while (tokenizer.hasMoreTokens()) {
/* 139 */         String token = tokenizer.nextToken();
/* 140 */         tokenCounter++;
/* 141 */         if (("constructorName".equals(token)) || 
/* 142 */           (token.equals("pkgDefaultMethod")) || 
/* 143 */           (token.equals("packageDefaultMethod")) || 
/* 144 */           (token.equals("maskedCatchBlock")) || 
/* 145 */           (token.equals("maskedCatchBlocks")) || 
/* 146 */           (token.equals("deprecation")) || 
/* 147 */           (token.equals("allDeprecation")) || 
/* 148 */           (token.equals("unusedLocal")) || 
/* 149 */           (token.equals("unusedLocals")) || 
/* 150 */           (token.equals("unusedArgument")) || 
/* 151 */           (token.equals("unusedArguments")) || 
/* 152 */           (token.equals("unusedImport")) || 
/* 153 */           (token.equals("unusedImports")) || 
/* 154 */           (token.equals("unusedPrivate")) || 
/* 155 */           (token.equals("unusedLabel")) || 
/* 156 */           (token.equals("localHiding")) || 
/* 157 */           (token.equals("fieldHiding")) || 
/* 158 */           (token.equals("specialParamHiding")) || 
/* 159 */           (token.equals("conditionAssign")) || 
/* 160 */           (token.equals("syntheticAccess")) || 
/* 161 */           (token.equals("synthetic-access")) || 
/* 162 */           (token.equals("nls")) || 
/* 163 */           (token.equals("staticReceiver")) || 
/* 164 */           (token.equals("indirectStatic")) || 
/* 165 */           (token.equals("noEffectAssign")) || 
/* 166 */           (token.equals("intfNonInherited")) || 
/* 167 */           (token.equals("interfaceNonInherited")) || 
/* 168 */           (token.equals("charConcat")) || 
/* 169 */           (token.equals("noImplicitStringConversion")) || 
/* 170 */           (token.equals("semicolon")) || 
/* 171 */           (token.equals("serial")) || 
/* 172 */           (token.equals("emptyBlock")) || 
/* 173 */           (token.equals("uselessTypeCheck")) || 
/* 174 */           (token.equals("unchecked")) || 
/* 175 */           (token.equals("unsafe")) || 
/* 176 */           (token.equals("raw")) || 
/* 177 */           (token.equals("finalBound")) || 
/* 178 */           (token.equals("suppress")) || 
/* 179 */           (token.equals("warningToken")) || 
/* 180 */           (token.equals("unnecessaryElse")) || 
/* 181 */           (token.equals("javadoc")) || 
/* 182 */           (token.equals("allJavadoc")) || 
/* 183 */           (token.equals("assertIdentifier")) || 
/* 184 */           (token.equals("enumIdentifier")) || 
/* 185 */           (token.equals("finally")) || 
/* 186 */           (token.equals("unusedThrown")) || 
/* 187 */           (token.equals("unqualifiedField")) || 
/* 188 */           (token.equals("unqualified-field-access")) || 
/* 189 */           (token.equals("typeHiding")) || 
/* 190 */           (token.equals("varargsCast")) || 
/* 191 */           (token.equals("null")) || 
/* 192 */           (token.equals("boxing")) || 
/* 193 */           (token.equals("over-ann")) || 
/* 194 */           (token.equals("dep-ann")) || 
/* 195 */           (token.equals("intfAnnotation")) || 
/* 196 */           (token.equals("enumSwitch")) || 
/* 197 */           (token.equals("incomplete-switch")) || 
/* 198 */           (token.equals("hiding")) || 
/* 199 */           (token.equals("static-access")) || 
/* 200 */           (token.equals("unused")) || 
/* 201 */           (token.equals("paramAssign")) || 
/* 202 */           (token.equals("discouraged")) || 
/* 203 */           (token.equals("forbidden")) || 
/* 204 */           (token.equals("fallthrough")))
/*     */           continue;
/* 206 */         if (token.equals("tasks")) {
/* 207 */           String taskTags = "";
/* 208 */           int start = token.indexOf('(');
/* 209 */           int end = token.indexOf(')');
/* 210 */           if ((start >= 0) && (end >= 0) && (start < end)) {
/* 211 */             taskTags = token.substring(start + 1, end).trim();
/* 212 */             taskTags = taskTags.replace('|', ',');
/*     */           }
/* 214 */           if (taskTags.length() == 0)
/* 215 */             return -1;
/*     */         }
/*     */         else
/*     */         {
/* 219 */           return -1;
/*     */         }
/*     */       }
/* 222 */       if (tokenCounter == 0) {
/* 223 */         return -1;
/*     */       }
/* 225 */       return 0;
/*     */     }
/*     */ 
/* 228 */     if ((option.startsWith("-J")) || 
/* 229 */       (option.startsWith("-X")) || 
/* 230 */       (option.startsWith("-A"))) {
/* 231 */       return 0;
/*     */     }
/* 233 */     return -1;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\ecj-3.5.2.jar
 * Qualified Name:     org.eclipse.jdt.internal.compiler.apt.util.Options
 * JD-Core Version:    0.6.0
 */