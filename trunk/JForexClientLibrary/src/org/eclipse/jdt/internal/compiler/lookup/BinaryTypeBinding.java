/*      */ package org.eclipse.jdt.internal.compiler.lookup;
/*      */ 
/*      */ import java.util.ArrayList;
/*      */ import org.eclipse.jdt.core.compiler.CharOperation;
/*      */ import org.eclipse.jdt.internal.compiler.env.ClassSignature;
/*      */ import org.eclipse.jdt.internal.compiler.env.EnumConstantSignature;
/*      */ import org.eclipse.jdt.internal.compiler.env.IBinaryAnnotation;
/*      */ import org.eclipse.jdt.internal.compiler.env.IBinaryElementValuePair;
/*      */ import org.eclipse.jdt.internal.compiler.env.IBinaryField;
/*      */ import org.eclipse.jdt.internal.compiler.env.IBinaryMethod;
/*      */ import org.eclipse.jdt.internal.compiler.env.IBinaryNestedType;
/*      */ import org.eclipse.jdt.internal.compiler.env.IBinaryType;
/*      */ import org.eclipse.jdt.internal.compiler.impl.CompilerOptions;
/*      */ import org.eclipse.jdt.internal.compiler.impl.Constant;
/*      */ import org.eclipse.jdt.internal.compiler.problem.AbortCompilation;
/*      */ import org.eclipse.jdt.internal.compiler.util.SimpleLookupTable;
/*      */ 
/*      */ public class BinaryTypeBinding extends ReferenceBinding
/*      */ {
/*      */   protected ReferenceBinding superclass;
/*      */   protected ReferenceBinding enclosingType;
/*      */   protected ReferenceBinding[] superInterfaces;
/*      */   protected FieldBinding[] fields;
/*      */   protected MethodBinding[] methods;
/*      */   protected ReferenceBinding[] memberTypes;
/*      */   protected TypeVariableBinding[] typeVariables;
/*      */   protected LookupEnvironment environment;
/*   47 */   protected SimpleLookupTable storedAnnotations = null;
/*      */ 
/*      */   static Object convertMemberValue(Object binaryValue, LookupEnvironment env, char[][][] missingTypeNames) {
/*   50 */     if (binaryValue == null) return null;
/*   51 */     if ((binaryValue instanceof Constant))
/*   52 */       return binaryValue;
/*   53 */     if ((binaryValue instanceof ClassSignature))
/*   54 */       return env.getTypeFromSignature(((ClassSignature)binaryValue).getTypeName(), 0, -1, false, null, missingTypeNames);
/*   55 */     if ((binaryValue instanceof IBinaryAnnotation))
/*   56 */       return createAnnotation((IBinaryAnnotation)binaryValue, env, missingTypeNames);
/*   57 */     if ((binaryValue instanceof EnumConstantSignature)) {
/*   58 */       EnumConstantSignature ref = (EnumConstantSignature)binaryValue;
/*   59 */       ReferenceBinding enumType = (ReferenceBinding)env.getTypeFromSignature(ref.getTypeName(), 0, -1, false, null, missingTypeNames);
/*   60 */       enumType = (ReferenceBinding)resolveType(enumType, env, false);
/*   61 */       return enumType.getField(ref.getEnumConstantName(), false);
/*      */     }
/*   63 */     if ((binaryValue instanceof Object[])) {
/*   64 */       Object[] objects = (Object[])binaryValue;
/*   65 */       int length = objects.length;
/*   66 */       if (length == 0) return objects;
/*   67 */       Object[] values = new Object[length];
/*   68 */       for (int i = 0; i < length; i++)
/*   69 */         values[i] = convertMemberValue(objects[i], env, missingTypeNames);
/*   70 */       return values;
/*      */     }
/*      */ 
/*   74 */     throw new IllegalStateException();
/*      */   }
/*      */ 
/*      */   static AnnotationBinding createAnnotation(IBinaryAnnotation annotationInfo, LookupEnvironment env, char[][][] missingTypeNames) {
/*   78 */     IBinaryElementValuePair[] binaryPairs = annotationInfo.getElementValuePairs();
/*   79 */     int length = binaryPairs == null ? 0 : binaryPairs.length;
/*   80 */     ElementValuePair[] pairs = length == 0 ? Binding.NO_ELEMENT_VALUE_PAIRS : new ElementValuePair[length];
/*   81 */     for (int i = 0; i < length; i++) {
/*   82 */       pairs[i] = new ElementValuePair(binaryPairs[i].getName(), convertMemberValue(binaryPairs[i].getValue(), env, missingTypeNames), null);
/*      */     }
/*   84 */     char[] typeName = annotationInfo.getTypeName();
/*   85 */     ReferenceBinding annotationType = env.getTypeFromConstantPoolName(typeName, 1, typeName.length - 1, false, missingTypeNames);
/*   86 */     return new UnresolvedAnnotationBinding(annotationType, pairs, env);
/*      */   }
/*      */ 
/*      */   public static AnnotationBinding[] createAnnotations(IBinaryAnnotation[] annotationInfos, LookupEnvironment env, char[][][] missingTypeNames) {
/*   90 */     int length = annotationInfos == null ? 0 : annotationInfos.length;
/*   91 */     AnnotationBinding[] result = length == 0 ? Binding.NO_ANNOTATIONS : new AnnotationBinding[length];
/*   92 */     for (int i = 0; i < length; i++)
/*   93 */       result[i] = createAnnotation(annotationInfos[i], env, missingTypeNames);
/*   94 */     return result;
/*      */   }
/*      */ 
/*      */   public static TypeBinding resolveType(TypeBinding type, LookupEnvironment environment, boolean convertGenericToRawType) {
/*   98 */     switch (type.kind()) {
/*      */     case 260:
/*  100 */       ((ParameterizedTypeBinding)type).resolve();
/*  101 */       break;
/*      */     case 516:
/*      */     case 8196:
/*  105 */       return ((WildcardBinding)type).resolve();
/*      */     case 68:
/*  108 */       resolveType(((ArrayBinding)type).leafComponentType, environment, convertGenericToRawType);
/*  109 */       break;
/*      */     case 4100:
/*  112 */       ((TypeVariableBinding)type).resolve();
/*  113 */       break;
/*      */     case 2052:
/*  116 */       if (!convertGenericToRawType) break;
/*  117 */       return environment.convertUnresolvedBinaryToRawType(type);
/*      */     default:
/*  121 */       if ((type instanceof UnresolvedReferenceBinding))
/*  122 */         return ((UnresolvedReferenceBinding)type).resolve(environment, convertGenericToRawType);
/*  123 */       if (!convertGenericToRawType) break;
/*  124 */       return environment.convertUnresolvedBinaryToRawType(type);
/*      */     }
/*      */ 
/*  127 */     return type;
/*      */   }
/*      */ 
/*      */   protected BinaryTypeBinding()
/*      */   {
/*      */   }
/*      */ 
/*      */   public BinaryTypeBinding(PackageBinding packageBinding, IBinaryType binaryType, LookupEnvironment environment)
/*      */   {
/*  144 */     this.compoundName = CharOperation.splitOn('/', binaryType.getName());
/*  145 */     computeId();
/*      */ 
/*  147 */     this.tagBits |= 64L;
/*  148 */     this.environment = environment;
/*  149 */     this.fPackage = packageBinding;
/*  150 */     this.fileName = binaryType.getFileName();
/*      */ 
/*  152 */     char[] typeSignature = environment.globalOptions.sourceLevel >= 3211264L ? binaryType.getGenericSignature() : null;
/*  153 */     this.typeVariables = ((typeSignature != null) && (typeSignature.length > 0) && (typeSignature[0] == '<') ? 
/*  154 */       null : 
/*  155 */       Binding.NO_TYPE_VARIABLES);
/*      */ 
/*  157 */     this.sourceName = binaryType.getSourceName();
/*  158 */     this.modifiers = binaryType.getModifiers();
/*      */ 
/*  160 */     if ((binaryType.getTagBits() & 0x20000) != 0L) {
/*  161 */       this.tagBits |= 131072L;
/*      */     }
/*  163 */     if (binaryType.isAnonymous())
/*  164 */       this.tagBits |= 2100L;
/*  165 */     else if (binaryType.isLocal())
/*  166 */       this.tagBits |= 2068L;
/*  167 */     else if (binaryType.isMember()) {
/*  168 */       this.tagBits |= 2060L;
/*      */     }
/*      */ 
/*  171 */     char[] enclosingTypeName = binaryType.getEnclosingTypeName();
/*  172 */     if (enclosingTypeName != null)
/*      */     {
/*  174 */       this.enclosingType = environment.getTypeFromConstantPoolName(enclosingTypeName, 0, -1, true, null);
/*  175 */       this.tagBits |= 2060L;
/*  176 */       this.tagBits |= 134217728L;
/*  177 */       if (enclosingType().isStrictfp())
/*  178 */         this.modifiers |= 2048;
/*  179 */       if (enclosingType().isDeprecated())
/*  180 */         this.modifiers |= 2097152;
/*      */     }
/*      */   }
/*      */ 
/*      */   public FieldBinding[] availableFields()
/*      */   {
/*  188 */     if ((this.tagBits & 0x2000) != 0L) {
/*  189 */       return this.fields;
/*      */     }
/*      */ 
/*  192 */     if ((this.tagBits & 0x1000) == 0L) {
/*  193 */       int length = this.fields.length;
/*  194 */       if (length > 1)
/*  195 */         ReferenceBinding.sortFields(this.fields, 0, length);
/*  196 */       this.tagBits |= 4096L;
/*      */     }
/*  198 */     FieldBinding[] availableFields = new FieldBinding[this.fields.length];
/*  199 */     int count = 0;
/*  200 */     for (int i = 0; i < this.fields.length; i++)
/*      */       try {
/*  202 */         availableFields[count] = resolveTypeFor(this.fields[i]);
/*  203 */         count++;
/*      */       }
/*      */       catch (AbortCompilation localAbortCompilation)
/*      */       {
/*      */       }
/*  208 */     if (count < availableFields.length)
/*  209 */       System.arraycopy(availableFields, 0, availableFields = new FieldBinding[count], 0, count);
/*  210 */     return availableFields;
/*      */   }
/*      */ 
/*      */   public MethodBinding[] availableMethods()
/*      */   {
/*  217 */     if ((this.tagBits & 0x8000) != 0L) {
/*  218 */       return this.methods;
/*      */     }
/*      */ 
/*  221 */     if ((this.tagBits & 0x4000) == 0L) {
/*  222 */       int length = this.methods.length;
/*  223 */       if (length > 1)
/*  224 */         ReferenceBinding.sortMethods(this.methods, 0, length);
/*  225 */       this.tagBits |= 16384L;
/*      */     }
/*  227 */     MethodBinding[] availableMethods = new MethodBinding[this.methods.length];
/*  228 */     int count = 0;
/*  229 */     for (int i = 0; i < this.methods.length; i++)
/*      */       try {
/*  231 */         availableMethods[count] = resolveTypesFor(this.methods[i]);
/*  232 */         count++;
/*      */       }
/*      */       catch (AbortCompilation localAbortCompilation)
/*      */       {
/*      */       }
/*  237 */     if (count < availableMethods.length)
/*  238 */       System.arraycopy(availableMethods, 0, availableMethods = new MethodBinding[count], 0, count);
/*  239 */     return availableMethods;
/*      */   }
/*      */ 
/*      */   void cachePartsFrom(IBinaryType binaryType, boolean needFieldsAndMethods)
/*      */   {
/*      */     try
/*      */     {
/*  246 */       this.typeVariables = Binding.NO_TYPE_VARIABLES;
/*  247 */       this.superInterfaces = Binding.NO_SUPERINTERFACES;
/*      */ 
/*  250 */       this.memberTypes = Binding.NO_MEMBER_TYPES;
/*  251 */       IBinaryNestedType[] memberTypeStructures = binaryType.getMemberTypes();
/*  252 */       if (memberTypeStructures != null) {
/*  253 */         int size = memberTypeStructures.length;
/*  254 */         if (size > 0) {
/*  255 */           this.memberTypes = new ReferenceBinding[size];
/*  256 */           for (int i = 0; i < size; i++)
/*      */           {
/*  258 */             this.memberTypes[i] = this.environment.getTypeFromConstantPoolName(memberTypeStructures[i].getName(), 0, -1, false, null);
/*  259 */           }this.tagBits |= 268435456L;
/*      */         }
/*      */       }
/*      */ 
/*  263 */       long sourceLevel = this.environment.globalOptions.sourceLevel;
/*  264 */       char[] typeSignature = (char[])null;
/*  265 */       if (sourceLevel >= 3211264L) {
/*  266 */         typeSignature = binaryType.getGenericSignature();
/*  267 */         this.tagBits |= binaryType.getTagBits();
/*      */       }
/*  269 */       char[][][] missingTypeNames = binaryType.getMissingTypeNames();
/*  270 */       if (typeSignature == null) {
/*  271 */         char[] superclassName = binaryType.getSuperclassName();
/*  272 */         if (superclassName != null)
/*      */         {
/*  274 */           this.superclass = this.environment.getTypeFromConstantPoolName(superclassName, 0, -1, false, missingTypeNames);
/*  275 */           this.tagBits |= 33554432L;
/*      */         }
/*      */ 
/*  278 */         this.superInterfaces = Binding.NO_SUPERINTERFACES;
/*  279 */         char[][] interfaceNames = binaryType.getInterfaceNames();
/*  280 */         if (interfaceNames != null) {
/*  281 */           int size = interfaceNames.length;
/*  282 */           if (size > 0) {
/*  283 */             this.superInterfaces = new ReferenceBinding[size];
/*  284 */             for (int i = 0; i < size; i++)
/*      */             {
/*  286 */               this.superInterfaces[i] = this.environment.getTypeFromConstantPoolName(interfaceNames[i], 0, -1, false, missingTypeNames);
/*  287 */             }this.tagBits |= 67108864L;
/*      */           }
/*      */         }
/*      */       }
/*      */       else {
/*  292 */         SignatureWrapper wrapper = new SignatureWrapper(typeSignature);
/*  293 */         if (wrapper.signature[wrapper.start] == '<')
/*      */         {
/*  295 */           wrapper.start += 1;
/*  296 */           this.typeVariables = createTypeVariables(wrapper, true, missingTypeNames);
/*  297 */           wrapper.start += 1;
/*  298 */           this.tagBits |= 16777216L;
/*  299 */           this.modifiers |= 1073741824;
/*      */         }
/*  301 */         TypeVariableBinding[] typeVars = Binding.NO_TYPE_VARIABLES;
/*  302 */         char[] methodDescriptor = binaryType.getEnclosingMethod();
/*  303 */         if (methodDescriptor != null) {
/*  304 */           MethodBinding enclosingMethod = findMethod(methodDescriptor, missingTypeNames);
/*  305 */           typeVars = enclosingMethod.typeVariables;
/*      */         }
/*      */ 
/*  309 */         this.superclass = ((ReferenceBinding)this.environment.getTypeFromTypeSignature(wrapper, typeVars, this, missingTypeNames));
/*  310 */         this.tagBits |= 33554432L;
/*      */ 
/*  312 */         this.superInterfaces = Binding.NO_SUPERINTERFACES;
/*  313 */         if (!wrapper.atEnd())
/*      */         {
/*  315 */           ArrayList types = new ArrayList(2);
/*      */           do
/*  317 */             types.add(this.environment.getTypeFromTypeSignature(wrapper, typeVars, this, missingTypeNames));
/*  316 */           while (!
/*  318 */             wrapper.atEnd());
/*  319 */           this.superInterfaces = new ReferenceBinding[types.size()];
/*  320 */           types.toArray(this.superInterfaces);
/*  321 */           this.tagBits |= 67108864L;
/*      */         }
/*      */       }
/*      */ 
/*  325 */       if (needFieldsAndMethods) {
/*  326 */         createFields(binaryType.getFields(), sourceLevel, missingTypeNames);
/*  327 */         createMethods(binaryType.getMethods(), sourceLevel, missingTypeNames);
/*  328 */         boolean isViewedAsDeprecated = isViewedAsDeprecated();
/*  329 */         if (isViewedAsDeprecated) {
/*  330 */           int i = 0; for (int max = this.fields.length; i < max; i++) {
/*  331 */             FieldBinding field = this.fields[i];
/*  332 */             if (!field.isDeprecated()) {
/*  333 */               field.modifiers |= 2097152;
/*      */             }
/*      */           }
/*  336 */           int i = 0; for (int max = this.methods.length; i < max; i++) {
/*  337 */             MethodBinding method = this.methods[i];
/*  338 */             if (!method.isDeprecated()) {
/*  339 */               method.modifiers |= 2097152;
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/*  344 */       if (this.environment.globalOptions.storeAnnotations)
/*  345 */         setAnnotations(createAnnotations(binaryType.getAnnotations(), this.environment, missingTypeNames));
/*      */     }
/*      */     finally {
/*  348 */       if (this.fields == null)
/*  349 */         this.fields = Binding.NO_FIELDS;
/*  350 */       if (this.methods == null)
/*  351 */         this.methods = Binding.NO_METHODS;
/*      */     }
/*      */   }
/*      */ 
/*      */   private void createFields(IBinaryField[] iFields, long sourceLevel, char[][][] missingTypeNames) {
/*  356 */     this.fields = Binding.NO_FIELDS;
/*  357 */     if (iFields != null) {
/*  358 */       int size = iFields.length;
/*  359 */       if (size > 0) {
/*  360 */         this.fields = new FieldBinding[size];
/*  361 */         boolean use15specifics = sourceLevel >= 3211264L;
/*  362 */         boolean hasRestrictedAccess = hasRestrictedAccess();
/*  363 */         int firstAnnotatedFieldIndex = -1;
/*  364 */         for (int i = 0; i < size; i++) {
/*  365 */           IBinaryField binaryField = iFields[i];
/*  366 */           char[] fieldSignature = use15specifics ? binaryField.getGenericSignature() : null;
/*  367 */           TypeBinding type = fieldSignature == null ? 
/*  368 */             this.environment.getTypeFromSignature(binaryField.getTypeName(), 0, -1, false, this, missingTypeNames) : 
/*  369 */             this.environment.getTypeFromTypeSignature(new SignatureWrapper(fieldSignature), Binding.NO_TYPE_VARIABLES, this, missingTypeNames);
/*  370 */           FieldBinding field = 
/*  371 */             new FieldBinding(
/*  372 */             binaryField.getName(), 
/*  373 */             type, 
/*  374 */             binaryField.getModifiers() | 0x2000000, 
/*  375 */             this, 
/*  376 */             binaryField.getConstant());
/*  377 */           if ((firstAnnotatedFieldIndex < 0) && 
/*  378 */             (this.environment.globalOptions.storeAnnotations) && 
/*  379 */             (binaryField.getAnnotations() != null)) {
/*  380 */             firstAnnotatedFieldIndex = i;
/*      */           }
/*  382 */           field.id = i;
/*  383 */           if (use15specifics)
/*  384 */             field.tagBits |= binaryField.getTagBits();
/*  385 */           if (hasRestrictedAccess)
/*  386 */             field.modifiers |= 262144;
/*  387 */           if (fieldSignature != null)
/*  388 */             field.modifiers |= 1073741824;
/*  389 */           this.fields[i] = field;
/*      */         }
/*      */ 
/*  392 */         if (firstAnnotatedFieldIndex >= 0)
/*  393 */           for (int i = firstAnnotatedFieldIndex; i < size; i++) {
/*  394 */             IBinaryField binaryField = iFields[i];
/*  395 */             this.fields[i].setAnnotations(createAnnotations(binaryField.getAnnotations(), this.environment, missingTypeNames));
/*      */           }
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   private MethodBinding createMethod(IBinaryMethod method, long sourceLevel, char[][][] missingTypeNames)
/*      */   {
/*  403 */     int methodModifiers = method.getModifiers() | 0x2000000;
/*  404 */     if (sourceLevel < 3211264L)
/*  405 */       methodModifiers &= -129;
/*  406 */     ReferenceBinding[] exceptions = Binding.NO_EXCEPTIONS;
/*  407 */     TypeBinding[] parameters = Binding.NO_PARAMETERS;
/*  408 */     TypeVariableBinding[] typeVars = Binding.NO_TYPE_VARIABLES;
/*  409 */     AnnotationBinding[][] paramAnnotations = (AnnotationBinding[][])null;
/*  410 */     TypeBinding returnType = null;
/*      */ 
/*  412 */     boolean use15specifics = sourceLevel >= 3211264L;
/*  413 */     char[] methodSignature = use15specifics ? method.getGenericSignature() : null;
/*  414 */     if (methodSignature == null) {
/*  415 */       char[] methodDescriptor = method.getMethodDescriptor();
/*  416 */       int numOfParams = 0;
/*      */ 
/*  418 */       int index = 0;
/*      */       char nextChar;
/*      */       do
/*      */       {
/*      */         char nextChar;
/*  420 */         if (nextChar != '[') {
/*  421 */           numOfParams++;
/*  422 */           if (nextChar == 'L')
/*      */             do index++; while ((nextChar = methodDescriptor[index]) != ';');
/*      */         }
/*  419 */         index++; } while ((nextChar = methodDescriptor[index]) != ')');
/*      */ 
/*  428 */       int startIndex = 0;
/*  429 */       if (method.isConstructor()) {
/*  430 */         if ((isMemberType()) && (!isStatic()))
/*      */         {
/*  432 */           startIndex++;
/*      */         }
/*  434 */         if (isEnum())
/*      */         {
/*  436 */           startIndex += 2;
/*      */         }
/*      */       }
/*  439 */       int size = numOfParams - startIndex;
/*  440 */       if (size > 0) {
/*  441 */         parameters = new TypeBinding[size];
/*  442 */         if (this.environment.globalOptions.storeAnnotations)
/*  443 */           paramAnnotations = new AnnotationBinding[size][];
/*  444 */         index = 1;
/*  445 */         int end = 0;
/*  446 */         for (int i = 0; i < numOfParams; i++) {
/*      */           do end++; while ((nextChar = methodDescriptor[end]) == '[');
/*  448 */           if (nextChar == 'L') {
/*      */             do end++; while ((nextChar = methodDescriptor[end]) != ';');
/*      */           }
/*  451 */           if (i >= startIndex) {
/*  452 */             parameters[(i - startIndex)] = this.environment.getTypeFromSignature(methodDescriptor, index, end, false, this, missingTypeNames);
/*      */ 
/*  455 */             if (paramAnnotations != null)
/*  456 */               paramAnnotations[(i - startIndex)] = createAnnotations(method.getParameterAnnotations(i - startIndex), this.environment, missingTypeNames);
/*      */           }
/*  458 */           index = end + 1;
/*      */         }
/*      */       }
/*      */ 
/*  462 */       char[][] exceptionTypes = method.getExceptionTypeNames();
/*  463 */       if (exceptionTypes != null) {
/*  464 */         size = exceptionTypes.length;
/*  465 */         if (size > 0) {
/*  466 */           exceptions = new ReferenceBinding[size];
/*  467 */           for (int i = 0; i < size; i++) {
/*  468 */             exceptions[i] = this.environment.getTypeFromConstantPoolName(exceptionTypes[i], 0, -1, false, missingTypeNames);
/*      */           }
/*      */         }
/*      */       }
/*  472 */       if (!method.isConstructor())
/*  473 */         returnType = this.environment.getTypeFromSignature(methodDescriptor, index + 1, -1, false, this, missingTypeNames);
/*      */     } else {
/*  475 */       methodModifiers |= 1073741824;
/*      */ 
/*  477 */       SignatureWrapper wrapper = new SignatureWrapper(methodSignature);
/*  478 */       if (wrapper.signature[wrapper.start] == '<')
/*      */       {
/*  481 */         wrapper.start += 1;
/*  482 */         typeVars = createTypeVariables(wrapper, false, missingTypeNames);
/*  483 */         wrapper.start += 1;
/*      */       }
/*      */ 
/*  486 */       if (wrapper.signature[wrapper.start] == '(') {
/*  487 */         wrapper.start += 1;
/*  488 */         if (wrapper.signature[wrapper.start] == ')') {
/*  489 */           wrapper.start += 1;
/*      */         } else {
/*  491 */           ArrayList types = new ArrayList(2);
/*  492 */           while (wrapper.signature[wrapper.start] != ')')
/*  493 */             types.add(this.environment.getTypeFromTypeSignature(wrapper, typeVars, this, missingTypeNames));
/*  494 */           wrapper.start += 1;
/*  495 */           int numParam = types.size();
/*  496 */           parameters = new TypeBinding[numParam];
/*  497 */           types.toArray(parameters);
/*  498 */           if (this.environment.globalOptions.storeAnnotations) {
/*  499 */             paramAnnotations = new AnnotationBinding[numParam][];
/*  500 */             for (int i = 0; i < numParam; i++) {
/*  501 */               paramAnnotations[i] = createAnnotations(method.getParameterAnnotations(i), this.environment, missingTypeNames);
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/*      */ 
/*  507 */       returnType = this.environment.getTypeFromTypeSignature(wrapper, typeVars, this, missingTypeNames);
/*      */ 
/*  509 */       if ((!wrapper.atEnd()) && (wrapper.signature[wrapper.start] == '^'))
/*      */       {
/*  511 */         ArrayList types = new ArrayList(2);
/*      */         do {
/*  513 */           wrapper.start += 1;
/*  514 */           types.add(this.environment.getTypeFromTypeSignature(wrapper, typeVars, this, missingTypeNames));
/*  515 */         }while ((!wrapper.atEnd()) && (wrapper.signature[wrapper.start] == '^'));
/*  516 */         exceptions = new ReferenceBinding[types.size()];
/*  517 */         types.toArray(exceptions);
/*      */       } else {
/*  519 */         char[][] exceptionTypes = method.getExceptionTypeNames();
/*  520 */         if (exceptionTypes != null) {
/*  521 */           int size = exceptionTypes.length;
/*  522 */           if (size > 0) {
/*  523 */             exceptions = new ReferenceBinding[size];
/*  524 */             for (int i = 0; i < size; i++) {
/*  525 */               exceptions[i] = this.environment.getTypeFromConstantPoolName(exceptionTypes[i], 0, -1, false, missingTypeNames);
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*  531 */     MethodBinding result = method.isConstructor() ? 
/*  532 */       new MethodBinding(methodModifiers, parameters, exceptions, this) : 
/*  533 */       new MethodBinding(methodModifiers, method.getSelector(), returnType, parameters, exceptions, this);
/*  534 */     if (this.environment.globalOptions.storeAnnotations) {
/*  535 */       result.setAnnotations(
/*  536 */         createAnnotations(method.getAnnotations(), this.environment, missingTypeNames), 
/*  537 */         paramAnnotations, 
/*  538 */         isAnnotationType() ? convertMemberValue(method.getDefaultValue(), this.environment, missingTypeNames) : null, 
/*  539 */         this.environment);
/*      */     }
/*  541 */     if (use15specifics)
/*  542 */       result.tagBits |= method.getTagBits();
/*  543 */     result.typeVariables = typeVars;
/*      */ 
/*  545 */     int i = 0; for (int length = typeVars.length; i < length; i++)
/*  546 */       typeVars[i].declaringElement = result;
/*  547 */     return result;
/*      */   }
/*      */ 
/*      */   private void createMethods(IBinaryMethod[] iMethods, long sourceLevel, char[][][] missingTypeNames)
/*      */   {
/*  554 */     int total = 0; int initialTotal = 0; int iClinit = -1;
/*  555 */     int[] toSkip = (int[])null;
/*  556 */     if (iMethods != null) {
/*  557 */       total = initialTotal = iMethods.length;
/*  558 */       boolean keepBridgeMethods = (sourceLevel < 3211264L) && 
/*  559 */         (this.environment.globalOptions.complianceLevel >= 3211264L);
/*  560 */       int i = total;
/*      */       do { IBinaryMethod method = iMethods[i];
/*  562 */         if ((method.getModifiers() & 0x1000) != 0) {
/*  563 */           if ((!keepBridgeMethods) || ((method.getModifiers() & 0x40) == 0))
/*      */           {
/*  566 */             if (toSkip == null) toSkip = new int[iMethods.length];
/*  567 */             toSkip[i] = -1;
/*  568 */             total--;
/*      */           }
/*  569 */         } else if (iClinit == -1) {
/*  570 */           char[] methodName = method.getSelector();
/*  571 */           if ((methodName.length == 8) && (methodName[0] == '<'))
/*      */           {
/*  573 */             iClinit = i;
/*  574 */             total--;
/*      */           }
/*      */         }
/*  560 */         i--; } while (i >= 0);
/*      */     }
/*      */ 
/*  579 */     if (total == 0) {
/*  580 */       this.methods = Binding.NO_METHODS;
/*  581 */       return;
/*      */     }
/*      */ 
/*  584 */     boolean hasRestrictedAccess = hasRestrictedAccess();
/*  585 */     this.methods = new MethodBinding[total];
/*  586 */     if (total == initialTotal) {
/*  587 */       for (int i = 0; i < initialTotal; i++) {
/*  588 */         MethodBinding method = createMethod(iMethods[i], sourceLevel, missingTypeNames);
/*  589 */         if (hasRestrictedAccess)
/*  590 */           method.modifiers |= 262144;
/*  591 */         this.methods[i] = method;
/*      */       }
/*      */     } else {
/*  594 */       int i = 0; for (int index = 0; i < initialTotal; i++)
/*  595 */         if ((iClinit != i) && ((toSkip == null) || (toSkip[i] != -1))) {
/*  596 */           MethodBinding method = createMethod(iMethods[i], sourceLevel, missingTypeNames);
/*  597 */           if (hasRestrictedAccess)
/*  598 */             method.modifiers |= 262144;
/*  599 */           this.methods[(index++)] = method;
/*      */         }
/*      */     }
/*      */   }
/*      */ 
/*      */   private TypeVariableBinding[] createTypeVariables(SignatureWrapper wrapper, boolean assignVariables, char[][][] missingTypeNames)
/*      */   {
/*  607 */     char[] typeSignature = wrapper.signature;
/*  608 */     int depth = 0; int length = typeSignature.length;
/*  609 */     int rank = 0;
/*  610 */     ArrayList variables = new ArrayList(1);
/*  611 */     depth = 0;
/*  612 */     boolean pendingVariable = true;
/*      */ 
/*  614 */     for (int i = 1; i < length; i++)
/*  615 */       switch (typeSignature[i]) {
/*      */       case '<':
/*  617 */         depth++;
/*  618 */         break;
/*      */       case '>':
/*  620 */         depth--; if (depth >= 0) continue;
/*  621 */         break;
/*      */       case ';':
/*  624 */         if ((depth != 0) || (i + 1 >= length) || (typeSignature[(i + 1)] == ':')) continue;
/*  625 */         pendingVariable = true;
/*  626 */         break;
/*      */       case '=':
/*      */       default:
/*  628 */         if (!pendingVariable) continue;
/*  629 */         pendingVariable = false;
/*  630 */         int colon = CharOperation.indexOf(':', typeSignature, i);
/*  631 */         char[] variableName = CharOperation.subarray(typeSignature, i, colon);
/*  632 */         variables.add(new TypeVariableBinding(variableName, this, rank++, this.environment));
/*      */       }
/*      */     TypeVariableBinding[] result;
/*  639 */     variables.toArray(result = new TypeVariableBinding[rank]);
/*      */ 
/*  642 */     if (assignVariables)
/*  643 */       this.typeVariables = result;
/*  644 */     for (int i = 0; i < rank; i++) {
/*  645 */       initializeTypeVariable(result[i], result, wrapper, missingTypeNames);
/*      */     }
/*  647 */     return result;
/*      */   }
/*      */ 
/*      */   public ReferenceBinding enclosingType()
/*      */   {
/*  655 */     if ((this.tagBits & 0x8000000) == 0L) {
/*  656 */       return this.enclosingType;
/*      */     }
/*      */ 
/*  659 */     this.enclosingType = ((ReferenceBinding)resolveType(this.enclosingType, this.environment, false));
/*  660 */     this.tagBits &= -134217729L;
/*  661 */     return this.enclosingType;
/*      */   }
/*      */ 
/*      */   public FieldBinding[] fields() {
/*  665 */     if ((this.tagBits & 0x2000) != 0L) {
/*  666 */       return this.fields;
/*      */     }
/*      */ 
/*  669 */     if ((this.tagBits & 0x1000) == 0L) {
/*  670 */       int length = this.fields.length;
/*  671 */       if (length > 1)
/*  672 */         ReferenceBinding.sortFields(this.fields, 0, length);
/*  673 */       this.tagBits |= 4096L;
/*      */     }
/*  675 */     int i = this.fields.length;
/*      */     do { resolveTypeFor(this.fields[i]);
/*      */ 
/*  675 */       i--; } while (i >= 0);
/*      */ 
/*  677 */     this.tagBits |= 8192L;
/*  678 */     return this.fields;
/*      */   }
/*      */ 
/*      */   private MethodBinding findMethod(char[] methodDescriptor, char[][][] missingTypeNames) {
/*  682 */     int index = -1;
/*      */     do index++; while (methodDescriptor[index] != '(');
/*      */ 
/*  686 */     char[] selector = new char[index];
/*  687 */     System.arraycopy(methodDescriptor, 0, selector, 0, index);
/*  688 */     TypeBinding[] parameters = Binding.NO_PARAMETERS;
/*  689 */     int numOfParams = 0;
/*      */     char nextChar;
/*      */     do
/*      */     {
/*      */       char nextChar;
/*  692 */       if (nextChar != '[') {
/*  693 */         numOfParams++;
/*  694 */         if (nextChar == 'L')
/*      */           do index++; while ((nextChar = methodDescriptor[index]) != ';');
/*      */       }
/*  691 */       index++; } while ((nextChar = methodDescriptor[index]) != ')');
/*      */ 
/*  699 */     int startIndex = 0;
/*  700 */     if (numOfParams > 0) {
/*  701 */       parameters = new TypeBinding[numOfParams];
/*  702 */       index = 1;
/*  703 */       int end = 0;
/*  704 */       for (int i = 0; i < numOfParams; i++) {
/*      */         do end++; while ((nextChar = methodDescriptor[end]) == '[');
/*  706 */         if (nextChar == 'L') {
/*      */           do end++; while ((nextChar = methodDescriptor[end]) != ';');
/*      */         }
/*  709 */         if (i >= startIndex) {
/*  710 */           parameters[(i - startIndex)] = this.environment.getTypeFromSignature(methodDescriptor, index, end, false, this, missingTypeNames);
/*      */         }
/*  712 */         index = end + 1;
/*      */       }
/*      */     }
/*      */ 
/*  716 */     return CharOperation.equals(selector, TypeConstants.INIT) ? 
/*  717 */       this.enclosingType.getExactConstructor(parameters) : 
/*  718 */       this.enclosingType.getExactMethod(selector, parameters, null);
/*      */   }
/*      */ 
/*      */   public char[] genericTypeSignature()
/*      */   {
/*  725 */     return computeGenericTypeSignature(this.typeVariables);
/*      */   }
/*      */ 
/*      */   public MethodBinding getExactConstructor(TypeBinding[] argumentTypes)
/*      */   {
/*  732 */     if ((this.tagBits & 0x4000) == 0L) {
/*  733 */       int length = this.methods.length;
/*  734 */       if (length > 1)
/*  735 */         ReferenceBinding.sortMethods(this.methods, 0, length);
/*  736 */       this.tagBits |= 16384L;
/*      */     }
/*  738 */     int argCount = argumentTypes.length;
/*      */     long range;
/*  740 */     if ((range = ReferenceBinding.binarySearch(TypeConstants.INIT, this.methods)) >= 0L) {
/*  741 */       int imethod = (int)range; for (int end = (int)(range >> 32); imethod <= end; imethod++) {
/*  742 */         MethodBinding method = this.methods[imethod];
/*  743 */         if (method.parameters.length == argCount) {
/*  744 */           resolveTypesFor(method);
/*  745 */           TypeBinding[] toMatch = method.parameters;
/*  746 */           int iarg = 0;
/*  747 */           while (toMatch[iarg] == argumentTypes[iarg])
/*      */           {
/*  746 */             iarg++; if (iarg >= argCount)
/*      */             {
/*  749 */               return method;
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*  753 */     return null;
/*      */   }
/*      */ 
/*      */   public MethodBinding getExactMethod(char[] selector, TypeBinding[] argumentTypes, CompilationUnitScope refScope)
/*      */   {
/*  762 */     if ((this.tagBits & 0x4000) == 0L) {
/*  763 */       int length = this.methods.length;
/*  764 */       if (length > 1)
/*  765 */         ReferenceBinding.sortMethods(this.methods, 0, length);
/*  766 */       this.tagBits |= 16384L;
/*      */     }
/*      */ 
/*  769 */     int argCount = argumentTypes.length;
/*  770 */     boolean foundNothing = true;
/*      */     long range;
/*  773 */     if ((range = ReferenceBinding.binarySearch(selector, this.methods)) >= 0L) {
/*  774 */       int imethod = (int)range; for (int end = (int)(range >> 32); imethod <= end; imethod++) {
/*  775 */         MethodBinding method = this.methods[imethod];
/*  776 */         foundNothing = false;
/*  777 */         if (method.parameters.length == argCount) {
/*  778 */           resolveTypesFor(method);
/*  779 */           TypeBinding[] toMatch = method.parameters;
/*  780 */           int iarg = 0;
/*  781 */           while (toMatch[iarg] == argumentTypes[iarg])
/*      */           {
/*  780 */             iarg++; if (iarg >= argCount)
/*      */             {
/*  783 */               return method;
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*  787 */     if (foundNothing) {
/*  788 */       if (isInterface()) {
/*  789 */         if (superInterfaces().length == 1) {
/*  790 */           if (refScope != null)
/*  791 */             refScope.recordTypeReference(this.superInterfaces[0]);
/*  792 */           return this.superInterfaces[0].getExactMethod(selector, argumentTypes, refScope);
/*      */         }
/*  794 */       } else if (superclass() != null) {
/*  795 */         if (refScope != null)
/*  796 */           refScope.recordTypeReference(this.superclass);
/*  797 */         return this.superclass.getExactMethod(selector, argumentTypes, refScope);
/*      */       }
/*      */     }
/*  800 */     return null;
/*      */   }
/*      */ 
/*      */   public FieldBinding getField(char[] fieldName, boolean needResolve)
/*      */   {
/*  805 */     if ((this.tagBits & 0x1000) == 0L) {
/*  806 */       int length = this.fields.length;
/*  807 */       if (length > 1)
/*  808 */         ReferenceBinding.sortFields(this.fields, 0, length);
/*  809 */       this.tagBits |= 4096L;
/*      */     }
/*  811 */     FieldBinding field = ReferenceBinding.binarySearch(fieldName, this.fields);
/*  812 */     return (needResolve) && (field != null) ? resolveTypeFor(field) : field;
/*      */   }
/*      */ 
/*      */   public ReferenceBinding getMemberType(char[] typeName)
/*      */   {
/*  818 */     int i = this.memberTypes.length;
/*      */     do { ReferenceBinding memberType = this.memberTypes[i];
/*  820 */       if ((memberType instanceof UnresolvedReferenceBinding)) {
/*  821 */         char[] name = memberType.sourceName;
/*  822 */         int prefixLength = this.compoundName[(this.compoundName.length - 1)].length + 1;
/*  823 */         if ((name.length == prefixLength + typeName.length) && 
/*  824 */           (CharOperation.fragmentEquals(typeName, name, prefixLength, true)))
/*  825 */           return this.memberTypes[i] =  = (ReferenceBinding)resolveType(memberType, this.environment, false);
/*  826 */       } else if (CharOperation.equals(typeName, memberType.sourceName)) {
/*  827 */         return memberType;
/*      */       }
/*  818 */       i--; } while (i >= 0);
/*      */ 
/*  830 */     return null;
/*      */   }
/*      */ 
/*      */   public MethodBinding[] getMethods(char[] selector) {
/*  834 */     if ((this.tagBits & 0x8000) != 0L)
/*      */     {
/*      */       long range;
/*  836 */       if ((range = ReferenceBinding.binarySearch(selector, this.methods)) >= 0L) {
/*  837 */         int start = (int)range; int end = (int)(range >> 32);
/*  838 */         int length = end - start + 1;
/*  839 */         if ((this.tagBits & 0x8000) != 0L)
/*      */         {
/*      */           MethodBinding[] result;
/*  842 */           System.arraycopy(this.methods, start, result = new MethodBinding[length], 0, length);
/*  843 */           return result;
/*      */         }
/*      */       }
/*  846 */       return Binding.NO_METHODS;
/*      */     }
/*      */ 
/*  849 */     if ((this.tagBits & 0x4000) == 0L) {
/*  850 */       int length = this.methods.length;
/*  851 */       if (length > 1)
/*  852 */         ReferenceBinding.sortMethods(this.methods, 0, length);
/*  853 */       this.tagBits |= 16384L;
/*      */     }
/*      */     long range;
/*  856 */     if ((range = ReferenceBinding.binarySearch(selector, this.methods)) >= 0L) {
/*  857 */       int start = (int)range; int end = (int)(range >> 32);
/*  858 */       int length = end - start + 1;
/*  859 */       MethodBinding[] result = new MethodBinding[length];
/*      */ 
/*  861 */       int i = start; for (int index = 0; i <= end; index++) {
/*  862 */         result[index] = resolveTypesFor(this.methods[i]);
/*      */ 
/*  861 */         i++;
/*      */       }
/*  863 */       return result;
/*      */     }
/*  865 */     return Binding.NO_METHODS;
/*      */   }
/*      */ 
/*      */   public MethodBinding[] getMethods(char[] selector, int suggestedParameterLength)
/*      */   {
/*  870 */     if ((this.tagBits & 0x8000) != 0L) {
/*  871 */       return getMethods(selector);
/*      */     }
/*  873 */     if ((this.tagBits & 0x4000) == 0L) {
/*  874 */       int length = this.methods.length;
/*  875 */       if (length > 1)
/*  876 */         ReferenceBinding.sortMethods(this.methods, 0, length);
/*  877 */       this.tagBits |= 16384L;
/*      */     }
/*      */     long range;
/*  880 */     if ((range = ReferenceBinding.binarySearch(selector, this.methods)) >= 0L) {
/*  881 */       int start = (int)range; int end = (int)(range >> 32);
/*  882 */       int length = end - start + 1;
/*  883 */       int count = 0;
/*  884 */       for (int i = start; i <= end; i++) {
/*  885 */         int len = this.methods[i].parameters.length;
/*  886 */         if ((len <= suggestedParameterLength) || ((this.methods[i].isVarargs()) && (len == suggestedParameterLength + 1)))
/*  887 */           count++;
/*      */       }
/*  889 */       if (count == 0) {
/*  890 */         MethodBinding[] result = new MethodBinding[length];
/*      */ 
/*  892 */         int i = start; for (int index = 0; i <= end; i++)
/*  893 */           result[(index++)] = resolveTypesFor(this.methods[i]);
/*  894 */         return result;
/*      */       }
/*  896 */       MethodBinding[] result = new MethodBinding[count];
/*      */ 
/*  898 */       int i = start; for (int index = 0; i <= end; i++) {
/*  899 */         int len = this.methods[i].parameters.length;
/*  900 */         if ((len <= suggestedParameterLength) || ((this.methods[i].isVarargs()) && (len == suggestedParameterLength + 1)))
/*  901 */           result[(index++)] = resolveTypesFor(this.methods[i]);
/*      */       }
/*  903 */       return result;
/*      */     }
/*      */ 
/*  906 */     return Binding.NO_METHODS;
/*      */   }
/*      */   public boolean hasMemberTypes() {
/*  909 */     return this.memberTypes.length > 0;
/*      */   }
/*      */ 
/*      */   public TypeVariableBinding getTypeVariable(char[] variableName) {
/*  913 */     TypeVariableBinding variable = super.getTypeVariable(variableName);
/*  914 */     variable.resolve();
/*  915 */     return variable;
/*      */   }
/*      */ 
/*      */   private void initializeTypeVariable(TypeVariableBinding variable, TypeVariableBinding[] existingVariables, SignatureWrapper wrapper, char[][][] missingTypeNames)
/*      */   {
/*  921 */     int colon = CharOperation.indexOf(':', wrapper.signature, wrapper.start);
/*  922 */     wrapper.start = (colon + 1);
/*  923 */     ReferenceBinding firstBound = null;
/*      */     ReferenceBinding type;
/*      */     ReferenceBinding type;
/*  924 */     if (wrapper.signature[wrapper.start] == ':') {
/*  925 */       type = this.environment.getResolvedType(TypeConstants.JAVA_LANG_OBJECT, null);
/*      */     } else {
/*  927 */       type = (ReferenceBinding)this.environment.getTypeFromTypeSignature(wrapper, existingVariables, this, missingTypeNames);
/*  928 */       firstBound = type;
/*      */     }
/*      */ 
/*  932 */     variable.modifiers |= 33554432;
/*  933 */     variable.superclass = type;
/*      */ 
/*  935 */     ReferenceBinding[] bounds = (ReferenceBinding[])null;
/*  936 */     if (wrapper.signature[wrapper.start] == ':') {
/*  937 */       ArrayList types = new ArrayList(2);
/*      */       do {
/*  939 */         wrapper.start += 1;
/*  940 */         types.add(this.environment.getTypeFromTypeSignature(wrapper, existingVariables, this, missingTypeNames));
/*  941 */       }while (wrapper.signature[wrapper.start] == ':');
/*  942 */       bounds = new ReferenceBinding[types.size()];
/*  943 */       types.toArray(bounds);
/*      */     }
/*      */ 
/*  946 */     variable.superInterfaces = (bounds == null ? Binding.NO_SUPERINTERFACES : bounds);
/*  947 */     if (firstBound == null) {
/*  948 */       firstBound = variable.superInterfaces.length == 0 ? null : variable.superInterfaces[0];
/*      */     }
/*  950 */     variable.firstBound = firstBound;
/*      */   }
/*      */ 
/*      */   public boolean isEquivalentTo(TypeBinding otherType)
/*      */   {
/*  957 */     if (this == otherType) return true;
/*  958 */     if (otherType == null) return false;
/*  959 */     switch (otherType.kind()) {
/*      */     case 516:
/*      */     case 8196:
/*  962 */       return ((WildcardBinding)otherType).boundCheck(this);
/*      */     case 1028:
/*  964 */       return otherType.erasure() == this;
/*      */     }
/*  966 */     return false;
/*      */   }
/*      */   public boolean isGenericType() {
/*  969 */     return this.typeVariables != Binding.NO_TYPE_VARIABLES;
/*      */   }
/*      */   public boolean isHierarchyConnected() {
/*  972 */     return (this.tagBits & 0x6000000) == 0L;
/*      */   }
/*      */   public int kind() {
/*  975 */     if (this.typeVariables != Binding.NO_TYPE_VARIABLES)
/*  976 */       return 2052;
/*  977 */     return 4;
/*      */   }
/*      */ 
/*      */   public ReferenceBinding[] memberTypes() {
/*  981 */     if ((this.tagBits & 0x10000000) == 0L) {
/*  982 */       return this.memberTypes;
/*      */     }
/*  984 */     int i = this.memberTypes.length;
/*      */     do { this.memberTypes[i] = ((ReferenceBinding)resolveType(this.memberTypes[i], this.environment, false));
/*      */ 
/*  984 */       i--; } while (i >= 0);
/*      */ 
/*  986 */     this.tagBits &= -268435457L;
/*  987 */     return this.memberTypes;
/*      */   }
/*      */ 
/*      */   public MethodBinding[] methods() {
/*  991 */     if ((this.tagBits & 0x8000) != 0L) {
/*  992 */       return this.methods;
/*      */     }
/*      */ 
/*  995 */     if ((this.tagBits & 0x4000) == 0L) {
/*  996 */       int length = this.methods.length;
/*  997 */       if (length > 1)
/*  998 */         ReferenceBinding.sortMethods(this.methods, 0, length);
/*  999 */       this.tagBits |= 16384L;
/*      */     }
/* 1001 */     int i = this.methods.length;
/*      */     do { resolveTypesFor(this.methods[i]);
/*      */ 
/* 1001 */       i--; } while (i >= 0);
/*      */ 
/* 1003 */     this.tagBits |= 32768L;
/* 1004 */     return this.methods;
/*      */   }
/*      */   private FieldBinding resolveTypeFor(FieldBinding field) {
/* 1007 */     if ((field.modifiers & 0x2000000) == 0) {
/* 1008 */       return field;
/*      */     }
/* 1010 */     TypeBinding resolvedType = resolveType(field.type, this.environment, true);
/* 1011 */     field.type = resolvedType;
/* 1012 */     if ((resolvedType.tagBits & 0x80) != 0L) {
/* 1013 */       field.tagBits |= 128L;
/*      */     }
/* 1015 */     field.modifiers &= -33554433;
/* 1016 */     return field;
/*      */   }
/*      */   MethodBinding resolveTypesFor(MethodBinding method) {
/* 1019 */     if ((method.modifiers & 0x2000000) == 0) {
/* 1020 */       return method;
/*      */     }
/* 1022 */     if (!method.isConstructor()) {
/* 1023 */       TypeBinding resolvedType = resolveType(method.returnType, this.environment, true);
/* 1024 */       method.returnType = resolvedType;
/* 1025 */       if ((resolvedType.tagBits & 0x80) != 0L) {
/* 1026 */         method.tagBits |= 128L;
/*      */       }
/*      */     }
/* 1029 */     int i = method.parameters.length;
/*      */     do { TypeBinding resolvedType = resolveType(method.parameters[i], this.environment, true);
/* 1031 */       method.parameters[i] = resolvedType;
/* 1032 */       if ((resolvedType.tagBits & 0x80) != 0L)
/* 1033 */         method.tagBits |= 128L;
/* 1029 */       i--; } while (i >= 0);
/*      */ 
/* 1036 */     int i = method.thrownExceptions.length;
/*      */     do { ReferenceBinding resolvedType = (ReferenceBinding)resolveType(method.thrownExceptions[i], this.environment, true);
/* 1038 */       method.thrownExceptions[i] = resolvedType;
/* 1039 */       if ((resolvedType.tagBits & 0x80) != 0L)
/* 1040 */         method.tagBits |= 128L;
/* 1036 */       i--; } while (i >= 0);
/*      */ 
/* 1043 */     int i = method.typeVariables.length;
/*      */     do { method.typeVariables[i].resolve();
/*      */ 
/* 1043 */       i--; } while (i >= 0);
/*      */ 
/* 1046 */     method.modifiers &= -33554433;
/* 1047 */     return method;
/*      */   }
/*      */   AnnotationBinding[] retrieveAnnotations(Binding binding) {
/* 1050 */     return AnnotationBinding.addStandardAnnotations(super.retrieveAnnotations(binding), binding.getAnnotationTagBits(), this.environment);
/*      */   }
/*      */   SimpleLookupTable storedAnnotations(boolean forceInitialize) {
/* 1053 */     if ((forceInitialize) && (this.storedAnnotations == null)) {
/* 1054 */       if (!this.environment.globalOptions.storeAnnotations)
/* 1055 */         return null;
/* 1056 */       this.storedAnnotations = new SimpleLookupTable(3);
/*      */     }
/* 1058 */     return this.storedAnnotations;
/*      */   }
/*      */ 
/*      */   public ReferenceBinding superclass()
/*      */   {
/* 1065 */     if ((this.tagBits & 0x2000000) == 0L) {
/* 1066 */       return this.superclass;
/*      */     }
/*      */ 
/* 1069 */     this.superclass = ((ReferenceBinding)resolveType(this.superclass, this.environment, true));
/* 1070 */     this.tagBits &= -33554433L;
/* 1071 */     if (this.superclass.problemId() == 1)
/* 1072 */       this.tagBits |= 131072L;
/* 1073 */     return this.superclass;
/*      */   }
/*      */ 
/*      */   public ReferenceBinding[] superInterfaces() {
/* 1077 */     if ((this.tagBits & 0x4000000) == 0L) {
/* 1078 */       return this.superInterfaces;
/*      */     }
/* 1080 */     int i = this.superInterfaces.length;
/*      */     do { this.superInterfaces[i] = ((ReferenceBinding)resolveType(this.superInterfaces[i], this.environment, true));
/* 1082 */       if (this.superInterfaces[i].problemId() == 1)
/* 1083 */         this.tagBits |= 131072L;
/* 1080 */       i--; } while (i >= 0);
/*      */ 
/* 1085 */     this.tagBits &= -67108865L;
/* 1086 */     return this.superInterfaces;
/*      */   }
/*      */   public TypeVariableBinding[] typeVariables() {
/* 1089 */     if ((this.tagBits & 0x1000000) == 0L) {
/* 1090 */       return this.typeVariables;
/*      */     }
/* 1092 */     int i = this.typeVariables.length;
/*      */     do { this.typeVariables[i].resolve();
/*      */ 
/* 1092 */       i--; } while (i >= 0);
/*      */ 
/* 1094 */     this.tagBits &= -16777217L;
/* 1095 */     return this.typeVariables;
/*      */   }
/*      */   public String toString() {
/* 1098 */     StringBuffer buffer = new StringBuffer();
/*      */ 
/* 1100 */     if (isDeprecated()) buffer.append("deprecated ");
/* 1101 */     if (isPublic()) buffer.append("public ");
/* 1102 */     if (isProtected()) buffer.append("protected ");
/* 1103 */     if (isPrivate()) buffer.append("private ");
/* 1104 */     if ((isAbstract()) && (isClass())) buffer.append("abstract ");
/* 1105 */     if ((isStatic()) && (isNestedType())) buffer.append("static ");
/* 1106 */     if (isFinal()) buffer.append("final ");
/*      */ 
/* 1108 */     if (isEnum()) buffer.append("enum ");
/* 1109 */     else if (isAnnotationType()) buffer.append("@interface ");
/* 1110 */     else if (isClass()) buffer.append("class "); else
/* 1111 */       buffer.append("interface ");
/* 1112 */     buffer.append(this.compoundName != null ? CharOperation.toString(this.compoundName) : "UNNAMED TYPE");
/*      */ 
/* 1114 */     if (this.typeVariables == null) {
/* 1115 */       buffer.append("<NULL TYPE VARIABLES>");
/* 1116 */     } else if (this.typeVariables != Binding.NO_TYPE_VARIABLES) {
/* 1117 */       buffer.append("<");
/* 1118 */       int i = 0; for (int length = this.typeVariables.length; i < length; i++) {
/* 1119 */         if (i > 0) buffer.append(", ");
/* 1120 */         if (this.typeVariables[i] == null) {
/* 1121 */           buffer.append("NULL TYPE VARIABLE");
/*      */         }
/*      */         else {
/* 1124 */           char[] varChars = this.typeVariables[i].toString().toCharArray();
/* 1125 */           buffer.append(varChars, 1, varChars.length - 2);
/*      */         }
/*      */       }
/* 1127 */       buffer.append(">");
/*      */     }
/* 1129 */     buffer.append("\n\textends ");
/* 1130 */     buffer.append(this.superclass != null ? this.superclass.debugName() : "NULL TYPE");
/*      */ 
/* 1132 */     if (this.superInterfaces != null) {
/* 1133 */       if (this.superInterfaces != Binding.NO_SUPERINTERFACES) {
/* 1134 */         buffer.append("\n\timplements : ");
/* 1135 */         int i = 0; for (int length = this.superInterfaces.length; i < length; i++) {
/* 1136 */           if (i > 0)
/* 1137 */             buffer.append(", ");
/* 1138 */           buffer.append(this.superInterfaces[i] != null ? this.superInterfaces[i].debugName() : "NULL TYPE");
/*      */         }
/*      */       }
/*      */     }
/* 1142 */     else buffer.append("NULL SUPERINTERFACES");
/*      */ 
/* 1145 */     if (this.enclosingType != null) {
/* 1146 */       buffer.append("\n\tenclosing type : ");
/* 1147 */       buffer.append(this.enclosingType.debugName());
/*      */     }
/*      */ 
/* 1150 */     if (this.fields != null) {
/* 1151 */       if (this.fields != Binding.NO_FIELDS) {
/* 1152 */         buffer.append("\n/*   fields   */");
/* 1153 */         int i = 0; for (int length = this.fields.length; i < length; i++)
/* 1154 */           buffer.append(this.fields[i] != null ? "\n" + this.fields[i].toString() : "\nNULL FIELD");
/*      */       }
/*      */     }
/* 1157 */     else buffer.append("NULL FIELDS");
/*      */ 
/* 1160 */     if (this.methods != null) {
/* 1161 */       if (this.methods != Binding.NO_METHODS) {
/* 1162 */         buffer.append("\n/*   methods   */");
/* 1163 */         int i = 0; for (int length = this.methods.length; i < length; i++)
/* 1164 */           buffer.append(this.methods[i] != null ? "\n" + this.methods[i].toString() : "\nNULL METHOD");
/*      */       }
/*      */     }
/* 1167 */     else buffer.append("NULL METHODS");
/*      */ 
/* 1170 */     if (this.memberTypes != null) {
/* 1171 */       if (this.memberTypes != Binding.NO_MEMBER_TYPES) {
/* 1172 */         buffer.append("\n/*   members   */");
/* 1173 */         int i = 0; for (int length = this.memberTypes.length; i < length; i++)
/* 1174 */           buffer.append(this.memberTypes[i] != null ? "\n" + this.memberTypes[i].toString() : "\nNULL TYPE");
/*      */       }
/*      */     }
/* 1177 */     else buffer.append("NULL MEMBER TYPES");
/*      */ 
/* 1180 */     buffer.append("\n\n\n");
/* 1181 */     return buffer.toString();
/*      */   }
/*      */   MethodBinding[] unResolvedMethods() {
/* 1184 */     return this.methods;
/*      */   }
/*      */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\ecj-3.5.2.jar
 * Qualified Name:     org.eclipse.jdt.internal.compiler.lookup.BinaryTypeBinding
 * JD-Core Version:    0.6.0
 */