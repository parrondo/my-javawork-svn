/*     */ package org.eclipse.jdt.internal.compiler.ast;
/*     */ 
/*     */ import org.eclipse.jdt.internal.compiler.codegen.BranchLabel;
/*     */ import org.eclipse.jdt.internal.compiler.codegen.CodeStream;
/*     */ import org.eclipse.jdt.internal.compiler.flow.FlowContext;
/*     */ import org.eclipse.jdt.internal.compiler.flow.FlowInfo;
/*     */ import org.eclipse.jdt.internal.compiler.impl.CompilerOptions;
/*     */ import org.eclipse.jdt.internal.compiler.impl.Constant;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.ArrayBinding;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.BlockScope;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.LookupEnvironment;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.MethodBinding;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.Scope;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;
/*     */ import org.eclipse.jdt.internal.compiler.problem.ProblemReporter;
/*     */ 
/*     */ public abstract class Statement extends ASTNode
/*     */ {
/*     */   public static final int NOT_COMPLAINED = 0;
/*     */   public static final int COMPLAINED_FAKE_REACHABLE = 1;
/*     */   public static final int COMPLAINED_UNREACHABLE = 2;
/*     */ 
/*     */   public abstract FlowInfo analyseCode(BlockScope paramBlockScope, FlowContext paramFlowContext, FlowInfo paramFlowInfo);
/*     */ 
/*     */   public void branchChainTo(BranchLabel label)
/*     */   {
/*     */   }
/*     */ 
/*     */   public int complainIfUnreachable(FlowInfo flowInfo, BlockScope scope, int previousComplaintLevel)
/*     */   {
/*  37 */     if ((flowInfo.reachMode() & 0x1) != 0) {
/*  38 */       this.bits &= 2147483647;
/*  39 */       if (flowInfo == FlowInfo.DEAD_END) {
/*  40 */         if (previousComplaintLevel < 2) {
/*  41 */           scope.problemReporter().unreachableCode(this);
/*     */         }
/*  43 */         return 2;
/*     */       }
/*  45 */       if (previousComplaintLevel < 1) {
/*  46 */         scope.problemReporter().fakeReachable(this);
/*     */       }
/*  48 */       return 1;
/*     */     }
/*     */ 
/*  51 */     return previousComplaintLevel;
/*     */   }
/*     */ 
/*     */   public void generateArguments(MethodBinding binding, Expression[] arguments, BlockScope currentScope, CodeStream codeStream)
/*     */   {
/*  58 */     if (binding.isVarargs())
/*     */     {
/*  61 */       TypeBinding[] params = binding.parameters;
/*  62 */       int paramLength = params.length;
/*  63 */       int varArgIndex = paramLength - 1;
/*  64 */       for (int i = 0; i < varArgIndex; i++) {
/*  65 */         arguments[i].generateCode(currentScope, codeStream, true);
/*     */       }
/*     */ 
/*  68 */       ArrayBinding varArgsType = (ArrayBinding)params[varArgIndex];
/*  69 */       ArrayBinding codeGenVarArgsType = (ArrayBinding)binding.parameters[varArgIndex].erasure();
/*  70 */       int elementsTypeID = varArgsType.elementsType().id;
/*  71 */       int argLength = arguments == null ? 0 : arguments.length;
/*     */ 
/*  73 */       if (argLength > paramLength)
/*     */       {
/*  77 */         codeStream.generateInlinedValue(argLength - varArgIndex);
/*  78 */         codeStream.newArray(codeGenVarArgsType);
/*  79 */         for (int i = varArgIndex; i < argLength; i++) {
/*  80 */           codeStream.dup();
/*  81 */           codeStream.generateInlinedValue(i - varArgIndex);
/*  82 */           arguments[i].generateCode(currentScope, codeStream, true);
/*  83 */           codeStream.arrayAtPut(elementsTypeID, false);
/*     */         }
/*  85 */       } else if (argLength == paramLength)
/*     */       {
/*  87 */         TypeBinding lastType = arguments[varArgIndex].resolvedType;
/*  88 */         if ((lastType == TypeBinding.NULL) || (
/*  89 */           (varArgsType.dimensions() == lastType.dimensions()) && 
/*  90 */           (lastType.isCompatibleWith(varArgsType))))
/*     */         {
/*  92 */           arguments[varArgIndex].generateCode(currentScope, codeStream, true);
/*     */         }
/*     */         else
/*     */         {
/*  96 */           codeStream.generateInlinedValue(1);
/*  97 */           codeStream.newArray(codeGenVarArgsType);
/*  98 */           codeStream.dup();
/*  99 */           codeStream.generateInlinedValue(0);
/* 100 */           arguments[varArgIndex].generateCode(currentScope, codeStream, true);
/* 101 */           codeStream.arrayAtPut(elementsTypeID, false);
/*     */         }
/*     */       }
/*     */       else
/*     */       {
/* 106 */         codeStream.generateInlinedValue(0);
/* 107 */         codeStream.newArray(codeGenVarArgsType);
/*     */       }
/* 109 */     } else if (arguments != null) {
/* 110 */       int i = 0; for (int max = arguments.length; i < max; i++)
/* 111 */         arguments[i].generateCode(currentScope, codeStream, true); 
/*     */     }
/*     */   }
/*     */ 
/*     */   public abstract void generateCode(BlockScope paramBlockScope, CodeStream paramCodeStream);
/*     */ 
/*     */   protected boolean isBoxingCompatible(TypeBinding expressionType, TypeBinding targetType, Expression expression, Scope scope) {
/* 118 */     if (scope.isBoxingCompatibleWith(expressionType, targetType)) {
/* 119 */       return true;
/*     */     }
/*     */ 
/* 125 */     return (expressionType.isBaseType()) && 
/* 122 */       (!targetType.isBaseType()) && 
/* 123 */       (!targetType.isTypeVariable()) && 
/* 124 */       (scope.compilerOptions().sourceLevel >= 3211264L) && 
/* 125 */       (expression.isConstantValueOfTypeAssignableToType(expressionType, scope.environment().computeBoxingType(targetType)));
/*     */   }
/*     */ 
/*     */   public boolean isEmptyBlock() {
/* 129 */     return false;
/*     */   }
/*     */ 
/*     */   public boolean isValidJavaStatement()
/*     */   {
/* 144 */     return true;
/*     */   }
/*     */ 
/*     */   public StringBuffer print(int indent, StringBuffer output) {
/* 148 */     return printStatement(indent, output);
/*     */   }
/*     */ 
/*     */   public abstract StringBuffer printStatement(int paramInt, StringBuffer paramStringBuffer);
/*     */ 
/*     */   public abstract void resolve(BlockScope paramBlockScope);
/*     */ 
/*     */   public Constant resolveCase(BlockScope scope, TypeBinding testType, SwitchStatement switchStatement)
/*     */   {
/* 160 */     resolve(scope);
/* 161 */     return Constant.NotAConstant;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\ecj-3.5.2.jar
 * Qualified Name:     org.eclipse.jdt.internal.compiler.ast.Statement
 * JD-Core Version:    0.6.0
 */