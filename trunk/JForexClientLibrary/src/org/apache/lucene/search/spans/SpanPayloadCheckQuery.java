/*     */ package org.apache.lucene.search.spans;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.util.Arrays;
/*     */ import java.util.Collection;
/*     */ import java.util.Iterator;
/*     */ import org.apache.lucene.util.ToStringUtils;
/*     */ 
/*     */ public class SpanPayloadCheckQuery extends SpanPositionCheckQuery
/*     */ {
/*     */   protected final Collection<byte[]> payloadToMatch;
/*     */ 
/*     */   public SpanPayloadCheckQuery(SpanQuery match, Collection<byte[]> payloadToMatch)
/*     */   {
/*  45 */     super(match);
/*  46 */     if ((match instanceof SpanNearQuery)) {
/*  47 */       throw new IllegalArgumentException("SpanNearQuery not allowed");
/*     */     }
/*  49 */     this.payloadToMatch = payloadToMatch;
/*     */   }
/*     */ 
/*     */   protected SpanPositionCheckQuery.AcceptStatus acceptPosition(Spans spans) throws IOException
/*     */   {
/*  54 */     boolean result = spans.isPayloadAvailable();
/*  55 */     if (result == true) {
/*  56 */       Collection candidate = spans.getPayload();
/*  57 */       if (candidate.size() == this.payloadToMatch.size())
/*     */       {
/*  59 */         Iterator toMatchIter = this.payloadToMatch.iterator();
/*     */ 
/*  62 */         for (byte[] candBytes : candidate)
/*     */         {
/*  64 */           if (!Arrays.equals(candBytes, (byte[])toMatchIter.next())) {
/*  65 */             return SpanPositionCheckQuery.AcceptStatus.NO;
/*     */           }
/*     */         }
/*     */ 
/*  69 */         return SpanPositionCheckQuery.AcceptStatus.YES;
/*     */       }
/*  71 */       return SpanPositionCheckQuery.AcceptStatus.NO;
/*     */     }
/*     */ 
/*  74 */     return SpanPositionCheckQuery.AcceptStatus.YES;
/*     */   }
/*     */ 
/*     */   public String toString(String field)
/*     */   {
/*  79 */     StringBuilder buffer = new StringBuilder();
/*  80 */     buffer.append("spanPayCheck(");
/*  81 */     buffer.append(this.match.toString(field));
/*  82 */     buffer.append(", payloadRef: ");
/*  83 */     for (byte[] bytes : this.payloadToMatch) {
/*  84 */       ToStringUtils.byteArray(buffer, bytes);
/*  85 */       buffer.append(';');
/*     */     }
/*  87 */     buffer.append(")");
/*  88 */     buffer.append(ToStringUtils.boost(getBoost()));
/*  89 */     return buffer.toString();
/*     */   }
/*     */ 
/*     */   public Object clone()
/*     */   {
/*  94 */     SpanPayloadCheckQuery result = new SpanPayloadCheckQuery((SpanQuery)this.match.clone(), this.payloadToMatch);
/*  95 */     result.setBoost(getBoost());
/*  96 */     return result;
/*     */   }
/*     */ 
/*     */   public boolean equals(Object o)
/*     */   {
/* 101 */     if (this == o) return true;
/* 102 */     if (!(o instanceof SpanPayloadCheckQuery)) return false;
/*     */ 
/* 104 */     SpanPayloadCheckQuery other = (SpanPayloadCheckQuery)o;
/* 105 */     return (this.payloadToMatch.equals(other.payloadToMatch)) && (this.match.equals(other.match)) && (getBoost() == other.getBoost());
/*     */   }
/*     */ 
/*     */   public int hashCode()
/*     */   {
/* 112 */     int h = this.match.hashCode();
/* 113 */     h ^= (h << 8 | h >>> 25);
/*     */ 
/* 115 */     h ^= this.payloadToMatch.hashCode();
/* 116 */     h ^= Float.floatToRawIntBits(getBoost());
/* 117 */     return h;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.search.spans.SpanPayloadCheckQuery
 * JD-Core Version:    0.6.0
 */