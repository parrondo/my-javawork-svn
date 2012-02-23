/*     */ package com.dukascopy.dds2.greed.util;
/*     */ 
/*     */ import java.io.BufferedInputStream;
/*     */ import java.io.BufferedReader;
/*     */ import java.io.BufferedWriter;
/*     */ import java.io.ByteArrayOutputStream;
/*     */ import java.io.Closeable;
/*     */ import java.io.File;
/*     */ import java.io.FileInputStream;
/*     */ import java.io.FileNotFoundException;
/*     */ import java.io.FileOutputStream;
/*     */ import java.io.FileWriter;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.InputStreamReader;
/*     */ import java.io.OutputStream;
/*     */ import java.io.Reader;
/*     */ import java.io.StringWriter;
/*     */ import java.io.Writer;
/*     */ import java.net.URL;
/*     */ import java.nio.channels.FileChannel;
/*     */ import java.util.LinkedList;
/*     */ import java.util.List;
/*     */ import java.util.jar.JarEntry;
/*     */ import java.util.jar.JarInputStream;
/*     */ import java.util.jar.JarOutputStream;
/*     */ import org.slf4j.Logger;
/*     */ import org.slf4j.LoggerFactory;
/*     */ 
/*     */ public class IOUtils
/*     */ {
/*     */   private static final int DEFAULT_BUFFER_SIZE = 4096;
/*  99 */   private static final Logger LOGGER = LoggerFactory.getLogger(IOUtils.class);
/*     */   private static final String java_io_tmpdir = "java.io.tmpdir";
/*     */   private static final String default_lib_dir = "jfxide";
/*     */ 
/*     */   public static int copy(InputStream input, OutputStream output)
/*     */     throws IOException
/*     */   {
/*  36 */     long count = copyLarge(input, output);
/*  37 */     if (count > 2147483647L) {
/*  38 */       return -1;
/*     */     }
/*  40 */     return (int)count;
/*     */   }
/*     */ 
/*     */   public static long copyLarge(InputStream input, OutputStream output) throws IOException {
/*  44 */     byte[] buffer = new byte[4096];
/*  45 */     long count = 0L;
/*  46 */     int n = 0;
/*  47 */     while (-1 != (n = input.read(buffer))) {
/*  48 */       output.write(buffer, 0, n);
/*  49 */       count += n;
/*     */     }
/*  51 */     return count;
/*     */   }
/*     */ 
/*     */   public static byte[] toByteArray(InputStream input) throws IOException {
/*  55 */     ByteArrayOutputStream output = new ByteArrayOutputStream();
/*  56 */     copy(input, output);
/*  57 */     return output.toByteArray();
/*     */   }
/*     */ 
/*     */   public static void closeQuietly(Closeable closable) {
/*     */     try {
/*  62 */       if (closable != null)
/*  63 */         closable.close();
/*     */     }
/*     */     catch (IOException ioe)
/*     */     {
/*     */     }
/*     */   }
/*     */ 
/*     */   public static void writeByteArrayToFile(File file, byte[] data) throws IOException {
/*  71 */     OutputStream out = null;
/*     */     try {
/*  73 */       out = openOutputStream(file);
/*  74 */       out.write(data);
/*     */     } finally {
/*  76 */       closeQuietly(out);
/*     */     }
/*     */   }
/*     */ 
/*     */   public static FileOutputStream openOutputStream(File file) throws IOException {
/*  81 */     if (file.exists()) {
/*  82 */       if (file.isDirectory()) {
/*  83 */         throw new IOException("File '" + file + "' exists but is a directory");
/*     */       }
/*  85 */       if (!file.canWrite())
/*  86 */         throw new IOException("File '" + file + "' cannot be written to");
/*     */     }
/*     */     else {
/*  89 */       File parent = file.getParentFile();
/*  90 */       if ((parent != null) && (!parent.exists()) && 
/*  91 */         (!parent.mkdirs())) {
/*  92 */         throw new IOException("File '" + file + "' could not be created");
/*     */       }
/*     */     }
/*     */ 
/*  96 */     return new FileOutputStream(file);
/*     */   }
/*     */ 
/*     */   public static boolean copyFiles(File inFile, File outFile)
/*     */   {
/*     */     try
/*     */     {
/* 105 */       FileInputStream fin = null;
/* 106 */       FileOutputStream fout = null;
/*     */       try {
/* 108 */         fin = new FileInputStream(inFile);
/* 109 */         fout = new FileOutputStream(outFile);
/*     */ 
/* 111 */         FileChannel inChannel = fin.getChannel();
/* 112 */         FileChannel outChannel = fout.getChannel();
/*     */ 
/* 114 */         int maxCount = 67076096;
/* 115 */         long size = inChannel.size();
/* 116 */         long position = 0L;
/* 117 */         while (position < size)
/* 118 */           position += inChannel.transferTo(position, maxCount, outChannel);
/*     */       }
/*     */       finally {
/* 121 */         if (fin != null) {
/* 122 */           fin.close();
/*     */         }
/* 124 */         if (fout != null) {
/* 125 */           fout.close();
/*     */         }
/*     */       }
/* 128 */       return true;
/*     */     } catch (Exception e) {
/* 130 */       LOGGER.error(e.getMessage(), e);
/* 131 */     }return false;
/*     */   }
/*     */ 
/*     */   public static boolean isJarMagic(byte[] magic)
/*     */   {
/* 137 */     return (magic[0] == 80) && (magic[1] == 75) && (magic[2] >= 1) && (magic[2] < 8) && (magic[3] == magic[2] + 1);
/*     */   }
/*     */ 
/*     */   public static byte[] readMagic(BufferedInputStream in) throws IOException
/*     */   {
/* 142 */     in.mark(4);
/* 143 */     byte[] magic = new byte[4];
/* 144 */     for (int i = 0; i < magic.length; i++)
/*     */     {
/* 146 */       if (1 != in.read(magic, i, 1))
/*     */         break;
/*     */     }
/* 149 */     in.reset();
/* 150 */     return magic;
/*     */   }
/*     */ 
/*     */   public static void copyJarFile(JarInputStream in, JarOutputStream out) throws IOException {
/* 154 */     byte[] buffer = new byte[16384];
/*     */     JarEntry je;
/* 155 */     while ((je = in.getNextJarEntry()) != null) {
/* 156 */       out.putNextEntry(je);
/*     */       int nr;
/* 157 */       while (0 < (nr = in.read(buffer))) {
/* 158 */         out.write(buffer, 0, nr);
/*     */       }
/*     */     }
/* 161 */     in.close();
/*     */   }
/*     */ 
/*     */   public static boolean recreateDir(File dir) {
/* 165 */     boolean rc = true;
/* 166 */     rc = deleteDir(dir);
/* 167 */     if (!rc) {
/* 168 */       return false;
/*     */     }
/* 170 */     rc = dir.mkdirs();
/* 171 */     return rc;
/*     */   }
/*     */ 
/*     */   public static boolean deleteDir(File dir)
/*     */   {
/* 176 */     boolean rc = false;
/* 177 */     if (dir.exists()) {
/* 178 */       if (dir.isDirectory()) {
/* 179 */         for (File child : dir.listFiles()) {
/* 180 */           rc = deleteDir(child);
/* 181 */           if (!rc) {
/* 182 */             return false;
/*     */           }
/*     */         }
/*     */       }
/* 186 */       rc = dir.delete();
/* 187 */       if (!rc) {
/* 188 */         return false;
/*     */       }
/*     */     }
/* 191 */     return true;
/*     */   }
/*     */ 
/*     */   public static File getRootPath() {
/* 195 */     String path = System.getProperty("java.io.tmpdir") + File.separator + "jfxide";
/* 196 */     File file = new File(path);
/* 197 */     if (!file.exists()) {
/* 198 */       file.mkdirs();
/*     */     }
/* 200 */     return file;
/*     */   }
/*     */ 
/*     */   public static boolean writeFile(String text, File outFile) {
/* 204 */     boolean success = false;
/* 205 */     BufferedWriter bufWriter = null;
/* 206 */     FileWriter fileWriter = null;
/*     */     try {
/* 208 */       fileWriter = new FileWriter(outFile);
/* 209 */       bufWriter = new BufferedWriter(fileWriter);
/* 210 */       bufWriter.write(text);
/* 211 */       bufWriter.flush();
/* 212 */       success = true;
/*     */     } catch (IOException e) {
/* 214 */       LOGGER.error(e.getMessage(), e);
/*     */     } finally {
/* 216 */       closeQuietly(fileWriter);
/* 217 */       closeQuietly(bufWriter);
/*     */     }
/* 219 */     return success;
/*     */   }
/*     */ 
/*     */   public static InputStream getResourceAsStream(String path) throws FileNotFoundException
/*     */   {
/* 224 */     ClassLoader cl = Thread.currentThread().getContextClassLoader();
/* 225 */     URL dirURL = cl.getResource(path);
/*     */ 
/* 227 */     if (dirURL.getProtocol().equals("jar"))
/* 228 */       return cl.getResourceAsStream(path);
/* 229 */     if (dirURL.getProtocol().equals("file")) {
/* 230 */       return new FileInputStream(new File(path));
/*     */     }
/* 232 */     return null;
/*     */   }
/*     */ 
/*     */   public static String streamToString(InputStream is) throws IOException {
/* 236 */     String content = "";
/* 237 */     if (is != null) {
/* 238 */       Writer writer = new StringWriter();
/*     */ 
/* 240 */       char[] buffer = new char[1024];
/*     */ 
/* 242 */       Reader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
/*     */       int n;
/* 245 */       while ((n = reader.read(buffer)) != -1) {
/* 246 */         writer.write(buffer, 0, n);
/*     */       }
/*     */ 
/* 249 */       content = writer.toString();
/*     */     }
/* 251 */     return content;
/*     */   }
/*     */ 
/*     */   public static List<String> listAll(String dir) {
/* 255 */     List result = new LinkedList();
/* 256 */     File fileDir = new File(dir);
/* 257 */     if (fileDir.exists()) {
/* 258 */       if (!fileDir.isDirectory()) {
/* 259 */         String path = fileDir.getPath();
/* 260 */         if (!path.contains(".svn"))
/* 261 */           result.add(path);
/*     */       }
/*     */       else {
/* 264 */         for (String fileName : fileDir.list()) {
/* 265 */           result.addAll(listAll(dir + "/" + fileName));
/*     */         }
/*     */       }
/*     */     }
/* 269 */     return result;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.dds2.greed.util.IOUtils
 * JD-Core Version:    0.6.0
 */