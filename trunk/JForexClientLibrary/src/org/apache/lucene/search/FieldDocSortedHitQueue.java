/*     */ package org.apache.lucene.search;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.text.Collator;
/*     */ import java.util.Locale;
/*     */ import org.apache.lucene.util.PriorityQueue;
/*     */ 
/*     */ class FieldDocSortedHitQueue extends PriorityQueue<FieldDoc>
/*     */ {
/*  35 */   volatile SortField[] fields = null;
/*     */ 
/*  39 */   volatile Collator[] collators = null;
/*     */ 
/*  41 */   volatile FieldComparator[] comparators = null;
/*     */ 
/*     */   FieldDocSortedHitQueue(int size)
/*     */   {
/*  50 */     initialize(size);
/*     */   }
/*     */ 
/*     */   void setFields(SortField[] fields)
/*     */     throws IOException
/*     */   {
/*  63 */     this.fields = fields;
/*  64 */     this.collators = hasCollators(fields);
/*  65 */     this.comparators = new FieldComparator[fields.length];
/*  66 */     for (int fieldIDX = 0; fieldIDX < fields.length; fieldIDX++)
/*  67 */       this.comparators[fieldIDX] = fields[fieldIDX].getComparator(1, fieldIDX);
/*     */   }
/*     */ 
/*     */   SortField[] getFields()
/*     */   {
/*  74 */     return this.fields;
/*     */   }
/*     */ 
/*     */   private Collator[] hasCollators(SortField[] fields)
/*     */   {
/*  84 */     if (fields == null) return null;
/*  85 */     Collator[] ret = new Collator[fields.length];
/*  86 */     for (int i = 0; i < fields.length; i++) {
/*  87 */       Locale locale = fields[i].getLocale();
/*  88 */       if (locale != null)
/*  89 */         ret[i] = Collator.getInstance(locale);
/*     */     }
/*  91 */     return ret;
/*     */   }
/*     */ 
/*     */   protected final boolean lessThan(FieldDoc docA, FieldDoc docB)
/*     */   {
/* 103 */     int n = this.fields.length;
/* 104 */     int c = 0;
/* 105 */     for (int i = 0; (i < n) && (c == 0); i++) {
/* 106 */       int type = this.fields[i].getType();
/* 107 */       if (type == 3) {
/* 108 */         String s1 = (String)docA.fields[i];
/* 109 */         String s2 = (String)docB.fields[i];
/*     */ 
/* 113 */         if (s1 == null)
/* 114 */           c = s2 == null ? 0 : -1;
/* 115 */         else if (s2 == null)
/* 116 */           c = 1;
/* 117 */         else if (this.fields[i].getLocale() == null)
/* 118 */           c = s1.compareTo(s2);
/*     */         else
/* 120 */           c = this.collators[i].compare(s1, s2);
/*     */       }
/*     */       else {
/* 123 */         c = this.comparators[i].compareValues(docA.fields[i], docB.fields[i]);
/*     */       }
/*     */ 
/* 126 */       if (this.fields[i].getReverse()) {
/* 127 */         c = -c;
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 132 */     if (c == 0) {
/* 133 */       return docA.doc > docB.doc;
/*     */     }
/* 135 */     return c > 0;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.search.FieldDocSortedHitQueue
 * JD-Core Version:    0.6.0
 */