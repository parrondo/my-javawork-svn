/*     */ package org.eclipse.jdt.internal.compiler.lookup;
/*     */ 
/*     */ import org.eclipse.jdt.internal.compiler.ast.MessageSend;
/*     */ 
/*     */ public class ParameterizedGenericMethodBinding extends ParameterizedMethodBinding
/*     */   implements Substitution
/*     */ {
/*     */   public TypeBinding[] typeArguments;
/*     */   private LookupEnvironment environment;
/*     */   public boolean inferredReturnType;
/*     */   public boolean wasInferred;
/*     */   public boolean isRaw;
/*     */   private MethodBinding tiebreakMethod;
/*     */ 
/*     */   public static MethodBinding computeCompatibleMethod(MethodBinding originalMethod, TypeBinding[] arguments, Scope scope, InvocationSite invocationSite)
/*     */   {
/*  35 */     TypeVariableBinding[] typeVariables = originalMethod.typeVariables;
/*  36 */     TypeBinding[] substitutes = invocationSite.genericTypeArguments();
/*  37 */     TypeBinding[] uncheckedArguments = (TypeBinding[])null;
/*     */     ParameterizedGenericMethodBinding methodSubstitute;
/*     */     ParameterizedGenericMethodBinding methodSubstitute;
/*  39 */     if (substitutes != null)
/*     */     {
/*  41 */       if (substitutes.length != typeVariables.length)
/*     */       {
/*  43 */         return new ProblemMethodBinding(originalMethod, originalMethod.selector, substitutes, 11);
/*     */       }
/*  45 */       methodSubstitute = scope.environment().createParameterizedGenericMethod(originalMethod, substitutes);
/*     */     }
/*     */     else
/*     */     {
/*  50 */       TypeBinding[] parameters = originalMethod.parameters;
/*  51 */       InferenceContext inferenceContext = new InferenceContext(originalMethod);
/*  52 */       methodSubstitute = inferFromArgumentTypes(scope, originalMethod, arguments, parameters, inferenceContext);
/*  53 */       if (methodSubstitute == null) {
/*  54 */         return null;
/*     */       }
/*     */ 
/*  58 */       if (inferenceContext.hasUnresolvedTypeArgument()) {
/*  59 */         if (inferenceContext.isUnchecked) {
/*  60 */           int length = inferenceContext.substitutes.length;
/*  61 */           System.arraycopy(inferenceContext.substitutes, 0, uncheckedArguments = new TypeBinding[length], 0, length);
/*     */         }
/*  63 */         if (methodSubstitute.returnType != TypeBinding.VOID) {
/*  64 */           TypeBinding expectedType = null;
/*     */ 
/*  66 */           if ((invocationSite instanceof MessageSend)) {
/*  67 */             MessageSend message = (MessageSend)invocationSite;
/*  68 */             expectedType = message.expectedType;
/*     */           }
/*  70 */           if (expectedType != null)
/*     */           {
/*  72 */             inferenceContext.hasExplicitExpectedType = true;
/*     */           }
/*  74 */           else expectedType = scope.getJavaLangObject();
/*     */ 
/*  76 */           inferenceContext.expectedType = expectedType;
/*     */         }
/*  78 */         methodSubstitute = methodSubstitute.inferFromExpectedType(scope, inferenceContext);
/*  79 */         if (methodSubstitute == null) {
/*  80 */           return null;
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/*  85 */     int i = 0; for (int length = typeVariables.length; i < length; i++) {
/*  86 */       TypeVariableBinding typeVariable = typeVariables[i];
/*  87 */       TypeBinding substitute = methodSubstitute.typeArguments[i];
/*  88 */       if ((uncheckedArguments == null) || (uncheckedArguments[i] != null)) {
/*  89 */         switch (typeVariable.boundCheck(methodSubstitute, substitute))
/*     */         {
/*     */         case 2:
/*  92 */           int argLength = arguments.length;
/*  93 */           TypeBinding[] augmentedArguments = new TypeBinding[argLength + 2];
/*  94 */           System.arraycopy(arguments, 0, augmentedArguments, 0, argLength);
/*  95 */           augmentedArguments[argLength] = substitute;
/*  96 */           augmentedArguments[(argLength + 1)] = typeVariable;
/*  97 */           return new ProblemMethodBinding(methodSubstitute, originalMethod.selector, augmentedArguments, 10);
/*     */         case 1:
/* 100 */           methodSubstitute.tagBits |= 256L;
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/* 105 */     return methodSubstitute;
/*     */   }
/*     */ 
/*     */   private static ParameterizedGenericMethodBinding inferFromArgumentTypes(Scope scope, MethodBinding originalMethod, TypeBinding[] arguments, TypeBinding[] parameters, InferenceContext inferenceContext)
/*     */   {
/* 112 */     if (originalMethod.isVarargs()) {
/* 113 */       int paramLength = parameters.length;
/* 114 */       int minArgLength = paramLength - 1;
/* 115 */       int argLength = arguments.length;
/*     */ 
/* 117 */       for (int i = 0; i < minArgLength; i++) {
/* 118 */         parameters[i].collectSubstitutes(scope, arguments[i], inferenceContext, 1);
/* 119 */         if (inferenceContext.status == 1) return null;
/*     */       }
/*     */ 
/* 122 */       if (minArgLength < argLength) {
/* 123 */         TypeBinding varargType = parameters[minArgLength];
/* 124 */         TypeBinding lastArgument = arguments[minArgLength];
/*     */ 
/* 126 */         if (paramLength == argLength) {
/* 127 */           if (lastArgument == TypeBinding.NULL) break label158; switch (lastArgument.dimensions()) {
/*     */           case 0:
/* 130 */             break;
/*     */           case 1:
/* 132 */             if (lastArgument.leafComponentType().isBaseType()) break; break;
/*     */           default:
/* 135 */             break;
/*     */           }
/*     */         }
/*     */ 
/* 139 */         varargType = ((ArrayBinding)varargType).elementsType();
/*     */ 
/* 141 */         label158: for (int i = minArgLength; i < argLength; i++) {
/* 142 */           varargType.collectSubstitutes(scope, arguments[i], inferenceContext, 1);
/* 143 */           if (inferenceContext.status == 1) return null; 
/*     */         }
/*     */       }
/*     */     }
/*     */     else {
/* 147 */       int paramLength = parameters.length;
/* 148 */       for (int i = 0; i < paramLength; i++) {
/* 149 */         parameters[i].collectSubstitutes(scope, arguments[i], inferenceContext, 1);
/* 150 */         if (inferenceContext.status == 1) return null;
/*     */       }
/*     */     }
/* 153 */     TypeVariableBinding[] originalVariables = originalMethod.typeVariables;
/* 154 */     if (!resolveSubstituteConstraints(scope, originalVariables, inferenceContext, false)) {
/* 155 */       return null;
/*     */     }
/*     */ 
/* 158 */     TypeBinding[] inferredSustitutes = inferenceContext.substitutes;
/* 159 */     TypeBinding[] actualSubstitutes = inferredSustitutes;
/* 160 */     int i = 0; for (int varLength = originalVariables.length; i < varLength; i++) {
/* 161 */       if (inferredSustitutes[i] == null) {
/* 162 */         if (actualSubstitutes == inferredSustitutes) {
/* 163 */           System.arraycopy(inferredSustitutes, 0, actualSubstitutes = new TypeBinding[varLength], 0, i);
/*     */         }
/* 165 */         actualSubstitutes[i] = originalVariables[i];
/* 166 */       } else if (actualSubstitutes != inferredSustitutes) {
/* 167 */         actualSubstitutes[i] = inferredSustitutes[i];
/*     */       }
/*     */     }
/* 170 */     ParameterizedGenericMethodBinding paramMethod = scope.environment().createParameterizedGenericMethod(originalMethod, actualSubstitutes);
/* 171 */     return paramMethod;
/*     */   }
/*     */ 
/*     */   private static boolean resolveSubstituteConstraints(Scope scope, TypeVariableBinding[] typeVariables, InferenceContext inferenceContext, boolean considerEXTENDSConstraints) {
/* 175 */     TypeBinding[] substitutes = inferenceContext.substitutes;
/* 176 */     int varLength = typeVariables.length;
/*     */ 
/* 179 */     for (int i = 0; i < varLength; i++) {
/* 180 */       TypeVariableBinding current = typeVariables[i];
/* 181 */       TypeBinding substitute = substitutes[i];
/* 182 */       if (substitute == null) {
/* 183 */         TypeBinding[] equalSubstitutes = inferenceContext.getSubstitutes(current, 0);
/* 184 */         if (equalSubstitutes == null)
/*     */           continue;
/* 186 */         int j = 0; for (int equalLength = equalSubstitutes.length; j < equalLength; j++) {
/* 187 */           TypeBinding equalSubstitute = equalSubstitutes[j];
/* 188 */           if (equalSubstitute != null) {
/* 189 */             if (equalSubstitute == current)
/*     */             {
/* 191 */               for (int k = j + 1; k < equalLength; k++) {
/* 192 */                 equalSubstitute = equalSubstitutes[k];
/* 193 */                 if ((equalSubstitute != current) && (equalSubstitute != null)) {
/* 194 */                   substitutes[i] = equalSubstitute;
/* 195 */                   break;
/*     */                 }
/*     */               }
/* 198 */               substitutes[i] = current;
/* 199 */               break;
/*     */             }
/*     */ 
/* 209 */             substitutes[i] = equalSubstitute;
/* 210 */             break;
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/* 214 */     if (inferenceContext.hasUnresolvedTypeArgument())
/*     */     {
/* 217 */       for (int i = 0; i < varLength; i++) {
/* 218 */         TypeVariableBinding current = typeVariables[i];
/* 219 */         TypeBinding substitute = substitutes[i];
/* 220 */         if (substitute == null) {
/* 221 */           TypeBinding[] bounds = inferenceContext.getSubstitutes(current, 2);
/* 222 */           if (bounds != null) {
/* 223 */             TypeBinding mostSpecificSubstitute = scope.lowerUpperBound(bounds);
/* 224 */             if (mostSpecificSubstitute == null) {
/* 225 */               return false;
/*     */             }
/* 227 */             if (mostSpecificSubstitute != TypeBinding.VOID)
/* 228 */               substitutes[i] = mostSpecificSubstitute; 
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/* 232 */     if ((considerEXTENDSConstraints) && (inferenceContext.hasUnresolvedTypeArgument()))
/*     */     {
/* 235 */       for (int i = 0; i < varLength; i++) {
/* 236 */         TypeVariableBinding current = typeVariables[i];
/* 237 */         TypeBinding substitute = substitutes[i];
/* 238 */         if (substitute == null) {
/* 239 */           TypeBinding[] bounds = inferenceContext.getSubstitutes(current, 1);
/* 240 */           if (bounds != null) {
/* 241 */             TypeBinding[] glb = Scope.greaterLowerBound(bounds);
/* 242 */             TypeBinding mostSpecificSubstitute = null;
/* 243 */             if (glb != null) mostSpecificSubstitute = glb[0];
/*     */ 
/* 245 */             if (mostSpecificSubstitute != null)
/* 246 */               substitutes[i] = mostSpecificSubstitute; 
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/* 250 */     return true;
/*     */   }
/*     */ 
/*     */   public ParameterizedGenericMethodBinding(MethodBinding originalMethod, RawTypeBinding rawType, LookupEnvironment environment)
/*     */   {
/* 258 */     TypeVariableBinding[] originalVariables = originalMethod.typeVariables;
/* 259 */     int length = originalVariables.length;
/* 260 */     TypeBinding[] rawArguments = new TypeBinding[length];
/* 261 */     for (int i = 0; i < length; i++) {
/* 262 */       rawArguments[i] = environment.convertToRawType(originalVariables[i].erasure(), false);
/*     */     }
/* 264 */     this.isRaw = true;
/* 265 */     this.tagBits = originalMethod.tagBits;
/* 266 */     this.environment = environment;
/* 267 */     this.modifiers = originalMethod.modifiers;
/* 268 */     this.selector = originalMethod.selector;
/* 269 */     this.declaringClass = (rawType == null ? originalMethod.declaringClass : rawType);
/* 270 */     this.typeVariables = Binding.NO_TYPE_VARIABLES;
/* 271 */     this.typeArguments = rawArguments;
/* 272 */     this.originalMethod = originalMethod;
/* 273 */     boolean ignoreRawTypeSubstitution = (rawType == null) || (originalMethod.isStatic());
/* 274 */     this.parameters = Scope.substitute(this, ignoreRawTypeSubstitution ? 
/* 275 */       originalMethod.parameters : 
/* 276 */       Scope.substitute(rawType, originalMethod.parameters));
/* 277 */     this.thrownExceptions = Scope.substitute(this, ignoreRawTypeSubstitution ? 
/* 278 */       originalMethod.thrownExceptions : 
/* 279 */       Scope.substitute(rawType, originalMethod.thrownExceptions));
/*     */ 
/* 281 */     if (this.thrownExceptions == null) this.thrownExceptions = Binding.NO_EXCEPTIONS;
/* 282 */     this.returnType = Scope.substitute(this, ignoreRawTypeSubstitution ? 
/* 283 */       originalMethod.returnType : 
/* 284 */       Scope.substitute(rawType, originalMethod.returnType));
/* 285 */     this.wasInferred = false;
/*     */   }
/*     */ 
/*     */   public ParameterizedGenericMethodBinding(MethodBinding originalMethod, TypeBinding[] typeArguments, LookupEnvironment environment)
/*     */   {
/* 292 */     this.environment = environment;
/* 293 */     this.modifiers = originalMethod.modifiers;
/* 294 */     this.selector = originalMethod.selector;
/* 295 */     this.declaringClass = originalMethod.declaringClass;
/* 296 */     this.typeVariables = Binding.NO_TYPE_VARIABLES;
/* 297 */     this.typeArguments = typeArguments;
/* 298 */     this.isRaw = false;
/* 299 */     this.tagBits = originalMethod.tagBits;
/* 300 */     this.originalMethod = originalMethod;
/* 301 */     this.parameters = Scope.substitute(this, originalMethod.parameters);
/*     */ 
/* 303 */     this.returnType = Scope.substitute(this, originalMethod.returnType);
/* 304 */     this.thrownExceptions = Scope.substitute(this, originalMethod.thrownExceptions);
/* 305 */     if (this.thrownExceptions == null) this.thrownExceptions = Binding.NO_EXCEPTIONS;
/*     */ 
/* 307 */     if ((this.tagBits & 0x80) == 0L)
/*     */     {
/* 309 */       if ((this.returnType.tagBits & 0x80) != 0L) {
/* 310 */         this.tagBits |= 128L;
/*     */       }
/*     */       else {
/* 313 */         int i = 0; int max = this.parameters.length;
/*     */         while (true) if ((this.parameters[i].tagBits & 0x80) != 0L) {
/* 315 */             this.tagBits |= 128L;
/*     */           }
/*     */           else
/*     */           {
/* 313 */             i++; if (i < max)
/*     */             {
/*     */               continue;
/*     */             }
/*     */ 
/* 319 */             int i = 0; for (int max = this.thrownExceptions.length; i < max; i++)
/* 320 */               if ((this.thrownExceptions[i].tagBits & 0x80) != 0L) {
/* 321 */                 this.tagBits |= 128L;
/* 322 */                 break;
/*     */               }
/*     */           } 
/*     */       }
/*     */     }
/* 326 */     this.wasInferred = true;
/*     */   }
/*     */ 
/*     */   public char[] computeUniqueKey(boolean isLeaf)
/*     */   {
/* 334 */     StringBuffer buffer = new StringBuffer();
/* 335 */     buffer.append(this.originalMethod.computeUniqueKey(false));
/* 336 */     buffer.append('%');
/* 337 */     buffer.append('<');
/* 338 */     if (!this.isRaw) {
/* 339 */       int length = this.typeArguments.length;
/* 340 */       for (int i = 0; i < length; i++) {
/* 341 */         TypeBinding typeArgument = this.typeArguments[i];
/* 342 */         buffer.append(typeArgument.computeUniqueKey(false));
/*     */       }
/*     */     }
/* 345 */     buffer.append('>');
/* 346 */     int resultLength = buffer.length();
/* 347 */     char[] result = new char[resultLength];
/* 348 */     buffer.getChars(0, resultLength, result, 0);
/* 349 */     return result;
/*     */   }
/*     */ 
/*     */   public LookupEnvironment environment()
/*     */   {
/* 356 */     return this.environment;
/*     */   }
/*     */ 
/*     */   public boolean hasSubstitutedParameters()
/*     */   {
/* 364 */     if (this.wasInferred)
/* 365 */       return this.originalMethod.hasSubstitutedParameters();
/* 366 */     return super.hasSubstitutedParameters();
/*     */   }
/*     */ 
/*     */   public boolean hasSubstitutedReturnType()
/*     */   {
/* 373 */     if (this.inferredReturnType)
/* 374 */       return this.originalMethod.hasSubstitutedReturnType();
/* 375 */     return super.hasSubstitutedReturnType();
/*     */   }
/*     */ 
/*     */   private ParameterizedGenericMethodBinding inferFromExpectedType(Scope scope, InferenceContext inferenceContext)
/*     */   {
/* 382 */     TypeVariableBinding[] originalVariables = this.originalMethod.typeVariables;
/* 383 */     int varLength = originalVariables.length;
/*     */ 
/* 385 */     if (inferenceContext.expectedType != null) {
/* 386 */       this.returnType.collectSubstitutes(scope, inferenceContext.expectedType, inferenceContext, 2);
/* 387 */       if (inferenceContext.status == 1) return null;
/*     */     }
/*     */ 
/* 390 */     for (int i = 0; i < varLength; i++) {
/* 391 */       TypeVariableBinding originalVariable = originalVariables[i];
/* 392 */       TypeBinding argument = this.typeArguments[i];
/* 393 */       boolean argAlreadyInferred = argument != originalVariable;
/* 394 */       if (originalVariable.firstBound == originalVariable.superclass) {
/* 395 */         TypeBinding substitutedBound = Scope.substitute(this, originalVariable.superclass);
/* 396 */         argument.collectSubstitutes(scope, substitutedBound, inferenceContext, 2);
/* 397 */         if (inferenceContext.status == 1) return null;
/*     */ 
/* 401 */         if (argAlreadyInferred) {
/* 402 */           substitutedBound.collectSubstitutes(scope, argument, inferenceContext, 1);
/* 403 */           if (inferenceContext.status == 1) return null;
/*     */         }
/*     */       }
/* 406 */       int j = 0; for (int max = originalVariable.superInterfaces.length; j < max; j++) {
/* 407 */         TypeBinding substitutedBound = Scope.substitute(this, originalVariable.superInterfaces[j]);
/* 408 */         argument.collectSubstitutes(scope, substitutedBound, inferenceContext, 2);
/* 409 */         if (inferenceContext.status == 1) return null;
/*     */ 
/* 411 */         if (argAlreadyInferred) {
/* 412 */           substitutedBound.collectSubstitutes(scope, argument, inferenceContext, 1);
/* 413 */           if (inferenceContext.status == 1) return null;
/*     */         }
/*     */       }
/*     */     }
/* 417 */     if (!resolveSubstituteConstraints(scope, originalVariables, inferenceContext, true)) {
/* 418 */       return null;
/*     */     }
/* 420 */     for (int i = 0; i < varLength; i++) {
/* 421 */       TypeBinding substitute = inferenceContext.substitutes[i];
/* 422 */       if (substitute != null) {
/* 423 */         this.typeArguments[i] = inferenceContext.substitutes[i];
/*     */       }
/*     */       else {
/* 426 */         this.typeArguments[i] = originalVariables[i].upperBound();
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 431 */     this.typeArguments = Scope.substitute(this, this.typeArguments);
/*     */ 
/* 434 */     TypeBinding oldReturnType = this.returnType;
/* 435 */     this.returnType = Scope.substitute(this, this.returnType);
/* 436 */     this.inferredReturnType = ((inferenceContext.hasExplicitExpectedType) && (this.returnType != oldReturnType));
/* 437 */     this.parameters = Scope.substitute(this, this.parameters);
/* 438 */     this.thrownExceptions = Scope.substitute(this, this.thrownExceptions);
/*     */ 
/* 440 */     if (this.thrownExceptions == null) this.thrownExceptions = Binding.NO_EXCEPTIONS;
/*     */ 
/* 442 */     if ((this.tagBits & 0x80) == 0L)
/*     */     {
/* 444 */       if ((this.returnType.tagBits & 0x80) != 0L) {
/* 445 */         this.tagBits |= 128L;
/*     */       }
/*     */       else {
/* 448 */         int i = 0; int max = this.parameters.length;
/*     */         while (true) if ((this.parameters[i].tagBits & 0x80) != 0L) {
/* 450 */             this.tagBits |= 128L;
/*     */           }
/*     */           else
/*     */           {
/* 448 */             i++; if (i < max)
/*     */             {
/*     */               continue;
/*     */             }
/*     */ 
/* 454 */             int i = 0; for (int max = this.thrownExceptions.length; i < max; i++)
/* 455 */               if ((this.thrownExceptions[i].tagBits & 0x80) != 0L) {
/* 456 */                 this.tagBits |= 128L;
/* 457 */                 break;
/*     */               }
/*     */           } 
/*     */       }
/*     */     }
/* 461 */     return this;
/*     */   }
/*     */ 
/*     */   public boolean isRawSubstitution()
/*     */   {
/* 468 */     return this.isRaw;
/*     */   }
/*     */ 
/*     */   public TypeBinding substitute(TypeVariableBinding originalVariable)
/*     */   {
/* 475 */     TypeVariableBinding[] variables = this.originalMethod.typeVariables;
/* 476 */     int length = variables.length;
/*     */ 
/* 478 */     if ((originalVariable.rank < length) && (variables[originalVariable.rank] == originalVariable)) {
/* 479 */       return this.typeArguments[originalVariable.rank];
/*     */     }
/* 481 */     return originalVariable;
/*     */   }
/*     */ 
/*     */   public MethodBinding tiebreakMethod()
/*     */   {
/* 487 */     if (this.tiebreakMethod == null)
/* 488 */       this.tiebreakMethod = this.originalMethod.asRawMethod(this.environment);
/* 489 */     return this.tiebreakMethod;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\ecj-3.5.2.jar
 * Qualified Name:     org.eclipse.jdt.internal.compiler.lookup.ParameterizedGenericMethodBinding
 * JD-Core Version:    0.6.0
 */