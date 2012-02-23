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
/*     */ import org.eclipse.jdt.internal.compiler.lookup.ParameterizedTypeBinding;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.ReferenceBinding;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.Scope;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.TypeVariableBinding;
/*     */ import org.eclipse.jdt.internal.compiler.problem.ProblemReporter;
/*     */ 
/*     */ public class ParameterizedSingleTypeReference extends ArrayTypeReference
/*     */ {
/*     */   public TypeReference[] typeArguments;
/*     */ 
/*     */   public ParameterizedSingleTypeReference(char[] name, TypeReference[] typeArguments, int dim, long pos)
/*     */   {
/*  28 */     super(name, dim, pos);
/*  29 */     this.originalSourceEnd = this.sourceEnd;
/*  30 */     this.typeArguments = typeArguments;
/*     */   }
/*     */   public void checkBounds(Scope scope) {
/*  33 */     if (this.resolvedType == null) return;
/*     */ 
/*  35 */     if ((this.resolvedType.leafComponentType() instanceof ParameterizedTypeBinding)) {
/*  36 */       ParameterizedTypeBinding parameterizedType = (ParameterizedTypeBinding)this.resolvedType.leafComponentType();
/*  37 */       ReferenceBinding currentType = parameterizedType.genericType();
/*  38 */       TypeVariableBinding[] typeVariables = currentType.typeVariables();
/*  39 */       TypeBinding[] argTypes = parameterizedType.arguments;
/*  40 */       if ((argTypes != null) && (typeVariables != null))
/*  41 */         parameterizedType.boundCheck(scope, this.typeArguments);
/*     */     }
/*     */   }
/*     */ 
/*     */   public TypeReference copyDims(int dim)
/*     */   {
/*  49 */     return new ParameterizedSingleTypeReference(this.token, this.typeArguments, dim, (this.sourceStart << 32) + this.sourceEnd);
/*     */   }
/*     */ 
/*     */   public char[][] getParameterizedTypeName()
/*     */   {
/*  56 */     StringBuffer buffer = new StringBuffer(5);
/*  57 */     buffer.append(this.token).append('<');
/*  58 */     int i = 0; for (int length = this.typeArguments.length; i < length; i++) {
/*  59 */       if (i > 0) buffer.append(',');
/*  60 */       buffer.append(CharOperation.concatWith(this.typeArguments[i].getParameterizedTypeName(), '.'));
/*     */     }
/*  62 */     buffer.append('>');
/*  63 */     int nameLength = buffer.length();
/*  64 */     char[] name = new char[nameLength];
/*  65 */     buffer.getChars(0, nameLength, name, 0);
/*  66 */     int dim = this.dimensions;
/*  67 */     if (dim > 0) {
/*  68 */       char[] dimChars = new char[dim * 2];
/*  69 */       for (int i = 0; i < dim; i++) {
/*  70 */         int index = i * 2;
/*  71 */         dimChars[index] = '[';
/*  72 */         dimChars[(index + 1)] = ']';
/*     */       }
/*  74 */       name = CharOperation.concat(name, dimChars);
/*     */     }
/*  76 */     return new char[][] { name };
/*     */   }
/*     */ 
/*     */   protected TypeBinding getTypeBinding(Scope scope)
/*     */   {
/*  82 */     return null;
/*     */   }
/*     */ 
/*     */   private TypeBinding internalResolveType(Scope scope, ReferenceBinding enclosingType, boolean checkBounds)
/*     */   {
/*  90 */     this.constant = Constant.NotAConstant;
/*  91 */     if (((this.bits & 0x40000) != 0) && 
/*  92 */       (this.resolvedType != null)) {
/*  93 */       if (this.resolvedType.isValidBinding()) {
/*  94 */         return this.resolvedType;
/*     */       }
/*  96 */       switch (this.resolvedType.problemId()) {
/*     */       case 1:
/*     */       case 2:
/*     */       case 5:
/* 100 */         TypeBinding type = this.resolvedType.closestMatch();
/* 101 */         return type;
/*     */       case 3:
/* 103 */       case 4: } return null;
/*     */     }
/*     */ 
/* 108 */     boolean hasGenericError = false;
/*     */ 
/* 110 */     this.bits |= 262144;
/*     */     ReferenceBinding currentType;
/* 111 */     if (enclosingType == null) {
/* 112 */       this.resolvedType = scope.getType(this.token);
/*     */       ReferenceBinding currentType;
/* 113 */       if (this.resolvedType.isValidBinding()) {
/* 114 */         currentType = (ReferenceBinding)this.resolvedType;
/*     */       } else {
/* 116 */         hasGenericError = true;
/* 117 */         reportInvalidType(scope);
/*     */         ReferenceBinding currentType;
/* 118 */         switch (this.resolvedType.problemId()) {
/*     */         case 1:
/*     */         case 2:
/*     */         case 5:
/* 122 */           TypeBinding type = this.resolvedType.closestMatch();
/* 123 */           if (!(type instanceof ReferenceBinding)) break;
/* 124 */           currentType = (ReferenceBinding)type;
/* 125 */           break;
/*     */         case 3:
/*     */         case 4:
/*     */         }
/* 129 */         boolean isClassScope = scope.kind == 3;
/* 130 */         int argLength = this.typeArguments.length;
/* 131 */         for (int i = 0; i < argLength; i++) {
/* 132 */           TypeReference typeArgument = this.typeArguments[i];
/* 133 */           if (isClassScope)
/* 134 */             typeArgument.resolveType((ClassScope)scope);
/*     */           else {
/* 136 */             typeArgument.resolveType((BlockScope)scope, checkBounds);
/*     */           }
/*     */         }
/* 139 */         return null;
/*     */       }
/*     */       ReferenceBinding currentType;
/* 143 */       enclosingType = currentType.enclosingType();
/* 144 */       if (enclosingType != null) {
/* 145 */         enclosingType = currentType.isStatic() ? 
/* 146 */           (ReferenceBinding)scope.environment().convertToRawType(enclosingType, false) : 
/* 147 */           scope.environment().convertToParameterizedType(enclosingType);
/* 148 */         currentType = scope.environment().createParameterizedType((ReferenceBinding)currentType.erasure(), null, enclosingType);
/*     */       }
/*     */     } else {
/* 151 */       this.resolvedType = (currentType = scope.getMemberType(this.token, enclosingType));
/* 152 */       if (!this.resolvedType.isValidBinding()) {
/* 153 */         hasGenericError = true;
/* 154 */         scope.problemReporter().invalidEnclosingType(this, currentType, enclosingType);
/* 155 */         return null;
/*     */       }
/* 157 */       if (isTypeUseDeprecated(currentType, scope))
/* 158 */         scope.problemReporter().deprecatedType(currentType, this);
/* 159 */       ReferenceBinding currentEnclosing = currentType.enclosingType();
/* 160 */       if ((currentEnclosing != null) && (currentEnclosing.erasure() != enclosingType.erasure())) {
/* 161 */         enclosingType = currentEnclosing;
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 166 */     boolean isClassScope = scope.kind == 3;
/* 167 */     TypeReference keep = null;
/* 168 */     if (isClassScope) {
/* 169 */       keep = ((ClassScope)scope).superTypeReference;
/* 170 */       ((ClassScope)scope).superTypeReference = null;
/*     */     }
/* 172 */     int argLength = this.typeArguments.length;
/* 173 */     TypeBinding[] argTypes = new TypeBinding[argLength];
/* 174 */     boolean argHasError = false;
/* 175 */     ReferenceBinding currentOriginal = (ReferenceBinding)currentType.original();
/* 176 */     for (int i = 0; i < argLength; i++) {
/* 177 */       TypeReference typeArgument = this.typeArguments[i];
/* 178 */       TypeBinding argType = isClassScope ? 
/* 179 */         typeArgument.resolveTypeArgument((ClassScope)scope, currentOriginal, i) : 
/* 180 */         typeArgument.resolveTypeArgument((BlockScope)scope, currentOriginal, i);
/* 181 */       if (argType == null)
/* 182 */         argHasError = true;
/*     */       else {
/* 184 */         argTypes[i] = argType;
/*     */       }
/*     */     }
/* 187 */     if (argHasError) {
/* 188 */       return null;
/*     */     }
/* 190 */     if (isClassScope) {
/* 191 */       ((ClassScope)scope).superTypeReference = keep;
/* 192 */       if (((ClassScope)scope).detectHierarchyCycle(currentOriginal, this)) {
/* 193 */         return null;
/*     */       }
/*     */     }
/* 196 */     TypeVariableBinding[] typeVariables = currentOriginal.typeVariables();
/* 197 */     if (typeVariables == Binding.NO_TYPE_VARIABLES) {
/* 198 */       boolean isCompliant15 = scope.compilerOptions().sourceLevel >= 3211264L;
/* 199 */       if (((currentOriginal.tagBits & 0x80) == 0L) && 
/* 200 */         (isCompliant15)) {
/* 201 */         this.resolvedType = currentType;
/* 202 */         scope.problemReporter().nonGenericTypeCannotBeParameterized(0, this, currentType, argTypes);
/* 203 */         return null;
/*     */       }
/*     */ 
/* 207 */       if (!isCompliant15)
/*     */       {
/* 209 */         TypeBinding type = currentType;
/* 210 */         if (this.dimensions > 0) {
/* 211 */           if (this.dimensions > 255)
/* 212 */             scope.problemReporter().tooManyDimensions(this);
/* 213 */           type = scope.createArrayType(type, this.dimensions);
/*     */         }
/* 215 */         if (hasGenericError)
/* 216 */           return type;
/* 217 */         return this.resolvedType = type;
/*     */       }
/*     */     } else {
/* 220 */       if (argLength != typeVariables.length) {
/* 221 */         scope.problemReporter().incorrectArityForParameterizedType(this, currentType, argTypes);
/* 222 */         return null;
/* 223 */       }if (!currentType.isStatic()) {
/* 224 */         ReferenceBinding actualEnclosing = currentType.enclosingType();
/* 225 */         if ((actualEnclosing != null) && (actualEnclosing.isRawType())) {
/* 226 */           scope.problemReporter().rawMemberTypeCannotBeParameterized(
/* 227 */             this, scope.environment().createRawType(currentOriginal, actualEnclosing), argTypes);
/* 228 */           return null;
/*     */         }
/*     */       }
/*     */     }
/* 232 */     ParameterizedTypeBinding parameterizedType = scope.environment().createParameterizedType(currentOriginal, argTypes, enclosingType);
/*     */ 
/* 234 */     if (checkBounds)
/* 235 */       parameterizedType.boundCheck(scope, this.typeArguments);
/*     */     else
/* 237 */       scope.deferBoundCheck(this);
/* 238 */     if (isTypeUseDeprecated(parameterizedType, scope)) {
/* 239 */       reportDeprecatedType(parameterizedType, scope);
/*     */     }
/* 241 */     TypeBinding type = parameterizedType;
/*     */ 
/* 243 */     if (this.dimensions > 0) {
/* 244 */       if (this.dimensions > 255)
/* 245 */         scope.problemReporter().tooManyDimensions(this);
/* 246 */       type = scope.createArrayType(type, this.dimensions);
/*     */     }
/* 248 */     if (hasGenericError) {
/* 249 */       return type;
/*     */     }
/* 251 */     return this.resolvedType = type;
/*     */   }
/*     */ 
/*     */   public StringBuffer printExpression(int indent, StringBuffer output) {
/* 255 */     output.append(this.token);
/* 256 */     output.append("<");
/* 257 */     int max = this.typeArguments.length - 1;
/* 258 */     for (int i = 0; i < max; i++) {
/* 259 */       this.typeArguments[i].print(0, output);
/* 260 */       output.append(", ");
/*     */     }
/* 262 */     this.typeArguments[max].print(0, output);
/* 263 */     output.append(">");
/* 264 */     if ((this.bits & 0x4000) != 0) {
/* 265 */       for (int i = 0; i < this.dimensions - 1; i++) {
/* 266 */         output.append("[]");
/*     */       }
/* 268 */       output.append("...");
/*     */     } else {
/* 270 */       for (int i = 0; i < this.dimensions; i++) {
/* 271 */         output.append("[]");
/*     */       }
/*     */     }
/* 274 */     return output;
/*     */   }
/*     */ 
/*     */   public TypeBinding resolveType(BlockScope scope, boolean checkBounds) {
/* 278 */     return internalResolveType(scope, null, checkBounds);
/*     */   }
/*     */ 
/*     */   public TypeBinding resolveType(ClassScope scope) {
/* 282 */     return internalResolveType(scope, null, false);
/*     */   }
/*     */ 
/*     */   public TypeBinding resolveTypeEnclosing(BlockScope scope, ReferenceBinding enclosingType) {
/* 286 */     return internalResolveType(scope, enclosingType, true);
/*     */   }
/*     */ 
/*     */   public void traverse(ASTVisitor visitor, BlockScope scope) {
/* 290 */     if (visitor.visit(this, scope)) {
/* 291 */       int i = 0; for (int max = this.typeArguments.length; i < max; i++) {
/* 292 */         this.typeArguments[i].traverse(visitor, scope);
/*     */       }
/*     */     }
/* 295 */     visitor.endVisit(this, scope);
/*     */   }
/*     */ 
/*     */   public void traverse(ASTVisitor visitor, ClassScope scope) {
/* 299 */     if (visitor.visit(this, scope)) {
/* 300 */       int i = 0; for (int max = this.typeArguments.length; i < max; i++) {
/* 301 */         this.typeArguments[i].traverse(visitor, scope);
/*     */       }
/*     */     }
/* 304 */     visitor.endVisit(this, scope);
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\ecj-3.5.2.jar
 * Qualified Name:     org.eclipse.jdt.internal.compiler.ast.ParameterizedSingleTypeReference
 * JD-Core Version:    0.6.0
 */