/*    */ package org.apache.lucene.search;
/*    */ 
/*    */ import java.io.Serializable;
/*    */ 
/*    */ public class ScoreDoc
/*    */   implements Serializable
/*    */ {
/*    */   public float score;
/*    */   public int doc;
/*    */   public int shardIndex;
/*    */ 
/*    */   public ScoreDoc(int doc, float score)
/*    */   {
/* 36 */     this(doc, score, -1);
/*    */   }
/*    */ 
/*    */   public ScoreDoc(int doc, float score, int shardIndex)
/*    */   {
/* 41 */     this.doc = doc;
/* 42 */     this.score = score;
/* 43 */     this.shardIndex = shardIndex;
/*    */   }
/*    */ 
/*    */   public String toString()
/*    */   {
/* 49 */     return "doc=" + this.doc + " score=" + this.score;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.search.ScoreDoc
 * JD-Core Version:    0.6.0
 */