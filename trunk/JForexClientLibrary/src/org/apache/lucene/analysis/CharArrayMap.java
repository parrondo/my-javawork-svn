/*     */ package org.apache.lucene.analysis;
/*     */ 
/*     */ import C;
/*     */ import java.util.AbstractMap;
/*     */ import java.util.AbstractSet;
/*     */ import java.util.Arrays;
/*     */ import java.util.Iterator;
/*     */ import java.util.Map;
/*     */ import java.util.Map.Entry;
/*     */ import java.util.Set;
/*     */ import org.apache.lucene.util.CharacterUtils;
/*     */ import org.apache.lucene.util.Version;
/*     */ 
/*     */ public class CharArrayMap<V> extends AbstractMap<Object, V>
/*     */ {
/*     */   private static final CharArrayMap<?> EMPTY_MAP;
/*     */   private static final int INIT_SIZE = 8;
/*     */   private final CharacterUtils charUtils;
/*     */   private boolean ignoreCase;
/*     */   private int count;
/*     */   final Version matchVersion;
/*     */   char[][] keys;
/*     */   V[] values;
/* 356 */   private CharArrayMap<V>.EntrySet entrySet = null;
/* 357 */   private CharArraySet keySet = null;
/*     */ 
/*     */   public CharArrayMap(Version matchVersion, int startSize, boolean ignoreCase)
/*     */   {
/*  76 */     this.ignoreCase = ignoreCase;
/*  77 */     int size = 8;
/*  78 */     while (startSize + (startSize >> 2) > size)
/*  79 */       size <<= 1;
/*  80 */     this.keys = new char[size][];
/*  81 */     this.values = ((Object[])new Object[size]);
/*  82 */     this.charUtils = CharacterUtils.getInstance(matchVersion);
/*  83 */     this.matchVersion = matchVersion;
/*     */   }
/*     */ 
/*     */   public CharArrayMap(Version matchVersion, Map<?, ? extends V> c, boolean ignoreCase)
/*     */   {
/*  99 */     this(matchVersion, c.size(), ignoreCase);
/* 100 */     putAll(c);
/*     */   }
/*     */ 
/*     */   private CharArrayMap(CharArrayMap<V> toCopy)
/*     */   {
/* 105 */     this.keys = toCopy.keys;
/* 106 */     this.values = toCopy.values;
/* 107 */     this.ignoreCase = toCopy.ignoreCase;
/* 108 */     this.count = toCopy.count;
/* 109 */     this.charUtils = toCopy.charUtils;
/* 110 */     this.matchVersion = toCopy.matchVersion;
/*     */   }
/*     */ 
/*     */   public void clear()
/*     */   {
/* 116 */     this.count = 0;
/* 117 */     Arrays.fill(this.keys, null);
/* 118 */     Arrays.fill(this.values, null);
/*     */   }
/*     */ 
/*     */   public boolean containsKey(char[] text, int off, int len)
/*     */   {
/* 124 */     return this.keys[getSlot(text, off, len)] != null;
/*     */   }
/*     */ 
/*     */   public boolean containsKey(CharSequence cs)
/*     */   {
/* 129 */     return this.keys[getSlot(cs)] != null;
/*     */   }
/*     */ 
/*     */   public boolean containsKey(Object o)
/*     */   {
/* 134 */     if ((o instanceof char[])) {
/* 135 */       char[] text = (char[])(char[])o;
/* 136 */       return containsKey(text, 0, text.length);
/*     */     }
/* 138 */     return containsKey(o.toString());
/*     */   }
/*     */ 
/*     */   public V get(char[] text, int off, int len)
/*     */   {
/* 144 */     return this.values[getSlot(text, off, len)];
/*     */   }
/*     */ 
/*     */   public V get(CharSequence cs)
/*     */   {
/* 149 */     return this.values[getSlot(cs)];
/*     */   }
/*     */ 
/*     */   public V get(Object o)
/*     */   {
/* 154 */     if ((o instanceof char[])) {
/* 155 */       char[] text = (char[])(char[])o;
/* 156 */       return get(text, 0, text.length);
/*     */     }
/* 158 */     return get(o.toString());
/*     */   }
/*     */ 
/*     */   private int getSlot(char[] text, int off, int len) {
/* 162 */     int code = getHashCode(text, off, len);
/* 163 */     int pos = code & this.keys.length - 1;
/* 164 */     char[] text2 = this.keys[pos];
/* 165 */     if ((text2 != null) && (!equals(text, off, len, text2))) {
/* 166 */       int inc = (code >> 8) + code | 0x1;
/*     */       do {
/* 168 */         code += inc;
/* 169 */         pos = code & this.keys.length - 1;
/* 170 */         text2 = this.keys[pos];
/* 171 */       }while ((text2 != null) && (!equals(text, off, len, text2)));
/*     */     }
/* 173 */     return pos;
/*     */   }
/*     */ 
/*     */   private int getSlot(CharSequence text)
/*     */   {
/* 178 */     int code = getHashCode(text);
/* 179 */     int pos = code & this.keys.length - 1;
/* 180 */     char[] text2 = this.keys[pos];
/* 181 */     if ((text2 != null) && (!equals(text, text2))) {
/* 182 */       int inc = (code >> 8) + code | 0x1;
/*     */       do {
/* 184 */         code += inc;
/* 185 */         pos = code & this.keys.length - 1;
/* 186 */         text2 = this.keys[pos];
/* 187 */       }while ((text2 != null) && (!equals(text, text2)));
/*     */     }
/* 189 */     return pos;
/*     */   }
/*     */ 
/*     */   public V put(CharSequence text, V value)
/*     */   {
/* 194 */     return put(text.toString(), value);
/*     */   }
/*     */ 
/*     */   public V put(Object o, V value)
/*     */   {
/* 199 */     if ((o instanceof char[])) {
/* 200 */       return put((char[])(char[])o, value);
/*     */     }
/* 202 */     return put(o.toString(), value);
/*     */   }
/*     */ 
/*     */   public V put(String text, V value)
/*     */   {
/* 207 */     return put(text.toCharArray(), value);
/*     */   }
/*     */ 
/*     */   public V put(char[] text, V value)
/*     */   {
/*     */     int i;
/* 215 */     if (this.ignoreCase) {
/* 216 */       for (i = 0; i < text.length; ) {
/* 217 */         i += Character.toChars(Character.toLowerCase(this.charUtils.codePointAt(text, i)), text, i);
/*     */       }
/*     */     }
/*     */ 
/* 221 */     int slot = getSlot(text, 0, text.length);
/* 222 */     if (this.keys[slot] != null) {
/* 223 */       Object oldValue = this.values[slot];
/* 224 */       this.values[slot] = value;
/* 225 */       return oldValue;
/*     */     }
/* 227 */     this.keys[slot] = text;
/* 228 */     this.values[slot] = value;
/* 229 */     this.count += 1;
/*     */ 
/* 231 */     if (this.count + (this.count >> 2) > this.keys.length) {
/* 232 */       rehash();
/*     */     }
/*     */ 
/* 235 */     return null;
/*     */   }
/*     */ 
/*     */   private void rehash()
/*     */   {
/* 240 */     assert (this.keys.length == this.values.length);
/* 241 */     int newSize = 2 * this.keys.length;
/* 242 */     char[][] oldkeys = this.keys;
/* 243 */     Object[] oldvalues = this.values;
/* 244 */     this.keys = new char[newSize][];
/* 245 */     this.values = ((Object[])new Object[newSize]);
/*     */ 
/* 247 */     for (int i = 0; i < oldkeys.length; i++) {
/* 248 */       char[] text = oldkeys[i];
/* 249 */       if (text == null)
/*     */         continue;
/* 251 */       int slot = getSlot(text, 0, text.length);
/* 252 */       this.keys[slot] = text;
/* 253 */       this.values[slot] = oldvalues[i];
/*     */     }
/*     */   }
/*     */ 
/*     */   private boolean equals(char[] text1, int off, int len, char[] text2)
/*     */   {
/* 259 */     if (len != text2.length)
/* 260 */       return false;
/* 261 */     int limit = off + len;
/*     */     int i;
/* 262 */     if (this.ignoreCase)
/* 263 */       for (i = 0; i < len; ) {
/* 264 */         int codePointAt = this.charUtils.codePointAt(text1, off + i, limit);
/* 265 */         if (Character.toLowerCase(codePointAt) != this.charUtils.codePointAt(text2, i))
/* 266 */           return false;
/* 267 */         i += Character.charCount(codePointAt);
/*     */       }
/*     */     else {
/* 270 */       for (int i = 0; i < len; i++) {
/* 271 */         if (text1[(off + i)] != text2[i])
/* 272 */           return false;
/*     */       }
/*     */     }
/* 275 */     return true;
/*     */   }
/*     */ 
/*     */   private boolean equals(CharSequence text1, char[] text2) {
/* 279 */     int len = text1.length();
/* 280 */     if (len != text2.length)
/* 281 */       return false;
/*     */     int i;
/* 282 */     if (this.ignoreCase)
/* 283 */       for (i = 0; i < len; ) {
/* 284 */         int codePointAt = this.charUtils.codePointAt(text1, i);
/* 285 */         if (Character.toLowerCase(codePointAt) != this.charUtils.codePointAt(text2, i))
/* 286 */           return false;
/* 287 */         i += Character.charCount(codePointAt);
/*     */       }
/*     */     else {
/* 290 */       for (int i = 0; i < len; i++) {
/* 291 */         if (text1.charAt(i) != text2[i])
/* 292 */           return false;
/*     */       }
/*     */     }
/* 295 */     return true;
/*     */   }
/*     */ 
/*     */   private int getHashCode(char[] text, int offset, int len) {
/* 299 */     if (text == null)
/* 300 */       throw new NullPointerException();
/* 301 */     int code = 0;
/* 302 */     int stop = offset + len;
/*     */     int i;
/* 303 */     if (this.ignoreCase)
/* 304 */       for (i = offset; i < stop; ) {
/* 305 */         int codePointAt = this.charUtils.codePointAt(text, i, stop);
/* 306 */         code = code * 31 + Character.toLowerCase(codePointAt);
/* 307 */         i += Character.charCount(codePointAt);
/*     */       }
/*     */     else {
/* 310 */       for (int i = offset; i < stop; i++) {
/* 311 */         code = code * 31 + text[i];
/*     */       }
/*     */     }
/* 314 */     return code;
/*     */   }
/*     */ 
/*     */   private int getHashCode(CharSequence text) {
/* 318 */     if (text == null)
/* 319 */       throw new NullPointerException();
/* 320 */     int code = 0;
/* 321 */     int len = text.length();
/*     */     int i;
/* 322 */     if (this.ignoreCase)
/* 323 */       for (i = 0; i < len; ) {
/* 324 */         int codePointAt = this.charUtils.codePointAt(text, i);
/* 325 */         code = code * 31 + Character.toLowerCase(codePointAt);
/* 326 */         i += Character.charCount(codePointAt);
/*     */       }
/*     */     else {
/* 329 */       for (int i = 0; i < len; i++) {
/* 330 */         code = code * 31 + text.charAt(i);
/*     */       }
/*     */     }
/* 333 */     return code;
/*     */   }
/*     */ 
/*     */   public V remove(Object key)
/*     */   {
/* 338 */     throw new UnsupportedOperationException();
/*     */   }
/*     */ 
/*     */   public int size()
/*     */   {
/* 343 */     return this.count;
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/* 348 */     StringBuilder sb = new StringBuilder("{");
/* 349 */     for (Map.Entry entry : entrySet()) {
/* 350 */       if (sb.length() > 1) sb.append(", ");
/* 351 */       sb.append(entry);
/*     */     }
/* 353 */     return '}';
/*     */   }
/*     */ 
/*     */   CharArrayMap<V>.EntrySet createEntrySet()
/*     */   {
/* 360 */     return new EntrySet(true, null);
/*     */   }
/*     */ 
/*     */   public final CharArrayMap<V>.EntrySet entrySet()
/*     */   {
/* 365 */     if (this.entrySet == null) {
/* 366 */       this.entrySet = createEntrySet();
/*     */     }
/* 368 */     return this.entrySet;
/*     */   }
/*     */ 
/*     */   final Set<Object> originalKeySet()
/*     */   {
/* 373 */     return super.keySet();
/*     */   }
/*     */ 
/*     */   public final CharArraySet keySet()
/*     */   {
/* 380 */     if (this.keySet == null)
/*     */     {
/* 382 */       this.keySet = new CharArraySet(this)
/*     */       {
/*     */         public boolean add(Object o) {
/* 385 */           throw new UnsupportedOperationException();
/*     */         }
/*     */ 
/*     */         public boolean add(CharSequence text) {
/* 389 */           throw new UnsupportedOperationException();
/*     */         }
/*     */ 
/*     */         public boolean add(String text) {
/* 393 */           throw new UnsupportedOperationException();
/*     */         }
/*     */ 
/*     */         public boolean add(char[] text) {
/* 397 */           throw new UnsupportedOperationException();
/*     */         } } ;
/*     */     }
/* 401 */     return this.keySet;
/*     */   }
/*     */ 
/*     */   public static <V> CharArrayMap<V> unmodifiableMap(CharArrayMap<V> map)
/*     */   {
/* 549 */     if (map == null)
/* 550 */       throw new NullPointerException("Given map is null");
/* 551 */     if ((map == emptyMap()) || (map.isEmpty()))
/* 552 */       return emptyMap();
/* 553 */     if ((map instanceof UnmodifiableCharArrayMap))
/* 554 */       return map;
/* 555 */     return new UnmodifiableCharArrayMap(map);
/*     */   }
/*     */ 
/*     */   public static <V> CharArrayMap<V> copy(Version matchVersion, Map<?, ? extends V> map)
/*     */   {
/* 581 */     if (map == EMPTY_MAP)
/* 582 */       return emptyMap();
/* 583 */     if ((map instanceof CharArrayMap)) {
/* 584 */       CharArrayMap m = (CharArrayMap)map;
/*     */ 
/* 587 */       char[][] keys = new char[m.keys.length][];
/* 588 */       System.arraycopy(m.keys, 0, keys, 0, keys.length);
/* 589 */       Object[] values = (Object[])new Object[m.values.length];
/* 590 */       System.arraycopy(m.values, 0, values, 0, values.length);
/* 591 */       m = new CharArrayMap(m);
/* 592 */       m.keys = keys;
/* 593 */       m.values = values;
/* 594 */       return m;
/*     */     }
/* 596 */     return new CharArrayMap(matchVersion, map, false);
/*     */   }
/*     */ 
/*     */   public static <V> CharArrayMap<V> emptyMap()
/*     */   {
/* 602 */     return EMPTY_MAP;
/*     */   }
/*     */ 
/*     */   static
/*     */   {
/*  52 */     EMPTY_MAP = new EmptyCharArrayMap();
/*     */   }
/*     */ 
/*     */   private static final class EmptyCharArrayMap<V> extends CharArrayMap.UnmodifiableCharArrayMap<V>
/*     */   {
/*     */     EmptyCharArrayMap()
/*     */     {
/* 655 */       super();
/*     */     }
/*     */ 
/*     */     public boolean containsKey(char[] text, int off, int len)
/*     */     {
/* 660 */       if (text == null)
/* 661 */         throw new NullPointerException();
/* 662 */       return false;
/*     */     }
/*     */ 
/*     */     public boolean containsKey(CharSequence cs)
/*     */     {
/* 667 */       if (cs == null)
/* 668 */         throw new NullPointerException();
/* 669 */       return false;
/*     */     }
/*     */ 
/*     */     public boolean containsKey(Object o)
/*     */     {
/* 674 */       if (o == null)
/* 675 */         throw new NullPointerException();
/* 676 */       return false;
/*     */     }
/*     */ 
/*     */     public V get(char[] text, int off, int len)
/*     */     {
/* 681 */       if (text == null)
/* 682 */         throw new NullPointerException();
/* 683 */       return null;
/*     */     }
/*     */ 
/*     */     public V get(CharSequence cs)
/*     */     {
/* 688 */       if (cs == null)
/* 689 */         throw new NullPointerException();
/* 690 */       return null;
/*     */     }
/*     */ 
/*     */     public V get(Object o)
/*     */     {
/* 695 */       if (o == null)
/* 696 */         throw new NullPointerException();
/* 697 */       return null;
/*     */     }
/*     */   }
/*     */ 
/*     */   static class UnmodifiableCharArrayMap<V> extends CharArrayMap<V>
/*     */   {
/*     */     UnmodifiableCharArrayMap(CharArrayMap<V> map)
/*     */     {
/* 609 */       super(null);
/*     */     }
/*     */ 
/*     */     public void clear()
/*     */     {
/* 614 */       throw new UnsupportedOperationException();
/*     */     }
/*     */ 
/*     */     public V put(Object o, V val)
/*     */     {
/* 619 */       throw new UnsupportedOperationException();
/*     */     }
/*     */ 
/*     */     public V put(char[] text, V val)
/*     */     {
/* 624 */       throw new UnsupportedOperationException();
/*     */     }
/*     */ 
/*     */     public V put(CharSequence text, V val)
/*     */     {
/* 629 */       throw new UnsupportedOperationException();
/*     */     }
/*     */ 
/*     */     public V put(String text, V val)
/*     */     {
/* 634 */       throw new UnsupportedOperationException();
/*     */     }
/*     */ 
/*     */     public V remove(Object key)
/*     */     {
/* 639 */       throw new UnsupportedOperationException();
/*     */     }
/*     */ 
/*     */     CharArrayMap<V>.EntrySet createEntrySet()
/*     */     {
/* 644 */       return new CharArrayMap.EntrySet(this, false, null);
/*     */     }
/*     */   }
/*     */ 
/*     */   public final class EntrySet extends AbstractSet<Map.Entry<Object, V>>
/*     */   {
/*     */     private final boolean allowModify;
/*     */ 
/*     */     private EntrySet(boolean allowModify)
/*     */     {
/* 501 */       this.allowModify = allowModify;
/*     */     }
/*     */ 
/*     */     public CharArrayMap<V>.EntryIterator iterator()
/*     */     {
/* 506 */       return new CharArrayMap.EntryIterator(CharArrayMap.this, this.allowModify, null);
/*     */     }
/*     */ 
/*     */     public boolean contains(Object o)
/*     */     {
/* 511 */       if (!(o instanceof Map.Entry))
/* 512 */         return false;
/* 513 */       Map.Entry e = (Map.Entry)o;
/* 514 */       Object key = e.getKey();
/* 515 */       Object val = e.getValue();
/* 516 */       Object v = CharArrayMap.this.get(key);
/* 517 */       return v == null ? false : val == null ? true : v.equals(val);
/*     */     }
/*     */ 
/*     */     public boolean remove(Object o)
/*     */     {
/* 522 */       throw new UnsupportedOperationException();
/*     */     }
/*     */ 
/*     */     public int size()
/*     */     {
/* 527 */       return CharArrayMap.this.count;
/*     */     }
/*     */ 
/*     */     public void clear()
/*     */     {
/* 532 */       if (!this.allowModify)
/* 533 */         throw new UnsupportedOperationException();
/* 534 */       CharArrayMap.this.clear();
/*     */     }
/*     */   }
/*     */ 
/*     */   private final class MapEntry
/*     */     implements Map.Entry<Object, V>
/*     */   {
/*     */     private final int pos;
/*     */     private final boolean allowModify;
/*     */ 
/*     */     private MapEntry(int pos, boolean allowModify)
/*     */     {
/* 466 */       this.pos = pos;
/* 467 */       this.allowModify = allowModify;
/*     */     }
/*     */ 
/*     */     public Object getKey()
/*     */     {
/* 473 */       return CharArrayMap.this.keys[this.pos].clone();
/*     */     }
/*     */ 
/*     */     public V getValue() {
/* 477 */       return CharArrayMap.this.values[this.pos];
/*     */     }
/*     */ 
/*     */     public V setValue(V value) {
/* 481 */       if (!this.allowModify)
/* 482 */         throw new UnsupportedOperationException();
/* 483 */       Object old = CharArrayMap.this.values[this.pos];
/* 484 */       CharArrayMap.this.values[this.pos] = value;
/* 485 */       return old;
/*     */     }
/*     */ 
/*     */     public String toString()
/*     */     {
/* 490 */       return CharArrayMap.this.keys[this.pos] + '=' + (CharArrayMap.this.values[this.pos] == CharArrayMap.this ? "(this Map)" : CharArrayMap.this.values[this.pos]);
/*     */     }
/*     */   }
/*     */ 
/*     */   public class EntryIterator
/*     */     implements Iterator<Map.Entry<Object, V>>
/*     */   {
/* 406 */     private int pos = -1;
/*     */     private int lastPos;
/*     */     private final boolean allowModify;
/*     */ 
/*     */     private EntryIterator(boolean allowModify)
/*     */     {
/* 411 */       this.allowModify = allowModify;
/* 412 */       goNext();
/*     */     }
/*     */ 
/*     */     private void goNext() {
/* 416 */       this.lastPos = this.pos;
/* 417 */       this.pos += 1;
/* 418 */       while ((this.pos < CharArrayMap.this.keys.length) && (CharArrayMap.this.keys[this.pos] == null)) this.pos += 1; 
/*     */     }
/*     */ 
/*     */     public boolean hasNext()
/*     */     {
/* 422 */       return this.pos < CharArrayMap.this.keys.length;
/*     */     }
/*     */ 
/*     */     public char[] nextKey()
/*     */     {
/* 427 */       goNext();
/* 428 */       return CharArrayMap.this.keys[this.lastPos];
/*     */     }
/*     */ 
/*     */     public String nextKeyString()
/*     */     {
/* 433 */       return new String(nextKey());
/*     */     }
/*     */ 
/*     */     public V currentValue()
/*     */     {
/* 438 */       return CharArrayMap.this.values[this.lastPos];
/*     */     }
/*     */ 
/*     */     public V setValue(V value)
/*     */     {
/* 443 */       if (!this.allowModify)
/* 444 */         throw new UnsupportedOperationException();
/* 445 */       Object old = CharArrayMap.this.values[this.lastPos];
/* 446 */       CharArrayMap.this.values[this.lastPos] = value;
/* 447 */       return old;
/*     */     }
/*     */ 
/*     */     public Map.Entry<Object, V> next()
/*     */     {
/* 452 */       goNext();
/* 453 */       return new CharArrayMap.MapEntry(CharArrayMap.this, this.lastPos, this.allowModify, null);
/*     */     }
/*     */ 
/*     */     public void remove() {
/* 457 */       throw new UnsupportedOperationException();
/*     */     }
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.analysis.CharArrayMap
 * JD-Core Version:    0.6.0
 */