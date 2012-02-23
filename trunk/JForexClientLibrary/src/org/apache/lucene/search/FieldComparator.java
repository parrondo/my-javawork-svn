/*      */ package org.apache.lucene.search;
/*      */ 
/*      */ import java.io.IOException;
/*      */ import java.text.Collator;
/*      */ import java.util.Locale;
/*      */ import org.apache.lucene.index.IndexReader;
/*      */ 
/*      */ public abstract class FieldComparator<T>
/*      */ {
/*      */   protected T missingValue;
/*      */ 
/*      */   public FieldComparator()
/*      */   {
/*   87 */     this.missingValue = null;
/*      */   }
/*      */ 
/*      */   public FieldComparator<T> setMissingValue(T missingValue) {
/*   91 */     this.missingValue = missingValue;
/*   92 */     return this;
/*      */   }
/*      */ 
/*      */   public abstract int compare(int paramInt1, int paramInt2);
/*      */ 
/*      */   public abstract void setBottom(int paramInt);
/*      */ 
/*      */   public abstract int compareBottom(int paramInt)
/*      */     throws IOException;
/*      */ 
/*      */   public abstract void copy(int paramInt1, int paramInt2)
/*      */     throws IOException;
/*      */ 
/*      */   public abstract void setNextReader(IndexReader paramIndexReader, int paramInt)
/*      */     throws IOException;
/*      */ 
/*      */   public void setScorer(Scorer scorer)
/*      */   {
/*      */   }
/*      */ 
/*      */   public abstract T value(int paramInt);
/*      */ 
/*      */   public int compareValues(T first, T second)
/*      */   {
/*  181 */     return ((Comparable)first).compareTo(second);
/*      */   }
/*      */ 
/*      */   protected static final int binarySearch(String[] a, String key)
/*      */   {
/* 1017 */     return binarySearch(a, key, 0, a.length - 1);
/*      */   }
/*      */ 
/*      */   protected static final int binarySearch(String[] a, String key, int low, int high)
/*      */   {
/* 1022 */     while (low <= high) {
/* 1023 */       int mid = low + high >>> 1;
/* 1024 */       String midVal = a[mid];
/*      */       int cmp;
/*      */       int cmp;
/* 1026 */       if (midVal != null)
/* 1027 */         cmp = midVal.compareTo(key);
/*      */       else {
/* 1029 */         cmp = -1;
/*      */       }
/*      */ 
/* 1032 */       if (cmp < 0)
/* 1033 */         low = mid + 1;
/* 1034 */       else if (cmp > 0)
/* 1035 */         high = mid - 1;
/*      */       else
/* 1037 */         return mid;
/*      */     }
/* 1039 */     return -(low + 1);
/*      */   }
/*      */ 
/*      */   public static final class StringValComparator extends FieldComparator<String>
/*      */   {
/*      */     private String[] values;
/*      */     private String[] currentReaderValues;
/*      */     private final String field;
/*      */     private String bottom;
/*      */ 
/*      */     StringValComparator(int numHits, String field)
/*      */     {
/*  947 */       this.values = new String[numHits];
/*  948 */       this.field = field;
/*      */     }
/*      */ 
/*      */     public int compare(int slot1, int slot2)
/*      */     {
/*  953 */       String val1 = this.values[slot1];
/*  954 */       String val2 = this.values[slot2];
/*  955 */       if (val1 == null) {
/*  956 */         if (val2 == null) {
/*  957 */           return 0;
/*      */         }
/*  959 */         return -1;
/*  960 */       }if (val2 == null) {
/*  961 */         return 1;
/*      */       }
/*      */ 
/*  964 */       return val1.compareTo(val2);
/*      */     }
/*      */ 
/*      */     public int compareBottom(int doc)
/*      */     {
/*  969 */       String val2 = this.currentReaderValues[doc];
/*  970 */       if (this.bottom == null) {
/*  971 */         if (val2 == null) {
/*  972 */           return 0;
/*      */         }
/*  974 */         return -1;
/*  975 */       }if (val2 == null) {
/*  976 */         return 1;
/*      */       }
/*  978 */       return this.bottom.compareTo(val2);
/*      */     }
/*      */ 
/*      */     public void copy(int slot, int doc)
/*      */     {
/*  983 */       this.values[slot] = this.currentReaderValues[doc];
/*      */     }
/*      */ 
/*      */     public void setNextReader(IndexReader reader, int docBase) throws IOException
/*      */     {
/*  988 */       this.currentReaderValues = FieldCache.DEFAULT.getStrings(reader, this.field);
/*      */     }
/*      */ 
/*      */     public void setBottom(int bottom)
/*      */     {
/*  993 */       this.bottom = this.values[bottom];
/*      */     }
/*      */ 
/*      */     public String value(int slot)
/*      */     {
/*  998 */       return this.values[slot];
/*      */     }
/*      */ 
/*      */     public int compareValues(String val1, String val2)
/*      */     {
/* 1003 */       if (val1 == null) {
/* 1004 */         if (val2 == null) {
/* 1005 */           return 0;
/*      */         }
/* 1007 */         return -1;
/* 1008 */       }if (val2 == null) {
/* 1009 */         return 1;
/*      */       }
/* 1011 */       return val1.compareTo(val2);
/*      */     }
/*      */   }
/*      */ 
/*      */   public static final class StringOrdValComparator extends FieldComparator<String>
/*      */   {
/*      */     private final int[] ords;
/*      */     private final String[] values;
/*      */     private final int[] readerGen;
/*  786 */     private int currentReaderGen = -1;
/*      */     private String[] lookup;
/*      */     private int[] order;
/*      */     private final String field;
/*  791 */     private int bottomSlot = -1;
/*      */     private int bottomOrd;
/*      */     private boolean bottomSameReader;
/*      */     private String bottomValue;
/*      */ 
/*      */     public StringOrdValComparator(int numHits, String field, int sortPos, boolean reversed)
/*      */     {
/*  797 */       this.ords = new int[numHits];
/*  798 */       this.values = new String[numHits];
/*  799 */       this.readerGen = new int[numHits];
/*  800 */       this.field = field;
/*      */     }
/*      */ 
/*      */     public int compare(int slot1, int slot2)
/*      */     {
/*  805 */       if (this.readerGen[slot1] == this.readerGen[slot2]) {
/*  806 */         return this.ords[slot1] - this.ords[slot2];
/*      */       }
/*      */ 
/*  809 */       String val1 = this.values[slot1];
/*  810 */       String val2 = this.values[slot2];
/*  811 */       if (val1 == null) {
/*  812 */         if (val2 == null) {
/*  813 */           return 0;
/*      */         }
/*  815 */         return -1;
/*  816 */       }if (val2 == null) {
/*  817 */         return 1;
/*      */       }
/*  819 */       return val1.compareTo(val2);
/*      */     }
/*      */ 
/*      */     public int compareBottom(int doc)
/*      */     {
/*  824 */       assert (this.bottomSlot != -1);
/*  825 */       if (this.bottomSameReader)
/*      */       {
/*  827 */         return this.bottomOrd - this.order[doc];
/*      */       }
/*      */ 
/*  832 */       int order = this.order[doc];
/*  833 */       int cmp = this.bottomOrd - order;
/*  834 */       if (cmp != 0) {
/*  835 */         return cmp;
/*      */       }
/*      */ 
/*  838 */       String val2 = this.lookup[order];
/*  839 */       if (this.bottomValue == null) {
/*  840 */         if (val2 == null) {
/*  841 */           return 0;
/*      */         }
/*      */ 
/*  844 */         return -1;
/*  845 */       }if (val2 == null)
/*      */       {
/*  847 */         return 1;
/*      */       }
/*  849 */       return this.bottomValue.compareTo(val2);
/*      */     }
/*      */ 
/*      */     public void copy(int slot, int doc)
/*      */     {
/*  855 */       int ord = this.order[doc];
/*  856 */       this.ords[slot] = ord;
/*  857 */       assert (ord >= 0);
/*  858 */       this.values[slot] = this.lookup[ord];
/*  859 */       this.readerGen[slot] = this.currentReaderGen;
/*      */     }
/*      */ 
/*      */     public void setNextReader(IndexReader reader, int docBase) throws IOException
/*      */     {
/*  864 */       FieldCache.StringIndex currentReaderValues = FieldCache.DEFAULT.getStringIndex(reader, this.field);
/*  865 */       this.currentReaderGen += 1;
/*  866 */       this.order = currentReaderValues.order;
/*  867 */       this.lookup = currentReaderValues.lookup;
/*  868 */       assert (this.lookup.length > 0);
/*  869 */       if (this.bottomSlot != -1)
/*  870 */         setBottom(this.bottomSlot);
/*      */     }
/*      */ 
/*      */     public void setBottom(int bottom)
/*      */     {
/*  876 */       this.bottomSlot = bottom;
/*      */ 
/*  878 */       this.bottomValue = this.values[this.bottomSlot];
/*  879 */       if (this.currentReaderGen == this.readerGen[this.bottomSlot]) {
/*  880 */         this.bottomOrd = this.ords[this.bottomSlot];
/*  881 */         this.bottomSameReader = true;
/*      */       }
/*  883 */       else if (this.bottomValue == null) {
/*  884 */         this.ords[this.bottomSlot] = 0;
/*  885 */         this.bottomOrd = 0;
/*  886 */         this.bottomSameReader = true;
/*  887 */         this.readerGen[this.bottomSlot] = this.currentReaderGen;
/*      */       } else {
/*  889 */         int index = binarySearch(this.lookup, this.bottomValue);
/*  890 */         if (index < 0) {
/*  891 */           this.bottomOrd = (-index - 2);
/*  892 */           this.bottomSameReader = false;
/*      */         } else {
/*  894 */           this.bottomOrd = index;
/*      */ 
/*  896 */           this.bottomSameReader = true;
/*  897 */           this.readerGen[this.bottomSlot] = this.currentReaderGen;
/*  898 */           this.ords[this.bottomSlot] = this.bottomOrd;
/*      */         }
/*      */       }
/*      */     }
/*      */ 
/*      */     public String value(int slot)
/*      */     {
/*  906 */       return this.values[slot];
/*      */     }
/*      */ 
/*      */     public int compareValues(String val1, String val2)
/*      */     {
/*  911 */       if (val1 == null) {
/*  912 */         if (val2 == null) {
/*  913 */           return 0;
/*      */         }
/*  915 */         return -1;
/*  916 */       }if (val2 == null) {
/*  917 */         return 1;
/*      */       }
/*  919 */       return val1.compareTo(val2);
/*      */     }
/*      */ 
/*      */     public String[] getValues() {
/*  923 */       return this.values;
/*      */     }
/*      */ 
/*      */     public int getBottomSlot() {
/*  927 */       return this.bottomSlot;
/*      */     }
/*      */ 
/*      */     public String getField() {
/*  931 */       return this.field;
/*      */     }
/*      */   }
/*      */ 
/*      */   public static final class StringComparatorLocale extends FieldComparator<String>
/*      */   {
/*      */     private final String[] values;
/*      */     private String[] currentReaderValues;
/*      */     private final String field;
/*      */     final Collator collator;
/*      */     private String bottom;
/*      */ 
/*      */     StringComparatorLocale(int numHits, String field, Locale locale)
/*      */     {
/*  703 */       this.values = new String[numHits];
/*  704 */       this.field = field;
/*  705 */       this.collator = Collator.getInstance(locale);
/*      */     }
/*      */ 
/*      */     public int compare(int slot1, int slot2)
/*      */     {
/*  710 */       String val1 = this.values[slot1];
/*  711 */       String val2 = this.values[slot2];
/*  712 */       if (val1 == null) {
/*  713 */         if (val2 == null) {
/*  714 */           return 0;
/*      */         }
/*  716 */         return -1;
/*  717 */       }if (val2 == null) {
/*  718 */         return 1;
/*      */       }
/*  720 */       return this.collator.compare(val1, val2);
/*      */     }
/*      */ 
/*      */     public int compareBottom(int doc)
/*      */     {
/*  725 */       String val2 = this.currentReaderValues[doc];
/*  726 */       if (this.bottom == null) {
/*  727 */         if (val2 == null) {
/*  728 */           return 0;
/*      */         }
/*  730 */         return -1;
/*  731 */       }if (val2 == null) {
/*  732 */         return 1;
/*      */       }
/*  734 */       return this.collator.compare(this.bottom, val2);
/*      */     }
/*      */ 
/*      */     public void copy(int slot, int doc)
/*      */     {
/*  739 */       this.values[slot] = this.currentReaderValues[doc];
/*      */     }
/*      */ 
/*      */     public void setNextReader(IndexReader reader, int docBase) throws IOException
/*      */     {
/*  744 */       this.currentReaderValues = FieldCache.DEFAULT.getStrings(reader, this.field);
/*      */     }
/*      */ 
/*      */     public void setBottom(int bottom)
/*      */     {
/*  749 */       this.bottom = this.values[bottom];
/*      */     }
/*      */ 
/*      */     public String value(int slot)
/*      */     {
/*  754 */       return this.values[slot];
/*      */     }
/*      */ 
/*      */     public int compareValues(String val1, String val2)
/*      */     {
/*  759 */       if (val1 == null) {
/*  760 */         if (val2 == null) {
/*  761 */           return 0;
/*      */         }
/*  763 */         return -1;
/*  764 */       }if (val2 == null) {
/*  765 */         return 1;
/*      */       }
/*  767 */       return this.collator.compare(val1, val2);
/*      */     }
/*      */   }
/*      */ 
/*      */   public static final class ShortComparator extends FieldComparator<Short>
/*      */   {
/*      */     private final short[] values;
/*      */     private short[] currentReaderValues;
/*      */     private final String field;
/*      */     private FieldCache.ShortParser parser;
/*      */     private short bottom;
/*      */ 
/*      */     ShortComparator(int numHits, String field, FieldCache.Parser parser)
/*      */     {
/*  649 */       this.values = new short[numHits];
/*  650 */       this.field = field;
/*  651 */       this.parser = ((FieldCache.ShortParser)parser);
/*      */     }
/*      */ 
/*      */     public int compare(int slot1, int slot2)
/*      */     {
/*  656 */       return this.values[slot1] - this.values[slot2];
/*      */     }
/*      */ 
/*      */     public int compareBottom(int doc)
/*      */     {
/*  661 */       return this.bottom - this.currentReaderValues[doc];
/*      */     }
/*      */ 
/*      */     public void copy(int slot, int doc)
/*      */     {
/*  666 */       this.values[slot] = this.currentReaderValues[doc];
/*      */     }
/*      */ 
/*      */     public void setNextReader(IndexReader reader, int docBase) throws IOException
/*      */     {
/*  671 */       this.currentReaderValues = FieldCache.DEFAULT.getShorts(reader, this.field, this.parser);
/*  672 */       if (this.missingValue != null) {
/*  673 */         DocIdSetIterator iterator = FieldCache.DEFAULT.getUnValuedDocs(reader, this.field).iterator();
/*  674 */         short shortValue = ((Short)this.missingValue).shortValue();
/*  675 */         for (int doc = iterator.nextDoc(); doc != 2147483647; doc = iterator.nextDoc())
/*  676 */           this.currentReaderValues[doc] = shortValue;
/*      */       }
/*      */     }
/*      */ 
/*      */     public void setBottom(int bottom)
/*      */     {
/*  683 */       this.bottom = this.values[bottom];
/*      */     }
/*      */ 
/*      */     public Short value(int slot)
/*      */     {
/*  688 */       return Short.valueOf(this.values[slot]);
/*      */     }
/*      */   }
/*      */ 
/*      */   public static final class RelevanceComparator extends FieldComparator<Float>
/*      */   {
/*      */     private final float[] scores;
/*      */     private float bottom;
/*      */     private Scorer scorer;
/*      */ 
/*      */     RelevanceComparator(int numHits)
/*      */     {
/*  583 */       this.scores = new float[numHits];
/*      */     }
/*      */ 
/*      */     public int compare(int slot1, int slot2)
/*      */     {
/*  588 */       float score1 = this.scores[slot1];
/*  589 */       float score2 = this.scores[slot2];
/*  590 */       return score1 < score2 ? 1 : score1 > score2 ? -1 : 0;
/*      */     }
/*      */ 
/*      */     public int compareBottom(int doc) throws IOException
/*      */     {
/*  595 */       float score = this.scorer.score();
/*  596 */       return this.bottom < score ? 1 : this.bottom > score ? -1 : 0;
/*      */     }
/*      */ 
/*      */     public void copy(int slot, int doc) throws IOException
/*      */     {
/*  601 */       this.scores[slot] = this.scorer.score();
/*      */     }
/*      */ 
/*      */     public void setNextReader(IndexReader reader, int docBase)
/*      */     {
/*      */     }
/*      */ 
/*      */     public void setBottom(int bottom)
/*      */     {
/*  610 */       this.bottom = this.scores[bottom];
/*      */     }
/*      */ 
/*      */     public void setScorer(Scorer scorer)
/*      */     {
/*  618 */       if (!(scorer instanceof ScoreCachingWrappingScorer))
/*  619 */         this.scorer = new ScoreCachingWrappingScorer(scorer);
/*      */       else
/*  621 */         this.scorer = scorer;
/*      */     }
/*      */ 
/*      */     public Float value(int slot)
/*      */     {
/*  627 */       return Float.valueOf(this.scores[slot]);
/*      */     }
/*      */ 
/*      */     public int compareValues(Float first, Float second)
/*      */     {
/*  635 */       return second.compareTo(first);
/*      */     }
/*      */   }
/*      */ 
/*      */   public static final class LongComparator extends FieldComparator<Long>
/*      */   {
/*      */     private final long[] values;
/*      */     private long[] currentReaderValues;
/*      */     private final String field;
/*      */     private FieldCache.LongParser parser;
/*      */     private long bottom;
/*      */ 
/*      */     LongComparator(int numHits, String field, FieldCache.Parser parser)
/*      */     {
/*  509 */       this.values = new long[numHits];
/*  510 */       this.field = field;
/*  511 */       this.parser = ((FieldCache.LongParser)parser);
/*      */     }
/*      */ 
/*      */     public int compare(int slot1, int slot2)
/*      */     {
/*  518 */       long v1 = this.values[slot1];
/*  519 */       long v2 = this.values[slot2];
/*  520 */       if (v1 > v2)
/*  521 */         return 1;
/*  522 */       if (v1 < v2) {
/*  523 */         return -1;
/*      */       }
/*  525 */       return 0;
/*      */     }
/*      */ 
/*      */     public int compareBottom(int doc)
/*      */     {
/*  533 */       long v2 = this.currentReaderValues[doc];
/*  534 */       if (this.bottom > v2)
/*  535 */         return 1;
/*  536 */       if (this.bottom < v2) {
/*  537 */         return -1;
/*      */       }
/*  539 */       return 0;
/*      */     }
/*      */ 
/*      */     public void copy(int slot, int doc)
/*      */     {
/*  545 */       this.values[slot] = this.currentReaderValues[doc];
/*      */     }
/*      */ 
/*      */     public void setNextReader(IndexReader reader, int docBase) throws IOException
/*      */     {
/*  550 */       this.currentReaderValues = FieldCache.DEFAULT.getLongs(reader, this.field, this.parser);
/*  551 */       if (this.missingValue != null) {
/*  552 */         DocIdSetIterator iterator = FieldCache.DEFAULT.getUnValuedDocs(reader, this.field).iterator();
/*  553 */         long longValue = ((Long)this.missingValue).longValue();
/*  554 */         for (int doc = iterator.nextDoc(); doc != 2147483647; doc = iterator.nextDoc())
/*  555 */           this.currentReaderValues[doc] = longValue;
/*      */       }
/*      */     }
/*      */ 
/*      */     public void setBottom(int bottom)
/*      */     {
/*  562 */       this.bottom = this.values[bottom];
/*      */     }
/*      */ 
/*      */     public Long value(int slot)
/*      */     {
/*  567 */       return Long.valueOf(this.values[slot]);
/*      */     }
/*      */   }
/*      */ 
/*      */   public static final class IntComparator extends FieldComparator<Integer>
/*      */   {
/*      */     private final int[] values;
/*      */     private int[] currentReaderValues;
/*      */     private final String field;
/*      */     private FieldCache.IntParser parser;
/*      */     private int bottom;
/*      */ 
/*      */     IntComparator(int numHits, String field, FieldCache.Parser parser)
/*      */     {
/*  433 */       this.values = new int[numHits];
/*  434 */       this.field = field;
/*  435 */       this.parser = ((FieldCache.IntParser)parser);
/*      */     }
/*      */ 
/*      */     public int compare(int slot1, int slot2)
/*      */     {
/*  444 */       int v1 = this.values[slot1];
/*  445 */       int v2 = this.values[slot2];
/*  446 */       if (v1 > v2)
/*  447 */         return 1;
/*  448 */       if (v1 < v2) {
/*  449 */         return -1;
/*      */       }
/*  451 */       return 0;
/*      */     }
/*      */ 
/*      */     public int compareBottom(int doc)
/*      */     {
/*  461 */       int v2 = this.currentReaderValues[doc];
/*  462 */       if (this.bottom > v2)
/*  463 */         return 1;
/*  464 */       if (this.bottom < v2) {
/*  465 */         return -1;
/*      */       }
/*  467 */       return 0;
/*      */     }
/*      */ 
/*      */     public void copy(int slot, int doc)
/*      */     {
/*  473 */       this.values[slot] = this.currentReaderValues[doc];
/*      */     }
/*      */ 
/*      */     public void setNextReader(IndexReader reader, int docBase) throws IOException
/*      */     {
/*  478 */       this.currentReaderValues = FieldCache.DEFAULT.getInts(reader, this.field, this.parser);
/*  479 */       if (this.missingValue != null) {
/*  480 */         DocIdSetIterator iterator = FieldCache.DEFAULT.getUnValuedDocs(reader, this.field).iterator();
/*  481 */         int intValue = ((Integer)this.missingValue).intValue();
/*  482 */         for (int doc = iterator.nextDoc(); doc != 2147483647; doc = iterator.nextDoc())
/*  483 */           this.currentReaderValues[doc] = intValue;
/*      */       }
/*      */     }
/*      */ 
/*      */     public void setBottom(int bottom)
/*      */     {
/*  490 */       this.bottom = this.values[bottom];
/*      */     }
/*      */ 
/*      */     public Integer value(int slot)
/*      */     {
/*  495 */       return Integer.valueOf(this.values[slot]);
/*      */     }
/*      */   }
/*      */ 
/*      */   public static final class FloatComparator extends FieldComparator<Float>
/*      */   {
/*      */     private final float[] values;
/*      */     private float[] currentReaderValues;
/*      */     private final String field;
/*      */     private FieldCache.FloatParser parser;
/*      */     private float bottom;
/*      */ 
/*      */     FloatComparator(int numHits, String field, FieldCache.Parser parser)
/*      */     {
/*  361 */       this.values = new float[numHits];
/*  362 */       this.field = field;
/*  363 */       this.parser = ((FieldCache.FloatParser)parser);
/*      */     }
/*      */ 
/*      */     public int compare(int slot1, int slot2)
/*      */     {
/*  370 */       float v1 = this.values[slot1];
/*  371 */       float v2 = this.values[slot2];
/*  372 */       if (v1 > v2)
/*  373 */         return 1;
/*  374 */       if (v1 < v2) {
/*  375 */         return -1;
/*      */       }
/*  377 */       return 0;
/*      */     }
/*      */ 
/*      */     public int compareBottom(int doc)
/*      */     {
/*  385 */       float v2 = this.currentReaderValues[doc];
/*  386 */       if (this.bottom > v2)
/*  387 */         return 1;
/*  388 */       if (this.bottom < v2) {
/*  389 */         return -1;
/*      */       }
/*  391 */       return 0;
/*      */     }
/*      */ 
/*      */     public void copy(int slot, int doc)
/*      */     {
/*  397 */       this.values[slot] = this.currentReaderValues[doc];
/*      */     }
/*      */ 
/*      */     public void setNextReader(IndexReader reader, int docBase) throws IOException
/*      */     {
/*  402 */       this.currentReaderValues = FieldCache.DEFAULT.getFloats(reader, this.field, this.parser);
/*  403 */       if (this.missingValue != null) {
/*  404 */         DocIdSetIterator iterator = FieldCache.DEFAULT.getUnValuedDocs(reader, this.field).iterator();
/*  405 */         float floatValue = ((Float)this.missingValue).floatValue();
/*  406 */         for (int doc = iterator.nextDoc(); doc != 2147483647; doc = iterator.nextDoc())
/*  407 */           this.currentReaderValues[doc] = floatValue;
/*      */       }
/*      */     }
/*      */ 
/*      */     public void setBottom(int bottom)
/*      */     {
/*  414 */       this.bottom = this.values[bottom];
/*      */     }
/*      */ 
/*      */     public Float value(int slot)
/*      */     {
/*  419 */       return Float.valueOf(this.values[slot]);
/*      */     }
/*      */   }
/*      */ 
/*      */   public static final class DoubleComparator extends FieldComparator<Double>
/*      */   {
/*      */     private final double[] values;
/*      */     private double[] currentReaderValues;
/*      */     private final String field;
/*      */     private FieldCache.DoubleParser parser;
/*      */     private double bottom;
/*      */ 
/*      */     DoubleComparator(int numHits, String field, FieldCache.Parser parser)
/*      */     {
/*  293 */       this.values = new double[numHits];
/*  294 */       this.field = field;
/*  295 */       this.parser = ((FieldCache.DoubleParser)parser);
/*      */     }
/*      */ 
/*      */     public int compare(int slot1, int slot2)
/*      */     {
/*  300 */       double v1 = this.values[slot1];
/*  301 */       double v2 = this.values[slot2];
/*  302 */       if (v1 > v2)
/*  303 */         return 1;
/*  304 */       if (v1 < v2) {
/*  305 */         return -1;
/*      */       }
/*  307 */       return 0;
/*      */     }
/*      */ 
/*      */     public int compareBottom(int doc)
/*      */     {
/*  313 */       double v2 = this.currentReaderValues[doc];
/*  314 */       if (this.bottom > v2)
/*  315 */         return 1;
/*  316 */       if (this.bottom < v2) {
/*  317 */         return -1;
/*      */       }
/*  319 */       return 0;
/*      */     }
/*      */ 
/*      */     public void copy(int slot, int doc)
/*      */     {
/*  325 */       this.values[slot] = this.currentReaderValues[doc];
/*      */     }
/*      */ 
/*      */     public void setNextReader(IndexReader reader, int docBase) throws IOException
/*      */     {
/*  330 */       this.currentReaderValues = FieldCache.DEFAULT.getDoubles(reader, this.field, this.parser);
/*  331 */       if (this.missingValue != null) {
/*  332 */         DocIdSetIterator iterator = FieldCache.DEFAULT.getUnValuedDocs(reader, this.field).iterator();
/*  333 */         double doubleValue = ((Double)this.missingValue).doubleValue();
/*  334 */         for (int doc = iterator.nextDoc(); doc != 2147483647; doc = iterator.nextDoc())
/*  335 */           this.currentReaderValues[doc] = doubleValue;
/*      */       }
/*      */     }
/*      */ 
/*      */     public void setBottom(int bottom)
/*      */     {
/*  342 */       this.bottom = this.values[bottom];
/*      */     }
/*      */ 
/*      */     public Double value(int slot)
/*      */     {
/*  347 */       return Double.valueOf(this.values[slot]);
/*      */     }
/*      */   }
/*      */ 
/*      */   public static final class DocComparator extends FieldComparator<Integer>
/*      */   {
/*      */     private final int[] docIDs;
/*      */     private int docBase;
/*      */     private int bottom;
/*      */ 
/*      */     DocComparator(int numHits)
/*      */     {
/*  244 */       this.docIDs = new int[numHits];
/*      */     }
/*      */ 
/*      */     public int compare(int slot1, int slot2)
/*      */     {
/*  250 */       return this.docIDs[slot1] - this.docIDs[slot2];
/*      */     }
/*      */ 
/*      */     public int compareBottom(int doc)
/*      */     {
/*  256 */       return this.bottom - (this.docBase + doc);
/*      */     }
/*      */ 
/*      */     public void copy(int slot, int doc)
/*      */     {
/*  261 */       this.docIDs[slot] = (this.docBase + doc);
/*      */     }
/*      */ 
/*      */     public void setNextReader(IndexReader reader, int docBase)
/*      */     {
/*  269 */       this.docBase = docBase;
/*      */     }
/*      */ 
/*      */     public void setBottom(int bottom)
/*      */     {
/*  274 */       this.bottom = this.docIDs[bottom];
/*      */     }
/*      */ 
/*      */     public Integer value(int slot)
/*      */     {
/*  279 */       return Integer.valueOf(this.docIDs[slot]);
/*      */     }
/*      */   }
/*      */ 
/*      */   public static final class ByteComparator extends FieldComparator<Byte>
/*      */   {
/*      */     private final byte[] values;
/*      */     private byte[] currentReaderValues;
/*      */     private final String field;
/*      */     private FieldCache.ByteParser parser;
/*      */     private byte bottom;
/*      */ 
/*      */     ByteComparator(int numHits, String field, FieldCache.Parser parser)
/*      */     {
/*  194 */       this.values = new byte[numHits];
/*  195 */       this.field = field;
/*  196 */       this.parser = ((FieldCache.ByteParser)parser);
/*      */     }
/*      */ 
/*      */     public int compare(int slot1, int slot2)
/*      */     {
/*  201 */       return this.values[slot1] - this.values[slot2];
/*      */     }
/*      */ 
/*      */     public int compareBottom(int doc)
/*      */     {
/*  206 */       return this.bottom - this.currentReaderValues[doc];
/*      */     }
/*      */ 
/*      */     public void copy(int slot, int doc)
/*      */     {
/*  211 */       this.values[slot] = this.currentReaderValues[doc];
/*      */     }
/*      */ 
/*      */     public void setNextReader(IndexReader reader, int docBase) throws IOException
/*      */     {
/*  216 */       this.currentReaderValues = FieldCache.DEFAULT.getBytes(reader, this.field, this.parser);
/*  217 */       if (this.missingValue != null) {
/*  218 */         DocIdSetIterator iterator = FieldCache.DEFAULT.getUnValuedDocs(reader, this.field).iterator();
/*  219 */         byte byteValue = ((Byte)this.missingValue).byteValue();
/*  220 */         for (int doc = iterator.nextDoc(); doc != 2147483647; doc = iterator.nextDoc())
/*  221 */           this.currentReaderValues[doc] = byteValue;
/*      */       }
/*      */     }
/*      */ 
/*      */     public void setBottom(int bottom)
/*      */     {
/*  228 */       this.bottom = this.values[bottom];
/*      */     }
/*      */ 
/*      */     public Byte value(int slot)
/*      */     {
/*  233 */       return Byte.valueOf(this.values[slot]);
/*      */     }
/*      */   }
/*      */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.search.FieldComparator
 * JD-Core Version:    0.6.0
 */