/*     */ package org.apache.lucene.search.spans;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.util.Arrays;
/*     */ import java.util.Collection;
/*     */ import java.util.Iterator;
/*     */ import org.apache.lucene.util.ToStringUtils;
/*     */ 
/*     */ public class SpanNearPayloadCheckQuery extends SpanPositionCheckQuery
/*     */ {
/*     */   protected final Collection<byte[]> payloadToMatch;
/*     */ 
/*     */   public SpanNearPayloadCheckQuery(SpanNearQuery match, Collection<byte[]> payloadToMatch)
/*     */   {
/*  40 */     super(match);
/*  41 */     this.payloadToMatch = payloadToMatch;
/*     */   }
/*     */ 
/*     */   protected SpanPositionCheckQuery.AcceptStatus acceptPosition(Spans spans) throws IOException
/*     */   {
/*  46 */     boolean result = spans.isPayloadAvailable();
/*  47 */     if (result == true) {
/*  48 */       Collection candidate = spans.getPayload();
/*  49 */       if (candidate.size() == this.payloadToMatch.size())
/*     */       {
/*  52 */         int matches = 0;
/*  53 */         for (Iterator i$ = candidate.iterator(); i$.hasNext(); ) { candBytes = (byte[])i$.next();
/*     */ 
/*  55 */           for (byte[] payBytes : this.payloadToMatch)
/*  56 */             if (Arrays.equals(candBytes, payBytes) == true) {
/*  57 */               matches++;
/*  58 */               break;
/*     */             }
/*     */         }
/*     */         byte[] candBytes;
/*  62 */         if (matches == this.payloadToMatch.size())
/*     */         {
/*  64 */           return SpanPositionCheckQuery.AcceptStatus.YES;
/*     */         }
/*  66 */         return SpanPositionCheckQuery.AcceptStatus.NO;
/*     */       }
/*     */ 
/*  69 */       return SpanPositionCheckQuery.AcceptStatus.NO;
/*     */     }
/*     */ 
/*  72 */     return SpanPositionCheckQuery.AcceptStatus.NO;
/*     */   }
/*     */ 
/*     */   public String toString(String field)
/*     */   {
/*  77 */     StringBuilder buffer = new StringBuilder();
/*  78 */     buffer.append("spanPayCheck(");
/*  79 */     buffer.append(this.match.toString(field));
/*  80 */     buffer.append(", payloadRef: ");
/*  81 */     for (byte[] bytes : this.payloadToMatch) {
/*  82 */       ToStringUtils.byteArray(buffer, bytes);
/*  83 */       buffer.append(';');
/*     */     }
/*  85 */     buffer.append(")");
/*  86 */     buffer.append(ToStringUtils.boost(getBoost()));
/*  87 */     return buffer.toString();
/*     */   }
/*     */ 
/*     */   public Object clone()
/*     */   {
/*  92 */     SpanNearPayloadCheckQuery result = new SpanNearPayloadCheckQuery((SpanNearQuery)this.match.clone(), this.payloadToMatch);
/*  93 */     result.setBoost(getBoost());
/*  94 */     return result;
/*     */   }
/*     */ 
/*     */   public boolean equals(Object o)
/*     */   {
/*  99 */     if (this == o) return true;
/* 100 */     if (!(o instanceof SpanNearPayloadCheckQuery)) return false;
/*     */ 
/* 102 */     SpanNearPayloadCheckQuery other = (SpanNearPayloadCheckQuery)o;
/* 103 */     return (this.payloadToMatch.equals(other.payloadToMatch)) && (this.match.equals(other.match)) && (getBoost() == other.getBoost());
/*     */   }
/*     */ 
/*     */   public int hashCode()
/*     */   {
/* 110 */     int h = this.match.hashCode();
/* 111 */     h ^= (h << 8 | h >>> 25);
/*     */ 
/* 113 */     h ^= this.payloadToMatch.hashCode();
/* 114 */     h ^= Float.floatToRawIntBits(getBoost());
/* 115 */     return h;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.search.spans.SpanNearPayloadCheckQuery
 * JD-Core Version:    0.6.0
 */