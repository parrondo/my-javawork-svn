/*    */ package org.apache.lucene.search.spans;
/*    */ 
/*    */ import java.io.IOException;
/*    */ import org.apache.lucene.util.ToStringUtils;
/*    */ 
/*    */ public class SpanFirstQuery extends SpanPositionRangeQuery
/*    */ {
/*    */   public SpanFirstQuery(SpanQuery match, int end)
/*    */   {
/* 36 */     super(match, 0, end);
/*    */   }
/*    */ 
/*    */   protected SpanPositionCheckQuery.AcceptStatus acceptPosition(Spans spans) throws IOException
/*    */   {
/* 41 */     assert (spans.start() != spans.end());
/* 42 */     if (spans.start() >= this.end)
/* 43 */       return SpanPositionCheckQuery.AcceptStatus.NO_AND_ADVANCE;
/* 44 */     if (spans.end() <= this.end) {
/* 45 */       return SpanPositionCheckQuery.AcceptStatus.YES;
/*    */     }
/* 47 */     return SpanPositionCheckQuery.AcceptStatus.NO;
/*    */   }
/*    */ 
/*    */   public String toString(String field)
/*    */   {
/* 53 */     StringBuilder buffer = new StringBuilder();
/* 54 */     buffer.append("spanFirst(");
/* 55 */     buffer.append(this.match.toString(field));
/* 56 */     buffer.append(", ");
/* 57 */     buffer.append(this.end);
/* 58 */     buffer.append(")");
/* 59 */     buffer.append(ToStringUtils.boost(getBoost()));
/* 60 */     return buffer.toString();
/*    */   }
/*    */ 
/*    */   public Object clone()
/*    */   {
/* 65 */     SpanFirstQuery spanFirstQuery = new SpanFirstQuery((SpanQuery)this.match.clone(), this.end);
/* 66 */     spanFirstQuery.setBoost(getBoost());
/* 67 */     return spanFirstQuery;
/*    */   }
/*    */ 
/*    */   public boolean equals(Object o)
/*    */   {
/* 72 */     if (this == o) return true;
/* 73 */     if (!(o instanceof SpanFirstQuery)) return false;
/*    */ 
/* 75 */     SpanFirstQuery other = (SpanFirstQuery)o;
/* 76 */     return (this.end == other.end) && (this.match.equals(other.match)) && (getBoost() == other.getBoost());
/*    */   }
/*    */ 
/*    */   public int hashCode()
/*    */   {
/* 83 */     int h = this.match.hashCode();
/* 84 */     h ^= (h << 8 | h >>> 25);
/* 85 */     h ^= Float.floatToRawIntBits(getBoost()) ^ this.end;
/* 86 */     return h;
/*    */   }
/*    */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.search.spans.SpanFirstQuery
 * JD-Core Version:    0.6.0
 */