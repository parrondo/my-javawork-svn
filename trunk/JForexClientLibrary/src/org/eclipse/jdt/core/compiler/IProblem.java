package org.eclipse.jdt.core.compiler;

public abstract interface IProblem
{
  public static final int TypeRelated = 16777216;
  public static final int FieldRelated = 33554432;
  public static final int MethodRelated = 67108864;
  public static final int ConstructorRelated = 134217728;
  public static final int ImportRelated = 268435456;
  public static final int Internal = 536870912;
  public static final int Syntax = 1073741824;
  public static final int Javadoc = -2147483648;
  public static final int IgnoreCategoriesMask = 16777215;
  public static final int Unclassified = 0;
  public static final int ObjectHasNoSuperclass = 16777217;
  public static final int UndefinedType = 16777218;
  public static final int NotVisibleType = 16777219;
  public static final int AmbiguousType = 16777220;
  public static final int UsingDeprecatedType = 16777221;
  public static final int InternalTypeNameProvided = 16777222;
  public static final int UnusedPrivateType = 553648135;
  public static final int IncompatibleTypesInEqualityOperator = 16777231;
  public static final int IncompatibleTypesInConditionalOperator = 16777232;
  public static final int TypeMismatch = 16777233;
  public static final int IndirectAccessToStaticType = 553648146;
  public static final int MissingEnclosingInstanceForConstructorCall = 16777236;
  public static final int MissingEnclosingInstance = 16777237;
  public static final int IncorrectEnclosingInstanceReference = 16777238;
  public static final int IllegalEnclosingInstanceSpecification = 16777239;
  public static final int CannotDefineStaticInitializerInLocalType = 536870936;
  public static final int OuterLocalMustBeFinal = 536870937;
  public static final int CannotDefineInterfaceInLocalType = 536870938;
  public static final int IllegalPrimitiveOrArrayTypeForEnclosingInstance = 16777243;
  public static final int EnclosingInstanceInConstructorCall = 536870940;
  public static final int AnonymousClassCannotExtendFinalClass = 16777245;
  public static final int CannotDefineAnnotationInLocalType = 536870942;
  public static final int CannotDefineEnumInLocalType = 536870943;
  public static final int NonStaticContextForEnumMemberType = 536870944;
  public static final int TypeHidingType = 16777249;
  public static final int UndefinedName = 570425394;
  public static final int UninitializedLocalVariable = 536870963;
  public static final int VariableTypeCannotBeVoid = 536870964;

  /** @deprecated */
  public static final int VariableTypeCannotBeVoidArray = 536870965;
  public static final int CannotAllocateVoidArray = 536870966;
  public static final int RedefinedLocal = 536870967;
  public static final int RedefinedArgument = 536870968;
  public static final int DuplicateFinalLocalInitialization = 536870969;
  public static final int NonBlankFinalLocalAssignment = 536870970;
  public static final int ParameterAssignment = 536870971;
  public static final int FinalOuterLocalAssignment = 536870972;
  public static final int LocalVariableIsNeverUsed = 536870973;
  public static final int ArgumentIsNeverUsed = 536870974;
  public static final int BytecodeExceeds64KLimit = 536870975;
  public static final int BytecodeExceeds64KLimitForClinit = 536870976;
  public static final int TooManyArgumentSlots = 536870977;
  public static final int TooManyLocalVariableSlots = 536870978;
  public static final int TooManySyntheticArgumentSlots = 536870979;
  public static final int TooManyArrayDimensions = 536870980;
  public static final int BytecodeExceeds64KLimitForConstructor = 536870981;
  public static final int UndefinedField = 33554502;
  public static final int NotVisibleField = 33554503;
  public static final int AmbiguousField = 33554504;
  public static final int UsingDeprecatedField = 33554505;
  public static final int NonStaticFieldFromStaticInvocation = 33554506;
  public static final int ReferenceToForwardField = 570425419;
  public static final int NonStaticAccessToStaticField = 570425420;
  public static final int UnusedPrivateField = 570425421;
  public static final int IndirectAccessToStaticField = 570425422;
  public static final int UnqualifiedFieldAccess = 570425423;
  public static final int FinalFieldAssignment = 33554512;
  public static final int UninitializedBlankFinalField = 33554513;
  public static final int DuplicateBlankFinalFieldInitialization = 33554514;
  public static final int LocalVariableHidingLocalVariable = 536871002;
  public static final int LocalVariableHidingField = 570425435;
  public static final int FieldHidingLocalVariable = 570425436;
  public static final int FieldHidingField = 570425437;
  public static final int ArgumentHidingLocalVariable = 536871006;
  public static final int ArgumentHidingField = 536871007;
  public static final int MissingSerialVersion = 536871008;
  public static final int UndefinedMethod = 67108964;
  public static final int NotVisibleMethod = 67108965;
  public static final int AmbiguousMethod = 67108966;
  public static final int UsingDeprecatedMethod = 67108967;
  public static final int DirectInvocationOfAbstractMethod = 67108968;
  public static final int VoidMethodReturnsValue = 67108969;
  public static final int MethodReturnsVoid = 67108970;
  public static final int MethodRequiresBody = 603979883;
  public static final int ShouldReturnValue = 603979884;
  public static final int MethodButWithConstructorName = 67108974;
  public static final int MissingReturnType = 16777327;
  public static final int BodyForNativeMethod = 603979888;
  public static final int BodyForAbstractMethod = 603979889;
  public static final int NoMessageSendOnBaseType = 67108978;
  public static final int ParameterMismatch = 67108979;
  public static final int NoMessageSendOnArrayType = 67108980;
  public static final int NonStaticAccessToStaticMethod = 603979893;
  public static final int UnusedPrivateMethod = 603979894;
  public static final int IndirectAccessToStaticMethod = 603979895;
  public static final int MissingTypeInMethod = 67108984;
  public static final int MissingTypeInConstructor = 134217857;
  public static final int UndefinedConstructor = 134217858;
  public static final int NotVisibleConstructor = 134217859;
  public static final int AmbiguousConstructor = 134217860;
  public static final int UsingDeprecatedConstructor = 134217861;
  public static final int UnusedPrivateConstructor = 603979910;
  public static final int InstanceFieldDuringConstructorInvocation = 134217863;
  public static final int InstanceMethodDuringConstructorInvocation = 134217864;
  public static final int RecursiveConstructorInvocation = 134217865;
  public static final int ThisSuperDuringConstructorInvocation = 134217866;
  public static final int InvalidExplicitConstructorCall = 1207959691;
  public static final int UndefinedConstructorInDefaultConstructor = 134217868;
  public static final int NotVisibleConstructorInDefaultConstructor = 134217869;
  public static final int AmbiguousConstructorInDefaultConstructor = 134217870;
  public static final int UndefinedConstructorInImplicitConstructorCall = 134217871;
  public static final int NotVisibleConstructorInImplicitConstructorCall = 134217872;
  public static final int AmbiguousConstructorInImplicitConstructorCall = 134217873;
  public static final int UnhandledExceptionInDefaultConstructor = 16777362;
  public static final int UnhandledExceptionInImplicitConstructorCall = 16777363;
  public static final int DeadCode = 536871061;
  public static final int ArrayReferenceRequired = 536871062;
  public static final int NoImplicitStringConversionForCharArrayExpression = 536871063;
  public static final int StringConstantIsExceedingUtf8Limit = 536871064;
  public static final int NonConstantExpression = 536871065;
  public static final int NumericValueOutOfRange = 536871066;
  public static final int IllegalCast = 16777372;
  public static final int InvalidClassInstantiation = 16777373;
  public static final int CannotDefineDimensionExpressionsWithInit = 536871070;
  public static final int MustDefineEitherDimensionExpressionsOrInitializer = 536871071;
  public static final int InvalidOperator = 536871072;
  public static final int CodeCannotBeReached = 536871073;
  public static final int CannotReturnInInitializer = 536871074;
  public static final int InitializerMustCompleteNormally = 536871075;
  public static final int InvalidVoidExpression = 536871076;
  public static final int MaskedCatch = 16777381;
  public static final int DuplicateDefaultCase = 536871078;
  public static final int UnreachableCatch = 83886247;
  public static final int UnhandledException = 16777384;
  public static final int IncorrectSwitchType = 16777385;
  public static final int DuplicateCase = 33554602;
  public static final int DuplicateLabel = 536871083;
  public static final int InvalidBreak = 536871084;
  public static final int InvalidContinue = 536871085;
  public static final int UndefinedLabel = 536871086;
  public static final int InvalidTypeToSynchronized = 536871087;
  public static final int InvalidNullToSynchronized = 536871088;
  public static final int CannotThrowNull = 536871089;
  public static final int AssignmentHasNoEffect = 536871090;
  public static final int PossibleAccidentalBooleanAssignment = 536871091;
  public static final int SuperfluousSemicolon = 536871092;
  public static final int UnnecessaryCast = 553648309;

  /** @deprecated */
  public static final int UnnecessaryArgumentCast = 553648310;
  public static final int UnnecessaryInstanceof = 553648311;
  public static final int FinallyMustCompleteNormally = 536871096;
  public static final int UnusedMethodDeclaredThrownException = 536871097;
  public static final int UnusedConstructorDeclaredThrownException = 536871098;
  public static final int InvalidCatchBlockSequence = 553648315;
  public static final int EmptyControlFlowStatement = 553648316;
  public static final int UnnecessaryElse = 536871101;
  public static final int NeedToEmulateFieldReadAccess = 33554622;
  public static final int NeedToEmulateFieldWriteAccess = 33554623;
  public static final int NeedToEmulateMethodAccess = 67109056;
  public static final int NeedToEmulateConstructorAccess = 67109057;
  public static final int FallthroughCase = 536871106;
  public static final int InheritedMethodHidesEnclosingName = 67109059;
  public static final int InheritedFieldHidesEnclosingName = 33554628;
  public static final int InheritedTypeHidesEnclosingName = 16777413;
  public static final int IllegalUsageOfQualifiedTypeReference = 1610612934;
  public static final int UnusedLabel = 536871111;
  public static final int ThisInStaticContext = 536871112;
  public static final int StaticMethodRequested = 603979977;
  public static final int IllegalDimension = 536871114;
  public static final int InvalidTypeExpression = 536871115;
  public static final int ParsingError = 1610612940;
  public static final int ParsingErrorNoSuggestion = 1610612941;
  public static final int InvalidUnaryExpression = 1610612942;
  public static final int InterfaceCannotHaveConstructors = 1610612943;
  public static final int ArrayConstantsOnlyInArrayInitializers = 1610612944;
  public static final int ParsingErrorOnKeyword = 1610612945;
  public static final int ParsingErrorOnKeywordNoSuggestion = 1610612946;
  public static final int ComparingIdentical = 536871123;
  public static final int UnmatchedBracket = 1610612956;
  public static final int NoFieldOnBaseType = 33554653;
  public static final int InvalidExpressionAsStatement = 1610612958;
  public static final int ExpressionShouldBeAVariable = 1610612959;
  public static final int MissingSemiColon = 1610612960;
  public static final int InvalidParenthesizedExpression = 1610612961;
  public static final int ParsingErrorInsertTokenBefore = 1610612966;
  public static final int ParsingErrorInsertTokenAfter = 1610612967;
  public static final int ParsingErrorDeleteToken = 1610612968;
  public static final int ParsingErrorDeleteTokens = 1610612969;
  public static final int ParsingErrorMergeTokens = 1610612970;
  public static final int ParsingErrorInvalidToken = 1610612971;
  public static final int ParsingErrorMisplacedConstruct = 1610612972;
  public static final int ParsingErrorReplaceTokens = 1610612973;
  public static final int ParsingErrorNoSuggestionForTokens = 1610612974;
  public static final int ParsingErrorUnexpectedEOF = 1610612975;
  public static final int ParsingErrorInsertToComplete = 1610612976;
  public static final int ParsingErrorInsertToCompleteScope = 1610612977;
  public static final int ParsingErrorInsertToCompletePhrase = 1610612978;
  public static final int EndOfSource = 1610612986;
  public static final int InvalidHexa = 1610612987;
  public static final int InvalidOctal = 1610612988;
  public static final int InvalidCharacterConstant = 1610612989;
  public static final int InvalidEscape = 1610612990;
  public static final int InvalidInput = 1610612991;
  public static final int InvalidUnicodeEscape = 1610612992;
  public static final int InvalidFloat = 1610612993;
  public static final int NullSourceString = 1610612994;
  public static final int UnterminatedString = 1610612995;
  public static final int UnterminatedComment = 1610612996;
  public static final int NonExternalizedStringLiteral = 536871173;
  public static final int InvalidDigit = 1610612998;
  public static final int InvalidLowSurrogate = 1610612999;
  public static final int InvalidHighSurrogate = 1610613000;
  public static final int UnnecessaryNLSTag = 536871177;
  public static final int DiscouragedReference = 16777496;
  public static final int InterfaceCannotHaveInitializers = 16777516;
  public static final int DuplicateModifierForType = 16777517;
  public static final int IllegalModifierForClass = 16777518;
  public static final int IllegalModifierForInterface = 16777519;
  public static final int IllegalModifierForMemberClass = 16777520;
  public static final int IllegalModifierForMemberInterface = 16777521;
  public static final int IllegalModifierForLocalClass = 16777522;
  public static final int ForbiddenReference = 16777523;
  public static final int IllegalModifierCombinationFinalAbstractForClass = 16777524;
  public static final int IllegalVisibilityModifierForInterfaceMemberType = 16777525;
  public static final int IllegalVisibilityModifierCombinationForMemberType = 16777526;
  public static final int IllegalStaticModifierForMemberType = 16777527;
  public static final int SuperclassMustBeAClass = 16777528;
  public static final int ClassExtendFinalClass = 16777529;
  public static final int DuplicateSuperInterface = 16777530;
  public static final int SuperInterfaceMustBeAnInterface = 16777531;
  public static final int HierarchyCircularitySelfReference = 16777532;
  public static final int HierarchyCircularity = 16777533;
  public static final int HidingEnclosingType = 16777534;
  public static final int DuplicateNestedType = 16777535;
  public static final int CannotThrowType = 16777536;
  public static final int PackageCollidesWithType = 16777537;
  public static final int TypeCollidesWithPackage = 16777538;
  public static final int DuplicateTypes = 16777539;
  public static final int IsClassPathCorrect = 16777540;
  public static final int PublicClassMustMatchFileName = 16777541;
  public static final int MustSpecifyPackage = 536871238;
  public static final int HierarchyHasProblems = 16777543;
  public static final int PackageIsNotExpectedPackage = 536871240;
  public static final int ObjectCannotHaveSuperTypes = 536871241;
  public static final int ObjectMustBeClass = 536871242;
  public static final int RedundantSuperinterface = 16777547;
  public static final int ShouldImplementHashcode = 16777548;
  public static final int AbstractMethodsInConcreteClass = 16777549;

  /** @deprecated */
  public static final int SuperclassNotFound = 16777546;

  /** @deprecated */
  public static final int SuperclassNotVisible = 16777547;

  /** @deprecated */
  public static final int SuperclassAmbiguous = 16777548;

  /** @deprecated */
  public static final int SuperclassInternalNameProvided = 16777549;

  /** @deprecated */
  public static final int SuperclassInheritedNameHidesEnclosingName = 16777550;

  /** @deprecated */
  public static final int InterfaceNotFound = 16777551;

  /** @deprecated */
  public static final int InterfaceNotVisible = 16777552;

  /** @deprecated */
  public static final int InterfaceAmbiguous = 16777553;

  /** @deprecated */
  public static final int InterfaceInternalNameProvided = 16777554;

  /** @deprecated */
  public static final int InterfaceInheritedNameHidesEnclosingName = 16777555;
  public static final int DuplicateField = 33554772;
  public static final int DuplicateModifierForField = 33554773;
  public static final int IllegalModifierForField = 33554774;
  public static final int IllegalModifierForInterfaceField = 33554775;
  public static final int IllegalVisibilityModifierCombinationForField = 33554776;
  public static final int IllegalModifierCombinationFinalVolatileForField = 33554777;
  public static final int UnexpectedStaticModifierForField = 33554778;

  /** @deprecated */
  public static final int FieldTypeNotFound = 33554782;

  /** @deprecated */
  public static final int FieldTypeNotVisible = 33554783;

  /** @deprecated */
  public static final int FieldTypeAmbiguous = 33554784;

  /** @deprecated */
  public static final int FieldTypeInternalNameProvided = 33554785;

  /** @deprecated */
  public static final int FieldTypeInheritedNameHidesEnclosingName = 33554786;
  public static final int DuplicateMethod = 67109219;
  public static final int IllegalModifierForArgument = 67109220;
  public static final int DuplicateModifierForMethod = 67109221;
  public static final int IllegalModifierForMethod = 67109222;
  public static final int IllegalModifierForInterfaceMethod = 67109223;
  public static final int IllegalVisibilityModifierCombinationForMethod = 67109224;
  public static final int UnexpectedStaticModifierForMethod = 67109225;
  public static final int IllegalAbstractModifierCombinationForMethod = 67109226;
  public static final int AbstractMethodInAbstractClass = 67109227;
  public static final int ArgumentTypeCannotBeVoid = 67109228;

  /** @deprecated */
  public static final int ArgumentTypeCannotBeVoidArray = 67109229;

  /** @deprecated */
  public static final int ReturnTypeCannotBeVoidArray = 67109230;
  public static final int NativeMethodsCannotBeStrictfp = 67109231;
  public static final int DuplicateModifierForArgument = 67109232;
  public static final int IllegalModifierForConstructor = 67109233;

  /** @deprecated */
  public static final int ArgumentTypeNotFound = 67109234;

  /** @deprecated */
  public static final int ArgumentTypeNotVisible = 67109235;

  /** @deprecated */
  public static final int ArgumentTypeAmbiguous = 67109236;

  /** @deprecated */
  public static final int ArgumentTypeInternalNameProvided = 67109237;

  /** @deprecated */
  public static final int ArgumentTypeInheritedNameHidesEnclosingName = 67109238;

  /** @deprecated */
  public static final int ExceptionTypeNotFound = 67109239;

  /** @deprecated */
  public static final int ExceptionTypeNotVisible = 67109240;

  /** @deprecated */
  public static final int ExceptionTypeAmbiguous = 67109241;

  /** @deprecated */
  public static final int ExceptionTypeInternalNameProvided = 67109242;

  /** @deprecated */
  public static final int ExceptionTypeInheritedNameHidesEnclosingName = 67109243;

  /** @deprecated */
  public static final int ReturnTypeNotFound = 67109244;

  /** @deprecated */
  public static final int ReturnTypeNotVisible = 67109245;

  /** @deprecated */
  public static final int ReturnTypeAmbiguous = 67109246;

  /** @deprecated */
  public static final int ReturnTypeInternalNameProvided = 67109247;

  /** @deprecated */
  public static final int ReturnTypeInheritedNameHidesEnclosingName = 67109248;
  public static final int ConflictingImport = 268435841;
  public static final int DuplicateImport = 268435842;
  public static final int CannotImportPackage = 268435843;
  public static final int UnusedImport = 268435844;
  public static final int ImportNotFound = 268435846;

  /** @deprecated */
  public static final int ImportNotVisible = 268435847;

  /** @deprecated */
  public static final int ImportAmbiguous = 268435848;

  /** @deprecated */
  public static final int ImportInternalNameProvided = 268435849;

  /** @deprecated */
  public static final int ImportInheritedNameHidesEnclosingName = 268435850;
  public static final int InvalidTypeForStaticImport = 268435847;
  public static final int DuplicateModifierForVariable = 67109259;
  public static final int IllegalModifierForVariable = 67109260;

  /** @deprecated */
  public static final int LocalVariableCannotBeNull = 536871309;

  /** @deprecated */
  public static final int LocalVariableCanOnlyBeNull = 536871310;

  /** @deprecated */
  public static final int LocalVariableMayBeNull = 536871311;
  public static final int AbstractMethodMustBeImplemented = 67109264;
  public static final int FinalMethodCannotBeOverridden = 67109265;
  public static final int IncompatibleExceptionInThrowsClause = 67109266;
  public static final int IncompatibleExceptionInInheritedMethodThrowsClause = 67109267;
  public static final int IncompatibleReturnType = 67109268;
  public static final int InheritedMethodReducesVisibility = 67109269;
  public static final int CannotOverrideAStaticMethodWithAnInstanceMethod = 67109270;
  public static final int CannotHideAnInstanceMethodWithAStaticMethod = 67109271;
  public static final int StaticInheritedMethodConflicts = 67109272;
  public static final int MethodReducesVisibility = 67109273;
  public static final int OverridingNonVisibleMethod = 67109274;
  public static final int AbstractMethodCannotBeOverridden = 67109275;
  public static final int OverridingDeprecatedMethod = 67109276;
  public static final int IncompatibleReturnTypeForNonInheritedInterfaceMethod = 67109277;
  public static final int IncompatibleExceptionInThrowsClauseForNonInheritedInterfaceMethod = 67109278;
  public static final int IllegalVararg = 67109279;
  public static final int OverridingMethodWithoutSuperInvocation = 67109280;
  public static final int MissingSynchronizedModifierInInheritedMethod = 67109281;
  public static final int AbstractMethodMustBeImplementedOverConcreteMethod = 67109282;
  public static final int InheritedIncompatibleReturnType = 67109283;
  public static final int CodeSnippetMissingClass = 536871332;
  public static final int CodeSnippetMissingMethod = 536871333;
  public static final int CannotUseSuperInCodeSnippet = 536871334;
  public static final int TooManyConstantsInConstantPool = 536871342;
  public static final int TooManyBytesForStringConstant = 536871343;
  public static final int TooManyFields = 536871344;
  public static final int TooManyMethods = 536871345;
  public static final int UseAssertAsAnIdentifier = 536871352;
  public static final int UseEnumAsAnIdentifier = 536871353;
  public static final int EnumConstantsCannotBeSurroundedByParenthesis = 1610613178;
  public static final int Task = 536871362;
  public static final int NullLocalVariableReference = 536871363;
  public static final int PotentialNullLocalVariableReference = 536871364;
  public static final int RedundantNullCheckOnNullLocalVariable = 536871365;
  public static final int NullLocalVariableComparisonYieldsFalse = 536871366;
  public static final int RedundantLocalVariableNullAssignment = 536871367;
  public static final int NullLocalVariableInstanceofYieldsFalse = 536871368;
  public static final int RedundantNullCheckOnNonNullLocalVariable = 536871369;
  public static final int NonNullLocalVariableComparisonYieldsFalse = 536871370;
  public static final int UndocumentedEmptyBlock = 536871372;
  public static final int JavadocInvalidSeeUrlReference = -1610612274;
  public static final int JavadocMissingTagDescription = -1610612273;
  public static final int JavadocDuplicateTag = -1610612272;
  public static final int JavadocHiddenReference = -1610612271;
  public static final int JavadocInvalidMemberTypeQualification = -1610612270;
  public static final int JavadocMissingIdentifier = -1610612269;
  public static final int JavadocNonStaticTypeFromStaticInvocation = -1610612268;
  public static final int JavadocInvalidParamTagTypeParameter = -1610612267;
  public static final int JavadocUnexpectedTag = -1610612266;
  public static final int JavadocMissingParamTag = -1610612265;
  public static final int JavadocMissingParamName = -1610612264;
  public static final int JavadocDuplicateParamName = -1610612263;
  public static final int JavadocInvalidParamName = -1610612262;
  public static final int JavadocMissingReturnTag = -1610612261;
  public static final int JavadocDuplicateReturnTag = -1610612260;
  public static final int JavadocMissingThrowsTag = -1610612259;
  public static final int JavadocMissingThrowsClassName = -1610612258;
  public static final int JavadocInvalidThrowsClass = -1610612257;
  public static final int JavadocDuplicateThrowsClassName = -1610612256;
  public static final int JavadocInvalidThrowsClassName = -1610612255;
  public static final int JavadocMissingSeeReference = -1610612254;
  public static final int JavadocInvalidSeeReference = -1610612253;
  public static final int JavadocInvalidSeeHref = -1610612252;
  public static final int JavadocInvalidSeeArgs = -1610612251;
  public static final int JavadocMissing = -1610612250;
  public static final int JavadocInvalidTag = -1610612249;
  public static final int JavadocUndefinedField = -1610612248;
  public static final int JavadocNotVisibleField = -1610612247;
  public static final int JavadocAmbiguousField = -1610612246;
  public static final int JavadocUsingDeprecatedField = -1610612245;
  public static final int JavadocUndefinedConstructor = -1610612244;
  public static final int JavadocNotVisibleConstructor = -1610612243;
  public static final int JavadocAmbiguousConstructor = -1610612242;
  public static final int JavadocUsingDeprecatedConstructor = -1610612241;
  public static final int JavadocUndefinedMethod = -1610612240;
  public static final int JavadocNotVisibleMethod = -1610612239;
  public static final int JavadocAmbiguousMethod = -1610612238;
  public static final int JavadocUsingDeprecatedMethod = -1610612237;
  public static final int JavadocNoMessageSendOnBaseType = -1610612236;
  public static final int JavadocParameterMismatch = -1610612235;
  public static final int JavadocNoMessageSendOnArrayType = -1610612234;
  public static final int JavadocUndefinedType = -1610612233;
  public static final int JavadocNotVisibleType = -1610612232;
  public static final int JavadocAmbiguousType = -1610612231;
  public static final int JavadocUsingDeprecatedType = -1610612230;
  public static final int JavadocInternalTypeNameProvided = -1610612229;
  public static final int JavadocInheritedMethodHidesEnclosingName = -1610612228;
  public static final int JavadocInheritedFieldHidesEnclosingName = -1610612227;
  public static final int JavadocInheritedNameHidesEnclosingTypeName = -1610612226;
  public static final int JavadocAmbiguousMethodReference = -1610612225;
  public static final int JavadocUnterminatedInlineTag = -1610612224;
  public static final int JavadocMalformedSeeReference = -1610612223;
  public static final int JavadocMessagePrefix = 536871426;
  public static final int JavadocMissingHashCharacter = -1610612221;
  public static final int JavadocEmptyReturnTag = -1610612220;
  public static final int JavadocInvalidValueReference = -1610612219;
  public static final int JavadocUnexpectedText = -1610612218;
  public static final int JavadocInvalidParamTagName = -1610612217;
  public static final int DuplicateTypeVariable = 536871432;
  public static final int IllegalTypeVariableSuperReference = 536871433;
  public static final int NonStaticTypeFromStaticInvocation = 536871434;
  public static final int ObjectCannotBeGeneric = 536871435;
  public static final int NonGenericType = 16777740;
  public static final int IncorrectArityForParameterizedType = 16777741;
  public static final int TypeArgumentMismatch = 16777742;
  public static final int DuplicateMethodErasure = 16777743;
  public static final int ReferenceToForwardTypeVariable = 16777744;
  public static final int BoundMustBeAnInterface = 16777745;
  public static final int UnsafeRawConstructorInvocation = 16777746;
  public static final int UnsafeRawMethodInvocation = 16777747;
  public static final int UnsafeTypeConversion = 16777748;
  public static final int InvalidTypeVariableExceptionType = 16777749;
  public static final int InvalidParameterizedExceptionType = 16777750;
  public static final int IllegalGenericArray = 16777751;
  public static final int UnsafeRawFieldAssignment = 16777752;
  public static final int FinalBoundForTypeVariable = 16777753;
  public static final int UndefinedTypeVariable = 536871450;
  public static final int SuperInterfacesCollide = 16777755;
  public static final int WildcardConstructorInvocation = 16777756;
  public static final int WildcardMethodInvocation = 16777757;
  public static final int WildcardFieldAssignment = 16777758;
  public static final int GenericMethodTypeArgumentMismatch = 16777759;
  public static final int GenericConstructorTypeArgumentMismatch = 16777760;
  public static final int UnsafeGenericCast = 16777761;
  public static final int IllegalInstanceofParameterizedType = 536871458;
  public static final int IllegalInstanceofTypeParameter = 536871459;
  public static final int NonGenericMethod = 16777764;
  public static final int IncorrectArityForParameterizedMethod = 16777765;
  public static final int ParameterizedMethodArgumentTypeMismatch = 16777766;
  public static final int NonGenericConstructor = 16777767;
  public static final int IncorrectArityForParameterizedConstructor = 16777768;
  public static final int ParameterizedConstructorArgumentTypeMismatch = 16777769;
  public static final int TypeArgumentsForRawGenericMethod = 16777770;
  public static final int TypeArgumentsForRawGenericConstructor = 16777771;
  public static final int SuperTypeUsingWildcard = 16777772;
  public static final int GenericTypeCannotExtendThrowable = 16777773;
  public static final int IllegalClassLiteralForTypeVariable = 16777774;
  public static final int UnsafeReturnTypeOverride = 67109423;
  public static final int MethodNameClash = 67109424;
  public static final int RawMemberTypeCannotBeParameterized = 16777777;
  public static final int MissingArgumentsForParameterizedMemberType = 16777778;
  public static final int StaticMemberOfParameterizedType = 16777779;
  public static final int BoundHasConflictingArguments = 16777780;
  public static final int DuplicateParameterizedMethods = 67109429;
  public static final int IllegalQualifiedParameterizedTypeAllocation = 16777782;
  public static final int DuplicateBounds = 16777783;
  public static final int BoundCannotBeArray = 16777784;
  public static final int UnsafeRawGenericConstructorInvocation = 16777785;
  public static final int UnsafeRawGenericMethodInvocation = 16777786;
  public static final int TypeParameterHidingType = 16777787;
  public static final int RawTypeReference = 16777788;
  public static final int NoAdditionalBoundAfterTypeVariable = 16777789;
  public static final int UnsafeGenericArrayForVarargs = 67109438;
  public static final int IllegalAccessFromTypeVariable = 16777791;
  public static final int TypeHidingTypeParameterFromType = 16777792;
  public static final int TypeHidingTypeParameterFromMethod = 16777793;
  public static final int InvalidUsageOfWildcard = 1610613314;
  public static final int UnusedTypeArgumentsForMethodInvocation = 67109443;
  public static final int IncompatibleTypesInForeach = 16777796;
  public static final int InvalidTypeForCollection = 536871493;
  public static final int InvalidUsageOfTypeParameters = 1610613326;
  public static final int InvalidUsageOfStaticImports = 1610613327;
  public static final int InvalidUsageOfForeachStatements = 1610613328;
  public static final int InvalidUsageOfTypeArguments = 1610613329;
  public static final int InvalidUsageOfEnumDeclarations = 1610613330;
  public static final int InvalidUsageOfVarargs = 1610613331;
  public static final int InvalidUsageOfAnnotations = 1610613332;
  public static final int InvalidUsageOfAnnotationDeclarations = 1610613333;
  public static final int InvalidUsageOfTypeParametersForAnnotationDeclaration = 1610613334;
  public static final int InvalidUsageOfTypeParametersForEnumDeclaration = 1610613335;
  public static final int IllegalModifierForAnnotationMethod = 67109464;
  public static final int IllegalExtendedDimensions = 67109465;
  public static final int InvalidFileNameForPackageAnnotations = 1610613338;
  public static final int IllegalModifierForAnnotationType = 16777819;
  public static final int IllegalModifierForAnnotationMemberType = 16777820;
  public static final int InvalidAnnotationMemberType = 16777821;
  public static final int AnnotationCircularitySelfReference = 16777822;
  public static final int AnnotationCircularity = 16777823;
  public static final int DuplicateAnnotation = 16777824;
  public static final int MissingValueForAnnotationMember = 16777825;
  public static final int DuplicateAnnotationMember = 536871522;
  public static final int UndefinedAnnotationMember = 67109475;
  public static final int AnnotationValueMustBeClassLiteral = 536871524;
  public static final int AnnotationValueMustBeConstant = 536871525;

  /** @deprecated */
  public static final int AnnotationFieldNeedConstantInitialization = 536871526;
  public static final int IllegalModifierForAnnotationField = 536871527;
  public static final int AnnotationCannotOverrideMethod = 67109480;
  public static final int AnnotationMembersCannotHaveParameters = 1610613353;
  public static final int AnnotationMembersCannotHaveTypeParameters = 1610613354;
  public static final int AnnotationTypeDeclarationCannotHaveSuperclass = 1610613355;
  public static final int AnnotationTypeDeclarationCannotHaveSuperinterfaces = 1610613356;
  public static final int DuplicateTargetInTargetAnnotation = 536871533;
  public static final int DisallowedTargetForAnnotation = 16777838;
  public static final int MethodMustOverride = 67109487;
  public static final int AnnotationTypeDeclarationCannotHaveConstructor = 1610613360;
  public static final int AnnotationValueMustBeAnnotation = 536871537;
  public static final int AnnotationTypeUsedAsSuperInterface = 16777842;
  public static final int MissingOverrideAnnotation = 67109491;
  public static final int FieldMissingDeprecatedAnnotation = 536871540;
  public static final int MethodMissingDeprecatedAnnotation = 536871541;
  public static final int TypeMissingDeprecatedAnnotation = 536871542;
  public static final int UnhandledWarningToken = 536871543;
  public static final int AnnotationValueMustBeArrayInitializer = 536871544;
  public static final int AnnotationValueMustBeAnEnumConstant = 536871545;
  public static final int MethodMustOverrideOrImplement = 67109498;
  public static final int UnusedWarningToken = 536871547;
  public static final int UnusedTypeArgumentsForConstructorInvocation = 67109524;
  public static final int CorruptedSignature = 536871612;
  public static final int InvalidEncoding = 536871613;
  public static final int CannotReadSource = 536871614;
  public static final int BoxingConversion = 536871632;
  public static final int UnboxingConversion = 536871633;
  public static final int IllegalModifierForEnum = 16777966;
  public static final int IllegalModifierForEnumConstant = 33555183;

  /** @deprecated */
  public static final int IllegalModifierForLocalEnum = 16777968;
  public static final int IllegalModifierForMemberEnum = 16777969;
  public static final int CannotDeclareEnumSpecialMethod = 67109618;
  public static final int IllegalQualifiedEnumConstantLabel = 33555187;
  public static final int CannotExtendEnum = 16777972;
  public static final int CannotInvokeSuperConstructorInEnum = 67109621;
  public static final int EnumAbstractMethodMustBeImplemented = 67109622;
  public static final int EnumSwitchCannotTargetField = 33555191;
  public static final int IllegalModifierForEnumConstructor = 67109624;
  public static final int MissingEnumConstantCase = 33555193;
  public static final int EnumStaticFieldInInInitializerContext = 33555194;
  public static final int EnumConstantMustImplementAbstractMethod = 67109627;
  public static final int EnumConstantCannotDefineAbstractMethod = 67109628;
  public static final int AbstractMethodInEnum = 67109629;
  public static final int IllegalExtendedDimensionsForVarArgs = 1610613536;
  public static final int MethodVarargsArgumentNeedCast = 67109665;
  public static final int ConstructorVarargsArgumentNeedCast = 134218530;
  public static final int VarargsConflict = 67109667;
  public static final int JavadocGenericMethodTypeArgumentMismatch = -1610611886;
  public static final int JavadocNonGenericMethod = -1610611885;
  public static final int JavadocIncorrectArityForParameterizedMethod = -1610611884;
  public static final int JavadocParameterizedMethodArgumentTypeMismatch = -1610611883;
  public static final int JavadocTypeArgumentsForRawGenericMethod = -1610611882;
  public static final int JavadocGenericConstructorTypeArgumentMismatch = -1610611881;
  public static final int JavadocNonGenericConstructor = -1610611880;
  public static final int JavadocIncorrectArityForParameterizedConstructor = -1610611879;
  public static final int JavadocParameterizedConstructorArgumentTypeMismatch = -1610611878;
  public static final int JavadocTypeArgumentsForRawGenericConstructor = -1610611877;
  public static final int ExternalProblemNotFixable = 900;
  public static final int ExternalProblemFixable = 901;

  public abstract String[] getArguments();

  public abstract int getID();

  public abstract String getMessage();

  public abstract char[] getOriginatingFileName();

  public abstract int getSourceEnd();

  public abstract int getSourceLineNumber();

  public abstract int getSourceStart();

  public abstract boolean isError();

  public abstract boolean isWarning();

  public abstract void setSourceEnd(int paramInt);

  public abstract void setSourceLineNumber(int paramInt);

  public abstract void setSourceStart(int paramInt);
}

/* Location:           G:\javawork\JForexClientLibrary\libs\ecj-3.5.2.jar
 * Qualified Name:     org.eclipse.jdt.core.compiler.IProblem
 * JD-Core Version:    0.6.0
 */