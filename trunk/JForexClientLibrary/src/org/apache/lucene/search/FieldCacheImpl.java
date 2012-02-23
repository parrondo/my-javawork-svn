/*     */ package org.apache.lucene.search;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.PrintStream;
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Map.Entry;
/*     */ import java.util.WeakHashMap;
/*     */ import org.apache.lucene.index.IndexReader;
/*     */ import org.apache.lucene.index.IndexReader.ReaderFinishedListener;
/*     */ import org.apache.lucene.index.Term;
/*     */ import org.apache.lucene.index.TermDocs;
/*     */ import org.apache.lucene.index.TermEnum;
/*     */ import org.apache.lucene.util.FieldCacheSanityChecker;
/*     */ import org.apache.lucene.util.FieldCacheSanityChecker.Insanity;
/*     */ import org.apache.lucene.util.OpenBitSet;
/*     */ import org.apache.lucene.util.StringHelper;
/*     */ 
/*     */ class FieldCacheImpl
/*     */   implements FieldCache
/*     */ {
/*     */   private Map<Class<?>, Cache> caches;
/* 140 */   static final IndexReader.ReaderFinishedListener purgeReader = new IndexReader.ReaderFinishedListener()
/*     */   {
/*     */     public void finished(IndexReader reader) {
/* 143 */       FieldCache.DEFAULT.purge(reader);
/*     */     }
/* 140 */   };
/*     */   private volatile PrintStream infoStream;
/*     */ 
/*     */   FieldCacheImpl()
/*     */   {
/*  51 */     init();
/*     */   }
/*     */   private synchronized void init() {
/*  54 */     this.caches = new HashMap(9);
/*  55 */     this.caches.put(Byte.TYPE, new ByteCache(this));
/*  56 */     this.caches.put(Short.TYPE, new ShortCache(this));
/*  57 */     this.caches.put(Integer.TYPE, new IntCache(this));
/*  58 */     this.caches.put(Float.TYPE, new FloatCache(this));
/*  59 */     this.caches.put(Long.TYPE, new LongCache(this));
/*  60 */     this.caches.put(Double.TYPE, new DoubleCache(this));
/*  61 */     this.caches.put(String.class, new StringCache(this));
/*  62 */     this.caches.put(FieldCache.StringIndex.class, new StringIndexCache(this));
/*  63 */     this.caches.put(UnValuedDocsCache.class, new UnValuedDocsCache(this));
/*     */   }
/*     */ 
/*     */   public synchronized void purgeAllCaches() {
/*  67 */     init();
/*     */   }
/*     */ 
/*     */   public synchronized void purge(IndexReader r) {
/*  71 */     for (Cache c : this.caches.values())
/*  72 */       c.purge(r);
/*     */   }
/*     */ 
/*     */   public synchronized FieldCache.CacheEntry[] getCacheEntries()
/*     */   {
/*  77 */     List result = new ArrayList(17);
/*  78 */     for (Map.Entry cacheEntry : this.caches.entrySet()) {
/*  79 */       Cache cache = (Cache)cacheEntry.getValue();
/*  80 */       Class cacheType = (Class)cacheEntry.getKey();
/*     */       Object readerKey;
/*  81 */       synchronized (cache.readerCache) {
/*  82 */         for (Map.Entry readerCacheEntry : cache.readerCache.entrySet()) {
/*  83 */           readerKey = readerCacheEntry.getKey();
/*  84 */           if (readerKey != null) {
/*  85 */             Map innerCache = (Map)readerCacheEntry.getValue();
/*  86 */             for (Map.Entry mapEntry : innerCache.entrySet()) {
/*  87 */               Entry entry = (Entry)mapEntry.getKey();
/*  88 */               result.add(new CacheEntryImpl(readerKey, entry.field, cacheType, entry.custom, mapEntry.getValue()));
/*     */             }
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/*  95 */     return (FieldCache.CacheEntry[])result.toArray(new FieldCache.CacheEntry[result.size()]);
/*     */   }
/*     */ 
/*     */   public byte[] getBytes(IndexReader reader, String field)
/*     */     throws IOException
/*     */   {
/* 271 */     return getBytes(reader, field, null);
/*     */   }
/*     */ 
/*     */   public byte[] getBytes(IndexReader reader, String field, FieldCache.ByteParser parser)
/*     */     throws IOException
/*     */   {
/* 277 */     return (byte[])(byte[])((Cache)this.caches.get(Byte.TYPE)).get(reader, new Entry(field, parser));
/*     */   }
/*     */ 
/*     */   public short[] getShorts(IndexReader reader, String field)
/*     */     throws IOException
/*     */   {
/* 317 */     return getShorts(reader, field, null);
/*     */   }
/*     */ 
/*     */   public short[] getShorts(IndexReader reader, String field, FieldCache.ShortParser parser)
/*     */     throws IOException
/*     */   {
/* 323 */     return (short[])(short[])((Cache)this.caches.get(Short.TYPE)).get(reader, new Entry(field, parser));
/*     */   }
/*     */ 
/*     */   public int[] getInts(IndexReader reader, String field)
/*     */     throws IOException
/*     */   {
/* 364 */     return getInts(reader, field, null);
/*     */   }
/*     */ 
/*     */   public int[] getInts(IndexReader reader, String field, FieldCache.IntParser parser)
/*     */     throws IOException
/*     */   {
/* 370 */     return (int[])(int[])((Cache)this.caches.get(Integer.TYPE)).get(reader, new Entry(field, parser));
/*     */   }
/*     */ 
/*     */   public float[] getFloats(IndexReader reader, String field)
/*     */     throws IOException
/*     */   {
/* 457 */     return getFloats(reader, field, null);
/*     */   }
/*     */ 
/*     */   public float[] getFloats(IndexReader reader, String field, FieldCache.FloatParser parser)
/*     */     throws IOException
/*     */   {
/* 464 */     return (float[])(float[])((Cache)this.caches.get(Float.TYPE)).get(reader, new Entry(field, parser));
/*     */   }
/*     */ 
/*     */   public long[] getLongs(IndexReader reader, String field)
/*     */     throws IOException
/*     */   {
/* 513 */     return getLongs(reader, field, null);
/*     */   }
/*     */ 
/*     */   public long[] getLongs(IndexReader reader, String field, FieldCache.LongParser parser)
/*     */     throws IOException
/*     */   {
/* 519 */     return (long[])(long[])((Cache)this.caches.get(Long.TYPE)).get(reader, new Entry(field, parser));
/*     */   }
/*     */ 
/*     */   public double[] getDoubles(IndexReader reader, String field)
/*     */     throws IOException
/*     */   {
/* 568 */     return getDoubles(reader, field, null);
/*     */   }
/*     */ 
/*     */   public double[] getDoubles(IndexReader reader, String field, FieldCache.DoubleParser parser)
/*     */     throws IOException
/*     */   {
/* 574 */     return (double[])(double[])((Cache)this.caches.get(Double.TYPE)).get(reader, new Entry(field, parser));
/*     */   }
/*     */ 
/*     */   public String[] getStrings(IndexReader reader, String field)
/*     */     throws IOException
/*     */   {
/* 624 */     return (String[])(String[])((Cache)this.caches.get(String.class)).get(reader, new Entry(field, (FieldCache.Parser)null));
/*     */   }
/*     */ 
/*     */   public FieldCache.StringIndex getStringIndex(IndexReader reader, String field)
/*     */     throws IOException
/*     */   {
/* 669 */     return (FieldCache.StringIndex)((Cache)this.caches.get(FieldCache.StringIndex.class)).get(reader, new Entry(field, (FieldCache.Parser)null));
/*     */   }
/*     */ 
/*     */   public void setInfoStream(PrintStream stream)
/*     */   {
/* 733 */     this.infoStream = stream;
/*     */   }
/*     */ 
/*     */   public PrintStream getInfoStream() {
/* 737 */     return this.infoStream;
/*     */   }
/*     */ 
/*     */   public DocIdSet getUnValuedDocs(IndexReader reader, String field) throws IOException
/*     */   {
/* 742 */     return (DocIdSet)((Cache)this.caches.get(UnValuedDocsCache.class)).get(reader, new Entry(field, null));
/*     */   }
/*     */ 
/*     */   static final class StringIndexCache extends FieldCacheImpl.Cache
/*     */   {
/*     */     StringIndexCache(FieldCache wrapper)
/*     */     {
/* 674 */       super();
/*     */     }
/*     */ 
/*     */     protected Object createValue(IndexReader reader, FieldCacheImpl.Entry entryKey)
/*     */       throws IOException
/*     */     {
/* 680 */       String field = StringHelper.intern(entryKey.field);
/* 681 */       int[] retArray = new int[reader.maxDoc()];
/* 682 */       String[] mterms = new String[reader.maxDoc() + 1];
/* 683 */       TermDocs termDocs = reader.termDocs();
/* 684 */       TermEnum termEnum = reader.terms(new Term(field));
/* 685 */       int t = 0;
/*     */ 
/* 691 */       mterms[(t++)] = null;
/*     */       try
/*     */       {
/*     */         do {
/* 695 */           Term term = termEnum.term();
/* 696 */           if ((term == null) || (term.field() != field) || (t >= mterms.length)) {
/*     */             break;
/*     */           }
/* 699 */           mterms[t] = term.text();
/*     */ 
/* 701 */           termDocs.seek(termEnum);
/* 702 */           while (termDocs.next()) {
/* 703 */             retArray[termDocs.doc()] = t;
/*     */           }
/*     */ 
/* 706 */           t++;
/* 707 */         }while (termEnum.next());
/*     */       } finally {
/* 709 */         termDocs.close();
/* 710 */         termEnum.close();
/*     */       }
/*     */ 
/* 713 */       if (t == 0)
/*     */       {
/* 716 */         mterms = new String[1];
/* 717 */       } else if (t < mterms.length)
/*     */       {
/* 720 */         String[] terms = new String[t];
/* 721 */         System.arraycopy(mterms, 0, terms, 0, t);
/* 722 */         mterms = terms;
/*     */       }
/*     */ 
/* 725 */       FieldCache.StringIndex value = new FieldCache.StringIndex(retArray, mterms);
/* 726 */       return value;
/*     */     }
/*     */   }
/*     */ 
/*     */   static final class StringCache extends FieldCacheImpl.Cache
/*     */   {
/*     */     StringCache(FieldCache wrapper)
/*     */     {
/* 629 */       super();
/*     */     }
/*     */ 
/*     */     protected Object createValue(IndexReader reader, FieldCacheImpl.Entry entryKey)
/*     */       throws IOException
/*     */     {
/* 635 */       String field = StringHelper.intern(entryKey.field);
/* 636 */       String[] retArray = new String[reader.maxDoc()];
/* 637 */       TermDocs termDocs = reader.termDocs();
/* 638 */       TermEnum termEnum = reader.terms(new Term(field));
/* 639 */       int termCountHardLimit = reader.maxDoc();
/* 640 */       int termCount = 0;
/*     */       try {
/*     */         do {
/* 643 */           if (termCount++ == termCountHardLimit)
/*     */           {
/*     */             break;
/*     */           }
/*     */ 
/* 650 */           Term term = termEnum.term();
/* 651 */           if ((term == null) || (term.field() != field)) break;
/* 652 */           String termval = term.text();
/* 653 */           termDocs.seek(termEnum);
/* 654 */           while (termDocs.next())
/* 655 */             retArray[termDocs.doc()] = termval;
/*     */         }
/* 657 */         while (termEnum.next());
/*     */       } finally {
/* 659 */         termDocs.close();
/* 660 */         termEnum.close();
/*     */       }
/* 662 */       return retArray;
/*     */     }
/*     */   }
/*     */ 
/*     */   static final class DoubleCache extends FieldCacheImpl.Cache
/*     */   {
/*     */     DoubleCache(FieldCache wrapper)
/*     */     {
/* 579 */       super();
/*     */     }
/*     */ 
/*     */     protected Object createValue(IndexReader reader, FieldCacheImpl.Entry entryKey)
/*     */       throws IOException
/*     */     {
/* 585 */       FieldCacheImpl.Entry entry = entryKey;
/* 586 */       String field = entry.field;
/* 587 */       FieldCache.DoubleParser parser = (FieldCache.DoubleParser)entry.custom;
/* 588 */       if (parser == null) {
/*     */         try {
/* 590 */           return this.wrapper.getDoubles(reader, field, FieldCache.DEFAULT_DOUBLE_PARSER);
/*     */         } catch (NumberFormatException ne) {
/* 592 */           return this.wrapper.getDoubles(reader, field, FieldCache.NUMERIC_UTILS_DOUBLE_PARSER);
/*     */         }
/*     */       }
/* 595 */       double[] retArray = null;
/* 596 */       TermDocs termDocs = reader.termDocs();
/* 597 */       TermEnum termEnum = reader.terms(new Term(field));
/*     */       try {
/*     */         do {
/* 600 */           Term term = termEnum.term();
/* 601 */           if ((term == null) || (term.field() != field)) break;
/* 602 */           double termval = parser.parseDouble(term.text());
/* 603 */           if (retArray == null)
/* 604 */             retArray = new double[reader.maxDoc()];
/* 605 */           termDocs.seek(termEnum);
/* 606 */           while (termDocs.next())
/* 607 */             retArray[termDocs.doc()] = termval;
/*     */         }
/* 609 */         while (termEnum.next());
/*     */       } catch (FieldCacheImpl.StopFillCacheException stop) {
/*     */       } finally {
/* 612 */         termDocs.close();
/* 613 */         termEnum.close();
/*     */       }
/* 615 */       if (retArray == null)
/* 616 */         retArray = new double[reader.maxDoc()];
/* 617 */       return retArray;
/*     */     }
/*     */   }
/*     */ 
/*     */   static final class LongCache extends FieldCacheImpl.Cache
/*     */   {
/*     */     LongCache(FieldCache wrapper)
/*     */     {
/* 524 */       super();
/*     */     }
/*     */ 
/*     */     protected Object createValue(IndexReader reader, FieldCacheImpl.Entry entry)
/*     */       throws IOException
/*     */     {
/* 530 */       String field = entry.field;
/* 531 */       FieldCache.LongParser parser = (FieldCache.LongParser)entry.custom;
/* 532 */       if (parser == null) {
/*     */         try {
/* 534 */           return this.wrapper.getLongs(reader, field, FieldCache.DEFAULT_LONG_PARSER);
/*     */         } catch (NumberFormatException ne) {
/* 536 */           return this.wrapper.getLongs(reader, field, FieldCache.NUMERIC_UTILS_LONG_PARSER);
/*     */         }
/*     */       }
/* 539 */       long[] retArray = null;
/* 540 */       TermDocs termDocs = reader.termDocs();
/* 541 */       TermEnum termEnum = reader.terms(new Term(field));
/*     */       try {
/*     */         do {
/* 544 */           Term term = termEnum.term();
/* 545 */           if ((term == null) || (term.field() != field)) break;
/* 546 */           long termval = parser.parseLong(term.text());
/* 547 */           if (retArray == null)
/* 548 */             retArray = new long[reader.maxDoc()];
/* 549 */           termDocs.seek(termEnum);
/* 550 */           while (termDocs.next())
/* 551 */             retArray[termDocs.doc()] = termval;
/*     */         }
/* 553 */         while (termEnum.next());
/*     */       } catch (FieldCacheImpl.StopFillCacheException stop) {
/*     */       } finally {
/* 556 */         termDocs.close();
/* 557 */         termEnum.close();
/*     */       }
/* 559 */       if (retArray == null)
/* 560 */         retArray = new long[reader.maxDoc()];
/* 561 */       return retArray;
/*     */     }
/*     */   }
/*     */ 
/*     */   static final class FloatCache extends FieldCacheImpl.Cache
/*     */   {
/*     */     FloatCache(FieldCache wrapper)
/*     */     {
/* 469 */       super();
/*     */     }
/*     */ 
/*     */     protected Object createValue(IndexReader reader, FieldCacheImpl.Entry entryKey)
/*     */       throws IOException
/*     */     {
/* 475 */       FieldCacheImpl.Entry entry = entryKey;
/* 476 */       String field = entry.field;
/* 477 */       FieldCache.FloatParser parser = (FieldCache.FloatParser)entry.custom;
/* 478 */       if (parser == null) {
/*     */         try {
/* 480 */           return this.wrapper.getFloats(reader, field, FieldCache.DEFAULT_FLOAT_PARSER);
/*     */         } catch (NumberFormatException ne) {
/* 482 */           return this.wrapper.getFloats(reader, field, FieldCache.NUMERIC_UTILS_FLOAT_PARSER);
/*     */         }
/*     */       }
/* 485 */       float[] retArray = null;
/* 486 */       TermDocs termDocs = reader.termDocs();
/* 487 */       TermEnum termEnum = reader.terms(new Term(field));
/*     */       try {
/*     */         do {
/* 490 */           Term term = termEnum.term();
/* 491 */           if ((term == null) || (term.field() != field)) break;
/* 492 */           float termval = parser.parseFloat(term.text());
/* 493 */           if (retArray == null)
/* 494 */             retArray = new float[reader.maxDoc()];
/* 495 */           termDocs.seek(termEnum);
/* 496 */           while (termDocs.next())
/* 497 */             retArray[termDocs.doc()] = termval;
/*     */         }
/* 499 */         while (termEnum.next());
/*     */       } catch (FieldCacheImpl.StopFillCacheException stop) {
/*     */       } finally {
/* 502 */         termDocs.close();
/* 503 */         termEnum.close();
/*     */       }
/* 505 */       if (retArray == null)
/* 506 */         retArray = new float[reader.maxDoc()];
/* 507 */       return retArray;
/*     */     }
/*     */   }
/*     */ 
/*     */   static final class UnValuedDocsCache extends FieldCacheImpl.Cache
/*     */   {
/*     */     UnValuedDocsCache(FieldCache wrapper)
/*     */     {
/* 419 */       super();
/*     */     }
/*     */ 
/*     */     protected Object createValue(IndexReader reader, FieldCacheImpl.Entry entryKey)
/*     */       throws IOException
/*     */     {
/* 425 */       FieldCacheImpl.Entry entry = entryKey;
/* 426 */       String field = entry.field;
/*     */ 
/* 428 */       if (reader.maxDoc() == reader.docFreq(new Term(field))) {
/* 429 */         return DocIdSet.EMPTY_DOCIDSET;
/*     */       }
/*     */ 
/* 432 */       OpenBitSet res = new OpenBitSet(reader.maxDoc());
/* 433 */       TermDocs termDocs = reader.termDocs();
/* 434 */       TermEnum termEnum = reader.terms(new Term(field));
/*     */       try {
/*     */         do {
/* 437 */           Term term = termEnum.term();
/* 438 */           if ((term == null) || (term.field() != field)) break;
/* 439 */           termDocs.seek(termEnum);
/* 440 */           while (termDocs.next())
/* 441 */             res.fastSet(termDocs.doc());
/*     */         }
/* 443 */         while (termEnum.next());
/*     */       } finally {
/* 445 */         termDocs.close();
/* 446 */         termEnum.close();
/*     */       }
/* 448 */       res.flip(0L, reader.maxDoc());
/* 449 */       return res;
/*     */     }
/*     */   }
/*     */ 
/*     */   static final class IntCache extends FieldCacheImpl.Cache
/*     */   {
/*     */     IntCache(FieldCache wrapper)
/*     */     {
/* 375 */       super();
/*     */     }
/*     */ 
/*     */     protected Object createValue(IndexReader reader, FieldCacheImpl.Entry entryKey)
/*     */       throws IOException
/*     */     {
/* 381 */       FieldCacheImpl.Entry entry = entryKey;
/* 382 */       String field = entry.field;
/* 383 */       FieldCache.IntParser parser = (FieldCache.IntParser)entry.custom;
/* 384 */       if (parser == null) {
/*     */         try {
/* 386 */           return this.wrapper.getInts(reader, field, FieldCache.DEFAULT_INT_PARSER);
/*     */         } catch (NumberFormatException ne) {
/* 388 */           return this.wrapper.getInts(reader, field, FieldCache.NUMERIC_UTILS_INT_PARSER);
/*     */         }
/*     */       }
/* 391 */       int[] retArray = null;
/* 392 */       TermDocs termDocs = reader.termDocs();
/* 393 */       TermEnum termEnum = reader.terms(new Term(field));
/*     */       try {
/*     */         do {
/* 396 */           Term term = termEnum.term();
/* 397 */           if ((term == null) || (term.field() != field)) break;
/* 398 */           int termval = parser.parseInt(term.text());
/* 399 */           if (retArray == null)
/* 400 */             retArray = new int[reader.maxDoc()];
/* 401 */           termDocs.seek(termEnum);
/* 402 */           while (termDocs.next())
/* 403 */             retArray[termDocs.doc()] = termval;
/*     */         }
/* 405 */         while (termEnum.next());
/*     */       } catch (FieldCacheImpl.StopFillCacheException stop) {
/*     */       } finally {
/* 408 */         termDocs.close();
/* 409 */         termEnum.close();
/*     */       }
/* 411 */       if (retArray == null)
/* 412 */         retArray = new int[reader.maxDoc()];
/* 413 */       return retArray;
/*     */     }
/*     */   }
/*     */ 
/*     */   static final class ShortCache extends FieldCacheImpl.Cache
/*     */   {
/*     */     ShortCache(FieldCache wrapper)
/*     */     {
/* 328 */       super();
/*     */     }
/*     */ 
/*     */     protected Object createValue(IndexReader reader, FieldCacheImpl.Entry entryKey)
/*     */       throws IOException
/*     */     {
/* 334 */       FieldCacheImpl.Entry entry = entryKey;
/* 335 */       String field = entry.field;
/* 336 */       FieldCache.ShortParser parser = (FieldCache.ShortParser)entry.custom;
/* 337 */       if (parser == null) {
/* 338 */         return this.wrapper.getShorts(reader, field, FieldCache.DEFAULT_SHORT_PARSER);
/*     */       }
/* 340 */       short[] retArray = new short[reader.maxDoc()];
/* 341 */       TermDocs termDocs = reader.termDocs();
/* 342 */       TermEnum termEnum = reader.terms(new Term(field));
/*     */       try {
/*     */         do {
/* 345 */           Term term = termEnum.term();
/* 346 */           if ((term == null) || (term.field() != field)) break;
/* 347 */           short termval = parser.parseShort(term.text());
/* 348 */           termDocs.seek(termEnum);
/* 349 */           while (termDocs.next())
/* 350 */             retArray[termDocs.doc()] = termval;
/*     */         }
/* 352 */         while (termEnum.next());
/*     */       } catch (FieldCacheImpl.StopFillCacheException stop) {
/*     */       } finally {
/* 355 */         termDocs.close();
/* 356 */         termEnum.close();
/*     */       }
/* 358 */       return retArray;
/*     */     }
/*     */   }
/*     */ 
/*     */   static final class ByteCache extends FieldCacheImpl.Cache
/*     */   {
/*     */     ByteCache(FieldCache wrapper)
/*     */     {
/* 282 */       super();
/*     */     }
/*     */ 
/*     */     protected Object createValue(IndexReader reader, FieldCacheImpl.Entry entryKey) throws IOException
/*     */     {
/* 287 */       FieldCacheImpl.Entry entry = entryKey;
/* 288 */       String field = entry.field;
/* 289 */       FieldCache.ByteParser parser = (FieldCache.ByteParser)entry.custom;
/* 290 */       if (parser == null) {
/* 291 */         return this.wrapper.getBytes(reader, field, FieldCache.DEFAULT_BYTE_PARSER);
/*     */       }
/* 293 */       byte[] retArray = new byte[reader.maxDoc()];
/* 294 */       TermDocs termDocs = reader.termDocs();
/* 295 */       TermEnum termEnum = reader.terms(new Term(field));
/*     */       try {
/*     */         do {
/* 298 */           Term term = termEnum.term();
/* 299 */           if ((term == null) || (term.field() != field)) break;
/* 300 */           byte termval = parser.parseByte(term.text());
/* 301 */           termDocs.seek(termEnum);
/* 302 */           while (termDocs.next())
/* 303 */             retArray[termDocs.doc()] = termval;
/*     */         }
/* 305 */         while (termEnum.next());
/*     */       } catch (FieldCacheImpl.StopFillCacheException stop) {
/*     */       } finally {
/* 308 */         termDocs.close();
/* 309 */         termEnum.close();
/*     */       }
/* 311 */       return retArray;
/*     */     }
/*     */   }
/*     */ 
/*     */   static class Entry
/*     */   {
/*     */     final String field;
/*     */     final Object custom;
/*     */ 
/*     */     Entry(String field, Object custom)
/*     */     {
/* 242 */       this.field = StringHelper.intern(field);
/* 243 */       this.custom = custom;
/*     */     }
/*     */ 
/*     */     public boolean equals(Object o)
/*     */     {
/* 249 */       if ((o instanceof Entry)) {
/* 250 */         Entry other = (Entry)o;
/* 251 */         if (other.field == this.field) {
/* 252 */           if (other.custom == null) {
/* 253 */             if (this.custom == null) return true; 
/*     */           }
/* 254 */           else if (other.custom.equals(this.custom)) {
/* 255 */             return true;
/*     */           }
/*     */         }
/*     */       }
/* 259 */       return false;
/*     */     }
/*     */ 
/*     */     public int hashCode()
/*     */     {
/* 265 */       return this.field.hashCode() ^ (this.custom == null ? 0 : this.custom.hashCode());
/*     */     }
/*     */   }
/*     */ 
/*     */   static abstract class Cache
/*     */   {
/*     */     final FieldCache wrapper;
/* 159 */     final Map<Object, Map<FieldCacheImpl.Entry, Object>> readerCache = new WeakHashMap();
/*     */ 
/*     */     Cache()
/*     */     {
/* 150 */       this.wrapper = null;
/*     */     }
/*     */ 
/*     */     Cache(FieldCache wrapper) {
/* 154 */       this.wrapper = wrapper;
/*     */     }
/*     */ 
/*     */     protected abstract Object createValue(IndexReader paramIndexReader, FieldCacheImpl.Entry paramEntry)
/*     */       throws IOException;
/*     */ 
/*     */     public void purge(IndexReader r)
/*     */     {
/* 166 */       Object readerKey = r.getCoreCacheKey();
/* 167 */       synchronized (this.readerCache) {
/* 168 */         this.readerCache.remove(readerKey);
/*     */       }
/*     */     }
/*     */ 
/*     */     public Object get(IndexReader reader, FieldCacheImpl.Entry key)
/*     */       throws IOException
/*     */     {
/* 175 */       Object readerKey = reader.getCoreCacheKey();
/*     */       Map innerCache;
/*     */       Object value;
/* 176 */       synchronized (this.readerCache) {
/* 177 */         innerCache = (Map)this.readerCache.get(readerKey);
/*     */         Object value;
/* 178 */         if (innerCache == null)
/*     */         {
/* 180 */           innerCache = new HashMap();
/* 181 */           this.readerCache.put(readerKey, innerCache);
/* 182 */           reader.addReaderFinishedListener(FieldCacheImpl.purgeReader);
/* 183 */           value = null;
/*     */         } else {
/* 185 */           value = innerCache.get(key);
/*     */         }
/* 187 */         if (value == null) {
/* 188 */           value = new FieldCache.CreationPlaceholder();
/* 189 */           innerCache.put(key, value);
/*     */         }
/*     */       }
/* 192 */       if ((value instanceof FieldCache.CreationPlaceholder)) {
/* 193 */         synchronized (value) {
/* 194 */           FieldCache.CreationPlaceholder progress = (FieldCache.CreationPlaceholder)value;
/* 195 */           if (progress.value == null) {
/* 196 */             progress.value = createValue(reader, key);
/* 197 */             synchronized (this.readerCache) {
/* 198 */               innerCache.put(key, progress.value);
/*     */             }
/*     */ 
/* 204 */             if ((key.custom != null) && (this.wrapper != null)) {
/* 205 */               PrintStream infoStream = this.wrapper.getInfoStream();
/* 206 */               if (infoStream != null) {
/* 207 */                 printNewInsanity(infoStream, progress.value);
/*     */               }
/*     */             }
/*     */           }
/* 211 */           return progress.value;
/*     */         }
/*     */       }
/* 214 */       return value;
/*     */     }
/*     */ 
/*     */     private void printNewInsanity(PrintStream infoStream, Object value) {
/* 218 */       FieldCacheSanityChecker.Insanity[] insanities = FieldCacheSanityChecker.checkSanity(this.wrapper);
/* 219 */       for (int i = 0; i < insanities.length; i++) {
/* 220 */         FieldCacheSanityChecker.Insanity insanity = insanities[i];
/* 221 */         FieldCache.CacheEntry[] entries = insanity.getCacheEntries();
/* 222 */         for (int j = 0; j < entries.length; j++) {
/* 223 */           if (entries[j].getValue() != value)
/*     */             continue;
/* 225 */           infoStream.println("WARNING: new FieldCache insanity created\nDetails: " + insanity.toString());
/* 226 */           infoStream.println("\nStack:\n");
/* 227 */           new Throwable().printStackTrace(infoStream);
/* 228 */           break;
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   static final class StopFillCacheException extends RuntimeException
/*     */   {
/*     */   }
/*     */ 
/*     */   private static final class CacheEntryImpl extends FieldCache.CacheEntry
/*     */   {
/*     */     private final Object readerKey;
/*     */     private final String fieldName;
/*     */     private final Class<?> cacheType;
/*     */     private final Object custom;
/*     */     private final Object value;
/*     */ 
/*     */     CacheEntryImpl(Object readerKey, String fieldName, Class<?> cacheType, Object custom, Object value)
/*     */     {
/* 108 */       this.readerKey = readerKey;
/* 109 */       this.fieldName = fieldName;
/* 110 */       this.cacheType = cacheType;
/* 111 */       this.custom = custom;
/* 112 */       this.value = value;
/*     */     }
/*     */ 
/*     */     public Object getReaderKey()
/*     */     {
/* 121 */       return this.readerKey;
/*     */     }
/* 123 */     public String getFieldName() { return this.fieldName; } 
/*     */     public Class<?> getCacheType() {
/* 125 */       return this.cacheType;
/*     */     }
/* 127 */     public Object getCustom() { return this.custom; } 
/*     */     public Object getValue() {
/* 129 */       return this.value;
/*     */     }
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.search.FieldCacheImpl
 * JD-Core Version:    0.6.0
 */