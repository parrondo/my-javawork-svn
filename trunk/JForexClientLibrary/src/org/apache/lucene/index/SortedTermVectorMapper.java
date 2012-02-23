/*     */ package org.apache.lucene.index;
/*     */ 
/*     */ import java.util.Comparator;
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
/*     */ import java.util.SortedSet;
/*     */ import java.util.TreeSet;
/*     */ 
/*     */ public class SortedTermVectorMapper extends TermVectorMapper
/*     */ {
/*     */   private SortedSet<TermVectorEntry> currentSet;
/*  33 */   private Map<String, TermVectorEntry> termToTVE = new HashMap();
/*     */   private boolean storeOffsets;
/*     */   private boolean storePositions;
/*     */   public static final String ALL = "_ALL_";
/*     */ 
/*     */   public SortedTermVectorMapper(Comparator<TermVectorEntry> comparator)
/*     */   {
/*  46 */     this(false, false, comparator);
/*     */   }
/*     */ 
/*     */   public SortedTermVectorMapper(boolean ignoringPositions, boolean ignoringOffsets, Comparator<TermVectorEntry> comparator)
/*     */   {
/*  51 */     super(ignoringPositions, ignoringOffsets);
/*  52 */     this.currentSet = new TreeSet(comparator);
/*     */   }
/*     */ 
/*     */   public void map(String term, int frequency, TermVectorOffsetInfo[] offsets, int[] positions)
/*     */   {
/*  65 */     TermVectorEntry entry = (TermVectorEntry)this.termToTVE.get(term);
/*  66 */     if (entry == null) {
/*  67 */       entry = new TermVectorEntry("_ALL_", term, frequency, this.storeOffsets == true ? offsets : null, this.storePositions == true ? positions : null);
/*     */ 
/*  70 */       this.termToTVE.put(term, entry);
/*  71 */       this.currentSet.add(entry);
/*     */     } else {
/*  73 */       entry.setFrequency(entry.getFrequency() + frequency);
/*  74 */       if (this.storeOffsets)
/*     */       {
/*  76 */         TermVectorOffsetInfo[] existingOffsets = entry.getOffsets();
/*     */ 
/*  78 */         if ((existingOffsets != null) && (offsets != null) && (offsets.length > 0))
/*     */         {
/*  81 */           TermVectorOffsetInfo[] newOffsets = new TermVectorOffsetInfo[existingOffsets.length + offsets.length];
/*  82 */           System.arraycopy(existingOffsets, 0, newOffsets, 0, existingOffsets.length);
/*  83 */           System.arraycopy(offsets, 0, newOffsets, existingOffsets.length, offsets.length);
/*  84 */           entry.setOffsets(newOffsets);
/*     */         }
/*  86 */         else if ((existingOffsets == null) && (offsets != null) && (offsets.length > 0))
/*     */         {
/*  88 */           entry.setOffsets(offsets);
/*     */         }
/*     */       }
/*     */ 
/*  92 */       if (this.storePositions)
/*     */       {
/*  94 */         int[] existingPositions = entry.getPositions();
/*  95 */         if ((existingPositions != null) && (positions != null) && (positions.length > 0))
/*     */         {
/*  97 */           int[] newPositions = new int[existingPositions.length + positions.length];
/*  98 */           System.arraycopy(existingPositions, 0, newPositions, 0, existingPositions.length);
/*  99 */           System.arraycopy(positions, 0, newPositions, existingPositions.length, positions.length);
/* 100 */           entry.setPositions(newPositions);
/*     */         }
/* 102 */         else if ((existingPositions == null) && (positions != null) && (positions.length > 0))
/*     */         {
/* 104 */           entry.setPositions(positions);
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public void setExpectations(String field, int numTerms, boolean storeOffsets, boolean storePositions)
/*     */   {
/* 115 */     this.storeOffsets = storeOffsets;
/* 116 */     this.storePositions = storePositions;
/*     */   }
/*     */ 
/*     */   public SortedSet<TermVectorEntry> getTermVectorEntrySet()
/*     */   {
/* 128 */     return this.currentSet;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.index.SortedTermVectorMapper
 * JD-Core Version:    0.6.0
 */