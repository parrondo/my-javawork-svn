/*     */ package org.eclipse.jdt.internal.compiler.lookup;
/*     */ 
/*     */ import org.eclipse.jdt.core.compiler.CharOperation;
/*     */ import org.eclipse.jdt.internal.compiler.ast.ASTNode;
/*     */ import org.eclipse.jdt.internal.compiler.ast.FieldDeclaration;
/*     */ import org.eclipse.jdt.internal.compiler.ast.TypeDeclaration;
/*     */ import org.eclipse.jdt.internal.compiler.impl.Constant;
/*     */ 
/*     */ public class FieldBinding extends VariableBinding
/*     */ {
/*     */   public ReferenceBinding declaringClass;
/*     */ 
/*     */   protected FieldBinding()
/*     */   {
/*  23 */     super(null, null, 0, null);
/*     */   }
/*     */ 
/*     */   public FieldBinding(char[] name, TypeBinding type, int modifiers, ReferenceBinding declaringClass, Constant constant) {
/*  27 */     super(name, type, modifiers, constant);
/*  28 */     this.declaringClass = declaringClass;
/*     */   }
/*     */ 
/*     */   public FieldBinding(FieldBinding initialFieldBinding, ReferenceBinding declaringClass) {
/*  32 */     super(initialFieldBinding.name, initialFieldBinding.type, initialFieldBinding.modifiers, initialFieldBinding.constant());
/*  33 */     this.declaringClass = declaringClass;
/*  34 */     this.id = initialFieldBinding.id;
/*  35 */     setAnnotations(initialFieldBinding.getAnnotations());
/*     */   }
/*     */ 
/*     */   public FieldBinding(FieldDeclaration field, TypeBinding type, int modifiers, ReferenceBinding declaringClass)
/*     */   {
/*  41 */     this(field.name, type, modifiers, declaringClass, null);
/*  42 */     field.binding = this;
/*     */   }
/*     */ 
/*     */   public final boolean canBeSeenBy(PackageBinding invocationPackage) {
/*  46 */     if (isPublic()) return true;
/*  47 */     if (isPrivate()) return false;
/*     */ 
/*  50 */     return invocationPackage == this.declaringClass.getPackage();
/*     */   }
/*     */ 
/*     */   public final boolean canBeSeenBy(TypeBinding receiverType, InvocationSite invocationSite, Scope scope)
/*     */   {
/*  60 */     if (isPublic()) return true;
/*     */ 
/*  62 */     SourceTypeBinding invocationType = scope.enclosingSourceType();
/*  63 */     if ((invocationType == this.declaringClass) && (invocationType == receiverType)) return true;
/*     */ 
/*  65 */     if (invocationType == null) {
/*  66 */       return (!isPrivate()) && (scope.getCurrentPackage() == this.declaringClass.fPackage);
/*     */     }
/*  68 */     if (isProtected())
/*     */     {
/*  74 */       if (invocationType == this.declaringClass) return true;
/*  75 */       if (invocationType.fPackage == this.declaringClass.fPackage) return true;
/*     */ 
/*  77 */       ReferenceBinding currentType = invocationType;
/*  78 */       int depth = 0;
/*  79 */       ReferenceBinding receiverErasure = (ReferenceBinding)receiverType.erasure();
/*  80 */       ReferenceBinding declaringErasure = (ReferenceBinding)this.declaringClass.erasure();
/*     */       do {
/*  82 */         if (currentType.findSuperTypeOriginatingFrom(declaringErasure) != null) {
/*  83 */           if (invocationSite.isSuperAccess()) {
/*  84 */             return true;
/*     */           }
/*  86 */           if ((receiverType instanceof ArrayBinding))
/*  87 */             return false;
/*  88 */           if (isStatic()) {
/*  89 */             if (depth > 0) invocationSite.setDepth(depth);
/*  90 */             return true;
/*     */           }
/*  92 */           if ((currentType == receiverErasure) || (receiverErasure.findSuperTypeOriginatingFrom(currentType) != null)) {
/*  93 */             if (depth > 0) invocationSite.setDepth(depth);
/*  94 */             return true;
/*     */           }
/*     */         }
/*  97 */         depth++;
/*  98 */         currentType = currentType.enclosingType();
/*  99 */       }while (currentType != null);
/* 100 */       return false;
/*     */     }
/*     */ 
/* 103 */     if (isPrivate())
/*     */     {
/* 107 */       if (receiverType != this.declaringClass)
/*     */       {
/* 109 */         if ((!receiverType.isTypeVariable()) || (!((TypeVariableBinding)receiverType).isErasureBoundTo(this.declaringClass.erasure())))
/*     */         {
/* 111 */           return false;
/*     */         }
/*     */       }
/*     */ 
/* 115 */       if (invocationType != this.declaringClass) {
/* 116 */         ReferenceBinding outerInvocationType = invocationType;
/* 117 */         ReferenceBinding temp = outerInvocationType.enclosingType();
/* 118 */         while (temp != null) {
/* 119 */           outerInvocationType = temp;
/* 120 */           temp = temp.enclosingType();
/*     */         }
/*     */ 
/* 123 */         ReferenceBinding outerDeclaringClass = (ReferenceBinding)this.declaringClass.erasure();
/* 124 */         temp = outerDeclaringClass.enclosingType();
/* 125 */         while (temp != null) {
/* 126 */           outerDeclaringClass = temp;
/* 127 */           temp = temp.enclosingType();
/*     */         }
/* 129 */         if (outerInvocationType != outerDeclaringClass) return false;
/*     */       }
/* 131 */       return true;
/*     */     }
/*     */ 
/* 135 */     PackageBinding declaringPackage = this.declaringClass.fPackage;
/* 136 */     if (invocationType.fPackage != declaringPackage) return false;
/*     */ 
/* 139 */     if ((receiverType instanceof ArrayBinding))
/* 140 */       return false;
/* 141 */     TypeBinding originalDeclaringClass = this.declaringClass.original();
/* 142 */     ReferenceBinding currentType = (ReferenceBinding)receiverType;
/*     */     do {
/* 144 */       if (originalDeclaringClass == currentType.original()) return true;
/* 145 */       PackageBinding currentPackage = currentType.fPackage;
/*     */ 
/* 147 */       if ((currentPackage != null) && (currentPackage != declaringPackage)) return false; 
/*     */     }
/* 148 */     while ((currentType = currentType.superclass()) != null);
/* 149 */     return false;
/*     */   }
/*     */ 
/*     */   public char[] computeUniqueKey(boolean isLeaf)
/*     */   {
/* 158 */     char[] declaringKey = 
/* 159 */       this.declaringClass == null ? 
/* 160 */       CharOperation.NO_CHAR : 
/* 161 */       this.declaringClass.computeUniqueKey(false);
/* 162 */     int declaringLength = declaringKey.length;
/*     */ 
/* 165 */     int nameLength = this.name.length;
/*     */ 
/* 168 */     char[] returnTypeKey = this.type == null ? new char[] { 'V' } : this.type.computeUniqueKey(false);
/* 169 */     int returnTypeLength = returnTypeKey.length;
/*     */ 
/* 171 */     char[] uniqueKey = new char[declaringLength + 1 + nameLength + 1 + returnTypeLength];
/* 172 */     int index = 0;
/* 173 */     System.arraycopy(declaringKey, 0, uniqueKey, index, declaringLength);
/* 174 */     index += declaringLength;
/* 175 */     uniqueKey[(index++)] = '.';
/* 176 */     System.arraycopy(this.name, 0, uniqueKey, index, nameLength);
/* 177 */     index += nameLength;
/* 178 */     uniqueKey[(index++)] = ')';
/* 179 */     System.arraycopy(returnTypeKey, 0, uniqueKey, index, returnTypeLength);
/* 180 */     return uniqueKey;
/*     */   }
/*     */   public Constant constant() {
/* 183 */     Constant fieldConstant = this.constant;
/* 184 */     if (fieldConstant == null) {
/* 185 */       if (isFinal())
/*     */       {
/* 190 */         FieldBinding originalField = original();
/* 191 */         if ((originalField.declaringClass instanceof SourceTypeBinding)) {
/* 192 */           SourceTypeBinding sourceType = (SourceTypeBinding)originalField.declaringClass;
/* 193 */           if (sourceType.scope != null) {
/* 194 */             TypeDeclaration typeDecl = sourceType.scope.referenceContext;
/* 195 */             FieldDeclaration fieldDecl = typeDecl.declarationOf(originalField);
/* 196 */             MethodScope initScope = originalField.isStatic() ? typeDecl.staticInitializerScope : typeDecl.initializerScope;
/* 197 */             boolean old = initScope.insideTypeAnnotation;
/*     */             try {
/* 199 */               initScope.insideTypeAnnotation = false;
/* 200 */               fieldDecl.resolve(initScope);
/*     */             } finally {
/* 202 */               initScope.insideTypeAnnotation = old;
/*     */             }
/* 204 */             fieldConstant = originalField.constant == null ? Constant.NotAConstant : originalField.constant;
/*     */           } else {
/* 206 */             fieldConstant = Constant.NotAConstant;
/*     */           }
/*     */         } else {
/* 209 */           fieldConstant = Constant.NotAConstant;
/*     */         }
/*     */       } else {
/* 212 */         fieldConstant = Constant.NotAConstant;
/*     */       }
/* 214 */       this.constant = fieldConstant;
/*     */     }
/* 216 */     return fieldConstant;
/*     */   }
/*     */ 
/*     */   public char[] genericSignature()
/*     */   {
/* 223 */     if ((this.modifiers & 0x40000000) == 0) return null;
/* 224 */     return this.type.genericTypeSignature();
/*     */   }
/*     */   public final int getAccessFlags() {
/* 227 */     return this.modifiers & 0xFFFF;
/*     */   }
/*     */ 
/*     */   public AnnotationBinding[] getAnnotations() {
/* 231 */     FieldBinding originalField = original();
/* 232 */     ReferenceBinding declaringClassBinding = originalField.declaringClass;
/* 233 */     if (declaringClassBinding == null) {
/* 234 */       return Binding.NO_ANNOTATIONS;
/*     */     }
/* 236 */     return declaringClassBinding.retrieveAnnotations(originalField);
/*     */   }
/*     */ 
/*     */   public long getAnnotationTagBits()
/*     */   {
/* 245 */     FieldBinding originalField = original();
/* 246 */     if (((originalField.tagBits & 0x0) == 0L) && ((originalField.declaringClass instanceof SourceTypeBinding))) {
/* 247 */       ClassScope scope = ((SourceTypeBinding)originalField.declaringClass).scope;
/* 248 */       if (scope == null) {
/* 249 */         this.tagBits |= 25769803776L;
/* 250 */         return 0L;
/*     */       }
/* 252 */       TypeDeclaration typeDecl = scope.referenceContext;
/* 253 */       FieldDeclaration fieldDecl = typeDecl.declarationOf(originalField);
/* 254 */       if (fieldDecl != null) {
/* 255 */         MethodScope initializationScope = isStatic() ? typeDecl.staticInitializerScope : typeDecl.initializerScope;
/* 256 */         FieldBinding previousField = initializationScope.initializedField;
/* 257 */         int previousFieldID = initializationScope.lastVisibleFieldID;
/*     */         try {
/* 259 */           initializationScope.initializedField = originalField;
/* 260 */           initializationScope.lastVisibleFieldID = originalField.id;
/* 261 */           ASTNode.resolveAnnotations(initializationScope, fieldDecl.annotations, originalField);
/*     */         } finally {
/* 263 */           initializationScope.initializedField = previousField;
/* 264 */           initializationScope.lastVisibleFieldID = previousFieldID;
/*     */         }
/*     */       }
/*     */     }
/* 268 */     return originalField.tagBits;
/*     */   }
/*     */ 
/*     */   public final boolean isDefault() {
/* 272 */     return (!isPublic()) && (!isProtected()) && (!isPrivate());
/*     */   }
/*     */ 
/*     */   public final boolean isDeprecated()
/*     */   {
/* 281 */     return (this.modifiers & 0x100000) != 0;
/*     */   }
/*     */ 
/*     */   public final boolean isPrivate()
/*     */   {
/* 287 */     return (this.modifiers & 0x2) != 0;
/*     */   }
/*     */ 
/*     */   public final boolean isOrEnclosedByPrivateType()
/*     */   {
/* 293 */     if ((this.modifiers & 0x2) != 0)
/* 294 */       return true;
/* 295 */     return (this.declaringClass != null) && (this.declaringClass.isOrEnclosedByPrivateType());
/*     */   }
/*     */ 
/*     */   public final boolean isProtected()
/*     */   {
/* 301 */     return (this.modifiers & 0x4) != 0;
/*     */   }
/*     */ 
/*     */   public final boolean isPublic()
/*     */   {
/* 307 */     return (this.modifiers & 0x1) != 0;
/*     */   }
/*     */ 
/*     */   public final boolean isStatic()
/*     */   {
/* 313 */     return (this.modifiers & 0x8) != 0;
/*     */   }
/*     */ 
/*     */   public final boolean isSynthetic()
/*     */   {
/* 319 */     return (this.modifiers & 0x1000) != 0;
/*     */   }
/*     */ 
/*     */   public final boolean isTransient()
/*     */   {
/* 325 */     return (this.modifiers & 0x80) != 0;
/*     */   }
/*     */ 
/*     */   public final boolean isUsed()
/*     */   {
/* 331 */     return (this.modifiers & 0x8000000) != 0;
/*     */   }
/*     */ 
/*     */   public final boolean isViewedAsDeprecated()
/*     */   {
/* 337 */     return (this.modifiers & 0x300000) != 0;
/*     */   }
/*     */ 
/*     */   public final boolean isVolatile()
/*     */   {
/* 343 */     return (this.modifiers & 0x40) != 0;
/*     */   }
/*     */ 
/*     */   public final int kind() {
/* 347 */     return 1;
/*     */   }
/*     */ 
/*     */   public FieldBinding original()
/*     */   {
/* 355 */     return this;
/*     */   }
/*     */   public void setAnnotations(AnnotationBinding[] annotations) {
/* 358 */     this.declaringClass.storeAnnotations(this, annotations);
/*     */   }
/*     */ 
/*     */   public FieldDeclaration sourceField() {
/*     */     try {
/* 363 */       sourceType = (SourceTypeBinding)this.declaringClass;
/*     */     }
/*     */     catch (ClassCastException localClassCastException)
/*     */     {
/*     */       SourceTypeBinding sourceType;
/* 365 */       return null;
/*     */     }
/*     */     SourceTypeBinding sourceType;
/* 368 */     FieldDeclaration[] fields = sourceType.scope.referenceContext.fields;
/* 369 */     if (fields != null) {
/* 370 */       int i = fields.length;
/*     */       do { if (this == fields[i].binding)
/* 372 */           return fields[i];
/* 370 */         i--; } while (i >= 0);
/*     */     }
/*     */ 
/* 374 */     return null;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\ecj-3.5.2.jar
 * Qualified Name:     org.eclipse.jdt.internal.compiler.lookup.FieldBinding
 * JD-Core Version:    0.6.0
 */