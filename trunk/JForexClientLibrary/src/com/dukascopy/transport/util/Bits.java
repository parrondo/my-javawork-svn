/*     */ package com.dukascopy.transport.util;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.OutputStream;
/*     */ import java.lang.reflect.Array;
/*     */ import java.math.BigDecimal;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Iterator;
/*     */ import java.util.LinkedHashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Map.Entry;
/*     */ import java.util.Set;
/*     */ 
/*     */ public class Bits
/*     */ {
/*     */   public static final int EOF_INDEX = -1;
/*     */ 
/*     */   public static byte[] longBytes(long val)
/*     */   {
/*  24 */     byte[] b = new byte[8];
/*  25 */     b[7] = (byte)(int)val;
/*  26 */     b[6] = (byte)(int)(val >>> 8);
/*  27 */     b[5] = (byte)(int)(val >>> 16);
/*  28 */     b[4] = (byte)(int)(val >>> 24);
/*  29 */     b[3] = (byte)(int)(val >>> 32);
/*  30 */     b[2] = (byte)(int)(val >>> 40);
/*  31 */     b[1] = (byte)(int)(val >>> 48);
/*  32 */     b[0] = (byte)(int)(val >>> 56);
/*  33 */     return b;
/*     */   }
/*     */ 
/*     */   public static long getLong(byte[] b) {
/*  37 */     return (b[7] & 0xFF) + ((b[6] & 0xFF) << 8) + ((b[5] & 0xFF) << 16) + ((b[4] & 0xFF) << 24) + ((b[3] & 0xFF) << 32) + ((b[2] & 0xFF) << 40) + ((b[1] & 0xFF) << 48) + (b[0] << 56);
/*     */   }
/*     */ 
/*     */   public static byte booleanBytes(boolean val)
/*     */   {
/*  48 */     return (byte)(val ? 1 : 0);
/*     */   }
/*     */ 
/*     */   public static boolean getBoolean(byte b) {
/*  52 */     return b != 0;
/*     */   }
/*     */ 
/*     */   public static byte[] intBytes(int val) {
/*  56 */     byte[] b = new byte[4];
/*  57 */     b[3] = (byte)val;
/*  58 */     b[2] = (byte)(val >>> 8);
/*  59 */     b[1] = (byte)(val >>> 16);
/*  60 */     b[0] = (byte)(val >>> 24);
/*  61 */     return b;
/*     */   }
/*     */ 
/*     */   public static int getInt(byte[] b) {
/*  65 */     return (b[3] & 0xFF) + ((b[2] & 0xFF) << 8) + ((b[1] & 0xFF) << 16) + (b[0] << 24);
/*     */   }
/*     */ 
/*     */   public static byte[] read(InputStream is, byte[] buff)
/*     */     throws IOException
/*     */   {
/*  72 */     int readBytes = 0;
/*     */     int i;
/*  74 */     while ((readBytes < buff.length) && ((i = is.read(buff, readBytes, buff.length - readBytes)) > -1)) {
/*  75 */       readBytes += i;
/*     */     }
/*  77 */     if (readBytes != buff.length) {
/*  78 */       throw new IOException("Cannot read " + buff.length + " bytes from the stream");
/*     */     }
/*  80 */     return buff;
/*     */   }
/*     */ 
/*     */   public static void writeObject(OutputStream os, Object o) throws IOException {
/*  84 */     if (o == null) {
/*  85 */       os.write(0);
/*     */     } else {
/*  87 */       os.write(1);
/*  88 */       if (o.getClass().equals(String.class)) {
/*  89 */         byte[] bytes = ((String)o).getBytes("UTF-8");
/*  90 */         os.write(intBytes(bytes.length));
/*  91 */         os.write(bytes);
/*  92 */       } else if (o.getClass().equals(BigDecimal.class)) {
/*  93 */         byte[] bytes = o.toString().getBytes("UTF-8");
/*  94 */         os.write(intBytes(bytes.length));
/*  95 */         os.write(bytes);
/*  96 */       } else if (o.getClass().isEnum()) {
/*  97 */         os.write(intBytes(((Enum)o).ordinal()));
/*  98 */       } else if (BitsSerializable.class.isAssignableFrom(o.getClass())) {
/*  99 */         ((BitsSerializable)o).writeObject(os);
/*     */       }
/*     */       else
/*     */       {
/*     */         Iterator i$;
/* 100 */         if (List.class.isAssignableFrom(o.getClass())) {
/* 101 */           List array = (List)o;
/* 102 */           os.write(intBytes(array.size()));
/* 103 */           for (i$ = array.iterator(); i$.hasNext(); ) { Object obj = i$.next();
/* 104 */             writeObject(os, obj);
/*     */           }
/*     */         }
/*     */         else
/*     */         {
/*     */           Iterator i$;
/* 106 */           if (Map.class.isAssignableFrom(o.getClass())) {
/* 107 */             Map map = (Map)o;
/* 108 */             os.write(intBytes(map.size()));
/* 109 */             for (i$ = map.entrySet().iterator(); i$.hasNext(); ) { Object entry = i$.next();
/* 110 */               Object key = ((Map.Entry)entry).getKey();
/* 111 */               Object value = ((Map.Entry)entry).getValue();
/* 112 */               writeObject(os, key);
/* 113 */               writeObject(os, value); }
/*     */           }
/* 115 */           else if (o.getClass().isArray()) {
/* 116 */             os.write(intBytes(((Object[])(Object[])o).length));
/* 117 */             for (Object obj : (Object[])(Object[])o)
/* 118 */               writeObject(os, obj);
/*     */           }
/*     */           else {
/* 121 */             throw new IOException("Unserializable class [" + o.getClass() + "]");
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public static <T> T readObject(InputStream is, Class<T> clazz) throws IOException {
/* 128 */     return readObject(is, clazz, null);
/*     */   }
/*     */ 
/*     */   public static <T, E> T readObject(InputStream is, Class<T> clazz, Class<E> innerClazz) throws IOException
/*     */   {
/* 133 */     return readObject(is, clazz, innerClazz, null);
/*     */   }
/*     */ 
/*     */   public static <T, E, F> T readObject(InputStream is, Class<T> clazz, Class<E> keyClazz, Class<F> valueClazz) throws IOException
/*     */   {
/* 138 */     int nullByte = is.read();
/* 139 */     if (nullByte == 0) {
/* 140 */       return null;
/*     */     }
/* 142 */     assert (nullByte == 1);
/* 143 */     if (clazz.equals(String.class)) {
/* 144 */       int length = getInt(read(is, new byte[4]));
/* 145 */       return new String(read(is, new byte[length]), "UTF-8");
/* 146 */     }if (clazz.equals(BigDecimal.class)) {
/* 147 */       int length = getInt(read(is, new byte[4]));
/* 148 */       return new BigDecimal(new String(read(is, new byte[length]), "UTF-8"));
/* 149 */     }if (clazz.isEnum())
/*     */       try {
/* 151 */         return ((Object[])(Object[])clazz.getMethod("values", new Class[0]).invoke(null, new Object[0]))[getInt(read(is, new byte[4]))];
/*     */       } catch (Exception e) {
/* 153 */         throw new IOException(e.getMessage(), e);
/*     */       }
/* 155 */     if (BitsSerializable.class.isAssignableFrom(clazz))
/*     */       try {
/* 157 */         BitsSerializable obj = (BitsSerializable)clazz.newInstance();
/* 158 */         obj.readObject(is);
/* 159 */         return obj;
/*     */       } catch (Exception e) {
/* 161 */         throw new IOException(e.getMessage(), e);
/*     */       }
/* 163 */     if (clazz.equals(ArrayList.class)) {
/* 164 */       int size = getInt(read(is, new byte[4]));
/* 165 */       ArrayList array = new ArrayList(size);
/* 166 */       for (int i = 0; i < size; i++) {
/* 167 */         Object o = readObject(is, keyClazz);
/* 168 */         array.add(o);
/*     */       }
/* 170 */       return array;
/* 171 */     }if (clazz.equals(LinkedHashMap.class)) {
/* 172 */       int size = getInt(read(is, new byte[4]));
/* 173 */       LinkedHashMap map = new LinkedHashMap();
/* 174 */       for (int i = 0; i < size; i++) {
/* 175 */         Object key = readObject(is, keyClazz);
/* 176 */         Object value = readObject(is, valueClazz);
/* 177 */         map.put(key, value);
/*     */       }
/* 179 */       return map;
/* 180 */     }if (clazz.isArray()) {
/* 181 */       int size = getInt(read(is, new byte[4]));
/* 182 */       Class componentClazz = clazz.getComponentType();
/* 183 */       Object array = Array.newInstance(componentClazz, size);
/* 184 */       for (int i = 0; i < size; i++) {
/* 185 */         Object obj = readObject(is, componentClazz);
/* 186 */         Array.set(array, i, obj);
/*     */       }
/* 188 */       return array;
/*     */     }
/* 190 */     throw new IOException("Unserializable class [" + clazz + "]");
/*     */   }
/*     */ 
/*     */   public static abstract interface BitsSerializable
/*     */   {
/*     */     public abstract void writeObject(OutputStream paramOutputStream)
/*     */       throws IOException;
/*     */ 
/*     */     public abstract void readObject(InputStream paramInputStream)
/*     */       throws IOException;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\dds2-common-2.3.77.jar
 * Qualified Name:     com.dukascopy.transport.util.Bits
 * JD-Core Version:    0.6.0
 */