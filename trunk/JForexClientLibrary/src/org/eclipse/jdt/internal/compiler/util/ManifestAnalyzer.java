/*     */ package org.eclipse.jdt.internal.compiler.util;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ 
/*     */ public class ManifestAnalyzer
/*     */ {
/*     */   private static final int START = 0;
/*     */   private static final int IN_CLASSPATH_HEADER = 1;
/*     */   private static final int PAST_CLASSPATH_HEADER = 2;
/*     */   private static final int SKIPPING_WHITESPACE = 3;
/*     */   private static final int READING_JAR = 4;
/*     */   private static final int CONTINUING = 5;
/*     */   private static final int SKIP_LINE = 6;
/*  28 */   private static final char[] CLASSPATH_HEADER_TOKEN = "Class-Path:".toCharArray();
/*     */   private int classpathSectionsCount;
/*     */   private ArrayList calledFilesNames;
/*     */ 
/*     */   public boolean analyzeManifestContents(InputStream inputStream)
/*     */     throws IOException
/*     */   {
/*  42 */     char[] chars = Util.getInputStreamAsCharArray(inputStream, -1, "UTF-8");
/*  43 */     int state = 0; int substate = 0;
/*  44 */     StringBuffer currentJarToken = new StringBuffer();
/*     */ 
/*  46 */     this.classpathSectionsCount = 0;
/*  47 */     this.calledFilesNames = null;
/*  48 */     int i = 0; for (int max = chars.length; i < max; ) {
/*  49 */       int currentChar = chars[(i++)];
/*  50 */       if (currentChar == 13)
/*     */       {
/*  52 */         if (i < max) {
/*  53 */           currentChar = chars[(i++)];
/*     */         }
/*     */       }
/*  56 */       switch (state) {
/*     */       case 0:
/*  58 */         if (currentChar == CLASSPATH_HEADER_TOKEN[0]) {
/*  59 */           state = 1;
/*  60 */           substate = 1;
/*     */         } else {
/*  62 */           state = 6;
/*     */         }
/*  64 */         break;
/*     */       case 1:
/*  66 */         if (currentChar == 10) {
/*  67 */           state = 0;
/*  68 */         } else if (currentChar != CLASSPATH_HEADER_TOKEN[(substate++)]) {
/*  69 */           state = 6; } else {
/*  70 */           if (substate != CLASSPATH_HEADER_TOKEN.length) continue;
/*  71 */           state = 2;
/*     */         }
/*  73 */         break;
/*     */       case 2:
/*  75 */         if (currentChar == 32) {
/*  76 */           state = 3;
/*  77 */           this.classpathSectionsCount += 1;
/*     */         } else {
/*  79 */           return false;
/*     */         }
/*     */ 
/*     */       case 3:
/*  83 */         if (currentChar == 10) {
/*  84 */           state = 5;
/*  85 */         } else if (currentChar != 32) {
/*  86 */           currentJarToken.append((char)currentChar);
/*  87 */           state = 4;
/*     */         }
/*     */         else {
/*  90 */           addCurrentTokenJarWhenNecessary(currentJarToken);
/*     */         }
/*  92 */         break;
/*     */       case 5:
/*  94 */         if (currentChar == 10) {
/*  95 */           addCurrentTokenJarWhenNecessary(currentJarToken);
/*  96 */           state = 0;
/*  97 */         } else if (currentChar == 32) {
/*  98 */           state = 3;
/*  99 */         } else if (currentChar == CLASSPATH_HEADER_TOKEN[0]) {
/* 100 */           addCurrentTokenJarWhenNecessary(currentJarToken);
/* 101 */           state = 1;
/* 102 */           substate = 1;
/* 103 */         } else if (this.calledFilesNames == null)
/*     */         {
/* 105 */           addCurrentTokenJarWhenNecessary(currentJarToken);
/* 106 */           state = 0;
/*     */         }
/*     */         else {
/* 109 */           addCurrentTokenJarWhenNecessary(currentJarToken);
/* 110 */           state = 6;
/*     */         }
/* 112 */         break;
/*     */       case 6:
/* 114 */         if (currentChar != 10) continue;
/* 115 */         state = 0;
/*     */ 
/* 117 */         break;
/*     */       case 4:
/* 119 */         if (currentChar == 10)
/*     */         {
/* 121 */           state = 5;
/*     */         }
/*     */         else {
/* 124 */           if (currentChar == 32)
/*     */           {
/* 126 */             state = 3;
/*     */           } else {
/* 128 */             currentJarToken.append((char)currentChar);
/* 129 */             continue;
/*     */           }
/* 131 */           addCurrentTokenJarWhenNecessary(currentJarToken);
/*     */         }
/*     */       }
/*     */     }
/* 135 */     switch (state) {
/*     */     case 0:
/* 137 */       return true;
/*     */     case 1:
/* 139 */       return true;
/*     */     case 2:
/* 141 */       return false;
/*     */     case 3:
/* 144 */       addCurrentTokenJarWhenNecessary(currentJarToken);
/* 145 */       return true;
/*     */     case 5:
/* 148 */       addCurrentTokenJarWhenNecessary(currentJarToken);
/* 149 */       return true;
/*     */     case 6:
/* 153 */       return (this.classpathSectionsCount == 0) || 
/* 152 */         (this.calledFilesNames != null);
/*     */     case 4:
/* 159 */       return false;
/*     */     }
/* 161 */     return true;
/*     */   }
/*     */ 
/*     */   private boolean addCurrentTokenJarWhenNecessary(StringBuffer currentJarToken)
/*     */   {
/* 166 */     if ((currentJarToken != null) && (currentJarToken.length() > 0)) {
/* 167 */       if (this.calledFilesNames == null) {
/* 168 */         this.calledFilesNames = new ArrayList();
/*     */       }
/* 170 */       this.calledFilesNames.add(currentJarToken.toString());
/* 171 */       currentJarToken.setLength(0);
/* 172 */       return true;
/*     */     }
/* 174 */     return false;
/*     */   }
/*     */ 
/*     */   public int getClasspathSectionsCount()
/*     */   {
/* 180 */     return this.classpathSectionsCount;
/*     */   }
/*     */   public List getCalledFileNames() {
/* 183 */     return this.calledFilesNames;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\ecj-3.5.2.jar
 * Qualified Name:     org.eclipse.jdt.internal.compiler.util.ManifestAnalyzer
 * JD-Core Version:    0.6.0
 */