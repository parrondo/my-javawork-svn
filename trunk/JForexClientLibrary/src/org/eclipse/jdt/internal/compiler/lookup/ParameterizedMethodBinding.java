/*     */ package org.eclipse.jdt.internal.compiler.lookup;
/*     */ 
/*     */ public class ParameterizedMethodBinding extends MethodBinding
/*     */ {
/*     */   protected MethodBinding originalMethod;
/*     */ 
/*     */   public ParameterizedMethodBinding(ParameterizedTypeBinding parameterizedDeclaringClass, MethodBinding originalMethod)
/*     */   {
/*  35 */     super(originalMethod.modifiers, 
/*  31 */       originalMethod.selector, 
/*  32 */       originalMethod.returnType, 
/*  33 */       originalMethod.parameters, 
/*  34 */       originalMethod.thrownExceptions, 
/*  35 */       parameterizedDeclaringClass);
/*  36 */     this.originalMethod = originalMethod;
/*  37 */     this.tagBits = originalMethod.tagBits;
/*     */ 
/*  39 */     TypeVariableBinding[] originalVariables = originalMethod.typeVariables;
/*  40 */     Substitution substitution = null;
/*  41 */     int length = originalVariables.length;
/*  42 */     boolean isStatic = originalMethod.isStatic();
/*  43 */     if (length == 0) {
/*  44 */       this.typeVariables = Binding.NO_TYPE_VARIABLES;
/*  45 */       if (!isStatic) substitution = parameterizedDeclaringClass; 
/*     */     }
/*     */     else
/*     */     {
/*  48 */       TypeVariableBinding[] substitutedVariables = new TypeVariableBinding[length];
/*  49 */       for (int i = 0; i < length; i++) {
/*  50 */         TypeVariableBinding originalVariable = originalVariables[i];
/*  51 */         substitutedVariables[i] = new TypeVariableBinding(originalVariable.sourceName, this, originalVariable.rank, parameterizedDeclaringClass.environment);
/*     */       }
/*  53 */       this.typeVariables = substitutedVariables;
/*     */ 
/*  56 */       substitution = new Substitution(parameterizedDeclaringClass, isStatic, length, originalVariables, substitutedVariables) { private final ParameterizedTypeBinding val$parameterizedDeclaringClass;
/*     */         private final boolean val$isStatic;
/*     */         private final int val$length;
/*     */         private final TypeVariableBinding[] val$originalVariables;
/*     */         private final TypeVariableBinding[] val$substitutedVariables;
/*     */ 
/*  58 */         public LookupEnvironment environment() { return this.val$parameterizedDeclaringClass.environment; }
/*     */ 
/*     */         public boolean isRawSubstitution() {
/*  61 */           return (!this.val$isStatic) && (this.val$parameterizedDeclaringClass.isRawSubstitution());
/*     */         }
/*     */ 
/*     */         public TypeBinding substitute(TypeVariableBinding typeVariable) {
/*  65 */           if ((typeVariable.rank < this.val$length) && (this.val$originalVariables[typeVariable.rank] == typeVariable)) {
/*  66 */             return this.val$substitutedVariables[typeVariable.rank];
/*     */           }
/*  68 */           if (!this.val$isStatic)
/*  69 */             return this.val$parameterizedDeclaringClass.substitute(typeVariable);
/*  70 */           return typeVariable;
/*     */         }
/*     */       };
/*  75 */       for (int i = 0; i < length; i++) {
/*  76 */         TypeVariableBinding originalVariable = originalVariables[i];
/*  77 */         TypeVariableBinding substitutedVariable = substitutedVariables[i];
/*  78 */         TypeBinding substitutedSuperclass = Scope.substitute(substitution, originalVariable.superclass);
/*  79 */         ReferenceBinding[] substitutedInterfaces = Scope.substitute(substitution, originalVariable.superInterfaces);
/*  80 */         if (originalVariable.firstBound != null) {
/*  81 */           substitutedVariable.firstBound = (originalVariable.firstBound == originalVariable.superclass ? 
/*  82 */             substitutedSuperclass : 
/*  83 */             substitutedInterfaces[0]);
/*     */         }
/*  85 */         switch (substitutedSuperclass.kind()) {
/*     */         case 68:
/*  87 */           substitutedVariable.superclass = parameterizedDeclaringClass.environment.getResolvedType(TypeConstants.JAVA_LANG_OBJECT, null);
/*  88 */           substitutedVariable.superInterfaces = substitutedInterfaces;
/*  89 */           break;
/*     */         default:
/*  91 */           if (substitutedSuperclass.isInterface()) {
/*  92 */             substitutedVariable.superclass = parameterizedDeclaringClass.environment.getResolvedType(TypeConstants.JAVA_LANG_OBJECT, null);
/*  93 */             int interfaceCount = substitutedInterfaces.length;
/*  94 */             System.arraycopy(substitutedInterfaces, 0, substitutedInterfaces = new ReferenceBinding[interfaceCount + 1], 1, interfaceCount);
/*  95 */             substitutedInterfaces[0] = ((ReferenceBinding)substitutedSuperclass);
/*  96 */             substitutedVariable.superInterfaces = substitutedInterfaces;
/*     */           } else {
/*  98 */             substitutedVariable.superclass = ((ReferenceBinding)substitutedSuperclass);
/*  99 */             substitutedVariable.superInterfaces = substitutedInterfaces;
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/* 104 */     if (substitution != null) {
/* 105 */       this.returnType = Scope.substitute(substitution, this.returnType);
/* 106 */       this.parameters = Scope.substitute(substitution, this.parameters);
/* 107 */       this.thrownExceptions = Scope.substitute(substitution, this.thrownExceptions);
/*     */ 
/* 109 */       if (this.thrownExceptions == null) this.thrownExceptions = Binding.NO_EXCEPTIONS;
/*     */     }
/*     */ 
/* 112 */     if ((this.tagBits & 0x80) == 0L)
/*     */     {
/* 114 */       if ((this.returnType.tagBits & 0x80) != 0L) {
/* 115 */         this.tagBits |= 128L;
/*     */       }
/*     */       else {
/* 118 */         int i = 0; int max = this.parameters.length;
/*     */         while (true) if ((this.parameters[i].tagBits & 0x80) != 0L) {
/* 120 */             this.tagBits |= 128L;
/*     */           }
/*     */           else
/*     */           {
/* 118 */             i++; if (i < max)
/*     */             {
/*     */               continue;
/*     */             }
/*     */ 
/* 124 */             int i = 0; for (int max = this.thrownExceptions.length; i < max; i++)
/* 125 */               if ((this.thrownExceptions[i].tagBits & 0x80) != 0L) {
/* 126 */                 this.tagBits |= 128L;
/* 127 */                 break;
/*     */               }
/*     */           }
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public ParameterizedMethodBinding(ReferenceBinding declaringClass, MethodBinding originalMethod, char[][] alternateParamaterNames, LookupEnvironment environment)
/*     */   {
/* 144 */     super(originalMethod.modifiers, 
/* 140 */       originalMethod.selector, 
/* 141 */       originalMethod.returnType, 
/* 142 */       originalMethod.parameters, 
/* 143 */       originalMethod.thrownExceptions, 
/* 144 */       declaringClass);
/* 145 */     this.originalMethod = originalMethod;
/* 146 */     this.tagBits = originalMethod.tagBits;
/*     */ 
/* 148 */     TypeVariableBinding[] originalVariables = originalMethod.typeVariables;
/* 149 */     Substitution substitution = null;
/* 150 */     int length = originalVariables.length;
/* 151 */     if (length == 0) {
/* 152 */       this.typeVariables = Binding.NO_TYPE_VARIABLES;
/*     */     }
/*     */     else {
/* 155 */       TypeVariableBinding[] substitutedVariables = new TypeVariableBinding[length];
/* 156 */       for (int i = 0; i < length; i++) {
/* 157 */         TypeVariableBinding originalVariable = originalVariables[i];
/* 158 */         substitutedVariables[i] = 
/* 164 */           new TypeVariableBinding(alternateParamaterNames == null ? 
/* 160 */           originalVariable.sourceName : 
/* 161 */           alternateParamaterNames[i], 
/* 162 */           this, 
/* 163 */           originalVariable.rank, 
/* 164 */           environment);
/*     */       }
/* 166 */       this.typeVariables = substitutedVariables;
/*     */ 
/* 169 */       substitution = new Substitution(environment, length, originalVariables, substitutedVariables) { private final LookupEnvironment val$environment;
/*     */         private final int val$length;
/*     */         private final TypeVariableBinding[] val$originalVariables;
/*     */         private final TypeVariableBinding[] val$substitutedVariables;
/*     */ 
/* 171 */         public LookupEnvironment environment() { return this.val$environment; }
/*     */ 
/*     */         public boolean isRawSubstitution() {
/* 174 */           return false;
/*     */         }
/*     */ 
/*     */         public TypeBinding substitute(TypeVariableBinding typeVariable) {
/* 178 */           if ((typeVariable.rank < this.val$length) && (this.val$originalVariables[typeVariable.rank] == typeVariable)) {
/* 179 */             return this.val$substitutedVariables[typeVariable.rank];
/*     */           }
/* 181 */           return typeVariable;
/*     */         }
/*     */       };
/* 186 */       for (int i = 0; i < length; i++) {
/* 187 */         TypeVariableBinding originalVariable = originalVariables[i];
/* 188 */         TypeVariableBinding substitutedVariable = substitutedVariables[i];
/* 189 */         TypeBinding substitutedSuperclass = Scope.substitute(substitution, originalVariable.superclass);
/* 190 */         ReferenceBinding[] substitutedInterfaces = Scope.substitute(substitution, originalVariable.superInterfaces);
/* 191 */         if (originalVariable.firstBound != null) {
/* 192 */           substitutedVariable.firstBound = (originalVariable.firstBound == originalVariable.superclass ? 
/* 193 */             substitutedSuperclass : 
/* 194 */             substitutedInterfaces[0]);
/*     */         }
/* 196 */         switch (substitutedSuperclass.kind()) {
/*     */         case 68:
/* 198 */           substitutedVariable.superclass = environment.getResolvedType(TypeConstants.JAVA_LANG_OBJECT, null);
/* 199 */           substitutedVariable.superInterfaces = substitutedInterfaces;
/* 200 */           break;
/*     */         default:
/* 202 */           if (substitutedSuperclass.isInterface()) {
/* 203 */             substitutedVariable.superclass = environment.getResolvedType(TypeConstants.JAVA_LANG_OBJECT, null);
/* 204 */             int interfaceCount = substitutedInterfaces.length;
/* 205 */             System.arraycopy(substitutedInterfaces, 0, substitutedInterfaces = new ReferenceBinding[interfaceCount + 1], 1, interfaceCount);
/* 206 */             substitutedInterfaces[0] = ((ReferenceBinding)substitutedSuperclass);
/* 207 */             substitutedVariable.superInterfaces = substitutedInterfaces;
/*     */           } else {
/* 209 */             substitutedVariable.superclass = ((ReferenceBinding)substitutedSuperclass);
/* 210 */             substitutedVariable.superInterfaces = substitutedInterfaces;
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/* 215 */     if (substitution != null) {
/* 216 */       this.returnType = Scope.substitute(substitution, this.returnType);
/* 217 */       this.parameters = Scope.substitute(substitution, this.parameters);
/* 218 */       this.thrownExceptions = Scope.substitute(substitution, this.thrownExceptions);
/*     */ 
/* 220 */       if (this.thrownExceptions == null) this.thrownExceptions = Binding.NO_EXCEPTIONS;
/*     */     }
/*     */ 
/* 223 */     if ((this.tagBits & 0x80) == 0L)
/*     */     {
/* 225 */       if ((this.returnType.tagBits & 0x80) != 0L) {
/* 226 */         this.tagBits |= 128L;
/*     */       }
/*     */       else {
/* 229 */         int i = 0; int max = this.parameters.length;
/*     */         while (true) if ((this.parameters[i].tagBits & 0x80) != 0L) {
/* 231 */             this.tagBits |= 128L;
/*     */           }
/*     */           else
/*     */           {
/* 229 */             i++; if (i < max)
/*     */             {
/*     */               continue;
/*     */             }
/*     */ 
/* 235 */             int i = 0; for (int max = this.thrownExceptions.length; i < max; i++)
/* 236 */               if ((this.thrownExceptions[i].tagBits & 0x80) != 0L) {
/* 237 */                 this.tagBits |= 128L;
/* 238 */                 break;
/*     */               }
/*     */           }
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public ParameterizedMethodBinding()
/*     */   {
/*     */   }
/*     */ 
/*     */   public static ParameterizedMethodBinding instantiateGetClass(TypeBinding receiverType, MethodBinding originalMethod, Scope scope)
/*     */   {
/* 252 */     ParameterizedMethodBinding method = new ParameterizedMethodBinding();
/* 253 */     method.modifiers = originalMethod.modifiers;
/* 254 */     method.selector = originalMethod.selector;
/* 255 */     method.declaringClass = originalMethod.declaringClass;
/* 256 */     method.typeVariables = Binding.NO_TYPE_VARIABLES;
/* 257 */     method.originalMethod = originalMethod;
/* 258 */     method.parameters = originalMethod.parameters;
/* 259 */     method.thrownExceptions = originalMethod.thrownExceptions;
/* 260 */     method.tagBits = originalMethod.tagBits;
/* 261 */     ReferenceBinding genericClassType = scope.getJavaLangClass();
/* 262 */     LookupEnvironment environment = scope.environment();
/* 263 */     TypeBinding rawType = environment.convertToRawType(receiverType.erasure(), false);
/* 264 */     method.returnType = environment.createParameterizedType(
/* 265 */       genericClassType, 
/* 266 */       new TypeBinding[] { environment.createWildcard(genericClassType, 0, rawType, null, 1) }, 
/* 267 */       null);
/* 268 */     if ((method.returnType.tagBits & 0x80) != 0L) {
/* 269 */       method.tagBits |= 128L;
/*     */     }
/* 271 */     return method;
/*     */   }
/*     */ 
/*     */   public boolean hasSubstitutedParameters()
/*     */   {
/* 278 */     return this.parameters != this.originalMethod.parameters;
/*     */   }
/*     */ 
/*     */   public boolean hasSubstitutedReturnType()
/*     */   {
/* 285 */     return this.returnType != this.originalMethod.returnType;
/*     */   }
/*     */ 
/*     */   public MethodBinding original()
/*     */   {
/* 292 */     return this.originalMethod.original();
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\ecj-3.5.2.jar
 * Qualified Name:     org.eclipse.jdt.internal.compiler.lookup.ParameterizedMethodBinding
 * JD-Core Version:    0.6.0
 */