/*     */ package org.apache.lucene.util;
/*     */ 
/*     */ import java.lang.reflect.Method;
/*     */ import java.util.Collections;
/*     */ import java.util.HashSet;
/*     */ import java.util.Set;
/*     */ import java.util.WeakHashMap;
/*     */ 
/*     */ public final class VirtualMethod<C>
/*     */ {
/*  62 */   private static final Set<Method> singletonSet = Collections.synchronizedSet(new HashSet());
/*     */   private final Class<C> baseClass;
/*     */   private final String method;
/*     */   private final Class<?>[] parameters;
/*  67 */   private final WeakHashMap<Class<? extends C>, Integer> cache = new WeakHashMap();
/*     */ 
/*     */   public VirtualMethod(Class<C> baseClass, String method, Class<?>[] parameters)
/*     */   {
/*  77 */     this.baseClass = baseClass;
/*  78 */     this.method = method;
/*  79 */     this.parameters = parameters;
/*     */     try {
/*  81 */       if (!singletonSet.add(baseClass.getDeclaredMethod(method, parameters))) {
/*  82 */         throw new UnsupportedOperationException("VirtualMethod instances must be singletons and therefore assigned to static final members in the same class, they use as baseClass ctor param.");
/*     */       }
/*     */     }
/*     */     catch (NoSuchMethodException nsme)
/*     */     {
/*  87 */       throw new IllegalArgumentException(baseClass.getName() + " has no such method: " + nsme.getMessage());
/*     */     }
/*     */   }
/*     */ 
/*     */   public synchronized int getImplementationDistance(Class<? extends C> subclazz)
/*     */   {
/*  97 */     Integer distance = (Integer)this.cache.get(subclazz);
/*  98 */     if (distance == null) {
/*  99 */       this.cache.put(subclazz, distance = Integer.valueOf(reflectImplementationDistance(subclazz)));
/*     */     }
/* 101 */     return distance.intValue();
/*     */   }
/*     */ 
/*     */   public boolean isOverriddenAsOf(Class<? extends C> subclazz)
/*     */   {
/* 112 */     return getImplementationDistance(subclazz) > 0;
/*     */   }
/*     */ 
/*     */   private int reflectImplementationDistance(Class<? extends C> subclazz) {
/* 116 */     if (!this.baseClass.isAssignableFrom(subclazz))
/* 117 */       throw new IllegalArgumentException(subclazz.getName() + " is not a subclass of " + this.baseClass.getName());
/* 118 */     boolean overridden = false;
/* 119 */     int distance = 0;
/* 120 */     for (Class clazz = subclazz; (clazz != this.baseClass) && (clazz != null); clazz = clazz.getSuperclass())
/*     */     {
/* 122 */       if (!overridden) {
/*     */         try {
/* 124 */           clazz.getDeclaredMethod(this.method, this.parameters);
/* 125 */           overridden = true;
/*     */         }
/*     */         catch (NoSuchMethodException nsme)
/*     */         {
/*     */         }
/*     */       }
/* 131 */       if (!overridden) continue; distance++;
/*     */     }
/* 133 */     return distance;
/*     */   }
/*     */ 
/*     */   public static <C> int compareImplementationDistance(Class<? extends C> clazz, VirtualMethod<C> m1, VirtualMethod<C> m2)
/*     */   {
/* 147 */     return Integer.valueOf(m1.getImplementationDistance(clazz)).compareTo(Integer.valueOf(m2.getImplementationDistance(clazz)));
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.util.VirtualMethod
 * JD-Core Version:    0.6.0
 */