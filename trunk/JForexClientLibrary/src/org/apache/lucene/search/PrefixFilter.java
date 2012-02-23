/*    */ package org.apache.lucene.search;
/*    */ 
/*    */ import org.apache.lucene.index.Term;
/*    */ 
/*    */ public class PrefixFilter extends MultiTermQueryWrapperFilter<PrefixQuery>
/*    */ {
/*    */   public PrefixFilter(Term prefix)
/*    */   {
/* 29 */     super(new PrefixQuery(prefix));
/*    */   }
/*    */   public Term getPrefix() {
/* 32 */     return ((PrefixQuery)this.query).getPrefix();
/*    */   }
/*    */ 
/*    */   public String toString()
/*    */   {
/* 37 */     StringBuilder buffer = new StringBuilder();
/* 38 */     buffer.append("PrefixFilter(");
/* 39 */     buffer.append(getPrefix().toString());
/* 40 */     buffer.append(")");
/* 41 */     return buffer.toString();
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.search.PrefixFilter
 * JD-Core Version:    0.6.0
 */