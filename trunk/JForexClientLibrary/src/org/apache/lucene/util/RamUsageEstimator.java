/*     */ package org.apache.lucene.util;
/*     */ 
/*     */ import java.lang.reflect.Array;
/*     */ import java.lang.reflect.Field;
/*     */ import java.lang.reflect.Modifier;
/*     */ import java.text.DecimalFormat;
/*     */ import java.util.IdentityHashMap;
/*     */ import java.util.Map;
/*     */ 
/*     */ public final class RamUsageEstimator
/*     */ {
/*     */   public static final int NUM_BYTES_SHORT = 2;
/*     */   public static final int NUM_BYTES_INT = 4;
/*     */   public static final int NUM_BYTES_LONG = 8;
/*     */   public static final int NUM_BYTES_FLOAT = 4;
/*     */   public static final int NUM_BYTES_DOUBLE = 8;
/*     */   public static final int NUM_BYTES_CHAR = 2;
/*     */   public static final int NUM_BYTES_OBJECT_HEADER = 8;
/*  48 */   public static final int NUM_BYTES_OBJECT_REF = Constants.JRE_IS_64BIT ? 8 : 4;
/*  49 */   public static final int NUM_BYTES_ARRAY_HEADER = 12 + NUM_BYTES_OBJECT_REF;
/*     */   private MemoryModel memoryModel;
/*     */   private final Map<Object, Object> seen;
/*     */   private int refSize;
/*     */   private int arraySize;
/*     */   private int classSize;
/*     */   private boolean checkInterned;
/*     */   private static final long ONE_KB = 1024L;
/*     */   private static final long ONE_MB = 1048576L;
/*     */   private static final long ONE_GB = 1073741824L;
/*     */ 
/*     */   public RamUsageEstimator()
/*     */   {
/*  66 */     this(new AverageGuessMemoryModel());
/*     */   }
/*     */ 
/*     */   public RamUsageEstimator(boolean checkInterned)
/*     */   {
/*  76 */     this(new AverageGuessMemoryModel(), checkInterned);
/*     */   }
/*     */ 
/*     */   public RamUsageEstimator(MemoryModel memoryModel)
/*     */   {
/*  83 */     this(memoryModel, true);
/*     */   }
/*     */ 
/*     */   public RamUsageEstimator(MemoryModel memoryModel, boolean checkInterned)
/*     */   {
/*  94 */     this.memoryModel = memoryModel;
/*  95 */     this.checkInterned = checkInterned;
/*     */ 
/*  98 */     this.seen = new IdentityHashMap(64);
/*  99 */     this.refSize = memoryModel.getReferenceSize();
/* 100 */     this.arraySize = memoryModel.getArraySize();
/* 101 */     this.classSize = memoryModel.getClassSize();
/*     */   }
/*     */ 
/*     */   public long estimateRamUsage(Object obj) {
/* 105 */     long size = size(obj);
/* 106 */     this.seen.clear();
/* 107 */     return size;
/*     */   }
/*     */ 
/*     */   private long size(Object obj) {
/* 111 */     if (obj == null) {
/* 112 */       return 0L;
/*     */     }
/*     */ 
/* 115 */     if ((this.checkInterned) && ((obj instanceof String)) && (obj == ((String)obj).intern()))
/*     */     {
/* 119 */       return 0L;
/*     */     }
/*     */ 
/* 123 */     if (this.seen.containsKey(obj)) {
/* 124 */       return 0L;
/*     */     }
/*     */ 
/* 128 */     this.seen.put(obj, null);
/*     */ 
/* 130 */     Class clazz = obj.getClass();
/* 131 */     if (clazz.isArray()) {
/* 132 */       return sizeOfArray(obj);
/*     */     }
/*     */ 
/* 135 */     long size = 0L;
/*     */ 
/* 138 */     while (clazz != null) {
/* 139 */       Field[] fields = clazz.getDeclaredFields();
/* 140 */       for (int i = 0; i < fields.length; i++) {
/* 141 */         if (Modifier.isStatic(fields[i].getModifiers()))
/*     */         {
/*     */           continue;
/*     */         }
/* 145 */         if (fields[i].getType().isPrimitive()) {
/* 146 */           size += this.memoryModel.getPrimitiveSize(fields[i].getType());
/*     */         } else {
/* 148 */           size += this.refSize;
/* 149 */           fields[i].setAccessible(true);
/*     */           try {
/* 151 */             Object value = fields[i].get(obj);
/* 152 */             if (value != null) {
/* 153 */               size += size(value);
/*     */             }
/*     */           }
/*     */           catch (IllegalAccessException ex)
/*     */           {
/*     */           }
/*     */         }
/*     */       }
/* 161 */       clazz = clazz.getSuperclass();
/*     */     }
/* 163 */     size += this.classSize;
/* 164 */     return size;
/*     */   }
/*     */ 
/*     */   private long sizeOfArray(Object obj) {
/* 168 */     int len = Array.getLength(obj);
/* 169 */     if (len == 0) {
/* 170 */       return 0L;
/*     */     }
/* 172 */     long size = this.arraySize;
/* 173 */     Class arrayElementClazz = obj.getClass().getComponentType();
/* 174 */     if (arrayElementClazz.isPrimitive())
/* 175 */       size += len * this.memoryModel.getPrimitiveSize(arrayElementClazz);
/*     */     else {
/* 177 */       for (int i = 0; i < len; i++) {
/* 178 */         size += this.refSize + size(Array.get(obj, i));
/*     */       }
/*     */     }
/*     */ 
/* 182 */     return size;
/*     */   }
/*     */ 
/*     */   public static String humanReadableUnits(long bytes, DecimalFormat df)
/*     */   {
/*     */     String newSizeAndUnits;
/*     */     String newSizeAndUnits;
/* 195 */     if (bytes / 1073741824L > 0L) {
/* 196 */       newSizeAndUnits = String.valueOf(df.format((float)bytes / 1.073742E+009F)) + " GB";
/*     */     }
/*     */     else
/*     */     {
/*     */       String newSizeAndUnits;
/* 198 */       if (bytes / 1048576L > 0L) {
/* 199 */         newSizeAndUnits = String.valueOf(df.format((float)bytes / 1048576.0F)) + " MB";
/*     */       }
/*     */       else
/*     */       {
/*     */         String newSizeAndUnits;
/* 201 */         if (bytes / 1024L > 0L) {
/* 202 */           newSizeAndUnits = String.valueOf(df.format((float)bytes / 1024.0F)) + " KB";
/*     */         }
/*     */         else
/* 205 */           newSizeAndUnits = String.valueOf(bytes) + " bytes";
/*     */       }
/*     */     }
/* 208 */     return newSizeAndUnits;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.util.RamUsageEstimator
 * JD-Core Version:    0.6.0
 */