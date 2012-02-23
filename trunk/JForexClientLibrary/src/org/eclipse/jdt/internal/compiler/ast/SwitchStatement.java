/*     */ package org.eclipse.jdt.internal.compiler.ast;
/*     */ 
/*     */ import org.eclipse.jdt.internal.compiler.ASTVisitor;
/*     */ import org.eclipse.jdt.internal.compiler.codegen.BranchLabel;
/*     */ import org.eclipse.jdt.internal.compiler.codegen.CaseLabel;
/*     */ import org.eclipse.jdt.internal.compiler.codegen.CodeStream;
/*     */ import org.eclipse.jdt.internal.compiler.flow.FlowContext;
/*     */ import org.eclipse.jdt.internal.compiler.flow.FlowInfo;
/*     */ import org.eclipse.jdt.internal.compiler.flow.SwitchFlowContext;
/*     */ import org.eclipse.jdt.internal.compiler.impl.CompilerOptions;
/*     */ import org.eclipse.jdt.internal.compiler.impl.Constant;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.BlockScope;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.ClassScope;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.FieldBinding;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.MethodScope;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.ReferenceBinding;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.SourceTypeBinding;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.SyntheticMethodBinding;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;
/*     */ import org.eclipse.jdt.internal.compiler.problem.ProblemReporter;
/*     */ 
/*     */ public class SwitchStatement extends Statement
/*     */ {
/*     */   public Expression expression;
/*     */   public Statement[] statements;
/*     */   public BlockScope scope;
/*     */   public int explicitDeclarations;
/*     */   public BranchLabel breakLabel;
/*     */   public CaseStatement[] cases;
/*     */   public CaseStatement defaultCase;
/*     */   public int blockStart;
/*     */   public int caseCount;
/*     */   int[] constants;
/*     */   public static final int CASE = 0;
/*     */   public static final int FALLTHROUGH = 1;
/*     */   public static final int ESCAPING = 2;
/*     */   public SyntheticMethodBinding synthetic;
/*  44 */   int preSwitchInitStateIndex = -1;
/*  45 */   int mergedInitStateIndex = -1;
/*     */ 
/*     */   public FlowInfo analyseCode(BlockScope currentScope, FlowContext flowContext, FlowInfo flowInfo) {
/*     */     try {
/*  49 */       flowInfo = this.expression.analyseCode(currentScope, flowContext, flowInfo);
/*  50 */       SwitchFlowContext switchContext = 
/*  51 */         new SwitchFlowContext(flowContext, this, this.breakLabel = new BranchLabel());
/*     */ 
/*  55 */       FlowInfo caseInits = FlowInfo.DEAD_END;
/*     */ 
/*  57 */       this.preSwitchInitStateIndex = currentScope.methodScope().recordInitializationStates(flowInfo);
/*  58 */       int caseIndex = 0;
/*  59 */       if (this.statements != null) {
/*  60 */         int initialComplaintLevel = (flowInfo.reachMode() & 0x1) != 0 ? 1 : 0;
/*  61 */         int complaintLevel = initialComplaintLevel;
/*  62 */         int fallThroughState = 0;
/*  63 */         int i = 0; for (int max = this.statements.length; i < max; i++) {
/*  64 */           Statement statement = this.statements[i];
/*  65 */           if ((caseIndex < this.caseCount) && (statement == this.cases[caseIndex])) {
/*  66 */             this.scope.enclosingCase = this.cases[caseIndex];
/*  67 */             caseIndex++;
/*  68 */             if ((fallThroughState == 1) && 
/*  69 */               ((statement.bits & 0x20000000) == 0)) {
/*  70 */               this.scope.problemReporter().possibleFallThroughCase(this.scope.enclosingCase);
/*     */             }
/*  72 */             caseInits = caseInits.mergedWith(flowInfo.unconditionalInits());
/*  73 */             complaintLevel = initialComplaintLevel;
/*  74 */             fallThroughState = 0;
/*  75 */           } else if (statement == this.defaultCase) {
/*  76 */             this.scope.enclosingCase = this.defaultCase;
/*  77 */             if ((fallThroughState == 1) && 
/*  78 */               ((statement.bits & 0x20000000) == 0)) {
/*  79 */               this.scope.problemReporter().possibleFallThroughCase(this.scope.enclosingCase);
/*     */             }
/*  81 */             caseInits = caseInits.mergedWith(flowInfo.unconditionalInits());
/*  82 */             complaintLevel = initialComplaintLevel;
/*  83 */             fallThroughState = 0;
/*     */           } else {
/*  85 */             fallThroughState = 1;
/*     */           }
/*  87 */           if ((complaintLevel = statement.complainIfUnreachable(caseInits, this.scope, complaintLevel)) < 2) {
/*  88 */             caseInits = statement.analyseCode(this.scope, switchContext, caseInits);
/*  89 */             if (caseInits == FlowInfo.DEAD_END) {
/*  90 */               fallThroughState = 2;
/*     */             }
/*     */           }
/*     */         }
/*     */       }
/*     */ 
/*  96 */       TypeBinding resolvedTypeBinding = this.expression.resolvedType;
/*  97 */       if ((this.caseCount > 0) && (resolvedTypeBinding.isEnum())) {
/*  98 */         SourceTypeBinding sourceTypeBinding = this.scope.classScope().referenceContext.binding;
/*  99 */         this.synthetic = sourceTypeBinding.addSyntheticMethodForSwitchEnum(resolvedTypeBinding);
/*     */       }
/*     */ 
/* 102 */       if (this.defaultCase == null)
/*     */       {
/* 104 */         flowInfo.addPotentialInitializationsFrom(caseInits.mergedWith(switchContext.initsOnBreak));
/* 105 */         this.mergedInitStateIndex = currentScope.methodScope().recordInitializationStates(flowInfo);
/* 106 */         localFlowInfo1 = flowInfo;
/*     */         return localFlowInfo1;
/*     */       }
/*     */       TypeBinding resolvedTypeBinding;
/*     */       int caseIndex;
/*     */       FlowInfo caseInits;
/*     */       SwitchFlowContext switchContext;
/* 110 */       FlowInfo mergedInfo = caseInits.mergedWith(switchContext.initsOnBreak);
/* 111 */       this.mergedInitStateIndex = 
/* 112 */         currentScope.methodScope().recordInitializationStates(mergedInfo);
/* 113 */       FlowInfo localFlowInfo1 = mergedInfo;
/*     */       return localFlowInfo1;
/*     */     } finally {
/* 115 */       if (this.scope != null) this.scope.enclosingCase = null; 
/*     */     }
/* 116 */     throw localObject;
/*     */   }
/*     */ 
/*     */   public void generateCode(BlockScope currentScope, CodeStream codeStream)
/*     */   {
/*     */     try
/*     */     {
/* 128 */       if ((this.bits & 0x80000000) == 0)
/*     */         return;
/* 131 */       int pc = codeStream.position;
/*     */ 
/* 134 */       this.breakLabel.initialize(codeStream);
/* 135 */       CaseLabel[] caseLabels = new CaseLabel[this.caseCount];
/* 136 */       boolean needSwitch = this.caseCount != 0;
/* 137 */       for (int i = 0; i < this.caseCount; i++) {
/* 138 */         this.cases[i].targetLabel = (caseLabels[i] =  = new CaseLabel(codeStream));
/* 139 */         caseLabels[i].tagBits |= 2;
/*     */       }
/* 141 */       CaseLabel defaultLabel = new CaseLabel(codeStream);
/* 142 */       if (needSwitch) defaultLabel.tagBits |= 2;
/* 143 */       if (this.defaultCase != null) {
/* 144 */         this.defaultCase.targetLabel = defaultLabel;
/*     */       }
/*     */ 
/* 147 */       TypeBinding resolvedType = this.expression.resolvedType;
/* 148 */       if (resolvedType.isEnum()) {
/* 149 */         if (needSwitch)
/*     */         {
/* 151 */           codeStream.invoke(-72, this.synthetic, null);
/* 152 */           this.expression.generateCode(currentScope, codeStream, true);
/*     */ 
/* 154 */           codeStream.invokeEnumOrdinal(resolvedType.constantPoolName());
/* 155 */           codeStream.iaload();
/*     */         }
/*     */         else {
/* 158 */           this.expression.generateCode(currentScope, codeStream, false);
/*     */         }
/*     */       }
/*     */       else {
/* 162 */         this.expression.generateCode(currentScope, codeStream, needSwitch);
/*     */       }
/*     */ 
/* 165 */       if (needSwitch) {
/* 166 */         int[] sortedIndexes = new int[this.caseCount];
/*     */ 
/* 168 */         for (int i = 0; i < this.caseCount; i++)
/* 169 */           sortedIndexes[i] = i;
/*     */         int[] localKeysCopy;
/* 172 */         System.arraycopy(this.constants, 0, localKeysCopy = new int[this.caseCount], 0, this.caseCount);
/* 173 */         CodeStream.sort(localKeysCopy, 0, this.caseCount - 1, sortedIndexes);
/*     */ 
/* 175 */         int max = localKeysCopy[(this.caseCount - 1)];
/* 176 */         int min = localKeysCopy[0];
/* 177 */         if (()(this.caseCount * 2.5D) > max - min)
/*     */         {
/* 181 */           if ((max > 2147418112) && (currentScope.compilerOptions().complianceLevel < 3145728L)) {
/* 182 */             codeStream.lookupswitch(defaultLabel, this.constants, sortedIndexes, caseLabels);
/*     */           }
/*     */           else
/* 185 */             codeStream.tableswitch(
/* 186 */               defaultLabel, 
/* 187 */               min, 
/* 188 */               max, 
/* 189 */               this.constants, 
/* 190 */               sortedIndexes, 
/* 191 */               caseLabels);
/*     */         }
/*     */         else {
/* 194 */           codeStream.lookupswitch(defaultLabel, this.constants, sortedIndexes, caseLabels);
/*     */         }
/* 196 */         codeStream.updateLastRecordedEndPC(this.scope, codeStream.position);
/*     */       }
/*     */ 
/* 200 */       int caseIndex = 0;
/* 201 */       if (this.statements != null) {
/* 202 */         int i = 0; for (int maxCases = this.statements.length; i < maxCases; i++) {
/* 203 */           Statement statement = this.statements[i];
/* 204 */           if ((caseIndex < this.caseCount) && (statement == this.cases[caseIndex])) {
/* 205 */             this.scope.enclosingCase = this.cases[caseIndex];
/* 206 */             if (this.preSwitchInitStateIndex != -1) {
/* 207 */               codeStream.removeNotDefinitelyAssignedVariables(currentScope, this.preSwitchInitStateIndex);
/*     */             }
/* 209 */             caseIndex++;
/*     */           }
/* 211 */           else if (statement == this.defaultCase) {
/* 212 */             this.scope.enclosingCase = this.defaultCase;
/* 213 */             if (this.preSwitchInitStateIndex != -1) {
/* 214 */               codeStream.removeNotDefinitelyAssignedVariables(currentScope, this.preSwitchInitStateIndex);
/*     */             }
/*     */           }
/*     */ 
/* 218 */           statement.generateCode(this.scope, codeStream);
/*     */         }
/*     */       }
/*     */ 
/* 222 */       if (this.mergedInitStateIndex != -1) {
/* 223 */         codeStream.removeNotDefinitelyAssignedVariables(currentScope, this.mergedInitStateIndex);
/* 224 */         codeStream.addDefinitelyAssignedVariables(currentScope, this.mergedInitStateIndex);
/*     */       }
/* 226 */       if (this.scope != currentScope) {
/* 227 */         codeStream.exitUserScope(this.scope);
/*     */       }
/*     */ 
/* 230 */       this.breakLabel.place();
/* 231 */       if (this.defaultCase == null)
/*     */       {
/* 233 */         codeStream.recordPositionsFrom(codeStream.position, this.sourceEnd, true);
/* 234 */         defaultLabel.place();
/*     */       }
/* 236 */       codeStream.recordPositionsFrom(pc, this.sourceStart);
/*     */     } finally {
/* 238 */       if (this.scope != null) this.scope.enclosingCase = null; 
/* 238 */     }if (this.scope != null) this.scope.enclosingCase = null;
/*     */   }
/*     */ 
/*     */   public StringBuffer printStatement(int indent, StringBuffer output)
/*     */   {
/* 244 */     printIndent(indent, output).append("switch (");
/* 245 */     this.expression.printExpression(0, output).append(") {");
/* 246 */     if (this.statements != null) {
/* 247 */       for (int i = 0; i < this.statements.length; i++) {
/* 248 */         output.append('\n');
/* 249 */         if ((this.statements[i] instanceof CaseStatement))
/* 250 */           this.statements[i].printStatement(indent, output);
/*     */         else {
/* 252 */           this.statements[i].printStatement(indent + 2, output);
/*     */         }
/*     */       }
/*     */     }
/* 256 */     output.append("\n");
/* 257 */     return printIndent(indent, output).append('}');
/*     */   }
/*     */ 
/*     */   public void resolve(BlockScope upperScope)
/*     */   {
/* 263 */     label657: 
/*     */     try { boolean isEnumSwitch = false;
/* 264 */       TypeBinding expressionType = this.expression.resolveType(upperScope);
/* 265 */       if (expressionType != null) {
/* 266 */         this.expression.computeConversion(upperScope, expressionType, expressionType);
/*     */ 
/* 268 */         if (!expressionType.isValidBinding()) {
/* 269 */           expressionType = null;
/*     */         } else {
/* 271 */           if (expressionType.isBaseType()) {
/* 272 */             if (this.expression.isConstantValueOfTypeAssignableToType(expressionType, TypeBinding.INT)) break label126;
/* 274 */             if (expressionType.isCompatibleWith(TypeBinding.INT))
/* 275 */               break label126; 
/*     */           } else {
/* 276 */             if (expressionType.isEnum()) {
/* 277 */               isEnumSwitch = true;
/* 278 */               break label126;
/* 279 */             }if (upperScope.isBoxingCompatibleWith(expressionType, TypeBinding.INT)) {
/* 280 */               this.expression.computeConversion(upperScope, TypeBinding.INT, expressionType);
/* 281 */               break label126;
/*     */             }
/*     */           }
/* 283 */           upperScope.problemReporter().incorrectSwitchType(this.expression, expressionType);
/* 284 */           expressionType = null;
/*     */         }
/*     */       }
/* 287 */       label126: if (this.statements != null) {
/* 288 */         this.scope = new BlockScope(upperScope);
/*     */         int length;
/* 291 */         this.cases = new CaseStatement[length = this.statements.length];
/* 292 */         this.constants = new int[length];
/* 293 */         CaseStatement[] duplicateCaseStatements = (CaseStatement[])null;
/* 294 */         int duplicateCaseStatementsCounter = 0;
/* 295 */         int counter = 0;
/* 296 */         for (int i = 0; i < length; i++)
/*     */         {
/* 298 */           Statement statement = this.statements[i];
/*     */           Constant constant;
/* 299 */           if ((constant = statement.resolveCase(this.scope, expressionType, this)) != Constant.NotAConstant) {
/* 300 */             int key = constant.intValue();
/*     */ 
/* 302 */             for (int j = 0; j < counter; j++) {
/* 303 */               if (this.constants[j] == key) {
/* 304 */                 CaseStatement currentCaseStatement = (CaseStatement)statement;
/* 305 */                 if (duplicateCaseStatements == null) {
/* 306 */                   this.scope.problemReporter().duplicateCase(this.cases[j]);
/* 307 */                   this.scope.problemReporter().duplicateCase(currentCaseStatement);
/* 308 */                   duplicateCaseStatements = new CaseStatement[length];
/* 309 */                   duplicateCaseStatements[(duplicateCaseStatementsCounter++)] = this.cases[j];
/* 310 */                   duplicateCaseStatements[(duplicateCaseStatementsCounter++)] = currentCaseStatement;
/*     */                 } else {
/* 312 */                   boolean found = false;
/* 313 */                   for (int k = 2; k < duplicateCaseStatementsCounter; k++) {
/* 314 */                     if (duplicateCaseStatements[k] == statement) {
/* 315 */                       found = true;
/* 316 */                       break;
/*     */                     }
/*     */                   }
/* 319 */                   if (!found) {
/* 320 */                     this.scope.problemReporter().duplicateCase(currentCaseStatement);
/* 321 */                     duplicateCaseStatements[(duplicateCaseStatementsCounter++)] = currentCaseStatement;
/*     */                   }
/*     */                 }
/*     */               }
/*     */             }
/* 326 */             this.constants[(counter++)] = key;
/*     */           }
/*     */         }
/* 329 */         if (length != counter) {
/* 330 */           System.arraycopy(this.constants, 0, this.constants = new int[counter], 0, counter);
/*     */         }
/*     */       }
/* 333 */       else if ((this.bits & 0x8) != 0) {
/* 334 */         upperScope.problemReporter().undocumentedEmptyBlock(this.blockStart, this.sourceEnd);
/*     */       }
/*     */ 
/* 338 */       if ((isEnumSwitch) && (this.defaultCase == null)) {
/* 339 */         if (upperScope.compilerOptions().getSeverity(536875008) == -1) break label657; int constantCount = this.constants == null ? 0 : this.constants.length;
/* 341 */         if ((constantCount != this.caseCount) || 
/* 342 */           (this.caseCount == ((ReferenceBinding)expressionType).enumConstantCount())) break label657; FieldBinding[] enumFields = ((ReferenceBinding)expressionType.erasure()).fields();
/* 344 */         int i = 0; for (int max = enumFields.length; i < max; i++) {
/* 345 */           FieldBinding enumConstant = enumFields[i];
/* 346 */           if ((enumConstant.modifiers & 0x4000) == 0)
/*     */             continue;
/* 348 */           int j = 0;
/* 349 */           while (enumConstant.id + 1 != this.constants[j])
/*     */           {
/* 348 */             j++; if (j < this.caseCount)
/*     */             {
/*     */               continue;
/*     */             }
/*     */ 
/* 353 */             upperScope.problemReporter().missingEnumConstantCase(this, enumConstant);
/*     */           }
/*     */         }
/*     */       }
/*     */     } finally
/*     */     {
/* 359 */       if (this.scope != null) this.scope.enclosingCase = null;
/*     */     }
/*     */   }
/*     */ 
/*     */   public void traverse(ASTVisitor visitor, BlockScope blockScope)
/*     */   {
/* 367 */     if (visitor.visit(this, blockScope)) {
/* 368 */       this.expression.traverse(visitor, blockScope);
/* 369 */       if (this.statements != null) {
/* 370 */         int statementsLength = this.statements.length;
/* 371 */         for (int i = 0; i < statementsLength; i++)
/* 372 */           this.statements[i].traverse(visitor, this.scope);
/*     */       }
/*     */     }
/* 375 */     visitor.endVisit(this, blockScope);
/*     */   }
/*     */ 
/*     */   public void branchChainTo(BranchLabel label)
/*     */   {
/* 388 */     if (this.breakLabel.forwardReferenceCount() > 0)
/* 389 */       label.becomeDelegateFor(this.breakLabel);
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\ecj-3.5.2.jar
 * Qualified Name:     org.eclipse.jdt.internal.compiler.ast.SwitchStatement
 * JD-Core Version:    0.6.0
 */