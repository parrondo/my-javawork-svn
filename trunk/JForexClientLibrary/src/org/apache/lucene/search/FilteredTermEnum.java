/*     */ package org.apache.lucene.search;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import org.apache.lucene.index.Term;
/*     */ import org.apache.lucene.index.TermEnum;
/*     */ 
/*     */ public abstract class FilteredTermEnum extends TermEnum
/*     */ {
/*  30 */   protected Term currentTerm = null;
/*     */ 
/*  33 */   protected TermEnum actualEnum = null;
/*     */ 
/*     */   protected abstract boolean termCompare(Term paramTerm);
/*     */ 
/*     */   public abstract float difference();
/*     */ 
/*     */   protected abstract boolean endEnum();
/*     */ 
/*     */   protected void setEnum(TermEnum actualEnum)
/*     */     throws IOException
/*     */   {
/*  51 */     this.actualEnum = actualEnum;
/*     */ 
/*  53 */     Term term = actualEnum.term();
/*  54 */     if ((term != null) && (termCompare(term)))
/*  55 */       this.currentTerm = term;
/*  56 */     else next();
/*     */   }
/*     */ 
/*     */   public int docFreq()
/*     */   {
/*  65 */     if (this.currentTerm == null) return -1;
/*  66 */     assert (this.actualEnum != null);
/*  67 */     return this.actualEnum.docFreq();
/*     */   }
/*     */ 
/*     */   public boolean next()
/*     */     throws IOException
/*     */   {
/*  73 */     if (this.actualEnum == null) return false;
/*  74 */     this.currentTerm = null;
/*  75 */     while (this.currentTerm == null) {
/*  76 */       if (endEnum()) return false;
/*  77 */       if (this.actualEnum.next()) {
/*  78 */         Term term = this.actualEnum.term();
/*  79 */         if (termCompare(term)) {
/*  80 */           this.currentTerm = term;
/*  81 */           return true;
/*     */         }
/*  83 */         continue;
/*  84 */       }return false;
/*     */     }
/*  86 */     this.currentTerm = null;
/*  87 */     return false;
/*     */   }
/*     */ 
/*     */   public Term term()
/*     */   {
/*  94 */     return this.currentTerm;
/*     */   }
/*     */ 
/*     */   public void close()
/*     */     throws IOException
/*     */   {
/* 100 */     if (this.actualEnum != null) this.actualEnum.close();
/* 101 */     this.currentTerm = null;
/* 102 */     this.actualEnum = null;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.search.FilteredTermEnum
 * JD-Core Version:    0.6.0
 */