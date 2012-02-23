/*     */ package com.dukascopy.dds2.greed.connector.impl;
/*     */ 
/*     */ import com.dukascopy.api.connector.ISettings;
/*     */ import java.io.ByteArrayInputStream;
/*     */ import java.io.ByteArrayOutputStream;
/*     */ import java.io.File;
/*     */ import java.io.IOException;
/*     */ import java.io.PrintStream;
/*     */ import java.io.RandomAccessFile;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.nio.channels.FileChannel;
/*     */ import java.nio.channels.FileLock;
/*     */ import java.util.Properties;
/*     */ 
/*     */ public class HighAvailabilityStore
/*     */   implements ISettings
/*     */ {
/*  38 */   private String lockFileName = null;
/*     */ 
/*  40 */   private FileLock lock = null;
/*     */ 
/*     */   public HighAvailabilityStore(String lockFile)
/*     */   {
/*  44 */     this.lockFileName = lockFile;
/*     */   }
/*     */ 
/*     */   private String lock()
/*     */   {
/*     */     try
/*     */     {
/*  51 */       File file = new File(this.lockFileName);
/*  52 */       FileChannel channel = new RandomAccessFile(file, "rw").getChannel();
/*     */ 
/*  56 */       int attempts = 0;
/*     */       while (true)
/*     */         try {
/*  59 */           attempts++;
/*  60 */           this.lock = channel.tryLock();
/*  61 */           if (this.lock != null) {
/*     */             break;
/*     */           }
/*  64 */           Thread.sleep(100L);
/*  65 */           if (attempts > 100) {
/*  66 */             System.out.println("Error Unabel to lock file " + this.lockFileName);
/*  67 */             return null;
/*     */           }
/*     */ 
/*  71 */           continue;
/*     */         } catch (Exception e) {
/*     */         }
/*  74 */       ByteBuffer byteBuffer = ByteBuffer.allocate((int)file.length());
/*  75 */       channel.read(byteBuffer);
/*     */ 
/*  77 */       return new String(byteBuffer.array());
/*     */     }
/*     */     catch (Exception e) {
/*  80 */       e.printStackTrace();
/*     */     }
/*  82 */     return null;
/*     */   }
/*     */ 
/*     */   private void unlock(String string)
/*     */   {
/*     */     try
/*     */     {
/*  89 */       FileChannel channel = this.lock.channel();
/*     */ 
/*  91 */       if (string != null) {
/*  92 */         channel.position(0L);
/*  93 */         ByteBuffer byteBuffer = ByteBuffer.wrap(string.getBytes());
/*  94 */         channel.write(byteBuffer);
/*     */       }
/*     */ 
/*  97 */       this.lock.release();
/*     */ 
/*  99 */       channel.close();
/* 100 */       this.lock = null;
/*     */     } catch (Exception e) {
/* 102 */       e.printStackTrace();
/*     */     }
/*     */   }
/*     */ 
/*     */   public void clear()
/*     */   {
/* 110 */     Properties properties = new Properties();
/*     */ 
/* 112 */     ByteArrayOutputStream baos = new ByteArrayOutputStream();
/*     */     try {
/* 114 */       properties.store(baos, getClass().getSimpleName());
/*     */     } catch (IOException e) {
/* 116 */       e.printStackTrace();
/*     */     }
/*     */ 
/* 119 */     unlock(new String(baos.toByteArray()));
/*     */   }
/*     */ 
/*     */   public String get(String key)
/*     */   {
/* 125 */     String props = lock();
/* 126 */     unlock(null);
/*     */ 
/* 128 */     Properties properties = new Properties();
/*     */     try
/*     */     {
/* 131 */       properties.load(new ByteArrayInputStream(props.getBytes()));
/*     */     } catch (IOException e) {
/* 133 */       e.printStackTrace();
/*     */     }
/* 135 */     String rc = properties.getProperty(key);
/* 136 */     if (rc != null) {
/* 137 */       rc.trim();
/*     */     }
/* 139 */     return rc;
/*     */   }
/*     */ 
/*     */   public String put(String key, String newValue)
/*     */   {
/* 145 */     String oldValue = null;
/* 146 */     if (newValue == null) {
/* 147 */       throw new NullPointerException();
/*     */     }
/*     */ 
/* 150 */     String props = lock();
/*     */ 
/* 152 */     Properties properties = new Properties();
/*     */     try
/*     */     {
/* 155 */       properties.load(new ByteArrayInputStream(props.getBytes()));
/*     */     } catch (IOException e) {
/* 157 */       e.printStackTrace();
/*     */     }
/*     */ 
/* 160 */     oldValue = (String)properties.setProperty(key, newValue.trim());
/*     */ 
/* 162 */     ByteArrayOutputStream baos = new ByteArrayOutputStream();
/*     */     try {
/* 164 */       properties.store(baos, getClass().getSimpleName());
/*     */     } catch (IOException e) {
/* 166 */       e.printStackTrace();
/*     */     }
/*     */ 
/* 169 */     unlock(new String(baos.toByteArray()));
/*     */ 
/* 171 */     return oldValue;
/*     */   }
/*     */ 
/*     */   public String remove(String key)
/*     */   {
/* 176 */     String oldValue = null;
/*     */ 
/* 178 */     String props = lock();
/*     */ 
/* 180 */     Properties properties = new Properties();
/*     */     try
/*     */     {
/* 183 */       properties.load(new ByteArrayInputStream(props.getBytes()));
/*     */     } catch (IOException e) {
/* 185 */       e.printStackTrace();
/*     */     }
/*     */ 
/* 188 */     oldValue = (String)properties.remove(key);
/*     */ 
/* 190 */     unlock(properties.toString());
/*     */ 
/* 192 */     return oldValue;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Connector-1.1.49.jar
 * Qualified Name:     com.dukascopy.dds2.greed.connector.impl.HighAvailabilityStore
 * JD-Core Version:    0.6.0
 */