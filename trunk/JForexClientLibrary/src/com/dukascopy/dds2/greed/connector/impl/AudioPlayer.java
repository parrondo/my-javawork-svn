/*     */ package com.dukascopy.dds2.greed.connector.impl;
/*     */ 
/*     */ import java.io.File;
/*     */ import java.io.IOException;
/*     */ import javax.sound.sampled.AudioFormat;
/*     */ import javax.sound.sampled.AudioInputStream;
/*     */ import javax.sound.sampled.AudioSystem;
/*     */ import javax.sound.sampled.DataLine.Info;
/*     */ import javax.sound.sampled.LineUnavailableException;
/*     */ import javax.sound.sampled.SourceDataLine;
/*     */ 
/*     */ class AudioPlayer
/*     */ {
/*     */   private static final int EXTERNAL_BUFFER_SIZE = 128000;
/*     */ 
/*     */   public static void main(String[] args)
/*     */   {
/*  41 */     AudioPlayer player = new AudioPlayer();
/*  42 */     player.play("test.mp3");
/*     */   }
/*     */ 
/*     */   public boolean play(String strFilename)
/*     */   {
/*  47 */     boolean rc = false;
/*     */ 
/*  52 */     File soundFile = new File(strFilename);
/*  53 */     if ((soundFile.exists()) && (soundFile.isFile()))
/*     */     {
/*  56 */       if (soundFile.exists()) {
/*  57 */         AudioInputStream audioInputStream = null;
/*     */         try {
/*  59 */           audioInputStream = AudioSystem.getAudioInputStream(soundFile);
/*     */         }
/*     */         catch (Exception e) {
/*  62 */           e.printStackTrace();
/*     */         }
/*     */ 
/*  65 */         if (audioInputStream == null) {
/*  66 */           return rc;
/*     */         }
/*     */ 
/*  69 */         AudioFormat audioFormat = audioInputStream.getFormat();
/*     */ 
/*  71 */         SourceDataLine line = null;
/*  72 */         DataLine.Info info = new DataLine.Info(SourceDataLine.class, audioFormat);
/*     */         try {
/*  74 */           line = (SourceDataLine)AudioSystem.getLine(info);
/*     */ 
/*  76 */           line.open(audioFormat);
/*     */ 
/*  78 */           line.getControls();
/*     */         }
/*     */         catch (LineUnavailableException e) {
/*  81 */           e.printStackTrace();
/*     */         }
/*     */         catch (Exception e) {
/*  84 */           e.printStackTrace();
/*     */         }
/*     */ 
/*  88 */         line.start();
/*     */ 
/*  90 */         int nBytesRead = 0;
/*  91 */         byte[] abData = new byte[128000];
/*     */         int nBytesWritten;
/*  92 */         while (nBytesRead != -1) {
/*     */           try {
/*  94 */             nBytesRead = audioInputStream.read(abData, 0, abData.length);
/*     */           } catch (IOException e) {
/*  96 */             e.printStackTrace();
/*     */           }
/*  98 */           if (nBytesRead >= 0) {
/*  99 */             nBytesWritten = line.write(abData, 0, nBytesRead);
/*     */           }
/*     */         }
/*     */ 
/* 103 */         line.drain();
/*     */ 
/* 105 */         line.close();
/* 106 */         rc = true;
/*     */       }
/*     */     }
/*     */ 
/* 110 */     return rc;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\DDS2-Connector-1.1.49.jar
 * Qualified Name:     com.dukascopy.dds2.greed.connector.impl.AudioPlayer
 * JD-Core Version:    0.6.0
 */