/*     */ package org.apache.lucene.search;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import org.apache.lucene.util.PriorityQueue;
/*     */ 
/*     */ public abstract class FieldValueHitQueue<T extends Entry> extends PriorityQueue<T>
/*     */ {
/*     */   protected final SortField[] fields;
/*     */   protected final FieldComparator[] comparators;
/*     */   protected final int[] reverseMul;
/*     */ 
/*     */   private FieldValueHitQueue(SortField[] fields)
/*     */   {
/* 146 */     this.fields = fields;
/* 147 */     int numComparators = fields.length;
/* 148 */     this.comparators = new FieldComparator[numComparators];
/* 149 */     this.reverseMul = new int[numComparators];
/*     */   }
/*     */ 
/*     */   public static <T extends Entry> FieldValueHitQueue<T> create(SortField[] fields, int size)
/*     */     throws IOException
/*     */   {
/* 167 */     if (fields.length == 0) {
/* 168 */       throw new IllegalArgumentException("Sort must contain at least one field");
/*     */     }
/*     */ 
/* 171 */     if (fields.length == 1) {
/* 172 */       return new OneComparatorFieldValueHitQueue(fields, size);
/*     */     }
/* 174 */     return new MultiComparatorsFieldValueHitQueue(fields, size);
/*     */   }
/*     */ 
/*     */   public FieldComparator[] getComparators()
/*     */   {
/* 179 */     return this.comparators;
/*     */   }
/*     */ 
/*     */   public int[] getReverseMul() {
/* 183 */     return this.reverseMul;
/*     */   }
/*     */ 
/*     */   protected abstract boolean lessThan(Entry paramEntry1, Entry paramEntry2);
/*     */ 
/*     */   FieldDoc fillFields(Entry entry)
/*     */   {
/* 206 */     int n = this.comparators.length;
/* 207 */     Object[] fields = new Object[n];
/* 208 */     for (int i = 0; i < n; i++) {
/* 209 */       fields[i] = this.comparators[i].value(entry.slot);
/*     */     }
/*     */ 
/* 212 */     return new FieldDoc(entry.doc, entry.score, fields);
/*     */   }
/*     */ 
/*     */   SortField[] getFields()
/*     */   {
/* 217 */     return this.fields;
/*     */   }
/*     */ 
/*     */   private static final class MultiComparatorsFieldValueHitQueue<T extends FieldValueHitQueue.Entry> extends FieldValueHitQueue<T>
/*     */   {
/*     */     public MultiComparatorsFieldValueHitQueue(SortField[] fields, int size)
/*     */       throws IOException
/*     */     {
/* 104 */       super(null);
/*     */ 
/* 106 */       int numComparators = this.comparators.length;
/* 107 */       for (int i = 0; i < numComparators; i++) {
/* 108 */         SortField field = fields[i];
/*     */ 
/* 110 */         this.reverseMul[i] = (field.reverse ? -1 : 1);
/* 111 */         this.comparators[i] = field.getComparator(size, i);
/*     */       }
/*     */ 
/* 114 */       initialize(size);
/*     */     }
/*     */ 
/*     */     protected boolean lessThan(FieldValueHitQueue.Entry hitA, FieldValueHitQueue.Entry hitB)
/*     */     {
/* 120 */       assert (hitA != hitB);
/* 121 */       assert (hitA.slot != hitB.slot);
/*     */ 
/* 123 */       int numComparators = this.comparators.length;
/* 124 */       for (int i = 0; i < numComparators; i++) {
/* 125 */         int c = this.reverseMul[i] * this.comparators[i].compare(hitA.slot, hitB.slot);
/* 126 */         if (c != 0)
/*     */         {
/* 128 */           return c > 0;
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/* 133 */       return hitA.doc > hitB.doc;
/*     */     }
/*     */   }
/*     */ 
/*     */   private static final class OneComparatorFieldValueHitQueue<T extends FieldValueHitQueue.Entry> extends FieldValueHitQueue<T>
/*     */   {
/*     */     private final FieldComparator comparator;
/*     */     private final int oneReverseMul;
/*     */ 
/*     */     public OneComparatorFieldValueHitQueue(SortField[] fields, int size)
/*     */       throws IOException
/*     */     {
/*  61 */       super(null);
/*     */ 
/*  63 */       SortField field = fields[0];
/*  64 */       this.comparator = field.getComparator(size, 0);
/*  65 */       this.oneReverseMul = (field.reverse ? -1 : 1);
/*     */ 
/*  67 */       this.comparators[0] = this.comparator;
/*  68 */       this.reverseMul[0] = this.oneReverseMul;
/*     */ 
/*  70 */       initialize(size);
/*     */     }
/*     */ 
/*     */     protected boolean lessThan(FieldValueHitQueue.Entry hitA, FieldValueHitQueue.Entry hitB)
/*     */     {
/*  82 */       assert (hitA != hitB);
/*  83 */       assert (hitA.slot != hitB.slot);
/*     */ 
/*  85 */       int c = this.oneReverseMul * this.comparator.compare(hitA.slot, hitB.slot);
/*  86 */       if (c != 0) {
/*  87 */         return c > 0;
/*     */       }
/*     */ 
/*  91 */       return hitA.doc > hitB.doc;
/*     */     }
/*     */   }
/*     */ 
/*     */   public static class Entry extends ScoreDoc
/*     */   {
/*     */     public int slot;
/*     */ 
/*     */     public Entry(int slot, int doc, float score)
/*     */     {
/*  40 */       super(score);
/*  41 */       this.slot = slot;
/*     */     }
/*     */ 
/*     */     public String toString()
/*     */     {
/*  46 */       return "slot:" + this.slot + " " + super.toString();
/*     */     }
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.search.FieldValueHitQueue
 * JD-Core Version:    0.6.0
 */