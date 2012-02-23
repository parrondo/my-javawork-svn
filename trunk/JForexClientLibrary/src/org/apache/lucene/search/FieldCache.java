/*     */ package org.apache.lucene.search;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.PrintStream;
/*     */ import java.io.Serializable;
/*     */ import java.text.DecimalFormat;
/*     */ import org.apache.lucene.index.IndexReader;
/*     */ import org.apache.lucene.util.NumericUtils;
/*     */ import org.apache.lucene.util.RamUsageEstimator;
/*     */ 
/*     */ public abstract interface FieldCache
/*     */ {
/*     */   public static final int STRING_INDEX = -1;
/* 147 */   public static final FieldCache DEFAULT = new FieldCacheImpl();
/*     */ 
/* 150 */   public static final ByteParser DEFAULT_BYTE_PARSER = new ByteParser() {
/*     */     public byte parseByte(String value) {
/* 152 */       return Byte.parseByte(value);
/*     */     }
/*     */     protected Object readResolve() {
/* 155 */       return FieldCache.DEFAULT_BYTE_PARSER;
/*     */     }
/*     */ 
/*     */     public String toString() {
/* 159 */       return FieldCache.class.getName() + ".DEFAULT_BYTE_PARSER";
/*     */     }
/* 150 */   };
/*     */ 
/* 164 */   public static final ShortParser DEFAULT_SHORT_PARSER = new ShortParser() {
/*     */     public short parseShort(String value) {
/* 166 */       return Short.parseShort(value);
/*     */     }
/*     */     protected Object readResolve() {
/* 169 */       return FieldCache.DEFAULT_SHORT_PARSER;
/*     */     }
/*     */ 
/*     */     public String toString() {
/* 173 */       return FieldCache.class.getName() + ".DEFAULT_SHORT_PARSER";
/*     */     }
/* 164 */   };
/*     */ 
/* 178 */   public static final IntParser DEFAULT_INT_PARSER = new IntParser() {
/*     */     public int parseInt(String value) {
/* 180 */       return Integer.parseInt(value);
/*     */     }
/*     */     protected Object readResolve() {
/* 183 */       return FieldCache.DEFAULT_INT_PARSER;
/*     */     }
/*     */ 
/*     */     public String toString() {
/* 187 */       return FieldCache.class.getName() + ".DEFAULT_INT_PARSER";
/*     */     }
/* 178 */   };
/*     */ 
/* 192 */   public static final FloatParser DEFAULT_FLOAT_PARSER = new FloatParser() {
/*     */     public float parseFloat(String value) {
/* 194 */       return Float.parseFloat(value);
/*     */     }
/*     */     protected Object readResolve() {
/* 197 */       return FieldCache.DEFAULT_FLOAT_PARSER;
/*     */     }
/*     */ 
/*     */     public String toString() {
/* 201 */       return FieldCache.class.getName() + ".DEFAULT_FLOAT_PARSER";
/*     */     }
/* 192 */   };
/*     */ 
/* 206 */   public static final LongParser DEFAULT_LONG_PARSER = new LongParser() {
/*     */     public long parseLong(String value) {
/* 208 */       return Long.parseLong(value);
/*     */     }
/*     */     protected Object readResolve() {
/* 211 */       return FieldCache.DEFAULT_LONG_PARSER;
/*     */     }
/*     */ 
/*     */     public String toString() {
/* 215 */       return FieldCache.class.getName() + ".DEFAULT_LONG_PARSER";
/*     */     }
/* 206 */   };
/*     */ 
/* 220 */   public static final DoubleParser DEFAULT_DOUBLE_PARSER = new DoubleParser() {
/*     */     public double parseDouble(String value) {
/* 222 */       return Double.parseDouble(value);
/*     */     }
/*     */     protected Object readResolve() {
/* 225 */       return FieldCache.DEFAULT_DOUBLE_PARSER;
/*     */     }
/*     */ 
/*     */     public String toString() {
/* 229 */       return FieldCache.class.getName() + ".DEFAULT_DOUBLE_PARSER";
/*     */     }
/* 220 */   };
/*     */ 
/* 237 */   public static final IntParser NUMERIC_UTILS_INT_PARSER = new IntParser() {
/*     */     public int parseInt(String val) {
/* 239 */       int shift = val.charAt(0) - '`';
/* 240 */       if ((shift > 0) && (shift <= 31))
/* 241 */         throw new FieldCacheImpl.StopFillCacheException();
/* 242 */       return NumericUtils.prefixCodedToInt(val);
/*     */     }
/*     */     protected Object readResolve() {
/* 245 */       return FieldCache.NUMERIC_UTILS_INT_PARSER;
/*     */     }
/*     */ 
/*     */     public String toString() {
/* 249 */       return FieldCache.class.getName() + ".NUMERIC_UTILS_INT_PARSER";
/*     */     }
/* 237 */   };
/*     */ 
/* 257 */   public static final FloatParser NUMERIC_UTILS_FLOAT_PARSER = new FloatParser() {
/*     */     public float parseFloat(String val) {
/* 259 */       int shift = val.charAt(0) - '`';
/* 260 */       if ((shift > 0) && (shift <= 31))
/* 261 */         throw new FieldCacheImpl.StopFillCacheException();
/* 262 */       return NumericUtils.sortableIntToFloat(NumericUtils.prefixCodedToInt(val));
/*     */     }
/*     */     protected Object readResolve() {
/* 265 */       return FieldCache.NUMERIC_UTILS_FLOAT_PARSER;
/*     */     }
/*     */ 
/*     */     public String toString() {
/* 269 */       return FieldCache.class.getName() + ".NUMERIC_UTILS_FLOAT_PARSER";
/*     */     }
/* 257 */   };
/*     */ 
/* 277 */   public static final LongParser NUMERIC_UTILS_LONG_PARSER = new LongParser() {
/*     */     public long parseLong(String val) {
/* 279 */       int shift = val.charAt(0) - ' ';
/* 280 */       if ((shift > 0) && (shift <= 63))
/* 281 */         throw new FieldCacheImpl.StopFillCacheException();
/* 282 */       return NumericUtils.prefixCodedToLong(val);
/*     */     }
/*     */     protected Object readResolve() {
/* 285 */       return FieldCache.NUMERIC_UTILS_LONG_PARSER;
/*     */     }
/*     */ 
/*     */     public String toString() {
/* 289 */       return FieldCache.class.getName() + ".NUMERIC_UTILS_LONG_PARSER";
/*     */     }
/* 277 */   };
/*     */ 
/* 297 */   public static final DoubleParser NUMERIC_UTILS_DOUBLE_PARSER = new DoubleParser() {
/*     */     public double parseDouble(String val) {
/* 299 */       int shift = val.charAt(0) - ' ';
/* 300 */       if ((shift > 0) && (shift <= 63))
/* 301 */         throw new FieldCacheImpl.StopFillCacheException();
/* 302 */       return NumericUtils.sortableLongToDouble(NumericUtils.prefixCodedToLong(val));
/*     */     }
/*     */     protected Object readResolve() {
/* 305 */       return FieldCache.NUMERIC_UTILS_DOUBLE_PARSER;
/*     */     }
/*     */ 
/*     */     public String toString() {
/* 309 */       return FieldCache.class.getName() + ".NUMERIC_UTILS_DOUBLE_PARSER";
/*     */     }
/* 297 */   };
/*     */ 
/*     */   public abstract DocIdSet getUnValuedDocs(IndexReader paramIndexReader, String paramString)
/*     */     throws IOException;
/*     */ 
/*     */   public abstract byte[] getBytes(IndexReader paramIndexReader, String paramString)
/*     */     throws IOException;
/*     */ 
/*     */   public abstract byte[] getBytes(IndexReader paramIndexReader, String paramString, ByteParser paramByteParser)
/*     */     throws IOException;
/*     */ 
/*     */   public abstract short[] getShorts(IndexReader paramIndexReader, String paramString)
/*     */     throws IOException;
/*     */ 
/*     */   public abstract short[] getShorts(IndexReader paramIndexReader, String paramString, ShortParser paramShortParser)
/*     */     throws IOException;
/*     */ 
/*     */   public abstract int[] getInts(IndexReader paramIndexReader, String paramString)
/*     */     throws IOException;
/*     */ 
/*     */   public abstract int[] getInts(IndexReader paramIndexReader, String paramString, IntParser paramIntParser)
/*     */     throws IOException;
/*     */ 
/*     */   public abstract float[] getFloats(IndexReader paramIndexReader, String paramString)
/*     */     throws IOException;
/*     */ 
/*     */   public abstract float[] getFloats(IndexReader paramIndexReader, String paramString, FloatParser paramFloatParser)
/*     */     throws IOException;
/*     */ 
/*     */   public abstract long[] getLongs(IndexReader paramIndexReader, String paramString)
/*     */     throws IOException;
/*     */ 
/*     */   public abstract long[] getLongs(IndexReader paramIndexReader, String paramString, LongParser paramLongParser)
/*     */     throws IOException;
/*     */ 
/*     */   public abstract double[] getDoubles(IndexReader paramIndexReader, String paramString)
/*     */     throws IOException;
/*     */ 
/*     */   public abstract double[] getDoubles(IndexReader paramIndexReader, String paramString, DoubleParser paramDoubleParser)
/*     */     throws IOException;
/*     */ 
/*     */   public abstract String[] getStrings(IndexReader paramIndexReader, String paramString)
/*     */     throws IOException;
/*     */ 
/*     */   public abstract StringIndex getStringIndex(IndexReader paramIndexReader, String paramString)
/*     */     throws IOException;
/*     */ 
/*     */   public abstract CacheEntry[] getCacheEntries();
/*     */ 
/*     */   public abstract void purgeAllCaches();
/*     */ 
/*     */   public abstract void purge(IndexReader paramIndexReader);
/*     */ 
/*     */   public abstract void setInfoStream(PrintStream paramPrintStream);
/*     */ 
/*     */   public abstract PrintStream getInfoStream();
/*     */ 
/*     */   public static abstract class CacheEntry
/*     */   {
/* 515 */     private String size = null;
/*     */ 
/*     */     public abstract Object getReaderKey();
/*     */ 
/*     */     public abstract String getFieldName();
/*     */ 
/*     */     public abstract Class<?> getCacheType();
/*     */ 
/*     */     public abstract Object getCustom();
/*     */ 
/*     */     public abstract Object getValue();
/*     */ 
/* 517 */     protected final void setEstimatedSize(String size) { this.size = size;
/*     */     }
/*     */ 
/*     */     public void estimateSize()
/*     */     {
/* 523 */       estimateSize(new RamUsageEstimator(false));
/*     */     }
/*     */ 
/*     */     public void estimateSize(RamUsageEstimator ramCalc)
/*     */     {
/* 530 */       long size = ramCalc.estimateRamUsage(getValue());
/* 531 */       setEstimatedSize(RamUsageEstimator.humanReadableUnits(size, new DecimalFormat("0.#")));
/*     */     }
/*     */ 
/*     */     public final String getEstimatedSize()
/*     */     {
/* 540 */       return this.size;
/*     */     }
/*     */ 
/*     */     public String toString()
/*     */     {
/* 546 */       StringBuilder b = new StringBuilder();
/* 547 */       b.append("'").append(getReaderKey()).append("'=>");
/* 548 */       b.append("'").append(getFieldName()).append("',");
/* 549 */       b.append(getCacheType()).append(",").append(getCustom());
/* 550 */       b.append("=>").append(getValue().getClass().getName()).append("#");
/* 551 */       b.append(System.identityHashCode(getValue()));
/*     */ 
/* 553 */       String s = getEstimatedSize();
/* 554 */       if (null != s) {
/* 555 */         b.append(" (size =~ ").append(s).append(')');
/*     */       }
/*     */ 
/* 558 */       return b.toString();
/*     */     }
/*     */   }
/*     */ 
/*     */   public static abstract interface DoubleParser extends FieldCache.Parser
/*     */   {
/*     */     public abstract double parseDouble(String paramString);
/*     */   }
/*     */ 
/*     */   public static abstract interface LongParser extends FieldCache.Parser
/*     */   {
/*     */     public abstract long parseLong(String paramString);
/*     */   }
/*     */ 
/*     */   public static abstract interface FloatParser extends FieldCache.Parser
/*     */   {
/*     */     public abstract float parseFloat(String paramString);
/*     */   }
/*     */ 
/*     */   public static abstract interface IntParser extends FieldCache.Parser
/*     */   {
/*     */     public abstract int parseInt(String paramString);
/*     */   }
/*     */ 
/*     */   public static abstract interface ShortParser extends FieldCache.Parser
/*     */   {
/*     */     public abstract short parseShort(String paramString);
/*     */   }
/*     */ 
/*     */   public static abstract interface ByteParser extends FieldCache.Parser
/*     */   {
/*     */     public abstract byte parseByte(String paramString);
/*     */   }
/*     */ 
/*     */   public static abstract interface Parser extends Serializable
/*     */   {
/*     */   }
/*     */ 
/*     */   public static class StringIndex
/*     */   {
/*     */     public final String[] lookup;
/*     */     public final int[] order;
/*     */ 
/*     */     public int binarySearchLookup(String key)
/*     */     {
/*  57 */       if (key == null) {
/*  58 */         return 0;
/*     */       }
/*  60 */       int low = 1;
/*  61 */       int high = this.lookup.length - 1;
/*     */ 
/*  63 */       while (low <= high) {
/*  64 */         int mid = low + high >>> 1;
/*  65 */         int cmp = this.lookup[mid].compareTo(key);
/*     */ 
/*  67 */         if (cmp < 0)
/*  68 */           low = mid + 1;
/*  69 */         else if (cmp > 0)
/*  70 */           high = mid - 1;
/*     */         else
/*  72 */           return mid;
/*     */       }
/*  74 */       return -(low + 1);
/*     */     }
/*     */ 
/*     */     public StringIndex(int[] values, String[] lookup)
/*     */     {
/*  85 */       this.order = values;
/*  86 */       this.lookup = lookup;
/*     */     }
/*     */   }
/*     */ 
/*     */   public static final class CreationPlaceholder
/*     */   {
/*     */     Object value;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.search.FieldCache
 * JD-Core Version:    0.6.0
 */