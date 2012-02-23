/*      */ package org.eclipse.jdt.internal.compiler.lookup;
/*      */ 
/*      */ import java.util.List;
/*      */ import org.eclipse.jdt.core.compiler.CharOperation;
/*      */ import org.eclipse.jdt.internal.compiler.ast.TypeReference;
/*      */ import org.eclipse.jdt.internal.compiler.problem.ProblemReporter;
/*      */ 
/*      */ public class ParameterizedTypeBinding extends ReferenceBinding
/*      */   implements Substitution
/*      */ {
/*      */   private ReferenceBinding type;
/*      */   public TypeBinding[] arguments;
/*      */   public LookupEnvironment environment;
/*      */   public char[] genericTypeSignature;
/*      */   public ReferenceBinding superclass;
/*      */   public ReferenceBinding[] superInterfaces;
/*      */   public FieldBinding[] fields;
/*      */   public ReferenceBinding[] memberTypes;
/*      */   public MethodBinding[] methods;
/*      */   private ReferenceBinding enclosingType;
/*      */ 
/*      */   public ParameterizedTypeBinding(ReferenceBinding type, TypeBinding[] arguments, ReferenceBinding enclosingType, LookupEnvironment environment)
/*      */   {
/*   36 */     this.environment = environment;
/*   37 */     this.enclosingType = enclosingType;
/*      */ 
/*   46 */     initialize(type, arguments);
/*   47 */     if ((type instanceof UnresolvedReferenceBinding))
/*   48 */       ((UnresolvedReferenceBinding)type).addWrapper(this, environment);
/*   49 */     if (arguments != null) {
/*   50 */       int i = 0; for (int l = arguments.length; i < l; i++)
/*   51 */         if ((arguments[i] instanceof UnresolvedReferenceBinding))
/*   52 */           ((UnresolvedReferenceBinding)arguments[i]).addWrapper(this, environment);
/*      */     }
/*   54 */     this.tagBits |= 16777216L;
/*      */   }
/*      */ 
/*      */   protected ReferenceBinding actualType()
/*      */   {
/*   62 */     return this.type;
/*      */   }
/*      */ 
/*      */   public void boundCheck(Scope scope, TypeReference[] argumentReferences)
/*      */   {
/*   69 */     if ((this.tagBits & 0x400000) == 0L) {
/*   70 */       boolean hasErrors = false;
/*   71 */       TypeVariableBinding[] typeVariables = this.type.typeVariables();
/*   72 */       if ((this.arguments != null) && (typeVariables != null)) {
/*   73 */         int i = 0; for (int length = typeVariables.length; i < length; i++) {
/*   74 */           if (typeVariables[i].boundCheck(this, this.arguments[i]) != 0) {
/*   75 */             hasErrors = true;
/*   76 */             if ((this.arguments[i].tagBits & 0x80) != 0L)
/*      */               continue;
/*   78 */             scope.problemReporter().typeMismatchError(this.arguments[i], typeVariables[i], this.type, argumentReferences[i]);
/*      */           }
/*      */         }
/*      */       }
/*      */ 
/*   83 */       if (!hasErrors) this.tagBits |= 4194304L;
/*      */     }
/*      */   }
/*      */ 
/*      */   public boolean canBeInstantiated()
/*      */   {
/*   90 */     return ((this.tagBits & 0x40000000) == 0L) && (super.canBeInstantiated());
/*      */   }
/*      */ 
/*      */   public TypeBinding capture(Scope scope, int position)
/*      */   {
/*   97 */     if ((this.tagBits & 0x40000000) == 0L) {
/*   98 */       return this;
/*      */     }
/*  100 */     TypeBinding[] originalArguments = this.arguments;
/*  101 */     int length = originalArguments.length;
/*  102 */     TypeBinding[] capturedArguments = new TypeBinding[length];
/*      */ 
/*  105 */     ReferenceBinding contextType = scope.enclosingSourceType();
/*  106 */     if (contextType != null) contextType = contextType.outermostEnclosingType();
/*      */ 
/*  108 */     for (int i = 0; i < length; i++) {
/*  109 */       TypeBinding argument = originalArguments[i];
/*  110 */       if (argument.kind() == 516)
/*  111 */         capturedArguments[i] = new CaptureBinding((WildcardBinding)argument, contextType, position, scope.compilationUnitScope().nextCaptureID());
/*      */       else {
/*  113 */         capturedArguments[i] = argument;
/*      */       }
/*      */     }
/*  116 */     ParameterizedTypeBinding capturedParameterizedType = this.environment.createParameterizedType(this.type, capturedArguments, enclosingType());
/*  117 */     for (int i = 0; i < length; i++) {
/*  118 */       TypeBinding argument = capturedArguments[i];
/*  119 */       if (argument.isCapture()) {
/*  120 */         ((CaptureBinding)argument).initializeBounds(scope, capturedParameterizedType);
/*      */       }
/*      */     }
/*  123 */     return capturedParameterizedType;
/*      */   }
/*      */ 
/*      */   public List collectMissingTypes(List missingTypes)
/*      */   {
/*  130 */     if ((this.tagBits & 0x80) != 0L) {
/*  131 */       if (this.enclosingType != null) {
/*  132 */         missingTypes = this.enclosingType.collectMissingTypes(missingTypes);
/*      */       }
/*  134 */       missingTypes = genericType().collectMissingTypes(missingTypes);
/*  135 */       if (this.arguments != null) {
/*  136 */         int i = 0; for (int max = this.arguments.length; i < max; i++) {
/*  137 */           missingTypes = this.arguments[i].collectMissingTypes(missingTypes);
/*      */         }
/*      */       }
/*      */     }
/*  141 */     return missingTypes;
/*      */   }
/*      */ 
/*      */   public void collectSubstitutes(Scope scope, TypeBinding actualType, InferenceContext inferenceContext, int constraint)
/*      */   {
/*  153 */     if ((this.tagBits & 0x20000000) == 0L) {
/*  154 */       TypeBinding actualEquivalent = actualType.findSuperTypeOriginatingFrom(this.type);
/*  155 */       if ((actualEquivalent != null) && (actualEquivalent.isRawType())) {
/*  156 */         inferenceContext.isUnchecked = true;
/*      */       }
/*  158 */       return;
/*      */     }
/*  160 */     if (actualType == TypeBinding.NULL) return;
/*      */ 
/*  162 */     if (!(actualType instanceof ReferenceBinding)) return;
/*      */     TypeBinding formalEquivalent;
/*      */     TypeBinding actualEquivalent;
/*  164 */     switch (constraint) {
/*      */     case 0:
/*      */     case 1:
/*  167 */       TypeBinding formalEquivalent = this;
/*  168 */       TypeBinding actualEquivalent = actualType.findSuperTypeOriginatingFrom(this.type);
/*  169 */       if (actualEquivalent != null) break; return;
/*      */     case 2:
/*      */     default:
/*  173 */       formalEquivalent = findSuperTypeOriginatingFrom(actualType);
/*  174 */       if (formalEquivalent == null) return;
/*  175 */       actualEquivalent = actualType;
/*      */     }
/*      */ 
/*  179 */     ReferenceBinding formalEnclosingType = formalEquivalent.enclosingType();
/*  180 */     if (formalEnclosingType != null) {
/*  181 */       formalEnclosingType.collectSubstitutes(scope, actualEquivalent.enclosingType(), inferenceContext, constraint);
/*      */     }
/*      */ 
/*  184 */     if (this.arguments == null) return;
/*      */     TypeBinding[] formalArguments;
/*      */     TypeBinding[] formalArguments;
/*  186 */     switch (formalEquivalent.kind()) {
/*      */     case 2052:
/*  188 */       formalArguments = formalEquivalent.typeVariables();
/*  189 */       break;
/*      */     case 260:
/*  191 */       formalArguments = ((ParameterizedTypeBinding)formalEquivalent).arguments;
/*  192 */       break;
/*      */     case 1028:
/*  194 */       if (inferenceContext.depth > 0) {
/*  195 */         inferenceContext.status = 1;
/*      */       }
/*  197 */       return;
/*      */     default:
/*  199 */       return;
/*      */     }
/*      */     TypeBinding[] formalArguments;
/*      */     TypeBinding[] actualArguments;
/*      */     TypeBinding[] actualArguments;
/*  202 */     switch (actualEquivalent.kind()) {
/*      */     case 2052:
/*  204 */       actualArguments = actualEquivalent.typeVariables();
/*  205 */       break;
/*      */     case 260:
/*  207 */       actualArguments = ((ParameterizedTypeBinding)actualEquivalent).arguments;
/*  208 */       break;
/*      */     case 1028:
/*  210 */       if (inferenceContext.depth > 0)
/*  211 */         inferenceContext.status = 1;
/*      */       else {
/*  213 */         inferenceContext.isUnchecked = true;
/*      */       }
/*  215 */       return;
/*      */     default:
/*  217 */       return;
/*      */     }
/*      */     TypeBinding[] actualArguments;
/*  219 */     inferenceContext.depth += 1;
/*  220 */     int i = 0; for (int length = formalArguments.length; i < length; i++) {
/*  221 */       TypeBinding formalArgument = formalArguments[i];
/*  222 */       TypeBinding actualArgument = actualArguments[i];
/*  223 */       if (formalArgument.isWildcard()) {
/*  224 */         formalArgument.collectSubstitutes(scope, actualArgument, inferenceContext, constraint);
/*      */       } else {
/*  226 */         if (actualArgument.isWildcard()) {
/*  227 */           WildcardBinding actualWildcardArgument = (WildcardBinding)actualArgument;
/*  228 */           if (actualWildcardArgument.otherBounds == null) {
/*  229 */             if (constraint != 2) continue;
/*  230 */             switch (actualWildcardArgument.boundKind) {
/*      */             case 1:
/*  232 */               formalArgument.collectSubstitutes(scope, actualWildcardArgument.bound, inferenceContext, 2);
/*  233 */               break;
/*      */             case 2:
/*  235 */               formalArgument.collectSubstitutes(scope, actualWildcardArgument.bound, inferenceContext, 1);
/*  236 */               break;
/*      */             default:
/*  238 */               break;
/*      */             }
/*      */ 
/*      */           }
/*      */ 
/*      */         }
/*      */ 
/*  246 */         formalArgument.collectSubstitutes(scope, actualArgument, inferenceContext, 0);
/*      */       }
/*      */     }
/*  248 */     inferenceContext.depth -= 1;
/*      */   }
/*      */ 
/*      */   public void computeId()
/*      */   {
/*  255 */     this.id = 2147483647;
/*      */   }
/*      */ 
/*      */   public char[] computeUniqueKey(boolean isLeaf) {
/*  259 */     StringBuffer sig = new StringBuffer(10);
/*      */     ReferenceBinding enclosing;
/*  261 */     if ((isMemberType()) && (((enclosing = enclosingType()).isParameterizedType()) || (enclosing.isRawType()))) {
/*  262 */       char[] typeSig = enclosing.computeUniqueKey(false);
/*  263 */       sig.append(typeSig, 0, typeSig.length - 1);
/*  264 */       sig.append('.').append(sourceName());
/*  265 */     } else if (this.type.isLocalType()) {
/*  266 */       LocalTypeBinding localTypeBinding = (LocalTypeBinding)this.type;
/*  267 */       ReferenceBinding enclosing = localTypeBinding.enclosingType();
/*      */       ReferenceBinding temp;
/*  269 */       while ((temp = enclosing.enclosingType()) != null)
/*      */       {
/*      */         ReferenceBinding temp;
/*  270 */         enclosing = temp;
/*  271 */       }char[] typeSig = enclosing.computeUniqueKey(false);
/*  272 */       sig.append(typeSig, 0, typeSig.length - 1);
/*  273 */       sig.append('$');
/*  274 */       sig.append(localTypeBinding.sourceStart);
/*      */     } else {
/*  276 */       char[] typeSig = this.type.computeUniqueKey(false);
/*  277 */       sig.append(typeSig, 0, typeSig.length - 1);
/*      */     }
/*  279 */     ReferenceBinding captureSourceType = null;
/*  280 */     if (this.arguments != null) {
/*  281 */       sig.append('<');
/*  282 */       int i = 0; for (int length = this.arguments.length; i < length; i++) {
/*  283 */         TypeBinding typeBinding = this.arguments[i];
/*  284 */         sig.append(typeBinding.computeUniqueKey(false));
/*  285 */         if ((typeBinding instanceof CaptureBinding))
/*  286 */           captureSourceType = ((CaptureBinding)typeBinding).sourceType;
/*      */       }
/*  288 */       sig.append('>');
/*      */     }
/*  290 */     sig.append(';');
/*  291 */     if ((captureSourceType != null) && (captureSourceType != this.type))
/*      */     {
/*  293 */       sig.insert(0, "&");
/*  294 */       sig.insert(0, captureSourceType.computeUniqueKey(false));
/*      */     }
/*      */ 
/*  297 */     int sigLength = sig.length();
/*  298 */     char[] uniqueKey = new char[sigLength];
/*  299 */     sig.getChars(0, sigLength, uniqueKey, 0);
/*  300 */     return uniqueKey;
/*      */   }
/*      */ 
/*      */   public char[] constantPoolName()
/*      */   {
/*  307 */     return this.type.constantPoolName();
/*      */   }
/*      */ 
/*      */   public ParameterizedMethodBinding createParameterizedMethod(MethodBinding originalMethod) {
/*  311 */     return new ParameterizedMethodBinding(this, originalMethod);
/*      */   }
/*      */ 
/*      */   public String debugName()
/*      */   {
/*  318 */     StringBuffer nameBuffer = new StringBuffer(10);
/*  319 */     if ((this.type instanceof UnresolvedReferenceBinding))
/*  320 */       nameBuffer.append(this.type);
/*      */     else {
/*  322 */       nameBuffer.append(this.type.sourceName());
/*      */     }
/*  324 */     if (this.arguments != null) {
/*  325 */       nameBuffer.append('<');
/*  326 */       int i = 0; for (int length = this.arguments.length; i < length; i++) {
/*  327 */         if (i > 0) nameBuffer.append(',');
/*  328 */         nameBuffer.append(this.arguments[i].debugName());
/*      */       }
/*  330 */       nameBuffer.append('>');
/*      */     }
/*  332 */     return nameBuffer.toString();
/*      */   }
/*      */ 
/*      */   public ReferenceBinding enclosingType()
/*      */   {
/*  339 */     return this.enclosingType;
/*      */   }
/*      */ 
/*      */   public LookupEnvironment environment()
/*      */   {
/*  346 */     return this.environment;
/*      */   }
/*      */ 
/*      */   public TypeBinding erasure()
/*      */   {
/*  353 */     return this.type.erasure();
/*      */   }
/*      */ 
/*      */   public int fieldCount()
/*      */   {
/*  359 */     return this.type.fieldCount();
/*      */   }
/*      */ 
/*      */   public FieldBinding[] fields()
/*      */   {
/*  366 */     if ((this.tagBits & 0x2000) != 0L)
/*  367 */       return this.fields;
/*      */     try
/*      */     {
/*  370 */       FieldBinding[] originalFields = this.type.fields();
/*  371 */       int length = originalFields.length;
/*  372 */       FieldBinding[] parameterizedFields = new FieldBinding[length];
/*  373 */       for (int i = 0; i < length; i++)
/*      */       {
/*  375 */         parameterizedFields[i] = new ParameterizedFieldBinding(this, originalFields[i]);
/*  376 */       }this.fields = parameterizedFields;
/*      */     }
/*      */     finally {
/*  379 */       if (this.fields == null)
/*  380 */         this.fields = Binding.NO_FIELDS;
/*  381 */       this.tagBits |= 8192L;
/*      */     }
/*  383 */     return this.fields;
/*      */   }
/*      */ 
/*      */   public ReferenceBinding genericType()
/*      */   {
/*  392 */     if ((this.type instanceof UnresolvedReferenceBinding))
/*  393 */       ((UnresolvedReferenceBinding)this.type).resolve(this.environment, false);
/*  394 */     return this.type;
/*      */   }
/*      */ 
/*      */   public char[] genericTypeSignature()
/*      */   {
/*  402 */     if (this.genericTypeSignature == null) {
/*  403 */       if ((this.modifiers & 0x40000000) == 0) {
/*  404 */         this.genericTypeSignature = this.type.signature();
/*      */       } else {
/*  406 */         StringBuffer sig = new StringBuffer(10);
/*  407 */         if (isMemberType()) {
/*  408 */           ReferenceBinding enclosing = enclosingType();
/*  409 */           char[] typeSig = enclosing.genericTypeSignature();
/*  410 */           sig.append(typeSig, 0, typeSig.length - 1);
/*  411 */           if ((enclosing.modifiers & 0x40000000) != 0)
/*  412 */             sig.append('.');
/*      */           else {
/*  414 */             sig.append('$');
/*      */           }
/*  416 */           sig.append(sourceName());
/*      */         } else {
/*  418 */           char[] typeSig = this.type.signature();
/*  419 */           sig.append(typeSig, 0, typeSig.length - 1);
/*      */         }
/*  421 */         if (this.arguments != null) {
/*  422 */           sig.append('<');
/*  423 */           int i = 0; for (int length = this.arguments.length; i < length; i++) {
/*  424 */             sig.append(this.arguments[i].genericTypeSignature());
/*      */           }
/*  426 */           sig.append('>');
/*      */         }
/*  428 */         sig.append(';');
/*  429 */         int sigLength = sig.length();
/*  430 */         this.genericTypeSignature = new char[sigLength];
/*  431 */         sig.getChars(0, sigLength, this.genericTypeSignature, 0);
/*      */       }
/*      */     }
/*  434 */     return this.genericTypeSignature;
/*      */   }
/*      */ 
/*      */   public long getAnnotationTagBits()
/*      */   {
/*  441 */     return this.type.getAnnotationTagBits();
/*      */   }
/*      */ 
/*      */   public int getEnclosingInstancesSlotSize() {
/*  445 */     return genericType().getEnclosingInstancesSlotSize();
/*      */   }
/*      */ 
/*      */   public MethodBinding getExactConstructor(TypeBinding[] argumentTypes)
/*      */   {
/*  452 */     int argCount = argumentTypes.length;
/*  453 */     MethodBinding match = null;
/*      */ 
/*  455 */     if ((this.tagBits & 0x8000) != 0L)
/*      */     {
/*      */       long range;
/*  457 */       if ((range = ReferenceBinding.binarySearch(TypeConstants.INIT, this.methods)) >= 0L) {
/*  458 */         int imethod = (int)range; for (int end = (int)(range >> 32); imethod <= end; imethod++) {
/*  459 */           MethodBinding method = this.methods[imethod];
/*  460 */           if (method.parameters.length == argCount) {
/*  461 */             TypeBinding[] toMatch = method.parameters;
/*  462 */             int iarg = 0;
/*  463 */             while (toMatch[iarg] == argumentTypes[iarg])
/*      */             {
/*  462 */               iarg++; if (iarg < argCount) {
/*      */                 continue;
/*      */               }
/*  465 */               if (match != null) return null;
/*  466 */               match = method;
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/*      */     } else {
/*  471 */       MethodBinding[] matchingMethods = getMethods(TypeConstants.INIT);
/*  472 */       int m = matchingMethods.length;
/*      */       do { MethodBinding method = matchingMethods[m];
/*  474 */         TypeBinding[] toMatch = method.parameters;
/*  475 */         if (toMatch.length == argCount) {
/*  476 */           int p = 0;
/*  477 */           while (toMatch[p] == argumentTypes[p])
/*      */           {
/*  476 */             p++; if (p < argCount) {
/*      */               continue;
/*      */             }
/*  479 */             if (match != null) return null;
/*  480 */             match = method;
/*      */           }
/*      */         }
/*  472 */         m--; } while (m >= 0);
/*      */     }
/*      */ 
/*  484 */     return match;
/*      */   }
/*      */ 
/*      */   public MethodBinding getExactMethod(char[] selector, TypeBinding[] argumentTypes, CompilationUnitScope refScope)
/*      */   {
/*  492 */     int argCount = argumentTypes.length;
/*  493 */     boolean foundNothing = true;
/*  494 */     MethodBinding match = null;
/*      */ 
/*  496 */     if ((this.tagBits & 0x8000) != 0L)
/*      */     {
/*      */       long range;
/*  498 */       if ((range = ReferenceBinding.binarySearch(selector, this.methods)) >= 0L) {
/*  499 */         int imethod = (int)range; for (int end = (int)(range >> 32); imethod <= end; imethod++) {
/*  500 */           MethodBinding method = this.methods[imethod];
/*  501 */           foundNothing = false;
/*  502 */           if (method.parameters.length == argCount) {
/*  503 */             TypeBinding[] toMatch = method.parameters;
/*  504 */             int iarg = 0;
/*  505 */             while (toMatch[iarg] == argumentTypes[iarg])
/*      */             {
/*  504 */               iarg++; if (iarg < argCount) {
/*      */                 continue;
/*      */               }
/*  507 */               if (match != null) return null;
/*  508 */               match = method;
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/*      */     } else {
/*  513 */       MethodBinding[] matchingMethods = getMethods(selector);
/*  514 */       foundNothing = matchingMethods == Binding.NO_METHODS;
/*  515 */       int m = matchingMethods.length;
/*      */       do { MethodBinding method = matchingMethods[m];
/*  517 */         TypeBinding[] toMatch = method.parameters;
/*  518 */         if (toMatch.length == argCount) {
/*  519 */           int p = 0;
/*  520 */           while (toMatch[p] == argumentTypes[p])
/*      */           {
/*  519 */             p++; if (p < argCount) {
/*      */               continue;
/*      */             }
/*  522 */             if (match != null) return null;
/*  523 */             match = method;
/*      */           }
/*      */         }
/*  515 */         m--; } while (m >= 0);
/*      */     }
/*      */ 
/*  527 */     if (match != null)
/*      */     {
/*  531 */       if (match.hasSubstitutedParameters()) return null;
/*  532 */       return match;
/*      */     }
/*      */ 
/*  535 */     if ((foundNothing) && ((this.arguments == null) || (this.arguments.length <= 1))) {
/*  536 */       if (isInterface()) {
/*  537 */         if (superInterfaces().length == 1) {
/*  538 */           if (refScope != null)
/*  539 */             refScope.recordTypeReference(this.superInterfaces[0]);
/*  540 */           return this.superInterfaces[0].getExactMethod(selector, argumentTypes, refScope);
/*      */         }
/*  542 */       } else if (superclass() != null) {
/*  543 */         if (refScope != null)
/*  544 */           refScope.recordTypeReference(this.superclass);
/*  545 */         return this.superclass.getExactMethod(selector, argumentTypes, refScope);
/*      */       }
/*      */     }
/*  548 */     return null;
/*      */   }
/*      */ 
/*      */   public FieldBinding getField(char[] fieldName, boolean needResolve)
/*      */   {
/*  555 */     fields();
/*  556 */     return ReferenceBinding.binarySearch(fieldName, this.fields);
/*      */   }
/*      */ 
/*      */   public ReferenceBinding getMemberType(char[] typeName)
/*      */   {
/*  563 */     memberTypes();
/*  564 */     int typeLength = typeName.length;
/*  565 */     int i = this.memberTypes.length;
/*      */     do { ReferenceBinding memberType = this.memberTypes[i];
/*  567 */       if ((memberType.sourceName.length == typeLength) && (CharOperation.equals(memberType.sourceName, typeName)))
/*  568 */         return memberType;
/*  565 */       i--; } while (i >= 0);
/*      */ 
/*  570 */     return null; } 
/*      */   // ERROR //
/*      */   public MethodBinding[] getMethods(char[] selector) { // Byte code:
/*      */     //   0: aload_0
/*      */     //   1: getfield 393	org/eclipse/jdt/internal/compiler/lookup/ParameterizedTypeBinding:methods	[Lorg/eclipse/jdt/internal/compiler/lookup/MethodBinding;
/*      */     //   4: ifnull +57 -> 61
/*      */     //   7: aload_1
/*      */     //   8: aload_0
/*      */     //   9: getfield 393	org/eclipse/jdt/internal/compiler/lookup/ParameterizedTypeBinding:methods	[Lorg/eclipse/jdt/internal/compiler/lookup/MethodBinding;
/*      */     //   12: invokestatic 395	org/eclipse/jdt/internal/compiler/lookup/ReferenceBinding:binarySearch	([C[Lorg/eclipse/jdt/internal/compiler/lookup/MethodBinding;)J
/*      */     //   15: dup2
/*      */     //   16: lstore_2
/*      */     //   17: lconst_0
/*      */     //   18: lcmp
/*      */     //   19: iflt +42 -> 61
/*      */     //   22: lload_2
/*      */     //   23: l2i
/*      */     //   24: istore 4
/*      */     //   26: lload_2
/*      */     //   27: bipush 32
/*      */     //   29: lshr
/*      */     //   30: l2i
/*      */     //   31: iload 4
/*      */     //   33: isub
/*      */     //   34: iconst_1
/*      */     //   35: iadd
/*      */     //   36: istore 5
/*      */     //   38: aload_0
/*      */     //   39: getfield 393	org/eclipse/jdt/internal/compiler/lookup/ParameterizedTypeBinding:methods	[Lorg/eclipse/jdt/internal/compiler/lookup/MethodBinding;
/*      */     //   42: iload 4
/*      */     //   44: iload 5
/*      */     //   46: anewarray 400	org/eclipse/jdt/internal/compiler/lookup/MethodBinding
/*      */     //   49: dup
/*      */     //   50: astore 6
/*      */     //   52: iconst_0
/*      */     //   53: iload 5
/*      */     //   55: invokestatic 475	java/lang/System:arraycopy	(Ljava/lang/Object;ILjava/lang/Object;II)V
/*      */     //   58: aload 6
/*      */     //   60: areturn
/*      */     //   61: aload_0
/*      */     //   62: getfield 44	org/eclipse/jdt/internal/compiler/lookup/ParameterizedTypeBinding:tagBits	J
/*      */     //   65: ldc2_w 386
/*      */     //   68: land
/*      */     //   69: lconst_0
/*      */     //   70: lcmp
/*      */     //   71: ifeq +7 -> 78
/*      */     //   74: getstatic 422	org/eclipse/jdt/internal/compiler/lookup/Binding:NO_METHODS	[Lorg/eclipse/jdt/internal/compiler/lookup/MethodBinding;
/*      */     //   77: areturn
/*      */     //   78: aconst_null
/*      */     //   79: checkcast 481	[Lorg/eclipse/jdt/internal/compiler/lookup/MethodBinding;
/*      */     //   82: astore_2
/*      */     //   83: aload_0
/*      */     //   84: getfield 59	org/eclipse/jdt/internal/compiler/lookup/ParameterizedTypeBinding:type	Lorg/eclipse/jdt/internal/compiler/lookup/ReferenceBinding;
/*      */     //   87: aload_1
/*      */     //   88: invokevirtual 482	org/eclipse/jdt/internal/compiler/lookup/ReferenceBinding:getMethods	([C)[Lorg/eclipse/jdt/internal/compiler/lookup/MethodBinding;
/*      */     //   91: astore_3
/*      */     //   92: aload_3
/*      */     //   93: arraylength
/*      */     //   94: istore 4
/*      */     //   96: iload 4
/*      */     //   98: ifne +24 -> 122
/*      */     //   101: getstatic 422	org/eclipse/jdt/internal/compiler/lookup/Binding:NO_METHODS	[Lorg/eclipse/jdt/internal/compiler/lookup/MethodBinding;
/*      */     //   104: astore 8
/*      */     //   106: aload_2
/*      */     //   107: ifnonnull +12 -> 119
/*      */     //   110: aload_0
/*      */     //   111: getstatic 422	org/eclipse/jdt/internal/compiler/lookup/Binding:NO_METHODS	[Lorg/eclipse/jdt/internal/compiler/lookup/MethodBinding;
/*      */     //   114: dup
/*      */     //   115: astore_2
/*      */     //   116: putfield 393	org/eclipse/jdt/internal/compiler/lookup/ParameterizedTypeBinding:methods	[Lorg/eclipse/jdt/internal/compiler/lookup/MethodBinding;
/*      */     //   119: aload 8
/*      */     //   121: areturn
/*      */     //   122: iload 4
/*      */     //   124: anewarray 400	org/eclipse/jdt/internal/compiler/lookup/MethodBinding
/*      */     //   127: astore_2
/*      */     //   128: iconst_0
/*      */     //   129: istore 5
/*      */     //   131: goto +18 -> 149
/*      */     //   134: aload_2
/*      */     //   135: iload 5
/*      */     //   137: aload_0
/*      */     //   138: aload_3
/*      */     //   139: iload 5
/*      */     //   141: aaload
/*      */     //   142: invokevirtual 483	org/eclipse/jdt/internal/compiler/lookup/ParameterizedTypeBinding:createParameterizedMethod	(Lorg/eclipse/jdt/internal/compiler/lookup/MethodBinding;)Lorg/eclipse/jdt/internal/compiler/lookup/ParameterizedMethodBinding;
/*      */     //   145: aastore
/*      */     //   146: iinc 5 1
/*      */     //   149: iload 5
/*      */     //   151: iload 4
/*      */     //   153: if_icmplt -19 -> 134
/*      */     //   156: aload_0
/*      */     //   157: getfield 393	org/eclipse/jdt/internal/compiler/lookup/ParameterizedTypeBinding:methods	[Lorg/eclipse/jdt/internal/compiler/lookup/MethodBinding;
/*      */     //   160: ifnonnull +29 -> 189
/*      */     //   163: iload 4
/*      */     //   165: anewarray 400	org/eclipse/jdt/internal/compiler/lookup/MethodBinding
/*      */     //   168: astore 5
/*      */     //   170: aload_2
/*      */     //   171: iconst_0
/*      */     //   172: aload 5
/*      */     //   174: iconst_0
/*      */     //   175: iload 4
/*      */     //   177: invokestatic 475	java/lang/System:arraycopy	(Ljava/lang/Object;ILjava/lang/Object;II)V
/*      */     //   180: aload_0
/*      */     //   181: aload 5
/*      */     //   183: putfield 393	org/eclipse/jdt/internal/compiler/lookup/ParameterizedTypeBinding:methods	[Lorg/eclipse/jdt/internal/compiler/lookup/MethodBinding;
/*      */     //   186: goto +67 -> 253
/*      */     //   189: iload 4
/*      */     //   191: aload_0
/*      */     //   192: getfield 393	org/eclipse/jdt/internal/compiler/lookup/ParameterizedTypeBinding:methods	[Lorg/eclipse/jdt/internal/compiler/lookup/MethodBinding;
/*      */     //   195: arraylength
/*      */     //   196: iadd
/*      */     //   197: istore 5
/*      */     //   199: iload 5
/*      */     //   201: anewarray 400	org/eclipse/jdt/internal/compiler/lookup/MethodBinding
/*      */     //   204: astore 6
/*      */     //   206: aload_2
/*      */     //   207: iconst_0
/*      */     //   208: aload 6
/*      */     //   210: iconst_0
/*      */     //   211: iload 4
/*      */     //   213: invokestatic 475	java/lang/System:arraycopy	(Ljava/lang/Object;ILjava/lang/Object;II)V
/*      */     //   216: aload_0
/*      */     //   217: getfield 393	org/eclipse/jdt/internal/compiler/lookup/ParameterizedTypeBinding:methods	[Lorg/eclipse/jdt/internal/compiler/lookup/MethodBinding;
/*      */     //   220: iconst_0
/*      */     //   221: aload 6
/*      */     //   223: iload 4
/*      */     //   225: aload_0
/*      */     //   226: getfield 393	org/eclipse/jdt/internal/compiler/lookup/ParameterizedTypeBinding:methods	[Lorg/eclipse/jdt/internal/compiler/lookup/MethodBinding;
/*      */     //   229: arraylength
/*      */     //   230: invokestatic 475	java/lang/System:arraycopy	(Ljava/lang/Object;ILjava/lang/Object;II)V
/*      */     //   233: iload 5
/*      */     //   235: iconst_1
/*      */     //   236: if_icmple +11 -> 247
/*      */     //   239: aload 6
/*      */     //   241: iconst_0
/*      */     //   242: iload 5
/*      */     //   244: invokestatic 485	org/eclipse/jdt/internal/compiler/lookup/ReferenceBinding:sortMethods	([Lorg/eclipse/jdt/internal/compiler/lookup/MethodBinding;II)V
/*      */     //   247: aload_0
/*      */     //   248: aload 6
/*      */     //   250: putfield 393	org/eclipse/jdt/internal/compiler/lookup/ParameterizedTypeBinding:methods	[Lorg/eclipse/jdt/internal/compiler/lookup/MethodBinding;
/*      */     //   253: aload_2
/*      */     //   254: astore 8
/*      */     //   256: aload_2
/*      */     //   257: ifnonnull +12 -> 269
/*      */     //   260: aload_0
/*      */     //   261: getstatic 422	org/eclipse/jdt/internal/compiler/lookup/Binding:NO_METHODS	[Lorg/eclipse/jdt/internal/compiler/lookup/MethodBinding;
/*      */     //   264: dup
/*      */     //   265: astore_2
/*      */     //   266: putfield 393	org/eclipse/jdt/internal/compiler/lookup/ParameterizedTypeBinding:methods	[Lorg/eclipse/jdt/internal/compiler/lookup/MethodBinding;
/*      */     //   269: aload 8
/*      */     //   271: areturn
/*      */     //   272: astore 7
/*      */     //   274: aload_2
/*      */     //   275: ifnonnull +12 -> 287
/*      */     //   278: aload_0
/*      */     //   279: getstatic 422	org/eclipse/jdt/internal/compiler/lookup/Binding:NO_METHODS	[Lorg/eclipse/jdt/internal/compiler/lookup/MethodBinding;
/*      */     //   282: dup
/*      */     //   283: astore_2
/*      */     //   284: putfield 393	org/eclipse/jdt/internal/compiler/lookup/ParameterizedTypeBinding:methods	[Lorg/eclipse/jdt/internal/compiler/lookup/MethodBinding;
/*      */     //   287: aload 7
/*      */     //   289: athrow
/*      */     //
/*      */     // Exception table:
/*      */     //   from	to	target	type
/*      */     //   83	106	272	finally
/*      */     //   122	256	272	finally } 
/*  625 */   public int getOuterLocalVariablesSlotSize() { return genericType().getOuterLocalVariablesSlotSize(); }
/*      */ 
/*      */   public boolean hasMemberTypes()
/*      */   {
/*  629 */     return this.type.hasMemberTypes();
/*      */   }
/*      */ 
/*      */   public boolean implementsMethod(MethodBinding method)
/*      */   {
/*  636 */     return this.type.implementsMethod(method);
/*      */   }
/*      */ 
/*      */   void initialize(ReferenceBinding someType, TypeBinding[] someArguments) {
/*  640 */     this.type = someType;
/*  641 */     this.sourceName = someType.sourceName;
/*  642 */     this.compoundName = someType.compoundName;
/*  643 */     this.fPackage = someType.fPackage;
/*  644 */     this.fileName = someType.fileName;
/*      */ 
/*  650 */     this.modifiers = (someType.modifiers & 0xBFFFFFFF);
/*      */ 
/*  652 */     if (someArguments != null) {
/*  653 */       this.modifiers |= 1073741824;
/*  654 */     } else if (this.enclosingType != null) {
/*  655 */       this.modifiers |= this.enclosingType.modifiers & 0x40000000;
/*  656 */       this.tagBits |= this.enclosingType.tagBits & 0x20000080;
/*      */     }
/*  658 */     if (someArguments != null) {
/*  659 */       this.arguments = someArguments;
/*  660 */       int i = 0; for (int length = someArguments.length; i < length; i++) {
/*  661 */         TypeBinding someArgument = someArguments[i];
/*  662 */         switch (someArgument.kind()) {
/*      */         case 516:
/*  664 */           this.tagBits |= 1073741824L;
/*  665 */           if (((WildcardBinding)someArgument).boundKind == 0) break;
/*  666 */           this.tagBits |= 8388608L;
/*      */ 
/*  668 */           break;
/*      */         case 8196:
/*  670 */           this.tagBits |= 1073741824L;
/*  671 */           break;
/*      */         default:
/*  673 */           this.tagBits |= 8388608L;
/*      */         }
/*      */ 
/*  676 */         this.tagBits |= someArgument.tagBits & 0x20000880;
/*      */       }
/*      */     }
/*  679 */     this.tagBits |= someType.tagBits & 0x89C;
/*  680 */     this.tagBits &= -40961L;
/*      */   }
/*      */ 
/*      */   protected void initializeArguments()
/*      */   {
/*      */   }
/*      */ 
/*      */   void initializeForStaticImports() {
/*  688 */     this.type.initializeForStaticImports();
/*      */   }
/*      */ 
/*      */   public boolean isEquivalentTo(TypeBinding otherType) {
/*  692 */     if (this == otherType)
/*  693 */       return true;
/*  694 */     if (otherType == null)
/*  695 */       return false;
/*  696 */     switch (otherType.kind())
/*      */     {
/*      */     case 516:
/*      */     case 8196:
/*  700 */       return ((WildcardBinding)otherType).boundCheck(this);
/*      */     case 260:
/*  703 */       ParameterizedTypeBinding otherParamType = (ParameterizedTypeBinding)otherType;
/*  704 */       if (this.type != otherParamType.type)
/*  705 */         return false;
/*  706 */       if (!isStatic()) {
/*  707 */         ReferenceBinding enclosing = enclosingType();
/*  708 */         if (enclosing != null) {
/*  709 */           ReferenceBinding otherEnclosing = otherParamType.enclosingType();
/*  710 */           if (otherEnclosing == null) return false;
/*  711 */           if ((otherEnclosing.tagBits & 0x40000000) == 0L) {
/*  712 */             if (enclosing != otherEnclosing) return false;
/*      */           }
/*  714 */           else if (!enclosing.isEquivalentTo(otherParamType.enclosingType())) return false;
/*      */         }
/*      */       }
/*      */ 
/*  718 */       if (this.arguments == null) {
/*  719 */         return otherParamType.arguments == null;
/*      */       }
/*  721 */       int length = this.arguments.length;
/*  722 */       TypeBinding[] otherArguments = otherParamType.arguments;
/*  723 */       if ((otherArguments == null) || (otherArguments.length != length)) return false;
/*  724 */       for (int i = 0; i < length; i++) {
/*  725 */         if (!this.arguments[i].isTypeArgumentContainedBy(otherArguments[i]))
/*  726 */           return false;
/*      */       }
/*  728 */       return true;
/*      */     case 1028:
/*  731 */       return erasure() == otherType.erasure();
/*      */     }
/*  733 */     return false;
/*      */   }
/*      */ 
/*      */   public boolean isHierarchyConnected() {
/*  737 */     return (this.superclass != null) && (this.superInterfaces != null);
/*      */   }
/*      */ 
/*      */   public boolean isRawSubstitution()
/*      */   {
/*  744 */     return isRawType();
/*      */   }
/*      */ 
/*      */   public int kind() {
/*  748 */     return 260;
/*      */   }
/*      */ 
/*      */   public ReferenceBinding[] memberTypes()
/*      */   {
/*  755 */     if (this.memberTypes == null) {
/*      */       try {
/*  757 */         ReferenceBinding[] originalMemberTypes = this.type.memberTypes();
/*  758 */         int length = originalMemberTypes.length;
/*  759 */         ReferenceBinding[] parameterizedMemberTypes = new ReferenceBinding[length];
/*      */ 
/*  761 */         for (int i = 0; i < length; i++)
/*      */         {
/*  763 */           parameterizedMemberTypes[i] = 
/*  765 */             this.environment.createParameterizedType(originalMemberTypes[i], null, this);
/*  766 */         }this.memberTypes = parameterizedMemberTypes;
/*      */       }
/*      */       finally {
/*  769 */         if (this.memberTypes == null)
/*  770 */           this.memberTypes = Binding.NO_MEMBER_TYPES;
/*      */       }
/*      */     }
/*  773 */     return this.memberTypes;
/*      */   }
/*      */ 
/*      */   public MethodBinding[] methods()
/*      */   {
/*  780 */     if ((this.tagBits & 0x8000) != 0L)
/*  781 */       return this.methods;
/*      */     try
/*      */     {
/*  784 */       MethodBinding[] originalMethods = this.type.methods();
/*  785 */       int length = originalMethods.length;
/*  786 */       MethodBinding[] parameterizedMethods = new MethodBinding[length];
/*  787 */       for (int i = 0; i < length; i++)
/*      */       {
/*  789 */         parameterizedMethods[i] = createParameterizedMethod(originalMethods[i]);
/*  790 */       }this.methods = parameterizedMethods;
/*      */     }
/*      */     finally {
/*  793 */       if (this.methods == null) {
/*  794 */         this.methods = Binding.NO_METHODS;
/*      */       }
/*  796 */       this.tagBits |= 32768L;
/*      */     }
/*  798 */     return this.methods;
/*      */   }
/*      */ 
/*      */   public int problemId()
/*      */   {
/*  806 */     return this.type.problemId();
/*      */   }
/*      */ 
/*      */   public char[] qualifiedPackageName()
/*      */   {
/*  812 */     return this.type.qualifiedPackageName();
/*      */   }
/*      */ 
/*      */   public char[] qualifiedSourceName()
/*      */   {
/*  819 */     return this.type.qualifiedSourceName();
/*      */   }
/*      */ 
/*      */   public char[] readableName()
/*      */   {
/*  826 */     StringBuffer nameBuffer = new StringBuffer(10);
/*  827 */     if (isMemberType())
/*  828 */       nameBuffer.append(CharOperation.concat(enclosingType().readableName(), this.sourceName, '.'));
/*      */     else {
/*  830 */       nameBuffer.append(CharOperation.concatWith(this.type.compoundName, '.'));
/*      */     }
/*  832 */     if (this.arguments != null) {
/*  833 */       nameBuffer.append('<');
/*  834 */       int i = 0; for (int length = this.arguments.length; i < length; i++) {
/*  835 */         if (i > 0) nameBuffer.append(',');
/*  836 */         nameBuffer.append(this.arguments[i].readableName());
/*      */       }
/*  838 */       nameBuffer.append('>');
/*      */     }
/*  840 */     int nameLength = nameBuffer.length();
/*  841 */     char[] readableName = new char[nameLength];
/*  842 */     nameBuffer.getChars(0, nameLength, readableName, 0);
/*  843 */     return readableName;
/*      */   }
/*      */ 
/*      */   ReferenceBinding resolve() {
/*  847 */     if ((this.tagBits & 0x1000000) == 0L) {
/*  848 */       return this;
/*      */     }
/*  850 */     this.tagBits &= -16777217L;
/*  851 */     ReferenceBinding resolvedType = (ReferenceBinding)BinaryTypeBinding.resolveType(this.type, this.environment, false);
/*  852 */     this.tagBits |= resolvedType.tagBits & 0x800;
/*  853 */     if (this.arguments != null) {
/*  854 */       int argLength = this.arguments.length;
/*  855 */       for (int i = 0; i < argLength; i++) {
/*  856 */         TypeBinding resolveType = BinaryTypeBinding.resolveType(this.arguments[i], this.environment, true);
/*  857 */         this.arguments[i] = resolveType;
/*  858 */         this.tagBits |= resolvedType.tagBits & 0x800;
/*      */       }
/*      */ 
/*  861 */       TypeVariableBinding[] refTypeVariables = resolvedType.typeVariables();
/*  862 */       if (refTypeVariables == Binding.NO_TYPE_VARIABLES) {
/*  863 */         if ((resolvedType.tagBits & 0x80) == 0L) {
/*  864 */           this.environment.problemReporter.nonGenericTypeCannotBeParameterized(0, null, resolvedType, this.arguments);
/*      */         }
/*  866 */         return this;
/*  867 */       }if (argLength != refTypeVariables.length) {
/*  868 */         this.environment.problemReporter.incorrectArityForParameterizedType(null, resolvedType, this.arguments);
/*  869 */         return this;
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  879 */     return this;
/*      */   }
/*      */ 
/*      */   public char[] shortReadableName()
/*      */   {
/*  886 */     StringBuffer nameBuffer = new StringBuffer(10);
/*  887 */     if (isMemberType())
/*  888 */       nameBuffer.append(CharOperation.concat(enclosingType().shortReadableName(), this.sourceName, '.'));
/*      */     else {
/*  890 */       nameBuffer.append(this.type.sourceName);
/*      */     }
/*  892 */     if (this.arguments != null) {
/*  893 */       nameBuffer.append('<');
/*  894 */       int i = 0; for (int length = this.arguments.length; i < length; i++) {
/*  895 */         if (i > 0) nameBuffer.append(',');
/*  896 */         nameBuffer.append(this.arguments[i].shortReadableName());
/*      */       }
/*  898 */       nameBuffer.append('>');
/*      */     }
/*  900 */     int nameLength = nameBuffer.length();
/*  901 */     char[] shortReadableName = new char[nameLength];
/*  902 */     nameBuffer.getChars(0, nameLength, shortReadableName, 0);
/*  903 */     return shortReadableName;
/*      */   }
/*      */ 
/*      */   public char[] signature()
/*      */   {
/*  910 */     if (this.signature == null) {
/*  911 */       this.signature = this.type.signature();
/*      */     }
/*  913 */     return this.signature;
/*      */   }
/*      */ 
/*      */   public char[] sourceName()
/*      */   {
/*  920 */     return this.type.sourceName();
/*      */   }
/*      */ 
/*      */   public TypeBinding substitute(TypeVariableBinding originalVariable)
/*      */   {
/*  928 */     ParameterizedTypeBinding currentType = this;
/*      */     while (true) {
/*  930 */       TypeVariableBinding[] typeVariables = currentType.type.typeVariables();
/*  931 */       int length = typeVariables.length;
/*      */ 
/*  933 */       if ((originalVariable.rank < length) && (typeVariables[originalVariable.rank] == originalVariable))
/*      */       {
/*  935 */         if (currentType.arguments == null)
/*  936 */           currentType.initializeArguments();
/*  937 */         if (currentType.arguments != null) {
/*  938 */           return currentType.arguments[originalVariable.rank];
/*      */         }
/*      */       }
/*  941 */       if (currentType.isStatic()) break;
/*  942 */       ReferenceBinding enclosing = currentType.enclosingType();
/*  943 */       if (!(enclosing instanceof ParameterizedTypeBinding))
/*      */         break;
/*  945 */       currentType = (ParameterizedTypeBinding)enclosing;
/*      */     }
/*  947 */     return originalVariable;
/*      */   }
/*      */ 
/*      */   public ReferenceBinding superclass()
/*      */   {
/*  954 */     if (this.superclass == null)
/*      */     {
/*  956 */       ReferenceBinding genericSuperclass = this.type.superclass();
/*  957 */       if (genericSuperclass == null) return null;
/*  958 */       this.superclass = ((ReferenceBinding)Scope.substitute(this, genericSuperclass));
/*      */     }
/*  960 */     return this.superclass;
/*      */   }
/*      */ 
/*      */   public ReferenceBinding[] superInterfaces()
/*      */   {
/*  967 */     if (this.superInterfaces == null) {
/*  968 */       if (this.type.isHierarchyBeingConnected())
/*  969 */         return Binding.NO_SUPERINTERFACES;
/*  970 */       this.superInterfaces = Scope.substitute(this, this.type.superInterfaces());
/*      */     }
/*  972 */     return this.superInterfaces;
/*      */   }
/*      */ 
/*      */   public void swapUnresolved(UnresolvedReferenceBinding unresolvedType, ReferenceBinding resolvedType, LookupEnvironment env) {
/*  976 */     boolean update = false;
/*  977 */     if (this.type == unresolvedType) {
/*  978 */       this.type = resolvedType;
/*  979 */       update = true;
/*  980 */       ReferenceBinding enclosing = resolvedType.enclosingType();
/*  981 */       if (enclosing != null) {
/*  982 */         this.enclosingType = ((ReferenceBinding)env.convertUnresolvedBinaryToRawType(enclosing));
/*      */       }
/*      */     }
/*  985 */     if (this.arguments != null) {
/*  986 */       int i = 0; for (int l = this.arguments.length; i < l; i++) {
/*  987 */         if (this.arguments[i] == unresolvedType) {
/*  988 */           this.arguments[i] = env.convertUnresolvedBinaryToRawType(resolvedType);
/*  989 */           update = true;
/*      */         }
/*      */       }
/*      */     }
/*  993 */     if (update)
/*  994 */       initialize(this.type, this.arguments);
/*      */   }
/*      */ 
/*      */   public ReferenceBinding[] syntheticEnclosingInstanceTypes()
/*      */   {
/* 1001 */     return genericType().syntheticEnclosingInstanceTypes();
/*      */   }
/*      */ 
/*      */   public SyntheticArgumentBinding[] syntheticOuterLocalVariables()
/*      */   {
/* 1008 */     return genericType().syntheticOuterLocalVariables();
/*      */   }
/*      */ 
/*      */   public String toString()
/*      */   {
/* 1015 */     StringBuffer buffer = new StringBuffer(30);
/* 1016 */     if ((this.type instanceof UnresolvedReferenceBinding)) {
/* 1017 */       buffer.append(debugName());
/*      */     } else {
/* 1019 */       if (isDeprecated()) buffer.append("deprecated ");
/* 1020 */       if (isPublic()) buffer.append("public ");
/* 1021 */       if (isProtected()) buffer.append("protected ");
/* 1022 */       if (isPrivate()) buffer.append("private ");
/* 1023 */       if ((isAbstract()) && (isClass())) buffer.append("abstract ");
/* 1024 */       if ((isStatic()) && (isNestedType())) buffer.append("static ");
/* 1025 */       if (isFinal()) buffer.append("final ");
/*      */ 
/* 1027 */       if (isEnum()) buffer.append("enum ");
/* 1028 */       else if (isAnnotationType()) buffer.append("@interface ");
/* 1029 */       else if (isClass()) buffer.append("class "); else
/* 1030 */         buffer.append("interface ");
/* 1031 */       buffer.append(debugName());
/*      */ 
/* 1033 */       buffer.append("\n\textends ");
/* 1034 */       buffer.append(this.superclass != null ? this.superclass.debugName() : "NULL TYPE");
/*      */ 
/* 1036 */       if (this.superInterfaces != null) {
/* 1037 */         if (this.superInterfaces != Binding.NO_SUPERINTERFACES) {
/* 1038 */           buffer.append("\n\timplements : ");
/* 1039 */           int i = 0; for (int length = this.superInterfaces.length; i < length; i++) {
/* 1040 */             if (i > 0)
/* 1041 */               buffer.append(", ");
/* 1042 */             buffer.append(this.superInterfaces[i] != null ? this.superInterfaces[i].debugName() : "NULL TYPE");
/*      */           }
/*      */         }
/*      */       }
/* 1046 */       else buffer.append("NULL SUPERINTERFACES");
/*      */ 
/* 1049 */       if (enclosingType() != null) {
/* 1050 */         buffer.append("\n\tenclosing type : ");
/* 1051 */         buffer.append(enclosingType().debugName());
/*      */       }
/*      */ 
/* 1054 */       if (this.fields != null) {
/* 1055 */         if (this.fields != Binding.NO_FIELDS) {
/* 1056 */           buffer.append("\n/*   fields   */");
/* 1057 */           int i = 0; for (int length = this.fields.length; i < length; i++)
/* 1058 */             buffer.append('\n').append(this.fields[i] != null ? this.fields[i].toString() : "NULL FIELD");
/*      */         }
/*      */       }
/* 1061 */       else buffer.append("NULL FIELDS");
/*      */ 
/* 1064 */       if (this.methods != null) {
/* 1065 */         if (this.methods != Binding.NO_METHODS) {
/* 1066 */           buffer.append("\n/*   methods   */");
/* 1067 */           int i = 0; for (int length = this.methods.length; i < length; i++)
/* 1068 */             buffer.append('\n').append(this.methods[i] != null ? this.methods[i].toString() : "NULL METHOD");
/*      */         }
/*      */       }
/* 1071 */       else buffer.append("NULL METHODS");
/*      */ 
/* 1084 */       buffer.append("\n\n");
/*      */     }
/* 1086 */     return buffer.toString();
/*      */   }
/*      */ 
/*      */   public TypeVariableBinding[] typeVariables()
/*      */   {
/* 1091 */     if (this.arguments == null)
/*      */     {
/* 1093 */       return this.type.typeVariables();
/*      */     }
/* 1095 */     return Binding.NO_TYPE_VARIABLES;
/*      */   }
/*      */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\ecj-3.5.2.jar
 * Qualified Name:     org.eclipse.jdt.internal.compiler.lookup.ParameterizedTypeBinding
 * JD-Core Version:    0.6.0
 */