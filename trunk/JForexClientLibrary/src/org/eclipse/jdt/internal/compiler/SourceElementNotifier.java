/*     */ package org.eclipse.jdt.internal.compiler;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ import java.util.Map;
/*     */ import org.eclipse.jdt.core.compiler.CharOperation;
/*     */ import org.eclipse.jdt.internal.compiler.ast.ASTNode;
/*     */ import org.eclipse.jdt.internal.compiler.ast.AbstractMethodDeclaration;
/*     */ import org.eclipse.jdt.internal.compiler.ast.AllocationExpression;
/*     */ import org.eclipse.jdt.internal.compiler.ast.Annotation;
/*     */ import org.eclipse.jdt.internal.compiler.ast.AnnotationMethodDeclaration;
/*     */ import org.eclipse.jdt.internal.compiler.ast.Argument;
/*     */ import org.eclipse.jdt.internal.compiler.ast.ArrayAllocationExpression;
/*     */ import org.eclipse.jdt.internal.compiler.ast.ArrayInitializer;
/*     */ import org.eclipse.jdt.internal.compiler.ast.ArrayReference;
/*     */ import org.eclipse.jdt.internal.compiler.ast.Assignment;
/*     */ import org.eclipse.jdt.internal.compiler.ast.Block;
/*     */ import org.eclipse.jdt.internal.compiler.ast.ClassLiteralAccess;
/*     */ import org.eclipse.jdt.internal.compiler.ast.CompilationUnitDeclaration;
/*     */ import org.eclipse.jdt.internal.compiler.ast.ConstructorDeclaration;
/*     */ import org.eclipse.jdt.internal.compiler.ast.ExplicitConstructorCall;
/*     */ import org.eclipse.jdt.internal.compiler.ast.Expression;
/*     */ import org.eclipse.jdt.internal.compiler.ast.FieldDeclaration;
/*     */ import org.eclipse.jdt.internal.compiler.ast.ImportReference;
/*     */ import org.eclipse.jdt.internal.compiler.ast.Initializer;
/*     */ import org.eclipse.jdt.internal.compiler.ast.MessageSend;
/*     */ import org.eclipse.jdt.internal.compiler.ast.MethodDeclaration;
/*     */ import org.eclipse.jdt.internal.compiler.ast.QualifiedAllocationExpression;
/*     */ import org.eclipse.jdt.internal.compiler.ast.Statement;
/*     */ import org.eclipse.jdt.internal.compiler.ast.ThisReference;
/*     */ import org.eclipse.jdt.internal.compiler.ast.TypeDeclaration;
/*     */ import org.eclipse.jdt.internal.compiler.ast.TypeParameter;
/*     */ import org.eclipse.jdt.internal.compiler.ast.TypeReference;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.BlockScope;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.ClassScope;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.TypeConstants;
/*     */ import org.eclipse.jdt.internal.compiler.util.HashtableOfObjectToInt;
/*     */ 
/*     */ public class SourceElementNotifier
/*     */ {
/*     */   ISourceElementRequestor requestor;
/*     */   boolean reportReferenceInfo;
/*     */   char[][] typeNames;
/*     */   char[][] superTypeNames;
/*     */   int nestedTypeIndex;
/*  89 */   LocalDeclarationVisitor localDeclarationVisitor = null;
/*     */   HashtableOfObjectToInt sourceEnds;
/*     */   Map nodesToCategories;
/*     */   int initialPosition;
/*     */   int eofPosition;
/*     */ 
/*     */   public SourceElementNotifier(ISourceElementRequestor requestor, boolean reportLocalDeclarations)
/*     */   {
/*  98 */     this.requestor = requestor;
/*  99 */     if (reportLocalDeclarations) {
/* 100 */       this.localDeclarationVisitor = new LocalDeclarationVisitor();
/*     */     }
/* 102 */     this.typeNames = new char[4][];
/* 103 */     this.superTypeNames = new char[4][];
/* 104 */     this.nestedTypeIndex = 0;
/*     */   }
/*     */   protected char[][][] getArguments(Argument[] arguments) {
/* 107 */     int argumentLength = arguments.length;
/* 108 */     char[][] argumentTypes = new char[argumentLength][];
/* 109 */     char[][] argumentNames = new char[argumentLength][];
/* 110 */     for (int i = 0; i < argumentLength; i++) {
/* 111 */       argumentTypes[i] = CharOperation.concatWith(arguments[i].type.getParameterizedTypeName(), '.');
/* 112 */       argumentNames[i] = arguments[i].name;
/*     */     }
/*     */ 
/* 115 */     return new char[][][] { argumentTypes, argumentNames };
/*     */   }
/*     */   protected char[][] getInterfaceNames(TypeDeclaration typeDeclaration) {
/* 118 */     char[][] interfaceNames = (char[][])null;
/* 119 */     int superInterfacesLength = 0;
/* 120 */     TypeReference[] superInterfaces = typeDeclaration.superInterfaces;
/* 121 */     if (superInterfaces != null) {
/* 122 */       superInterfacesLength = superInterfaces.length;
/* 123 */       interfaceNames = new char[superInterfacesLength][];
/*     */     }
/* 125 */     else if ((typeDeclaration.bits & 0x200) != 0)
/*     */     {
/* 127 */       QualifiedAllocationExpression alloc = typeDeclaration.allocation;
/* 128 */       if ((alloc != null) && (alloc.type != null)) {
/* 129 */         superInterfaces = new TypeReference[] { alloc.type };
/* 130 */         superInterfacesLength = 1;
/* 131 */         interfaceNames = new char[1][];
/*     */       }
/*     */     }
/*     */ 
/* 135 */     if (superInterfaces != null) {
/* 136 */       for (int i = 0; i < superInterfacesLength; i++) {
/* 137 */         interfaceNames[i] = 
/* 138 */           CharOperation.concatWith(superInterfaces[i].getParameterizedTypeName(), '.');
/*     */       }
/*     */     }
/* 141 */     return interfaceNames;
/*     */   }
/*     */   protected char[] getSuperclassName(TypeDeclaration typeDeclaration) {
/* 144 */     TypeReference superclass = typeDeclaration.superclass;
/* 145 */     return superclass != null ? CharOperation.concatWith(superclass.getParameterizedTypeName(), '.') : null;
/*     */   }
/*     */   protected char[][] getThrownExceptions(AbstractMethodDeclaration methodDeclaration) {
/* 148 */     char[][] thrownExceptionTypes = (char[][])null;
/* 149 */     TypeReference[] thrownExceptions = methodDeclaration.thrownExceptions;
/* 150 */     if (thrownExceptions != null) {
/* 151 */       int thrownExceptionLength = thrownExceptions.length;
/* 152 */       thrownExceptionTypes = new char[thrownExceptionLength][];
/* 153 */       for (int i = 0; i < thrownExceptionLength; i++) {
/* 154 */         thrownExceptionTypes[i] = 
/* 155 */           CharOperation.concatWith(thrownExceptions[i].getParameterizedTypeName(), '.');
/*     */       }
/*     */     }
/* 158 */     return thrownExceptionTypes;
/*     */   }
/*     */   protected char[][] getTypeParameterBounds(TypeParameter typeParameter) {
/* 161 */     TypeReference firstBound = typeParameter.type;
/* 162 */     TypeReference[] otherBounds = typeParameter.bounds;
/* 163 */     char[][] typeParameterBounds = (char[][])null;
/* 164 */     if (firstBound != null) {
/* 165 */       if (otherBounds != null) {
/* 166 */         int otherBoundsLength = otherBounds.length;
/* 167 */         char[][] boundNames = new char[otherBoundsLength + 1][];
/* 168 */         boundNames[0] = CharOperation.concatWith(firstBound.getParameterizedTypeName(), '.');
/* 169 */         for (int j = 0; j < otherBoundsLength; j++) {
/* 170 */           boundNames[(j + 1)] = 
/* 171 */             CharOperation.concatWith(otherBounds[j].getParameterizedTypeName(), '.');
/*     */         }
/* 173 */         typeParameterBounds = boundNames;
/*     */       } else {
/* 175 */         typeParameterBounds = new char[][] { CharOperation.concatWith(firstBound.getParameterizedTypeName(), '.') };
/*     */       }
/*     */     }
/* 178 */     else typeParameterBounds = CharOperation.NO_CHAR_CHAR;
/*     */ 
/* 181 */     return typeParameterBounds;
/*     */   }
/*     */   private ISourceElementRequestor.TypeParameterInfo[] getTypeParameterInfos(TypeParameter[] typeParameters) {
/* 184 */     if (typeParameters == null) return null;
/* 185 */     int typeParametersLength = typeParameters.length;
/* 186 */     ISourceElementRequestor.TypeParameterInfo[] result = new ISourceElementRequestor.TypeParameterInfo[typeParametersLength];
/* 187 */     for (int i = 0; i < typeParametersLength; i++) {
/* 188 */       TypeParameter typeParameter = typeParameters[i];
/* 189 */       char[][] typeParameterBounds = getTypeParameterBounds(typeParameter);
/* 190 */       ISourceElementRequestor.TypeParameterInfo typeParameterInfo = new ISourceElementRequestor.TypeParameterInfo();
/* 191 */       typeParameterInfo.declarationStart = typeParameter.declarationSourceStart;
/* 192 */       typeParameterInfo.declarationEnd = typeParameter.declarationSourceEnd;
/* 193 */       typeParameterInfo.name = typeParameter.name;
/* 194 */       typeParameterInfo.nameSourceStart = typeParameter.sourceStart;
/* 195 */       typeParameterInfo.nameSourceEnd = typeParameter.sourceEnd;
/* 196 */       typeParameterInfo.bounds = typeParameterBounds;
/* 197 */       result[i] = typeParameterInfo;
/*     */     }
/* 199 */     return result;
/*     */   }
/*     */ 
/*     */   private boolean hasDeprecatedAnnotation(Annotation[] annotations)
/*     */   {
/* 206 */     if (annotations != null) {
/* 207 */       int i = 0; for (int length = annotations.length; i < length; i++) {
/* 208 */         Annotation annotation = annotations[i];
/* 209 */         if (CharOperation.equals(annotation.type.getLastToken(), TypeConstants.JAVA_LANG_DEPRECATED[2])) {
/* 210 */           return true;
/*     */         }
/*     */       }
/*     */     }
/* 214 */     return false;
/*     */   }
/*     */ 
/*     */   protected void notifySourceElementRequestor(AbstractMethodDeclaration methodDeclaration, TypeDeclaration declaringType, ImportReference currentPackage)
/*     */   {
/* 222 */     boolean isInRange = 
/* 223 */       (this.initialPosition <= methodDeclaration.declarationSourceStart) && 
/* 224 */       (this.eofPosition >= methodDeclaration.declarationSourceEnd);
/*     */ 
/* 226 */     if (methodDeclaration.isClinit()) {
/* 227 */       visitIfNeeded(methodDeclaration);
/* 228 */       return;
/*     */     }
/*     */ 
/* 231 */     if (methodDeclaration.isDefaultConstructor()) {
/* 232 */       if (this.reportReferenceInfo) {
/* 233 */         ConstructorDeclaration constructorDeclaration = (ConstructorDeclaration)methodDeclaration;
/* 234 */         ExplicitConstructorCall constructorCall = constructorDeclaration.constructorCall;
/* 235 */         if (constructorCall != null) {
/* 236 */           switch (constructorCall.accessMode) {
/*     */           case 3:
/* 238 */             this.requestor.acceptConstructorReference(
/* 239 */               this.typeNames[(this.nestedTypeIndex - 1)], 
/* 240 */               constructorCall.arguments == null ? 0 : constructorCall.arguments.length, 
/* 241 */               constructorCall.sourceStart);
/* 242 */             break;
/*     */           case 1:
/*     */           case 2:
/* 245 */             this.requestor.acceptConstructorReference(
/* 246 */               this.superTypeNames[(this.nestedTypeIndex - 1)], 
/* 247 */               constructorCall.arguments == null ? 0 : constructorCall.arguments.length, 
/* 248 */               constructorCall.sourceStart);
/*     */           }
/*     */         }
/*     */       }
/*     */ 
/* 253 */       return;
/*     */     }
/* 255 */     char[][] argumentTypes = (char[][])null;
/* 256 */     char[][] argumentNames = (char[][])null;
/* 257 */     boolean isVarArgs = false;
/* 258 */     Argument[] arguments = methodDeclaration.arguments;
/* 259 */     if (arguments != null) {
/* 260 */       char[][][] argumentTypesAndNames = getArguments(arguments);
/* 261 */       argumentTypes = argumentTypesAndNames[0];
/* 262 */       argumentNames = argumentTypesAndNames[1];
/*     */ 
/* 264 */       isVarArgs = arguments[(arguments.length - 1)].isVarArgs();
/*     */     }
/* 266 */     char[][] thrownExceptionTypes = getThrownExceptions(methodDeclaration);
/*     */ 
/* 268 */     int selectorSourceEnd = -1;
/* 269 */     if (methodDeclaration.isConstructor()) {
/* 270 */       selectorSourceEnd = this.sourceEnds.get(methodDeclaration);
/* 271 */       if (isInRange) {
/* 272 */         int currentModifiers = methodDeclaration.modifiers;
/* 273 */         if (isVarArgs) {
/* 274 */           currentModifiers |= 128;
/*     */         }
/*     */ 
/* 277 */         boolean deprecated = ((currentModifiers & 0x100000) != 0) || (hasDeprecatedAnnotation(methodDeclaration.annotations));
/*     */ 
/* 279 */         ISourceElementRequestor.MethodInfo methodInfo = new ISourceElementRequestor.MethodInfo();
/* 280 */         methodInfo.isConstructor = true;
/* 281 */         methodInfo.declarationStart = methodDeclaration.declarationSourceStart;
/* 282 */         methodInfo.modifiers = (deprecated ? currentModifiers & 0xFFFF | 0x100000 : currentModifiers & 0xFFFF);
/* 283 */         methodInfo.name = methodDeclaration.selector;
/* 284 */         methodInfo.nameSourceStart = methodDeclaration.sourceStart;
/* 285 */         methodInfo.nameSourceEnd = selectorSourceEnd;
/* 286 */         methodInfo.parameterTypes = argumentTypes;
/* 287 */         methodInfo.parameterNames = argumentNames;
/* 288 */         methodInfo.exceptionTypes = thrownExceptionTypes;
/* 289 */         methodInfo.typeParameters = getTypeParameterInfos(methodDeclaration.typeParameters());
/* 290 */         methodInfo.categories = ((char[][])this.nodesToCategories.get(methodDeclaration));
/* 291 */         methodInfo.annotations = methodDeclaration.annotations;
/* 292 */         methodInfo.declaringPackageName = (currentPackage == null ? CharOperation.NO_CHAR : CharOperation.concatWith(currentPackage.tokens, '.'));
/* 293 */         methodInfo.declaringTypeModifiers = declaringType.modifiers;
/* 294 */         methodInfo.extraFlags = ExtraFlags.getExtraFlags(declaringType);
/* 295 */         methodInfo.node = methodDeclaration;
/* 296 */         this.requestor.enterConstructor(methodInfo);
/*     */       }
/* 298 */       if (this.reportReferenceInfo) {
/* 299 */         ConstructorDeclaration constructorDeclaration = (ConstructorDeclaration)methodDeclaration;
/* 300 */         ExplicitConstructorCall constructorCall = constructorDeclaration.constructorCall;
/* 301 */         if (constructorCall != null) {
/* 302 */           switch (constructorCall.accessMode) {
/*     */           case 3:
/* 304 */             this.requestor.acceptConstructorReference(
/* 305 */               this.typeNames[(this.nestedTypeIndex - 1)], 
/* 306 */               constructorCall.arguments == null ? 0 : constructorCall.arguments.length, 
/* 307 */               constructorCall.sourceStart);
/* 308 */             break;
/*     */           case 1:
/*     */           case 2:
/* 311 */             this.requestor.acceptConstructorReference(
/* 312 */               this.superTypeNames[(this.nestedTypeIndex - 1)], 
/* 313 */               constructorCall.arguments == null ? 0 : constructorCall.arguments.length, 
/* 314 */               constructorCall.sourceStart);
/*     */           }
/*     */         }
/*     */       }
/*     */ 
/* 319 */       visitIfNeeded(methodDeclaration);
/* 320 */       if (isInRange) {
/* 321 */         this.requestor.exitConstructor(methodDeclaration.declarationSourceEnd);
/*     */       }
/* 323 */       return;
/*     */     }
/* 325 */     selectorSourceEnd = this.sourceEnds.get(methodDeclaration);
/* 326 */     if (isInRange) {
/* 327 */       int currentModifiers = methodDeclaration.modifiers;
/* 328 */       if (isVarArgs) {
/* 329 */         currentModifiers |= 128;
/*     */       }
/*     */ 
/* 332 */       boolean deprecated = ((currentModifiers & 0x100000) != 0) || (hasDeprecatedAnnotation(methodDeclaration.annotations));
/*     */ 
/* 334 */       TypeReference returnType = (methodDeclaration instanceof MethodDeclaration) ? 
/* 335 */         ((MethodDeclaration)methodDeclaration).returnType : 
/* 336 */         null;
/* 337 */       ISourceElementRequestor.MethodInfo methodInfo = new ISourceElementRequestor.MethodInfo();
/* 338 */       methodInfo.isAnnotation = (methodDeclaration instanceof AnnotationMethodDeclaration);
/* 339 */       methodInfo.declarationStart = methodDeclaration.declarationSourceStart;
/* 340 */       methodInfo.modifiers = (deprecated ? currentModifiers & 0xFFFF | 0x100000 : currentModifiers & 0xFFFF);
/* 341 */       methodInfo.returnType = (returnType == null ? null : CharOperation.concatWith(returnType.getParameterizedTypeName(), '.'));
/* 342 */       methodInfo.name = methodDeclaration.selector;
/* 343 */       methodInfo.nameSourceStart = methodDeclaration.sourceStart;
/* 344 */       methodInfo.nameSourceEnd = selectorSourceEnd;
/* 345 */       methodInfo.parameterTypes = argumentTypes;
/* 346 */       methodInfo.parameterNames = argumentNames;
/* 347 */       methodInfo.exceptionTypes = thrownExceptionTypes;
/* 348 */       methodInfo.typeParameters = getTypeParameterInfos(methodDeclaration.typeParameters());
/* 349 */       methodInfo.categories = ((char[][])this.nodesToCategories.get(methodDeclaration));
/* 350 */       methodInfo.annotations = methodDeclaration.annotations;
/* 351 */       methodInfo.node = methodDeclaration;
/* 352 */       this.requestor.enterMethod(methodInfo);
/*     */     }
/*     */ 
/* 355 */     visitIfNeeded(methodDeclaration);
/*     */ 
/* 357 */     if (isInRange) {
/* 358 */       if ((methodDeclaration instanceof AnnotationMethodDeclaration)) {
/* 359 */         AnnotationMethodDeclaration annotationMethodDeclaration = (AnnotationMethodDeclaration)methodDeclaration;
/* 360 */         Expression expression = annotationMethodDeclaration.defaultValue;
/* 361 */         if (expression != null) {
/* 362 */           this.requestor.exitMethod(methodDeclaration.declarationSourceEnd, expression);
/* 363 */           return;
/*     */         }
/*     */       }
/* 366 */       this.requestor.exitMethod(methodDeclaration.declarationSourceEnd, null);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void notifySourceElementRequestor(CompilationUnitDeclaration parsedUnit, int sourceStart, int sourceEnd, boolean reportReference, HashtableOfObjectToInt sourceEndsMap, Map nodesToCategoriesMap)
/*     */   {
/* 381 */     this.initialPosition = sourceStart;
/* 382 */     this.eofPosition = sourceEnd;
/*     */ 
/* 384 */     this.reportReferenceInfo = reportReference;
/* 385 */     this.sourceEnds = sourceEndsMap;
/* 386 */     this.nodesToCategories = nodesToCategoriesMap;
/*     */     try
/*     */     {
/* 390 */       boolean isInRange = 
/* 391 */         (this.initialPosition <= parsedUnit.sourceStart) && 
/* 392 */         (this.eofPosition >= parsedUnit.sourceEnd);
/*     */ 
/* 395 */       int length = 0;
/* 396 */       ASTNode[] nodes = (ASTNode[])null;
/* 397 */       if (isInRange) {
/* 398 */         this.requestor.enterCompilationUnit();
/*     */       }
/* 400 */       ImportReference currentPackage = parsedUnit.currentPackage;
/* 401 */       if (this.localDeclarationVisitor != null) {
/* 402 */         this.localDeclarationVisitor.currentPackage = currentPackage;
/*     */       }
/* 404 */       ImportReference[] imports = parsedUnit.imports;
/* 405 */       TypeDeclaration[] types = parsedUnit.types;
/* 406 */       length = 
/* 407 */         (currentPackage == null ? 0 : 1) + (
/* 408 */         imports == null ? 0 : imports.length) + (
/* 409 */         types == null ? 0 : types.length);
/* 410 */       nodes = new ASTNode[length];
/* 411 */       int index = 0;
/* 412 */       if (currentPackage != null) {
/* 413 */         nodes[(index++)] = currentPackage;
/*     */       }
/* 415 */       if (imports != null) {
/* 416 */         int i = 0; for (int max = imports.length; i < max; i++) {
/* 417 */           nodes[(index++)] = imports[i];
/*     */         }
/*     */       }
/* 420 */       if (types != null) {
/* 421 */         int i = 0; for (int max = types.length; i < max; i++) {
/* 422 */           nodes[(index++)] = types[i];
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/* 427 */       if (length > 0) {
/* 428 */         quickSort(nodes, 0, length - 1);
/* 429 */         for (int i = 0; i < length; i++) {
/* 430 */           ASTNode node = nodes[i];
/* 431 */           if ((node instanceof ImportReference)) {
/* 432 */             ImportReference importRef = (ImportReference)node;
/* 433 */             if (node == parsedUnit.currentPackage)
/* 434 */               notifySourceElementRequestor(importRef, true);
/*     */             else
/* 436 */               notifySourceElementRequestor(importRef, false);
/*     */           }
/*     */           else {
/* 439 */             notifySourceElementRequestor((TypeDeclaration)node, true, null, currentPackage);
/*     */           }
/*     */         }
/*     */       }
/*     */ 
/* 444 */       if (isInRange)
/* 445 */         this.requestor.exitCompilationUnit(parsedUnit.sourceEnd);
/*     */     }
/*     */     finally {
/* 448 */       reset();
/*     */     }
/*     */   }
/*     */ 
/*     */   protected void notifySourceElementRequestor(FieldDeclaration fieldDeclaration, TypeDeclaration declaringType)
/*     */   {
/* 458 */     boolean isInRange = 
/* 459 */       (this.initialPosition <= fieldDeclaration.declarationSourceStart) && 
/* 460 */       (this.eofPosition >= fieldDeclaration.declarationSourceEnd);
/*     */ 
/* 462 */     switch (fieldDeclaration.getKind()) {
/*     */     case 3:
/* 464 */       if (!this.reportReferenceInfo)
/*     */         break;
/* 466 */       if (!(fieldDeclaration.initialization instanceof AllocationExpression)) break;
/* 467 */       AllocationExpression alloc = (AllocationExpression)fieldDeclaration.initialization;
/* 468 */       this.requestor.acceptConstructorReference(
/* 469 */         declaringType.name, 
/* 470 */         alloc.arguments == null ? 0 : alloc.arguments.length, 
/* 471 */         alloc.sourceStart);
/*     */     case 1:
/* 476 */       int fieldEndPosition = this.sourceEnds.get(fieldDeclaration);
/* 477 */       if (fieldEndPosition == -1)
/*     */       {
/* 479 */         fieldEndPosition = fieldDeclaration.declarationSourceEnd;
/*     */       }
/* 481 */       if (isInRange) {
/* 482 */         int currentModifiers = fieldDeclaration.modifiers;
/*     */ 
/* 485 */         boolean deprecated = ((currentModifiers & 0x100000) != 0) || (hasDeprecatedAnnotation(fieldDeclaration.annotations));
/*     */ 
/* 487 */         char[] typeName = (char[])null;
/* 488 */         if (fieldDeclaration.type == null)
/*     */         {
/* 490 */           typeName = declaringType.name;
/* 491 */           currentModifiers |= 16384;
/*     */         }
/*     */         else {
/* 494 */           typeName = CharOperation.concatWith(fieldDeclaration.type.getParameterizedTypeName(), '.');
/*     */         }
/* 496 */         ISourceElementRequestor.FieldInfo fieldInfo = new ISourceElementRequestor.FieldInfo();
/* 497 */         fieldInfo.declarationStart = fieldDeclaration.declarationSourceStart;
/* 498 */         fieldInfo.name = fieldDeclaration.name;
/* 499 */         fieldInfo.modifiers = (deprecated ? currentModifiers & 0xFFFF | 0x100000 : currentModifiers & 0xFFFF);
/* 500 */         fieldInfo.type = typeName;
/* 501 */         fieldInfo.nameSourceStart = fieldDeclaration.sourceStart;
/* 502 */         fieldInfo.nameSourceEnd = fieldDeclaration.sourceEnd;
/* 503 */         fieldInfo.categories = ((char[][])this.nodesToCategories.get(fieldDeclaration));
/* 504 */         fieldInfo.annotations = fieldDeclaration.annotations;
/* 505 */         fieldInfo.node = fieldDeclaration;
/* 506 */         this.requestor.enterField(fieldInfo);
/*     */       }
/* 508 */       visitIfNeeded(fieldDeclaration, declaringType);
/* 509 */       if (isInRange) {
/* 510 */         this.requestor.exitField(
/* 512 */           (fieldDeclaration.initialization == null) || 
/* 513 */           ((fieldDeclaration.initialization instanceof ArrayInitializer)) || 
/* 514 */           ((fieldDeclaration.initialization instanceof AllocationExpression)) || 
/* 515 */           ((fieldDeclaration.initialization instanceof ArrayAllocationExpression)) || 
/* 516 */           ((fieldDeclaration.initialization instanceof Assignment)) || 
/* 517 */           ((fieldDeclaration.initialization instanceof ClassLiteralAccess)) || 
/* 518 */           ((fieldDeclaration.initialization instanceof MessageSend)) || 
/* 519 */           ((fieldDeclaration.initialization instanceof ArrayReference)) || 
/* 520 */           ((fieldDeclaration.initialization instanceof ThisReference)) ? 
/* 521 */           -1 : 
/* 522 */           fieldDeclaration.initialization.sourceStart, 
/* 523 */           fieldEndPosition, 
/* 524 */           fieldDeclaration.declarationSourceEnd);
/*     */       }
/* 526 */       break;
/*     */     case 2:
/* 528 */       if (isInRange) {
/* 529 */         this.requestor.enterInitializer(
/* 530 */           fieldDeclaration.declarationSourceStart, 
/* 531 */           fieldDeclaration.modifiers);
/*     */       }
/* 533 */       visitIfNeeded((Initializer)fieldDeclaration);
/* 534 */       if (isInRange)
/* 535 */         this.requestor.exitInitializer(fieldDeclaration.declarationSourceEnd);
/*     */     }
/*     */   }
/*     */ 
/*     */   protected void notifySourceElementRequestor(ImportReference importReference, boolean isPackage)
/*     */   {
/* 543 */     if (isPackage)
/* 544 */       this.requestor.acceptPackage(importReference);
/*     */     else
/* 546 */       this.requestor.acceptImport(
/* 547 */         importReference.declarationSourceStart, 
/* 548 */         importReference.declarationSourceEnd, 
/* 549 */         importReference.tokens, 
/* 550 */         (importReference.bits & 0x20000) != 0, 
/* 551 */         importReference.modifiers);
/*     */   }
/*     */ 
/*     */   protected void notifySourceElementRequestor(TypeDeclaration typeDeclaration, boolean notifyTypePresence, TypeDeclaration declaringType, ImportReference currentPackage)
/*     */   {
/* 556 */     if (CharOperation.equals(TypeConstants.PACKAGE_INFO_NAME, typeDeclaration.name)) return;
/*     */ 
/* 559 */     boolean isInRange = 
/* 560 */       (this.initialPosition <= typeDeclaration.declarationSourceStart) && 
/* 561 */       (this.eofPosition >= typeDeclaration.declarationSourceEnd);
/*     */ 
/* 563 */     FieldDeclaration[] fields = typeDeclaration.fields;
/* 564 */     AbstractMethodDeclaration[] methods = typeDeclaration.methods;
/* 565 */     TypeDeclaration[] memberTypes = typeDeclaration.memberTypes;
/* 566 */     int fieldCounter = fields == null ? 0 : fields.length;
/* 567 */     int methodCounter = methods == null ? 0 : methods.length;
/* 568 */     int memberTypeCounter = memberTypes == null ? 0 : memberTypes.length;
/* 569 */     int fieldIndex = 0;
/* 570 */     int methodIndex = 0;
/* 571 */     int memberTypeIndex = 0;
/*     */ 
/* 573 */     if (notifyTypePresence) {
/* 574 */       char[][] interfaceNames = getInterfaceNames(typeDeclaration);
/* 575 */       int kind = TypeDeclaration.kind(typeDeclaration.modifiers);
/* 576 */       char[] implicitSuperclassName = TypeConstants.CharArray_JAVA_LANG_OBJECT;
/* 577 */       if (isInRange) {
/* 578 */         int currentModifiers = typeDeclaration.modifiers;
/*     */ 
/* 581 */         boolean deprecated = ((currentModifiers & 0x100000) != 0) || (hasDeprecatedAnnotation(typeDeclaration.annotations));
/*     */ 
/* 583 */         boolean isEnumInit = (typeDeclaration.allocation != null) && (typeDeclaration.allocation.enumConstant != null);
/*     */         char[] superclassName;
/*     */         char[] superclassName;
/* 585 */         if (isEnumInit) {
/* 586 */           currentModifiers |= 16384;
/* 587 */           superclassName = declaringType.name;
/*     */         } else {
/* 589 */           superclassName = getSuperclassName(typeDeclaration);
/*     */         }
/* 591 */         ISourceElementRequestor.TypeInfo typeInfo = new ISourceElementRequestor.TypeInfo();
/* 592 */         if (typeDeclaration.allocation == null)
/* 593 */           typeInfo.declarationStart = typeDeclaration.declarationSourceStart;
/* 594 */         else if (isEnumInit)
/* 595 */           typeInfo.declarationStart = typeDeclaration.allocation.enumConstant.sourceStart;
/*     */         else {
/* 597 */           typeInfo.declarationStart = typeDeclaration.allocation.sourceStart;
/*     */         }
/* 599 */         typeInfo.modifiers = (deprecated ? currentModifiers & 0xFFFF | 0x100000 : currentModifiers & 0xFFFF);
/* 600 */         typeInfo.name = typeDeclaration.name;
/* 601 */         typeInfo.nameSourceStart = (isEnumInit ? typeDeclaration.allocation.enumConstant.sourceStart : typeDeclaration.sourceStart);
/* 602 */         typeInfo.nameSourceEnd = sourceEnd(typeDeclaration);
/* 603 */         typeInfo.superclass = superclassName;
/* 604 */         typeInfo.superinterfaces = interfaceNames;
/* 605 */         typeInfo.typeParameters = getTypeParameterInfos(typeDeclaration.typeParameters);
/* 606 */         typeInfo.categories = ((char[][])this.nodesToCategories.get(typeDeclaration));
/* 607 */         typeInfo.secondary = typeDeclaration.isSecondary();
/* 608 */         typeInfo.anonymousMember = ((typeDeclaration.allocation != null) && (typeDeclaration.allocation.enclosingInstance != null));
/* 609 */         typeInfo.annotations = typeDeclaration.annotations;
/* 610 */         typeInfo.extraFlags = ExtraFlags.getExtraFlags(typeDeclaration);
/* 611 */         typeInfo.node = typeDeclaration;
/* 612 */         this.requestor.enterType(typeInfo);
/* 613 */         switch (kind) {
/*     */         case 1:
/* 615 */           if (superclassName == null) break;
/* 616 */           implicitSuperclassName = superclassName;
/* 617 */           break;
/*     */         case 2:
/* 619 */           implicitSuperclassName = TypeConstants.CharArray_JAVA_LANG_OBJECT;
/* 620 */           break;
/*     */         case 3:
/* 622 */           implicitSuperclassName = TypeConstants.CharArray_JAVA_LANG_ENUM;
/* 623 */           break;
/*     */         case 4:
/* 625 */           implicitSuperclassName = TypeConstants.CharArray_JAVA_LANG_ANNOTATION_ANNOTATION;
/*     */         }
/*     */       }
/*     */ 
/* 629 */       if (this.nestedTypeIndex == this.typeNames.length)
/*     */       {
/* 631 */         System.arraycopy(this.typeNames, 0, this.typeNames = new char[this.nestedTypeIndex * 2][], 0, this.nestedTypeIndex);
/* 632 */         System.arraycopy(this.superTypeNames, 0, this.superTypeNames = new char[this.nestedTypeIndex * 2][], 0, this.nestedTypeIndex);
/*     */       }
/* 634 */       this.typeNames[this.nestedTypeIndex] = typeDeclaration.name;
/* 635 */       this.superTypeNames[(this.nestedTypeIndex++)] = implicitSuperclassName;
/*     */     }
/* 637 */     while ((fieldIndex < fieldCounter) || 
/* 638 */       (memberTypeIndex < memberTypeCounter) || 
/* 639 */       (methodIndex < methodCounter)) {
/* 640 */       FieldDeclaration nextFieldDeclaration = null;
/* 641 */       AbstractMethodDeclaration nextMethodDeclaration = null;
/* 642 */       TypeDeclaration nextMemberDeclaration = null;
/*     */ 
/* 644 */       int position = 2147483647;
/* 645 */       int nextDeclarationType = -1;
/* 646 */       if (fieldIndex < fieldCounter) {
/* 647 */         nextFieldDeclaration = fields[fieldIndex];
/* 648 */         if (nextFieldDeclaration.declarationSourceStart < position) {
/* 649 */           position = nextFieldDeclaration.declarationSourceStart;
/* 650 */           nextDeclarationType = 0;
/*     */         }
/*     */       }
/* 653 */       if (methodIndex < methodCounter) {
/* 654 */         nextMethodDeclaration = methods[methodIndex];
/* 655 */         if (nextMethodDeclaration.declarationSourceStart < position) {
/* 656 */           position = nextMethodDeclaration.declarationSourceStart;
/* 657 */           nextDeclarationType = 1;
/*     */         }
/*     */       }
/* 660 */       if (memberTypeIndex < memberTypeCounter) {
/* 661 */         nextMemberDeclaration = memberTypes[memberTypeIndex];
/* 662 */         if (nextMemberDeclaration.declarationSourceStart < position) {
/* 663 */           position = nextMemberDeclaration.declarationSourceStart;
/* 664 */           nextDeclarationType = 2;
/*     */         }
/*     */       }
/* 667 */       switch (nextDeclarationType) {
/*     */       case 0:
/* 669 */         fieldIndex++;
/* 670 */         notifySourceElementRequestor(nextFieldDeclaration, typeDeclaration);
/* 671 */         break;
/*     */       case 1:
/* 673 */         methodIndex++;
/* 674 */         notifySourceElementRequestor(nextMethodDeclaration, typeDeclaration, currentPackage);
/* 675 */         break;
/*     */       case 2:
/* 677 */         memberTypeIndex++;
/* 678 */         notifySourceElementRequestor(nextMemberDeclaration, true, null, currentPackage);
/*     */       }
/*     */     }
/* 681 */     if (notifyTypePresence) {
/* 682 */       if (isInRange) {
/* 683 */         this.requestor.exitType(typeDeclaration.declarationSourceEnd);
/*     */       }
/* 685 */       this.nestedTypeIndex -= 1;
/*     */     }
/*     */   }
/*     */ 
/*     */   private static void quickSort(ASTNode[] sortedCollection, int left, int right)
/*     */   {
/* 692 */     int original_left = left;
/* 693 */     int original_right = right;
/* 694 */     ASTNode mid = sortedCollection[(left + (right - left) / 2)];
/*     */     do {
/* 696 */       while (sortedCollection[left].sourceStart < mid.sourceStart) {
/* 697 */         left++;
/*     */       }
/* 699 */       while (mid.sourceStart < sortedCollection[right].sourceStart) {
/* 700 */         right--;
/*     */       }
/* 702 */       if (left <= right) {
/* 703 */         ASTNode tmp = sortedCollection[left];
/* 704 */         sortedCollection[left] = sortedCollection[right];
/* 705 */         sortedCollection[right] = tmp;
/* 706 */         left++;
/* 707 */         right--;
/*     */       }
/*     */     }
/* 709 */     while (left <= right);
/* 710 */     if (original_left < right) {
/* 711 */       quickSort(sortedCollection, original_left, right);
/*     */     }
/* 713 */     if (left < original_right)
/* 714 */       quickSort(sortedCollection, left, original_right);
/*     */   }
/*     */ 
/*     */   private void reset() {
/* 718 */     this.typeNames = new char[4][];
/* 719 */     this.superTypeNames = new char[4][];
/* 720 */     this.nestedTypeIndex = 0;
/*     */ 
/* 722 */     this.sourceEnds = null;
/*     */   }
/*     */   private int sourceEnd(TypeDeclaration typeDeclaration) {
/* 725 */     if ((typeDeclaration.bits & 0x200) != 0) {
/* 726 */       QualifiedAllocationExpression allocation = typeDeclaration.allocation;
/* 727 */       if (allocation.enumConstant != null)
/* 728 */         return allocation.enumConstant.sourceEnd;
/* 729 */       return allocation.type.sourceEnd;
/*     */     }
/* 731 */     return typeDeclaration.sourceEnd;
/*     */   }
/*     */ 
/*     */   private void visitIfNeeded(AbstractMethodDeclaration method) {
/* 735 */     if ((this.localDeclarationVisitor != null) && 
/* 736 */       ((method.bits & 0x2) != 0)) {
/* 737 */       if ((method instanceof ConstructorDeclaration)) {
/* 738 */         ConstructorDeclaration constructorDeclaration = (ConstructorDeclaration)method;
/* 739 */         if (constructorDeclaration.constructorCall != null) {
/* 740 */           constructorDeclaration.constructorCall.traverse(this.localDeclarationVisitor, method.scope);
/*     */         }
/*     */       }
/* 743 */       if (method.statements != null) {
/* 744 */         int statementsLength = method.statements.length;
/* 745 */         for (int i = 0; i < statementsLength; i++)
/* 746 */           method.statements[i].traverse(this.localDeclarationVisitor, method.scope);
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   private void visitIfNeeded(FieldDeclaration field, TypeDeclaration declaringType) {
/* 752 */     if ((this.localDeclarationVisitor != null) && 
/* 753 */       ((field.bits & 0x2) != 0) && 
/* 754 */       (field.initialization != null))
/*     */       try {
/* 756 */         this.localDeclarationVisitor.pushDeclaringType(declaringType);
/* 757 */         field.initialization.traverse(this.localDeclarationVisitor, null);
/*     */       } finally {
/* 759 */         this.localDeclarationVisitor.popDeclaringType();
/*     */       }
/*     */   }
/*     */ 
/*     */   private void visitIfNeeded(Initializer initializer)
/*     */   {
/* 766 */     if ((this.localDeclarationVisitor != null) && 
/* 767 */       ((initializer.bits & 0x2) != 0) && 
/* 768 */       (initializer.block != null))
/* 769 */       initializer.block.traverse(this.localDeclarationVisitor, null);
/*     */   }
/*     */ 
/*     */   public class LocalDeclarationVisitor extends ASTVisitor
/*     */   {
/*     */     public ImportReference currentPackage;
/*     */     ArrayList declaringTypes;
/*     */ 
/*     */     public LocalDeclarationVisitor()
/*     */     {
/*     */     }
/*     */ 
/*     */     public void pushDeclaringType(TypeDeclaration declaringType)
/*     */     {
/*  60 */       if (this.declaringTypes == null) {
/*  61 */         this.declaringTypes = new ArrayList();
/*     */       }
/*  63 */       this.declaringTypes.add(declaringType);
/*     */     }
/*     */     public void popDeclaringType() {
/*  66 */       this.declaringTypes.remove(this.declaringTypes.size() - 1);
/*     */     }
/*     */     public TypeDeclaration peekDeclaringType() {
/*  69 */       if (this.declaringTypes == null) return null;
/*  70 */       int size = this.declaringTypes.size();
/*  71 */       if (size == 0) return null;
/*  72 */       return (TypeDeclaration)this.declaringTypes.get(size - 1);
/*     */     }
/*     */     public boolean visit(TypeDeclaration typeDeclaration, BlockScope scope) {
/*  75 */       SourceElementNotifier.this.notifySourceElementRequestor(typeDeclaration, true, peekDeclaringType(), this.currentPackage);
/*  76 */       return false;
/*     */     }
/*     */     public boolean visit(TypeDeclaration typeDeclaration, ClassScope scope) {
/*  79 */       SourceElementNotifier.this.notifySourceElementRequestor(typeDeclaration, true, peekDeclaringType(), this.currentPackage);
/*  80 */       return false;
/*     */     }
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\ecj-3.5.2.jar
 * Qualified Name:     org.eclipse.jdt.internal.compiler.SourceElementNotifier
 * JD-Core Version:    0.6.0
 */