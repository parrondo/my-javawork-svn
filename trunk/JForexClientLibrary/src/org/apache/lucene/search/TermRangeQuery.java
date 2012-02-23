/*     */ package org.apache.lucene.search;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.text.Collator;
/*     */ import org.apache.lucene.index.IndexReader;
/*     */ import org.apache.lucene.util.ToStringUtils;
/*     */ 
/*     */ public class TermRangeQuery extends MultiTermQuery
/*     */ {
/*     */   private String lowerTerm;
/*     */   private String upperTerm;
/*     */   private Collator collator;
/*     */   private String field;
/*     */   private boolean includeLower;
/*     */   private boolean includeUpper;
/*     */ 
/*     */   public TermRangeQuery(String field, String lowerTerm, String upperTerm, boolean includeLower, boolean includeUpper)
/*     */   {
/*  72 */     this(field, lowerTerm, upperTerm, includeLower, includeUpper, null);
/*     */   }
/*     */ 
/*     */   public TermRangeQuery(String field, String lowerTerm, String upperTerm, boolean includeLower, boolean includeUpper, Collator collator)
/*     */   {
/* 107 */     this.field = field;
/* 108 */     this.lowerTerm = lowerTerm;
/* 109 */     this.upperTerm = upperTerm;
/* 110 */     this.includeLower = includeLower;
/* 111 */     this.includeUpper = includeUpper;
/* 112 */     this.collator = collator;
/*     */   }
/*     */ 
/*     */   public String getField() {
/* 116 */     return this.field;
/*     */   }
/*     */   public String getLowerTerm() {
/* 119 */     return this.lowerTerm;
/*     */   }
/*     */   public String getUpperTerm() {
/* 122 */     return this.upperTerm;
/*     */   }
/*     */   public boolean includesLower() {
/* 125 */     return this.includeLower;
/*     */   }
/*     */   public boolean includesUpper() {
/* 128 */     return this.includeUpper;
/*     */   }
/*     */   public Collator getCollator() {
/* 131 */     return this.collator;
/*     */   }
/*     */ 
/*     */   protected FilteredTermEnum getEnum(IndexReader reader) throws IOException {
/* 135 */     return new TermRangeTermEnum(reader, this.field, this.lowerTerm, this.upperTerm, this.includeLower, this.includeUpper, this.collator);
/*     */   }
/*     */ 
/*     */   public String toString(String field)
/*     */   {
/* 142 */     StringBuilder buffer = new StringBuilder();
/* 143 */     if (!getField().equals(field)) {
/* 144 */       buffer.append(getField());
/* 145 */       buffer.append(":");
/*     */     }
/* 147 */     buffer.append(this.includeLower ? '[' : '{');
/* 148 */     buffer.append(this.lowerTerm != null ? this.lowerTerm : "*");
/* 149 */     buffer.append(" TO ");
/* 150 */     buffer.append(this.upperTerm != null ? this.upperTerm : "*");
/* 151 */     buffer.append(this.includeUpper ? ']' : '}');
/* 152 */     buffer.append(ToStringUtils.boost(getBoost()));
/* 153 */     return buffer.toString();
/*     */   }
/*     */ 
/*     */   public int hashCode()
/*     */   {
/* 158 */     int prime = 31;
/* 159 */     int result = super.hashCode();
/* 160 */     result = 31 * result + (this.collator == null ? 0 : this.collator.hashCode());
/* 161 */     result = 31 * result + (this.field == null ? 0 : this.field.hashCode());
/* 162 */     result = 31 * result + (this.includeLower ? 1231 : 1237);
/* 163 */     result = 31 * result + (this.includeUpper ? 1231 : 1237);
/* 164 */     result = 31 * result + (this.lowerTerm == null ? 0 : this.lowerTerm.hashCode());
/* 165 */     result = 31 * result + (this.upperTerm == null ? 0 : this.upperTerm.hashCode());
/* 166 */     return result;
/*     */   }
/*     */ 
/*     */   public boolean equals(Object obj)
/*     */   {
/* 171 */     if (this == obj)
/* 172 */       return true;
/* 173 */     if (!super.equals(obj))
/* 174 */       return false;
/* 175 */     if (getClass() != obj.getClass())
/* 176 */       return false;
/* 177 */     TermRangeQuery other = (TermRangeQuery)obj;
/* 178 */     if (this.collator == null) {
/* 179 */       if (other.collator != null)
/* 180 */         return false;
/* 181 */     } else if (!this.collator.equals(other.collator))
/* 182 */       return false;
/* 183 */     if (this.field == null) {
/* 184 */       if (other.field != null)
/* 185 */         return false;
/* 186 */     } else if (!this.field.equals(other.field))
/* 187 */       return false;
/* 188 */     if (this.includeLower != other.includeLower)
/* 189 */       return false;
/* 190 */     if (this.includeUpper != other.includeUpper)
/* 191 */       return false;
/* 192 */     if (this.lowerTerm == null) {
/* 193 */       if (other.lowerTerm != null)
/* 194 */         return false;
/* 195 */     } else if (!this.lowerTerm.equals(other.lowerTerm))
/* 196 */       return false;
/* 197 */     if (this.upperTerm == null) {
/* 198 */       if (other.upperTerm != null)
/* 199 */         return false;
/* 200 */     } else if (!this.upperTerm.equals(other.upperTerm))
/* 201 */       return false;
/* 202 */     return true;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.search.TermRangeQuery
 * JD-Core Version:    0.6.0
 */