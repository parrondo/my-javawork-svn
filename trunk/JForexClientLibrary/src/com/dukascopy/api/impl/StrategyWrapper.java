/*     */ package com.dukascopy.api.impl;
/*     */ 
/*     */ import com.dukascopy.api.Configurable;
/*     */ import com.dukascopy.api.IStrategy;
/*     */ import com.dukascopy.api.Library;
/*     */ import com.dukascopy.api.RequiresFullAccess;
/*     */ import com.dukascopy.dds2.greed.agent.compiler.JFXPack;
/*     */ import com.dukascopy.dds2.greed.util.FullAccessDisclaimerProvider;
/*     */ import com.dukascopy.dds2.greed.util.IFullAccessDisclaimer;
/*     */ import java.io.File;
/*     */ import java.lang.annotation.Annotation;
/*     */ import java.lang.reflect.Field;
/*     */ 
/*     */ public class StrategyWrapper extends ServiceWrapper
/*     */ {
/*  19 */   protected IStrategy strategy = null;
/*  20 */   protected JFXPack pack = null;
/*     */ 
/*     */   public String getName()
/*     */   {
/*  24 */     if (this.isNewUnsaved) {
/*  25 */       return "*Strategy" + this.newFileIndex;
/*     */     }
/*  27 */     if (this.srcFile != null) {
/*  28 */       if (this.isModified) {
/*  29 */         return "*" + this.srcFile.getName();
/*     */       }
/*  31 */       return this.srcFile.getName();
/*     */     }
/*     */ 
/*  34 */     if (this.binFile != null) {
/*  35 */       return this.binFile.getName();
/*     */     }
/*  37 */     return null;
/*     */   }
/*     */ 
/*     */   public IStrategy getStrategy(boolean hasToReload) throws Exception {
/*  41 */     if ((this.strategy == null) || (hasToReload)) {
/*  42 */       this.pack = JFXPack.loadFromPack(getBinaryFile());
/*  43 */       if (this.pack != null) {
/*  44 */         if (this.pack.isFullAccessRequested()) {
/*  45 */           FullAccessDisclaimerProvider.getDisclaimer().showDialog(this.pack);
/*     */         }
/*  47 */         if (this.pack != null) {
/*  48 */           this.strategy = ((IStrategy)this.pack.getTarget());
/*     */         }
/*     */       }
/*     */     }
/*  52 */     return this.strategy;
/*     */   }
/*     */ 
/*     */   public IStrategy getStrategy(boolean hasToReload, boolean fullAccessGranted) throws Exception {
/*  56 */     if ((this.strategy == null) || (hasToReload)) {
/*  57 */       this.pack = JFXPack.loadFromPack(getBinaryFile());
/*  58 */       if (this.pack != null) {
/*  59 */         if ((this.pack.isFullAccessRequested()) && 
/*  60 */           (fullAccessGranted)) {
/*  61 */           this.pack.setFullAccess(fullAccessGranted);
/*     */         }
/*     */ 
/*  64 */         if (this.pack != null) {
/*  65 */           this.strategy = ((IStrategy)this.pack.getTarget());
/*     */         }
/*     */       }
/*     */     }
/*  69 */     return this.strategy;
/*     */   }
/*     */ 
/*     */   public IStrategy getStrategy() throws Exception {
/*  73 */     if (this.strategy == null) {
/*  74 */       throw new Exception("Init strategy first");
/*     */     }
/*  76 */     return this.strategy;
/*     */   }
/*     */ 
/*     */   public String getStrategyKey() {
/*  80 */     return getName() + " " + this.pack.getMD5HexString();
/*     */   }
/*     */ 
/*     */   public boolean isFullAccessGranted() {
/*  84 */     return (this.strategy != null) && (this.pack != null) && (this.pack.isFullAccess());
/*     */   }
/*     */ 
/*     */   public ClassLoader getClassLoader() {
/*  88 */     return this.pack.getClassLoader();
/*     */   }
/*     */ 
/*     */   public boolean isAnnotated() throws Exception {
/*  92 */     if (this.strategy != null) {
/*  93 */       IStrategy strategy = getStrategy();
/*     */ 
/*  95 */       if (strategy != null) {
/*  96 */         for (Field field : strategy.getClass().getFields()) {
/*  97 */           for (Annotation annotation : field.getAnnotations()) {
/*  98 */             if ((annotation.annotationType().equals(Configurable.class)) || (annotation.annotationType().equals(Library.class)) || (annotation.annotationType().equals(RequiresFullAccess.class)))
/*     */             {
/* 101 */               return true;
/*     */             }
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/* 107 */     return false;
/*     */   }
/*     */ 
/*     */   public boolean isFullAccessRequested() throws Exception {
/* 111 */     File binaryFile = getBinaryFile();
/* 112 */     if (binaryFile != null) {
/* 113 */       return JFXPack.loadFromPack(binaryFile).isFullAccessRequested();
/*     */     }
/* 115 */     return false;
/*     */   }
/*     */ 
/*     */   public static String representError(Object str, Throwable ex)
/*     */   {
/* 120 */     Throwable throwable = null;
/* 121 */     if (ex.getCause() != null)
/* 122 */       throwable = ex.getCause();
/*     */     else {
/* 124 */       throwable = ex;
/*     */     }
/*     */ 
/* 127 */     String msg = throwable.toString();
/*     */ 
/* 129 */     StackTraceElement[] elements = throwable.getStackTrace();
/* 130 */     if ((elements != null) && (elements.length > 0)) {
/* 131 */       StackTraceElement element = elements[0];
/* 132 */       for (StackTraceElement stackTraceElement : elements) {
/* 133 */         if ((stackTraceElement != null) && (stackTraceElement.getClassName().equals(str.getClass().getName()))) {
/* 134 */           element = stackTraceElement;
/* 135 */           break;
/*     */         }
/*     */       }
/* 138 */       if (element != null) {
/* 139 */         msg = msg + " @ " + element;
/*     */       }
/*     */     }
/*     */ 
/* 143 */     return msg;
/*     */   }
/*     */ 
/*     */   public boolean isRemote() {
/* 147 */     return false;
/*     */   }
/*     */ 
/*     */   public boolean isRemotelyRunnable()
/*     */   {
/* 152 */     return (getBinaryFile() != null) && (getBinaryFile().exists());
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\greed-common-173.jar
 * Qualified Name:     com.dukascopy.api.impl.StrategyWrapper
 * JD-Core Version:    0.6.0
 */