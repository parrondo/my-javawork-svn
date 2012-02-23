/*     */ package com.dukascopy.dds2.greed.agent.compiler;
/*     */ 
/*     */ import com.dukascopy.api.ConnectorStrategy;
/*     */ import com.dukascopy.api.IConsole;
/*     */ import com.dukascopy.api.IStrategy;
/*     */ import com.dukascopy.api.indicators.IIndicator;
/*     */ import com.dukascopy.dds2.greed.agent.strategy.StratUtils;
/*     */ import com.dukascopy.dds2.greed.util.FilePathManager;
/*     */ import com.dukascopy.dds2.greed.util.IOUtils;
/*     */ import java.io.BufferedInputStream;
/*     */ import java.io.BufferedReader;
/*     */ import java.io.File;
/*     */ import java.io.FileOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.InputStreamReader;
/*     */ import java.io.OutputStream;
/*     */ import java.io.PrintStream;
/*     */ import java.io.PrintWriter;
/*     */ import java.net.URL;
/*     */ import java.net.URLDecoder;
/*     */ import java.security.CodeSource;
/*     */ import java.security.ProtectionDomain;
/*     */ import java.util.ArrayList;
/*     */ import java.util.LinkedList;
/*     */ import java.util.List;
/*     */ import java.util.jar.JarInputStream;
/*     */ import java.util.jar.JarOutputStream;
/*     */ import java.util.jar.Pack200;
/*     */ import java.util.jar.Pack200.Unpacker;
/*     */ import java.util.regex.Matcher;
/*     */ import java.util.regex.Pattern;
/*     */ import java.util.zip.GZIPInputStream;
/*     */ import net.sf.javaguard.JavaGuard2;
/*     */ import org.eclipse.jdt.internal.compiler.batch.Main;
/*     */ import org.fife.ui.rsyntaxtextarea.CompileError;
/*     */ import org.fife.ui.rsyntaxtextarea.ICompiler;
/*     */ import org.fife.ui.rsyntaxtextarea.ParseUtils;
/*     */ import org.fife.ui.rsyntaxtextarea.parser.Parser;
/*     */ import org.slf4j.Logger;
/*     */ import org.slf4j.LoggerFactory;
/*     */ 
/*     */ public class JFXCompiler
/*     */   implements ICompiler
/*     */ {
/*  49 */   private static final Logger LOGGER = LoggerFactory.getLogger(JFXCompiler.class);
/*  50 */   private static String classpathAgent = null;
/*  51 */   private static String classpathJDT = null;
/*  52 */   private static String classpathConnector = null;
/*     */   public static final String JAVA_PATH_KEY = "jfxcompiler.java.path";
/*     */   private static final String JAVA_PATH_DEFAULT_VALUE = "java";
/*     */   static JFXCompiler instance;
/*  85 */   private static boolean running = false;
/*     */ 
/*     */   public static JFXCompiler getInstance()
/*     */   {
/*  78 */     if (instance == null) {
/*  79 */       instance = new JFXCompiler();
/*     */     }
/*  81 */     return instance;
/*     */   }
/*     */ 
/*     */   private static synchronized boolean tryToLock()
/*     */   {
/*  88 */     if (running) {
/*  89 */       LOGGER.debug("locked");
/*     */ 
/*  91 */       return false;
/*     */     }
/*  93 */     LOGGER.debug("lock ok");
/*     */ 
/*  95 */     running = true;
/*  96 */     return true;
/*     */   }
/*     */ 
/*     */   private static synchronized void unlock()
/*     */   {
/* 101 */     LOGGER.debug("unlock");
/* 102 */     running = false;
/*     */   }
/*     */ 
/*     */   public static String prepareClasspath(Class<?> clazz, String jarName)
/*     */   {
/* 107 */     String classpath = null;
/* 108 */     File libPath = new File(IOUtils.getRootPath().getPath() + File.separator + "lib");
/* 109 */     libPath.mkdirs();
/*     */     try {
/* 111 */       if (clazz.getProtectionDomain().getCodeSource() != null) {
/* 112 */         URL iMainURL = clazz.getProtectionDomain().getCodeSource().getLocation();
/*     */ 
/* 114 */         if (iMainURL.getProtocol().equals("file")) {
/* 115 */           String str = URLDecoder.decode(iMainURL.getFile(), "UTF-8");
/* 116 */           classpath = new File(str).getAbsolutePath();
/*     */         } else {
/* 118 */           LOGGER.debug("library...");
/* 119 */           File file = new File(libPath + File.separator + jarName);
/*     */ 
/* 121 */           FileOutputStream fileOutputStream = new FileOutputStream(file);
/* 122 */           if (returnURL(iMainURL, fileOutputStream)) {
/* 123 */             classpath = file.getPath();
/*     */           }
/* 125 */           fileOutputStream.close();
/* 126 */           LOGGER.debug("ready.");
/*     */         }
/*     */       }
/*     */     }
/*     */     catch (IOException exc) {
/* 131 */       LOGGER.error(exc.getMessage(), exc);
/*     */     }
/* 133 */     return classpath;
/*     */   }
/*     */ 
/*     */   private static boolean prepareLib() {
/* 137 */     if (classpathAgent == null) {
/* 138 */       classpathAgent = prepareClasspath(IStrategy.class, "dds2-agent.jar");
/*     */     }
/* 140 */     LOGGER.info("Agent classpath [" + classpathAgent + "]");
/*     */ 
/* 142 */     if (classpathJDT == null) {
/* 143 */       classpathJDT = prepareClasspath(Main.class, "ecj.jar");
/*     */     }
/* 145 */     LOGGER.info("JDT classpath [" + classpathJDT + "]");
/*     */ 
/* 147 */     if (classpathConnector == null) {
/* 148 */       classpathConnector = prepareClasspath(ConnectorStrategy.class, "connector.jar");
/*     */     }
/* 150 */     LOGGER.info("Connector classpath [" + classpathConnector + "]");
/*     */ 
/* 152 */     return (classpathAgent != null) && (classpathJDT != null) && (classpathConnector != null);
/*     */   }
/*     */ 
/*     */   public static boolean returnURL(URL url, OutputStream out)
/*     */   {
/* 157 */     InputStream urlIn = null;
/* 158 */     BufferedInputStream in = null;
/* 159 */     JarOutputStream jostream = null;
/*     */     try {
/* 161 */       urlIn = url.openStream();
/* 162 */       in = new BufferedInputStream(urlIn);
/*     */ 
/* 164 */       if (!IOUtils.isJarMagic(IOUtils.readMagic(in)))
/*     */       {
/* 166 */         gIn = new GZIPInputStream(in);
/* 167 */         jostream = new JarOutputStream(out);
/* 168 */         Pack200.Unpacker unpacker = Pack200.newUnpacker();
/*     */ 
/* 170 */         unpacker.unpack(gIn, jostream);
/*     */ 
/* 173 */         gIn.close();
/*     */       }
/*     */       else {
/* 176 */         jostream = new JarOutputStream(out);
/* 177 */         IOUtils.copyJarFile(new JarInputStream(in), jostream);
/*     */       }
/*     */ 
/* 180 */       GZIPInputStream gIn = 1;
/*     */       return gIn;
/*     */     }
/*     */     catch (Exception e)
/*     */     {
/* 182 */       LOGGER.error(e.getMessage(), e);
/*     */     } finally {
/* 184 */       if (jostream != null) {
/*     */         try {
/* 186 */           jostream.close();
/*     */         } catch (IOException e) {
/* 188 */           LOGGER.error(e.getMessage(), e);
/*     */         }
/*     */       }
/* 191 */       if (in != null) {
/*     */         try {
/* 193 */           in.close();
/*     */         } catch (IOException e) {
/* 195 */           LOGGER.error(e.getMessage(), e);
/*     */         }
/*     */       }
/* 198 */       if (urlIn != null) {
/*     */         try {
/* 200 */           urlIn.close();
/*     */         } catch (IOException e) {
/* 202 */           LOGGER.error(e.getMessage(), e);
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/* 207 */     return false;
/*     */   }
/*     */ 
/*     */   public static boolean compile(File srcFile, IConsole console)
/*     */   {
/* 212 */     boolean result = false;
/*     */ 
/* 215 */     boolean goodToGo = false;
/* 216 */     if (tryToLock()) {
/* 217 */       goodToGo = true;
/*     */     }
/*     */     else {
/* 220 */       for (int i = 0; i < 20; i++) {
/*     */         try {
/* 222 */           Thread.sleep(1000L);
/*     */         } catch (InterruptedException e) {
/* 224 */           LOGGER.info("Wait to compile", e);
/*     */         }
/* 226 */         if (tryToLock()) {
/* 227 */           goodToGo = true;
/* 228 */           break;
/*     */         }
/*     */       }
/*     */     }
/* 232 */     if (!goodToGo) {
/* 233 */       LOGGER.info("Another thread is compiling. Waited for too long.");
/* 234 */       return true;
/*     */     }
/*     */ 
/*     */     try
/*     */     {
/* 239 */       File tmpObfDir = null;
/* 240 */       File tmpCompileDir = null;
/*     */       try
/*     */       {
/* 243 */         PrintWriter errWriter = null;
/* 244 */         PrintWriter outWriter = null;
/*     */ 
/* 246 */         if (console != null) {
/* 247 */           errWriter = new CompilerWriter(console.getErr());
/* 248 */           outWriter = new CompilerWriter(console.getOut());
/*     */         }
/*     */ 
/* 251 */         outWriter.println("Compiling " + srcFile.getName());
/* 252 */         outWriter.flush();
/*     */ 
/* 254 */         if (!prepareLib()) {
/* 255 */           errWriter.println("Unable to compile. Please restart platform.\n");
/* 256 */           boolean bool1 = result;
/*     */ 
/* 406 */           if (tmpObfDir != null) {
/* 407 */             IOUtils.deleteDir(tmpObfDir);
/*     */           }
/* 409 */           if (tmpCompileDir != null) {
/* 410 */             IOUtils.deleteDir(tmpCompileDir);
/*     */           }
/*     */ 
/* 415 */           unlock(); return bool1;
/*     */         }
/* 259 */         String classname = srcFile.getName().substring(0, srcFile.getName().length() - 5);
/*     */ 
/* 261 */         tmpObfDir = new File(IOUtils.getRootPath().getPath() + File.separator + "tmp" + File.separator + "ofbuscation");
/*     */         boolean bool2;
/* 262 */         if (!IOUtils.recreateDir(tmpObfDir)) {
/* 263 */           LOGGER.info("Unable to compile. Failed to recreate: " + tmpObfDir);
/* 264 */           errWriter.println("Unable to compile. 3");
/* 265 */           bool2 = result;
/*     */ 
/* 406 */           if (tmpObfDir != null) {
/* 407 */             IOUtils.deleteDir(tmpObfDir);
/*     */           }
/* 409 */           if (tmpCompileDir != null) {
/* 410 */             IOUtils.deleteDir(tmpCompileDir);
/*     */           }
/*     */ 
/* 415 */           unlock(); return bool2;
/*     */         }
/* 268 */         tmpCompileDir = new File(IOUtils.getRootPath().getPath() + File.separator + "tmp" + File.separator + "compile");
/* 269 */         if (!IOUtils.recreateDir(tmpCompileDir)) {
/* 270 */           LOGGER.info("Unable to compile. Failed to recreate: " + tmpObfDir);
/* 271 */           errWriter.println("Unable to compile. 2");
/* 272 */           bool2 = result;
/*     */ 
/* 406 */           if (tmpObfDir != null) {
/* 407 */             IOUtils.deleteDir(tmpObfDir);
/*     */           }
/* 409 */           if (tmpCompileDir != null) {
/* 410 */             IOUtils.deleteDir(tmpCompileDir);
/*     */           }
/*     */ 
/* 415 */           unlock(); return bool2;
/*     */         }
/* 275 */         File tmpSrcFile = new File(tmpCompileDir + File.separator + srcFile.getName());
/* 276 */         if (!IOUtils.copyFiles(srcFile, tmpSrcFile)) {
/* 277 */           LOGGER.info("Unable to compile. Failed to recreate: " + tmpObfDir);
/* 278 */           errWriter.println("Unable to compile. 1");
/* 279 */           boolean bool3 = result;
/*     */ 
/* 406 */           if (tmpObfDir != null) {
/* 407 */             IOUtils.deleteDir(tmpObfDir);
/*     */           }
/* 409 */           if (tmpCompileDir != null) {
/* 410 */             IOUtils.deleteDir(tmpCompileDir);
/*     */           }
/*     */ 
/* 415 */           unlock(); return bool3;
/*     */         }
/* 282 */         File[] jars = prepareAdditionalLibraries(tmpSrcFile);
/* 283 */         String tmpClasspath = classpathAgent;
/* 284 */         if (jars.length > 0) {
/* 285 */           for (File file : jars) {
/* 286 */             if (!file.exists()) {
/* 287 */               console.getErr().println("File " + file.getAbsolutePath() + " not found.");
/* 288 */               boolean bool4 = result;
/*     */ 
/* 406 */               if (tmpObfDir != null) {
/* 407 */                 IOUtils.deleteDir(tmpObfDir);
/*     */               }
/* 409 */               if (tmpCompileDir != null) {
/* 410 */                 IOUtils.deleteDir(tmpCompileDir);
/*     */               }
/*     */ 
/* 415 */               unlock(); return bool4;
/*     */             }
/* 290 */             tmpClasspath = tmpClasspath + File.pathSeparatorChar + file.getAbsolutePath();
/*     */           }
/*     */         }
/*     */ 
/* 294 */         boolean success = false;
/*     */         try
/*     */         {
/* 299 */           List command = new ArrayList();
/* 300 */           command.add(System.getProperty("jfxcompiler.java.path", "java"));
/* 301 */           command.add("-classpath");
/* 302 */           command.add(classpathJDT);
/*     */ 
/* 306 */           command.add("org.eclipse.jdt.internal.compiler.batch.Main");
/* 307 */           command.add("-classpath");
/* 308 */           command.add(tmpClasspath);
/* 309 */           command.add("-classpath");
/* 310 */           command.add(classpathConnector);
/* 311 */           command.add("-encoding");
/* 312 */           command.add("UTF8");
/* 313 */           command.add("-sourcepath");
/* 314 */           command.add(tmpSrcFile.getParent());
/* 315 */           command.add("-warn:none");
/* 316 */           command.add("-source");
/* 317 */           command.add("1.6");
/* 318 */           command.add("-target");
/* 319 */           command.add("1.6");
/*     */ 
/* 321 */           command.add(tmpSrcFile.getPath());
/* 322 */           LOGGER.debug("Compile command : {}", command);
/*     */ 
/* 324 */           ProcessBuilder processBuilder = new ProcessBuilder(command);
/*     */ 
/* 331 */           Process process = processBuilder.start();
/*     */ 
/* 333 */           Object inputReader = new ProcessInputStreamThread(process.getInputStream(), console.getOut());
/* 334 */           ((Thread)inputReader).start();
/*     */ 
/* 336 */           Thread errorReader = new ProcessInputStreamThread(process.getErrorStream(), console.getErr());
/* 337 */           errorReader.start();
/*     */ 
/* 339 */           success = process.waitFor() == 0;
/*     */         } catch (Exception e) {
/* 341 */           LOGGER.error(e.getMessage(), e);
/*     */         }
/*     */ 
/* 344 */         if (success) {
/* 345 */           String name = srcFile.getName();
/* 346 */           JavaGuard2.start(tmpCompileDir, tmpObfDir, name.substring(0, name.lastIndexOf(".java")));
/*     */         }
/*     */ 
/* 349 */         JFXPack pack = null;
/* 350 */         if (success)
/*     */         {
/* 352 */           File classFile = new File(tmpObfDir, classname + ".class");
/*     */           try
/*     */           {
/* 357 */             pack = JFXPack.buildFromClass(classFile, jars);
/*     */ 
/* 359 */             File jxfFile = new File(srcFile.getParent() + File.separator + classFile.getName().substring(0, classFile.getName().lastIndexOf(46)) + ".jfx");
/*     */ 
/* 361 */             Class targetClass = pack.getTargetClass(true);
/* 362 */             if (targetClass == null) {
/* 363 */               throw new Exception("Cannot load target class");
/*     */             }
/*     */             try
/*     */             {
/* 367 */               targetClass.getDeclaredConstructor(new Class[0]);
/*     */             } catch (NoSuchMethodException e) {
/* 369 */               throw new Exception("Class doesn't have default constructor");
/*     */             }
/*     */ 
/* 372 */             if ((!IStrategy.class.isAssignableFrom(targetClass)) && (!IIndicator.class.isAssignableFrom(targetClass))) {
/* 373 */               throw new Exception("Class doesn't implement IStrategy or IIndicator interface");
/*     */             }
/*     */ 
/* 376 */             if (jxfFile.exists()) {
/* 377 */               jxfFile.delete();
/*     */             }
/*     */ 
/* 380 */             if (!jxfFile.exists()) {
/* 381 */               pack.write(jxfFile);
/* 382 */               String type = IStrategy.class.isAssignableFrom(targetClass) ? "Strategy" : "Indicator";
/* 383 */               outWriter.println(String.format("Compilation successful. %s ID: %s", new Object[] { type, pack.getMD5HexString() }));
/* 384 */               outWriter.flush();
/* 385 */               result = true;
/*     */             } else {
/* 387 */               errWriter.println("File " + jxfFile.getName() + " is locked");
/* 388 */               outWriter.flush();
/*     */             }
/*     */           } catch (Exception ex) {
/* 391 */             LOGGER.info(ex.getMessage(), ex);
/*     */ 
/* 393 */             errWriter.println(ex.getMessage());
/* 394 */             errWriter.flush();
/* 395 */             outWriter.flush();
/*     */           }
/*     */         } else {
/* 398 */           errWriter.println("\n");
/* 399 */           errWriter.flush();
/*     */         }
/*     */       }
/*     */       catch (RuntimeException e) {
/* 403 */         LOGGER.info(e.getMessage(), e);
/* 404 */         throw e;
/*     */       } finally {
/* 406 */         if (tmpObfDir != null) {
/* 407 */           IOUtils.deleteDir(tmpObfDir);
/*     */         }
/* 409 */         if (tmpCompileDir != null)
/* 410 */           IOUtils.deleteDir(tmpCompileDir);
/*     */       }
/*     */     }
/*     */     finally
/*     */     {
/* 415 */       unlock();
/*     */     }
/*     */ 
/* 418 */     return result;
/*     */   }
/*     */ 
/*     */   public void compileForErrors(String text, Parser parser) {
/* 422 */     ShowErrorsThread th = new ShowErrorsThread(text, parser);
/* 423 */     th.start();
/*     */   }
/*     */ 
/*     */   String getClassName(String text) {
/* 427 */     String className = "noname";
/* 428 */     int classIndex = text.indexOf("class ");
/* 429 */     if (classIndex < 0) {
/* 430 */       return className;
/*     */     }
/* 432 */     char[] charr = text.toCharArray();
/* 433 */     int classNameStart = ParseUtils.jumpOverVoidSpace(charr, classIndex + 5, text.length());
/* 434 */     if (classNameStart < 0) {
/* 435 */       return className;
/*     */     }
/* 437 */     Matcher m = Pattern.compile("[A-Z_]([A-Za-z0-9_])*").matcher(text);
/* 438 */     if ((!m.find(classNameStart)) || (m.start() != classNameStart))
/* 439 */       return className;
/* 440 */     int classNameEnd = m.end();
/*     */ 
/* 442 */     className = text.substring(classNameStart, classNameEnd);
/* 443 */     return className;
/*     */   }
/*     */ 
/*     */   private static File[] prepareAdditionalLibraries(File tmpSrcFile)
/*     */   {
/* 646 */     List listFile = new ArrayList();
/* 647 */     byte[] b = StratUtils.readFile(tmpSrcFile.getAbsolutePath());
/* 648 */     String allPath = new String(b);
/*     */     try
/*     */     {
/* 651 */       allPath = allPath.replaceAll("(?:/\\*(?:[^*]|(?:\\*+[^*/]))*\\*+/)|(?://.*)", " ");
/*     */     }
/*     */     catch (StackOverflowError stackOverflowError) {
/*     */     }
/* 655 */     Pattern pattern = Pattern.compile("@Library\\(\"(.*?)\"\\)");
/* 656 */     Matcher matcher = pattern.matcher(allPath);
/* 657 */     String libPath = null;
/* 658 */     if (matcher.find()) {
/* 659 */       libPath = matcher.group(1);
/* 660 */       String[] jarPath = libPath.split(File.pathSeparator);
/* 661 */       for (String path : jarPath) {
/* 662 */         File theFile = null;
/* 663 */         if ((path.indexOf("/") != -1) || (path.indexOf("\\") != -1))
/*     */         {
/* 665 */           theFile = new File(path);
/*     */         }
/*     */         else {
/* 668 */           path = FilePathManager.getInstance().getFilesForStrategiesDir() + File.separator + path;
/* 669 */           theFile = new File(path);
/*     */         }
/* 671 */         listFile.add(theFile);
/*     */       }
/*     */     }
/* 674 */     return (File[])listFile.toArray(new File[0]);
/*     */   }
/*     */ 
/*     */   private class ShowErrorsThread extends Thread
/*     */   {
/*     */     Parser parser;
/*     */     List<CompileError> errors;
/*     */     String text;
/*     */ 
/*     */     ShowErrorsThread(String text, Parser parser)
/*     */     {
/* 454 */       this.parser = parser;
/* 455 */       this.errors = new LinkedList();
/* 456 */       this.text = text;
/*     */     }
/*     */ 
/*     */     public void run()
/*     */     {
/* 462 */       boolean goodToGo = false;
/* 463 */       if (JFXCompiler.access$000()) {
/* 464 */         goodToGo = true;
/*     */       }
/* 466 */       if (!goodToGo) {
/* 467 */         JFXCompiler.LOGGER.info("Another thread is compiling. Do nothing.");
/* 468 */         return;
/*     */       }
/*     */ 
/*     */       try
/*     */       {
/* 473 */         JFXCompiler.LOGGER.debug("Compiling ");
/*     */ 
/* 475 */         String tmpFileName = JFXCompiler.this.getClassName(this.text) + ".java";
/*     */ 
/* 477 */         if (!JFXCompiler.access$200()) { JFXCompiler.LOGGER.debug("Unable to compile. Please restart platform.\n");
/*     */           return;
/*     */         }
/* 482 */         File tmpDir = new File(IOUtils.getRootPath().getPath() + File.separator + "tmp" + File.separator + "compileToShowErrors");
/* 483 */         if (!IOUtils.recreateDir(tmpDir)) { JFXCompiler.LOGGER.debug("Unable to compile.\n");
/*     */           return;
/*     */         }
/* 488 */         File tmpSrcFile = new File(tmpDir + File.separator + tmpFileName);
/*     */ 
/* 490 */         if (!IOUtils.writeFile(this.text, tmpSrcFile)) { JFXCompiler.LOGGER.error("Unable to compile.\n");
/*     */           return;
/*     */         }
/* 495 */         File[] jars = JFXCompiler.access$300(tmpSrcFile);
/* 496 */         String tmpClasspath = JFXCompiler.classpathAgent;
/* 497 */         if (jars.length > 0) {
/* 498 */           for (File file : jars) {
/* 499 */             if (!file.exists()) { JFXCompiler.LOGGER.error("File " + file.getAbsolutePath() + " not found.");
/*     */               return; }
/* 503 */             tmpClasspath = tmpClasspath + File.pathSeparatorChar + file.getAbsolutePath();
/*     */           }
/*     */         }
/*     */         try
/*     */         {
/* 508 */           List command = new ArrayList();
/* 509 */           command.add(System.getProperty("jfxcompiler.java.path", "java"));
/* 510 */           command.add("-classpath");
/* 511 */           command.add(JFXCompiler.classpathJDT);
/*     */ 
/* 513 */           command.add("org.eclipse.jdt.internal.compiler.batch.Main");
/* 514 */           command.add("-classpath");
/* 515 */           command.add(tmpClasspath);
/* 516 */           command.add("-classpath");
/* 517 */           command.add(JFXCompiler.classpathConnector);
/* 518 */           command.add("-encoding");
/* 519 */           command.add("UTF8");
/* 520 */           command.add("-sourcepath");
/* 521 */           command.add(tmpSrcFile.getParent());
/* 522 */           command.add("-warn:none");
/* 523 */           command.add("-source");
/* 524 */           command.add("1.6");
/* 525 */           command.add("-target");
/* 526 */           command.add("1.6");
/* 527 */           command.add("-proceedOnError");
/* 528 */           command.add("-maxProblems");
/* 529 */           command.add("100");
/*     */ 
/* 531 */           command.add(tmpSrcFile.getPath());
/* 532 */           JFXCompiler.LOGGER.debug("Compile command : {}", command);
/*     */ 
/* 534 */           ProcessBuilder processBuilder = new ProcessBuilder(command);
/* 535 */           Process process = processBuilder.start();
/*     */ 
/* 537 */           showErrors(process.getErrorStream());
/* 538 */           process.waitFor();
/*     */         }
/*     */         catch (Exception e) {
/* 541 */           JFXCompiler.LOGGER.error(e.getMessage(), e);
/*     */         }
/*     */ 
/* 544 */         IOUtils.recreateDir(tmpDir);
/*     */       }
/*     */       catch (RuntimeException e) {
/* 547 */         JFXCompiler.LOGGER.error(e.getMessage(), e);
/*     */       }
/*     */       finally
/*     */       {
/* 551 */         JFXCompiler.access$700();
/*     */       }
/*     */     }
/*     */ 
/*     */     void showErrors(InputStream is)
/*     */     {
/* 557 */       BufferedReader reader = new BufferedReader(new InputStreamReader(new BufferedInputStream(is)));
/*     */       try
/*     */       {
/* 560 */         int state = 0;
/* 561 */         int errStart = -1;
/* 562 */         int errLen = -1;
/* 563 */         int errLineNr = 0;
/*     */ 
/* 565 */         String line = reader.readLine();
/* 566 */         while (line != null)
/*     */         {
/* 568 */           if (line.startsWith("-")) {
/* 569 */             state = 1;
/*     */ 
/* 571 */             errStart = -1;
/* 572 */             errLen = -1;
/* 573 */             errLineNr = 0;
/*     */           }
/* 575 */           else if (state == 1) {
/* 576 */             state = 0;
/* 577 */             if (!line.contains("ERROR"))
/*     */               continue;
/* 579 */             int start = line.indexOf("line") + 5;
/* 580 */             int end = line.indexOf(')');
/*     */             try
/*     */             {
/* 583 */               errLineNr = Integer.valueOf(line.substring(start, end)).intValue() - 1;
/*     */             } catch (NumberFormatException e) {
/* 585 */               JFXCompiler.LOGGER.debug(e.getMessage(), e);
/*     */ 
/* 640 */               IOUtils.closeQuietly(reader); return;
/*     */             }
/*     */             catch (StringIndexOutOfBoundsException e)
/*     */             {
/* 588 */               JFXCompiler.LOGGER.debug(e.getMessage(), e);
/*     */ 
/* 640 */               IOUtils.closeQuietly(reader); return;
/*     */             }
/* 591 */             state = 2;
/* 592 */           } else if (state == 2) {
/* 593 */             state = 3;
/* 594 */           } else if (state == 3) {
/* 595 */             state = 0;
/*     */ 
/* 597 */             int i = 1;
/* 598 */             int indexInDocument = 0;
/* 599 */             boolean found = false;
/* 600 */             for (; i < line.length(); i++) {
/* 601 */               char ch = line.charAt(i);
/* 602 */               if (ch == '^') {
/* 603 */                 found = true;
/* 604 */                 break;
/*     */               }
/* 606 */               indexInDocument++;
/*     */             }
/*     */ 
/* 610 */             errStart = indexInDocument;
/*     */ 
/* 612 */             for (; i < line.length(); i++) {
/* 613 */               char ch = line.charAt(i);
/* 614 */               if (ch != '^') {
/*     */                 break;
/*     */               }
/* 617 */               indexInDocument++;
/*     */             }
/*     */ 
/* 620 */             errLen = indexInDocument - errStart;
/*     */ 
/* 622 */             if (found) {
/* 623 */               state = 4;
/*     */             }
/*     */           }
/* 626 */           else if (state == 4) {
/* 627 */             state = 0;
/* 628 */             this.errors.add(new CompileError(errLineNr, errStart, errLen, line));
/*     */           }
/*     */ 
/* 631 */           line = reader.readLine();
/*     */         }
/*     */ 
/* 634 */         this.parser.processParseResult(this.errors);
/*     */       }
/*     */       catch (IOException ioe) {
/* 637 */         JFXCompiler.LOGGER.debug(ioe.getMessage(), ioe);
/*     */       }
/*     */       finally {
/* 640 */         IOUtils.closeQuietly(reader);
/*     */       }
/*     */     }
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.dds2.greed.agent.compiler.JFXCompiler
 * JD-Core Version:    0.6.0
 */