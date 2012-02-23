/*      */ package org.apache.lucene.index;
/*      */ 
/*      */ import java.io.Closeable;
/*      */ import java.io.FileNotFoundException;
/*      */ import java.io.IOException;
/*      */ import java.io.PrintStream;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Arrays;
/*      */ import java.util.Collection;
/*      */ import java.util.Collections;
/*      */ import java.util.HashMap;
/*      */ import java.util.HashSet;
/*      */ import java.util.Iterator;
/*      */ import java.util.List;
/*      */ import java.util.Map;
/*      */ import java.util.Set;
/*      */ import org.apache.lucene.store.ChecksumIndexInput;
/*      */ import org.apache.lucene.store.ChecksumIndexOutput;
/*      */ import org.apache.lucene.store.Directory;
/*      */ import org.apache.lucene.store.IndexInput;
/*      */ import org.apache.lucene.store.IndexOutput;
/*      */ import org.apache.lucene.store.NoSuchDirectoryException;
/*      */ import org.apache.lucene.util.IOUtils;
/*      */ import org.apache.lucene.util.ThreadInterruptedException;
/*      */ 
/*      */ public final class SegmentInfos
/*      */   implements Cloneable, Iterable<SegmentInfo>
/*      */ {
/*      */   public static final int FORMAT = -1;
/*      */   public static final int FORMAT_LOCKLESS = -2;
/*      */   public static final int FORMAT_SINGLE_NORM_FILE = -3;
/*      */   public static final int FORMAT_SHARED_DOC_STORE = -4;
/*      */   public static final int FORMAT_CHECKSUM = -5;
/*      */   public static final int FORMAT_DEL_COUNT = -6;
/*      */   public static final int FORMAT_HAS_PROX = -7;
/*      */   public static final int FORMAT_USER_DATA = -8;
/*      */   public static final int FORMAT_DIAGNOSTICS = -9;
/*      */   public static final int FORMAT_HAS_VECTORS = -10;
/*      */   public static final int FORMAT_3_1 = -11;
/*      */   public static final int CURRENT_FORMAT = -11;
/*      */   public static final int FORMAT_MINIMUM = -1;
/*      */   public static final int FORMAT_MAXIMUM = -11;
/*      */   public int counter;
/*      */   long version;
/*      */   private long generation;
/*      */   private long lastGeneration;
/*      */   private Map<String, String> userData;
/*      */   private int format;
/*      */   private List<SegmentInfo> segments;
/*      */   private Set<SegmentInfo> segmentSet;
/*      */   private transient List<SegmentInfo> cachedUnmodifiableList;
/*      */   private transient Set<SegmentInfo> cachedUnmodifiableSet;
/*      */   private static PrintStream infoStream;
/*      */   ChecksumIndexOutput pendingSegnOutput;
/*      */   private static int defaultGenFileRetryCount;
/*      */   private static int defaultGenFileRetryPauseMsec;
/*      */   private static int defaultGenLookaheadCount;
/*      */ 
/*      */   public SegmentInfos()
/*      */   {
/*  108 */     this.counter = 0;
/*      */ 
/*  113 */     this.version = System.currentTimeMillis();
/*      */ 
/*  115 */     this.generation = 0L;
/*  116 */     this.lastGeneration = 0L;
/*      */ 
/*  120 */     this.userData = Collections.emptyMap();
/*      */ 
/*  124 */     this.segments = new ArrayList();
/*  125 */     this.segmentSet = new HashSet();
/*      */   }
/*      */ 
/*      */   public void setFormat(int format)
/*      */   {
/*  136 */     this.format = format;
/*      */   }
/*      */ 
/*      */   public int getFormat() {
/*  140 */     return this.format;
/*      */   }
/*      */ 
/*      */   public SegmentInfo info(int i) {
/*  144 */     return (SegmentInfo)this.segments.get(i);
/*      */   }
/*      */ 
/*      */   public static long getCurrentSegmentGeneration(String[] files)
/*      */   {
/*  154 */     if (files == null) {
/*  155 */       return -1L;
/*      */     }
/*  157 */     long max = -1L;
/*  158 */     for (int i = 0; i < files.length; i++) {
/*  159 */       String file = files[i];
/*  160 */       if ((file.startsWith("segments")) && (!file.equals("segments.gen"))) {
/*  161 */         long gen = generationFromSegmentsFileName(file);
/*  162 */         if (gen > max) {
/*  163 */           max = gen;
/*      */         }
/*      */       }
/*      */     }
/*  167 */     return max;
/*      */   }
/*      */ 
/*      */   public static long getCurrentSegmentGeneration(Directory directory)
/*      */     throws IOException
/*      */   {
/*      */     try
/*      */     {
/*  178 */       return getCurrentSegmentGeneration(directory.listAll()); } catch (NoSuchDirectoryException nsde) {
/*      */     }
/*  180 */     return -1L;
/*      */   }
/*      */ 
/*      */   public static String getCurrentSegmentFileName(String[] files)
/*      */     throws IOException
/*      */   {
/*  192 */     return IndexFileNames.fileNameFromGeneration("segments", "", getCurrentSegmentGeneration(files));
/*      */   }
/*      */ 
/*      */   public static String getCurrentSegmentFileName(Directory directory)
/*      */     throws IOException
/*      */   {
/*  204 */     return IndexFileNames.fileNameFromGeneration("segments", "", getCurrentSegmentGeneration(directory));
/*      */   }
/*      */ 
/*      */   public String getCurrentSegmentFileName()
/*      */   {
/*  213 */     return IndexFileNames.fileNameFromGeneration("segments", "", this.lastGeneration);
/*      */   }
/*      */ 
/*      */   public static long generationFromSegmentsFileName(String fileName)
/*      */   {
/*  223 */     if (fileName.equals("segments"))
/*  224 */       return 0L;
/*  225 */     if (fileName.startsWith("segments")) {
/*  226 */       return Long.parseLong(fileName.substring(1 + "segments".length()), 36);
/*      */     }
/*      */ 
/*  229 */     throw new IllegalArgumentException("fileName \"" + fileName + "\" is not a segments file");
/*      */   }
/*      */ 
/*      */   public String getNextSegmentFileName()
/*      */   {
/*      */     long nextGeneration;
/*      */     long nextGeneration;
/*  240 */     if (this.generation == -1L)
/*  241 */       nextGeneration = 1L;
/*      */     else {
/*  243 */       nextGeneration = this.generation + 1L;
/*      */     }
/*  245 */     return IndexFileNames.fileNameFromGeneration("segments", "", nextGeneration);
/*      */   }
/*      */ 
/*      */   public final void read(Directory directory, String segmentFileName)
/*      */     throws CorruptIndexException, IOException
/*      */   {
/*  260 */     boolean success = false;
/*      */ 
/*  263 */     clear();
/*      */ 
/*  265 */     ChecksumIndexInput input = new ChecksumIndexInput(directory.openInput(segmentFileName));
/*      */ 
/*  267 */     this.generation = generationFromSegmentsFileName(segmentFileName);
/*      */ 
/*  269 */     this.lastGeneration = this.generation;
/*      */     try
/*      */     {
/*  272 */       int format = input.readInt();
/*      */ 
/*  274 */       if (format > -1) {
/*  275 */         throw new IndexFormatTooOldException(segmentFileName, format, -1, -11);
/*      */       }
/*      */ 
/*  278 */       if (format < -11) {
/*  279 */         throw new IndexFormatTooNewException(segmentFileName, format, -1, -11);
/*      */       }
/*      */ 
/*  282 */       this.version = input.readLong();
/*  283 */       this.counter = input.readInt();
/*      */ 
/*  285 */       for (int i = input.readInt(); i > 0; i--) {
/*  286 */         SegmentInfo si = new SegmentInfo(directory, format, input);
/*  287 */         if (si.getVersion() == null)
/*      */         {
/*  289 */           Directory dir = directory;
/*  290 */           if (si.getDocStoreOffset() != -1) {
/*  291 */             if (si.getDocStoreIsCompoundFile()) {
/*  292 */               dir = new CompoundFileReader(dir, IndexFileNames.segmentFileName(si.getDocStoreSegment(), "cfx"), 1024);
/*      */             }
/*      */ 
/*      */           }
/*  296 */           else if (si.getUseCompoundFile()) {
/*  297 */             dir = new CompoundFileReader(dir, IndexFileNames.segmentFileName(si.name, "cfs"), 1024);
/*      */           }
/*      */ 
/*      */           try
/*      */           {
/*  302 */             String store = si.getDocStoreOffset() != -1 ? si.getDocStoreSegment() : si.name;
/*  303 */             si.setVersion(FieldsReader.detectCodeVersion(dir, store));
/*      */           }
/*      */           finally {
/*  306 */             if (dir != directory) dir.close();
/*      */           }
/*      */         }
/*  309 */         add(si);
/*      */       }
/*      */ 
/*  312 */       if (format >= 0) {
/*  313 */         if (input.getFilePointer() >= input.length())
/*  314 */           this.version = System.currentTimeMillis();
/*      */         else {
/*  316 */           this.version = input.readLong();
/*      */         }
/*      */       }
/*  319 */       if (format <= -8) {
/*  320 */         if (format <= -9)
/*  321 */           this.userData = input.readStringStringMap();
/*  322 */         else if (0 != input.readByte())
/*  323 */           this.userData = Collections.singletonMap("userData", input.readString());
/*      */         else
/*  325 */           this.userData = Collections.emptyMap();
/*      */       }
/*      */       else {
/*  328 */         this.userData = Collections.emptyMap();
/*      */       }
/*      */ 
/*  331 */       if (format <= -5) {
/*  332 */         long checksumNow = input.getChecksum();
/*  333 */         long checksumThen = input.readLong();
/*  334 */         if (checksumNow != checksumThen)
/*  335 */           throw new CorruptIndexException("checksum mismatch in segments file");
/*      */       }
/*  337 */       success = true;
/*      */     }
/*      */     finally {
/*  340 */       input.close();
/*  341 */       if (!success)
/*      */       {
/*  344 */         clear();
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   public final void read(Directory directory)
/*      */     throws CorruptIndexException, IOException
/*      */   {
/*  357 */     this.generation = (this.lastGeneration = -1L);
/*      */ 
/*  359 */     new FindSegmentsFile(directory)
/*      */     {
/*      */       protected Object doBody(String segmentFileName) throws CorruptIndexException, IOException
/*      */       {
/*  363 */         SegmentInfos.this.read(this.directory, segmentFileName);
/*  364 */         return null;
/*      */       }
/*      */     }
/*  359 */     .run();
/*      */   }
/*      */ 
/*      */   private final void write(Directory directory)
/*      */     throws IOException
/*      */   {
/*  375 */     String segmentFileName = getNextSegmentFileName();
/*      */ 
/*  378 */     if (this.generation == -1L)
/*  379 */       this.generation = 1L;
/*      */     else {
/*  381 */       this.generation += 1L;
/*      */     }
/*      */ 
/*  384 */     ChecksumIndexOutput segnOutput = new ChecksumIndexOutput(directory.createOutput(segmentFileName));
/*      */ 
/*  386 */     boolean success = false;
/*      */     try
/*      */     {
/*  389 */       segnOutput.writeInt(-11);
/*  390 */       segnOutput.writeLong(this.version);
/*  391 */       segnOutput.writeInt(this.counter);
/*  392 */       segnOutput.writeInt(size());
/*  393 */       for (SegmentInfo si : this) {
/*  394 */         si.write(segnOutput);
/*      */       }
/*  396 */       segnOutput.writeStringStringMap(this.userData);
/*  397 */       segnOutput.prepareCommit();
/*  398 */       this.pendingSegnOutput = segnOutput;
/*  399 */       success = true;
/*      */     } finally {
/*  401 */       if (!success)
/*      */       {
/*  404 */         IOUtils.closeWhileHandlingException(new Closeable[] { segnOutput });
/*      */         try
/*      */         {
/*  408 */           directory.deleteFile(segmentFileName);
/*      */         }
/*      */         catch (Throwable t)
/*      */         {
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   public void pruneDeletedSegments() throws IOException {
/*  418 */     for (Iterator it = this.segments.iterator(); it.hasNext(); ) {
/*  419 */       SegmentInfo info = (SegmentInfo)it.next();
/*  420 */       if (info.getDelCount() == info.docCount) {
/*  421 */         it.remove();
/*  422 */         this.segmentSet.remove(info);
/*      */       }
/*      */     }
/*  425 */     assert (this.segmentSet.size() == this.segments.size());
/*      */   }
/*      */ 
/*      */   public Object clone()
/*      */   {
/*      */     try
/*      */     {
/*  436 */       SegmentInfos sis = (SegmentInfos)super.clone();
/*      */ 
/*  438 */       sis.segments = new ArrayList(size());
/*  439 */       sis.segmentSet = new HashSet(size());
/*  440 */       sis.cachedUnmodifiableList = null;
/*  441 */       sis.cachedUnmodifiableSet = null;
/*  442 */       for (SegmentInfo info : this)
/*      */       {
/*  444 */         sis.add((SegmentInfo)info.clone());
/*      */       }
/*  446 */       sis.userData = new HashMap(this.userData);
/*  447 */       return sis; } catch (CloneNotSupportedException e) {
/*      */     }
/*  449 */     throw new RuntimeException("should not happen", e);
/*      */   }
/*      */ 
/*      */   public long getVersion()
/*      */   {
/*  457 */     return this.version;
/*      */   }
/*      */   public long getGeneration() {
/*  460 */     return this.generation;
/*      */   }
/*      */   public long getLastGeneration() {
/*  463 */     return this.lastGeneration;
/*      */   }
/*      */ 
/*      */   public static long readCurrentVersion(Directory directory)
/*      */     throws CorruptIndexException, IOException
/*      */   {
/*  479 */     SegmentInfos sis = new SegmentInfos();
/*  480 */     sis.read(directory);
/*  481 */     return sis.version;
/*      */   }
/*      */ 
/*      */   public static Map<String, String> readCurrentUserData(Directory directory)
/*      */     throws CorruptIndexException, IOException
/*      */   {
/*  491 */     SegmentInfos sis = new SegmentInfos();
/*  492 */     sis.read(directory);
/*  493 */     return sis.getUserData();
/*      */   }
/*      */ 
/*      */   public static void setInfoStream(PrintStream infoStream)
/*      */   {
/*  500 */     infoStream = infoStream;
/*      */   }
/*      */ 
/*      */   public static void setDefaultGenFileRetryCount(int count)
/*      */   {
/*  516 */     defaultGenFileRetryCount = count;
/*      */   }
/*      */ 
/*      */   public static int getDefaultGenFileRetryCount()
/*      */   {
/*  523 */     return defaultGenFileRetryCount;
/*      */   }
/*      */ 
/*      */   public static void setDefaultGenFileRetryPauseMsec(int msec)
/*      */   {
/*  531 */     defaultGenFileRetryPauseMsec = msec;
/*      */   }
/*      */ 
/*      */   public static int getDefaultGenFileRetryPauseMsec()
/*      */   {
/*  538 */     return defaultGenFileRetryPauseMsec;
/*      */   }
/*      */ 
/*      */   public static void setDefaultGenLookaheadCount(int count)
/*      */   {
/*  549 */     defaultGenLookaheadCount = count;
/*      */   }
/*      */ 
/*      */   public static int getDefaultGenLookahedCount()
/*      */   {
/*  555 */     return defaultGenLookaheadCount;
/*      */   }
/*      */ 
/*      */   public static PrintStream getInfoStream()
/*      */   {
/*  562 */     return infoStream;
/*      */   }
/*      */ 
/*      */   private static void message(String message)
/*      */   {
/*  572 */     infoStream.println("SIS [" + Thread.currentThread().getName() + "]: " + message);
/*      */   }
/*      */ 
/*      */   @Deprecated
/*      */   public SegmentInfos range(int first, int last)
/*      */   {
/*  824 */     SegmentInfos infos = new SegmentInfos();
/*  825 */     infos.addAll(this.segments.subList(first, last));
/*  826 */     return infos;
/*      */   }
/*      */ 
/*      */   void updateGeneration(SegmentInfos other)
/*      */   {
/*  831 */     this.lastGeneration = other.lastGeneration;
/*  832 */     this.generation = other.generation;
/*      */   }
/*      */ 
/*      */   final void rollbackCommit(Directory dir) throws IOException {
/*  836 */     if (this.pendingSegnOutput != null) {
/*      */       try {
/*  838 */         this.pendingSegnOutput.close();
/*      */       }
/*      */       catch (Throwable t)
/*      */       {
/*      */       }
/*      */ 
/*      */       try
/*      */       {
/*  847 */         String segmentFileName = IndexFileNames.fileNameFromGeneration("segments", "", this.generation);
/*      */ 
/*  850 */         dir.deleteFile(segmentFileName);
/*      */       }
/*      */       catch (Throwable t)
/*      */       {
/*      */       }
/*  855 */       this.pendingSegnOutput = null;
/*      */     }
/*      */   }
/*      */ 
/*      */   final void prepareCommit(Directory dir)
/*      */     throws IOException
/*      */   {
/*  870 */     if (this.pendingSegnOutput != null)
/*  871 */       throw new IllegalStateException("prepareCommit was already called");
/*  872 */     write(dir);
/*      */   }
/*      */ 
/*      */   public Collection<String> files(Directory dir, boolean includeSegmentsFile)
/*      */     throws IOException
/*      */   {
/*  881 */     HashSet files = new HashSet();
/*  882 */     if (includeSegmentsFile) {
/*  883 */       files.add(getCurrentSegmentFileName());
/*      */     }
/*  885 */     int size = size();
/*  886 */     for (int i = 0; i < size; i++) {
/*  887 */       SegmentInfo info = info(i);
/*  888 */       if (info.dir == dir) {
/*  889 */         files.addAll(info(i).files());
/*      */       }
/*      */     }
/*  892 */     return files;
/*      */   }
/*      */ 
/*      */   final void finishCommit(Directory dir) throws IOException {
/*  896 */     if (this.pendingSegnOutput == null)
/*  897 */       throw new IllegalStateException("prepareCommit was not called");
/*  898 */     boolean success = false;
/*      */     try {
/*  900 */       this.pendingSegnOutput.finishCommit();
/*  901 */       this.pendingSegnOutput.close();
/*  902 */       this.pendingSegnOutput = null;
/*  903 */       success = true;
/*      */     } finally {
/*  905 */       if (!success) {
/*  906 */         rollbackCommit(dir);
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  919 */     String fileName = IndexFileNames.fileNameFromGeneration("segments", "", this.generation);
/*      */ 
/*  922 */     success = false;
/*      */     try {
/*  924 */       dir.sync(Collections.singleton(fileName));
/*  925 */       success = true;
/*      */     } finally {
/*  927 */       if (!success) {
/*      */         try {
/*  929 */           dir.deleteFile(fileName);
/*      */         }
/*      */         catch (Throwable t)
/*      */         {
/*      */         }
/*      */       }
/*      */     }
/*  936 */     this.lastGeneration = this.generation;
/*      */     try
/*      */     {
/*  939 */       IndexOutput genOutput = dir.createOutput("segments.gen");
/*      */       try {
/*  941 */         genOutput.writeInt(-2);
/*  942 */         genOutput.writeLong(this.generation);
/*  943 */         genOutput.writeLong(this.generation);
/*      */       } finally {
/*  945 */         genOutput.close();
/*      */       }
/*      */     } catch (ThreadInterruptedException t) {
/*  948 */       throw t;
/*      */     }
/*      */     catch (Throwable t)
/*      */     {
/*      */     }
/*      */   }
/*      */ 
/*      */   final void commit(Directory dir)
/*      */     throws IOException
/*      */   {
/*  963 */     prepareCommit(dir);
/*  964 */     finishCommit(dir);
/*      */   }
/*      */ 
/*      */   public String toString(Directory directory) {
/*  968 */     StringBuilder buffer = new StringBuilder();
/*  969 */     buffer.append(getCurrentSegmentFileName()).append(": ");
/*  970 */     int count = size();
/*  971 */     for (int i = 0; i < count; i++) {
/*  972 */       if (i > 0) {
/*  973 */         buffer.append(' ');
/*      */       }
/*  975 */       SegmentInfo info = info(i);
/*  976 */       buffer.append(info.toString(directory, 0));
/*      */     }
/*  978 */     return buffer.toString();
/*      */   }
/*      */ 
/*      */   public Map<String, String> getUserData() {
/*  982 */     return this.userData;
/*      */   }
/*      */ 
/*      */   void setUserData(Map<String, String> data) {
/*  986 */     if (data == null)
/*  987 */       this.userData = Collections.emptyMap();
/*      */     else
/*  989 */       this.userData = data;
/*      */   }
/*      */ 
/*      */   void replace(SegmentInfos other)
/*      */   {
/*  998 */     rollbackSegmentInfos(other.asList());
/*  999 */     this.lastGeneration = other.lastGeneration;
/*      */   }
/*      */ 
/*      */   public int totalDocCount()
/*      */   {
/* 1005 */     int count = 0;
/* 1006 */     for (SegmentInfo info : this) {
/* 1007 */       count += info.docCount;
/*      */     }
/* 1009 */     return count;
/*      */   }
/*      */ 
/*      */   public void changed()
/*      */   {
/* 1015 */     this.version += 1L;
/*      */   }
/*      */ 
/*      */   void applyMergeChanges(MergePolicy.OneMerge merge, boolean dropSegment)
/*      */   {
/* 1020 */     Set mergedAway = new HashSet(merge.segments);
/* 1021 */     boolean inserted = false;
/* 1022 */     int newSegIdx = 0;
/* 1023 */     int segIdx = 0; for (int cnt = this.segments.size(); segIdx < cnt; segIdx++) {
/* 1024 */       assert (segIdx >= newSegIdx);
/* 1025 */       SegmentInfo info = (SegmentInfo)this.segments.get(segIdx);
/* 1026 */       if (mergedAway.contains(info)) {
/* 1027 */         if ((!inserted) && (!dropSegment)) {
/* 1028 */           this.segments.set(segIdx, merge.info);
/* 1029 */           inserted = true;
/* 1030 */           newSegIdx++;
/*      */         }
/*      */       } else {
/* 1033 */         this.segments.set(newSegIdx, info);
/* 1034 */         newSegIdx++;
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 1043 */     if ((!inserted) && (!dropSegment)) {
/* 1044 */       this.segments.add(0, merge.info);
/*      */     }
/*      */ 
/* 1048 */     this.segments.subList(newSegIdx, this.segments.size()).clear();
/*      */ 
/* 1051 */     if (!dropSegment) {
/* 1052 */       this.segmentSet.add(merge.info);
/*      */     }
/* 1054 */     this.segmentSet.removeAll(mergedAway);
/*      */ 
/* 1056 */     assert (this.segmentSet.size() == this.segments.size());
/*      */   }
/*      */ 
/*      */   List<SegmentInfo> createBackupSegmentInfos(boolean cloneChildren) {
/* 1060 */     if (cloneChildren) {
/* 1061 */       List list = new ArrayList(size());
/* 1062 */       for (SegmentInfo info : this) {
/* 1063 */         list.add((SegmentInfo)info.clone());
/*      */       }
/* 1065 */       return list;
/*      */     }
/* 1067 */     return new ArrayList(this.segments);
/*      */   }
/*      */ 
/*      */   void rollbackSegmentInfos(List<SegmentInfo> infos)
/*      */   {
/* 1072 */     clear();
/* 1073 */     addAll(infos);
/*      */   }
/*      */ 
/*      */   public Iterator<SegmentInfo> iterator()
/*      */   {
/* 1079 */     return asList().iterator();
/*      */   }
/*      */ 
/*      */   public List<SegmentInfo> asList()
/*      */   {
/* 1084 */     if (this.cachedUnmodifiableList == null) {
/* 1085 */       this.cachedUnmodifiableList = Collections.unmodifiableList(this.segments);
/*      */     }
/* 1087 */     return this.cachedUnmodifiableList;
/*      */   }
/*      */ 
/*      */   public Set<SegmentInfo> asSet()
/*      */   {
/* 1093 */     if (this.cachedUnmodifiableSet == null) {
/* 1094 */       this.cachedUnmodifiableSet = Collections.unmodifiableSet(this.segmentSet);
/*      */     }
/* 1096 */     return this.cachedUnmodifiableSet;
/*      */   }
/*      */ 
/*      */   public int size() {
/* 1100 */     return this.segments.size();
/*      */   }
/*      */ 
/*      */   public void add(SegmentInfo si) {
/* 1104 */     if (this.segmentSet.contains(si)) {
/* 1105 */       throw new IllegalStateException("Cannot add the same segment two times to this SegmentInfos instance");
/*      */     }
/* 1107 */     this.segments.add(si);
/* 1108 */     this.segmentSet.add(si);
/* 1109 */     assert (this.segmentSet.size() == this.segments.size());
/*      */   }
/*      */ 
/*      */   public void addAll(Iterable<SegmentInfo> sis) {
/* 1113 */     for (SegmentInfo si : sis)
/* 1114 */       add(si);
/*      */   }
/*      */ 
/*      */   public void clear()
/*      */   {
/* 1119 */     this.segments.clear();
/* 1120 */     this.segmentSet.clear();
/*      */   }
/*      */ 
/*      */   public void remove(SegmentInfo si) {
/* 1124 */     int index = indexOf(si);
/* 1125 */     if (index >= 0)
/* 1126 */       remove(index);
/*      */   }
/*      */ 
/*      */   public void remove(int index)
/*      */   {
/* 1131 */     this.segmentSet.remove(this.segments.remove(index));
/* 1132 */     assert (this.segmentSet.size() == this.segments.size());
/*      */   }
/*      */ 
/*      */   public boolean contains(SegmentInfo si) {
/* 1136 */     return this.segmentSet.contains(si);
/*      */   }
/*      */ 
/*      */   public int indexOf(SegmentInfo si) {
/* 1140 */     if (this.segmentSet.contains(si)) {
/* 1141 */       return this.segments.indexOf(si);
/*      */     }
/* 1143 */     return -1;
/*      */   }
/*      */ 
/*      */   static
/*      */   {
/*  133 */     infoStream = null;
/*      */ 
/*  505 */     defaultGenFileRetryCount = 10;
/*  506 */     defaultGenFileRetryPauseMsec = 50;
/*  507 */     defaultGenLookaheadCount = 10;
/*      */   }
/*      */ 
/*      */   public static abstract class FindSegmentsFile
/*      */   {
/*      */     final Directory directory;
/*      */ 
/*      */     public FindSegmentsFile(Directory directory)
/*      */     {
/*  589 */       this.directory = directory;
/*      */     }
/*      */ 
/*      */     public Object run() throws CorruptIndexException, IOException {
/*  593 */       return run(null);
/*      */     }
/*      */ 
/*      */     public Object run(IndexCommit commit) throws CorruptIndexException, IOException {
/*  597 */       if (commit != null) {
/*  598 */         if (this.directory != commit.getDirectory())
/*  599 */           throw new IOException("the specified commit does not match the specified Directory");
/*  600 */         return doBody(commit.getSegmentsFileName());
/*      */       }
/*      */ 
/*  603 */       String segmentFileName = null;
/*  604 */       long lastGen = -1L;
/*  605 */       long gen = 0L;
/*  606 */       int genLookaheadCount = 0;
/*  607 */       IOException exc = null;
/*  608 */       int retryCount = 0;
/*      */ 
/*  610 */       boolean useFirstMethod = true;
/*      */       while (true)
/*      */       {
/*  630 */         if (useFirstMethod)
/*      */         {
/*  637 */           String[] files = null;
/*      */ 
/*  639 */           long genA = -1L;
/*      */ 
/*  641 */           files = this.directory.listAll();
/*      */ 
/*  643 */           if (files != null) {
/*  644 */             genA = SegmentInfos.getCurrentSegmentGeneration(files);
/*      */           }
/*      */ 
/*  647 */           if (SegmentInfos.infoStream != null) {
/*  648 */             SegmentInfos.access$100("directory listing genA=" + genA);
/*      */           }
/*      */ 
/*  656 */           long genB = -1L;
/*  657 */           for (int i = 0; i < SegmentInfos.defaultGenFileRetryCount; i++) {
/*  658 */             IndexInput genInput = null;
/*      */             try {
/*  660 */               genInput = this.directory.openInput("segments.gen");
/*      */             } catch (FileNotFoundException e) {
/*  662 */               if (SegmentInfos.infoStream != null) {
/*  663 */                 SegmentInfos.access$100("segments.gen open: FileNotFoundException " + e);
/*      */               }
/*  665 */               break;
/*      */             } catch (IOException e) {
/*  667 */               if (SegmentInfos.infoStream != null) {
/*  668 */                 SegmentInfos.access$100("segments.gen open: IOException " + e);
/*      */               }
/*      */             }
/*      */ 
/*  672 */             if (genInput != null)
/*      */               try {
/*  674 */                 int version = genInput.readInt();
/*  675 */                 if (version == -2) {
/*  676 */                   long gen0 = genInput.readLong();
/*  677 */                   long gen1 = genInput.readLong();
/*  678 */                   if (SegmentInfos.infoStream != null) {
/*  679 */                     SegmentInfos.access$100("fallback check: " + gen0 + "; " + gen1);
/*      */                   }
/*  681 */                   if (gen0 == gen1)
/*      */                   {
/*  683 */                     genB = gen0;
/*  684 */                     jsr 28;
/*      */                   }
/*      */                 }
/*      */               } catch (IOException err2) {
/*      */               }
/*      */               finally {
/*  690 */                 genInput.close();
/*      */               }
/*      */             try
/*      */             {
/*  694 */               Thread.sleep(SegmentInfos.defaultGenFileRetryPauseMsec);
/*      */             } catch (InterruptedException ie) {
/*  696 */               throw new ThreadInterruptedException(ie);
/*      */             }
/*      */           }
/*      */ 
/*  700 */           if (SegmentInfos.infoStream != null) {
/*  701 */             SegmentInfos.access$100("segments.gen check: genB=" + genB);
/*      */           }
/*      */ 
/*  705 */           if (genA > genB)
/*  706 */             gen = genA;
/*      */           else {
/*  708 */             gen = genB;
/*      */           }
/*  710 */           if (gen == -1L)
/*      */           {
/*  712 */             throw new IndexNotFoundException("no segments* file found in " + this.directory + ": files: " + Arrays.toString(files));
/*      */           }
/*      */         }
/*      */ 
/*  716 */         if ((useFirstMethod) && (lastGen == gen) && (retryCount >= 2))
/*      */         {
/*  720 */           useFirstMethod = false;
/*      */         }
/*      */ 
/*  726 */         if (!useFirstMethod) {
/*  727 */           if (genLookaheadCount < SegmentInfos.defaultGenLookaheadCount) {
/*  728 */             gen += 1L;
/*  729 */             genLookaheadCount++;
/*  730 */             if (SegmentInfos.infoStream != null)
/*  731 */               SegmentInfos.access$100("look ahead increment gen to " + gen);
/*      */           }
/*      */           else
/*      */           {
/*  735 */             throw exc;
/*      */           }
/*  737 */         } else if (lastGen == gen)
/*      */         {
/*  740 */           retryCount++;
/*      */         }
/*      */         else
/*      */         {
/*  744 */           retryCount = 0;
/*      */         }
/*      */ 
/*  747 */         lastGen = gen;
/*      */ 
/*  749 */         segmentFileName = IndexFileNames.fileNameFromGeneration("segments", "", gen);
/*      */         try
/*      */         {
/*  754 */           Object v = doBody(segmentFileName);
/*  755 */           if (SegmentInfos.infoStream != null) {
/*  756 */             SegmentInfos.access$100("success on " + segmentFileName);
/*      */           }
/*  758 */           return v;
/*      */         }
/*      */         catch (IOException err)
/*      */         {
/*  762 */           if (exc == null) {
/*  763 */             exc = err;
/*      */           }
/*      */ 
/*  766 */           if (SegmentInfos.infoStream != null) {
/*  767 */             SegmentInfos.access$100("primary Exception on '" + segmentFileName + "': " + err + "'; will retry: retryCount=" + retryCount + "; gen = " + gen);
/*      */           }
/*      */ 
/*  770 */           if ((gen > 1L) && (useFirstMethod) && (retryCount == 1))
/*      */           {
/*  777 */             String prevSegmentFileName = IndexFileNames.fileNameFromGeneration("segments", "", gen - 1L);
/*      */ 
/*  782 */             boolean prevExists = this.directory.fileExists(prevSegmentFileName);
/*      */ 
/*  784 */             if (prevExists) {
/*  785 */               if (SegmentInfos.infoStream != null)
/*  786 */                 SegmentInfos.access$100("fallback to prior segment file '" + prevSegmentFileName + "'");
/*      */               try
/*      */               {
/*  789 */                 Object v = doBody(prevSegmentFileName);
/*  790 */                 if (SegmentInfos.infoStream != null) {
/*  791 */                   SegmentInfos.access$100("success on fallback " + prevSegmentFileName);
/*      */                 }
/*  793 */                 return v;
/*      */               } catch (IOException err2) {
/*  795 */                 if (SegmentInfos.infoStream != null)
/*  796 */                   SegmentInfos.access$100("secondary Exception on '" + prevSegmentFileName + "': " + err2 + "'; will retry");
/*      */               }
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */ 
/*      */     protected abstract Object doBody(String paramString)
/*      */       throws CorruptIndexException, IOException;
/*      */   }
/*      */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.index.SegmentInfos
 * JD-Core Version:    0.6.0
 */