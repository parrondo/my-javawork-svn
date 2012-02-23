/*      */ package org.eclipse.jdt.internal.compiler.impl;
/*      */ 
/*      */ import java.io.ByteArrayInputStream;
/*      */ import java.io.InputStreamReader;
/*      */ import java.io.UnsupportedEncodingException;
/*      */ import java.util.HashMap;
/*      */ import java.util.Map;
/*      */ import org.eclipse.jdt.core.compiler.CharOperation;
/*      */ import org.eclipse.jdt.internal.compiler.Compiler;
/*      */ import org.eclipse.jdt.internal.compiler.util.Util;
/*      */ 
/*      */ public class CompilerOptions
/*      */ {
/*      */   public static final String OPTION_LocalVariableAttribute = "org.eclipse.jdt.core.compiler.debug.localVariable";
/*      */   public static final String OPTION_LineNumberAttribute = "org.eclipse.jdt.core.compiler.debug.lineNumber";
/*      */   public static final String OPTION_SourceFileAttribute = "org.eclipse.jdt.core.compiler.debug.sourceFile";
/*      */   public static final String OPTION_PreserveUnusedLocal = "org.eclipse.jdt.core.compiler.codegen.unusedLocal";
/*      */   public static final String OPTION_DocCommentSupport = "org.eclipse.jdt.core.compiler.doc.comment.support";
/*      */   public static final String OPTION_ReportMethodWithConstructorName = "org.eclipse.jdt.core.compiler.problem.methodWithConstructorName";
/*      */   public static final String OPTION_ReportOverridingPackageDefaultMethod = "org.eclipse.jdt.core.compiler.problem.overridingPackageDefaultMethod";
/*      */   public static final String OPTION_ReportDeprecation = "org.eclipse.jdt.core.compiler.problem.deprecation";
/*      */   public static final String OPTION_ReportDeprecationInDeprecatedCode = "org.eclipse.jdt.core.compiler.problem.deprecationInDeprecatedCode";
/*      */   public static final String OPTION_ReportDeprecationWhenOverridingDeprecatedMethod = "org.eclipse.jdt.core.compiler.problem.deprecationWhenOverridingDeprecatedMethod";
/*      */   public static final String OPTION_ReportHiddenCatchBlock = "org.eclipse.jdt.core.compiler.problem.hiddenCatchBlock";
/*      */   public static final String OPTION_ReportUnusedLocal = "org.eclipse.jdt.core.compiler.problem.unusedLocal";
/*      */   public static final String OPTION_ReportUnusedParameter = "org.eclipse.jdt.core.compiler.problem.unusedParameter";
/*      */   public static final String OPTION_ReportUnusedParameterWhenImplementingAbstract = "org.eclipse.jdt.core.compiler.problem.unusedParameterWhenImplementingAbstract";
/*      */   public static final String OPTION_ReportUnusedParameterWhenOverridingConcrete = "org.eclipse.jdt.core.compiler.problem.unusedParameterWhenOverridingConcrete";
/*      */   public static final String OPTION_ReportUnusedParameterIncludeDocCommentReference = "org.eclipse.jdt.core.compiler.problem.unusedParameterIncludeDocCommentReference";
/*      */   public static final String OPTION_ReportUnusedImport = "org.eclipse.jdt.core.compiler.problem.unusedImport";
/*      */   public static final String OPTION_ReportSyntheticAccessEmulation = "org.eclipse.jdt.core.compiler.problem.syntheticAccessEmulation";
/*      */   public static final String OPTION_ReportNoEffectAssignment = "org.eclipse.jdt.core.compiler.problem.noEffectAssignment";
/*      */   public static final String OPTION_ReportLocalVariableHiding = "org.eclipse.jdt.core.compiler.problem.localVariableHiding";
/*      */   public static final String OPTION_ReportSpecialParameterHidingField = "org.eclipse.jdt.core.compiler.problem.specialParameterHidingField";
/*      */   public static final String OPTION_ReportFieldHiding = "org.eclipse.jdt.core.compiler.problem.fieldHiding";
/*      */   public static final String OPTION_ReportTypeParameterHiding = "org.eclipse.jdt.core.compiler.problem.typeParameterHiding";
/*      */   public static final String OPTION_ReportPossibleAccidentalBooleanAssignment = "org.eclipse.jdt.core.compiler.problem.possibleAccidentalBooleanAssignment";
/*      */   public static final String OPTION_ReportNonExternalizedStringLiteral = "org.eclipse.jdt.core.compiler.problem.nonExternalizedStringLiteral";
/*      */   public static final String OPTION_ReportIncompatibleNonInheritedInterfaceMethod = "org.eclipse.jdt.core.compiler.problem.incompatibleNonInheritedInterfaceMethod";
/*      */   public static final String OPTION_ReportUnusedPrivateMember = "org.eclipse.jdt.core.compiler.problem.unusedPrivateMember";
/*      */   public static final String OPTION_ReportNoImplicitStringConversion = "org.eclipse.jdt.core.compiler.problem.noImplicitStringConversion";
/*      */   public static final String OPTION_ReportAssertIdentifier = "org.eclipse.jdt.core.compiler.problem.assertIdentifier";
/*      */   public static final String OPTION_ReportEnumIdentifier = "org.eclipse.jdt.core.compiler.problem.enumIdentifier";
/*      */   public static final String OPTION_ReportNonStaticAccessToStatic = "org.eclipse.jdt.core.compiler.problem.staticAccessReceiver";
/*      */   public static final String OPTION_ReportIndirectStaticAccess = "org.eclipse.jdt.core.compiler.problem.indirectStaticAccess";
/*      */   public static final String OPTION_ReportEmptyStatement = "org.eclipse.jdt.core.compiler.problem.emptyStatement";
/*      */   public static final String OPTION_ReportUnnecessaryTypeCheck = "org.eclipse.jdt.core.compiler.problem.unnecessaryTypeCheck";
/*      */   public static final String OPTION_ReportUnnecessaryElse = "org.eclipse.jdt.core.compiler.problem.unnecessaryElse";
/*      */   public static final String OPTION_ReportUndocumentedEmptyBlock = "org.eclipse.jdt.core.compiler.problem.undocumentedEmptyBlock";
/*      */   public static final String OPTION_ReportInvalidJavadoc = "org.eclipse.jdt.core.compiler.problem.invalidJavadoc";
/*      */   public static final String OPTION_ReportInvalidJavadocTags = "org.eclipse.jdt.core.compiler.problem.invalidJavadocTags";
/*      */   public static final String OPTION_ReportInvalidJavadocTagsDeprecatedRef = "org.eclipse.jdt.core.compiler.problem.invalidJavadocTagsDeprecatedRef";
/*      */   public static final String OPTION_ReportInvalidJavadocTagsNotVisibleRef = "org.eclipse.jdt.core.compiler.problem.invalidJavadocTagsNotVisibleRef";
/*      */   public static final String OPTION_ReportInvalidJavadocTagsVisibility = "org.eclipse.jdt.core.compiler.problem.invalidJavadocTagsVisibility";
/*      */   public static final String OPTION_ReportMissingJavadocTags = "org.eclipse.jdt.core.compiler.problem.missingJavadocTags";
/*      */   public static final String OPTION_ReportMissingJavadocTagsVisibility = "org.eclipse.jdt.core.compiler.problem.missingJavadocTagsVisibility";
/*      */   public static final String OPTION_ReportMissingJavadocTagsOverriding = "org.eclipse.jdt.core.compiler.problem.missingJavadocTagsOverriding";
/*      */   public static final String OPTION_ReportMissingJavadocComments = "org.eclipse.jdt.core.compiler.problem.missingJavadocComments";
/*      */   public static final String OPTION_ReportMissingJavadocTagDescription = "org.eclipse.jdt.core.compiler.problem.missingJavadocTagDescription";
/*      */   public static final String OPTION_ReportMissingJavadocCommentsVisibility = "org.eclipse.jdt.core.compiler.problem.missingJavadocCommentsVisibility";
/*      */   public static final String OPTION_ReportMissingJavadocCommentsOverriding = "org.eclipse.jdt.core.compiler.problem.missingJavadocCommentsOverriding";
/*      */   public static final String OPTION_ReportFinallyBlockNotCompletingNormally = "org.eclipse.jdt.core.compiler.problem.finallyBlockNotCompletingNormally";
/*      */   public static final String OPTION_ReportUnusedDeclaredThrownException = "org.eclipse.jdt.core.compiler.problem.unusedDeclaredThrownException";
/*      */   public static final String OPTION_ReportUnusedDeclaredThrownExceptionWhenOverriding = "org.eclipse.jdt.core.compiler.problem.unusedDeclaredThrownExceptionWhenOverriding";
/*      */   public static final String OPTION_ReportUnusedDeclaredThrownExceptionIncludeDocCommentReference = "org.eclipse.jdt.core.compiler.problem.unusedDeclaredThrownExceptionIncludeDocCommentReference";
/*      */   public static final String OPTION_ReportUnusedDeclaredThrownExceptionExemptExceptionAndThrowable = "org.eclipse.jdt.core.compiler.problem.unusedDeclaredThrownExceptionExemptExceptionAndThrowable";
/*      */   public static final String OPTION_ReportUnqualifiedFieldAccess = "org.eclipse.jdt.core.compiler.problem.unqualifiedFieldAccess";
/*      */   public static final String OPTION_ReportUncheckedTypeOperation = "org.eclipse.jdt.core.compiler.problem.uncheckedTypeOperation";
/*      */   public static final String OPTION_ReportRawTypeReference = "org.eclipse.jdt.core.compiler.problem.rawTypeReference";
/*      */   public static final String OPTION_ReportFinalParameterBound = "org.eclipse.jdt.core.compiler.problem.finalParameterBound";
/*      */   public static final String OPTION_ReportMissingSerialVersion = "org.eclipse.jdt.core.compiler.problem.missingSerialVersion";
/*      */   public static final String OPTION_ReportVarargsArgumentNeedCast = "org.eclipse.jdt.core.compiler.problem.varargsArgumentNeedCast";
/*      */   public static final String OPTION_ReportUnusedTypeArgumentsForMethodInvocation = "org.eclipse.jdt.core.compiler.problem.unusedTypeArgumentsForMethodInvocation";
/*      */   public static final String OPTION_Source = "org.eclipse.jdt.core.compiler.source";
/*      */   public static final String OPTION_TargetPlatform = "org.eclipse.jdt.core.compiler.codegen.targetPlatform";
/*      */   public static final String OPTION_Compliance = "org.eclipse.jdt.core.compiler.compliance";
/*      */   public static final String OPTION_Encoding = "org.eclipse.jdt.core.encoding";
/*      */   public static final String OPTION_MaxProblemPerUnit = "org.eclipse.jdt.core.compiler.maxProblemPerUnit";
/*      */   public static final String OPTION_TaskTags = "org.eclipse.jdt.core.compiler.taskTags";
/*      */   public static final String OPTION_TaskPriorities = "org.eclipse.jdt.core.compiler.taskPriorities";
/*      */   public static final String OPTION_TaskCaseSensitive = "org.eclipse.jdt.core.compiler.taskCaseSensitive";
/*      */   public static final String OPTION_InlineJsr = "org.eclipse.jdt.core.compiler.codegen.inlineJsrBytecode";
/*      */   public static final String OPTION_ReportNullReference = "org.eclipse.jdt.core.compiler.problem.nullReference";
/*      */   public static final String OPTION_ReportPotentialNullReference = "org.eclipse.jdt.core.compiler.problem.potentialNullReference";
/*      */   public static final String OPTION_ReportRedundantNullCheck = "org.eclipse.jdt.core.compiler.problem.redundantNullCheck";
/*      */   public static final String OPTION_ReportAutoboxing = "org.eclipse.jdt.core.compiler.problem.autoboxing";
/*      */   public static final String OPTION_ReportAnnotationSuperInterface = "org.eclipse.jdt.core.compiler.problem.annotationSuperInterface";
/*      */   public static final String OPTION_ReportMissingOverrideAnnotation = "org.eclipse.jdt.core.compiler.problem.missingOverrideAnnotation";
/*      */   public static final String OPTION_ReportMissingDeprecatedAnnotation = "org.eclipse.jdt.core.compiler.problem.missingDeprecatedAnnotation";
/*      */   public static final String OPTION_ReportIncompleteEnumSwitch = "org.eclipse.jdt.core.compiler.problem.incompleteEnumSwitch";
/*      */   public static final String OPTION_ReportForbiddenReference = "org.eclipse.jdt.core.compiler.problem.forbiddenReference";
/*      */   public static final String OPTION_ReportDiscouragedReference = "org.eclipse.jdt.core.compiler.problem.discouragedReference";
/*      */   public static final String OPTION_SuppressWarnings = "org.eclipse.jdt.core.compiler.problem.suppressWarnings";
/*      */   public static final String OPTION_ReportUnhandledWarningToken = "org.eclipse.jdt.core.compiler.problem.unhandledWarningToken";
/*      */   public static final String OPTION_ReportUnusedWarningToken = "org.eclipse.jdt.core.compiler.problem.unusedWarningToken";
/*      */   public static final String OPTION_ReportUnusedLabel = "org.eclipse.jdt.core.compiler.problem.unusedLabel";
/*      */   public static final String OPTION_FatalOptionalError = "org.eclipse.jdt.core.compiler.problem.fatalOptionalError";
/*      */   public static final String OPTION_ReportParameterAssignment = "org.eclipse.jdt.core.compiler.problem.parameterAssignment";
/*      */   public static final String OPTION_ReportFallthroughCase = "org.eclipse.jdt.core.compiler.problem.fallthroughCase";
/*      */   public static final String OPTION_ReportOverridingMethodWithoutSuperInvocation = "org.eclipse.jdt.core.compiler.problem.overridingMethodWithoutSuperInvocation";
/*      */   public static final String OPTION_GenerateClassFiles = "org.eclipse.jdt.core.compiler.generateClassFiles";
/*      */   public static final String OPTION_Process_Annotations = "org.eclipse.jdt.core.compiler.processAnnotations";
/*      */   public static final String OPTION_ReportRedundantSuperinterface = "org.eclipse.jdt.core.compiler.problem.redundantSuperinterface";
/*      */   public static final String OPTION_ReportComparingIdentical = "org.eclipse.jdt.core.compiler.problem.comparingIdentical";
/*      */   public static final String OPTION_ReportMissingSynchronizedOnInheritedMethod = "org.eclipse.jdt.core.compiler.problem.missingSynchronizedOnInheritedMethod";
/*      */   public static final String OPTION_ReportMissingHashCodeMethod = "org.eclipse.jdt.core.compiler.problem.missingHashCodeMethod";
/*      */   public static final String OPTION_ReportDeadCode = "org.eclipse.jdt.core.compiler.problem.deadCode";
/*      */   public static final String OPTION_ReportDeadCodeInTrivialIfStatement = "org.eclipse.jdt.core.compiler.problem.deadCodeInTrivialIfStatement";
/*      */   public static final String OPTION_ReportInvalidAnnotation = "org.eclipse.jdt.core.compiler.problem.invalidAnnotation";
/*      */   public static final String OPTION_ReportMissingAnnotation = "org.eclipse.jdt.core.compiler.problem.missingAnnotation";
/*      */   public static final String OPTION_ReportMissingJavadoc = "org.eclipse.jdt.core.compiler.problem.missingJavadoc";
/*      */   public static final String GENERATE = "generate";
/*      */   public static final String DO_NOT_GENERATE = "do not generate";
/*      */   public static final String PRESERVE = "preserve";
/*      */   public static final String OPTIMIZE_OUT = "optimize out";
/*      */   public static final String VERSION_1_1 = "1.1";
/*      */   public static final String VERSION_1_2 = "1.2";
/*      */   public static final String VERSION_1_3 = "1.3";
/*      */   public static final String VERSION_1_4 = "1.4";
/*      */   public static final String VERSION_JSR14 = "jsr14";
/*      */   public static final String VERSION_CLDC1_1 = "cldc1.1";
/*      */   public static final String VERSION_1_5 = "1.5";
/*      */   public static final String VERSION_1_6 = "1.6";
/*      */   public static final String VERSION_1_7 = "1.7";
/*      */   public static final String ERROR = "error";
/*      */   public static final String WARNING = "warning";
/*      */   public static final String IGNORE = "ignore";
/*      */   public static final String ENABLED = "enabled";
/*      */   public static final String DISABLED = "disabled";
/*      */   public static final String PUBLIC = "public";
/*      */   public static final String PROTECTED = "protected";
/*      */   public static final String DEFAULT = "default";
/*      */   public static final String PRIVATE = "private";
/*      */   public static final String RETURN_TAG = "return_tag";
/*      */   public static final String NO_TAG = "no_tag";
/*      */   public static final String ALL_STANDARD_TAGS = "all_standard_tags";
/*      */   public static final int MethodWithConstructorName = 1;
/*      */   public static final int OverriddenPackageDefaultMethod = 2;
/*      */   public static final int UsingDeprecatedAPI = 4;
/*      */   public static final int MaskedCatchBlock = 8;
/*      */   public static final int UnusedLocalVariable = 16;
/*      */   public static final int UnusedArgument = 32;
/*      */   public static final int NoImplicitStringConversion = 64;
/*      */   public static final int AccessEmulation = 128;
/*      */   public static final int NonExternalizedString = 256;
/*      */   public static final int AssertUsedAsAnIdentifier = 512;
/*      */   public static final int UnusedImport = 1024;
/*      */   public static final int NonStaticAccessToStatic = 2048;
/*      */   public static final int Task = 4096;
/*      */   public static final int NoEffectAssignment = 8192;
/*      */   public static final int IncompatibleNonInheritedInterfaceMethod = 16384;
/*      */   public static final int UnusedPrivateMember = 32768;
/*      */   public static final int LocalVariableHiding = 65536;
/*      */   public static final int FieldHiding = 131072;
/*      */   public static final int AccidentalBooleanAssign = 262144;
/*      */   public static final int EmptyStatement = 524288;
/*      */   public static final int MissingJavadocComments = 1048576;
/*      */   public static final int MissingJavadocTags = 2097152;
/*      */   public static final int UnqualifiedFieldAccess = 4194304;
/*      */   public static final int UnusedDeclaredThrownException = 8388608;
/*      */   public static final int FinallyBlockNotCompleting = 16777216;
/*      */   public static final int InvalidJavadoc = 33554432;
/*      */   public static final int UnnecessaryTypeCheck = 67108864;
/*      */   public static final int UndocumentedEmptyBlock = 134217728;
/*      */   public static final int IndirectStaticAccess = 268435456;
/*      */   public static final int UnnecessaryElse = 536870913;
/*      */   public static final int UncheckedTypeOperation = 536870914;
/*      */   public static final int FinalParameterBound = 536870916;
/*      */   public static final int MissingSerialVersion = 536870920;
/*      */   public static final int EnumUsedAsAnIdentifier = 536870928;
/*      */   public static final int ForbiddenReference = 536870944;
/*      */   public static final int VarargsArgumentNeedCast = 536870976;
/*      */   public static final int NullReference = 536871040;
/*      */   public static final int AutoBoxing = 536871168;
/*      */   public static final int AnnotationSuperInterface = 536871424;
/*      */   public static final int TypeHiding = 536871936;
/*      */   public static final int MissingOverrideAnnotation = 536872960;
/*      */   public static final int IncompleteEnumSwitch = 536875008;
/*      */   public static final int MissingDeprecatedAnnotation = 536879104;
/*      */   public static final int DiscouragedReference = 536887296;
/*      */   public static final int UnhandledWarningToken = 536903680;
/*      */   public static final int RawTypeReference = 536936448;
/*      */   public static final int UnusedLabel = 537001984;
/*      */   public static final int ParameterAssignment = 537133056;
/*      */   public static final int FallthroughCase = 537395200;
/*      */   public static final int OverridingMethodWithoutSuperInvocation = 537919488;
/*      */   public static final int PotentialNullReference = 538968064;
/*      */   public static final int RedundantNullCheck = 541065216;
/*      */   public static final int MissingJavadocTagDescription = 545259520;
/*      */   public static final int UnusedTypeArguments = 553648128;
/*      */   public static final int UnusedWarningToken = 570425344;
/*      */   public static final int RedundantSuperinterface = 603979776;
/*      */   public static final int ComparingIdentical = 671088640;
/*      */   public static final int MissingSynchronizedModifierInInheritedMethod = 805306368;
/*      */   public static final int ShouldImplementHashcode = 1073741825;
/*      */   public static final int DeadCode = 1073741826;
/*      */   private static Map OptionToIrritants;
/*      */   protected IrritantSet errorThreshold;
/*      */   protected IrritantSet warningThreshold;
/*      */   public int produceDebugAttributes;
/*      */   public long complianceLevel;
/*      */   public long sourceLevel;
/*      */   public long targetJDK;
/*      */   public String defaultEncoding;
/*      */   public boolean verbose;
/*      */   public boolean produceReferenceInfo;
/*      */   public boolean preserveAllLocalVariables;
/*      */   public boolean parseLiteralExpressionsAsConstants;
/*      */   public int maxProblemsPerUnit;
/*      */   public char[][] taskTags;
/*      */   public char[][] taskPriorites;
/*      */   public boolean isTaskCaseSensitive;
/*      */   public boolean reportDeprecationInsideDeprecatedCode;
/*      */   public boolean reportDeprecationWhenOverridingDeprecatedMethod;
/*      */   public boolean reportUnusedParameterWhenImplementingAbstract;
/*      */   public boolean reportUnusedParameterWhenOverridingConcrete;
/*      */   public boolean reportUnusedParameterIncludeDocCommentReference;
/*      */   public boolean reportUnusedDeclaredThrownExceptionWhenOverriding;
/*      */   public boolean reportUnusedDeclaredThrownExceptionIncludeDocCommentReference;
/*      */   public boolean reportUnusedDeclaredThrownExceptionExemptExceptionAndThrowable;
/*      */   public boolean reportSpecialParameterHidingField;
/*      */   public boolean reportDeadCodeInTrivialIfStatement;
/*      */   public boolean docCommentSupport;
/*      */   public boolean reportInvalidJavadocTags;
/*      */   public int reportInvalidJavadocTagsVisibility;
/*      */   public boolean reportInvalidJavadocTagsDeprecatedRef;
/*      */   public boolean reportInvalidJavadocTagsNotVisibleRef;
/*      */   public String reportMissingJavadocTagDescription;
/*      */   public int reportMissingJavadocTagsVisibility;
/*      */   public boolean reportMissingJavadocTagsOverriding;
/*      */   public int reportMissingJavadocCommentsVisibility;
/*      */   public boolean reportMissingJavadocCommentsOverriding;
/*      */   public boolean inlineJsrBytecode;
/*      */   public boolean suppressWarnings;
/*      */   public boolean treatOptionalErrorAsFatal;
/*      */   public boolean performMethodsFullRecovery;
/*      */   public boolean performStatementsRecovery;
/*      */   public boolean processAnnotations;
/*      */   public boolean storeAnnotations;
/*      */   public boolean generateClassFiles;
/*      */   public boolean ignoreMethodBodies;
/*  339 */   public static final String[] warningTokens = { 
/*  340 */     "all", 
/*  341 */     "boxing", 
/*  342 */     "cast", 
/*  343 */     "dep-ann", 
/*  344 */     "deprecation", 
/*  345 */     "fallthrough", 
/*  346 */     "finally", 
/*  347 */     "hiding", 
/*  348 */     "incomplete-switch", 
/*  349 */     "nls", 
/*  350 */     "null", 
/*  351 */     "restriction", 
/*  352 */     "serial", 
/*  353 */     "static-access", 
/*  354 */     "super", 
/*  355 */     "synthetic-access", 
/*  356 */     "unchecked", 
/*  357 */     "unqualified-field-access", 
/*  358 */     "unused" };
/*      */ 
/*      */   public CompilerOptions()
/*      */   {
/*  365 */     this(null);
/*      */   }
/*      */ 
/*      */   public CompilerOptions(Map settings)
/*      */   {
/*  373 */     resetDefaults();
/*  374 */     if (settings != null)
/*  375 */       set(settings);
/*      */   }
/*      */ 
/*      */   /** @deprecated */
/*      */   public CompilerOptions(Map settings, boolean parseLiteralExpressionsAsConstants)
/*      */   {
/*  383 */     this(settings);
/*  384 */     this.parseLiteralExpressionsAsConstants = parseLiteralExpressionsAsConstants;
/*      */   }
/*      */ 
/*      */   public static String optionKeyFromIrritant(int irritant)
/*      */   {
/*  394 */     switch (irritant) {
/*      */     case 1:
/*  396 */       return "org.eclipse.jdt.core.compiler.problem.methodWithConstructorName";
/*      */     case 2:
/*  398 */       return "org.eclipse.jdt.core.compiler.problem.overridingPackageDefaultMethod";
/*      */     case 4:
/*      */     case 33554436:
/*  401 */       return "org.eclipse.jdt.core.compiler.problem.deprecation";
/*      */     case 8:
/*  403 */       return "org.eclipse.jdt.core.compiler.problem.hiddenCatchBlock";
/*      */     case 16:
/*  405 */       return "org.eclipse.jdt.core.compiler.problem.unusedLocal";
/*      */     case 32:
/*  407 */       return "org.eclipse.jdt.core.compiler.problem.unusedParameter";
/*      */     case 64:
/*  409 */       return "org.eclipse.jdt.core.compiler.problem.noImplicitStringConversion";
/*      */     case 128:
/*  411 */       return "org.eclipse.jdt.core.compiler.problem.syntheticAccessEmulation";
/*      */     case 256:
/*  413 */       return "org.eclipse.jdt.core.compiler.problem.nonExternalizedStringLiteral";
/*      */     case 512:
/*  415 */       return "org.eclipse.jdt.core.compiler.problem.assertIdentifier";
/*      */     case 1024:
/*  417 */       return "org.eclipse.jdt.core.compiler.problem.unusedImport";
/*      */     case 2048:
/*  419 */       return "org.eclipse.jdt.core.compiler.problem.staticAccessReceiver";
/*      */     case 4096:
/*  421 */       return "org.eclipse.jdt.core.compiler.taskTags";
/*      */     case 8192:
/*  423 */       return "org.eclipse.jdt.core.compiler.problem.noEffectAssignment";
/*      */     case 16384:
/*  425 */       return "org.eclipse.jdt.core.compiler.problem.incompatibleNonInheritedInterfaceMethod";
/*      */     case 32768:
/*  427 */       return "org.eclipse.jdt.core.compiler.problem.unusedPrivateMember";
/*      */     case 65536:
/*  429 */       return "org.eclipse.jdt.core.compiler.problem.localVariableHiding";
/*      */     case 131072:
/*  431 */       return "org.eclipse.jdt.core.compiler.problem.fieldHiding";
/*      */     case 262144:
/*  433 */       return "org.eclipse.jdt.core.compiler.problem.possibleAccidentalBooleanAssignment";
/*      */     case 524288:
/*  435 */       return "org.eclipse.jdt.core.compiler.problem.emptyStatement";
/*      */     case 1048576:
/*  437 */       return "org.eclipse.jdt.core.compiler.problem.missingJavadocComments";
/*      */     case 2097152:
/*  439 */       return "org.eclipse.jdt.core.compiler.problem.missingJavadocTags";
/*      */     case 4194304:
/*  441 */       return "org.eclipse.jdt.core.compiler.problem.unqualifiedFieldAccess";
/*      */     case 8388608:
/*  443 */       return "org.eclipse.jdt.core.compiler.problem.unusedDeclaredThrownExceptionWhenOverriding";
/*      */     case 16777216:
/*  445 */       return "org.eclipse.jdt.core.compiler.problem.finallyBlockNotCompletingNormally";
/*      */     case 33554432:
/*  447 */       return "org.eclipse.jdt.core.compiler.problem.invalidJavadoc";
/*      */     case 67108864:
/*  449 */       return "org.eclipse.jdt.core.compiler.problem.unnecessaryTypeCheck";
/*      */     case 134217728:
/*  451 */       return "org.eclipse.jdt.core.compiler.problem.undocumentedEmptyBlock";
/*      */     case 268435456:
/*  453 */       return "org.eclipse.jdt.core.compiler.problem.indirectStaticAccess";
/*      */     case 536870913:
/*  455 */       return "org.eclipse.jdt.core.compiler.problem.unnecessaryElse";
/*      */     case 536870914:
/*  457 */       return "org.eclipse.jdt.core.compiler.problem.uncheckedTypeOperation";
/*      */     case 536870916:
/*  459 */       return "org.eclipse.jdt.core.compiler.problem.finalParameterBound";
/*      */     case 536870920:
/*  461 */       return "org.eclipse.jdt.core.compiler.problem.missingSerialVersion";
/*      */     case 536870928:
/*  463 */       return "org.eclipse.jdt.core.compiler.problem.enumIdentifier";
/*      */     case 536870944:
/*  465 */       return "org.eclipse.jdt.core.compiler.problem.forbiddenReference";
/*      */     case 536870976:
/*  467 */       return "org.eclipse.jdt.core.compiler.problem.varargsArgumentNeedCast";
/*      */     case 536871040:
/*  469 */       return "org.eclipse.jdt.core.compiler.problem.nullReference";
/*      */     case 538968064:
/*  471 */       return "org.eclipse.jdt.core.compiler.problem.potentialNullReference";
/*      */     case 541065216:
/*  473 */       return "org.eclipse.jdt.core.compiler.problem.redundantNullCheck";
/*      */     case 536871168:
/*  475 */       return "org.eclipse.jdt.core.compiler.problem.autoboxing";
/*      */     case 536871424:
/*  477 */       return "org.eclipse.jdt.core.compiler.problem.annotationSuperInterface";
/*      */     case 536871936:
/*  479 */       return "org.eclipse.jdt.core.compiler.problem.typeParameterHiding";
/*      */     case 536872960:
/*  481 */       return "org.eclipse.jdt.core.compiler.problem.missingOverrideAnnotation";
/*      */     case 536875008:
/*  483 */       return "org.eclipse.jdt.core.compiler.problem.incompleteEnumSwitch";
/*      */     case 536879104:
/*  485 */       return "org.eclipse.jdt.core.compiler.problem.missingDeprecatedAnnotation";
/*      */     case 536887296:
/*  487 */       return "org.eclipse.jdt.core.compiler.problem.discouragedReference";
/*      */     case 536903680:
/*  489 */       return "org.eclipse.jdt.core.compiler.problem.unhandledWarningToken";
/*      */     case 536936448:
/*  491 */       return "org.eclipse.jdt.core.compiler.problem.rawTypeReference";
/*      */     case 537001984:
/*  493 */       return "org.eclipse.jdt.core.compiler.problem.unusedLabel";
/*      */     case 537133056:
/*  495 */       return "org.eclipse.jdt.core.compiler.problem.parameterAssignment";
/*      */     case 537395200:
/*  497 */       return "org.eclipse.jdt.core.compiler.problem.fallthroughCase";
/*      */     case 537919488:
/*  499 */       return "org.eclipse.jdt.core.compiler.problem.overridingMethodWithoutSuperInvocation";
/*      */     case 545259520:
/*  501 */       return "org.eclipse.jdt.core.compiler.problem.missingJavadocTagDescription";
/*      */     case 553648128:
/*  503 */       return "org.eclipse.jdt.core.compiler.problem.unusedTypeArgumentsForMethodInvocation";
/*      */     case 570425344:
/*  505 */       return "org.eclipse.jdt.core.compiler.problem.unusedWarningToken";
/*      */     case 603979776:
/*  507 */       return "org.eclipse.jdt.core.compiler.problem.redundantSuperinterface";
/*      */     case 671088640:
/*  509 */       return "org.eclipse.jdt.core.compiler.problem.comparingIdentical";
/*      */     case 805306368:
/*  511 */       return "org.eclipse.jdt.core.compiler.problem.missingSynchronizedOnInheritedMethod";
/*      */     case 1073741825:
/*  513 */       return "org.eclipse.jdt.core.compiler.problem.missingHashCodeMethod";
/*      */     case 1073741826:
/*  515 */       return "org.eclipse.jdt.core.compiler.problem.deadCode";
/*      */     }
/*  517 */     return null;
/*      */   }
/*      */ 
/*      */   public static long optionKeyToIrritant(String optionName) {
/*  521 */     if (OptionToIrritants == null) {
/*  522 */       Map temp = new HashMap();
/*  523 */       int group = 0;
/*  524 */       for (int g = 0; g < 8; g++) {
/*  525 */         group <<= 1;
/*  526 */         int index = 0;
/*  527 */         for (int i = 0; i < 30; i++) {
/*  528 */           index <<= 1;
/*  529 */           int irritant = (group << 29) + index;
/*  530 */           String optionKey = optionKeyFromIrritant(irritant);
/*  531 */           if (optionKey != null)
/*  532 */             temp.put(optionKey, new Integer(irritant));
/*      */         }
/*      */       }
/*  535 */       OptionToIrritants = temp;
/*      */     }
/*  537 */     Long irritant = (Long)OptionToIrritants.get(optionName);
/*  538 */     return irritant == null ? 0L : irritant.longValue();
/*      */   }
/*      */ 
/*      */   public static String versionFromJdkLevel(long jdkLevel) {
/*  542 */     switch ((int)(jdkLevel >> 16)) {
/*      */     case 45:
/*  544 */       if (jdkLevel != 2949123L) break;
/*  545 */       return "1.1";
/*      */     case 46:
/*  548 */       if (jdkLevel != 3014656L) break;
/*  549 */       return "1.2";
/*      */     case 47:
/*  552 */       if (jdkLevel != 3080192L) break;
/*  553 */       return "1.3";
/*      */     case 48:
/*  556 */       if (jdkLevel != 3145728L) break;
/*  557 */       return "1.4";
/*      */     case 49:
/*  560 */       if (jdkLevel != 3211264L) break;
/*  561 */       return "1.5";
/*      */     case 50:
/*  564 */       if (jdkLevel != 3276800L) break;
/*  565 */       return "1.6";
/*      */     case 51:
/*  568 */       if (jdkLevel != 3342336L) break;
/*  569 */       return "1.7";
/*      */     }
/*      */ 
/*  572 */     return Util.EMPTY_STRING;
/*      */   }
/*      */ 
/*      */   public static long versionToJdkLevel(Object versionID) {
/*  576 */     if ((versionID instanceof String)) {
/*  577 */       String version = (String)versionID;
/*      */ 
/*  579 */       if ((version.length() == 3) && (version.charAt(0) == '1') && (version.charAt(1) == '.')) {
/*  580 */         switch (version.charAt(2)) {
/*      */         case '1':
/*  582 */           return 2949123L;
/*      */         case '2':
/*  584 */           return 3014656L;
/*      */         case '3':
/*  586 */           return 3080192L;
/*      */         case '4':
/*  588 */           return 3145728L;
/*      */         case '5':
/*  590 */           return 3211264L;
/*      */         case '6':
/*  592 */           return 3276800L;
/*      */         case '7':
/*  594 */           return 3342336L;
/*      */         }
/*  596 */         return 0L;
/*      */       }
/*      */ 
/*  599 */       if ("jsr14".equals(versionID)) {
/*  600 */         return 3145728L;
/*      */       }
/*  602 */       if ("cldc1.1".equals(versionID)) {
/*  603 */         return 2949124L;
/*      */       }
/*      */     }
/*  606 */     return 0L;
/*      */   }
/*      */ 
/*      */   public static String[] warningOptionNames()
/*      */   {
/*  615 */     String[] result = { 
/*  616 */       "org.eclipse.jdt.core.compiler.problem.annotationSuperInterface", 
/*  617 */       "org.eclipse.jdt.core.compiler.problem.assertIdentifier", 
/*  618 */       "org.eclipse.jdt.core.compiler.problem.autoboxing", 
/*  619 */       "org.eclipse.jdt.core.compiler.problem.deadCode", 
/*  620 */       "org.eclipse.jdt.core.compiler.problem.deprecation", 
/*  621 */       "org.eclipse.jdt.core.compiler.problem.discouragedReference", 
/*  622 */       "org.eclipse.jdt.core.compiler.problem.emptyStatement", 
/*  623 */       "org.eclipse.jdt.core.compiler.problem.enumIdentifier", 
/*  624 */       "org.eclipse.jdt.core.compiler.problem.fallthroughCase", 
/*  625 */       "org.eclipse.jdt.core.compiler.problem.fieldHiding", 
/*  626 */       "org.eclipse.jdt.core.compiler.problem.finalParameterBound", 
/*  627 */       "org.eclipse.jdt.core.compiler.problem.finallyBlockNotCompletingNormally", 
/*  628 */       "org.eclipse.jdt.core.compiler.problem.forbiddenReference", 
/*  629 */       "org.eclipse.jdt.core.compiler.problem.hiddenCatchBlock", 
/*  630 */       "org.eclipse.jdt.core.compiler.problem.incompatibleNonInheritedInterfaceMethod", 
/*  631 */       "org.eclipse.jdt.core.compiler.problem.incompleteEnumSwitch", 
/*  632 */       "org.eclipse.jdt.core.compiler.problem.indirectStaticAccess", 
/*  633 */       "org.eclipse.jdt.core.compiler.problem.invalidJavadoc", 
/*  634 */       "org.eclipse.jdt.core.compiler.problem.localVariableHiding", 
/*  635 */       "org.eclipse.jdt.core.compiler.problem.methodWithConstructorName", 
/*  636 */       "org.eclipse.jdt.core.compiler.problem.missingDeprecatedAnnotation", 
/*  637 */       "org.eclipse.jdt.core.compiler.problem.missingJavadocComments", 
/*  638 */       "org.eclipse.jdt.core.compiler.problem.missingJavadocTagDescription", 
/*  639 */       "org.eclipse.jdt.core.compiler.problem.missingJavadocTags", 
/*  640 */       "org.eclipse.jdt.core.compiler.problem.missingOverrideAnnotation", 
/*  641 */       "org.eclipse.jdt.core.compiler.problem.missingSerialVersion", 
/*  642 */       "org.eclipse.jdt.core.compiler.problem.noEffectAssignment", 
/*  643 */       "org.eclipse.jdt.core.compiler.problem.noImplicitStringConversion", 
/*  644 */       "org.eclipse.jdt.core.compiler.problem.nonExternalizedStringLiteral", 
/*  645 */       "org.eclipse.jdt.core.compiler.problem.staticAccessReceiver", 
/*  646 */       "org.eclipse.jdt.core.compiler.problem.nullReference", 
/*  647 */       "org.eclipse.jdt.core.compiler.problem.potentialNullReference", 
/*  648 */       "org.eclipse.jdt.core.compiler.problem.redundantNullCheck", 
/*  649 */       "org.eclipse.jdt.core.compiler.problem.redundantSuperinterface", 
/*  650 */       "org.eclipse.jdt.core.compiler.problem.overridingPackageDefaultMethod", 
/*  651 */       "org.eclipse.jdt.core.compiler.problem.parameterAssignment", 
/*  652 */       "org.eclipse.jdt.core.compiler.problem.possibleAccidentalBooleanAssignment", 
/*  653 */       "org.eclipse.jdt.core.compiler.problem.syntheticAccessEmulation", 
/*  654 */       "org.eclipse.jdt.core.compiler.problem.typeParameterHiding", 
/*  655 */       "org.eclipse.jdt.core.compiler.problem.uncheckedTypeOperation", 
/*  656 */       "org.eclipse.jdt.core.compiler.problem.undocumentedEmptyBlock", 
/*  657 */       "org.eclipse.jdt.core.compiler.problem.unnecessaryElse", 
/*  658 */       "org.eclipse.jdt.core.compiler.problem.unnecessaryTypeCheck", 
/*  659 */       "org.eclipse.jdt.core.compiler.problem.unqualifiedFieldAccess", 
/*  660 */       "org.eclipse.jdt.core.compiler.problem.unusedDeclaredThrownException", 
/*  661 */       "org.eclipse.jdt.core.compiler.problem.unusedImport", 
/*  662 */       "org.eclipse.jdt.core.compiler.problem.unusedLocal", 
/*  663 */       "org.eclipse.jdt.core.compiler.problem.unusedParameter", 
/*  664 */       "org.eclipse.jdt.core.compiler.problem.unusedPrivateMember", 
/*  665 */       "org.eclipse.jdt.core.compiler.problem.varargsArgumentNeedCast", 
/*  666 */       "org.eclipse.jdt.core.compiler.problem.unhandledWarningToken", 
/*  667 */       "org.eclipse.jdt.core.compiler.problem.unusedWarningToken", 
/*  668 */       "org.eclipse.jdt.core.compiler.problem.overridingMethodWithoutSuperInvocation", 
/*  669 */       "org.eclipse.jdt.core.compiler.problem.unusedTypeArgumentsForMethodInvocation" };
/*      */ 
/*  671 */     return result;
/*      */   }
/*      */ 
/*      */   public static String warningTokenFromIrritant(int irritant)
/*      */   {
/*  679 */     switch (irritant) {
/*      */     case 4:
/*      */     case 33554436:
/*  682 */       return "deprecation";
/*      */     case 16777216:
/*  684 */       return "finally";
/*      */     case 8:
/*      */     case 65536:
/*      */     case 131072:
/*  688 */       return "hiding";
/*      */     case 256:
/*  690 */       return "nls";
/*      */     case 67108864:
/*  692 */       return "cast";
/*      */     case 16:
/*      */     case 32:
/*      */     case 1024:
/*      */     case 32768:
/*      */     case 8388608:
/*  698 */       return "unused";
/*      */     case 2048:
/*      */     case 268435456:
/*  701 */       return "static-access";
/*      */     case 128:
/*  703 */       return "synthetic-access";
/*      */     case 4194304:
/*  705 */       return "unqualified-field-access";
/*      */     case 536870914:
/*  707 */       return "unchecked";
/*      */     case 536870920:
/*  709 */       return "serial";
/*      */     case 536871168:
/*  711 */       return "boxing";
/*      */     case 536871936:
/*  713 */       return "hiding";
/*      */     case 536875008:
/*  715 */       return "incomplete-switch";
/*      */     case 536879104:
/*  717 */       return "dep-ann";
/*      */     case 536936448:
/*  719 */       return "unchecked";
/*      */     case 537001984:
/*      */     case 553648128:
/*      */     case 603979776:
/*  723 */       return "unused";
/*      */     case 536870944:
/*      */     case 536887296:
/*  726 */       return "restriction";
/*      */     case 536871040:
/*      */     case 538968064:
/*      */     case 541065216:
/*  730 */       return "null";
/*      */     case 537395200:
/*  732 */       return "fallthrough";
/*      */     case 537919488:
/*  734 */       return "super";
/*      */     }
/*  736 */     return null;
/*      */   }
/*      */ 
/*      */   public static IrritantSet warningTokenToIrritants(String warningToken)
/*      */   {
/*  741 */     if ((warningToken == null) || (warningToken.length() == 0)) return null;
/*  742 */     switch (warningToken.charAt(0)) {
/*      */     case 'a':
/*  744 */       if (!"all".equals(warningToken)) break;
/*  745 */       return IrritantSet.ALL;
/*      */     case 'b':
/*  748 */       if (!"boxing".equals(warningToken)) break;
/*  749 */       return IrritantSet.BOXING;
/*      */     case 'c':
/*  752 */       if (!"cast".equals(warningToken)) break;
/*  753 */       return IrritantSet.CAST;
/*      */     case 'd':
/*  756 */       if ("deprecation".equals(warningToken))
/*  757 */         return IrritantSet.DEPRECATION;
/*  758 */       if (!"dep-ann".equals(warningToken)) break;
/*  759 */       return IrritantSet.DEP_ANN;
/*      */     case 'f':
/*  762 */       if ("fallthrough".equals(warningToken))
/*  763 */         return IrritantSet.FALLTHROUGH;
/*  764 */       if (!"finally".equals(warningToken)) break;
/*  765 */       return IrritantSet.FINALLY;
/*      */     case 'h':
/*  768 */       if (!"hiding".equals(warningToken)) break;
/*  769 */       return IrritantSet.HIDING;
/*      */     case 'i':
/*  772 */       if (!"incomplete-switch".equals(warningToken)) break;
/*  773 */       return IrritantSet.INCOMPLETE_SWITCH;
/*      */     case 'n':
/*  776 */       if ("nls".equals(warningToken))
/*  777 */         return IrritantSet.NLS;
/*  778 */       if (!"null".equals(warningToken)) break;
/*  779 */       return IrritantSet.NULL;
/*      */     case 'r':
/*  782 */       if (!"restriction".equals(warningToken)) break;
/*  783 */       return IrritantSet.RESTRICTION;
/*      */     case 's':
/*  786 */       if ("serial".equals(warningToken))
/*  787 */         return IrritantSet.SERIAL;
/*  788 */       if ("static-access".equals(warningToken))
/*  789 */         return IrritantSet.STATIC_ACCESS;
/*  790 */       if ("synthetic-access".equals(warningToken))
/*  791 */         return IrritantSet.SYNTHETIC_ACCESS;
/*  792 */       if (!"super".equals(warningToken)) break;
/*  793 */       return IrritantSet.SUPER;
/*      */     case 'u':
/*  797 */       if ("unused".equals(warningToken))
/*  798 */         return IrritantSet.UNUSED;
/*  799 */       if ("unchecked".equals(warningToken))
/*  800 */         return IrritantSet.UNCHECKED;
/*  801 */       if (!"unqualified-field-access".equals(warningToken)) break;
/*  802 */       return IrritantSet.UNQUALIFIED_FIELD_ACCESS;
/*      */     case 'e':
/*      */     case 'g':
/*      */     case 'j':
/*      */     case 'k':
/*      */     case 'l':
/*      */     case 'm':
/*      */     case 'o':
/*      */     case 'p':
/*      */     case 'q':
/*  805 */     case 't': } return null;
/*      */   }
/*      */ 
/*      */   public Map getMap()
/*      */   {
/*  810 */     Map optionsMap = new HashMap(30);
/*  811 */     optionsMap.put("org.eclipse.jdt.core.compiler.debug.localVariable", (this.produceDebugAttributes & 0x4) != 0 ? "generate" : "do not generate");
/*  812 */     optionsMap.put("org.eclipse.jdt.core.compiler.debug.lineNumber", (this.produceDebugAttributes & 0x2) != 0 ? "generate" : "do not generate");
/*  813 */     optionsMap.put("org.eclipse.jdt.core.compiler.debug.sourceFile", (this.produceDebugAttributes & 0x1) != 0 ? "generate" : "do not generate");
/*  814 */     optionsMap.put("org.eclipse.jdt.core.compiler.codegen.unusedLocal", this.preserveAllLocalVariables ? "preserve" : "optimize out");
/*  815 */     optionsMap.put("org.eclipse.jdt.core.compiler.doc.comment.support", this.docCommentSupport ? "enabled" : "disabled");
/*  816 */     optionsMap.put("org.eclipse.jdt.core.compiler.problem.methodWithConstructorName", getSeverityString(1));
/*  817 */     optionsMap.put("org.eclipse.jdt.core.compiler.problem.overridingPackageDefaultMethod", getSeverityString(2));
/*  818 */     optionsMap.put("org.eclipse.jdt.core.compiler.problem.deprecation", getSeverityString(4));
/*  819 */     optionsMap.put("org.eclipse.jdt.core.compiler.problem.deprecationInDeprecatedCode", this.reportDeprecationInsideDeprecatedCode ? "enabled" : "disabled");
/*  820 */     optionsMap.put("org.eclipse.jdt.core.compiler.problem.deprecationWhenOverridingDeprecatedMethod", this.reportDeprecationWhenOverridingDeprecatedMethod ? "enabled" : "disabled");
/*  821 */     optionsMap.put("org.eclipse.jdt.core.compiler.problem.hiddenCatchBlock", getSeverityString(8));
/*  822 */     optionsMap.put("org.eclipse.jdt.core.compiler.problem.unusedLocal", getSeverityString(16));
/*  823 */     optionsMap.put("org.eclipse.jdt.core.compiler.problem.unusedParameter", getSeverityString(32));
/*  824 */     optionsMap.put("org.eclipse.jdt.core.compiler.problem.unusedImport", getSeverityString(1024));
/*  825 */     optionsMap.put("org.eclipse.jdt.core.compiler.problem.syntheticAccessEmulation", getSeverityString(128));
/*  826 */     optionsMap.put("org.eclipse.jdt.core.compiler.problem.noEffectAssignment", getSeverityString(8192));
/*  827 */     optionsMap.put("org.eclipse.jdt.core.compiler.problem.nonExternalizedStringLiteral", getSeverityString(256));
/*  828 */     optionsMap.put("org.eclipse.jdt.core.compiler.problem.noImplicitStringConversion", getSeverityString(64));
/*  829 */     optionsMap.put("org.eclipse.jdt.core.compiler.problem.staticAccessReceiver", getSeverityString(2048));
/*  830 */     optionsMap.put("org.eclipse.jdt.core.compiler.problem.indirectStaticAccess", getSeverityString(268435456));
/*  831 */     optionsMap.put("org.eclipse.jdt.core.compiler.problem.incompatibleNonInheritedInterfaceMethod", getSeverityString(16384));
/*  832 */     optionsMap.put("org.eclipse.jdt.core.compiler.problem.unusedPrivateMember", getSeverityString(32768));
/*  833 */     optionsMap.put("org.eclipse.jdt.core.compiler.problem.localVariableHiding", getSeverityString(65536));
/*  834 */     optionsMap.put("org.eclipse.jdt.core.compiler.problem.fieldHiding", getSeverityString(131072));
/*  835 */     optionsMap.put("org.eclipse.jdt.core.compiler.problem.typeParameterHiding", getSeverityString(536871936));
/*  836 */     optionsMap.put("org.eclipse.jdt.core.compiler.problem.possibleAccidentalBooleanAssignment", getSeverityString(262144));
/*  837 */     optionsMap.put("org.eclipse.jdt.core.compiler.problem.emptyStatement", getSeverityString(524288));
/*  838 */     optionsMap.put("org.eclipse.jdt.core.compiler.problem.assertIdentifier", getSeverityString(512));
/*  839 */     optionsMap.put("org.eclipse.jdt.core.compiler.problem.enumIdentifier", getSeverityString(536870928));
/*  840 */     optionsMap.put("org.eclipse.jdt.core.compiler.problem.undocumentedEmptyBlock", getSeverityString(134217728));
/*  841 */     optionsMap.put("org.eclipse.jdt.core.compiler.problem.unnecessaryTypeCheck", getSeverityString(67108864));
/*  842 */     optionsMap.put("org.eclipse.jdt.core.compiler.problem.unnecessaryElse", getSeverityString(536870913));
/*  843 */     optionsMap.put("org.eclipse.jdt.core.compiler.problem.autoboxing", getSeverityString(536871168));
/*  844 */     optionsMap.put("org.eclipse.jdt.core.compiler.problem.annotationSuperInterface", getSeverityString(536871424));
/*  845 */     optionsMap.put("org.eclipse.jdt.core.compiler.problem.incompleteEnumSwitch", getSeverityString(536875008));
/*  846 */     optionsMap.put("org.eclipse.jdt.core.compiler.problem.invalidJavadoc", getSeverityString(33554432));
/*  847 */     optionsMap.put("org.eclipse.jdt.core.compiler.problem.invalidJavadocTagsVisibility", getVisibilityString(this.reportInvalidJavadocTagsVisibility));
/*  848 */     optionsMap.put("org.eclipse.jdt.core.compiler.problem.invalidJavadocTags", this.reportInvalidJavadocTags ? "enabled" : "disabled");
/*  849 */     optionsMap.put("org.eclipse.jdt.core.compiler.problem.invalidJavadocTagsDeprecatedRef", this.reportInvalidJavadocTagsDeprecatedRef ? "enabled" : "disabled");
/*  850 */     optionsMap.put("org.eclipse.jdt.core.compiler.problem.invalidJavadocTagsNotVisibleRef", this.reportInvalidJavadocTagsNotVisibleRef ? "enabled" : "disabled");
/*  851 */     optionsMap.put("org.eclipse.jdt.core.compiler.problem.missingJavadocTags", getSeverityString(2097152));
/*  852 */     optionsMap.put("org.eclipse.jdt.core.compiler.problem.missingJavadocTagsVisibility", getVisibilityString(this.reportMissingJavadocTagsVisibility));
/*  853 */     optionsMap.put("org.eclipse.jdt.core.compiler.problem.missingJavadocTagsOverriding", this.reportMissingJavadocTagsOverriding ? "enabled" : "disabled");
/*  854 */     optionsMap.put("org.eclipse.jdt.core.compiler.problem.missingJavadocComments", getSeverityString(1048576));
/*  855 */     optionsMap.put("org.eclipse.jdt.core.compiler.problem.missingJavadocTagDescription", this.reportMissingJavadocTagDescription);
/*  856 */     optionsMap.put("org.eclipse.jdt.core.compiler.problem.missingJavadocCommentsVisibility", getVisibilityString(this.reportMissingJavadocCommentsVisibility));
/*  857 */     optionsMap.put("org.eclipse.jdt.core.compiler.problem.missingJavadocCommentsOverriding", this.reportMissingJavadocCommentsOverriding ? "enabled" : "disabled");
/*  858 */     optionsMap.put("org.eclipse.jdt.core.compiler.problem.finallyBlockNotCompletingNormally", getSeverityString(16777216));
/*  859 */     optionsMap.put("org.eclipse.jdt.core.compiler.problem.unusedDeclaredThrownException", getSeverityString(8388608));
/*  860 */     optionsMap.put("org.eclipse.jdt.core.compiler.problem.unusedDeclaredThrownExceptionWhenOverriding", this.reportUnusedDeclaredThrownExceptionWhenOverriding ? "enabled" : "disabled");
/*  861 */     optionsMap.put("org.eclipse.jdt.core.compiler.problem.unusedDeclaredThrownExceptionIncludeDocCommentReference", this.reportUnusedDeclaredThrownExceptionIncludeDocCommentReference ? "enabled" : "disabled");
/*  862 */     optionsMap.put("org.eclipse.jdt.core.compiler.problem.unusedDeclaredThrownExceptionExemptExceptionAndThrowable", this.reportUnusedDeclaredThrownExceptionExemptExceptionAndThrowable ? "enabled" : "disabled");
/*  863 */     optionsMap.put("org.eclipse.jdt.core.compiler.problem.unqualifiedFieldAccess", getSeverityString(4194304));
/*  864 */     optionsMap.put("org.eclipse.jdt.core.compiler.problem.uncheckedTypeOperation", getSeverityString(536870914));
/*  865 */     optionsMap.put("org.eclipse.jdt.core.compiler.problem.rawTypeReference", getSeverityString(536936448));
/*  866 */     optionsMap.put("org.eclipse.jdt.core.compiler.problem.finalParameterBound", getSeverityString(536870916));
/*  867 */     optionsMap.put("org.eclipse.jdt.core.compiler.problem.missingSerialVersion", getSeverityString(536870920));
/*  868 */     optionsMap.put("org.eclipse.jdt.core.compiler.problem.forbiddenReference", getSeverityString(536870944));
/*  869 */     optionsMap.put("org.eclipse.jdt.core.compiler.problem.discouragedReference", getSeverityString(536887296));
/*  870 */     optionsMap.put("org.eclipse.jdt.core.compiler.problem.varargsArgumentNeedCast", getSeverityString(536870976));
/*  871 */     optionsMap.put("org.eclipse.jdt.core.compiler.problem.missingOverrideAnnotation", getSeverityString(536872960));
/*  872 */     optionsMap.put("org.eclipse.jdt.core.compiler.problem.missingDeprecatedAnnotation", getSeverityString(536879104));
/*  873 */     optionsMap.put("org.eclipse.jdt.core.compiler.problem.incompleteEnumSwitch", getSeverityString(536875008));
/*  874 */     optionsMap.put("org.eclipse.jdt.core.compiler.problem.unusedLabel", getSeverityString(537001984));
/*  875 */     optionsMap.put("org.eclipse.jdt.core.compiler.problem.unusedTypeArgumentsForMethodInvocation", getSeverityString(553648128));
/*  876 */     optionsMap.put("org.eclipse.jdt.core.compiler.compliance", versionFromJdkLevel(this.complianceLevel));
/*  877 */     optionsMap.put("org.eclipse.jdt.core.compiler.source", versionFromJdkLevel(this.sourceLevel));
/*  878 */     optionsMap.put("org.eclipse.jdt.core.compiler.codegen.targetPlatform", versionFromJdkLevel(this.targetJDK));
/*  879 */     optionsMap.put("org.eclipse.jdt.core.compiler.problem.fatalOptionalError", this.treatOptionalErrorAsFatal ? "enabled" : "disabled");
/*  880 */     if (this.defaultEncoding != null) {
/*  881 */       optionsMap.put("org.eclipse.jdt.core.encoding", this.defaultEncoding);
/*      */     }
/*  883 */     optionsMap.put("org.eclipse.jdt.core.compiler.taskTags", this.taskTags == null ? Util.EMPTY_STRING : new String(CharOperation.concatWith(this.taskTags, ',')));
/*  884 */     optionsMap.put("org.eclipse.jdt.core.compiler.taskPriorities", this.taskPriorites == null ? Util.EMPTY_STRING : new String(CharOperation.concatWith(this.taskPriorites, ',')));
/*  885 */     optionsMap.put("org.eclipse.jdt.core.compiler.taskCaseSensitive", this.isTaskCaseSensitive ? "enabled" : "disabled");
/*  886 */     optionsMap.put("org.eclipse.jdt.core.compiler.problem.unusedParameterWhenImplementingAbstract", this.reportUnusedParameterWhenImplementingAbstract ? "enabled" : "disabled");
/*  887 */     optionsMap.put("org.eclipse.jdt.core.compiler.problem.unusedParameterWhenOverridingConcrete", this.reportUnusedParameterWhenOverridingConcrete ? "enabled" : "disabled");
/*  888 */     optionsMap.put("org.eclipse.jdt.core.compiler.problem.unusedParameterIncludeDocCommentReference", this.reportUnusedParameterIncludeDocCommentReference ? "enabled" : "disabled");
/*  889 */     optionsMap.put("org.eclipse.jdt.core.compiler.problem.specialParameterHidingField", this.reportSpecialParameterHidingField ? "enabled" : "disabled");
/*  890 */     optionsMap.put("org.eclipse.jdt.core.compiler.maxProblemPerUnit", String.valueOf(this.maxProblemsPerUnit));
/*  891 */     optionsMap.put("org.eclipse.jdt.core.compiler.codegen.inlineJsrBytecode", this.inlineJsrBytecode ? "enabled" : "disabled");
/*  892 */     optionsMap.put("org.eclipse.jdt.core.compiler.problem.nullReference", getSeverityString(536871040));
/*  893 */     optionsMap.put("org.eclipse.jdt.core.compiler.problem.potentialNullReference", getSeverityString(538968064));
/*  894 */     optionsMap.put("org.eclipse.jdt.core.compiler.problem.redundantNullCheck", getSeverityString(541065216));
/*  895 */     optionsMap.put("org.eclipse.jdt.core.compiler.problem.suppressWarnings", this.suppressWarnings ? "enabled" : "disabled");
/*  896 */     optionsMap.put("org.eclipse.jdt.core.compiler.problem.unhandledWarningToken", getSeverityString(536903680));
/*  897 */     optionsMap.put("org.eclipse.jdt.core.compiler.problem.unusedWarningToken", getSeverityString(570425344));
/*  898 */     optionsMap.put("org.eclipse.jdt.core.compiler.problem.parameterAssignment", getSeverityString(537133056));
/*  899 */     optionsMap.put("org.eclipse.jdt.core.compiler.problem.fallthroughCase", getSeverityString(537395200));
/*  900 */     optionsMap.put("org.eclipse.jdt.core.compiler.problem.overridingMethodWithoutSuperInvocation", getSeverityString(537919488));
/*  901 */     optionsMap.put("org.eclipse.jdt.core.compiler.generateClassFiles", this.generateClassFiles ? "enabled" : "disabled");
/*  902 */     optionsMap.put("org.eclipse.jdt.core.compiler.processAnnotations", this.processAnnotations ? "enabled" : "disabled");
/*  903 */     optionsMap.put("org.eclipse.jdt.core.compiler.problem.redundantSuperinterface", getSeverityString(603979776));
/*  904 */     optionsMap.put("org.eclipse.jdt.core.compiler.problem.comparingIdentical", getSeverityString(671088640));
/*  905 */     optionsMap.put("org.eclipse.jdt.core.compiler.problem.missingSynchronizedOnInheritedMethod", getSeverityString(805306368));
/*  906 */     optionsMap.put("org.eclipse.jdt.core.compiler.problem.missingHashCodeMethod", getSeverityString(1073741825));
/*  907 */     optionsMap.put("org.eclipse.jdt.core.compiler.problem.deadCode", getSeverityString(1073741826));
/*  908 */     optionsMap.put("org.eclipse.jdt.core.compiler.problem.deadCodeInTrivialIfStatement", this.reportDeadCodeInTrivialIfStatement ? "enabled" : "disabled");
/*  909 */     return optionsMap;
/*      */   }
/*      */ 
/*      */   public int getSeverity(int irritant) {
/*  913 */     if (this.errorThreshold.isSet(irritant)) {
/*  914 */       if ((irritant & 0xE2000000) == 570425344) {
/*  915 */         return 33;
/*      */       }
/*  917 */       return this.treatOptionalErrorAsFatal ? 
/*  918 */         161 : 
/*  919 */         33;
/*      */     }
/*  921 */     if (this.warningThreshold.isSet(irritant)) {
/*  922 */       return 32;
/*      */     }
/*  924 */     return -1;
/*      */   }
/*      */ 
/*      */   public String getSeverityString(int irritant) {
/*  928 */     if (this.errorThreshold.isSet(irritant))
/*  929 */       return "error";
/*  930 */     if (this.warningThreshold.isSet(irritant))
/*  931 */       return "warning";
/*  932 */     return "ignore";
/*      */   }
/*      */   public String getVisibilityString(int level) {
/*  935 */     switch (level & 0x7) {
/*      */     case 1:
/*  937 */       return "public";
/*      */     case 4:
/*  939 */       return "protected";
/*      */     case 2:
/*  941 */       return "private";
/*      */     case 3:
/*  943 */     }return "default";
/*      */   }
/*      */ 
/*      */   public boolean isAnyEnabled(IrritantSet irritants)
/*      */   {
/*  948 */     return (this.warningThreshold.isAnySet(irritants)) || (this.errorThreshold.isAnySet(irritants));
/*      */   }
/*      */ 
/*      */   protected void resetDefaults()
/*      */   {
/*  953 */     this.errorThreshold = new IrritantSet(IrritantSet.COMPILER_DEFAULT_ERRORS);
/*  954 */     this.warningThreshold = new IrritantSet(IrritantSet.COMPILER_DEFAULT_WARNINGS);
/*      */ 
/*  957 */     this.produceDebugAttributes = 3;
/*  958 */     this.complianceLevel = 3145728L;
/*  959 */     this.sourceLevel = 3080192L;
/*  960 */     this.targetJDK = 3014656L;
/*      */ 
/*  962 */     this.defaultEncoding = null;
/*      */ 
/*  965 */     this.verbose = Compiler.DEBUG;
/*      */ 
/*  967 */     this.produceReferenceInfo = false;
/*      */ 
/*  970 */     this.preserveAllLocalVariables = false;
/*      */ 
/*  973 */     this.parseLiteralExpressionsAsConstants = true;
/*      */ 
/*  976 */     this.maxProblemsPerUnit = 100;
/*      */ 
/*  979 */     this.taskTags = null;
/*  980 */     this.taskPriorites = null;
/*  981 */     this.isTaskCaseSensitive = true;
/*      */ 
/*  984 */     this.reportDeprecationInsideDeprecatedCode = false;
/*  985 */     this.reportDeprecationWhenOverridingDeprecatedMethod = false;
/*      */ 
/*  988 */     this.reportUnusedParameterWhenImplementingAbstract = false;
/*  989 */     this.reportUnusedParameterWhenOverridingConcrete = false;
/*  990 */     this.reportUnusedParameterIncludeDocCommentReference = true;
/*      */ 
/*  993 */     this.reportUnusedDeclaredThrownExceptionWhenOverriding = false;
/*  994 */     this.reportUnusedDeclaredThrownExceptionIncludeDocCommentReference = true;
/*  995 */     this.reportUnusedDeclaredThrownExceptionExemptExceptionAndThrowable = true;
/*      */ 
/*  998 */     this.reportSpecialParameterHidingField = false;
/*      */ 
/* 1001 */     this.reportInvalidJavadocTagsVisibility = 1;
/* 1002 */     this.reportInvalidJavadocTags = false;
/* 1003 */     this.reportInvalidJavadocTagsDeprecatedRef = false;
/* 1004 */     this.reportInvalidJavadocTagsNotVisibleRef = false;
/* 1005 */     this.reportMissingJavadocTagDescription = "return_tag";
/*      */ 
/* 1008 */     this.reportMissingJavadocTagsVisibility = 1;
/* 1009 */     this.reportMissingJavadocTagsOverriding = false;
/*      */ 
/* 1012 */     this.reportMissingJavadocCommentsVisibility = 1;
/* 1013 */     this.reportMissingJavadocCommentsOverriding = false;
/*      */ 
/* 1016 */     this.inlineJsrBytecode = false;
/*      */ 
/* 1019 */     this.docCommentSupport = false;
/*      */ 
/* 1022 */     this.suppressWarnings = true;
/*      */ 
/* 1025 */     this.treatOptionalErrorAsFatal = true;
/*      */ 
/* 1028 */     this.performMethodsFullRecovery = true;
/*      */ 
/* 1031 */     this.performStatementsRecovery = true;
/*      */ 
/* 1034 */     this.storeAnnotations = false;
/*      */ 
/* 1037 */     this.generateClassFiles = true;
/*      */ 
/* 1040 */     this.processAnnotations = false;
/*      */ 
/* 1043 */     this.reportDeadCodeInTrivialIfStatement = false;
/*      */ 
/* 1046 */     this.ignoreMethodBodies = false;
/*      */   }
/*      */ 
/*      */   public void set(Map optionsMap)
/*      */   {
/*      */     Object optionValue;
/* 1051 */     if ((optionValue = optionsMap.get("org.eclipse.jdt.core.compiler.debug.localVariable")) != null) {
/* 1052 */       if ("generate".equals(optionValue))
/* 1053 */         this.produceDebugAttributes |= 4;
/* 1054 */       else if ("do not generate".equals(optionValue)) {
/* 1055 */         this.produceDebugAttributes &= -5;
/*      */       }
/*      */     }
/* 1058 */     if ((optionValue = optionsMap.get("org.eclipse.jdt.core.compiler.debug.lineNumber")) != null) {
/* 1059 */       if ("generate".equals(optionValue))
/* 1060 */         this.produceDebugAttributes |= 2;
/* 1061 */       else if ("do not generate".equals(optionValue)) {
/* 1062 */         this.produceDebugAttributes &= -3;
/*      */       }
/*      */     }
/* 1065 */     if ((optionValue = optionsMap.get("org.eclipse.jdt.core.compiler.debug.sourceFile")) != null) {
/* 1066 */       if ("generate".equals(optionValue))
/* 1067 */         this.produceDebugAttributes |= 1;
/* 1068 */       else if ("do not generate".equals(optionValue)) {
/* 1069 */         this.produceDebugAttributes &= -2;
/*      */       }
/*      */     }
/* 1072 */     if ((optionValue = optionsMap.get("org.eclipse.jdt.core.compiler.codegen.unusedLocal")) != null) {
/* 1073 */       if ("preserve".equals(optionValue))
/* 1074 */         this.preserveAllLocalVariables = true;
/* 1075 */       else if ("optimize out".equals(optionValue)) {
/* 1076 */         this.preserveAllLocalVariables = false;
/*      */       }
/*      */     }
/* 1079 */     if ((optionValue = optionsMap.get("org.eclipse.jdt.core.compiler.problem.deprecationInDeprecatedCode")) != null) {
/* 1080 */       if ("enabled".equals(optionValue))
/* 1081 */         this.reportDeprecationInsideDeprecatedCode = true;
/* 1082 */       else if ("disabled".equals(optionValue)) {
/* 1083 */         this.reportDeprecationInsideDeprecatedCode = false;
/*      */       }
/*      */     }
/* 1086 */     if ((optionValue = optionsMap.get("org.eclipse.jdt.core.compiler.problem.deprecationWhenOverridingDeprecatedMethod")) != null) {
/* 1087 */       if ("enabled".equals(optionValue))
/* 1088 */         this.reportDeprecationWhenOverridingDeprecatedMethod = true;
/* 1089 */       else if ("disabled".equals(optionValue)) {
/* 1090 */         this.reportDeprecationWhenOverridingDeprecatedMethod = false;
/*      */       }
/*      */     }
/* 1093 */     if ((optionValue = optionsMap.get("org.eclipse.jdt.core.compiler.problem.unusedDeclaredThrownExceptionWhenOverriding")) != null) {
/* 1094 */       if ("enabled".equals(optionValue))
/* 1095 */         this.reportUnusedDeclaredThrownExceptionWhenOverriding = true;
/* 1096 */       else if ("disabled".equals(optionValue)) {
/* 1097 */         this.reportUnusedDeclaredThrownExceptionWhenOverriding = false;
/*      */       }
/*      */     }
/* 1100 */     if ((optionValue = optionsMap.get("org.eclipse.jdt.core.compiler.problem.unusedDeclaredThrownExceptionIncludeDocCommentReference")) != null) {
/* 1101 */       if ("enabled".equals(optionValue))
/* 1102 */         this.reportUnusedDeclaredThrownExceptionIncludeDocCommentReference = true;
/* 1103 */       else if ("disabled".equals(optionValue)) {
/* 1104 */         this.reportUnusedDeclaredThrownExceptionIncludeDocCommentReference = false;
/*      */       }
/*      */     }
/* 1107 */     if ((optionValue = optionsMap.get("org.eclipse.jdt.core.compiler.problem.unusedDeclaredThrownExceptionExemptExceptionAndThrowable")) != null) {
/* 1108 */       if ("enabled".equals(optionValue))
/* 1109 */         this.reportUnusedDeclaredThrownExceptionExemptExceptionAndThrowable = true;
/* 1110 */       else if ("disabled".equals(optionValue)) {
/* 1111 */         this.reportUnusedDeclaredThrownExceptionExemptExceptionAndThrowable = false;
/*      */       }
/*      */     }
/* 1114 */     if ((optionValue = optionsMap.get("org.eclipse.jdt.core.compiler.compliance")) != null) {
/* 1115 */       long level = versionToJdkLevel(optionValue);
/* 1116 */       if (level != 0L) this.complianceLevel = level;
/*      */     }
/* 1118 */     if ((optionValue = optionsMap.get("org.eclipse.jdt.core.compiler.source")) != null) {
/* 1119 */       long level = versionToJdkLevel(optionValue);
/* 1120 */       if (level != 0L) this.sourceLevel = level;
/*      */     }
/* 1122 */     if ((optionValue = optionsMap.get("org.eclipse.jdt.core.compiler.codegen.targetPlatform")) != null) {
/* 1123 */       long level = versionToJdkLevel(optionValue);
/* 1124 */       if (level != 0L) {
/* 1125 */         this.targetJDK = level;
/*      */       }
/* 1127 */       if (this.targetJDK >= 3211264L) this.inlineJsrBytecode = true;
/*      */     }
/* 1129 */     if (((optionValue = optionsMap.get("org.eclipse.jdt.core.encoding")) != null) && 
/* 1130 */       ((optionValue instanceof String))) {
/* 1131 */       this.defaultEncoding = null;
/* 1132 */       String stringValue = (String)optionValue;
/* 1133 */       if (stringValue.length() > 0) {
/*      */         try {
/* 1135 */           new InputStreamReader(new ByteArrayInputStream(new byte[0]), stringValue);
/* 1136 */           this.defaultEncoding = stringValue;
/*      */         }
/*      */         catch (UnsupportedEncodingException localUnsupportedEncodingException)
/*      */         {
/*      */         }
/*      */       }
/*      */     }
/* 1143 */     if ((optionValue = optionsMap.get("org.eclipse.jdt.core.compiler.problem.unusedParameterWhenImplementingAbstract")) != null) {
/* 1144 */       if ("enabled".equals(optionValue))
/* 1145 */         this.reportUnusedParameterWhenImplementingAbstract = true;
/* 1146 */       else if ("disabled".equals(optionValue)) {
/* 1147 */         this.reportUnusedParameterWhenImplementingAbstract = false;
/*      */       }
/*      */     }
/* 1150 */     if ((optionValue = optionsMap.get("org.eclipse.jdt.core.compiler.problem.unusedParameterWhenOverridingConcrete")) != null) {
/* 1151 */       if ("enabled".equals(optionValue))
/* 1152 */         this.reportUnusedParameterWhenOverridingConcrete = true;
/* 1153 */       else if ("disabled".equals(optionValue)) {
/* 1154 */         this.reportUnusedParameterWhenOverridingConcrete = false;
/*      */       }
/*      */     }
/* 1157 */     if ((optionValue = optionsMap.get("org.eclipse.jdt.core.compiler.problem.unusedParameterIncludeDocCommentReference")) != null) {
/* 1158 */       if ("enabled".equals(optionValue))
/* 1159 */         this.reportUnusedParameterIncludeDocCommentReference = true;
/* 1160 */       else if ("disabled".equals(optionValue)) {
/* 1161 */         this.reportUnusedParameterIncludeDocCommentReference = false;
/*      */       }
/*      */     }
/* 1164 */     if ((optionValue = optionsMap.get("org.eclipse.jdt.core.compiler.problem.specialParameterHidingField")) != null) {
/* 1165 */       if ("enabled".equals(optionValue))
/* 1166 */         this.reportSpecialParameterHidingField = true;
/* 1167 */       else if ("disabled".equals(optionValue)) {
/* 1168 */         this.reportSpecialParameterHidingField = false;
/*      */       }
/*      */     }
/* 1171 */     if ((optionValue = optionsMap.get("org.eclipse.jdt.core.compiler.problem.deadCodeInTrivialIfStatement")) != null) {
/* 1172 */       if ("enabled".equals(optionValue))
/* 1173 */         this.reportDeadCodeInTrivialIfStatement = true;
/* 1174 */       else if ("disabled".equals(optionValue)) {
/* 1175 */         this.reportDeadCodeInTrivialIfStatement = false;
/*      */       }
/*      */     }
/* 1178 */     if (((optionValue = optionsMap.get("org.eclipse.jdt.core.compiler.maxProblemPerUnit")) != null) && 
/* 1179 */       ((optionValue instanceof String))) {
/* 1180 */       String stringValue = (String)optionValue;
/*      */       try {
/* 1182 */         int val = Integer.parseInt(stringValue);
/* 1183 */         if (val >= 0) this.maxProblemsPerUnit = val;
/*      */       }
/*      */       catch (NumberFormatException localNumberFormatException)
/*      */       {
/*      */       }
/*      */     }
/* 1189 */     if (((optionValue = optionsMap.get("org.eclipse.jdt.core.compiler.taskTags")) != null) && 
/* 1190 */       ((optionValue instanceof String))) {
/* 1191 */       String stringValue = (String)optionValue;
/* 1192 */       if (stringValue.length() == 0)
/* 1193 */         this.taskTags = null;
/*      */       else {
/* 1195 */         this.taskTags = CharOperation.splitAndTrimOn(',', stringValue.toCharArray());
/*      */       }
/*      */     }
/*      */ 
/* 1199 */     if (((optionValue = optionsMap.get("org.eclipse.jdt.core.compiler.taskPriorities")) != null) && 
/* 1200 */       ((optionValue instanceof String))) {
/* 1201 */       String stringValue = (String)optionValue;
/* 1202 */       if (stringValue.length() == 0)
/* 1203 */         this.taskPriorites = null;
/*      */       else {
/* 1205 */         this.taskPriorites = CharOperation.splitAndTrimOn(',', stringValue.toCharArray());
/*      */       }
/*      */     }
/*      */ 
/* 1209 */     if ((optionValue = optionsMap.get("org.eclipse.jdt.core.compiler.taskCaseSensitive")) != null) {
/* 1210 */       if ("enabled".equals(optionValue))
/* 1211 */         this.isTaskCaseSensitive = true;
/* 1212 */       else if ("disabled".equals(optionValue)) {
/* 1213 */         this.isTaskCaseSensitive = false;
/*      */       }
/*      */     }
/* 1216 */     if (((optionValue = optionsMap.get("org.eclipse.jdt.core.compiler.codegen.inlineJsrBytecode")) != null) && 
/* 1217 */       (this.targetJDK < 3211264L)) {
/* 1218 */       if ("enabled".equals(optionValue))
/* 1219 */         this.inlineJsrBytecode = true;
/* 1220 */       else if ("disabled".equals(optionValue)) {
/* 1221 */         this.inlineJsrBytecode = false;
/*      */       }
/*      */     }
/*      */ 
/* 1225 */     if ((optionValue = optionsMap.get("org.eclipse.jdt.core.compiler.problem.suppressWarnings")) != null) {
/* 1226 */       if ("enabled".equals(optionValue))
/* 1227 */         this.suppressWarnings = true;
/* 1228 */       else if ("disabled".equals(optionValue)) {
/* 1229 */         this.suppressWarnings = false;
/*      */       }
/*      */     }
/* 1232 */     if ((optionValue = optionsMap.get("org.eclipse.jdt.core.compiler.problem.fatalOptionalError")) != null) {
/* 1233 */       if ("enabled".equals(optionValue))
/* 1234 */         this.treatOptionalErrorAsFatal = true;
/* 1235 */       else if ("disabled".equals(optionValue)) {
/* 1236 */         this.treatOptionalErrorAsFatal = false;
/*      */       }
/*      */     }
/* 1239 */     if ((optionValue = optionsMap.get("org.eclipse.jdt.core.compiler.problem.methodWithConstructorName")) != null) updateSeverity(1, optionValue);
/* 1240 */     if ((optionValue = optionsMap.get("org.eclipse.jdt.core.compiler.problem.overridingPackageDefaultMethod")) != null) updateSeverity(2, optionValue);
/* 1241 */     if ((optionValue = optionsMap.get("org.eclipse.jdt.core.compiler.problem.deprecation")) != null) updateSeverity(4, optionValue);
/* 1242 */     if ((optionValue = optionsMap.get("org.eclipse.jdt.core.compiler.problem.hiddenCatchBlock")) != null) updateSeverity(8, optionValue);
/* 1243 */     if ((optionValue = optionsMap.get("org.eclipse.jdt.core.compiler.problem.unusedLocal")) != null) updateSeverity(16, optionValue);
/* 1244 */     if ((optionValue = optionsMap.get("org.eclipse.jdt.core.compiler.problem.unusedParameter")) != null) updateSeverity(32, optionValue);
/* 1245 */     if ((optionValue = optionsMap.get("org.eclipse.jdt.core.compiler.problem.unusedImport")) != null) updateSeverity(1024, optionValue);
/* 1246 */     if ((optionValue = optionsMap.get("org.eclipse.jdt.core.compiler.problem.unusedPrivateMember")) != null) updateSeverity(32768, optionValue);
/* 1247 */     if ((optionValue = optionsMap.get("org.eclipse.jdt.core.compiler.problem.unusedDeclaredThrownException")) != null) updateSeverity(8388608, optionValue);
/* 1248 */     if ((optionValue = optionsMap.get("org.eclipse.jdt.core.compiler.problem.noImplicitStringConversion")) != null) updateSeverity(64, optionValue);
/* 1249 */     if ((optionValue = optionsMap.get("org.eclipse.jdt.core.compiler.problem.syntheticAccessEmulation")) != null) updateSeverity(128, optionValue);
/* 1250 */     if ((optionValue = optionsMap.get("org.eclipse.jdt.core.compiler.problem.localVariableHiding")) != null) updateSeverity(65536, optionValue);
/* 1251 */     if ((optionValue = optionsMap.get("org.eclipse.jdt.core.compiler.problem.fieldHiding")) != null) updateSeverity(131072, optionValue);
/* 1252 */     if ((optionValue = optionsMap.get("org.eclipse.jdt.core.compiler.problem.typeParameterHiding")) != null) updateSeverity(536871936, optionValue);
/* 1253 */     if ((optionValue = optionsMap.get("org.eclipse.jdt.core.compiler.problem.possibleAccidentalBooleanAssignment")) != null) updateSeverity(262144, optionValue);
/* 1254 */     if ((optionValue = optionsMap.get("org.eclipse.jdt.core.compiler.problem.emptyStatement")) != null) updateSeverity(524288, optionValue);
/* 1255 */     if ((optionValue = optionsMap.get("org.eclipse.jdt.core.compiler.problem.nonExternalizedStringLiteral")) != null) updateSeverity(256, optionValue);
/* 1256 */     if ((optionValue = optionsMap.get("org.eclipse.jdt.core.compiler.problem.assertIdentifier")) != null) updateSeverity(512, optionValue);
/* 1257 */     if ((optionValue = optionsMap.get("org.eclipse.jdt.core.compiler.problem.enumIdentifier")) != null) updateSeverity(536870928, optionValue);
/* 1258 */     if ((optionValue = optionsMap.get("org.eclipse.jdt.core.compiler.problem.staticAccessReceiver")) != null) updateSeverity(2048, optionValue);
/* 1259 */     if ((optionValue = optionsMap.get("org.eclipse.jdt.core.compiler.problem.indirectStaticAccess")) != null) updateSeverity(268435456, optionValue);
/* 1260 */     if ((optionValue = optionsMap.get("org.eclipse.jdt.core.compiler.problem.incompatibleNonInheritedInterfaceMethod")) != null) updateSeverity(16384, optionValue);
/* 1261 */     if ((optionValue = optionsMap.get("org.eclipse.jdt.core.compiler.problem.undocumentedEmptyBlock")) != null) updateSeverity(134217728, optionValue);
/* 1262 */     if ((optionValue = optionsMap.get("org.eclipse.jdt.core.compiler.problem.unnecessaryTypeCheck")) != null) updateSeverity(67108864, optionValue);
/* 1263 */     if ((optionValue = optionsMap.get("org.eclipse.jdt.core.compiler.problem.unnecessaryElse")) != null) updateSeverity(536870913, optionValue);
/* 1264 */     if ((optionValue = optionsMap.get("org.eclipse.jdt.core.compiler.problem.finallyBlockNotCompletingNormally")) != null) updateSeverity(16777216, optionValue);
/* 1265 */     if ((optionValue = optionsMap.get("org.eclipse.jdt.core.compiler.problem.unqualifiedFieldAccess")) != null) updateSeverity(4194304, optionValue);
/* 1266 */     if ((optionValue = optionsMap.get("org.eclipse.jdt.core.compiler.problem.noEffectAssignment")) != null) updateSeverity(8192, optionValue);
/* 1267 */     if ((optionValue = optionsMap.get("org.eclipse.jdt.core.compiler.problem.uncheckedTypeOperation")) != null) updateSeverity(536870914, optionValue);
/* 1268 */     if ((optionValue = optionsMap.get("org.eclipse.jdt.core.compiler.problem.rawTypeReference")) != null) updateSeverity(536936448, optionValue);
/* 1269 */     if ((optionValue = optionsMap.get("org.eclipse.jdt.core.compiler.problem.finalParameterBound")) != null) updateSeverity(536870916, optionValue);
/* 1270 */     if ((optionValue = optionsMap.get("org.eclipse.jdt.core.compiler.problem.missingSerialVersion")) != null) updateSeverity(536870920, optionValue);
/* 1271 */     if ((optionValue = optionsMap.get("org.eclipse.jdt.core.compiler.problem.forbiddenReference")) != null) updateSeverity(536870944, optionValue);
/* 1272 */     if ((optionValue = optionsMap.get("org.eclipse.jdt.core.compiler.problem.discouragedReference")) != null) updateSeverity(536887296, optionValue);
/* 1273 */     if ((optionValue = optionsMap.get("org.eclipse.jdt.core.compiler.problem.varargsArgumentNeedCast")) != null) updateSeverity(536870976, optionValue);
/* 1274 */     if ((optionValue = optionsMap.get("org.eclipse.jdt.core.compiler.problem.nullReference")) != null) updateSeverity(536871040, optionValue);
/* 1275 */     if ((optionValue = optionsMap.get("org.eclipse.jdt.core.compiler.problem.potentialNullReference")) != null) updateSeverity(538968064, optionValue);
/* 1276 */     if ((optionValue = optionsMap.get("org.eclipse.jdt.core.compiler.problem.redundantNullCheck")) != null) updateSeverity(541065216, optionValue);
/* 1277 */     if ((optionValue = optionsMap.get("org.eclipse.jdt.core.compiler.problem.autoboxing")) != null) updateSeverity(536871168, optionValue);
/* 1278 */     if ((optionValue = optionsMap.get("org.eclipse.jdt.core.compiler.problem.annotationSuperInterface")) != null) updateSeverity(536871424, optionValue);
/* 1279 */     if ((optionValue = optionsMap.get("org.eclipse.jdt.core.compiler.problem.missingOverrideAnnotation")) != null) updateSeverity(536872960, optionValue);
/* 1280 */     if ((optionValue = optionsMap.get("org.eclipse.jdt.core.compiler.problem.missingDeprecatedAnnotation")) != null) updateSeverity(536879104, optionValue);
/* 1281 */     if ((optionValue = optionsMap.get("org.eclipse.jdt.core.compiler.problem.incompleteEnumSwitch")) != null) updateSeverity(536875008, optionValue);
/* 1282 */     if ((optionValue = optionsMap.get("org.eclipse.jdt.core.compiler.problem.unhandledWarningToken")) != null) updateSeverity(536903680, optionValue);
/* 1283 */     if ((optionValue = optionsMap.get("org.eclipse.jdt.core.compiler.problem.unusedWarningToken")) != null) updateSeverity(570425344, optionValue);
/* 1284 */     if ((optionValue = optionsMap.get("org.eclipse.jdt.core.compiler.problem.unusedLabel")) != null) updateSeverity(537001984, optionValue);
/* 1285 */     if ((optionValue = optionsMap.get("org.eclipse.jdt.core.compiler.problem.parameterAssignment")) != null) updateSeverity(537133056, optionValue);
/* 1286 */     if ((optionValue = optionsMap.get("org.eclipse.jdt.core.compiler.problem.fallthroughCase")) != null) updateSeverity(537395200, optionValue);
/* 1287 */     if ((optionValue = optionsMap.get("org.eclipse.jdt.core.compiler.problem.overridingMethodWithoutSuperInvocation")) != null) updateSeverity(537919488, optionValue);
/* 1288 */     if ((optionValue = optionsMap.get("org.eclipse.jdt.core.compiler.problem.unusedTypeArgumentsForMethodInvocation")) != null) updateSeverity(553648128, optionValue);
/* 1289 */     if ((optionValue = optionsMap.get("org.eclipse.jdt.core.compiler.problem.redundantSuperinterface")) != null) updateSeverity(603979776, optionValue);
/* 1290 */     if ((optionValue = optionsMap.get("org.eclipse.jdt.core.compiler.problem.comparingIdentical")) != null) updateSeverity(671088640, optionValue);
/* 1291 */     if ((optionValue = optionsMap.get("org.eclipse.jdt.core.compiler.problem.missingSynchronizedOnInheritedMethod")) != null) updateSeverity(805306368, optionValue);
/* 1292 */     if ((optionValue = optionsMap.get("org.eclipse.jdt.core.compiler.problem.missingHashCodeMethod")) != null) updateSeverity(1073741825, optionValue);
/* 1293 */     if ((optionValue = optionsMap.get("org.eclipse.jdt.core.compiler.problem.deadCode")) != null) updateSeverity(1073741826, optionValue);
/*      */ 
/* 1296 */     if ((optionValue = optionsMap.get("org.eclipse.jdt.core.compiler.doc.comment.support")) != null) {
/* 1297 */       if ("enabled".equals(optionValue))
/* 1298 */         this.docCommentSupport = true;
/* 1299 */       else if ("disabled".equals(optionValue)) {
/* 1300 */         this.docCommentSupport = false;
/*      */       }
/*      */     }
/* 1303 */     if ((optionValue = optionsMap.get("org.eclipse.jdt.core.compiler.problem.invalidJavadoc")) != null) {
/* 1304 */       updateSeverity(33554432, optionValue);
/*      */     }
/* 1306 */     if ((optionValue = optionsMap.get("org.eclipse.jdt.core.compiler.problem.invalidJavadocTagsVisibility")) != null) {
/* 1307 */       if ("public".equals(optionValue))
/* 1308 */         this.reportInvalidJavadocTagsVisibility = 1;
/* 1309 */       else if ("protected".equals(optionValue))
/* 1310 */         this.reportInvalidJavadocTagsVisibility = 4;
/* 1311 */       else if ("default".equals(optionValue))
/* 1312 */         this.reportInvalidJavadocTagsVisibility = 0;
/* 1313 */       else if ("private".equals(optionValue)) {
/* 1314 */         this.reportInvalidJavadocTagsVisibility = 2;
/*      */       }
/*      */     }
/* 1317 */     if ((optionValue = optionsMap.get("org.eclipse.jdt.core.compiler.problem.invalidJavadocTags")) != null) {
/* 1318 */       if ("enabled".equals(optionValue))
/* 1319 */         this.reportInvalidJavadocTags = true;
/* 1320 */       else if ("disabled".equals(optionValue)) {
/* 1321 */         this.reportInvalidJavadocTags = false;
/*      */       }
/*      */     }
/* 1324 */     if ((optionValue = optionsMap.get("org.eclipse.jdt.core.compiler.problem.invalidJavadocTagsDeprecatedRef")) != null) {
/* 1325 */       if ("enabled".equals(optionValue))
/* 1326 */         this.reportInvalidJavadocTagsDeprecatedRef = true;
/* 1327 */       else if ("disabled".equals(optionValue)) {
/* 1328 */         this.reportInvalidJavadocTagsDeprecatedRef = false;
/*      */       }
/*      */     }
/* 1331 */     if ((optionValue = optionsMap.get("org.eclipse.jdt.core.compiler.problem.invalidJavadocTagsNotVisibleRef")) != null) {
/* 1332 */       if ("enabled".equals(optionValue))
/* 1333 */         this.reportInvalidJavadocTagsNotVisibleRef = true;
/* 1334 */       else if ("disabled".equals(optionValue)) {
/* 1335 */         this.reportInvalidJavadocTagsNotVisibleRef = false;
/*      */       }
/*      */     }
/* 1338 */     if ((optionValue = optionsMap.get("org.eclipse.jdt.core.compiler.problem.missingJavadocTags")) != null) {
/* 1339 */       updateSeverity(2097152, optionValue);
/*      */     }
/* 1341 */     if ((optionValue = optionsMap.get("org.eclipse.jdt.core.compiler.problem.missingJavadocTagsVisibility")) != null) {
/* 1342 */       if ("public".equals(optionValue))
/* 1343 */         this.reportMissingJavadocTagsVisibility = 1;
/* 1344 */       else if ("protected".equals(optionValue))
/* 1345 */         this.reportMissingJavadocTagsVisibility = 4;
/* 1346 */       else if ("default".equals(optionValue))
/* 1347 */         this.reportMissingJavadocTagsVisibility = 0;
/* 1348 */       else if ("private".equals(optionValue)) {
/* 1349 */         this.reportMissingJavadocTagsVisibility = 2;
/*      */       }
/*      */     }
/* 1352 */     if ((optionValue = optionsMap.get("org.eclipse.jdt.core.compiler.problem.missingJavadocTagsOverriding")) != null) {
/* 1353 */       if ("enabled".equals(optionValue))
/* 1354 */         this.reportMissingJavadocTagsOverriding = true;
/* 1355 */       else if ("disabled".equals(optionValue)) {
/* 1356 */         this.reportMissingJavadocTagsOverriding = false;
/*      */       }
/*      */     }
/* 1359 */     if ((optionValue = optionsMap.get("org.eclipse.jdt.core.compiler.problem.missingJavadocComments")) != null) {
/* 1360 */       updateSeverity(1048576, optionValue);
/*      */     }
/* 1362 */     if ((optionValue = optionsMap.get("org.eclipse.jdt.core.compiler.problem.missingJavadocTagDescription")) != null) {
/* 1363 */       this.reportMissingJavadocTagDescription = ((String)optionValue);
/*      */     }
/* 1365 */     if ((optionValue = optionsMap.get("org.eclipse.jdt.core.compiler.problem.missingJavadocCommentsVisibility")) != null) {
/* 1366 */       if ("public".equals(optionValue))
/* 1367 */         this.reportMissingJavadocCommentsVisibility = 1;
/* 1368 */       else if ("protected".equals(optionValue))
/* 1369 */         this.reportMissingJavadocCommentsVisibility = 4;
/* 1370 */       else if ("default".equals(optionValue))
/* 1371 */         this.reportMissingJavadocCommentsVisibility = 0;
/* 1372 */       else if ("private".equals(optionValue)) {
/* 1373 */         this.reportMissingJavadocCommentsVisibility = 2;
/*      */       }
/*      */     }
/* 1376 */     if ((optionValue = optionsMap.get("org.eclipse.jdt.core.compiler.problem.missingJavadocCommentsOverriding")) != null) {
/* 1377 */       if ("enabled".equals(optionValue))
/* 1378 */         this.reportMissingJavadocCommentsOverriding = true;
/* 1379 */       else if ("disabled".equals(optionValue)) {
/* 1380 */         this.reportMissingJavadocCommentsOverriding = false;
/*      */       }
/*      */     }
/* 1383 */     if ((optionValue = optionsMap.get("org.eclipse.jdt.core.compiler.generateClassFiles")) != null) {
/* 1384 */       if ("enabled".equals(optionValue))
/* 1385 */         this.generateClassFiles = true;
/* 1386 */       else if ("disabled".equals(optionValue)) {
/* 1387 */         this.generateClassFiles = false;
/*      */       }
/*      */     }
/* 1390 */     if ((optionValue = optionsMap.get("org.eclipse.jdt.core.compiler.processAnnotations")) != null)
/* 1391 */       if ("enabled".equals(optionValue)) {
/* 1392 */         this.processAnnotations = true;
/* 1393 */         this.storeAnnotations = true;
/* 1394 */         this.docCommentSupport = true;
/* 1395 */       } else if ("disabled".equals(optionValue)) {
/* 1396 */         this.processAnnotations = false;
/* 1397 */         this.storeAnnotations = false;
/*      */       }
/*      */   }
/*      */ 
/*      */   public String toString() {
/* 1402 */     StringBuffer buf = new StringBuffer("CompilerOptions:");
/* 1403 */     buf.append("\n\t- local variables debug attributes: ").append((this.produceDebugAttributes & 0x4) != 0 ? "ON" : " OFF");
/* 1404 */     buf.append("\n\t- line number debug attributes: ").append((this.produceDebugAttributes & 0x2) != 0 ? "ON" : " OFF");
/* 1405 */     buf.append("\n\t- source debug attributes: ").append((this.produceDebugAttributes & 0x1) != 0 ? "ON" : " OFF");
/* 1406 */     buf.append("\n\t- preserve all local variables: ").append(this.preserveAllLocalVariables ? "ON" : " OFF");
/* 1407 */     buf.append("\n\t- method with constructor name: ").append(getSeverityString(1));
/* 1408 */     buf.append("\n\t- overridden package default method: ").append(getSeverityString(2));
/* 1409 */     buf.append("\n\t- deprecation: ").append(getSeverityString(4));
/* 1410 */     buf.append("\n\t- masked catch block: ").append(getSeverityString(8));
/* 1411 */     buf.append("\n\t- unused local variable: ").append(getSeverityString(16));
/* 1412 */     buf.append("\n\t- unused parameter: ").append(getSeverityString(32));
/* 1413 */     buf.append("\n\t- unused import: ").append(getSeverityString(1024));
/* 1414 */     buf.append("\n\t- synthetic access emulation: ").append(getSeverityString(128));
/* 1415 */     buf.append("\n\t- assignment with no effect: ").append(getSeverityString(8192));
/* 1416 */     buf.append("\n\t- non externalized string: ").append(getSeverityString(256));
/* 1417 */     buf.append("\n\t- static access receiver: ").append(getSeverityString(2048));
/* 1418 */     buf.append("\n\t- indirect static access: ").append(getSeverityString(268435456));
/* 1419 */     buf.append("\n\t- incompatible non inherited interface method: ").append(getSeverityString(16384));
/* 1420 */     buf.append("\n\t- unused private member: ").append(getSeverityString(32768));
/* 1421 */     buf.append("\n\t- local variable hiding another variable: ").append(getSeverityString(65536));
/* 1422 */     buf.append("\n\t- field hiding another variable: ").append(getSeverityString(131072));
/* 1423 */     buf.append("\n\t- type hiding another type: ").append(getSeverityString(536871936));
/* 1424 */     buf.append("\n\t- possible accidental boolean assignment: ").append(getSeverityString(262144));
/* 1425 */     buf.append("\n\t- superfluous semicolon: ").append(getSeverityString(524288));
/* 1426 */     buf.append("\n\t- uncommented empty block: ").append(getSeverityString(134217728));
/* 1427 */     buf.append("\n\t- unnecessary type check: ").append(getSeverityString(67108864));
/* 1428 */     buf.append("\n\t- javadoc comment support: ").append(this.docCommentSupport ? "ON" : " OFF");
/* 1429 */     buf.append("\n\t\t+ invalid javadoc: ").append(getSeverityString(33554432));
/* 1430 */     buf.append("\n\t\t+ report invalid javadoc tags: ").append(this.reportInvalidJavadocTags ? "enabled" : "disabled");
/* 1431 */     buf.append("\n\t\t\t* deprecated references: ").append(this.reportInvalidJavadocTagsDeprecatedRef ? "enabled" : "disabled");
/* 1432 */     buf.append("\n\t\t\t* not visible references: ").append(this.reportInvalidJavadocTagsNotVisibleRef ? "enabled" : "disabled");
/* 1433 */     buf.append("\n\t\t+ visibility level to report invalid javadoc tags: ").append(getVisibilityString(this.reportInvalidJavadocTagsVisibility));
/* 1434 */     buf.append("\n\t\t+ missing javadoc tags: ").append(getSeverityString(2097152));
/* 1435 */     buf.append("\n\t\t+ visibility level to report missing javadoc tags: ").append(getVisibilityString(this.reportMissingJavadocTagsVisibility));
/* 1436 */     buf.append("\n\t\t+ report missing javadoc tags in overriding methods: ").append(this.reportMissingJavadocTagsOverriding ? "enabled" : "disabled");
/* 1437 */     buf.append("\n\t\t+ missing javadoc comments: ").append(getSeverityString(1048576));
/* 1438 */     buf.append("\n\t\t+ report missing tag description option: ").append(this.reportMissingJavadocTagDescription);
/* 1439 */     buf.append("\n\t\t+ visibility level to report missing javadoc comments: ").append(getVisibilityString(this.reportMissingJavadocCommentsVisibility));
/* 1440 */     buf.append("\n\t\t+ report missing javadoc comments in overriding methods: ").append(this.reportMissingJavadocCommentsOverriding ? "enabled" : "disabled");
/* 1441 */     buf.append("\n\t- finally block not completing normally: ").append(getSeverityString(16777216));
/* 1442 */     buf.append("\n\t- report unused declared thrown exception: ").append(getSeverityString(8388608));
/* 1443 */     buf.append("\n\t- report unused declared thrown exception when overriding: ").append(this.reportUnusedDeclaredThrownExceptionWhenOverriding ? "enabled" : "disabled");
/* 1444 */     buf.append("\n\t- report unused declared thrown exception include doc comment reference: ").append(this.reportUnusedDeclaredThrownExceptionIncludeDocCommentReference ? "enabled" : "disabled");
/* 1445 */     buf.append("\n\t- report unused declared thrown exception exempt exception and throwable: ").append(this.reportUnusedDeclaredThrownExceptionExemptExceptionAndThrowable ? "enabled" : "disabled");
/* 1446 */     buf.append("\n\t- unnecessary else: ").append(getSeverityString(536870913));
/* 1447 */     buf.append("\n\t- JDK compliance level: " + versionFromJdkLevel(this.complianceLevel));
/* 1448 */     buf.append("\n\t- JDK source level: " + versionFromJdkLevel(this.sourceLevel));
/* 1449 */     buf.append("\n\t- JDK target level: " + versionFromJdkLevel(this.targetJDK));
/* 1450 */     buf.append("\n\t- verbose : ").append(this.verbose ? "ON" : "OFF");
/* 1451 */     buf.append("\n\t- produce reference info : ").append(this.produceReferenceInfo ? "ON" : "OFF");
/* 1452 */     buf.append("\n\t- parse literal expressions as constants : ").append(this.parseLiteralExpressionsAsConstants ? "ON" : "OFF");
/* 1453 */     buf.append("\n\t- encoding : ").append(this.defaultEncoding == null ? "<default>" : this.defaultEncoding);
/* 1454 */     buf.append("\n\t- task tags: ").append(this.taskTags == null ? Util.EMPTY_STRING : new String(CharOperation.concatWith(this.taskTags, ',')));
/* 1455 */     buf.append("\n\t- task priorities : ").append(this.taskPriorites == null ? Util.EMPTY_STRING : new String(CharOperation.concatWith(this.taskPriorites, ',')));
/* 1456 */     buf.append("\n\t- report deprecation inside deprecated code : ").append(this.reportDeprecationInsideDeprecatedCode ? "enabled" : "disabled");
/* 1457 */     buf.append("\n\t- report deprecation when overriding deprecated method : ").append(this.reportDeprecationWhenOverridingDeprecatedMethod ? "enabled" : "disabled");
/* 1458 */     buf.append("\n\t- report unused parameter when implementing abstract method : ").append(this.reportUnusedParameterWhenImplementingAbstract ? "enabled" : "disabled");
/* 1459 */     buf.append("\n\t- report unused parameter when overriding concrete method : ").append(this.reportUnusedParameterWhenOverridingConcrete ? "enabled" : "disabled");
/* 1460 */     buf.append("\n\t- report unused parameter include doc comment reference : ").append(this.reportUnusedParameterIncludeDocCommentReference ? "enabled" : "disabled");
/* 1461 */     buf.append("\n\t- report constructor/setter parameter hiding existing field : ").append(this.reportSpecialParameterHidingField ? "enabled" : "disabled");
/* 1462 */     buf.append("\n\t- inline JSR bytecode : ").append(this.inlineJsrBytecode ? "enabled" : "disabled");
/* 1463 */     buf.append("\n\t- unsafe type operation: ").append(getSeverityString(536870914));
/* 1464 */     buf.append("\n\t- unsafe raw type: ").append(getSeverityString(536936448));
/* 1465 */     buf.append("\n\t- final bound for type parameter: ").append(getSeverityString(536870916));
/* 1466 */     buf.append("\n\t- missing serialVersionUID: ").append(getSeverityString(536870920));
/* 1467 */     buf.append("\n\t- varargs argument need cast: ").append(getSeverityString(536870976));
/* 1468 */     buf.append("\n\t- forbidden reference to type with access restriction: ").append(getSeverityString(536870944));
/* 1469 */     buf.append("\n\t- discouraged reference to type with access restriction: ").append(getSeverityString(536887296));
/* 1470 */     buf.append("\n\t- null reference: ").append(getSeverityString(536871040));
/* 1471 */     buf.append("\n\t- potential null reference: ").append(getSeverityString(538968064));
/* 1472 */     buf.append("\n\t- redundant null check: ").append(getSeverityString(541065216));
/* 1473 */     buf.append("\n\t- autoboxing: ").append(getSeverityString(536871168));
/* 1474 */     buf.append("\n\t- annotation super interface: ").append(getSeverityString(536871424));
/* 1475 */     buf.append("\n\t- missing @Override annotation: ").append(getSeverityString(536872960));
/* 1476 */     buf.append("\n\t- missing @Deprecated annotation: ").append(getSeverityString(536879104));
/* 1477 */     buf.append("\n\t- incomplete enum switch: ").append(getSeverityString(536875008));
/* 1478 */     buf.append("\n\t- suppress warnings: ").append(this.suppressWarnings ? "enabled" : "disabled");
/* 1479 */     buf.append("\n\t- unhandled warning token: ").append(getSeverityString(536903680));
/* 1480 */     buf.append("\n\t- unused warning token: ").append(getSeverityString(570425344));
/* 1481 */     buf.append("\n\t- unused label: ").append(getSeverityString(537001984));
/* 1482 */     buf.append("\n\t- treat optional error as fatal: ").append(this.treatOptionalErrorAsFatal ? "enabled" : "disabled");
/* 1483 */     buf.append("\n\t- parameter assignment: ").append(getSeverityString(537133056));
/* 1484 */     buf.append("\n\t- generate class files: ").append(this.generateClassFiles ? "enabled" : "disabled");
/* 1485 */     buf.append("\n\t- process annotations: ").append(this.processAnnotations ? "enabled" : "disabled");
/* 1486 */     buf.append("\n\t- unused type arguments for method/constructor invocation: ").append(getSeverityString(553648128));
/* 1487 */     buf.append("\n\t- redundant superinterface: ").append(getSeverityString(603979776));
/* 1488 */     buf.append("\n\t- comparing identical expr: ").append(getSeverityString(671088640));
/* 1489 */     buf.append("\n\t- missing synchronized on inherited method: ").append(getSeverityString(805306368));
/* 1490 */     buf.append("\n\t- should implement hashCode() method: ").append(getSeverityString(1073741825));
/* 1491 */     buf.append("\n\t- dead code: ").append(getSeverityString(1073741826));
/* 1492 */     buf.append("\n\t- dead code in trivial if statement: ").append(this.reportDeadCodeInTrivialIfStatement ? "enabled" : "disabled");
/* 1493 */     return buf.toString();
/*      */   }
/*      */ 
/*      */   protected void updateSeverity(int irritant, Object severityString) {
/* 1497 */     if ("error".equals(severityString)) {
/* 1498 */       this.errorThreshold.set(irritant);
/* 1499 */       this.warningThreshold.clear(irritant);
/* 1500 */     } else if ("warning".equals(severityString)) {
/* 1501 */       this.errorThreshold.clear(irritant);
/* 1502 */       this.warningThreshold.set(irritant);
/* 1503 */     } else if ("ignore".equals(severityString)) {
/* 1504 */       this.errorThreshold.clear(irritant);
/* 1505 */       this.warningThreshold.clear(irritant);
/*      */     }
/*      */   }
/*      */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\ecj-3.5.2.jar
 * Qualified Name:     org.eclipse.jdt.internal.compiler.impl.CompilerOptions
 * JD-Core Version:    0.6.0
 */