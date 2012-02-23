/*     */ package org.apache.lucene.search;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.text.Collator;
/*     */ import org.apache.lucene.index.IndexReader;
/*     */ import org.apache.lucene.index.Term;
/*     */ import org.apache.lucene.util.StringHelper;
/*     */ 
/*     */ public class TermRangeTermEnum extends FilteredTermEnum
/*     */ {
/*  37 */   private Collator collator = null;
/*  38 */   private boolean endEnum = false;
/*     */   private String field;
/*     */   private String upperTermText;
/*     */   private String lowerTermText;
/*     */   private boolean includeLower;
/*     */   private boolean includeUpper;
/*     */ 
/*     */   public TermRangeTermEnum(IndexReader reader, String field, String lowerTermText, String upperTermText, boolean includeLower, boolean includeUpper, Collator collator)
/*     */     throws IOException
/*     */   {
/*  74 */     this.collator = collator;
/*  75 */     this.upperTermText = upperTermText;
/*  76 */     this.lowerTermText = lowerTermText;
/*  77 */     this.includeLower = includeLower;
/*  78 */     this.includeUpper = includeUpper;
/*  79 */     this.field = StringHelper.intern(field);
/*     */ 
/*  83 */     if (this.lowerTermText == null) {
/*  84 */       this.lowerTermText = "";
/*  85 */       this.includeLower = true;
/*     */     }
/*     */ 
/*  88 */     if (this.upperTermText == null) {
/*  89 */       this.includeUpper = true;
/*     */     }
/*     */ 
/*  92 */     String startTermText = collator == null ? this.lowerTermText : "";
/*  93 */     setEnum(reader.terms(new Term(this.field, startTermText)));
/*     */   }
/*     */ 
/*     */   public float difference()
/*     */   {
/*  98 */     return 1.0F;
/*     */   }
/*     */ 
/*     */   protected boolean endEnum()
/*     */   {
/* 103 */     return this.endEnum;
/*     */   }
/*     */ 
/*     */   protected boolean termCompare(Term term)
/*     */   {
/* 108 */     if (this.collator == null)
/*     */     {
/* 110 */       boolean checkLower = false;
/* 111 */       if (!this.includeLower)
/* 112 */         checkLower = true;
/* 113 */       if ((term != null) && (term.field() == this.field)) {
/* 114 */         if ((!checkLower) || (null == this.lowerTermText) || (term.text().compareTo(this.lowerTermText) > 0)) {
/* 115 */           checkLower = false;
/* 116 */           if (this.upperTermText != null) {
/* 117 */             int compare = this.upperTermText.compareTo(term.text());
/*     */ 
/* 122 */             if ((compare < 0) || ((!this.includeUpper) && (compare == 0)))
/*     */             {
/* 124 */               this.endEnum = true;
/* 125 */               return false;
/*     */             }
/*     */           }
/* 128 */           return true;
/*     */         }
/*     */       }
/*     */       else {
/* 132 */         this.endEnum = true;
/* 133 */         return false;
/*     */       }
/* 135 */       return false;
/*     */     }
/* 137 */     if ((term != null) && (term.field() == this.field))
/*     */     {
/* 146 */       return ((this.lowerTermText == null) || (this.includeLower ? this.collator.compare(term.text(), this.lowerTermText) >= 0 : this.collator.compare(term.text(), this.lowerTermText) > 0)) && ((this.upperTermText == null) || (this.includeUpper ? this.collator.compare(term.text(), this.upperTermText) <= 0 : this.collator.compare(term.text(), this.upperTermText) < 0));
/*     */     }
/*     */ 
/* 150 */     this.endEnum = true;
/* 151 */     return false;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.search.TermRangeTermEnum
 * JD-Core Version:    0.6.0
 */