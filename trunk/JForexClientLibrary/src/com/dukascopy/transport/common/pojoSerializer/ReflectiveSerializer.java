/*     */ package com.dukascopy.transport.common.pojoSerializer;
/*     */ 
/*     */ import java.io.ByteArrayInputStream;
/*     */ import java.io.ByteArrayOutputStream;
/*     */ import java.io.DataInputStream;
/*     */ import java.io.DataOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.PrintStream;
/*     */ import java.lang.reflect.Array;
/*     */ import java.lang.reflect.Constructor;
/*     */ import java.lang.reflect.Field;
/*     */ import java.lang.reflect.InvocationTargetException;
/*     */ import java.lang.reflect.Method;
/*     */ import java.lang.reflect.Modifier;
/*     */ import java.math.BigDecimal;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collection;
/*     */ import java.util.HashMap;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Map.Entry;
/*     */ import java.util.Set;
/*     */ 
/*     */ public class ReflectiveSerializer
/*     */ {
/*  41 */   private static Map<String, Map<String, ObjectFieldDescriptor>> cache = new HashMap();
/*     */ 
/*     */   public static byte[] encode(Object object)
/*     */   {
/*  50 */     ByteArrayOutputStream baos = new ByteArrayOutputStream();
/*     */     try
/*     */     {
/*  57 */       DataOutputStream dos = new DataOutputStream(baos);
/*  58 */       if (object == null) {
/*  59 */         dos.writeInt(4);
/*  60 */         dos.writeBytes("null");
/*  61 */         dos.flush();
/*  62 */         return baos.toByteArray();
/*     */       }
/*  64 */       Class objectClass = object.getClass();
/*  65 */       String name = Dictionary.encodeName(objectClass);
/*  66 */       int len = name.getBytes().length;
/*  67 */       dos.writeInt(len);
/*  68 */       dos.writeBytes(name);
/*  69 */       if ((objectClass == Integer.TYPE) || (objectClass == Integer.class))
/*     */       {
/*  71 */         dos.writeInt(((Integer)object).intValue());
/*  72 */         dos.flush();
/*  73 */         return baos.toByteArray();
/*  74 */       }if ((objectClass == Short.class) || (objectClass == Short.TYPE)) {
/*  75 */         dos.writeShort(((Short)object).shortValue());
/*  76 */         dos.flush();
/*  77 */         return baos.toByteArray();
/*  78 */       }if ((objectClass == Long.class) || (objectClass == Long.TYPE)) {
/*  79 */         dos.writeLong(((Long)object).longValue());
/*  80 */         dos.flush();
/*  81 */         return baos.toByteArray();
/*  82 */       }if ((objectClass == Boolean.class) || (objectClass == Boolean.TYPE)) {
/*  83 */         dos.writeBoolean(((Boolean)object).booleanValue());
/*  84 */         dos.flush();
/*  85 */         return baos.toByteArray();
/*  86 */       }if ((objectClass == Float.class) || (objectClass == Float.TYPE)) {
/*  87 */         dos.writeFloat(((Float)object).floatValue());
/*  88 */         dos.flush();
/*  89 */         return baos.toByteArray();
/*  90 */       }if ((objectClass == Double.class) || (objectClass == Double.TYPE)) {
/*  91 */         dos.writeDouble(((Double)object).doubleValue());
/*  92 */         dos.flush();
/*  93 */         return baos.toByteArray();
/*  94 */       }if ((objectClass == Character.class) || (objectClass == Character.TYPE)) {
/*  95 */         dos.writeChar(((Character)object).charValue());
/*  96 */         dos.flush();
/*  97 */         return baos.toByteArray();
/*  98 */       }if ((objectClass == Byte.class) || (objectClass == Byte.TYPE)) {
/*  99 */         dos.writeByte(((Byte)object).byteValue());
/* 100 */         dos.flush();
/* 101 */         return baos.toByteArray();
/* 102 */       }if (objectClass == String.class) {
/* 103 */         dos.write(((String)object).getBytes());
/* 104 */         dos.flush();
/* 105 */         return baos.toByteArray();
/* 106 */       }if (objectClass == BigDecimal.class) {
/* 107 */         dos.writeBytes(((BigDecimal)object).toPlainString());
/* 108 */         dos.flush();
/* 109 */         return baos.toByteArray();
/* 110 */       }if (objectClass.isArray()) {
/* 111 */         Object[] objs = (Object[])(Object[])object;
/* 112 */         for (Object o : objs) {
/* 113 */           byte[] encoded = encode(o);
/* 114 */           dos.writeInt(encoded.length);
/* 115 */           dos.write(encoded);
/*     */         }
/* 117 */         dos.flush();
/* 118 */         return baos.toByteArray();
/* 119 */       }if (Collection.class.isAssignableFrom(objectClass)) {
/* 120 */         Collection collection = (Collection)object;
/* 121 */         Iterator i = collection.iterator();
/* 122 */         while (i.hasNext()) {
/* 123 */           Object o = i.next();
/* 124 */           byte[] encoded = encode(o);
/* 125 */           dos.writeInt(encoded.length);
/* 126 */           dos.write(encoded);
/*     */         }
/* 128 */         dos.flush();
/* 129 */         return baos.toByteArray();
/* 130 */       }if (Map.class.isAssignableFrom(objectClass)) {
/* 131 */         Map map = (Map)object;
/* 132 */         Iterator i = map.entrySet().iterator();
/* 133 */         while (i.hasNext()) {
/* 134 */           Map.Entry o = (Map.Entry)i.next();
/*     */ 
/* 137 */           byte[] encodedKey = encode(o.getKey());
/* 138 */           byte[] encodedValue = encode(o.getValue());
/*     */ 
/* 140 */           String bytes = "";
/* 141 */           for (byte b : encodedValue) {
/* 142 */             bytes = bytes + b + ", ";
/*     */           }
/*     */ 
/* 145 */           dos.writeInt(encodedKey.length);
/* 146 */           dos.write(encodedKey);
/* 147 */           dos.writeInt(encodedValue.length);
/* 148 */           dos.write(encodedValue);
/*     */         }
/* 150 */         dos.flush();
/* 151 */         return baos.toByteArray();
/*     */       }
/*     */ 
/* 154 */       Map map = getObjectDecriptorMap(object.getClass());
/*     */ 
/* 156 */       List dataList = new ArrayList();
/* 157 */       for (String field : map.keySet()) {
/* 158 */         ObjectFieldDescriptor ofd = (ObjectFieldDescriptor)map.get(field);
/* 159 */         Method getter = ofd.getGetter();
/*     */         try
/*     */         {
/* 162 */           Object result = getter.invoke(object, new Object[0]);
/*     */ 
/* 166 */           if (result != null) {
/* 167 */             FieldData fd = new FieldData(field, result);
/*     */ 
/* 169 */             dataList.add(fd);
/*     */           }
/*     */         }
/*     */         catch (IllegalArgumentException e)
/*     */         {
/* 174 */           e.printStackTrace();
/*     */         }
/*     */         catch (IllegalAccessException e) {
/* 177 */           e.printStackTrace();
/*     */         }
/*     */         catch (InvocationTargetException e) {
/* 180 */           e.printStackTrace();
/*     */         }
/*     */       }
/* 183 */       for (FieldData fd : dataList) {
/* 184 */         int namelen = fd.getFieldName().getBytes().length;
/* 185 */         byte[] encodedVal = encode(fd.getValue());
/* 186 */         dos.writeInt(namelen);
/* 187 */         dos.writeBytes(fd.getFieldName());
/* 188 */         dos.writeInt(encodedVal.length);
/* 189 */         dos.write(encodedVal);
/*     */       }
/* 191 */       dos.flush();
/*     */     }
/*     */     catch (IOException e) {
/* 194 */       e.printStackTrace();
/*     */     }
/* 196 */     return baos.toByteArray();
/*     */   }
/*     */ 
/*     */   public static Object decode(byte[] data)
/*     */   {
/* 204 */     Object result = null;
/* 205 */     Class objectClass = null;
/* 206 */     List dataList = new ArrayList();
/*     */     try {
/* 208 */       ByteArrayInputStream bais = new ByteArrayInputStream(data);
/* 209 */       DataInputStream ois = new DataInputStream(bais);
/*     */ 
/* 213 */       int len = ois.readInt();
/*     */ 
/* 215 */       byte[] namebytes = new byte[len];
/* 216 */       ois.readFully(namebytes);
/* 217 */       String className = new String(namebytes);
/* 218 */       if (className.equals("null")) {
/* 219 */         return null;
/*     */       }
/* 221 */       objectClass = Dictionary.decodeName(className);
/*     */ 
/* 224 */       if ((objectClass == Integer.TYPE) || (objectClass == Integer.class))
/* 225 */         return Integer.valueOf(ois.readInt());
/* 226 */       if ((objectClass == Long.class) || (objectClass == Long.TYPE))
/* 227 */         return Long.valueOf(ois.readLong());
/* 228 */       if ((objectClass == Boolean.class) || (objectClass == Boolean.TYPE))
/* 229 */         return Boolean.valueOf(ois.readBoolean());
/* 230 */       if ((objectClass == Float.class) || (objectClass == Float.TYPE))
/* 231 */         return Float.valueOf(ois.readFloat());
/* 232 */       if ((objectClass == Short.class) || (objectClass == Short.TYPE))
/* 233 */         return Short.valueOf(ois.readShort());
/* 234 */       if ((objectClass == Double.class) || (objectClass == Double.TYPE))
/* 235 */         return Double.valueOf(ois.readDouble());
/* 236 */       if ((objectClass == Character.class) || (objectClass == Character.TYPE))
/* 237 */         return Character.valueOf(ois.readChar());
/* 238 */       if ((objectClass == Byte.class) || (objectClass == Byte.TYPE))
/* 239 */         return Byte.valueOf(ois.readByte());
/* 240 */       if (objectClass == String.class) {
/* 241 */         int slen = ois.available();
/* 242 */         byte[] bytes = new byte[slen];
/* 243 */         ois.readFully(bytes);
/* 244 */         return new String(bytes);
/* 245 */       }if (objectClass == BigDecimal.class) {
/* 246 */         int slen = ois.available();
/* 247 */         byte[] bytes = new byte[slen];
/* 248 */         ois.readFully(bytes);
/* 249 */         return new BigDecimal(new String(bytes));
/* 250 */       }if (objectClass.isArray())
/*     */       {
/* 252 */         List list = new ArrayList();
/* 253 */         Class ctype = Object.class;
/* 254 */         int slen = ois.available();
/* 255 */         while (slen > 0)
/*     */         {
/* 257 */           int olen = ois.readInt();
/* 258 */           byte[] bytes = new byte[olen];
/* 259 */           ois.readFully(bytes);
/* 260 */           Object val = decode(bytes);
/* 261 */           ctype = val.getClass();
/* 262 */           list.add(val);
/* 263 */           slen = ois.available();
/*     */         }
/* 265 */         Object[] array = null;
/* 266 */         array = (Object[])(Object[])Array.newInstance(ctype, list.size());
/* 267 */         for (int i = 0; i < array.length; i++) {
/* 268 */           array[i] = list.get(i);
/*     */         }
/* 270 */         return array;
/* 271 */       }if (Collection.class.isAssignableFrom(objectClass))
/*     */       {
/* 275 */         Collection list = (Collection)objectClass.newInstance();
/* 276 */         int slen = ois.available();
/* 277 */         while (slen > 0)
/*     */         {
/* 279 */           int olen = ois.readInt();
/* 280 */           byte[] bytes = new byte[olen];
/* 281 */           ois.readFully(bytes);
/* 282 */           list.add(decode(bytes));
/* 283 */           slen = ois.available();
/*     */         }
/* 285 */         return list;
/* 286 */       }if (Map.class.isAssignableFrom(objectClass))
/*     */       {
/* 290 */         Map map = (Map)objectClass.newInstance();
/* 291 */         int slen = ois.available();
/* 292 */         while (slen > 0)
/*     */         {
/* 294 */           int keylen = ois.readInt();
/* 295 */           byte[] keybytes = new byte[keylen];
/* 296 */           ois.readFully(keybytes);
/* 297 */           int vallen = ois.readInt();
/* 298 */           byte[] valbytes = new byte[vallen];
/*     */ 
/* 300 */           ois.readFully(valbytes);
/* 301 */           String bytes = "";
/* 302 */           for (byte b : valbytes) {
/* 303 */             bytes = bytes + b + ", ";
/*     */           }
/*     */ 
/* 306 */           map.put(decode(keybytes), decode(valbytes));
/* 307 */           slen = ois.available();
/*     */         }
/* 309 */         return map;
/*     */       }
/*     */ 
/* 312 */       while (ois.available() > 0) {
/* 313 */         int lenFname = ois.readInt();
/* 314 */         byte[] b = new byte[lenFname];
/* 315 */         ois.readFully(b);
/* 316 */         String fname = new String(b);
/* 317 */         int valLen = ois.readInt();
/* 318 */         Object value = null;
/*     */ 
/* 320 */         byte[] v = new byte[valLen];
/* 321 */         ois.readFully(v);
/*     */ 
/* 323 */         value = decode(v);
/*     */ 
/* 325 */         FieldData fd = new FieldData(fname, value);
/* 326 */         dataList.add(fd);
/*     */       }
/*     */     }
/*     */     catch (IOException e) {
/* 330 */       e.printStackTrace();
/*     */     }
/*     */     catch (InstantiationException e) {
/* 333 */       e.printStackTrace();
/*     */     }
/*     */     catch (IllegalAccessException e) {
/* 336 */       e.printStackTrace();
/*     */     }
/*     */ 
/* 339 */     Map map = getObjectDecriptorMap(objectClass);
/*     */     try
/*     */     {
/* 342 */       result = objectClass.newInstance();
/*     */     } catch (InstantiationException e) {
/* 344 */       System.out.println(objectClass.getCanonicalName());
/* 345 */       e.printStackTrace();
/*     */     } catch (IllegalAccessException e) {
/* 347 */       System.out.println(objectClass.getCanonicalName());
/* 348 */       e.printStackTrace();
/*     */     }
/* 350 */     for (FieldData fd : dataList) {
/* 351 */       ObjectFieldDescriptor ofd = (ObjectFieldDescriptor)map.get(fd.getFieldName());
/* 352 */       if (ofd != null) {
/* 353 */         Method setter = ofd.getSetter();
/*     */         try {
/* 355 */           if (fd.getValue() != null) {
/* 356 */             if (ofd.getParameterType().isAssignableFrom(fd.getValue().getClass()))
/* 357 */               setter.invoke(result, new Object[] { fd.getValue() });
/* 358 */             else if ((ofd.getParameterType().isPrimitive()) && (fd.getValue() != null) && (fd.getValue() != null) && (comparePrimitive(ofd.getParameterType(), fd.getValue().getClass())))
/*     */             {
/* 360 */               setter.invoke(result, new Object[] { fd.getValue() });
/*     */             }
/*     */ 
/*     */           }
/*     */ 
/*     */         }
/*     */         catch (IllegalArgumentException e)
/*     */         {
/* 368 */           e.printStackTrace();
/*     */         }
/*     */         catch (IllegalAccessException e) {
/* 371 */           e.printStackTrace();
/*     */         }
/*     */         catch (InvocationTargetException e) {
/* 374 */           e.printStackTrace();
/*     */         }
/*     */       }
/*     */     }
/* 378 */     return result;
/*     */   }
/*     */ 
/*     */   private static boolean comparePrimitive(Class primitive, Class objectClass) {
/* 382 */     if ((primitive == Boolean.TYPE) && (objectClass == Boolean.class)) {
/* 383 */       return true;
/*     */     }
/* 385 */     if ((primitive == Integer.TYPE) && (objectClass == Integer.class)) {
/* 386 */       return true;
/*     */     }
/* 388 */     if ((primitive == Long.TYPE) && (objectClass == Long.class)) {
/* 389 */       return true;
/*     */     }
/* 391 */     if ((primitive == Float.TYPE) && (objectClass == Float.class)) {
/* 392 */       return true;
/*     */     }
/* 394 */     if ((primitive == Double.TYPE) && (objectClass == Double.class)) {
/* 395 */       return true;
/*     */     }
/* 397 */     if ((primitive == Character.TYPE) && (objectClass == Character.class)) {
/* 398 */       return true;
/*     */     }
/* 400 */     if ((primitive == Byte.TYPE) && (objectClass == Byte.class)) {
/* 401 */       return true;
/*     */     }
/* 403 */     if ((primitive == Short.TYPE) && (objectClass == Short.class)) {
/* 404 */       return true;
/*     */     }
/*     */ 
/* 407 */     return (primitive == Void.TYPE) && (objectClass == Void.class);
/*     */   }
/*     */ 
/*     */   private static Map<String, ObjectFieldDescriptor> getObjectDecriptorMap(Class objectClass)
/*     */   {
/* 414 */     Map map = (Map)cache.get(objectClass.getCanonicalName());
/* 415 */     if (map == null) {
/* 416 */       map = new HashMap();
/* 417 */       Class cl = objectClass;
/* 418 */       while (cl != Object.class) {
/* 419 */         Field[] flist = cl.getDeclaredFields();
/* 420 */         for (Field f : flist) {
/* 421 */           ObjectFieldDescriptor ofd = getFieldDecriptor(f, cl);
/* 422 */           if (ofd == null)
/*     */             continue;
/* 424 */           if (!map.containsKey(f.getName())) {
/* 425 */             map.put(f.getName(), ofd);
/*     */           }
/*     */ 
/*     */         }
/*     */ 
/* 430 */         cl = cl.getSuperclass();
/*     */       }
/*     */ 
/* 433 */       cache.put(objectClass.getCanonicalName(), map);
/*     */     }
/* 435 */     return map;
/*     */   }
/*     */ 
/*     */   private static ObjectFieldDescriptor getFieldDecriptor(Field field, Class objectClass) {
/* 439 */     if (Modifier.isTransient(field.getModifiers())) {
/* 440 */       return null;
/*     */     }
/* 442 */     ObjectFieldDescriptor ofd = null;
/* 443 */     String setterName = "set" + field.getName().substring(0, 1).toUpperCase() + field.getName().substring(1, field.getName().length());
/* 444 */     String getterName = "get" + field.getName().substring(0, 1).toUpperCase() + field.getName().substring(1, field.getName().length());
/* 445 */     Method setter = null;
/* 446 */     Method getter = null;
/* 447 */     Constructor c = null;
/* 448 */     Class fieldType = field.getType();
/*     */     try {
/* 450 */       c = objectClass.getConstructor(new Class[0]);
/* 451 */       setter = objectClass.getMethod(setterName, new Class[] { fieldType });
/*     */     }
/*     */     catch (SecurityException e) {
/*     */     }
/*     */     catch (NoSuchMethodException e) {
/*     */     }
/* 457 */     if ((setter == null) || (c == null)) {
/* 458 */       return null;
/*     */     }
/* 460 */     if ((setter.getReturnType() != Void.TYPE) || (setter.getModifiers() != 1))
/* 461 */       return null;
/*     */     try
/*     */     {
/* 464 */       getter = objectClass.getMethod(getterName, new Class[0]);
/*     */     }
/*     */     catch (SecurityException e) {
/*     */     }
/*     */     catch (NoSuchMethodException e) {
/*     */     }
/* 470 */     if ((getter == null) && (fieldType == Boolean.TYPE))
/* 471 */       getterName = "is" + field.getName().substring(0, 1).toUpperCase() + field.getName().substring(1, field.getName().length());
/*     */     try
/*     */     {
/* 474 */       getter = objectClass.getMethod(getterName, new Class[0]);
/*     */     }
/*     */     catch (SecurityException e) {
/*     */     }
/*     */     catch (NoSuchMethodException e) {
/*     */     }
/* 480 */     if (getter == null) {
/* 481 */       return null;
/*     */     }
/*     */ 
/* 486 */     ofd = new ObjectFieldDescriptor();
/* 487 */     ofd.setFieldName(field.getName());
/* 488 */     ofd.setParameterType(fieldType);
/* 489 */     ofd.setResultType(fieldType);
/* 490 */     ofd.setGetter(getter);
/* 491 */     ofd.setSetter(setter);
/* 492 */     return ofd;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\dds2-common-2.3.77.jar
 * Qualified Name:     com.dukascopy.transport.common.pojoSerializer.ReflectiveSerializer
 * JD-Core Version:    0.6.0
 */