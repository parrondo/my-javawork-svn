/*      */ package org.eclipse.jdt.internal.compiler.tool;
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
/*  601 */         if (remaining.hasNext()) {
/*  602 */           Iterable bootclasspaths = getPathsFrom((String)remaining.next());
/*  603 */           if (bootclasspaths != null) {
/*  604 */             Iterable iterable = getLocation(StandardLocation.PLATFORM_CLASS_PATH);
/*  605 */             if (((this.flags & 0x4) == 0) && 
/*  606 */               ((this.flags & 0x1) == 0))
/*      */             {
/*  608 */               setLocation(StandardLocation.PLATFORM_CLASS_PATH, bootclasspaths);
/*  609 */             } else if ((this.flags & 0x4) != 0)
/*      */             {
/*  611 */               setLocation(StandardLocation.PLATFORM_CLASS_PATH, 
/*  612 */                 concatFiles(iterable, bootclasspaths));
/*      */             }
/*      */             else {
/*  615 */               setLocation(StandardLocation.PLATFORM_CLASS_PATH, 
/*  616 */                 prependFiles(iterable, bootclasspaths));
/*      */             }
/*      */           }
/*  619 */           this.flags |= 2;
/*  620 */           return true;
/*      */         }
/*  622 */         throw new IllegalArgumentException();
/*      */       }
/*      */ 
/*  625 */       if (("-classpath".equals(current)) || ("-cp".equals(current))) {
/*  626 */         if (remaining.hasNext()) {
/*  627 */           Iterable classpaths = getPathsFrom((String)remaining.next());
/*  628 */           if (classpaths != null) {
/*  629 */             Iterable iterable = getLocation(StandardLocation.CLASS_PATH);
/*  630 */             if (iterable != null)
/*  631 */               setLocation(StandardLocation.CLASS_PATH, 
/*  632 */                 concatFiles(iterable, classpaths));
/*      */             else {
/*  634 */               setLocation(StandardLocation.CLASS_PATH, classpaths);
/*      */             }
/*  636 */             if ((this.flags & 0x8) == 0) {
/*  637 */               setLocation(StandardLocation.ANNOTATION_PROCESSOR_PATH, classpaths);
/*      */             }
/*      */           }
/*  640 */           return true;
/*      */         }
/*  642 */         throw new IllegalArgumentException();
/*      */       }
/*      */ 
/*  645 */       if ("-encoding".equals(current)) {
/*  646 */         if (remaining.hasNext()) {
/*  647 */           this.charset = Charset.forName((String)remaining.next());
/*  648 */           return true;
/*      */         }
/*  650 */         throw new IllegalArgumentException();
/*      */       }
/*      */ 
/*  653 */       if ("-sourcepath".equals(current)) {
/*  654 */         if (remaining.hasNext()) {
/*  655 */           Iterable sourcepaths = getPathsFrom((String)remaining.next());
/*  656 */           if (sourcepaths != null) setLocation(StandardLocation.SOURCE_PATH, sourcepaths);
/*  657 */           return true;
/*      */         }
/*  659 */         throw new IllegalArgumentException();
/*      */       }
/*      */ 
/*  662 */       if ("-extdirs".equals(current)) {
/*  663 */         if (remaining.hasNext()) {
/*  664 */           Iterable iterable = getLocation(StandardLocation.PLATFORM_CLASS_PATH);
/*  665 */           setLocation(StandardLocation.PLATFORM_CLASS_PATH, 
/*  666 */             concatFiles(iterable, getExtdirsFrom((String)remaining.next())));
/*  667 */           this.flags |= 1;
/*  668 */           return true;
/*      */         }
/*  670 */         throw new IllegalArgumentException();
/*      */       }
/*      */ 
/*  673 */       if ("-endorseddirs".equals(current)) {
/*  674 */         if (remaining.hasNext()) {
/*  675 */           Iterable iterable = getLocation(StandardLocation.PLATFORM_CLASS_PATH);
/*  676 */           setLocation(StandardLocation.PLATFORM_CLASS_PATH, 
/*  677 */             prependFiles(iterable, getEndorsedDirsFrom((String)remaining.next())));
/*  678 */           this.flags |= 4;
/*  679 */           return true;
/*      */         }
/*  681 */         throw new IllegalArgumentException();
/*      */       }
/*      */ 
/*  684 */       if ("-d".equals(current)) {
/*  685 */         if (remaining.hasNext()) {
/*  686 */           Iterable outputDir = getOutputDir((String)remaining.next());
/*  687 */           if (outputDir != null) {
/*  688 */             setLocation(StandardLocation.CLASS_OUTPUT, outputDir);
/*      */           }
/*  690 */           return true;
/*      */         }
/*  692 */         throw new IllegalArgumentException();
/*      */       }
/*      */ 
/*  695 */       if ("-s".equals(current)) {
/*  696 */         if (remaining.hasNext()) {
/*  697 */           Iterable outputDir = getOutputDir((String)remaining.next());
/*  698 */           if (outputDir != null) {
/*  699 */             setLocation(StandardLocation.SOURCE_OUTPUT, outputDir);
/*      */           }
/*  701 */           return true;
/*      */         }
/*  703 */         throw new IllegalArgumentException();
/*      */       }
/*      */ 
/*  706 */       if ("-processorpath".equals(current)) {
/*  707 */         if (remaining.hasNext()) {
/*  708 */           Iterable processorpaths = getPathsFrom((String)remaining.next());
/*  709 */           if (processorpaths != null) {
/*  710 */             setLocation(StandardLocation.ANNOTATION_PROCESSOR_PATH, processorpaths);
/*      */           }
/*  712 */           this.flags |= 8;
/*  713 */           return true;
/*      */         }
/*  715 */         throw new IllegalArgumentException();
/*      */       }
/*      */     }
/*      */     catch (IOException localIOException)
/*      */     {
/*      */     }
/*  721 */     return false;
/*      */   }
/*      */ 
/*      */   public boolean hasLocation(JavaFileManager.Location location)
/*      */   {
/*  728 */     return (this.locations != null) && (this.locations.containsKey(location.getName()));
/*      */   }
/*      */ 
/*      */   public String inferBinaryName(JavaFileManager.Location location, JavaFileObject file)
/*      */   {
/*  735 */     String name = file.getName();
/*  736 */     JavaFileObject javaFileObject = null;
/*  737 */     int index = name.lastIndexOf('.');
/*  738 */     if (index != -1)
/*  739 */       name = name.substring(0, index);
/*      */     try
/*      */     {
/*  742 */       javaFileObject = getJavaFileForInput(location, name, file.getKind());
/*      */     }
/*      */     catch (IOException localIOException) {
/*      */     }
/*  746 */     if (javaFileObject == null) {
/*  747 */       return null;
/*      */     }
/*  749 */     return normalized(name);
/*      */   }
/*      */ 
/*      */   private boolean isArchive(File f) {
/*  753 */     String extension = getExtension(f);
/*  754 */     return (extension.equalsIgnoreCase(".jar")) || (extension.equalsIgnoreCase(".zip"));
/*      */   }
/*      */ 
/*      */   public boolean isSameFile(FileObject fileObject1, FileObject fileObject2)
/*      */   {
/*  762 */     if (!(fileObject1 instanceof EclipseFileObject)) throw new IllegalArgumentException("Unsupported file object class : " + fileObject1.getClass());
/*  763 */     if (!(fileObject2 instanceof EclipseFileObject)) throw new IllegalArgumentException("Unsupported file object class : " + fileObject2.getClass());
/*  764 */     return fileObject1.equals(fileObject2);
/*      */   }
/*      */ 
/*      */   public int isSupportedOption(String option)
/*      */   {
/*  770 */     return Options.processOptionsFileManager(option);
/*      */   }
/*      */ 
/*      */   public Iterable<JavaFileObject> list(JavaFileManager.Location location, String packageName, Set<JavaFileObject.Kind> kinds, boolean recurse)
/*      */     throws IOException
/*      */   {
/*  779 */     Iterable allFilesInLocations = getLocation(location);
/*  780 */     if (allFilesInLocations == null) {
/*  781 */       throw new IllegalArgumentException("Unknown location : " + location);
/*      */     }
/*      */ 
/*  784 */     ArrayList collector = new ArrayList();
/*  785 */     String normalizedPackageName = normalized(packageName);
/*  786 */     for (File file : allFilesInLocations) {
/*  787 */       collectAllMatchingFiles(file, normalizedPackageName, kinds, recurse, collector);
/*      */     }
/*  789 */     return collector;
/*      */   }
/*      */ 
/*      */   private String normalized(String className) {
/*  793 */     char[] classNameChars = className.toCharArray();
/*  794 */     int i = 0; for (int max = classNameChars.length; i < max; i++) {
/*  795 */       switch (classNameChars[i]) {
/*      */       case '\\':
/*  797 */         classNameChars[i] = '/';
/*  798 */         break;
/*      */       case '.':
/*  800 */         classNameChars[i] = '/';
/*      */       }
/*      */     }
/*  803 */     return new String(classNameChars);
/*      */   }
/*      */ 
/*      */   private Iterable<? extends File> prependFiles(Iterable<? extends File> iterable, Iterable<? extends File> iterable2)
/*      */   {
/*  808 */     if (iterable2 == null) return iterable;
/*  809 */     ArrayList list = new ArrayList();
/*  810 */     for (Iterator iterator = iterable2.iterator(); iterator.hasNext(); ) {
/*  811 */       list.add((File)iterator.next());
/*      */     }
/*  813 */     for (Iterator iterator = iterable.iterator(); iterator.hasNext(); ) {
/*  814 */       list.add((File)iterator.next());
/*      */     }
/*  816 */     return list;
/*      */   }
/*      */ 
/*      */   public void setLocation(JavaFileManager.Location location, Iterable<? extends File> path)
/*      */     throws IOException
/*      */   {
/*  823 */     if (path != null) {
/*  824 */       if (location.isOutputLocation())
/*      */       {
/*  826 */         int count = 0;
/*  827 */         for (Iterator iterator = path.iterator(); iterator.hasNext(); ) {
/*  828 */           iterator.next();
/*  829 */           count++;
/*      */         }
/*  831 */         if (count != 1) {
/*  832 */           throw new IllegalArgumentException("output location can only have one path");
/*      */         }
/*      */       }
/*  835 */       this.locations.put(location.getName(), path);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void setLocale(Locale locale) {
/*  840 */     this.locale = (locale == null ? Locale.getDefault() : locale);
/*      */     try {
/*  842 */       this.bundle = Main.ResourceBundleFactory.getBundle(this.locale);
/*      */     } catch (MissingResourceException e) {
/*  844 */       System.out.println("Missing resource : " + "org.eclipse.jdt.internal.compiler.batch.messages".replace('.', '/') + ".properties for locale " + locale);
/*  845 */       throw e;
/*      */     }
/*      */   }
/*      */ 
/*      */   public void processPathEntries(int defaultSize, ArrayList paths, String currentPath, String customEncoding, boolean isSourceOnly, boolean rejectDestinationPathOnJars)
/*      */   {
/*  854 */     String currentClasspathName = null;
/*  855 */     String currentDestinationPath = null;
/*  856 */     ArrayList currentRuleSpecs = new ArrayList(defaultSize);
/*  857 */     StringTokenizer tokenizer = new StringTokenizer(currentPath, 
/*  858 */       File.pathSeparator + "[]", true);
/*  859 */     ArrayList tokens = new ArrayList();
/*  860 */     while (tokenizer.hasMoreTokens()) {
/*  861 */       tokens.add(tokenizer.nextToken());
/*      */     }
/*      */ 
/*  889 */     int state = 0;
/*  890 */     String token = null;
/*  891 */     int cursor = 0; int tokensNb = tokens.size(); int bracket = -1;
/*  892 */     label712: while ((cursor < tokensNb) && (state != 99)) {
/*  893 */       token = (String)tokens.get(cursor++);
/*  894 */       if (token.equals(File.pathSeparator)) {
/*  895 */         switch (state) {
/*      */         case 0:
/*      */         case 3:
/*      */         case 10:
/*  899 */           break;
/*      */         case 1:
/*      */         case 2:
/*      */         case 8:
/*  903 */           state = 3;
/*  904 */           addNewEntry(paths, currentClasspathName, currentRuleSpecs, 
/*  905 */             customEncoding, currentDestinationPath, isSourceOnly, 
/*  906 */             rejectDestinationPathOnJars);
/*  907 */           currentRuleSpecs.clear();
/*  908 */           break;
/*      */         case 6:
/*  910 */           state = 4;
/*  911 */           break;
/*      */         case 7:
/*  913 */           throw new IllegalArgumentException(
/*  914 */             bind("configure.incorrectDestinationPathEntry", 
/*  915 */             currentPath));
/*      */         case 11:
/*  917 */           cursor = bracket + 1;
/*  918 */           state = 5;
/*  919 */           break;
/*      */         case 4:
/*      */         case 5:
/*      */         case 9:
/*      */         default:
/*  921 */           state = 99; break;
/*      */         }
/*  923 */       } else if (token.equals("[")) {
/*  924 */         switch (state) {
/*      */         case 0:
/*  926 */           currentClasspathName = "";
/*      */         case 1:
/*  929 */           bracket = cursor - 1;
/*      */         case 11:
/*  932 */           state = 10;
/*  933 */           break;
/*      */         case 2:
/*  935 */           state = 9;
/*  936 */           break;
/*      */         case 8:
/*  938 */           state = 5;
/*  939 */           break;
/*      */         case 3:
/*      */         case 4:
/*      */         case 5:
/*      */         case 6:
/*      */         case 7:
/*      */         case 9:
/*      */         case 10:
/*      */         default:
/*  942 */           state = 99; break;
/*      */         }
/*  944 */       } else if (token.equals("]")) {
/*  945 */         switch (state) {
/*      */         case 6:
/*  947 */           state = 2;
/*  948 */           break;
/*      */         case 7:
/*  950 */           state = 8;
/*  951 */           break;
/*      */         case 10:
/*  953 */           state = 11;
/*  954 */           break;
/*      */         case 8:
/*      */         case 9:
/*      */         case 11:
/*      */         default:
/*  957 */           state = 99; break;
/*      */         }
/*      */       }
/*      */       else {
/*  961 */         switch (state) {
/*      */         case 0:
/*      */         case 3:
/*  964 */           state = 1;
/*  965 */           currentClasspathName = token;
/*  966 */           break;
/*      */         case 5:
/*  968 */           if (!token.startsWith("-d ")) break;
/*  969 */           if (currentDestinationPath != null) {
/*  970 */             throw new IllegalArgumentException(
/*  971 */               bind("configure.duplicateDestinationPathEntry", 
/*  972 */               currentPath));
/*      */           }
/*  974 */           currentDestinationPath = token.substring(3).trim();
/*  975 */           state = 7;
/*  976 */           break;
/*      */         case 4:
/*  980 */           if (currentDestinationPath != null) {
/*  981 */             throw new IllegalArgumentException(
/*  982 */               bind("configure.accessRuleAfterDestinationPath", 
/*  983 */               currentPath));
/*      */           }
/*  985 */           state = 6;
/*  986 */           currentRuleSpecs.add(token);
/*  987 */           break;
/*      */         case 9:
/*  989 */           if (!token.startsWith("-d ")) {
/*  990 */             state = 99; break label712;
/*      */           }
/*  992 */           currentDestinationPath = token.substring(3).trim();
/*  993 */           state = 7;
/*      */ 
/*  995 */           break;
/*      */         case 11:
/*  997 */           for (int i = bracket; i < cursor; i++) {
/*  998 */             currentClasspathName = currentClasspathName + (String)tokens.get(i);
/*      */           }
/* 1000 */           state = 1;
/* 1001 */           break;
/*      */         case 10:
/* 1003 */           break;
/*      */         case 1:
/*      */         case 2:
/*      */         case 6:
/*      */         case 7:
/* 1005 */         case 8: } state = 99;
/*      */       }
/*      */ 
/* 1008 */       if ((state == 11) && (cursor == tokensNb)) {
/* 1009 */         cursor = bracket + 1;
/* 1010 */         state = 5;
/*      */       }
/*      */     }
/* 1013 */     switch (state) {
/*      */     case 3:
/* 1015 */       break;
/*      */     case 1:
/*      */     case 2:
/*      */     case 8:
/* 1019 */       addNewEntry(paths, currentClasspathName, currentRuleSpecs, 
/* 1020 */         customEncoding, currentDestinationPath, isSourceOnly, 
/* 1021 */         rejectDestinationPathOnJars);
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
/* 1035 */     int rulesSpecsSize = currentRuleSpecs.size();
/* 1036 */     AccessRuleSet accessRuleSet = null;
/* 1037 */     if (rulesSpecsSize != 0) {
/* 1038 */       AccessRule[] accessRules = new AccessRule[currentRuleSpecs.size()];
/* 1039 */       boolean rulesOK = true;
/* 1040 */       Iterator i = currentRuleSpecs.iterator();
/* 1041 */       int j = 0;
/* 1042 */       while (i.hasNext()) {
/* 1043 */         String ruleSpec = (String)i.next();
/* 1044 */         char key = ruleSpec.charAt(0);
/* 1045 */         String pattern = ruleSpec.substring(1);
/* 1046 */         if (pattern.length() > 0)
/* 1047 */           switch (key) {
/*      */           case '+':
/* 1049 */             accessRules[(j++)] = 
/* 1050 */               new AccessRule(pattern
/* 1050 */               .toCharArray(), 0);
/* 1051 */             break;
/*      */           case '~':
/* 1053 */             accessRules[(j++)] = 
/* 1055 */               new AccessRule(pattern
/* 1054 */               .toCharArray(), 
/* 1055 */               16777496);
/* 1056 */             break;
/*      */           case '-':
/* 1058 */             accessRules[(j++)] = 
/* 1060 */               new AccessRule(pattern
/* 1059 */               .toCharArray(), 
/* 1060 */               16777523);
/* 1061 */             break;
/*      */           case '?':
/* 1063 */             accessRules[(j++)] = 
/* 1065 */               new AccessRule(pattern
/* 1064 */               .toCharArray(), 
/* 1065 */               16777523, true);
/* 1066 */             break;
/*      */           default:
/* 1068 */             rulesOK = false; break;
/*      */           }
/*      */         else {
/* 1071 */           rulesOK = false;
/*      */         }
/*      */       }
/* 1074 */       if (rulesOK)
/* 1075 */         accessRuleSet = new AccessRuleSet(accessRules, 0, currentClasspathName);
/*      */       else {
/* 1077 */         return;
/*      */       }
/*      */     }
/* 1080 */     if ("none".equals(destPath)) {
/* 1081 */       destPath = "none";
/*      */     }
/* 1083 */     if ((rejectDestinationPathOnJars) && (destPath != null) && (
/* 1084 */       (currentClasspathName.endsWith(".jar")) || 
/* 1085 */       (currentClasspathName.endsWith(".zip")))) {
/* 1086 */       throw new IllegalArgumentException(
/* 1087 */         bind("configure.unexpectedDestinationPathEntryFile", 
/* 1088 */         currentClasspathName));
/*      */     }
/* 1090 */     FileSystem.Classpath currentClasspath = FileSystem.getClasspath(
/* 1091 */       currentClasspathName, 
/* 1092 */       customEncoding, 
/* 1093 */       isSourceOnly, 
/* 1094 */       accessRuleSet, 
/* 1095 */       destPath);
/* 1096 */     if (currentClasspath != null)
/* 1097 */       paths.add(currentClasspath);
/*      */   }
/*      */ 
/*      */   private String bind(String id, String binding)
/*      */   {
/* 1105 */     return bind(id, new String[] { binding });
/*      */   }
/*      */ 
/*      */   private String bind(String id, String[] arguments)
/*      */   {
/* 1113 */     if (id == null)
/* 1114 */       return "No message available";
/* 1115 */     String message = null;
/*      */     try {
/* 1117 */       message = this.bundle.getString(id);
/*      */     }
/*      */     catch (MissingResourceException localMissingResourceException)
/*      */     {
/* 1121 */       return "Missing message: " + id + " in: " + "org.eclipse.jdt.internal.compiler.batch.messages";
/*      */     }
/* 1123 */     return MessageFormat.format(message, arguments);
/*      */   }
/*      */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\ecj-3.5.2.jar
 * Qualified Name:     org.eclipse.jdt.internal.compiler.tool.EclipseFileManager
 * JD-Core Version:    0.6.0
 */