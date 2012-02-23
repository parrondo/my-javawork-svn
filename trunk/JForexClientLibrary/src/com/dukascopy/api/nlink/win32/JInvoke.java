/*     */ package com.dukascopy.api.nlink.win32;
/*     */ 
/*     */ import com.dukascopy.api.nlink.win32.engine.MethodInfo;
/*     */ import java.io.BufferedInputStream;
/*     */ import java.io.BufferedOutputStream;
/*     */ import java.io.File;
/*     */ import java.io.FileOutputStream;
/*     */ import java.io.InputStream;
/*     */ import java.io.PrintStream;
/*     */ import java.lang.annotation.Annotation;
/*     */ import java.lang.reflect.Method;
/*     */ import java.net.URISyntaxException;
/*     */ import java.net.URL;
/*     */ import java.security.CodeSource;
/*     */ import java.security.ProtectionDomain;
/*     */ 
/*     */ public class JInvoke
/*     */ {
/* 400 */   private static boolean libloaded = false;
/*     */ 
/*     */   public static void initialize()
/*     */   {
/*  22 */     if (!libloaded)
/*     */     {
/*  24 */       loadLib();
/*     */ 
/*  26 */       libloaded = true;
/*     */     }
/*  28 */     Throwable t = new Throwable();
/*  29 */     StackTraceElement[] ste = t.getStackTrace();
/*  30 */     String classname = ste[1].getClassName();
/*     */     try
/*     */     {
/*  33 */       Class clazz = Class.forName(classname);
/*  34 */       NativeImport nativeannoclass = (NativeImport)clazz.getAnnotation(NativeImport.class);
/*  35 */       String library = "";
/*  36 */       CallingConvention cc = CallingConvention.STDCALL;
/*  37 */       Charset charset = Charset.UNICODE;
/*  38 */       boolean isAnnotated = false;
/*  39 */       String globallib = null;
/*  40 */       if (nativeannoclass != null)
/*     */       {
/*  42 */         globallib = library = nativeannoclass.library();
/*  43 */         cc = nativeannoclass.convention();
/*  44 */         charset = nativeannoclass.charset();
/*  45 */         isAnnotated = true;
/*     */       }
/*  47 */       Method[] methods = clazz.getDeclaredMethods();
/*  48 */       Method[] amethod = methods;
/*  49 */       int i = 0;
/*  50 */       for (int j = amethod.length; i < j; i++)
/*     */       {
/*  52 */         Method method = amethod[i];
/*  53 */         if ((method.getModifiers() & 0x100) == 0)
/*     */           continue;
/*  55 */         NativeImport nativeanno = (NativeImport)method.getAnnotation(NativeImport.class);
/*  56 */         String methodname = method.getName();
/*  57 */         MethodInfo mi = getMethodInfo(method);
/*  58 */         String function = null;
/*  59 */         if (nativeanno != null)
/*     */         {
/*  61 */           library = nativeanno.library();
/*  62 */           function = nativeanno.function();
/*  63 */           cc = nativeanno.convention();
/*  64 */           charset = nativeanno.charset();
/*  65 */           isAnnotated = true;
/*     */         }
/*  67 */         if ((library == null) || (library.length() == 0))
/*  68 */           library = globallib == null ? clazz.getSimpleName() : globallib;
/*  69 */         if ((function == null) || (function.length() == 0))
/*  70 */           function = method.getName();
/*  71 */         if (charset == Charset.AUTO) {
/*  72 */           charset = Charset.UNICODE;
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/*     */     }
/*     */     catch (ClassNotFoundException e)
/*     */     {
/*  81 */       e.printStackTrace();
/*     */     }
/*     */   }
/*     */ 
/*     */   private static synchronized native void InitMethod(Class paramClass, String paramString1, String paramString2, String paramString3, String paramString4, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int[] paramArrayOfInt);
/*     */ 
/*     */   static MethodInfo getMethodInfo(Method method)
/*     */   {
/*  90 */     StringBuffer signature = new StringBuffer();
/*  91 */     int jargsize = 8;
/*  92 */     int nargsize = 0;
/*  93 */     Class[] paramclass = method.getParameterTypes();
/*  94 */     Annotation[][] paramannos = method.getParameterAnnotations();
/*  95 */     int numparams = paramclass.length;
/*  96 */     int[] argtype = new int[numparams + 1];
/*  97 */     signature.append("(");
/*  98 */     for (int i = 0; i < numparams; i++) {
/*  99 */       if (paramclass[i] == Boolean.TYPE)
/*     */       {
/* 101 */         signature.append("Z");
/* 102 */         jargsize += 4;
/* 103 */         nargsize += 4;
/* 104 */         argtype[i] = 1;
/*     */       }
/* 106 */       else if (paramclass[i] == Boolean.class)
/*     */       {
/* 108 */         signature.append("Ljava/lang/Boolean;");
/* 109 */         jargsize += 4;
/* 110 */         nargsize += 4;
/* 111 */         argtype[i] = 2;
/*     */       }
/* 113 */       else if (paramclass[i] == Byte.TYPE)
/*     */       {
/* 115 */         signature.append("B");
/* 116 */         jargsize += 4;
/* 117 */         nargsize += 4;
/* 118 */         argtype[i] = 3;
/*     */       }
/* 120 */       else if (paramclass[i] == Character.TYPE)
/*     */       {
/* 122 */         signature.append("C");
/* 123 */         jargsize += 4;
/* 124 */         nargsize += 4;
/* 125 */         argtype[i] = 4;
/*     */       }
/* 127 */       else if (paramclass[i] == Short.TYPE)
/*     */       {
/* 129 */         signature.append("S");
/* 130 */         jargsize += 4;
/* 131 */         nargsize += 4;
/* 132 */         argtype[i] = 5;
/*     */       }
/* 134 */       else if (paramclass[i] == Integer.TYPE)
/*     */       {
/* 136 */         signature.append("I");
/* 137 */         jargsize += 4;
/* 138 */         nargsize += 4;
/* 139 */         argtype[i] = 6;
/*     */       }
/* 141 */       else if (paramclass[i] == Long.TYPE)
/*     */       {
/* 143 */         signature.append("J");
/* 144 */         jargsize += 8;
/* 145 */         nargsize += 8;
/* 146 */         argtype[i] = 7;
/*     */       }
/* 148 */       else if (paramclass[i] == Float.TYPE)
/*     */       {
/* 150 */         signature.append("F");
/* 151 */         jargsize += 4;
/* 152 */         nargsize += 4;
/* 153 */         argtype[i] = 9;
/*     */       }
/* 155 */       else if (paramclass[i] == Double.TYPE)
/*     */       {
/* 157 */         signature.append("D");
/* 158 */         jargsize += 8;
/* 159 */         nargsize += 8;
/* 160 */         argtype[i] = 10;
/*     */       }
/* 162 */       else if (paramclass[i] == Boolean.class)
/*     */       {
/* 164 */         signature.append("[Ljava/lang/Boolean;");
/* 165 */         jargsize += 4;
/* 166 */         nargsize += 4;
/* 167 */         argtype[i] = 4098;
/*     */       }
/* 169 */       else if ((paramclass[i].isArray()) && (paramclass[i].getComponentType() != null) && (paramclass[i].getComponentType().getAnnotation(NativeStruct.class) != null))
/*     */       {
/* 171 */         signature.append("[L");
/* 172 */         signature.append(paramclass[i].getComponentType().getName().replace('.', '/'));
/* 173 */         signature.append(";");
/* 174 */         jargsize += 4;
/* 175 */         nargsize += 4;
/* 176 */         argtype[i] = 4110;
/*     */       }
/* 178 */       else if (paramclass[i] == String.class)
/*     */       {
/* 180 */         signature.append("Ljava/lang/String;");
/* 181 */         jargsize += 4;
/* 182 */         nargsize += 4;
/* 183 */         argtype[i] = 11;
/*     */       }
/* 185 */       else if (paramclass[i] == StringBuffer.class)
/*     */       {
/* 187 */         signature.append("Ljava/lang/StringBuffer;");
/* 188 */         jargsize += 4;
/* 189 */         nargsize += 4;
/* 190 */         argtype[i] = 12;
/*     */       }
/* 192 */       else if (paramclass[i] == StringBuilder.class)
/*     */       {
/* 194 */         signature.append("Ljava/lang/StringBuilder;");
/* 195 */         jargsize += 4;
/* 196 */         nargsize += 4;
/* 197 */         argtype[i] = 13;
/*     */       }
/* 199 */       else if (paramclass[i].getAnnotation(NativeStruct.class) != null)
/*     */       {
/* 201 */         signature.append("L");
/* 202 */         signature.append(paramclass[i].getName().replace('.', '/'));
/* 203 */         signature.append(";");
/* 204 */         jargsize += 4;
/* 205 */         if ((paramannos[i].length != 0) && (paramannos[i][0].annotationType().equals(ByVal.class)))
/*     */         {
/* 207 */           argtype[i] = 17;
/* 208 */           nargsize += Util.getStructSize(paramclass[i]);
/*     */         }
/*     */         else {
/* 211 */           argtype[i] = 14;
/* 212 */           nargsize += 4;
/*     */         }
/*     */       }
/* 215 */       else if (paramclass[i] == Callback.class)
/*     */       {
/* 217 */         signature.append("Lnlink.win32.Callback;");
/* 218 */         jargsize += 4;
/* 219 */         nargsize += 4;
/* 220 */         argtype[i] = 16;
/*     */       }
/*     */       else {
/* 223 */         throw new RuntimeException("Method " + method + " has parameters of unsupported type: " + paramclass[i].getName());
/*     */       }
/*     */     }
/* 226 */     signature.append(")");
/* 227 */     Class returnType = method.getReturnType();
/* 228 */     if (returnType == Void.TYPE)
/*     */     {
/* 230 */       signature.append("V");
/* 231 */       argtype[numparams] = 0;
/*     */     }
/* 233 */     else if (returnType == Boolean.TYPE)
/*     */     {
/* 235 */       signature.append("Z");
/* 236 */       argtype[numparams] = 1;
/*     */     }
/* 238 */     else if (returnType.getClass().equals(Boolean.class))
/*     */     {
/* 240 */       signature.append("Ljava/lang/Boolean;");
/* 241 */       argtype[numparams] = 2;
/*     */     }
/* 243 */     else if (returnType == Byte.TYPE)
/*     */     {
/* 245 */       signature.append("B");
/* 246 */       argtype[numparams] = 3;
/*     */     }
/* 248 */     else if (returnType == Character.TYPE)
/*     */     {
/* 250 */       signature.append("C");
/* 251 */       argtype[numparams] = 4;
/*     */     }
/* 253 */     else if (returnType == Short.TYPE)
/*     */     {
/* 255 */       signature.append("S");
/* 256 */       argtype[numparams] = 5;
/*     */     }
/* 258 */     else if (returnType == Integer.TYPE)
/*     */     {
/* 260 */       signature.append("I");
/* 261 */       argtype[numparams] = 6;
/*     */     }
/* 263 */     else if (returnType == Long.TYPE)
/*     */     {
/* 265 */       signature.append("J");
/* 266 */       argtype[numparams] = 7;
/*     */     }
/* 268 */     else if (returnType == Float.TYPE)
/*     */     {
/* 270 */       signature.append("F");
/* 271 */       argtype[numparams] = 9;
/*     */     }
/* 273 */     else if (returnType == Double.TYPE)
/*     */     {
/* 275 */       signature.append("D");
/* 276 */       argtype[numparams] = 10;
/*     */     }
/* 278 */     else if (returnType == Boolean.class) {
/* 279 */       signature.append("[Ljava/lang/Boolean;");
/*     */     }
/* 281 */     else if (returnType == String.class)
/*     */     {
/* 283 */       signature.append("Ljava/lang/String;");
/* 284 */       argtype[numparams] = 11;
/*     */     }
/* 286 */     else if (returnType.getAnnotation(NativeStruct.class) != null)
/*     */     {
/* 288 */       signature.append("L");
/* 289 */       signature.append(returnType.getName().replace('.', '/'));
/* 290 */       signature.append(";");
/* 291 */       argtype[numparams] = 14;
/*     */     }
/*     */     else {
/* 294 */       throw new RuntimeException("Method " + method + " has unsupported return type: " + returnType.getName());
/*     */     }
/* 296 */     return new MethodInfo(signature.toString(), jargsize, nargsize, argtype);
/*     */   }
/*     */ 
/*     */   private static native void init();
/*     */ 
/*     */   static synchronized void loadNativeLib() {
/* 303 */     if (!libloaded)
/*     */     {
/* 305 */       loadLib();
/* 306 */       init();
/* 307 */       libloaded = true;
/*     */     }
/*     */   }
/*     */ 
/*     */   private static synchronized void loadLib()
/*     */   {
/* 313 */     String libname = "nlink";
/* 314 */     boolean loaded = false;
/*     */ 
/* 333 */     if (!loaded)
/*     */       try
/*     */       {
/* 336 */         System.loadLibrary(libname);
/*     */       }
/*     */       catch (Throwable t)
/*     */       {
/* 340 */         File jarFile = null;
/* 341 */         String dllpath = null;
/*     */         try
/*     */         {
/* 344 */           jarFile = new File(JInvoke.class.getProtectionDomain().getCodeSource().getLocation().toURI());
/* 345 */           dllpath = String.valueOf(jarFile.getParent()) + File.separator + System.mapLibraryName(libname);
/*     */         }
/*     */         catch (URISyntaxException urise)
/*     */         {
/* 349 */           String jarFileLoc = JInvoke.class.getProtectionDomain().getCodeSource().getLocation().getPath();
/* 350 */           jarFileLoc = jarFileLoc.substring(1, jarFileLoc.lastIndexOf(47));
/* 351 */           dllpath = String.valueOf(jarFileLoc) + File.separator + System.mapLibraryName(libname);
/*     */         }
/*     */         try
/*     */         {
/* 355 */           System.load(dllpath);
/*     */         }
/*     */         catch (Throwable tt)
/*     */         {
/* 359 */           extractNativeLib(dllpath);
/* 360 */           System.load(dllpath);
/*     */         }
/*     */       }
/*     */   }
/*     */ 
/*     */   private static void extractNativeLib(String dllpath)
/*     */   {
/*     */     try
/*     */     {
/* 369 */       String osname = System.getProperty("os.name");
/* 370 */       String osarch = System.getProperty("os.arch");
/* 371 */       String jinvokelib = "/native/" + System.mapLibraryName("jinvoke");
/* 372 */       if (osname.equals("SunOS"))
/* 373 */         if (osarch.equals("x86")) {
/* 374 */           jinvokelib = "/native/" + osname + "/" + osarch + "/" + System.mapLibraryName("jinvoke");
/*     */         }
/* 376 */         else if (osarch.equals("sparc"))
/* 377 */           jinvokelib = "/native/" + osname + "/" + osarch + "/" + System.mapLibraryName("jinvoke");
/*     */         else
/* 379 */           System.out.println("SunOS on " + osarch + " architecture is currently not supported by J/Invoke. Contact support@jinvoke.com to request support.");
/* 380 */       InputStream is = JInvoke.class.getResourceAsStream(jinvokelib);
/* 381 */       BufferedInputStream bis = new BufferedInputStream(is);
/* 382 */       File libfile = new File(dllpath);
/* 383 */       libfile.createNewFile();
/* 384 */       FileOutputStream fos = new FileOutputStream(libfile);
/* 385 */       BufferedOutputStream bos = new BufferedOutputStream(fos);
/*     */       int c;
/* 387 */       while ((c = bis.read()) != -1)
/* 388 */         bos.write(c);
/* 389 */       bos.close();
/* 390 */       bis.close();
/* 391 */       is.close();
/* 392 */       fos.close();
/*     */     }
/*     */     catch (Exception e)
/*     */     {
/* 396 */       e.printStackTrace();
/*     */     }
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Connector-1.1.49.jar
 * Qualified Name:     com.dukascopy.api.nlink.win32.JInvoke
 * JD-Core Version:    0.6.0
 */