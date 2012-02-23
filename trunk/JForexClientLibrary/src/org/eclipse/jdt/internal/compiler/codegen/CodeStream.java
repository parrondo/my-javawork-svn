/*      */ package org.eclipse.jdt.internal.compiler.codegen;
/*      */ 
/*      */ import org.eclipse.jdt.core.compiler.CharOperation;
/*      */ import org.eclipse.jdt.internal.compiler.ClassFile;
/*      */ import org.eclipse.jdt.internal.compiler.CompilationResult;
/*      */ import org.eclipse.jdt.internal.compiler.ast.ASTNode;
/*      */ import org.eclipse.jdt.internal.compiler.ast.AbstractMethodDeclaration;
/*      */ import org.eclipse.jdt.internal.compiler.ast.AllocationExpression;
/*      */ import org.eclipse.jdt.internal.compiler.ast.CompilationUnitDeclaration;
/*      */ import org.eclipse.jdt.internal.compiler.ast.ExplicitConstructorCall;
/*      */ import org.eclipse.jdt.internal.compiler.ast.Expression;
/*      */ import org.eclipse.jdt.internal.compiler.ast.TypeDeclaration;
/*      */ import org.eclipse.jdt.internal.compiler.impl.CompilerOptions;
/*      */ import org.eclipse.jdt.internal.compiler.impl.Constant;
/*      */ import org.eclipse.jdt.internal.compiler.lookup.ArrayBinding;
/*      */ import org.eclipse.jdt.internal.compiler.lookup.Binding;
/*      */ import org.eclipse.jdt.internal.compiler.lookup.BlockScope;
/*      */ import org.eclipse.jdt.internal.compiler.lookup.ClassScope;
/*      */ import org.eclipse.jdt.internal.compiler.lookup.FieldBinding;
/*      */ import org.eclipse.jdt.internal.compiler.lookup.LocalVariableBinding;
/*      */ import org.eclipse.jdt.internal.compiler.lookup.LookupEnvironment;
/*      */ import org.eclipse.jdt.internal.compiler.lookup.MethodBinding;
/*      */ import org.eclipse.jdt.internal.compiler.lookup.MethodScope;
/*      */ import org.eclipse.jdt.internal.compiler.lookup.NestedTypeBinding;
/*      */ import org.eclipse.jdt.internal.compiler.lookup.ReferenceBinding;
/*      */ import org.eclipse.jdt.internal.compiler.lookup.Scope;
/*      */ import org.eclipse.jdt.internal.compiler.lookup.SourceTypeBinding;
/*      */ import org.eclipse.jdt.internal.compiler.lookup.SyntheticArgumentBinding;
/*      */ import org.eclipse.jdt.internal.compiler.lookup.SyntheticMethodBinding;
/*      */ import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;
/*      */ import org.eclipse.jdt.internal.compiler.lookup.TypeConstants;
/*      */ import org.eclipse.jdt.internal.compiler.lookup.VariableBinding;
/*      */ import org.eclipse.jdt.internal.compiler.problem.AbortMethod;
/*      */ import org.eclipse.jdt.internal.compiler.problem.ProblemReporter;
/*      */ import org.eclipse.jdt.internal.compiler.util.Util;
/*      */ 
/*      */ public class CodeStream
/*      */ {
/*   35 */   public static FieldBinding[] ImplicitThis = new FieldBinding[0];
/*      */   public static final int LABELS_INCREMENT = 5;
/*      */   public static final int LOCALS_INCREMENT = 10;
/*   39 */   static ExceptionLabel[] noExceptionHandlers = new ExceptionLabel[5];
/*   40 */   static BranchLabel[] noLabels = new BranchLabel[5];
/*   41 */   static LocalVariableBinding[] noLocals = new LocalVariableBinding[10];
/*   42 */   static LocalVariableBinding[] noVisibleLocals = new LocalVariableBinding[10];
/*   43 */   public static final CompilationResult RESTART_IN_WIDE_MODE = new CompilationResult(null, 0, 0, 0);
/*      */   public int allLocalsCounter;
/*      */   public byte[] bCodeStream;
/*      */   public ClassFile classFile;
/*      */   public int classFileOffset;
/*      */   public ConstantPool constantPool;
/*      */   public int countLabels;
/*   51 */   public ExceptionLabel[] exceptionLabels = new ExceptionLabel[5];
/*      */   public int exceptionLabelsCounter;
/*      */   public int generateAttributes;
/*      */   static final int L_UNKNOWN = 0;
/*      */   static final int L_OPTIMIZABLE = 2;
/*      */   static final int L_CANNOT_OPTIMIZE = 4;
/*   57 */   public BranchLabel[] labels = new BranchLabel[5];
/*      */   public int lastEntryPC;
/*      */   public int lastAbruptCompletion;
/*      */   public int[] lineSeparatorPositions;
/*      */   public int lineNumberStart;
/*      */   public int lineNumberEnd;
/*   66 */   public LocalVariableBinding[] locals = new LocalVariableBinding[10];
/*      */   public int maxFieldCount;
/*      */   public int maxLocals;
/*      */   public AbstractMethodDeclaration methodDeclaration;
/*   70 */   public int[] pcToSourceMap = new int[24];
/*      */   public int pcToSourceMapSize;
/*      */   public int position;
/*      */   public boolean preserveUnusedLocals;
/*      */   public int stackDepth;
/*      */   public int stackMax;
/*      */   public int startingClassFileOffset;
/*      */   protected long targetLevel;
/*   82 */   public LocalVariableBinding[] visibleLocals = new LocalVariableBinding[10];
/*      */   int visibleLocalsCount;
/*   87 */   public boolean wideMode = false;
/*      */ 
/*      */   public CodeStream(ClassFile givenClassFile) {
/*   90 */     this.targetLevel = givenClassFile.targetJDK;
/*   91 */     this.generateAttributes = givenClassFile.produceAttributes;
/*   92 */     if ((givenClassFile.produceAttributes & 0x2) != 0)
/*   93 */       this.lineSeparatorPositions = givenClassFile.referenceBinding.scope.referenceCompilationUnit().compilationResult.getLineSeparatorPositions();
/*      */   }
/*      */ 
/*      */   public static int insertionIndex(int[] pcToSourceMap, int length, int pc)
/*      */   {
/*  108 */     int g = 0;
/*  109 */     int d = length - 2;
/*  110 */     int m = 0;
/*  111 */     while (g <= d) {
/*  112 */       m = (g + d) / 2;
/*      */ 
/*  114 */       if ((m & 0x1) != 0)
/*  115 */         m--;
/*  116 */       int currentPC = pcToSourceMap[m];
/*  117 */       if (pc < currentPC) {
/*  118 */         d = m - 2;
/*      */       }
/*  120 */       else if (pc > currentPC)
/*  121 */         g = m + 2;
/*      */       else {
/*  123 */         return -1;
/*      */       }
/*      */     }
/*  126 */     if (pc < pcToSourceMap[m])
/*  127 */       return m;
/*  128 */     return m + 2;
/*      */   }
/*      */   public static final void sort(int[] tab, int lo0, int hi0, int[] result) {
/*  131 */     int lo = lo0;
/*  132 */     int hi = hi0;
/*      */ 
/*  134 */     if (hi0 > lo0) {
/*  138 */       int mid = tab[(lo0 + (hi0 - lo0) / 2)];
/*      */ 
/*  140 */       break label86;
/*      */ 
/*  145 */       lo++;
/*      */       label86: 
/*      */       do { if (lo < hi0) if (tab[lo] < mid)
/*      */           {
/*      */             break;
/*      */           }
/*      */ 
/*  149 */         while ((hi > lo0) && (tab[hi] > mid)) {
/*  150 */           hi--;
/*      */         }
/*  152 */         if (lo <= hi) {
/*  153 */           swap(tab, lo, hi, result);
/*  154 */           lo++;
/*  155 */           hi--;
/*      */         }
/*      */       }
/*  140 */       while (lo <= hi);
/*      */ 
/*  161 */       if (lo0 < hi) {
/*  162 */         sort(tab, lo0, hi, result);
/*      */       }
/*      */ 
/*  166 */       if (lo < hi0)
/*  167 */         sort(tab, lo, hi0, result);
/*      */     }
/*      */   }
/*      */ 
/*      */   private static final void swap(int[] a, int i, int j, int[] result)
/*      */   {
/*  174 */     int T = a[i];
/*  175 */     a[i] = a[j];
/*  176 */     a[j] = T;
/*  177 */     T = result[j];
/*  178 */     result[j] = result[i];
/*  179 */     result[i] = T;
/*      */   }
/*      */ 
/*      */   public void aaload() {
/*  183 */     this.countLabels = 0;
/*  184 */     this.stackDepth -= 1;
/*  185 */     if (this.classFileOffset >= this.bCodeStream.length) {
/*  186 */       resizeByteArray();
/*      */     }
/*  188 */     this.position += 1;
/*  189 */     this.bCodeStream[(this.classFileOffset++)] = 50;
/*      */   }
/*      */ 
/*      */   public void aastore() {
/*  193 */     this.countLabels = 0;
/*  194 */     this.stackDepth -= 3;
/*  195 */     if (this.classFileOffset >= this.bCodeStream.length) {
/*  196 */       resizeByteArray();
/*      */     }
/*  198 */     this.position += 1;
/*  199 */     this.bCodeStream[(this.classFileOffset++)] = 83;
/*      */   }
/*      */ 
/*      */   public void aconst_null() {
/*  203 */     this.countLabels = 0;
/*  204 */     this.stackDepth += 1;
/*  205 */     if (this.stackDepth > this.stackMax) {
/*  206 */       this.stackMax = this.stackDepth;
/*      */     }
/*  208 */     if (this.classFileOffset >= this.bCodeStream.length) {
/*  209 */       resizeByteArray();
/*      */     }
/*  211 */     this.position += 1;
/*  212 */     this.bCodeStream[(this.classFileOffset++)] = 1;
/*      */   }
/*      */ 
/*      */   public void addDefinitelyAssignedVariables(Scope scope, int initStateIndex)
/*      */   {
/*  217 */     if ((this.generateAttributes & 0x1C) == 0)
/*      */     {
/*  220 */       return;
/*  221 */     }for (int i = 0; i < this.visibleLocalsCount; i++) {
/*  222 */       LocalVariableBinding localBinding = this.visibleLocals[i];
/*  223 */       if (localBinding == null)
/*      */         continue;
/*  225 */       if ((!isDefinitelyAssigned(scope, initStateIndex, localBinding)) || (
/*  226 */         (localBinding.initializationCount != 0) && (localBinding.initializationPCs[((localBinding.initializationCount - 1 << 1) + 1)] == -1)))
/*      */       {
/*      */         continue;
/*      */       }
/*      */ 
/*  236 */       localBinding.recordInitializationStartPC(this.position);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void addLabel(BranchLabel aLabel)
/*      */   {
/*  244 */     if (this.countLabels == this.labels.length)
/*  245 */       System.arraycopy(this.labels, 0, this.labels = new BranchLabel[this.countLabels + 5], 0, this.countLabels);
/*  246 */     this.labels[(this.countLabels++)] = aLabel;
/*      */   }
/*      */ 
/*      */   public void addVariable(LocalVariableBinding localBinding)
/*      */   {
/*      */   }
/*      */ 
/*      */   public void addVisibleLocalVariable(LocalVariableBinding localBinding) {
/*  254 */     if ((this.generateAttributes & 0x1C) == 0)
/*      */     {
/*  257 */       return;
/*      */     }
/*  259 */     if (this.visibleLocalsCount >= this.visibleLocals.length)
/*  260 */       System.arraycopy(this.visibleLocals, 0, this.visibleLocals = new LocalVariableBinding[this.visibleLocalsCount * 2], 0, this.visibleLocalsCount);
/*  261 */     this.visibleLocals[(this.visibleLocalsCount++)] = localBinding;
/*      */   }
/*      */ 
/*      */   public void aload(int iArg) {
/*  265 */     this.countLabels = 0;
/*  266 */     this.stackDepth += 1;
/*  267 */     if (this.stackDepth > this.stackMax)
/*  268 */       this.stackMax = this.stackDepth;
/*  269 */     if (this.maxLocals <= iArg) {
/*  270 */       this.maxLocals = (iArg + 1);
/*      */     }
/*  272 */     if (iArg > 255) {
/*  273 */       if (this.classFileOffset + 3 >= this.bCodeStream.length) {
/*  274 */         resizeByteArray();
/*      */       }
/*  276 */       this.position += 2;
/*  277 */       this.bCodeStream[(this.classFileOffset++)] = -60;
/*  278 */       this.bCodeStream[(this.classFileOffset++)] = 25;
/*  279 */       writeUnsignedShort(iArg);
/*      */     }
/*      */     else {
/*  282 */       if (this.classFileOffset + 1 >= this.bCodeStream.length) {
/*  283 */         resizeByteArray();
/*      */       }
/*  285 */       this.position += 2;
/*  286 */       this.bCodeStream[(this.classFileOffset++)] = 25;
/*  287 */       this.bCodeStream[(this.classFileOffset++)] = (byte)iArg;
/*      */     }
/*      */   }
/*      */ 
/*      */   public void aload_0() {
/*  292 */     this.countLabels = 0;
/*  293 */     this.stackDepth += 1;
/*  294 */     if (this.stackDepth > this.stackMax) {
/*  295 */       this.stackMax = this.stackDepth;
/*      */     }
/*  297 */     if (this.maxLocals == 0) {
/*  298 */       this.maxLocals = 1;
/*      */     }
/*  300 */     if (this.classFileOffset >= this.bCodeStream.length) {
/*  301 */       resizeByteArray();
/*      */     }
/*  303 */     this.position += 1;
/*  304 */     this.bCodeStream[(this.classFileOffset++)] = 42;
/*      */   }
/*      */ 
/*      */   public void aload_1() {
/*  308 */     this.countLabels = 0;
/*  309 */     this.stackDepth += 1;
/*  310 */     if (this.stackDepth > this.stackMax)
/*  311 */       this.stackMax = this.stackDepth;
/*  312 */     if (this.maxLocals <= 1) {
/*  313 */       this.maxLocals = 2;
/*      */     }
/*  315 */     if (this.classFileOffset >= this.bCodeStream.length) {
/*  316 */       resizeByteArray();
/*      */     }
/*  318 */     this.position += 1;
/*  319 */     this.bCodeStream[(this.classFileOffset++)] = 43;
/*      */   }
/*      */ 
/*      */   public void aload_2() {
/*  323 */     this.countLabels = 0;
/*  324 */     this.stackDepth += 1;
/*  325 */     if (this.stackDepth > this.stackMax)
/*  326 */       this.stackMax = this.stackDepth;
/*  327 */     if (this.maxLocals <= 2) {
/*  328 */       this.maxLocals = 3;
/*      */     }
/*  330 */     if (this.classFileOffset >= this.bCodeStream.length) {
/*  331 */       resizeByteArray();
/*      */     }
/*  333 */     this.position += 1;
/*  334 */     this.bCodeStream[(this.classFileOffset++)] = 44;
/*      */   }
/*      */ 
/*      */   public void aload_3() {
/*  338 */     this.countLabels = 0;
/*  339 */     this.stackDepth += 1;
/*  340 */     if (this.stackDepth > this.stackMax)
/*  341 */       this.stackMax = this.stackDepth;
/*  342 */     if (this.maxLocals <= 3) {
/*  343 */       this.maxLocals = 4;
/*      */     }
/*  345 */     if (this.classFileOffset >= this.bCodeStream.length) {
/*  346 */       resizeByteArray();
/*      */     }
/*  348 */     this.position += 1;
/*  349 */     this.bCodeStream[(this.classFileOffset++)] = 45;
/*      */   }
/*      */ 
/*      */   public void anewarray(TypeBinding typeBinding) {
/*  353 */     this.countLabels = 0;
/*  354 */     if (this.classFileOffset + 2 >= this.bCodeStream.length) {
/*  355 */       resizeByteArray();
/*      */     }
/*  357 */     this.position += 1;
/*  358 */     this.bCodeStream[(this.classFileOffset++)] = -67;
/*  359 */     writeUnsignedShort(this.constantPool.literalIndexForType(typeBinding));
/*      */   }
/*      */ 
/*      */   public void areturn() {
/*  363 */     this.countLabels = 0;
/*  364 */     this.stackDepth -= 1;
/*      */ 
/*  366 */     if (this.classFileOffset >= this.bCodeStream.length) {
/*  367 */       resizeByteArray();
/*      */     }
/*  369 */     this.position += 1;
/*  370 */     this.bCodeStream[(this.classFileOffset++)] = -80;
/*  371 */     this.lastAbruptCompletion = this.position;
/*      */   }
/*      */ 
/*      */   public void arrayAt(int typeBindingID) {
/*  375 */     switch (typeBindingID) {
/*      */     case 10:
/*  377 */       iaload();
/*  378 */       break;
/*      */     case 3:
/*      */     case 5:
/*  381 */       baload();
/*  382 */       break;
/*      */     case 4:
/*  384 */       saload();
/*  385 */       break;
/*      */     case 2:
/*  387 */       caload();
/*  388 */       break;
/*      */     case 7:
/*  390 */       laload();
/*  391 */       break;
/*      */     case 9:
/*  393 */       faload();
/*  394 */       break;
/*      */     case 8:
/*  396 */       daload();
/*  397 */       break;
/*      */     case 6:
/*      */     default:
/*  399 */       aaload();
/*      */     }
/*      */   }
/*      */ 
/*      */   public void arrayAtPut(int elementTypeID, boolean valueRequired) {
/*  404 */     switch (elementTypeID) {
/*      */     case 10:
/*  406 */       if (valueRequired)
/*  407 */         dup_x2();
/*  408 */       iastore();
/*  409 */       break;
/*      */     case 3:
/*      */     case 5:
/*  412 */       if (valueRequired)
/*  413 */         dup_x2();
/*  414 */       bastore();
/*  415 */       break;
/*      */     case 4:
/*  417 */       if (valueRequired)
/*  418 */         dup_x2();
/*  419 */       sastore();
/*  420 */       break;
/*      */     case 2:
/*  422 */       if (valueRequired)
/*  423 */         dup_x2();
/*  424 */       castore();
/*  425 */       break;
/*      */     case 7:
/*  427 */       if (valueRequired)
/*  428 */         dup2_x2();
/*  429 */       lastore();
/*  430 */       break;
/*      */     case 9:
/*  432 */       if (valueRequired)
/*  433 */         dup_x2();
/*  434 */       fastore();
/*  435 */       break;
/*      */     case 8:
/*  437 */       if (valueRequired)
/*  438 */         dup2_x2();
/*  439 */       dastore();
/*  440 */       break;
/*      */     case 6:
/*      */     default:
/*  442 */       if (valueRequired)
/*  443 */         dup_x2();
/*  444 */       aastore();
/*      */     }
/*      */   }
/*      */ 
/*      */   public void arraylength() {
/*  449 */     this.countLabels = 0;
/*  450 */     if (this.classFileOffset >= this.bCodeStream.length) {
/*  451 */       resizeByteArray();
/*      */     }
/*  453 */     this.position += 1;
/*  454 */     this.bCodeStream[(this.classFileOffset++)] = -66;
/*      */   }
/*      */ 
/*      */   public void astore(int iArg) {
/*  458 */     this.countLabels = 0;
/*  459 */     this.stackDepth -= 1;
/*  460 */     if (this.maxLocals <= iArg) {
/*  461 */       this.maxLocals = (iArg + 1);
/*      */     }
/*  463 */     if (iArg > 255) {
/*  464 */       if (this.classFileOffset + 3 >= this.bCodeStream.length) {
/*  465 */         resizeByteArray();
/*      */       }
/*  467 */       this.position += 2;
/*  468 */       this.bCodeStream[(this.classFileOffset++)] = -60;
/*  469 */       this.bCodeStream[(this.classFileOffset++)] = 58;
/*  470 */       writeUnsignedShort(iArg);
/*      */     } else {
/*  472 */       if (this.classFileOffset + 1 >= this.bCodeStream.length) {
/*  473 */         resizeByteArray();
/*      */       }
/*  475 */       this.position += 2;
/*  476 */       this.bCodeStream[(this.classFileOffset++)] = 58;
/*  477 */       this.bCodeStream[(this.classFileOffset++)] = (byte)iArg;
/*      */     }
/*      */   }
/*      */ 
/*      */   public void astore_0() {
/*  482 */     this.countLabels = 0;
/*  483 */     this.stackDepth -= 1;
/*  484 */     if (this.maxLocals == 0) {
/*  485 */       this.maxLocals = 1;
/*      */     }
/*  487 */     if (this.classFileOffset >= this.bCodeStream.length) {
/*  488 */       resizeByteArray();
/*      */     }
/*  490 */     this.position += 1;
/*  491 */     this.bCodeStream[(this.classFileOffset++)] = 75;
/*      */   }
/*      */ 
/*      */   public void astore_1() {
/*  495 */     this.countLabels = 0;
/*  496 */     this.stackDepth -= 1;
/*  497 */     if (this.maxLocals <= 1) {
/*  498 */       this.maxLocals = 2;
/*      */     }
/*  500 */     if (this.classFileOffset >= this.bCodeStream.length) {
/*  501 */       resizeByteArray();
/*      */     }
/*  503 */     this.position += 1;
/*  504 */     this.bCodeStream[(this.classFileOffset++)] = 76;
/*      */   }
/*      */ 
/*      */   public void astore_2() {
/*  508 */     this.countLabels = 0;
/*  509 */     this.stackDepth -= 1;
/*  510 */     if (this.maxLocals <= 2) {
/*  511 */       this.maxLocals = 3;
/*      */     }
/*  513 */     if (this.classFileOffset >= this.bCodeStream.length) {
/*  514 */       resizeByteArray();
/*      */     }
/*  516 */     this.position += 1;
/*  517 */     this.bCodeStream[(this.classFileOffset++)] = 77;
/*      */   }
/*      */ 
/*      */   public void astore_3() {
/*  521 */     this.countLabels = 0;
/*  522 */     this.stackDepth -= 1;
/*  523 */     if (this.maxLocals <= 3) {
/*  524 */       this.maxLocals = 4;
/*      */     }
/*  526 */     if (this.classFileOffset >= this.bCodeStream.length) {
/*  527 */       resizeByteArray();
/*      */     }
/*  529 */     this.position += 1;
/*  530 */     this.bCodeStream[(this.classFileOffset++)] = 78;
/*      */   }
/*      */ 
/*      */   public void athrow() {
/*  534 */     this.countLabels = 0;
/*  535 */     this.stackDepth -= 1;
/*  536 */     if (this.classFileOffset >= this.bCodeStream.length) {
/*  537 */       resizeByteArray();
/*      */     }
/*  539 */     this.position += 1;
/*  540 */     this.bCodeStream[(this.classFileOffset++)] = -65;
/*  541 */     this.lastAbruptCompletion = this.position;
/*      */   }
/*      */ 
/*      */   public void baload() {
/*  545 */     this.countLabels = 0;
/*  546 */     this.stackDepth -= 1;
/*  547 */     if (this.classFileOffset >= this.bCodeStream.length) {
/*  548 */       resizeByteArray();
/*      */     }
/*  550 */     this.position += 1;
/*  551 */     this.bCodeStream[(this.classFileOffset++)] = 51;
/*      */   }
/*      */ 
/*      */   public void bastore() {
/*  555 */     this.countLabels = 0;
/*  556 */     this.stackDepth -= 3;
/*  557 */     if (this.classFileOffset >= this.bCodeStream.length) {
/*  558 */       resizeByteArray();
/*      */     }
/*  560 */     this.position += 1;
/*  561 */     this.bCodeStream[(this.classFileOffset++)] = 84;
/*      */   }
/*      */ 
/*      */   public void bipush(byte b) {
/*  565 */     this.countLabels = 0;
/*  566 */     this.stackDepth += 1;
/*  567 */     if (this.stackDepth > this.stackMax)
/*  568 */       this.stackMax = this.stackDepth;
/*  569 */     if (this.classFileOffset + 1 >= this.bCodeStream.length) {
/*  570 */       resizeByteArray();
/*      */     }
/*  572 */     this.position += 2;
/*  573 */     this.bCodeStream[(this.classFileOffset++)] = 16;
/*  574 */     this.bCodeStream[(this.classFileOffset++)] = b;
/*      */   }
/*      */ 
/*      */   public void caload() {
/*  578 */     this.countLabels = 0;
/*  579 */     this.stackDepth -= 1;
/*  580 */     if (this.classFileOffset >= this.bCodeStream.length) {
/*  581 */       resizeByteArray();
/*      */     }
/*  583 */     this.position += 1;
/*  584 */     this.bCodeStream[(this.classFileOffset++)] = 52;
/*      */   }
/*      */ 
/*      */   public void castore() {
/*  588 */     this.countLabels = 0;
/*  589 */     this.stackDepth -= 3;
/*  590 */     if (this.classFileOffset >= this.bCodeStream.length) {
/*  591 */       resizeByteArray();
/*      */     }
/*  593 */     this.position += 1;
/*  594 */     this.bCodeStream[(this.classFileOffset++)] = 85;
/*      */   }
/*      */ 
/*      */   public void checkcast(int baseId) {
/*  598 */     this.countLabels = 0;
/*  599 */     if (this.classFileOffset + 2 >= this.bCodeStream.length) {
/*  600 */       resizeByteArray();
/*      */     }
/*  602 */     this.position += 1;
/*  603 */     this.bCodeStream[(this.classFileOffset++)] = -64;
/*  604 */     switch (baseId) {
/*      */     case 3:
/*  606 */       writeUnsignedShort(this.constantPool.literalIndexForType(ConstantPool.JavaLangByteConstantPoolName));
/*  607 */       break;
/*      */     case 4:
/*  609 */       writeUnsignedShort(this.constantPool.literalIndexForType(ConstantPool.JavaLangShortConstantPoolName));
/*  610 */       break;
/*      */     case 2:
/*  612 */       writeUnsignedShort(this.constantPool.literalIndexForType(ConstantPool.JavaLangCharacterConstantPoolName));
/*  613 */       break;
/*      */     case 10:
/*  615 */       writeUnsignedShort(this.constantPool.literalIndexForType(ConstantPool.JavaLangIntegerConstantPoolName));
/*  616 */       break;
/*      */     case 7:
/*  618 */       writeUnsignedShort(this.constantPool.literalIndexForType(ConstantPool.JavaLangLongConstantPoolName));
/*  619 */       break;
/*      */     case 9:
/*  621 */       writeUnsignedShort(this.constantPool.literalIndexForType(ConstantPool.JavaLangFloatConstantPoolName));
/*  622 */       break;
/*      */     case 8:
/*  624 */       writeUnsignedShort(this.constantPool.literalIndexForType(ConstantPool.JavaLangDoubleConstantPoolName));
/*  625 */       break;
/*      */     case 5:
/*  627 */       writeUnsignedShort(this.constantPool.literalIndexForType(ConstantPool.JavaLangBooleanConstantPoolName));
/*      */     case 6:
/*      */     }
/*      */   }
/*      */ 
/*      */   public void checkcast(TypeBinding typeBinding) {
/*  632 */     this.countLabels = 0;
/*  633 */     if (this.classFileOffset + 2 >= this.bCodeStream.length) {
/*  634 */       resizeByteArray();
/*      */     }
/*  636 */     this.position += 1;
/*  637 */     this.bCodeStream[(this.classFileOffset++)] = -64;
/*  638 */     writeUnsignedShort(this.constantPool.literalIndexForType(typeBinding));
/*      */   }
/*      */ 
/*      */   public void d2f() {
/*  642 */     this.countLabels = 0;
/*  643 */     this.stackDepth -= 1;
/*  644 */     if (this.classFileOffset >= this.bCodeStream.length) {
/*  645 */       resizeByteArray();
/*      */     }
/*  647 */     this.position += 1;
/*  648 */     this.bCodeStream[(this.classFileOffset++)] = -112;
/*      */   }
/*      */ 
/*      */   public void d2i() {
/*  652 */     this.countLabels = 0;
/*  653 */     this.stackDepth -= 1;
/*  654 */     if (this.classFileOffset >= this.bCodeStream.length) {
/*  655 */       resizeByteArray();
/*      */     }
/*  657 */     this.position += 1;
/*  658 */     this.bCodeStream[(this.classFileOffset++)] = -114;
/*      */   }
/*      */ 
/*      */   public void d2l() {
/*  662 */     this.countLabels = 0;
/*  663 */     if (this.classFileOffset >= this.bCodeStream.length) {
/*  664 */       resizeByteArray();
/*      */     }
/*  666 */     this.position += 1;
/*  667 */     this.bCodeStream[(this.classFileOffset++)] = -113;
/*      */   }
/*      */ 
/*      */   public void dadd() {
/*  671 */     this.countLabels = 0;
/*  672 */     this.stackDepth -= 2;
/*  673 */     if (this.classFileOffset >= this.bCodeStream.length) {
/*  674 */       resizeByteArray();
/*      */     }
/*  676 */     this.position += 1;
/*  677 */     this.bCodeStream[(this.classFileOffset++)] = 99;
/*      */   }
/*      */ 
/*      */   public void daload() {
/*  681 */     this.countLabels = 0;
/*  682 */     if (this.classFileOffset >= this.bCodeStream.length) {
/*  683 */       resizeByteArray();
/*      */     }
/*  685 */     this.position += 1;
/*  686 */     this.bCodeStream[(this.classFileOffset++)] = 49;
/*      */   }
/*      */ 
/*      */   public void dastore() {
/*  690 */     this.countLabels = 0;
/*  691 */     this.stackDepth -= 4;
/*  692 */     if (this.classFileOffset >= this.bCodeStream.length) {
/*  693 */       resizeByteArray();
/*      */     }
/*  695 */     this.position += 1;
/*  696 */     this.bCodeStream[(this.classFileOffset++)] = 82;
/*      */   }
/*      */ 
/*      */   public void dcmpg() {
/*  700 */     this.countLabels = 0;
/*  701 */     this.stackDepth -= 3;
/*  702 */     if (this.classFileOffset >= this.bCodeStream.length) {
/*  703 */       resizeByteArray();
/*      */     }
/*  705 */     this.position += 1;
/*  706 */     this.bCodeStream[(this.classFileOffset++)] = -104;
/*      */   }
/*      */ 
/*      */   public void dcmpl() {
/*  710 */     this.countLabels = 0;
/*  711 */     this.stackDepth -= 3;
/*  712 */     if (this.classFileOffset >= this.bCodeStream.length) {
/*  713 */       resizeByteArray();
/*      */     }
/*  715 */     this.position += 1;
/*  716 */     this.bCodeStream[(this.classFileOffset++)] = -105;
/*      */   }
/*      */ 
/*      */   public void dconst_0() {
/*  720 */     this.countLabels = 0;
/*  721 */     this.stackDepth += 2;
/*  722 */     if (this.stackDepth > this.stackMax)
/*  723 */       this.stackMax = this.stackDepth;
/*  724 */     if (this.classFileOffset >= this.bCodeStream.length) {
/*  725 */       resizeByteArray();
/*      */     }
/*  727 */     this.position += 1;
/*  728 */     this.bCodeStream[(this.classFileOffset++)] = 14;
/*      */   }
/*      */ 
/*      */   public void dconst_1() {
/*  732 */     this.countLabels = 0;
/*  733 */     this.stackDepth += 2;
/*  734 */     if (this.stackDepth > this.stackMax)
/*  735 */       this.stackMax = this.stackDepth;
/*  736 */     if (this.classFileOffset >= this.bCodeStream.length) {
/*  737 */       resizeByteArray();
/*      */     }
/*  739 */     this.position += 1;
/*  740 */     this.bCodeStream[(this.classFileOffset++)] = 15;
/*      */   }
/*      */ 
/*      */   public void ddiv() {
/*  744 */     this.countLabels = 0;
/*  745 */     this.stackDepth -= 2;
/*  746 */     if (this.classFileOffset >= this.bCodeStream.length) {
/*  747 */       resizeByteArray();
/*      */     }
/*  749 */     this.position += 1;
/*  750 */     this.bCodeStream[(this.classFileOffset++)] = 111;
/*      */   }
/*      */ 
/*      */   public void decrStackSize(int offset) {
/*  754 */     this.stackDepth -= offset;
/*      */   }
/*      */ 
/*      */   public void dload(int iArg) {
/*  758 */     this.countLabels = 0;
/*  759 */     this.stackDepth += 2;
/*  760 */     if (this.stackDepth > this.stackMax)
/*  761 */       this.stackMax = this.stackDepth;
/*  762 */     if (this.maxLocals < iArg + 2) {
/*  763 */       this.maxLocals = (iArg + 2);
/*      */     }
/*  765 */     if (iArg > 255) {
/*  766 */       if (this.classFileOffset + 3 >= this.bCodeStream.length) {
/*  767 */         resizeByteArray();
/*      */       }
/*  769 */       this.position += 2;
/*  770 */       this.bCodeStream[(this.classFileOffset++)] = -60;
/*  771 */       this.bCodeStream[(this.classFileOffset++)] = 24;
/*  772 */       writeUnsignedShort(iArg);
/*      */     }
/*      */     else {
/*  775 */       if (this.classFileOffset + 1 >= this.bCodeStream.length) {
/*  776 */         resizeByteArray();
/*      */       }
/*  778 */       this.position += 2;
/*  779 */       this.bCodeStream[(this.classFileOffset++)] = 24;
/*  780 */       this.bCodeStream[(this.classFileOffset++)] = (byte)iArg;
/*      */     }
/*      */   }
/*      */ 
/*      */   public void dload_0() {
/*  785 */     this.countLabels = 0;
/*  786 */     this.stackDepth += 2;
/*  787 */     if (this.stackDepth > this.stackMax)
/*  788 */       this.stackMax = this.stackDepth;
/*  789 */     if (this.maxLocals < 2) {
/*  790 */       this.maxLocals = 2;
/*      */     }
/*  792 */     if (this.classFileOffset >= this.bCodeStream.length) {
/*  793 */       resizeByteArray();
/*      */     }
/*  795 */     this.position += 1;
/*  796 */     this.bCodeStream[(this.classFileOffset++)] = 38;
/*      */   }
/*      */ 
/*      */   public void dload_1() {
/*  800 */     this.countLabels = 0;
/*  801 */     this.stackDepth += 2;
/*  802 */     if (this.stackDepth > this.stackMax)
/*  803 */       this.stackMax = this.stackDepth;
/*  804 */     if (this.maxLocals < 3) {
/*  805 */       this.maxLocals = 3;
/*      */     }
/*  807 */     if (this.classFileOffset >= this.bCodeStream.length) {
/*  808 */       resizeByteArray();
/*      */     }
/*  810 */     this.position += 1;
/*  811 */     this.bCodeStream[(this.classFileOffset++)] = 39;
/*      */   }
/*      */ 
/*      */   public void dload_2() {
/*  815 */     this.countLabels = 0;
/*  816 */     this.stackDepth += 2;
/*  817 */     if (this.stackDepth > this.stackMax)
/*  818 */       this.stackMax = this.stackDepth;
/*  819 */     if (this.maxLocals < 4) {
/*  820 */       this.maxLocals = 4;
/*      */     }
/*  822 */     if (this.classFileOffset >= this.bCodeStream.length) {
/*  823 */       resizeByteArray();
/*      */     }
/*  825 */     this.position += 1;
/*  826 */     this.bCodeStream[(this.classFileOffset++)] = 40;
/*      */   }
/*      */ 
/*      */   public void dload_3() {
/*  830 */     this.countLabels = 0;
/*  831 */     this.stackDepth += 2;
/*  832 */     if (this.stackDepth > this.stackMax)
/*  833 */       this.stackMax = this.stackDepth;
/*  834 */     if (this.maxLocals < 5) {
/*  835 */       this.maxLocals = 5;
/*      */     }
/*  837 */     if (this.classFileOffset >= this.bCodeStream.length) {
/*  838 */       resizeByteArray();
/*      */     }
/*  840 */     this.position += 1;
/*  841 */     this.bCodeStream[(this.classFileOffset++)] = 41;
/*      */   }
/*      */ 
/*      */   public void dmul() {
/*  845 */     this.countLabels = 0;
/*  846 */     this.stackDepth -= 2;
/*  847 */     if (this.classFileOffset >= this.bCodeStream.length) {
/*  848 */       resizeByteArray();
/*      */     }
/*  850 */     this.position += 1;
/*  851 */     this.bCodeStream[(this.classFileOffset++)] = 107;
/*      */   }
/*      */ 
/*      */   public void dneg() {
/*  855 */     this.countLabels = 0;
/*  856 */     if (this.classFileOffset >= this.bCodeStream.length) {
/*  857 */       resizeByteArray();
/*      */     }
/*  859 */     this.position += 1;
/*  860 */     this.bCodeStream[(this.classFileOffset++)] = 119;
/*      */   }
/*      */ 
/*      */   public void drem() {
/*  864 */     this.countLabels = 0;
/*  865 */     this.stackDepth -= 2;
/*  866 */     if (this.classFileOffset >= this.bCodeStream.length) {
/*  867 */       resizeByteArray();
/*      */     }
/*  869 */     this.position += 1;
/*  870 */     this.bCodeStream[(this.classFileOffset++)] = 115;
/*      */   }
/*      */ 
/*      */   public void dreturn() {
/*  874 */     this.countLabels = 0;
/*  875 */     this.stackDepth -= 2;
/*      */ 
/*  877 */     if (this.classFileOffset >= this.bCodeStream.length) {
/*  878 */       resizeByteArray();
/*      */     }
/*  880 */     this.position += 1;
/*  881 */     this.bCodeStream[(this.classFileOffset++)] = -81;
/*  882 */     this.lastAbruptCompletion = this.position;
/*      */   }
/*      */ 
/*      */   public void dstore(int iArg) {
/*  886 */     this.countLabels = 0;
/*  887 */     this.stackDepth -= 2;
/*  888 */     if (this.maxLocals <= iArg + 1) {
/*  889 */       this.maxLocals = (iArg + 2);
/*      */     }
/*  891 */     if (iArg > 255) {
/*  892 */       if (this.classFileOffset + 3 >= this.bCodeStream.length) {
/*  893 */         resizeByteArray();
/*      */       }
/*  895 */       this.position += 2;
/*  896 */       this.bCodeStream[(this.classFileOffset++)] = -60;
/*  897 */       this.bCodeStream[(this.classFileOffset++)] = 57;
/*  898 */       writeUnsignedShort(iArg);
/*      */     } else {
/*  900 */       if (this.classFileOffset + 1 >= this.bCodeStream.length) {
/*  901 */         resizeByteArray();
/*      */       }
/*  903 */       this.position += 2;
/*  904 */       this.bCodeStream[(this.classFileOffset++)] = 57;
/*  905 */       this.bCodeStream[(this.classFileOffset++)] = (byte)iArg;
/*      */     }
/*      */   }
/*      */ 
/*      */   public void dstore_0() {
/*  910 */     this.countLabels = 0;
/*  911 */     this.stackDepth -= 2;
/*  912 */     if (this.maxLocals < 2) {
/*  913 */       this.maxLocals = 2;
/*      */     }
/*  915 */     if (this.classFileOffset >= this.bCodeStream.length) {
/*  916 */       resizeByteArray();
/*      */     }
/*  918 */     this.position += 1;
/*  919 */     this.bCodeStream[(this.classFileOffset++)] = 71;
/*      */   }
/*      */ 
/*      */   public void dstore_1() {
/*  923 */     this.countLabels = 0;
/*  924 */     this.stackDepth -= 2;
/*  925 */     if (this.maxLocals < 3) {
/*  926 */       this.maxLocals = 3;
/*      */     }
/*  928 */     if (this.classFileOffset >= this.bCodeStream.length) {
/*  929 */       resizeByteArray();
/*      */     }
/*  931 */     this.position += 1;
/*  932 */     this.bCodeStream[(this.classFileOffset++)] = 72;
/*      */   }
/*      */ 
/*      */   public void dstore_2() {
/*  936 */     this.countLabels = 0;
/*  937 */     this.stackDepth -= 2;
/*  938 */     if (this.maxLocals < 4) {
/*  939 */       this.maxLocals = 4;
/*      */     }
/*  941 */     if (this.classFileOffset >= this.bCodeStream.length) {
/*  942 */       resizeByteArray();
/*      */     }
/*  944 */     this.position += 1;
/*  945 */     this.bCodeStream[(this.classFileOffset++)] = 73;
/*      */   }
/*      */ 
/*      */   public void dstore_3() {
/*  949 */     this.countLabels = 0;
/*  950 */     this.stackDepth -= 2;
/*  951 */     if (this.maxLocals < 5) {
/*  952 */       this.maxLocals = 5;
/*      */     }
/*  954 */     if (this.classFileOffset >= this.bCodeStream.length) {
/*  955 */       resizeByteArray();
/*      */     }
/*  957 */     this.position += 1;
/*  958 */     this.bCodeStream[(this.classFileOffset++)] = 74;
/*      */   }
/*      */ 
/*      */   public void dsub() {
/*  962 */     this.countLabels = 0;
/*  963 */     this.stackDepth -= 2;
/*  964 */     if (this.classFileOffset >= this.bCodeStream.length) {
/*  965 */       resizeByteArray();
/*      */     }
/*  967 */     this.position += 1;
/*  968 */     this.bCodeStream[(this.classFileOffset++)] = 103;
/*      */   }
/*      */ 
/*      */   public void dup() {
/*  972 */     this.countLabels = 0;
/*  973 */     this.stackDepth += 1;
/*  974 */     if (this.stackDepth > this.stackMax) {
/*  975 */       this.stackMax = this.stackDepth;
/*      */     }
/*  977 */     if (this.classFileOffset >= this.bCodeStream.length) {
/*  978 */       resizeByteArray();
/*      */     }
/*  980 */     this.position += 1;
/*  981 */     this.bCodeStream[(this.classFileOffset++)] = 89;
/*      */   }
/*      */ 
/*      */   public void dup_x1() {
/*  985 */     this.countLabels = 0;
/*  986 */     this.stackDepth += 1;
/*  987 */     if (this.stackDepth > this.stackMax)
/*  988 */       this.stackMax = this.stackDepth;
/*  989 */     if (this.classFileOffset >= this.bCodeStream.length) {
/*  990 */       resizeByteArray();
/*      */     }
/*  992 */     this.position += 1;
/*  993 */     this.bCodeStream[(this.classFileOffset++)] = 90;
/*      */   }
/*      */ 
/*      */   public void dup_x2() {
/*  997 */     this.countLabels = 0;
/*  998 */     this.stackDepth += 1;
/*  999 */     if (this.stackDepth > this.stackMax)
/* 1000 */       this.stackMax = this.stackDepth;
/* 1001 */     if (this.classFileOffset >= this.bCodeStream.length) {
/* 1002 */       resizeByteArray();
/*      */     }
/* 1004 */     this.position += 1;
/* 1005 */     this.bCodeStream[(this.classFileOffset++)] = 91;
/*      */   }
/*      */ 
/*      */   public void dup2() {
/* 1009 */     this.countLabels = 0;
/* 1010 */     this.stackDepth += 2;
/* 1011 */     if (this.stackDepth > this.stackMax)
/* 1012 */       this.stackMax = this.stackDepth;
/* 1013 */     if (this.classFileOffset >= this.bCodeStream.length) {
/* 1014 */       resizeByteArray();
/*      */     }
/* 1016 */     this.position += 1;
/* 1017 */     this.bCodeStream[(this.classFileOffset++)] = 92;
/*      */   }
/*      */ 
/*      */   public void dup2_x1() {
/* 1021 */     this.countLabels = 0;
/* 1022 */     this.stackDepth += 2;
/* 1023 */     if (this.stackDepth > this.stackMax)
/* 1024 */       this.stackMax = this.stackDepth;
/* 1025 */     if (this.classFileOffset >= this.bCodeStream.length) {
/* 1026 */       resizeByteArray();
/*      */     }
/* 1028 */     this.position += 1;
/* 1029 */     this.bCodeStream[(this.classFileOffset++)] = 93;
/*      */   }
/*      */ 
/*      */   public void dup2_x2() {
/* 1033 */     this.countLabels = 0;
/* 1034 */     this.stackDepth += 2;
/* 1035 */     if (this.stackDepth > this.stackMax)
/* 1036 */       this.stackMax = this.stackDepth;
/* 1037 */     if (this.classFileOffset >= this.bCodeStream.length) {
/* 1038 */       resizeByteArray();
/*      */     }
/* 1040 */     this.position += 1;
/* 1041 */     this.bCodeStream[(this.classFileOffset++)] = 94;
/*      */   }
/*      */ 
/*      */   public void exitUserScope(BlockScope currentScope)
/*      */   {
/* 1046 */     if ((this.generateAttributes & 0x1C) == 0)
/*      */     {
/* 1049 */       return;
/* 1050 */     }int index = this.visibleLocalsCount - 1;
/* 1051 */     while (index >= 0) {
/* 1052 */       LocalVariableBinding visibleLocal = this.visibleLocals[index];
/* 1053 */       if ((visibleLocal == null) || (visibleLocal.declaringScope != currentScope))
/*      */       {
/* 1055 */         index--;
/*      */       }
/*      */       else
/*      */       {
/* 1060 */         if (visibleLocal.initializationCount > 0) {
/* 1061 */           visibleLocal.recordInitializationEndPC(this.position);
/*      */         }
/* 1063 */         this.visibleLocals[(index--)] = null;
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   public void exitUserScope(BlockScope currentScope, LocalVariableBinding binding) {
/* 1069 */     if ((this.generateAttributes & 0x1C) == 0)
/*      */     {
/* 1072 */       return;
/* 1073 */     }int index = this.visibleLocalsCount - 1;
/* 1074 */     while (index >= 0) {
/* 1075 */       LocalVariableBinding visibleLocal = this.visibleLocals[index];
/* 1076 */       if ((visibleLocal == null) || (visibleLocal.declaringScope != currentScope) || (visibleLocal == binding))
/*      */       {
/* 1078 */         index--;
/*      */       }
/*      */       else
/*      */       {
/* 1082 */         if (visibleLocal.initializationCount > 0) {
/* 1083 */           visibleLocal.recordInitializationEndPC(this.position);
/*      */         }
/* 1085 */         this.visibleLocals[(index--)] = null;
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   public void f2d() {
/* 1090 */     this.countLabels = 0;
/* 1091 */     this.stackDepth += 1;
/* 1092 */     if (this.stackDepth > this.stackMax)
/* 1093 */       this.stackMax = this.stackDepth;
/* 1094 */     if (this.classFileOffset >= this.bCodeStream.length) {
/* 1095 */       resizeByteArray();
/*      */     }
/* 1097 */     this.position += 1;
/* 1098 */     this.bCodeStream[(this.classFileOffset++)] = -115;
/*      */   }
/*      */ 
/*      */   public void f2i() {
/* 1102 */     this.countLabels = 0;
/* 1103 */     if (this.classFileOffset >= this.bCodeStream.length) {
/* 1104 */       resizeByteArray();
/*      */     }
/* 1106 */     this.position += 1;
/* 1107 */     this.bCodeStream[(this.classFileOffset++)] = -117;
/*      */   }
/*      */ 
/*      */   public void f2l() {
/* 1111 */     this.countLabels = 0;
/* 1112 */     this.stackDepth += 1;
/* 1113 */     if (this.stackDepth > this.stackMax)
/* 1114 */       this.stackMax = this.stackDepth;
/* 1115 */     if (this.classFileOffset >= this.bCodeStream.length) {
/* 1116 */       resizeByteArray();
/*      */     }
/* 1118 */     this.position += 1;
/* 1119 */     this.bCodeStream[(this.classFileOffset++)] = -116;
/*      */   }
/*      */ 
/*      */   public void fadd() {
/* 1123 */     this.countLabels = 0;
/* 1124 */     this.stackDepth -= 1;
/* 1125 */     if (this.classFileOffset >= this.bCodeStream.length) {
/* 1126 */       resizeByteArray();
/*      */     }
/* 1128 */     this.position += 1;
/* 1129 */     this.bCodeStream[(this.classFileOffset++)] = 98;
/*      */   }
/*      */ 
/*      */   public void faload() {
/* 1133 */     this.countLabels = 0;
/* 1134 */     this.stackDepth -= 1;
/* 1135 */     if (this.classFileOffset >= this.bCodeStream.length) {
/* 1136 */       resizeByteArray();
/*      */     }
/* 1138 */     this.position += 1;
/* 1139 */     this.bCodeStream[(this.classFileOffset++)] = 48;
/*      */   }
/*      */ 
/*      */   public void fastore() {
/* 1143 */     this.countLabels = 0;
/* 1144 */     this.stackDepth -= 3;
/* 1145 */     if (this.classFileOffset >= this.bCodeStream.length) {
/* 1146 */       resizeByteArray();
/*      */     }
/* 1148 */     this.position += 1;
/* 1149 */     this.bCodeStream[(this.classFileOffset++)] = 81;
/*      */   }
/*      */ 
/*      */   public void fcmpg() {
/* 1153 */     this.countLabels = 0;
/* 1154 */     this.stackDepth -= 1;
/* 1155 */     if (this.classFileOffset >= this.bCodeStream.length) {
/* 1156 */       resizeByteArray();
/*      */     }
/* 1158 */     this.position += 1;
/* 1159 */     this.bCodeStream[(this.classFileOffset++)] = -106;
/*      */   }
/*      */ 
/*      */   public void fcmpl() {
/* 1163 */     this.countLabels = 0;
/* 1164 */     this.stackDepth -= 1;
/* 1165 */     if (this.classFileOffset >= this.bCodeStream.length) {
/* 1166 */       resizeByteArray();
/*      */     }
/* 1168 */     this.position += 1;
/* 1169 */     this.bCodeStream[(this.classFileOffset++)] = -107;
/*      */   }
/*      */ 
/*      */   public void fconst_0() {
/* 1173 */     this.countLabels = 0;
/* 1174 */     this.stackDepth += 1;
/* 1175 */     if (this.stackDepth > this.stackMax)
/* 1176 */       this.stackMax = this.stackDepth;
/* 1177 */     if (this.classFileOffset >= this.bCodeStream.length) {
/* 1178 */       resizeByteArray();
/*      */     }
/* 1180 */     this.position += 1;
/* 1181 */     this.bCodeStream[(this.classFileOffset++)] = 11;
/*      */   }
/*      */ 
/*      */   public void fconst_1() {
/* 1185 */     this.countLabels = 0;
/* 1186 */     this.stackDepth += 1;
/* 1187 */     if (this.stackDepth > this.stackMax)
/* 1188 */       this.stackMax = this.stackDepth;
/* 1189 */     if (this.classFileOffset >= this.bCodeStream.length) {
/* 1190 */       resizeByteArray();
/*      */     }
/* 1192 */     this.position += 1;
/* 1193 */     this.bCodeStream[(this.classFileOffset++)] = 12;
/*      */   }
/*      */ 
/*      */   public void fconst_2() {
/* 1197 */     this.countLabels = 0;
/* 1198 */     this.stackDepth += 1;
/* 1199 */     if (this.stackDepth > this.stackMax)
/* 1200 */       this.stackMax = this.stackDepth;
/* 1201 */     if (this.classFileOffset >= this.bCodeStream.length) {
/* 1202 */       resizeByteArray();
/*      */     }
/* 1204 */     this.position += 1;
/* 1205 */     this.bCodeStream[(this.classFileOffset++)] = 13;
/*      */   }
/*      */ 
/*      */   public void fdiv() {
/* 1209 */     this.countLabels = 0;
/* 1210 */     this.stackDepth -= 1;
/* 1211 */     if (this.classFileOffset >= this.bCodeStream.length) {
/* 1212 */       resizeByteArray();
/*      */     }
/* 1214 */     this.position += 1;
/* 1215 */     this.bCodeStream[(this.classFileOffset++)] = 110;
/*      */   }
/*      */ 
/*      */   public void fieldAccess(byte opcode, FieldBinding fieldBinding, TypeBinding declaringClass) {
/* 1219 */     if (declaringClass == null) declaringClass = fieldBinding.declaringClass;
/* 1220 */     if ((declaringClass.tagBits & 0x800) != 0L) {
/* 1221 */       Util.recordNestedType(this.classFile, declaringClass);
/*      */     }
/* 1223 */     TypeBinding returnType = fieldBinding.type;
/*      */     int returnTypeSize;
/*      */     int returnTypeSize;
/* 1225 */     switch (returnType.id) {
/*      */     case 7:
/*      */     case 8:
/* 1228 */       returnTypeSize = 2;
/* 1229 */       break;
/*      */     default:
/* 1231 */       returnTypeSize = 1;
/*      */     }
/*      */ 
/* 1234 */     fieldAccess(opcode, returnTypeSize, declaringClass.constantPoolName(), fieldBinding.name, returnType.signature());
/*      */   }
/*      */ 
/*      */   private void fieldAccess(byte opcode, int returnTypeSize, char[] declaringClass, char[] fieldName, char[] signature) {
/* 1238 */     this.countLabels = 0;
/* 1239 */     switch (opcode) {
/*      */     case -76:
/* 1241 */       if (returnTypeSize != 2) break;
/* 1242 */       this.stackDepth += 1;
/*      */ 
/* 1244 */       break;
/*      */     case -78:
/* 1246 */       if (returnTypeSize == 2)
/* 1247 */         this.stackDepth += 2;
/*      */       else {
/* 1249 */         this.stackDepth += 1;
/*      */       }
/* 1251 */       break;
/*      */     case -75:
/* 1253 */       if (returnTypeSize == 2)
/* 1254 */         this.stackDepth -= 3;
/*      */       else {
/* 1256 */         this.stackDepth -= 2;
/*      */       }
/* 1258 */       break;
/*      */     case -77:
/* 1260 */       if (returnTypeSize == 2)
/* 1261 */         this.stackDepth -= 2;
/*      */       else {
/* 1263 */         this.stackDepth -= 1;
/*      */       }
/*      */     }
/* 1266 */     if (this.stackDepth > this.stackMax) {
/* 1267 */       this.stackMax = this.stackDepth;
/*      */     }
/* 1269 */     if (this.classFileOffset + 2 >= this.bCodeStream.length) {
/* 1270 */       resizeByteArray();
/*      */     }
/* 1272 */     this.position += 1;
/* 1273 */     this.bCodeStream[(this.classFileOffset++)] = opcode;
/* 1274 */     writeUnsignedShort(this.constantPool.literalIndexForField(declaringClass, fieldName, signature));
/*      */   }
/*      */ 
/*      */   public void fload(int iArg) {
/* 1278 */     this.countLabels = 0;
/* 1279 */     this.stackDepth += 1;
/* 1280 */     if (this.maxLocals <= iArg) {
/* 1281 */       this.maxLocals = (iArg + 1);
/*      */     }
/* 1283 */     if (this.stackDepth > this.stackMax)
/* 1284 */       this.stackMax = this.stackDepth;
/* 1285 */     if (iArg > 255) {
/* 1286 */       if (this.classFileOffset + 3 >= this.bCodeStream.length) {
/* 1287 */         resizeByteArray();
/*      */       }
/* 1289 */       this.position += 2;
/* 1290 */       this.bCodeStream[(this.classFileOffset++)] = -60;
/* 1291 */       this.bCodeStream[(this.classFileOffset++)] = 23;
/* 1292 */       writeUnsignedShort(iArg);
/*      */     } else {
/* 1294 */       if (this.classFileOffset + 1 >= this.bCodeStream.length) {
/* 1295 */         resizeByteArray();
/*      */       }
/* 1297 */       this.position += 2;
/* 1298 */       this.bCodeStream[(this.classFileOffset++)] = 23;
/* 1299 */       this.bCodeStream[(this.classFileOffset++)] = (byte)iArg;
/*      */     }
/*      */   }
/*      */ 
/*      */   public void fload_0() {
/* 1304 */     this.countLabels = 0;
/* 1305 */     this.stackDepth += 1;
/* 1306 */     if (this.maxLocals == 0) {
/* 1307 */       this.maxLocals = 1;
/*      */     }
/* 1309 */     if (this.stackDepth > this.stackMax)
/* 1310 */       this.stackMax = this.stackDepth;
/* 1311 */     if (this.classFileOffset >= this.bCodeStream.length) {
/* 1312 */       resizeByteArray();
/*      */     }
/* 1314 */     this.position += 1;
/* 1315 */     this.bCodeStream[(this.classFileOffset++)] = 34;
/*      */   }
/*      */ 
/*      */   public void fload_1() {
/* 1319 */     this.countLabels = 0;
/* 1320 */     this.stackDepth += 1;
/* 1321 */     if (this.maxLocals <= 1) {
/* 1322 */       this.maxLocals = 2;
/*      */     }
/* 1324 */     if (this.stackDepth > this.stackMax)
/* 1325 */       this.stackMax = this.stackDepth;
/* 1326 */     if (this.classFileOffset >= this.bCodeStream.length) {
/* 1327 */       resizeByteArray();
/*      */     }
/* 1329 */     this.position += 1;
/* 1330 */     this.bCodeStream[(this.classFileOffset++)] = 35;
/*      */   }
/*      */ 
/*      */   public void fload_2() {
/* 1334 */     this.countLabels = 0;
/* 1335 */     this.stackDepth += 1;
/* 1336 */     if (this.maxLocals <= 2) {
/* 1337 */       this.maxLocals = 3;
/*      */     }
/* 1339 */     if (this.stackDepth > this.stackMax)
/* 1340 */       this.stackMax = this.stackDepth;
/* 1341 */     if (this.classFileOffset >= this.bCodeStream.length) {
/* 1342 */       resizeByteArray();
/*      */     }
/* 1344 */     this.position += 1;
/* 1345 */     this.bCodeStream[(this.classFileOffset++)] = 36;
/*      */   }
/*      */ 
/*      */   public void fload_3() {
/* 1349 */     this.countLabels = 0;
/* 1350 */     this.stackDepth += 1;
/* 1351 */     if (this.maxLocals <= 3) {
/* 1352 */       this.maxLocals = 4;
/*      */     }
/* 1354 */     if (this.stackDepth > this.stackMax)
/* 1355 */       this.stackMax = this.stackDepth;
/* 1356 */     if (this.classFileOffset >= this.bCodeStream.length) {
/* 1357 */       resizeByteArray();
/*      */     }
/* 1359 */     this.position += 1;
/* 1360 */     this.bCodeStream[(this.classFileOffset++)] = 37;
/*      */   }
/*      */ 
/*      */   public void fmul() {
/* 1364 */     this.countLabels = 0;
/* 1365 */     this.stackDepth -= 1;
/* 1366 */     if (this.classFileOffset >= this.bCodeStream.length) {
/* 1367 */       resizeByteArray();
/*      */     }
/* 1369 */     this.position += 1;
/* 1370 */     this.bCodeStream[(this.classFileOffset++)] = 106;
/*      */   }
/*      */ 
/*      */   public void fneg() {
/* 1374 */     this.countLabels = 0;
/* 1375 */     if (this.classFileOffset >= this.bCodeStream.length) {
/* 1376 */       resizeByteArray();
/*      */     }
/* 1378 */     this.position += 1;
/* 1379 */     this.bCodeStream[(this.classFileOffset++)] = 118;
/*      */   }
/*      */ 
/*      */   public void frem() {
/* 1383 */     this.countLabels = 0;
/* 1384 */     this.stackDepth -= 1;
/* 1385 */     if (this.classFileOffset >= this.bCodeStream.length) {
/* 1386 */       resizeByteArray();
/*      */     }
/* 1388 */     this.position += 1;
/* 1389 */     this.bCodeStream[(this.classFileOffset++)] = 114;
/*      */   }
/*      */ 
/*      */   public void freturn() {
/* 1393 */     this.countLabels = 0;
/* 1394 */     this.stackDepth -= 1;
/*      */ 
/* 1396 */     if (this.classFileOffset >= this.bCodeStream.length) {
/* 1397 */       resizeByteArray();
/*      */     }
/* 1399 */     this.position += 1;
/* 1400 */     this.bCodeStream[(this.classFileOffset++)] = -82;
/* 1401 */     this.lastAbruptCompletion = this.position;
/*      */   }
/*      */ 
/*      */   public void fstore(int iArg) {
/* 1405 */     this.countLabels = 0;
/* 1406 */     this.stackDepth -= 1;
/* 1407 */     if (this.maxLocals <= iArg) {
/* 1408 */       this.maxLocals = (iArg + 1);
/*      */     }
/* 1410 */     if (iArg > 255) {
/* 1411 */       if (this.classFileOffset + 3 >= this.bCodeStream.length) {
/* 1412 */         resizeByteArray();
/*      */       }
/* 1414 */       this.position += 2;
/* 1415 */       this.bCodeStream[(this.classFileOffset++)] = -60;
/* 1416 */       this.bCodeStream[(this.classFileOffset++)] = 56;
/* 1417 */       writeUnsignedShort(iArg);
/*      */     } else {
/* 1419 */       if (this.classFileOffset + 1 >= this.bCodeStream.length) {
/* 1420 */         resizeByteArray();
/*      */       }
/* 1422 */       this.position += 2;
/* 1423 */       this.bCodeStream[(this.classFileOffset++)] = 56;
/* 1424 */       this.bCodeStream[(this.classFileOffset++)] = (byte)iArg;
/*      */     }
/*      */   }
/*      */ 
/*      */   public void fstore_0() {
/* 1429 */     this.countLabels = 0;
/* 1430 */     this.stackDepth -= 1;
/* 1431 */     if (this.maxLocals == 0) {
/* 1432 */       this.maxLocals = 1;
/*      */     }
/* 1434 */     if (this.classFileOffset >= this.bCodeStream.length) {
/* 1435 */       resizeByteArray();
/*      */     }
/* 1437 */     this.position += 1;
/* 1438 */     this.bCodeStream[(this.classFileOffset++)] = 67;
/*      */   }
/*      */ 
/*      */   public void fstore_1() {
/* 1442 */     this.countLabels = 0;
/* 1443 */     this.stackDepth -= 1;
/* 1444 */     if (this.maxLocals <= 1) {
/* 1445 */       this.maxLocals = 2;
/*      */     }
/* 1447 */     if (this.classFileOffset >= this.bCodeStream.length) {
/* 1448 */       resizeByteArray();
/*      */     }
/* 1450 */     this.position += 1;
/* 1451 */     this.bCodeStream[(this.classFileOffset++)] = 68;
/*      */   }
/*      */ 
/*      */   public void fstore_2() {
/* 1455 */     this.countLabels = 0;
/* 1456 */     this.stackDepth -= 1;
/* 1457 */     if (this.maxLocals <= 2) {
/* 1458 */       this.maxLocals = 3;
/*      */     }
/* 1460 */     if (this.classFileOffset >= this.bCodeStream.length) {
/* 1461 */       resizeByteArray();
/*      */     }
/* 1463 */     this.position += 1;
/* 1464 */     this.bCodeStream[(this.classFileOffset++)] = 69;
/*      */   }
/*      */ 
/*      */   public void fstore_3() {
/* 1468 */     this.countLabels = 0;
/* 1469 */     this.stackDepth -= 1;
/* 1470 */     if (this.maxLocals <= 3) {
/* 1471 */       this.maxLocals = 4;
/*      */     }
/* 1473 */     if (this.classFileOffset >= this.bCodeStream.length) {
/* 1474 */       resizeByteArray();
/*      */     }
/* 1476 */     this.position += 1;
/* 1477 */     this.bCodeStream[(this.classFileOffset++)] = 70;
/*      */   }
/*      */ 
/*      */   public void fsub() {
/* 1481 */     this.countLabels = 0;
/* 1482 */     this.stackDepth -= 1;
/* 1483 */     if (this.classFileOffset >= this.bCodeStream.length) {
/* 1484 */       resizeByteArray();
/*      */     }
/* 1486 */     this.position += 1;
/* 1487 */     this.bCodeStream[(this.classFileOffset++)] = 102;
/*      */   }
/*      */ 
/*      */   public void generateBoxingConversion(int unboxedTypeID) {
/* 1491 */     switch (unboxedTypeID) {
/*      */     case 3:
/* 1493 */       if (this.targetLevel >= 3211264L)
/*      */       {
/* 1495 */         invoke(
/* 1496 */           -72, 
/* 1497 */           1, 
/* 1498 */           1, 
/* 1499 */           ConstantPool.JavaLangByteConstantPoolName, 
/* 1500 */           ConstantPool.ValueOf, 
/* 1501 */           ConstantPool.byteByteSignature);
/*      */       }
/*      */       else {
/* 1504 */         newWrapperFor(unboxedTypeID);
/* 1505 */         dup_x1();
/* 1506 */         swap();
/* 1507 */         invoke(
/* 1508 */           -73, 
/* 1509 */           2, 
/* 1510 */           0, 
/* 1511 */           ConstantPool.JavaLangByteConstantPoolName, 
/* 1512 */           ConstantPool.Init, 
/* 1513 */           ConstantPool.ByteConstrSignature);
/*      */       }
/* 1515 */       break;
/*      */     case 4:
/* 1517 */       if (this.targetLevel >= 3211264L)
/*      */       {
/* 1519 */         invoke(
/* 1520 */           -72, 
/* 1521 */           1, 
/* 1522 */           1, 
/* 1523 */           ConstantPool.JavaLangShortConstantPoolName, 
/* 1524 */           ConstantPool.ValueOf, 
/* 1525 */           ConstantPool.shortShortSignature);
/*      */       }
/*      */       else {
/* 1528 */         newWrapperFor(unboxedTypeID);
/* 1529 */         dup_x1();
/* 1530 */         swap();
/* 1531 */         invoke(
/* 1532 */           -73, 
/* 1533 */           2, 
/* 1534 */           0, 
/* 1535 */           ConstantPool.JavaLangShortConstantPoolName, 
/* 1536 */           ConstantPool.Init, 
/* 1537 */           ConstantPool.ShortConstrSignature);
/*      */       }
/* 1539 */       break;
/*      */     case 2:
/* 1541 */       if (this.targetLevel >= 3211264L)
/*      */       {
/* 1543 */         invoke(
/* 1544 */           -72, 
/* 1545 */           1, 
/* 1546 */           1, 
/* 1547 */           ConstantPool.JavaLangCharacterConstantPoolName, 
/* 1548 */           ConstantPool.ValueOf, 
/* 1549 */           ConstantPool.charCharacterSignature);
/*      */       }
/*      */       else {
/* 1552 */         newWrapperFor(unboxedTypeID);
/* 1553 */         dup_x1();
/* 1554 */         swap();
/* 1555 */         invoke(
/* 1556 */           -73, 
/* 1557 */           2, 
/* 1558 */           0, 
/* 1559 */           ConstantPool.JavaLangCharacterConstantPoolName, 
/* 1560 */           ConstantPool.Init, 
/* 1561 */           ConstantPool.CharConstrSignature);
/*      */       }
/* 1563 */       break;
/*      */     case 10:
/* 1565 */       if (this.targetLevel >= 3211264L)
/*      */       {
/* 1567 */         invoke(
/* 1568 */           -72, 
/* 1569 */           1, 
/* 1570 */           1, 
/* 1571 */           ConstantPool.JavaLangIntegerConstantPoolName, 
/* 1572 */           ConstantPool.ValueOf, 
/* 1573 */           ConstantPool.IntIntegerSignature);
/*      */       }
/*      */       else {
/* 1576 */         newWrapperFor(unboxedTypeID);
/* 1577 */         dup_x1();
/* 1578 */         swap();
/* 1579 */         invoke(
/* 1580 */           -73, 
/* 1581 */           2, 
/* 1582 */           0, 
/* 1583 */           ConstantPool.JavaLangIntegerConstantPoolName, 
/* 1584 */           ConstantPool.Init, 
/* 1585 */           ConstantPool.IntConstrSignature);
/*      */       }
/* 1587 */       break;
/*      */     case 7:
/* 1589 */       if (this.targetLevel >= 3211264L)
/*      */       {
/* 1591 */         invoke(
/* 1592 */           -72, 
/* 1593 */           2, 
/* 1594 */           1, 
/* 1595 */           ConstantPool.JavaLangLongConstantPoolName, 
/* 1596 */           ConstantPool.ValueOf, 
/* 1597 */           ConstantPool.longLongSignature);
/*      */       }
/*      */       else {
/* 1600 */         newWrapperFor(unboxedTypeID);
/* 1601 */         dup_x2();
/* 1602 */         dup_x2();
/* 1603 */         pop();
/* 1604 */         invoke(
/* 1605 */           -73, 
/* 1606 */           3, 
/* 1607 */           0, 
/* 1608 */           ConstantPool.JavaLangLongConstantPoolName, 
/* 1609 */           ConstantPool.Init, 
/* 1610 */           ConstantPool.LongConstrSignature);
/*      */       }
/* 1612 */       break;
/*      */     case 9:
/* 1614 */       if (this.targetLevel >= 3211264L)
/*      */       {
/* 1616 */         invoke(
/* 1617 */           -72, 
/* 1618 */           1, 
/* 1619 */           1, 
/* 1620 */           ConstantPool.JavaLangFloatConstantPoolName, 
/* 1621 */           ConstantPool.ValueOf, 
/* 1622 */           ConstantPool.floatFloatSignature);
/*      */       }
/*      */       else {
/* 1625 */         newWrapperFor(unboxedTypeID);
/* 1626 */         dup_x1();
/* 1627 */         swap();
/* 1628 */         invoke(
/* 1629 */           -73, 
/* 1630 */           2, 
/* 1631 */           0, 
/* 1632 */           ConstantPool.JavaLangFloatConstantPoolName, 
/* 1633 */           ConstantPool.Init, 
/* 1634 */           ConstantPool.FloatConstrSignature);
/*      */       }
/* 1636 */       break;
/*      */     case 8:
/* 1638 */       if (this.targetLevel >= 3211264L)
/*      */       {
/* 1640 */         invoke(
/* 1641 */           -72, 
/* 1642 */           2, 
/* 1643 */           1, 
/* 1644 */           ConstantPool.JavaLangDoubleConstantPoolName, 
/* 1645 */           ConstantPool.ValueOf, 
/* 1646 */           ConstantPool.doubleDoubleSignature);
/*      */       }
/*      */       else {
/* 1649 */         newWrapperFor(unboxedTypeID);
/* 1650 */         dup_x2();
/* 1651 */         dup_x2();
/* 1652 */         pop();
/*      */ 
/* 1654 */         invoke(
/* 1655 */           -73, 
/* 1656 */           3, 
/* 1657 */           0, 
/* 1658 */           ConstantPool.JavaLangDoubleConstantPoolName, 
/* 1659 */           ConstantPool.Init, 
/* 1660 */           ConstantPool.DoubleConstrSignature);
/*      */       }
/*      */ 
/* 1663 */       break;
/*      */     case 5:
/* 1665 */       if (this.targetLevel >= 3211264L)
/*      */       {
/* 1667 */         invoke(
/* 1668 */           -72, 
/* 1669 */           1, 
/* 1670 */           1, 
/* 1671 */           ConstantPool.JavaLangBooleanConstantPoolName, 
/* 1672 */           ConstantPool.ValueOf, 
/* 1673 */           ConstantPool.booleanBooleanSignature);
/*      */       }
/*      */       else {
/* 1676 */         newWrapperFor(unboxedTypeID);
/* 1677 */         dup_x1();
/* 1678 */         swap();
/* 1679 */         invoke(
/* 1680 */           -73, 
/* 1681 */           2, 
/* 1682 */           0, 
/* 1683 */           ConstantPool.JavaLangBooleanConstantPoolName, 
/* 1684 */           ConstantPool.Init, 
/* 1685 */           ConstantPool.BooleanConstrSignature);
/*      */       }
/*      */     case 6:
/*      */     }
/*      */   }
/*      */ 
/*      */   public void generateClassLiteralAccessForType(TypeBinding accessedType, FieldBinding syntheticFieldBinding)
/*      */   {
/* 1694 */     if ((accessedType.isBaseType()) && (accessedType != TypeBinding.NULL)) {
/* 1695 */       getTYPE(accessedType.id);
/* 1696 */       return;
/*      */     }
/* 1698 */     if (this.targetLevel >= 3211264L)
/*      */     {
/* 1700 */       ldc(accessedType);
/*      */     } else {
/* 1702 */       BranchLabel endLabel = new BranchLabel(this);
/* 1703 */       if (syntheticFieldBinding != null) {
/* 1704 */         fieldAccess(-78, syntheticFieldBinding, null);
/* 1705 */         dup();
/* 1706 */         ifnonnull(endLabel);
/* 1707 */         pop();
/*      */       }
/*      */ 
/* 1721 */       ExceptionLabel classNotFoundExceptionHandler = new ExceptionLabel(this, TypeBinding.NULL);
/* 1722 */       classNotFoundExceptionHandler.placeStart();
/* 1723 */       ldc(accessedType == TypeBinding.NULL ? "java.lang.Object" : String.valueOf(accessedType.constantPoolName()).replace('/', '.'));
/* 1724 */       invokeClassForName();
/*      */ 
/* 1743 */       classNotFoundExceptionHandler.placeEnd();
/*      */ 
/* 1745 */       if (syntheticFieldBinding != null) {
/* 1746 */         dup();
/* 1747 */         fieldAccess(-77, syntheticFieldBinding, null);
/*      */       }
/* 1749 */       goto_(endLabel);
/*      */ 
/* 1751 */       int savedStackDepth = this.stackDepth;
/*      */ 
/* 1757 */       pushExceptionOnStack(TypeBinding.NULL);
/* 1758 */       classNotFoundExceptionHandler.place();
/*      */ 
/* 1763 */       newNoClassDefFoundError();
/* 1764 */       dup_x1();
/* 1765 */       swap();
/*      */ 
/* 1768 */       invokeThrowableGetMessage();
/*      */ 
/* 1771 */       invokeNoClassDefFoundErrorStringConstructor();
/* 1772 */       athrow();
/* 1773 */       endLabel.place();
/* 1774 */       this.stackDepth = savedStackDepth;
/*      */     }
/*      */   }
/*      */ 
/*      */   public final void generateCodeAttributeForProblemMethod(String problemMessage)
/*      */   {
/* 1782 */     newJavaLangError();
/* 1783 */     dup();
/* 1784 */     ldc(problemMessage);
/* 1785 */     invokeJavaLangErrorConstructor();
/* 1786 */     athrow();
/*      */   }
/*      */ 
/*      */   public void generateConstant(Constant constant, int implicitConversionCode) {
/* 1790 */     int targetTypeID = (implicitConversionCode & 0xFF) >> 4;
/* 1791 */     if (targetTypeID == 0) targetTypeID = constant.typeID();
/* 1792 */     switch (targetTypeID) {
/*      */     case 5:
/* 1794 */       generateInlinedValue(constant.booleanValue());
/* 1795 */       break;
/*      */     case 2:
/* 1797 */       generateInlinedValue(constant.charValue());
/* 1798 */       break;
/*      */     case 3:
/* 1800 */       generateInlinedValue(constant.byteValue());
/* 1801 */       break;
/*      */     case 4:
/* 1803 */       generateInlinedValue(constant.shortValue());
/* 1804 */       break;
/*      */     case 10:
/* 1806 */       generateInlinedValue(constant.intValue());
/* 1807 */       break;
/*      */     case 7:
/* 1809 */       generateInlinedValue(constant.longValue());
/* 1810 */       break;
/*      */     case 9:
/* 1812 */       generateInlinedValue(constant.floatValue());
/* 1813 */       break;
/*      */     case 8:
/* 1815 */       generateInlinedValue(constant.doubleValue());
/* 1816 */       break;
/*      */     case 11:
/* 1818 */       ldc(constant.stringValue());
/*      */     case 6:
/* 1820 */     }if ((implicitConversionCode & 0x200) != 0)
/*      */     {
/* 1822 */       generateBoxingConversion(targetTypeID);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void generateEmulatedReadAccessForField(FieldBinding fieldBinding) {
/* 1827 */     generateEmulationForField(fieldBinding);
/*      */ 
/* 1829 */     swap();
/* 1830 */     invokeJavaLangReflectFieldGetter(fieldBinding.type.id);
/* 1831 */     if (!fieldBinding.type.isBaseType())
/* 1832 */       checkcast(fieldBinding.type);
/*      */   }
/*      */ 
/*      */   public void generateEmulatedWriteAccessForField(FieldBinding fieldBinding)
/*      */   {
/* 1837 */     invokeJavaLangReflectFieldSetter(fieldBinding.type.id);
/*      */   }
/*      */ 
/*      */   public void generateEmulationForConstructor(Scope scope, MethodBinding methodBinding)
/*      */   {
/* 1842 */     ldc(String.valueOf(methodBinding.declaringClass.constantPoolName()).replace('/', '.'));
/* 1843 */     invokeClassForName();
/* 1844 */     int paramLength = methodBinding.parameters.length;
/* 1845 */     generateInlinedValue(paramLength);
/* 1846 */     newArray(scope.createArrayType(scope.getType(TypeConstants.JAVA_LANG_CLASS, 3), 1));
/* 1847 */     if (paramLength > 0) {
/* 1848 */       dup();
/* 1849 */       for (int i = 0; i < paramLength; i++) {
/* 1850 */         generateInlinedValue(i);
/* 1851 */         TypeBinding parameter = methodBinding.parameters[i];
/* 1852 */         if (parameter.isBaseType()) {
/* 1853 */           getTYPE(parameter.id);
/* 1854 */         } else if (parameter.isArrayType()) {
/* 1855 */           ArrayBinding array = (ArrayBinding)parameter;
/* 1856 */           if (array.leafComponentType.isBaseType()) {
/* 1857 */             getTYPE(array.leafComponentType.id);
/*      */           } else {
/* 1859 */             ldc(String.valueOf(array.leafComponentType.constantPoolName()).replace('/', '.'));
/* 1860 */             invokeClassForName();
/*      */           }
/* 1862 */           int dimensions = array.dimensions;
/* 1863 */           generateInlinedValue(dimensions);
/* 1864 */           newarray(10);
/* 1865 */           invokeArrayNewInstance();
/* 1866 */           invokeObjectGetClass();
/*      */         }
/*      */         else {
/* 1869 */           ldc(String.valueOf(methodBinding.declaringClass.constantPoolName()).replace('/', '.'));
/* 1870 */           invokeClassForName();
/*      */         }
/* 1872 */         aastore();
/* 1873 */         if (i < paramLength - 1) {
/* 1874 */           dup();
/*      */         }
/*      */       }
/*      */     }
/* 1878 */     invokeClassGetDeclaredConstructor();
/* 1879 */     dup();
/* 1880 */     iconst_1();
/* 1881 */     invokeAccessibleObjectSetAccessible();
/*      */   }
/*      */ 
/*      */   public void generateEmulationForField(FieldBinding fieldBinding)
/*      */   {
/* 1886 */     ldc(String.valueOf(fieldBinding.declaringClass.constantPoolName()).replace('/', '.'));
/* 1887 */     invokeClassForName();
/* 1888 */     ldc(String.valueOf(fieldBinding.name));
/* 1889 */     invokeClassGetDeclaredField();
/* 1890 */     dup();
/* 1891 */     iconst_1();
/* 1892 */     invokeAccessibleObjectSetAccessible();
/*      */   }
/*      */ 
/*      */   public void generateEmulationForMethod(Scope scope, MethodBinding methodBinding)
/*      */   {
/* 1897 */     ldc(String.valueOf(methodBinding.declaringClass.constantPoolName()).replace('/', '.'));
/* 1898 */     invokeClassForName();
/* 1899 */     ldc(String.valueOf(methodBinding.selector));
/* 1900 */     int paramLength = methodBinding.parameters.length;
/* 1901 */     generateInlinedValue(paramLength);
/* 1902 */     newArray(scope.createArrayType(scope.getType(TypeConstants.JAVA_LANG_CLASS, 3), 1));
/* 1903 */     if (paramLength > 0) {
/* 1904 */       dup();
/* 1905 */       for (int i = 0; i < paramLength; i++) {
/* 1906 */         generateInlinedValue(i);
/* 1907 */         TypeBinding parameter = methodBinding.parameters[i];
/* 1908 */         if (parameter.isBaseType()) {
/* 1909 */           getTYPE(parameter.id);
/* 1910 */         } else if (parameter.isArrayType()) {
/* 1911 */           ArrayBinding array = (ArrayBinding)parameter;
/* 1912 */           if (array.leafComponentType.isBaseType()) {
/* 1913 */             getTYPE(array.leafComponentType.id);
/*      */           } else {
/* 1915 */             ldc(String.valueOf(array.leafComponentType.constantPoolName()).replace('/', '.'));
/* 1916 */             invokeClassForName();
/*      */           }
/* 1918 */           int dimensions = array.dimensions;
/* 1919 */           generateInlinedValue(dimensions);
/* 1920 */           newarray(10);
/* 1921 */           invokeArrayNewInstance();
/* 1922 */           invokeObjectGetClass();
/*      */         }
/*      */         else {
/* 1925 */           ldc(String.valueOf(methodBinding.declaringClass.constantPoolName()).replace('/', '.'));
/* 1926 */           invokeClassForName();
/*      */         }
/* 1928 */         aastore();
/* 1929 */         if (i < paramLength - 1) {
/* 1930 */           dup();
/*      */         }
/*      */       }
/*      */     }
/* 1934 */     invokeClassGetDeclaredMethod();
/* 1935 */     dup();
/* 1936 */     iconst_1();
/* 1937 */     invokeAccessibleObjectSetAccessible();
/*      */   }
/*      */ 
/*      */   public void generateImplicitConversion(int implicitConversionCode)
/*      */   {
/* 1946 */     if ((implicitConversionCode & 0x400) != 0) {
/* 1947 */       int typeId = implicitConversionCode & 0xF;
/* 1948 */       generateUnboxingConversion(typeId);
/*      */     }
/*      */ 
/* 1951 */     switch (implicitConversionCode & 0xFF) {
/*      */     case 41:
/* 1953 */       f2i();
/* 1954 */       i2c();
/* 1955 */       break;
/*      */     case 40:
/* 1957 */       d2i();
/* 1958 */       i2c();
/* 1959 */       break;
/*      */     case 35:
/*      */     case 36:
/*      */     case 42:
/* 1963 */       i2c();
/* 1964 */       break;
/*      */     case 39:
/* 1966 */       l2i();
/* 1967 */       i2c();
/* 1968 */       break;
/*      */     case 146:
/*      */     case 147:
/*      */     case 148:
/*      */     case 154:
/* 1973 */       i2f();
/* 1974 */       break;
/*      */     case 152:
/* 1976 */       d2f();
/* 1977 */       break;
/*      */     case 151:
/* 1979 */       l2f();
/* 1980 */       break;
/*      */     case 57:
/* 1982 */       f2i();
/* 1983 */       i2b();
/* 1984 */       break;
/*      */     case 56:
/* 1986 */       d2i();
/* 1987 */       i2b();
/* 1988 */       break;
/*      */     case 50:
/*      */     case 52:
/*      */     case 58:
/* 1992 */       i2b();
/* 1993 */       break;
/*      */     case 55:
/* 1995 */       l2i();
/* 1996 */       i2b();
/* 1997 */       break;
/*      */     case 130:
/*      */     case 131:
/*      */     case 132:
/*      */     case 138:
/* 2002 */       i2d();
/* 2003 */       break;
/*      */     case 137:
/* 2005 */       f2d();
/* 2006 */       break;
/*      */     case 135:
/* 2008 */       l2d();
/* 2009 */       break;
/*      */     case 66:
/*      */     case 67:
/*      */     case 74:
/* 2013 */       i2s();
/* 2014 */       break;
/*      */     case 72:
/* 2016 */       d2i();
/* 2017 */       i2s();
/* 2018 */       break;
/*      */     case 71:
/* 2020 */       l2i();
/* 2021 */       i2s();
/* 2022 */       break;
/*      */     case 73:
/* 2024 */       f2i();
/* 2025 */       i2s();
/* 2026 */       break;
/*      */     case 168:
/* 2028 */       d2i();
/* 2029 */       break;
/*      */     case 169:
/* 2031 */       f2i();
/* 2032 */       break;
/*      */     case 167:
/* 2034 */       l2i();
/* 2035 */       break;
/*      */     case 114:
/*      */     case 115:
/*      */     case 116:
/*      */     case 122:
/* 2040 */       i2l();
/* 2041 */       break;
/*      */     case 120:
/* 2043 */       d2l();
/* 2044 */       break;
/*      */     case 121:
/* 2046 */       f2l();
/*      */     }
/* 2048 */     if ((implicitConversionCode & 0x200) != 0)
/*      */     {
/* 2050 */       int typeId = (implicitConversionCode & 0xFF) >> 4;
/* 2051 */       generateBoxingConversion(typeId);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void generateInlinedValue(boolean inlinedValue) {
/* 2056 */     if (inlinedValue)
/* 2057 */       iconst_1();
/*      */     else
/* 2059 */       iconst_0();
/*      */   }
/*      */ 
/*      */   public void generateInlinedValue(byte inlinedValue) {
/* 2063 */     switch (inlinedValue) {
/*      */     case -1:
/* 2065 */       iconst_m1();
/* 2066 */       break;
/*      */     case 0:
/* 2068 */       iconst_0();
/* 2069 */       break;
/*      */     case 1:
/* 2071 */       iconst_1();
/* 2072 */       break;
/*      */     case 2:
/* 2074 */       iconst_2();
/* 2075 */       break;
/*      */     case 3:
/* 2077 */       iconst_3();
/* 2078 */       break;
/*      */     case 4:
/* 2080 */       iconst_4();
/* 2081 */       break;
/*      */     case 5:
/* 2083 */       iconst_5();
/* 2084 */       break;
/*      */     default:
/* 2086 */       if ((-128 > inlinedValue) || (inlinedValue > 127)) break;
/* 2087 */       bipush(inlinedValue);
/* 2088 */       return;
/*      */     }
/*      */   }
/*      */ 
/*      */   public void generateInlinedValue(char inlinedValue)
/*      */   {
/* 2094 */     switch (inlinedValue) {
/*      */     case '\000':
/* 2096 */       iconst_0();
/* 2097 */       break;
/*      */     case '\001':
/* 2099 */       iconst_1();
/* 2100 */       break;
/*      */     case '\002':
/* 2102 */       iconst_2();
/* 2103 */       break;
/*      */     case '\003':
/* 2105 */       iconst_3();
/* 2106 */       break;
/*      */     case '\004':
/* 2108 */       iconst_4();
/* 2109 */       break;
/*      */     case '\005':
/* 2111 */       iconst_5();
/* 2112 */       break;
/*      */     default:
/* 2114 */       if (('\006' <= inlinedValue) && (inlinedValue <= '')) {
/* 2115 */         bipush((byte)inlinedValue);
/* 2116 */         return;
/*      */       }
/* 2118 */       if (('' <= inlinedValue) && (inlinedValue <= '翿')) {
/* 2119 */         sipush(inlinedValue);
/* 2120 */         return;
/*      */       }
/* 2122 */       ldc(inlinedValue);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void generateInlinedValue(double inlinedValue) {
/* 2127 */     if (inlinedValue == 0.0D) {
/* 2128 */       if (Double.doubleToLongBits(inlinedValue) != 0L)
/* 2129 */         ldc2_w(inlinedValue);
/*      */       else
/* 2131 */         dconst_0();
/* 2132 */       return;
/*      */     }
/* 2134 */     if (inlinedValue == 1.0D) {
/* 2135 */       dconst_1();
/* 2136 */       return;
/*      */     }
/* 2138 */     ldc2_w(inlinedValue);
/*      */   }
/*      */ 
/*      */   public void generateInlinedValue(float inlinedValue) {
/* 2142 */     if (inlinedValue == 0.0F) {
/* 2143 */       if (Float.floatToIntBits(inlinedValue) != 0)
/* 2144 */         ldc(inlinedValue);
/*      */       else
/* 2146 */         fconst_0();
/* 2147 */       return;
/*      */     }
/* 2149 */     if (inlinedValue == 1.0F) {
/* 2150 */       fconst_1();
/* 2151 */       return;
/*      */     }
/* 2153 */     if (inlinedValue == 2.0F) {
/* 2154 */       fconst_2();
/* 2155 */       return;
/*      */     }
/* 2157 */     ldc(inlinedValue);
/*      */   }
/*      */ 
/*      */   public void generateInlinedValue(int inlinedValue) {
/* 2161 */     switch (inlinedValue) {
/*      */     case -1:
/* 2163 */       iconst_m1();
/* 2164 */       break;
/*      */     case 0:
/* 2166 */       iconst_0();
/* 2167 */       break;
/*      */     case 1:
/* 2169 */       iconst_1();
/* 2170 */       break;
/*      */     case 2:
/* 2172 */       iconst_2();
/* 2173 */       break;
/*      */     case 3:
/* 2175 */       iconst_3();
/* 2176 */       break;
/*      */     case 4:
/* 2178 */       iconst_4();
/* 2179 */       break;
/*      */     case 5:
/* 2181 */       iconst_5();
/* 2182 */       break;
/*      */     default:
/* 2184 */       if ((-128 <= inlinedValue) && (inlinedValue <= 127)) {
/* 2185 */         bipush((byte)inlinedValue);
/* 2186 */         return;
/*      */       }
/* 2188 */       if ((-32768 <= inlinedValue) && (inlinedValue <= 32767)) {
/* 2189 */         sipush(inlinedValue);
/* 2190 */         return;
/*      */       }
/* 2192 */       ldc(inlinedValue);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void generateInlinedValue(long inlinedValue) {
/* 2197 */     if (inlinedValue == 0L) {
/* 2198 */       lconst_0();
/* 2199 */       return;
/*      */     }
/* 2201 */     if (inlinedValue == 1L) {
/* 2202 */       lconst_1();
/* 2203 */       return;
/*      */     }
/* 2205 */     ldc2_w(inlinedValue);
/*      */   }
/*      */ 
/*      */   public void generateInlinedValue(short inlinedValue) {
/* 2209 */     switch (inlinedValue) {
/*      */     case -1:
/* 2211 */       iconst_m1();
/* 2212 */       break;
/*      */     case 0:
/* 2214 */       iconst_0();
/* 2215 */       break;
/*      */     case 1:
/* 2217 */       iconst_1();
/* 2218 */       break;
/*      */     case 2:
/* 2220 */       iconst_2();
/* 2221 */       break;
/*      */     case 3:
/* 2223 */       iconst_3();
/* 2224 */       break;
/*      */     case 4:
/* 2226 */       iconst_4();
/* 2227 */       break;
/*      */     case 5:
/* 2229 */       iconst_5();
/* 2230 */       break;
/*      */     default:
/* 2232 */       if ((-128 <= inlinedValue) && (inlinedValue <= 127)) {
/* 2233 */         bipush((byte)inlinedValue);
/* 2234 */         return;
/*      */       }
/* 2236 */       sipush(inlinedValue);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void generateOuterAccess(Object[] mappingSequence, ASTNode invocationSite, Binding target, Scope scope) {
/* 2241 */     if (mappingSequence == null) {
/* 2242 */       if ((target instanceof LocalVariableBinding))
/* 2243 */         scope.problemReporter().needImplementation(invocationSite);
/*      */       else {
/* 2245 */         scope.problemReporter().noSuchEnclosingInstance((ReferenceBinding)target, invocationSite, false);
/*      */       }
/* 2247 */       return;
/*      */     }
/* 2249 */     if (mappingSequence == BlockScope.NoEnclosingInstanceInConstructorCall) {
/* 2250 */       scope.problemReporter().noSuchEnclosingInstance((ReferenceBinding)target, invocationSite, true);
/* 2251 */       return;
/* 2252 */     }if (mappingSequence == BlockScope.NoEnclosingInstanceInStaticContext) {
/* 2253 */       scope.problemReporter().noSuchEnclosingInstance((ReferenceBinding)target, invocationSite, false);
/* 2254 */       return;
/*      */     }
/*      */ 
/* 2257 */     if (mappingSequence == BlockScope.EmulationPathToImplicitThis) {
/* 2258 */       aload_0();
/* 2259 */       return;
/* 2260 */     }if ((mappingSequence[0] instanceof FieldBinding)) {
/* 2261 */       FieldBinding fieldBinding = (FieldBinding)mappingSequence[0];
/* 2262 */       aload_0();
/* 2263 */       fieldAccess(-76, fieldBinding, null);
/*      */     } else {
/* 2265 */       load((LocalVariableBinding)mappingSequence[0]);
/*      */     }
/* 2267 */     int i = 1; for (int length = mappingSequence.length; i < length; i++)
/* 2268 */       if ((mappingSequence[i] instanceof FieldBinding)) {
/* 2269 */         FieldBinding fieldBinding = (FieldBinding)mappingSequence[i];
/* 2270 */         fieldAccess(-76, fieldBinding, null);
/*      */       } else {
/* 2272 */         invoke(-72, (MethodBinding)mappingSequence[i], null);
/*      */       }
/*      */   }
/*      */ 
/*      */   public void generateReturnBytecode(Expression expression)
/*      */   {
/* 2278 */     if (expression == null) {
/* 2279 */       return_();
/*      */     } else {
/* 2281 */       int implicitConversion = expression.implicitConversion;
/* 2282 */       if ((implicitConversion & 0x200) != 0) {
/* 2283 */         areturn();
/* 2284 */         return;
/*      */       }
/* 2286 */       int runtimeType = (implicitConversion & 0xFF) >> 4;
/* 2287 */       switch (runtimeType) {
/*      */       case 5:
/*      */       case 10:
/* 2290 */         ireturn();
/* 2291 */         break;
/*      */       case 9:
/* 2293 */         freturn();
/* 2294 */         break;
/*      */       case 7:
/* 2296 */         lreturn();
/* 2297 */         break;
/*      */       case 8:
/* 2299 */         dreturn();
/* 2300 */         break;
/*      */       case 6:
/*      */       default:
/* 2302 */         areturn();
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   public void generateStringConcatenationAppend(BlockScope blockScope, Expression oper1, Expression oper2)
/*      */   {
/* 2316 */     if (oper1 == null)
/*      */     {
/* 2319 */       newStringContatenation();
/* 2320 */       dup_x1();
/* 2321 */       swap();
/*      */ 
/* 2324 */       invokeStringValueOf(1);
/* 2325 */       invokeStringConcatenationStringConstructor();
/*      */     } else {
/* 2327 */       int pc = this.position;
/* 2328 */       oper1.generateOptimizedStringConcatenationCreation(blockScope, this, oper1.implicitConversion & 0xF);
/* 2329 */       recordPositionsFrom(pc, oper1.sourceStart);
/*      */     }
/* 2331 */     int pc = this.position;
/* 2332 */     oper2.generateOptimizedStringConcatenation(blockScope, this, oper2.implicitConversion & 0xF);
/* 2333 */     recordPositionsFrom(pc, oper2.sourceStart);
/* 2334 */     invokeStringConcatenationToString();
/*      */   }
/*      */ 
/*      */   public void generateSyntheticBodyForConstructorAccess(SyntheticMethodBinding accessBinding)
/*      */   {
/* 2341 */     initializeMaxLocals(accessBinding);
/* 2342 */     MethodBinding constructorBinding = accessBinding.targetMethod;
/* 2343 */     TypeBinding[] parameters = constructorBinding.parameters;
/* 2344 */     int length = parameters.length;
/* 2345 */     int resolvedPosition = 1;
/* 2346 */     aload_0();
/*      */ 
/* 2348 */     TypeBinding declaringClass = constructorBinding.declaringClass;
/* 2349 */     if ((declaringClass.erasure().id == 41) || (declaringClass.isEnum())) {
/* 2350 */       aload_1();
/* 2351 */       iload_2();
/* 2352 */       resolvedPosition += 2;
/*      */     }
/* 2354 */     if (declaringClass.isNestedType()) {
/* 2355 */       NestedTypeBinding nestedType = (NestedTypeBinding)declaringClass;
/* 2356 */       SyntheticArgumentBinding[] syntheticArguments = nestedType.syntheticEnclosingInstances();
/* 2357 */       for (int i = 0; i < (syntheticArguments == null ? 0 : syntheticArguments.length); i++)
/*      */       {
/*      */         TypeBinding type;
/* 2359 */         load(type = syntheticArguments[i].type, resolvedPosition);
/* 2360 */         switch (type.id) {
/*      */         case 7:
/*      */         case 8:
/* 2363 */           resolvedPosition += 2;
/* 2364 */           break;
/*      */         default:
/* 2366 */           resolvedPosition++;
/*      */         }
/*      */       }
/*      */     }
/*      */ 
/* 2371 */     for (int i = 0; i < length; i++)
/*      */     {
/*      */       TypeBinding parameter;
/* 2373 */       load(parameter = parameters[i], resolvedPosition);
/* 2374 */       switch (parameter.id) {
/*      */       case 7:
/*      */       case 8:
/* 2377 */         resolvedPosition += 2;
/* 2378 */         break;
/*      */       default:
/* 2380 */         resolvedPosition++;
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 2385 */     if (declaringClass.isNestedType()) {
/* 2386 */       NestedTypeBinding nestedType = (NestedTypeBinding)declaringClass;
/* 2387 */       SyntheticArgumentBinding[] syntheticArguments = nestedType.syntheticOuterLocalVariables();
/* 2388 */       for (int i = 0; i < (syntheticArguments == null ? 0 : syntheticArguments.length); i++)
/*      */       {
/*      */         TypeBinding type;
/* 2390 */         load(type = syntheticArguments[i].type, resolvedPosition);
/* 2391 */         switch (type.id) {
/*      */         case 7:
/*      */         case 8:
/* 2394 */           resolvedPosition += 2;
/* 2395 */           break;
/*      */         default:
/* 2397 */           resolvedPosition++;
/*      */         }
/*      */       }
/*      */     }
/*      */ 
/* 2402 */     invoke(-73, constructorBinding, null);
/* 2403 */     return_();
/*      */   }
/*      */ 
/*      */   public void generateSyntheticBodyForEnumValueOf(SyntheticMethodBinding methodBinding)
/*      */   {
/* 2410 */     initializeMaxLocals(methodBinding);
/* 2411 */     ReferenceBinding declaringClass = methodBinding.declaringClass;
/* 2412 */     generateClassLiteralAccessForType(declaringClass, null);
/* 2413 */     aload_0();
/* 2414 */     invokeJavaLangEnumvalueOf(declaringClass);
/* 2415 */     checkcast(declaringClass);
/* 2416 */     areturn();
/*      */   }
/*      */ 
/*      */   public void generateSyntheticBodyForEnumValues(SyntheticMethodBinding methodBinding)
/*      */   {
/* 2427 */     ClassScope scope = ((SourceTypeBinding)methodBinding.declaringClass).scope;
/* 2428 */     initializeMaxLocals(methodBinding);
/* 2429 */     TypeBinding enumArray = methodBinding.returnType;
/* 2430 */     fieldAccess(-78, scope.referenceContext.enumValuesSyntheticfield, null);
/* 2431 */     dup();
/* 2432 */     astore_0();
/* 2433 */     iconst_0();
/* 2434 */     aload_0();
/* 2435 */     arraylength();
/* 2436 */     dup();
/* 2437 */     istore_1();
/* 2438 */     newArray((ArrayBinding)enumArray);
/* 2439 */     dup();
/* 2440 */     astore_2();
/* 2441 */     iconst_0();
/* 2442 */     iload_1();
/* 2443 */     invokeSystemArraycopy();
/* 2444 */     aload_2();
/* 2445 */     areturn();
/*      */   }
/*      */ 
/*      */   public void generateSyntheticBodyForFieldReadAccess(SyntheticMethodBinding accessMethod) {
/* 2449 */     initializeMaxLocals(accessMethod);
/* 2450 */     FieldBinding fieldBinding = accessMethod.targetReadField;
/*      */ 
/* 2452 */     TypeBinding declaringClass = accessMethod.purpose == 3 ? 
/* 2453 */       accessMethod.declaringClass.superclass() : 
/* 2454 */       accessMethod.declaringClass;
/* 2455 */     if (fieldBinding.isStatic()) {
/* 2456 */       fieldAccess(-78, fieldBinding, declaringClass);
/*      */     } else {
/* 2458 */       aload_0();
/* 2459 */       fieldAccess(-76, fieldBinding, declaringClass);
/*      */     }
/* 2461 */     switch (fieldBinding.type.id)
/*      */     {
/*      */     case 2:
/*      */     case 3:
/*      */     case 4:
/*      */     case 5:
/*      */     case 10:
/* 2470 */       ireturn();
/* 2471 */       break;
/*      */     case 7:
/* 2473 */       lreturn();
/* 2474 */       break;
/*      */     case 9:
/* 2476 */       freturn();
/* 2477 */       break;
/*      */     case 8:
/* 2479 */       dreturn();
/* 2480 */       break;
/*      */     case 6:
/*      */     default:
/* 2482 */       areturn();
/*      */     }
/*      */   }
/*      */ 
/*      */   public void generateSyntheticBodyForFieldWriteAccess(SyntheticMethodBinding accessMethod) {
/* 2487 */     initializeMaxLocals(accessMethod);
/* 2488 */     FieldBinding fieldBinding = accessMethod.targetWriteField;
/*      */ 
/* 2490 */     TypeBinding declaringClass = accessMethod.purpose == 4 ? 
/* 2491 */       accessMethod.declaringClass.superclass() : 
/* 2492 */       accessMethod.declaringClass;
/* 2493 */     if (fieldBinding.isStatic()) {
/* 2494 */       load(fieldBinding.type, 0);
/* 2495 */       fieldAccess(-77, fieldBinding, declaringClass);
/*      */     } else {
/* 2497 */       aload_0();
/* 2498 */       load(fieldBinding.type, 1);
/* 2499 */       fieldAccess(-75, fieldBinding, declaringClass);
/*      */     }
/* 2501 */     return_();
/*      */   }
/*      */ 
/*      */   public void generateSyntheticBodyForMethodAccess(SyntheticMethodBinding accessMethod) {
/* 2505 */     initializeMaxLocals(accessMethod);
/* 2506 */     MethodBinding targetMethod = accessMethod.targetMethod;
/* 2507 */     TypeBinding[] parameters = targetMethod.parameters;
/* 2508 */     int length = parameters.length;
/* 2509 */     TypeBinding[] arguments = accessMethod.purpose == 8 ? 
/* 2510 */       accessMethod.parameters : 
/* 2511 */       null;
/*      */     int resolvedPosition;
/*      */     int resolvedPosition;
/* 2513 */     if (targetMethod.isStatic()) {
/* 2514 */       resolvedPosition = 0;
/*      */     } else {
/* 2516 */       aload_0();
/* 2517 */       resolvedPosition = 1;
/*      */     }
/* 2519 */     for (int i = 0; i < length; i++) {
/* 2520 */       TypeBinding parameter = parameters[i];
/* 2521 */       if (arguments != null) {
/* 2522 */         TypeBinding argument = arguments[i];
/* 2523 */         load(argument, resolvedPosition);
/* 2524 */         if (argument != parameter)
/* 2525 */           checkcast(parameter);
/*      */       } else {
/* 2527 */         load(parameter, resolvedPosition);
/*      */       }
/* 2529 */       switch (parameter.id) {
/*      */       case 7:
/*      */       case 8:
/* 2532 */         resolvedPosition += 2;
/* 2533 */         break;
/*      */       default:
/* 2535 */         resolvedPosition++;
/*      */       }
/*      */     }
/*      */ 
/* 2539 */     if (targetMethod.isStatic()) {
/* 2540 */       invoke(-72, targetMethod, accessMethod.declaringClass);
/*      */     }
/* 2542 */     else if ((targetMethod.isConstructor()) || 
/* 2543 */       (targetMethod.isPrivate()) || 
/* 2545 */       (accessMethod.purpose == 7))
/*      */     {
/* 2547 */       TypeBinding declaringClass = accessMethod.purpose == 7 ? 
/* 2548 */         accessMethod.declaringClass.superclass() : 
/* 2549 */         accessMethod.declaringClass;
/* 2550 */       invoke(-73, targetMethod, declaringClass);
/*      */     }
/* 2552 */     else if (targetMethod.declaringClass.isInterface()) {
/* 2553 */       invoke(-71, targetMethod, null);
/*      */     } else {
/* 2555 */       invoke(-74, targetMethod, accessMethod.declaringClass);
/*      */     }
/*      */ 
/* 2559 */     switch (targetMethod.returnType.id) {
/*      */     case 6:
/* 2561 */       return_();
/* 2562 */       break;
/*      */     case 2:
/*      */     case 3:
/*      */     case 4:
/*      */     case 5:
/*      */     case 10:
/* 2568 */       ireturn();
/* 2569 */       break;
/*      */     case 7:
/* 2571 */       lreturn();
/* 2572 */       break;
/*      */     case 9:
/* 2574 */       freturn();
/* 2575 */       break;
/*      */     case 8:
/* 2577 */       dreturn();
/* 2578 */       break;
/*      */     default:
/* 2580 */       TypeBinding accessErasure = accessMethod.returnType.erasure();
/* 2581 */       TypeBinding match = targetMethod.returnType.findSuperTypeOriginatingFrom(accessErasure);
/* 2582 */       if (match == null) {
/* 2583 */         checkcast(accessErasure);
/*      */       }
/* 2585 */       areturn();
/*      */     }
/*      */   }
/*      */ 
/*      */   public void generateSyntheticBodyForSwitchTable(SyntheticMethodBinding methodBinding) {
/* 2590 */     ClassScope scope = ((SourceTypeBinding)methodBinding.declaringClass).scope;
/* 2591 */     initializeMaxLocals(methodBinding);
/* 2592 */     BranchLabel nullLabel = new BranchLabel(this);
/* 2593 */     FieldBinding syntheticFieldBinding = methodBinding.targetReadField;
/* 2594 */     fieldAccess(-78, syntheticFieldBinding, null);
/* 2595 */     dup();
/* 2596 */     ifnull(nullLabel);
/* 2597 */     areturn();
/* 2598 */     pushOnStack(syntheticFieldBinding.type);
/* 2599 */     nullLabel.place();
/* 2600 */     pop();
/* 2601 */     ReferenceBinding enumBinding = (ReferenceBinding)methodBinding.targetEnumType;
/* 2602 */     ArrayBinding arrayBinding = scope.createArrayType(enumBinding, 1);
/* 2603 */     invokeJavaLangEnumValues(enumBinding, arrayBinding);
/* 2604 */     arraylength();
/* 2605 */     newarray(10);
/* 2606 */     astore_0();
/* 2607 */     LocalVariableBinding localVariableBinding = new LocalVariableBinding(" tab".toCharArray(), scope.createArrayType(TypeBinding.INT, 1), 0, false);
/* 2608 */     addVariable(localVariableBinding);
/* 2609 */     FieldBinding[] fields = enumBinding.fields();
/* 2610 */     if (fields != null) {
/* 2611 */       int i = 0; for (int max = fields.length; i < max; i++) {
/* 2612 */         FieldBinding fieldBinding = fields[i];
/* 2613 */         if ((fieldBinding.getAccessFlags() & 0x4000) != 0) {
/* 2614 */           BranchLabel endLabel = new BranchLabel(this);
/* 2615 */           ExceptionLabel anyExceptionHandler = new ExceptionLabel(this, TypeBinding.LONG);
/* 2616 */           anyExceptionHandler.placeStart();
/* 2617 */           aload_0();
/* 2618 */           fieldAccess(-78, fieldBinding, null);
/* 2619 */           invokeEnumOrdinal(enumBinding.constantPoolName());
/* 2620 */           generateInlinedValue(fieldBinding.id + 1);
/* 2621 */           iastore();
/* 2622 */           anyExceptionHandler.placeEnd();
/* 2623 */           goto_(endLabel);
/*      */ 
/* 2625 */           pushExceptionOnStack(TypeBinding.LONG);
/* 2626 */           anyExceptionHandler.place();
/* 2627 */           pop();
/* 2628 */           endLabel.place();
/*      */         }
/*      */       }
/*      */     }
/* 2632 */     aload_0();
/* 2633 */     dup();
/* 2634 */     fieldAccess(-77, syntheticFieldBinding, null);
/* 2635 */     areturn();
/* 2636 */     removeVariable(localVariableBinding);
/*      */   }
/*      */ 
/*      */   public void generateSyntheticEnclosingInstanceValues(BlockScope currentScope, ReferenceBinding targetType, Expression enclosingInstance, ASTNode invocationSite)
/*      */   {
/* 2645 */     ReferenceBinding checkedTargetType = targetType.isAnonymousType() ? (ReferenceBinding)targetType.superclass().erasure() : targetType;
/* 2646 */     boolean hasExtraEnclosingInstance = enclosingInstance != null;
/* 2647 */     if ((hasExtraEnclosingInstance) && (
/* 2648 */       (!checkedTargetType.isNestedType()) || (checkedTargetType.isStatic()))) {
/* 2649 */       currentScope.problemReporter().unnecessaryEnclosingInstanceSpecification(enclosingInstance, checkedTargetType);
/* 2650 */       return;
/*      */     }
/*      */     ReferenceBinding[] syntheticArgumentTypes;
/* 2655 */     if ((syntheticArgumentTypes = targetType.syntheticEnclosingInstanceTypes()) != null)
/*      */     {
/* 2657 */       ReferenceBinding targetEnclosingType = checkedTargetType.enclosingType();
/* 2658 */       long compliance = currentScope.compilerOptions().complianceLevel;
/*      */       boolean denyEnclosingArgInConstructorCall;
/*      */       boolean denyEnclosingArgInConstructorCall;
/* 2663 */       if (compliance <= 3080192L) {
/* 2664 */         denyEnclosingArgInConstructorCall = invocationSite instanceof AllocationExpression;
/*      */       }
/*      */       else
/*      */       {
/*      */         boolean denyEnclosingArgInConstructorCall;
/* 2665 */         if (compliance == 3145728L) {
/* 2666 */           denyEnclosingArgInConstructorCall = ((invocationSite instanceof AllocationExpression)) || (
/* 2667 */             ((invocationSite instanceof ExplicitConstructorCall)) && (((ExplicitConstructorCall)invocationSite).isSuperAccess()));
/*      */         }
/*      */         else {
/* 2670 */           denyEnclosingArgInConstructorCall = (((invocationSite instanceof AllocationExpression)) || (
/* 2671 */             ((invocationSite instanceof ExplicitConstructorCall)) && (((ExplicitConstructorCall)invocationSite).isSuperAccess()))) && 
/* 2672 */             (!targetType.isLocalType());
/*      */         }
/*      */       }
/* 2675 */       boolean complyTo14 = compliance >= 3145728L;
/* 2676 */       int i = 0; for (int max = syntheticArgumentTypes.length; i < max; i++) {
/* 2677 */         ReferenceBinding syntheticArgType = syntheticArgumentTypes[i];
/* 2678 */         if ((hasExtraEnclosingInstance) && (syntheticArgType == targetEnclosingType)) {
/* 2679 */           hasExtraEnclosingInstance = false;
/* 2680 */           enclosingInstance.generateCode(currentScope, this, true);
/* 2681 */           if (complyTo14) {
/* 2682 */             dup();
/* 2683 */             invokeObjectGetClass();
/* 2684 */             pop();
/*      */           }
/*      */         } else {
/* 2687 */           Object[] emulationPath = currentScope.getEmulationPath(
/* 2688 */             syntheticArgType, 
/* 2689 */             false, 
/* 2690 */             denyEnclosingArgInConstructorCall);
/* 2691 */           generateOuterAccess(emulationPath, invocationSite, syntheticArgType, currentScope);
/*      */         }
/*      */       }
/* 2694 */       if (hasExtraEnclosingInstance)
/* 2695 */         currentScope.problemReporter().unnecessaryEnclosingInstanceSpecification(enclosingInstance, checkedTargetType);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void generateSyntheticOuterArgumentValues(BlockScope currentScope, ReferenceBinding targetType, ASTNode invocationSite)
/*      */   {
/*      */     SyntheticArgumentBinding[] syntheticArguments;
/* 2708 */     if ((syntheticArguments = targetType.syntheticOuterLocalVariables()) != null) {
/* 2709 */       int i = 0; for (int max = syntheticArguments.length; i < max; i++) {
/* 2710 */         LocalVariableBinding targetVariable = syntheticArguments[i].actualOuterLocalVariable;
/* 2711 */         VariableBinding[] emulationPath = currentScope.getEmulationPath(targetVariable);
/* 2712 */         generateOuterAccess(emulationPath, invocationSite, targetVariable, currentScope);
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   public void generateUnboxingConversion(int unboxedTypeID) {
/* 2718 */     switch (unboxedTypeID)
/*      */     {
/*      */     case 3:
/* 2721 */       invoke(
/* 2722 */         -74, 
/* 2723 */         1, 
/* 2724 */         1, 
/* 2725 */         ConstantPool.JavaLangByteConstantPoolName, 
/* 2726 */         ConstantPool.BYTEVALUE_BYTE_METHOD_NAME, 
/* 2727 */         ConstantPool.BYTEVALUE_BYTE_METHOD_SIGNATURE);
/* 2728 */       break;
/*      */     case 4:
/* 2731 */       invoke(
/* 2732 */         -74, 
/* 2733 */         1, 
/* 2734 */         1, 
/* 2735 */         ConstantPool.JavaLangShortConstantPoolName, 
/* 2736 */         ConstantPool.SHORTVALUE_SHORT_METHOD_NAME, 
/* 2737 */         ConstantPool.SHORTVALUE_SHORT_METHOD_SIGNATURE);
/* 2738 */       break;
/*      */     case 2:
/* 2741 */       invoke(
/* 2742 */         -74, 
/* 2743 */         1, 
/* 2744 */         1, 
/* 2745 */         ConstantPool.JavaLangCharacterConstantPoolName, 
/* 2746 */         ConstantPool.CHARVALUE_CHARACTER_METHOD_NAME, 
/* 2747 */         ConstantPool.CHARVALUE_CHARACTER_METHOD_SIGNATURE);
/* 2748 */       break;
/*      */     case 10:
/* 2751 */       invoke(
/* 2752 */         -74, 
/* 2753 */         1, 
/* 2754 */         1, 
/* 2755 */         ConstantPool.JavaLangIntegerConstantPoolName, 
/* 2756 */         ConstantPool.INTVALUE_INTEGER_METHOD_NAME, 
/* 2757 */         ConstantPool.INTVALUE_INTEGER_METHOD_SIGNATURE);
/* 2758 */       break;
/*      */     case 7:
/* 2761 */       invoke(
/* 2762 */         -74, 
/* 2763 */         1, 
/* 2764 */         2, 
/* 2765 */         ConstantPool.JavaLangLongConstantPoolName, 
/* 2766 */         ConstantPool.LONGVALUE_LONG_METHOD_NAME, 
/* 2767 */         ConstantPool.LONGVALUE_LONG_METHOD_SIGNATURE);
/* 2768 */       break;
/*      */     case 9:
/* 2771 */       invoke(
/* 2772 */         -74, 
/* 2773 */         1, 
/* 2774 */         1, 
/* 2775 */         ConstantPool.JavaLangFloatConstantPoolName, 
/* 2776 */         ConstantPool.FLOATVALUE_FLOAT_METHOD_NAME, 
/* 2777 */         ConstantPool.FLOATVALUE_FLOAT_METHOD_SIGNATURE);
/* 2778 */       break;
/*      */     case 8:
/* 2781 */       invoke(
/* 2782 */         -74, 
/* 2783 */         1, 
/* 2784 */         2, 
/* 2785 */         ConstantPool.JavaLangDoubleConstantPoolName, 
/* 2786 */         ConstantPool.DOUBLEVALUE_DOUBLE_METHOD_NAME, 
/* 2787 */         ConstantPool.DOUBLEVALUE_DOUBLE_METHOD_SIGNATURE);
/* 2788 */       break;
/*      */     case 5:
/* 2791 */       invoke(
/* 2792 */         -74, 
/* 2793 */         1, 
/* 2794 */         1, 
/* 2795 */         ConstantPool.JavaLangBooleanConstantPoolName, 
/* 2796 */         ConstantPool.BOOLEANVALUE_BOOLEAN_METHOD_NAME, 
/* 2797 */         ConstantPool.BOOLEANVALUE_BOOLEAN_METHOD_SIGNATURE);
/*      */     case 6:
/*      */     }
/*      */   }
/*      */ 
/*      */   public void generateWideRevertedConditionalBranch(byte revertedOpcode, BranchLabel wideTarget)
/*      */   {
/* 2810 */     BranchLabel intermediate = new BranchLabel(this);
/* 2811 */     if (this.classFileOffset >= this.bCodeStream.length) {
/* 2812 */       resizeByteArray();
/*      */     }
/* 2814 */     this.position += 1;
/* 2815 */     this.bCodeStream[(this.classFileOffset++)] = revertedOpcode;
/* 2816 */     intermediate.branch();
/* 2817 */     goto_w(wideTarget);
/* 2818 */     intermediate.place();
/*      */   }
/*      */ 
/*      */   public void getBaseTypeValue(int baseTypeID) {
/* 2822 */     switch (baseTypeID)
/*      */     {
/*      */     case 3:
/* 2825 */       invoke(
/* 2826 */         -74, 
/* 2827 */         1, 
/* 2828 */         1, 
/* 2829 */         ConstantPool.JavaLangByteConstantPoolName, 
/* 2830 */         ConstantPool.BYTEVALUE_BYTE_METHOD_NAME, 
/* 2831 */         ConstantPool.BYTEVALUE_BYTE_METHOD_SIGNATURE);
/* 2832 */       break;
/*      */     case 4:
/* 2835 */       invoke(
/* 2836 */         -74, 
/* 2837 */         1, 
/* 2838 */         1, 
/* 2839 */         ConstantPool.JavaLangShortConstantPoolName, 
/* 2840 */         ConstantPool.SHORTVALUE_SHORT_METHOD_NAME, 
/* 2841 */         ConstantPool.SHORTVALUE_SHORT_METHOD_SIGNATURE);
/* 2842 */       break;
/*      */     case 2:
/* 2845 */       invoke(
/* 2846 */         -74, 
/* 2847 */         1, 
/* 2848 */         1, 
/* 2849 */         ConstantPool.JavaLangCharacterConstantPoolName, 
/* 2850 */         ConstantPool.CHARVALUE_CHARACTER_METHOD_NAME, 
/* 2851 */         ConstantPool.CHARVALUE_CHARACTER_METHOD_SIGNATURE);
/* 2852 */       break;
/*      */     case 10:
/* 2855 */       invoke(
/* 2856 */         -74, 
/* 2857 */         1, 
/* 2858 */         1, 
/* 2859 */         ConstantPool.JavaLangIntegerConstantPoolName, 
/* 2860 */         ConstantPool.INTVALUE_INTEGER_METHOD_NAME, 
/* 2861 */         ConstantPool.INTVALUE_INTEGER_METHOD_SIGNATURE);
/* 2862 */       break;
/*      */     case 7:
/* 2865 */       invoke(
/* 2866 */         -74, 
/* 2867 */         1, 
/* 2868 */         2, 
/* 2869 */         ConstantPool.JavaLangLongConstantPoolName, 
/* 2870 */         ConstantPool.LONGVALUE_LONG_METHOD_NAME, 
/* 2871 */         ConstantPool.LONGVALUE_LONG_METHOD_SIGNATURE);
/* 2872 */       break;
/*      */     case 9:
/* 2875 */       invoke(
/* 2876 */         -74, 
/* 2877 */         1, 
/* 2878 */         1, 
/* 2879 */         ConstantPool.JavaLangFloatConstantPoolName, 
/* 2880 */         ConstantPool.FLOATVALUE_FLOAT_METHOD_NAME, 
/* 2881 */         ConstantPool.FLOATVALUE_FLOAT_METHOD_SIGNATURE);
/* 2882 */       break;
/*      */     case 8:
/* 2885 */       invoke(
/* 2886 */         -74, 
/* 2887 */         1, 
/* 2888 */         2, 
/* 2889 */         ConstantPool.JavaLangDoubleConstantPoolName, 
/* 2890 */         ConstantPool.DOUBLEVALUE_DOUBLE_METHOD_NAME, 
/* 2891 */         ConstantPool.DOUBLEVALUE_DOUBLE_METHOD_SIGNATURE);
/* 2892 */       break;
/*      */     case 5:
/* 2895 */       invoke(
/* 2896 */         -74, 
/* 2897 */         1, 
/* 2898 */         1, 
/* 2899 */         ConstantPool.JavaLangBooleanConstantPoolName, 
/* 2900 */         ConstantPool.BOOLEANVALUE_BOOLEAN_METHOD_NAME, 
/* 2901 */         ConstantPool.BOOLEANVALUE_BOOLEAN_METHOD_SIGNATURE);
/*      */     case 6:
/*      */     }
/*      */   }
/*      */ 
/*      */   public final byte[] getContents()
/*      */   {
/*      */     byte[] contents;
/* 2907 */     System.arraycopy(this.bCodeStream, 0, contents = new byte[this.position], 0, this.position);
/* 2908 */     return contents;
/*      */   }
/*      */ 
/*      */   public static TypeBinding getConstantPoolDeclaringClass(Scope currentScope, FieldBinding codegenBinding, TypeBinding actualReceiverType, boolean isImplicitThisReceiver)
/*      */   {
/* 2920 */     ReferenceBinding constantPoolDeclaringClass = codegenBinding.declaringClass;
/*      */ 
/* 2925 */     if ((constantPoolDeclaringClass != actualReceiverType.erasure()) && 
/* 2926 */       (!actualReceiverType.isArrayType()) && 
/* 2927 */       (constantPoolDeclaringClass != null) && 
/* 2928 */       (codegenBinding.constant() == Constant.NotAConstant)) {
/* 2929 */       CompilerOptions options = currentScope.compilerOptions();
/* 2930 */       if (((options.targetJDK >= 3014656L) && 
/* 2931 */         ((options.complianceLevel >= 3145728L) || (!isImplicitThisReceiver) || (!codegenBinding.isStatic())) && 
/* 2932 */         (constantPoolDeclaringClass.id != 1)) || 
/* 2933 */         (!constantPoolDeclaringClass.canBeSeenBy(currentScope)))
/*      */       {
/* 2935 */         return actualReceiverType.erasure();
/*      */       }
/*      */     }
/* 2938 */     return constantPoolDeclaringClass;
/*      */   }
/*      */ 
/*      */   public static TypeBinding getConstantPoolDeclaringClass(Scope currentScope, MethodBinding codegenBinding, TypeBinding actualReceiverType, boolean isImplicitThisReceiver)
/*      */   {
/* 2950 */     TypeBinding constantPoolDeclaringClass = codegenBinding.declaringClass;
/*      */ 
/* 2953 */     if (codegenBinding == currentScope.environment().arrayClone) {
/* 2954 */       CompilerOptions options = currentScope.compilerOptions();
/* 2955 */       if (options.sourceLevel > 3145728L) {
/* 2956 */         constantPoolDeclaringClass = actualReceiverType.erasure();
/*      */       }
/*      */ 
/*      */     }
/* 2963 */     else if ((constantPoolDeclaringClass != actualReceiverType.erasure()) && (!actualReceiverType.isArrayType())) {
/* 2964 */       CompilerOptions options = currentScope.compilerOptions();
/* 2965 */       if (((options.targetJDK >= 3014656L) && 
/* 2966 */         ((options.complianceLevel >= 3145728L) || (!isImplicitThisReceiver) || (!codegenBinding.isStatic())) && 
/* 2967 */         (codegenBinding.declaringClass.id != 1)) || 
/* 2968 */         (!codegenBinding.declaringClass.canBeSeenBy(currentScope))) {
/* 2969 */         constantPoolDeclaringClass = actualReceiverType.erasure();
/*      */       }
/*      */     }
/*      */ 
/* 2973 */     return constantPoolDeclaringClass;
/*      */   }
/*      */   protected int getPosition() {
/* 2976 */     return this.position;
/*      */   }
/*      */ 
/*      */   public void getTYPE(int baseTypeID) {
/* 2980 */     this.countLabels = 0;
/* 2981 */     switch (baseTypeID)
/*      */     {
/*      */     case 3:
/* 2984 */       fieldAccess(
/* 2985 */         -78, 
/* 2986 */         1, 
/* 2987 */         ConstantPool.JavaLangByteConstantPoolName, 
/* 2988 */         ConstantPool.TYPE, 
/* 2989 */         ConstantPool.JavaLangClassSignature);
/* 2990 */       break;
/*      */     case 4:
/* 2993 */       fieldAccess(
/* 2994 */         -78, 
/* 2995 */         1, 
/* 2996 */         ConstantPool.JavaLangShortConstantPoolName, 
/* 2997 */         ConstantPool.TYPE, 
/* 2998 */         ConstantPool.JavaLangClassSignature);
/* 2999 */       break;
/*      */     case 2:
/* 3002 */       fieldAccess(
/* 3003 */         -78, 
/* 3004 */         1, 
/* 3005 */         ConstantPool.JavaLangCharacterConstantPoolName, 
/* 3006 */         ConstantPool.TYPE, 
/* 3007 */         ConstantPool.JavaLangClassSignature);
/* 3008 */       break;
/*      */     case 10:
/* 3011 */       fieldAccess(
/* 3012 */         -78, 
/* 3013 */         1, 
/* 3014 */         ConstantPool.JavaLangIntegerConstantPoolName, 
/* 3015 */         ConstantPool.TYPE, 
/* 3016 */         ConstantPool.JavaLangClassSignature);
/* 3017 */       break;
/*      */     case 7:
/* 3020 */       fieldAccess(
/* 3021 */         -78, 
/* 3022 */         1, 
/* 3023 */         ConstantPool.JavaLangLongConstantPoolName, 
/* 3024 */         ConstantPool.TYPE, 
/* 3025 */         ConstantPool.JavaLangClassSignature);
/* 3026 */       break;
/*      */     case 9:
/* 3029 */       fieldAccess(
/* 3030 */         -78, 
/* 3031 */         1, 
/* 3032 */         ConstantPool.JavaLangFloatConstantPoolName, 
/* 3033 */         ConstantPool.TYPE, 
/* 3034 */         ConstantPool.JavaLangClassSignature);
/* 3035 */       break;
/*      */     case 8:
/* 3038 */       fieldAccess(
/* 3039 */         -78, 
/* 3040 */         1, 
/* 3041 */         ConstantPool.JavaLangDoubleConstantPoolName, 
/* 3042 */         ConstantPool.TYPE, 
/* 3043 */         ConstantPool.JavaLangClassSignature);
/* 3044 */       break;
/*      */     case 5:
/* 3047 */       fieldAccess(
/* 3048 */         -78, 
/* 3049 */         1, 
/* 3050 */         ConstantPool.JavaLangBooleanConstantPoolName, 
/* 3051 */         ConstantPool.TYPE, 
/* 3052 */         ConstantPool.JavaLangClassSignature);
/* 3053 */       break;
/*      */     case 6:
/* 3056 */       fieldAccess(
/* 3057 */         -78, 
/* 3058 */         1, 
/* 3059 */         ConstantPool.JavaLangVoidConstantPoolName, 
/* 3060 */         ConstantPool.TYPE, 
/* 3061 */         ConstantPool.JavaLangClassSignature);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void goto_(BranchLabel label)
/*      */   {
/* 3070 */     if (this.wideMode) {
/* 3071 */       goto_w(label);
/* 3072 */       return;
/*      */     }
/* 3074 */     if (this.classFileOffset >= this.bCodeStream.length) {
/* 3075 */       resizeByteArray();
/*      */     }
/* 3077 */     boolean chained = inlineForwardReferencesFromLabelsTargeting(label, this.position);
/*      */ 
/* 3096 */     if ((chained) && (this.lastAbruptCompletion == this.position)) {
/* 3097 */       if (label.position != -1) {
/* 3098 */         int[] forwardRefs = label.forwardReferences();
/* 3099 */         int i = 0; for (int max = label.forwardReferenceCount(); i < max; i++) {
/* 3100 */           writePosition(label, forwardRefs[i]);
/*      */         }
/* 3102 */         this.countLabels = 0;
/*      */       }
/* 3104 */       return;
/*      */     }
/* 3106 */     this.position += 1;
/* 3107 */     this.bCodeStream[(this.classFileOffset++)] = -89;
/* 3108 */     label.branch();
/* 3109 */     this.lastAbruptCompletion = this.position;
/*      */   }
/*      */ 
/*      */   public void goto_w(BranchLabel label) {
/* 3113 */     if (this.classFileOffset >= this.bCodeStream.length) {
/* 3114 */       resizeByteArray();
/*      */     }
/* 3116 */     this.position += 1;
/* 3117 */     this.bCodeStream[(this.classFileOffset++)] = -56;
/* 3118 */     label.branchWide();
/* 3119 */     this.lastAbruptCompletion = this.position;
/*      */   }
/*      */ 
/*      */   public void i2b() {
/* 3123 */     this.countLabels = 0;
/* 3124 */     if (this.classFileOffset >= this.bCodeStream.length) {
/* 3125 */       resizeByteArray();
/*      */     }
/* 3127 */     this.position += 1;
/* 3128 */     this.bCodeStream[(this.classFileOffset++)] = -111;
/*      */   }
/*      */ 
/*      */   public void i2c() {
/* 3132 */     this.countLabels = 0;
/* 3133 */     if (this.classFileOffset >= this.bCodeStream.length) {
/* 3134 */       resizeByteArray();
/*      */     }
/* 3136 */     this.position += 1;
/* 3137 */     this.bCodeStream[(this.classFileOffset++)] = -110;
/*      */   }
/*      */ 
/*      */   public void i2d() {
/* 3141 */     this.countLabels = 0;
/* 3142 */     this.stackDepth += 1;
/* 3143 */     if (this.stackDepth > this.stackMax)
/* 3144 */       this.stackMax = this.stackDepth;
/* 3145 */     if (this.classFileOffset >= this.bCodeStream.length) {
/* 3146 */       resizeByteArray();
/*      */     }
/* 3148 */     this.position += 1;
/* 3149 */     this.bCodeStream[(this.classFileOffset++)] = -121;
/*      */   }
/*      */ 
/*      */   public void i2f() {
/* 3153 */     this.countLabels = 0;
/* 3154 */     if (this.classFileOffset >= this.bCodeStream.length) {
/* 3155 */       resizeByteArray();
/*      */     }
/* 3157 */     this.position += 1;
/* 3158 */     this.bCodeStream[(this.classFileOffset++)] = -122;
/*      */   }
/*      */ 
/*      */   public void i2l() {
/* 3162 */     this.countLabels = 0;
/* 3163 */     this.stackDepth += 1;
/* 3164 */     if (this.stackDepth > this.stackMax)
/* 3165 */       this.stackMax = this.stackDepth;
/* 3166 */     if (this.classFileOffset >= this.bCodeStream.length) {
/* 3167 */       resizeByteArray();
/*      */     }
/* 3169 */     this.position += 1;
/* 3170 */     this.bCodeStream[(this.classFileOffset++)] = -123;
/*      */   }
/*      */ 
/*      */   public void i2s() {
/* 3174 */     this.countLabels = 0;
/* 3175 */     if (this.classFileOffset >= this.bCodeStream.length) {
/* 3176 */       resizeByteArray();
/*      */     }
/* 3178 */     this.position += 1;
/* 3179 */     this.bCodeStream[(this.classFileOffset++)] = -109;
/*      */   }
/*      */ 
/*      */   public void iadd() {
/* 3183 */     this.countLabels = 0;
/* 3184 */     this.stackDepth -= 1;
/* 3185 */     if (this.classFileOffset >= this.bCodeStream.length) {
/* 3186 */       resizeByteArray();
/*      */     }
/* 3188 */     this.position += 1;
/* 3189 */     this.bCodeStream[(this.classFileOffset++)] = 96;
/*      */   }
/*      */ 
/*      */   public void iaload() {
/* 3193 */     this.countLabels = 0;
/* 3194 */     this.stackDepth -= 1;
/* 3195 */     if (this.classFileOffset >= this.bCodeStream.length) {
/* 3196 */       resizeByteArray();
/*      */     }
/* 3198 */     this.position += 1;
/* 3199 */     this.bCodeStream[(this.classFileOffset++)] = 46;
/*      */   }
/*      */ 
/*      */   public void iand() {
/* 3203 */     this.countLabels = 0;
/* 3204 */     this.stackDepth -= 1;
/* 3205 */     if (this.classFileOffset >= this.bCodeStream.length) {
/* 3206 */       resizeByteArray();
/*      */     }
/* 3208 */     this.position += 1;
/* 3209 */     this.bCodeStream[(this.classFileOffset++)] = 126;
/*      */   }
/*      */ 
/*      */   public void iastore() {
/* 3213 */     this.countLabels = 0;
/* 3214 */     this.stackDepth -= 3;
/* 3215 */     if (this.classFileOffset >= this.bCodeStream.length) {
/* 3216 */       resizeByteArray();
/*      */     }
/* 3218 */     this.position += 1;
/* 3219 */     this.bCodeStream[(this.classFileOffset++)] = 79;
/*      */   }
/*      */ 
/*      */   public void iconst_0() {
/* 3223 */     this.countLabels = 0;
/* 3224 */     this.stackDepth += 1;
/* 3225 */     if (this.stackDepth > this.stackMax)
/* 3226 */       this.stackMax = this.stackDepth;
/* 3227 */     if (this.classFileOffset >= this.bCodeStream.length) {
/* 3228 */       resizeByteArray();
/*      */     }
/* 3230 */     this.position += 1;
/* 3231 */     this.bCodeStream[(this.classFileOffset++)] = 3;
/*      */   }
/*      */ 
/*      */   public void iconst_1() {
/* 3235 */     this.countLabels = 0;
/* 3236 */     this.stackDepth += 1;
/* 3237 */     if (this.stackDepth > this.stackMax)
/* 3238 */       this.stackMax = this.stackDepth;
/* 3239 */     if (this.classFileOffset >= this.bCodeStream.length) {
/* 3240 */       resizeByteArray();
/*      */     }
/* 3242 */     this.position += 1;
/* 3243 */     this.bCodeStream[(this.classFileOffset++)] = 4;
/*      */   }
/*      */ 
/*      */   public void iconst_2() {
/* 3247 */     this.countLabels = 0;
/* 3248 */     this.stackDepth += 1;
/* 3249 */     if (this.stackDepth > this.stackMax)
/* 3250 */       this.stackMax = this.stackDepth;
/* 3251 */     if (this.classFileOffset >= this.bCodeStream.length) {
/* 3252 */       resizeByteArray();
/*      */     }
/* 3254 */     this.position += 1;
/* 3255 */     this.bCodeStream[(this.classFileOffset++)] = 5;
/*      */   }
/*      */   public void iconst_3() {
/* 3258 */     this.countLabels = 0;
/* 3259 */     this.stackDepth += 1;
/* 3260 */     if (this.stackDepth > this.stackMax)
/* 3261 */       this.stackMax = this.stackDepth;
/* 3262 */     if (this.classFileOffset >= this.bCodeStream.length) {
/* 3263 */       resizeByteArray();
/*      */     }
/* 3265 */     this.position += 1;
/* 3266 */     this.bCodeStream[(this.classFileOffset++)] = 6;
/*      */   }
/*      */ 
/*      */   public void iconst_4() {
/* 3270 */     this.countLabels = 0;
/* 3271 */     this.stackDepth += 1;
/* 3272 */     if (this.stackDepth > this.stackMax)
/* 3273 */       this.stackMax = this.stackDepth;
/* 3274 */     if (this.classFileOffset >= this.bCodeStream.length) {
/* 3275 */       resizeByteArray();
/*      */     }
/* 3277 */     this.position += 1;
/* 3278 */     this.bCodeStream[(this.classFileOffset++)] = 7;
/*      */   }
/*      */ 
/*      */   public void iconst_5() {
/* 3282 */     this.countLabels = 0;
/* 3283 */     this.stackDepth += 1;
/* 3284 */     if (this.stackDepth > this.stackMax)
/* 3285 */       this.stackMax = this.stackDepth;
/* 3286 */     if (this.classFileOffset >= this.bCodeStream.length) {
/* 3287 */       resizeByteArray();
/*      */     }
/* 3289 */     this.position += 1;
/* 3290 */     this.bCodeStream[(this.classFileOffset++)] = 8;
/*      */   }
/*      */ 
/*      */   public void iconst_m1() {
/* 3294 */     this.countLabels = 0;
/* 3295 */     this.stackDepth += 1;
/* 3296 */     if (this.stackDepth > this.stackMax)
/* 3297 */       this.stackMax = this.stackDepth;
/* 3298 */     if (this.classFileOffset >= this.bCodeStream.length) {
/* 3299 */       resizeByteArray();
/*      */     }
/* 3301 */     this.position += 1;
/* 3302 */     this.bCodeStream[(this.classFileOffset++)] = 2;
/*      */   }
/*      */ 
/*      */   public void idiv() {
/* 3306 */     this.countLabels = 0;
/* 3307 */     this.stackDepth -= 1;
/* 3308 */     if (this.classFileOffset >= this.bCodeStream.length) {
/* 3309 */       resizeByteArray();
/*      */     }
/* 3311 */     this.position += 1;
/* 3312 */     this.bCodeStream[(this.classFileOffset++)] = 108;
/*      */   }
/*      */ 
/*      */   public void if_acmpeq(BranchLabel lbl) {
/* 3316 */     this.countLabels = 0;
/* 3317 */     this.stackDepth -= 2;
/* 3318 */     if (this.wideMode) {
/* 3319 */       generateWideRevertedConditionalBranch(-90, lbl);
/*      */     } else {
/* 3321 */       if (this.classFileOffset >= this.bCodeStream.length) {
/* 3322 */         resizeByteArray();
/*      */       }
/* 3324 */       this.position += 1;
/* 3325 */       this.bCodeStream[(this.classFileOffset++)] = -91;
/* 3326 */       lbl.branch();
/*      */     }
/*      */   }
/*      */ 
/*      */   public void if_acmpne(BranchLabel lbl) {
/* 3331 */     this.countLabels = 0;
/* 3332 */     this.stackDepth -= 2;
/* 3333 */     if (this.wideMode) {
/* 3334 */       generateWideRevertedConditionalBranch(-91, lbl);
/*      */     } else {
/* 3336 */       if (this.classFileOffset >= this.bCodeStream.length) {
/* 3337 */         resizeByteArray();
/*      */       }
/* 3339 */       this.position += 1;
/* 3340 */       this.bCodeStream[(this.classFileOffset++)] = -90;
/* 3341 */       lbl.branch();
/*      */     }
/*      */   }
/*      */ 
/*      */   public void if_icmpeq(BranchLabel lbl) {
/* 3346 */     this.countLabels = 0;
/* 3347 */     this.stackDepth -= 2;
/* 3348 */     if (this.wideMode) {
/* 3349 */       generateWideRevertedConditionalBranch(-96, lbl);
/*      */     } else {
/* 3351 */       if (this.classFileOffset >= this.bCodeStream.length) {
/* 3352 */         resizeByteArray();
/*      */       }
/* 3354 */       this.position += 1;
/* 3355 */       this.bCodeStream[(this.classFileOffset++)] = -97;
/* 3356 */       lbl.branch();
/*      */     }
/*      */   }
/*      */ 
/*      */   public void if_icmpge(BranchLabel lbl) {
/* 3361 */     this.countLabels = 0;
/* 3362 */     this.stackDepth -= 2;
/* 3363 */     if (this.wideMode) {
/* 3364 */       generateWideRevertedConditionalBranch(-95, lbl);
/*      */     } else {
/* 3366 */       if (this.classFileOffset >= this.bCodeStream.length) {
/* 3367 */         resizeByteArray();
/*      */       }
/* 3369 */       this.position += 1;
/* 3370 */       this.bCodeStream[(this.classFileOffset++)] = -94;
/* 3371 */       lbl.branch();
/*      */     }
/*      */   }
/*      */ 
/*      */   public void if_icmpgt(BranchLabel lbl) {
/* 3376 */     this.countLabels = 0;
/* 3377 */     this.stackDepth -= 2;
/* 3378 */     if (this.wideMode) {
/* 3379 */       generateWideRevertedConditionalBranch(-92, lbl);
/*      */     } else {
/* 3381 */       if (this.classFileOffset >= this.bCodeStream.length) {
/* 3382 */         resizeByteArray();
/*      */       }
/* 3384 */       this.position += 1;
/* 3385 */       this.bCodeStream[(this.classFileOffset++)] = -93;
/* 3386 */       lbl.branch();
/*      */     }
/*      */   }
/*      */ 
/*      */   public void if_icmple(BranchLabel lbl) {
/* 3391 */     this.countLabels = 0;
/* 3392 */     this.stackDepth -= 2;
/* 3393 */     if (this.wideMode) {
/* 3394 */       generateWideRevertedConditionalBranch(-93, lbl);
/*      */     } else {
/* 3396 */       if (this.classFileOffset >= this.bCodeStream.length) {
/* 3397 */         resizeByteArray();
/*      */       }
/* 3399 */       this.position += 1;
/* 3400 */       this.bCodeStream[(this.classFileOffset++)] = -92;
/* 3401 */       lbl.branch();
/*      */     }
/*      */   }
/*      */ 
/*      */   public void if_icmplt(BranchLabel lbl) {
/* 3406 */     this.countLabels = 0;
/* 3407 */     this.stackDepth -= 2;
/* 3408 */     if (this.wideMode) {
/* 3409 */       generateWideRevertedConditionalBranch(-94, lbl);
/*      */     } else {
/* 3411 */       if (this.classFileOffset >= this.bCodeStream.length) {
/* 3412 */         resizeByteArray();
/*      */       }
/* 3414 */       this.position += 1;
/* 3415 */       this.bCodeStream[(this.classFileOffset++)] = -95;
/* 3416 */       lbl.branch();
/*      */     }
/*      */   }
/*      */ 
/*      */   public void if_icmpne(BranchLabel lbl) {
/* 3421 */     this.countLabels = 0;
/* 3422 */     this.stackDepth -= 2;
/* 3423 */     if (this.wideMode) {
/* 3424 */       generateWideRevertedConditionalBranch(-97, lbl);
/*      */     } else {
/* 3426 */       if (this.classFileOffset >= this.bCodeStream.length) {
/* 3427 */         resizeByteArray();
/*      */       }
/* 3429 */       this.position += 1;
/* 3430 */       this.bCodeStream[(this.classFileOffset++)] = -96;
/* 3431 */       lbl.branch();
/*      */     }
/*      */   }
/*      */ 
/*      */   public void ifeq(BranchLabel lbl) {
/* 3436 */     this.countLabels = 0;
/* 3437 */     this.stackDepth -= 1;
/* 3438 */     if (this.wideMode) {
/* 3439 */       generateWideRevertedConditionalBranch(-102, lbl);
/*      */     } else {
/* 3441 */       if (this.classFileOffset >= this.bCodeStream.length) {
/* 3442 */         resizeByteArray();
/*      */       }
/* 3444 */       this.position += 1;
/* 3445 */       this.bCodeStream[(this.classFileOffset++)] = -103;
/* 3446 */       lbl.branch();
/*      */     }
/*      */   }
/*      */ 
/*      */   public void ifge(BranchLabel lbl) {
/* 3451 */     this.countLabels = 0;
/* 3452 */     this.stackDepth -= 1;
/* 3453 */     if (this.wideMode) {
/* 3454 */       generateWideRevertedConditionalBranch(-101, lbl);
/*      */     } else {
/* 3456 */       if (this.classFileOffset >= this.bCodeStream.length) {
/* 3457 */         resizeByteArray();
/*      */       }
/* 3459 */       this.position += 1;
/* 3460 */       this.bCodeStream[(this.classFileOffset++)] = -100;
/* 3461 */       lbl.branch();
/*      */     }
/*      */   }
/*      */ 
/*      */   public void ifgt(BranchLabel lbl) {
/* 3466 */     this.countLabels = 0;
/* 3467 */     this.stackDepth -= 1;
/* 3468 */     if (this.wideMode) {
/* 3469 */       generateWideRevertedConditionalBranch(-98, lbl);
/*      */     } else {
/* 3471 */       if (this.classFileOffset >= this.bCodeStream.length) {
/* 3472 */         resizeByteArray();
/*      */       }
/* 3474 */       this.position += 1;
/* 3475 */       this.bCodeStream[(this.classFileOffset++)] = -99;
/* 3476 */       lbl.branch();
/*      */     }
/*      */   }
/*      */ 
/*      */   public void ifle(BranchLabel lbl) {
/* 3481 */     this.countLabels = 0;
/* 3482 */     this.stackDepth -= 1;
/* 3483 */     if (this.wideMode) {
/* 3484 */       generateWideRevertedConditionalBranch(-99, lbl);
/*      */     } else {
/* 3486 */       if (this.classFileOffset >= this.bCodeStream.length) {
/* 3487 */         resizeByteArray();
/*      */       }
/* 3489 */       this.position += 1;
/* 3490 */       this.bCodeStream[(this.classFileOffset++)] = -98;
/* 3491 */       lbl.branch();
/*      */     }
/*      */   }
/*      */ 
/*      */   public void iflt(BranchLabel lbl) {
/* 3496 */     this.countLabels = 0;
/* 3497 */     this.stackDepth -= 1;
/* 3498 */     if (this.wideMode) {
/* 3499 */       generateWideRevertedConditionalBranch(-100, lbl);
/*      */     } else {
/* 3501 */       if (this.classFileOffset >= this.bCodeStream.length) {
/* 3502 */         resizeByteArray();
/*      */       }
/* 3504 */       this.position += 1;
/* 3505 */       this.bCodeStream[(this.classFileOffset++)] = -101;
/* 3506 */       lbl.branch();
/*      */     }
/*      */   }
/*      */ 
/*      */   public void ifne(BranchLabel lbl) {
/* 3511 */     this.countLabels = 0;
/* 3512 */     this.stackDepth -= 1;
/* 3513 */     if (this.wideMode) {
/* 3514 */       generateWideRevertedConditionalBranch(-103, lbl);
/*      */     } else {
/* 3516 */       if (this.classFileOffset >= this.bCodeStream.length) {
/* 3517 */         resizeByteArray();
/*      */       }
/* 3519 */       this.position += 1;
/* 3520 */       this.bCodeStream[(this.classFileOffset++)] = -102;
/* 3521 */       lbl.branch();
/*      */     }
/*      */   }
/*      */ 
/*      */   public void ifnonnull(BranchLabel lbl) {
/* 3526 */     this.countLabels = 0;
/* 3527 */     this.stackDepth -= 1;
/* 3528 */     if (this.wideMode) {
/* 3529 */       generateWideRevertedConditionalBranch(-58, lbl);
/*      */     } else {
/* 3531 */       if (this.classFileOffset >= this.bCodeStream.length) {
/* 3532 */         resizeByteArray();
/*      */       }
/* 3534 */       this.position += 1;
/* 3535 */       this.bCodeStream[(this.classFileOffset++)] = -57;
/* 3536 */       lbl.branch();
/*      */     }
/*      */   }
/*      */ 
/*      */   public void ifnull(BranchLabel lbl) {
/* 3541 */     this.countLabels = 0;
/* 3542 */     this.stackDepth -= 1;
/* 3543 */     if (this.wideMode) {
/* 3544 */       generateWideRevertedConditionalBranch(-57, lbl);
/*      */     } else {
/* 3546 */       if (this.classFileOffset >= this.bCodeStream.length) {
/* 3547 */         resizeByteArray();
/*      */       }
/* 3549 */       this.position += 1;
/* 3550 */       this.bCodeStream[(this.classFileOffset++)] = -58;
/* 3551 */       lbl.branch();
/*      */     }
/*      */   }
/*      */ 
/*      */   public final void iinc(int index, int value) {
/* 3556 */     this.countLabels = 0;
/* 3557 */     if ((index > 255) || (value < -128) || (value > 127)) {
/* 3558 */       if (this.classFileOffset + 3 >= this.bCodeStream.length) {
/* 3559 */         resizeByteArray();
/*      */       }
/* 3561 */       this.position += 2;
/* 3562 */       this.bCodeStream[(this.classFileOffset++)] = -60;
/* 3563 */       this.bCodeStream[(this.classFileOffset++)] = -124;
/* 3564 */       writeUnsignedShort(index);
/* 3565 */       writeSignedShort(value);
/*      */     } else {
/* 3567 */       if (this.classFileOffset + 2 >= this.bCodeStream.length) {
/* 3568 */         resizeByteArray();
/*      */       }
/* 3570 */       this.position += 3;
/* 3571 */       this.bCodeStream[(this.classFileOffset++)] = -124;
/* 3572 */       this.bCodeStream[(this.classFileOffset++)] = (byte)index;
/* 3573 */       this.bCodeStream[(this.classFileOffset++)] = (byte)value;
/*      */     }
/*      */   }
/*      */ 
/*      */   public void iload(int iArg) {
/* 3578 */     this.countLabels = 0;
/* 3579 */     this.stackDepth += 1;
/* 3580 */     if (this.maxLocals <= iArg) {
/* 3581 */       this.maxLocals = (iArg + 1);
/*      */     }
/* 3583 */     if (this.stackDepth > this.stackMax)
/* 3584 */       this.stackMax = this.stackDepth;
/* 3585 */     if (iArg > 255) {
/* 3586 */       if (this.classFileOffset + 3 >= this.bCodeStream.length) {
/* 3587 */         resizeByteArray();
/*      */       }
/* 3589 */       this.position += 2;
/* 3590 */       this.bCodeStream[(this.classFileOffset++)] = -60;
/* 3591 */       this.bCodeStream[(this.classFileOffset++)] = 21;
/* 3592 */       writeUnsignedShort(iArg);
/*      */     } else {
/* 3594 */       if (this.classFileOffset + 1 >= this.bCodeStream.length) {
/* 3595 */         resizeByteArray();
/*      */       }
/* 3597 */       this.position += 2;
/* 3598 */       this.bCodeStream[(this.classFileOffset++)] = 21;
/* 3599 */       this.bCodeStream[(this.classFileOffset++)] = (byte)iArg;
/*      */     }
/*      */   }
/*      */ 
/*      */   public void iload_0() {
/* 3604 */     this.countLabels = 0;
/* 3605 */     this.stackDepth += 1;
/* 3606 */     if (this.maxLocals <= 0) {
/* 3607 */       this.maxLocals = 1;
/*      */     }
/* 3609 */     if (this.stackDepth > this.stackMax)
/* 3610 */       this.stackMax = this.stackDepth;
/* 3611 */     if (this.classFileOffset >= this.bCodeStream.length) {
/* 3612 */       resizeByteArray();
/*      */     }
/* 3614 */     this.position += 1;
/* 3615 */     this.bCodeStream[(this.classFileOffset++)] = 26;
/*      */   }
/*      */ 
/*      */   public void iload_1() {
/* 3619 */     this.countLabels = 0;
/* 3620 */     this.stackDepth += 1;
/* 3621 */     if (this.maxLocals <= 1) {
/* 3622 */       this.maxLocals = 2;
/*      */     }
/* 3624 */     if (this.stackDepth > this.stackMax)
/* 3625 */       this.stackMax = this.stackDepth;
/* 3626 */     if (this.classFileOffset >= this.bCodeStream.length) {
/* 3627 */       resizeByteArray();
/*      */     }
/* 3629 */     this.position += 1;
/* 3630 */     this.bCodeStream[(this.classFileOffset++)] = 27;
/*      */   }
/*      */ 
/*      */   public void iload_2() {
/* 3634 */     this.countLabels = 0;
/* 3635 */     this.stackDepth += 1;
/* 3636 */     if (this.maxLocals <= 2) {
/* 3637 */       this.maxLocals = 3;
/*      */     }
/* 3639 */     if (this.stackDepth > this.stackMax)
/* 3640 */       this.stackMax = this.stackDepth;
/* 3641 */     if (this.classFileOffset >= this.bCodeStream.length) {
/* 3642 */       resizeByteArray();
/*      */     }
/* 3644 */     this.position += 1;
/* 3645 */     this.bCodeStream[(this.classFileOffset++)] = 28;
/*      */   }
/*      */ 
/*      */   public void iload_3() {
/* 3649 */     this.countLabels = 0;
/* 3650 */     this.stackDepth += 1;
/* 3651 */     if (this.maxLocals <= 3) {
/* 3652 */       this.maxLocals = 4;
/*      */     }
/* 3654 */     if (this.stackDepth > this.stackMax)
/* 3655 */       this.stackMax = this.stackDepth;
/* 3656 */     if (this.classFileOffset >= this.bCodeStream.length) {
/* 3657 */       resizeByteArray();
/*      */     }
/* 3659 */     this.position += 1;
/* 3660 */     this.bCodeStream[(this.classFileOffset++)] = 29;
/*      */   }
/*      */ 
/*      */   public void imul() {
/* 3664 */     this.countLabels = 0;
/* 3665 */     this.stackDepth -= 1;
/* 3666 */     if (this.classFileOffset >= this.bCodeStream.length) {
/* 3667 */       resizeByteArray();
/*      */     }
/* 3669 */     this.position += 1;
/* 3670 */     this.bCodeStream[(this.classFileOffset++)] = 104;
/*      */   }
/*      */ 
/*      */   public int indexOfSameLineEntrySincePC(int pc, int line) {
/* 3674 */     int index = pc; for (int max = this.pcToSourceMapSize; index < max; index += 2) {
/* 3675 */       if (this.pcToSourceMap[(index + 1)] == line)
/* 3676 */         return index;
/*      */     }
/* 3678 */     return -1;
/*      */   }
/*      */ 
/*      */   public void ineg() {
/* 3682 */     this.countLabels = 0;
/* 3683 */     if (this.classFileOffset >= this.bCodeStream.length) {
/* 3684 */       resizeByteArray();
/*      */     }
/* 3686 */     this.position += 1;
/* 3687 */     this.bCodeStream[(this.classFileOffset++)] = 116;
/*      */   }
/*      */ 
/*      */   public void init(ClassFile targetClassFile) {
/* 3691 */     this.classFile = targetClassFile;
/* 3692 */     this.constantPool = targetClassFile.constantPool;
/* 3693 */     this.bCodeStream = targetClassFile.contents;
/* 3694 */     this.classFileOffset = targetClassFile.contentsOffset;
/* 3695 */     this.startingClassFileOffset = this.classFileOffset;
/* 3696 */     this.pcToSourceMapSize = 0;
/* 3697 */     this.lastEntryPC = 0;
/* 3698 */     int length = this.visibleLocals.length;
/* 3699 */     if (noVisibleLocals.length < length) {
/* 3700 */       noVisibleLocals = new LocalVariableBinding[length];
/*      */     }
/* 3702 */     System.arraycopy(noVisibleLocals, 0, this.visibleLocals, 0, length);
/* 3703 */     this.visibleLocalsCount = 0;
/*      */ 
/* 3705 */     length = this.locals.length;
/* 3706 */     if (noLocals.length < length) {
/* 3707 */       noLocals = new LocalVariableBinding[length];
/*      */     }
/* 3709 */     System.arraycopy(noLocals, 0, this.locals, 0, length);
/* 3710 */     this.allLocalsCounter = 0;
/*      */ 
/* 3712 */     length = this.exceptionLabels.length;
/* 3713 */     if (noExceptionHandlers.length < length) {
/* 3714 */       noExceptionHandlers = new ExceptionLabel[length];
/*      */     }
/* 3716 */     System.arraycopy(noExceptionHandlers, 0, this.exceptionLabels, 0, length);
/* 3717 */     this.exceptionLabelsCounter = 0;
/*      */ 
/* 3719 */     length = this.labels.length;
/* 3720 */     if (noLabels.length < length) {
/* 3721 */       noLabels = new BranchLabel[length];
/*      */     }
/* 3723 */     System.arraycopy(noLabels, 0, this.labels, 0, length);
/* 3724 */     this.countLabels = 0;
/* 3725 */     this.lastAbruptCompletion = -1;
/*      */ 
/* 3727 */     this.stackMax = 0;
/* 3728 */     this.stackDepth = 0;
/* 3729 */     this.maxLocals = 0;
/* 3730 */     this.position = 0;
/*      */   }
/*      */ 
/*      */   public void initializeMaxLocals(MethodBinding methodBinding)
/*      */   {
/* 3737 */     if (methodBinding == null) {
/* 3738 */       this.maxLocals = 0;
/* 3739 */       return;
/*      */     }
/* 3741 */     this.maxLocals = (methodBinding.isStatic() ? 0 : 1);
/* 3742 */     ReferenceBinding declaringClass = methodBinding.declaringClass;
/*      */ 
/* 3744 */     if ((methodBinding.isConstructor()) && (declaringClass.isEnum())) {
/* 3745 */       this.maxLocals += 2;
/*      */     }
/*      */ 
/* 3749 */     if ((methodBinding.isConstructor()) && (declaringClass.isNestedType())) {
/* 3750 */       this.maxLocals += declaringClass.getEnclosingInstancesSlotSize();
/* 3751 */       this.maxLocals += declaringClass.getOuterLocalVariablesSlotSize();
/*      */     }
/*      */     TypeBinding[] parameterTypes;
/* 3754 */     if ((parameterTypes = methodBinding.parameters) != null) {
/* 3755 */       int i = 0; for (int max = parameterTypes.length; i < max; i++)
/* 3756 */         switch (parameterTypes[i].id) {
/*      */         case 7:
/*      */         case 8:
/* 3759 */           this.maxLocals += 2;
/* 3760 */           break;
/*      */         default:
/* 3762 */           this.maxLocals += 1;
/*      */         }
/*      */     }
/*      */   }
/*      */ 
/*      */   public boolean inlineForwardReferencesFromLabelsTargeting(BranchLabel targetLabel, int gotoLocation)
/*      */   {
/* 3772 */     if (targetLabel.delegate != null) return false;
/* 3773 */     int chaining = 0;
/* 3774 */     for (int i = this.countLabels - 1; i >= 0; i--) {
/* 3775 */       BranchLabel currentLabel = this.labels[i];
/* 3776 */       if (currentLabel.position != gotoLocation) break;
/* 3777 */       if (currentLabel == targetLabel) {
/* 3778 */         chaining |= 4;
/*      */       }
/* 3781 */       else if (currentLabel.isStandardLabel()) {
/* 3782 */         if (currentLabel.delegate == null) {
/* 3783 */           targetLabel.becomeDelegateFor(currentLabel);
/* 3784 */           chaining |= 2;
/*      */         }
/*      */       }
/*      */       else
/* 3788 */         chaining |= 4;
/*      */     }
/* 3790 */     return (chaining & 0x6) == 2;
/*      */   }
/*      */ 
/*      */   public void instance_of(TypeBinding typeBinding)
/*      */   {
/* 3798 */     this.countLabels = 0;
/* 3799 */     if (this.classFileOffset + 2 >= this.bCodeStream.length) {
/* 3800 */       resizeByteArray();
/*      */     }
/* 3802 */     this.position += 1;
/* 3803 */     this.bCodeStream[(this.classFileOffset++)] = -63;
/* 3804 */     writeUnsignedShort(this.constantPool.literalIndexForType(typeBinding));
/*      */   }
/*      */ 
/*      */   protected void invoke(byte opcode, int receiverAndArgsSize, int returnTypeSize, char[] declaringClass, char[] selector, char[] signature) {
/* 3808 */     this.countLabels = 0;
/* 3809 */     if (opcode == -71)
/*      */     {
/* 3811 */       if (this.classFileOffset + 4 >= this.bCodeStream.length) {
/* 3812 */         resizeByteArray();
/*      */       }
/* 3814 */       this.position += 3;
/* 3815 */       this.bCodeStream[(this.classFileOffset++)] = opcode;
/* 3816 */       writeUnsignedShort(this.constantPool.literalIndexForMethod(declaringClass, selector, signature, true));
/* 3817 */       this.bCodeStream[(this.classFileOffset++)] = (byte)receiverAndArgsSize;
/* 3818 */       this.bCodeStream[(this.classFileOffset++)] = 0;
/*      */     }
/*      */     else
/*      */     {
/* 3823 */       if (this.classFileOffset + 2 >= this.bCodeStream.length) {
/* 3824 */         resizeByteArray();
/*      */       }
/* 3826 */       this.position += 1;
/* 3827 */       this.bCodeStream[(this.classFileOffset++)] = opcode;
/* 3828 */       writeUnsignedShort(this.constantPool.literalIndexForMethod(declaringClass, selector, signature, false));
/*      */     }
/* 3830 */     this.stackDepth += returnTypeSize - receiverAndArgsSize;
/* 3831 */     if (this.stackDepth > this.stackMax)
/* 3832 */       this.stackMax = this.stackDepth;
/*      */   }
/*      */ 
/*      */   public void invoke(byte opcode, MethodBinding methodBinding, TypeBinding declaringClass)
/*      */   {
/* 3837 */     if (declaringClass == null) declaringClass = methodBinding.declaringClass;
/* 3838 */     if ((declaringClass.tagBits & 0x800) != 0L)
/* 3839 */       Util.recordNestedType(this.classFile, declaringClass);
/*      */     int receiverAndArgsSize;
/*      */     int receiverAndArgsSize;
/* 3843 */     switch (opcode) {
/*      */     case -72:
/* 3845 */       receiverAndArgsSize = 0;
/* 3846 */       break;
/*      */     case -74:
/*      */     case -71:
/* 3849 */       receiverAndArgsSize = 1;
/* 3850 */       break;
/*      */     case -73:
/* 3852 */       int receiverAndArgsSize = 1;
/* 3853 */       if (!methodBinding.isConstructor()) break;
/* 3854 */       if (declaringClass.isNestedType()) {
/* 3855 */         ReferenceBinding nestedType = (ReferenceBinding)declaringClass;
/*      */ 
/* 3857 */         receiverAndArgsSize += nestedType.getEnclosingInstancesSlotSize();
/*      */ 
/* 3859 */         SyntheticArgumentBinding[] syntheticArguments = nestedType.syntheticOuterLocalVariables();
/* 3860 */         if (syntheticArguments != null) {
/* 3861 */           int i = 0; for (int max = syntheticArguments.length; i < max; i++) {
/* 3862 */             switch (syntheticArguments[i].id) {
/*      */             case 7:
/*      */             case 8:
/* 3865 */               receiverAndArgsSize += 2;
/* 3866 */               break;
/*      */             default:
/* 3868 */               receiverAndArgsSize++;
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/*      */ 
/* 3874 */       if (!declaringClass.isEnum())
/*      */         break;
/* 3876 */       receiverAndArgsSize += 2;
/*      */ 
/* 3879 */       break;
/*      */     default:
/* 3881 */       return;
/*      */     }
/*      */     int receiverAndArgsSize;
/* 3884 */     for (int i = methodBinding.parameters.length - 1; i >= 0; i--)
/* 3885 */       switch (methodBinding.parameters[i].id) {
/*      */       case 7:
/*      */       case 8:
/* 3888 */         receiverAndArgsSize += 2;
/* 3889 */         break;
/*      */       default:
/* 3891 */         receiverAndArgsSize++;
/*      */       }
/*      */     int returnTypeSize;
/*      */     int returnTypeSize;
/*      */     int returnTypeSize;
/* 3897 */     switch (methodBinding.returnType.id) {
/*      */     case 7:
/*      */     case 8:
/* 3900 */       returnTypeSize = 2;
/* 3901 */       break;
/*      */     case 6:
/* 3903 */       returnTypeSize = 0;
/* 3904 */       break;
/*      */     default:
/* 3906 */       returnTypeSize = 1;
/*      */     }
/*      */ 
/* 3909 */     invoke(
/* 3910 */       opcode, 
/* 3911 */       receiverAndArgsSize, 
/* 3912 */       returnTypeSize, 
/* 3913 */       declaringClass.constantPoolName(), 
/* 3914 */       methodBinding.selector, 
/* 3915 */       methodBinding.signature(this.classFile));
/*      */   }
/*      */ 
/*      */   protected void invokeAccessibleObjectSetAccessible()
/*      */   {
/* 3920 */     invoke(
/* 3921 */       -74, 
/* 3922 */       2, 
/* 3923 */       0, 
/* 3924 */       ConstantPool.JAVALANGREFLECTACCESSIBLEOBJECT_CONSTANTPOOLNAME, 
/* 3925 */       ConstantPool.SETACCESSIBLE_NAME, 
/* 3926 */       ConstantPool.SETACCESSIBLE_SIGNATURE);
/*      */   }
/*      */ 
/*      */   protected void invokeArrayNewInstance()
/*      */   {
/* 3931 */     invoke(
/* 3932 */       -72, 
/* 3933 */       2, 
/* 3934 */       1, 
/* 3935 */       ConstantPool.JAVALANGREFLECTARRAY_CONSTANTPOOLNAME, 
/* 3936 */       ConstantPool.NewInstance, 
/* 3937 */       ConstantPool.NewInstanceSignature);
/*      */   }
/*      */ 
/*      */   public void invokeClassForName() {
/* 3941 */     invoke(
/* 3942 */       -72, 
/* 3943 */       1, 
/* 3944 */       1, 
/* 3945 */       ConstantPool.JavaLangClassConstantPoolName, 
/* 3946 */       ConstantPool.ForName, 
/* 3947 */       ConstantPool.ForNameSignature);
/*      */   }
/*      */ 
/*      */   protected void invokeClassGetDeclaredConstructor()
/*      */   {
/* 3952 */     invoke(
/* 3953 */       -74, 
/* 3954 */       2, 
/* 3955 */       1, 
/* 3956 */       ConstantPool.JavaLangClassConstantPoolName, 
/* 3957 */       ConstantPool.GETDECLAREDCONSTRUCTOR_NAME, 
/* 3958 */       ConstantPool.GETDECLAREDCONSTRUCTOR_SIGNATURE);
/*      */   }
/*      */ 
/*      */   protected void invokeClassGetDeclaredField()
/*      */   {
/* 3963 */     invoke(
/* 3964 */       -74, 
/* 3965 */       2, 
/* 3966 */       1, 
/* 3967 */       ConstantPool.JavaLangClassConstantPoolName, 
/* 3968 */       ConstantPool.GETDECLAREDFIELD_NAME, 
/* 3969 */       ConstantPool.GETDECLAREDFIELD_SIGNATURE);
/*      */   }
/*      */ 
/*      */   protected void invokeClassGetDeclaredMethod()
/*      */   {
/* 3974 */     invoke(
/* 3975 */       -74, 
/* 3976 */       3, 
/* 3977 */       1, 
/* 3978 */       ConstantPool.JavaLangClassConstantPoolName, 
/* 3979 */       ConstantPool.GETDECLAREDMETHOD_NAME, 
/* 3980 */       ConstantPool.GETDECLAREDMETHOD_SIGNATURE);
/*      */   }
/*      */ 
/*      */   public void invokeEnumOrdinal(char[] enumTypeConstantPoolName)
/*      */   {
/* 3985 */     invoke(
/* 3986 */       -74, 
/* 3987 */       1, 
/* 3988 */       1, 
/* 3989 */       enumTypeConstantPoolName, 
/* 3990 */       ConstantPool.Ordinal, 
/* 3991 */       ConstantPool.OrdinalSignature);
/*      */   }
/*      */ 
/*      */   public void invokeIterableIterator(TypeBinding iterableReceiverType)
/*      */   {
/* 3996 */     if ((iterableReceiverType.tagBits & 0x800) != 0L) {
/* 3997 */       Util.recordNestedType(this.classFile, iterableReceiverType);
/*      */     }
/* 3999 */     invoke(
/* 4000 */       iterableReceiverType.isInterface() ? -71 : -74, 
/* 4001 */       1, 
/* 4002 */       1, 
/* 4003 */       iterableReceiverType.constantPoolName(), 
/* 4004 */       ConstantPool.ITERATOR_NAME, 
/* 4005 */       ConstantPool.ITERATOR_SIGNATURE);
/*      */   }
/*      */ 
/*      */   public void invokeJavaLangAssertionErrorConstructor(int typeBindingID)
/*      */   {
/*      */     int receiverAndArgsSize;
/*      */     int receiverAndArgsSize;
/*      */     int receiverAndArgsSize;
/*      */     int receiverAndArgsSize;
/*      */     int receiverAndArgsSize;
/*      */     int receiverAndArgsSize;
/*      */     int receiverAndArgsSize;
/* 4012 */     switch (typeBindingID) {
/*      */     case 3:
/*      */     case 4:
/*      */     case 10:
/* 4016 */       char[] signature = ConstantPool.IntConstrSignature;
/* 4017 */       receiverAndArgsSize = 2;
/* 4018 */       break;
/*      */     case 7:
/* 4020 */       char[] signature = ConstantPool.LongConstrSignature;
/* 4021 */       receiverAndArgsSize = 3;
/* 4022 */       break;
/*      */     case 9:
/* 4024 */       char[] signature = ConstantPool.FloatConstrSignature;
/* 4025 */       receiverAndArgsSize = 2;
/* 4026 */       break;
/*      */     case 8:
/* 4028 */       char[] signature = ConstantPool.DoubleConstrSignature;
/* 4029 */       receiverAndArgsSize = 3;
/* 4030 */       break;
/*      */     case 2:
/* 4032 */       char[] signature = ConstantPool.CharConstrSignature;
/* 4033 */       receiverAndArgsSize = 2;
/* 4034 */       break;
/*      */     case 5:
/* 4036 */       char[] signature = ConstantPool.BooleanConstrSignature;
/* 4037 */       receiverAndArgsSize = 2;
/* 4038 */       break;
/*      */     case 1:
/*      */     case 11:
/*      */     case 12:
/* 4042 */       char[] signature = ConstantPool.ObjectConstrSignature;
/* 4043 */       receiverAndArgsSize = 2;
/* 4044 */       break;
/*      */     case 6:
/*      */     default:
/* 4046 */       return;
/*      */     }
/*      */     char[] signature;
/*      */     int receiverAndArgsSize;
/* 4048 */     invoke(
/* 4049 */       -73, 
/* 4050 */       receiverAndArgsSize, 
/* 4051 */       0, 
/* 4052 */       ConstantPool.JavaLangAssertionErrorConstantPoolName, 
/* 4053 */       ConstantPool.Init, 
/* 4054 */       signature);
/*      */   }
/*      */ 
/*      */   public void invokeJavaLangAssertionErrorDefaultConstructor()
/*      */   {
/* 4059 */     invoke(
/* 4060 */       -73, 
/* 4061 */       1, 
/* 4062 */       0, 
/* 4063 */       ConstantPool.JavaLangAssertionErrorConstantPoolName, 
/* 4064 */       ConstantPool.Init, 
/* 4065 */       ConstantPool.DefaultConstructorSignature);
/*      */   }
/*      */ 
/*      */   public void invokeJavaLangClassDesiredAssertionStatus()
/*      */   {
/* 4070 */     invoke(
/* 4071 */       -74, 
/* 4072 */       1, 
/* 4073 */       1, 
/* 4074 */       ConstantPool.JavaLangClassConstantPoolName, 
/* 4075 */       ConstantPool.DesiredAssertionStatus, 
/* 4076 */       ConstantPool.DesiredAssertionStatusSignature);
/*      */   }
/*      */ 
/*      */   public void invokeJavaLangEnumvalueOf(ReferenceBinding binding)
/*      */   {
/* 4081 */     invoke(
/* 4082 */       -72, 
/* 4083 */       2, 
/* 4084 */       1, 
/* 4085 */       ConstantPool.JavaLangEnumConstantPoolName, 
/* 4086 */       ConstantPool.ValueOf, 
/* 4087 */       ConstantPool.ValueOfStringClassSignature);
/*      */   }
/*      */ 
/*      */   public void invokeJavaLangEnumValues(TypeBinding enumBinding, ArrayBinding arrayBinding) {
/* 4091 */     char[] signature = "()".toCharArray();
/* 4092 */     signature = CharOperation.concat(signature, arrayBinding.constantPoolName());
/* 4093 */     invoke(
/* 4094 */       -72, 
/* 4095 */       0, 
/* 4096 */       1, 
/* 4097 */       enumBinding.constantPoolName(), 
/* 4098 */       TypeConstants.VALUES, 
/* 4099 */       signature);
/*      */   }
/*      */ 
/*      */   public void invokeJavaLangErrorConstructor()
/*      */   {
/* 4104 */     invoke(
/* 4105 */       -73, 
/* 4106 */       2, 
/* 4107 */       0, 
/* 4108 */       ConstantPool.JavaLangErrorConstantPoolName, 
/* 4109 */       ConstantPool.Init, 
/* 4110 */       ConstantPool.StringConstructorSignature);
/*      */   }
/*      */ 
/*      */   public void invokeJavaLangReflectConstructorNewInstance()
/*      */   {
/* 4115 */     invoke(
/* 4116 */       -74, 
/* 4117 */       2, 
/* 4118 */       1, 
/* 4119 */       ConstantPool.JavaLangReflectConstructorConstantPoolName, 
/* 4120 */       ConstantPool.NewInstance, 
/* 4121 */       ConstantPool.JavaLangReflectConstructorNewInstanceSignature);
/*      */   }
/*      */ 
/*      */   protected void invokeJavaLangReflectFieldGetter(int typeID)
/*      */   {
/*      */     int returnTypeSize;
/*      */     int returnTypeSize;
/*      */     int returnTypeSize;
/*      */     int returnTypeSize;
/*      */     int returnTypeSize;
/*      */     int returnTypeSize;
/*      */     int returnTypeSize;
/*      */     int returnTypeSize;
/*      */     char[] selector;
/*      */     char[] signature;
/*      */     int returnTypeSize;
/* 4128 */     switch (typeID) {
/*      */     case 10:
/* 4130 */       char[] selector = ConstantPool.GET_INT_METHOD_NAME;
/* 4131 */       char[] signature = ConstantPool.GET_INT_METHOD_SIGNATURE;
/* 4132 */       returnTypeSize = 1;
/* 4133 */       break;
/*      */     case 3:
/* 4135 */       char[] selector = ConstantPool.GET_BYTE_METHOD_NAME;
/* 4136 */       char[] signature = ConstantPool.GET_BYTE_METHOD_SIGNATURE;
/* 4137 */       returnTypeSize = 1;
/* 4138 */       break;
/*      */     case 4:
/* 4140 */       char[] selector = ConstantPool.GET_SHORT_METHOD_NAME;
/* 4141 */       char[] signature = ConstantPool.GET_SHORT_METHOD_SIGNATURE;
/* 4142 */       returnTypeSize = 1;
/* 4143 */       break;
/*      */     case 7:
/* 4145 */       char[] selector = ConstantPool.GET_LONG_METHOD_NAME;
/* 4146 */       char[] signature = ConstantPool.GET_LONG_METHOD_SIGNATURE;
/* 4147 */       returnTypeSize = 2;
/* 4148 */       break;
/*      */     case 9:
/* 4150 */       char[] selector = ConstantPool.GET_FLOAT_METHOD_NAME;
/* 4151 */       char[] signature = ConstantPool.GET_FLOAT_METHOD_SIGNATURE;
/* 4152 */       returnTypeSize = 1;
/* 4153 */       break;
/*      */     case 8:
/* 4155 */       char[] selector = ConstantPool.GET_DOUBLE_METHOD_NAME;
/* 4156 */       char[] signature = ConstantPool.GET_DOUBLE_METHOD_SIGNATURE;
/* 4157 */       returnTypeSize = 2;
/* 4158 */       break;
/*      */     case 2:
/* 4160 */       char[] selector = ConstantPool.GET_CHAR_METHOD_NAME;
/* 4161 */       char[] signature = ConstantPool.GET_CHAR_METHOD_SIGNATURE;
/* 4162 */       returnTypeSize = 1;
/* 4163 */       break;
/*      */     case 5:
/* 4165 */       char[] selector = ConstantPool.GET_BOOLEAN_METHOD_NAME;
/* 4166 */       char[] signature = ConstantPool.GET_BOOLEAN_METHOD_SIGNATURE;
/* 4167 */       returnTypeSize = 1;
/* 4168 */       break;
/*      */     case 6:
/*      */     default:
/* 4170 */       selector = ConstantPool.GET_OBJECT_METHOD_NAME;
/* 4171 */       signature = ConstantPool.GET_OBJECT_METHOD_SIGNATURE;
/* 4172 */       returnTypeSize = 1;
/*      */     }
/*      */ 
/* 4175 */     invoke(
/* 4176 */       -74, 
/* 4177 */       2, 
/* 4178 */       returnTypeSize, 
/* 4179 */       ConstantPool.JAVALANGREFLECTFIELD_CONSTANTPOOLNAME, 
/* 4180 */       selector, 
/* 4181 */       signature);
/*      */   }
/*      */ 
/*      */   protected void invokeJavaLangReflectFieldSetter(int typeID)
/*      */   {
/*      */     int receiverAndArgsSize;
/*      */     int receiverAndArgsSize;
/*      */     int receiverAndArgsSize;
/*      */     int receiverAndArgsSize;
/*      */     int receiverAndArgsSize;
/*      */     int receiverAndArgsSize;
/*      */     int receiverAndArgsSize;
/*      */     int receiverAndArgsSize;
/*      */     char[] selector;
/*      */     char[] signature;
/*      */     int receiverAndArgsSize;
/* 4188 */     switch (typeID) {
/*      */     case 10:
/* 4190 */       char[] selector = ConstantPool.SET_INT_METHOD_NAME;
/* 4191 */       char[] signature = ConstantPool.SET_INT_METHOD_SIGNATURE;
/* 4192 */       receiverAndArgsSize = 3;
/* 4193 */       break;
/*      */     case 3:
/* 4195 */       char[] selector = ConstantPool.SET_BYTE_METHOD_NAME;
/* 4196 */       char[] signature = ConstantPool.SET_BYTE_METHOD_SIGNATURE;
/* 4197 */       receiverAndArgsSize = 3;
/* 4198 */       break;
/*      */     case 4:
/* 4200 */       char[] selector = ConstantPool.SET_SHORT_METHOD_NAME;
/* 4201 */       char[] signature = ConstantPool.SET_SHORT_METHOD_SIGNATURE;
/* 4202 */       receiverAndArgsSize = 3;
/* 4203 */       break;
/*      */     case 7:
/* 4205 */       char[] selector = ConstantPool.SET_LONG_METHOD_NAME;
/* 4206 */       char[] signature = ConstantPool.SET_LONG_METHOD_SIGNATURE;
/* 4207 */       receiverAndArgsSize = 4;
/* 4208 */       break;
/*      */     case 9:
/* 4210 */       char[] selector = ConstantPool.SET_FLOAT_METHOD_NAME;
/* 4211 */       char[] signature = ConstantPool.SET_FLOAT_METHOD_SIGNATURE;
/* 4212 */       receiverAndArgsSize = 3;
/* 4213 */       break;
/*      */     case 8:
/* 4215 */       char[] selector = ConstantPool.SET_DOUBLE_METHOD_NAME;
/* 4216 */       char[] signature = ConstantPool.SET_DOUBLE_METHOD_SIGNATURE;
/* 4217 */       receiverAndArgsSize = 4;
/* 4218 */       break;
/*      */     case 2:
/* 4220 */       char[] selector = ConstantPool.SET_CHAR_METHOD_NAME;
/* 4221 */       char[] signature = ConstantPool.SET_CHAR_METHOD_SIGNATURE;
/* 4222 */       receiverAndArgsSize = 3;
/* 4223 */       break;
/*      */     case 5:
/* 4225 */       char[] selector = ConstantPool.SET_BOOLEAN_METHOD_NAME;
/* 4226 */       char[] signature = ConstantPool.SET_BOOLEAN_METHOD_SIGNATURE;
/* 4227 */       receiverAndArgsSize = 3;
/* 4228 */       break;
/*      */     case 6:
/*      */     default:
/* 4230 */       selector = ConstantPool.SET_OBJECT_METHOD_NAME;
/* 4231 */       signature = ConstantPool.SET_OBJECT_METHOD_SIGNATURE;
/* 4232 */       receiverAndArgsSize = 3;
/*      */     }
/*      */ 
/* 4235 */     invoke(
/* 4236 */       -74, 
/* 4237 */       receiverAndArgsSize, 
/* 4238 */       0, 
/* 4239 */       ConstantPool.JAVALANGREFLECTFIELD_CONSTANTPOOLNAME, 
/* 4240 */       selector, 
/* 4241 */       signature);
/*      */   }
/*      */ 
/*      */   public void invokeJavaLangReflectMethodInvoke()
/*      */   {
/* 4246 */     invoke(
/* 4247 */       -74, 
/* 4248 */       3, 
/* 4249 */       1, 
/* 4250 */       ConstantPool.JAVALANGREFLECTMETHOD_CONSTANTPOOLNAME, 
/* 4251 */       ConstantPool.INVOKE_METHOD_METHOD_NAME, 
/* 4252 */       ConstantPool.INVOKE_METHOD_METHOD_SIGNATURE);
/*      */   }
/*      */ 
/*      */   public void invokeJavaUtilIteratorHasNext()
/*      */   {
/* 4257 */     invoke(
/* 4258 */       -71, 
/* 4259 */       1, 
/* 4260 */       1, 
/* 4261 */       ConstantPool.JavaUtilIteratorConstantPoolName, 
/* 4262 */       ConstantPool.HasNext, 
/* 4263 */       ConstantPool.HasNextSignature);
/*      */   }
/*      */ 
/*      */   public void invokeJavaUtilIteratorNext()
/*      */   {
/* 4268 */     invoke(
/* 4269 */       -71, 
/* 4270 */       1, 
/* 4271 */       1, 
/* 4272 */       ConstantPool.JavaUtilIteratorConstantPoolName, 
/* 4273 */       ConstantPool.Next, 
/* 4274 */       ConstantPool.NextSignature);
/*      */   }
/*      */ 
/*      */   public void invokeNoClassDefFoundErrorStringConstructor()
/*      */   {
/* 4279 */     invoke(
/* 4280 */       -73, 
/* 4281 */       2, 
/* 4282 */       0, 
/* 4283 */       ConstantPool.JavaLangNoClassDefFoundErrorConstantPoolName, 
/* 4284 */       ConstantPool.Init, 
/* 4285 */       ConstantPool.StringConstructorSignature);
/*      */   }
/*      */ 
/*      */   public void invokeObjectGetClass()
/*      */   {
/* 4290 */     invoke(
/* 4291 */       -74, 
/* 4292 */       1, 
/* 4293 */       1, 
/* 4294 */       ConstantPool.JavaLangObjectConstantPoolName, 
/* 4295 */       ConstantPool.GetClass, 
/* 4296 */       ConstantPool.GetClassSignature);
/*      */   }
/*      */ 
/*      */   public void invokeStringConcatenationAppendForType(int typeID)
/*      */   {
/* 4305 */     char[] declaringClass = (char[])null;
/* 4306 */     char[] selector = ConstantPool.Append;
/* 4307 */     char[] signature = (char[])null;
/*      */     int receiverAndArgsSize;
/*      */     int receiverAndArgsSize;
/*      */     int receiverAndArgsSize;
/*      */     int receiverAndArgsSize;
/*      */     int receiverAndArgsSize;
/*      */     int receiverAndArgsSize;
/*      */     int receiverAndArgsSize;
/*      */     int receiverAndArgsSize;
/* 4308 */     switch (typeID) {
/*      */     case 3:
/*      */     case 4:
/*      */     case 10:
/* 4312 */       if (this.targetLevel >= 3211264L) {
/* 4313 */         declaringClass = ConstantPool.JavaLangStringBuilderConstantPoolName;
/* 4314 */         signature = ConstantPool.StringBuilderAppendIntSignature;
/*      */       } else {
/* 4316 */         declaringClass = ConstantPool.JavaLangStringBufferConstantPoolName;
/* 4317 */         signature = ConstantPool.StringBufferAppendIntSignature;
/*      */       }
/* 4319 */       receiverAndArgsSize = 2;
/* 4320 */       break;
/*      */     case 7:
/* 4322 */       if (this.targetLevel >= 3211264L) {
/* 4323 */         declaringClass = ConstantPool.JavaLangStringBuilderConstantPoolName;
/* 4324 */         signature = ConstantPool.StringBuilderAppendLongSignature;
/*      */       } else {
/* 4326 */         declaringClass = ConstantPool.JavaLangStringBufferConstantPoolName;
/* 4327 */         signature = ConstantPool.StringBufferAppendLongSignature;
/*      */       }
/* 4329 */       receiverAndArgsSize = 3;
/* 4330 */       break;
/*      */     case 9:
/* 4332 */       if (this.targetLevel >= 3211264L) {
/* 4333 */         declaringClass = ConstantPool.JavaLangStringBuilderConstantPoolName;
/* 4334 */         signature = ConstantPool.StringBuilderAppendFloatSignature;
/*      */       } else {
/* 4336 */         declaringClass = ConstantPool.JavaLangStringBufferConstantPoolName;
/* 4337 */         signature = ConstantPool.StringBufferAppendFloatSignature;
/*      */       }
/* 4339 */       receiverAndArgsSize = 2;
/* 4340 */       break;
/*      */     case 8:
/* 4342 */       if (this.targetLevel >= 3211264L) {
/* 4343 */         declaringClass = ConstantPool.JavaLangStringBuilderConstantPoolName;
/* 4344 */         signature = ConstantPool.StringBuilderAppendDoubleSignature;
/*      */       } else {
/* 4346 */         declaringClass = ConstantPool.JavaLangStringBufferConstantPoolName;
/* 4347 */         signature = ConstantPool.StringBufferAppendDoubleSignature;
/*      */       }
/* 4349 */       receiverAndArgsSize = 3;
/* 4350 */       break;
/*      */     case 2:
/* 4352 */       if (this.targetLevel >= 3211264L) {
/* 4353 */         declaringClass = ConstantPool.JavaLangStringBuilderConstantPoolName;
/* 4354 */         signature = ConstantPool.StringBuilderAppendCharSignature;
/*      */       } else {
/* 4356 */         declaringClass = ConstantPool.JavaLangStringBufferConstantPoolName;
/* 4357 */         signature = ConstantPool.StringBufferAppendCharSignature;
/*      */       }
/* 4359 */       receiverAndArgsSize = 2;
/* 4360 */       break;
/*      */     case 5:
/* 4362 */       if (this.targetLevel >= 3211264L) {
/* 4363 */         declaringClass = ConstantPool.JavaLangStringBuilderConstantPoolName;
/* 4364 */         signature = ConstantPool.StringBuilderAppendBooleanSignature;
/*      */       } else {
/* 4366 */         declaringClass = ConstantPool.JavaLangStringBufferConstantPoolName;
/* 4367 */         signature = ConstantPool.StringBufferAppendBooleanSignature;
/*      */       }
/* 4369 */       receiverAndArgsSize = 2;
/* 4370 */       break;
/*      */     case 11:
/* 4372 */       if (this.targetLevel >= 3211264L) {
/* 4373 */         declaringClass = ConstantPool.JavaLangStringBuilderConstantPoolName;
/* 4374 */         signature = ConstantPool.StringBuilderAppendStringSignature;
/*      */       } else {
/* 4376 */         declaringClass = ConstantPool.JavaLangStringBufferConstantPoolName;
/* 4377 */         signature = ConstantPool.StringBufferAppendStringSignature;
/*      */       }
/* 4379 */       receiverAndArgsSize = 2;
/* 4380 */       break;
/*      */     case 6:
/*      */     default:
/* 4382 */       if (this.targetLevel >= 3211264L) {
/* 4383 */         declaringClass = ConstantPool.JavaLangStringBuilderConstantPoolName;
/* 4384 */         signature = ConstantPool.StringBuilderAppendObjectSignature;
/*      */       } else {
/* 4386 */         declaringClass = ConstantPool.JavaLangStringBufferConstantPoolName;
/* 4387 */         signature = ConstantPool.StringBufferAppendObjectSignature;
/*      */       }
/* 4389 */       receiverAndArgsSize = 2;
/*      */     }
/*      */ 
/* 4392 */     invoke(
/* 4393 */       -74, 
/* 4394 */       receiverAndArgsSize, 
/* 4395 */       1, 
/* 4396 */       declaringClass, 
/* 4397 */       selector, 
/* 4398 */       signature);
/*      */   }
/*      */ 
/*      */   public void invokeStringConcatenationDefaultConstructor()
/*      */   {
/*      */     char[] declaringClass;
/*      */     char[] declaringClass;
/* 4405 */     if (this.targetLevel < 3211264L)
/* 4406 */       declaringClass = ConstantPool.JavaLangStringBufferConstantPoolName;
/*      */     else {
/* 4408 */       declaringClass = ConstantPool.JavaLangStringBuilderConstantPoolName;
/*      */     }
/* 4410 */     invoke(
/* 4411 */       -73, 
/* 4412 */       1, 
/* 4413 */       0, 
/* 4414 */       declaringClass, 
/* 4415 */       ConstantPool.Init, 
/* 4416 */       ConstantPool.DefaultConstructorSignature);
/*      */   }
/*      */ 
/*      */   public void invokeStringConcatenationStringConstructor()
/*      */   {
/*      */     char[] declaringClass;
/*      */     char[] declaringClass;
/* 4423 */     if (this.targetLevel < 3211264L)
/*      */     {
/* 4425 */       declaringClass = ConstantPool.JavaLangStringBufferConstantPoolName;
/*      */     }
/*      */     else {
/* 4428 */       declaringClass = ConstantPool.JavaLangStringBuilderConstantPoolName;
/*      */     }
/* 4430 */     invoke(
/* 4431 */       -73, 
/* 4432 */       2, 
/* 4433 */       0, 
/* 4434 */       declaringClass, 
/* 4435 */       ConstantPool.Init, 
/* 4436 */       ConstantPool.StringConstructorSignature);
/*      */   }
/*      */ 
/*      */   public void invokeStringConcatenationToString()
/*      */   {
/*      */     char[] declaringClass;
/*      */     char[] declaringClass;
/* 4443 */     if (this.targetLevel < 3211264L)
/*      */     {
/* 4445 */       declaringClass = ConstantPool.JavaLangStringBufferConstantPoolName;
/*      */     }
/*      */     else {
/* 4448 */       declaringClass = ConstantPool.JavaLangStringBuilderConstantPoolName;
/*      */     }
/* 4450 */     invoke(
/* 4451 */       -74, 
/* 4452 */       1, 
/* 4453 */       1, 
/* 4454 */       declaringClass, 
/* 4455 */       ConstantPool.ToString, 
/* 4456 */       ConstantPool.ToStringSignature);
/*      */   }
/*      */ 
/*      */   public void invokeStringIntern()
/*      */   {
/* 4461 */     invoke(
/* 4462 */       -74, 
/* 4463 */       1, 
/* 4464 */       1, 
/* 4465 */       ConstantPool.JavaLangStringConstantPoolName, 
/* 4466 */       ConstantPool.Intern, 
/* 4467 */       ConstantPool.InternSignature);
/*      */   }
/*      */ 
/*      */   public void invokeStringValueOf(int typeID)
/*      */   {
/*      */     int receiverAndArgsSize;
/*      */     int receiverAndArgsSize;
/*      */     int receiverAndArgsSize;
/*      */     int receiverAndArgsSize;
/*      */     int receiverAndArgsSize;
/*      */     int receiverAndArgsSize;
/*      */     int receiverAndArgsSize;
/* 4474 */     switch (typeID) {
/*      */     case 3:
/*      */     case 4:
/*      */     case 10:
/* 4478 */       char[] signature = ConstantPool.ValueOfIntSignature;
/* 4479 */       receiverAndArgsSize = 1;
/* 4480 */       break;
/*      */     case 7:
/* 4482 */       char[] signature = ConstantPool.ValueOfLongSignature;
/* 4483 */       receiverAndArgsSize = 2;
/* 4484 */       break;
/*      */     case 9:
/* 4486 */       char[] signature = ConstantPool.ValueOfFloatSignature;
/* 4487 */       receiverAndArgsSize = 1;
/* 4488 */       break;
/*      */     case 8:
/* 4490 */       char[] signature = ConstantPool.ValueOfDoubleSignature;
/* 4491 */       receiverAndArgsSize = 2;
/* 4492 */       break;
/*      */     case 2:
/* 4494 */       char[] signature = ConstantPool.ValueOfCharSignature;
/* 4495 */       receiverAndArgsSize = 1;
/* 4496 */       break;
/*      */     case 5:
/* 4498 */       char[] signature = ConstantPool.ValueOfBooleanSignature;
/* 4499 */       receiverAndArgsSize = 1;
/* 4500 */       break;
/*      */     case 0:
/*      */     case 1:
/*      */     case 11:
/*      */     case 12:
/* 4505 */       char[] signature = ConstantPool.ValueOfObjectSignature;
/* 4506 */       receiverAndArgsSize = 1;
/* 4507 */       break;
/*      */     case 6:
/*      */     default:
/* 4509 */       return;
/*      */     }
/*      */     int receiverAndArgsSize;
/*      */     char[] signature;
/* 4511 */     invoke(
/* 4512 */       -72, 
/* 4513 */       receiverAndArgsSize, 
/* 4514 */       1, 
/* 4515 */       ConstantPool.JavaLangStringConstantPoolName, 
/* 4516 */       ConstantPool.ValueOf, 
/* 4517 */       signature);
/*      */   }
/*      */ 
/*      */   public void invokeSystemArraycopy()
/*      */   {
/* 4522 */     invoke(
/* 4523 */       -72, 
/* 4524 */       5, 
/* 4525 */       0, 
/* 4526 */       ConstantPool.JavaLangSystemConstantPoolName, 
/* 4527 */       ConstantPool.ArrayCopy, 
/* 4528 */       ConstantPool.ArrayCopySignature);
/*      */   }
/*      */ 
/*      */   public void invokeThrowableGetMessage()
/*      */   {
/* 4533 */     invoke(
/* 4534 */       -74, 
/* 4535 */       1, 
/* 4536 */       1, 
/* 4537 */       ConstantPool.JavaLangThrowableConstantPoolName, 
/* 4538 */       ConstantPool.GetMessage, 
/* 4539 */       ConstantPool.GetMessageSignature);
/*      */   }
/*      */ 
/*      */   public void ior() {
/* 4543 */     this.countLabels = 0;
/* 4544 */     this.stackDepth -= 1;
/* 4545 */     if (this.classFileOffset >= this.bCodeStream.length) {
/* 4546 */       resizeByteArray();
/*      */     }
/* 4548 */     this.position += 1;
/* 4549 */     this.bCodeStream[(this.classFileOffset++)] = -128;
/*      */   }
/*      */ 
/*      */   public void irem() {
/* 4553 */     this.countLabels = 0;
/* 4554 */     this.stackDepth -= 1;
/* 4555 */     if (this.classFileOffset >= this.bCodeStream.length) {
/* 4556 */       resizeByteArray();
/*      */     }
/* 4558 */     this.position += 1;
/* 4559 */     this.bCodeStream[(this.classFileOffset++)] = 112;
/*      */   }
/*      */ 
/*      */   public void ireturn() {
/* 4563 */     this.countLabels = 0;
/* 4564 */     this.stackDepth -= 1;
/*      */ 
/* 4566 */     if (this.classFileOffset >= this.bCodeStream.length) {
/* 4567 */       resizeByteArray();
/*      */     }
/* 4569 */     this.position += 1;
/* 4570 */     this.bCodeStream[(this.classFileOffset++)] = -84;
/* 4571 */     this.lastAbruptCompletion = this.position;
/*      */   }
/*      */ 
/*      */   public boolean isDefinitelyAssigned(Scope scope, int initStateIndex, LocalVariableBinding local)
/*      */   {
/* 4576 */     if ((local.tagBits & 0x400) != 0L) {
/* 4577 */       return true;
/*      */     }
/* 4579 */     if (initStateIndex == -1)
/* 4580 */       return false;
/* 4581 */     int localPosition = local.id + this.maxFieldCount;
/* 4582 */     MethodScope methodScope = scope.methodScope();
/*      */ 
/* 4584 */     if (localPosition < 64) {
/* 4585 */       return (methodScope.definiteInits[initStateIndex] & 1L << localPosition) != 0L;
/*      */     }
/*      */ 
/* 4588 */     long[] extraInits = methodScope.extraDefiniteInits[initStateIndex];
/* 4589 */     if (extraInits == null)
/* 4590 */       return false;
/*      */     int vectorIndex;
/* 4592 */     if ((vectorIndex = localPosition / 64 - 1) >= extraInits.length)
/* 4593 */       return false;
/* 4594 */     return (extraInits[vectorIndex] & 1L << localPosition % 64) != 0L;
/*      */   }
/*      */ 
/*      */   public void ishl() {
/* 4598 */     this.countLabels = 0;
/* 4599 */     this.stackDepth -= 1;
/* 4600 */     if (this.classFileOffset >= this.bCodeStream.length) {
/* 4601 */       resizeByteArray();
/*      */     }
/* 4603 */     this.position += 1;
/* 4604 */     this.bCodeStream[(this.classFileOffset++)] = 120;
/*      */   }
/*      */ 
/*      */   public void ishr() {
/* 4608 */     this.countLabels = 0;
/* 4609 */     this.stackDepth -= 1;
/* 4610 */     if (this.classFileOffset >= this.bCodeStream.length) {
/* 4611 */       resizeByteArray();
/*      */     }
/* 4613 */     this.position += 1;
/* 4614 */     this.bCodeStream[(this.classFileOffset++)] = 122;
/*      */   }
/*      */ 
/*      */   public void istore(int iArg) {
/* 4618 */     this.countLabels = 0;
/* 4619 */     this.stackDepth -= 1;
/* 4620 */     if (this.maxLocals <= iArg) {
/* 4621 */       this.maxLocals = (iArg + 1);
/*      */     }
/* 4623 */     if (iArg > 255) {
/* 4624 */       if (this.classFileOffset + 3 >= this.bCodeStream.length) {
/* 4625 */         resizeByteArray();
/*      */       }
/* 4627 */       this.position += 2;
/* 4628 */       this.bCodeStream[(this.classFileOffset++)] = -60;
/* 4629 */       this.bCodeStream[(this.classFileOffset++)] = 54;
/* 4630 */       writeUnsignedShort(iArg);
/*      */     } else {
/* 4632 */       if (this.classFileOffset + 1 >= this.bCodeStream.length) {
/* 4633 */         resizeByteArray();
/*      */       }
/* 4635 */       this.position += 2;
/* 4636 */       this.bCodeStream[(this.classFileOffset++)] = 54;
/* 4637 */       this.bCodeStream[(this.classFileOffset++)] = (byte)iArg;
/*      */     }
/*      */   }
/*      */ 
/*      */   public void istore_0() {
/* 4642 */     this.countLabels = 0;
/* 4643 */     this.stackDepth -= 1;
/* 4644 */     if (this.maxLocals == 0) {
/* 4645 */       this.maxLocals = 1;
/*      */     }
/* 4647 */     if (this.classFileOffset >= this.bCodeStream.length) {
/* 4648 */       resizeByteArray();
/*      */     }
/* 4650 */     this.position += 1;
/* 4651 */     this.bCodeStream[(this.classFileOffset++)] = 59;
/*      */   }
/*      */ 
/*      */   public void istore_1() {
/* 4655 */     this.countLabels = 0;
/* 4656 */     this.stackDepth -= 1;
/* 4657 */     if (this.maxLocals <= 1) {
/* 4658 */       this.maxLocals = 2;
/*      */     }
/* 4660 */     if (this.classFileOffset >= this.bCodeStream.length) {
/* 4661 */       resizeByteArray();
/*      */     }
/* 4663 */     this.position += 1;
/* 4664 */     this.bCodeStream[(this.classFileOffset++)] = 60;
/*      */   }
/*      */ 
/*      */   public void istore_2() {
/* 4668 */     this.countLabels = 0;
/* 4669 */     this.stackDepth -= 1;
/* 4670 */     if (this.maxLocals <= 2) {
/* 4671 */       this.maxLocals = 3;
/*      */     }
/* 4673 */     if (this.classFileOffset >= this.bCodeStream.length) {
/* 4674 */       resizeByteArray();
/*      */     }
/* 4676 */     this.position += 1;
/* 4677 */     this.bCodeStream[(this.classFileOffset++)] = 61;
/*      */   }
/*      */ 
/*      */   public void istore_3() {
/* 4681 */     this.countLabels = 0;
/* 4682 */     this.stackDepth -= 1;
/* 4683 */     if (this.maxLocals <= 3) {
/* 4684 */       this.maxLocals = 4;
/*      */     }
/* 4686 */     if (this.classFileOffset >= this.bCodeStream.length) {
/* 4687 */       resizeByteArray();
/*      */     }
/* 4689 */     this.position += 1;
/* 4690 */     this.bCodeStream[(this.classFileOffset++)] = 62;
/*      */   }
/*      */ 
/*      */   public void isub() {
/* 4694 */     this.countLabels = 0;
/* 4695 */     this.stackDepth -= 1;
/* 4696 */     if (this.classFileOffset >= this.bCodeStream.length) {
/* 4697 */       resizeByteArray();
/*      */     }
/* 4699 */     this.position += 1;
/* 4700 */     this.bCodeStream[(this.classFileOffset++)] = 100;
/*      */   }
/*      */ 
/*      */   public void iushr() {
/* 4704 */     this.countLabels = 0;
/* 4705 */     this.stackDepth -= 1;
/* 4706 */     if (this.classFileOffset >= this.bCodeStream.length) {
/* 4707 */       resizeByteArray();
/*      */     }
/* 4709 */     this.position += 1;
/* 4710 */     this.bCodeStream[(this.classFileOffset++)] = 124;
/*      */   }
/*      */ 
/*      */   public void ixor() {
/* 4714 */     this.countLabels = 0;
/* 4715 */     this.stackDepth -= 1;
/* 4716 */     if (this.classFileOffset >= this.bCodeStream.length) {
/* 4717 */       resizeByteArray();
/*      */     }
/* 4719 */     this.position += 1;
/* 4720 */     this.bCodeStream[(this.classFileOffset++)] = -126;
/*      */   }
/*      */ 
/*      */   public final void jsr(BranchLabel lbl) {
/* 4724 */     if (this.wideMode) {
/* 4725 */       jsr_w(lbl);
/* 4726 */       return;
/*      */     }
/* 4728 */     this.countLabels = 0;
/* 4729 */     if (this.classFileOffset >= this.bCodeStream.length) {
/* 4730 */       resizeByteArray();
/*      */     }
/* 4732 */     this.position += 1;
/* 4733 */     this.bCodeStream[(this.classFileOffset++)] = -88;
/* 4734 */     lbl.branch();
/*      */   }
/*      */ 
/*      */   public final void jsr_w(BranchLabel lbl) {
/* 4738 */     this.countLabels = 0;
/* 4739 */     if (this.classFileOffset >= this.bCodeStream.length) {
/* 4740 */       resizeByteArray();
/*      */     }
/* 4742 */     this.position += 1;
/* 4743 */     this.bCodeStream[(this.classFileOffset++)] = -55;
/* 4744 */     lbl.branchWide();
/*      */   }
/*      */ 
/*      */   public void l2d() {
/* 4748 */     this.countLabels = 0;
/* 4749 */     if (this.classFileOffset >= this.bCodeStream.length) {
/* 4750 */       resizeByteArray();
/*      */     }
/* 4752 */     this.position += 1;
/* 4753 */     this.bCodeStream[(this.classFileOffset++)] = -118;
/*      */   }
/*      */ 
/*      */   public void l2f() {
/* 4757 */     this.countLabels = 0;
/* 4758 */     this.stackDepth -= 1;
/* 4759 */     if (this.classFileOffset >= this.bCodeStream.length) {
/* 4760 */       resizeByteArray();
/*      */     }
/* 4762 */     this.position += 1;
/* 4763 */     this.bCodeStream[(this.classFileOffset++)] = -119;
/*      */   }
/*      */ 
/*      */   public void l2i() {
/* 4767 */     this.countLabels = 0;
/* 4768 */     this.stackDepth -= 1;
/* 4769 */     if (this.classFileOffset >= this.bCodeStream.length) {
/* 4770 */       resizeByteArray();
/*      */     }
/* 4772 */     this.position += 1;
/* 4773 */     this.bCodeStream[(this.classFileOffset++)] = -120;
/*      */   }
/*      */ 
/*      */   public void ladd() {
/* 4777 */     this.countLabels = 0;
/* 4778 */     this.stackDepth -= 2;
/* 4779 */     if (this.classFileOffset >= this.bCodeStream.length) {
/* 4780 */       resizeByteArray();
/*      */     }
/* 4782 */     this.position += 1;
/* 4783 */     this.bCodeStream[(this.classFileOffset++)] = 97;
/*      */   }
/*      */ 
/*      */   public void laload() {
/* 4787 */     this.countLabels = 0;
/* 4788 */     if (this.classFileOffset >= this.bCodeStream.length) {
/* 4789 */       resizeByteArray();
/*      */     }
/* 4791 */     this.position += 1;
/* 4792 */     this.bCodeStream[(this.classFileOffset++)] = 47;
/*      */   }
/*      */ 
/*      */   public void land() {
/* 4796 */     this.countLabels = 0;
/* 4797 */     this.stackDepth -= 2;
/* 4798 */     if (this.classFileOffset >= this.bCodeStream.length) {
/* 4799 */       resizeByteArray();
/*      */     }
/* 4801 */     this.position += 1;
/* 4802 */     this.bCodeStream[(this.classFileOffset++)] = 127;
/*      */   }
/*      */ 
/*      */   public void lastore() {
/* 4806 */     this.countLabels = 0;
/* 4807 */     this.stackDepth -= 4;
/* 4808 */     if (this.classFileOffset >= this.bCodeStream.length) {
/* 4809 */       resizeByteArray();
/*      */     }
/* 4811 */     this.position += 1;
/* 4812 */     this.bCodeStream[(this.classFileOffset++)] = 80;
/*      */   }
/*      */ 
/*      */   public void lcmp() {
/* 4816 */     this.countLabels = 0;
/* 4817 */     this.stackDepth -= 3;
/* 4818 */     if (this.classFileOffset >= this.bCodeStream.length) {
/* 4819 */       resizeByteArray();
/*      */     }
/* 4821 */     this.position += 1;
/* 4822 */     this.bCodeStream[(this.classFileOffset++)] = -108;
/*      */   }
/*      */ 
/*      */   public void lconst_0() {
/* 4826 */     this.countLabels = 0;
/* 4827 */     this.stackDepth += 2;
/* 4828 */     if (this.stackDepth > this.stackMax)
/* 4829 */       this.stackMax = this.stackDepth;
/* 4830 */     if (this.classFileOffset >= this.bCodeStream.length) {
/* 4831 */       resizeByteArray();
/*      */     }
/* 4833 */     this.position += 1;
/* 4834 */     this.bCodeStream[(this.classFileOffset++)] = 9;
/*      */   }
/*      */ 
/*      */   public void lconst_1() {
/* 4838 */     this.countLabels = 0;
/* 4839 */     this.stackDepth += 2;
/* 4840 */     if (this.stackDepth > this.stackMax)
/* 4841 */       this.stackMax = this.stackDepth;
/* 4842 */     if (this.classFileOffset >= this.bCodeStream.length) {
/* 4843 */       resizeByteArray();
/*      */     }
/* 4845 */     this.position += 1;
/* 4846 */     this.bCodeStream[(this.classFileOffset++)] = 10;
/*      */   }
/*      */ 
/*      */   public void ldc(float constant) {
/* 4850 */     this.countLabels = 0;
/* 4851 */     int index = this.constantPool.literalIndex(constant);
/* 4852 */     this.stackDepth += 1;
/* 4853 */     if (this.stackDepth > this.stackMax)
/* 4854 */       this.stackMax = this.stackDepth;
/* 4855 */     if (index > 255)
/*      */     {
/* 4857 */       if (this.classFileOffset + 2 >= this.bCodeStream.length) {
/* 4858 */         resizeByteArray();
/*      */       }
/* 4860 */       this.position += 1;
/* 4861 */       this.bCodeStream[(this.classFileOffset++)] = 19;
/* 4862 */       writeUnsignedShort(index);
/*      */     }
/*      */     else {
/* 4865 */       if (this.classFileOffset + 1 >= this.bCodeStream.length) {
/* 4866 */         resizeByteArray();
/*      */       }
/* 4868 */       this.position += 2;
/* 4869 */       this.bCodeStream[(this.classFileOffset++)] = 18;
/* 4870 */       this.bCodeStream[(this.classFileOffset++)] = (byte)index;
/*      */     }
/*      */   }
/*      */ 
/*      */   public void ldc(int constant) {
/* 4875 */     this.countLabels = 0;
/* 4876 */     int index = this.constantPool.literalIndex(constant);
/* 4877 */     this.stackDepth += 1;
/* 4878 */     if (this.stackDepth > this.stackMax)
/* 4879 */       this.stackMax = this.stackDepth;
/* 4880 */     if (index > 255)
/*      */     {
/* 4882 */       if (this.classFileOffset + 2 >= this.bCodeStream.length) {
/* 4883 */         resizeByteArray();
/*      */       }
/* 4885 */       this.position += 1;
/* 4886 */       this.bCodeStream[(this.classFileOffset++)] = 19;
/* 4887 */       writeUnsignedShort(index);
/*      */     }
/*      */     else {
/* 4890 */       if (this.classFileOffset + 1 >= this.bCodeStream.length) {
/* 4891 */         resizeByteArray();
/*      */       }
/* 4893 */       this.position += 2;
/* 4894 */       this.bCodeStream[(this.classFileOffset++)] = 18;
/* 4895 */       this.bCodeStream[(this.classFileOffset++)] = (byte)index;
/*      */     }
/*      */   }
/*      */ 
/*      */   public void ldc(String constant) {
/* 4900 */     this.countLabels = 0;
/* 4901 */     int currentCodeStreamPosition = this.position;
/* 4902 */     char[] constantChars = constant.toCharArray();
/* 4903 */     int index = this.constantPool.literalIndexForLdc(constantChars);
/* 4904 */     if (index > 0)
/*      */     {
/* 4907 */       ldcForIndex(index, constantChars);
/*      */     }
/*      */     else
/*      */     {
/* 4913 */       this.position = currentCodeStreamPosition;
/* 4914 */       int i = 0;
/* 4915 */       int length = 0;
/* 4916 */       int constantLength = constant.length();
/* 4917 */       byte[] utf8encoding = new byte[Math.min(constantLength + 100, 65535)];
/* 4918 */       int utf8encodingLength = 0;
/* 4919 */       while ((length < 65532) && (i < constantLength)) {
/* 4920 */         char current = constantChars[i];
/*      */ 
/* 4922 */         if (length + 3 > (utf8encodingLength = utf8encoding.length)) {
/* 4923 */           System.arraycopy(utf8encoding, 0, utf8encoding = new byte[Math.min(utf8encodingLength + 100, 65535)], 0, length);
/*      */         }
/* 4925 */         if ((current >= '\001') && (current <= ''))
/*      */         {
/* 4927 */           utf8encoding[(length++)] = (byte)current;
/*      */         }
/* 4929 */         else if (current > '߿')
/*      */         {
/* 4931 */           utf8encoding[(length++)] = (byte)(0xE0 | current >> '\f' & 0xF);
/* 4932 */           utf8encoding[(length++)] = (byte)(0x80 | current >> '\006' & 0x3F);
/* 4933 */           utf8encoding[(length++)] = (byte)(0x80 | current & 0x3F);
/*      */         }
/*      */         else
/*      */         {
/* 4937 */           utf8encoding[(length++)] = (byte)(0xC0 | current >> '\006' & 0x1F);
/* 4938 */           utf8encoding[(length++)] = (byte)(0x80 | current & 0x3F);
/*      */         }
/*      */ 
/* 4941 */         i++;
/*      */       }
/*      */ 
/* 4945 */       newStringContatenation();
/* 4946 */       dup();
/*      */ 
/* 4948 */       char[] subChars = new char[i];
/* 4949 */       System.arraycopy(constantChars, 0, subChars, 0, i);
/* 4950 */       System.arraycopy(utf8encoding, 0, utf8encoding = new byte[length], 0, length);
/* 4951 */       index = this.constantPool.literalIndex(subChars, utf8encoding);
/* 4952 */       ldcForIndex(index, subChars);
/*      */ 
/* 4954 */       invokeStringConcatenationStringConstructor();
/* 4955 */       while (i < constantLength) {
/* 4956 */         length = 0;
/* 4957 */         utf8encoding = new byte[Math.min(constantLength - i + 100, 65535)];
/* 4958 */         int startIndex = i;
/* 4959 */         while ((length < 65532) && (i < constantLength)) {
/* 4960 */           char current = constantChars[i];
/*      */ 
/* 4962 */           if (length + 3 > (utf8encodingLength = utf8encoding.length)) {
/* 4963 */             System.arraycopy(utf8encoding, 0, utf8encoding = new byte[Math.min(utf8encodingLength + 100, 65535)], 0, length);
/*      */           }
/* 4965 */           if ((current >= '\001') && (current <= ''))
/*      */           {
/* 4967 */             utf8encoding[(length++)] = (byte)current;
/*      */           }
/* 4969 */           else if (current > '߿')
/*      */           {
/* 4971 */             utf8encoding[(length++)] = (byte)(0xE0 | current >> '\f' & 0xF);
/* 4972 */             utf8encoding[(length++)] = (byte)(0x80 | current >> '\006' & 0x3F);
/* 4973 */             utf8encoding[(length++)] = (byte)(0x80 | current & 0x3F);
/*      */           }
/*      */           else
/*      */           {
/* 4977 */             utf8encoding[(length++)] = (byte)(0xC0 | current >> '\006' & 0x1F);
/* 4978 */             utf8encoding[(length++)] = (byte)(0x80 | current & 0x3F);
/*      */           }
/*      */ 
/* 4981 */           i++;
/*      */         }
/*      */ 
/* 4984 */         int newCharLength = i - startIndex;
/* 4985 */         subChars = new char[newCharLength];
/* 4986 */         System.arraycopy(constantChars, startIndex, subChars, 0, newCharLength);
/* 4987 */         System.arraycopy(utf8encoding, 0, utf8encoding = new byte[length], 0, length);
/* 4988 */         index = this.constantPool.literalIndex(subChars, utf8encoding);
/* 4989 */         ldcForIndex(index, subChars);
/*      */ 
/* 4991 */         invokeStringConcatenationAppendForType(11);
/*      */       }
/* 4993 */       invokeStringConcatenationToString();
/* 4994 */       invokeStringIntern();
/*      */     }
/*      */   }
/*      */ 
/*      */   public void ldc(TypeBinding typeBinding) {
/* 4999 */     this.countLabels = 0;
/* 5000 */     int index = this.constantPool.literalIndexForType(typeBinding);
/* 5001 */     this.stackDepth += 1;
/* 5002 */     if (this.stackDepth > this.stackMax)
/* 5003 */       this.stackMax = this.stackDepth;
/* 5004 */     if (index > 255)
/*      */     {
/* 5006 */       if (this.classFileOffset + 2 >= this.bCodeStream.length) {
/* 5007 */         resizeByteArray();
/*      */       }
/* 5009 */       this.position += 1;
/* 5010 */       this.bCodeStream[(this.classFileOffset++)] = 19;
/* 5011 */       writeUnsignedShort(index);
/*      */     }
/*      */     else {
/* 5014 */       if (this.classFileOffset + 1 >= this.bCodeStream.length) {
/* 5015 */         resizeByteArray();
/*      */       }
/* 5017 */       this.position += 2;
/* 5018 */       this.bCodeStream[(this.classFileOffset++)] = 18;
/* 5019 */       this.bCodeStream[(this.classFileOffset++)] = (byte)index;
/*      */     }
/*      */   }
/*      */ 
/*      */   public void ldc2_w(double constant) {
/* 5024 */     this.countLabels = 0;
/* 5025 */     int index = this.constantPool.literalIndex(constant);
/* 5026 */     this.stackDepth += 2;
/* 5027 */     if (this.stackDepth > this.stackMax) {
/* 5028 */       this.stackMax = this.stackDepth;
/*      */     }
/* 5030 */     if (this.classFileOffset + 2 >= this.bCodeStream.length) {
/* 5031 */       resizeByteArray();
/*      */     }
/* 5033 */     this.position += 1;
/* 5034 */     this.bCodeStream[(this.classFileOffset++)] = 20;
/* 5035 */     writeUnsignedShort(index);
/*      */   }
/*      */ 
/*      */   public void ldc2_w(long constant) {
/* 5039 */     this.countLabels = 0;
/* 5040 */     int index = this.constantPool.literalIndex(constant);
/* 5041 */     this.stackDepth += 2;
/* 5042 */     if (this.stackDepth > this.stackMax) {
/* 5043 */       this.stackMax = this.stackDepth;
/*      */     }
/* 5045 */     if (this.classFileOffset + 2 >= this.bCodeStream.length) {
/* 5046 */       resizeByteArray();
/*      */     }
/* 5048 */     this.position += 1;
/* 5049 */     this.bCodeStream[(this.classFileOffset++)] = 20;
/* 5050 */     writeUnsignedShort(index);
/*      */   }
/*      */ 
/*      */   public void ldcForIndex(int index, char[] constant) {
/* 5054 */     this.stackDepth += 1;
/* 5055 */     if (this.stackDepth > this.stackMax) {
/* 5056 */       this.stackMax = this.stackDepth;
/*      */     }
/* 5058 */     if (index > 255)
/*      */     {
/* 5060 */       if (this.classFileOffset + 2 >= this.bCodeStream.length) {
/* 5061 */         resizeByteArray();
/*      */       }
/* 5063 */       this.position += 1;
/* 5064 */       this.bCodeStream[(this.classFileOffset++)] = 19;
/* 5065 */       writeUnsignedShort(index);
/*      */     }
/*      */     else {
/* 5068 */       if (this.classFileOffset + 1 >= this.bCodeStream.length) {
/* 5069 */         resizeByteArray();
/*      */       }
/* 5071 */       this.position += 2;
/* 5072 */       this.bCodeStream[(this.classFileOffset++)] = 18;
/* 5073 */       this.bCodeStream[(this.classFileOffset++)] = (byte)index;
/*      */     }
/*      */   }
/*      */ 
/*      */   public void ldiv() {
/* 5078 */     this.countLabels = 0;
/* 5079 */     this.stackDepth -= 2;
/* 5080 */     if (this.classFileOffset >= this.bCodeStream.length) {
/* 5081 */       resizeByteArray();
/*      */     }
/* 5083 */     this.position += 1;
/* 5084 */     this.bCodeStream[(this.classFileOffset++)] = 109;
/*      */   }
/*      */ 
/*      */   public void lload(int iArg) {
/* 5088 */     this.countLabels = 0;
/* 5089 */     this.stackDepth += 2;
/* 5090 */     if (this.maxLocals <= iArg + 1) {
/* 5091 */       this.maxLocals = (iArg + 2);
/*      */     }
/* 5093 */     if (this.stackDepth > this.stackMax)
/* 5094 */       this.stackMax = this.stackDepth;
/* 5095 */     if (iArg > 255) {
/* 5096 */       if (this.classFileOffset + 3 >= this.bCodeStream.length) {
/* 5097 */         resizeByteArray();
/*      */       }
/* 5099 */       this.position += 2;
/* 5100 */       this.bCodeStream[(this.classFileOffset++)] = -60;
/* 5101 */       this.bCodeStream[(this.classFileOffset++)] = 22;
/* 5102 */       writeUnsignedShort(iArg);
/*      */     } else {
/* 5104 */       if (this.classFileOffset + 1 >= this.bCodeStream.length) {
/* 5105 */         resizeByteArray();
/*      */       }
/* 5107 */       this.position += 2;
/* 5108 */       this.bCodeStream[(this.classFileOffset++)] = 22;
/* 5109 */       this.bCodeStream[(this.classFileOffset++)] = (byte)iArg;
/*      */     }
/*      */   }
/*      */ 
/*      */   public void lload_0() {
/* 5114 */     this.countLabels = 0;
/* 5115 */     this.stackDepth += 2;
/* 5116 */     if (this.maxLocals < 2) {
/* 5117 */       this.maxLocals = 2;
/*      */     }
/* 5119 */     if (this.stackDepth > this.stackMax)
/* 5120 */       this.stackMax = this.stackDepth;
/* 5121 */     if (this.classFileOffset >= this.bCodeStream.length) {
/* 5122 */       resizeByteArray();
/*      */     }
/* 5124 */     this.position += 1;
/* 5125 */     this.bCodeStream[(this.classFileOffset++)] = 30;
/*      */   }
/*      */ 
/*      */   public void lload_1() {
/* 5129 */     this.countLabels = 0;
/* 5130 */     this.stackDepth += 2;
/* 5131 */     if (this.maxLocals < 3) {
/* 5132 */       this.maxLocals = 3;
/*      */     }
/* 5134 */     if (this.stackDepth > this.stackMax)
/* 5135 */       this.stackMax = this.stackDepth;
/* 5136 */     if (this.classFileOffset >= this.bCodeStream.length) {
/* 5137 */       resizeByteArray();
/*      */     }
/* 5139 */     this.position += 1;
/* 5140 */     this.bCodeStream[(this.classFileOffset++)] = 31;
/*      */   }
/*      */ 
/*      */   public void lload_2() {
/* 5144 */     this.countLabels = 0;
/* 5145 */     this.stackDepth += 2;
/* 5146 */     if (this.maxLocals < 4) {
/* 5147 */       this.maxLocals = 4;
/*      */     }
/* 5149 */     if (this.stackDepth > this.stackMax)
/* 5150 */       this.stackMax = this.stackDepth;
/* 5151 */     if (this.classFileOffset >= this.bCodeStream.length) {
/* 5152 */       resizeByteArray();
/*      */     }
/* 5154 */     this.position += 1;
/* 5155 */     this.bCodeStream[(this.classFileOffset++)] = 32;
/*      */   }
/*      */ 
/*      */   public void lload_3() {
/* 5159 */     this.countLabels = 0;
/* 5160 */     this.stackDepth += 2;
/* 5161 */     if (this.maxLocals < 5) {
/* 5162 */       this.maxLocals = 5;
/*      */     }
/* 5164 */     if (this.stackDepth > this.stackMax)
/* 5165 */       this.stackMax = this.stackDepth;
/* 5166 */     if (this.classFileOffset >= this.bCodeStream.length) {
/* 5167 */       resizeByteArray();
/*      */     }
/* 5169 */     this.position += 1;
/* 5170 */     this.bCodeStream[(this.classFileOffset++)] = 33;
/*      */   }
/*      */ 
/*      */   public void lmul() {
/* 5174 */     this.countLabels = 0;
/* 5175 */     this.stackDepth -= 2;
/* 5176 */     if (this.classFileOffset >= this.bCodeStream.length) {
/* 5177 */       resizeByteArray();
/*      */     }
/* 5179 */     this.position += 1;
/* 5180 */     this.bCodeStream[(this.classFileOffset++)] = 105;
/*      */   }
/*      */ 
/*      */   public void lneg() {
/* 5184 */     this.countLabels = 0;
/* 5185 */     if (this.classFileOffset >= this.bCodeStream.length) {
/* 5186 */       resizeByteArray();
/*      */     }
/* 5188 */     this.position += 1;
/* 5189 */     this.bCodeStream[(this.classFileOffset++)] = 117;
/*      */   }
/*      */ 
/*      */   public final void load(LocalVariableBinding localBinding) {
/* 5193 */     load(localBinding.type, localBinding.resolvedPosition);
/*      */   }
/*      */ 
/*      */   protected final void load(TypeBinding typeBinding, int resolvedPosition) {
/* 5197 */     this.countLabels = 0;
/*      */ 
/* 5199 */     switch (typeBinding.id) {
/*      */     case 2:
/*      */     case 3:
/*      */     case 4:
/*      */     case 5:
/*      */     case 10:
/* 5205 */       switch (resolvedPosition) {
/*      */       case 0:
/* 5207 */         iload_0();
/* 5208 */         break;
/*      */       case 1:
/* 5210 */         iload_1();
/* 5211 */         break;
/*      */       case 2:
/* 5213 */         iload_2();
/* 5214 */         break;
/*      */       case 3:
/* 5216 */         iload_3();
/* 5217 */         break;
/*      */       default:
/* 5222 */         iload(resolvedPosition);
/*      */       }
/* 5224 */       break;
/*      */     case 9:
/* 5226 */       switch (resolvedPosition) {
/*      */       case 0:
/* 5228 */         fload_0();
/* 5229 */         break;
/*      */       case 1:
/* 5231 */         fload_1();
/* 5232 */         break;
/*      */       case 2:
/* 5234 */         fload_2();
/* 5235 */         break;
/*      */       case 3:
/* 5237 */         fload_3();
/* 5238 */         break;
/*      */       default:
/* 5240 */         fload(resolvedPosition);
/*      */       }
/* 5242 */       break;
/*      */     case 7:
/* 5244 */       switch (resolvedPosition) {
/*      */       case 0:
/* 5246 */         lload_0();
/* 5247 */         break;
/*      */       case 1:
/* 5249 */         lload_1();
/* 5250 */         break;
/*      */       case 2:
/* 5252 */         lload_2();
/* 5253 */         break;
/*      */       case 3:
/* 5255 */         lload_3();
/* 5256 */         break;
/*      */       default:
/* 5258 */         lload(resolvedPosition);
/*      */       }
/* 5260 */       break;
/*      */     case 8:
/* 5262 */       switch (resolvedPosition) {
/*      */       case 0:
/* 5264 */         dload_0();
/* 5265 */         break;
/*      */       case 1:
/* 5267 */         dload_1();
/* 5268 */         break;
/*      */       case 2:
/* 5270 */         dload_2();
/* 5271 */         break;
/*      */       case 3:
/* 5273 */         dload_3();
/* 5274 */         break;
/*      */       default:
/* 5276 */         dload(resolvedPosition);
/*      */       }
/* 5278 */       break;
/*      */     case 6:
/*      */     default:
/* 5280 */       switch (resolvedPosition) {
/*      */       case 0:
/* 5282 */         aload_0();
/* 5283 */         break;
/*      */       case 1:
/* 5285 */         aload_1();
/* 5286 */         break;
/*      */       case 2:
/* 5288 */         aload_2();
/* 5289 */         break;
/*      */       case 3:
/* 5291 */         aload_3();
/* 5292 */         break;
/*      */       default:
/* 5294 */         aload(resolvedPosition);
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   public void lookupswitch(CaseLabel defaultLabel, int[] keys, int[] sortedIndexes, CaseLabel[] casesLabel) {
/* 5300 */     this.countLabels = 0;
/* 5301 */     this.stackDepth -= 1;
/* 5302 */     int length = keys.length;
/* 5303 */     int pos = this.position;
/* 5304 */     defaultLabel.placeInstruction();
/* 5305 */     for (int i = 0; i < length; i++) {
/* 5306 */       casesLabel[i].placeInstruction();
/*      */     }
/* 5308 */     if (this.classFileOffset >= this.bCodeStream.length) {
/* 5309 */       resizeByteArray();
/*      */     }
/* 5311 */     this.position += 1;
/* 5312 */     this.bCodeStream[(this.classFileOffset++)] = -85;
/* 5313 */     for (int i = 3 - (pos & 0x3); i > 0; i--) {
/* 5314 */       if (this.classFileOffset >= this.bCodeStream.length) {
/* 5315 */         resizeByteArray();
/*      */       }
/* 5317 */       this.position += 1;
/* 5318 */       this.bCodeStream[(this.classFileOffset++)] = 0;
/*      */     }
/* 5320 */     defaultLabel.branch();
/* 5321 */     writeSignedWord(length);
/* 5322 */     for (int i = 0; i < length; i++) {
/* 5323 */       writeSignedWord(keys[sortedIndexes[i]]);
/* 5324 */       casesLabel[sortedIndexes[i]].branch();
/*      */     }
/*      */   }
/*      */ 
/*      */   public void lor() {
/* 5329 */     this.countLabels = 0;
/* 5330 */     this.stackDepth -= 2;
/* 5331 */     if (this.classFileOffset >= this.bCodeStream.length) {
/* 5332 */       resizeByteArray();
/*      */     }
/* 5334 */     this.position += 1;
/* 5335 */     this.bCodeStream[(this.classFileOffset++)] = -127;
/*      */   }
/*      */ 
/*      */   public void lrem() {
/* 5339 */     this.countLabels = 0;
/* 5340 */     this.stackDepth -= 2;
/* 5341 */     if (this.classFileOffset >= this.bCodeStream.length) {
/* 5342 */       resizeByteArray();
/*      */     }
/* 5344 */     this.position += 1;
/* 5345 */     this.bCodeStream[(this.classFileOffset++)] = 113;
/*      */   }
/*      */ 
/*      */   public void lreturn() {
/* 5349 */     this.countLabels = 0;
/* 5350 */     this.stackDepth -= 2;
/*      */ 
/* 5352 */     if (this.classFileOffset >= this.bCodeStream.length) {
/* 5353 */       resizeByteArray();
/*      */     }
/* 5355 */     this.position += 1;
/* 5356 */     this.bCodeStream[(this.classFileOffset++)] = -83;
/* 5357 */     this.lastAbruptCompletion = this.position;
/*      */   }
/*      */ 
/*      */   public void lshl() {
/* 5361 */     this.countLabels = 0;
/* 5362 */     this.stackDepth -= 1;
/* 5363 */     if (this.classFileOffset >= this.bCodeStream.length) {
/* 5364 */       resizeByteArray();
/*      */     }
/* 5366 */     this.position += 1;
/* 5367 */     this.bCodeStream[(this.classFileOffset++)] = 121;
/*      */   }
/*      */ 
/*      */   public void lshr() {
/* 5371 */     this.countLabels = 0;
/* 5372 */     this.stackDepth -= 1;
/* 5373 */     if (this.classFileOffset >= this.bCodeStream.length) {
/* 5374 */       resizeByteArray();
/*      */     }
/* 5376 */     this.position += 1;
/* 5377 */     this.bCodeStream[(this.classFileOffset++)] = 123;
/*      */   }
/*      */ 
/*      */   public void lstore(int iArg) {
/* 5381 */     this.countLabels = 0;
/* 5382 */     this.stackDepth -= 2;
/* 5383 */     if (this.maxLocals <= iArg + 1) {
/* 5384 */       this.maxLocals = (iArg + 2);
/*      */     }
/* 5386 */     if (iArg > 255) {
/* 5387 */       if (this.classFileOffset + 3 >= this.bCodeStream.length) {
/* 5388 */         resizeByteArray();
/*      */       }
/* 5390 */       this.position += 2;
/* 5391 */       this.bCodeStream[(this.classFileOffset++)] = -60;
/* 5392 */       this.bCodeStream[(this.classFileOffset++)] = 55;
/* 5393 */       writeUnsignedShort(iArg);
/*      */     } else {
/* 5395 */       if (this.classFileOffset + 1 >= this.bCodeStream.length) {
/* 5396 */         resizeByteArray();
/*      */       }
/* 5398 */       this.position += 2;
/* 5399 */       this.bCodeStream[(this.classFileOffset++)] = 55;
/* 5400 */       this.bCodeStream[(this.classFileOffset++)] = (byte)iArg;
/*      */     }
/*      */   }
/*      */ 
/*      */   public void lstore_0() {
/* 5405 */     this.countLabels = 0;
/* 5406 */     this.stackDepth -= 2;
/* 5407 */     if (this.maxLocals < 2) {
/* 5408 */       this.maxLocals = 2;
/*      */     }
/* 5410 */     if (this.classFileOffset >= this.bCodeStream.length) {
/* 5411 */       resizeByteArray();
/*      */     }
/* 5413 */     this.position += 1;
/* 5414 */     this.bCodeStream[(this.classFileOffset++)] = 63;
/*      */   }
/*      */ 
/*      */   public void lstore_1() {
/* 5418 */     this.countLabels = 0;
/* 5419 */     this.stackDepth -= 2;
/* 5420 */     if (this.maxLocals < 3) {
/* 5421 */       this.maxLocals = 3;
/*      */     }
/* 5423 */     if (this.classFileOffset >= this.bCodeStream.length) {
/* 5424 */       resizeByteArray();
/*      */     }
/* 5426 */     this.position += 1;
/* 5427 */     this.bCodeStream[(this.classFileOffset++)] = 64;
/*      */   }
/*      */ 
/*      */   public void lstore_2() {
/* 5431 */     this.countLabels = 0;
/* 5432 */     this.stackDepth -= 2;
/* 5433 */     if (this.maxLocals < 4) {
/* 5434 */       this.maxLocals = 4;
/*      */     }
/* 5436 */     if (this.classFileOffset >= this.bCodeStream.length) {
/* 5437 */       resizeByteArray();
/*      */     }
/* 5439 */     this.position += 1;
/* 5440 */     this.bCodeStream[(this.classFileOffset++)] = 65;
/*      */   }
/*      */ 
/*      */   public void lstore_3() {
/* 5444 */     this.countLabels = 0;
/* 5445 */     this.stackDepth -= 2;
/* 5446 */     if (this.maxLocals < 5) {
/* 5447 */       this.maxLocals = 5;
/*      */     }
/* 5449 */     if (this.classFileOffset >= this.bCodeStream.length) {
/* 5450 */       resizeByteArray();
/*      */     }
/* 5452 */     this.position += 1;
/* 5453 */     this.bCodeStream[(this.classFileOffset++)] = 66;
/*      */   }
/*      */ 
/*      */   public void lsub() {
/* 5457 */     this.countLabels = 0;
/* 5458 */     this.stackDepth -= 2;
/* 5459 */     if (this.classFileOffset >= this.bCodeStream.length) {
/* 5460 */       resizeByteArray();
/*      */     }
/* 5462 */     this.position += 1;
/* 5463 */     this.bCodeStream[(this.classFileOffset++)] = 101;
/*      */   }
/*      */ 
/*      */   public void lushr() {
/* 5467 */     this.countLabels = 0;
/* 5468 */     this.stackDepth -= 1;
/* 5469 */     if (this.classFileOffset >= this.bCodeStream.length) {
/* 5470 */       resizeByteArray();
/*      */     }
/* 5472 */     this.position += 1;
/* 5473 */     this.bCodeStream[(this.classFileOffset++)] = 125;
/*      */   }
/*      */ 
/*      */   public void lxor() {
/* 5477 */     this.countLabels = 0;
/* 5478 */     this.stackDepth -= 2;
/* 5479 */     if (this.classFileOffset >= this.bCodeStream.length) {
/* 5480 */       resizeByteArray();
/*      */     }
/* 5482 */     this.position += 1;
/* 5483 */     this.bCodeStream[(this.classFileOffset++)] = -125;
/*      */   }
/*      */ 
/*      */   public void monitorenter() {
/* 5487 */     this.countLabels = 0;
/* 5488 */     this.stackDepth -= 1;
/* 5489 */     if (this.classFileOffset >= this.bCodeStream.length) {
/* 5490 */       resizeByteArray();
/*      */     }
/* 5492 */     this.position += 1;
/* 5493 */     this.bCodeStream[(this.classFileOffset++)] = -62;
/*      */   }
/*      */ 
/*      */   public void monitorexit() {
/* 5497 */     this.countLabels = 0;
/* 5498 */     this.stackDepth -= 1;
/* 5499 */     if (this.classFileOffset >= this.bCodeStream.length) {
/* 5500 */       resizeByteArray();
/*      */     }
/* 5502 */     this.position += 1;
/* 5503 */     this.bCodeStream[(this.classFileOffset++)] = -61;
/*      */   }
/*      */ 
/*      */   public void multianewarray(TypeBinding typeBinding, int dimensions) {
/* 5507 */     this.countLabels = 0;
/* 5508 */     this.stackDepth += 1 - dimensions;
/* 5509 */     if (this.classFileOffset + 3 >= this.bCodeStream.length) {
/* 5510 */       resizeByteArray();
/*      */     }
/* 5512 */     this.position += 2;
/* 5513 */     this.bCodeStream[(this.classFileOffset++)] = -59;
/* 5514 */     writeUnsignedShort(this.constantPool.literalIndexForType(typeBinding));
/* 5515 */     this.bCodeStream[(this.classFileOffset++)] = (byte)dimensions;
/*      */   }
/*      */ 
/*      */   public void new_(TypeBinding typeBinding)
/*      */   {
/* 5520 */     this.countLabels = 0;
/* 5521 */     this.stackDepth += 1;
/* 5522 */     if (this.stackDepth > this.stackMax)
/* 5523 */       this.stackMax = this.stackDepth;
/* 5524 */     if (this.classFileOffset + 2 >= this.bCodeStream.length) {
/* 5525 */       resizeByteArray();
/*      */     }
/* 5527 */     this.position += 1;
/* 5528 */     this.bCodeStream[(this.classFileOffset++)] = -69;
/* 5529 */     writeUnsignedShort(this.constantPool.literalIndexForType(typeBinding));
/*      */   }
/*      */ 
/*      */   public void newarray(int array_Type) {
/* 5533 */     this.countLabels = 0;
/* 5534 */     if (this.classFileOffset + 1 >= this.bCodeStream.length) {
/* 5535 */       resizeByteArray();
/*      */     }
/* 5537 */     this.position += 2;
/* 5538 */     this.bCodeStream[(this.classFileOffset++)] = -68;
/* 5539 */     this.bCodeStream[(this.classFileOffset++)] = (byte)array_Type;
/*      */   }
/*      */ 
/*      */   public void newArray(ArrayBinding arrayBinding) {
/* 5543 */     TypeBinding component = arrayBinding.elementsType();
/* 5544 */     switch (component.id) {
/*      */     case 10:
/* 5546 */       newarray(10);
/* 5547 */       break;
/*      */     case 3:
/* 5549 */       newarray(8);
/* 5550 */       break;
/*      */     case 5:
/* 5552 */       newarray(4);
/* 5553 */       break;
/*      */     case 4:
/* 5555 */       newarray(9);
/* 5556 */       break;
/*      */     case 2:
/* 5558 */       newarray(5);
/* 5559 */       break;
/*      */     case 7:
/* 5561 */       newarray(11);
/* 5562 */       break;
/*      */     case 9:
/* 5564 */       newarray(6);
/* 5565 */       break;
/*      */     case 8:
/* 5567 */       newarray(7);
/* 5568 */       break;
/*      */     case 6:
/*      */     default:
/* 5570 */       anewarray(component);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void newJavaLangAssertionError()
/*      */   {
/* 5576 */     this.countLabels = 0;
/* 5577 */     this.stackDepth += 1;
/* 5578 */     if (this.stackDepth > this.stackMax)
/* 5579 */       this.stackMax = this.stackDepth;
/* 5580 */     if (this.classFileOffset + 2 >= this.bCodeStream.length) {
/* 5581 */       resizeByteArray();
/*      */     }
/* 5583 */     this.position += 1;
/* 5584 */     this.bCodeStream[(this.classFileOffset++)] = -69;
/* 5585 */     writeUnsignedShort(this.constantPool.literalIndexForType(ConstantPool.JavaLangAssertionErrorConstantPoolName));
/*      */   }
/*      */ 
/*      */   public void newJavaLangError()
/*      */   {
/* 5590 */     this.countLabels = 0;
/* 5591 */     this.stackDepth += 1;
/* 5592 */     if (this.stackDepth > this.stackMax)
/* 5593 */       this.stackMax = this.stackDepth;
/* 5594 */     if (this.classFileOffset + 2 >= this.bCodeStream.length) {
/* 5595 */       resizeByteArray();
/*      */     }
/* 5597 */     this.position += 1;
/* 5598 */     this.bCodeStream[(this.classFileOffset++)] = -69;
/* 5599 */     writeUnsignedShort(this.constantPool.literalIndexForType(ConstantPool.JavaLangErrorConstantPoolName));
/*      */   }
/*      */ 
/*      */   public void newNoClassDefFoundError()
/*      */   {
/* 5604 */     this.countLabels = 0;
/* 5605 */     this.stackDepth += 1;
/* 5606 */     if (this.stackDepth > this.stackMax)
/* 5607 */       this.stackMax = this.stackDepth;
/* 5608 */     if (this.classFileOffset + 2 >= this.bCodeStream.length) {
/* 5609 */       resizeByteArray();
/*      */     }
/* 5611 */     this.position += 1;
/* 5612 */     this.bCodeStream[(this.classFileOffset++)] = -69;
/* 5613 */     writeUnsignedShort(this.constantPool.literalIndexForType(ConstantPool.JavaLangNoClassDefFoundErrorConstantPoolName));
/*      */   }
/*      */ 
/*      */   public void newStringContatenation()
/*      */   {
/* 5619 */     this.countLabels = 0;
/* 5620 */     this.stackDepth += 1;
/* 5621 */     if (this.stackDepth > this.stackMax) {
/* 5622 */       this.stackMax = this.stackDepth;
/*      */     }
/* 5624 */     if (this.classFileOffset + 2 >= this.bCodeStream.length) {
/* 5625 */       resizeByteArray();
/*      */     }
/* 5627 */     this.position += 1;
/* 5628 */     this.bCodeStream[(this.classFileOffset++)] = -69;
/* 5629 */     if (this.targetLevel >= 3211264L)
/* 5630 */       writeUnsignedShort(this.constantPool.literalIndexForType(ConstantPool.JavaLangStringBuilderConstantPoolName));
/*      */     else
/* 5632 */       writeUnsignedShort(this.constantPool.literalIndexForType(ConstantPool.JavaLangStringBufferConstantPoolName));
/*      */   }
/*      */ 
/*      */   public void newWrapperFor(int typeID)
/*      */   {
/* 5637 */     this.countLabels = 0;
/* 5638 */     this.stackDepth += 1;
/* 5639 */     if (this.stackDepth > this.stackMax)
/* 5640 */       this.stackMax = this.stackDepth;
/* 5641 */     if (this.classFileOffset + 2 >= this.bCodeStream.length) {
/* 5642 */       resizeByteArray();
/*      */     }
/* 5644 */     this.position += 1;
/* 5645 */     this.bCodeStream[(this.classFileOffset++)] = -69;
/* 5646 */     switch (typeID) {
/*      */     case 10:
/* 5648 */       writeUnsignedShort(this.constantPool.literalIndexForType(ConstantPool.JavaLangIntegerConstantPoolName));
/* 5649 */       break;
/*      */     case 5:
/* 5651 */       writeUnsignedShort(this.constantPool.literalIndexForType(ConstantPool.JavaLangBooleanConstantPoolName));
/* 5652 */       break;
/*      */     case 3:
/* 5654 */       writeUnsignedShort(this.constantPool.literalIndexForType(ConstantPool.JavaLangByteConstantPoolName));
/* 5655 */       break;
/*      */     case 2:
/* 5657 */       writeUnsignedShort(this.constantPool.literalIndexForType(ConstantPool.JavaLangCharacterConstantPoolName));
/* 5658 */       break;
/*      */     case 9:
/* 5660 */       writeUnsignedShort(this.constantPool.literalIndexForType(ConstantPool.JavaLangFloatConstantPoolName));
/* 5661 */       break;
/*      */     case 8:
/* 5663 */       writeUnsignedShort(this.constantPool.literalIndexForType(ConstantPool.JavaLangDoubleConstantPoolName));
/* 5664 */       break;
/*      */     case 4:
/* 5666 */       writeUnsignedShort(this.constantPool.literalIndexForType(ConstantPool.JavaLangShortConstantPoolName));
/* 5667 */       break;
/*      */     case 7:
/* 5669 */       writeUnsignedShort(this.constantPool.literalIndexForType(ConstantPool.JavaLangLongConstantPoolName));
/* 5670 */       break;
/*      */     case 6:
/* 5672 */       writeUnsignedShort(this.constantPool.literalIndexForType(ConstantPool.JavaLangVoidConstantPoolName));
/*      */     }
/*      */   }
/*      */ 
/*      */   public void nop() {
/* 5677 */     this.countLabels = 0;
/* 5678 */     if (this.classFileOffset >= this.bCodeStream.length) {
/* 5679 */       resizeByteArray();
/*      */     }
/* 5681 */     this.position += 1;
/* 5682 */     this.bCodeStream[(this.classFileOffset++)] = 0;
/*      */   }
/*      */ 
/*      */   public void optimizeBranch(int oldPosition, BranchLabel lbl) {
/* 5686 */     for (int i = 0; i < this.countLabels; i++) {
/* 5687 */       BranchLabel label = this.labels[i];
/* 5688 */       if (oldPosition == label.position) {
/* 5689 */         label.position = this.position;
/* 5690 */         if ((label instanceof CaseLabel)) {
/* 5691 */           int offset = this.position - ((CaseLabel)label).instructionPosition;
/* 5692 */           int[] forwardRefs = label.forwardReferences();
/* 5693 */           int j = 0; for (int length = label.forwardReferenceCount(); j < length; j++) {
/* 5694 */             int forwardRef = forwardRefs[j];
/* 5695 */             writeSignedWord(forwardRef, offset);
/*      */           }
/*      */         } else {
/* 5698 */           int[] forwardRefs = label.forwardReferences();
/* 5699 */           int j = 0; for (int length = label.forwardReferenceCount(); j < length; j++) {
/* 5700 */             int forwardRef = forwardRefs[j];
/* 5701 */             writePosition(lbl, forwardRef);
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   public void pop() {
/* 5709 */     this.countLabels = 0;
/* 5710 */     this.stackDepth -= 1;
/* 5711 */     if (this.classFileOffset >= this.bCodeStream.length) {
/* 5712 */       resizeByteArray();
/*      */     }
/* 5714 */     this.position += 1;
/* 5715 */     this.bCodeStream[(this.classFileOffset++)] = 87;
/*      */   }
/*      */ 
/*      */   public void pop2() {
/* 5719 */     this.countLabels = 0;
/* 5720 */     this.stackDepth -= 2;
/* 5721 */     if (this.classFileOffset >= this.bCodeStream.length) {
/* 5722 */       resizeByteArray();
/*      */     }
/* 5724 */     this.position += 1;
/* 5725 */     this.bCodeStream[(this.classFileOffset++)] = 88;
/*      */   }
/*      */ 
/*      */   public void pushExceptionOnStack(TypeBinding binding) {
/* 5729 */     this.stackDepth = 1;
/* 5730 */     if (this.stackDepth > this.stackMax)
/* 5731 */       this.stackMax = this.stackDepth;
/*      */   }
/*      */ 
/*      */   public void pushOnStack(TypeBinding binding) {
/* 5735 */     if (++this.stackDepth > this.stackMax)
/* 5736 */       this.stackMax = this.stackDepth;
/*      */   }
/*      */ 
/*      */   public void record(LocalVariableBinding local) {
/* 5740 */     if ((this.generateAttributes & 0x1C) == 0)
/*      */     {
/* 5743 */       return;
/* 5744 */     }if (this.allLocalsCounter == this.locals.length)
/*      */     {
/* 5746 */       System.arraycopy(this.locals, 0, this.locals = new LocalVariableBinding[this.allLocalsCounter + 10], 0, this.allLocalsCounter);
/*      */     }
/* 5748 */     this.locals[(this.allLocalsCounter++)] = local;
/* 5749 */     local.initializationPCs = new int[4];
/* 5750 */     local.initializationCount = 0;
/*      */   }
/*      */ 
/*      */   public void recordExpressionType(TypeBinding typeBinding)
/*      */   {
/*      */   }
/*      */ 
/*      */   public void recordPositionsFrom(int startPC, int sourcePos) {
/* 5758 */     recordPositionsFrom(startPC, sourcePos, false);
/*      */   }
/*      */ 
/*      */   public void recordPositionsFrom(int startPC, int sourcePos, boolean widen)
/*      */   {
/* 5767 */     if (((this.generateAttributes & 0x2) == 0) || 
/* 5768 */       (sourcePos == 0) || (
/* 5769 */       (startPC == this.position) && (!widen))) {
/* 5770 */       return;
/*      */     }
/*      */ 
/* 5773 */     if (this.pcToSourceMapSize + 4 > this.pcToSourceMap.length)
/*      */     {
/* 5775 */       System.arraycopy(this.pcToSourceMap, 0, this.pcToSourceMap = new int[this.pcToSourceMapSize << 1], 0, this.pcToSourceMapSize);
/*      */     }
/*      */ 
/* 5778 */     if (this.pcToSourceMapSize > 0)
/*      */     {
/* 5780 */       int previousLineNumber = this.pcToSourceMap[(this.pcToSourceMapSize - 1)];
/*      */       int lineNumber;
/*      */       int lineNumber;
/* 5781 */       if (this.lineNumberStart == this.lineNumberEnd)
/*      */       {
/* 5783 */         lineNumber = this.lineNumberStart;
/*      */       }
/*      */       else {
/* 5786 */         int[] lineSeparatorPositions2 = this.lineSeparatorPositions;
/* 5787 */         int length = lineSeparatorPositions2.length;
/*      */         int lineNumber;
/* 5788 */         if (previousLineNumber == 1) {
/* 5789 */           if (sourcePos < lineSeparatorPositions2[0]) {
/* 5790 */             int lineNumber = 1;
/*      */ 
/* 5794 */             if (startPC < this.pcToSourceMap[(this.pcToSourceMapSize - 2)]) {
/* 5795 */               int insertionIndex = insertionIndex(this.pcToSourceMap, this.pcToSourceMapSize, startPC);
/* 5796 */               if (insertionIndex != -1)
/*      */               {
/* 5804 */                 if ((insertionIndex <= 1) || (this.pcToSourceMap[(insertionIndex - 1)] != lineNumber)) {
/* 5805 */                   if ((this.pcToSourceMapSize > 4) && (this.pcToSourceMap[(this.pcToSourceMapSize - 4)] > startPC)) {
/* 5806 */                     System.arraycopy(this.pcToSourceMap, insertionIndex, this.pcToSourceMap, insertionIndex + 2, this.pcToSourceMapSize - 2 - insertionIndex);
/* 5807 */                     this.pcToSourceMap[(insertionIndex++)] = startPC;
/* 5808 */                     this.pcToSourceMap[insertionIndex] = lineNumber;
/*      */                   } else {
/* 5810 */                     this.pcToSourceMap[(this.pcToSourceMapSize - 2)] = startPC;
/*      */                   }
/*      */                 }
/*      */               }
/*      */             }
/* 5815 */             this.lastEntryPC = this.position;
/* 5816 */             return;
/* 5817 */           }if ((length == 1) || (sourcePos < lineSeparatorPositions2[1])) {
/* 5818 */             int lineNumber = 2;
/* 5819 */             if (startPC <= this.lastEntryPC)
/*      */             {
/* 5822 */               int insertionIndex = insertionIndex(this.pcToSourceMap, this.pcToSourceMapSize, startPC);
/* 5823 */               if (insertionIndex != -1)
/*      */               {
/* 5825 */                 int existingEntryIndex = indexOfSameLineEntrySincePC(startPC, lineNumber);
/*      */ 
/* 5836 */                 if (existingEntryIndex != -1)
/*      */                 {
/* 5838 */                   this.pcToSourceMap[existingEntryIndex] = startPC;
/* 5839 */                 } else if ((insertionIndex < 1) || (this.pcToSourceMap[(insertionIndex - 1)] != lineNumber))
/*      */                 {
/* 5841 */                   System.arraycopy(this.pcToSourceMap, insertionIndex, this.pcToSourceMap, insertionIndex + 2, this.pcToSourceMapSize - insertionIndex);
/* 5842 */                   this.pcToSourceMap[(insertionIndex++)] = startPC;
/* 5843 */                   this.pcToSourceMap[insertionIndex] = lineNumber;
/* 5844 */                   this.pcToSourceMapSize += 2;
/*      */                 }
/* 5846 */               } else if (this.position != this.lastEntryPC) {
/* 5847 */                 if ((this.lastEntryPC == startPC) || (this.lastEntryPC == this.pcToSourceMap[(this.pcToSourceMapSize - 2)])) {
/* 5848 */                   this.pcToSourceMap[(this.pcToSourceMapSize - 1)] = lineNumber;
/*      */                 } else {
/* 5850 */                   this.pcToSourceMap[(this.pcToSourceMapSize++)] = this.lastEntryPC;
/* 5851 */                   this.pcToSourceMap[(this.pcToSourceMapSize++)] = lineNumber;
/*      */                 }
/* 5853 */               } else if ((this.pcToSourceMap[(this.pcToSourceMapSize - 1)] < lineNumber) && (widen))
/*      */               {
/* 5855 */                 this.pcToSourceMap[(this.pcToSourceMapSize - 1)] = lineNumber;
/*      */               }
/*      */             }
/*      */             else {
/* 5859 */               this.pcToSourceMap[(this.pcToSourceMapSize++)] = startPC;
/* 5860 */               this.pcToSourceMap[(this.pcToSourceMapSize++)] = lineNumber;
/*      */             }
/* 5862 */             this.lastEntryPC = this.position;
/* 5863 */             return;
/*      */           }
/*      */ 
/* 5866 */           lineNumber = Util.getLineNumber(sourcePos, this.lineSeparatorPositions, this.lineNumberStart - 1, this.lineNumberEnd - 1);
/*      */         }
/*      */         else
/*      */         {
/*      */           int lineNumber;
/* 5868 */           if (previousLineNumber < length)
/*      */           {
/*      */             int lineNumber;
/* 5869 */             if (lineSeparatorPositions2[(previousLineNumber - 2)] < sourcePos) {
/* 5870 */               if (sourcePos < lineSeparatorPositions2[(previousLineNumber - 1)]) {
/* 5871 */                 int lineNumber = previousLineNumber;
/*      */ 
/* 5875 */                 if (startPC < this.pcToSourceMap[(this.pcToSourceMapSize - 2)]) {
/* 5876 */                   int insertionIndex = insertionIndex(this.pcToSourceMap, this.pcToSourceMapSize, startPC);
/* 5877 */                   if (insertionIndex != -1)
/*      */                   {
/* 5885 */                     if ((insertionIndex <= 1) || (this.pcToSourceMap[(insertionIndex - 1)] != lineNumber)) {
/* 5886 */                       if ((this.pcToSourceMapSize > 4) && (this.pcToSourceMap[(this.pcToSourceMapSize - 4)] > startPC)) {
/* 5887 */                         System.arraycopy(this.pcToSourceMap, insertionIndex, this.pcToSourceMap, insertionIndex + 2, this.pcToSourceMapSize - 2 - insertionIndex);
/* 5888 */                         this.pcToSourceMap[(insertionIndex++)] = startPC;
/* 5889 */                         this.pcToSourceMap[insertionIndex] = lineNumber;
/*      */                       } else {
/* 5891 */                         this.pcToSourceMap[(this.pcToSourceMapSize - 2)] = startPC;
/*      */                       }
/*      */                     }
/*      */                   }
/*      */                 }
/* 5896 */                 this.lastEntryPC = this.position;
/* 5897 */                 return;
/* 5898 */               }if (sourcePos < lineSeparatorPositions2[previousLineNumber]) {
/* 5899 */                 int lineNumber = previousLineNumber + 1;
/* 5900 */                 if (startPC <= this.lastEntryPC)
/*      */                 {
/* 5903 */                   int insertionIndex = insertionIndex(this.pcToSourceMap, this.pcToSourceMapSize, startPC);
/* 5904 */                   if (insertionIndex != -1)
/*      */                   {
/* 5906 */                     int existingEntryIndex = indexOfSameLineEntrySincePC(startPC, lineNumber);
/*      */ 
/* 5917 */                     if (existingEntryIndex != -1)
/*      */                     {
/* 5919 */                       this.pcToSourceMap[existingEntryIndex] = startPC;
/* 5920 */                     } else if ((insertionIndex < 1) || (this.pcToSourceMap[(insertionIndex - 1)] != lineNumber))
/*      */                     {
/* 5922 */                       System.arraycopy(this.pcToSourceMap, insertionIndex, this.pcToSourceMap, insertionIndex + 2, this.pcToSourceMapSize - insertionIndex);
/* 5923 */                       this.pcToSourceMap[(insertionIndex++)] = startPC;
/* 5924 */                       this.pcToSourceMap[insertionIndex] = lineNumber;
/* 5925 */                       this.pcToSourceMapSize += 2;
/*      */                     }
/* 5927 */                   } else if (this.position != this.lastEntryPC) {
/* 5928 */                     if ((this.lastEntryPC == startPC) || (this.lastEntryPC == this.pcToSourceMap[(this.pcToSourceMapSize - 2)])) {
/* 5929 */                       this.pcToSourceMap[(this.pcToSourceMapSize - 1)] = lineNumber;
/*      */                     } else {
/* 5931 */                       this.pcToSourceMap[(this.pcToSourceMapSize++)] = this.lastEntryPC;
/* 5932 */                       this.pcToSourceMap[(this.pcToSourceMapSize++)] = lineNumber;
/*      */                     }
/* 5934 */                   } else if ((this.pcToSourceMap[(this.pcToSourceMapSize - 1)] < lineNumber) && (widen))
/*      */                   {
/* 5936 */                     this.pcToSourceMap[(this.pcToSourceMapSize - 1)] = lineNumber;
/*      */                   }
/*      */                 }
/*      */                 else {
/* 5940 */                   this.pcToSourceMap[(this.pcToSourceMapSize++)] = startPC;
/* 5941 */                   this.pcToSourceMap[(this.pcToSourceMapSize++)] = lineNumber;
/*      */                 }
/* 5943 */                 this.lastEntryPC = this.position;
/* 5944 */                 return;
/*      */               }
/*      */ 
/* 5947 */               lineNumber = Util.getLineNumber(sourcePos, this.lineSeparatorPositions, this.lineNumberStart - 1, this.lineNumberEnd - 1);
/*      */             }
/*      */             else
/*      */             {
/* 5951 */               lineNumber = Util.getLineNumber(sourcePos, this.lineSeparatorPositions, this.lineNumberStart - 1, this.lineNumberEnd - 1);
/*      */             }
/*      */           } else {
/* 5953 */             if (lineSeparatorPositions2[(length - 1)] < sourcePos) {
/* 5954 */               int lineNumber = length + 1;
/* 5955 */               if (startPC <= this.lastEntryPC)
/*      */               {
/* 5958 */                 int insertionIndex = insertionIndex(this.pcToSourceMap, this.pcToSourceMapSize, startPC);
/* 5959 */                 if (insertionIndex != -1)
/*      */                 {
/* 5961 */                   int existingEntryIndex = indexOfSameLineEntrySincePC(startPC, lineNumber);
/*      */ 
/* 5972 */                   if (existingEntryIndex != -1)
/*      */                   {
/* 5974 */                     this.pcToSourceMap[existingEntryIndex] = startPC;
/* 5975 */                   } else if ((insertionIndex < 1) || (this.pcToSourceMap[(insertionIndex - 1)] != lineNumber))
/*      */                   {
/* 5977 */                     System.arraycopy(this.pcToSourceMap, insertionIndex, this.pcToSourceMap, insertionIndex + 2, this.pcToSourceMapSize - insertionIndex);
/* 5978 */                     this.pcToSourceMap[(insertionIndex++)] = startPC;
/* 5979 */                     this.pcToSourceMap[insertionIndex] = lineNumber;
/* 5980 */                     this.pcToSourceMapSize += 2;
/*      */                   }
/* 5982 */                 } else if (this.position != this.lastEntryPC) {
/* 5983 */                   if ((this.lastEntryPC == startPC) || (this.lastEntryPC == this.pcToSourceMap[(this.pcToSourceMapSize - 2)])) {
/* 5984 */                     this.pcToSourceMap[(this.pcToSourceMapSize - 1)] = lineNumber;
/*      */                   } else {
/* 5986 */                     this.pcToSourceMap[(this.pcToSourceMapSize++)] = this.lastEntryPC;
/* 5987 */                     this.pcToSourceMap[(this.pcToSourceMapSize++)] = lineNumber;
/*      */                   }
/* 5989 */                 } else if ((this.pcToSourceMap[(this.pcToSourceMapSize - 1)] < lineNumber) && (widen))
/*      */                 {
/* 5991 */                   this.pcToSourceMap[(this.pcToSourceMapSize - 1)] = lineNumber;
/*      */                 }
/*      */               }
/*      */               else {
/* 5995 */                 this.pcToSourceMap[(this.pcToSourceMapSize++)] = startPC;
/* 5996 */                 this.pcToSourceMap[(this.pcToSourceMapSize++)] = lineNumber;
/*      */               }
/* 5998 */               this.lastEntryPC = this.position;
/* 5999 */               return;
/*      */             }
/*      */ 
/* 6002 */             lineNumber = Util.getLineNumber(sourcePos, this.lineSeparatorPositions, this.lineNumberStart - 1, this.lineNumberEnd - 1);
/*      */           }
/*      */         }
/*      */       }
/* 6006 */       if (previousLineNumber != lineNumber) {
/* 6007 */         if (startPC <= this.lastEntryPC)
/*      */         {
/* 6010 */           int insertionIndex = insertionIndex(this.pcToSourceMap, this.pcToSourceMapSize, startPC);
/* 6011 */           if (insertionIndex != -1)
/*      */           {
/* 6013 */             int existingEntryIndex = indexOfSameLineEntrySincePC(startPC, lineNumber);
/*      */ 
/* 6024 */             if (existingEntryIndex != -1)
/*      */             {
/* 6026 */               this.pcToSourceMap[existingEntryIndex] = startPC;
/* 6027 */             } else if ((insertionIndex < 1) || (this.pcToSourceMap[(insertionIndex - 1)] != lineNumber))
/*      */             {
/* 6029 */               System.arraycopy(this.pcToSourceMap, insertionIndex, this.pcToSourceMap, insertionIndex + 2, this.pcToSourceMapSize - insertionIndex);
/* 6030 */               this.pcToSourceMap[(insertionIndex++)] = startPC;
/* 6031 */               this.pcToSourceMap[insertionIndex] = lineNumber;
/* 6032 */               this.pcToSourceMapSize += 2;
/*      */             }
/* 6034 */           } else if (this.position != this.lastEntryPC) {
/* 6035 */             if ((this.lastEntryPC == startPC) || (this.lastEntryPC == this.pcToSourceMap[(this.pcToSourceMapSize - 2)])) {
/* 6036 */               this.pcToSourceMap[(this.pcToSourceMapSize - 1)] = lineNumber;
/*      */             } else {
/* 6038 */               this.pcToSourceMap[(this.pcToSourceMapSize++)] = this.lastEntryPC;
/* 6039 */               this.pcToSourceMap[(this.pcToSourceMapSize++)] = lineNumber;
/*      */             }
/* 6041 */           } else if ((this.pcToSourceMap[(this.pcToSourceMapSize - 1)] < lineNumber) && (widen))
/*      */           {
/* 6043 */             this.pcToSourceMap[(this.pcToSourceMapSize - 1)] = lineNumber;
/*      */           }
/*      */         }
/*      */         else {
/* 6047 */           this.pcToSourceMap[(this.pcToSourceMapSize++)] = startPC;
/* 6048 */           this.pcToSourceMap[(this.pcToSourceMapSize++)] = lineNumber;
/*      */         }
/*      */ 
/*      */       }
/* 6054 */       else if (startPC < this.pcToSourceMap[(this.pcToSourceMapSize - 2)]) {
/* 6055 */         int insertionIndex = insertionIndex(this.pcToSourceMap, this.pcToSourceMapSize, startPC);
/* 6056 */         if (insertionIndex != -1)
/*      */         {
/* 6064 */           if ((insertionIndex <= 1) || (this.pcToSourceMap[(insertionIndex - 1)] != lineNumber)) {
/* 6065 */             if ((this.pcToSourceMapSize > 4) && (this.pcToSourceMap[(this.pcToSourceMapSize - 4)] > startPC)) {
/* 6066 */               System.arraycopy(this.pcToSourceMap, insertionIndex, this.pcToSourceMap, insertionIndex + 2, this.pcToSourceMapSize - 2 - insertionIndex);
/* 6067 */               this.pcToSourceMap[(insertionIndex++)] = startPC;
/* 6068 */               this.pcToSourceMap[insertionIndex] = lineNumber;
/*      */             } else {
/* 6070 */               this.pcToSourceMap[(this.pcToSourceMapSize - 2)] = startPC;
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/*      */ 
/* 6076 */       this.lastEntryPC = this.position;
/*      */     } else {
/* 6078 */       int lineNumber = 0;
/* 6079 */       if (this.lineNumberStart == this.lineNumberEnd)
/*      */       {
/* 6081 */         lineNumber = this.lineNumberStart;
/*      */       }
/*      */       else {
/* 6084 */         lineNumber = Util.getLineNumber(sourcePos, this.lineSeparatorPositions, this.lineNumberStart - 1, this.lineNumberEnd - 1);
/*      */       }
/*      */ 
/* 6087 */       this.pcToSourceMap[(this.pcToSourceMapSize++)] = startPC;
/* 6088 */       this.pcToSourceMap[(this.pcToSourceMapSize++)] = lineNumber;
/* 6089 */       this.lastEntryPC = this.position;
/*      */     }
/*      */   }
/*      */ 
/*      */   public void registerExceptionHandler(ExceptionLabel anExceptionLabel)
/*      */   {
/*      */     int length;
/* 6098 */     if (this.exceptionLabelsCounter == (length = this.exceptionLabels.length))
/*      */     {
/* 6100 */       System.arraycopy(this.exceptionLabels, 0, this.exceptionLabels = new ExceptionLabel[length + 5], 0, length);
/*      */     }
/*      */ 
/* 6103 */     this.exceptionLabels[(this.exceptionLabelsCounter++)] = anExceptionLabel;
/*      */   }
/*      */ 
/*      */   public void removeNotDefinitelyAssignedVariables(Scope scope, int initStateIndex)
/*      */   {
/* 6109 */     if ((this.generateAttributes & 0x1C) == 0)
/*      */     {
/* 6112 */       return;
/* 6113 */     }for (int i = 0; i < this.visibleLocalsCount; i++) {
/* 6114 */       LocalVariableBinding localBinding = this.visibleLocals[i];
/* 6115 */       if ((localBinding != null) && (!isDefinitelyAssigned(scope, initStateIndex, localBinding)) && (localBinding.initializationCount > 0))
/* 6116 */         localBinding.recordInitializationEndPC(this.position);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void removeUnusedPcToSourceMapEntries()
/*      */   {
/* 6125 */     if (this.pcToSourceMapSize != 0)
/* 6126 */       while ((this.pcToSourceMapSize >= 2) && (this.pcToSourceMap[(this.pcToSourceMapSize - 2)] > this.position))
/* 6127 */         this.pcToSourceMapSize -= 2;
/*      */   }
/*      */ 
/*      */   public void removeVariable(LocalVariableBinding localBinding)
/*      */   {
/* 6132 */     if (localBinding == null) return;
/* 6133 */     if (localBinding.initializationCount > 0) {
/* 6134 */       localBinding.recordInitializationEndPC(this.position);
/*      */     }
/* 6136 */     for (int i = this.visibleLocalsCount - 1; i >= 0; i--) {
/* 6137 */       LocalVariableBinding visibleLocal = this.visibleLocals[i];
/* 6138 */       if (visibleLocal == localBinding) {
/* 6139 */         this.visibleLocals[i] = null;
/* 6140 */         return;
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   public void reset(AbstractMethodDeclaration referenceMethod, ClassFile targetClassFile)
/*      */   {
/* 6150 */     init(targetClassFile);
/* 6151 */     this.methodDeclaration = referenceMethod;
/* 6152 */     int[] lineSeparatorPositions2 = this.lineSeparatorPositions;
/* 6153 */     if (lineSeparatorPositions2 != null) {
/* 6154 */       int length = lineSeparatorPositions2.length;
/* 6155 */       int lineSeparatorPositionsEnd = length - 1;
/* 6156 */       if ((referenceMethod.isClinit()) || 
/* 6157 */         (referenceMethod.isConstructor())) {
/* 6158 */         this.lineNumberStart = 1;
/* 6159 */         this.lineNumberEnd = (length == 0 ? 1 : length);
/*      */       } else {
/* 6161 */         int start = Util.getLineNumber(referenceMethod.bodyStart, lineSeparatorPositions2, 0, lineSeparatorPositionsEnd);
/* 6162 */         this.lineNumberStart = start;
/* 6163 */         if (start > lineSeparatorPositionsEnd) {
/* 6164 */           this.lineNumberEnd = start;
/*      */         } else {
/* 6166 */           int end = Util.getLineNumber(referenceMethod.bodyEnd, lineSeparatorPositions2, start - 1, lineSeparatorPositionsEnd);
/* 6167 */           if (end >= lineSeparatorPositionsEnd) {
/* 6168 */             end = length;
/*      */           }
/* 6170 */           this.lineNumberEnd = (end == 0 ? 1 : end);
/*      */         }
/*      */       }
/*      */     }
/* 6174 */     this.preserveUnusedLocals = referenceMethod.scope.compilerOptions().preserveAllLocalVariables;
/* 6175 */     initializeMaxLocals(referenceMethod.binding);
/*      */   }
/*      */ 
/*      */   public void reset(ClassFile givenClassFile) {
/* 6179 */     this.targetLevel = givenClassFile.targetJDK;
/* 6180 */     int produceAttributes = givenClassFile.produceAttributes;
/* 6181 */     this.generateAttributes = produceAttributes;
/* 6182 */     if ((produceAttributes & 0x2) != 0)
/* 6183 */       this.lineSeparatorPositions = givenClassFile.referenceBinding.scope.referenceCompilationUnit().compilationResult.getLineSeparatorPositions();
/*      */     else
/* 6185 */       this.lineSeparatorPositions = null;
/*      */   }
/*      */ 
/*      */   public void resetForProblemClinit(ClassFile targetClassFile)
/*      */   {
/* 6193 */     init(targetClassFile);
/* 6194 */     initializeMaxLocals(null);
/*      */   }
/*      */ 
/*      */   public void resetInWideMode() {
/* 6198 */     this.wideMode = true;
/*      */   }
/*      */ 
/*      */   private final void resizeByteArray() {
/* 6202 */     int length = this.bCodeStream.length;
/* 6203 */     int requiredSize = length + length;
/* 6204 */     if (this.classFileOffset >= requiredSize)
/*      */     {
/* 6206 */       requiredSize = this.classFileOffset + length;
/*      */     }
/* 6208 */     System.arraycopy(this.bCodeStream, 0, this.bCodeStream = new byte[requiredSize], 0, length);
/*      */   }
/*      */ 
/*      */   public final void ret(int index) {
/* 6212 */     this.countLabels = 0;
/* 6213 */     if (index > 255) {
/* 6214 */       if (this.classFileOffset + 3 >= this.bCodeStream.length) {
/* 6215 */         resizeByteArray();
/*      */       }
/* 6217 */       this.position += 2;
/* 6218 */       this.bCodeStream[(this.classFileOffset++)] = -60;
/* 6219 */       this.bCodeStream[(this.classFileOffset++)] = -87;
/* 6220 */       writeUnsignedShort(index);
/*      */     } else {
/* 6222 */       if (this.classFileOffset + 1 >= this.bCodeStream.length) {
/* 6223 */         resizeByteArray();
/*      */       }
/* 6225 */       this.position += 2;
/* 6226 */       this.bCodeStream[(this.classFileOffset++)] = -87;
/* 6227 */       this.bCodeStream[(this.classFileOffset++)] = (byte)index;
/*      */     }
/*      */   }
/*      */ 
/*      */   public void return_() {
/* 6232 */     this.countLabels = 0;
/*      */ 
/* 6234 */     if (this.classFileOffset >= this.bCodeStream.length) {
/* 6235 */       resizeByteArray();
/*      */     }
/* 6237 */     this.position += 1;
/* 6238 */     this.bCodeStream[(this.classFileOffset++)] = -79;
/* 6239 */     this.lastAbruptCompletion = this.position;
/*      */   }
/*      */ 
/*      */   public void saload() {
/* 6243 */     this.countLabels = 0;
/* 6244 */     this.stackDepth -= 1;
/* 6245 */     if (this.classFileOffset >= this.bCodeStream.length) {
/* 6246 */       resizeByteArray();
/*      */     }
/* 6248 */     this.position += 1;
/* 6249 */     this.bCodeStream[(this.classFileOffset++)] = 53;
/*      */   }
/*      */ 
/*      */   public void sastore() {
/* 6253 */     this.countLabels = 0;
/* 6254 */     this.stackDepth -= 3;
/* 6255 */     if (this.classFileOffset >= this.bCodeStream.length) {
/* 6256 */       resizeByteArray();
/*      */     }
/* 6258 */     this.position += 1;
/* 6259 */     this.bCodeStream[(this.classFileOffset++)] = 86;
/*      */   }
/*      */ 
/*      */   public void sendOperator(int operatorConstant, int type_ID)
/*      */   {
/* 6267 */     switch (type_ID) {
/*      */     case 2:
/*      */     case 3:
/*      */     case 4:
/*      */     case 5:
/*      */     case 10:
/* 6273 */       switch (operatorConstant) {
/*      */       case 14:
/* 6275 */         iadd();
/* 6276 */         break;
/*      */       case 13:
/* 6278 */         isub();
/* 6279 */         break;
/*      */       case 15:
/* 6281 */         imul();
/* 6282 */         break;
/*      */       case 9:
/* 6284 */         idiv();
/* 6285 */         break;
/*      */       case 16:
/* 6287 */         irem();
/* 6288 */         break;
/*      */       case 10:
/* 6290 */         ishl();
/* 6291 */         break;
/*      */       case 17:
/* 6293 */         ishr();
/* 6294 */         break;
/*      */       case 19:
/* 6296 */         iushr();
/* 6297 */         break;
/*      */       case 2:
/* 6299 */         iand();
/* 6300 */         break;
/*      */       case 3:
/* 6302 */         ior();
/* 6303 */         break;
/*      */       case 8:
/* 6305 */         ixor();
/*      */       case 4:
/*      */       case 5:
/*      */       case 6:
/*      */       case 7:
/*      */       case 11:
/*      */       case 12:
/* 6308 */       case 18: } break;
/*      */     case 7:
/* 6310 */       switch (operatorConstant) {
/*      */       case 14:
/* 6312 */         ladd();
/* 6313 */         break;
/*      */       case 13:
/* 6315 */         lsub();
/* 6316 */         break;
/*      */       case 15:
/* 6318 */         lmul();
/* 6319 */         break;
/*      */       case 9:
/* 6321 */         ldiv();
/* 6322 */         break;
/*      */       case 16:
/* 6324 */         lrem();
/* 6325 */         break;
/*      */       case 10:
/* 6327 */         lshl();
/* 6328 */         break;
/*      */       case 17:
/* 6330 */         lshr();
/* 6331 */         break;
/*      */       case 19:
/* 6333 */         lushr();
/* 6334 */         break;
/*      */       case 2:
/* 6336 */         land();
/* 6337 */         break;
/*      */       case 3:
/* 6339 */         lor();
/* 6340 */         break;
/*      */       case 8:
/* 6342 */         lxor();
/*      */       case 4:
/*      */       case 5:
/*      */       case 6:
/*      */       case 7:
/*      */       case 11:
/*      */       case 12:
/* 6345 */       case 18: } break;
/*      */     case 9:
/* 6347 */       switch (operatorConstant) {
/*      */       case 14:
/* 6349 */         fadd();
/* 6350 */         break;
/*      */       case 13:
/* 6352 */         fsub();
/* 6353 */         break;
/*      */       case 15:
/* 6355 */         fmul();
/* 6356 */         break;
/*      */       case 9:
/* 6358 */         fdiv();
/* 6359 */         break;
/*      */       case 16:
/* 6361 */         frem();
/*      */       case 10:
/*      */       case 11:
/* 6363 */       case 12: } break;
/*      */     case 8:
/* 6365 */       switch (operatorConstant) {
/*      */       case 14:
/* 6367 */         dadd();
/* 6368 */         break;
/*      */       case 13:
/* 6370 */         dsub();
/* 6371 */         break;
/*      */       case 15:
/* 6373 */         dmul();
/* 6374 */         break;
/*      */       case 9:
/* 6376 */         ddiv();
/* 6377 */         break;
/*      */       case 16:
/* 6379 */         drem();
/*      */       case 10:
/*      */       case 11:
/*      */       case 12:
/*      */       }case 6:
/*      */     }
/*      */   }
/* 6385 */   public void sipush(int s) { this.countLabels = 0;
/* 6386 */     this.stackDepth += 1;
/* 6387 */     if (this.stackDepth > this.stackMax)
/* 6388 */       this.stackMax = this.stackDepth;
/* 6389 */     if (this.classFileOffset >= this.bCodeStream.length) {
/* 6390 */       resizeByteArray();
/*      */     }
/* 6392 */     this.position += 1;
/* 6393 */     this.bCodeStream[(this.classFileOffset++)] = 17;
/* 6394 */     writeSignedShort(s); }
/*      */ 
/*      */   public void store(LocalVariableBinding localBinding, boolean valueRequired)
/*      */   {
/* 6398 */     int localPosition = localBinding.resolvedPosition;
/*      */ 
/* 6400 */     switch (localBinding.type.id) {
/*      */     case 2:
/*      */     case 3:
/*      */     case 4:
/*      */     case 5:
/*      */     case 10:
/* 6406 */       if (valueRequired)
/* 6407 */         dup();
/* 6408 */       switch (localPosition) {
/*      */       case 0:
/* 6410 */         istore_0();
/* 6411 */         break;
/*      */       case 1:
/* 6413 */         istore_1();
/* 6414 */         break;
/*      */       case 2:
/* 6416 */         istore_2();
/* 6417 */         break;
/*      */       case 3:
/* 6419 */         istore_3();
/* 6420 */         break;
/*      */       default:
/* 6425 */         istore(localPosition);
/*      */       }
/* 6427 */       break;
/*      */     case 9:
/* 6429 */       if (valueRequired)
/* 6430 */         dup();
/* 6431 */       switch (localPosition) {
/*      */       case 0:
/* 6433 */         fstore_0();
/* 6434 */         break;
/*      */       case 1:
/* 6436 */         fstore_1();
/* 6437 */         break;
/*      */       case 2:
/* 6439 */         fstore_2();
/* 6440 */         break;
/*      */       case 3:
/* 6442 */         fstore_3();
/* 6443 */         break;
/*      */       default:
/* 6445 */         fstore(localPosition);
/*      */       }
/* 6447 */       break;
/*      */     case 8:
/* 6449 */       if (valueRequired)
/* 6450 */         dup2();
/* 6451 */       switch (localPosition) {
/*      */       case 0:
/* 6453 */         dstore_0();
/* 6454 */         break;
/*      */       case 1:
/* 6456 */         dstore_1();
/* 6457 */         break;
/*      */       case 2:
/* 6459 */         dstore_2();
/* 6460 */         break;
/*      */       case 3:
/* 6462 */         dstore_3();
/* 6463 */         break;
/*      */       default:
/* 6465 */         dstore(localPosition);
/*      */       }
/* 6467 */       break;
/*      */     case 7:
/* 6469 */       if (valueRequired)
/* 6470 */         dup2();
/* 6471 */       switch (localPosition) {
/*      */       case 0:
/* 6473 */         lstore_0();
/* 6474 */         break;
/*      */       case 1:
/* 6476 */         lstore_1();
/* 6477 */         break;
/*      */       case 2:
/* 6479 */         lstore_2();
/* 6480 */         break;
/*      */       case 3:
/* 6482 */         lstore_3();
/* 6483 */         break;
/*      */       default:
/* 6485 */         lstore(localPosition);
/*      */       }
/* 6487 */       break;
/*      */     case 6:
/*      */     default:
/* 6490 */       if (valueRequired)
/* 6491 */         dup();
/* 6492 */       switch (localPosition) {
/*      */       case 0:
/* 6494 */         astore_0();
/* 6495 */         break;
/*      */       case 1:
/* 6497 */         astore_1();
/* 6498 */         break;
/*      */       case 2:
/* 6500 */         astore_2();
/* 6501 */         break;
/*      */       case 3:
/* 6503 */         astore_3();
/* 6504 */         break;
/*      */       default:
/* 6506 */         astore(localPosition);
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   public void swap() {
/* 6512 */     this.countLabels = 0;
/* 6513 */     if (this.classFileOffset >= this.bCodeStream.length) {
/* 6514 */       resizeByteArray();
/*      */     }
/* 6516 */     this.position += 1;
/* 6517 */     this.bCodeStream[(this.classFileOffset++)] = 95;
/*      */   }
/*      */ 
/*      */   public void tableswitch(CaseLabel defaultLabel, int low, int high, int[] keys, int[] sortedIndexes, CaseLabel[] casesLabel) {
/* 6521 */     this.countLabels = 0;
/* 6522 */     this.stackDepth -= 1;
/* 6523 */     int length = casesLabel.length;
/* 6524 */     int pos = this.position;
/* 6525 */     defaultLabel.placeInstruction();
/* 6526 */     for (int i = 0; i < length; i++)
/* 6527 */       casesLabel[i].placeInstruction();
/* 6528 */     if (this.classFileOffset >= this.bCodeStream.length) {
/* 6529 */       resizeByteArray();
/*      */     }
/* 6531 */     this.position += 1;
/* 6532 */     this.bCodeStream[(this.classFileOffset++)] = -86;
/*      */ 
/* 6534 */     for (int i = 3 - (pos & 0x3); i > 0; i--) {
/* 6535 */       if (this.classFileOffset >= this.bCodeStream.length) {
/* 6536 */         resizeByteArray();
/*      */       }
/* 6538 */       this.position += 1;
/* 6539 */       this.bCodeStream[(this.classFileOffset++)] = 0;
/*      */     }
/* 6541 */     defaultLabel.branch();
/* 6542 */     writeSignedWord(low);
/* 6543 */     writeSignedWord(high);
/* 6544 */     int i = low; int j = low;
/*      */     while (true)
/*      */     {
/*      */       int index;
/* 6549 */       int key = keys[(index = sortedIndexes[(j - low)])];
/* 6550 */       if (key == i) {
/* 6551 */         casesLabel[index].branch();
/* 6552 */         j++;
/* 6553 */         if (i == high) break; 
/*      */       }
/*      */       else {
/* 6555 */         defaultLabel.branch();
/*      */       }
/* 6557 */       i++;
/*      */     }
/*      */   }
/*      */ 
/*      */   public void throwAnyException(LocalVariableBinding anyExceptionVariable) {
/* 6562 */     load(anyExceptionVariable);
/* 6563 */     athrow();
/*      */   }
/*      */ 
/*      */   public String toString() {
/* 6567 */     StringBuffer buffer = new StringBuffer("( position:");
/* 6568 */     buffer.append(this.position);
/* 6569 */     buffer.append(",\nstackDepth:");
/* 6570 */     buffer.append(this.stackDepth);
/* 6571 */     buffer.append(",\nmaxStack:");
/* 6572 */     buffer.append(this.stackMax);
/* 6573 */     buffer.append(",\nmaxLocals:");
/* 6574 */     buffer.append(this.maxLocals);
/* 6575 */     buffer.append(")");
/* 6576 */     return buffer.toString();
/*      */   }
/*      */ 
/*      */   public void updateLastRecordedEndPC(Scope scope, int pos)
/*      */   {
/* 6600 */     if ((this.generateAttributes & 0x2) != 0) {
/* 6601 */       this.lastEntryPC = pos;
/*      */     }
/*      */ 
/* 6604 */     if ((this.generateAttributes & 0x1C) != 0)
/*      */     {
/* 6607 */       int i = 0; for (int max = this.locals.length; i < max; i++) {
/* 6608 */         LocalVariableBinding local = this.locals[i];
/* 6609 */         if ((local == null) || (local.declaringScope != scope) || (local.initializationCount <= 0) || 
/* 6610 */           (local.initializationPCs[((local.initializationCount - 1 << 1) + 1)] != pos)) continue;
/* 6611 */         local.initializationPCs[((local.initializationCount - 1 << 1) + 1)] = this.position;
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   protected void writePosition(BranchLabel label)
/*      */   {
/* 6619 */     int offset = label.position - this.position + 1;
/* 6620 */     if ((Math.abs(offset) > 32767) && (!this.wideMode)) {
/* 6621 */       throw new AbortMethod(RESTART_IN_WIDE_MODE, null);
/*      */     }
/* 6623 */     writeSignedShort(offset);
/* 6624 */     int[] forwardRefs = label.forwardReferences();
/* 6625 */     int i = 0; for (int max = label.forwardReferenceCount(); i < max; i++)
/* 6626 */       writePosition(label, forwardRefs[i]);
/*      */   }
/*      */ 
/*      */   protected void writePosition(BranchLabel label, int forwardReference)
/*      */   {
/* 6631 */     int offset = label.position - forwardReference + 1;
/* 6632 */     if ((Math.abs(offset) > 32767) && (!this.wideMode)) {
/* 6633 */       throw new AbortMethod(RESTART_IN_WIDE_MODE, null);
/*      */     }
/* 6635 */     if (this.wideMode) {
/* 6636 */       if ((label.tagBits & 0x1) != 0)
/* 6637 */         writeSignedWord(forwardReference, offset);
/*      */       else
/* 6639 */         writeSignedShort(forwardReference, offset);
/*      */     }
/*      */     else
/* 6642 */       writeSignedShort(forwardReference, offset);
/*      */   }
/*      */ 
/*      */   private final void writeSignedShort(int value)
/*      */   {
/* 6652 */     if (this.classFileOffset + 1 >= this.bCodeStream.length) {
/* 6653 */       resizeByteArray();
/*      */     }
/* 6655 */     this.position += 2;
/* 6656 */     this.bCodeStream[(this.classFileOffset++)] = (byte)(value >> 8);
/* 6657 */     this.bCodeStream[(this.classFileOffset++)] = (byte)value;
/*      */   }
/*      */ 
/*      */   private final void writeSignedShort(int pos, int value) {
/* 6661 */     int currentOffset = this.startingClassFileOffset + pos;
/* 6662 */     if (currentOffset + 1 >= this.bCodeStream.length) {
/* 6663 */       resizeByteArray();
/*      */     }
/* 6665 */     this.bCodeStream[currentOffset] = (byte)(value >> 8);
/* 6666 */     this.bCodeStream[(currentOffset + 1)] = (byte)value;
/*      */   }
/*      */ 
/*      */   protected final void writeSignedWord(int value)
/*      */   {
/* 6671 */     if (this.classFileOffset + 3 >= this.bCodeStream.length) {
/* 6672 */       resizeByteArray();
/*      */     }
/* 6674 */     this.position += 4;
/* 6675 */     this.bCodeStream[(this.classFileOffset++)] = (byte)((value & 0xFF000000) >> 24);
/* 6676 */     this.bCodeStream[(this.classFileOffset++)] = (byte)((value & 0xFF0000) >> 16);
/* 6677 */     this.bCodeStream[(this.classFileOffset++)] = (byte)((value & 0xFF00) >> 8);
/* 6678 */     this.bCodeStream[(this.classFileOffset++)] = (byte)(value & 0xFF);
/*      */   }
/*      */ 
/*      */   protected void writeSignedWord(int pos, int value) {
/* 6682 */     int currentOffset = this.startingClassFileOffset + pos;
/* 6683 */     if (currentOffset + 3 >= this.bCodeStream.length) {
/* 6684 */       resizeByteArray();
/*      */     }
/* 6686 */     this.bCodeStream[(currentOffset++)] = (byte)((value & 0xFF000000) >> 24);
/* 6687 */     this.bCodeStream[(currentOffset++)] = (byte)((value & 0xFF0000) >> 16);
/* 6688 */     this.bCodeStream[(currentOffset++)] = (byte)((value & 0xFF00) >> 8);
/* 6689 */     this.bCodeStream[(currentOffset++)] = (byte)(value & 0xFF);
/*      */   }
/*      */ 
/*      */   private final void writeUnsignedShort(int value)
/*      */   {
/* 6698 */     this.position += 2;
/* 6699 */     this.bCodeStream[(this.classFileOffset++)] = (byte)(value >>> 8);
/* 6700 */     this.bCodeStream[(this.classFileOffset++)] = (byte)value;
/*      */   }
/*      */ 
/*      */   protected void writeWidePosition(BranchLabel label) {
/* 6704 */     int labelPos = label.position;
/* 6705 */     int offset = labelPos - this.position + 1;
/* 6706 */     writeSignedWord(offset);
/* 6707 */     int[] forwardRefs = label.forwardReferences();
/* 6708 */     int i = 0; for (int max = label.forwardReferenceCount(); i < max; i++) {
/* 6709 */       int forward = forwardRefs[i];
/* 6710 */       offset = labelPos - forward + 1;
/* 6711 */       writeSignedWord(forward, offset);
/*      */     }
/*      */   }
/*      */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\ecj-3.5.2.jar
 * Qualified Name:     org.eclipse.jdt.internal.compiler.codegen.CodeStream
 * JD-Core Version:    0.6.0
 */