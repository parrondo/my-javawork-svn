/*     */ package org.apache.lucene.index;
/*     */ 
/*     */ import J;
/*     */ import java.io.IOException;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collections;
/*     */ import java.util.HashMap;
/*     */ import java.util.HashSet;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import org.apache.lucene.store.Directory;
/*     */ import org.apache.lucene.store.IndexInput;
/*     */ import org.apache.lucene.store.IndexOutput;
/*     */ import org.apache.lucene.util.BitVector;
/*     */ import org.apache.lucene.util.Constants;
/*     */ 
/*     */ public final class SegmentInfo
/*     */   implements Cloneable
/*     */ {
/*     */   static final int NO = -1;
/*     */   static final int YES = 1;
/*     */   static final int CHECK_DIR = 0;
/*     */   static final int WITHOUT_GEN = 0;
/*     */   public String name;
/*     */   public int docCount;
/*     */   public Directory dir;
/*     */   private boolean preLockless;
/*     */   private long delGen;
/*     */   private long[] normGen;
/*     */   private byte isCompoundFile;
/*     */   private boolean hasSingleNormFile;
/*     */   private volatile List<String> files;
/*  82 */   private volatile long sizeInBytesNoStore = -1L;
/*  83 */   private volatile long sizeInBytesWithStore = -1L;
/*     */   private int docStoreOffset;
/*     */   private String docStoreSegment;
/*     */   private boolean docStoreIsCompoundFile;
/*     */   private int delCount;
/*     */   private boolean hasProx;
/*     */   private boolean hasVectors;
/*     */   private Map<String, String> diagnostics;
/*     */   private String version;
/*     */   private long bufferedDeletesGen;
/*     */ 
/*     */   public SegmentInfo(String name, int docCount, Directory dir, boolean isCompoundFile, boolean hasSingleNormFile, boolean hasProx, boolean hasVectors)
/*     */   {
/* 112 */     this.name = name;
/* 113 */     this.docCount = docCount;
/* 114 */     this.dir = dir;
/* 115 */     this.delGen = -1L;
/* 116 */     this.isCompoundFile = (byte)(isCompoundFile ? 1 : -1);
/* 117 */     this.preLockless = false;
/* 118 */     this.hasSingleNormFile = hasSingleNormFile;
/* 119 */     this.docStoreOffset = -1;
/* 120 */     this.delCount = 0;
/* 121 */     this.hasProx = hasProx;
/* 122 */     this.hasVectors = hasVectors;
/* 123 */     this.version = Constants.LUCENE_MAIN_VERSION;
/*     */   }
/*     */ 
/*     */   void reset(SegmentInfo src)
/*     */   {
/* 130 */     clearFiles();
/* 131 */     this.version = src.version;
/* 132 */     this.name = src.name;
/* 133 */     this.docCount = src.docCount;
/* 134 */     this.dir = src.dir;
/* 135 */     this.preLockless = src.preLockless;
/* 136 */     this.delGen = src.delGen;
/* 137 */     this.docStoreOffset = src.docStoreOffset;
/* 138 */     this.docStoreIsCompoundFile = src.docStoreIsCompoundFile;
/* 139 */     this.hasVectors = src.hasVectors;
/* 140 */     this.hasProx = src.hasProx;
/* 141 */     if (src.normGen == null) {
/* 142 */       this.normGen = null;
/*     */     } else {
/* 144 */       this.normGen = new long[src.normGen.length];
/* 145 */       System.arraycopy(src.normGen, 0, this.normGen, 0, src.normGen.length);
/*     */     }
/* 147 */     this.isCompoundFile = src.isCompoundFile;
/* 148 */     this.hasSingleNormFile = src.hasSingleNormFile;
/* 149 */     this.delCount = src.delCount;
/*     */   }
/*     */ 
/*     */   void setDiagnostics(Map<String, String> diagnostics) {
/* 153 */     this.diagnostics = diagnostics;
/*     */   }
/*     */ 
/*     */   public Map<String, String> getDiagnostics() {
/* 157 */     return this.diagnostics;
/*     */   }
/*     */ 
/*     */   SegmentInfo(Directory dir, int format, IndexInput input)
/*     */     throws IOException
/*     */   {
/* 169 */     this.dir = dir;
/* 170 */     if (format <= -11) {
/* 171 */       this.version = input.readString();
/*     */     }
/* 173 */     this.name = input.readString();
/* 174 */     this.docCount = input.readInt();
/* 175 */     if (format <= -2) {
/* 176 */       this.delGen = input.readLong();
/* 177 */       if (format <= -4) {
/* 178 */         this.docStoreOffset = input.readInt();
/* 179 */         if (this.docStoreOffset != -1) {
/* 180 */           this.docStoreSegment = input.readString();
/* 181 */           this.docStoreIsCompoundFile = (1 == input.readByte());
/*     */         } else {
/* 183 */           this.docStoreSegment = this.name;
/* 184 */           this.docStoreIsCompoundFile = false;
/*     */         }
/*     */       } else {
/* 187 */         this.docStoreOffset = -1;
/* 188 */         this.docStoreSegment = this.name;
/* 189 */         this.docStoreIsCompoundFile = false;
/*     */       }
/* 191 */       if (format <= -3)
/* 192 */         this.hasSingleNormFile = (1 == input.readByte());
/*     */       else {
/* 194 */         this.hasSingleNormFile = false;
/*     */       }
/* 196 */       int numNormGen = input.readInt();
/* 197 */       if (numNormGen == -1) {
/* 198 */         this.normGen = null;
/*     */       } else {
/* 200 */         this.normGen = new long[numNormGen];
/* 201 */         for (int j = 0; j < numNormGen; j++) {
/* 202 */           this.normGen[j] = input.readLong();
/*     */         }
/*     */       }
/* 205 */       this.isCompoundFile = input.readByte();
/* 206 */       this.preLockless = (this.isCompoundFile == 0);
/* 207 */       if (format <= -6) {
/* 208 */         this.delCount = input.readInt();
/* 209 */         if ((!$assertionsDisabled) && (this.delCount > this.docCount)) throw new AssertionError(); 
/*     */       }
/*     */       else {
/* 211 */         this.delCount = -1;
/* 212 */       }if (format <= -7)
/* 213 */         this.hasProx = (input.readByte() == 1);
/*     */       else {
/* 215 */         this.hasProx = true;
/*     */       }
/* 217 */       if (format <= -9)
/* 218 */         this.diagnostics = input.readStringStringMap();
/*     */       else {
/* 220 */         this.diagnostics = Collections.emptyMap();
/*     */       }
/*     */ 
/* 223 */       if (format <= -10) {
/* 224 */         this.hasVectors = (input.readByte() == 1);
/*     */       }
/*     */       else
/*     */       {
/*     */         String ext;
/*     */         String storesSegment;
/*     */         boolean isCompoundFile;
/*     */         String ext;
/* 229 */         if (this.docStoreOffset != -1) {
/* 230 */           String storesSegment = this.docStoreSegment;
/* 231 */           boolean isCompoundFile = this.docStoreIsCompoundFile;
/* 232 */           ext = "cfx";
/*     */         } else {
/* 234 */           storesSegment = this.name;
/* 235 */           isCompoundFile = getUseCompoundFile();
/* 236 */           ext = "cfs";
/*     */         }
/*     */         Directory dirToTest;
/*     */         Directory dirToTest;
/* 239 */         if (isCompoundFile)
/* 240 */           dirToTest = new CompoundFileReader(dir, IndexFileNames.segmentFileName(storesSegment, ext));
/*     */         else
/* 242 */           dirToTest = dir;
/*     */         try
/*     */         {
/* 245 */           this.hasVectors = dirToTest.fileExists(IndexFileNames.segmentFileName(storesSegment, "tvx"));
/*     */         } finally {
/* 247 */           if (isCompoundFile)
/* 248 */             dirToTest.close();
/*     */         }
/*     */       }
/*     */     }
/*     */     else {
/* 253 */       this.delGen = 0L;
/* 254 */       this.normGen = null;
/* 255 */       this.isCompoundFile = 0;
/* 256 */       this.preLockless = true;
/* 257 */       this.hasSingleNormFile = false;
/* 258 */       this.docStoreOffset = -1;
/* 259 */       this.docStoreIsCompoundFile = false;
/* 260 */       this.docStoreSegment = null;
/* 261 */       this.delCount = -1;
/* 262 */       this.hasProx = true;
/* 263 */       this.diagnostics = Collections.emptyMap();
/*     */     }
/*     */   }
/*     */ 
/*     */   void setNumFields(int numFields) {
/* 268 */     if (this.normGen == null)
/*     */     {
/* 272 */       this.normGen = new long[numFields];
/*     */ 
/* 274 */       if (!this.preLockless)
/*     */       {
/* 281 */         for (int i = 0; i < numFields; i++)
/* 282 */           this.normGen[i] = -1L;
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public long sizeInBytes(boolean includeDocStores)
/*     */     throws IOException
/*     */   {
/* 294 */     if (includeDocStores) {
/* 295 */       if (this.sizeInBytesWithStore != -1L) {
/* 296 */         return this.sizeInBytesWithStore;
/*     */       }
/* 298 */       long sum = 0L;
/* 299 */       for (String fileName : files())
/*     */       {
/* 302 */         if ((this.docStoreOffset == -1) || (!IndexFileNames.isDocStoreFile(fileName))) {
/* 303 */           sum += this.dir.fileLength(fileName);
/*     */         }
/*     */       }
/* 306 */       this.sizeInBytesWithStore = sum;
/* 307 */       return this.sizeInBytesWithStore;
/*     */     }
/* 309 */     if (this.sizeInBytesNoStore != -1L) {
/* 310 */       return this.sizeInBytesNoStore;
/*     */     }
/* 312 */     long sum = 0L;
/* 313 */     for (String fileName : files()) {
/* 314 */       if (IndexFileNames.isDocStoreFile(fileName)) {
/*     */         continue;
/*     */       }
/* 317 */       sum += this.dir.fileLength(fileName);
/*     */     }
/* 319 */     this.sizeInBytesNoStore = sum;
/* 320 */     return this.sizeInBytesNoStore;
/*     */   }
/*     */ 
/*     */   public boolean getHasVectors() throws IOException
/*     */   {
/* 325 */     return this.hasVectors;
/*     */   }
/*     */ 
/*     */   public void setHasVectors(boolean v) {
/* 329 */     this.hasVectors = v;
/* 330 */     clearFiles();
/*     */   }
/*     */ 
/*     */   public boolean hasDeletions()
/*     */     throws IOException
/*     */   {
/* 349 */     if (this.delGen == -1L)
/* 350 */       return false;
/* 351 */     if (this.delGen >= 1L) {
/* 352 */       return true;
/*     */     }
/* 354 */     return this.dir.fileExists(getDelFileName());
/*     */   }
/*     */ 
/*     */   void advanceDelGen()
/*     */   {
/* 360 */     if (this.delGen == -1L)
/* 361 */       this.delGen = 1L;
/*     */     else {
/* 363 */       this.delGen += 1L;
/*     */     }
/* 365 */     clearFiles();
/*     */   }
/*     */ 
/*     */   void clearDelGen() {
/* 369 */     this.delGen = -1L;
/* 370 */     clearFiles();
/*     */   }
/*     */ 
/*     */   public Object clone()
/*     */   {
/* 375 */     SegmentInfo si = new SegmentInfo(this.name, this.docCount, this.dir, false, this.hasSingleNormFile, this.hasProx, this.hasVectors);
/*     */ 
/* 377 */     si.docStoreOffset = this.docStoreOffset;
/* 378 */     si.docStoreSegment = this.docStoreSegment;
/* 379 */     si.docStoreIsCompoundFile = this.docStoreIsCompoundFile;
/* 380 */     si.delGen = this.delGen;
/* 381 */     si.delCount = this.delCount;
/* 382 */     si.preLockless = this.preLockless;
/* 383 */     si.isCompoundFile = this.isCompoundFile;
/* 384 */     si.diagnostics = new HashMap(this.diagnostics);
/* 385 */     if (this.normGen != null) {
/* 386 */       si.normGen = ((long[])this.normGen.clone());
/*     */     }
/* 388 */     si.version = this.version;
/* 389 */     return si;
/*     */   }
/*     */ 
/*     */   public String getDelFileName() {
/* 393 */     if (this.delGen == -1L)
/*     */     {
/* 396 */       return null;
/*     */     }
/*     */ 
/* 399 */     return IndexFileNames.fileNameFromGeneration(this.name, "del", this.delGen);
/*     */   }
/*     */ 
/*     */   public boolean hasSeparateNorms(int fieldNumber)
/*     */     throws IOException
/*     */   {
/* 410 */     if (((this.normGen == null) && (this.preLockless)) || ((this.normGen != null) && (this.normGen[fieldNumber] == 0L)))
/*     */     {
/* 412 */       String fileName = this.name + ".s" + fieldNumber;
/* 413 */       return this.dir.fileExists(fileName);
/*     */     }
/* 415 */     return (this.normGen != null) && (this.normGen[fieldNumber] != -1L);
/*     */   }
/*     */ 
/*     */   public boolean hasSeparateNorms()
/*     */     throws IOException
/*     */   {
/* 426 */     if (this.normGen == null) {
/* 427 */       if (!this.preLockless)
/*     */       {
/* 430 */         return false;
/*     */       }
/*     */ 
/* 435 */       String[] result = this.dir.listAll();
/* 436 */       if (result == null) {
/* 437 */         throw new IOException("cannot read directory " + this.dir + ": listAll() returned null");
/*     */       }
/* 439 */       IndexFileNameFilter filter = IndexFileNameFilter.getFilter();
/*     */ 
/* 441 */       String pattern = this.name + ".s";
/* 442 */       int patternLength = pattern.length();
/* 443 */       for (int i = 0; i < result.length; i++) {
/* 444 */         String fileName = result[i];
/* 445 */         if ((filter.accept(null, fileName)) && (fileName.startsWith(pattern)) && (Character.isDigit(fileName.charAt(patternLength))))
/* 446 */           return true;
/*     */       }
/* 448 */       return false;
/*     */     }
/*     */ 
/* 454 */     for (int i = 0; i < this.normGen.length; i++) {
/* 455 */       if (this.normGen[i] >= 1L) {
/* 456 */         return true;
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 461 */     for (int i = 0; i < this.normGen.length; i++) {
/* 462 */       if ((this.normGen[i] == 0L) && 
/* 463 */         (hasSeparateNorms(i))) {
/* 464 */         return true;
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 470 */     return false;
/*     */   }
/*     */ 
/*     */   void advanceNormGen(int fieldIndex)
/*     */   {
/* 480 */     if (this.normGen[fieldIndex] == -1L)
/* 481 */       this.normGen[fieldIndex] = 1L;
/*     */     else {
/* 483 */       this.normGen[fieldIndex] += 1L;
/*     */     }
/* 485 */     clearFiles();
/*     */   }
/*     */ 
/*     */   public String getNormFileName(int number)
/*     */     throws IOException
/*     */   {
/*     */     long gen;
/*     */     long gen;
/* 495 */     if (this.normGen == null)
/* 496 */       gen = 0L;
/*     */     else {
/* 498 */       gen = this.normGen[number];
/*     */     }
/*     */ 
/* 501 */     if (hasSeparateNorms(number))
/*     */     {
/* 503 */       return IndexFileNames.fileNameFromGeneration(this.name, "s" + number, gen);
/*     */     }
/*     */ 
/* 506 */     if (this.hasSingleNormFile)
/*     */     {
/* 508 */       return IndexFileNames.fileNameFromGeneration(this.name, "nrm", 0L);
/*     */     }
/*     */ 
/* 512 */     return IndexFileNames.fileNameFromGeneration(this.name, "f" + number, 0L);
/*     */   }
/*     */ 
/*     */   void setUseCompoundFile(boolean isCompoundFile)
/*     */   {
/* 522 */     if (isCompoundFile)
/* 523 */       this.isCompoundFile = 1;
/*     */     else {
/* 525 */       this.isCompoundFile = -1;
/*     */     }
/* 527 */     clearFiles();
/*     */   }
/*     */ 
/*     */   public boolean getUseCompoundFile()
/*     */     throws IOException
/*     */   {
/* 535 */     if (this.isCompoundFile == -1)
/* 536 */       return false;
/* 537 */     if (this.isCompoundFile == 1) {
/* 538 */       return true;
/*     */     }
/* 540 */     return this.dir.fileExists(IndexFileNames.segmentFileName(this.name, "cfs"));
/*     */   }
/*     */ 
/*     */   public int getDelCount() throws IOException
/*     */   {
/* 545 */     if (this.delCount == -1)
/* 546 */       if (hasDeletions()) {
/* 547 */         String delFileName = getDelFileName();
/* 548 */         this.delCount = new BitVector(this.dir, delFileName).count();
/*     */       } else {
/* 550 */         this.delCount = 0;
/*     */       }
/* 552 */     assert (this.delCount <= this.docCount);
/* 553 */     return this.delCount;
/*     */   }
/*     */ 
/*     */   void setDelCount(int delCount) {
/* 557 */     this.delCount = delCount;
/* 558 */     assert (delCount <= this.docCount);
/*     */   }
/*     */ 
/*     */   public int getDocStoreOffset() {
/* 562 */     return this.docStoreOffset;
/*     */   }
/*     */ 
/*     */   public boolean getDocStoreIsCompoundFile() {
/* 566 */     return this.docStoreIsCompoundFile;
/*     */   }
/*     */ 
/*     */   void setDocStoreIsCompoundFile(boolean v) {
/* 570 */     this.docStoreIsCompoundFile = v;
/* 571 */     clearFiles();
/*     */   }
/*     */ 
/*     */   public String getDocStoreSegment() {
/* 575 */     return this.docStoreSegment;
/*     */   }
/*     */ 
/*     */   public void setDocStoreSegment(String segment) {
/* 579 */     this.docStoreSegment = segment;
/*     */   }
/*     */ 
/*     */   void setDocStoreOffset(int offset) {
/* 583 */     this.docStoreOffset = offset;
/* 584 */     clearFiles();
/*     */   }
/*     */ 
/*     */   void setDocStore(int offset, String segment, boolean isCompoundFile) {
/* 588 */     this.docStoreOffset = offset;
/* 589 */     this.docStoreSegment = segment;
/* 590 */     this.docStoreIsCompoundFile = isCompoundFile;
/* 591 */     clearFiles();
/*     */   }
/*     */ 
/*     */   void write(IndexOutput output)
/*     */     throws IOException
/*     */   {
/* 599 */     assert (this.delCount <= this.docCount) : ("delCount=" + this.delCount + " docCount=" + this.docCount + " segment=" + this.name);
/*     */ 
/* 601 */     output.writeString(this.version);
/* 602 */     output.writeString(this.name);
/* 603 */     output.writeInt(this.docCount);
/* 604 */     output.writeLong(this.delGen);
/* 605 */     output.writeInt(this.docStoreOffset);
/* 606 */     if (this.docStoreOffset != -1) {
/* 607 */       output.writeString(this.docStoreSegment);
/* 608 */       output.writeByte((byte)(this.docStoreIsCompoundFile ? 1 : 0));
/*     */     }
/*     */ 
/* 611 */     output.writeByte((byte)(this.hasSingleNormFile ? 1 : 0));
/* 612 */     if (this.normGen == null) {
/* 613 */       output.writeInt(-1);
/*     */     } else {
/* 615 */       output.writeInt(this.normGen.length);
/* 616 */       for (int j = 0; j < this.normGen.length; j++) {
/* 617 */         output.writeLong(this.normGen[j]);
/*     */       }
/*     */     }
/* 620 */     output.writeByte(this.isCompoundFile);
/* 621 */     output.writeInt(this.delCount);
/* 622 */     output.writeByte((byte)(this.hasProx ? 1 : 0));
/* 623 */     output.writeStringStringMap(this.diagnostics);
/* 624 */     output.writeByte((byte)(this.hasVectors ? 1 : 0));
/*     */   }
/*     */ 
/*     */   void setHasProx(boolean hasProx) {
/* 628 */     this.hasProx = hasProx;
/* 629 */     clearFiles();
/*     */   }
/*     */ 
/*     */   public boolean getHasProx() {
/* 633 */     return this.hasProx;
/*     */   }
/*     */ 
/*     */   private void addIfExists(Set<String> files, String fileName) throws IOException {
/* 637 */     if (this.dir.fileExists(fileName))
/* 638 */       files.add(fileName);
/*     */   }
/*     */ 
/*     */   public List<String> files()
/*     */     throws IOException
/*     */   {
/* 649 */     if (this.files != null)
/*     */     {
/* 651 */       return this.files;
/*     */     }
/*     */ 
/* 654 */     HashSet filesSet = new HashSet();
/*     */ 
/* 656 */     boolean useCompoundFile = getUseCompoundFile();
/*     */ 
/* 658 */     if (useCompoundFile)
/* 659 */       filesSet.add(IndexFileNames.segmentFileName(this.name, "cfs"));
/*     */     else {
/* 661 */       for (String ext : IndexFileNames.NON_STORE_INDEX_EXTENSIONS) {
/* 662 */         addIfExists(filesSet, IndexFileNames.segmentFileName(this.name, ext));
/*     */       }
/*     */     }
/* 665 */     if (this.docStoreOffset != -1)
/*     */     {
/* 668 */       assert (this.docStoreSegment != null);
/* 669 */       if (this.docStoreIsCompoundFile) {
/* 670 */         filesSet.add(IndexFileNames.segmentFileName(this.docStoreSegment, "cfx"));
/*     */       } else {
/* 672 */         filesSet.add(IndexFileNames.segmentFileName(this.docStoreSegment, "fdx"));
/* 673 */         filesSet.add(IndexFileNames.segmentFileName(this.docStoreSegment, "fdt"));
/* 674 */         if (this.hasVectors) {
/* 675 */           filesSet.add(IndexFileNames.segmentFileName(this.docStoreSegment, "tvx"));
/* 676 */           filesSet.add(IndexFileNames.segmentFileName(this.docStoreSegment, "tvd"));
/* 677 */           filesSet.add(IndexFileNames.segmentFileName(this.docStoreSegment, "tvf"));
/*     */         }
/*     */       }
/* 680 */     } else if (!useCompoundFile) {
/* 681 */       filesSet.add(IndexFileNames.segmentFileName(this.name, "fdx"));
/* 682 */       filesSet.add(IndexFileNames.segmentFileName(this.name, "fdt"));
/* 683 */       if (this.hasVectors) {
/* 684 */         filesSet.add(IndexFileNames.segmentFileName(this.name, "tvx"));
/* 685 */         filesSet.add(IndexFileNames.segmentFileName(this.name, "tvd"));
/* 686 */         filesSet.add(IndexFileNames.segmentFileName(this.name, "tvf"));
/*     */       }
/*     */     }
/*     */ 
/* 690 */     String delFileName = IndexFileNames.fileNameFromGeneration(this.name, "del", this.delGen);
/* 691 */     if ((delFileName != null) && ((this.delGen >= 1L) || (this.dir.fileExists(delFileName)))) {
/* 692 */       filesSet.add(delFileName);
/*     */     }
/*     */ 
/* 696 */     if (this.normGen != null) {
/* 697 */       for (int i = 0; i < this.normGen.length; i++) {
/* 698 */         long gen = this.normGen[i];
/* 699 */         if (gen >= 1L)
/*     */         {
/* 701 */           filesSet.add(IndexFileNames.fileNameFromGeneration(this.name, "s" + i, gen));
/* 702 */         } else if (-1L == gen)
/*     */         {
/* 705 */           if ((!this.hasSingleNormFile) && (!useCompoundFile)) {
/* 706 */             String fileName = IndexFileNames.segmentFileName(this.name, "f" + i);
/* 707 */             if (this.dir.fileExists(fileName))
/* 708 */               filesSet.add(fileName);
/*     */           }
/*     */         } else {
/* 711 */           if (0L != gen)
/*     */             continue;
/* 713 */           String fileName = null;
/* 714 */           if (useCompoundFile)
/* 715 */             fileName = IndexFileNames.segmentFileName(this.name, "s" + i);
/* 716 */           else if (!this.hasSingleNormFile) {
/* 717 */             fileName = IndexFileNames.segmentFileName(this.name, "f" + i);
/*     */           }
/* 719 */           if ((fileName != null) && (this.dir.fileExists(fileName)))
/* 720 */             filesSet.add(fileName);
/*     */         }
/*     */       }
/*     */     }
/* 724 */     else if ((this.preLockless) || ((!this.hasSingleNormFile) && (!useCompoundFile)))
/*     */     {
/*     */       String prefix;
/*     */       String prefix;
/* 728 */       if (useCompoundFile)
/* 729 */         prefix = IndexFileNames.segmentFileName(this.name, "s");
/*     */       else
/* 731 */         prefix = IndexFileNames.segmentFileName(this.name, "f");
/* 732 */       int prefixLength = prefix.length();
/* 733 */       String[] allFiles = this.dir.listAll();
/* 734 */       IndexFileNameFilter filter = IndexFileNameFilter.getFilter();
/* 735 */       for (int i = 0; i < allFiles.length; i++) {
/* 736 */         String fileName = allFiles[i];
/* 737 */         if ((filter.accept(null, fileName)) && (fileName.length() > prefixLength) && (Character.isDigit(fileName.charAt(prefixLength))) && (fileName.startsWith(prefix))) {
/* 738 */           filesSet.add(fileName);
/*     */         }
/*     */       }
/*     */     }
/* 742 */     return this.files = new ArrayList(filesSet);
/*     */   }
/*     */ 
/*     */   private void clearFiles()
/*     */   {
/* 748 */     this.files = null;
/* 749 */     this.sizeInBytesNoStore = -1L;
/* 750 */     this.sizeInBytesWithStore = -1L;
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/* 756 */     return toString(this.dir, 0);
/*     */   }
/*     */ 
/*     */   public String toString(Directory dir, int pendingDelCount)
/*     */   {
/* 773 */     StringBuilder s = new StringBuilder();
/* 774 */     s.append(this.name).append('(').append(this.version == null ? "?" : this.version).append(')').append(':');
/*     */     char cfs;
/*     */     try
/*     */     {
/*     */       char cfs;
/* 778 */       if (getUseCompoundFile())
/* 779 */         cfs = 'c';
/*     */       else
/* 781 */         cfs = 'C';
/*     */     }
/*     */     catch (IOException ioe) {
/* 784 */       cfs = '?';
/*     */     }
/* 786 */     s.append(cfs);
/*     */ 
/* 788 */     if (this.dir != dir) {
/* 789 */       s.append('x');
/*     */     }
/* 791 */     if (this.hasVectors) {
/* 792 */       s.append('v');
/* 794 */     }s.append(this.docCount);
/*     */     int delCount;
/*     */     try {
/* 798 */       delCount = getDelCount();
/*     */     } catch (IOException ioe) {
/* 800 */       delCount = -1;
/*     */     }
/* 802 */     if (delCount != -1) {
/* 803 */       delCount += pendingDelCount;
/*     */     }
/* 805 */     if (delCount != 0) {
/* 806 */       s.append('/');
/* 807 */       if (delCount == -1)
/* 808 */         s.append('?');
/*     */       else {
/* 810 */         s.append(delCount);
/*     */       }
/*     */     }
/*     */ 
/* 814 */     if (this.docStoreOffset != -1) {
/* 815 */       s.append("->").append(this.docStoreSegment);
/* 816 */       if (this.docStoreIsCompoundFile)
/* 817 */         s.append('c');
/*     */       else {
/* 819 */         s.append('C');
/*     */       }
/* 821 */       s.append('+').append(this.docStoreOffset);
/*     */     }
/*     */ 
/* 824 */     return s.toString();
/*     */   }
/*     */ 
/*     */   public boolean equals(Object obj)
/*     */   {
/* 831 */     if (this == obj) return true;
/* 832 */     if ((obj instanceof SegmentInfo)) {
/* 833 */       SegmentInfo other = (SegmentInfo)obj;
/* 834 */       return (other.dir == this.dir) && (other.name.equals(this.name));
/*     */     }
/* 836 */     return false;
/*     */   }
/*     */ 
/*     */   public int hashCode()
/*     */   {
/* 842 */     return this.dir.hashCode() + this.name.hashCode();
/*     */   }
/*     */ 
/*     */   void setVersion(String version)
/*     */   {
/* 856 */     this.version = version;
/*     */   }
/*     */ 
/*     */   public String getVersion()
/*     */   {
/* 861 */     return this.version;
/*     */   }
/*     */ 
/*     */   long getBufferedDeletesGen() {
/* 865 */     return this.bufferedDeletesGen;
/*     */   }
/*     */ 
/*     */   void setBufferedDeletesGen(long v) {
/* 869 */     this.bufferedDeletesGen = v;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.index.SegmentInfo
 * JD-Core Version:    0.6.0
 */