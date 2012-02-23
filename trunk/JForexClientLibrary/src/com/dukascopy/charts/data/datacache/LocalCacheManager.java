/*      */ package com.dukascopy.charts.data.datacache;
/*      */ 
/*      */ import com.dukascopy.api.Instrument;
/*      */ import com.dukascopy.api.OfferSide;
/*      */ import com.dukascopy.api.Period;
/*      */ import com.dukascopy.charts.data.datacache.feed.IFeedCommissionManager;
/*      */ import com.dukascopy.charts.data.datacache.feed.ZeroFeedCommissionManager;
/*      */ import com.dukascopy.dds2.greed.util.FilePathManager;
/*      */ import com.dukascopy.transport.util.Hex;
/*      */ import java.io.BufferedInputStream;
/*      */ import java.io.BufferedOutputStream;
/*      */ import java.io.ByteArrayInputStream;
/*      */ import java.io.ByteArrayOutputStream;
/*      */ import java.io.File;
/*      */ import java.io.FileInputStream;
/*      */ import java.io.FileOutputStream;
/*      */ import java.io.IOException;
/*      */ import java.io.InputStream;
/*      */ import java.io.InvalidClassException;
/*      */ import java.io.OutputStream;
/*      */ import java.io.RandomAccessFile;
/*      */ import java.io.StreamCorruptedException;
/*      */ import java.nio.channels.FileChannel;
/*      */ import java.nio.channels.FileLock;
/*      */ import java.nio.channels.OverlappingFileLockException;
/*      */ import java.text.DateFormat;
/*      */ import java.text.SimpleDateFormat;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Collection;
/*      */ import java.util.Collections;
/*      */ import java.util.Comparator;
/*      */ import java.util.Date;
/*      */ import java.util.HashMap;
/*      */ import java.util.Iterator;
/*      */ import java.util.LinkedHashMap;
/*      */ import java.util.List;
/*      */ import java.util.Map;
/*      */ import java.util.Map.Entry;
/*      */ import java.util.Set;
/*      */ import java.util.TimeZone;
/*      */ import java.util.Timer;
/*      */ import java.util.TimerTask;
/*      */ import java.util.concurrent.locks.ReentrantReadWriteLock;
/*      */ import java.util.concurrent.locks.ReentrantReadWriteLock.ReadLock;
/*      */ import java.util.concurrent.locks.ReentrantReadWriteLock.WriteLock;
/*      */ import javax.crypto.Cipher;
/*      */ import javax.crypto.CipherInputStream;
/*      */ import javax.crypto.CipherOutputStream;
/*      */ import javax.crypto.spec.SecretKeySpec;
/*      */ import org.slf4j.Logger;
/*      */ import org.slf4j.LoggerFactory;
/*      */ 
/*      */ public class LocalCacheManager extends CacheManager
/*      */   implements InstrumentSubscriptionListener
/*      */ {
/*      */   private static final Logger LOGGER;
/*      */   protected static final SimpleDateFormat DATE_FORMAT;
/*      */   private static final int NORMAL_NUMBER_OF_OPEN_FILES = 40;
/*      */   private static final int MAX_NUMBER_OF_OPEN_FILES = 100;
/*      */   private static List<LocalCacheManager> cacheManagers;
/*      */   private final String scheme;
/*   84 */   protected final Map<File, RandomAccessFile> fileHandles = new LinkedHashMap(140, 0.75F, true);
/*      */   protected FileLock cacheLock;
/*   86 */   long[] lastWrittenTickTimes = new long[Instrument.values().length];
/*      */   protected int intraperiodNum;
/*   88 */   private final Map<File, DelayedWriteTask> delayedWriteTasks = new HashMap();
/*      */   private Timer timer;
/*      */   private WriteDelayedTasks writeDelayedTasks;
/*   91 */   private long[] lastOrderUpdateTimes = new long[Instrument.values().length];
/*      */   private static final IFeedCommissionManager ZERO_FEED_COMMISSION_MANAGER;
/*      */   protected File firstTimesLockFile;
/*      */ 
/*      */   public LocalCacheManager(String scheme, boolean purgeOnWrongVersion)
/*      */     throws DataCacheException
/*      */   {
/*  102 */     super(purgeOnWrongVersion);
/*      */ 
/*   93 */     for (int i = 0; i < this.lastOrderUpdateTimes.length; i++) {
/*   94 */       this.lastOrderUpdateTimes[i] = -9223372036854775808L;
/*      */     }
/*      */ 
/*  103 */     this.scheme = scheme;
/*  104 */     File cacheDir = new File(FilePathManager.getInstance().getCacheDirectory());
/*      */ 
/*  107 */     int maxCacheInstances = 500;
/*  108 */     String prop = System.getProperty("jforex.max.cache.instances");
/*  109 */     if (prop != null) {
/*      */       try {
/*  111 */         maxCacheInstances = Integer.parseInt(prop);
/*      */       } catch (NumberFormatException e) {
/*  113 */         LOGGER.warn(e.getMessage(), e);
/*      */       }
/*      */     }
/*  116 */     for (int i = 0; i < maxCacheInstances; i++) {
/*  117 */       File lockFile = new File(cacheDir, new StringBuilder().append("lock").append(i).append(".lck").toString());
/*      */       try {
/*  119 */         FileChannel channel = new RandomAccessFile(lockFile, "rw").getChannel();
/*      */         try {
/*  121 */           FileLock lock = channel.tryLock();
/*  122 */           if ((lock != null) && (lock.isValid())) {
/*  123 */             this.cacheLock = lock;
/*  124 */             this.intraperiodNum = i;
/*      */ 
/*  127 */             if (cacheDir.exists()) {
/*  128 */               File[] instruments = cacheDir.listFiles();
/*  129 */               for (File instrumentDir : instruments) {
/*  130 */                 if (instrumentDir.isDirectory()) {
/*  131 */                   File intraperiodDir = new File(instrumentDir, new StringBuilder().append("intraperiod").append(this.intraperiodNum == 0 ? "" : Integer.valueOf(this.intraperiodNum)).toString());
/*      */ 
/*  133 */                   if (intraperiodDir.exists()) {
/*  134 */                     File[] intraperiodFiles = intraperiodDir.listFiles();
/*  135 */                     for (File intraperiodFile : intraperiodFiles) {
/*  136 */                       if ((!intraperiodFile.isFile()) || 
/*  137 */                         (intraperiodFile.delete())) continue;
/*  138 */                       this.cacheLock.release();
/*  139 */                       channel.close();
/*  140 */                       throw new IOException(new StringBuilder().append("Cannot delete old cache files [").append(intraperiodFile.getPath()).append("]").toString());
/*      */                     }
/*      */                   }
/*      */                 }
/*      */               }
/*      */ 
/*      */             }
/*      */ 
/*  148 */             break;
/*      */           }
/*      */         }
/*      */         catch (OverlappingFileLockException e)
/*      */         {
/*  153 */           channel.close();
/*      */         }
/*      */       } catch (IOException e) {
/*  156 */         LOGGER.warn(e.getMessage(), e);
/*      */       }
/*      */     }
/*  159 */     if ((this.cacheLock == null) || (!this.cacheLock.isValid()))
/*      */     {
/*  161 */       throw new DataCacheException("Can not lock cache");
/*      */     }
/*      */ 
/*  164 */     for (int i = 0; i < this.lastWrittenTickTimes.length; i++) {
/*  165 */       this.lastWrittenTickTimes[i] = -9223372036854775808L;
/*      */     }
/*      */ 
/*  168 */     this.timer = new Timer("LocalCacheTimer", true);
/*  169 */     CloseFileHandles checkTask = new CloseFileHandles(null);
/*  170 */     this.timer.schedule(checkTask, 5000L, 60000L);
/*  171 */     CloseFileLocks checkLocksTask = new CloseFileLocks(null);
/*  172 */     this.timer.schedule(checkLocksTask, 5000L, 10000L);
/*  173 */     this.writeDelayedTasks = new WriteDelayedTasks(null);
/*  174 */     this.timer.schedule(this.writeDelayedTasks, 5000L, 5000L);
/*  175 */     cacheManagers.add(this);
/*      */   }
/*      */ 
/*      */   public void subscribedToInstrument(Instrument instrument)
/*      */   {
/*  181 */     File cacheDir = new File(FilePathManager.getInstance().getCacheDirectory());
/*  182 */     File intraperiodDir = new File(new File(cacheDir, instrument.name()), new StringBuilder().append("intraperiod").append(this.intraperiodNum == 0 ? "" : Integer.valueOf(this.intraperiodNum)).toString());
/*  183 */     if (intraperiodDir.exists()) {
/*  184 */       File[] intraperiodFiles = intraperiodDir.listFiles();
/*  185 */       for (File intraperiodFile : intraperiodFiles)
/*  186 */         if ((intraperiodFile.isFile()) && (intraperiodFile.getName().endsWith("_ticks.bin"))) {
/*  187 */           ReentrantReadWriteLock rwLock = getLock(intraperiodFile);
/*  188 */           ReentrantReadWriteLock.WriteLock wLock = rwLock.writeLock();
/*  189 */           wLock.lock();
/*      */           try {
/*      */             try {
/*  192 */               RandomAccessFile fileHandle = getIntraPeriodFileHandle(intraperiodFile);
/*  193 */               clearDelayedWriteTasks(intraperiodFile);
/*  194 */               fileHandle.close();
/*      */             } catch (IOException e) {
/*  196 */               LOGGER.error(e.getMessage(), e);
/*      */             }
/*  198 */             synchronized (this.fileHandles) {
/*  199 */               this.fileHandles.remove(intraperiodFile);
/*      */             }
/*  201 */             if (!intraperiodFile.delete())
/*  202 */               LOGGER.error(new StringBuilder().append("Cannot delete old ticks intraperiod file [").append(intraperiodFile.getPath()).append("]").toString());
/*      */           }
/*      */           finally {
/*  205 */             wLock.unlock();
/*      */           }
/*      */         }
/*      */     }
/*      */   }
/*      */ 
/*      */   public void unsubscribedFromInstrument(Instrument instrument)
/*      */   {
/*  213 */     clearDelayedWriteTasks(instrument);
/*  214 */     this.lastWrittenTickTimes[instrument.ordinal()] = -9223372036854775808L;
/*      */   }
/*      */ 
/*      */   public static void deleteCacheBeforeHalt() {
/*  218 */     for (LocalCacheManager cacheManager : cacheManagers) {
/*  219 */       Thread lockAndDeleteThread = new Thread(cacheManager)
/*      */       {
/*      */         public void run() {
/*  222 */           this.val$cacheManager.deleteIntaperiodCacheBeforeHalt();
/*      */         }
/*      */       };
/*  225 */       lockAndDeleteThread.start();
/*      */     }
/*      */ 
/*  228 */     long startTime = System.currentTimeMillis();
/*  229 */     while ((cacheManagers.size() > 0) && (System.currentTimeMillis() < startTime + 30000L)) {
/*      */       try {
/*  231 */         Thread.sleep(100L);
/*      */       } catch (InterruptedException e) {
/*  233 */         LOGGER.error(e.getMessage(), e);
/*      */       }
/*      */     }
/*  236 */     deleteChunksCache();
/*      */   }
/*      */ 
/*      */   private static void deleteChunksCache() {
/*  240 */     File dirToDelete = new File(FilePathManager.getInstance().getCacheDirectory());
/*      */ 
/*  242 */     if (dirToDelete.exists()) {
/*  243 */       File[] files = dirToDelete.listFiles();
/*  244 */       for (File file : files) {
/*  245 */         if (file.isDirectory())
/*  246 */           deleteCacheRecursive(file);
/*      */         else {
/*  248 */           file.delete();
/*      */         }
/*      */       }
/*      */     }
/*  252 */     dirToDelete.delete(); } 
/*      */   // ERROR //
/*      */   public void deleteIntaperiodCacheBeforeHalt() { // Byte code:
/*      */     //   0: aload_0
/*      */     //   1: getfield 119	com/dukascopy/charts/data/datacache/LocalCacheManager:fileHandlesCache	Ljava/util/Map;
/*      */     //   4: dup
/*      */     //   5: astore_1
/*      */     //   6: monitorenter
/*      */     //   7: aload_0
/*      */     //   8: getfield 8	com/dukascopy/charts/data/datacache/LocalCacheManager:fileHandles	Ljava/util/Map;
/*      */     //   11: dup
/*      */     //   12: astore_2
/*      */     //   13: monitorenter
/*      */     //   14: aload_0
/*      */     //   15: getfield 67	com/dukascopy/charts/data/datacache/LocalCacheManager:timer	Ljava/util/Timer;
/*      */     //   18: invokevirtual 120	java/util/Timer:cancel	()V
/*      */     //   21: ldc2_w 121
/*      */     //   24: invokestatic 114	java/lang/Thread:sleep	(J)V
/*      */     //   27: goto +17 -> 44
/*      */     //   30: astore_3
/*      */     //   31: getstatic 3	com/dukascopy/charts/data/datacache/LocalCacheManager:LOGGER	Lorg/slf4j/Logger;
/*      */     //   34: aload_3
/*      */     //   35: invokevirtual 116	java/lang/InterruptedException:getMessage	()Ljava/lang/String;
/*      */     //   38: aload_3
/*      */     //   39: invokeinterface 26 3 0
/*      */     //   44: aload_0
/*      */     //   45: getfield 119	com/dukascopy/charts/data/datacache/LocalCacheManager:fileHandlesCache	Ljava/util/Map;
/*      */     //   48: invokeinterface 123 1 0
/*      */     //   53: invokeinterface 124 1 0
/*      */     //   58: astore_3
/*      */     //   59: aload_3
/*      */     //   60: invokeinterface 102 1 0
/*      */     //   65: ifeq +100 -> 165
/*      */     //   68: aload_3
/*      */     //   69: invokeinterface 103 1 0
/*      */     //   74: checkcast 125	java/util/Map$Entry
/*      */     //   77: astore 4
/*      */     //   79: aload 4
/*      */     //   81: invokeinterface 126 1 0
/*      */     //   86: checkcast 127	com/dukascopy/charts/data/datacache/CacheManager$FileCacheItem
/*      */     //   89: astore 5
/*      */     //   91: aload 5
/*      */     //   93: getfield 128	com/dukascopy/charts/data/datacache/CacheManager$FileCacheItem:fileHandles	[Ljava/io/RandomAccessFile;
/*      */     //   96: astore 6
/*      */     //   98: aload 6
/*      */     //   100: arraylength
/*      */     //   101: istore 7
/*      */     //   103: iconst_0
/*      */     //   104: istore 8
/*      */     //   106: iload 8
/*      */     //   108: iload 7
/*      */     //   110: if_icmpge +46 -> 156
/*      */     //   113: aload 6
/*      */     //   115: iload 8
/*      */     //   117: aaload
/*      */     //   118: astore 9
/*      */     //   120: aload 9
/*      */     //   122: ifnull +28 -> 150
/*      */     //   125: aload 9
/*      */     //   127: invokevirtual 93	java/io/RandomAccessFile:close	()V
/*      */     //   130: goto +20 -> 150
/*      */     //   133: astore 10
/*      */     //   135: getstatic 3	com/dukascopy/charts/data/datacache/LocalCacheManager:LOGGER	Lorg/slf4j/Logger;
/*      */     //   138: aload 10
/*      */     //   140: invokevirtual 60	java/io/IOException:getMessage	()Ljava/lang/String;
/*      */     //   143: aload 10
/*      */     //   145: invokeinterface 94 3 0
/*      */     //   150: iinc 8 1
/*      */     //   153: goto -47 -> 106
/*      */     //   156: aload_3
/*      */     //   157: invokeinterface 129 1 0
/*      */     //   162: goto -103 -> 59
/*      */     //   165: aload_0
/*      */     //   166: getfield 8	com/dukascopy/charts/data/datacache/LocalCacheManager:fileHandles	Ljava/util/Map;
/*      */     //   169: invokeinterface 123 1 0
/*      */     //   174: invokeinterface 124 1 0
/*      */     //   179: astore_3
/*      */     //   180: aload_3
/*      */     //   181: invokeinterface 102 1 0
/*      */     //   186: ifeq +65 -> 251
/*      */     //   189: aload_3
/*      */     //   190: invokeinterface 103 1 0
/*      */     //   195: checkcast 125	java/util/Map$Entry
/*      */     //   198: astore 4
/*      */     //   200: aload 4
/*      */     //   202: invokeinterface 126 1 0
/*      */     //   207: checkcast 35	java/io/RandomAccessFile
/*      */     //   210: astore 5
/*      */     //   212: aload 5
/*      */     //   214: ifnull +28 -> 242
/*      */     //   217: aload 5
/*      */     //   219: invokevirtual 93	java/io/RandomAccessFile:close	()V
/*      */     //   222: goto +20 -> 242
/*      */     //   225: astore 6
/*      */     //   227: getstatic 3	com/dukascopy/charts/data/datacache/LocalCacheManager:LOGGER	Lorg/slf4j/Logger;
/*      */     //   230: aload 6
/*      */     //   232: invokevirtual 60	java/io/IOException:getMessage	()Ljava/lang/String;
/*      */     //   235: aload 6
/*      */     //   237: invokeinterface 94 3 0
/*      */     //   242: aload_3
/*      */     //   243: invokeinterface 129 1 0
/*      */     //   248: goto -68 -> 180
/*      */     //   251: invokestatic 9	com/dukascopy/api/Instrument:values	()[Lcom/dukascopy/api/Instrument;
/*      */     //   254: astore_3
/*      */     //   255: aload_3
/*      */     //   256: arraylength
/*      */     //   257: istore 4
/*      */     //   259: iconst_0
/*      */     //   260: istore 5
/*      */     //   262: iload 5
/*      */     //   264: iload 4
/*      */     //   266: if_icmpge +202 -> 468
/*      */     //   269: aload_3
/*      */     //   270: iload 5
/*      */     //   272: aaload
/*      */     //   273: astore 6
/*      */     //   275: new 27	java/lang/StringBuilder
/*      */     //   278: dup
/*      */     //   279: aload_0
/*      */     //   280: invokevirtual 130	com/dukascopy/charts/data/datacache/LocalCacheManager:getCacheDirectory	()Ljava/lang/String;
/*      */     //   283: invokespecial 131	java/lang/StringBuilder:<init>	(Ljava/lang/String;)V
/*      */     //   286: astore 7
/*      */     //   288: aload 7
/*      */     //   290: aload 6
/*      */     //   292: invokevirtual 84	com/dukascopy/api/Instrument:name	()Ljava/lang/String;
/*      */     //   295: invokevirtual 30	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   298: getstatic 132	java/io/File:separatorChar	C
/*      */     //   301: invokevirtual 133	java/lang/StringBuilder:append	(C)Ljava/lang/StringBuilder;
/*      */     //   304: pop
/*      */     //   305: aload 7
/*      */     //   307: ldc 46
/*      */     //   309: invokevirtual 30	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*      */     //   312: pop
/*      */     //   313: aload_0
/*      */     //   314: getfield 42	com/dukascopy/charts/data/datacache/LocalCacheManager:intraperiodNum	I
/*      */     //   317: ifeq +13 -> 330
/*      */     //   320: aload 7
/*      */     //   322: aload_0
/*      */     //   323: getfield 42	com/dukascopy/charts/data/datacache/LocalCacheManager:intraperiodNum	I
/*      */     //   326: invokevirtual 31	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
/*      */     //   329: pop
/*      */     //   330: aload 7
/*      */     //   332: getstatic 132	java/io/File:separatorChar	C
/*      */     //   335: invokevirtual 133	java/lang/StringBuilder:append	(C)Ljava/lang/StringBuilder;
/*      */     //   338: pop
/*      */     //   339: new 17	java/io/File
/*      */     //   342: dup
/*      */     //   343: aload 7
/*      */     //   345: invokevirtual 33	java/lang/StringBuilder:toString	()Ljava/lang/String;
/*      */     //   348: invokespecial 20	java/io/File:<init>	(Ljava/lang/String;)V
/*      */     //   351: astore 8
/*      */     //   353: aload 8
/*      */     //   355: invokevirtual 43	java/io/File:exists	()Z
/*      */     //   358: ifeq +64 -> 422
/*      */     //   361: aload 8
/*      */     //   363: invokevirtual 44	java/io/File:listFiles	()[Ljava/io/File;
/*      */     //   366: astore 9
/*      */     //   368: aload 9
/*      */     //   370: astore 10
/*      */     //   372: aload 10
/*      */     //   374: arraylength
/*      */     //   375: istore 11
/*      */     //   377: iconst_0
/*      */     //   378: istore 12
/*      */     //   380: iload 12
/*      */     //   382: iload 11
/*      */     //   384: if_icmpge +38 -> 422
/*      */     //   387: aload 10
/*      */     //   389: iload 12
/*      */     //   391: aaload
/*      */     //   392: astore 13
/*      */     //   394: aload 13
/*      */     //   396: invokevirtual 45	java/io/File:isDirectory	()Z
/*      */     //   399: ifeq +11 -> 410
/*      */     //   402: aload 13
/*      */     //   404: invokestatic 118	com/dukascopy/charts/data/datacache/LocalCacheManager:deleteCacheRecursive	(Ljava/io/File;)V
/*      */     //   407: goto +9 -> 416
/*      */     //   410: aload 13
/*      */     //   412: invokevirtual 51	java/io/File:delete	()Z
/*      */     //   415: pop
/*      */     //   416: iinc 12 1
/*      */     //   419: goto -39 -> 380
/*      */     //   422: aload 8
/*      */     //   424: invokevirtual 51	java/io/File:delete	()Z
/*      */     //   427: pop
/*      */     //   428: aload_0
/*      */     //   429: getfield 41	com/dukascopy/charts/data/datacache/LocalCacheManager:cacheLock	Ljava/nio/channels/FileLock;
/*      */     //   432: invokevirtual 134	java/nio/channels/FileLock:channel	()Ljava/nio/channels/FileChannel;
/*      */     //   435: astore 9
/*      */     //   437: aload 9
/*      */     //   439: invokevirtual 53	java/nio/channels/FileChannel:close	()V
/*      */     //   442: goto +20 -> 462
/*      */     //   445: astore 9
/*      */     //   447: getstatic 3	com/dukascopy/charts/data/datacache/LocalCacheManager:LOGGER	Lorg/slf4j/Logger;
/*      */     //   450: aload 9
/*      */     //   452: invokevirtual 60	java/io/IOException:getMessage	()Ljava/lang/String;
/*      */     //   455: aload 9
/*      */     //   457: invokeinterface 94 3 0
/*      */     //   462: iinc 5 1
/*      */     //   465: goto -203 -> 262
/*      */     //   468: getstatic 82	com/dukascopy/charts/data/datacache/LocalCacheManager:cacheManagers	Ljava/util/List;
/*      */     //   471: aload_0
/*      */     //   472: invokeinterface 135 2 0
/*      */     //   477: pop
/*      */     //   478: ldc2_w 112
/*      */     //   481: invokestatic 114	java/lang/Thread:sleep	(J)V
/*      */     //   484: goto -6 -> 478
/*      */     //   487: astore_3
/*      */     //   488: getstatic 3	com/dukascopy/charts/data/datacache/LocalCacheManager:LOGGER	Lorg/slf4j/Logger;
/*      */     //   491: aload_3
/*      */     //   492: invokevirtual 116	java/lang/InterruptedException:getMessage	()Ljava/lang/String;
/*      */     //   495: aload_3
/*      */     //   496: invokeinterface 94 3 0
/*      */     //   501: goto -23 -> 478
/*      */     //   504: astore 14
/*      */     //   506: aload_2
/*      */     //   507: monitorexit
/*      */     //   508: aload 14
/*      */     //   510: athrow
/*      */     //   511: astore 15
/*      */     //   513: aload_1
/*      */     //   514: monitorexit
/*      */     //   515: aload 15
/*      */     //   517: athrow
/*      */     //
/*      */     // Exception table:
/*      */     //   from	to	target	type
/*      */     //   21	27	30	java/lang/InterruptedException
/*      */     //   125	130	133	java/io/IOException
/*      */     //   217	222	225	java/io/IOException
/*      */     //   428	442	445	java/io/IOException
/*      */     //   478	484	487	java/lang/InterruptedException
/*      */     //   14	508	504	finally
/*      */     //   7	515	511	finally } 
/*  335 */   protected static void deleteCacheRecursive(File dirToDelete) { if (dirToDelete.exists()) {
/*  336 */       File[] files = dirToDelete.listFiles();
/*  337 */       for (File file : files) {
/*  338 */         if (file.isDirectory())
/*  339 */           deleteCacheRecursive(file);
/*      */         else {
/*  341 */           file.delete();
/*      */         }
/*      */       }
/*  344 */       dirToDelete.delete();
/*      */     } }
/*      */ 
/*      */   public void newTickChunkStart(Instrument instrument, long time) {
/*  355 */     long fileStartTime = DataCacheUtils.getChunkStartFast(Period.TICK, time);
/*      */     File file;
/*      */     try {
/*  358 */       file = getIntraPeriodFile(instrument, Period.TICK, null, fileStartTime);
/*      */     } catch (DataCacheException e) {
/*  360 */       LOGGER.error(e.getMessage(), e);
/*  361 */       return;
/*      */     }
/*      */ 
/*  364 */     synchronized (this.delayedWriteTasks) {
/*  365 */       TickWriteTask tickWriteTask = (TickWriteTask)this.delayedWriteTasks.get(file);
/*  366 */       if (tickWriteTask == null) {
/*  367 */         tickWriteTask = new TickWriteTask(null);
/*  368 */         tickWriteTask.instrument = instrument;
/*  369 */         tickWriteTask.intraperiodFile = file;
/*  370 */         this.delayedWriteTasks.put(file, tickWriteTask);
/*      */       }
/*  372 */       int bytesCount = TickData.getLength(this.version);
/*  373 */       tickWriteTask.ensureCapacity(tickWriteTask.size + bytesCount);
/*  374 */       TickData data = new TickData();
/*  375 */       data.time = -9223372036854775808L;
/*  376 */       data.ask = 0.0D;
/*  377 */       data.bid = 0.0D;
/*  378 */       data.askVol = 0.0D;
/*  379 */       data.bidVol = 0.0D;
/*  380 */       data.toBytes(this.version, fileStartTime, instrument.getPipValue(), tickWriteTask.data, tickWriteTask.size);
/*  381 */       tickWriteTask.size += bytesCount;
/*  382 */       tickWriteTask.lastWrittenTickTime = time;
/*      */     }
/*      */   }
/*      */ 
/*  387 */   public void newTick(Instrument instrument, long time, double ask, double bid, double askVol, double bidVol, boolean allowWrite) { long fileStartTime = DataCacheUtils.getChunkStartFast(Period.TICK, time);
/*      */     File file;
/*      */     try {
/*  390 */       file = getIntraPeriodFile(instrument, Period.TICK, null, fileStartTime);
/*      */     } catch (DataCacheException e) {
/*  392 */       LOGGER.error(e.getMessage(), e);
/*  393 */       return;
/*      */     }
/*      */ 
/*  396 */     boolean forceFlush = false;
/*  397 */     synchronized (this.delayedWriteTasks) {
/*  398 */       TickWriteTask tickWriteTask = (TickWriteTask)this.delayedWriteTasks.get(file);
/*  399 */       if (tickWriteTask == null) {
/*  400 */         tickWriteTask = new TickWriteTask(null);
/*  401 */         tickWriteTask.instrument = instrument;
/*  402 */         tickWriteTask.intraperiodFile = file;
/*  403 */         this.delayedWriteTasks.put(file, tickWriteTask);
/*      */       }
/*  405 */       int bytesCount = TickData.getLength(this.version);
/*  406 */       tickWriteTask.ensureCapacity(tickWriteTask.size + bytesCount);
/*  407 */       TickData data = new TickData();
/*  408 */       data.time = time;
/*  409 */       data.ask = ask;
/*  410 */       data.bid = bid;
/*  411 */       data.askVol = askVol;
/*  412 */       data.bidVol = bidVol;
/*  413 */       data.toBytes(this.version, fileStartTime, instrument.getPipValue(), tickWriteTask.data, tickWriteTask.size);
/*  414 */       tickWriteTask.size += bytesCount;
/*  415 */       tickWriteTask.lastWrittenTickTime = time;
/*  416 */       if ((allowWrite) && (tickWriteTask.size > 524288))
/*      */       {
/*  418 */         forceFlush = true;
/*      */       }
/*      */     }
/*  421 */     if (forceFlush)
/*  422 */       flushDelayedWriteTasks(); }
/*      */ 
/*      */   private void processTickWriteTasks(TickWriteTask tickWriteTask, RandomAccessFile fileHandle)
/*      */     throws DataCacheException
/*      */   {
/*      */     try
/*      */     {
/*  429 */       if (this.lastWrittenTickTimes[tickWriteTask.instrument.ordinal()] == -9223372036854775808L) {
/*  430 */         fileHandle.seek(0L);
/*  431 */         fileHandle.setLength(0L);
/*      */       }
/*  433 */       fileHandle.seek(fileHandle.length());
/*  434 */       fileHandle.write(tickWriteTask.data, 0, tickWriteTask.size);
/*      */ 
/*  436 */       this.lastWrittenTickTimes[tickWriteTask.instrument.ordinal()] = tickWriteTask.lastWrittenTickTime;
/*      */     } catch (IOException e) {
/*  438 */       throw new DataCacheException(e);
/*      */     }
/*      */   }
/*      */ 
/*      */   protected RandomAccessFile getIntraPeriodFileHandle(File file)
/*      */     throws IOException
/*      */   {
/*  481 */     synchronized (this.fileHandles) {
/*  482 */       RandomAccessFile fileHandle = (RandomAccessFile)this.fileHandles.get(file);
/*  483 */       if (fileHandle != null) {
/*  484 */         return fileHandle;
/*      */       }
/*  486 */       if (this.fileHandles.size() >= 100)
/*  487 */         new CloseFileHandles(null).run();
/*      */       try
/*      */       {
/*  490 */         file.deleteOnExit();
/*      */       }
/*      */       catch (NullPointerException e)
/*      */       {
/*      */       }
/*      */ 
/*  496 */       fileHandle = new RandomAccessFile(file, "rw");
/*  497 */       this.fileHandles.put(file, fileHandle);
/*  498 */       return fileHandle;
/*      */     }
/*      */   }
/*      */ 
/*      */   public void saveChunkInCache(Instrument instrument, Period period, OfferSide side, long from, Data[] data) throws DataCacheException {
/*  503 */     super.saveChunkInCache(instrument, period, side, from, data);
/*      */ 
/*  506 */     from = DataCacheUtils.getChunkStart(period, from);
/*  507 */     File file = getIntraPeriodFile(instrument, period, side, from);
/*  508 */     ReentrantReadWriteLock rwLock = getLock(file);
/*  509 */     ReentrantReadWriteLock.WriteLock wLock = rwLock.writeLock();
/*  510 */     wLock.lock();
/*      */     try {
/*  512 */       clearDelayedWriteTasks(file);
/*  513 */       if ((!file.getParentFile().mkdirs()) && (!file.getParentFile().exists())) {
/*  514 */         throw new DataCacheException("Cannot create cache directory");
/*      */       }
/*  516 */       if (file.exists())
/*      */         try {
/*  518 */           RandomAccessFile fileHandle = getIntraPeriodFileHandle(file);
/*  519 */           fileHandle.close();
/*  520 */           synchronized (this.fileHandles) {
/*  521 */             this.fileHandles.remove(file);
/*      */           }
/*  523 */           if (!file.delete())
/*  524 */             LOGGER.warn(new StringBuilder().append("Cannot delete old file [").append(file.getPath()).append("]").toString());
/*      */         }
/*      */         catch (IOException e) {
/*  527 */           LOGGER.warn(e.getMessage(), e);
/*      */         }
/*      */     }
/*      */     finally {
/*  531 */       wLock.unlock();
/*      */     }
/*      */   }
/*      */ 
/*      */   public long[][] getIntraperiodIntervalsToLoad(Instrument instrument, Period period, OfferSide side, long from, long to) throws DataCacheException
/*      */   {
/*  537 */     if (LOGGER.isTraceEnabled()) {
/*  538 */       DateFormat format = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss SSS");
/*  539 */       format.setTimeZone(TimeZone.getTimeZone("GMT"));
/*  540 */       LOGGER.trace(new StringBuilder().append("Finding intraperiod intervals that we need to load, instrument [").append(instrument).append("], period [").append(period).append("], side [").append(side).append("], from [").append(format.format(new Date(from))).append("], to [").append(format.format(new Date(to))).append("]").toString());
/*      */     }
/*      */ 
/*  543 */     assert ((period == Period.TICK) || (from == DataCacheUtils.getCandleStart(period, from)));
/*  544 */     assert ((period == Period.TICK) || (to == DataCacheUtils.getCandleStart(period, to)));
/*  545 */     long fileStartTime = DataCacheUtils.getChunkStart(period, from);
/*  546 */     File file = getIntraPeriodFile(instrument, period, side, fileStartTime);
/*  547 */     ReentrantReadWriteLock rwLock = getLock(file);
/*  548 */     ReentrantReadWriteLock.WriteLock wLock = rwLock.writeLock();
/*  549 */     wLock.lock();
/*      */     try {
/*  551 */       if ((file.exists()) || (delayedTasksExists(file))) {
/*      */         try {
/*  553 */           if ((!file.getParentFile().mkdirs()) && (!file.getParentFile().exists())) {
/*  554 */             LOGGER.error("Cannot create cache directory");
/*      */           }
/*  556 */           RandomAccessFile fileHandle = getIntraPeriodFileHandle(file);
/*  557 */           flushDelayedWriteTasks(file, fileHandle);
/*  558 */           fileHandle.seek(0L);
/*  559 */           if (period == Period.TICK)
/*      */           {
/*  561 */             TickData[] firstLastTicks = readFirstLastTicks(instrument, fileStartTime, fileHandle);
/*  562 */             if (firstLastTicks == null)
/*      */             {
/*  565 */               fileHandle.close();
/*  566 */               synchronized (this.fileHandles) {
/*  567 */                 this.fileHandles.remove(file);
/*      */               }
/*  569 */               if (!file.delete()) {
/*  570 */                 throw new DataCacheException(new StringBuilder().append("Data cache corrupted, cannot delete file [").append(file.getPath()).append("]").toString());
/*      */               }
/*  572 */               ??? = new long[][] { { from, DataCacheUtils.getChunkEnd(period, from) } };
/*      */ 
/*  679 */               wLock.unlock(); return ???;
/*      */             }
/*  575 */             if (this.lastWrittenTickTimes[instrument.ordinal()] != -9223372036854775808L) {
/*  576 */               if (firstLastTicks[0].time == -9223372036854775808L)
/*      */               {
/*  578 */                 ??? = new long[0][];
/*      */ 
/*  679 */                 wLock.unlock(); return ???;
/*      */               }
/*  579 */               if (from < firstLastTicks[0].time) {
/*  580 */                 ??? = new long[][] { { from, firstLastTicks[0].time } };
/*      */ 
/*  679 */                 wLock.unlock(); return ???;
/*      */               }
/*  582 */               ??? = new long[0][];
/*      */ 
/*  679 */               wLock.unlock(); return ???;
/*      */             }
/*  585 */             if (firstLastTicks[0].time == -9223372036854775808L) {
/*  586 */               if (to > firstLastTicks[1].time) {
/*  587 */                 ??? = new long[][] { { firstLastTicks[1].time, to } };
/*      */ 
/*  679 */                 wLock.unlock(); return ???;
/*      */               }
/*  589 */               ??? = new long[0][];
/*      */ 
/*  679 */               wLock.unlock(); return ???;
/*      */             }
/*  591 */             if (from < firstLastTicks[0].time) {
/*  592 */               if (to > firstLastTicks[1].time) {
/*  593 */                 ??? = new long[][] { { from, firstLastTicks[0].time }, { firstLastTicks[1].time, to } };
/*      */ 
/*  679 */                 wLock.unlock(); return ???;
/*      */               }
/*  595 */               ??? = new long[][] { { from, firstLastTicks[0].time } };
/*      */ 
/*  679 */               wLock.unlock(); return ???;
/*      */             }
/*  598 */             if (to > firstLastTicks[1].time) {
/*  599 */               ??? = new long[][] { { firstLastTicks[1].time, to } };
/*      */ 
/*  679 */               wLock.unlock(); return ???;
/*      */             }
/*  601 */             ??? = new long[0][];
/*      */ 
/*  679 */             wLock.unlock(); return ???;
/*      */           }
/*  606 */           long firstCandleTime = DataCacheUtils.getFirstCandleInChunk(period, fileStartTime);
/*  607 */           int candlesToSkip = DataCacheUtils.getCandlesCountBetween(period, firstCandleTime, DataCacheUtils.getPreviousCandleStart(period, from));
/*  608 */           int intraPeriodCandleDataLength = IntraPeriodCandleData.getLength(this.version);
/*  609 */           int position = intraPeriodCandleDataLength * candlesToSkip;
/*  610 */           if (position > fileHandle.length())
/*      */           {
/*  613 */             long[][] arrayOfLong1 = { { from, to } };
/*      */ 
/*  679 */             wLock.unlock(); return arrayOfLong1;
/*      */           }
/*  615 */           fileHandle.seek(position);
/*  616 */           long intervalStart = from;
/*  617 */           long nextExpectedTime = from;
/*  618 */           byte[] buff = new byte[intraPeriodCandleDataLength * 10];
/*  619 */           IntraPeriodCandleData candleData = new IntraPeriodCandleData();
/*  620 */           List intervals = new ArrayList();
/*      */           int readBytes;
/*      */           do
/*      */           {
/*  625 */             readBytes = 0;
/*  626 */             while (((i = fileHandle.read(buff, readBytes, buff.length - readBytes)) > -1) && (readBytes < buff.length)) {
/*  627 */               readBytes += i;
/*      */             }
/*      */ 
/*  630 */             for (int offset = 0; (offset < readBytes) && (nextExpectedTime <= to); nextExpectedTime = DataCacheUtils.getNextCandleStart(period, nextExpectedTime))
/*      */             {
/*  632 */               if (offset + intraPeriodCandleDataLength <= readBytes)
/*      */               {
/*  634 */                 candleData.fromBytes(this.version, firstCandleTime, instrument.getPipValue(), buff, offset);
/*  635 */                 if ((nextExpectedTime != candleData.time) && (!candleData.empty)) {
/*  636 */                   DateFormat format = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss SSS");
/*  637 */                   format.setTimeZone(TimeZone.getTimeZone("GMT"));
/*  638 */                   LOGGER.warn(new StringBuilder().append("intraperiod has wrong time at position [").append(position).append("], expected time [").append(format.format(new Date(nextExpectedTime))).append("], read time [").append(format.format(new Date(candleData.time))).append("], file [").append(file.getPath()).append("], length [").append(fileHandle.length()).append("]").toString());
/*      */ 
/*  642 */                   long[][] arrayOfLong2 = { { firstCandleTime, DataCacheUtils.getChunkEnd(period, from) } };
/*      */ 
/*  679 */                   wLock.unlock(); return arrayOfLong2;
/*      */                 }
/*  644 */                 if (!candleData.empty)
/*      */                 {
/*  647 */                   if (candleData.time == intervalStart)
/*      */                   {
/*  649 */                     intervalStart = DataCacheUtils.getNextCandleStart(period, intervalStart);
/*      */                   }
/*      */                   else
/*      */                   {
/*  653 */                     intervals.add(new long[] { intervalStart, DataCacheUtils.getCandleStart(period, candleData.time - 1L) });
/*  654 */                     intervalStart = DataCacheUtils.getNextCandleStart(period, candleData.time);
/*      */                   }
/*      */                 }
/*      */               }
/*  630 */               offset += intraPeriodCandleDataLength;
/*      */             }
/*      */ 
/*      */           }
/*      */ 
/*  662 */           while ((nextExpectedTime <= to) && (readBytes > 0));
/*      */ 
/*  664 */           if ((intervalStart < nextExpectedTime) || ((intervalStart == nextExpectedTime) && (intervalStart <= to)))
/*      */           {
/*  666 */             intervals.add(new long[] { intervalStart, to });
/*      */           }
/*  668 */           int i = (long[][])intervals.toArray(new long[intervals.size()][]);
/*      */ 
/*  679 */           wLock.unlock(); return i;
/*      */         }
/*      */         catch (IOException e)
/*      */         {
/*  672 */           throw new DataCacheException(e);
/*      */         }
/*      */       }
/*      */ 
/*  676 */       e = new long[][] { { from, to } };
/*      */       return e; } finally { wLock.unlock(); } throw localObject2;
/*      */   }
/*      */ 
/*      */   private TickData[] readFirstLastTicks(Instrument instrument, long firstChunkCandle, RandomAccessFile fileHandle) throws IOException
/*      */   {
/*  684 */     fileHandle.seek(0L);
/*  685 */     int tickDataLength = TickData.getLength(this.version);
/*  686 */     byte[] tickBuff = new byte[tickDataLength];
/*      */ 
/*  688 */     int readBytes = 0;
/*      */     int i;
/*  689 */     while (((i = fileHandle.read(tickBuff, readBytes, tickBuff.length - readBytes)) > -1) && (readBytes < tickBuff.length)) {
/*  690 */       readBytes += i;
/*      */     }
/*  692 */     if (readBytes < tickDataLength) {
/*  693 */       return null;
/*      */     }
/*  695 */     TickData firstTick = new TickData();
/*  696 */     firstTick.fromBytes(this.version, firstChunkCandle, instrument.getPipValue(), tickBuff, 0);
/*      */ 
/*  698 */     fileHandle.seek(fileHandle.length() - tickDataLength - fileHandle.length() % tickDataLength);
/*  699 */     readBytes = 0;
/*  700 */     while (((i = fileHandle.read(tickBuff, readBytes, tickBuff.length - readBytes)) > -1) && (readBytes < tickBuff.length)) {
/*  701 */       readBytes += i;
/*      */     }
/*  703 */     if (readBytes < tickDataLength) {
/*  704 */       return null;
/*      */     }
/*  706 */     TickData lastTick = new TickData();
/*  707 */     lastTick.fromBytes(this.version, firstChunkCandle, instrument.getPipValue(), tickBuff, 0);
/*  708 */     return new TickData[] { firstTick, lastTick };
/*      */   }
/*      */ 
/*      */   public void saveIntraperiodData(Instrument instrument, Period period, OfferSide side, Data[] data, boolean dataFromChunkStart)
/*      */     throws DataCacheException
/*      */   {
/*  714 */     if ((LOGGER.isTraceEnabled()) && (data.length > 1)) {
/*  715 */       LOGGER.trace(new StringBuilder().append("Saving intraperiod data in cache, instrument [").append(instrument).append("], period [").append(period).append("], side [").append(side).append("], data size [").append(data.length).append("]").toString());
/*      */     }
/*      */ 
/*  718 */     if (data.length == 0) {
/*  719 */       return;
/*      */     }
/*  721 */     if (period == Period.TICK) {
/*  722 */       saveIntraperiodDataForTicks(instrument, data, dataFromChunkStart);
/*      */     } else {
/*  724 */       long fileStartTime = DataCacheUtils.getChunkStart(period, data[0].time);
/*  725 */       File file = getIntraPeriodFile(instrument, period, side, fileStartTime);
/*  726 */       ReentrantReadWriteLock rwLock = getLock(file);
/*  727 */       ReentrantReadWriteLock.WriteLock wLock = rwLock.writeLock();
/*  728 */       wLock.lock();
/*      */       try {
/*  730 */         if ((!file.getParentFile().mkdirs()) && (!file.getParentFile().exists())) {
/*  731 */           throw new DataCacheException("Cannot create cache directory");
/*      */         }
/*  733 */         RandomAccessFile fileHandle = getIntraPeriodFileHandle(file);
/*  734 */         flushDelayedWriteTasks(file, fileHandle);
/*  735 */         saveIntraperiodCandleDataToFile(instrument, DataCacheUtils.getFirstCandleInChunk(period, fileStartTime), file, fileHandle, period, data);
/*      */       } catch (IOException e) {
/*  737 */         throw new DataCacheException(e);
/*      */       } finally {
/*  739 */         wLock.unlock();
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   private void saveIntraperiodCandleDataToFile(Instrument instrument, long firstChunkCandle, File file, RandomAccessFile fileHandle, Period period, Data[] data) throws DataCacheException {
/*      */     try {
/*  746 */       fileHandle.seek(0L);
/*  747 */       IntraPeriodCandleData emptyCandle = new IntraPeriodCandleData();
/*  748 */       emptyCandle.empty = true;
/*  749 */       int intraPeriodCandleDataLength = IntraPeriodCandleData.getLength(this.version);
/*  750 */       byte[] emptyCandleData = new byte[intraPeriodCandleDataLength];
/*  751 */       emptyCandle.toBytes(this.version, firstChunkCandle, instrument.getPipValue(), emptyCandleData, 0);
/*  752 */       byte[] candleBytes = new byte[intraPeriodCandleDataLength];
/*  753 */       long fileStartTime = DataCacheUtils.getChunkStart(period, data[0].time);
/*  754 */       long firstCandleTime = DataCacheUtils.getFirstCandleInChunkFast(period, fileStartTime);
/*  755 */       for (Data dataElement : data) {
/*  756 */         IntraPeriodCandleData candleData = (IntraPeriodCandleData)dataElement;
/*  757 */         int candlesToSkip = DataCacheUtils.getCandlesCountBetween(period, firstCandleTime, DataCacheUtils.getPreviousCandleStart(period, candleData.time));
/*  758 */         int position = intraPeriodCandleDataLength * candlesToSkip;
/*  759 */         while (fileHandle.length() < position) {
/*  760 */           fileHandle.seek(fileHandle.length() - fileHandle.length() % intraPeriodCandleDataLength);
/*  761 */           fileHandle.write(emptyCandleData);
/*      */         }
/*  763 */         fileHandle.seek(position);
/*  764 */         candleData.toBytes(this.version, firstChunkCandle, instrument.getPipValue(), candleBytes, 0);
/*  765 */         fileHandle.write(candleBytes);
/*  766 */         if (assertionsEnabled()) {
/*  767 */           long expectedTime = DataCacheUtils.getTimeForNCandlesForward(period, firstCandleTime, candlesToSkip + 1);
/*  768 */           if (candleData.time != expectedTime) {
/*  769 */             throw new DataCacheException(new StringBuilder().append("Trying to write candle with time [").append(candleData.time).append("] in the place where should be candle with time [").append(expectedTime).append("] in file [").append(file.getPath()).append("]").toString());
/*      */           }
/*      */ 
/*  772 */           if ((position == fileHandle.getFilePointer() - intraPeriodCandleDataLength) && (DataCacheUtils.getTimeForNCandlesForward(period, firstCandleTime, (int)fileHandle.getFilePointer() / intraPeriodCandleDataLength) == candleData.time)) {
/*      */             continue;
/*      */           }
/*  775 */           throw new DataCacheException(new StringBuilder().append("Position doesn't belong to the candle, first candle time [").append(firstCandleTime).append("] position [").append(position).append("] in file [").append(file.getPath()).append("], candle time we wrote there [").append(candleData.time).append("]").toString());
/*      */         }
/*      */       }
/*      */     }
/*      */     catch (IOException e)
/*      */     {
/*  781 */       throw new DataCacheException(e);
/*      */     }
/*      */   }
/*      */ 
/*      */   protected void saveIntraperiodDataForTicks(Instrument instrument, Data[] data, boolean dataFromChunkStart)
/*      */     throws DataCacheException
/*      */   {
/*  788 */     int tickDataLength = TickData.getLength(this.version);
/*  789 */     long fileStartTime = DataCacheUtils.getChunkStart(Period.TICK, data[0].time);
/*  790 */     File file = getIntraPeriodFile(instrument, Period.TICK, null, fileStartTime);
/*  791 */     ReentrantReadWriteLock rwLock = getLock(file);
/*  792 */     ReentrantReadWriteLock.WriteLock wLock = rwLock.writeLock();
/*  793 */     wLock.lock();
/*      */     try {
/*  795 */       if ((!file.getParentFile().mkdirs()) && (!file.getParentFile().exists())) {
/*  796 */         throw new DataCacheException("Cannot create cache directory");
/*      */       }
/*  798 */       if (dataFromChunkStart)
/*      */       {
/*  800 */         Data[] newData = new Data[data.length + 1];
/*  801 */         newData[0] = new TickData(-9223372036854775808L, 0.0D, 0.0D, 0.0D, 0.0D);
/*  802 */         System.arraycopy(data, 0, newData, 1, data.length);
/*  803 */         data = newData;
/*      */       }
/*  805 */       if ((file.exists()) || (delayedTasksExists(file))) {
/*  806 */         if ((!file.getParentFile().mkdirs()) && (!file.getParentFile().exists())) {
/*  807 */           LOGGER.error("Cannot create cache directory");
/*      */         }
/*  809 */         RandomAccessFile fileHandle = getIntraPeriodFileHandle(file);
/*  810 */         flushDelayedWriteTasks(file, fileHandle);
/*      */ 
/*  816 */         long startPosition = findStartPositionInTicksFile(fileStartTime, fileHandle, data[(data.length - 1)].time);
/*  817 */         if ((startPosition >= 0L) && (startPosition < fileHandle.length()))
/*      */         {
/*  819 */           fileHandle.seek(startPosition);
/*  820 */           byte[] tickBuff = new byte[tickDataLength];
/*      */ 
/*  822 */           int readBytes = 0;
/*      */           int i;
/*  823 */           while (((i = fileHandle.read(tickBuff, readBytes, tickBuff.length - readBytes)) > -1) && (readBytes < tickBuff.length)) {
/*  824 */             readBytes += i;
/*      */           }
/*  826 */           if (readBytes < tickDataLength) {
/*  827 */             LOGGER.error(new StringBuilder().append("Couldn't read tick from intraperiod file [").append(file.getPath()).append("] at position [").append(startPosition).append("], file length [").append(fileHandle.length()).append("]").toString());
/*      */ 
/*  830 */             fileHandle.close();
/*  831 */             synchronized (this.fileHandles) {
/*  832 */               this.fileHandles.remove(file);
/*      */             }
/*  834 */             if (!file.delete()) {
/*  835 */               throw new DataCacheException(new StringBuilder().append("Data cache corrupted, cannot delete file [").append(file.getPath()).append("]").toString());
/*      */             }
/*      */ 
/*  838 */             fileHandle = getIntraPeriodFileHandle(file);
/*      */           }
/*      */           else {
/*  841 */             TickData tickAtPosition = new TickData();
/*  842 */             tickAtPosition.fromBytes(this.version, fileStartTime, instrument.getPipValue(), tickBuff, 0);
/*  843 */             if (data[(data.length - 1)].time == tickAtPosition.time)
/*      */             {
/*  845 */               startPosition += tickDataLength;
/*      */             }
/*      */ 
/*      */           }
/*      */ 
/*      */         }
/*      */ 
/*  853 */         if ((startPosition >= 0L) && (startPosition < fileHandle.length())) {
/*  854 */           fileHandle.seek(startPosition);
/*  855 */           byte[] fileBytes = new byte[(int)(fileHandle.length() - fileHandle.length() % tickDataLength - startPosition)];
/*      */ 
/*  857 */           int readBytes = 0;
/*      */           int i;
/*  858 */           while (((i = fileHandle.read(fileBytes, readBytes, fileBytes.length - readBytes)) > -1) && (readBytes < fileBytes.length)) {
/*  859 */             readBytes += i;
/*      */           }
/*  861 */           if (readBytes < fileBytes.length)
/*      */           {
/*  864 */             fileHandle.close();
/*  865 */             synchronized (this.fileHandles) {
/*  866 */               this.fileHandles.remove(file);
/*      */             }
/*  868 */             if (!file.delete()) {
/*  869 */               throw new DataCacheException(new StringBuilder().append("Data cache corrupted, cannot delete file [").append(file.getPath()).append("]").toString());
/*      */             }
/*      */ 
/*  872 */             fileHandle = getIntraPeriodFileHandle(file);
/*      */           }
/*      */           else {
/*  875 */             Data[] newData = new Data[data.length + fileBytes.length / tickDataLength];
/*  876 */             System.arraycopy(data, 0, newData, 0, data.length);
/*      */ 
/*  878 */             for (int j = 0; j < fileBytes.length / tickDataLength; j++) {
/*  879 */               TickData tickData = new TickData();
/*  880 */               tickData.fromBytes(this.version, fileStartTime, instrument.getPipValue(), fileBytes, j * tickDataLength);
/*  881 */               newData[(data.length + j)] = tickData;
/*      */             }
/*  883 */             if (assertionsEnabled()) {
/*  884 */               long prevTime = newData[0].time;
/*  885 */               for (Data dataElement : newData) {
/*  886 */                 if (prevTime > dataElement.time) {
/*  887 */                   DateFormat format = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss SSS");
/*  888 */                   format.setTimeZone(TimeZone.getTimeZone("GMT"));
/*  889 */                   throw new DataCacheException(new StringBuilder().append("Data consistency check failed for intraperiod file: [").append(file.getName()).append("]. ").append("Time of the previous tick [").append(format.format(new Date(prevTime))).append("], time of the next tick [").append(format.format(new Date(dataElement.time))).append("]").toString());
/*      */                 }
/*      */ 
/*  893 */                 prevTime = dataElement.time;
/*      */               }
/*      */             }
/*  896 */             data = newData;
/*      */           }
/*      */ 
/*      */         }
/*      */ 
/*  901 */         startPosition = findStartPositionInTicksFile(fileStartTime, fileHandle, data[0].time);
/*  902 */         if ((startPosition > 0L) && (startPosition < fileHandle.length()))
/*      */         {
/*  904 */           fileHandle.seek(startPosition);
/*  905 */           byte[] tickBuff = new byte[tickDataLength];
/*      */ 
/*  907 */           int readBytes = 0;
/*      */           int i;
/*  908 */           while (((i = fileHandle.read(tickBuff, readBytes, tickBuff.length - readBytes)) > -1) && (readBytes < tickBuff.length)) {
/*  909 */             readBytes += i;
/*      */           }
/*  911 */           if (readBytes < tickDataLength) {
/*  912 */             LOGGER.error(new StringBuilder().append("Couldn't read tick from intraperiod file [").append(file.getPath()).append("] at position [").append(startPosition).append("], file length [").append(fileHandle.length()).append("]").toString());
/*      */ 
/*  915 */             fileHandle.close();
/*  916 */             synchronized (this.fileHandles) {
/*  917 */               this.fileHandles.remove(file);
/*      */             }
/*  919 */             if (!file.delete()) {
/*  920 */               throw new DataCacheException(new StringBuilder().append("Data cache corrupted, cannot delete file [").append(file.getPath()).append("]").toString());
/*      */             }
/*      */ 
/*  923 */             fileHandle = getIntraPeriodFileHandle(file);
/*      */           }
/*      */           else {
/*  926 */             TickData tickAtPosition = new TickData();
/*  927 */             tickAtPosition.fromBytes(this.version, fileStartTime, instrument.getPipValue(), tickBuff, 0);
/*  928 */             if (tickAtPosition.time != -9223372036854775808L) {
/*  929 */               if (tickAtPosition.time != data[0].time) {
/*  930 */                 LOGGER.warn("Tick at first element of the incoming intraperiod array was not found in already existing intraperiod file");
/*      */               }
/*      */ 
/*  933 */               fileHandle.seek(0L);
/*  934 */               byte[] fileBytes = new byte[(int)startPosition];
/*  935 */               readBytes = 0;
/*  936 */               while (((i = fileHandle.read(fileBytes, readBytes, fileBytes.length - readBytes)) > -1) && (readBytes < fileBytes.length)) {
/*  937 */                 readBytes += i;
/*      */               }
/*  939 */               if (readBytes < fileBytes.length)
/*      */               {
/*  942 */                 fileHandle.close();
/*  943 */                 synchronized (this.fileHandles) {
/*  944 */                   this.fileHandles.remove(file);
/*      */                 }
/*  946 */                 if (!file.delete()) {
/*  947 */                   throw new DataCacheException(new StringBuilder().append("Data cache corrupted, cannot delete file [").append(file.getPath()).append("]").toString());
/*      */                 }
/*      */ 
/*  950 */                 fileHandle = getIntraPeriodFileHandle(file);
/*      */               }
/*      */               else {
/*  953 */                 Data[] newData = new Data[data.length + fileBytes.length / tickDataLength];
/*      */ 
/*  955 */                 for (int j = 0; j < fileBytes.length / tickDataLength; j++) {
/*  956 */                   TickData tickData = new TickData();
/*  957 */                   tickData.fromBytes(this.version, fileStartTime, instrument.getPipValue(), fileBytes, j * tickDataLength);
/*  958 */                   newData[j] = tickData;
/*      */                 }
/*  960 */                 System.arraycopy(data, 0, newData, j, data.length);
/*  961 */                 if (assertionsEnabled()) {
/*  962 */                   long prevTime = newData[0].time;
/*  963 */                   for (Data dataElement : newData) {
/*  964 */                     if (prevTime > dataElement.time) {
/*  965 */                       Object format = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss SSS");
/*  966 */                       ((DateFormat)format).setTimeZone(TimeZone.getTimeZone("GMT"));
/*  967 */                       throw new DataCacheException(new StringBuilder().append("Data consistency check failed for intraperiod file: [").append(file.getName()).append("]. ").append("Time of the previous tick [").append(((DateFormat)format).format(new Date(prevTime))).append("], time of the next tick [").append(((DateFormat)format).format(new Date(dataElement.time))).append("]").toString());
/*      */                     }
/*      */ 
/*  971 */                     prevTime = dataElement.time;
/*      */                   }
/*      */                 }
/*  974 */                 data = newData;
/*      */               }
/*      */             }
/*      */           }
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/*  982 */       if (assertionsEnabled()) {
/*  983 */         long prevTime = data[0].time;
/*  984 */         for (Data dataElement : data) {
/*  985 */           if (prevTime > dataElement.time) {
/*  986 */             DateFormat format = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss SSS");
/*  987 */             format.setTimeZone(TimeZone.getTimeZone("GMT"));
/*  988 */             throw new DataCacheException(new StringBuilder().append("Data consistency check failed for intraperiod file: [").append(file.getName()).append("]. ").append("Time of the previous tick [").append(format.format(new Date(prevTime))).append("], time of the next tick [").append(format.format(new Date(dataElement.time))).append("]").toString());
/*      */           }
/*      */ 
/*  992 */           prevTime = dataElement.time;
/*      */         }
/*      */       }
/*  995 */       RandomAccessFile fileHandle = getIntraPeriodFileHandle(file);
/*  996 */       fileHandle.setLength(0L);
/*  997 */       fileHandle.seek(0L);
/*  998 */       byte[] buffer = new byte[tickDataLength * 10];
/*  999 */       int count = 0;
/* 1000 */       for (Data dataBlock : data) {
/* 1001 */         dataBlock.toBytes(this.version, fileStartTime, instrument.getPipValue(), buffer, count * tickDataLength);
/* 1002 */         if (count == 9)
/*      */         {
/* 1004 */           fileHandle.write(buffer);
/* 1005 */           count = 0;
/*      */         } else {
/* 1007 */           count++;
/*      */         }
/*      */       }
/* 1010 */       if (count > 0)
/* 1011 */         fileHandle.write(buffer, 0, count * tickDataLength);
/*      */     }
/*      */     catch (IOException e) {
/* 1014 */       throw new DataCacheException(e);
/*      */     } finally {
/* 1016 */       wLock.unlock();
/*      */     }
/*      */   }
/*      */ 
/*      */   public void clearTicksIntraPeriod(Instrument instrument) {
/* 1021 */     File[] files = DataCacheUtils.getIntraPeriodTickFiles(this.intraperiodNum, instrument);
/* 1022 */     for (File file : files) {
/* 1023 */       ReentrantReadWriteLock rwLock = getLock(file);
/* 1024 */       ReentrantReadWriteLock.WriteLock wLock = rwLock.writeLock();
/* 1025 */       wLock.lock();
/*      */       try {
/* 1027 */         if (file.exists()) {
/* 1028 */           RandomAccessFile fileHandle = getIntraPeriodFileHandle(file);
/* 1029 */           fileHandle.close();
/* 1030 */           synchronized (this.fileHandles) {
/* 1031 */             this.fileHandles.remove(file);
/*      */           }
/* 1033 */           if (!file.delete()) {
/* 1034 */             LOGGER.warn(new StringBuilder().append("Cannot delete intraperiod file for ticks [").append(file.getPath()).append("]").toString());
/* 1035 */             fileHandle = getIntraPeriodFileHandle(file);
/* 1036 */             fileHandle.setLength(0L);
/*      */           }
/*      */         }
/* 1039 */         clearDelayedWriteTasks(file);
/*      */       } catch (IOException e) {
/* 1041 */         LOGGER.warn(e.getMessage(), e);
/*      */       } finally {
/* 1043 */         wLock.unlock();
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   public void addIntraPeriodCandle(Instrument instrument, Period period, OfferSide side, IntraPeriodCandleData candleData) {
/*      */     try {
/* 1050 */       if ((candleData.open == 0.0D) || (candleData.close == 0.0D) || (candleData.low == 0.0D) || (candleData.high == 0.0D)) {
/* 1051 */         LOGGER.error("Trying to write candle with one of the prices set to zero... ignoring this call");
/* 1052 */         return;
/*      */       }
/* 1054 */       long fileStartTime = DataCacheUtils.getChunkStart(period, candleData.time);
/* 1055 */       File file = getIntraPeriodFile(instrument, period, side, fileStartTime);
/* 1056 */       synchronized (this.delayedWriteTasks) {
/* 1057 */         CandleWriteTask candleWriteTask = (CandleWriteTask)this.delayedWriteTasks.get(file);
/* 1058 */         if (candleWriteTask == null) {
/* 1059 */           candleWriteTask = new CandleWriteTask(null);
/* 1060 */           candleWriteTask.instrument = instrument;
/* 1061 */           candleWriteTask.firstChunkCandle = DataCacheUtils.getFirstCandleInChunk(period, fileStartTime);
/* 1062 */           candleWriteTask.intraperiodFile = file;
/* 1063 */           candleWriteTask.period = period;
/* 1064 */           this.delayedWriteTasks.put(file, candleWriteTask);
/*      */         }
/* 1066 */         candleWriteTask.candleData.add(candleData);
/*      */       }
/*      */     } catch (DataCacheException e) {
/* 1069 */       LOGGER.error(e.getMessage(), e);
/*      */     }
/*      */   }
/*      */ 
/*      */   public boolean isDataCached(Instrument instrument, Period period, OfferSide side, long from, long to) throws DataCacheException {
/* 1074 */     if (LOGGER.isTraceEnabled()) {
/* 1075 */       DateFormat format = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss SSS");
/* 1076 */       format.setTimeZone(TimeZone.getTimeZone("GMT"));
/* 1077 */       LOGGER.trace(new StringBuilder().append("Checking if data is in cache, instrument [").append(instrument).append("], period [").append(period).append("], side [").append(side).append("], from [").append(format.format(new Date(from))).append("], to [").append(format.format(new Date(to))).append("]").toString());
/*      */     }
/*      */ 
/* 1080 */     assert ((period == Period.TICK) || (from == DataCacheUtils.getCandleStart(period, from)));
/* 1081 */     assert ((period == Period.TICK) || (to == DataCacheUtils.getCandleStart(period, to)));
/* 1082 */     long[][] chunks = DataCacheUtils.separateChunksForCache(period, from, to);
/* 1083 */     for (int i = 0; i < chunks.length; i++)
/*      */     {
/* 1085 */       File file = getChunkFile(instrument, period, side, chunks[i][0], this.version);
/* 1086 */       if (file.exists())
/*      */         continue;
/* 1088 */       file = getIntraPeriodFile(instrument, period, side, chunks[i][0]);
/* 1089 */       if ((file.exists()) || (delayedTasksExists(file))) {
/* 1090 */         if (period != Period.TICK) {
/* 1091 */           long chunkStart = chunks[i][0];
/* 1092 */           long fromFixed = DataCacheUtils.getCandleStart(period, chunkStart);
/* 1093 */           if (fromFixed < chunkStart) {
/* 1094 */             chunkStart = DataCacheUtils.getNextCandleStart(period, chunkStart);
/*      */           }
/* 1096 */           long[][] intervals = getIntraperiodIntervalsToLoad(instrument, period, side, from > chunkStart ? from : DataCacheUtils.getFirstCandleInChunkFast(period, chunkStart), chunks[i][1]);
/* 1097 */           if (intervals.length > 0)
/* 1098 */             return false;
/*      */         }
/*      */       }
/*      */       else {
/* 1102 */         return false;
/*      */       }
/*      */     }
/*      */ 
/* 1106 */     return true;
/*      */   }
/*      */ 
/*      */   public CandleData readData(Instrument instrument, Period period, OfferSide side, long from, long to, LiveFeedListener listener, boolean blocking, CurvesDataLoader.IntraperiodExistsPolicy intraperiodExistsPolicy, long[] chunk, CandleData lastNonEmptyElement, IFeedCommissionManager feedCommissionManager)
/*      */     throws DataCacheException
/*      */   {
/* 1122 */     if (intraperiodExistsPolicy == CurvesDataLoader.IntraperiodExistsPolicy.USE_INTRAPERIOD_WHEN_POSSIBLE)
/*      */     {
/* 1124 */       File file = getIntraPeriodFile(instrument, period, side, chunk[0]);
/* 1125 */       if ((file.exists()) || (delayedTasksExists(file))) {
/* 1126 */         if (period == Period.TICK)
/* 1127 */           readTicksFromIntraPeriodFile(instrument, file, chunk[0], chunk[1], from, to, listener, blocking);
/*      */         else
/* 1129 */           lastNonEmptyElement = readCandlesFromIntraPeriodFile(file, instrument, period, side, chunk[0], from, to, listener, lastNonEmptyElement, blocking, feedCommissionManager);
/*      */       }
/*      */       else
/*      */       {
/* 1133 */         file = getChunkFile(instrument, period, side, chunk[0], this.version);
/* 1134 */         if (chunkFileExists(file)) {
/* 1135 */           if (period == Period.TICK)
/* 1136 */             readTicksFromChunkFile(instrument, file, chunk[0], from, to, listener, blocking, feedCommissionManager);
/*      */           else
/* 1138 */             lastNonEmptyElement = readCandlesFromChunkFile(file, instrument, period, side, chunk[0], from, to, listener, lastNonEmptyElement, blocking, feedCommissionManager);
/*      */         }
/* 1140 */         else if (lastNonEmptyElement != null)
/*      */         {
/* 1143 */           lastNonEmptyElement = fillWithFlats(instrument, period, side, chunk[0], chunk[1], from, to, listener, lastNonEmptyElement);
/*      */         }
/*      */         else
/*      */         {
/* 1147 */           DateFormat format = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss SSS");
/* 1148 */           format.setTimeZone(TimeZone.getTimeZone("GMT"));
/* 1149 */           LOGGER.warn(new StringBuilder().append("Some or all of the requested data was not found, instrument [").append(instrument).append("], period [").append(period).append("], side [").append(side).append("], chunk from [").append(format.format(new Date(chunk[0]))).append("], chunk to [").append(format.format(new Date(chunk[1]))).append("]").toString());
/*      */         }
/*      */       }
/*      */ 
/*      */     }
/*      */     else
/*      */     {
/* 1156 */       File file = getChunkFile(instrument, period, side, chunk[0], this.version);
/* 1157 */       if (chunkFileExists(file)) {
/* 1158 */         if (period == Period.TICK)
/* 1159 */           readTicksFromChunkFile(instrument, file, chunk[0], from, to, listener, blocking, feedCommissionManager);
/*      */         else {
/* 1161 */           lastNonEmptyElement = readCandlesFromChunkFile(file, instrument, period, side, chunk[0], from, to, listener, lastNonEmptyElement, blocking, feedCommissionManager);
/*      */         }
/*      */       }
/*      */       else
/*      */       {
/* 1166 */         file = getIntraPeriodFile(instrument, period, side, chunk[0]);
/* 1167 */         if ((file.exists()) || (delayedTasksExists(file))) {
/* 1168 */           if (period == Period.TICK)
/* 1169 */             readTicksFromIntraPeriodFile(instrument, file, chunk[0], chunk[1], from, to, listener, blocking);
/*      */           else {
/* 1171 */             lastNonEmptyElement = readCandlesFromIntraPeriodFile(file, instrument, period, side, chunk[0], from, to, listener, lastNonEmptyElement, blocking, feedCommissionManager);
/*      */           }
/*      */         }
/* 1174 */         else if (lastNonEmptyElement != null)
/*      */         {
/* 1177 */           lastNonEmptyElement = fillWithFlats(instrument, period, side, chunk[0], chunk[1], from, to, listener, lastNonEmptyElement);
/*      */         }
/*      */         else
/*      */         {
/* 1182 */           DateFormat format = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss SSS");
/* 1183 */           format.setTimeZone(TimeZone.getTimeZone("GMT"));
/* 1184 */           LOGGER.warn(new StringBuilder().append("Some or all of the requested data was not found, instrument [").append(instrument).append("], period [").append(period).append("], side [").append(side).append("], chunk from [").append(format.format(new Date(chunk[0]))).append("], chunk to [").append(format.format(new Date(chunk[1]))).append("]").toString());
/*      */         }
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 1190 */     return lastNonEmptyElement;
/*      */   }
/*      */ 
/*      */   public void readLastAvailableData(Instrument instrument, Period period, OfferSide side, long from, long to, CurvesDataLoader.IntraperiodExistsPolicy intraperiodExistsPolicy, LiveFeedListener listener, LoadingProgressListener loadingProgress, IFeedCommissionManager feedCommissionManager)
/*      */     throws DataCacheException
/*      */   {
/* 1204 */     if (LOGGER.isTraceEnabled()) {
/* 1205 */       DateFormat format = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss SSS");
/* 1206 */       format.setTimeZone(TimeZone.getTimeZone("GMT"));
/* 1207 */       LOGGER.trace(new StringBuilder().append("Reading last available data from cache, instrument [").append(instrument).append("], period [").append(period).append("], side [").append(side).append("], from [").append(format.format(new Date(from))).append("], to [").append(format.format(new Date(to))).append("]").toString());
/*      */     }
/*      */ 
/* 1210 */     assert ((period == Period.TICK) || (from == -9223372036854775808L) || (from == DataCacheUtils.getCandleStart(period, from)));
/* 1211 */     assert ((period == Period.TICK) || (to == DataCacheUtils.getCandleStart(period, to)));
/* 1212 */     long fromChunkStart = DataCacheUtils.getChunkStart(period, from);
/* 1213 */     long chunkStart = DataCacheUtils.getChunkStart(period, to);
/* 1214 */     long chunkEnd = DataCacheUtils.getChunkEnd(period, to);
/* 1215 */     CandleData lastNonEmptyElement = null;
/*      */     do {
/* 1217 */       if (intraperiodExistsPolicy == CurvesDataLoader.IntraperiodExistsPolicy.USE_INTRAPERIOD_WHEN_POSSIBLE)
/*      */       {
/* 1219 */         File file = getIntraPeriodFile(instrument, period, side, chunkStart);
/* 1220 */         if ((file.exists()) || (delayedTasksExists(file))) {
/* 1221 */           if (period == Period.TICK) {
/* 1222 */             if (!readLastAvailableTicksFromIntraPeriodFile(instrument, file, chunkStart, chunkEnd, from, to, listener, loadingProgress))
/*      */             {
/* 1232 */               break;
/*      */             }
/*      */           }
/* 1235 */           else if (!readLastAvailableCandlesFromIntraPeriodFile(file, instrument, period, side, chunkStart, from, to, listener, loadingProgress, lastNonEmptyElement))
/*      */           {
/* 1247 */             break;
/*      */           }
/*      */         }
/*      */         else {
/* 1251 */           if ((!chunkFileExists(file)) || 
/* 1252 */             (period == Period.TICK ? 
/* 1253 */             !readLastAvailableTicksFromChunkFile(instrument, file, chunkStart, chunkEnd, from, to, listener, loadingProgress, feedCommissionManager) : 
/* 1267 */             !readLastAvailableCandlesFromChunkFile(file, instrument, period, side, chunkStart, from, to, listener, loadingProgress, feedCommissionManager)))
/*      */           {
/*      */             break;
/*      */           }
/*      */ 
/*      */         }
/*      */ 
/*      */       }
/*      */       else
/*      */       {
/* 1289 */         File file = getChunkFile(instrument, period, side, chunkStart, this.version);
/* 1290 */         if (chunkFileExists(file)) {
/* 1291 */           if (period == Period.TICK) {
/* 1292 */             if (!readLastAvailableTicksFromChunkFile(instrument, file, chunkStart, chunkEnd, from, to, listener, loadingProgress, feedCommissionManager))
/*      */             {
/* 1303 */               break;
/*      */             }
/*      */           }
/* 1306 */           else if (!readLastAvailableCandlesFromChunkFile(file, instrument, period, side, chunkStart, from, to, listener, loadingProgress, feedCommissionManager))
/*      */           {
/* 1318 */             break;
/*      */           }
/*      */         }
/*      */         else
/*      */         {
/* 1323 */           file = getIntraPeriodFile(instrument, period, side, chunkStart);
/* 1324 */           if (((!file.exists()) && (!delayedTasksExists(file))) || 
/* 1325 */             (period == Period.TICK ? 
/* 1326 */             !readLastAvailableTicksFromIntraPeriodFile(instrument, file, chunkStart, chunkEnd, from, to, listener, loadingProgress) : 
/* 1331 */             !readLastAvailableCandlesFromIntraPeriodFile(file, instrument, period, side, chunkStart, from, to, listener, loadingProgress, lastNonEmptyElement)))
/*      */           {
/*      */             break;
/*      */           }
/*      */ 
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/* 1342 */       if (loadingProgress.stopJob()) {
/*      */         break;
/*      */       }
/* 1345 */       chunkStart = DataCacheUtils.getPreviousChunkStart(period, chunkStart);
/* 1346 */     }while (chunkStart >= fromChunkStart);
/*      */   }
/*      */ 
/*      */   protected boolean readLastAvailableTicksFromChunkFile(Instrument instrument, File file, long fileStartTime, long fileEndTime, long from, long to, LiveFeedListener listener, LoadingProgressListener loadingProgress, IFeedCommissionManager feedCommissionManager)
/*      */     throws DataCacheException
/*      */   {
/* 1360 */     if (LOGGER.isTraceEnabled()) {
/* 1361 */       LOGGER.trace(new StringBuilder().append("Reading last available ticks from file [").append(file.getPath()).append("]").toString());
/*      */     }
/* 1363 */     ReentrantReadWriteLock rwLock = getLock(file);
/* 1364 */     ReentrantReadWriteLock.ReadLock rLock = rwLock.readLock();
/* 1365 */     rLock.lock();
/*      */     try
/*      */     {
/* 1368 */       if ((!file.getParentFile().mkdirs()) && (!file.getParentFile().exists())) {
/* 1369 */         LOGGER.error("Cannot create cache directory");
/*      */       }
/* 1371 */       RandomAccessFile fileHandle = getChunkFileHandle(file, true);
/*      */       try {
/* 1373 */         boolean bool = readTicksFromEndFromFile(instrument, file, fileStartTime, from, to, listener, loadingProgress, fileHandle, feedCommissionManager);
/*      */ 
/* 1419 */         returnChunkFileHandle(file, fileHandle);
/*      */ 
/* 1425 */         rLock.unlock(); return bool;
/*      */       }
/*      */       catch (DataCacheException e)
/*      */       {
/* 1386 */         rLock.unlock();
/*      */ 
/* 1388 */         ReentrantReadWriteLock.WriteLock wLock = rwLock.writeLock();
/* 1389 */         wLock.lock();
/*      */         try {
/* 1391 */           synchronized (this.fileHandlesCache) {
/* 1392 */             CacheManager.FileCacheItem cacheItem = (CacheManager.FileCacheItem)this.fileHandlesCache.remove(file);
/* 1393 */             if (cacheItem != null) {
/* 1394 */               for (RandomAccessFile cachedFileHandle : cacheItem.fileHandles) {
/* 1395 */                 if (cachedFileHandle == null) continue;
/*      */                 try {
/* 1397 */                   cachedFileHandle.close();
/*      */                 } catch (IOException ex) {
/* 1399 */                   LOGGER.error(ex.getMessage(), ex);
/*      */                 }
/*      */               }
/*      */             }
/*      */           }
/*      */ 
/* 1405 */           fileHandle.close();
/* 1406 */           if (!file.delete())
/* 1407 */             LOGGER.error(new StringBuilder().append("Data cache corrupted, cannot delete file [").append(file.getPath()).append("]").toString());
/*      */           else {
/* 1409 */             synchronized (this.fileExistanceCache) {
/* 1410 */               this.fileExistanceCache.remove(file);
/*      */             }
/*      */           }
/* 1413 */           throw e;
/*      */         } finally {
/* 1415 */           wLock.unlock();
/* 1416 */           rLock.lock();
/*      */         }
/*      */       } finally {
/* 1419 */         returnChunkFileHandle(file, fileHandle);
/*      */       }
/*      */     } catch (IOException e) {
/* 1422 */       throw new DataCacheException(e);
/*      */     }
/*      */     finally {
/* 1425 */       rLock.unlock(); } throw localObject5;
/*      */   }
/*      */ 
/*      */   private boolean readLastAvailableCandlesFromChunkFile(File file, Instrument instrument, Period period, OfferSide side, long fileStartTime, long from, long to, LiveFeedListener listener, LoadingProgressListener loadingProgress, IFeedCommissionManager feedCommissionManager)
/*      */     throws DataCacheException
/*      */   {
/* 1441 */     if (LOGGER.isTraceEnabled()) {
/* 1442 */       LOGGER.trace(new StringBuilder().append("Reading last available candles from intraperiod file [").append(file.getPath()).append("]").toString());
/*      */     }
/* 1444 */     assert ((from == -9223372036854775808L) || (from == DataCacheUtils.getCandleStart(period, from)));
/* 1445 */     assert (to == DataCacheUtils.getCandleStart(period, to));
/* 1446 */     ReentrantReadWriteLock rwLock = getLock(file);
/* 1447 */     ReentrantReadWriteLock.ReadLock rLock = rwLock.readLock();
/* 1448 */     rLock.lock();
/*      */     try
/*      */     {
/* 1451 */       if ((!file.getParentFile().mkdirs()) && (!file.getParentFile().exists())) {
/* 1452 */         LOGGER.error("Cannot create cache directory");
/*      */       }
/* 1454 */       RandomAccessFile fileHandle = getChunkFileHandle(file, true);
/*      */       try {
/* 1456 */         boolean bool = readCandlesFromEndFromFile(file, fileHandle, instrument, period, side, fileStartTime, from, to, listener, loadingProgress, feedCommissionManager);
/*      */ 
/* 1504 */         returnChunkFileHandle(file, fileHandle);
/*      */ 
/* 1510 */         rLock.unlock(); return bool;
/*      */       }
/*      */       catch (DataCacheException e)
/*      */       {
/* 1471 */         rLock.unlock();
/*      */ 
/* 1473 */         ReentrantReadWriteLock.WriteLock wLock = rwLock.writeLock();
/* 1474 */         wLock.lock();
/*      */         try {
/* 1476 */           synchronized (this.fileHandlesCache) {
/* 1477 */             CacheManager.FileCacheItem cacheItem = (CacheManager.FileCacheItem)this.fileHandlesCache.remove(file);
/* 1478 */             if (cacheItem != null) {
/* 1479 */               for (RandomAccessFile cachedFileHandle : cacheItem.fileHandles) {
/* 1480 */                 if (cachedFileHandle == null) continue;
/*      */                 try {
/* 1482 */                   cachedFileHandle.close();
/*      */                 } catch (IOException ex) {
/* 1484 */                   LOGGER.error(ex.getMessage(), ex);
/*      */                 }
/*      */               }
/*      */             }
/*      */           }
/*      */ 
/* 1490 */           fileHandle.close();
/* 1491 */           if (!file.delete())
/* 1492 */             LOGGER.error(new StringBuilder().append("Data cache corrupted, cannot delete file [").append(file.getPath()).append("]").toString());
/*      */           else {
/* 1494 */             synchronized (this.fileExistanceCache) {
/* 1495 */               this.fileExistanceCache.remove(file);
/*      */             }
/*      */           }
/* 1498 */           throw e;
/*      */         } finally {
/* 1500 */           wLock.unlock();
/* 1501 */           rLock.lock();
/*      */         }
/*      */       } finally {
/* 1504 */         returnChunkFileHandle(file, fileHandle);
/*      */       }
/*      */     } catch (IOException e) {
/* 1507 */       throw new DataCacheException(e);
/*      */     }
/*      */     finally {
/* 1510 */       rLock.unlock(); } throw localObject5;
/*      */   }
/*      */ 
/*      */   private boolean readLastAvailableTicksFromIntraPeriodFile(Instrument instrument, File file, long fileStartTime, long fileEndTime, long from, long to, LiveFeedListener listener, LoadingProgressListener loadingProgress)
/*      */     throws DataCacheException
/*      */   {
/* 1524 */     if (LOGGER.isTraceEnabled()) {
/* 1525 */       LOGGER.trace(new StringBuilder().append("Reading last available ticks from intraperiod file [").append(file.getPath()).append("]").toString());
/*      */     }
/* 1527 */     ReentrantReadWriteLock rwLock = getLock(file);
/* 1528 */     ReentrantReadWriteLock.WriteLock wLock = rwLock.writeLock();
/* 1529 */     wLock.lock();
/*      */     try
/*      */     {
/* 1532 */       if ((!file.getParentFile().mkdirs()) && (!file.getParentFile().exists())) {
/* 1533 */         LOGGER.error("Cannot create cache directory");
/*      */       }
/* 1535 */       RandomAccessFile fileHandle = getIntraPeriodFileHandle(file);
/* 1536 */       flushDelayedWriteTasks(file, fileHandle);
/*      */       try {
/* 1538 */         boolean bool = readTicksFromEndFromFile(instrument, file, fileStartTime, from, to, listener, loadingProgress, fileHandle, ZERO_FEED_COMMISSION_MANAGER);
/*      */ 
/* 1563 */         wLock.unlock(); return bool;
/*      */       }
/*      */       catch (DataCacheException e)
/*      */       {
/* 1550 */         fileHandle.close();
/* 1551 */         synchronized (this.fileHandles) {
/* 1552 */           this.fileHandles.remove(file);
/*      */         }
/* 1554 */         if (!file.delete()) {
/* 1555 */           LOGGER.error(new StringBuilder().append("Data cache corrupted, cannot delete file [").append(file.getPath()).append("]").toString());
/*      */         }
/* 1557 */         throw e;
/*      */       }
/*      */     } catch (IOException e) {
/* 1560 */       throw new DataCacheException(e);
/*      */     }
/*      */     finally {
/* 1563 */       wLock.unlock(); } throw localObject2;
/*      */   }
/*      */ 
/*      */   private boolean readLastAvailableCandlesFromIntraPeriodFile(File file, Instrument instrument, Period period, OfferSide side, long fileStartTime, long from, long to, LiveFeedListener listener, LoadingProgressListener loadingProgress, CandleData lastNonEmptyElement)
/*      */     throws DataCacheException
/*      */   {
/* 1569 */     if (LOGGER.isTraceEnabled()) {
/* 1570 */       LOGGER.trace(new StringBuilder().append("Reading last available candles from intraperiod file [").append(file.getPath()).append("]").toString());
/*      */     }
/* 1572 */     assert ((from == -9223372036854775808L) || (from == DataCacheUtils.getCandleStart(period, from)));
/* 1573 */     assert (to == DataCacheUtils.getCandleStart(period, to));
/* 1574 */     ReentrantReadWriteLock rwLock = getLock(file);
/* 1575 */     ReentrantReadWriteLock.WriteLock wLock = rwLock.writeLock();
/* 1576 */     wLock.lock();
/*      */     try
/*      */     {
/* 1579 */       if ((!file.getParentFile().mkdirs()) && (!file.getParentFile().exists())) {
/* 1580 */         LOGGER.error("Cannot create cache directory");
/*      */       }
/* 1582 */       RandomAccessFile fileHandle = getIntraPeriodFileHandle(file);
/* 1583 */       flushDelayedWriteTasks(file, fileHandle);
/*      */       try {
/* 1585 */         boolean bool = readIntraperiodCandlesFromEndFromFile(file, fileHandle, instrument, period, side, fileStartTime, from, to, listener, loadingProgress);
/*      */ 
/* 1600 */         wLock.unlock(); return bool;
/*      */       }
/*      */       catch (DataCacheException e)
/*      */       {
/* 1587 */         fileHandle.close();
/* 1588 */         synchronized (this.fileHandles) {
/* 1589 */           this.fileHandles.remove(file);
/*      */         }
/* 1591 */         if (!file.delete()) {
/* 1592 */           LOGGER.error(new StringBuilder().append("Data cache corrupted, cannot delete file [").append(file.getPath()).append("]").toString());
/*      */         }
/* 1594 */         throw e;
/*      */       }
/*      */     } catch (IOException e) {
/* 1597 */       throw new DataCacheException(e);
/*      */     }
/*      */     finally {
/* 1600 */       wLock.unlock(); } throw localObject2;
/*      */   }
/*      */ 
/*      */   private boolean readIntraperiodCandlesFromEndFromFile(File file, RandomAccessFile fileHandle, Instrument instrument, Period period, OfferSide side, long fileStartTime, long from, long to, LiveFeedListener listener, LoadingProgressListener loadingProgress)
/*      */     throws IOException, DataCacheException
/*      */   {
/* 1608 */     if ((!file.getParentFile().mkdirs()) && (!file.getParentFile().exists())) {
/* 1609 */       LOGGER.error("Cannot create cache directory");
/*      */     }
/* 1611 */     int intraPeriodCandleDataLength = IntraPeriodCandleData.getLength(this.version);
/* 1612 */     int startPosition = 0;
/* 1613 */     long candleTime = DataCacheUtils.getFirstCandleInChunkFast(period, fileStartTime);
/* 1614 */     long firstChunkCandle = candleTime;
/* 1615 */     if (to > candleTime)
/*      */     {
/* 1617 */       startPosition = DataCacheUtils.getCandlesCountBetween(period, candleTime, DataCacheUtils.getPreviousCandleStart(period, to)) * intraPeriodCandleDataLength;
/*      */ 
/* 1619 */       candleTime = to;
/*      */     }
/* 1621 */     if (startPosition + intraPeriodCandleDataLength > fileHandle.length()) {
/* 1622 */       return false;
/*      */     }
/*      */ 
/* 1625 */     byte[] buff = new byte[intraPeriodCandleDataLength];
/*      */ 
/* 1627 */     while (startPosition >= 0) {
/* 1628 */       fileHandle.seek(startPosition);
/* 1629 */       int readBytes = 0;
/*      */       int i;
/* 1630 */       while (((i = fileHandle.read(buff, readBytes, buff.length - readBytes)) > -1) && (readBytes < buff.length)) {
/* 1631 */         readBytes += i;
/*      */       }
/* 1633 */       if (readBytes < intraPeriodCandleDataLength) {
/* 1634 */         return false;
/*      */       }
/* 1636 */       IntraPeriodCandleData candleData = new IntraPeriodCandleData();
/* 1637 */       candleData.fromBytes(this.version, firstChunkCandle, instrument.getPipValue(), buff, 0);
/* 1638 */       if (candleData.empty)
/*      */       {
/* 1640 */         return false;
/*      */       }
/* 1642 */       if (candleData.time != candleTime)
/*      */       {
/* 1644 */         DateFormat format = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss SSS");
/* 1645 */         format.setTimeZone(TimeZone.getTimeZone("GMT"));
/* 1646 */         String errorMessage = new StringBuilder().append("Data cache file corrupted, candle time [").append(format.format(new Date(candleData.time))).append("],").append("expected candle time [").append(format.format(new Date(candleTime))).append("], file [").append(file.getPath()).append("], position in file [").append(startPosition).append("]").toString();
/*      */ 
/* 1649 */         throw new DataCacheException(errorMessage);
/*      */       }
/* 1651 */       if ((candleData.time >= from) && ((loadingProgress == null) || (!loadingProgress.stopJob()))) {
/* 1652 */         listener.newCandle(instrument, period, side, candleTime, candleData.open, candleData.close, candleData.low, candleData.high, candleData.vol);
/*      */       }
/*      */       else
/*      */       {
/* 1656 */         return false;
/*      */       }
/*      */ 
/* 1659 */       candleTime = DataCacheUtils.getPreviousCandleStart(period, candleTime);
/* 1660 */       startPosition -= intraPeriodCandleDataLength;
/*      */     }
/* 1662 */     return true;
/*      */   }
/*      */ 
/*      */   private void readTicksFromIntraPeriodFile(Instrument instrument, File file, long fileStartTime, long fileEndTime, long from, long to, LiveFeedListener listener, boolean blocking)
/*      */     throws DataCacheException
/*      */   {
/* 1675 */     if (LOGGER.isTraceEnabled()) {
/* 1676 */       LOGGER.trace(new StringBuilder().append("Reading ticks from intraperiod file [").append(file.getPath()).append("]").toString());
/*      */     }
/* 1678 */     ReentrantReadWriteLock rwLock = getLock(file);
/* 1679 */     ReentrantReadWriteLock.WriteLock wLock = rwLock.writeLock();
/* 1680 */     wLock.lock();
/*      */     try {
/*      */       try {
/* 1683 */         if ((!file.getParentFile().mkdirs()) && (!file.getParentFile().exists())) {
/* 1684 */           LOGGER.error("Cannot create cache directory");
/*      */         }
/*      */ 
/* 1687 */         RandomAccessFile fileHandle = getIntraPeriodFileHandle(file);
/* 1688 */         flushDelayedWriteTasks(file, fileHandle);
/* 1689 */         if (blocking) {
/* 1690 */           File tempFile = File.createTempFile("jforex", ".tmp");
/* 1691 */           tempFile.deleteOnExit();
/* 1692 */           InputStream is = new BufferedInputStream(new FileInputStream(file));
/* 1693 */           OutputStream os = new BufferedOutputStream(new FileOutputStream(tempFile));
/*      */           try {
/* 1695 */             byte[] buff = new byte[1024];
/*      */             int i;
/* 1697 */             while ((i = is.read(buff, 0, buff.length)) != -1) {
/* 1698 */               os.write(buff, 0, i);
/*      */             }
/* 1700 */             os.flush();
/*      */           } finally {
/* 1702 */             os.close();
/* 1703 */             is.close();
/*      */           }
/* 1705 */           file = tempFile;
/* 1706 */           wLock.unlock();
/* 1707 */           wLock = null;
/*      */ 
/* 1709 */           fileHandle = new RandomAccessFile(file, "rw");
/*      */         }
/*      */         try {
/* 1712 */           readTicksFromFile(instrument, file, fileStartTime, from, to, listener, fileHandle, ZERO_FEED_COMMISSION_MANAGER);
/*      */         } catch (DataCacheException e) {
/* 1714 */           fileHandle.close();
/* 1715 */           synchronized (this.fileHandles) {
/* 1716 */             this.fileHandles.remove(file);
/*      */           }
/* 1718 */           if (!file.delete()) {
/* 1719 */             LOGGER.error(new StringBuilder().append("Data cache corrupted, cannot delete file [").append(file.getPath()).append("]").toString());
/*      */           }
/* 1721 */           throw e;
/*      */         } finally {
/* 1723 */           if (blocking) {
/* 1724 */             fileHandle.close();
/* 1725 */             file.delete();
/*      */           }
/*      */         }
/*      */       } catch (IOException e) {
/* 1729 */         throw new DataCacheException(e);
/*      */       }
/*      */     } finally {
/* 1732 */       if (wLock != null)
/* 1733 */         wLock.unlock();
/*      */     }
/*      */   }
/*      */ 
/*      */   public CandleData readCandlesFromChunkFile(File file, Instrument instrument, Period period, OfferSide side, long fileStartTime, long from, long to, LiveFeedListener listener, CandleData lastNonEmptyElement, boolean blocking, IFeedCommissionManager feedCommissionManager)
/*      */     throws DataCacheException
/*      */   {
/* 1751 */     if (LOGGER.isTraceEnabled()) {
/* 1752 */       LOGGER.trace(new StringBuilder().append("Reading candles from chunk file [").append(file.getPath()).append("]").toString());
/*      */     }
/* 1754 */     assert (from == DataCacheUtils.getCandleStart(period, from));
/* 1755 */     assert (to == DataCacheUtils.getCandleStart(period, to));
/* 1756 */     ReentrantReadWriteLock rwLock = getLock(file);
/* 1757 */     ReentrantReadWriteLock.ReadLock rLock = rwLock.readLock();
/* 1758 */     rLock.lock();
/*      */     try
/*      */     {
/* 1761 */       RandomAccessFile fileHandle = getChunkFileHandle(file, !blocking);
/* 1762 */       long firstChunkCandle = DataCacheUtils.getFirstCandleInChunk(period, fileStartTime);
/*      */       try {
/* 1764 */         long fileLength = fileHandle.length();
/* 1765 */         if (fileLength == 0L)
/*      */         {
/* 1768 */           if (lastNonEmptyElement != null) {
/* 1769 */             long chunkEnd = DataCacheUtils.getChunkEnd(period, fileStartTime);
/* 1770 */             lastNonEmptyElement = fillWithFlats(instrument, period, side, fileStartTime, chunkEnd, from, to, listener, lastNonEmptyElement);
/*      */           }
/*      */         } else {
/* 1773 */           startPosition = 0;
/* 1774 */           long candleTime = DataCacheUtils.getCandleStart(period, fileStartTime);
/* 1775 */           if (candleTime < fileStartTime) {
/* 1776 */             candleTime = DataCacheUtils.getNextCandleStartFast(period, candleTime);
/*      */           }
/* 1778 */           int candleDataLength = CandleData.getLength(this.version);
/* 1779 */           if (from > candleTime)
/*      */           {
/* 1781 */             startPosition = DataCacheUtils.getCandlesCountBetween(period, candleTime, DataCacheUtils.getPreviousCandleStart(period, from)) * candleDataLength;
/* 1782 */             candleTime = from;
/*      */           }
/*      */ 
/* 1785 */           int bufferSize = 8192 / candleDataLength * candleDataLength;
/* 1786 */           if (period != Period.MONTHLY)
/*      */           {
/* 1788 */             int untilTo = DataCacheUtils.getCandlesCountBetweenFast(period, candleTime, to) * candleDataLength;
/* 1789 */             if (bufferSize > untilTo) {
/* 1790 */               bufferSize = untilTo + candleDataLength;
/*      */             }
/* 1792 */             if (bufferSize > fileLength - startPosition) {
/* 1793 */               bufferSize = (int)fileLength - startPosition; } 
/* 1797 */           }
/*      */ fileHandle.seek(startPosition);
/* 1798 */           byte[] buff = new byte[bufferSize];
/* 1799 */           boolean feedCommissionExists = feedCommissionManager.hasCommission(instrument);
/*      */           int i;
/*      */           do { int readBytes = 0;
/* 1803 */             while (((i = fileHandle.read(buff, readBytes, buff.length - readBytes)) > -1) && (readBytes < buff.length)) {
/* 1804 */               readBytes += i;
/*      */             }
/* 1806 */             CandleData candleData = new CandleData();
/* 1807 */             for (int j = 0; j < readBytes; j += candleDataLength)
/* 1808 */               if (j + candleDataLength <= readBytes) {
/* 1809 */                 candleData.fromBytes(this.version, firstChunkCandle, instrument.getPipValue(), buff, j);
/*      */                 ReentrantReadWriteLock.WriteLock wLock;
/* 1810 */                 if (candleData.time <= to) {
/* 1811 */                   if (candleData.time != candleTime)
/*      */                   {
/* 1814 */                     rLock.unlock();
/*      */ 
/* 1816 */                     wLock = rwLock.writeLock();
/* 1817 */                     wLock.lock();
/*      */                     try {
/* 1819 */                       synchronized (this.fileHandlesCache) {
/* 1820 */                         CacheManager.FileCacheItem cacheItem = (CacheManager.FileCacheItem)this.fileHandlesCache.remove(file);
/* 1821 */                         if (cacheItem != null) {
/* 1822 */                           for (RandomAccessFile cachedFileHandle : cacheItem.fileHandles) {
/* 1823 */                             if (cachedFileHandle == null) continue;
/*      */                             try {
/* 1825 */                               cachedFileHandle.close();
/*      */                             } catch (IOException ex) {
/* 1827 */                               LOGGER.error(ex.getMessage(), ex);
/*      */                             }
/*      */                           }
/*      */                         }
/*      */                       }
/*      */ 
/* 1833 */                       fileHandle.close();
/* 1834 */                       if (file.delete()) {
/* 1835 */                         synchronized (this.fileExistanceCache) {
/* 1836 */                           this.fileExistanceCache.remove(file);
/*      */                         }
/*      */                       }
/* 1839 */                       DateFormat format = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss SSS");
/* 1840 */                       format.setTimeZone(TimeZone.getTimeZone("GMT"));
/* 1841 */                       throw new DataCacheException(new StringBuilder().append("Data cache file [").append(file.getPath()).append("] corrupted, read candle time [").append(format.format(new Date(candleData.time))).append("], expected candle time [").append(format.format(new Date(candleTime))).append("]").toString());
/*      */                     } finally {
/* 1843 */                       wLock.unlock();
/* 1844 */                       rLock.lock();
/*      */                     }
/*      */                   }
/* 1847 */                   if (lastNonEmptyElement == null) {
/* 1848 */                     lastNonEmptyElement = new CandleData();
/*      */                   }
/*      */ 
/* 1851 */                   if (feedCommissionExists) {
/* 1852 */                     candleData = feedCommissionManager.applyFeedCommissionToCandle(instrument, side, candleData);
/*      */                   }
/*      */ 
/* 1855 */                   listener.newCandle(instrument, period, side, candleTime, candleData.open, candleData.close, candleData.low, candleData.high, candleData.vol);
/* 1856 */                   lastNonEmptyElement.time = candleData.time;
/* 1857 */                   lastNonEmptyElement.open = candleData.open;
/* 1858 */                   lastNonEmptyElement.close = candleData.close;
/* 1859 */                   lastNonEmptyElement.high = candleData.high;
/* 1860 */                   lastNonEmptyElement.low = candleData.low;
/* 1861 */                   lastNonEmptyElement.vol = candleData.vol;
/* 1862 */                   candleTime = DataCacheUtils.getNextCandleStartFast(period, candleTime);
/*      */                 }
/*      */                 else {
/* 1865 */                   wLock = lastNonEmptyElement;
/*      */ 
/* 1873 */                   returnChunkFileHandle(file, fileHandle);
/*      */ 
/* 1879 */                   rLock.unlock(); return wLock;
/*      */                 }
/*      */               }
/*      */           }
/* 1869 */           while (i > -1);
/*      */         }
/* 1871 */         int startPosition = lastNonEmptyElement;
/*      */ 
/* 1873 */         returnChunkFileHandle(file, fileHandle);
/*      */ 
/* 1879 */         rLock.unlock(); return startPosition;
/*      */       }
/*      */       finally
/*      */       {
/* 1873 */         returnChunkFileHandle(file, fileHandle);
/*      */       }
/*      */     } catch (IOException e) {
/* 1876 */       throw new DataCacheException(e);
/*      */     }
/*      */     finally {
/* 1879 */       rLock.unlock(); } throw localObject5;
/*      */   }
/*      */ 
/*      */   private CandleData fillWithFlats(Instrument instrument, Period period, OfferSide side, long chunkStart, long chunkEnd, long from, long to, LiveFeedListener listener, CandleData lastNonEmptyElement)
/*      */     throws DataCacheException
/*      */   {
/* 1885 */     long candleTime = DataCacheUtils.getCandleStart(period, chunkStart);
/* 1886 */     if (candleTime < chunkStart) {
/* 1887 */       candleTime = DataCacheUtils.getNextCandleStart(period, candleTime);
/*      */     }
/* 1889 */     if (from > candleTime)
/*      */     {
/* 1891 */       candleTime = from;
/*      */     }
/* 1893 */     while ((candleTime <= to) && (candleTime <= chunkEnd)) {
/* 1894 */       lastNonEmptyElement.time = candleTime;
/* 1895 */       listener.newCandle(instrument, period, side, candleTime, lastNonEmptyElement.close, lastNonEmptyElement.close, lastNonEmptyElement.close, lastNonEmptyElement.close, 0.0D);
/* 1896 */       candleTime = DataCacheUtils.getNextCandleStart(period, candleTime);
/*      */     }
/* 1898 */     return lastNonEmptyElement;
/*      */   }
/*      */ 
/*      */   private CandleData readCandlesFromIntraPeriodFile(File file, Instrument instrument, Period period, OfferSide side, long fileStartTime, long from, long to, LiveFeedListener listener, CandleData lastNonEmptyElement, boolean blocking, IFeedCommissionManager feedCommissionManager)
/*      */     throws DataCacheException
/*      */   {
/* 1915 */     if (LOGGER.isTraceEnabled()) {
/* 1916 */       LOGGER.trace(new StringBuilder().append("Reading candles from intraperiod file [").append(file.getPath()).append("]").toString());
/*      */     }
/* 1918 */     assert (from == DataCacheUtils.getCandleStart(period, from));
/* 1919 */     assert (to == DataCacheUtils.getCandleStart(period, to));
/* 1920 */     ReentrantReadWriteLock rwLock = getLock(file);
/* 1921 */     ReentrantReadWriteLock.WriteLock wLock = rwLock.writeLock();
/* 1922 */     wLock.lock();
/*      */     try {
/* 1924 */       from = DataCacheUtils.getCandleStart(period, from);
/*      */       try {
/* 1926 */         if ((!file.getParentFile().mkdirs()) && (!file.getParentFile().exists())) {
/* 1927 */           LOGGER.error("Cannot create cache directory");
/*      */         }
/*      */ 
/* 1930 */         RandomAccessFile fileHandle = getIntraPeriodFileHandle(file);
/* 1931 */         flushDelayedWriteTasks(file, fileHandle);
/* 1932 */         if (blocking) {
/* 1933 */           File tempFile = File.createTempFile("jforex", ".tmp");
/* 1934 */           tempFile.deleteOnExit();
/* 1935 */           InputStream is = new BufferedInputStream(new FileInputStream(file));
/* 1936 */           OutputStream os = new BufferedOutputStream(new FileOutputStream(tempFile));
/*      */           try {
/* 1938 */             byte[] buff = new byte[1024];
/*      */             int i;
/* 1940 */             while ((i = is.read(buff, 0, buff.length)) != -1) {
/* 1941 */               os.write(buff, 0, i);
/*      */             }
/* 1943 */             os.flush();
/*      */           } finally {
/* 1945 */             os.close();
/* 1946 */             is.close();
/*      */           }
/* 1948 */           file = tempFile;
/* 1949 */           wLock.unlock();
/* 1950 */           wLock = null;
/*      */ 
/* 1952 */           fileHandle = new RandomAccessFile(file, "rw");
/*      */         }
/*      */         try {
/* 1955 */           int startPosition = 0;
/* 1956 */           long firstCandleTime = DataCacheUtils.getFirstCandleInChunkFast(period, fileStartTime);
/* 1957 */           long candleTime = firstCandleTime;
/* 1958 */           int intraPeriodCandleDataLength = IntraPeriodCandleData.getLength(this.version);
/* 1959 */           if (from > candleTime)
/*      */           {
/* 1961 */             startPosition = DataCacheUtils.getCandlesCountBetween(period, candleTime, DataCacheUtils.getPreviousCandleStart(period, from)) * intraPeriodCandleDataLength;
/*      */ 
/* 1964 */             candleTime = from; } 
/*      */ fileHandle.seek(startPosition);
/* 1968 */           byte[] buff = new byte[intraPeriodCandleDataLength * 10];
/*      */           int i;
/*      */           do { readBytes = 0;
/* 1972 */             while (((i = fileHandle.read(buff, readBytes, buff.length - readBytes)) > -1) && (readBytes < buff.length)) {
/* 1973 */               readBytes += i;
/*      */             }
/* 1975 */             IntraPeriodCandleData candleData = new IntraPeriodCandleData();
/* 1976 */             for (int j = 0; j < readBytes; j += intraPeriodCandleDataLength)
/* 1977 */               if (j + intraPeriodCandleDataLength <= readBytes) {
/* 1978 */                 candleData.fromBytes(this.version, firstCandleTime, instrument.getPipValue(), buff, j);
/*      */                 CandleData candleFromChunkFile;
/* 1979 */                 if (candleData.empty)
/*      */                 {
/* 1985 */                   candleFromChunkFile = readCandleFromChunkFile(instrument, period, side, fileStartTime, candleTime, lastNonEmptyElement, blocking, feedCommissionManager);
/* 1986 */                   if (candleFromChunkFile != null) {
/* 1987 */                     candleData.empty = false;
/* 1988 */                     candleData.time = candleFromChunkFile.time;
/* 1989 */                     candleData.open = candleFromChunkFile.open;
/* 1990 */                     candleData.high = candleFromChunkFile.high;
/* 1991 */                     candleData.low = candleFromChunkFile.low;
/* 1992 */                     candleData.close = candleFromChunkFile.close;
/*      */                   }
/*      */                 }
/*      */ 
/* 1996 */                 if (candleData.empty) {
/* 1997 */                   if (candleTime <= to) {
/* 1998 */                     if (lastNonEmptyElement != null) {
/* 1999 */                       lastNonEmptyElement.time = candleTime;
/* 2000 */                       listener.newCandle(instrument, period, side, candleTime, lastNonEmptyElement.close, lastNonEmptyElement.close, lastNonEmptyElement.close, lastNonEmptyElement.close, 0.0D);
/*      */                     }
/*      */ 
/*      */                   }
/*      */                   else
/*      */                   {
/* 2010 */                     candleFromChunkFile = lastNonEmptyElement;
/*      */ 
/* 2060 */                     if (blocking) {
/* 2061 */                       fileHandle.close();
/* 2062 */                       file.delete();
/*      */                     }
/*      */ 
/* 2069 */                     if (wLock != null)
/* 2070 */                       wLock.unlock(); return candleFromChunkFile;
/*      */                   }
/*      */                 }
/*      */                 else
/*      */                 {
/*      */                   DateFormat format;
/* 2013 */                   if (candleData.time != candleTime)
/*      */                   {
/* 2015 */                     format = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss SSS");
/* 2016 */                     format.setTimeZone(TimeZone.getTimeZone("GMT"));
/* 2017 */                     String errorMessage = new StringBuilder().append("Data cache file corrupted, candle time [").append(format.format(new Date(candleData.time))).append("],").append("expected candle time [").append(format.format(new Date(candleTime))).append("], file [").append(file.getPath()).append("], position in file [").append(fileHandle.getFilePointer() - readBytes + j).append("]").toString();
/*      */ 
/* 2021 */                     fileHandle.close();
/* 2022 */                     synchronized (this.fileHandles) {
/* 2023 */                       this.fileHandles.remove(file);
/*      */                     }
/* 2025 */                     file.delete();
/* 2026 */                     throw new DataCacheException(errorMessage);
/*      */                   }
/* 2028 */                   if (candleData.time <= to) {
/* 2029 */                     if (lastNonEmptyElement == null) {
/* 2030 */                       lastNonEmptyElement = new CandleData();
/*      */                     }
/* 2032 */                     lastNonEmptyElement.time = candleData.time;
/* 2033 */                     lastNonEmptyElement.open = candleData.open;
/* 2034 */                     lastNonEmptyElement.close = candleData.close;
/* 2035 */                     lastNonEmptyElement.high = candleData.high;
/* 2036 */                     lastNonEmptyElement.low = candleData.low;
/* 2037 */                     lastNonEmptyElement.vol = candleData.vol;
/* 2038 */                     listener.newCandle(instrument, period, side, candleTime, candleData.open, candleData.close, candleData.low, candleData.high, candleData.vol);
/*      */ 
/* 2040 */                     if ((assertionsEnabled()) && (DataCacheUtils.getTimeForNCandlesForward(period, firstCandleTime, (int)((fileHandle.getFilePointer() - readBytes + j) / intraPeriodCandleDataLength) + 1) != candleData.time))
/*      */                     {
/* 2044 */                       throw new DataCacheException(new StringBuilder().append("Position doesn't belong to the candle, first candle time [").append(firstCandleTime).append("] position [").append(fileHandle.getFilePointer() - readBytes + j).append("] in file [").append(file.getPath()).append("], candle time we wrote there [").append(candleData.time).append("]").toString());
/*      */                     }
/*      */ 
/*      */                   }
/*      */                   else
/*      */                   {
/* 2051 */                     format = lastNonEmptyElement;
/*      */ 
/* 2060 */                     if (blocking) {
/* 2061 */                       fileHandle.close();
/* 2062 */                       file.delete();
/*      */                     }
/*      */ 
/* 2069 */                     if (wLock != null)
/* 2070 */                       wLock.unlock(); return format;
/*      */                   }
/*      */                 }
/* 2054 */                 candleTime = DataCacheUtils.getNextCandleStart(period, candleTime);
/*      */               }
/*      */           }
/* 2057 */           while (i > -1);
/* 2058 */           int readBytes = lastNonEmptyElement;
/*      */ 
/* 2060 */           if (blocking) {
/* 2061 */             fileHandle.close();
/* 2062 */             file.delete();
/*      */           }
/*      */ 
/* 2069 */           if (wLock != null)
/* 2070 */             wLock.unlock(); return readBytes;
/*      */         }
/*      */         finally
/*      */         {
/* 2060 */           if (blocking) {
/* 2061 */             fileHandle.close();
/* 2062 */             file.delete();
/*      */           }
/*      */         }
/*      */       } catch (IOException e) {
/* 2066 */         throw new DataCacheException(e);
/*      */       }
/*      */     } finally {
/* 2069 */       if (wLock != null)
/* 2070 */         wLock.unlock(); 
/* 2070 */     }throw localObject4;
/*      */   }
/*      */ 
/*      */   private CandleData readCandleFromChunkFile(Instrument instrument, Period period, OfferSide side, long fileStartTime, long candleTime, CandleData lastNonEmptyElement, boolean blocking, IFeedCommissionManager feedCommissionManager)
/*      */     throws DataCacheException
/*      */   {
/* 2085 */     File file = getChunkFile(instrument, period, side, fileStartTime, this.version);
/* 2086 */     if (chunkFileExists(file)) {
/* 2087 */       LiveFeedListener liveFeedListener = new LiveFeedListener()
/*      */       {
/*      */         public void newTick(Instrument instrument, long time, double ask, double bid, double askVol, double bidVol)
/*      */         {
/*      */         }
/*      */ 
/*      */         public void newCandle(Instrument instrument, Period period, OfferSide side, long time, double open, double close, double low, double high, double vol)
/*      */         {
/*      */         }
/*      */       };
/* 2095 */       CandleData result = readCandlesFromChunkFile(file, instrument, period, side, fileStartTime, candleTime, candleTime, liveFeedListener, lastNonEmptyElement, blocking, feedCommissionManager);
/* 2096 */       return result;
/*      */     }
/* 2098 */     return null;
/*      */   }
/*      */ 
/*      */   protected File getIntraPeriodFile(Instrument instrument, Period period, OfferSide side, long from) throws DataCacheException {
/* 2102 */     return DataCacheUtils.getIntraPeriodFile(this.intraperiodNum, instrument, period, side, from);
/*      */   }
/*      */ 
/*      */   protected void closeHandles()
/*      */   {
/* 2107 */     this.timer.cancel();
/*      */ 
/* 2110 */     WriteDelayedTasks writeDelayedTasks = new WriteDelayedTasks(null);
/* 2111 */     CloseFileLocks checkLocksTask = new CloseFileLocks(null);
/* 2112 */     CloseFileHandles checkTask = new CloseFileHandles(null);
/* 2113 */     writeDelayedTasks.run();
/* 2114 */     checkLocksTask.run();
/* 2115 */     checkTask.run();
/*      */ 
/* 2117 */     super.closeHandles();
/* 2118 */     synchronized (this.fileHandles) {
/* 2119 */       Map.Entry[] entries = (Map.Entry[])this.fileHandles.entrySet().toArray(new Map.Entry[this.fileHandles.entrySet().size()]);
/* 2120 */       for (Map.Entry fileHandleEntry : entries)
/* 2121 */         if (fileHandleEntry != null) {
/* 2122 */           File file = (File)fileHandleEntry.getKey();
/* 2123 */           ReentrantReadWriteLock rwLock = getLock(file);
/* 2124 */           ReentrantReadWriteLock.WriteLock wLock = rwLock.writeLock();
/* 2125 */           wLock.lock();
/*      */           try {
/*      */             try {
/* 2128 */               RandomAccessFile fileHandle = (RandomAccessFile)fileHandleEntry.getValue();
/* 2129 */               fileHandle.close();
/*      */             } catch (IOException e) {
/* 2131 */               LOGGER.error(e.getMessage(), e);
/*      */             }
/*      */           } finally {
/* 2134 */             wLock.unlock();
/*      */           }
/*      */         }
/*      */     }
/*      */   }
/*      */ 
/*      */   private boolean delayedTasksExists(File file)
/*      */   {
/* 2143 */     synchronized (this.delayedWriteTasks) {
/* 2144 */       return this.delayedWriteTasks.containsKey(file);
/*      */     }
/*      */   }
/*      */ 
/*      */   private void clearDelayedWriteTasks(Instrument instrument)
/*      */   {
/*      */     Iterator iterator;
/* 2150 */     synchronized (this.delayedWriteTasks) {
/* 2151 */       for (iterator = this.delayedWriteTasks.values().iterator(); iterator.hasNext(); ) {
/* 2152 */         DelayedWriteTask task = (DelayedWriteTask)iterator.next();
/* 2153 */         if (task.instrument == instrument)
/* 2154 */           iterator.remove();
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   private void clearDelayedWriteTasks(File file)
/*      */   {
/* 2162 */     synchronized (this.delayedWriteTasks) {
/* 2163 */       this.delayedWriteTasks.remove(file);
/*      */     }
/*      */   }
/*      */ 
/*      */   private void flushDelayedWriteTasks(File file, RandomAccessFile fileHandle)
/*      */     throws DataCacheException
/*      */   {
/*      */     DelayedWriteTask task;
/* 2170 */     synchronized (this.delayedWriteTasks) {
/* 2171 */       task = (DelayedWriteTask)this.delayedWriteTasks.remove(file);
/*      */     }
/*      */ 
/* 2174 */     if (task != null)
/* 2175 */       if ((task instanceof TickWriteTask)) {
/* 2176 */         if (LOGGER.isTraceEnabled()) {
/* 2177 */           LOGGER.trace(new StringBuilder().append("Flushing delayed write tasks for file [").append(file.getPath()).append("] [").append(((TickWriteTask)task).data.length / TickData.getLength(this.version)).append("]").toString());
/*      */         }
/* 2179 */         processTickWriteTasks((TickWriteTask)task, fileHandle);
/*      */       } else {
/* 2181 */         CandleWriteTask candleWriteTask = (CandleWriteTask)task;
/* 2182 */         if (LOGGER.isTraceEnabled()) {
/* 2183 */           LOGGER.trace(new StringBuilder().append("Flushing delayed write tasks for file [").append(file.getPath()).append("] [").append(((CandleWriteTask)task).candleData.size()).append("]").toString());
/*      */         }
/* 2185 */         saveIntraperiodCandleDataToFile(candleWriteTask.instrument, candleWriteTask.firstChunkCandle, file, fileHandle, candleWriteTask.period, (Data[])((CandleWriteTask)task).candleData.toArray(new Data[((CandleWriteTask)task).candleData.size()]));
/*      */       }
/*      */   }
/*      */ 
/*      */   public void flushDelayedWriteTasks()
/*      */   {
/* 2192 */     this.writeDelayedTasks.run();
/*      */   }
/*      */ 
/*      */   public void saveOrdersData(String accountId, Instrument instrument, long chunkStart, OrdersChunkData data) throws DataCacheException
/*      */   {
/* 2197 */     assert ((data.from == chunkStart) && (chunkStart == DataCacheUtils.getOrdersChunkStart(chunkStart)));
/*      */ 
/* 2199 */     File file = DataCacheUtils.getOrdersChunkFile(this.scheme, accountId, instrument, chunkStart, this.version);
/* 2200 */     File lockFile = new File(new StringBuilder().append(file.getPath()).append(".lock").toString());
/* 2201 */     ReentrantReadWriteLock rwLock = getLock(file);
/* 2202 */     ReentrantReadWriteLock.WriteLock wLock = rwLock.writeLock();
/* 2203 */     wLock.lock();
/*      */     try {
/* 2205 */       if ((!file.getParentFile().mkdirs()) && (!file.getParentFile().exists())) {
/* 2206 */         throw new DataCacheException("Cannot create cache directory");
/*      */       }
/*      */ 
/* 2212 */       RandomAccessFile raf = new RandomAccessFile(lockFile, "rw");
/*      */       try {
/* 2214 */         FileChannel channel = raf.getChannel();
/*      */         try {
/* 2216 */           FileLock lock = tryLock(lockFile, channel, 10000L);
/*      */           try {
/* 2218 */             FileOutputStream fos = new FileOutputStream(file);
/*      */             try {
/* 2220 */               BufferedOutputStream bos = new BufferedOutputStream(fos);
/*      */ 
/* 2222 */               byte[] key = Hex.decodeHex(FeedDataProvider.getEncryptionKey().toCharArray());
/* 2223 */               SecretKeySpec skeySpec = new SecretKeySpec(key, "AES");
/*      */ 
/* 2225 */               Cipher cipher = Cipher.getInstance("AES");
/* 2226 */               cipher.init(1, skeySpec);
/* 2227 */               CipherOutputStream cos = new CipherOutputStream(bos, cipher);
/*      */ 
/* 2229 */               data.writeObject(cos);
/*      */ 
/* 2231 */               setLastOrderUpdateTime(instrument, data.to);
/* 2232 */               cos.flush();
/* 2233 */               cos.close();
/* 2234 */               bos.close();
/*      */             } finally {
/* 2236 */               fos.close();
/*      */             }
/*      */           } finally {
/* 2239 */             if (lock.isValid())
/* 2240 */               lock.release();
/*      */           }
/*      */         }
/*      */         finally {
/* 2244 */           channel.close();
/*      */         }
/*      */       } finally {
/* 2247 */         raf.close();
/*      */       }
/* 2249 */       if (assertionsEnabled()) {
/* 2250 */         RandomAccessFile lockRaf = new RandomAccessFile(lockFile, "rw");
/*      */         try {
/* 2252 */           FileChannel channel = lockRaf.getChannel();
/* 2253 */           FileLock lock = tryLock(lockFile, channel, 10000L);
/*      */           try {
/* 2255 */             raf = new RandomAccessFile(file, "rw");
/*      */             try {
/* 2257 */               byte[] buff = new byte[8192];
/* 2258 */               ByteArrayOutputStream baos = new ByteArrayOutputStream();
/*      */               int i;
/* 2260 */               while ((i = raf.read(buff)) != -1) {
/* 2261 */                 baos.write(buff, 0, i);
/*      */               }
/* 2263 */               ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
/*      */ 
/* 2265 */               byte[] key = Hex.decodeHex(FeedDataProvider.getEncryptionKey().toCharArray());
/* 2266 */               SecretKeySpec skeySpec = new SecretKeySpec(key, "AES");
/*      */ 
/* 2268 */               Cipher cipher = Cipher.getInstance("AES");
/* 2269 */               cipher.init(2, skeySpec);
/* 2270 */               CipherInputStream cis = new CipherInputStream(bais, cipher);
/* 2271 */               OrdersChunkData checkData = new OrdersChunkData();
/* 2272 */               checkData.readObject(cis);
/* 2273 */               if (cis.read() != -1) {
/* 2274 */                 throw new IOException("Orders file corrupted, contains more bytes than expected");
/*      */               }
/* 2276 */               cis.close();
/*      */ 
/* 2278 */               if (!data.equals(checkData))
/* 2279 */                 throw new DataCacheException("Saved orders data not equal to restored orders data");
/*      */             }
/*      */             finally {
/* 2282 */               raf.close();
/*      */             }
/*      */           } finally {
/* 2285 */             if (lock.isValid()) {
/* 2286 */               lock.release();
/*      */             }
/* 2288 */             channel.close();
/*      */           }
/*      */         } finally {
/* 2291 */           lockRaf.close();
/*      */         }
/*      */       }
/*      */     } catch (DataCacheException e) {
/* 2295 */       throw e;
/*      */     } catch (RuntimeException e) {
/* 2297 */       throw e;
/*      */     } catch (Exception e) {
/* 2299 */       throw new DataCacheException(e);
/*      */     } finally {
/* 2301 */       if (wLock != null)
/* 2302 */         wLock.unlock();
/*      */     }
/*      */   }
/*      */ 
/*      */   private FileLock tryLock(File file, FileChannel channel, long timeout)
/*      */     throws IOException, DataCacheException, InterruptedException
/*      */   {
/* 2309 */     long time = System.currentTimeMillis();
/* 2310 */     while (time + timeout > System.currentTimeMillis()) {
/* 2311 */       FileLock lock = channel.tryLock();
/* 2312 */       if ((lock != null) && (lock.isValid())) {
/* 2313 */         return lock;
/*      */       }
/* 2315 */       Thread.sleep(20L);
/*      */     }
/*      */ 
/* 2318 */     throw new DataCacheException(new StringBuilder().append("Cannot lock file: ").append(file).toString());
/*      */   }
/*      */ 
/*      */   public boolean isOrderDataCached(String accountId, Instrument instrument, long chunkStart) throws DataCacheException {
/* 2322 */     assert (chunkStart == DataCacheUtils.getOrdersChunkStart(chunkStart));
/* 2323 */     File file = DataCacheUtils.getOrdersChunkFile(this.scheme, accountId, instrument, chunkStart, this.version);
/* 2324 */     return file.exists();
/*      */   }
/*      */ 
/*      */   public boolean isFullOrderChunkExists(String accountId, Instrument instrument, long chunkStart, Set<String> openOrderIds) throws DataCacheException {
/* 2328 */     assert (chunkStart == DataCacheUtils.getOrdersChunkStart(chunkStart));
/* 2329 */     File file = DataCacheUtils.getOrdersChunkFile(this.scheme, accountId, instrument, chunkStart, this.version);
/* 2330 */     File lockFile = new File(new StringBuilder().append(file.getPath()).append(".lock").toString());
/* 2331 */     if (!file.exists()) {
/* 2332 */       return false;
/*      */     }
/*      */ 
/* 2335 */     ReentrantReadWriteLock rwLock = getLock(file);
/* 2336 */     ReentrantReadWriteLock.WriteLock wLock = rwLock.writeLock();
/* 2337 */     wLock.lock();
/*      */     try
/*      */     {
/* 2343 */       RandomAccessFile lockRaf = new RandomAccessFile(lockFile, "rw");
/*      */       try {
/* 2345 */         FileChannel channel = lockRaf.getChannel();
/*      */         try {
/* 2347 */           lock = tryLock(lockFile, channel, 10000L);
/*      */           try {
/* 2349 */             RandomAccessFile raf = new RandomAccessFile(file, "rw");
/*      */             try {
/* 2351 */               byte[] buff = new byte[8192];
/* 2352 */               ByteArrayOutputStream baos = new ByteArrayOutputStream();
/*      */               int i;
/* 2354 */               while ((i = raf.read(buff)) != -1) {
/* 2355 */                 baos.write(buff, 0, i);
/*      */               }
/* 2357 */               ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
/*      */ 
/* 2359 */               byte[] key = Hex.decodeHex(FeedDataProvider.getEncryptionKey().toCharArray());
/* 2360 */               SecretKeySpec skeySpec = new SecretKeySpec(key, "AES");
/*      */ 
/* 2362 */               Cipher cipher = Cipher.getInstance("AES");
/* 2363 */               cipher.init(2, skeySpec);
/* 2364 */               CipherInputStream cis = new CipherInputStream(bais, cipher);
/*      */ 
/* 2366 */               OrdersChunkData chunkData = new OrdersChunkData();
/* 2367 */               chunkData.readObject(cis);
/* 2368 */               if (cis.read() != -1) {
/* 2369 */                 throw new IOException("Orders file corrupted, contains more bytes than expected");
/*      */               }
/* 2371 */               cis.close();
/* 2372 */               if ((chunkData.openGroupsIds == null) || (chunkData.openGroupsIds.isEmpty()) || (!chunkData.full)) {
/* 2373 */                 boolean bool = chunkData.full;
/*      */ 
/* 2384 */                 raf.close();
/*      */ 
/* 2387 */                 if (lock.isValid()) {
/* 2388 */                   lock.release();
/*      */                 }
/*      */ 
/* 2392 */                 channel.close();
/*      */ 
/* 2395 */                 lockRaf.close();
/*      */ 
/* 2425 */                 if (wLock != null);
/*      */                 return bool;
/*      */               }
/* 2375 */               for (String orderGroupId : chunkData.openGroupsIds)
/* 2376 */                 if (!openOrderIds.contains(orderGroupId))
/*      */                 {
/* 2378 */                   int j = 0;
/*      */ 
/* 2384 */                   raf.close();
/*      */ 
/* 2387 */                   if (lock.isValid()) {
/* 2388 */                     lock.release();
/*      */                   }
/*      */ 
/* 2392 */                   channel.close();
/*      */ 
/* 2395 */                   lockRaf.close();
/*      */ 
/* 2425 */                   if (wLock != null);
/*      */                   return j;
/*      */                 }
/* 2381 */               int i = 1;
/*      */ 
/* 2384 */               raf.close();
/*      */ 
/* 2387 */               if (lock.isValid()) {
/* 2388 */                 lock.release();
/*      */               }
/*      */ 
/* 2392 */               channel.close();
/*      */ 
/* 2395 */               lockRaf.close();
/*      */ 
/* 2425 */               if (wLock != null);
/*      */               return i;
/*      */             }
/*      */             finally
/*      */             {
/* 2384 */               raf.close();
/*      */             }
/*      */           } finally {
/* 2387 */             if (lock.isValid())
/* 2388 */               lock.release();
/*      */           }
/*      */         }
/*      */         finally {
/* 2392 */           channel.close();
/*      */         }
/*      */       } finally {
/* 2395 */         lockRaf.close();
/*      */       }
/*      */     }
/*      */     catch (StreamCorruptedException e) {
/* 2399 */       message = e.getMessage();
/* 2400 */       if ((message.contains("stream version [1]")) && (message.contains("class version [2]")))
/* 2401 */         LOGGER.debug(message, e);
/*      */       else {
/* 2403 */         LOGGER.warn(message, e);
/*      */       }
/* 2405 */       file.delete();
/* 2406 */       FileLock lock = 0;
/*      */       return lock;
/*      */     }
/*      */     catch (InvalidClassException e)
/*      */     {
/* 2409 */       file.delete();
/* 2410 */       message = 0;
/*      */       return message;
/*      */     }
/*      */     catch (DataCacheException e)
/*      */     {
/* 2412 */       file.delete();
/* 2413 */       throw e;
/*      */     } catch (RuntimeException e) {
/* 2415 */       file.delete();
/* 2416 */       throw e;
/*      */     } catch (IOException e) {
/* 2418 */       LOGGER.warn(e.getMessage(), e);
/* 2419 */       file.delete();
/* 2420 */       String message = 0;
/*      */       return message;
/*      */     }
/*      */     catch (Exception e)
/*      */     {
/* 2422 */       file.delete();
/* 2423 */       throw new DataCacheException(e);
/*      */     } finally {
/* 2425 */       if (wLock != null)
/* 2426 */         wLock.unlock(); 
/* 2426 */     }throw localObject5;
/*      */   }
/*      */ 
/*      */   public OrdersChunkData readOrdersData(String accountId, Instrument instrument, long chunkStart)
/*      */     throws DataCacheException
/*      */   {
/* 2433 */     assert (chunkStart == DataCacheUtils.getOrdersChunkStart(chunkStart));
/* 2434 */     File file = DataCacheUtils.getOrdersChunkFile(this.scheme, accountId, instrument, chunkStart, this.version);
/* 2435 */     File lockFile = new File(new StringBuilder().append(file.getPath()).append(".lock").toString());
/* 2436 */     if (!file.exists()) {
/* 2437 */       return null;
/*      */     }
/*      */ 
/* 2440 */     ReentrantReadWriteLock rwLock = getLock(file);
/* 2441 */     ReentrantReadWriteLock.WriteLock wLock = rwLock.writeLock();
/* 2442 */     wLock.lock();
/*      */     try
/*      */     {
/* 2448 */       RandomAccessFile lockRaf = new RandomAccessFile(lockFile, "rw");
/*      */       try {
/* 2450 */         channel = lockRaf.getChannel();
/*      */         try {
/* 2452 */           FileLock lock = tryLock(lockFile, channel, 10000L);
/*      */           try {
/* 2454 */             RandomAccessFile raf = new RandomAccessFile(file, "rw");
/*      */             try {
/* 2456 */               byte[] buff = new byte[8192];
/* 2457 */               ByteArrayOutputStream baos = new ByteArrayOutputStream();
/*      */               int i;
/* 2459 */               while ((i = raf.read(buff)) != -1) {
/* 2460 */                 baos.write(buff, 0, i);
/*      */               }
/* 2462 */               ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
/*      */ 
/* 2464 */               byte[] key = Hex.decodeHex(FeedDataProvider.getEncryptionKey().toCharArray());
/* 2465 */               SecretKeySpec skeySpec = new SecretKeySpec(key, "AES");
/*      */ 
/* 2467 */               Cipher cipher = Cipher.getInstance("AES");
/* 2468 */               cipher.init(2, skeySpec);
/* 2469 */               CipherInputStream cis = new CipherInputStream(bais, cipher);
/*      */ 
/* 2471 */               OrdersChunkData chunkData = new OrdersChunkData();
/* 2472 */               chunkData.readObject(cis);
/* 2473 */               if (cis.read() != -1) {
/* 2474 */                 throw new IOException("Orders file corrupted, contains more bytes than expected");
/*      */               }
/* 2476 */               cis.close();
/* 2477 */               OrdersChunkData localOrdersChunkData1 = chunkData;
/*      */ 
/* 2479 */               raf.close();
/*      */ 
/* 2482 */               if (lock.isValid()) {
/* 2483 */                 lock.release();
/*      */               }
/*      */ 
/* 2487 */               channel.close();
/*      */ 
/* 2490 */               lockRaf.close();
/*      */ 
/* 2508 */               if (wLock != null);
/*      */               return localOrdersChunkData1;
/*      */             }
/*      */             finally
/*      */             {
/* 2479 */               raf.close();
/*      */             }
/*      */           } finally {
/* 2482 */             if (lock.isValid())
/* 2483 */               lock.release();
/*      */           }
/*      */         }
/*      */         finally {
/* 2487 */           channel.close();
/*      */         }
/*      */       } finally {
/* 2490 */         lockRaf.close();
/*      */       }
/*      */     }
/*      */     catch (StreamCorruptedException e) {
/* 2494 */       LOGGER.debug(e.getMessage(), e);
/* 2495 */       file.delete();
/* 2496 */       channel = null;
/*      */       return channel;
/*      */     }
/*      */     catch (InvalidClassException e)
/*      */     {
/* 2499 */       file.delete();
/* 2500 */       FileChannel channel = null;
/*      */       return channel;
/*      */     }
/*      */     catch (DataCacheException e)
/*      */     {
/* 2502 */       throw e;
/*      */     } catch (RuntimeException e) {
/* 2504 */       throw e;
/*      */     } catch (Exception e) {
/* 2506 */       throw new DataCacheException(e);
/*      */     } finally {
/* 2508 */       if (wLock != null)
/* 2509 */         wLock.unlock(); 
/* 2509 */     }throw localObject5;
/*      */   }
/*      */ 
/*      */   public Collection<OrderHistoricalData> readOrdersData(String accountId, Instrument instrument, long from, long to)
/*      */     throws DataCacheException
/*      */   {
/* 2516 */     long[][] chunks = DataCacheUtils.separateOrderChunksForCache(from, to);
/* 2517 */     Map result = new HashMap();
/* 2518 */     for (long[] chunk : chunks) {
/* 2519 */       OrdersChunkData chunkData = readOrdersData(accountId, instrument, chunk[0]);
/* 2520 */       if (chunkData == null) {
/* 2521 */         throw new DataCacheException(new StringBuilder().append("Failed to load orders chunk for accountId [").append(accountId).append("], instrument [").append(instrument).append("], time [").append(chunk[0]).append("], file [").append(DataCacheUtils.getOrdersChunkFile(this.scheme, accountId, instrument, chunk[0], this.version)).append("]").toString());
/*      */       }
/*      */ 
/* 2524 */       if (chunkData.orders != null) {
/* 2525 */         for (OrderHistoricalData data : chunkData.orders) {
/* 2526 */           if ((data.getHistoryEnd() >= from) && (data.getHistoryStart() <= to)) {
/* 2527 */             result.put(data.getOrderGroupId(), data);
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/* 2532 */     return new ArrayList(result.values());
/*      */   }
/*      */ 
/*      */   public long getLastOrderUpdateTime(Instrument instrument) {
/* 2536 */     return this.lastOrderUpdateTimes[instrument.ordinal()];
/*      */   }
/*      */ 
/*      */   public void setLastOrderUpdateTime(Instrument instrument, long lastOrderUpdateTime) {
/* 2540 */     if (this.lastOrderUpdateTimes[instrument.ordinal()] < lastOrderUpdateTime)
/* 2541 */       this.lastOrderUpdateTimes[instrument.ordinal()] = lastOrderUpdateTime;
/*      */   }
/*      */ 
/*      */   public void resetLastOrderUpdateTimes()
/*      */   {
/* 2546 */     for (int i = 0; i < this.lastOrderUpdateTimes.length; i++)
/* 2547 */       this.lastOrderUpdateTimes[i] = -9223372036854775808L;
/*      */   }
/*      */ 
/*      */   public void saveOrderData(String accountId, Instrument instrument, OrderHistoricalData order) throws DataCacheException
/*      */   {
/* 2552 */     long[][] chunks = DataCacheUtils.separateOrderChunksForCache(order.getHistoryStart(), order.getHistoryEnd());
/* 2553 */     for (long[] chunk : chunks) {
/* 2554 */       File file = DataCacheUtils.getOrdersChunkFile(this.scheme, accountId, instrument, chunk[0], this.version);
/* 2555 */       File lockFile = new File(new StringBuilder().append(file.getPath()).append(".lock").toString());
/* 2556 */       ReentrantReadWriteLock rwLock = getLock(file);
/* 2557 */       ReentrantReadWriteLock.WriteLock wLock = rwLock.writeLock();
/* 2558 */       wLock.lock();
/*      */       try {
/* 2560 */         if ((!file.getParentFile().mkdirs()) && (!file.getParentFile().exists())) {
/* 2561 */           throw new DataCacheException("Cannot create cache directory");
/*      */         }
/* 2563 */         if (file.exists()) {
/* 2564 */           RandomAccessFile lockRaf = new RandomAccessFile(lockFile, "rw");
/*      */           try {
/* 2566 */             FileChannel channel = lockRaf.getChannel();
/*      */             try {
/* 2568 */               FileLock lock = tryLock(lockFile, channel, 10000L);
/*      */               try {
/* 2570 */                 RandomAccessFile raf = new RandomAccessFile(file, "rw");
/*      */                 try
/*      */                 {
/* 2573 */                   byte[] buff = new byte[8192];
/* 2574 */                   ByteArrayOutputStream baos = new ByteArrayOutputStream();
/*      */                   int i;
/* 2576 */                   while ((i = raf.read(buff)) != -1) {
/* 2577 */                     baos.write(buff, 0, i);
/*      */                   }
/* 2579 */                   ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
/*      */ 
/* 2581 */                   byte[] key = Hex.decodeHex(FeedDataProvider.getEncryptionKey().toCharArray());
/* 2582 */                   SecretKeySpec skeySpec = new SecretKeySpec(key, "AES");
/*      */ 
/* 2584 */                   Cipher cipher = Cipher.getInstance("AES");
/* 2585 */                   cipher.init(2, skeySpec);
/* 2586 */                   CipherInputStream cis = new CipherInputStream(bais, cipher);
/*      */ 
/* 2588 */                   OrdersChunkData chunkData = new OrdersChunkData();
/* 2589 */                   chunkData.readObject(cis);
/* 2590 */                   if (cis.read() != -1) {
/* 2591 */                     throw new IOException("Orders file corrupted, contains more bytes than expected");
/*      */                   }
/* 2593 */                   cis.close();
/* 2594 */                   if ((chunkData.full) || (chunkData.to == getLastOrderUpdateTime(instrument))) {
/* 2595 */                     setLastOrderUpdateTime(instrument, order.getHistoryEnd());
/* 2596 */                     if (!chunkData.full) {
/* 2597 */                       if (order.getHistoryEnd() > chunk[1])
/* 2598 */                         chunkData.to = chunk[1];
/*      */                       else {
/* 2600 */                         chunkData.to = order.getHistoryEnd();
/*      */                       }
/*      */                     }
/* 2603 */                     boolean orderExists = false;
/* 2604 */                     for (OrderHistoricalData savedOrder : chunkData.orders) {
/* 2605 */                       if (savedOrder.getOrderGroupId().equals(order.getOrderGroupId())) {
/* 2606 */                         orderExists = true;
/* 2607 */                         break;
/*      */                       }
/*      */                     }
/* 2610 */                     if (!orderExists) {
/* 2611 */                       chunkData.orders.add(order);
/* 2612 */                       Collections.sort(chunkData.orders, new Comparator()
/*      */                       {
/*      */                         public int compare(OrderHistoricalData o1, OrderHistoricalData o2) {
/* 2615 */                           if (o1.getHistoryStart() > o2.getHistoryStart())
/* 2616 */                             return 1;
/* 2617 */                           if (o1.getHistoryStart() < o2.getHistoryStart()) {
/* 2618 */                             return -1;
/*      */                           }
/* 2620 */                           return 0;
/*      */                         }
/*      */                       });
/* 2623 */                       if (chunkData.openGroupsIds.contains(order.getOrderGroupId())) {
/* 2624 */                         chunkData.openGroupsIds.remove(order.getOrderGroupId());
/*      */                       }
/*      */ 
/* 2627 */                       baos = new ByteArrayOutputStream();
/*      */ 
/* 2629 */                       cipher.init(1, skeySpec);
/* 2630 */                       CipherOutputStream cos = new CipherOutputStream(baos, cipher);
/*      */ 
/* 2632 */                       chunkData.writeObject(cos);
/* 2633 */                       cos.flush();
/* 2634 */                       cos.close();
/* 2635 */                       byte[] data = baos.toByteArray();
/* 2636 */                       raf.setLength(0L);
/* 2637 */                       raf.seek(0L);
/* 2638 */                       raf.write(data);
/*      */ 
/* 2640 */                       if (assertionsEnabled()) {
/* 2641 */                         raf.seek(0L);
/* 2642 */                         baos = new ByteArrayOutputStream();
/* 2643 */                         while ((i = raf.read(buff)) != -1) {
/* 2644 */                           baos.write(buff, 0, i);
/*      */                         }
/* 2646 */                         bais = new ByteArrayInputStream(baos.toByteArray());
/*      */ 
/* 2648 */                         cipher.init(2, skeySpec);
/* 2649 */                         cis = new CipherInputStream(bais, cipher);
/*      */ 
/* 2651 */                         OrdersChunkData checkData = new OrdersChunkData();
/* 2652 */                         checkData.readObject(cis);
/* 2653 */                         if (cis.read() != -1) {
/* 2654 */                           throw new IOException("Orders file corrupted, contains more bytes than expected");
/*      */                         }
/* 2656 */                         cis.close();
/*      */ 
/* 2658 */                         if (!chunkData.equals(checkData)) {
/* 2659 */                           throw new DataCacheException("Saved orders data not equal to restored orders data");
/*      */                         }
/*      */                       }
/*      */                     }
/*      */                   }
/*      */                 }
/*      */                 finally
/*      */                 {
/* 2667 */                   raf.close();
/*      */                 }
/*      */               } finally {
/* 2670 */                 if (lock.isValid())
/* 2671 */                   lock.release();
/*      */               }
/*      */             }
/*      */             finally {
/* 2675 */               channel.close();
/*      */             }
/*      */           } finally {
/* 2678 */             lockRaf.close();
/*      */           }
/*      */         }
/*      */ 
/*      */       }
/*      */       catch (StreamCorruptedException e)
/*      */       {
/* 2685 */         LOGGER.debug(e.getMessage(), e);
/* 2686 */         file.delete();
/*      */       } catch (DataCacheException e) {
/* 2688 */         throw e;
/*      */       } catch (RuntimeException e) {
/* 2690 */         throw e;
/*      */       } catch (Exception e) {
/* 2692 */         throw new DataCacheException(e);
/*      */       } finally {
/* 2694 */         if (wLock != null)
/* 2695 */           wLock.unlock();
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   static
/*      */   {
/*   67 */     LOGGER = LoggerFactory.getLogger(LocalCacheManager.class);
/*      */ 
/*   69 */     DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");
/*      */ 
/*   71 */     DATE_FORMAT.setTimeZone(TimeZone.getTimeZone("GMT 0"));
/*      */ 
/*   77 */     cacheManagers = Collections.synchronizedList(new ArrayList());
/*      */ 
/*   98 */     ZERO_FEED_COMMISSION_MANAGER = new ZeroFeedCommissionManager();
/*      */   }
/*      */ 
/*      */   private static class CandleWriteTask extends LocalCacheManager.DelayedWriteTask
/*      */   {
/*      */     public Period period;
/*      */     public long firstChunkCandle;
/* 2828 */     public List<IntraPeriodCandleData> candleData = new ArrayList();
/*      */ 
/*      */     private CandleWriteTask()
/*      */     {
/* 2825 */       super();
/*      */     }
/*      */   }
/*      */ 
/*      */   private static class TickWriteTask extends LocalCacheManager.DelayedWriteTask
/*      */   {
/* 2806 */     public byte[] data = new byte[40];
/*      */     public int size;
/*      */     public long lastWrittenTickTime;
/*      */ 
/*      */     private TickWriteTask()
/*      */     {
/* 2805 */       super();
/*      */     }
/*      */ 
/*      */     public void ensureCapacity(int minCapacity)
/*      */     {
/* 2811 */       int oldCapacity = this.data.length;
/* 2812 */       if (minCapacity > oldCapacity) {
/* 2813 */         int newCapacity = oldCapacity * 3 / 2 + 1;
/* 2814 */         if (newCapacity < minCapacity) {
/* 2815 */           newCapacity = minCapacity;
/*      */         }
/*      */ 
/* 2818 */         byte[] copy = new byte[newCapacity];
/* 2819 */         System.arraycopy(this.data, 0, copy, 0, oldCapacity);
/* 2820 */         this.data = copy;
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   private static class DelayedWriteTask
/*      */   {
/*      */     public File intraperiodFile;
/*      */     public Instrument instrument;
/*      */   }
/*      */ 
/*      */   private class WriteDelayedTasks extends TimerTask
/*      */   {
/*      */     private WriteDelayedTasks()
/*      */     {
/*      */     }
/*      */ 
/*      */     public void run()
/*      */     {
/* 2766 */       if (LocalCacheManager.LOGGER.isTraceEnabled())
/* 2767 */         LocalCacheManager.LOGGER.trace("Flushing delayed write tasks [" + LocalCacheManager.this.delayedWriteTasks.size() + "]");
/*      */       while (true)
/*      */       {
/*      */         File file;
/* 2772 */         synchronized (LocalCacheManager.this.delayedWriteTasks) {
/* 2773 */           if (LocalCacheManager.this.delayedWriteTasks.isEmpty()) {
/* 2774 */             return;
/*      */           }
/* 2776 */           file = (File)LocalCacheManager.this.delayedWriteTasks.keySet().iterator().next();
/*      */         }
/*      */ 
/* 2779 */         ReentrantReadWriteLock rwLock = LocalCacheManager.this.getLock(file);
/* 2780 */         ReentrantReadWriteLock.WriteLock wLock = rwLock.writeLock();
/* 2781 */         wLock.lock();
/*      */         try {
/* 2783 */           if ((!file.getParentFile().mkdirs()) && (!file.getParentFile().exists())) {
/* 2784 */             LocalCacheManager.LOGGER.error("Cannot create cache directory");
/*      */           }
/*      */           try
/*      */           {
/* 2788 */             RandomAccessFile fileHandle = LocalCacheManager.this.getIntraPeriodFileHandle(file);
/* 2789 */             LocalCacheManager.this.flushDelayedWriteTasks(file, fileHandle);
/*      */           } catch (Exception e) {
/* 2791 */             LocalCacheManager.LOGGER.error(e.getMessage(), e);
/*      */           }
/*      */         } finally {
/* 2794 */           wLock.unlock();
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   private class CloseFileLocks extends TimerTask
/*      */   {
/*      */     private CloseFileLocks()
/*      */     {
/*      */     }
/*      */ 
/*      */     public void run()
/*      */     {
/*      */       try
/*      */       {
/* 2740 */         synchronized (LocalCacheManager.this.fileLocks) {
/* 2741 */           if (LocalCacheManager.this.fileLocks.size() > 40) {
/* 2742 */             Iterator iterator = LocalCacheManager.this.fileLocks.entrySet().iterator();
/* 2743 */             while ((iterator.hasNext()) && (LocalCacheManager.this.fileLocks.size() > 40)) {
/* 2744 */               Map.Entry entry = (Map.Entry)iterator.next();
/* 2745 */               ReentrantReadWriteLock rwLock = (ReentrantReadWriteLock)entry.getValue();
/* 2746 */               ReentrantReadWriteLock.WriteLock wLock = rwLock.writeLock();
/* 2747 */               boolean locked = wLock.tryLock();
/* 2748 */               if (locked)
/*      */                 try {
/* 2750 */                   iterator.remove();
/*      */                 } finally {
/* 2752 */                   wLock.unlock();
/*      */                 }
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/*      */       catch (Exception e) {
/* 2759 */         LocalCacheManager.LOGGER.error(e.getMessage(), e);
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   private class CloseFileHandles extends TimerTask
/*      */   {
/*      */     private CloseFileHandles()
/*      */     {
/*      */     }
/*      */ 
/*      */     public void run()
/*      */     {
/*      */       try
/*      */       {
/* 2706 */         synchronized (LocalCacheManager.this.fileHandles) {
/* 2707 */           if (LocalCacheManager.this.fileHandles.size() > 40) {
/* 2708 */             if (LocalCacheManager.LOGGER.isTraceEnabled()) {
/* 2709 */               LocalCacheManager.LOGGER.trace("[" + LocalCacheManager.this.fileHandles.size() + "] open file handles, closing less used of them");
/*      */             }
/* 2711 */             Iterator iterator = LocalCacheManager.this.fileHandles.entrySet().iterator();
/* 2712 */             while ((iterator.hasNext()) && (LocalCacheManager.this.fileHandles.size() > 40)) {
/* 2713 */               Map.Entry entry = (Map.Entry)iterator.next();
/* 2714 */               File fileToClose = (File)entry.getKey();
/* 2715 */               ReentrantReadWriteLock rwLock = LocalCacheManager.this.getLock(fileToClose);
/* 2716 */               ReentrantReadWriteLock.WriteLock wLock = rwLock.writeLock();
/* 2717 */               boolean locked = wLock.tryLock();
/* 2718 */               if (locked)
/*      */                 try {
/* 2720 */                   RandomAccessFile raFile = (RandomAccessFile)entry.getValue();
/* 2721 */                   raFile.close();
/* 2722 */                   iterator.remove();
/* 2723 */                   LocalCacheManager.LOGGER.trace("Removing file handle for file [" + fileToClose + "]");
/*      */                 } finally {
/* 2725 */                   wLock.unlock();
/*      */                 }
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/*      */       catch (IOException e) {
/* 2732 */         LocalCacheManager.LOGGER.error(e.getMessage(), e);
/*      */       }
/*      */     }
/*      */   }
/*      */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.charts.data.datacache.LocalCacheManager
 * JD-Core Version:    0.6.0
 */