/*     */ package org.apache.lucene.search;
/*     */ 
/*     */ import java.io.Serializable;
/*     */ import java.util.Arrays;
/*     */ 
/*     */ public class Sort
/*     */   implements Serializable
/*     */ {
/* 109 */   public static final Sort RELEVANCE = new Sort();
/*     */ 
/* 112 */   public static final Sort INDEXORDER = new Sort(SortField.FIELD_DOC);
/*     */   SortField[] fields;
/*     */ 
/*     */   public Sort()
/*     */   {
/* 123 */     this(SortField.FIELD_SCORE);
/*     */   }
/*     */ 
/*     */   public Sort(SortField field)
/*     */   {
/* 128 */     setSort(field);
/*     */   }
/*     */ 
/*     */   public Sort(SortField[] fields)
/*     */   {
/* 133 */     setSort(fields);
/*     */   }
/*     */ 
/*     */   public void setSort(SortField field)
/*     */   {
/* 138 */     this.fields = new SortField[] { field };
/*     */   }
/*     */ 
/*     */   public void setSort(SortField[] fields)
/*     */   {
/* 143 */     this.fields = fields;
/*     */   }
/*     */ 
/*     */   public SortField[] getSort()
/*     */   {
/* 151 */     return this.fields;
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/* 156 */     StringBuilder buffer = new StringBuilder();
/*     */ 
/* 158 */     for (int i = 0; i < this.fields.length; i++) {
/* 159 */       buffer.append(this.fields[i].toString());
/* 160 */       if (i + 1 < this.fields.length) {
/* 161 */         buffer.append(',');
/*     */       }
/*     */     }
/* 164 */     return buffer.toString();
/*     */   }
/*     */ 
/*     */   public boolean equals(Object o)
/*     */   {
/* 170 */     if (this == o) return true;
/* 171 */     if (!(o instanceof Sort)) return false;
/* 172 */     Sort other = (Sort)o;
/* 173 */     return Arrays.equals(this.fields, other.fields);
/*     */   }
/*     */ 
/*     */   public int hashCode()
/*     */   {
/* 179 */     return 1168832101 + Arrays.hashCode(this.fields);
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.search.Sort
 * JD-Core Version:    0.6.0
 */