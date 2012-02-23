/*     */ package org.apache.lucene.search;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import org.apache.lucene.index.IndexReader;
/*     */ import org.apache.lucene.search.spans.SpanQuery;
/*     */ import org.apache.lucene.search.spans.Spans;
/*     */ import org.apache.lucene.util.FixedBitSet;
/*     */ 
/*     */ public class SpanQueryFilter extends SpanFilter
/*     */ {
/*     */   protected SpanQuery query;
/*     */ 
/*     */   protected SpanQueryFilter()
/*     */   {
/*     */   }
/*     */ 
/*     */   public SpanQueryFilter(SpanQuery query)
/*     */   {
/*  51 */     this.query = query;
/*     */   }
/*     */ 
/*     */   public DocIdSet getDocIdSet(IndexReader reader) throws IOException
/*     */   {
/*  56 */     SpanFilterResult result = bitSpans(reader);
/*  57 */     return result.getDocIdSet();
/*     */   }
/*     */ 
/*     */   public SpanFilterResult bitSpans(IndexReader reader)
/*     */     throws IOException
/*     */   {
/*  63 */     FixedBitSet bits = new FixedBitSet(reader.maxDoc());
/*  64 */     Spans spans = this.query.getSpans(reader);
/*  65 */     List tmp = new ArrayList(20);
/*  66 */     int currentDoc = -1;
/*  67 */     SpanFilterResult.PositionInfo currentInfo = null;
/*  68 */     while (spans.next())
/*     */     {
/*  70 */       int doc = spans.doc();
/*  71 */       bits.set(doc);
/*  72 */       if (currentDoc != doc)
/*     */       {
/*  74 */         currentInfo = new SpanFilterResult.PositionInfo(doc);
/*  75 */         tmp.add(currentInfo);
/*  76 */         currentDoc = doc;
/*     */       }
/*  78 */       currentInfo.addPosition(spans.start(), spans.end());
/*     */     }
/*  80 */     return new SpanFilterResult(bits, tmp);
/*     */   }
/*     */ 
/*     */   public SpanQuery getQuery()
/*     */   {
/*  85 */     return this.query;
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/*  90 */     return "SpanQueryFilter(" + this.query + ")";
/*     */   }
/*     */ 
/*     */   public boolean equals(Object o)
/*     */   {
/*  95 */     return ((o instanceof SpanQueryFilter)) && (this.query.equals(((SpanQueryFilter)o).query));
/*     */   }
/*     */ 
/*     */   public int hashCode()
/*     */   {
/* 100 */     return this.query.hashCode() ^ 0x923F64B9;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.search.SpanQueryFilter
 * JD-Core Version:    0.6.0
 */