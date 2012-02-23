/*      */ package org.eclipse.jdt.internal.compiler.batch;
/*      */ 
/*      */ import java.io.ByteArrayInputStream;
/*      */ import java.io.File;
/*      */ import java.io.FileNotFoundException;
/*      */ import java.io.FileOutputStream;
/*      */ import java.io.FilenameFilter;
/*      */ import java.io.IOException;
/*      */ import java.io.InputStreamReader;
/*      */ import java.io.LineNumberReader;
/*      */ import java.io.OutputStreamWriter;
/*      */ import java.io.PrintStream;
/*      */ import java.io.PrintWriter;
/*      */ import java.io.StringReader;
/*      */ import java.io.StringWriter;
/*      */ import java.io.UnsupportedEncodingException;
/*      */ import java.lang.reflect.Field;
/*      */ import java.text.DateFormat;
/*      */ import java.text.MessageFormat;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Arrays;
/*      */ import java.util.Date;
/*      */ import java.util.HashMap;
/*      */ import java.util.Iterator;
/*      */ import java.util.List;
/*      */ import java.util.Locale;
/*      */ import java.util.Map;
/*      */ import java.util.Map.Entry;
/*      */ import java.util.MissingResourceException;
/*      */ import java.util.ResourceBundle;
/*      */ import java.util.Set;
/*      */ import java.util.StringTokenizer;
/*      */ import org.eclipse.jdt.core.compiler.CategorizedProblem;
/*      */ import org.eclipse.jdt.core.compiler.CharOperation;
/*      */ import org.eclipse.jdt.core.compiler.CompilationProgress;
/*      */ import org.eclipse.jdt.internal.compiler.AbstractAnnotationProcessorManager;
/*      */ import org.eclipse.jdt.internal.compiler.ClassFile;
/*      */ import org.eclipse.jdt.internal.compiler.CompilationResult;
/*      */ import org.eclipse.jdt.internal.compiler.Compiler;
/*      */ import org.eclipse.jdt.internal.compiler.ICompilerRequestor;
/*      */ import org.eclipse.jdt.internal.compiler.IErrorHandlingPolicy;
/*      */ import org.eclipse.jdt.internal.compiler.IProblemFactory;
/*      */ import org.eclipse.jdt.internal.compiler.env.AccessRule;
/*      */ import org.eclipse.jdt.internal.compiler.env.AccessRuleSet;
/*      */ import org.eclipse.jdt.internal.compiler.env.ICompilationUnit;
/*      */ import org.eclipse.jdt.internal.compiler.impl.CompilerOptions;
/*      */ import org.eclipse.jdt.internal.compiler.impl.CompilerStats;
/*      */ import org.eclipse.jdt.internal.compiler.lookup.LookupEnvironment;
/*      */ import org.eclipse.jdt.internal.compiler.lookup.ReferenceBinding;
/*      */ import org.eclipse.jdt.internal.compiler.problem.AbortCompilation;
/*      */ import org.eclipse.jdt.internal.compiler.problem.DefaultProblemFactory;
/*      */ import org.eclipse.jdt.internal.compiler.problem.ProblemReporter;
/*      */ import org.eclipse.jdt.internal.compiler.problem.ProblemSeverities;
/*      */ import org.eclipse.jdt.internal.compiler.util.GenericXMLWriter;
/*      */ import org.eclipse.jdt.internal.compiler.util.HashtableOfInt;
/*      */ import org.eclipse.jdt.internal.compiler.util.HashtableOfObject;
/*      */ import org.eclipse.jdt.internal.compiler.util.Messages;
/*      */ import org.eclipse.jdt.internal.compiler.util.SuffixConstants;
/*      */ import org.eclipse.jdt.internal.compiler.util.Util;
/*      */ 
/*      */ public class Main
/*      */   implements ProblemSeverities, SuffixConstants
/*      */ {
/*      */   boolean enableJavadocOn;
/*      */   boolean warnJavadocOn;
/*      */   boolean warnAllJavadocOn;
/*      */   public Compiler batchCompiler;
/*      */   public ResourceBundle bundle;
/*      */   protected FileSystem.Classpath[] checkedClasspaths;
/*      */   public Locale compilerLocale;
/*      */   public CompilerOptions compilerOptions;
/*      */   public CompilationProgress progress;
/*      */   public String destinationPath;
/*      */   public String[] destinationPaths;
/*      */   private boolean didSpecifySource;
/*      */   private boolean didSpecifyTarget;
/*      */   public String[] encodings;
/*      */   public int exportedClassFilesCounter;
/*      */   public String[] filenames;
/*      */   public String[] classNames;
/*      */   public int globalErrorsCount;
/*      */   public int globalProblemsCount;
/*      */   public int globalTasksCount;
/*      */   public int globalWarningsCount;
/*      */   private File javaHomeCache;
/* 1284 */   private boolean javaHomeChecked = false;
/*      */   public long lineCount0;
/*      */   public String log;
/*      */   public Logger logger;
/*      */   public int maxProblems;
/*      */   public Map options;
/*      */   protected PrintWriter out;
/* 1293 */   public boolean proceed = true;
/* 1294 */   public boolean proceedOnError = false;
/* 1295 */   public boolean produceRefInfo = false;
/*      */   public int currentRepetition;
/*      */   public int maxRepetition;
/* 1297 */   public boolean showProgress = false;
/*      */   public long startTime;
/*      */   public ArrayList pendingErrors;
/* 1300 */   public boolean systemExitWhenFinished = true;
/*      */   public static final int TIMING_DISABLED = 0;
/*      */   public static final int TIMING_ENABLED = 1;
/*      */   public static final int TIMING_DETAILED = 2;
/* 1306 */   public int timing = 0;
/*      */   public CompilerStats[] compilerStats;
/* 1308 */   public boolean verbose = false;
/*      */   private String[] expandedCommandLine;
/*      */   private PrintWriter err;
/*      */   ArrayList extraProblems;
/*      */   public static final String bundleName = "org.eclipse.jdt.internal.compiler.batch.messages";
/*      */   public static final int DEFAULT_SIZE_CLASSPATH = 4;
/*      */   public static final String NONE = "none";
/*      */   static Class class$0;
/*      */ 
/*      */   /** @deprecated */
/*      */   public static boolean compile(String commandLine)
/*      */   {
/* 1326 */     return new Main(new PrintWriter(System.out), new PrintWriter(System.err), false, null, null).compile(tokenize(commandLine));
/*      */   }
/*      */ 
/*      */   /** @deprecated */
/*      */   public static boolean compile(String commandLine, PrintWriter outWriter, PrintWriter errWriter)
/*      */   {
/* 1334 */     return new Main(outWriter, errWriter, false, null, null).compile(tokenize(commandLine));
/*      */   }
/*      */ 
/*      */   public static boolean compile(String[] commandLineArguments, PrintWriter outWriter, PrintWriter errWriter, CompilationProgress progress)
/*      */   {
/* 1341 */     return new Main(outWriter, errWriter, false, null, progress).compile(commandLineArguments);
/*      */   }
/*      */   public static File[][] getLibrariesFiles(File[] files) {
/* 1344 */     FilenameFilter filter = new FilenameFilter() {
/*      */       public boolean accept(File dir, String name) {
/* 1346 */         return Util.isPotentialZipArchive(name);
/*      */       }
/*      */     };
/* 1349 */     int filesLength = files.length;
/* 1350 */     File[][] result = new File[filesLength][];
/* 1351 */     for (int i = 0; i < filesLength; i++) {
/* 1352 */       File currentFile = files[i];
/* 1353 */       if ((currentFile.exists()) && (currentFile.isDirectory())) {
/* 1354 */         result[i] = currentFile.listFiles(filter);
/*      */       }
/*      */     }
/* 1357 */     return result;
/*      */   }
/*      */ 
/*      */   public static void main(String[] argv) {
/* 1361 */     new Main(new PrintWriter(System.out), new PrintWriter(System.err), true, null, null).compile(argv);
/*      */   }
/*      */ 
/*      */   public static String[] tokenize(String commandLine)
/*      */   {
/* 1366 */     int count = 0;
/* 1367 */     String[] arguments = new String[10];
/* 1368 */     StringTokenizer tokenizer = new StringTokenizer(commandLine, " \"", true);
/* 1369 */     String token = Util.EMPTY_STRING;
/* 1370 */     boolean insideQuotes = false;
/* 1371 */     boolean startNewToken = true;
/*      */ 
/* 1378 */     while (tokenizer.hasMoreTokens()) {
/* 1379 */       token = tokenizer.nextToken();
/*      */ 
/* 1381 */       if (token.equals(" ")) {
/* 1382 */         if (insideQuotes)
/*      */         {
/*      */           int tmp59_58 = (count - 1);
/*      */           String[] tmp59_55 = arguments; tmp59_55[tmp59_58] = (tmp59_55[tmp59_58] + token);
/* 1384 */           startNewToken = false;
/*      */         } else {
/* 1386 */           startNewToken = true;
/*      */         }
/* 1388 */       } else if (token.equals("\"")) {
/* 1389 */         if ((!insideQuotes) && (startNewToken)) {
/* 1390 */           if (count == arguments.length)
/* 1391 */             System.arraycopy(arguments, 0, arguments = new String[count * 2], 0, count);
/* 1392 */           arguments[(count++)] = Util.EMPTY_STRING;
/*      */         }
/* 1394 */         insideQuotes = !insideQuotes;
/* 1395 */         startNewToken = false;
/*      */       } else {
/* 1397 */         if (insideQuotes)
/*      */         {
/*      */           int tmp170_169 = (count - 1);
/*      */           String[] tmp170_166 = arguments; tmp170_166[tmp170_169] = (tmp170_166[tmp170_169] + token);
/*      */         }
/* 1400 */         else if ((token.length() > 0) && (!startNewToken))
/*      */         {
/*      */           int tmp212_211 = (count - 1);
/*      */           String[] tmp212_208 = arguments; tmp212_208[tmp212_211] = (tmp212_208[tmp212_211] + token);
/*      */         } else {
/* 1403 */           if (count == arguments.length)
/* 1404 */             System.arraycopy(arguments, 0, arguments = new String[count * 2], 0, count);
/* 1405 */           String trimmedToken = token.trim();
/* 1406 */           if (trimmedToken.length() != 0) {
/* 1407 */             arguments[(count++)] = trimmedToken;
/*      */           }
/*      */         }
/*      */ 
/* 1411 */         startNewToken = false;
/*      */       }
/*      */     }
/* 1414 */     System.arraycopy(arguments, 0, arguments = new String[count], 0, count);
/* 1415 */     return arguments;
/*      */   }
/*      */ 
/*      */   /** @deprecated */
/*      */   public Main(PrintWriter outWriter, PrintWriter errWriter, boolean systemExitWhenFinished)
/*      */   {
/* 1423 */     this(outWriter, errWriter, systemExitWhenFinished, null, null);
/*      */   }
/*      */ 
/*      */   /** @deprecated */
/*      */   public Main(PrintWriter outWriter, PrintWriter errWriter, boolean systemExitWhenFinished, Map customDefaultOptions)
/*      */   {
/* 1431 */     this(outWriter, errWriter, systemExitWhenFinished, customDefaultOptions, null);
/*      */   }
/*      */ 
/*      */   public Main(PrintWriter outWriter, PrintWriter errWriter, boolean systemExitWhenFinished, Map customDefaultOptions, CompilationProgress compilationProgress) {
/* 1435 */     initialize(outWriter, errWriter, systemExitWhenFinished, customDefaultOptions, compilationProgress);
/* 1436 */     relocalize();
/*      */   }
/*      */ 
/*      */   public void addExtraProblems(CategorizedProblem problem) {
/* 1440 */     if (this.extraProblems == null) {
/* 1441 */       this.extraProblems = new ArrayList();
/*      */     }
/* 1443 */     this.extraProblems.add(problem);
/*      */   }
/*      */ 
/*      */   protected void addNewEntry(ArrayList paths, String currentClasspathName, ArrayList currentRuleSpecs, String customEncoding, String destPath, boolean isSourceOnly, boolean rejectDestinationPathOnJars)
/*      */   {
/* 1450 */     int rulesSpecsSize = currentRuleSpecs.size();
/* 1451 */     AccessRuleSet accessRuleSet = null;
/* 1452 */     if (rulesSpecsSize != 0) {
/* 1453 */       AccessRule[] accessRules = new AccessRule[currentRuleSpecs.size()];
/* 1454 */       boolean rulesOK = true;
/* 1455 */       Iterator i = currentRuleSpecs.iterator();
/* 1456 */       int j = 0;
/* 1457 */       while (i.hasNext()) {
/* 1458 */         String ruleSpec = (String)i.next();
/* 1459 */         char key = ruleSpec.charAt(0);
/* 1460 */         String pattern = ruleSpec.substring(1);
/* 1461 */         if (pattern.length() > 0)
/* 1462 */           switch (key) {
/*      */           case '+':
/* 1464 */             accessRules[(j++)] = 
/* 1465 */               new AccessRule(pattern
/* 1465 */               .toCharArray(), 0);
/* 1466 */             break;
/*      */           case '~':
/* 1468 */             accessRules[(j++)] = 
/* 1470 */               new AccessRule(pattern
/* 1469 */               .toCharArray(), 
/* 1470 */               16777496);
/* 1471 */             break;
/*      */           case '-':
/* 1473 */             accessRules[(j++)] = 
/* 1475 */               new AccessRule(pattern
/* 1474 */               .toCharArray(), 
/* 1475 */               16777523);
/* 1476 */             break;
/*      */           case '?':
/* 1478 */             accessRules[(j++)] = 
/* 1480 */               new AccessRule(pattern
/* 1479 */               .toCharArray(), 
/* 1480 */               16777523, true);
/* 1481 */             break;
/*      */           default:
/* 1483 */             rulesOK = false; break;
/*      */           }
/*      */         else {
/* 1486 */           rulesOK = false;
/*      */         }
/*      */       }
/* 1489 */       if (rulesOK) {
/* 1490 */         accessRuleSet = new AccessRuleSet(accessRules, 0, currentClasspathName);
/*      */       } else {
/* 1492 */         if (currentClasspathName.length() != 0)
/*      */         {
/* 1494 */           addPendingErrors(bind("configure.incorrectClasspath", currentClasspathName));
/*      */         }
/* 1496 */         return;
/*      */       }
/*      */     }
/* 1499 */     if ("none".equals(destPath)) {
/* 1500 */       destPath = "none";
/*      */     }
/* 1502 */     if ((rejectDestinationPathOnJars) && (destPath != null) && 
/* 1503 */       (Util.isPotentialZipArchive(currentClasspathName))) {
/* 1504 */       throw new IllegalArgumentException(
/* 1505 */         bind("configure.unexpectedDestinationPathEntryFile", 
/* 1506 */         currentClasspathName));
/*      */     }
/* 1508 */     FileSystem.Classpath currentClasspath = FileSystem.getClasspath(
/* 1509 */       currentClasspathName, 
/* 1510 */       customEncoding, 
/* 1511 */       isSourceOnly, 
/* 1512 */       accessRuleSet, 
/* 1513 */       destPath);
/* 1514 */     if (currentClasspath != null)
/* 1515 */       paths.add(currentClasspath);
/* 1516 */     else if (currentClasspathName.length() != 0)
/*      */     {
/* 1518 */       addPendingErrors(bind("configure.incorrectClasspath", currentClasspathName));
/*      */     }
/*      */   }
/*      */ 
/*      */   void addPendingErrors(String message) {
/* 1522 */     if (this.pendingErrors == null) {
/* 1523 */       this.pendingErrors = new ArrayList();
/*      */     }
/* 1525 */     this.pendingErrors.add(message);
/*      */   }
/*      */ 
/*      */   public String bind(String id)
/*      */   {
/* 1531 */     return bind(id, null);
/*      */   }
/*      */ 
/*      */   public String bind(String id, String binding)
/*      */   {
/* 1538 */     return bind(id, new String[] { binding });
/*      */   }
/*      */ 
/*      */   public String bind(String id, String binding1, String binding2)
/*      */   {
/* 1546 */     return bind(id, new String[] { binding1, binding2 });
/*      */   }
/*      */ 
/*      */   public String bind(String id, String[] arguments)
/*      */   {
/* 1554 */     if (id == null)
/* 1555 */       return "No message available";
/* 1556 */     String message = null;
/*      */     try {
/* 1558 */       message = this.bundle.getString(id);
/*      */     }
/*      */     catch (MissingResourceException localMissingResourceException)
/*      */     {
/* 1562 */       return "Missing message: " + id + " in: " + "org.eclipse.jdt.internal.compiler.batch.messages";
/*      */     }
/* 1564 */     return MessageFormat.format(message, arguments);
/*      */   }
/*      */ 
/*      */   private boolean checkVMVersion(long minimalSupportedVersion)
/*      */   {
/* 1585 */     String classFileVersion = System.getProperty("java.class.version");
/* 1586 */     if (classFileVersion == null)
/*      */     {
/* 1588 */       return false;
/*      */     }
/* 1590 */     int index = classFileVersion.indexOf('.');
/* 1591 */     if (index == -1)
/*      */     {
/* 1593 */       return false;
/*      */     }
/*      */     try
/*      */     {
/* 1597 */       majorVersion = Integer.parseInt(classFileVersion.substring(0, index));
/*      */     }
/*      */     catch (NumberFormatException localNumberFormatException)
/*      */     {
/*      */       int majorVersion;
/* 1600 */       return false;
/*      */     }
/*      */     int majorVersion;
/* 1602 */     switch (majorVersion) {
/*      */     case 45:
/* 1604 */       return 2949123L >= minimalSupportedVersion;
/*      */     case 46:
/* 1606 */       return 3014656L >= minimalSupportedVersion;
/*      */     case 47:
/* 1608 */       return 3080192L >= minimalSupportedVersion;
/*      */     case 48:
/* 1610 */       return 3145728L >= minimalSupportedVersion;
/*      */     case 49:
/* 1612 */       return 3211264L >= minimalSupportedVersion;
/*      */     case 50:
/* 1614 */       return 3276800L >= minimalSupportedVersion;
/*      */     case 51:
/* 1616 */       return 3342336L >= minimalSupportedVersion;
/*      */     }
/*      */ 
/* 1619 */     return false;
/*      */   }
/*      */ 
/*      */   public boolean compile(String[] argv)
/*      */   {
/*      */     try
/*      */     {
/* 1628 */       configure(argv);
/* 1629 */       if (this.progress != null)
/* 1630 */         this.progress.begin(this.filenames == null ? 0 : this.filenames.length * this.maxRepetition);
/* 1631 */       if (this.proceed)
/*      */       {
/* 1635 */         if (this.showProgress) this.logger.compiling();
/* 1636 */         for (this.currentRepetition = 0; this.currentRepetition < this.maxRepetition; this.currentRepetition += 1) {
/* 1637 */           this.globalProblemsCount = 0;
/* 1638 */           this.globalErrorsCount = 0;
/* 1639 */           this.globalWarningsCount = 0;
/* 1640 */           this.globalTasksCount = 0;
/* 1641 */           this.exportedClassFilesCounter = 0;
/*      */ 
/* 1643 */           if (this.maxRepetition > 1) {
/* 1644 */             this.logger.flush();
/* 1645 */             this.logger.logRepetition(this.currentRepetition, this.maxRepetition);
/*      */           }
/*      */ 
/* 1648 */           performCompilation();
/*      */         }
/* 1650 */         if (this.compilerStats != null) {
/* 1651 */           this.logger.logAverage();
/*      */         }
/* 1653 */         if (this.showProgress) this.logger.printNewLine();
/*      */       }
/* 1655 */       if (this.systemExitWhenFinished) {
/* 1656 */         this.logger.flush();
/* 1657 */         this.logger.close();
/* 1658 */         System.exit(this.globalErrorsCount > 0 ? -1 : 0);
/*      */       }
/*      */     } catch (IllegalArgumentException e) {
/* 1661 */       this.logger.logException(e);
/* 1662 */       if (this.systemExitWhenFinished) {
/* 1663 */         this.logger.flush();
/* 1664 */         this.logger.close();
/* 1665 */         System.exit(-1);
/*      */       }
/*      */       return false;
/*      */     } catch (RuntimeException e) {
/* 1669 */       this.logger.logException(e);
/* 1670 */       if (this.systemExitWhenFinished) {
/* 1671 */         this.logger.flush();
/* 1672 */         this.logger.close();
/* 1673 */         System.exit(-1);
/*      */       }
/*      */       return false;
/*      */     } finally {
/* 1677 */       this.logger.flush();
/* 1678 */       this.logger.close();
/* 1679 */       if (this.progress != null)
/* 1680 */         this.progress.done();
/*      */     }
/* 1677 */     this.logger.flush();
/* 1678 */     this.logger.close();
/* 1679 */     if (this.progress != null) {
/* 1680 */       this.progress.done();
/*      */     }
/*      */ 
/* 1683 */     return (this.globalErrorsCount == 0) && ((this.progress == null) || (!this.progress.isCanceled()));
/*      */   }
/*      */ 
/*      */   public void configure(String[] argv)
/*      */   {
/* 1692 */     if ((argv == null) || (argv.length == 0)) {
/* 1693 */       printUsage();
/* 1694 */       return;
/*      */     }
/*      */ 
/* 1716 */     ArrayList bootclasspaths = new ArrayList(4);
/* 1717 */     String sourcepathClasspathArg = null;
/* 1718 */     ArrayList sourcepathClasspaths = new ArrayList(4);
/* 1719 */     ArrayList classpaths = new ArrayList(4);
/* 1720 */     ArrayList extdirsClasspaths = null;
/* 1721 */     ArrayList endorsedDirClasspaths = null;
/*      */ 
/* 1723 */     int index = -1;
/* 1724 */     int filesCount = 0;
/* 1725 */     int classCount = 0;
/* 1726 */     int argCount = argv.length;
/* 1727 */     int mode = 0;
/* 1728 */     this.maxRepetition = 0;
/* 1729 */     boolean printUsageRequired = false;
/* 1730 */     String usageSection = null;
/* 1731 */     boolean printVersionRequired = false;
/*      */ 
/* 1733 */     boolean didSpecifyDefaultEncoding = false;
/* 1734 */     boolean didSpecifyDeprecation = false;
/* 1735 */     boolean didSpecifyCompliance = false;
/* 1736 */     boolean didSpecifyDisabledAnnotationProcessing = false;
/*      */ 
/* 1738 */     String customEncoding = null;
/* 1739 */     String customDestinationPath = null;
/* 1740 */     String currentSourceDirectory = null;
/* 1741 */     String currentArg = Util.EMPTY_STRING;
/*      */ 
/* 1744 */     boolean needExpansion = false;
/* 1745 */     for (int i = 0; i < argCount; i++) {
/* 1746 */       if (argv[i].startsWith("@")) {
/* 1747 */         needExpansion = true;
/* 1748 */         break;
/*      */       }
/*      */     }
/*      */ 
/* 1752 */     String[] newCommandLineArgs = (String[])null;
/* 1753 */     if (needExpansion) {
/* 1754 */       newCommandLineArgs = new String[argCount];
/* 1755 */       index = 0;
/* 1756 */       for (int i = 0; i < argCount; i++) {
/* 1757 */         String[] newArgs = (String[])null;
/* 1758 */         String arg = argv[i].trim();
/* 1759 */         if (arg.startsWith("@")) {
/*      */           try {
/* 1761 */             LineNumberReader reader = new LineNumberReader(new StringReader(new String(Util.getFileCharContent(new File(arg.substring(1)), null))));
/* 1762 */             StringBuffer buffer = new StringBuffer();
/*      */             String line;
/* 1764 */             while ((line = reader.readLine()) != null) {
/* 1765 */               String line = line.trim();
/* 1766 */               if (!line.startsWith("#")) {
/* 1767 */                 buffer.append(line).append(" ");
/*      */               }
/*      */             }
/* 1770 */             newArgs = tokenize(buffer.toString());
/*      */           } catch (IOException localIOException) {
/* 1772 */             throw new IllegalArgumentException(
/* 1773 */               bind("configure.invalidexpansionargumentname", arg));
/*      */           }
/*      */         }
/* 1776 */         if (newArgs != null) {
/* 1777 */           int newCommandLineArgsLength = newCommandLineArgs.length;
/* 1778 */           int newArgsLength = newArgs.length;
/* 1779 */           System.arraycopy(newCommandLineArgs, 0, newCommandLineArgs = new String[newCommandLineArgsLength + newArgsLength - 1], 0, index);
/* 1780 */           System.arraycopy(newArgs, 0, newCommandLineArgs, index, newArgsLength);
/* 1781 */           index += newArgsLength;
/*      */         } else {
/* 1783 */           newCommandLineArgs[(index++)] = arg;
/*      */         }
/*      */       }
/* 1786 */       index = -1;
/*      */     } else {
/* 1788 */       newCommandLineArgs = argv;
/* 1789 */       for (int i = 0; i < argCount; i++) {
/* 1790 */         newCommandLineArgs[i] = newCommandLineArgs[i].trim();
/*      */       }
/* 1793 */     }argCount = newCommandLineArgs.length;
/* 1794 */     this.expandedCommandLine = newCommandLineArgs;
/*      */     label4976: 
/*      */     do {
/* 1797 */       if (customEncoding != null) {
/* 1798 */         throw new IllegalArgumentException(
/* 1799 */           bind("configure.unexpectedCustomEncoding", currentArg, customEncoding));
/*      */       }
/*      */ 
/* 1802 */       currentArg = newCommandLineArgs[index];
/*      */ 
/* 1804 */       switch (mode) {
/*      */       case 0:
/* 1806 */         if (currentArg.startsWith("[")) {
/* 1807 */           throw new IllegalArgumentException(
/* 1808 */             bind("configure.unexpectedBracket", 
/* 1809 */             currentArg));
/*      */         }
/*      */ 
/* 1812 */         if (currentArg.endsWith("]"))
/*      */         {
/* 1814 */           int encodingStart = currentArg.indexOf('[') + 1;
/* 1815 */           if (encodingStart <= 1) {
/* 1816 */             throw new IllegalArgumentException(
/* 1817 */               bind("configure.unexpectedBracket", currentArg));
/*      */           }
/* 1819 */           int encodingEnd = currentArg.length() - 1;
/* 1820 */           if (encodingStart >= 1) {
/* 1821 */             if (encodingStart < encodingEnd) {
/* 1822 */               customEncoding = currentArg.substring(encodingStart, encodingEnd);
/*      */               try {
/* 1824 */                 new InputStreamReader(new ByteArrayInputStream(new byte[0]), customEncoding);
/*      */               } catch (UnsupportedEncodingException localUnsupportedEncodingException1) {
/* 1826 */                 throw new IllegalArgumentException(
/* 1827 */                   bind("configure.unsupportedEncoding", customEncoding));
/*      */               }
/*      */             }
/* 1830 */             currentArg = currentArg.substring(0, encodingStart - 1);
/*      */           }
/*      */         }
/*      */ 
/* 1834 */         if (currentArg.endsWith(".java")) {
/* 1835 */           if (this.filenames == null) {
/* 1836 */             this.filenames = new String[argCount - index];
/* 1837 */             this.encodings = new String[argCount - index];
/* 1838 */             this.destinationPaths = new String[argCount - index];
/* 1839 */           } else if (filesCount == this.filenames.length) {
/* 1840 */             int length = this.filenames.length;
/* 1841 */             System.arraycopy(
/* 1842 */               this.filenames, 
/* 1843 */               0, 
/* 1844 */               this.filenames = new String[length + argCount - index], 
/* 1845 */               0, 
/* 1846 */               length);
/* 1847 */             System.arraycopy(
/* 1848 */               this.encodings, 
/* 1849 */               0, 
/* 1850 */               this.encodings = new String[length + argCount - index], 
/* 1851 */               0, 
/* 1852 */               length);
/* 1853 */             System.arraycopy(
/* 1854 */               this.destinationPaths, 
/* 1855 */               0, 
/* 1856 */               this.destinationPaths = new String[length + argCount - index], 
/* 1857 */               0, 
/* 1858 */               length);
/*      */           }
/* 1860 */           this.filenames[filesCount] = currentArg;
/* 1861 */           this.encodings[(filesCount++)] = customEncoding;
/*      */ 
/* 1863 */           customEncoding = null;
/* 1864 */           mode = 0;
/* 1865 */           break label4976;
/*      */         }
/* 1867 */         if (currentArg.equals("-log")) {
/* 1868 */           if (this.log != null)
/* 1869 */             throw new IllegalArgumentException(
/* 1870 */               bind("configure.duplicateLog", currentArg));
/* 1871 */           mode = 5;
/* 1872 */           break label4976;
/*      */         }
/* 1874 */         if (currentArg.equals("-repeat")) {
/* 1875 */           if (this.maxRepetition > 0)
/* 1876 */             throw new IllegalArgumentException(
/* 1877 */               bind("configure.duplicateRepeat", currentArg));
/* 1878 */           mode = 6;
/* 1879 */           break label4976;
/*      */         }
/* 1881 */         if (currentArg.equals("-maxProblems")) {
/* 1882 */           if (this.maxProblems > 0)
/* 1883 */             throw new IllegalArgumentException(
/* 1884 */               bind("configure.duplicateMaxProblems", currentArg));
/* 1885 */           mode = 11;
/* 1886 */           break label4976;
/*      */         }
/* 1888 */         if (currentArg.equals("-source")) {
/* 1889 */           mode = 7;
/* 1890 */           break label4976;
/*      */         }
/* 1892 */         if (currentArg.equals("-encoding")) {
/* 1893 */           mode = 8;
/* 1894 */           break label4976;
/*      */         }
/* 1896 */         if (currentArg.equals("-1.3")) {
/* 1897 */           if (didSpecifyCompliance) {
/* 1898 */             throw new IllegalArgumentException(
/* 1899 */               bind("configure.duplicateCompliance", currentArg));
/*      */           }
/* 1901 */           didSpecifyCompliance = true;
/* 1902 */           this.options.put("org.eclipse.jdt.core.compiler.compliance", "1.3");
/* 1903 */           mode = 0;
/* 1904 */           break label4976;
/*      */         }
/* 1906 */         if (currentArg.equals("-1.4")) {
/* 1907 */           if (didSpecifyCompliance) {
/* 1908 */             throw new IllegalArgumentException(
/* 1909 */               bind("configure.duplicateCompliance", currentArg));
/*      */           }
/* 1911 */           didSpecifyCompliance = true;
/* 1912 */           this.options.put("org.eclipse.jdt.core.compiler.compliance", "1.4");
/* 1913 */           mode = 0;
/* 1914 */           break label4976;
/*      */         }
/* 1916 */         if ((currentArg.equals("-1.5")) || (currentArg.equals("-5")) || (currentArg.equals("-5.0"))) {
/* 1917 */           if (didSpecifyCompliance) {
/* 1918 */             throw new IllegalArgumentException(
/* 1919 */               bind("configure.duplicateCompliance", currentArg));
/*      */           }
/* 1921 */           didSpecifyCompliance = true;
/* 1922 */           this.options.put("org.eclipse.jdt.core.compiler.compliance", "1.5");
/* 1923 */           mode = 0;
/* 1924 */           break label4976;
/*      */         }
/* 1926 */         if ((currentArg.equals("-1.6")) || (currentArg.equals("-6")) || (currentArg.equals("-6.0"))) {
/* 1927 */           if (didSpecifyCompliance) {
/* 1928 */             throw new IllegalArgumentException(
/* 1929 */               bind("configure.duplicateCompliance", currentArg));
/*      */           }
/* 1931 */           didSpecifyCompliance = true;
/* 1932 */           this.options.put("org.eclipse.jdt.core.compiler.compliance", "1.6");
/* 1933 */           mode = 0;
/* 1934 */           break label4976;
/*      */         }
/* 1936 */         if ((currentArg.equals("-1.7")) || (currentArg.equals("-7")) || (currentArg.equals("-7.0"))) {
/* 1937 */           if (didSpecifyCompliance) {
/* 1938 */             throw new IllegalArgumentException(
/* 1939 */               bind("configure.duplicateCompliance", currentArg));
/*      */           }
/* 1941 */           didSpecifyCompliance = true;
/* 1942 */           this.options.put("org.eclipse.jdt.core.compiler.compliance", "1.7");
/* 1943 */           mode = 0;
/* 1944 */           break label4976;
/*      */         }
/* 1946 */         if (currentArg.equals("-d")) {
/* 1947 */           if (this.destinationPath != null) {
/* 1948 */             StringBuffer errorMessage = new StringBuffer();
/* 1949 */             errorMessage.append(currentArg);
/* 1950 */             if (index + 1 < argCount) {
/* 1951 */               errorMessage.append(' ');
/* 1952 */               errorMessage.append(newCommandLineArgs[(index + 1)]);
/*      */             }
/* 1954 */             throw new IllegalArgumentException(
/* 1955 */               bind("configure.duplicateOutputPath", errorMessage.toString()));
/*      */           }
/* 1957 */           mode = 3;
/* 1958 */           break label4976;
/*      */         }
/* 1960 */         if ((currentArg.equals("-classpath")) || 
/* 1961 */           (currentArg.equals("-cp"))) {
/* 1962 */           mode = 1;
/* 1963 */           break label4976;
/*      */         }
/* 1965 */         if (currentArg.equals("-bootclasspath")) {
/* 1966 */           if (bootclasspaths.size() > 0) {
/* 1967 */             StringBuffer errorMessage = new StringBuffer();
/* 1968 */             errorMessage.append(currentArg);
/* 1969 */             if (index + 1 < argCount) {
/* 1970 */               errorMessage.append(' ');
/* 1971 */               errorMessage.append(newCommandLineArgs[(index + 1)]);
/*      */             }
/* 1973 */             throw new IllegalArgumentException(
/* 1974 */               bind("configure.duplicateBootClasspath", errorMessage.toString()));
/*      */           }
/* 1976 */           mode = 9;
/* 1977 */           break label4976;
/*      */         }
/* 1979 */         if (currentArg.equals("-sourcepath")) {
/* 1980 */           if (sourcepathClasspathArg != null) {
/* 1981 */             StringBuffer errorMessage = new StringBuffer();
/* 1982 */             errorMessage.append(currentArg);
/* 1983 */             if (index + 1 < argCount) {
/* 1984 */               errorMessage.append(' ');
/* 1985 */               errorMessage.append(newCommandLineArgs[(index + 1)]);
/*      */             }
/* 1987 */             throw new IllegalArgumentException(
/* 1988 */               bind("configure.duplicateSourcepath", errorMessage.toString()));
/*      */           }
/* 1990 */           mode = 13;
/* 1991 */           break label4976;
/*      */         }
/* 1993 */         if (currentArg.equals("-extdirs")) {
/* 1994 */           if (extdirsClasspaths != null) {
/* 1995 */             StringBuffer errorMessage = new StringBuffer();
/* 1996 */             errorMessage.append(currentArg);
/* 1997 */             if (index + 1 < argCount) {
/* 1998 */               errorMessage.append(' ');
/* 1999 */               errorMessage.append(newCommandLineArgs[(index + 1)]);
/*      */             }
/* 2001 */             throw new IllegalArgumentException(
/* 2002 */               bind("configure.duplicateExtDirs", errorMessage.toString()));
/*      */           }
/* 2004 */           mode = 12;
/* 2005 */           break label4976;
/*      */         }
/* 2007 */         if (currentArg.equals("-endorseddirs")) {
/* 2008 */           if (endorsedDirClasspaths != null) {
/* 2009 */             StringBuffer errorMessage = new StringBuffer();
/* 2010 */             errorMessage.append(currentArg);
/* 2011 */             if (index + 1 < argCount) {
/* 2012 */               errorMessage.append(' ');
/* 2013 */               errorMessage.append(newCommandLineArgs[(index + 1)]);
/*      */             }
/* 2015 */             throw new IllegalArgumentException(
/* 2016 */               bind("configure.duplicateEndorsedDirs", errorMessage.toString()));
/*      */           }
/* 2018 */           mode = 15;
/* 2019 */           break label4976;
/*      */         }
/* 2021 */         if (currentArg.equals("-progress")) {
/* 2022 */           mode = 0;
/* 2023 */           this.showProgress = true;
/* 2024 */           break label4976;
/*      */         }
/* 2026 */         if (currentArg.equals("-proceedOnError")) {
/* 2027 */           mode = 0;
/* 2028 */           this.proceedOnError = true;
/* 2029 */           break label4976;
/*      */         }
/* 2031 */         if (currentArg.equals("-time")) {
/* 2032 */           mode = 0;
/* 2033 */           this.timing = 1;
/* 2034 */           break label4976;
/*      */         }
/* 2036 */         if (currentArg.equals("-time:detail")) {
/* 2037 */           mode = 0;
/* 2038 */           this.timing = 3;
/* 2039 */           break label4976;
/*      */         }
/* 2041 */         if ((currentArg.equals("-version")) || 
/* 2042 */           (currentArg.equals("-v"))) {
/* 2043 */           this.logger.logVersion(true);
/* 2044 */           this.proceed = false;
/* 2045 */           return;
/*      */         }
/* 2047 */         if (currentArg.equals("-showversion")) {
/* 2048 */           printVersionRequired = true;
/* 2049 */           mode = 0;
/* 2050 */           break label4976;
/*      */         }
/* 2052 */         if ("-deprecation".equals(currentArg)) {
/* 2053 */           didSpecifyDeprecation = true;
/* 2054 */           this.options.put("org.eclipse.jdt.core.compiler.problem.deprecation", "warning");
/* 2055 */           mode = 0;
/* 2056 */           break label4976;
/*      */         }
/* 2058 */         if ((currentArg.equals("-help")) || (currentArg.equals("-?"))) {
/* 2059 */           printUsageRequired = true;
/* 2060 */           mode = 0;
/* 2061 */           break label4976;
/*      */         }
/* 2063 */         if ((currentArg.equals("-help:warn")) || 
/* 2064 */           (currentArg.equals("-?:warn"))) {
/* 2065 */           printUsageRequired = true;
/* 2066 */           usageSection = "misc.usage.warn";
/* 2067 */           break label4976;
/*      */         }
/* 2069 */         if (currentArg.equals("-noExit")) {
/* 2070 */           this.systemExitWhenFinished = false;
/* 2071 */           mode = 0;
/* 2072 */           break label4976;
/*      */         }
/* 2074 */         if (currentArg.equals("-verbose")) {
/* 2075 */           this.verbose = true;
/* 2076 */           mode = 0;
/* 2077 */           break label4976;
/*      */         }
/* 2079 */         if (currentArg.equals("-referenceInfo")) {
/* 2080 */           this.produceRefInfo = true;
/* 2081 */           mode = 0;
/* 2082 */           break label4976;
/*      */         }
/* 2084 */         if (currentArg.equals("-inlineJSR")) {
/* 2085 */           mode = 0;
/* 2086 */           this.options.put(
/* 2087 */             "org.eclipse.jdt.core.compiler.codegen.inlineJsrBytecode", 
/* 2088 */             "enabled");
/* 2089 */           break label4976;
/*      */         }
/* 2091 */         if (currentArg.startsWith("-g")) {
/* 2092 */           mode = 0;
/* 2093 */           String debugOption = currentArg;
/* 2094 */           int length = currentArg.length();
/* 2095 */           if (length == 2) {
/* 2096 */             this.options.put(
/* 2097 */               "org.eclipse.jdt.core.compiler.debug.localVariable", 
/* 2098 */               "generate");
/* 2099 */             this.options.put(
/* 2100 */               "org.eclipse.jdt.core.compiler.debug.lineNumber", 
/* 2101 */               "generate");
/* 2102 */             this.options.put(
/* 2103 */               "org.eclipse.jdt.core.compiler.debug.sourceFile", 
/* 2104 */               "generate");
/* 2105 */             break label4976;
/*      */           }
/* 2107 */           if (length > 3) {
/* 2108 */             this.options.put(
/* 2109 */               "org.eclipse.jdt.core.compiler.debug.localVariable", 
/* 2110 */               "do not generate");
/* 2111 */             this.options.put(
/* 2112 */               "org.eclipse.jdt.core.compiler.debug.lineNumber", 
/* 2113 */               "do not generate");
/* 2114 */             this.options.put(
/* 2115 */               "org.eclipse.jdt.core.compiler.debug.sourceFile", 
/* 2116 */               "do not generate");
/* 2117 */             if ((length == 7) && (debugOption.equals("-g:none"))) break label4976;
/* 2119 */             StringTokenizer tokenizer = 
/* 2120 */               new StringTokenizer(debugOption.substring(3, debugOption.length()), ",");
/* 2121 */             while (tokenizer.hasMoreTokens()) {
/* 2122 */               String token = tokenizer.nextToken();
/* 2123 */               if (token.equals("vars"))
/* 2124 */                 this.options.put(
/* 2125 */                   "org.eclipse.jdt.core.compiler.debug.localVariable", 
/* 2126 */                   "generate");
/* 2127 */               else if (token.equals("lines"))
/* 2128 */                 this.options.put(
/* 2129 */                   "org.eclipse.jdt.core.compiler.debug.lineNumber", 
/* 2130 */                   "generate");
/* 2131 */               else if (token.equals("source"))
/* 2132 */                 this.options.put(
/* 2133 */                   "org.eclipse.jdt.core.compiler.debug.sourceFile", 
/* 2134 */                   "generate");
/*      */               else {
/* 2136 */                 throw new IllegalArgumentException(
/* 2137 */                   bind("configure.invalidDebugOption", debugOption));
/*      */               }
/*      */             }
/* 2140 */             break label4976;
/*      */           }
/* 2142 */           throw new IllegalArgumentException(
/* 2143 */             bind("configure.invalidDebugOption", debugOption));
/*      */         }
/* 2145 */         if (currentArg.startsWith("-nowarn")) {
/* 2146 */           disableWarnings();
/* 2147 */           mode = 0;
/* 2148 */           break label4976;
/*      */         }
/* 2150 */         if (currentArg.startsWith("-warn")) {
/* 2151 */           mode = 0;
/* 2152 */           String warningOption = currentArg;
/* 2153 */           int length = currentArg.length();
/* 2154 */           if ((length == 10) && (warningOption.equals("-warn:none"))) {
/* 2155 */             disableWarnings();
/* 2156 */             break label4976;
/*      */           }
/* 2158 */           if (length <= 6)
/* 2159 */             throw new IllegalArgumentException(
/* 2160 */               bind("configure.invalidWarningConfiguration", warningOption));
/*      */           boolean allowPlusOrMinus;
/*      */           boolean allowPlusOrMinus;
/*      */           int warnTokenStart;
/*      */           boolean isEnabling;
/*      */           boolean allowPlusOrMinus;
/* 2164 */           switch (warningOption.charAt(6)) {
/*      */           case '+':
/* 2166 */             int warnTokenStart = 7;
/* 2167 */             boolean isEnabling = true;
/* 2168 */             allowPlusOrMinus = true;
/* 2169 */             break;
/*      */           case '-':
/* 2171 */             int warnTokenStart = 7;
/* 2172 */             boolean isEnabling = false;
/* 2173 */             allowPlusOrMinus = true;
/* 2174 */             break;
/*      */           case ',':
/*      */           default:
/* 2176 */             disableWarnings();
/* 2177 */             warnTokenStart = 6;
/* 2178 */             isEnabling = true;
/* 2179 */             allowPlusOrMinus = false;
/*      */           }
/*      */ 
/* 2182 */           StringTokenizer tokenizer = 
/* 2183 */             new StringTokenizer(warningOption.substring(warnTokenStart, warningOption.length()), ",");
/* 2184 */           int tokenCounter = 0;
/*      */ 
/* 2186 */           if (didSpecifyDeprecation) {
/* 2187 */             this.options.put("org.eclipse.jdt.core.compiler.problem.deprecation", "warning");
/*      */           }
/*      */ 
/* 2190 */           while (tokenizer.hasMoreTokens()) {
/* 2191 */             String token = tokenizer.nextToken();
/* 2192 */             tokenCounter++;
/* 2193 */             switch (token.charAt(0)) {
/*      */             case '+':
/* 2195 */               if (allowPlusOrMinus) {
/* 2196 */                 isEnabling = true;
/* 2197 */                 token = token.substring(1);
/*      */               } else {
/* 2199 */                 tokenCounter = 0;
/* 2200 */               }break;
/*      */             case '-':
/* 2204 */               if (allowPlusOrMinus) {
/* 2205 */                 isEnabling = false;
/* 2206 */                 token = token.substring(1);
/*      */               } else {
/* 2208 */                 tokenCounter = 0;
/* 2209 */               }break;
/*      */             case ',':
/*      */             }
/*      */ 
/* 2213 */             handleWarningToken(token, isEnabling);
/*      */           }
/* 2215 */           if (tokenCounter != 0) break label4976; throw new IllegalArgumentException(
/* 2217 */             bind("configure.invalidWarningOption", currentArg));
/*      */         }
/*      */ 
/* 2221 */         if (currentArg.equals("-target")) {
/* 2222 */           mode = 4;
/* 2223 */           break label4976;
/*      */         }
/* 2225 */         if (currentArg.equals("-preserveAllLocals")) {
/* 2226 */           this.options.put(
/* 2227 */             "org.eclipse.jdt.core.compiler.codegen.unusedLocal", 
/* 2228 */             "preserve");
/* 2229 */           mode = 0;
/* 2230 */           break label4976;
/*      */         }
/* 2232 */         if (currentArg.equals("-enableJavadoc")) {
/* 2233 */           mode = 0;
/* 2234 */           this.enableJavadocOn = true;
/* 2235 */           break label4976;
/*      */         }
/* 2237 */         if (currentArg.equals("-Xemacs")) {
/* 2238 */           mode = 0;
/* 2239 */           this.logger.setEmacs();
/* 2240 */           break label4976;
/*      */         }
/*      */ 
/* 2243 */         if (currentArg.startsWith("-A")) {
/* 2244 */           mode = 0;
/* 2245 */           break label4976;
/*      */         }
/* 2247 */         if (currentArg.equals("-processorpath")) {
/* 2248 */           mode = 17;
/* 2249 */           break label4976;
/*      */         }
/* 2251 */         if (currentArg.equals("-processor")) {
/* 2252 */           mode = 18;
/* 2253 */           break label4976;
/*      */         }
/* 2255 */         if (currentArg.equals("-proc:only")) {
/* 2256 */           this.options.put(
/* 2257 */             "org.eclipse.jdt.core.compiler.generateClassFiles", 
/* 2258 */             "disabled");
/* 2259 */           mode = 0;
/* 2260 */           break label4976;
/*      */         }
/* 2262 */         if (currentArg.equals("-proc:none")) {
/* 2263 */           didSpecifyDisabledAnnotationProcessing = true;
/* 2264 */           this.options.put(
/* 2265 */             "org.eclipse.jdt.core.compiler.processAnnotations", 
/* 2266 */             "disabled");
/* 2267 */           mode = 0;
/* 2268 */           break label4976;
/*      */         }
/* 2270 */         if (currentArg.equals("-s")) {
/* 2271 */           mode = 19;
/* 2272 */           break label4976;
/*      */         }
/* 2274 */         if ((currentArg.equals("-XprintProcessorInfo")) || 
/* 2275 */           (currentArg.equals("-XprintRounds"))) {
/* 2276 */           mode = 0;
/* 2277 */           break label4976;
/*      */         }
/*      */ 
/* 2280 */         if (currentArg.startsWith("-X")) {
/* 2281 */           mode = 0;
/* 2282 */           break label4976;
/*      */         }
/* 2284 */         if (currentArg.startsWith("-J")) {
/* 2285 */           mode = 0;
/* 2286 */           break label4976;
/*      */         }
/* 2288 */         if (currentArg.equals("-O")) {
/* 2289 */           mode = 0;
/* 2290 */           break label4976;
/*      */         }
/* 2292 */         if (!currentArg.equals("-classNames")) break;
/* 2293 */         mode = 20;
/* 2294 */         break;
/*      */       case 4:
/* 2298 */         if (this.didSpecifyTarget) {
/* 2299 */           throw new IllegalArgumentException(
/* 2300 */             bind("configure.duplicateTarget", currentArg));
/*      */         }
/* 2302 */         this.didSpecifyTarget = true;
/* 2303 */         if (currentArg.equals("1.1")) {
/* 2304 */           this.options.put("org.eclipse.jdt.core.compiler.codegen.targetPlatform", "1.1");
/* 2305 */         } else if (currentArg.equals("1.2")) {
/* 2306 */           this.options.put("org.eclipse.jdt.core.compiler.codegen.targetPlatform", "1.2");
/* 2307 */         } else if (currentArg.equals("1.3")) {
/* 2308 */           this.options.put("org.eclipse.jdt.core.compiler.codegen.targetPlatform", "1.3");
/* 2309 */         } else if (currentArg.equals("1.4")) {
/* 2310 */           this.options.put("org.eclipse.jdt.core.compiler.codegen.targetPlatform", "1.4");
/* 2311 */         } else if ((currentArg.equals("1.5")) || (currentArg.equals("5")) || (currentArg.equals("5.0"))) {
/* 2312 */           this.options.put("org.eclipse.jdt.core.compiler.codegen.targetPlatform", "1.5");
/* 2313 */         } else if ((currentArg.equals("1.6")) || (currentArg.equals("6")) || (currentArg.equals("6.0"))) {
/* 2314 */           this.options.put("org.eclipse.jdt.core.compiler.codegen.targetPlatform", "1.6");
/* 2315 */         } else if ((currentArg.equals("1.7")) || (currentArg.equals("7")) || (currentArg.equals("7.0"))) {
/* 2316 */           this.options.put("org.eclipse.jdt.core.compiler.codegen.targetPlatform", "1.7");
/* 2317 */         } else if (currentArg.equals("jsr14")) {
/* 2318 */           this.options.put("org.eclipse.jdt.core.compiler.codegen.targetPlatform", "jsr14");
/* 2319 */         } else if (currentArg.equals("cldc1.1")) {
/* 2320 */           this.options.put("org.eclipse.jdt.core.compiler.codegen.targetPlatform", "cldc1.1");
/* 2321 */           this.options.put("org.eclipse.jdt.core.compiler.codegen.inlineJsrBytecode", "enabled");
/*      */         } else {
/* 2323 */           throw new IllegalArgumentException(bind("configure.targetJDK", currentArg));
/*      */         }
/* 2325 */         mode = 0;
/* 2326 */         break;
/*      */       case 5:
/* 2328 */         this.log = currentArg;
/* 2329 */         mode = 0;
/* 2330 */         break;
/*      */       case 6:
/*      */         try {
/* 2333 */           this.maxRepetition = Integer.parseInt(currentArg);
/* 2334 */           if (this.maxRepetition <= 0)
/* 2335 */             throw new IllegalArgumentException(bind("configure.repetition", currentArg));
/*      */         }
/*      */         catch (NumberFormatException localNumberFormatException1) {
/* 2338 */           throw new IllegalArgumentException(bind("configure.repetition", currentArg));
/*      */         }
/* 2340 */         mode = 0;
/* 2341 */         break;
/*      */       case 11:
/*      */         try {
/* 2344 */           this.maxProblems = Integer.parseInt(currentArg);
/* 2345 */           if (this.maxProblems <= 0) {
/* 2346 */             throw new IllegalArgumentException(bind("configure.maxProblems", currentArg));
/*      */           }
/* 2348 */           this.options.put("org.eclipse.jdt.core.compiler.maxProblemPerUnit", currentArg);
/*      */         } catch (NumberFormatException localNumberFormatException2) {
/* 2350 */           throw new IllegalArgumentException(bind("configure.maxProblems", currentArg));
/*      */         }
/* 2352 */         mode = 0;
/* 2353 */         break;
/*      */       case 7:
/* 2355 */         if (this.didSpecifySource) {
/* 2356 */           throw new IllegalArgumentException(
/* 2357 */             bind("configure.duplicateSource", currentArg));
/*      */         }
/* 2359 */         this.didSpecifySource = true;
/* 2360 */         if (currentArg.equals("1.3"))
/* 2361 */           this.options.put("org.eclipse.jdt.core.compiler.source", "1.3");
/* 2362 */         else if (currentArg.equals("1.4"))
/* 2363 */           this.options.put("org.eclipse.jdt.core.compiler.source", "1.4");
/* 2364 */         else if ((currentArg.equals("1.5")) || (currentArg.equals("5")) || (currentArg.equals("5.0")))
/* 2365 */           this.options.put("org.eclipse.jdt.core.compiler.source", "1.5");
/* 2366 */         else if ((currentArg.equals("1.6")) || (currentArg.equals("6")) || (currentArg.equals("6.0")))
/* 2367 */           this.options.put("org.eclipse.jdt.core.compiler.source", "1.6");
/* 2368 */         else if ((currentArg.equals("1.7")) || (currentArg.equals("7")) || (currentArg.equals("7.0")))
/* 2369 */           this.options.put("org.eclipse.jdt.core.compiler.source", "1.7");
/*      */         else {
/* 2371 */           throw new IllegalArgumentException(bind("configure.source", currentArg));
/*      */         }
/* 2373 */         mode = 0;
/* 2374 */         break;
/*      */       case 8:
/* 2376 */         if (didSpecifyDefaultEncoding)
/* 2377 */           throw new IllegalArgumentException(
/* 2378 */             bind("configure.duplicateDefaultEncoding", currentArg));
/*      */         try
/*      */         {
/* 2381 */           new InputStreamReader(new ByteArrayInputStream(new byte[0]), currentArg);
/*      */         } catch (UnsupportedEncodingException localUnsupportedEncodingException2) {
/* 2383 */           throw new IllegalArgumentException(
/* 2384 */             bind("configure.unsupportedEncoding", currentArg));
/*      */         }
/* 2386 */         this.options.put("org.eclipse.jdt.core.encoding", currentArg);
/* 2387 */         didSpecifyDefaultEncoding = true;
/* 2388 */         mode = 0;
/* 2389 */         break;
/*      */       case 3:
/* 2391 */         setDestinationPath(currentArg.equals("none") ? "none" : currentArg);
/* 2392 */         mode = 0;
/* 2393 */         break;
/*      */       case 1:
/* 2395 */         mode = 0;
/* 2396 */         index += processPaths(newCommandLineArgs, index, currentArg, classpaths);
/* 2397 */         break;
/*      */       case 9:
/* 2399 */         mode = 0;
/* 2400 */         index += processPaths(newCommandLineArgs, index, currentArg, bootclasspaths);
/* 2401 */         break;
/*      */       case 13:
/* 2403 */         mode = 0;
/* 2404 */         String[] sourcePaths = new String[1];
/* 2405 */         index += processPaths(newCommandLineArgs, index, currentArg, sourcePaths);
/* 2406 */         sourcepathClasspathArg = sourcePaths[0];
/* 2407 */         break;
/*      */       case 12:
/* 2409 */         if (currentArg.indexOf("[-d") != -1) {
/* 2410 */           throw new IllegalArgumentException(
/* 2411 */             bind("configure.unexpectedDestinationPathEntry", 
/* 2412 */             "-extdir"));
/*      */         }
/* 2414 */         StringTokenizer tokenizer = new StringTokenizer(currentArg, File.pathSeparator, false);
/* 2415 */         extdirsClasspaths = new ArrayList(4);
/* 2416 */         while (tokenizer.hasMoreTokens())
/* 2417 */           extdirsClasspaths.add(tokenizer.nextToken());
/* 2418 */         mode = 0;
/* 2419 */         break;
/*      */       case 15:
/* 2421 */         if (currentArg.indexOf("[-d") != -1)
/* 2422 */           throw new IllegalArgumentException(
/* 2423 */             bind("configure.unexpectedDestinationPathEntry", 
/* 2424 */             "-endorseddirs"));
/* 2425 */         StringTokenizer tokenizer = new StringTokenizer(currentArg, File.pathSeparator, false);
/* 2426 */         endorsedDirClasspaths = new ArrayList(4);
/* 2427 */         while (tokenizer.hasMoreTokens())
/* 2428 */           endorsedDirClasspaths.add(tokenizer.nextToken());
/* 2429 */         mode = 0;
/* 2430 */         break;
/*      */       case 16:
/* 2432 */         if (currentArg.endsWith("]"))
/* 2433 */           customDestinationPath = currentArg.substring(0, 
/* 2434 */             currentArg.length() - 1);
/*      */         else {
/* 2436 */           throw new IllegalArgumentException(
/* 2437 */             bind("configure.incorrectDestinationPathEntry", 
/* 2438 */             "[-d " + currentArg));
/*      */         }
/*      */ 
/*      */       case 17:
/* 2443 */         mode = 0;
/* 2444 */         break;
/*      */       case 18:
/* 2447 */         mode = 0;
/* 2448 */         break;
/*      */       case 19:
/* 2451 */         mode = 0;
/* 2452 */         break;
/*      */       case 20:
/* 2454 */         StringTokenizer tokenizer = new StringTokenizer(currentArg, ",");
/* 2455 */         if (this.classNames == null) {
/* 2456 */           this.classNames = new String[4];
/*      */         }
/* 2458 */         while (tokenizer.hasMoreTokens()) {
/* 2459 */           if (this.classNames.length == classCount)
/*      */           {
/* 2461 */             System.arraycopy(
/* 2462 */               this.classNames, 
/* 2463 */               0, 
/* 2464 */               this.classNames = new String[classCount * 2], 
/* 2465 */               0, 
/* 2466 */               classCount);
/*      */           }
/* 2468 */           this.classNames[(classCount++)] = tokenizer.nextToken();
/*      */         }
/* 2470 */         mode = 0;
/* 2471 */         break;
/*      */       case 2:
/*      */       case 10:
/*      */       case 14:
/* 2475 */       }if (customDestinationPath == null) {
/* 2476 */         if (File.separatorChar != '/') {
/* 2477 */           currentArg = currentArg.replace('/', File.separatorChar);
/*      */         }
/* 2479 */         if (currentArg.endsWith("[-d")) {
/* 2480 */           currentSourceDirectory = currentArg.substring(0, 
/* 2481 */             currentArg.length() - 3);
/* 2482 */           mode = 16;
/*      */         }
/*      */         else {
/* 2485 */           currentSourceDirectory = currentArg;
/*      */         }
/*      */       } else {
/* 2487 */         File dir = new File(currentSourceDirectory);
/* 2488 */         if (!dir.isDirectory()) {
/* 2489 */           throw new IllegalArgumentException(
/* 2490 */             bind("configure.unrecognizedOption", currentSourceDirectory));
/*      */         }
/* 2492 */         String[] result = FileFinder.find(dir, ".JAVA");
/* 2493 */         if ("none".equals(customDestinationPath)) {
/* 2494 */           customDestinationPath = "none";
/*      */         }
/* 2496 */         if (this.filenames != null)
/*      */         {
/* 2498 */           int length = result.length;
/* 2499 */           System.arraycopy(
/* 2500 */             this.filenames, 
/* 2501 */             0, 
/* 2502 */             this.filenames = new String[length + filesCount], 
/* 2503 */             0, 
/* 2504 */             filesCount);
/* 2505 */           System.arraycopy(
/* 2506 */             this.encodings, 
/* 2507 */             0, 
/* 2508 */             this.encodings = new String[length + filesCount], 
/* 2509 */             0, 
/* 2510 */             filesCount);
/* 2511 */           System.arraycopy(
/* 2512 */             this.destinationPaths, 
/* 2513 */             0, 
/* 2514 */             this.destinationPaths = new String[length + filesCount], 
/* 2515 */             0, 
/* 2516 */             filesCount);
/* 2517 */           System.arraycopy(result, 0, this.filenames, filesCount, length);
/* 2518 */           for (int i = 0; i < length; i++) {
/* 2519 */             this.encodings[(filesCount + i)] = customEncoding;
/* 2520 */             this.destinationPaths[(filesCount + i)] = customDestinationPath;
/*      */           }
/* 2522 */           filesCount += length;
/* 2523 */           customEncoding = null;
/* 2524 */           customDestinationPath = null;
/* 2525 */           currentSourceDirectory = null;
/*      */         } else {
/* 2527 */           this.filenames = result;
/* 2528 */           filesCount = this.filenames.length;
/* 2529 */           this.encodings = new String[filesCount];
/* 2530 */           this.destinationPaths = new String[filesCount];
/* 2531 */           for (int i = 0; i < filesCount; i++) {
/* 2532 */             this.encodings[i] = customEncoding;
/* 2533 */             this.destinationPaths[i] = customDestinationPath;
/*      */           }
/* 2535 */           customEncoding = null;
/* 2536 */           customDestinationPath = null;
/* 2537 */           currentSourceDirectory = null;
/*      */         }
/* 2539 */         mode = 0;
/*      */       }
/* 1795 */       index++; } while (index < argCount);
/*      */ 
/* 2545 */     if (this.enableJavadocOn) {
/* 2546 */       this.options.put(
/* 2547 */         "org.eclipse.jdt.core.compiler.doc.comment.support", 
/* 2548 */         "enabled");
/* 2549 */     } else if ((this.warnJavadocOn) || (this.warnAllJavadocOn)) {
/* 2550 */       this.options.put(
/* 2551 */         "org.eclipse.jdt.core.compiler.doc.comment.support", 
/* 2552 */         "enabled");
/*      */ 
/* 2555 */       this.options.put(
/* 2556 */         "org.eclipse.jdt.core.compiler.problem.unusedParameterIncludeDocCommentReference", 
/* 2557 */         "disabled");
/* 2558 */       this.options.put(
/* 2559 */         "org.eclipse.jdt.core.compiler.problem.unusedDeclaredThrownExceptionIncludeDocCommentReference", 
/* 2560 */         "disabled");
/*      */     }
/*      */ 
/* 2563 */     if (this.warnJavadocOn) {
/* 2564 */       this.options.put(
/* 2565 */         "org.eclipse.jdt.core.compiler.problem.invalidJavadoc", 
/* 2566 */         "warning");
/* 2567 */       this.options.put(
/* 2568 */         "org.eclipse.jdt.core.compiler.problem.invalidJavadocTags", 
/* 2569 */         "enabled");
/* 2570 */       this.options.put(
/* 2571 */         "org.eclipse.jdt.core.compiler.problem.invalidJavadocTagsDeprecatedRef", 
/* 2572 */         "enabled");
/* 2573 */       this.options.put(
/* 2574 */         "org.eclipse.jdt.core.compiler.problem.invalidJavadocTagsNotVisibleRef", 
/* 2575 */         "enabled");
/* 2576 */       this.options.put(
/* 2577 */         "org.eclipse.jdt.core.compiler.problem.missingJavadocTags", 
/* 2578 */         "warning");
/* 2579 */       this.options.put(
/* 2580 */         "org.eclipse.jdt.core.compiler.problem.missingJavadocTagsVisibility", 
/* 2581 */         "private");
/*      */     }
/* 2583 */     if (this.warnAllJavadocOn) {
/* 2584 */       this.options.put(
/* 2585 */         "org.eclipse.jdt.core.compiler.problem.missingJavadocComments", 
/* 2586 */         "warning");
/*      */     }
/*      */ 
/* 2589 */     if ((printUsageRequired) || ((filesCount == 0) && (classCount == 0))) {
/* 2590 */       if (usageSection == null)
/* 2591 */         printUsage();
/*      */       else {
/* 2593 */         printUsage(usageSection);
/*      */       }
/* 2595 */       this.proceed = false;
/* 2596 */       return;
/*      */     }
/*      */ 
/* 2599 */     if (this.log != null)
/* 2600 */       this.logger.setLog(this.log);
/*      */     else {
/* 2602 */       this.showProgress = false;
/*      */     }
/* 2604 */     this.logger.logVersion(printVersionRequired);
/*      */ 
/* 2606 */     validateOptions(didSpecifyCompliance);
/*      */ 
/* 2610 */     if ((!didSpecifyDisabledAnnotationProcessing) && 
/* 2611 */       (CompilerOptions.versionToJdkLevel(this.options.get("org.eclipse.jdt.core.compiler.compliance")) >= 3276800L)) {
/* 2612 */       this.options.put("org.eclipse.jdt.core.compiler.processAnnotations", "enabled");
/*      */     }
/*      */ 
/* 2615 */     this.logger.logCommandLineArguments(newCommandLineArgs);
/* 2616 */     this.logger.logOptions(this.options);
/*      */ 
/* 2618 */     if (this.maxRepetition == 0) {
/* 2619 */       this.maxRepetition = 1;
/*      */     }
/* 2621 */     if ((this.maxRepetition >= 3) && ((this.timing & 0x1) != 0)) {
/* 2622 */       this.compilerStats = new CompilerStats[this.maxRepetition];
/*      */     }
/*      */ 
/* 2625 */     if (filesCount != 0) {
/* 2626 */       System.arraycopy(
/* 2627 */         this.filenames, 
/* 2628 */         0, 
/* 2629 */         this.filenames = new String[filesCount], 
/* 2630 */         0, 
/* 2631 */         filesCount);
/*      */     }
/*      */ 
/* 2634 */     if (classCount != 0) {
/* 2635 */       System.arraycopy(
/* 2636 */         this.classNames, 
/* 2637 */         0, 
/* 2638 */         this.classNames = new String[classCount], 
/* 2639 */         0, 
/* 2640 */         classCount);
/*      */     }
/*      */ 
/* 2643 */     setPaths(bootclasspaths, 
/* 2644 */       sourcepathClasspathArg, 
/* 2645 */       sourcepathClasspaths, 
/* 2646 */       classpaths, 
/* 2647 */       extdirsClasspaths, 
/* 2648 */       endorsedDirClasspaths, 
/* 2649 */       customEncoding);
/*      */ 
/* 2651 */     if (this.pendingErrors != null) {
/* 2652 */       for (Iterator iterator = this.pendingErrors.iterator(); iterator.hasNext(); ) {
/* 2653 */         String message = (String)iterator.next();
/* 2654 */         this.logger.logPendingError(message);
/*      */       }
/* 2656 */       this.pendingErrors = null;
/*      */     }
/*      */   }
/*      */ 
/*      */   protected void disableWarnings() {
/* 2660 */     Object[] entries = this.options.entrySet().toArray();
/* 2661 */     int i = 0; for (int max = entries.length; i < max; i++) {
/* 2662 */       Map.Entry entry = (Map.Entry)entries[i];
/* 2663 */       if (!(entry.getKey() instanceof String))
/*      */         continue;
/* 2665 */       if (!(entry.getValue() instanceof String))
/*      */         continue;
/* 2667 */       if (((String)entry.getValue()).equals("warning")) {
/* 2668 */         this.options.put(entry.getKey(), "ignore");
/*      */       }
/*      */     }
/* 2671 */     this.options.put("org.eclipse.jdt.core.compiler.taskTags", Util.EMPTY_STRING);
/*      */   }
/*      */   public String extractDestinationPathFromSourceFile(CompilationResult result) {
/* 2674 */     ICompilationUnit compilationUnit = result.compilationUnit;
/* 2675 */     if (compilationUnit != null) {
/* 2676 */       char[] fileName = compilationUnit.getFileName();
/* 2677 */       int lastIndex = CharOperation.lastIndexOf(File.separatorChar, fileName);
/* 2678 */       if (lastIndex != -1) {
/* 2679 */         String outputPathName = new String(fileName, 0, lastIndex);
/* 2680 */         File output = new File(outputPathName);
/* 2681 */         if ((output.exists()) && (output.isDirectory())) {
/* 2682 */           return outputPathName;
/*      */         }
/*      */       }
/*      */     }
/* 2686 */     return System.getProperty("user.dir");
/*      */   }
/*      */ 
/*      */   public ICompilerRequestor getBatchRequestor()
/*      */   {
/* 2692 */     return new ICompilerRequestor() {
/* 2693 */       int lineDelta = 0;
/*      */ 
/* 2695 */       public void acceptResult(CompilationResult compilationResult) { if (compilationResult.lineSeparatorPositions != null) {
/* 2696 */           int unitLineCount = compilationResult.lineSeparatorPositions.length;
/* 2697 */           this.lineDelta += unitLineCount;
/* 2698 */           if ((Main.this.showProgress) && (this.lineDelta > 2000))
/*      */           {
/* 2700 */             Main.this.logger.logProgress();
/* 2701 */             this.lineDelta = 0;
/*      */           }
/*      */         }
/* 2704 */         Main.this.logger.startLoggingSource(compilationResult);
/* 2705 */         if ((compilationResult.hasProblems()) || (compilationResult.hasTasks())) {
/* 2706 */           Main.this.logger.logProblems(compilationResult.getAllProblems(), compilationResult.compilationUnit.getContents(), Main.this);
/*      */         }
/* 2708 */         Main.this.outputClassFiles(compilationResult);
/* 2709 */         Main.this.logger.endLoggingSource();
/*      */       }
/*      */     };
/*      */   }
/*      */ 
/*      */   public CompilationUnit[] getCompilationUnits()
/*      */   {
/* 2717 */     int fileCount = this.filenames.length;
/* 2718 */     CompilationUnit[] units = new CompilationUnit[fileCount];
/* 2719 */     HashtableOfObject knownFileNames = new HashtableOfObject(fileCount);
/*      */ 
/* 2721 */     String defaultEncoding = (String)this.options.get("org.eclipse.jdt.core.encoding");
/* 2722 */     if (Util.EMPTY_STRING.equals(defaultEncoding)) {
/* 2723 */       defaultEncoding = null;
/*      */     }
/* 2725 */     for (int i = 0; i < fileCount; i++) {
/* 2726 */       char[] charName = this.filenames[i].toCharArray();
/* 2727 */       if (knownFileNames.get(charName) != null)
/* 2728 */         throw new IllegalArgumentException(bind("unit.more", this.filenames[i]));
/* 2729 */       knownFileNames.put(charName, charName);
/* 2730 */       File file = new File(this.filenames[i]);
/* 2731 */       if (!file.exists())
/* 2732 */         throw new IllegalArgumentException(bind("unit.missing", this.filenames[i]));
/* 2733 */       String encoding = this.encodings[i];
/* 2734 */       if (encoding == null)
/* 2735 */         encoding = defaultEncoding;
/* 2736 */       units[i] = 
/* 2737 */         new CompilationUnit(null, this.filenames[i], encoding, 
/* 2737 */         this.destinationPaths[i]);
/*      */     }
/* 2739 */     return units;
/*      */   }
/*      */ 
/*      */   public IErrorHandlingPolicy getHandlingPolicy()
/*      */   {
/* 2748 */     return new IErrorHandlingPolicy() {
/*      */       public boolean proceedOnErrors() {
/* 2750 */         return Main.this.proceedOnError;
/*      */       }
/*      */       public boolean stopOnFirstError() {
/* 2753 */         return false;
/*      */       }
/*      */     };
/*      */   }
/*      */ 
/*      */   public File getJavaHome()
/*      */   {
/* 2762 */     if (!this.javaHomeChecked) {
/* 2763 */       this.javaHomeChecked = true;
/* 2764 */       String javaHome = System.getProperty("java.home");
/* 2765 */       if (javaHome != null) {
/* 2766 */         this.javaHomeCache = new File(javaHome);
/* 2767 */         if (!this.javaHomeCache.exists())
/* 2768 */           this.javaHomeCache = null;
/*      */       }
/*      */     }
/* 2771 */     return this.javaHomeCache;
/*      */   }
/*      */ 
/*      */   public FileSystem getLibraryAccess() {
/* 2775 */     return new FileSystem(this.checkedClasspaths, this.filenames);
/*      */   }
/*      */ 
/*      */   public IProblemFactory getProblemFactory()
/*      */   {
/* 2782 */     return new DefaultProblemFactory(this.compilerLocale);
/*      */   }
/*      */ 
/*      */   protected ArrayList handleBootclasspath(ArrayList bootclasspaths, String customEncoding)
/*      */   {
/* 2789 */     int bootclasspathsSize = bootclasspaths == null ? 0 : bootclasspaths.size();
/* 2790 */     if (bootclasspathsSize != 0) {
/* 2791 */       String[] paths = new String[bootclasspathsSize];
/* 2792 */       bootclasspaths.toArray(paths);
/* 2793 */       bootclasspaths.clear();
/* 2794 */       for (int i = 0; i < bootclasspathsSize; i++)
/* 2795 */         processPathEntries(4, bootclasspaths, 
/* 2796 */           paths[i], customEncoding, false, true);
/*      */     }
/*      */     else {
/* 2799 */       bootclasspaths = new ArrayList(4);
/*      */ 
/* 2804 */       String javaversion = System.getProperty("java.version");
/* 2805 */       if ((javaversion != null) && (javaversion.equalsIgnoreCase("1.1.8"))) {
/* 2806 */         this.logger.logWrongJDK();
/* 2807 */         this.proceed = false;
/* 2808 */         return null;
/*      */       }
/*      */ 
/* 2815 */       String bootclasspathProperty = System.getProperty("sun.boot.class.path");
/* 2816 */       if ((bootclasspathProperty == null) || (bootclasspathProperty.length() == 0))
/*      */       {
/* 2818 */         bootclasspathProperty = System.getProperty("vm.boot.class.path");
/* 2819 */         if ((bootclasspathProperty == null) || (bootclasspathProperty.length() == 0))
/*      */         {
/* 2821 */           bootclasspathProperty = System.getProperty("org.apache.harmony.boot.class.path");
/*      */         }
/*      */       }
/* 2824 */       if ((bootclasspathProperty != null) && (bootclasspathProperty.length() != 0)) {
/* 2825 */         StringTokenizer tokenizer = new StringTokenizer(bootclasspathProperty, File.pathSeparator);
/*      */ 
/* 2827 */         while (tokenizer.hasMoreTokens()) {
/* 2828 */           String token = tokenizer.nextToken();
/* 2829 */           FileSystem.Classpath currentClasspath = 
/* 2830 */             FileSystem.getClasspath(token, customEncoding, null);
/* 2831 */           if (currentClasspath != null)
/* 2832 */             bootclasspaths.add(currentClasspath);
/*      */         }
/*      */       }
/*      */       else
/*      */       {
/* 2837 */         File javaHome = getJavaHome();
/* 2838 */         if (javaHome != null) {
/* 2839 */           File[] directoriesToCheck = (File[])null;
/* 2840 */           if (System.getProperty("os.name").startsWith("Mac")) {
/* 2841 */             directoriesToCheck = new File[] { 
/* 2842 */               new File(javaHome, "../Classes") };
/*      */           }
/*      */           else
/*      */           {
/* 2846 */             directoriesToCheck = new File[] { 
/* 2847 */               new File(javaHome, "lib") };
/*      */           }
/*      */ 
/* 2850 */           File[][] systemLibrariesJars = getLibrariesFiles(directoriesToCheck);
/* 2851 */           if (systemLibrariesJars != null) {
/* 2852 */             int i = 0; for (int max = systemLibrariesJars.length; i < max; i++) {
/* 2853 */               File[] current = systemLibrariesJars[i];
/* 2854 */               if (current != null) {
/* 2855 */                 int j = 0; for (int max2 = current.length; j < max2; j++) {
/* 2856 */                   FileSystem.Classpath classpath = 
/* 2857 */                     FileSystem.getClasspath(current[j].getAbsolutePath(), 
/* 2858 */                     null, false, null, null);
/* 2859 */                   if (classpath != null) {
/* 2860 */                     bootclasspaths.add(classpath);
/*      */                   }
/*      */                 }
/*      */               }
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/* 2869 */     return bootclasspaths;
/*      */   }
/*      */ 
/*      */   protected ArrayList handleClasspath(ArrayList classpaths, String customEncoding)
/*      */   {
/* 2876 */     int classpathsSize = classpaths == null ? 0 : classpaths.size();
/* 2877 */     if (classpathsSize != 0) {
/* 2878 */       String[] paths = new String[classpathsSize];
/* 2879 */       classpaths.toArray(paths);
/* 2880 */       classpaths.clear();
/* 2881 */       for (int i = 0; i < classpathsSize; i++)
/* 2882 */         processPathEntries(4, classpaths, paths[i], 
/* 2883 */           customEncoding, false, true);
/*      */     }
/*      */     else
/*      */     {
/* 2887 */       classpaths = new ArrayList(4);
/* 2888 */       String classProp = System.getProperty("java.class.path");
/* 2889 */       if ((classProp == null) || (classProp.length() == 0)) {
/* 2890 */         addPendingErrors(bind("configure.noClasspath"));
/* 2891 */         FileSystem.Classpath classpath = FileSystem.getClasspath(System.getProperty("user.dir"), customEncoding, null);
/* 2892 */         if (classpath != null)
/* 2893 */           classpaths.add(classpath);
/*      */       }
/*      */       else {
/* 2896 */         StringTokenizer tokenizer = new StringTokenizer(classProp, File.pathSeparator);
/*      */ 
/* 2898 */         while (tokenizer.hasMoreTokens()) {
/* 2899 */           String token = tokenizer.nextToken();
/* 2900 */           FileSystem.Classpath currentClasspath = 
/* 2901 */             FileSystem.getClasspath(token, customEncoding, null);
/* 2902 */           if (currentClasspath != null)
/* 2903 */             classpaths.add(currentClasspath);
/* 2904 */           else if (token.length() != 0) {
/* 2905 */             addPendingErrors(bind("configure.incorrectClasspath", token));
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/* 2910 */     ArrayList result = new ArrayList();
/* 2911 */     HashMap knownNames = new HashMap();
/* 2912 */     FileSystem.ClasspathSectionProblemReporter problemReporter = 
/* 2913 */       new FileSystem.ClasspathSectionProblemReporter() {
/*      */       public void invalidClasspathSection(String jarFilePath) {
/* 2915 */         Main.this.addPendingErrors(Main.this.bind("configure.invalidClasspathSection", jarFilePath));
/*      */       }
/*      */       public void multipleClasspathSections(String jarFilePath) {
/* 2918 */         Main.this.addPendingErrors(Main.this.bind("configure.multipleClasspathSections", jarFilePath));
/*      */       }
/*      */     };
/* 2921 */     while (!classpaths.isEmpty()) {
/* 2922 */       FileSystem.Classpath current = (FileSystem.Classpath)classpaths.remove(0);
/* 2923 */       String currentPath = current.getPath();
/* 2924 */       if (knownNames.get(currentPath) == null) {
/* 2925 */         knownNames.put(currentPath, current);
/* 2926 */         result.add(current);
/* 2927 */         List linkedJars = current.fetchLinkedJars(problemReporter);
/* 2928 */         if (linkedJars != null) {
/* 2929 */           classpaths.addAll(0, linkedJars);
/*      */         }
/*      */       }
/*      */     }
/* 2933 */     return result;
/*      */   }
/*      */ 
/*      */   protected ArrayList handleEndorseddirs(ArrayList endorsedDirClasspaths)
/*      */   {
/* 2939 */     File javaHome = getJavaHome();
/*      */ 
/* 2946 */     if (endorsedDirClasspaths == null) {
/* 2947 */       endorsedDirClasspaths = new ArrayList(4);
/* 2948 */       String endorsedDirsStr = System.getProperty("java.endorsed.dirs");
/* 2949 */       if (endorsedDirsStr == null) {
/* 2950 */         if (javaHome != null)
/* 2951 */           endorsedDirClasspaths.add(javaHome.getAbsolutePath() + "/lib/endorsed");
/*      */       }
/*      */       else {
/* 2954 */         StringTokenizer tokenizer = new StringTokenizer(endorsedDirsStr, File.pathSeparator);
/* 2955 */         while (tokenizer.hasMoreTokens()) {
/* 2956 */           endorsedDirClasspaths.add(tokenizer.nextToken());
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 2965 */     if (endorsedDirClasspaths.size() != 0) {
/* 2966 */       File[] directoriesToCheck = new File[endorsedDirClasspaths.size()];
/* 2967 */       for (int i = 0; i < directoriesToCheck.length; i++)
/* 2968 */         directoriesToCheck[i] = new File((String)endorsedDirClasspaths.get(i));
/* 2969 */       endorsedDirClasspaths.clear();
/* 2970 */       File[][] endorsedDirsJars = getLibrariesFiles(directoriesToCheck);
/* 2971 */       if (endorsedDirsJars != null) {
/* 2972 */         int i = 0; for (int max = endorsedDirsJars.length; i < max; i++) {
/* 2973 */           File[] current = endorsedDirsJars[i];
/* 2974 */           if (current != null) {
/* 2975 */             int j = 0; for (int max2 = current.length; j < max2; j++) {
/* 2976 */               FileSystem.Classpath classpath = 
/* 2977 */                 FileSystem.getClasspath(
/* 2978 */                 current[j].getAbsolutePath(), 
/* 2979 */                 null, null);
/* 2980 */               if (classpath != null)
/* 2981 */                 endorsedDirClasspaths.add(classpath);
/*      */             }
/*      */           }
/* 2984 */           else if (directoriesToCheck[i].isFile()) {
/* 2985 */             addPendingErrors(
/* 2986 */               bind(
/* 2987 */               "configure.incorrectEndorsedDirsEntry", 
/* 2988 */               directoriesToCheck[i].getAbsolutePath()));
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/* 2993 */     return endorsedDirClasspaths;
/*      */   }
/*      */ 
/*      */   protected ArrayList handleExtdirs(ArrayList extdirsClasspaths)
/*      */   {
/* 3001 */     File javaHome = getJavaHome();
/*      */ 
/* 3009 */     if (extdirsClasspaths == null) {
/* 3010 */       extdirsClasspaths = new ArrayList(4);
/* 3011 */       String extdirsStr = System.getProperty("java.ext.dirs");
/* 3012 */       if (extdirsStr == null) {
/* 3013 */         extdirsClasspaths.add(javaHome.getAbsolutePath() + "/lib/ext");
/*      */       } else {
/* 3015 */         StringTokenizer tokenizer = new StringTokenizer(extdirsStr, File.pathSeparator);
/* 3016 */         while (tokenizer.hasMoreTokens()) {
/* 3017 */           extdirsClasspaths.add(tokenizer.nextToken());
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 3025 */     if (extdirsClasspaths.size() != 0) {
/* 3026 */       File[] directoriesToCheck = new File[extdirsClasspaths.size()];
/* 3027 */       for (int i = 0; i < directoriesToCheck.length; i++)
/* 3028 */         directoriesToCheck[i] = new File((String)extdirsClasspaths.get(i));
/* 3029 */       extdirsClasspaths.clear();
/* 3030 */       File[][] extdirsJars = getLibrariesFiles(directoriesToCheck);
/* 3031 */       if (extdirsJars != null) {
/* 3032 */         int i = 0; for (int max = extdirsJars.length; i < max; i++) {
/* 3033 */           File[] current = extdirsJars[i];
/* 3034 */           if (current != null) {
/* 3035 */             int j = 0; for (int max2 = current.length; j < max2; j++) {
/* 3036 */               FileSystem.Classpath classpath = 
/* 3037 */                 FileSystem.getClasspath(
/* 3038 */                 current[j].getAbsolutePath(), 
/* 3039 */                 null, null);
/* 3040 */               if (classpath != null)
/* 3041 */                 extdirsClasspaths.add(classpath);
/*      */             }
/*      */           }
/* 3044 */           else if (directoriesToCheck[i].isFile()) {
/* 3045 */             addPendingErrors(bind(
/* 3046 */               "configure.incorrectExtDirsEntry", 
/* 3047 */               directoriesToCheck[i].getAbsolutePath()));
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */ 
/* 3053 */     return extdirsClasspaths;
/*      */   }
/*      */ 
/*      */   protected void handleWarningToken(String token, boolean isEnabling)
/*      */   {
/* 3061 */     if (token.length() == 0) return;
/* 3062 */     switch (token.charAt(0)) {
/*      */     case 'a':
/* 3064 */       if (token.equals("allDeprecation")) {
/* 3065 */         this.options.put(
/* 3066 */           "org.eclipse.jdt.core.compiler.problem.deprecation", 
/* 3067 */           isEnabling ? "warning" : "ignore");
/* 3068 */         this.options.put(
/* 3069 */           "org.eclipse.jdt.core.compiler.problem.deprecationInDeprecatedCode", 
/* 3070 */           isEnabling ? "enabled" : "disabled");
/* 3071 */         this.options.put(
/* 3072 */           "org.eclipse.jdt.core.compiler.problem.deprecationWhenOverridingDeprecatedMethod", 
/* 3073 */           isEnabling ? "enabled" : "disabled");
/* 3074 */         return;
/* 3075 */       }if (token.equals("allJavadoc")) {
/* 3076 */         this.warnAllJavadocOn = (this.warnJavadocOn = isEnabling);
/* 3077 */         return;
/* 3078 */       }if (token.equals("assertIdentifier")) {
/* 3079 */         this.options.put(
/* 3080 */           "org.eclipse.jdt.core.compiler.problem.assertIdentifier", 
/* 3081 */           isEnabling ? "warning" : "ignore");
/* 3082 */         return;
/* 3083 */       }if (!token.equals("allDeadCode")) break;
/* 3084 */       this.options.put(
/* 3085 */         "org.eclipse.jdt.core.compiler.problem.deadCode", 
/* 3086 */         isEnabling ? "warning" : "ignore");
/* 3087 */       this.options.put(
/* 3088 */         "org.eclipse.jdt.core.compiler.problem.deadCodeInTrivialIfStatement", 
/* 3089 */         isEnabling ? "enabled" : "disabled");
/* 3090 */       return;
/*      */     case 'b':
/* 3094 */       if (!token.equals("boxing")) break;
/* 3095 */       this.options.put(
/* 3096 */         "org.eclipse.jdt.core.compiler.problem.autoboxing", 
/* 3097 */         isEnabling ? "warning" : "ignore");
/* 3098 */       return;
/*      */     case 'c':
/* 3102 */       if (token.equals("constructorName")) {
/* 3103 */         this.options.put(
/* 3104 */           "org.eclipse.jdt.core.compiler.problem.methodWithConstructorName", 
/* 3105 */           isEnabling ? "warning" : "ignore");
/* 3106 */         return;
/* 3107 */       }if (token.equals("conditionAssign")) {
/* 3108 */         this.options.put(
/* 3109 */           "org.eclipse.jdt.core.compiler.problem.possibleAccidentalBooleanAssignment", 
/* 3110 */           isEnabling ? "warning" : "ignore");
/* 3111 */         return;
/* 3112 */       }if (token.equals("compareIdentical")) {
/* 3113 */         this.options.put(
/* 3114 */           "org.eclipse.jdt.core.compiler.problem.comparingIdentical", 
/* 3115 */           isEnabling ? "warning" : "ignore");
/* 3116 */         return;
/* 3117 */       }if (!token.equals("charConcat")) break;
/* 3118 */       this.options.put(
/* 3119 */         "org.eclipse.jdt.core.compiler.problem.noImplicitStringConversion", 
/* 3120 */         isEnabling ? "warning" : "ignore");
/* 3121 */       return;
/*      */     case 'd':
/* 3125 */       if (token.equals("deprecation")) {
/* 3126 */         this.options.put(
/* 3127 */           "org.eclipse.jdt.core.compiler.problem.deprecation", 
/* 3128 */           isEnabling ? "warning" : "ignore");
/* 3129 */         this.options.put(
/* 3130 */           "org.eclipse.jdt.core.compiler.problem.deprecationInDeprecatedCode", 
/* 3131 */           "disabled");
/* 3132 */         this.options.put(
/* 3133 */           "org.eclipse.jdt.core.compiler.problem.deprecationWhenOverridingDeprecatedMethod", 
/* 3134 */           "disabled");
/* 3135 */         return;
/* 3136 */       }if (token.equals("dep-ann")) {
/* 3137 */         this.options.put(
/* 3138 */           "org.eclipse.jdt.core.compiler.problem.missingDeprecatedAnnotation", 
/* 3139 */           isEnabling ? "warning" : "ignore");
/* 3140 */         return;
/* 3141 */       }if (token.equals("discouraged")) {
/* 3142 */         this.options.put(
/* 3143 */           "org.eclipse.jdt.core.compiler.problem.discouragedReference", 
/* 3144 */           isEnabling ? "warning" : "ignore");
/* 3145 */         return;
/* 3146 */       }if (!token.equals("deadCode")) break;
/* 3147 */       this.options.put(
/* 3148 */         "org.eclipse.jdt.core.compiler.problem.deadCode", 
/* 3149 */         isEnabling ? "warning" : "ignore");
/* 3150 */       this.options.put(
/* 3151 */         "org.eclipse.jdt.core.compiler.problem.deadCodeInTrivialIfStatement", 
/* 3152 */         "disabled");
/* 3153 */       return;
/*      */     case 'e':
/* 3157 */       if ((token.equals("enumSwitch")) || 
/* 3158 */         (token.equals("incomplete-switch"))) {
/* 3159 */         this.options.put(
/* 3160 */           "org.eclipse.jdt.core.compiler.problem.incompleteEnumSwitch", 
/* 3161 */           isEnabling ? "warning" : "ignore");
/* 3162 */         return;
/* 3163 */       }if (token.equals("emptyBlock")) {
/* 3164 */         this.options.put(
/* 3165 */           "org.eclipse.jdt.core.compiler.problem.undocumentedEmptyBlock", 
/* 3166 */           isEnabling ? "warning" : "ignore");
/* 3167 */         return;
/* 3168 */       }if (!token.equals("enumIdentifier")) break;
/* 3169 */       this.options.put(
/* 3170 */         "org.eclipse.jdt.core.compiler.problem.enumIdentifier", 
/* 3171 */         isEnabling ? "warning" : "ignore");
/* 3172 */       return;
/*      */     case 'f':
/* 3176 */       if (token.equals("fieldHiding")) {
/* 3177 */         this.options.put(
/* 3178 */           "org.eclipse.jdt.core.compiler.problem.fieldHiding", 
/* 3179 */           isEnabling ? "warning" : "ignore");
/* 3180 */         return;
/* 3181 */       }if (token.equals("finalBound")) {
/* 3182 */         this.options.put(
/* 3183 */           "org.eclipse.jdt.core.compiler.problem.finalParameterBound", 
/* 3184 */           isEnabling ? "warning" : "ignore");
/* 3185 */         return;
/* 3186 */       }if (token.equals("finally")) {
/* 3187 */         this.options.put(
/* 3188 */           "org.eclipse.jdt.core.compiler.problem.finallyBlockNotCompletingNormally", 
/* 3189 */           isEnabling ? "warning" : "ignore");
/* 3190 */         return;
/* 3191 */       }if (token.equals("forbidden")) {
/* 3192 */         this.options.put(
/* 3193 */           "org.eclipse.jdt.core.compiler.problem.forbiddenReference", 
/* 3194 */           isEnabling ? "warning" : "ignore");
/* 3195 */         return;
/* 3196 */       }if (!token.equals("fallthrough")) break;
/* 3197 */       this.options.put(
/* 3198 */         "org.eclipse.jdt.core.compiler.problem.fallthroughCase", 
/* 3199 */         isEnabling ? "warning" : "ignore");
/* 3200 */       return;
/*      */     case 'h':
/* 3204 */       if (token.equals("hiding")) {
/* 3205 */         this.options.put(
/* 3206 */           "org.eclipse.jdt.core.compiler.problem.hiddenCatchBlock", 
/* 3207 */           isEnabling ? "warning" : "ignore");
/* 3208 */         this.options.put(
/* 3209 */           "org.eclipse.jdt.core.compiler.problem.localVariableHiding", 
/* 3210 */           isEnabling ? "warning" : "ignore");
/* 3211 */         this.options.put(
/* 3212 */           "org.eclipse.jdt.core.compiler.problem.fieldHiding", 
/* 3213 */           isEnabling ? "warning" : "ignore");
/* 3214 */         this.options.put(
/* 3215 */           "org.eclipse.jdt.core.compiler.problem.typeParameterHiding", 
/* 3216 */           isEnabling ? "warning" : "ignore");
/* 3217 */         return;
/* 3218 */       }if (!token.equals("hashCode")) break;
/* 3219 */       this.options.put(
/* 3220 */         "org.eclipse.jdt.core.compiler.problem.missingHashCodeMethod", 
/* 3221 */         isEnabling ? "warning" : "ignore");
/* 3222 */       return;
/*      */     case 'i':
/* 3226 */       if (token.equals("indirectStatic")) {
/* 3227 */         this.options.put(
/* 3228 */           "org.eclipse.jdt.core.compiler.problem.indirectStaticAccess", 
/* 3229 */           isEnabling ? "warning" : "ignore");
/* 3230 */         return;
/* 3231 */       }if ((token.equals("intfNonInherited")) || (token.equals("interfaceNonInherited"))) {
/* 3232 */         this.options.put(
/* 3233 */           "org.eclipse.jdt.core.compiler.problem.incompatibleNonInheritedInterfaceMethod", 
/* 3234 */           isEnabling ? "warning" : "ignore");
/* 3235 */         return;
/* 3236 */       }if (token.equals("intfAnnotation")) {
/* 3237 */         this.options.put(
/* 3238 */           "org.eclipse.jdt.core.compiler.problem.annotationSuperInterface", 
/* 3239 */           isEnabling ? "warning" : "ignore");
/* 3240 */         return;
/* 3241 */       }if (!token.equals("intfRedundant")) break;
/* 3242 */       this.options.put(
/* 3243 */         "org.eclipse.jdt.core.compiler.problem.redundantSuperinterface", 
/* 3244 */         isEnabling ? "warning" : "ignore");
/* 3245 */       return;
/*      */     case 'j':
/* 3249 */       if (!token.equals("javadoc")) break;
/* 3250 */       this.warnJavadocOn = isEnabling;
/* 3251 */       return;
/*      */     case 'l':
/* 3255 */       if (!token.equals("localHiding")) break;
/* 3256 */       this.options.put(
/* 3257 */         "org.eclipse.jdt.core.compiler.problem.localVariableHiding", 
/* 3258 */         isEnabling ? "warning" : "ignore");
/* 3259 */       return;
/*      */     case 'm':
/* 3263 */       if ((!token.equals("maskedCatchBlock")) && (!token.equals("maskedCatchBlocks"))) break;
/* 3264 */       this.options.put(
/* 3265 */         "org.eclipse.jdt.core.compiler.problem.hiddenCatchBlock", 
/* 3266 */         isEnabling ? "warning" : "ignore");
/* 3267 */       return;
/*      */     case 'n':
/* 3271 */       if (token.equals("nls")) {
/* 3272 */         this.options.put(
/* 3273 */           "org.eclipse.jdt.core.compiler.problem.nonExternalizedStringLiteral", 
/* 3274 */           isEnabling ? "warning" : "ignore");
/* 3275 */         return;
/* 3276 */       }if (token.equals("noEffectAssign")) {
/* 3277 */         this.options.put(
/* 3278 */           "org.eclipse.jdt.core.compiler.problem.noEffectAssignment", 
/* 3279 */           isEnabling ? "warning" : "ignore");
/* 3280 */         return;
/* 3281 */       }if (token.equals("noImplicitStringConversion")) {
/* 3282 */         this.options.put(
/* 3283 */           "org.eclipse.jdt.core.compiler.problem.noImplicitStringConversion", 
/* 3284 */           isEnabling ? "warning" : "ignore");
/* 3285 */         return;
/* 3286 */       }if (token.equals("null")) {
/* 3287 */         this.options.put(
/* 3288 */           "org.eclipse.jdt.core.compiler.problem.nullReference", 
/* 3289 */           isEnabling ? "warning" : "ignore");
/* 3290 */         this.options.put(
/* 3291 */           "org.eclipse.jdt.core.compiler.problem.potentialNullReference", 
/* 3292 */           isEnabling ? "warning" : "ignore");
/* 3293 */         this.options.put(
/* 3294 */           "org.eclipse.jdt.core.compiler.problem.redundantNullCheck", 
/* 3295 */           isEnabling ? "warning" : "ignore");
/* 3296 */         return;
/* 3297 */       }if (!token.equals("nullDereference")) break;
/* 3298 */       this.options.put(
/* 3299 */         "org.eclipse.jdt.core.compiler.problem.nullReference", 
/* 3300 */         isEnabling ? "warning" : "ignore");
/* 3301 */       if (!isEnabling) {
/* 3302 */         this.options.put(
/* 3303 */           "org.eclipse.jdt.core.compiler.problem.potentialNullReference", 
/* 3304 */           "ignore");
/* 3305 */         this.options.put(
/* 3306 */           "org.eclipse.jdt.core.compiler.problem.redundantNullCheck", 
/* 3307 */           "ignore");
/*      */       }
/* 3309 */       return;
/*      */     case 'o':
/* 3313 */       if (token.equals("over-sync")) {
/* 3314 */         this.options.put(
/* 3315 */           "org.eclipse.jdt.core.compiler.problem.missingSynchronizedOnInheritedMethod", 
/* 3316 */           isEnabling ? "error" : "ignore");
/* 3317 */         return;
/* 3318 */       }if (!token.equals("over-ann")) break;
/* 3319 */       this.options.put(
/* 3320 */         "org.eclipse.jdt.core.compiler.problem.missingOverrideAnnotation", 
/* 3321 */         isEnabling ? "warning" : "ignore");
/* 3322 */       return;
/*      */     case 'p':
/* 3326 */       if ((token.equals("pkgDefaultMethod")) || (token.equals("packageDefaultMethod"))) {
/* 3327 */         this.options.put(
/* 3328 */           "org.eclipse.jdt.core.compiler.problem.overridingPackageDefaultMethod", 
/* 3329 */           isEnabling ? "warning" : "ignore");
/* 3330 */         return;
/* 3331 */       }if (!token.equals("paramAssign")) break;
/* 3332 */       this.options.put(
/* 3333 */         "org.eclipse.jdt.core.compiler.problem.parameterAssignment", 
/* 3334 */         isEnabling ? "warning" : "ignore");
/* 3335 */       return;
/*      */     case 'r':
/* 3339 */       if (token.equals("raw")) {
/* 3340 */         this.options.put(
/* 3341 */           "org.eclipse.jdt.core.compiler.problem.rawTypeReference", 
/* 3342 */           isEnabling ? "warning" : "ignore");
/* 3343 */         return;
/* 3344 */       }if (!token.equals("redundantSuperinterface")) break;
/* 3345 */       this.options.put(
/* 3346 */         "org.eclipse.jdt.core.compiler.problem.redundantSuperinterface", 
/* 3347 */         isEnabling ? "warning" : "ignore");
/* 3348 */       return;
/*      */     case 's':
/* 3352 */       if (token.equals("specialParamHiding")) {
/* 3353 */         this.options.put(
/* 3354 */           "org.eclipse.jdt.core.compiler.problem.specialParameterHidingField", 
/* 3355 */           isEnabling ? "enabled" : "disabled");
/* 3356 */         return;
/* 3357 */       }if ((token.equals("syntheticAccess")) || (token.equals("synthetic-access"))) {
/* 3358 */         this.options.put(
/* 3359 */           "org.eclipse.jdt.core.compiler.problem.syntheticAccessEmulation", 
/* 3360 */           isEnabling ? "warning" : "ignore");
/* 3361 */         return;
/* 3362 */       }if (token.equals("staticReceiver")) {
/* 3363 */         this.options.put(
/* 3364 */           "org.eclipse.jdt.core.compiler.problem.staticAccessReceiver", 
/* 3365 */           isEnabling ? "warning" : "ignore");
/* 3366 */         return;
/* 3367 */       }if (token.equals("syncOverride")) {
/* 3368 */         this.options.put(
/* 3369 */           "org.eclipse.jdt.core.compiler.problem.missingSynchronizedOnInheritedMethod", 
/* 3370 */           isEnabling ? "error" : "ignore");
/* 3371 */         return;
/* 3372 */       }if (token.equals("semicolon")) {
/* 3373 */         this.options.put(
/* 3374 */           "org.eclipse.jdt.core.compiler.problem.emptyStatement", 
/* 3375 */           isEnabling ? "warning" : "ignore");
/* 3376 */         return;
/* 3377 */       }if (token.equals("serial")) {
/* 3378 */         this.options.put(
/* 3379 */           "org.eclipse.jdt.core.compiler.problem.missingSerialVersion", 
/* 3380 */           isEnabling ? "warning" : "ignore");
/* 3381 */         return;
/* 3382 */       }if (token.equals("suppress")) {
/* 3383 */         this.options.put(
/* 3384 */           "org.eclipse.jdt.core.compiler.problem.suppressWarnings", 
/* 3385 */           isEnabling ? "enabled" : "disabled");
/* 3386 */         return;
/* 3387 */       }if (token.equals("static-access")) {
/* 3388 */         this.options.put(
/* 3389 */           "org.eclipse.jdt.core.compiler.problem.staticAccessReceiver", 
/* 3390 */           isEnabling ? "warning" : "ignore");
/* 3391 */         this.options.put(
/* 3392 */           "org.eclipse.jdt.core.compiler.problem.indirectStaticAccess", 
/* 3393 */           isEnabling ? "warning" : "ignore");
/* 3394 */         return;
/* 3395 */       }if (!token.equals("super")) break;
/* 3396 */       this.options.put(
/* 3397 */         "org.eclipse.jdt.core.compiler.problem.overridingMethodWithoutSuperInvocation", 
/* 3398 */         isEnabling ? "warning" : "ignore");
/* 3399 */       return;
/*      */     case 't':
/* 3403 */       if (token.startsWith("tasks")) {
/* 3404 */         String taskTags = Util.EMPTY_STRING;
/* 3405 */         int start = token.indexOf('(');
/* 3406 */         int end = token.indexOf(')');
/* 3407 */         if ((start >= 0) && (end >= 0) && (start < end)) {
/* 3408 */           taskTags = token.substring(start + 1, end).trim();
/* 3409 */           taskTags = taskTags.replace('|', ',');
/*      */         }
/* 3411 */         if (taskTags.length() == 0) {
/* 3412 */           throw new IllegalArgumentException(bind("configure.invalidTaskTag", token));
/*      */         }
/* 3414 */         this.options.put(
/* 3415 */           "org.eclipse.jdt.core.compiler.taskTags", 
/* 3416 */           isEnabling ? taskTags : Util.EMPTY_STRING);
/* 3417 */         return;
/* 3418 */       }if (!token.equals("typeHiding")) break;
/* 3419 */       this.options.put(
/* 3420 */         "org.eclipse.jdt.core.compiler.problem.typeParameterHiding", 
/* 3421 */         isEnabling ? "warning" : "ignore");
/* 3422 */       return;
/*      */     case 'u':
/* 3426 */       if ((token.equals("unusedLocal")) || (token.equals("unusedLocals"))) {
/* 3427 */         this.options.put(
/* 3428 */           "org.eclipse.jdt.core.compiler.problem.unusedLocal", 
/* 3429 */           isEnabling ? "warning" : "ignore");
/* 3430 */         return;
/* 3431 */       }if ((token.equals("unusedArgument")) || (token.equals("unusedArguments"))) {
/* 3432 */         this.options.put(
/* 3433 */           "org.eclipse.jdt.core.compiler.problem.unusedParameter", 
/* 3434 */           isEnabling ? "warning" : "ignore");
/* 3435 */         return;
/* 3436 */       }if ((token.equals("unusedImport")) || (token.equals("unusedImports"))) {
/* 3437 */         this.options.put(
/* 3438 */           "org.eclipse.jdt.core.compiler.problem.unusedImport", 
/* 3439 */           isEnabling ? "warning" : "ignore");
/* 3440 */         return;
/* 3441 */       }if (token.equals("unusedPrivate")) {
/* 3442 */         this.options.put(
/* 3443 */           "org.eclipse.jdt.core.compiler.problem.unusedPrivateMember", 
/* 3444 */           isEnabling ? "warning" : "ignore");
/* 3445 */         return;
/* 3446 */       }if (token.equals("unusedLabel")) {
/* 3447 */         this.options.put(
/* 3448 */           "org.eclipse.jdt.core.compiler.problem.unusedLabel", 
/* 3449 */           isEnabling ? "warning" : "ignore");
/* 3450 */         return;
/* 3451 */       }if (token.equals("uselessTypeCheck")) {
/* 3452 */         this.options.put(
/* 3453 */           "org.eclipse.jdt.core.compiler.problem.unnecessaryTypeCheck", 
/* 3454 */           isEnabling ? "warning" : "ignore");
/* 3455 */         return;
/* 3456 */       }if ((token.equals("unchecked")) || (token.equals("unsafe"))) {
/* 3457 */         this.options.put(
/* 3458 */           "org.eclipse.jdt.core.compiler.problem.uncheckedTypeOperation", 
/* 3459 */           isEnabling ? "warning" : "ignore");
/* 3460 */         return;
/* 3461 */       }if (token.equals("unnecessaryElse")) {
/* 3462 */         this.options.put(
/* 3463 */           "org.eclipse.jdt.core.compiler.problem.unnecessaryElse", 
/* 3464 */           isEnabling ? "warning" : "ignore");
/* 3465 */         return;
/* 3466 */       }if (token.equals("unusedThrown")) {
/* 3467 */         this.options.put(
/* 3468 */           "org.eclipse.jdt.core.compiler.problem.unusedDeclaredThrownException", 
/* 3469 */           isEnabling ? "warning" : "ignore");
/* 3470 */         return;
/* 3471 */       }if ((token.equals("unqualifiedField")) || (token.equals("unqualified-field-access"))) {
/* 3472 */         this.options.put(
/* 3473 */           "org.eclipse.jdt.core.compiler.problem.unqualifiedFieldAccess", 
/* 3474 */           isEnabling ? "warning" : "ignore");
/* 3475 */         return;
/* 3476 */       }if (token.equals("unused")) {
/* 3477 */         this.options.put(
/* 3478 */           "org.eclipse.jdt.core.compiler.problem.unusedLocal", 
/* 3479 */           isEnabling ? "warning" : "ignore");
/* 3480 */         this.options.put(
/* 3481 */           "org.eclipse.jdt.core.compiler.problem.unusedParameter", 
/* 3482 */           isEnabling ? "warning" : "ignore");
/* 3483 */         this.options.put(
/* 3484 */           "org.eclipse.jdt.core.compiler.problem.unusedImport", 
/* 3485 */           isEnabling ? "warning" : "ignore");
/* 3486 */         this.options.put(
/* 3487 */           "org.eclipse.jdt.core.compiler.problem.unusedPrivateMember", 
/* 3488 */           isEnabling ? "warning" : "ignore");
/* 3489 */         this.options.put(
/* 3490 */           "org.eclipse.jdt.core.compiler.problem.unusedDeclaredThrownException", 
/* 3491 */           isEnabling ? "warning" : "ignore");
/* 3492 */         this.options.put(
/* 3493 */           "org.eclipse.jdt.core.compiler.problem.unusedLabel", 
/* 3494 */           isEnabling ? "warning" : "ignore");
/* 3495 */         this.options.put(
/* 3496 */           "org.eclipse.jdt.core.compiler.problem.unusedTypeArgumentsForMethodInvocation", 
/* 3497 */           isEnabling ? "warning" : "ignore");
/* 3498 */         return;
/* 3499 */       }if (!token.equals("unusedTypeArgs")) break;
/* 3500 */       this.options.put(
/* 3501 */         "org.eclipse.jdt.core.compiler.problem.unusedTypeArgumentsForMethodInvocation", 
/* 3502 */         isEnabling ? "warning" : "ignore");
/* 3503 */       return;
/*      */     case 'v':
/* 3507 */       if (!token.equals("varargsCast")) break;
/* 3508 */       this.options.put(
/* 3509 */         "org.eclipse.jdt.core.compiler.problem.varargsArgumentNeedCast", 
/* 3510 */         isEnabling ? "warning" : "ignore");
/* 3511 */       return;
/*      */     case 'w':
/* 3515 */       if (!token.equals("warningToken")) break;
/* 3516 */       this.options.put(
/* 3517 */         "org.eclipse.jdt.core.compiler.problem.unhandledWarningToken", 
/* 3518 */         isEnabling ? "warning" : "ignore");
/* 3519 */       this.options.put(
/* 3520 */         "org.eclipse.jdt.core.compiler.problem.unusedWarningToken", 
/* 3521 */         isEnabling ? "warning" : "ignore");
/* 3522 */       return;
/*      */     case 'g':
/*      */     case 'k':
/*      */     case 'q':
/* 3526 */     }addPendingErrors(bind("configure.invalidWarning", token));
/*      */   }
/*      */ 
/*      */   /** @deprecated */
/*      */   protected void initialize(PrintWriter outWriter, PrintWriter errWriter, boolean systemExit)
/*      */   {
/* 3533 */     initialize(outWriter, errWriter, systemExit, null, null);
/*      */   }
/*      */ 
/*      */   /** @deprecated */
/*      */   protected void initialize(PrintWriter outWriter, PrintWriter errWriter, boolean systemExit, Map customDefaultOptions)
/*      */   {
/* 3540 */     initialize(outWriter, errWriter, systemExit, customDefaultOptions, null);
/*      */   }
/*      */   protected void initialize(PrintWriter outWriter, PrintWriter errWriter, boolean systemExit, Map customDefaultOptions, CompilationProgress compilationProgress) {
/* 3543 */     this.logger = new Logger(this, outWriter, errWriter);
/* 3544 */     this.proceed = true;
/* 3545 */     this.out = outWriter;
/* 3546 */     this.err = errWriter;
/* 3547 */     this.systemExitWhenFinished = systemExit;
/* 3548 */     this.options = new CompilerOptions().getMap();
/* 3549 */     this.progress = compilationProgress;
/* 3550 */     if (customDefaultOptions != null) {
/* 3551 */       this.didSpecifySource = (customDefaultOptions.get("org.eclipse.jdt.core.compiler.source") != null);
/* 3552 */       this.didSpecifyTarget = (customDefaultOptions.get("org.eclipse.jdt.core.compiler.codegen.targetPlatform") != null);
/* 3553 */       for (Iterator iter = customDefaultOptions.entrySet().iterator(); iter.hasNext(); ) {
/* 3554 */         Map.Entry entry = (Map.Entry)iter.next();
/* 3555 */         this.options.put(entry.getKey(), entry.getValue());
/*      */       }
/*      */     } else {
/* 3558 */       this.didSpecifySource = false;
/* 3559 */       this.didSpecifyTarget = false;
/*      */     }
/* 3561 */     this.classNames = null;
/*      */   }
/*      */   protected void initializeAnnotationProcessorManager() {
/*      */     try {
/* 3565 */       Class c = Class.forName("org.eclipse.jdt.internal.compiler.apt.dispatch.BatchAnnotationProcessorManager");
/* 3566 */       AbstractAnnotationProcessorManager annotationManager = (AbstractAnnotationProcessorManager)c.newInstance();
/* 3567 */       annotationManager.configure(this, this.expandedCommandLine);
/* 3568 */       annotationManager.setErr(this.err);
/* 3569 */       annotationManager.setOut(this.out);
/* 3570 */       this.batchCompiler.annotationProcessorManager = annotationManager;
/*      */     }
/*      */     catch (ClassNotFoundException localClassNotFoundException) {
/*      */     }
/*      */     catch (InstantiationException localInstantiationException) {
/* 3575 */       throw new AbortCompilation();
/*      */     }
/*      */     catch (IllegalAccessException localIllegalAccessException) {
/* 3578 */       throw new AbortCompilation();
/*      */     }
/*      */     catch (UnsupportedClassVersionError localUnsupportedClassVersionError) {
/* 3581 */       this.logger.logIncorrectVMVersionForAnnotationProcessing();
/*      */     }
/*      */   }
/*      */ 
/*      */   public void outputClassFiles(CompilationResult unitResult)
/*      */   {
/* 3588 */     if ((unitResult != null) && ((!unitResult.hasErrors()) || (this.proceedOnError))) {
/* 3589 */       ClassFile[] classFiles = unitResult.getClassFiles();
/* 3590 */       String currentDestinationPath = null;
/* 3591 */       boolean generateClasspathStructure = false;
/* 3592 */       CompilationUnit compilationUnit = 
/* 3593 */         (CompilationUnit)unitResult.compilationUnit;
/* 3594 */       if (compilationUnit.destinationPath == null) {
/* 3595 */         if (this.destinationPath == null) {
/* 3596 */           currentDestinationPath = 
/* 3597 */             extractDestinationPathFromSourceFile(unitResult);
/* 3598 */         } else if (this.destinationPath != "none") {
/* 3599 */           currentDestinationPath = this.destinationPath;
/* 3600 */           generateClasspathStructure = true;
/*      */         }
/* 3602 */       } else if (compilationUnit.destinationPath != "none") {
/* 3603 */         currentDestinationPath = compilationUnit.destinationPath;
/* 3604 */         generateClasspathStructure = true;
/*      */       }
/* 3606 */       if (currentDestinationPath != null) {
/* 3607 */         int i = 0; for (int fileCount = classFiles.length; i < fileCount; i++)
/*      */         {
/* 3609 */           ClassFile classFile = classFiles[i];
/* 3610 */           char[] filename = classFile.fileName();
/* 3611 */           int length = filename.length;
/* 3612 */           char[] relativeName = new char[length + 6];
/* 3613 */           System.arraycopy(filename, 0, relativeName, 0, length);
/* 3614 */           System.arraycopy(SuffixConstants.SUFFIX_class, 0, relativeName, length, 6);
/* 3615 */           CharOperation.replace(relativeName, '/', File.separatorChar);
/* 3616 */           String relativeStringName = new String(relativeName);
/*      */           try {
/* 3618 */             if (this.compilerOptions.verbose) {
/* 3619 */               this.out.println(
/* 3620 */                 Messages.bind(
/* 3621 */                 Messages.compilation_write, 
/* 3622 */                 new String[] { 
/* 3623 */                 String.valueOf(this.exportedClassFilesCounter + 1), 
/* 3624 */                 relativeStringName }));
/*      */             }
/* 3626 */             Util.writeToDisk(
/* 3627 */               generateClasspathStructure, 
/* 3628 */               currentDestinationPath, 
/* 3629 */               relativeStringName, 
/* 3630 */               classFile);
/* 3631 */             this.logger.logClassFile(
/* 3632 */               generateClasspathStructure, 
/* 3633 */               currentDestinationPath, 
/* 3634 */               relativeStringName);
/* 3635 */             this.exportedClassFilesCounter += 1;
/*      */           } catch (IOException e) {
/* 3637 */             this.logger.logNoClassFileCreated(currentDestinationPath, relativeStringName, e);
/*      */           }
/*      */         }
/* 3640 */         this.batchCompiler.lookupEnvironment.releaseClassFiles(classFiles);
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   public void performCompilation()
/*      */   {
/* 3649 */     this.startTime = System.currentTimeMillis();
/*      */ 
/* 3651 */     FileSystem environment = getLibraryAccess();
/* 3652 */     this.compilerOptions = new CompilerOptions(this.options);
/* 3653 */     this.compilerOptions.performMethodsFullRecovery = false;
/* 3654 */     this.compilerOptions.performStatementsRecovery = false;
/* 3655 */     this.batchCompiler = 
/* 3656 */       new Compiler(
/* 3657 */       environment, 
/* 3658 */       getHandlingPolicy(), 
/* 3659 */       this.compilerOptions, 
/* 3660 */       getBatchRequestor(), 
/* 3661 */       getProblemFactory(), 
/* 3662 */       this.out, 
/* 3663 */       this.progress);
/* 3664 */     this.batchCompiler.remainingIterations = (this.maxRepetition - this.currentRepetition);
/*      */ 
/* 3666 */     String setting = System.getProperty("jdt.compiler.useSingleThread");
/* 3667 */     this.batchCompiler.useSingleThread = ((setting != null) && (setting.equals("true")));
/*      */ 
/* 3669 */     if ((this.compilerOptions.complianceLevel >= 3276800L) && 
/* 3670 */       (this.compilerOptions.processAnnotations)) {
/* 3671 */       if (checkVMVersion(3276800L)) {
/* 3672 */         initializeAnnotationProcessorManager();
/* 3673 */         if (this.classNames != null)
/* 3674 */           this.batchCompiler.setBinaryTypes(processClassNames(this.batchCompiler.lookupEnvironment));
/*      */       }
/*      */       else
/*      */       {
/* 3678 */         this.logger.logIncorrectVMVersionForAnnotationProcessing();
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 3683 */     this.compilerOptions.verbose = this.verbose;
/* 3684 */     this.compilerOptions.produceReferenceInfo = this.produceRefInfo;
/*      */     try {
/* 3686 */       this.logger.startLoggingSources();
/* 3687 */       this.batchCompiler.compile(getCompilationUnits());
/*      */     } finally {
/* 3689 */       this.logger.endLoggingSources();
/*      */     }
/*      */ 
/* 3692 */     if (this.extraProblems != null) {
/* 3693 */       this.logger.loggingExtraProblems(this);
/* 3694 */       this.extraProblems = null;
/*      */     }
/* 3696 */     if (this.compilerStats != null) {
/* 3697 */       this.compilerStats[this.currentRepetition] = this.batchCompiler.stats;
/*      */     }
/* 3699 */     this.logger.printStats();
/*      */ 
/* 3702 */     environment.cleanup();
/*      */   }
/*      */   public void printUsage() {
/* 3705 */     printUsage("misc.usage");
/*      */   }
/*      */   private void printUsage(String sectionID) {
/* 3708 */     this.logger.logUsage(
/* 3709 */       bind(
/* 3710 */       sectionID, 
/* 3711 */       new String[] { 
/* 3712 */       System.getProperty("path.separator"), 
/* 3713 */       bind("compiler.name"), 
/* 3714 */       bind("compiler.version"), 
/* 3715 */       bind("compiler.copyright") }));
/*      */ 
/* 3717 */     this.logger.flush();
/*      */   }
/*      */ 
/*      */   private ReferenceBinding[] processClassNames(LookupEnvironment environment) {
/* 3721 */     int length = this.classNames.length;
/* 3722 */     ReferenceBinding[] referenceBindings = new ReferenceBinding[length];
/* 3723 */     for (int i = 0; i < length; i++) {
/* 3724 */       String currentName = this.classNames[i];
/* 3725 */       char[][] compoundName = (char[][])null;
/* 3726 */       if (currentName.indexOf('.') != -1)
/*      */       {
/* 3728 */         char[] typeName = currentName.toCharArray();
/* 3729 */         compoundName = CharOperation.splitOn('.', typeName);
/*      */       } else {
/* 3731 */         compoundName = new char[][] { currentName.toCharArray() };
/*      */       }
/* 3733 */       ReferenceBinding type = environment.getType(compoundName);
/* 3734 */       if ((type != null) && (type.isValidBinding())) {
/* 3735 */         if (type.isBinaryBinding())
/* 3736 */           referenceBindings[i] = type;
/*      */       }
/*      */       else {
/* 3739 */         throw new IllegalArgumentException(
/* 3740 */           bind("configure.invalidClassName", currentName));
/*      */       }
/*      */     }
/* 3743 */     return referenceBindings;
/*      */   }
/*      */ 
/*      */   public void processPathEntries(int defaultSize, ArrayList paths, String currentPath, String customEncoding, boolean isSourceOnly, boolean rejectDestinationPathOnJars)
/*      */   {
/* 3751 */     String currentClasspathName = null;
/* 3752 */     String currentDestinationPath = null;
/* 3753 */     ArrayList currentRuleSpecs = new ArrayList(defaultSize);
/* 3754 */     StringTokenizer tokenizer = new StringTokenizer(currentPath, 
/* 3755 */       File.pathSeparator + "[]", true);
/* 3756 */     ArrayList tokens = new ArrayList();
/* 3757 */     while (tokenizer.hasMoreTokens()) {
/* 3758 */       tokens.add(tokenizer.nextToken());
/*      */     }
/*      */ 
/* 3786 */     int state = 0;
/* 3787 */     String token = null;
/* 3788 */     int cursor = 0; int tokensNb = tokens.size(); int bracket = -1;
/* 3789 */     label712: while ((cursor < tokensNb) && (state != 99)) {
/* 3790 */       token = (String)tokens.get(cursor++);
/* 3791 */       if (token.equals(File.pathSeparator)) {
/* 3792 */         switch (state) {
/*      */         case 0:
/*      */         case 3:
/*      */         case 10:
/* 3796 */           break;
/*      */         case 1:
/*      */         case 2:
/*      */         case 8:
/* 3800 */           state = 3;
/* 3801 */           addNewEntry(paths, currentClasspathName, currentRuleSpecs, 
/* 3802 */             customEncoding, currentDestinationPath, isSourceOnly, 
/* 3803 */             rejectDestinationPathOnJars);
/* 3804 */           currentRuleSpecs.clear();
/* 3805 */           break;
/*      */         case 6:
/* 3807 */           state = 4;
/* 3808 */           break;
/*      */         case 7:
/* 3810 */           throw new IllegalArgumentException(
/* 3811 */             bind("configure.incorrectDestinationPathEntry", 
/* 3812 */             currentPath));
/*      */         case 11:
/* 3814 */           cursor = bracket + 1;
/* 3815 */           state = 5;
/* 3816 */           break;
/*      */         case 4:
/*      */         case 5:
/*      */         case 9:
/*      */         default:
/* 3818 */           state = 99; break;
/*      */         }
/* 3820 */       } else if (token.equals("[")) {
/* 3821 */         switch (state) {
/*      */         case 0:
/* 3823 */           currentClasspathName = "";
/*      */         case 1:
/* 3826 */           bracket = cursor - 1;
/*      */         case 11:
/* 3829 */           state = 10;
/* 3830 */           break;
/*      */         case 2:
/* 3832 */           state = 9;
/* 3833 */           break;
/*      */         case 8:
/* 3835 */           state = 5;
/* 3836 */           break;
/*      */         case 3:
/*      */         case 4:
/*      */         case 5:
/*      */         case 6:
/*      */         case 7:
/*      */         case 9:
/*      */         case 10:
/*      */         default:
/* 3839 */           state = 99; break;
/*      */         }
/* 3841 */       } else if (token.equals("]")) {
/* 3842 */         switch (state) {
/*      */         case 6:
/* 3844 */           state = 2;
/* 3845 */           break;
/*      */         case 7:
/* 3847 */           state = 8;
/* 3848 */           break;
/*      */         case 10:
/* 3850 */           state = 11;
/* 3851 */           break;
/*      */         case 8:
/*      */         case 9:
/*      */         case 11:
/*      */         default:
/* 3854 */           state = 99; break;
/*      */         }
/*      */       }
/*      */       else {
/* 3858 */         switch (state) {
/*      */         case 0:
/*      */         case 3:
/* 3861 */           state = 1;
/* 3862 */           currentClasspathName = token;
/* 3863 */           break;
/*      */         case 5:
/* 3865 */           if (!token.startsWith("-d ")) break;
/* 3866 */           if (currentDestinationPath != null) {
/* 3867 */             throw new IllegalArgumentException(
/* 3868 */               bind("configure.duplicateDestinationPathEntry", 
/* 3869 */               currentPath));
/*      */           }
/* 3871 */           currentDestinationPath = token.substring(3).trim();
/* 3872 */           state = 7;
/* 3873 */           break;
/*      */         case 4:
/* 3877 */           if (currentDestinationPath != null) {
/* 3878 */             throw new IllegalArgumentException(
/* 3879 */               bind("configure.accessRuleAfterDestinationPath", 
/* 3880 */               currentPath));
/*      */           }
/* 3882 */           state = 6;
/* 3883 */           currentRuleSpecs.add(token);
/* 3884 */           break;
/*      */         case 9:
/* 3886 */           if (!token.startsWith("-d ")) {
/* 3887 */             state = 99; break label712;
/*      */           }
/* 3889 */           currentDestinationPath = token.substring(3).trim();
/* 3890 */           state = 7;
/*      */ 
/* 3892 */           break;
/*      */         case 11:
/* 3894 */           for (int i = bracket; i < cursor; i++) {
/* 3895 */             currentClasspathName = currentClasspathName + (String)tokens.get(i);
/*      */           }
/* 3897 */           state = 1;
/* 3898 */           break;
/*      */         case 10:
/* 3900 */           break;
/*      */         case 1:
/*      */         case 2:
/*      */         case 6:
/*      */         case 7:
/* 3902 */         case 8: } state = 99;
/*      */       }
/*      */ 
/* 3905 */       if ((state == 11) && (cursor == tokensNb)) {
/* 3906 */         cursor = bracket + 1;
/* 3907 */         state = 5;
/*      */       }
/*      */     }
/* 3910 */     switch (state) {
/*      */     case 3:
/* 3912 */       break;
/*      */     case 1:
/*      */     case 2:
/*      */     case 8:
/* 3916 */       addNewEntry(paths, currentClasspathName, currentRuleSpecs, 
/* 3917 */         customEncoding, currentDestinationPath, isSourceOnly, 
/* 3918 */         rejectDestinationPathOnJars);
/* 3919 */       break;
/*      */     case 4:
/*      */     case 5:
/*      */     case 6:
/*      */     case 7:
/*      */     case 9:
/*      */     case 10:
/*      */     case 11:
/*      */     default:
/* 3924 */       if (currentPath.length() == 0) break;
/* 3925 */       addPendingErrors(bind("configure.incorrectClasspath", currentPath));
/*      */     }
/*      */   }
/*      */ 
/*      */   private int processPaths(String[] args, int index, String currentArg, ArrayList paths)
/*      */   {
/* 3931 */     int localIndex = index;
/* 3932 */     int count = 0;
/* 3933 */     int i = 0; for (int max = currentArg.length(); i < max; i++) {
/* 3934 */       switch (currentArg.charAt(i)) {
/*      */       case '[':
/* 3936 */         count++;
/* 3937 */         break;
/*      */       case ']':
/* 3939 */         count--;
/*      */       case '\\':
/*      */       }
/*      */     }
/* 3943 */     if (count == 0) {
/* 3944 */       paths.add(currentArg); } else {
/* 3945 */       if (count > 1) {
/* 3946 */         throw new IllegalArgumentException(
/* 3947 */           bind("configure.unexpectedBracket", 
/* 3948 */           currentArg));
/*      */       }
/* 3950 */       StringBuffer currentPath = new StringBuffer(currentArg);
/*      */       while (true) {
/* 3952 */         if (localIndex >= args.length) {
/* 3953 */           throw new IllegalArgumentException(
/* 3954 */             bind("configure.unexpectedBracket", 
/* 3955 */             currentArg));
/*      */         }
/* 3957 */         localIndex++;
/* 3958 */         String nextArg = args[localIndex];
/* 3959 */         int i = 0; for (int max = nextArg.length(); i < max; i++) {
/* 3960 */           switch (nextArg.charAt(i)) {
/*      */           case '[':
/* 3962 */             if (count > 1) {
/* 3963 */               throw new IllegalArgumentException(
/* 3964 */                 bind("configure.unexpectedBracket", 
/* 3965 */                 nextArg));
/*      */             }
/* 3967 */             count++;
/* 3968 */             break;
/*      */           case ']':
/* 3970 */             count--;
/*      */           case '\\':
/*      */           }
/*      */         }
/* 3974 */         if (count == 0) {
/* 3975 */           currentPath.append(' ');
/* 3976 */           currentPath.append(nextArg);
/* 3977 */           paths.add(currentPath.toString());
/* 3978 */           return localIndex - index;
/* 3979 */         }if (count < 0) {
/* 3980 */           throw new IllegalArgumentException(
/* 3981 */             bind("configure.unexpectedBracket", 
/* 3982 */             nextArg));
/*      */         }
/* 3984 */         currentPath.append(' ');
/* 3985 */         currentPath.append(nextArg);
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 3990 */     return localIndex - index;
/*      */   }
/*      */   private int processPaths(String[] args, int index, String currentArg, String[] paths) {
/* 3993 */     int localIndex = index;
/* 3994 */     int count = 0;
/* 3995 */     int i = 0; for (int max = currentArg.length(); i < max; i++) {
/* 3996 */       switch (currentArg.charAt(i)) {
/*      */       case '[':
/* 3998 */         count++;
/* 3999 */         break;
/*      */       case ']':
/* 4001 */         count--;
/*      */       case '\\':
/*      */       }
/*      */     }
/* 4005 */     if (count == 0) {
/* 4006 */       paths[0] = currentArg;
/*      */     } else {
/* 4008 */       StringBuffer currentPath = new StringBuffer(currentArg);
/*      */       while (true) {
/* 4010 */         localIndex++;
/* 4011 */         if (localIndex >= args.length) {
/* 4012 */           throw new IllegalArgumentException(
/* 4013 */             bind("configure.unexpectedBracket", 
/* 4014 */             currentArg));
/*      */         }
/* 4016 */         String nextArg = args[localIndex];
/* 4017 */         int i = 0; for (int max = nextArg.length(); i < max; i++) {
/* 4018 */           switch (nextArg.charAt(i)) {
/*      */           case '[':
/* 4020 */             if (count > 1) {
/* 4021 */               throw new IllegalArgumentException(
/* 4022 */                 bind("configure.unexpectedBracket", 
/* 4023 */                 currentArg));
/*      */             }
/* 4025 */             count++;
/* 4026 */             break;
/*      */           case ']':
/* 4028 */             count--;
/*      */           case '\\':
/*      */           }
/*      */         }
/* 4032 */         if (count == 0) {
/* 4033 */           currentPath.append(' ');
/* 4034 */           currentPath.append(nextArg);
/* 4035 */           paths[0] = currentPath.toString();
/* 4036 */           return localIndex - index;
/* 4037 */         }if (count < 0) {
/* 4038 */           throw new IllegalArgumentException(
/* 4039 */             bind("configure.unexpectedBracket", 
/* 4040 */             currentArg));
/*      */         }
/* 4042 */         currentPath.append(' ');
/* 4043 */         currentPath.append(nextArg);
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 4048 */     return localIndex - index;
/*      */   }
/*      */ 
/*      */   public void relocalize()
/*      */   {
/* 4054 */     relocalize(Locale.getDefault());
/*      */   }
/*      */ 
/*      */   private void relocalize(Locale locale) {
/* 4058 */     this.compilerLocale = locale;
/*      */     try {
/* 4060 */       this.bundle = ResourceBundleFactory.getBundle(locale);
/*      */     } catch (MissingResourceException e) {
/* 4062 */       System.out.println("Missing resource : " + "org.eclipse.jdt.internal.compiler.batch.messages".replace('.', '/') + ".properties for locale " + locale);
/* 4063 */       throw e;
/*      */     }
/*      */   }
/*      */ 
/*      */   public void setDestinationPath(String dest)
/*      */   {
/* 4070 */     this.destinationPath = dest;
/*      */   }
/*      */ 
/*      */   public void setLocale(Locale locale)
/*      */   {
/* 4076 */     relocalize(locale);
/*      */   }
/*      */ 
/*      */   protected void setPaths(ArrayList bootclasspaths, String sourcepathClasspathArg, ArrayList sourcepathClasspaths, ArrayList classpaths, ArrayList extdirsClasspaths, ArrayList endorsedDirClasspaths, String customEncoding)
/*      */   {
/* 4090 */     bootclasspaths = handleBootclasspath(bootclasspaths, customEncoding);
/*      */ 
/* 4092 */     classpaths = handleClasspath(classpaths, customEncoding);
/*      */ 
/* 4094 */     if (sourcepathClasspathArg != null) {
/* 4095 */       processPathEntries(4, sourcepathClasspaths, 
/* 4096 */         sourcepathClasspathArg, customEncoding, true, false);
/*      */     }
/*      */ 
/* 4105 */     extdirsClasspaths = handleExtdirs(extdirsClasspaths);
/*      */ 
/* 4107 */     endorsedDirClasspaths = handleEndorseddirs(endorsedDirClasspaths);
/*      */ 
/* 4117 */     bootclasspaths.addAll(endorsedDirClasspaths);
/* 4118 */     bootclasspaths.addAll(extdirsClasspaths);
/* 4119 */     bootclasspaths.addAll(sourcepathClasspaths);
/* 4120 */     bootclasspaths.addAll(classpaths);
/* 4121 */     classpaths = bootclasspaths;
/* 4122 */     classpaths = FileSystem.ClasspathNormalizer.normalize(classpaths);
/* 4123 */     this.checkedClasspaths = new FileSystem.Classpath[classpaths.size()];
/* 4124 */     classpaths.toArray(this.checkedClasspaths);
/* 4125 */     this.logger.logClasspath(this.checkedClasspaths);
/*      */   }
/*      */   protected void validateOptions(boolean didSpecifyCompliance) {
/* 4128 */     if (didSpecifyCompliance) {
/* 4129 */       Object version = this.options.get("org.eclipse.jdt.core.compiler.compliance");
/* 4130 */       if ("1.3".equals(version)) {
/* 4131 */         if (!this.didSpecifySource) this.options.put("org.eclipse.jdt.core.compiler.source", "1.3");
/* 4132 */         if (!this.didSpecifyTarget) this.options.put("org.eclipse.jdt.core.compiler.codegen.targetPlatform", "1.1"); 
/*      */       }
/* 4133 */       else if ("1.4".equals(version)) {
/* 4134 */         if (this.didSpecifySource) {
/* 4135 */           Object source = this.options.get("org.eclipse.jdt.core.compiler.source");
/* 4136 */           if ("1.3".equals(source)) {
/* 4137 */             if (!this.didSpecifyTarget) this.options.put("org.eclipse.jdt.core.compiler.codegen.targetPlatform", "1.2"); 
/*      */           }
/* 4138 */           else if (("1.4".equals(source)) && 
/* 4139 */             (!this.didSpecifyTarget)) this.options.put("org.eclipse.jdt.core.compiler.codegen.targetPlatform", "1.4"); 
/*      */         }
/*      */         else
/*      */         {
/* 4142 */           this.options.put("org.eclipse.jdt.core.compiler.source", "1.3");
/* 4143 */           if (!this.didSpecifyTarget) this.options.put("org.eclipse.jdt.core.compiler.codegen.targetPlatform", "1.2"); 
/*      */         }
/*      */       }
/* 4145 */       else if ("1.5".equals(version)) {
/* 4146 */         if (this.didSpecifySource) {
/* 4147 */           Object source = this.options.get("org.eclipse.jdt.core.compiler.source");
/* 4148 */           if (("1.3".equals(source)) || 
/* 4149 */             ("1.4".equals(source))) {
/* 4150 */             if (!this.didSpecifyTarget) this.options.put("org.eclipse.jdt.core.compiler.codegen.targetPlatform", "1.4"); 
/*      */           }
/* 4151 */           else if (("1.5".equals(source)) && 
/* 4152 */             (!this.didSpecifyTarget)) this.options.put("org.eclipse.jdt.core.compiler.codegen.targetPlatform", "1.5"); 
/*      */         }
/*      */         else
/*      */         {
/* 4155 */           this.options.put("org.eclipse.jdt.core.compiler.source", "1.5");
/* 4156 */           if (!this.didSpecifyTarget) this.options.put("org.eclipse.jdt.core.compiler.codegen.targetPlatform", "1.5"); 
/*      */         }
/*      */       }
/* 4158 */       else if ("1.6".equals(version)) {
/* 4159 */         if (this.didSpecifySource) {
/* 4160 */           Object source = this.options.get("org.eclipse.jdt.core.compiler.source");
/* 4161 */           if (("1.3".equals(source)) || 
/* 4162 */             ("1.4".equals(source))) {
/* 4163 */             if (!this.didSpecifyTarget) this.options.put("org.eclipse.jdt.core.compiler.codegen.targetPlatform", "1.4"); 
/*      */           }
/* 4164 */           else if ((("1.5".equals(source)) || 
/* 4165 */             ("1.6".equals(source))) && 
/* 4166 */             (!this.didSpecifyTarget)) this.options.put("org.eclipse.jdt.core.compiler.codegen.targetPlatform", "1.6"); 
/*      */         }
/*      */         else
/*      */         {
/* 4169 */           this.options.put("org.eclipse.jdt.core.compiler.source", "1.6");
/* 4170 */           if (!this.didSpecifyTarget) this.options.put("org.eclipse.jdt.core.compiler.codegen.targetPlatform", "1.6"); 
/*      */         }
/*      */       }
/* 4172 */       else if ("1.7".equals(version)) {
/* 4173 */         if (this.didSpecifySource) {
/* 4174 */           Object source = this.options.get("org.eclipse.jdt.core.compiler.source");
/* 4175 */           if (("1.3".equals(source)) || 
/* 4176 */             ("1.4".equals(source))) {
/* 4177 */             if (!this.didSpecifyTarget) this.options.put("org.eclipse.jdt.core.compiler.codegen.targetPlatform", "1.4"); 
/*      */           }
/* 4178 */           else if (("1.5".equals(source)) || 
/* 4179 */             ("1.6".equals(source))) {
/* 4180 */             if (!this.didSpecifyTarget) this.options.put("org.eclipse.jdt.core.compiler.codegen.targetPlatform", "1.6"); 
/*      */           }
/* 4181 */           else if (("1.7".equals(source)) && 
/* 4182 */             (!this.didSpecifyTarget)) this.options.put("org.eclipse.jdt.core.compiler.codegen.targetPlatform", "1.7"); 
/*      */         }
/*      */         else
/*      */         {
/* 4185 */           this.options.put("org.eclipse.jdt.core.compiler.source", "1.7");
/* 4186 */           if (!this.didSpecifyTarget) this.options.put("org.eclipse.jdt.core.compiler.codegen.targetPlatform", "1.7"); 
/*      */         }
/*      */       }
/*      */     }
/* 4189 */     else if (this.didSpecifySource) {
/* 4190 */       Object version = this.options.get("org.eclipse.jdt.core.compiler.source");
/*      */ 
/* 4192 */       if ("1.4".equals(version)) {
/* 4193 */         if (!didSpecifyCompliance) this.options.put("org.eclipse.jdt.core.compiler.compliance", "1.4");
/* 4194 */         if (!this.didSpecifyTarget) this.options.put("org.eclipse.jdt.core.compiler.codegen.targetPlatform", "1.4"); 
/*      */       }
/* 4195 */       else if ("1.5".equals(version)) {
/* 4196 */         if (!didSpecifyCompliance) this.options.put("org.eclipse.jdt.core.compiler.compliance", "1.5");
/* 4197 */         if (!this.didSpecifyTarget) this.options.put("org.eclipse.jdt.core.compiler.codegen.targetPlatform", "1.5"); 
/*      */       }
/* 4198 */       else if ("1.6".equals(version)) {
/* 4199 */         if (!didSpecifyCompliance) this.options.put("org.eclipse.jdt.core.compiler.compliance", "1.6");
/* 4200 */         if (!this.didSpecifyTarget) this.options.put("org.eclipse.jdt.core.compiler.codegen.targetPlatform", "1.6"); 
/*      */       }
/* 4201 */       else if ("1.7".equals(version)) {
/* 4202 */         if (!didSpecifyCompliance) this.options.put("org.eclipse.jdt.core.compiler.compliance", "1.7");
/* 4203 */         if (!this.didSpecifyTarget) this.options.put("org.eclipse.jdt.core.compiler.codegen.targetPlatform", "1.7");
/*      */       }
/*      */     }
/*      */ 
/* 4207 */     Object sourceVersion = this.options.get("org.eclipse.jdt.core.compiler.source");
/* 4208 */     Object compliance = this.options.get("org.eclipse.jdt.core.compiler.compliance");
/* 4209 */     if ((sourceVersion.equals("1.7")) && 
/* 4210 */       (CompilerOptions.versionToJdkLevel(compliance) < 3342336L))
/*      */     {
/* 4212 */       throw new IllegalArgumentException(bind("configure.incompatibleComplianceForSource", (String)this.options.get("org.eclipse.jdt.core.compiler.compliance"), "1.7"));
/* 4213 */     }if ((sourceVersion.equals("1.6")) && 
/* 4214 */       (CompilerOptions.versionToJdkLevel(compliance) < 3276800L))
/*      */     {
/* 4216 */       throw new IllegalArgumentException(bind("configure.incompatibleComplianceForSource", (String)this.options.get("org.eclipse.jdt.core.compiler.compliance"), "1.6"));
/* 4217 */     }if ((sourceVersion.equals("1.5")) && 
/* 4218 */       (CompilerOptions.versionToJdkLevel(compliance) < 3211264L))
/*      */     {
/* 4220 */       throw new IllegalArgumentException(bind("configure.incompatibleComplianceForSource", (String)this.options.get("org.eclipse.jdt.core.compiler.compliance"), "1.5"));
/* 4221 */     }if ((sourceVersion.equals("1.4")) && 
/* 4222 */       (CompilerOptions.versionToJdkLevel(compliance) < 3145728L))
/*      */     {
/* 4224 */       throw new IllegalArgumentException(bind("configure.incompatibleComplianceForSource", (String)this.options.get("org.eclipse.jdt.core.compiler.compliance"), "1.4"));
/*      */     }
/*      */ 
/* 4228 */     if (this.didSpecifyTarget) {
/* 4229 */       Object targetVersion = this.options.get("org.eclipse.jdt.core.compiler.codegen.targetPlatform");
/*      */ 
/* 4231 */       if ("jsr14".equals(targetVersion))
/*      */       {
/* 4233 */         if (CompilerOptions.versionToJdkLevel(sourceVersion) < 3211264L)
/* 4234 */           throw new IllegalArgumentException(bind("configure.incompatibleTargetForGenericSource", (String)targetVersion, (String)sourceVersion));
/*      */       }
/* 4236 */       else if ("cldc1.1".equals(targetVersion)) {
/* 4237 */         if ((this.didSpecifySource) && (CompilerOptions.versionToJdkLevel(sourceVersion) >= 3145728L)) {
/* 4238 */           throw new IllegalArgumentException(bind("configure.incompatibleSourceForCldcTarget", (String)targetVersion, (String)sourceVersion));
/*      */         }
/* 4240 */         if (CompilerOptions.versionToJdkLevel(compliance) >= 3211264L)
/* 4241 */           throw new IllegalArgumentException(bind("configure.incompatibleComplianceForCldcTarget", (String)targetVersion, (String)sourceVersion));
/*      */       }
/*      */       else
/*      */       {
/* 4245 */         if ((CompilerOptions.versionToJdkLevel(sourceVersion) >= 3342336L) && 
/* 4246 */           (CompilerOptions.versionToJdkLevel(targetVersion) < 3342336L)) {
/* 4247 */           throw new IllegalArgumentException(bind("configure.incompatibleTargetForSource", (String)targetVersion, "1.7"));
/*      */         }
/*      */ 
/* 4250 */         if ((CompilerOptions.versionToJdkLevel(sourceVersion) >= 3276800L) && 
/* 4251 */           (CompilerOptions.versionToJdkLevel(targetVersion) < 3276800L)) {
/* 4252 */           throw new IllegalArgumentException(bind("configure.incompatibleTargetForSource", (String)targetVersion, "1.6"));
/*      */         }
/*      */ 
/* 4255 */         if ((CompilerOptions.versionToJdkLevel(sourceVersion) >= 3211264L) && 
/* 4256 */           (CompilerOptions.versionToJdkLevel(targetVersion) < 3211264L)) {
/* 4257 */           throw new IllegalArgumentException(bind("configure.incompatibleTargetForSource", (String)targetVersion, "1.5"));
/*      */         }
/*      */ 
/* 4260 */         if ((CompilerOptions.versionToJdkLevel(sourceVersion) >= 3145728L) && 
/* 4261 */           (CompilerOptions.versionToJdkLevel(targetVersion) < 3145728L)) {
/* 4262 */           throw new IllegalArgumentException(bind("configure.incompatibleTargetForSource", (String)targetVersion, "1.4"));
/*      */         }
/*      */ 
/* 4265 */         if (CompilerOptions.versionToJdkLevel(compliance) < CompilerOptions.versionToJdkLevel(targetVersion))
/* 4266 */           throw new IllegalArgumentException(bind("configure.incompatibleComplianceForTarget", (String)this.options.get("org.eclipse.jdt.core.compiler.compliance"), (String)targetVersion));
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   public static class Logger
/*      */   {
/*      */     private PrintWriter err;
/*      */     private PrintWriter log;
/*      */     private Main main;
/*      */     private PrintWriter out;
/*      */     private HashMap parameters;
/*      */     int tagBits;
/*      */     private static final String CLASS = "class";
/*      */     private static final String CLASS_FILE = "classfile";
/*      */     private static final String CLASSPATH = "classpath";
/*      */     private static final String CLASSPATH_FILE = "FILE";
/*      */     private static final String CLASSPATH_FOLDER = "FOLDER";
/*      */     private static final String CLASSPATH_ID = "id";
/*      */     private static final String CLASSPATH_JAR = "JAR";
/*      */     private static final String CLASSPATHS = "classpaths";
/*      */     private static final String COMMAND_LINE_ARGUMENT = "argument";
/*      */     private static final String COMMAND_LINE_ARGUMENTS = "command_line";
/*      */     private static final String COMPILER = "compiler";
/*      */     private static final String COMPILER_COPYRIGHT = "copyright";
/*      */     private static final String COMPILER_NAME = "name";
/*      */     private static final String COMPILER_VERSION = "version";
/*      */     public static final int EMACS = 2;
/*      */     private static final String ERROR = "ERROR";
/*      */     private static final String ERROR_TAG = "error";
/*      */     private static final String EXCEPTION = "exception";
/*      */     private static final String EXTRA_PROBLEM_TAG = "extra_problem";
/*      */     private static final String EXTRA_PROBLEMS = "extra_problems";
/*  106 */     private static final HashtableOfInt FIELD_TABLE = new HashtableOfInt();
/*      */     private static final String KEY = "key";
/*      */     private static final String MESSAGE = "message";
/*      */     private static final String NUMBER_OF_CLASSFILES = "number_of_classfiles";
/*      */     private static final String NUMBER_OF_ERRORS = "errors";
/*      */     private static final String NUMBER_OF_LINES = "number_of_lines";
/*      */     private static final String NUMBER_OF_PROBLEMS = "problems";
/*      */     private static final String NUMBER_OF_TASKS = "tasks";
/*      */     private static final String NUMBER_OF_WARNINGS = "warnings";
/*      */     private static final String OPTION = "option";
/*      */     private static final String OPTIONS = "options";
/*      */     private static final String OUTPUT = "output";
/*      */     private static final String PACKAGE = "package";
/*      */     private static final String PATH = "path";
/*      */     private static final String PROBLEM_ARGUMENT = "argument";
/*      */     private static final String PROBLEM_ARGUMENT_VALUE = "value";
/*      */     private static final String PROBLEM_ARGUMENTS = "arguments";
/*      */     private static final String PROBLEM_CATEGORY_ID = "categoryID";
/*      */     private static final String ID = "id";
/*      */     private static final String PROBLEM_ID = "problemID";
/*      */     private static final String PROBLEM_LINE = "line";
/*      */     private static final String PROBLEM_OPTION_KEY = "optionKey";
/*      */     private static final String PROBLEM_MESSAGE = "message";
/*      */     private static final String PROBLEM_SEVERITY = "severity";
/*      */     private static final String PROBLEM_SOURCE_END = "charEnd";
/*      */     private static final String PROBLEM_SOURCE_START = "charStart";
/*      */     private static final String PROBLEM_SUMMARY = "problem_summary";
/*      */     private static final String PROBLEM_TAG = "problem";
/*      */     private static final String PROBLEMS = "problems";
/*      */     private static final String SOURCE = "source";
/*      */     private static final String SOURCE_CONTEXT = "source_context";
/*      */     private static final String SOURCE_END = "sourceEnd";
/*      */     private static final String SOURCE_START = "sourceStart";
/*      */     private static final String SOURCES = "sources";
/*      */     private static final String STATS = "stats";
/*      */     private static final String TASK = "task";
/*      */     private static final String TASKS = "tasks";
/*      */     private static final String TIME = "time";
/*      */     private static final String VALUE = "value";
/*      */     private static final String WARNING = "WARNING";
/*      */     public static final int XML = 1;
/*      */     private static final String XML_DTD_DECLARATION = "<!DOCTYPE compiler PUBLIC \"-//Eclipse.org//DTD Eclipse JDT 3.2.003 Compiler//EN\" \"http://www.eclipse.org/jdt/core/compiler_32_003.dtd\">";
/*      */ 
/*      */     static
/*      */     {
/*      */       try
/*      */       {
/*  152 */         Class tmp13_10 = Main.class$0; if (tmp13_10 == null) { tmp13_10;
/*      */           try { tmpTernaryOp = (Main.class$0 = Class.forName("org.eclipse.jdt.core.compiler.IProblem")); } catch (ClassNotFoundException localClassNotFoundException) { throw new NoClassDefFoundError(localClassNotFoundException.getMessage()); }  }
/*  152 */         Class c = tmp13_10;
/*  153 */         Field[] fields = c.getFields();
/*  154 */         int i = 0; for (int max = fields.length; i < max; i++) {
/*  155 */           Field field = fields[i];
/*  156 */           if (field.getType().equals(Integer.TYPE)) {
/*  157 */             Integer value = (Integer)field.get(null);
/*  158 */             int key2 = value.intValue() & 0xFFFFFF;
/*  159 */             if (key2 == 0) {
/*  160 */               key2 = 2147483647;
/*      */             }
/*  162 */             FIELD_TABLE.put(key2, field.getName());
/*      */           }
/*      */         }
/*      */       } catch (SecurityException e) {
/*  166 */         e.printStackTrace();
/*      */       } catch (IllegalArgumentException e) {
/*  168 */         e.printStackTrace();
/*      */       } catch (IllegalAccessException e) {
/*  170 */         e.printStackTrace();
/*      */       }
/*      */     }
/*      */ 
/*      */     public Logger(Main main, PrintWriter out, PrintWriter err) {
/*  174 */       this.out = out;
/*  175 */       this.err = err;
/*  176 */       this.parameters = new HashMap();
/*  177 */       this.main = main;
/*      */     }
/*      */ 
/*      */     public String buildFileName(String outputPath, String relativeFileName)
/*      */     {
/*  183 */       char fileSeparatorChar = File.separatorChar;
/*  184 */       String fileSeparator = File.separator;
/*      */ 
/*  186 */       outputPath = outputPath.replace('/', fileSeparatorChar);
/*      */ 
/*  188 */       StringBuffer outDir = new StringBuffer(outputPath);
/*  189 */       if (!outputPath.endsWith(fileSeparator)) {
/*  190 */         outDir.append(fileSeparator);
/*      */       }
/*  192 */       StringTokenizer tokenizer = 
/*  193 */         new StringTokenizer(relativeFileName, fileSeparator);
/*  194 */       String token = tokenizer.nextToken();
/*  195 */       while (tokenizer.hasMoreTokens()) {
/*  196 */         outDir.append(token).append(fileSeparator);
/*  197 */         token = tokenizer.nextToken();
/*      */       }
/*      */ 
/*  200 */       return token;
/*      */     }
/*      */ 
/*      */     public void close() {
/*  204 */       if (this.log != null) {
/*  205 */         if ((this.tagBits & 0x1) != 0) {
/*  206 */           endTag("compiler");
/*  207 */           flush();
/*      */         }
/*  209 */         this.log.close();
/*      */       }
/*      */     }
/*      */ 
/*      */     public void compiling()
/*      */     {
/*  217 */       printlnOut(this.main.bind("progress.compiling"));
/*      */     }
/*      */     private void endLoggingExtraProblems() {
/*  220 */       endTag("extra_problems");
/*      */     }
/*      */ 
/*      */     private void endLoggingProblems()
/*      */     {
/*  227 */       endTag("problems");
/*      */     }
/*      */     public void endLoggingSource() {
/*  230 */       if ((this.tagBits & 0x1) != 0)
/*  231 */         endTag("source");
/*      */     }
/*      */ 
/*      */     public void endLoggingSources()
/*      */     {
/*  236 */       if ((this.tagBits & 0x1) != 0)
/*  237 */         endTag("sources");
/*      */     }
/*      */ 
/*      */     public void endLoggingTasks()
/*      */     {
/*  242 */       if ((this.tagBits & 0x1) != 0)
/*  243 */         endTag("tasks");
/*      */     }
/*      */ 
/*      */     private void endTag(String name) {
/*  247 */       if (this.log != null)
/*  248 */         ((GenericXMLWriter)this.log).endTag(name, true, true);
/*      */     }
/*      */ 
/*      */     private String errorReportSource(CategorizedProblem problem, char[] unitSource, int bits)
/*      */     {
/*  259 */       int startPosition = problem.getSourceStart();
/*  260 */       int endPosition = problem.getSourceEnd();
/*  261 */       if ((unitSource == null) && 
/*  262 */         (problem.getOriginatingFileName() != null)) {
/*      */         try {
/*  264 */           unitSource = Util.getFileCharContent(new File(new String(problem.getOriginatingFileName())), null);
/*      */         }
/*      */         catch (IOException localIOException)
/*      */         {
/*      */         }
/*      */       }
/*  270 */       int length = unitSource == null ? 0 : unitSource.length;
/*  271 */       if ((startPosition > endPosition) || 
/*  272 */         ((startPosition < 0) && (endPosition < 0)) || 
/*  273 */         (length == 0)) {
/*  274 */         return Messages.problem_noSourceInformation;
/*      */       }
/*  276 */       StringBuffer errorBuffer = new StringBuffer();
/*  277 */       if ((bits & 0x2) == 0) {
/*  278 */         errorBuffer.append(' ').append(Messages.bind(Messages.problem_atLine, String.valueOf(problem.getSourceLineNumber())));
/*  279 */         errorBuffer.append(Util.LINE_SEPARATOR);
/*      */       }
/*  281 */       errorBuffer.append('\t');
/*      */ 
/*  295 */       for (int begin = startPosition >= length ? length - 1 : startPosition; begin > 0; begin--)
/*      */       {
/*      */         char c;
/*  296 */         if (((c = unitSource[(begin - 1)]) == '\n') || (c == '\r')) break;
/*      */       }
/*  298 */       for (int end = endPosition >= length ? length - 1 : endPosition; end + 1 < length; end++)
/*      */       {
/*      */         char c;
/*  299 */         if (((c = unitSource[(end + 1)]) == '\r') || (c == '\n'))
/*      */           break;
/*      */       }
/*      */       char c;
/*  303 */       while (((c = unitSource[begin]) == ' ') || (c == '\t'))
/*      */       {
/*      */         char c;
/*  303 */         begin++;
/*      */       }
/*      */ 
/*  307 */       errorBuffer.append(unitSource, begin, end - begin + 1);
/*  308 */       errorBuffer.append(Util.LINE_SEPARATOR).append("\t");
/*      */ 
/*  311 */       for (int i = begin; i < startPosition; i++) {
/*  312 */         errorBuffer.append(unitSource[i] == '\t' ? '\t' : ' ');
/*      */       }
/*  314 */       for (int i = startPosition; i <= (endPosition >= length ? length - 1 : endPosition); i++) {
/*  315 */         errorBuffer.append('^');
/*      */       }
/*  317 */       return errorBuffer.toString();
/*      */     }
/*      */ 
/*      */     private void extractContext(CategorizedProblem problem, char[] unitSource)
/*      */     {
/*  322 */       int startPosition = problem.getSourceStart();
/*  323 */       int endPosition = problem.getSourceEnd();
/*  324 */       if ((unitSource == null) && 
/*  325 */         (problem.getOriginatingFileName() != null)) {
/*      */         try {
/*  327 */           unitSource = Util.getFileCharContent(new File(new String(problem.getOriginatingFileName())), null);
/*      */         }
/*      */         catch (IOException localIOException)
/*      */         {
/*      */         }
/*      */       }
/*  333 */       int length = unitSource == null ? 0 : unitSource.length;
/*  334 */       if ((startPosition > endPosition) || 
/*  335 */         ((startPosition < 0) && (endPosition < 0)) || 
/*  336 */         (length <= 0) || 
/*  337 */         (endPosition > length)) {
/*  338 */         this.parameters.put("value", Messages.problem_noSourceInformation);
/*  339 */         this.parameters.put("sourceStart", "-1");
/*  340 */         this.parameters.put("sourceEnd", "-1");
/*  341 */         printTag("source_context", this.parameters, true, true);
/*  342 */         return;
/*      */       }
/*      */ 
/*  353 */       for (int begin = startPosition >= length ? length - 1 : startPosition; begin > 0; begin--)
/*      */       {
/*      */         char c;
/*  354 */         if (((c = unitSource[(begin - 1)]) == '\n') || (c == '\r')) break;
/*      */       }
/*  356 */       for (int end = endPosition >= length ? length - 1 : endPosition; end + 1 < length; end++)
/*      */       {
/*      */         char c;
/*  357 */         if (((c = unitSource[(end + 1)]) == '\r') || (c == '\n'))
/*      */           break;
/*      */       }
/*      */       char c;
/*  361 */       while (((c = unitSource[begin]) == ' ') || (c == '\t'))
/*      */       {
/*      */         char c;
/*  361 */         begin++;
/*  362 */       }while (((c = unitSource[end]) == ' ') || (c == '\t')) end--;
/*      */ 
/*  365 */       StringBuffer buffer = new StringBuffer();
/*  366 */       buffer.append(unitSource, begin, end - begin + 1);
/*      */ 
/*  368 */       this.parameters.put("value", String.valueOf(buffer));
/*  369 */       this.parameters.put("sourceStart", Integer.toString(startPosition - begin));
/*  370 */       this.parameters.put("sourceEnd", Integer.toString(endPosition - begin));
/*  371 */       printTag("source_context", this.parameters, true, true);
/*      */     }
/*      */     public void flush() {
/*  374 */       this.out.flush();
/*  375 */       this.err.flush();
/*  376 */       if (this.log != null)
/*  377 */         this.log.flush();
/*      */     }
/*      */ 
/*      */     private String getFieldName(int id)
/*      */     {
/*  382 */       int key2 = id & 0xFFFFFF;
/*  383 */       if (key2 == 0) {
/*  384 */         key2 = 2147483647;
/*      */       }
/*  386 */       return (String)FIELD_TABLE.get(key2);
/*      */     }
/*      */ 
/*      */     private String getProblemOptionKey(int problemID)
/*      */     {
/*  391 */       int irritant = ProblemReporter.getIrritant(problemID);
/*  392 */       return CompilerOptions.optionKeyFromIrritant(irritant);
/*      */     }
/*      */ 
/*      */     public void logAverage() {
/*  396 */       Arrays.sort(this.main.compilerStats);
/*  397 */       long lineCount = this.main.compilerStats[0].lineCount;
/*  398 */       int length = this.main.maxRepetition;
/*  399 */       long sum = 0L;
/*  400 */       long parseSum = 0L; long resolveSum = 0L; long analyzeSum = 0L; long generateSum = 0L;
/*  401 */       int i = 1; for (int max = length - 1; i < max; i++) {
/*  402 */         CompilerStats stats = this.main.compilerStats[i];
/*  403 */         sum += stats.elapsedTime();
/*  404 */         parseSum += stats.parseTime;
/*  405 */         resolveSum += stats.resolveTime;
/*  406 */         analyzeSum += stats.analyzeTime;
/*  407 */         generateSum += stats.generateTime;
/*      */       }
/*  409 */       long time = sum / (length - 2);
/*  410 */       long parseTime = parseSum / (length - 2);
/*  411 */       long resolveTime = resolveSum / (length - 2);
/*  412 */       long analyzeTime = analyzeSum / (length - 2);
/*  413 */       long generateTime = generateSum / (length - 2);
/*  414 */       printlnOut(this.main.bind(
/*  415 */         "compile.averageTime", 
/*  416 */         new String[] { 
/*  417 */         String.valueOf(lineCount), 
/*  418 */         String.valueOf(time), 
/*  419 */         String.valueOf((int)(lineCount * 10000.0D / time) / 10.0D) }));
/*      */ 
/*  421 */       if ((this.main.timing & 0x2) != 0)
/*  422 */         printlnOut(
/*  423 */           this.main.bind("compile.detailedTime", 
/*  424 */           new String[] { 
/*  425 */           String.valueOf(parseTime), 
/*  426 */           String.valueOf((int)(parseTime * 1000.0D / time) / 10.0D), 
/*  427 */           String.valueOf(resolveTime), 
/*  428 */           String.valueOf((int)(resolveTime * 1000.0D / time) / 10.0D), 
/*  429 */           String.valueOf(analyzeTime), 
/*  430 */           String.valueOf((int)(analyzeTime * 1000.0D / time) / 10.0D), 
/*  431 */           String.valueOf(generateTime), 
/*  432 */           String.valueOf((int)(generateTime * 1000.0D / time) / 10.0D) }));
/*      */     }
/*      */ 
/*      */     public void logClassFile(boolean generatePackagesStructure, String outputPath, String relativeFileName)
/*      */     {
/*  437 */       if ((this.tagBits & 0x1) != 0) {
/*  438 */         String fileName = null;
/*  439 */         if (generatePackagesStructure) {
/*  440 */           fileName = buildFileName(outputPath, relativeFileName);
/*      */         } else {
/*  442 */           char fileSeparatorChar = File.separatorChar;
/*  443 */           String fileSeparator = File.separator;
/*      */ 
/*  445 */           outputPath = outputPath.replace('/', fileSeparatorChar);
/*      */ 
/*  447 */           int indexOfPackageSeparator = relativeFileName.lastIndexOf(fileSeparatorChar);
/*  448 */           if (indexOfPackageSeparator == -1) {
/*  449 */             if (outputPath.endsWith(fileSeparator))
/*  450 */               fileName = outputPath + relativeFileName;
/*      */             else
/*  452 */               fileName = outputPath + fileSeparator + relativeFileName;
/*      */           }
/*      */           else {
/*  455 */             int length = relativeFileName.length();
/*  456 */             if (outputPath.endsWith(fileSeparator))
/*  457 */               fileName = outputPath + relativeFileName.substring(indexOfPackageSeparator + 1, length);
/*      */             else {
/*  459 */               fileName = outputPath + fileSeparator + relativeFileName.substring(indexOfPackageSeparator + 1, length);
/*      */             }
/*      */           }
/*      */         }
/*  463 */         File f = new File(fileName);
/*      */         try {
/*  465 */           this.parameters.put("path", f.getCanonicalPath());
/*  466 */           printTag("classfile", this.parameters, true, true);
/*      */         } catch (IOException e) {
/*  468 */           logNoClassFileCreated(outputPath, relativeFileName, e);
/*      */         }
/*      */       }
/*      */     }
/*      */ 
/*      */     public void logClasspath(FileSystem.Classpath[] classpaths) {
/*  473 */       if (classpaths == null) return;
/*  474 */       if ((this.tagBits & 0x1) != 0) {
/*  475 */         int length = classpaths.length;
/*  476 */         if (length != 0)
/*      */         {
/*  478 */           printTag("classpaths", null, true, false);
/*  479 */           for (int i = 0; i < length; i++) {
/*  480 */             String classpath = classpaths[i].getPath();
/*  481 */             this.parameters.put("path", classpath);
/*  482 */             File f = new File(classpath);
/*  483 */             String id = null;
/*  484 */             if (f.isFile()) {
/*  485 */               if (Util.isPotentialZipArchive(classpath))
/*  486 */                 id = "JAR";
/*      */               else
/*  488 */                 id = "FILE";
/*      */             }
/*  490 */             else if (f.isDirectory()) {
/*  491 */               id = "FOLDER";
/*      */             }
/*  493 */             if (id != null) {
/*  494 */               this.parameters.put("id", id);
/*  495 */               printTag("classpath", this.parameters, true, true);
/*      */             }
/*      */           }
/*  498 */           endTag("classpaths");
/*      */         }
/*      */       }
/*      */     }
/*      */ 
/*      */     public void logCommandLineArguments(String[] commandLineArguments)
/*      */     {
/*  505 */       if (commandLineArguments == null) return;
/*  506 */       if ((this.tagBits & 0x1) != 0) {
/*  507 */         int length = commandLineArguments.length;
/*  508 */         if (length != 0)
/*      */         {
/*  510 */           printTag("command_line", null, true, false);
/*  511 */           for (int i = 0; i < length; i++) {
/*  512 */             this.parameters.put("value", commandLineArguments[i]);
/*  513 */             printTag("argument", this.parameters, true, true);
/*      */           }
/*  515 */           endTag("command_line");
/*      */         }
/*      */       }
/*      */     }
/*      */ 
/*      */     public void logException(Exception e)
/*      */     {
/*  524 */       StringWriter writer = new StringWriter();
/*  525 */       PrintWriter printWriter = new PrintWriter(writer);
/*  526 */       e.printStackTrace(printWriter);
/*  527 */       printWriter.flush();
/*  528 */       printWriter.close();
/*  529 */       String stackTrace = writer.toString();
/*  530 */       if ((this.tagBits & 0x1) != 0) {
/*  531 */         LineNumberReader reader = new LineNumberReader(new StringReader(stackTrace));
/*      */ 
/*  533 */         int i = 0;
/*  534 */         StringBuffer buffer = new StringBuffer();
/*  535 */         String message = e.getMessage();
/*  536 */         if (message != null)
/*  537 */           buffer.append(message).append(Util.LINE_SEPARATOR);
/*      */         try
/*      */         {
/*      */           String line;
/*  540 */           while (((line = reader.readLine()) != null) && (i < 4))
/*      */           {
/*      */             String line;
/*  541 */             buffer.append(line).append(Util.LINE_SEPARATOR);
/*  542 */             i++;
/*      */           }
/*  544 */           reader.close();
/*      */         }
/*      */         catch (IOException localIOException) {
/*      */         }
/*  548 */         message = buffer.toString();
/*  549 */         this.parameters.put("message", message);
/*  550 */         this.parameters.put("class", e.getClass());
/*  551 */         printTag("exception", this.parameters, true, true);
/*      */       }
/*  553 */       String message = e.getMessage();
/*  554 */       if (message == null)
/*  555 */         printlnErr(stackTrace);
/*      */       else
/*  557 */         printlnErr(message);
/*      */     }
/*      */ 
/*      */     private void logExtraProblem(CategorizedProblem problem, int localErrorCount, int globalErrorCount)
/*      */     {
/*  562 */       char[] originatingFileName = problem.getOriginatingFileName();
/*  563 */       String fileName = 
/*  564 */         originatingFileName == null ? 
/*  565 */         this.main.bind("requestor.noFileNameSpecified") : 
/*  566 */         new String(originatingFileName);
/*  567 */       if ((this.tagBits & 0x2) != 0) {
/*  568 */         String result = fileName + 
/*  569 */           ":" + 
/*  570 */           problem.getSourceLineNumber() + 
/*  571 */           ": " + (
/*  572 */           problem.isError() ? this.main.bind("output.emacs.error") : this.main.bind("output.emacs.warning")) + 
/*  573 */           ": " + 
/*  574 */           problem.getMessage();
/*  575 */         printlnErr(result);
/*  576 */         String errorReportSource = errorReportSource(problem, null, this.tagBits);
/*  577 */         printlnErr(errorReportSource);
/*      */       } else {
/*  579 */         if (localErrorCount == 0) {
/*  580 */           printlnErr("----------");
/*      */         }
/*  582 */         printErr(problem.isError() ? 
/*  583 */           this.main.bind(
/*  584 */           "requestor.error", 
/*  585 */           Integer.toString(globalErrorCount), 
/*  586 */           new String(fileName)) : 
/*  587 */           this.main.bind(
/*  588 */           "requestor.warning", 
/*  589 */           Integer.toString(globalErrorCount), 
/*  590 */           new String(fileName)));
/*  591 */         String errorReportSource = errorReportSource(problem, null, 0);
/*  592 */         printlnErr(errorReportSource);
/*  593 */         printlnErr(problem.getMessage());
/*  594 */         printlnErr("----------");
/*      */       }
/*      */     }
/*      */ 
/*      */     public void loggingExtraProblems(Main currentMain) {
/*  599 */       ArrayList problems = currentMain.extraProblems;
/*  600 */       int count = problems.size();
/*  601 */       int localErrorCount = 0;
/*  602 */       int localProblemCount = 0;
/*  603 */       if (count != 0) {
/*  604 */         int errors = 0;
/*  605 */         int warnings = 0;
/*  606 */         for (int i = 0; i < count; i++) {
/*  607 */           CategorizedProblem problem = (CategorizedProblem)problems.get(i);
/*  608 */           if (problem != null) {
/*  609 */             currentMain.globalProblemsCount += 1;
/*  610 */             logExtraProblem(problem, localProblemCount, currentMain.globalProblemsCount);
/*  611 */             localProblemCount++;
/*  612 */             if (problem.isError()) {
/*  613 */               localErrorCount++;
/*  614 */               errors++;
/*  615 */               currentMain.globalErrorsCount += 1;
/*  616 */             } else if (problem.isWarning()) {
/*  617 */               currentMain.globalWarningsCount += 1;
/*  618 */               warnings++;
/*      */             }
/*      */           }
/*      */         }
/*  622 */         if (((this.tagBits & 0x1) != 0) && 
/*  623 */           (errors + warnings != 0)) {
/*  624 */           startLoggingExtraProblems(count);
/*  625 */           for (int i = 0; i < count; i++) {
/*  626 */             CategorizedProblem problem = (CategorizedProblem)problems.get(i);
/*  627 */             if ((problem == null) || 
/*  628 */               (problem.getID() == 536871362)) continue;
/*  629 */             logXmlExtraProblem(problem, localProblemCount, currentMain.globalProblemsCount);
/*      */           }
/*      */ 
/*  633 */           endLoggingExtraProblems();
/*      */         }
/*      */       }
/*      */     }
/*      */ 
/*      */     public void logIncorrectVMVersionForAnnotationProcessing()
/*      */     {
/*  640 */       if ((this.tagBits & 0x1) != 0) {
/*  641 */         this.parameters.put("message", this.main.bind("configure.incorrectVMVersionforAPT"));
/*  642 */         printTag("error", this.parameters, true, true);
/*      */       }
/*  644 */       printlnErr(this.main.bind("configure.incorrectVMVersionforAPT"));
/*      */     }
/*      */ 
/*      */     public void logNoClassFileCreated(String outputDir, String relativeFileName, IOException e)
/*      */     {
/*  651 */       if ((this.tagBits & 0x1) != 0) {
/*  652 */         this.parameters.put("message", this.main.bind("output.noClassFileCreated", 
/*  653 */           new String[] { 
/*  654 */           outputDir, 
/*  655 */           relativeFileName, 
/*  656 */           e.getMessage() }));
/*      */ 
/*  658 */         printTag("error", this.parameters, true, true);
/*      */       }
/*  660 */       printlnErr(this.main.bind("output.noClassFileCreated", 
/*  661 */         new String[] { 
/*  662 */         outputDir, 
/*  663 */         relativeFileName, 
/*  664 */         e.getMessage() }));
/*      */     }
/*      */ 
/*      */     public void logNumberOfClassFilesGenerated(int exportedClassFilesCounter)
/*      */     {
/*  672 */       if ((this.tagBits & 0x1) != 0) {
/*  673 */         this.parameters.put("value", new Integer(exportedClassFilesCounter));
/*  674 */         printTag("number_of_classfiles", this.parameters, true, true);
/*      */       }
/*  676 */       if (exportedClassFilesCounter == 1)
/*  677 */         printlnOut(this.main.bind("compile.oneClassFileGenerated"));
/*      */       else
/*  679 */         printlnOut(this.main.bind("compile.severalClassFilesGenerated", 
/*  680 */           String.valueOf(exportedClassFilesCounter)));
/*      */     }
/*      */ 
/*      */     public void logOptions(Map options)
/*      */     {
/*  688 */       if ((this.tagBits & 0x1) != 0) {
/*  689 */         printTag("options", null, true, false);
/*  690 */         Set entriesSet = options.entrySet();
/*  691 */         Object[] entries = entriesSet.toArray();
/*  692 */         Arrays.sort(entries, new Main.1(this));
/*      */ 
/*  699 */         int i = 0; for (int max = entries.length; i < max; i++) {
/*  700 */           Map.Entry entry = (Map.Entry)entries[i];
/*  701 */           String key = (String)entry.getKey();
/*  702 */           this.parameters.put("key", key);
/*  703 */           this.parameters.put("value", entry.getValue());
/*  704 */           printTag("option", this.parameters, true, true);
/*      */         }
/*  706 */         endTag("options");
/*      */       }
/*      */     }
/*      */ 
/*      */     public void logPendingError(String error)
/*      */     {
/*  714 */       if ((this.tagBits & 0x1) != 0) {
/*  715 */         this.parameters.put("message", error);
/*  716 */         printTag("error", this.parameters, true, true);
/*      */       }
/*  718 */       printlnErr(error);
/*      */     }
/*      */ 
/*      */     private void logProblem(CategorizedProblem problem, int localErrorCount, int globalErrorCount, char[] unitSource)
/*      */     {
/*  723 */       if ((this.tagBits & 0x2) != 0) {
/*  724 */         String result = new String(problem.getOriginatingFileName()) + 
/*  725 */           ":" + 
/*  726 */           problem.getSourceLineNumber() + 
/*  727 */           ": " + (
/*  728 */           problem.isError() ? this.main.bind("output.emacs.error") : this.main.bind("output.emacs.warning")) + 
/*  729 */           ": " + 
/*  730 */           problem.getMessage();
/*  731 */         printlnErr(result);
/*  732 */         String errorReportSource = errorReportSource(problem, unitSource, this.tagBits);
/*  733 */         if (errorReportSource.length() != 0) printlnErr(errorReportSource); 
/*      */       }
/*      */       else {
/*  735 */         if (localErrorCount == 0) {
/*  736 */           printlnErr("----------");
/*      */         }
/*  738 */         printErr(problem.isError() ? 
/*  739 */           this.main.bind(
/*  740 */           "requestor.error", 
/*  741 */           Integer.toString(globalErrorCount), 
/*  742 */           new String(problem.getOriginatingFileName())) : 
/*  743 */           this.main.bind(
/*  744 */           "requestor.warning", 
/*  745 */           Integer.toString(globalErrorCount), 
/*  746 */           new String(problem.getOriginatingFileName())));
/*      */         try {
/*  748 */           String errorReportSource = errorReportSource(problem, unitSource, 0);
/*  749 */           printlnErr(errorReportSource);
/*  750 */           printlnErr(problem.getMessage());
/*      */         } catch (Exception localException) {
/*  752 */           printlnErr(this.main.bind(
/*  753 */             "requestor.notRetrieveErrorMessage", problem.toString()));
/*      */         }
/*  755 */         printlnErr("----------");
/*      */       }
/*      */     }
/*      */ 
/*      */     public int logProblems(CategorizedProblem[] problems, char[] unitSource, Main currentMain) {
/*  760 */       int count = problems.length;
/*  761 */       int localErrorCount = 0;
/*  762 */       int localProblemCount = 0;
/*  763 */       if (count != 0) {
/*  764 */         int errors = 0;
/*  765 */         int warnings = 0;
/*  766 */         int tasks = 0;
/*  767 */         for (int i = 0; i < count; i++) {
/*  768 */           CategorizedProblem problem = problems[i];
/*  769 */           if (problem != null) {
/*  770 */             currentMain.globalProblemsCount += 1;
/*  771 */             logProblem(problem, localProblemCount, currentMain.globalProblemsCount, unitSource);
/*  772 */             localProblemCount++;
/*  773 */             if (problem.isError()) {
/*  774 */               localErrorCount++;
/*  775 */               errors++;
/*  776 */               currentMain.globalErrorsCount += 1;
/*  777 */             } else if (problem.getID() == 536871362) {
/*  778 */               currentMain.globalTasksCount += 1;
/*  779 */               tasks++;
/*      */             } else {
/*  781 */               currentMain.globalWarningsCount += 1;
/*  782 */               warnings++;
/*      */             }
/*      */           }
/*      */         }
/*  786 */         if ((this.tagBits & 0x1) != 0) {
/*  787 */           if (errors + warnings != 0) {
/*  788 */             startLoggingProblems(errors, warnings);
/*  789 */             for (int i = 0; i < count; i++) {
/*  790 */               CategorizedProblem problem = problems[i];
/*  791 */               if ((problem == null) || 
/*  792 */                 (problem.getID() == 536871362)) continue;
/*  793 */               logXmlProblem(problem, unitSource);
/*      */             }
/*      */ 
/*  797 */             endLoggingProblems();
/*      */           }
/*  799 */           if (tasks != 0) {
/*  800 */             startLoggingTasks(tasks);
/*  801 */             for (int i = 0; i < count; i++) {
/*  802 */               CategorizedProblem problem = problems[i];
/*  803 */               if ((problem == null) || 
/*  804 */                 (problem.getID() != 536871362)) continue;
/*  805 */               logXmlTask(problem, unitSource);
/*      */             }
/*      */ 
/*  809 */             endLoggingTasks();
/*      */           }
/*      */         }
/*      */       }
/*  813 */       return localErrorCount;
/*      */     }
/*      */ 
/*      */     public void logProblemsSummary(int globalProblemsCount, int globalErrorsCount, int globalWarningsCount, int globalTasksCount)
/*      */     {
/*  823 */       if ((this.tagBits & 0x1) != 0)
/*      */       {
/*  825 */         this.parameters.put("problems", new Integer(globalProblemsCount));
/*  826 */         this.parameters.put("errors", new Integer(globalErrorsCount));
/*  827 */         this.parameters.put("warnings", new Integer(globalWarningsCount));
/*  828 */         this.parameters.put("tasks", new Integer(globalTasksCount));
/*  829 */         printTag("problem_summary", this.parameters, true, true);
/*      */       }
/*  831 */       if (globalProblemsCount == 1) {
/*  832 */         String message = null;
/*  833 */         if (globalErrorsCount == 1)
/*  834 */           message = this.main.bind("compile.oneError");
/*      */         else {
/*  836 */           message = this.main.bind("compile.oneWarning");
/*      */         }
/*  838 */         printErr(this.main.bind("compile.oneProblem", message));
/*      */       } else {
/*  840 */         String errorMessage = null;
/*  841 */         String warningMessage = null;
/*  842 */         if (globalErrorsCount > 0) {
/*  843 */           if (globalErrorsCount == 1)
/*  844 */             errorMessage = this.main.bind("compile.oneError");
/*      */           else {
/*  846 */             errorMessage = this.main.bind("compile.severalErrors", String.valueOf(globalErrorsCount));
/*      */           }
/*      */         }
/*  849 */         int warningsNumber = globalWarningsCount + globalTasksCount;
/*  850 */         if (warningsNumber > 0) {
/*  851 */           if (warningsNumber == 1)
/*  852 */             warningMessage = this.main.bind("compile.oneWarning");
/*      */           else {
/*  854 */             warningMessage = this.main.bind("compile.severalWarnings", String.valueOf(warningsNumber));
/*      */           }
/*      */         }
/*  857 */         if ((errorMessage == null) || (warningMessage == null)) {
/*  858 */           if (errorMessage == null)
/*  859 */             printErr(this.main.bind(
/*  860 */               "compile.severalProblemsErrorsOrWarnings", 
/*  861 */               String.valueOf(globalProblemsCount), 
/*  862 */               warningMessage));
/*      */           else
/*  864 */             printErr(this.main.bind(
/*  865 */               "compile.severalProblemsErrorsOrWarnings", 
/*  866 */               String.valueOf(globalProblemsCount), 
/*  867 */               errorMessage));
/*      */         }
/*      */         else {
/*  870 */           printErr(this.main.bind(
/*  871 */             "compile.severalProblemsErrorsAndWarnings", 
/*  872 */             new String[] { 
/*  873 */             String.valueOf(globalProblemsCount), 
/*  874 */             errorMessage, 
/*  875 */             warningMessage }));
/*      */         }
/*      */       }
/*      */ 
/*  879 */       if ((this.tagBits & 0x2) != 0)
/*  880 */         printlnErr();
/*      */     }
/*      */ 
/*      */     public void logProgress()
/*      */     {
/*  888 */       printOut('.');
/*      */     }
/*      */ 
/*      */     public void logRepetition(int i, int repetitions)
/*      */     {
/*  898 */       printlnOut(this.main.bind("compile.repetition", 
/*  899 */         String.valueOf(i + 1), String.valueOf(repetitions)));
/*      */     }
/*      */ 
/*      */     public void logTiming(CompilerStats compilerStats)
/*      */     {
/*  905 */       long time = compilerStats.elapsedTime();
/*  906 */       long lineCount = compilerStats.lineCount;
/*  907 */       if ((this.tagBits & 0x1) != 0) {
/*  908 */         this.parameters.put("value", new Long(time));
/*  909 */         printTag("time", this.parameters, true, true);
/*  910 */         this.parameters.put("value", new Long(lineCount));
/*  911 */         printTag("number_of_lines", this.parameters, true, true);
/*      */       }
/*  913 */       if (lineCount != 0L) {
/*  914 */         printlnOut(
/*  915 */           this.main.bind("compile.instantTime", 
/*  916 */           new String[] { 
/*  917 */           String.valueOf(lineCount), 
/*  918 */           String.valueOf(time), 
/*  919 */           String.valueOf((int)(lineCount * 10000.0D / time) / 10.0D) }));
/*      */       }
/*      */       else {
/*  922 */         printlnOut(
/*  923 */           this.main.bind("compile.totalTime", 
/*  924 */           new String[] { 
/*  925 */           String.valueOf(time) }));
/*      */       }
/*      */ 
/*  928 */       if ((this.main.timing & 0x2) != 0)
/*  929 */         printlnOut(
/*  930 */           this.main.bind("compile.detailedTime", 
/*  931 */           new String[] { 
/*  932 */           String.valueOf(compilerStats.parseTime), 
/*  933 */           String.valueOf((int)(compilerStats.parseTime * 1000.0D / time) / 10.0D), 
/*  934 */           String.valueOf(compilerStats.resolveTime), 
/*  935 */           String.valueOf((int)(compilerStats.resolveTime * 1000.0D / time) / 10.0D), 
/*  936 */           String.valueOf(compilerStats.analyzeTime), 
/*  937 */           String.valueOf((int)(compilerStats.analyzeTime * 1000.0D / time) / 10.0D), 
/*  938 */           String.valueOf(compilerStats.generateTime), 
/*  939 */           String.valueOf((int)(compilerStats.generateTime * 1000.0D / time) / 10.0D) }));
/*      */     }
/*      */ 
/*      */     public void logUsage(String usage)
/*      */     {
/*  949 */       printlnOut(usage);
/*      */     }
/*      */ 
/*      */     public void logVersion(boolean printToOut)
/*      */     {
/*  956 */       if ((this.log != null) && ((this.tagBits & 0x1) == 0)) {
/*  957 */         String version = this.main.bind("misc.version", 
/*  958 */           new String[] { 
/*  959 */           this.main.bind("compiler.name"), 
/*  960 */           this.main.bind("compiler.version"), 
/*  961 */           this.main.bind("compiler.copyright") });
/*      */ 
/*  964 */         this.log.println("# " + version);
/*  965 */         if (printToOut) {
/*  966 */           this.out.println(version);
/*  967 */           this.out.flush();
/*      */         }
/*  969 */       } else if (printToOut) {
/*  970 */         String version = this.main.bind("misc.version", 
/*  971 */           new String[] { 
/*  972 */           this.main.bind("compiler.name"), 
/*  973 */           this.main.bind("compiler.version"), 
/*  974 */           this.main.bind("compiler.copyright") });
/*      */ 
/*  977 */         this.out.println(version);
/*  978 */         this.out.flush();
/*      */       }
/*      */     }
/*      */ 
/*      */     public void logWrongJDK()
/*      */     {
/*  986 */       if ((this.tagBits & 0x1) != 0) {
/*  987 */         this.parameters.put("message", this.main.bind("configure.requiresJDK1.2orAbove"));
/*  988 */         printTag("ERROR", this.parameters, true, true);
/*      */       }
/*  990 */       printlnErr(this.main.bind("configure.requiresJDK1.2orAbove"));
/*      */     }
/*      */ 
/*      */     private void logXmlExtraProblem(CategorizedProblem problem, int globalErrorCount, int localErrorCount) {
/*  994 */       int sourceStart = problem.getSourceStart();
/*  995 */       int sourceEnd = problem.getSourceEnd();
/*  996 */       boolean isError = problem.isError();
/*  997 */       this.parameters.put("severity", isError ? "ERROR" : "WARNING");
/*  998 */       this.parameters.put("line", new Integer(problem.getSourceLineNumber()));
/*  999 */       this.parameters.put("charStart", new Integer(sourceStart));
/* 1000 */       this.parameters.put("charEnd", new Integer(sourceEnd));
/* 1001 */       printTag("extra_problem", this.parameters, true, false);
/* 1002 */       this.parameters.put("value", problem.getMessage());
/* 1003 */       printTag("message", this.parameters, true, true);
/* 1004 */       extractContext(problem, null);
/* 1005 */       endTag("extra_problem");
/*      */     }
/*      */ 
/*      */     private void logXmlProblem(CategorizedProblem problem, char[] unitSource)
/*      */     {
/* 1014 */       int sourceStart = problem.getSourceStart();
/* 1015 */       int sourceEnd = problem.getSourceEnd();
/* 1016 */       int id = problem.getID();
/* 1017 */       this.parameters.put("id", getFieldName(id));
/* 1018 */       this.parameters.put("problemID", new Integer(id));
/* 1019 */       boolean isError = problem.isError();
/* 1020 */       int severity = isError ? 1 : 0;
/* 1021 */       this.parameters.put("severity", isError ? "ERROR" : "WARNING");
/* 1022 */       this.parameters.put("line", new Integer(problem.getSourceLineNumber()));
/* 1023 */       this.parameters.put("charStart", new Integer(sourceStart));
/* 1024 */       this.parameters.put("charEnd", new Integer(sourceEnd));
/* 1025 */       String problemOptionKey = getProblemOptionKey(id);
/* 1026 */       if (problemOptionKey != null) {
/* 1027 */         this.parameters.put("optionKey", problemOptionKey);
/*      */       }
/* 1029 */       int categoryID = ProblemReporter.getProblemCategory(severity, id);
/* 1030 */       this.parameters.put("categoryID", new Integer(categoryID));
/* 1031 */       printTag("problem", this.parameters, true, false);
/* 1032 */       this.parameters.put("value", problem.getMessage());
/* 1033 */       printTag("message", this.parameters, true, true);
/* 1034 */       extractContext(problem, unitSource);
/* 1035 */       String[] arguments = problem.getArguments();
/* 1036 */       int length = arguments.length;
/* 1037 */       if (length != 0) {
/* 1038 */         printTag("arguments", null, true, false);
/* 1039 */         for (int i = 0; i < length; i++) {
/* 1040 */           this.parameters.put("value", arguments[i]);
/* 1041 */           printTag("argument", this.parameters, true, true);
/*      */         }
/* 1043 */         endTag("arguments");
/*      */       }
/* 1045 */       endTag("problem");
/*      */     }
/*      */ 
/*      */     private void logXmlTask(CategorizedProblem problem, char[] unitSource)
/*      */     {
/* 1054 */       this.parameters.put("line", new Integer(problem.getSourceLineNumber()));
/* 1055 */       this.parameters.put("charStart", new Integer(problem.getSourceStart()));
/* 1056 */       this.parameters.put("charEnd", new Integer(problem.getSourceEnd()));
/* 1057 */       String problemOptionKey = getProblemOptionKey(problem.getID());
/* 1058 */       if (problemOptionKey != null) {
/* 1059 */         this.parameters.put("optionKey", problemOptionKey);
/*      */       }
/* 1061 */       printTag("task", this.parameters, true, false);
/* 1062 */       this.parameters.put("value", problem.getMessage());
/* 1063 */       printTag("message", this.parameters, true, true);
/* 1064 */       extractContext(problem, unitSource);
/* 1065 */       endTag("task");
/*      */     }
/*      */ 
/*      */     private void printErr(String s) {
/* 1069 */       this.err.print(s);
/* 1070 */       if (((this.tagBits & 0x1) == 0) && (this.log != null))
/* 1071 */         this.log.print(s);
/*      */     }
/*      */ 
/*      */     private void printlnErr()
/*      */     {
/* 1076 */       this.err.println();
/* 1077 */       if (((this.tagBits & 0x1) == 0) && (this.log != null))
/* 1078 */         this.log.println();
/*      */     }
/*      */ 
/*      */     private void printlnErr(String s)
/*      */     {
/* 1083 */       this.err.println(s);
/* 1084 */       if (((this.tagBits & 0x1) == 0) && (this.log != null))
/* 1085 */         this.log.println(s);
/*      */     }
/*      */ 
/*      */     private void printlnOut(String s)
/*      */     {
/* 1090 */       this.out.println(s);
/* 1091 */       if (((this.tagBits & 0x1) == 0) && (this.log != null))
/* 1092 */         this.log.println(s);
/*      */     }
/*      */ 
/*      */     public void printNewLine()
/*      */     {
/* 1100 */       this.out.println();
/*      */     }
/*      */ 
/*      */     private void printOut(char c) {
/* 1104 */       this.out.print(c);
/*      */     }
/*      */ 
/*      */     public void printStats() {
/* 1108 */       boolean isTimed = (this.main.timing & 0x1) != 0;
/* 1109 */       if ((this.tagBits & 0x1) != 0) {
/* 1110 */         printTag("stats", null, true, false);
/*      */       }
/* 1112 */       if (isTimed) {
/* 1113 */         CompilerStats compilerStats = this.main.batchCompiler.stats;
/* 1114 */         compilerStats.startTime = this.main.startTime;
/* 1115 */         compilerStats.endTime = System.currentTimeMillis();
/* 1116 */         logTiming(compilerStats);
/*      */       }
/* 1118 */       if (this.main.globalProblemsCount > 0) {
/* 1119 */         logProblemsSummary(this.main.globalProblemsCount, this.main.globalErrorsCount, this.main.globalWarningsCount, this.main.globalTasksCount);
/*      */       }
/* 1121 */       if ((this.main.exportedClassFilesCounter != 0) && (
/* 1122 */         (this.main.showProgress) || (isTimed) || (this.main.verbose))) {
/* 1123 */         logNumberOfClassFilesGenerated(this.main.exportedClassFilesCounter);
/*      */       }
/* 1125 */       if ((this.tagBits & 0x1) != 0)
/* 1126 */         endTag("stats");
/*      */     }
/*      */ 
/*      */     private void printTag(String name, HashMap params, boolean insertNewLine, boolean closeTag)
/*      */     {
/* 1131 */       if (this.log != null) {
/* 1132 */         ((GenericXMLWriter)this.log).printTag(name, this.parameters, true, insertNewLine, closeTag);
/*      */       }
/* 1134 */       this.parameters.clear();
/*      */     }
/*      */ 
/*      */     public void setEmacs() {
/* 1138 */       this.tagBits |= 2;
/*      */     }
/*      */     public void setLog(String logFileName) {
/* 1141 */       Date date = new Date();
/* 1142 */       DateFormat dateFormat = DateFormat.getDateTimeInstance(3, 1, Locale.getDefault());
/*      */       try {
/* 1144 */         int index = logFileName.lastIndexOf('.');
/* 1145 */         if (index != -1) {
/* 1146 */           if (logFileName.substring(index).toLowerCase().equals(".xml")) {
/* 1147 */             this.log = new GenericXMLWriter(new OutputStreamWriter(new FileOutputStream(logFileName, false), "UTF-8"), Util.LINE_SEPARATOR, true);
/* 1148 */             this.tagBits |= 1;
/*      */ 
/* 1150 */             this.log.println("<!-- " + dateFormat.format(date) + " -->");
/* 1151 */             this.log.println("<!DOCTYPE compiler PUBLIC \"-//Eclipse.org//DTD Eclipse JDT 3.2.003 Compiler//EN\" \"http://www.eclipse.org/jdt/core/compiler_32_003.dtd\">");
/* 1152 */             this.parameters.put("name", this.main.bind("compiler.name"));
/* 1153 */             this.parameters.put("version", this.main.bind("compiler.version"));
/* 1154 */             this.parameters.put("copyright", this.main.bind("compiler.copyright"));
/* 1155 */             printTag("compiler", this.parameters, true, false);
/*      */           } else {
/* 1157 */             this.log = new PrintWriter(new FileOutputStream(logFileName, false));
/* 1158 */             this.log.println("# " + dateFormat.format(date));
/*      */           }
/*      */         } else {
/* 1161 */           this.log = new PrintWriter(new FileOutputStream(logFileName, false));
/* 1162 */           this.log.println("# " + dateFormat.format(date));
/*      */         }
/*      */       } catch (FileNotFoundException localFileNotFoundException) {
/* 1165 */         throw new IllegalArgumentException(this.main.bind("configure.cannotOpenLog", logFileName));
/*      */       } catch (UnsupportedEncodingException localUnsupportedEncodingException) {
/* 1167 */         throw new IllegalArgumentException(this.main.bind("configure.cannotOpenLogInvalidEncoding", logFileName));
/*      */       }
/*      */     }
/*      */ 
/*      */     private void startLoggingExtraProblems(int count) {
/* 1171 */       this.parameters.put("problems", new Integer(count));
/* 1172 */       printTag("extra_problems", this.parameters, true, false);
/*      */     }
/*      */ 
/*      */     private void startLoggingProblems(int errors, int warnings)
/*      */     {
/* 1180 */       this.parameters.put("problems", new Integer(errors + warnings));
/* 1181 */       this.parameters.put("errors", new Integer(errors));
/* 1182 */       this.parameters.put("warnings", new Integer(warnings));
/* 1183 */       printTag("problems", this.parameters, true, false);
/*      */     }
/*      */ 
/*      */     public void startLoggingSource(CompilationResult compilationResult) {
/* 1187 */       if ((this.tagBits & 0x1) != 0) {
/* 1188 */         ICompilationUnit compilationUnit = compilationResult.compilationUnit;
/* 1189 */         if (compilationUnit != null) {
/* 1190 */           char[] fileName = compilationUnit.getFileName();
/* 1191 */           File f = new File(new String(fileName));
/* 1192 */           if (fileName != null) {
/* 1193 */             this.parameters.put("path", f.getAbsolutePath());
/*      */           }
/* 1195 */           char[][] packageName = compilationResult.packageName;
/* 1196 */           if (packageName != null) {
/* 1197 */             this.parameters.put(
/* 1198 */               "package", 
/* 1199 */               new String(CharOperation.concatWith(packageName, File.separatorChar)));
/*      */           }
/* 1201 */           CompilationUnit unit = (CompilationUnit)compilationUnit;
/* 1202 */           String destinationPath = unit.destinationPath;
/* 1203 */           if (destinationPath == null) {
/* 1204 */             destinationPath = this.main.destinationPath;
/*      */           }
/* 1206 */           if ((destinationPath != null) && (destinationPath != "none")) {
/* 1207 */             if (File.separatorChar == '/')
/* 1208 */               this.parameters.put("output", destinationPath);
/*      */             else {
/* 1210 */               this.parameters.put("output", destinationPath.replace('/', File.separatorChar));
/*      */             }
/*      */           }
/*      */         }
/* 1214 */         printTag("source", this.parameters, true, false);
/*      */       }
/*      */     }
/*      */ 
/*      */     public void startLoggingSources() {
/* 1219 */       if ((this.tagBits & 0x1) != 0)
/* 1220 */         printTag("sources", null, true, false);
/*      */     }
/*      */ 
/*      */     public void startLoggingTasks(int tasks)
/*      */     {
/* 1225 */       if ((this.tagBits & 0x1) != 0) {
/* 1226 */         this.parameters.put("tasks", new Integer(tasks));
/* 1227 */         printTag("tasks", this.parameters, true, false);
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   public static class ResourceBundleFactory
/*      */   {
/* 1236 */     private static HashMap Cache = new HashMap();
/*      */ 
/* 1238 */     public static synchronized ResourceBundle getBundle(Locale locale) { ResourceBundle bundle = (ResourceBundle)Cache.get(locale);
/* 1239 */       if (bundle == null) {
/* 1240 */         bundle = ResourceBundle.getBundle("org.eclipse.jdt.internal.compiler.batch.messages", locale);
/* 1241 */         Cache.put(locale, bundle);
/*      */       }
/* 1243 */       return bundle;
/*      */     }
/*      */   }
/*      */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\ecj-3.5.2.jar
 * Qualified Name:     org.eclipse.jdt.internal.compiler.batch.Main
 * JD-Core Version:    0.6.0
 */