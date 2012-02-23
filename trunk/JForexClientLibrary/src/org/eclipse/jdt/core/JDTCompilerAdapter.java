/*     */ package org.eclipse.jdt.core;
/*     */ 
/*     */ import [Ljava.lang.String;;
/*     */ import java.io.File;
/*     */ import java.io.IOException;
/*     */ import java.io.PrintWriter;
/*     */ import java.lang.reflect.Constructor;
/*     */ import java.lang.reflect.InvocationTargetException;
/*     */ import java.lang.reflect.Method;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import java.util.Comparator;
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Map.Entry;
/*     */ import java.util.Set;
/*     */ import org.apache.tools.ant.BuildException;
/*     */ import org.apache.tools.ant.taskdefs.Javac;
/*     */ import org.apache.tools.ant.taskdefs.compilers.DefaultCompilerAdapter;
/*     */ import org.apache.tools.ant.types.Commandline;
/*     */ import org.apache.tools.ant.types.Commandline.Argument;
/*     */ import org.apache.tools.ant.types.Path;
/*     */ import org.apache.tools.ant.util.JavaEnvUtils;
/*     */ import org.eclipse.jdt.core.compiler.CharOperation;
/*     */ import org.eclipse.jdt.internal.antadapter.AntAdapterMessages;
/*     */ import org.eclipse.jdt.internal.compiler.impl.CompilerOptions;
/*     */ import org.eclipse.jdt.internal.compiler.util.SuffixConstants;
/*     */ import org.eclipse.jdt.internal.compiler.util.Util;
/*     */ 
/*     */ public class JDTCompilerAdapter extends DefaultCompilerAdapter
/*     */ {
/*  54 */   private static final char[] SEPARATOR_CHARS = { '/', '\\' };
/*  55 */   private static final char[] ADAPTER_PREFIX = "#ADAPTER#".toCharArray();
/*  56 */   private static final char[] ADAPTER_ENCODING = "ENCODING#".toCharArray();
/*  57 */   private static final char[] ADAPTER_ACCESS = "ACCESS#".toCharArray();
/*  58 */   private static String compilerClass = "org.eclipse.jdt.internal.compiler.batch.Main";
/*     */   String logFileName;
/*     */   Map customDefaultOptions;
/*  61 */   private Map fileEncodings = null;
/*  62 */   private Map dirEncodings = null;
/*  63 */   private List accessRules = null;
/*     */ 
/*     */   public boolean execute()
/*     */     throws BuildException
/*     */   {
/*  71 */     this.attributes.log(AntAdapterMessages.getString("ant.jdtadapter.info.usingJDTCompiler"), 3);
/*  72 */     Commandline cmd = setupJavacCommand();
/*     */     try
/*     */     {
/*  75 */       Class c = Class.forName(compilerClass);
/*  76 */       Constructor batchCompilerConstructor = c.getConstructor(new Class[] { PrintWriter.class, PrintWriter.class, Boolean.TYPE, Map.class });
/*  77 */       Object batchCompilerInstance = batchCompilerConstructor.newInstance(new Object[] { new PrintWriter(System.out), new PrintWriter(System.err), Boolean.TRUE, this.customDefaultOptions });
/*  78 */       Method compile = c.getMethod("compile", new Class[] { [Ljava.lang.String.class });
/*  79 */       Object result = compile.invoke(batchCompilerInstance, new Object[] { cmd.getArguments() });
/*  80 */       boolean resultValue = ((Boolean)result).booleanValue();
/*  81 */       if ((!resultValue) && (this.logFileName != null)) {
/*  82 */         this.attributes.log(AntAdapterMessages.getString("ant.jdtadapter.error.compilationFailed", this.logFileName));
/*     */       }
/*  84 */       return resultValue;
/*     */     } catch (ClassNotFoundException localClassNotFoundException5) {
/*  86 */       throw new BuildException(AntAdapterMessages.getString("ant.jdtadapter.error.cannotFindJDTCompiler")); } catch (Exception ex) {
/*     */     }
/*  88 */     throw new BuildException(ex);
/*     */   }
/*     */ 
/*     */   protected Commandline setupJavacCommand()
/*     */     throws BuildException
/*     */   {
/*  94 */     Commandline cmd = new Commandline();
/*  95 */     this.customDefaultOptions = new CompilerOptions().getMap();
/*     */ 
/*  97 */     Class javacClass = Javac.class;
/*     */ 
/* 103 */     String[] compilerArgs = processCompilerArguments(javacClass);
/*     */ 
/* 108 */     cmd.createArgument().setValue("-noExit");
/*     */ 
/* 110 */     if (this.bootclasspath != null) {
/* 111 */       cmd.createArgument().setValue("-bootclasspath");
/* 112 */       if (this.bootclasspath.size() != 0)
/*     */       {
/* 116 */         cmd.createArgument().setPath(this.bootclasspath);
/*     */       }
/* 118 */       else cmd.createArgument().setValue(Util.EMPTY_STRING);
/*     */ 
/*     */     }
/*     */ 
/* 122 */     Path classpath = new Path(this.project);
/*     */ 
/* 129 */     if (this.extdirs != null) {
/* 130 */       cmd.createArgument().setValue("-extdirs");
/* 131 */       cmd.createArgument().setPath(this.extdirs);
/*     */     }
/*     */ 
/* 138 */     classpath.append(getCompileClasspath());
/*     */ 
/* 142 */     Path sourcepath = null;
/*     */ 
/* 146 */     Method getSourcepathMethod = null;
/*     */     try {
/* 148 */       getSourcepathMethod = javacClass.getMethod("getSourcepath", null);
/*     */     }
/*     */     catch (NoSuchMethodException localNoSuchMethodException1) {
/*     */     }
/* 152 */     Path compileSourcePath = null;
/* 153 */     if (getSourcepathMethod != null)
/*     */       try {
/* 155 */         compileSourcePath = (Path)getSourcepathMethod.invoke(this.attributes, null);
/*     */       }
/*     */       catch (IllegalAccessException localIllegalAccessException1)
/*     */       {
/*     */       }
/*     */       catch (InvocationTargetException localInvocationTargetException1) {
/*     */       }
/* 162 */     if (compileSourcePath != null)
/* 163 */       sourcepath = compileSourcePath;
/*     */     else {
/* 165 */       sourcepath = this.src;
/*     */     }
/* 167 */     classpath.append(sourcepath);
/*     */ 
/* 171 */     cmd.createArgument().setValue("-classpath");
/* 172 */     createClasspathArgument(cmd, classpath);
/*     */ 
/* 174 */     String javaVersion = JavaEnvUtils.getJavaVersion();
/* 175 */     String memoryParameterPrefix = javaVersion.equals("1.1") ? "-J-" : "-J-X";
/* 176 */     if (this.memoryInitialSize != null) {
/* 177 */       if (!this.attributes.isForkedJavac())
/* 178 */         this.attributes.log(AntAdapterMessages.getString("ant.jdtadapter.info.ignoringMemoryInitialSize"), 1);
/*     */       else {
/* 180 */         cmd.createArgument().setValue(memoryParameterPrefix + 
/* 181 */           "ms" + this.memoryInitialSize);
/*     */       }
/*     */     }
/*     */ 
/* 185 */     if (this.memoryMaximumSize != null) {
/* 186 */       if (!this.attributes.isForkedJavac())
/* 187 */         this.attributes.log(AntAdapterMessages.getString("ant.jdtadapter.info.ignoringMemoryMaximumSize"), 1);
/*     */       else {
/* 189 */         cmd.createArgument().setValue(memoryParameterPrefix + 
/* 190 */           "mx" + this.memoryMaximumSize);
/*     */       }
/*     */     }
/*     */ 
/* 194 */     if (this.debug)
/*     */     {
/* 197 */       Method getDebugLevelMethod = null;
/*     */       try {
/* 199 */         getDebugLevelMethod = javacClass.getMethod("getDebugLevel", null);
/*     */       }
/*     */       catch (NoSuchMethodException localNoSuchMethodException2)
/*     */       {
/*     */       }
/* 204 */       String debugLevel = null;
/* 205 */       if (getDebugLevelMethod != null)
/*     */         try {
/* 207 */           debugLevel = (String)getDebugLevelMethod.invoke(this.attributes, null);
/*     */         }
/*     */         catch (IllegalAccessException localIllegalAccessException2)
/*     */         {
/*     */         }
/*     */         catch (InvocationTargetException localInvocationTargetException2) {
/*     */         }
/* 214 */       if (debugLevel != null) {
/* 215 */         this.customDefaultOptions.put("org.eclipse.jdt.core.compiler.debug.localVariable", "do not generate");
/* 216 */         this.customDefaultOptions.put("org.eclipse.jdt.core.compiler.debug.lineNumber", "do not generate");
/* 217 */         this.customDefaultOptions.put("org.eclipse.jdt.core.compiler.debug.sourceFile", "do not generate");
/* 218 */         if (debugLevel.length() != 0) {
/* 219 */           if (debugLevel.indexOf("vars") != -1) {
/* 220 */             this.customDefaultOptions.put("org.eclipse.jdt.core.compiler.debug.localVariable", "generate");
/*     */           }
/* 222 */           if (debugLevel.indexOf("lines") != -1) {
/* 223 */             this.customDefaultOptions.put("org.eclipse.jdt.core.compiler.debug.lineNumber", "generate");
/*     */           }
/* 225 */           if (debugLevel.indexOf("source") != -1)
/* 226 */             this.customDefaultOptions.put("org.eclipse.jdt.core.compiler.debug.sourceFile", "generate");
/*     */         }
/*     */       }
/*     */       else {
/* 230 */         this.customDefaultOptions.put("org.eclipse.jdt.core.compiler.debug.localVariable", "generate");
/* 231 */         this.customDefaultOptions.put("org.eclipse.jdt.core.compiler.debug.lineNumber", "generate");
/* 232 */         this.customDefaultOptions.put("org.eclipse.jdt.core.compiler.debug.sourceFile", "generate");
/*     */       }
/*     */     } else {
/* 235 */       this.customDefaultOptions.put("org.eclipse.jdt.core.compiler.debug.localVariable", "do not generate");
/* 236 */       this.customDefaultOptions.put("org.eclipse.jdt.core.compiler.debug.lineNumber", "do not generate");
/* 237 */       this.customDefaultOptions.put("org.eclipse.jdt.core.compiler.debug.sourceFile", "do not generate");
/*     */     }
/*     */ 
/* 243 */     if (this.attributes.getNowarn())
/*     */     {
/* 245 */       Object[] entries = this.customDefaultOptions.entrySet().toArray();
/* 246 */       int i = 0; for (int max = entries.length; i < max; i++) {
/* 247 */         Map.Entry entry = (Map.Entry)entries[i];
/* 248 */         if (!(entry.getKey() instanceof String))
/*     */           continue;
/* 250 */         if (!(entry.getValue() instanceof String))
/*     */           continue;
/* 252 */         if (((String)entry.getValue()).equals("warning")) {
/* 253 */           this.customDefaultOptions.put(entry.getKey(), "ignore");
/*     */         }
/*     */       }
/* 256 */       this.customDefaultOptions.put("org.eclipse.jdt.core.compiler.taskTags", Util.EMPTY_STRING);
/* 257 */       if (this.deprecation) {
/* 258 */         this.customDefaultOptions.put("org.eclipse.jdt.core.compiler.problem.deprecation", "warning");
/* 259 */         this.customDefaultOptions.put("org.eclipse.jdt.core.compiler.problem.deprecationInDeprecatedCode", "enabled");
/* 260 */         this.customDefaultOptions.put("org.eclipse.jdt.core.compiler.problem.deprecationWhenOverridingDeprecatedMethod", "enabled");
/*     */       }
/* 262 */     } else if (this.deprecation) {
/* 263 */       this.customDefaultOptions.put("org.eclipse.jdt.core.compiler.problem.deprecation", "warning");
/* 264 */       this.customDefaultOptions.put("org.eclipse.jdt.core.compiler.problem.deprecationInDeprecatedCode", "enabled");
/* 265 */       this.customDefaultOptions.put("org.eclipse.jdt.core.compiler.problem.deprecationWhenOverridingDeprecatedMethod", "enabled");
/*     */     } else {
/* 267 */       this.customDefaultOptions.put("org.eclipse.jdt.core.compiler.problem.deprecation", "ignore");
/* 268 */       this.customDefaultOptions.put("org.eclipse.jdt.core.compiler.problem.deprecationInDeprecatedCode", "disabled");
/* 269 */       this.customDefaultOptions.put("org.eclipse.jdt.core.compiler.problem.deprecationWhenOverridingDeprecatedMethod", "disabled");
/*     */     }
/*     */ 
/* 275 */     if (this.destDir != null) {
/* 276 */       cmd.createArgument().setValue("-d");
/* 277 */       cmd.createArgument().setFile(this.destDir.getAbsoluteFile());
/*     */     }
/*     */ 
/* 283 */     if (this.verbose) {
/* 284 */       cmd.createArgument().setValue("-verbose");
/*     */     }
/*     */ 
/* 290 */     if (!this.attributes.getFailonerror()) {
/* 291 */       cmd.createArgument().setValue("-proceedOnError");
/*     */     }
/*     */ 
/* 297 */     if (this.target != null) {
/* 298 */       this.customDefaultOptions.put("org.eclipse.jdt.core.compiler.codegen.targetPlatform", this.target);
/*     */     }
/*     */ 
/* 304 */     String source = this.attributes.getSource();
/* 305 */     if (source != null) {
/* 306 */       this.customDefaultOptions.put("org.eclipse.jdt.core.compiler.source", source);
/*     */     }
/*     */ 
/* 312 */     if (this.encoding != null) {
/* 313 */       cmd.createArgument().setValue("-encoding");
/* 314 */       cmd.createArgument().setValue(this.encoding);
/*     */     }
/*     */ 
/* 317 */     if (compilerArgs != null)
/*     */     {
/* 321 */       int length = compilerArgs.length;
/* 322 */       if (length != 0) {
/* 323 */         int i = 0; for (int max = length; i < max; i++) {
/* 324 */           String arg = compilerArgs[i];
/* 325 */           if ((this.logFileName == null) && ("-log".equals(arg)) && (i + 1 < max)) {
/* 326 */             this.logFileName = compilerArgs[(i + 1)];
/*     */           }
/* 328 */           cmd.createArgument().setValue(arg);
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 337 */     logAndAddFilesToCompile(cmd);
/* 338 */     return cmd;
/*     */   }
/*     */ 
/*     */   private String[] processCompilerArguments(Class javacClass)
/*     */   {
/* 349 */     Method getCurrentCompilerArgsMethod = null;
/*     */     try {
/* 351 */       getCurrentCompilerArgsMethod = javacClass.getMethod("getCurrentCompilerArgs", null);
/*     */     }
/*     */     catch (NoSuchMethodException localNoSuchMethodException)
/*     */     {
/*     */     }
/* 356 */     String[] compilerArgs = (String[])null;
/* 357 */     if (getCurrentCompilerArgsMethod != null)
/*     */       try {
/* 359 */         compilerArgs = (String[])getCurrentCompilerArgsMethod.invoke(this.attributes, null);
/*     */       }
/*     */       catch (IllegalAccessException localIllegalAccessException)
/*     */       {
/*     */       }
/*     */       catch (InvocationTargetException localInvocationTargetException)
/*     */       {
/*     */       }
/* 367 */     if (compilerArgs != null) checkCompilerArgs(compilerArgs);
/* 368 */     return compilerArgs;
/*     */   }
/*     */ 
/*     */   private void checkCompilerArgs(String[] args)
/*     */   {
/* 377 */     for (int i = 0; i < args.length; i++) {
/* 378 */       if (args[i].charAt(0) != '@') continue;
/*     */       try {
/* 380 */         char[] content = Util.getFileCharContent(new File(args[i].substring(1)), null);
/* 381 */         int offset = 0;
/* 382 */         int prefixLength = ADAPTER_PREFIX.length;
/* 383 */         while ((offset = CharOperation.indexOf(ADAPTER_PREFIX, content, true, offset)) > -1) {
/* 384 */           int start = offset + prefixLength;
/* 385 */           int end = CharOperation.indexOf('\n', content, start);
/* 386 */           if (end == -1)
/* 387 */             end = content.length;
/* 388 */           while (CharOperation.isWhitespace(content[end])) {
/* 389 */             end--;
/*     */           }
/*     */ 
/* 393 */           if (CharOperation.equals(ADAPTER_ENCODING, content, start, start + ADAPTER_ENCODING.length)) {
/* 394 */             CharOperation.replace(content, SEPARATOR_CHARS, File.separatorChar, start, end + 1);
/*     */ 
/* 396 */             start += ADAPTER_ENCODING.length;
/* 397 */             int encodeStart = CharOperation.lastIndexOf('[', content, start, end);
/* 398 */             if ((start < encodeStart) && (encodeStart < end)) {
/* 399 */               boolean isFile = CharOperation.equals(SuffixConstants.SUFFIX_java, content, encodeStart - 5, encodeStart, false);
/*     */ 
/* 401 */               String str = String.valueOf(content, start, encodeStart - start);
/* 402 */               String enc = String.valueOf(content, encodeStart, end - encodeStart + 1);
/* 403 */               if (isFile) {
/* 404 */                 if (this.fileEncodings == null) {
/* 405 */                   this.fileEncodings = new HashMap();
/*     */                 }
/* 407 */                 this.fileEncodings.put(str, enc);
/*     */               } else {
/* 409 */                 if (this.dirEncodings == null)
/* 410 */                   this.dirEncodings = new HashMap();
/* 411 */                 this.dirEncodings.put(str, enc);
/*     */               }
/*     */             }
/* 414 */           } else if (CharOperation.equals(ADAPTER_ACCESS, content, start, start + ADAPTER_ACCESS.length))
/*     */           {
/* 416 */             start += ADAPTER_ACCESS.length;
/* 417 */             int accessStart = CharOperation.indexOf('[', content, start, end);
/* 418 */             CharOperation.replace(content, SEPARATOR_CHARS, File.separatorChar, start, accessStart);
/* 419 */             if ((start < accessStart) && (accessStart < end)) {
/* 420 */               String path = String.valueOf(content, start, accessStart - start);
/* 421 */               String access = String.valueOf(content, accessStart, end - accessStart + 1);
/* 422 */               if (this.accessRules == null)
/* 423 */                 this.accessRules = new ArrayList();
/* 424 */               this.accessRules.add(path);
/* 425 */               this.accessRules.add(access);
/*     */             }
/*     */           }
/* 428 */           offset = end;
/*     */         }
/*     */       }
/*     */       catch (IOException localIOException)
/*     */       {
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   private void createClasspathArgument(Commandline cmd, Path classpath)
/*     */   {
/* 444 */     Commandline.Argument arg = cmd.createArgument();
/* 445 */     String[] pathElements = classpath.list();
/*     */ 
/* 448 */     if (pathElements.length == 0) {
/* 449 */       arg.setValue(Util.EMPTY_STRING);
/* 450 */       return;
/*     */     }
/*     */ 
/* 454 */     if (this.accessRules == null) {
/* 455 */       arg.setPath(classpath);
/* 456 */       return;
/*     */     }
/*     */ 
/* 459 */     int rulesLength = this.accessRules.size();
/* 460 */     String[] rules = (String[])this.accessRules.toArray(new String[rulesLength]);
/* 461 */     int nextRule = 0;
/* 462 */     StringBuffer result = new StringBuffer();
/*     */ 
/* 466 */     int i = 0; for (int max = pathElements.length; i < max; i++) {
/* 467 */       if (i > 0)
/* 468 */         result.append(File.pathSeparatorChar);
/* 469 */       String pathElement = pathElements[i];
/* 470 */       result.append(pathElement);
/*     */ 
/* 472 */       for (int j = nextRule; j < rulesLength; j += 2) {
/* 473 */         String rule = rules[j];
/* 474 */         if (pathElement.endsWith(rule)) {
/* 475 */           result.append(rules[(j + 1)]);
/* 476 */           nextRule = j + 2;
/* 477 */           break;
/*     */         }
/*     */         int ruleLength;
/* 480 */         if (rule.endsWith(File.separator))
/*     */         {
/* 483 */           int ruleLength = rule.length();
/* 484 */           if (pathElement.regionMatches(false, pathElement.length() - ruleLength + 1, rule, 0, ruleLength - 1)) {
/* 485 */             result.append(rules[(j + 1)]);
/* 486 */             nextRule = j + 2;
/* 487 */             break;
/*     */           }
/*     */         } else {
/* 489 */           if (!pathElement.endsWith(File.separator))
/*     */             continue;
/* 491 */           ruleLength = rule.length();
/* 492 */           if (pathElement.regionMatches(false, pathElement.length() - ruleLength - 1, rule, 0, ruleLength)) {
/* 493 */             result.append(rules[(j + 1)]);
/* 494 */             nextRule = j + 2;
/* 495 */             break;
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/* 501 */     arg.setValue(result.toString());
/*     */   }
/*     */ 
/*     */   protected void logAndAddFilesToCompile(Commandline cmd)
/*     */   {
/* 511 */     this.attributes.log("Compilation " + cmd.describeArguments(), 
/* 512 */       3);
/*     */ 
/* 514 */     StringBuffer niceSourceList = new StringBuffer("File");
/* 515 */     if (this.compileList.length != 1) {
/* 516 */       niceSourceList.append("s");
/*     */     }
/* 518 */     niceSourceList.append(" to be compiled:");
/* 519 */     niceSourceList.append(lSep);
/*     */ 
/* 521 */     String[] encodedFiles = (String[])null; String[] encodedDirs = (String[])null;
/* 522 */     int encodedFilesLength = 0; int encodedDirsLength = 0;
/* 523 */     if (this.fileEncodings != null) {
/* 524 */       encodedFilesLength = this.fileEncodings.size();
/* 525 */       encodedFiles = new String[encodedFilesLength];
/* 526 */       this.fileEncodings.keySet().toArray(encodedFiles);
/*     */     }
/* 528 */     if (this.dirEncodings != null) {
/* 529 */       encodedDirsLength = this.dirEncodings.size();
/* 530 */       encodedDirs = new String[encodedDirsLength];
/* 531 */       this.dirEncodings.keySet().toArray(encodedDirs);
/*     */ 
/* 534 */       Comparator comparator = new Comparator() {
/*     */         public int compare(Object o1, Object o2) {
/* 536 */           return ((String)o2).length() - ((String)o1).length();
/*     */         }
/*     */       };
/* 539 */       Arrays.sort(encodedDirs, comparator);
/*     */     }
/*     */ 
/* 542 */     for (int i = 0; i < this.compileList.length; i++) {
/* 543 */       String arg = this.compileList[i].getAbsolutePath();
/* 544 */       boolean encoded = false;
/* 545 */       if (encodedFiles != null)
/*     */       {
/* 547 */         for (int j = 0; j < encodedFilesLength; j++) {
/* 548 */           if (!arg.endsWith(encodedFiles[j]))
/*     */             continue;
/* 550 */           arg = arg + (String)this.fileEncodings.get(encodedFiles[j]);
/* 551 */           if (j < encodedFilesLength - 1) {
/* 552 */             System.arraycopy(encodedFiles, j + 1, encodedFiles, j, encodedFilesLength - j - 1);
/*     */           }
/* 554 */           encodedFilesLength--; encodedFiles[encodedFilesLength] = null;
/* 555 */           encoded = true;
/* 556 */           break;
/*     */         }
/*     */       }
/*     */ 
/* 560 */       if ((!encoded) && (encodedDirs != null))
/*     */       {
/* 562 */         for (int j = 0; j < encodedDirsLength; j++) {
/* 563 */           if (arg.lastIndexOf(encodedDirs[j]) != -1) {
/* 564 */             arg = arg + (String)this.dirEncodings.get(encodedDirs[j]);
/* 565 */             break;
/*     */           }
/*     */         }
/*     */       }
/* 569 */       cmd.createArgument().setValue(arg);
/* 570 */       niceSourceList.append("    " + arg + lSep);
/*     */     }
/*     */ 
/* 573 */     this.attributes.log(niceSourceList.toString(), 3);
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\ecj-3.5.2.jar
 * Qualified Name:     org.eclipse.jdt.core.JDTCompilerAdapter
 * JD-Core Version:    0.6.0
 */