/*    */ package org.apache.lucene.search;
/*    */ 
/*    */ import java.io.IOException;
/*    */ import org.apache.lucene.index.IndexReader;
/*    */ import org.apache.lucene.index.Term;
/*    */ import org.apache.lucene.util.ToStringUtils;
/*    */ 
/*    */ public class PrefixQuery extends MultiTermQuery
/*    */ {
/*    */   private Term prefix;
/*    */ 
/*    */   public PrefixQuery(Term prefix)
/*    */   {
/* 37 */     this.prefix = prefix;
/*    */   }
/*    */ 
/*    */   public Term getPrefix() {
/* 41 */     return this.prefix;
/*    */   }
/*    */ 
/*    */   protected FilteredTermEnum getEnum(IndexReader reader) throws IOException {
/* 45 */     return new PrefixTermEnum(reader, this.prefix);
/*    */   }
/*    */ 
/*    */   public String toString(String field)
/*    */   {
/* 51 */     StringBuilder buffer = new StringBuilder();
/* 52 */     if (!this.prefix.field().equals(field)) {
/* 53 */       buffer.append(this.prefix.field());
/* 54 */       buffer.append(":");
/*    */     }
/* 56 */     buffer.append(this.prefix.text());
/* 57 */     buffer.append('*');
/* 58 */     buffer.append(ToStringUtils.boost(getBoost()));
/* 59 */     return buffer.toString();
/*    */   }
/*    */ 
/*    */   public int hashCode()
/*    */   {
/* 64 */     int prime = 31;
/* 65 */     int result = super.hashCode();
/* 66 */     result = 31 * result + (this.prefix == null ? 0 : this.prefix.hashCode());
/* 67 */     return result;
/*    */   }
/*    */ 
/*    */   public boolean equals(Object obj)
/*    */   {
/* 72 */     if (this == obj)
/* 73 */       return true;
/* 74 */     if (!super.equals(obj))
/* 75 */       return false;
/* 76 */     if (getClass() != obj.getClass())
/* 77 */       return false;
/* 78 */     PrefixQuery other = (PrefixQuery)obj;
/* 79 */     if (this.prefix == null) {
/* 80 */       if (other.prefix != null)
/* 81 */         return false;
/* 82 */     } else if (!this.prefix.equals(other.prefix))
/* 83 */       return false;
/* 84 */     return true;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.search.PrefixQuery
 * JD-Core Version:    0.6.0
 */