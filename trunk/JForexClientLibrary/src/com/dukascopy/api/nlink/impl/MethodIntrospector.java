/*     */ package com.dukascopy.api.nlink.impl;
/*     */ 
/*     */ import com.dukascopy.api.nlink.Holder;
/*     */ import com.dukascopy.api.nlink.IllegalAnnotationError;
/*     */ import com.dukascopy.api.nlink.MarshalAs;
/*     */ import com.dukascopy.api.nlink.NativeType;
/*     */ import java.lang.annotation.Annotation;
/*     */ import java.lang.reflect.Method;
/*     */ import java.lang.reflect.ParameterizedType;
/*     */ import java.lang.reflect.Type;
/*     */ import java.nio.Buffer;
/*     */ import java.util.Calendar;
/*     */ import java.util.Date;
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
/*     */ 
/*     */ public class MethodIntrospector
/*     */ {
/*     */   final Method method;
/*     */   final Annotation[][] pa;
/*     */   final Type[] paramTypes;
/*  71 */   private static final Map<Class<?>, NativeType> defaultConversions = new HashMap();
/*     */ 
/*     */   public MethodIntrospector(Method method)
/*     */   {
/*  30 */     this.method = method;
/*  31 */     this.pa = method.getParameterAnnotations();
/*  32 */     this.paramTypes = method.getParameterTypes();
/*     */   }
/*     */ 
/*     */   public final MarshalAs getMarshalAs(int idx) {
/*  36 */     for (Annotation a : this.pa[idx]) {
/*  37 */       if ((a instanceof MarshalAs))
/*  38 */         return (MarshalAs)a;
/*     */     }
/*  40 */     return null;
/*     */   }
/*     */ 
/*     */   public final NativeType getParamConversion(int idx) {
/*  44 */     MarshalAs ma = getMarshalAs(idx);
/*  45 */     if (ma != null) {
/*  46 */       return ma.value();
/*     */     }
/*  48 */     return getDefaultConversion(this.paramTypes[idx]);
/*     */   }
/*     */ 
/*     */   public final int paramLength()
/*     */   {
/*  53 */     return this.pa.length;
/*     */   }
/*     */ 
/*     */   public final NativeType getReturnConversion() {
/*  57 */     MarshalAs rt = (MarshalAs)this.method.getAnnotation(MarshalAs.class);
/*  58 */     if (rt != null) {
/*  59 */       return rt.value();
/*     */     }
/*     */ 
/*  62 */     if (this.method.getReturnType() == Void.TYPE)
/*     */     {
/*  64 */       return NativeType.Default;
/*     */     }
/*  66 */     return getDefaultConversion(this.method.getReturnType());
/*     */   }
/*     */ 
/*     */   static NativeType getDefaultConversion(Type t)
/*     */   {
/*  88 */     if ((t instanceof Class)) {
/*  89 */       Class c = (Class)t;
/*  90 */       NativeType r = (NativeType)defaultConversions.get(c);
/*  91 */       if (r != null) {
/*  92 */         return r;
/*     */       }
/*  94 */       if (Enum.class.isAssignableFrom(c))
/*  95 */         return NativeType.Int32;
/*  96 */       if (Buffer.class.isAssignableFrom(c))
/*  97 */         return NativeType.PVOID;
/*  98 */       if (Calendar.class.isAssignableFrom(c)) {
/*  99 */         return NativeType.Date;
/*     */       }
/*     */     }
/* 102 */     if ((t instanceof ParameterizedType)) {
/* 103 */       ParameterizedType p = (ParameterizedType)t;
/* 104 */       if (p.getRawType() == Holder.class)
/*     */       {
/* 106 */         Type v = p.getActualTypeArguments()[0];
/* 107 */         if (String.class == v)
/* 108 */           return NativeType.BSTR_ByRef;
/* 109 */         if ((Integer.class == v) || (Enum.class.isAssignableFrom((Class)v)))
/*     */         {
/* 111 */           return NativeType.Int32_ByRef;
/* 112 */         }if (Boolean.class == v) {
/* 113 */           return NativeType.VariantBool_ByRef;
/*     */         }
/*     */       }
/*     */     }
/* 117 */     throw new IllegalAnnotationError("no default conversion available for " + t);
/*     */   }
/*     */ 
/*     */   static
/*     */   {
/*  74 */     defaultConversions.put(Double.TYPE, NativeType.Double);
/*  75 */     defaultConversions.put(Float.TYPE, NativeType.Float);
/*  76 */     defaultConversions.put(Integer.TYPE, NativeType.Int32);
/*  77 */     defaultConversions.put(Short.TYPE, NativeType.Int16);
/*  78 */     defaultConversions.put(Byte.TYPE, NativeType.Int8);
/*  79 */     defaultConversions.put(Boolean.TYPE, NativeType.VariantBool);
/*  80 */     defaultConversions.put(String.class, NativeType.Unicode);
/*  81 */     defaultConversions.put(Date.class, NativeType.Date);
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Connector-1.1.49.jar
 * Qualified Name:     com.dukascopy.api.nlink.impl.MethodIntrospector
 * JD-Core Version:    0.6.0
 */