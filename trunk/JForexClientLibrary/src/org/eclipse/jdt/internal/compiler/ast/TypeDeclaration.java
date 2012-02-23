/*      */ package org.eclipse.jdt.internal.compiler.ast;
/*      */ 
/*      */ import org.eclipse.jdt.core.compiler.CategorizedProblem;
/*      */ import org.eclipse.jdt.core.compiler.CharOperation;
/*      */ import org.eclipse.jdt.internal.compiler.ASTVisitor;
/*      */ import org.eclipse.jdt.internal.compiler.ClassFile;
/*      */ import org.eclipse.jdt.internal.compiler.CompilationResult;
/*      */ import org.eclipse.jdt.internal.compiler.codegen.CodeStream;
/*      */ import org.eclipse.jdt.internal.compiler.flow.FlowContext;
/*      */ import org.eclipse.jdt.internal.compiler.flow.FlowInfo;
/*      */ import org.eclipse.jdt.internal.compiler.flow.InitializationFlowContext;
/*      */ import org.eclipse.jdt.internal.compiler.flow.UnconditionalFlowInfo;
/*      */ import org.eclipse.jdt.internal.compiler.impl.CompilerOptions;
/*      */ import org.eclipse.jdt.internal.compiler.impl.ReferenceContext;
/*      */ import org.eclipse.jdt.internal.compiler.lookup.Binding;
/*      */ import org.eclipse.jdt.internal.compiler.lookup.BlockScope;
/*      */ import org.eclipse.jdt.internal.compiler.lookup.ClassScope;
/*      */ import org.eclipse.jdt.internal.compiler.lookup.CompilationUnitScope;
/*      */ import org.eclipse.jdt.internal.compiler.lookup.FieldBinding;
/*      */ import org.eclipse.jdt.internal.compiler.lookup.LocalTypeBinding;
/*      */ import org.eclipse.jdt.internal.compiler.lookup.LookupEnvironment;
/*      */ import org.eclipse.jdt.internal.compiler.lookup.MemberTypeBinding;
/*      */ import org.eclipse.jdt.internal.compiler.lookup.MethodBinding;
/*      */ import org.eclipse.jdt.internal.compiler.lookup.MethodScope;
/*      */ import org.eclipse.jdt.internal.compiler.lookup.NestedTypeBinding;
/*      */ import org.eclipse.jdt.internal.compiler.lookup.ReferenceBinding;
/*      */ import org.eclipse.jdt.internal.compiler.lookup.SourceTypeBinding;
/*      */ import org.eclipse.jdt.internal.compiler.lookup.SyntheticArgumentBinding;
/*      */ import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;
/*      */ import org.eclipse.jdt.internal.compiler.lookup.TypeConstants;
/*      */ import org.eclipse.jdt.internal.compiler.lookup.TypeVariableBinding;
/*      */ import org.eclipse.jdt.internal.compiler.parser.Parser;
/*      */ import org.eclipse.jdt.internal.compiler.problem.AbortCompilation;
/*      */ import org.eclipse.jdt.internal.compiler.problem.AbortCompilationUnit;
/*      */ import org.eclipse.jdt.internal.compiler.problem.AbortMethod;
/*      */ import org.eclipse.jdt.internal.compiler.problem.AbortType;
/*      */ import org.eclipse.jdt.internal.compiler.problem.ProblemReporter;
/*      */ import org.eclipse.jdt.internal.compiler.problem.ProblemSeverities;
/*      */ import org.eclipse.jdt.internal.compiler.util.Util;
/*      */ 
/*      */ public class TypeDeclaration extends Statement
/*      */   implements ProblemSeverities, ReferenceContext
/*      */ {
/*      */   public static final int CLASS_DECL = 1;
/*      */   public static final int INTERFACE_DECL = 2;
/*      */   public static final int ENUM_DECL = 3;
/*      */   public static final int ANNOTATION_TYPE_DECL = 4;
/*   31 */   public int modifiers = 0;
/*      */   public int modifiersSourceStart;
/*      */   public Annotation[] annotations;
/*      */   public char[] name;
/*      */   public TypeReference superclass;
/*      */   public TypeReference[] superInterfaces;
/*      */   public FieldDeclaration[] fields;
/*      */   public AbstractMethodDeclaration[] methods;
/*      */   public TypeDeclaration[] memberTypes;
/*      */   public SourceTypeBinding binding;
/*      */   public ClassScope scope;
/*      */   public MethodScope initializerScope;
/*      */   public MethodScope staticInitializerScope;
/*   44 */   public boolean ignoreFurtherInvestigation = false;
/*      */   public int maxFieldCount;
/*      */   public int declarationSourceStart;
/*      */   public int declarationSourceEnd;
/*      */   public int bodyStart;
/*      */   public int bodyEnd;
/*      */   public CompilationResult compilationResult;
/*      */   public MethodDeclaration[] missingAbstractMethods;
/*      */   public Javadoc javadoc;
/*      */   public QualifiedAllocationExpression allocation;
/*      */   public TypeDeclaration enclosingType;
/*      */   public FieldBinding enumValuesSyntheticfield;
/*      */   public TypeParameter[] typeParameters;
/*      */ 
/*      */   public TypeDeclaration(CompilationResult compilationResult)
/*      */   {
/*   63 */     this.compilationResult = compilationResult;
/*      */   }
/*      */ 
/*      */   public void abort(int abortLevel, CategorizedProblem problem)
/*      */   {
/*   70 */     switch (abortLevel) {
/*      */     case 2:
/*   72 */       throw new AbortCompilation(this.compilationResult, problem);
/*      */     case 4:
/*   74 */       throw new AbortCompilationUnit(this.compilationResult, problem);
/*      */     case 16:
/*   76 */       throw new AbortMethod(this.compilationResult, problem);
/*      */     }
/*   78 */     throw new AbortType(this.compilationResult, problem);
/*      */   }
/*      */ 
/*      */   public final void addClinit()
/*      */   {
/*   94 */     if (needClassInitMethod())
/*      */     {
/*      */       AbstractMethodDeclaration[] methodDeclarations;
/*   97 */       if ((methodDeclarations = this.methods) == null) {
/*   98 */         int length = 0;
/*   99 */         methodDeclarations = new AbstractMethodDeclaration[1];
/*      */       } else {
/*  101 */         int length = methodDeclarations.length;
/*  102 */         System.arraycopy(
/*  103 */           methodDeclarations, 
/*  104 */           0, 
/*  105 */           methodDeclarations = new AbstractMethodDeclaration[length + 1], 
/*  106 */           1, 
/*  107 */           length);
/*      */       }
/*  109 */       Clinit clinit = new Clinit(this.compilationResult);
/*  110 */       methodDeclarations[0] = clinit;
/*      */ 
/*  112 */       clinit.declarationSourceStart = (clinit.sourceStart = this.sourceStart);
/*  113 */       clinit.declarationSourceEnd = (clinit.sourceEnd = this.sourceEnd);
/*  114 */       clinit.bodyEnd = this.sourceEnd;
/*  115 */       this.methods = methodDeclarations;
/*      */     }
/*      */   }
/*      */ 
/*      */   public MethodDeclaration addMissingAbstractMethodFor(MethodBinding methodBinding)
/*      */   {
/*  124 */     TypeBinding[] argumentTypes = methodBinding.parameters;
/*  125 */     int argumentsLength = argumentTypes.length;
/*      */ 
/*  127 */     MethodDeclaration methodDeclaration = new MethodDeclaration(this.compilationResult);
/*  128 */     methodDeclaration.selector = methodBinding.selector;
/*  129 */     methodDeclaration.sourceStart = this.sourceStart;
/*  130 */     methodDeclaration.sourceEnd = this.sourceEnd;
/*  131 */     methodDeclaration.modifiers = (methodBinding.getAccessFlags() & 0xFFFFFBFF);
/*      */ 
/*  133 */     if (argumentsLength > 0) {
/*  134 */       String baseName = "arg";
/*  135 */       Argument[] arguments = methodDeclaration.arguments = new Argument[argumentsLength];
/*  136 */       int i = argumentsLength;
/*      */       do { arguments[i] = new Argument((baseName + i).toCharArray(), 0L, null, 0);
/*      */ 
/*  136 */         i--; } while (i >= 0);
/*      */     }
/*      */ 
/*  142 */     if (this.missingAbstractMethods == null) {
/*  143 */       this.missingAbstractMethods = new MethodDeclaration[] { methodDeclaration };
/*      */     }
/*      */     else
/*      */     {
/*      */       MethodDeclaration[] newMethods;
/*  146 */       System.arraycopy(
/*  147 */         this.missingAbstractMethods, 
/*  148 */         0, 
/*  149 */         newMethods = new MethodDeclaration[this.missingAbstractMethods.length + 1], 
/*  150 */         1, 
/*  151 */         this.missingAbstractMethods.length);
/*  152 */       newMethods[0] = methodDeclaration;
/*  153 */       this.missingAbstractMethods = newMethods;
/*      */     }
/*      */ 
/*  157 */     methodDeclaration.binding = 
/*  163 */       new MethodBinding(methodDeclaration.modifiers | 0x1000, 
/*  159 */       methodBinding.selector, 
/*  160 */       methodBinding.returnType, 
/*  161 */       argumentsLength == 0 ? Binding.NO_PARAMETERS : argumentTypes, 
/*  162 */       methodBinding.thrownExceptions, 
/*  163 */       this.binding);
/*      */ 
/*  165 */     methodDeclaration.scope = new MethodScope(this.scope, methodDeclaration, true);
/*  166 */     methodDeclaration.bindArguments();
/*      */ 
/*  183 */     return methodDeclaration;
/*      */   }
/*      */ 
/*      */   public FlowInfo analyseCode(BlockScope currentScope, FlowContext flowContext, FlowInfo flowInfo)
/*      */   {
/*  191 */     if (this.ignoreFurtherInvestigation)
/*  192 */       return flowInfo;
/*      */     try {
/*  194 */       if ((flowInfo.tagBits & 0x1) == 0) {
/*  195 */         this.bits |= -2147483648;
/*  196 */         LocalTypeBinding localType = (LocalTypeBinding)this.binding;
/*  197 */         localType.setConstantPoolName(currentScope.compilationUnitScope().computeConstantPoolName(localType));
/*      */       }
/*  199 */       manageEnclosingInstanceAccessIfNecessary(currentScope, flowInfo);
/*  200 */       updateMaxFieldCount();
/*  201 */       internalAnalyseCode(flowContext, flowInfo);
/*      */     } catch (AbortType localAbortType) {
/*  203 */       this.ignoreFurtherInvestigation = true;
/*      */     }
/*  205 */     return flowInfo;
/*      */   }
/*      */ 
/*      */   public void analyseCode(ClassScope enclosingClassScope)
/*      */   {
/*  213 */     if (this.ignoreFurtherInvestigation)
/*  214 */       return;
/*      */     try
/*      */     {
/*  217 */       updateMaxFieldCount();
/*  218 */       internalAnalyseCode(null, FlowInfo.initial(this.maxFieldCount));
/*      */     } catch (AbortType localAbortType) {
/*  220 */       this.ignoreFurtherInvestigation = true;
/*      */     }
/*      */   }
/*      */ 
/*      */   public void analyseCode(ClassScope currentScope, FlowContext flowContext, FlowInfo flowInfo)
/*      */   {
/*  229 */     if (this.ignoreFurtherInvestigation)
/*  230 */       return;
/*      */     try {
/*  232 */       if ((flowInfo.tagBits & 0x1) == 0) {
/*  233 */         this.bits |= -2147483648;
/*  234 */         LocalTypeBinding localType = (LocalTypeBinding)this.binding;
/*  235 */         localType.setConstantPoolName(currentScope.compilationUnitScope().computeConstantPoolName(localType));
/*      */       }
/*  237 */       manageEnclosingInstanceAccessIfNecessary(currentScope, flowInfo);
/*  238 */       updateMaxFieldCount();
/*  239 */       internalAnalyseCode(flowContext, flowInfo);
/*      */     } catch (AbortType localAbortType) {
/*  241 */       this.ignoreFurtherInvestigation = true;
/*      */     }
/*      */   }
/*      */ 
/*      */   public void analyseCode(CompilationUnitScope unitScope)
/*      */   {
/*  250 */     if (this.ignoreFurtherInvestigation)
/*  251 */       return;
/*      */     try {
/*  253 */       internalAnalyseCode(null, FlowInfo.initial(this.maxFieldCount));
/*      */     } catch (AbortType localAbortType) {
/*  255 */       this.ignoreFurtherInvestigation = true;
/*      */     }
/*      */   }
/*      */ 
/*      */   public boolean checkConstructors(Parser parser)
/*      */   {
/*  266 */     boolean hasConstructor = false;
/*  267 */     if (this.methods != null) {
/*  268 */       int i = this.methods.length;
/*      */       do
/*      */       {
/*      */         AbstractMethodDeclaration am;
/*  270 */         if ((am = this.methods[i]).isConstructor())
/*  271 */           if (!CharOperation.equals(am.selector, this.name))
/*      */           {
/*  274 */             ConstructorDeclaration c = (ConstructorDeclaration)am;
/*  275 */             if ((c.constructorCall == null) || (c.constructorCall.isImplicitSuper())) {
/*  276 */               MethodDeclaration m = parser.convertToMethodDeclaration(c, this.compilationResult);
/*  277 */               this.methods[i] = m;
/*      */             }
/*      */           } else {
/*  280 */             switch (kind(this.modifiers))
/*      */             {
/*      */             case 2:
/*  283 */               parser.problemReporter().interfaceCannotHaveConstructors((ConstructorDeclaration)am);
/*  284 */               break;
/*      */             case 4:
/*  287 */               parser.problemReporter().annotationTypeDeclarationCannotHaveConstructor((ConstructorDeclaration)am);
/*      */             case 3:
/*      */             }
/*      */ 
/*  291 */             hasConstructor = true;
/*      */           }
/*  268 */         i--; } while (i >= 0);
/*      */     }
/*      */ 
/*  296 */     return hasConstructor;
/*      */   }
/*      */ 
/*      */   public CompilationResult compilationResult() {
/*  300 */     return this.compilationResult;
/*      */   }
/*      */ 
/*      */   public ConstructorDeclaration createDefaultConstructor(boolean needExplicitConstructorCall, boolean needToInsert)
/*      */   {
/*  310 */     ConstructorDeclaration constructor = new ConstructorDeclaration(this.compilationResult);
/*  311 */     constructor.bits |= 128;
/*  312 */     constructor.selector = this.name;
/*  313 */     constructor.modifiers = (this.modifiers & 0x7);
/*      */ 
/*  317 */     constructor.declarationSourceStart = (constructor.sourceStart = this.sourceStart);
/*  318 */     constructor.declarationSourceEnd = 
/*  319 */       (constructor.sourceEnd = constructor.bodyEnd = this.sourceEnd);
/*      */ 
/*  322 */     if (needExplicitConstructorCall) {
/*  323 */       constructor.constructorCall = SuperReference.implicitSuperConstructorCall();
/*  324 */       constructor.constructorCall.sourceStart = this.sourceStart;
/*  325 */       constructor.constructorCall.sourceEnd = this.sourceEnd;
/*      */     }
/*      */ 
/*  329 */     if (needToInsert) {
/*  330 */       if (this.methods == null) {
/*  331 */         this.methods = new AbstractMethodDeclaration[] { constructor };
/*      */       }
/*      */       else
/*      */       {
/*      */         AbstractMethodDeclaration[] newMethods;
/*  334 */         System.arraycopy(
/*  335 */           this.methods, 
/*  336 */           0, 
/*  337 */           newMethods = new AbstractMethodDeclaration[this.methods.length + 1], 
/*  338 */           1, 
/*  339 */           this.methods.length);
/*  340 */         newMethods[0] = constructor;
/*  341 */         this.methods = newMethods;
/*      */       }
/*      */     }
/*  344 */     return constructor;
/*      */   }
/*      */ 
/*      */   public MethodBinding createDefaultConstructorWithBinding(MethodBinding inheritedConstructorBinding, boolean eraseThrownExceptions)
/*      */   {
/*  351 */     String baseName = "$anonymous";
/*  352 */     TypeBinding[] argumentTypes = inheritedConstructorBinding.parameters;
/*  353 */     int argumentsLength = argumentTypes.length;
/*      */ 
/*  355 */     ConstructorDeclaration constructor = new ConstructorDeclaration(this.compilationResult);
/*  356 */     constructor.selector = new char[] { 'x' };
/*  357 */     constructor.sourceStart = this.sourceStart;
/*  358 */     constructor.sourceEnd = this.sourceEnd;
/*  359 */     int newModifiers = this.modifiers & 0x7;
/*  360 */     if (inheritedConstructorBinding.isVarargs()) {
/*  361 */       newModifiers |= 128;
/*      */     }
/*  363 */     constructor.modifiers = newModifiers;
/*  364 */     constructor.bits |= 128;
/*      */ 
/*  366 */     if (argumentsLength > 0) {
/*  367 */       Argument[] arguments = constructor.arguments = new Argument[argumentsLength];
/*  368 */       int i = argumentsLength;
/*      */       do { arguments[i] = new Argument((baseName + i).toCharArray(), 0L, null, 0);
/*      */ 
/*  368 */         i--; } while (i >= 0);
/*      */     }
/*      */ 
/*  373 */     constructor.constructorCall = SuperReference.implicitSuperConstructorCall();
/*  374 */     constructor.constructorCall.sourceStart = this.sourceStart;
/*  375 */     constructor.constructorCall.sourceEnd = this.sourceEnd;
/*      */ 
/*  377 */     if (argumentsLength > 0)
/*      */     {
/*  379 */       Expression[] args = constructor.constructorCall.arguments = new Expression[argumentsLength];
/*  380 */       int i = argumentsLength;
/*      */       do { args[i] = new SingleNameReference((baseName + i).toCharArray(), 0L);
/*      */ 
/*  380 */         i--; } while (i >= 0);
/*      */     }
/*      */ 
/*  386 */     if (this.methods == null) {
/*  387 */       this.methods = new AbstractMethodDeclaration[] { constructor };
/*      */     }
/*      */     else
/*      */     {
/*      */       AbstractMethodDeclaration[] newMethods;
/*  390 */       System.arraycopy(this.methods, 0, newMethods = new AbstractMethodDeclaration[this.methods.length + 1], 1, this.methods.length);
/*  391 */       newMethods[0] = constructor;
/*  392 */       this.methods = newMethods;
/*      */     }
/*      */ 
/*  397 */     ReferenceBinding[] thrownExceptions = eraseThrownExceptions ? 
/*  398 */       this.scope.environment().convertToRawTypes(inheritedConstructorBinding.thrownExceptions, true, true) : 
/*  399 */       inheritedConstructorBinding.thrownExceptions;
/*      */ 
/*  401 */     SourceTypeBinding sourceType = this.binding;
/*  402 */     constructor.binding = 
/*  406 */       new MethodBinding(constructor.modifiers, 
/*  404 */       argumentsLength == 0 ? Binding.NO_PARAMETERS : argumentTypes, 
/*  405 */       thrownExceptions, 
/*  406 */       sourceType);
/*  407 */     constructor.binding.tagBits |= inheritedConstructorBinding.tagBits & 0x80;
/*  408 */     constructor.binding.modifiers |= 67108864;
/*      */ 
/*  410 */     constructor.scope = new MethodScope(this.scope, constructor, true);
/*  411 */     constructor.bindArguments();
/*  412 */     constructor.constructorCall.resolve(constructor.scope);
/*      */ 
/*  414 */     MethodBinding[] methodBindings = sourceType.methods();
/*      */     int length;
/*  416 */     System.arraycopy(methodBindings, 0, methodBindings = new MethodBinding[(length = methodBindings.length) + 1], 1, length);
/*  417 */     methodBindings[0] = constructor.binding;
/*  418 */     length++; if (length > 1)
/*  419 */       ReferenceBinding.sortMethods(methodBindings, 0, length);
/*  420 */     sourceType.setMethods(methodBindings);
/*      */ 
/*  423 */     return constructor.binding;
/*      */   }
/*      */ 
/*      */   public FieldDeclaration declarationOf(FieldBinding fieldBinding)
/*      */   {
/*  430 */     if ((fieldBinding != null) && (this.fields != null)) {
/*  431 */       int i = 0; for (int max = this.fields.length; i < max; i++)
/*      */       {
/*      */         FieldDeclaration fieldDecl;
/*  433 */         if ((fieldDecl = this.fields[i]).binding == fieldBinding)
/*  434 */           return fieldDecl;
/*      */       }
/*      */     }
/*  437 */     return null;
/*      */   }
/*      */ 
/*      */   public TypeDeclaration declarationOf(MemberTypeBinding memberTypeBinding)
/*      */   {
/*  444 */     if ((memberTypeBinding != null) && (this.memberTypes != null)) {
/*  445 */       int i = 0; for (int max = this.memberTypes.length; i < max; i++)
/*      */       {
/*      */         TypeDeclaration memberTypeDecl;
/*  447 */         if ((memberTypeDecl = this.memberTypes[i]).binding == memberTypeBinding)
/*  448 */           return memberTypeDecl;
/*      */       }
/*      */     }
/*  451 */     return null;
/*      */   }
/*      */ 
/*      */   public AbstractMethodDeclaration declarationOf(MethodBinding methodBinding)
/*      */   {
/*  458 */     if ((methodBinding != null) && (this.methods != null)) {
/*  459 */       int i = 0; for (int max = this.methods.length; i < max; i++)
/*      */       {
/*      */         AbstractMethodDeclaration methodDecl;
/*  462 */         if ((methodDecl = this.methods[i]).binding == methodBinding)
/*  463 */           return methodDecl;
/*      */       }
/*      */     }
/*  466 */     return null;
/*      */   }
/*      */ 
/*      */   public TypeDeclaration declarationOfType(char[][] typeName)
/*      */   {
/*  477 */     int typeNameLength = typeName.length;
/*  478 */     if ((typeNameLength < 1) || (!CharOperation.equals(typeName[0], this.name))) {
/*  479 */       return null;
/*      */     }
/*  481 */     if (typeNameLength == 1) {
/*  482 */       return this;
/*      */     }
/*  484 */     char[][] subTypeName = new char[typeNameLength - 1][];
/*  485 */     System.arraycopy(typeName, 1, subTypeName, 0, typeNameLength - 1);
/*  486 */     for (int i = 0; i < this.memberTypes.length; i++) {
/*  487 */       TypeDeclaration typeDecl = this.memberTypes[i].declarationOfType(subTypeName);
/*  488 */       if (typeDecl != null) {
/*  489 */         return typeDecl;
/*      */       }
/*      */     }
/*  492 */     return null;
/*      */   }
/*      */ 
/*      */   public void generateCode(ClassFile enclosingClassFile)
/*      */   {
/*  499 */     if ((this.bits & 0x2000) != 0)
/*  500 */       return;
/*  501 */     this.bits |= 8192;
/*  502 */     if (this.ignoreFurtherInvestigation) {
/*  503 */       if (this.binding == null)
/*  504 */         return;
/*  505 */       ClassFile.createProblemType(
/*  506 */         this, 
/*  507 */         this.scope.referenceCompilationUnit().compilationResult);
/*  508 */       return;
/*      */     }
/*      */     try
/*      */     {
/*  512 */       ClassFile classFile = ClassFile.getNewInstance(this.binding);
/*  513 */       classFile.initialize(this.binding, enclosingClassFile, false);
/*  514 */       if (this.binding.isMemberType()) {
/*  515 */         classFile.recordInnerClasses(this.binding);
/*  516 */       } else if (this.binding.isLocalType()) {
/*  517 */         enclosingClassFile.recordInnerClasses(this.binding);
/*  518 */         classFile.recordInnerClasses(this.binding);
/*      */       }
/*  520 */       TypeVariableBinding[] typeVariables = this.binding.typeVariables();
/*  521 */       int i = 0; for (int max = typeVariables.length; i < max; i++) {
/*  522 */         TypeVariableBinding typeVariableBinding = typeVariables[i];
/*  523 */         if ((typeVariableBinding.tagBits & 0x800) != 0L) {
/*  524 */           Util.recordNestedType(classFile, typeVariableBinding);
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/*  529 */       classFile.addFieldInfos();
/*      */ 
/*  531 */       if (this.memberTypes != null) {
/*  532 */         int i = 0; for (int max = this.memberTypes.length; i < max; i++) {
/*  533 */           TypeDeclaration memberType = this.memberTypes[i];
/*  534 */           classFile.recordInnerClasses(memberType.binding);
/*  535 */           memberType.generateCode(this.scope, classFile);
/*      */         }
/*      */       }
/*      */ 
/*  539 */       classFile.setForMethodInfos();
/*  540 */       if (this.methods != null) {
/*  541 */         int i = 0; for (int max = this.methods.length; i < max; i++) {
/*  542 */           this.methods[i].generateCode(this.scope, classFile);
/*      */         }
/*      */       }
/*      */ 
/*  546 */       classFile.addSpecialMethods();
/*      */ 
/*  548 */       if (this.ignoreFurtherInvestigation) {
/*  549 */         throw new AbortType(this.scope.referenceCompilationUnit().compilationResult, null);
/*      */       }
/*      */ 
/*  553 */       classFile.addAttributes();
/*  554 */       this.scope.referenceCompilationUnit().compilationResult.record(
/*  555 */         this.binding.constantPoolName(), 
/*  556 */         classFile);
/*      */     } catch (AbortType localAbortType) {
/*  558 */       if (this.binding == null)
/*  559 */         return;
/*  560 */       ClassFile.createProblemType(
/*  561 */         this, 
/*  562 */         this.scope.referenceCompilationUnit().compilationResult);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void generateCode(BlockScope blockScope, CodeStream codeStream)
/*      */   {
/*  570 */     if ((this.bits & 0x80000000) == 0) {
/*  571 */       return;
/*      */     }
/*  573 */     if ((this.bits & 0x2000) != 0) return;
/*  574 */     int pc = codeStream.position;
/*  575 */     if (this.binding != null) {
/*  576 */       SyntheticArgumentBinding[] enclosingInstances = ((NestedTypeBinding)this.binding).syntheticEnclosingInstances();
/*  577 */       int i = 0; int slotSize = 0; for (int count = enclosingInstances == null ? 0 : enclosingInstances.length; i < count; i++) {
/*  578 */         SyntheticArgumentBinding enclosingInstance = enclosingInstances[i];
/*  579 */         slotSize++; enclosingInstance.resolvedPosition = slotSize;
/*  580 */         if (slotSize > 255) {
/*  581 */           blockScope.problemReporter().noMoreAvailableSpaceForArgument(enclosingInstance, blockScope.referenceType());
/*      */         }
/*      */       }
/*      */     }
/*  585 */     generateCode(codeStream.classFile);
/*  586 */     codeStream.recordPositionsFrom(pc, this.sourceStart);
/*      */   }
/*      */ 
/*      */   public void generateCode(ClassScope classScope, ClassFile enclosingClassFile)
/*      */   {
/*  593 */     if ((this.bits & 0x2000) != 0) return;
/*  594 */     if (this.binding != null) {
/*  595 */       SyntheticArgumentBinding[] enclosingInstances = ((NestedTypeBinding)this.binding).syntheticEnclosingInstances();
/*  596 */       int i = 0; int slotSize = 0; for (int count = enclosingInstances == null ? 0 : enclosingInstances.length; i < count; i++) {
/*  597 */         SyntheticArgumentBinding enclosingInstance = enclosingInstances[i];
/*  598 */         slotSize++; enclosingInstance.resolvedPosition = slotSize;
/*  599 */         if (slotSize > 255) {
/*  600 */           classScope.problemReporter().noMoreAvailableSpaceForArgument(enclosingInstance, classScope.referenceType());
/*      */         }
/*      */       }
/*      */     }
/*  604 */     generateCode(enclosingClassFile);
/*      */   }
/*      */ 
/*      */   public void generateCode(CompilationUnitScope unitScope)
/*      */   {
/*  611 */     generateCode(null);
/*      */   }
/*      */ 
/*      */   public boolean hasErrors() {
/*  615 */     return this.ignoreFurtherInvestigation;
/*      */   }
/*      */ 
/*      */   private void internalAnalyseCode(FlowContext flowContext, FlowInfo flowInfo)
/*      */   {
/*  622 */     if ((!this.binding.isUsed()) && (this.binding.isOrEnclosedByPrivateType()) && 
/*  623 */       (!this.scope.referenceCompilationUnit().compilationResult.hasSyntaxError)) {
/*  624 */       this.scope.problemReporter().unusedPrivateType(this);
/*      */     }
/*      */ 
/*  627 */     InitializationFlowContext initializerContext = new InitializationFlowContext(null, this, flowInfo, flowContext, this.initializerScope);
/*  628 */     InitializationFlowContext staticInitializerContext = new InitializationFlowContext(null, this, flowInfo, flowContext, this.staticInitializerScope);
/*  629 */     FlowInfo nonStaticFieldInfo = flowInfo.unconditionalFieldLessCopy();
/*  630 */     FlowInfo staticFieldInfo = flowInfo.unconditionalFieldLessCopy();
/*  631 */     if (this.fields != null) {
/*  632 */       int i = 0; for (int count = this.fields.length; i < count; i++) {
/*  633 */         FieldDeclaration field = this.fields[i];
/*  634 */         if (field.isStatic()) {
/*  635 */           if ((staticFieldInfo.tagBits & 0x1) != 0) {
/*  636 */             field.bits &= 2147483647;
/*      */           }
/*      */ 
/*  641 */           staticInitializerContext.handledExceptions = Binding.ANY_EXCEPTION;
/*      */ 
/*  643 */           staticFieldInfo = field.analyseCode(this.staticInitializerScope, staticInitializerContext, staticFieldInfo);
/*      */ 
/*  646 */           if (staticFieldInfo == FlowInfo.DEAD_END) {
/*  647 */             this.staticInitializerScope.problemReporter().initializerMustCompleteNormally(field);
/*  648 */             staticFieldInfo = FlowInfo.initial(this.maxFieldCount).setReachMode(1);
/*      */           }
/*      */         } else {
/*  651 */           if ((nonStaticFieldInfo.tagBits & 0x1) != 0) {
/*  652 */             field.bits &= 2147483647;
/*      */           }
/*      */ 
/*  657 */           initializerContext.handledExceptions = Binding.ANY_EXCEPTION;
/*      */ 
/*  659 */           nonStaticFieldInfo = field.analyseCode(this.initializerScope, initializerContext, nonStaticFieldInfo);
/*      */ 
/*  662 */           if (nonStaticFieldInfo == FlowInfo.DEAD_END) {
/*  663 */             this.initializerScope.problemReporter().initializerMustCompleteNormally(field);
/*  664 */             nonStaticFieldInfo = FlowInfo.initial(this.maxFieldCount).setReachMode(1);
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*  669 */     if (this.memberTypes != null) {
/*  670 */       int i = 0; for (int count = this.memberTypes.length; i < count; i++) {
/*  671 */         if (flowContext != null)
/*  672 */           this.memberTypes[i].analyseCode(this.scope, flowContext, nonStaticFieldInfo.copy().setReachMode(flowInfo.reachMode()));
/*      */         else {
/*  674 */           this.memberTypes[i].analyseCode(this.scope);
/*      */         }
/*      */       }
/*      */     }
/*  678 */     if (this.methods != null) {
/*  679 */       UnconditionalFlowInfo outerInfo = flowInfo.unconditionalFieldLessCopy();
/*  680 */       FlowInfo constructorInfo = nonStaticFieldInfo.unconditionalInits().discardNonFieldInitializations().addInitializationsFrom(outerInfo);
/*  681 */       int i = 0; for (int count = this.methods.length; i < count; i++) {
/*  682 */         AbstractMethodDeclaration method = this.methods[i];
/*  683 */         if (method.ignoreFurtherInvestigation)
/*      */           continue;
/*  685 */         if (method.isInitializationMethod()) {
/*  686 */           if (method.isStatic())
/*  687 */             method.analyseCode(
/*  688 */               this.scope, 
/*  689 */               staticInitializerContext, 
/*  690 */               staticFieldInfo.unconditionalInits().discardNonFieldInitializations().addInitializationsFrom(outerInfo));
/*      */           else
/*  692 */             ((ConstructorDeclaration)method).analyseCode(this.scope, initializerContext, constructorInfo.copy(), flowInfo.reachMode());
/*      */         }
/*      */         else {
/*  695 */           method.analyseCode(this.scope, null, flowInfo.copy());
/*      */         }
/*      */       }
/*      */     }
/*      */ 
/*  700 */     if ((this.binding.isEnum()) && (!this.binding.isAnonymousType()))
/*  701 */       this.enumValuesSyntheticfield = this.binding.addSyntheticFieldForEnumValues();
/*      */   }
/*      */ 
/*      */   public static final int kind(int flags)
/*      */   {
/*  706 */     switch (flags & 0x6200) {
/*      */     case 512:
/*  708 */       return 2;
/*      */     case 8704:
/*  710 */       return 4;
/*      */     case 16384:
/*  712 */       return 3;
/*      */     }
/*  714 */     return 1;
/*      */   }
/*      */ 
/*      */   public void manageEnclosingInstanceAccessIfNecessary(BlockScope currentScope, FlowInfo flowInfo)
/*      */   {
/*  727 */     if ((flowInfo.tagBits & 0x1) != 0) return;
/*  728 */     NestedTypeBinding nestedType = (NestedTypeBinding)this.binding;
/*      */ 
/*  730 */     MethodScope methodScope = currentScope.methodScope();
/*  731 */     if ((!methodScope.isStatic) && (!methodScope.isConstructorCall)) {
/*  732 */       nestedType.addSyntheticArgumentAndField(nestedType.enclosingType());
/*      */     }
/*      */ 
/*  735 */     if (nestedType.isAnonymousType()) {
/*  736 */       ReferenceBinding superclassBinding = (ReferenceBinding)nestedType.superclass.erasure();
/*  737 */       if ((superclassBinding.enclosingType() != null) && (!superclassBinding.isStatic()) && (
/*  738 */         (!superclassBinding.isLocalType()) || 
/*  739 */         (((NestedTypeBinding)superclassBinding).getSyntheticField(superclassBinding.enclosingType(), true) != null)))
/*      */       {
/*  741 */         nestedType.addSyntheticArgument(superclassBinding.enclosingType());
/*      */       }
/*      */ 
/*  753 */       if ((!methodScope.isStatic) && (methodScope.isConstructorCall) && (currentScope.compilerOptions().complianceLevel >= 3211264L)) {
/*  754 */         ReferenceBinding enclosing = nestedType.enclosingType();
/*  755 */         if (enclosing.isNestedType()) {
/*  756 */           NestedTypeBinding nestedEnclosing = (NestedTypeBinding)enclosing;
/*      */ 
/*  758 */           SyntheticArgumentBinding syntheticEnclosingInstanceArgument = nestedEnclosing.getSyntheticArgument(nestedEnclosing.enclosingType(), true);
/*  759 */           if (syntheticEnclosingInstanceArgument != null)
/*  760 */             nestedType.addSyntheticArgumentAndField(syntheticEnclosingInstanceArgument);
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   public void manageEnclosingInstanceAccessIfNecessary(ClassScope currentScope, FlowInfo flowInfo)
/*      */   {
/*  778 */     if ((flowInfo.tagBits & 0x1) == 0) {
/*  779 */       NestedTypeBinding nestedType = (NestedTypeBinding)this.binding;
/*  780 */       nestedType.addSyntheticArgumentAndField(this.binding.enclosingType());
/*      */     }
/*      */   }
/*      */ 
/*      */   public final boolean needClassInitMethod()
/*      */   {
/*  790 */     if ((this.bits & 0x1) != 0) {
/*  791 */       return true;
/*      */     }
/*  793 */     switch (kind(this.modifiers)) {
/*      */     case 2:
/*      */     case 4:
/*  796 */       return this.fields != null;
/*      */     case 3:
/*  798 */       return true;
/*      */     }
/*  800 */     if (this.fields != null) {
/*  801 */       int i = this.fields.length;
/*      */       do { FieldDeclaration field = this.fields[i];
/*      */ 
/*  804 */         if ((field.modifiers & 0x8) != 0)
/*  805 */           return true;
/*  801 */         i--; } while (i >= 0);
/*      */     }
/*      */ 
/*  808 */     return false;
/*      */   }
/*      */ 
/*      */   public void parseMethods(Parser parser, CompilationUnitDeclaration unit)
/*      */   {
/*  813 */     if (unit.ignoreMethodBodies) {
/*  814 */       return;
/*      */     }
/*      */ 
/*  817 */     if (this.memberTypes != null) {
/*  818 */       int length = this.memberTypes.length;
/*  819 */       for (int i = 0; i < length; i++) {
/*  820 */         TypeDeclaration typeDeclaration = this.memberTypes[i];
/*  821 */         typeDeclaration.parseMethods(parser, unit);
/*  822 */         this.bits |= typeDeclaration.bits & 0x80000;
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  827 */     if (this.methods != null) {
/*  828 */       int length = this.methods.length;
/*  829 */       for (int i = 0; i < length; i++) {
/*  830 */         AbstractMethodDeclaration abstractMethodDeclaration = this.methods[i];
/*  831 */         abstractMethodDeclaration.parseStatements(parser, unit);
/*  832 */         this.bits |= abstractMethodDeclaration.bits & 0x80000;
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  837 */     if (this.fields != null) {
/*  838 */       int length = this.fields.length;
/*  839 */       for (int i = 0; i < length; i++) {
/*  840 */         FieldDeclaration fieldDeclaration = this.fields[i];
/*  841 */         switch (fieldDeclaration.getKind()) {
/*      */         case 2:
/*  843 */           ((Initializer)fieldDeclaration).parseStatements(parser, this, unit);
/*  844 */           this.bits |= fieldDeclaration.bits & 0x80000;
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   public StringBuffer print(int indent, StringBuffer output)
/*      */   {
/*  852 */     if (this.javadoc != null) {
/*  853 */       this.javadoc.print(indent, output);
/*      */     }
/*  855 */     if ((this.bits & 0x200) == 0) {
/*  856 */       printIndent(indent, output);
/*  857 */       printHeader(0, output);
/*      */     }
/*  859 */     return printBody(indent, output);
/*      */   }
/*      */ 
/*      */   public StringBuffer printBody(int indent, StringBuffer output) {
/*  863 */     output.append(" {");
/*  864 */     if (this.memberTypes != null) {
/*  865 */       for (int i = 0; i < this.memberTypes.length; i++) {
/*  866 */         if (this.memberTypes[i] != null) {
/*  867 */           output.append('\n');
/*  868 */           this.memberTypes[i].print(indent + 1, output);
/*      */         }
/*      */       }
/*      */     }
/*  872 */     if (this.fields != null) {
/*  873 */       for (int fieldI = 0; fieldI < this.fields.length; fieldI++) {
/*  874 */         if (this.fields[fieldI] != null) {
/*  875 */           output.append('\n');
/*  876 */           this.fields[fieldI].print(indent + 1, output);
/*      */         }
/*      */       }
/*      */     }
/*  880 */     if (this.methods != null) {
/*  881 */       for (int i = 0; i < this.methods.length; i++) {
/*  882 */         if (this.methods[i] != null) {
/*  883 */           output.append('\n');
/*  884 */           this.methods[i].print(indent + 1, output);
/*      */         }
/*      */       }
/*      */     }
/*  888 */     output.append('\n');
/*  889 */     return printIndent(indent, output).append('}');
/*      */   }
/*      */ 
/*      */   public StringBuffer printHeader(int indent, StringBuffer output) {
/*  893 */     printModifiers(this.modifiers, output);
/*  894 */     if (this.annotations != null) printAnnotations(this.annotations, output);
/*      */ 
/*  896 */     switch (kind(this.modifiers)) {
/*      */     case 1:
/*  898 */       output.append("class ");
/*  899 */       break;
/*      */     case 2:
/*  901 */       output.append("interface ");
/*  902 */       break;
/*      */     case 3:
/*  904 */       output.append("enum ");
/*  905 */       break;
/*      */     case 4:
/*  907 */       output.append("@interface ");
/*      */     }
/*      */ 
/*  910 */     output.append(this.name);
/*  911 */     if (this.typeParameters != null) {
/*  912 */       output.append("<");
/*  913 */       for (int i = 0; i < this.typeParameters.length; i++) {
/*  914 */         if (i > 0) output.append(", ");
/*  915 */         this.typeParameters[i].print(0, output);
/*      */       }
/*  917 */       output.append(">");
/*      */     }
/*  919 */     if (this.superclass != null) {
/*  920 */       output.append(" extends ");
/*  921 */       this.superclass.print(0, output);
/*      */     }
/*  923 */     if ((this.superInterfaces != null) && (this.superInterfaces.length > 0)) {
/*  924 */       switch (kind(this.modifiers)) {
/*      */       case 1:
/*      */       case 3:
/*  927 */         output.append(" implements ");
/*  928 */         break;
/*      */       case 2:
/*      */       case 4:
/*  931 */         output.append(" extends ");
/*      */       }
/*      */ 
/*  934 */       for (int i = 0; i < this.superInterfaces.length; i++) {
/*  935 */         if (i > 0) output.append(", ");
/*  936 */         this.superInterfaces[i].print(0, output);
/*      */       }
/*      */     }
/*  939 */     return output;
/*      */   }
/*      */ 
/*      */   public StringBuffer printStatement(int tab, StringBuffer output) {
/*  943 */     return print(tab, output);
/*      */   }
/*      */ 
/*      */   public void resolve()
/*      */   {
/*  949 */     SourceTypeBinding sourceType = this.binding;
/*  950 */     if (sourceType == null) {
/*  951 */       this.ignoreFurtherInvestigation = true;
/*  952 */       return;
/*      */     }
/*      */     try {
/*  955 */       boolean old = this.staticInitializerScope.insideTypeAnnotation;
/*      */       try {
/*  957 */         this.staticInitializerScope.insideTypeAnnotation = true;
/*  958 */         resolveAnnotations(this.staticInitializerScope, this.annotations, sourceType);
/*      */       } finally {
/*  960 */         this.staticInitializerScope.insideTypeAnnotation = old;
/*      */       }
/*      */ 
/*  963 */       if (((sourceType.getAnnotationTagBits() & 0x0) == 0L) && 
/*  964 */         ((sourceType.modifiers & 0x100000) != 0) && 
/*  965 */         (this.scope.compilerOptions().sourceLevel >= 3211264L)) {
/*  966 */         this.scope.problemReporter().missingDeprecatedAnnotationForType(this);
/*      */       }
/*  968 */       if ((this.bits & 0x8) != 0) {
/*  969 */         this.scope.problemReporter().undocumentedEmptyBlock(this.bodyStart - 1, this.bodyEnd);
/*      */       }
/*  971 */       boolean needSerialVersion = 
/*  972 */         (this.scope.compilerOptions().getSeverity(536870920) != -1) && 
/*  973 */         (sourceType.isClass()) && 
/*  974 */         (sourceType.findSuperTypeOriginatingFrom(56, false) == null) && 
/*  975 */         (sourceType.findSuperTypeOriginatingFrom(37, false) != null);
/*      */ 
/*  977 */       if (needSerialVersion)
/*      */       {
/*  980 */         CompilationUnitScope compilationUnitScope = this.scope.compilationUnitScope();
/*  981 */         MethodBinding methodBinding = sourceType.getExactMethod(TypeConstants.WRITEREPLACE, new TypeBinding[0], compilationUnitScope);
/*      */         ReferenceBinding[] throwsExceptions;
/*  983 */         needSerialVersion = 
/*  984 */           (methodBinding == null) || 
/*  985 */           (!methodBinding.isValidBinding()) || 
/*  986 */           (methodBinding.returnType.id != 1) || 
/*  987 */           ((throwsExceptions = methodBinding.thrownExceptions).length != 1) || 
/*  988 */           (throwsExceptions[0].id != 57);
/*  989 */         if (needSerialVersion)
/*      */         {
/*  993 */           boolean hasWriteObjectMethod = false;
/*  994 */           boolean hasReadObjectMethod = false;
/*  995 */           TypeBinding argumentTypeBinding = this.scope.getType(TypeConstants.JAVA_IO_OBJECTOUTPUTSTREAM, 3);
/*  996 */           if (argumentTypeBinding.isValidBinding()) {
/*  997 */             methodBinding = sourceType.getExactMethod(TypeConstants.WRITEOBJECT, new TypeBinding[] { argumentTypeBinding }, compilationUnitScope);
/*      */             ReferenceBinding[] throwsExceptions;
/*  998 */             hasWriteObjectMethod = (methodBinding != null) && 
/*  999 */               (methodBinding.isValidBinding()) && 
/* 1000 */               (methodBinding.modifiers == 2) && 
/* 1001 */               (methodBinding.returnType == TypeBinding.VOID) && 
/* 1002 */               ((throwsExceptions = methodBinding.thrownExceptions).length == 1) && 
/* 1003 */               (throwsExceptions[0].id == 58);
/*      */           }
/* 1005 */           argumentTypeBinding = this.scope.getType(TypeConstants.JAVA_IO_OBJECTINPUTSTREAM, 3);
/* 1006 */           if (argumentTypeBinding.isValidBinding()) {
/* 1007 */             methodBinding = sourceType.getExactMethod(TypeConstants.READOBJECT, new TypeBinding[] { argumentTypeBinding }, compilationUnitScope);
/*      */             ReferenceBinding[] throwsExceptions;
/* 1008 */             hasReadObjectMethod = (methodBinding != null) && 
/* 1009 */               (methodBinding.isValidBinding()) && 
/* 1010 */               (methodBinding.modifiers == 2) && 
/* 1011 */               (methodBinding.returnType == TypeBinding.VOID) && 
/* 1012 */               ((throwsExceptions = methodBinding.thrownExceptions).length == 1) && 
/* 1013 */               (throwsExceptions[0].id == 58);
/*      */           }
/* 1015 */           needSerialVersion = (!hasWriteObjectMethod) || (!hasReadObjectMethod);
/*      */         }
/*      */       }
/*      */ 
/* 1019 */       if (sourceType.findSuperTypeOriginatingFrom(21, true) != null) {
/* 1020 */         ReferenceBinding current = sourceType;
/*      */         do {
/* 1022 */           if (current.isGenericType()) {
/* 1023 */             this.scope.problemReporter().genericTypeCannotExtendThrowable(this);
/* 1024 */             break;
/*      */           }
/* 1026 */           if (current.isStatic()) break;
/* 1027 */           if (current.isLocalType()) {
/* 1028 */             NestedTypeBinding nestedType = (NestedTypeBinding)current.erasure();
/* 1029 */             if (nestedType.scope.methodScope().isStatic) break; 
/*      */           }
/*      */         }
/* 1031 */         while ((current = current.enclosingType()) != null);
/*      */       }
/*      */ 
/* 1034 */       int localMaxFieldCount = 0;
/* 1035 */       int lastVisibleFieldID = -1;
/* 1036 */       boolean hasEnumConstants = false;
/* 1037 */       FieldDeclaration[] enumConstantsWithoutBody = (FieldDeclaration[])null;
/*      */ 
/* 1039 */       if (this.typeParameters != null) {
/* 1040 */         int i = 0; for (int count = this.typeParameters.length; i < count; i++) {
/* 1041 */           this.typeParameters[i].resolve(this.scope);
/*      */         }
/*      */       }
/* 1044 */       if (this.memberTypes != null) {
/* 1045 */         int i = 0; for (int count = this.memberTypes.length; i < count; i++) {
/* 1046 */           this.memberTypes[i].resolve(this.scope);
/*      */         }
/*      */       }
/* 1049 */       if (this.fields != null) {
/* 1050 */         int i = 0; for (int count = this.fields.length; i < count; i++) {
/* 1051 */           FieldDeclaration field = this.fields[i];
/* 1052 */           switch (field.getKind()) {
/*      */           case 3:
/* 1054 */             hasEnumConstants = true;
/* 1055 */             if ((field.initialization instanceof QualifiedAllocationExpression)) break;
/* 1056 */             if (enumConstantsWithoutBody == null)
/* 1057 */               enumConstantsWithoutBody = new FieldDeclaration[count];
/* 1058 */             enumConstantsWithoutBody[i] = field;
/*      */           case 1:
/* 1062 */             FieldBinding fieldBinding = field.binding;
/* 1063 */             if (fieldBinding == null)
/*      */             {
/* 1065 */               if (field.initialization != null) field.initialization.resolve(field.isStatic() ? this.staticInitializerScope : this.initializerScope);
/* 1066 */               this.ignoreFurtherInvestigation = true;
/* 1067 */               continue;
/*      */             }
/* 1069 */             if ((needSerialVersion) && 
/* 1070 */               ((fieldBinding.modifiers & 0x18) == 24) && 
/* 1071 */               (CharOperation.equals(TypeConstants.SERIALVERSIONUID, fieldBinding.name)) && 
/* 1072 */               (TypeBinding.LONG == fieldBinding.type)) {
/* 1073 */               needSerialVersion = false;
/*      */             }
/* 1075 */             localMaxFieldCount++;
/* 1076 */             lastVisibleFieldID = field.binding.id;
/* 1077 */             break;
/*      */           case 2:
/* 1080 */             ((Initializer)field).lastVisibleFieldID = (lastVisibleFieldID + 1);
/*      */           }
/*      */ 
/* 1083 */           field.resolve(field.isStatic() ? this.staticInitializerScope : this.initializerScope);
/*      */         }
/*      */       }
/* 1086 */       if (this.maxFieldCount < localMaxFieldCount) {
/* 1087 */         this.maxFieldCount = localMaxFieldCount;
/*      */       }
/* 1089 */       if (needSerialVersion)
/*      */       {
/* 1091 */         TypeBinding javaxRmiCorbaStub = this.scope.getType(TypeConstants.JAVAX_RMI_CORBA_STUB, 4);
/* 1092 */         if (javaxRmiCorbaStub.isValidBinding()) {
/* 1093 */           ReferenceBinding superclassBinding = this.binding.superclass;
/* 1094 */           while (superclassBinding != null) {
/* 1095 */             if (superclassBinding == javaxRmiCorbaStub) {
/* 1096 */               needSerialVersion = false;
/* 1097 */               break;
/*      */             }
/* 1099 */             superclassBinding = superclassBinding.superclass();
/*      */           }
/*      */         }
/* 1102 */         if (needSerialVersion) {
/* 1103 */           this.scope.problemReporter().missingSerialVersion(this);
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/* 1108 */       switch (kind(this.modifiers)) {
/*      */       case 4:
/* 1110 */         if (this.superclass != null) {
/* 1111 */           this.scope.problemReporter().annotationTypeDeclarationCannotHaveSuperclass(this);
/*      */         }
/* 1113 */         if (this.superInterfaces == null) break;
/* 1114 */         this.scope.problemReporter().annotationTypeDeclarationCannotHaveSuperinterfaces(this);
/*      */ 
/* 1116 */         break;
/*      */       case 3:
/* 1119 */         if (!this.binding.isAbstract()) break;
/* 1120 */         if (!hasEnumConstants) {
/* 1121 */           int i = 0; for (int count = this.methods.length; i < count; i++) {
/* 1122 */             AbstractMethodDeclaration methodDeclaration = this.methods[i];
/* 1123 */             if ((methodDeclaration.isAbstract()) && (methodDeclaration.binding != null))
/* 1124 */               this.scope.problemReporter().enumAbstractMethodMustBeImplemented(methodDeclaration); 
/*      */           }
/*      */         } else {
/* 1126 */           if (enumConstantsWithoutBody == null) break;
/* 1127 */           int i = 0; for (int count = this.methods.length; i < count; i++) {
/* 1128 */             AbstractMethodDeclaration methodDeclaration = this.methods[i];
/* 1129 */             if ((methodDeclaration.isAbstract()) && (methodDeclaration.binding != null)) {
/* 1130 */               int f = 0; for (int l = enumConstantsWithoutBody.length; f < l; f++) {
/* 1131 */                 if (enumConstantsWithoutBody[f] != null) {
/* 1132 */                   this.scope.problemReporter().enumConstantMustImplementAbstractMethod(methodDeclaration, enumConstantsWithoutBody[f]);
/*      */                 }
/*      */               }
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/*      */ 
/* 1140 */       int missingAbstractMethodslength = this.missingAbstractMethods == null ? 0 : this.missingAbstractMethods.length;
/* 1141 */       int methodsLength = this.methods == null ? 0 : this.methods.length;
/* 1142 */       if (methodsLength + missingAbstractMethodslength > 65535) {
/* 1143 */         this.scope.problemReporter().tooManyMethods(this);
/*      */       }
/* 1145 */       if (this.methods != null) {
/* 1146 */         int i = 0; for (int count = this.methods.length; i < count; i++) {
/* 1147 */           this.methods[i].resolve(this.scope);
/*      */         }
/*      */       }
/*      */ 
/* 1151 */       if (this.javadoc != null) {
/* 1152 */         if ((this.scope != null) && (this.name != TypeConstants.PACKAGE_INFO_NAME))
/*      */         {
/* 1154 */           this.javadoc.resolve(this.scope);
/*      */         }
/* 1156 */       } else if (!sourceType.isLocalType())
/*      */       {
/* 1158 */         int visibility = sourceType.modifiers & 0x7;
/* 1159 */         ProblemReporter reporter = this.scope.problemReporter();
/* 1160 */         int severity = reporter.computeSeverity(-1610612250);
/* 1161 */         if (severity != -1) {
/* 1162 */           if (this.enclosingType != null) {
/* 1163 */             visibility = Util.computeOuterMostVisibility(this.enclosingType, visibility);
/*      */           }
/* 1165 */           int javadocModifiers = this.binding.modifiers & 0xFFFFFFF8 | visibility;
/* 1166 */           reporter.javadocMissing(this.sourceStart, this.sourceEnd, severity, javadocModifiers);
/*      */         }
/*      */       }
/*      */     } catch (AbortType localAbortType) {
/* 1170 */       this.ignoreFurtherInvestigation = true;
/* 1171 */       return;
/*      */     }
/*      */   }
/*      */ 
/*      */   public void resolve(BlockScope blockScope)
/*      */   {
/* 1181 */     if ((this.bits & 0x200) == 0)
/*      */     {
/* 1183 */       Binding existing = blockScope.getType(this.name);
/* 1184 */       if (((existing instanceof ReferenceBinding)) && 
/* 1185 */         (existing != this.binding) && 
/* 1186 */         (existing.isValidBinding())) {
/* 1187 */         ReferenceBinding existingType = (ReferenceBinding)existing;
/* 1188 */         if ((existingType instanceof TypeVariableBinding))
/* 1189 */           blockScope.problemReporter().typeHiding(this, (TypeVariableBinding)existingType);
/* 1190 */         else if (((existingType instanceof LocalTypeBinding)) && 
/* 1191 */           (((LocalTypeBinding)existingType).scope.methodScope() == blockScope.methodScope()))
/*      */         {
/* 1193 */           blockScope.problemReporter().duplicateNestedType(this);
/* 1194 */         } else if (blockScope.isDefinedInType(existingType))
/*      */         {
/* 1196 */           blockScope.problemReporter().typeCollidesWithEnclosingType(this);
/* 1197 */         } else if (blockScope.isDefinedInSameUnit(existingType))
/*      */         {
/* 1199 */           blockScope.problemReporter().typeHiding(this, existingType);
/*      */         }
/*      */       }
/* 1202 */       blockScope.addLocalType(this);
/*      */     }
/*      */ 
/* 1205 */     if (this.binding != null)
/*      */     {
/* 1207 */       blockScope.referenceCompilationUnit().record((LocalTypeBinding)this.binding);
/*      */ 
/* 1210 */       resolve();
/* 1211 */       updateMaxFieldCount();
/*      */     }
/*      */   }
/*      */ 
/*      */   public void resolve(ClassScope upperScope)
/*      */   {
/* 1222 */     if ((this.binding != null) && ((this.binding instanceof LocalTypeBinding)))
/*      */     {
/* 1224 */       upperScope.referenceCompilationUnit().record((LocalTypeBinding)this.binding);
/*      */     }
/* 1226 */     resolve();
/* 1227 */     updateMaxFieldCount();
/*      */   }
/*      */ 
/*      */   public void resolve(CompilationUnitScope upperScope)
/*      */   {
/* 1235 */     resolve();
/* 1236 */     updateMaxFieldCount();
/*      */   }
/*      */ 
/*      */   public void tagAsHavingErrors() {
/* 1240 */     this.ignoreFurtherInvestigation = true;
/*      */   }
/*      */ 
/*      */   public void traverse(ASTVisitor visitor, CompilationUnitScope unitScope)
/*      */   {
/*      */     try
/*      */     {
/* 1249 */       if (visitor.visit(this, unitScope)) {
/* 1250 */         if (this.javadoc != null) {
/* 1251 */           this.javadoc.traverse(visitor, this.scope);
/*      */         }
/* 1253 */         if (this.annotations != null) {
/* 1254 */           int annotationsLength = this.annotations.length;
/* 1255 */           for (int i = 0; i < annotationsLength; i++)
/* 1256 */             this.annotations[i].traverse(visitor, this.staticInitializerScope);
/*      */         }
/* 1258 */         if (this.superclass != null)
/* 1259 */           this.superclass.traverse(visitor, this.scope);
/* 1260 */         if (this.superInterfaces != null) {
/* 1261 */           int length = this.superInterfaces.length;
/* 1262 */           for (int i = 0; i < length; i++)
/* 1263 */             this.superInterfaces[i].traverse(visitor, this.scope);
/*      */         }
/* 1265 */         if (this.typeParameters != null) {
/* 1266 */           int length = this.typeParameters.length;
/* 1267 */           for (int i = 0; i < length; i++) {
/* 1268 */             this.typeParameters[i].traverse(visitor, this.scope);
/*      */           }
/*      */         }
/* 1271 */         if (this.memberTypes != null) {
/* 1272 */           int length = this.memberTypes.length;
/* 1273 */           for (int i = 0; i < length; i++)
/* 1274 */             this.memberTypes[i].traverse(visitor, this.scope);
/*      */         }
/* 1276 */         if (this.fields != null) {
/* 1277 */           int length = this.fields.length;
/* 1278 */           for (int i = 0; i < length; i++)
/*      */           {
/*      */             FieldDeclaration field;
/* 1280 */             if ((field = this.fields[i]).isStatic())
/* 1281 */               field.traverse(visitor, this.staticInitializerScope);
/*      */             else {
/* 1283 */               field.traverse(visitor, this.initializerScope);
/*      */             }
/*      */           }
/*      */         }
/* 1287 */         if (this.methods != null) {
/* 1288 */           int length = this.methods.length;
/* 1289 */           for (int i = 0; i < length; i++)
/* 1290 */             this.methods[i].traverse(visitor, this.scope);
/*      */         }
/*      */       }
/* 1293 */       visitor.endVisit(this, unitScope);
/*      */     }
/*      */     catch (AbortType localAbortType)
/*      */     {
/*      */     }
/*      */   }
/*      */ 
/*      */   public void traverse(ASTVisitor visitor, BlockScope blockScope)
/*      */   {
/*      */     try
/*      */     {
/* 1304 */       if (visitor.visit(this, blockScope)) {
/* 1305 */         if (this.javadoc != null) {
/* 1306 */           this.javadoc.traverse(visitor, this.scope);
/*      */         }
/* 1308 */         if (this.annotations != null) {
/* 1309 */           int annotationsLength = this.annotations.length;
/* 1310 */           for (int i = 0; i < annotationsLength; i++)
/* 1311 */             this.annotations[i].traverse(visitor, this.staticInitializerScope);
/*      */         }
/* 1313 */         if (this.superclass != null)
/* 1314 */           this.superclass.traverse(visitor, this.scope);
/* 1315 */         if (this.superInterfaces != null) {
/* 1316 */           int length = this.superInterfaces.length;
/* 1317 */           for (int i = 0; i < length; i++)
/* 1318 */             this.superInterfaces[i].traverse(visitor, this.scope);
/*      */         }
/* 1320 */         if (this.typeParameters != null) {
/* 1321 */           int length = this.typeParameters.length;
/* 1322 */           for (int i = 0; i < length; i++) {
/* 1323 */             this.typeParameters[i].traverse(visitor, this.scope);
/*      */           }
/*      */         }
/* 1326 */         if (this.memberTypes != null) {
/* 1327 */           int length = this.memberTypes.length;
/* 1328 */           for (int i = 0; i < length; i++)
/* 1329 */             this.memberTypes[i].traverse(visitor, this.scope);
/*      */         }
/* 1331 */         if (this.fields != null) {
/* 1332 */           int length = this.fields.length;
/* 1333 */           for (int i = 0; i < length; i++)
/*      */           {
/*      */             FieldDeclaration field;
/* 1335 */             if ((field = this.fields[i]).isStatic()) {
/*      */               continue;
/*      */             }
/* 1338 */             field.traverse(visitor, this.initializerScope);
/*      */           }
/*      */         }
/*      */ 
/* 1342 */         if (this.methods != null) {
/* 1343 */           int length = this.methods.length;
/* 1344 */           for (int i = 0; i < length; i++)
/* 1345 */             this.methods[i].traverse(visitor, this.scope);
/*      */         }
/*      */       }
/* 1348 */       visitor.endVisit(this, blockScope);
/*      */     }
/*      */     catch (AbortType localAbortType)
/*      */     {
/*      */     }
/*      */   }
/*      */ 
/*      */   public void traverse(ASTVisitor visitor, ClassScope classScope)
/*      */   {
/*      */     try
/*      */     {
/* 1360 */       if (visitor.visit(this, classScope)) {
/* 1361 */         if (this.javadoc != null) {
/* 1362 */           this.javadoc.traverse(visitor, this.scope);
/*      */         }
/* 1364 */         if (this.annotations != null) {
/* 1365 */           int annotationsLength = this.annotations.length;
/* 1366 */           for (int i = 0; i < annotationsLength; i++)
/* 1367 */             this.annotations[i].traverse(visitor, this.staticInitializerScope);
/*      */         }
/* 1369 */         if (this.superclass != null)
/* 1370 */           this.superclass.traverse(visitor, this.scope);
/* 1371 */         if (this.superInterfaces != null) {
/* 1372 */           int length = this.superInterfaces.length;
/* 1373 */           for (int i = 0; i < length; i++)
/* 1374 */             this.superInterfaces[i].traverse(visitor, this.scope);
/*      */         }
/* 1376 */         if (this.typeParameters != null) {
/* 1377 */           int length = this.typeParameters.length;
/* 1378 */           for (int i = 0; i < length; i++) {
/* 1379 */             this.typeParameters[i].traverse(visitor, this.scope);
/*      */           }
/*      */         }
/* 1382 */         if (this.memberTypes != null) {
/* 1383 */           int length = this.memberTypes.length;
/* 1384 */           for (int i = 0; i < length; i++)
/* 1385 */             this.memberTypes[i].traverse(visitor, this.scope);
/*      */         }
/* 1387 */         if (this.fields != null) {
/* 1388 */           int length = this.fields.length;
/* 1389 */           for (int i = 0; i < length; i++)
/*      */           {
/*      */             FieldDeclaration field;
/* 1391 */             if ((field = this.fields[i]).isStatic())
/* 1392 */               field.traverse(visitor, this.staticInitializerScope);
/*      */             else {
/* 1394 */               field.traverse(visitor, this.initializerScope);
/*      */             }
/*      */           }
/*      */         }
/* 1398 */         if (this.methods != null) {
/* 1399 */           int length = this.methods.length;
/* 1400 */           for (int i = 0; i < length; i++)
/* 1401 */             this.methods[i].traverse(visitor, this.scope);
/*      */         }
/*      */       }
/* 1404 */       visitor.endVisit(this, classScope);
/*      */     }
/*      */     catch (AbortType localAbortType)
/*      */     {
/*      */     }
/*      */   }
/*      */ 
/*      */   void updateMaxFieldCount()
/*      */   {
/* 1421 */     if (this.binding == null)
/* 1422 */       return;
/* 1423 */     TypeDeclaration outerMostType = this.scope.outerMostClassScope().referenceType();
/* 1424 */     if (this.maxFieldCount > outerMostType.maxFieldCount)
/* 1425 */       outerMostType.maxFieldCount = this.maxFieldCount;
/*      */     else
/* 1427 */       this.maxFieldCount = outerMostType.maxFieldCount;
/*      */   }
/*      */ 
/*      */   public boolean isSecondary()
/*      */   {
/* 1435 */     return (this.bits & 0x1000) != 0;
/*      */   }
/*      */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\ecj-3.5.2.jar
 * Qualified Name:     org.eclipse.jdt.internal.compiler.ast.TypeDeclaration
 * JD-Core Version:    0.6.0
 */