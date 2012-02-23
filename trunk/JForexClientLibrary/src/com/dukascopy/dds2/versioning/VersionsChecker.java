/*     */ package com.dukascopy.dds2.versioning;
/*     */ 
/*     */ import java.io.BufferedInputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.PrintStream;
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashMap;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ 
/*     */ public class VersionsChecker
/*     */ {
/*     */   public static final String WRONG_VERSIONS = "Wrong jar version in CP";
/*     */   public static final String REDUNDANT = "Redundant jar in Cp";
/*     */   public static final String MISSED = "Missed jar in CP";
/*     */ 
/*     */   public static List<String> getClassPathJars()
/*     */   {
/*  22 */     ArrayList arrayList = new ArrayList();
/*  23 */     String separator = System.getProperty("path.separator");
/*  24 */     String fullPath = System.getProperty("java.class.path");
/*  25 */     String[] paths = fullPath.split(separator);
/*  26 */     for (String path : paths) {
/*  27 */       String jarName = path.substring(path.lastIndexOf("/") + 1);
/*  28 */       if (jarName.contains(".jar")) {
/*  29 */         arrayList.add(jarName);
/*     */       }
/*     */     }
/*     */ 
/*  33 */     return arrayList;
/*     */   }
/*     */ 
/*     */   public static List<String> getDependecies() {
/*  37 */     ArrayList arrayList = new ArrayList();
/*  38 */     InputStream stream = ClassLoader.getSystemResourceAsStream("META-INF/project.dependecies");
/*  39 */     BufferedInputStream inputStream = new BufferedInputStream(stream);
/*     */     try {
/*  41 */       byte[] bites = new byte[inputStream.available()];
/*  42 */       inputStream.read(bites);
/*  43 */       String s = new String(bites);
/*  44 */       String[] jars = s.split("\n");
/*  45 */       for (String jar : jars)
/*  46 */         arrayList.add(jar);
/*     */     }
/*     */     catch (IOException e) {
/*  49 */       System.out.println("No dependencies meta info in jar. Please use maven version plugin to build jar");
/*     */     }
/*     */ 
/*  52 */     return arrayList;
/*     */   }
/*     */ 
/*     */   public static List<String> getWrongVersionsJars() {
/*  56 */     List classPathJars = getClassPathJars();
/*  57 */     List dependecies = getDependecies();
/*  58 */     ArrayList output = new ArrayList();
/*  59 */     for (Iterator i$ = classPathJars.iterator(); i$.hasNext(); ) { classPathJar = (String)i$.next();
/*  60 */       jarSplits = splitJarAndVersion(classPathJar);
/*  61 */       for (String dependecy : dependecies) {
/*  62 */         String[] dependencySplits = splitJarAndVersion(dependecy + ".jar");
/*  63 */         if ((jarSplits != null) && (dependencySplits != null) && 
/*  64 */           (jarSplits[0].equals(dependencySplits[0])) && 
/*  65 */           (!jarSplits[1].equals(dependencySplits[1])))
/*  66 */           output.add(classPathJar);
/*     */       }
/*     */     }
/*     */     String classPathJar;
/*     */     String[] jarSplits;
/*  72 */     return output;
/*     */   }
/*     */ 
/*     */   public static List<String> getRedundantJars() {
/*  76 */     List classPathJars = getClassPathJars();
/*  77 */     List dependecies = getDependecies();
/*  78 */     List toRemove = new ArrayList();
/*  79 */     for (Iterator i$ = classPathJars.iterator(); i$.hasNext(); ) { classPathJar = (String)i$.next();
/*  80 */       jarSplits = splitJarAndVersion(classPathJar);
/*  81 */       for (String dependecy : dependecies) {
/*  82 */         String[] dependencySplits = splitJarAndVersion(dependecy + ".jar");
/*  83 */         if ((jarSplits != null) && (dependencySplits != null) && 
/*  84 */           (jarSplits[0].equals(dependencySplits[0])))
/*  85 */           toRemove.add(classPathJar);
/*     */       }
/*     */     }
/*     */     String classPathJar;
/*     */     String[] jarSplits;
/*  90 */     classPathJars.removeAll(toRemove);
/*  91 */     return classPathJars;
/*     */   }
/*     */ 
/*     */   public static List<String> getMissedJars() {
/*  95 */     List classPathJars = getClassPathJars();
/*  96 */     List dependecies = getDependecies();
/*  97 */     List toRemove = new ArrayList();
/*  98 */     for (Iterator i$ = dependecies.iterator(); i$.hasNext(); ) { dependecy = (String)i$.next();
/*  99 */       dependencySplits = splitJarAndVersion(dependecy + ".jar");
/* 100 */       for (String classPathJar : classPathJars) {
/* 101 */         String[] jarSplits = splitJarAndVersion(classPathJar);
/* 102 */         if ((jarSplits != null) && (dependencySplits != null) && 
/* 103 */           (jarSplits[0].equals(dependencySplits[0])))
/* 104 */           toRemove.add(dependecy);
/*     */       }
/*     */     }
/*     */     String dependecy;
/*     */     String[] dependencySplits;
/* 109 */     dependecies.removeAll(toRemove);
/* 110 */     return dependecies;
/*     */   }
/*     */ 
/*     */   public static Map<String, List<String>> getProblemsInDependencies()
/*     */   {
/* 115 */     if (getDependecies().isEmpty()) {
/* 116 */       return null;
/*     */     }
/* 118 */     List classPathJars = getClassPathJars();
/* 119 */     List dependecies = getDependecies();
/* 120 */     List toRemoveClassPath = new ArrayList();
/* 121 */     List toRemoveDependecy = new ArrayList();
/* 122 */     ArrayList wrongVersions = new ArrayList();
/* 123 */     for (Iterator i$ = classPathJars.iterator(); i$.hasNext(); ) { classPathJar = (String)i$.next();
/* 124 */       jarSplits = splitJarAndVersion(classPathJar);
/* 125 */       for (String dependecy : dependecies) {
/* 126 */         String[] dependencySplits = splitJarAndVersion(dependecy + ".jar");
/* 127 */         if ((jarSplits != null) && (dependencySplits != null) && 
/* 128 */           (jarSplits[0].equals(dependencySplits[0]))) {
/* 129 */           if (!jarSplits[1].equals(dependencySplits[1])) {
/* 130 */             wrongVersions.add(classPathJar);
/*     */           }
/* 132 */           toRemoveClassPath.add(classPathJar);
/* 133 */           toRemoveDependecy.add(dependecy);
/*     */         }
/*     */       }
/*     */     }
/*     */     String classPathJar;
/*     */     String[] jarSplits;
/* 138 */     classPathJars.removeAll(toRemoveClassPath);
/* 139 */     dependecies.removeAll(toRemoveDependecy);
/* 140 */     Map output = new HashMap();
/* 141 */     output.put("Wrong jar version in CP", wrongVersions);
/* 142 */     output.put("Redundant jar in Cp", classPathJars);
/* 143 */     output.put("Missed jar in CP", dependecies);
/* 144 */     return output;
/*     */   }
/*     */ 
/*     */   private static String[] splitJarAndVersion(String fullName) {
/* 148 */     int lastVersionIndex = fullName.indexOf(".jar");
/* 149 */     int firstIndexOfVersion = fullName.lastIndexOf("-");
/* 150 */     String[] output = new String[2];
/* 151 */     if (lastVersionIndex == -1) {
/* 152 */       return null;
/*     */     }
/* 154 */     if (firstIndexOfVersion != -1) {
/* 155 */       output[0] = fullName.substring(0, firstIndexOfVersion);
/* 156 */       output[1] = fullName.substring(firstIndexOfVersion + 1, lastVersionIndex);
/*     */     } else {
/* 158 */       output[0] = fullName.substring(0, lastVersionIndex);
/* 159 */       output[1] = "none";
/*     */     }
/* 161 */     return output;
/*     */   }
/*     */ 
/*     */   public static void main(String[] args) {
/* 165 */     Map map = getProblemsInDependencies();
/* 166 */     for (String key : map.keySet()) {
/* 167 */       List jars = (List)map.get(key);
/* 168 */       System.out.println(key + ":" + jars);
/*     */     }
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\dds2-common-2.3.77.jar
 * Qualified Name:     com.dukascopy.dds2.versioning.VersionsChecker
 * JD-Core Version:    0.6.0
 */