/*     */ package org.eclipse.jdt.internal.compiler.apt.model;
/*     */ 
/*     */ import java.lang.reflect.Array;
/*     */ import java.lang.reflect.Field;
/*     */ import java.lang.reflect.InvocationHandler;
/*     */ import java.lang.reflect.Method;
/*     */ import java.lang.reflect.Proxy;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collections;
/*     */ import java.util.LinkedHashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import javax.lang.model.element.AnnotationMirror;
/*     */ import javax.lang.model.element.AnnotationValue;
/*     */ import javax.lang.model.element.ExecutableElement;
/*     */ import javax.lang.model.type.DeclaredType;
/*     */ import javax.lang.model.type.MirroredTypeException;
/*     */ import javax.lang.model.type.MirroredTypesException;
/*     */ import javax.lang.model.type.TypeMirror;
/*     */ import org.eclipse.jdt.internal.compiler.apt.dispatch.BaseProcessingEnvImpl;
/*     */ import org.eclipse.jdt.internal.compiler.impl.Constant;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.AnnotationBinding;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.ArrayBinding;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.ElementValuePair;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.FieldBinding;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.MethodBinding;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.ReferenceBinding;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;
/*     */ 
/*     */ public class AnnotationMirrorImpl
/*     */   implements AnnotationMirror, InvocationHandler
/*     */ {
/*     */   public final BaseProcessingEnvImpl _env;
/*     */   public final AnnotationBinding _binding;
/*     */ 
/*     */   AnnotationMirrorImpl(BaseProcessingEnvImpl env, AnnotationBinding binding)
/*     */   {
/*  49 */     this._env = env;
/*  50 */     this._binding = binding;
/*     */   }
/*     */ 
/*     */   public boolean equals(Object obj)
/*     */   {
/*  55 */     if ((obj instanceof AnnotationMirrorImpl)) {
/*  56 */       if (this._binding == null) {
/*  57 */         return ((AnnotationMirrorImpl)obj)._binding == null;
/*     */       }
/*  59 */       return equals(this._binding, ((AnnotationMirrorImpl)obj)._binding);
/*     */     }
/*  61 */     return false;
/*     */   }
/*     */ 
/*     */   private static boolean equals(AnnotationBinding annotationBinding, AnnotationBinding annotationBinding2) {
/*  65 */     if (annotationBinding.getAnnotationType() != annotationBinding2.getAnnotationType()) return false;
/*  66 */     ElementValuePair[] elementValuePairs = annotationBinding.getElementValuePairs();
/*  67 */     ElementValuePair[] elementValuePairs2 = annotationBinding2.getElementValuePairs();
/*  68 */     int length = elementValuePairs.length;
/*  69 */     if (length != elementValuePairs2.length) return false;
/*  70 */     for (int i = 0; i < length; i++) {
/*  71 */       ElementValuePair pair = elementValuePairs[i];
/*     */ 
/*  73 */       int j = 0;
/*     */       while (true) { ElementValuePair pair2 = elementValuePairs2[j];
/*  75 */         if (pair.binding == pair2.binding) {
/*  76 */           if (pair.value == null) {
/*  77 */             if (pair2.value == null) {
/*     */               break;
/*     */             }
/*  80 */             return false;
/*     */           } else {
/*  82 */             if ((pair2.value != null) && 
/*  83 */               (pair2.value.equals(pair.value))) break;
/*  84 */             return false;
/*     */           }
/*     */         }
/*     */         else
/*     */         {
/*  73 */           j++; if (j >= length)
/*     */           {
/*  90 */             return false;
/*     */           }
/*     */         } }
/*     */     }
/*  92 */     return true;
/*     */   }
/*     */ 
/*     */   public DeclaredType getAnnotationType() {
/*  96 */     if (this._binding == null) {
/*  97 */       return this._env.getFactory().getErrorType();
/*     */     }
/*  99 */     ReferenceBinding annoType = this._binding.getAnnotationType();
/* 100 */     return this._env.getFactory().newAnnotationType(annoType);
/*     */   }
/*     */ 
/*     */   public Map<? extends ExecutableElement, ? extends AnnotationValue> getElementValues()
/*     */   {
/* 108 */     if (this._binding == null) {
/* 109 */       return Collections.emptyMap();
/*     */     }
/* 111 */     ElementValuePair[] pairs = this._binding.getElementValuePairs();
/* 112 */     Map valueMap = 
/* 113 */       new LinkedHashMap(pairs.length);
/* 114 */     for (ElementValuePair pair : pairs) {
/* 115 */       MethodBinding method = pair.getMethodBinding();
/* 116 */       if (method == null)
/*     */       {
/*     */         continue;
/*     */       }
/* 120 */       ExecutableElement e = new ExecutableElementImpl(this._env, method);
/* 121 */       AnnotationValue v = new AnnotationMemberValue(this._env, pair.getValue(), method);
/* 122 */       valueMap.put(e, v);
/*     */     }
/* 124 */     return Collections.unmodifiableMap(valueMap);
/*     */   }
/*     */ 
/*     */   public Map<? extends ExecutableElement, ? extends AnnotationValue> getElementValuesWithDefaults()
/*     */   {
/* 133 */     if (this._binding == null) {
/* 134 */       return Collections.emptyMap();
/*     */     }
/* 136 */     ElementValuePair[] pairs = this._binding.getElementValuePairs();
/* 137 */     ReferenceBinding annoType = this._binding.getAnnotationType();
/* 138 */     Map valueMap = 
/* 139 */       new LinkedHashMap();
/* 140 */     for (MethodBinding method : annoType.methods())
/*     */     {
/* 142 */       boolean foundExplicitValue = false;
/* 143 */       for (int i = 0; i < pairs.length; i++) {
/* 144 */         MethodBinding explicitBinding = pairs[i].getMethodBinding();
/* 145 */         if (method == explicitBinding) {
/* 146 */           ExecutableElement e = new ExecutableElementImpl(this._env, explicitBinding);
/* 147 */           AnnotationValue v = new AnnotationMemberValue(this._env, pairs[i].getValue(), explicitBinding);
/* 148 */           valueMap.put(e, v);
/* 149 */           foundExplicitValue = true;
/* 150 */           break;
/*     */         }
/*     */       }
/*     */ 
/* 154 */       if (!foundExplicitValue) {
/* 155 */         Object defaultVal = method.getDefaultValue();
/* 156 */         if (defaultVal != null) {
/* 157 */           ExecutableElement e = new ExecutableElementImpl(this._env, method);
/* 158 */           AnnotationValue v = new AnnotationMemberValue(this._env, defaultVal, method);
/* 159 */           valueMap.put(e, v);
/*     */         }
/*     */       }
/*     */     }
/* 163 */     return Collections.unmodifiableMap(valueMap);
/*     */   }
/*     */ 
/*     */   public int hashCode() {
/* 167 */     if (this._binding == null) return this._env.hashCode();
/* 168 */     return this._binding.hashCode();
/*     */   }
/*     */ 
/*     */   public Object invoke(Object proxy, Method method, Object[] args)
/*     */     throws Throwable
/*     */   {
/* 186 */     if (this._binding == null) return null;
/* 187 */     String methodName = method.getName();
/* 188 */     if ((args == null) || (args.length == 0)) {
/* 189 */       if (methodName.equals("hashCode")) {
/* 190 */         return new Integer(hashCode());
/*     */       }
/* 192 */       if (methodName.equals("toString")) {
/* 193 */         return toString();
/*     */       }
/* 195 */       if (methodName.equals("annotationType")) {
/* 196 */         return proxy.getClass().getInterfaces()[0];
/*     */       }
/*     */     }
/* 199 */     else if ((args.length == 1) && (methodName.equals("equals"))) {
/* 200 */       return new Boolean(equals(args[0]));
/*     */     }
/*     */ 
/* 204 */     if ((args != null) && (args.length != 0)) {
/* 205 */       throw new NoSuchMethodException("method " + method.getName() + formatArgs(args) + " does not exist on annotation " + toString());
/*     */     }
/* 207 */     MethodBinding methodBinding = getMethodBinding(methodName);
/* 208 */     if (methodBinding == null) {
/* 209 */       throw new NoSuchMethodException("method " + method.getName() + "() does not exist on annotation" + toString());
/*     */     }
/*     */ 
/* 212 */     Object actualValue = null;
/* 213 */     boolean foundMethod = false;
/* 214 */     ElementValuePair[] pairs = this._binding.getElementValuePairs();
/* 215 */     for (ElementValuePair pair : pairs) {
/* 216 */       if (methodName.equals(new String(pair.getName()))) {
/* 217 */         actualValue = pair.getValue();
/* 218 */         foundMethod = true;
/* 219 */         break;
/*     */       }
/*     */     }
/* 222 */     if (!foundMethod)
/*     */     {
/* 224 */       actualValue = methodBinding.getDefaultValue();
/*     */     }
/* 226 */     Class expectedType = method.getReturnType();
/* 227 */     TypeBinding actualType = methodBinding.returnType;
/* 228 */     return getReflectionValue(actualValue, actualType, expectedType);
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/* 238 */     if (this._binding == null) {
/* 239 */       return "@any()";
/*     */     }
/* 241 */     return "@" + this._binding.getAnnotationType().debugName();
/*     */   }
/*     */ 
/*     */   private String formatArgs(Object[] args)
/*     */   {
/* 251 */     StringBuilder builder = new StringBuilder(args.length * 8 + 2);
/* 252 */     builder.append('(');
/* 253 */     for (int i = 0; i < args.length; i++)
/*     */     {
/* 255 */       if (i > 0)
/* 256 */         builder.append(", ");
/* 257 */       builder.append(args[i].getClass().getName());
/*     */     }
/* 259 */     builder.append(')');
/* 260 */     return builder.toString();
/*     */   }
/*     */ 
/*     */   private MethodBinding getMethodBinding(String name)
/*     */   {
/* 268 */     ReferenceBinding annoType = this._binding.getAnnotationType();
/* 269 */     MethodBinding[] methods = annoType.getMethods(name.toCharArray());
/* 270 */     for (MethodBinding method : methods)
/*     */     {
/* 272 */       if (method.parameters.length == 0) {
/* 273 */         return method;
/*     */       }
/*     */     }
/* 276 */     return null;
/*     */   }
/*     */ 
/*     */   private Object getReflectionValue(Object actualValue, TypeBinding actualType, Class<?> expectedType)
/*     */   {
/* 295 */     if (expectedType == null)
/*     */     {
/* 297 */       return null;
/*     */     }
/* 299 */     if (actualValue == null)
/*     */     {
/* 301 */       return Factory.getMatchingDummyValue(expectedType);
/*     */     }
/* 303 */     if (expectedType.isArray()) {
/* 304 */       if (Class.class.equals(expectedType.getComponentType()))
/*     */       {
/* 306 */         if ((actualType.isArrayType()) && ((actualValue instanceof Object[])) && 
/* 307 */           (((ArrayBinding)actualType).leafComponentType.erasure().id == 16)) {
/* 308 */           Object[] bindings = (Object[])actualValue;
/* 309 */           List mirrors = new ArrayList(bindings.length);
/* 310 */           for (int i = 0; i < bindings.length; i++) {
/* 311 */             if ((bindings[i] instanceof TypeBinding)) {
/* 312 */               mirrors.add(this._env.getFactory().newTypeMirror((TypeBinding)bindings[i]));
/*     */             }
/*     */           }
/* 315 */           throw new MirroredTypesException(mirrors);
/*     */         }
/*     */ 
/* 318 */         return null;
/*     */       }
/*     */ 
/* 321 */       return convertJDTArrayToReflectionArray(actualValue, actualType, expectedType);
/*     */     }
/* 323 */     if (Class.class.equals(expectedType))
/*     */     {
/* 325 */       if ((actualValue instanceof TypeBinding)) {
/* 326 */         TypeMirror mirror = this._env.getFactory().newTypeMirror((TypeBinding)actualValue);
/* 327 */         throw new MirroredTypeException(mirror);
/*     */       }
/*     */ 
/* 331 */       return null;
/*     */     }
/*     */ 
/* 336 */     return convertJDTValueToReflectionType(actualValue, actualType, expectedType);
/*     */   }
/*     */ 
/*     */   private Object convertJDTArrayToReflectionArray(Object jdtValue, TypeBinding jdtType, Class<?> expectedType)
/*     */   {
/* 354 */     assert ((expectedType != null) && (expectedType.isArray()));
/* 355 */     if ((!jdtType.isArrayType()) || (!(jdtValue instanceof Object[])))
/*     */     {
/* 357 */       return null;
/*     */     }
/* 359 */     TypeBinding jdtLeafType = jdtType.leafComponentType();
/* 360 */     Object[] jdtArray = (Object[])jdtValue;
/* 361 */     Class expectedLeafType = expectedType.getComponentType();
/* 362 */     int length = jdtArray.length;
/* 363 */     Object returnArray = Array.newInstance(expectedLeafType, length);
/* 364 */     for (int i = 0; i < length; i++) {
/* 365 */       Object jdtElementValue = jdtArray[i];
/* 366 */       if ((expectedLeafType.isPrimitive()) || (String.class.equals(expectedLeafType))) {
/* 367 */         if ((jdtElementValue instanceof Constant)) {
/* 368 */           if (Boolean.TYPE.equals(expectedLeafType)) {
/* 369 */             Array.setBoolean(returnArray, i, ((Constant)jdtElementValue).booleanValue());
/*     */           }
/* 371 */           else if (Byte.TYPE.equals(expectedLeafType)) {
/* 372 */             Array.setByte(returnArray, i, ((Constant)jdtElementValue).byteValue());
/*     */           }
/* 374 */           else if (Character.TYPE.equals(expectedLeafType)) {
/* 375 */             Array.setChar(returnArray, i, ((Constant)jdtElementValue).charValue());
/*     */           }
/* 377 */           else if (Double.TYPE.equals(expectedLeafType)) {
/* 378 */             Array.setDouble(returnArray, i, ((Constant)jdtElementValue).doubleValue());
/*     */           }
/* 380 */           else if (Float.TYPE.equals(expectedLeafType)) {
/* 381 */             Array.setFloat(returnArray, i, ((Constant)jdtElementValue).floatValue());
/*     */           }
/* 383 */           else if (Integer.TYPE.equals(expectedLeafType)) {
/* 384 */             Array.setInt(returnArray, i, ((Constant)jdtElementValue).intValue());
/*     */           }
/* 386 */           else if (Long.TYPE.equals(expectedLeafType)) {
/* 387 */             Array.setLong(returnArray, i, ((Constant)jdtElementValue).longValue());
/*     */           }
/* 389 */           else if (Short.TYPE.equals(expectedLeafType)) {
/* 390 */             Array.setShort(returnArray, i, ((Constant)jdtElementValue).shortValue());
/*     */           }
/* 392 */           else if (String.class.equals(expectedLeafType)) {
/* 393 */             Array.set(returnArray, i, ((Constant)jdtElementValue).stringValue());
/*     */           }
/*     */ 
/*     */         }
/*     */         else
/*     */         {
/* 399 */           Factory.setArrayMatchingDummyValue(returnArray, i, expectedLeafType);
/*     */         }
/*     */       }
/* 402 */       else if (expectedLeafType.isEnum()) {
/* 403 */         Object returnVal = null;
/* 404 */         if ((jdtLeafType != null) && (jdtLeafType.isEnum()) && ((jdtElementValue instanceof FieldBinding))) {
/* 405 */           FieldBinding binding = (FieldBinding)jdtElementValue;
/*     */           try {
/* 407 */             Field returnedField = null;
/* 408 */             returnedField = expectedLeafType.getField(new String(binding.name));
/* 409 */             if (returnedField != null) {
/* 410 */               returnVal = returnedField.get(null);
/*     */             }
/*     */           }
/*     */           catch (NoSuchFieldException localNoSuchFieldException)
/*     */           {
/*     */           }
/*     */           catch (IllegalAccessException localIllegalAccessException)
/*     */           {
/*     */           }
/*     */         }
/* 420 */         Array.set(returnArray, i, returnVal);
/*     */       }
/* 422 */       else if (expectedLeafType.isAnnotation())
/*     */       {
/* 424 */         Object returnVal = null;
/* 425 */         if ((jdtLeafType.isAnnotationType()) && ((jdtElementValue instanceof AnnotationBinding))) {
/* 426 */           AnnotationMirrorImpl annoMirror = 
/* 427 */             (AnnotationMirrorImpl)this._env.getFactory().newAnnotationMirror((AnnotationBinding)jdtElementValue);
/* 428 */           returnVal = Proxy.newProxyInstance(expectedLeafType.getClassLoader(), 
/* 429 */             new Class[] { expectedLeafType }, annoMirror);
/*     */         }
/* 431 */         Array.set(returnArray, i, returnVal);
/*     */       }
/*     */       else {
/* 434 */         Array.set(returnArray, i, null);
/*     */       }
/*     */     }
/* 437 */     return returnArray;
/*     */   }
/*     */ 
/*     */   private Object convertJDTValueToReflectionType(Object jdtValue, TypeBinding actualType, Class<?> expectedType)
/*     */   {
/* 448 */     if ((expectedType.isPrimitive()) || (String.class.equals(expectedType))) {
/* 449 */       if ((jdtValue instanceof Constant)) {
/* 450 */         if (Boolean.TYPE.equals(expectedType)) {
/* 451 */           return Boolean.valueOf(((Constant)jdtValue).booleanValue());
/*     */         }
/* 453 */         if (Byte.TYPE.equals(expectedType)) {
/* 454 */           return Byte.valueOf(((Constant)jdtValue).byteValue());
/*     */         }
/* 456 */         if (Character.TYPE.equals(expectedType)) {
/* 457 */           return Character.valueOf(((Constant)jdtValue).charValue());
/*     */         }
/* 459 */         if (Double.TYPE.equals(expectedType)) {
/* 460 */           return Double.valueOf(((Constant)jdtValue).doubleValue());
/*     */         }
/* 462 */         if (Float.TYPE.equals(expectedType)) {
/* 463 */           return Float.valueOf(((Constant)jdtValue).floatValue());
/*     */         }
/* 465 */         if (Integer.TYPE.equals(expectedType)) {
/* 466 */           return Integer.valueOf(((Constant)jdtValue).intValue());
/*     */         }
/* 468 */         if (Long.TYPE.equals(expectedType)) {
/* 469 */           return Long.valueOf(((Constant)jdtValue).longValue());
/*     */         }
/* 471 */         if (Short.TYPE.equals(expectedType)) {
/* 472 */           return Short.valueOf(((Constant)jdtValue).shortValue());
/*     */         }
/* 474 */         if (String.class.equals(expectedType)) {
/* 475 */           return ((Constant)jdtValue).stringValue();
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/* 480 */       return Factory.getMatchingDummyValue(expectedType);
/*     */     }
/* 482 */     if (expectedType.isEnum()) {
/* 483 */       Object returnVal = null;
/* 484 */       if ((actualType != null) && (actualType.isEnum()) && ((jdtValue instanceof FieldBinding)))
/*     */       {
/* 486 */         FieldBinding binding = (FieldBinding)jdtValue;
/*     */         try {
/* 488 */           Field returnedField = null;
/* 489 */           returnedField = expectedType.getField(new String(binding.name));
/* 490 */           if (returnedField != null) {
/* 491 */             returnVal = returnedField.get(null);
/*     */           }
/*     */         }
/*     */         catch (NoSuchFieldException localNoSuchFieldException)
/*     */         {
/*     */         }
/*     */         catch (IllegalAccessException localIllegalAccessException)
/*     */         {
/*     */         }
/*     */       }
/* 501 */       return returnVal == null ? Factory.getMatchingDummyValue(expectedType) : returnVal;
/*     */     }
/* 503 */     if (expectedType.isAnnotation())
/*     */     {
/* 505 */       if ((actualType.isAnnotationType()) && ((jdtValue instanceof AnnotationBinding))) {
/* 506 */         AnnotationMirrorImpl annoMirror = 
/* 507 */           (AnnotationMirrorImpl)this._env.getFactory().newAnnotationMirror((AnnotationBinding)jdtValue);
/* 508 */         return Proxy.newProxyInstance(expectedType.getClassLoader(), 
/* 509 */           new Class[] { expectedType }, annoMirror);
/*     */       }
/*     */ 
/* 513 */       return null;
/*     */     }
/*     */ 
/* 517 */     return Factory.getMatchingDummyValue(expectedType);
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\ecj-3.5.2.jar
 * Qualified Name:     org.eclipse.jdt.internal.compiler.apt.model.AnnotationMirrorImpl
 * JD-Core Version:    0.6.0
 */