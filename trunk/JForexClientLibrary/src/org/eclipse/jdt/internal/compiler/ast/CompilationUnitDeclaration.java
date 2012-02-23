/*     */ package org.eclipse.jdt.internal.compiler.ast;
/*     */ 
/*     */ import java.util.Arrays;
/*     */ import java.util.Comparator;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import org.eclipse.jdt.core.compiler.CategorizedProblem;
/*     */ import org.eclipse.jdt.core.compiler.CharOperation;
/*     */ import org.eclipse.jdt.internal.compiler.ASTVisitor;
/*     */ import org.eclipse.jdt.internal.compiler.ClassFile;
/*     */ import org.eclipse.jdt.internal.compiler.CompilationResult;
/*     */ import org.eclipse.jdt.internal.compiler.env.ICompilationUnit;
/*     */ import org.eclipse.jdt.internal.compiler.impl.CompilerOptions;
/*     */ import org.eclipse.jdt.internal.compiler.impl.Constant;
/*     */ import org.eclipse.jdt.internal.compiler.impl.IrritantSet;
/*     */ import org.eclipse.jdt.internal.compiler.impl.ReferenceContext;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.ClassScope;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.CompilationUnitScope;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.ImportBinding;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.LocalTypeBinding;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.MethodScope;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.SourceTypeBinding;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.TypeConstants;
/*     */ import org.eclipse.jdt.internal.compiler.parser.NLSTag;
/*     */ import org.eclipse.jdt.internal.compiler.problem.AbortCompilationUnit;
/*     */ import org.eclipse.jdt.internal.compiler.problem.AbortMethod;
/*     */ import org.eclipse.jdt.internal.compiler.problem.AbortType;
/*     */ import org.eclipse.jdt.internal.compiler.problem.ProblemReporter;
/*     */ import org.eclipse.jdt.internal.compiler.problem.ProblemSeverities;
/*     */ import org.eclipse.jdt.internal.compiler.util.HashSetOfInt;
/*     */ 
/*     */ public class CompilationUnitDeclaration extends ASTNode
/*     */   implements ProblemSeverities, ReferenceContext
/*     */ {
/*  43 */   private static final Comparator STRING_LITERAL_COMPARATOR = new Comparator() {
/*     */     public int compare(Object o1, Object o2) {
/*  45 */       StringLiteral literal1 = (StringLiteral)o1;
/*  46 */       StringLiteral literal2 = (StringLiteral)o2;
/*  47 */       return literal1.sourceStart - literal2.sourceStart; }  } ;
/*     */   private static final int STRING_LITERALS_INCREMENT = 10;
/*     */   public ImportReference currentPackage;
/*     */   public ImportReference[] imports;
/*     */   public TypeDeclaration[] types;
/*     */   public int[][] comments;
/*  57 */   public boolean ignoreFurtherInvestigation = false;
/*  58 */   public boolean ignoreMethodBodies = false;
/*     */   public CompilationUnitScope scope;
/*     */   public ProblemReporter problemReporter;
/*     */   public CompilationResult compilationResult;
/*     */   public LocalTypeBinding[] localTypes;
/*  64 */   public int localTypeCount = 0;
/*     */   public boolean isPropagatingInnerClassEmulation;
/*     */   public Javadoc javadoc;
/*     */   public NLSTag[] nlsTags;
/*     */   private StringLiteral[] stringLiterals;
/*     */   private int stringLiteralsPtr;
/*     */   private HashSetOfInt stringLiteralsStart;
/*     */   IrritantSet[] suppressWarningIrritants;
/*     */   Annotation[] suppressWarningAnnotations;
/*     */   long[] suppressWarningScopePositions;
/*     */   int suppressWarningsCount;
/*     */ 
/*  81 */   public CompilationUnitDeclaration(ProblemReporter problemReporter, CompilationResult compilationResult, int sourceLength) { this.problemReporter = problemReporter;
/*  82 */     this.compilationResult = compilationResult;
/*     */ 
/*  84 */     this.sourceStart = 0;
/*  85 */     this.sourceEnd = (sourceLength - 1);
/*     */   }
/*     */ 
/*     */   public void abort(int abortLevel, CategorizedProblem problem)
/*     */   {
/*  92 */     switch (abortLevel) {
/*     */     case 8:
/*  94 */       throw new AbortType(this.compilationResult, problem);
/*     */     case 16:
/*  96 */       throw new AbortMethod(this.compilationResult, problem);
/*     */     }
/*  98 */     throw new AbortCompilationUnit(this.compilationResult, problem);
/*     */   }
/*     */ 
/*     */   public void analyseCode()
/*     */   {
/* 106 */     if (this.ignoreFurtherInvestigation)
/* 107 */       return;
/*     */     try {
/* 109 */       if (this.types != null) {
/* 110 */         int i = 0; for (int count = this.types.length; i < count; i++) {
/* 111 */           this.types[i].analyseCode(this.scope);
/*     */         }
/*     */       }
/*     */ 
/* 115 */       propagateInnerEmulationForAllLocalTypes();
/*     */     } catch (AbortCompilationUnit localAbortCompilationUnit) {
/* 117 */       this.ignoreFurtherInvestigation = true;
/* 118 */       return;
/*     */     }
/*     */   }
/*     */ 
/*     */   public void cleanUp()
/*     */   {
/* 127 */     if (this.types != null) {
/* 128 */       int i = 0; for (int max = this.types.length; i < max; i++) {
/* 129 */         cleanUp(this.types[i]);
/*     */       }
/* 131 */       int i = 0; for (int max = this.localTypeCount; i < max; i++) {
/* 132 */         LocalTypeBinding localType = this.localTypes[i];
/*     */ 
/* 134 */         localType.scope = null;
/* 135 */         localType.enclosingCase = null;
/*     */       }
/*     */     }
/*     */ 
/* 139 */     this.compilationResult.recoveryScannerData = null;
/*     */ 
/* 141 */     ClassFile[] classFiles = this.compilationResult.getClassFiles();
/* 142 */     int i = 0; for (int max = classFiles.length; i < max; i++)
/*     */     {
/* 144 */       ClassFile classFile = classFiles[i];
/*     */ 
/* 146 */       classFile.referenceBinding = null;
/* 147 */       classFile.innerClassesBindings = null;
/* 148 */       classFile.missingTypes = null;
/* 149 */       classFile.visitedTypes = null;
/*     */     }
/*     */ 
/* 152 */     this.suppressWarningAnnotations = null;
/*     */   }
/*     */ 
/*     */   private void cleanUp(TypeDeclaration type) {
/* 156 */     if (type.memberTypes != null) {
/* 157 */       int i = 0; for (int max = type.memberTypes.length; i < max; i++) {
/* 158 */         cleanUp(type.memberTypes[i]);
/*     */       }
/*     */     }
/* 161 */     if ((type.binding != null) && (type.binding.isAnnotationType()))
/* 162 */       this.compilationResult.hasAnnotations = true;
/* 163 */     if (type.binding != null)
/*     */     {
/* 165 */       type.binding.scope = null;
/*     */     }
/*     */   }
/*     */ 
/*     */   public void checkUnusedImports() {
/* 170 */     if (this.scope.imports != null) {
/* 171 */       int i = 0; for (int max = this.scope.imports.length; i < max; i++) {
/* 172 */         ImportBinding importBinding = this.scope.imports[i];
/* 173 */         ImportReference importReference = importBinding.reference;
/* 174 */         if ((importReference != null) && ((importReference.bits & 0x2) == 0))
/* 175 */           this.scope.problemReporter().unusedImport(importReference);
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public CompilationResult compilationResult()
/*     */   {
/* 182 */     return this.compilationResult;
/*     */   }
/*     */ 
/*     */   public void createPackageInfoType() {
/* 186 */     TypeDeclaration declaration = new TypeDeclaration(this.compilationResult);
/* 187 */     declaration.name = TypeConstants.PACKAGE_INFO_NAME;
/* 188 */     declaration.modifiers = 512;
/* 189 */     declaration.javadoc = this.javadoc;
/* 190 */     this.types[0] = declaration;
/*     */   }
/*     */ 
/*     */   public TypeDeclaration declarationOfType(char[][] typeName)
/*     */   {
/* 200 */     for (int i = 0; i < this.types.length; i++) {
/* 201 */       TypeDeclaration typeDecl = this.types[i].declarationOfType(typeName);
/* 202 */       if (typeDecl != null) {
/* 203 */         return typeDecl;
/*     */       }
/*     */     }
/* 206 */     return null;
/*     */   }
/*     */ 
/*     */   public void finalizeProblems() {
/* 210 */     if (this.suppressWarningsCount == 0) return;
/* 211 */     int removed = 0;
/* 212 */     CategorizedProblem[] problems = this.compilationResult.problems;
/* 213 */     int problemCount = this.compilationResult.problemCount;
/* 214 */     IrritantSet[] foundIrritants = new IrritantSet[this.suppressWarningsCount];
/* 215 */     CompilerOptions options = this.scope.compilerOptions();
/* 216 */     boolean hasErrors = false;
/* 217 */     int iProblem = 0; for (int length = problemCount; iProblem < length; iProblem++) {
/* 218 */       CategorizedProblem problem = problems[iProblem];
/* 219 */       int problemID = problem.getID();
/* 220 */       if (problem.isError()) {
/* 221 */         if (problemID == 536871547)
/*     */           continue;
/* 223 */         hasErrors = true;
/*     */       }
/*     */       else
/*     */       {
/* 227 */         int start = problem.getSourceStart();
/* 228 */         int end = problem.getSourceEnd();
/* 229 */         int irritant = ProblemReporter.getIrritant(problemID);
/* 230 */         int iSuppress = 0; for (int suppressCount = this.suppressWarningsCount; iSuppress < suppressCount; iSuppress++) {
/* 231 */           long position = this.suppressWarningScopePositions[iSuppress];
/* 232 */           int startSuppress = (int)(position >>> 32);
/* 233 */           int endSuppress = (int)position;
/* 234 */           if ((start < startSuppress) || 
/* 235 */             (end > endSuppress) || 
/* 236 */             (!this.suppressWarningIrritants[iSuppress].isSet(irritant))) {
/*     */             continue;
/*     */           }
/* 239 */           removed++;
/* 240 */           problems[iProblem] = null;
/* 241 */           if (this.compilationResult.problemsMap != null) this.compilationResult.problemsMap.remove(problem);
/* 242 */           if (this.compilationResult.firstErrors != null) this.compilationResult.firstErrors.remove(problem);
/* 243 */           if (foundIrritants[iSuppress] == null) {
/* 244 */             foundIrritants[iSuppress] = new IrritantSet(irritant); break;
/*     */           }
/* 246 */           foundIrritants[iSuppress].set(irritant);
/*     */ 
/* 248 */           break;
/*     */         }
/*     */       }
/*     */     }
/* 252 */     if (removed > 0) {
/* 253 */       int i = 0; for (int index = 0; i < problemCount; i++)
/*     */       {
/*     */         CategorizedProblem problem;
/* 255 */         if ((problem = problems[i]) != null) {
/* 256 */           if (i > index)
/* 257 */             problems[(index++)] = problem;
/*     */           else {
/* 259 */             index++;
/*     */           }
/*     */         }
/*     */       }
/* 263 */       this.compilationResult.problemCount -= removed;
/*     */     }
/*     */ 
/* 266 */     if (!hasErrors) {
/* 267 */       int severity = options.getSeverity(570425344);
/* 268 */       if (severity != -1) {
/* 269 */         boolean unusedWarningTokenIsWarning = (severity & 0x1) == 0;
/* 270 */         int iSuppress = 0; for (int suppressCount = this.suppressWarningsCount; iSuppress < suppressCount; iSuppress++) {
/* 271 */           Annotation annotation = this.suppressWarningAnnotations[iSuppress];
/* 272 */           if (annotation != null) {
/* 273 */             IrritantSet irritants = this.suppressWarningIrritants[iSuppress];
/* 274 */             if (((unusedWarningTokenIsWarning) && (irritants.areAllSet())) || 
/* 275 */               (irritants == foundIrritants[iSuppress])) continue;
/* 276 */             MemberValuePair[] pairs = annotation.memberValuePairs();
/* 277 */             int iPair = 0; for (int pairCount = pairs.length; iPair < pairCount; iPair++) {
/* 278 */               MemberValuePair pair = pairs[iPair];
/* 279 */               if (CharOperation.equals(pair.name, TypeConstants.VALUE)) {
/* 280 */                 Expression value = pair.value;
/* 281 */                 if ((value instanceof ArrayInitializer)) {
/* 282 */                   ArrayInitializer initializer = (ArrayInitializer)value;
/* 283 */                   Expression[] inits = initializer.expressions;
/* 284 */                   if (inits == null) break;
/* 285 */                   int iToken = 0; for (int tokenCount = inits.length; iToken < tokenCount; iToken++) {
/* 286 */                     Constant cst = inits[iToken].constant;
/* 287 */                     if ((cst != Constant.NotAConstant) && (cst.typeID() == 11)) {
/* 288 */                       IrritantSet tokenIrritants = CompilerOptions.warningTokenToIrritants(cst.stringValue());
/* 289 */                       if ((tokenIrritants == null) || 
/* 290 */                         (tokenIrritants.areAllSet()) || 
/* 291 */                         (!options.isAnyEnabled(tokenIrritants)) || (
/* 292 */                         (foundIrritants[iSuppress] != null) && (foundIrritants[iSuppress].isAnySet(tokenIrritants)))) continue;
/* 293 */                       if (unusedWarningTokenIsWarning) {
/* 294 */                         int start = value.sourceStart; int end = value.sourceEnd;
/* 295 */                         for (int jSuppress = iSuppress - 1; jSuppress >= 0; jSuppress--) {
/* 296 */                           long position = this.suppressWarningScopePositions[jSuppress];
/* 297 */                           int startSuppress = (int)(position >>> 32);
/* 298 */                           int endSuppress = (int)position;
/* 299 */                           if ((start >= startSuppress) && 
/* 300 */                             (end <= endSuppress) && 
/* 301 */                             (this.suppressWarningIrritants[jSuppress].areAllSet())) break;
/*     */                         }
/*     */                       }
/* 304 */                       this.scope.problemReporter().unusedWarningToken(inits[iToken]);
/*     */                     }
/*     */                   }
/* 285 */                   break;
/*     */                 }
/*     */ 
/* 310 */                 Constant cst = value.constant;
/* 311 */                 if ((cst == Constant.NotAConstant) || (cst.typeID() != 11)) break;
/* 312 */                 IrritantSet tokenIrritants = CompilerOptions.warningTokenToIrritants(cst.stringValue());
/* 313 */                 if ((tokenIrritants == null) || 
/* 314 */                   (tokenIrritants.areAllSet()) || 
/* 315 */                   (!options.isAnyEnabled(tokenIrritants)) || (
/* 316 */                   (foundIrritants[iSuppress] != null) && (foundIrritants[iSuppress].isAnySet(tokenIrritants)))) break;
/* 317 */                 if (unusedWarningTokenIsWarning) {
/* 318 */                   int start = value.sourceStart; int end = value.sourceEnd;
/* 319 */                   for (int jSuppress = iSuppress - 1; jSuppress >= 0; jSuppress--) {
/* 320 */                     long position = this.suppressWarningScopePositions[jSuppress];
/* 321 */                     int startSuppress = (int)(position >>> 32);
/* 322 */                     int endSuppress = (int)position;
/* 323 */                     if ((start >= startSuppress) && 
/* 324 */                       (end <= endSuppress) && 
/* 325 */                       (this.suppressWarningIrritants[jSuppress].areAllSet())) break;
/*     */                   }
/*     */                 }
/* 328 */                 this.scope.problemReporter().unusedWarningToken(value);
/*     */ 
/* 332 */                 break;
/*     */               }
/*     */             }
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public void generateCode()
/*     */   {
/* 345 */     if (this.ignoreFurtherInvestigation) {
/* 346 */       if (this.types != null) {
/* 347 */         int i = 0; for (int count = this.types.length; i < count; i++) {
/* 348 */           this.types[i].ignoreFurtherInvestigation = true;
/*     */ 
/* 350 */           this.types[i].generateCode(this.scope);
/*     */         }
/*     */       }
/* 353 */       return;
/*     */     }
/*     */     try {
/* 356 */       if (this.types != null) {
/* 357 */         int i = 0; for (int count = this.types.length; i < count; i++)
/* 358 */           this.types[i].generateCode(this.scope);
/*     */       }
/*     */     }
/*     */     catch (AbortCompilationUnit localAbortCompilationUnit) {
/*     */     }
/*     */   }
/*     */ 
/*     */   public char[] getFileName() {
/* 366 */     return this.compilationResult.getFileName();
/*     */   }
/*     */ 
/*     */   public char[] getMainTypeName() {
/* 370 */     if (this.compilationResult.compilationUnit == null) {
/* 371 */       char[] fileName = this.compilationResult.getFileName();
/*     */ 
/* 373 */       int start = CharOperation.lastIndexOf('/', fileName) + 1;
/* 374 */       if ((start == 0) || (start < CharOperation.lastIndexOf('\\', fileName))) {
/* 375 */         start = CharOperation.lastIndexOf('\\', fileName) + 1;
/*     */       }
/* 377 */       int end = CharOperation.lastIndexOf('.', fileName);
/* 378 */       if (end == -1) {
/* 379 */         end = fileName.length;
/*     */       }
/* 381 */       return CharOperation.subarray(fileName, start, end);
/*     */     }
/* 383 */     return this.compilationResult.compilationUnit.getMainTypeName();
/*     */   }
/*     */ 
/*     */   public boolean isEmpty()
/*     */   {
/* 388 */     return (this.currentPackage == null) && (this.imports == null) && (this.types == null);
/*     */   }
/*     */ 
/*     */   public boolean isPackageInfo() {
/* 392 */     return CharOperation.equals(getMainTypeName(), TypeConstants.PACKAGE_INFO_NAME);
/*     */   }
/*     */ 
/*     */   public boolean hasErrors() {
/* 396 */     return this.ignoreFurtherInvestigation;
/*     */   }
/*     */ 
/*     */   public StringBuffer print(int indent, StringBuffer output) {
/* 400 */     if (this.currentPackage != null) {
/* 401 */       printIndent(indent, output).append("package ");
/* 402 */       this.currentPackage.print(0, output, false).append(";\n");
/*     */     }
/* 404 */     if (this.imports != null) {
/* 405 */       for (int i = 0; i < this.imports.length; i++) {
/* 406 */         printIndent(indent, output).append("import ");
/* 407 */         ImportReference currentImport = this.imports[i];
/* 408 */         if (currentImport.isStatic()) {
/* 409 */           output.append("static ");
/*     */         }
/* 411 */         currentImport.print(0, output).append(";\n");
/*     */       }
/*     */     }
/* 414 */     if (this.types != null) {
/* 415 */       for (int i = 0; i < this.types.length; i++) {
/* 416 */         this.types[i].print(indent, output).append("\n");
/*     */       }
/*     */     }
/* 419 */     return output;
/*     */   }
/*     */ 
/*     */   public void propagateInnerEmulationForAllLocalTypes()
/*     */   {
/* 426 */     this.isPropagatingInnerClassEmulation = true;
/* 427 */     int i = 0; for (int max = this.localTypeCount; i < max; i++) {
/* 428 */       LocalTypeBinding localType = this.localTypes[i];
/*     */ 
/* 430 */       if ((localType.scope.referenceType().bits & 0x80000000) != 0)
/* 431 */         localType.updateInnerEmulationDependents();
/*     */     }
/*     */   }
/*     */ 
/*     */   public void recordStringLiteral(StringLiteral literal, boolean fromRecovery)
/*     */   {
/* 437 */     if (this.stringLiteralsStart != null) {
/* 438 */       if (this.stringLiteralsStart.contains(literal.sourceStart)) return;
/* 439 */       this.stringLiteralsStart.add(literal.sourceStart);
/* 440 */     } else if (fromRecovery) {
/* 441 */       this.stringLiteralsStart = new HashSetOfInt(this.stringLiteralsPtr + 10);
/* 442 */       for (int i = 0; i < this.stringLiteralsPtr; i++) {
/* 443 */         this.stringLiteralsStart.add(this.stringLiterals[i].sourceStart);
/*     */       }
/*     */ 
/* 446 */       if (this.stringLiteralsStart.contains(literal.sourceStart)) return;
/* 447 */       this.stringLiteralsStart.add(literal.sourceStart);
/*     */     }
/*     */ 
/* 450 */     if (this.stringLiterals == null) {
/* 451 */       this.stringLiterals = new StringLiteral[10];
/* 452 */       this.stringLiteralsPtr = 0;
/*     */     } else {
/* 454 */       int stackLength = this.stringLiterals.length;
/* 455 */       if (this.stringLiteralsPtr == stackLength) {
/* 456 */         System.arraycopy(
/* 457 */           this.stringLiterals, 
/* 458 */           0, 
/* 459 */           this.stringLiterals = new StringLiteral[stackLength + 10], 
/* 460 */           0, 
/* 461 */           stackLength);
/*     */       }
/*     */     }
/* 464 */     this.stringLiterals[(this.stringLiteralsPtr++)] = literal;
/*     */   }
/*     */ 
/*     */   public void recordSuppressWarnings(IrritantSet irritants, Annotation annotation, int scopeStart, int scopeEnd) {
/* 468 */     if (this.suppressWarningIrritants == null) {
/* 469 */       this.suppressWarningIrritants = new IrritantSet[3];
/* 470 */       this.suppressWarningAnnotations = new Annotation[3];
/* 471 */       this.suppressWarningScopePositions = new long[3];
/* 472 */     } else if (this.suppressWarningIrritants.length == this.suppressWarningsCount) {
/* 473 */       System.arraycopy(this.suppressWarningIrritants, 0, this.suppressWarningIrritants = new IrritantSet[2 * this.suppressWarningsCount], 0, this.suppressWarningsCount);
/* 474 */       System.arraycopy(this.suppressWarningAnnotations, 0, this.suppressWarningAnnotations = new Annotation[2 * this.suppressWarningsCount], 0, this.suppressWarningsCount);
/* 475 */       System.arraycopy(this.suppressWarningScopePositions, 0, this.suppressWarningScopePositions = new long[2 * this.suppressWarningsCount], 0, this.suppressWarningsCount);
/*     */     }
/* 477 */     this.suppressWarningIrritants[this.suppressWarningsCount] = irritants;
/* 478 */     this.suppressWarningAnnotations[this.suppressWarningsCount] = annotation;
/* 479 */     this.suppressWarningScopePositions[(this.suppressWarningsCount++)] = ((scopeStart << 32) + scopeEnd);
/*     */   }
/*     */ 
/*     */   public void record(LocalTypeBinding localType)
/*     */   {
/* 487 */     if (this.localTypeCount == 0)
/* 488 */       this.localTypes = new LocalTypeBinding[5];
/* 489 */     else if (this.localTypeCount == this.localTypes.length) {
/* 490 */       System.arraycopy(this.localTypes, 0, this.localTypes = new LocalTypeBinding[this.localTypeCount * 2], 0, this.localTypeCount);
/*     */     }
/* 492 */     this.localTypes[(this.localTypeCount++)] = localType;
/*     */   }
/*     */ 
/*     */   public void resolve() {
/* 496 */     int startingTypeIndex = 0;
/* 497 */     boolean isPackageInfo = isPackageInfo();
/* 498 */     if ((this.types != null) && (isPackageInfo))
/*     */     {
/* 500 */       TypeDeclaration syntheticTypeDeclaration = this.types[0];
/*     */ 
/* 502 */       if (syntheticTypeDeclaration.javadoc == null) {
/* 503 */         syntheticTypeDeclaration.javadoc = new Javadoc(syntheticTypeDeclaration.declarationSourceStart, syntheticTypeDeclaration.declarationSourceStart);
/*     */       }
/* 505 */       syntheticTypeDeclaration.resolve(this.scope);
/*     */ 
/* 512 */       if ((this.javadoc != null) && (syntheticTypeDeclaration.staticInitializerScope != null)) {
/* 513 */         this.javadoc.resolve(syntheticTypeDeclaration.staticInitializerScope);
/*     */       }
/* 515 */       startingTypeIndex = 1;
/*     */     }
/* 518 */     else if (this.javadoc != null) {
/* 519 */       this.javadoc.resolve(this.scope);
/*     */     }
/*     */ 
/* 522 */     if ((this.currentPackage != null) && (this.currentPackage.annotations != null) && (!isPackageInfo))
/* 523 */       this.scope.problemReporter().invalidFileNameForPackageAnnotations(this.currentPackage.annotations[0]);
/*     */     try
/*     */     {
/* 526 */       if (this.types != null) {
/* 527 */         int i = startingTypeIndex; for (int count = this.types.length; i < count; i++) {
/* 528 */           this.types[i].resolve(this.scope);
/*     */         }
/*     */       }
/* 531 */       if (!this.compilationResult.hasErrors()) checkUnusedImports();
/* 532 */       reportNLSProblems();
/*     */     } catch (AbortCompilationUnit localAbortCompilationUnit) {
/* 534 */       this.ignoreFurtherInvestigation = true;
/* 535 */       return;
/*     */     }
/*     */   }
/*     */ 
/*     */   private void reportNLSProblems() {
/* 540 */     if ((this.nlsTags != null) || (this.stringLiterals != null)) {
/* 541 */       int stringLiteralsLength = this.stringLiteralsPtr;
/* 542 */       int nlsTagsLength = this.nlsTags == null ? 0 : this.nlsTags.length;
/* 543 */       if (stringLiteralsLength == 0) {
/* 544 */         if (nlsTagsLength != 0) {
/* 545 */           for (int i = 0; i < nlsTagsLength; i++) {
/* 546 */             NLSTag tag = this.nlsTags[i];
/* 547 */             if (tag != null)
/* 548 */               this.scope.problemReporter().unnecessaryNLSTags(tag.start, tag.end);
/*     */           }
/*     */         }
/*     */       }
/* 552 */       else if (nlsTagsLength == 0)
/*     */       {
/* 554 */         if (this.stringLiterals.length != stringLiteralsLength) {
/* 555 */           System.arraycopy(this.stringLiterals, 0, this.stringLiterals = new StringLiteral[stringLiteralsLength], 0, stringLiteralsLength);
/*     */         }
/* 557 */         Arrays.sort(this.stringLiterals, STRING_LITERAL_COMPARATOR);
/* 558 */         for (int i = 0; i < stringLiteralsLength; i++)
/* 559 */           this.scope.problemReporter().nonExternalizedStringLiteral(this.stringLiterals[i]);
/*     */       }
/*     */       else
/*     */       {
/* 563 */         if (this.stringLiterals.length != stringLiteralsLength) {
/* 564 */           System.arraycopy(this.stringLiterals, 0, this.stringLiterals = new StringLiteral[stringLiteralsLength], 0, stringLiteralsLength);
/*     */         }
/* 566 */         Arrays.sort(this.stringLiterals, STRING_LITERAL_COMPARATOR);
/* 567 */         int indexInLine = 1;
/* 568 */         int lastLineNumber = -1;
/* 569 */         StringLiteral literal = null;
/* 570 */         int index = 0;
/* 571 */         int i = 0;
/* 572 */         for (; i < stringLiteralsLength; i++) {
/* 573 */           literal = this.stringLiterals[i];
/* 574 */           int literalLineNumber = literal.lineNumber;
/* 575 */           if (lastLineNumber != literalLineNumber) {
/* 576 */             indexInLine = 1;
/* 577 */             lastLineNumber = literalLineNumber;
/*     */           } else {
/* 579 */             indexInLine++;
/*     */           }
/* 581 */           if (index >= nlsTagsLength) break;
/*     */           while (true) {
/* 583 */             NLSTag tag = this.nlsTags[index];
/* 584 */             if (tag != null) {
/* 585 */               int tagLineNumber = tag.lineNumber;
/* 586 */               if (literalLineNumber < tagLineNumber) {
/* 587 */                 this.scope.problemReporter().nonExternalizedStringLiteral(literal);
/*     */               }
/* 589 */               else if (literalLineNumber == tagLineNumber) {
/* 590 */                 if (tag.index == indexInLine) {
/* 591 */                   this.nlsTags[index] = null;
/* 592 */                   index++;
/*     */                 }
/*     */                 else {
/* 595 */                   for (int index2 = index + 1; index2 < nlsTagsLength; index2++) {
/* 596 */                     NLSTag tag2 = this.nlsTags[index2];
/* 597 */                     if (tag2 != null) {
/* 598 */                       int tagLineNumber2 = tag2.lineNumber;
/* 599 */                       if (literalLineNumber == tagLineNumber2) {
/* 600 */                         if (tag2.index == indexInLine) {
/* 601 */                           this.nlsTags[index2] = null;
/* 602 */                           break;
/*     */                         }
/*     */                       }
/*     */                       else
/*     */                       {
/* 607 */                         this.scope.problemReporter().nonExternalizedStringLiteral(literal);
/* 608 */                         break;
/*     */                       }
/*     */                     }
/*     */                   }
/* 611 */                   this.scope.problemReporter().nonExternalizedStringLiteral(literal);
/*     */                 }
/*     */               }
/*     */               else
/* 615 */                 this.scope.problemReporter().unnecessaryNLSTags(tag.start, tag.end);
/*     */             }
/*     */             else
/*     */             {
/* 582 */               index++; if (index < nlsTagsLength)
/*     */               {
/*     */                 continue;
/*     */               }
/*     */ 
/* 621 */               break label505;
/*     */             }
/*     */           }
/*     */         }
/* 623 */         label505: for (; i < stringLiteralsLength; i++) {
/* 624 */           this.scope.problemReporter().nonExternalizedStringLiteral(this.stringLiterals[i]);
/*     */         }
/* 626 */         if (index < nlsTagsLength)
/* 627 */           for (; index < nlsTagsLength; index++) {
/* 628 */             NLSTag tag = this.nlsTags[index];
/* 629 */             if (tag != null)
/* 630 */               this.scope.problemReporter().unnecessaryNLSTags(tag.start, tag.end);
/*     */           }
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public void tagAsHavingErrors()
/*     */   {
/* 639 */     this.ignoreFurtherInvestigation = true;
/*     */   }
/*     */ 
/*     */   public void traverse(ASTVisitor visitor, CompilationUnitScope unitScope) {
/* 643 */     if (this.ignoreFurtherInvestigation)
/* 644 */       return;
/*     */     try {
/* 646 */       if (visitor.visit(this, this.scope)) {
/* 647 */         if ((this.types != null) && (isPackageInfo()))
/*     */         {
/* 649 */           TypeDeclaration syntheticTypeDeclaration = this.types[0];
/*     */ 
/* 651 */           MethodScope methodScope = syntheticTypeDeclaration.staticInitializerScope;
/*     */ 
/* 653 */           if ((this.javadoc != null) && (methodScope != null)) {
/* 654 */             this.javadoc.traverse(visitor, methodScope);
/*     */           }
/*     */ 
/* 657 */           if ((this.currentPackage != null) && (methodScope != null)) {
/* 658 */             Annotation[] annotations = this.currentPackage.annotations;
/* 659 */             if (annotations != null) {
/* 660 */               int annotationsLength = annotations.length;
/* 661 */               for (int i = 0; i < annotationsLength; i++) {
/* 662 */                 annotations[i].traverse(visitor, methodScope);
/*     */               }
/*     */             }
/*     */           }
/*     */         }
/* 667 */         if (this.currentPackage != null) {
/* 668 */           this.currentPackage.traverse(visitor, this.scope);
/*     */         }
/* 670 */         if (this.imports != null) {
/* 671 */           int importLength = this.imports.length;
/* 672 */           for (int i = 0; i < importLength; i++) {
/* 673 */             this.imports[i].traverse(visitor, this.scope);
/*     */           }
/*     */         }
/* 676 */         if (this.types != null) {
/* 677 */           int typesLength = this.types.length;
/* 678 */           for (int i = 0; i < typesLength; i++) {
/* 679 */             this.types[i].traverse(visitor, this.scope);
/*     */           }
/*     */         }
/*     */       }
/* 683 */       visitor.endVisit(this, this.scope);
/*     */     }
/*     */     catch (AbortCompilationUnit localAbortCompilationUnit)
/*     */     {
/*     */     }
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\ecj-3.5.2.jar
 * Qualified Name:     org.eclipse.jdt.internal.compiler.ast.CompilationUnitDeclaration
 * JD-Core Version:    0.6.0
 */