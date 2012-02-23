/*     */ package org.apache.lucene.search;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ 
/*     */ public class SpanFilterResult
/*     */ {
/*     */   private DocIdSet docIdSet;
/*     */   private List<PositionInfo> positions;
/*     */ 
/*     */   public SpanFilterResult(DocIdSet docIdSet, List<PositionInfo> positions)
/*     */   {
/*  39 */     this.docIdSet = docIdSet;
/*  40 */     this.positions = positions;
/*     */   }
/*     */ 
/*     */   public List<PositionInfo> getPositions()
/*     */   {
/*  49 */     return this.positions;
/*     */   }
/*     */ 
/*     */   public DocIdSet getDocIdSet()
/*     */   {
/*  54 */     return this.docIdSet;
/*     */   }
/*     */ 
/*     */   public static class StartEnd
/*     */   {
/*     */     private int start;
/*     */     private int end;
/*     */ 
/*     */     public StartEnd(int start, int end)
/*     */     {
/*  92 */       this.start = start;
/*  93 */       this.end = end;
/*     */     }
/*     */ 
/*     */     public int getEnd()
/*     */     {
/* 101 */       return this.end;
/*     */     }
/*     */ 
/*     */     public int getStart()
/*     */     {
/* 109 */       return this.start;
/*     */     }
/*     */   }
/*     */ 
/*     */   public static class PositionInfo
/*     */   {
/*     */     private int doc;
/*     */     private List<SpanFilterResult.StartEnd> positions;
/*     */ 
/*     */     public PositionInfo(int doc)
/*     */     {
/*  63 */       this.doc = doc;
/*  64 */       this.positions = new ArrayList();
/*     */     }
/*     */ 
/*     */     public void addPosition(int start, int end)
/*     */     {
/*  69 */       this.positions.add(new SpanFilterResult.StartEnd(start, end));
/*     */     }
/*     */ 
/*     */     public int getDoc() {
/*  73 */       return this.doc;
/*     */     }
/*     */ 
/*     */     public List<SpanFilterResult.StartEnd> getPositions()
/*     */     {
/*  81 */       return this.positions;
/*     */     }
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.search.SpanFilterResult
 * JD-Core Version:    0.6.0
 */