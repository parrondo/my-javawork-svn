/*     */ package com.dukascopy.dds2.greed.agent.compiler;
/*     */ 
/*     */ import com.dukascopy.api.RequiresFullAccess;
/*     */ import com.dukascopy.dds2.greed.agent.strategy.StratUtils;
/*     */ import com.dukascopy.transport.util.Hex;
/*     */ import java.io.ByteArrayInputStream;
/*     */ import java.io.ByteArrayOutputStream;
/*     */ import java.io.DataInputStream;
/*     */ import java.io.DataOutputStream;
/*     */ import java.io.File;
/*     */ import java.io.FileInputStream;
/*     */ import java.io.FileOutputStream;
/*     */ import java.io.FilenameFilter;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.security.GeneralSecurityException;
/*     */ import java.security.MessageDigest;
/*     */ import java.security.NoSuchAlgorithmException;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Enumeration;
/*     */ import java.util.List;
/*     */ import java.util.jar.JarEntry;
/*     */ import java.util.jar.JarFile;
/*     */ import org.slf4j.Logger;
/*     */ import org.slf4j.LoggerFactory;
/*     */ 
/*     */ public class JFXPack
/*     */ {
/*  36 */   private static final Logger LOGGER = LoggerFactory.getLogger(JFXPack.class);
/*     */   private File packedFile;
/*     */   byte[][] classCode;
/*     */   String[] className;
/*     */   byte[][] resourceCode;
/*     */   String[] resourceName;
/*     */   private JFXClassLoader classLoader;
/*     */ 
/*     */   public JFXPack()
/*     */   {
/*  39 */     this.packedFile = null;
/*     */ 
/*  42 */     this.classCode = ((byte[][])null);
/*     */ 
/*  44 */     this.className = null;
/*     */ 
/*  47 */     this.resourceCode = ((byte[][])null);
/*     */ 
/*  49 */     this.resourceName = null;
/*     */ 
/*  51 */     this.classLoader = null;
/*     */   }
/*     */ 
/*     */   public static JFXPack buildFromClass(File classFile, File[] jars)
/*     */   {
/*  59 */     JFXPack pack = new JFXPack();
/*     */ 
/*  61 */     File dir = classFile.getParentFile();
/*  62 */     File[] classFiles = dir.listFiles(new FilenameFilter(classFile) {
/*     */       public boolean accept(File dir, String name) {
/*  64 */         return (name.endsWith(".class")) && (!this.val$classFile.getName().equals(name));
/*     */       }
/*     */     });
/*  69 */     List jfxClassEntries = new ArrayList();
/*  70 */     List jfxResourceEntries = new ArrayList();
/*  71 */     if (jars.length > 0) {
/*  72 */       for (File file : jars) {
/*     */         try {
/*  74 */           if (file.isFile()) {
/*  75 */             JarFile jarFile = new JarFile(file);
/*  76 */             Enumeration enumeration = jarFile.entries();
/*  77 */             while (enumeration.hasMoreElements()) {
/*  78 */               JarEntry jarEntry = (JarEntry)enumeration.nextElement();
/*  79 */               if (jarEntry.isDirectory()) {
/*     */                 continue;
/*     */               }
/*  82 */               String entryName = jarEntry.getName();
/*     */ 
/*  84 */               if (entryName.endsWith("MANIFEST.MF"))
/*     */               {
/*     */                 continue;
/*     */               }
/*  88 */               InputStream inputStream = jarFile.getInputStream(jarEntry);
/*  89 */               ByteArrayOutputStream baos = new ByteArrayOutputStream();
/*  90 */               StratUtils.turboPipe(inputStream, baos);
/*  91 */               inputStream.close();
/*  92 */               baos.close();
/*  93 */               JFXEntry entry = new JFXEntry();
/*  94 */               entry.name = entryName;
/*  95 */               entry.bytes = baos.toByteArray();
/*  96 */               if (entryName.endsWith(".class"))
/*  97 */                 jfxClassEntries.add(entry);
/*     */               else {
/*  99 */                 jfxResourceEntries.add(entry);
/*     */               }
/*     */             }
/*     */ 
/* 103 */             jarFile.close();
/*     */           }
/*     */         } catch (Exception e) {
/* 106 */           LOGGER.error(e.getMessage(), e);
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/* 111 */     int jarResourcesSize = jfxResourceEntries.size();
/*     */ 
/* 113 */     int resourcesIndexer = 0;
/* 114 */     pack.resourceCode = new byte[jarResourcesSize][0];
/* 115 */     pack.resourceName = new String[jarResourcesSize];
/*     */ 
/* 117 */     for (JFXEntry entry : jfxResourceEntries) {
/* 118 */       pack.resourceCode[resourcesIndexer] = entry.bytes;
/* 119 */       pack.resourceName[resourcesIndexer] = entry.name;
/* 120 */       resourcesIndexer++;
/*     */     }
/*     */ 
/* 124 */     int jarClassesSize = jfxClassEntries.size();
/*     */ 
/* 126 */     int classesIndexer = 0;
/* 127 */     pack.classCode = new byte[classFiles.length + 1 + jarClassesSize][0];
/* 128 */     pack.className = new String[classFiles.length + 1 + jarClassesSize];
/*     */ 
/* 130 */     pack.classCode[classesIndexer] = loadFile(classFile);
/*     */ 
/* 132 */     JFXPack p = new JFXPack();
/* 133 */     JFXClassLoader cl = new JFXClassLoader(null, p);
/* 134 */     String goodName = cl.findName(pack.classCode[classesIndexer]);
/*     */ 
/* 136 */     pack.className[classesIndexer] = goodName;
/*     */ 
/* 138 */     for (int i = 0; i < classFiles.length; i++) {
/* 139 */       classesIndexer++;
/* 140 */       pack.classCode[classesIndexer] = loadFile(classFiles[i]);
/* 141 */       goodName = cl.findName(pack.classCode[classesIndexer]);
/* 142 */       pack.className[classesIndexer] = goodName;
/*     */     }
/*     */ 
/* 145 */     for (JFXEntry entry : jfxClassEntries) {
/* 146 */       classesIndexer++;
/* 147 */       pack.classCode[classesIndexer] = entry.bytes;
/*     */ 
/* 149 */       pack.className[classesIndexer] = entry.name.substring(0, entry.name.lastIndexOf(46)).replace('/', '.');
/*     */     }
/* 151 */     return pack;
/*     */   }
/*     */ 
/*     */   public static JFXPack loadFromPack(File packFile) throws IOException, GeneralSecurityException {
/* 155 */     JFXPack pack = null;
/* 156 */     if ((packFile != null) && (packFile.exists())) {
/* 157 */       FileInputStream fileInputStream = new FileInputStream(packFile);
/* 158 */       DataInputStream dataInputStream = new DataInputStream(fileInputStream);
/* 159 */       int version = dataInputStream.readInt();
/* 160 */       if (version == 2) {
/* 161 */         pack = new JFXPack();
/*     */         JFXPack tmp56_55 = pack; tmp56_55.getClass(); JFXHeader header = new JFXHeader2();
/* 163 */         header.read(dataInputStream);
/*     */       }
/* 165 */       dataInputStream.close();
/* 166 */       fileInputStream.close();
/*     */     }
/* 168 */     return pack;
/*     */   }
/*     */ 
/*     */   public static JFXPack loadFromPack(byte[] bytes)
/*     */     throws Exception
/*     */   {
/* 179 */     DataInputStream is = new DataInputStream(new ByteArrayInputStream(bytes));
/*     */     try {
/* 181 */       int version = is.readInt();
/* 182 */       if (version == 2) {
/* 183 */         pack = new JFXPack();
/*     */         JFXPack tmp39_38 = pack; tmp39_38.getClass(); JFXHeader header = new JFXHeader2();
/* 185 */         header.read(is);
/* 186 */         JFXPack localJFXPack1 = pack;
/*     */         return localJFXPack1;
/*     */       }
/* 188 */       LOGGER.debug("Null returned because of not supported version: " + version);
/* 189 */       JFXPack pack = null;
/*     */       return pack; } finally { is.close(); } throw localObject;
/*     */   }
/*     */ 
/*     */   public boolean isFullAccessRequested()
/*     */   {
/*     */     try
/*     */     {
/* 201 */       Class clazz = getTargetClass(true);
/* 202 */       if (clazz != null) {
/* 203 */         return clazz.getAnnotation(RequiresFullAccess.class) != null;
/*     */       }
/* 205 */       return false;
/*     */     } catch (Exception e) {
/* 207 */       LOGGER.error(e.getMessage(), e);
/* 208 */     }return false;
/*     */   }
/*     */ 
/*     */   public void setFullAccess(boolean fullAccess)
/*     */   {
/* 213 */     getClassLoader().setFullAccess(fullAccess);
/*     */   }
/*     */ 
/*     */   public boolean isFullAccess() {
/* 217 */     return (this.classLoader != null) && (this.classLoader.isFullAccess());
/*     */   }
/*     */ 
/*     */   public Object getTarget() {
/*     */     try {
/* 222 */       Class targetClass = getTargetClass(false);
/* 223 */       if (targetClass != null)
/* 224 */         return targetClass.newInstance();
/*     */     }
/*     */     catch (Exception e) {
/* 227 */       LOGGER.error(e.getMessage(), e);
/*     */     }
/* 229 */     return null;
/*     */   }
/*     */ 
/*     */   public Class<?> getTargetClass() {
/* 233 */     return getTargetClass(true);
/*     */   }
/*     */ 
/*     */   Class<?> getTargetClass(boolean useTemporaryCL) {
/*     */     try {
/* 238 */       JFXClassLoader jfxClassLoader = useTemporaryCL ? new JFXClassLoader(Thread.currentThread().getContextClassLoader(), this) : getClassLoader();
/* 239 */       return Class.forName(this.className[0], true, jfxClassLoader);
/*     */     } catch (Throwable e) {
/* 241 */       LOGGER.error(e.getMessage(), e);
/*     */     }
/* 243 */     return null;
/*     */   }
/*     */ 
/*     */   public static byte[] loadFile(File file) {
/* 247 */     FileInputStream fileInputStream = null;
/* 248 */     byte[] rc = null;
/*     */     try {
/* 250 */       rc = new byte[(int)file.length()];
/* 251 */       fileInputStream = new FileInputStream(file);
/* 252 */       fileInputStream.read(rc);
/*     */     } catch (Exception e) {
/* 254 */       LOGGER.error(e.getMessage(), e);
/*     */     } finally {
/*     */       try {
/* 257 */         fileInputStream.close();
/*     */       } catch (Exception e) {
/* 259 */         LOGGER.error(e.getMessage(), e);
/*     */       }
/*     */     }
/*     */ 
/* 263 */     return rc;
/*     */   }
/*     */ 
/*     */   public void write(File jfxFile)
/*     */   {
/* 268 */     if (jfxFile.exists()) {
/* 269 */       jfxFile.delete();
/*     */     }
/*     */ 
/* 272 */     FileOutputStream fileOutputStream = null;
/* 273 */     DataOutputStream dataOutputStream = null;
/*     */     try {
/* 275 */       jfxFile.createNewFile();
/* 276 */       fileOutputStream = new FileOutputStream(jfxFile);
/* 277 */       dataOutputStream = new DataOutputStream(fileOutputStream);
/*     */ 
/* 279 */       JFXHeader header = new JFXHeader2();
/* 280 */       header.write(dataOutputStream);
/*     */     } catch (Exception e) {
/* 282 */       LOGGER.error(e.getMessage(), e);
/*     */     } finally {
/*     */       try {
/* 285 */         dataOutputStream.close();
/* 286 */         fileOutputStream.close();
/*     */       } catch (Exception e) {
/* 288 */         LOGGER.error(e.getMessage(), e);
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 293 */     this.packedFile = jfxFile;
/*     */   }
/*     */ 
/*     */   public String getMD5HexString()
/*     */   {
/*     */     try
/*     */     {
/* 301 */       MessageDigest md = MessageDigest.getInstance("MD5");
/* 302 */       for (byte[] data : this.classCode) {
/* 303 */         md.update(data);
/*     */       }
/* 305 */       for (byte[] data : this.resourceCode) {
/* 306 */         md.update(data);
/*     */       }
/* 308 */       return Hex.encodeHexString(md.digest()).toUpperCase();
/*     */     } catch (NoSuchAlgorithmException e) {
/* 310 */       LOGGER.error(e.getMessage(), e);
/* 311 */     }return null;
/*     */   }
/*     */ 
/*     */   public File getPackedFile()
/*     */   {
/* 316 */     return this.packedFile;
/*     */   }
/*     */ 
/*     */   public JFXClassLoader getClassLoader()
/*     */   {
/* 423 */     if (this.classLoader == null) {
/* 424 */       this.classLoader = new JFXClassLoader(Thread.currentThread().getContextClassLoader(), this);
/*     */     }
/* 426 */     return this.classLoader;
/*     */   }
/*     */ 
/*     */   class JFXHeader2 extends JFXPack.JFXHeader
/*     */   {
/*     */     JFXHeader2()
/*     */     {
/* 328 */       super();
/*     */     }
/*     */     int vestion() {
/* 331 */       return 2;
/*     */     }
/*     */ 
/*     */     void write(DataOutputStream dataOutputStream)
/*     */       throws Exception
/*     */     {
/* 338 */       dataOutputStream.writeInt(vestion());
/*     */ 
/* 340 */       dataOutputStream.writeLong(0L);
/*     */ 
/* 343 */       byte[] key = AESCript.key();
/* 344 */       dataOutputStream.writeInt(key.length);
/* 345 */       dataOutputStream.write(key);
/*     */ 
/* 347 */       dataOutputStream.writeInt(JFXPack.this.className.length);
/*     */ 
/* 349 */       for (int i = 0; i < JFXPack.this.className.length; i++)
/*     */       {
/* 351 */         dataOutputStream.writeInt(JFXPack.this.className[i].length());
/* 352 */         dataOutputStream.write(JFXPack.this.className[i].getBytes());
/*     */ 
/* 354 */         byte[] content = AESCript.encript(JFXPack.this.classCode[i], key);
/* 355 */         dataOutputStream.writeInt(content.length);
/* 356 */         dataOutputStream.write(content);
/*     */       }
/*     */ 
/* 360 */       dataOutputStream.writeInt(JFXPack.this.resourceName.length);
/*     */ 
/* 362 */       for (int i = 0; i < JFXPack.this.resourceName.length; i++)
/*     */       {
/* 364 */         dataOutputStream.writeInt(JFXPack.this.resourceName[i].length());
/* 365 */         dataOutputStream.write(JFXPack.this.resourceName[i].getBytes());
/*     */ 
/* 367 */         byte[] content = AESCript.encript(JFXPack.this.resourceCode[i], key);
/* 368 */         dataOutputStream.writeInt(content.length);
/* 369 */         dataOutputStream.write(content);
/*     */       }
/*     */     }
/*     */ 
/*     */     void read(DataInputStream dataInputStream)
/*     */       throws IOException, GeneralSecurityException
/*     */     {
/* 378 */       dataInputStream.readLong();
/*     */ 
/* 380 */       int keyLen = dataInputStream.readInt();
/* 381 */       byte[] key = new byte[keyLen];
/* 382 */       int read = 0;
/* 383 */       while (read += dataInputStream.read(key, read, key.length - read) < key.length);
/* 386 */       int countClassesLen = dataInputStream.readInt();
/* 387 */       JFXPack.this.className = new String[countClassesLen];
/* 388 */       JFXPack.this.classCode = new byte[countClassesLen][];
/* 389 */       for (int i = 0; i < JFXPack.this.className.length; i++) {
/* 390 */         int nameLen = dataInputStream.readInt();
/* 391 */         byte[] name = new byte[nameLen];
/* 392 */         read = 0;
/* 393 */         while (read += dataInputStream.read(name, read, name.length - read) < name.length);
/* 394 */         JFXPack.this.className[i] = new String(name);
/* 395 */         int contentLen = dataInputStream.readInt();
/* 396 */         byte[] content = new byte[contentLen];
/* 397 */         read = 0;
/* 398 */         while (read += dataInputStream.read(content, read, content.length - read) < content.length);
/* 399 */         JFXPack.this.classCode[i] = AESCript.decript(content, key);
/*     */       }
/*     */ 
/* 402 */       int countResourcesLen = dataInputStream.readInt();
/* 403 */       JFXPack.this.resourceName = new String[countResourcesLen];
/* 404 */       JFXPack.this.resourceCode = new byte[countResourcesLen][];
/* 405 */       for (int i = 0; i < JFXPack.this.resourceName.length; i++) {
/* 406 */         int nameLen = dataInputStream.readInt();
/* 407 */         byte[] name = new byte[nameLen];
/* 408 */         read = 0;
/* 409 */         while (read += dataInputStream.read(name, read, name.length - read) < name.length);
/* 410 */         JFXPack.this.resourceName[i] = new String(name);
/* 411 */         int contentLen = dataInputStream.readInt();
/* 412 */         byte[] content = new byte[contentLen];
/* 413 */         read = 0;
/* 414 */         while (read += dataInputStream.read(content, read, content.length - read) < content.length);
/* 415 */         JFXPack.this.resourceCode[i] = AESCript.decript(content, key);
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   abstract class JFXHeader
/*     */   {
/*     */     JFXHeader()
/*     */     {
/*     */     }
/*     */ 
/*     */     abstract int vestion();
/*     */ 
/*     */     abstract void write(DataOutputStream paramDataOutputStream)
/*     */       throws Exception;
/*     */ 
/*     */     abstract void read(DataInputStream paramDataInputStream)
/*     */       throws IOException, GeneralSecurityException;
/*     */   }
/*     */ 
/*     */   static class JFXEntry
/*     */   {
/*     */     String name;
/*     */     byte[] bytes;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.dds2.greed.agent.compiler.JFXPack
 * JD-Core Version:    0.6.0
 */