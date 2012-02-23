/*     */ package org.eclipse.jdt.internal.compiler.tool;
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
/*  58 */     ZERO_ARGUMENT_OPTIONS.add("-1.7");
/*  59 */     ZERO_ARGUMENT_OPTIONS.add("-7");
/*  60 */     ZERO_ARGUMENT_OPTIONS.add("-7.0");
/*  61 */     ZERO_ARGUMENT_OPTIONS.add("-proc:only");
/*  62 */     ZERO_ARGUMENT_OPTIONS.add("-proc:none");
/*  63 */     ZERO_ARGUMENT_OPTIONS.add("-XprintProcessorInfo");
/*  64 */     ZERO_ARGUMENT_OPTIONS.add("-XprintRounds");
/*     */ 
/*  66 */     FILE_MANAGER_OPTIONS = new HashSet();
/*  67 */     FILE_MANAGER_OPTIONS.add("-bootclasspath");
/*  68 */     FILE_MANAGER_OPTIONS.add("-encoding");
/*  69 */     FILE_MANAGER_OPTIONS.add("-d");
/*  70 */     FILE_MANAGER_OPTIONS.add("-classpath");
/*  71 */     FILE_MANAGER_OPTIONS.add("-cp");
/*  72 */     FILE_MANAGER_OPTIONS.add("-sourcepath");
/*  73 */     FILE_MANAGER_OPTIONS.add("-extdirs");
/*  74 */     FILE_MANAGER_OPTIONS.add("-endorseddirs");
/*  75 */     FILE_MANAGER_OPTIONS.add("-s");
/*  76 */     FILE_MANAGER_OPTIONS.add("-processorpath");
/*     */ 
/*  78 */     ONE_ARGUMENT_OPTIONS = new HashSet();
/*  79 */     ONE_ARGUMENT_OPTIONS.addAll(FILE_MANAGER_OPTIONS);
/*  80 */     ONE_ARGUMENT_OPTIONS.add("-log");
/*  81 */     ONE_ARGUMENT_OPTIONS.add("-repeat");
/*  82 */     ONE_ARGUMENT_OPTIONS.add("-maxProblems");
/*  83 */     ONE_ARGUMENT_OPTIONS.add("-source");
/*  84 */     ONE_ARGUMENT_OPTIONS.add("-target");
/*  85 */     ONE_ARGUMENT_OPTIONS.add("-processor");
/*  86 */     ONE_ARGUMENT_OPTIONS.add("-classNames");
/*     */   }
/*     */ 
/*     */   public static int processOptionsFileManager(String option) {
/*  90 */     if (option == null) return -1;
/*  91 */     if (FILE_MANAGER_OPTIONS.contains(option)) {
/*  92 */       return 1;
/*     */     }
/*  94 */     return -1;
/*     */   }
/*     */ 
/*     */   public static int processOptions(String option) {
/*  98 */     if (option == null) return -1;
/*  99 */     if (ZERO_ARGUMENT_OPTIONS.contains(option)) {
/* 100 */       return 0;
/*     */     }
/* 102 */     if (ONE_ARGUMENT_OPTIONS.contains(option)) {
/* 103 */       return 1;
/*     */     }
/* 105 */     if (option.startsWith("-g")) {
/* 106 */       int length = option.length();
/* 107 */       if (length > 3) {
/* 108 */         StringTokenizer tokenizer = 
/* 109 */           new StringTokenizer(option.substring(3, option.length()), ",");
/* 110 */         while (tokenizer.hasMoreTokens()) {
/* 111 */           String token = tokenizer.nextToken();
/* 112 */           if ((!"vars".equals(token)) && (!"lines".equals(token)) && (!"source".equals(token)))
/*     */           {
/* 115 */             return -1;
/*     */           }
/*     */         }
/* 117 */         return 0;
/*     */       }
/* 119 */       return -1;
/*     */     }
/* 121 */     if (option.startsWith("-warn")) {
/* 122 */       int length = option.length();
/* 123 */       if (length <= 6)
/* 124 */         return -1;
/*     */       int warnTokenStart;
/*     */       int warnTokenStart;
/*     */       int warnTokenStart;
/* 127 */       switch (option.charAt(6)) {
/*     */       case '+':
/* 129 */         warnTokenStart = 7;
/* 130 */         break;
/*     */       case '-':
/* 132 */         warnTokenStart = 7;
/* 133 */         break;
/*     */       case ',':
/*     */       default:
/* 135 */         warnTokenStart = 6;
/*     */       }
/*     */ 
/* 138 */       StringTokenizer tokenizer = 
/* 139 */         new StringTokenizer(option.substring(warnTokenStart, option.length()), ",");
/* 140 */       int tokenCounter = 0;
/*     */ 
/* 142 */       while (tokenizer.hasMoreTokens()) {
/* 143 */         String token = tokenizer.nextToken();
/* 144 */         tokenCounter++;
/* 145 */         if ((token.equals("allDeadCode")) || 
/* 146 */           (token.equals("allDeprecation")) || 
/* 147 */           (token.equals("allJavadoc")) || 
/* 148 */           (token.equals("assertIdentifier")) || 
/* 149 */           (token.equals("boxing")) || 
/* 150 */           (token.equals("charConcat")) || 
/* 151 */           (token.equals("compareIdentical")) || 
/* 152 */           (token.equals("conditionAssign")) || 
/* 153 */           (token.equals("constructorName")) || 
/* 154 */           (token.equals("deadCode")) || 
/* 155 */           (token.equals("dep-ann")) || 
/* 156 */           (token.equals("deprecation")) || 
/* 157 */           (token.equals("discouraged")) || 
/* 158 */           (token.equals("emptyBlock")) || 
/* 159 */           (token.equals("enumIdentifier")) || 
/* 160 */           (token.equals("enumSwitch")) || 
/* 161 */           (token.equals("fallthrough")) || 
/* 162 */           (token.equals("fieldHiding")) || 
/* 163 */           (token.equals("finalBound")) || 
/* 164 */           (token.equals("finally")) || 
/* 165 */           (token.equals("forbidden")) || 
/* 166 */           (token.equals("hashCode")) || 
/* 167 */           (token.equals("hiding")) || 
/* 168 */           (token.equals("incomplete-switch")) || 
/* 169 */           (token.equals("indirectStatic")) || 
/* 170 */           (token.equals("interfaceNonInherited")) || 
/* 171 */           (token.equals("intfAnnotation")) || 
/* 172 */           (token.equals("intfNonInherited")) || 
/* 173 */           (token.equals("intfRedundant")) || 
/* 174 */           (token.equals("javadoc")) || 
/* 175 */           (token.equals("localHiding")) || 
/* 176 */           (token.equals("maskedCatchBlock")) || 
/* 177 */           (token.equals("maskedCatchBlocks")) || 
/* 178 */           (token.equals("nls")) || 
/* 179 */           (token.equals("noEffectAssign")) || 
/* 180 */           (token.equals("noImplicitStringConversion")) || 
/* 181 */           (token.equals("null")) || 
/* 182 */           (token.equals("nullDereference")) || 
/* 183 */           (token.equals("over-ann")) || 
/* 184 */           (token.equals("packageDefaultMethod")) || 
/* 185 */           (token.equals("paramAssign")) || 
/* 186 */           (token.equals("pkgDefaultMethod")) || 
/* 187 */           (token.equals("raw")) || 
/* 188 */           (token.equals("semicolon")) || 
/* 189 */           (token.equals("serial")) || 
/* 190 */           (token.equals("specialParamHiding")) || 
/* 191 */           (token.equals("static-access")) || 
/* 192 */           (token.equals("staticReceiver")) || 
/* 193 */           (token.equals("super")) || 
/* 194 */           (token.equals("suppress")) || 
/* 195 */           (token.equals("syncOverride")) || 
/* 196 */           (token.equals("synthetic-access")) || 
/* 197 */           (token.equals("syntheticAccess")) || 
/* 198 */           (token.equals("typeHiding")) || 
/* 199 */           (token.equals("unchecked")) || 
/* 200 */           (token.equals("unnecessaryElse")) || 
/* 201 */           (token.equals("unqualified-field-access")) || 
/* 202 */           (token.equals("unqualifiedField")) || 
/* 203 */           (token.equals("unsafe")) || 
/* 204 */           (token.equals("unused")) || 
/* 205 */           (token.equals("unusedArgument")) || 
/* 206 */           (token.equals("unusedArguments")) || 
/* 207 */           (token.equals("unusedImport")) || 
/* 208 */           (token.equals("unusedImports")) || 
/* 209 */           (token.equals("unusedLabel")) || 
/* 210 */           (token.equals("unusedLocal")) || 
/* 211 */           (token.equals("unusedLocals")) || 
/* 212 */           (token.equals("unusedPrivate")) || 
/* 213 */           (token.equals("unusedThrown")) || 
/* 214 */           (token.equals("unusedTypeArgs")) || 
/* 215 */           (token.equals("uselessTypeCheck")) || 
/* 216 */           (token.equals("varargsCast")) || 
/* 217 */           (token.equals("warningToken")))
/*     */           continue;
/* 219 */         if (token.equals("tasks")) {
/* 220 */           String taskTags = "";
/* 221 */           int start = token.indexOf('(');
/* 222 */           int end = token.indexOf(')');
/* 223 */           if ((start >= 0) && (end >= 0) && (start < end)) {
/* 224 */             taskTags = token.substring(start + 1, end).trim();
/* 225 */             taskTags = taskTags.replace('|', ',');
/*     */           }
/* 227 */           if (taskTags.length() == 0)
/* 228 */             return -1;
/*     */         }
/*     */         else
/*     */         {
/* 232 */           return -1;
/*     */         }
/*     */       }
/* 235 */       if (tokenCounter == 0) {
/* 236 */         return -1;
/*     */       }
/* 238 */       return 0;
/*     */     }
/*     */ 
/* 241 */     if ((option.startsWith("-J")) || 
/* 242 */       (option.startsWith("-X")) || 
/* 243 */       (option.startsWith("-A"))) {
/* 244 */       return 0;
/*     */     }
/* 246 */     return -1;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\ecj-3.5.2.jar
 * Qualified Name:     org.eclipse.jdt.internal.compiler.tool.Options
 * JD-Core Version:    0.6.0
 */