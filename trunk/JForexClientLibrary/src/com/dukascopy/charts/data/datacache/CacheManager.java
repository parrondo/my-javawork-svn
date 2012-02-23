/*      */ package com.dukascopy.charts.data.datacache;
/*      */ 
/*      */ import SevenZip.Compression.LZMA.Encoder;
/*      */ import com.dukascopy.api.Instrument;
/*      */ import com.dukascopy.api.OfferSide;
/*      */ import com.dukascopy.api.Period;
/*      */ import com.dukascopy.api.Unit;
/*      */ import com.dukascopy.charts.data.datacache.feed.IFeedCommissionManager;
/*      */ import com.dukascopy.dds2.greed.agent.strategy.StratUtils;
/*      */ import com.dukascopy.dds2.greed.gui.l10n.components.JLocalizableButton;
/*      */ import com.dukascopy.dds2.greed.gui.l10n.components.JLocalizableDialog;
/*      */ import com.dukascopy.dds2.greed.gui.l10n.components.JLocalizableLabel;
/*      */ import com.dukascopy.dds2.greed.util.FilePathManager;
/*      */ import java.awt.Frame;
/*      */ import java.awt.GridBagConstraints;
/*      */ import java.awt.GridBagLayout;
/*      */ import java.awt.event.ActionEvent;
/*      */ import java.awt.event.ActionListener;
/*      */ import java.io.BufferedInputStream;
/*      */ import java.io.BufferedOutputStream;
/*      */ import java.io.File;
/*      */ import java.io.FileFilter;
/*      */ import java.io.FileInputStream;
/*      */ import java.io.FileOutputStream;
/*      */ import java.io.FileReader;
/*      */ import java.io.FileWriter;
/*      */ import java.io.IOException;
/*      */ import java.io.RandomAccessFile;
/*      */ import java.security.AccessControlException;
/*      */ import java.text.DateFormat;
/*      */ import java.text.SimpleDateFormat;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Calendar;
/*      */ import java.util.Date;
/*      */ import java.util.Iterator;
/*      */ import java.util.LinkedHashMap;
/*      */ import java.util.List;
/*      */ import java.util.Map;
/*      */ import java.util.Map.Entry;
/*      */ import java.util.Set;
/*      */ import java.util.TimeZone;
/*      */ import java.util.concurrent.TimeUnit;
/*      */ import java.util.concurrent.locks.ReentrantReadWriteLock;
/*      */ import java.util.concurrent.locks.ReentrantReadWriteLock.ReadLock;
/*      */ import java.util.concurrent.locks.ReentrantReadWriteLock.WriteLock;
/*      */ import java.util.regex.Matcher;
/*      */ import java.util.regex.Pattern;
/*      */ import java.util.zip.GZIPOutputStream;
/*      */ import java.util.zip.ZipEntry;
/*      */ import java.util.zip.ZipOutputStream;
/*      */ import javax.swing.JButton;
/*      */ import javax.swing.JDialog;
/*      */ import javax.swing.JPanel;
/*      */ import javax.swing.JProgressBar;
/*      */ import javax.swing.SwingUtilities;
/*      */ import javax.swing.Timer;
/*      */ import org.slf4j.Logger;
/*      */ import org.slf4j.LoggerFactory;
/*      */ 
/*      */ public class CacheManager
/*      */ {
/*      */   private static final Logger LOGGER;
/*      */   protected static final int EOF_INDEX = -1;
/*      */   protected static final long FIRST_TICK_MARKER = -9223372036854775808L;
/*      */   private static final Pattern tickFilePattern;
/*      */   private static final Pattern yearCandleFilesPattern;
/*      */   private static final Pattern monthCandleFilesPattern;
/*      */   private static final Pattern dayCandleFilesPattern;
/*      */   private static final Pattern lockFilesPattern;
/*      */   private static final Pattern tempFilesPattern;
/*   80 */   protected final Map<File, ReentrantReadWriteLock> fileLocks = new LinkedHashMap(140, 0.75F, true);
/*   81 */   protected final Map<File, Boolean> fileExistanceCache = new LinkedHashMap(500, 0.75F, true) {
/*      */     protected boolean removeEldestEntry(Map.Entry<File, Boolean> eldest) {
/*   83 */       return size() > 500; }  } ;
/*      */ 
/*   86 */   protected final Map<File, FileCacheItem> fileHandlesCache = new LinkedHashMap(100, 0.75F, true);
/*      */   protected int version;
/*      */ 
/*   90 */   public CacheManager(boolean purgeOnWrongVersion) throws DataCacheException { File cacheDir = new File(FilePathManager.getInstance().getCacheDirectory());
/*      */ 
/*   93 */     File versionFile = new File(cacheDir, "version.txt");
/*   94 */     if (versionFile.exists()) { try { FileReader reader = new FileReader(versionFile);
/*      */         String versionStr;
/*      */         try {
/*   99 */           char[] buff = new char[256];
/*      */ 
/*  101 */           int readBytes = 0;
/*      */           int i;
/*  102 */           while (((i = reader.read(buff, readBytes, buff.length - readBytes)) > -1) && (readBytes < buff.length)) {
/*  103 */             readBytes += i;
/*      */           }
/*  105 */           versionStr = new String(buff, 0, readBytes);
/*      */         } finally {
/*  107 */           reader.close();
/*      */         }
/*  109 */         if (!versionStr.equals(Integer.toString(5))) {
/*  110 */           int parsedVersion = 0;
/*      */           try {
/*  112 */             parsedVersion = Integer.parseInt(versionStr);
/*      */           }
/*      */           catch (NumberFormatException e) {
/*      */           }
/*  116 */           if (purgeOnWrongVersion) {
/*  117 */             boolean converted = true;
/*  118 */             if (parsedVersion == 4)
/*  119 */               converted = convertFromVersion4To5(cacheDir);
/*      */             else {
/*  121 */               deleteCache(cacheDir);
/*      */             }
/*  123 */             if (converted)
/*  124 */               this.version = 5;
/*      */             else
/*  126 */               this.version = 4;
/*      */           }
/*  128 */           else if (parsedVersion == 4) {
/*  129 */             this.version = 4;
/*      */           } else {
/*  131 */             deleteCache(cacheDir);
/*  132 */             this.version = 5;
/*      */           }
/*      */         } else {
/*  135 */           this.version = 5;
/*      */         }
/*      */       } catch (IOException e) {
/*  138 */         throw new DataCacheException("Cannot read version file of cache");
/*      */       }
/*      */     } else {
/*  141 */       deleteCache(cacheDir);
/*  142 */       this.version = 5;
/*      */     }
/*      */ 
/*  146 */     if ((!cacheDir.exists()) && (!cacheDir.mkdirs()) && (!cacheDir.exists())) {
/*  147 */       throw new DataCacheException(new StringBuilder().append("Cannot create cache directory [").append(cacheDir).append("]. File.exists() - [").append(cacheDir.exists()).append("] File.mkdirs() - [").append(cacheDir.mkdirs()).append("]").toString());
/*      */     }
/*      */ 
/*  151 */     if (!versionFile.exists())
/*      */       try {
/*  153 */         FileWriter writer = new FileWriter(versionFile);
/*      */         try {
/*  155 */           writer.write(Integer.toString(5));
/*      */         } finally {
/*  157 */           writer.close();
/*      */         }
/*      */       } catch (IOException e) {
/*  160 */         throw new DataCacheException("Cannot write version file in cache");
/*      */       } }
/*      */ 
/*      */   private boolean convertFromVersion4To5(File cacheDir)
/*      */     throws DataCacheException
/*      */   {
/*  166 */     String[] fileProcessed = { "" };
/*  167 */     int[] counts = new int[2];
/*  168 */     boolean[] canceled = { false };
/*  169 */     boolean[] converted = { false };
/*  170 */     Thread conversionThread = new Thread(new Object(cacheDir, fileProcessed, counts, canceled, converted)
/*      */     {
/*      */       public void run()
/*      */       {
/*      */         try
/*      */         {
/*      */           Iterator iterator;
/*  174 */           synchronized (CacheManager.this.fileHandlesCache) {
/*  175 */             for (iterator = CacheManager.this.fileHandlesCache.entrySet().iterator(); iterator.hasNext(); ) {
/*  176 */               Map.Entry entry = (Map.Entry)iterator.next();
/*  177 */               CacheManager.FileCacheItem cacheItem = (CacheManager.FileCacheItem)entry.getValue();
/*  178 */               for (RandomAccessFile fileHandle : cacheItem.fileHandles) {
/*  179 */                 int i = 6;
/*  180 */                 if (fileHandle == null) continue;
/*      */                 try {
/*  182 */                   fileHandle.close();
/*      */                 } catch (IOException e) {
/*  184 */                   CacheManager.LOGGER.error(e.getMessage(), e);
/*      */                 }
/*      */               }
/*      */ 
/*  188 */               iterator.remove();
/*      */             }
/*      */           }
/*      */ 
/*  192 */           if (this.val$cacheDir.exists()) {
/*  193 */             File[] files = this.val$cacheDir.listFiles();
/*  194 */             for (File file : files) {
/*  195 */               if (file.isDirectory()) {
/*  196 */                 if (CacheManager.this.isInstrumentDir(file)) {
/*  197 */                   Instrument instrument = Instrument.valueOf(file.getName().toUpperCase());
/*  198 */                   CacheManager.this.countFilesRecursive(file, instrument, this.val$fileProcessed, this.val$counts, this.val$canceled);
/*      */                 }
/*      */               }
/*  201 */               else if (CacheManager.this.isVersionOrLockFile(file)) {
/*  202 */                 this.val$counts[1] += 1;
/*      */               }
/*      */             }
/*      */ 
/*      */           }
/*      */ 
/*  208 */           if (this.val$cacheDir.exists()) {
/*  209 */             File[] files = this.val$cacheDir.listFiles();
/*      */ 
/*  211 */             for (File file : files) {
/*  212 */               if ((!file.isDirectory()) || 
/*  213 */                 (!CacheManager.this.isInstrumentDir(file))) continue;
/*  214 */               Instrument instrument = Instrument.valueOf(file.getName().toUpperCase());
/*  215 */               CacheManager.this.convertCacheRecursive(file, instrument, this.val$fileProcessed, this.val$counts, this.val$canceled);
/*      */             }
/*      */ 
/*  220 */             if (this.val$canceled[0] == 0) {
/*  221 */               this.val$converted[0] = true;
/*  222 */               for (File file : files) {
/*  223 */                 if ((file.isDirectory()) || 
/*  224 */                   (!CacheManager.this.isVersionOrLockFile(file))) continue;
/*  225 */                 this.val$counts[0] += 1;
/*  226 */                 if (!file.delete()) {
/*  227 */                   throw new DataCacheException("Cannot delete cache files [" + file.toString() + "]");
/*      */                 }
/*      */ 
/*      */               }
/*      */ 
/*  232 */               this.val$canceled[0] = false;
/*      */             }
/*      */           }
/*  235 */           if (this.val$canceled[0] == 0) {
/*  236 */             this.val$converted[0] = true;
/*      */           }
/*  238 */           this.val$fileProcessed[0] = null;
/*      */         } catch (DataCacheException e) {
/*  240 */           CacheManager.LOGGER.error(e.getMessage(), e);
/*  241 */           this.val$fileProcessed[0] = null;
/*      */         }
/*      */       }
/*      */     });
/*  245 */     if (SwingUtilities.isEventDispatchThread()) {
/*  246 */       JDialog convertDialog = new JLocalizableDialog((Frame)null, true, "cache.conversion.title", canceled, fileProcessed, counts)
/*      */       {
/*      */         JPanel contentPanel;
/*      */         JProgressBar progressBar;
/*      */       };
/*  304 */       conversionThread.start();
/*  305 */       convertDialog.setVisible(true);
/*      */       try {
/*  307 */         conversionThread.join();
/*      */       } catch (InterruptedException e) {
/*  309 */         LOGGER.error(e.getMessage(), e);
/*  310 */         throw new DataCacheException("Cache data conversion was interrupted");
/*      */       }
/*      */     } else {
/*  313 */       conversionThread.start();
/*      */       try {
/*  315 */         conversionThread.join();
/*      */       } catch (InterruptedException e) {
/*  317 */         LOGGER.error(e.getMessage(), e);
/*  318 */         throw new DataCacheException("Cache data conversion was interrupted");
/*      */       }
/*      */     }
/*  321 */     return converted[0];
/*      */   }
/*      */ 
/*      */   private void convertCacheRecursive(File dirToConvert, Instrument instrument, String[] fileProcessed, int[] counts, boolean[] canceled) throws DataCacheException {
/*  325 */     if (dirToConvert.exists()) {
/*  326 */       File[] files = dirToConvert.listFiles();
/*  327 */       for (File file : files)
/*  328 */         if (file.isDirectory()) {
/*  329 */           if (file.getName().startsWith("intraperiod"))
/*  330 */             deleteCacheRecursive(file);
/*      */           else {
/*  332 */             convertCacheRecursive(file, instrument, fileProcessed, counts, canceled);
/*      */           }
/*      */         }
/*  335 */         else if ((canceled[0] == 0) && (file.getName().endsWith("bin"))) {
/*  336 */           fileProcessed[0] = file.getPath();
/*  337 */           counts[0] += 1;
/*  338 */           convertFile(file, instrument);
/*      */         }
/*      */     }
/*      */   }
/*      */ 
/*      */   private void countFilesRecursive(File dirToConvert, Instrument instrument, String[] fileProcessed, int[] counts, boolean[] canceled)
/*      */     throws DataCacheException
/*      */   {
/*  346 */     if (dirToConvert.exists()) {
/*  347 */       File[] files = dirToConvert.listFiles();
/*  348 */       for (File file : files)
/*  349 */         if (file.isDirectory()) {
/*  350 */           if (!file.getName().startsWith("intraperiod")) {
/*  351 */             countFilesRecursive(file, instrument, fileProcessed, counts, canceled);
/*      */           }
/*      */         }
/*  354 */         else if ((canceled[0] == 0) && (file.getName().endsWith("bin")))
/*  355 */           counts[1] += 1;
/*      */     }
/*      */   }
/*      */ 
/*      */   private void convertFile(File file, Instrument instrument)
/*      */     throws DataCacheException
/*      */   {
/*  363 */     String fileName = file.getName();
/*  364 */     File newFile = new File(file.getParent(), new StringBuilder().append(fileName.substring(0, fileName.length() - 3)).append("bi5").toString());
/*  365 */     boolean isTicks = tickFilePattern.matcher(fileName).matches();
/*  366 */     List dataList = new ArrayList();
/*      */     try
/*      */     {
/*  369 */       RandomAccessFile fileHandle = new RandomAccessFile(file, "r");
/*      */       try { int dataLength = isTicks ? TickData.getLength(4) : CandleData.getLength(4);
/*  372 */         byte[] buff = new byte[8192 / dataLength * dataLength];
/*      */         int i;
/*      */         do { int readBytes = 0;
/*  376 */           while (((i = fileHandle.read(buff, readBytes, buff.length - readBytes)) > -1) && (readBytes < buff.length)) {
/*  377 */             readBytes += i;
/*      */           }
/*  379 */           for (int j = 0; j < readBytes; j += dataLength) {
/*  380 */             if (j + dataLength <= readBytes)
/*  381 */               if (isTicks) {
/*  382 */                 TickData tickData = new TickData();
/*  383 */                 tickData.fromBytes(4, 0L, 0.0D, buff, j);
/*  384 */                 dataList.add(tickData);
/*      */               } else {
/*  386 */                 CandleData candleData = new CandleData();
/*  387 */                 candleData.fromBytes(4, 0L, 0.0D, buff, j);
/*  388 */                 dataList.add(candleData);
/*      */               }
/*      */           }
/*      */         }
/*  392 */         while (i > -1);
/*      */       } finally {
/*  394 */         fileHandle.close();
/*      */       }
/*      */     } catch (IOException e) {
/*  397 */       if (!file.delete()) {
/*  398 */         throw new DataCacheException(new StringBuilder().append("Cannot delete cache files [").append(file.toString()).append("]").toString());
/*      */       }
/*      */     }
/*      */ 
/*  402 */     if (!newFile.exists()) {
/*      */       try {
/*  404 */         File tempDir = DataCacheUtils.getChunkTempDirectory();
/*      */ 
/*  406 */         if ((!tempDir.exists()) && (!tempDir.mkdirs()) && (!tempDir.exists())) {
/*  407 */           throw new DataCacheException(new StringBuilder().append("Cannot create cache temp directory [").append(tempDir).append("]. File.exists() - [").append(tempDir.exists()).append("] File.mkdirs() - [").append(tempDir.mkdirs()).append("]").toString());
/*      */         }
/*  409 */         File tempFile = File.createTempFile("JForex_cache_", ".bin", tempDir);
/*  410 */         RandomAccessFile tempFileHandle = new RandomAccessFile(tempFile, "rw");
/*      */         try {
/*  412 */           if (dataList.size() > 0) {
/*  413 */             tempFileHandle.setLength(0L);
/*  414 */             tempFileHandle.seek(0L);
/*  415 */             Data firstData = (Data)dataList.get(0);
/*  416 */             long firstChunkCandle = firstData.getTime();
/*  417 */             int bytesCount = firstData.getBytesCount(5);
/*  418 */             byte[] buffer = new byte[bytesCount * 10];
/*  419 */             int count = 0;
/*  420 */             for (Data dataBlock : dataList) {
/*  421 */               dataBlock.toBytes(5, firstChunkCandle, instrument.getPipValue(), buffer, count * bytesCount);
/*  422 */               if (assertionsEnabled()) {
/*  423 */                 if ((firstData instanceof TickData)) {
/*  424 */                   TickData checkData = new TickData();
/*  425 */                   checkData.fromBytes(5, firstChunkCandle, instrument.getPipValue(), buffer, count * bytesCount);
/*  426 */                   if (!checkData.equals(dataBlock))
/*  427 */                     throw new DataCacheException(new StringBuilder().append("Data for chunk file [").append(tempFile.getPath()).append("] was saved incorrectly. Original item [").append(dataBlock).append("], saved and read item [").append(checkData).append("]").toString());
/*      */                 }
/*      */                 else
/*      */                 {
/*  431 */                   CandleData dataBlockCandle = (CandleData)dataBlock;
/*  432 */                   dataBlockCandle.vol = StratUtils.round((float)dataBlockCandle.vol, 2);
/*  433 */                   CandleData checkData = new CandleData();
/*  434 */                   checkData.fromBytes(5, firstChunkCandle, instrument.getPipValue(), buffer, count * bytesCount);
/*  435 */                   if (!checkData.equals(dataBlock)) {
/*  436 */                     throw new DataCacheException(new StringBuilder().append("Data for chunk file [").append(tempFile.getPath()).append("] was saved incorrectly. Original item [").append(dataBlock).append("], saved and read item [").append(checkData).append("]").toString());
/*      */                   }
/*      */                 }
/*      */               }
/*      */ 
/*  441 */               if (count == 9)
/*      */               {
/*  443 */                 tempFileHandle.write(buffer);
/*  444 */                 count = 0;
/*      */               } else {
/*  446 */                 count++;
/*      */               }
/*      */             }
/*  449 */             if (count > 0)
/*  450 */               tempFileHandle.write(buffer, 0, count * bytesCount);
/*      */           }
/*      */         }
/*      */         finally {
/*  454 */           tempFileHandle.close();
/*      */         }
/*  456 */         if (!tempFile.renameTo(newFile))
/*      */         {
/*  458 */           if (!newFile.exists())
/*      */           {
/*  460 */             throw new DataCacheException(new StringBuilder().append("Cannot rename temporary file [").append(tempFile).append("] to data cache file [").append(file).append("]").toString());
/*      */           }
/*      */         }
/*      */ 
/*      */       }
/*      */       catch (IOException e)
/*      */       {
/*  467 */         throw new DataCacheException(e);
/*      */       }
/*      */     }
/*      */ 
/*  471 */     int i = 5;
/*  472 */     while ((!file.delete()) && (file.exists()) && (i > 0)) {
/*      */       try {
/*  474 */         Thread.sleep(10L);
/*      */       } catch (InterruptedException e) {
/*  476 */         LOGGER.error(e.getMessage(), e);
/*      */       }
/*  478 */       LOGGER.warn(new StringBuilder().append("Cannot delete cache files [").append(file.toString()).append("]").toString());
/*  479 */       i--;
/*      */     }
/*  481 */     if ((!file.delete()) && (file.exists()))
/*  482 */       throw new DataCacheException(new StringBuilder().append("Cannot delete cache files [").append(file.toString()).append("]").toString());
/*      */   }
/*      */ 
/*      */   public void recreateCache() throws DataCacheException
/*      */   {
/*  487 */     File cacheDir = new File(FilePathManager.getInstance().getCacheDirectory());
/*      */ 
/*  489 */     deleteCache(cacheDir);
/*      */ 
/*  492 */     if ((!cacheDir.exists()) && (!cacheDir.mkdirs()) && (!cacheDir.exists())) {
/*  493 */       throw new DataCacheException(new StringBuilder().append("Cannot create cache directory [").append(cacheDir).append("]. File.exists() - [").append(cacheDir.exists()).append("] File.mkdirs() - [").append(cacheDir.mkdirs()).append("]").toString());
/*      */     }
/*      */ 
/*  496 */     File versionFile = new File(cacheDir, "version.txt");
/*      */ 
/*  499 */     if (!versionFile.exists())
/*      */       try {
/*  501 */         FileWriter writer = new FileWriter(versionFile);
/*      */         try {
/*  503 */           writer.write(Integer.toString(5));
/*      */         } finally {
/*  505 */           writer.close();
/*      */         }
/*      */       } catch (IOException e) {
/*  508 */         throw new DataCacheException("Cannot write version file in cache");
/*      */       }
/*      */   }
/*      */ 
/*      */   protected void deleteCache(File dirToDelete)
/*      */     throws DataCacheException
/*      */   {
/*      */     Iterator iterator;
/*  514 */     synchronized (this.fileHandlesCache) {
/*  515 */       for (iterator = this.fileHandlesCache.entrySet().iterator(); iterator.hasNext(); ) {
/*  516 */         Map.Entry entry = (Map.Entry)iterator.next();
/*  517 */         FileCacheItem cacheItem = (FileCacheItem)entry.getValue();
/*  518 */         for (RandomAccessFile fileHandle : cacheItem.fileHandles) {
/*  519 */           if (fileHandle == null) continue;
/*      */           try {
/*  521 */             fileHandle.close();
/*      */           } catch (IOException e) {
/*  523 */             LOGGER.error(e.getMessage(), e);
/*      */           }
/*      */         }
/*      */ 
/*  527 */         iterator.remove();
/*      */       }
/*      */     }
/*      */ 
/*  531 */     synchronized (this.fileExistanceCache) {
/*  532 */       this.fileExistanceCache.clear();
/*      */     }
/*      */ 
/*  535 */     if (dirToDelete.exists()) {
/*  536 */       File[] files = dirToDelete.listFiles();
/*  537 */       for (File file : files)
/*  538 */         if (file.isDirectory()) {
/*  539 */           if (isInstrumentDir(file))
/*  540 */             deleteCacheRecursive(file);
/*      */         }
/*      */         else {
/*  543 */           if ((!isVersionOrLockFile(file)) || 
/*  544 */             (file.delete())) continue;
/*  545 */           throw new DataCacheException(new StringBuilder().append("Cannot delete cache files [").append(file.toString()).append("]").toString());
/*      */         }
/*      */     }
/*      */   }
/*      */ 
/*      */   protected static void deleteCacheRecursive(File dirToDelete)
/*      */     throws DataCacheException
/*      */   {
/*  554 */     if (dirToDelete.exists()) {
/*  555 */       File[] files = dirToDelete.listFiles();
/*  556 */       for (File file : files) {
/*  557 */         if (file.isDirectory()) {
/*  558 */           deleteCacheRecursive(file);
/*      */         }
/*      */         else {
/*  561 */           int i = 5;
/*  562 */           while ((!file.delete()) && (file.exists()) && (i > 0)) {
/*      */             try {
/*  564 */               Thread.sleep(10L);
/*      */             } catch (InterruptedException e) {
/*  566 */               LOGGER.error(e.getMessage(), e);
/*      */             }
/*  568 */             LOGGER.warn(new StringBuilder().append("Cannot delete cache files [").append(file.toString()).append("]").toString());
/*  569 */             i--;
/*      */           }
/*  571 */           if ((!file.delete()) && (file.exists())) {
/*  572 */             throw new DataCacheException(new StringBuilder().append("Cannot delete cache files [").append(file.toString()).append("]").toString());
/*      */           }
/*      */         }
/*      */       }
/*      */ 
/*  577 */       int i = 5;
/*  578 */       while ((!dirToDelete.delete()) && (dirToDelete.exists()) && (i > 0)) {
/*      */         try {
/*  580 */           Thread.sleep(10L);
/*      */         } catch (InterruptedException e) {
/*  582 */           LOGGER.error(e.getMessage(), e);
/*      */         }
/*  584 */         LOGGER.warn(new StringBuilder().append("Cannot delete cache files [").append(dirToDelete.toString()).append("]").toString());
/*  585 */         i--;
/*      */       }
/*  587 */       if ((!dirToDelete.delete()) && (dirToDelete.exists()))
/*  588 */         throw new DataCacheException(new StringBuilder().append("Cannot delete cache files [").append(dirToDelete.toString()).append("]").toString());
/*      */     }
/*      */   }
/*      */ 
/*      */   public void deleteInstrumentCache(Instrument instrument, boolean deleteFolderToo) throws DataCacheException
/*      */   {
/*  594 */     File cacheDir = new File(FilePathManager.getInstance().getCacheDirectory());
/*  595 */     if (cacheDir.exists()) {
/*  596 */       File[] files = cacheDir.listFiles();
/*  597 */       for (File file : files) {
/*  598 */         if ((!file.isDirectory()) || 
/*  599 */           (!file.getName().equalsIgnoreCase(instrument.name()))) continue;
/*  600 */         deleteCache(file);
/*  601 */         if ((deleteFolderToo) && (!file.delete()))
/*  602 */           throw new DataCacheException("Cannot delete cache files");
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   private boolean isInstrumentDir(File file)
/*      */   {
/*  611 */     if ((file.exists()) && (file.isDirectory())) {
/*  612 */       String name = file.getName();
/*  613 */       for (Instrument value : Instrument.values()) {
/*  614 */         if (value.name().equalsIgnoreCase(name)) {
/*  615 */           return true;
/*      */         }
/*      */       }
/*      */     }
/*  619 */     return false;
/*      */   }
/*      */ 
/*      */   private boolean isVersionOrLockFile(File file) {
/*  623 */     if ((file.exists()) && (file.isFile())) {
/*  624 */       String fileName = file.getName();
/*  625 */       if (fileName.equalsIgnoreCase("version.txt")) {
/*  626 */         return true;
/*      */       }
/*  628 */       Matcher lockFilesMatcher = lockFilesPattern.matcher(fileName);
/*  629 */       if (lockFilesMatcher.matches()) {
/*  630 */         return true;
/*      */       }
/*  632 */       Matcher tempFilesMatcher = tempFilesPattern.matcher(fileName);
/*  633 */       if (tempFilesMatcher.matches()) {
/*  634 */         return true;
/*      */       }
/*      */     }
/*  637 */     return false;
/*      */   }
/*      */ 
/*      */   protected RandomAccessFile getChunkFileHandle(File file, boolean cache)
/*      */     throws IOException
/*      */   {
/*  643 */     if (cache) {
/*  644 */       int waitCount = 50;
/*  645 */       while (waitCount > 0)
/*      */       {
/*  647 */         synchronized (this.fileHandlesCache) {
/*  648 */           int fileHandlesCacheSize = this.fileHandlesCache.size();
/*  649 */           FileCacheItem cacheItem = (FileCacheItem)this.fileHandlesCache.get(file);
/*  650 */           if (cacheItem == null) {
/*  651 */             cacheItem = new FileCacheItem();
/*  652 */             this.fileHandlesCache.put(file, cacheItem);
/*      */           }
/*  654 */           for (int i = 0; i < cacheItem.taken.length; i++)
/*  655 */             if (cacheItem.taken[i] == 0) {
/*  656 */               if (cacheItem.fileHandles[i] == null) {
/*  657 */                 for (int k = 0; k < 3; k++) {
/*      */                   try {
/*  659 */                     cacheItem.fileHandles[i] = new RandomAccessFile(file, "r");
/*      */                   }
/*      */                   catch (AccessControlException e) {
/*  662 */                     if (k == 2)
/*  663 */                       throw e;
/*      */                     try
/*      */                     {
/*  666 */                       Thread.sleep(10L);
/*      */                     }
/*      */                     catch (InterruptedException e1)
/*      */                     {
/*      */                     }
/*      */                   }
/*      */                 }
/*      */               }
/*  674 */               cacheItem.taken[i] = true;
/*      */ 
/*  677 */               if (this.fileHandlesCache.size() > 70) {
/*  678 */                 int toDelete = this.fileHandlesCache.size() - 70;
/*  679 */                 for (Iterator iterator = this.fileHandlesCache.entrySet().iterator(); (iterator.hasNext()) && (toDelete > 0); ) {
/*  680 */                   FileCacheItem item = (FileCacheItem)((Map.Entry)iterator.next()).getValue();
/*  681 */                   boolean release = true;
/*  682 */                   for (int j = 0; j < item.taken.length; j++) {
/*  683 */                     if (item.taken[j] == 0)
/*      */                       continue;
/*  685 */                     release = false;
/*      */                   }
/*      */ 
/*  688 */                   if (release) {
/*  689 */                     LOGGER.trace("Deleting eldest fileHandlesCache entry");
/*  690 */                     for (int j = 0; j < item.fileHandles.length; j++) {
/*  691 */                       if (item.fileHandles[j] == null) continue;
/*      */                       try {
/*  693 */                         item.fileHandles[j].close();
/*  694 */                         item.fileHandles[j] = null;
/*      */                       } catch (IOException e) {
/*  696 */                         LOGGER.error(e.getMessage(), e);
/*      */                       }
/*      */                     }
/*      */ 
/*  700 */                     iterator.remove();
/*  701 */                     toDelete--;
/*      */                   }
/*      */                 }
/*  704 */                 if ((toDelete > 0) && (fileHandlesCacheSize != this.fileHandlesCache.size())) {
/*  705 */                   int handlesCount = 0;
/*  706 */                   for (FileCacheItem item : this.fileHandlesCache.values()) {
/*  707 */                     for (RandomAccessFile fileHandle : item.fileHandles) {
/*  708 */                       if (fileHandle != null) {
/*  709 */                         handlesCount++;
/*      */                       }
/*      */                     }
/*      */                   }
/*  713 */                   LOGGER.debug(new StringBuilder().append("File cache overload... [").append(this.fileHandlesCache.size()).append("] cache items, [").append(handlesCount).append("] open handles").toString());
/*      */                 }
/*  715 */                 return cacheItem.fileHandles[i];
/*      */               }
/*  717 */               return cacheItem.fileHandles[i];
/*      */             }
/*      */           try
/*      */           {
/*  721 */             Thread.sleep(10L);
/*  722 */             waitCount--;
/*      */           } catch (InterruptedException e) {
/*  724 */             LOGGER.error(e.getMessage(), e);
/*      */           }
/*      */         }
/*      */       }
/*  728 */       for (int k = 0; k < 3; k++)
/*      */         try {
/*  730 */           return new RandomAccessFile(file, "r");
/*      */         } catch (AccessControlException e) {
/*  732 */           if (k == 2)
/*  733 */             throw e;
/*      */           try
/*      */           {
/*  736 */             Thread.sleep(10L);
/*      */           }
/*      */           catch (InterruptedException e1)
/*      */           {
/*      */           }
/*      */         }
/*      */     }
/*      */     else {
/*  744 */       for (int k = 0; k < 3; k++) {
/*      */         try {
/*  746 */           return new RandomAccessFile(file, "r");
/*      */         } catch (AccessControlException e) {
/*  748 */           if (k == 2)
/*  749 */             throw e;
/*      */           try
/*      */           {
/*  752 */             Thread.sleep(10L);
/*      */           }
/*      */           catch (InterruptedException e1)
/*      */           {
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */ 
/*  761 */     throw new IOException("Cannot open file");
/*      */   }
/*      */ 
/*      */   protected void returnChunkFileHandle(File file, RandomAccessFile fileHandle) throws IOException {
/*  765 */     synchronized (this.fileHandlesCache) {
/*  766 */       FileCacheItem cacheItem = (FileCacheItem)this.fileHandlesCache.get(file);
/*  767 */       if (cacheItem != null) {
/*  768 */         for (int i = 0; i < cacheItem.taken.length; i++) {
/*  769 */           if (cacheItem.fileHandles[i] != fileHandle)
/*      */             continue;
/*  771 */           cacheItem.taken[i] = false;
/*  772 */           return;
/*      */         }
/*      */       }
/*      */ 
/*  776 */       fileHandle.close();
/*      */     }
/*      */   }
/*      */ 
/*      */   protected void closeHandles()
/*      */   {
/*      */     Iterator iterator;
/*  782 */     synchronized (this.fileHandlesCache) {
/*  783 */       for (iterator = this.fileHandlesCache.entrySet().iterator(); iterator.hasNext(); ) {
/*  784 */         Map.Entry entry = (Map.Entry)iterator.next();
/*  785 */         File file = (File)entry.getKey();
/*  786 */         ReentrantReadWriteLock rwLock = getLock(file);
/*  787 */         ReentrantReadWriteLock.WriteLock wLock = rwLock.writeLock();
/*  788 */         wLock.lock();
/*      */         try {
/*  790 */           FileCacheItem cacheItem = (FileCacheItem)entry.getValue();
/*  791 */           for (RandomAccessFile fileHandle : cacheItem.fileHandles) {
/*  792 */             if (fileHandle == null) continue;
/*      */             try {
/*  794 */               fileHandle.close();
/*      */             } catch (IOException e) {
/*  796 */               LOGGER.error(e.getMessage(), e);
/*      */             }
/*      */           }
/*      */         }
/*      */         finally {
/*  801 */           wLock.unlock();
/*      */         }
/*  803 */         iterator.remove();
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   protected boolean chunkFileExists(File file)
/*      */   {
/*      */     Boolean exists;
/*  811 */     synchronized (this.fileExistanceCache) {
/*  812 */       exists = (Boolean)this.fileExistanceCache.get(file);
/*      */     }
/*  814 */     if (exists == null) {
/*  815 */       for (int i = 0; i < 3; i++) {
/*      */         try {
/*  817 */           exists = Boolean.valueOf(file.exists());
/*      */         }
/*      */         catch (AccessControlException e)
/*      */         {
/*  821 */           if (i == 2)
/*      */           {
/*  823 */             throw e;
/*      */           }
/*      */           try {
/*  826 */             Thread.sleep(10L);
/*      */           }
/*      */           catch (InterruptedException e1)
/*      */           {
/*      */           }
/*      */         }
/*      */       }
/*  833 */       if (exists.booleanValue()) {
/*  834 */         synchronized (this.fileExistanceCache) {
/*  835 */           this.fileExistanceCache.put(file, exists);
/*      */         }
/*  837 */         return true;
/*      */       }
/*  839 */       return false;
/*      */     }
/*  841 */     return true;
/*      */   }
/*      */ 
/*      */   public boolean isDataChunkExists(Instrument instrument, Period period, OfferSide side, long from) throws DataCacheException
/*      */   {
/*  846 */     from = DataCacheUtils.getChunkStart(period, from);
/*  847 */     File file = DataCacheUtils.getChunkFile(instrument, period, side, from, this.version);
/*  848 */     return chunkFileExists(file);
/*      */   }
/*      */ 
/*      */   public void deleteDataChunk(Instrument instrument, Period period, OfferSide side, long from) throws DataCacheException {
/*  852 */     from = DataCacheUtils.getChunkStart(period, from);
/*  853 */     File file = DataCacheUtils.getChunkFile(instrument, period, side, from, this.version);
/*  854 */     deleteDataChunk(file);
/*      */   }
/*      */ 
/*      */   public void deleteDataChunk(File file) throws DataCacheException {
/*  858 */     synchronized (this.fileExistanceCache) {
/*  859 */       this.fileExistanceCache.remove(file);
/*      */     }
/*      */ 
/*  862 */     synchronized (this.fileHandlesCache) {
/*  863 */       FileCacheItem cacheItem = (FileCacheItem)this.fileHandlesCache.remove(file);
/*  864 */       if (cacheItem != null) {
/*  865 */         for (RandomAccessFile fileHandle : cacheItem.fileHandles) {
/*  866 */           if (fileHandle == null) continue;
/*      */           try {
/*  868 */             fileHandle.close();
/*      */           } catch (IOException e) {
/*  870 */             LOGGER.error(e.getMessage(), e);
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */ 
/*  876 */     if ((file.exists()) && 
/*  877 */       (!file.delete()))
/*  878 */       throw new DataCacheException(new StringBuilder().append("Cannot delete cache file [").append(file.getPath()).append("]").toString());
/*      */   }
/*      */ 
/*      */   public boolean isAnyDataChunkExistsAfter(Instrument instrument, Period period, OfferSide side, long from)
/*      */     throws DataCacheException
/*      */   {
/*  884 */     from = DataCacheUtils.getChunkStart(period, from);
/*  885 */     Unit chunkUnit = DataCacheUtils.getChunkLength(period);
/*  886 */     if (LOGGER.isDebugEnabled()) {
/*  887 */       DateFormat format = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss SSS");
/*  888 */       format.setTimeZone(TimeZone.getTimeZone("GMT"));
/*  889 */       LOGGER.debug(new StringBuilder().append("Checking if any data chunk exists after, instrument [").append(instrument).append("], period [").append(period).append("], side [").append(side).append("], from [").append(format.format(new Date(from))).append("]").toString());
/*      */     }
/*      */ 
/*  892 */     Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
/*  893 */     cal.setTimeInMillis(from);
/*  894 */     int month = cal.get(2);
/*  895 */     int day = cal.get(5);
/*  896 */     int year = cal.get(1);
/*  897 */     int hour = cal.get(11);
/*      */ 
/*  899 */     StringBuilder fileName = new StringBuilder(getCacheDirectory());
/*  900 */     fileName.append(instrument.name()).append(File.separatorChar);
/*      */ 
/*  902 */     File instrumentFile = new File(fileName.toString());
/*  903 */     if (instrumentFile.exists()) {
/*  904 */       File[] yearFiles = instrumentFile.listFiles(new FileFilter(year) {
/*      */         public boolean accept(File pathname) {
/*  906 */           if (pathname.isDirectory()) {
/*      */             try {
/*  908 */               int dirYear = Integer.parseInt(pathname.getName());
/*  909 */               if (dirYear >= this.val$year)
/*  910 */                 return true;
/*      */             }
/*      */             catch (NumberFormatException e)
/*      */             {
/*      */             }
/*      */           }
/*  916 */           return false;
/*      */         }
/*      */       });
/*  919 */       for (File yearFile : yearFiles) {
/*  920 */         File[] yearCandleFiles = yearFile.listFiles(new FileFilter() {
/*      */           public boolean accept(File pathname) {
/*  922 */             if (pathname.isFile()) {
/*  923 */               Matcher yearFilesMatcher = CacheManager.yearCandleFilesPattern.matcher(pathname.getName());
/*  924 */               return yearFilesMatcher.matches();
/*      */             }
/*  926 */             return false;
/*      */           }
/*      */         });
/*  929 */         if (yearCandleFiles.length > 0)
/*      */         {
/*  931 */           return true;
/*      */         }
/*  933 */         int dirYear = Integer.parseInt(yearFile.getName());
/*  934 */         File[] monthFiles = yearFile.listFiles(new FileFilter(dirYear, year, month, chunkUnit) {
/*      */           public boolean accept(File pathname) {
/*  936 */             if (pathname.isDirectory()) {
/*      */               try {
/*  938 */                 int dirMonth = Integer.parseInt(pathname.getName());
/*  939 */                 if ((this.val$dirYear > this.val$year) || (dirMonth > this.val$month) || ((dirMonth == this.val$month) && (this.val$chunkUnit.getInterval() < Unit.Month.getInterval())))
/*  940 */                   return true;
/*      */               }
/*      */               catch (NumberFormatException e)
/*      */               {
/*      */               }
/*      */             }
/*  946 */             return false;
/*      */           }
/*      */         });
/*  949 */         for (File monthFile : monthFiles) {
/*  950 */           File[] monthCandleFiles = monthFile.listFiles(new FileFilter() {
/*      */             public boolean accept(File pathname) {
/*  952 */               if (pathname.isFile()) {
/*  953 */                 Matcher monthFilesMatcher = CacheManager.monthCandleFilesPattern.matcher(pathname.getName());
/*  954 */                 return monthFilesMatcher.matches();
/*      */               }
/*  956 */               return false;
/*      */             }
/*      */           });
/*  959 */           if (monthCandleFiles.length > 0)
/*      */           {
/*  961 */             return true;
/*      */           }
/*  963 */           int dirMonth = Integer.parseInt(monthFile.getName());
/*  964 */           File[] dayFiles = monthFile.listFiles(new FileFilter(dirYear, year, dirMonth, month, day, chunkUnit) {
/*      */             public boolean accept(File pathname) {
/*  966 */               if (pathname.isDirectory()) {
/*      */                 try {
/*  968 */                   int dirDay = Integer.parseInt(pathname.getName());
/*  969 */                   if ((this.val$dirYear > this.val$year) || (this.val$dirMonth > this.val$month) || (dirDay > this.val$day) || ((dirDay == this.val$day) && (this.val$chunkUnit.getInterval() < Unit.Day.getInterval())))
/*  970 */                     return true;
/*      */                 }
/*      */                 catch (NumberFormatException e)
/*      */                 {
/*      */                 }
/*      */               }
/*  976 */               return false;
/*      */             }
/*      */           });
/*  979 */           for (File dayFile : dayFiles) {
/*  980 */             File[] dayCandleFiles = dayFile.listFiles(new FileFilter() {
/*      */               public boolean accept(File pathname) {
/*  982 */                 if (pathname.isFile()) {
/*  983 */                   Matcher dayFilesMatcher = CacheManager.dayCandleFilesPattern.matcher(pathname.getName());
/*  984 */                   return dayFilesMatcher.matches();
/*      */                 }
/*  986 */                 return false;
/*      */               }
/*      */             });
/*  989 */             if (dayCandleFiles.length > 0)
/*      */             {
/*  991 */               return true;
/*      */             }
/*  993 */             int dirDay = Integer.parseInt(dayFile.getName());
/*  994 */             File[] tickFiles = dayFile.listFiles(new FileFilter(dirYear, year, dirMonth, month, dirDay, day, hour, chunkUnit) {
/*      */               public boolean accept(File pathname) {
/*  996 */                 if (pathname.isFile()) {
/*      */                   try {
/*  998 */                     String tickFileName = pathname.getName();
/*  999 */                     Matcher tickFileMatcher = CacheManager.tickFilePattern.matcher(tickFileName);
/* 1000 */                     if (tickFileMatcher.matches()) {
/* 1001 */                       int fileHour = Integer.parseInt(tickFileMatcher.group(1));
/* 1002 */                       if ((this.val$dirYear > this.val$year) || (this.val$dirMonth > this.val$month) || (this.val$dirDay > this.val$day) || (fileHour > this.val$hour) || ((fileHour == this.val$hour) && (this.val$chunkUnit.getInterval() < Unit.Hour.getInterval())))
/* 1003 */                         return true;
/*      */                     }
/*      */                   }
/*      */                   catch (NumberFormatException e)
/*      */                   {
/*      */                   }
/*      */                 }
/* 1010 */                 return false;
/*      */               }
/*      */             });
/* 1013 */             if (tickFiles.length > 0)
/*      */             {
/* 1015 */               return true;
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/* 1021 */     return false;
/*      */   }
/*      */ 
/*      */   public void saveChunkInCache(Instrument instrument, Period period, OfferSide side, long from, Data[] data) throws DataCacheException {
/* 1025 */     saveChunkInCache(instrument, period, side, from, data, this.version);
/*      */   }
/*      */ 
/*      */   public void saveChunkInCache(Instrument instrument, Period period, OfferSide side, long from, Data[] data, int version) throws DataCacheException {
/* 1029 */     from = DataCacheUtils.getChunkStart(period, from);
/* 1030 */     long firstChunkCandle = period == Period.TICK ? from : DataCacheUtils.getFirstCandleInChunk(period, from);
/* 1031 */     if (LOGGER.isDebugEnabled()) {
/* 1032 */       DateFormat format = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss SSS");
/* 1033 */       format.setTimeZone(TimeZone.getTimeZone("GMT"));
/* 1034 */       LOGGER.debug(new StringBuilder().append("Saving chunk in cache, instrument [").append(instrument).append("], period [").append(period).append("], side [").append(side).append("], from [").append(format.format(new Date(from))).append("], data size [").append(data.length).append("]").toString());
/*      */     }
/*      */ 
/* 1038 */     if ((period != Period.TICK) && (data.length != 0) && (data.length != DataCacheUtils.getCandleCountInChunk(period, from))) {
/* 1039 */       throw new DataCacheException("Wrong chunk data");
/*      */     }
/*      */ 
/* 1042 */     File file = getChunkFile(instrument, period, side, from, version);
/* 1043 */     saveChunkInCache(instrument, period, file, firstChunkCandle, data, version, Compression.NONE);
/*      */   }
/*      */ 
/*      */   protected void saveChunkInCache(Instrument instrument, Period period, File file, long firstChunkCandle, Data[] data, int version, Compression compression) throws DataCacheException {
/* 1047 */     saveChunkInCache(instrument.getPipValue(), period, file, firstChunkCandle, data, version, compression);
/*      */   }
/*      */ 
/*      */   protected void saveChunkInCache(double instrumentPipValue, Period period, File file, long firstChunkCandle, Data[] data, int version, Compression compression) throws DataCacheException
/*      */   {
/* 1052 */     File fileDir = file.getParentFile();
/* 1053 */     if ((!fileDir.exists()) && (!fileDir.mkdirs()) && (!fileDir.exists())) {
/* 1054 */       throw new DataCacheException(new StringBuilder().append("Cannot create cache directory [").append(fileDir).append("]. File.exists() - [").append(fileDir.exists()).append("] File.mkdirs() - [").append(fileDir.mkdirs()).append("]").toString());
/*      */     }
/* 1056 */     if (chunkFileExists(file))
/*      */     {
/* 1058 */       return;
/*      */     }
/* 1060 */     ReentrantReadWriteLock rwLock = getLock(file);
/* 1061 */     ReentrantReadWriteLock.WriteLock wLock = rwLock.writeLock();
/*      */     try {
/* 1063 */       while (!wLock.tryLock(500L, TimeUnit.MILLISECONDS))
/*      */       {
/* 1066 */         if (chunkFileExists(file))
/*      */         {
/* 1068 */           return;
/*      */         }
/*      */       }
/*      */     }
/*      */     catch (InterruptedException e) {
/* 1073 */       return;
/*      */     }
/*      */     try {
/* 1076 */       if (chunkFileExists(file)) {
/*      */         return;
/*      */       }
/*      */       try
/*      */       {
/* 1082 */         File tempDir = DataCacheUtils.getChunkTempDirectory();
/*      */ 
/* 1084 */         if ((!tempDir.exists()) && (!tempDir.mkdirs()) && (!tempDir.exists())) {
/* 1085 */           throw new DataCacheException(new StringBuilder().append("Cannot create cache temp directory [").append(tempDir).append("]. File.exists() - [").append(tempDir.exists()).append("] File.mkdirs() - [").append(tempDir.mkdirs()).append("]").toString());
/*      */         }
/* 1087 */         File tempFile = File.createTempFile("JForex_cache_", ".bin", tempDir);
/* 1088 */         RandomAccessFile tempFileHandle = new RandomAccessFile(tempFile, "rw");
/*      */         try {
/* 1090 */           if (data.length > 0) {
/* 1091 */             tempFileHandle.setLength(0L);
/* 1092 */             tempFileHandle.seek(0L);
/* 1093 */             int bytesCount = data[0].getBytesCount(version);
/* 1094 */             byte[] buffer = new byte[bytesCount * 10];
/* 1095 */             int count = 0;
/* 1096 */             long expectedCandleTime = period == Period.TICK ? -9223372036854775808L : firstChunkCandle;
/* 1097 */             for (Data dataBlock : data) {
/* 1098 */               if (expectedCandleTime != -9223372036854775808L) {
/* 1099 */                 if (dataBlock.time != expectedCandleTime) {
/* 1100 */                   tempFileHandle.close();
/* 1101 */                   if (!tempFile.delete());
/* 1104 */                   DateFormat format = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss SSS");
/* 1105 */                   format.setTimeZone(TimeZone.getTimeZone("GMT"));
/* 1106 */                   throw new DataCacheException(new StringBuilder().append("Data for chunk file [").append(file.getPath()).append("] corrupted, expectedCandleTime [").append(format.format(new Date(expectedCandleTime))).append("], candle time [").append(format.format(new Date(dataBlock.time))).append("]").toString());
/*      */                 }
/*      */ 
/* 1110 */                 expectedCandleTime = DataCacheUtils.getNextCandleStart(period, expectedCandleTime);
/*      */               }
/*      */ 
/* 1113 */               dataBlock.toBytes(version, firstChunkCandle, instrumentPipValue, buffer, count * bytesCount);
/*      */ 
/* 1145 */               if (count == 9)
/*      */               {
/* 1147 */                 tempFileHandle.write(buffer);
/* 1148 */                 count = 0;
/*      */               } else {
/* 1150 */                 count++;
/*      */               }
/*      */             }
/* 1153 */             if (count > 0)
/* 1154 */               tempFileHandle.write(buffer, 0, count * bytesCount);
/*      */           }
/*      */         }
/*      */         finally {
/* 1158 */           tempFileHandle.close();
/*      */         }
/* 1160 */         if (data.length > 0) {
/* 1161 */           if (compression == null) {
/* 1162 */             compression = Compression.NONE;
/*      */           }
/* 1164 */           switch (11.$SwitchMap$com$dukascopy$charts$data$datacache$CacheManager$Compression[compression.ordinal()]) {
/*      */           case 1:
/* 1166 */             break;
/*      */           case 2:
/* 1168 */             File gzipTempFile = File.createTempFile("JForex_cache_", ".gzip", tempDir);
/* 1169 */             FileOutputStream fos = new FileOutputStream(gzipTempFile);
/* 1170 */             BufferedOutputStream bos = new BufferedOutputStream(fos);
/* 1171 */             GZIPOutputStream gos = new GZIPOutputStream(bos);
/*      */             try {
/* 1173 */               FileInputStream fis = new FileInputStream(tempFile);
/* 1174 */               BufferedInputStream bis = new BufferedInputStream(fis);
/*      */               try {
/* 1176 */                 byte[] buff = new byte[512];
/*      */                 int i;
/* 1178 */                 while ((i = bis.read(buff)) != -1)
/* 1179 */                   gos.write(buff, 0, i);
/*      */               }
/*      */               finally {
/* 1182 */                 bis.close();
/* 1183 */                 fis.close();
/*      */               }
/*      */             } finally {
/* 1186 */               gos.close();
/* 1187 */               bos.close();
/* 1188 */               fos.close();
/*      */             }
/* 1190 */             if (!tempFile.delete())
/*      */             {
/* 1192 */               throw new DataCacheException(new StringBuilder().append("Cannot delete temporary data cache file [").append(tempFile.getPath()).append("]").toString());
/*      */             }
/* 1194 */             tempFile = gzipTempFile;
/* 1195 */             break;
/*      */           case 3:
/* 1198 */             File zipTempFile = File.createTempFile("JForex_cache_", ".zip", tempDir);
/* 1199 */             FileOutputStream fos = new FileOutputStream(zipTempFile);
/* 1200 */             BufferedOutputStream bos = new BufferedOutputStream(fos);
/* 1201 */             ZipOutputStream zos = new ZipOutputStream(bos);
/*      */             try {
/* 1203 */               zos.setLevel(9);
/* 1204 */               ZipEntry zipEntry = new ZipEntry(file.getName());
/* 1205 */               zos.putNextEntry(zipEntry);
/* 1206 */               FileInputStream fis = new FileInputStream(tempFile);
/* 1207 */               BufferedInputStream bis = new BufferedInputStream(fis);
/*      */               try {
/* 1209 */                 byte[] buff = new byte[512];
/*      */                 int i;
/* 1211 */                 while ((i = bis.read(buff)) != -1)
/* 1212 */                   zos.write(buff, 0, i);
/*      */               }
/*      */               finally {
/* 1215 */                 bis.close();
/* 1216 */                 fis.close();
/*      */               }
/* 1218 */               zos.closeEntry();
/*      */             } finally {
/* 1220 */               zos.close();
/* 1221 */               bos.close();
/* 1222 */               fos.close();
/*      */             }
/* 1224 */             if (!tempFile.delete())
/*      */             {
/* 1226 */               throw new DataCacheException(new StringBuilder().append("Cannot delete temporary data cache file [").append(tempFile.getPath()).append("]").toString());
/*      */             }
/* 1228 */             tempFile = zipTempFile;
/* 1229 */             break;
/*      */           case 4:
/* 1232 */             File zipTempFile = File.createTempFile("JForex_cache_", ".7z", tempDir);
/* 1233 */             Encoder encoder = new Encoder();
/* 1234 */             FileOutputStream fos = new FileOutputStream(zipTempFile);
/* 1235 */             BufferedOutputStream bos = new BufferedOutputStream(fos);
/*      */             try {
/* 1237 */               FileInputStream fis = new FileInputStream(tempFile);
/* 1238 */               BufferedInputStream bis = new BufferedInputStream(fis);
/*      */               try {
/* 1240 */                 encoder.WriteCoderProperties(bos);
/* 1241 */                 for (int i = 0; i < 8; i++) {
/* 1242 */                   bos.write((int)(tempFile.length() >>> 8 * i) & 0xFF);
/*      */                 }
/* 1244 */                 encoder.Code(bis, bos, -1L, -1L, null);
/*      */               } finally {
/* 1246 */                 bis.close();
/* 1247 */                 fis.close();
/*      */               }
/*      */             } finally {
/* 1250 */               bos.close();
/* 1251 */               fos.close();
/*      */             }
/* 1253 */             if (!tempFile.delete())
/*      */             {
/* 1255 */               throw new DataCacheException(new StringBuilder().append("Cannot delete temporary data cache file [").append(tempFile.getPath()).append("]").toString());
/*      */             }
/* 1257 */             tempFile = zipTempFile;
/* 1258 */             break;
/*      */           }
/*      */         }
/*      */ 
/* 1262 */         if (!tempFile.renameTo(file))
/*      */         {
/* 1264 */           if (!file.exists())
/*      */           {
/* 1266 */             throw new DataCacheException(new StringBuilder().append("Cannot rename temporary file [").append(tempFile).append("] to data cache file [").append(file).append("]").toString());
/*      */           }
/*      */         }
/*      */ 
/*      */       }
/*      */       catch (IOException e)
/*      */       {
/* 1273 */         throw new DataCacheException(e);
/*      */       }
/*      */     } finally {
/* 1276 */       wLock.unlock();
/*      */     }
/*      */   }
/*      */ 
/*      */   protected ReentrantReadWriteLock getLock(File file)
/*      */   {
/*      */     ReentrantReadWriteLock lock;
/* 1282 */     synchronized (this.fileLocks) {
/* 1283 */       lock = (ReentrantReadWriteLock)this.fileLocks.get(file);
/* 1284 */       if (lock == null) {
/* 1285 */         lock = new ReentrantReadWriteLock();
/* 1286 */         this.fileLocks.put(file, lock);
/*      */       }
/*      */     }
/* 1289 */     return lock;
/*      */   }
/*      */ 
/*      */   protected String getCacheDirectory() {
/* 1293 */     return FilePathManager.getInstance().getCacheDirectory();
/*      */   }
/*      */ 
/*      */   protected File getChunkFile(Instrument instrument, Period period, OfferSide side, long from) throws DataCacheException {
/* 1297 */     return getChunkFile(instrument, period, side, from, this.version);
/*      */   }
/*      */ 
/*      */   protected static File getChunkFile(Instrument instrument, Period period, OfferSide side, long from, int version) throws DataCacheException {
/* 1301 */     return DataCacheUtils.getChunkFile(instrument, period, side, from, version);
/*      */   }
/*      */ 
/*      */   protected void readTicksFromChunkFile(Instrument instrument, File file, long fileStartTime, long from, long to, LiveFeedListener listener, boolean blocking, IFeedCommissionManager feedCommissionManager)
/*      */     throws DataCacheException
/*      */   {
/* 1314 */     if (LOGGER.isTraceEnabled()) {
/* 1315 */       LOGGER.trace(new StringBuilder().append("Reading ticks from chunk file [").append(file.getPath()).append("]").toString());
/*      */     }
/* 1317 */     ReentrantReadWriteLock rwLock = getLock(file);
/* 1318 */     ReentrantReadWriteLock.ReadLock rLock = rwLock.readLock();
/* 1319 */     rLock.lock();
/*      */     try {
/*      */       try {
/* 1322 */         RandomAccessFile fileHandle = getChunkFileHandle(file, !blocking);
/*      */         try {
/*      */           try {
/* 1325 */             readTicksFromFile(instrument, file, fileStartTime, from, to, listener, fileHandle, feedCommissionManager);
/*      */           }
/*      */           catch (DataCacheException e) {
/* 1328 */             rLock.unlock();
/*      */ 
/* 1330 */             ReentrantReadWriteLock.WriteLock wLock = rwLock.writeLock();
/* 1331 */             wLock.lock();
/*      */             try {
/* 1333 */               synchronized (this.fileHandlesCache) {
/* 1334 */                 FileCacheItem cacheItem = (FileCacheItem)this.fileHandlesCache.remove(file);
/* 1335 */                 if (cacheItem != null) {
/* 1336 */                   for (RandomAccessFile cachedFileHandle : cacheItem.fileHandles) {
/* 1337 */                     if (cachedFileHandle == null) continue;
/*      */                     try {
/* 1339 */                       cachedFileHandle.close();
/*      */                     } catch (IOException ex) {
/* 1341 */                       LOGGER.error(ex.getMessage(), ex);
/*      */                     }
/*      */                   }
/*      */                 }
/*      */               }
/*      */ 
/* 1347 */               fileHandle.close();
/* 1348 */               if (!file.delete())
/* 1349 */                 LOGGER.error(new StringBuilder().append("Data cache corrupted, cannot delete file [").append(file.getPath()).append("]").toString());
/*      */               else {
/* 1351 */                 synchronized (this.fileExistanceCache) {
/* 1352 */                   this.fileExistanceCache.remove(file);
/*      */                 }
/*      */               }
/* 1355 */               throw e;
/*      */             } finally {
/* 1357 */               wLock.unlock();
/* 1358 */               rLock.lock();
/*      */             }
/*      */           }
/*      */         } finally {
/* 1362 */           returnChunkFileHandle(file, fileHandle);
/*      */         }
/*      */       } catch (IOException e) {
/* 1365 */         throw new DataCacheException(e);
/*      */       }
/*      */     } finally {
/* 1368 */       rLock.unlock();
/*      */     }
/*      */   }
/*      */ 
/*      */   protected void readTicksFromFile(Instrument instrument, File file, long fileStartTime, long from, long to, LiveFeedListener listener, RandomAccessFile fileHandle, IFeedCommissionManager feedCommissionManager)
/*      */     throws DataCacheException, IOException
/*      */   {
/* 1382 */     long startPosition = 0L;
/* 1383 */     if (from > fileStartTime)
/*      */     {
/* 1385 */       startPosition = findStartPositionInTicksFile(fileStartTime, fileHandle, from); } 
/*      */ fileHandle.seek(startPosition);
/* 1389 */     int tickDataLength = TickData.getLength(this.version);
/* 1390 */     byte[] buff = new byte[8192 / tickDataLength * tickDataLength];
/* 1391 */     boolean feedCommissionExists = feedCommissionManager.hasCommission(instrument);
/*      */ 
/* 1393 */     long previousTime = from;
/*      */     int i;
/*      */     do { int readBytes = 0;
/* 1396 */       while (((i = fileHandle.read(buff, readBytes, buff.length - readBytes)) > -1) && (readBytes < buff.length)) {
/* 1397 */         readBytes += i;
/*      */       }
/* 1399 */       TickData tickData = new TickData();
/* 1400 */       for (int j = 0; j < readBytes; j += tickDataLength)
/* 1401 */         if (j + tickDataLength <= readBytes) {
/* 1402 */           tickData.fromBytes(this.version, fileStartTime, instrument.getPipValue(), buff, j);
/* 1403 */           if (tickData.time == -9223372036854775808L)
/*      */           {
/*      */             continue;
/*      */           }
/* 1407 */           if (tickData.time < previousTime) {
/* 1408 */             DateFormat format = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss SSS");
/* 1409 */             format.setTimeZone(TimeZone.getTimeZone("GMT"));
/* 1410 */             throw new DataCacheException(new StringBuilder().append("Wrong data from file [").append(file.getPath()).append("], read tick with time [").append(format.format(new Date(tickData.time))).append("], previous tick time [").append(format.format(new Date(previousTime))).append("]").toString());
/*      */           }
/*      */ 
/* 1413 */           if (tickData.time <= to) {
/* 1414 */             if (feedCommissionExists) {
/* 1415 */               tickData = feedCommissionManager.applyFeedCommissionToTick(instrument, tickData);
/*      */             }
/*      */ 
/* 1418 */             listener.newTick(instrument, tickData.time, tickData.ask, tickData.bid, tickData.askVol, tickData.bidVol);
/* 1419 */             previousTime = tickData.time;
/*      */           }
/*      */           else {
/* 1422 */             return;
/*      */           }
/*      */         }
/*      */     }
/* 1426 */     while (i > -1);
/*      */   }
/*      */ 
/*      */   protected boolean readTicksFromEndFromFile(Instrument instrument, File file, long fileStartTime, long from, long to, LiveFeedListener listener, LoadingProgressListener loadingProgress, RandomAccessFile fileHandle, IFeedCommissionManager feedCommissionManager)
/*      */     throws DataCacheException, IOException
/*      */   {
/* 1440 */     long startPosition = 0L;
/* 1441 */     if (to > fileStartTime)
/*      */     {
/* 1443 */       startPosition = findStartPositionInTicksFile(fileStartTime, fileHandle, to);
/*      */     }
/* 1445 */     int tickDataLength = TickData.getLength(this.version);
/* 1446 */     if (startPosition + tickDataLength > fileHandle.length()) {
/* 1447 */       return false;
/*      */     }
/*      */ 
/* 1450 */     fileHandle.seek(0L);
/* 1451 */     byte[] buff = new byte[(int)startPosition + tickDataLength];
/*      */ 
/* 1453 */     long previousTime = to;
/* 1454 */     int readBytes = 0;
/*      */     int i;
/* 1455 */     while (((i = fileHandle.read(buff, readBytes, buff.length - readBytes)) > -1) && (readBytes < buff.length)) {
/* 1456 */       readBytes += i;
/*      */     }
/* 1458 */     TickData tickData = new TickData();
/* 1459 */     boolean hasFeedCommission = feedCommissionManager.hasCommission(instrument);
/*      */ 
/* 1461 */     readBytes -= readBytes % tickDataLength;
/* 1462 */     for (int j = readBytes - tickDataLength; j >= 0; j -= tickDataLength) {
/* 1463 */       tickData.fromBytes(this.version, fileStartTime, instrument.getPipValue(), buff, j);
/* 1464 */       if (tickData.time == -9223372036854775808L)
/*      */       {
/* 1466 */         return true;
/*      */       }
/* 1468 */       if (tickData.time > to)
/*      */       {
/*      */         continue;
/*      */       }
/* 1472 */       if (tickData.time > previousTime) {
/* 1473 */         DateFormat format = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss SSS");
/* 1474 */         format.setTimeZone(TimeZone.getTimeZone("GMT"));
/* 1475 */         throw new DataCacheException(new StringBuilder().append("Wrong data from file [").append(file.getPath()).append("], read tick with time [").append(format.format(new Date(tickData.time))).append("], previous tick time [").append(format.format(new Date(previousTime))).append("] while reading from end of the file").toString());
/*      */       }
/*      */ 
/* 1479 */       if ((tickData.time >= from) && ((loadingProgress == null) || (!loadingProgress.stopJob()))) {
/* 1480 */         if (hasFeedCommission) {
/* 1481 */           feedCommissionManager.applyFeedCommissionToTick(instrument, tickData);
/*      */         }
/* 1483 */         listener.newTick(instrument, tickData.time, tickData.ask, tickData.bid, tickData.askVol, tickData.bidVol);
/* 1484 */         previousTime = tickData.time;
/*      */       }
/*      */       else {
/* 1487 */         return false;
/*      */       }
/*      */     }
/* 1490 */     return false;
/*      */   }
/*      */ 
/*      */   protected boolean readCandlesFromEndFromFile(File file, RandomAccessFile fileHandle, Instrument instrument, Period period, OfferSide side, long fileStartTime, long from, long to, LiveFeedListener listener, LoadingProgressListener loadingProgress, IFeedCommissionManager feedCommissionManager)
/*      */     throws IOException, DataCacheException
/*      */   {
/* 1507 */     File fileDir = file.getParentFile();
/* 1508 */     if ((!fileDir.exists()) && (!fileDir.mkdirs()) && (!fileDir.exists())) {
/* 1509 */       throw new DataCacheException(new StringBuilder().append("Cannot create cache directory [").append(fileDir).append("]. File.exists() - [").append(fileDir.exists()).append("] File.mkdirs() - [").append(fileDir.mkdirs()).append("]").toString());
/*      */     }
/* 1511 */     int startPosition = 0;
/* 1512 */     long candleTime = DataCacheUtils.getFirstCandleInChunkFast(period, fileStartTime);
/* 1513 */     long firstChunkCandle = candleTime;
/* 1514 */     int candleDataLength = CandleData.getLength(this.version);
/* 1515 */     if (to > candleTime)
/*      */     {
/* 1517 */       startPosition = DataCacheUtils.getCandlesCountBetween(period, candleTime, DataCacheUtils.getPreviousCandleStart(period, to)) * candleDataLength;
/*      */ 
/* 1519 */       candleTime = to;
/*      */     }
/* 1521 */     if (startPosition + candleDataLength > fileHandle.length()) {
/* 1522 */       return false;
/*      */     }
/*      */ 
/* 1525 */     byte[] buff = new byte[candleDataLength];
/* 1526 */     boolean hasFeedCommission = feedCommissionManager.hasCommission(instrument);
/*      */ 
/* 1528 */     while (startPosition >= 0) {
/* 1529 */       fileHandle.seek(startPosition);
/* 1530 */       int readBytes = 0;
/*      */       int i;
/* 1531 */       while (((i = fileHandle.read(buff, readBytes, buff.length - readBytes)) > -1) && (readBytes < buff.length)) {
/* 1532 */         readBytes += i;
/*      */       }
/* 1534 */       if (readBytes < candleDataLength) {
/* 1535 */         return false;
/*      */       }
/* 1537 */       CandleData candleData = new CandleData();
/* 1538 */       candleData.fromBytes(this.version, firstChunkCandle, instrument.getPipValue(), buff, 0);
/* 1539 */       if (candleData.time != candleTime)
/*      */       {
/* 1541 */         DateFormat format = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss SSS");
/* 1542 */         format.setTimeZone(TimeZone.getTimeZone("GMT"));
/* 1543 */         String errorMessage = new StringBuilder().append("Data cache file corrupted, candle time [").append(format.format(new Date(candleData.time))).append("],").append("expected candle time [").append(format.format(new Date(candleTime))).append("], file [").append(file.getPath()).append("], position in file [").append(startPosition).append("]").toString();
/*      */ 
/* 1546 */         throw new DataCacheException(errorMessage);
/*      */       }
/* 1548 */       if ((candleData.time >= from) && ((loadingProgress == null) || (!loadingProgress.stopJob()))) {
/* 1549 */         if (hasFeedCommission) {
/* 1550 */           candleData = feedCommissionManager.applyFeedCommissionToCandle(instrument, side, candleData);
/*      */         }
/* 1552 */         listener.newCandle(instrument, period, side, candleTime, candleData.open, candleData.close, candleData.low, candleData.high, candleData.vol);
/*      */       }
/*      */       else {
/* 1555 */         return false;
/*      */       }
/* 1557 */       candleTime = DataCacheUtils.getPreviousCandleStart(period, candleTime);
/* 1558 */       startPosition -= candleDataLength;
/*      */     }
/* 1560 */     return true;
/*      */   }
/*      */ 
/*      */   protected long findStartPositionInTicksFile(long fileStartTime, RandomAccessFile fileHandle, long time) throws DataCacheException, IOException {
/* 1564 */     int timeSize = this.version <= 4 ? 8 : 4;
/* 1565 */     byte[] longBuff = new byte[timeSize];
/* 1566 */     int tickDataLength = TickData.getLength(this.version);
/* 1567 */     long size = (fileHandle.length() - fileHandle.length() % tickDataLength) / tickDataLength;
/* 1568 */     if (size == 0L)
/* 1569 */       return size * tickDataLength;
/* 1570 */     if (size == 1L) {
/* 1571 */       fileHandle.seek(0L);
/*      */ 
/* 1573 */       int j = 0;
/*      */       int i;
/* 1574 */       while (((i = fileHandle.read(longBuff, j, longBuff.length - j)) > -1) && (j < longBuff.length)) {
/* 1575 */         j += i;
/*      */       }
/*      */ 
/* 1578 */       if (j != timeSize)
/*      */       {
/* 1580 */         throw new DataCacheException("Data cache file corrupted");
/*      */       }
/*      */       long elemTime;
/*      */       long elemTime;
/* 1582 */       if (this.version <= 4)
/* 1583 */         elemTime = Data.getLong(longBuff, 0);
/*      */       else {
/* 1585 */         elemTime = fileStartTime + Data.getInt(longBuff, 0);
/*      */       }
/*      */ 
/* 1588 */       if (elemTime == -9223372036854775808L)
/*      */       {
/* 1590 */         return size * tickDataLength;
/* 1591 */       }if (elemTime >= time) {
/* 1592 */         return 0L;
/*      */       }
/* 1594 */       return size * tickDataLength;
/*      */     }
/*      */ 
/* 1599 */     fileHandle.seek(0L);
/*      */ 
/* 1601 */     int j = 0;
/*      */     int i;
/* 1602 */     while (((i = fileHandle.read(longBuff, j, longBuff.length - j)) > -1) && (j < longBuff.length)) {
/* 1603 */       j += i;
/*      */     }
/*      */ 
/* 1606 */     if (j != timeSize)
/*      */     {
/* 1608 */       throw new DataCacheException("Data cache file corrupted");
/*      */     }
/*      */     long elemTime;
/*      */     long elemTime;
/* 1610 */     if (this.version <= 4)
/* 1611 */       elemTime = Data.getLong(longBuff, 0);
/*      */     else {
/* 1613 */       elemTime = fileStartTime + Data.getInt(longBuff, 0);
/*      */     }
/*      */ 
/* 1617 */     int low = elemTime == -9223372036854775808L ? 1 : 0;
/* 1618 */     int high = (int)(size - 1L);
/* 1619 */     while (low <= high) {
/* 1620 */       int mid = low + high >>> 1;
/* 1621 */       fileHandle.seek(mid * tickDataLength);
/* 1622 */       j = 0;
/* 1623 */       while (((i = fileHandle.read(longBuff, j, longBuff.length - j)) > -1) && (j < longBuff.length)) {
/* 1624 */         j += i;
/*      */       }
/*      */ 
/* 1627 */       if (j != timeSize)
/*      */       {
/* 1629 */         throw new DataCacheException("Data cache file corrupted");
/*      */       }
/*      */       long midVal;
/*      */       long midVal;
/* 1631 */       if (this.version <= 4)
/* 1632 */         midVal = Data.getLong(longBuff, 0);
/*      */       else {
/* 1634 */         midVal = fileStartTime + Data.getInt(longBuff, 0);
/*      */       }
/*      */ 
/* 1638 */       if (midVal < time)
/* 1639 */         low = mid + 1;
/* 1640 */       else if (midVal > time)
/* 1641 */         high = mid - 1;
/*      */       else {
/* 1643 */         return mid * tickDataLength;
/*      */       }
/*      */     }
/* 1646 */     return low * tickDataLength;
/*      */   }
/*      */ 
/*      */   protected boolean assertionsEnabled() {
/* 1650 */     boolean b = false;
/* 1651 */     assert ((b = 1) != 0);
/* 1652 */     return b;
/*      */   }
/*      */ 
/*      */   static
/*      */   {
/*   69 */     LOGGER = LoggerFactory.getLogger(CacheManager.class);
/*      */ 
/*   74 */     tickFilePattern = Pattern.compile("(\\d\\d)h_ticks\\.bi.");
/*   75 */     yearCandleFilesPattern = Pattern.compile("(BID|ASK)_candles_(month|week|day|hour)_\\d+\\.bi.");
/*   76 */     monthCandleFilesPattern = Pattern.compile("(BID|ASK)_candles_(hour|min)_\\d+\\.bi.");
/*   77 */     dayCandleFilesPattern = Pattern.compile("(BID|ASK)_candles_(min|sec)_\\d+\\.bi.");
/*   78 */     lockFilesPattern = Pattern.compile("lock\\d+\\.lck");
/*   79 */     tempFilesPattern = Pattern.compile("JForex_cache_.+\\.bi.");
/*      */   }
/*      */ 
/*      */   protected static class FileCacheItem
/*      */   {
/* 1656 */     public boolean[] taken = new boolean[4];
/* 1657 */     public RandomAccessFile[] fileHandles = new RandomAccessFile[4];
/*      */   }
/*      */ 
/*      */   public static enum Compression
/*      */   {
/*   67 */     NONE, GZIP, ZIP, ZIP7;
/*      */   }
/*      */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.charts.data.datacache.CacheManager
 * JD-Core Version:    0.6.0
 */