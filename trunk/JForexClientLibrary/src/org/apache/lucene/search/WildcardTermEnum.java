/*     */ package org.apache.lucene.search;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import org.apache.lucene.index.IndexReader;
/*     */ import org.apache.lucene.index.Term;
/*     */ 
/*     */ public class WildcardTermEnum extends FilteredTermEnum
/*     */ {
/*     */   final Term searchTerm;
/*     */   final String field;
/*     */   final String text;
/*     */   final String pre;
/*     */   final int preLen;
/*  38 */   boolean endEnum = false;
/*     */   public static final char WILDCARD_STRING = '*';
/*     */   public static final char WILDCARD_CHAR = '?';
/*     */ 
/*     */   public WildcardTermEnum(IndexReader reader, Term term)
/*     */     throws IOException
/*     */   {
/*  48 */     this.searchTerm = term;
/*  49 */     this.field = this.searchTerm.field();
/*  50 */     String searchTermText = this.searchTerm.text();
/*     */ 
/*  52 */     int sidx = searchTermText.indexOf('*');
/*  53 */     int cidx = searchTermText.indexOf('?');
/*  54 */     int idx = sidx;
/*  55 */     if (idx == -1) {
/*  56 */       idx = cidx;
/*     */     }
/*  58 */     else if (cidx >= 0) {
/*  59 */       idx = Math.min(idx, cidx);
/*     */     }
/*  61 */     this.pre = (idx != -1 ? this.searchTerm.text().substring(0, idx) : "");
/*     */ 
/*  63 */     this.preLen = this.pre.length();
/*  64 */     this.text = searchTermText.substring(this.preLen);
/*  65 */     setEnum(reader.terms(new Term(this.searchTerm.field(), this.pre)));
/*     */   }
/*     */ 
/*     */   protected final boolean termCompare(Term term)
/*     */   {
/*  70 */     if (this.field == term.field()) {
/*  71 */       String searchText = term.text();
/*  72 */       if (searchText.startsWith(this.pre)) {
/*  73 */         return wildcardEquals(this.text, 0, searchText, this.preLen);
/*     */       }
/*     */     }
/*  76 */     this.endEnum = true;
/*  77 */     return false;
/*     */   }
/*     */ 
/*     */   public float difference()
/*     */   {
/*  82 */     return 1.0F;
/*     */   }
/*     */ 
/*     */   public final boolean endEnum()
/*     */   {
/*  87 */     return this.endEnum;
/*     */   }
/*     */ 
/*     */   public static final boolean wildcardEquals(String pattern, int patternIdx, String string, int stringIdx)
/*     */   {
/* 105 */     int p = patternIdx;
/*     */ 
/* 107 */     for (int s = stringIdx; ; s++)
/*     */     {
/* 110 */       boolean sEnd = s >= string.length();
/*     */ 
/* 112 */       boolean pEnd = p >= pattern.length();
/*     */ 
/* 115 */       if (sEnd)
/*     */       {
/* 118 */         boolean justWildcardsLeft = true;
/*     */ 
/* 121 */         int wildcardSearchPos = p;
/*     */ 
/* 124 */         while ((wildcardSearchPos < pattern.length()) && (justWildcardsLeft))
/*     */         {
/* 127 */           char wildchar = pattern.charAt(wildcardSearchPos);
/*     */ 
/* 131 */           if ((wildchar != '?') && (wildchar != '*'))
/*     */           {
/* 133 */             justWildcardsLeft = false;
/*     */           }
/*     */           else
/*     */           {
/* 138 */             if (wildchar == '?') {
/* 139 */               return false;
/*     */             }
/*     */ 
/* 143 */             wildcardSearchPos++;
/*     */           }
/*     */ 
/*     */         }
/*     */ 
/* 149 */         if (justWildcardsLeft)
/*     */         {
/* 151 */           return true;
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/* 157 */       if ((sEnd) || (pEnd))
/*     */       {
/*     */         break;
/*     */       }
/*     */ 
/* 163 */       if (pattern.charAt(p) != '?')
/*     */       {
/* 169 */         if (pattern.charAt(p) == '*')
/*     */         {
/* 172 */           while ((p < pattern.length()) && (pattern.charAt(p) == '*')) {
/* 173 */             p++;
/*     */           }
/* 175 */           for (int i = string.length(); i >= s; i--)
/*     */           {
/* 177 */             if (wildcardEquals(pattern, p, string, i))
/*     */             {
/* 179 */               return true;
/*     */             }
/*     */           }
/* 182 */           break;
/*     */         }
/* 184 */         if (pattern.charAt(p) != string.charAt(s))
/*     */           break;
/*     */       }
/* 107 */       p++;
/*     */     }
/*     */ 
/* 189 */     return false;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.search.WildcardTermEnum
 * JD-Core Version:    0.6.0
 */