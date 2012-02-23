/*     */ package com.dukascopy.api.nlink.win32.engine;
/*     */ 
/*     */ import com.dukascopy.api.nlink.win32.Callback;
/*     */ import com.dukascopy.api.nlink.win32.Charset;
/*     */ import com.dukascopy.api.nlink.win32.Embedded;
/*     */ import com.dukascopy.api.nlink.win32.NativeStruct;
/*     */ import java.io.IOException;
/*     */ import java.io.PrintStream;
/*     */ import java.lang.reflect.Field;
/*     */ 
/*     */ public class Util
/*     */ {
/*     */   public static int getStructSize(Object o)
/*     */     throws IllegalArgumentException, IOException, IllegalAccessException
/*     */   {
/*  14 */     return getStructSize(o.getClass());
/*     */   }
/*     */ 
/*     */   public static int getStructSize(Class structClass)
/*     */     throws IllegalArgumentException, IOException, IllegalAccessException
/*     */   {
/*  20 */     Field[] fields = structClass.getFields();
/*  21 */     NativeStruct nativeAnno = (NativeStruct)structClass.getAnnotation(NativeStruct.class);
/*     */ 
/*  23 */     if (nativeAnno == null) {
/*  24 */       throw new IllegalArgumentException("The class " + structClass + " does not have @NativeStruct annotation");
/*     */     }
/*     */ 
/*  27 */     int packing = nativeAnno.packing();
/*  28 */     Charset charset = nativeAnno.charset();
/*     */ 
/*  30 */     int size = 0;
/*  31 */     for (int i = 0; i < fields.length; i++) {
/*  32 */       Field field = fields[i];
/*  33 */       size = getStructMemberSize(field, size, packing, charset);
/*     */     }
/*     */ 
/*  36 */     int structAlignment = getNaturalAlignment(structClass, charset);
/*  37 */     if (structAlignment != 0) {
/*  38 */       int remainder = size % structAlignment;
/*  39 */       if (remainder != 0) {
/*  40 */         size += structAlignment - remainder;
/*     */       }
/*     */     }
/*  43 */     return size;
/*     */   }
/*     */ 
/*     */   static int getStructMemberSize(Field field, int size, int packing, Charset charset)
/*     */     throws IllegalArgumentException, IOException, IllegalAccessException
/*     */   {
/*  49 */     Class type = field.getType();
/*     */ 
/*  51 */     int naturalalignment = getNaturalAlignment(type, charset);
/*     */ 
/*  53 */     int alignment = naturalalignment <= packing ? naturalalignment : packing;
/*     */ 
/*  56 */     int gap = 0;
/*  57 */     int remainder = size % alignment;
/*  58 */     if (remainder > 0) {
/*  59 */       gap = alignment - remainder;
/*  60 */       size += gap;
/*     */     }
/*     */ 
/*  63 */     if (type.getAnnotation(NativeStruct.class) != null) {
/*  64 */       size += getStructSize(type);
/*     */     }
/*  67 */     else if (type.isArray()) {
/*  68 */       Embedded embeddedanno = (Embedded)field.getAnnotation(Embedded.class);
/*     */ 
/*  70 */       if (embeddedanno != null) {
/*  71 */         int numelements = embeddedanno.length();
/*     */ 
/*  73 */         Class arraymembertype = type.getComponentType();
/*  74 */         if (arraymembertype.getAnnotation(NativeStruct.class) != null)
/*  75 */           size += numelements * getStructSize(arraymembertype);
/*     */         else
/*  77 */           size += numelements * getPrimitiveSize(arraymembertype, charset);
/*     */       }
/*     */       else {
/*  80 */         size += com.dukascopy.api.nlink.win32.Util.sizeOfPointer();
/*     */       }
/*  82 */     } else if ((type == String.class) || (type == StringBuffer.class) || (type == StringBuilder.class))
/*     */     {
/*  84 */       Embedded embeddedanno = (Embedded)field.getAnnotation(Embedded.class);
/*     */ 
/*  86 */       if (embeddedanno != null) {
/*  87 */         int length = embeddedanno.length();
/*  88 */         if (charset == Charset.ANSI)
/*  89 */           size += length * com.dukascopy.api.nlink.win32.Util.sizeOfChar();
/*     */         else
/*  91 */           size += length * com.dukascopy.api.nlink.win32.Util.sizeOfWideChar();
/*     */       }
/*     */       else {
/*  94 */         size += com.dukascopy.api.nlink.win32.Util.sizeOfPointer();
/*     */       }
/*  96 */     } else if (type == Callback.class) {
/*  97 */       size += com.dukascopy.api.nlink.win32.Util.sizeOfPointer();
/*     */     } else {
/*  99 */       size += getPrimitiveSize(type, charset);
/*     */     }
/*     */ 
/* 102 */     return size;
/*     */   }
/*     */ 
/*     */   static int getNaturalAlignment(Class<? extends Object> type) {
/* 106 */     return getNaturalAlignment(type, Charset.ANSI);
/*     */   }
/*     */ 
/*     */   static int getNaturalAlignment(Class<? extends Object> type, Charset charset) {
/* 110 */     if (type.isArray())
/* 111 */       return getNaturalAlignment(type.getComponentType(), charset);
/* 112 */     if (type.getAnnotation(NativeStruct.class) != null) {
/* 113 */       NativeStruct structAnno = (NativeStruct)type.getAnnotation(NativeStruct.class);
/*     */ 
/* 115 */       Charset charset2 = Charset.UNICODE;
/* 116 */       if (structAnno != null) {
/* 117 */         charset2 = structAnno.charset();
/*     */       }
/* 119 */       Field[] fields = type.getFields();
/*     */ 
/* 121 */       int maxAlignment = 0;
/* 122 */       int i = 0;
/*     */ 
/* 124 */       Class subtype = fields[i].getType();
/* 125 */       int alnmnt = getNaturalAlignment(subtype, charset2);
/*     */ 
/* 127 */       if (alnmnt > maxAlignment)
/* 128 */         maxAlignment = alnmnt;
/* 129 */       i++;
/* 130 */       if (i >= fields.length) {
/* 131 */         return maxAlignment;
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 136 */     if ((type == StringBuffer.class) || (type == StringBuilder.class)) {
/* 137 */       if (charset == Charset.ANSI) {
/* 138 */         return com.dukascopy.api.nlink.win32.Util.sizeOfChar();
/*     */       }
/* 140 */       return com.dukascopy.api.nlink.win32.Util.sizeOfWideChar();
/*     */     }
/* 142 */     if (type == String.class)
/* 143 */       return com.dukascopy.api.nlink.win32.Util.sizeOfPointer();
/* 144 */     if (Callback.class == type)
/* 145 */       return com.dukascopy.api.nlink.win32.Util.sizeOfPointer();
/* 146 */     if (type == Double.TYPE) {
/* 147 */       if (System.getProperty("os.name").toUpperCase().indexOf("LINUX") != -1) {
/* 148 */         return 4;
/*     */       }
/* 150 */       return 8;
/*     */     }
/*     */ 
/* 153 */     return getPrimitiveSize(type, charset);
/*     */   }
/*     */ 
/*     */   public static void main(String[] args) {
/* 157 */     System.out.println(":util:");
/*     */   }
/*     */ 
/*     */   static int getPrimitiveSize(Class type, Charset charset) {
/* 161 */     int size = 0;
/* 162 */     if (type == Boolean.TYPE)
/* 163 */       size += 4;
/* 164 */     else if (type == Byte.TYPE)
/* 165 */       size++;
/* 166 */     else if (type == Character.TYPE) {
/* 167 */       if (charset == Charset.UNICODE)
/* 168 */         size += 2;
/*     */       else
/* 170 */         size++;
/* 171 */     } else if (type == Short.TYPE)
/* 172 */       size += 2;
/* 173 */     else if (type == Integer.TYPE)
/* 174 */       size += 4;
/* 175 */     else if (type == Long.TYPE)
/* 176 */       size += 8;
/* 177 */     else if (type == Float.TYPE)
/* 178 */       size += 4;
/* 179 */     else if (type == Boolean.class)
/* 180 */       size += 2;
/* 181 */     else if (type == Double.TYPE)
/* 182 */       size += 8;
/* 183 */     else if (type == Callback.class)
/* 184 */       size += 4;
/*     */     else {
/* 186 */       System.out.println("NativeStruct does not support members of type " + type + ".");
/*     */     }
/* 188 */     return size;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Connector-1.1.49.jar
 * Qualified Name:     com.dukascopy.api.nlink.win32.engine.Util
 * JD-Core Version:    0.6.0
 */