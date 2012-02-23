/*     */ package org.eclipse.jdt.internal.compiler.ast;
/*     */ 
/*     */ import org.eclipse.jdt.core.compiler.CategorizedProblem;
/*     */ import org.eclipse.jdt.core.compiler.CharOperation;
/*     */ import org.eclipse.jdt.internal.compiler.ASTVisitor;
/*     */ import org.eclipse.jdt.internal.compiler.ClassFile;
/*     */ import org.eclipse.jdt.internal.compiler.CompilationResult;
/*     */ import org.eclipse.jdt.internal.compiler.codegen.CodeStream;
/*     */ import org.eclipse.jdt.internal.compiler.flow.FlowInfo;
/*     */ import org.eclipse.jdt.internal.compiler.flow.InitializationFlowContext;
/*     */ import org.eclipse.jdt.internal.compiler.impl.CompilerOptions;
/*     */ import org.eclipse.jdt.internal.compiler.impl.ReferenceContext;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.AnnotationBinding;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.Binding;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.ClassScope;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.LocalVariableBinding;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.MethodBinding;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.MethodScope;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.ReferenceBinding;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;
/*     */ import org.eclipse.jdt.internal.compiler.parser.Parser;
/*     */ import org.eclipse.jdt.internal.compiler.problem.AbortCompilation;
/*     */ import org.eclipse.jdt.internal.compiler.problem.AbortCompilationUnit;
/*     */ import org.eclipse.jdt.internal.compiler.problem.AbortMethod;
/*     */ import org.eclipse.jdt.internal.compiler.problem.AbortType;
/*     */ import org.eclipse.jdt.internal.compiler.problem.ProblemReporter;
/*     */ import org.eclipse.jdt.internal.compiler.problem.ProblemSeverities;
/*     */ import org.eclipse.jdt.internal.compiler.util.Util;
/*     */ 
/*     */ public abstract class AbstractMethodDeclaration extends ASTNode
/*     */   implements ProblemSeverities, ReferenceContext
/*     */ {
/*     */   public MethodScope scope;
/*     */   public char[] selector;
/*     */   public int declarationSourceStart;
/*     */   public int declarationSourceEnd;
/*     */   public int modifiers;
/*     */   public int modifiersSourceStart;
/*     */   public Annotation[] annotations;
/*     */   public Argument[] arguments;
/*     */   public TypeReference[] thrownExceptions;
/*     */   public Statement[] statements;
/*     */   public int explicitDeclarations;
/*     */   public MethodBinding binding;
/*  43 */   public boolean ignoreFurtherInvestigation = false;
/*     */   public Javadoc javadoc;
/*     */   public int bodyStart;
/*  48 */   public int bodyEnd = -1;
/*     */   public CompilationResult compilationResult;
/*     */ 
/*     */   AbstractMethodDeclaration(CompilationResult compilationResult)
/*     */   {
/*  52 */     this.compilationResult = compilationResult;
/*     */   }
/*     */ 
/*     */   public void abort(int abortLevel, CategorizedProblem problem)
/*     */   {
/*  60 */     switch (abortLevel) {
/*     */     case 2:
/*  62 */       throw new AbortCompilation(this.compilationResult, problem);
/*     */     case 4:
/*  64 */       throw new AbortCompilationUnit(this.compilationResult, problem);
/*     */     case 8:
/*  66 */       throw new AbortType(this.compilationResult, problem);
/*     */     case 3:
/*     */     case 5:
/*     */     case 6:
/*  68 */     case 7: } throw new AbortMethod(this.compilationResult, problem);
/*     */   }
/*     */ 
/*     */   public abstract void analyseCode(ClassScope paramClassScope, InitializationFlowContext paramInitializationFlowContext, FlowInfo paramFlowInfo);
/*     */ 
/*     */   public void bindArguments()
/*     */   {
/*  79 */     if (this.arguments != null)
/*     */     {
/*  81 */       if (this.binding == null) {
/*  82 */         int i = 0; for (int length = this.arguments.length; i < length; i++) {
/*  83 */           this.arguments[i].bind(this.scope, null, true);
/*     */         }
/*  85 */         return;
/*     */       }
/*  87 */       boolean used = (this.binding.isAbstract()) || (this.binding.isNative());
/*  88 */       AnnotationBinding[][] paramAnnotations = (AnnotationBinding[][])null;
/*  89 */       int i = 0; for (int length = this.arguments.length; i < length; i++) {
/*  90 */         Argument argument = this.arguments[i];
/*  91 */         argument.bind(this.scope, this.binding.parameters[i], used);
/*  92 */         if (argument.annotations != null) {
/*  93 */           if (paramAnnotations == null) {
/*  94 */             paramAnnotations = new AnnotationBinding[length][];
/*  95 */             for (int j = 0; j < i; j++) {
/*  96 */               paramAnnotations[j] = Binding.NO_ANNOTATIONS;
/*     */             }
/*     */           }
/*  99 */           paramAnnotations[i] = argument.binding.getAnnotations();
/* 100 */         } else if (paramAnnotations != null) {
/* 101 */           paramAnnotations[i] = Binding.NO_ANNOTATIONS;
/*     */         }
/*     */       }
/* 104 */       if (paramAnnotations != null)
/* 105 */         this.binding.setParameterAnnotations(paramAnnotations);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void bindThrownExceptions()
/*     */   {
/* 114 */     if ((this.thrownExceptions != null) && 
/* 115 */       (this.binding != null) && 
/* 116 */       (this.binding.thrownExceptions != null)) {
/* 117 */       int thrownExceptionLength = this.thrownExceptions.length;
/* 118 */       int length = this.binding.thrownExceptions.length;
/* 119 */       if (length == thrownExceptionLength) {
/* 120 */         for (int i = 0; i < length; i++)
/* 121 */           this.thrownExceptions[i].resolvedType = this.binding.thrownExceptions[i];
/*     */       }
/*     */       else {
/* 124 */         int bindingIndex = 0;
/* 125 */         for (int i = 0; (i < thrownExceptionLength) && (bindingIndex < length); i++) {
/* 126 */           TypeReference thrownException = this.thrownExceptions[i];
/* 127 */           ReferenceBinding thrownExceptionBinding = this.binding.thrownExceptions[bindingIndex];
/* 128 */           char[][] bindingCompoundName = thrownExceptionBinding.compoundName;
/* 129 */           if (bindingCompoundName != null)
/* 130 */             if ((thrownException instanceof SingleTypeReference))
/*     */             {
/* 132 */               int lengthName = bindingCompoundName.length;
/* 133 */               char[] thrownExceptionTypeName = thrownException.getTypeName()[0];
/* 134 */               if (CharOperation.equals(thrownExceptionTypeName, bindingCompoundName[(lengthName - 1)])) {
/* 135 */                 thrownException.resolvedType = thrownExceptionBinding;
/* 136 */                 bindingIndex++;
/*     */               }
/*     */ 
/*     */             }
/* 140 */             else if (CharOperation.equals(thrownException.getTypeName(), bindingCompoundName)) {
/* 141 */               thrownException.resolvedType = thrownExceptionBinding;
/* 142 */               bindingIndex++;
/*     */             }
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public CompilationResult compilationResult()
/*     */   {
/* 152 */     return this.compilationResult;
/*     */   }
/*     */ 
/*     */   public void generateCode(ClassScope classScope, ClassFile classFile)
/*     */   {
/* 162 */     int problemResetPC = 0;
/* 163 */     classFile.codeStream.wideMode = false;
/* 164 */     if (this.ignoreFurtherInvestigation)
/*     */     {
/* 166 */       if (this.binding == null) {
/* 167 */         return;
/*     */       }
/* 169 */       CategorizedProblem[] problems = 
/* 170 */         this.scope.referenceCompilationUnit().compilationResult.getProblems();
/*     */       int problemsLength;
/* 171 */       CategorizedProblem[] problemsCopy = new CategorizedProblem[problemsLength = problems.length];
/* 172 */       System.arraycopy(problems, 0, problemsCopy, 0, problemsLength);
/* 173 */       classFile.addProblemMethod(this, this.binding, problemsCopy);
/* 174 */       return;
/*     */     }
/*     */     try
/*     */     {
/* 178 */       problemResetPC = classFile.contentsOffset;
/* 179 */       generateCode(classFile);
/*     */     }
/*     */     catch (AbortMethod e) {
/* 182 */       if (e.compilationResult == CodeStream.RESTART_IN_WIDE_MODE)
/*     */       {
/*     */         try {
/* 185 */           classFile.contentsOffset = problemResetPC;
/* 186 */           classFile.methodCount -= 1;
/* 187 */           classFile.codeStream.resetInWideMode();
/* 188 */           generateCode(classFile);
/*     */         }
/*     */         catch (AbortMethod localAbortMethod1) {
/* 191 */           CategorizedProblem[] problems = 
/* 192 */             this.scope.referenceCompilationUnit().compilationResult.getAllProblems();
/*     */           int problemsLength;
/* 193 */           CategorizedProblem[] problemsCopy = new CategorizedProblem[problemsLength = problems.length];
/* 194 */           System.arraycopy(problems, 0, problemsCopy, 0, problemsLength);
/* 195 */           classFile.addProblemMethod(this, this.binding, problemsCopy, problemResetPC);
/*     */         }
/*     */       }
/*     */       else
/*     */       {
/* 200 */         CategorizedProblem[] problems = 
/* 201 */           this.scope.referenceCompilationUnit().compilationResult.getAllProblems();
/*     */         int problemsLength;
/* 202 */         CategorizedProblem[] problemsCopy = new CategorizedProblem[problemsLength = problems.length];
/* 203 */         System.arraycopy(problems, 0, problemsCopy, 0, problemsLength);
/* 204 */         classFile.addProblemMethod(this, this.binding, problemsCopy, problemResetPC);
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public void generateCode(ClassFile classFile)
/*     */   {
/* 211 */     classFile.generateMethodInfoHeader(this.binding);
/* 212 */     int methodAttributeOffset = classFile.contentsOffset;
/* 213 */     int attributeNumber = classFile.generateMethodInfoAttribute(this.binding);
/* 214 */     if ((!this.binding.isNative()) && (!this.binding.isAbstract())) {
/* 215 */       int codeAttributeOffset = classFile.contentsOffset;
/* 216 */       classFile.generateCodeAttributeHeader();
/* 217 */       CodeStream codeStream = classFile.codeStream;
/* 218 */       codeStream.reset(this, classFile);
/*     */ 
/* 220 */       this.scope.computeLocalVariablePositions(this.binding.isStatic() ? 0 : 1, codeStream);
/*     */ 
/* 223 */       if (this.arguments != null) {
/* 224 */         int i = 0; for (int max = this.arguments.length; i < max; i++)
/*     */         {
/*     */           LocalVariableBinding argBinding;
/* 226 */           codeStream.addVisibleLocalVariable(argBinding = this.arguments[i].binding);
/* 227 */           argBinding.recordInitializationStartPC(0);
/*     */         }
/*     */       }
/* 230 */       if (this.statements != null) {
/* 231 */         int i = 0; for (int max = this.statements.length; i < max; i++) {
/* 232 */           this.statements[i].generateCode(this.scope, codeStream);
/*     */         }
/*     */       }
/* 235 */       if (this.ignoreFurtherInvestigation) {
/* 236 */         throw new AbortMethod(this.scope.referenceCompilationUnit().compilationResult, null);
/*     */       }
/* 238 */       if ((this.bits & 0x40) != 0) {
/* 239 */         codeStream.return_();
/*     */       }
/*     */ 
/* 242 */       codeStream.exitUserScope(this.scope);
/* 243 */       codeStream.recordPositionsFrom(0, this.declarationSourceEnd);
/* 244 */       classFile.completeCodeAttribute(codeAttributeOffset);
/* 245 */       attributeNumber++;
/*     */     } else {
/* 247 */       checkArgumentsSize();
/*     */     }
/* 249 */     classFile.completeMethodInfo(methodAttributeOffset, attributeNumber);
/*     */   }
/*     */ 
/*     */   private void checkArgumentsSize() {
/* 253 */     TypeBinding[] parameters = this.binding.parameters;
/* 254 */     int size = 1;
/* 255 */     int i = 0; for (int max = parameters.length; i < max; i++) {
/* 256 */       switch (parameters[i].id) {
/*     */       case 7:
/*     */       case 8:
/* 259 */         size += 2;
/* 260 */         break;
/*     */       default:
/* 262 */         size++;
/*     */       }
/*     */ 
/* 265 */       if (size > 255)
/* 266 */         this.scope.problemReporter().noMoreAvailableSpaceForArgument(this.scope.locals[i], this.scope.locals[i].declaration);
/*     */     }
/*     */   }
/*     */ 
/*     */   public boolean hasErrors()
/*     */   {
/* 272 */     return this.ignoreFurtherInvestigation;
/*     */   }
/*     */ 
/*     */   public boolean isAbstract()
/*     */   {
/* 277 */     if (this.binding != null)
/* 278 */       return this.binding.isAbstract();
/* 279 */     return (this.modifiers & 0x400) != 0;
/*     */   }
/*     */ 
/*     */   public boolean isAnnotationMethod()
/*     */   {
/* 284 */     return false;
/*     */   }
/*     */ 
/*     */   public boolean isClinit()
/*     */   {
/* 289 */     return false;
/*     */   }
/*     */ 
/*     */   public boolean isConstructor()
/*     */   {
/* 294 */     return false;
/*     */   }
/*     */ 
/*     */   public boolean isDefaultConstructor()
/*     */   {
/* 299 */     return false;
/*     */   }
/*     */ 
/*     */   public boolean isInitializationMethod()
/*     */   {
/* 304 */     return false;
/*     */   }
/*     */ 
/*     */   public boolean isMethod()
/*     */   {
/* 309 */     return false;
/*     */   }
/*     */ 
/*     */   public boolean isNative()
/*     */   {
/* 314 */     if (this.binding != null)
/* 315 */       return this.binding.isNative();
/* 316 */     return (this.modifiers & 0x100) != 0;
/*     */   }
/*     */ 
/*     */   public boolean isStatic()
/*     */   {
/* 321 */     if (this.binding != null)
/* 322 */       return this.binding.isStatic();
/* 323 */     return (this.modifiers & 0x8) != 0;
/*     */   }
/*     */ 
/*     */   public abstract void parseStatements(Parser paramParser, CompilationUnitDeclaration paramCompilationUnitDeclaration);
/*     */ 
/*     */   public StringBuffer print(int tab, StringBuffer output)
/*     */   {
/* 335 */     if (this.javadoc != null) {
/* 336 */       this.javadoc.print(tab, output);
/*     */     }
/* 338 */     printIndent(tab, output);
/* 339 */     printModifiers(this.modifiers, output);
/* 340 */     if (this.annotations != null) printAnnotations(this.annotations, output);
/*     */ 
/* 342 */     TypeParameter[] typeParams = typeParameters();
/* 343 */     if (typeParams != null) {
/* 344 */       output.append('<');
/* 345 */       int max = typeParams.length - 1;
/* 346 */       for (int j = 0; j < max; j++) {
/* 347 */         typeParams[j].print(0, output);
/* 348 */         output.append(", ");
/*     */       }
/* 350 */       typeParams[max].print(0, output);
/* 351 */       output.append('>');
/*     */     }
/*     */ 
/* 354 */     printReturnType(0, output).append(this.selector).append('(');
/* 355 */     if (this.arguments != null) {
/* 356 */       for (int i = 0; i < this.arguments.length; i++) {
/* 357 */         if (i > 0) output.append(", ");
/* 358 */         this.arguments[i].print(0, output);
/*     */       }
/*     */     }
/* 361 */     output.append(')');
/* 362 */     if (this.thrownExceptions != null) {
/* 363 */       output.append(" throws ");
/* 364 */       for (int i = 0; i < this.thrownExceptions.length; i++) {
/* 365 */         if (i > 0) output.append(", ");
/* 366 */         this.thrownExceptions[i].print(0, output);
/*     */       }
/*     */     }
/* 369 */     printBody(tab + 1, output);
/* 370 */     return output;
/*     */   }
/*     */ 
/*     */   public StringBuffer printBody(int indent, StringBuffer output)
/*     */   {
/* 375 */     if ((isAbstract()) || ((this.modifiers & 0x1000000) != 0)) {
/* 376 */       return output.append(';');
/*     */     }
/* 378 */     output.append(" {");
/* 379 */     if (this.statements != null) {
/* 380 */       for (int i = 0; i < this.statements.length; i++) {
/* 381 */         output.append('\n');
/* 382 */         this.statements[i].printStatement(indent, output);
/*     */       }
/*     */     }
/* 385 */     output.append('\n');
/* 386 */     printIndent(indent == 0 ? 0 : indent - 1, output).append('}');
/* 387 */     return output;
/*     */   }
/*     */ 
/*     */   public StringBuffer printReturnType(int indent, StringBuffer output)
/*     */   {
/* 392 */     return output;
/*     */   }
/*     */ 
/*     */   public void resolve(ClassScope upperScope)
/*     */   {
/* 397 */     if (this.binding == null) {
/* 398 */       this.ignoreFurtherInvestigation = true;
/*     */     }
/*     */     try
/*     */     {
/* 402 */       bindArguments();
/* 403 */       bindThrownExceptions();
/* 404 */       resolveJavadoc();
/* 405 */       resolveAnnotations(this.scope, this.annotations, this.binding);
/* 406 */       resolveStatements();
/*     */ 
/* 408 */       if ((this.binding != null) && 
/* 409 */         ((this.binding.getAnnotationTagBits() & 0x0) == 0L) && 
/* 410 */         ((this.binding.modifiers & 0x100000) != 0) && 
/* 411 */         (this.scope.compilerOptions().sourceLevel >= 3211264L))
/* 412 */         this.scope.problemReporter().missingDeprecatedAnnotationForMethod(this);
/*     */     }
/*     */     catch (AbortMethod localAbortMethod)
/*     */     {
/* 416 */       this.ignoreFurtherInvestigation = true;
/*     */     }
/*     */   }
/*     */ 
/*     */   public void resolveJavadoc()
/*     */   {
/* 422 */     if (this.binding == null) return;
/* 423 */     if (this.javadoc != null) {
/* 424 */       this.javadoc.resolve(this.scope);
/* 425 */       return;
/*     */     }
/* 427 */     if ((this.binding.declaringClass != null) && (!this.binding.declaringClass.isLocalType()))
/*     */     {
/* 429 */       int javadocVisibility = this.binding.modifiers & 0x7;
/* 430 */       ClassScope classScope = this.scope.classScope();
/* 431 */       ProblemReporter reporter = this.scope.problemReporter();
/* 432 */       int severity = reporter.computeSeverity(-1610612250);
/* 433 */       if (severity != -1) {
/* 434 */         if (classScope != null) {
/* 435 */           javadocVisibility = Util.computeOuterMostVisibility(classScope.referenceType(), javadocVisibility);
/*     */         }
/* 437 */         int javadocModifiers = this.binding.modifiers & 0xFFFFFFF8 | javadocVisibility;
/* 438 */         reporter.javadocMissing(this.sourceStart, this.sourceEnd, severity, javadocModifiers);
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public void resolveStatements()
/*     */   {
/* 445 */     if (this.statements != null) {
/* 446 */       int i = 0; for (int length = this.statements.length; i < length; i++)
/* 447 */         this.statements[i].resolve(this.scope);
/*     */     }
/* 449 */     else if ((this.bits & 0x8) != 0) {
/* 450 */       this.scope.problemReporter().undocumentedEmptyBlock(this.bodyStart - 1, this.bodyEnd + 1);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void tagAsHavingErrors() {
/* 455 */     this.ignoreFurtherInvestigation = true;
/*     */   }
/*     */ 
/*     */   public void traverse(ASTVisitor visitor, ClassScope classScope)
/*     */   {
/*     */   }
/*     */ 
/*     */   public TypeParameter[] typeParameters()
/*     */   {
/* 465 */     return null;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\ecj-3.5.2.jar
 * Qualified Name:     org.eclipse.jdt.internal.compiler.ast.AbstractMethodDeclaration
 * JD-Core Version:    0.6.0
 */