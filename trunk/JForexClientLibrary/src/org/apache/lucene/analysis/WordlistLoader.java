/*     */ package org.apache.lucene.analysis;
/*     */ 
/*     */ import java.io.BufferedReader;
/*     */ import java.io.File;
/*     */ import java.io.FileReader;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStreamReader;
/*     */ import java.io.Reader;
/*     */ import java.util.HashMap;
/*     */ import java.util.HashSet;
/*     */ import java.util.Set;
/*     */ 
/*     */ public class WordlistLoader
/*     */ {
/*     */   public static Set<String> getWordSet(Class<?> aClass, String stopwordResource)
/*     */     throws IOException
/*     */   {
/*  51 */     Reader reader = new BufferedReader(new InputStreamReader(aClass.getResourceAsStream(stopwordResource), "UTF-8"));
/*     */     try
/*     */     {
/*  54 */       HashSet localHashSet = getWordSet(reader);
/*     */       return localHashSet; } finally { reader.close(); } throw localObject;
/*     */   }
/*     */ 
/*     */   public static Set<String> getWordSet(Class<?> aClass, String stopwordResource, String comment)
/*     */     throws IOException
/*     */   {
/*  78 */     Reader reader = new BufferedReader(new InputStreamReader(aClass.getResourceAsStream(stopwordResource), "UTF-8"));
/*     */     try
/*     */     {
/*  81 */       HashSet localHashSet = getWordSet(reader, comment);
/*     */       return localHashSet; } finally { reader.close(); } throw localObject;
/*     */   }
/*     */ 
/*     */   public static HashSet<String> getWordSet(File wordfile)
/*     */     throws IOException
/*     */   {
/*  97 */     FileReader reader = null;
/*     */     try {
/*  99 */       reader = new FileReader(wordfile);
/* 100 */       HashSet localHashSet = getWordSet(reader);
/*     */       return localHashSet;
/*     */     }
/*     */     finally
/*     */     {
/* 103 */       if (reader != null)
/* 104 */         reader.close(); 
/* 104 */     }throw localObject;
/*     */   }
/*     */ 
/*     */   public static HashSet<String> getWordSet(File wordfile, String comment)
/*     */     throws IOException
/*     */   {
/* 119 */     FileReader reader = null;
/*     */     try {
/* 121 */       reader = new FileReader(wordfile);
/* 122 */       HashSet localHashSet = getWordSet(reader, comment);
/*     */       return localHashSet;
/*     */     }
/*     */     finally
/*     */     {
/* 125 */       if (reader != null)
/* 126 */         reader.close(); 
/* 126 */     }throw localObject;
/*     */   }
/*     */ 
/*     */   public static HashSet<String> getWordSet(Reader reader)
/*     */     throws IOException
/*     */   {
/* 141 */     HashSet result = new HashSet();
/* 142 */     BufferedReader br = null;
/*     */     try {
/* 144 */       if ((reader instanceof BufferedReader))
/* 145 */         br = (BufferedReader)reader;
/*     */       else {
/* 147 */         br = new BufferedReader(reader);
/*     */       }
/* 149 */       String word = null;
/* 150 */       while ((word = br.readLine()) != null)
/* 151 */         result.add(word.trim());
/*     */     }
/*     */     finally
/*     */     {
/* 155 */       if (br != null)
/* 156 */         br.close();
/*     */     }
/* 158 */     return result;
/*     */   }
/*     */ 
/*     */   public static HashSet<String> getWordSet(Reader reader, String comment)
/*     */     throws IOException
/*     */   {
/* 172 */     HashSet result = new HashSet();
/* 173 */     BufferedReader br = null;
/*     */     try {
/* 175 */       if ((reader instanceof BufferedReader))
/* 176 */         br = (BufferedReader)reader;
/*     */       else {
/* 178 */         br = new BufferedReader(reader);
/*     */       }
/* 180 */       String word = null;
/* 181 */       while ((word = br.readLine()) != null) {
/* 182 */         if (!word.startsWith(comment))
/* 183 */           result.add(word.trim());
/*     */       }
/*     */     }
/*     */     finally
/*     */     {
/* 188 */       if (br != null)
/* 189 */         br.close();
/*     */     }
/* 191 */     return result;
/*     */   }
/*     */ 
/*     */   public static Set<String> getSnowballWordSet(Class<?> aClass, String stopwordResource)
/*     */     throws IOException
/*     */   {
/* 208 */     Reader reader = new BufferedReader(new InputStreamReader(aClass.getResourceAsStream(stopwordResource), "UTF-8"));
/*     */     try
/*     */     {
/* 211 */       Set localSet = getSnowballWordSet(reader);
/*     */       return localSet; } finally { reader.close(); } throw localObject;
/*     */   }
/*     */ 
/*     */   public static Set<String> getSnowballWordSet(Reader reader)
/*     */     throws IOException
/*     */   {
/* 233 */     Set result = new HashSet();
/* 234 */     BufferedReader br = null;
/*     */     try {
/* 236 */       if ((reader instanceof BufferedReader))
/* 237 */         br = (BufferedReader)reader;
/*     */       else {
/* 239 */         br = new BufferedReader(reader);
/*     */       }
/* 241 */       String line = null;
/* 242 */       while ((line = br.readLine()) != null) {
/* 243 */         int comment = line.indexOf('|');
/* 244 */         if (comment >= 0) line = line.substring(0, comment);
/* 245 */         String[] words = line.split("\\s+");
/* 246 */         for (int i = 0; i < words.length; i++) {
/* 247 */           if (words[i].length() <= 0) continue; result.add(words[i]);
/*     */         }
/*     */       }
/*     */     } finally {
/* 250 */       if (br != null) br.close();
/*     */     }
/* 252 */     return result;
/*     */   }
/*     */ 
/*     */   public static HashMap<String, String> getStemDict(File wordstemfile)
/*     */     throws IOException
/*     */   {
/* 265 */     if (wordstemfile == null)
/* 266 */       throw new NullPointerException("wordstemfile may not be null");
/* 267 */     HashMap result = new HashMap();
/* 268 */     BufferedReader br = null;
/*     */     try
/*     */     {
/* 271 */       br = new BufferedReader(new FileReader(wordstemfile));
/*     */       String line;
/* 273 */       while ((line = br.readLine()) != null) {
/* 274 */         String[] wordstem = line.split("\t", 2);
/* 275 */         result.put(wordstem[0], wordstem[1]);
/*     */       }
/*     */     } finally {
/* 278 */       if (br != null)
/* 279 */         br.close();
/*     */     }
/* 281 */     return result;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.analysis.WordlistLoader
 * JD-Core Version:    0.6.0
 */