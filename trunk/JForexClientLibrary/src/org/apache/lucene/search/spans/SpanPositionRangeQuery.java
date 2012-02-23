/*     */ package org.apache.lucene.search.spans;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import org.apache.lucene.util.ToStringUtils;
/*     */ 
/*     */ public class SpanPositionRangeQuery extends SpanPositionCheckQuery
/*     */ {
/*  31 */   protected int start = 0;
/*     */   protected int end;
/*     */ 
/*     */   public SpanPositionRangeQuery(SpanQuery match, int start, int end)
/*     */   {
/*  35 */     super(match);
/*  36 */     this.start = start;
/*  37 */     this.end = end;
/*     */   }
/*     */ 
/*     */   protected SpanPositionCheckQuery.AcceptStatus acceptPosition(Spans spans)
/*     */     throws IOException
/*     */   {
/*  43 */     assert (spans.start() != spans.end());
/*  44 */     if (spans.start() >= this.end)
/*  45 */       return SpanPositionCheckQuery.AcceptStatus.NO_AND_ADVANCE;
/*  46 */     if ((spans.start() >= this.start) && (spans.end() <= this.end)) {
/*  47 */       return SpanPositionCheckQuery.AcceptStatus.YES;
/*     */     }
/*  49 */     return SpanPositionCheckQuery.AcceptStatus.NO;
/*     */   }
/*     */ 
/*     */   public int getStart()
/*     */   {
/*  57 */     return this.start;
/*     */   }
/*     */ 
/*     */   public int getEnd()
/*     */   {
/*  64 */     return this.end;
/*     */   }
/*     */ 
/*     */   public String toString(String field)
/*     */   {
/*  69 */     StringBuilder buffer = new StringBuilder();
/*  70 */     buffer.append("spanPosRange(");
/*  71 */     buffer.append(this.match.toString(field));
/*  72 */     buffer.append(", ").append(this.start).append(", ");
/*  73 */     buffer.append(this.end);
/*  74 */     buffer.append(")");
/*  75 */     buffer.append(ToStringUtils.boost(getBoost()));
/*  76 */     return buffer.toString();
/*     */   }
/*     */ 
/*     */   public Object clone()
/*     */   {
/*  81 */     SpanPositionRangeQuery result = new SpanPositionRangeQuery((SpanQuery)this.match.clone(), this.start, this.end);
/*  82 */     result.setBoost(getBoost());
/*  83 */     return result;
/*     */   }
/*     */ 
/*     */   public boolean equals(Object o)
/*     */   {
/*  88 */     if (this == o) return true;
/*  89 */     if (!(o instanceof SpanPositionRangeQuery)) return false;
/*     */ 
/*  91 */     SpanPositionRangeQuery other = (SpanPositionRangeQuery)o;
/*  92 */     return (this.end == other.end) && (this.start == other.start) && (this.match.equals(other.match)) && (getBoost() == other.getBoost());
/*     */   }
/*     */ 
/*     */   public int hashCode()
/*     */   {
/*  99 */     int h = this.match.hashCode();
/* 100 */     h ^= (h << 8 | h >>> 25);
/* 101 */     h ^= Float.floatToRawIntBits(getBoost()) ^ this.end ^ this.start;
/* 102 */     return h;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.search.spans.SpanPositionRangeQuery
 * JD-Core Version:    0.6.0
 */