/*     */ package org.eclipse.jdt.internal.compiler.util;
/*     */ 
/*     */ import java.io.BufferedInputStream;
/*     */ import java.io.BufferedOutputStream;
/*     */ import java.io.BufferedReader;
/*     */ import java.io.ByteArrayInputStream;
/*     */ import java.io.File;
/*     */ import java.io.FileInputStream;
/*     */ import java.io.FileOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.InputStreamReader;
/*     */ import java.io.PrintWriter;
/*     */ import java.io.StringWriter;
/*     */ import java.io.UnsupportedEncodingException;
/*     */ import java.util.HashSet;
/*     */ import java.util.Set;
/*     */ import java.util.StringTokenizer;
/*     */ import java.util.zip.ZipEntry;
/*     */ import java.util.zip.ZipFile;
/*     */ import org.eclipse.jdt.core.compiler.CharOperation;
/*     */ import org.eclipse.jdt.internal.compiler.ClassFile;
/*     */ import org.eclipse.jdt.internal.compiler.ast.TypeDeclaration;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.ParameterizedTypeBinding;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.ReferenceBinding;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.TypeVariableBinding;
/*     */ import org.eclipse.jdt.internal.compiler.lookup.WildcardBinding;
/*     */ 
/*     */ public class Util
/*     */   implements SuffixConstants
/*     */ {
/*     */   private static final int DEFAULT_READING_SIZE = 8192;
/*     */   private static final int DEFAULT_WRITING_SIZE = 1024;
/*     */   public static final String UTF_8 = "UTF-8";
/*  52 */   public static final String LINE_SEPARATOR = System.getProperty("line.separator");
/*     */ 
/*  54 */   public static final String EMPTY_STRING = new String(CharOperation.NO_CHAR);
/*  55 */   public static final int[] EMPTY_INT_ARRAY = new int[0];
/*     */ 
/*     */   public static String buildAllDirectoriesInto(String outputPath, String relativeFileName)
/*     */     throws IOException
/*     */   {
/*  71 */     char fileSeparatorChar = File.separatorChar;
/*  72 */     String fileSeparator = File.separator;
/*     */ 
/*  74 */     outputPath = outputPath.replace('/', fileSeparatorChar);
/*     */ 
/*  77 */     relativeFileName = relativeFileName.replace('/', fileSeparatorChar);
/*     */ 
/*  79 */     int separatorIndex = relativeFileName.lastIndexOf(fileSeparatorChar);
/*     */     String fileName;
/*     */     String outputDirPath;
/*     */     String fileName;
/*  80 */     if (separatorIndex == -1)
/*     */     {
/*     */       String fileName;
/*  81 */       if (outputPath.endsWith(fileSeparator)) {
/*  82 */         String outputDirPath = outputPath.substring(0, outputPath.length() - 1);
/*  83 */         fileName = outputPath + relativeFileName;
/*     */       } else {
/*  85 */         String outputDirPath = outputPath;
/*  86 */         fileName = outputPath + fileSeparator + relativeFileName;
/*     */       }
/*     */     }
/*     */     else
/*     */     {
/*     */       String fileName;
/*  89 */       if (outputPath.endsWith(fileSeparator)) {
/*  90 */         String outputDirPath = outputPath + 
/*  91 */           relativeFileName.substring(0, separatorIndex);
/*  92 */         fileName = outputPath + relativeFileName;
/*     */       } else {
/*  94 */         outputDirPath = outputPath + fileSeparator + 
/*  95 */           relativeFileName.substring(0, separatorIndex);
/*  96 */         fileName = outputPath + fileSeparator + relativeFileName;
/*     */       }
/*     */     }
/*  99 */     File f = new File(outputDirPath);
/* 100 */     f.mkdirs();
/* 101 */     if (f.isDirectory()) {
/* 102 */       return fileName;
/*     */     }
/*     */ 
/* 106 */     if (outputPath.endsWith(fileSeparator)) {
/* 107 */       outputPath = outputPath.substring(0, outputPath.length() - 1);
/*     */     }
/* 109 */     f = new File(outputPath);
/* 110 */     boolean checkFileType = false;
/* 111 */     if (f.exists()) {
/* 112 */       checkFileType = true;
/*     */     }
/* 115 */     else if (!f.mkdirs()) {
/* 116 */       if (f.exists())
/*     */       {
/* 118 */         checkFileType = true;
/*     */       }
/*     */       else {
/* 121 */         throw new IOException(Messages.bind(
/* 122 */           Messages.output_notValidAll, f.getAbsolutePath()));
/*     */       }
/*     */     }
/*     */ 
/* 126 */     if ((checkFileType) && 
/* 127 */       (!f.isDirectory())) {
/* 128 */       throw new IOException(Messages.bind(
/* 129 */         Messages.output_isFile, f.getAbsolutePath()));
/*     */     }
/*     */ 
/* 132 */     StringBuffer outDir = new StringBuffer(outputPath);
/* 133 */     outDir.append(fileSeparator);
/* 134 */     StringTokenizer tokenizer = 
/* 135 */       new StringTokenizer(relativeFileName, fileSeparator);
/* 136 */     String token = tokenizer.nextToken();
/* 137 */     while (tokenizer.hasMoreTokens()) {
/* 138 */       f = new File(token + fileSeparator);
/* 139 */       checkFileType = false;
/* 140 */       if (f.exists()) {
/* 141 */         checkFileType = true;
/*     */       }
/* 145 */       else if (!f.mkdir()) {
/* 146 */         if (f.exists())
/*     */         {
/* 148 */           checkFileType = true;
/*     */         }
/*     */         else {
/* 151 */           throw new IOException(Messages.bind(
/* 152 */             Messages.output_notValid, 
/* 153 */             outDir.substring(outputPath.length() + 1, 
/* 154 */             outDir.length() - 1), 
/* 155 */             outputPath));
/*     */         }
/*     */       }
/*     */ 
/* 159 */       if ((checkFileType) && 
/* 160 */         (!f.isDirectory())) {
/* 161 */         throw new IOException(Messages.bind(
/* 162 */           Messages.output_isFile, f.getAbsolutePath()));
/*     */       }
/*     */ 
/* 165 */       token = tokenizer.nextToken();
/*     */     }
/*     */ 
/* 168 */     return token;
/*     */   }
/*     */ 
/*     */   public static char[] bytesToChar(byte[] bytes, String encoding)
/*     */     throws IOException
/*     */   {
/* 177 */     return getInputStreamAsCharArray(new ByteArrayInputStream(bytes), bytes.length, encoding);
/*     */   }
/*     */ 
/*     */   public static int computeOuterMostVisibility(TypeDeclaration typeDeclaration, int visibility)
/*     */   {
/* 186 */     while (typeDeclaration != null) {
/* 187 */       switch (typeDeclaration.modifiers & 0x7) {
/*     */       case 2:
/* 189 */         visibility = 2;
/* 190 */         break;
/*     */       case 0:
/* 192 */         if (visibility == 2) break;
/* 193 */         visibility = 0;
/*     */ 
/* 195 */         break;
/*     */       case 4:
/* 197 */         if (visibility != 1) break;
/* 198 */         visibility = 4;
/*     */       case 1:
/*     */       case 3:
/*     */       }
/* 202 */       typeDeclaration = typeDeclaration.enclosingType;
/*     */     }
/*     */ 
/* 204 */     return visibility;
/*     */   }
/*     */ 
/*     */   public static byte[] getFileByteContent(File file)
/*     */     throws IOException
/*     */   {
/* 211 */     InputStream stream = null;
/*     */     try {
/* 213 */       stream = new BufferedInputStream(new FileInputStream(file));
/* 214 */       byte[] arrayOfByte = getInputStreamAsByteArray(stream, (int)file.length());
/*     */       return arrayOfByte;
/*     */     } finally {
/* 216 */       if (stream != null)
/*     */         try {
/* 218 */           stream.close();
/*     */         }
/*     */         catch (IOException localIOException2) {
/*     */         }
/*     */     }
/* 223 */     throw localObject;
/*     */   }
/*     */ 
/*     */   public static char[] getFileCharContent(File file, String encoding)
/*     */     throws IOException
/*     */   {
/* 231 */     InputStream stream = null;
/*     */     try {
/* 233 */       stream = new FileInputStream(file);
/* 234 */       char[] arrayOfChar = getInputStreamAsCharArray(stream, (int)file.length(), encoding);
/*     */       return arrayOfChar;
/*     */     } finally {
/* 236 */       if (stream != null)
/*     */         try {
/* 238 */           stream.close();
/*     */         }
/*     */         catch (IOException localIOException2) {
/*     */         }
/*     */     }
/* 243 */     throw localObject;
/*     */   }
/*     */   private static FileOutputStream getFileOutputStream(boolean generatePackagesStructure, String outputPath, String relativeFileName) throws IOException {
/* 246 */     if (generatePackagesStructure) {
/* 247 */       return new FileOutputStream(new File(buildAllDirectoriesInto(outputPath, relativeFileName)));
/*     */     }
/* 249 */     String fileName = null;
/* 250 */     char fileSeparatorChar = File.separatorChar;
/* 251 */     String fileSeparator = File.separator;
/*     */ 
/* 253 */     outputPath = outputPath.replace('/', fileSeparatorChar);
/*     */ 
/* 255 */     int indexOfPackageSeparator = relativeFileName.lastIndexOf(fileSeparatorChar);
/* 256 */     if (indexOfPackageSeparator == -1) {
/* 257 */       if (outputPath.endsWith(fileSeparator))
/* 258 */         fileName = outputPath + relativeFileName;
/*     */       else
/* 260 */         fileName = outputPath + fileSeparator + relativeFileName;
/*     */     }
/*     */     else {
/* 263 */       int length = relativeFileName.length();
/* 264 */       if (outputPath.endsWith(fileSeparator))
/* 265 */         fileName = outputPath + relativeFileName.substring(indexOfPackageSeparator + 1, length);
/*     */       else {
/* 267 */         fileName = outputPath + fileSeparator + relativeFileName.substring(indexOfPackageSeparator + 1, length);
/*     */       }
/*     */     }
/* 270 */     return new FileOutputStream(new File(fileName));
/*     */   }
/*     */ 
/*     */   public static byte[] getInputStreamAsByteArray(InputStream stream, int length)
/*     */     throws IOException
/*     */   {
/*     */     byte[] contents;
/* 302 */     if (length == -1) {
/* 303 */       byte[] contents = new byte[0];
/* 304 */       int contentsLength = 0;
/* 305 */       int amountRead = -1;
/*     */       do {
/* 307 */         int amountRequested = Math.max(stream.available(), 8192);
/*     */ 
/* 310 */         if (contentsLength + amountRequested > contents.length) {
/* 311 */           System.arraycopy(
/* 312 */             contents, 
/* 313 */             0, 
/* 314 */             contents = new byte[contentsLength + amountRequested], 
/* 315 */             0, 
/* 316 */             contentsLength);
/*     */         }
/*     */ 
/* 320 */         amountRead = stream.read(contents, contentsLength, amountRequested);
/*     */ 
/* 322 */         if (amountRead <= 0)
/*     */           continue;
/* 324 */         contentsLength += amountRead;
/*     */       }
/* 326 */       while (amountRead != -1);
/*     */ 
/* 329 */       if (contentsLength < contents.length)
/* 330 */         System.arraycopy(
/* 331 */           contents, 
/* 332 */           0, 
/* 333 */           contents = new byte[contentsLength], 
/* 334 */           0, 
/* 335 */           contentsLength);
/*     */     }
/*     */     else {
/* 338 */       contents = new byte[length];
/* 339 */       int len = 0;
/* 340 */       int readSize = 0;
/* 341 */       while ((readSize != -1) && (len != length))
/*     */       {
/* 344 */         len += readSize;
/* 345 */         readSize = stream.read(contents, len, length - len);
/*     */       }
/*     */     }
/*     */ 
/* 349 */     return contents;
/*     */   }
/*     */ 
/*     */   public static char[] getInputStreamAsCharArray(InputStream stream, int length, String encoding)
/*     */     throws IOException
/*     */   {
/* 383 */     BufferedReader reader = null;
/*     */     try {
/* 385 */       reader = encoding == null ? 
/* 386 */         new BufferedReader(new InputStreamReader(stream)) : 
/* 387 */         new BufferedReader(new InputStreamReader(stream, encoding));
/*     */     }
/*     */     catch (UnsupportedEncodingException localUnsupportedEncodingException) {
/* 390 */       reader = new BufferedReader(new InputStreamReader(stream));
/*     */     }
/*     */ 
/* 393 */     int totalRead = 0;
/*     */     char[] contents;
/*     */     char[] contents;
/* 394 */     if (length == -1) {
/* 395 */       contents = CharOperation.NO_CHAR;
/*     */     }
/*     */     else
/* 398 */       contents = new char[length];
/*     */     while (true)
/*     */     {
/*     */       int amountRequested;
/*     */       int amountRequested;
/* 403 */       if (totalRead < length)
/*     */       {
/* 405 */         amountRequested = length - totalRead;
/*     */       }
/*     */       else {
/* 408 */         int current = reader.read();
/* 409 */         if (current < 0)
/*     */           break;
/* 411 */         amountRequested = Math.max(stream.available(), 8192);
/*     */ 
/* 414 */         if (totalRead + 1 + amountRequested > contents.length) {
/* 415 */           System.arraycopy(contents, 0, contents = new char[totalRead + 1 + amountRequested], 0, totalRead);
/*     */         }
/*     */ 
/* 418 */         contents[(totalRead++)] = (char)current;
/*     */       }
/*     */ 
/* 421 */       int amountRead = reader.read(contents, totalRead, amountRequested);
/* 422 */       if (amountRead < 0) break;
/* 423 */       totalRead += amountRead;
/*     */     }
/*     */ 
/* 427 */     int start = 0;
/* 428 */     if ((totalRead > 0) && ("UTF-8".equals(encoding)) && 
/* 429 */       (contents[0] == 65279)) {
/* 430 */       totalRead--;
/* 431 */       start = 1;
/*     */     }
/*     */ 
/* 436 */     if (totalRead < contents.length) {
/* 437 */       System.arraycopy(contents, start, contents = new char[totalRead], 0, totalRead);
/*     */     }
/* 439 */     return contents;
/*     */   }
/*     */ 
/*     */   public static String getExceptionSummary(Throwable exception)
/*     */   {
/* 448 */     StringWriter stringWriter = new StringWriter();
/* 449 */     exception.printStackTrace(new PrintWriter(stringWriter));
/* 450 */     StringBuffer buffer = stringWriter.getBuffer();
/* 451 */     StringBuffer exceptionBuffer = new StringBuffer(50);
/* 452 */     exceptionBuffer.append(exception.toString());
/*     */ 
/* 454 */     int i = 0; int lineSep = 0; int max = buffer.length(); for (int line2Start = 0; i < max; i++) {
/* 455 */       switch (buffer.charAt(i)) {
/*     */       case '\n':
/*     */       case '\r':
/* 458 */         if (line2Start > 0) {
/* 459 */           exceptionBuffer.append(' ').append(buffer.substring(line2Start, i));
/* 460 */           break label169;
/*     */         }
/* 462 */         lineSep++;
/* 463 */         break;
/*     */       case '\t':
/*     */       case ' ':
/* 466 */         break;
/*     */       default:
/* 468 */         if (lineSep <= 0) continue;
/* 469 */         line2Start = i;
/* 470 */         lineSep = 0;
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 475 */     label169: return exceptionBuffer.toString();
/*     */   }
/*     */ 
/*     */   public static int getLineNumber(int position, int[] lineEnds, int g, int d) {
/* 479 */     if (lineEnds == null)
/* 480 */       return 1;
/* 481 */     if (d == -1)
/* 482 */       return 1;
/* 483 */     int m = g;
/* 484 */     while (g <= d) {
/* 485 */       m = g + (d - g) / 2;
/*     */       int start;
/* 486 */       if (position < (start = lineEnds[m]))
/* 487 */         d = m - 1;
/* 488 */       else if (position > start)
/* 489 */         g = m + 1;
/*     */       else {
/* 491 */         return m + 1;
/*     */       }
/*     */     }
/* 494 */     if (position < lineEnds[m]) {
/* 495 */       return m + 1;
/*     */     }
/* 497 */     return m + 2;
/*     */   }
/*     */ 
/*     */   public static byte[] getZipEntryByteContent(ZipEntry ze, ZipFile zip)
/*     */     throws IOException
/*     */   {
/* 506 */     InputStream stream = null;
/*     */     try {
/* 508 */       InputStream inputStream = zip.getInputStream(ze);
/* 509 */       if (inputStream == null) throw new IOException("Invalid zip entry name : " + ze.getName());
/* 510 */       stream = new BufferedInputStream(inputStream);
/* 511 */       byte[] arrayOfByte = getInputStreamAsByteArray(stream, (int)ze.getSize());
/*     */       return arrayOfByte;
/*     */     } finally {
/* 513 */       if (stream != null)
/*     */         try {
/* 515 */           stream.close();
/*     */         }
/*     */         catch (IOException localIOException2) {
/*     */         }
/*     */     }
/* 520 */     throw localObject;
/*     */   }
/*     */ 
/*     */   public static final boolean isPotentialZipArchive(String name)
/*     */   {
/* 528 */     int lastDot = name.lastIndexOf('.');
/* 529 */     if (lastDot == -1)
/* 530 */       return false;
/* 531 */     if (name.lastIndexOf(File.separatorChar) > lastDot)
/* 532 */       return false;
/* 533 */     int length = name.length();
/* 534 */     int extensionLength = length - lastDot - 1;
/* 535 */     if (extensionLength == "java".length()) {
/* 536 */       for (int i = extensionLength - 1; i >= 0; i--) {
/* 537 */         if (Character.toLowerCase(name.charAt(length - extensionLength + i)) != "java".charAt(i)) {
/*     */           break;
/*     */         }
/* 540 */         if (i == 0) {
/* 541 */           return false;
/*     */         }
/*     */       }
/*     */     }
/* 545 */     if (extensionLength == "class".length()) {
/* 546 */       for (int i = extensionLength - 1; i >= 0; i--) {
/* 547 */         if (Character.toLowerCase(name.charAt(length - extensionLength + i)) != "class".charAt(i)) {
/* 548 */           return true;
/*     */         }
/*     */       }
/* 551 */       return false;
/*     */     }
/* 553 */     return true;
/*     */   }
/*     */ 
/*     */   public static final boolean isClassFileName(char[] name)
/*     */   {
/* 561 */     int nameLength = name == null ? 0 : name.length;
/* 562 */     int suffixLength = SUFFIX_CLASS.length;
/* 563 */     if (nameLength < suffixLength) return false;
/*     */ 
/* 565 */     int i = 0; for (int offset = nameLength - suffixLength; i < suffixLength; i++) {
/* 566 */       char c = name[(offset + i)];
/* 567 */       if ((c != SUFFIX_class[i]) && (c != SUFFIX_CLASS[i])) return false;
/*     */     }
/* 569 */     return true;
/*     */   }
/*     */ 
/*     */   public static final boolean isClassFileName(String name)
/*     */   {
/* 576 */     int nameLength = name == null ? 0 : name.length();
/* 577 */     int suffixLength = SUFFIX_CLASS.length;
/* 578 */     if (nameLength < suffixLength) return false;
/*     */ 
/* 580 */     for (int i = 0; i < suffixLength; i++) {
/* 581 */       char c = name.charAt(nameLength - i - 1);
/* 582 */       int suffixIndex = suffixLength - i - 1;
/* 583 */       if ((c != SUFFIX_class[suffixIndex]) && (c != SUFFIX_CLASS[suffixIndex])) return false;
/*     */     }
/* 585 */     return true;
/*     */   }
/*     */ 
/*     */   public static final boolean isExcluded(char[] path, char[][] inclusionPatterns, char[][] exclusionPatterns, boolean isFolderPath)
/*     */   {
/* 595 */     if ((inclusionPatterns == null) && (exclusionPatterns == null)) return false;
/*     */ 
/* 597 */     if (inclusionPatterns != null) {
/* 598 */       int i = 0; int length = inclusionPatterns.length;
/*     */       while (true) { char[] pattern = inclusionPatterns[i];
/* 600 */         char[] folderPattern = pattern;
/* 601 */         if (isFolderPath) {
/* 602 */           int lastSlash = CharOperation.lastIndexOf('/', pattern);
/* 603 */           if ((lastSlash != -1) && (lastSlash != pattern.length - 1)) {
/* 604 */             int star = CharOperation.indexOf('*', pattern, lastSlash);
/* 605 */             if ((star == -1) || 
/* 606 */               (star >= pattern.length - 1) || 
/* 607 */               (pattern[(star + 1)] != '*')) {
/* 608 */               folderPattern = CharOperation.subarray(pattern, 0, lastSlash);
/*     */             }
/*     */           }
/*     */         }
/* 612 */         if (CharOperation.pathMatch(folderPattern, path, true, '/'))
/*     */           break;
/* 598 */         i++; if (i >= length)
/*     */         {
/* 616 */           return true;
/*     */         } }
/*     */     }
/* 618 */     if (isFolderPath) {
/* 619 */       path = CharOperation.concat(path, new char[] { '*' }, '/');
/*     */     }
/* 621 */     if (exclusionPatterns != null) {
/* 622 */       int i = 0; for (int length = exclusionPatterns.length; i < length; i++) {
/* 623 */         if (CharOperation.pathMatch(exclusionPatterns[i], path, true, '/')) {
/* 624 */           return true;
/*     */         }
/*     */       }
/*     */     }
/* 628 */     return false;
/*     */   }
/*     */ 
/*     */   public static final boolean isJavaFileName(char[] name)
/*     */   {
/* 636 */     int nameLength = name == null ? 0 : name.length;
/* 637 */     int suffixLength = SUFFIX_JAVA.length;
/* 638 */     if (nameLength < suffixLength) return false;
/*     */ 
/* 640 */     int i = 0; for (int offset = nameLength - suffixLength; i < suffixLength; i++) {
/* 641 */       char c = name[(offset + i)];
/* 642 */       if ((c != SUFFIX_java[i]) && (c != SUFFIX_JAVA[i])) return false;
/*     */     }
/* 644 */     return true;
/*     */   }
/*     */ 
/*     */   public static final boolean isJavaFileName(String name)
/*     */   {
/* 652 */     int nameLength = name == null ? 0 : name.length();
/* 653 */     int suffixLength = SUFFIX_JAVA.length;
/* 654 */     if (nameLength < suffixLength) return false;
/*     */ 
/* 656 */     for (int i = 0; i < suffixLength; i++) {
/* 657 */       char c = name.charAt(nameLength - i - 1);
/* 658 */       int suffixIndex = suffixLength - i - 1;
/* 659 */       if ((c != SUFFIX_java[suffixIndex]) && (c != SUFFIX_JAVA[suffixIndex])) return false;
/*     */     }
/* 661 */     return true;
/*     */   }
/*     */ 
/*     */   public static void reverseQuickSort(char[][] list, int left, int right) {
/* 665 */     int original_left = left;
/* 666 */     int original_right = right;
/* 667 */     char[] mid = list[((right + left) / 2)];
/*     */     do {
/* 669 */       while (CharOperation.compareTo(list[left], mid) > 0) {
/* 670 */         left++;
/*     */       }
/* 672 */       while (CharOperation.compareTo(mid, list[right]) > 0) {
/* 673 */         right--;
/*     */       }
/* 675 */       if (left <= right) {
/* 676 */         char[] tmp = list[left];
/* 677 */         list[left] = list[right];
/* 678 */         list[right] = tmp;
/* 679 */         left++;
/* 680 */         right--;
/*     */       }
/*     */     }
/* 682 */     while (left <= right);
/* 683 */     if (original_left < right) {
/* 684 */       reverseQuickSort(list, original_left, right);
/*     */     }
/* 686 */     if (left < original_right)
/* 687 */       reverseQuickSort(list, left, original_right);
/*     */   }
/*     */ 
/*     */   public static void reverseQuickSort(char[][] list, int left, int right, int[] result) {
/* 691 */     int original_left = left;
/* 692 */     int original_right = right;
/* 693 */     char[] mid = list[((right + left) / 2)];
/*     */     do {
/* 695 */       while (CharOperation.compareTo(list[left], mid) > 0) {
/* 696 */         left++;
/*     */       }
/* 698 */       while (CharOperation.compareTo(mid, list[right]) > 0) {
/* 699 */         right--;
/*     */       }
/* 701 */       if (left <= right) {
/* 702 */         char[] tmp = list[left];
/* 703 */         list[left] = list[right];
/* 704 */         list[right] = tmp;
/* 705 */         int temp = result[left];
/* 706 */         result[left] = result[right];
/* 707 */         result[right] = temp;
/* 708 */         left++;
/* 709 */         right--;
/*     */       }
/*     */     }
/* 711 */     while (left <= right);
/* 712 */     if (original_left < right) {
/* 713 */       reverseQuickSort(list, original_left, right, result);
/*     */     }
/* 715 */     if (left < original_right)
/* 716 */       reverseQuickSort(list, left, original_right, result);
/*     */   }
/*     */ 
/*     */   public static final int searchColumnNumber(int[] startLineIndexes, int lineNumber, int position)
/*     */   {
/* 724 */     switch (lineNumber) {
/*     */     case 1:
/* 726 */       return position + 1;
/*     */     case 2:
/* 728 */       return position - startLineIndexes[0];
/*     */     }
/* 730 */     int line = lineNumber - 2;
/* 731 */     int length = startLineIndexes.length;
/* 732 */     if (line >= length) {
/* 733 */       return position - startLineIndexes[(length - 1)];
/*     */     }
/* 735 */     return position - startLineIndexes[line];
/*     */   }
/*     */ 
/*     */   public static Boolean toBoolean(boolean bool)
/*     */   {
/* 745 */     if (bool) {
/* 746 */       return Boolean.TRUE;
/*     */     }
/* 748 */     return Boolean.FALSE;
/*     */   }
/*     */ 
/*     */   public static String toString(Object[] objects)
/*     */   {
/* 755 */     return toString(objects, 
/* 756 */       new Displayable() {
/*     */       public String displayString(Object o) {
/* 758 */         if (o == null) return "null";
/* 759 */         return o.toString();
/*     */       }
/*     */     });
/*     */   }
/*     */ 
/*     */   public static String toString(Object[] objects, Displayable renderer)
/*     */   {
/* 768 */     if (objects == null) return "";
/* 769 */     StringBuffer buffer = new StringBuffer(10);
/* 770 */     for (int i = 0; i < objects.length; i++) {
/* 771 */       if (i > 0) buffer.append(", ");
/* 772 */       buffer.append(renderer.displayString(objects[i]));
/*     */     }
/* 774 */     return buffer.toString();
/*     */   }
/*     */ 
/*     */   public static void writeToDisk(boolean generatePackagesStructure, String outputPath, String relativeFileName, ClassFile classFile)
/*     */     throws IOException
/*     */   {
/* 789 */     FileOutputStream file = getFileOutputStream(generatePackagesStructure, outputPath, relativeFileName);
/*     */ 
/* 807 */     BufferedOutputStream output = new BufferedOutputStream(file, 1024);
/*     */     try
/*     */     {
/* 811 */       output.write(classFile.header, 0, classFile.headerOffset);
/* 812 */       output.write(classFile.contents, 0, classFile.contentsOffset);
/* 813 */       output.flush();
/*     */     } catch (IOException e) {
/* 815 */       throw e;
/*     */     } finally {
/* 817 */       output.close();
/*     */     }
/*     */   }
/*     */ 
/*     */   public static void recordNestedType(ClassFile classFile, TypeBinding typeBinding) {
/* 821 */     if (classFile.visitedTypes == null)
/* 822 */       classFile.visitedTypes = new HashSet(3);
/* 823 */     else if (classFile.visitedTypes.contains(typeBinding))
/*     */     {
/* 825 */       return;
/*     */     }
/* 827 */     classFile.visitedTypes.add(typeBinding);
/* 828 */     if ((typeBinding.isParameterizedType()) && 
/* 829 */       ((typeBinding.tagBits & 0x800) != 0L)) {
/* 830 */       ParameterizedTypeBinding parameterizedTypeBinding = (ParameterizedTypeBinding)typeBinding;
/* 831 */       ReferenceBinding genericType = parameterizedTypeBinding.genericType();
/* 832 */       if ((genericType.tagBits & 0x800) != 0L) {
/* 833 */         recordNestedType(classFile, genericType);
/*     */       }
/* 835 */       TypeBinding[] arguments = parameterizedTypeBinding.arguments;
/* 836 */       if (arguments != null) {
/* 837 */         int j = 0; for (int max2 = arguments.length; j < max2; j++) {
/* 838 */           TypeBinding argument = arguments[j];
/* 839 */           if (argument.isWildcard()) {
/* 840 */             WildcardBinding wildcardBinding = (WildcardBinding)argument;
/* 841 */             TypeBinding bound = wildcardBinding.bound;
/* 842 */             if ((bound != null) && 
/* 843 */               ((bound.tagBits & 0x800) != 0L)) {
/* 844 */               recordNestedType(classFile, bound);
/*     */             }
/* 846 */             ReferenceBinding superclass = wildcardBinding.superclass();
/* 847 */             if ((superclass != null) && 
/* 848 */               ((superclass.tagBits & 0x800) != 0L)) {
/* 849 */               recordNestedType(classFile, superclass);
/*     */             }
/* 851 */             ReferenceBinding[] superInterfaces = wildcardBinding.superInterfaces();
/* 852 */             if (superInterfaces != null) {
/* 853 */               int k = 0; for (int max3 = superInterfaces.length; k < max3; k++) {
/* 854 */                 ReferenceBinding superInterface = superInterfaces[k];
/* 855 */                 if ((superInterface.tagBits & 0x800) != 0L)
/* 856 */                   recordNestedType(classFile, superInterface);
/*     */               }
/*     */             }
/*     */           }
/* 860 */           else if ((argument.tagBits & 0x800) != 0L) {
/* 861 */             recordNestedType(classFile, argument);
/*     */           }
/*     */         }
/*     */       }
/* 865 */     } else if ((typeBinding.isTypeVariable()) && 
/* 866 */       ((typeBinding.tagBits & 0x800) != 0L)) {
/* 867 */       TypeVariableBinding typeVariableBinding = (TypeVariableBinding)typeBinding;
/* 868 */       TypeBinding upperBound = typeVariableBinding.upperBound();
/* 869 */       if ((upperBound != null) && ((upperBound.tagBits & 0x800) != 0L)) {
/* 870 */         recordNestedType(classFile, upperBound);
/*     */       }
/* 872 */       TypeBinding[] upperBounds = typeVariableBinding.otherUpperBounds();
/* 873 */       if (upperBounds != null) {
/* 874 */         int k = 0; for (int max3 = upperBounds.length; k < max3; k++) {
/* 875 */           TypeBinding otherUpperBound = upperBounds[k];
/* 876 */           if ((otherUpperBound.tagBits & 0x800) != 0L)
/* 877 */             recordNestedType(classFile, otherUpperBound);
/*     */         }
/*     */       }
/*     */     }
/* 881 */     else if (typeBinding.isNestedType()) {
/* 882 */       classFile.recordInnerClasses(typeBinding);
/*     */     }
/*     */   }
/*     */ 
/*     */   public static abstract interface Displayable
/*     */   {
/*     */     public abstract String displayString(Object paramObject);
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\ecj-3.5.2.jar
 * Qualified Name:     org.eclipse.jdt.internal.compiler.util.Util
 * JD-Core Version:    0.6.0
 */