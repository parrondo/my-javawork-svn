/*    */ package org.apache.lucene.search;
/*    */ 
/*    */ public class FieldDoc extends ScoreDoc
/*    */ {
/*    */   public Object[] fields;
/*    */ 
/*    */   public FieldDoc(int doc, float score)
/*    */   {
/* 53 */     super(doc, score);
/*    */   }
/*    */ 
/*    */   public FieldDoc(int doc, float score, Object[] fields)
/*    */   {
/* 58 */     super(doc, score);
/* 59 */     this.fields = fields;
/*    */   }
/*    */ 
/*    */   public FieldDoc(int doc, float score, Object[] fields, int shardIndex)
/*    */   {
/* 64 */     super(doc, score, shardIndex);
/* 65 */     this.fields = fields;
/*    */   }
/*    */ 
/*    */   public String toString()
/*    */   {
/* 73 */     StringBuilder sb = new StringBuilder(super.toString());
/* 74 */     sb.append("[");
/* 75 */     for (int i = 0; i < this.fields.length; i++) {
/* 76 */       sb.append(this.fields[i]).append(", ");
/*    */     }
/* 78 */     sb.setLength(sb.length() - 2);
/* 79 */     sb.append("]");
/* 80 */     return sb.toString();
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.search.FieldDoc
 * JD-Core Version:    0.6.0
 */