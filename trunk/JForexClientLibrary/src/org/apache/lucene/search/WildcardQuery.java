/*     */ package org.apache.lucene.search;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import org.apache.lucene.index.IndexReader;
/*     */ import org.apache.lucene.index.Term;
/*     */ import org.apache.lucene.util.ToStringUtils;
/*     */ 
/*     */ public class WildcardQuery extends MultiTermQuery
/*     */ {
/*     */   private boolean termContainsWildcard;
/*     */   private boolean termIsPrefix;
/*     */   protected Term term;
/*     */ 
/*     */   public WildcardQuery(Term term)
/*     */   {
/*  44 */     this.term = term;
/*  45 */     String text = term.text();
/*  46 */     this.termContainsWildcard = ((text.indexOf('*') != -1) || (text.indexOf('?') != -1));
/*     */ 
/*  48 */     this.termIsPrefix = ((this.termContainsWildcard) && (text.indexOf('?') == -1) && (text.indexOf('*') == text.length() - 1));
/*     */   }
/*     */ 
/*     */   protected FilteredTermEnum getEnum(IndexReader reader)
/*     */     throws IOException
/*     */   {
/*  55 */     if (this.termIsPrefix) {
/*  56 */       return new PrefixTermEnum(reader, this.term.createTerm(this.term.text().substring(0, this.term.text().indexOf('*'))));
/*     */     }
/*  58 */     if (this.termContainsWildcard) {
/*  59 */       return new WildcardTermEnum(reader, getTerm());
/*     */     }
/*  61 */     return new SingleTermEnum(reader, getTerm());
/*     */   }
/*     */ 
/*     */   public Term getTerm()
/*     */   {
/*  69 */     return this.term;
/*     */   }
/*     */ 
/*     */   public String toString(String field)
/*     */   {
/*  75 */     StringBuilder buffer = new StringBuilder();
/*  76 */     if (!this.term.field().equals(field)) {
/*  77 */       buffer.append(this.term.field());
/*  78 */       buffer.append(":");
/*     */     }
/*  80 */     buffer.append(this.term.text());
/*  81 */     buffer.append(ToStringUtils.boost(getBoost()));
/*  82 */     return buffer.toString();
/*     */   }
/*     */ 
/*     */   public int hashCode()
/*     */   {
/*  87 */     int prime = 31;
/*  88 */     int result = super.hashCode();
/*  89 */     result = 31 * result + (this.term == null ? 0 : this.term.hashCode());
/*  90 */     return result;
/*     */   }
/*     */ 
/*     */   public boolean equals(Object obj)
/*     */   {
/*  95 */     if (this == obj)
/*  96 */       return true;
/*  97 */     if (!super.equals(obj))
/*  98 */       return false;
/*  99 */     if (getClass() != obj.getClass())
/* 100 */       return false;
/* 101 */     WildcardQuery other = (WildcardQuery)obj;
/* 102 */     if (this.term == null) {
/* 103 */       if (other.term != null)
/* 104 */         return false;
/* 105 */     } else if (!this.term.equals(other.term))
/* 106 */       return false;
/* 107 */     return true;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.search.WildcardQuery
 * JD-Core Version:    0.6.0
 */