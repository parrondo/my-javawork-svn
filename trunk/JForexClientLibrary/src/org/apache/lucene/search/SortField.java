/*     */ package org.apache.lucene.search;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.ObjectInputStream;
/*     */ import java.io.Serializable;
/*     */ import java.util.Locale;
/*     */ import org.apache.lucene.util.StringHelper;
/*     */ 
/*     */ public class SortField
/*     */   implements Serializable
/*     */ {
/*     */   public static final int SCORE = 0;
/*     */   public static final int DOC = 1;
/*     */   public static final int STRING = 3;
/*     */   public static final int INT = 4;
/*     */   public static final int FLOAT = 5;
/*     */   public static final int LONG = 6;
/*     */   public static final int DOUBLE = 7;
/*     */   public static final int SHORT = 8;
/*     */   public static final int CUSTOM = 9;
/*     */   public static final int BYTE = 10;
/*     */   public static final int STRING_VAL = 11;
/*     */   public static final SortField FIELD_SCORE;
/*     */   public static final SortField FIELD_DOC;
/*     */   private String field;
/*     */   private int type;
/*     */   private Locale locale;
/*  99 */   boolean reverse = false;
/*     */   private FieldCache.Parser parser;
/*     */   private FieldComparatorSource comparatorSource;
/*     */   private Object missingValue;
/*     */ 
/*     */   public SortField(String field, int type)
/*     */   {
/* 114 */     initFieldType(field, type);
/*     */   }
/*     */ 
/*     */   public SortField(String field, int type, boolean reverse)
/*     */   {
/* 125 */     initFieldType(field, type);
/* 126 */     this.reverse = reverse;
/*     */   }
/*     */ 
/*     */   public SortField(String field, FieldCache.Parser parser)
/*     */   {
/* 140 */     this(field, parser, false);
/*     */   }
/*     */ 
/*     */   public SortField(String field, FieldCache.Parser parser, boolean reverse)
/*     */   {
/* 155 */     if ((parser instanceof FieldCache.IntParser)) initFieldType(field, 4);
/* 156 */     else if ((parser instanceof FieldCache.FloatParser)) initFieldType(field, 5);
/* 157 */     else if ((parser instanceof FieldCache.ShortParser)) initFieldType(field, 8);
/* 158 */     else if ((parser instanceof FieldCache.ByteParser)) initFieldType(field, 10);
/* 159 */     else if ((parser instanceof FieldCache.LongParser)) initFieldType(field, 6);
/* 160 */     else if ((parser instanceof FieldCache.DoubleParser)) initFieldType(field, 7);
/*     */     else {
/* 162 */       throw new IllegalArgumentException("Parser instance does not subclass existing numeric parser from FieldCache (got " + parser + ")");
/*     */     }
/* 164 */     this.reverse = reverse;
/* 165 */     this.parser = parser;
/*     */   }
/*     */ 
/*     */   public SortField(String field, Locale locale)
/*     */   {
/* 174 */     initFieldType(field, 3);
/* 175 */     this.locale = locale;
/*     */   }
/*     */ 
/*     */   public SortField(String field, Locale locale, boolean reverse)
/*     */   {
/* 184 */     initFieldType(field, 3);
/* 185 */     this.locale = locale;
/* 186 */     this.reverse = reverse;
/*     */   }
/*     */ 
/*     */   public SortField(String field, FieldComparatorSource comparator)
/*     */   {
/* 194 */     initFieldType(field, 9);
/* 195 */     this.comparatorSource = comparator;
/*     */   }
/*     */ 
/*     */   public SortField(String field, FieldComparatorSource comparator, boolean reverse)
/*     */   {
/* 204 */     initFieldType(field, 9);
/* 205 */     this.reverse = reverse;
/* 206 */     this.comparatorSource = comparator;
/*     */   }
/*     */ 
/*     */   public SortField setMissingValue(Object missingValue)
/*     */   {
/* 211 */     if ((this.type != 10) && (this.type != 8) && (this.type != 4) && (this.type != 5) && (this.type != 6) && (this.type != 7)) {
/* 212 */       throw new IllegalArgumentException("Missing value only works for numeric types");
/*     */     }
/* 214 */     this.missingValue = missingValue;
/*     */ 
/* 216 */     return this;
/*     */   }
/*     */ 
/*     */   private void initFieldType(String field, int type)
/*     */   {
/* 222 */     this.type = type;
/* 223 */     if (field == null) {
/* 224 */       if ((type != 0) && (type != 1))
/* 225 */         throw new IllegalArgumentException("field can only be null when type is SCORE or DOC");
/*     */     }
/* 227 */     else this.field = StringHelper.intern(field);
/*     */   }
/*     */ 
/*     */   public String getField()
/*     */   {
/* 236 */     return this.field;
/*     */   }
/*     */ 
/*     */   public int getType()
/*     */   {
/* 243 */     return this.type;
/*     */   }
/*     */ 
/*     */   public Locale getLocale()
/*     */   {
/* 251 */     return this.locale;
/*     */   }
/*     */ 
/*     */   public FieldCache.Parser getParser()
/*     */   {
/* 259 */     return this.parser;
/*     */   }
/*     */ 
/*     */   public boolean getReverse()
/*     */   {
/* 266 */     return this.reverse;
/*     */   }
/*     */ 
/*     */   public FieldComparatorSource getComparatorSource()
/*     */   {
/* 273 */     return this.comparatorSource;
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/* 278 */     StringBuilder buffer = new StringBuilder();
/* 279 */     switch (this.type) {
/*     */     case 0:
/* 281 */       buffer.append("<score>");
/* 282 */       break;
/*     */     case 1:
/* 285 */       buffer.append("<doc>");
/* 286 */       break;
/*     */     case 3:
/* 289 */       buffer.append("<string: \"").append(this.field).append("\">");
/* 290 */       break;
/*     */     case 11:
/* 293 */       buffer.append("<string_val: \"").append(this.field).append("\">");
/* 294 */       break;
/*     */     case 10:
/* 297 */       buffer.append("<byte: \"").append(this.field).append("\">");
/* 298 */       break;
/*     */     case 8:
/* 301 */       buffer.append("<short: \"").append(this.field).append("\">");
/* 302 */       break;
/*     */     case 4:
/* 305 */       buffer.append("<int: \"").append(this.field).append("\">");
/* 306 */       break;
/*     */     case 6:
/* 309 */       buffer.append("<long: \"").append(this.field).append("\">");
/* 310 */       break;
/*     */     case 5:
/* 313 */       buffer.append("<float: \"").append(this.field).append("\">");
/* 314 */       break;
/*     */     case 7:
/* 317 */       buffer.append("<double: \"").append(this.field).append("\">");
/* 318 */       break;
/*     */     case 9:
/* 321 */       buffer.append("<custom:\"").append(this.field).append("\": ").append(this.comparatorSource).append('>');
/* 322 */       break;
/*     */     case 2:
/*     */     default:
/* 325 */       buffer.append("<???: \"").append(this.field).append("\">");
/*     */     }
/*     */ 
/* 329 */     if (this.locale != null) buffer.append('(').append(this.locale).append(')');
/* 330 */     if (this.parser != null) buffer.append('(').append(this.parser).append(')');
/* 331 */     if (this.reverse) buffer.append('!');
/*     */ 
/* 333 */     return buffer.toString();
/*     */   }
/*     */ 
/*     */   public boolean equals(Object o)
/*     */   {
/* 342 */     if (this == o) return true;
/* 343 */     if (!(o instanceof SortField)) return false;
/* 344 */     SortField other = (SortField)o;
/* 345 */     return (other.field == this.field) && (other.type == this.type) && (other.reverse == this.reverse) && (other.locale == null ? this.locale == null : other.locale.equals(this.locale)) && (other.comparatorSource == null ? this.comparatorSource == null : other.comparatorSource.equals(this.comparatorSource)) && (other.parser == null ? this.parser == null : other.parser.equals(this.parser));
/*     */   }
/*     */ 
/*     */   public int hashCode()
/*     */   {
/* 362 */     int hash = this.type ^ 879060445 + Boolean.valueOf(this.reverse).hashCode() ^ 0xAF5998BB;
/* 363 */     if (this.field != null) hash += (this.field.hashCode() ^ 0xFF5685DD);
/* 364 */     if (this.locale != null) hash += (this.locale.hashCode() ^ 0x8150815);
/* 365 */     if (this.comparatorSource != null) hash += this.comparatorSource.hashCode();
/* 366 */     if (this.parser != null) hash += (this.parser.hashCode() ^ 0x3AAF56FF);
/* 367 */     return hash;
/*     */   }
/*     */ 
/*     */   private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException
/*     */   {
/* 372 */     in.defaultReadObject();
/* 373 */     if (this.field != null)
/* 374 */       this.field = StringHelper.intern(this.field);
/*     */   }
/*     */ 
/*     */   public FieldComparator getComparator(int numHits, int sortPos)
/*     */     throws IOException
/*     */   {
/* 391 */     if (this.locale != null)
/*     */     {
/* 395 */       return new FieldComparator.StringComparatorLocale(numHits, this.field, this.locale);
/*     */     }
/*     */ 
/* 398 */     switch (this.type) {
/*     */     case 0:
/* 400 */       return new FieldComparator.RelevanceComparator(numHits);
/*     */     case 1:
/* 403 */       return new FieldComparator.DocComparator(numHits);
/*     */     case 4:
/* 406 */       return new FieldComparator.IntComparator(numHits, this.field, this.parser).setMissingValue((Integer)this.missingValue);
/*     */     case 5:
/* 409 */       return new FieldComparator.FloatComparator(numHits, this.field, this.parser).setMissingValue((Float)this.missingValue);
/*     */     case 6:
/* 412 */       return new FieldComparator.LongComparator(numHits, this.field, this.parser).setMissingValue((Long)this.missingValue);
/*     */     case 7:
/* 415 */       return new FieldComparator.DoubleComparator(numHits, this.field, this.parser).setMissingValue((Double)this.missingValue);
/*     */     case 10:
/* 418 */       return new FieldComparator.ByteComparator(numHits, this.field, this.parser).setMissingValue((Byte)this.missingValue);
/*     */     case 8:
/* 421 */       return new FieldComparator.ShortComparator(numHits, this.field, this.parser).setMissingValue((Short)this.missingValue);
/*     */     case 9:
/* 424 */       assert (this.comparatorSource != null);
/* 425 */       return this.comparatorSource.newComparator(this.field, numHits, sortPos, this.reverse);
/*     */     case 3:
/* 428 */       return new FieldComparator.StringOrdValComparator(numHits, this.field, sortPos, this.reverse);
/*     */     case 11:
/* 431 */       return new FieldComparator.StringValComparator(numHits, this.field);
/*     */     case 2:
/*     */     }
/* 434 */     throw new IllegalStateException("Illegal sort type: " + this.type);
/*     */   }
/*     */ 
/*     */   static
/*     */   {
/*  91 */     FIELD_SCORE = new SortField(null, 0);
/*     */ 
/*  94 */     FIELD_DOC = new SortField(null, 1);
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.search.SortField
 * JD-Core Version:    0.6.0
 */