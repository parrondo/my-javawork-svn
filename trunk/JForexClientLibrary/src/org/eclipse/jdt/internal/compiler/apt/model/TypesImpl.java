/*     */ package org.eclipse.jdt.internal.compiler.apt.model;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collections;
/*     */ import java.util.List;
/*     */ import javax.lang.model.element.Element;
/*     */ import javax.lang.model.element.TypeElement;
/*     */ import javax.lang.model.type.ArrayType;
/*     */ import javax.lang.model.type.DeclaredType;
/*     */ import javax.lang.model.type.ExecutableType;
/*     */ import javax.lang.model.type.NoType;
/*     */ import javax.lang.model.type.NullType;
/*     */ import javax.lang.model.type.PrimitiveType;
/*     */ import javax.lang.model.type.TypeKind;
/*     */ import javax.lang.model.type.TypeMirror;
/*     */ import javax.lang.model.type.WildcardType;
/*     */ import javax.lang.model.util.Types;
/*     */ import org.eclipse.jdt.core.compiler.CharOperation;
/*     */ import org.eclipse.jdt.internal.compiler.apt.dispatch.BaseProcessingEnvImpl;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.ArrayBinding;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.BaseTypeBinding;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.Binding;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.FieldBinding;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.LookupEnvironment;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.MethodBinding;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.ReferenceBinding;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.TypeVariableBinding;
/*     */ 
/*     */ public class TypesImpl
/*     */   implements Types
/*     */ {
/*     */   private final BaseProcessingEnvImpl _env;
/*     */ 
/*     */   public TypesImpl(BaseProcessingEnvImpl env)
/*     */   {
/*  57 */     this._env = env;
/*     */   }
/*     */ 
/*     */   public Element asElement(TypeMirror t)
/*     */   {
/*  65 */     switch ($SWITCH_TABLE$javax$lang$model$type$TypeKind()[t.getKind().ordinal()]) {
/*     */     case 13:
/*     */     case 15:
/*  68 */       return this._env.getFactory().newElement(((TypeMirrorImpl)t).binding());
/*     */     case 14:
/*  70 */     }return null;
/*     */   }
/*     */ 
/*     */   public TypeMirror asMemberOf(DeclaredType containing, Element element)
/*     */   {
/*  80 */     ElementImpl elementImpl = (ElementImpl)element;
/*  81 */     DeclaredTypeImpl declaredTypeImpl = (DeclaredTypeImpl)containing;
/*  82 */     ReferenceBinding referenceBinding = (ReferenceBinding)declaredTypeImpl._binding;
/*  83 */     switch ($SWITCH_TABLE$javax$lang$model$element$ElementKind()[element.getKind().ordinal()]) {
/*     */     case 11:
/*     */     case 12:
/*  86 */       MethodBinding methodBinding = (MethodBinding)elementImpl._binding;
/*  87 */       if (methodBinding.declaringClass != referenceBinding) {
/*  88 */         throw new IllegalArgumentException("element is not valid for the containing declared type");
/*     */       }
/*  90 */       for (MethodBinding method : referenceBinding.methods()) {
/*  91 */         if ((CharOperation.equals(method.selector, methodBinding.selector)) && 
/*  92 */           (method.areParameterErasuresEqual(methodBinding))) {
/*  93 */           return this._env.getFactory().newTypeMirror(method);
/*     */         }
/*     */       }
/*  96 */       break;
/*     */     case 6:
/*     */     case 7:
/*  99 */       FieldBinding fieldBinding = (FieldBinding)elementImpl._binding;
/* 100 */       if (fieldBinding.declaringClass != referenceBinding) {
/* 101 */         throw new IllegalArgumentException("element is not valid for the containing declared type");
/*     */       }
/* 103 */       for (FieldBinding field : referenceBinding.fields()) {
/* 104 */         if (CharOperation.equals(field.name, fieldBinding.name)) {
/* 105 */           return this._env.getFactory().newTypeMirror(field);
/*     */         }
/*     */       }
/* 108 */       break;
/*     */     case 2:
/*     */     case 3:
/*     */     case 4:
/*     */     case 5:
/* 113 */       ReferenceBinding referenceBinding2 = (ReferenceBinding)elementImpl._binding;
/* 114 */       if (referenceBinding2.enclosingType() != referenceBinding) {
/* 115 */         throw new IllegalArgumentException("element is not valid for the containing declared type");
/*     */       }
/* 117 */       for (ReferenceBinding referenceBinding3 : referenceBinding.memberTypes())
/* 118 */         if (CharOperation.equals(referenceBinding3.compoundName, referenceBinding3.compoundName))
/* 119 */           return this._env.getFactory().newTypeMirror(referenceBinding3);
/*     */     case 8:
/*     */     case 9:
/*     */     case 10:
/*     */     }
/* 124 */     throw new IllegalArgumentException("element is not valid for the containing declared type: element kind " + element.getKind());
/*     */   }
/*     */ 
/*     */   public TypeElement boxedClass(PrimitiveType p)
/*     */   {
/* 132 */     PrimitiveTypeImpl primitiveTypeImpl = (PrimitiveTypeImpl)p;
/* 133 */     BaseTypeBinding baseTypeBinding = (BaseTypeBinding)primitiveTypeImpl._binding;
/* 134 */     TypeBinding boxed = this._env.getLookupEnvironment().computeBoxingType(baseTypeBinding);
/* 135 */     return (TypeElement)this._env.getFactory().newElement(boxed);
/*     */   }
/*     */ 
/*     */   public TypeMirror capture(TypeMirror t)
/*     */   {
/* 144 */     throw new UnsupportedOperationException("NYI: TypesImpl.capture(...)");
/*     */   }
/*     */ 
/*     */   public boolean contains(TypeMirror t1, TypeMirror t2)
/*     */   {
/* 152 */     switch ($SWITCH_TABLE$javax$lang$model$type$TypeKind()[t1.getKind().ordinal()]) {
/*     */     case 17:
/*     */     case 18:
/* 155 */       throw new IllegalArgumentException("Executable and package are illegal argument for Types.contains(..)");
/*     */     }
/* 157 */     switch ($SWITCH_TABLE$javax$lang$model$type$TypeKind()[
/* 157 */       t2.getKind().ordinal()]) {
/*     */     case 17:
/*     */     case 18:
/* 160 */       throw new IllegalArgumentException("Executable and package are illegal argument for Types.contains(..)");
/*     */     }
/*     */ 
/* 163 */     throw new UnsupportedOperationException("NYI: TypesImpl.contains(" + t1 + ", " + t2 + ")");
/*     */   }
/*     */ 
/*     */   public List<? extends TypeMirror> directSupertypes(TypeMirror t)
/*     */   {
/* 171 */     switch ($SWITCH_TABLE$javax$lang$model$type$TypeKind()[t.getKind().ordinal()]) {
/*     */     case 17:
/*     */     case 18:
/* 174 */       throw new IllegalArgumentException("Invalid type mirror for directSypertypes");
/*     */     }
/* 176 */     TypeMirrorImpl typeMirrorImpl = (TypeMirrorImpl)t;
/* 177 */     Binding binding = typeMirrorImpl._binding;
/* 178 */     if ((binding instanceof ReferenceBinding)) {
/* 179 */       ReferenceBinding referenceBinding = (ReferenceBinding)binding;
/* 180 */       ArrayList list = new ArrayList();
/* 181 */       ReferenceBinding superclass = referenceBinding.superclass();
/* 182 */       if (superclass != null) {
/* 183 */         list.add(this._env.getFactory().newTypeMirror(superclass));
/*     */       }
/* 185 */       for (ReferenceBinding interfaceBinding : referenceBinding.superInterfaces()) {
/* 186 */         list.add(this._env.getFactory().newTypeMirror(interfaceBinding));
/*     */       }
/* 188 */       return Collections.unmodifiableList(list);
/*     */     }
/* 190 */     return Collections.emptyList();
/*     */   }
/*     */ 
/*     */   public TypeMirror erasure(TypeMirror t)
/*     */   {
/* 198 */     TypeMirrorImpl typeMirrorImpl = (TypeMirrorImpl)t;
/* 199 */     Binding binding = typeMirrorImpl._binding;
/* 200 */     if ((binding instanceof ReferenceBinding)) {
/* 201 */       return this._env.getFactory().newTypeMirror(((ReferenceBinding)binding).erasure());
/*     */     }
/* 203 */     if ((binding instanceof ArrayBinding)) {
/* 204 */       TypeBinding typeBinding = (TypeBinding)binding;
/* 205 */       return new ArrayTypeImpl(this._env, this._env.getLookupEnvironment().createArrayType(
/* 206 */         typeBinding.leafComponentType().erasure(), 
/* 207 */         typeBinding.dimensions()));
/*     */     }
/* 209 */     return t;
/*     */   }
/*     */ 
/*     */   public ArrayType getArrayType(TypeMirror componentType)
/*     */   {
/* 217 */     TypeMirrorImpl typeMirrorImpl = (TypeMirrorImpl)componentType;
/* 218 */     TypeBinding typeBinding = (TypeBinding)typeMirrorImpl._binding;
/* 219 */     return new ArrayTypeImpl(this._env, this._env.getLookupEnvironment().createArrayType(
/* 220 */       typeBinding.leafComponentType(), 
/* 221 */       typeBinding.dimensions() + 1));
/*     */   }
/*     */ 
/*     */   public DeclaredType getDeclaredType(TypeElement typeElem, TypeMirror[] typeArgs)
/*     */   {
/* 229 */     int typeArgsLength = typeArgs.length;
/* 230 */     TypeElementImpl typeElementImpl = (TypeElementImpl)typeElem;
/* 231 */     ReferenceBinding referenceBinding = (ReferenceBinding)typeElementImpl._binding;
/* 232 */     TypeVariableBinding[] typeVariables = referenceBinding.typeVariables();
/* 233 */     int typeVariablesLength = typeVariables.length;
/* 234 */     if (typeArgsLength == 0) {
/* 235 */       if (referenceBinding.isGenericType())
/*     */       {
/* 237 */         return this._env.getFactory().newDeclaredType(this._env.getLookupEnvironment().createRawType(referenceBinding, null));
/*     */       }
/* 239 */       return (DeclaredType)typeElem.asType();
/* 240 */     }if (typeArgsLength != typeVariablesLength) {
/* 241 */       throw new IllegalArgumentException("Number of typeArguments doesn't match the number of formal parameters of typeElem");
/*     */     }
/* 243 */     TypeBinding[] typeArguments = new TypeBinding[typeArgsLength];
/* 244 */     for (int i = 0; i < typeArgsLength; i++) {
/* 245 */       TypeMirrorImpl typeMirrorImpl = (TypeMirrorImpl)typeArgs[i];
/* 246 */       Binding binding = typeMirrorImpl._binding;
/* 247 */       if (!(binding instanceof TypeBinding)) {
/* 248 */         throw new IllegalArgumentException("Invalid type argument: " + typeMirrorImpl);
/*     */       }
/* 250 */       typeArguments[i] = ((TypeBinding)binding);
/*     */     }
/* 252 */     return this._env.getFactory().newDeclaredType(
/* 253 */       this._env.getLookupEnvironment().createParameterizedType(referenceBinding, typeArguments, null));
/*     */   }
/*     */ 
/*     */   public DeclaredType getDeclaredType(DeclaredType containing, TypeElement typeElem, TypeMirror[] typeArgs)
/*     */   {
/* 262 */     int typeArgsLength = typeArgs.length;
/* 263 */     TypeElementImpl typeElementImpl = (TypeElementImpl)typeElem;
/* 264 */     ReferenceBinding referenceBinding = (ReferenceBinding)typeElementImpl._binding;
/* 265 */     TypeVariableBinding[] typeVariables = referenceBinding.typeVariables();
/* 266 */     int typeVariablesLength = typeVariables.length;
/* 267 */     DeclaredTypeImpl declaredTypeImpl = (DeclaredTypeImpl)containing;
/* 268 */     ReferenceBinding enclosingType = (ReferenceBinding)declaredTypeImpl._binding;
/* 269 */     if (typeArgsLength == 0) {
/* 270 */       if (referenceBinding.isGenericType())
/*     */       {
/* 272 */         return this._env.getFactory().newDeclaredType(this._env.getLookupEnvironment().createRawType(referenceBinding, enclosingType));
/*     */       }
/*     */ 
/* 275 */       throw new UnsupportedOperationException("NYI: TypesImpl.getDeclaredType(...) for member types");
/* 276 */     }if (typeArgsLength != typeVariablesLength) {
/* 277 */       throw new IllegalArgumentException("Number of typeArguments doesn't match the number of formal parameters of typeElem");
/*     */     }
/* 279 */     TypeBinding[] typeArguments = new TypeBinding[typeArgsLength];
/* 280 */     for (int i = 0; i < typeArgsLength; i++) {
/* 281 */       TypeMirrorImpl typeMirrorImpl = (TypeMirrorImpl)typeArgs[i];
/* 282 */       Binding binding = typeMirrorImpl._binding;
/* 283 */       if (!(binding instanceof TypeBinding)) {
/* 284 */         throw new IllegalArgumentException("Invalid type for a type arguments : " + typeMirrorImpl);
/*     */       }
/* 286 */       typeArguments[i] = ((TypeBinding)binding);
/*     */     }
/* 288 */     return this._env.getFactory().newDeclaredType(
/* 289 */       this._env.getLookupEnvironment().createParameterizedType(referenceBinding, typeArguments, enclosingType));
/*     */   }
/*     */ 
/*     */   public NoType getNoType(TypeKind kind)
/*     */   {
/* 294 */     return this._env.getFactory().getNoType(kind);
/*     */   }
/*     */ 
/*     */   public NullType getNullType()
/*     */   {
/* 302 */     return this._env.getFactory().getNullType();
/*     */   }
/*     */ 
/*     */   public PrimitiveType getPrimitiveType(TypeKind kind)
/*     */   {
/* 310 */     return this._env.getFactory().getPrimitiveType(kind);
/*     */   }
/*     */ 
/*     */   public WildcardType getWildcardType(TypeMirror extendsBound, TypeMirror superBound)
/*     */   {
/* 318 */     if ((extendsBound != null) && (superBound != null)) {
/* 319 */       throw new IllegalArgumentException("Extends and super bounds cannot be set at the same time");
/*     */     }
/* 321 */     if (extendsBound != null) {
/* 322 */       TypeMirrorImpl extendsBoundMirrorType = (TypeMirrorImpl)extendsBound;
/* 323 */       TypeBinding typeBinding = (TypeBinding)extendsBoundMirrorType._binding;
/* 324 */       return new WildcardTypeImpl(this._env, this._env.getLookupEnvironment().createWildcard(
/* 325 */         null, 
/* 326 */         0, 
/* 327 */         typeBinding, 
/* 328 */         null, 
/* 329 */         1));
/*     */     }
/* 331 */     if (superBound != null) {
/* 332 */       TypeMirrorImpl superBoundMirrorType = (TypeMirrorImpl)superBound;
/* 333 */       TypeBinding typeBinding = (TypeBinding)superBoundMirrorType._binding;
/* 334 */       return new WildcardTypeImpl(this._env, this._env.getLookupEnvironment().createWildcard(
/* 335 */         null, 
/* 336 */         0, 
/* 337 */         typeBinding, 
/* 338 */         null, 
/* 339 */         2));
/*     */     }
/* 341 */     return new WildcardTypeImpl(this._env, this._env.getLookupEnvironment().createWildcard(
/* 342 */       null, 
/* 343 */       0, 
/* 344 */       null, 
/* 345 */       null, 
/* 346 */       0));
/*     */   }
/*     */ 
/*     */   public boolean isAssignable(TypeMirror t1, TypeMirror t2)
/*     */   {
/* 354 */     if ((!(t1 instanceof TypeMirrorImpl)) || (!(t2 instanceof TypeMirrorImpl))) {
/* 355 */       return false;
/*     */     }
/* 357 */     Binding b1 = ((TypeMirrorImpl)t1).binding();
/* 358 */     Binding b2 = ((TypeMirrorImpl)t2).binding();
/* 359 */     if ((!(b1 instanceof TypeBinding)) || (!(b2 instanceof TypeBinding)))
/*     */     {
/* 361 */       throw new IllegalArgumentException();
/*     */     }
/* 363 */     if (((TypeBinding)b1).isCompatibleWith((TypeBinding)b2)) {
/* 364 */       return true;
/*     */     }
/*     */ 
/* 367 */     TypeBinding convertedType = this._env.getLookupEnvironment().computeBoxingType((TypeBinding)b1);
/* 368 */     return (convertedType != null) && (convertedType.isCompatibleWith((TypeBinding)b2));
/*     */   }
/*     */ 
/*     */   public boolean isSameType(TypeMirror t1, TypeMirror t2)
/*     */   {
/* 376 */     if ((t1.getKind() == TypeKind.WILDCARD) || (t2.getKind() == TypeKind.WILDCARD))
/*     */     {
/* 378 */       return false;
/*     */     }
/* 380 */     if (t1 == t2) {
/* 381 */       return true;
/*     */     }
/* 383 */     if ((!(t1 instanceof TypeMirrorImpl)) || (!(t2 instanceof TypeMirrorImpl))) {
/* 384 */       return false;
/*     */     }
/* 386 */     Binding b1 = ((TypeMirrorImpl)t1).binding();
/* 387 */     Binding b2 = ((TypeMirrorImpl)t2).binding();
/* 388 */     return b1 == b2;
/*     */   }
/*     */ 
/*     */   public boolean isSubsignature(ExecutableType m1, ExecutableType m2)
/*     */   {
/* 396 */     MethodBinding methodBinding1 = (MethodBinding)((ExecutableTypeImpl)m1)._binding;
/* 397 */     MethodBinding methodBinding2 = (MethodBinding)((ExecutableTypeImpl)m2)._binding;
/* 398 */     if (!CharOperation.equals(methodBinding1.selector, methodBinding2.selector))
/* 399 */       return false;
/* 400 */     return (methodBinding1.areParameterErasuresEqual(methodBinding2)) && (methodBinding1.areTypeVariableErasuresEqual(methodBinding2));
/*     */   }
/*     */ 
/*     */   public boolean isSubtype(TypeMirror t1, TypeMirror t2)
/*     */   {
/* 408 */     if ((t1 instanceof NoTypeImpl)) {
/* 409 */       if ((t2 instanceof NoTypeImpl)) {
/* 410 */         return ((NoTypeImpl)t1).getKind() == ((NoTypeImpl)t2).getKind();
/*     */       }
/* 412 */       return false;
/* 413 */     }if ((t2 instanceof NoTypeImpl)) {
/* 414 */       return false;
/*     */     }
/* 416 */     if ((!(t1 instanceof TypeMirrorImpl)) || (!(t2 instanceof TypeMirrorImpl))) {
/* 417 */       return false;
/*     */     }
/* 419 */     if (t1 == t2) {
/* 420 */       return true;
/*     */     }
/* 422 */     Binding b1 = ((TypeMirrorImpl)t1).binding();
/* 423 */     Binding b2 = ((TypeMirrorImpl)t2).binding();
/* 424 */     if (b1 == b2) {
/* 425 */       return true;
/*     */     }
/* 427 */     if ((!(b1 instanceof TypeBinding)) || (!(b2 instanceof TypeBinding)))
/*     */     {
/* 429 */       return false;
/*     */     }
/* 431 */     if ((b1.kind() == 132) || (b2.kind() == 132)) {
/* 432 */       if (b1.kind() != b2.kind()) {
/* 433 */         return false;
/*     */       }
/*     */ 
/* 437 */       return ((TypeBinding)b1).isCompatibleWith((TypeBinding)b2);
/*     */     }
/*     */ 
/* 440 */     return ((TypeBinding)b1).isCompatibleWith((TypeBinding)b2);
/*     */   }
/*     */ 
/*     */   public PrimitiveType unboxedType(TypeMirror t)
/*     */   {
/* 445 */     if (!(((TypeMirrorImpl)t)._binding instanceof ReferenceBinding))
/*     */     {
/* 447 */       throw new IllegalArgumentException("Given type mirror cannot be unboxed");
/*     */     }
/* 449 */     ReferenceBinding boxed = (ReferenceBinding)((TypeMirrorImpl)t)._binding;
/* 450 */     TypeBinding unboxed = this._env.getLookupEnvironment().computeBoxingType(boxed);
/* 451 */     if (unboxed.kind() != 132)
/*     */     {
/* 453 */       throw new IllegalArgumentException();
/*     */     }
/* 455 */     return this._env.getFactory().getPrimitiveType((BaseTypeBinding)unboxed);
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\ecj-3.5.2.jar
 * Qualified Name:     org.eclipse.jdt.internal.compiler.apt.model.TypesImpl
 * JD-Core Version:    0.6.0
 */