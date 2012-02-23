/*     */ package org.apache.lucene.analysis.standard;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.Reader;
/*     */ import org.apache.lucene.analysis.Tokenizer;
/*     */ import org.apache.lucene.analysis.standard.std31.StandardTokenizerImpl31;
/*     */ import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
/*     */ import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
/*     */ import org.apache.lucene.analysis.tokenattributes.PositionIncrementAttribute;
/*     */ import org.apache.lucene.analysis.tokenattributes.TypeAttribute;
/*     */ import org.apache.lucene.util.AttributeSource;
/*     */ import org.apache.lucene.util.AttributeSource.AttributeFactory;
/*     */ import org.apache.lucene.util.Version;
/*     */ 
/*     */ public final class StandardTokenizer extends Tokenizer
/*     */ {
/*     */   private StandardTokenizerInterface scanner;
/*     */   public static final int ALPHANUM = 0;
/*     */ 
/*     */   @Deprecated
/*     */   public static final int APOSTROPHE = 1;
/*     */ 
/*     */   @Deprecated
/*     */   public static final int ACRONYM = 2;
/*     */ 
/*     */   @Deprecated
/*     */   public static final int COMPANY = 3;
/*     */   public static final int EMAIL = 4;
/*     */ 
/*     */   @Deprecated
/*     */   public static final int HOST = 5;
/*     */   public static final int NUM = 6;
/*     */ 
/*     */   @Deprecated
/*     */   public static final int CJ = 7;
/*     */ 
/*     */   @Deprecated
/*     */   public static final int ACRONYM_DEP = 8;
/*     */   public static final int SOUTHEAST_ASIAN = 9;
/*     */   public static final int IDEOGRAPHIC = 10;
/*     */   public static final int HIRAGANA = 11;
/*     */   public static final int KATAKANA = 12;
/*     */   public static final int HANGUL = 13;
/*  92 */   public static final String[] TOKEN_TYPES = { "<ALPHANUM>", "<APOSTROPHE>", "<ACRONYM>", "<COMPANY>", "<EMAIL>", "<HOST>", "<NUM>", "<CJ>", "<ACRONYM_DEP>", "<SOUTHEAST_ASIAN>", "<IDEOGRAPHIC>", "<HIRAGANA>", "<KATAKANA>", "<HANGUL>" };
/*     */   private boolean replaceInvalidAcronym;
/* 111 */   private int maxTokenLength = 255;
/*     */ 
/* 171 */   private final CharTermAttribute termAtt = (CharTermAttribute)addAttribute(CharTermAttribute.class);
/* 172 */   private final OffsetAttribute offsetAtt = (OffsetAttribute)addAttribute(OffsetAttribute.class);
/* 173 */   private final PositionIncrementAttribute posIncrAtt = (PositionIncrementAttribute)addAttribute(PositionIncrementAttribute.class);
/* 174 */   private final TypeAttribute typeAtt = (TypeAttribute)addAttribute(TypeAttribute.class);
/*     */ 
/*     */   public void setMaxTokenLength(int length)
/*     */   {
/* 116 */     this.maxTokenLength = length;
/*     */   }
/*     */ 
/*     */   public int getMaxTokenLength()
/*     */   {
/* 121 */     return this.maxTokenLength;
/*     */   }
/*     */ 
/*     */   public StandardTokenizer(Version matchVersion, Reader input)
/*     */   {
/* 134 */     init(input, matchVersion);
/*     */   }
/*     */ 
/*     */   public StandardTokenizer(Version matchVersion, AttributeSource source, Reader input)
/*     */   {
/* 141 */     super(source);
/* 142 */     init(input, matchVersion);
/*     */   }
/*     */ 
/*     */   public StandardTokenizer(Version matchVersion, AttributeSource.AttributeFactory factory, Reader input)
/*     */   {
/* 149 */     super(factory);
/* 150 */     init(input, matchVersion);
/*     */   }
/*     */ 
/*     */   private final void init(Reader input, Version matchVersion) {
/* 154 */     if (matchVersion.onOrAfter(Version.LUCENE_34))
/* 155 */       this.scanner = new StandardTokenizerImpl(input);
/* 156 */     else if (matchVersion.onOrAfter(Version.LUCENE_31))
/* 157 */       this.scanner = new StandardTokenizerImpl31(input);
/*     */     else {
/* 159 */       this.scanner = new ClassicTokenizerImpl(input);
/*     */     }
/* 161 */     if (matchVersion.onOrAfter(Version.LUCENE_24))
/* 162 */       this.replaceInvalidAcronym = true;
/*     */     else {
/* 164 */       this.replaceInvalidAcronym = false;
/*     */     }
/* 166 */     this.input = input;
/*     */   }
/*     */ 
/*     */   public final boolean incrementToken()
/*     */     throws IOException
/*     */   {
/* 183 */     clearAttributes();
/* 184 */     int posIncr = 1;
/*     */     while (true)
/*     */     {
/* 187 */       int tokenType = this.scanner.getNextToken();
/*     */ 
/* 189 */       if (tokenType == -1) {
/* 190 */         return false;
/*     */       }
/*     */ 
/* 193 */       if (this.scanner.yylength() <= this.maxTokenLength) {
/* 194 */         this.posIncrAtt.setPositionIncrement(posIncr);
/* 195 */         this.scanner.getText(this.termAtt);
/* 196 */         int start = this.scanner.yychar();
/* 197 */         this.offsetAtt.setOffset(correctOffset(start), correctOffset(start + this.termAtt.length()));
/*     */ 
/* 201 */         if (tokenType == 8) {
/* 202 */           if (this.replaceInvalidAcronym) {
/* 203 */             this.typeAtt.setType(TOKEN_TYPES[5]);
/* 204 */             this.termAtt.setLength(this.termAtt.length() - 1);
/*     */           } else {
/* 206 */             this.typeAtt.setType(TOKEN_TYPES[2]);
/*     */           }
/*     */         }
/* 209 */         else this.typeAtt.setType(TOKEN_TYPES[tokenType]);
/*     */ 
/* 211 */         return true;
/*     */       }
/*     */ 
/* 215 */       posIncr++;
/*     */     }
/*     */   }
/*     */ 
/*     */   public final void end()
/*     */   {
/* 222 */     int finalOffset = correctOffset(this.scanner.yychar() + this.scanner.yylength());
/* 223 */     this.offsetAtt.setOffset(finalOffset, finalOffset);
/*     */   }
/*     */ 
/*     */   public void reset(Reader reader) throws IOException
/*     */   {
/* 228 */     super.reset(reader);
/* 229 */     this.scanner.yyreset(reader);
/*     */   }
/*     */ 
/*     */   @Deprecated
/*     */   public boolean isReplaceInvalidAcronym()
/*     */   {
/* 241 */     return this.replaceInvalidAcronym;
/*     */   }
/*     */ 
/*     */   @Deprecated
/*     */   public void setReplaceInvalidAcronym(boolean replaceInvalidAcronym)
/*     */   {
/* 253 */     this.replaceInvalidAcronym = replaceInvalidAcronym;
/*     */   }
/*     */ }

/* Location:           G:\javawork\JForexClientLibrary\libs\lucene-core-3.4.0.jar
 * Qualified Name:     org.apache.lucene.analysis.standard.StandardTokenizer
 * JD-Core Version:    0.6.0
 */