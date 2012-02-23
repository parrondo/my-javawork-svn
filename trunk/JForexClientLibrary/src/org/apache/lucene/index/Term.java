/*     */ package org.apache.lucene.index;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.ObjectInputStream;
/*     */ import java.io.Serializable;
/*     */ import org.apache.lucene.util.StringHelper;
/*     */ 
/*     */ public final class Term
/*     */   implements Comparable<Term>, Serializable
/*     */ {
/*     */   String field;
/*     */   String text;
/*     */ 
/*     */   public Term(String fld, String txt)
/*     */   {
/*  38 */     this.field = StringHelper.intern(fld);
/*  39 */     this.text = txt;
/*     */   }
/*     */ 
/*     */   public Term(String fld)
/*     */   {
/*  49 */     this(fld, "", true);
/*     */   }
/*     */ 
/*     */   Term(String fld, String txt, boolean intern) {
/*  53 */     this.field = (intern ? StringHelper.intern(fld) : fld);
/*  54 */     this.text = txt;
/*     */   }
/*     */ 
/*     */   public final String field()
/*     */   {
/*  59 */     return this.field;
/*     */   }
/*     */ 
/*     */   public final String text()
/*     */   {
/*  64 */     return this.text;
/*     */   }
/*     */ 
/*     */   public Term createTerm(String text)
/*     */   {
/*  74 */     return new Term(this.field, text, false);
/*     */   }
/*     */ 
/*     */   public boolean equals(Object obj)
/*     */   {
/*  79 */     if (this == obj)
/*  80 */       return true;
/*  81 */     if (obj == null)
/*  82 */       return false;
/*  83 */     if (getClass() != obj.getClass())
/*  84 */       return false;
/*  85 */     Term other = (Term)obj;
/*  86 */     if (this.field == null) {
/*  87 */       if (other.field != null)
/*  88 */         return false;
/*  89 */     } else if (this.field != other.field)
/*  90 */       return false;
/*  91 */     if (this.text == null) {
/*  92 */       if (other.text != null)
/*  93 */         return false;
/*  94 */     } else if (!this.text.equals(other.text))
/*  95 */       return false;
/*  96 */     return true;
/*     */   }
/*     */ 
/*     */   public int hashCode()
/*     */   {
/* 101 */     int prime = 31;
/* 102 */     int result = 1;
/* 103 */     result = 31 * result + (this.field == null ? 0 : this.field.hashCode());
/* 104 */     result = 31 * result + (this.text == null ? 0 : this.text.hashCode());
/* 105 */     return result;
/*     */   }
/*     */ 
/*     */   public final int compareTo(Term other)
/*     */   {
/* 114 */     if (this.field == other.field) {
/* 115 */       return this.text.compareTo(other.text);
/*     */     }
/* 117 */     return this.field.compareTo(other.field);
/*     */   }
/*     */ 
/*     */   final void set(String fld, String txt)
/*     */   {
/* 122 */     this.field = fld;
/* 123 */     this.text = txt;
/*     */   }
/*     */ 
/*     */   public final String toString() {
/* 127 */     return this.field + ":" + this.text;
/*     */   }
/*     */ 
/*     */   private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException
/*     */   {
/* 132 */     in.defaultReadObject();
/* 133 */     this.field = StringHelper.intern(this.field);
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.index.Term
 * JD-Core Version:    0.6.0
 */