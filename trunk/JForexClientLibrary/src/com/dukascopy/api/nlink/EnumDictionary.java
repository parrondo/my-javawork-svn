/*    */ package com.dukascopy.api.nlink;
/*    */ 
/*    */ import java.util.Collections;
/*    */ import java.util.HashMap;
/*    */ import java.util.Map;
/*    */ import java.util.WeakHashMap;
/*    */ 
/*    */ abstract class EnumDictionary<T extends Enum<T>>
/*    */ {
/*    */   protected final Class<T> clazz;
/*    */   private static final Map<Class<? extends Enum>, EnumDictionary> registry;
/*    */ 
/*    */   private EnumDictionary(Class<T> clazz)
/*    */   {
/* 17 */     this.clazz = clazz;
/* 18 */     assert (clazz.isEnum());
/*    */   }
/*    */ 
/*    */   public static <T extends Enum<T>> EnumDictionary<T> get(Class<T> clazz)
/*    */   {
/* 25 */     EnumDictionary dic = (EnumDictionary)registry.get(clazz);
/* 26 */     if (dic == null) {
/* 27 */       boolean sparse = NLinkEnum.class.isAssignableFrom(clazz);
/* 28 */       if (sparse)
/* 29 */         dic = new Sparse(clazz, null);
/*    */       else
/* 31 */         dic = new Continuous(clazz, null);
/* 32 */       registry.put(clazz, dic);
/*    */     }
/* 34 */     return dic;
/*    */   }
/*    */ 
/*    */   abstract int value(Enum paramEnum);
/*    */ 
/*    */   abstract T constant(int paramInt);
/*    */ 
/*    */   static
/*    */   {
/* 99 */     registry = Collections.synchronizedMap(new WeakHashMap());
/*    */   }
/*    */ 
/*    */   static class Sparse<T extends Enum<T>> extends EnumDictionary<T>
/*    */   {
/* 73 */     private final Map<Integer, T> fromValue = new HashMap();
/*    */ 
/*    */     private Sparse(Class<T> clazz) {
/* 76 */       super(null);
/*    */ 
/* 78 */       Enum[] consts = (Enum[])clazz.getEnumConstants();
/* 79 */       for (Enum v : consts)
/* 80 */         this.fromValue.put(Integer.valueOf(((NLinkEnum)v).nativeEnumValue()), v);
/*    */     }
/*    */ 
/*    */     public int value(Enum t)
/*    */     {
/* 86 */       return ((NLinkEnum)t).nativeEnumValue();
/*    */     }
/*    */ 
/*    */     public T constant(int v)
/*    */     {
/* 91 */       Enum t = (Enum)this.fromValue.get(Integer.valueOf(v));
/* 92 */       if (t == null) {
/* 93 */         throw new IllegalArgumentException(this.clazz.getName() + " has no constant of the value " + v);
/*    */       }
/* 95 */       return t;
/*    */     }
/*    */   }
/*    */ 
/*    */   static class Continuous<T extends Enum<T>> extends EnumDictionary<T>
/*    */   {
/*    */     private T[] consts;
/*    */ 
/*    */     private Continuous(Class<T> clazz)
/*    */     {
/* 54 */       super(null);
/* 55 */       this.consts = ((Enum[])clazz.getEnumConstants());
/*    */     }
/*    */ 
/*    */     public int value(Enum t)
/*    */     {
/* 60 */       return t.ordinal();
/*    */     }
/*    */ 
/*    */     public T constant(int v)
/*    */     {
/* 65 */       return this.consts[v];
/*    */     }
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Connector-1.1.49.jar
 * Qualified Name:     com.dukascopy.api.nlink.EnumDictionary
 * JD-Core Version:    0.6.0
 */