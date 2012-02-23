/*     */ package org.eclipse.jdt.internal.compiler.apt.model;
/*     */ 
/*     */ import java.lang.reflect.Array;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collections;
/*     */ import java.util.EnumSet;
/*     */ import java.util.List;
/*     */ import java.util.Set;
/*     */ import javax.lang.model.element.AnnotationMirror;
/*     */ import javax.lang.model.element.Element;
/*     */ import javax.lang.model.element.ElementKind;
/*     */ import javax.lang.model.element.Modifier;
/*     */ import javax.lang.model.element.PackageElement;
/*     */ import javax.lang.model.element.TypeParameterElement;
/*     */ import javax.lang.model.type.DeclaredType;
/*     */ import javax.lang.model.type.ErrorType;
/*     */ import javax.lang.model.type.NoType;
/*     */ import javax.lang.model.type.NullType;
/*     */ import javax.lang.model.type.TypeKind;
/*     */ import javax.lang.model.type.TypeMirror;
/*     */ import org.eclipse.jdt.internal.compiler.apt.dispatch.BaseProcessingEnvImpl;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.AnnotationBinding;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.ArrayBinding;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.BaseTypeBinding;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.Binding;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.MethodBinding;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.PackageBinding;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.ParameterizedTypeBinding;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.ReferenceBinding;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.TypeConstants;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.TypeVariableBinding;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.VariableBinding;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.WildcardBinding;
/*     */ 
/*     */ public class Factory
/*     */ {
/*     */   public static final Byte DUMMY_BYTE;
/*     */   public static final Character DUMMY_CHAR;
/*     */   public static final Double DUMMY_DOUBLE;
/*     */   public static final Float DUMMY_FLOAT;
/*     */   public static final Integer DUMMY_INTEGER;
/*     */   public static final Long DUMMY_LONG;
/*     */   public static final Short DUMMY_SHORT;
/*     */   private final BaseProcessingEnvImpl _env;
/*     */ 
/*     */   static
/*     */   {
/*  58 */     DUMMY_BYTE = Byte.valueOf(0);
/*  59 */     DUMMY_CHAR = Character.valueOf('0');
/*  60 */     DUMMY_DOUBLE = Double.valueOf(0.0D);
/*  61 */     DUMMY_FLOAT = Float.valueOf(0.0F);
/*  62 */     DUMMY_INTEGER = Integer.valueOf(0);
/*  63 */     DUMMY_LONG = Long.valueOf(0L);
/*  64 */     DUMMY_SHORT = Short.valueOf(0);
/*     */   }
/*     */ 
/*     */   public Factory(BaseProcessingEnvImpl env)
/*     */   {
/*  72 */     this._env = env;
/*     */   }
/*     */ 
/*     */   public List<? extends AnnotationMirror> getAnnotationMirrors(AnnotationBinding[] annotations)
/*     */   {
/*  80 */     if ((annotations == null) || (annotations.length == 0)) {
/*  81 */       return Collections.emptyList();
/*     */     }
/*  83 */     List list = new ArrayList(annotations.length);
/*  84 */     for (AnnotationBinding annotation : annotations) {
/*  85 */       if (annotation != null)
/*  86 */         list.add(newAnnotationMirror(annotation));
/*     */     }
/*  88 */     return Collections.unmodifiableList(list);
/*     */   }
/*     */ 
/*     */   private static void appendModifier(Set<Modifier> result, int modifiers, int modifierConstant, Modifier modifier) {
/*  92 */     if ((modifiers & modifierConstant) != 0)
/*  93 */       result.add(modifier);
/*     */   }
/*     */ 
/*     */   private static void decodeModifiers(Set<Modifier> result, int modifiers, int[] checkBits)
/*     */   {
/*  98 */     if (checkBits == null) return;
/*  99 */     int i = 0; for (int max = checkBits.length; i < max; i++)
/* 100 */       switch (checkBits[i]) {
/*     */       case 1:
/* 102 */         appendModifier(result, modifiers, checkBits[i], Modifier.PUBLIC);
/* 103 */         break;
/*     */       case 4:
/* 105 */         appendModifier(result, modifiers, checkBits[i], Modifier.PROTECTED);
/* 106 */         break;
/*     */       case 2:
/* 108 */         appendModifier(result, modifiers, checkBits[i], Modifier.PRIVATE);
/* 109 */         break;
/*     */       case 1024:
/* 111 */         appendModifier(result, modifiers, checkBits[i], Modifier.ABSTRACT);
/* 112 */         break;
/*     */       case 8:
/* 114 */         appendModifier(result, modifiers, checkBits[i], Modifier.STATIC);
/* 115 */         break;
/*     */       case 16:
/* 117 */         appendModifier(result, modifiers, checkBits[i], Modifier.FINAL);
/* 118 */         break;
/*     */       case 32:
/* 120 */         appendModifier(result, modifiers, checkBits[i], Modifier.SYNCHRONIZED);
/* 121 */         break;
/*     */       case 256:
/* 123 */         appendModifier(result, modifiers, checkBits[i], Modifier.NATIVE);
/* 124 */         break;
/*     */       case 2048:
/* 126 */         appendModifier(result, modifiers, checkBits[i], Modifier.STRICTFP);
/* 127 */         break;
/*     */       case 128:
/* 129 */         appendModifier(result, modifiers, checkBits[i], Modifier.TRANSIENT);
/* 130 */         break;
/*     */       case 64:
/* 132 */         appendModifier(result, modifiers, checkBits[i], Modifier.VOLATILE);
/*     */       }
/*     */   }
/*     */ 
/*     */   public static Object getMatchingDummyValue(Class<?> expectedType)
/*     */   {
/* 139 */     if (expectedType.isPrimitive()) {
/* 140 */       if (expectedType == Boolean.TYPE)
/* 141 */         return Boolean.FALSE;
/* 142 */       if (expectedType == Byte.TYPE)
/* 143 */         return DUMMY_BYTE;
/* 144 */       if (expectedType == Character.TYPE)
/* 145 */         return DUMMY_CHAR;
/* 146 */       if (expectedType == Double.TYPE)
/* 147 */         return DUMMY_DOUBLE;
/* 148 */       if (expectedType == Float.TYPE)
/* 149 */         return DUMMY_FLOAT;
/* 150 */       if (expectedType == Integer.TYPE)
/* 151 */         return DUMMY_INTEGER;
/* 152 */       if (expectedType == Long.TYPE)
/* 153 */         return DUMMY_LONG;
/* 154 */       if (expectedType == Short.TYPE) {
/* 155 */         return DUMMY_SHORT;
/*     */       }
/* 157 */       return DUMMY_INTEGER;
/*     */     }
/*     */ 
/* 160 */     return null;
/*     */   }
/*     */ 
/*     */   public static Set<Modifier> getModifiers(int modifiers, ElementKind kind) {
/* 164 */     return getModifiers(modifiers, kind, false);
/*     */   }
/*     */ 
/*     */   public static Set<Modifier> getModifiers(int modifiers, ElementKind kind, boolean isFromBinary)
/*     */   {
/* 171 */     EnumSet result = EnumSet.noneOf(Modifier.class);
/* 172 */     switch (kind)
/*     */     {
/*     */     case METHOD:
/*     */     case OTHER:
/* 176 */       decodeModifiers(result, modifiers, new int[] { 
/* 177 */         1, 
/* 178 */         4, 
/* 179 */         2, 
/* 180 */         1024, 
/* 181 */         8, 
/* 182 */         16, 
/* 183 */         32, 
/* 184 */         256, 
/* 185 */         2048 });
/*     */ 
/* 187 */       break;
/*     */     case EXCEPTION_PARAMETER:
/*     */     case FIELD:
/* 191 */       decodeModifiers(result, modifiers, new int[] { 
/* 192 */         1, 
/* 193 */         4, 
/* 194 */         2, 
/* 195 */         8, 
/* 196 */         16, 
/* 197 */         128, 
/* 198 */         64 });
/*     */ 
/* 200 */       break;
/*     */     case CLASS:
/* 202 */       if (isFromBinary) {
/* 203 */         decodeModifiers(result, modifiers, new int[] { 
/* 204 */           1, 
/* 205 */           4, 
/* 206 */           16, 
/* 207 */           2, 
/* 208 */           1024, 
/* 209 */           8, 
/* 210 */           2048 });
/*     */       }
/*     */       else
/*     */       {
/* 214 */         decodeModifiers(result, modifiers, new int[] { 
/* 215 */           1, 
/* 216 */           4, 
/* 217 */           16, 
/* 218 */           2, 
/* 219 */           8, 
/* 220 */           2048 });
/*     */       }
/*     */ 
/* 223 */       break;
/*     */     case CONSTRUCTOR:
/*     */     case ENUM:
/*     */     case ENUM_CONSTANT:
/* 228 */       decodeModifiers(result, modifiers, new int[] { 
/* 229 */         1, 
/* 230 */         4, 
/* 231 */         1024, 
/* 232 */         16, 
/* 233 */         2, 
/* 234 */         8, 
/* 235 */         2048 });
/*     */     case INSTANCE_INIT:
/*     */     case INTERFACE:
/* 238 */     case LOCAL_VARIABLE: } return Collections.unmodifiableSet(result);
/*     */   }
/*     */ 
/*     */   public AnnotationMirror newAnnotationMirror(AnnotationBinding binding)
/*     */   {
/* 243 */     return new AnnotationMirrorImpl(this._env, binding);
/*     */   }
/*     */ 
/*     */   public Element newElement(Binding binding, ElementKind kindHint)
/*     */   {
/* 250 */     if (binding == null) {
/* 251 */       return new ErrorTypeElement(this._env);
/*     */     }
/* 253 */     switch (binding.kind()) {
/*     */     case 1:
/*     */     case 2:
/*     */     case 3:
/* 257 */       return new VariableElementImpl(this._env, (VariableBinding)binding);
/*     */     case 4:
/*     */     case 2052:
/* 260 */       ReferenceBinding referenceBinding = (ReferenceBinding)binding;
/* 261 */       if (referenceBinding.sourceName == TypeConstants.PACKAGE_INFO_NAME) {
/* 262 */         return new PackageElementImpl(this._env, referenceBinding.fPackage);
/*     */       }
/* 264 */       return new TypeElementImpl(this._env, referenceBinding, kindHint);
/*     */     case 8:
/* 266 */       return new ExecutableElementImpl(this._env, (MethodBinding)binding);
/*     */     case 260:
/*     */     case 1028:
/* 269 */       return new TypeElementImpl(this._env, ((ParameterizedTypeBinding)binding).genericType(), kindHint);
/*     */     case 16:
/* 271 */       return new PackageElementImpl(this._env, (PackageBinding)binding);
/*     */     case 4100:
/* 273 */       return new TypeParameterElementImpl(this._env, (TypeVariableBinding)binding);
/*     */     case 32:
/*     */     case 68:
/*     */     case 132:
/*     */     case 516:
/*     */     case 8196:
/* 280 */       throw new UnsupportedOperationException("NYI: binding type " + binding.kind());
/*     */     }
/* 282 */     return null;
/*     */   }
/*     */ 
/*     */   public Element newElement(Binding binding) {
/* 286 */     return newElement(binding, null);
/*     */   }
/*     */ 
/*     */   public DeclaredType newDeclaredType(ReferenceBinding binding) {
/* 290 */     switch (binding.kind())
/*     */     {
/*     */     case 516:
/* 293 */       throw new IllegalArgumentException("A wildcard binding can't be turned into a DeclaredType");
/*     */     case 8196:
/* 296 */       throw new IllegalArgumentException("An intersection binding can't be turned into a DeclaredType");
/*     */     }
/* 298 */     return new DeclaredTypeImpl(this._env, binding);
/*     */   }
/*     */ 
/*     */   public DeclaredType newAnnotationType(ReferenceBinding binding)
/*     */   {
/* 308 */     switch (binding.kind())
/*     */     {
/*     */     case 516:
/* 311 */       throw new IllegalArgumentException("A wildcard binding can't be turned into a DeclaredType");
/*     */     case 8196:
/* 314 */       throw new IllegalArgumentException("An intersection binding can't be turned into a DeclaredType");
/*     */     }
/* 316 */     return new DeclaredTypeImpl(this._env, binding, ElementKind.ANNOTATION_TYPE);
/*     */   }
/*     */ 
/*     */   public PackageElement newPackageElement(PackageBinding binding)
/*     */   {
/* 324 */     return new PackageElementImpl(this._env, binding);
/*     */   }
/*     */ 
/*     */   public NullType getNullType() {
/* 328 */     return NoTypeImpl.NULL_TYPE;
/*     */   }
/*     */ 
/*     */   public NoType getNoType(TypeKind kind)
/*     */   {
/* 333 */     switch ($SWITCH_TABLE$javax$lang$model$type$TypeKind()[kind.ordinal()]) {
/*     */     case 10:
/* 335 */       return NoTypeImpl.NO_TYPE_NONE;
/*     */     case 9:
/* 337 */       return NoTypeImpl.NO_TYPE_VOID;
/*     */     case 17:
/* 339 */       return NoTypeImpl.NO_TYPE_PACKAGE;
/*     */     }
/* 341 */     throw new IllegalArgumentException();
/*     */   }
/*     */ 
/*     */   public PrimitiveTypeImpl getPrimitiveType(TypeKind kind)
/*     */   {
/* 351 */     switch (kind) {
/*     */     case ARRAY:
/* 353 */       return PrimitiveTypeImpl.BOOLEAN;
/*     */     case BOOLEAN:
/* 355 */       return PrimitiveTypeImpl.BYTE;
/*     */     case DOUBLE:
/* 357 */       return PrimitiveTypeImpl.CHAR;
/*     */     case EXECUTABLE:
/* 359 */       return PrimitiveTypeImpl.DOUBLE;
/*     */     case ERROR:
/* 361 */       return PrimitiveTypeImpl.FLOAT;
/*     */     case CHAR:
/* 363 */       return PrimitiveTypeImpl.INT;
/*     */     case DECLARED:
/* 365 */       return PrimitiveTypeImpl.LONG;
/*     */     case BYTE:
/* 367 */       return PrimitiveTypeImpl.SHORT;
/*     */     }
/* 369 */     throw new IllegalStateException();
/*     */   }
/*     */ 
/*     */   public PrimitiveTypeImpl getPrimitiveType(BaseTypeBinding binding)
/*     */   {
/* 377 */     return getPrimitiveType(PrimitiveTypeImpl.getKind(binding));
/*     */   }
/*     */ 
/*     */   public TypeMirror newTypeMirror(Binding binding)
/*     */   {
/* 384 */     switch (binding.kind())
/*     */     {
/*     */     case 1:
/*     */     case 2:
/*     */     case 3:
/* 389 */       return newTypeMirror(((VariableBinding)binding).type);
/*     */     case 16:
/* 392 */       return getNoType(TypeKind.PACKAGE);
/*     */     case 32:
/* 395 */       throw new UnsupportedOperationException("NYI: import type " + binding.kind());
/*     */     case 8:
/* 398 */       return new ExecutableTypeImpl(this._env, (MethodBinding)binding);
/*     */     case 4:
/*     */     case 260:
/*     */     case 1028:
/*     */     case 2052:
/* 404 */       return new DeclaredTypeImpl(this._env, (ReferenceBinding)binding);
/*     */     case 68:
/* 407 */       return new ArrayTypeImpl(this._env, (ArrayBinding)binding);
/*     */     case 132:
/* 410 */       BaseTypeBinding btb = (BaseTypeBinding)binding;
/* 411 */       switch (btb.id) {
/*     */       case 6:
/* 413 */         return getNoType(TypeKind.VOID);
/*     */       case 12:
/* 415 */         return getNullType();
/*     */       }
/* 417 */       return getPrimitiveType(PrimitiveTypeImpl.getKind((BaseTypeBinding)binding));
/*     */     case 516:
/*     */     case 8196:
/* 422 */       return new WildcardTypeImpl(this._env, (WildcardBinding)binding);
/*     */     case 4100:
/* 425 */       return new TypeVariableImpl(this._env, (TypeVariableBinding)binding);
/*     */     }
/* 427 */     return null;
/*     */   }
/*     */ 
/*     */   public TypeParameterElement newTypeParameterElement(TypeVariableBinding variable, Element declaringElement)
/*     */   {
/* 435 */     return new TypeParameterElementImpl(this._env, variable, declaringElement);
/*     */   }
/*     */ 
/*     */   public ErrorType getErrorType() {
/* 439 */     return new ErrorTypeImpl(this._env);
/*     */   }
/*     */ 
/*     */   public static Object performNecessaryPrimitiveTypeConversion(Class<?> expectedType, Object value, boolean avoidReflectException)
/*     */   {
/* 488 */     assert (expectedType.isPrimitive()) : ("expectedType is not a primitive type: " + expectedType.getName());
/* 489 */     if (value == null) {
/* 490 */       return avoidReflectException ? getMatchingDummyValue(expectedType) : null;
/*     */     }
/* 492 */     String typeName = expectedType.getName();
/* 493 */     char expectedTypeChar = typeName.charAt(0);
/* 494 */     int nameLen = typeName.length();
/*     */ 
/* 497 */     if ((value instanceof Byte))
/*     */     {
/* 499 */       byte b = ((Byte)value).byteValue();
/* 500 */       switch (expectedTypeChar)
/*     */       {
/*     */       case 'b':
/* 503 */         if (nameLen == 4) {
/* 504 */           return value;
/*     */         }
/* 506 */         return avoidReflectException ? Boolean.FALSE : value;
/*     */       case 'c':
/* 508 */         return new Character((char)b);
/*     */       case 'd':
/* 510 */         return new Double(b);
/*     */       case 'f':
/* 512 */         return new Float(b);
/*     */       case 'i':
/* 514 */         return new Integer(b);
/*     */       case 'l':
/* 516 */         return new Long(b);
/*     */       case 's':
/* 518 */         return new Short(b);
/*     */       }
/* 520 */       throw new IllegalStateException("unknown type " + expectedTypeChar);
/*     */     }
/*     */ 
/* 525 */     if ((value instanceof Short))
/*     */     {
/* 527 */       short s = ((Short)value).shortValue();
/* 528 */       switch (expectedTypeChar)
/*     */       {
/*     */       case 'b':
/* 531 */         if (nameLen == 4) {
/* 532 */           return new Byte((byte)s);
/*     */         }
/* 534 */         return avoidReflectException ? Boolean.FALSE : value;
/*     */       case 'c':
/* 536 */         return new Character((char)s);
/*     */       case 'd':
/* 538 */         return new Double(s);
/*     */       case 'f':
/* 540 */         return new Float(s);
/*     */       case 'i':
/* 542 */         return new Integer(s);
/*     */       case 'l':
/* 544 */         return new Long(s);
/*     */       case 's':
/* 546 */         return value;
/*     */       }
/* 548 */       throw new IllegalStateException("unknown type " + expectedTypeChar);
/*     */     }
/*     */ 
/* 553 */     if ((value instanceof Character))
/*     */     {
/* 555 */       char c = ((Character)value).charValue();
/* 556 */       switch (expectedTypeChar)
/*     */       {
/*     */       case 'b':
/* 559 */         if (nameLen == 4) {
/* 560 */           return new Byte((byte)c);
/*     */         }
/* 562 */         return avoidReflectException ? Boolean.FALSE : value;
/*     */       case 'c':
/* 564 */         return value;
/*     */       case 'd':
/* 566 */         return new Double(c);
/*     */       case 'f':
/* 568 */         return new Float(c);
/*     */       case 'i':
/* 570 */         return new Integer(c);
/*     */       case 'l':
/* 572 */         return new Long(c);
/*     */       case 's':
/* 574 */         return new Short((short)c);
/*     */       }
/* 576 */       throw new IllegalStateException("unknown type " + expectedTypeChar);
/*     */     }
/*     */ 
/* 582 */     if ((value instanceof Integer))
/*     */     {
/* 584 */       int i = ((Integer)value).intValue();
/* 585 */       switch (expectedTypeChar)
/*     */       {
/*     */       case 'b':
/* 588 */         if (nameLen == 4) {
/* 589 */           return new Byte((byte)i);
/*     */         }
/* 591 */         return avoidReflectException ? Boolean.FALSE : value;
/*     */       case 'c':
/* 593 */         return new Character((char)i);
/*     */       case 'd':
/* 595 */         return new Double(i);
/*     */       case 'f':
/* 597 */         return new Float(i);
/*     */       case 'i':
/* 599 */         return value;
/*     */       case 'l':
/* 601 */         return new Long(i);
/*     */       case 's':
/* 603 */         return new Short((short)i);
/*     */       }
/* 605 */       throw new IllegalStateException("unknown type " + expectedTypeChar);
/*     */     }
/*     */ 
/* 609 */     if ((value instanceof Long))
/*     */     {
/* 611 */       long l = ((Long)value).longValue();
/* 612 */       switch (expectedTypeChar)
/*     */       {
/*     */       case 'b':
/*     */       case 'c':
/*     */       case 'i':
/*     */       case 's':
/* 619 */         return avoidReflectException ? getMatchingDummyValue(expectedType) : value;
/*     */       case 'd':
/* 621 */         return new Double(l);
/*     */       case 'f':
/* 623 */         return new Float((float)l);
/*     */       case 'l':
/* 625 */         return value;
/*     */       }
/*     */ 
/* 628 */       throw new IllegalStateException("unknown type " + expectedTypeChar);
/*     */     }
/*     */ 
/* 633 */     if ((value instanceof Float))
/*     */     {
/* 635 */       float f = ((Float)value).floatValue();
/* 636 */       switch (expectedTypeChar)
/*     */       {
/*     */       case 'b':
/*     */       case 'c':
/*     */       case 'i':
/*     */       case 'l':
/*     */       case 's':
/* 644 */         return avoidReflectException ? getMatchingDummyValue(expectedType) : value;
/*     */       case 'd':
/* 646 */         return new Double(f);
/*     */       case 'f':
/* 648 */         return value;
/*     */       }
/* 650 */       throw new IllegalStateException("unknown type " + expectedTypeChar);
/*     */     }
/*     */ 
/* 653 */     if ((value instanceof Double)) {
/* 654 */       if (expectedTypeChar == 'd') {
/* 655 */         return value;
/*     */       }
/* 657 */       return avoidReflectException ? getMatchingDummyValue(expectedType) : value;
/*     */     }
/*     */ 
/* 660 */     if ((value instanceof Boolean)) {
/* 661 */       if ((expectedTypeChar == 'b') && (nameLen == 7)) {
/* 662 */         return value;
/*     */       }
/* 664 */       return avoidReflectException ? getMatchingDummyValue(expectedType) : value;
/*     */     }
/*     */ 
/* 667 */     return avoidReflectException ? getMatchingDummyValue(expectedType) : value;
/*     */   }
/*     */ 
/*     */   public static void setArrayMatchingDummyValue(Object array, int i, Class<?> expectedLeafType)
/*     */   {
/* 678 */     if (Boolean.TYPE.equals(expectedLeafType)) {
/* 679 */       Array.setBoolean(array, i, false);
/*     */     }
/* 681 */     else if (Byte.TYPE.equals(expectedLeafType)) {
/* 682 */       Array.setByte(array, i, DUMMY_BYTE.byteValue());
/*     */     }
/* 684 */     else if (Character.TYPE.equals(expectedLeafType)) {
/* 685 */       Array.setChar(array, i, DUMMY_CHAR.charValue());
/*     */     }
/* 687 */     else if (Double.TYPE.equals(expectedLeafType)) {
/* 688 */       Array.setDouble(array, i, DUMMY_DOUBLE.doubleValue());
/*     */     }
/* 690 */     else if (Float.TYPE.equals(expectedLeafType)) {
/* 691 */       Array.setFloat(array, i, DUMMY_FLOAT.floatValue());
/*     */     }
/* 693 */     else if (Integer.TYPE.equals(expectedLeafType)) {
/* 694 */       Array.setInt(array, i, DUMMY_INTEGER.intValue());
/*     */     }
/* 696 */     else if (Long.TYPE.equals(expectedLeafType)) {
/* 697 */       Array.setLong(array, i, DUMMY_LONG.longValue());
/*     */     }
/* 699 */     else if (Short.TYPE.equals(expectedLeafType)) {
/* 700 */       Array.setShort(array, i, DUMMY_SHORT.shortValue());
/*     */     }
/*     */     else
/* 703 */       Array.set(array, i, null);
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\ecj-3.5.2.jar
 * Qualified Name:     org.eclipse.jdt.internal.compiler.apt.model.Factory
 * JD-Core Version:    0.6.0
 */