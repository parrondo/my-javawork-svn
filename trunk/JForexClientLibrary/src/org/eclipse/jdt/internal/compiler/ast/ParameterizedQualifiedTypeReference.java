/*     */ package org.eclipse.jdt.internal.compiler.ast;
/*     */ 
/*     */ import org.eclipse.jdt.core.compiler.CharOperation;
/*     */ import org.eclipse.jdt.internal.compiler.ASTVisitor;
/*     */ import org.eclipse.jdt.internal.compiler.impl.CompilerOptions;
/*     */ import org.eclipse.jdt.internal.compiler.impl.Constant;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.Binding;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.BlockScope;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.ClassScope;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.LookupEnvironment;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.PackageBinding;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.ParameterizedTypeBinding;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.ReferenceBinding;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.Scope;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.TypeVariableBinding;
/*     */ import org.eclipse.jdt.internal.compiler.problem.ProblemReporter;
/*     */ 
/*     */ public class ParameterizedQualifiedTypeReference extends ArrayQualifiedTypeReference
/*     */ {
/*     */   public TypeReference[][] typeArguments;
/*     */ 
/*     */   public ParameterizedQualifiedTypeReference(char[][] tokens, TypeReference[][] typeArguments, int dim, long[] positions)
/*     */   {
/*  34 */     super(tokens, dim, positions);
/*  35 */     this.typeArguments = typeArguments;
/*     */   }
/*     */   public void checkBounds(Scope scope) {
/*  38 */     if (this.resolvedType == null) return;
/*     */ 
/*  40 */     checkBounds(
/*  41 */       (ReferenceBinding)this.resolvedType.leafComponentType(), 
/*  42 */       scope, 
/*  43 */       this.typeArguments.length - 1);
/*     */   }
/*     */ 
/*     */   public void checkBounds(ReferenceBinding type, Scope scope, int index) {
/*  47 */     if ((index > 0) && (type.enclosingType() != null)) {
/*  48 */       checkBounds(type.enclosingType(), scope, index - 1);
/*     */     }
/*  50 */     if (type.isParameterizedTypeWithActualArguments()) {
/*  51 */       ParameterizedTypeBinding parameterizedType = (ParameterizedTypeBinding)type;
/*  52 */       ReferenceBinding currentType = parameterizedType.genericType();
/*  53 */       TypeVariableBinding[] typeVariables = currentType.typeVariables();
/*  54 */       if (typeVariables != null)
/*  55 */         parameterizedType.boundCheck(scope, this.typeArguments[index]);
/*     */     }
/*     */   }
/*     */ 
/*     */   public TypeReference copyDims(int dim) {
/*  60 */     return new ParameterizedQualifiedTypeReference(this.tokens, this.typeArguments, dim, this.sourcePositions);
/*     */   }
/*     */ 
/*     */   public char[][] getParameterizedTypeName()
/*     */   {
/*  67 */     int length = this.tokens.length;
/*  68 */     char[][] qParamName = new char[length][];
/*  69 */     for (int i = 0; i < length; i++) {
/*  70 */       TypeReference[] arguments = this.typeArguments[i];
/*  71 */       if (arguments == null) {
/*  72 */         qParamName[i] = this.tokens[i];
/*     */       } else {
/*  74 */         StringBuffer buffer = new StringBuffer(5);
/*  75 */         buffer.append(this.tokens[i]);
/*  76 */         buffer.append('<');
/*  77 */         int j = 0; for (int argLength = arguments.length; j < argLength; j++) {
/*  78 */           if (j > 0) buffer.append(',');
/*  79 */           buffer.append(CharOperation.concatWith(arguments[j].getParameterizedTypeName(), '.'));
/*     */         }
/*  81 */         buffer.append('>');
/*  82 */         int nameLength = buffer.length();
/*  83 */         qParamName[i] = new char[nameLength];
/*  84 */         buffer.getChars(0, nameLength, qParamName[i], 0);
/*     */       }
/*     */     }
/*  87 */     int dim = this.dimensions;
/*  88 */     if (dim > 0) {
/*  89 */       char[] dimChars = new char[dim * 2];
/*  90 */       for (int i = 0; i < dim; i++) {
/*  91 */         int index = i * 2;
/*  92 */         dimChars[index] = '[';
/*  93 */         dimChars[(index + 1)] = ']';
/*     */       }
/*  95 */       qParamName[(length - 1)] = CharOperation.concat(qParamName[(length - 1)], dimChars);
/*     */     }
/*  97 */     return qParamName;
/*     */   }
/*     */ 
/*     */   protected TypeBinding getTypeBinding(Scope scope)
/*     */   {
/* 104 */     return null;
/*     */   }
/*     */ 
/*     */   private TypeBinding internalResolveType(Scope scope, boolean checkBounds)
/*     */   {
/* 112 */     this.constant = Constant.NotAConstant;
/* 113 */     if (((this.bits & 0x40000) != 0) && 
/* 114 */       (this.resolvedType != null) && 
/* 115 */       (this.resolvedType != null)) {
/* 116 */       if (this.resolvedType.isValidBinding()) {
/* 117 */         return this.resolvedType;
/*     */       }
/* 119 */       switch (this.resolvedType.problemId()) {
/*     */       case 1:
/*     */       case 2:
/*     */       case 5:
/* 123 */         TypeBinding type = this.resolvedType.closestMatch();
/* 124 */         return type;
/*     */       case 3:
/* 126 */       case 4: } return null;
/*     */     }
/*     */ 
/* 132 */     this.bits |= 262144;
/* 133 */     boolean isClassScope = scope.kind == 3;
/* 134 */     Binding binding = scope.getPackage(this.tokens);
/* 135 */     if ((binding != null) && (!binding.isValidBinding())) {
/* 136 */       this.resolvedType = ((ReferenceBinding)binding);
/* 137 */       reportInvalidType(scope);
/*     */ 
/* 139 */       int i = 0; for (int max = this.tokens.length; i < max; i++) {
/* 140 */         TypeReference[] args = this.typeArguments[i];
/* 141 */         if (args != null) {
/* 142 */           int argLength = args.length;
/* 143 */           for (int j = 0; j < argLength; j++) {
/* 144 */             TypeReference typeArgument = args[j];
/* 145 */             if (isClassScope)
/* 146 */               typeArgument.resolveType((ClassScope)scope);
/*     */             else {
/* 148 */               typeArgument.resolveType((BlockScope)scope, checkBounds);
/*     */             }
/*     */           }
/*     */         }
/*     */       }
/* 153 */       return null;
/*     */     }
/*     */ 
/* 156 */     PackageBinding packageBinding = binding == null ? null : (PackageBinding)binding;
/* 157 */     boolean typeIsConsistent = true;
/* 158 */     ReferenceBinding qualifyingType = null;
/* 159 */     int i = packageBinding == null ? 0 : packageBinding.compoundName.length; for (int max = this.tokens.length; i < max; i++) {
/* 160 */       findNextTypeBinding(i, scope, packageBinding);
/* 161 */       if (!this.resolvedType.isValidBinding()) {
/* 162 */         reportInvalidType(scope);
/*     */ 
/* 164 */         for (int j = i; j < max; j++) {
/* 165 */           TypeReference[] args = this.typeArguments[j];
/* 166 */           if (args != null) {
/* 167 */             int argLength = args.length;
/* 168 */             for (int k = 0; k < argLength; k++) {
/* 169 */               TypeReference typeArgument = args[k];
/* 170 */               if (isClassScope)
/* 171 */                 typeArgument.resolveType((ClassScope)scope);
/*     */               else {
/* 173 */                 typeArgument.resolveType((BlockScope)scope);
/*     */               }
/*     */             }
/*     */           }
/*     */         }
/* 178 */         return null;
/*     */       }
/* 180 */       ReferenceBinding currentType = (ReferenceBinding)this.resolvedType;
/* 181 */       if (qualifyingType == null) {
/* 182 */         qualifyingType = currentType.enclosingType();
/* 183 */         if (qualifyingType != null)
/* 184 */           qualifyingType = currentType.isStatic() ? 
/* 185 */             (ReferenceBinding)scope.environment().convertToRawType(qualifyingType, false) : 
/* 186 */             scope.environment().convertToParameterizedType(qualifyingType);
/*     */       }
/*     */       else {
/* 189 */         if ((typeIsConsistent) && (currentType.isStatic()) && (
/* 190 */           (qualifyingType.isParameterizedTypeWithActualArguments()) || (qualifyingType.isGenericType()))) {
/* 191 */           scope.problemReporter().staticMemberOfParameterizedType(this, scope.environment().createParameterizedType((ReferenceBinding)currentType.erasure(), null, qualifyingType));
/* 192 */           typeIsConsistent = false;
/*     */         }
/* 194 */         ReferenceBinding enclosingType = currentType.enclosingType();
/* 195 */         if ((enclosingType != null) && (enclosingType.erasure() != qualifyingType.erasure())) {
/* 196 */           qualifyingType = enclosingType;
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/* 201 */       TypeReference[] args = this.typeArguments[i];
/* 202 */       if (args != null) {
/* 203 */         TypeReference keep = null;
/* 204 */         if (isClassScope) {
/* 205 */           keep = ((ClassScope)scope).superTypeReference;
/* 206 */           ((ClassScope)scope).superTypeReference = null;
/*     */         }
/* 208 */         int argLength = args.length;
/* 209 */         TypeBinding[] argTypes = new TypeBinding[argLength];
/* 210 */         boolean argHasError = false;
/* 211 */         ReferenceBinding currentOriginal = (ReferenceBinding)currentType.original();
/* 212 */         for (int j = 0; j < argLength; j++) {
/* 213 */           TypeReference arg = args[j];
/* 214 */           TypeBinding argType = isClassScope ? 
/* 215 */             arg.resolveTypeArgument((ClassScope)scope, currentOriginal, j) : 
/* 216 */             arg.resolveTypeArgument((BlockScope)scope, currentOriginal, j);
/* 217 */           if (argType == null)
/* 218 */             argHasError = true;
/*     */           else {
/* 220 */             argTypes[j] = argType;
/*     */           }
/*     */         }
/* 223 */         if (argHasError) {
/* 224 */           return null;
/*     */         }
/* 226 */         if (isClassScope) {
/* 227 */           ((ClassScope)scope).superTypeReference = keep;
/* 228 */           if (((ClassScope)scope).detectHierarchyCycle(currentOriginal, this)) {
/* 229 */             return null;
/*     */           }
/*     */         }
/* 232 */         TypeVariableBinding[] typeVariables = currentOriginal.typeVariables();
/* 233 */         if (typeVariables == Binding.NO_TYPE_VARIABLES) {
/* 234 */           if (scope.compilerOptions().sourceLevel >= 3211264L) {
/* 235 */             scope.problemReporter().nonGenericTypeCannotBeParameterized(i, this, currentType, argTypes);
/* 236 */             return null;
/*     */           }
/* 238 */           this.resolvedType = ((qualifyingType != null) && (qualifyingType.isParameterizedType()) ? 
/* 239 */             scope.environment().createParameterizedType(currentOriginal, null, qualifyingType) : 
/* 240 */             currentType);
/* 241 */           if (this.dimensions > 0) {
/* 242 */             if (this.dimensions > 255)
/* 243 */               scope.problemReporter().tooManyDimensions(this);
/* 244 */             this.resolvedType = scope.createArrayType(this.resolvedType, this.dimensions);
/*     */           }
/* 246 */           return this.resolvedType;
/* 247 */         }if (argLength != typeVariables.length) {
/* 248 */           scope.problemReporter().incorrectArityForParameterizedType(this, currentType, argTypes);
/* 249 */           return null;
/*     */         }
/*     */ 
/* 252 */         if ((typeIsConsistent) && (!currentType.isStatic())) {
/* 253 */           ReferenceBinding actualEnclosing = currentType.enclosingType();
/* 254 */           if ((actualEnclosing != null) && (actualEnclosing.isRawType())) {
/* 255 */             scope.problemReporter().rawMemberTypeCannotBeParameterized(
/* 256 */               this, scope.environment().createRawType(currentOriginal, actualEnclosing), argTypes);
/* 257 */             typeIsConsistent = false;
/*     */           }
/*     */         }
/* 260 */         ParameterizedTypeBinding parameterizedType = scope.environment().createParameterizedType(currentOriginal, argTypes, qualifyingType);
/*     */ 
/* 262 */         if (checkBounds)
/* 263 */           parameterizedType.boundCheck(scope, args);
/*     */         else
/* 265 */           scope.deferBoundCheck(this);
/* 266 */         qualifyingType = parameterizedType;
/*     */       } else {
/* 268 */         ReferenceBinding currentOriginal = (ReferenceBinding)currentType.original();
/* 269 */         if ((isClassScope) && 
/* 270 */           (((ClassScope)scope).detectHierarchyCycle(currentOriginal, this)))
/* 271 */           return null;
/* 272 */         if (currentOriginal.isGenericType()) {
/* 273 */           if ((typeIsConsistent) && (qualifyingType != null) && (qualifyingType.isParameterizedType())) {
/* 274 */             scope.problemReporter().parameterizedMemberTypeMissingArguments(this, scope.environment().createParameterizedType(currentOriginal, null, qualifyingType));
/* 275 */             typeIsConsistent = false;
/*     */           }
/* 277 */           qualifyingType = scope.environment().createRawType(currentOriginal, qualifyingType);
/*     */         } else {
/* 279 */           qualifyingType = (qualifyingType != null) && (qualifyingType.isParameterizedType()) ? 
/* 280 */             scope.environment().createParameterizedType(currentOriginal, null, qualifyingType) : 
/* 281 */             currentType;
/*     */         }
/*     */       }
/* 284 */       if (isTypeUseDeprecated(qualifyingType, scope))
/* 285 */         reportDeprecatedType(qualifyingType, scope);
/* 286 */       this.resolvedType = qualifyingType;
/*     */     }
/*     */ 
/* 289 */     if (this.dimensions > 0) {
/* 290 */       if (this.dimensions > 255)
/* 291 */         scope.problemReporter().tooManyDimensions(this);
/* 292 */       this.resolvedType = scope.createArrayType(this.resolvedType, this.dimensions);
/*     */     }
/* 294 */     return this.resolvedType;
/*     */   }
/*     */ 
/*     */   public StringBuffer printExpression(int indent, StringBuffer output) {
/* 298 */     int length = this.tokens.length;
/* 299 */     for (int i = 0; i < length - 1; i++) {
/* 300 */       output.append(this.tokens[i]);
/* 301 */       TypeReference[] typeArgument = this.typeArguments[i];
/* 302 */       if (typeArgument != null) {
/* 303 */         output.append('<');
/* 304 */         int max = typeArgument.length - 1;
/* 305 */         for (int j = 0; j < max; j++) {
/* 306 */           typeArgument[j].print(0, output);
/* 307 */           output.append(", ");
/*     */         }
/* 309 */         typeArgument[max].print(0, output);
/* 310 */         output.append('>');
/*     */       }
/* 312 */       output.append('.');
/*     */     }
/* 314 */     output.append(this.tokens[(length - 1)]);
/* 315 */     TypeReference[] typeArgument = this.typeArguments[(length - 1)];
/* 316 */     if (typeArgument != null) {
/* 317 */       output.append('<');
/* 318 */       int max = typeArgument.length - 1;
/* 319 */       for (int j = 0; j < max; j++) {
/* 320 */         typeArgument[j].print(0, output);
/* 321 */         output.append(", ");
/*     */       }
/* 323 */       typeArgument[max].print(0, output);
/* 324 */       output.append('>');
/*     */     }
/* 326 */     if ((this.bits & 0x4000) != 0) {
/* 327 */       for (int i = 0; i < this.dimensions - 1; i++) {
/* 328 */         output.append("[]");
/*     */       }
/* 330 */       output.append("...");
/*     */     } else {
/* 332 */       for (int i = 0; i < this.dimensions; i++) {
/* 333 */         output.append("[]");
/*     */       }
/*     */     }
/* 336 */     return output;
/*     */   }
/*     */ 
/*     */   public TypeBinding resolveType(BlockScope scope, boolean checkBounds) {
/* 340 */     return internalResolveType(scope, checkBounds);
/*     */   }
/*     */   public TypeBinding resolveType(ClassScope scope) {
/* 343 */     return internalResolveType(scope, false);
/*     */   }
/*     */   public void traverse(ASTVisitor visitor, BlockScope scope) {
/* 346 */     if (visitor.visit(this, scope)) {
/* 347 */       int i = 0; for (int max = this.typeArguments.length; i < max; i++) {
/* 348 */         if (this.typeArguments[i] != null) {
/* 349 */           int j = 0; for (int max2 = this.typeArguments[i].length; j < max2; j++) {
/* 350 */             this.typeArguments[i][j].traverse(visitor, scope);
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/* 355 */     visitor.endVisit(this, scope);
/*     */   }
/*     */ 
/*     */   public void traverse(ASTVisitor visitor, ClassScope scope) {
/* 359 */     if (visitor.visit(this, scope)) {
/* 360 */       int i = 0; for (int max = this.typeArguments.length; i < max; i++) {
/* 361 */         if (this.typeArguments[i] != null) {
/* 362 */           int j = 0; for (int max2 = this.typeArguments[i].length; j < max2; j++) {
/* 363 */             this.typeArguments[i][j].traverse(visitor, scope);
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/* 368 */     visitor.endVisit(this, scope);
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\ecj-3.5.2.jar
 * Qualified Name:     org.eclipse.jdt.internal.compiler.ast.ParameterizedQualifiedTypeReference
 * JD-Core Version:    0.6.0
 */