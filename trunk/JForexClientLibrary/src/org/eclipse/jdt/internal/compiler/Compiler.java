/*     */ package org.eclipse.jdt.internal.compiler;
/*     */ 
/*     */ import java.io.PrintWriter;
/*     */ import java.util.Map;
/*     */ import org.eclipse.jdt.core.compiler.CategorizedProblem;
/*     */ import org.eclipse.jdt.core.compiler.CompilationProgress;
/*     */ import org.eclipse.jdt.internal.compiler.ast.CompilationUnitDeclaration;
/*     */ import org.eclipse.jdt.internal.compiler.ast.ImportReference;
/*     */ import org.eclipse.jdt.internal.compiler.env.AccessRestriction;
/*     */ import org.eclipse.jdt.internal.compiler.env.IBinaryType;
/*     */ import org.eclipse.jdt.internal.compiler.env.ICompilationUnit;
/*     */ import org.eclipse.jdt.internal.compiler.env.INameEnvironment;
/*     */ import org.eclipse.jdt.internal.compiler.env.ISourceType;
/*     */ import org.eclipse.jdt.internal.compiler.impl.CompilerOptions;
/*     */ import org.eclipse.jdt.internal.compiler.impl.CompilerStats;
/*     */ import org.eclipse.jdt.internal.compiler.impl.ITypeRequestor;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.CompilationUnitScope;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.LookupEnvironment;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.PackageBinding;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.ReferenceBinding;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.SourceTypeCollisionException;
/*     */ import org.eclipse.jdt.internal.compiler.parser.Parser;
/*     */ import org.eclipse.jdt.internal.compiler.problem.AbortCompilation;
/*     */ import org.eclipse.jdt.internal.compiler.problem.AbortCompilationUnit;
/*     */ import org.eclipse.jdt.internal.compiler.problem.DefaultProblem;
/*     */ import org.eclipse.jdt.internal.compiler.problem.ProblemReporter;
/*     */ import org.eclipse.jdt.internal.compiler.problem.ProblemSeverities;
/*     */ import org.eclipse.jdt.internal.compiler.util.Messages;
/*     */ import org.eclipse.jdt.internal.compiler.util.Util;
/*     */ 
/*     */ public class Compiler
/*     */   implements ITypeRequestor, ProblemSeverities
/*     */ {
/*     */   public Parser parser;
/*     */   public ICompilerRequestor requestor;
/*     */   public CompilerOptions options;
/*     */   public ProblemReporter problemReporter;
/*     */   protected PrintWriter out;
/*     */   public CompilerStats stats;
/*     */   public CompilationProgress progress;
/*  33 */   public int remainingIterations = 1;
/*     */   public CompilationUnitDeclaration[] unitsToProcess;
/*     */   public int totalUnits;
/*     */   public LookupEnvironment lookupEnvironment;
/*  44 */   public static boolean DEBUG = false;
/*  45 */   public int parseThreshold = -1;
/*     */   public AbstractAnnotationProcessorManager annotationProcessorManager;
/*  48 */   public int annotationProcessorStartIndex = 0;
/*     */   public ReferenceBinding[] referenceBindings;
/*  50 */   public boolean useSingleThread = true;
/*     */ 
/*  60 */   public static IDebugRequestor DebugRequestor = null;
/*     */ 
/*     */   /** @deprecated */
/*     */   public Compiler(INameEnvironment environment, IErrorHandlingPolicy policy, Map settings, ICompilerRequestor requestor, IProblemFactory problemFactory)
/*     */   {
/* 106 */     this(environment, policy, new CompilerOptions(settings), requestor, problemFactory, null, null);
/*     */   }
/*     */ 
/*     */   /** @deprecated */
/*     */   public Compiler(INameEnvironment environment, IErrorHandlingPolicy policy, Map settings, ICompilerRequestor requestor, IProblemFactory problemFactory, boolean parseLiteralExpressionsAsConstants)
/*     */   {
/* 158 */     this(environment, policy, new CompilerOptions(settings, parseLiteralExpressionsAsConstants), requestor, problemFactory, null, null);
/*     */   }
/*     */ 
/*     */   public Compiler(INameEnvironment environment, IErrorHandlingPolicy policy, CompilerOptions options, ICompilerRequestor requestor, IProblemFactory problemFactory)
/*     */   {
/* 203 */     this(environment, policy, options, requestor, problemFactory, null, null);
/*     */   }
/*     */ 
/*     */   /** @deprecated */
/*     */   public Compiler(INameEnvironment environment, IErrorHandlingPolicy policy, CompilerOptions options, ICompilerRequestor requestor, IProblemFactory problemFactory, PrintWriter out)
/*     */   {
/* 250 */     this(environment, policy, options, requestor, problemFactory, out, null);
/*     */   }
/*     */ 
/*     */   public Compiler(INameEnvironment environment, IErrorHandlingPolicy policy, CompilerOptions options, ICompilerRequestor requestor, IProblemFactory problemFactory, PrintWriter out, CompilationProgress progress)
/*     */   {
/* 262 */     this.options = options;
/* 263 */     this.progress = progress;
/*     */ 
/* 266 */     if (DebugRequestor == null)
/* 267 */       this.requestor = requestor;
/*     */     else
/* 269 */       this.requestor = new ICompilerRequestor(requestor) { private final ICompilerRequestor val$requestor;
/*     */ 
/* 271 */         public void acceptResult(CompilationResult result) { if (Compiler.DebugRequestor.isActive()) {
/* 272 */             Compiler.DebugRequestor.acceptDebugResult(result);
/*     */           }
/* 274 */           this.val$requestor.acceptResult(result);
/*     */         }
/*     */       };
/* 278 */     this.problemReporter = new ProblemReporter(policy, this.options, problemFactory);
/* 279 */     this.lookupEnvironment = new LookupEnvironment(this, this.options, this.problemReporter, environment);
/* 280 */     this.out = (out == null ? new PrintWriter(System.out, true) : out);
/* 281 */     this.stats = new CompilerStats();
/* 282 */     initializeParser();
/*     */   }
/*     */ 
/*     */   public void accept(IBinaryType binaryType, PackageBinding packageBinding, AccessRestriction accessRestriction)
/*     */   {
/* 289 */     if (this.options.verbose) {
/* 290 */       this.out.println(
/* 291 */         Messages.bind(Messages.compilation_loadBinary, new String(binaryType.getName())));
/*     */     }
/*     */ 
/* 295 */     this.lookupEnvironment.createBinaryTypeFrom(binaryType, packageBinding, accessRestriction);
/*     */   }
/*     */ 
/*     */   public void accept(ICompilationUnit sourceUnit, AccessRestriction accessRestriction)
/*     */   {
/* 304 */     CompilationResult unitResult = 
/* 305 */       new CompilationResult(sourceUnit, this.totalUnits, this.totalUnits, this.options.maxProblemsPerUnit);
/* 306 */     unitResult.checkSecondaryTypes = true;
/*     */     try {
/* 308 */       if (this.options.verbose) {
/* 309 */         String count = String.valueOf(this.totalUnits + 1);
/* 310 */         this.out.println(
/* 311 */           Messages.bind(Messages.compilation_request, 
/* 312 */           new String[] { 
/* 313 */           count, 
/* 314 */           count, 
/* 315 */           new String(sourceUnit.getFileName()) }));
/*     */       }
/*     */       CompilationUnitDeclaration parsedUnit;
/*     */       CompilationUnitDeclaration parsedUnit;
/* 320 */       if (this.totalUnits < this.parseThreshold)
/* 321 */         parsedUnit = this.parser.parse(sourceUnit, unitResult);
/*     */       else {
/* 323 */         parsedUnit = this.parser.dietParse(sourceUnit, unitResult);
/*     */       }
/* 325 */       parsedUnit.bits |= 1;
/*     */ 
/* 327 */       this.lookupEnvironment.buildTypeBindings(parsedUnit, accessRestriction);
/* 328 */       addCompilationUnit(sourceUnit, parsedUnit);
/*     */ 
/* 331 */       this.lookupEnvironment.completeTypeBindings(parsedUnit);
/*     */     }
/*     */     catch (AbortCompilationUnit e)
/*     */     {
/* 335 */       if (unitResult.compilationUnit == sourceUnit)
/* 336 */         this.requestor.acceptResult(unitResult.tagAsAccepted());
/*     */       else
/* 338 */         throw e;
/*     */     }
/*     */   }
/*     */ 
/*     */   public void accept(ISourceType[] sourceTypes, PackageBinding packageBinding, AccessRestriction accessRestriction)
/*     */   {
/* 347 */     this.problemReporter.abortDueToInternalError(
/* 348 */       Messages.bind(Messages.abort_againstSourceModel, new String[] { String.valueOf(sourceTypes[0].getName()), String.valueOf(sourceTypes[0].getFileName()) }));
/*     */   }
/*     */ 
/*     */   protected synchronized void addCompilationUnit(ICompilationUnit sourceUnit, CompilationUnitDeclaration parsedUnit)
/*     */   {
/* 356 */     int size = this.unitsToProcess.length;
/* 357 */     if (this.totalUnits == size)
/*     */     {
/* 359 */       System.arraycopy(
/* 360 */         this.unitsToProcess, 
/* 361 */         0, 
/* 362 */         this.unitsToProcess = new CompilationUnitDeclaration[size * 2], 
/* 363 */         0, 
/* 364 */         this.totalUnits);
/* 365 */     }this.unitsToProcess[(this.totalUnits++)] = parsedUnit;
/*     */   }
/*     */ 
/*     */   protected void beginToCompile(ICompilationUnit[] sourceUnits)
/*     */   {
/* 373 */     int maxUnits = sourceUnits.length;
/* 374 */     this.totalUnits = 0;
/* 375 */     this.unitsToProcess = new CompilationUnitDeclaration[maxUnits];
/*     */ 
/* 377 */     internalBeginToCompile(sourceUnits, maxUnits);
/*     */   }
/*     */ 
/*     */   protected void reportProgress(String taskDecription)
/*     */   {
/* 384 */     if (this.progress != null) {
/* 385 */       if (this.progress.isCanceled())
/*     */       {
/* 388 */         throw new AbortCompilation(true, null);
/*     */       }
/* 390 */       this.progress.setTaskName(taskDecription);
/*     */     }
/*     */   }
/*     */ 
/*     */   protected void reportWorked(int workIncrement, int currentUnitIndex)
/*     */   {
/* 398 */     if (this.progress != null) {
/* 399 */       if (this.progress.isCanceled())
/*     */       {
/* 402 */         throw new AbortCompilation(true, null);
/*     */       }
/* 404 */       this.progress.worked(workIncrement, this.totalUnits * this.remainingIterations - currentUnitIndex - 1);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void compile(ICompilationUnit[] sourceUnits)
/*     */   {
/* 414 */     this.stats.startTime = System.currentTimeMillis();
/* 415 */     CompilationUnitDeclaration unit = null;
/* 416 */     ProcessTaskManager processingTask = null;
/*     */     try
/*     */     {
/* 419 */       reportProgress(Messages.compilation_beginningToCompile);
/*     */ 
/* 421 */       if (this.annotationProcessorManager == null) {
/* 422 */         beginToCompile(sourceUnits);
/*     */       } else {
/* 424 */         ICompilationUnit[] originalUnits = (ICompilationUnit[])sourceUnits.clone();
/*     */         try {
/* 426 */           beginToCompile(sourceUnits);
/*     */ 
/* 428 */           processAnnotations();
/* 429 */           if (!this.options.generateClassFiles)
/*     */           {
/* 524 */             if (processingTask != null) {
/* 525 */               processingTask.shutdown();
/* 526 */               processingTask = null;
/*     */             }
/* 528 */             reset();
/* 529 */             this.annotationProcessorStartIndex = 0;
/* 530 */             this.stats.endTime = System.currentTimeMillis();
/*     */ 
/* 431 */             return;
/*     */           }
/*     */         }
/*     */         catch (SourceTypeCollisionException e)
/*     */         {
/*     */           ICompilationUnit[] originalUnits;
/* 434 */           reset();
/*     */ 
/* 439 */           int originalLength = originalUnits.length;
/* 440 */           int newProcessedLength = e.newAnnotationProcessorUnits.length;
/* 441 */           ICompilationUnit[] combinedUnits = new ICompilationUnit[originalLength + newProcessedLength];
/* 442 */           System.arraycopy(originalUnits, 0, combinedUnits, 0, originalLength);
/* 443 */           System.arraycopy(e.newAnnotationProcessorUnits, 0, combinedUnits, originalLength, newProcessedLength);
/* 444 */           this.annotationProcessorStartIndex = originalLength;
/* 445 */           compile(combinedUnits);
/*     */ 
/* 524 */           if (processingTask != null) {
/* 525 */             processingTask.shutdown();
/* 526 */             processingTask = null;
/*     */           }
/* 528 */           reset();
/* 529 */           this.annotationProcessorStartIndex = 0;
/* 530 */           this.stats.endTime = System.currentTimeMillis();
/*     */ 
/* 446 */           return;
/*     */         }
/*     */       }
/*     */ 
/* 450 */       if (this.useSingleThread)
/*     */       {
/* 452 */         for (int i = 0; i < this.totalUnits; i++) {
/* 453 */           unit = this.unitsToProcess[i];
/* 454 */           reportProgress(Messages.bind(Messages.compilation_processing, new String(unit.getFileName())));
/*     */           try {
/* 456 */             if (this.options.verbose) {
/* 457 */               this.out.println(
/* 458 */                 Messages.bind(Messages.compilation_process, 
/* 459 */                 new String[] { 
/* 460 */                 String.valueOf(i + 1), 
/* 461 */                 String.valueOf(this.totalUnits), 
/* 462 */                 new String(this.unitsToProcess[i].getFileName()) }));
/*     */             }
/* 464 */             process(unit, i);
/*     */           }
/*     */           finally {
/* 467 */             unit.cleanUp();
/*     */           }
/* 469 */           this.unitsToProcess[i] = null;
/*     */ 
/* 471 */           reportWorked(1, i);
/* 472 */           this.stats.lineCount += unit.compilationResult.lineSeparatorPositions.length;
/* 473 */           long acceptStart = System.currentTimeMillis();
/* 474 */           this.requestor.acceptResult(unit.compilationResult.tagAsAccepted());
/* 475 */           this.stats.generateTime += System.currentTimeMillis() - acceptStart;
/* 476 */           if (this.options.verbose)
/* 477 */             this.out.println(
/* 478 */               Messages.bind(Messages.compilation_done, 
/* 479 */               new String[] { 
/* 480 */               String.valueOf(i + 1), 
/* 481 */               String.valueOf(this.totalUnits), 
/* 482 */               new String(unit.getFileName()) }));
/*     */         }
/*     */       }
/*     */       else {
/* 486 */         processingTask = new ProcessTaskManager(this);
/* 487 */         int acceptedCount = 0;
/*     */         while (true)
/*     */         {
/*     */           try
/*     */           {
/* 493 */             unit = processingTask.removeNextUnit();
/*     */           } catch (Error e) {
/* 495 */             unit = processingTask.unitToProcess;
/* 496 */             throw e;
/*     */           } catch (RuntimeException e) {
/* 498 */             unit = processingTask.unitToProcess;
/* 499 */             throw e;
/*     */           }
/* 501 */           if (unit == null) break;
/* 502 */           reportWorked(1, acceptedCount++);
/* 503 */           this.stats.lineCount += unit.compilationResult.lineSeparatorPositions.length;
/* 504 */           this.requestor.acceptResult(unit.compilationResult.tagAsAccepted());
/* 505 */           if (this.options.verbose)
/* 506 */             this.out.println(
/* 507 */               Messages.bind(Messages.compilation_done, 
/* 508 */               new String[] { 
/* 509 */               String.valueOf(acceptedCount), 
/* 510 */               String.valueOf(this.totalUnits), 
/* 511 */               new String(unit.getFileName()) }));
/*     */         }
/*     */       }
/*     */     }
/*     */     catch (AbortCompilation e) {
/* 516 */       handleInternalException(e, unit);
/*     */     } catch (Error e) {
/* 518 */       handleInternalException(e, unit, null);
/* 519 */       throw e;
/*     */     } catch (RuntimeException e) {
/* 521 */       handleInternalException(e, unit, null);
/* 522 */       throw e;
/*     */     } finally {
/* 524 */       if (processingTask != null) {
/* 525 */         processingTask.shutdown();
/* 526 */         processingTask = null;
/*     */       }
/* 528 */       reset();
/* 529 */       this.annotationProcessorStartIndex = 0;
/* 530 */       this.stats.endTime = System.currentTimeMillis();
/*     */     }
/* 524 */     if (processingTask != null) {
/* 525 */       processingTask.shutdown();
/* 526 */       processingTask = null;
/*     */     }
/* 528 */     reset();
/* 529 */     this.annotationProcessorStartIndex = 0;
/* 530 */     this.stats.endTime = System.currentTimeMillis();
/*     */ 
/* 532 */     if (this.options.verbose)
/* 533 */       if (this.totalUnits > 1)
/* 534 */         this.out.println(
/* 535 */           Messages.bind(Messages.compilation_units, String.valueOf(this.totalUnits)));
/*     */       else
/* 537 */         this.out.println(
/* 538 */           Messages.bind(Messages.compilation_unit, String.valueOf(this.totalUnits)));
/*     */   }
/*     */ 
/*     */   public synchronized CompilationUnitDeclaration getUnitToProcess(int next)
/*     */   {
/* 544 */     if (next < this.totalUnits) {
/* 545 */       CompilationUnitDeclaration unit = this.unitsToProcess[next];
/* 546 */       this.unitsToProcess[next] = null;
/* 547 */       return unit;
/*     */     }
/* 549 */     return null;
/*     */   }
/*     */ 
/*     */   public void setBinaryTypes(ReferenceBinding[] binaryTypes) {
/* 553 */     this.referenceBindings = binaryTypes;
/*     */   }
/*     */ 
/*     */   protected void handleInternalException(Throwable internalException, CompilationUnitDeclaration unit, CompilationResult result)
/*     */   {
/* 563 */     if ((result == null) && (unit != null)) {
/* 564 */       result = unit.compilationResult;
/*     */     }
/*     */ 
/* 567 */     if ((result == null) && (this.lookupEnvironment.unitBeingCompleted != null)) {
/* 568 */       result = this.lookupEnvironment.unitBeingCompleted.compilationResult;
/*     */     }
/* 570 */     if (result == null) {
/* 571 */       synchronized (this) {
/* 572 */         if ((this.unitsToProcess != null) && (this.totalUnits > 0)) {
/* 573 */           result = this.unitsToProcess[(this.totalUnits - 1)].compilationResult;
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/* 578 */     boolean needToPrint = true;
/* 579 */     if (result != null)
/*     */     {
/* 582 */       String[] pbArguments = { 
/* 583 */         Messages.bind(Messages.compilation_internalError, Util.getExceptionSummary(internalException)) };
/*     */ 
/* 586 */       result
/* 587 */         .record(
/* 588 */         this.problemReporter
/* 589 */         .createProblem(
/* 590 */         result.getFileName(), 
/* 591 */         0, 
/* 592 */         pbArguments, 
/* 593 */         pbArguments, 
/* 594 */         1, 
/* 595 */         0, 
/* 596 */         0, 
/* 597 */         0, 
/* 598 */         0), 
/* 599 */         unit);
/*     */ 
/* 602 */       if (!result.hasBeenAccepted) {
/* 603 */         this.requestor.acceptResult(result.tagAsAccepted());
/* 604 */         needToPrint = false;
/*     */       }
/*     */     }
/* 607 */     if (needToPrint)
/*     */     {
/* 609 */       internalException.printStackTrace();
/*     */     }
/*     */   }
/*     */ 
/*     */   protected void handleInternalException(AbortCompilation abortException, CompilationUnitDeclaration unit)
/*     */   {
/* 621 */     if (abortException.isSilent) {
/* 622 */       if (abortException.silentException == null) {
/* 623 */         return;
/*     */       }
/* 625 */       throw abortException.silentException;
/*     */     }
/*     */ 
/* 632 */     CompilationResult result = abortException.compilationResult;
/* 633 */     if ((result == null) && (unit != null)) {
/* 634 */       result = unit.compilationResult;
/*     */     }
/*     */ 
/* 637 */     if ((result == null) && (this.lookupEnvironment.unitBeingCompleted != null)) {
/* 638 */       result = this.lookupEnvironment.unitBeingCompleted.compilationResult;
/*     */     }
/* 640 */     if (result == null) {
/* 641 */       synchronized (this) {
/* 642 */         if ((this.unitsToProcess != null) && (this.totalUnits > 0)) {
/* 643 */           result = this.unitsToProcess[(this.totalUnits - 1)].compilationResult;
/*     */         }
/*     */       }
/*     */     }
/* 647 */     if ((result != null) && (!result.hasBeenAccepted))
/*     */     {
/* 649 */       if (abortException.problem != null)
/*     */       {
/* 651 */         CategorizedProblem distantProblem = abortException.problem;
/* 652 */         CategorizedProblem[] knownProblems = result.problems;
/* 653 */         for (int i = 0; i < result.problemCount; i++) {
/* 654 */           if (knownProblems[i] == distantProblem) {
/*     */             break;
/*     */           }
/*     */         }
/* 658 */         if ((distantProblem instanceof DefaultProblem)) {
/* 659 */           ((DefaultProblem)distantProblem).setOriginatingFileName(result.getFileName());
/*     */         }
/* 661 */         result.record(distantProblem, unit);
/*     */       }
/* 665 */       else if (abortException.exception != null) {
/* 666 */         handleInternalException(abortException.exception, null, result);
/* 667 */         return;
/*     */       }
/*     */ 
/* 671 */       if (!result.hasBeenAccepted)
/* 672 */         this.requestor.acceptResult(result.tagAsAccepted());
/*     */     }
/*     */     else {
/* 675 */       abortException.printStackTrace();
/*     */     }
/*     */   }
/*     */ 
/*     */   public void initializeParser()
/*     */   {
/* 681 */     this.parser = new Parser(this.problemReporter, this.options.parseLiteralExpressionsAsConstants);
/*     */   }
/*     */ 
/*     */   protected void internalBeginToCompile(ICompilationUnit[] sourceUnits, int maxUnits)
/*     */   {
/* 689 */     if ((!this.useSingleThread) && (maxUnits >= 10)) {
/* 690 */       this.parser.readManager = new ReadManager(sourceUnits, maxUnits);
/*     */     }
/*     */ 
/* 693 */     for (int i = 0; i < maxUnits; i++) {
/*     */       try {
/* 695 */         if (this.options.verbose) {
/* 696 */           this.out.println(
/* 697 */             Messages.bind(Messages.compilation_request, 
/* 698 */             new String[] { 
/* 699 */             String.valueOf(i + 1), 
/* 700 */             String.valueOf(maxUnits), 
/* 701 */             new String(sourceUnits[i].getFileName()) }));
/*     */         }
/*     */ 
/* 706 */         CompilationResult unitResult = 
/* 707 */           new CompilationResult(sourceUnits[i], i, maxUnits, this.options.maxProblemsPerUnit);
/* 708 */         long parseStart = System.currentTimeMillis();
/*     */         CompilationUnitDeclaration parsedUnit;
/*     */         CompilationUnitDeclaration parsedUnit;
/* 709 */         if (this.totalUnits < this.parseThreshold)
/* 710 */           parsedUnit = this.parser.parse(sourceUnits[i], unitResult);
/*     */         else {
/* 712 */           parsedUnit = this.parser.dietParse(sourceUnits[i], unitResult);
/*     */         }
/* 714 */         long resolveStart = System.currentTimeMillis();
/* 715 */         this.stats.parseTime += resolveStart - parseStart;
/*     */ 
/* 717 */         this.lookupEnvironment.buildTypeBindings(parsedUnit, null);
/* 718 */         this.stats.resolveTime += System.currentTimeMillis() - resolveStart;
/* 719 */         addCompilationUnit(sourceUnits[i], parsedUnit);
/* 720 */         ImportReference currentPackage = parsedUnit.currentPackage;
/* 721 */         if (currentPackage != null) {
/* 722 */           unitResult.recordPackageName(currentPackage.tokens);
/*     */         }
/*     */       }
/*     */       finally
/*     */       {
/* 727 */         sourceUnits[i] = null;
/*     */       }
/*     */     }
/* 730 */     if (this.parser.readManager != null) {
/* 731 */       this.parser.readManager.shutdown();
/* 732 */       this.parser.readManager = null;
/*     */     }
/*     */ 
/* 735 */     this.lookupEnvironment.completeTypeBindings();
/*     */   }
/*     */ 
/*     */   public void process(CompilationUnitDeclaration unit, int i)
/*     */   {
/* 742 */     this.lookupEnvironment.unitBeingCompleted = unit;
/* 743 */     long parseStart = System.currentTimeMillis();
/*     */ 
/* 745 */     this.parser.getMethodBodies(unit);
/*     */ 
/* 747 */     long resolveStart = System.currentTimeMillis();
/* 748 */     this.stats.parseTime += resolveStart - parseStart;
/*     */ 
/* 751 */     if (unit.scope != null) {
/* 752 */       unit.scope.faultInTypes();
/*     */     }
/*     */ 
/* 755 */     if (unit.scope != null) {
/* 756 */       unit.scope.verifyMethods(this.lookupEnvironment.methodVerifier());
/*     */     }
/*     */ 
/* 759 */     unit.resolve();
/*     */ 
/* 761 */     long analyzeStart = System.currentTimeMillis();
/* 762 */     this.stats.resolveTime += analyzeStart - resolveStart;
/*     */ 
/* 765 */     if (!this.options.ignoreMethodBodies) unit.analyseCode();
/*     */ 
/* 767 */     long generateStart = System.currentTimeMillis();
/* 768 */     this.stats.analyzeTime += generateStart - analyzeStart;
/*     */ 
/* 770 */     if (!this.options.ignoreMethodBodies) unit.generateCode();
/*     */ 
/* 773 */     if ((this.options.produceReferenceInfo) && (unit.scope != null)) {
/* 774 */       unit.scope.storeDependencyInfo();
/*     */     }
/*     */ 
/* 777 */     unit.finalizeProblems();
/*     */ 
/* 779 */     this.stats.generateTime += System.currentTimeMillis() - generateStart;
/*     */ 
/* 782 */     unit.compilationResult.totalUnitsKnown = this.totalUnits;
/*     */ 
/* 784 */     this.lookupEnvironment.unitBeingCompleted = null;
/*     */   }
/*     */ 
/*     */   protected void processAnnotations() {
/* 788 */     int newUnitSize = 0;
/* 789 */     int newClassFilesSize = 0;
/* 790 */     int bottom = this.annotationProcessorStartIndex;
/* 791 */     int top = this.totalUnits;
/* 792 */     ReferenceBinding[] binaryTypeBindingsTemp = this.referenceBindings;
/* 793 */     if ((top == 0) && (binaryTypeBindingsTemp == null)) return;
/* 794 */     this.referenceBindings = null;
/*     */     do
/*     */     {
/* 797 */       int length = top - bottom;
/* 798 */       CompilationUnitDeclaration[] currentUnits = new CompilationUnitDeclaration[length];
/* 799 */       int index = 0;
/* 800 */       for (int i = bottom; i < top; i++) {
/* 801 */         CompilationUnitDeclaration currentUnit = this.unitsToProcess[i];
/* 802 */         if ((currentUnit.bits & 0x1) == 0) {
/* 803 */           currentUnits[(index++)] = currentUnit;
/*     */         }
/*     */       }
/* 806 */       if (index != length) {
/* 807 */         System.arraycopy(currentUnits, 0, currentUnits = new CompilationUnitDeclaration[index], 0, index);
/*     */       }
/* 809 */       this.annotationProcessorManager.processAnnotations(currentUnits, binaryTypeBindingsTemp, false);
/* 810 */       ICompilationUnit[] newUnits = this.annotationProcessorManager.getNewUnits();
/* 811 */       newUnitSize = newUnits.length;
/* 812 */       ReferenceBinding[] newClassFiles = this.annotationProcessorManager.getNewClassFiles();
/* 813 */       binaryTypeBindingsTemp = newClassFiles;
/* 814 */       newClassFilesSize = newClassFiles.length;
/* 815 */       if (newUnitSize != 0) {
/* 816 */         ICompilationUnit[] newProcessedUnits = (ICompilationUnit[])newUnits.clone();
/*     */         try {
/* 818 */           this.lookupEnvironment.isProcessingAnnotations = true;
/* 819 */           internalBeginToCompile(newUnits, newUnitSize);
/*     */         } catch (SourceTypeCollisionException e) {
/* 821 */           e.newAnnotationProcessorUnits = newProcessedUnits;
/* 822 */           throw e;
/*     */         } finally {
/* 824 */           this.lookupEnvironment.isProcessingAnnotations = false;
/* 825 */           this.annotationProcessorManager.reset();
/*     */         }
/* 827 */         bottom = top;
/* 828 */         top = this.totalUnits;
/*     */       } else {
/* 830 */         bottom = top;
/* 831 */         this.annotationProcessorManager.reset();
/*     */       }
/*     */     }
/* 833 */     while ((newUnitSize != 0) || (newClassFilesSize != 0));
/*     */ 
/* 836 */     this.annotationProcessorManager.processAnnotations(null, null, true);
/*     */   }
/*     */ 
/*     */   public void reset()
/*     */   {
/* 841 */     this.lookupEnvironment.reset();
/* 842 */     this.parser.scanner.source = null;
/* 843 */     this.unitsToProcess = null;
/* 844 */     if (DebugRequestor != null) DebugRequestor.reset();
/* 845 */     this.problemReporter.reset();
/*     */   }
/*     */ 
/*     */   public CompilationUnitDeclaration resolve(CompilationUnitDeclaration unit, ICompilationUnit sourceUnit, boolean verifyMethods, boolean analyzeCode, boolean generateCode)
/*     */   {
/*     */     try
/*     */     {
/* 859 */       if (unit == null)
/*     */       {
/* 861 */         this.parseThreshold = 0;
/* 862 */         beginToCompile(new ICompilationUnit[] { sourceUnit });
/*     */ 
/* 864 */         unit = this.unitsToProcess[0];
/*     */       }
/*     */       else {
/* 867 */         this.lookupEnvironment.buildTypeBindings(unit, null);
/*     */ 
/* 870 */         this.lookupEnvironment.completeTypeBindings();
/*     */       }
/* 872 */       this.lookupEnvironment.unitBeingCompleted = unit;
/* 873 */       this.parser.getMethodBodies(unit);
/* 874 */       if (unit.scope != null)
/*     */       {
/* 876 */         unit.scope.faultInTypes();
/* 877 */         if ((unit.scope != null) && (verifyMethods))
/*     */         {
/* 880 */           unit.scope.verifyMethods(this.lookupEnvironment.methodVerifier());
/*     */         }
/*     */ 
/* 883 */         unit.resolve();
/*     */ 
/* 886 */         if (analyzeCode) unit.analyseCode();
/*     */ 
/* 889 */         if (generateCode) unit.generateCode();
/*     */ 
/* 892 */         unit.finalizeProblems();
/*     */       }
/* 894 */       if (this.unitsToProcess != null) this.unitsToProcess[0] = null;
/* 895 */       this.requestor.acceptResult(unit.compilationResult.tagAsAccepted());
/* 896 */       return unit;
/*     */     } catch (AbortCompilation e) {
/* 898 */       handleInternalException(e, unit);
/* 899 */       return unit == null ? this.unitsToProcess[0] : unit;
/*     */     } catch (Error e) {
/* 901 */       handleInternalException(e, unit, null);
/* 902 */       throw e;
/*     */     } catch (RuntimeException e) {
/* 904 */       handleInternalException(e, unit, null);
/* 905 */     }throw e;
/*     */   }
/*     */ 
/*     */   public CompilationUnitDeclaration resolve(ICompilationUnit sourceUnit, boolean verifyMethods, boolean analyzeCode, boolean generateCode)
/*     */   {
/* 928 */     return resolve(
/* 929 */       null, 
/* 930 */       sourceUnit, 
/* 931 */       verifyMethods, 
/* 932 */       analyzeCode, 
/* 933 */       generateCode);
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\ecj-3.5.2.jar
 * Qualified Name:     org.eclipse.jdt.internal.compiler.Compiler
 * JD-Core Version:    0.6.0
 */