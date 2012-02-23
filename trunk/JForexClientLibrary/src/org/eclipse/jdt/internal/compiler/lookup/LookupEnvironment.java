/*      */ package org.eclipse.jdt.internal.compiler.lookup;
/*      */ 
/*      */ import java.util.ArrayList;
/*      */ import java.util.HashMap;
/*      */ import java.util.Map;
/*      */ import org.eclipse.jdt.core.compiler.CharOperation;
/*      */ import org.eclipse.jdt.internal.compiler.ClassFile;
/*      */ import org.eclipse.jdt.internal.compiler.ClassFilePool;
/*      */ import org.eclipse.jdt.internal.compiler.ast.CompilationUnitDeclaration;
/*      */ import org.eclipse.jdt.internal.compiler.env.AccessRestriction;
/*      */ import org.eclipse.jdt.internal.compiler.env.IBinaryType;
/*      */ import org.eclipse.jdt.internal.compiler.env.INameEnvironment;
/*      */ import org.eclipse.jdt.internal.compiler.env.NameEnvironmentAnswer;
/*      */ import org.eclipse.jdt.internal.compiler.impl.CompilerOptions;
/*      */ import org.eclipse.jdt.internal.compiler.impl.ITypeRequestor;
/*      */ import org.eclipse.jdt.internal.compiler.problem.ProblemReporter;
/*      */ import org.eclipse.jdt.internal.compiler.util.HashtableOfPackage;
/*      */ import org.eclipse.jdt.internal.compiler.util.SimpleLookupTable;
/*      */ 
/*      */ public class LookupEnvironment
/*      */   implements ProblemReasons, TypeConstants
/*      */ {
/*      */   private Map accessRestrictions;
/*      */   ImportBinding[] defaultImports;
/*      */   public PackageBinding defaultPackage;
/*      */   HashtableOfPackage knownPackages;
/*   38 */   private int lastCompletedUnitIndex = -1;
/*   39 */   private int lastUnitIndex = -1;
/*      */   public INameEnvironment nameEnvironment;
/*      */   public CompilerOptions globalOptions;
/*      */   public ProblemReporter problemReporter;
/*      */   public ClassFilePool classFilePool;
/*      */   private int stepCompleted;
/*      */   public ITypeRequestor typeRequestor;
/*      */   private ArrayBinding[][] uniqueArrayBindings;
/*      */   private SimpleLookupTable uniqueParameterizedTypeBindings;
/*      */   private SimpleLookupTable uniqueRawTypeBindings;
/*      */   private SimpleLookupTable uniqueWildcardBindings;
/*      */   private SimpleLookupTable uniqueParameterizedGenericMethodBindings;
/*   59 */   public CompilationUnitDeclaration unitBeingCompleted = null;
/*   60 */   public Object missingClassFileLocation = null;
/*   61 */   private CompilationUnitDeclaration[] units = new CompilationUnitDeclaration[4];
/*      */   private MethodVerifier verifier;
/*      */   public MethodBinding arrayClone;
/*      */   private ArrayList missingTypes;
/*   67 */   public boolean isProcessingAnnotations = false;
/*      */   static final int BUILD_FIELDS_AND_METHODS = 4;
/*      */   static final int BUILD_TYPE_HIERARCHY = 1;
/*      */   static final int CHECK_AND_SET_IMPORTS = 2;
/*      */   static final int CONNECT_TYPE_HIERARCHY = 3;
/*   74 */   static final ProblemPackageBinding TheNotFoundPackage = new ProblemPackageBinding(CharOperation.NO_CHAR, 1);
/*   75 */   static final ProblemReferenceBinding TheNotFoundType = new ProblemReferenceBinding(CharOperation.NO_CHAR_CHAR, null, 1);
/*      */ 
/*      */   public LookupEnvironment(ITypeRequestor typeRequestor, CompilerOptions globalOptions, ProblemReporter problemReporter, INameEnvironment nameEnvironment) {
/*   78 */     this.typeRequestor = typeRequestor;
/*   79 */     this.globalOptions = globalOptions;
/*   80 */     this.problemReporter = problemReporter;
/*   81 */     this.defaultPackage = new PackageBinding(this);
/*   82 */     this.defaultImports = null;
/*   83 */     this.nameEnvironment = nameEnvironment;
/*   84 */     this.knownPackages = new HashtableOfPackage();
/*   85 */     this.uniqueArrayBindings = new ArrayBinding[5][];
/*   86 */     this.uniqueArrayBindings[0] = new ArrayBinding[50];
/*   87 */     this.uniqueParameterizedTypeBindings = new SimpleLookupTable(3);
/*   88 */     this.uniqueRawTypeBindings = new SimpleLookupTable(3);
/*   89 */     this.uniqueWildcardBindings = new SimpleLookupTable(3);
/*   90 */     this.uniqueParameterizedGenericMethodBindings = new SimpleLookupTable(3);
/*   91 */     this.missingTypes = null;
/*   92 */     this.accessRestrictions = new HashMap(3);
/*   93 */     this.classFilePool = ClassFilePool.newInstance();
/*      */   }
/*      */ 
/*      */   public ReferenceBinding askForType(char[][] compoundName)
/*      */   {
/*  102 */     NameEnvironmentAnswer answer = this.nameEnvironment.findType(compoundName);
/*  103 */     if (answer == null) return null;
/*      */ 
/*  105 */     if (answer.isBinaryType())
/*      */     {
/*  107 */       this.typeRequestor.accept(answer.getBinaryType(), computePackageFrom(compoundName, false), answer.getAccessRestriction());
/*  108 */     } else if (answer.isCompilationUnit())
/*      */     {
/*  110 */       this.typeRequestor.accept(answer.getCompilationUnit(), answer.getAccessRestriction());
/*  111 */     } else if (answer.isSourceType())
/*      */     {
/*  113 */       this.typeRequestor.accept(answer.getSourceTypes(), computePackageFrom(compoundName, false), answer.getAccessRestriction());
/*      */     }
/*  115 */     return getCachedType(compoundName);
/*      */   }
/*      */ 
/*      */   ReferenceBinding askForType(PackageBinding packageBinding, char[] name)
/*      */   {
/*  122 */     if (packageBinding == null) {
/*  123 */       if (this.defaultPackage == null)
/*  124 */         return null;
/*  125 */       packageBinding = this.defaultPackage;
/*      */     }
/*  127 */     NameEnvironmentAnswer answer = this.nameEnvironment.findType(name, packageBinding.compoundName);
/*  128 */     if (answer == null) {
/*  129 */       return null;
/*      */     }
/*  131 */     if (answer.isBinaryType())
/*      */     {
/*  133 */       this.typeRequestor.accept(answer.getBinaryType(), packageBinding, answer.getAccessRestriction());
/*  134 */     } else if (answer.isCompilationUnit())
/*      */     {
/*  136 */       this.typeRequestor.accept(answer.getCompilationUnit(), answer.getAccessRestriction());
/*  137 */     } else if (answer.isSourceType())
/*      */     {
/*  139 */       this.typeRequestor.accept(answer.getSourceTypes(), packageBinding, answer.getAccessRestriction());
/*      */     }
/*  141 */     return packageBinding.getType0(name);
/*      */   }
/*      */ 
/*      */   public void buildTypeBindings(CompilationUnitDeclaration unit, AccessRestriction accessRestriction)
/*      */   {
/*  151 */     CompilationUnitScope scope = new CompilationUnitScope(unit, this);
/*  152 */     scope.buildTypeBindings(accessRestriction);
/*  153 */     int unitsLength = this.units.length;
/*  154 */     if (++this.lastUnitIndex >= unitsLength)
/*  155 */       System.arraycopy(this.units, 0, this.units = new CompilationUnitDeclaration[2 * unitsLength], 0, unitsLength);
/*  156 */     this.units[this.lastUnitIndex] = unit;
/*      */   }
/*      */ 
/*      */   public BinaryTypeBinding cacheBinaryType(IBinaryType binaryType, AccessRestriction accessRestriction)
/*      */   {
/*  164 */     return cacheBinaryType(binaryType, true, accessRestriction);
/*      */   }
/*      */ 
/*      */   public BinaryTypeBinding cacheBinaryType(IBinaryType binaryType, boolean needFieldsAndMethods, AccessRestriction accessRestriction)
/*      */   {
/*  172 */     char[][] compoundName = CharOperation.splitOn('/', binaryType.getName());
/*  173 */     ReferenceBinding existingType = getCachedType(compoundName);
/*      */ 
/*  175 */     if ((existingType == null) || ((existingType instanceof UnresolvedReferenceBinding)))
/*      */     {
/*  177 */       return createBinaryTypeFrom(binaryType, computePackageFrom(compoundName, false), needFieldsAndMethods, accessRestriction);
/*  178 */     }return null;
/*      */   }
/*      */ 
/*      */   public void completeTypeBindings() {
/*  182 */     this.stepCompleted = 1;
/*      */ 
/*  184 */     for (int i = this.lastCompletedUnitIndex + 1; i <= this.lastUnitIndex; i++) {
/*  185 */       (this.unitBeingCompleted = this.units[i]).scope.checkAndSetImports();
/*      */     }
/*  187 */     this.stepCompleted = 2;
/*      */ 
/*  189 */     for (int i = this.lastCompletedUnitIndex + 1; i <= this.lastUnitIndex; i++) {
/*  190 */       (this.unitBeingCompleted = this.units[i]).scope.connectTypeHierarchy();
/*      */     }
/*  192 */     this.stepCompleted = 3;
/*      */ 
/*  194 */     for (int i = this.lastCompletedUnitIndex + 1; i <= this.lastUnitIndex; i++) {
/*  195 */       CompilationUnitScope unitScope = (this.unitBeingCompleted = this.units[i]).scope;
/*  196 */       unitScope.checkParameterizedTypes();
/*  197 */       unitScope.buildFieldsAndMethods();
/*  198 */       this.units[i] = null;
/*      */     }
/*  200 */     this.stepCompleted = 4;
/*  201 */     this.lastCompletedUnitIndex = this.lastUnitIndex;
/*  202 */     this.unitBeingCompleted = null;
/*      */   }
/*      */ 
/*      */   public void completeTypeBindings(CompilationUnitDeclaration parsedUnit)
/*      */   {
/*  218 */     if (this.stepCompleted == 4)
/*      */     {
/*  222 */       completeTypeBindings();
/*      */     } else {
/*  224 */       if (parsedUnit.scope == null) return;
/*      */ 
/*  226 */       if (this.stepCompleted >= 2) {
/*  227 */         (this.unitBeingCompleted = parsedUnit).scope.checkAndSetImports();
/*      */       }
/*  229 */       if (this.stepCompleted >= 3) {
/*  230 */         (this.unitBeingCompleted = parsedUnit).scope.connectTypeHierarchy();
/*      */       }
/*  232 */       this.unitBeingCompleted = null;
/*      */     }
/*      */   }
/*      */ 
/*      */   public void completeTypeBindings(CompilationUnitDeclaration parsedUnit, boolean buildFieldsAndMethods)
/*      */   {
/*  250 */     if (parsedUnit.scope == null) return;
/*      */ 
/*  252 */     (this.unitBeingCompleted = parsedUnit).scope.checkAndSetImports();
/*  253 */     parsedUnit.scope.connectTypeHierarchy();
/*  254 */     parsedUnit.scope.checkParameterizedTypes();
/*  255 */     if (buildFieldsAndMethods)
/*  256 */       parsedUnit.scope.buildFieldsAndMethods();
/*  257 */     this.unitBeingCompleted = null;
/*      */   }
/*      */   public MethodBinding computeArrayClone(MethodBinding objectClone) {
/*  260 */     if (this.arrayClone == null) {
/*  261 */       this.arrayClone = 
/*  267 */         new MethodBinding(objectClone.modifiers & 0xFFFFFFFB | 0x1, 
/*  263 */         TypeConstants.CLONE, 
/*  264 */         objectClone.returnType, 
/*  265 */         Binding.NO_PARAMETERS, 
/*  266 */         Binding.NO_EXCEPTIONS, 
/*  267 */         (ReferenceBinding)objectClone.returnType);
/*      */     }
/*  269 */     return this.arrayClone;
/*      */   }
/*      */ 
/*      */   public TypeBinding computeBoxingType(TypeBinding type)
/*      */   {
/*  274 */     switch (type.id) {
/*      */     case 33:
/*  276 */       return TypeBinding.BOOLEAN;
/*      */     case 26:
/*  278 */       return TypeBinding.BYTE;
/*      */     case 28:
/*  280 */       return TypeBinding.CHAR;
/*      */     case 27:
/*  282 */       return TypeBinding.SHORT;
/*      */     case 32:
/*  284 */       return TypeBinding.DOUBLE;
/*      */     case 31:
/*  286 */       return TypeBinding.FLOAT;
/*      */     case 29:
/*  288 */       return TypeBinding.INT;
/*      */     case 30:
/*  290 */       return TypeBinding.LONG;
/*      */     case 10:
/*  293 */       TypeBinding boxedType = getType(JAVA_LANG_INTEGER);
/*  294 */       if (boxedType != null) return boxedType;
/*  295 */       return new ProblemReferenceBinding(JAVA_LANG_INTEGER, null, 1);
/*      */     case 3:
/*  297 */       TypeBinding boxedType = getType(JAVA_LANG_BYTE);
/*  298 */       if (boxedType != null) return boxedType;
/*  299 */       return new ProblemReferenceBinding(JAVA_LANG_BYTE, null, 1);
/*      */     case 4:
/*  301 */       TypeBinding boxedType = getType(JAVA_LANG_SHORT);
/*  302 */       if (boxedType != null) return boxedType;
/*  303 */       return new ProblemReferenceBinding(JAVA_LANG_SHORT, null, 1);
/*      */     case 2:
/*  305 */       TypeBinding boxedType = getType(JAVA_LANG_CHARACTER);
/*  306 */       if (boxedType != null) return boxedType;
/*  307 */       return new ProblemReferenceBinding(JAVA_LANG_CHARACTER, null, 1);
/*      */     case 7:
/*  309 */       TypeBinding boxedType = getType(JAVA_LANG_LONG);
/*  310 */       if (boxedType != null) return boxedType;
/*  311 */       return new ProblemReferenceBinding(JAVA_LANG_LONG, null, 1);
/*      */     case 9:
/*  313 */       TypeBinding boxedType = getType(JAVA_LANG_FLOAT);
/*  314 */       if (boxedType != null) return boxedType;
/*  315 */       return new ProblemReferenceBinding(JAVA_LANG_FLOAT, null, 1);
/*      */     case 8:
/*  317 */       TypeBinding boxedType = getType(JAVA_LANG_DOUBLE);
/*  318 */       if (boxedType != null) return boxedType;
/*  319 */       return new ProblemReferenceBinding(JAVA_LANG_DOUBLE, null, 1);
/*      */     case 5:
/*  321 */       TypeBinding boxedType = getType(JAVA_LANG_BOOLEAN);
/*  322 */       if (boxedType != null) return boxedType;
/*  323 */       return new ProblemReferenceBinding(JAVA_LANG_BOOLEAN, null, 1);
/*      */     case 6:
/*      */     case 11:
/*      */     case 12:
/*      */     case 13:
/*      */     case 14:
/*      */     case 15:
/*      */     case 16:
/*      */     case 17:
/*      */     case 18:
/*      */     case 19:
/*      */     case 20:
/*      */     case 21:
/*      */     case 22:
/*      */     case 23:
/*      */     case 24:
/*      */     case 25:
/*      */     }
/*      */ 
/*  342 */     switch (type.kind()) {
/*      */     case 516:
/*      */     case 4100:
/*      */     case 8196:
/*  346 */       switch (type.erasure().id) {
/*      */       case 33:
/*  348 */         return TypeBinding.BOOLEAN;
/*      */       case 26:
/*  350 */         return TypeBinding.BYTE;
/*      */       case 28:
/*  352 */         return TypeBinding.CHAR;
/*      */       case 27:
/*  354 */         return TypeBinding.SHORT;
/*      */       case 32:
/*  356 */         return TypeBinding.DOUBLE;
/*      */       case 31:
/*  358 */         return TypeBinding.FLOAT;
/*      */       case 29:
/*  360 */         return TypeBinding.INT;
/*      */       case 30:
/*  362 */         return TypeBinding.LONG;
/*      */       }
/*      */     }
/*  365 */     return type;
/*      */   }
/*      */ 
/*      */   private PackageBinding computePackageFrom(char[][] constantPoolName, boolean isMissing) {
/*  369 */     if (constantPoolName.length == 1) {
/*  370 */       return this.defaultPackage;
/*      */     }
/*  372 */     PackageBinding packageBinding = getPackage0(constantPoolName[0]);
/*  373 */     if ((packageBinding == null) || (packageBinding == TheNotFoundPackage)) {
/*  374 */       packageBinding = new PackageBinding(constantPoolName[0], this);
/*  375 */       if (isMissing) packageBinding.tagBits |= 128L;
/*  376 */       this.knownPackages.put(constantPoolName[0], packageBinding);
/*      */     }
/*      */ 
/*  379 */     int i = 1; for (int length = constantPoolName.length - 1; i < length; i++) {
/*  380 */       PackageBinding parent = packageBinding;
/*  381 */       if (((packageBinding = parent.getPackage0(constantPoolName[i])) == null) || (packageBinding == TheNotFoundPackage)) {
/*  382 */         packageBinding = new PackageBinding(CharOperation.subarray(constantPoolName, 0, i + 1), parent, this);
/*  383 */         if (isMissing) {
/*  384 */           packageBinding.tagBits |= 128L;
/*      */         }
/*  386 */         parent.addPackage(packageBinding);
/*      */       }
/*      */     }
/*  389 */     return packageBinding;
/*      */   }
/*      */ 
/*      */   public ReferenceBinding convertToParameterizedType(ReferenceBinding originalType)
/*      */   {
/*  397 */     if (originalType != null) {
/*  398 */       boolean isGeneric = originalType.isGenericType();
/*  399 */       ReferenceBinding originalEnclosingType = originalType.enclosingType();
/*  400 */       ReferenceBinding convertedEnclosingType = originalEnclosingType;
/*  401 */       boolean needToConvert = isGeneric;
/*  402 */       if (originalEnclosingType != null) {
/*  403 */         convertedEnclosingType = originalType.isStatic() ? 
/*  404 */           (ReferenceBinding)convertToRawType(originalEnclosingType, false) : 
/*  405 */           convertToParameterizedType(originalEnclosingType);
/*  406 */         needToConvert |= originalEnclosingType != convertedEnclosingType;
/*      */       }
/*  408 */       if (needToConvert) {
/*  409 */         return createParameterizedType(originalType, isGeneric ? originalType.typeVariables() : null, convertedEnclosingType);
/*      */       }
/*      */     }
/*  412 */     return originalType;
/*      */   }
/*      */ 
/*      */   public TypeBinding convertToRawType(TypeBinding type, boolean forceRawEnclosingType)
/*      */   {
/*      */     TypeBinding originalType;
/*      */     int dimension;
/*      */     TypeBinding originalType;
/*  424 */     switch (type.kind()) {
/*      */     case 132:
/*      */     case 516:
/*      */     case 1028:
/*      */     case 4100:
/*      */     case 8196:
/*  430 */       return type;
/*      */     case 68:
/*  432 */       int dimension = type.dimensions();
/*  433 */       originalType = type.leafComponentType();
/*  434 */       break;
/*      */     default:
/*  436 */       if (type.id == 1)
/*  437 */         return type;
/*  438 */       dimension = 0;
/*  439 */       originalType = type;
/*      */     }
/*      */     boolean needToConvert;
/*      */     boolean needToConvert;
/*      */     boolean needToConvert;
/*  442 */     switch (originalType.kind()) {
/*      */     case 132:
/*  444 */       return type;
/*      */     case 2052:
/*  446 */       needToConvert = true;
/*  447 */       break;
/*      */     case 260:
/*  449 */       ParameterizedTypeBinding paramType = (ParameterizedTypeBinding)originalType;
/*  450 */       needToConvert = paramType.genericType().isGenericType();
/*  451 */       break;
/*      */     default:
/*  453 */       needToConvert = false;
/*      */     }
/*      */ 
/*  456 */     ReferenceBinding originalEnclosing = originalType.enclosingType();
/*      */     TypeBinding convertedType;
/*      */     TypeBinding convertedType;
/*  458 */     if (originalEnclosing == null) {
/*  459 */       convertedType = needToConvert ? createRawType((ReferenceBinding)originalType.erasure(), null) : originalType;
/*      */     }
/*      */     else
/*      */     {
/*      */       ReferenceBinding convertedEnclosing;
/*      */       ReferenceBinding convertedEnclosing;
/*  462 */       if (originalEnclosing.kind() == 1028) {
/*  463 */         needToConvert |= !((ReferenceBinding)originalType).isStatic();
/*  464 */         convertedEnclosing = originalEnclosing;
/*  465 */       } else if ((forceRawEnclosingType) && (!needToConvert)) {
/*  466 */         ReferenceBinding convertedEnclosing = (ReferenceBinding)convertToRawType(originalEnclosing, forceRawEnclosingType);
/*  467 */         needToConvert = originalEnclosing != convertedEnclosing;
/*      */       }
/*      */       else
/*      */       {
/*      */         ReferenceBinding convertedEnclosing;
/*  468 */         if ((needToConvert) || (((ReferenceBinding)originalType).isStatic()))
/*  469 */           convertedEnclosing = (ReferenceBinding)convertToRawType(originalEnclosing, false);
/*      */         else
/*  471 */           convertedEnclosing = convertToParameterizedType(originalEnclosing);
/*      */       }
/*      */       TypeBinding convertedType;
/*  473 */       if (needToConvert) {
/*  474 */         convertedType = createRawType((ReferenceBinding)originalType.erasure(), convertedEnclosing);
/*      */       }
/*      */       else
/*      */       {
/*      */         TypeBinding convertedType;
/*  475 */         if (originalEnclosing != convertedEnclosing)
/*  476 */           convertedType = createParameterizedType((ReferenceBinding)originalType.erasure(), null, convertedEnclosing);
/*      */         else
/*  478 */           convertedType = originalType;
/*      */       }
/*      */     }
/*  481 */     if (originalType != convertedType) {
/*  482 */       return dimension > 0 ? createArrayType(convertedType, dimension) : convertedType;
/*      */     }
/*  484 */     return type;
/*      */   }
/*      */ 
/*      */   public ReferenceBinding[] convertToRawTypes(ReferenceBinding[] originalTypes, boolean forceErasure, boolean forceRawEnclosingType)
/*      */   {
/*  492 */     if (originalTypes == null) return null;
/*  493 */     ReferenceBinding[] convertedTypes = originalTypes;
/*  494 */     int i = 0; for (int length = originalTypes.length; i < length; i++) {
/*  495 */       ReferenceBinding originalType = originalTypes[i];
/*  496 */       ReferenceBinding convertedType = (ReferenceBinding)convertToRawType(forceErasure ? originalType.erasure() : originalType, forceRawEnclosingType);
/*  497 */       if (convertedType != originalType) {
/*  498 */         if (convertedTypes == originalTypes) {
/*  499 */           System.arraycopy(originalTypes, 0, convertedTypes = new ReferenceBinding[length], 0, i);
/*      */         }
/*  501 */         convertedTypes[i] = convertedType;
/*  502 */       } else if (convertedTypes != originalTypes) {
/*  503 */         convertedTypes[i] = originalType;
/*      */       }
/*      */     }
/*  506 */     return convertedTypes;
/*      */   }
/*      */ 
/*      */   public TypeBinding convertUnresolvedBinaryToRawType(TypeBinding type)
/*      */   {
/*      */     TypeBinding originalType;
/*      */     int dimension;
/*      */     TypeBinding originalType;
/*  513 */     switch (type.kind()) {
/*      */     case 132:
/*      */     case 516:
/*      */     case 1028:
/*      */     case 4100:
/*      */     case 8196:
/*  519 */       return type;
/*      */     case 68:
/*  521 */       int dimension = type.dimensions();
/*  522 */       originalType = type.leafComponentType();
/*  523 */       break;
/*      */     default:
/*  525 */       if (type.id == 1)
/*  526 */         return type;
/*  527 */       dimension = 0;
/*  528 */       originalType = type;
/*      */     }
/*      */     boolean needToConvert;
/*      */     boolean needToConvert;
/*      */     boolean needToConvert;
/*  531 */     switch (originalType.kind()) {
/*      */     case 132:
/*  533 */       return type;
/*      */     case 2052:
/*  535 */       needToConvert = true;
/*  536 */       break;
/*      */     case 260:
/*  538 */       ParameterizedTypeBinding paramType = (ParameterizedTypeBinding)originalType;
/*  539 */       needToConvert = paramType.genericType().isGenericType();
/*  540 */       break;
/*      */     default:
/*  542 */       needToConvert = false;
/*      */     }
/*      */ 
/*  545 */     ReferenceBinding originalEnclosing = originalType.enclosingType();
/*      */     TypeBinding convertedType;
/*      */     TypeBinding convertedType;
/*  547 */     if (originalEnclosing == null) {
/*  548 */       convertedType = needToConvert ? createRawType((ReferenceBinding)originalType.erasure(), null) : originalType;
/*      */     } else {
/*  550 */       ReferenceBinding convertedEnclosing = (ReferenceBinding)convertUnresolvedBinaryToRawType(originalEnclosing);
/*  551 */       if (convertedEnclosing != originalEnclosing)
/*  552 */         needToConvert |= !((ReferenceBinding)originalType).isStatic();
/*      */       TypeBinding convertedType;
/*  554 */       if (needToConvert) {
/*  555 */         convertedType = createRawType((ReferenceBinding)originalType.erasure(), convertedEnclosing);
/*      */       }
/*      */       else
/*      */       {
/*      */         TypeBinding convertedType;
/*  556 */         if (originalEnclosing != convertedEnclosing)
/*  557 */           convertedType = createParameterizedType((ReferenceBinding)originalType.erasure(), null, convertedEnclosing);
/*      */         else
/*  559 */           convertedType = originalType;
/*      */       }
/*      */     }
/*  562 */     if (originalType != convertedType) {
/*  563 */       return dimension > 0 ? createArrayType(convertedType, dimension) : convertedType;
/*      */     }
/*  565 */     return type;
/*      */   }
/*      */ 
/*      */   public AnnotationBinding createAnnotation(ReferenceBinding annotationType, ElementValuePair[] pairs)
/*      */   {
/*  571 */     if (pairs.length != 0) {
/*  572 */       AnnotationBinding.setMethodBindings(annotationType, pairs);
/*      */     }
/*  574 */     return new AnnotationBinding(annotationType, pairs);
/*      */   }
/*      */ 
/*      */   public ArrayBinding createArrayType(TypeBinding leafComponentType, int dimensionCount)
/*      */   {
/*  581 */     if ((leafComponentType instanceof LocalTypeBinding)) {
/*  582 */       return ((LocalTypeBinding)leafComponentType).createArrayType(dimensionCount, this);
/*      */     }
/*      */ 
/*  585 */     int dimIndex = dimensionCount - 1;
/*  586 */     int length = this.uniqueArrayBindings.length;
/*      */     ArrayBinding[] arrayBindings;
/*  588 */     if (dimIndex < length)
/*      */     {
/*      */       ArrayBinding[] arrayBindings;
/*  589 */       if ((arrayBindings = this.uniqueArrayBindings[dimIndex]) == null)
/*      */       {
/*      */         ArrayBinding[] tmp56_53 = new ArrayBinding[10]; arrayBindings = tmp56_53; this.uniqueArrayBindings[dimIndex] = tmp56_53;
/*      */       }
/*      */     } else {
/*  592 */       System.arraycopy(
/*  593 */         this.uniqueArrayBindings, 0, 
/*  594 */         this.uniqueArrayBindings = new ArrayBinding[dimensionCount][], 0, 
/*  595 */         length);
/*      */       ArrayBinding[] tmp93_90 = new ArrayBinding[10]; arrayBindings = tmp93_90; this.uniqueArrayBindings[dimIndex] = tmp93_90;
/*      */     }
/*      */ 
/*  600 */     int index = -1;
/*  601 */     length = arrayBindings.length;
/*      */     do {
/*  603 */       ArrayBinding currentBinding = arrayBindings[index];
/*  604 */       if (currentBinding == null)
/*  605 */         return arrayBindings[index] =  = new ArrayBinding(leafComponentType, dimensionCount, this);
/*  606 */       if (currentBinding.leafComponentType == leafComponentType)
/*  607 */         return currentBinding;
/*  602 */       index++; } while (index < length);
/*      */ 
/*  611 */     System.arraycopy(
/*  612 */       arrayBindings, 0, 
/*  613 */       arrayBindings = new ArrayBinding[length * 2], 0, 
/*  614 */       length);
/*  615 */     this.uniqueArrayBindings[dimIndex] = arrayBindings;
/*  616 */     return arrayBindings[length] =  = new ArrayBinding(leafComponentType, dimensionCount, this);
/*      */   }
/*      */   public BinaryTypeBinding createBinaryTypeFrom(IBinaryType binaryType, PackageBinding packageBinding, AccessRestriction accessRestriction) {
/*  619 */     return createBinaryTypeFrom(binaryType, packageBinding, true, accessRestriction);
/*      */   }
/*      */ 
/*      */   public BinaryTypeBinding createBinaryTypeFrom(IBinaryType binaryType, PackageBinding packageBinding, boolean needFieldsAndMethods, AccessRestriction accessRestriction) {
/*  623 */     BinaryTypeBinding binaryBinding = new BinaryTypeBinding(packageBinding, binaryType, this);
/*      */ 
/*  626 */     ReferenceBinding cachedType = packageBinding.getType0(binaryBinding.compoundName[(binaryBinding.compoundName.length - 1)]);
/*  627 */     if (cachedType != null) {
/*  628 */       if ((cachedType instanceof UnresolvedReferenceBinding)) {
/*  629 */         ((UnresolvedReferenceBinding)cachedType).setResolvedType(binaryBinding, this);
/*      */       } else {
/*  631 */         if (cachedType.isBinaryBinding()) {
/*  632 */           return (BinaryTypeBinding)cachedType;
/*      */         }
/*      */ 
/*  635 */         return null;
/*      */       }
/*      */     }
/*  638 */     packageBinding.addType(binaryBinding);
/*  639 */     setAccessRestriction(binaryBinding, accessRestriction);
/*  640 */     binaryBinding.cachePartsFrom(binaryType, needFieldsAndMethods);
/*  641 */     return binaryBinding;
/*      */   }
/*      */ 
/*      */   public MissingTypeBinding createMissingType(PackageBinding packageBinding, char[][] compoundName)
/*      */   {
/*  651 */     if (packageBinding == null) {
/*  652 */       packageBinding = computePackageFrom(compoundName, true);
/*  653 */       if (packageBinding == TheNotFoundPackage) packageBinding = this.defaultPackage;
/*      */     }
/*  655 */     MissingTypeBinding missingType = new MissingTypeBinding(packageBinding, compoundName, this);
/*  656 */     if (missingType.id != 1)
/*      */     {
/*  658 */       ReferenceBinding objectType = getType(TypeConstants.JAVA_LANG_OBJECT);
/*  659 */       if (objectType == null) {
/*  660 */         objectType = createMissingType(null, TypeConstants.JAVA_LANG_OBJECT);
/*      */       }
/*  662 */       missingType.setMissingSuperclass(objectType);
/*      */     }
/*  664 */     packageBinding.addType(missingType);
/*  665 */     if (this.missingTypes == null)
/*  666 */       this.missingTypes = new ArrayList(3);
/*  667 */     this.missingTypes.add(missingType);
/*  668 */     return missingType;
/*      */   }
/*      */ 
/*      */   public PackageBinding createPackage(char[][] compoundName)
/*      */   {
/*  677 */     PackageBinding packageBinding = getPackage0(compoundName[0]);
/*  678 */     if ((packageBinding == null) || (packageBinding == TheNotFoundPackage)) {
/*  679 */       packageBinding = new PackageBinding(compoundName[0], this);
/*  680 */       this.knownPackages.put(compoundName[0], packageBinding);
/*      */     }
/*      */ 
/*  683 */     int i = 1; for (int length = compoundName.length; i < length; i++)
/*      */     {
/*  690 */       ReferenceBinding type = packageBinding.getType0(compoundName[i]);
/*  691 */       if ((type != null) && (type != TheNotFoundType) && (!(type instanceof UnresolvedReferenceBinding))) {
/*  692 */         return null;
/*      */       }
/*  694 */       PackageBinding parent = packageBinding;
/*  695 */       if (((packageBinding = parent.getPackage0(compoundName[i])) != null) && (packageBinding != TheNotFoundPackage))
/*      */       {
/*      */         continue;
/*      */       }
/*      */ 
/*  700 */       if (this.nameEnvironment.findType(compoundName[i], parent.compoundName) != null) {
/*  701 */         return null;
/*      */       }
/*  703 */       packageBinding = new PackageBinding(CharOperation.subarray(compoundName, 0, i + 1), parent, this);
/*  704 */       parent.addPackage(packageBinding);
/*      */     }
/*      */ 
/*  707 */     return packageBinding;
/*      */   }
/*      */ 
/*      */   public ParameterizedGenericMethodBinding createParameterizedGenericMethod(MethodBinding genericMethod, RawTypeBinding rawType)
/*      */   {
/*  712 */     ParameterizedGenericMethodBinding[] cachedInfo = (ParameterizedGenericMethodBinding[])this.uniqueParameterizedGenericMethodBindings.get(genericMethod);
/*  713 */     boolean needToGrow = false;
/*  714 */     int index = 0;
/*  715 */     if (cachedInfo != null)
/*      */     {
/*  718 */       for (int max = cachedInfo.length; index < max; index++) {
/*  719 */         ParameterizedGenericMethodBinding cachedMethod = cachedInfo[index];
/*  720 */         if (cachedMethod == null) break;
/*  721 */         if (cachedMethod.isRaw)
/*  722 */           if (cachedMethod.declaringClass == (rawType == null ? genericMethod.declaringClass : rawType))
/*  723 */             return cachedMethod;
/*      */       }
/*  725 */       needToGrow = true;
/*      */     } else {
/*  727 */       cachedInfo = new ParameterizedGenericMethodBinding[5];
/*  728 */       this.uniqueParameterizedGenericMethodBindings.put(genericMethod, cachedInfo);
/*      */     }
/*      */ 
/*  731 */     int length = cachedInfo.length;
/*  732 */     if ((needToGrow) && (index == length)) {
/*  733 */       System.arraycopy(cachedInfo, 0, cachedInfo = new ParameterizedGenericMethodBinding[length * 2], 0, length);
/*  734 */       this.uniqueParameterizedGenericMethodBindings.put(genericMethod, cachedInfo);
/*      */     }
/*      */ 
/*  737 */     ParameterizedGenericMethodBinding parameterizedGenericMethod = new ParameterizedGenericMethodBinding(genericMethod, rawType, this);
/*  738 */     cachedInfo[index] = parameterizedGenericMethod;
/*  739 */     return parameterizedGenericMethod;
/*      */   }
/*      */ 
/*      */   public ParameterizedGenericMethodBinding createParameterizedGenericMethod(MethodBinding genericMethod, TypeBinding[] typeArguments)
/*      */   {
/*  744 */     ParameterizedGenericMethodBinding[] cachedInfo = (ParameterizedGenericMethodBinding[])this.uniqueParameterizedGenericMethodBindings.get(genericMethod);
/*  745 */     int argLength = typeArguments == null ? 0 : typeArguments.length;
/*  746 */     boolean needToGrow = false;
/*  747 */     int index = 0;
/*  748 */     if (cachedInfo != null)
/*      */     {
/*  751 */       for (int max = cachedInfo.length; index < max; index++) {
/*  752 */         ParameterizedGenericMethodBinding cachedMethod = cachedInfo[index];
/*  753 */         if (cachedMethod == null) break;
/*  754 */         if (!cachedMethod.isRaw) {
/*  755 */           TypeBinding[] cachedArguments = cachedMethod.typeArguments;
/*  756 */           int cachedArgLength = cachedArguments == null ? 0 : cachedArguments.length;
/*  757 */           if (argLength == cachedArgLength) {
/*  758 */             int j = 0;
/*  759 */             while (typeArguments[j] == cachedArguments[j])
/*      */             {
/*  758 */               j++; if (j >= cachedArgLength)
/*      */               {
/*  762 */                 return cachedMethod;
/*      */               }
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/*  764 */       needToGrow = true;
/*      */     } else {
/*  766 */       cachedInfo = new ParameterizedGenericMethodBinding[5];
/*  767 */       this.uniqueParameterizedGenericMethodBindings.put(genericMethod, cachedInfo);
/*      */     }
/*      */ 
/*  770 */     int length = cachedInfo.length;
/*  771 */     if ((needToGrow) && (index == length)) {
/*  772 */       System.arraycopy(cachedInfo, 0, cachedInfo = new ParameterizedGenericMethodBinding[length * 2], 0, length);
/*  773 */       this.uniqueParameterizedGenericMethodBindings.put(genericMethod, cachedInfo);
/*      */     }
/*      */ 
/*  776 */     ParameterizedGenericMethodBinding parameterizedGenericMethod = new ParameterizedGenericMethodBinding(genericMethod, typeArguments, this);
/*  777 */     cachedInfo[index] = parameterizedGenericMethod;
/*  778 */     return parameterizedGenericMethod;
/*      */   }
/*      */ 
/*      */   public ParameterizedTypeBinding createParameterizedType(ReferenceBinding genericType, TypeBinding[] typeArguments, ReferenceBinding enclosingType)
/*      */   {
/*  783 */     ParameterizedTypeBinding[] cachedInfo = (ParameterizedTypeBinding[])this.uniqueParameterizedTypeBindings.get(genericType);
/*  784 */     int argLength = typeArguments == null ? 0 : typeArguments.length;
/*  785 */     boolean needToGrow = false;
/*  786 */     int index = 0;
/*  787 */     if (cachedInfo != null)
/*      */     {
/*  790 */       for (int max = cachedInfo.length; index < max; index++) {
/*  791 */         ParameterizedTypeBinding cachedType = cachedInfo[index];
/*  792 */         if (cachedType == null) break;
/*  793 */         if ((cachedType.actualType() != genericType) || 
/*  794 */           (cachedType.enclosingType() != enclosingType)) continue;
/*  795 */         TypeBinding[] cachedArguments = cachedType.arguments;
/*  796 */         int cachedArgLength = cachedArguments == null ? 0 : cachedArguments.length;
/*  797 */         if (argLength == cachedArgLength) {
/*  798 */           int j = 0;
/*  799 */           while (typeArguments[j] == cachedArguments[j])
/*      */           {
/*  798 */             j++; if (j >= cachedArgLength)
/*      */             {
/*  802 */               return cachedType;
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/*  804 */       needToGrow = true;
/*      */     } else {
/*  806 */       cachedInfo = new ParameterizedTypeBinding[5];
/*  807 */       this.uniqueParameterizedTypeBindings.put(genericType, cachedInfo);
/*      */     }
/*      */ 
/*  810 */     int length = cachedInfo.length;
/*  811 */     if ((needToGrow) && (index == length)) {
/*  812 */       System.arraycopy(cachedInfo, 0, cachedInfo = new ParameterizedTypeBinding[length * 2], 0, length);
/*  813 */       this.uniqueParameterizedTypeBindings.put(genericType, cachedInfo);
/*      */     }
/*      */ 
/*  816 */     ParameterizedTypeBinding parameterizedType = new ParameterizedTypeBinding(genericType, typeArguments, enclosingType, this);
/*  817 */     cachedInfo[index] = parameterizedType;
/*  818 */     return parameterizedType;
/*      */   }
/*      */ 
/*      */   public RawTypeBinding createRawType(ReferenceBinding genericType, ReferenceBinding enclosingType)
/*      */   {
/*  823 */     RawTypeBinding[] cachedInfo = (RawTypeBinding[])this.uniqueRawTypeBindings.get(genericType);
/*  824 */     boolean needToGrow = false;
/*  825 */     int index = 0;
/*  826 */     if (cachedInfo != null)
/*      */     {
/*  829 */       for (int max = cachedInfo.length; index < max; index++) {
/*  830 */         RawTypeBinding cachedType = cachedInfo[index];
/*  831 */         if (cachedType == null) break;
/*  832 */         if ((cachedType.actualType() == genericType) && 
/*  833 */           (cachedType.enclosingType() == enclosingType))
/*      */         {
/*  835 */           return cachedType;
/*      */         }
/*      */       }
/*  837 */       needToGrow = true;
/*      */     } else {
/*  839 */       cachedInfo = new RawTypeBinding[1];
/*  840 */       this.uniqueRawTypeBindings.put(genericType, cachedInfo);
/*      */     }
/*      */ 
/*  843 */     int length = cachedInfo.length;
/*  844 */     if ((needToGrow) && (index == length)) {
/*  845 */       System.arraycopy(cachedInfo, 0, cachedInfo = new RawTypeBinding[length * 2], 0, length);
/*  846 */       this.uniqueRawTypeBindings.put(genericType, cachedInfo);
/*      */     }
/*      */ 
/*  849 */     RawTypeBinding rawType = new RawTypeBinding(genericType, enclosingType, this);
/*  850 */     cachedInfo[index] = rawType;
/*  851 */     return rawType;
/*      */   }
/*      */ 
/*      */   public WildcardBinding createWildcard(ReferenceBinding genericType, int rank, TypeBinding bound, TypeBinding[] otherBounds, int boundKind)
/*      */   {
/*  857 */     if (genericType == null)
/*  858 */       genericType = ReferenceBinding.LUB_GENERIC;
/*  859 */     WildcardBinding[] cachedInfo = (WildcardBinding[])this.uniqueWildcardBindings.get(genericType);
/*  860 */     boolean needToGrow = false;
/*  861 */     int index = 0;
/*  862 */     if (cachedInfo != null)
/*      */     {
/*  865 */       for (int max = cachedInfo.length; index < max; index++) {
/*  866 */         WildcardBinding cachedType = cachedInfo[index];
/*  867 */         if (cachedType == null) break;
/*  868 */         if ((cachedType.genericType != genericType) || 
/*  869 */           (cachedType.rank != rank) || 
/*  870 */           (cachedType.boundKind != boundKind) || 
/*  871 */           (cachedType.bound != bound)) continue;
/*  872 */         if (cachedType.otherBounds != otherBounds) {
/*  873 */           int cachedLength = cachedType.otherBounds == null ? 0 : cachedType.otherBounds.length;
/*  874 */           int length = otherBounds == null ? 0 : otherBounds.length;
/*  875 */           if (cachedLength == length)
/*  876 */             for (int j = 0; j < length; j++)
/*  877 */               if (cachedType.otherBounds[j] != otherBounds[j])
/*      */                 break;
/*      */         }
/*      */         else {
/*  881 */           return cachedType;
/*      */         }
/*      */       }
/*  883 */       needToGrow = true;
/*      */     } else {
/*  885 */       cachedInfo = new WildcardBinding[10];
/*  886 */       this.uniqueWildcardBindings.put(genericType, cachedInfo);
/*      */     }
/*      */ 
/*  889 */     int length = cachedInfo.length;
/*  890 */     if ((needToGrow) && (index == length)) {
/*  891 */       System.arraycopy(cachedInfo, 0, cachedInfo = new WildcardBinding[length * 2], 0, length);
/*  892 */       this.uniqueWildcardBindings.put(genericType, cachedInfo);
/*      */     }
/*      */ 
/*  895 */     WildcardBinding wildcard = new WildcardBinding(genericType, rank, bound, otherBounds, boundKind, this);
/*  896 */     cachedInfo[index] = wildcard;
/*  897 */     return wildcard;
/*      */   }
/*      */ 
/*      */   public AccessRestriction getAccessRestriction(TypeBinding type)
/*      */   {
/*  904 */     return (AccessRestriction)this.accessRestrictions.get(type);
/*      */   }
/*      */ 
/*      */   public ReferenceBinding getCachedType(char[][] compoundName)
/*      */   {
/*  916 */     if (compoundName.length == 1) {
/*  917 */       if (this.defaultPackage == null)
/*  918 */         return null;
/*  919 */       return this.defaultPackage.getType0(compoundName[0]);
/*      */     }
/*  921 */     PackageBinding packageBinding = getPackage0(compoundName[0]);
/*  922 */     if ((packageBinding == null) || (packageBinding == TheNotFoundPackage)) {
/*  923 */       return null;
/*      */     }
/*  925 */     int i = 1; for (int packageLength = compoundName.length - 1; i < packageLength; i++)
/*  926 */       if (((packageBinding = packageBinding.getPackage0(compoundName[i])) == null) || (packageBinding == TheNotFoundPackage))
/*  927 */         return null;
/*  928 */     return packageBinding.getType0(compoundName[(compoundName.length - 1)]);
/*      */   }
/*      */ 
/*      */   PackageBinding getPackage0(char[] name)
/*      */   {
/*  939 */     return this.knownPackages.get(name);
/*      */   }
/*      */ 
/*      */   public ReferenceBinding getResolvedType(char[][] compoundName, Scope scope)
/*      */   {
/*  947 */     ReferenceBinding type = getType(compoundName);
/*  948 */     if (type != null) return type;
/*      */ 
/*  952 */     this.problemReporter.isClassPathCorrect(
/*  953 */       compoundName, 
/*  954 */       scope == null ? this.unitBeingCompleted : scope.referenceCompilationUnit(), 
/*  955 */       this.missingClassFileLocation);
/*  956 */     return createMissingType(null, compoundName);
/*      */   }
/*      */ 
/*      */   PackageBinding getTopLevelPackage(char[] name)
/*      */   {
/*  964 */     PackageBinding packageBinding = getPackage0(name);
/*  965 */     if (packageBinding != null) {
/*  966 */       if (packageBinding == TheNotFoundPackage)
/*  967 */         return null;
/*  968 */       return packageBinding;
/*      */     }
/*      */ 
/*  971 */     if (this.nameEnvironment.isPackage(null, name)) {
/*  972 */       this.knownPackages.put(name, packageBinding = new PackageBinding(name, this));
/*  973 */       return packageBinding;
/*      */     }
/*      */ 
/*  976 */     this.knownPackages.put(name, TheNotFoundPackage);
/*  977 */     return null;
/*      */   }
/*      */ 
/*      */   public ReferenceBinding getType(char[][] compoundName)
/*      */   {
/*  987 */     if (compoundName.length == 1) {
/*  988 */       if (this.defaultPackage == null)
/*  989 */         return null;
/*      */       ReferenceBinding referenceBinding;
/*  991 */       if ((referenceBinding = this.defaultPackage.getType0(compoundName[0])) == null) {
/*  992 */         PackageBinding packageBinding = getPackage0(compoundName[0]);
/*  993 */         if ((packageBinding != null) && (packageBinding != TheNotFoundPackage))
/*  994 */           return null;
/*  995 */         referenceBinding = askForType(this.defaultPackage, compoundName[0]);
/*      */       }
/*      */     } else {
/*  998 */       PackageBinding packageBinding = getPackage0(compoundName[0]);
/*  999 */       if (packageBinding == TheNotFoundPackage) {
/* 1000 */         return null;
/*      */       }
/* 1002 */       if (packageBinding != null) {
/* 1003 */         int i = 1; for (int packageLength = compoundName.length - 1; i < packageLength; i++) {
/* 1004 */           if ((packageBinding = packageBinding.getPackage0(compoundName[i])) == null)
/*      */             break;
/* 1006 */           if (packageBinding == TheNotFoundPackage)
/* 1007 */             return null;
/*      */         }
/*      */       }
/*      */       ReferenceBinding referenceBinding;
/* 1011 */       if (packageBinding == null)
/* 1012 */         referenceBinding = askForType(compoundName);
/* 1013 */       else if ((referenceBinding = packageBinding.getType0(compoundName[(compoundName.length - 1)])) == null) {
/* 1014 */         referenceBinding = askForType(packageBinding, compoundName[(compoundName.length - 1)]);
/*      */       }
/*      */     }
/* 1017 */     if ((referenceBinding == null) || (referenceBinding == TheNotFoundType))
/* 1018 */       return null;
/* 1019 */     ReferenceBinding referenceBinding = (ReferenceBinding)BinaryTypeBinding.resolveType(referenceBinding, this, false);
/*      */ 
/* 1022 */     if (referenceBinding.isNestedType())
/* 1023 */       return new ProblemReferenceBinding(compoundName, referenceBinding, 4);
/* 1024 */     return referenceBinding;
/*      */   }
/*      */ 
/*      */   private TypeBinding[] getTypeArgumentsFromSignature(SignatureWrapper wrapper, TypeVariableBinding[] staticVariables, ReferenceBinding enclosingType, ReferenceBinding genericType, char[][][] missingTypeNames) {
/* 1028 */     ArrayList args = new ArrayList(2);
/* 1029 */     int rank = 0;
/*      */     do
/* 1031 */       args.add(getTypeFromVariantTypeSignature(wrapper, staticVariables, enclosingType, genericType, rank++, missingTypeNames));
/* 1032 */     while (wrapper.signature[wrapper.start] != '>');
/* 1033 */     wrapper.start += 1;
/* 1034 */     TypeBinding[] typeArguments = new TypeBinding[args.size()];
/* 1035 */     args.toArray(typeArguments);
/* 1036 */     return typeArguments;
/*      */   }
/*      */ 
/*      */   private ReferenceBinding getTypeFromCompoundName(char[][] compoundName, boolean isParameterized, boolean wasMissingType)
/*      */   {
/* 1046 */     ReferenceBinding binding = getCachedType(compoundName);
/* 1047 */     if (binding == null) {
/* 1048 */       PackageBinding packageBinding = computePackageFrom(compoundName, false);
/* 1049 */       binding = new UnresolvedReferenceBinding(compoundName, packageBinding);
/* 1050 */       if (wasMissingType) {
/* 1051 */         binding.tagBits |= 128L;
/*      */       }
/* 1053 */       packageBinding.addType(binding);
/* 1054 */     } else if (binding == TheNotFoundType)
/*      */     {
/* 1056 */       this.problemReporter.isClassPathCorrect(compoundName, this.unitBeingCompleted, this.missingClassFileLocation);
/*      */ 
/* 1058 */       binding = createMissingType(null, compoundName);
/* 1059 */     } else if (!isParameterized)
/*      */     {
/* 1061 */       binding = (ReferenceBinding)convertUnresolvedBinaryToRawType(binding);
/*      */     }
/* 1063 */     return binding;
/*      */   }
/*      */ 
/*      */   ReferenceBinding getTypeFromConstantPoolName(char[] signature, int start, int end, boolean isParameterized, char[][][] missingTypeNames)
/*      */   {
/* 1073 */     if (end == -1)
/* 1074 */       end = signature.length;
/* 1075 */     char[][] compoundName = CharOperation.splitOn('/', signature, start, end);
/* 1076 */     boolean wasMissingType = false;
/* 1077 */     if (missingTypeNames != null) {
/* 1078 */       int i = 0; for (int max = missingTypeNames.length; i < max; i++) {
/* 1079 */         if (CharOperation.equals(compoundName, missingTypeNames[i])) {
/* 1080 */           wasMissingType = true;
/* 1081 */           break;
/*      */         }
/*      */       }
/*      */     }
/* 1085 */     return getTypeFromCompoundName(compoundName, isParameterized, wasMissingType);
/*      */   }
/*      */ 
/*      */   TypeBinding getTypeFromSignature(char[] signature, int start, int end, boolean isParameterized, TypeBinding enclosingType, char[][][] missingTypeNames)
/*      */   {
/* 1095 */     int dimension = 0;
/* 1096 */     while (signature[start] == '[') {
/* 1097 */       start++;
/* 1098 */       dimension++;
/*      */     }
/* 1100 */     if (end == -1) {
/* 1101 */       end = signature.length - 1;
/*      */     }
/*      */ 
/* 1104 */     TypeBinding binding = null;
/* 1105 */     if (start == end) {
/* 1106 */       switch (signature[start]) {
/*      */       case 'I':
/* 1108 */         binding = TypeBinding.INT;
/* 1109 */         break;
/*      */       case 'Z':
/* 1111 */         binding = TypeBinding.BOOLEAN;
/* 1112 */         break;
/*      */       case 'V':
/* 1114 */         binding = TypeBinding.VOID;
/* 1115 */         break;
/*      */       case 'C':
/* 1117 */         binding = TypeBinding.CHAR;
/* 1118 */         break;
/*      */       case 'D':
/* 1120 */         binding = TypeBinding.DOUBLE;
/* 1121 */         break;
/*      */       case 'B':
/* 1123 */         binding = TypeBinding.BYTE;
/* 1124 */         break;
/*      */       case 'F':
/* 1126 */         binding = TypeBinding.FLOAT;
/* 1127 */         break;
/*      */       case 'J':
/* 1129 */         binding = TypeBinding.LONG;
/* 1130 */         break;
/*      */       case 'S':
/* 1132 */         binding = TypeBinding.SHORT;
/* 1133 */         break;
/*      */       default:
/* 1135 */         this.problemReporter.corruptedSignature(enclosingType, signature, start); break;
/*      */       }
/*      */     }
/*      */     else {
/* 1139 */       binding = getTypeFromConstantPoolName(signature, start + 1, end, isParameterized, missingTypeNames);
/*      */     }
/*      */ 
/* 1142 */     if (dimension == 0)
/* 1143 */       return binding;
/* 1144 */     return createArrayType(binding, dimension);
/*      */   }
/*      */ 
/*      */   public TypeBinding getTypeFromTypeSignature(SignatureWrapper wrapper, TypeVariableBinding[] staticVariables, ReferenceBinding enclosingType, char[][][] missingTypeNames)
/*      */   {
/* 1153 */     int dimension = 0;
/* 1154 */     while (wrapper.signature[wrapper.start] == '[') {
/* 1155 */       wrapper.start += 1;
/* 1156 */       dimension++;
/*      */     }
/* 1158 */     if (wrapper.signature[wrapper.start] == 'T') {
/* 1159 */       int varStart = wrapper.start + 1;
/* 1160 */       int varEnd = wrapper.computeEnd();
/* 1161 */       int i = staticVariables.length;
/*      */       do { if (CharOperation.equals(staticVariables[i].sourceName, wrapper.signature, varStart, varEnd))
/* 1163 */           return dimension == 0 ? staticVariables[i] : createArrayType(staticVariables[i], dimension);
/* 1161 */         i--; } while (i >= 0);
/*      */ 
/* 1164 */       ReferenceBinding initialType = enclosingType;
/*      */       do
/*      */       {
/*      */         TypeVariableBinding[] enclosingTypeVariables;
/*      */         TypeVariableBinding[] enclosingTypeVariables;
/* 1167 */         if ((enclosingType instanceof BinaryTypeBinding))
/* 1168 */           enclosingTypeVariables = ((BinaryTypeBinding)enclosingType).typeVariables;
/*      */         else {
/* 1170 */           enclosingTypeVariables = enclosingType.typeVariables();
/*      */         }
/* 1172 */         int i = enclosingTypeVariables.length;
/*      */         do { if (CharOperation.equals(enclosingTypeVariables[i].sourceName, wrapper.signature, varStart, varEnd))
/* 1174 */             return dimension == 0 ? enclosingTypeVariables[i] : createArrayType(enclosingTypeVariables[i], dimension);
/* 1172 */           i--; } while (i >= 0);
/*      */       }
/*      */ 
/* 1175 */       while ((enclosingType = enclosingType.enclosingType()) != null);
/* 1176 */       this.problemReporter.undefinedTypeVariableSignature(CharOperation.subarray(wrapper.signature, varStart, varEnd), initialType);
/* 1177 */       return null;
/*      */     }
/*      */     boolean isParameterized;
/* 1180 */     TypeBinding type = getTypeFromSignature(wrapper.signature, wrapper.start, wrapper.computeEnd(), isParameterized = wrapper.end == wrapper.bracket ? 1 : 0, enclosingType, missingTypeNames);
/* 1181 */     if (!isParameterized) {
/* 1182 */       return dimension == 0 ? type : createArrayType(type, dimension);
/*      */     }
/*      */ 
/* 1185 */     ReferenceBinding actualType = (ReferenceBinding)type;
/* 1186 */     if (((actualType instanceof UnresolvedReferenceBinding)) && 
/* 1187 */       (CharOperation.indexOf('$', actualType.compoundName[(actualType.compoundName.length - 1)]) > 0))
/* 1188 */       actualType = (ReferenceBinding)BinaryTypeBinding.resolveType(actualType, this, false);
/* 1189 */     ReferenceBinding actualEnclosing = actualType.enclosingType();
/* 1190 */     if (actualEnclosing != null) {
/* 1191 */       actualEnclosing = (ReferenceBinding)convertToRawType(actualEnclosing, false);
/*      */     }
/* 1193 */     TypeBinding[] typeArguments = getTypeArgumentsFromSignature(wrapper, staticVariables, enclosingType, actualType, missingTypeNames);
/* 1194 */     ParameterizedTypeBinding parameterizedType = createParameterizedType(actualType, typeArguments, actualEnclosing);
/*      */ 
/* 1196 */     while (wrapper.signature[wrapper.start] == '.') {
/* 1197 */       wrapper.start += 1;
/* 1198 */       int memberStart = wrapper.start;
/* 1199 */       char[] memberName = wrapper.nextWord();
/* 1200 */       BinaryTypeBinding.resolveType(parameterizedType, this, false);
/* 1201 */       ReferenceBinding memberType = parameterizedType.genericType().getMemberType(memberName);
/*      */ 
/* 1203 */       if (memberType == null)
/* 1204 */         this.problemReporter.corruptedSignature(parameterizedType, wrapper.signature, memberStart);
/* 1205 */       if (wrapper.signature[wrapper.start] == '<') {
/* 1206 */         wrapper.start += 1;
/* 1207 */         typeArguments = getTypeArgumentsFromSignature(wrapper, staticVariables, enclosingType, memberType, missingTypeNames);
/*      */       } else {
/* 1209 */         typeArguments = (TypeBinding[])null;
/*      */       }
/* 1211 */       parameterizedType = createParameterizedType(memberType, typeArguments, parameterizedType);
/*      */     }
/* 1213 */     wrapper.start += 1;
/* 1214 */     return dimension == 0 ? parameterizedType : createArrayType(parameterizedType, dimension);
/*      */   }
/*      */ 
/*      */   TypeBinding getTypeFromVariantTypeSignature(SignatureWrapper wrapper, TypeVariableBinding[] staticVariables, ReferenceBinding enclosingType, ReferenceBinding genericType, int rank, char[][][] missingTypeNames)
/*      */   {
/* 1228 */     switch (wrapper.signature[wrapper.start])
/*      */     {
/*      */     case '-':
/* 1231 */       wrapper.start += 1;
/* 1232 */       TypeBinding bound = getTypeFromTypeSignature(wrapper, staticVariables, enclosingType, missingTypeNames);
/* 1233 */       return createWildcard(genericType, rank, bound, null, 2);
/*      */     case '+':
/* 1236 */       wrapper.start += 1;
/* 1237 */       TypeBinding bound = getTypeFromTypeSignature(wrapper, staticVariables, enclosingType, missingTypeNames);
/* 1238 */       return createWildcard(genericType, rank, bound, null, 1);
/*      */     case '*':
/* 1241 */       wrapper.start += 1;
/* 1242 */       return createWildcard(genericType, rank, null, null, 0);
/*      */     case ',':
/* 1244 */     }return getTypeFromTypeSignature(wrapper, staticVariables, enclosingType, missingTypeNames);
/*      */   }
/*      */ 
/*      */   boolean isMissingType(char[] typeName)
/*      */   {
/* 1249 */     int i = this.missingTypes == null ? 0 : this.missingTypes.size();
/*      */     do { MissingTypeBinding missingType = (MissingTypeBinding)this.missingTypes.get(i);
/* 1251 */       if (CharOperation.equals(missingType.sourceName, typeName))
/* 1252 */         return true;
/* 1249 */       i--; } while (i >= 0);
/*      */ 
/* 1254 */     return false;
/*      */   }
/*      */ 
/*      */   boolean isPackage(char[][] compoundName, char[] name)
/*      */   {
/* 1260 */     if ((compoundName == null) || (compoundName.length == 0))
/* 1261 */       return this.nameEnvironment.isPackage(null, name);
/* 1262 */     return this.nameEnvironment.isPackage(compoundName, name);
/*      */   }
/*      */ 
/*      */   public MethodVerifier methodVerifier() {
/* 1266 */     if (this.verifier == null)
/* 1267 */       this.verifier = newMethodVerifier();
/* 1268 */     return this.verifier;
/*      */   }
/*      */ 
/*      */   public MethodVerifier newMethodVerifier() {
/* 1272 */     return this.globalOptions.sourceLevel < 3211264L ? 
/* 1273 */       new MethodVerifier(this) : 
/* 1274 */       new MethodVerifier15(this);
/*      */   }
/*      */ 
/*      */   public void releaseClassFiles(ClassFile[] classFiles) {
/* 1278 */     int i = 0; for (int fileCount = classFiles.length; i < fileCount; i++)
/* 1279 */       this.classFilePool.release(classFiles[i]);
/*      */   }
/*      */ 
/*      */   public void reset() {
/* 1283 */     this.defaultPackage = new PackageBinding(this);
/* 1284 */     this.defaultImports = null;
/* 1285 */     this.knownPackages = new HashtableOfPackage();
/* 1286 */     this.accessRestrictions = new HashMap(3);
/*      */ 
/* 1288 */     this.verifier = null;
/* 1289 */     int i = this.uniqueArrayBindings.length;
/*      */     do { ArrayBinding[] arrayBindings = this.uniqueArrayBindings[i];
/* 1291 */       if (arrayBindings != null) {
/* 1292 */         int j = arrayBindings.length;
/*      */         do { arrayBindings[j] = null;
/*      */ 
/* 1292 */           j--; } while (j >= 0);
/*      */       }
/* 1289 */       i--; } while (i >= 0);
/*      */ 
/* 1296 */     this.uniqueParameterizedTypeBindings = new SimpleLookupTable(3);
/* 1297 */     this.uniqueRawTypeBindings = new SimpleLookupTable(3);
/* 1298 */     this.uniqueWildcardBindings = new SimpleLookupTable(3);
/* 1299 */     this.uniqueParameterizedGenericMethodBindings = new SimpleLookupTable(3);
/* 1300 */     this.missingTypes = null;
/*      */ 
/* 1302 */     int i = this.units.length;
/*      */     do { this.units[i] = null;
/*      */ 
/* 1302 */       i--; } while (i >= 0);
/*      */ 
/* 1304 */     this.lastUnitIndex = -1;
/* 1305 */     this.lastCompletedUnitIndex = -1;
/* 1306 */     this.unitBeingCompleted = null;
/*      */ 
/* 1308 */     this.classFilePool.reset();
/*      */   }
/*      */ 
/*      */   public void setAccessRestriction(ReferenceBinding type, AccessRestriction accessRestriction)
/*      */   {
/* 1318 */     if (accessRestriction == null) return;
/* 1319 */     type.modifiers |= 262144;
/* 1320 */     this.accessRestrictions.put(type, accessRestriction);
/*      */   }
/*      */ 
/*      */   void updateCaches(UnresolvedReferenceBinding unresolvedType, ReferenceBinding resolvedType)
/*      */   {
/* 1326 */     if (this.uniqueParameterizedTypeBindings.get(unresolvedType) != null) {
/* 1327 */       Object[] keys = this.uniqueParameterizedTypeBindings.keyTable;
/* 1328 */       int i = 0; for (int l = keys.length; i < l; i++) {
/* 1329 */         if (keys[i] == unresolvedType) {
/* 1330 */           keys[i] = resolvedType;
/* 1331 */           break;
/*      */         }
/*      */       }
/*      */     }
/* 1335 */     if (this.uniqueRawTypeBindings.get(unresolvedType) != null) {
/* 1336 */       Object[] keys = this.uniqueRawTypeBindings.keyTable;
/* 1337 */       int i = 0; for (int l = keys.length; i < l; i++) {
/* 1338 */         if (keys[i] == unresolvedType) {
/* 1339 */           keys[i] = resolvedType;
/* 1340 */           break;
/*      */         }
/*      */       }
/*      */     }
/* 1344 */     if (this.uniqueWildcardBindings.get(unresolvedType) != null) {
/* 1345 */       Object[] keys = this.uniqueWildcardBindings.keyTable;
/* 1346 */       int i = 0; for (int l = keys.length; i < l; i++)
/* 1347 */         if (keys[i] == unresolvedType) {
/* 1348 */           keys[i] = resolvedType;
/* 1349 */           break;
/*      */         }
/*      */     }
/*      */   }
/*      */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\ecj-3.5.2.jar
 * Qualified Name:     org.eclipse.jdt.internal.compiler.lookup.LookupEnvironment
 * JD-Core Version:    0.6.0
 */