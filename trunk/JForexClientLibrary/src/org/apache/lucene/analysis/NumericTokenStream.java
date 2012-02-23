/*     */ package org.apache.lucene.analysis;
/*     */ 
/*     */ import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
/*     */ import org.apache.lucene.analysis.tokenattributes.PositionIncrementAttribute;
/*     */ import org.apache.lucene.analysis.tokenattributes.TypeAttribute;
/*     */ import org.apache.lucene.util.AttributeSource;
/*     */ import org.apache.lucene.util.AttributeSource.AttributeFactory;
/*     */ import org.apache.lucene.util.NumericUtils;
/*     */ 
/*     */ public final class NumericTokenStream extends TokenStream
/*     */ {
/*     */   public static final String TOKEN_TYPE_FULL_PREC = "fullPrecNumeric";
/*     */   public static final String TOKEN_TYPE_LOWER_PREC = "lowerPrecNumeric";
/* 244 */   private final CharTermAttribute termAtt = (CharTermAttribute)addAttribute(CharTermAttribute.class);
/* 245 */   private final TypeAttribute typeAtt = (TypeAttribute)addAttribute(TypeAttribute.class);
/* 246 */   private final PositionIncrementAttribute posIncrAtt = (PositionIncrementAttribute)addAttribute(PositionIncrementAttribute.class);
/*     */ 
/* 248 */   private int shift = 0; private int valSize = 0;
/*     */   private final int precisionStep;
/* 251 */   private long value = 0L;
/*     */ 
/*     */   public NumericTokenStream()
/*     */   {
/*  99 */     this(4);
/*     */   }
/*     */ 
/*     */   public NumericTokenStream(int precisionStep)
/*     */   {
/* 109 */     this.precisionStep = precisionStep;
/* 110 */     if (precisionStep < 1)
/* 111 */       throw new IllegalArgumentException("precisionStep must be >=1");
/*     */   }
/*     */ 
/*     */   public NumericTokenStream(AttributeSource source, int precisionStep)
/*     */   {
/* 121 */     super(source);
/* 122 */     this.precisionStep = precisionStep;
/* 123 */     if (precisionStep < 1)
/* 124 */       throw new IllegalArgumentException("precisionStep must be >=1");
/*     */   }
/*     */ 
/*     */   public NumericTokenStream(AttributeSource.AttributeFactory factory, int precisionStep)
/*     */   {
/* 135 */     super(factory);
/* 136 */     this.precisionStep = precisionStep;
/* 137 */     if (precisionStep < 1)
/* 138 */       throw new IllegalArgumentException("precisionStep must be >=1");
/*     */   }
/*     */ 
/*     */   public NumericTokenStream setLongValue(long value)
/*     */   {
/* 148 */     this.value = value;
/* 149 */     this.valSize = 64;
/* 150 */     this.shift = 0;
/* 151 */     return this;
/*     */   }
/*     */ 
/*     */   public NumericTokenStream setIntValue(int value)
/*     */   {
/* 161 */     this.value = value;
/* 162 */     this.valSize = 32;
/* 163 */     this.shift = 0;
/* 164 */     return this;
/*     */   }
/*     */ 
/*     */   public NumericTokenStream setDoubleValue(double value)
/*     */   {
/* 174 */     this.value = NumericUtils.doubleToSortableLong(value);
/* 175 */     this.valSize = 64;
/* 176 */     this.shift = 0;
/* 177 */     return this;
/*     */   }
/*     */ 
/*     */   public NumericTokenStream setFloatValue(float value)
/*     */   {
/* 187 */     this.value = NumericUtils.floatToSortableInt(value);
/* 188 */     this.valSize = 32;
/* 189 */     this.shift = 0;
/* 190 */     return this;
/*     */   }
/*     */ 
/*     */   public void reset()
/*     */   {
/* 195 */     if (this.valSize == 0)
/* 196 */       throw new IllegalStateException("call set???Value() before usage");
/* 197 */     this.shift = 0;
/*     */   }
/*     */ 
/*     */   public boolean incrementToken()
/*     */   {
/* 202 */     if (this.valSize == 0)
/* 203 */       throw new IllegalStateException("call set???Value() before usage");
/* 204 */     if (this.shift >= this.valSize) {
/* 205 */       return false;
/*     */     }
/* 207 */     clearAttributes();
/*     */     char[] buffer;
/* 209 */     switch (this.valSize) {
/*     */     case 64:
/* 211 */       buffer = this.termAtt.resizeBuffer(11);
/* 212 */       this.termAtt.setLength(NumericUtils.longToPrefixCoded(this.value, this.shift, buffer));
/* 213 */       break;
/*     */     case 32:
/* 216 */       buffer = this.termAtt.resizeBuffer(6);
/* 217 */       this.termAtt.setLength(NumericUtils.intToPrefixCoded((int)this.value, this.shift, buffer));
/* 218 */       break;
/*     */     default:
/* 222 */       throw new IllegalArgumentException("valSize must be 32 or 64");
/*     */     }
/*     */ 
/* 225 */     this.typeAtt.setType(this.shift == 0 ? "fullPrecNumeric" : "lowerPrecNumeric");
/* 226 */     this.posIncrAtt.setPositionIncrement(this.shift == 0 ? 1 : 0);
/* 227 */     this.shift += this.precisionStep;
/* 228 */     return true;
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/* 233 */     StringBuilder sb = new StringBuilder("(numeric,valSize=").append(this.valSize);
/* 234 */     sb.append(",precisionStep=").append(this.precisionStep).append(')');
/* 235 */     return sb.toString();
/*     */   }
/*     */ 
/*     */   public int getPrecisionStep()
/*     */   {
/* 240 */     return this.precisionStep;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.analysis.NumericTokenStream
 * JD-Core Version:    0.6.0
 */