/*      */ package org.eclipse.jdt.internal.compiler.apt.util;
/*      */ 
/*      */ import java.io.File;
/*      */ import java.io.IOException;
/*      */ import java.io.PrintStream;
/*      */ import java.net.MalformedURLException;
/*      */ import java.net.URI;
/*      */ import java.net.URISyntaxException;
/*      */ import java.net.URL;
/*      */ import java.net.URLClassLoader;
/*      */ import java.nio.charset.Charset;
/*      */ import java.text.MessageFormat;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Arrays;
/*      */ import java.util.HashMap;
/*      */ import java.util.Iterator;
/*      */ import java.util.Locale;
/*      */ import java.util.Map;
/*      */ import java.util.MissingResourceException;
/*      */ import java.util.ResourceBundle;
/*      */ import java.util.Set;
/*      */ import java.util.StringTokenizer;
/*      */ import java.util.zip.ZipException;
/*      */ import javax.tools.FileObject;
/*      */ import javax.tools.JavaFileManager.Location;
/*      */ import javax.tools.JavaFileObject;
/*      */ import javax.tools.JavaFileObject.Kind;
/*      */ import javax.tools.StandardJavaFileManager;
/*      */ import javax.tools.StandardLocation;
/*      */ import org.eclipse.jdt.internal.compiler.batch.FileSystem;
/*      */ import org.eclipse.jdt.internal.compiler.batch.FileSystem.Classpath;
/*      */ import org.eclipse.jdt.internal.compiler.batch.Main;
/*      */ import org.eclipse.jdt.internal.compiler.batch.Main.ResourceBundleFactory;
/*      */ import org.eclipse.jdt.internal.compiler.env.AccessRule;
/*      */ import org.eclipse.jdt.internal.compiler.env.AccessRuleSet;
/*      */ 
/*      */ public class EclipseFileManager
/*      */   implements StandardJavaFileManager
/*      */ {
/*      */   private static final String NO_EXTENSION = "";
/*      */   static final int HAS_EXT_DIRS = 1;
/*      */   static final int HAS_BOOTCLASSPATH = 2;
/*      */   static final int HAS_ENDORSED_DIRS = 4;
/*      */   static final int HAS_PROCESSORPATH = 8;
/*      */   Map<File, Archive> archivesCache;
/*      */   Charset charset;
/*      */   Locale locale;
/*      */   Map<String, Iterable<? extends File>> locations;
/*      */   int flags;
/*      */   public ResourceBundle bundle;
/*      */ 
/*      */   public EclipseFileManager(Locale locale, Charset charset)
/*      */   {
/*   66 */     this.locale = (locale == null ? Locale.getDefault() : locale);
/*   67 */     this.charset = (charset == null ? Charset.defaultCharset() : charset);
/*   68 */     this.locations = new HashMap();
/*   69 */     this.archivesCache = new HashMap();
/*      */     try {
/*   71 */       setLocation(StandardLocation.PLATFORM_CLASS_PATH, getDefaultBootclasspath());
/*   72 */       Iterable defaultClasspath = getDefaultClasspath();
/*   73 */       setLocation(StandardLocation.CLASS_PATH, defaultClasspath);
/*   74 */       setLocation(StandardLocation.ANNOTATION_PROCESSOR_PATH, defaultClasspath);
/*      */     }
/*      */     catch (IOException localIOException) {
/*      */     }
/*      */     try {
/*   79 */       this.bundle = Main.ResourceBundleFactory.getBundle(this.locale);
/*      */     } catch (MissingResourceException localMissingResourceException) {
/*   81 */       System.out.println("Missing resource : " + "org.eclipse.jdt.internal.compiler.batch.messages".replace('.', '/') + ".properties for locale " + locale);
/*      */     }
/*      */   }
/*      */ 
/*      */   private void addFiles(File[][] jars, ArrayList<File> files) {
/*   86 */     if (jars != null)
/*   87 */       for (File[] currentJars : jars)
/*   88 */         if (currentJars != null)
/*   89 */           for (File currentJar : currentJars)
/*   90 */             if (currentJar.exists())
/*   91 */               files.add(currentJar);
/*      */   }
/*      */ 
/*      */   private void addFilesFrom(File javaHome, String propertyName, String defaultPath, ArrayList<File> files)
/*      */   {
/*  101 */     String extdirsStr = System.getProperty(propertyName);
/*  102 */     File[] directoriesToCheck = (File[])null;
/*  103 */     if (extdirsStr == null) {
/*  104 */       if (javaHome != null)
/*  105 */         directoriesToCheck = new File[] { new File(javaHome, defaultPath) };
/*      */     }
/*      */     else {
/*  108 */       StringTokenizer tokenizer = new StringTokenizer(extdirsStr, File.pathSeparator);
/*  109 */       ArrayList paths = new ArrayList();
/*  110 */       while (tokenizer.hasMoreTokens()) {
/*  111 */         paths.add(tokenizer.nextToken());
/*      */       }
/*  113 */       if (paths.size() != 0) {
/*  114 */         directoriesToCheck = new File[paths.size()];
/*  115 */         for (int i = 0; i < directoriesToCheck.length; i++) {
/*  116 */           directoriesToCheck[i] = new File((String)paths.get(i));
/*      */         }
/*      */       }
/*      */     }
/*  120 */     if (directoriesToCheck != null)
/*  121 */       addFiles(Main.getLibrariesFiles(directoriesToCheck), files);
/*      */   }
/*      */ 
/*      */   public void close()
/*      */     throws IOException
/*      */   {
/*  130 */     this.locations = null;
/*  131 */     for (Archive archive : this.archivesCache.values())
/*  132 */       archive.close();
/*      */   }
/*      */ 
/*      */   private void collectAllMatchingFiles(File file, String normalizedPackageName, Set<JavaFileObject.Kind> kinds, boolean recurse, ArrayList<JavaFileObject> collector)
/*      */   {
/*      */     JavaFileObject.Kind kind;
/*  137 */     if (!isArchive(file))
/*      */     {
/*  139 */       File currentFile = new File(file, normalizedPackageName);
/*  140 */       if (!currentFile.exists()) return;
/*      */       try
/*      */       {
/*  143 */         path = currentFile.getCanonicalPath();
/*      */       }
/*      */       catch (IOException localIOException)
/*      */       {
/*      */         String path;
/*  145 */         return;
/*      */       }
/*      */       String path;
/*  147 */       if (File.separatorChar == '/') {
/*  148 */         if (!path.endsWith(normalizedPackageName)) return; 
/*      */       }
/*  149 */       else if (!path.endsWith(normalizedPackageName.replace('/', File.separatorChar))) return;
/*  150 */       File[] files = currentFile.listFiles();
/*  151 */       if (files != null)
/*      */       {
/*  153 */         for (File f : files) {
/*  154 */           if ((f.isDirectory()) && (recurse)) {
/*  155 */             collectAllMatchingFiles(file, normalizedPackageName + '/' + f.getName(), kinds, recurse, collector);
/*      */           } else {
/*  157 */             JavaFileObject.Kind kind = getKind(f);
/*  158 */             if (kinds.contains(kind)) {
/*  159 */               collector.add(new EclipseFileObject(normalizedPackageName + currentFile.getName(), currentFile.toURI(), kind, this.charset));
/*      */             }
/*      */           }
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/*  166 */       kind = getKind(file);
/*  167 */       if (kinds.contains(kind))
/*  168 */         collector.add(new EclipseFileObject(normalizedPackageName + currentFile.getName(), currentFile.toURI(), kind, this.charset));
/*      */     }
/*      */     else {
/*  171 */       Archive archive = getArchive(file);
/*  172 */       String key = normalizedPackageName;
/*  173 */       if (!normalizedPackageName.endsWith("/"))
/*  174 */         key = key + '/';
/*      */       Object types;
/*  177 */       if (recurse) {
/*  178 */         for (String packageName : archive.allPackages()) {
/*  179 */           if (packageName.startsWith(key)) {
/*  180 */             types = archive.getTypes(packageName);
/*  181 */             if (types != null)
/*  182 */               for (??? = ((ArrayList)types).iterator(); ((Iterator)???).hasNext(); ) { String typeName = (String)((Iterator)???).next();
/*  183 */                 JavaFileObject.Kind kind = getKind(getExtension(typeName));
/*  184 */                 if (kinds.contains(kind))
/*  185 */                   collector.add(archive.getArchiveFileObject(packageName + typeName, this.charset));
/*      */               }
/*      */           }
/*      */         }
/*      */       }
/*      */       else
/*      */       {
/*  192 */         ArrayList types = archive.getTypes(key);
/*  193 */         if (types != null)
/*  194 */           for (types = types.iterator(); ((Iterator)types).hasNext(); ) { String typeName = (String)((Iterator)types).next();
/*  195 */             JavaFileObject.Kind kind = getKind(typeName);
/*  196 */             if (kinds.contains(kind))
/*  197 */               collector.add(archive.getArchiveFileObject(normalizedPackageName + typeName, this.charset));
/*      */           }
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   private Iterable<? extends File> concatFiles(Iterable<? extends File> iterable, Iterable<? extends File> iterable2)
/*      */   {
/*  206 */     ArrayList list = new ArrayList();
/*  207 */     if (iterable2 == null) return iterable;
/*  208 */     for (Iterator iterator = iterable.iterator(); iterator.hasNext(); ) {
/*  209 */       list.add((File)iterator.next());
/*      */     }
/*  211 */     for (Iterator iterator = iterable2.iterator(); iterator.hasNext(); ) {
/*  212 */       list.add((File)iterator.next());
/*      */     }
/*  214 */     return list;
/*      */   }
/*      */ 
/*      */   public void flush()
/*      */     throws IOException
/*      */   {
/*  221 */     for (Archive archive : this.archivesCache.values())
/*  222 */       archive.flush();
/*      */   }
/*      */ 
/*      */   private Archive getArchive(File f)
/*      */   {
/*  228 */     Archive archive = (Archive)this.archivesCache.get(f);
/*  229 */     if (archive == null)
/*      */     {
/*  231 */       if (f.exists()) {
/*      */         try {
/*  233 */           archive = new Archive(f);
/*      */         }
/*      */         catch (ZipException localZipException) {
/*      */         }
/*      */         catch (IOException localIOException) {
/*      */         }
/*  239 */         if (archive != null)
/*  240 */           this.archivesCache.put(f, archive);
/*      */         else
/*  242 */           this.archivesCache.put(f, Archive.UNKNOWN_ARCHIVE);
/*      */       }
/*      */       else {
/*  245 */         this.archivesCache.put(f, Archive.UNKNOWN_ARCHIVE);
/*      */       }
/*      */     }
/*  248 */     return archive;
/*      */   }
/*      */ 
/*      */   public ClassLoader getClassLoader(JavaFileManager.Location location)
/*      */   {
/*  255 */     Iterable files = getLocation(location);
/*  256 */     if (files == null)
/*      */     {
/*  258 */       return null;
/*      */     }
/*  260 */     ArrayList allURLs = new ArrayList();
/*  261 */     for (File f : files) {
/*      */       try {
/*  263 */         allURLs.add(f.toURI().toURL());
/*      */       }
/*      */       catch (MalformedURLException e) {
/*  266 */         throw new RuntimeException(e);
/*      */       }
/*      */     }
/*  269 */     URL[] result = new URL[allURLs.size()];
/*  270 */     return new URLClassLoader((URL[])allURLs.toArray(result), getClass().getClassLoader());
/*      */   }
/*      */ 
/*      */   private Iterable<? extends File> getPathsFrom(String path) {
/*  274 */     ArrayList paths = new ArrayList();
/*  275 */     ArrayList files = new ArrayList();
/*      */     try {
/*  277 */       processPathEntries(4, paths, path, this.charset.toString(), false, false);
/*      */     } catch (IllegalArgumentException localIllegalArgumentException) {
/*  279 */       return null;
/*      */     }
/*  281 */     for (FileSystem.Classpath classpath : paths) {
/*  282 */       files.add(new File(classpath.getPath()));
/*      */     }
/*  284 */     return files;
/*      */   }
/*      */ 
/*      */   Iterable<? extends File> getDefaultBootclasspath() {
/*  288 */     ArrayList files = new ArrayList();
/*  289 */     String javaversion = System.getProperty("java.version");
/*  290 */     if ((javaversion != null) && (!javaversion.startsWith("1.6")))
/*      */     {
/*  292 */       return null;
/*      */     }
/*      */ 
/*  298 */     String javaHome = System.getProperty("java.home");
/*  299 */     File javaHomeFile = null;
/*  300 */     if (javaHome != null) {
/*  301 */       javaHomeFile = new File(javaHome);
/*  302 */       if (!javaHomeFile.exists()) {
/*  303 */         javaHomeFile = null;
/*      */       }
/*      */     }
/*  306 */     addFilesFrom(javaHomeFile, "java.endorsed.dirs", "/lib/endorsed", files);
/*  307 */     if (javaHomeFile != null) {
/*  308 */       File[] directoriesToCheck = (File[])null;
/*  309 */       if (System.getProperty("os.name").startsWith("Mac")) {
/*  310 */         directoriesToCheck = new File[] { new File(javaHomeFile, "../Classes") };
/*      */       }
/*      */       else {
/*  313 */         directoriesToCheck = new File[] { new File(javaHomeFile, "lib") };
/*      */       }
/*      */ 
/*  316 */       File[][] jars = Main.getLibrariesFiles(directoriesToCheck);
/*  317 */       addFiles(jars, files);
/*      */     }
/*  319 */     addFilesFrom(javaHomeFile, "java.ext.dirs", "/lib/ext", files);
/*  320 */     return files;
/*      */   }
/*      */ 
/*      */   Iterable<? extends File> getDefaultClasspath()
/*      */   {
/*  325 */     ArrayList files = new ArrayList();
/*  326 */     String classProp = System.getProperty("java.class.path");
/*  327 */     if ((classProp == null) || (classProp.length() == 0)) {
/*  328 */       return null;
/*      */     }
/*  330 */     StringTokenizer tokenizer = new StringTokenizer(classProp, File.pathSeparator);
/*      */ 
/*  332 */     while (tokenizer.hasMoreTokens()) {
/*  333 */       String token = tokenizer.nextToken();
/*  334 */       File file = new File(token);
/*  335 */       if (file.exists()) {
/*  336 */         files.add(file);
/*      */       }
/*      */     }
/*      */ 
/*  340 */     return files;
/*      */   }
/*      */ 
/*      */   private Iterable<? extends File> getEndorsedDirsFrom(String path) {
/*  344 */     ArrayList paths = new ArrayList();
/*  345 */     ArrayList files = new ArrayList();
/*      */     try {
/*  347 */       processPathEntries(4, paths, path, this.charset.toString(), false, false);
/*      */     } catch (IllegalArgumentException localIllegalArgumentException) {
/*  349 */       return null;
/*      */     }
/*  351 */     for (FileSystem.Classpath classpath : paths) {
/*  352 */       files.add(new File(classpath.getPath()));
/*      */     }
/*  354 */     return files;
/*      */   }
/*      */ 
/*      */   private Iterable<? extends File> getExtdirsFrom(String path) {
/*  358 */     ArrayList paths = new ArrayList();
/*  359 */     ArrayList files = new ArrayList();
/*      */     try {
/*  361 */       processPathEntries(4, paths, path, this.charset.toString(), false, false);
/*      */     } catch (IllegalArgumentException localIllegalArgumentException) {
/*  363 */       return null;
/*      */     }
/*  365 */     for (FileSystem.Classpath classpath : paths) {
/*  366 */       files.add(new File(classpath.getPath()));
/*      */     }
/*  368 */     return files;
/*      */   }
/*      */ 
/*      */   private String getExtension(File file) {
/*  372 */     String name = file.getName();
/*  373 */     return getExtension(name);
/*      */   }
/*      */   private String getExtension(String name) {
/*  376 */     int index = name.lastIndexOf('.');
/*  377 */     if (index == -1) {
/*  378 */       return "";
/*      */     }
/*  380 */     return name.substring(index);
/*      */   }
/*      */ 
/*      */   public FileObject getFileForInput(JavaFileManager.Location location, String packageName, String relativeName)
/*      */     throws IOException
/*      */   {
/*  387 */     Iterable files = getLocation(location);
/*  388 */     if (files == null) {
/*  389 */       throw new IllegalArgumentException("Unknown location : " + location);
/*      */     }
/*  391 */     String normalizedFileName = normalized(packageName) + '/' + relativeName.replace('\\', '/');
/*  392 */     for (File file : files) {
/*  393 */       if (file.isDirectory())
/*      */       {
/*  395 */         File f = new File(file, normalizedFileName);
/*  396 */         if (f.exists())
/*  397 */           return new EclipseFileObject(packageName + File.separator + relativeName, f.toURI(), getKind(f), this.charset);
/*      */       }
/*      */       else
/*      */       {
/*  401 */         if (!isArchive(file))
/*      */           continue;
/*  403 */         Archive archive = getArchive(file);
/*  404 */         if ((archive != Archive.UNKNOWN_ARCHIVE) && 
/*  405 */           (archive.contains(normalizedFileName))) {
/*  406 */           return archive.getArchiveFileObject(normalizedFileName, this.charset);
/*      */         }
/*      */       }
/*      */     }
/*      */ 
/*  411 */     return null;
/*      */   }
/*      */ 
/*      */   public FileObject getFileForOutput(JavaFileManager.Location location, String packageName, String relativeName, FileObject sibling)
/*      */     throws IOException
/*      */   {
/*  419 */     Iterable files = getLocation(location);
/*  420 */     if (files == null) {
/*  421 */       throw new IllegalArgumentException("Unknown location : " + location);
/*      */     }
/*  423 */     Iterator iterator = files.iterator();
/*  424 */     if (iterator.hasNext()) {
/*  425 */       File file = (File)iterator.next();
/*  426 */       String normalizedFileName = normalized(packageName) + '/' + relativeName.replace('\\', '/');
/*  427 */       File f = new File(file, normalizedFileName);
/*  428 */       return new EclipseFileObject(packageName + File.separator + relativeName, f.toURI(), getKind(f), this.charset);
/*      */     }
/*  430 */     throw new IllegalArgumentException("location is empty : " + location);
/*      */   }
/*      */ 
/*      */   public JavaFileObject getJavaFileForInput(JavaFileManager.Location location, String className, JavaFileObject.Kind kind)
/*      */     throws IOException
/*      */   {
/*  438 */     if ((kind != JavaFileObject.Kind.CLASS) && (kind != JavaFileObject.Kind.SOURCE)) {
/*  439 */       throw new IllegalArgumentException("Invalid kind : " + kind);
/*      */     }
/*  441 */     Iterable files = getLocation(location);
/*  442 */     if (files == null) {
/*  443 */       throw new IllegalArgumentException("Unknown location : " + location);
/*      */     }
/*  445 */     String normalizedFileName = normalized(className);
/*  446 */     normalizedFileName = normalizedFileName + kind.extension;
/*  447 */     for (File file : files) {
/*  448 */       if (file.isDirectory())
/*      */       {
/*  450 */         File f = new File(file, normalizedFileName);
/*  451 */         if (f.exists())
/*  452 */           return new EclipseFileObject(className, f.toURI(), kind, this.charset);
/*      */       }
/*      */       else
/*      */       {
/*  456 */         if (!isArchive(file))
/*      */           continue;
/*  458 */         Archive archive = getArchive(file);
/*  459 */         if ((archive != Archive.UNKNOWN_ARCHIVE) && 
/*  460 */           (archive.contains(normalizedFileName))) {
/*  461 */           return archive.getArchiveFileObject(normalizedFileName, this.charset);
/*      */         }
/*      */       }
/*      */     }
/*      */ 
/*  466 */     return null;
/*      */   }
/*      */ 
/*      */   public JavaFileObject getJavaFileForOutput(JavaFileManager.Location location, String className, JavaFileObject.Kind kind, FileObject sibling)
/*      */     throws IOException
/*      */   {
/*  474 */     if ((kind != JavaFileObject.Kind.CLASS) && (kind != JavaFileObject.Kind.SOURCE)) {
/*  475 */       throw new IllegalArgumentException("Invalid kind : " + kind);
/*      */     }
/*  477 */     Iterable files = getLocation(location);
/*  478 */     if (files == null) {
/*  479 */       if ((!location.equals(StandardLocation.CLASS_OUTPUT)) && 
/*  480 */         (!location.equals(StandardLocation.SOURCE_OUTPUT))) {
/*  481 */         throw new IllegalArgumentException("Unknown location : " + location);
/*      */       }
/*  483 */       if (sibling != null) {
/*  484 */         String normalizedFileName = normalized(className);
/*  485 */         int index = normalizedFileName.lastIndexOf('/');
/*  486 */         if (index != -1) {
/*  487 */           normalizedFileName = normalizedFileName.substring(index + 1);
/*      */         }
/*  489 */         normalizedFileName = normalizedFileName + kind.extension;
/*  490 */         URI uri = sibling.toUri();
/*  491 */         URI uri2 = null;
/*      */         try {
/*  493 */           String path = uri.getPath();
/*  494 */           index = path.lastIndexOf('/');
/*  495 */           if (index != -1) {
/*  496 */             path = path.substring(0, index + 1);
/*  497 */             path = path + normalizedFileName;
/*      */           }
/*  499 */           uri2 = new URI(uri.getScheme(), uri.getHost(), path, uri.getFragment());
/*      */         } catch (URISyntaxException localURISyntaxException) {
/*  501 */           throw new IllegalArgumentException("invalid sibling");
/*      */         }
/*  503 */         return new EclipseFileObject(className, uri2, kind, this.charset);
/*      */       }
/*  505 */       String normalizedFileName = normalized(className);
/*  506 */       normalizedFileName = normalizedFileName + kind.extension;
/*  507 */       File f = new File(System.getProperty("user.dir"), normalizedFileName);
/*  508 */       return new EclipseFileObject(className, f.toURI(), kind, this.charset);
/*      */     }
/*      */ 
/*  511 */     Iterator iterator = files.iterator();
/*  512 */     if (iterator.hasNext()) {
/*  513 */       File file = (File)iterator.next();
/*  514 */       String normalizedFileName = normalized(className);
/*  515 */       normalizedFileName = normalizedFileName + kind.extension;
/*  516 */       File f = new File(file, normalizedFileName);
/*  517 */       return new EclipseFileObject(className, f.toURI(), kind, this.charset);
/*      */     }
/*  519 */     throw new IllegalArgumentException("location is empty : " + location);
/*      */   }
/*      */ 
/*      */   public Iterable<? extends JavaFileObject> getJavaFileObjects(File[] files)
/*      */   {
/*  527 */     return getJavaFileObjectsFromFiles(Arrays.asList(files));
/*      */   }
/*      */ 
/*      */   public Iterable<? extends JavaFileObject> getJavaFileObjects(String[] names)
/*      */   {
/*  534 */     return getJavaFileObjectsFromStrings(Arrays.asList(names));
/*      */   }
/*      */ 
/*      */   public Iterable<? extends JavaFileObject> getJavaFileObjectsFromFiles(Iterable<? extends File> files)
/*      */   {
/*  541 */     ArrayList javaFileArrayList = new ArrayList();
/*  542 */     for (File f : files) {
/*  543 */       javaFileArrayList.add(new EclipseFileObject(f.getAbsolutePath(), f.toURI(), getKind(f), this.charset));
/*      */     }
/*  545 */     return javaFileArrayList;
/*      */   }
/*      */ 
/*      */   public Iterable<? extends JavaFileObject> getJavaFileObjectsFromStrings(Iterable<String> names)
/*      */   {
/*  552 */     ArrayList files = new ArrayList();
/*  553 */     for (String name : names) {
/*  554 */       files.add(new File(name));
/*      */     }
/*  556 */     return getJavaFileObjectsFromFiles(files);
/*      */   }
/*      */ 
/*      */   public JavaFileObject.Kind getKind(File f) {
/*  560 */     return getKind(getExtension(f));
/*      */   }
/*      */ 
/*      */   private JavaFileObject.Kind getKind(String extension) {
/*  564 */     if (JavaFileObject.Kind.CLASS.extension.equals(extension))
/*  565 */       return JavaFileObject.Kind.CLASS;
/*  566 */     if (JavaFileObject.Kind.SOURCE.extension.equals(extension))
/*  567 */       return JavaFileObject.Kind.SOURCE;
/*  568 */     if (JavaFileObject.Kind.HTML.extension.equals(extension)) {
/*  569 */       return JavaFileObject.Kind.HTML;
/*      */     }
/*  571 */     return JavaFileObject.Kind.OTHER;
/*      */   }
/*      */ 
/*      */   public Iterable<? extends File> getLocation(JavaFileManager.Location location)
/*      */   {
/*  578 */     if (this.locations == null) return null;
/*  579 */     return (Iterable)this.locations.get(location.getName());
/*      */   }
/*      */ 
/*      */   private Iterable<? extends File> getOutputDir(String string) {
/*  583 */     if ("none".equals(string)) {
/*  584 */       return null;
/*      */     }
/*  586 */     File file = new File(string);
/*  587 */     if ((file.exists()) && (!file.isDirectory())) {
/*  588 */       throw new IllegalArgumentException("file : " + file.getAbsolutePath() + " is not a directory");
/*      */     }
/*  590 */     ArrayList list = new ArrayList(1);
/*  591 */     list.add(file);
/*  592 */     return list;
/*      */   }
/*      */ 
/*      */   public boolean handleOption(String current, Iterator<String> remaining)
/*      */   {
/*      */     try
/*      */     {
/*  600 */       if ("-bootclasspath".equals(current)) {
/*  601 */         remaining.remove();
/*  602 */         if (remaining.hasNext()) {
/*  603 */           Iterable bootclasspaths = getPathsFrom((String)remaining.next());
/*  604 */           if (bootclasspaths != null) {
/*  605 */             Iterable iterable = getLocation(StandardLocation.PLATFORM_CLASS_PATH);
/*  606 */             if (((this.flags & 0x4) == 0) && 
/*  607 */               ((this.flags & 0x1) == 0))
/*      */             {
/*  609 */               setLocation(StandardLocation.PLATFORM_CLASS_PATH, bootclasspaths);
/*  610 */             } else if ((this.flags & 0x4) != 0)
/*      */             {
/*  612 */               setLocation(StandardLocation.PLATFORM_CLASS_PATH, 
/*  613 */                 concatFiles(iterable, bootclasspaths));
/*      */             }
/*      */             else {
/*  616 */               setLocation(StandardLocation.PLATFORM_CLASS_PATH, 
/*  617 */                 prependFiles(iterable, bootclasspaths));
/*      */             }
/*      */           }
/*  620 */           remaining.remove();
/*  621 */           this.flags |= 2;
/*  622 */           return true;
/*      */         }
/*  624 */         throw new IllegalArgumentException();
/*      */       }
/*      */ 
/*  627 */       if (("-classpath".equals(current)) || ("-cp".equals(current))) {
/*  628 */         remaining.remove();
/*  629 */         if (remaining.hasNext()) {
/*  630 */           Iterable classpaths = getPathsFrom((String)remaining.next());
/*  631 */           if (classpaths != null) {
/*  632 */             Iterable iterable = getLocation(StandardLocation.CLASS_PATH);
/*  633 */             if (iterable != null)
/*  634 */               setLocation(StandardLocation.CLASS_PATH, 
/*  635 */                 concatFiles(iterable, classpaths));
/*      */             else {
/*  637 */               setLocation(StandardLocation.CLASS_PATH, classpaths);
/*      */             }
/*  639 */             if ((this.flags & 0x8) == 0) {
/*  640 */               setLocation(StandardLocation.ANNOTATION_PROCESSOR_PATH, classpaths);
/*      */             }
/*      */           }
/*  643 */           remaining.remove();
/*  644 */           return true;
/*      */         }
/*  646 */         throw new IllegalArgumentException();
/*      */       }
/*      */ 
/*  649 */       if ("-encoding".equals(current)) {
/*  650 */         remaining.remove();
/*  651 */         if (remaining.hasNext()) {
/*  652 */           this.charset = Charset.forName((String)remaining.next());
/*  653 */           remaining.remove();
/*  654 */           return true;
/*      */         }
/*  656 */         throw new IllegalArgumentException();
/*      */       }
/*      */ 
/*  659 */       if ("-sourcepath".equals(current)) {
/*  660 */         remaining.remove();
/*  661 */         if (remaining.hasNext()) {
/*  662 */           Iterable sourcepaths = getPathsFrom((String)remaining.next());
/*  663 */           if (sourcepaths != null) setLocation(StandardLocation.SOURCE_PATH, sourcepaths);
/*  664 */           remaining.remove();
/*  665 */           return true;
/*      */         }
/*  667 */         throw new IllegalArgumentException();
/*      */       }
/*      */ 
/*  670 */       if ("-extdirs".equals(current)) {
/*  671 */         remaining.remove();
/*  672 */         if (remaining.hasNext()) {
/*  673 */           Iterable iterable = getLocation(StandardLocation.PLATFORM_CLASS_PATH);
/*  674 */           setLocation(StandardLocation.PLATFORM_CLASS_PATH, 
/*  675 */             concatFiles(iterable, getExtdirsFrom((String)remaining.next())));
/*  676 */           remaining.remove();
/*  677 */           this.flags |= 1;
/*  678 */           return true;
/*      */         }
/*  680 */         throw new IllegalArgumentException();
/*      */       }
/*      */ 
/*  683 */       if ("-endorseddirs".equals(current)) {
/*  684 */         remaining.remove();
/*  685 */         if (remaining.hasNext()) {
/*  686 */           Iterable iterable = getLocation(StandardLocation.PLATFORM_CLASS_PATH);
/*  687 */           setLocation(StandardLocation.PLATFORM_CLASS_PATH, 
/*  688 */             prependFiles(iterable, getEndorsedDirsFrom((String)remaining.next())));
/*  689 */           remaining.remove();
/*  690 */           this.flags |= 4;
/*  691 */           return true;
/*      */         }
/*  693 */         throw new IllegalArgumentException();
/*      */       }
/*      */ 
/*  696 */       if ("-d".equals(current)) {
/*  697 */         remaining.remove();
/*  698 */         if (remaining.hasNext()) {
/*  699 */           Iterable outputDir = getOutputDir((String)remaining.next());
/*  700 */           if (outputDir != null) {
/*  701 */             setLocation(StandardLocation.CLASS_OUTPUT, outputDir);
/*      */           }
/*  703 */           remaining.remove();
/*  704 */           return true;
/*      */         }
/*  706 */         throw new IllegalArgumentException();
/*      */       }
/*      */ 
/*  709 */       if ("-s".equals(current)) {
/*  710 */         remaining.remove();
/*  711 */         if (remaining.hasNext()) {
/*  712 */           Iterable outputDir = getOutputDir((String)remaining.next());
/*  713 */           if (outputDir != null) {
/*  714 */             setLocation(StandardLocation.SOURCE_OUTPUT, outputDir);
/*      */           }
/*  716 */           remaining.remove();
/*  717 */           return true;
/*      */         }
/*  719 */         throw new IllegalArgumentException();
/*      */       }
/*      */ 
/*  722 */       if ("-processorpath".equals(current)) {
/*  723 */         remaining.remove();
/*  724 */         if (remaining.hasNext()) {
/*  725 */           Iterable processorpaths = getPathsFrom((String)remaining.next());
/*  726 */           if (processorpaths != null) {
/*  727 */             setLocation(StandardLocation.ANNOTATION_PROCESSOR_PATH, processorpaths);
/*      */           }
/*  729 */           remaining.remove();
/*  730 */           this.flags |= 8;
/*  731 */           return true;
/*      */         }
/*  733 */         throw new IllegalArgumentException();
/*      */       }
/*      */     }
/*      */     catch (IOException localIOException)
/*      */     {
/*      */     }
/*  739 */     return false;
/*      */   }
/*      */ 
/*      */   public boolean hasLocation(JavaFileManager.Location location)
/*      */   {
/*  746 */     return (this.locations != null) && (this.locations.containsKey(location.getName()));
/*      */   }
/*      */ 
/*      */   public String inferBinaryName(JavaFileManager.Location location, JavaFileObject file)
/*      */   {
/*  753 */     String name = file.getName();
/*  754 */     JavaFileObject javaFileObject = null;
/*  755 */     int index = name.lastIndexOf('.');
/*  756 */     if (index != -1)
/*  757 */       name = name.substring(0, index);
/*      */     try
/*      */     {
/*  760 */       javaFileObject = getJavaFileForInput(location, name, file.getKind());
/*      */     }
/*      */     catch (IOException localIOException) {
/*      */     }
/*  764 */     if (javaFileObject == null) {
/*  765 */       return null;
/*      */     }
/*  767 */     return normalized(name);
/*      */   }
/*      */ 
/*      */   private boolean isArchive(File f) {
/*  771 */     String extension = getExtension(f);
/*  772 */     return (extension.equalsIgnoreCase(".jar")) || (extension.equalsIgnoreCase(".zip"));
/*      */   }
/*      */ 
/*      */   public boolean isSameFile(FileObject fileObject1, FileObject fileObject2)
/*      */   {
/*  780 */     if (!(fileObject1 instanceof EclipseFileObject)) throw new IllegalArgumentException("Unsupported file object class : " + fileObject1.getClass());
/*  781 */     if (!(fileObject2 instanceof EclipseFileObject)) throw new IllegalArgumentException("Unsupported file object class : " + fileObject2.getClass());
/*  782 */     return fileObject1.equals(fileObject2);
/*      */   }
/*      */ 
/*      */   public int isSupportedOption(String option)
/*      */   {
/*  788 */     return Options.processOptionsFileManager(option);
/*      */   }
/*      */ 
/*      */   public Iterable<JavaFileObject> list(JavaFileManager.Location location, String packageName, Set<JavaFileObject.Kind> kinds, boolean recurse)
/*      */     throws IOException
/*      */   {
/*  797 */     Iterable allFilesInLocations = getLocation(location);
/*  798 */     if (allFilesInLocations == null) {
/*  799 */       throw new IllegalArgumentException("Unknown location : " + location);
/*      */     }
/*      */ 
/*  802 */     ArrayList collector = new ArrayList();
/*  803 */     String normalizedPackageName = normalized(packageName);
/*  804 */     for (File file : allFilesInLocations) {
/*  805 */       collectAllMatchingFiles(file, normalizedPackageName, kinds, recurse, collector);
/*      */     }
/*  807 */     return collector;
/*      */   }
/*      */ 
/*      */   private String normalized(String className) {
/*  811 */     char[] classNameChars = className.toCharArray();
/*  812 */     int i = 0; for (int max = classNameChars.length; i < max; i++) {
/*  813 */       switch (classNameChars[i]) {
/*      */       case '\\':
/*  815 */         classNameChars[i] = '/';
/*  816 */         break;
/*      */       case '.':
/*  818 */         classNameChars[i] = '/';
/*      */       }
/*      */     }
/*  821 */     return new String(classNameChars);
/*      */   }
/*      */ 
/*      */   private Iterable<? extends File> prependFiles(Iterable<? extends File> iterable, Iterable<? extends File> iterable2)
/*      */   {
/*  826 */     if (iterable2 == null) return iterable;
/*  827 */     ArrayList list = new ArrayList();
/*  828 */     for (Iterator iterator = iterable2.iterator(); iterator.hasNext(); ) {
/*  829 */       list.add((File)iterator.next());
/*      */     }
/*  831 */     for (Iterator iterator = iterable.iterator(); iterator.hasNext(); ) {
/*  832 */       list.add((File)iterator.next());
/*      */     }
/*  834 */     return list;
/*      */   }
/*      */ 
/*      */   public void setLocation(JavaFileManager.Location location, Iterable<? extends File> path)
/*      */     throws IOException
/*      */   {
/*  841 */     if (path != null) {
/*  842 */       if (location.isOutputLocation())
/*      */       {
/*  844 */         int count = 0;
/*  845 */         for (Iterator iterator = path.iterator(); iterator.hasNext(); ) {
/*  846 */           iterator.next();
/*  847 */           count++;
/*      */         }
/*  849 */         if (count != 1) {
/*  850 */           throw new IllegalArgumentException("output location can only have one path");
/*      */         }
/*      */       }
/*  853 */       this.locations.put(location.getName(), path);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void setLocale(Locale locale) {
/*  858 */     this.locale = (locale == null ? Locale.getDefault() : locale);
/*      */     try {
/*  860 */       this.bundle = Main.ResourceBundleFactory.getBundle(this.locale);
/*      */     } catch (MissingResourceException e) {
/*  862 */       System.out.println("Missing resource : " + "org.eclipse.jdt.internal.compiler.batch.messages".replace('.', '/') + ".properties for locale " + locale);
/*  863 */       throw e;
/*      */     }
/*      */   }
/*      */ 
/*      */   public void processPathEntries(int defaultSize, ArrayList paths, String currentPath, String customEncoding, boolean isSourceOnly, boolean rejectDestinationPathOnJars)
/*      */   {
/*  872 */     String currentClasspathName = null;
/*  873 */     String currentDestinationPath = null;
/*  874 */     ArrayList currentRuleSpecs = new ArrayList(defaultSize);
/*  875 */     StringTokenizer tokenizer = new StringTokenizer(currentPath, 
/*  876 */       File.pathSeparator + "[]", true);
/*  877 */     ArrayList tokens = new ArrayList();
/*  878 */     while (tokenizer.hasMoreTokens()) {
/*  879 */       tokens.add(tokenizer.nextToken());
/*      */     }
/*      */ 
/*  907 */     int state = 0;
/*  908 */     String token = null;
/*  909 */     int cursor = 0; int tokensNb = tokens.size(); int bracket = -1;
/*  910 */     label712: while ((cursor < tokensNb) && (state != 99)) {
/*  911 */       token = (String)tokens.get(cursor++);
/*  912 */       if (token.equals(File.pathSeparator)) {
/*  913 */         switch (state) {
/*      */         case 0:
/*      */         case 3:
/*      */         case 10:
/*  917 */           break;
/*      */         case 1:
/*      */         case 2:
/*      */         case 8:
/*  921 */           state = 3;
/*  922 */           addNewEntry(paths, currentClasspathName, currentRuleSpecs, 
/*  923 */             customEncoding, currentDestinationPath, isSourceOnly, 
/*  924 */             rejectDestinationPathOnJars);
/*  925 */           currentRuleSpecs.clear();
/*  926 */           break;
/*      */         case 6:
/*  928 */           state = 4;
/*  929 */           break;
/*      */         case 7:
/*  931 */           throw new IllegalArgumentException(
/*  932 */             bind("configure.incorrectDestinationPathEntry", 
/*  933 */             currentPath));
/*      */         case 11:
/*  935 */           cursor = bracket + 1;
/*  936 */           state = 5;
/*  937 */           break;
/*      */         case 4:
/*      */         case 5:
/*      */         case 9:
/*      */         default:
/*  939 */           state = 99; break;
/*      */         }
/*  941 */       } else if (token.equals("[")) {
/*  942 */         switch (state) {
/*      */         case 0:
/*  944 */           currentClasspathName = "";
/*      */         case 1:
/*  947 */           bracket = cursor - 1;
/*      */         case 11:
/*  950 */           state = 10;
/*  951 */           break;
/*      */         case 2:
/*  953 */           state = 9;
/*  954 */           break;
/*      */         case 8:
/*  956 */           state = 5;
/*  957 */           break;
/*      */         case 3:
/*      */         case 4:
/*      */         case 5:
/*      */         case 6:
/*      */         case 7:
/*      */         case 9:
/*      */         case 10:
/*      */         default:
/*  960 */           state = 99; break;
/*      */         }
/*  962 */       } else if (token.equals("]")) {
/*  963 */         switch (state) {
/*      */         case 6:
/*  965 */           state = 2;
/*  966 */           break;
/*      */         case 7:
/*  968 */           state = 8;
/*  969 */           break;
/*      */         case 10:
/*  971 */           state = 11;
/*  972 */           break;
/*      */         case 8:
/*      */         case 9:
/*      */         case 11:
/*      */         default:
/*  975 */           state = 99; break;
/*      */         }
/*      */       }
/*      */       else {
/*  979 */         switch (state) {
/*      */         case 0:
/*      */         case 3:
/*  982 */           state = 1;
/*  983 */           currentClasspathName = token;
/*  984 */           break;
/*      */         case 5:
/*  986 */           if (!token.startsWith("-d ")) break;
/*  987 */           if (currentDestinationPath != null) {
/*  988 */             throw new IllegalArgumentException(
/*  989 */               bind("configure.duplicateDestinationPathEntry", 
/*  990 */               currentPath));
/*      */           }
/*  992 */           currentDestinationPath = token.substring(3).trim();
/*  993 */           state = 7;
/*  994 */           break;
/*      */         case 4:
/*  998 */           if (currentDestinationPath != null) {
/*  999 */             throw new IllegalArgumentException(
/* 1000 */               bind("configure.accessRuleAfterDestinationPath", 
/* 1001 */               currentPath));
/*      */           }
/* 1003 */           state = 6;
/* 1004 */           currentRuleSpecs.add(token);
/* 1005 */           break;
/*      */         case 9:
/* 1007 */           if (!token.startsWith("-d ")) {
/* 1008 */             state = 99; break label712;
/*      */           }
/* 1010 */           currentDestinationPath = token.substring(3).trim();
/* 1011 */           state = 7;
/*      */ 
/* 1013 */           break;
/*      */         case 11:
/* 1015 */           for (int i = bracket; i < cursor; i++) {
/* 1016 */             currentClasspathName = currentClasspathName + (String)tokens.get(i);
/*      */           }
/* 1018 */           state = 1;
/* 1019 */           break;
/*      */         case 10:
/* 1021 */           break;
/*      */         case 1:
/*      */         case 2:
/*      */         case 6:
/*      */         case 7:
/* 1023 */         case 8: } state = 99;
/*      */       }
/*      */ 
/* 1026 */       if ((state == 11) && (cursor == tokensNb)) {
/* 1027 */         cursor = bracket + 1;
/* 1028 */         state = 5;
/*      */       }
/*      */     }
/* 1031 */     switch (state) {
/*      */     case 3:
/* 1033 */       break;
/*      */     case 1:
/*      */     case 2:
/*      */     case 8:
/* 1037 */       addNewEntry(paths, currentClasspathName, currentRuleSpecs, 
/* 1038 */         customEncoding, currentDestinationPath, isSourceOnly, 
/* 1039 */         rejectDestinationPathOnJars);
/*      */     case 4:
/*      */     case 5:
/*      */     case 6:
/*      */     case 7:
/*      */     case 9:
/*      */     case 10:
/*      */     case 11:
/*      */     }
/*      */   }
/*      */ 
/*      */   protected void addNewEntry(ArrayList paths, String currentClasspathName, ArrayList currentRuleSpecs, String customEncoding, String destPath, boolean isSourceOnly, boolean rejectDestinationPathOnJars)
/*      */   {
/* 1053 */     int rulesSpecsSize = currentRuleSpecs.size();
/* 1054 */     AccessRuleSet accessRuleSet = null;
/* 1055 */     if (rulesSpecsSize != 0) {
/* 1056 */       AccessRule[] accessRules = new AccessRule[currentRuleSpecs.size()];
/* 1057 */       boolean rulesOK = true;
/* 1058 */       Iterator i = currentRuleSpecs.iterator();
/* 1059 */       int j = 0;
/* 1060 */       while (i.hasNext()) {
/* 1061 */         String ruleSpec = (String)i.next();
/* 1062 */         char key = ruleSpec.charAt(0);
/* 1063 */         String pattern = ruleSpec.substring(1);
/* 1064 */         if (pattern.length() > 0)
/* 1065 */           switch (key) {
/*      */           case '+':
/* 1067 */             accessRules[(j++)] = 
/* 1068 */               new AccessRule(pattern
/* 1068 */               .toCharArray(), 0);
/* 1069 */             break;
/*      */           case '~':
/* 1071 */             accessRules[(j++)] = 
/* 1073 */               new AccessRule(pattern
/* 1072 */               .toCharArray(), 
/* 1073 */               16777496);
/* 1074 */             break;
/*      */           case '-':
/* 1076 */             accessRules[(j++)] = 
/* 1078 */               new AccessRule(pattern
/* 1077 */               .toCharArray(), 
/* 1078 */               16777523);
/* 1079 */             break;
/*      */           case '?':
/* 1081 */             accessRules[(j++)] = 
/* 1083 */               new AccessRule(pattern
/* 1082 */               .toCharArray(), 
/* 1083 */               16777523, true);
/* 1084 */             break;
/*      */           default:
/* 1086 */             rulesOK = false; break;
/*      */           }
/*      */         else {
/* 1089 */           rulesOK = false;
/*      */         }
/*      */       }
/* 1092 */       if (rulesOK)
/* 1093 */         accessRuleSet = new AccessRuleSet(accessRules, 0, currentClasspathName);
/*      */       else {
/* 1095 */         return;
/*      */       }
/*      */     }
/* 1098 */     if ("none".equals(destPath)) {
/* 1099 */       destPath = "none";
/*      */     }
/* 1101 */     if ((rejectDestinationPathOnJars) && (destPath != null) && (
/* 1102 */       (currentClasspathName.endsWith(".jar")) || 
/* 1103 */       (currentClasspathName.endsWith(".zip")))) {
/* 1104 */       throw new IllegalArgumentException(
/* 1105 */         bind("configure.unexpectedDestinationPathEntryFile", 
/* 1106 */         currentClasspathName));
/*      */     }
/* 1108 */     FileSystem.Classpath currentClasspath = FileSystem.getClasspath(
/* 1109 */       currentClasspathName, 
/* 1110 */       customEncoding, 
/* 1111 */       isSourceOnly, 
/* 1112 */       accessRuleSet, 
/* 1113 */       destPath);
/* 1114 */     if (currentClasspath != null)
/* 1115 */       paths.add(currentClasspath);
/*      */   }
/*      */ 
/*      */   private String bind(String id, String binding)
/*      */   {
/* 1123 */     return bind(id, new String[] { binding });
/*      */   }
/*      */ 
/*      */   private String bind(String id, String[] arguments)
/*      */   {
/* 1131 */     if (id == null)
/* 1132 */       return "No message available";
/* 1133 */     String message = null;
/*      */     try {
/* 1135 */       message = this.bundle.getString(id);
/*      */     }
/*      */     catch (MissingResourceException localMissingResourceException)
/*      */     {
/* 1139 */       return "Missing message: " + id + " in: " + "org.eclipse.jdt.internal.compiler.batch.messages";
/*      */     }
/* 1141 */     return MessageFormat.format(message, arguments);
/*      */   }
/*      */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\ecj-3.5.2.jar
 * Qualified Name:     org.eclipse.jdt.internal.compiler.apt.util.EclipseFileManager
 * JD-Core Version:    0.6.0
 */