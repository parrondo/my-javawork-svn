/*      */ package org.eclipse.jdt.internal.compiler.lookup;
/*      */ 
/*      */ import java.util.ArrayList;
/*      */ import java.util.HashMap;
/*      */ import java.util.Map;
/*      */ import org.eclipse.jdt.core.compiler.CharOperation;
/*      */ import org.eclipse.jdt.internal.compiler.ast.AbstractMethodDeclaration;
/*      */ import org.eclipse.jdt.internal.compiler.ast.CompilationUnitDeclaration;
/*      */ import org.eclipse.jdt.internal.compiler.ast.FieldDeclaration;
/*      */ import org.eclipse.jdt.internal.compiler.ast.QualifiedAllocationExpression;
/*      */ import org.eclipse.jdt.internal.compiler.ast.TypeDeclaration;
/*      */ import org.eclipse.jdt.internal.compiler.ast.TypeParameter;
/*      */ import org.eclipse.jdt.internal.compiler.ast.TypeReference;
/*      */ import org.eclipse.jdt.internal.compiler.env.AccessRestriction;
/*      */ import org.eclipse.jdt.internal.compiler.impl.CompilerOptions;
/*      */ import org.eclipse.jdt.internal.compiler.problem.AbortCompilation;
/*      */ import org.eclipse.jdt.internal.compiler.problem.ProblemReporter;
/*      */ import org.eclipse.jdt.internal.compiler.util.HashtableOfObject;
/*      */ 
/*      */ public class ClassScope extends Scope
/*      */ {
/*      */   public TypeDeclaration referenceContext;
/*      */   public TypeReference superTypeReference;
/*      */   ArrayList deferredBoundChecks;
/*      */ 
/*      */   public ClassScope(Scope parent, TypeDeclaration context)
/*      */   {
/*   38 */     super(3, parent);
/*   39 */     this.referenceContext = context;
/*   40 */     this.deferredBoundChecks = null;
/*      */   }
/*      */ 
/*      */   void buildAnonymousTypeBinding(SourceTypeBinding enclosingType, ReferenceBinding supertype) {
/*   44 */     LocalTypeBinding anonymousType = buildLocalType(enclosingType, supertype, enclosingType.fPackage);
/*   45 */     anonymousType.modifiers |= 134217728;
/*   46 */     if (supertype.isInterface()) {
/*   47 */       anonymousType.superclass = getJavaLangObject();
/*   48 */       anonymousType.superInterfaces = new ReferenceBinding[] { supertype };
/*   49 */       TypeReference typeReference = this.referenceContext.allocation.type;
/*   50 */       if ((typeReference != null) && 
/*   51 */         ((supertype.tagBits & 0x40000000) != 0L)) {
/*   52 */         problemReporter().superTypeCannotUseWildcard(anonymousType, typeReference, supertype);
/*   53 */         anonymousType.tagBits |= 131072L;
/*   54 */         anonymousType.superInterfaces = Binding.NO_SUPERINTERFACES;
/*      */       }
/*      */     }
/*      */     else {
/*   58 */       anonymousType.superclass = supertype;
/*   59 */       anonymousType.superInterfaces = Binding.NO_SUPERINTERFACES;
/*   60 */       TypeReference typeReference = this.referenceContext.allocation.type;
/*   61 */       if (typeReference != null) {
/*   62 */         if (supertype.erasure().id == 41) {
/*   63 */           problemReporter().cannotExtendEnum(anonymousType, typeReference, supertype);
/*   64 */           anonymousType.tagBits |= 131072L;
/*   65 */           anonymousType.superclass = getJavaLangObject();
/*   66 */         } else if (supertype.isFinal()) {
/*   67 */           problemReporter().anonymousClassCannotExtendFinalClass(typeReference, supertype);
/*   68 */           anonymousType.tagBits |= 131072L;
/*   69 */           anonymousType.superclass = getJavaLangObject();
/*   70 */         } else if ((supertype.tagBits & 0x40000000) != 0L) {
/*   71 */           problemReporter().superTypeCannotUseWildcard(anonymousType, typeReference, supertype);
/*   72 */           anonymousType.tagBits |= 131072L;
/*   73 */           anonymousType.superclass = getJavaLangObject();
/*      */         }
/*      */       }
/*      */     }
/*   77 */     connectMemberTypes();
/*   78 */     buildFieldsAndMethods();
/*   79 */     anonymousType.faultInTypesForFieldsAndMethods();
/*   80 */     anonymousType.verifyMethods(environment().methodVerifier());
/*      */   }
/*      */ 
/*      */   void buildFields() {
/*   84 */     SourceTypeBinding sourceType = this.referenceContext.binding;
/*   85 */     if (sourceType.areFieldsInitialized()) return;
/*   86 */     if (this.referenceContext.fields == null) {
/*   87 */       sourceType.setFields(Binding.NO_FIELDS);
/*   88 */       return;
/*      */     }
/*      */ 
/*   91 */     FieldDeclaration[] fields = this.referenceContext.fields;
/*   92 */     int size = fields.length;
/*   93 */     int count = 0;
/*   94 */     for (int i = 0; i < size; i++) {
/*   95 */       switch (fields[i].getKind()) {
/*      */       case 1:
/*      */       case 3:
/*   98 */         count++;
/*      */       case 2:
/*      */       }
/*      */     }
/*      */ 
/*  103 */     FieldBinding[] fieldBindings = new FieldBinding[count];
/*  104 */     HashtableOfObject knownFieldNames = new HashtableOfObject(count);
/*  105 */     count = 0;
/*  106 */     for (int i = 0; i < size; i++) {
/*  107 */       FieldDeclaration field = fields[i];
/*  108 */       if (field.getKind() == 2) {
/*  109 */         if (sourceType.isInterface())
/*  110 */           problemReporter().interfaceCannotHaveInitializers(sourceType, field);
/*      */       } else {
/*  112 */         FieldBinding fieldBinding = new FieldBinding(field, null, field.modifiers | 0x2000000, sourceType);
/*  113 */         fieldBinding.id = count;
/*      */ 
/*  115 */         checkAndSetModifiersForField(fieldBinding, field);
/*      */ 
/*  117 */         if (knownFieldNames.containsKey(field.name)) {
/*  118 */           FieldBinding previousBinding = (FieldBinding)knownFieldNames.get(field.name);
/*  119 */           if (previousBinding != null) {
/*  120 */             for (int f = 0; f < i; f++) {
/*  121 */               FieldDeclaration previousField = fields[f];
/*  122 */               if (previousField.binding == previousBinding) {
/*  123 */                 problemReporter().duplicateFieldInType(sourceType, previousField);
/*  124 */                 break;
/*      */               }
/*      */             }
/*      */           }
/*  128 */           knownFieldNames.put(field.name, null);
/*  129 */           problemReporter().duplicateFieldInType(sourceType, field);
/*  130 */           field.binding = null;
/*      */         } else {
/*  132 */           knownFieldNames.put(field.name, fieldBinding);
/*      */ 
/*  134 */           fieldBindings[(count++)] = fieldBinding;
/*      */         }
/*      */       }
/*      */     }
/*      */ 
/*  139 */     if (count != fieldBindings.length)
/*  140 */       System.arraycopy(fieldBindings, 0, fieldBindings = new FieldBinding[count], 0, count);
/*  141 */     sourceType.tagBits &= -12289L;
/*  142 */     sourceType.setFields(fieldBindings);
/*      */   }
/*      */ 
/*      */   void buildFieldsAndMethods() {
/*  146 */     buildFields();
/*  147 */     buildMethods();
/*      */ 
/*  149 */     SourceTypeBinding sourceType = this.referenceContext.binding;
/*  150 */     if ((sourceType.isMemberType()) && (!sourceType.isLocalType())) {
/*  151 */       ((MemberTypeBinding)sourceType).checkSyntheticArgsAndFields();
/*      */     }
/*  153 */     ReferenceBinding[] memberTypes = sourceType.memberTypes;
/*  154 */     int i = 0; for (int length = memberTypes.length; i < length; i++)
/*  155 */       ((SourceTypeBinding)memberTypes[i]).scope.buildFieldsAndMethods();
/*      */   }
/*      */ 
/*      */   private LocalTypeBinding buildLocalType(SourceTypeBinding enclosingType, ReferenceBinding anonymousOriginalSuperType, PackageBinding packageBinding)
/*      */   {
/*  160 */     this.referenceContext.scope = this;
/*  161 */     this.referenceContext.staticInitializerScope = new MethodScope(this, this.referenceContext, true);
/*  162 */     this.referenceContext.initializerScope = new MethodScope(this, this.referenceContext, false);
/*      */ 
/*  165 */     LocalTypeBinding localType = new LocalTypeBinding(this, enclosingType, innermostSwitchCase(), anonymousOriginalSuperType);
/*  166 */     this.referenceContext.binding = localType;
/*  167 */     checkAndSetModifiers();
/*  168 */     buildTypeVariables();
/*      */ 
/*  171 */     ReferenceBinding[] memberTypeBindings = Binding.NO_MEMBER_TYPES;
/*  172 */     if (this.referenceContext.memberTypes != null) {
/*  173 */       int size = this.referenceContext.memberTypes.length;
/*  174 */       memberTypeBindings = new ReferenceBinding[size];
/*  175 */       int count = 0;
/*  176 */       for (int i = 0; i < size; i++) {
/*  177 */         TypeDeclaration memberContext = this.referenceContext.memberTypes[i];
/*  178 */         switch (TypeDeclaration.kind(memberContext.modifiers)) {
/*      */         case 2:
/*      */         case 4:
/*  181 */           problemReporter().illegalLocalTypeDeclaration(memberContext);
/*  182 */           break;
/*      */         case 3:
/*      */         default:
/*  184 */           ReferenceBinding type = localType;
/*      */           while (true)
/*      */           {
/*  187 */             if (CharOperation.equals(type.sourceName, memberContext.name)) {
/*  188 */               problemReporter().typeCollidesWithEnclosingType(memberContext);
/*      */             }
/*      */             else {
/*  191 */               type = type.enclosingType();
/*  192 */               if (type != null)
/*      */                 continue;
/*  194 */               int j = 0;
/*      */               while (true) if (CharOperation.equals(this.referenceContext.memberTypes[j].name, memberContext.name)) {
/*  196 */                   problemReporter().duplicateNestedType(memberContext);
/*      */                 }
/*      */                 else
/*      */                 {
/*  194 */                   j++; if (j < i)
/*      */                   {
/*      */                     continue;
/*      */                   }
/*      */ 
/*  200 */                   ClassScope memberScope = new ClassScope(this, this.referenceContext.memberTypes[i]);
/*  201 */                   LocalTypeBinding memberBinding = memberScope.buildLocalType(localType, null, packageBinding);
/*  202 */                   memberBinding.setAsMemberType();
/*  203 */                   memberTypeBindings[(count++)] = memberBinding;
/*      */                 } 
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/*  205 */       if (count != size)
/*  206 */         System.arraycopy(memberTypeBindings, 0, memberTypeBindings = new ReferenceBinding[count], 0, count);
/*      */     }
/*  208 */     localType.memberTypes = memberTypeBindings;
/*  209 */     return localType;
/*      */   }
/*      */ 
/*      */   void buildLocalTypeBinding(SourceTypeBinding enclosingType)
/*      */   {
/*  214 */     LocalTypeBinding localType = buildLocalType(enclosingType, null, enclosingType.fPackage);
/*  215 */     connectTypeHierarchy();
/*  216 */     if (compilerOptions().sourceLevel >= 3211264L) {
/*  217 */       checkParameterizedTypeBounds();
/*  218 */       checkParameterizedSuperTypeCollisions();
/*      */     }
/*  220 */     buildFieldsAndMethods();
/*  221 */     localType.faultInTypesForFieldsAndMethods();
/*      */ 
/*  223 */     this.referenceContext.binding.verifyMethods(environment().methodVerifier());
/*      */   }
/*      */ 
/*      */   private void buildMemberTypes(AccessRestriction accessRestriction) {
/*  227 */     SourceTypeBinding sourceType = this.referenceContext.binding;
/*  228 */     ReferenceBinding[] memberTypeBindings = Binding.NO_MEMBER_TYPES;
/*  229 */     if (this.referenceContext.memberTypes != null) {
/*  230 */       int length = this.referenceContext.memberTypes.length;
/*  231 */       memberTypeBindings = new ReferenceBinding[length];
/*  232 */       int count = 0;
/*  233 */       for (int i = 0; i < length; i++) {
/*  234 */         TypeDeclaration memberContext = this.referenceContext.memberTypes[i];
/*  235 */         switch (TypeDeclaration.kind(memberContext.modifiers)) {
/*      */         case 2:
/*      */         case 4:
/*  238 */           if ((!sourceType.isNestedType()) || 
/*  239 */             (!sourceType.isClass()) || 
/*  240 */             (sourceType.isStatic())) break;
/*  241 */           problemReporter().illegalLocalTypeDeclaration(memberContext);
/*  242 */           break;
/*      */         case 3:
/*      */         }
/*      */ 
/*  246 */         ReferenceBinding type = sourceType;
/*      */         while (true)
/*      */         {
/*  249 */           if (CharOperation.equals(type.sourceName, memberContext.name)) {
/*  250 */             problemReporter().typeCollidesWithEnclosingType(memberContext);
/*      */           }
/*      */           else {
/*  253 */             type = type.enclosingType();
/*  254 */             if (type != null)
/*      */               continue;
/*  256 */             int j = 0;
/*      */             while (true) if (CharOperation.equals(this.referenceContext.memberTypes[j].name, memberContext.name)) {
/*  258 */                 problemReporter().duplicateNestedType(memberContext);
/*      */               }
/*      */               else
/*      */               {
/*  256 */                 j++; if (j < i)
/*      */                 {
/*      */                   continue;
/*      */                 }
/*      */ 
/*  263 */                 ClassScope memberScope = new ClassScope(this, memberContext);
/*  264 */                 memberTypeBindings[(count++)] = memberScope.buildType(sourceType, sourceType.fPackage, accessRestriction);
/*      */               } 
/*      */           }
/*      */         }
/*      */       }
/*  266 */       if (count != length)
/*  267 */         System.arraycopy(memberTypeBindings, 0, memberTypeBindings = new ReferenceBinding[count], 0, count);
/*      */     }
/*  269 */     sourceType.memberTypes = memberTypeBindings;
/*      */   }
/*      */ 
/*      */   void buildMethods() {
/*  273 */     SourceTypeBinding sourceType = this.referenceContext.binding;
/*  274 */     if (sourceType.areMethodsInitialized()) return;
/*      */ 
/*  276 */     boolean isEnum = TypeDeclaration.kind(this.referenceContext.modifiers) == 3;
/*  277 */     if ((this.referenceContext.methods == null) && (!isEnum)) {
/*  278 */       this.referenceContext.binding.setMethods(Binding.NO_METHODS);
/*  279 */       return;
/*      */     }
/*      */ 
/*  283 */     AbstractMethodDeclaration[] methods = this.referenceContext.methods;
/*  284 */     int size = methods == null ? 0 : methods.length;
/*      */ 
/*  286 */     int clinitIndex = -1;
/*  287 */     for (int i = 0; i < size; i++) {
/*  288 */       if (methods[i].isClinit()) {
/*  289 */         clinitIndex = i;
/*  290 */         break;
/*      */       }
/*      */     }
/*      */ 
/*  294 */     int count = isEnum ? 2 : 0;
/*  295 */     MethodBinding[] methodBindings = new MethodBinding[(clinitIndex == -1 ? size : size - 1) + count];
/*      */ 
/*  297 */     if (isEnum) {
/*  298 */       methodBindings[0] = sourceType.addSyntheticEnumMethod(TypeConstants.VALUES);
/*  299 */       methodBindings[1] = sourceType.addSyntheticEnumMethod(TypeConstants.VALUEOF);
/*      */     }
/*      */ 
/*  302 */     if (sourceType.isAbstract()) {
/*  303 */       for (int i = 0; i < size; i++)
/*  304 */         if (i != clinitIndex) {
/*  305 */           MethodScope scope = new MethodScope(this, methods[i], false);
/*  306 */           MethodBinding methodBinding = scope.createMethod(methods[i]);
/*  307 */           if (methodBinding != null)
/*  308 */             methodBindings[(count++)] = methodBinding;
/*      */         }
/*      */     }
/*      */     else {
/*  312 */       boolean hasAbstractMethods = false;
/*  313 */       for (int i = 0; i < size; i++) {
/*  314 */         if (i != clinitIndex) {
/*  315 */           MethodScope scope = new MethodScope(this, methods[i], false);
/*  316 */           MethodBinding methodBinding = scope.createMethod(methods[i]);
/*  317 */           if (methodBinding != null) {
/*  318 */             methodBindings[(count++)] = methodBinding;
/*  319 */             hasAbstractMethods = (hasAbstractMethods) || (methodBinding.isAbstract());
/*      */           }
/*      */         }
/*      */       }
/*  323 */       if (hasAbstractMethods)
/*  324 */         problemReporter().abstractMethodInConcreteClass(sourceType);
/*      */     }
/*  326 */     if (count != methodBindings.length)
/*  327 */       System.arraycopy(methodBindings, 0, methodBindings = new MethodBinding[count], 0, count);
/*  328 */     sourceType.tagBits &= -49153L;
/*  329 */     sourceType.setMethods(methodBindings);
/*      */   }
/*      */ 
/*      */   SourceTypeBinding buildType(SourceTypeBinding enclosingType, PackageBinding packageBinding, AccessRestriction accessRestriction)
/*      */   {
/*  334 */     this.referenceContext.scope = this;
/*  335 */     this.referenceContext.staticInitializerScope = new MethodScope(this, this.referenceContext, true);
/*  336 */     this.referenceContext.initializerScope = new MethodScope(this, this.referenceContext, false);
/*      */ 
/*  338 */     if (enclosingType == null) {
/*  339 */       char[][] className = CharOperation.arrayConcat(packageBinding.compoundName, this.referenceContext.name);
/*  340 */       this.referenceContext.binding = new SourceTypeBinding(className, packageBinding, this);
/*      */     } else {
/*  342 */       char[][] className = CharOperation.deepCopy(enclosingType.compoundName);
/*  343 */       className[(className.length - 1)] = 
/*  344 */         CharOperation.concat(className[(className.length - 1)], this.referenceContext.name, '$');
/*  345 */       ReferenceBinding existingType = packageBinding.getType0(className[(className.length - 1)]);
/*  346 */       if ((existingType != null) && 
/*  347 */         (!(existingType instanceof UnresolvedReferenceBinding)))
/*      */       {
/*  352 */         this.parent.problemReporter().duplicateNestedType(this.referenceContext);
/*      */       }
/*      */ 
/*  355 */       this.referenceContext.binding = new MemberTypeBinding(className, this, enclosingType);
/*      */     }
/*      */ 
/*  358 */     SourceTypeBinding sourceType = this.referenceContext.binding;
/*  359 */     environment().setAccessRestriction(sourceType, accessRestriction);
/*  360 */     sourceType.fPackage.addType(sourceType);
/*  361 */     checkAndSetModifiers();
/*  362 */     buildTypeVariables();
/*  363 */     buildMemberTypes(accessRestriction);
/*  364 */     return sourceType;
/*      */   }
/*      */ 
/*      */   private void buildTypeVariables()
/*      */   {
/*  369 */     SourceTypeBinding sourceType = this.referenceContext.binding;
/*  370 */     TypeParameter[] typeParameters = this.referenceContext.typeParameters;
/*      */ 
/*  373 */     if ((typeParameters == null) || (compilerOptions().sourceLevel < 3211264L)) {
/*  374 */       sourceType.typeVariables = Binding.NO_TYPE_VARIABLES;
/*  375 */       return;
/*      */     }
/*  377 */     sourceType.typeVariables = Binding.NO_TYPE_VARIABLES;
/*      */ 
/*  379 */     if (sourceType.id == 1) {
/*  380 */       problemReporter().objectCannotBeGeneric(this.referenceContext);
/*  381 */       return;
/*      */     }
/*  383 */     sourceType.typeVariables = createTypeVariables(typeParameters, sourceType);
/*  384 */     sourceType.modifiers |= 1073741824;
/*      */   }
/*      */ 
/*      */   private void checkAndSetModifiers() {
/*  388 */     SourceTypeBinding sourceType = this.referenceContext.binding;
/*  389 */     int modifiers = sourceType.modifiers;
/*  390 */     if ((modifiers & 0x400000) != 0)
/*  391 */       problemReporter().duplicateModifierForType(sourceType);
/*  392 */     ReferenceBinding enclosingType = sourceType.enclosingType();
/*  393 */     boolean isMemberType = sourceType.isMemberType();
/*  394 */     if (isMemberType) {
/*  395 */       modifiers |= enclosingType.modifiers & 0x40000800;
/*      */ 
/*  397 */       if (enclosingType.isInterface())
/*  398 */         modifiers |= 1;
/*  399 */       if (sourceType.isEnum()) {
/*  400 */         if (!enclosingType.isStatic())
/*  401 */           problemReporter().nonStaticContextForEnumMemberType(sourceType);
/*      */         else
/*  403 */           modifiers |= 8;
/*      */       }
/*  405 */       if ((enclosingType.isViewedAsDeprecated()) && (!sourceType.isDeprecated()))
/*  406 */         modifiers |= 2097152;
/*  407 */     } else if (sourceType.isLocalType()) {
/*  408 */       if (sourceType.isEnum()) {
/*  409 */         problemReporter().illegalLocalTypeDeclaration(this.referenceContext);
/*  410 */         sourceType.modifiers = 0;
/*  411 */         return;
/*      */       }
/*  413 */       if (sourceType.isAnonymousType()) {
/*  414 */         modifiers |= 16;
/*      */ 
/*  416 */         if (this.referenceContext.allocation.type == null)
/*  417 */           modifiers |= 16384;
/*      */       }
/*  419 */       Scope scope = this;
/*      */       do {
/*  421 */         switch (scope.kind) {
/*      */         case 2:
/*  423 */           MethodScope methodScope = (MethodScope)scope;
/*  424 */           if (methodScope.isInsideInitializer()) {
/*  425 */             SourceTypeBinding type = ((TypeDeclaration)methodScope.referenceContext).binding;
/*      */ 
/*  428 */             if (methodScope.initializedField != null)
/*      */             {
/*  430 */               if ((!methodScope.initializedField.isViewedAsDeprecated()) || (sourceType.isDeprecated())) break;
/*  431 */               modifiers |= 2097152;
/*      */             } else {
/*  433 */               if (type.isStrictfp())
/*  434 */                 modifiers |= 2048;
/*  435 */               if ((!type.isViewedAsDeprecated()) || (sourceType.isDeprecated())) break;
/*  436 */               modifiers |= 2097152;
/*      */             }
/*      */           } else {
/*  439 */             MethodBinding method = ((AbstractMethodDeclaration)methodScope.referenceContext).binding;
/*  440 */             if (method == null) break;
/*  441 */             if (method.isStrictfp())
/*  442 */               modifiers |= 2048;
/*  443 */             if ((!method.isViewedAsDeprecated()) || (sourceType.isDeprecated())) break;
/*  444 */             modifiers |= 2097152;
/*      */           }
/*      */ 
/*  447 */           break;
/*      */         case 3:
/*  450 */           if (enclosingType.isStrictfp())
/*  451 */             modifiers |= 2048;
/*  452 */           if ((!enclosingType.isViewedAsDeprecated()) || (sourceType.isDeprecated())) break;
/*  453 */           modifiers |= 2097152;
/*      */         }
/*      */ 
/*  456 */         scope = scope.parent;
/*      */       }
/*  457 */       while (scope != null);
/*      */     }
/*      */ 
/*  461 */     int realModifiers = modifiers & 0xFFFF;
/*      */ 
/*  463 */     if ((realModifiers & 0x200) != 0)
/*      */     {
/*  465 */       if (isMemberType)
/*      */       {
/*  468 */         if ((realModifiers & 0xFFFFD1F0) != 0) {
/*  469 */           if ((realModifiers & 0x2000) != 0)
/*  470 */             problemReporter().illegalModifierForAnnotationMemberType(sourceType);
/*      */           else {
/*  472 */             problemReporter().illegalModifierForMemberInterface(sourceType);
/*      */           }
/*      */ 
/*      */         }
/*      */ 
/*      */       }
/*  482 */       else if ((realModifiers & 0xFFFFD1FE) != 0) {
/*  483 */         if ((realModifiers & 0x2000) != 0)
/*  484 */           problemReporter().illegalModifierForAnnotationType(sourceType);
/*      */         else {
/*  486 */           problemReporter().illegalModifierForInterface(sourceType);
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/*  492 */       if ((sourceType.sourceName == TypeConstants.PACKAGE_INFO_NAME) && (compilerOptions().targetJDK > 3211264L)) {
/*  493 */         modifiers |= 4096;
/*      */       }
/*  495 */       modifiers |= 1024;
/*  496 */     } else if ((realModifiers & 0x4000) != 0)
/*      */     {
/*  498 */       if (isMemberType)
/*      */       {
/*  500 */         if ((realModifiers & 0xFFFFB7F0) != 0) {
/*  501 */           problemReporter().illegalModifierForMemberEnum(sourceType);
/*  502 */           modifiers &= -1025;
/*  503 */           realModifiers &= -1025;
/*      */         }
/*      */ 
/*      */       }
/*  507 */       else if (!sourceType.isLocalType())
/*      */       {
/*  511 */         if ((realModifiers & 0xFFFFB7FE) != 0)
/*  512 */           problemReporter().illegalModifierForEnum(sourceType);
/*      */       }
/*  514 */       if (!sourceType.isAnonymousType())
/*      */       {
/*  517 */         if ((this.referenceContext.bits & 0x800) != 0) {
/*  518 */           modifiers |= 1024;
/*      */         }
/*      */         else
/*      */         {
/*  523 */           TypeDeclaration typeDeclaration = this.referenceContext;
/*  524 */           FieldDeclaration[] fields = typeDeclaration.fields;
/*  525 */           int fieldsLength = fields == null ? 0 : fields.length;
/*  526 */           if (fieldsLength != 0) {
/*  527 */             AbstractMethodDeclaration[] methods = typeDeclaration.methods;
/*  528 */             int methodsLength = methods == null ? 0 : methods.length;
/*      */ 
/*  530 */             boolean definesAbstractMethod = typeDeclaration.superInterfaces != null;
/*  531 */             for (int i = 0; (i < methodsLength) && (!definesAbstractMethod); i++)
/*  532 */               definesAbstractMethod = methods[i].isAbstract();
/*  533 */             if (definesAbstractMethod) {
/*  534 */               boolean needAbstractBit = false;
/*  535 */               int i = 0;
/*      */               while (true) { FieldDeclaration fieldDecl = fields[i];
/*  537 */                 if (fieldDecl.getKind() == 3) {
/*  538 */                   if (!(fieldDecl.initialization instanceof QualifiedAllocationExpression)) break;
/*  539 */                   needAbstractBit = true;
/*      */                 }
/*      */                 else
/*      */                 {
/*  535 */                   i++; if (i < fieldsLength)
/*      */                   {
/*      */                     continue;
/*      */                   }
/*      */ 
/*  547 */                   if (!needAbstractBit) break;
/*  548 */                   modifiers |= 1024;
/*      */                 } }
/*      */             }
/*      */           }
/*      */         }
/*  553 */         TypeDeclaration typeDeclaration = this.referenceContext;
/*  554 */         FieldDeclaration[] fields = typeDeclaration.fields;
/*  555 */         if (fields != null) {
/*  556 */           int i = 0; for (int fieldsLength = fields.length; i < fieldsLength; i++) {
/*  557 */             FieldDeclaration fieldDecl = fields[i];
/*  558 */             if ((fieldDecl.getKind() == 3) && 
/*  559 */               ((fieldDecl.initialization instanceof QualifiedAllocationExpression)))
/*      */             {
/*      */               break;
/*      */             }
/*      */           }
/*      */         }
/*  565 */         modifiers |= 16;
/*      */       }
/*      */     }
/*      */     else
/*      */     {
/*  570 */       if (isMemberType)
/*      */       {
/*  572 */         if ((realModifiers & 0xFFFFF3E0) != 0)
/*  573 */           problemReporter().illegalModifierForMemberClass(sourceType);
/*  574 */       } else if (sourceType.isLocalType())
/*      */       {
/*  576 */         if ((realModifiers & 0xFFFFF3EF) != 0) {
/*  577 */           problemReporter().illegalModifierForLocalClass(sourceType);
/*      */         }
/*      */       }
/*  580 */       else if ((realModifiers & 0xFFFFF3EE) != 0) {
/*  581 */         problemReporter().illegalModifierForClass(sourceType);
/*      */       }
/*      */ 
/*  585 */       if ((realModifiers & 0x410) == 1040) {
/*  586 */         problemReporter().illegalModifierCombinationFinalAbstractForClass(sourceType);
/*      */       }
/*      */     }
/*  589 */     if (isMemberType)
/*      */     {
/*  591 */       if (enclosingType.isInterface()) {
/*  592 */         if ((realModifiers & 0x6) != 0) {
/*  593 */           problemReporter().illegalVisibilityModifierForInterfaceMemberType(sourceType);
/*      */ 
/*  596 */           if ((realModifiers & 0x4) != 0)
/*  597 */             modifiers &= -5;
/*  598 */           if ((realModifiers & 0x2) != 0)
/*  599 */             modifiers &= -3;
/*      */         }
/*      */       } else {
/*  602 */         int accessorBits = realModifiers & 0x7;
/*  603 */         if ((accessorBits & accessorBits - 1) > 1) {
/*  604 */           problemReporter().illegalVisibilityModifierCombinationForMemberType(sourceType);
/*      */ 
/*  607 */           if ((accessorBits & 0x1) != 0) {
/*  608 */             if ((accessorBits & 0x4) != 0)
/*  609 */               modifiers &= -5;
/*  610 */             if ((accessorBits & 0x2) != 0)
/*  611 */               modifiers &= -3;
/*  612 */           } else if (((accessorBits & 0x4) != 0) && ((accessorBits & 0x2) != 0)) {
/*  613 */             modifiers &= -3;
/*      */           }
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/*  619 */       if ((realModifiers & 0x8) == 0) {
/*  620 */         if (enclosingType.isInterface())
/*  621 */           modifiers |= 8;
/*  622 */       } else if (!enclosingType.isStatic())
/*      */       {
/*  624 */         problemReporter().illegalStaticModifierForMemberType(sourceType);
/*      */       }
/*      */     }
/*      */ 
/*  628 */     sourceType.modifiers = modifiers;
/*      */   }
/*      */ 
/*      */   private void checkAndSetModifiersForField(FieldBinding fieldBinding, FieldDeclaration fieldDecl)
/*      */   {
/*  639 */     int modifiers = fieldBinding.modifiers;
/*  640 */     ReferenceBinding declaringClass = fieldBinding.declaringClass;
/*  641 */     if ((modifiers & 0x400000) != 0) {
/*  642 */       problemReporter().duplicateModifierForField(declaringClass, fieldDecl);
/*      */     }
/*  644 */     if (declaringClass.isInterface())
/*      */     {
/*  647 */       modifiers |= 25;
/*      */ 
/*  650 */       if ((modifiers & 0xFFFF) != 25) {
/*  651 */         if ((declaringClass.modifiers & 0x2000) != 0)
/*  652 */           problemReporter().illegalModifierForAnnotationField(fieldDecl);
/*      */         else
/*  654 */           problemReporter().illegalModifierForInterfaceField(fieldDecl);
/*      */       }
/*  656 */       fieldBinding.modifiers = modifiers;
/*  657 */       return;
/*  658 */     }if (fieldDecl.getKind() == 3)
/*      */     {
/*  660 */       if ((modifiers & 0xFFFF) != 0) {
/*  661 */         problemReporter().illegalModifierForEnumConstant(declaringClass, fieldDecl);
/*      */       }
/*      */ 
/*  669 */       fieldBinding.modifiers |= 134234137;
/*  670 */       return;
/*      */     }
/*      */ 
/*  674 */     int realModifiers = modifiers & 0xFFFF;
/*      */ 
/*  676 */     if ((realModifiers & 0xFFFFFF20) != 0) {
/*  677 */       problemReporter().illegalModifierForField(declaringClass, fieldDecl);
/*  678 */       modifiers &= -65313;
/*      */     }
/*      */ 
/*  681 */     int accessorBits = realModifiers & 0x7;
/*  682 */     if ((accessorBits & accessorBits - 1) > 1) {
/*  683 */       problemReporter().illegalVisibilityModifierCombinationForField(declaringClass, fieldDecl);
/*      */ 
/*  686 */       if ((accessorBits & 0x1) != 0) {
/*  687 */         if ((accessorBits & 0x4) != 0)
/*  688 */           modifiers &= -5;
/*  689 */         if ((accessorBits & 0x2) != 0)
/*  690 */           modifiers &= -3;
/*  691 */       } else if (((accessorBits & 0x4) != 0) && ((accessorBits & 0x2) != 0)) {
/*  692 */         modifiers &= -3;
/*      */       }
/*      */     }
/*      */ 
/*  696 */     if ((realModifiers & 0x50) == 80) {
/*  697 */       problemReporter().illegalModifierCombinationFinalVolatileForField(declaringClass, fieldDecl);
/*      */     }
/*  699 */     if ((fieldDecl.initialization == null) && ((modifiers & 0x10) != 0))
/*  700 */       modifiers |= 67108864;
/*  701 */     fieldBinding.modifiers = modifiers;
/*      */   }
/*      */ 
/*      */   public void checkParameterizedSuperTypeCollisions()
/*      */   {
/*  706 */     SourceTypeBinding sourceType = this.referenceContext.binding;
/*  707 */     ReferenceBinding[] interfaces = sourceType.superInterfaces;
/*  708 */     Map invocations = new HashMap(2);
/*  709 */     ReferenceBinding itsSuperclass = sourceType.isInterface() ? null : sourceType.superclass;
/*  710 */     int i = 0; for (int length = interfaces.length; i < length; i++) {
/*  711 */       ReferenceBinding one = interfaces[i];
/*  712 */       if ((one == null) || (
/*  713 */         (itsSuperclass != null) && (hasErasedCandidatesCollisions(itsSuperclass, one, invocations, sourceType, this.referenceContext))))
/*      */         continue;
/*  715 */       for (int j = 0; j < i; j++) {
/*  716 */         ReferenceBinding two = interfaces[j];
/*  717 */         if ((two != null) && 
/*  718 */           (hasErasedCandidatesCollisions(one, two, invocations, sourceType, this.referenceContext))) {
/*      */           break;
/*      */         }
/*      */       }
/*      */     }
/*  723 */     TypeParameter[] typeParameters = this.referenceContext.typeParameters;
/*  724 */     int i = 0; for (int paramLength = typeParameters == null ? 0 : typeParameters.length; i < paramLength; i++) {
/*  725 */       TypeParameter typeParameter = typeParameters[i];
/*  726 */       TypeVariableBinding typeVariable = typeParameter.binding;
/*  727 */       if ((typeVariable == null) || (!typeVariable.isValidBinding()))
/*      */         continue;
/*  729 */       TypeReference[] boundRefs = typeParameter.bounds;
/*  730 */       if (boundRefs != null) {
/*  731 */         boolean checkSuperclass = typeVariable.firstBound == typeVariable.superclass;
/*  732 */         int j = 0; for (int boundLength = boundRefs.length; j < boundLength; j++) {
/*  733 */           TypeReference typeRef = boundRefs[j];
/*  734 */           TypeBinding superType = typeRef.resolvedType;
/*  735 */           if ((superType == null) || (!superType.isValidBinding())) {
/*      */             continue;
/*      */           }
/*  738 */           if ((checkSuperclass) && 
/*  739 */             (hasErasedCandidatesCollisions(superType, typeVariable.superclass, invocations, typeVariable, typeRef))) {
/*      */             break;
/*      */           }
/*  742 */           int index = typeVariable.superInterfaces.length;
/*      */           do { if (hasErasedCandidatesCollisions(superType, typeVariable.superInterfaces[index], invocations, typeVariable, typeRef))
/*      */               break;
/*  742 */             index--; } while (index >= 0);
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  749 */     ReferenceBinding[] memberTypes = this.referenceContext.binding.memberTypes;
/*  750 */     if ((memberTypes != null) && (memberTypes != Binding.NO_MEMBER_TYPES)) {
/*  751 */       int i = 0; for (int size = memberTypes.length; i < size; i++)
/*  752 */         ((SourceTypeBinding)memberTypes[i]).scope.checkParameterizedSuperTypeCollisions();
/*      */     }
/*      */   }
/*      */ 
/*      */   private void checkForInheritedMemberTypes(SourceTypeBinding sourceType)
/*      */   {
/*  759 */     ReferenceBinding currentType = sourceType;
/*  760 */     ReferenceBinding[] interfacesToVisit = (ReferenceBinding[])null;
/*  761 */     int nextPosition = 0;
/*      */     do {
/*  763 */       if (currentType.hasMemberTypes()) {
/*  764 */         return;
/*      */       }
/*  766 */       ReferenceBinding[] itsInterfaces = currentType.superInterfaces();
/*      */ 
/*  768 */       if ((itsInterfaces != null) && (itsInterfaces != Binding.NO_SUPERINTERFACES))
/*  769 */         if (interfacesToVisit == null) {
/*  770 */           interfacesToVisit = itsInterfaces;
/*  771 */           nextPosition = interfacesToVisit.length;
/*      */         } else {
/*  773 */           int itsLength = itsInterfaces.length;
/*  774 */           if (nextPosition + itsLength >= interfacesToVisit.length)
/*  775 */             System.arraycopy(interfacesToVisit, 0, interfacesToVisit = new ReferenceBinding[nextPosition + itsLength + 5], 0, nextPosition);
/*  776 */           for (int a = 0; a < itsLength; a++) {
/*  777 */             ReferenceBinding next = itsInterfaces[a];
/*  778 */             int b = 0;
/*  779 */             while (next != interfacesToVisit[b])
/*      */             {
/*  778 */               b++; if (b < nextPosition)
/*      */                 continue;
/*  780 */               interfacesToVisit[(nextPosition++)] = next;
/*      */             }
/*      */           }
/*      */         }
/*      */     }
/*  784 */     while (((currentType = currentType.superclass()) != null) && ((currentType.tagBits & 0x10000) == 0L));
/*      */ 
/*  786 */     if (interfacesToVisit != null)
/*      */     {
/*  788 */       boolean needToTag = false;
/*  789 */       for (int i = 0; i < nextPosition; i++) {
/*  790 */         ReferenceBinding anInterface = interfacesToVisit[i];
/*  791 */         if ((anInterface.tagBits & 0x10000) == 0L) {
/*  792 */           if (anInterface.hasMemberTypes()) {
/*  793 */             return;
/*      */           }
/*  795 */           needToTag = true;
/*  796 */           ReferenceBinding[] itsInterfaces = anInterface.superInterfaces();
/*  797 */           if ((itsInterfaces != null) && (itsInterfaces != Binding.NO_SUPERINTERFACES)) {
/*  798 */             int itsLength = itsInterfaces.length;
/*  799 */             if (nextPosition + itsLength >= interfacesToVisit.length)
/*  800 */               System.arraycopy(interfacesToVisit, 0, interfacesToVisit = new ReferenceBinding[nextPosition + itsLength + 5], 0, nextPosition);
/*  801 */             for (int a = 0; a < itsLength; a++) {
/*  802 */               ReferenceBinding next = itsInterfaces[a];
/*  803 */               int b = 0;
/*  804 */               while (next != interfacesToVisit[b])
/*      */               {
/*  803 */                 b++; if (b < nextPosition)
/*      */                   continue;
/*  805 */                 interfacesToVisit[(nextPosition++)] = next;
/*      */               }
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/*  811 */       if (needToTag) {
/*  812 */         for (int i = 0; i < nextPosition; i++) {
/*  813 */           interfacesToVisit[i].tagBits |= 65536L;
/*      */         }
/*      */       }
/*      */     }
/*      */ 
/*  818 */     currentType = sourceType;
/*      */     do
/*  820 */       currentType.tagBits |= 65536L;
/*  821 */     while (((currentType = currentType.superclass()) != null) && ((currentType.tagBits & 0x10000) == 0L));
/*      */   }
/*      */ 
/*      */   public void checkParameterizedTypeBounds()
/*      */   {
/*  826 */     int i = 0; for (int l = this.deferredBoundChecks == null ? 0 : this.deferredBoundChecks.size(); i < l; i++)
/*  827 */       ((TypeReference)this.deferredBoundChecks.get(i)).checkBounds(this);
/*  828 */     this.deferredBoundChecks = null;
/*      */ 
/*  830 */     ReferenceBinding[] memberTypes = this.referenceContext.binding.memberTypes;
/*  831 */     if ((memberTypes != null) && (memberTypes != Binding.NO_MEMBER_TYPES)) {
/*  832 */       int i = 0; for (int size = memberTypes.length; i < size; i++)
/*  833 */         ((SourceTypeBinding)memberTypes[i]).scope.checkParameterizedTypeBounds(); 
/*      */     }
/*      */   }
/*      */ 
/*      */   private void connectMemberTypes() {
/*  837 */     SourceTypeBinding sourceType = this.referenceContext.binding;
/*  838 */     ReferenceBinding[] memberTypes = sourceType.memberTypes;
/*  839 */     if ((memberTypes != null) && (memberTypes != Binding.NO_MEMBER_TYPES)) {
/*  840 */       int i = 0; for (int size = memberTypes.length; i < size; i++)
/*  841 */         ((SourceTypeBinding)memberTypes[i]).scope.connectTypeHierarchy();
/*      */     }
/*      */   }
/*      */ 
/*      */   private boolean connectSuperclass()
/*      */   {
/*  856 */     SourceTypeBinding sourceType = this.referenceContext.binding;
/*  857 */     if (sourceType.id == 1) {
/*  858 */       sourceType.superclass = null;
/*  859 */       sourceType.superInterfaces = Binding.NO_SUPERINTERFACES;
/*  860 */       if (!sourceType.isClass())
/*  861 */         problemReporter().objectMustBeClass(sourceType);
/*  862 */       if ((this.referenceContext.superclass != null) || ((this.referenceContext.superInterfaces != null) && (this.referenceContext.superInterfaces.length > 0)))
/*  863 */         problemReporter().objectCannotHaveSuperTypes(sourceType);
/*  864 */       return true;
/*      */     }
/*  866 */     if (this.referenceContext.superclass == null) {
/*  867 */       if ((sourceType.isEnum()) && (compilerOptions().sourceLevel >= 3211264L))
/*  868 */         return connectEnumSuperclass();
/*  869 */       sourceType.superclass = getJavaLangObject();
/*  870 */       return !detectHierarchyCycle(sourceType, sourceType.superclass, null);
/*      */     }
/*  872 */     TypeReference superclassRef = this.referenceContext.superclass;
/*  873 */     ReferenceBinding superclass = findSupertype(superclassRef);
/*  874 */     if (superclass != null) {
/*  875 */       if ((!superclass.isClass()) && ((superclass.tagBits & 0x80) == 0L)) {
/*  876 */         problemReporter().superclassMustBeAClass(sourceType, superclassRef, superclass);
/*  877 */       } else if (superclass.isFinal()) {
/*  878 */         problemReporter().classExtendFinalClass(sourceType, superclassRef, superclass);
/*  879 */       } else if ((superclass.tagBits & 0x40000000) != 0L) {
/*  880 */         problemReporter().superTypeCannotUseWildcard(sourceType, superclassRef, superclass);
/*  881 */       } else if (superclass.erasure().id == 41) {
/*  882 */         problemReporter().cannotExtendEnum(sourceType, superclassRef, superclass); } else {
/*  883 */         if (((superclass.tagBits & 0x20000) != 0L) || 
/*  884 */           (!superclassRef.resolvedType.isValidBinding())) {
/*  885 */           sourceType.superclass = superclass;
/*  886 */           sourceType.tagBits |= 131072L;
/*  887 */           return superclassRef.resolvedType.isValidBinding();
/*      */         }
/*      */ 
/*  890 */         sourceType.superclass = superclass;
/*  891 */         return true;
/*      */       }
/*      */     }
/*  894 */     sourceType.tagBits |= 131072L;
/*  895 */     sourceType.superclass = getJavaLangObject();
/*  896 */     if ((sourceType.superclass.tagBits & 0x100) == 0L)
/*  897 */       detectHierarchyCycle(sourceType, sourceType.superclass, null);
/*  898 */     return false;
/*      */   }
/*      */ 
/*      */   private boolean connectEnumSuperclass()
/*      */   {
/*  905 */     SourceTypeBinding sourceType = this.referenceContext.binding;
/*  906 */     ReferenceBinding rootEnumType = getJavaLangEnum();
/*  907 */     boolean foundCycle = detectHierarchyCycle(sourceType, rootEnumType, null);
/*      */ 
/*  909 */     TypeVariableBinding[] refTypeVariables = rootEnumType.typeVariables();
/*  910 */     if (refTypeVariables == Binding.NO_TYPE_VARIABLES) {
/*  911 */       problemReporter().nonGenericTypeCannotBeParameterized(0, null, rootEnumType, new TypeBinding[] { sourceType });
/*  912 */       return false;
/*  913 */     }if (1 != refTypeVariables.length) {
/*  914 */       problemReporter().incorrectArityForParameterizedType(null, rootEnumType, new TypeBinding[] { sourceType });
/*  915 */       return false;
/*      */     }
/*      */ 
/*  918 */     ParameterizedTypeBinding superType = environment().createParameterizedType(
/*  919 */       rootEnumType, 
/*  920 */       new TypeBinding[] { 
/*  921 */       environment().convertToRawType(sourceType, false) }, 
/*  923 */       null);
/*  924 */     sourceType.tagBits |= superType.tagBits & 0x20000;
/*  925 */     sourceType.superclass = superType;
/*      */ 
/*  927 */     if (refTypeVariables[0].boundCheck(superType, sourceType) != 0) {
/*  928 */       problemReporter().typeMismatchError(rootEnumType, refTypeVariables[0], sourceType, null);
/*      */     }
/*  930 */     return !foundCycle;
/*      */   }
/*      */ 
/*      */   private boolean connectSuperInterfaces()
/*      */   {
/*  944 */     SourceTypeBinding sourceType = this.referenceContext.binding;
/*  945 */     sourceType.superInterfaces = Binding.NO_SUPERINTERFACES;
/*  946 */     if (this.referenceContext.superInterfaces == null) {
/*  947 */       if ((sourceType.isAnnotationType()) && (compilerOptions().sourceLevel >= 3211264L)) {
/*  948 */         ReferenceBinding annotationType = getJavaLangAnnotationAnnotation();
/*  949 */         boolean foundCycle = detectHierarchyCycle(sourceType, annotationType, null);
/*  950 */         sourceType.superInterfaces = new ReferenceBinding[] { annotationType };
/*  951 */         return !foundCycle;
/*      */       }
/*  953 */       return true;
/*      */     }
/*  955 */     if (sourceType.id == 1) {
/*  956 */       return true;
/*      */     }
/*  958 */     boolean noProblems = true;
/*  959 */     int length = this.referenceContext.superInterfaces.length;
/*  960 */     ReferenceBinding[] interfaceBindings = new ReferenceBinding[length];
/*  961 */     int count = 0;
/*  962 */     for (int i = 0; i < length; i++) {
/*  963 */       TypeReference superInterfaceRef = this.referenceContext.superInterfaces[i];
/*  964 */       ReferenceBinding superInterface = findSupertype(superInterfaceRef);
/*  965 */       if (superInterface == null) {
/*  966 */         sourceType.tagBits |= 131072L;
/*  967 */         noProblems = false;
/*      */       }
/*      */       else
/*      */       {
/*  973 */         int j = 0;
/*      */         while (true) if (interfaceBindings[j] == superInterface) {
/*  975 */             problemReporter().duplicateSuperinterface(sourceType, superInterfaceRef, superInterface);
/*  976 */             sourceType.tagBits |= 131072L;
/*  977 */             noProblems = false;
/*      */           }
/*      */           else
/*      */           {
/*  973 */             j++; if (j < i)
/*      */             {
/*      */               continue;
/*      */             }
/*      */ 
/*  981 */             if ((!superInterface.isInterface()) && ((superInterface.tagBits & 0x80) == 0L)) {
/*  982 */               problemReporter().superinterfaceMustBeAnInterface(sourceType, superInterfaceRef, superInterface);
/*  983 */               sourceType.tagBits |= 131072L;
/*  984 */               noProblems = false;
/*      */             } else {
/*  986 */               if (superInterface.isAnnotationType()) {
/*  987 */                 problemReporter().annotationTypeUsedAsSuperinterface(sourceType, superInterfaceRef, superInterface);
/*      */               }
/*  989 */               if ((superInterface.tagBits & 0x40000000) != 0L) {
/*  990 */                 problemReporter().superTypeCannotUseWildcard(sourceType, superInterfaceRef, superInterface);
/*  991 */                 sourceType.tagBits |= 131072L;
/*  992 */                 noProblems = false;
/*      */               }
/*      */               else {
/*  995 */                 if (((superInterface.tagBits & 0x20000) != 0L) || 
/*  996 */                   (!superInterfaceRef.resolvedType.isValidBinding())) {
/*  997 */                   sourceType.tagBits |= 131072L;
/*  998 */                   noProblems &= superInterfaceRef.resolvedType.isValidBinding();
/*      */                 }
/*      */ 
/* 1001 */                 interfaceBindings[(count++)] = superInterface;
/*      */               }
/*      */             }
/*      */           } 
/*      */       }
/*      */     }
/* 1004 */     if (count > 0) {
/* 1005 */       if (count != length)
/* 1006 */         System.arraycopy(interfaceBindings, 0, interfaceBindings = new ReferenceBinding[count], 0, count);
/* 1007 */       sourceType.superInterfaces = interfaceBindings;
/*      */     }
/* 1009 */     return noProblems;
/*      */   }
/*      */ 
/*      */   void connectTypeHierarchy() {
/* 1013 */     SourceTypeBinding sourceType = this.referenceContext.binding;
/* 1014 */     if ((sourceType.tagBits & 0x100) == 0L) {
/* 1015 */       sourceType.tagBits |= 256L;
/* 1016 */       boolean noProblems = connectSuperclass();
/* 1017 */       noProblems &= connectSuperInterfaces();
/* 1018 */       sourceType.tagBits |= 512L;
/* 1019 */       noProblems &= connectTypeVariables(this.referenceContext.typeParameters, false);
/* 1020 */       sourceType.tagBits |= 262144L;
/* 1021 */       if ((noProblems) && (sourceType.isHierarchyInconsistent()))
/* 1022 */         problemReporter().hierarchyHasProblems(sourceType);
/*      */     }
/* 1024 */     connectMemberTypes();
/* 1025 */     LookupEnvironment env = environment();
/*      */     try {
/* 1027 */       env.missingClassFileLocation = this.referenceContext;
/* 1028 */       checkForInheritedMemberTypes(sourceType);
/*      */     } catch (AbortCompilation e) {
/* 1030 */       e.updateContext(this.referenceContext, referenceCompilationUnit().compilationResult);
/* 1031 */       throw e;
/*      */     } finally {
/* 1033 */       env.missingClassFileLocation = null;
/*      */     }
/*      */   }
/*      */ 
/*      */   private void connectTypeHierarchyWithoutMembers()
/*      */   {
/* 1039 */     if ((this.parent instanceof CompilationUnitScope)) {
/* 1040 */       if (((CompilationUnitScope)this.parent).imports == null)
/* 1041 */         ((CompilationUnitScope)this.parent).checkAndSetImports();
/* 1042 */     } else if ((this.parent instanceof ClassScope))
/*      */     {
/* 1044 */       ((ClassScope)this.parent).connectTypeHierarchyWithoutMembers();
/*      */     }
/*      */ 
/* 1048 */     SourceTypeBinding sourceType = this.referenceContext.binding;
/* 1049 */     if ((sourceType.tagBits & 0x100) != 0L) {
/* 1050 */       return;
/*      */     }
/* 1052 */     sourceType.tagBits |= 256L;
/* 1053 */     boolean noProblems = connectSuperclass();
/* 1054 */     noProblems &= connectSuperInterfaces();
/* 1055 */     sourceType.tagBits |= 512L;
/* 1056 */     noProblems &= connectTypeVariables(this.referenceContext.typeParameters, false);
/* 1057 */     sourceType.tagBits |= 262144L;
/* 1058 */     if ((noProblems) && (sourceType.isHierarchyInconsistent()))
/* 1059 */       problemReporter().hierarchyHasProblems(sourceType);
/*      */   }
/*      */ 
/*      */   public boolean detectHierarchyCycle(TypeBinding superType, TypeReference reference) {
/* 1063 */     if (!(superType instanceof ReferenceBinding)) return false;
/*      */ 
/* 1065 */     if (reference == this.superTypeReference) {
/* 1066 */       if (superType.isTypeVariable()) {
/* 1067 */         return false;
/*      */       }
/*      */ 
/* 1070 */       if (superType.isParameterizedType())
/* 1071 */         superType = ((ParameterizedTypeBinding)superType).genericType();
/* 1072 */       compilationUnitScope().recordSuperTypeReference(superType);
/* 1073 */       return detectHierarchyCycle(this.referenceContext.binding, (ReferenceBinding)superType, reference);
/*      */     }
/* 1075 */     return false;
/*      */   }
/*      */ 
/*      */   private boolean detectHierarchyCycle(SourceTypeBinding sourceType, ReferenceBinding superType, TypeReference reference)
/*      */   {
/* 1080 */     if (superType.isRawType()) {
/* 1081 */       superType = ((RawTypeBinding)superType).genericType();
/*      */     }
/*      */ 
/* 1084 */     if (sourceType == superType) {
/* 1085 */       problemReporter().hierarchyCircularity(sourceType, superType, reference);
/* 1086 */       sourceType.tagBits |= 131072L;
/* 1087 */       return true;
/*      */     }
/*      */ 
/* 1090 */     if (superType.isMemberType()) {
/* 1091 */       ReferenceBinding current = superType.enclosingType();
/*      */       do
/* 1093 */         if ((current.isHierarchyBeingConnected()) && (current == sourceType)) {
/* 1094 */           problemReporter().hierarchyCircularity(sourceType, current, reference);
/* 1095 */           sourceType.tagBits |= 131072L;
/* 1096 */           current.tagBits |= 131072L;
/* 1097 */           return true;
/*      */         }
/* 1099 */       while ((current = current.enclosingType()) != null);
/*      */     }
/*      */ 
/* 1102 */     if (superType.isBinaryBinding())
/*      */     {
/* 1106 */       boolean hasCycle = false;
/* 1107 */       ReferenceBinding parentType = superType.superclass();
/* 1108 */       if (parentType != null) {
/* 1109 */         if (sourceType == parentType) {
/* 1110 */           problemReporter().hierarchyCircularity(sourceType, superType, reference);
/* 1111 */           sourceType.tagBits |= 131072L;
/* 1112 */           superType.tagBits |= 131072L;
/* 1113 */           return true;
/*      */         }
/* 1115 */         if (parentType.isParameterizedType())
/* 1116 */           parentType = ((ParameterizedTypeBinding)parentType).genericType();
/* 1117 */         hasCycle |= detectHierarchyCycle(sourceType, parentType, reference);
/* 1118 */         if ((parentType.tagBits & 0x20000) != 0L) {
/* 1119 */           sourceType.tagBits |= 131072L;
/* 1120 */           parentType.tagBits |= 131072L;
/*      */         }
/*      */       }
/*      */ 
/* 1124 */       ReferenceBinding[] itsInterfaces = superType.superInterfaces();
/* 1125 */       if ((itsInterfaces != null) && (itsInterfaces != Binding.NO_SUPERINTERFACES)) {
/* 1126 */         int i = 0; for (int length = itsInterfaces.length; i < length; i++) {
/* 1127 */           ReferenceBinding anInterface = itsInterfaces[i];
/* 1128 */           if (sourceType == anInterface) {
/* 1129 */             problemReporter().hierarchyCircularity(sourceType, superType, reference);
/* 1130 */             sourceType.tagBits |= 131072L;
/* 1131 */             superType.tagBits |= 131072L;
/* 1132 */             return true;
/*      */           }
/* 1134 */           if (anInterface.isParameterizedType())
/* 1135 */             anInterface = ((ParameterizedTypeBinding)anInterface).genericType();
/* 1136 */           hasCycle |= detectHierarchyCycle(sourceType, anInterface, reference);
/* 1137 */           if ((anInterface.tagBits & 0x20000) != 0L) {
/* 1138 */             sourceType.tagBits |= 131072L;
/* 1139 */             superType.tagBits |= 131072L;
/*      */           }
/*      */         }
/*      */       }
/* 1143 */       return hasCycle;
/*      */     }
/*      */ 
/* 1146 */     if (superType.isHierarchyBeingConnected()) {
/* 1147 */       TypeReference ref = ((SourceTypeBinding)superType).scope.superTypeReference;
/*      */ 
/* 1150 */       if ((ref != null) && ((ref.resolvedType == null) || (((ReferenceBinding)ref.resolvedType).isHierarchyBeingConnected()))) {
/* 1151 */         problemReporter().hierarchyCircularity(sourceType, superType, reference);
/* 1152 */         sourceType.tagBits |= 131072L;
/* 1153 */         superType.tagBits |= 131072L;
/* 1154 */         return true;
/*      */       }
/*      */     }
/* 1157 */     if ((superType.tagBits & 0x100) == 0L)
/*      */     {
/* 1159 */       ((SourceTypeBinding)superType).scope.connectTypeHierarchyWithoutMembers();
/* 1160 */     }if ((superType.tagBits & 0x20000) != 0L)
/* 1161 */       sourceType.tagBits |= 131072L;
/* 1162 */     return false;
/*      */   }
/*      */ 
/*      */   private ReferenceBinding findSupertype(TypeReference typeReference) {
/* 1166 */     CompilationUnitScope unitScope = compilationUnitScope();
/* 1167 */     LookupEnvironment env = unitScope.environment;
/*      */     try {
/* 1169 */       env.missingClassFileLocation = typeReference;
/* 1170 */       typeReference.aboutToResolve(this);
/* 1171 */       unitScope.recordQualifiedReference(typeReference.getTypeName());
/* 1172 */       this.superTypeReference = typeReference;
/* 1173 */       ReferenceBinding superType = (ReferenceBinding)typeReference.resolveSuperType(this);
/* 1174 */       ReferenceBinding localReferenceBinding1 = superType;
/*      */       return localReferenceBinding1;
/*      */     } catch (AbortCompilation e) {
/* 1176 */       SourceTypeBinding sourceType = this.referenceContext.binding;
/* 1177 */       if (sourceType.superInterfaces == null) sourceType.superInterfaces = Binding.NO_SUPERINTERFACES;
/* 1178 */       e.updateContext(typeReference, referenceCompilationUnit().compilationResult);
/* 1179 */       throw e;
/*      */     } finally {
/* 1181 */       env.missingClassFileLocation = null;
/* 1182 */       this.superTypeReference = null;
/* 1183 */     }throw localObject;
/*      */   }
/*      */ 
/*      */   public ProblemReporter problemReporter()
/*      */   {
/*      */     MethodScope outerMethodScope;
/* 1194 */     if ((outerMethodScope = outerMostMethodScope()) == null) {
/* 1195 */       ProblemReporter problemReporter = referenceCompilationUnit().problemReporter;
/* 1196 */       problemReporter.referenceContext = this.referenceContext;
/* 1197 */       return problemReporter;
/*      */     }
/* 1199 */     return outerMethodScope.problemReporter();
/*      */   }
/*      */ 
/*      */   public TypeDeclaration referenceType()
/*      */   {
/* 1206 */     return this.referenceContext;
/*      */   }
/*      */ 
/*      */   public String toString() {
/* 1210 */     if (this.referenceContext != null)
/* 1211 */       return "--- Class Scope ---\n\n" + 
/* 1212 */         this.referenceContext.binding.toString();
/* 1213 */     return "--- Class Scope ---\n\n Binding not initialized";
/*      */   }
/*      */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\ecj-3.5.2.jar
 * Qualified Name:     org.eclipse.jdt.internal.compiler.lookup.ClassScope
 * JD-Core Version:    0.6.0
 */