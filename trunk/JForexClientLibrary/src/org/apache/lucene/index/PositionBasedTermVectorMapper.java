/*     */ package org.apache.lucene.index;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ 
/*     */ public class PositionBasedTermVectorMapper extends TermVectorMapper
/*     */ {
/*     */   private Map<String, Map<Integer, TVPositionInfo>> fieldToTerms;
/*     */   private String currentField;
/*     */   private Map<Integer, TVPositionInfo> currentPositions;
/*     */   private boolean storeOffsets;
/*     */ 
/*     */   public PositionBasedTermVectorMapper()
/*     */   {
/*  47 */     super(false, false);
/*     */   }
/*     */ 
/*     */   public PositionBasedTermVectorMapper(boolean ignoringOffsets)
/*     */   {
/*  52 */     super(false, ignoringOffsets);
/*     */   }
/*     */ 
/*     */   public boolean isIgnoringPositions()
/*     */   {
/*  61 */     return false;
/*     */   }
/*     */ 
/*     */   public void map(String term, int frequency, TermVectorOffsetInfo[] offsets, int[] positions)
/*     */   {
/*  73 */     for (int i = 0; i < positions.length; i++) {
/*  74 */       Integer posVal = Integer.valueOf(positions[i]);
/*  75 */       TVPositionInfo pos = (TVPositionInfo)this.currentPositions.get(posVal);
/*  76 */       if (pos == null) {
/*  77 */         pos = new TVPositionInfo(positions[i], this.storeOffsets);
/*  78 */         this.currentPositions.put(posVal, pos);
/*     */       }
/*  80 */       pos.addTerm(term, offsets != null ? offsets[i] : null);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void setExpectations(String field, int numTerms, boolean storeOffsets, boolean storePositions)
/*     */   {
/*  93 */     if (!storePositions)
/*     */     {
/*  95 */       throw new RuntimeException("You must store positions in order to use this Mapper");
/*     */     }
/*  97 */     if (storeOffsets == true);
/* 101 */     this.fieldToTerms = new HashMap(numTerms);
/* 102 */     this.storeOffsets = storeOffsets;
/* 103 */     this.currentField = field;
/* 104 */     this.currentPositions = new HashMap();
/* 105 */     this.fieldToTerms.put(this.currentField, this.currentPositions);
/*     */   }
/*     */ 
/*     */   public Map<String, Map<Integer, TVPositionInfo>> getFieldToTerms()
/*     */   {
/* 114 */     return this.fieldToTerms;
/*     */   }
/*     */ 
/*     */   public static class TVPositionInfo
/*     */   {
/*     */     private int position;
/*     */     private List<String> terms;
/*     */     private List<TermVectorOffsetInfo> offsets;
/*     */ 
/*     */     public TVPositionInfo(int position, boolean storeOffsets)
/*     */     {
/* 129 */       this.position = position;
/* 130 */       this.terms = new ArrayList();
/* 131 */       if (storeOffsets)
/* 132 */         this.offsets = new ArrayList();
/*     */     }
/*     */ 
/*     */     void addTerm(String term, TermVectorOffsetInfo info)
/*     */     {
/* 138 */       this.terms.add(term);
/* 139 */       if (this.offsets != null)
/* 140 */         this.offsets.add(info);
/*     */     }
/*     */ 
/*     */     public int getPosition()
/*     */     {
/* 149 */       return this.position;
/*     */     }
/*     */ 
/*     */     public List<String> getTerms()
/*     */     {
/* 157 */       return this.terms;
/*     */     }
/*     */ 
/*     */     public List<TermVectorOffsetInfo> getOffsets()
/*     */     {
/* 165 */       return this.offsets;
/*     */     }
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.index.PositionBasedTermVectorMapper
 * JD-Core Version:    0.6.0
 */