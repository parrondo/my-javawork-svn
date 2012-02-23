/*     */ package com.dukascopy.api.nlink;
/*     */ 
/*     */ import com.dukascopy.api.DllMethod;
/*     */ import com.dukascopy.api.nlink.impl.MethodIntrospector;
/*     */ import com.dukascopy.api.nlink.win32.CheckLastError;
/*     */ import com.dukascopy.api.nlink.win32.NativeImport;
/*     */ import java.lang.annotation.Annotation;
/*     */ import java.lang.reflect.Array;
/*     */ import java.lang.reflect.GenericArrayType;
/*     */ import java.lang.reflect.InvocationHandler;
/*     */ import java.lang.reflect.InvocationTargetException;
/*     */ import java.lang.reflect.Method;
/*     */ import java.lang.reflect.ParameterizedType;
/*     */ import java.lang.reflect.Type;
/*     */ import java.lang.reflect.TypeVariable;
/*     */ import java.lang.reflect.WildcardType;
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
/*     */ 
/*     */ public class Wrapper
/*     */   implements InvocationHandler
/*     */ {
/*     */   private final int hModule;
/*  30 */   private final Map<Method, Invoker> methods = new HashMap();
/*     */ 
/* 254 */   private static final Object[] EMPTY_ARRAY = new Object[0];
/*     */ 
/*     */   public Wrapper(Class<?> dllInterface, String libPath)
/*     */   {
/*  33 */     this.hModule = Native.loadLibrary(libPath);
/*  34 */     if (this.hModule == 0) {
/*  35 */       throw new Win32NLinkException("Failed to load library " + libPath);
/*     */     }
/*     */ 
/*  39 */     for (Method m : dllInterface.getDeclaredMethods()) {
/*  40 */       DllMethod dm = (DllMethod)m.getAnnotation(DllMethod.class);
/*     */ 
/*  42 */       if (dm == null)
/*     */       {
/*     */         continue;
/*     */       }
/*  46 */       this.methods.put(m, new Invoker(m, dm));
/*     */     }
/*     */ 
/*  49 */     if (this.methods.isEmpty())
/*  50 */       throw new NLinkException("No @DllMethod found on " + dllInterface);
/*     */   }
/*     */ 
/*     */   public Object invoke(Object proxy, Method method, Object[] args) throws Throwable
/*     */   {
/*  55 */     if (args == null) {
/*  56 */       args = EMPTY_ARRAY;
/*     */     }
/*     */ 
/*  59 */     if (method.getDeclaringClass() == Object.class) {
/*     */       try {
/*  61 */         return method.invoke(this, args);
/*     */       } catch (InvocationTargetException e) {
/*  63 */         throw e.getTargetException();
/*     */       }
/*     */     }
/*     */ 
/*  67 */     Invoker invoker = (Invoker)this.methods.get(method);
/*  68 */     if (invoker == null) {
/*  69 */       throw new NLinkException("Not a DLL method: " + method);
/*     */     }
/*     */ 
/*  72 */     return invoker.invoke(args); } 
/*     */   private class Invoker { final Method method;
/*     */     final int functionPtr;
/*     */     final int[] paramConvs;
/*     */     final NativeType[] params;
/*     */     final NativeType returnConv;
/*     */     final Class<?>[] paramTypes;
/*     */     final Type[] genericParamTypes;
/*     */     final boolean checkError;
/*     */ 
/*  89 */     public Invoker(Method m, DllMethod dm) { String name = dm.value();
/*  90 */       if (name.equals("")) {
/*  91 */         int p = Native.getProcAddress(Wrapper.this.hModule, m.getName());
/*  92 */         if (p == 0)
/*     */         {
/*  94 */           p = Native.getProcAddress(Wrapper.this.hModule, m.getName() + 'W');
/*     */         }
/*  96 */         this.functionPtr = p;
/*  97 */       } else if (name.startsWith("#")) {
/*  98 */         this.functionPtr = Native.getProcAddress2(Wrapper.this.hModule, Integer.parseInt(name.substring(1)));
/*     */       }
/*     */       else {
/* 101 */         this.functionPtr = Native.getProcAddress(Wrapper.this.hModule, name);
/*     */       }
/*     */ 
/* 104 */       if (this.functionPtr == 0) {
/* 105 */         throw new Win32NLinkException("Failed to find a function for " + m.getName());
/*     */       }
/*     */ 
/* 108 */       this.method = m;
/*     */ 
/* 110 */       this.checkError = (m.getAnnotation(CheckLastError.class) != null);
/*     */ 
/* 112 */       MethodIntrospector mi = new MethodIntrospector(m);
/*     */ 
/* 114 */       Annotation[][] pa = m.getParameterAnnotations();
/* 115 */       int paramLen = pa.length;
/*     */ 
/* 117 */       this.returnConv = mi.getReturnConversion();
/* 118 */       this.paramTypes = m.getParameterTypes();
/* 119 */       this.genericParamTypes = m.getGenericParameterTypes();
/* 120 */       this.paramConvs = new int[paramLen];
/* 121 */       this.params = new NativeType[paramLen];
/* 122 */       for (int i = 0; i < paramLen; i++) {
/* 123 */         NativeType n = mi.getParamConversion(i);
/* 124 */         this.params[i] = n;
/* 125 */         this.paramConvs[i] = n.code;
/*     */       }
/*     */     }
/*     */ 
/*     */     public Invoker(Method m, NativeImport dm)
/*     */     {
/* 131 */       String name = dm.function();
/* 132 */       if (name.equals("")) {
/* 133 */         int p = Native.getProcAddress(Wrapper.this.hModule, m.getName());
/* 134 */         if (p == 0)
/*     */         {
/* 136 */           p = Native.getProcAddress(Wrapper.this.hModule, m.getName() + 'W');
/*     */         }
/* 138 */         this.functionPtr = p;
/* 139 */       } else if (name.startsWith("#")) {
/* 140 */         this.functionPtr = Native.getProcAddress2(Wrapper.this.hModule, Integer.parseInt(name.substring(1)));
/*     */       }
/*     */       else {
/* 143 */         this.functionPtr = Native.getProcAddress(Wrapper.this.hModule, name);
/*     */       }
/*     */ 
/* 146 */       if (this.functionPtr == 0) {
/* 147 */         throw new Win32NLinkException("Failed to find a function for " + m.getName());
/*     */       }
/*     */ 
/* 150 */       this.method = m;
/*     */ 
/* 152 */       this.checkError = (m.getAnnotation(CheckLastError.class) != null);
/*     */ 
/* 154 */       MethodIntrospector mi = new MethodIntrospector(m);
/*     */ 
/* 156 */       Annotation[][] pa = m.getParameterAnnotations();
/* 157 */       int paramLen = pa.length;
/*     */ 
/* 159 */       this.returnConv = mi.getReturnConversion();
/* 160 */       this.paramTypes = m.getParameterTypes();
/* 161 */       this.genericParamTypes = m.getGenericParameterTypes();
/* 162 */       this.paramConvs = new int[paramLen];
/* 163 */       this.params = new NativeType[paramLen];
/* 164 */       for (int i = 0; i < paramLen; i++) {
/* 165 */         NativeType n = mi.getParamConversion(i);
/* 166 */         this.params[i] = n;
/* 167 */         this.paramConvs[i] = n.code;
/*     */       }
/*     */     }
/*     */ 
/*     */     Object invoke(Object[] args)
/*     */     {
/*     */       Holder h;
/* 172 */       for (int i = 0; i < args.length; i++) {
/* 173 */         if (((args[i] instanceof Holder)) && (this.params[i].getNoByRef() != null))
/*     */         {
/* 175 */           h = (Holder)args[i];
/* 176 */           h.value = this.params[i].getNoByRef().massage(h.value);
/*     */         } else {
/* 178 */           args[i] = this.params[i].massage(args[i]);
/*     */         }
/*     */       }
/*     */       try
/*     */       {
/* 183 */         Object r = Native.invoke(this.functionPtr, args, this.paramConvs, this.method.getReturnType(), this.returnConv.code);
/*     */ 
/* 186 */         if ((this.checkError) && (Native.getLastError() != 0)) {
/* 187 */           throw new Win32NLinkException(this.method + " failed");
/*     */         }
/*     */ 
/* 190 */         h = this.returnConv.unmassage(this.method.getReturnType(), this.method.getGenericReturnType(), r);
/*     */         int i;
/*     */         Holder h;
/*     */         Type holderParamType;
/*     */         return h; } finally { for (int i = 0; i < args.length; i++)
/* 194 */           if (((args[i] instanceof Holder)) && (this.params[i].getNoByRef() != null))
/*     */           {
/* 196 */             Holder h = (Holder)args[i];
/* 197 */             Type holderParamType = getTypeParameter(this.genericParamTypes[i], 0);
/*     */ 
/* 199 */             h.value = this.params[i].getNoByRef().unmassage(erasure(holderParamType), holderParamType, h.value);
/*     */           }
/*     */           else
/*     */           {
/* 203 */             args[i] = this.params[i].unmassage(this.paramTypes[i], this.genericParamTypes[i], args[i]);
/*     */           }
/*     */       }
/* 193 */       throw localObject1;
/*     */     }
/*     */ 
/*     */     private Type getTypeParameter(Type t, int index)
/*     */     {
/* 211 */       if ((t instanceof ParameterizedType)) {
/* 212 */         ParameterizedType pt = (ParameterizedType)t;
/* 213 */         return pt.getActualTypeArguments()[index];
/*     */       }
/* 215 */       return Object.class;
/*     */     }
/*     */ 
/*     */     private Class<?> erasure(Type t)
/*     */     {
/* 220 */       if ((t instanceof Class)) {
/* 221 */         return (Class)t;
/*     */       }
/* 223 */       if ((t instanceof ParameterizedType)) {
/* 224 */         ParameterizedType pt = (ParameterizedType)t;
/* 225 */         return erasure(pt.getRawType());
/*     */       }
/* 227 */       if ((t instanceof WildcardType)) {
/* 228 */         WildcardType wt = (WildcardType)t;
/* 229 */         Type[] ub = wt.getUpperBounds();
/* 230 */         if (ub.length == 0) {
/* 231 */           return Object.class;
/*     */         }
/* 233 */         return erasure(ub[0]);
/*     */       }
/*     */ 
/* 236 */       if ((t instanceof GenericArrayType)) {
/* 237 */         GenericArrayType ga = (GenericArrayType)t;
/* 238 */         return Array.newInstance(erasure(ga.getGenericComponentType()), 0).getClass();
/*     */       }
/*     */ 
/* 241 */       if ((t instanceof TypeVariable)) {
/* 242 */         TypeVariable tv = (TypeVariable)t;
/* 243 */         Type[] ub = tv.getBounds();
/* 244 */         if (ub.length == 0) {
/* 245 */           return Object.class;
/*     */         }
/* 247 */         return erasure(ub[0]);
/*     */       }
/*     */ 
/* 250 */       throw new IllegalArgumentException(t.toString());
/*     */     }
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Connector-1.1.49.jar
 * Qualified Name:     com.dukascopy.api.nlink.Wrapper
 * JD-Core Version:    0.6.0
 */