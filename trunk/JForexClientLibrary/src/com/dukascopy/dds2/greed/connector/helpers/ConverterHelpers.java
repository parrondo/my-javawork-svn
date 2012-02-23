/*     */ package com.dukascopy.dds2.greed.connector.helpers;
/*     */ 
/*     */ import com.dukascopy.dds2.greed.connector.IConverter;
/*     */ import com.dukascopy.dds2.greed.connector.mt4.MTJFConverter;
/*     */ import java.io.BufferedInputStream;
/*     */ import java.io.BufferedReader;
/*     */ import java.io.ByteArrayInputStream;
/*     */ import java.io.File;
/*     */ import java.io.FileInputStream;
/*     */ import java.io.FileOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.InputStreamReader;
/*     */ import java.io.UnsupportedEncodingException;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import java.util.Scanner;
/*     */ import java.util.StringTokenizer;
/*     */ import java.util.regex.Matcher;
/*     */ import java.util.regex.Pattern;
/*     */ 
/*     */ public class ConverterHelpers
/*     */ {
/*     */   private static final String LINE_SEPARATOR_PATTERN = "\r\n|[\n\r  ]";
/*     */   private static final String LINE_PATTERN = ".*(\r\n|[\n\r  ])";
/* 118 */   public static final byte[] NULL_BYTE_ARRAY = new byte[0];
/*     */ 
/*     */   public static final IConverter getInstance(ExternalEngine engine)
/*     */   {
/*  33 */     IConverter converter = null;
/*  34 */     if (converter == null) {
/*  35 */       converter = new MTJFConverter(engine);
/*     */     }
/*  37 */     return converter;
/*     */   }
/*     */ 
/*     */   @Deprecated
/*     */   public static final IConverter getMT4Converter()
/*     */   {
/*  44 */     IConverter converter = null;
/*  45 */     if (converter == null) {
/*  46 */       converter = new MTJFConverter(ExternalEngine.MT5);
/*     */     }
/*  48 */     return converter;
/*     */   }
/*     */ 
/*     */   @Deprecated
/*     */   public static final IConverter getMT5Converter()
/*     */   {
/*  56 */     IConverter converter = null;
/*  57 */     if (converter == null) {
/*  58 */       converter = new MTJFConverter(ExternalEngine.MT5);
/*     */     }
/*  60 */     return converter;
/*     */   }
/*     */ 
/*     */   public static final IConverter getMT4StrategyConverter() {
/*  64 */     return getInstance(ExternalEngine.MT4STRATEGY);
/*     */   }
/*     */   public static final IConverter getMT5StrategyConverter() {
/*  67 */     return getInstance(ExternalEngine.MT5STRATEGY);
/*     */   }
/*     */   public static final IConverter getMT4IndicatorConverter() {
/*  70 */     return getInstance(ExternalEngine.MT4INDICATOR);
/*     */   }
/*     */   public static final IConverter getMT5IndicatorConverter() {
/*  73 */     return getInstance(ExternalEngine.MT5INDICATOR);
/*     */   }
/*     */ 
/*     */   public static String niceArrayInJava(String type, String variable) {
/*  77 */     StringBuilder normalArray = new StringBuilder();
/*  78 */     int dimention = 0;
/*  79 */     int fromIndex = 0;
/*  80 */     int index = 0;
/*  81 */     while ((index = variable.indexOf("[", fromIndex)) > 0) {
/*  82 */       dimention++;
/*  83 */       fromIndex += index + 1;
/*     */     }
/*  85 */     normalArray.append(type);
/*  86 */     if (dimention > 0) {
/*  87 */       for (int i = 0; i < dimention; i++) {
/*  88 */         normalArray.append("[]");
/*     */       }
/*  90 */       normalArray.append(" ");
/*  91 */       normalArray.append(variable.substring(0, variable.indexOf("[")));
/*     */     } else {
/*  93 */       normalArray.append(" ");
/*  94 */       normalArray.append(variable);
/*     */     }
/*  96 */     return normalArray.toString();
/*     */   }
/*     */ 
/*     */   public static int getLineCount(String src) {
/* 100 */     Pattern p = Pattern.compile("\n", 32);
/* 101 */     Matcher m = p.matcher(src);
/* 102 */     int count = 0;
/* 103 */     while (m.find()) {
/* 104 */       count++;
/*     */     }
/* 106 */     return count;
/*     */   }
/*     */ 
/*     */   public static String normaliseStr(String value)
/*     */   {
/* 111 */     Pattern pattern = Pattern.compile("[.@$?]");
/* 112 */     Matcher matcher = pattern.matcher(value.trim());
/* 113 */     String result = matcher.replaceAll("_");
/* 114 */     result = result.replaceAll("\\P{ASCII}", "0");
/* 115 */     return result.trim();
/*     */   }
/*     */ 
/*     */   public static byte[] readFile(File f)
/*     */     throws IOException
/*     */   {
/* 121 */     if (!f.exists())
/* 122 */       return NULL_BYTE_ARRAY;
/* 123 */     int size = (int)f.length();
/* 124 */     if (size <= 0)
/* 125 */       return NULL_BYTE_ARRAY;
/* 126 */     byte[] data = new byte[size];
/*     */     try {
/* 128 */       FileInputStream fis = new FileInputStream(f);
/* 129 */       int bytes_read = 0;
/* 130 */       while (bytes_read < size)
/* 131 */         bytes_read += fis.read(data, bytes_read, size - bytes_read);
/* 132 */       fis.close();
/*     */     } catch (Exception e) {
/* 134 */       e.printStackTrace();
/* 135 */       return NULL_BYTE_ARRAY;
/*     */     }
/* 137 */     return data;
/*     */   }
/*     */ 
/*     */   public static boolean writeFile(String filePath, byte[] bs) throws IOException {
/* 141 */     FileOutputStream fileOutputStream = null;
/*     */     try {
/* 143 */       File file = new File(filePath);
/* 144 */       if (!file.exists()) {
/* 145 */         file.createNewFile();
/*     */       }
/* 147 */       fileOutputStream = new FileOutputStream(file);
/* 148 */       ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bs);
/*     */ 
/* 152 */       byte[] buffer = new byte[4096];
/*     */       while (true) {
/* 154 */         int bytes_read = byteArrayInputStream.read(buffer);
/*     */ 
/* 156 */         if (bytes_read == -1) {
/*     */           break;
/*     */         }
/* 159 */         fileOutputStream.write(buffer, 0, bytes_read);
/*     */       }
/* 161 */       int i = 1;
/*     */       return i;
/*     */     }
/*     */     catch (Throwable te)
/*     */     {
/* 164 */       throw new IOException(te);
/*     */     } finally {
/*     */       try {
/* 167 */         fileOutputStream.close();
/*     */       } catch (Exception e) {
/* 169 */         throw new IOException(e); } 
/* 169 */     }throw localObject;
/*     */   }
/*     */ 
/*     */   public static String convertStreamToString(BufferedInputStream is, String encoding)
/*     */     throws IOException
/*     */   {
/* 181 */     String s = null;
/* 182 */     if (is != null) {
/* 183 */       StringBuilder sb = new StringBuilder();
/*     */       try
/*     */       {
/* 187 */         BufferedReader reader = new BufferedReader(new InputStreamReader(is, encoding));
/*     */         String line;
/* 188 */         while ((line = reader.readLine()) != null)
/* 189 */           sb.append(line).append("\n");
/*     */       }
/*     */       finally
/*     */       {
/*     */       }
/* 194 */       s = sb.toString();
/*     */     }
/* 196 */     return s;
/*     */   }
/*     */ 
/*     */   public static InputStream convertStringToStream(String string, String encoding)
/*     */   {
/* 204 */     InputStream is = null;
/*     */     try {
/* 206 */       is = new ByteArrayInputStream(string.getBytes(encoding));
/*     */     } catch (UnsupportedEncodingException e) {
/* 208 */       e.printStackTrace();
/*     */     }
/* 210 */     return is;
/*     */   }
/*     */ 
/*     */   public static StringBuilder convertImportToInterface(StringBuilder src, StringBuilder withoutImports) {
/* 214 */     Pattern pattern = Pattern.compile("#import.*(\r\n|[\n\r  ])");
/* 215 */     Matcher matcher = pattern.matcher(src);
/*     */ 
/* 217 */     withoutImports.append(src);
/*     */ 
/* 219 */     StringBuilder importString = new StringBuilder();
/* 220 */     int start = 0;
/* 221 */     int end = 1;
/* 222 */     int startIndex = 0;
/* 223 */     int endIndex = 1;
/* 224 */     while (matcher.find()) {
/* 225 */       if (start != end) {
/* 226 */         start = matcher.start();
/* 227 */         startIndex = start;
/*     */ 
/* 229 */         if (matcher.find()) {
/* 230 */           end = matcher.end();
/* 231 */           if (matcher.group().length() > 8) {
/* 232 */             end = matcher.start();
/* 233 */             start = matcher.start();
/*     */           }
/* 235 */           endIndex = end;
/*     */         }
/*     */ 
/*     */       }
/*     */       else
/*     */       {
/* 241 */         startIndex = start;
/* 242 */         if (matcher.group().length() > 8) {
/* 243 */           end = matcher.start();
/* 244 */           start = matcher.start();
/*     */         } else {
/* 246 */           end = matcher.end();
/*     */         }
/* 248 */         endIndex = end;
/*     */       }
/* 250 */       String inStr = src.substring(startIndex, endIndex);
/*     */ 
/* 252 */       importString.append(inStr);
/* 253 */       int pos = withoutImports.indexOf(inStr);
/* 254 */       withoutImports = withoutImports.delete(pos, pos + inStr.length());
/*     */     }
/*     */ 
/* 257 */     Scanner in = new Scanner(importString.toString());
/* 258 */     StringBuilder result = new StringBuilder();
/* 259 */     StringBuilder buff = new StringBuilder();
/* 260 */     StringBuilder varDecl = new StringBuilder();
/* 261 */     StringBuilder funcDecl = new StringBuilder();
/* 262 */     String currentClass = "";
/*     */ 
/* 264 */     boolean startInterface = false;
/* 265 */     while (in.hasNextLine()) {
/* 266 */       String line = in.nextLine();
/* 267 */       StringTokenizer st = new StringTokenizer(line);
/* 268 */       if (st.hasMoreTokens()) {
/* 269 */         String importWord = st.nextToken();
/* 270 */         if (!importWord.startsWith("//"))
/* 271 */           if (importWord.startsWith("#import")) {
/* 272 */             if (st.hasMoreTokens()) {
/* 273 */               String interfaceName = st.nextToken();
/* 274 */               if ((interfaceName.startsWith("\"")) && 
/* 275 */                 (startInterface)) {
/* 276 */                 buff.append("}\n");
/*     */               }
/*     */ 
/* 279 */               currentClass = interfaceName.substring(1, interfaceName.length() - 5);
/*     */ 
/* 281 */               currentClass = currentClass.replaceAll("-", "_");
/*     */ 
/* 284 */               varDecl.append(new StringBuilder().append(currentClass).append(" ").append(currentClass).append(" = NLink.create(").append(currentClass).append(".class);\n").toString());
/* 285 */               buff.append("@DllClass\n");
/* 286 */               buff.append(new StringBuilder().append("public interface ").append(currentClass).append(" { \n").toString());
/* 287 */               startInterface = true;
/*     */             } else {
/* 289 */               buff.append("}\n");
/* 290 */               startInterface = false;
/*     */             }
/*     */           } else {
/* 293 */             StringBuilder fnLine = new StringBuilder();
/*     */ 
/* 296 */             if ((line.trim().lastIndexOf("//") > line.trim().lastIndexOf(");")) || (line.trim().lastIndexOf("*/") > line.trim().lastIndexOf(");")))
/*     */             {
/* 298 */               line = line.substring(0, line.trim().lastIndexOf(";") + 1);
/*     */             }
/* 300 */             fnLine.append(line.trim());
/*     */ 
/* 302 */             while ((!line.trim().endsWith(");")) && (in.hasNextLine())) {
/* 303 */               line = in.nextLine();
/* 304 */               fnLine.append(line.trim());
/*     */             }
/* 306 */             String[] normalDeclaration = makeCorrectFunctionDeclaration(fnLine.toString(), currentClass);
/* 307 */             funcDecl.append(normalDeclaration[1]);
/* 308 */             buff.append("@DllMethod\n");
/* 309 */             buff.append(new StringBuilder().append(normalDeclaration[0]).append("\n").toString());
/*     */           }
/*     */       }
/*     */     }
/* 312 */     if (startInterface) {
/* 313 */       buff.append("}\n");
/*     */     }
/* 315 */     result.append(buff);
/* 316 */     result.append(varDecl);
/* 317 */     result.append(funcDecl);
/*     */ 
/* 320 */     return result;
/*     */   }
/*     */ 
/*     */   public static String[] makeCorrectFunctionDeclaration(String line, String className) {
/* 324 */     String[] resultArray = new String[2];
/* 325 */     List variable = new ArrayList();
/* 326 */     List type = new ArrayList();
/* 327 */     String fnType = "";
/* 328 */     String fnName = "";
/* 329 */     String fnParams = "";
/* 330 */     String fnNewParams = "";
/*     */ 
/* 332 */     StringTokenizer fnst = new StringTokenizer(line);
/* 333 */     fnType = fnst.nextToken();
/*     */ 
/* 335 */     fnName = line.substring(fnType.length(), line.indexOf("("));
/* 336 */     fnParams = line.substring(line.indexOf("(") + 1, line.indexOf(")"));
/*     */ 
/* 338 */     fnst = new StringTokenizer(fnParams, ",");
/* 339 */     int count = 0;
/* 340 */     while (fnst.hasMoreTokens()) {
/* 341 */       String value = fnst.nextToken();
/* 342 */       if (!value.trim().isEmpty()) {
/* 343 */         fnNewParams = new StringBuilder().append(fnNewParams).append(value).toString();
/* 344 */         if (value.trim().indexOf(" ") < 0) {
/* 345 */           variable.add(new StringBuilder().append("param").append(count++).toString());
/* 346 */           type.add(value);
/*     */         } else {
/* 348 */           int paramLen = value.trim().lastIndexOf("=");
/* 349 */           if (paramLen < 0) {
/* 350 */             paramLen = value.trim().length();
/*     */           }
/* 352 */           String key = value.substring(value.trim().lastIndexOf(" "), paramLen);
/* 353 */           if (key.trim().startsWith("/*")) {
/* 354 */             paramLen = value.trim().lastIndexOf("/*");
/* 355 */             key = value.substring(0, value.trim().lastIndexOf("/*")).trim();
/* 356 */             key = key.substring(key.trim().lastIndexOf(" "), key.length());
/*     */           }
/*     */ 
/* 359 */           if ((key.trim().startsWith("&")) || (key.trim().startsWith("*"))) {
/* 360 */             paramLen--;
/* 361 */             key = key.substring(1, key.length());
/*     */           }
/*     */ 
/* 364 */           variable.add(key.trim());
/* 365 */           String typeString = value.substring(0, paramLen - key.length()).trim();
/* 366 */           if ((typeString.trim().endsWith("&")) || (typeString.endsWith("*"))) {
/* 367 */             typeString = typeString.substring(0, typeString.length() - 1);
/*     */           }
/* 369 */           type.add(typeString);
/*     */         }
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 375 */     StringBuilder decl = new StringBuilder();
/* 376 */     decl.append(new StringBuilder().append(fnType).append(" ").append(fnName.trim()).append("(").toString());
/* 377 */     for (int i = 0; i < variable.size(); i++) {
/* 378 */       String variabletype = niceArrayInJava((String)type.get(i), (String)variable.get(i));
/* 379 */       decl.append(variabletype);
/* 380 */       if (i < variable.size() - 1) {
/* 381 */         decl.append(", ");
/*     */       }
/*     */     }
/* 384 */     decl.append(")");
/* 385 */     resultArray[0] = decl.toString();
/*     */ 
/* 389 */     StringBuilder call = new StringBuilder();
/* 390 */     call.append(new StringBuilder().append("public ").append(decl.toString()).toString());
/* 391 */     call.append("{");
/* 392 */     if (!fnType.trim().startsWith("void")) {
/* 393 */       call.append("return ");
/*     */     }
/*     */ 
/* 396 */     call.append(new StringBuilder().append(className).append(".").append(fnName.trim()).append("(").toString());
/* 397 */     for (int i = 0; i < variable.size(); i++) {
/* 398 */       String var = (String)variable.get(i);
/* 399 */       int arrayDimBegin = var.indexOf("[");
/* 400 */       if (arrayDimBegin > 0) {
/* 401 */         var = var.substring(0, arrayDimBegin);
/*     */       }
/*     */ 
/* 404 */       call.append(var);
/* 405 */       if (i < variable.size() - 1) {
/* 406 */         call.append(", ");
/*     */       }
/*     */     }
/* 409 */     call.append(");}\n");
/*     */ 
/* 411 */     decl.append(";\n");
/* 412 */     resultArray[0] = decl.toString();
/* 413 */     resultArray[1] = call.toString();
/* 414 */     return resultArray;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Connector-1.1.49.jar
 * Qualified Name:     com.dukascopy.dds2.greed.connector.helpers.ConverterHelpers
 * JD-Core Version:    0.6.0
 */