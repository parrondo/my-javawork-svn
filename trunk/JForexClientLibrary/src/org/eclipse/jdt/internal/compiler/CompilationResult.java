/*     */ package org.eclipse.jdt.internal.compiler;
/*     */ 
/*     */ import java.util.Arrays;
/*     */ import java.util.Collection;
/*     */ import java.util.Comparator;
/*     */ import java.util.HashMap;
/*     */ import java.util.HashSet;
/*     */ import java.util.Hashtable;
/*     */ import java.util.Iterator;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import org.eclipse.jdt.core.compiler.CategorizedProblem;
/*     */ import org.eclipse.jdt.internal.compiler.ast.AbstractMethodDeclaration;
/*     */ import org.eclipse.jdt.internal.compiler.env.ICompilationUnit;
/*     */ import org.eclipse.jdt.internal.compiler.impl.ReferenceContext;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.SourceTypeBinding;
/*     */ import org.eclipse.jdt.internal.compiler.parser.RecoveryScannerData;
/*     */ import org.eclipse.jdt.internal.compiler.util.Util;
/*     */ 
/*     */ public class CompilationResult
/*     */ {
/*     */   public CategorizedProblem[] problems;
/*     */   public CategorizedProblem[] tasks;
/*     */   public int problemCount;
/*     */   public int taskCount;
/*     */   public ICompilationUnit compilationUnit;
/*     */   public Map problemsMap;
/*     */   public Set firstErrors;
/*     */   private int maxProblemPerUnit;
/*     */   public char[][][] qualifiedReferences;
/*     */   public char[][] simpleNameReferences;
/*     */   public char[][] rootReferences;
/*  64 */   public boolean hasAnnotations = false;
/*     */   public int[] lineSeparatorPositions;
/*     */   public RecoveryScannerData recoveryScannerData;
/*  67 */   public Map compiledTypes = new Hashtable(11);
/*     */   public int unitIndex;
/*     */   public int totalUnitsKnown;
/*  69 */   public boolean hasBeenAccepted = false;
/*     */   public char[] fileName;
/*  71 */   public boolean hasInconsistentToplevelHierarchies = false;
/*  72 */   public boolean hasSyntaxError = false;
/*     */   public char[][] packageName;
/*  74 */   public boolean checkSecondaryTypes = false;
/*     */ 
/*  76 */   private static final int[] EMPTY_LINE_ENDS = Util.EMPTY_INT_ARRAY;
/*  77 */   private static final Comparator PROBLEM_COMPARATOR = new Comparator() {
/*     */     public int compare(Object o1, Object o2) {
/*  79 */       return ((CategorizedProblem)o1).getSourceStart() - ((CategorizedProblem)o2).getSourceStart();
/*     */     }
/*  77 */   };
/*     */ 
/*     */   public CompilationResult(char[] fileName, int unitIndex, int totalUnitsKnown, int maxProblemPerUnit)
/*     */   {
/*  84 */     this.fileName = fileName;
/*  85 */     this.unitIndex = unitIndex;
/*  86 */     this.totalUnitsKnown = totalUnitsKnown;
/*  87 */     this.maxProblemPerUnit = maxProblemPerUnit;
/*     */   }
/*     */ 
/*     */   public CompilationResult(ICompilationUnit compilationUnit, int unitIndex, int totalUnitsKnown, int maxProblemPerUnit) {
/*  91 */     this.fileName = compilationUnit.getFileName();
/*  92 */     this.compilationUnit = compilationUnit;
/*  93 */     this.unitIndex = unitIndex;
/*  94 */     this.totalUnitsKnown = totalUnitsKnown;
/*  95 */     this.maxProblemPerUnit = maxProblemPerUnit;
/*     */   }
/*     */ 
/*     */   private int computePriority(CategorizedProblem problem)
/*     */   {
/* 104 */     int priority = 10000 - problem.getSourceLineNumber();
/* 105 */     if (priority < 0) priority = 0;
/* 106 */     if (problem.isError()) {
/* 107 */       priority += 100000;
/*     */     }
/* 109 */     ReferenceContext context = this.problemsMap == null ? null : (ReferenceContext)this.problemsMap.get(problem);
/* 110 */     if (context != null) {
/* 111 */       if ((context instanceof AbstractMethodDeclaration)) {
/* 112 */         AbstractMethodDeclaration method = (AbstractMethodDeclaration)context;
/* 113 */         if (method.isStatic())
/* 114 */           priority += 10000;
/*     */       }
/*     */       else {
/* 117 */         priority += 40000;
/*     */       }
/* 119 */       if (this.firstErrors.contains(problem))
/* 120 */         priority += 20000;
/*     */     }
/*     */     else {
/* 123 */       priority += 40000;
/*     */     }
/* 125 */     return priority;
/*     */   }
/*     */ 
/*     */   public CategorizedProblem[] getAllProblems() {
/* 129 */     CategorizedProblem[] onlyProblems = getProblems();
/* 130 */     int onlyProblemCount = onlyProblems != null ? onlyProblems.length : 0;
/* 131 */     CategorizedProblem[] onlyTasks = getTasks();
/* 132 */     int onlyTaskCount = onlyTasks != null ? onlyTasks.length : 0;
/* 133 */     if (onlyTaskCount == 0) {
/* 134 */       return onlyProblems;
/*     */     }
/* 136 */     if (onlyProblemCount == 0) {
/* 137 */       return onlyTasks;
/*     */     }
/* 139 */     int totalNumberOfProblem = onlyProblemCount + onlyTaskCount;
/* 140 */     CategorizedProblem[] allProblems = new CategorizedProblem[totalNumberOfProblem];
/* 141 */     int allProblemIndex = 0;
/* 142 */     int taskIndex = 0;
/* 143 */     int problemIndex = 0;
/* 144 */     while (taskIndex + problemIndex < totalNumberOfProblem) {
/* 145 */       CategorizedProblem nextTask = null;
/* 146 */       CategorizedProblem nextProblem = null;
/* 147 */       if (taskIndex < onlyTaskCount) {
/* 148 */         nextTask = onlyTasks[taskIndex];
/*     */       }
/* 150 */       if (problemIndex < onlyProblemCount) {
/* 151 */         nextProblem = onlyProblems[problemIndex];
/*     */       }
/*     */ 
/* 154 */       CategorizedProblem currentProblem = null;
/* 155 */       if (nextProblem != null) {
/* 156 */         if (nextTask != null) {
/* 157 */           if (nextProblem.getSourceStart() < nextTask.getSourceStart()) {
/* 158 */             currentProblem = nextProblem;
/* 159 */             problemIndex++;
/*     */           } else {
/* 161 */             currentProblem = nextTask;
/* 162 */             taskIndex++;
/*     */           }
/*     */         } else {
/* 165 */           currentProblem = nextProblem;
/* 166 */           problemIndex++;
/*     */         }
/*     */       }
/* 169 */       else if (nextTask != null) {
/* 170 */         currentProblem = nextTask;
/* 171 */         taskIndex++;
/*     */       }
/*     */ 
/* 174 */       allProblems[(allProblemIndex++)] = currentProblem;
/*     */     }
/* 176 */     return allProblems;
/*     */   }
/*     */ 
/*     */   public ClassFile[] getClassFiles() {
/* 180 */     ClassFile[] classFiles = new ClassFile[this.compiledTypes.size()];
/* 181 */     this.compiledTypes.values().toArray(classFiles);
/* 182 */     return classFiles;
/*     */   }
/*     */ 
/*     */   public ICompilationUnit getCompilationUnit()
/*     */   {
/* 189 */     return this.compilationUnit;
/*     */   }
/*     */ 
/*     */   public CategorizedProblem[] getErrors()
/*     */   {
/* 196 */     CategorizedProblem[] reportedProblems = getProblems();
/* 197 */     int errorCount = 0;
/* 198 */     for (int i = 0; i < this.problemCount; i++) {
/* 199 */       if (!reportedProblems[i].isError()) continue; errorCount++;
/*     */     }
/* 201 */     if (errorCount == this.problemCount) return reportedProblems;
/* 202 */     CategorizedProblem[] errors = new CategorizedProblem[errorCount];
/* 203 */     int index = 0;
/* 204 */     for (int i = 0; i < this.problemCount; i++) {
/* 205 */       if (!reportedProblems[i].isError()) continue; errors[(index++)] = reportedProblems[i];
/*     */     }
/* 207 */     return errors;
/*     */   }
/*     */ 
/*     */   public char[] getFileName()
/*     */   {
/* 215 */     return this.fileName;
/*     */   }
/*     */ 
/*     */   public int[] getLineSeparatorPositions() {
/* 219 */     return this.lineSeparatorPositions == null ? EMPTY_LINE_ENDS : this.lineSeparatorPositions;
/*     */   }
/*     */ 
/*     */   public CategorizedProblem[] getProblems()
/*     */   {
/* 232 */     if (this.problems != null) {
/* 233 */       if (this.problemCount != this.problems.length) {
/* 234 */         System.arraycopy(this.problems, 0, this.problems = new CategorizedProblem[this.problemCount], 0, this.problemCount);
/*     */       }
/*     */ 
/* 237 */       if ((this.maxProblemPerUnit > 0) && (this.problemCount > this.maxProblemPerUnit)) {
/* 238 */         quickPrioritize(this.problems, 0, this.problemCount - 1);
/* 239 */         this.problemCount = this.maxProblemPerUnit;
/* 240 */         System.arraycopy(this.problems, 0, this.problems = new CategorizedProblem[this.problemCount], 0, this.problemCount);
/*     */       }
/*     */ 
/* 244 */       Arrays.sort(this.problems, 0, this.problems.length, PROBLEM_COMPARATOR);
/*     */     }
/*     */ 
/* 247 */     return this.problems;
/*     */   }
/*     */ 
/*     */   public CategorizedProblem[] getTasks()
/*     */   {
/* 260 */     if (this.tasks != null)
/*     */     {
/* 262 */       if (this.taskCount != this.tasks.length) {
/* 263 */         System.arraycopy(this.tasks, 0, this.tasks = new CategorizedProblem[this.taskCount], 0, this.taskCount);
/*     */       }
/*     */ 
/* 266 */       Arrays.sort(this.tasks, 0, this.tasks.length, PROBLEM_COMPARATOR);
/*     */     }
/*     */ 
/* 269 */     return this.tasks;
/*     */   }
/*     */ 
/*     */   public boolean hasErrors() {
/* 273 */     if (this.problems != null) {
/* 274 */       for (int i = 0; i < this.problemCount; i++)
/* 275 */         if (this.problems[i].isError())
/* 276 */           return true;
/*     */     }
/* 278 */     return false;
/*     */   }
/*     */ 
/*     */   public boolean hasProblems() {
/* 282 */     return this.problemCount != 0;
/*     */   }
/*     */ 
/*     */   public boolean hasTasks() {
/* 286 */     return this.taskCount != 0;
/*     */   }
/*     */ 
/*     */   public boolean hasWarnings() {
/* 290 */     if (this.problems != null) {
/* 291 */       for (int i = 0; i < this.problemCount; i++)
/* 292 */         if (this.problems[i].isWarning())
/* 293 */           return true;
/*     */     }
/* 295 */     return false;
/*     */   }
/*     */ 
/*     */   private void quickPrioritize(CategorizedProblem[] problemList, int left, int right) {
/* 299 */     if (left >= right) return;
/*     */ 
/* 301 */     int original_left = left;
/* 302 */     int original_right = right;
/* 303 */     int mid = computePriority(problemList[(left + (right - left) / 2)]);
/*     */     do {
/* 305 */       while (computePriority(problemList[right]) < mid)
/* 306 */         right--;
/* 307 */       while (mid < computePriority(problemList[left]))
/* 308 */         left++;
/* 309 */       if (left <= right) {
/* 310 */         CategorizedProblem tmp = problemList[left];
/* 311 */         problemList[left] = problemList[right];
/* 312 */         problemList[right] = tmp;
/* 313 */         left++;
/* 314 */         right--;
/*     */       }
/*     */     }
/* 316 */     while (left <= right);
/* 317 */     if (original_left < right)
/* 318 */       quickPrioritize(problemList, original_left, right);
/* 319 */     if (left < original_right)
/* 320 */       quickPrioritize(problemList, left, original_right);
/*     */   }
/*     */ 
/*     */   public void recordPackageName(char[][] packName)
/*     */   {
/* 327 */     this.packageName = packName;
/*     */   }
/*     */ 
/*     */   public void record(CategorizedProblem newProblem, ReferenceContext referenceContext)
/*     */   {
/* 332 */     if (newProblem.getID() == 536871362) {
/* 333 */       recordTask(newProblem);
/* 334 */       return;
/*     */     }
/* 336 */     if (this.problemCount == 0)
/* 337 */       this.problems = new CategorizedProblem[5];
/* 338 */     else if (this.problemCount == this.problems.length) {
/* 339 */       System.arraycopy(this.problems, 0, this.problems = new CategorizedProblem[this.problemCount * 2], 0, this.problemCount);
/*     */     }
/* 341 */     this.problems[(this.problemCount++)] = newProblem;
/* 342 */     if (referenceContext != null) {
/* 343 */       if (this.problemsMap == null) this.problemsMap = new HashMap(5);
/* 344 */       if (this.firstErrors == null) this.firstErrors = new HashSet(5);
/* 345 */       if ((newProblem.isError()) && (!referenceContext.hasErrors())) this.firstErrors.add(newProblem);
/* 346 */       this.problemsMap.put(newProblem, referenceContext);
/*     */     }
/* 348 */     if (((newProblem.getID() & 0x40000000) != 0) && (newProblem.isError()))
/* 349 */       this.hasSyntaxError = true;
/*     */   }
/*     */ 
/*     */   public void record(char[] typeName, ClassFile classFile)
/*     */   {
/* 356 */     SourceTypeBinding sourceType = classFile.referenceBinding;
/* 357 */     if ((!sourceType.isLocalType()) && (sourceType.isHierarchyInconsistent())) {
/* 358 */       this.hasInconsistentToplevelHierarchies = true;
/*     */     }
/* 360 */     this.compiledTypes.put(typeName, classFile);
/*     */   }
/*     */ 
/*     */   private void recordTask(CategorizedProblem newProblem) {
/* 364 */     if (this.taskCount == 0)
/* 365 */       this.tasks = new CategorizedProblem[5];
/* 366 */     else if (this.taskCount == this.tasks.length) {
/* 367 */       System.arraycopy(this.tasks, 0, this.tasks = new CategorizedProblem[this.taskCount * 2], 0, this.taskCount);
/*     */     }
/* 369 */     this.tasks[(this.taskCount++)] = newProblem;
/*     */   }
/*     */ 
/*     */   public CompilationResult tagAsAccepted() {
/* 373 */     this.hasBeenAccepted = true;
/* 374 */     this.problemsMap = null;
/* 375 */     this.firstErrors = null;
/* 376 */     return this;
/*     */   }
/*     */ 
/*     */   public String toString() {
/* 380 */     StringBuffer buffer = new StringBuffer();
/* 381 */     if (this.fileName != null) {
/* 382 */       buffer.append("Filename : ").append(this.fileName).append('\n');
/*     */     }
/* 384 */     if (this.compiledTypes != null) {
/* 385 */       buffer.append("COMPILED type(s)\t\n");
/* 386 */       Iterator keys = this.compiledTypes.keySet().iterator();
/* 387 */       while (keys.hasNext()) {
/* 388 */         char[] typeName = (char[])keys.next();
/* 389 */         buffer.append("\t - ").append(typeName).append('\n');
/*     */       }
/*     */     }
/*     */     else {
/* 393 */       buffer.append("No COMPILED type\n");
/*     */     }
/* 395 */     if (this.problems != null) {
/* 396 */       buffer.append(this.problemCount).append(" PROBLEM(s) detected \n");
/* 397 */       for (int i = 0; i < this.problemCount; i++)
/* 398 */         buffer.append("\t - ").append(this.problems[i]).append('\n');
/*     */     }
/*     */     else {
/* 401 */       buffer.append("No PROBLEM\n");
/*     */     }
/* 403 */     return buffer.toString();
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\ecj-3.5.2.jar
 * Qualified Name:     org.eclipse.jdt.internal.compiler.CompilationResult
 * JD-Core Version:    0.6.0
 */