/*     */ package org.apache.lucene.analysis.standard;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.InputStreamReader;
/*     */ import java.io.Reader;
/*     */ import org.apache.lucene.analysis.Tokenizer;
/*     */ import org.apache.lucene.analysis.standard.std31.UAX29URLEmailTokenizerImpl31;
/*     */ import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
/*     */ import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
/*     */ import org.apache.lucene.analysis.tokenattributes.PositionIncrementAttribute;
/*     */ import org.apache.lucene.analysis.tokenattributes.TypeAttribute;
/*     */ import org.apache.lucene.util.AttributeSource;
/*     */ import org.apache.lucene.util.AttributeSource.AttributeFactory;
/*     */ import org.apache.lucene.util.Version;
/*     */ 
/*     */ public final class UAX29URLEmailTokenizer extends Tokenizer
/*     */ {
/*     */   private final StandardTokenizerInterface scanner;
/*     */   public static final int ALPHANUM = 0;
/*     */   public static final int NUM = 1;
/*     */   public static final int SOUTHEAST_ASIAN = 2;
/*     */   public static final int IDEOGRAPHIC = 3;
/*     */   public static final int HIRAGANA = 4;
/*     */   public static final int KATAKANA = 5;
/*     */   public static final int HANGUL = 6;
/*     */   public static final int URL = 7;
/*     */   public static final int EMAIL = 8;
/*  78 */   public static final String[] TOKEN_TYPES = { StandardTokenizer.TOKEN_TYPES[0], StandardTokenizer.TOKEN_TYPES[6], StandardTokenizer.TOKEN_TYPES[9], StandardTokenizer.TOKEN_TYPES[10], StandardTokenizer.TOKEN_TYPES[11], StandardTokenizer.TOKEN_TYPES[12], StandardTokenizer.TOKEN_TYPES[13], "<URL>", "<EMAIL>" };
/*     */ 
/*     */   @Deprecated
/*  93 */   public static final String WORD_TYPE = TOKEN_TYPES[0];
/*     */ 
/*     */   @Deprecated
/*  98 */   public static final String NUMERIC_TYPE = TOKEN_TYPES[1];
/*     */ 
/*     */   @Deprecated
/* 103 */   public static final String URL_TYPE = TOKEN_TYPES[7];
/*     */ 
/*     */   @Deprecated
/* 108 */   public static final String EMAIL_TYPE = TOKEN_TYPES[8];
/*     */ 
/*     */   @Deprecated
/* 120 */   public static final String SOUTH_EAST_ASIAN_TYPE = TOKEN_TYPES[2];
/*     */ 
/*     */   @Deprecated
/* 124 */   public static final String IDEOGRAPHIC_TYPE = TOKEN_TYPES[3];
/*     */ 
/*     */   @Deprecated
/* 128 */   public static final String HIRAGANA_TYPE = TOKEN_TYPES[4];
/*     */ 
/*     */   @Deprecated
/* 132 */   public static final String KATAKANA_TYPE = TOKEN_TYPES[5];
/*     */ 
/*     */   @Deprecated
/* 136 */   public static final String HANGUL_TYPE = TOKEN_TYPES[6];
/*     */ 
/* 138 */   private int maxTokenLength = 255;
/*     */ 
/* 212 */   private final CharTermAttribute termAtt = (CharTermAttribute)addAttribute(CharTermAttribute.class);
/* 213 */   private final OffsetAttribute offsetAtt = (OffsetAttribute)addAttribute(OffsetAttribute.class);
/* 214 */   private final PositionIncrementAttribute posIncrAtt = (PositionIncrementAttribute)addAttribute(PositionIncrementAttribute.class);
/* 215 */   private final TypeAttribute typeAtt = (TypeAttribute)addAttribute(TypeAttribute.class);
/*     */ 
/*     */   public void setMaxTokenLength(int length)
/*     */   {
/* 143 */     this.maxTokenLength = length;
/*     */   }
/*     */ 
/*     */   public int getMaxTokenLength()
/*     */   {
/* 148 */     return this.maxTokenLength;
/*     */   }
/*     */ 
/*     */   @Deprecated
/*     */   public UAX29URLEmailTokenizer(Reader input) {
/* 154 */     this(Version.LUCENE_31, input);
/*     */   }
/*     */ 
/*     */   @Deprecated
/*     */   public UAX29URLEmailTokenizer(InputStream input) {
/* 160 */     this(Version.LUCENE_31, new InputStreamReader(input));
/*     */   }
/*     */ 
/*     */   @Deprecated
/*     */   public UAX29URLEmailTokenizer(AttributeSource source, Reader input) {
/* 166 */     this(Version.LUCENE_31, source, input);
/*     */   }
/*     */ 
/*     */   @Deprecated
/*     */   public UAX29URLEmailTokenizer(AttributeSource.AttributeFactory factory, Reader input) {
/* 172 */     this(Version.LUCENE_31, factory, input);
/*     */   }
/*     */ 
/*     */   public UAX29URLEmailTokenizer(Version matchVersion, Reader input)
/*     */   {
/* 182 */     super(input);
/* 183 */     this.scanner = getScannerFor(matchVersion, input);
/*     */   }
/*     */ 
/*     */   public UAX29URLEmailTokenizer(Version matchVersion, AttributeSource source, Reader input)
/*     */   {
/* 190 */     super(source, input);
/* 191 */     this.scanner = getScannerFor(matchVersion, input);
/*     */   }
/*     */ 
/*     */   public UAX29URLEmailTokenizer(Version matchVersion, AttributeSource.AttributeFactory factory, Reader input)
/*     */   {
/* 198 */     super(factory, input);
/* 199 */     this.scanner = getScannerFor(matchVersion, input);
/*     */   }
/*     */ 
/*     */   private static StandardTokenizerInterface getScannerFor(Version matchVersion, Reader input) {
/* 203 */     if (matchVersion.onOrAfter(Version.LUCENE_34)) {
/* 204 */       return new UAX29URLEmailTokenizerImpl(input);
/*     */     }
/* 206 */     return new UAX29URLEmailTokenizerImpl31(input);
/*     */   }
/*     */ 
/*     */   public final boolean incrementToken()
/*     */     throws IOException
/*     */   {
/* 219 */     clearAttributes();
/* 220 */     int posIncr = 1;
/*     */     while (true)
/*     */     {
/* 223 */       int tokenType = this.scanner.getNextToken();
/*     */ 
/* 225 */       if (tokenType == -1) {
/* 226 */         return false;
/*     */       }
/*     */ 
/* 229 */       if (this.scanner.yylength() <= this.maxTokenLength) {
/* 230 */         this.posIncrAtt.setPositionIncrement(posIncr);
/* 231 */         this.scanner.getText(this.termAtt);
/* 232 */         int start = this.scanner.yychar();
/* 233 */         this.offsetAtt.setOffset(correctOffset(start), correctOffset(start + this.termAtt.length()));
/* 234 */         this.typeAtt.setType(TOKEN_TYPES[tokenType]);
/* 235 */         return true;
/*     */       }
/*     */ 
/* 239 */       posIncr++;
/*     */     }
/*     */   }
/*     */ 
/*     */   public final void end()
/*     */   {
/* 246 */     int finalOffset = correctOffset(this.scanner.yychar() + this.scanner.yylength());
/* 247 */     this.offsetAtt.setOffset(finalOffset, finalOffset);
/*     */   }
/*     */ 
/*     */   public void reset(Reader reader) throws IOException
/*     */   {
/* 252 */     super.reset(reader);
/* 253 */     this.scanner.yyreset(reader);
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.analysis.standard.UAX29URLEmailTokenizer
 * JD-Core Version:    0.6.0
 */