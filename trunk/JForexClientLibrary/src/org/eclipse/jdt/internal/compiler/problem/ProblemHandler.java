/*     */ package org.eclipse.jdt.internal.compiler.problem;
/*     */ 
/*     */ import org.eclipse.jdt.core.compiler.CategorizedProblem;
/*     */ import org.eclipse.jdt.core.compiler.CharOperation;
/*     */ import org.eclipse.jdt.internal.compiler.CompilationResult;
/*     */ import org.eclipse.jdt.internal.compiler.IErrorHandlingPolicy;
/*     */ import org.eclipse.jdt.internal.compiler.IProblemFactory;
/*     */ import org.eclipse.jdt.internal.compiler.impl.CompilerOptions;
/*     */ import org.eclipse.jdt.internal.compiler.impl.ReferenceContext;
/*     */ import org.eclipse.jdt.internal.compiler.util.Util;
/*     */ 
/*     */ public class ProblemHandler
/*     */ {
/*  33 */   public static final String[] NoArgument = CharOperation.NO_STRINGS;
/*     */   public final IErrorHandlingPolicy policy;
/*     */   public final IProblemFactory problemFactory;
/*     */   public final CompilerOptions options;
/*     */ 
/*     */   public ProblemHandler(IErrorHandlingPolicy policy, CompilerOptions options, IProblemFactory problemFactory)
/*     */   {
/*  45 */     this.policy = policy;
/*  46 */     this.problemFactory = problemFactory;
/*  47 */     this.options = options;
/*     */   }
/*     */ 
/*     */   public int computeSeverity(int problemId)
/*     */   {
/*  56 */     return 1;
/*     */   }
/*     */ 
/*     */   public CategorizedProblem createProblem(char[] fileName, int problemId, String[] problemArguments, String[] messageArguments, int severity, int problemStartPosition, int problemEndPosition, int lineNumber, int columnNumber)
/*     */   {
/*  69 */     return this.problemFactory.createProblem(
/*  70 */       fileName, 
/*  71 */       problemId, 
/*  72 */       problemArguments, 
/*  73 */       messageArguments, 
/*  74 */       severity, 
/*  75 */       problemStartPosition, 
/*  76 */       problemEndPosition, 
/*  77 */       lineNumber, 
/*  78 */       columnNumber);
/*     */   }
/*     */ 
/*     */   public CategorizedProblem createProblem(char[] fileName, int problemId, String[] problemArguments, int elaborationId, String[] messageArguments, int severity, int problemStartPosition, int problemEndPosition, int lineNumber, int columnNumber)
/*     */   {
/*  91 */     return this.problemFactory.createProblem(
/*  92 */       fileName, 
/*  93 */       problemId, 
/*  94 */       problemArguments, 
/*  95 */       elaborationId, 
/*  96 */       messageArguments, 
/*  97 */       severity, 
/*  98 */       problemStartPosition, 
/*  99 */       problemEndPosition, 
/* 100 */       lineNumber, 
/* 101 */       columnNumber);
/*     */   }
/*     */ 
/*     */   public void handle(int problemId, String[] problemArguments, int elaborationId, String[] messageArguments, int severity, int problemStartPosition, int problemEndPosition, ReferenceContext referenceContext, CompilationResult unitResult)
/*     */   {
/* 114 */     if (severity == -1) {
/* 115 */       return;
/*     */     }
/*     */ 
/* 118 */     if (referenceContext == null) {
/* 119 */       if ((severity & 0x1) != 0) {
/* 120 */         CategorizedProblem problem = createProblem(null, problemId, problemArguments, elaborationId, messageArguments, severity, 0, 0, 0, 0);
/* 121 */         throw new AbortCompilation(null, problem);
/*     */       }
/* 123 */       return;
/*     */     }
/* 147 */     int[] lineEnds;
/* 128 */     int lineNumber = problemStartPosition >= 0 ? 
/* 129 */       Util.getLineNumber(problemStartPosition, lineEnds = unitResult.getLineSeparatorPositions(), 0, lineEnds.length - 1) : 
/* 130 */       0;
/* 131 */     int columnNumber = problemStartPosition >= 0 ? 
/* 132 */       Util.searchColumnNumber(unitResult.getLineSeparatorPositions(), lineNumber, problemStartPosition) : 
/* 133 */       0;
/* 134 */     CategorizedProblem problem = 
/* 135 */       createProblem(
/* 136 */       unitResult.getFileName(), 
/* 137 */       problemId, 
/* 138 */       problemArguments, 
/* 139 */       elaborationId, 
/* 140 */       messageArguments, 
/* 141 */       severity, 
/* 142 */       problemStartPosition, 
/* 143 */       problemEndPosition, 
/* 144 */       lineNumber, 
/* 145 */       columnNumber);
/*     */ 
/* 147 */     if (problem == null) return;
/*     */ 
/* 149 */     switch (severity & 0x1) {
/*     */     case 1:
/* 151 */       record(problem, unitResult, referenceContext);
/* 152 */       if ((severity & 0x80) == 0) break;
/* 153 */       referenceContext.tagAsHavingErrors();
/*     */       int abortLevel;
/* 156 */       if ((abortLevel = this.policy.stopOnFirstError() ? 2 : severity & 0x1E) == 0) break;
/* 157 */       referenceContext.abort(abortLevel, problem);
/*     */ 
/* 160 */       break;
/*     */     case 0:
/* 162 */       record(problem, unitResult, referenceContext);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void handle(int problemId, String[] problemArguments, String[] messageArguments, int problemStartPosition, int problemEndPosition, ReferenceContext referenceContext, CompilationResult unitResult)
/*     */   {
/* 179 */     handle(
/* 180 */       problemId, 
/* 181 */       problemArguments, 
/* 182 */       0, 
/* 183 */       messageArguments, 
/* 184 */       computeSeverity(problemId), 
/* 185 */       problemStartPosition, 
/* 186 */       problemEndPosition, 
/* 187 */       referenceContext, 
/* 188 */       unitResult);
/*     */   }
/*     */   public void record(CategorizedProblem problem, CompilationResult unitResult, ReferenceContext referenceContext) {
/* 191 */     unitResult.record(problem, referenceContext);
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\ecj-3.5.2.jar
 * Qualified Name:     org.eclipse.jdt.internal.compiler.problem.ProblemHandler
 * JD-Core Version:    0.6.0
 */