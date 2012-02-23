/*     */ package com.dukascopy.api.nlink;
/*     */ 
/*     */ import java.lang.reflect.Type;
/*     */ import java.util.Calendar;
/*     */ import java.util.Date;
/*     */ import java.util.GregorianCalendar;
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
/*     */ import java.util.TimeZone;
/*     */ 
/*     */ public enum NativeType
/*     */ {
/*  26 */   BSTR(1, 4), 
/*     */ 
/*  35 */   BSTR_ByRef(32769, 4), 
/*     */ 
/*  42 */   Unicode(2, 4), 
/*     */ 
/*  55 */   CSTR(3, 4), 
/*     */ 
/*  63 */   Int8(100, 1), Int8_ByRef(32868, 1), 
/*     */ 
/*  70 */   Int16(101, 2), Int16_ByRef(32869, 2), 
/*     */ 
/*  81 */   Int32(102, 4), 
/*     */ 
/* 109 */   Int32_ByRef(32870, 4), 
/*     */ 
/* 118 */   Bool(103, 4), 
/*     */ 
/* 127 */   VariantBool(104, 2), VariantBool_ByRef(32872, 2), 
/*     */ 
/* 135 */   Float(120, 4), 
/*     */ 
/* 143 */   Double(121, 8), 
/*     */ 
/* 149 */   Default(201, 9999), 
/*     */ 
/* 162 */   PVOID(304, 4), 
/*     */ 
/* 174 */   PVOID_ByRef(33072, 4), 
/*     */ 
/* 184 */   Date(400, 8);
/*     */ 
/*     */   public final int code;
/*     */   final int size;
/*     */   private static final Map<Integer, NativeType> codeMap;
/*     */   private static final long MSPD = 86400000L;
/*     */   private static final TimeZone defaultTimeZone;
/*     */ 
/*     */   private NativeType(int code, int size)
/*     */   {
/* 254 */     this.code = code;
/* 255 */     this.size = size;
/*     */   }
/*     */ 
/*     */   public Object massage(Object param)
/*     */   {
/* 269 */     return param;
/*     */   }
/*     */ 
/*     */   public Object unmassage(Class<?> signature, Type genericSignature, Object param)
/*     */   {
/* 286 */     return param;
/*     */   }
/*     */ 
/*     */   public final NativeType byRef()
/*     */   {
/* 293 */     if (this.code == (this.code | 0x8000)) {
/* 294 */       return null;
/*     */     }
/* 296 */     return (NativeType)codeMap.get(Integer.valueOf(this.code | 0x8000));
/*     */   }
/*     */ 
/*     */   public final NativeType getNoByRef() {
/* 300 */     if (this.code == (this.code & 0xFFFF7FFF)) {
/* 301 */       return null;
/*     */     }
/* 303 */     return (NativeType)codeMap.get(Integer.valueOf(this.code & 0xFFFF7FFF));
/*     */   }
/*     */ 
/*     */   static
/*     */   {
/* 245 */     codeMap = new HashMap();
/*     */ 
/* 248 */     for (NativeType nt : values()) {
/* 249 */       codeMap.put(Integer.valueOf(nt.code), nt);
/*     */     }
/*     */ 
/* 307 */     defaultTimeZone = TimeZone.getDefault();
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Connector-1.1.49.jar
 * Qualified Name:     com.dukascopy.api.nlink.NativeType
 * JD-Core Version:    0.6.0
 */