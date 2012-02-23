/*     */ package org.eclipse.jdt.internal.compiler.lookup;
/*     */ 
/*     */ import org.eclipse.jdt.core.compiler.CharOperation;
/*     */ import org.eclipse.jdt.internal.compiler.CompilationResult;
/*     */ import org.eclipse.jdt.internal.compiler.ast.CompilationUnitDeclaration;
/*     */ import org.eclipse.jdt.internal.compiler.ast.ImportReference;
/*     */ import org.eclipse.jdt.internal.compiler.ast.TypeDeclaration;
/*     */ import org.eclipse.jdt.internal.compiler.env.AccessRestriction;
/*     */ import org.eclipse.jdt.internal.compiler.env.ICompilationUnit;
/*     */ import org.eclipse.jdt.internal.compiler.impl.CompilerOptions;
/*     */ import org.eclipse.jdt.internal.compiler.problem.ProblemReporter;
/*     */ import org.eclipse.jdt.internal.compiler.util.CompoundNameVector;
/*     */ import org.eclipse.jdt.internal.compiler.util.HashtableOfObject;
/*     */ import org.eclipse.jdt.internal.compiler.util.HashtableOfType;
/*     */ import org.eclipse.jdt.internal.compiler.util.ObjectVector;
/*     */ import org.eclipse.jdt.internal.compiler.util.SimpleNameVector;
/*     */ 
/*     */ public class CompilationUnitScope extends Scope
/*     */ {
/*     */   public LookupEnvironment environment;
/*     */   public CompilationUnitDeclaration referenceContext;
/*     */   public char[][] currentPackageName;
/*     */   public PackageBinding fPackage;
/*     */   public ImportBinding[] imports;
/*     */   public HashtableOfObject typeOrPackageCache;
/*     */   public SourceTypeBinding[] topLevelTypes;
/*     */   private CompoundNameVector qualifiedReferences;
/*     */   private SimpleNameVector simpleNameReferences;
/*     */   private SimpleNameVector rootReferences;
/*     */   private ObjectVector referencedTypes;
/*     */   private ObjectVector referencedSuperTypes;
/*     */   HashtableOfType constantPoolNameUsage;
/*  39 */   private int captureID = 1;
/*     */ 
/*     */   public CompilationUnitScope(CompilationUnitDeclaration unit, LookupEnvironment environment) {
/*  42 */     super(4, null);
/*  43 */     this.environment = environment;
/*  44 */     this.referenceContext = unit;
/*  45 */     unit.scope = this;
/*  46 */     this.currentPackageName = (unit.currentPackage == null ? CharOperation.NO_CHAR_CHAR : unit.currentPackage.tokens);
/*     */ 
/*  48 */     if (compilerOptions().produceReferenceInfo) {
/*  49 */       this.qualifiedReferences = new CompoundNameVector();
/*  50 */       this.simpleNameReferences = new SimpleNameVector();
/*  51 */       this.rootReferences = new SimpleNameVector();
/*  52 */       this.referencedTypes = new ObjectVector();
/*  53 */       this.referencedSuperTypes = new ObjectVector();
/*     */     } else {
/*  55 */       this.qualifiedReferences = null;
/*  56 */       this.simpleNameReferences = null;
/*  57 */       this.rootReferences = null;
/*  58 */       this.referencedTypes = null;
/*  59 */       this.referencedSuperTypes = null;
/*     */     }
/*     */   }
/*     */ 
/*     */   void buildFieldsAndMethods() {
/*  63 */     int i = 0; for (int length = this.topLevelTypes.length; i < length; i++)
/*  64 */       this.topLevelTypes[i].scope.buildFieldsAndMethods(); 
/*     */   }
/*     */ 
/*     */   void buildTypeBindings(AccessRestriction accessRestriction) {
/*  67 */     this.topLevelTypes = new SourceTypeBinding[0];
/*  68 */     boolean firstIsSynthetic = false;
/*  69 */     if (this.referenceContext.compilationResult.compilationUnit != null) {
/*  70 */       char[][] expectedPackageName = this.referenceContext.compilationResult.compilationUnit.getPackageName();
/*  71 */       if ((expectedPackageName != null) && 
/*  72 */         (!CharOperation.equals(this.currentPackageName, expectedPackageName)))
/*     */       {
/*  75 */         if ((this.referenceContext.currentPackage != null) || 
/*  76 */           (this.referenceContext.types != null) || 
/*  77 */           (this.referenceContext.imports != null)) {
/*  78 */           problemReporter().packageIsNotExpectedPackage(this.referenceContext);
/*     */         }
/*  80 */         this.currentPackageName = (expectedPackageName.length == 0 ? CharOperation.NO_CHAR_CHAR : expectedPackageName);
/*     */       }
/*     */     }
/*  83 */     if (this.currentPackageName == CharOperation.NO_CHAR_CHAR) {
/*  84 */       if ((this.fPackage = this.environment.defaultPackage) == null) {
/*  85 */         problemReporter().mustSpecifyPackage(this.referenceContext);
/*  86 */         return;
/*     */       }
/*     */     } else {
/*  89 */       if ((this.fPackage = this.environment.createPackage(this.currentPackageName)) == null) {
/*  90 */         if (this.referenceContext.currentPackage != null)
/*  91 */           problemReporter().packageCollidesWithType(this.referenceContext);
/*  92 */         return;
/*  93 */       }if (this.referenceContext.isPackageInfo())
/*     */       {
/*  95 */         if ((this.referenceContext.types == null) || (this.referenceContext.types.length == 0)) {
/*  96 */           this.referenceContext.types = new TypeDeclaration[1];
/*  97 */           this.referenceContext.createPackageInfoType();
/*  98 */           firstIsSynthetic = true;
/*     */         }
/*     */ 
/* 101 */         if (this.referenceContext.currentPackage != null)
/* 102 */           this.referenceContext.types[0].annotations = this.referenceContext.currentPackage.annotations;
/*     */       }
/* 104 */       recordQualifiedReference(this.currentPackageName);
/*     */     }
/*     */ 
/* 108 */     TypeDeclaration[] types = this.referenceContext.types;
/* 109 */     int typeLength = types == null ? 0 : types.length;
/* 110 */     this.topLevelTypes = new SourceTypeBinding[typeLength];
/* 111 */     int count = 0;
/* 112 */     for (int i = 0; i < typeLength; i++) {
/* 113 */       TypeDeclaration typeDecl = types[i];
/* 114 */       if ((this.environment.isProcessingAnnotations) && (this.environment.isMissingType(typeDecl.name)))
/* 115 */         throw new SourceTypeCollisionException();
/* 116 */       ReferenceBinding typeBinding = this.fPackage.getType0(typeDecl.name);
/* 117 */       recordSimpleReference(typeDecl.name);
/* 118 */       if ((typeBinding != null) && (typeBinding.isValidBinding()) && (!(typeBinding instanceof UnresolvedReferenceBinding)))
/*     */       {
/* 120 */         if (this.environment.isProcessingAnnotations) {
/* 121 */           throw new SourceTypeCollisionException();
/*     */         }
/*     */ 
/* 125 */         problemReporter().duplicateTypes(this.referenceContext, typeDecl);
/*     */       }
/*     */       else {
/* 128 */         if ((this.fPackage != this.environment.defaultPackage) && (this.fPackage.getPackage(typeDecl.name) != null))
/*     */         {
/* 131 */           problemReporter().typeCollidesWithPackage(this.referenceContext, typeDecl);
/*     */         }
/*     */ 
/* 134 */         if ((typeDecl.modifiers & 0x1) != 0)
/*     */         {
/*     */           char[] mainTypeName;
/* 136 */           if (((mainTypeName = this.referenceContext.getMainTypeName()) != null) && 
/* 137 */             (!CharOperation.equals(mainTypeName, typeDecl.name))) {
/* 138 */             problemReporter().publicClassMustMatchFileName(this.referenceContext, typeDecl);
/*     */           }
/*     */ 
/*     */         }
/*     */ 
/* 143 */         ClassScope child = new ClassScope(this, typeDecl);
/* 144 */         SourceTypeBinding type = child.buildType(null, this.fPackage, accessRestriction);
/* 145 */         if ((firstIsSynthetic) && (i == 0))
/* 146 */           type.modifiers |= 4096;
/* 147 */         if (type != null) {
/* 148 */           this.topLevelTypes[(count++)] = type;
/*     */         }
/*     */       }
/*     */     }
/* 152 */     if (count != this.topLevelTypes.length)
/* 153 */       System.arraycopy(this.topLevelTypes, 0, this.topLevelTypes = new SourceTypeBinding[count], 0, count); 
/*     */   }
/*     */ 
/*     */   void checkAndSetImports() {
/* 156 */     if (this.referenceContext.imports == null) {
/* 157 */       this.imports = getDefaultImports();
/* 158 */       return;
/*     */     }
/*     */ 
/* 162 */     int numberOfStatements = this.referenceContext.imports.length;
/* 163 */     int numberOfImports = numberOfStatements + 1;
/* 164 */     for (int i = 0; i < numberOfStatements; i++) {
/* 165 */       ImportReference importReference = this.referenceContext.imports[i];
/* 166 */       if (((importReference.bits & 0x20000) != 0) && (CharOperation.equals(TypeConstants.JAVA_LANG, importReference.tokens)) && (!importReference.isStatic())) {
/* 167 */         numberOfImports--;
/* 168 */         break;
/*     */       }
/*     */     }
/* 171 */     ImportBinding[] resolvedImports = new ImportBinding[numberOfImports];
/* 172 */     resolvedImports[0] = getDefaultImports()[0];
/* 173 */     int index = 1;
/*     */ 
/* 175 */     for (int i = 0; i < numberOfStatements; i++) {
/* 176 */       ImportReference importReference = this.referenceContext.imports[i];
/* 177 */       char[][] compoundName = importReference.tokens;
/*     */ 
/* 180 */       int j = 0;
/*     */       while (true) { ImportBinding resolved = resolvedImports[j];
/* 182 */         if (resolved.onDemand == ((importReference.bits & 0x20000) != 0)) { if ((resolved.isStatic() == importReference.isStatic()) && 
/* 183 */             (CharOperation.equals(compoundName, resolvedImports[j].compoundName)))
/*     */             break;
/*     */         }
/*     */         else
/*     */         {
/* 180 */           j++; if (j < index)
/*     */           {
/*     */             continue;
/*     */           }
/*     */ 
/* 187 */           if ((importReference.bits & 0x20000) != 0) {
/* 188 */             if (CharOperation.equals(compoundName, this.currentPackageName)) {
/*     */               break;
/*     */             }
/* 191 */             Binding importBinding = findImport(compoundName, compoundName.length);
/* 192 */             if ((!importBinding.isValidBinding()) || ((importReference.isStatic()) && ((importBinding instanceof PackageBinding))))
/*     */               break;
/* 194 */             resolvedImports[(index++)] = new ImportBinding(compoundName, true, importBinding, importReference);
/*     */           }
/*     */           else {
/* 197 */             resolvedImports[(index++)] = new ImportBinding(compoundName, false, null, importReference);
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/* 202 */     if (resolvedImports.length > index)
/* 203 */       System.arraycopy(resolvedImports, 0, resolvedImports = new ImportBinding[index], 0, index);
/* 204 */     this.imports = resolvedImports;
/*     */   }
/*     */ 
/*     */   void checkParameterizedTypes()
/*     */   {
/* 211 */     if (compilerOptions().sourceLevel < 3211264L) return;
/*     */ 
/* 213 */     int i = 0; for (int length = this.topLevelTypes.length; i < length; i++) {
/* 214 */       ClassScope scope = this.topLevelTypes[i].scope;
/* 215 */       scope.checkParameterizedTypeBounds();
/* 216 */       scope.checkParameterizedSuperTypeCollisions();
/*     */     }
/*     */   }
/*     */ 
/*     */   public char[] computeConstantPoolName(LocalTypeBinding localType)
/*     */   {
/* 225 */     if (localType.constantPoolName() != null) {
/* 226 */       return localType.constantPoolName();
/*     */     }
/*     */ 
/* 230 */     if (this.constantPoolNameUsage == null) {
/* 231 */       this.constantPoolNameUsage = new HashtableOfType();
/*     */     }
/* 233 */     ReferenceBinding outerMostEnclosingType = localType.scope.outerMostClassScope().enclosingSourceType();
/*     */ 
/* 236 */     int index = 0;
/*     */ 
/* 238 */     boolean isCompliant15 = compilerOptions().complianceLevel >= 3211264L;
/*     */     char[] candidateName;
/*     */     while (true)
/*     */     {
/*     */       char[] candidateName;
/* 240 */       if (localType.isMemberType())
/*     */       {
/*     */         char[] candidateName;
/* 241 */         if (index == 0) {
/* 242 */           candidateName = CharOperation.concat(
/* 243 */             localType.enclosingType().constantPoolName(), 
/* 244 */             localType.sourceName, 
/* 245 */             '$');
/*     */         }
/*     */         else
/*     */         {
/* 249 */           candidateName = CharOperation.concat(
/* 250 */             localType.enclosingType().constantPoolName(), 
/* 251 */             '$', 
/* 252 */             String.valueOf(index).toCharArray(), 
/* 253 */             '$', 
/* 254 */             localType.sourceName);
/*     */         }
/*     */       }
/*     */       else
/*     */       {
/*     */         char[] candidateName;
/* 256 */         if (localType.isAnonymousType())
/*     */         {
/*     */           char[] candidateName;
/* 257 */           if (isCompliant15)
/*     */           {
/* 259 */             candidateName = CharOperation.concat(
/* 260 */               localType.enclosingType.constantPoolName(), 
/* 261 */               String.valueOf(index + 1).toCharArray(), 
/* 262 */               '$');
/*     */           }
/* 264 */           else candidateName = CharOperation.concat(
/* 265 */               outerMostEnclosingType.constantPoolName(), 
/* 266 */               String.valueOf(index + 1).toCharArray(), 
/* 267 */               '$');
/*     */         }
/*     */         else
/*     */         {
/*     */           char[] candidateName;
/* 271 */           if (isCompliant15)
/* 272 */             candidateName = CharOperation.concat(
/* 273 */               CharOperation.concat(
/* 274 */               localType.enclosingType().constantPoolName(), 
/* 275 */               String.valueOf(index + 1).toCharArray(), 
/* 276 */               '$'), 
/* 277 */               localType.sourceName);
/*     */           else
/* 279 */             candidateName = CharOperation.concat(
/* 280 */               outerMostEnclosingType.constantPoolName(), 
/* 281 */               '$', 
/* 282 */               String.valueOf(index + 1).toCharArray(), 
/* 283 */               '$', 
/* 284 */               localType.sourceName);
/*     */         }
/*     */       }
/* 287 */       if (this.constantPoolNameUsage.get(candidateName) == null) break;
/* 288 */       index++;
/*     */     }
/* 290 */     this.constantPoolNameUsage.put(candidateName, localType);
/*     */ 
/* 294 */     return candidateName;
/*     */   }
/*     */ 
/*     */   void connectTypeHierarchy() {
/* 298 */     int i = 0; for (int length = this.topLevelTypes.length; i < length; i++)
/* 299 */       this.topLevelTypes[i].scope.connectTypeHierarchy(); 
/*     */   }
/*     */ 
/*     */   void faultInImports() {
/* 302 */     if (this.typeOrPackageCache != null)
/* 303 */       return;
/* 304 */     if (this.referenceContext.imports == null) {
/* 305 */       this.typeOrPackageCache = new HashtableOfObject(1);
/* 306 */       return;
/*     */     }
/*     */ 
/* 310 */     int numberOfStatements = this.referenceContext.imports.length;
/* 311 */     HashtableOfType typesBySimpleNames = null;
/* 312 */     for (int i = 0; i < numberOfStatements; i++) {
/* 313 */       if ((this.referenceContext.imports[i].bits & 0x20000) == 0) {
/* 314 */         typesBySimpleNames = new HashtableOfType(this.topLevelTypes.length + numberOfStatements);
/* 315 */         int j = 0; for (int length = this.topLevelTypes.length; j < length; j++)
/* 316 */           typesBySimpleNames.put(this.topLevelTypes[j].sourceName, this.topLevelTypes[j]);
/* 317 */         break;
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 322 */     int numberOfImports = numberOfStatements + 1;
/* 323 */     for (int i = 0; i < numberOfStatements; i++) {
/* 324 */       ImportReference importReference = this.referenceContext.imports[i];
/* 325 */       if (((importReference.bits & 0x20000) != 0) && (CharOperation.equals(TypeConstants.JAVA_LANG, importReference.tokens)) && (!importReference.isStatic())) {
/* 326 */         numberOfImports--;
/* 327 */         break;
/*     */       }
/*     */     }
/* 330 */     ImportBinding[] resolvedImports = new ImportBinding[numberOfImports];
/* 331 */     resolvedImports[0] = getDefaultImports()[0];
/* 332 */     int index = 1;
/*     */ 
/* 337 */     for (int i = 0; i < numberOfStatements; i++) {
/* 338 */       ImportReference importReference = this.referenceContext.imports[i];
/* 339 */       char[][] compoundName = importReference.tokens;
/*     */ 
/* 342 */       int j = 0;
/*     */       while (true) { ImportBinding resolved = resolvedImports[j];
/* 344 */         if (resolved.onDemand == ((importReference.bits & 0x20000) != 0)) if ((resolved.isStatic() == importReference.isStatic()) && 
/* 345 */             (CharOperation.equals(compoundName, resolved.compoundName))) {
/* 346 */             problemReporter().unusedImport(importReference);
/* 347 */             break;
/*     */           }
/* 342 */         j++; if (j < index)
/*     */         {
/*     */           continue;
/*     */         }
/*     */ 
/* 351 */         if ((importReference.bits & 0x20000) != 0) {
/* 352 */           if (CharOperation.equals(compoundName, this.currentPackageName)) {
/* 353 */             problemReporter().unusedImport(importReference);
/*     */           }
/*     */           else
/*     */           {
/* 357 */             Binding importBinding = findImport(compoundName, compoundName.length);
/* 358 */             if (!importBinding.isValidBinding()) {
/* 359 */               problemReporter().importProblem(importReference, importBinding);
/*     */             }
/* 362 */             else if ((importReference.isStatic()) && ((importBinding instanceof PackageBinding))) {
/* 363 */               problemReporter().cannotImportPackage(importReference);
/*     */             }
/*     */             else
/* 366 */               resolvedImports[(index++)] = new ImportBinding(compoundName, true, importBinding, importReference); 
/*     */           }
/*     */         } else {
/* 368 */           Binding importBinding = findSingleImport(compoundName, 13, importReference.isStatic());
/* 369 */           if ((!importBinding.isValidBinding()) && 
/* 370 */             (importBinding.problemId() != 3))
/*     */           {
/* 373 */             problemReporter().importProblem(importReference, importBinding);
/*     */           }
/* 377 */           else if ((importBinding instanceof PackageBinding)) {
/* 378 */             problemReporter().cannotImportPackage(importReference);
/*     */           }
/*     */           else {
/* 381 */             ReferenceBinding conflictingType = null;
/* 382 */             if ((importBinding instanceof MethodBinding)) {
/* 383 */               conflictingType = (ReferenceBinding)getType(compoundName, compoundName.length);
/* 384 */               if (!conflictingType.isValidBinding()) {
/* 385 */                 conflictingType = null;
/*     */               }
/*     */             }
/* 388 */             if (((importBinding instanceof ReferenceBinding)) || (conflictingType != null)) {
/* 389 */               ReferenceBinding referenceBinding = conflictingType == null ? (ReferenceBinding)importBinding : conflictingType;
/* 390 */               ReferenceBinding typeToCheck = referenceBinding.problemId() == 3 ? 
/* 391 */                 ((ProblemReferenceBinding)referenceBinding).closestMatch : 
/* 392 */                 referenceBinding;
/* 393 */               if (importReference.isTypeUseDeprecated(typeToCheck, this)) {
/* 394 */                 problemReporter().deprecatedType(typeToCheck, importReference);
/*     */               }
/* 396 */               ReferenceBinding existingType = typesBySimpleNames.get(compoundName[(compoundName.length - 1)]);
/* 397 */               if (existingType != null)
/*     */               {
/* 399 */                 if (existingType == referenceBinding) {
/*     */                   break;
/*     */                 }
/* 402 */                 int j = 0; for (int length = this.topLevelTypes.length; j < length; j++) {
/* 403 */                   if (CharOperation.equals(this.topLevelTypes[j].sourceName, existingType.sourceName)) {
/* 404 */                     problemReporter().conflictingImport(importReference);
/* 405 */                     break;
/*     */                   }
/*     */                 }
/* 408 */                 problemReporter().duplicateImport(importReference);
/* 409 */                 break;
/*     */               }
/* 411 */               typesBySimpleNames.put(compoundName[(compoundName.length - 1)], referenceBinding);
/* 412 */             } else if ((importBinding instanceof FieldBinding)) {
/* 413 */               for (int j = 0; j < index; j++) {
/* 414 */                 ImportBinding resolved = resolvedImports[j];
/*     */ 
/* 416 */                 if ((!resolved.isStatic()) || (!(resolved.resolvedImport instanceof FieldBinding)) || (importBinding == resolved.resolvedImport) || 
/* 417 */                   (!CharOperation.equals(compoundName[(compoundName.length - 1)], resolved.compoundName[(resolved.compoundName.length - 1)]))) continue;
/* 418 */                 problemReporter().duplicateImport(importReference);
/* 419 */                 break;
/*     */               }
/*     */ 
/*     */             }
/*     */ 
/* 424 */             resolvedImports[(index++)] = (conflictingType == null ? 
/* 425 */               new ImportBinding(compoundName, false, importBinding, importReference) : 
/* 426 */               new ImportConflictBinding(compoundName, importBinding, conflictingType, importReference));
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/* 431 */     if (resolvedImports.length > index)
/* 432 */       System.arraycopy(resolvedImports, 0, resolvedImports = new ImportBinding[index], 0, index);
/* 433 */     this.imports = resolvedImports;
/*     */ 
/* 435 */     int length = this.imports.length;
/* 436 */     this.typeOrPackageCache = new HashtableOfObject(length);
/* 437 */     for (int i = 0; i < length; i++) {
/* 438 */       ImportBinding binding = this.imports[i];
/* 439 */       if (((!binding.onDemand) && ((binding.resolvedImport instanceof ReferenceBinding))) || ((binding instanceof ImportConflictBinding)))
/* 440 */         this.typeOrPackageCache.put(binding.compoundName[(binding.compoundName.length - 1)], binding); 
/*     */     }
/*     */   }
/*     */ 
/*     */   public void faultInTypes() {
/* 444 */     faultInImports();
/*     */ 
/* 446 */     int i = 0; for (int length = this.topLevelTypes.length; i < length; i++)
/* 447 */       this.topLevelTypes[i].faultInTypesForFieldsAndMethods();
/*     */   }
/*     */ 
/*     */   public Binding findImport(char[][] compoundName, boolean findStaticImports, boolean onDemand) {
/* 451 */     if (onDemand) {
/* 452 */       return findImport(compoundName, compoundName.length);
/*     */     }
/* 454 */     return findSingleImport(compoundName, 13, findStaticImports);
/*     */   }
/*     */ 
/*     */   private Binding findImport(char[][] compoundName, int length) {
/* 458 */     recordQualifiedReference(compoundName);
/*     */ 
/* 460 */     Binding binding = this.environment.getTopLevelPackage(compoundName[0]);
/* 461 */     int i = 1;
/* 462 */     if (binding != null) {
/* 463 */       PackageBinding packageBinding = (PackageBinding)binding;
/*     */       while (true) {
/* 465 */         binding = packageBinding.getTypeOrPackage(compoundName[(i++)]);
/* 466 */         if ((binding == null) || (!binding.isValidBinding())) {
/* 467 */           binding = null;
/*     */         }
/*     */         else {
/* 470 */           if (!(binding instanceof PackageBinding)) {
/*     */             break;
/*     */           }
/* 473 */           packageBinding = (PackageBinding)binding;
/*     */ 
/* 464 */           if (i >= length)
/*     */           {
/* 475 */             return packageBinding;
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*     */     ReferenceBinding type;
/* 479 */     if (binding == null) {
/* 480 */       if ((this.environment.defaultPackage == null) || (compilerOptions().complianceLevel >= 3145728L))
/* 481 */         return new ProblemReferenceBinding(CharOperation.subarray(compoundName, 0, i), null, 1);
/* 482 */       ReferenceBinding type = findType(compoundName[0], this.environment.defaultPackage, this.environment.defaultPackage);
/* 483 */       if ((type == null) || (!type.isValidBinding()))
/* 484 */         return new ProblemReferenceBinding(CharOperation.subarray(compoundName, 0, i), null, 1);
/* 485 */       i = 1;
/*     */     } else {
/* 487 */       type = (ReferenceBinding)binding;
/*     */     }
/*     */ 
/* 490 */     while (i < length) {
/* 491 */       type = (ReferenceBinding)this.environment.convertToRawType(type, false);
/* 492 */       if (!type.canBeSeenBy(this.fPackage)) {
/* 493 */         return new ProblemReferenceBinding(CharOperation.subarray(compoundName, 0, i), type, 2);
/*     */       }
/* 495 */       char[] name = compoundName[(i++)];
/*     */ 
/* 497 */       type = type.getMemberType(name);
/* 498 */       if (type == null)
/* 499 */         return new ProblemReferenceBinding(CharOperation.subarray(compoundName, 0, i), null, 1);
/*     */     }
/* 501 */     if (!type.canBeSeenBy(this.fPackage))
/* 502 */       return new ProblemReferenceBinding(compoundName, type, 2);
/* 503 */     return type;
/*     */   }
/*     */   private Binding findSingleImport(char[][] compoundName, int mask, boolean findStaticImports) {
/* 506 */     if (compoundName.length == 1)
/*     */     {
/* 509 */       if ((this.environment.defaultPackage == null) || (compilerOptions().complianceLevel >= 3145728L))
/* 510 */         return new ProblemReferenceBinding(compoundName, null, 1);
/* 511 */       ReferenceBinding typeBinding = findType(compoundName[0], this.environment.defaultPackage, this.fPackage);
/* 512 */       if (typeBinding == null)
/* 513 */         return new ProblemReferenceBinding(compoundName, null, 1);
/* 514 */       return typeBinding;
/*     */     }
/*     */ 
/* 517 */     if (findStaticImports)
/* 518 */       return findSingleStaticImport(compoundName, mask);
/* 519 */     return findImport(compoundName, compoundName.length);
/*     */   }
/*     */   private Binding findSingleStaticImport(char[][] compoundName, int mask) {
/* 522 */     Binding binding = findImport(compoundName, compoundName.length - 1);
/* 523 */     if (!binding.isValidBinding()) return binding;
/*     */ 
/* 525 */     char[] name = compoundName[(compoundName.length - 1)];
/* 526 */     if ((binding instanceof PackageBinding)) {
/* 527 */       Binding temp = ((PackageBinding)binding).getTypeOrPackage(name);
/* 528 */       if ((temp != null) && ((temp instanceof ReferenceBinding)))
/* 529 */         return new ProblemReferenceBinding(compoundName, (ReferenceBinding)temp, 14);
/* 530 */       return binding;
/*     */     }
/*     */ 
/* 534 */     ReferenceBinding type = (ReferenceBinding)binding;
/* 535 */     FieldBinding field = (mask & 0x1) != 0 ? findField(type, name, null, true) : null;
/* 536 */     if (field != null) {
/* 537 */       if ((field.problemId() == 3) && (((ProblemFieldBinding)field).closestMatch.isStatic()))
/* 538 */         return field;
/* 539 */       if ((field.isValidBinding()) && (field.isStatic()) && (field.canBeSeenBy(type, null, this))) {
/* 540 */         return field;
/*     */       }
/*     */     }
/*     */ 
/* 544 */     MethodBinding method = (mask & 0x8) != 0 ? findStaticMethod(type, name) : null;
/* 545 */     if (method != null) return method;
/*     */ 
/* 547 */     type = findMemberType(name, type);
/* 548 */     if ((type == null) || (!type.isStatic())) {
/* 549 */       if ((field != null) && (!field.isValidBinding()) && (field.problemId() != 1))
/* 550 */         return field;
/* 551 */       return new ProblemReferenceBinding(compoundName, type, 1);
/*     */     }
/* 553 */     if ((type.isValidBinding()) && (!type.canBeSeenBy(this.fPackage)))
/* 554 */       return new ProblemReferenceBinding(compoundName, type, 2);
/* 555 */     if (type.problemId() == 2)
/* 556 */       return new ProblemReferenceBinding(compoundName, ((ProblemReferenceBinding)type).closestMatch, 2);
/* 557 */     return type;
/*     */   }
/*     */ 
/*     */   private MethodBinding findStaticMethod(ReferenceBinding currentType, char[] selector) {
/* 561 */     if (!currentType.canBeSeenBy(this))
/* 562 */       return null;
/*     */     do
/*     */     {
/* 565 */       currentType.initializeForStaticImports();
/* 566 */       MethodBinding[] methods = currentType.getMethods(selector);
/* 567 */       if (methods != Binding.NO_METHODS) {
/* 568 */         int i = methods.length;
/*     */         do { MethodBinding method = methods[i];
/* 570 */           if ((method.isStatic()) && (method.canBeSeenBy(this.fPackage)))
/* 571 */             return method;
/* 568 */           i--; } while (i >= 0);
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 574 */     while ((currentType = currentType.superclass()) != null);
/* 575 */     return null;
/*     */   }
/*     */ 
/*     */   ImportBinding[] getDefaultImports() {
/* 579 */     if (this.environment.defaultImports != null) return this.environment.defaultImports;
/*     */ 
/* 581 */     Binding importBinding = this.environment.getTopLevelPackage(TypeConstants.JAVA);
/* 582 */     if (importBinding != null) {
/* 583 */       importBinding = ((PackageBinding)importBinding).getTypeOrPackage(TypeConstants.JAVA_LANG[1]);
/*     */     }
/* 585 */     if ((importBinding == null) || (!importBinding.isValidBinding()))
/*     */     {
/* 587 */       problemReporter().isClassPathCorrect(
/* 588 */         TypeConstants.JAVA_LANG_OBJECT, 
/* 589 */         this.referenceContext, 
/* 590 */         this.environment.missingClassFileLocation);
/* 591 */       BinaryTypeBinding missingObject = this.environment.createMissingType(null, TypeConstants.JAVA_LANG_OBJECT);
/* 592 */       importBinding = missingObject.fPackage;
/*     */     }
/*     */ 
/* 595 */     return this.environment.defaultImports = new ImportBinding[] { new ImportBinding(TypeConstants.JAVA_LANG, true, importBinding, null) };
/*     */   }
/*     */ 
/*     */   public final Binding getImport(char[][] compoundName, boolean onDemand, boolean isStaticImport) {
/* 599 */     if (onDemand)
/* 600 */       return findImport(compoundName, compoundName.length);
/* 601 */     return findSingleImport(compoundName, 13, isStaticImport);
/*     */   }
/*     */ 
/*     */   public int nextCaptureID() {
/* 605 */     return this.captureID++;
/*     */   }
/*     */ 
/*     */   public ProblemReporter problemReporter()
/*     */   {
/* 615 */     ProblemReporter problemReporter = this.referenceContext.problemReporter;
/* 616 */     problemReporter.referenceContext = this.referenceContext;
/* 617 */     return problemReporter;
/*     */   }
/*     */ 
/*     */   void recordQualifiedReference(char[][] qualifiedName)
/*     */   {
/* 657 */     if (this.qualifiedReferences == null) return;
/*     */ 
/* 659 */     int length = qualifiedName.length;
/* 660 */     if (length > 1) {
/* 661 */       recordRootReference(qualifiedName[0]);
/* 662 */       while (!this.qualifiedReferences.contains(qualifiedName)) {
/* 663 */         this.qualifiedReferences.add(qualifiedName);
/* 664 */         if (length == 2) {
/* 665 */           recordSimpleReference(qualifiedName[0]);
/* 666 */           recordSimpleReference(qualifiedName[1]);
/* 667 */           return;
/*     */         }
/* 669 */         length--;
/* 670 */         recordSimpleReference(qualifiedName[length]);
/* 671 */         System.arraycopy(qualifiedName, 0, qualifiedName = new char[length][], 0, length);
/*     */       }
/* 673 */     } else if (length == 1) {
/* 674 */       recordRootReference(qualifiedName[0]);
/* 675 */       recordSimpleReference(qualifiedName[0]);
/*     */     }
/*     */   }
/*     */ 
/*     */   void recordReference(char[][] qualifiedEnclosingName, char[] simpleName) {
/* 679 */     recordQualifiedReference(qualifiedEnclosingName);
/* 680 */     if (qualifiedEnclosingName.length == 0)
/* 681 */       recordRootReference(simpleName);
/* 682 */     recordSimpleReference(simpleName);
/*     */   }
/*     */   void recordReference(ReferenceBinding type, char[] simpleName) {
/* 685 */     ReferenceBinding actualType = typeToRecord(type);
/* 686 */     if (actualType != null)
/* 687 */       recordReference(actualType.compoundName, simpleName); 
/*     */   }
/*     */ 
/*     */   void recordRootReference(char[] simpleName) {
/* 690 */     if (this.rootReferences == null) return;
/*     */ 
/* 692 */     if (!this.rootReferences.contains(simpleName))
/* 693 */       this.rootReferences.add(simpleName); 
/*     */   }
/*     */ 
/*     */   void recordSimpleReference(char[] simpleName) {
/* 696 */     if (this.simpleNameReferences == null) return;
/*     */ 
/* 698 */     if (!this.simpleNameReferences.contains(simpleName))
/* 699 */       this.simpleNameReferences.add(simpleName); 
/*     */   }
/*     */ 
/*     */   void recordSuperTypeReference(TypeBinding type) {
/* 702 */     if (this.referencedSuperTypes == null) return;
/*     */ 
/* 704 */     ReferenceBinding actualType = typeToRecord(type);
/* 705 */     if ((actualType != null) && (!this.referencedSuperTypes.containsIdentical(actualType)))
/* 706 */       this.referencedSuperTypes.add(actualType); 
/*     */   }
/*     */ 
/*     */   public void recordTypeConversion(TypeBinding superType, TypeBinding subType) {
/* 709 */     recordSuperTypeReference(subType);
/*     */   }
/*     */   void recordTypeReference(TypeBinding type) {
/* 712 */     if (this.referencedTypes == null) return;
/*     */ 
/* 714 */     ReferenceBinding actualType = typeToRecord(type);
/* 715 */     if ((actualType != null) && (!this.referencedTypes.containsIdentical(actualType)))
/* 716 */       this.referencedTypes.add(actualType); 
/*     */   }
/*     */ 
/*     */   void recordTypeReferences(TypeBinding[] types) {
/* 719 */     if (this.referencedTypes == null) return;
/* 720 */     if ((types == null) || (types.length == 0)) return;
/*     */ 
/* 722 */     int i = 0; for (int max = types.length; i < max; i++)
/*     */     {
/* 725 */       ReferenceBinding actualType = typeToRecord(types[i]);
/* 726 */       if ((actualType != null) && (!this.referencedTypes.containsIdentical(actualType)))
/* 727 */         this.referencedTypes.add(actualType); 
/*     */     }
/*     */   }
/*     */ 
/*     */   Binding resolveSingleImport(ImportBinding importBinding, int mask) {
/* 731 */     if (importBinding.resolvedImport == null) {
/* 732 */       importBinding.resolvedImport = findSingleImport(importBinding.compoundName, mask, importBinding.isStatic());
/* 733 */       if ((!importBinding.resolvedImport.isValidBinding()) || ((importBinding.resolvedImport instanceof PackageBinding))) {
/* 734 */         if (importBinding.resolvedImport.problemId() == 3)
/* 735 */           return importBinding.resolvedImport;
/* 736 */         if (this.imports != null) {
/* 737 */           ImportBinding[] newImports = new ImportBinding[this.imports.length - 1];
/* 738 */           int i = 0; int n = 0; for (int max = this.imports.length; i < max; i++)
/* 739 */             if (this.imports[i] != importBinding)
/* 740 */               newImports[(n++)] = this.imports[i];
/* 741 */           this.imports = newImports;
/*     */         }
/* 743 */         return null;
/*     */       }
/*     */     }
/* 746 */     return importBinding.resolvedImport;
/*     */   }
/*     */ 
/*     */   public void storeDependencyInfo()
/*     */   {
/* 751 */     for (int i = 0; i < this.referencedSuperTypes.size; i++) {
/* 752 */       ReferenceBinding type = (ReferenceBinding)this.referencedSuperTypes.elementAt(i);
/* 753 */       if (!this.referencedTypes.containsIdentical(type)) {
/* 754 */         this.referencedTypes.add(type);
/*     */       }
/* 756 */       if (!type.isLocalType()) {
/* 757 */         ReferenceBinding enclosing = type.enclosingType();
/* 758 */         if (enclosing != null)
/* 759 */           recordSuperTypeReference(enclosing);
/*     */       }
/* 761 */       ReferenceBinding superclass = type.superclass();
/* 762 */       if (superclass != null)
/* 763 */         recordSuperTypeReference(superclass);
/* 764 */       ReferenceBinding[] interfaces = type.superInterfaces();
/* 765 */       if (interfaces != null) {
/* 766 */         int j = 0; for (int length = interfaces.length; j < length; j++)
/* 767 */           recordSuperTypeReference(interfaces[j]);
/*     */       }
/*     */     }
/* 770 */     int i = 0; for (int l = this.referencedTypes.size; i < l; i++) {
/* 771 */       ReferenceBinding type = (ReferenceBinding)this.referencedTypes.elementAt(i);
/* 772 */       if (!type.isLocalType()) {
/* 773 */         recordQualifiedReference(type.isMemberType() ? 
/* 774 */           CharOperation.splitOn('.', type.readableName()) : 
/* 775 */           type.compoundName);
/*     */       }
/*     */     }
/* 778 */     int size = this.qualifiedReferences.size;
/* 779 */     char[][][] qualifiedRefs = new char[size][][];
/* 780 */     for (int i = 0; i < size; i++)
/* 781 */       qualifiedRefs[i] = this.qualifiedReferences.elementAt(i);
/* 782 */     this.referenceContext.compilationResult.qualifiedReferences = qualifiedRefs;
/*     */ 
/* 784 */     size = this.simpleNameReferences.size;
/* 785 */     char[][] simpleRefs = new char[size][];
/* 786 */     for (int i = 0; i < size; i++)
/* 787 */       simpleRefs[i] = this.simpleNameReferences.elementAt(i);
/* 788 */     this.referenceContext.compilationResult.simpleNameReferences = simpleRefs;
/*     */ 
/* 790 */     size = this.rootReferences.size;
/* 791 */     char[][] rootRefs = new char[size][];
/* 792 */     for (int i = 0; i < size; i++)
/* 793 */       rootRefs[i] = this.rootReferences.elementAt(i);
/* 794 */     this.referenceContext.compilationResult.rootReferences = rootRefs;
/*     */   }
/*     */   public String toString() {
/* 797 */     return "--- CompilationUnit Scope : " + new String(this.referenceContext.getFileName());
/*     */   }
/*     */   private ReferenceBinding typeToRecord(TypeBinding type) {
/* 800 */     if (type.isArrayType()) {
/* 801 */       type = ((ArrayBinding)type).leafComponentType;
/*     */     }
/* 803 */     switch (type.kind()) {
/*     */     case 132:
/*     */     case 516:
/*     */     case 4100:
/*     */     case 8196:
/* 808 */       return null;
/*     */     case 260:
/*     */     case 1028:
/* 811 */       type = type.erasure();
/*     */     }
/* 813 */     ReferenceBinding refType = (ReferenceBinding)type;
/* 814 */     if (refType.isLocalType()) return null;
/* 815 */     return refType;
/*     */   }
/*     */   public void verifyMethods(MethodVerifier verifier) {
/* 818 */     int i = 0; for (int length = this.topLevelTypes.length; i < length; i++)
/* 819 */       this.topLevelTypes[i].verifyMethods(verifier);
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\ecj-3.5.2.jar
 * Qualified Name:     org.eclipse.jdt.internal.compiler.lookup.CompilationUnitScope
 * JD-Core Version:    0.6.0
 */