/*      */ package org.eclipse.jdt.internal.compiler.classfmt;
/*      */ 
/*      */ import java.io.ByteArrayOutputStream;
/*      */ import java.io.File;
/*      */ import java.io.IOException;
/*      */ import java.io.InputStream;
/*      */ import java.io.PrintWriter;
/*      */ import java.util.Arrays;
/*      */ import java.util.zip.ZipEntry;
/*      */ import java.util.zip.ZipFile;
/*      */ import org.eclipse.jdt.core.compiler.CharOperation;
/*      */ import org.eclipse.jdt.internal.compiler.codegen.AttributeNamesConstants;
/*      */ import org.eclipse.jdt.internal.compiler.env.IBinaryAnnotation;
/*      */ import org.eclipse.jdt.internal.compiler.env.IBinaryElementValuePair;
/*      */ import org.eclipse.jdt.internal.compiler.env.IBinaryField;
/*      */ import org.eclipse.jdt.internal.compiler.env.IBinaryMethod;
/*      */ import org.eclipse.jdt.internal.compiler.env.IBinaryNestedType;
/*      */ import org.eclipse.jdt.internal.compiler.env.IBinaryType;
/*      */ import org.eclipse.jdt.internal.compiler.impl.Constant;
/*      */ import org.eclipse.jdt.internal.compiler.util.Util;
/*      */ 
/*      */ public class ClassFileReader extends ClassFileStruct
/*      */   implements IBinaryType
/*      */ {
/*      */   private int accessFlags;
/*      */   private char[] classFileName;
/*      */   private char[] className;
/*      */   private int classNameIndex;
/*      */   private int constantPoolCount;
/*      */   private AnnotationInfo[] annotations;
/*      */   private FieldInfo[] fields;
/*      */   private int fieldsCount;
/*      */   private InnerClassInfo innerInfo;
/*      */   private int innerInfoIndex;
/*      */   private InnerClassInfo[] innerInfos;
/*      */   private char[][] interfaceNames;
/*      */   private int interfacesCount;
/*      */   private MethodInfo[] methods;
/*      */   private int methodsCount;
/*      */   private char[] signature;
/*      */   private char[] sourceName;
/*      */   private char[] sourceFileName;
/*      */   private char[] superclassName;
/*      */   private long tagBits;
/*      */   private long version;
/*      */   private char[] enclosingTypeName;
/*      */   private char[][][] missingTypeNames;
/*      */   private int enclosingNameAndTypeIndex;
/*      */   private char[] enclosingMethod;
/*      */ 
/*      */   private static String printTypeModifiers(int modifiers)
/*      */   {
/*   57 */     ByteArrayOutputStream out = new ByteArrayOutputStream();
/*   58 */     PrintWriter print = new PrintWriter(out);
/*      */ 
/*   60 */     if ((modifiers & 0x1) != 0) print.print("public ");
/*   61 */     if ((modifiers & 0x2) != 0) print.print("private ");
/*   62 */     if ((modifiers & 0x10) != 0) print.print("final ");
/*   63 */     if ((modifiers & 0x20) != 0) print.print("super ");
/*   64 */     if ((modifiers & 0x200) != 0) print.print("interface ");
/*   65 */     if ((modifiers & 0x400) != 0) print.print("abstract ");
/*   66 */     print.flush();
/*   67 */     return out.toString();
/*      */   }
/*      */ 
/*      */   public static ClassFileReader read(File file) throws ClassFormatException, IOException {
/*   71 */     return read(file, false);
/*      */   }
/*      */ 
/*      */   public static ClassFileReader read(File file, boolean fullyInitialize) throws ClassFormatException, IOException {
/*   75 */     byte[] classFileBytes = Util.getFileByteContent(file);
/*   76 */     ClassFileReader classFileReader = new ClassFileReader(classFileBytes, file.getAbsolutePath().toCharArray());
/*   77 */     if (fullyInitialize) {
/*   78 */       classFileReader.initialize();
/*      */     }
/*   80 */     return classFileReader;
/*      */   }
/*      */ 
/*      */   public static ClassFileReader read(InputStream stream, String fileName) throws ClassFormatException, IOException {
/*   84 */     return read(stream, fileName, false);
/*      */   }
/*      */ 
/*      */   public static ClassFileReader read(InputStream stream, String fileName, boolean fullyInitialize) throws ClassFormatException, IOException {
/*   88 */     byte[] classFileBytes = Util.getInputStreamAsByteArray(stream, -1);
/*   89 */     ClassFileReader classFileReader = new ClassFileReader(classFileBytes, fileName.toCharArray());
/*   90 */     if (fullyInitialize) {
/*   91 */       classFileReader.initialize();
/*      */     }
/*   93 */     return classFileReader;
/*      */   }
/*      */ 
/*      */   public static ClassFileReader read(ZipFile zip, String filename)
/*      */     throws ClassFormatException, IOException
/*      */   {
/*  100 */     return read(zip, filename, false);
/*      */   }
/*      */ 
/*      */   public static ClassFileReader read(ZipFile zip, String filename, boolean fullyInitialize)
/*      */     throws ClassFormatException, IOException
/*      */   {
/*  108 */     ZipEntry ze = zip.getEntry(filename);
/*  109 */     if (ze == null)
/*  110 */       return null;
/*  111 */     byte[] classFileBytes = Util.getZipEntryByteContent(ze, zip);
/*  112 */     ClassFileReader classFileReader = new ClassFileReader(classFileBytes, filename.toCharArray());
/*  113 */     if (fullyInitialize) {
/*  114 */       classFileReader.initialize();
/*      */     }
/*  116 */     return classFileReader;
/*      */   }
/*      */ 
/*      */   public static ClassFileReader read(String fileName) throws ClassFormatException, IOException {
/*  120 */     return read(fileName, false);
/*      */   }
/*      */ 
/*      */   public static ClassFileReader read(String fileName, boolean fullyInitialize) throws ClassFormatException, IOException {
/*  124 */     return read(new File(fileName), fullyInitialize);
/*      */   }
/*      */ 
/*      */   public ClassFileReader(byte[] classFileBytes, char[] fileName)
/*      */     throws ClassFormatException
/*      */   {
/*  134 */     this(classFileBytes, fileName, false);
/*      */   }
/*      */ 
/*      */   public ClassFileReader(byte[] classFileBytes, char[] fileName, boolean fullyInitialize)
/*      */     throws ClassFormatException
/*      */   {
/*  153 */     super(classFileBytes, null, 0);
/*  154 */     this.classFileName = fileName;
/*  155 */     int readOffset = 10;
/*      */     try {
/*  157 */       this.version = ((u2At(6) << 16) + u2At(4));
/*  158 */       this.constantPoolCount = u2At(8);
/*      */ 
/*  160 */       this.constantPoolOffsets = new int[this.constantPoolCount];
/*  161 */       for (int i = 1; i < this.constantPoolCount; i++) {
/*  162 */         int tag = u1At(readOffset);
/*  163 */         switch (tag) {
/*      */         case 1:
/*  165 */           this.constantPoolOffsets[i] = readOffset;
/*  166 */           readOffset += u2At(readOffset + 1);
/*  167 */           readOffset += 3;
/*  168 */           break;
/*      */         case 3:
/*  170 */           this.constantPoolOffsets[i] = readOffset;
/*  171 */           readOffset += 5;
/*  172 */           break;
/*      */         case 4:
/*  174 */           this.constantPoolOffsets[i] = readOffset;
/*  175 */           readOffset += 5;
/*  176 */           break;
/*      */         case 5:
/*  178 */           this.constantPoolOffsets[i] = readOffset;
/*  179 */           readOffset += 9;
/*  180 */           i++;
/*  181 */           break;
/*      */         case 6:
/*  183 */           this.constantPoolOffsets[i] = readOffset;
/*  184 */           readOffset += 9;
/*  185 */           i++;
/*  186 */           break;
/*      */         case 7:
/*  188 */           this.constantPoolOffsets[i] = readOffset;
/*  189 */           readOffset += 3;
/*  190 */           break;
/*      */         case 8:
/*  192 */           this.constantPoolOffsets[i] = readOffset;
/*  193 */           readOffset += 3;
/*  194 */           break;
/*      */         case 9:
/*  196 */           this.constantPoolOffsets[i] = readOffset;
/*  197 */           readOffset += 5;
/*  198 */           break;
/*      */         case 10:
/*  200 */           this.constantPoolOffsets[i] = readOffset;
/*  201 */           readOffset += 5;
/*  202 */           break;
/*      */         case 11:
/*  204 */           this.constantPoolOffsets[i] = readOffset;
/*  205 */           readOffset += 5;
/*  206 */           break;
/*      */         case 12:
/*  208 */           this.constantPoolOffsets[i] = readOffset;
/*  209 */           readOffset += 5;
/*      */         case 2:
/*      */         }
/*      */       }
/*  213 */       this.accessFlags = u2At(readOffset);
/*  214 */       readOffset += 2;
/*      */ 
/*  217 */       this.classNameIndex = u2At(readOffset);
/*  218 */       this.className = getConstantClassNameAt(this.classNameIndex);
/*  219 */       readOffset += 2;
/*      */ 
/*  222 */       int superclassNameIndex = u2At(readOffset);
/*  223 */       readOffset += 2;
/*      */ 
/*  226 */       if (superclassNameIndex != 0) {
/*  227 */         this.superclassName = getConstantClassNameAt(superclassNameIndex);
/*      */       }
/*      */ 
/*  231 */       this.interfacesCount = u2At(readOffset);
/*  232 */       readOffset += 2;
/*  233 */       if (this.interfacesCount != 0) {
/*  234 */         this.interfaceNames = new char[this.interfacesCount][];
/*  235 */         for (int i = 0; i < this.interfacesCount; i++) {
/*  236 */           this.interfaceNames[i] = getConstantClassNameAt(u2At(readOffset));
/*  237 */           readOffset += 2;
/*      */         }
/*      */       }
/*      */ 
/*  241 */       this.fieldsCount = u2At(readOffset);
/*  242 */       readOffset += 2;
/*  243 */       if (this.fieldsCount != 0)
/*      */       {
/*  245 */         this.fields = new FieldInfo[this.fieldsCount];
/*  246 */         for (int i = 0; i < this.fieldsCount; i++) {
/*  247 */           FieldInfo field = FieldInfo.createField(this.reference, this.constantPoolOffsets, readOffset);
/*  248 */           this.fields[i] = field;
/*  249 */           readOffset += field.sizeInBytes();
/*      */         }
/*      */       }
/*      */ 
/*  253 */       this.methodsCount = u2At(readOffset);
/*  254 */       readOffset += 2;
/*  255 */       if (this.methodsCount != 0) {
/*  256 */         this.methods = new MethodInfo[this.methodsCount];
/*  257 */         boolean isAnnotationType = (this.accessFlags & 0x2000) != 0;
/*  258 */         for (int i = 0; i < this.methodsCount; i++) {
/*  259 */           this.methods[i] = (isAnnotationType ? 
/*  260 */             AnnotationMethodInfo.createAnnotationMethod(this.reference, this.constantPoolOffsets, readOffset) : 
/*  261 */             MethodInfo.createMethod(this.reference, this.constantPoolOffsets, readOffset));
/*  262 */           readOffset += this.methods[i].sizeInBytes();
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/*  267 */       int attributesCount = u2At(readOffset);
/*  268 */       readOffset += 2;
/*      */ 
/*  270 */       for (int i = 0; i < attributesCount; i++) {
/*  271 */         int utf8Offset = this.constantPoolOffsets[u2At(readOffset)];
/*  272 */         char[] attributeName = utf8At(utf8Offset + 3, u2At(utf8Offset + 1));
/*  273 */         if (attributeName.length == 0) {
/*  274 */           readOffset = (int)(readOffset + (6L + u4At(readOffset + 2)));
/*      */         }
/*      */         else {
/*  277 */           switch (attributeName[0]) {
/*      */           case 'E':
/*  279 */             if (!CharOperation.equals(attributeName, AttributeNamesConstants.EnclosingMethodName)) break;
/*  280 */             utf8Offset = 
/*  281 */               this.constantPoolOffsets[u2At(this.constantPoolOffsets[u2At(readOffset + 6)] + 1)];
/*  282 */             this.enclosingTypeName = utf8At(utf8Offset + 3, u2At(utf8Offset + 1));
/*  283 */             this.enclosingNameAndTypeIndex = u2At(readOffset + 8);
/*      */ 
/*  285 */             break;
/*      */           case 'D':
/*  287 */             if (!CharOperation.equals(attributeName, AttributeNamesConstants.DeprecatedName)) break;
/*  288 */             this.accessFlags |= 1048576;
/*      */ 
/*  290 */             break;
/*      */           case 'I':
/*  292 */             if (CharOperation.equals(attributeName, AttributeNamesConstants.InnerClassName)) {
/*  293 */               int innerOffset = readOffset + 6;
/*  294 */               int number_of_classes = u2At(innerOffset);
/*  295 */               if (number_of_classes == 0) break;
/*  296 */               innerOffset += 2;
/*  297 */               this.innerInfos = new InnerClassInfo[number_of_classes];
/*  298 */               for (int j = 0; j < number_of_classes; j++) {
/*  299 */                 this.innerInfos[j] = 
/*  300 */                   new InnerClassInfo(this.reference, this.constantPoolOffsets, innerOffset);
/*  301 */                 if (this.classNameIndex == this.innerInfos[j].innerClassNameIndex) {
/*  302 */                   this.innerInfo = this.innerInfos[j];
/*  303 */                   this.innerInfoIndex = j;
/*      */                 }
/*  305 */                 innerOffset += 8;
/*      */               }
/*  307 */               if (this.innerInfo == null) break;
/*  308 */               char[] enclosingType = this.innerInfo.getEnclosingTypeName();
/*  309 */               if (enclosingType == null) break;
/*  310 */               this.enclosingTypeName = enclosingType;
/*      */             }
/*      */             else
/*      */             {
/*  314 */               if (!CharOperation.equals(attributeName, AttributeNamesConstants.InconsistentHierarchy)) break;
/*  315 */               this.tagBits |= 131072L;
/*      */             }
/*  317 */             break;
/*      */           case 'S':
/*  319 */             if (attributeName.length <= 2) break;
/*  320 */             switch (attributeName[1]) {
/*      */             case 'o':
/*  322 */               if (!CharOperation.equals(attributeName, AttributeNamesConstants.SourceName)) break;
/*  323 */               utf8Offset = this.constantPoolOffsets[u2At(readOffset + 6)];
/*  324 */               this.sourceFileName = utf8At(utf8Offset + 3, u2At(utf8Offset + 1));
/*      */ 
/*  326 */               break;
/*      */             case 'y':
/*  328 */               if (!CharOperation.equals(attributeName, AttributeNamesConstants.SyntheticName)) break;
/*  329 */               this.accessFlags |= 4096;
/*      */ 
/*  331 */               break;
/*      */             case 'i':
/*  333 */               if (!CharOperation.equals(attributeName, AttributeNamesConstants.SignatureName)) break;
/*  334 */               utf8Offset = this.constantPoolOffsets[u2At(readOffset + 6)];
/*  335 */               this.signature = utf8At(utf8Offset + 3, u2At(utf8Offset + 1));
/*      */             }
/*      */ 
/*  339 */             break;
/*      */           case 'R':
/*  341 */             if (CharOperation.equals(attributeName, AttributeNamesConstants.RuntimeVisibleAnnotationsName)) {
/*  342 */               decodeAnnotations(readOffset, true); } else {
/*  343 */               if (!CharOperation.equals(attributeName, AttributeNamesConstants.RuntimeInvisibleAnnotationsName)) break;
/*  344 */               decodeAnnotations(readOffset, false);
/*      */             }
/*  346 */             break;
/*      */           case 'M':
/*  348 */             if (!CharOperation.equals(attributeName, AttributeNamesConstants.MissingTypesName))
/*      */               break;
/*  350 */             int missingTypeOffset = readOffset + 6;
/*  351 */             int numberOfMissingTypes = u2At(missingTypeOffset);
/*  352 */             if (numberOfMissingTypes == 0) break;
/*  353 */             this.missingTypeNames = new char[numberOfMissingTypes][][];
/*  354 */             missingTypeOffset += 2;
/*  355 */             for (int j = 0; j < numberOfMissingTypes; j++) {
/*  356 */               utf8Offset = this.constantPoolOffsets[u2At(this.constantPoolOffsets[u2At(missingTypeOffset)] + 1)];
/*  357 */               char[] missingTypeConstantPoolName = utf8At(utf8Offset + 3, u2At(utf8Offset + 1));
/*  358 */               this.missingTypeNames[j] = CharOperation.splitOn('/', missingTypeConstantPoolName);
/*  359 */               missingTypeOffset += 2;
/*      */             }
/*      */ 
/*      */           }
/*      */ 
/*  364 */           readOffset = (int)(readOffset + (
/*  364 */             6L + u4At(readOffset + 2)));
/*      */         }
/*      */       }
/*  366 */       if (fullyInitialize)
/*  367 */         initialize();
/*      */     }
/*      */     catch (ClassFormatException e) {
/*  370 */       throw e;
/*      */     } catch (Exception localException) {
/*  372 */       throw new ClassFormatException(
/*  373 */         21, 
/*  374 */         readOffset);
/*      */     }
/*      */   }
/*      */ 
/*      */   public int accessFlags()
/*      */   {
/*  384 */     return this.accessFlags;
/*      */   }
/*      */ 
/*      */   private void decodeAnnotations(int offset, boolean runtimeVisible) {
/*  388 */     int numberOfAnnotations = u2At(offset + 6);
/*  389 */     if (numberOfAnnotations > 0) {
/*  390 */       int readOffset = offset + 8;
/*  391 */       AnnotationInfo[] newInfos = (AnnotationInfo[])null;
/*  392 */       int newInfoCount = 0;
/*  393 */       for (int i = 0; i < numberOfAnnotations; i++)
/*      */       {
/*  395 */         AnnotationInfo newInfo = new AnnotationInfo(this.reference, this.constantPoolOffsets, readOffset, runtimeVisible, false);
/*  396 */         readOffset += newInfo.readOffset;
/*  397 */         long standardTagBits = newInfo.standardAnnotationTagBits;
/*  398 */         if (standardTagBits != 0L) {
/*  399 */           this.tagBits |= standardTagBits;
/*      */         } else {
/*  401 */           if (newInfos == null)
/*  402 */             newInfos = new AnnotationInfo[numberOfAnnotations - i];
/*  403 */           newInfos[(newInfoCount++)] = newInfo;
/*      */         }
/*      */       }
/*  406 */       if (newInfos == null) {
/*  407 */         return;
/*      */       }
/*  409 */       if (this.annotations == null) {
/*  410 */         if (newInfoCount != newInfos.length)
/*  411 */           System.arraycopy(newInfos, 0, newInfos = new AnnotationInfo[newInfoCount], 0, newInfoCount);
/*  412 */         this.annotations = newInfos;
/*      */       } else {
/*  414 */         int length = this.annotations.length;
/*  415 */         AnnotationInfo[] temp = new AnnotationInfo[length + newInfoCount];
/*  416 */         System.arraycopy(this.annotations, 0, temp, 0, length);
/*  417 */         System.arraycopy(newInfos, 0, temp, length, newInfoCount);
/*  418 */         this.annotations = temp;
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   public IBinaryAnnotation[] getAnnotations()
/*      */   {
/*  427 */     return this.annotations;
/*      */   }
/*      */ 
/*      */   private char[] getConstantClassNameAt(int constantPoolIndex)
/*      */   {
/*  438 */     int utf8Offset = this.constantPoolOffsets[u2At(this.constantPoolOffsets[constantPoolIndex] + 1)];
/*  439 */     return utf8At(utf8Offset + 3, u2At(utf8Offset + 1));
/*      */   }
/*      */ 
/*      */   public int[] getConstantPoolOffsets()
/*      */   {
/*  448 */     return this.constantPoolOffsets;
/*      */   }
/*      */ 
/*      */   public char[] getEnclosingMethod() {
/*  452 */     if (this.enclosingNameAndTypeIndex <= 0) {
/*  453 */       return null;
/*      */     }
/*  455 */     if (this.enclosingMethod == null)
/*      */     {
/*  457 */       StringBuffer buffer = new StringBuffer();
/*      */ 
/*  459 */       int nameAndTypeOffset = this.constantPoolOffsets[this.enclosingNameAndTypeIndex];
/*  460 */       int utf8Offset = this.constantPoolOffsets[u2At(nameAndTypeOffset + 1)];
/*  461 */       buffer.append(utf8At(utf8Offset + 3, u2At(utf8Offset + 1)));
/*      */ 
/*  463 */       utf8Offset = this.constantPoolOffsets[u2At(nameAndTypeOffset + 3)];
/*  464 */       buffer.append(utf8At(utf8Offset + 3, u2At(utf8Offset + 1)));
/*      */ 
/*  466 */       this.enclosingMethod = String.valueOf(buffer).toCharArray();
/*      */     }
/*  468 */     return this.enclosingMethod;
/*      */   }
/*      */ 
/*      */   public char[] getEnclosingTypeName()
/*      */   {
/*  476 */     return this.enclosingTypeName;
/*      */   }
/*      */ 
/*      */   public IBinaryField[] getFields()
/*      */   {
/*  484 */     return this.fields;
/*      */   }
/*      */ 
/*      */   public char[] getFileName()
/*      */   {
/*  491 */     return this.classFileName;
/*      */   }
/*      */ 
/*      */   public char[] getGenericSignature() {
/*  495 */     return this.signature;
/*      */   }
/*      */ 
/*      */   public char[] getInnerSourceName()
/*      */   {
/*  520 */     if (this.innerInfo != null)
/*  521 */       return this.innerInfo.getSourceName();
/*  522 */     return null;
/*      */   }
/*      */ 
/*      */   public char[][] getInterfaceNames()
/*      */   {
/*  534 */     return this.interfaceNames;
/*      */   }
/*      */ 
/*      */   public IBinaryNestedType[] getMemberTypes()
/*      */   {
/*  547 */     if (this.innerInfos == null) return null;
/*      */ 
/*  549 */     int length = this.innerInfos.length;
/*  550 */     int startingIndex = this.innerInfo != null ? this.innerInfoIndex + 1 : 0;
/*  551 */     if (length != startingIndex) {
/*  552 */       IBinaryNestedType[] memberTypes = 
/*  553 */         new IBinaryNestedType[length - this.innerInfoIndex];
/*  554 */       int memberTypeIndex = 0;
/*  555 */       for (int i = startingIndex; i < length; i++) {
/*  556 */         InnerClassInfo currentInnerInfo = this.innerInfos[i];
/*  557 */         int outerClassNameIdx = currentInnerInfo.outerClassNameIndex;
/*  558 */         int innerNameIndex = currentInnerInfo.innerNameIndex;
/*      */ 
/*  572 */         if ((outerClassNameIdx == 0) || 
/*  573 */           (innerNameIndex == 0) || 
/*  574 */           (outerClassNameIdx != this.classNameIndex) || 
/*  575 */           (currentInnerInfo.getSourceName().length == 0)) continue;
/*  576 */         memberTypes[(memberTypeIndex++)] = currentInnerInfo;
/*      */       }
/*      */ 
/*  579 */       if (memberTypeIndex == 0) return null;
/*  580 */       if (memberTypeIndex != memberTypes.length)
/*      */       {
/*  583 */         System.arraycopy(
/*  584 */           memberTypes, 
/*  585 */           0, 
/*  586 */           memberTypes = new IBinaryNestedType[memberTypeIndex], 
/*  587 */           0, 
/*  588 */           memberTypeIndex);
/*      */       }
/*  590 */       return memberTypes;
/*      */     }
/*  592 */     return null;
/*      */   }
/*      */ 
/*      */   public IBinaryMethod[] getMethods()
/*      */   {
/*  600 */     return this.methods;
/*      */   }
/*      */ 
/*      */   public char[][][] getMissingTypeNames()
/*      */   {
/*  651 */     return this.missingTypeNames;
/*      */   }
/*      */ 
/*      */   public int getModifiers()
/*      */   {
/*  661 */     if (this.innerInfo != null) {
/*  662 */       return this.innerInfo.getModifiers() | this.accessFlags & 0x100000;
/*      */     }
/*  664 */     return this.accessFlags;
/*      */   }
/*      */ 
/*      */   public char[] getName()
/*      */   {
/*  675 */     return this.className;
/*      */   }
/*      */ 
/*      */   public char[] getSourceName() {
/*  679 */     if (this.sourceName != null) {
/*  680 */       return this.sourceName;
/*      */     }
/*  682 */     char[] name = getInnerSourceName();
/*  683 */     if (name == null) {
/*  684 */       name = getName();
/*      */       int start;
/*      */       int start;
/*  686 */       if (isAnonymous())
/*  687 */         start = CharOperation.indexOf('$', name, CharOperation.lastIndexOf('/', name) + 1) + 1;
/*      */       else {
/*  689 */         start = CharOperation.lastIndexOf('/', name) + 1;
/*      */       }
/*  691 */       if (start > 0) {
/*  692 */         char[] newName = new char[name.length - start];
/*  693 */         System.arraycopy(name, start, newName, 0, newName.length);
/*  694 */         name = newName;
/*      */       }
/*      */     }
/*  697 */     return this.sourceName = name;
/*      */   }
/*      */ 
/*      */   public char[] getSuperclassName()
/*      */   {
/*  709 */     return this.superclassName;
/*      */   }
/*      */ 
/*      */   public long getTagBits() {
/*  713 */     return this.tagBits;
/*      */   }
/*      */ 
/*      */   public long getVersion()
/*      */   {
/*  722 */     return this.version;
/*      */   }
/*      */ 
/*      */   private boolean hasNonSyntheticFieldChanges(FieldInfo[] currentFieldInfos, FieldInfo[] otherFieldInfos) {
/*  726 */     int length1 = currentFieldInfos == null ? 0 : currentFieldInfos.length;
/*  727 */     int length2 = otherFieldInfos == null ? 0 : otherFieldInfos.length;
/*  728 */     int index1 = 0;
/*  729 */     int index2 = 0;
/*      */     do
/*      */     {
/*  733 */       index1++;
/*      */ 
/*  732 */       while (currentFieldInfos[index1].isSynthetic())
/*  733 */         if (index1 >= length1)
/*      */           break;
/*  735 */       while (otherFieldInfos[index2].isSynthetic()) {
/*  736 */         index2++; if (index2 >= length2) break;
/*      */       }
/*  738 */       if (hasStructuralFieldChanges(currentFieldInfos[(index1++)], otherFieldInfos[(index2++)]))
/*  739 */         return true;
/*  731 */       if (index1 >= length1) break; 
/*  731 */     }while (index2 < length2);
/*      */ 
/*  742 */     while (index1 < length1) {
/*  743 */       if (!currentFieldInfos[(index1++)].isSynthetic()) return true;
/*      */     }
/*  745 */     while (index2 < length2) {
/*  746 */       if (!otherFieldInfos[(index2++)].isSynthetic()) return true;
/*      */     }
/*  748 */     return false;
/*      */   }
/*      */ 
/*      */   private boolean hasNonSyntheticMethodChanges(MethodInfo[] currentMethodInfos, MethodInfo[] otherMethodInfos) {
/*  752 */     int length1 = currentMethodInfos == null ? 0 : currentMethodInfos.length;
/*  753 */     int length2 = otherMethodInfos == null ? 0 : otherMethodInfos.length;
/*  754 */     int index1 = 0;
/*  755 */     int index2 = 0;
/*      */     do
/*      */     {
/*  760 */       index1++;
/*      */       MethodInfo m;
/*  759 */       while (((m = currentMethodInfos[index1]).isSynthetic()) || (m.isClinit()))
/*      */       {
/*      */         MethodInfo m;
/*  760 */         if (index1 >= length1) break;
/*      */       }
/*  762 */       while (((m = otherMethodInfos[index2]).isSynthetic()) || (m.isClinit())) {
/*  763 */         index2++; if (index2 >= length2) break;
/*      */       }
/*  765 */       if (hasStructuralMethodChanges(currentMethodInfos[(index1++)], otherMethodInfos[(index2++)]))
/*  766 */         return true;
/*  758 */       if (index1 >= length1) break; 
/*  758 */     }while (index2 < length2);
/*      */ 
/*  769 */     while (index1 < length1)
/*      */     {
/*  770 */       MethodInfo m;
/*  770 */       if ((!(m = currentMethodInfos[(index1++)]).isSynthetic()) && (!m.isClinit())) return true;
/*      */     }
/*  772 */     while (index2 < length2)
/*      */     {
/*  773 */       MethodInfo m;
/*  773 */       if ((!(m = otherMethodInfos[(index2++)]).isSynthetic()) && (!m.isClinit())) return true;
/*      */     }
/*  775 */     return false;
/*      */   }
/*      */ 
/*      */   public boolean hasStructuralChanges(byte[] newBytes)
/*      */   {
/*  794 */     return hasStructuralChanges(newBytes, true, true);
/*      */   }
/*      */ 
/*      */   public boolean hasStructuralChanges(byte[] newBytes, boolean orderRequired, boolean excludesSynthetic)
/*      */   {
/*      */     try
/*      */     {
/*  815 */       ClassFileReader newClassFile = 
/*  816 */         new ClassFileReader(newBytes, this.classFileName);
/*      */ 
/*  819 */       if (getModifiers() != newClassFile.getModifiers()) {
/*  820 */         return true;
/*      */       }
/*      */ 
/*  824 */       long OnlyStructuralTagBits = 140703128748032L;
/*      */ 
/*  830 */       if ((getTagBits() & OnlyStructuralTagBits) != (newClassFile.getTagBits() & OnlyStructuralTagBits)) {
/*  831 */         return true;
/*      */       }
/*  833 */       if (hasStructuralAnnotationChanges(getAnnotations(), newClassFile.getAnnotations())) {
/*  834 */         return true;
/*      */       }
/*      */ 
/*  837 */       if (!CharOperation.equals(getGenericSignature(), newClassFile.getGenericSignature())) {
/*  838 */         return true;
/*      */       }
/*  840 */       if (!CharOperation.equals(getSuperclassName(), newClassFile.getSuperclassName())) {
/*  841 */         return true;
/*      */       }
/*  843 */       char[][] newInterfacesNames = newClassFile.getInterfaceNames();
/*  844 */       if (this.interfaceNames != newInterfacesNames) {
/*  845 */         int newInterfacesLength = newInterfacesNames == null ? 0 : newInterfacesNames.length;
/*  846 */         if (newInterfacesLength != this.interfacesCount)
/*  847 */           return true;
/*  848 */         int i = 0; for (int max = this.interfacesCount; i < max; i++) {
/*  849 */           if (!CharOperation.equals(this.interfaceNames[i], newInterfacesNames[i])) {
/*  850 */             return true;
/*      */           }
/*      */         }
/*      */       }
/*  854 */       IBinaryNestedType[] currentMemberTypes = getMemberTypes();
/*  855 */       IBinaryNestedType[] otherMemberTypes = newClassFile.getMemberTypes();
/*  856 */       if (currentMemberTypes != otherMemberTypes) {
/*  857 */         int currentMemberTypeLength = currentMemberTypes == null ? 0 : currentMemberTypes.length;
/*  858 */         int otherMemberTypeLength = otherMemberTypes == null ? 0 : otherMemberTypes.length;
/*  859 */         if (currentMemberTypeLength != otherMemberTypeLength)
/*  860 */           return true;
/*  861 */         for (int i = 0; i < currentMemberTypeLength; i++) {
/*  862 */           if ((!CharOperation.equals(currentMemberTypes[i].getName(), otherMemberTypes[i].getName())) || 
/*  863 */             (currentMemberTypes[i].getModifiers() != otherMemberTypes[i].getModifiers())) {
/*  864 */             return true;
/*      */           }
/*      */         }
/*      */       }
/*  868 */       FieldInfo[] otherFieldInfos = (FieldInfo[])newClassFile.getFields();
/*  869 */       int otherFieldInfosLength = otherFieldInfos == null ? 0 : otherFieldInfos.length;
/*  870 */       boolean compareFields = true;
/*  871 */       if (this.fieldsCount == otherFieldInfosLength) {
/*  872 */         int i = 0;
/*  873 */         for (; i < this.fieldsCount; i++)
/*  874 */           if (hasStructuralFieldChanges(this.fields[i], otherFieldInfos[i])) break;
/*  875 */         if (((compareFields = i != this.fieldsCount ? 1 : 0) != 0) && (!orderRequired) && (!excludesSynthetic))
/*  876 */           return true;
/*      */       }
/*  878 */       if (compareFields) {
/*  879 */         if ((this.fieldsCount != otherFieldInfosLength) && (!excludesSynthetic))
/*  880 */           return true;
/*  881 */         if (orderRequired) {
/*  882 */           if (this.fieldsCount != 0)
/*  883 */             Arrays.sort(this.fields);
/*  884 */           if (otherFieldInfosLength != 0)
/*  885 */             Arrays.sort(otherFieldInfos);
/*      */         }
/*  887 */         if (excludesSynthetic) {
/*  888 */           if (hasNonSyntheticFieldChanges(this.fields, otherFieldInfos))
/*  889 */             return true;
/*      */         }
/*  891 */         else for (int i = 0; i < this.fieldsCount; i++) {
/*  892 */             if (hasStructuralFieldChanges(this.fields[i], otherFieldInfos[i])) {
/*  893 */               return true;
/*      */             }
/*      */           }
/*      */       }
/*      */ 
/*  898 */       MethodInfo[] otherMethodInfos = (MethodInfo[])newClassFile.getMethods();
/*  899 */       int otherMethodInfosLength = otherMethodInfos == null ? 0 : otherMethodInfos.length;
/*  900 */       boolean compareMethods = true;
/*  901 */       if (this.methodsCount == otherMethodInfosLength) {
/*  902 */         int i = 0;
/*  903 */         for (; i < this.methodsCount; i++)
/*  904 */           if (hasStructuralMethodChanges(this.methods[i], otherMethodInfos[i])) break;
/*  905 */         if (((compareMethods = i != this.methodsCount ? 1 : 0) != 0) && (!orderRequired) && (!excludesSynthetic))
/*  906 */           return true;
/*      */       }
/*  908 */       if (compareMethods) {
/*  909 */         if ((this.methodsCount != otherMethodInfosLength) && (!excludesSynthetic))
/*  910 */           return true;
/*  911 */         if (orderRequired) {
/*  912 */           if (this.methodsCount != 0)
/*  913 */             Arrays.sort(this.methods);
/*  914 */           if (otherMethodInfosLength != 0)
/*  915 */             Arrays.sort(otherMethodInfos);
/*      */         }
/*  917 */         if (excludesSynthetic) {
/*  918 */           if (hasNonSyntheticMethodChanges(this.methods, otherMethodInfos))
/*  919 */             return true;
/*      */         }
/*  921 */         else for (int i = 0; i < this.methodsCount; i++) {
/*  922 */             if (hasStructuralMethodChanges(this.methods[i], otherMethodInfos[i])) {
/*  923 */               return true;
/*      */             }
/*      */           }
/*      */       }
/*      */ 
/*  928 */       char[][][] missingTypes = getMissingTypeNames();
/*  929 */       char[][][] newMissingTypes = newClassFile.getMissingTypeNames();
/*  930 */       if (missingTypes != null) {
/*  931 */         if (newMissingTypes == null) {
/*  932 */           return true;
/*      */         }
/*  934 */         int length = missingTypes.length;
/*  935 */         if (length != newMissingTypes.length) {
/*  936 */           return true;
/*      */         }
/*  938 */         for (int i = 0; i < length; i++) {
/*  939 */           if (!CharOperation.equals(missingTypes[i], newMissingTypes[i]))
/*  940 */             return true;
/*      */         }
/*      */       }
/*  943 */       else if (newMissingTypes != null) {
/*  944 */         return true;
/*      */       }
/*  946 */       return false; } catch (ClassFormatException localClassFormatException) {
/*      */     }
/*  948 */     return true;
/*      */   }
/*      */ 
/*      */   private boolean hasStructuralAnnotationChanges(IBinaryAnnotation[] currentAnnotations, IBinaryAnnotation[] otherAnnotations)
/*      */   {
/*  953 */     if (currentAnnotations == otherAnnotations) {
/*  954 */       return false;
/*      */     }
/*  956 */     int currentAnnotationsLength = currentAnnotations == null ? 0 : currentAnnotations.length;
/*  957 */     int otherAnnotationsLength = otherAnnotations == null ? 0 : otherAnnotations.length;
/*  958 */     if (currentAnnotationsLength != otherAnnotationsLength)
/*  959 */       return true;
/*  960 */     for (int i = 0; i < currentAnnotationsLength; i++) {
/*  961 */       if (!CharOperation.equals(currentAnnotations[i].getTypeName(), otherAnnotations[i].getTypeName()))
/*  962 */         return true;
/*  963 */       IBinaryElementValuePair[] currentPairs = currentAnnotations[i].getElementValuePairs();
/*  964 */       IBinaryElementValuePair[] otherPairs = otherAnnotations[i].getElementValuePairs();
/*  965 */       int currentPairsLength = currentPairs == null ? 0 : currentPairs.length;
/*  966 */       int otherPairsLength = otherPairs == null ? 0 : otherPairs.length;
/*  967 */       if (currentPairsLength != otherPairsLength)
/*  968 */         return true;
/*  969 */       for (int j = 0; j < currentPairsLength; j++) {
/*  970 */         if (!CharOperation.equals(currentPairs[j].getName(), otherPairs[j].getName()))
/*  971 */           return true;
/*  972 */         if (!currentPairs[j].getValue().equals(otherPairs[j].getValue()))
/*  973 */           return true;
/*      */       }
/*      */     }
/*  976 */     return false;
/*      */   }
/*      */ 
/*      */   private boolean hasStructuralFieldChanges(FieldInfo currentFieldInfo, FieldInfo otherFieldInfo)
/*      */   {
/*  981 */     if (!CharOperation.equals(currentFieldInfo.getGenericSignature(), otherFieldInfo.getGenericSignature()))
/*  982 */       return true;
/*  983 */     if (currentFieldInfo.getModifiers() != otherFieldInfo.getModifiers())
/*  984 */       return true;
/*  985 */     if ((currentFieldInfo.getTagBits() & 0x0) != (otherFieldInfo.getTagBits() & 0x0))
/*  986 */       return true;
/*  987 */     if (hasStructuralAnnotationChanges(currentFieldInfo.getAnnotations(), otherFieldInfo.getAnnotations()))
/*  988 */       return true;
/*  989 */     if (!CharOperation.equals(currentFieldInfo.getName(), otherFieldInfo.getName()))
/*  990 */       return true;
/*  991 */     if (!CharOperation.equals(currentFieldInfo.getTypeName(), otherFieldInfo.getTypeName()))
/*  992 */       return true;
/*  993 */     if (currentFieldInfo.hasConstant() != otherFieldInfo.hasConstant())
/*  994 */       return true;
/*  995 */     if (currentFieldInfo.hasConstant()) {
/*  996 */       Constant currentConstant = currentFieldInfo.getConstant();
/*  997 */       Constant otherConstant = otherFieldInfo.getConstant();
/*  998 */       if (currentConstant.typeID() != otherConstant.typeID())
/*  999 */         return true;
/* 1000 */       if (!currentConstant.getClass().equals(otherConstant.getClass()))
/* 1001 */         return true;
/* 1002 */       switch (currentConstant.typeID()) {
/*      */       case 10:
/* 1004 */         return currentConstant.intValue() != otherConstant.intValue();
/*      */       case 3:
/* 1006 */         return currentConstant.byteValue() != otherConstant.byteValue();
/*      */       case 4:
/* 1008 */         return currentConstant.shortValue() != otherConstant.shortValue();
/*      */       case 2:
/* 1010 */         return currentConstant.charValue() != otherConstant.charValue();
/*      */       case 7:
/* 1012 */         return currentConstant.longValue() != otherConstant.longValue();
/*      */       case 9:
/* 1014 */         return currentConstant.floatValue() != otherConstant.floatValue();
/*      */       case 8:
/* 1016 */         return currentConstant.doubleValue() != otherConstant.doubleValue();
/*      */       case 5:
/* 1018 */         return currentConstant.booleanValue() ^ otherConstant.booleanValue();
/*      */       case 11:
/* 1020 */         return !currentConstant.stringValue().equals(otherConstant.stringValue());
/*      */       case 6:
/*      */       }
/*      */     }
/* 1023 */     return false;
/*      */   }
/*      */ 
/*      */   private boolean hasStructuralMethodChanges(MethodInfo currentMethodInfo, MethodInfo otherMethodInfo)
/*      */   {
/* 1028 */     if (!CharOperation.equals(currentMethodInfo.getGenericSignature(), otherMethodInfo.getGenericSignature()))
/* 1029 */       return true;
/* 1030 */     if (currentMethodInfo.getModifiers() != otherMethodInfo.getModifiers())
/* 1031 */       return true;
/* 1032 */     if ((currentMethodInfo.getTagBits() & 0x0) != (otherMethodInfo.getTagBits() & 0x0))
/* 1033 */       return true;
/* 1034 */     if (hasStructuralAnnotationChanges(currentMethodInfo.getAnnotations(), otherMethodInfo.getAnnotations()))
/* 1035 */       return true;
/* 1036 */     if (!CharOperation.equals(currentMethodInfo.getSelector(), otherMethodInfo.getSelector()))
/* 1037 */       return true;
/* 1038 */     if (!CharOperation.equals(currentMethodInfo.getMethodDescriptor(), otherMethodInfo.getMethodDescriptor()))
/* 1039 */       return true;
/* 1040 */     if (!CharOperation.equals(currentMethodInfo.getGenericSignature(), otherMethodInfo.getGenericSignature())) {
/* 1041 */       return true;
/*      */     }
/* 1043 */     char[][] currentThrownExceptions = currentMethodInfo.getExceptionTypeNames();
/* 1044 */     char[][] otherThrownExceptions = otherMethodInfo.getExceptionTypeNames();
/* 1045 */     if (currentThrownExceptions != otherThrownExceptions) {
/* 1046 */       int currentThrownExceptionsLength = currentThrownExceptions == null ? 0 : currentThrownExceptions.length;
/* 1047 */       int otherThrownExceptionsLength = otherThrownExceptions == null ? 0 : otherThrownExceptions.length;
/* 1048 */       if (currentThrownExceptionsLength != otherThrownExceptionsLength)
/* 1049 */         return true;
/* 1050 */       for (int k = 0; k < currentThrownExceptionsLength; k++)
/* 1051 */         if (!CharOperation.equals(currentThrownExceptions[k], otherThrownExceptions[k]))
/* 1052 */           return true;
/*      */     }
/* 1054 */     return false;
/*      */   }
/*      */ 
/*      */   private void initialize()
/*      */     throws ClassFormatException
/*      */   {
/*      */     try
/*      */     {
/* 1063 */       int i = 0; for (int max = this.fieldsCount; i < max; i++) {
/* 1064 */         this.fields[i].initialize();
/*      */       }
/* 1066 */       int i = 0; for (int max = this.methodsCount; i < max; i++) {
/* 1067 */         this.methods[i].initialize();
/*      */       }
/* 1069 */       if (this.innerInfos != null) {
/* 1070 */         int i = 0; for (int max = this.innerInfos.length; i < max; i++) {
/* 1071 */           this.innerInfos[i].initialize();
/*      */         }
/*      */       }
/* 1074 */       if (this.annotations != null) {
/* 1075 */         int i = 0; for (int max = this.annotations.length; i < max; i++) {
/* 1076 */           this.annotations[i].initialize();
/*      */         }
/*      */       }
/* 1079 */       getEnclosingMethod();
/* 1080 */       reset();
/*      */     } catch (RuntimeException e) {
/* 1082 */       ClassFormatException exception = new ClassFormatException(e, this.classFileName);
/* 1083 */       throw exception;
/*      */     }
/*      */   }
/*      */ 
/*      */   public boolean isAnonymous()
/*      */   {
/* 1093 */     if (this.innerInfo == null) return false;
/* 1094 */     char[] innerSourceName = this.innerInfo.getSourceName();
/* 1095 */     return (innerSourceName == null) || (innerSourceName.length == 0);
/*      */   }
/*      */ 
/*      */   public boolean isBinaryType()
/*      */   {
/* 1104 */     return true;
/*      */   }
/*      */ 
/*      */   public boolean isLocal()
/*      */   {
/* 1113 */     if (this.innerInfo == null) return false;
/* 1114 */     if (this.innerInfo.getEnclosingTypeName() != null) return false;
/* 1115 */     char[] innerSourceName = this.innerInfo.getSourceName();
/* 1116 */     return (innerSourceName != null) && (innerSourceName.length > 0);
/*      */   }
/*      */ 
/*      */   public boolean isMember()
/*      */   {
/* 1125 */     if (this.innerInfo == null) return false;
/* 1126 */     if (this.innerInfo.getEnclosingTypeName() == null) return false;
/* 1127 */     char[] innerSourceName = this.innerInfo.getSourceName();
/* 1128 */     return (innerSourceName != null) && (innerSourceName.length > 0);
/*      */   }
/*      */ 
/*      */   public boolean isNestedType()
/*      */   {
/* 1137 */     return this.innerInfo != null;
/*      */   }
/*      */ 
/*      */   public char[] sourceFileName()
/*      */   {
/* 1146 */     return this.sourceFileName;
/*      */   }
/*      */ 
/*      */   public String toString() {
/* 1150 */     ByteArrayOutputStream out = new ByteArrayOutputStream();
/* 1151 */     PrintWriter print = new PrintWriter(out);
/* 1152 */     print.println(getClass().getName() + "{");
/* 1153 */     print.println(" this.className: " + new String(getName()));
/* 1154 */     print.println(" this.superclassName: " + (getSuperclassName() == null ? "null" : new String(getSuperclassName())));
/* 1155 */     print.println(" access_flags: " + printTypeModifiers(accessFlags()) + "(" + accessFlags() + ")");
/* 1156 */     print.flush();
/* 1157 */     return out.toString();
/*      */   }
/*      */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\ecj-3.5.2.jar
 * Qualified Name:     org.eclipse.jdt.internal.compiler.classfmt.ClassFileReader
 * JD-Core Version:    0.6.0
 */