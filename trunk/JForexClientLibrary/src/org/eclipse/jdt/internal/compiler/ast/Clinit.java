/*     */ package org.eclipse.jdt.internal.compiler.ast;
/*     */ 
/*     */ import org.eclipse.jdt.internal.compiler.ASTVisitor;
/*     */ import org.eclipse.jdt.internal.compiler.ClassFile;
/*     */ import org.eclipse.jdt.internal.compiler.CompilationResult;
/*     */ import org.eclipse.jdt.internal.compiler.codegen.BranchLabel;
/*     */ import org.eclipse.jdt.internal.compiler.codegen.CodeStream;
/*     */ import org.eclipse.jdt.internal.compiler.codegen.ConstantPool;
/*     */ import org.eclipse.jdt.internal.compiler.flow.ExceptionHandlingFlowContext;
/*     */ import org.eclipse.jdt.internal.compiler.flow.FlowInfo;
/*     */ import org.eclipse.jdt.internal.compiler.flow.InitializationFlowContext;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.Binding;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.BlockScope;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.ClassScope;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.FieldBinding;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.MethodScope;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.SourceTypeBinding;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.TypeConstants;
/*     */ import org.eclipse.jdt.internal.compiler.parser.Parser;
/*     */ import org.eclipse.jdt.internal.compiler.problem.AbortMethod;
/*     */ import org.eclipse.jdt.internal.compiler.problem.ProblemReporter;
/*     */ 
/*     */ public class Clinit extends AbstractMethodDeclaration
/*     */ {
/*  35 */   private FieldBinding assertionSyntheticFieldBinding = null;
/*  36 */   private FieldBinding classLiteralSyntheticField = null;
/*     */ 
/*     */   public Clinit(CompilationResult compilationResult) {
/*  39 */     super(compilationResult);
/*  40 */     this.modifiers = 0;
/*  41 */     this.selector = TypeConstants.CLINIT;
/*     */   }
/*     */ 
/*     */   public void analyseCode(ClassScope classScope, InitializationFlowContext staticInitializerFlowContext, FlowInfo flowInfo)
/*     */   {
/*  49 */     if (this.ignoreFurtherInvestigation)
/*  50 */       return;
/*     */     try {
/*  52 */       ExceptionHandlingFlowContext clinitContext = 
/*  53 */         new ExceptionHandlingFlowContext(
/*  54 */         staticInitializerFlowContext.parent, 
/*  55 */         this, 
/*  56 */         Binding.NO_EXCEPTIONS, 
/*  57 */         staticInitializerFlowContext, 
/*  58 */         this.scope, 
/*  59 */         FlowInfo.DEAD_END);
/*     */ 
/*  62 */       if ((flowInfo.tagBits & 0x1) == 0) {
/*  63 */         this.bits |= 64;
/*     */       }
/*     */ 
/*  67 */       flowInfo = flowInfo.mergedWith(staticInitializerFlowContext.initsOnReturn);
/*  68 */       FieldBinding[] fields = this.scope.enclosingSourceType().fields();
/*  69 */       int i = 0; for (int count = fields.length; i < count; i++)
/*     */       {
/*     */         FieldBinding field;
/*  71 */         if ((!(field = fields[i]).isStatic()) || 
/*  72 */           (!field.isFinal()) || 
/*  73 */           (flowInfo.isDefinitelyAssigned(fields[i]))) continue;
/*  74 */         this.scope.problemReporter().uninitializedBlankFinalField(
/*  75 */           field, 
/*  76 */           this.scope.referenceType().declarationOf(field.original()));
/*     */       }
/*     */ 
/*  81 */       staticInitializerFlowContext.checkInitializerExceptions(
/*  82 */         this.scope, 
/*  83 */         clinitContext, 
/*  84 */         flowInfo);
/*     */     } catch (AbortMethod localAbortMethod) {
/*  86 */       this.ignoreFurtherInvestigation = true;
/*     */     }
/*     */   }
/*     */ 
/*     */   public void generateCode(ClassScope classScope, ClassFile classFile)
/*     */   {
/*  98 */     int clinitOffset = 0;
/*  99 */     if (this.ignoreFurtherInvestigation)
/*     */     {
/* 101 */       return;
/*     */     }
/*     */     try {
/* 104 */       clinitOffset = classFile.contentsOffset;
/* 105 */       generateCode(classScope, classFile, clinitOffset);
/*     */     }
/*     */     catch (AbortMethod e)
/*     */     {
/* 114 */       if (e.compilationResult == CodeStream.RESTART_IN_WIDE_MODE)
/*     */       {
/*     */         try {
/* 117 */           classFile.contentsOffset = clinitOffset;
/* 118 */           classFile.methodCount -= 1;
/* 119 */           classFile.codeStream.resetInWideMode();
/* 120 */           generateCode(classScope, classFile, clinitOffset);
/*     */         }
/*     */         catch (AbortMethod localAbortMethod1) {
/* 123 */           classFile.contentsOffset = clinitOffset;
/* 124 */           classFile.methodCount -= 1;
/*     */         }
/*     */       }
/*     */       else {
/* 128 */         classFile.contentsOffset = clinitOffset;
/* 129 */         classFile.methodCount -= 1;
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   private void generateCode(ClassScope classScope, ClassFile classFile, int clinitOffset)
/*     */   {
/* 145 */     ConstantPool constantPool = classFile.constantPool;
/* 146 */     int constantPoolOffset = constantPool.currentOffset;
/* 147 */     int constantPoolIndex = constantPool.currentIndex;
/* 148 */     classFile.generateMethodInfoHeaderForClinit();
/* 149 */     int codeAttributeOffset = classFile.contentsOffset;
/* 150 */     classFile.generateCodeAttributeHeader();
/* 151 */     CodeStream codeStream = classFile.codeStream;
/* 152 */     resolve(classScope);
/*     */ 
/* 154 */     codeStream.reset(this, classFile);
/* 155 */     TypeDeclaration declaringType = classScope.referenceContext;
/*     */ 
/* 158 */     MethodScope staticInitializerScope = declaringType.staticInitializerScope;
/* 159 */     staticInitializerScope.computeLocalVariablePositions(0, codeStream);
/*     */ 
/* 163 */     if (this.assertionSyntheticFieldBinding != null)
/*     */     {
/* 165 */       codeStream.generateClassLiteralAccessForType(
/* 166 */         classScope.outerMostClassScope().enclosingSourceType(), 
/* 167 */         this.classLiteralSyntheticField);
/* 168 */       codeStream.invokeJavaLangClassDesiredAssertionStatus();
/* 169 */       BranchLabel falseLabel = new BranchLabel(codeStream);
/* 170 */       codeStream.ifne(falseLabel);
/* 171 */       codeStream.iconst_1();
/* 172 */       BranchLabel jumpLabel = new BranchLabel(codeStream);
/* 173 */       codeStream.decrStackSize(1);
/* 174 */       codeStream.goto_(jumpLabel);
/* 175 */       falseLabel.place();
/* 176 */       codeStream.iconst_0();
/* 177 */       jumpLabel.place();
/* 178 */       codeStream.fieldAccess(-77, this.assertionSyntheticFieldBinding, null);
/*     */     }
/*     */ 
/* 181 */     FieldDeclaration[] fieldDeclarations = declaringType.fields;
/* 182 */     BlockScope lastInitializerScope = null;
/* 183 */     if (TypeDeclaration.kind(declaringType.modifiers) == 3) {
/* 184 */       int enumCount = 0;
/* 185 */       int remainingFieldCount = 0;
/* 186 */       if (fieldDeclarations != null) {
/* 187 */         int i = 0; for (int max = fieldDeclarations.length; i < max; i++) {
/* 188 */           FieldDeclaration fieldDecl = fieldDeclarations[i];
/* 189 */           if (fieldDecl.isStatic()) {
/* 190 */             if (fieldDecl.getKind() == 3) {
/* 191 */               fieldDecl.generateCode(staticInitializerScope, codeStream);
/* 192 */               enumCount++;
/*     */             } else {
/* 194 */               remainingFieldCount++;
/*     */             }
/*     */           }
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/* 201 */       codeStream.generateInlinedValue(enumCount);
/* 202 */       codeStream.anewarray(declaringType.binding);
/* 203 */       if ((enumCount > 0) && 
/* 204 */         (fieldDeclarations != null)) {
/* 205 */         int i = 0; for (int max = fieldDeclarations.length; i < max; i++) {
/* 206 */           FieldDeclaration fieldDecl = fieldDeclarations[i];
/*     */ 
/* 208 */           if (fieldDecl.getKind() == 3) {
/* 209 */             codeStream.dup();
/* 210 */             codeStream.generateInlinedValue(fieldDecl.binding.id);
/* 211 */             codeStream.fieldAccess(-78, fieldDecl.binding, null);
/* 212 */             codeStream.aastore();
/*     */           }
/*     */         }
/*     */       }
/*     */ 
/* 217 */       codeStream.fieldAccess(-77, declaringType.enumValuesSyntheticfield, null);
/* 218 */       if (remainingFieldCount != 0)
/*     */       {
/* 220 */         int i = 0; for (int max = fieldDeclarations.length; i < max; i++) {
/* 221 */           FieldDeclaration fieldDecl = fieldDeclarations[i];
/* 222 */           switch (fieldDecl.getKind()) {
/*     */           case 3:
/* 224 */             break;
/*     */           case 2:
/* 226 */             if (!fieldDecl.isStatic())
/*     */               continue;
/* 228 */             lastInitializerScope = ((Initializer)fieldDecl).block.scope;
/* 229 */             fieldDecl.generateCode(staticInitializerScope, codeStream);
/* 230 */             break;
/*     */           case 1:
/* 232 */             if (!fieldDecl.binding.isStatic())
/*     */               continue;
/* 234 */             lastInitializerScope = null;
/* 235 */             fieldDecl.generateCode(staticInitializerScope, codeStream);
/*     */           }
/*     */         }
/*     */       }
/*     */ 
/*     */     }
/* 241 */     else if (fieldDeclarations != null) {
/* 242 */       int i = 0; for (int max = fieldDeclarations.length; i < max; i++) {
/* 243 */         FieldDeclaration fieldDecl = fieldDeclarations[i];
/* 244 */         switch (fieldDecl.getKind()) {
/*     */         case 2:
/* 246 */           if (!fieldDecl.isStatic())
/*     */             continue;
/* 248 */           lastInitializerScope = ((Initializer)fieldDecl).block.scope;
/* 249 */           fieldDecl.generateCode(staticInitializerScope, codeStream);
/* 250 */           break;
/*     */         case 1:
/* 252 */           if (!fieldDecl.binding.isStatic())
/*     */             continue;
/* 254 */           lastInitializerScope = null;
/* 255 */           fieldDecl.generateCode(staticInitializerScope, codeStream);
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 262 */     if (codeStream.position == 0)
/*     */     {
/* 265 */       classFile.contentsOffset = clinitOffset;
/*     */ 
/* 267 */       classFile.methodCount -= 1;
/*     */ 
/* 269 */       constantPool.resetForClinit(constantPoolIndex, constantPoolOffset);
/*     */     } else {
/* 271 */       if ((this.bits & 0x40) != 0) {
/* 272 */         int before = codeStream.position;
/* 273 */         codeStream.return_();
/* 274 */         if (lastInitializerScope != null)
/*     */         {
/* 276 */           codeStream.updateLastRecordedEndPC(lastInitializerScope, before);
/*     */         }
/*     */       }
/*     */ 
/* 280 */       codeStream.recordPositionsFrom(0, declaringType.sourceStart);
/* 281 */       classFile.completeCodeAttributeForClinit(codeAttributeOffset);
/*     */     }
/*     */   }
/*     */ 
/*     */   public boolean isClinit()
/*     */   {
/* 287 */     return true;
/*     */   }
/*     */ 
/*     */   public boolean isInitializationMethod()
/*     */   {
/* 292 */     return true;
/*     */   }
/*     */ 
/*     */   public boolean isStatic()
/*     */   {
/* 297 */     return true;
/*     */   }
/*     */ 
/*     */   public void parseStatements(Parser parser, CompilationUnitDeclaration unit)
/*     */   {
/*     */   }
/*     */ 
/*     */   public StringBuffer print(int tab, StringBuffer output)
/*     */   {
/* 306 */     printIndent(tab, output).append("<clinit>()");
/* 307 */     printBody(tab + 1, output);
/* 308 */     return output;
/*     */   }
/*     */ 
/*     */   public void resolve(ClassScope classScope)
/*     */   {
/* 313 */     this.scope = new MethodScope(classScope, classScope.referenceContext, true);
/*     */   }
/*     */ 
/*     */   public void traverse(ASTVisitor visitor, ClassScope classScope)
/*     */   {
/* 320 */     visitor.visit(this, classScope);
/* 321 */     visitor.endVisit(this, classScope);
/*     */   }
/*     */ 
/*     */   public void setAssertionSupport(FieldBinding assertionSyntheticFieldBinding, boolean needClassLiteralField)
/*     */   {
/* 326 */     this.assertionSyntheticFieldBinding = assertionSyntheticFieldBinding;
/*     */ 
/* 329 */     if (needClassLiteralField) {
/* 330 */       SourceTypeBinding sourceType = 
/* 331 */         this.scope.outerMostClassScope().enclosingSourceType();
/*     */ 
/* 333 */       if ((!sourceType.isInterface()) && (!sourceType.isBaseType()))
/* 334 */         this.classLiteralSyntheticField = sourceType.addSyntheticFieldForClassLiteral(sourceType, this.scope);
/*     */     }
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\ecj-3.5.2.jar
 * Qualified Name:     org.eclipse.jdt.internal.compiler.ast.Clinit
 * JD-Core Version:    0.6.0
 */