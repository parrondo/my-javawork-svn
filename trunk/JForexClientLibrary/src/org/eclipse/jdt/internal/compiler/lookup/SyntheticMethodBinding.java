/*     */ package org.eclipse.jdt.internal.compiler.lookup;
/*     */ 
/*     */ import org.eclipse.jdt.core.compiler.CharOperation;
/*     */ import org.eclipse.jdt.internal.compiler.ast.AbstractMethodDeclaration;
/*     */ import org.eclipse.jdt.internal.compiler.ast.FieldDeclaration;
/*     */ import org.eclipse.jdt.internal.compiler.ast.TypeDeclaration;
/*     */ 
/*     */ public class SyntheticMethodBinding extends MethodBinding
/*     */ {
/*     */   public FieldBinding targetReadField;
/*     */   public FieldBinding targetWriteField;
/*     */   public MethodBinding targetMethod;
/*     */   public TypeBinding targetEnumType;
/*     */   public int purpose;
/*     */   public static final int FieldReadAccess = 1;
/*     */   public static final int FieldWriteAccess = 2;
/*     */   public static final int SuperFieldReadAccess = 3;
/*     */   public static final int SuperFieldWriteAccess = 4;
/*     */   public static final int MethodAccess = 5;
/*     */   public static final int ConstructorAccess = 6;
/*     */   public static final int SuperMethodAccess = 7;
/*     */   public static final int BridgeMethod = 8;
/*     */   public static final int EnumValues = 9;
/*     */   public static final int EnumValueOf = 10;
/*     */   public static final int SwitchTable = 11;
/*  39 */   public int sourceStart = 0;
/*     */   public int index;
/*     */ 
/*     */   public SyntheticMethodBinding(FieldBinding targetField, boolean isReadAccess, boolean isSuperAccess, ReferenceBinding declaringClass)
/*     */   {
/*  44 */     this.modifiers = 4104;
/*  45 */     this.tagBits |= 25769803776L;
/*  46 */     SourceTypeBinding declaringSourceType = (SourceTypeBinding)declaringClass;
/*  47 */     SyntheticMethodBinding[] knownAccessMethods = declaringSourceType.syntheticMethods();
/*  48 */     int methodId = knownAccessMethods == null ? 0 : knownAccessMethods.length;
/*  49 */     this.index = methodId;
/*  50 */     this.selector = CharOperation.concat(TypeConstants.SYNTHETIC_ACCESS_METHOD_PREFIX, String.valueOf(methodId).toCharArray());
/*  51 */     if (isReadAccess) {
/*  52 */       this.returnType = targetField.type;
/*  53 */       if (targetField.isStatic()) {
/*  54 */         this.parameters = Binding.NO_PARAMETERS;
/*     */       } else {
/*  56 */         this.parameters = new TypeBinding[1];
/*  57 */         this.parameters[0] = declaringSourceType;
/*     */       }
/*  59 */       this.targetReadField = targetField;
/*  60 */       this.purpose = (isSuperAccess ? 3 : 1);
/*     */     } else {
/*  62 */       this.returnType = TypeBinding.VOID;
/*  63 */       if (targetField.isStatic()) {
/*  64 */         this.parameters = new TypeBinding[1];
/*  65 */         this.parameters[0] = targetField.type;
/*     */       } else {
/*  67 */         this.parameters = new TypeBinding[2];
/*  68 */         this.parameters[0] = declaringSourceType;
/*  69 */         this.parameters[1] = targetField.type;
/*     */       }
/*  71 */       this.targetWriteField = targetField;
/*  72 */       this.purpose = (isSuperAccess ? 4 : 2);
/*  74 */     }this.thrownExceptions = Binding.NO_EXCEPTIONS;
/*  75 */     this.declaringClass = declaringSourceType;
/*     */     boolean needRename;
/*     */     label436: 
/*     */     do {
/*  81 */       needRename = false;
/*     */ 
/*  84 */       MethodBinding[] methods = declaringSourceType.methods();
/*     */       long range;
/*  85 */       if ((range = ReferenceBinding.binarySearch(this.selector, methods)) >= 0L) {
/*  86 */         int paramCount = this.parameters.length;
/*  87 */         int imethod = (int)range; for (int end = (int)(range >> 32); imethod <= end; imethod++) {
/*  88 */           MethodBinding method = methods[imethod];
/*  89 */           if (method.parameters.length == paramCount) {
/*  90 */             TypeBinding[] toMatch = method.parameters;
/*  91 */             int i = 0;
/*  92 */             while (toMatch[i] == this.parameters[i])
/*     */             {
/*  91 */               i++; if (i < paramCount)
/*     */               {
/*     */                 continue;
/*     */               }
/*     */ 
/*  96 */               needRename = true;
/*  97 */               break label436;
/*     */             }
/*     */           }
/*     */         }
/*     */       }
/* 102 */       if (knownAccessMethods != null) {
/* 103 */         int i = 0; for (int length = knownAccessMethods.length; i < length; i++) {
/* 104 */           if ((knownAccessMethods[i] == null) || 
/* 105 */             (!CharOperation.equals(this.selector, knownAccessMethods[i].selector)) || (!areParametersEqual(methods[i]))) continue;
/* 106 */           needRename = true;
/* 107 */           break;
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/* 112 */       if (needRename) {
/* 113 */         methodId++; setSelector(CharOperation.concat(TypeConstants.SYNTHETIC_ACCESS_METHOD_PREFIX, String.valueOf(methodId).toCharArray()));
/*     */       }
/*     */     }
/*  79 */     while (
/* 115 */       needRename);
/*     */ 
/* 118 */     FieldDeclaration[] fieldDecls = declaringSourceType.scope.referenceContext.fields;
/* 119 */     if (fieldDecls != null) {
/* 120 */       int i = 0; for (int max = fieldDecls.length; i < max; i++) {
/* 121 */         if (fieldDecls[i].binding == targetField) {
/* 122 */           this.sourceStart = fieldDecls[i].sourceStart;
/* 123 */           return;
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 144 */     this.sourceStart = declaringSourceType.scope.referenceContext.sourceStart;
/*     */   }
/*     */ 
/*     */   public SyntheticMethodBinding(FieldBinding targetField, ReferenceBinding declaringClass, TypeBinding enumBinding, char[] selector) {
/* 148 */     this.modifiers = 4104;
/* 149 */     this.tagBits |= 25769803776L;
/* 150 */     SourceTypeBinding declaringSourceType = (SourceTypeBinding)declaringClass;
/* 151 */     SyntheticMethodBinding[] knownAccessMethods = declaringSourceType.syntheticMethods();
/* 152 */     int methodId = knownAccessMethods == null ? 0 : knownAccessMethods.length;
/* 153 */     this.index = methodId;
/* 154 */     this.selector = selector;
/* 155 */     this.returnType = declaringSourceType.scope.createArrayType(TypeBinding.INT, 1);
/* 156 */     this.parameters = Binding.NO_PARAMETERS;
/* 157 */     this.targetReadField = targetField;
/* 158 */     this.targetEnumType = enumBinding;
/* 159 */     this.purpose = 11;
/* 160 */     this.thrownExceptions = Binding.NO_EXCEPTIONS;
/* 161 */     this.declaringClass = declaringSourceType;
/*     */ 
/* 163 */     if (declaringSourceType.isStrictfp())
/* 164 */       this.modifiers |= 2048;
/*     */     boolean needRename;
/*     */     label337: 
/*     */     do
/*     */     {
/* 170 */       needRename = false;
/*     */ 
/* 173 */       MethodBinding[] methods = declaringSourceType.methods();
/*     */       long range;
/* 174 */       if ((range = ReferenceBinding.binarySearch(this.selector, methods)) >= 0L) {
/* 175 */         int paramCount = this.parameters.length;
/* 176 */         int imethod = (int)range; for (int end = (int)(range >> 32); imethod <= end; imethod++) {
/* 177 */           MethodBinding method = methods[imethod];
/* 178 */           if (method.parameters.length == paramCount) {
/* 179 */             TypeBinding[] toMatch = method.parameters;
/* 180 */             int i = 0;
/* 181 */             while (toMatch[i] == this.parameters[i])
/*     */             {
/* 180 */               i++; if (i < paramCount)
/*     */               {
/*     */                 continue;
/*     */               }
/*     */ 
/* 185 */               needRename = true;
/* 186 */               break label337;
/*     */             }
/*     */           }
/*     */         }
/*     */       }
/* 191 */       if (knownAccessMethods != null) {
/* 192 */         int i = 0; for (int length = knownAccessMethods.length; i < length; i++) {
/* 193 */           if ((knownAccessMethods[i] == null) || 
/* 194 */             (!CharOperation.equals(this.selector, knownAccessMethods[i].selector)) || (!areParametersEqual(methods[i]))) continue;
/* 195 */           needRename = true;
/* 196 */           break;
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/* 201 */       if (needRename) {
/* 202 */         methodId++; setSelector(CharOperation.concat(selector, String.valueOf(methodId).toCharArray()));
/*     */       }
/*     */     }
/* 168 */     while (
/* 204 */       needRename);
/*     */ 
/* 208 */     this.sourceStart = declaringSourceType.scope.referenceContext.sourceStart;
/*     */   }
/*     */ 
/*     */   public SyntheticMethodBinding(MethodBinding targetMethod, boolean isSuperAccess, ReferenceBinding declaringClass)
/*     */   {
/* 213 */     if (targetMethod.isConstructor())
/* 214 */       initializeConstructorAccessor(targetMethod);
/*     */     else
/* 216 */       initializeMethodAccessor(targetMethod, isSuperAccess, declaringClass);
/*     */   }
/*     */ 
/*     */   public SyntheticMethodBinding(MethodBinding overridenMethodToBridge, MethodBinding targetMethod, SourceTypeBinding declaringClass)
/*     */   {
/* 225 */     this.declaringClass = declaringClass;
/* 226 */     this.selector = overridenMethodToBridge.selector;
/*     */ 
/* 229 */     this.modifiers = ((targetMethod.modifiers | 0x40 | 0x1000) & 0xBFFFFAEF);
/* 230 */     this.tagBits |= 25769803776L;
/* 231 */     this.returnType = overridenMethodToBridge.returnType;
/* 232 */     this.parameters = overridenMethodToBridge.parameters;
/* 233 */     this.thrownExceptions = overridenMethodToBridge.thrownExceptions;
/* 234 */     this.targetMethod = targetMethod;
/* 235 */     this.purpose = 8;
/* 236 */     SyntheticMethodBinding[] knownAccessMethods = declaringClass.syntheticMethods();
/* 237 */     int methodId = knownAccessMethods == null ? 0 : knownAccessMethods.length;
/* 238 */     this.index = methodId;
/*     */   }
/*     */ 
/*     */   public SyntheticMethodBinding(SourceTypeBinding declaringEnum, char[] selector)
/*     */   {
/* 245 */     this.declaringClass = declaringEnum;
/* 246 */     this.selector = selector;
/* 247 */     this.modifiers = 9;
/* 248 */     this.tagBits |= 25769803776L;
/* 249 */     LookupEnvironment environment = declaringEnum.scope.environment();
/* 250 */     this.thrownExceptions = Binding.NO_EXCEPTIONS;
/* 251 */     if (selector == TypeConstants.VALUES) {
/* 252 */       this.returnType = environment.createArrayType(environment.convertToParameterizedType(declaringEnum), 1);
/* 253 */       this.parameters = Binding.NO_PARAMETERS;
/* 254 */       this.purpose = 9;
/* 255 */     } else if (selector == TypeConstants.VALUEOF) {
/* 256 */       this.returnType = environment.convertToParameterizedType(declaringEnum);
/* 257 */       this.parameters = new TypeBinding[] { declaringEnum.scope.getJavaLangString() };
/* 258 */       this.purpose = 10;
/*     */     }
/* 260 */     SyntheticMethodBinding[] knownAccessMethods = ((SourceTypeBinding)this.declaringClass).syntheticMethods();
/* 261 */     int methodId = knownAccessMethods == null ? 0 : knownAccessMethods.length;
/* 262 */     this.index = methodId;
/* 263 */     if (declaringEnum.isStrictfp())
/* 264 */       this.modifiers |= 2048;  } 
/* 274 */   public void initializeConstructorAccessor(MethodBinding accessedConstructor) { this.targetMethod = accessedConstructor;
/* 275 */     this.modifiers = 4096;
/* 276 */     this.tagBits |= 25769803776L;
/* 277 */     SourceTypeBinding sourceType = (SourceTypeBinding)accessedConstructor.declaringClass;
/* 278 */     SyntheticMethodBinding[] knownSyntheticMethods = sourceType.syntheticMethods();
/* 279 */     this.index = (knownSyntheticMethods == null ? 0 : knownSyntheticMethods.length);
/*     */ 
/* 281 */     this.selector = accessedConstructor.selector;
/* 282 */     this.returnType = accessedConstructor.returnType;
/* 283 */     this.purpose = 6;
/* 284 */     this.parameters = new TypeBinding[accessedConstructor.parameters.length + 1];
/* 285 */     System.arraycopy(
/* 286 */       accessedConstructor.parameters, 
/* 287 */       0, 
/* 288 */       this.parameters, 
/* 289 */       0, 
/* 290 */       accessedConstructor.parameters.length);
/* 291 */     this.parameters[accessedConstructor.parameters.length] = 
/* 292 */       accessedConstructor.declaringClass;
/* 293 */     this.thrownExceptions = accessedConstructor.thrownExceptions;
/* 294 */     this.declaringClass = sourceType;
/*     */     boolean needRename;
/*     */     do { needRename = false;
/*     */ 
/* 302 */       MethodBinding[] methods = sourceType.methods();
/* 303 */       int i = 0; int length = methods.length;
/*     */       while (true) if ((CharOperation.equals(this.selector, methods[i].selector)) && 
/* 305 */           (areParametersEqual(methods[i]))) {
/* 306 */           needRename = true;
/*     */         }
/*     */         else
/*     */         {
/* 303 */           i++; if (i < length)
/*     */           {
/*     */             continue;
/*     */           }
/*     */ 
/* 311 */           if (knownSyntheticMethods == null) break;
/* 312 */           int i = 0; for (int length = knownSyntheticMethods.length; i < length; i++) {
/* 313 */             if (knownSyntheticMethods[i] == null)
/*     */               continue;
/* 315 */             if ((!CharOperation.equals(this.selector, knownSyntheticMethods[i].selector)) || 
/* 316 */               (!areParametersEqual(knownSyntheticMethods[i]))) continue;
/* 317 */             needRename = true;
/* 318 */             break;
/*     */           }
/*     */         }
/*     */ 
/*     */ 
/* 323 */       if (needRename) {
/* 324 */         int length = this.parameters.length;
/* 325 */         System.arraycopy(
/* 326 */           this.parameters, 
/* 327 */           0, 
/* 328 */           this.parameters = new TypeBinding[length + 1], 
/* 329 */           0, 
/* 330 */           length);
/* 331 */         this.parameters[length] = this.declaringClass;
/*     */       }
/*     */     }
/* 298 */     while (
/* 333 */       needRename);
/*     */ 
/* 336 */     AbstractMethodDeclaration[] methodDecls = 
/* 337 */       sourceType.scope.referenceContext.methods;
/* 338 */     if (methodDecls != null) {
/* 339 */       int i = 0; for (int length = methodDecls.length; i < length; i++)
/* 340 */         if (methodDecls[i].binding == accessedConstructor) {
/* 341 */           this.sourceStart = methodDecls[i].sourceStart;
/* 342 */           return;
/*     */         }
/*     */     }
/*     */   }
/*     */ 
/*     */   public void initializeMethodAccessor(MethodBinding accessedMethod, boolean isSuperAccess, ReferenceBinding receiverType)
/*     */   {
/* 353 */     this.targetMethod = accessedMethod;
/* 354 */     this.modifiers = 4104;
/* 355 */     this.tagBits |= 25769803776L;
/* 356 */     SourceTypeBinding declaringSourceType = (SourceTypeBinding)receiverType;
/* 357 */     SyntheticMethodBinding[] knownAccessMethods = declaringSourceType.syntheticMethods();
/* 358 */     int methodId = knownAccessMethods == null ? 0 : knownAccessMethods.length;
/* 359 */     this.index = methodId;
/*     */ 
/* 361 */     this.selector = CharOperation.concat(TypeConstants.SYNTHETIC_ACCESS_METHOD_PREFIX, String.valueOf(methodId).toCharArray());
/* 362 */     this.returnType = accessedMethod.returnType;
/* 363 */     this.purpose = (isSuperAccess ? 7 : 5);
/*     */ 
/* 365 */     if (accessedMethod.isStatic()) {
/* 366 */       this.parameters = accessedMethod.parameters;
/*     */     } else {
/* 368 */       this.parameters = new TypeBinding[accessedMethod.parameters.length + 1];
/* 369 */       this.parameters[0] = declaringSourceType;
/* 370 */       System.arraycopy(accessedMethod.parameters, 0, this.parameters, 1, accessedMethod.parameters.length);
/*     */     }
/* 372 */     this.thrownExceptions = accessedMethod.thrownExceptions;
/* 373 */     this.declaringClass = declaringSourceType;
/*     */     boolean needRename;
/*     */     do {
/* 379 */       needRename = false;
/*     */ 
/* 381 */       MethodBinding[] methods = declaringSourceType.methods();
/* 382 */       int i = 0; int length = methods.length;
/*     */       while (true) if ((CharOperation.equals(this.selector, methods[i].selector)) && (areParametersEqual(methods[i]))) {
/* 384 */           needRename = true;
/*     */         }
/*     */         else
/*     */         {
/* 382 */           i++; if (i < length)
/*     */           {
/*     */             continue;
/*     */           }
/*     */ 
/* 389 */           if (knownAccessMethods == null) break;
/* 390 */           int i = 0; for (int length = knownAccessMethods.length; i < length; i++) {
/* 391 */             if ((knownAccessMethods[i] == null) || 
/* 392 */               (!CharOperation.equals(this.selector, knownAccessMethods[i].selector)) || (!areParametersEqual(knownAccessMethods[i]))) continue;
/* 393 */             needRename = true;
/* 394 */             break;
/*     */           }
/*     */         }
/*     */ 
/*     */ 
/* 399 */       if (needRename) {
/* 400 */         methodId++; setSelector(CharOperation.concat(TypeConstants.SYNTHETIC_ACCESS_METHOD_PREFIX, String.valueOf(methodId).toCharArray()));
/*     */       }
/*     */     }
/* 377 */     while (
/* 402 */       needRename);
/*     */ 
/* 405 */     AbstractMethodDeclaration[] methodDecls = declaringSourceType.scope.referenceContext.methods;
/* 406 */     if (methodDecls != null) {
/* 407 */       int i = 0; for (int length = methodDecls.length; i < length; i++)
/* 408 */         if (methodDecls[i].binding == accessedMethod) {
/* 409 */           this.sourceStart = methodDecls[i].sourceStart;
/* 410 */           return;
/*     */         }
/*     */     }
/*     */   }
/*     */ 
/*     */   protected boolean isConstructorRelated()
/*     */   {
/* 417 */     return this.purpose == 6;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\ecj-3.5.2.jar
 * Qualified Name:     org.eclipse.jdt.internal.compiler.lookup.SyntheticMethodBinding
 * JD-Core Version:    0.6.0
 */