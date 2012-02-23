/*     */ package org.apache.lucene.analysis;
/*     */ 
/*     */ import java.util.AbstractSet;
/*     */ import java.util.Collection;
/*     */ import java.util.Iterator;
/*     */ import java.util.Set;
/*     */ import org.apache.lucene.util.Version;
/*     */ 
/*     */ public class CharArraySet extends AbstractSet<Object>
/*     */ {
/*  57 */   public static final CharArraySet EMPTY_SET = new CharArraySet(CharArrayMap.emptyMap());
/*  58 */   private static final Object PLACEHOLDER = new Object();
/*     */   private final CharArrayMap<Object> map;
/*     */ 
/*     */   public CharArraySet(Version matchVersion, int startSize, boolean ignoreCase)
/*     */   {
/*  75 */     this(new CharArrayMap(matchVersion, startSize, ignoreCase));
/*     */   }
/*     */ 
/*     */   public CharArraySet(Version matchVersion, Collection<?> c, boolean ignoreCase)
/*     */   {
/*  91 */     this(matchVersion, c.size(), ignoreCase);
/*  92 */     addAll(c);
/*     */   }
/*     */ 
/*     */   @Deprecated
/*     */   public CharArraySet(int startSize, boolean ignoreCase)
/*     */   {
/* 107 */     this(Version.LUCENE_30, startSize, ignoreCase);
/*     */   }
/*     */ 
/*     */   @Deprecated
/*     */   public CharArraySet(Collection<?> c, boolean ignoreCase)
/*     */   {
/* 122 */     this(Version.LUCENE_30, c.size(), ignoreCase);
/* 123 */     addAll(c);
/*     */   }
/*     */ 
/*     */   CharArraySet(CharArrayMap<Object> map)
/*     */   {
/* 128 */     this.map = map;
/*     */   }
/*     */ 
/*     */   public void clear()
/*     */   {
/* 134 */     this.map.clear();
/*     */   }
/*     */ 
/*     */   public boolean contains(char[] text, int off, int len)
/*     */   {
/* 140 */     return this.map.containsKey(text, off, len);
/*     */   }
/*     */ 
/*     */   public boolean contains(CharSequence cs)
/*     */   {
/* 145 */     return this.map.containsKey(cs);
/*     */   }
/*     */ 
/*     */   public boolean contains(Object o)
/*     */   {
/* 150 */     return this.map.containsKey(o);
/*     */   }
/*     */ 
/*     */   public boolean add(Object o)
/*     */   {
/* 155 */     return this.map.put(o, PLACEHOLDER) == null;
/*     */   }
/*     */ 
/*     */   public boolean add(CharSequence text)
/*     */   {
/* 160 */     return this.map.put(text, PLACEHOLDER) == null;
/*     */   }
/*     */ 
/*     */   public boolean add(String text)
/*     */   {
/* 165 */     return this.map.put(text, PLACEHOLDER) == null;
/*     */   }
/*     */ 
/*     */   public boolean add(char[] text)
/*     */   {
/* 173 */     return this.map.put(text, PLACEHOLDER) == null;
/*     */   }
/*     */ 
/*     */   public int size()
/*     */   {
/* 178 */     return this.map.size();
/*     */   }
/*     */ 
/*     */   public static CharArraySet unmodifiableSet(CharArraySet set)
/*     */   {
/* 192 */     if (set == null)
/* 193 */       throw new NullPointerException("Given set is null");
/* 194 */     if (set == EMPTY_SET)
/* 195 */       return EMPTY_SET;
/* 196 */     if ((set.map instanceof CharArrayMap.UnmodifiableCharArrayMap))
/* 197 */       return set;
/* 198 */     return new CharArraySet(CharArrayMap.unmodifiableMap(set.map));
/*     */   }
/*     */ 
/*     */   @Deprecated
/*     */   public static CharArraySet copy(Set<?> set)
/*     */   {
/* 214 */     if (set == EMPTY_SET)
/* 215 */       return EMPTY_SET;
/* 216 */     return copy(Version.LUCENE_30, set);
/*     */   }
/*     */ 
/*     */   public static CharArraySet copy(Version matchVersion, Set<?> set)
/*     */   {
/* 241 */     if (set == EMPTY_SET)
/* 242 */       return EMPTY_SET;
/* 243 */     if ((set instanceof CharArraySet)) {
/* 244 */       CharArraySet source = (CharArraySet)set;
/* 245 */       return new CharArraySet(CharArrayMap.copy(source.map.matchVersion, source.map));
/*     */     }
/* 247 */     return new CharArraySet(matchVersion, set, false);
/*     */   }
/*     */ 
/*     */   @Deprecated
/*     */   public Iterator<String> stringIterator()
/*     */   {
/* 295 */     return new CharArraySetIterator(null);
/*     */   }
/*     */ 
/*     */   public Iterator<Object> iterator()
/*     */   {
/* 310 */     return this.map.matchVersion.onOrAfter(Version.LUCENE_31) ? this.map.originalKeySet().iterator() : stringIterator();
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/* 316 */     StringBuilder sb = new StringBuilder("[");
/* 317 */     for (Iterator i$ = iterator(); i$.hasNext(); ) { Object item = i$.next();
/* 318 */       if (sb.length() > 1) sb.append(", ");
/* 319 */       if ((item instanceof char[]))
/* 320 */         sb.append((char[])(char[])item);
/*     */       else {
/* 322 */         sb.append(item);
/*     */       }
/*     */     }
/* 325 */     return ']';
/*     */   }
/*     */ 
/*     */   @Deprecated
/*     */   public class CharArraySetIterator
/*     */     implements Iterator<String>
/*     */   {
/* 256 */     int pos = -1;
/*     */     char[] next;
/*     */ 
/*     */     private CharArraySetIterator()
/*     */     {
/* 259 */       goNext();
/*     */     }
/*     */ 
/*     */     private void goNext() {
/* 263 */       this.next = null;
/* 264 */       this.pos += 1;
/* 265 */       while ((this.pos < CharArraySet.this.map.keys.length) && ((this.next = CharArraySet.this.map.keys[this.pos]) == null)) this.pos += 1; 
/*     */     }
/*     */ 
/*     */     public boolean hasNext()
/*     */     {
/* 269 */       return this.next != null;
/*     */     }
/*     */ 
/*     */     public char[] nextCharArray()
/*     */     {
/* 274 */       char[] ret = this.next;
/* 275 */       goNext();
/* 276 */       return ret;
/*     */     }
/*     */ 
/*     */     public String next()
/*     */     {
/* 282 */       return new String(nextCharArray());
/*     */     }
/*     */ 
/*     */     public void remove() {
/* 286 */       throw new UnsupportedOperationException();
/*     */     }
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.analysis.CharArraySet
 * JD-Core Version:    0.6.0
 */