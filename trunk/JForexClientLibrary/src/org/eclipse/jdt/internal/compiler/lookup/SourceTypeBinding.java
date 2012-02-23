/*      */ package org.eclipse.jdt.internal.compiler.lookup;
/*      */ 
/*      */ import java.util.Collection;
/*      */ import java.util.HashMap;
/*      */ import java.util.Iterator;
/*      */ import java.util.Set;
/*      */ import org.eclipse.jdt.core.compiler.CharOperation;
/*      */ import org.eclipse.jdt.internal.compiler.ast.ASTNode;
/*      */ import org.eclipse.jdt.internal.compiler.ast.AbstractMethodDeclaration;
/*      */ import org.eclipse.jdt.internal.compiler.ast.Argument;
/*      */ import org.eclipse.jdt.internal.compiler.ast.CompilationUnitDeclaration;
/*      */ import org.eclipse.jdt.internal.compiler.ast.FieldDeclaration;
/*      */ import org.eclipse.jdt.internal.compiler.ast.MethodDeclaration;
/*      */ import org.eclipse.jdt.internal.compiler.ast.TypeDeclaration;
/*      */ import org.eclipse.jdt.internal.compiler.ast.TypeParameter;
/*      */ import org.eclipse.jdt.internal.compiler.ast.TypeReference;
/*      */ import org.eclipse.jdt.internal.compiler.impl.CompilerOptions;
/*      */ import org.eclipse.jdt.internal.compiler.impl.Constant;
/*      */ import org.eclipse.jdt.internal.compiler.problem.ProblemReporter;
/*      */ import org.eclipse.jdt.internal.compiler.util.SimpleLookupTable;
/*      */ import org.eclipse.jdt.internal.compiler.util.Util;
/*      */ 
/*      */ public class SourceTypeBinding extends ReferenceBinding
/*      */ {
/*      */   public ReferenceBinding superclass;
/*      */   public ReferenceBinding[] superInterfaces;
/*      */   private FieldBinding[] fields;
/*      */   private MethodBinding[] methods;
/*      */   public ReferenceBinding[] memberTypes;
/*      */   public TypeVariableBinding[] typeVariables;
/*      */   public ClassScope scope;
/*      */   private static final int METHOD_EMUL = 0;
/*      */   private static final int FIELD_EMUL = 1;
/*      */   private static final int CLASS_LITERAL_EMUL = 2;
/*      */   private static final int MAX_SYNTHETICS = 3;
/*      */   HashMap[] synthetics;
/*      */   char[] genericReferenceTypeSignature;
/*   52 */   private SimpleLookupTable storedAnnotations = null;
/*      */ 
/*      */   public SourceTypeBinding(char[][] compoundName, PackageBinding fPackage, ClassScope scope) {
/*   55 */     this.compoundName = compoundName;
/*   56 */     this.fPackage = fPackage;
/*   57 */     this.fileName = scope.referenceCompilationUnit().getFileName();
/*   58 */     this.modifiers = scope.referenceContext.modifiers;
/*   59 */     this.sourceName = scope.referenceContext.name;
/*   60 */     this.scope = scope;
/*      */ 
/*   63 */     this.fields = Binding.UNINITIALIZED_FIELDS;
/*   64 */     this.methods = Binding.UNINITIALIZED_METHODS;
/*      */ 
/*   66 */     computeId();
/*      */   }
/*      */ 
/*      */   private void addDefaultAbstractMethods() {
/*   70 */     if ((this.tagBits & 0x400) != 0L) return;
/*      */ 
/*   72 */     this.tagBits |= 1024L;
/*   73 */     if ((isClass()) && (isAbstract())) {
/*   74 */       if (this.scope.compilerOptions().targetJDK >= 3014656L) {
/*   75 */         return;
/*      */       }
/*   77 */       ReferenceBinding[] itsInterfaces = superInterfaces();
/*   78 */       if (itsInterfaces != Binding.NO_SUPERINTERFACES) {
/*   79 */         MethodBinding[] defaultAbstracts = (MethodBinding[])null;
/*   80 */         int defaultAbstractsCount = 0;
/*   81 */         ReferenceBinding[] interfacesToVisit = itsInterfaces;
/*   82 */         int nextPosition = interfacesToVisit.length;
/*   83 */         for (int i = 0; i < nextPosition; i++) {
/*   84 */           ReferenceBinding superType = interfacesToVisit[i];
/*   85 */           if (superType.isValidBinding()) {
/*   86 */             MethodBinding[] superMethods = superType.methods();
/*   87 */             int m = superMethods.length;
/*      */             do { MethodBinding method = superMethods[m];
/*      */ 
/*   90 */               if (!implementsMethod(method))
/*      */               {
/*   92 */                 if (defaultAbstractsCount == 0) {
/*   93 */                   defaultAbstracts = new MethodBinding[5];
/*      */                 }
/*      */                 else {
/*   96 */                   for (int k = 0; k < defaultAbstractsCount; k++) {
/*   97 */                     MethodBinding alreadyAdded = defaultAbstracts[k];
/*   98 */                     if ((CharOperation.equals(alreadyAdded.selector, method.selector)) && (alreadyAdded.areParametersEqual(method)))
/*      */                       break;
/*      */                   }
/*      */                 }
/*  102 */                 MethodBinding defaultAbstract = new MethodBinding(
/*  103 */                   method.modifiers | 0x80000 | 0x1000, 
/*  104 */                   method.selector, 
/*  105 */                   method.returnType, 
/*  106 */                   method.parameters, 
/*  107 */                   method.thrownExceptions, 
/*  108 */                   this);
/*  109 */                 if (defaultAbstractsCount == defaultAbstracts.length)
/*  110 */                   System.arraycopy(defaultAbstracts, 0, defaultAbstracts = new MethodBinding[2 * defaultAbstractsCount], 0, defaultAbstractsCount);
/*  111 */                 defaultAbstracts[(defaultAbstractsCount++)] = defaultAbstract;
/*      */               }
/*   87 */               m--; } while (m >= 0);
/*      */ 
/*  114 */             if ((itsInterfaces = superType.superInterfaces()) != Binding.NO_SUPERINTERFACES) {
/*  115 */               int itsLength = itsInterfaces.length;
/*  116 */               if (nextPosition + itsLength >= interfacesToVisit.length)
/*  117 */                 System.arraycopy(interfacesToVisit, 0, interfacesToVisit = new ReferenceBinding[nextPosition + itsLength + 5], 0, nextPosition);
/*  118 */               for (int a = 0; a < itsLength; a++) {
/*  119 */                 ReferenceBinding next = itsInterfaces[a];
/*  120 */                 int b = 0;
/*  121 */                 while (next != interfacesToVisit[b])
/*      */                 {
/*  120 */                   b++; if (b < nextPosition)
/*      */                     continue;
/*  122 */                   interfacesToVisit[(nextPosition++)] = next;
/*      */                 }
/*      */               }
/*      */             }
/*      */           }
/*      */         }
/*  127 */         if (defaultAbstractsCount > 0) {
/*  128 */           int length = this.methods.length;
/*  129 */           System.arraycopy(this.methods, 0, this.methods = new MethodBinding[length + defaultAbstractsCount], 0, length);
/*  130 */           System.arraycopy(defaultAbstracts, 0, this.methods, length, defaultAbstractsCount);
/*      */ 
/*  132 */           length += defaultAbstractsCount;
/*  133 */           if (length > 1)
/*  134 */             ReferenceBinding.sortMethods(this.methods, 0, length);
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   public FieldBinding addSyntheticFieldForInnerclass(LocalVariableBinding actualOuterLocalVariable)
/*      */   {
/*  144 */     if (this.synthetics == null)
/*  145 */       this.synthetics = new HashMap[3];
/*  146 */     if (this.synthetics[1] == null) {
/*  147 */       this.synthetics[1] = new HashMap(5);
/*      */     }
/*  149 */     FieldBinding synthField = (FieldBinding)this.synthetics[1].get(actualOuterLocalVariable);
/*  150 */     if (synthField == null) {
/*  151 */       synthField = new SyntheticFieldBinding(
/*  152 */         CharOperation.concat(TypeConstants.SYNTHETIC_OUTER_LOCAL_PREFIX, actualOuterLocalVariable.name), 
/*  153 */         actualOuterLocalVariable.type, 
/*  154 */         4114, 
/*  155 */         this, 
/*  156 */         Constant.NotAConstant, 
/*  157 */         this.synthetics[1].size());
/*  158 */       this.synthetics[1].put(actualOuterLocalVariable, synthField);
/*  163 */     }
/*      */ int index = 1;
/*      */     boolean needRecheck;
/*      */     do {
/*  165 */       needRecheck = false;
/*      */       FieldBinding existingField;
/*  167 */       if ((existingField = getField(synthField.name, true)) != null) {
/*  168 */         TypeDeclaration typeDecl = this.scope.referenceContext;
/*  169 */         int i = 0; for (int max = typeDecl.fields.length; i < max; i++) {
/*  170 */           FieldDeclaration fieldDecl = typeDecl.fields[i];
/*  171 */           if (fieldDecl.binding == existingField) {
/*  172 */             synthField.name = CharOperation.concat(
/*  173 */               TypeConstants.SYNTHETIC_OUTER_LOCAL_PREFIX, 
/*  174 */               actualOuterLocalVariable.name, 
/*  175 */               ("$" + String.valueOf(index++)).toCharArray());
/*  176 */             needRecheck = true;
/*  177 */             break;
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*  164 */     while (
/*  181 */       needRecheck);
/*  182 */     return synthField;
/*      */   }
/*      */ 
/*      */   public FieldBinding addSyntheticFieldForInnerclass(ReferenceBinding enclosingType)
/*      */   {
/*  188 */     if (this.synthetics == null)
/*  189 */       this.synthetics = new HashMap[3];
/*  190 */     if (this.synthetics[1] == null) {
/*  191 */       this.synthetics[1] = new HashMap(5);
/*      */     }
/*  193 */     FieldBinding synthField = (FieldBinding)this.synthetics[1].get(enclosingType);
/*  194 */     if (synthField == null) {
/*  195 */       synthField = new SyntheticFieldBinding(
/*  196 */         CharOperation.concat(
/*  197 */         TypeConstants.SYNTHETIC_ENCLOSING_INSTANCE_PREFIX, 
/*  198 */         String.valueOf(enclosingType.depth()).toCharArray()), 
/*  199 */         enclosingType, 
/*  200 */         4112, 
/*  201 */         this, 
/*  202 */         Constant.NotAConstant, 
/*  203 */         this.synthetics[1].size());
/*  204 */       this.synthetics[1].put(enclosingType, synthField);
/*      */     }
/*      */     boolean needRecheck;
/*      */     do {
/*  209 */       needRecheck = false;
/*      */       FieldBinding existingField;
/*  211 */       if ((existingField = getField(synthField.name, true)) != null) {
/*  212 */         TypeDeclaration typeDecl = this.scope.referenceContext;
/*  213 */         int i = 0; for (int max = typeDecl.fields.length; i < max; i++) {
/*  214 */           FieldDeclaration fieldDecl = typeDecl.fields[i];
/*  215 */           if (fieldDecl.binding == existingField) {
/*  216 */             if (this.scope.compilerOptions().complianceLevel >= 3211264L) {
/*  217 */               synthField.name = CharOperation.concat(
/*  218 */                 synthField.name, 
/*  219 */                 "$".toCharArray());
/*  220 */               needRecheck = true; break;
/*      */             }
/*  222 */             this.scope.problemReporter().duplicateFieldInType(this, fieldDecl);
/*      */ 
/*  224 */             break;
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*  208 */     while (
/*  228 */       needRecheck);
/*  229 */     return synthField;
/*      */   }
/*      */ 
/*      */   public FieldBinding addSyntheticFieldForClassLiteral(TypeBinding targetType, BlockScope blockScope)
/*      */   {
/*  235 */     if (this.synthetics == null)
/*  236 */       this.synthetics = new HashMap[3];
/*  237 */     if (this.synthetics[2] == null) {
/*  238 */       this.synthetics[2] = new HashMap(5);
/*      */     }
/*      */ 
/*  241 */     FieldBinding synthField = (FieldBinding)this.synthetics[2].get(targetType);
/*  242 */     if (synthField == null) {
/*  243 */       synthField = new SyntheticFieldBinding(
/*  244 */         CharOperation.concat(
/*  245 */         TypeConstants.SYNTHETIC_CLASS, 
/*  246 */         String.valueOf(this.synthetics[2].size()).toCharArray()), 
/*  247 */         blockScope.getJavaLangClass(), 
/*  248 */         4104, 
/*  249 */         this, 
/*  250 */         Constant.NotAConstant, 
/*  251 */         this.synthetics[2].size());
/*  252 */       this.synthetics[2].put(targetType, synthField);
/*      */     }
/*      */     FieldBinding existingField;
/*  256 */     if ((existingField = getField(synthField.name, true)) != null) {
/*  257 */       TypeDeclaration typeDecl = blockScope.referenceType();
/*  258 */       int i = 0; for (int max = typeDecl.fields.length; i < max; i++) {
/*  259 */         FieldDeclaration fieldDecl = typeDecl.fields[i];
/*  260 */         if (fieldDecl.binding == existingField) {
/*  261 */           blockScope.problemReporter().duplicateFieldInType(this, fieldDecl);
/*  262 */           break;
/*      */         }
/*      */       }
/*      */     }
/*  266 */     return synthField;
/*      */   }
/*      */ 
/*      */   public FieldBinding addSyntheticFieldForAssert(BlockScope blockScope)
/*      */   {
/*  272 */     if (this.synthetics == null)
/*  273 */       this.synthetics = new HashMap[3];
/*  274 */     if (this.synthetics[1] == null) {
/*  275 */       this.synthetics[1] = new HashMap(5);
/*      */     }
/*  277 */     FieldBinding synthField = (FieldBinding)this.synthetics[1].get("assertionEmulation");
/*  278 */     if (synthField == null) {
/*  279 */       synthField = new SyntheticFieldBinding(
/*  280 */         TypeConstants.SYNTHETIC_ASSERT_DISABLED, 
/*  281 */         TypeBinding.BOOLEAN, 
/*  282 */         4120, 
/*  283 */         this, 
/*  284 */         Constant.NotAConstant, 
/*  285 */         this.synthetics[1].size());
/*  286 */       this.synthetics[1].put("assertionEmulation", synthField);
/*  291 */     }
/*      */ int index = 0;
/*      */     boolean needRecheck;
/*      */     do {
/*  293 */       needRecheck = false;
/*      */       FieldBinding existingField;
/*  295 */       if ((existingField = getField(synthField.name, true)) != null) {
/*  296 */         TypeDeclaration typeDecl = this.scope.referenceContext;
/*  297 */         int i = 0; for (int max = typeDecl.fields.length; i < max; i++) {
/*  298 */           FieldDeclaration fieldDecl = typeDecl.fields[i];
/*  299 */           if (fieldDecl.binding == existingField) {
/*  300 */             synthField.name = CharOperation.concat(
/*  301 */               TypeConstants.SYNTHETIC_ASSERT_DISABLED, 
/*  302 */               ("_" + String.valueOf(index++)).toCharArray());
/*  303 */             needRecheck = true;
/*  304 */             break;
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*  292 */     while (
/*  308 */       needRecheck);
/*  309 */     return synthField;
/*      */   }
/*      */ 
/*      */   public FieldBinding addSyntheticFieldForEnumValues()
/*      */   {
/*  315 */     if (this.synthetics == null)
/*  316 */       this.synthetics = new HashMap[3];
/*  317 */     if (this.synthetics[1] == null) {
/*  318 */       this.synthetics[1] = new HashMap(5);
/*      */     }
/*  320 */     FieldBinding synthField = (FieldBinding)this.synthetics[1].get("enumConstantValues");
/*  321 */     if (synthField == null) {
/*  322 */       synthField = new SyntheticFieldBinding(
/*  323 */         TypeConstants.SYNTHETIC_ENUM_VALUES, 
/*  324 */         this.scope.createArrayType(this, 1), 
/*  325 */         4122, 
/*  326 */         this, 
/*  327 */         Constant.NotAConstant, 
/*  328 */         this.synthetics[1].size());
/*  329 */       this.synthetics[1].put("enumConstantValues", synthField);
/*  334 */     }
/*      */ int index = 0;
/*      */     boolean needRecheck;
/*      */     do {
/*  336 */       needRecheck = false;
/*      */       FieldBinding existingField;
/*  338 */       if ((existingField = getField(synthField.name, true)) != null) {
/*  339 */         TypeDeclaration typeDecl = this.scope.referenceContext;
/*  340 */         int i = 0; for (int max = typeDecl.fields.length; i < max; i++) {
/*  341 */           FieldDeclaration fieldDecl = typeDecl.fields[i];
/*  342 */           if (fieldDecl.binding == existingField) {
/*  343 */             synthField.name = CharOperation.concat(
/*  344 */               TypeConstants.SYNTHETIC_ENUM_VALUES, 
/*  345 */               ("_" + String.valueOf(index++)).toCharArray());
/*  346 */             needRecheck = true;
/*  347 */             break;
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*  335 */     while (
/*  351 */       needRecheck);
/*  352 */     return synthField;
/*      */   }
/*      */ 
/*      */   public SyntheticMethodBinding addSyntheticMethod(FieldBinding targetField, boolean isReadAccess, boolean isSuperAccess)
/*      */   {
/*  358 */     if (this.synthetics == null)
/*  359 */       this.synthetics = new HashMap[3];
/*  360 */     if (this.synthetics[0] == null) {
/*  361 */       this.synthetics[0] = new HashMap(5);
/*      */     }
/*  363 */     SyntheticMethodBinding accessMethod = null;
/*  364 */     SyntheticMethodBinding[] accessors = (SyntheticMethodBinding[])this.synthetics[0].get(targetField);
/*  365 */     if (accessors == null) {
/*  366 */       accessMethod = new SyntheticMethodBinding(targetField, isReadAccess, isSuperAccess, this);
/*  367 */       this.synthetics[0].put(targetField, accessors = new SyntheticMethodBinding[2]);
/*  368 */       accessors[(isReadAccess ? 0 : 1)] = accessMethod;
/*      */     }
/*  370 */     else if ((accessMethod = accessors[1]) == null) {
/*  371 */       accessMethod = new SyntheticMethodBinding(targetField, isReadAccess, isSuperAccess, this);
/*  372 */       accessors[(isReadAccess ? 0 : 1)] = accessMethod;
/*      */     }
/*      */ 
/*  375 */     return accessMethod;
/*      */   }
/*      */ 
/*      */   public SyntheticMethodBinding addSyntheticEnumMethod(char[] selector)
/*      */   {
/*  381 */     if (this.synthetics == null)
/*  382 */       this.synthetics = new HashMap[3];
/*  383 */     if (this.synthetics[0] == null) {
/*  384 */       this.synthetics[0] = new HashMap(5);
/*      */     }
/*  386 */     SyntheticMethodBinding accessMethod = null;
/*  387 */     SyntheticMethodBinding[] accessors = (SyntheticMethodBinding[])this.synthetics[0].get(selector);
/*  388 */     if (accessors == null) {
/*  389 */       accessMethod = new SyntheticMethodBinding(this, selector);
/*  390 */       this.synthetics[0].put(selector, accessors = new SyntheticMethodBinding[2]);
/*  391 */       accessors[0] = accessMethod;
/*      */     }
/*  393 */     else if ((accessMethod = accessors[0]) == null) {
/*  394 */       accessMethod = new SyntheticMethodBinding(this, selector);
/*  395 */       accessors[0] = accessMethod;
/*      */     }
/*      */ 
/*  398 */     return accessMethod;
/*      */   }
/*      */ 
/*      */   public SyntheticFieldBinding addSyntheticFieldForSwitchEnum(char[] fieldName, String key)
/*      */   {
/*  404 */     if (this.synthetics == null)
/*  405 */       this.synthetics = new HashMap[3];
/*  406 */     if (this.synthetics[1] == null) {
/*  407 */       this.synthetics[1] = new HashMap(5);
/*      */     }
/*  409 */     SyntheticFieldBinding synthField = (SyntheticFieldBinding)this.synthetics[1].get(key);
/*  410 */     if (synthField == null) {
/*  411 */       synthField = new SyntheticFieldBinding(
/*  412 */         fieldName, 
/*  413 */         this.scope.createArrayType(TypeBinding.INT, 1), 
/*  414 */         4106, 
/*  415 */         this, 
/*  416 */         Constant.NotAConstant, 
/*  417 */         this.synthetics[1].size());
/*  418 */       this.synthetics[1].put(key, synthField);
/*  422 */     }
/*      */ int index = 0;
/*      */     boolean needRecheck;
/*      */     do { needRecheck = false;
/*      */       FieldBinding existingField;
/*  426 */       if ((existingField = getField(synthField.name, true)) != null) {
/*  427 */         TypeDeclaration typeDecl = this.scope.referenceContext;
/*  428 */         int i = 0; for (int max = typeDecl.fields.length; i < max; i++) {
/*  429 */           FieldDeclaration fieldDecl = typeDecl.fields[i];
/*  430 */           if (fieldDecl.binding == existingField) {
/*  431 */             synthField.name = CharOperation.concat(
/*  432 */               fieldName, 
/*  433 */               ("_" + String.valueOf(index++)).toCharArray());
/*  434 */             needRecheck = true;
/*  435 */             break;
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*  423 */     while (
/*  439 */       needRecheck);
/*  440 */     return synthField;
/*      */   }
/*      */ 
/*      */   public SyntheticMethodBinding addSyntheticMethodForSwitchEnum(TypeBinding enumBinding)
/*      */   {
/*  446 */     if (this.synthetics == null)
/*  447 */       this.synthetics = new HashMap[3];
/*  448 */     if (this.synthetics[0] == null) {
/*  449 */       this.synthetics[0] = new HashMap(5);
/*      */     }
/*  451 */     SyntheticMethodBinding accessMethod = null;
/*  452 */     char[] selector = CharOperation.concat(TypeConstants.SYNTHETIC_SWITCH_ENUM_TABLE, enumBinding.constantPoolName());
/*  453 */     CharOperation.replace(selector, '/', '$');
/*  454 */     String key = new String(selector);
/*  455 */     SyntheticMethodBinding[] accessors = (SyntheticMethodBinding[])this.synthetics[0].get(key);
/*      */ 
/*  457 */     if (accessors == null)
/*      */     {
/*  459 */       SyntheticFieldBinding fieldBinding = addSyntheticFieldForSwitchEnum(selector, key);
/*  460 */       accessMethod = new SyntheticMethodBinding(fieldBinding, this, enumBinding, selector);
/*  461 */       this.synthetics[0].put(key, accessors = new SyntheticMethodBinding[2]);
/*  462 */       accessors[0] = accessMethod;
/*      */     }
/*  464 */     else if ((accessMethod = accessors[0]) == null) {
/*  465 */       SyntheticFieldBinding fieldBinding = addSyntheticFieldForSwitchEnum(selector, key);
/*  466 */       accessMethod = new SyntheticMethodBinding(fieldBinding, this, enumBinding, selector);
/*  467 */       accessors[0] = accessMethod;
/*      */     }
/*      */ 
/*  470 */     return accessMethod;
/*      */   }
/*      */ 
/*      */   public SyntheticMethodBinding addSyntheticMethod(MethodBinding targetMethod, boolean isSuperAccess)
/*      */   {
/*  477 */     if (this.synthetics == null)
/*  478 */       this.synthetics = new HashMap[3];
/*  479 */     if (this.synthetics[0] == null) {
/*  480 */       this.synthetics[0] = new HashMap(5);
/*      */     }
/*  482 */     SyntheticMethodBinding accessMethod = null;
/*  483 */     SyntheticMethodBinding[] accessors = (SyntheticMethodBinding[])this.synthetics[0].get(targetMethod);
/*  484 */     if (accessors == null) {
/*  485 */       accessMethod = new SyntheticMethodBinding(targetMethod, isSuperAccess, this);
/*  486 */       this.synthetics[0].put(targetMethod, accessors = new SyntheticMethodBinding[2]);
/*  487 */       accessors[(isSuperAccess ? 0 : 1)] = accessMethod;
/*      */     }
/*  489 */     else if ((accessMethod = accessors[1]) == null) {
/*  490 */       accessMethod = new SyntheticMethodBinding(targetMethod, isSuperAccess, this);
/*  491 */       accessors[(isSuperAccess ? 0 : 1)] = accessMethod;
/*      */     }
/*      */ 
/*  494 */     return accessMethod;
/*      */   }
/*      */ 
/*      */   public SyntheticMethodBinding addSyntheticBridgeMethod(MethodBinding inheritedMethodToBridge, MethodBinding targetMethod)
/*      */   {
/*  500 */     if (isInterface()) return null;
/*      */ 
/*  502 */     if ((inheritedMethodToBridge.returnType.erasure() == targetMethod.returnType.erasure()) && 
/*  503 */       (inheritedMethodToBridge.areParameterErasuresEqual(targetMethod))) {
/*  504 */       return null;
/*      */     }
/*  506 */     if (this.synthetics == null)
/*  507 */       this.synthetics = new HashMap[3];
/*  508 */     if (this.synthetics[0] == null) {
/*  509 */       this.synthetics[0] = new HashMap(5);
/*      */     }
/*      */     else {
/*  512 */       Iterator synthMethods = this.synthetics[0].keySet().iterator();
/*  513 */       while (synthMethods.hasNext()) {
/*  514 */         Object synthetic = synthMethods.next();
/*  515 */         if ((synthetic instanceof MethodBinding)) {
/*  516 */           MethodBinding method = (MethodBinding)synthetic;
/*  517 */           if ((CharOperation.equals(inheritedMethodToBridge.selector, method.selector)) && 
/*  518 */             (inheritedMethodToBridge.returnType.erasure() == method.returnType.erasure()) && 
/*  519 */             (inheritedMethodToBridge.areParameterErasuresEqual(method))) {
/*  520 */             return null;
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */ 
/*  526 */     SyntheticMethodBinding accessMethod = null;
/*  527 */     SyntheticMethodBinding[] accessors = (SyntheticMethodBinding[])this.synthetics[0].get(inheritedMethodToBridge);
/*  528 */     if (accessors == null) {
/*  529 */       accessMethod = new SyntheticMethodBinding(inheritedMethodToBridge, targetMethod, this);
/*  530 */       this.synthetics[0].put(inheritedMethodToBridge, accessors = new SyntheticMethodBinding[2]);
/*  531 */       accessors[1] = accessMethod;
/*      */     }
/*  533 */     else if ((accessMethod = accessors[1]) == null) {
/*  534 */       accessMethod = new SyntheticMethodBinding(inheritedMethodToBridge, targetMethod, this);
/*  535 */       accessors[1] = accessMethod;
/*      */     }
/*      */ 
/*  538 */     return accessMethod;
/*      */   }
/*      */   boolean areFieldsInitialized() {
/*  541 */     return this.fields != Binding.UNINITIALIZED_FIELDS;
/*      */   }
/*      */   boolean areMethodsInitialized() {
/*  544 */     return this.methods != Binding.UNINITIALIZED_METHODS;
/*      */   }
/*      */   public int kind() {
/*  547 */     if (this.typeVariables != Binding.NO_TYPE_VARIABLES) return 2052;
/*  548 */     return 4;
/*      */   }
/*      */ 
/*      */   public char[] computeUniqueKey(boolean isLeaf) {
/*  552 */     char[] uniqueKey = super.computeUniqueKey(isLeaf);
/*  553 */     if (uniqueKey.length == 2) return uniqueKey;
/*  554 */     if (Util.isClassFileName(this.fileName)) return uniqueKey;
/*      */ 
/*  557 */     int end = CharOperation.lastIndexOf('.', this.fileName);
/*  558 */     if (end != -1) {
/*  559 */       int start = CharOperation.lastIndexOf('/', this.fileName) + 1;
/*  560 */       char[] mainTypeName = CharOperation.subarray(this.fileName, start, end);
/*  561 */       start = CharOperation.lastIndexOf('/', uniqueKey) + 1;
/*  562 */       if (start == 0)
/*  563 */         start = 1;
/*  564 */       end = CharOperation.indexOf('$', uniqueKey, start);
/*  565 */       if (end == -1)
/*  566 */         end = CharOperation.indexOf('<', uniqueKey, start);
/*  567 */       if (end == -1)
/*  568 */         end = CharOperation.indexOf(';', uniqueKey, start);
/*  569 */       char[] topLevelType = CharOperation.subarray(uniqueKey, start, end);
/*  570 */       if (!CharOperation.equals(topLevelType, mainTypeName)) {
/*  571 */         StringBuffer buffer = new StringBuffer();
/*  572 */         buffer.append(uniqueKey, 0, start);
/*  573 */         buffer.append(mainTypeName);
/*  574 */         buffer.append('~');
/*  575 */         buffer.append(topLevelType);
/*  576 */         buffer.append(uniqueKey, end, uniqueKey.length - end);
/*  577 */         int length = buffer.length();
/*  578 */         uniqueKey = new char[length];
/*  579 */         buffer.getChars(0, length, uniqueKey, 0);
/*  580 */         return uniqueKey;
/*      */       }
/*      */     }
/*  583 */     return uniqueKey;
/*      */   }
/*      */ 
/*      */   void faultInTypesForFieldsAndMethods()
/*      */   {
/*  588 */     getAnnotationTagBits();
/*  589 */     ReferenceBinding enclosingType = enclosingType();
/*  590 */     if ((enclosingType != null) && (enclosingType.isViewedAsDeprecated()) && (!isDeprecated()))
/*  591 */       this.modifiers |= 2097152;
/*  592 */     fields();
/*  593 */     methods();
/*      */ 
/*  595 */     int i = 0; for (int length = this.memberTypes.length; i < length; i++)
/*  596 */       ((SourceTypeBinding)this.memberTypes[i]).faultInTypesForFieldsAndMethods();
/*      */   }
/*      */ 
/*      */   public FieldBinding[] fields() {
/*  600 */     if ((this.tagBits & 0x2000) != 0L) {
/*  601 */       return this.fields; } int failed = 0;
/*  604 */     FieldBinding[] resolvedFields = this.fields;
/*      */     int newSize;
/*      */     FieldBinding[] newFields;
/*      */     int i;
/*      */     int j;
/*      */     int length;
/*      */     try { if ((this.tagBits & 0x1000) == 0L) {
/*  608 */         int length = this.fields.length;
/*  609 */         if (length > 1)
/*  610 */           ReferenceBinding.sortFields(this.fields, 0, length);
/*  611 */         this.tagBits |= 4096L;
/*      */       }
/*  613 */       int i = 0; for (int length = this.fields.length; i < length; i++) {
/*  614 */         if (resolveTypeFor(this.fields[i]) != null)
/*      */           continue;
/*  616 */         if (resolvedFields == this.fields) {
/*  617 */           System.arraycopy(this.fields, 0, resolvedFields = new FieldBinding[length], 0, length);
/*      */         }
/*  619 */         resolvedFields[i] = null;
/*  620 */         failed++;
/*      */       }
/*      */     } finally
/*      */     {
/*  624 */       if (failed > 0)
/*      */       {
/*  626 */         int newSize = resolvedFields.length - failed;
/*  627 */         if (newSize == 0) {
/*  628 */           return this.fields = Binding.NO_FIELDS;
/*      */         }
/*  630 */         FieldBinding[] newFields = new FieldBinding[newSize];
/*  631 */         int i = 0; int j = 0; for (int length = resolvedFields.length; i < length; i++) {
/*  632 */           if (resolvedFields[i] != null)
/*  633 */             newFields[(j++)] = resolvedFields[i];
/*      */         }
/*  635 */         this.fields = newFields;
/*      */       }
/*      */     }
/*  638 */     this.tagBits |= 8192L;
/*  639 */     return this.fields;
/*      */   }
/*      */ 
/*      */   public char[] genericTypeSignature()
/*      */   {
/*  645 */     if (this.genericReferenceTypeSignature == null)
/*  646 */       this.genericReferenceTypeSignature = computeGenericTypeSignature(this.typeVariables);
/*  647 */     return this.genericReferenceTypeSignature;
/*      */   }
/*      */ 
/*      */   public char[] genericSignature()
/*      */   {
/*  654 */     StringBuffer sig = null;
/*  655 */     if (this.typeVariables != Binding.NO_TYPE_VARIABLES) {
/*  656 */       sig = new StringBuffer(10);
/*  657 */       sig.append('<');
/*  658 */       int i = 0; for (int length = this.typeVariables.length; i < length; i++)
/*  659 */         sig.append(this.typeVariables[i].genericSignature());
/*  660 */       sig.append('>');
/*      */     }
/*      */     else {
/*  663 */       if ((this.superclass == null) || (!this.superclass.isParameterizedType())) {
/*  664 */         int i = 0; int length = this.superInterfaces.length;
/*  665 */         while (!this.superInterfaces[i].isParameterizedType())
/*      */         {
/*  664 */           i++; if (i >= length)
/*      */           {
/*  667 */             return null;
/*      */           }
/*      */         }
/*      */       }
/*  669 */       sig = new StringBuffer(10);
/*      */     }
/*  671 */     if (this.superclass != null)
/*  672 */       sig.append(this.superclass.genericTypeSignature());
/*      */     else
/*  674 */       sig.append(this.scope.getJavaLangObject().genericTypeSignature());
/*  675 */     int i = 0; for (int length = this.superInterfaces.length; i < length; i++)
/*  676 */       sig.append(this.superInterfaces[i].genericTypeSignature());
/*  677 */     return sig.toString().toCharArray();
/*      */   }
/*      */ 
/*      */   public long getAnnotationTagBits()
/*      */   {
/*  686 */     if (((this.tagBits & 0x0) == 0L) && (this.scope != null)) {
/*  687 */       TypeDeclaration typeDecl = this.scope.referenceContext;
/*  688 */       boolean old = typeDecl.staticInitializerScope.insideTypeAnnotation;
/*      */       try {
/*  690 */         typeDecl.staticInitializerScope.insideTypeAnnotation = true;
/*  691 */         ASTNode.resolveAnnotations(typeDecl.staticInitializerScope, typeDecl.annotations, this);
/*      */       } finally {
/*  693 */         typeDecl.staticInitializerScope.insideTypeAnnotation = old;
/*      */       }
/*  695 */       if ((this.tagBits & 0x0) != 0L)
/*  696 */         this.modifiers |= 1048576;
/*      */     }
/*  698 */     return this.tagBits;
/*      */   }
/*      */   public MethodBinding[] getDefaultAbstractMethods() {
/*  701 */     int count = 0;
/*  702 */     int i = this.methods.length;
/*      */     do { if (this.methods[i].isDefaultAbstract())
/*  704 */         count++;
/*  702 */       i--; } while (i >= 0);
/*      */ 
/*  705 */     if (count == 0) return Binding.NO_METHODS;
/*      */ 
/*  707 */     MethodBinding[] result = new MethodBinding[count];
/*  708 */     count = 0;
/*  709 */     int i = this.methods.length;
/*      */     do { if (this.methods[i].isDefaultAbstract())
/*  711 */         result[(count++)] = this.methods[i];
/*  709 */       i--; } while (i >= 0);
/*      */ 
/*  712 */     return result;
/*      */   }
/*      */ 
/*      */   public MethodBinding getExactConstructor(TypeBinding[] argumentTypes) {
/*  716 */     int argCount = argumentTypes.length;
/*  717 */     if ((this.tagBits & 0x8000) != 0L)
/*      */     {
/*      */       long range;
/*  719 */       if ((range = ReferenceBinding.binarySearch(TypeConstants.INIT, this.methods)) >= 0L) {
/*  720 */         int imethod = (int)range; for (int end = (int)(range >> 32); imethod <= end; imethod++) {
/*  721 */           MethodBinding method = this.methods[imethod];
/*  722 */           if (method.parameters.length == argCount) {
/*  723 */             TypeBinding[] toMatch = method.parameters;
/*  724 */             int iarg = 0;
/*  725 */             while (toMatch[iarg] == argumentTypes[iarg])
/*      */             {
/*  724 */               iarg++; if (iarg >= argCount)
/*      */               {
/*  727 */                 return method;
/*      */               }
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/*      */     } else {
/*  733 */       if ((this.tagBits & 0x4000) == 0L) {
/*  734 */         int length = this.methods.length;
/*  735 */         if (length > 1)
/*  736 */           ReferenceBinding.sortMethods(this.methods, 0, length);
/*  737 */         this.tagBits |= 16384L;
/*      */       }
/*      */       long range;
/*  740 */       if ((range = ReferenceBinding.binarySearch(TypeConstants.INIT, this.methods)) >= 0L) {
/*  741 */         int imethod = (int)range; for (int end = (int)(range >> 32); imethod <= end; imethod++) {
/*  742 */           MethodBinding method = this.methods[imethod];
/*  743 */           if ((resolveTypesFor(method) == null) || (method.returnType == null)) {
/*  744 */             methods();
/*  745 */             return getExactConstructor(argumentTypes);
/*      */           }
/*  747 */           if (method.parameters.length == argCount) {
/*  748 */             TypeBinding[] toMatch = method.parameters;
/*  749 */             int iarg = 0;
/*  750 */             while (toMatch[iarg] == argumentTypes[iarg])
/*      */             {
/*  749 */               iarg++; if (iarg >= argCount)
/*      */               {
/*  752 */                 return method;
/*      */               }
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*  757 */     return null;
/*      */   }
/*      */ 
/*      */   public MethodBinding getExactMethod(char[] selector, TypeBinding[] argumentTypes, CompilationUnitScope refScope)
/*      */   {
/*  764 */     int argCount = argumentTypes.length;
/*  765 */     boolean foundNothing = true;
/*      */ 
/*  767 */     if ((this.tagBits & 0x8000) != 0L)
/*      */     {
/*      */       long range;
/*  769 */       if ((range = ReferenceBinding.binarySearch(selector, this.methods)) >= 0L) {
/*  770 */         int imethod = (int)range; for (int end = (int)(range >> 32); imethod <= end; imethod++) {
/*  771 */           MethodBinding method = this.methods[imethod];
/*  772 */           foundNothing = false;
/*  773 */           if (method.parameters.length == argCount) {
/*  774 */             TypeBinding[] toMatch = method.parameters;
/*  775 */             int iarg = 0;
/*  776 */             while (toMatch[iarg] == argumentTypes[iarg])
/*      */             {
/*  775 */               iarg++; if (iarg >= argCount)
/*      */               {
/*  778 */                 return method;
/*      */               }
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/*      */     } else {
/*  784 */       if ((this.tagBits & 0x4000) == 0L) {
/*  785 */         int length = this.methods.length;
/*  786 */         if (length > 1)
/*  787 */           ReferenceBinding.sortMethods(this.methods, 0, length);
/*  788 */         this.tagBits |= 16384L;
/*      */       }
/*      */       long range;
/*  792 */       if ((range = ReferenceBinding.binarySearch(selector, this.methods)) >= 0L)
/*      */       {
/*  794 */         int start = (int)range; int end = (int)(range >> 32);
/*  795 */         for (int imethod = start; imethod <= end; imethod++) {
/*  796 */           MethodBinding method = this.methods[imethod];
/*  797 */           if ((resolveTypesFor(method) == null) || (method.returnType == null)) {
/*  798 */             methods();
/*  799 */             return getExactMethod(selector, argumentTypes, refScope);
/*      */           }
/*      */         }
/*      */ 
/*  803 */         boolean isSource15 = this.scope.compilerOptions().sourceLevel >= 3211264L;
/*  804 */         for (int i = start; i <= end; i++) {
/*  805 */           MethodBinding method1 = this.methods[i];
/*  806 */           for (int j = end; j > i; j--) {
/*  807 */             MethodBinding method2 = this.methods[j];
/*  808 */             boolean paramsMatch = isSource15 ? 
/*  809 */               method1.areParameterErasuresEqual(method2) : 
/*  810 */               method1.areParametersEqual(method2);
/*  811 */             if (paramsMatch) {
/*  812 */               methods();
/*  813 */               return getExactMethod(selector, argumentTypes, refScope);
/*      */             }
/*      */           }
/*      */         }
/*  817 */         for (int imethod = start; imethod <= end; imethod++) {
/*  818 */           MethodBinding method = this.methods[imethod];
/*  819 */           TypeBinding[] toMatch = method.parameters;
/*  820 */           if (toMatch.length == argCount) {
/*  821 */             int iarg = 0;
/*  822 */             while (toMatch[iarg] == argumentTypes[iarg])
/*      */             {
/*  821 */               iarg++; if (iarg >= argCount)
/*      */               {
/*  824 */                 return method;
/*      */               }
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*  830 */     if (foundNothing) {
/*  831 */       if (isInterface()) {
/*  832 */         if (this.superInterfaces.length == 1) {
/*  833 */           if (refScope != null)
/*  834 */             refScope.recordTypeReference(this.superInterfaces[0]);
/*  835 */           return this.superInterfaces[0].getExactMethod(selector, argumentTypes, refScope);
/*      */         }
/*  837 */       } else if (this.superclass != null) {
/*  838 */         if (refScope != null)
/*  839 */           refScope.recordTypeReference(this.superclass);
/*  840 */         return this.superclass.getExactMethod(selector, argumentTypes, refScope);
/*      */       }
/*      */     }
/*  843 */     return null;
/*      */   }
/*      */ 
/*      */   public FieldBinding getField(char[] fieldName, boolean needResolve)
/*      */   {
/*  849 */     if ((this.tagBits & 0x2000) != 0L) {
/*  850 */       return ReferenceBinding.binarySearch(fieldName, this.fields);
/*      */     }
/*      */ 
/*  853 */     if ((this.tagBits & 0x1000) == 0L) {
/*  854 */       int length = this.fields.length;
/*  855 */       if (length > 1)
/*  856 */         ReferenceBinding.sortFields(this.fields, 0, length);
/*  857 */       this.tagBits |= 4096L;
/*      */     }
/*      */ 
/*  860 */     FieldBinding field = ReferenceBinding.binarySearch(fieldName, this.fields);
/*  861 */     if (field != null) {
/*  862 */       FieldBinding result = null;
/*      */       try {
/*  864 */         result = resolveTypeFor(field);
/*  865 */         FieldBinding localFieldBinding1 = result;
/*      */         int newSize;
/*      */         FieldBinding[] newFields;
/*      */         int index;
/*      */         int i;
/*      */         int length;
/*      */         FieldBinding f;
/*      */         return localFieldBinding1;
/*      */       } finally {
/*  867 */         if (result == null)
/*      */         {
/*  869 */           int newSize = this.fields.length - 1;
/*  870 */           if (newSize == 0) {
/*  871 */             this.fields = Binding.NO_FIELDS;
/*      */           } else {
/*  873 */             FieldBinding[] newFields = new FieldBinding[newSize];
/*  874 */             int index = 0;
/*  875 */             int i = 0; for (int length = this.fields.length; i < length; i++) {
/*  876 */               FieldBinding f = this.fields[i];
/*  877 */               if (f != field)
/*  878 */                 newFields[(index++)] = f;
/*      */             }
/*  880 */             this.fields = newFields;
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*  885 */     return null;
/*      */   }
/*      */ 
/*      */   public MethodBinding[] getMethods(char[] selector)
/*      */   {
/*  890 */     if ((this.tagBits & 0x8000) != 0L)
/*      */     {
/*      */       long range;
/*  892 */       if ((range = ReferenceBinding.binarySearch(selector, this.methods)) >= 0L) {
/*  893 */         int start = (int)range; int end = (int)(range >> 32);
/*  894 */         int length = end - start + 1;
/*      */         MethodBinding[] result;
/*  896 */         System.arraycopy(this.methods, start, result = new MethodBinding[length], 0, length);
/*  897 */         return result;
/*      */       }
/*  899 */       return Binding.NO_METHODS;
/*      */     }
/*      */ 
/*  903 */     if ((this.tagBits & 0x4000) == 0L) {
/*  904 */       int length = this.methods.length;
/*  905 */       if (length > 1)
/*  906 */         ReferenceBinding.sortMethods(this.methods, 0, length);
/*  907 */       this.tagBits |= 16384L;
/*      */     }
/*      */     long range;
/*  911 */     if ((range = ReferenceBinding.binarySearch(selector, this.methods)) >= 0L) {
/*  912 */       int start = (int)range; int end = (int)(range >> 32);
/*  913 */       for (int i = start; i <= end; i++) {
/*  914 */         MethodBinding method = this.methods[i];
/*  915 */         if ((resolveTypesFor(method) == null) || (method.returnType == null)) {
/*  916 */           methods();
/*  917 */           return getMethods(selector);
/*      */         }
/*      */       }
/*  920 */       int length = end - start + 1;
/*      */       MethodBinding[] result;
/*  921 */       System.arraycopy(this.methods, start, result = new MethodBinding[length], 0, length);
/*      */     } else {
/*  923 */       return Binding.NO_METHODS;
/*      */     }
/*      */     MethodBinding[] result;
/*  925 */     boolean isSource15 = this.scope.compilerOptions().sourceLevel >= 3211264L;
/*  926 */     int i = 0; for (int length = result.length - 1; i < length; i++) {
/*  927 */       MethodBinding method = result[i];
/*  928 */       for (int j = length; j > i; j--) {
/*  929 */         boolean paramsMatch = isSource15 ? 
/*  930 */           method.areParameterErasuresEqual(result[j]) : 
/*  931 */           method.areParametersEqual(result[j]);
/*  932 */         if (paramsMatch) {
/*  933 */           methods();
/*  934 */           return getMethods(selector);
/*      */         }
/*      */       }
/*      */     }
/*  938 */     return result;
/*      */   }
/*      */ 
/*      */   public FieldBinding getSyntheticField(LocalVariableBinding actualOuterLocalVariable)
/*      */   {
/*  944 */     if ((this.synthetics == null) || (this.synthetics[1] == null)) return null;
/*  945 */     return (FieldBinding)this.synthetics[1].get(actualOuterLocalVariable);
/*      */   }
/*      */ 
/*      */   public FieldBinding getSyntheticField(ReferenceBinding targetEnclosingType, boolean onlyExactMatch)
/*      */   {
/*  952 */     if ((this.synthetics == null) || (this.synthetics[1] == null)) return null;
/*  953 */     FieldBinding field = (FieldBinding)this.synthetics[1].get(targetEnclosingType);
/*  954 */     if (field != null) return field;
/*      */ 
/*  959 */     if (!onlyExactMatch) {
/*  960 */       Iterator accessFields = this.synthetics[1].values().iterator();
/*  961 */       while (accessFields.hasNext()) {
/*  962 */         field = (FieldBinding)accessFields.next();
/*  963 */         if ((CharOperation.prefixEquals(TypeConstants.SYNTHETIC_ENCLOSING_INSTANCE_PREFIX, field.name)) && 
/*  964 */           (field.type.findSuperTypeOriginatingFrom(targetEnclosingType) != null))
/*  965 */           return field;
/*      */       }
/*      */     }
/*  968 */     return null;
/*      */   }
/*      */ 
/*      */   public SyntheticMethodBinding getSyntheticBridgeMethod(MethodBinding inheritedMethodToBridge)
/*      */   {
/*  974 */     if (this.synthetics == null) return null;
/*  975 */     if (this.synthetics[0] == null) return null;
/*  976 */     SyntheticMethodBinding[] accessors = (SyntheticMethodBinding[])this.synthetics[0].get(inheritedMethodToBridge);
/*  977 */     if (accessors == null) return null;
/*  978 */     return accessors[1];
/*      */   }
/*      */ 
/*      */   public void initializeDeprecatedAnnotationTagBits()
/*      */   {
/*  985 */     if ((this.tagBits & 0x0) == 0L) {
/*  986 */       TypeDeclaration typeDecl = this.scope.referenceContext;
/*  987 */       boolean old = typeDecl.staticInitializerScope.insideTypeAnnotation;
/*      */       try {
/*  989 */         typeDecl.staticInitializerScope.insideTypeAnnotation = true;
/*  990 */         ASTNode.resolveDeprecatedAnnotations(typeDecl.staticInitializerScope, typeDecl.annotations, this);
/*  991 */         this.tagBits |= 17179869184L;
/*      */       } finally {
/*  993 */         typeDecl.staticInitializerScope.insideTypeAnnotation = old;
/*      */       }
/*  995 */       if ((this.tagBits & 0x0) != 0L)
/*  996 */         this.modifiers |= 1048576;
/*      */     }
/*      */   }
/*      */ 
/*      */   void initializeForStaticImports()
/*      */   {
/* 1004 */     if (this.scope == null) return;
/*      */ 
/* 1006 */     if (this.superInterfaces == null)
/* 1007 */       this.scope.connectTypeHierarchy();
/* 1008 */     this.scope.buildFields();
/* 1009 */     this.scope.buildMethods();
/*      */   }
/*      */ 
/*      */   public boolean isEquivalentTo(TypeBinding otherType)
/*      */   {
/* 1018 */     if (this == otherType) return true;
/* 1019 */     if (otherType == null) return false;
/* 1020 */     switch (otherType.kind())
/*      */     {
/*      */     case 516:
/*      */     case 8196:
/* 1024 */       return ((WildcardBinding)otherType).boundCheck(this);
/*      */     case 260:
/* 1027 */       if (((otherType.tagBits & 0x40000000) == 0L) && ((!isMemberType()) || (!otherType.isMemberType())))
/* 1028 */         return false;
/* 1029 */       ParameterizedTypeBinding otherParamType = (ParameterizedTypeBinding)otherType;
/* 1030 */       if (this != otherParamType.genericType())
/* 1031 */         return false;
/* 1032 */       if (!isStatic()) {
/* 1033 */         ReferenceBinding enclosing = enclosingType();
/* 1034 */         if (enclosing != null) {
/* 1035 */           ReferenceBinding otherEnclosing = otherParamType.enclosingType();
/* 1036 */           if (otherEnclosing == null) return false;
/* 1037 */           if ((otherEnclosing.tagBits & 0x40000000) == 0L) {
/* 1038 */             if (enclosing != otherEnclosing) return false;
/*      */           }
/* 1040 */           else if (!enclosing.isEquivalentTo(otherParamType.enclosingType())) return false;
/*      */         }
/*      */       }
/*      */ 
/* 1044 */       int length = this.typeVariables == null ? 0 : this.typeVariables.length;
/* 1045 */       TypeBinding[] otherArguments = otherParamType.arguments;
/* 1046 */       int otherLength = otherArguments == null ? 0 : otherArguments.length;
/* 1047 */       if (otherLength != length)
/* 1048 */         return false;
/* 1049 */       for (int i = 0; i < length; i++)
/* 1050 */         if (!this.typeVariables[i].isTypeArgumentContainedBy(otherArguments[i]))
/* 1051 */           return false;
/* 1052 */       return true;
/*      */     case 1028:
/* 1055 */       return otherType.erasure() == this;
/*      */     }
/* 1057 */     return false;
/*      */   }
/*      */   public boolean isGenericType() {
/* 1060 */     return this.typeVariables != Binding.NO_TYPE_VARIABLES;
/*      */   }
/*      */   public boolean isHierarchyConnected() {
/* 1063 */     return (this.tagBits & 0x200) != 0L;
/*      */   }
/*      */   public ReferenceBinding[] memberTypes() {
/* 1066 */     return this.memberTypes;
/*      */   }
/*      */ 
/*      */   public boolean hasMemberTypes() {
/* 1070 */     return this.memberTypes.length > 0;
/*      */   }
/*      */ 
/*      */   public MethodBinding[] methods()
/*      */   {
/* 1075 */     if ((this.tagBits & 0x8000) != 0L) {
/* 1076 */       return this.methods;
/*      */     }
/*      */ 
/* 1079 */     if ((this.tagBits & 0x4000) == 0L) {
/* 1080 */       int length = this.methods.length;
/* 1081 */       if (length > 1)
/* 1082 */         ReferenceBinding.sortMethods(this.methods, 0, length);
/* 1083 */       this.tagBits |= 16384L; } 
/*      */ int failed = 0;
/* 1087 */     MethodBinding[] resolvedMethods = this.methods;
/*      */     int newSize;
/*      */     MethodBinding[] newMethods;
/*      */     int i;
/*      */     int j;
/*      */     int length;
/*      */     try { int i = 0; for (int length = this.methods.length; i < length; i++) {
/* 1090 */         if (resolveTypesFor(this.methods[i]) != null)
/*      */           continue;
/* 1092 */         if (resolvedMethods == this.methods) {
/* 1093 */           System.arraycopy(this.methods, 0, resolvedMethods = new MethodBinding[length], 0, length);
/*      */         }
/* 1095 */         resolvedMethods[i] = null;
/* 1096 */         failed++;
/*      */       }
/*      */ 
/* 1101 */       boolean complyTo15 = this.scope.compilerOptions().sourceLevel >= 3211264L;
/* 1102 */       boolean complyTo17 = this.scope.compilerOptions().sourceLevel >= 3342336L;
/* 1103 */       int i = 0; for (int length = this.methods.length; i < length; i++) {
/* 1104 */         MethodBinding method = resolvedMethods[i];
/* 1105 */         if (method == null)
/*      */           continue;
/* 1107 */         char[] selector = method.selector;
/* 1108 */         AbstractMethodDeclaration methodDecl = null;
/* 1109 */         for (int j = i + 1; j < length; j++) {
/* 1110 */           MethodBinding method2 = resolvedMethods[j];
/* 1111 */           if (method2 == null)
/*      */             continue;
/* 1113 */           if (!CharOperation.equals(selector, method2.selector)) {
/*      */             break;
/*      */           }
/* 1116 */           if ((complyTo15) && (method.returnType != null) && (method2.returnType != null))
/*      */           {
/* 1120 */             TypeBinding[] params1 = method.parameters;
/* 1121 */             TypeBinding[] params2 = method2.parameters;
/* 1122 */             int pLength = params1.length;
/* 1123 */             if (pLength != params2.length) {
/*      */               continue;
/*      */             }
/* 1126 */             TypeVariableBinding[] vars = method.typeVariables;
/* 1127 */             TypeVariableBinding[] vars2 = method2.typeVariables;
/* 1128 */             boolean equalTypeVars = vars == vars2;
/* 1129 */             MethodBinding subMethod = method2;
/* 1130 */             if (!equalTypeVars) {
/* 1131 */               MethodBinding temp = method.computeSubstitutedMethod(method2, this.scope.environment());
/* 1132 */               if (temp != null) {
/* 1133 */                 equalTypeVars = true;
/* 1134 */                 subMethod = temp;
/*      */               }
/*      */             }
/* 1137 */             boolean equalParams = method.areParametersEqual(subMethod);
/* 1138 */             if ((!equalParams) || (!equalTypeVars))
/*      */             {
/* 1140 */               if (((!complyTo17) && (method.returnType.erasure() != subMethod.returnType.erasure())) || (
/* 1141 */                 (!equalParams) && (!method.areParameterErasuresEqual(method2))))
/*      */               {
/* 1146 */                 if ((!equalTypeVars) && (vars != Binding.NO_TYPE_VARIABLES) && (vars2 != Binding.NO_TYPE_VARIABLES)) {
/*      */                   continue;
/*      */                 }
/* 1149 */                 if (pLength > 0)
/*      */                 {
/* 1151 */                   int index = pLength;
/*      */                   do {
/* 1153 */                     if (params1[index] != params2[index].erasure())
/*      */                       break;
/* 1155 */                     if (params1[index] == params2[index]) {
/* 1156 */                       TypeBinding type = params1[index].leafComponentType();
/* 1157 */                       if (((type instanceof SourceTypeBinding)) && (type.typeVariables() != Binding.NO_TYPE_VARIABLES)) {
/* 1158 */                         index = pLength;
/* 1159 */                         break;
/*      */                       }
/*      */                     }
/* 1152 */                     index--; } while (index >= 0);
/*      */ 
/* 1163 */                   if ((index >= 0) && (index < pLength)) {
/* 1164 */                     index = pLength;
/*      */                     do { if (params1[index].erasure() != params2[index])
/*      */                         break;
/* 1164 */                       index--; } while (index >= 0);
/*      */                   }
/*      */ 
/* 1168 */                   if (index >= 0)
/* 1169 */                     continue; 
/*      */                 }
/*      */               }
/*      */             }
/*      */           } else {
/* 1171 */             if (!method.areParametersEqual(method2))
/*      */               continue;
/*      */           }
/* 1174 */           boolean isEnumSpecialMethod = (isEnum()) && ((CharOperation.equals(selector, TypeConstants.VALUEOF)) || (CharOperation.equals(selector, TypeConstants.VALUES)));
/*      */ 
/* 1176 */           boolean removeMethod2 = true;
/* 1177 */           if (methodDecl == null) {
/* 1178 */             methodDecl = method.sourceMethod();
/* 1179 */             if ((methodDecl != null) && (methodDecl.binding != null)) {
/* 1180 */               boolean removeMethod = (method.returnType == null) && (method2.returnType != null);
/* 1181 */               if (isEnumSpecialMethod) {
/* 1182 */                 this.scope.problemReporter().duplicateEnumSpecialMethod(this, methodDecl);
/*      */ 
/* 1184 */                 removeMethod = true;
/*      */               } else {
/* 1186 */                 this.scope.problemReporter().duplicateMethodInType(this, methodDecl, method.areParametersEqual(method2));
/*      */               }
/* 1188 */               if (removeMethod) {
/* 1189 */                 removeMethod2 = false;
/* 1190 */                 methodDecl.binding = null;
/*      */ 
/* 1192 */                 if (resolvedMethods == this.methods)
/* 1193 */                   System.arraycopy(this.methods, 0, resolvedMethods = new MethodBinding[length], 0, length);
/* 1194 */                 resolvedMethods[i] = null;
/* 1195 */                 failed++;
/*      */               }
/*      */             }
/*      */           }
/* 1199 */           AbstractMethodDeclaration method2Decl = method2.sourceMethod();
/* 1200 */           if ((method2Decl != null) && (method2Decl.binding != null)) {
/* 1201 */             if (isEnumSpecialMethod) {
/* 1202 */               this.scope.problemReporter().duplicateEnumSpecialMethod(this, method2Decl);
/* 1203 */               removeMethod2 = true;
/*      */             } else {
/* 1205 */               this.scope.problemReporter().duplicateMethodInType(this, method2Decl, method.areParametersEqual(method2));
/*      */             }
/* 1207 */             if (removeMethod2) {
/* 1208 */               method2Decl.binding = null;
/*      */ 
/* 1210 */               if (resolvedMethods == this.methods)
/* 1211 */                 System.arraycopy(this.methods, 0, resolvedMethods = new MethodBinding[length], 0, length);
/* 1212 */               resolvedMethods[j] = null;
/* 1213 */               failed++;
/*      */             }
/*      */           }
/*      */         }
/* 1217 */         if ((method.returnType == null) && (resolvedMethods[i] != null)) {
/* 1218 */           methodDecl = method.sourceMethod();
/* 1219 */           if (methodDecl != null) {
/* 1220 */             methodDecl.binding = null;
/*      */           }
/* 1222 */           if (resolvedMethods == this.methods)
/* 1223 */             System.arraycopy(this.methods, 0, resolvedMethods = new MethodBinding[length], 0, length);
/* 1224 */           resolvedMethods[i] = null;
/* 1225 */           failed++;
/*      */         }
/*      */       }
/*      */     } finally {
/* 1229 */       if (failed > 0) {
/* 1230 */         int newSize = resolvedMethods.length - failed;
/* 1231 */         if (newSize == 0) {
/* 1232 */           this.methods = Binding.NO_METHODS;
/*      */         } else {
/* 1234 */           MethodBinding[] newMethods = new MethodBinding[newSize];
/* 1235 */           int i = 0; int j = 0; for (int length = resolvedMethods.length; i < length; i++)
/* 1236 */             if (resolvedMethods[i] != null)
/* 1237 */               newMethods[(j++)] = resolvedMethods[i];
/* 1238 */           this.methods = newMethods;
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/* 1243 */       addDefaultAbstractMethods();
/* 1244 */       this.tagBits |= 32768L;
/*      */     }
/* 1246 */     return this.methods;
/*      */   }
/*      */   public FieldBinding resolveTypeFor(FieldBinding field) {
/* 1249 */     if ((field.modifiers & 0x2000000) == 0) {
/* 1250 */       return field;
/*      */     }
/* 1252 */     if ((this.scope.compilerOptions().sourceLevel >= 3211264L) && 
/* 1253 */       ((field.getAnnotationTagBits() & 0x0) != 0L)) {
/* 1254 */       field.modifiers |= 1048576;
/*      */     }
/* 1256 */     if ((isViewedAsDeprecated()) && (!field.isDeprecated()))
/* 1257 */       field.modifiers |= 2097152;
/* 1258 */     if (hasRestrictedAccess())
/* 1259 */       field.modifiers |= 262144;
/* 1260 */     FieldDeclaration[] fieldDecls = this.scope.referenceContext.fields;
/* 1261 */     int f = 0; for (int length = fieldDecls.length; f < length; f++) {
/* 1262 */       if (fieldDecls[f].binding != field) {
/*      */         continue;
/*      */       }
/* 1265 */       MethodScope initializationScope = field.isStatic() ? 
/* 1266 */         this.scope.referenceContext.staticInitializerScope : 
/* 1267 */         this.scope.referenceContext.initializerScope;
/* 1268 */       FieldBinding previousField = initializationScope.initializedField;
/*      */       try {
/* 1270 */         initializationScope.initializedField = field;
/* 1271 */         FieldDeclaration fieldDecl = fieldDecls[f];
/* 1272 */         TypeBinding fieldType = 
/* 1273 */           fieldDecl.getKind() == 3 ? 
/* 1274 */           initializationScope.environment().convertToRawType(this, false) : 
/* 1275 */           fieldDecl.type.resolveType(initializationScope, true);
/* 1276 */         field.type = fieldType;
/* 1277 */         field.modifiers &= -33554433;
/* 1278 */         if (fieldType == null)
/* 1279 */           fieldDecl.binding = null;
/*      */         while (true)
/*      */         {
/*      */           return null;
/*      */ 
/* 1282 */           if (fieldType == TypeBinding.VOID) {
/* 1283 */             this.scope.problemReporter().variableTypeCannotBeVoid(fieldDecl);
/* 1284 */             fieldDecl.binding = null;
/* 1285 */             continue;
/*      */           }
/* 1287 */           if ((!fieldType.isArrayType()) || (((ArrayBinding)fieldType).leafComponentType != TypeBinding.VOID)) break;
/* 1288 */           this.scope.problemReporter().variableTypeCannotBeVoidArray(fieldDecl);
/* 1289 */           fieldDecl.binding = null;
/*      */         }
/*      */ 
/* 1292 */         if ((fieldType.tagBits & 0x80) != 0L) {
/* 1293 */           field.tagBits |= 128L;
/*      */         }
/* 1295 */         TypeBinding leafType = fieldType.leafComponentType();
/* 1296 */         if (((leafType instanceof ReferenceBinding)) && ((((ReferenceBinding)leafType).modifiers & 0x40000000) != 0))
/* 1297 */           field.modifiers |= 1073741824;
/*      */       }
/*      */       finally {
/* 1300 */         initializationScope.initializedField = previousField; } initializationScope.initializedField = previousField;
/*      */ 
/* 1302 */       return field;
/*      */     }
/* 1304 */     return null;
/*      */   }
/*      */   public MethodBinding resolveTypesFor(MethodBinding method) {
/* 1307 */     if ((method.modifiers & 0x2000000) == 0) {
/* 1308 */       return method;
/*      */     }
/* 1310 */     if ((this.scope.compilerOptions().sourceLevel >= 3211264L) && 
/* 1311 */       ((method.getAnnotationTagBits() & 0x0) != 0L)) {
/* 1312 */       method.modifiers |= 1048576;
/*      */     }
/* 1314 */     if ((isViewedAsDeprecated()) && (!method.isDeprecated()))
/* 1315 */       method.modifiers |= 2097152;
/* 1316 */     if (hasRestrictedAccess()) {
/* 1317 */       method.modifiers |= 262144;
/*      */     }
/* 1319 */     AbstractMethodDeclaration methodDecl = method.sourceMethod();
/* 1320 */     if (methodDecl == null) return null;
/*      */ 
/* 1322 */     TypeParameter[] typeParameters = methodDecl.typeParameters();
/* 1323 */     if (typeParameters != null) {
/* 1324 */       methodDecl.scope.connectTypeVariables(typeParameters, true);
/*      */ 
/* 1326 */       int i = 0; for (int paramLength = typeParameters.length; i < paramLength; i++)
/* 1327 */         typeParameters[i].checkBounds(methodDecl.scope);
/*      */     }
/* 1329 */     TypeReference[] exceptionTypes = methodDecl.thrownExceptions;
/* 1330 */     if (exceptionTypes != null) {
/* 1331 */       int size = exceptionTypes.length;
/* 1332 */       method.thrownExceptions = new ReferenceBinding[size];
/* 1333 */       int count = 0;
/*      */ 
/* 1335 */       for (int i = 0; i < size; i++) {
/* 1336 */         ReferenceBinding resolvedExceptionType = (ReferenceBinding)exceptionTypes[i].resolveType(methodDecl.scope, true);
/* 1337 */         if (resolvedExceptionType == null)
/*      */           continue;
/* 1339 */         if (resolvedExceptionType.isBoundParameterizedType()) {
/* 1340 */           methodDecl.scope.problemReporter().invalidParameterizedExceptionType(resolvedExceptionType, exceptionTypes[i]);
/*      */         }
/* 1343 */         else if ((resolvedExceptionType.findSuperTypeOriginatingFrom(21, true) == null) && 
/* 1344 */           (resolvedExceptionType.isValidBinding())) {
/* 1345 */           methodDecl.scope.problemReporter().cannotThrowType(exceptionTypes[i], resolvedExceptionType);
/*      */         }
/*      */         else
/*      */         {
/* 1349 */           if ((resolvedExceptionType.tagBits & 0x80) != 0L) {
/* 1350 */             method.tagBits |= 128L;
/*      */           }
/* 1352 */           method.modifiers |= resolvedExceptionType.modifiers & 0x40000000;
/* 1353 */           method.thrownExceptions[(count++)] = resolvedExceptionType;
/*      */         }
/*      */       }
/* 1355 */       if (count < size) {
/* 1356 */         System.arraycopy(method.thrownExceptions, 0, method.thrownExceptions = new ReferenceBinding[count], 0, count);
/*      */       }
/*      */     }
/* 1359 */     boolean foundArgProblem = false;
/* 1360 */     Argument[] arguments = methodDecl.arguments;
/* 1361 */     if (arguments != null) {
/* 1362 */       int size = arguments.length;
/* 1363 */       method.parameters = Binding.NO_PARAMETERS;
/* 1364 */       TypeBinding[] newParameters = new TypeBinding[size];
/* 1365 */       for (int i = 0; i < size; i++) {
/* 1366 */         Argument arg = arguments[i];
/* 1367 */         if (arg.annotations != null) {
/* 1368 */           method.tagBits |= 1024L;
/*      */         }
/* 1370 */         TypeBinding parameterType = arg.type.resolveType(methodDecl.scope, true);
/* 1371 */         if (parameterType == null) {
/* 1372 */           foundArgProblem = true;
/* 1373 */         } else if (parameterType == TypeBinding.VOID) {
/* 1374 */           methodDecl.scope.problemReporter().argumentTypeCannotBeVoid(this, methodDecl, arg);
/* 1375 */           foundArgProblem = true;
/*      */         } else {
/* 1377 */           if ((parameterType.tagBits & 0x80) != 0L) {
/* 1378 */             method.tagBits |= 128L;
/*      */           }
/* 1380 */           TypeBinding leafType = parameterType.leafComponentType();
/* 1381 */           if (((leafType instanceof ReferenceBinding)) && ((((ReferenceBinding)leafType).modifiers & 0x40000000) != 0))
/* 1382 */             method.modifiers |= 1073741824;
/* 1383 */           newParameters[i] = parameterType;
/* 1384 */           arg.binding = new LocalVariableBinding(arg, parameterType, arg.modifiers, true);
/*      */         }
/*      */       }
/*      */ 
/* 1388 */       if (!foundArgProblem) {
/* 1389 */         method.parameters = newParameters;
/*      */       }
/*      */     }
/*      */ 
/* 1393 */     boolean foundReturnTypeProblem = false;
/* 1394 */     if (!method.isConstructor()) {
/* 1395 */       TypeReference returnType = (methodDecl instanceof MethodDeclaration) ? 
/* 1396 */         ((MethodDeclaration)methodDecl).returnType : 
/* 1397 */         null;
/* 1398 */       if (returnType == null) {
/* 1399 */         methodDecl.scope.problemReporter().missingReturnType(methodDecl);
/* 1400 */         method.returnType = null;
/* 1401 */         foundReturnTypeProblem = true;
/*      */       } else {
/* 1403 */         TypeBinding methodType = returnType.resolveType(methodDecl.scope, true);
/* 1404 */         if (methodType == null) {
/* 1405 */           foundReturnTypeProblem = true;
/* 1406 */         } else if ((methodType.isArrayType()) && (((ArrayBinding)methodType).leafComponentType == TypeBinding.VOID)) {
/* 1407 */           methodDecl.scope.problemReporter().returnTypeCannotBeVoidArray((MethodDeclaration)methodDecl);
/* 1408 */           foundReturnTypeProblem = true;
/*      */         } else {
/* 1410 */           if ((methodType.tagBits & 0x80) != 0L) {
/* 1411 */             method.tagBits |= 128L;
/*      */           }
/* 1413 */           method.returnType = methodType;
/* 1414 */           TypeBinding leafType = methodType.leafComponentType();
/* 1415 */           if (((leafType instanceof ReferenceBinding)) && ((((ReferenceBinding)leafType).modifiers & 0x40000000) != 0))
/* 1416 */             method.modifiers |= 1073741824;
/*      */         }
/*      */       }
/*      */     }
/* 1420 */     if (foundArgProblem) {
/* 1421 */       methodDecl.binding = null;
/* 1422 */       method.parameters = Binding.NO_PARAMETERS;
/*      */ 
/* 1425 */       if (typeParameters != null) {
/* 1426 */         int i = 0; for (int length = typeParameters.length; i < length; i++)
/* 1427 */           typeParameters[i].binding = null; 
/*      */       }
/* 1428 */       return null;
/*      */     }
/* 1430 */     if (foundReturnTypeProblem) {
/* 1431 */       return method;
/*      */     }
/* 1433 */     method.modifiers &= -33554433;
/* 1434 */     return method;
/*      */   }
/*      */   public AnnotationHolder retrieveAnnotationHolder(Binding binding, boolean forceInitialization) {
/* 1437 */     if (forceInitialization)
/* 1438 */       binding.getAnnotationTagBits();
/* 1439 */     return super.retrieveAnnotationHolder(binding, false);
/*      */   }
/*      */   public void setFields(FieldBinding[] fields) {
/* 1442 */     this.fields = fields;
/*      */   }
/*      */   public void setMethods(MethodBinding[] methods) {
/* 1445 */     this.methods = methods;
/*      */   }
/*      */   public final int sourceEnd() {
/* 1448 */     return this.scope.referenceContext.sourceEnd;
/*      */   }
/*      */   public final int sourceStart() {
/* 1451 */     return this.scope.referenceContext.sourceStart;
/*      */   }
/*      */   SimpleLookupTable storedAnnotations(boolean forceInitialize) {
/* 1454 */     if ((forceInitialize) && (this.storedAnnotations == null) && (this.scope != null)) {
/* 1455 */       this.scope.referenceCompilationUnit().compilationResult.hasAnnotations = true;
/* 1456 */       if (!this.scope.environment().globalOptions.storeAnnotations)
/* 1457 */         return null;
/* 1458 */       this.storedAnnotations = new SimpleLookupTable(3);
/*      */     }
/* 1460 */     return this.storedAnnotations;
/*      */   }
/*      */   public ReferenceBinding superclass() {
/* 1463 */     return this.superclass;
/*      */   }
/*      */   public ReferenceBinding[] superInterfaces() {
/* 1466 */     return this.superInterfaces;
/*      */   }
/*      */ 
/*      */   public SyntheticMethodBinding[] syntheticMethods() {
/* 1470 */     if ((this.synthetics == null) || 
/* 1471 */       (this.synthetics[0] == null) || 
/* 1472 */       (this.synthetics[0].size() == 0)) {
/* 1473 */       return null;
/*      */     }
/*      */ 
/* 1476 */     int index = 0;
/* 1477 */     SyntheticMethodBinding[] bindings = new SyntheticMethodBinding[1];
/* 1478 */     Iterator methodArrayIterator = this.synthetics[0].values().iterator();
/* 1479 */     while (methodArrayIterator.hasNext()) {
/* 1480 */       SyntheticMethodBinding[] methodAccessors = (SyntheticMethodBinding[])methodArrayIterator.next();
/* 1481 */       int i = 0; for (int max = methodAccessors.length; i < max; i++)
/* 1482 */         if (methodAccessors[i] != null) {
/* 1483 */           if (index + 1 > bindings.length) {
/* 1484 */             System.arraycopy(bindings, 0, bindings = new SyntheticMethodBinding[index + 1], 0, index);
/*      */           }
/* 1486 */           bindings[(index++)] = methodAccessors[i];
/*      */         }
/*      */     }
/*      */     int length;
/* 1492 */     SyntheticMethodBinding[] sortedBindings = new SyntheticMethodBinding[length = bindings.length];
/* 1493 */     for (int i = 0; i < length; i++) {
/* 1494 */       SyntheticMethodBinding binding = bindings[i];
/* 1495 */       sortedBindings[binding.index] = binding;
/*      */     }
/* 1497 */     return sortedBindings;
/*      */   }
/*      */ 
/*      */   public FieldBinding[] syntheticFields()
/*      */   {
/* 1503 */     if (this.synthetics == null) return null;
/* 1504 */     int fieldSize = this.synthetics[1] == null ? 0 : this.synthetics[1].size();
/* 1505 */     int literalSize = this.synthetics[2] == null ? 0 : this.synthetics[2].size();
/* 1506 */     int totalSize = fieldSize + literalSize;
/* 1507 */     if (totalSize == 0) return null;
/* 1508 */     FieldBinding[] bindings = new FieldBinding[totalSize];
/*      */ 
/* 1511 */     if (this.synthetics[1] != null) {
/* 1512 */       Iterator elements = this.synthetics[1].values().iterator();
/* 1513 */       for (int i = 0; i < fieldSize; i++) {
/* 1514 */         SyntheticFieldBinding synthBinding = (SyntheticFieldBinding)elements.next();
/* 1515 */         bindings[synthBinding.index] = synthBinding;
/*      */       }
/*      */     }
/*      */ 
/* 1519 */     if (this.synthetics[2] != null) {
/* 1520 */       Iterator elements = this.synthetics[2].values().iterator();
/* 1521 */       for (int i = 0; i < literalSize; i++) {
/* 1522 */         SyntheticFieldBinding synthBinding = (SyntheticFieldBinding)elements.next();
/* 1523 */         bindings[(fieldSize + synthBinding.index)] = synthBinding;
/*      */       }
/*      */     }
/* 1526 */     return bindings;
/*      */   }
/*      */   public String toString() {
/* 1529 */     StringBuffer buffer = new StringBuffer(30);
/* 1530 */     buffer.append("(id=");
/* 1531 */     if (this.id == 2147483647)
/* 1532 */       buffer.append("NoId");
/*      */     else
/* 1534 */       buffer.append(this.id);
/* 1535 */     buffer.append(")\n");
/* 1536 */     if (isDeprecated()) buffer.append("deprecated ");
/* 1537 */     if (isPublic()) buffer.append("public ");
/* 1538 */     if (isProtected()) buffer.append("protected ");
/* 1539 */     if (isPrivate()) buffer.append("private ");
/* 1540 */     if ((isAbstract()) && (isClass())) buffer.append("abstract ");
/* 1541 */     if ((isStatic()) && (isNestedType())) buffer.append("static ");
/* 1542 */     if (isFinal()) buffer.append("final ");
/*      */ 
/* 1544 */     if (isEnum()) buffer.append("enum ");
/* 1545 */     else if (isAnnotationType()) buffer.append("@interface ");
/* 1546 */     else if (isClass()) buffer.append("class "); else
/* 1547 */       buffer.append("interface ");
/* 1548 */     buffer.append(this.compoundName != null ? CharOperation.toString(this.compoundName) : "UNNAMED TYPE");
/*      */ 
/* 1550 */     if (this.typeVariables == null) {
/* 1551 */       buffer.append("<NULL TYPE VARIABLES>");
/* 1552 */     } else if (this.typeVariables != Binding.NO_TYPE_VARIABLES) {
/* 1553 */       buffer.append("<");
/* 1554 */       int i = 0; for (int length = this.typeVariables.length; i < length; i++) {
/* 1555 */         if (i > 0) buffer.append(", ");
/* 1556 */         if (this.typeVariables[i] == null) {
/* 1557 */           buffer.append("NULL TYPE VARIABLE");
/*      */         }
/*      */         else {
/* 1560 */           char[] varChars = this.typeVariables[i].toString().toCharArray();
/* 1561 */           buffer.append(varChars, 1, varChars.length - 2);
/*      */         }
/*      */       }
/* 1563 */       buffer.append(">");
/*      */     }
/* 1565 */     buffer.append("\n\textends ");
/* 1566 */     buffer.append(this.superclass != null ? this.superclass.debugName() : "NULL TYPE");
/*      */ 
/* 1568 */     if (this.superInterfaces != null) {
/* 1569 */       if (this.superInterfaces != Binding.NO_SUPERINTERFACES) {
/* 1570 */         buffer.append("\n\timplements : ");
/* 1571 */         int i = 0; for (int length = this.superInterfaces.length; i < length; i++) {
/* 1572 */           if (i > 0)
/* 1573 */             buffer.append(", ");
/* 1574 */           buffer.append(this.superInterfaces[i] != null ? this.superInterfaces[i].debugName() : "NULL TYPE");
/*      */         }
/*      */       }
/*      */     }
/* 1578 */     else buffer.append("NULL SUPERINTERFACES");
/*      */ 
/* 1581 */     if (enclosingType() != null) {
/* 1582 */       buffer.append("\n\tenclosing type : ");
/* 1583 */       buffer.append(enclosingType().debugName());
/*      */     }
/*      */ 
/* 1586 */     if (this.fields != null) {
/* 1587 */       if (this.fields != Binding.NO_FIELDS) {
/* 1588 */         buffer.append("\n/*   fields   */");
/* 1589 */         int i = 0; for (int length = this.fields.length; i < length; i++)
/* 1590 */           buffer.append('\n').append(this.fields[i] != null ? this.fields[i].toString() : "NULL FIELD");
/*      */       }
/*      */     }
/* 1593 */     else buffer.append("NULL FIELDS");
/*      */ 
/* 1596 */     if (this.methods != null) {
/* 1597 */       if (this.methods != Binding.NO_METHODS) {
/* 1598 */         buffer.append("\n/*   methods   */");
/* 1599 */         int i = 0; for (int length = this.methods.length; i < length; i++)
/* 1600 */           buffer.append('\n').append(this.methods[i] != null ? this.methods[i].toString() : "NULL METHOD");
/*      */       }
/*      */     }
/* 1603 */     else buffer.append("NULL METHODS");
/*      */ 
/* 1606 */     if (this.memberTypes != null) {
/* 1607 */       if (this.memberTypes != Binding.NO_MEMBER_TYPES) {
/* 1608 */         buffer.append("\n/*   members   */");
/* 1609 */         int i = 0; for (int length = this.memberTypes.length; i < length; i++)
/* 1610 */           buffer.append('\n').append(this.memberTypes[i] != null ? this.memberTypes[i].toString() : "NULL TYPE");
/*      */       }
/*      */     }
/* 1613 */     else buffer.append("NULL MEMBER TYPES");
/*      */ 
/* 1616 */     buffer.append("\n\n");
/* 1617 */     return buffer.toString();
/*      */   }
/*      */   public TypeVariableBinding[] typeVariables() {
/* 1620 */     return this.typeVariables;
/*      */   }
/*      */   void verifyMethods(MethodVerifier verifier) {
/* 1623 */     verifier.verify(this);
/*      */ 
/* 1625 */     int i = this.memberTypes.length;
/*      */     do { ((SourceTypeBinding)this.memberTypes[i]).verifyMethods(verifier);
/*      */ 
/* 1625 */       i--; } while (i >= 0);
/*      */   }
/*      */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\ecj-3.5.2.jar
 * Qualified Name:     org.eclipse.jdt.internal.compiler.lookup.SourceTypeBinding
 * JD-Core Version:    0.6.0
 */